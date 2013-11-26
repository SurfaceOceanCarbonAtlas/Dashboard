/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataListService;

import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the DashboardMetadataListService
 * @author Karl Smith
 */
public class DashboardMetadataListServiceImpl extends RemoteServiceServlet
									implements DashboardMetadataListService {

	private static final long serialVersionUID = 9076359833749591580L;

	@Override
	public DashboardMetadataList getMetadataList(String username, String passhash, 
			TreeSet<String> cruiseExpocodes) throws IllegalArgumentException {
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
		// Get the metadata listing for this user
		DashboardMetadataList mdataList = 
				dataStore.getUserFileHandler().getMetadataListing(username);
		// Get the list of metadata documents for the cruises
		HashSet<String> expocodeFilenames = new HashSet<String>();
		for ( String expocode : cruiseExpocodes ) {
			DashboardCruise cruise = dataStore.getCruiseFileHandler()
											  .getCruiseFromInfoFile(expocode);
			if ( cruise == null )
				throw new IllegalArgumentException(
						"No cruise file for " + expocode);
			for ( String metadataName : cruise.getMetadataFilenames() ) {
				// The string should have format "expocodeFilename (uploadFilename)"
				expocodeFilenames.add(metadataName.split(" (")[0]);
			}
		}
		// Go through the metadata listing, selecting those in the cruises
		for ( DashboardMetadata mdata : mdataList ) {
			if ( expocodeFilenames.contains(mdata.getExpocodeFilename()) )
				mdata.setSelected(true);
			else
				mdata.setSelected(false);
		}
		// Return the metadata listing
		return mdataList;
	}

	@Override
	public void associateMetadata(String username, String passhash,
			TreeSet<String> cruiseExpocodes, HashSet<DashboardMetadata> metadata)
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

		// Make metadata descriptions for each metadata document
		TreeSet<String> metadataNames = new TreeSet<String>(); 
		for ( DashboardMetadata mdata : metadata )
			metadataNames.add(mdata.getExpocodeFilename() + 
					" (" + mdata.getUploadFilename() + ")");

		// Create the commit message
		StringBuilder sb = new StringBuilder();
		sb.append("Assigned associated metadata document(s): ");
		boolean first = true;
		for ( String name : metadataNames ) {
			if ( first )
				first = false;
			else
				sb.append(", ");
			sb.append(name);
		}
		sb.append(" to cruise ");
		String message = sb.toString();

		// Assign this set of metadata documents to each of the cruises
		for ( String expocode : cruiseExpocodes ) {
			// Read the cruise information file
			DashboardCruise cruise = dataStore.getCruiseFileHandler()
											  .getCruiseFromInfoFile(expocode);
			if ( cruise == null )
				throw new IllegalArgumentException(
						"Cruise does not exist: " + expocode);
			// Change the set of metadata documents for this cruise
			cruise.setMetadataFilenames(metadataNames);
			// Save the updated cruise
			dataStore.getCruiseFileHandler()
					 .saveCruiseToInfoFile(cruise, message + expocode);
		}
	}

}

	