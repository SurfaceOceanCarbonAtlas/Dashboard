/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.actions.DashboardCruiseSubmitter;
import gov.noaa.pmel.socat.dashboard.shared.AddToSocatService;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of AddToSocatService
 * 
 * @author Karl Smith
 */
public class AddToSocatServiceImpl extends RemoteServiceServlet 
									implements AddToSocatService {

	private static final long serialVersionUID = -1915513030007107112L;

	private String username = null;
	private DashboardDataStore dataStore = null;

	/**
	 * Validates the given request by retrieving the current username from the request.
	 * Assigns the username and dataStore fields in this instance.
	 * 
	 * @return
	 * 		true if the request obtained a valid username; otherwise false
	 * @throws IllegalArgumentException
	 * 		if unable to obtain the dashboard data store
	 */
	private boolean validateRequest() throws IllegalArgumentException {
		username = null;
		dataStore = null;
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unexpected configuration error: " + ex.getMessage());
		}
		HttpServletRequest request = getThreadLocalRequest();
		try {
			username = request.getUserPrincipal().getName().trim();
		} catch (Exception ex) {
			// Probably null pointer exception - leave username null
			return false;
		}
		return dataStore.validateUser(username);
	}

	@Override
	public void addCruisesToSocat(String pageUsername, HashSet<String> cruiseExpocodes, 
			String archiveStatus, String localTimestamp, boolean repeatSend) 
					throws IllegalArgumentException {
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		// Check that the username matches that which was displayed on the page
		if ( ! username.equals(pageUsername) )
			throw new IllegalArgumentException("Invalid user request");
		// Submit the cruises for QC and possibly send to CDIAC
		DashboardCruiseSubmitter submitter = new DashboardCruiseSubmitter(dataStore);
		submitter.submitCruises(cruiseExpocodes, archiveStatus, 
								localTimestamp, repeatSend, username);
		Logger.getLogger("AddToSocatService").info("cruises " + cruiseExpocodes.toString() + 
				" submitted by " + username);
	}

}
