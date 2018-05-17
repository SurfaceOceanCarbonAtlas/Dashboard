/**
 *
 */
package gov.noaa.pmel.dashboard.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gov.noaa.pmel.dashboard.actions.DatasetChecker;
import gov.noaa.pmel.dashboard.actions.DatasetModifier;
import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.handlers.UserFileHandler;
import gov.noaa.pmel.dashboard.shared.ADCMessageList;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCEvent;
import gov.noaa.pmel.dashboard.shared.TypesDatasetDataPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Implementation of DashboardServicesInterface
 *
 * @author Karl Smith
 */
public class DashboardServices extends RemoteServiceServlet implements DashboardServicesInterface {

    private static final long serialVersionUID = -8189933983319827049L;

    private String username = null;
    private DashboardConfigStore configStore = null;

    @Override
    public void logoutUser() {
        HttpServletRequest request = getThreadLocalRequest();
        username = null;
        try {
            username = DashboardServerUtils.cleanUsername(request.getUserPrincipal().getName().trim());
        } catch ( Exception ex ) {
            // Probably null pointer exception - leave username null
        }
        HttpSession session = request.getSession(false);
        try {
            session.invalidate();
        } catch ( Exception ex ) {
            // Log but otherwise ignore this error
            LogManager.getLogger("DashboardServices").error("session.invalidate failed: " + ex.getMessage());
        }
        try {
            request.logout();
        } catch ( Exception ex ) {
            LogManager.getLogger("DashboardServices").error("request.logout failed: " + ex.getMessage());
        }

        LogManager.getLogger("DashboardServices").info("logged out " + username);
    }

    /**
     * Validates the given request by retrieving the current username from the request and verifying that username with
     * the Dashboard data store.  If pageUsername is given, also checks these usernames are the same. Assigns the
     * username and configStore fields in this instance.
     *
     * @param pageUsername
     *         if not null, check that this matches the current page username
     *
     * @return true if the request obtained a valid username; otherwise false
     *
     * @throws IllegalArgumentException
     *         if unable to obtain the dashboard data store
     */
    private boolean validateRequest(String pageUsername) throws IllegalArgumentException {
        username = null;
        HttpServletRequest request = getThreadLocalRequest();
        try {
            username = DashboardServerUtils.cleanUsername(request.getUserPrincipal().getName().trim());
        } catch ( Exception ex ) {
            // Probably null pointer exception
            return false;
        }
        if ( (pageUsername != null) && !pageUsername.equals(username) )
            return false;

        configStore = null;
        try {
            configStore = DashboardConfigStore.get(true);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Unexpected configuration error: " + ex.getMessage());
        }
        return configStore.validateUser(username);
    }

    @Override
    public DashboardDatasetList getDatasetList() throws IllegalArgumentException {
        // Get the dashboard data store and current username
        if ( !validateRequest(null) )
            throw new IllegalArgumentException("Invalid user request");
        DashboardDatasetList datasetList = configStore.getUserFileHandler().getDatasetListing(username);
        LogManager.getLogger("DashboardServices").info("dataset list returned for " + username);
        return datasetList;
    }

    @Override
    public DashboardDatasetList deleteDatasets(String pageUsername, TreeSet<String> idsSet,
            Boolean deleteMetadata) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        DataFileHandler dataHandler = configStore.getDataFileHandler();
        // Delete each of the datesets in the given set
        for (String datasetId : idsSet) {
            dataHandler.deleteDatasetFiles(datasetId, username, deleteMetadata);
            // IllegalArgumentException for other problems escape as-is
            LogManager.getLogger("DashboardServices").info("dataset " + datasetId + " deleted by " + username);
        }

        // Return the current list of datasets, which should
        // detect the missing datasets and update itself
        DashboardDatasetList datasetList = configStore.getUserFileHandler().getDatasetListing(username);
        LogManager.getLogger("DashboardServices").info("dataset list returned for " + username);
        return datasetList;
    }

    @Override
    public DashboardDatasetList addDatasetsToList(String pageUsername,
            String wildDatasetId) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        // Add the datasets to the user's list and return the updated list
        DashboardDatasetList cruiseList = configStore.getUserFileHandler()
                                                     .addDatasetsToListing(wildDatasetId, username);
        LogManager.getLogger("DashboardServices").info("added datasets " + wildDatasetId + " for " + username);
        return cruiseList;
    }

    @Override
    public DashboardDatasetList removeDatasetsFromList(String pageUsername,
            TreeSet<String> idsSet) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        // Remove the datasets from the user's list and return the updated list
        DashboardDatasetList datasetList = configStore.getUserFileHandler()
                                                      .removeDatasetsFromListing(idsSet, username);
        LogManager.getLogger("DashboardServices").info("removed datasets " + idsSet.toString() + " for " + username);
        return datasetList;
    }

    @Override
    public DashboardDatasetList changeDatasetOwner(String pageUsername,
            TreeSet<String> idsSet, String newOwner)
            throws IllegalArgumentException {
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");
        // Get the dashboard username of the new owner
        String newUsername;
        if ( configStore.validateUser(newOwner) ) {
            // dashboard username was given
            newUsername = newOwner;
        }
        else {
            // actual name given?
            try {
                newUsername = configStore.getDatabaseRequestHandler().getReviewerUsername(newOwner);
            } catch ( Exception ex ) {
                newUsername = null;
            }
            if ( (newUsername == null) || !configStore.validateUser(newUsername) )
                throw new IllegalArgumentException("Unknown dashboard user " + newOwner);
        }
        // Change the owner of the datasets
        DatasetModifier modifier = new DatasetModifier(configStore);
        Logger itsLogger = LogManager.getLogger("DashboardServices");
        for (String datasetId : idsSet) {
            modifier.changeDatasetOwner(datasetId, newUsername);
            itsLogger.info("changed owner of " + datasetId + " to " + newUsername);
        }
        // Return the updated list of cruises for this user
        DashboardDatasetList datasetList = configStore.getUserFileHandler().getDatasetListing(pageUsername);
        return datasetList;
    }

    @Override
    public DashboardDatasetList getUpdatedDatasets(String pageUsername,
            TreeSet<String> idsSet) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        // Create the set of updated dataset information to return
        DataFileHandler dataHandler = configStore.getDataFileHandler();
        DashboardDatasetList datasetList = new DashboardDatasetList();
        datasetList.setUsername(username);
        datasetList.setManager(configStore.isManager(username));
        for (String datasetId : idsSet) {
            datasetList.put(datasetId, dataHandler.getDatasetFromInfoFile(datasetId));
        }
        LogManager.getLogger("DashboardServices").info("returned updated dataset information for " + username);
        return datasetList;
    }

    @Override
    public DashboardDatasetList deleteAddlDoc(String pageUsername, String deleteFilename,
            String datasetId, TreeSet<String> allIds)
            throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        DataFileHandler dataHandler = configStore.getDataFileHandler();
        DashboardDataset dataset = dataHandler.getDatasetFromInfoFile(datasetId);

        // Get the current metadata documents for the cruise
        MetadataFileHandler mdataHandler = configStore.getMetadataFileHandler();
        if ( DashboardUtils.OME_FILENAME.equals(deleteFilename) ) {
            // Remove the OME XML stub file
            if ( !Boolean.TRUE.equals(dataset.isEditable()) )
                throw new IllegalArgumentException("Cannot delete the OME metadata for a submitted dataset");
        }
        else if ( DashboardUtils.PI_OME_FILENAME.equals(deleteFilename) ) {
            // No more PI-provided OME metadata for this cruise
            dataset.setOmeTimestamp(null);
        }
        else {
            // Directly modify the additional documents list in this dataset
            TreeSet<String> addlDocs = dataset.getAddlDocs();
            // Find this additional document for this cruise
            String titleToRemove = null;
            for (String docTitle : addlDocs) {
                String name = DashboardMetadata.splitAddlDocsTitle(docTitle)[0];
                if ( name.equals(deleteFilename) ) {
                    titleToRemove = docTitle;
                    break;
                }
            }
            if ( (titleToRemove == null) || !addlDocs.remove(titleToRemove) )
                throw new IllegalArgumentException("Document " + deleteFilename +
                        " is not associated with dataset " + datasetId);
        }
        // Delete this OME metadata or additional documents file on the server
        mdataHandler.deleteMetadata(username, datasetId, deleteFilename);

        LogManager.getLogger("DashboardServices").info("deleted metadata " + deleteFilename +
                " from " + datasetId + " for " + username);

        // Save the updated cruise
        dataHandler.saveDatasetInfoToFile(dataset, "Removed metadata document " +
                deleteFilename + " from dataset " + datasetId);

        // If the dataset has been submitted, add QC update ('U') global flag about removal of metadata
        if ( !Boolean.TRUE.equals(dataset.isEditable()) ) {
            QCEvent qcEvent = new QCEvent();
            qcEvent.setDatasetId(datasetId);
            qcEvent.setFlagValue(DashboardServerUtils.DATASET_QCFLAG_UPDATED);
            qcEvent.setFlagDate(new Date());
            qcEvent.setRegionId(DashboardUtils.REGION_ID_GLOBAL);
            qcEvent.setVersion(configStore.getUploadVersion());
            qcEvent.setUsername(username);
            String comment = "Deleted metadata file \"" + deleteFilename +
                    "\".  Data and WOCE flags were not changed.";
            qcEvent.setComment(comment);
            try {
                // Add the 'U' QC flag with the current upload version
                configStore.getDatabaseRequestHandler().addDatasetQCEvent(qcEvent);
                configStore.getDsgNcFileHandler().updateDatasetQCFlag(qcEvent);
                // Update the dashboard status
                dataset.setSubmitStatus(DashboardUtils.STATUS_SUBMITTED);
                if ( dataset.isEditable() == null ) {
                    dataset.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_WITH_NEXT_RELEASE);
                }
                dataHandler.saveDatasetInfoToFile(dataset, comment);
                LogManager.getLogger("DashboardServices").info("updated QC status for " + datasetId);
            } catch ( Exception ex ) {
                // Should not fail.  If does, record but otherwise ignore the failure.
                LogManager.getLogger("DashboardServices").error("failed to update QC status for " +
                        datasetId + " after deleting metadata " + deleteFilename +
                        " from " + datasetId + " for " + username + ": " + ex.getMessage());
            }
        }

        // Create the set of updated dataset information to return
        DashboardDatasetList datasetList = new DashboardDatasetList();
        datasetList.setUsername(username);
        datasetList.setManager(configStore.isManager(username));
        for (String id : allIds) {
            datasetList.put(id, dataHandler.getDatasetFromInfoFile(id));
        }
        LogManager.getLogger("DashboardServices").info("returned updated dataset information for " + username);
        return datasetList;
    }

    @Override
    public TypesDatasetDataPair getDataColumnSpecs(String pageUsername,
            String datasetId) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        // Get the list of known user-provided data column types
        KnownDataTypes knownUserTypes = configStore.getKnownUserDataTypes();
        if ( knownUserTypes == null )
            throw new IllegalArgumentException("unexpected missing list of all known data column types");
        TreeSet<DashDataType<?>> knownTypesSet = knownUserTypes.getKnownTypesSet();
        if ( knownTypesSet.isEmpty() )
            throw new IllegalArgumentException("unexpected empty list of all known data column types");
        ArrayList<DataColumnType> knownTypesList = new ArrayList<DataColumnType>(knownTypesSet.size());
        for (DashDataType<?> dtype : knownTypesSet) {
            knownTypesList.add(dtype.duplicate());
        }

        // Get the cruise with the first maximum-needed number of rows
        DashboardDatasetData dataset = configStore.getDataFileHandler()
                                                  .getDatasetDataFromFiles(datasetId, 0,
                                                          DashboardUtils.MAX_ROWS_PER_GRID_PAGE);
        if ( dataset == null )
            throw new IllegalArgumentException(datasetId + " does not exist");

        TypesDatasetDataPair typesAndDataset = new TypesDatasetDataPair();
        typesAndDataset.setAllKnownTypes(knownTypesList);
        typesAndDataset.setDatasetData(dataset);

        LogManager.getLogger("DashboardServices").info("data columns specs returned for " +
                datasetId + " for " + username);
        // Return the cruise with the partial data
        return typesAndDataset;
    }

    @Override
    public ArrayList<ArrayList<String>> getDataWithRowNum(String pageUsername,
            String datasetId, int firstRow, int numRows)
            throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        int myFirstRow = firstRow;
        if ( myFirstRow < 0 )
            myFirstRow = 0;
        // Get only the desired data from the dataset
        DashboardDatasetData dataset = configStore.getDataFileHandler()
                                                  .getDatasetDataFromFiles(datasetId, myFirstRow, numRows);
        if ( dataset == null )
            throw new IllegalArgumentException(datasetId + " does not exist");
        ArrayList<ArrayList<String>> dataWithRowNums = dataset.getDataValues();
        ArrayList<Integer> rowNums = dataset.getRowNums();
        // Modify the list in this DashboardDatasetData since it is then thrown away
        int k = 0;
        for (ArrayList<String> rowData : dataWithRowNums) {
            rowData.add(0, rowNums.get(k).toString());
            k++;
        }
        int myLastRow = myFirstRow + dataWithRowNums.size() - 1;
        Logger myLogger = LogManager.getLogger("DashboardServices");
        myLogger.info(datasetId + " dataset data [" + Integer.toString(myFirstRow) +
                " - " + Integer.toString(myLastRow) + "] returned for " + username);
        if ( myLogger.isDebugEnabled() ) {
            for (k = 0; k < dataWithRowNums.size(); k++) {
                myLogger.debug("  data[" + Integer.toString(k) + "]=" + dataWithRowNums.get(k).toString());
            }
        }
        return dataWithRowNums;
    }

    @Override
    public void saveDataColumnSpecs(String pageUsername, DashboardDataset newSpecs) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        // Retrieve all the current cruise data
        DashboardDatasetData dataset = configStore.getDataFileHandler()
                                                  .getDatasetDataFromFiles(newSpecs.getDatasetId(), 0, -1);
        if ( !dataset.isEditable() )
            throw new IllegalArgumentException(newSpecs.getDatasetId() +
                    " has been submitted for QC; data column types cannot be modified.");

        // Revise the data column types and units
        if ( newSpecs.getDataColTypes().size() != dataset.getDataColTypes().size() )
            throw new IllegalArgumentException("Unexpected number of data columns (" +
                    newSpecs.getDataColTypes().size() + " instead of " + dataset.getDataColTypes().size());
        dataset.setDataColTypes(newSpecs.getDataColTypes());

        // Save and commit the updated data columns
        configStore.getDataFileHandler().saveDatasetInfoToFile(dataset,
                "Data column types, units, and missing values for " + dataset
                        .getDatasetId() + " updated by " + username);
        // Update the user-specific data column names to types, units, and missing values
        configStore.getUserFileHandler().updateUserDataColumnTypes(dataset, username);
        if ( !username.equals(dataset.getOwner()) )
            configStore.getUserFileHandler().updateUserDataColumnTypes(dataset, dataset.getOwner());

        LogManager.getLogger("DashboardServices").info("data columns specs saved for " +
                dataset.getDatasetId() + " by " + username);
    }

    @Override
    public DashboardDatasetData updateDataColumnSpecs(String pageUsername,
            DashboardDataset newSpecs) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        // Retrieve all the current cruise data
        DashboardDatasetData dataset = configStore.getDataFileHandler()
                                                  .getDatasetDataFromFiles(newSpecs.getDatasetId(), 0, -1);
        if ( !dataset.isEditable() )
            throw new IllegalArgumentException(newSpecs.getDatasetId() +
                    " has been submitted for QC; data column types cannot be modified.");

        // Revise the data column types and units
        if ( newSpecs.getDataColTypes().size() != dataset.getDataColTypes().size() )
            throw new IllegalArgumentException("Unexpected number of data columns (" +
                    newSpecs.getDataColTypes().size() + " instead of " + dataset.getDataColTypes().size());
        dataset.setDataColTypes(newSpecs.getDataColTypes());

        // Run the automated data checker with the updated data types.
        // Assigns the data check status and the WOCE-3 and WOCE-4 flags.
        configStore.getDashboardDatasetChecker().standardizeDataset(dataset, null);

        // Save and commit the updated data columns
        configStore.getDataFileHandler().saveDatasetInfoToFile(dataset,
                "Data column types, units, and missing values for " + dataset
                        .getDatasetId() + " updated by " + username);
        // Update the user-specific data column names to types, units, and missing values
        configStore.getUserFileHandler().updateUserDataColumnTypes(dataset, username);
        if ( !username.equals(dataset.getOwner()) )
            configStore.getUserFileHandler().updateUserDataColumnTypes(dataset, dataset.getOwner());

        // Remove all but the first maximum-needed number of rows of cruise data
        // to minimize the payload of the returned cruise data
        int numRows = dataset.getNumDataRows();
        if ( numRows > DashboardUtils.MAX_ROWS_PER_GRID_PAGE ) {
            dataset.getDataValues()
                   .subList(DashboardUtils.MAX_ROWS_PER_GRID_PAGE, numRows)
                   .clear();
            dataset.getRowNums()
                   .subList(DashboardUtils.MAX_ROWS_PER_GRID_PAGE, numRows)
                   .clear();
        }

        LogManager.getLogger("DashboardServices").info("data columns specs updated for " +
                dataset.getDatasetId() + " by " + username);
        // Return the updated truncated cruise data for redisplay
        // in the DataColumnSpecsPage
        return dataset;
    }

    @Override
    public void updateDataColumns(String pageUsername,
            ArrayList<String> idsList) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        DataFileHandler dataHandler = configStore.getDataFileHandler();
        UserFileHandler userHandler = configStore.getUserFileHandler();
        DatasetChecker datasetChecker = configStore.getDashboardDatasetChecker();
        Logger dataSpecsLogger = LogManager.getLogger("DashboardServices");

        for (String datasetId : idsList) {
            // Retrieve all the current data
            DashboardDatasetData dataset = dataHandler.getDatasetDataFromFiles(datasetId, 0, -1);
            if ( !dataset.isEditable() )
                throw new IllegalArgumentException("Dataset " + datasetId +
                        " has been submitted for QC; data column types cannot be modified.");

            try {
                // Identify the columns from stored names-to-types for this user
                userHandler.assignDataColumnTypes(dataset);
                // Save and commit these column assignments in case the sanity checker has problems
                dataHandler.saveDatasetInfoToFile(dataset, "Column types for " + datasetId +
                        " updated by " + username + " from post-processing a multiple-dataset upload");

                // Run the automated data checker with the updated data types.  Saves the messages,
                // and assigns the data check status and the WOCE-3 and WOCE-4 flags.
                datasetChecker.standardizeDataset(dataset, null);

                // Save and commit the updated dataset information
                dataHandler.saveDatasetInfoToFile(dataset, "Automated data check status and flags for " +
                        datasetId + " updated by " + username + " from post-processing a multiple-dataset upload");
                dataSpecsLogger.info("Updated data column specs for " + datasetId + " for " + username);
            } catch ( Exception ex ) {
                // ignore problems (such as unidentified columns) - cruise will not have been updated
                dataSpecsLogger.error("Unable to update data column specs for " + datasetId + ": " + ex.getMessage());
                continue;
            }
        }
    }

    @Override
    public ADCMessageList getDataMessages(String pageUsername,
            String datasetId) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        // Get the list of saved automated data checker messages for this dataset
        ADCMessageList scMsgList;
        try {
            scMsgList = configStore.getCheckerMsgHandler().getCheckerMessages(datasetId);
        } catch ( FileNotFoundException ex ) {
            throw new IllegalArgumentException("The automated data checker has never been run on dataset " + datasetId);
        }
        scMsgList.setUsername(username);
        LogManager.getLogger("DashboardServices")
                  .info("returned automated data checker messages for " + datasetId + " for " + username);
        return scMsgList;
    }

    @Override
    public String getOmeXmlPath(String pageUsername, String datasetId,
            String previousId) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");
        MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();

        if ( !previousId.isEmpty() ) {
            // Read the OME XML contents for the previous dataset
            DashboardMetadata mdata = metadataHandler.getMetadataInfo(previousId, DashboardUtils.OME_FILENAME);
            DashboardOmeMetadata updatedOmeMData = new DashboardOmeMetadata(mdata, metadataHandler);
            // Reset the ID and related fields to that for this dataset
            updatedOmeMData.changeDatasetID(datasetId);
            // Read the OME XML contents currently saved for activeExpocode
            mdata = metadataHandler.getMetadataInfo(datasetId, DashboardUtils.OME_FILENAME);
            DashboardOmeMetadata origOmeMData = new DashboardOmeMetadata(mdata, metadataHandler);
            // Create the merged OME and save the results
            DashboardOmeMetadata mergedOmeMData = origOmeMData.mergeModifiable(updatedOmeMData);
            metadataHandler.saveAsOmeXmlDoc(mergedOmeMData, "Merged OME of " + previousId +
                    " into OME of " + datasetId);
        }

        // return the absolute path to the OME.xml for activeExpcode
        File omeFile = metadataHandler.getMetadataFile(datasetId, DashboardUtils.OME_FILENAME);
        return omeFile.getAbsolutePath();
    }

    @Override
    public boolean buildPreviewImages(String pageUsername, String datasetId,
            String timetag, boolean firstCall) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        // Generate the preview plots for this dataset
        // TODO: refactor so starts this in a separate thread when firstCall is true and
        //       returns false, then when gets called again with firstCall is false for
        //       a status update, returns false if still working and true if all plots are done
        if ( firstCall )
            configStore.getPreviewPlotsHandler().createPreviewPlots(datasetId, timetag);
        return true;
    }

    @Override
    public void submitDatasetsForQC(String pageUsername, HashSet<String> idsSet, String archiveStatus,
            String timestamp, boolean repeatSend) throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        // Submit the datasets for QC and possibly send to be archived
        configStore.getDashboardDatasetSubmitter().submitDatasets(idsSet,
                archiveStatus, timestamp, repeatSend, username);
        LogManager.getLogger("DashboardServices").info("datasets " + idsSet.toString() +
                " submitted by " + username);
    }

    @Override
    public void suspendDatasets(String pageUsername, HashSet<String> idsSet, String timestamp)
            throws IllegalArgumentException {
        // Get the dashboard data store and current username, and validate that username
        if ( !validateRequest(pageUsername) )
            throw new IllegalArgumentException("Invalid user request");

        for (String datasetId : idsSet) {
            String message = "Suspending dataset " + datasetId;
            DataFileHandler df = configStore.getDataFileHandler();
            DashboardDataset ds = df.getDatasetFromInfoFile(datasetId);
            ds.setSubmitStatus(DashboardUtils.STATUS_SUSPENDED);
            df.saveDatasetInfoToFile(ds, message);
            // TODO: Need to add QCFlag to database and set in DSG file
        }
        LogManager.getLogger("DashboardServices").info("datasets " + idsSet.toString() +
                " suspended by " + username);
    }

}
