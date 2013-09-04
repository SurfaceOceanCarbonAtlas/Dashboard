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
			File.separator + SERVER_APP_NAME + File.separator + "config.preferences";
	private static final String ENCRYPTION_KEY_NAME_TAG = "EncryptionKey";
	private static final String ENCRYPTION_SALT_NAME_TAG = "EncryptionSalt";
	private static final String SOCAT_VERSION_NAME_TAG = "SocatVersion";
	private static final String USER_FILES_DIR_NAME_TAG = "UserFilesDir";
	private static final String CRUISE_FILES_DIR_NAME_TAG = "CruiseFilesDir";
	private static final String AUTHENTICATION_NAME_TAG_PREFIX = "HashFor_";

	private static final String CONFIG_FILE_INFO_MSG = 
			"This configuration file should look something like: \n" +
			"# ------------------------------ \n" +
			"EncryptionKey=[ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 ] \n" +
			"EncryptionSalt=SomeArbitraryStringOfCharacters \n" +
			"SocatVersion=4 \n" +
			"UserFilesDir=/Some/Directory/For/User/Data \n" +
			"CruiseFilesDir=/Some/Directory/For/Cruise/Data \n" +
			"HashFor_SomeUserName=AVeryLongKeyOfHexidecimalValues \n" +
			"HashFor_AnotherUserName=AnotherVeryLongKeyOfHexidecimalValues \n" +
			"# ------------------------------ \n" +
			"The EncryptionKey should be 24 random integer values in [-128,127] \n" +
			"The hashes for users can be added using the main method \n" +
			"of gov.noaa.pmel.socat.dashboard.server.DashboardDataStore \n";

	private static final AtomicReference<DashboardDataStore> singleton = 
			new AtomicReference<DashboardDataStore>();

	private TripleDesCipher cipher;
	private String encryptionSalt;
	private HashMap<String,String> authenticationHashes;
	private String socatVersion;
	private DashboardUserFileHandler userFileHandler;
	private DashboardCruiseFileHandler cruiseFileHandler;

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
		// Read the user files directory name
		try {
			propVal = configProps.getProperty(USER_FILES_DIR_NAME_TAG);
			if ( propVal == null )
				throw new IllegalArgumentException("value not defined");
			propVal = propVal.trim();
			userFileHandler = new DashboardUserFileHandler(propVal);
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
			cruiseFileHandler = new DashboardCruiseFileHandler(propVal);
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + CRUISE_FILES_DIR_NAME_TAG + 
					" value specified in " + configFile.getPath() + 
					" : " + ex.getMessage() + "\n" + CONFIG_FILE_INFO_MSG);
		}
		// Read and assign the authentication hashes 
		authenticationHashes = new HashMap<String,String>();
		for ( Entry<Object,Object> entry : configProps.entrySet() ) {
			if ( ! ((entry.getKey() instanceof String) && 
					(entry.getValue() instanceof String)) )
				continue;
			String username = (String) entry.getKey();
			if ( ! username.startsWith(AUTHENTICATION_NAME_TAG_PREFIX) )
				continue;
			username = username.substring(AUTHENTICATION_NAME_TAG_PREFIX.length());
			if ( username.length() < 4 )
				throw new IOException("Username too short for " + username + 
						" specified in " + configFile.getPath() + 
						"\n" + CONFIG_FILE_INFO_MSG);
			String hash = (String) entry.getValue();
			if ( hash.length() < 32 )
				throw new IOException("Hash too short for username " + username + 
						" specified in " + configFile.getPath() + 
						"\n" + CONFIG_FILE_INFO_MSG);
			// Note that the hash is the key and the user name is the value
			authenticationHashes.put(username, hash);
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
	 * Authenticates a user from the given username and password hashes.
	 *  
	 * @param username
	 * 		username
	 * @param passhash
	 * 		password hash
	 * @return
	 * 		true if successful
	 */
	boolean validateUser(String username, String passhash) {
		if ( (username == null) || username.isEmpty() )
			return false;
		if ( (passhash == null) || passhash.isEmpty() )
			return false;
		String computedHash = spicedHash(username, passhash);
		if ( (computedHash != null) && computedHash.isEmpty() )
			return false;
		String expectedHash = authenticationHashes.get(username);
		if ( (expectedHash != null) && expectedHash.isEmpty() )
			return false;
		return expectedHash.equals(computedHash);
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
