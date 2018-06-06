/**
 *
 */
package gov.noaa.pmel.dashboard.actions;

import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.dsg.StdDataArray;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.handlers.UserFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.DataLocation;
import gov.noaa.pmel.dashboard.server.DataQCEvent;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Methods for revising dataset information, such as dataset owner or dataset ID.
 *
 * @author Karl Smith
 */
public class DatasetModifier {

    private static final SimpleDateFormat DATETIMESTAMPER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        DATETIMESTAMPER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    DashboardConfigStore configStore;

    /**
     * Modifies information about datasets.
     *
     * @param configStore
     *         configuration store to use
     */
    public DatasetModifier(DashboardConfigStore configStore) {
        this.configStore = configStore;
    }

    /**
     * Changes the owner of the data and metadata files for a dataset. The dataset is added to the list of datasets for
     * the new owner.
     *
     * @param datasetId
     *         change the owner of the data and metadata files for the dataset with this dataset
     * @param newOwner
     *         change the owner of the data and metadata files to this username
     *
     * @throws IllegalArgumentException
     *         if the dataset is invalid, if the new owner username is not recognized, if there is no data file for the
     *         indicated dataset
     */
    public void changeDatasetOwner(String datasetId, String newOwner)
            throws IllegalArgumentException {
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        if ( !configStore.validateUser(newOwner) )
            throw new IllegalArgumentException("Unknown dashboard user " + newOwner);

        DataFileHandler dataHandler = configStore.getDataFileHandler();
        DashboardDataset dataset = dataHandler.getDatasetFromInfoFile(stdId);
        String oldOwner = dataset.getOwner();
        dataset.setOwner(newOwner);
        dataHandler.saveDatasetInfoToFile(dataset, "Owner of " + stdId +
                " data file changed from " + oldOwner + " to " + newOwner);

        MetadataFileHandler metaHandler = configStore.getMetadataFileHandler();
        ArrayList<DashboardMetadata> metaList = metaHandler.getMetadataFiles(stdId);
        for (DashboardMetadata mdata : metaList) {
            String oldMetaOwner = mdata.getOwner();
            mdata.setOwner(newOwner);
            metaHandler.saveMetadataInfo(mdata, "Owner of " + stdId +
                    " metadata file changed from " + oldMetaOwner + " to " + newOwner, false);
        }

        UserFileHandler userHandler = configStore.getUserFileHandler();
        String commitMsg = "Dataset " + stdId + " moved from " + oldOwner + " to " + newOwner;

        // Add this dataset to the list for the new owner
        DashboardDatasetList datasetList = userHandler.getDatasetListing(newOwner);
        if ( datasetList.put(stdId, dataset) == null ) {
            userHandler.saveDatasetListing(datasetList, commitMsg);
        }

        // Rely on update-on-read to remove the cruise from the list of the old owner
        // (and others) if they no longer should be able to see this cruise
    }

    /**
     * Appropriately renames dashboard dataset files.  If an exception is thrown, the system is likely have a corrupt
     * mix of renamed and original-name files.
     *
     * @param oldId
     *         current ID for the dataset
     * @param newId
     *         new ID to use for the dataset
     * @param username
     *         user requesting this rename
     *
     * @throws IllegalArgumentException
     *         if the username is not an admin, if either dataset ID is invalid, if files for the old dataset ID do not
     *         exist, if any files for the new dataset ID already exist
     * @throws IOException
     *         if updating a file with the new ID throws one
     * @throws SQLException
     *         if username is not a known user, or if accessing or updating the database throws one
     */
    public void renameDataset(String oldId, String newId, String username)
            throws IllegalArgumentException, IOException, SQLException {
        // check and standardize the dataset IDs
        String oldStdId = DashboardServerUtils.checkDatasetID(oldId);
        String newStdId = DashboardServerUtils.checkDatasetID(newId);
        // rename the dataset data and info files, updating the dataset ID
        configStore.getDataFileHandler().renameDatasetFiles(oldStdId, newStdId);
        // rename the automated data checker messages file, if it exists
        configStore.getCheckerMsgHandler().renameMsgsFile(oldStdId, newStdId);
        // rename metadata files, updating the dataset ID
        configStore.getMetadataFileHandler().renameMetadataFiles(oldStdId, newStdId);
        // rename the DSG and decimated DSG files, updating the dataset ID
        configStore.getDsgNcFileHandler().renameDsgFiles(oldStdId, newStdId);
    }

    /**
     * Applies WOCE-4 flags to any duplicated lon/lat/depth/time/fCO2_rec data points found within a data set.
     * Add the WOCE event to the database, modifies the full-data DSG file, and recreates the decimated-data DSG file.
     * Does not flag ERDDAP as this may be called repeatedly with different expocodes.
     *
     * @param expocode
     *         examine the data of the dataset with this ID
     *
     * @return list of duplicate lon/lat/depth/time/fCO2_rec data points given a WOCE-4 flag
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, or if unable to regenerate the decimated DSG file
     * @throws IOException
     *         if unable to read or update the full-data DSG file, or if unable to read or update the database
     */
    public ArrayList<DataLocation> woceDuplicateDatapoints(String expocode)
            throws IllegalArgumentException, IOException {
        String upperExpo = DashboardServerUtils.checkDatasetID(expocode);

        // Get the metadata and data from the DSG file
        DsgNcFileHandler dsgFileHandler = configStore.getDsgNcFileHandler();
        DsgNcFile dsgFile = dsgFileHandler.getDsgNcFile(upperExpo);
        ArrayList<String> unknownVars = dsgFile.readMetadata(configStore.getKnownMetadataTypes());
        if ( unknownVars.size() > 0 ) {
            String msg = "Unassigned metadata variables: ";
            for (String var : unknownVars) {
                msg += var + "; ";
            }
            System.err.println(msg);
        }
        unknownVars = dsgFile.readData(configStore.getKnownDataFileTypes());
        if ( unknownVars.size() > 0 ) {
            String msg = "Unassigned data variables: ";
            for (String var : unknownVars) {
                msg += var + "; ";
            }
            System.err.println(msg);
        }

        DsgMetadata socatMeta = dsgFile.getMetadata();
        StdDataArray dataArray = dsgFile.getStdDataArray();

        String versionStatus = socatMeta.getVersion();
        // Remove the final 'U' or 'N' off the version-status saved in the DsgMetadata
        String version = versionStatus.substring(0, versionStatus.length() - 1);

        Integer lonIdx = dataArray.getIndexOfType(DashboardServerUtils.LONGITUDE);
        if ( lonIdx == null )
            throw new IllegalArgumentException("Full-data DSG file does not have longitudes");
        Integer latIdx = dataArray.getIndexOfType(DashboardServerUtils.LATITUDE);
        if ( latIdx == null )
            throw new IllegalArgumentException("Full-data DSG file does not have latitudes");
        Integer depthIdx = dataArray.getIndexOfType(DashboardServerUtils.SAMPLE_DEPTH);
        if ( depthIdx == null )
            throw new IllegalArgumentException("Full-data DSG file does not have depths");
        Integer timeIdx = dataArray.getIndexOfType(DashboardServerUtils.TIME);
        if ( timeIdx == null )
            throw new IllegalArgumentException("Full-data DSG file does not have fully-specified times");
        Integer fco2RecIdx = dataArray.getIndexOfType(SocatTypes.FCO2_REC);
        if ( fco2RecIdx == null )
            throw new IllegalArgumentException("Full-data DSG file does not have " +
                    SocatTypes.FCO2_REC.getVarName() + " values");
        Integer woceWaterIdx = dataArray.getIndexOfType(SocatTypes.WOCE_CO2_WATER);
        if ( woceWaterIdx == null )
            throw new IllegalArgumentException("Full-data DSG file does not have " +
                    SocatTypes.WOCE_CO2_WATER.getVarName() + " values");

        // Create the set for holding previous lon/lat/depth/time/fCO2_rec data
        TreeSet<DataLocation> prevDatInf = new TreeSet<DataLocation>(DataLocation.IGNORE_ROW_NUM_COMPARATOR);
        // Create a list for holding any duplicate lon/lat/depth/time/fCO2_rec data
        ArrayList<DataLocation> dupDatInf = new ArrayList<DataLocation>();
        // Process all the data points that are not already WOCE-4,
        // looking for duplicate lon/lat/depth/time/fCO2_rec
        for (int j = 0; j < dataArray.getNumSamples(); j++) {
            String woceFlag = (String) dataArray.getStdVal(j, woceWaterIdx);
            if ( DashboardServerUtils.WOCE_ACCEPTABLE.equals(woceFlag) ||
                    DashboardServerUtils.WOCE_QUESTIONABLE.equals(woceFlag) ) {
                DataLocation datinf = new DataLocation();
                datinf.setRowNumber(j);
                datinf.setLongitude((Double) dataArray.getStdVal(j, lonIdx));
                datinf.setLatitude((Double) dataArray.getStdVal(j, latIdx));
                datinf.setDepth((Double) dataArray.getStdVal(j, depthIdx));
                datinf.setDataDate(new Date(Math.round(1000.0 * (Double) dataArray.getStdVal(j, timeIdx))));
                datinf.setDataValue((Double) dataArray.getStdVal(j, fco2RecIdx));
                if ( !prevDatInf.add(datinf) ) {
                    dupDatInf.add(datinf);
                }
            }
        }

        if ( !dupDatInf.isEmpty() ) {
            // Assign the WOCE-4 flag for duplicates
            DataQCEvent woceEvent = new DataQCEvent();
            woceEvent.setDatasetId(upperExpo);
            woceEvent.setVersion(version);
            woceEvent.setFlagName(SocatTypes.WOCE_CO2_WATER.getVarName());
            woceEvent.setFlagValue(DashboardServerUtils.WOCE_BAD);
            woceEvent.setFlagDate(new Date());
            woceEvent.setComment("duplicate lon/lat/depth/time/fCO2_rec data points detected by automation");
            woceEvent.setUsername(DashboardServerUtils.AUTOMATED_DATA_CHECKER_USERNAME);
            woceEvent.setRealname(DashboardServerUtils.AUTOMATED_DATA_CHECKER_REALNAME);
            woceEvent.setVarName(SocatTypes.FCO2_REC.getVarName());
            woceEvent.setLocations(dupDatInf);
            // Add the WOCE event to the database
            try {
                configStore.getDatabaseRequestHandler().addDataQCEvent(Arrays.asList(woceEvent));
            } catch ( SQLException ex ) {
                throw new IOException("Problem assigning WOCE-4 flags in database: " + ex.getMessage());
            }
            // Assign the WOCE-4 flags in the full-data DSG file
            ArrayList<DataLocation> unidentified = dsgFile.updateDataQCFlags(woceEvent, false);
            if ( !unidentified.isEmpty() ) {
                for (DataLocation loc : unidentified) {
                    System.err.println("unexpected unknown data location: " + loc.toString());
                }
                throw new IOException("Problem assigning WOCE-4 flags in the full-data DSG file");
            }
            // Re-create the decimated-data DSG file
            try {
                dsgFileHandler.decimateDatasetDsg(upperExpo);
            } catch ( Exception ex ) {
                throw new IOException("Unable to decimate the updated full-data DSG file: " + ex.getMessage());
            }
        }

        return dupDatInf;
    }

}
