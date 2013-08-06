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
public interface DashboardLogoutServiceAsync {

	/**
	 * Generate an request to logout the current user 
	 * 
	 * @param callback
	 * 		callback to make with response
	 */
	void logoutUser(AsyncCallback<Void> callback);

}
