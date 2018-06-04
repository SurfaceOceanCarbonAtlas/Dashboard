/**
 *
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.actions.DatasetChecker;
import gov.noaa.pmel.dashboard.actions.DatasetSubmitter;
import gov.noaa.pmel.dashboard.actions.OmePdfGenerator;
import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;
import gov.noaa.pmel.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.handlers.PreviewPlotsHandler;
import gov.noaa.pmel.dashboard.handlers.SpellingHandler;
import gov.noaa.pmel.dashboard.handlers.UserFileHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Reads and holds the Dashboard configuration details
 *
 * @author Karl Smith
 */
public class DashboardConfigStore {

    private static final String UPLOAD_VERSION_NAME_TAG = "UploadVersion";
    private static final String QC_VERSION_NAME_TAG = "QCVersion";
    private static final String SVN_USER_NAME_TAG = "SVNUsername";
    private static final String SVN_PASSWORD_NAME_TAG = "SVNPassword";
    private static final String USER_FILES_DIR_NAME_TAG = "UserFilesDir";
    private static final String DATA_FILES_DIR_NAME_TAG = "DataFilesDir";
    private static final String METADATA_FILES_DIR_NAME_TAG = "MetadataFilesDir";
    private static final String DSG_NC_FILES_DIR_NAME_TAG = "DsgNcFilesDir";
    private static final String DEC_DSG_NC_FILES_DIR_NAME_TAG = "DecDsgNcFilesDir";
    private static final String ARCHIVE_BUNDLES_DIR_NAME_TAG = "ArchiveBundlesDir";
    private static final String ARCHIVE_BUNDLES_EMAIL_ADDRESS_TAG = "ArchiveBundlesEmailAddress";
    private static final String CC_BUNDLES_EMAIL_ADDRESS_TAG = "CCBundlesEmailAddress";
    private static final String SMTP_HOST_ADDRESS_TAG = "SMTPHostAddress";
    private static final String SMTP_HOST_PORT_TAG = "SMTPHostPort";
    private static final String SMTP_USERNAME_TAG = "SMTPUsername";
    private static final String SMTP_PASSWORD_TAG = "SMTPPassword";
    private static final String ERDDAP_DSG_FLAG_FILE_NAME_TAG = "ErddapDsgFlagFile";
    private static final String ERDDAP_DEC_DSG_FLAG_FILE_NAME_TAG = "ErddapDecDsgFlagFile";
    private static final String KNOWN_TYPES_PROPS_FILE_TAG = "KnownTypesFile";
    private static final String DEFAULT_TYPE_KEYS_FILE_TAG = "DefaultTypeKeysFile";
    private static final String FERRET_CONFIG_FILE_NAME_TAG = "FerretConfigFile";
    private static final String DATABASE_CONFIG_FILE_NAME_TAG = "DatabaseConfigFile";
    private static final String SPELLING_CONFIG_FILE_NAME_TAG = "SpellingConfigFile";
    private static final String USER_ROLE_NAME_TAG_PREFIX = "RoleFor_";

    private static final String CONFIG_FILE_INFO_MSG =
            "This configuration file should look something like: \n" +
                    "# ------------------------------ \n" +
                    UPLOAD_VERSION_NAME_TAG + "=SomeVersionNumber \n" +
                    QC_VERSION_NAME_TAG + "=SomeVersionNumber \n" +
                    SVN_USER_NAME_TAG + "=SVNUsername \n" +
                    SVN_PASSWORD_NAME_TAG + "=SVNPasswork \n" +
                    USER_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/User/Data \n" +
                    DATA_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/Data/Files \n" +
                    METADATA_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/Metadata/Docs \n" +
                    DSG_NC_FILES_DIR_NAME_TAG + "=/Some/Plain/Dir/For/NetCDF/DSG/Files \n" +
                    DEC_DSG_NC_FILES_DIR_NAME_TAG + "=/Some/Plain/Dir/For/NetCDF/Decimated/DSG/Files \n" +
                    ARCHIVE_BUNDLES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/Archive/Bundles \n" +
                    ARCHIVE_BUNDLES_EMAIL_ADDRESS_TAG + "=archiver@gdac.org \n" +
                    CC_BUNDLES_EMAIL_ADDRESS_TAG + "=dashboard@my.group.org \n" +
                    SMTP_HOST_ADDRESS_TAG + "=smtp.server.for.dashboard \n" +
                    SMTP_HOST_PORT_TAG + "=smtp.server.port.number \n" +
                    SMTP_USERNAME_TAG + "=username.for.smtp \n" +
                    SMTP_PASSWORD_TAG + "=password.for.smtp \n" +
                    ERDDAP_DSG_FLAG_FILE_NAME_TAG + "=/Some/ERDDAP/Flag/Filename/For/DSG/Update \n" +
                    ERDDAP_DEC_DSG_FLAG_FILE_NAME_TAG + "=/Some/ERDDAP/Flag/Filename/For/DecDSG/Update \n" +
                    KNOWN_TYPES_PROPS_FILE_TAG + "=/Path/To/User/Known/Types/PropsFile \n" +
                    DEFAULT_TYPE_KEYS_FILE_TAG + "=/Path/To/To/TypeNameKeys/PropsFile \n" +
                    FERRET_CONFIG_FILE_NAME_TAG + "=/Path/To/FerretConfig/XMLFile \n" +
                    DATABASE_CONFIG_FILE_NAME_TAG + "=/Path/To/DatabaseConfig/PropsFile \n" +
                    SPELLING_CONFIG_FILE_NAME_TAG + "=/Path/To/SpellingConfig/TSVFile \n" +
                    USER_ROLE_NAME_TAG_PREFIX + "SomeUserName=MemberOf1,MemberOf2 \n" +
                    USER_ROLE_NAME_TAG_PREFIX + "SomeManagerName=ManagerOf1,MemberOf2 \n" +
                    USER_ROLE_NAME_TAG_PREFIX + "SomeAdminName=Admin \n" +
                    "# ------------------------------ \n";

    private static final double DEFAULT_MAX_GOOD_CALC_SPEED_KNOTS = 80;
    private static final double DEFAULT_MAX_MAYBE_CALC_SPEED_KNOTS = 400;

    private static final Object SINGLETON_SYNC_OBJECT = new Object();
    private static DashboardConfigStore singleton = null;

    // Map of username to user info
    private HashMap<String,DashboardUserInfo> userInfoMap;
    private String uploadVersion;
    private String qcVersion;
    private UserFileHandler userFileHandler;
    private DataFileHandler dataFileHandler;
    private CheckerMessageHandler checkerMsgHandler;
    private MetadataFileHandler metadataFileHandler;
    private SpellingHandler spellingHandler;
    private ArchiveFilesBundler archiveFilesBundler;
    private DsgNcFileHandler dsgNcFileHandler;
    private FerretConfig ferretConf;
    private DatasetChecker datasetChecker;
    private DatabaseRequestHandler databaseRequestHandler;
    private PreviewPlotsHandler plotsHandler;
    private DatasetSubmitter datasetSubmitter;
    private OmePdfGenerator omePdfGenerator;
    private KnownDataTypes knownUserDataTypes;
    private KnownDataTypes knownMetadataTypes;
    private KnownDataTypes knownDataFileTypes;
    private double maxGoodCalcSpeedKnots;
    private double maxMaybeCalcSpeedKnots;

    private HashSet<File> filesToWatch;
    private Thread watcherThread;
    private WatchService watcher;
    private boolean needToRestart;
    private Logger itsLogger;

    /**
     * Creates a data store initialized from the contents of the standard configuration file.  See the contents of
     * {@link #CONFIG_FILE_INFO_MSG} for information on the configuration file format.
     * <p>
     * Do not create an instance of this class; instead use {@link #get()} to retrieve the singleton instance
     *
     * @param startMonitors
     *         start the file change monitors?
     *
     * @throws IOException
     *         if unable to read the standard configuration file
     */
    private DashboardConfigStore(boolean startMonitors) throws IOException {
        String baseDir = System.getenv("CATALINA_BASE");
        if ( baseDir == null ) {
            baseDir = System.getProperty("CATALINA_BASE");
        }
        if ( baseDir == null ) {
            throw new IOException("CATALINA_BASE environment variable is not defined");
        }
        else {
            System.out.println("CATALINA_BASE:" + baseDir);
        }
        baseDir += File.separator;

        // First check is UPLOAD_DASHBOARD_SERVER_NAME is defined for alternate configurations
        // when running the dashboard.program.* applications
        String serverAppName = System.getenv("UPLOAD_DASHBOARD_SERVER_NAME");
        if ( serverAppName == null )
            serverAppName = System.getProperty("UPLOAD_DASHBOARD_SERVER_NAME");
        if ( serverAppName == null ) {
            // Get the app name from the location of this class source in tomcat;
            // e.g., "/home/users/tomcat/webapps/SocatUploadDashboard/WEB-INF/classes/gov/noaa/pmel/dashboard/server/DashboardConfigStore.class"
            try {
                File webAppSubDir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
                do {
                    webAppSubDir = webAppSubDir.getParentFile();
                    serverAppName = webAppSubDir.getName();
                } while ( !serverAppName.equals("WEB-INF") );
                webAppSubDir = webAppSubDir.getParentFile();
                serverAppName = webAppSubDir.getName();
            } catch ( Exception ex ) {
                serverAppName = "";
            }
            if ( serverAppName.isEmpty() )
                throw new IOException("Unable to obtain the upload dashboard server name");
        }
        String appContentDirPath = baseDir + "content" + File.separator + serverAppName + File.separator;
        String appConfigDirPath = appContentDirPath + "config" + File.separator;
        File appConfigDir = new File(appConfigDirPath);
        if ( !appConfigDir.exists() || !appConfigDir.isDirectory() || !appConfigDir.canRead() ) {
            throw new IllegalStateException("Problem with app config dir: " + appConfigDir.getAbsoluteFile());
        }
        String previewDirname = baseDir + "webapps" + File.separator + serverAppName + File.separator +
                "preview" + File.separator;

        // Configure the log4j2 logger
        System.setProperty("log4j.configurationFile", appConfigDirPath + "log4j2.properties");
        itsLogger = LogManager.getLogger(serverAppName);

        // Record configuration files that should be monitored for changes
        filesToWatch = new HashSet<File>();

        // Get the properties from the primary configuration file
        Properties configProps = new Properties();
        File configFile = new File(appConfigDir, serverAppName + ".properties");
        filesToWatch.add(configFile);
        try {
            FileReader reader = new FileReader(configFile);
            try {
                configProps.load(reader);
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            throw new IOException("Problems reading " + configFile.getPath() + "\n" +
                    ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }
        String propVal;

        // Read the SOCAT versions
        try {
            propVal = configProps.getProperty(UPLOAD_VERSION_NAME_TAG);
            if ( propVal == null )
                throw new IllegalArgumentException("value not defined");
            propVal = propVal.trim();
            if ( propVal.isEmpty() )
                throw new IllegalArgumentException("blank value");
            uploadVersion = propVal;
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + UPLOAD_VERSION_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }
        try {
            propVal = configProps.getProperty(QC_VERSION_NAME_TAG);
            if ( propVal == null )
                throw new IllegalArgumentException("value not defined");
            propVal = propVal.trim();
            if ( propVal.isEmpty() )
                throw new IllegalArgumentException("blank value");
            qcVersion = propVal;
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + QC_VERSION_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }

        // Read the SVN username
        String svnUsername;
        try {
            propVal = configProps.getProperty(SVN_USER_NAME_TAG);
            if ( propVal == null )
                throw new IllegalArgumentException("value not defined");
            propVal = propVal.trim();
            if ( propVal.isEmpty() )
                throw new IllegalArgumentException("blank value");
            svnUsername = propVal;
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + SVN_USER_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }

        // Read the SVN password; can be blank or not given
        String svnPassword = "";
        propVal = configProps.getProperty(SVN_PASSWORD_NAME_TAG);
        if ( propVal != null )
            svnPassword = propVal.trim();

        // Get the known user-provided, file metadata, and file data types
        try {
            propVal = getFilePathProperty(configProps, KNOWN_TYPES_PROPS_FILE_TAG, appConfigDir);
            Properties typeProps = new Properties();
            FileReader propsReader = new FileReader(propVal);
            try {
                typeProps.load(propsReader);
            } finally {
                propsReader.close();
            }
            knownUserDataTypes = new KnownDataTypes();
            knownUserDataTypes.addStandardTypesForUsers();
            knownUserDataTypes.addTypesFromProperties(typeProps, DashDataType.Role.USER_DATA, itsLogger);
            knownMetadataTypes = new KnownDataTypes();
            knownMetadataTypes.addStandardTypesForMetadataFiles();
            knownMetadataTypes.addTypesFromProperties(typeProps, DashDataType.Role.FILE_METADATA, itsLogger);
            knownDataFileTypes = new KnownDataTypes();
            knownDataFileTypes.addStandardTypesForDataFiles();
            knownDataFileTypes.addTypesFromProperties(typeProps, DashDataType.Role.FILE_DATA, itsLogger);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + KNOWN_TYPES_PROPS_FILE_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }
        if ( itsLogger.isInfoEnabled() ) {
            itsLogger.info("Known user-provided data types: ");
            for (DashDataType<?> dtype : knownUserDataTypes.getKnownTypesSet()) {
                itsLogger.info("    " + dtype.getVarName() + "=" + dtype.toPropertyValue());
            }
            itsLogger.info("Known file metadata types: ");
            for (DashDataType<?> dtype : knownMetadataTypes.getKnownTypesSet()) {
                itsLogger.info("    " + dtype.getVarName() + "=" + dtype.toPropertyValue());
            }
            itsLogger.info("Known file data types: ");
            for (DashDataType<?> dtype : knownDataFileTypes.getKnownTypesSet()) {
                itsLogger.info("    " + dtype.getVarName() + "=" + dtype.toPropertyValue());
            }
        }
        try {
            DoubleDashDataType calcSpeed =
                    (DoubleDashDataType) knownDataFileTypes.getDataType(SocatTypes.CALC_SPEED.getVarName());
            maxGoodCalcSpeedKnots = (double) calcSpeed.getMaxGoodValue();
            maxMaybeCalcSpeedKnots = (double) calcSpeed.getMaxMaybeValue();
        } catch ( Exception ex ) {
            maxGoodCalcSpeedKnots = 80;
            maxMaybeCalcSpeedKnots = 400;
        }

        // Read the name of the default-column-names-to-types-with-units configuration file
        String nameKeysToColumnTypesFilename;
        try {
            nameKeysToColumnTypesFilename = getFilePathProperty(configProps, DEFAULT_TYPE_KEYS_FILE_TAG, appConfigDir);
            if ( !(new File(nameKeysToColumnTypesFilename).exists()) )
                throw new IOException("file does not exist");
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + DEFAULT_TYPE_KEYS_FILE_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }

        // Handler for user-specific configuration files
        try {
            propVal = getFilePathProperty(configProps, USER_FILES_DIR_NAME_TAG, appConfigDir);
            userFileHandler = new UserFileHandler(propVal, svnUsername, svnPassword,
                    nameKeysToColumnTypesFilename, knownUserDataTypes);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + USER_FILES_DIR_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }

        // Handler for dataset data files and handler for automated data checker messages
        try {
            propVal = getFilePathProperty(configProps, DATA_FILES_DIR_NAME_TAG, appConfigDir);
            dataFileHandler = new DataFileHandler(propVal, svnUsername, svnPassword,
                    knownUserDataTypes, userFileHandler, uploadVersion);
            // Put automated data checker message files in the same directory
            checkerMsgHandler = new CheckerMessageHandler(propVal);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + DATA_FILES_DIR_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }

        // Handler for dataset metadata files
        try {
            propVal = getFilePathProperty(configProps, METADATA_FILES_DIR_NAME_TAG, appConfigDir);
            metadataFileHandler = new MetadataFileHandler(propVal, svnUsername, svnPassword);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + METADATA_FILES_DIR_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }

        // Handler for name corrections - primarly correcting anglicized names
        try {
            propVal = getFilePathProperty(configProps, SPELLING_CONFIG_FILE_NAME_TAG, appConfigDir);
            spellingHandler = new SpellingHandler(propVal);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + SPELLING_CONFIG_FILE_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }

        // Handler for archival bundles
        propVal = configProps.getProperty(ARCHIVE_BUNDLES_EMAIL_ADDRESS_TAG);
        if ( propVal == null )
            throw new IOException("Invalid " + ARCHIVE_BUNDLES_EMAIL_ADDRESS_TAG + " value specified in " +
                    configFile.getPath() + "\nvalue not defined\n" + CONFIG_FILE_INFO_MSG);
        String[] toEmailAddresses = propVal.trim().split(",");
        propVal = configProps.getProperty(CC_BUNDLES_EMAIL_ADDRESS_TAG);
        if ( propVal == null )
            throw new IOException("Invalid " + CC_BUNDLES_EMAIL_ADDRESS_TAG + " value specified in " +
                    configFile.getPath() + "\nvalue not defined\n" + CONFIG_FILE_INFO_MSG);
        String[] ccEmailAddresses = propVal.trim().split(",");
        propVal = configProps.getProperty(SMTP_HOST_ADDRESS_TAG);
        if ( propVal == null )
            throw new IOException("Invalid " + SMTP_HOST_ADDRESS_TAG + " value specified in " +
                    configFile.getPath() + "\nvalue not defined\n" + CONFIG_FILE_INFO_MSG);
        String smtpHostAddress = propVal.trim();
        propVal = configProps.getProperty(SMTP_HOST_PORT_TAG);
        if ( propVal == null )
            throw new IOException("Invalid " + SMTP_HOST_PORT_TAG + " value specified in " +
                    configFile.getPath() + "\nvalue not defined\n" + CONFIG_FILE_INFO_MSG);
        String smtpHostPort = propVal.trim();
        propVal = configProps.getProperty(SMTP_USERNAME_TAG);
        if ( propVal == null )
            throw new IOException("Invalid " + SMTP_USERNAME_TAG + " value specified in " +
                    configFile.getPath() + "\nvalue not defined\n" + CONFIG_FILE_INFO_MSG);
        String smtpUsername = propVal.trim();
        propVal = configProps.getProperty(SMTP_PASSWORD_TAG);
        if ( propVal == null )
            throw new IOException("Invalid " + SMTP_PASSWORD_TAG + " value specified in " +
                    configFile.getPath() + "\nvalue not defined\n" + CONFIG_FILE_INFO_MSG);
        String smtpPassword = propVal.trim();
        try {
            propVal = getFilePathProperty(configProps, ARCHIVE_BUNDLES_DIR_NAME_TAG, appConfigDir);
            archiveFilesBundler = new ArchiveFilesBundler(propVal, svnUsername, svnPassword, toEmailAddresses,
                    ccEmailAddresses, smtpHostAddress, smtpHostPort, smtpUsername, smtpPassword, false);
            itsLogger.info("Archive files bundler and mailer using:");
            itsLogger.info("    bundles directory: " + propVal);
            String emails = toEmailAddresses[0];
            for (int k = 1; k < toEmailAddresses.length; k++) {
                emails += ", " + toEmailAddresses[k];
            }
            itsLogger.info("    To: " + emails);
            emails = ccEmailAddresses[0];
            for (int k = 1; k < ccEmailAddresses.length; k++) {
                emails += ", " + ccEmailAddresses[k];
            }
            itsLogger.info("    CC: " + emails);
            itsLogger.info("    SMTP host: " + smtpHostAddress);
            itsLogger.info("    SMTP port: " + smtpHostPort);
            itsLogger.info("    SMTP username: " + smtpUsername);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + ARCHIVE_BUNDLES_DIR_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }

        // Ferret configuration
        InputStream stream;
        try {
            propVal = getFilePathProperty(configProps, FERRET_CONFIG_FILE_NAME_TAG, appConfigDir);
            File ferretPropsFile = new File(propVal);
            filesToWatch.add(ferretPropsFile);
            stream = new FileInputStream(ferretPropsFile);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + FERRET_CONFIG_FILE_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }
        try {
            try {
                SAXBuilder sb = new SAXBuilder();
                Document jdom = sb.build(stream);
                ferretConf = new FerretConfig();
                ferretConf.setRootElement((Element) jdom.getRootElement().clone());
            } finally {
                stream.close();
            }
            itsLogger.info("read Ferret configuration file " + propVal);
        } catch ( Exception ex ) {
            throw new IOException("Invalid value in the Ferret configuration file specified by the " +
                    FERRET_CONFIG_FILE_NAME_TAG + " value given in " + configFile.getPath() + "\n" + ex.getMessage());
        }

        // Handler for DSG NC files
        String dsgFileDirName;
        try {
            dsgFileDirName = getFilePathProperty(configProps, DSG_NC_FILES_DIR_NAME_TAG, appConfigDir);
            itsLogger.info("DSG directory = " + dsgFileDirName);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + DSG_NC_FILES_DIR_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }
        String decDsgFileDirName;
        try {
            decDsgFileDirName = getFilePathProperty(configProps, DEC_DSG_NC_FILES_DIR_NAME_TAG, appConfigDir);
            itsLogger.info("Decimated DSG directory = " + decDsgFileDirName);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + DEC_DSG_NC_FILES_DIR_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }
        String erddapDsgFlagFileName;
        try {
            erddapDsgFlagFileName = getFilePathProperty(configProps, ERDDAP_DSG_FLAG_FILE_NAME_TAG, appConfigDir);
            itsLogger.info("ERDDAP DSG flag file = " + erddapDsgFlagFileName);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + ERDDAP_DSG_FLAG_FILE_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }
        String erddapDecDsgFlagFileName;
        try {
            erddapDecDsgFlagFileName =
                    getFilePathProperty(configProps, ERDDAP_DEC_DSG_FLAG_FILE_NAME_TAG, appConfigDir);
            itsLogger.info("ERDDAP decimated DSG flag file = " + erddapDecDsgFlagFileName);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + ERDDAP_DEC_DSG_FLAG_FILE_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }
        try {
            dsgNcFileHandler = new DsgNcFileHandler(dsgFileDirName, decDsgFileDirName, erddapDsgFlagFileName,
                    erddapDecDsgFlagFileName, ferretConf, knownUserDataTypes, knownMetadataTypes, knownDataFileTypes);
        } catch ( Exception ex ) {
            throw new IOException(ex);
        }

        // Handler for database interactions
        try {
            propVal = getFilePathProperty(configProps, DATABASE_CONFIG_FILE_NAME_TAG, appConfigDir);
            filesToWatch.add(new File(propVal));
            databaseRequestHandler = new DatabaseRequestHandler(propVal);
            itsLogger.info("read Database configuration file " + propVal);
        } catch ( Exception ex ) {
            throw new IOException("Invalid " + DATABASE_CONFIG_FILE_NAME_TAG + " value specified in " +
                    configFile.getPath() + "\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
        }

        // Automated data checker
        datasetChecker = new DatasetChecker(knownUserDataTypes, checkerMsgHandler);

        // Handler for preview plots - uses various handlers just created
        plotsHandler = new PreviewPlotsHandler(previewDirname + "dsgfiles",
                previewDirname + "plots", this);

        // OME XML to PDF generator
        omePdfGenerator = new OmePdfGenerator(appConfigDir, metadataFileHandler, dataFileHandler);

        // Dataset submitter - uses various handlers just created
        datasetSubmitter = new DatasetSubmitter(this);

        // Authorized users and their roles
        userInfoMap = new HashMap<String,DashboardUserInfo>();
        for (Entry<Object,Object> entry : configProps.entrySet()) {
            if ( !((entry.getKey() instanceof String) && (entry.getValue() instanceof String)) )
                continue;
            String username = (String) entry.getKey();
            if ( !username.startsWith(USER_ROLE_NAME_TAG_PREFIX) )
                continue;
            username = username.substring(USER_ROLE_NAME_TAG_PREFIX.length());
            username = DashboardServerUtils.cleanUsername(username);
            DashboardUserInfo userInfo;
            try {
                userInfo = new DashboardUserInfo(username);
            } catch ( IllegalArgumentException ex ) {
                throw new IOException(ex.getMessage() + "\n" + "for " + username + " specified in " +
                        configFile.getPath() + "\n" + CONFIG_FILE_INFO_MSG);
            }
            String rolesString = (String) entry.getValue();
            try {
                userInfo.addUserRoles(rolesString);
            } catch ( IllegalArgumentException ex ) {
                throw new IOException(ex.getMessage() + "\n" + "for " + username + " specified in " +
                        configFile.getPath() + "\n" + CONFIG_FILE_INFO_MSG);
            }
            userInfoMap.put(username, userInfo);
        }
        for (DashboardUserInfo info : userInfoMap.values()) {
            itsLogger.info("    user info: " + info.toString());
        }

        itsLogger.info("read configuration file " + configFile.getPath());
        watcher = null;
        watcherThread = null;
        needToRestart = false;
        if ( startMonitors ) {
            // Watch for changes to the configuration file
            watchConfigFiles();
        }
    }

    /**
     * Returns the full-path filename associated with the given key in the given properties.
     * If the filename specified in the properties file is a relative pathname, the full-path
     * filename returned is that relative to the given base directory.
     *
     * @param configProps
     *         properties containing the filename associated with the given key
     * @param propKey
     *         key for the filename in the given properties
     * @param baseDir
     *         base directory to resolve relative filenames
     *
     * @return full-path filename; never null or empty
     *
     * @throws IllegalArgumentException
     *         if the properties file does not contain the given key, or
     *         if the value associated with the given key is empty
     */
    private static String getFilePathProperty(Properties configProps, String propKey, File baseDir)
            throws IllegalArgumentException {
        String fPath = configProps.getProperty(propKey);
        if ( fPath == null )
            throw new IllegalArgumentException("value not defined");
        String path = fPath.trim();
        if ( path.isEmpty() )
            throw new IllegalArgumentException("blank value");
        File propFile = new File(path);
        if ( path.equals(propFile.getAbsolutePath()) )
            return path;
        propFile = new File(baseDir, path);
        return propFile.getAbsolutePath();
    }

    /**
     * @param startMonitors
     *         start the file change monitors?
     *         ignored if the singleton instance of the DashboardConfigStore already exists
     *
     * @return the singleton instance of the DashboardConfigStore
     *
     * @throws IOException
     *         if unable to read the standard configuration file
     */
    public static DashboardConfigStore get(boolean startMonitors) throws IOException {
        synchronized(SINGLETON_SYNC_OBJECT) {
            if ( (singleton != null) && singleton.needToRestart ) {
                singleton.stopMonitors();
                singleton = null;
            }
            if ( singleton == null ) {
                singleton = new DashboardConfigStore(startMonitors);
            }
        }
        return singleton;
    }

    /**
     * Shuts down the handlers and monitors associated with the current singleton data store and removes it as the
     * singleton instance of this class.
     */
    public static void shutdown() {
        synchronized(SINGLETON_SYNC_OBJECT) {
            if ( singleton != null ) {
                // stop the handler and monitors for the singleton instance
                singleton.stopMonitors();
                // Discard this DashboardConfigStore as the singleton instance
                singleton = null;
            }
        }
    }

    /**
     * Shuts down the handlers and monitors associated with this data store.
     */
    private void stopMonitors() {
        // Shutdown all the VersionsedFileHandlers
        userFileHandler.shutdown();
        dataFileHandler.shutdown();
        metadataFileHandler.shutdown();
        archiveFilesBundler.shutdown();
        // Stop the configuration watcher
        cancelWatch();
    }

    /**
     * Monitors the configuration files for the current DashboardConfigStore singleton object.  If a configuration file
     * has changed, sets needsToRestart to true and the monitoring thread exits.
     */
    private void watchConfigFiles() {
        // Make sure the watcher is not already running
        if ( watcherThread != null )
            return;
        watcherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Create a new watch service for the dashboard configuration files
                try {
                    watcher = FileSystems.getDefault().newWatchService();
                } catch ( Exception ex ) {
                    itsLogger.error("Unexpected error starting a watcher for the default file system", ex);
                    return;
                }
                // Register the the directories containing the dashboard configuration files with the watch service
                HashSet<File> parentDirs = new HashSet<File>();
                for (File configFile : filesToWatch) {
                    parentDirs.add(configFile.getParentFile());
                }
                ArrayList<WatchKey> registrations = new ArrayList<WatchKey>(parentDirs.size());
                for (File watchDir : parentDirs) {
                    try {
                        registrations.add(watchDir.toPath().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY));
                    } catch ( Exception ex ) {
                        itsLogger.error("Unexpected error registering " + watchDir.getPath() + " for watching", ex);
                        for (WatchKey reg : registrations) {
                            reg.cancel();
                            reg.pollEvents();
                        }
                        try {
                            watcher.close();
                        } catch ( Exception e ) {
                            ;
                        }
                        watcher = null;
                        return;
                    }
                }
                for (; ; ) {
                    try {
                        WatchKey key = watcher.take();
                        Path parentPath = (Path) key.watchable();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            Path relPath = (Path) event.context();
                            File thisFile = parentPath.resolve(relPath).toFile();
                            if ( filesToWatch.contains(thisFile) ) {
                                needToRestart = true;
                                throw new Exception();
                            }
                        }
                        if ( !key.reset() )
                            break;
                    } catch ( Exception ex ) {
                        // Probably the watcher was closed
                        break;
                    }
                }
                for (WatchKey reg : registrations) {
                    reg.cancel();
                    reg.pollEvents();
                }
                try {
                    watcher.close();
                } catch ( Exception ex ) {
                    ;
                }
                watcher = null;
                return;
            }
        });
        itsLogger.info("Starting new thread monitoring the dashboard configuration files");
        watcherThread.start();
    }

    /**
     * Stops the monitoring the dashboard configuration files. If the dashboard configuration files are not being
     * monitored, this call does nothing.
     */
    private void cancelWatch() {
        try {
            watcher.close();
            // Only the thread modifies the value of watcher
        } catch ( Exception ex ) {
            // Might be NullPointerException
        }
        if ( watcherThread != null ) {
            try {
                watcherThread.join();
            } catch ( Exception ex ) {
                ;
            }
            watcherThread = null;
            itsLogger.info("End of thread monitoring the dashboard configuration files");
        }
    }

    /**
     * @return the version for uploaded data; never null
     */
    public String getUploadVersion() {
        return uploadVersion;
    }

    /**
     * @return the version for QC flagging; never null
     */
    public String getQCVersion() {
        return qcVersion;
    }

    /**
     * @return the handler for user data files
     */
    public UserFileHandler getUserFileHandler() {
        return userFileHandler;
    }

    /**
     * @return the handler for dataset data files
     */
    public DataFileHandler getDataFileHandler() {
        return dataFileHandler;
    }

    /**
     * @return the handler for automated data checker messages
     */
    public CheckerMessageHandler getCheckerMsgHandler() {
        return checkerMsgHandler;
    }

    /**
     * @return the handler for dataset metadata documents
     */
    public MetadataFileHandler getMetadataFileHandler() {
        return metadataFileHandler;
    }

    /**
     * @return the handler for name corrections
     */
    public SpellingHandler getSpellingHandler() {
        return spellingHandler;
    }

    /**
     * @return the handler for NetCDF DSG files
     */
    public DsgNcFileHandler getDsgNcFileHandler() {
        return dsgNcFileHandler;
    }

    /**
     * @return the Ferret configuration
     */
    public FerretConfig getFerretConfig() {
        return ferretConf;
    }

    /**
     * @return the database request handler
     */
    public DatabaseRequestHandler getDatabaseRequestHandler() {
        return databaseRequestHandler;
    }

    /**
     * @return the automated data checker
     */
    public DatasetChecker getDashboardDatasetChecker() {
        return datasetChecker;
    }

    /**
     * @return the preview plots handler
     */
    public PreviewPlotsHandler getPreviewPlotsHandler() {
        return plotsHandler;
    }

    /**
     * @return the submitter for dashboard datasets
     */
    public DatasetSubmitter getDashboardDatasetSubmitter() {
        return datasetSubmitter;
    }

    /**
     * @return the files bundler for archiving datasets
     */
    public ArchiveFilesBundler getArchiveFilesBundler() {
        return archiveFilesBundler;
    }

    /**
     * @return the OME XML to PDF generator
     */
    public OmePdfGenerator getOmePdfGenerator() {
        return omePdfGenerator;
    }

    /**
     * @return the known user data column types
     */
    public KnownDataTypes getKnownUserDataTypes() {
        return this.knownUserDataTypes;
    }

    /**
     * @return the known metadata types in DSG files
     */
    public KnownDataTypes getKnownMetadataTypes() {
        return this.knownMetadataTypes;
    }

    /**
     * @return the known data types in DSG files
     */
    public KnownDataTypes getKnownDataFileTypes() {
        return this.knownDataFileTypes;
    }

    /**
     * @return the maximum good and questionable calculated ship speed in knots.
     *         The values returned are the the maximum good and questionable values,
     *         if given, by the CALC_SPEED data type.  If not given, or if the
     *         upload dashboard has not been started, default values are returned.
     */
    public static double[] getMaxCalcSpeedsKnots() {
        double[] values = new double[2];
        synchronized(SINGLETON_SYNC_OBJECT) {
            if ( singleton != null ) {
                values[0] = singleton.maxGoodCalcSpeedKnots;
                values[1] = singleton.maxMaybeCalcSpeedKnots;
            }
            else {
                values[0] = DEFAULT_MAX_GOOD_CALC_SPEED_KNOTS;
                values[1] = DEFAULT_MAX_MAYBE_CALC_SPEED_KNOTS;
            }
        }
        return values;
    }

    /**
     * Validate a username from the user info map
     *
     * @param username
     *         username
     *
     * @return true if successful
     */
    public boolean validateUser(String username) {
        if ( (username == null) || username.isEmpty() )
            return false;
        String name = DashboardServerUtils.cleanUsername(username);
        DashboardUserInfo userInfo = userInfoMap.get(name);
        if ( userInfo == null )
            return false;
        return true;
    }


    /**
     * Determines if username has manager privilege over othername. This can be from username being an administrator,
     * a manager of a group othername belongs to, having the same username, or othername being invalid (most likely
     * an unspecified user), so long as username is an authorized user.
     *
     * @param username
     *         manager username to check; if not a valid user, returns false
     * @param othername
     *         group member username to check; if not a valid user, returns true as long as username is a valid user
     *
     * @return true if username is an authorized user and has manager privileges over othername
     */
    public boolean userManagesOver(String username, String othername) {
        DashboardUserInfo userInfo = userInfoMap.get(DashboardServerUtils.cleanUsername(username));
        if ( userInfo == null )
            return false;
        return userInfo.managesOver(userInfoMap.get(DashboardServerUtils.cleanUsername(othername)));
    }

    /**
     * @param username
     *         name of the user
     *
     * @return true is this user is an admin or a manager of a group,
     *         regardless of whether there is anyone else in the group
     */
    public boolean isManager(String username) {
        DashboardUserInfo userInfo = userInfoMap.get(DashboardServerUtils.cleanUsername(username));
        if ( userInfo == null )
            return false;
        return userInfo.isManager();
    }

    /**
     * @param username
     *         name of the user
     *
     * @return true is this user is an admin
     */
    public boolean isAdmin(String username) {
        DashboardUserInfo userInfo = userInfoMap.get(DashboardServerUtils.cleanUsername(username));
        if ( userInfo == null )
            return false;
        return userInfo.isAdmin();
    }

}
