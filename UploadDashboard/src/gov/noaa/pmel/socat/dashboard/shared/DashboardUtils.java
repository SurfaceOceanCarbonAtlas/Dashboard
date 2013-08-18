/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import com.googlecode.gwt.crypto.client.TripleDesCipher;

/**
 * Static dashboard utility functions 
 * for use on both the client and server side.
 *  
 * @author Karl Smith
 */
public class DashboardUtils {

	/**
	 * Generate the encrypted userhash and passhash for a given 
	 * plaintext username and password.
	 * 
	 * @param username
	 * 		plaintext username to use
	 * @param password
	 * 		plaintext password to use 
	 * @return
	 * 		encrypted username and password as an array of two Strings;
	 * 		these Strings will be empty if an error occurs 
	 */
	static public String[] hashesFromPlainText(String username, String password) {
		// Make sure something reasonable Strings are given
		if ( (username.length() < 4) || (password.length() < 7) ) {
			return new String[] { "", "" };
		}

		// This salt is just to make sure the keys are long enough
		String salt = "4z#Ni!q?F7b0m9nK(uDF[g%T";
		TripleDesCipher cipher = new TripleDesCipher();

		// Encrypt the username
		cipher.setKey((password.substring(0,4) + username + salt)
				.substring(0,24).getBytes());
		String passhash;
		try {
			passhash = cipher.encrypt(password);
		} catch (Exception ex) {
			passhash = "";
		}

		// Encrypt the password
		cipher.setKey((username.substring(0,4) + password + salt)
				.substring(0,24).getBytes());
		String userhash;
		try {
			userhash = cipher.encrypt(username);
		} catch (Exception ex) {
			userhash = "";
		}

		return new String[] {userhash, passhash};
	}

}
