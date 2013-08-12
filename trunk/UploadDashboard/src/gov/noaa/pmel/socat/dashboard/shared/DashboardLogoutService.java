/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

/**
 * Server side service interface for logging out a user from the dashboard
 * 
 * @author Karl Smith
 */
@RemoteServiceRelativePath("logoutService")
public interface DashboardLogoutService extends XsrfProtectedService {

	/**
	 * Logs out the current user
	 */
	void logoutUser();

}
