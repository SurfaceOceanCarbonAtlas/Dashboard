/**
 * 
 */
package gov.noaa.pmel.dashboard.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Handles storage and retrieval of user data in files.
 * 
 * @author Karl Smith
 */
public class UserFileHandler extends VersionedFileHandler {

	private static final String USER_CRUISE_LIST_NAME_EXTENSION = 
			"_cruise_list.txt";
	private static final String USER_DATA_COLUMNS_NAME_EXTENSION =
			"_data_columns.properties";

	private KnownDataTypes userTypes;
	private HashMap<String,DataColumnType> defaultColNamesToTypes;

	/**
	 * Handles storage and retrieval of user data in files under 
	 * the given user files directory.
	 * 
	 * @param userFilesDirName
	 * 		name of the user files directory
	 * @param svnUsername
	 * 		username for SVN authentication
	 * @param svnPassword
	 * 		password for SVN authentication
	 * @param colNamesToTypesFilename
	 * 		name of properties file giving the default mapping 
	 * 		of column name keys to column types with units
	 * @param userTypes
	 * 		known user-provided data column types 
	 * @throws IllegalArgumentException
	 * 		if the specified directory does not exist, is not a 
	 * 		directory, or is not under SVN version control; 
	 * 		if the default column name to type properties
	 * 		file does not exist or is invalid.
	 */
	public UserFileHandler(String userFilesDirName, String svnUsername, 
			String svnPassword, String colNamesToTypesFilename, 
			KnownDataTypes userTypes) throws IllegalArgumentException {
		super(userFilesDirName, svnUsername, svnPassword);
		this.userTypes = userTypes;
		// Generate the default data column name to type map
		defaultColNamesToTypes = new HashMap<String,DataColumnType>();
		addDataColumnNames(defaultColNamesToTypes, new File(colNamesToTypesFilename));
	}

	/**
	 * Reads a properties file mapping data column names to data column
	 * types, units, and missing values, and adds these mappings to the 
	 * provided maps.
	 * 
	 * @param dataColNamesToTypes
	 * 		add the mappings of column name keys to types to this map
	 * @param propFile
	 * 		properties file where each key is the data column name key, 
	 * 		and each value is the varName of a DataColumnType, a comma, 
	 * 		a unit string, a comma, and a missing value string
	 * @throws IllegalArgumentException
	 * 		if the properties file does not exist or is invalid
	 */
	private void addDataColumnNames(HashMap<String,DataColumnType> dataColNamesToTypes, 
			File propFile) throws IllegalArgumentException {
		// Read the column name to type properties file
		Properties colProps = new Properties();
		try {
			FileReader propsReader = new FileReader(propFile);
			try {
				colProps.load(propsReader);
			} finally {
				propsReader.close();
			}
		} catch (IOException ex) {
			throw new IllegalArgumentException(ex);
		}
		// Convert the properties to a map of data column name to type
		// and add to the given map
		for ( Entry<Object,Object> prop : colProps.entrySet() ) {
			String colName = (String) prop.getKey();
			String propVal = (String) prop.getValue();
			String[] vals = propVal.split(",",-1);
			if ( vals.length != 3 ) 
				throw new IllegalArgumentException("invalid type,unit,missing value \"" + 
						propVal + "\" for key \"" + colName + "\" given in " +
						propFile.getPath());

			DashDataType<?> dtype = userTypes.getDataType(vals[0]);
			if ( dtype == null )
				throw new IllegalArgumentException("Unknown data type \"" + 
						vals[0] + "\" for tag \"" + colName + "\"");
			DataColumnType dctype = dtype.duplicate();
			if ( ! dctype.setSelectedUnit(vals[1]) )
				throw new IllegalArgumentException("Unknown data unit \"" + vals[1] + 
						"\" for data type \"" + vals[0] + "\"");
			dctype.setSelectedMissingValue(vals[2]);
			dataColNamesToTypes.put(colName, dctype);
		}
	}

	/**
	 * Gets the list of datasets for a user
	 * 
	 * @param username
	 * 		get cruises for this user
	 * @return
	 * 		the list of datasets for the user; will not be null, 
	 * 		but may be empty (including if there is no saved listing)
	 * @throws IllegalArgumentException
	 * 		if username is invalid, 
	 * 		if there was a problem reading an existing dataset listing, or 
	 * 		if there was an error committing the updated dataset listing to version control
	 */
	public DashboardDatasetList getDatasetListing(String username) 
										throws IllegalArgumentException {
		// Get the name of the dataset list file for this user
		String cleanUsername = DashboardUtils.cleanUsername(username);
		if ( cleanUsername.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		File userDataFile = new File(filesDir, cleanUsername + USER_CRUISE_LIST_NAME_EXTENSION);
		boolean needsCommit = false;
		String commitMessage = "";
		// Read the cruise expocodes from the cruise list file
		HashSet<String> dataIdsSet = new HashSet<String>();
		try {
			BufferedReader idsReader = new BufferedReader(new FileReader(userDataFile));
			try {
				String datasetId = idsReader.readLine();
				while ( datasetId != null ) {
					dataIdsSet.add(datasetId);
					datasetId = idsReader.readLine();
				}
			} finally {
				idsReader.close();
			}
		} catch ( FileNotFoundException ex ) {
			// Return a valid cruise listing with no cruises
			needsCommit = true;
			commitMessage = "add new dataset listing for " + cleanUsername + "; ";
		} catch ( Exception ex ) {
			// Problems with the listing in the existing file
			throw new IllegalArgumentException("Problems reading the dataset listing for " + 
					cleanUsername + ": " + ex.getMessage());
		}
		// Get the cruise file handler
		DashboardConfigStore configStore;
		try {
			configStore = DashboardConfigStore.get(false);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException("Unexpected failure to get dashboard settings");
		}
		DataFileHandler dataHandler = configStore.getDataFileHandler();
		// Create the dataset list (map) for these cruises
		DashboardDatasetList datasetList = new DashboardDatasetList();
		datasetList.setUsername(cleanUsername);
		for ( String datasetId : dataIdsSet ) {
			// Create the DashboardDataset from the info file
			DashboardDataset dataset = dataHandler.getDatasetFromInfoFile(datasetId);
			if ( dataset == null ) {
				// Dataset no longer exists - remove this ID from the saved list
				needsCommit = true;
				commitMessage += "remove non-existant dataset " + datasetId + "; ";
			}
			else {
				String owner = dataset.getOwner();
				if ( ! configStore.userManagesOver(cleanUsername, owner) ) {
					// No longer authorized to view - remove this ID from the saved list
					needsCommit = true;
					commitMessage += "remove unauthorized dataset " + datasetId + "; ";
				}
				else {
					datasetList.put(datasetId, dataset);
				}
			}
		}
		if ( needsCommit )
			saveDatasetListing(datasetList, commitMessage);
		// Determine whether or not this user is a manager/admin 
		datasetList.setManager(configStore.isManager(cleanUsername));
		// Return the listing of cruises
		return datasetList;
	}

	/**
	 * Saves the list of datasets for a user
	 * 
	 * @param datasetList
	 * 		listing of datasets to be saved
	 * @param message
	 * 		version control commit message; 
	 * 		if null or blank, the commit will not be performed 
	 * @throws IllegalArgumentException
	 * 		if the dataset listing was invalid, 
	 * 		if there was a problem saving the cruise listing, or 
	 * 		if there was an error committing the updated file to version control
	 */
	public void saveDatasetListing(DashboardDatasetList datasetList, 
						String message) throws IllegalArgumentException {
		// Get the name of the dataset list file for this user
		String username = datasetList.getUsername();
		if ( (username == null) || username.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		File userDataFile = new File(filesDir, username + USER_CRUISE_LIST_NAME_EXTENSION);
		// Write the IDs of the datasets in the listing to the file
		try {
			PrintWriter idsWriter = new PrintWriter(userDataFile);
			try {
				for ( String datasetId : datasetList.keySet() )
					idsWriter.println(datasetId);
			} finally {
				idsWriter.close();
			}
		} catch ( Exception ex ) {
			throw new IllegalArgumentException("Problems saving the dataset listing for " + 
					username + ": " + ex.getMessage());
		}
		if ( (message == null) || message.trim().isEmpty() )
			return;
		// Commit the update to this list of dataset IDs
		try {
			commitVersion(userDataFile, message);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException("Problems committing the updated dataset listing for " + 
					username + ": " + ex.getMessage());
		}
	}

	/**
	 * Removes an entry from a user's list of datasets, and
	 * saves the resulting list of datasets.
	 * 
	 * @param idsSet
	 * 		IDs of datasets to remove from the list
	 * @param username
	 * 		user whose dataset list is to be updated
	 * @return
	 * 		updated list of datasets for user
	 * @throws IllegalArgumentException
	 * 		if username is invalid, 
	 * 		if there was a problem saving the updated dataset listing, or
	 * 		if there was an error committing the updated dataset listing to version control
	 */
	public DashboardDatasetList removeDatasetsFromListing(TreeSet<String> idsSet, 
			String username) throws IllegalArgumentException {
		String cleanUsername = DashboardUtils.cleanUsername(username);
		if ( cleanUsername.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		DashboardDatasetList datasetList = getDatasetListing(cleanUsername);
		boolean changeMade = false;
		String commitMessage = "datasets removed from the listing for " + cleanUsername + ": ";
		for ( String datasetId : idsSet ) {
			if ( datasetList.containsKey(datasetId) ) {
				datasetList.remove(datasetId);
				changeMade = true;
				commitMessage += datasetId + "; ";
			}
		}
		if ( changeMade ) {
			// Save the updated cruise listing
			saveDatasetListing(datasetList, commitMessage);
		}
		return datasetList;
	}

	/**
	 * Adds matching entries to a user's list of datasets, and saves the 
	 * resulting list of datasets.  Only datasets that are owned by the 
	 * user, or owned by someone the user manages, are added. 
	 * 
	 * @param wildDatasetId
	 * 		dataset ID, possibly with wildcards * and ?, to add
	 * @param username
	 * 		user whose dataset list is to be updated
	 * @return
	 * 		updated list of datasets for the user
	 * @throws IllegalArgumentException
	 * 		if username is invalid, 
	 * 		if the dataset ID is invalid,
	 * 		if the dataset information file does not exist, 
	 * 		if there was a problem saving the updated dateset listing, or 
	 * 		if there was an error committing the updated dataset listing to version control
	 */
	public DashboardDatasetList addDatasetsToListing(String wildDatasetId, 
						String username) throws IllegalArgumentException {
		String cleanUsername = DashboardUtils.cleanUsername(username);
		if ( cleanUsername.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		DashboardConfigStore configStore;
		try {
			configStore = DashboardConfigStore.get(false);
		} catch ( IOException ex ) {
			throw new IllegalArgumentException("Unexpected failure to get the default dashboard settings");
		}
		DataFileHandler dataHandler = configStore.getDataFileHandler();
		HashSet<String> matchingIds = dataHandler.getMatchingDatasetIds(wildDatasetId);
		if ( matchingIds.size() == 0 ) 
			throw new IllegalArgumentException("No datasets with an ID matching " + wildDatasetId);
		DashboardDatasetList datasetList = getDatasetListing(cleanUsername);
		String commitMsg = "Added dataset(s) ";
		boolean needsCommit = false;
		boolean viewableFound = false;
		for ( String datasetId : matchingIds ) {
			// Create a dataset entry for this data
			DashboardDataset dataset = dataHandler.getDatasetFromInfoFile(datasetId);
			if ( dataset == null ) 
				throw new IllegalArgumentException("Unexpected error: dataset " +
						datasetId + " does not exist");
			if ( configStore.userManagesOver(cleanUsername, dataset.getOwner()) ) {
				// Add or replace this dataset entry in the dataset list
				viewableFound = true;
				if ( datasetList.put(datasetId, dataset) == null ) {
					// Only the IDs are saved to the dataset listing file
					commitMsg += datasetId + ", ";
					needsCommit = true;
				}
			}
		}
		if ( ! viewableFound )
			throw new IllegalArgumentException("No datasets with an ID matching " + wildDatasetId + 
					" that can be viewed by " + cleanUsername);
		if ( needsCommit )
			saveDatasetListing(datasetList, commitMsg + " to the listing for " + cleanUsername);
		return datasetList;
	}

	/**
	 * Adds entries to a user's list of datasets, and saves the resulting 
	 * list of datasets.  This does not check dataset ownership.
	 * 
	 * @param idsSet
	 * 		list of IDs of datasets to add to the list
	 * @param username
	 * 		user whose dataset list is to be updated
	 * @return
	 * 		updated list of datasets for user
	 * @throws IllegalArgumentException
	 * 		if username is invalid, 
	 * 		if any of the expocodes are invalid,
	 * 		if the cruise information file does not exist, 
	 * 		if there was a problem saving the updated cruise listing, or 
	 * 		if there was an error committing the updated cruise listing to version control
	 */
	public DashboardDatasetList addDatasetsToListing(Collection<String> idsSet, 
							String username) throws IllegalArgumentException {
		String cleanUsername = DashboardUtils.cleanUsername(username);
		if ( cleanUsername.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		DataFileHandler dataHandler;
		try {
			dataHandler = DashboardConfigStore.get(false).getDataFileHandler();
		} catch ( IOException ex ) {
			throw new IllegalArgumentException("Unexpected failure to get the cruise file handler");
		}
		DashboardDatasetList datasetList = getDatasetListing(cleanUsername);
		String commitMsg = "Added dataset(s) ";
		boolean needsCommit = false;
		for ( String datasetId : idsSet) {
			// Create a dataset entry for this data
			DashboardDataset dataset = dataHandler.getDatasetFromInfoFile(datasetId);
			if ( dataset == null ) 
				throw new IllegalArgumentException("dataset " + datasetId + " does not exist");
			// Add or replace this dataset entry in the dataset list
			if ( datasetList.put(datasetId, dataset) == null ) {
				// Only the IDs are saved to the dataset listing file
				commitMsg += datasetId + ", ";
				needsCommit = true;
			}
		}
		if ( needsCommit ) {
			commitMsg += " to the listing for " + cleanUsername;
			saveDatasetListing(datasetList, commitMsg);
		}
		return datasetList;
	}

	/**
	 * Assigns the data column types, units, and missing values for a dataset 
	 * from the user-provided data column names using the mappings of column 
	 * names to types, units, and missing values associated with the dataset 
	 * owner. 
	 *  
	 * @param dataset
	 * 		dataset whose data column types, units, and missing values 
	 * 		are to be assigned
	 * @throws IllegalArgumentException
	 * 		if the data column names to types, units, and missing values
	 * 		properties file for the dataset owner, if the file exists, 
	 * 		is invalid.
	 */
	public void assignDataColumnTypes(DashboardDataset dataset) throws IllegalArgumentException {
		// Copy the default maps of data column names to types and units
		HashMap<String,DataColumnType> userColNamesToTypes = 
				new HashMap<String,DataColumnType>(defaultColNamesToTypes);
		// Add the user-customized map of column names to types
		File propsFile = new File(filesDir, dataset.getOwner() + USER_DATA_COLUMNS_NAME_EXTENSION);
		if ( propsFile.exists() ) 
			addDataColumnNames(userColNamesToTypes, propsFile);
		ArrayList<String> userColNames = dataset.getUserColNames();
		ArrayList<DataColumnType> colTypes = new ArrayList<DataColumnType>(userColNames.size());
		// Go through the column names to assign these lists
		for ( String colName : userColNames ) {
			String key = DashboardServerUtils.getKeyForName(colName);
			DataColumnType thisColType = userColNamesToTypes.get(key);
			if ( thisColType == null )
				thisColType = DashboardUtils.UNKNOWN;
			colTypes.add(thisColType);
		}
		dataset.setDataColTypes(colTypes);
	}

	/**
	 * Updates and saves the data column names to types for a user from 
	 * the currently assigned column names, types, units, and missing values 
	 * given in a dataset.
	 * 
	 * @param dataset
	 * 		update the data column names to types from this dataset
	 * @param username
	 * 		update data column names to types for this user
	 * @throws IllegalArgumentException
	 * 		if the data column names to types file is invalid (if it exists), or 
	 * 		if unable to save or commit the updated version of this file 
	 */
	public void updateUserDataColumnTypes(DashboardDataset dataset, String username) 
											throws IllegalArgumentException {
		// Copy the default maps of data column names to types and units
		HashMap<String,DataColumnType> userColNamesToTypes = 
				new HashMap<String,DataColumnType>(defaultColNamesToTypes);
		// Add the user-customized map of column names to types
		File propsFile = new File(filesDir, username + USER_DATA_COLUMNS_NAME_EXTENSION);
		if ( propsFile.exists() ) 
			addDataColumnNames(userColNamesToTypes, propsFile);
		// Add mappings of data columns names to types, units, and missing values from this dataset
		ArrayList<DataColumnType> colTypes = dataset.getDataColTypes();
		boolean changed = false;
		int k = 0;
		for ( String colName : dataset.getUserColNames() ) {
			String key = DashboardServerUtils.getKeyForName(colName);
			DataColumnType thisColType = colTypes.get(k);
			DataColumnType oldType = userColNamesToTypes.put(key, thisColType);
			if ( ! thisColType.equals(oldType) )
				changed = true;
			k++;
		}

		// If nothing has changed, nothing to do
		if ( ! changed ) 
			return;

		// Remove the default name to type mappings (remove and put back if different)
		for ( Entry<String,DataColumnType> defEntry : defaultColNamesToTypes.entrySet() ) {
			DataColumnType thisColType = userColNamesToTypes.remove(defEntry.getKey());
			if ( ! defEntry.getValue().equals(thisColType) )
				userColNamesToTypes.put(defEntry.getKey(), thisColType);
		}
		// Create the Properties object for these mappings. 
		Properties colProps = new Properties();
		// Note that the keys for the maps could no longer be identical. 
		HashSet<String> allKeys = new HashSet<String>(userColNamesToTypes.keySet());
		for ( String key : allKeys ) {
			DataColumnType thisColType = userColNamesToTypes.get(key);
			colProps.setProperty(key, thisColType.getVarName() + "," + 
					thisColType.getUnits().get(thisColType.getSelectedUnitIndex()) + 
					"," + thisColType.getSelectedMissingValue());
		}
		// Save this Properties object to file
		try {
			FileWriter propsWriter = new FileWriter(propsFile);
			try {
				colProps.store(propsWriter, null);
			} finally {
				propsWriter.close();
			}
		} catch (IOException ex) {
			throw new IllegalArgumentException("Problems saving the data column names to types, units, " +
					"and missing values file for " + username + "\n" + ex.getMessage());
		}
		// Commit the update version of this file
		try {
			commitVersion(propsFile, "Updated data column names to types, units, and missing values for " + 
					username);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problems committing the data column names to types for " + 
					username + "\n" + ex.getMessage());
		}
	}

}
