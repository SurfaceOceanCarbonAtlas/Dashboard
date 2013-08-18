/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;
import gov.noaa.pmel.socat.dashboard.shared.DashboardLoginService;

import java.io.File;
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

	private static final long serialVersionUID = 3988110209221394253L;

	public DashboardLoginServiceImpl() throws IOException {
		// TODO: use actual store name
		DashboardDataStore.setStoreFile(new File("fake"));
	}

	@Override
	public DashboardCruiseListing authenticateUser(String userhash, String passhash) {
		// Authenticate the user
		String username = DashboardDataStore.get().getUsernameFromHashes(userhash, passhash);
		ArrayList<DashboardCruise> cruises;
		if ( username != null ) {
			cruises = DashboardDataStore.get().getCruisesForUser(username);
		}
		else
			cruises = null;
		return new DashboardCruiseListing(username, cruises);
	}

}
