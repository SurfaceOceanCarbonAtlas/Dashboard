/**
 * 
 */
package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;

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

			DataColumnType dctype = userTypes.getDataColumnType(vals[0]);
			if ( dctype == null )
				throw new IllegalArgumentException("Unknown data type \"" + 
						vals[0] + "\" for tag \"" + colName + "\"");
			if ( ! dctype.setSelectedUnit(vals[1]) )
				throw new IllegalArgumentException("Unknown data unit \"" + vals[1] + 
						"\" for data type \"" + vals[0] + "\"");
			dctype.setSelectedMissingValue(vals[2]);
			dataColNamesToTypes.put(colName, dctype);
		}
	}

	/**
	 * Gets the list of cruises for a user
	 * 
	 * @param username
	 * 		get cruises for this user
	 * @return
	 * 		the list of cruises for the user; will not be null 
	 * 		but may be empty (including if there is no saved listing)
	 * @throws IllegalArgumentException
	 * 		if username was invalid, if there was a problem
	 * 		reading an existing cruise listing, or 
	 * 		if there was an error committing the updated 
	 * 		cruise listing to version control
	 */
	public DashboardDatasetList getCruiseListing(String username) 
										throws IllegalArgumentException {
		// Get the name of the cruise list file for this user
		String cleanUsername = DashboardUtils.cleanUsername(username);
		if ( cleanUsername.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		File userDataFile = new File(filesDir, 
				cleanUsername + USER_CRUISE_LIST_NAME_EXTENSION);
		boolean needsCommit = false;
		String commitMessage = "";
		// Read the cruise expocodes from the cruise list file
		HashSet<String> expocodeSet = new HashSet<String>();
		try {
			BufferedReader expoReader = new BufferedReader(
										new FileReader(userDataFile));
			try {
				String expocode = expoReader.readLine();
				while ( expocode != null ) {
					expocodeSet.add(expocode);
					expocode = expoReader.readLine();
				}
			} finally {
				expoReader.close();
			}
		} catch ( FileNotFoundException ex ) {
			// Return a valid cruise listing with no cruises
			needsCommit = true;
			commitMessage = "add new cruise listing for " + cleanUsername + "; ";
		} catch ( Exception ex ) {
			// Problems with the listing in the existing file
			throw new IllegalArgumentException(
					"Problems reading the cruise listing from " + 
					userDataFile.getPath() + ": " + ex.getMessage());
		}
		// Get the cruise file handler
		DashboardConfigStore configStore;
		try {
			configStore = DashboardConfigStore.get(false);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException("Unexpected failure to get settings");
		}
		DataFileHandler cruiseHandler = configStore.getDataFileHandler();
		// Create the cruise list (map) for these cruises
		DashboardDatasetList cruiseList = new DashboardDatasetList();
		cruiseList.setUsername(cleanUsername);
		cruiseList.setVersion(configStore.getUploadVersion());
		for ( String expocode : expocodeSet ) {
			// Create the DashboardDataset from the info file
			DashboardDataset cruise = cruiseHandler.getDatasetFromInfoFile(expocode);
			if ( cruise == null ) {
				// Cruise no longer exists - remove this dataset from the saved list
				needsCommit = true;
				commitMessage += "remove non-existant dataset " + expocode + "; ";
			}
			else {
				String owner = cruise.getOwner();
				if ( ! configStore.userManagesOver(cleanUsername, owner) ) {
					// No longer authorized to view - remove this dataset from the saved list
					needsCommit = true;
					commitMessage += "remove unauthorized dataset " + expocode + "; ";
				}
				else {
					cruiseList.put(expocode, cruise);
				}
			}
		}
		if ( needsCommit )
			saveCruiseListing(cruiseList, commitMessage);
		// Determine whether or not this user is a manager/admin 
		cruiseList.setManager(configStore.isManager(cleanUsername));
		// Return the listing of cruises
		return cruiseList;
	}

	/**
	 * Saves the list of cruises for a user
	 * 
	 * @param cruiseList
	 * 		listing of cruises to be saved
	 * @param message
	 * 		version control commit message; 
	 * 		if null or blank, the commit will not be performed 
	 * @throws IllegalArgumentException
	 * 		if the cruise listing was invalid, if there was 
	 * 		a problem saving the cruise listing, or if there 
	 * 		was an error committing the updated file to 
	 * 		version control
	 */
	public void saveCruiseListing(DashboardDatasetList cruiseList, 
						String message) throws IllegalArgumentException {
		String username = cruiseList.getUsername();
		// Get the name of the cruise list file for this user
		if ( (username == null) || username.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		File userDataFile = new File(filesDir, 
				username + USER_CRUISE_LIST_NAME_EXTENSION);
		// Write the expocodes of the cruises in the listing to the file
		try {
			PrintWriter expoWriter = new PrintWriter(userDataFile);
			try {
				for ( String expocode : cruiseList.keySet() )
					expoWriter.println(expocode);
			} finally {
				expoWriter.close();
			}
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems saving the cruise listing " + 
					userDataFile.getPath() + ": " + ex.getMessage());
		}
		if ( (message == null) || message.trim().isEmpty() )
			return;
		// Commit the update to this list of cruise expocodes
		try {
			commitVersion(userDataFile, message);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems committing the updated cruise listing: " + 
					ex.getMessage());
		}
	}

	/**
	 * Removes an entry from a user's list of cruises, and
	 * saves the resulting list of cruises.
	 * 
	 * @param expocodeSet
	 * 		expocodes of cruises to remove from the list
	 * @param username
	 * 		user whose cruise list is to be updated
	 * @return
	 * 		updated list of cruises for user
	 * @throws IllegalArgumentException
	 * 		if username is invalid, if there was a  
	 * 		problem saving the updated cruise listing, or
	 * 		if there was an error committing the updated 
	 * 		cruise listing to version control
	 */
	public DashboardDatasetList removeCruisesFromListing(
							TreeSet<String> expocodeSet, String username) 
										throws IllegalArgumentException {
		String cleanUsername = DashboardUtils.cleanUsername(username);
		if ( cleanUsername.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		DashboardDatasetList cruiseList = getCruiseListing(cleanUsername);
		boolean changeMade = false;
		String commitMessage = 
				"cruises removed from the listing for " + cleanUsername + ": ";
		for ( String expocode : expocodeSet ) {
			if ( cruiseList.containsKey(expocode) ) {
				cruiseList.remove(expocode);
				changeMade = true;
				commitMessage += expocode + "; ";
			}
		}
		if ( changeMade ) {
			// Save the updated cruise listing
			saveCruiseListing(cruiseList, commitMessage);
		}
		return cruiseList;
	}

	/**
	 * Adds matching entries to a user's list of cruises, and saves the resulting 
	 * list of cruises.  Only cruises that are modifiable by the use (owned by
	 * the user or owned by someone the user manages) are added. 
	 * 
	 * @param wildExpocode
	 * 		dataset, possibly with wildcards * and ?, to add
	 * @param username
	 * 		user whose cruise list is to be updated
	 * @return
	 * 		updated list of cruises for the user
	 * @throws IllegalArgumentException
	 * 		if username is invalid, 
	 * 		if wildExpocode is invalid,
	 * 		if the cruise information file does not exist, 
	 * 		if there was a problem saving the updated cruise listing, or 
	 * 		if there was an error committing the updated cruise listing 
	 * 			to version control
	 */
	public DashboardDatasetList addCruisesToListing(String wildExpocode, 
						String username) throws IllegalArgumentException {
		String cleanUsername = DashboardUtils.cleanUsername(username);
		if ( cleanUsername.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		DashboardConfigStore configStore;
		try {
			configStore = DashboardConfigStore.get(false);
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the default dashboard config store");
		}
		DataFileHandler cruiseHandler = configStore.getDataFileHandler();
		HashSet<String> matchExpocodes = cruiseHandler.getMatchingDatasetIds(wildExpocode);
		if ( matchExpocodes.size() == 0 ) 
			throw new IllegalArgumentException(
					"No datasets with an dataset matching " + wildExpocode);
		DashboardDatasetList cruiseList = getCruiseListing(cleanUsername);
		String commitMsg = "Added cruise(s) ";
		boolean needsCommit = false;
		boolean viewableFound = false;
		for ( String expocode : matchExpocodes ) {
			// Create a cruise entry for this data
			DashboardDataset cruise = cruiseHandler.getDatasetFromInfoFile(expocode);
			if ( cruise == null ) 
				throw new IllegalArgumentException("Unexpected error: dataset " +
						expocode + " does not exist");
			if ( configStore.userManagesOver(cleanUsername, cruise.getOwner()) ) {
				// Add or replace this cruise entry in the cruise list
				// Only the expocodes (keys) are saved in the cruise list
				viewableFound = true;
				if ( cruiseList.put(expocode, cruise) == null ) {
					commitMsg += expocode + ", ";
					needsCommit = true;
				}
			}
		}
		if ( ! viewableFound )
			throw new IllegalArgumentException(
					"No datasets with an dataset matching " + wildExpocode + 
					" that can be viewed by " + cleanUsername);
		if ( needsCommit )
			saveCruiseListing(cruiseList, commitMsg + " to the listing for " + cleanUsername);
		return cruiseList;
	}

	/**
	 * Adds an entries to a user's list of cruises, and saves the resulting 
	 * list of cruises.
	 * 
	 * @param expocodeList
	 * 		list of cruise expocodes to add to the list
	 * @param username
	 * 		user whose cruise list is to be updated
	 * @return
	 * 		updated list of cruises for user
	 * @throws IllegalArgumentException
	 * 		if username is invalid, 
	 * 		if any of the expocodes are invalid,
	 * 		if the cruise information file does not exist, 
	 * 		if there was a problem saving the updated cruise listing, or 
	 * 		if there was an error committing the updated cruise listing 
	 * 			to version control
	 */
	public DashboardDatasetList addCruisesToListing(ArrayList<String> expocodeList, 
							String username) throws IllegalArgumentException {
		String cleanUsername = DashboardUtils.cleanUsername(username);
		if ( cleanUsername.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		DataFileHandler cruiseHandler;
		try {
			cruiseHandler = DashboardConfigStore.get(false).getDataFileHandler();
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the cruise file handler");
		}
		DashboardDatasetList cruiseList = getCruiseListing(cleanUsername);
		String commitMsg = "Added cruises ";
		// Create a cruise entry for this data
		for ( String expocode : expocodeList) {
			DashboardDataset cruise = cruiseHandler.getDatasetFromInfoFile(expocode);
			if ( cruise == null ) 
				throw new IllegalArgumentException(
						"cruise " + expocode + " does not exist");
			// Add or replace this cruise entry in the cruise list
			// Only the expocodes (keys) are saved in the cruise list
			cruiseList.put(expocode, cruise);
			commitMsg += expocode + ", ";
		}
		commitMsg += " to the listing for " + cleanUsername;
		saveCruiseListing(cruiseList, commitMsg);
		return cruiseList;
	}

	/**
	 * Assigns the data column types, units, and missing values for a cruise 
	 * from the user-provided data column names using the mappings of column 
	 * names to types, units, and missing values associated with the cruise 
	 * owner. 
	 *  
	 * @param cruise
	 * 		cruise whose data column types, units, and missing values are to 
	 * 		be assigned
	 * @throws IllegalArgumentException
	 * 		if the data column names to types, units, and missing values
	 * 		properties file for the cruise owner, if it exists, is invalid.
	 */
	public void assignDataColumnTypes(DashboardDataset cruise) 
											throws IllegalArgumentException {
		// Copy the default maps of data column names to types and units
		HashMap<String,DataColumnType> userColNamesToTypes = 
				new HashMap<String,DataColumnType>(defaultColNamesToTypes);
		// Add the user-customized map of column names to types
		File propsFile = new File(filesDir, 
				cruise.getOwner() + USER_DATA_COLUMNS_NAME_EXTENSION);
		if ( propsFile.exists() ) 
			addDataColumnNames(userColNamesToTypes, propsFile);
		// Directly assign the lists contained in the cruise
		ArrayList<DataColumnType> colTypes = cruise.getDataColTypes();
		colTypes.clear();
		// Go through the column names to assign these lists
		for ( String colName : cruise.getUserColNames() ) {
			// Convert the column name to the key
			String key = DashboardServerUtils.getKeyForName(colName);
			DataColumnType thisColType = userColNamesToTypes.get(key);
			if ( thisColType == null )
				thisColType = DashboardUtils.UNKNOWN;
			colTypes.add(thisColType);
		}
	}

	/**
	 * Updates and saves the data column names to types for a user from 
	 * the currently assigned column names, types, units, and missing values 
	 * given in a cruise.
	 * 
	 * @param cruise
	 * 		update the data column names to types from this cruise
	 * @param username
	 * 		update data column names to types for this user
	 * @throws IllegalArgumentException
	 * 		if the data column names to types file is invalid (if it exists), or 
	 * 		if unable to save or commit the updated version of this file 
	 */
	public void updateUserDataColumnTypes(DashboardDataset cruise, String username) 
											throws IllegalArgumentException {
		// Copy the default maps of data column names to types and units
		HashMap<String,DataColumnType> userColNamesToTypes = 
				new HashMap<String,DataColumnType>(defaultColNamesToTypes);
		// Add the user-customized map of column names to types
		File propsFile = new File(filesDir, username + USER_DATA_COLUMNS_NAME_EXTENSION);
		if ( propsFile.exists() ) 
			addDataColumnNames(userColNamesToTypes, propsFile);
		// Add mappings of data columns names to types, units, 
		// and missing values from this cruise
		ArrayList<DataColumnType> colTypes = cruise.getDataColTypes();
		boolean changed = false;
		int k = 0;
		for ( String colName : cruise.getUserColNames() ) {
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
			throw new IllegalArgumentException(
					"Problems saving the data column names to types, units, " +
					"and missing values file for " + username + "\n" + 
					ex.getMessage());
		}
		// Commit the update version of this file
		try {
			commitVersion(propsFile, 
					"Data column names to types, units, and missing values " +
					"properties file for " + username);
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					"Problems committing the data column names to types, units, " +
					"and missing values file for " + username + "\n" + 
					ex.getMessage());
		}
	}

}
