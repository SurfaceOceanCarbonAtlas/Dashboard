/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Server side service interface for logging out a user from the dashboard
 * 
 * @author Karl Smith
 */
public interface LogoutServiceAsync {

	/**
	 * Generate an request to logout the current user 
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		password hash for validation of request
	 * @param callback
	 * 		callback to make with response
	 */
	void logoutUser(String username, String passhash, 
			AsyncCallback<Boolean> callback);

}
