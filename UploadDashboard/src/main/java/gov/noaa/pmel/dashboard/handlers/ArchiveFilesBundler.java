/**
 *
 */
package gov.noaa.pmel.dashboard.handlers;

import gov.loc.repository.bagit.creator.BagCreator;
import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.hash.StandardSupportedAlgorithms;
import gov.loc.repository.bagit.verify.BagVerifier;
import gov.noaa.pmel.dashboard.actions.SocatCruiseReporter;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.translate.CdiacReader;
import gov.noaa.pmel.sdimetadata.translate.OcadsWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Bundles files for sending out to be archived.
 *
 * @author Karl Smith
 */
public class ArchiveFilesBundler extends VersionedFileHandler {

    private static final String ENHANCED_REPORT_NAME_EXTENSION = "_SOCAT_enhanced.tsv";
    private static final String OCADS_OME_XML_FILENAME = "PI_OME_to_OCADS.xml";

    private static final String EMAIL_SUBJECT_MSG_START = "Request for OCADS archival of dataset ";
    private static final String EMAIL_SUBJECT_MSG_MIDDLE = " from SOCAT dashboard user ";
    private static final String EMAIL_MSG_START =
            "Dear OCADS Archival Team, \n" +
                    "\n" +
                    "As part of submitting dataset ";
    private static final String EMAIL_MSG_MIDDLE =
            " to SOCAT for QC, \n" +
                    "the SOCAT Upload Dashboard user ";
    private static final String EMAIL_MSG_END =
            " \nhas requested immediate OCADS archival of the attached BagIt ZIP file of data and metadata. \n" +
                    "\n" +
                    "The metadata file " + OCADS_OME_XML_FILENAME + ", if present, is an *experimental* translation \n" +
                    "of the PI-provided metadata in " + DashboardUtils.PI_OME_FILENAME + ". \n" +
                    "The " + OCADS_OME_XML_FILENAME + " file was machine-generated and added only to assist \n" +
                    "in the archival, but should *not* be archived. \n" +
                    "\n" +
                    "Best regards, \n" +
                    "SOCAT Team \n";

    private String[] toEmails;
    private String[] ccEmails;
    private String smtpHost;
    private String smtpPort;
    private PasswordAuthentication auth;
    private boolean debugIt;
    private Pattern nameCleaner;

    public enum BundleType {
        ORIG_FILE_BAGIT_ZIP,
        ENHANCED_FILE_PLAIN_ZIP
    }

    private static final HashMap<String,CdiacReader.VarType> DASH_TYPE_TO_CDIAC_TYPE;

    static {
        DASH_TYPE_TO_CDIAC_TYPE = new HashMap<String,CdiacReader.VarType>();
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_WATER_TEQU_WET.getVarName(), CdiacReader.VarType.FCO2_WATER_EQU);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_WATER_SST_WET.getVarName(), CdiacReader.VarType.FCO2_WATER_SST);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_WATER_TEQU_WET.getVarName(), CdiacReader.VarType.PCO2_WATER_EQU);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_WATER_SST_WET.getVarName(), CdiacReader.VarType.PCO2_WATER_SST);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_WATER_TEQU_DRY.getVarName(), CdiacReader.VarType.XCO2_WATER_EQU);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_WATER_SST_DRY.getVarName(), CdiacReader.VarType.XCO2_WATER_SST);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_ATM_DRY_ACTUAL.getVarName(), CdiacReader.VarType.FCO2_ATM_ACTUAL);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_ATM_DRY_INTERP.getVarName(), CdiacReader.VarType.FCO2_ATM_INTERP);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_ATM_DRY_ACTUAL.getVarName(), CdiacReader.VarType.PCO2_ATM_ACTUAL);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_ATM_DRY_INTERP.getVarName(), CdiacReader.VarType.PCO2_ATM_INTERP);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_ATM_DRY_ACTUAL.getVarName(), CdiacReader.VarType.XCO2_ATM_ACTUAL);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_ATM_DRY_INTERP.getVarName(), CdiacReader.VarType.XCO2_ATM_INTERP);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.SST.getVarName(), CdiacReader.VarType.SEA_SURFACE_TEMPERATURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.TEQU.getVarName(), CdiacReader.VarType.EQUILIBRATOR_TEMPERATURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PATM.getVarName(), CdiacReader.VarType.SEA_LEVEL_PRESSURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PEQU.getVarName(), CdiacReader.VarType.EQUILIBRATOR_PRESSURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.SALINITY.getVarName(), CdiacReader.VarType.SALINITY);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.WOCE_CO2_WATER.getVarName(), CdiacReader.VarType.WOCE_CO2_WATER);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.WOCE_CO2_ATM.getVarName(), CdiacReader.VarType.WOCE_CO2_ATM);
    }

    /**
     * A file bundler that saves the file bundles under the given directory and sends an email with the bundle
     * to the given email addresses.
     *
     * @param outputDirname
     *         save the file bundles under this directory
     * @param svnUsername
     *         username for SVN authentication;
     *         if null, the directory is not checked for version control and no version control is performed
     * @param svnPassword
     *         password for SVN authentication
     * @param toEmailAddresses
     *         e-mail addresses to send bundles to for archival
     * @param ccEmailAddresses
     *         e-mail addresses to be cc'd on the archival request
     * @param smtpHostAddress
     *         address of the SMTP host to use for email; if null or empty, "localhost" is used
     * @param smtpHostPort
     *         port number of the SMTP host to use for email; if null or empty, the appropriate default port is used
     * @param smtpUsername
     *         username for SMTPS authentication; if null or empty, SMTP is used without authentication
     * @param smtpPassword
     *         password for SMTPS authentication; if null or empty, SMTP is used without authentication
     * @param setDebug
     *         debug the SMTP connection?
     *
     * @throws IllegalArgumentException
     *         if the outputDirname directory does not exist, is not a directory, or is not under version control
     */
    public ArchiveFilesBundler(String outputDirname, String svnUsername, String svnPassword,
            String[] toEmailAddresses, String[] ccEmailAddresses, String smtpHostAddress,
            String smtpHostPort, String smtpUsername, String smtpPassword, boolean setDebug)
            throws IllegalArgumentException {
        super(outputDirname, svnUsername, svnPassword);
        if ( toEmailAddresses != null )
            toEmails = toEmailAddresses.clone();
        else
            toEmails = null;
        if ( ccEmailAddresses != null )
            ccEmails = ccEmailAddresses.clone();
        else
            ccEmails = null;
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
        nameCleaner = Pattern.compile("[^A-Za-z0-9]+");
    }

    /**
     * The zip bundle virtual File for the given dataset of the given type.
     * Creates the parent subdirectory, if it does not already exist, for this File.
     *
     * @param datasetId
     *         return the zip virtual File for the dataset with this ID
     * @param bundleType
     *         type of zip bundle
     *
     * @return the zip bundle virtual File for the dataset
     *
     * @throws IllegalArgumentException
     *         if the dataset is invalid, or
     *         if unable to generate the parent subdirectory if it does not already exist
     */
    public File getZipBundleFile(String datasetId, BundleType bundleType) throws IllegalArgumentException {
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        File parentFile = new File(filesDir, stdId.substring(0, 4));
        if ( !parentFile.isDirectory() ) {
            if ( parentFile.exists() )
                throw new IllegalArgumentException("File exists but is not a directory: " + parentFile.getPath());
            if ( !parentFile.mkdir() )
                throw new IllegalArgumentException("Problems creating the directory: " + parentFile.getPath());
        }
        File bundleFile;
        switch ( bundleType ) {
            case ORIG_FILE_BAGIT_ZIP:
                bundleFile = new File(parentFile, stdId + "_bagit.zip");
                break;
            case ENHANCED_FILE_PLAIN_ZIP:
                bundleFile = new File(parentFile, stdId + "_enhanced.zip");
                break;
            default:
                throw new IllegalArgumentException("Unknown bundle type of " + bundleType);
        }
        return bundleFile;
    }

    /**
     * Creates the bagit zip file bundle of the original data file converted to "Excel" CSV
     * format as well as any appropriate metadata files.  If appropriate, e-mails this bundle
     * for archival.  Also, if appropriate, commits this bundle to version control using the
     * given message.
     * <p>
     * If the value of userRealName is {@link DashboardServerUtils#NOMAIL_USER_REAL_NAME}
     * and the value of userEmail is {@link DashboardServerUtils#NOMAIL_USER_EMAIL}, then
     * the bundle is created but not emailed.
     * <p>
     * If the version control commit message is null or empty, the bundle is not committed
     * to version control.
     *
     * @param datasetId
     *         create the bundle for the dataset with this ID
     * @param message
     *         version control commit message for the bundle file
     * @param userRealName
     *         real name of the user make this archival request
     * @param userEmail
     *         email address of the user making this archival request
     *         (this address will be cc'd on the bundle email sent for archival)
     *
     * @return a message indicating what was sent and to whom
     *
     * @throws IllegalArgumentException
     *         if the dataset is not valid, or if there is a problem sending the archival request email
     * @throws IOException
     *         if unable to read the default DashboardConfigStore,
     *         if the dataset is has no data or metadata files,
     *         if unable to create the bundle file, or
     *         if unable to commit the bundle to version control
     */
    public String sendOrigFilesBundle(String datasetId, String message, String userRealName,
            String userEmail) throws IllegalArgumentException, IOException {
        if ( (toEmails == null) || (toEmails.length == 0) )
            throw new IllegalArgumentException("no archival email address");
        if ( (ccEmails == null) || (ccEmails.length == 0) )
            throw new IllegalArgumentException("no cc email address");
        if ( (userRealName == null) || userRealName.isEmpty() )
            throw new IllegalArgumentException("no user name");
        if ( (userEmail == null) || userEmail.isEmpty() )
            throw new IllegalArgumentException("no user email address");
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        DashboardConfigStore configStore = DashboardConfigStore.get(false);

        // The platform name needed for the email message - mainly for moorings, which do not have a distinctive NODC code
        String platformName = "";
        // The SDIMetadata object created from PI-provided OME metadata - for auto-generating OCADS XML file
        SDIMetadata sdimdata = null;

        MetadataFileHandler mdataHandler = configStore.getMetadataFileHandler();
        // Check if there is a PI-provided OME document
        try {
            DashboardOmeMetadata omeMetadata = mdataHandler.getOmeFromFile(stdId, DashboardUtils.PI_OME_FILENAME);
            // Get the platform name
            platformName = omeMetadata.getPlatformName();
            // Make sure all data column names used are mapped to the correct CdiacReader.VarType
            HashMap<String,CdiacReader.VarType> nameToVarTypeMap = new HashMap<String,CdiacReader.VarType>();
            DashboardDataset dsetInfo = configStore.getDataFileHandler().getDatasetFromInfoFile(datasetId);
            ArrayList<String> dataColNames = dsetInfo.getUserColNames();
            ArrayList<DataColumnType> dataColTypes = dsetInfo.getDataColTypes();
            for (int k = 0; k < dataColNames.size(); k++) {
                CdiacReader.VarType vtype = DASH_TYPE_TO_CDIAC_TYPE.get(dataColTypes.get(k).getVarName());
                if ( vtype != null ) {
                    String namekey = CdiacReader.STRIP_PATTERN.matcher(dataColNames.get(k).toUpperCase())
                                                              .replaceAll("")
                                                              .toLowerCase();
                    nameToVarTypeMap.put(namekey, vtype);
                }
            }
            // Create an SDIMetadata object from the PI-provided CDIAC XML
            File omefile = mdataHandler.getMetadataFile(stdId, DashboardUtils.PI_OME_FILENAME);
            FileReader xmlReader = new FileReader(omefile);
            CdiacReader reader = new CdiacReader(xmlReader, nameToVarTypeMap);
            sdimdata = reader.createSDIMetadata();
            xmlReader.close();
        } catch ( Exception ex ) {
            // Probably no PI_OME.xml metadata document
        }
        if ( platformName.isEmpty() ) {
            // Check if there is a platform name in the OME stub
            try {
                DashboardOmeMetadata omeMetadata = mdataHandler.getOmeFromFile(stdId, DashboardUtils.OME_FILENAME);
                platformName = omeMetadata.getPlatformName();
            } catch ( Exception ex ) {
                // Should always have an OME.xml metadata document, but ignore this problem here
            }
        }

        String fullId;
        String emailBundleName;
        if ( !platformName.isEmpty() ) {
            fullId = stdId + " (" + platformName + ")";
            emailBundleName = stdId + "_" + nameCleaner.matcher(platformName).replaceAll("") + "_bagit.zip";
        }
        else {
            fullId = stdId;
            emailBundleName = stdId + "_bagit.zip";
        }

        // Generate the bundle as a zip file
        File bundleFile = getZipBundleFile(stdId, BundleType.ORIG_FILE_BAGIT_ZIP);
        String infoMsg = createBagitFilesBundle(stdId, sdimdata);

        // Commit the bundle to version control
        if ( (message != null) && !message.isEmpty() ) {
            try {
                commitVersion(bundleFile, message);
            } catch ( Exception ex ) {
                throw new IOException("Problems committing the archival file bundle for " +
                        stdId + ": " + ex.getMessage());
            }
        }

        // If userRealName is "nobody" and userEmail is "nobody@nowhere" then skip the email
        if ( DashboardServerUtils.NOMAIL_USER_REAL_NAME.equals(userRealName) &&
                DashboardServerUtils.NOMAIL_USER_EMAIL.equals(userEmail) ) {
            return "Data files archival bundle created but not emailed";
        }

        // Create a Session for sending out the email
        Properties props = System.getProperties();
        if ( debugIt )
            props.setProperty("mail.debug", "true");
        props.setProperty("mail.transport.protocol", "smtp");
        if ( (smtpHost != null) && !smtpHost.isEmpty() )
            props.put("mail.smtp.host", smtpHost);
        else
            props.put("mail.smtp.host", "localhost");
        if ( (smtpPort != null) && !smtpPort.isEmpty() )
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

        // Parse all the email addresses, add the user's email as the first cc'd address
        InternetAddress[] ccAddresses = new InternetAddress[ccEmails.length + 1];
        try {
            ccAddresses[0] = new InternetAddress(userEmail);
        } catch ( MessagingException ex ) {
            String errmsg = getMessageExceptionMsgs(ex);
            throw new IllegalArgumentException("Invalid user email address: " + errmsg, ex);
        }
        for (int k = 0; k < ccEmails.length; k++) {
            try {
                ccAddresses[k + 1] = new InternetAddress(ccEmails[k]);
            } catch ( MessagingException ex ) {
                String errmsg = getMessageExceptionMsgs(ex);
                throw new IllegalArgumentException("Invalid 'CC:' email address: " + errmsg, ex);
            }
        }
        InternetAddress[] toAddresses = new InternetAddress[toEmails.length];
        for (int k = 0; k < toEmails.length; k++) {
            try {
                toAddresses[k] = new InternetAddress(toEmails[k]);
            } catch ( MessagingException ex ) {
                String errmsg = getMessageExceptionMsgs(ex);
                throw new IllegalArgumentException("Invalid 'To:' email address: " + errmsg, ex);
            }
        }

        // Create the email message with the renamed zip attachment
        MimeMessage msg = new MimeMessage(sessn);
        try {
            msg.setHeader("X-Mailer", "ArchiveFilesBundler");
            msg.setSubject(EMAIL_SUBJECT_MSG_START + fullId + EMAIL_SUBJECT_MSG_MIDDLE + userRealName);
            msg.setSentDate(new Date());
            // Set the addresses
            // Mark as sent from the second cc'd address (the dashboard's);
            // the first cc address is the user and any others are purely supplemental
            msg.setFrom(ccAddresses[1]);
            msg.setReplyTo(ccAddresses);
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setRecipients(Message.RecipientType.CC, ccAddresses);
            // Create the text message part
            MimeBodyPart textMsgPart = new MimeBodyPart();
            textMsgPart.setText(EMAIL_MSG_START + fullId + EMAIL_MSG_MIDDLE + userRealName + EMAIL_MSG_END);
            // Create the attachment message part
            MimeBodyPart attMsgPart = new MimeBodyPart();
            attMsgPart.attachFile(bundleFile);
            attMsgPart.setFileName(emailBundleName);
            // Create and add the multipart document to the message
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(textMsgPart);
            mp.addBodyPart(attMsgPart);
            msg.setContent(mp);
            // Update the headers
            msg.saveChanges();
        } catch ( MessagingException ex ) {
            String errmsg = getMessageExceptionMsgs(ex);
            throw new IllegalArgumentException("Problems creating the archival request email: " + errmsg, ex);
        }

        // Send the email
        try {
            Transport.send(msg);
        } catch ( MessagingException ex ) {
            String errmsg = getMessageExceptionMsgs(ex);
            throw new IllegalArgumentException("Problems sending the archival request email: " + errmsg, ex);
        }

        infoMsg += "Files bundle sent To: " + toEmails[0];
        for (int k = 1; k < toEmails.length; k++) {
            infoMsg += ", " + toEmails[k];
        }
        infoMsg += "; CC: " + userEmail + ", " + ccEmails[0];
        for (int k = 1; k < ccEmails.length; k++) {
            infoMsg += ", " + ccEmails[k];
        }
        infoMsg += "\n";
        return infoMsg;
    }

    /**
     * Generates the bagit zip file of the original data file converted to "Excel" CSV format,
     * as well as any appropriate metadata files for a given dataset.
     * Use {@link #getZipBundleFile(String, BundleType)} with {@link BundleType#ORIG_FILE_BAGIT_ZIP}
     * bundle type to get the virtual File of the created bundle.
     *
     * @param expocode
     *         create the bagit zip file for the dataset with this ID
     * @param sdimdata
     *         metadata to be written as an OCADS XML file; can be null
     *
     * @return info message describing this bundle and its contents
     *
     * @throws IllegalArgumentException
     *         if the expocode is invalid, or
     *         if unable to create the bagit bundle directory used for creating the bagit zip file
     * @throws IOException
     *         if there is no data or metadata files for this dataset,
     *         if there were problems copying files to the bagit bundle directory, or
     *         if there were problems creating the bagit zip file from the bagit bundle directory
     */
    private String createBagitFilesBundle(String expocode, SDIMetadata sdimdata)
            throws IllegalArgumentException, IOException {
        String stdId = DashboardServerUtils.checkDatasetID(expocode);
        File bundleFile = getZipBundleFile(stdId, BundleType.ORIG_FILE_BAGIT_ZIP);
        DashboardConfigStore configStore = DashboardConfigStore.get(false);

        // Get the original data file for this dataset
        File dataFile = configStore.getDataFileHandler().datasetDataFile(stdId);
        if ( !dataFile.exists() )
            throw new IOException("No data file for " + stdId);
        if ( !dataFile.getName().endsWith(".tsv") )
            throw new RuntimeException("Unexpected data file name does not end with .tsv");
        String csvFilename = dataFile.getName();
        csvFilename = csvFilename.substring(0, csvFilename.length() - 4) + ".csv";

        // Get the list of metadata documents to be bundled with this data file
        ArrayList<File> addlDocs = new ArrayList<File>();
        MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
        for (DashboardMetadata mdata : metadataHandler.getMetadataFiles(stdId)) {
            // Exclude the (dataset)/OME.xml document at this time;
            // do include the (dataset)/PI_OME.xml
            String filename = mdata.getFilename();
            if ( !filename.equals(DashboardUtils.OME_FILENAME) ) {
                addlDocs.add(metadataHandler.getMetadataFile(stdId, filename));
            }
        }
        if ( addlDocs.isEmpty() )
            throw new IOException("No metadata/supplemental documents for " + stdId);

        // Create the bagit bundle directory
        String dirname = bundleFile.getName();
        if ( !dirname.endsWith(".zip") )
            throw new RuntimeException("Unexpected bagit bundle filename does not end with \".zip\"");
        dirname = dirname.substring(0, dirname.length() - 4);
        File bundleDir = new File(bundleFile.getParent(), dirname);
        if ( !bundleDir.isDirectory() ) {
            if ( bundleDir.exists() )
                throw new IllegalArgumentException("File exists but is not a directory: " + bundleDir.getPath());
            if ( !bundleDir.mkdir() )
                throw new IllegalArgumentException("Problems creating the directory: " + bundleDir.getPath());
        }

        // Copy all the files to the bagit bundle directory
        String infoMsg = "Created files bundle " + bundleFile.getName() + " containing files:\n";
        try {
            infoMsg += "    " + csvFilename + "\n";
            File dest = new File(bundleDir, csvFilename);
            copyTsvToCsvFile(dataFile, dest);
            for (File metaFile : addlDocs) {
                infoMsg += "    " + metaFile.getName() + "\n";
                dest = new File(bundleDir, metaFile.getName());
                Files.copy(metaFile.toPath(), dest.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            }
            if ( sdimdata != null ) {
                infoMsg += "    " + OCADS_OME_XML_FILENAME + "\n";
                dest = new File(bundleDir, OCADS_OME_XML_FILENAME);
                FileWriter xmlwriter = new FileWriter(dest);
                (new OcadsWriter()).writeOcadsXml(sdimdata, xmlwriter);
                xmlwriter.close();
            }
        } catch ( Exception ex ) {
            throw new IOException("Problems copying files to bagit bundle directory: " + ex.getMessage(), ex);
        }

        // Create the bagit directory tree in-place (restructures and adds files) from the bagit bundles directory
        try {
            Bag bag = BagCreator.bagInPlace(bundleDir.toPath(), Arrays.asList(StandardSupportedAlgorithms.MD5), false);
            BagVerifier verifier = new BagVerifier();
            verifier.isComplete(bag, true);
            verifier.isValid(bag, true);
        } catch ( Exception ex ) {
            throw new IOException("Problems creating the bagit files: " + ex.getMessage(), ex);
        }

        // Create a zip file of the bagit directory tree
        final Path bundleDirPath = bundleDir.toPath();
        final ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(bundleFile));
        try {
            Files.walkFileTree(bundleDirPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                    Path relative = bundleDirPath.relativize(filePath);
                    ZipEntry entry = new ZipEntry(expocode + File.separator + relative.toString());
                    entry.setTime(filePath.toFile().lastModified());
                    zipOut.putNextEntry(entry);
                    Files.copy(filePath, zipOut);
                    zipOut.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        } finally {
            zipOut.close();
        }

        // No longer need the bagit directory tree
        Files.walkFileTree(bundleDirPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                Files.delete(filePath);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dirPath, IOException ex) throws IOException {
                if ( ex == null )
                    Files.delete(dirPath);
                return FileVisitResult.CONTINUE;
            }
        });

        return infoMsg;
    }

    /**
     * Generates a single-cruise enhanced data file, then bundles that report with all the metadata documents
     * for that dataset.  Use {@link #getZipBundleFile(String, BundleType)} with
     * {@link BundleType#ENHANCED_FILE_PLAIN_ZIP} bundle type to get the virtual File of the created bundle.
     *
     * @param expocode
     *         create the bundle for the dataset with this ID
     *
     * @return the warning messages from generating the single-cruise enhanced data file
     *
     * @throws IllegalArgumentException
     *         if the expoocode is invalid
     * @throws IOException
     *         if unable to read the default DashboardConfigStore,
     *         if unable to create the enhanced data file, or
     *         in unable to create the bundle file
     */
    public ArrayList<String> createEnhancedFilesBundle(String expocode) throws
            IllegalArgumentException, IOException {
        File bundleFile = getZipBundleFile(expocode, BundleType.ENHANCED_FILE_PLAIN_ZIP);
        DashboardConfigStore configStore = DashboardConfigStore.get(false);

        // Generate the single-cruise SOCAT-enhanced data file
        SocatCruiseReporter reporter = new SocatCruiseReporter(configStore);
        File enhancedDataFile = new File(bundleFile.getParent(), expocode + ENHANCED_REPORT_NAME_EXTENSION);
        ArrayList<String> warnings = reporter.generateReport(expocode, enhancedDataFile);

        // Get the list of metadata documents to be bundled with this data file
        ArrayList<File> addlDocs = new ArrayList<File>();
        MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
        for (DashboardMetadata mdata : metadataHandler.getMetadataFiles(expocode)) {
            // Exclude the (expocode)/OME.xml document at this time;
            // do include the (expocode)/PI_OME.xml
            String filename = mdata.getFilename();
            if ( !filename.equals(DashboardUtils.OME_FILENAME) ) {
                addlDocs.add(metadataHandler.getMetadataFile(expocode, filename));
            }
        }

        // Generate the bundle as a zip file
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(bundleFile));
        try {
            copyFileToZipBundle(zipOut, expocode, enhancedDataFile);
            for (File metaFile : addlDocs) {
                copyFileToZipBundle(zipOut, expocode, metaFile);
            }
        } finally {
            zipOut.close();
        }
        // No longer need the SOCAT-enhanced data file
        enhancedDataFile.delete();

        return warnings;
    }

    /**
     * Returns all messages in a possibly-nested MessagingException.  The messages are returned as a single String
     * by joining all the Exception messages together using a comma and space.
     *
     * @param ex
     *         get the error messages from this MessagingException
     *
     * @return all error messages concatenated together using a comma and a space;
     *         if no messages are present, an empty String is returned
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
     * Copies the contents of the given data file to the zip file.
     * Note that only the file names, without any path component, is recorded
     * in the zip file as being under a directory with name given by parentName.
     * The timestamp recorded in the zip file is the last modified time of the file.
     *
     * @param zipOut
     *         copy the contents of the given file to here
     * @param parentName
     *         name to use as the parent directory of all the files in the zip file
     * @param dataFile
     *         copy the contents of this file
     *
     * @throws IOException
     *         if reading from the data files throws one, or
     *         if writing to the zip file throws one
     */
    private void copyFileToZipBundle(ZipOutputStream zipOut, String parentName, File dataFile) throws IOException {
        ZipEntry entry = new ZipEntry(parentName + File.separator + dataFile.getName());
        entry.setTime(dataFile.lastModified());
        zipOut.putNextEntry(entry);
        Files.copy(dataFile.toPath(), zipOut);
        zipOut.closeEntry();
    }

    /**
     * Copies the simple TSV data file to a new file in "Excel" CSV format.
     *
     * @param dataFile
     *         existing simple TSV data file to be copied
     * @param csvFile
     *         new "Excel" CSV file to create
     *
     * @throws FileNotFoundException
     *         if the TSV data file does not exist
     * @throws IOException
     *         if reading from or writing to the files throws one
     */
    private void copyTsvToCsvFile(File dataFile, File csvFile) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(dataFile));
        try {
            CSVPrinter csvout = new CSVPrinter(new FileWriter(csvFile),
                    CSVFormat.EXCEL.withIgnoreSurroundingSpaces().withDelimiter(','));
            try {
                String dataline = reader.readLine();
                while ( dataline != null ) {
                    csvout.printRecord(Arrays.asList(dataline.split("\t", -1)));
                    dataline = reader.readLine();
                }
                csvout.flush();
            } finally {
                csvout.close();
            }
        } finally {
            reader.close();
        }
        try {
            Files.setLastModifiedTime(csvFile.toPath(), Files.getLastModifiedTime(dataFile.toPath()));
        } catch ( Exception ex ) {
            // ignore any problems with resetting the last modified time
        }
    }

}
