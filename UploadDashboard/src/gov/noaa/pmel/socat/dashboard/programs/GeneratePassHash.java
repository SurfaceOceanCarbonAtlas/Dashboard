/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

/**
 * Generates the username/password hash line for the dashboard configuration file.
 * 
 * @author Karl Smith
 */
public class GeneratePassHash {

	/**
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
			try {
				String computedHash = dataStore.spicedHash(username, passhash);
				System.out.println(DashboardDataStore.AUTHENTICATION_NAME_TAG_PREFIX + 
						DashboardUtils.cleanUsername(username) + "=" + computedHash);
			} finally {
				dataStore.shutdown();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}

}
