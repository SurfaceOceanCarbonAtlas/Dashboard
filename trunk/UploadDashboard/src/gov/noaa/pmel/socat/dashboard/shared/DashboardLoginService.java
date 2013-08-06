/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

/**
 * Server side service interface for logging in a user to the dashboard
 * 
 * @author Karl Smith
 */
@RemoteServiceRelativePath("service")
public interface DashboardLoginService extends XsrfProtectedService {

	/**
	 * Authenticate a user with the given login credentials
	 * 
	 * @param userhash
	 * 		encrypted username to use
	 * @param passhash
	 * 		encrypted password to use
	 * @return
	 * 		the list of cruises, which could be empty, for a user; 
	 * 		will have a null user if authentication fails
	 */
	DashboardCruiseListing authenticateUser(String userhash, String passhash);

}
