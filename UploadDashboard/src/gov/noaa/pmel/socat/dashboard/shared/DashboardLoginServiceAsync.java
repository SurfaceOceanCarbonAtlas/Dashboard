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
	 * @param userhash
	 * 		encrypted username to use
	 * @param passhash
	 * 		encrypted password to use
	 * @param callback
	 * 		callback to make with response
	 */
	void authenticateUser(String userhas, String passhash, 
			AsyncCallback<DashboardCruiseListing> callback);

}
