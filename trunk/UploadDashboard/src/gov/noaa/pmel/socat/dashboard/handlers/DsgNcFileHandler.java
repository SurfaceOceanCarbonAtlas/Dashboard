/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.handlers;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import ucar.ma2.InvalidRangeException;
import uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadata;

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
	private FerretConfig ferretConfig;
	private WatchService watcher;
	private Thread watcherThread;
	private Logger itsLogger;

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
	 * @param ferretConf
	 * 		configuration document for running Ferret
	 * @throws IllegalArgumentException
	 * 		if the specified DSG directories, or the parent directories
	 * 		of the ERDDAP flag files, do not exist or are not directories
	 */
	public DsgNcFileHandler(String dsgFilesDirName, String decDsgFilesDirName,
			String erddapDsgFlagFileName, String erddapDecDsgFlagFileName, 
			FerretConfig ferretConf) {
		dsgFilesDir = new File(dsgFilesDirName);
		if ( ! dsgFilesDir.isDirectory() )
			throw new IllegalArgumentException(dsgFilesDirName + " is not a directory");
		decDsgFilesDir = new File(decDsgFilesDirName);
		if ( ! decDsgFilesDir.isDirectory() )
			throw new IllegalArgumentException(decDsgFilesDirName + " is not a directory");
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
		ferretConfig = ferretConf;

		try {
			Path dsgFilesDirPath = dsgFilesDir.toPath();
			// Verify the OME output directory can be registered with the watch service
			watcher = FileSystems.getDefault().newWatchService();
			dsgFilesDirPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY).cancel();
			watcher.close();
			watcher = null;
			watcherThread = null;
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problems creating a watcher for the DSG files directory: " + 
					ex.getMessage(), ex);
		}

		itsLogger = Logger.getLogger("DsgNcFileHandler");
	}

	/**
	 * Generates the cruise-specific full NetCDF DSG abstract file for a cruise.
	 * Creates the parent subdirectory if it does not exist.
	 * 
	 * @param expocode
	 * 		expocode of the cruise 
	 * @return
	 * 		full NetCDF DSG abstract file for the cruise
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid, or
	 * 		if problems creating the parent subdirectory
	 */
	public CruiseDsgNcFile getDsgNcFile(String expocode) throws IllegalArgumentException {
		// Check and standardize the expocode
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		// Make sure the parent directory exists
		File parentDir = new File(dsgFilesDir, upperExpo.substring(0,4));
		if ( parentDir.exists() ) {
			if ( ! parentDir.isDirectory() ) {
				throw new IllegalArgumentException("Parent subdirectory exists but is not a directory: " +
						parentDir.getPath());
			}
		}
		else if ( ! parentDir.mkdirs() ) {
			throw new IllegalArgumentException("Unable to create the new subdirectory " + 
					parentDir.getPath());
		}
		return new CruiseDsgNcFile(parentDir, upperExpo + ".nc");
	}

	/**
	 * Generates the cruise-specific decimated NetCDF DSG abstract file for a cruise.
	 * Creates the parent subdirectory if it does not exist.
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
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		// Make sure the parent directory exists
		File parentDir = new File(decDsgFilesDir, upperExpo.substring(0,4));
		if ( parentDir.exists() ) {
			if ( ! parentDir.isDirectory() ) {
				throw new IllegalArgumentException("Parent subdirectory exists but is not a directory: " +
						parentDir.getPath());
			}
		}
		else if ( ! parentDir.mkdirs() ) {
			throw new IllegalArgumentException("Unable to create the new subdirectory " + 
					parentDir.getPath());
		}
		return new CruiseDsgNcFile(parentDir, upperExpo + ".nc");
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
		// Get the location and name for the NetCDF DSG file
		CruiseDsgNcFile dsgFile = getDsgNcFile(omeMData.getExpocode());

		// Get just the filenames from the set of additional documents
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
			dsgFile.delete();
			throw new IllegalArgumentException(
					"Problems creating the SOCAT DSG file " + dsgFile.getName() +
					"\n    " + ex.getMessage(), ex);
		}

		// Call Ferret to add the computed variables to the NetCDF DSG file
		SocatTool tool = new SocatTool(ferretConfig);
		ArrayList<String> scriptArgs = new ArrayList<String>(1);
		scriptArgs.add(dsgFile.getPath());
		tool.init(scriptArgs, cruiseData.getExpocode(), FerretConfig.Action.COMPUTE);
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
		// Get the location and name of the full DSG file
		File dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.canRead() )
			throw new IllegalArgumentException(
					"Full DSG file for " + expocode + " is not readable");

		// Get the location and name for the decimated DSG file
		File decDsgFile = getDecDsgNcFile(expocode);

		// Call Ferret to create the decimated DSG file from the full DSG file
		SocatTool tool = new SocatTool(ferretConfig);
		ArrayList<String> scriptArgs = new ArrayList<String>(2);
		scriptArgs.add(dsgFile.getPath());
		scriptArgs.add(decDsgFile.getPath());
		tool.init(scriptArgs, expocode, FerretConfig.Action.DECIMATE);
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
	 * 		if the contents of the old DSG file are invalid
	 * @throws IOException
	 * 		if unable to regenerate the DSG or decimated DSG file with the new expocode
	 * 		from the data and metadata (except for expocode-related fields) in the old 
	 * 		DSG file, or if unable to delete the old DSG or decimated DSG file
	 */
	public void renameDsgFiles(String oldExpocode, String newExpocode) 
									throws IllegalArgumentException, IOException {
		CruiseDsgNcFile newDsgFile = getDsgNcFile(newExpocode);
		if ( newDsgFile.exists() )
			throw new IllegalArgumentException(
					"DSG file for " + oldExpocode + " already exist");

		CruiseDsgNcFile newDecDsgFile = getDecDsgNcFile(newExpocode);
		if ( newDecDsgFile.exists() )
			throw new IllegalArgumentException(
					"Decimated DSG file for " + oldExpocode + " already exist");

		CruiseDsgNcFile oldDsgFile = getDsgNcFile(oldExpocode);
		if ( oldDsgFile.exists() )  {
			// Just re-create the DSG file with the updated metadata
			ArrayList<String> missing = oldDsgFile.read(false);
			if ( ! missing.isEmpty() )
				throw new RuntimeException("Unexpected values missing from the DSG file: " + missing);
			try {
				ArrayList<SocatCruiseData> dataVals = oldDsgFile.getDataList();
				SocatMetadata updatedMeta = oldDsgFile.getMetadata();
				updatedMeta.setExpocode(newExpocode);
				newDsgFile.create(updatedMeta, dataVals);
				// Call Ferret to add lon360 and tmonth (calculated data should be the same)
				SocatTool tool = new SocatTool(ferretConfig);
				ArrayList<String> scriptArgs = new ArrayList<String>(1);
				scriptArgs.add(newDsgFile.getPath());
				tool.init(scriptArgs, newExpocode, FerretConfig.Action.COMPUTE);
				tool.run();
				if ( tool.hasError() )
					throw new IllegalArgumentException(newExpocode + 
							": Failure adding computed variables: " + tool.getErrorMessage());
				// Re-create the decimated-data DSG file 
				decimateCruise(newExpocode);
				// Delete the old DSG and decimated-data DSG files
				oldDsgFile.delete();
				getDecDsgNcFile(oldExpocode).delete();
			} catch ( Exception ex ) {
				throw new IOException(ex);
			}

			// Tell ERDDAP there are changes
			flagErddap(true, true);
		}

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
	 * @param flagDsg
	 * 		if true, notify ERDDAP that content has changed in the full DSG files.
	 * @param flagDecDsg
	 * 		if true, notify ERDDAP that content has changed in the decimated DSG files.
	 * @return
	 * 		true if successful
	 */
	public boolean flagErddap(boolean flagDsg, boolean flagDecDsg) {
		try {
			if ( flagDsg ) {
				FileOutputStream touchFile = new FileOutputStream(erddapDsgFlagFile);
				touchFile.close();
			}
			if ( flagDecDsg ) {
				FileOutputStream touchFile = new FileOutputStream(erddapDecDsgFlagFile);
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
	 * contained in the full-data DSG file for the specified cruise.  The 
	 * variable must be saved in the DSG file as doubles.  NaN and infinite 
	 * values are changed to {@link SocatCruiseData#FP_MISSING_VALUE}.  For 
	 * some variables, the DSG file must have been processed by Ferret, such 
	 * as when saved using {@link DsgNcFileHandler#saveCruise(OmeMetadata, 
	 * DashboardCruiseWithData, String)} for the data values to be meaningful.
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
	 * Reads and returns the longitudes, latitudes, and times contained in the 
	 * full-data DSG file for the specified cruise.  NaN and infinite values 
	 * are changed to {@link SocatCruiseData#FP_MISSING_VALUE}.
	 * 
	 * @param expocode
	 * 		get the data values for the cruise with this expocode
	 * @return
	 * 		the array { lons, lats, times } from the full-data DSG file, 
	 * 		where lons are the array of longitudes, lats are the array of latitudes, 
	 * 		times are the array of times.
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 * @throws FileNotFoundException
	 * 		if the full-data DSG file does not exist
	 * @throws IOException
	 * 		if problems opening or reading from the DSG file, or 
	 * 		if any of the data arrays are not given in the DSG file
	 */
	public double[][] readLonLatTimeDataValues(String expocode) 
			throws IllegalArgumentException, FileNotFoundException, IOException {
		CruiseDsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new FileNotFoundException("Full data DSG file for " + 
					expocode + " does not exist");
		return dsgFile.readLonLatTimeDataValues();
	}

	/**
	 * Reads and returns the longitudes, latitudes, times, SST values, and 
	 * fCO2_recommended values contained in the full-data DSG file for the
	 * specified cruise.  NaN and infinite values are changed to 
	 * {@link SocatCruiseData#FP_MISSING_VALUE}.  The full-data DSG file must 
	 * have been processed by Ferret, such as when saved using 
	 * {@link DsgNcFileHandler#saveCruise(OmeMetadata, DashboardCruiseWithData, String)}
	 * for the fCO2_recommended values to be meaningful.
	 * 
	 * @param expocode
	 * 		get the data values for the cruise with this expocode
	 * @return
	 * 		the array { lons, lats, times, ssts, fco2s } from the full-data DSG file, 
	 * 		where lons are the array of longitudes, lats are the array of latitudes, 
	 * 		times are the array of times, ssts are the array of SST values, and 
	 * 		fco2s are the array of fCO2_recommended values.
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 * @throws FileNotFoundException
	 * 		if the full-data DSG file does not exist
	 * @throws IOException
	 * 		if problems opening or reading from the DSG file, or 
	 * 		if any of the data arrays are not given in the DSG file
	 */
	public double[][] readLonLatTimeSstFco2DataValues(String expocode) 
			throws IllegalArgumentException, FileNotFoundException, IOException {
		CruiseDsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new FileNotFoundException("Full data DSG file for " + 
					expocode + " does not exist");
		return dsgFile.readLonLatTimeSstFco2DataValues();
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
			dsgFile.updateQCFlag(qcEvent.getFlag());
		} catch (InvalidRangeException ex) {
			throw new IOException(ex);
		}
		CruiseDsgNcFile decDsgFile = getDecDsgNcFile(expocode);
		if ( ! decDsgFile.exists() )
			throw new IllegalArgumentException(
					"Decimated DSG file for " + expocode + " does not exist");
		try {
			decDsgFile.updateQCFlag(qcEvent.getFlag());
		} catch (InvalidRangeException ex) {
			throw new IOException(ex);
		}
		flagErddap(true, true);
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
		flagErddap(true, true);

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
	 * Starts a new Thread monitoring the full-data DSG directory.
	 * If a Thread is currently monitoring the directory, this call does nothing.
	 */
	public void watchForDsgFileUpdates() {
		// Make sure the watcher is not already running
		if ( watcherThread != null )
			return;
		watcherThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Path dsgFilesDirPath = dsgFilesDir.toPath();
				// Create a new watch service for the OME server output directory
				try {
					watcher = FileSystems.getDefault().newWatchService();
				} catch (Exception ex) {
					itsLogger.error("Unexpected error starting a watcher for the default file system", ex);
					return;
				}
				// Register the OME server output directory with the watch service
				WatchKey registration;
				try {
					registration = dsgFilesDirPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
				} catch (Exception ex) {
					itsLogger.error("Unexpected error registering the full-data DSG files directory for watching", ex);
					try {
						watcher.close();
					} catch (Exception e) {
						;
					}
					watcher = null;
					return;
				}
				for (;;) {
					try {
						WatchKey key = watcher.take();
						for ( WatchEvent<?> event : key.pollEvents() ) {
							Path relPath = (Path) event.context();
							handleDsgFileChange(dsgFilesDirPath.resolve(relPath).toFile());
						}
						if ( ! key.reset() )
							break;
					} catch (Exception ex) {
						// Probably the watcher was closed
						break;
					}
				}
				registration.cancel();
				registration.pollEvents();
				try {
					watcher.close();
				} catch (Exception ex) {
					;
				}
				watcher = null;
				return;
			}
		});
		itsLogger.info("Starting new thread monitoring the full-data DSG directory: " + dsgFilesDir.getPath()); 
		watcherThread.start();
	}

	private void handleDsgFileChange(File dsgFile) {
		if ( dsgFile.isDirectory() ) {
			itsLogger.info("Working with updated full-data DSG subdirectory " + dsgFile.getPath());
		}
		else {
			itsLogger.info("Working with updated full-data DSG file " + dsgFile.getPath());
		}
	}

	/**
	 * Stops the monitoring the full-data DSG directory.  
	 * If the full-data DSG directory is not being monitored, this call does nothing. 
	 */
	public void cancelWatch() {
		try {
			watcher.close();
			// Only the thread modifies the value of watcher
		} catch (Exception ex) {
			// Might be NullPointerException
		}
		if ( watcherThread != null ) {
			try {
				watcherThread.join();
			} catch (Exception ex) {
				;
			}
			watcherThread = null;
			itsLogger.info("End of thread monitoring the the full-data DSG directory: " + dsgFilesDir.getPath());
		}
	}

}
