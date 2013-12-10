/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

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
	private static final String CONFIG_RELATIVE_FILENAME = "content" + 
			File.separator + SERVER_APP_NAME + File.separator + "config.properties";
	private static final String ENCRYPTION_KEY_NAME_TAG = "EncryptionKey";
	private static final String ENCRYPTION_SALT_NAME_TAG = "EncryptionSalt";
	private static final String SOCAT_VERSION_NAME_TAG = "SocatVersion";
	private static final String SVN_USER_NAME_TAG = "SVNUsername";
	private static final String SVN_PASSWORD_NAME_TAG = "SVNPassword";
	private static final String USER_FILES_DIR_NAME_TAG = "UserFilesDir";
	private static final String CRUISE_FILES_DIR_NAME_TAG = "CruiseFilesDir";
	private static final String METADATA_FILES_DIR_NAME_TAG = "MetadataFilesDir";
	private static final String AUTHENTICATION_NAME_TAG_PREFIX = "HashFor_";
	private static final String USER_ROLE_NAME_TAG_PREFIX = "RoleFor_";

	private static final String CONFIG_FILE_INFO_MSG = 
			"This configuration file should look something like: \n" +
			"# ------------------------------ \n" +
			ENCRYPTION_KEY_NAME_TAG + "=[ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 ] \n" +
			ENCRYPTION_SALT_NAME_TAG + "=SomeArbitraryStringOfCharacters \n" +
			SOCAT_VERSION_NAME_TAG + "=SomeValue \n" +
			SVN_USER_NAME_TAG + "=SVNUsername" +
			SVN_PASSWORD_NAME_TAG + "=SVNPasswork" +
			USER_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/User/Data \n" +
			CRUISE_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/Cruise/Data \n" +
			METADATA_FILES_DIR_NAME_TAG + "=/Some/SVN/Work/Dir/For/Metadata/Docs \n" +
			AUTHENTICATION_NAME_TAG_PREFIX + "SomeUserName=AVeryLongKeyOfHexidecimalValues \n" +
			USER_ROLE_NAME_TAG_PREFIX + "SomeUserName=MemberOf1,MemberOf2 \n" +
			AUTHENTICATION_NAME_TAG_PREFIX + "SomeManagerName=AnotherVeryLongKeyOfHexidecimalValues \n" +
			USER_ROLE_NAME_TAG_PREFIX + "SomeManagerName=ManagerOf1,MemberOf2 \n" +
			AUTHENTICATION_NAME_TAG_PREFIX + "SomeAdminName=YetAnotherVeryLongKeyOfHexidecimalValues \n" +
			USER_ROLE_NAME_TAG_PREFIX + "SomeAdminName=Admin \n" +
			"# ------------------------------ \n" +
			"The EncryptionKey should be 24 random integer values in [-128,127] \n" +
			"The hashes for users can be added using the main method \n" +
			"of gov.noaa.pmel.socat.dashboard.server.DashboardDataStore \n";

	private static final AtomicReference<DashboardDataStore> singleton = 
			new AtomicReference<DashboardDataStore>();

	private TripleDesCipher cipher;
	private String encryptionSalt;
	// Map of username to user info
	private HashMap<String,DashboardUserInfo> userInfoMap;
	private String socatVersion;
	private DashboardUserFileHandler userFileHandler;
	private DashboardCruiseFileHandler cruiseFileHandler;
	private DashboardMetadataFileHandler metadataFileHandler;

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
		// Read the properties from the standard configuration file
		Properties configProps = new Properties();
		File configFile = new File(baseDir, CONFIG_RELATIVE_FILENAME);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(configFile));
			try {
				configProps.load(reader);
			} finally {
				reader.close();
			}
		}
		catch ( Exception ex ) {
			throw new IOException("Problems reading " + configFile.getPath() +
					" : " + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
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
					" value specified in " + configFile.getPath() + 
					" : " + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
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
					" value specified in " + configFile.getPath() + 
					" : " + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		// Read the SOCAT version
		try {
			propVal = configProps.getProperty(SOCAT_VERSION_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			if ( propVal.isEmpty() )
				throw new IllegalArgumentException("blank value");
			socatVersion = propVal;
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + SOCAT_VERSION_NAME_TAG + 
					" value specified in " + configFile.getPath() + 
					" : " + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
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
					" value specified in " + configFile.getPath() + 
					" : " + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		// Read the SVN password; can be blank or not given
		String svnPassword = "";
		propVal = configProps.getProperty(SVN_PASSWORD_NAME_TAG);
		if ( propVal != null ) {
			propVal = propVal.trim();
			svnPassword = propVal.trim();
		}
		// Read the user files directory name
		try {
			propVal = configProps.getProperty(USER_FILES_DIR_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			userFileHandler = new DashboardUserFileHandler(propVal, 
					svnUsername, svnPassword);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + USER_FILES_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + 
					" : " + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		// Read the cruise files directory name
		try {
			propVal = configProps.getProperty(CRUISE_FILES_DIR_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			cruiseFileHandler = new DashboardCruiseFileHandler(propVal,
					svnUsername, svnPassword);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + CRUISE_FILES_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + 
					" : " + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		// Read the metadata files directory name
		try {
			propVal = configProps.getProperty(METADATA_FILES_DIR_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			metadataFileHandler = new DashboardMetadataFileHandler(propVal,
					svnUsername, svnPassword);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + METADATA_FILES_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + 
					" : " + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
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
			String hash = (String) entry.getValue();
			DashboardUserInfo userInfo;
			try {
				userInfo = new DashboardUserInfo(username, hash);
			} catch ( IllegalArgumentException ex ) {
				throw new IOException(ex.getMessage() + " for " + username +
						" specified in " + configFile.getPath() + 
						"\n" + CONFIG_FILE_INFO_MSG);
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
			String rolesString = (String) entry.getValue();
			DashboardUserInfo userInfo = userInfoMap.get(username);
			if ( userInfo == null )
				throw new IOException("Unknown user " + username + 
						" assigned roles in " + configFile.getPath() +
						"\n" + CONFIG_FILE_INFO_MSG);
			try {
				userInfo.addUserRoles(rolesString);
			} catch ( IllegalArgumentException ex ) {
				throw new IOException(ex.getMessage() + " for " + username +
						" specified in " + configFile.getPath() + 
						"\n" + CONFIG_FILE_INFO_MSG);
			}
		}
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
	 * @return
	 * 		the SOCAT version
	 */
	public String getSocatVersion() {
		return socatVersion;
	}

	/**
	 * @return 
	 * 		the handler for user data files
	 */
	public DashboardUserFileHandler getUserFileHandler() {
		return userFileHandler;
	}

	/**
	 * @return 
	 * 		the handler for cruise data files
	 */
	public DashboardCruiseFileHandler getCruiseFileHandler() {
		return cruiseFileHandler;
	}

	/**
	 * @return
	 * 		the handler for cruise metadata documents
	 */
	public DashboardMetadataFileHandler getMetadataFileHandler() {
		return metadataFileHandler;
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
		DashboardUserInfo userInfo = userInfoMap.get(username);
		if ( userInfo == null )
			return false;
		String computedHash = spicedHash(username, passhash);
		if ( (computedHash == null) || computedHash.isEmpty() )
			return false;
		return computedHash.equals(userInfo.getAuthorizationHash());
	}


	/**
	 * Determines if username has manager privilege over othername. 
	 * This can be from username being an administrator, a manager
	 * of a group othername belongs to, or having the same username,
	 * so long as username is an authorized user.
	 * 
	 * @param username
	 * 		manager username to check
	 * @param othername
	 * 		group member username to check
	 * @return
	 * 		true if username is an authorized user and has manager
	 * 		privileges over username
	 */
	public boolean userManagesOver(String username, String othername) {
		DashboardUserInfo userInfo = userInfoMap.get(username);
		if ( userInfo == null )
			return false;
		return userInfo.managesOver(userInfoMap.get(othername));
	}

	/**
	 * @param username
	 * 		name of the user
	 * @return
	 * 		true is this user is an admin or a manager of a group
	 * 		(regardless of whether there is anyone else in the group)
	 */
	public boolean isManager(String username) {
		DashboardUserInfo userInfo = userInfoMap.get(username);
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
	 * Adds the username and username/password hash to the standard configuration file.
	 * 
	 * @param args
	 * 		(username)  (password)
	 */
	public static void main(String[] args) {
		if ( (args.length != 2) || 
			 args[0].trim().isEmpty() || 
			 args[1].trim().isEmpty() ) {
			System.err.println();
			System.err.println("arguments:  <username>  <password>");
			System.err.println("Adds the username and username/password hash ");
			System.err.println("to the standard configuration file");
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
			String baseDir = System.getProperty("catalina.base");
			// The following is just for debugging under Eclipse
			if ( baseDir == null ) 
				baseDir = System.getProperty("user.home");
			File configFile = new File(baseDir, CONFIG_RELATIVE_FILENAME);
			PrintWriter writer = new PrintWriter(
					new BufferedWriter(new FileWriter(configFile, true)));
			writer.println(AUTHENTICATION_NAME_TAG_PREFIX + username + "=" + computedHash);
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

}
