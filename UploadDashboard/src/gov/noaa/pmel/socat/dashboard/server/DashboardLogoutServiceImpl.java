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

	private static final long serialVersionUID = -5132899945725543765L;

	private DashboardDataStore dataStore;

	public DashboardLogoutServiceImpl() throws IOException {
		// Read the standard configuration file if not already done
		dataStore = DashboardDataStore.get();
	}

	@Override
	public boolean logoutUser(String username, String passhash) {
		if ( ! dataStore.validateUser(username, passhash) )
			return false;
		getServletContext().removeAttribute("JSESSIONID");
		return true;
	}

}
