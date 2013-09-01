/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Server side service interface for logging in a user to the dashboard
 * 
 * @author Karl Smith
 */
@RemoteServiceRelativePath("loginService")
public interface DashboardLoginService extends RemoteService {

	/**
	 * Authenticate a user with the given login credentials
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @return
	 * 		the list of cruises, which could be empty, for a user; 
	 * 		will have a null user if authentication fails
	 */
	DashboardCruiseListing authenticateUser(String username, String passhash);

}
