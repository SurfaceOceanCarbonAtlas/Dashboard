/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;

import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataListService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the DashboardMetadataListService
 * @author Karl Smith
 */
public class DashboardMetadataListServiceImpl extends RemoteServiceServlet
									implements DashboardMetadataListService {

	private static final long serialVersionUID = 8321670951192083592L;

	@Override
	public DashboardMetadataList getMetadataList(String username,
			String passhash) throws IllegalArgumentException {
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
		// Return the metadata listing for this user
		return dataStore.getUserFileHandler().getMetadataListing(username);
	}

	@Override
	public void associateMetadata(String username, String passhash,
			TreeSet<String> cruiseExpocodes, 
			HashSet<String> metadataExpocodeFilenames)
											throws IllegalArgumentException {
		// TODO:
		throw new IllegalArgumentException("not yet implemented");
	}

}
