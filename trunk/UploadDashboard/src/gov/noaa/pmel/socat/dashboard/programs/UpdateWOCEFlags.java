/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

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

	private static final String WOCE_CO2WATER_NC_VARNAME = 
			Constants.SHORT_NAMES.get(Constants.woceCO2Water_VARNAME);

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
			configStore = DashboardConfigStore.get();
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
					currentWoceFlags = dsgFile.readCharVarDataValues(WOCE_CO2WATER_NC_VARNAME);
				} catch (Exception ex) {
					System.err.println("Error reading the WOCE flags from the full-data DSG file for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}
				for (int k = 0; k < currentWoceFlags.length; k++) {
					currentWoceFlags[k] = SocatWoceEvent.WOCE_NOT_CHECKED;
				}
				try {
					dsgFile.writeCharVarDataValues(WOCE_CO2WATER_NC_VARNAME, currentWoceFlags);
				} catch (Exception ex) {
					System.err.println("Error clearing all the WOCE flags in the full-data DSG file for " +
							expocode + " : " + ex.getMessage());
					continue;
				}

				// Assign the applicable WOCE flags given in the database in the time order they were assigned
				ArrayList<SocatWoceEvent> woceList = null;
				try {
					woceList = dbHandler.getWoceEvents(expocode, false);
				} catch (Exception ex) {
					System.err.println("Error reading the database WOCE flags for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}
				try {
					for ( SocatWoceEvent woce : woceList ) {
						// Check if this is an applicable (not old) WOCE flag
						Character flag = woce.getFlag();
						if ( flag.equals(SocatWoceEvent.WOCE_GOOD) ||
							 flag.equals(SocatWoceEvent.WOCE_NOT_CHECKED) ||
							 flag.equals(SocatWoceEvent.WOCE_QUESTIONABLE) ||
							 flag.equals(SocatWoceEvent.WOCE_BAD) ||
							 flag.equals(SocatWoceEvent.WOCE_NO_DATA) ) {
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
			configStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
