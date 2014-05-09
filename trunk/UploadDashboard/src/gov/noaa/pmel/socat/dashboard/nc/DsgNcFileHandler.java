/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.nc;

import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.ome.OmeMetadata;
import gov.noaa.pmel.socat.dashboard.server.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

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
	 * @param qcFlag
	 * 		cruise QC flag to assign
	 * @throws IllegalArgumentException
	 * 		if there are problems with the metadata or data given, or
	 * 		if there are problems creating or writing the NetCDF DSG file
	 */
	public void saveCruise(OmeMetadata omeMData, DashboardCruiseWithData cruiseData, 
									String qcFlag) throws IllegalArgumentException {
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected failure to obtain the dashboard data store");
		}

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

		// Get just the filenames from the set of addition document
		TreeSet<String> addlDocs = new TreeSet<String>();
		for ( String docInfo : cruiseData.getAddlDocs() ) {
			addlDocs.add(DashboardMetadata.splitAddlDocsTitle(docInfo)[0]);
		}
		// Get the metadata needed for creating the DSG file
		SocatMetadata socatMData = omeMData.createSocatMetadata(
				cruiseData.getVersion(), addlDocs, qcFlag);
		// Convert the cruise data strings into the appropriate type
		ArrayList<SocatCruiseData> socatDatalist = 
				SocatCruiseData.dataListFromDashboardCruise(cruiseData);
		// Create the NetCDF DSG file
		CruiseDsgNcFile cruiseFile = new CruiseDsgNcFile(socatMData, socatDatalist);
		try {
			cruiseFile.create(dsgFile.getPath());
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					"Problems creating the SOCAT DSG file " + dsgFile.getName() +
					"\n    " + ex.getMessage(), ex);
		}

		// Call Ferret to add the computed variables to the NetCDF DSG file
		SocatTool tool = new SocatTool(dataStore.getFerretConfig());
		tool.init(dsgFile.getPath(), cruiseData.getExpocode());
		tool.run();
		if ( tool.hasError() )
			throw new IllegalArgumentException("Failure adding computed variables: " + 
					tool.getErrorMessage());

		// TODO: ? archive ncdump of the NetCDF DSG file ?
	}
	
}
