package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

public class CruisePreviewer {

	File dsgFilesDir;
	File plotsDir;

	/**
	 * Create with the given directories for the preview DSG files and plots.
	 * 
	 * @param dsgFilesDirName
	 * 		directory to contain the preview DSG files
	 * @param plotsDirName
	 * 		directory to contain the preview plots
	 */
	public CruisePreviewer(String dsgFilesDirName, String plotsDirName) {
		dsgFilesDir = new File(dsgFilesDirName);
		if ( ! dsgFilesDir.isDirectory() )
			throw new IllegalArgumentException(dsgFilesDirName + " is not a directory");
		plotsDir = new File(plotsDirName);
		if ( ! plotsDir.isDirectory() )
			throw new IllegalArgumentException(plotsDirName + " is not a directory");
	}

	/**
	 * Generates the preview NetCDF DSG abstract file for a cruise.
	 * 
	 * @param expocode
	 * 		expocode of the cruise 
	 * @return
	 * 		preview NetCDF DSG abstract file for the cruise
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 */
	public CruiseDsgNcFile getPreviewDsgFile(String expocode) throws IllegalArgumentException {
		// Check and standardize the expocode
		String expo = DashboardServerUtils.checkExpocode(expocode);
		return new CruiseDsgNcFile(dsgFilesDir + File.separator + expo.substring(0,4) +
				File.separator + expo + ".nc");
	}

	/**
	 * Generates the preview plots directory for a cruise.
	 * Creates this directory if it does not exist.
	 * 
	 * @param expocode
	 * 		expocode of the cruise
	 * @return
	 * 		preview plots directory for the cruise
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid, or
	 * 		if unable to create the 
	 */
	public File getCruisePreviewPlotsDir(String expocode) throws IllegalArgumentException {
		String expo = DashboardServerUtils.checkExpocode(expocode);
		File cruisePlotsDir = new File(plotsDir + File.separator + expo.substring(0,4));
		if ( cruisePlotsDir.exists() ) {
			if ( ! cruisePlotsDir.isDirectory() )
				throw new IllegalArgumentException("Plots directory exists "
						+ "but is not a directory: " + cruisePlotsDir.getPath()); 
		}
		else if ( ! cruisePlotsDir.mkdirs() ) {
				throw new IllegalArgumentException("Unexpected problems "
						+ "creating the new subdirectory " + cruisePlotsDir.getPath());
		}
		return cruisePlotsDir;
	}

	/**
	 * Generates the data preview plots for the given cruise.  The cruise 
	 * data is checked and standardized, the preview DSG file is created,
	 * Ferret is called to add the computed variables to the DSG file, and 
	 * finally Ferret is called to generate the data preview plots from the 
	 * data in the DSG file.
	 * 
	 * @param expocode
	 * 		expocode of the cruise to preview
	 * @param timetag
	 * 		time tag to add to the end of the names of the plots 
	 * 		(before the filename extension)
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid,
	 * 		if there is an error reading the data or OME metadata,
	 * 		if there is an error checking and standardizing the data,
	 * 		if there is an error generating the preview DSG file, or
	 * 		if there is an error generating the preview data plots 
	 */
	public void createPreviewPlots(String expocode, String timetag) 
			throws IllegalArgumentException {
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);

		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected failure to obtain the dashboard data store");
		}

		// Get the complete original cruise data
		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		DashboardCruiseWithData cruiseData = cruiseHandler.getCruiseDataFromFiles(upperExpo, 0, -1);

		/*
		 *  Convert the cruise data into standard units and setting to null
		 *  those data lines which the PI has marked as bad.  
		 *  Also adds and assigns year, month, day, hour, minute, second, 
		 *  and WOCE columns if not present.  SanityChecker WOCE-4 flags
		 *  are added to the WOCE column.
		 *  Note: this saves messages and assigns WOCE flags with row 
		 *  numbers of the trimmed data.
		 */
		DashboardCruiseChecker cruiseChecker = dataStore.getDashboardCruiseChecker();
		if ( ! cruiseChecker.standardizeCruiseData(cruiseData) ) {
			if ( cruiseData.getNumDataRows() < 1 )
				throw new IllegalArgumentException(upperExpo + ": unacceptable; all data points marked bad");
			else if (  ! cruiseChecker.checkProcessedOkay() )
				throw new IllegalArgumentException(upperExpo + ": unacceptable; automated checking of data failed");
			else if ( cruiseChecker.hadGeopositionErrors() )
				throw new IllegalArgumentException(upperExpo + ": unacceptable; automated checking of data " +
						"detected longitude, latitude, date, or time value errors");
			else
				throw new IllegalArgumentException(upperExpo + ": unacceptable for unknown reason - unexpected");
		}

		// Get the OME metadata for this cruise
		MetadataFileHandler metadataHandler = dataStore.getMetadataFileHandler();
		DashboardMetadata omeInfo = metadataHandler.getMetadataInfo(upperExpo, DashboardMetadata.OME_FILENAME);
		DashboardOmeMetadata omeMData = new DashboardOmeMetadata(omeInfo, metadataHandler);

		// Get the location and name for the NetCDF DSG file
		CruiseDsgNcFile dsgFile = getPreviewDsgFile(upperExpo);

		// Make sure the parent directory exists
		File parentDir = dsgFile.getParentFile();
		if ( ! parentDir.exists() ) {
			if ( ! parentDir.mkdirs() ) {
				throw new IllegalArgumentException(
						"Unexpected problems creating the new subdirectory " + parentDir.getPath());
			}
		}

		// Get just the filenames from the set of addition document
		TreeSet<String> addlDocs = new TreeSet<String>();
		for ( String docInfo : cruiseData.getAddlDocs() ) {
			addlDocs.add(DashboardMetadata.splitAddlDocsTitle(docInfo)[0]);
		}
		// Get the metadata needed for creating the DSG file
		SocatMetadata socatMData = omeMData.createSocatMetadata(
				cruiseData.getVersion(), addlDocs, SocatQCEvent.QC_PREVIEW_FLAG.toString());
		// Convert the cruise data strings into the appropriate type
		ArrayList<SocatCruiseData> socatDatalist = 
				SocatCruiseData.dataListFromDashboardCruise(cruiseData);

		// Create the preview NetCDF DSG file
		try {
			dsgFile.create(socatMData, socatDatalist);
		} catch (Exception ex) {
			dsgFile.delete();
			throw new IllegalArgumentException("Problems creating the SOCAT DSG file " + dsgFile.getName() +
					"\n    " + ex.getMessage(), ex);
		}

		// Call Ferret to add the computed variables to the preview DSG file
		SocatTool tool = new SocatTool(dataStore.getFerretConfig());
		ArrayList<String> scriptArgs = new ArrayList<String>(1);
		scriptArgs.add(dsgFile.getPath());
		tool.init(scriptArgs, upperExpo, FerretConfig.Action.COMPUTE);
		tool.run();
		if ( tool.hasError() )
			throw new IllegalArgumentException("Failure adding computed variables: " + 
					tool.getErrorMessage());

		File plotsDir = getCruisePreviewPlotsDir(upperExpo);

		// Call Ferret to generate the plots from the preview DSG file
		tool = new SocatTool(dataStore.getFerretConfig());
		scriptArgs.add(plotsDir.getPath());
		scriptArgs.add(timetag);
		tool.init(scriptArgs, upperExpo, FerretConfig.Action.PLOTS);
		tool.run();
		if ( tool.hasError() )
			throw new IllegalArgumentException("Failure generating data preview plots: " + 
					tool.getErrorMessage());
	}

}
