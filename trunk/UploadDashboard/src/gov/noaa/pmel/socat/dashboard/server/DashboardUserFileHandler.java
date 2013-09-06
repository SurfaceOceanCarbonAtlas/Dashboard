/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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
	 * 		if username was invalid or if there was a problem 
	 * 		reading an existing cruise listing
	 */
	public DashboardCruiseListing getCruiseListing(String username) 
										throws IllegalArgumentException {
		// Get the name of the cruise list XML file for this user
		if ( (username == null) || username.trim().isEmpty() )
			throw new IllegalArgumentException("invalid username");
		File userDataFile = new File(filesDir, 
				username + USER_CRUISE_LIST_NAME_EXTENSION);
		// Make sure we are reading the latest version
		try {
			updateVersion(userDataFile);
		} catch (SVNException ex) {
			// May not exist or may not yet be under version control
			;
		}
		// Read the cruise list from the XML file
		DashboardCruiseListing cruiseList;
		try {
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
					new FileInputStream(userDataFile)));
			try {
				Object obj = decoder.readObject();
				if ( ! (obj instanceof DashboardCruiseListing) )
					throw new Exception(
							"unexpected type of encoded object");
				cruiseList = (DashboardCruiseListing) obj;
				if ( ! username.equals(cruiseList.getUsername()) )
					throw new Exception(
							"unexpected username associated with saved listing");
			} finally {
				decoder.close();
			}
		} catch ( FileNotFoundException ex ) {
			// Return a valid cruise listing with no cruises
			cruiseList = new DashboardCruiseListing();
			cruiseList.setUsername(username);
		} catch ( Exception ex ) {
			// Problems with the listing in the existing file
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
	 * 		listing of cruises to be saved
	 * @param message
	 * 		version control commit message; 
	 * 		if null or blank, the commit will not be performed 
	 * @throws IllegalArgumentException
	 * 		if the cruise listing was invalid or if there was 
	 * 		a problem saving the cruise listing
	 * @throws SVNException
	 * 		if there was an error committing the updated file 
	 * 		to version control
	 */
	public void saveCruiseListing(DashboardCruiseListing cruiseList, 
			String message) throws IllegalArgumentException, SVNException {
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
		commitVersion(userDataFile, message);
	}

}
