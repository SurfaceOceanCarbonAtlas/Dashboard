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
import gov.noaa.pmel.socat.dashboard.handlers.UserFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

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

	private static final String SERVER_APP_NAME = "SocatUploadDashboard";
	private static final String LOGGER_CONFIG_RELATIVE_FILENAME = "content" + File.separator + 
			SERVER_APP_NAME + File.separator + "log4j.properties";
	private static final String CONFIG_RELATIVE_FILENAME = "content" + File.separator + 
			SERVER_APP_NAME + File.separator + "SocatUploadDashboard.properties";
	private static final String PREVIEW_RELATIVE_DIRNAME = "webapps" + File.separator + 
			SERVER_APP_NAME + File.separator + "preview" + File.separator;
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
			OME_SERVER_OUTPUT_DIR_NAME_TAG + "=/Path/To/SocatOME/guest/output/dir \n" +
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

	private static final AtomicReference<DashboardConfigStore> singleton = 
			new AtomicReference<DashboardConfigStore>();

	private File configFile;
	private long configFileTimestamp;
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
	private DsgNcFileHandler dsgNcFileHandler;
	private FerretConfig ferretConf;
	private CruiseChecker cruiseChecker;
	private DatabaseRequestHandler databaseRequestHandler;
	private PreviewPlotsHandler plotsHandler;
	private CruiseSubmitter cruiseSubmitter;
	private CruiseFlagsHandler cruiseFlagsHandler;
	private Timer configWatcher;

	/**
	 * Creates a data store initialized from the contents of the standard 
	 * configuration file.  See the contents of {@link #CONFIG_FILE_INFO_MSG} 
	 * for information on the configuration file format.
	 * 
	 * Do not create an instance of this class; 
	 * instead use {@link #get()} to retrieve the singleton instance
	 * 
	 * @throws IOException 
	 * 		if unable to read the standard configuration file
	 */
	private DashboardConfigStore() throws IOException {
		String baseDir = System.getProperty("catalina.base");
		// The following is just for debugging under Eclipse
		if ( baseDir == null ) 
			baseDir = System.getProperty("user.home");

		// Configure the log4j logger
		PropertyConfigurator.configure(baseDir + File.separator + 
				LOGGER_CONFIG_RELATIVE_FILENAME);
		Logger itsLogger = Logger.getLogger(SERVER_APP_NAME);

		// Read the properties from the standard configuration file
		Properties configProps = new Properties();
		configFile = new File(baseDir, CONFIG_RELATIVE_FILENAME);
		configFileTimestamp = configFile.lastModified();
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
			userFileHandler = new UserFileHandler(propVal, 
					svnUsername, svnPassword);
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
			cruiseFileHandler = new CruiseFileHandler(propVal,
					svnUsername, svnPassword);
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
			metadataFileHandler = new MetadataFileHandler(propVal,
					svnUsername, svnPassword);
			// Put the flag messages file in the same directory
			cruiseFlagsHandler = new CruiseFlagsHandler(propVal);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + METADATA_FILES_DIR_NAME_TAG + 
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
		    InputStream stream = new FileInputStream(new File(propVal));
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

		// The PreviewPlotsHandler uses the various handlers just created
		String previewDirname = baseDir + File.separator + PREVIEW_RELATIVE_DIRNAME;
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
		// Watch for changes to the configuration file
		watchConfigFile();
		// Watch for OME server XML output files
		omeFileHandler.watchForOmeOutput();
		// Watch for changes in the full-data DSG files
		dsgNcFileHandler.watchForDsgFileUpdates();
	}

	/**
	 * @return
	 * 		the singleton instance of the DashboardConfigStore
	 * @throws IOException 
	 * 		if unable to read the standard configuration file
	 */
	public static DashboardConfigStore get() throws IOException {
		if ( singleton.get() == null )
			singleton.compareAndSet(null, new DashboardConfigStore());
		return singleton.get();
	}

	/**
	 * Shuts down the handlers and timers associated with this data store and 
	 * removes this data store as the singleton instance of this class.
	 */
	public void shutdown() {
		// Stop the watch for changes in the full-data DSG files
		dsgNcFileHandler.cancelWatch();
		// Stop the watch for the OME server XML output files
		omeFileHandler.cancelWatch();
		// Shutdown all the VersionsedFileHandlers
		userFileHandler.shutdown();
		cruiseFileHandler.shutdown();
		metadataFileHandler.shutdown();
		// Stop the configuration watcher
		configWatcher.cancel();
		// Discard this DashboardConfigStore as the singleton instance
		singleton.set(null);
	}

	private static final long MINUTES_CHECK_INTERVAL = 1;
	/**
	 * Monitors the configuration file creating the current DashboardConfigStore 
	 * singleton object.  If the configuration file has changed, shuts down the 
	 * current DashboardConfigStore singleton object and stops monitoring the 
	 * configuration file.  Thus, the next time the DashboardConfigStore is needed, 
	 * the configuration file will be reread and this monitor will be restarted.
	 */
	private void watchConfigFile() {
		// Just create a timer to monitor the last modified timestamp 
		// of the config file every MINUTES_CHECK_INTERVAL minutes
		configWatcher = new Timer();
		configWatcher.schedule(new TimerTask() {
			@Override
			public void run() {
				DashboardConfigStore configStore = singleton.get();
				if ( configStore == null ) {
					// datastore already removed so cancel this timer
					cancel(); 
				}
				else if ( configStore.configFile.lastModified() != 
						  configStore.configFileTimestamp ) {
					// Shutdown all the handlers, cancel this timer, and remove the configstore
					configStore.shutdown();
				}
			}
		}, MINUTES_CHECK_INTERVAL * 60 * 1000, MINUTES_CHECK_INTERVAL * 60 * 1000);
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
		DashboardUserInfo userInfo = userInfoMap.get(
				DashboardUtils.cleanUsername(username));
		if ( userInfo == null )
			return false;
		return userInfo.managesOver(userInfoMap.get(
				DashboardUtils.cleanUsername(othername)));
	}

	/**
	 * @param username
	 * 		name of the user
	 * @return
	 * 		true is this user is an admin or a manager of a group
	 * 		(regardless of whether there is anyone else in the group)
	 */
	public boolean isManager(String username) {
		DashboardUserInfo userInfo = userInfoMap.get(
				DashboardUtils.cleanUsername(username));
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
		DashboardUserInfo userInfo = userInfoMap.get(
				DashboardUtils.cleanUsername(username));
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
