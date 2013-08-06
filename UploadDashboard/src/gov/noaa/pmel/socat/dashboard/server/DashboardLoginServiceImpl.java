/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;
import gov.noaa.pmel.socat.dashboard.shared.DashboardLoginService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;
import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.client.TripleDesCipher;

/**
 * Server side implementation of the DashboardLoginService
 * 
 * @author Karl Smith
 */
public class DashboardLoginServiceImpl extends XsrfProtectedServiceServlet
		implements DashboardLoginService {

	private static final long serialVersionUID = -3658366460482720904L;

	private final String saltKey;
	private final HashMap<String,String> hashesStore;

	public DashboardLoginServiceImpl() {
		super();
		// TODO: Value to be read from a data store
		saltKey = "a0U4N[2Uj;sfWzP(T+t9!d#i";
		// TODO: Values to be read from a data store, as <passhash,username>
		hashesStore = new HashMap<String,String>();
		// TODO: delete this user created for debugging
		String[] hashes = DashboardUtils.hashesFromPlainText("ksmith", "secretPassword");
		hashesStore.put(spicedHash(hashes[0], hashes[1]), "ksmith");
	}

	@Override
	public DashboardCruiseListing authenticateUser(String userhash, String passhash) {
		// Authenticate the user
		String username = hashesStore.get(spicedHash(userhash, passhash));
		ArrayList<DashboardCruise> cruises;
		if ( username != null ) {
			// TODO: get cruises from data store
			cruises = new ArrayList<DashboardCruise>();
		}
		else
			cruises = null;
		return new DashboardCruiseListing(username, cruises);
	}

	/**
	 * Generates a username/password hash from the given userhash and passhash 
	 */
	private String spicedHash(String userhash, String passhash) {
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey(saltKey.getBytes());
		String userSpicedHash;
		try {
			userSpicedHash = cipher.encrypt(userhash + saltKey);
		} catch (DataLengthException | IllegalStateException
				| InvalidCipherTextException ex) {
			userSpicedHash = "";
		}
		String passSpicedHash;
		try {
			passSpicedHash = cipher.encrypt(passhash + saltKey);
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
		System.out.println(username + "    " + 
				(new DashboardLoginServiceImpl()).spicedHash(hashes[0], hashes[1]));
	}

}
