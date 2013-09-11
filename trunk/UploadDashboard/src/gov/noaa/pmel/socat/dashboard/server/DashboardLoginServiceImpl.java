/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.io.IOException;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
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
	public DashboardCruiseList authenticateUser(String username, 
												   String passhash) {
		// Authenticate the user
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			return new DashboardCruiseList();
		}
		if ( ! dataStore.validateUser(username, passhash) )
			return new DashboardCruiseList();
		return dataStore.getUserFileHandler().getCruiseListing(username);
	}

}
