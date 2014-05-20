/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.HashSet;
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
	 * After authenticating the user using the given credentials,
	 * logs out the user.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @return
	 * 		true if successful
	 * @throws IllegalArgumentException
	 * 		if authentication failed
	 */
	boolean logoutUser(String username, String passhash);
	
	/**
	 * After authenticating the user using the given credentials,
	 * gets a user's list of cruises.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @return
	 * 		the list of cruises for the user
	 * @throws IllegalArgumentException
	 * 		if authentication failed
	 */
	DashboardCruiseList getCruiseList(String username, String passhash) 
										throws IllegalArgumentException;

	/**
	 * After authenticating the user using the given credentials,
	 * deletes all files for the indicated cruises.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param expocodeSet
	 * 		cruises to be deleted
	 * @return
	 * 		the updated list of cruises for the user
	 * @throws IllegalArgumentException
	 * 		if authentication failed, or 
	 * 		if problems deleting a cruise
	 */
	DashboardCruiseList deleteCruises(String username, String passhash, 
			TreeSet<String> expocodeSet) throws IllegalArgumentException;

	/**
	 * After authenticating the user using the given credentials,
	 * adds the indicated cruise to the user's list of cruises.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param expocode
	 * 		cruises to be added
	 * @return
	 * 		the updated list of cruises for the user
	 * @throws IllegalArgumentException
	 * 		if authentication failed, or 
	 * 		if the cruise to be added does not exist
	 */
	DashboardCruiseList addCruiseToList(String username, String passhash,
						String expocode) throws IllegalArgumentException;

	/**
	 * After authenticating the user using the given credentials,
	 * removes the indicated cruises from the user's list of cruises
	 * (but does not delete any files for these cruises).
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param expocodeSet
	 * 		cruises to be removed
	 * @return
	 * 		the updated list of cruises for the user
	 * @throws IllegalArgumentException
	 * 		if authentication failed, or 
	 * 		if problems removing a cruise
	 */
	DashboardCruiseList removeCruisesFromList(String username, String passhash,
			TreeSet<String> expocodeSet) throws IllegalArgumentException;

	/**
	 * After authenticating the user using the given credentials,
	 * returns the latest cruise information for the indicated cruises.
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
	HashSet<DashboardCruise> getUpdatedCruises(String username, String passhash,
			TreeSet<String> expocodeSet) throws IllegalArgumentException;

	/**
	 * After authenticating the user using the given credentials,
	 * delete an ancillary document for a cruise.
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
	 * @throws IllegalArgumentException
	 * 		if authentication failed or if the metadata document
	 * 		does not exist 
	 */
	HashSet<DashboardCruise> deleteAddlDoc(String username, String passhash,
			String deleteFilename, String expocode, 
			TreeSet<String> allExpocodes) throws IllegalArgumentException;

	/**
	 * After validating the user, returns the set of sanity checker 
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
	 * @throws IllegalArgumentException
	 * 		if authentication fails, 
	 * 		if the cruise expocode is invalid, or
	 * 		if the SanityChecker has never been run on this cruise.
	 */
	SCMessageList getDataMessages(String username, String passhash, 
						String expocode) throws IllegalArgumentException;

}
