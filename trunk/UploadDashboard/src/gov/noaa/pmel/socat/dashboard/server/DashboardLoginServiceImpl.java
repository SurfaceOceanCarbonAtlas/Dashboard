/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;
import gov.noaa.pmel.socat.dashboard.shared.DashboardLoginService;

import java.io.IOException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the DashboardLoginService
 * 
 * @author Karl Smith
 */
public class DashboardLoginServiceImpl extends RemoteServiceServlet
										implements DashboardLoginService {

	private static final long serialVersionUID = -3277121132729332575L;

	private DashboardDataStore dataStore;

	public DashboardLoginServiceImpl() throws IOException {
		// Read the standard configuration file if not already done
		dataStore = DashboardDataStore.get();
	}

	@Override
	public DashboardCruiseListing authenticateUser(String username, 
												   String passhash) {
		// Authenticate the user
		if ( ! dataStore.validateUser(username, passhash) )
			return new DashboardCruiseListing();
		return dataStore.getUserFileHandler().getCruiseListing(username);
	}

}
