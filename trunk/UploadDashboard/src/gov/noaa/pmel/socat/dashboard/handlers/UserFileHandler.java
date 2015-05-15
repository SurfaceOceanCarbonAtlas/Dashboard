/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.handlers;

import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

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
	private static final String DEFAULT_DATA_COLUMNS_FILENAME = 
			"data_column_defaults.properties";
	private static final String USER_DATA_COLUMNS_NAME_EXTENSION =
			"_data_columns.properties";

	private HashMap<String,DataColumnType> defaultColNamesToTypes;
	private HashMap<String,String> defaultColNamesToUnits;
	private HashMap<String,String> defaultColNamesToMissVals;

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
	 * @throws IllegalArgumentException
	 * 		if the specified directory does not exist, is not a 
	 * 		directory, or is not under SVN version control; 
	 * 		also, if the default column name to type properties
	 * 		file does not exist or is invalid.
	 */
	public UserFileHandler(String userFilesDirName, String svnUsername,
					String svnPassword) throws IllegalArgumentException {
		super(userFilesDirName, svnUsername, svnPassword);
		// Generate the default data column name to type map
		defaultColNamesToTypes = new HashMap<String,DataColumnType>();
		defaultColNamesToUnits = new HashMap<String,String>();
		defaultColNamesToMissVals = new HashMap<String,String>();
		addDataColumnNames(
				new File(userFilesDirName, DEFAULT_DATA_COLUMNS_FILENAME), 
				defaultColNamesToTypes, 
				defaultColNamesToUnits, 
				defaultColNamesToMissVals);
	}

	/**
	 * Reads a properties file mapping data column names to data column
	 * types, units, and missing values, and adds these mappings to the 
	 * provided maps.
	 * 
	 * @param propFile
	 * 		properties file where each key is the name of a data column, 
	 * 		and each value is the name of a DataColumnType, a comma, a 
	 * 		unit string, a comma, and a missing value string
	 * @param dataColNamesToTypes
	 * 		add the mappings of column names to types to this map
	 * @param dataColNamesToUnits
	 * 		add the mappings of column names to units to this map
	 * @param dataColNamesToMissVals
	 * 		add the mappings of column names to missing values to this map
	 * @throws IllegalArgumentException
	 * 		if the properties file does not exist or is invalid
	 */
	private void addDataColumnNames(File propFile, 
			HashMap<String,DataColumnType> dataColNamesToTypes,
			HashMap<String,String> dataColNamesToUnits,
			HashMap<String,String> dataColNamesToMissVals) 
										throws IllegalArgumentException {
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
			dataColNamesToTypes.put(colName, DataColumnType.valueOf(vals[0]));
			dataColNamesToUnits.put(colName, vals[1]);
			dataColNamesToMissVals.put(colName, vals[2]);
		}
	}

	/**
	 * Returns the column name key from the given column name.
	 *   
	 * @param columnName
	 * 		get the key for this column name
	 * @return
	 * 		the key for the given column name
	 */
	private String keyFromColumnName(String columnName) {
		return columnName.toLowerCase().replaceAll("[^a-z0-9]", "");
	}

	/**
	 * Gets the list of cruises for a user
	 * 
	 * @param username
	 * 		get cruises for this user
	 * @return
	 * 		the list of cruises for the user; will not be null 
	 * 		but may have a null cruise list if there is no saved listing
	 * @throws IllegalArgumentException
	 * 		if username was invalid, if there was a problem
	 * 		reading an existing cruise listing, or 
	 * 		if there was an error committing the updated 
	 * 		cruise listing to version control
	 */
	public DashboardCruiseList getCruiseListing(String username) 
										throws IllegalArgumentException {
		// Get the name of the cruise list file for this user
		if ( (username == null) || username.trim().isEmpty() )
			throw new IllegalArgumentException("invalid username");
		File userDataFile = new File(filesDir, 
				username + USER_CRUISE_LIST_NAME_EXTENSION);
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
			commitMessage = "add new cruise listing for " + username + "; ";
		} catch ( Exception ex ) {
			// Problems with the listing in the existing file
			throw new IllegalArgumentException(
					"Problems reading the cruise listing from " + 
					userDataFile.getPath() + ": " + ex.getMessage());
		}
		// Get the cruise file handler
		DashboardConfigStore configStore;
		try {
			configStore = DashboardConfigStore.get();
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to get settings");
		}
		CruiseFileHandler cruiseHandler = configStore.getCruiseFileHandler();
		// Create the cruise list (map) for these cruises
		DashboardCruiseList cruiseList = new DashboardCruiseList();
		cruiseList.setUsername(username);
		cruiseList.setSocatVersion(configStore.getSocatUploadVersion());
		for ( String expocode : expocodeSet ) {
			// Create the DashboardCruise from the info file
			DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
			if ( cruise == null ) {
				// Remove this expocode from the saved list
				needsCommit = true;
				commitMessage += "remove cruise " + expocode + "; ";
			}
			else {
				cruiseList.put(expocode, cruise);
			}
		}
		if ( needsCommit )
			saveCruiseListing(cruiseList, commitMessage);
		// Determine whether or not this user is a manager/admin 
		cruiseList.setManager(configStore.isManager(username));
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
	public void saveCruiseListing(DashboardCruiseList cruiseList, 
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
	public DashboardCruiseList removeCruisesFromListing(
							TreeSet<String> expocodeSet, String username) 
										throws IllegalArgumentException {
		DashboardCruiseList cruiseList = getCruiseListing(username);
		boolean changeMade = false;
		String commitMessage = 
				"cruises removed from the listing for " + username + ": ";
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
	 * Adds an entries to a user's list of cruises, and saves the resulting 
	 * list of cruises.
	 * 
	 * @param wildExpocode
	 * 		expocode, possibly with wildcards * and ?, to use
	 * @param username
	 * 		user whose cruise list is to be updated
	 * @return
	 * 		updated list of cruises for user
	 * @throws IllegalArgumentException
	 * 		if username is invalid, 
	 * 		if wildExpocode is invalid,
	 * 		if the cruise information file does not exist, 
	 * 		if there was a problem saving the updated cruise listing, or 
	 * 		if there was an error committing the updated cruise listing 
	 * 			to version control
	 */
	public DashboardCruiseList addCruisesToListing(String wildExpocode, 
						String username) throws IllegalArgumentException {
		CruiseFileHandler cruiseHandler;
		try {
			cruiseHandler = DashboardConfigStore.get().getCruiseFileHandler();
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the cruise file handler");
		}
		HashSet<String> matchExpocodes = 
				cruiseHandler.getMatchingExpocodes(wildExpocode);
		if ( matchExpocodes.size() == 0 ) 
			throw new IllegalArgumentException(
					"No datasets with an expocode matching " + wildExpocode);
		DashboardCruiseList cruiseList = getCruiseListing(username);
		String commitMsg = "Added cruise(s) ";
		boolean needsCommit = false;
		for ( String expocode : matchExpocodes ) {
			// Create a cruise entry for this data
			DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
			if ( cruise == null ) 
				throw new IllegalArgumentException("Unexpected error: dataset " +
						expocode + " does not exist");
			// Add or replace this cruise entry in the cruise list
			// Only the expocodes (keys) are saved in the cruise list
			if ( cruiseList.put(expocode, cruise) == null ) {
				commitMsg += expocode + ", ";
				needsCommit = true;
			}
		}
		if ( needsCommit )
			saveCruiseListing(cruiseList, 
					commitMsg + " to the listing for " + username); 
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
	public DashboardCruiseList addCruisesToListing(ArrayList<String> expocodeList, 
							String username) throws IllegalArgumentException {
		CruiseFileHandler cruiseHandler;
		try {
			cruiseHandler = DashboardConfigStore.get().getCruiseFileHandler();
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the cruise file handler");
		}
		DashboardCruiseList cruiseList = getCruiseListing(username);
		String commitMsg = "Added cruises ";
		// Create a cruise entry for this data
		for ( String expocode : expocodeList) {
			DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
			if ( cruise == null ) 
				throw new IllegalArgumentException(
						"cruise " + expocode + " does not exist");
			// Add or replace this cruise entry in the cruise list
			// Only the expocodes (keys) are saved in the cruise list
			cruiseList.put(expocode, cruise);
			commitMsg += expocode + ", ";
		}
		commitMsg += " to the listing for " + username;
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
	public void assignDataColumnTypes(DashboardCruise cruise) 
												throws IllegalArgumentException {
		// Copy the default maps of data column names to types and units
		HashMap<String,DataColumnType> userColNamesToTypes = 
				new HashMap<String,DataColumnType>(defaultColNamesToTypes);
		HashMap<String,String> userColNamesToUnits =
				new HashMap<String,String>(defaultColNamesToUnits);
		HashMap<String,String> userColNamesToMissVals =
				new HashMap<String,String>(defaultColNamesToMissVals);
		// Add the user-customized map of column names to types
		File propsFile = new File(filesDir, 
				cruise.getOwner() + USER_DATA_COLUMNS_NAME_EXTENSION);
		if ( propsFile.exists() ) 
			addDataColumnNames(propsFile, userColNamesToTypes, 
					userColNamesToUnits, userColNamesToMissVals);
		// Directly assign the lists contained in the cruise
		ArrayList<DataColumnType> colTypes = cruise.getDataColTypes();
		colTypes.clear();
		ArrayList<String> colUnits = cruise.getDataColUnits();
		colUnits.clear();
		ArrayList<String> missVals = cruise.getMissingValues();
		missVals.clear();
		// Go through the column names to assign these lists
		for ( String colName : cruise.getUserColNames() ) {
			// Convert the column name to the key
			String key = keyFromColumnName(colName);
			DataColumnType thisColType = userColNamesToTypes.get(key);
			if ( thisColType == null )
				thisColType = DataColumnType.UNKNOWN;
			String thisColUnit = userColNamesToUnits.get(key);
			if ( thisColUnit == null )
				thisColUnit = "";
			String thisMissVal = userColNamesToMissVals.get(key);
			if ( thisMissVal == null )
				thisMissVal = "";
			colTypes.add(thisColType);
			colUnits.add(thisColUnit);
			missVals.add(thisMissVal);
		}
	}

	/**
	 * Updates and saves the data column names to types, units, and missing
	 * values properties file for a cruise owner from the currently assigned 
	 * column names, types, units, and missing values given in a cruise.
	 * 
	 * @param cruise
	 * 		update the data column names to types, units, and missing values
	 * 		from this cruise
	 * @param username
	 * 		user making this update (for the version control commit message)
	 * @throws IllegalArgumentException
	 * 		if the data column names to types, units, and missing values 
	 * 		properties file for the cruise owner, if it exists, is invalid, 
	 * 		or if unable to save or commit the updated version of this file 
	 */
	public void updateUserDataColumnTypes(DashboardCruise cruise, String username) 
											throws IllegalArgumentException {
		// Copy the default maps of data column names to types and units
		HashMap<String,DataColumnType> userColNamesToTypes = 
				new HashMap<String,DataColumnType>(defaultColNamesToTypes);
		HashMap<String,String> userColNamesToUnits =
				new HashMap<String,String>(defaultColNamesToUnits);
		HashMap<String,String> userColNamesToMissVals = 
				new HashMap<String,String>(defaultColNamesToMissVals);
		// Add the user-customized map of column names to types
		File propsFile = new File(filesDir, 
				cruise.getOwner() + USER_DATA_COLUMNS_NAME_EXTENSION);
		if ( propsFile.exists() ) 
			addDataColumnNames(propsFile, userColNamesToTypes, 
								userColNamesToUnits, userColNamesToMissVals);
		// Add mappings of data columns names to types, units, 
		// and missing values from this cruise
		ArrayList<DataColumnType> colTypes = cruise.getDataColTypes();
		ArrayList<String> colUnits = cruise.getDataColUnits();
		ArrayList<String> missVals = cruise.getMissingValues();
		boolean changed = false;
		int k = 0;
		for ( String colName : cruise.getUserColNames() ) {
			String key = keyFromColumnName(colName);
			DataColumnType thisColType = colTypes.get(k);
			DataColumnType oldType = userColNamesToTypes.put(key, thisColType);
			if ( thisColType != oldType )
				changed = true;
			String thisColUnit = colUnits.get(k);
			String oldColUnit = userColNamesToUnits.put(key, thisColUnit);
			if ( ! thisColUnit.equals(oldColUnit) )
				changed = true;
			String thisMissVal = missVals.get(k);
			String oldMissVal = userColNamesToMissVals.put(key, thisMissVal);
			if ( ! thisMissVal.equals(oldMissVal) )
				changed = true;
			k++;
		}

		// If nothing has changed, nothing to do
		if ( ! changed ) 
			return;

		// Remove the default name to type mappings
		for ( Entry<String,DataColumnType> defEntry : defaultColNamesToTypes.entrySet() ) {
			DataColumnType thisColType = userColNamesToTypes.remove(defEntry.getKey());
			if ( thisColType != defEntry.getValue() )
				userColNamesToTypes.put(defEntry.getKey(), thisColType);
		}
		// Remove the default name to type mappings
		for ( Entry<String,String> defEntry : defaultColNamesToUnits.entrySet() ) {
			String thisColUnit = userColNamesToUnits.remove(defEntry.getKey());
			if ( ! thisColUnit.equals(defEntry.getValue()) )
				userColNamesToUnits.put(defEntry.getKey(), thisColUnit);
		}
		// Remove the default name to missing value mappings
		for ( Entry<String,String> defEntry : defaultColNamesToMissVals.entrySet() ) {
			String thisMissVal = userColNamesToMissVals.remove(defEntry.getKey());
			if ( ! thisMissVal.equals(defEntry.getValue()) )
				userColNamesToMissVals.put(defEntry.getKey(), thisMissVal);
		}
		// Create the Properties object for these mappings. 
		Properties colProps = new Properties();
		// Note that the keys for the maps could no longer be identical. 
		HashSet<String> allKeys = new HashSet<String>(userColNamesToTypes.keySet());
		allKeys.addAll(userColNamesToUnits.keySet());
		allKeys.addAll(userColNamesToMissVals.keySet());
		for ( String key : allKeys ) {
			DataColumnType thisColType = userColNamesToTypes.get(key);
			if ( thisColType == null )
				thisColType = defaultColNamesToTypes.get(key);
			String thisColUnit = userColNamesToUnits.get(key);
			if ( thisColUnit == null )
				thisColUnit = defaultColNamesToUnits.get(key);
			String thisMissVal = userColNamesToMissVals.get(key);
			if ( thisMissVal == null )
				thisMissVal = defaultColNamesToMissVals.get(key);
			colProps.setProperty(key, thisColType.name() + "," + 
									thisColUnit + "," + thisMissVal);
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
					"and missing values file for " + cruise.getOwner() + "\n" + 
					ex.getMessage());
		}
		// Commit the update version of this file
		try {
			commitVersion(propsFile, 
					"Data column names to types, units, and missing values " +
					"properties file for " + cruise.getOwner() + 
					" updated by " + username);
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					"Problems committing the data column names to types, units, " +
					"and missing values file for " + cruise.getOwner() + "\n" + 
					ex.getMessage());
		}
	}

}
