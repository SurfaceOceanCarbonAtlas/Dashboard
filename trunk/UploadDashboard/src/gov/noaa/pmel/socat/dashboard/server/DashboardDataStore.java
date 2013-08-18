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

	private static DashboardDataStore singleton;
	private byte[] storeKey;
	private String storeSalt;
	private HashMap<String,String> hashesStore;
	private HashMap<String,ArrayList<DashboardCruise>> userCruisesStore;

	/**
	 * Do not create an instance of this class; 
	 * instead use {@link #get()} to retrieve the singleton instance
	 */
	private DashboardDataStore() {
	}

	/**
	 * @return
	 * 		the singleton instance of the DashboardDataStore;
	 * 		will be null if {@link #setStoreFile(File)} was
	 * 		not last called with a valid store file. 
	 */
	static DashboardDataStore get() {
		return singleton;
	}

	/**
	 * Creates or reassigns the dashboard data store 
	 * with the the data in the given store file.
	 * 
	 * @param storeFile
	 * 		name of the data store file, or null to clear the data store
	 * @throws IOException
	 * 		if one is thrown while reading the data store
	 */
	static void setStoreFile(File storeFile) throws IOException {
		if ( singleton == null )
			singleton = new DashboardDataStore();
		if ( storeFile != null ) {
			// TODO: read data from an actual data store
			singleton.storeKey = "a0U4N[2Uj;sfWzP(T+t9!d#i".getBytes();
			singleton.storeSalt = "a0U4N[2Uj;sfWzP(T+t9!d#i";
			singleton.hashesStore = new HashMap<String,String>();
			String[] hashes = DashboardUtils.hashesFromPlainText("socatuser", "socatpass");
			singleton.hashesStore.put(singleton.spicedHash(hashes[0], hashes[1]), "socatuser");
			singleton.userCruisesStore = new HashMap<String,ArrayList<DashboardCruise>>();
			ArrayList<DashboardCruise> cruiseList = new ArrayList<DashboardCruise>();
			String[] qcStatuses = new String[] { "No", "Yes", "Yes", "Suspended", 
					"Accepted QC-B", "Accepted QC-B", "Accepted QC-C", "Accepted QC-D" };
			String[] archiveStatuses = new String[] { "No", "Submitted to CDIAC", "Submitted to XXXXXX", 
					"doi:xx.xxxx/XXXXXXX.xxxxxx", "Submit with SOCAT", "Submit with SOCAT", "Submit with SOCAT" };
			for (int k = 10; k < 23; k++) {
				DashboardCruise cruise = new DashboardCruise();
				cruise.setExpocode("XXXX201306" + k);
				cruise.setUploadFilename("/home/socatuser/data" + k + ".tsv");
				cruise.setDataCheckDate(new Date(System.currentTimeMillis() 
						- (long) (1.0E8 * Math.random())));
				cruise.setMetaCheckDate(new Date(System.currentTimeMillis() 
						- (long) (1.0E8 * Math.random())));
				String qcStat = qcStatuses[(int) (qcStatuses.length * Math.random())];
				cruise.setQCStatus(qcStat);
				int archiveNum;
				if ( qcStat.startsWith("Accepted") )
					archiveNum = (int) ((archiveStatuses.length - 1) * Math.random()) + 1;
				else
					archiveNum = 0;
				cruise.setArchiveStatus(archiveStatuses[archiveNum]);
				cruiseList.add(cruise);
			}
			singleton.userCruisesStore.put("socatuser", cruiseList);
			hashes = DashboardUtils.hashesFromPlainText("neweruser", "newerpass");
			singleton.hashesStore.put(singleton.spicedHash(hashes[0], hashes[1]), "neweruser");
		}
		else {
			singleton = null;
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
		String username = hashesStore.get(spicedHash(userhash, passhash));
		if ( username.isEmpty() )
			username = null;
		return username;
	}

	/**
	 * Gets the list of cruises for a user
	 * 
	 * @param username
	 * 		get cruises for this user
	 * @return
	 * 		the list of cruises for the user
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
		try {
			// TODO: use actual data store file
			DashboardDataStore.setStoreFile(new File("fake"));
			System.out.println(username + "    " + 
					DashboardDataStore.get().spicedHash(hashes[0], hashes[1]));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
