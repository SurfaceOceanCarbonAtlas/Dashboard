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
public class SocatFilesBundler extends VersionedFileHandler {

	private static final String BUNDLE_NAME_EXTENSION = "_bundle.zip";
	private static final String ENHANCED_REPORT_NAME_EXTENSION = "_SOCAT_enhanced.tsv";

	private static final String EMAIL_MESSAGE_START =
			"Dear CDIAC team, \n" +
			"\n" +
			"The SOCAT dashboard user ";
	private static final String EMAIL_MESSAGE_END = 
			",\n" +
			"when submitting a dataset for QC, has requested \n" +
			"immediate archival of the attached data and metadata. \n" +
			"\n" +
			"Best regards, \n" +
			"SOCAT team \n";

	private String archivalEmailAddress;

	/**
	 * A file bundler that saves the file bundles under the given directory
	 * and sends an email with the bundle to the given email address.
	 * 
	 * @param outputDirname
	 * 		save the file bundles under this directory
	 * @param svnUsername
	 * 		username for SVN authentication; 
	 * 		if null, the directory is not checked for version control 
	 * 		and no version control is performed
	 * @param svnPassword
	 * 		password for SVN authentication
	 * @param emailAddress
	 * 		e-mail address to send bundles for archival
	 * @throws IllegalArgumentException
	 * 		if the specified directory does not exist, is not a directory 
	 * 		or is not under version control
	 */
	public SocatFilesBundler(String outputDirname, String svnUsername, 
			String svnPassword, String emailAddress) throws IllegalArgumentException {
		super(outputDirname, svnUsername, svnPassword);
		archivalEmailAddress = emailAddress;
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
		File parentFile = new File(filesDir, upperExpo.substring(0,4));
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
	 * Use {@link #getBundleFile(String)} to get the virtual File of
	 * the created bundle.
	 * 
	 * @param expocode
	 * 		create the bundle for the cruise with this expocode
	 * @return
	 * 		the warning messages from generating the single-cruise 
	 * 		SOCAT-enhanced data file
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
			// Exclude the (expocode)_OME.xml document at this time;
			// do include the (expocode)_PI_OME.xml 
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
	 * Creates the file bundle of original data and metadata, 
	 * and sends this bundle to the email address, if given, 
	 * associated with this instance for archival.  This bundle 
	 * is also committed to version control using the given message. 
	 * 
	 * @param expocode
	 * 		create the bundle for the cruise with this expocode
	 * @param message
	 * 		version control commit message for the bundle file; 
	 * 		if null or empty, the bundle file is not committed 
	 * 		to version control
	 * @param userRealName
	 * 		real name of the user make this archival request
	 * @param userEmailAddress
	 * 		email address of the user making this archival request;
	 * 		this address will be cc'd on the bundle email sent for archival
	 * @return
	 * 		an message indicating what was sent and to whom
	 * @throws IllegalArgumentException
	 * 		if this SocatFilesBundler does not have a valid archivalEmailAddress,
	 * 		if the userRealName is not given,
	 * 		if the userEmailAddress is not valid, or
	 * 		if the expocode is not valid
	 * @throws IOException
	 * 		if unable to read the default DashboardConfigStore, 
	 * 		if the dataset is has no data or metadata files,
	 * 		if unable to create the bundle file, or
	 * 		if unable to commit the bundle to version control
	 */
	public String sendOrigFilesBundle(String expocode, String message, 
			String userRealName, String userEmailAddress) throws IllegalArgumentException, IOException {
		if ( (archivalEmailAddress == null) || archivalEmailAddress.isEmpty() )
			throw new IllegalArgumentException("no archival email address");
		if ( (userRealName == null) || userRealName.isEmpty() ) 
			throw new IllegalArgumentException("no user name");
		if ( (userEmailAddress == null) || userEmailAddress.isEmpty() )
			throw new IllegalArgumentException("no user email address");
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		DashboardConfigStore configStore = DashboardConfigStore.get();

		// Get the original data file for this dataset
		File origDataFile = configStore.getCruiseFileHandler().cruiseDataFile(upperExpo);
		if ( ! origDataFile.exists() )
			throw new IOException("No original data file for " + upperExpo);

		// Get the list of metadata documents to be bundled with this data file
		ArrayList<File> addlDocs = new ArrayList<File>();
		MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
		for ( DashboardMetadata mdata : metadataHandler.getMetadataFiles(upperExpo) ) {
			// Exclude the (expocode)_OME.xml document at this time;
			// do include the (expocode)_PI_OME.xml 
			String filename = mdata.getFilename();
			if ( ! filename.equals(DashboardMetadata.OME_FILENAME) ) {
				addlDocs.add(metadataHandler.getMetadataFile(upperExpo, filename));
			}
		}
		if ( addlDocs.isEmpty() )
			throw new IOException("No metadata/supplemental documents for " + upperExpo);

		// Generate the bundle as a zip file
		File bundleFile = getBundleFile(upperExpo);
		String infoMsg = "Created files bundle " + bundleFile.getName() + " containing files:\n";
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(bundleFile));
		try {
			copyFileToBundle(zipOut, origDataFile);
			infoMsg += "    " + origDataFile.getName() + "\n";
			for ( File metaFile : addlDocs ) {
				copyFileToBundle(zipOut, metaFile);
				infoMsg += "    " + metaFile.getName() + "\n";
			}
		} finally {
			zipOut.close();
		}

		// Commit the bundle to version control
		if ( (message != null) && ! message.isEmpty() ) {
			try {
				commitVersion(bundleFile, message);				
			} catch (Exception ex) {
				throw new IOException(
						"Problems committing the CDIAC file bundle for " + 
								upperExpo + ": " + ex.getMessage());
			}
		}

		String emailMessage = EMAIL_MESSAGE_START + userRealName + EMAIL_MESSAGE_END;
		/*
	String to = args[0];
	String from = args[1];
	String host = args[2];
	String filename = args[3];
	boolean debug = Boolean.valueOf(args[4]).booleanValue();
	String msgText1 = "Sending a file.\n";
	String subject = "Sending a file";
	
	// create some properties and get the default Session
	Properties props = System.getProperties();
	props.put("mail.smtp.host", host);
	
	Session session = Session.getInstance(props, null);
	session.setDebug(debug);
	
	try {
	    // create a message
	    MimeMessage msg = new MimeMessage(session);
	    msg.setFrom(new InternetAddress(from));
	    InternetAddress[] address = {new InternetAddress(to)};
	    msg.setRecipients(Message.RecipientType.TO, address);
	    msg.setSubject(subject);

	    // create and fill the first message part
	    MimeBodyPart mbp1 = new MimeBodyPart();
	    mbp1.setText(msgText1);

	    // create the second message part
	    MimeBodyPart mbp2 = new MimeBodyPart();

	    // attach the file to the message
	    mbp2.attachFile(filename);

	     *
	     * Use the following approach instead of the above line if
	     * you want to control the MIME type of the attached file.
	     * Normally you should never need to do this.
	     *
	    FileDataSource fds = new FileDataSource(filename) {
		public String getContentType() {
		    return "application/octet-stream";
		}
	    };
	    mbp2.setDataHandler(new DataHandler(fds));
	    mbp2.setFileName(fds.getName());
	     *

	    // create the Multipart and add its parts to it
	    Multipart mp = new MimeMultipart();
	    mp.addBodyPart(mbp1);
	    mp.addBodyPart(mbp2);

	    // add the Multipart to the message
	    msg.setContent(mp);

	    // set the Date: header
	    msg.setSentDate(new Date());

	     *
	     * If you want to control the Content-Transfer-Encoding
	     * of the attached file, do the following.  Normally you
	     * should never need to do this.
	     *
	    msg.saveChanges();
	    mbp2.setHeader("Content-Transfer-Encoding", "base64");
	     *

	    // send the message
	    Transport.send(msg);
	    
	} catch (MessagingException mex) {
	    mex.printStackTrace();
	    Exception ex = null;
	    if ((ex = mex.getNextException()) != null) {
		ex.printStackTrace();
	    }
	} catch (IOException ioex) {
	    ioex.printStackTrace();
	}

		 */

		infoMsg += "Files bundle sent to " + archivalEmailAddress + " and cc'd to " + userEmailAddress + "\n";
		return infoMsg;
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
