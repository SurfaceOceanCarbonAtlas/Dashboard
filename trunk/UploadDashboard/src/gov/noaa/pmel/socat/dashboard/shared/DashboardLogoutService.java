/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Server side service interface for logging out a user from the dashboard
 * 
 * @author Karl Smith
 */
@RemoteServiceRelativePath("logoutService")
public interface DashboardLogoutService extends RemoteService {

	/**
	 * Logs out the current user
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		password hash for validation of request
	 * @return
	 * 		true if successful
	 */
	boolean logoutUser(String username, String passhash);

}
