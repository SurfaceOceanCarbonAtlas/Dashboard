package gov.noaa.pmel.socat.dashboard.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Client side service interface for logging in a user to the dashboard
 * 
 * @author Karl Smith
 */
public interface DashboardLoginServiceAsync {

	/**
	 * Generate an request to authenticate a user 
	 * with the given login credentials
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param callback
	 * 		callback to make with response
	 */
	void authenticateUser(String username, String passhash, 
			AsyncCallback<DashboardCruiseList> callback);

}
