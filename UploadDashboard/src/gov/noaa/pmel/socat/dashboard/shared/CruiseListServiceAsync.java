/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.HashSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Client side interface for obtaining and modifying 
 * a user's list of cruises, and possibly the cruise data as well.
 * 
 * @author Karl Smith
 */
public interface CruiseListServiceAsync {

	/**
	 * Client side request to modify the user's list of cruises, 
	 * and possibly the cruise data as well, according to the 
	 * given action.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param action
	 * 		the cruise list action to perform
	 * @param expocodeSet
	 * 		cruise expocodes to act upon (if appropriate)
	 * @param callback
	 * 		the callback to make with the updated cruise list;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed, if the action is not recognized
	 * 		or permitted, or if an expocode (if used) is invalid
	 */
	void updateCruiseList(String username, String passhash, String action,
			HashSet<String> expocodeSet, AsyncCallback<DashboardCruiseList> callback);

}
