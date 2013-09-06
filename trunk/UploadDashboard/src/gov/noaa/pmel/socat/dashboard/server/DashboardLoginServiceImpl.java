/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.io.IOException;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;
import gov.noaa.pmel.socat.dashboard.shared.DashboardLoginService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the DashboardLoginService
 * 
 * @author Karl Smith
 */
public class DashboardLoginServiceImpl extends RemoteServiceServlet
										implements DashboardLoginService {

	private static final long serialVersionUID = 3988110209221394253L;

	@Override
	public DashboardCruiseListing authenticateUser(String username, 
												   String passhash) {
		// Authenticate the user
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			return new DashboardCruiseListing();
		}
		if ( ! dataStore.validateUser(username, passhash) )
			return new DashboardCruiseListing();
		return dataStore.getUserFileHandler().getCruiseListing(username);
	}

}
