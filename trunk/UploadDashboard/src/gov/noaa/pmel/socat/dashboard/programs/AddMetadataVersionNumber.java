/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Adds v1, v2, or v3 SOCAT version numbers to metadata documents.
 * 
 * @author Karl Smith
 */
public class AddMetadataVersionNumber {

	/**
	 * @param args
	 * 		ExpocodesFile - update metadata and additional documents 
	 * 						version numbers for these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println();
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Updates the metadata and additional documents version numbers ");
			System.err.println("for the cruises specified in ExpocodesFile.  The default "); 
			System.err.println("dashboard configuration is used for this process. ");
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
			System.err.println("Problems reading the default dashboard "
					+ "configuration file: " + ex.getMessage());
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

			CruiseFileHandler fileHandler = dataStore.getCruiseFileHandler();
			MetadataFileHandler metaHandler = dataStore.getMetadataFileHandler();
			for ( String expocode : allExpocodes ) {
				try {
					DashboardCruise cruise = fileHandler.getCruiseFromInfoFile(expocode);
					if ( cruise == null ) {
						System.err.println("No dataset with the expocode " + expocode);
						success = false;
						continue;
					}
					String socatVersion = cruise.getVersion();
					for ( DashboardMetadata mdata : metaHandler.getMetadataFiles(expocode) ) {
						String myVersion;
						String timestamp = mdata.getUploadTimestamp();
						if ( timestamp.startsWith("2014-12-19 14") ) {
							// Metadata uploaded from v2 links
							myVersion = socatVersion;
						}
						else {
							// Metadata uploaded/updated in v3 (data may still be an earlier version)
							myVersion = "3.0";
						}
						mdata.setVersion(myVersion);
						metaHandler.saveMetadataInfo(mdata, null);
						System.err.println("Metadata version for " + expocode + "_" + 
								mdata.getFilename() + " set to " + myVersion);
					}
				} catch (Exception ex) {
					System.err.println("Problems working with " + expocode);
					ex.printStackTrace();
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
