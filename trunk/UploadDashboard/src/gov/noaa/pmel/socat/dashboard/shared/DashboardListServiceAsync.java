/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

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
	 * Client side request to get the current user's list of cruises.
	 * 
	 * @param callback
	 * 		the callback to make with the cruise list.
	 */
	void getCruiseList(AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to log out the current user.
	 * 
	 * @param callback
	 * 		the callback to make after logout.
	 */
	void logoutUser(AsyncCallback<Void> callback);

	/**
	 * Client side request to deletes all files for the indicated cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		cruises to be deleted
	 * @param deleteMetadata
	 * 		also delete metadata and additional documents?
	 * @return
	 * 		the updated list of cruises for the current user
	 * @param callback
	 * 		the callback to make with the updated cruise list for the current user
	 */
	void deleteCruises(String username, TreeSet<String> expocodeSet, 
			Boolean deleteMetadata, AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to add the indicated cruises 
	 * to the current user's list of cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param wildExpocode
	 * 		expocode, possibly with wildcards * and ?, to use
	 * @param callback
	 * 		the callback to make with the current user's updated cruise list
	 */
	void addCruisesToList(String username, String wildExpocode,
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to remove the indicated cruises from the current 
	 * user's list of cruises (but does not delete any files for these cruises).
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		cruises to be removed
	 * @param callback
	 * 		the callback to make with the current user's updated cruise list
	 */	
	void removeCruisesFromList(String username, TreeSet<String> expocodeSet, 
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to return the latest cruise information 
	 * for the indicated cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		set of all cruises to include in the returned
	 * 		updated cruise information
	 * @param callback
	 * 		the callback to make with the updated cruise information
	 */
	void getUpdatedCruises(String username, TreeSet<String> expocodeSet,
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to remove (delete) an ancillary document 
	 * for a cruise.
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
	 * @param callback
	 * 		the callback to make with the updated cruise information
	 */
	void deleteAddlDoc(String username, String deleteFilename, 
			String expocode, TreeSet<String> allExpocodes,
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to send the set of sanity checker 
	 * data messages for a given cruise.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocode
	 * 		get data messages for this cruise
	 * @return
	 * 		list of data messages for the cruise; 
	 * 		never null, but may be empty if the SanityChecker 
	 * 		did not generate any data messages. 
	 * @param callback
	 * 		the callback to make with list of sanity checker data messages
	 */
	void getDataMessages(String username, String expocode,
			AsyncCallback<SCMessageList> callback);

}
