package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.noaa.pmel.socatmetadata.shared.core.SocatMetadata;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Client side interface for most dashboard server functions.
 *
 * @author Karl Smith
 */
public interface DashboardServicesInterfaceAsync {

    /**
     * Client side request to log out the current user.
     *
     * @param callback
     *         the callback to make after logout.
     */
    void logoutUser(AsyncCallback<Void> callback);

    /**
     * Client side request to get the current user's list of cruises.
     *
     * @param callback
     *         the callback to make with the cruise list.
     */
    void getDatasetList(AsyncCallback<DashboardDatasetList> callback);

    /**
     * Client side request to deletes all files for the indicated cruises.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetIds
     *         datasets to be deleted
     * @param deleteMetadata
     *         also delete metadata and additional documents?
     * @param callback
     *         the callback to make with the updated dataset list for the current user
     */
    void deleteDatasets(String username, TreeSet<String> datasetIds, Boolean deleteMetadata,
            AsyncCallback<DashboardDatasetList> callback);

    /**
     * Client side request to add the indicated datasets to the current user's list of datasets.
     *
     * @param username
     *         name of the current user - for validation
     * @param wildDatasetId
     *         ID, possibly with wildcards * and ?, of datasets to add
     * @param callback
     *         the callback to make with the current user's updated dataset list
     */
    void addDatasetsToList(String username, String wildDatasetId, AsyncCallback<DashboardDatasetList> callback);

    /**
     * Client side request to remove the indicated datasets from the current user's list of datasets (but does not
     * delete any files for these datasets).
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetIds
     *         datasets to be removed
     * @param callback
     *         the callback to make with the current user's updated dataset list
     */
    void removeDatasetsFromList(String username, TreeSet<String> datasetIds,
            AsyncCallback<DashboardDatasetList> callback);

    /**
     * Client side request to change the owner of the given cruises to the indicated new owner.
     *
     * @param username
     *         name of the current user - for validation and current ownership of cruises
     * @param datasetIds
     *         change the owner of datasets with these IDs
     * @param newOwner
     *         dashboard username of the new owner of these datasets
     * @param callback
     *         the callback to make with the current user's updated cruise list
     */
    void changeDatasetOwner(String username, TreeSet<String> datasetIds, String newOwner,
            AsyncCallback<DashboardDatasetList> callback);

    /**
     * Client side request to return the latest information for the indicated datasets.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetIds
     *         set of all datasets to include in the returned updated dataset information
     * @param callback
     *         the callback to make with the updated cruise information
     */
    void getUpdatedDatasets(String username, TreeSet<String> datasetIds, AsyncCallback<DashboardDatasetList> callback);

    /**
     * Client side request to remove (delete) an ancillary document for a dataset.
     *
     * @param username
     *         name of the current user - for validation
     * @param deleteFilename
     *         remove the ancillary document with this name
     * @param datasetId
     *         remove the ancillary document from this dataset
     * @param allDatasetIds
     *         IDs of all datasets to include in the returned updated dataset information
     * @param callback
     *         the callback to make with the updated cruise information
     */
    void deleteAddlDoc(String username, String deleteFilename, String datasetId, TreeSet<String> allDatasetIds,
            AsyncCallback<DashboardDatasetList> callback);

    /**
     * Reads the saved dataset file and returns the current data column specifications as well as data for some initial
     * samples to assist in identifying data columns.
     *
     * @param username
     *         username for validation
     * @param datasetId
     *         generate report for this dataset
     * @param callback
     *         callback to make with the current dataset data column specifications and initial (partial) sample data.
     *         The fail method is invoked if authentication fails, if dataset ID is invalid, if the dataset does not
     *         exist, or if there are problems obtaining the data for the dataset
     */
    void getDataColumnSpecs(String username, String datasetId, AsyncCallback<TypesDatasetDataPair> callback);

    /**
     * Reads the saved dataset file and returns the specified rows of data.  The outer list contains the rows of data;
     * the inner list contains the columns of data for that row.  (Thus, each row is all data measured for a given
     * sample, and each column is data of a given type measured for all samples.) The dashboard-generated row number is
     * added as the first data column.
     *
     * @param username
     *         username for validation
     * @param datasetId
     *         get data for this dataset
     * @param firstRow
     *         index of the first row of data to return
     * @param numRows
     *         number of rows of data to return
     * @param callback
     *         callback to make with rows of data for a dataset. The fail method is invoked if authentication fails, if
     *         dataset ID is invalid, if the dataset does not exist, or if there are problems obtaining the specified
     *         data for the dataset
     */
    void getDataWithRowNum(String username, String datasetId, int firstRow, int numRows,
            AsyncCallback<ArrayList<ArrayList<String>>> callback);

    /**
     * Saves the user's data column specifications for the given dataset
     * without running the automated data checker.  This allows the intermediate
     * saving of the entered dataset column specifications to prevent loss of work.
     *
     * @param username
     *         username for validation
     * @param newSpecs
     *         data column types to assign.  The dataset ID in this object specifies the dataset
     *         to update.  Any sample data in this object is ignored.
     * @param callback
     *         callback to make when complete. The fail method is invoked
     *         if authentication fails, if dataset ID is invalid,
     *         or if the dataset does not exist.
     */
    void saveDataColumnSpecs(String username, DashboardDataset newSpecs, AsyncCallback<Void> callback);

    /**
     * Updates the data column specifications for a dataset to those provided.  This triggers the automated data checker
     * to run using the new data column specifications.
     *
     * @param username
     *         username for validation
     * @param newSpecs
     *         data column types to assign.  The dataset ID in this object specifies the dataset to update.  Any sample
     *         data in this object is ignored.
     * @param callback
     *         callback to make with the the updated dataset with (abbreviated) data after processing through the
     *         automated data checker after processing through the automated data checker.  The fail method is invoked
     *         if authentication fails, if dataset ID is invalid, if the dataset does not exist, or if there are
     *         problems obtaining or evaluating the data for the dataset
     */
    void updateDataColumnSpecs(String username, DashboardDataset newSpecs,
            AsyncCallback<DashboardDatasetData> callback);

    /**
     * Updates the data column specifications for the datasets with the given IDs.  Column types are assigned from
     * column names-to-types saved for this user, and the automated data checker is run using these new column types.
     * Any exceptions thrown in the column assignment or sanity checking for a dataset only halt the process for that
     * dataset but otherwise is silently ignored.
     *
     * @param username
     *         username for validation
     * @param datasetIds
     *         process datasets with these IDs
     * @param callback
     *         callback to make after processing is complete. The fail method is invoked if authentication fails.
     */
    void updateDataColumns(String username, ArrayList<String> datasetIds, AsyncCallback<Void> callback);

    /**
     * Client side request to send the set of sanity checker data messages for a given dataset.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetId
     *         get data messages for this dataset
     * @param callback
     *         the callback to make with list of sanity checker data messages
     */
    void getDataMessages(String username, String datasetId, AsyncCallback<ADCMessageList> callback);

    /**
     * Client side request to generate the preview images for a dataset.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetId
     *         generate preview images for this dataset
     * @param timetag
     *         tag to be added to the end of the plot file names (before the filename extension) to make them specific
     *         to the time the request was made
     * @param firstCall
     *         is this the first request for the preview images? If true, the process to generate the images are
     *         started. If false, just checks if all the images have been created.
     * @param callback
     *         callback to make indicating the image-generating status (true if done generating plots)
     */
    void buildPreviewImages(String username, String datasetId, String timetag, boolean firstCall,
            AsyncCallback<Boolean> callback);

    /**
     * Client side request for the IDs of datasets which have associated SocatMetadata objects
     *
     * @param username
     *         name of the current user - for validation
     * @param callback
     *         callback to make with the list of dataset IDs
     */
    void getAllDatasetIdsForMetadata(String username, AsyncCallback<TreeSet<String>> callback);

    /**
     * Client side request to copy the SocatMetadata, with appropriate adjustments, from one dataset to another.
     *
     * @param username
     *         name of the current user - for validation
     * @param toId
     *         copy the SocatMetadata to the dataset with this ID
     * @param fromId
     *         copy the SocatMetadata from the dataset with this ID
     * @param callback
     *         callback to make on success or failure; the onFailure method of the callback will be called if
     *         authentication failed, if unable to get the SocatMetadata for the dataset associated with fromId, or
     *         if unable to write the SocatMetadata for the dataset associated with toId.
     */
    void copySocatMetadata(String username, String toId, String fromId, AsyncCallback<Void> callback);

    /**
     * Client side request to return the SocatMetadata for a dataset.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetId
     *         return the SocatMetadata for the dataset with this ID
     * @param callback
     *         callback to make on success or failure; the onFailure method of the callback will be called if
     *         authentication fails or if unable to read the SocatMetadata for the dataset.
     */
    void getSocatMetadata(String username, String datasetId, AsyncCallback<SocatMetadata> callback);

    /**
     * Client side request to save the given SocatMetadata for a dataset.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetId
     *         save the given SocatMetadata for the dataset with this ID
     * @param metadata
     *         SocatMetadata to save
     * @param callback
     *         callback to make on success or failure; the onFailure method of the callback will be called
     *         if authentication fails, if the SocatMetadata does not correspond to the indicated dataset, or
     *         if unable to save the SocatMetadata for the dataset.
     */
    void saveSocatMetadata(String username, String datasetId, SocatMetadata metadata, AsyncCallback<Void> callback);

    /**
     * Client-side interface for submitting datasets for QC.
     *
     * @param username
     *         name of user making this request - for validation
     * @param datasetIds
     *         IDs of datasets to submit
     * @param archiveStatus
     *         archive status to apply
     * @param localTimestamp
     *         client local timestamp string of this request
     * @param repeatSend
     *         if the archive request is to send for immediate archival, should datasets already sent be sent again?
     * @param callback
     *         the callback to make when complete; the onFailure method of the callback will be called if authentication
     *         failed, if a dataset does not exist for any of the IDs, or if the submitting of a dataset or change in
     *         archive status failed.
     */
    void submitDatasetsForQC(String username, TreeSet<String> datasetIds, String archiveStatus,
            String localTimestamp, boolean repeatSend, AsyncCallback<Void> callback);

    /**
     * Client-side interface for suspending datasets from QC.
     *
     * @param username
     *         name of user making this request - for validation
     * @param datasetIds
     *         IDs of datasets to suspend
     * @param callback
     *         the callback to make when complete; the onFailure method of the callback will be called if authentication
     *         failed, if a dataset does not exist for any of the IDs, or if the suspending of a dataset failed.
     */
    void suspendDatasets(String username, TreeSet<String> datasetIds, AsyncCallback<Void> callback);

}
