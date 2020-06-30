package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gov.noaa.pmel.socatmetadata.shared.core.SocatMetadata;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Server side interface for most dashboard server functions.
 *
 * @author Karl Smith
 */
@RemoteServiceRelativePath("DashboardServices")
public interface DashboardServicesInterface extends RemoteService {

    /**
     * Logs out the current user.
     */
    void logoutUser();

    /**
     * Gets the current user's list of datasets.
     *
     * @return the list of datasets for the current user
     *
     * @throws IllegalArgumentException
     *         if problems getting the cruise list
     */
    DashboardDatasetList getDatasetList() throws IllegalArgumentException;

    /**
     * Deletes all files for the indicated datasets.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetIds
     *         datasets to be deleted
     * @param deleteMetadata
     *         also delete metadata and additional documents?
     *
     * @return the updated list of datasets for the current user
     *
     * @throws IllegalArgumentException
     *         if problems deleting a cruise
     */
    DashboardDatasetList deleteDatasets(String username, TreeSet<String> datasetIds, Boolean deleteMetadata)
            throws IllegalArgumentException;

    /**
     * Adds the indicated datasets to the current user's list of datasets.
     *
     * @param username
     *         name of the current user - for validation
     * @param wildDatasetId
     *         ID, possibly with wildcards * and ?, of dataets to add
     *
     * @return the updated list of datasets for the current user
     *
     * @throws IllegalArgumentException
     *         if the dataset to be added does not exist
     */
    DashboardDatasetList addDatasetsToList(String username, String wildDatasetId) throws IllegalArgumentException;

    /**
     * Removes the indicated datasets from the current user's list of datasets (but does not delete any files for these
     * datasets).
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetIds
     *         cruises to be removed
     *
     * @return the updated list of cruises for the user
     *
     * @throws IllegalArgumentException
     *         if problems removing a cruise
     */
    DashboardDatasetList removeDatasetsFromList(String username, TreeSet<String> datasetIds)
            throws IllegalArgumentException;

    /**
     * Changes the owner of the given dataset to the indicated new owner.
     *
     * @param username
     *         name of the current user - for validation and current ownership of dataset
     * @param datasetIds
     *         change the owner of the datasets with these IDs
     * @param newOwner
     *         dashboard username of the new owner of these datasets
     *
     * @return the updated list of datasets for the current user
     *
     * @throws IllegalArgumentException
     *         if problems changing the ownership of any of the datasets
     */
    DashboardDatasetList changeDatasetOwner(String username, TreeSet<String> datasetIds, String newOwner)
            throws IllegalArgumentException;

    /**
     * Returns the latest information for the indicated datasets.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetIdSet
     *         set of all datasets to include in the returned updated dataset information
     *
     * @return updated cruise information for the indicated cruises
     *
     * @throws IllegalArgumentException
     *         if a cruise does not exist
     */
    DashboardDatasetList getUpdatedDatasets(String username, TreeSet<String> datasetIdSet)
            throws IllegalArgumentException;

    /**
     * Delete an ancillary document for a dataset.
     *
     * @param username
     *         name of the current user - for validation
     * @param deleteFilename
     *         remove the ancillary document with this name
     * @param datasetId
     *         remove the ancillary document from this dataset
     * @param allDatasetIds
     *         IDs of all datasets to include in the returned updated dataset information
     *
     * @return updated dataset information for the indicated datassets
     *
     * @throws IllegalArgumentException
     *         if the metadata document does not exist
     */
    DashboardDatasetList deleteAddlDoc(String username, String deleteFilename, String datasetId,
            TreeSet<String> allDatasetIds) throws IllegalArgumentException;

    /**
     * Reads the saved dataset file and returns the current data column specifications as well as data for some initial
     * samples to assist in identifying data columns.
     *
     * @param username
     *         username for validation
     * @param datasetId
     *         generate report for this dataset
     *
     * @return current data column specifications and initial (partial) sample data
     *
     * @throws IllegalArgumentException
     *         if authentication fails, if datasetId is invalid, if the dataset does not exist, or if there are problems
     *         obtaining the data for the dataset
     */
    TypesDatasetDataPair getDataColumnSpecs(String username, String datasetId) throws IllegalArgumentException;

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
     *
     * @return rows of data for a dataset.
     *
     * @throws IllegalArgumentException
     *         if authentication fails, if the dataset ID is invalid, if the dataset does not exist, or if there are
     *         problems obtaining the specified data
     */
    ArrayList<ArrayList<String>> getDataWithRowNum(String username, String datasetId, int firstRow, int numRows)
            throws IllegalArgumentException;

    /**
     * Saves the user's data column specifications for the given dataset without running the automated data checker.
     * This allows the intermediate saving of the entered dataset column specifications to prevent loss of work.
     *
     * @param username
     *         username for validation
     * @param newSpecs
     *         data column types to assign.  The dataset ID in this object specifies the dataset to update.  Any sample
     *         data in this object is ignored.
     */
    void saveDataColumnSpecs(String username, DashboardDataset newSpecs) throws IllegalArgumentException;

    /**
     * Updates the data column specifications for a dataset to those provided.  This triggers the automated data checker
     * to run using the new data column specifications.
     *
     * @param username
     *         username for validation
     * @param newSpecs
     *         data column types to assign.  The dataset ID in this object specifies the dataset to update. Any sample
     *         data in this object is ignored.
     *
     * @return the updated dataset with (abbreviated) data after processing through the automated data checker
     *
     * @throws IllegalArgumentException
     *         if authentication fails, if the dataset ID is invalid, if the dataset does not exist, or if there are
     *         problems obtaining or evaluating the data for the dataset
     */
    DashboardDatasetData updateDataColumnSpecs(String username, DashboardDataset newSpecs)
            throws IllegalArgumentException;

    /**
     * Updates the data column specifications for the datasets with the given IDs.  Column types are assigned from
     * column names-to-types saved for this user, and the automated data checker is run using these new column types.
     * Any exceptions thrown in the column assignment or sanity checking for a cruise only halt the process for that
     * dataset but otherwise is silently ignored.
     *
     * @param username
     *         username for validation
     * @param datasetIds
     *         process datasets with these IDs
     *
     * @throws IllegalArgumentException
     *         if authentication fails
     */
    void updateDataColumns(String username, ArrayList<String> datasetIds) throws IllegalArgumentException;

    /**
     * Returns the set of sanity checker data messages for a given dataset.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetId
     *         get data messages for this dataset
     *
     * @return list of data messages for the dataset; never null, but may be empty if the automated data checker did not
     *         generate any data messages.
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, or if the automated data checker has never been run on this cruise.
     */
    ADCMessageList getDataMessages(String username, String datasetId) throws IllegalArgumentException;

    /**
     * Requests that the preview images for a dataset be generated.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetId
     *         create preview images for this dataset
     * @param timetag
     *         tag to be added to the end of the plot file names (before the filename extension) to make them specific
     *         to the time the request was made
     * @param firstCall
     *         is this the first request for the preview images? If true, the process to generate the images are
     *         started. If false, just checks if all the images have been created.
     *
     * @return true if all the images have been created
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, or
     *         if the images cannot be created (probably because of bad data)
     */
    boolean buildPreviewImages(String username, String datasetId, String timetag, boolean firstCall)
            throws IllegalArgumentException;

    /**
     * Requests the IDs of datasets which have associated SocatMetadata objects accessible by the current user
     *
     * @param username
     *         name of the current user - used for validation as well as limiting the list of dataset IDs
     *
     * @return set of dataset IDs
     *
     * @throws IllegalArgumentException
     *         if authentication failed
     */
    TreeSet<String> getAllDatasetIdsForMetadata(String username) throws IllegalArgumentException;

    /**
     * Copy the SocatMetadata, with appropriate adjustments, from one dataset to another.
     *
     * @param username
     *         name of the current user - for validation
     * @param toId
     *         copy the SocatMetadata to the dataset with this ID
     * @param fromId
     *         copy the SocatMetadata from the dataset with this ID
     *
     * @throws IllegalArgumentException
     *         if authentication failed, or
     *         if there was a problem copying the SocatMetadata
     */
    void copySocatMetadata(String username, String toId, String fromId) throws IllegalArgumentException;

    /**
     * Return the SocatMetadata for a dataset.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetId
     *         return the SocatMetadata for the dataset with this ID
     *
     * @return the SocatMetadata for the dataset
     *
     * @throws IllegalArgumentException
     *         if authentication fails, or
     *         if unable to read the SocatMetadata for the dataset.
     */
    SocatMetadata getSocatMetadata(String username, String datasetId) throws IllegalArgumentException;

    /**
     * Save the given SocatMetadata for a dataset.
     *
     * @param username
     *         name of the current user - for validation
     * @param datasetId
     *         save the given SocatMetadata for the dataset with this ID
     * @param metadata
     *         SocatMetadata to save
     *
     * @throws IllegalArgumentException
     *         if authentication fails,
     *         if the SocatMetadata does not correspond to the indicated dataset, or
     *         if unable to save the SocatMetadata for the dataset.
     */
    void saveSocatMetadata(String username, String datasetId, SocatMetadata metadata) throws IllegalArgumentException;

    /**
     * Submits datasets named in the given listing for QC.
     *
     * @param username
     *         name of user making this request - for validation
     * @param datasetIds
     *         IDs of the datasets to submit
     * @param archiveStatus
     *         archive status to apply
     * @param localTimestamp
     *         client local timestamp string of this request
     * @param repeatSend
     *         if the archive request is to send for immediate archival, should datasets already sent be sent again?
     *
     * @throws IllegalArgumentException
     *         if authentication failed,
     *         if the dataset does not exist for any of the given IDs, or
     *         if submitting a dataset fails
     */
    void submitDatasetsForQC(String username, TreeSet<String> datasetIds, String archiveStatus,
            String localTimestamp, boolean repeatSend) throws IllegalArgumentException;

    /**
     * Suspends datasets from QC.
     *
     * @param username
     *         name of user making this request - for validation
     * @param datasetIds
     *         IDs of the datasets to suspend
     *
     * @throws IllegalArgumentException
     *         if authentication failed,
     *         if the dataset does not exist for any of the given IDs, or
     *         if suspending a dataset fails
     */
    void suspendDatasets(String username, TreeSet<String> datasetIds) throws IllegalArgumentException;

}
