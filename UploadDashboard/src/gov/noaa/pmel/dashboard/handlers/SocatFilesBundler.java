/**
 * 
 */
package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.actions.SocatCruiseReporter;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
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
	private static final String MAILED_BUNDLE_NAME_ADDENDUM = "_from_SOCAT";
	private static final String ENHANCED_REPORT_NAME_EXTENSION = "_SOCAT_enhanced.tsv";

	private static final String EMAIL_SUBJECT_MSG = 
			"Request for immediate archival from SOCAT dashboard user ";
	private static final String EMAIL_MSG_START =
			"Dear CDIAC team, \n" +
			"\n" +
			"As part of submitting a dataset to SOCAT for QC, the SOCAT Upload Dashboard user \n";
	private static final String EMAIL_MSG_END = 
			" has requested immediate archival of the attached data and metadata. \n" +
			"The attached file is a ZIP file of the data and metadata, but \"" + MAILED_BUNDLE_NAME_ADDENDUM + "\" \n" +
			"has been appended to the name for sending as an email attachment. \n" +
			"\n" +
			"Best regards, \n" +
			"SOCAT team \n";

	private String archivalEmail;
	private String socatEmail;
	private String smtpHost;
	private String smtpPort;
	private PasswordAuthentication auth;
	private boolean debugIt;

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
	 * 		address of the SMTP host to use for email; if null or empty, "localhost" is used
	 * @param smtpHostPort
	 * 		port number of the SMTP host to use for email; if null or empty, the appropriate default port is used
	 * @param smtpUsername
	 * 		username for SMTPS authentication; if null or empty, SMTP is used without authentication
	 * @param smtpPassword
	 * 		password for SMTPS authentication; if null or empty, SMTP is used without authentication
	 * @param setDebug
	 * 		debug the SMTP connection?
	 * @throws IllegalArgumentException
	 * 		if the outputDirname directory does not exist, 
	 * 		is not a directory, or is not under version control
	 */
	public SocatFilesBundler(String outputDirname, String svnUsername, String svnPassword, 
			String archivalEmailAddress, String socatEmailAddress, String smtpHostAddress,
			String smtpHostPort, String smtpUsername, String smtpPassword, boolean setDebug) 
					throws IllegalArgumentException {
		super(outputDirname, svnUsername, svnPassword);
		archivalEmail = archivalEmailAddress;
		socatEmail = socatEmailAddress;
		smtpHost = smtpHostAddress;
		smtpPort = smtpHostPort;
		if ( (smtpUsername == null) || smtpUsername.isEmpty() || 
			 (smtpPassword == null) || smtpPassword.isEmpty() ) {
			auth = null;
		}
		else {
			auth = new PasswordAuthentication(smtpUsername, smtpPassword);
		}
		debugIt = setDebug;
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
		DashboardConfigStore configStore = DashboardConfigStore.get(false);

		// Generate the single-cruise SOCAT-enhanced data file
		SocatCruiseReporter reporter = new SocatCruiseReporter(configStore);
		File reportFile = new File(bundleFile.getParent(), expocode + ENHANCED_REPORT_NAME_EXTENSION);
		ArrayList<String> warnings = reporter.generateReport(expocode, reportFile);

		// Get the list of metadata documents to be bundled with this data file
		ArrayList<File> addlDocs = new ArrayList<File>();
		MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
		for ( DashboardMetadata mdata : metadataHandler.getMetadataFiles(expocode) ) {
			// Exclude the (expocode)/OME.xml document at this time;
			// do include the (expocode)/PI_OME.xml 
			String filename = mdata.getFilename();
			if ( ! filename.equals(DashboardUtils.OME_FILENAME) ) {
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
	 * @param userEmail
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
			String userEmail) throws IllegalArgumentException, IOException {
		if ( (archivalEmail == null) || archivalEmail.isEmpty() )
			throw new IllegalArgumentException("no archival email address");
		if ( (userRealName == null) || userRealName.isEmpty() ) 
			throw new IllegalArgumentException("no user name");
		if ( (userEmail == null) || userEmail.isEmpty() )
			throw new IllegalArgumentException("no user email address");
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		DashboardConfigStore configStore = DashboardConfigStore.get(false);

		// Get the original data file for this dataset
		File origDataFile = configStore.getCruiseFileHandler().cruiseDataFile(upperExpo);
		if ( ! origDataFile.exists() )
			throw new IOException("No original data file for " + upperExpo);

		// Get the list of metadata documents to be bundled with this data file
		ArrayList<File> addlDocs = new ArrayList<File>();
		MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
		for ( DashboardMetadata mdata : metadataHandler.getMetadataFiles(upperExpo) ) {
			// Exclude the (expocode)/OME.xml document at this time;
			// do include the (expocode)/PI_OME.xml 
			String filename = mdata.getFilename();
			if ( ! filename.equals(DashboardUtils.OME_FILENAME) ) {
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
				throw new IOException("Problems committing the archival file bundle for " + 
						upperExpo + ": " + ex.getMessage());
			}
		}

		// If userRealName is "nobody" and userEmail is "nobody@nowhere" then skip the email
		if ( DashboardServerUtils.NOMAIL_USER_REAL_NAME.equals(userRealName) && DashboardServerUtils.NOMAIL_USER_EMAIL.equals(userEmail) ) {
			return "Original data files bundle archived but not emailed";
		}

		// Create a Session for sending out the email
		Properties props = System.getProperties();
		if ( debugIt )
			props.setProperty("mail.debug", "true");
		props.setProperty("mail.transport.protocol", "smtp");
		if ( (smtpHost != null) && ! smtpHost.isEmpty() )
			props.put("mail.smtp.host", smtpHost);
		else
			props.put("mail.smtp.host", "localhost");
		if ( (smtpPort != null) && ! smtpPort.isEmpty() )
			props.put("mail.smtp.port", smtpPort);
		Session sessn;
		if ( auth != null ) {
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");
			sessn = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return auth;
				}
			});
		}
		else {
			sessn = Session.getInstance(props, null);
		}

		// Parse all the email addresses
		InternetAddress socatAddress;
		try {
			socatAddress = new InternetAddress(socatEmail);
		} catch (MessagingException ex) {
			String errmsg = getMessageExceptionMsgs(ex);
			throw new IllegalArgumentException("Invalid SOCAT email address: " + errmsg, ex);
		}
		InternetAddress archivalAddress;
		try {
			archivalAddress = new InternetAddress(archivalEmail);
		} catch (MessagingException ex) {
			String errmsg = getMessageExceptionMsgs(ex);
			throw new IllegalArgumentException("Invalid archival email address: " + errmsg, ex);
		}
		InternetAddress userAddress;
		try {
			userAddress = new InternetAddress(userEmail);
		} catch (MessagingException ex) {
			String errmsg = getMessageExceptionMsgs(ex);
			throw new IllegalArgumentException("Invalid user email address: " + errmsg, ex);
		}

		// Create the email message with the renamed zip attachment
		MimeMessage msg = new MimeMessage(sessn);
		try {
			msg.setHeader("X-Mailer", "SocatFilesBundler");
			msg.setSubject(EMAIL_SUBJECT_MSG + userRealName);
			msg.setSentDate(new Date());
			// Set the addresses
			msg.setFrom(socatAddress);
			msg.setReplyTo(new InternetAddress[] { socatAddress });
			msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { archivalAddress });
			msg.setRecipients(Message.RecipientType.CC, new InternetAddress[] { userAddress, socatAddress });
			// Create the text message part
			MimeBodyPart textMsgPart = new MimeBodyPart();
			textMsgPart.setText(EMAIL_MSG_START + userRealName + EMAIL_MSG_END);
			// Create the attachment message part
			MimeBodyPart attMsgPart = new MimeBodyPart();
			attMsgPart.attachFile(bundleFile);
			attMsgPart.setFileName(bundleFile.getName() + MAILED_BUNDLE_NAME_ADDENDUM);
			// Create and add the multipart document to the message
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(textMsgPart);
			mp.addBodyPart(attMsgPart);
			msg.setContent(mp);
			// Update the headers
			msg.saveChanges();
		} catch (MessagingException ex) {
			String errmsg = getMessageExceptionMsgs(ex);
			throw new IllegalArgumentException("Unexpected problems creating the email: " + errmsg, ex);
		}

		// Send the email
		try {
			Transport.send(msg);
		} catch (MessagingException ex) {
			String errmsg = getMessageExceptionMsgs(ex);
			throw new IllegalArgumentException("Problems sending the archival request email: " + errmsg, ex);
		}

		infoMsg += "Original files bundle sent to " + archivalEmail +
				   " and cc'd to " + userEmail + " and " + socatEmail + "\n";
		return infoMsg;
	}

	/**
	 * Returns all messages in a possibly-nested MessagingException. 
	 * The messages are returned as a single String by joining 
	 * all the Exception messages together using a comma and space.
	 * 
	 * @param ex
	 * 		get the error messages from this MessagingException
	 * @return
	 * 		all error messages concatenated together using a comma and a space;
	 * 		if no messages are present, an empty String is returned
	 */
	private String getMessageExceptionMsgs(MessagingException ex) {
		String fullErrMsg = null;
		Exception nextEx = ex;
		while ( nextEx != null ) {
			String errMsg = nextEx.getMessage();
			if ( errMsg != null ) {
				if ( fullErrMsg == null ) {
					fullErrMsg = errMsg;
				}
				else {
					fullErrMsg += ", " + errMsg;
				}
			}
			if ( nextEx instanceof MessagingException ) {
				nextEx = ((MessagingException) nextEx).getNextException();
			}
			else {
				nextEx = null;
			}
		}
		if ( fullErrMsg == null )
			fullErrMsg = "";
		return fullErrMsg;
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
