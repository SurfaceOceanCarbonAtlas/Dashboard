/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.handlers;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import ucar.ma2.InvalidRangeException;

/**
 * NetCDF DSG file handler for the SOCAT upload dashboard.
 * 
 * @author Karl Smith
 */
public class DsgNcFileHandler {

	private File dsgFilesDir;
	private File decDsgFilesDir;
	private File erddapDsgFlagFile;
	private File erddapDecDsgFlagFile;

	/**
	 * Handles storage and retrieval of full and decimated NetCDF DSG files 
	 * under the given directories.
	 * 
	 * @param dsgFilesDirName
	 * 		name of the directory for the full NetCDF DSG files
	 * @param decDsgFilesDirName
	 * 		name of the directory for the decimated NetCDF DSG files
	 * @param erddapDsgFlagFileName
	 * 		name of the flag file to create to notify ERDDAP 
	 * 		of updates to the full NetCDF DSG files
	 * @param erddapDecDsgFlagFileName
	 * 		name of the flag file to create to notify ERDDAP 
	 * 		of updates to the decimated NetCDF DSG files
	 * @throws IllegalArgumentException
	 * 		if the specified DSG directories, or the parent directories
	 * 		of the ERDDAP flag files, do not exist or are not directories
	 */
	public DsgNcFileHandler(String dsgFilesDirName, String decDsgFilesDirName,
			String erddapDsgFlagFileName, String erddapDecDsgFlagFileName) {
		dsgFilesDir = new File(dsgFilesDirName);
		if ( ! dsgFilesDir.isDirectory() )
			throw new IllegalArgumentException(
					dsgFilesDirName + " is not a directory");
		decDsgFilesDir = new File(decDsgFilesDirName);
		if ( ! decDsgFilesDir.isDirectory() )
			throw new IllegalArgumentException(
					decDsgFilesDirName + " is not a directory");
		erddapDsgFlagFile = new File(erddapDsgFlagFileName);
		File parentDir = erddapDsgFlagFile.getParentFile();
		if ( (parentDir == null) || ! parentDir.isDirectory() )
			throw new IllegalArgumentException("parent directory of " + 
					erddapDsgFlagFile.getPath() + " is not valid");
		erddapDecDsgFlagFile = new File(erddapDecDsgFlagFileName);
		parentDir = erddapDecDsgFlagFile.getParentFile();
		if ( (parentDir == null) || ! parentDir.isDirectory() )
			throw new IllegalArgumentException("parent directory of " + 
					erddapDecDsgFlagFile.getPath() + " is not valid");
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
	public CruiseDsgNcFile getDsgNcFile(String expocode) throws IllegalArgumentException {
		// Check and standardize the expocode
		String expo = DashboardServerUtils.checkExpocode(expocode);
		return new CruiseDsgNcFile(dsgFilesDir + File.separator + expo.substring(0,4) +
				File.separator + expo + ".nc");
	}

	/**
	 * Generates the cruise-specific decimated NetCDF DSG abstract file for a cruise.
	 * The {@link CruiseDsgNcFile#create} and {@link CruiseDsgNcFile#updateWoceFlags} 
	 * methods should not be used with the decimated data file; the actual decimated 
	 * DSG file is created using {@link #decimateCruise(String)}.
	 * 
	 * @param expocode
	 * 		expocode of the cruise
	 * @return
	 * 		decimated NetCDF DSG abstract file for the cruise
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 */
	public CruiseDsgNcFile getDecDsgNcFile(String expocode) throws IllegalArgumentException {
		// Check and standardize the expocode
		String expo = DashboardServerUtils.checkExpocode(expocode);
		return new CruiseDsgNcFile(decDsgFilesDir + File.separator + expo.substring(0,4) +
				File.separator + expo + ".nc");
	}

	/**
	 * Saves the cruise OME metadata and cruise data into a new full-data 
	 * NetCDF DSG file.  After successful creation of the DSG file, 
	 * ERDDAP will need to be notified of changes to the DSG files. 
	 * This notification is not done in this routine so that a single 
	 * notification event can be made after multiple modifications.
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
	public void saveCruise(DashboardOmeMetadata omeMData, DashboardCruiseWithData cruiseData, 
									String qcFlag) throws IllegalArgumentException {
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected failure to obtain the dashboard data store");
		}

		// Get the location and name for the NetCDF DSG file
		CruiseDsgNcFile dsgFile = getDsgNcFile(omeMData.getExpocode());

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
		try {
			dsgFile.create(socatMData, socatDatalist);
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
	 * Generates the decimated-data NetCDF DSG file from the full-data 
	 * NetCDF DSG file.  After successful creation of the decimated DSG file, 
	 * ERDDAP will need to be notified of changes to the decimated DSG files. 
	 * This notification is not done in this routine so that a single 
	 * notification event can be made after multiple modifications.
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
					"Full DSG file for " + expocode + " is not readable");

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
	 * Appropriately renames any DSG and decimated DSG files, if they exist, 
	 * for a change in cruise expocode.  Renames the expocode in the DSG files.
	 * 
	 * @param oldExpocode
	 * 		standardized old expocode of the cruise
	 * @param newExpocode
	 * 		standardized new expocode for the cruise
	 * @throws IllegalArgumentException
	 * 		if a DSG or decimated DSG file for the new expocode already exists, or
	 * 		if unable to rename the DSG or decimated DSG file
	 * @throws IOException
	 * 		if unable to update the expocode contained in the DSG files
	 */
	public void renameDsgFiles(String oldExpocode, String newExpocode) 
									throws IllegalArgumentException, IOException {
		CruiseDsgNcFile newDsgFile = getDsgNcFile(newExpocode);
		if ( newDsgFile.exists() )
			throw new IllegalArgumentException(
					"DSG file for " + oldExpocode + " already exist");
		File newParent = newDsgFile.getParentFile();
		if ( ! newParent.exists() ) 
			newParent.mkdirs();

		CruiseDsgNcFile newDecDsgFile = getDecDsgNcFile(newExpocode);
		if ( newDecDsgFile.exists() )
			throw new IllegalArgumentException(
					"Decimated DSG file for " + oldExpocode + " already exist");
		newParent = newDecDsgFile.getParentFile();
		if ( ! newParent.exists() ) 
			newParent.mkdirs();

		String varName = Constants.SHORT_NAMES.get(Constants.expocode_VARNAME);

		// Rename and update the DSG file
		File oldDsgFile = getDsgNcFile(oldExpocode);
		if ( oldDsgFile.exists() )  {
			if ( ! oldDsgFile.renameTo(newDsgFile) ) 
				throw new IllegalArgumentException("Unable to rename DSG "
						+ "file from " + oldExpocode + " to " + newExpocode);
			try {
				newDsgFile.updateStringVarValue(varName, newExpocode);
			} catch (InvalidRangeException ex) {
				newDsgFile.renameTo(oldDsgFile);
				throw new IOException(ex);
			}
		}

		// Rename and update the decimated DSG file
		File oldDecDsgFile = getDecDsgNcFile(oldExpocode);
		if ( oldDecDsgFile.exists() ) {
			if ( ! oldDecDsgFile.renameTo(newDecDsgFile) ) 
				throw new IllegalArgumentException("Unable to rename decimated "
						+ "DSG file from " + oldExpocode + " to " + newExpocode);
			try {
				newDecDsgFile.updateStringVarValue(varName, newExpocode);
			} catch (InvalidRangeException ex) {
				newDecDsgFile.renameTo(oldDecDsgFile);
				throw new IOException(ex);
			}
		}

		// Tell ERDDAP there are changes
		flagErddap(true);
	}

	/**
	 * Deletes the DSG and decimated DSG files, if they exist, for a dataset.  
	 * If a file was deleted, true is returned and ERDDAP will need to be 
	 * notified of changes to the DSG and decimated DSG files.  This notification
	 * is not done in this routine so that a single notification event can be
	 * made after multiple modifications.
	 * 
	 * @param expocode
	 * 		delete the DSG and decimated DSG files for the dataset with this expocode
	 * @return
	 * 		true if a file was deleted
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid or 
	 * 		if unable to delete one of the files 
	 */
	public boolean deleteCruise(String expocode) throws IllegalArgumentException {
		boolean fileDeleted = false;
		File dsgFile = getDsgNcFile(expocode);
		if ( dsgFile.exists() ) {
			if ( ! dsgFile.delete() )
				throw new IllegalArgumentException(
						"Unable to delete the DSG file for " + expocode);
			fileDeleted = true;
		}
		File decDsgFile = getDecDsgNcFile(expocode);
		if ( decDsgFile.exists() ) {
			if ( ! decDsgFile.delete() )
				throw new IllegalArgumentException(
						"Unable to delete the decimated DSG file for " + expocode);
			fileDeleted = true;
		}
		return fileDeleted;
	}

	/**
	 * Notifies ERDDAP that content has changed in the DSG files. 
	 * 
	 * @param flagDecDsg
	 * 		if true, also notify ERDDAP that content has changed in the decimated DSG files.
	 * @return
	 * 		true if successful
	 */
	public boolean flagErddap(boolean flagDecDsg) {
		try {
			FileOutputStream touchFile = new FileOutputStream(erddapDsgFlagFile);
			touchFile.close();
			if ( flagDecDsg ) {
				touchFile = new FileOutputStream(erddapDecDsgFlagFile);
				touchFile.close();
			}
		} catch (IOException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Reads and returns the set of region IDs from the DSG file for the given 
	 * cruise.  This DSG file must have regions IDs assigned by Ferret, such as 
	 * when saved using 
	 * {@link #saveCruise(OmeMetadata, DashboardCruiseWithData, String)},
	 * for the set of region IDs to be meaningful.
	 * 
	 * @param expocode
	 * 		get the region IDs for the cruise with this expocode
	 * @return
	 * 		set of region IDs for the indicated cruise
	 * @throws FileNotFoundException
	 * 		if the full-data DSG file does not exist
	 * @throws IOException
	 * 		if there are problems opening or reading from the DSG file
	 * @throws IllegalArgumentException
	 * 		if the DSG file does not have a 'region_id' variable, or
	 * 		if a region ID is not recognized.
	 */
	public TreeSet<Character> getDataRegionsSet(String expocode) 
			throws IllegalArgumentException, FileNotFoundException, IOException {
		CruiseDsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new FileNotFoundException("Full data DSG file for " + 
					expocode + " does not exist");
		char[] regions = dsgFile.readCharVarDataValues(
				Constants.SHORT_NAMES.get(Constants.regionID_VARNAME));
		TreeSet<Character> regionsSet = new TreeSet<Character>();
		for ( char value : regions )
			regionsSet.add(value);
		for ( Character value : regionsSet )
			if ( DataLocation.REGION_NAMES.get(value) == null )
				throw new IllegalArgumentException("Unexpected region_id of '" + value + "'");
		return regionsSet;
	}

	/**
	 * Reads and returns the array of data values for the specified variable
	 * contained in the DSG file for the specified cruise.  The variable must 
	 * be saved in the DSG file as characters.  Empty strings are changed to 
	 * {@link SocatCruiseData#CHAR_MISSING_VALUE}.  For some variables, the  
	 * DSG file must have been processed by Ferret, such as when saved using 
	 * {@link DsgNcFileHandler#saveCruise(OmeMetadata, DashboardCruiseWithData, String)}
	 * for the data values to be meaningful.
	 * 
	 * @param expocode
	 * 		get the data values for the cruise with this expocode
	 * @param varName
	 * 		name of the variable to read
	 * @return
	 * 		array of values for the specified variable
	 * @throws FileNotFoundException
	 * 		if the full-data DSG file does not exist
	 * @throws IOException
	 * 		if there is a problem opening or reading from this DSG file
	 * @throws IllegalArgumentException
	 * 		if the variable name is invalid, or
	 * 		if the variable is not a single-character array variable
	 */
	public char[] readCharVarDataValues(String expocode, String varName) 
			throws IllegalArgumentException, FileNotFoundException, IOException {
		CruiseDsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new FileNotFoundException("Full data DSG file for " + 
					expocode + " does not exist");
		return dsgFile.readCharVarDataValues(varName);
	}

	/**
	 * Reads and returns the array of data values for the specified variable
	 * contained in the DSG file for the specified cruise.  The variable must 
	 * be saved in the DSG file as integers.  For some variables, the DSG file 
	 * must have been processed by Ferret, such as when saved using 
	 * {@link DsgNcFileHandler#saveCruise(OmeMetadata, DashboardCruiseWithData, String)}
	 * for the data values to be meaningful.
	 * 
	 * @param expocode
	 * 		get the data values for the cruise with this expocode
	 * @param varName
	 * 		name of the variable to read
	 * @return
	 * 		array of values for the specified variable
	 * @throws FileNotFoundException
	 * 		if the full-data DSG file does not exist
	 * @throws IOException
	 * 		if there is a problem opening or reading from this DSG file
	 * @throws IllegalArgumentException
	 * 		if the variable name is invalid
	 */
	public int[] readIntVarDataValues(String expocode, String varName) 
			throws IllegalArgumentException, FileNotFoundException, IOException {
		CruiseDsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new FileNotFoundException("Full data DSG file for " + 
					expocode + " does not exist");
		return dsgFile.readIntVarDataValues(varName);
	}

	/**
	 * Reads and returns the array of data values for the specified variable
	 * contained in the DSG file for the specified cruise.  The variable must 
	 * be saved in the DSG file as doubles.  NaN and infinite values are changed 
	 * to {@link SocatCruiseData#FP_MISSING_VALUE}.  For some variables, the 
	 * DSG file must have been processed by Ferret, such as when saved using 
	 * {@link DsgNcFileHandler#saveCruise(OmeMetadata, DashboardCruiseWithData, String)}
	 * for the data values to be meaningful.
	 * 
	 * @param expocode
	 * 		get the data values for the cruise with this expocode
	 * @param varName
	 * 		name of the variable to read
	 * @return
	 * 		array of values for the specified variable
	 * @throws FileNotFoundException
	 * 		if the full-data DSG file does not exist
	 * @throws IOException
	 * 		if there is a problem opening or reading from this DSG file
	 * @throws IllegalArgumentException
	 * 		if the variable name is invalid
	 */
	public double[] readDoubleVarDataValues(String expocode, String varName) 
			throws IllegalArgumentException, FileNotFoundException, IOException {
		CruiseDsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new FileNotFoundException("Full data DSG file for " + 
					expocode + " does not exist");
		return dsgFile.readDoubleVarDataValues(varName);
	}

	/**
	 * Read and returns the QC flag contained in the DSG file 
	 * for the cruise with the indicated expocode.
	 * 
	 * @param expocode
	 * 		get the DSG file QC flag for the cruise with this expocode
	 * @return
	 * 		the QC flag contained in the DSG file for the cruise
	 * @throws FileNotFoundException
	 * 		if the full-data DSG file does not exist
	 * @throws IOException
	 * 		if there is a problem opening or reading from this DSG file
	 * @throws IllegalArgumentException
	 * 		if the expocode is not invalid, or
	 * 		if the DSG file for the cruise is not valid
	 */
	public char getQCFlag(String expocode) 
			throws IllegalArgumentException, FileNotFoundException, IOException {
		CruiseDsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new FileNotFoundException("Full data DSG file for " + 
					expocode + " does not exist");
		return dsgFile.getQCFlag();
	}

	/**
	 * Assigns the QC flag given in qcEvent in the full and decimated 
	 * DSG files for the dataset with the expocode given in qvEvent. 
	 * 
	 * @param qcEvent
	 * 		get the QC flag and dataset expocode from here
	 * @throws IllegalArgumentException
	 * 		if the DSG files are not valid
	 * @throws IOException
	 * 		if problems opening or writing to a DSG file 
	 */
	public void updateQCFlag(SocatQCEvent qcEvent) 
			throws IllegalArgumentException, IOException {
		// Get the location and name for the NetCDF DSG file
		String expocode = qcEvent.getExpocode();
		CruiseDsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new IllegalArgumentException(
					"DSG file for " + expocode + " does not exist");
		try {
			dsgFile.updateQCFlag(qcEvent);
		} catch (InvalidRangeException ex) {
			throw new IOException(ex);
		}
		CruiseDsgNcFile decDsgFile = getDecDsgNcFile(expocode);
		if ( ! decDsgFile.exists() )
			throw new IllegalArgumentException(
					"Decimated DSG file for " + expocode + " does not exist");
		try {
			decDsgFile.updateQCFlag(qcEvent);
		} catch (InvalidRangeException ex) {
			throw new IOException(ex);
		}
		flagErddap(true);
	}

	/**
	 * Update the WOCE flags in the full and decimated DSG files, as well as 
	 * in the temporary DSG file, if given.  In the process, complete some of 
	 * the missing data in WOCE event (row number, region ID, data type).
	 * 
	 * @param woceEvent
	 * 		WOCE event to use; the expocode is used to identify datasets to update
	 * @param tempDsgFilename
	 * 		name of the temporary DSG file to also update; can be null
	 * @throws IllegalArgumentException
	 * 		if the DSG file or the WOCE flags are not valid
	 * @throws IOException
	 * 		if problems opening, reading from, or writing to the DSG file
	 */
	public void updateWoceFlags(SocatWoceEvent woceEvent, String tempDsgFilename) 
								throws IllegalArgumentException, IOException {
		String expocode = woceEvent.getExpocode();
		// Assign the WOCE flags in the full-data DSG file, and get missing data
		CruiseDsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.canRead() )
			throw new IllegalArgumentException(
					"DSG file for " + expocode + " does not exist");
		try {
			ArrayList<DataLocation> unidentified = 
					dsgFile.updateWoceFlags(woceEvent, true);
			if ( unidentified.size() > 0 ) {
				String msg  = "Unable to find data location(s): \n    ";
				for ( DataLocation dataloc : unidentified )
					msg += dataloc.toString() + "\n    ";
				msg += "in " + dsgFile.getName();
				throw new IllegalArgumentException(msg);
			}
		} catch (InvalidRangeException ex) {
			throw new IOException(ex);
		}

		// Assign the WOCE flags in the decimated DSG file
		dsgFile = getDecDsgNcFile(expocode);
		if ( ! dsgFile.canRead() )
			throw new IllegalArgumentException(
					"Decimated DSG file for " + expocode + " does not exist");
		try {
			// Very likely to return missing data locations, but not a problem
			dsgFile.updateWoceFlags(woceEvent, false);
		} catch (InvalidRangeException ex) {
			throw new IOException(ex);
		}

		// Let ERDDAP know files have changed
		flagErddap(true);

		// Set the flags in the temporary DSG file, if given 
		if ( (tempDsgFilename == null) || tempDsgFilename.trim().isEmpty() )
			return;
		dsgFile = new CruiseDsgNcFile(tempDsgFilename);
		if ( ! dsgFile.canRead() )
			throw new IllegalArgumentException("Temporary DSG file " + 
					dsgFile.getName() + " does not exist");
		try {
			// Probably should not be any missing data locations, but no guarantees
			dsgFile.updateWoceFlags(woceEvent, false);
		} catch (InvalidRangeException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * Generates the decimated DSG file from the full-data DSG file for 
	 * cruises specified in ExpocodesFile, or all cruises if ExpocodesFile 
	 * is '-'. The default dashboard configuration is used for this process. 
	 * 
	 * @param args
	 * 		ExpocodesFile
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  [ - | ExpocodesFile ]");
			System.err.println();
			System.err.println("Generates the decimated DSG file from the full-data DSG file for ");
			System.err.println("cruises specified in ExpocodesFile, or all cruises if ExpocodesFile ");
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
			DsgNcFileHandler dsgHandler = dataStore.getDsgNcFileHandler();

			// Get the expocode of the cruises to decimate
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

			// decimate each of these cruises
			for ( String expocode : allExpocodes ) {
				try {
					dsgHandler.decimateCruise(expocode);
				} catch (Exception ex) {
					System.err.println("Error decimating " + expocode + " : " + ex.getMessage());
					ex.printStackTrace();
					System.err.println("===================================================");
					success = false;
				}
			}

			// Flag ERDDAP that (only) the decimated files have been updated
			try {
				FileOutputStream touchFile = new FileOutputStream(dsgHandler.erddapDecDsgFlagFile);
				touchFile.close();
			} catch (IOException e) {
				// don't care
			}
		} finally {
			dataStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
