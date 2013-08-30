/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

	private static String CONFIG_RELATIVE_FILENAME = "content" + 
			File.separator + "SocatUploadDashboard" + 
			File.separator + "config.txt";
	private static String ENCRYPTION_KEY_NAME_TAG = "EncryptionKey";
	private static String ENCRYPTION_SALT_NAME_TAG = "EncryptionSalt";
	private static String AUTHENTICATION_HASHES_NAME_TAG = "AuthenticationHashes";

	private static AtomicReference<DashboardDataStore> singleton = 
			new AtomicReference<DashboardDataStore>();

	private byte[] encryptionKey;
	private String encryptionSalt;
	private Map<String,String> authenticationHashes;

	private HashMap<String,ArrayList<DashboardCruise>> userCruisesStore;

	/**
	 * Creates a data store initialized from the contents of the 
	 * standard configuration file.  This configuration file will
	 * look something like:
	 * <pre>
	 * EncryptionKey=[ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 ]
	 * EncryptionSalt="ChangeThisAndTheEncryptionKey"
	 * AuthenticationHashes={ "123456...abcdef"\:"socatuser" }
	 * </pre>
	 * 
	 * The EncryptionKey numbers are 24 values in [-128,127].
	 * The AuthenticationHashes can be added using the main method of this class.
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
		BufferedReader reader = new BufferedReader(new FileReader(configFile));
		try {
			configProps.load(reader);
		} finally {
			reader.close();
		}
		// Read and assign the encryption key from the data store
		try {
			String keyStr = configProps.getProperty(ENCRYPTION_KEY_NAME_TAG);
			if ( keyStr == null )
				throw new IOException("value not defined");
			encryptionKey = DashboardUtils.decodeByteArray(keyStr.trim());
		} catch ( Exception ex ) {
			throw new IOException("Invalid " + ENCRYPTION_KEY_NAME_TAG + 
					" value specified in " + configFile.getPath() + 
					" : " + ex.getMessage());			
		}
		// Read the salt string from the data store
		try {
			encryptionSalt = configProps.getProperty(ENCRYPTION_SALT_NAME_TAG);
			if ( encryptionSalt == null )
				throw new IOException("value not defined");
			encryptionSalt = encryptionSalt.trim();
			if ( ! ( encryptionSalt.startsWith("\"") && 
					 encryptionSalt.endsWith("\"") ) )
				throw new IOException("value not enclosed in double quotes");
			encryptionSalt = encryptionSalt.substring(1,encryptionSalt.length()-1);
		} catch ( Exception ex ) {  
			throw new IOException("Invalid encryption salt specified in " + 
					configFile.getPath() + " : " + ex.getMessage());
		}
		// Read and assign the authentication hashes 
		try {
			String mapStr = configProps.getProperty(AUTHENTICATION_HASHES_NAME_TAG);
			if ( mapStr == null ) 
				throw new IOException("value not defined");
			authenticationHashes = DashboardUtils.decodeStrStrMap(mapStr.trim());
		} catch ( Exception ex ) {
			authenticationHashes = new HashMap<String,String>();
		}

		//TODO: get the filename for the user cruises data store and read the data in that file
		userCruisesStore = new HashMap<String,ArrayList<DashboardCruise>>();
	}

	/**
	 * @return
	 * 		the singleton instance of the DashboardDataStore
	 * @throws IOException 
	 * 		if unable to read the standard configuration file
	 */
	static DashboardDataStore get() throws IOException {
		if ( singleton.get() == null )
			singleton.compareAndSet(null, new DashboardDataStore());
		return singleton.get();
	}

	/**
	 * Save the data store to the standard configuration file
	 * 
	 * @throws IOException
	 * 		if unable to write to the standard configuration file
	 */
	private void save() throws IOException {
		Properties configProps = new Properties();
		String baseDir = System.getProperty("catalina.base");
		// The following is just for debugging under Eclipse
		if ( baseDir == null ) 
			baseDir = System.getProperty("user.home");
		// Store the encryption key
		configProps.setProperty(ENCRYPTION_KEY_NAME_TAG, 
				DashboardUtils.encodeByteArray(encryptionKey));
		// Store the salt string
		configProps.setProperty(ENCRYPTION_SALT_NAME_TAG, 
				"\"" + encryptionSalt + "\"");
		// Store the authentication hashes
		configProps.setProperty(AUTHENTICATION_HASHES_NAME_TAG, 
				DashboardUtils.encodeStrStrMap(authenticationHashes));
		// Write the properties to the standard configuration file
		File configFile = new File(baseDir, CONFIG_RELATIVE_FILENAME);
		BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
		try {
			configProps.store(writer, "");
		} finally {
			writer.close();
		}
	}

	/**
	 * Authenticates a user from the given username and password hashes.
	 *  
	 * @param userhash
	 * 		username hash
	 * @param passhash
	 * 		password hash
	 * @return
	 * 		plaintext username if successful; otherwise null
	 */
	String getUsernameFromHashes(String userhash, String passhash) {
		if ( (userhash == null) || (passhash == null) ||
			 userhash.isEmpty() || passhash.isEmpty() )
			return null;
		String username = authenticationHashes.get(spicedHash(userhash, passhash));
		if ( (username != null) && username.isEmpty() )
			username = null;
		return username;
	}

	/**
	 * Gets the list of cruises for a user
	 * 
	 * @param username
	 * 		get cruises for this user
	 * @return
	 * 		the list of cruises for the user; may be null
	 */
	ArrayList<DashboardCruise> getCruisesForUser(String username) {
		if ( (username == null) || username.isEmpty() )
			return null;
		return userCruisesStore.get(username);
	}

	/**
	 * Generates a username/password hash from the given userhash and passhash 
	 */
	private String spicedHash(String userhash, String passhash) {
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey(encryptionKey);
		String userSpicedHash;
		try {
			userSpicedHash = cipher.encrypt(userhash + encryptionSalt);
		} catch (DataLengthException | IllegalStateException
				| InvalidCipherTextException ex) {
			userSpicedHash = "";
		}
		String passSpicedHash;
		try {
			passSpicedHash = cipher.encrypt(passhash + encryptionSalt);
		} catch (DataLengthException | IllegalStateException
				| InvalidCipherTextException ex) {
			passSpicedHash = "";
		}
		String[] hashes = DashboardUtils.hashesFromPlainText(
				userSpicedHash, passSpicedHash);
		return hashes[0] + hashes[1];
	}

	/**
	 * Adds the username and username/password hash to the standard configuration file.
	 * 
	 * @param args
	 * 		(username)  (password)
	 */
	public static void main(String[] args) {
		if ( (args.length != 2) || args[0].trim().isEmpty() || args[1].trim().isEmpty() ) {
			System.err.println();
			System.err.println("arguments:  <username>  <password>");
			System.err.println("Adds the username and username/password hash ");
			System.err.println("to the standard configuration file");
			System.exit(1);
		}
		String username = args[0];
		String password = args[1];
		String[] hashes = DashboardUtils.hashesFromPlainText(username, password);
		try {
			DashboardDataStore dataStore = DashboardDataStore.get();
			String combinedHash = dataStore.spicedHash(hashes[0], hashes[1]);
			dataStore.authenticationHashes.put(combinedHash, username);
			dataStore.save();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
