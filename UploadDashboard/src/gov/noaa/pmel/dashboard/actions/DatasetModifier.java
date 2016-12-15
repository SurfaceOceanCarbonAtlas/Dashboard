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
	 * Modifies information about datasets or restores previous versions of data or WOCE flags for datasets.
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
	 * @param dataset
	 * 		change the owner of the data and metadata files for the dataset with this dataset 
	 * @param newOwner
	 * 		change the owner of the data and metadata files to this username
	 * @throws IllegalArgumentException
	 * 		if the dataset is invalid,
	 * 		if the new owner username is not recognized,
	 * 		if there is no data file for the indicated dataset
	 */
	public void changeDatasetOwner(String expocode, String newOwner) 
									throws IllegalArgumentException {
		String upperExpo = DashboardServerUtils.checkDatasetID(expocode);
		if ( ! configStore.validateUser(newOwner) )
			throw new IllegalArgumentException("Unknown dashboard user " + newOwner);

		DataFileHandler cruiseHandler = configStore.getDataFileHandler();
		DashboardDataset cruise = cruiseHandler.getDatasetFromInfoFile(upperExpo);
		String oldOwner = cruise.getOwner();
		cruise.setOwner(newOwner);
		cruiseHandler.saveDatasetInfoToFile(cruise, "Owner of " + upperExpo + 
				" data file changed from " + oldOwner + " to " + newOwner);

		MetadataFileHandler metaHandler = configStore.getMetadataFileHandler();
		ArrayList<DashboardMetadata> metaList = metaHandler.getMetadataFiles(upperExpo);
		for ( DashboardMetadata mdata : metaList ) {
			String oldMetaOwner = mdata.getOwner();
			mdata.setOwner(newOwner);
			metaHandler.saveMetadataInfo(mdata, "Owner of " + upperExpo + 
					" metadata file changed from " + oldMetaOwner + " to " + newOwner, false);
		}

		UserFileHandler userHandler = configStore.getUserFileHandler();
		String commitMsg = "Dataset " + upperExpo + " moved from " + oldOwner + " to " + newOwner;

		// Add this cruise to the list for the new owner
		DashboardDatasetList cruiseList = userHandler.getDatasetListing(newOwner);
		if ( cruiseList.put(upperExpo, cruise) == null ) {
			userHandler.saveDatasetListing(cruiseList, commitMsg);
		}

		// Rely on update-on-read to remove the cruise from the list of the old owner 
		// (and others) if they no longer should be able to see this cruise 
	}

	/**
	 * Appropriately renames dashboard cruise files, as well as SOCAT files and 
	 * database flags if the cruise has been submitted.  If an exception is thrown,
	 * the system is likely have a corrupt mix of renamed and original-name files.
	 * 
	 * @param oldExpocode
	 * 		current dataset for the cruise
	 * @param newExpocode
	 * 		new dataset to use for the cruise
	 * @param username
	 * 		username to associate with the rename QC and WOCE events
	 * @throws IllegalArgumentException
	 * 		if the username is not an admin,
	 * 		if either dataset is invalid,
	 * 		if cruise files for the old dataset do not exist,
	 * 		if any files for the new dataset already exist
	 * @throws IOException
	 * 		if updating a file with the new dataset throws one
	 * @throws SQLException 
	 * 		if username is not a known user, or
	 * 		if accessing or updating the database throws one
	 */
	public void renameDataset(String oldExpocode, String newExpocode, String username) 
						throws IllegalArgumentException, IOException, SQLException {
		// check and standardized the expocodes
		String oldExpo = DashboardServerUtils.checkDatasetID(oldExpocode);
		String newExpo = DashboardServerUtils.checkDatasetID(newExpocode);
		// rename the cruise data and info files; update the dataset in the data file
		configStore.getDataFileHandler().renameDatasetFiles(oldExpo, newExpo);
		// rename the SanityChecker messages file, if it exists
		configStore.getCheckerMsgHandler().renameMsgsFile(oldExpo, newExpo);
		// rename metadata files; update the dataset in the OME metadata
		configStore.getMetadataFileHandler().renameMetadataFiles(oldExpo, newExpo);
		// rename the DSG and decimated DSG files; update the dataset ID in these files
		configStore.getDsgNcFileHandler().renameDsgFiles(oldExpo, newExpo);
	}

}
