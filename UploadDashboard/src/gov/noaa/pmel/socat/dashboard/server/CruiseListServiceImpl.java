/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.CruiseListService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of CruiseListService
 * @author Karl Smith
 */
public class CruiseListServiceImpl extends RemoteServiceServlet
									implements CruiseListService {

	private static final long serialVersionUID = 4534512457479911838L;

	@Override
	public DashboardCruiseList updateCruiseList(String username,
			String passhash, String action, HashSet<String> expocodeSet) 
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
			throw new IllegalArgumentException(
					"Invalid authentication credentials");

		if ( DashboardUtils.REQUEST_CRUISE_LIST_ACTION.equals(action) ) {
			// Just return the current cruise list for a user
			return dataStore.getUserFileHandler().getCruiseListing(username);
		}

		if ( DashboardUtils.REQUEST_CRUISE_DELETE_ACTION.equals(action) ) {
			// Remove a cruise data file
			for ( String expocode : expocodeSet ) {
				try {
					dataStore.getCruiseFileHandler()
							 .deleteCruiseFiles(expocode, username);

				} catch (FileNotFoundException ex) {
					// Cruise already deleted?
					;
				}
				// IllegalArgumentException for other problems escape as-is
			}

			// Return the current list of cruises, which should 
			// detect the missing cruises and update itself
			return dataStore.getUserFileHandler().getCruiseListing(username);
		}

		if ( DashboardUtils.REQUEST_CRUISE_ADD_ACTION.equals(action) ) {
			// Add the cruise to the user's list, and return the updated cruise list
			return dataStore.getUserFileHandler()
							.addCruisesToListing(expocodeSet, username);
		}

		if ( DashboardUtils.REQUEST_CRUISE_REMOVE_ACTION.equals(action) ) {
			// Remove the cruise from the user's list, and return the updated list
			return dataStore.getUserFileHandler()
							.removeCruisesFromListing(expocodeSet, username);
		}

		throw new IllegalArgumentException("Unknown action " + action);
	}

}
