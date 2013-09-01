/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

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
	 * @throws IllegalArgumentException
	 * 		if the directory does not exist
	 */
	DashboardUserFileHandler(String userFilesDirName) 
									throws IllegalArgumentException {
		super(userFilesDirName);
	}
	
	/**
	 * Gets the list of cruises for a user
	 * 
	 * @param username
	 * 		get cruises for this user
	 * @return
	 * 		the list of cruises for the user; 
	 * 		may be null if there is no list saved
	 * @throws IllegalArgumentException
	 * 		if username was invalid or if there was a problem 
	 * 		reading an existing cruise listing
	 */
	@SuppressWarnings("unchecked")
	ArrayList<DashboardCruise> getCruisesForUser(String username) 
										throws IllegalArgumentException {
		if ( (username == null) || username.trim().isEmpty() )
			throw new IllegalArgumentException("invalid username");
		File userDataFile = new File(filesDir, 
				username + USER_CRUISE_LIST_NAME_EXTENSION);
		ArrayList<DashboardCruise> cruiseList;
		try {
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
					new FileInputStream(userDataFile)));
			try {
				cruiseList = (ArrayList<DashboardCruise>) decoder.readObject();
			} finally {
				decoder.close();
			}
		} catch ( FileNotFoundException ex ) {
			cruiseList = null;
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems reading the cruise listing from " + 
					userDataFile.getPath() + ": " + ex.getMessage());

		}
		return cruiseList;
	}

	/**
	 * Saves the list of cruises for a user
	 * 
	 * @param cruiseList
	 * 		list of cruises to be saved
	 * @param username
	 * 		username for this cruise list
	 * @throws IllegalArgumentException
	 * 		if username was invalid or if there was a problem 
	 * 		saving the cruise listing
	 */
	void saveCruiseListForUser(ArrayList<DashboardCruise> cruiseList, 
			String username) throws IllegalArgumentException {
		if ( (username == null) || username.isEmpty() )
			throw new IllegalArgumentException("invalid username");
		File userDataFile = new File(filesDir, 
				username + USER_CRUISE_LIST_NAME_EXTENSION);
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
					"Problems saving the cruise listing as " + 
					userDataFile.getPath() + ": " + ex.getMessage());
		}
	}

}
