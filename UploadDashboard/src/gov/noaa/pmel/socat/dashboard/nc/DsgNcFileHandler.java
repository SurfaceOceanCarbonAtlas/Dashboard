/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.nc;

import gov.noaa.pmel.socat.dashboard.server.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.server.OmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;

import java.io.File;
import java.util.ArrayList;

/**
 * NetCDF DSG file handler for the SOCAT upload dashboard.
 * 
 * @author Karl Smith
 */
public class DsgNcFileHandler {

	File filesDir;

	/**
	 * Handles storage and retrieval of NetCDF DSG files 
	 * under the given directory.
	 * 
	 * @param filesDirName
	 * 		name of the directory for the NetCDF DSG files
	 * @throws IllegalArgumentException
	 * 		if the specified directory does not exist,
	 * 		or is not a directory
	 */
	public DsgNcFileHandler(String filesDirName) {
		filesDir = new File(filesDirName);
		// Check that this is a directory
		if ( ! filesDir.isDirectory() )
			throw new IllegalArgumentException(
					filesDirName + " is not a directory");
	}

	/**
	 * Generates the cruise-specific NetCDF DSG file for a cruise.
	 * 
	 * @param expocode
	 * 		expocode of the cruise associated with this metadata document
	 * @return
	 * 		NetCDF DSG file for the cruise
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 */
	public File getDsgNcFile(String expocode) throws IllegalArgumentException {
		// Check and standardize the expocode
		String expo = CruiseFileHandler.checkExpocode(expocode);
		// Generate the full path filename for this cruise NetCDF DSG
		File dsgNcFile = new File(filesDir, expo.substring(0,4) +
				File.separator + expo + ".nc");
		return dsgNcFile;
	}

	/**
	 * Saves the cruise OME metadata and cruise data into a new NetCDF DSG file.
	 * 
	 * @param omeMData
	 * 		metadata for the cruise
	 * @param cruiseData
	 * 		data for the cruise
	 * @throws IllegalArgumentException
	 * 		if there are problems with the metadata or data given, or
	 * 		if there are problems creating or writing the NetCDF DSG file
	 */
	public void saveCruise(OmeMetadata omeMData, DashboardCruiseWithData cruiseData)
											throws IllegalArgumentException {
		// Get the location and name for the NetCDF DSG file
		File dsgFile = getDsgNcFile(omeMData.getExpocode());

		// Make sure the parent directory exists
		File parentDir = dsgFile.getParentFile();
		if ( ! parentDir.exists() ) {
			if ( ! parentDir.mkdirs() )
				throw new IllegalArgumentException(
						"Unexpected problems creating the new subdirectory " + 
						parentDir.getPath());
		}

		// Get the metadata needed for creating the DSG file
		SocatMetadata socatMData = new SocatMetadata(omeMData);
		// Convert the cruise data strings into the appropriate type
		ArrayList<SocatCruiseData> socatDatalist = 
				SocatCruiseData.dataListFromDashboardCruise(cruiseData);
		// Create the NetCDF DSG file
		try {
			CruiseDsgNcFile cruiseFile = 
					new CruiseDsgNcFile(socatMData, socatDatalist);
			cruiseFile.create(dsgFile.getPath());
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					"Problems creating the SOCAT DSG file " + dsgFile.getName() +
					"\n    " + ex.getMessage(), ex);
		}
	}
	
}
