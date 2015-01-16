/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Updates the QC flags in the full-data and decimated-data DSG files for cruises
 * to the flag obtained from the database.
 * 
 * @author Karl Smith
 */
public class UpdateQCFlags {

	/**
	 * @param args
	 * 		ExpocodesFile - update dashboard status of these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Updates the QC flags in the full-data and decimated-data DSG ");
			System.err.println("files for cruises specified in ExpocodesFile to the flag obtained ");
			System.err.println("from the database.  The default dashboard configuration is ");
			System.err.println("used for this process. "); 
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];

		boolean success = true;
		boolean updated = false;

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

			DatabaseRequestHandler dbHandler = dataStore.getDatabaseRequestHandler();
			DsgNcFileHandler dsgHandler = dataStore.getDsgNcFileHandler();

			// update each of these cruises
			for ( String expocode : allExpocodes ) {
				Character qcFlag;
				try {
					qcFlag = dbHandler.getQCFlag(expocode);
				} catch (Exception ex) {
					System.err.println("Error getting the database QC flag for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}
				char oldFlag;
				try {
					oldFlag = dsgHandler.getQCFlag(expocode);
				} catch (Exception ex) {
					System.err.println("Error reading the current DSG QC flag for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}
				try {
					if ( ! qcFlag.equals(oldFlag) ) {
						dsgHandler.getDsgNcFile(expocode).updateQCFlag(qcFlag);
						dsgHandler.getDecDsgNcFile(expocode).updateQCFlag(qcFlag);
						updated = true;
					}
				} catch (Exception ex) {
					System.err.println("Error updating the QC flag in the DSG files for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}
				System.err.println("Updated dashboard status for " + 
						expocode + " to '" + qcFlag + "'");
			}
			if ( updated && success ) {
				dsgHandler.flagErddap(true, true);
			}
		} finally {
			dataStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
