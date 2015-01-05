/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CruiseResubmitter;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Rechecks cruises, and resubmits cruises that had been submitted.  Uses 
 * the default dashboard configuration.  If a submitted cruise does not 
 * have a DSG file, 'N' (new) will be assigned as the QC status; otherwise,
 * 'U' (updated) will be assigned.  Requests to "send to CDIAC immediately" 
 * are not re-sent.  The default dashboard configuration is used for this 
 * recheck and resubmit process.
 * 
 * @author Karl Smith
 */
public class ResubmitCruises {

	/**
	 * @param args
	 * 		Username - name of the dashboard admin user requesting this update.
	 * 		ExpocodesFile - file of expocodes to recheck/resubmit; if not given,
	 * 		                all cruises are rechecked/resubmitted.
	 */
	public static void main(String[] args) {
		if ( (args.length < 1) || (args.length > 2) ) {
			System.err.println("Arguments:  Username  [ ExpocodesFile ]");
			System.err.println();
			System.err.println("Rechecks all cruises, or those specified in ExpocodesFile if given. ");
			System.err.println("Resubmits all cruises that had been submitted.  If a submitted cruise ");
			System.err.println("does not have a DSG file, 'N' (new) will be assigned as the QC status; ");
			System.err.println("otherwise, 'U' (updated) will be assigned.  Requests to \"send to CDIAC ");
			System.err.println("immediately\" are not re-sent.  The default dashboard configuration is ");
			System.err.println("used for this recheck and resubmit process. ");
			System.err.println();
			System.err.println("Username is the dashboard admin requesting this update.");
			System.err.println();
			System.exit(1);
		}

		String username = args[0];
		String expocodesFilename;
		if ( args.length > 1 )
			expocodesFilename = args[1];
		else
			expocodesFilename = null;

		boolean success = true;

		// Get the default dashboard configuration
		DashboardDataStore dataStore = null;		
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard " +
					"configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			if ( ! dataStore.isAdmin(username) ) {
				System.err.println(username + " is not an admin for the dashboard");
				System.exit(1);				
			}
			CruiseResubmitter resubmitter = new CruiseResubmitter(dataStore);

			// Get the expocode of the cruises to resubmit
			TreeSet<String> allExpocodes = null; 
			if ( expocodesFilename != null ) {
				allExpocodes = new TreeSet<String>();
				try {
					BufferedReader expoReader = 
							new BufferedReader(new FileReader(expocodesFilename));
					try {
						String dataline = expoReader.readLine();
						while ( dataline != null ) {
							dataline = dataline.trim();
							if ( ! ( dataline.isEmpty() || dataline.startsWith("#") ) )
								allExpocodes.add(dataline);
							dataline = expoReader.readLine();
						}
					} finally {
						expoReader.close();
					}
				} catch (Exception ex) {
					System.err.println("Error getting expocodes from " + 
							expocodesFilename + ": " + ex.getMessage());
					ex.printStackTrace();
					System.exit(1);
				}
			} 
			else {
				try {
					allExpocodes = new TreeSet<String>(
							dataStore.getCruiseFileHandler().getMatchingExpocodes("*"));
				} catch (Exception ex) {
					System.err.println("Error getting all expocodes: " + ex.getMessage());
					ex.printStackTrace();
					System.exit(1);
				}
			}

			// Recheck, and possibly resubmit, each of these cruises
			for ( String expocode : allExpocodes ) {
				try {
					resubmitter.resubmitCruise(expocode, username);
				} catch (Exception ex) {
					System.err.println("Error updating " + expocode + " : " + ex.getMessage());
					ex.printStackTrace();
					System.err.println("===================================================");
					success = false;
				}
			}
		} finally {
			dataStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
