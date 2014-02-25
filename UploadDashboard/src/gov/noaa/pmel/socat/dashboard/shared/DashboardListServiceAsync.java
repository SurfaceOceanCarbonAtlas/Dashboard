/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.HashSet;
import java.util.TreeSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Client side interface for obtaining and modifying lists
 * (sets, maps) in the dashboard.
 * 
 * @author Karl Smith
 */
public interface DashboardListServiceAsync {

	/**
	 * Client side request to log out a user.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @return
	 * 		true if successful
	 * @param callback
	 * 		the callback to make with result;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed.
	 */
	void logoutUser(String username, String passhash,
			AsyncCallback<Boolean> callback);

	/**
	 * Client side request to get a user's list of cruises.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @return
	 * 		the list of cruises for the user
	 * @param callback
	 * 		the callback to make with the cruise list;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed.
	 */
	void getCruiseList(String username, String passhash,
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to deletes all files for the indicated cruises.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param expocodeSet
	 * 		cruises to be deleted
	 * @return
	 * 		the updated list of cruises for the user
	 * @param callback
	 * 		the callback to make with the cruise list;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed, or 
	 * 		if problems deleting a cruise
	 */
	void deleteCruises(String username, String passhash, 
			HashSet<String> expocodeSet,
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to add the indicated cruises to the user's 
	 * list of cruises.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param expocode
	 * 		cruises to be added
	 * @return
	 * 		the updated list of cruises for the user
	 * @param callback
	 * 		the callback to make with the cruise list;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed, or 
	 * 		if the cruise to be added does not exist
	 */
	void addCruiseToList(String username, String passhash, 
			String expocode,
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to remove the indicated cruises from the user's 
	 * list of cruises (but does not delete any files for these cruises).
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param expocodeSet
	 * 		cruises to be removed
	 * @return
	 * 		the updated list of cruises for the user
	 * @param callback
	 * 		the callback to make with the cruise list;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed, or 
	 * 		if problems removing a cruise
	 */	
	void removeCruisesFromList(String username, String passhash,
			HashSet<String> expocodeSet, 
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to return the latest cruise information 
	 * for the indicated cruises.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param expocodeSet
	 * 		set of all cruises to include in the returned
	 * 		updated cruise information
	 * @return
	 * 		updated cruise information for the indicated cruises
	 * @throws IllegalArgumentException
	 * 		if authentication failed or 
	 * 		if a cruise does not exist
	 */
	void getUpdatedCruises(String username, String passhash,
			TreeSet<String> expocodeSet,
			AsyncCallback<HashSet<DashboardCruise>> callback);

	/**
	 * Client side request to remove (delete) an ancillary document 
	 * for a cruise.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param deleteFilename
	 * 		remove the ancillary document with this name
	 * @param expocode
	 * 		remove the ancillary document from this cruise
	 * @param allExpocodes
	 * 		set of all cruises to include in the returned 
	 * 		updated cruise information
	 * @return
	 * 		updated cruise information for the indicated cruises 
	 * @param callback
	 * 		the callback to make with the cruise list;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed or if the ancillary document
	 * 		does not exist 
	 */
	void deleteAddlDoc(String username, String passhash,
			String deleteFilename, String expocode,
			TreeSet<String> allExpocodes,
			AsyncCallback<HashSet<DashboardCruise>> callback);

	/**
	 * Client side request to send the set of sanity checker 
	 * data messages for a given cruise.
	 * 
	 * @param username
	 * 		name of the user making the request
	 * @param passhash
	 * 		password hash of the user making the request
	 * @param expocode
	 * 		get data messages for this cruise
	 * @return
	 * 		list of data messages for the cruise; 
	 * 		never null, but may be empty if the SanityChecker 
	 * 		did not generate any data messages. 
	 * @param callback
	 * 		the callback to make with the cruise list;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication fails, 
	 * 		if the cruise expocode is invalid, or
	 * 		if the SanityChecker has never been run on this cruise.
	 */
	void getDataMessages(String username, String passhash, String expocode,
			AsyncCallback<SCMessageList> callback);

}
