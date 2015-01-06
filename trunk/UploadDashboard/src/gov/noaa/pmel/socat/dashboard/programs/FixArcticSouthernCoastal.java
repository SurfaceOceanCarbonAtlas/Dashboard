/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Corrects mistakenly identified Coastal region IDs and QC events for cruises 
 * in the Arctic and Southern Oceans (which do not have coastal regions).
 * 
 * @author Karl Smith
 */
public class FixArcticSouthernCoastal {

	/**
	 * @param args
	 * 		expocodesFile - file of expocodes of cruises to examine for 
	 * 				mistakenly identified Coastal regions in the Arctic 
	 * 				and Southern Oceans
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Corrects the cruises with expocodes in ExpocodesFile for mistakenly ");
			System.err.println("identified Coastal region IDs and QC events.  The default dashboard ");
			System.err.println("configuration is used for this process. ");
			System.err.println();
			System.exit(1);
		}
		String expocodesFilename = args[0];
		boolean success = true;

		// Get the expocodes of the cruises to check and possibly correct
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
			final String regionIdsVarName = Constants.SHORT_NAMES.get(Constants.regionID_VARNAME);
			final String longitudesVarName = Constants.SHORT_NAMES.get(Constants.longitude_VARNAME);
			final String latitudesVarName = Constants.SHORT_NAMES.get(Constants.latitude_VARNAME);
			DsgNcFileHandler dsgHandler = dataStore.getDsgNcFileHandler();
			DatabaseRequestHandler databaseHandler = dataStore.getDatabaseRequestHandler();
			for ( String expocode : allExpocodes ) {
				CruiseDsgNcFile dsgFile = null;
				try {
					dsgFile = dsgHandler.getDsgNcFile(expocode);
				} catch (Exception ex) {
					System.err.println("Problems getting the DSG file for " + expocode);
					ex.printStackTrace();
					System.err.println("===================================================");
					success = false;
					continue;
				}
				// Get the region IDs, longitudes, and latitudes for this cruise

				char[] regionIDs = null;
				try {
					regionIDs = dsgFile.readCharVarDataValues(regionIdsVarName);
				} catch (Exception ex) {
					System.err.println("Problem reading the region IDs from the DSG file for " + expocode);
					ex.printStackTrace();
					System.err.println("===================================================");
					success = false;
					continue;
				}
				int numData = regionIDs.length;
				double[] longitudes = null;
				try {
					longitudes = dsgFile.readDoubleVarDataValues(longitudesVarName);
				} catch (Exception ex) {
					System.err.println("Problem reading longitudes from the DSG file for " + expocode);
					ex.printStackTrace();
					System.err.println("===================================================");
					success = false;
					continue;
				}
				if ( longitudes.length != numData ) {
					System.err.println("Unexpected difference in number of data points: longitudes = " + 
							Integer.toString(longitudes.length) + "; region_ids = " + Integer.toString(numData));
					System.err.println("===================================================");
					success = false;
					continue;
				}
				double[] latitudes = null;
				try {
					latitudes = dsgFile.readDoubleVarDataValues(latitudesVarName);
				} catch (Exception ex) {
					System.err.println("Problem reading latitudes from the DSG file for " + expocode);
					ex.printStackTrace();
					System.err.println("===================================================");
					success = false;
					continue;
				}
				if ( latitudes.length != numData ) {
					System.err.println("Unexpected difference in number of data points: latitudes = " + 
							Integer.toString(latitudes.length) + "; region_ids = " + Integer.toString(numData));
					System.err.println("===================================================");
					success = false;
					continue;
				}
				// Check if there are any mistakenly identified coastal regions
				boolean changed = false;
				boolean hasCoastal = false;
				for (int k = 0; k < numData; k++) {
					if ( DataLocation.COASTAL_REGION_ID.equals(regionIDs[k]) ) {
						if ( latitudes[k] <= -30.0 ) {
							// Should be Southern Ocean
							regionIDs[k] = DataLocation.SOUTHERN_OCEANS_REGION_ID;
							changed = true;
						}
						else if ( (latitudes[k] > 70.0) || ( (latitudes[k] > 66.0) && 
									((longitudes[k] <= -100.0) || (longitudes[k] > 43.0)) ) ) {
							// Should be Arctic
							regionIDs[k] = DataLocation.ARCTIC_REGION_ID;
							changed = true;
						}
						else {
							// Actually is Coastal
							hasCoastal = true;
						}
					}
				}
				if ( changed ) {
					try {
						dsgFile.writeCharVarDataValues(regionIdsVarName, regionIDs);
					} catch (Exception ex) {
						System.err.println("Problem writing the updated region IDs to the DSG file for " + expocode);
						ex.printStackTrace();
						System.err.println("===================================================");
						success = false;
						continue;
					}
					if ( ! hasCoastal ) {
						// All coastal region removed - remove initial Coastal QC flags
						try {
							databaseHandler.removeInitialCoastalQCEvent(expocode);
						} catch (Exception ex) {
							System.err.println("Problem deleting the initial coastal QC flags for " + expocode);
							ex.printStackTrace();
							System.err.println("===================================================");
							success = false;
							continue;
						}
						// TODO: update QC flag in the DSG files
					}
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
