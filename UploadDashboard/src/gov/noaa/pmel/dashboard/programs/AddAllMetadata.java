/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Updates the metadata and additional documents for specified cruises 
 * from the current set of available documents for each cruise.
 *  
 * @author Karl Smith
 */
public class AddAllMetadata {

	/**
	 * @param args
	 * 		ExpocodesFile - update metadata and additional documents 
	 * 						for these cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println();
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Updates the metadata and additional documents for the cruises ");
			System.err.println("specified in ExpocodesFile from the current set of available "); 
			System.err.println("documents for each cruise.  The default dashboard configuration "); 
			System.err.println("is used for this process. ");
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

			CruiseFileHandler fileHandler = configStore.getCruiseFileHandler();
			MetadataFileHandler metaHandler = configStore.getMetadataFileHandler();
			for ( String expocode : allExpocodes ) {
				try {
					DashboardCruise cruise = fileHandler.getCruiseFromInfoFile(expocode);
					if ( cruise == null ) {
						System.err.println("No dataset with the expocode " + expocode);
						success = false;
						continue;
					}
					// Directly manipulate the set of additional documents for the cruise
					TreeSet<String> addlDocs = cruise.getAddlDocs();
					// Clear the OME upload timestamp and current additional documents
					cruise.setOmeTimestamp(null);
					addlDocs.clear();
					// And add all existing metadata documents
					for ( DashboardMetadata mdata : metaHandler.getMetadataFiles(expocode) ) {
						if ( mdata.getFilename().equals(DashboardUtils.OME_FILENAME) ) {
							// Ignore the OME.xml stub
							;
						}
						else if ( mdata.getFilename().equals(DashboardUtils.PI_OME_FILENAME) ) {
							// PI-provided OME.xml file - set the OME upload timestamp
							cruise.setOmeTimestamp(mdata.getUploadTimestamp());
						}
						else {
							// Add the "filename ; timestamp" additional document string
							addlDocs.add(mdata.getAddlDocsTitle());
						}
					}
					// Save the updated additional documents/OME timestamp for the cruise
					// but do not commit the change - to be done manually
					fileHandler.saveCruiseInfoToFile(cruise, null);
					System.err.println("Documents updated for " + expocode);
				} catch (Exception ex) {
					System.err.println("Problems working with " + expocode);
					ex.printStackTrace();
					success = false;
					continue;
				}
			}
		} finally {
			DashboardConfigStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
