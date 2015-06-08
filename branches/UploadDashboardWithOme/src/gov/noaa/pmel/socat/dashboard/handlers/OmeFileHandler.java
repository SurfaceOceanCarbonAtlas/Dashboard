/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.handlers;

import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;

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
	private CruiseFileHandler dataHandler;
	private String socatVersion;
	private Path omeServerOutputPath;
	private WatchService watcher;
	private Thread watcherThread;
	private Logger itsLogger;

	/**
	 * Create the handler for transferring OME XML files that appear in the given 
	 * OME server output directory using the given dashboard metadata file handler.
	 * 
	 * @param omeServerOutputDirname
	 * 		OME server output directory to monitor
	 * @param mdataHandler
	 * 		dashboard metadata file handler to use
	 * @param cruiseDataHandler
	 * 		daskboard cruise data file handler to use
	 * @param socatUploadVersion
	 * 		socat version for the updated OME metadata files 
	 * @throws IllegalArgumentException
	 * 		if the OME server output directory is invalid (e.g., not a directory) 
	 * 		or cannot be monitored, or if the dashboard metadata file handler is 
	 * 		null.
	 */
	public OmeFileHandler(String omeServerOutputDirname, MetadataFileHandler mdataHandler, 
			CruiseFileHandler cruiseDataHandler, String socatUploadVersion) throws IllegalArgumentException {
		metadataHandler = mdataHandler;
		if ( metadataHandler == null )
			throw new IllegalArgumentException("metadata file handler is null");
		dataHandler = cruiseDataHandler;
		if ( dataHandler == null )
			throw new IllegalArgumentException("cruise data file handler is null");
		socatVersion = socatUploadVersion;

		try {
			File omeServerOutputDir = new File(omeServerOutputDirname);
			if ( ! omeServerOutputDir.isDirectory() )
				throw new IllegalArgumentException("Not a directory: " + omeServerOutputDirname);
			omeServerOutputPath = omeServerOutputDir.toPath();
			// Verify the OME output directory can be registered with the watch service
			watcher = FileSystems.getDefault().newWatchService();
			omeServerOutputPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY).cancel();
			watcher.close();
			watcher = null;
			watcherThread = null;
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
				// Create a new watch service for the OME server output directory
				try {
					watcher = FileSystems.getDefault().newWatchService();
				} catch (Exception ex) {
					itsLogger.error("Unexpected error starting a watcher for the default file system", ex);
					return;
				}
				// Register the OME server output directory with the watch service
				WatchKey registration;
				try {
					registration = omeServerOutputPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
				} catch (Exception ex) {
					itsLogger.error("Unexpected error registering the OME server output directory for watching", ex);
					try {
						watcher.close();
					} catch (Exception e) {
						;
					}
					watcher = null;
					return;
				}
				for (;;) {
					try {
						WatchKey key = watcher.take();
						for ( WatchEvent<?> event : key.pollEvents() ) {
							Path relPath = (Path) event.context();
							handleOmeServerFile(omeServerOutputPath.resolve(relPath).toFile());
						}
						if ( ! key.reset() )
							break;
					} catch (Exception ex) {
						// Probably the watcher was closed
						break;
					}
				}
				registration.cancel();
				registration.pollEvents();
				try {
					watcher.close();
				} catch (Exception ex) {
					;
				}
				watcher = null;
				return;
			}
		});
		itsLogger.info("Starting new thread monitoring the OME server output directory: " + 
						omeServerOutputPath.toString());
		watcherThread.start();
	}

	/**
	 * Reads the given OME XML file in the OME server output directory and
	 * updates the appropriate dashboard OME XML file from its contents.  
	 * The OME server output file is deleted once the dashboard OME XML is updated. 
	 * 
	 * @param omeFile
	 * 		OME server XML output file to 
	 */
	private void handleOmeServerFile(File omeFile) {
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

		// Update the cruise info with this update
		try {
			DashboardCruise cruise = dataHandler.getCruiseFromInfoFile(expocode);
			cruise.setOmeTimestamp(timestamp);
			dataHandler.saveCruiseInfoToFile(cruise, message);
		} catch (Exception ex) {
			itsLogger.error("Problems updating the cruise info for " + expocode + " for updated OME server output");
		}

	}


	/**
	 * Stops the monitoring the OME server output directory.  
	 * If the OME server output directory is not being monitored, this call does nothing. 
	 */
	public void cancelWatch() {
		try {
			watcher.close();
			// Only the thread modifies the value of watcher
		} catch (Exception ex) {
			// Might be NullPointerException
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
