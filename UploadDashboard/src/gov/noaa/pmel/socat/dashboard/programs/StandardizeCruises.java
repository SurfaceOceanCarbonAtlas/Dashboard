/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CruiseStandardizer;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Standardizes the PI and ship names for cruises. 
 * The default dashboard configuration is used for this process.
 *   
 * @author Karl Smith
 */
public class StandardizeCruises {

	/**
	 * @param args
	 * 		ExpocodesFile - standard cruises with these expocodes
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Standardizes the PI and ship names for cruises specified ");
			System.err.println("in ExpocodesFile.  The default dashboard configuration is ");
			System.err.println("used for this process. ");
			System.err.println();
			System.exit(1);
		}
		String expocodesFilename = args[0];
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
			DsgNcFileHandler dsgHandler = dataStore.getDsgNcFileHandler();
			CruiseStandardizer standardizer = new CruiseStandardizer(dsgHandler, 
													dataStore.getFerretConfig());

			// Get the expocodes of the cruises to standardize
			TreeSet<String> allExpocodes = new TreeSet<String>();
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

			// standardize the PI and ship names in each of these cruises
			for ( String expocode : allExpocodes ) {
				try {
					standardizer.standardizePINames(expocode);
					standardizer.standardizeShipNames(expocode);
				} catch (Exception ex) {
					System.err.println("Error updating " + expocode + " : " + ex.getMessage());
					ex.printStackTrace();
					System.err.println("===================================================");
					success = false;
				}
			}
			dsgHandler.flagErddap(true, true);
		} finally {
			dataStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
