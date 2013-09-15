/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;

import org.tmatesoft.svn.core.SVNException;

/**
 * Handles storage and retrieval of user data in files.
 * 
 * @author Karl Smith
 */
public class DashboardUserFileHandler extends VersionedFileHandler {

	private static final String USER_CRUISE_LIST_NAME_EXTENSION = 
			"_cruise_list.xml";

	/**
	 * Handles storage and retrieval of user data in files under 
	 * the given user files directory.
	 * 
	 * @param userFilesDirName
	 * 		name of the user files directory
	 * @throws SVNException
	 * 		if the specified directory does not exist,
	 * 		is not a directory, or is not under SVN 
	 * 		version control
	 */
	DashboardUserFileHandler(String userFilesDirName) throws SVNException {
		super(userFilesDirName);
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
		// Get the name of the cruise list XML file for this user
		if ( (username == null) || username.trim().isEmpty() )
			throw new IllegalArgumentException("invalid username");
		File userDataFile = new File(filesDir, 
				username + USER_CRUISE_LIST_NAME_EXTENSION);
		boolean needsCommit = false;
		String commitMessage = "";
		// Make sure we are reading the latest version
		try {
			updateVersion(userDataFile);
		} catch (SVNException ex) {
			// May not exist or may not yet be under version control
			needsCommit = true;
			commitMessage = "add new cruise listing for " + username + "; ";
		}
		// Read the cruise list from the XML file
		DashboardCruiseList cruiseList;
		try {
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
					new FileInputStream(userDataFile)));
			try {
				Object obj = decoder.readObject();
				if ( ! (obj instanceof DashboardCruiseList) )
					throw new Exception(
							"unexpected type of encoded object");
				cruiseList = (DashboardCruiseList) obj;
				if ( ! username.equals(cruiseList.getUsername()) )
					throw new Exception(
							"unexpected username associated with saved listing");
			} finally {
				decoder.close();
			}
		} catch ( FileNotFoundException ex ) {
			// Return a valid cruise listing with no cruises
			cruiseList = new DashboardCruiseList();
			cruiseList.setUsername(username);
			needsCommit = true;
			commitMessage = "add new cruise listing for " + username + "; ";
		} catch ( Exception ex ) {
			// Problems with the listing in the existing file
			throw new IllegalArgumentException(
					"Problems reading the cruise listing from " + 
					userDataFile.getPath() + ": " + ex.getMessage());
		}
		// update the cruise information, committing any changes
		DashboardCruiseFileHandler cruiseHandler;
		try {
			cruiseHandler = DashboardDataStore.get().getCruiseFileHandler();
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the cruise file handler");
		}
		HashSet<String> cruisesToRemove = new HashSet<String>();
		for ( DashboardCruise cruise : cruiseList.values() ) {
			String expocode = cruise.getExpocode();
			// Collect cruise entries that need to be removed
			// (cannot modify the map while going through it)
			if ( ! cruiseHandler.cruiseDataFileExists(expocode) )
				cruisesToRemove.add(expocode);
			// TODO: check and update other attributes of the cruise, 
			//       setting needsCommit is something was changed
		}
		for ( String expocode : cruisesToRemove ) {
			cruiseList.remove(expocode);
			needsCommit = true;
			commitMessage += "remove invalid cruise " + expocode + "; ";
		}
		if ( needsCommit )
			saveCruiseListing(cruiseList, commitMessage);
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
		// Get the name of the cruise list XML file for this user
		if ( (username == null) || username.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		File userDataFile = new File(filesDir, 
				username + USER_CRUISE_LIST_NAME_EXTENSION);
		// Write the current cruise list to the XML file
		try {
			XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(
					new FileOutputStream(userDataFile)));
			try {
				encoder.writeObject(cruiseList);
			} finally {
				encoder.close();
			}
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems saving the cruise listing " + 
					userDataFile.getPath() + ": " + ex.getMessage());
		}
		if ( (message == null) || message.trim().isEmpty() )
			return;
		// Commit the update to this cruise list XML file
		try {
			commitVersion(userDataFile, message);
		} catch (SVNException ex) {
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
	 * 		if the cruise data file does not exist, if 
	 * 		there was a problem saving the updated cruise 
	 * 		listing, or if there was an error committing 
	 * 		the updated cruise listing to version control
	 */
	public DashboardCruiseList addCruisesToListing(
							HashSet<String> expocodeSet, String username) 
										throws IllegalArgumentException {
		DashboardCruiseList cruiseList = getCruiseListing(username);
		boolean changeMade = false;
		String commitMessage = 
				"cruises added to the listing for " + username + ": ";
		for ( String expocode : expocodeSet ) {
			// Create a cruise entry for this data
			try {
				// Create a cruise entry for this data and 
				// add or replace it in the cruise list
				cruiseList.put(expocode, DashboardDataStore.get()
								.getCruiseFileHandler()
								.createDashboardCruiseFromDataFile(expocode));
				changeMade = true;
				commitMessage += expocode + "; ";
			} catch (FileNotFoundException ex) {
				throw new IllegalArgumentException("cruise " + expocode + 
						" does not exist");
			} catch ( Exception ex ) {
				throw new IllegalArgumentException(
						"Unexpected failure to get the cruise file handler");
			}
		}
		if ( changeMade ) {
			// Save the updated cruise listing
			saveCruiseListing(cruiseList, commitMessage);
		}
		return cruiseList;
	}

}
