/**
 * 
 */
package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.dashboard.ferret.SocatTool;
import gov.noaa.pmel.dashboard.server.DsgNcFile;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.DsgData;
import gov.noaa.pmel.dashboard.server.DsgMetadata;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.QCEvent;
import gov.noaa.pmel.dashboard.shared.DataQCEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.ArrayList;

import ucar.ma2.InvalidRangeException;
import uk.ac.uea.socat.omemetadata.OmeMetadata;

/**
 * NetCDF DSG file handler for the SOCAT upload dashboard.
 * 
 * @author Karl Smith
 */
public class DsgNcFileHandler {

	private static final String DSG_FILE_SUFFIX = ".nc";

	private File dsgFilesDir;
	private File decDsgFilesDir;
	private File erddapDsgFlagFile;
	private File erddapDecDsgFlagFile;
	private FerretConfig ferretConfig;
	private KnownDataTypes knownMetadataTypes;
	private KnownDataTypes knownDataFileTypes;
	private WatchService watcher;

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
			FerretConfig ferretConf, KnownDataTypes knownMetadataTypes, 
			KnownDataTypes knownDataFileTypes) {
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
		this.knownMetadataTypes = knownMetadataTypes;
		this.knownDataFileTypes = knownDataFileTypes;

		try {
			Path dsgFilesDirPath = dsgFilesDir.toPath();
			// Verify the OME output directory can be registered with the watch service
			watcher = FileSystems.getDefault().newWatchService();
			dsgFilesDirPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY).cancel();
			watcher.close();
			watcher = null;
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problems creating a watcher for the DSG files directory: " + 
					ex.getMessage(), ex);
		}
	}

	/**
	 * Generates the cruise-specific full NetCDF DSG abstract file for a cruise.
	 * Creates the parent subdirectory if it does not exist.
	 * 
	 * @param dataset
	 * 		dataset of the cruise 
	 * @return
	 * 		full NetCDF DSG abstract file for the cruise
	 * @throws IllegalArgumentException
	 * 		if the dataset is invalid, or
	 * 		if problems creating the parent subdirectory
	 */
	public DsgNcFile getDsgNcFile(String expocode) throws IllegalArgumentException {
		// Check and standardize the dataset
		String upperExpo = DashboardServerUtils.checkDatasetID(expocode);
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
		return new DsgNcFile(parentDir, upperExpo + DSG_FILE_SUFFIX);
	}

	/**
	 * Generates the cruise-specific decimated NetCDF DSG abstract file for a cruise.
	 * Creates the parent subdirectory if it does not exist.
	 * The {@link DsgNcFile#create} and {@link DsgNcFile#updateWoceFlags} 
	 * methods should not be used with the decimated data file; the actual decimated 
	 * DSG file is created using {@link #decimateCruise(String)}.
	 * 
	 * @param dataset
	 * 		dataset of the cruise
	 * @return
	 * 		decimated NetCDF DSG abstract file for the cruise
	 * @throws IllegalArgumentException
	 * 		if the dataset is invalid
	 */
	public DsgNcFile getDecDsgNcFile(String expocode) throws IllegalArgumentException {
		// Check and standardize the dataset
		String upperExpo = DashboardServerUtils.checkDatasetID(expocode);
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
		return new DsgNcFile(parentDir, upperExpo + DSG_FILE_SUFFIX);
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
	 * @param datasetData
	 * 		data for the cruise
	 * @param versionStatus
	 * 		version number and status to assign
	 * 		(see: {@link DatabaseRequestHandler#getVersionStatus(String)}) 
	 * @param qcFlag
	 * 		cruise QC flag to assign
	 * @throws IllegalArgumentException
	 * 		if there are problems with the metadata or data given, or
	 * 		if there are problems creating or writing the full-data DSG file
	 */
	public void saveCruise(DashboardOmeMetadata omeMData, DashboardDatasetData cruiseData, 
			String versionStatus, String qcFlag) throws IllegalArgumentException {
		// Get the location and name for the NetCDF DSG file
		DsgNcFile dsgFile = getDsgNcFile(omeMData.getDatasetId());

		// Get the metadata needed for creating the DSG file
		DsgMetadata dsgMData = omeMData.createDsgMetadata();
		dsgMData.setVersion(versionStatus);
		dsgMData.setQcFlag(qcFlag);

		// Convert the cruise data strings into the appropriate type
		ArrayList<DsgData> socatDatalist = 
				DsgData.dataListFromDashboardCruise(knownDataFileTypes, cruiseData);

		// Create the NetCDF DSG file
		try {
			dsgFile.create(dsgMData, socatDatalist);
		} catch (Exception ex) {
			dsgFile.delete();
			throw new IllegalArgumentException("Problems creating the SOCAT DSG file " + 
					dsgFile.getName() + "\n    " + ex.getMessage(), ex);
		}

		// Call Ferret to add the computed variables to the NetCDF DSG file
		SocatTool tool = new SocatTool(ferretConfig);
		ArrayList<String> scriptArgs = new ArrayList<String>(1);
		scriptArgs.add(dsgFile.getPath());
		tool.init(scriptArgs, cruiseData.getDatasetId(), FerretConfig.Action.COMPUTE);
		tool.run();
		if ( tool.hasError() )
			throw new IllegalArgumentException("Failure adding computed variables: " + 
					tool.getErrorMessage());

	}

	/**
	 * Generates the decimated-data NetCDF DSG file from the full-data 
	 * NetCDF DSG file.  After successful creation of the decimated DSG file, 
	 * ERDDAP will need to be notified of changes to the decimated DSG files. 
	 * This notification is not done in this routine so that a single 
	 * notification event can be made after multiple modifications.
	 * 
	 * @param dataset
	 * 		generate the decimated-data DSG file for the dataset with this dataset
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
	 * for a change in cruise dataset.  Renames the dataset in the DSG files.
	 * 
	 * @param oldExpocode
	 * 		standardized old dataset of the cruise
	 * @param newExpocode
	 * 		standardized new dataset for the cruise
	 * @throws IllegalArgumentException
	 * 		if a DSG or decimated DSG file for the new dataset already exists, or
	 * 		if the contents of the old DSG file are invalid
	 * @throws IOException
	 * 		if unable to regenerate the DSG or decimated DSG file with the new dataset
	 * 		from the data and metadata (except for dataset-related fields) in the old 
	 * 		DSG file, or if unable to delete the old DSG or decimated DSG file
	 */
	public void renameDsgFiles(String oldExpocode, String newExpocode) 
									throws IllegalArgumentException, IOException {
		DsgNcFile newDsgFile = getDsgNcFile(newExpocode);
		if ( newDsgFile.exists() )
			throw new IllegalArgumentException(
					"DSG file for " + oldExpocode + " already exist");

		DsgNcFile newDecDsgFile = getDecDsgNcFile(newExpocode);
		if ( newDecDsgFile.exists() )
			throw new IllegalArgumentException(
					"Decimated DSG file for " + oldExpocode + " already exist");

		DsgNcFile oldDsgFile = getDsgNcFile(oldExpocode);
		if ( oldDsgFile.exists() )  {
			// Just re-create the DSG file with the updated metadata
			ArrayList<String> missing = oldDsgFile.readMetadata(knownMetadataTypes);
			if ( ! missing.isEmpty() )
				throw new RuntimeException("Unexpected metadata fields missing from the DSG file: " + missing);
			missing = oldDsgFile.readData(knownDataFileTypes);
			if ( ! missing.isEmpty() )
				throw new RuntimeException("Unexpected data fields missing from the DSG file: " + missing);
			try {
				ArrayList<DsgData> dataVals = oldDsgFile.getDataList();
				DsgMetadata updatedMeta = oldDsgFile.getMetadata();
				updatedMeta.setDatasetId(newExpocode);
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
	 * @param dataset
	 * 		delete the DSG and decimated DSG files for the dataset with this dataset
	 * @return
	 * 		true if a file was deleted
	 * @throws IllegalArgumentException
	 * 		if the dataset is invalid or 
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
	 * Reads and returns the array of data values for the specified variable
	 * contained in the DSG file for the specified cruise.  The variable must 
	 * be saved in the DSG file as characters.  Empty strings are changed to 
	 * {@link DsgData#CHAR_MISSING_VALUE}.  For some variables, the  
	 * DSG file must have been processed by Ferret, such as when saved using 
	 * {@link DsgNcFileHandler#saveCruise(OmeMetadata, DashboardDatasetData, String)}
	 * for the data values to be meaningful.
	 * 
	 * @param dataset
	 * 		get the data values for the cruise with this dataset
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
		DsgNcFile dsgFile = getDsgNcFile(expocode);
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
	 * {@link DsgNcFileHandler#saveCruise(OmeMetadata, DashboardDatasetData, String)}
	 * for the data values to be meaningful.
	 * 
	 * @param dataset
	 * 		get the data values for the cruise with this dataset
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
		DsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new FileNotFoundException("Full data DSG file for " + 
					expocode + " does not exist");
		return dsgFile.readIntVarDataValues(varName);
	}

	/**
	 * Reads and returns the array of data values for the specified variable
	 * contained in the full-data DSG file for the specified cruise.  The 
	 * variable must be saved in the DSG file as doubles.  NaN and infinite 
	 * values are changed to {@link DsgData#FP_MISSING_VALUE}.  For 
	 * some variables, the DSG file must have been processed by Ferret, such 
	 * as when saved using {@link DsgNcFileHandler#saveCruise(OmeMetadata, 
	 * DashboardDatasetData, String)} for the data values to be meaningful.
	 * 
	 * @param dataset
	 * 		get the data values for the cruise with this dataset
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
		DsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new FileNotFoundException("Full data DSG file for " + 
					expocode + " does not exist");
		return dsgFile.readDoubleVarDataValues(varName);
	}

	/**
	 * Reads and returns the longitudes, latitudes, and times contained in the 
	 * full-data DSG file for the specified cruise.  NaN and infinite values 
	 * are changed to {@link DsgData#FP_MISSING_VALUE}.
	 * 
	 * @param dataset
	 * 		get the data values for the cruise with this dataset
	 * @return
	 * 		the array { lons, lats, times } from the full-data DSG file, 
	 * 		where lons are the array of longitudes, lats are the array of latitudes, 
	 * 		times are the array of times.
	 * @throws IllegalArgumentException
	 * 		if the dataset is invalid
	 * @throws FileNotFoundException
	 * 		if the full-data DSG file does not exist
	 * @throws IOException
	 * 		if problems opening or reading from the DSG file, or 
	 * 		if any of the data arrays are not given in the DSG file
	 */
	public double[][] readLonLatTimeDataValues(String expocode) 
			throws IllegalArgumentException, FileNotFoundException, IOException {
		DsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new FileNotFoundException("Full data DSG file for " + 
					expocode + " does not exist");
		return dsgFile.readLonLatTimeDataValues();
	}

	/**
	 * Read and returns the QC flag contained in the DSG file 
	 * for the cruise with the indicated dataset.
	 * 
	 * @param dataset
	 * 		get the DSG file QC flag for the cruise with this dataset
	 * @return
	 * 		the QC flag contained in the DSG file for the cruise
	 * @throws FileNotFoundException
	 * 		if the full-data DSG file does not exist
	 * @throws IOException
	 * 		if there is a problem opening or reading from this DSG file
	 * @throws IllegalArgumentException
	 * 		if the dataset is not invalid, or
	 * 		if the DSG file for the cruise is not valid
	 */
	public char getQCFlag(String expocode) 
			throws IllegalArgumentException, FileNotFoundException, IOException {
		DsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new FileNotFoundException("Full data DSG file for " + 
					expocode + " does not exist");
		return dsgFile.getQCFlag();
	}

	/**
	 * Assigns the QC flag given in qcEvent in the full and decimated 
	 * DSG files for the dataset with the dataset given in qvEvent. 
	 * 
	 * @param qcEvent
	 * 		get the QC flag and dataset dataset from here
	 * @throws IllegalArgumentException
	 * 		if the DSG files are not valid
	 * @throws IOException
	 * 		if problems opening or writing to a DSG file 
	 */
	public void updateQCFlag(QCEvent qcEvent) 
			throws IllegalArgumentException, IOException {
		// Get the location and name for the NetCDF DSG file
		String expocode = qcEvent.getDatasetId();
		DsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.exists() )
			throw new IllegalArgumentException(
					"DSG file for " + expocode + " does not exist");
		try {
			dsgFile.updateQCFlag(qcEvent.getFlagValue());
		} catch (InvalidRangeException ex) {
			throw new IOException(ex);
		}
		DsgNcFile decDsgFile = getDecDsgNcFile(expocode);
		if ( ! decDsgFile.exists() )
			throw new IllegalArgumentException(
					"Decimated DSG file for " + expocode + " does not exist");
		try {
			decDsgFile.updateQCFlag(qcEvent.getFlagValue());
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
	 * 		WOCE event to use; the dataset is used to identify datasets to update
	 * @param tempDsgFilename
	 * 		name of the temporary DSG file to also update; can be null
	 * @throws IllegalArgumentException
	 * 		if the DSG file or the WOCE flags are not valid
	 * @throws IOException
	 * 		if problems opening, reading from, or writing to the DSG file
	 */
	public void updateWoceFlags(DataQCEvent woceEvent, String tempDsgFilename) 
								throws IllegalArgumentException, IOException {
		String expocode = woceEvent.getDatasetId();
		// Assign the WOCE flags in the full-data DSG file, and get missing data
		DsgNcFile dsgFile = getDsgNcFile(expocode);
		if ( ! dsgFile.canRead() )
			throw new IllegalArgumentException("DSG file for " + expocode + " does not exist");
		try {
			ArrayList<DataLocation> unidentified = dsgFile.updateWoceFlags(woceEvent, true);
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
		dsgFile = new DsgNcFile(tempDsgFilename);
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

}
