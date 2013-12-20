/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.io.IOException;

import gov.noaa.pmel.socat.dashboard.shared.LogoutService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the LogoutService
 * 
 * @author Karl Smith
 */
public class LogoutServiceImpl extends RemoteServiceServlet
									implements LogoutService {

	private static final long serialVersionUID = -9177352138008550409L;

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
