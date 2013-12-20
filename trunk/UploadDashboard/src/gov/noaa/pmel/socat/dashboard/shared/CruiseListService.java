/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.HashSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Server side interface for obtaining and modifying a user's list 
 * of cruises and possibly the cruise data as well.
 * 
 * @author Karl Smith
 */
@RemoteServiceRelativePath("CruiseListService")
public interface CruiseListService extends RemoteService {

	/**
	 * After authenticating the user using the given credentials,
	 * modifies the user's list of cruises, and possibly the cruise
	 * data as well, according to the given action.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param action
	 * 		the cruise list action to perform
	 * @param expocodeSet
	 * 		cruise expocodes to act upon (if appropriate)
	 * @return
	 * 		the updated cruise list
	 * @throws IllegalArgumentException
	 * 		if authentication failed, if the action is not recognized
	 * 		or permitted, or if the expocode (if used) is invalid
	 */
	DashboardCruiseList updateCruiseList(String username, String passhash, 
			String action, HashSet<String> expocodeSet) throws IllegalArgumentException;

}
