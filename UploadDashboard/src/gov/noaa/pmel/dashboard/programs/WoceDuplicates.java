/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.actions.CruiseModifier;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;

import java.util.ArrayList;

/**
 * WOCE out any duplicates of lon/lat/time/fCO2_rec in a dataset
 * @author Karl Smith
 */
public class WoceDuplicates {

	/**
	 * @param args
	 * 		expocode -	expoocode of the dataset to search for and WOCE-4 
	 * 					any duplicate lon/lat/time/fCO2_rec duplicates
	 */
	public static void main(String[] args) {
		String expocode;
		try {
			expocode = DashboardServerUtils.checkExpocode(args[0]);
		} catch ( Exception ex ) {
			expocode = null;
		}
		if ( (args.length != 1) || (expocode == null) ) {
			if ( args.length == 1 ) {
				System.err.println("");
				System.err.println("expocode not valid: " + args[0]);
			}
			System.err.println("");
			System.err.println("Arguments: expocode");
			System.err.println("");
			System.err.println("Search for lon/lat/time/fCO2_rec duplicates within the dataset ");
			System.err.println("with the given expocode.  Any duplicates found are assigned a ");
			System.err.println("WOCE-4 flag by the automated data checker with an appropriate ");
			System.err.println("message.  If duplicates are found, the database and full-data DSG");
			System.err.println("file are updated and the decimated-data DSG file is regenerated. ");
			System.err.println("ERDDAP is NOT notified of any changes since this program may ");
			System.err.println("be run repeatedly for a number of different expocodes. ");
			System.err.println("");
			System.exit(1);
		}

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		boolean success = true;
		try {
			CruiseModifier modifier = new CruiseModifier(configStore);
			// Assign any duplicate data points to 
			try {
				ArrayList<String> duplicates = modifier.woceDuplicateDatapoints(expocode);
				for ( String msg : duplicates ) {
					System.err.println(expocode + ": " + msg);
				}
			} catch (Exception ex) {
				System.err.println("Unable to WOCE-4 duplicate lon/lat/time/fCO2_rec data points for " + 
						expocode + ": " + ex.getMessage());
				success = false;
			}
		} finally {
			DashboardConfigStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
