/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;
import gov.noaa.pmel.socat.dashboard.shared.DashboardLoginService;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the DashboardLoginService
 * 
 * @author Karl Smith
 */
public class DashboardLoginServiceImpl extends RemoteServiceServlet
										implements DashboardLoginService {

	private static final long serialVersionUID = -3980197156120622588L;

	private DashboardDataStore dataStore;
	private DashboardUserFileHandler userFileHandler;

	public DashboardLoginServiceImpl() throws IOException {
		// Read the standard configuration file if not already done
		dataStore = DashboardDataStore.get();
		userFileHandler = dataStore.getUserFileHandler();
	}

	@Override
	public DashboardCruiseListing authenticateUser(String username, 
												   String passhash) {
		// Authenticate the user
		if ( ! dataStore.validateUser(username, passhash) )
			return new DashboardCruiseListing(null, null);
		ArrayList<DashboardCruise> cruises = 
				userFileHandler.getCruisesForUser(username);
		return new DashboardCruiseListing(username, cruises);
	}

}
