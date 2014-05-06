/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.nc.DsgNcFileHandler;
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
 * Reads and holds the data in the Dashboard configuration file
 * 
 * @author Karl Smith
 */
public class DashboardDataStore {

	private static final String SERVER_APP_NAME = "SocatUploadDashboard";
	private static final String LOGGER_CONFIG_RELATIVE_FILENAME = "content" +
			File.separator + SERVER_APP_NAME + File.separator + 
			"log4j.properties";
	private static final String CONFIG_RELATIVE_FILENAME = "content" + 
			File.separator + SERVER_APP_NAME + File.separator + 
			"SocatUploadDashboard.properties";
	private static final String ENCRYPTION_KEY_NAME_TAG = "EncryptionKey";
	private static final String ENCRYPTION_SALT_NAME_TAG = "EncryptionSalt";
	private static final String SOCAT_VERSION_NAME_TAG = "SocatVersion";
	private static final String SVN_USER_NAME_TAG = "SVNUsername";
	private static final String SVN_PASSWORD_NAME_TAG = "SVNPassword";
	private static final String USER_FILES_DIR_NAME_TAG = "UserFilesDir";
	private static final String CRUISE_FILES_DIR_NAME_TAG = "CruiseFilesDir";
	private static final String METADATA_FILES_DIR_NAME_TAG = "MetadataFilesDir";
	private static final String DSG_NC_FILES_DIR_NAME_TAG = "DsgNcFilesDir";
	private static final String FERRET_CONFIG_FILE_NAME_TAG = "FerretConfigFile";
	private static final String DATABASE_CONFIG_FILE_NAME_TAG = "DatabaseConfigFile";
	private static final String AUTHENTICATION_NAME_TAG_PREFIX = "HashFor_";
	private static final String USER_ROLE_NAME_TAG_PREFIX = "RoleFor_";

	private static final String CONFIG_FILE_INFO_MSG = 
			"This configuration file should look something like: \n" +
			"# ------------------------------ \n" +
			ENCRYPTION_KEY_NAME_TAG + "=[ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, " +
					"13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 ] \n" +
			ENCRYPTION_SALT_NAME_TAG + "=SomeArbitraryStringOfCharacters \n" +
			SOCAT_VERSION_NAME_TAG + "=SomeVersionNumber \n" +
			SVN_USER_NAME_TAG + "=SVNUsername \n" +
			SVN_PASSWORD_NAME_TAG + "=SVNPasswork \n" +
			USER_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/User/Data \n" +
			CRUISE_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/Cruise/Data \n" +
			METADATA_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/Metadata/Docs \n" +
			DSG_NC_FILES_DIR_NAME_TAG + "=/Some/Plain/Dir/For/NetCDF/DSG/Files \n" +
			FERRET_CONFIG_FILE_NAME_TAG + "=/Path/To/FerretConfig/XMLFile \n" +
			DATABASE_CONFIG_FILE_NAME_TAG + "=/Path/To/DatabaseConfig/PropsFile \n" + 
			BaseConfig.METADATA_CONFIG_FILE + "=/Path/To/MetadataConfig/CSVFile \n" + 
			BaseConfig.SOCAT_CONFIG_FILE + "=/Path/To/DataColumnConfig/CSVFile \n" + 
			BaseConfig.SANITY_CHECK_CONFIG_FILE + "/Path/To/SanityConfig/CSVFile \n" + 
			BaseConfig.COLUMN_SPEC_SCHEMA_FILE + "=/Path/To/ColumnSpecSchema/XMLFile \n" + 
			BaseConfig.COLUMN_CONVERSION_FILE + "=/Path/To/ColumnConversion/PropsFile \n" + 
			AUTHENTICATION_NAME_TAG_PREFIX + "SomeUserName=AVeryLongKeyOfHexidecimalValues \n" +
			USER_ROLE_NAME_TAG_PREFIX + "SomeUserName=MemberOf1,MemberOf2 \n" +
			AUTHENTICATION_NAME_TAG_PREFIX + "SomeManagerName=AnotherVeryLongKeyOfHexidecimalValues \n" +
			USER_ROLE_NAME_TAG_PREFIX + "SomeManagerName=ManagerOf1,MemberOf2 \n" +
			AUTHENTICATION_NAME_TAG_PREFIX + "SomeAdminName=YetAnotherVeryLongKeyOfHexidecimalValues \n" +
			USER_ROLE_NAME_TAG_PREFIX + "SomeAdminName=Admin \n" +
			"# ------------------------------ \n" +
			"The EncryptionKey should be 24 random integer values in [-128,127] \n" +
			"The hexidecimal keys for users can be generated using the mkpasshash.sh script. \n";

	private static final AtomicReference<DashboardDataStore> singleton = 
			new AtomicReference<DashboardDataStore>();

	private File configFile;
	private long configFileTimestamp;
	private TripleDesCipher cipher;
	private String encryptionSalt;
	// Map of username to user info
	private HashMap<String,DashboardUserInfo> userInfoMap;
	private Double socatVersion;
	private UserFileHandler userFileHandler;
	private CruiseFileHandler cruiseFileHandler;
	private MetadataFileHandler metadataFileHandler;
	private DsgNcFileHandler dsgNcFileHandler;
	private FerretConfig ferretConf;
	private DashboardCruiseChecker cruiseChecker;
	private DatabaseRequestHandler databaseRequestHandler;

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
	private DashboardDataStore() throws IOException {
		String baseDir = System.getProperty("catalina.base");
		// The following is just for debugging under Eclipse
		if ( baseDir == null ) 
			baseDir = System.getProperty("user.home");
		Logger itsLogger = Logger.getLogger(SERVER_APP_NAME);
		// Configure the log4j logger
		PropertyConfigurator.configure(baseDir + File.separator + 
				LOGGER_CONFIG_RELATIVE_FILENAME);
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
		// Read the SOCAT version
		try {
			propVal = configProps.getProperty(SOCAT_VERSION_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			if ( propVal.isEmpty() )
				throw new IllegalArgumentException("blank value");
			try {
				socatVersion = Double.valueOf(propVal);
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException(ex);
			}
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + SOCAT_VERSION_NAME_TAG + 
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
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + METADATA_FILES_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + "\n" + 
					ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		// Read the DSG NC files directory name
		try {
			propVal = configProps.getProperty(DSG_NC_FILES_DIR_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			dsgNcFileHandler = new DsgNcFileHandler(propVal);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + DSG_NC_FILES_DIR_NAME_TAG + 
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
		// Sanity checker initialization from this same properties file 
		try {
			cruiseChecker = new DashboardCruiseChecker(configFile);
		} catch ( IOException ex ) {
			throw new IOException(ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		// Read and assign the authorized users 
		userInfoMap = new HashMap<String,DashboardUserInfo>();
		for ( Entry<Object,Object> entry : configProps.entrySet() ) {
			if ( ! ((entry.getKey() instanceof String) && 
					(entry.getValue() instanceof String)) )
				continue;
			String username = (String) entry.getKey();
			if ( ! username.startsWith(AUTHENTICATION_NAME_TAG_PREFIX) )
				continue;
			username = username.substring(AUTHENTICATION_NAME_TAG_PREFIX.length());
			username = DashboardUtils.cleanUsername(username);
			String hash = (String) entry.getValue();
			DashboardUserInfo userInfo;
			try {
				userInfo = new DashboardUserInfo(username, hash);
			} catch ( IllegalArgumentException ex ) {
				throw new IOException(ex.getMessage() + "\n" +
						"for " + username + " specified in " + 
						configFile.getPath() + "\n" + CONFIG_FILE_INFO_MSG);
			}
			userInfoMap.put(username, userInfo);
		}
		// Read and assign the authorized user roles 
		for ( Entry<Object,Object> entry : configProps.entrySet() ) {
			if ( ! ((entry.getKey() instanceof String) && 
					(entry.getValue() instanceof String)) )
				continue;
			String username = (String) entry.getKey();
			if ( ! username.startsWith(USER_ROLE_NAME_TAG_PREFIX) )
				continue;
			username = username.substring(USER_ROLE_NAME_TAG_PREFIX.length());
			username = DashboardUtils.cleanUsername(username);
			String rolesString = (String) entry.getValue();
			DashboardUserInfo userInfo = userInfoMap.get(username);
			if ( userInfo == null )
				throw new IOException("Unknown user " + username + 
						" assigned roles in " + configFile.getPath() +
						"\n" + CONFIG_FILE_INFO_MSG);
			try {
				userInfo.addUserRoles(rolesString);
			} catch ( IllegalArgumentException ex ) {
				throw new IOException(ex.getMessage() + "\n" +
						"for " + username + " specified in " + 
						configFile.getPath() + "\n" + CONFIG_FILE_INFO_MSG);
			}
		}
		itsLogger.info("read configuration file " + configFile.getPath());
		// Watch for changes to the configuration file
		watchConfigFile();
	}

	/**
	 * @return
	 * 		the singleton instance of the DashboardDataStore
	 * @throws IOException 
	 * 		if unable to read the standard configuration file
	 */
	public static DashboardDataStore get() throws IOException {
		if ( singleton.get() == null )
			singleton.compareAndSet(null, new DashboardDataStore());
		return singleton.get();
	}

	/**
	 * Monitors the configuration file creating the current DashboardDataStore 
	 * singleton object.  If the configuration file has changed, sets the 
	 * current DashboardDataStore singleton object to null and stops monitoring
	 * the configuration file.  Thus, the next time the DashboardDataStore
	 * is needed, the configuration file will be reread and this monitor 
	 * will be restarted.
	 */
	private static final long MINUTES_CHECK_INTERVAL = 15;
	private static void watchConfigFile() {
		// Just create a time to monitor the last modified timestamp every fifteen 
		// minutes (don't bother with a separate thread running a watcher)
		(new Timer()).schedule(new TimerTask() {
			@Override
			public void run() {
				DashboardDataStore dataStore = singleton.get();
				if ( dataStore == null ) {
					cancel(); 
				}
				else if ( dataStore.configFile.lastModified() != 
							dataStore.configFileTimestamp ) {
					singleton.set(null);
					cancel();
				}
			}
		}, MINUTES_CHECK_INTERVAL * 60 * 1000, MINUTES_CHECK_INTERVAL * 60 * 1000);
	}

	/**
	 * @return
	 * 		the SOCAT version
	 */
	public Double getSocatVersion() {
		return socatVersion;
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

	/**
	 * @return
	 * 		the handler for cruise metadata documents
	 */
	public MetadataFileHandler getMetadataFileHandler() {
		return metadataFileHandler;
	}

	/**
	 * @return
	 * 		the checker for cruise data and metadata
	 */
	public DashboardCruiseChecker getDashboardCruiseChecker() {
		return cruiseChecker;
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
	 * Authenticates a user from the given username and password hashes.
	 *  
	 * @param username
	 * 		username
	 * @param passhash
	 * 		password hash
	 * @return
	 * 		true if successful
	 */
	public boolean validateUser(String username, String passhash) {
		if ( (username == null) || username.isEmpty() )
			return false;
		if ( (passhash == null) || passhash.isEmpty() )
			return false;
		String name = DashboardUtils.cleanUsername(username);
		DashboardUserInfo userInfo = userInfoMap.get(name);
		if ( userInfo == null )
			return false;
		String computedHash = spicedHash(name, passhash);
		if ( (computedHash == null) || computedHash.isEmpty() )
			return false;
		return computedHash.equals(userInfo.getAuthorizationHash());
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
	 * Generates an further encrypted password hash 
	 * from the given username and initially encrypted password
	 * @param username
	 * 		username to use
	 * @param passhash
	 * 		initially encrypted password to use
	 * @return
	 * 		further encrypted password, or an empty string on failure
	 */
	private String spicedHash(String username, String passhash) {
		String passSpicedHash;
		try {
			passSpicedHash = cipher.encrypt(passhash + encryptionSalt);
		} catch (DataLengthException | IllegalStateException
				| InvalidCipherTextException ex) {
			return "";
		}
		return DashboardUtils.passhashFromPlainText(username, passSpicedHash);
	}

	/**
	 * Prints out the username and password hash for the configuration file.
	 * 
	 * @param args
	 * 		(username)  (password)
	 */
	public static void main(String[] args) {
		if ( (args.length != 2) || 
			 args[0].trim().isEmpty() || 
			 args[1].trim().isEmpty() ) {
			System.err.println();
			System.err.println("arguments:  username  password");
			System.err.println();
			System.err.println("Prints out the username and password hash " +
					"for the dashboard configuration file");
			System.err.println();
			System.exit(1);
		}
		String username = args[0];
		String password = args[1];
		String passhash = DashboardUtils.passhashFromPlainText(username, password);
		if ( (passhash == null) || passhash.isEmpty() ) {
			System.err.println("Unacceptable username or password");
			System.exit(1);
		}
		try {
			DashboardDataStore dataStore = DashboardDataStore.get();
			String computedHash = dataStore.spicedHash(username, passhash);
			System.out.println(AUTHENTICATION_NAME_TAG_PREFIX + 
					DashboardUtils.cleanUsername(username) + "=" + computedHash);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}

}
