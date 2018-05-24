package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.actions.DatasetChecker;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.dsg.StdUserDataArray;
import gov.noaa.pmel.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.dashboard.ferret.SocatTool;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;


public class PreviewPlotsHandler {

    File dsgFilesDir;
    File plotsFilesDir;
    DataFileHandler dataHandler;
    DatasetChecker dataChecker;
    KnownDataTypes knownMetadataTypes;
    KnownDataTypes knownDataFileTypes;
    FerretConfig ferretConfig;

    /**
     * Create with the given directories for the preview DSG files and plots.
     *
     * @param previewDsgsDirName
     *         directory to contain the preview DSG files
     * @param previewPlotsDirName
     *         directory to contain the preview plots
     * @param configStore
     *         get the DataFileHandler, DatasetChecker, MetadataFileHandler, and FerretConfig from here
     */
    public PreviewPlotsHandler(String previewDsgsDirName, String previewPlotsDirName,
            DashboardConfigStore configStore) {
        dsgFilesDir = new File(previewDsgsDirName);
        if ( !dsgFilesDir.isDirectory() )
            throw new IllegalArgumentException(previewDsgsDirName + " is not a directory");
        plotsFilesDir = new File(previewPlotsDirName);
        if ( !plotsFilesDir.isDirectory() )
            throw new IllegalArgumentException(previewPlotsDirName + " is not a directory");
        dataHandler = configStore.getDataFileHandler();
        dataChecker = configStore.getDashboardDatasetChecker();
        knownMetadataTypes = configStore.getKnownMetadataTypes();
        knownDataFileTypes = configStore.getKnownDataFileTypes();
        ferretConfig = configStore.getFerretConfig();
    }

    /**
     * Returns the virtual file naming the directory that will contain the preview DSG file of a dataset.  Creates this
     * directory if it does not exist.
     *
     * @param datasetId
     *         ID of the dataset to use
     *
     * @return preview DSG directory of the dataset
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, if there are problems creating the directory
     */
    public File getDatasetPreviewDsgDir(String datasetId) throws IllegalArgumentException {
        // Check and standardize the dataset ID
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        // Create the preview DSG subdirectory if it does not exist
        File datasetDsgDir = new File(dsgFilesDir, stdId.substring(0, 4));
        if ( datasetDsgDir.exists() ) {
            if ( !datasetDsgDir.isDirectory() ) {
                throw new IllegalArgumentException("Preview DSG file subdirectory exists but is not a directory: " +
                        datasetDsgDir.getPath());
            }
        }
        else if ( !datasetDsgDir.mkdirs() ) {
            throw new IllegalArgumentException("Cannot create the preview DSG file subdirectory " +
                    datasetDsgDir.getPath());
        }
        return datasetDsgDir;
    }

    /**
     * Returns the virtual file naming the directory that will contain the preview plots directory for a dataset.
     * Creates this directory if it does not exist.
     *
     * @param datasetId
     *         ID of the dataset to use
     *
     * @return preview plots directory for the dataset
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, or if there are problems creating the directory
     */
    public File getDatasetPreviewPlotsDir(String datasetId) throws IllegalArgumentException {
        // Check and standardize the dataset ID
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        // Create the preview plots subdirectory if it does not exist
        File datasetPlotsDir = new File(plotsFilesDir, stdId.substring(0, 4));
        if ( datasetPlotsDir.exists() ) {
            if ( !datasetPlotsDir.isDirectory() ) {
                throw new IllegalArgumentException("Plots directory exists but is not a directory: " +
                        datasetPlotsDir.getPath());
            }
        }
        else if ( !datasetPlotsDir.mkdirs() ) {
            throw new IllegalArgumentException("Cannot create the preview plots file subdirectory " +
                    datasetPlotsDir.getPath());
        }
        return datasetPlotsDir;
    }

    /**
     * Generates the data preview plots for the given dataset.  The data is checked and standardized, the preview DSG
     * file is created, Ferret is called to add the computed variables to the DSG file, and finally Ferret is called to
     * generate the data preview plots from the data in the DSG file.
     *
     * @param datasetId
     *         ID of the dataset to preview
     * @param timetag
     *         time tag to add to the end of the names of the plots (before the filename extension)
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, if there is an error reading the data or OME metadata, if there is an error
     *         checking and standardizing the data, if there is an error generating the preview DSG file, or if there is
     *         an error generating the preview data plots
     */
    public void createPreviewPlots(String datasetId, String timetag) throws IllegalArgumentException {
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        Logger log = LogManager.getLogger("PreviewPlotsHandler");
        log.debug("reading data for " + stdId);

        // Get the complete original cruise data
        DashboardDatasetData dataset = dataHandler.getDatasetDataFromFiles(stdId, 0, -1);

        log.debug("standardizing data for " + stdId);

        // Just create a minimal DsgMetadata to create the preview DSG file
        DsgMetadata dsgMData = new DsgMetadata(knownMetadataTypes);
        dsgMData.setDatasetId(stdId);
        dsgMData.setVersion(dataset.getVersion());

        // TODO: update DsgMetadata with metadata derived from data
        // Although probably not important for the preview plots.
        StdUserDataArray stdUserData = dataChecker.standardizeDataset(dataset, null);
        if ( DashboardUtils.CHECK_STATUS_UNACCEPTABLE.equals(dataset.getDataCheckStatus()) )
            throw new IllegalArgumentException(stdId + ": unacceptable; check data check error messages " +
                    "(missing lon/lat/depth/time or uninterpretable values)");

        // Get the preview DSG filename, creating the parent directory if it does not exist
        DsgNcFile dsgFile = new DsgNcFile(getDatasetPreviewDsgDir(stdId),
                stdId + "_" + timetag + ".nc");

        log.debug("generating preview DSG file " + dsgFile.getPath());

        // Create the preview NetCDF DSG file
        try {
            dsgFile.createFromUserData(dsgMData, stdUserData, knownDataFileTypes);
        } catch ( Exception ex ) {
            dsgFile.delete();
            throw new IllegalArgumentException("Problems creating the preview DSG file for " +
                    datasetId + ": " + ex.getMessage(), ex);
        }

        log.debug("adding computed variables to preview DSG file " + dsgFile.getPath());

        // Call Ferret to add the computed variables to the preview DSG file
        SocatTool tool = new SocatTool(ferretConfig);
        ArrayList<String> scriptArgs = new ArrayList<String>(1);
        scriptArgs.add(dsgFile.getPath());
        tool.init(scriptArgs, stdId, FerretConfig.Action.COMPUTE);
        tool.run();
        if ( tool.hasError() )
            throw new IllegalArgumentException("Failure adding computed variables to the preview DSG file for " +
                    datasetId + ": " + tool.getErrorMessage());

        log.debug("generating preview plots for " + dsgFile.getPath());

        // Get the location for the preview plots, creating the directory if it does not exist
        String cruisePlotsDirname = getDatasetPreviewPlotsDir(stdId).getPath();

        // Call Ferret to generate the plots from the preview DSG file
        tool = new SocatTool(ferretConfig);
        scriptArgs.add(cruisePlotsDirname);
        scriptArgs.add(timetag);
        tool.init(scriptArgs, stdId, FerretConfig.Action.PLOTS);
        tool.run();
        if ( tool.hasError() )
            throw new IllegalArgumentException("Failure generating data preview plots for " +
                    datasetId + ": " + tool.getErrorMessage());

        log.debug("preview plots generated in " + cruisePlotsDirname);
    }

}
