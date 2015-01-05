/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Updates the cruise dashboard status from the current QC flag in the full-data 
 * DSG file for cruises specified in ExpocodesFile, or all cruises if ExpocodesFile 
 * is '-'. The default dashboard configuration is used for this process. 
 * 
 * @author Karl Smith
 */
public class UpdateDashboardStatuses {

	/**
	 * @param args
	 * 		ExpocodesFile - update dashboard status of these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  [ - | ExpocodesFile ]");
			System.err.println();
			System.err.println("Updates the cruise dashboard status from the current QC flag in the full-data ");
			System.err.println("DSG file for cruises specified in ExpocodesFile, or all cruises if ExpocodesFile "); 
			System.err.println("is '-'. The default dashboard configuration is used for this process. "); 
			System.err.println();
			System.exit(1);
		}

		String expocodesFilename = args[0];
		if ( "-".equals(expocodesFilename) )
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
			// Get the expocode of the cruises to update
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

			DsgNcFileHandler dsgHandler = dataStore.getDsgNcFileHandler();
			CruiseFileHandler fileHandler = dataStore.getCruiseFileHandler();

			// update each of these cruises
			for ( String expocode : allExpocodes ) {
				char qcFlag;
				try {
					qcFlag = dsgHandler.getDsgNcFile(expocode).getQCFlag();
				} catch (Exception ex) {
					System.err.println("Error reading the QC flag for " + expocode + 
							" : " + ex.getMessage());
					success = false;
					continue;
				}
				try {
					fileHandler.updateCruiseDashboardStatus(expocode, qcFlag);
				} catch (Exception ex) {
					System.err.println("Error updating the QC flag for " + expocode + 
							" : " + ex.getMessage());
					success = false;
					continue;
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
