/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;
import gov.noaa.pmel.socat.dashboard.shared.DashboardLoginService;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

/**
 * Server side implementation of the DashboardLoginService
 * 
 * @author Karl Smith
 */
public class DashboardLoginServiceImpl extends XsrfProtectedServiceServlet
		implements DashboardLoginService {

	private static final long serialVersionUID = -3277121132729332575L;

	private DashboardDataStore dataStore;

	public DashboardLoginServiceImpl() throws IOException {
		// Read the standard configuration file if not already done
		dataStore = DashboardDataStore.get();
	}

	@Override
	public DashboardCruiseListing authenticateUser(String userhash, String passhash) {
		// Authenticate the user
		String username = dataStore.getUsernameFromHashes(userhash, passhash);
		ArrayList<DashboardCruise> cruises;
		if ( username != null ) {
			cruises = dataStore.getCruisesForUser(username);
		}
		else
			cruises = null;
		return new DashboardCruiseListing(username, cruises);
	}

}
