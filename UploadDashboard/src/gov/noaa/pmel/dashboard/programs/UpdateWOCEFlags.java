/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.server.CruiseDsgNcFile;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.WoceEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Updates the WOCE flags in the full-data and decimated-data DSG files for cruises
 * to the latest applicable WOCE flags obtained from the database.
 * 
 * @author Karl Smith
 */
public class UpdateWOCEFlags {

	/**
	 * @param args
	 * 		ExpocodesFile - update WOCE flags of these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Updates the WOCE flags in the full-data and decimated-data DSG ");
			System.err.println("files for cruises specified in ExpocodesFile to the latest ");
			System.err.println("applicable WOCE flags obtained from the database.  The default ");
			System.err.println("dashboard configuration is used for this process. "); 
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];
		boolean success = true;

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard " +
					"configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			// Get the expocode of the cruises to update
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

			DatabaseRequestHandler dbHandler = configStore.getDatabaseRequestHandler();
			DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

			// update each of these cruises
			for ( String expocode : allExpocodes ) {
				System.err.println(expocode + " start");

				CruiseDsgNcFile dsgFile = null;
				// Clear all the WOCE flags in the DSG file
				char[] currentWoceFlags = null;
				try {
					dsgFile = dsgHandler.getDsgNcFile(expocode);
					currentWoceFlags = dsgFile.readCharVarDataValues(CruiseDsgNcFile.WOCECO2WATER_NCVAR_NAME);
				} catch (Exception ex) {
					System.err.println("Error reading the WOCE flags from the full-data DSG file for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}
				for (int k = 0; k < currentWoceFlags.length; k++) {
					currentWoceFlags[k] = WoceEvent.WOCE_NOT_CHECKED;
				}
				try {
					dsgFile.writeCharVarDataValues(CruiseDsgNcFile.WOCECO2WATER_NCVAR_NAME, currentWoceFlags);
				} catch (Exception ex) {
					System.err.println("Error clearing all the WOCE flags in the full-data DSG file for " +
							expocode + " : " + ex.getMessage());
					continue;
				}

				// Assign the applicable WOCE flags given in the database in the time order they were assigned
				ArrayList<WoceEvent> woceList = null;
				try {
					woceList = dbHandler.getWoceEvents(expocode, false);
				} catch (Exception ex) {
					System.err.println("Error reading the database WOCE flags for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}
				try {
					for ( WoceEvent woce : woceList ) {
						// Check if this is an applicable (not old) WOCE flag
						Character flag = woce.getFlag();
						if ( flag.equals(WoceEvent.WOCE_GOOD) ||
							 flag.equals(WoceEvent.WOCE_NOT_CHECKED) ||
							 flag.equals(WoceEvent.WOCE_QUESTIONABLE) ||
							 flag.equals(WoceEvent.WOCE_BAD) ||
							 flag.equals(WoceEvent.WOCE_NO_DATA) ) {
							ArrayList<String> issues = dsgFile.assignWoceFlags(woce);
							for ( String msg : issues )
								System.err.println(msg);
							if ( issues.size() > 0 )
								throw new IllegalArgumentException("Mismatch of WOCE location data");
						}
					}
				} catch (Exception ex) {
					System.err.println("Error reassigning WOCE flags in the full-data DSG file for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}

				// Re-create the decimated-data DSG file 
				try {
					dsgHandler.decimateCruise(expocode);
				} catch (Exception ex) {
					System.err.println("Error regenerating the decimated-data DSG file for " +
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}

				System.err.println(expocode + " success");
			}

			// Notify ERDDAP that DSG files have changed
			dsgHandler.flagErddap(true, true);

		} finally {
			DashboardConfigStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
