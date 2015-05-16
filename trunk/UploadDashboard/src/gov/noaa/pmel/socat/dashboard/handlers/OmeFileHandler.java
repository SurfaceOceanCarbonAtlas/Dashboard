/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.handlers;

import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadata;

/**
 * Handler for the transfer of the contents from files saved from the OME server 
 * into the Dashboard's OME XML files.
 * 
 * @author Karl Smith
 */
public class OmeFileHandler {

	private MetadataFileHandler metadataHandler;
	private String socatVersion;
	private Path omeServerOutputPath;
	private WatchService watcher;
	private Thread watcherThread;
	private WatchKey registration;
	private Logger itsLogger;

	/**
	 * Create the handler for transferring OME XML files that appear in the given 
	 * OME server output directory using the given dashboard metadata file handler.
	 * 
	 * @param omeServerOutputDirname
	 * 		OME server output directory to monitor
	 * @param mdataHandler
	 * 		dashboard metadata file handler to use
	 * @param socatUploadVersion
	 * 		socat version for the updated OME metadata files 
	 * @throws IllegalArgumentException
	 * 		if the OME server output directory is invalid (e.g., not a directory) 
	 * 		or cannot be monitored, or if the dashboard metadata file handler is 
	 * 		null.
	 */
	public OmeFileHandler(String omeServerOutputDirname, 
			MetadataFileHandler mdataHandler, String socatUploadVersion) throws IllegalArgumentException {
		metadataHandler = mdataHandler;
		if ( metadataHandler == null )
			throw new IllegalArgumentException("metadata file handler is null");
		socatVersion = socatUploadVersion;

		try {
			File omeServerOutputDir = new File(omeServerOutputDirname);
			if ( ! omeServerOutputDir.isDirectory() )
				throw new IllegalArgumentException("Not a directory: " + omeServerOutputDirname);
			omeServerOutputPath = omeServerOutputDir.toPath();
			watcher = FileSystems.getDefault().newWatchService();
			watcherThread = null;
			// Verify the OME output directory can be registered with the watch service
			registration = omeServerOutputPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			registration.cancel();
			registration = null;
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid OME server output directory: " + ex.getMessage(), ex);
		}

		itsLogger = Logger.getLogger("OmeFileHandler");
	}

	/**
	 * Starts a new Thread monitoring the OME server output directory.
	 * If a Thread is currently monitoring the directory, this call does nothing.
	 */
	public void watchForOmeOutput() {
		// Make sure the watcher is not already running
		if ( watcherThread != null )
			return;
		watcherThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// Register the OME output directory with the watch service
				try {
					registration = omeServerOutputPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
				} catch (Exception ex) {
					throw new RuntimeException("Unexpected error re-registering "
							+ "the OME server output directory with the watch service", ex);
				}
				while ( registration != null ) {
					try {
						WatchKey key = watcher.take();
						for ( WatchEvent<?> event : key.pollEvents() ) {
							Path relPath = (Path) event.context();
							handlerOmeServerFile(omeServerOutputPath.resolve(relPath).toFile());
						}
						if ( ! key.reset() )
							break;
					} catch (Exception ex) {
						break;
					}
				}
				if ( registration != null ) {
					registration.cancel();
					registration = null;
				}
				return;
			}
		});
		itsLogger.info("Starting new thread monitoring the OME server output directory: " + 
						omeServerOutputPath.toString());
		watcherThread.start();
	}

	private void handlerOmeServerFile(File omeFile) {
		itsLogger.info("Working with OME server XML file " + omeFile.getPath());
		Document omeDoc;
		try {
			omeDoc = (new SAXBuilder()).build(omeFile);
		} catch (Exception ex) {
			itsLogger.error("Problems reading the OME XML contents in " + omeFile.getPath(), ex);
			// There may be more updates of the file, so leave it there
			return;
		}

		OmeMetadata omeMData;
		String expocode;
		try {
			omeMData = new OmeMetadata("");
			omeMData.assignFromOmeXmlDoc(omeDoc);
			// Assign all fields associated with the expocode for consistency
			expocode = omeMData.getExpocode();
			omeMData.setExpocode(expocode);
		} catch (Exception ex) {
			itsLogger.error("Problems interpreting the OME XML contents in " + omeFile.getPath(), ex);
			// There may be more updates of the file, so leave it there
			return;
		}

		// TODO: Major disconnect - who making this update and is this person permitted to do this ????
		// TODO: Assign the owner from the OME XML contents 
		String timestamp = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z").print(new DateTime());
		DashboardOmeMetadata dashOmeMData = new DashboardOmeMetadata(omeMData, timestamp, null, socatVersion);
		// If conflicted or incomplete, set the conflicted flags in SocatMetadata
		dashOmeMData.setConflicted( ! omeMData.isAcceptable() );

		// Save this OME XML file for the dashboard
		// At this time do not worry about the lon/lat/time extents; 
		// a SanityCheck will update those values
		String message  = "Update of OME XML for " + expocode + " from OME server output";
		try {
			metadataHandler.saveMetadataInfo(dashOmeMData, message);
			metadataHandler.saveAsOmeXmlDoc(dashOmeMData, message);
		} catch (Exception ex) {
			itsLogger.error("Problems saving the OME XML for " + expocode + 
							" from the contents of " + omeFile.getPath(), ex);
			return;
		}
		itsLogger.info("Successful update of OME XML file for " + expocode + " from " + omeFile.getPath());

		// Remove the OME server file
		try {
			Files.delete(omeFile.toPath());
		} catch (Exception ex) {
			// Don't worry about this failure other than to log it
			itsLogger.error("Problems deleting the OME server file " + omeFile.getPath(), ex);
		}
	}

	/**
	 * Stops the monitoring the OME server output directory.  
	 * If the OME server output directory is not being monitored, this call does nothing. 
	 */
	public void cancelWatch() {
		if ( registration != null ) {
			registration.cancel();
			registration = null;
		}
		if ( watcherThread != null ) {
			try {
				watcherThread.join();
			} catch (Exception ex) {
				;
			}
			watcherThread = null;
			itsLogger.info("End of thread monitoring the OME server output directory: " + 
					omeServerOutputPath.toString());
		}
	}

}
