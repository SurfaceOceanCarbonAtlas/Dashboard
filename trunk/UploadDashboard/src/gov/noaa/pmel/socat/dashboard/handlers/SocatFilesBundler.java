/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.handlers;

import gov.noaa.pmel.socat.dashboard.actions.SocatCruiseReporter;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Bundles files, either original or SOCAT-enhanced documents, for sending out to be archived.
 * 
 * @author Karl Smith
 */
public class SocatFilesBundler {

	private static final String BUNDLE_NAME_EXTENSION = "_bundle.zip";
	private static final String ENHANCED_REPORT_NAME_EXTENSION = "_SOCAT_enhanced.tsv";

	private File outputDir;
	
	/**
	 * A file bundler that saves the file bundles under the given directory
	 * and sends an email with the bundle to the given email address.
	 * 
	 * @param outputDirname
	 * 		save the file bundles under this directory
	 * @throws IllegalArgumentException
	 * 		if the specified directory does not exist or is not a directory 
	 */
	public SocatFilesBundler(String outputDirname) throws IllegalArgumentException {
		outputDir = new File(outputDirname);
		if ( ! outputDir.isDirectory() )
			throw new IllegalArgumentException("Not a directory: " + outputDirname);
	}

	/**
	 * The bundle virtual File for the given dataset.
	 * Creates the parent subdirectory, if it does not already exist, for this File.
	 * 
	 * @param expocode
	 * 		return the virtual File for the dataset with this expocode
	 * @return
	 * 		the bundle virtual File
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid, or
	 * 		if unable to generate the parent subdirectory if it does not already exist
	 */
	public File getBundleFile(String expocode) throws IllegalArgumentException {
		// Check and standardize the expocode
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		// Create 
		File parentFile = new File(outputDir, upperExpo.substring(0,4));
		if ( ! parentFile.isDirectory() ) {
			if ( parentFile.exists() )
				throw new IllegalArgumentException(
						"File exists but is not a directory: " + parentFile.getPath());
			if ( ! parentFile.mkdir() )
				throw new IllegalArgumentException(
						"Problems creating the directory: " + parentFile.getPath());
		}
		// Generate the full path filename for this cruise metadata
		File bundleFile = new File(parentFile, upperExpo + BUNDLE_NAME_EXTENSION);
		return bundleFile;
	}

	/**
	 * Generates a single-cruise SOCAT-enhanced data file, then bundles 
	 * that report with all the metadata documents for that cruise.
	 * Use {@link #getBundleFile(String)} to get the virtural File of
	 * the created bundle.
	 * 
	 * @param expocode
	 * 		create the bundles for the cruise with this expocode
	 * @return
	 * 		the warning messages from generating the single-cruise SOCAT-enhanced data file
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 * @throws IOException
	 * 		if unable to read the default DashboardConfigStore,
	 * 		if unable to create the SOCAT-enhanced data file, or
	 * 		if unable to create the bundle file
	 */
	public ArrayList<String> createSocatEnhancedFilesBundle(String expocode) 
			throws IllegalArgumentException, IOException {
		File bundleFile = getBundleFile(expocode);
		DashboardConfigStore configStore = DashboardConfigStore.get();

		// Generate the single-cruise SOCAT-enhanced data file
		SocatCruiseReporter reporter = new SocatCruiseReporter(configStore);
		File reportFile = new File(bundleFile.getParent(), expocode + ENHANCED_REPORT_NAME_EXTENSION);
		ArrayList<String> warnings = reporter.generateReport(expocode, reportFile);

		// Get the list of metadata documents to be bundled with this data file
		ArrayList<File> addlDocs = new ArrayList<File>();
		MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
		for ( DashboardMetadata mdata : metadataHandler.getMetadataFiles(expocode) ) {
			// Exclude the OME XML document at this time
			String filename = mdata.getFilename();
			if ( ! filename.equals(DashboardMetadata.OME_FILENAME) ) {
				addlDocs.add(metadataHandler.getMetadataFile(expocode, filename));
			}
		}

		// Generate the bundle as a zip file
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(bundleFile));
		try {
			copyFileToBundle(zipOut, reportFile);
			for ( File metaFile : addlDocs )
				copyFileToBundle(zipOut, metaFile);
		} finally {
			zipOut.close();
		}

		return warnings;
	}

	/**
	 * Copies the contents of the given data file to the bundle file.
	 * 
	 * @param zipOut
	 * 		copy the contents of the given file to here
	 * @param dataFile
	 * 		copy the contents of this file
	 * @throws IOException
	 * 		if reading from the data files throws one, or
	 * 		if writing to the bundle file throws one
	 */
	private void copyFileToBundle(ZipOutputStream zipOut, File dataFile) throws IOException {
		// Create the entry in the zip file
		ZipEntry entry = new ZipEntry(dataFile.getName());
		entry.setTime(dataFile.lastModified());
		zipOut.putNextEntry(entry);

		// Copy the contents of the data file to the zip file
		FileInputStream dataIn = new FileInputStream(dataFile);
		try {
			byte[] data = new byte[4096];
			int numRead;
			while ( true ) {
				numRead = dataIn.read(data);
				if ( numRead < 0 )
					break;
				zipOut.write(data, 0, numRead);
			}
		} finally {
			dataIn.close();
		}

		// End this entry
		zipOut.closeEntry();
	}

}
