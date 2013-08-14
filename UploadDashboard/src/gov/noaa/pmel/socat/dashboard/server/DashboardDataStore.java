/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.client.TripleDesCipher;

/**
 * Reads and holds the data in a Dashboard data store file
 * 
 * @author Karl Smith
 */
public class DashboardDataStore {
	
	protected File storeFile;
	protected byte[] storeKey;
	protected String storeSalt;
	protected HashMap<String,String> hashesStore;
	protected HashMap<String,ArrayList<DashboardCruise>> userCruisesStore;

	/**
	 * Creates a Dashboard store with the the data in the given store file.
	 * 
	 * @param storeFile
	 * 		Name of the data store file
	 * @throws IOException
	 * 		If one is thrown while reading the data store
	 */
	public DashboardDataStore(File storeFile) throws IOException {
		this.storeFile = storeFile;
		// TODO: read data from an actual data store
		storeKey = "a0U4N[2Uj;sfWzP(T+t9!d#i".getBytes();
		storeSalt = "a0U4N[2Uj;sfWzP(T+t9!d#i";
		hashesStore = new HashMap<String,String>();
		String[] hashes = DashboardUtils.hashesFromPlainText("socatuser", "socatpass");
		hashesStore.put(spicedHash(hashes[0], hashes[1]), "socatuser");
		userCruisesStore = new HashMap<String,ArrayList<DashboardCruise>>();
		ArrayList<DashboardCruise> cruiseList = new ArrayList<DashboardCruise>();
		for (int k = 12; k < 28; k++) {
			DashboardCruise cruise = new DashboardCruise();
			cruise.setExpocode("XXXX201307" + k);
			cruise.setUploadFilename("/home/socatuser/data" + k + ".tsv");
			cruise.setDataCheckDate(new Date(System.currentTimeMillis() 
					- (long) (100000.0 * Math.random())));
			cruise.setMetaCheckDate(new Date(System.currentTimeMillis() 
					- (long) (100000.0 * Math.random())));
			cruise.setQCStatus("N/A");
			cruise.setArchiveStatus("NO");
			cruiseList.add(cruise);
		}
		userCruisesStore.put("socatuser", cruiseList);
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
	public String getUsernameFromHashes(String userhash, String passhash) {
		if ( (userhash == null) || (passhash == null) ||
			 userhash.isEmpty() || passhash.isEmpty() )
			return null;
		String username = hashesStore.get(spicedHash(userhash, passhash));
		if ( username.isEmpty() )
			username = null;
		return username;
	}

	/**
	 * Gest the list of cruises for a user
	 * 
	 * @param username
	 * 		get cruises for this user
	 * @return
	 * 		the list of cruises for the user
	 */
	public ArrayList<DashboardCruise> getCruisesForUser(String username) {
		if ( (username == null) || username.isEmpty() )
			return null;
		return userCruisesStore.get(username);
	}

	/**
	 * Generates a username/password hash from the given userhash and passhash 
	 */
	private String spicedHash(String userhash, String passhash) {
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey(storeKey);
		String userSpicedHash;
		try {
			userSpicedHash = cipher.encrypt(userhash + storeSalt);
		} catch (DataLengthException | IllegalStateException
				| InvalidCipherTextException ex) {
			userSpicedHash = "";
		}
		String passSpicedHash;
		try {
			passSpicedHash = cipher.encrypt(passhash + storeSalt);
		} catch (DataLengthException | IllegalStateException
				| InvalidCipherTextException ex) {
			passSpicedHash = "";
		}
		String[] hashes = DashboardUtils.hashesFromPlainText(
				userSpicedHash, passSpicedHash);
		return hashes[0] + hashes[1];
	}

	/**
	 * Prints out the username and username/password hash expected to be stored
	 * for the validateUser method of this class.  The username is unchanged 
	 * from input.
	 * 
	 * @param args
	 * 		(username)  (password)
	 */
	public static void main(String[] args) {
		if ( (args.length != 2) || args[0].trim().isEmpty() || args[1].trim().isEmpty() ) {
			System.err.println();
			System.err.println("arguments:  <username>  <password>");
			System.err.println("prints out the username and expected username/password hash");
			System.exit(1);
		}
		String username = args[0];
		String password = args[1];
		String[] hashes = DashboardUtils.hashesFromPlainText(username, password);
		DashboardDataStore store;
		try {
			// TODO: user actual data store file
			store = new DashboardDataStore(new File("fake"));
			System.out.println(username + "    " + store.spicedHash(hashes[0], hashes[1]));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
