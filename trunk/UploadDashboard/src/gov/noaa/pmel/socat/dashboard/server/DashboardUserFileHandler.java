/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * Handles storage and retrieval of user data in files.
 * 
 * @author Karl Smith
 */
public class DashboardUserFileHandler extends VersionedFileHandler {

	private static final String USER_CRUISE_LIST_NAME_EXTENSION = 
			"_cruise_list.txt";

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
	 * 		directory, or is not under SVN version control
	 */
	DashboardUserFileHandler(String userFilesDirName, String svnUsername,
					String svnPassword) throws IllegalArgumentException {
		super(userFilesDirName, svnUsername, svnPassword);
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
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to get settings");
		}
		DashboardCruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		// Create the cruise list (map) for these cruises
		DashboardCruiseList cruiseList = new DashboardCruiseList();
		cruiseList.setUsername(username);
		for ( String expocode : expocodeSet ) {
			// Create the DashboardCruise from the info file
			DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
			if ( cruise == null ) {
				// Remove this expocode from the saved list
				needsCommit = true;
				commitMessage += "remove invalid cruise " + expocode + "; ";
			}
			else {
				cruiseList.put(expocode, cruise);
			}
		}
		if ( needsCommit )
			saveCruiseListing(cruiseList, commitMessage);
		// Determine whether or not this user is a manager/admin 
		cruiseList.setManager(dataStore.isManager(username));
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
							HashSet<String> expocodeSet, String username) 
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
	 * Adds an entry from a user's list of cruises, and
	 * saves the resulting list of cruises.
	 * 
	 * @param expocodeSet
	 * 		expocodes of cruises to add to the list
	 * @param username
	 * 		user whose cruise list is to be updated
	 * @return
	 * 		updated list of cruises for user
	 * @throws IllegalArgumentException
	 * 		if username is invalid, if expocode is invalid,
	 * 		if the cruise information file does not exist, 
	 * 		if there was a problem saving the updated cruise 
	 * 		listing, or if there was an error committing 
	 * 		the updated cruise listing to version control
	 */
	public DashboardCruiseList addCruisesToListing(
							HashSet<String> expocodeSet, String username) 
										throws IllegalArgumentException {
		DashboardCruiseFileHandler cruiseHandler;
		try {
			cruiseHandler = DashboardDataStore.get().getCruiseFileHandler();
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the cruise file handler");
		}
		DashboardCruiseList cruiseList = getCruiseListing(username);
		boolean changeMade = false;
		String commitMessage = 
				"cruises added to the listing for " + username + ": ";
		for ( String expocode : expocodeSet ) {
			// Create a cruise entry for this data
			DashboardCruise cruise = 
					cruiseHandler.getCruiseFromInfoFile(expocode);
			if ( cruise == null ) 
				throw new IllegalArgumentException(
						"cruise " + expocode + " does not exist");
			// Add or replace this cruise entry in the cruise list
			// Only the expocodes (keys) are saved in the cruise list
			if ( cruiseList.put(expocode, cruise) == null ) {
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
	 * Assigns the standard data column types from the user-provided 
	 * data column names.  TODO: The cruise owner is used to obtain 
	 * customized associations of user-provided column names to standard 
	 * column names.
	 *  
	 * @param cruise
	 * 		cruise whose data column types are to be assigned
	 */
	public void assignStandardDataColumnTypes(DashboardCruise cruise) {
		// Directly assign the lists contained in the cruise
		ArrayList<CruiseDataColumnType> colTypes = cruise.getDataColTypes();
		colTypes.clear();
		ArrayList<String> colUnits = cruise.getDataColUnits();
		colUnits.clear();
		// Go through the column names to assign these lists
		for ( String colName : cruise.getUserColNames() ) {
			// TODO: use the cruise owner name to retrieve a file with 
			//       customized associations of column name to type
			CruiseDataColumnType dataType = CruiseDataColumnType.UNKNOWN;
			for ( Entry<CruiseDataColumnType,String> stdNameEntry : 
				DashboardUtils.STD_HEADER_NAMES.entrySet() ) {
				if ( colName.startsWith(stdNameEntry.getValue()) ) {
					dataType = stdNameEntry.getKey();
					break;
				}
			}
			colTypes.add(dataType);
			// TODO: get units from column name
			colUnits.add(DashboardUtils.STD_DATA_UNITS.get(dataType).get(0));
		}
	}

}
