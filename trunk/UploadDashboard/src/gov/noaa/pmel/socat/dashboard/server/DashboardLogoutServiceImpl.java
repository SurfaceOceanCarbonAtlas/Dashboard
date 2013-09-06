/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.io.IOException;

import gov.noaa.pmel.socat.dashboard.shared.DashboardLogoutService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the DashboardLogoutService
 * 
 * @author Karl Smith
 */
public class DashboardLogoutServiceImpl extends RemoteServiceServlet
		implements DashboardLogoutService {

	private static final long serialVersionUID = -2160237671362781212L;

	@Override
	public boolean logoutUser(String username, String passhash) {
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			return false;
		}
		if ( ! dataStore.validateUser(username, passhash) )
			return false;
		getServletContext().removeAttribute("JSESSIONID");
		return true;
	}

}
