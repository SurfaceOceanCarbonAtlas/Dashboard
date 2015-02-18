/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.actions.DashboardCruiseSubmitter;
import gov.noaa.pmel.socat.dashboard.shared.AddToSocatService;

import java.io.IOException;
import java.util.HashSet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of AddToSocatService
 * 
 * @author Karl Smith
 */
public class AddToSocatServiceImpl extends RemoteServiceServlet 
										implements AddToSocatService {

	private static final long serialVersionUID = -177066153383975100L;

	@Override
	public void addCruisesToSocat(String username, String passhash, 
			HashSet<String> cruiseExpocodes, String archiveStatus, 
			String localTimestamp, boolean repeatSend) 
										throws IllegalArgumentException {
		// Authenticate the user
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected configuration error: " + ex.getMessage());
		}
		if ( ! dataStore.validateUser(username, passhash) )
			throw new IllegalArgumentException("Invalid authentication credentials");

		// Submit the cruises for QC and possibly send to CDIAC
		DashboardCruiseSubmitter submitter = new DashboardCruiseSubmitter(dataStore);
		submitter.submitCruises(cruiseExpocodes, archiveStatus, 
								localTimestamp, repeatSend, username);
	}

}
