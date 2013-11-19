/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.util.HashSet;

import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataListService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the DashboardMetadataListService
 * @author Karl Smith
 */
public class DashboardMetadataListServiceImpl extends RemoteServiceServlet
		implements DashboardMetadataListService {

	private static final long serialVersionUID = 2192719156698169980L;

	@Override
	public DashboardMetadataList getMetadataList(String username,
			String passhash) throws IllegalArgumentException {
		// TODO:
		throw new IllegalArgumentException("not yet implemented");
	}

	@Override
	public void associateMetadata(String username, String passhash,
			HashSet<String> cruiseExpocodes,
			HashSet<String> metadataExpocodeFilenames)
			throws IllegalArgumentException {
		// TODO:
		throw new IllegalArgumentException("not yet implemented");
	}

}
