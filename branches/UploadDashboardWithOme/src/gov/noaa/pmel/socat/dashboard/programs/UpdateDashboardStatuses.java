/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Updates the cruise dashboard status from the current QC flag in the full-data DSG file.
s * 
 * @author Karl Smith
 */
public class UpdateDashboardStatuses {

	/**
	 * @param args
	 * 		ExpocodesFile - update dashboard status of these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Updates the cruise dashboard status from the current QC flag ");
			System.err.println("in the full-data DSG file for cruises specified in ExpocodesFile. "); 
			System.err.println("The default dashboard configuration is used for this process. "); 
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

			DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
			CruiseFileHandler fileHandler = configStore.getCruiseFileHandler();

			// update each of these cruises
			for ( String expocode : allExpocodes ) {
				char qcFlag;
				try {
					qcFlag = dsgHandler.getDsgNcFile(expocode).getQCFlag();
				} catch (Exception ex) {
					System.err.println("Error reading the QC flag for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}
				try {
					if ( fileHandler.updateCruiseDashboardStatus(expocode, qcFlag) ) {
						System.err.println("Updated dashboard status for " + 
								expocode + " to that for QC flag '" + qcFlag + "'");
					}
				} catch (Exception ex) {
					System.err.println("Error updating the dashboard status for " + 
							expocode + " : " + ex.getMessage());
					success = false;
					continue;
				}
			}
		} finally {
			configStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
