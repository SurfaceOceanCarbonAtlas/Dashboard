/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.actions.CruiseChecker;
import gov.noaa.pmel.socat.dashboard.actions.CruiseSubmitter;
import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFlagsHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.OmeFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.PreviewPlotsHandler;
import gov.noaa.pmel.socat.dashboard.handlers.SocatFilesBundler;
import gov.noaa.pmel.socat.dashboard.handlers.UserFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import uk.ac.uea.socat.sanitychecker.config.BaseConfig;

import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.client.TripleDesCipher;

/**
 * Reads and holds the Dashboard configuration details
 * 
 * @author Karl Smith
 */
public class DashboardConfigStore {

	private static final String DEFAULT_SERVER_APP_NAME = "SocatUploadDashboard";
	private static final String ENCRYPTION_KEY_NAME_TAG = "EncryptionKey";
	private static final String ENCRYPTION_SALT_NAME_TAG = "EncryptionSalt";
	private static final String SOCAT_UPLOAD_VERSION_NAME_TAG = "SocatUploadVersion";
	private static final String SOCAT_QC_VERSION_NAME_TAG = "SocatQCVersion";
	private static final String SVN_USER_NAME_TAG = "SVNUsername";
	private static final String SVN_PASSWORD_NAME_TAG = "SVNPassword";
	private static final String USER_FILES_DIR_NAME_TAG = "UserFilesDir";
	private static final String CRUISE_FILES_DIR_NAME_TAG = "CruiseFilesDir";
	private static final String METADATA_FILES_DIR_NAME_TAG = "MetadataFilesDir";
	private static final String OME_SERVER_OUTPUT_DIR_NAME_TAG = "OmeServerOutputDir";
	private static final String DSG_NC_FILES_DIR_NAME_TAG = "DsgNcFilesDir";
	private static final String DEC_DSG_NC_FILES_DIR_NAME_TAG = "DecDsgNcFilesDir";
	private static final String ERDDAP_DSG_FLAG_FILE_NAME_TAG = "ErddapDsgFlagFile";
	private static final String ERDDAP_DEC_DSG_FLAG_FILE_NAME_TAG = "ErddapDecDsgFlagFile"; 
	private static final String FERRET_CONFIG_FILE_NAME_TAG = "FerretConfigFile";
	private static final String DATABASE_CONFIG_FILE_NAME_TAG = "DatabaseConfigFile";
	private static final String CDIAC_BUNDLES_DIR_NAME_TAG = "CDIACBundlesDir";
	private static final String CDIAC_BUNDLES_EMAIL_ADDRESS_TAG = "CDIACBundlesEmailAddress";
	private static final String SOCAT_BUNDLES_EMAIL_ADDRESS_TAG = "SocatBundlesEmailAddress";
	private static final String SOCAT_SMTP_HOST_ADDRESS_TAG = "SocatSMTPHostAddress";
	private static final String USER_ROLE_NAME_TAG_PREFIX = "RoleFor_";

	private static final String CONFIG_FILE_INFO_MSG = 
			"This configuration file should look something like: \n" +
			"# ------------------------------ \n" +
			ENCRYPTION_KEY_NAME_TAG + "=[ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, " +
					"13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 ] \n" +
			ENCRYPTION_SALT_NAME_TAG + "=SomeArbitraryStringOfCharacters \n" +
			SOCAT_UPLOAD_VERSION_NAME_TAG + "=SomeVersionNumber \n" +
			SOCAT_QC_VERSION_NAME_TAG + "=SomeVersionNumber \n" +
			SVN_USER_NAME_TAG + "=SVNUsername \n" +
			SVN_PASSWORD_NAME_TAG + "=SVNPasswork \n" +
			USER_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/User/Data \n" +
			CRUISE_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/Cruise/Data \n" +
			METADATA_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/Metadata/Docs \n" +
			OME_SERVER_OUTPUT_DIR_NAME_TAG + "=/Path/To/SocatOME/Guest/Output/Dir \n" +
			CDIAC_BUNDLES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/CDIAC/Bundles \n" + 
			CDIAC_BUNDLES_EMAIL_ADDRESS_TAG + "=cdiac.archiver@bundles.for.cdiac \n" +
			SOCAT_BUNDLES_EMAIL_ADDRESS_TAG + "=socat.support@bundles.from.socat \n" +
			SOCAT_SMTP_HOST_ADDRESS_TAG + "=smtp.server.for.socat \n" +
			DSG_NC_FILES_DIR_NAME_TAG + "=/Some/Plain/Dir/For/NetCDF/DSG/Files \n" +
			DEC_DSG_NC_FILES_DIR_NAME_TAG + "=/Some/Plain/Dir/For/NetCDF/Decimated/DSG/Files \n" +
			ERDDAP_DSG_FLAG_FILE_NAME_TAG + "=/Some/ERDDAP/Flag/Filename/For/DSG/Update \n" +
			ERDDAP_DEC_DSG_FLAG_FILE_NAME_TAG + "=/Some/ERDDAP/Flag/Filename/For/Decimated/DSG/Update \n" +
			FERRET_CONFIG_FILE_NAME_TAG + "=/Path/To/FerretConfig/XMLFile \n" +
			DATABASE_CONFIG_FILE_NAME_TAG + "=/Path/To/DatabaseConfig/PropsFile \n" + 
			BaseConfig.METADATA_CONFIG_FILE + "=/Path/To/MetadataConfig/CSVFile \n" + 
			BaseConfig.SOCAT_CONFIG_FILE + "=/Path/To/DataColumnConfig/CSVFile \n" + 
			BaseConfig.SANITY_CHECK_CONFIG_FILE + "/Path/To/SanityConfig/CSVFile \n" + 
			BaseConfig.COLUMN_SPEC_SCHEMA_FILE + "=/Path/To/ColumnSpecSchema/XMLFile \n" + 
			BaseConfig.COLUMN_CONVERSION_FILE + "=/Path/To/ColumnConversion/PropsFile \n" + 
			USER_ROLE_NAME_TAG_PREFIX + "SomeUserName=MemberOf1,MemberOf2 \n" +
			USER_ROLE_NAME_TAG_PREFIX + "SomeManagerName=ManagerOf1,MemberOf2 \n" +
			USER_ROLE_NAME_TAG_PREFIX + "SomeAdminName=Admin \n" +
			"# ------------------------------ \n" +
			"The EncryptionKey should be 24 random integer values in [-128,127] \n" +
			"The hexidecimal keys for users can be generated using the mkpasshash.sh script. \n";

	private static final Object SINGLETON_SYNC_OBJECT = new Object();
	private static DashboardConfigStore singleton = null;

	private TripleDesCipher cipher;
	private String encryptionSalt;
	// Map of username to user info
	private HashMap<String,DashboardUserInfo> userInfoMap;
	private String socatUploadVersion;
	private String socatQCVersion;
	private UserFileHandler userFileHandler;
	private CruiseFileHandler cruiseFileHandler;
	private CheckerMessageHandler checkerMsgHandler;
	private MetadataFileHandler metadataFileHandler;
	private OmeFileHandler omeFileHandler;
	private SocatFilesBundler cdiacFilesBundler;
	private DsgNcFileHandler dsgNcFileHandler;
	private FerretConfig ferretConf;
	private CruiseChecker cruiseChecker;
	private DatabaseRequestHandler databaseRequestHandler;
	private PreviewPlotsHandler plotsHandler;
	private CruiseSubmitter cruiseSubmitter;
	private CruiseFlagsHandler cruiseFlagsHandler;
	private HashSet<File> filesToWatch;
	private Thread watcherThread;
	private WatchService watcher;
	private boolean needToRestart;
	private Logger itsLogger;

	/**
	 * Creates a data store initialized from the contents of the standard 
	 * configuration file.  See the contents of {@link #CONFIG_FILE_INFO_MSG} 
	 * for information on the configuration file format.
	 * 
	 * Do not create an instance of this class; 
	 * instead use {@link #get()} to retrieve the singleton instance
	 * 
	 * @param startMonitors
	 * 		start the file change monitors? 
	 * @throws IOException 
	 * 		if unable to read the standard configuration file
	 */
	private DashboardConfigStore(boolean startMonitors) throws IOException {
		String baseDir = System.getenv("CATALINA_BASE");
		if ( baseDir == null ) 
			throw new IOException("CATALINA_BASE environment variable is not defined");
		baseDir += File.separator;

		// The following is just for running the dashboard.programs.* apps with a different configuration
		String serverAppName = System.getenv("UPLOAD_DASHBOARD_SERVER_NAME");
		if ( serverAppName == null )
			serverAppName = DEFAULT_SERVER_APP_NAME;
		String contentAppDir = baseDir + "content" + File.separator + serverAppName + File.separator;
		String previewDirname = baseDir + "webapps" + File.separator + serverAppName + File.separator + 
				"preview" + File.separator;

		// Configure the log4j logger
		PropertyConfigurator.configure(contentAppDir + "log4j.properties");
		itsLogger = Logger.getLogger(serverAppName);

		// Record configuration files that should be monitored for changes 
		filesToWatch = new HashSet<File>();

		// Read the properties from the standard configuration file
		Properties configProps = new Properties();
		File configFile = new File(contentAppDir + serverAppName + ".properties");
		filesToWatch.add(configFile);
		FileReader reader;
		try {
			reader = new FileReader(configFile);
			try {
				configProps.load(reader);
			} finally {
				reader.close();
			}
		}
		catch ( Exception ex ) {
			throw new IOException("Problems reading " + configFile.getPath() +
					"\n" + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		String propVal;

		// Read the encryption key from the data store and initialize the cipher with it
		try {
			propVal = configProps.getProperty(ENCRYPTION_KEY_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			byte[] encryptionKey = DashboardUtils.decodeByteArray(propVal.trim());
			if ( (encryptionKey.length < 16) || (encryptionKey.length > 24) )
				throw new IllegalArgumentException(
						"array must have 16 to 24 values");
			cipher = new TripleDesCipher();
			cipher.setKey(encryptionKey);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + ENCRYPTION_KEY_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}

		// Read the salt string from the data store
		try {
			propVal = configProps.getProperty(ENCRYPTION_SALT_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			if ( propVal.length() < 16 )
				throw new IllegalArgumentException(
						"string must have 16 or more characters");
			encryptionSalt = propVal;
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + ENCRYPTION_SALT_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}

		// Read the SOCAT versions
		try {
			propVal = configProps.getProperty(SOCAT_UPLOAD_VERSION_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			if ( propVal.isEmpty() )
				throw new IllegalArgumentException("blank value");
			socatUploadVersion = propVal;
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + SOCAT_UPLOAD_VERSION_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		try {
			propVal = configProps.getProperty(SOCAT_QC_VERSION_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			if ( propVal.isEmpty() )
				throw new IllegalArgumentException("blank value");
			socatQCVersion = propVal;
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + SOCAT_QC_VERSION_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
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
			throw new IOException("Invalid " + SVN_USER_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}

		// Read the SVN password; can be blank or not given
		String svnPassword = "";
		propVal = configProps.getProperty(SVN_PASSWORD_NAME_TAG);
		if ( propVal != null )
			svnPassword = propVal.trim();

		// Read the user files directory name
		try {
			propVal = configProps.getProperty(USER_FILES_DIR_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			userFileHandler = new UserFileHandler(propVal, svnUsername, svnPassword);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + USER_FILES_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}

		// Read the cruise files directory name
		try {
			propVal = configProps.getProperty(CRUISE_FILES_DIR_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			cruiseFileHandler = new CruiseFileHandler(propVal, svnUsername, svnPassword);
			// Put SanityChecker message files in the same directory
			checkerMsgHandler = new CheckerMessageHandler(propVal);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + CRUISE_FILES_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}

		// Read the metadata files directory name
		try {
			propVal = configProps.getProperty(METADATA_FILES_DIR_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			metadataFileHandler = new MetadataFileHandler(propVal, svnUsername, svnPassword);
			// Put the flag messages file in the same directory
			cruiseFlagsHandler = new CruiseFlagsHandler(propVal);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + METADATA_FILES_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}

		// Read the CDIAC email address to send archival bundles
		String cdiacEmailAddress;
		try {
			propVal = configProps.getProperty(CDIAC_BUNDLES_EMAIL_ADDRESS_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			cdiacEmailAddress = propVal;
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + CDIAC_BUNDLES_EMAIL_ADDRESS_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		// Read the SOCAT email address for the from address for archival bundles
		String socatEmailAddress;
		try {
			propVal = configProps.getProperty(SOCAT_BUNDLES_EMAIL_ADDRESS_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			socatEmailAddress = propVal;
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + SOCAT_BUNDLES_EMAIL_ADDRESS_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		// Read the SOCAT SMTP server host name/address
		String smtpHostAddress;
		try {
			propVal = configProps.getProperty(SOCAT_SMTP_HOST_ADDRESS_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			smtpHostAddress = propVal;
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + SOCAT_SMTP_HOST_ADDRESS_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		// Read the CDIAC bundles directory name and create the CDIAC archival bundler
		try {
			propVal = configProps.getProperty(CDIAC_BUNDLES_DIR_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			cdiacFilesBundler = new SocatFilesBundler(propVal, svnUsername, 
					svnPassword, cdiacEmailAddress, socatEmailAddress, smtpHostAddress);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + CDIAC_BUNDLES_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}

		// Read the OME server guest output directory name
		try {
			propVal = configProps.getProperty(OME_SERVER_OUTPUT_DIR_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			omeFileHandler = new OmeFileHandler(propVal, metadataFileHandler, 
										cruiseFileHandler, socatUploadVersion);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + OME_SERVER_OUTPUT_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}

		// Read the Ferret configuration filename
		try {
			propVal = configProps.getProperty(FERRET_CONFIG_FILE_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			// Read the Ferret configuration given in this file
			File ferretPropsFile = new File(propVal);
			filesToWatch.add(ferretPropsFile);
		    InputStream stream = new FileInputStream(ferretPropsFile);
		    try {
			    SAXBuilder sb = new SAXBuilder();
		    	Document jdom = sb.build(stream);
		    	ferretConf = new FerretConfig();
		    	ferretConf.setRootElement((Element)jdom.getRootElement().clone());
		    } finally {
		    	stream.close();
		    }
		    itsLogger.info("read Ferret configuration file " + propVal);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + FERRET_CONFIG_FILE_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}

		// Read the DSG files directory names and ERDDAP flag file names
		String dsgFileDirName;
		try {
			dsgFileDirName = configProps.getProperty(DSG_NC_FILES_DIR_NAME_TAG);
			if ( dsgFileDirName == null )
				throw new IllegalArgumentException(DSG_NC_FILES_DIR_NAME_TAG + " not defined");
			dsgFileDirName = dsgFileDirName.trim();
		    itsLogger.info("DSG directory = " + dsgFileDirName);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + DSG_NC_FILES_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		String decDsgFileDirName;
		try {
			decDsgFileDirName = configProps.getProperty(DEC_DSG_NC_FILES_DIR_NAME_TAG);
			if ( decDsgFileDirName == null )
				throw new IllegalArgumentException(DEC_DSG_NC_FILES_DIR_NAME_TAG + " not defined");
			decDsgFileDirName = decDsgFileDirName.trim();
		    itsLogger.info("Decimated DSG directory = " + decDsgFileDirName);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + DEC_DSG_NC_FILES_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		String erddapDsgFlagFileName;
		try {
			erddapDsgFlagFileName = configProps.getProperty(ERDDAP_DSG_FLAG_FILE_NAME_TAG);
			if ( erddapDsgFlagFileName == null )
				throw new IllegalArgumentException("value not defined");
			erddapDsgFlagFileName = erddapDsgFlagFileName.trim();
		    itsLogger.info("ERDDAP DSG flag file = " + erddapDsgFlagFileName);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + ERDDAP_DSG_FLAG_FILE_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		String erddapDecDsgFlagFileName;
		try {
			erddapDecDsgFlagFileName = configProps.getProperty(ERDDAP_DEC_DSG_FLAG_FILE_NAME_TAG);
			if ( erddapDecDsgFlagFileName == null )
				throw new IllegalArgumentException("value not defined");
			erddapDecDsgFlagFileName = erddapDecDsgFlagFileName.trim();
		    itsLogger.info("ERDDAP decimated DSG flag file = " + erddapDecDsgFlagFileName);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + ERDDAP_DEC_DSG_FLAG_FILE_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		try {
			dsgNcFileHandler = new DsgNcFileHandler(dsgFileDirName, decDsgFileDirName,
					erddapDsgFlagFileName, erddapDecDsgFlagFileName, ferretConf);
		} catch ( Exception ex ) {
			throw new IOException(ex);
		}

		// Read the Database configuration filename
		try {
			propVal = configProps.getProperty(DATABASE_CONFIG_FILE_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			filesToWatch.add(new File(propVal));
			databaseRequestHandler = new DatabaseRequestHandler(propVal);
		    itsLogger.info("read Database configuration file " + propVal);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + DATABASE_CONFIG_FILE_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}

		// SanityChecker initialization from this same properties file 
		try {
			cruiseChecker = new CruiseChecker(configFile, checkerMsgHandler, metadataFileHandler);
		} catch ( IOException ex ) {
			throw new IOException(ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}

		// Add the SanityChecker configuration files listed in this config file to the files to be watched
		try {
			filesToWatch.add(new File(configProps.getProperty(BaseConfig.METADATA_CONFIG_FILE)));
		} catch ( Exception ex ) {
			// Should not happen but ignore if it did
		}
		try {
			filesToWatch.add(new File(configProps.getProperty(BaseConfig.SOCAT_CONFIG_FILE)));
		} catch ( Exception ex ) {
			// Should not happen but ignore if it did
		}
		try {
			filesToWatch.add(new File(configProps.getProperty(BaseConfig.SANITY_CHECK_CONFIG_FILE)));
		} catch ( Exception ex ) {
			// Should not happen but ignore if it did
		}
		try {
			filesToWatch.add(new File(configProps.getProperty(BaseConfig.COLUMN_SPEC_SCHEMA_FILE)));
		} catch ( Exception ex ) {
			// Should not happen but ignore if it did
		}
		try {
			propVal = configProps.getProperty(BaseConfig.COLUMN_CONVERSION_FILE);
			filesToWatch.add(new File(propVal));
		} catch ( Exception ex ) {
			// Should not happen but ignore if it did
		}

		// The PreviewPlotsHandler uses the various handlers just created
		plotsHandler = new PreviewPlotsHandler(previewDirname + "dsgfiles", 
				previewDirname + "plots", this);

		// The CruiseSubmitter uses the various handlers just created
		cruiseSubmitter = new CruiseSubmitter(this);

		// Read and assign the authorized users 
		userInfoMap = new HashMap<String,DashboardUserInfo>();
		for ( Entry<Object,Object> entry : configProps.entrySet() ) {
			if ( ! ((entry.getKey() instanceof String) && 
					(entry.getValue() instanceof String)) )
				continue;
			String username = (String) entry.getKey();
			if ( ! username.startsWith(USER_ROLE_NAME_TAG_PREFIX) )
				continue;
			username = username.substring(USER_ROLE_NAME_TAG_PREFIX.length());
			username = DashboardUtils.cleanUsername(username);
			DashboardUserInfo userInfo;
			try {
				userInfo = new DashboardUserInfo(username);
			} catch ( IllegalArgumentException ex ) {
				throw new IOException(ex.getMessage() + "\n" +
						"for " + username + " specified in " + 
						configFile.getPath() + "\n" + CONFIG_FILE_INFO_MSG);
			}
			String rolesString = (String) entry.getValue();
			try {
				userInfo.addUserRoles(rolesString);
			} catch ( IllegalArgumentException ex ) {
				throw new IOException(ex.getMessage() + "\n" +
						"for " + username + " specified in " + 
						configFile.getPath() + "\n" + CONFIG_FILE_INFO_MSG);
			}
			userInfoMap.put(username, userInfo);
		}
		itsLogger.info("read configuration file " + configFile.getPath());
		watcher = null;
		watcherThread = null;
		needToRestart = false;
		if ( startMonitors ) {
			// Watch for changes to the configuration file
			watchConfigFiles();
			// Watch for OME server XML output files
			omeFileHandler.watchForOmeOutput();
			// Watch for changes in the full-data DSG files
			dsgNcFileHandler.watchForDsgFileUpdates();
		}
	}

	/**
	 * @param startMonitors
	 * 		start the file change monitors? 
	 * 		(ignored if the singleton instance of the DashboardConfigStore already exists)
	 * @return
	 * 		the singleton instance of the DashboardConfigStore
	 * @throws IOException 
	 * 		if unable to read the standard configuration file
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
	 * Shuts down the handlers and monitors associated with the current singleton 
	 * data store and removes it as the singleton instance of this class.
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
		// Stop the watch for changes in the full-data DSG files
		dsgNcFileHandler.cancelWatch();
		// Stop the watch for the OME server XML output files
		omeFileHandler.cancelWatch();
		// Shutdown all the VersionsedFileHandlers
		userFileHandler.shutdown();
		cruiseFileHandler.shutdown();
		metadataFileHandler.shutdown();
		cdiacFilesBundler.shutdown();
		// Stop the configuration watcher
		cancelWatch();
	}

	/**
	 * Monitors the configuration files for the current DashboardConfigStore 
	 * singleton object.  If a configuration file has changed, sets 
	 * needsToRestart to true and the monitoring thread exits.
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
				} catch (Exception ex) {
					itsLogger.error("Unexpected error starting a watcher for the default file system", ex);
					return;
				}
				// Register the the directories containing the dashboard configuration files with the watch service
				HashSet<File> parentDirs = new HashSet<File>();
				for ( File configFile : filesToWatch ) {
					parentDirs.add(configFile.getParentFile());
				}
				ArrayList<WatchKey> registrations = new ArrayList<WatchKey>(parentDirs.size());
				for ( File watchDir : parentDirs ) {
					try {
						registrations.add(watchDir.toPath().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY));
					} catch (Exception ex) {
						itsLogger.error("Unexpected error registering " + watchDir.getPath() + " for watching", ex);
						for ( WatchKey reg : registrations ) {
							reg.cancel();
							reg.pollEvents();
						}
						try {
							watcher.close();
						} catch (Exception e) {
							;
						}
						watcher = null;
						return;
					}
				}
				for (;;) {
					try {
						WatchKey key = watcher.take();
						Path parentPath = (Path) key.watchable();
						for ( WatchEvent<?> event : key.pollEvents() ) {
							Path relPath = (Path) event.context();
							File thisFile = parentPath.resolve(relPath).toFile();
							if ( filesToWatch.contains(thisFile) ) {
								needToRestart = true;
								throw new Exception();
							}
						}
						if ( ! key.reset() )
							break;
					} catch (Exception ex) {
						// Probably the watcher was closed
						break;
					}
				}
				for ( WatchKey reg : registrations ) {
					reg.cancel();
					reg.pollEvents();
				}
				try {
					watcher.close();
				} catch (Exception ex) {
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
	 * Stops the monitoring the dashboard configuration files.  
	 * If the dashboard configuration files are not being monitored, this call does nothing. 
	 */
	private void cancelWatch() {
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
			itsLogger.info("End of thread monitoring the dashboard configuration files");
		}
	}

	/**
	 * @return
	 * 		the SOCAT version for uploaded data; never null
	 */
	public String getSocatUploadVersion() {
		return socatUploadVersion;
	}

	/**
	 * @return
	 * 		the SOCAT version for QC flagging; never null
	 */
	public String getSocatQCVersion() {
		return socatQCVersion;
	}

	/**
	 * @return 
	 * 		the handler for user data files
	 */
	public UserFileHandler getUserFileHandler() {
		return userFileHandler;
	}

	/**
	 * @return 
	 * 		the handler for cruise data files
	 */
	public CruiseFileHandler getCruiseFileHandler() {
		return cruiseFileHandler;
	}

	public CruiseFlagsHandler getCruiseFlagsHandler() {
		return cruiseFlagsHandler;
	}
	/**
	 * @return
	 * 		the handler for SanityChecker messages
	 */
	public CheckerMessageHandler getCheckerMsgHandler() {
		return checkerMsgHandler;
	}

	/**
	 * @return
	 * 		the handler for cruise metadata documents
	 */
	public MetadataFileHandler getMetadataFileHandler() {
		return metadataFileHandler;
	}

	/**
	 * @return
	 * 		the handler for NetCDF DSG files
	 */
	public DsgNcFileHandler getDsgNcFileHandler() {
		return dsgNcFileHandler;
	}

	/**
	 * @return
	 * 		the Ferret configuration
	 */
	public FerretConfig getFerretConfig() {
		return ferretConf;
	}

	/**
	 * @return
	 * 		the database request handler
	 */
	public DatabaseRequestHandler getDatabaseRequestHandler() {
		return databaseRequestHandler;
	}

	/**
	 * @return
	 * 		the checker for cruise data and metadata
	 */
	public CruiseChecker getDashboardCruiseChecker() {
		return cruiseChecker;
	}

	/**
	 * @return
	 * 		the preview plots handler
	 */
	public PreviewPlotsHandler getPreviewPlotsHandler() {
		return plotsHandler;
	}

	/**
	 * @return
	 * 		the submitter for dashboard cruises
	 */
	public CruiseSubmitter getDashboardCruiseSubmitter() {
		return cruiseSubmitter;
	}

	/**
	 * @return
	 * 		the files bundler for "send to CDIAC" datasets
	 */
	public SocatFilesBundler getCdiacFilesBundler() {
		return cdiacFilesBundler;
	}

	/**
	 * Validate a username from the user info map
	 *  
	 * @param username
	 * 		username
	 * @return
	 * 		true if successful
	 */
	public boolean validateUser(String username) {
		if ( (username == null) || username.isEmpty() )
			return false;
		String name = DashboardUtils.cleanUsername(username);
		DashboardUserInfo userInfo = userInfoMap.get(name);
		if ( userInfo == null )
			return false;
		return true;
	}


	/**
	 * Determines if username has manager privilege over othername. 
	 * This can be from username being an administrator, a manager
	 * of a group othername belongs to, having the same username,
	 * or othername being invalid (most likely an unspecified user),
	 * so long as username is an authorized user.
	 * 
	 * @param username
	 * 		manager username to check; if not a valid user, returns false
	 * @param othername
	 * 		group member username to check; if not a valid user, 
	 * 		returns true if username is a valid user
	 * @return
	 * 		true if username is an authorized user and has manager
	 * 		privileges over othername
	 */
	public boolean userManagesOver(String username, String othername) {
		DashboardUserInfo userInfo = userInfoMap.get(DashboardUtils.cleanUsername(username));
		if ( userInfo == null )
			return false;
		return userInfo.managesOver(userInfoMap.get(DashboardUtils.cleanUsername(othername)));
	}

	/**
	 * @param username
	 * 		name of the user
	 * @return
	 * 		true is this user is an admin or a manager of a group
	 * 		(regardless of whether there is anyone else in the group)
	 */
	public boolean isManager(String username) {
		DashboardUserInfo userInfo = userInfoMap.get(DashboardUtils.cleanUsername(username));
		if ( userInfo == null )
			return false;
		return userInfo.isManager();
	}

	/**
	 * @param username
	 * 		name of the user
	 * @return
	 * 		true is this user is an admin
	 */
	public boolean isAdmin(String username) {
		DashboardUserInfo userInfo = userInfoMap.get(DashboardUtils.cleanUsername(username));
		if ( userInfo == null )
			return false;
		return userInfo.isAdmin();
	}

	/**
	 * Generates an further encrypted password hash 
	 * from the given username and initially encrypted password
	 * @param username
	 * 		username to use
	 * @param passhash
	 * 		initially encrypted password to use
	 * @return
	 * 		further encrypted password, or an empty string on failure
	 */
	public String spicedHash(String username, String passhash) {
		String passSpicedHash;
		try {
			passSpicedHash = cipher.encrypt(passhash + encryptionSalt);
		} catch (DataLengthException | IllegalStateException
				| InvalidCipherTextException ex) {
			return "";
		}
		return DashboardUtils.passhashFromPlainText(username, passSpicedHash);
	}

}
