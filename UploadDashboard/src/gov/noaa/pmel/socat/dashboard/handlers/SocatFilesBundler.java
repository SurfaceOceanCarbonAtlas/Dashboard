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
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Bundles files, either original or SOCAT-enhanced documents, for sending out to be archived.
 * 
 * @author Karl Smith
 */
public class SocatFilesBundler extends VersionedFileHandler {

	private static final String BUNDLE_NAME_EXTENSION = "_bundle.zip";
	private static final String ENHANCED_REPORT_NAME_EXTENSION = "_SOCAT_enhanced.tsv";

	private static final String EMAIL_SUBJECT_MSG = 
			"Request for immediate archival from SOCAT dashboard user ";
	private static final String EMAIL_MSG_START =
			"Dear CDIAC team, \n" +
			"\n" +
			"The SOCAT dashboard user ";
	private static final String EMAIL_MSG_END = 
			",\n" +
			"as part of submitting a dataset to SOCAT for QC, \n" +
			"has requested immediate archival of the attached data and metadata. \n" +
			"\n" +
			"Best regards, \n" +
			"SOCAT team \n";

	private String archivalEmail;
	private String socatEmail;
	private String smtpHost;

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
	 * @param archivalEmailAddress
	 * 		e-mail address to send bundles for archival
	 * @param socatEmailAddress
	 * 		e-mail address from which these bundles are being sent
	 * @param smtpHostAddress
	 * 		address of the SMTP host to use for email
	 * @throws IllegalArgumentException
	 * 		if the specified directory does not exist, is not a directory 
	 * 		or is not under version control
	 */
	public SocatFilesBundler(String outputDirname, String svnUsername, String svnPassword, 
			String archivalEmailAddress, String socatEmailAddress, String smtpHostAddress) 
					throws IllegalArgumentException {
		super(outputDirname, svnUsername, svnPassword);
		archivalEmail = archivalEmailAddress;
		socatEmail = socatEmailAddress;
		smtpHost = smtpHostAddress;
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
	 * 		if the expocode is not valid, or
	 * 		if there is a problem sending the archival request email
	 * @throws IOException
	 * 		if unable to read the default DashboardConfigStore, 
	 * 		if the dataset is has no data or metadata files,
	 * 		if unable to create the bundle file, or
	 * 		if unable to commit the bundle to version control
	 */
	public String sendOrigFilesBundle(String expocode, String message, String userRealName, 
			String userEmailAddress) throws IllegalArgumentException, IOException {
		if ( (archivalEmail == null) || archivalEmail.isEmpty() )
			throw new IllegalArgumentException("no archival email address");
		if ( (smtpHost == null) || smtpHost.isEmpty() )
			throw new IllegalArgumentException("no SMTP host");
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
				throw new IOException("Problems committing the CDIAC file bundle for " + 
						upperExpo + ": " + ex.getMessage());
			}
		}

		// Get the default Session for e-mailing
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpHost);
		Session session = Session.getDefaultInstance(props);
		// Create the email message
		MimeMessage msg = new MimeMessage(session);
		try {
			msg.setSubject(EMAIL_SUBJECT_MSG + userRealName);
		} catch (MessagingException ex) {
			throw new IllegalArgumentException(
					"Unexpected problems assigning the email subject: " + ex.getMessage(), ex);
		}
		try {
			msg.setFrom(new InternetAddress(socatEmail));
		} catch (MessagingException ex) {
			throw new IllegalArgumentException(
					"Invalid SOCAT email address: " + ex.getMessage(), ex);
		}
		try {
			InternetAddress[] toAddress = { new InternetAddress(archivalEmail)};
			msg.setRecipients(Message.RecipientType.TO, toAddress);
		} catch (MessagingException ex) {
			throw new IllegalArgumentException(
					"Invalid CDIAC email address: " + ex.getMessage(), ex);
		}
		try {
			InternetAddress[] ccAddress = { new InternetAddress(userEmailAddress) };
			msg.setRecipients(Message.RecipientType.CC, ccAddress);
		} catch (MessagingException ex) {
			throw new IllegalArgumentException(
					"Invalid user email address: " + ex.getMessage(), ex);
		}
		try {
			// Create the text message part
			MimeBodyPart textMsgPart = new MimeBodyPart();
			textMsgPart.setText(EMAIL_MSG_START + userRealName + EMAIL_MSG_END);
			// Create the attachment message part
			MimeBodyPart attMsgPart = new MimeBodyPart();
			attMsgPart.attachFile(bundleFile);
			// Create and add the multipart document to the message
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(textMsgPart);
			mp.addBodyPart(attMsgPart);
			msg.setContent(mp);
		} catch (MessagingException ex) {
			throw new IllegalArgumentException(
					"Unexpected problems assigning the multipart document: " + ex.getMessage(), ex);
		}
		try {
			msg.setSentDate(new Date());
		} catch (MessagingException ex) {
			throw new IllegalArgumentException(
					"Unexpected problems assigning the email date: " + ex.getMessage(), ex);
		}
		try {
			Transport.send(msg);
		} catch (MessagingException ex) {
			throw new IllegalArgumentException(
					"Problems sending the archival request email: " + ex.getMessage(), ex);
		}

		infoMsg += "Files bundle sent to " + archivalEmail + " and cc'd to " + userEmailAddress + "\n";
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
