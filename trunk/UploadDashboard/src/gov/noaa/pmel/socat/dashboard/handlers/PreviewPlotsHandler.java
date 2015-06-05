package gov.noaa.pmel.socat.dashboard.handlers;

import gov.noaa.pmel.socat.dashboard.actions.CruiseChecker;
import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class PreviewPlotsHandler {

	File dsgFilesDir;
	File plotsDir;
	CruiseFileHandler cruiseHandler;
	CruiseChecker cruiseChecker;
	MetadataFileHandler metadataHandler;
	FerretConfig ferretConfig;

	/**
	 * Create with the given directories for the preview DSG files and plots.
	 * 
	 * @param dsgFilesDirName
	 * 		directory to contain the preview DSG files
	 * @param plotsDirName
	 * 		directory to contain the preview plots
	 * @param configStore
	 * 		get the CruiseFileHandler, CruiseChecker, 
	 * 		MetadataFileHandler, and FerretConfig from here
	 */
	public PreviewPlotsHandler(String dsgFilesDirName, String plotsDirName, DashboardConfigStore configStore) {
		dsgFilesDir = new File(dsgFilesDirName);
		if ( ! dsgFilesDir.isDirectory() )
			throw new IllegalArgumentException(dsgFilesDirName + " is not a directory");
		plotsDir = new File(plotsDirName);
		if ( ! plotsDir.isDirectory() )
			throw new IllegalArgumentException(plotsDirName + " is not a directory");
		cruiseHandler = configStore.getCruiseFileHandler();
		cruiseChecker = configStore.getDashboardCruiseChecker();
		metadataHandler = configStore.getMetadataFileHandler();
		ferretConfig = configStore.getFerretConfig();
	}

	/**
	 * Generates the preview DSG files for a cruise.
	 * Creates this directory if it does not exist.
	 * 
	 * @param expocode
	 * 		expocode of the cruise 
	 * @return
	 * 		DSG files directory for the cruise
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid, 
	 * 		if problems creating the directory
	 */
	public File getCruisePreviewDsgDir(String expocode) throws IllegalArgumentException {
		// Check and standardize the expocode
		String expo = DashboardServerUtils.checkExpocode(expocode);
		// Make sure the DSG subdirectory exists
		File cruiseDsgDir = new File(dsgFilesDir, expo.substring(0,4));
		if ( cruiseDsgDir.exists() ) {
			if ( ! cruiseDsgDir.isDirectory() ) {
				throw new IllegalArgumentException("Cruise DSG file subdirectory exists "
						+ "but is not a directory: " + cruiseDsgDir.getPath()); 
			}
		}
		else if ( ! cruiseDsgDir.mkdirs() ) {
			throw new IllegalArgumentException("Cannot create the cruise DSG file "
					+ "subdirectory " + cruiseDsgDir.getPath());
		}
		return cruiseDsgDir;
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
	 * 		if problems creating the directory
	 */
	public File getCruisePreviewPlotsDir(String expocode) throws IllegalArgumentException {
		// Check and standardize the expocode
		String expo = DashboardServerUtils.checkExpocode(expocode);
		// Make sure the plots subdirectory exists
		File cruisePlotsDir = new File(plotsDir, expo.substring(0,4));
		if ( cruisePlotsDir.exists() ) {
			if ( ! cruisePlotsDir.isDirectory() ) {
				throw new IllegalArgumentException("Plots directory exists "
						+ "but is not a directory: " + cruisePlotsDir.getPath());
			}
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
		Logger log = Logger.getLogger("PreviewPlotsHandler");
		log.debug("reading data for " + upperExpo);

		// Get the complete original cruise data
		DashboardCruiseWithData cruiseData = cruiseHandler.getCruiseDataFromFiles(upperExpo, 0, -1);

		log.debug("standardizing data for " + upperExpo);

		/*
		 *  Convert the cruise data into standard units and removes
		 *  those data lines which the PI has marked as bad.  
		 *  Also adds and assigns year, month, day, hour, minute, second, 
		 *  and WOCE columns if not present.  SanityChecker WOCE-4 flags
		 *  are added to the WOCE column.  This updates the OME metadata
		 *  from the SanityChecker, and saves SanityChecker messages with 
		 *  row numbers of the trimmed data.
		 */
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

		// Get the preview DSG filename, creating the parent directory if it does not exist
		CruiseDsgNcFile dsgFile = new CruiseDsgNcFile(
				getCruisePreviewDsgDir(upperExpo), upperExpo + "_" + timetag + ".nc");

		log.debug("generating preview DSG file " + dsgFile.getPath());

		// Do not use the metadata in the DSG file, and to avoid issues with the existing
		// OME metadata, just use what we already know to create a SocatMetadata
		SocatMetadata socatMData = new SocatMetadata();
		socatMData.setExpocode(upperExpo);
		socatMData.setSocatVersion(cruiseData.getVersion());
		socatMData.setQcFlag(SocatQCEvent.QC_PREVIEW_FLAG.toString());

		// Convert the cruise data strings into the appropriate list of data objects
		ArrayList<SocatCruiseData> socatDatalist = SocatCruiseData.dataListFromDashboardCruise(cruiseData);

		// Create the preview NetCDF DSG file
		try {
			dsgFile.create(socatMData, socatDatalist);
		} catch (Exception ex) {
			dsgFile.delete();
			throw new IllegalArgumentException("Problems creating the SOCAT DSG file " + dsgFile.getName() +
					"\n    " + ex.getMessage(), ex);
		}

		log.debug("adding computed variables to preview DSG file " + dsgFile.getPath());

		// Call Ferret to add the computed variables to the preview DSG file
		SocatTool tool = new SocatTool(ferretConfig);
		ArrayList<String> scriptArgs = new ArrayList<String>(1);
		scriptArgs.add(dsgFile.getPath());
		tool.init(scriptArgs, upperExpo, FerretConfig.Action.COMPUTE);
		tool.run();
		if ( tool.hasError() )
			throw new IllegalArgumentException("Failure adding computed variables: " + 
					tool.getErrorMessage());

		log.debug("generating preview plots for " + dsgFile.getPath());

		// Get the location for the preview plots, creating the driectory if it does not exist
		String cruisePlotsDirname = getCruisePreviewPlotsDir(upperExpo).getPath();

		// Call Ferret to generate the plots from the preview DSG file; 
		// the plots parent directory is created if it does not exist
		tool = new SocatTool(ferretConfig);
		scriptArgs.add(cruisePlotsDirname);
		scriptArgs.add(timetag);
		tool.init(scriptArgs, upperExpo, FerretConfig.Action.PLOTS);
		tool.run();
		if ( tool.hasError() )
			throw new IllegalArgumentException("Failure generating data preview plots: " + 
					tool.getErrorMessage());

		log.debug("preview plots generated in " + cruisePlotsDirname);
	}

}
