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

	private static final String regionIdsVarName = Constants.SHORT_NAMES.get(Constants.regionID_VARNAME);
	private static final String longitudesVarName = Constants.SHORT_NAMES.get(Constants.longitude_VARNAME);
	private static final String latitudesVarName = Constants.SHORT_NAMES.get(Constants.latitude_VARNAME);

	/**
	 * Examines the region IDs for a cruise, and changes any that should be Arctic or Southern Oceans.
	 * 
	 * @param dsgFile
	 * 		DSG file to read region IDs, latitudes, and longitudes, and possibly write corrected region IDs
	 * @param descr
	 * 		description of this DSG file for error messages; e.g., "the DSG file for 06AQ20131220". 
	 * @return
	 * 		a pair of integers: the first is the number of incorrect coastal region IDs that were changed,
	 * 		the second is the number of correct coastal region IDs still present.  So if the first number
	 * 		is zero, no region IDs were modified.  If the second number is zero, no data points are in
	 * 		the coastal region (after any corrections).
	 */
	public static int[] updateRegionIDs(CruiseDsgNcFile dsgFile, String descr) {
		// Get the region IDs, longitudes, and latitudes for this cruise
		char[] regionIDs = null;
		try {
			regionIDs = dsgFile.readCharVarDataValues(regionIdsVarName);
		} catch (Exception ex) {
			System.err.println("Problem reading the region IDs from " + descr);
			ex.printStackTrace();
			System.err.println("===================================================");
			return null;
		}
		int numData = regionIDs.length;

		double[] longitudes = null;
		try {
			longitudes = dsgFile.readDoubleVarDataValues(longitudesVarName);
		} catch (Exception ex) {
			System.err.println("Problem reading longitudes from " + descr);
			ex.printStackTrace();
			System.err.println("===================================================");
			return null;
		}
		if ( longitudes.length != numData ) {
			System.err.println("Unexpected difference in number of data points in " + descr + 
					": longitudes = " + Integer.toString(longitudes.length) + 
					"; region_ids = " + Integer.toString(numData));
			System.err.println("===================================================");
			return null;
		}

		double[] latitudes = null;
		try {
			latitudes = dsgFile.readDoubleVarDataValues(latitudesVarName);
		} catch (Exception ex) {
			System.err.println("Problem reading latitudes from " + descr);
			ex.printStackTrace();
			System.err.println("===================================================");
			return null;
		}
		if ( latitudes.length != numData ) {
			System.err.println("Unexpected difference in number of data points in " + descr + 
					": latitudes = " + Integer.toString(latitudes.length) + 
					"; region_ids = " + Integer.toString(numData));
			System.err.println("===================================================");
			return null;
		}

		// Check if there are any mistakenly identified coastal regions
		int numChanged = 0;
		int numCoastal = 0;
		for (int k = 0; k < numData; k++) {
			if ( DataLocation.COASTAL_REGION_ID.equals(regionIDs[k]) ) {
				if ( latitudes[k] <= -30.0 ) {
					// Should be Southern Ocean
					regionIDs[k] = DataLocation.SOUTHERN_OCEANS_REGION_ID;
					numChanged++;
				}
				else if ( (latitudes[k] > 70.0) || ( (latitudes[k] > 66.0) && 
							((longitudes[k] <= -100.0) || (longitudes[k] > 43.0)) ) ) {
					// Should be Arctic
					regionIDs[k] = DataLocation.ARCTIC_REGION_ID;
					numChanged++;
				}
				else {
					// Actually is Coastal
					numCoastal++;
				}
			}
		}
		if ( numChanged > 0 ) {
			try {
				dsgFile.writeCharVarDataValues(regionIdsVarName, regionIDs);
			} catch (Exception ex) {
				System.err.println("Problem writing the updated region IDs to " + descr);
				ex.printStackTrace();
				System.err.println("===================================================");
				return null;
			}
		}
		System.err.println("Modified " + Integer.toString(numChanged) + " region IDs in " + descr);
		return new int[] {numChanged, numCoastal};
	}

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

				int[] retVal = updateRegionIDs(dsgFile, "the DSG file for " + expocode);
				if ( retVal == null ) {
					// Error message already printed
					success = false;
					continue;
				}
				int numChanged = retVal[0];
				int numCoastal = retVal[1];
				
				Character newFlag = null;
				if ( (numChanged > 0) && (numCoastal == 0) ) {
					// Had incorrect coastal region IDs and all were removed - remove initial Coastal QC flags
					try {
						int numDeleted = databaseHandler.removeInitialCoastalQCEvent(expocode);
						System.err.println("Deleted " + Integer.toString(numDeleted) + " coastal QC flags for " + expocode);
					} catch (Exception ex) {
						System.err.println("Problem deleting the initial coastal QC flags for " + expocode);
						ex.printStackTrace();
						System.err.println("===================================================");
						success = false;
						continue;
					}
					// Get and assign the updated overall QC flag
					try {
						newFlag = databaseHandler.getQCFlag(expocode);
					} catch (Exception ex) {
						System.err.println("Problem getting the new overall QC flag for " + expocode);
						ex.printStackTrace();
						System.err.println("===================================================");
						success = false;
						continue;
					}
					try {
						dsgFile.updateQCFlag(newFlag);
					} catch (Exception ex) {
						System.err.println("Problem assigning the overall QC flag in the DSG file for " + expocode);
						ex.printStackTrace();
						System.err.println("===================================================");
						success = false;
						continue;
					}
				}
				
				if ( numChanged > 0 ) {
					CruiseDsgNcFile decDsgFile = null;
					// Fix any incorrect coastal region IDs that might be in the decimated DSG file
					try {
						decDsgFile = dsgHandler.getDecDsgNcFile(expocode);
					} catch (Exception ex) {
						System.err.println("Problems getting the decimated DSG file for " + expocode);
						ex.printStackTrace();
						System.err.println("===================================================");
						success = false;
						continue;
					}
					retVal = updateRegionIDs(decDsgFile, "the decimated DSG file for " + expocode);
					if ( retVal == null ) {
						// Error message already printed
						success = false;
						continue;
					}
					if ( newFlag != null ) {
						// Assign the updated overall QC flag to the decimated data file
						try {
							decDsgFile.updateQCFlag(newFlag);
						} catch (Exception ex) {
							System.err.println("Problem assigning the overall QC flag in the decimated DSG file for " + expocode);
							ex.printStackTrace();
							System.err.println("===================================================");
							success = false;
							continue;
						}
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
