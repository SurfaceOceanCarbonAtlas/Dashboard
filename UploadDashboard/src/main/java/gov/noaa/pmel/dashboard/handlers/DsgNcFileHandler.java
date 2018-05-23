/**
 *
 */
package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.dsg.StdDataArray;
import gov.noaa.pmel.dashboard.dsg.StdUserDataArray;
import gov.noaa.pmel.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.dashboard.ferret.SocatTool;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.ArrayList;

/**
 * NetCDF DSG file handler for the upload dashboard.
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
    private KnownDataTypes knownUserDataTypes;
    private KnownDataTypes knownMetadataTypes;
    private KnownDataTypes knownDataFileTypes;
    private WatchService watcher;

    /**
     * Handles storage and retrieval of full and decimated NetCDF discrete geometry files under the given directories.
     *
     * @param dsgFilesDirName
     *         name of the directory for the full NetCDF DSG files
     * @param decDsgFilesDirName
     *         name of the directory for the decimated NetCDF DSG files
     * @param erddapDsgFlagFileName
     *         name of the flag file to create to notify ERDDAP of updates to the full NetCDF DSG files
     * @param erddapDecDsgFlagFileName
     *         name of the flag file to create to notify ERDDAP of updates to the decimated NetCDF DSG files
     * @param ferretConf
     *         configuration document for running Ferret
     *
     * @throws IllegalArgumentException
     *         if the specified DSG directories, or the parent directories of the ERDDAP flag files,
     *         do not exist or are not directories
     */
    public DsgNcFileHandler(String dsgFilesDirName, String decDsgFilesDirName, String erddapDsgFlagFileName,
            String erddapDecDsgFlagFileName, FerretConfig ferretConf, KnownDataTypes knownUserDataTypes,
            KnownDataTypes knownMetadataTypes, KnownDataTypes knownDataFileTypes) {
        dsgFilesDir = new File(dsgFilesDirName);
        if ( !dsgFilesDir.isDirectory() )
            throw new IllegalArgumentException(dsgFilesDirName + " is not a directory");
        decDsgFilesDir = new File(decDsgFilesDirName);
        if ( !decDsgFilesDir.isDirectory() )
            throw new IllegalArgumentException(decDsgFilesDirName + " is not a directory");
        erddapDsgFlagFile = new File(erddapDsgFlagFileName);
        File parentDir = erddapDsgFlagFile.getParentFile();
        if ( (parentDir == null) || !parentDir.isDirectory() )
            throw new IllegalArgumentException("parent directory of " + erddapDsgFlagFile.getPath() +
                    " is not valid");
        erddapDecDsgFlagFile = new File(erddapDecDsgFlagFileName);
        parentDir = erddapDecDsgFlagFile.getParentFile();
        if ( (parentDir == null) || !parentDir.isDirectory() )
            throw new IllegalArgumentException("parent directory of " + erddapDecDsgFlagFile.getPath() +
                    " is not valid");
        ferretConfig = ferretConf;
        this.knownUserDataTypes = knownUserDataTypes;
        this.knownMetadataTypes = knownMetadataTypes;
        this.knownDataFileTypes = knownDataFileTypes;

        try {
            Path dsgFilesDirPath = dsgFilesDir.toPath();
            watcher = FileSystems.getDefault().newWatchService();
            dsgFilesDirPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY).cancel();
            watcher.close();
            watcher = null;
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems creating a watcher for the DSG files directory: " +
                    ex.getMessage(), ex);
        }
    }

    /**
     * Generates the full NetCDF DSG abstract file for a dataset. Creates the parent subdirectory if it does not exist.
     *
     * @param datasetId
     *         ID of the dataset
     *
     * @return full NetCDF DSG abstract file for the dataset
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, or if problems creating the parent subdirectory
     */
    public DsgNcFile getDsgNcFile(String datasetId) throws IllegalArgumentException {
        // Check and standardize the dataset ID
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        // Make sure the parent directory exists
        File parentDir = new File(dsgFilesDir, stdId.substring(0, 4));
        if ( parentDir.exists() ) {
            if ( !parentDir.isDirectory() ) {
                throw new IllegalArgumentException("Parent subdirectory exists but is not a directory: " +
                        parentDir.getPath());
            }
        }
        else if ( !parentDir.mkdirs() ) {
            throw new IllegalArgumentException("Unable to create the new subdirectory " +
                    parentDir.getPath());
        }
        return new DsgNcFile(parentDir, stdId + DSG_FILE_SUFFIX);
    }

    /**
     * Generates the decimated NetCDF DSG abstract file for a dataset.  Creates the parent subdirectory if it does not
     * exist.  The decimated NetCDF DSG file should only be created using {@link #decimateDatasetDsg(String)}.
     *
     * @param datasetId
     *         ID of the dataset
     *
     * @return decimated NetCDF DSG abstract file for the dataset
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid
     */
    public DsgNcFile getDecDsgNcFile(String datasetId) throws IllegalArgumentException {
        // Check and standardize the dataset ID
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        // Make sure the parent directory exists
        File parentDir = new File(decDsgFilesDir, stdId.substring(0, 4));
        if ( parentDir.exists() ) {
            if ( !parentDir.isDirectory() ) {
                throw new IllegalArgumentException("Parent subdirectory exists but is not a directory: " +
                        parentDir.getPath());
            }
        }
        else if ( !parentDir.mkdirs() ) {
            throw new IllegalArgumentException("Unable to create the new subdirectory " +
                    parentDir.getPath());
        }
        return new DsgNcFile(parentDir, stdId + DSG_FILE_SUFFIX);
    }

    /**
     * Saves the metadata and data into a new full-data NetCDF DSG file. After successful creation of the DSG file,
     * ERDDAP will need to be notified of changes to the DSG files.  This notification is not done in this routine so
     * that a single notification event can be made after multiple modifications.
     *
     * @param metadata
     *         metadata for the dataset
     * @param stdUserData
     *         standardized user-provided data
     *
     * @throws IllegalArgumentException
     *         if there are problems with the metadata or data given, or
     *         if there are problems creating or writing the full-data DSG file
     */
    public void saveDatasetDsg(DsgMetadata metadata, StdUserDataArray stdUserData) throws IllegalArgumentException {
        // Get the location and name for the NetCDF DSG file
        DsgNcFile dsgFile = getDsgNcFile(metadata.getDatasetId());

        // Create the NetCDF DSG file
        try {
            dsgFile.create(metadata, stdUserData, knownDataFileTypes);
        } catch ( Exception ex ) {
            dsgFile.delete();
            throw new IllegalArgumentException("Problems creating the DSG file " +
                    dsgFile.getName() + "\n    " + ex.getMessage(), ex);
        }

        // Call Ferret to add the computed variables to the NetCDF DSG file
        SocatTool tool = new SocatTool(ferretConfig);
        ArrayList<String> scriptArgs = new ArrayList<String>(1);
        scriptArgs.add(dsgFile.getPath());
        tool.init(scriptArgs, metadata.getDatasetId(), FerretConfig.Action.COMPUTE);
        tool.run();
        if ( tool.hasError() )
            throw new IllegalArgumentException("Failure adding computed variables: " + tool.getErrorMessage());
    }

    /**
     * Generates the decimated-data NetCDF DSG file from the full-data NetCDF DSG file.  After successful creation of
     * the decimated DSG file, ERDDAP will need to be notified of changes to the decimated DSG files. This notification
     * is not done in this routine so that a single notification event can be made after multiple modifications.
     *
     * @param datasetId
     *         generate the decimated-data DSG file for the dataset with this ID
     *
     * @throws IllegalArgumentException
     *         if there are problems reading the full-data DSG file, or
     *         if there are problems creating or writing the decimated-data DSG file
     */
    public void decimateDatasetDsg(String datasetId) throws IllegalArgumentException {
        // Get the location and name of the full DSG file
        File dsgFile = getDsgNcFile(datasetId);
        if ( !dsgFile.canRead() )
            throw new IllegalArgumentException(
                    "Full DSG file for " + datasetId + " is not readable");

        // Get the location and name for the decimated DSG file
        File decDsgFile = getDecDsgNcFile(datasetId);

        // Call Ferret to create the decimated DSG file from the full DSG file
        SocatTool tool = new SocatTool(ferretConfig);
        ArrayList<String> scriptArgs = new ArrayList<String>(2);
        scriptArgs.add(dsgFile.getPath());
        scriptArgs.add(decDsgFile.getPath());
        tool.init(scriptArgs, datasetId, FerretConfig.Action.DECIMATE);
        tool.run();
        if ( tool.hasError() )
            throw new IllegalArgumentException("Failure decimating the full DSG file: " + tool.getErrorMessage());
    }

    /**
     * Appropriately renames any DSG and decimated DSG files, if they exist, for a change in dataset ID.
     * Changes the dataset ID in the DSG files.
     *
     * @param oldId
     *         standardized old ID for the dataset
     * @param newId
     *         standardized new ID for the dataset
     *
     * @throws IllegalArgumentException
     *         if a DSG or decimated DSG file for the new ID already exists, or
     *         if the contents of the old DSG file are invalid
     * @throws IOException
     *         if unable to regenerate the DSG or decimated DSG file with the new ID from the data
     *         and metadata in the old DSG file, or if unable to delete the old DSG or decimated DSG file
     */
    public void renameDsgFiles(String oldId, String newId) throws IllegalArgumentException, IOException {
        DsgNcFile newDsgFile = getDsgNcFile(newId);
        if ( newDsgFile.exists() )
            throw new IllegalArgumentException("DSG file for " + oldId + " already exist");

        DsgNcFile newDecDsgFile = getDecDsgNcFile(newId);
        if ( newDecDsgFile.exists() )
            throw new IllegalArgumentException("Decimated DSG file for " + oldId + " already exist");

        DsgNcFile oldDsgFile = getDsgNcFile(oldId);
        if ( oldDsgFile.exists() ) {
            // Just re-create the DSG file with the updated metadata
            ArrayList<String> missing = oldDsgFile.readMetadata(knownMetadataTypes);
            if ( !missing.isEmpty() )
                throw new RuntimeException("Unexpected metadata fields missing from the DSG file: " + missing);
            missing = oldDsgFile.readData(knownDataFileTypes);
            if ( !missing.isEmpty() )
                throw new RuntimeException("Unexpected data fields missing from the DSG file: " + missing);
            try {
                StdDataArray dataVals = oldDsgFile.getStdDataArray();
                DsgMetadata updatedMeta = oldDsgFile.getMetadata();
                updatedMeta.setDatasetId(newId);
                newDsgFile.create(updatedMeta, dataVals);
                // Call Ferret to add lon360 and tmonth (calculated data should be the same)
                SocatTool tool = new SocatTool(ferretConfig);
                ArrayList<String> scriptArgs = new ArrayList<String>(1);
                scriptArgs.add(newDsgFile.getPath());
                tool.init(scriptArgs, newId, FerretConfig.Action.COMPUTE);
                tool.run();
                if ( tool.hasError() )
                    throw new IllegalArgumentException(newId + ": Failure adding computed variables: " +
                            tool.getErrorMessage());
                // Re-create the decimated-data DSG file
                decimateDatasetDsg(newId);
                // Delete the old DSG and decimated-data DSG files
                oldDsgFile.delete();
                getDecDsgNcFile(oldId).delete();
            } catch ( Exception ex ) {
                throw new IOException(ex);
            }

            // Tell ERDDAP there are changes
            flagErddap(true, true);
        }

    }

    /**
     * Deletes the DSG and decimated DSG files, if they exist, for a dataset.  If a file was deleted, true is returned
     * and ERDDAP will need to be notified of changes to the DSG and decimated DSG files.  This notification is not
     * done in this routine so that a single notification event can be made after multiple modifications.
     *
     * @param datasetId
     *         delete the DSG and decimated DSG files for the dataset with this ID
     *
     * @return true if a file was deleted
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, or if unable to delete one of the files
     */
    public boolean deleteCruise(String datasetId) throws IllegalArgumentException {
        boolean fileDeleted = false;
        File dsgFile = getDsgNcFile(datasetId);
        if ( dsgFile.exists() ) {
            if ( !dsgFile.delete() )
                throw new IllegalArgumentException("Unable to delete the DSG file for " + datasetId);
            fileDeleted = true;
        }
        File decDsgFile = getDecDsgNcFile(datasetId);
        if ( decDsgFile.exists() ) {
            if ( !decDsgFile.delete() )
                throw new IllegalArgumentException("Unable to delete the decimated DSG file for " + datasetId);
            fileDeleted = true;
        }
        return fileDeleted;
    }

    /**
     * Notifies ERDDAP that content has changed in the DSG files.
     *
     * @param flagDsg
     *         if true, notify ERDDAP that content has changed in the full DSG files.
     * @param flagDecDsg
     *         if true, notify ERDDAP that content has changed in the decimated DSG files.
     *
     * @return true if successful
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
        } catch ( IOException ex ) {
            return false;
        }
        return true;
    }

    /**
     * Reads and returns the array of data values for the specified variable contained in the DSG file
     * for the specified dataset.  The variable must be saved in the DSG file as Strings.  For some
     * variables, the DSG file must have been processed by Ferret for the data values to be meaningful.
     *
     * @param datasetId
     *         get the data values for the dataset with this ID
     * @param varName
     *         name of the variable to read
     *
     * @return array of values for the specified variable
     *
     * @throws FileNotFoundException
     *         if the full-data DSG file does not exist
     * @throws IOException
     *         if there is a problem opening or reading from this DSG file
     * @throws IllegalArgumentException
     *         if the variable name is invalid, or if the variable is not a single-character array variable
     */
    public String[] readStringVarDataValues(String datasetId, String varName)
            throws IllegalArgumentException, FileNotFoundException, IOException {
        DsgNcFile dsgFile = getDsgNcFile(datasetId);
        if ( !dsgFile.exists() )
            throw new FileNotFoundException("Full data DSG file for " + datasetId + " does not exist");
        return dsgFile.readStringVarDataValues(varName);
    }

    /**
     * Reads and returns the array of data values for the specified variable contained in the DSG file
     * for the specified dataset.  The variable must be saved in the DSG file as integers.  For some
     * variables, the DSG file must have been processed by Ferret for the data values to be meaningful.
     *
     * @param datasetId
     *         get the data values for the dataset with this ID
     * @param varName
     *         name of the variable to read
     *
     * @return array of values for the specified variable
     *
     * @throws FileNotFoundException
     *         if the full-data DSG file does not exist
     * @throws IOException
     *         if there is a problem opening or reading from this DSG file
     * @throws IllegalArgumentException
     *         if the variable name is invalid
     */
    public int[] readIntVarDataValues(String datasetId, String varName)
            throws IllegalArgumentException, FileNotFoundException, IOException {
        DsgNcFile dsgFile = getDsgNcFile(datasetId);
        if ( !dsgFile.exists() )
            throw new FileNotFoundException("Full data DSG file for " + datasetId + " does not exist");
        return dsgFile.readIntVarDataValues(varName);
    }

    /**
     * Reads and returns the array of data values for the specified variable contained in the full-data DSG file
     * for the specified dataset.  The variable must be saved in the DSG file as doubles.  NaN and infinite values
     * are changed to {@link DashboardUtils#FP_MISSING_VALUE}.  For some variables, the DSG file must have been
     * processed by Ferret for the data values to be meaningful.
     *
     * @param datasetId
     *         get the data values for the dataset with this ID
     * @param varName
     *         name of the variable to read
     *
     * @return array of values for the specified variable
     *
     * @throws FileNotFoundException
     *         if the full-data DSG file does not exist
     * @throws IOException
     *         if there is a problem opening or reading from this DSG file
     * @throws IllegalArgumentException
     *         if the variable name is invalid
     */
    public double[] readDoubleVarDataValues(String datasetId, String varName)
            throws IllegalArgumentException, FileNotFoundException, IOException {
        DsgNcFile dsgFile = getDsgNcFile(datasetId);
        if ( !dsgFile.exists() )
            throw new FileNotFoundException("Full data DSG file for " + datasetId + " does not exist");
        return dsgFile.readDoubleVarDataValues(varName);
    }

    /**
     * Reads and returns the longitudes, latitudes, and times contained in the full-data DSG file
     * for the specified dataset.  NaN and infinite values are changed to {@link DashboardUtils#FP_MISSING_VALUE}.
     *
     * @param datasetId
     *         get the data values for the cruise with this expocode
     *
     * @return the array { lons, lats, times } from the full-data DSG file,
     *         where lons are the array of longitudes, lats are the array of latitudes,
     *         times are the array of times.
     *
     * @throws IllegalArgumentException
     *         if the expocode is invalid
     * @throws FileNotFoundException
     *         if the full-data DSG file does not exist
     * @throws IOException
     *         if problems opening or reading from the DSG file, or
     *         if any of the data arrays are not given in the DSG file
     */
    public double[][] readLonLatTimeDataValues(String datasetId)
            throws IllegalArgumentException, FileNotFoundException, IOException {
        DsgNcFile dsgFile = getDsgNcFile(datasetId);
        if ( !dsgFile.exists() )
            throw new FileNotFoundException("Full data DSG file for " + datasetId + " does not exist");
        return dsgFile.readLonLatTimeDataValues();
    }

    /**
     * Reads and returns the longitudes, latitudes, times, SST values, and fCO2_recommended values contained
     * in the full-data DSG file for the specified dataset.  NaN and infinite values are changed to
     * {@link DashboardUtils#FP_MISSING_VALUE}.  The full-data DSG file must
     * have been processed by Ferret for the fCO2_recommended values to be meaningful.
     *
     * @param datasetId
     *         get the data values for the dataset with this ID
     *
     * @return the array { lons, lats, times, ssts, fco2s } from the full-data DSG file,
     *         where lons are the array of longitudes, lats are the array of latitudes,
     *         times are the array of times, ssts are the array of SST values, and
     *         fco2s are the array of fCO2_recommended values.
     *
     * @throws IllegalArgumentException
     *         if the expocode is invalid
     * @throws FileNotFoundException
     *         if the full-data DSG file does not exist
     * @throws IOException
     *         if problems opening or reading from the DSG file, or
     *         if any of the data arrays are not given in the DSG file
     */
    public double[][] readLonLatTimeSstFco2DataValues(String datasetId)
            throws IllegalArgumentException, FileNotFoundException, IOException {
        DsgNcFile dsgFile = getDsgNcFile(datasetId);
        if ( !dsgFile.exists() )
            throw new FileNotFoundException("Full data DSG file for " + datasetId + " does not exist");
        return dsgFile.readLonLatTimeSstFco2DataValues();
    }

    /**
     * Updates the dataset QC flag and version number in the full-data and decimated-data DSG files
     * for a dataset.  Flags ERDDAP after making these updates.
     *
     * @param qcEvent
     *         get the dataset ID, dataset QC flag, and version number to assign from this QC event
     *
     * @throws IllegalArgumentException
     *         if the DSG files are not valid
     * @throws IOException
     *         if problems opening or writing to a DSG file
     */
    public void updateDatasetQCFlag(QCEvent qcEvent) throws IllegalArgumentException, IOException {
        // Get the location and name for the NetCDF DSG file
        String datasetId = qcEvent.getDatasetId();
        DsgNcFile dsgFile = getDsgNcFile(datasetId);
        if ( !dsgFile.exists() )
            throw new IllegalArgumentException("DSG file for " + datasetId + " does not exist");
        dsgFile.updateDatasetQCFlag(qcEvent.getFlagValue(), qcEvent.getVersion());
        DsgNcFile decDsgFile = getDecDsgNcFile(datasetId);
        if ( !decDsgFile.exists() )
            throw new IllegalArgumentException("Decimated DSG file for " + datasetId + " does not exist");
        decDsgFile.updateDatasetQCFlag(qcEvent.getFlagValue(), qcEvent.getVersion());
        flagErddap(true, true);
    }

    /**
     * Updates the all_region_ids metadata variable in the DSG files for the indicated dataset
     * using the values in the region_id data variable in the full-data DSG file.
     * ERDDAP is NOT flagged about this change.
     *
     * @param datasetId
     *         update the all_region_ids metadata variable in the DSG files of the dataset with this ID
     *
     * @return the all_region_ids value assigned
     */
    public String updateAllRegionIds(String datasetId) {
        // Compute and assign the all_region_ids variable from the full-data DSG file
        DsgNcFile dsgFile = getDsgNcFile(datasetId);
        if ( !dsgFile.exists() )
            throw new IllegalArgumentException("DSG file for " + datasetId + " does not exist");
        String allRegionIds;
        try {
            allRegionIds = dsgFile.updateAllRegionIDs(null);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException(
                    "Problems resetting all_region_ids in the full-data DSG file for " + datasetId);
        }
        // Assign this all_region_ids value to the decimated-data DSG file
        DsgNcFile decDsgFile = getDecDsgNcFile(datasetId);
        if ( !decDsgFile.exists() )
            throw new IllegalArgumentException("Decimated DSG file for " + datasetId + " does not exist");
        try {
            dsgFile.updateAllRegionIDs(allRegionIds);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException(
                    "Problems resetting all_region_ids in the full-data DSG file for " + datasetId);
        }
        return allRegionIds;
    }

}
