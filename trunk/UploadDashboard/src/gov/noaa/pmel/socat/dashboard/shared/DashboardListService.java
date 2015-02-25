/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.TreeSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Server side interface for obtaining and modifying lists 
 * (sets, maps) in the dashboard.
 * 
 * @author Karl Smith
 */
@RemoteServiceRelativePath("DashboardListService")
public interface DashboardListService extends RemoteService {

	/**
	 * Gets the current user's list of cruises.
	 * 
	 * @return
	 * 		the list of cruises for the current user
	 * @throws IllegalArgumentException
	 * 		if problems getting the cruise list
	 */
	DashboardCruiseList getCruiseList() throws IllegalArgumentException;

	/**
	 * Logs out the current user.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @return
	 * 		true if successful
	 */
	boolean logoutUser(String username);
	
	/**
	 * Deletes all files for the indicated cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		cruises to be deleted
	 * @param deleteMetadata
	 * 		also delete metadata and additional documents?
	 * @return
	 * 		the updated list of cruises for the current user
	 * @throws IllegalArgumentException
	 * 		if problems deleting a cruise
	 */
	DashboardCruiseList deleteCruises(String username, TreeSet<String> expocodeSet, 
			Boolean deleteMetadata) throws IllegalArgumentException;

	/**
	 * Adds the indicated cruises to the current user's list of cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param wildExpocode
	 * 		expocode, possibly with wildcards * and ?, to use
	 * @return
	 * 		the updated list of cruises for the current user
	 * @throws IllegalArgumentException
	 * 		if the cruise to be added does not exist
	 */
	DashboardCruiseList addCruisesToList(String username, String wildExpocode) 
			throws IllegalArgumentException;

	/**
	 * Removes the indicated cruises from the current user's 
	 * list of cruises (but does not delete any files for these cruises).
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		cruises to be removed
	 * @return
	 * 		the updated list of cruises for the user
	 * @throws IllegalArgumentException
	 * 		if problems removing a cruise
	 */
	DashboardCruiseList removeCruisesFromList(String username, TreeSet<String> expocodeSet) 
			throws IllegalArgumentException;

	/**
	 * Returns the latest cruise information for the indicated cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		set of all cruises to include in the returned
	 * 		updated cruise information
	 * @return
	 * 		updated cruise information for the indicated cruises
	 * @throws IllegalArgumentException
	 * 		if a cruise does not exist
	 */
	DashboardCruiseList getUpdatedCruises(String username, TreeSet<String> expocodeSet) 
			throws IllegalArgumentException;

	/**
	 * Delete an ancillary document for a cruise.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param deleteFilename
	 * 		remove the ancillary document with this name
	 * @param expocode
	 * 		remove the ancillary document from this cruise
	 * @param allExpocodes
	 * 		set of all cruises to include in the returned 
	 * 		updated cruise information
	 * @return
	 * 		updated cruise information for the indicated cruises 
	 * @throws IllegalArgumentException
	 * 		if the metadata document does not exist 
	 */
	DashboardCruiseList deleteAddlDoc(String username, String deleteFilename, 
			String expocode, TreeSet<String> allExpocodes) throws IllegalArgumentException;

	/**
	 * Returns the set of sanity checker data messages for a given cruise.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocode
	 * 		get data messages for this cruise
	 * @return
	 * 		list of data messages for the cruise; 
	 * 		never null, but may be empty if the SanityChecker 
	 * 		did not generate any data messages. 
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is invalid, or
	 * 		if the SanityChecker has never been run on this cruise.
	 */
	SCMessageList getDataMessages(String username, String expocode) 
			throws IllegalArgumentException;

}
