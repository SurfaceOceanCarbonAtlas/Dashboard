/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.nc;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.ome.OmeMetadata;
import gov.noaa.pmel.socat.dashboard.server.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

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

	File dsgFilesDir;
	File decDsgFilesDir;

	/**
	 * Handles storage and retrieval of full and decimated NetCDF DSG files 
	 * under the given directories.
	 * 
	 * @param dsgFilesDirName
	 * 		name of the directory for the full NetCDF DSG files
	 * @param decDsgFilesDirName
	 * 		name of the directory for the decimated NetCDF DSG files
	 * @throws IllegalArgumentException
	 * 		if the specified directories do not exist,
	 * 		or are not directories
	 */
	public DsgNcFileHandler(String dsgFilesDirName, String decDsgFilesDirName) {
		dsgFilesDir = new File(dsgFilesDirName);
		if ( ! dsgFilesDir.isDirectory() )
			throw new IllegalArgumentException(
					dsgFilesDirName + " is not a directory");
		decDsgFilesDir = new File(decDsgFilesDirName);
		if ( ! decDsgFilesDir.isDirectory() )
			throw new IllegalArgumentException(
					decDsgFilesDirName + " is not a directory");
	}

	/**
	 * Generates the cruise-specific full NetCDF DSG abstract file for a cruise.
	 * 
	 * @param expocode
	 * 		expocode of the cruise 
	 * @return
	 * 		full NetCDF DSG abstract file for the cruise
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 */
	public File getDsgNcFile(String expocode) throws IllegalArgumentException {
		// Check and standardize the expocode
		String expo = CruiseFileHandler.checkExpocode(expocode);
		// Generate the full path filename for this cruise NetCDF DSG
		File dsgNcFile = new File(dsgFilesDir, expo.substring(0,4) +
				File.separator + expo + ".nc");
		return dsgNcFile;
	}

	/**
	 * Generates the cruise-specific decimated NetCDF DSG abstract file for a cruise.
	 * 
	 * @param expocode
	 * 		expocode of the cruise
	 * @return
	 * 		decimated NetCDF DSG abstract file for the cruise
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 */
	public File getDecDsgNcFile(String expocode) throws IllegalArgumentException {
		// Check and standardize the expocode
		String expo = CruiseFileHandler.checkExpocode(expocode);
		// Generate the full path filename for this cruise NetCDF DSG
		File decDsgNcFile = new File(decDsgFilesDir, expo.substring(0,4) +
				File.separator + expo + ".nc");
		return decDsgNcFile;
	}

	/**
	 * Saves the cruise OME metadata and cruise data into a new full-data 
	 * NetCDF DSG file.
	 * 
	 * @param omeMData
	 * 		metadata for the cruise
	 * @param cruiseData
	 * 		data for the cruise
	 * @param qcFlag
	 * 		cruise QC flag to assign
	 * @throws IllegalArgumentException
	 * 		if there are problems with the metadata or data given, or
	 * 		if there are problems creating or writing the full-data DSG file
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
		tool.init(dsgFile.getPath(), null, cruiseData.getExpocode(), FerretConfig.Action.COMPUTE);
		tool.run();
		if ( tool.hasError() )
			throw new IllegalArgumentException("Failure adding computed variables: " + 
					tool.getErrorMessage());

		// TODO: ? archive ncdump of the NetCDF DSG file ?
	}

	/**
	 * Generates the decimated-data NetCDF DSG file from the full-data NetCDF DSG file.
	 * 
	 * @param expocode
	 * 		generate the decimated-data DSG file for the dataset with this expocode
	 * @throws IllegalArgumentException
	 * 		if there are problems reading the full-data DSG file, or
	 * 		if there are problems creating or writing the decimated-data DSG file
	 */
	public void decimateCruise(String expocode) throws IllegalArgumentException {
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected failure to obtain the dashboard data store");
		}

		// Get the location and name of the full DSG file
		File dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.canRead() )
			throw new IllegalArgumentException(
					"Full DSG file for " + expocode + " does not exist");

		// Get the location and name for the decimated DSG file
		File decDsgFile = getDecDsgNcFile(expocode);
		// Make sure the parent directory exists
		File parentDir = decDsgFile.getParentFile();
		if ( ! parentDir.exists() ) {
			if ( ! parentDir.mkdirs() )
				throw new IllegalArgumentException(
						"Unexpected problems creating the new subdirectory " + 
						parentDir.getPath());
		}

		// Call Ferret to create the decimated DSG file from the full DSG file
		SocatTool tool = new SocatTool(dataStore.getFerretConfig());
		tool.init(dsgFile.getPath(), decDsgFile.getPath(), expocode, FerretConfig.Action.DECIMATE);
		tool.run();
		if ( tool.hasError() )
			throw new IllegalArgumentException("Failure decimating the full DSG file: " + 
					tool.getErrorMessage());
	}

	/**
	 * Update the WOCE flags in the full DSG file, as well as in the given temporary
	 * DSG file.  In the process, complete some of the missing data in WOCE event
	 * (row number, region ID, data type).
	 * 
	 * @param woceEvent
	 * 		WOCE event to use; the expocode is used to identify the full dataset to update
	 * @param tempDsgFile
	 * 		temporary DSG file to also update
	 */
	public void updateWoceFlags(SocatWoceEvent woceEvent, File tempDsgFile) {
		// TODO:
		throw new RuntimeException("not implemented");
	}
}
