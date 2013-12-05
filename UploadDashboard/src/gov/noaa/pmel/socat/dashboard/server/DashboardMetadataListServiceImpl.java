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
		// Remove the lists of associated cruises to reduce payload 
		// since these lists are not used in the page
		for ( DashboardMetadata mdata : mdataList.values() )
			mdata.setAssociatedCruises(null);
		// Select the metadata documents for the given cruises
		for ( String expocode : cruiseExpocodes ) {
			DashboardCruise cruise = dataStore.getCruiseFileHandler()
											  .getCruiseFromInfoFile(expocode);
			if ( cruise == null )
				throw new IllegalArgumentException(
						"No cruise file for " + expocode);
			for ( String metadataNames : cruise.getMetadataFilenames() ) {
				// The metadata filenames from the cruise 
				// should have format "expocodeFilename (uploadFilename)"
				String expoName = metadataNames.split(" (")[0];
				DashboardMetadata mdata = mdataList.get(expoName);
				if ( mdata == null )
					throw new IllegalArgumentException("Metadata document " + 
							expoName + " for cruise " + expocode + 
							" not in the metadata list for " + username);
				mdata.setSelected(true);
			}
		}
		// Return the metadata listing
		return mdataList;
	}

	@Override
	public void associateMetadata(String username, String passhash,
			TreeSet<String> cruiseExpocodes, HashSet<String> metaExpoNames)
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
		if ( cruiseExpocodes.size() < 1 ) 
			throw new IllegalArgumentException(
					"No cruise expocodes specified");
		if ( metaExpoNames.size() < 1 )
			throw new IllegalArgumentException(
					"No metadata documents specified");

		// Get the current metadata properties for the indicated metadata documents
		DashboardMetadataList mdataList = new DashboardMetadataList();
		for ( String metaName : metaExpoNames ) {
			DashboardMetadata mdata = dataStore.getMetadataFileHandler()
											   .getMetadataInfo(metaName);
			if ( mdata == null ) 
				throw new IllegalArgumentException(
						"No metadata properties for " + metaName);
			mdataList.put(metaName, mdata);
		}
		
		// Make metadata descriptions for each metadata document
		TreeSet<String> metadataNames = new TreeSet<String>(); 
		for ( DashboardMetadata mdata : mdataList.values() ) {
			metadataNames.add(mdata.getExpocodeFilename() + 
					" (" + mdata.getUploadFilename() + ")");
		}

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

		// Create a set of metadata documents removed from cruises
		TreeSet<String> removedMetaNames = new TreeSet<String>();

		// Assign this set of metadata documents to each of the cruises
		for ( String expocode : cruiseExpocodes ) {
			// Read the cruise information file
			DashboardCruise cruise = dataStore.getCruiseFileHandler()
											  .getCruiseFromInfoFile(expocode);
			if ( cruise == null )
				throw new IllegalArgumentException(
						"Cruise does not exist: " + expocode);
			// Working directly with the set in the cruise object
			TreeSet<String> cruiseMetaNames = cruise.getMetadataFilenames();
			// Record the metadata documents that will be removed by this change
			cruiseMetaNames.removeAll(metadataNames);
			removedMetaNames.addAll(cruiseMetaNames);
			// Change to the new set of metadata documents for this cruise
			cruiseMetaNames.clear();
			cruiseMetaNames.addAll(metadataNames);
			// Save the updated cruise
			dataStore.getCruiseFileHandler()
					 .saveCruiseToInfoFile(cruise, message + expocode);
		}

		// Build the message for updating metadata with removed cruises
		sb = new StringBuilder();
		first = true;
		for ( String expocode : cruiseExpocodes ) {
			if ( first )
				first = false;
			else
				sb.append(", ");
			sb.append(expocode);
		}
		String cruiseExposStr = sb.toString();

		// Remove associated cruises from the metadata documents that got dropped
		message = "Removed associated cruise(s): " + 
				cruiseExposStr + " from metadata ";
		for ( String metaNames : removedMetaNames ) {
			String expoName = metaNames.split(" (")[0];
			DashboardMetadata mdata = dataStore.getMetadataFileHandler()
											   .getMetadataInfo(expoName);
			if ( mdata == null ) 
				throw new IllegalArgumentException(
						"Metadata properties file for " + expoName + 
						" (a metadata document removed from cruise(s): " + 
						cruiseExposStr + ") does not exist");
			// Work directly with the set in the metadata object
			TreeSet<String> expocodes = mdata.getAssociatedCruises();
			int origSize = expocodes.size();
			expocodes.removeAll(cruiseExpocodes);
			// Save the updated metadata properties if a cruise was removed
			if ( expocodes.size() != origSize )
				dataStore.getMetadataFileHandler().saveMetadataInfo(mdata, 
						message + expoName);
		}

		// Add associated cruises to the metadata documents that were selected
		message = "Added associated cruise(s): " + 
				cruiseExposStr + " to metadata ";
		for ( DashboardMetadata mdata : mdataList.values() ) {
			// Work directly with the set in the metadata object
			TreeSet<String> expocodes = mdata.getAssociatedCruises();
			int origSize = expocodes.size();
			expocodes.addAll(cruiseExpocodes);
			// Save the updated metadata properties if a cruise was added
			if ( expocodes.size() != origSize )
				dataStore.getMetadataFileHandler().saveMetadataInfo(mdata, 
						message + mdata.getExpocodeFilename());
		}
	}

}

	