/**
 * 
 */
package gov.noaa.pmel.dashboard.actions;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.handlers.UserFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;

/**
 * Methods for revising dataset information, such as dataset owner and dataset.
 * 
 * @author Karl Smith
 */
public class DatasetModifier {

	private static final SimpleDateFormat DATETIMESTAMPER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static {
		DATETIMESTAMPER.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	DashboardConfigStore configStore;
	String restoredSocatVersion;

	/**
	 * Modifies information about datasets. 
	 * 
	 * @param configStore
	 * 		configuration store to use
	 */
	public DatasetModifier(DashboardConfigStore configStore) {
		this.configStore = configStore;
		this.restoredSocatVersion = null;
	}

	/**
	 * Changes the owner of the data and metadata files for a dataset.
	 * The dataset is added to the list of datasets for the new owner.
	 * 
	 * @param datasetId
	 * 		change the owner of the data and metadata files for the dataset with this dataset 
	 * @param newOwner
	 * 		change the owner of the data and metadata files to this username
	 * @throws IllegalArgumentException
	 * 		if the dataset is invalid,
	 * 		if the new owner username is not recognized,
	 * 		if there is no data file for the indicated dataset
	 */
	public void changeDatasetOwner(String datasetId, String newOwner) 
									throws IllegalArgumentException {
		String stdId = DashboardServerUtils.checkDatasetID(datasetId);
		if ( ! configStore.validateUser(newOwner) )
			throw new IllegalArgumentException("Unknown dashboard user " + newOwner);

		DataFileHandler dataHandler = configStore.getDataFileHandler();
		DashboardDataset dataset = dataHandler.getDatasetFromInfoFile(stdId);
		String oldOwner = dataset.getOwner();
		dataset.setOwner(newOwner);
		dataHandler.saveDatasetInfoToFile(dataset, "Owner of " + stdId + 
				" data file changed from " + oldOwner + " to " + newOwner);

		MetadataFileHandler metaHandler = configStore.getMetadataFileHandler();
		ArrayList<DashboardMetadata> metaList = metaHandler.getMetadataFiles(stdId);
		for ( DashboardMetadata mdata : metaList ) {
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
	 * Appropriately renames dashboard dataset files.  If an exception is thrown,
	 * the system is likely have a corrupt mix of renamed and original-name files.
	 * 
	 * @param oldId
	 * 		current ID for the dataset
	 * @param newId
	 * 		new ID to use for the dataset
	 * @param username
	 * 		user requesting this rename
	 * @throws IllegalArgumentException
	 * 		if the username is not an admin,
	 * 		if either dataset ID is invalid,
	 * 		if files for the old dataset ID do not exist,
	 * 		if any files for the new dataset ID already exist
	 * @throws IOException
	 * 		if updating a file with the new ID throws one
	 * @throws SQLException 
	 * 		if username is not a known user, or
	 * 		if accessing or updating the database throws one
	 */
	public void renameDataset(String oldId, String newId, String username) 
						throws IllegalArgumentException, IOException, SQLException {
		// check and standardized the dataset IDs
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

}
