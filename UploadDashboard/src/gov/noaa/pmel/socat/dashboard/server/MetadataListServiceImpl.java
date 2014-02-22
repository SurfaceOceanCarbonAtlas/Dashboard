/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList;
import gov.noaa.pmel.socat.dashboard.shared.MetadataListService;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the MetadataListService
 * 
 * @author Karl Smith
 */
public class MetadataListServiceImpl extends RemoteServiceServlet
									implements MetadataListService {

	private static final long serialVersionUID = 5809230023905849184L;

	@Override
	public DashboardMetadataList getMetadataList(String username, String passhash, 
			String cruiseExpocode) throws IllegalArgumentException {
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
		// Get the information about the cruise 
		DashboardCruise cruise = dataStore.getCruiseFileHandler()
									.getCruiseFromInfoFile(cruiseExpocode);
		if ( cruise == null )
			throw new IllegalArgumentException(
					"No cruise file for " + cruiseExpocode);
		// Create the metadata listing to be returned
		DashboardMetadataList mdataList = new DashboardMetadataList();
		mdataList.setUsername(username);
		MetadataFileHandler mdataHandler = dataStore.getMetadataFileHandler();
		DashboardMetadata mdata;
		// Get the information for the OME metadata document
		String omeTimestamp = cruise.getOmeTimestamp();
		if ( ! omeTimestamp.isEmpty() ) {
			mdata = mdataHandler.getMetadataInfo(cruiseExpocode, 
												 OmeMetadata.OME_FILENAME);
			if ( mdata != null ) 
				mdataList.setOmeMetadata(mdata);
		}
		// Get the information for additional documents 
		for ( String docTitle : cruise.getAddlDocs() ) {
			// Get the filename from the title string
			String mdataName = DashboardMetadata.splitAddlDocsTitle(docTitle)[0];
			mdata = mdataHandler.getMetadataInfo(cruiseExpocode, mdataName);
			if ( mdata != null ) 
				mdataList.put(mdataName, mdata);
		}
		// Return the metadata listing
		return mdataList;
	}

	@Override
	public DashboardMetadataList removeMetadata(String username, String passhash,
							String cruiseExpocode, TreeSet<String> metadataNames) 
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
		if ( metadataNames.size() < 1 )
			throw new IllegalArgumentException(
					"No documents given for removal");

		// Get the current metadata documents for the cruise
		DashboardCruise cruise = dataStore.getCruiseFileHandler()
										  .getCruiseFromInfoFile(cruiseExpocode);
		MetadataFileHandler mdataHandler = dataStore.getMetadataFileHandler();

		// Directly modify the additional documents list in this cruise
		TreeSet<String> addlDocs = cruise.getAddlDocs();
		// Create a map of additional document names to titles
		HashMap<String,String> addlDocsMap = new HashMap<String,String>();
		for ( String docTitle : addlDocs ) {
			addlDocsMap.put(DashboardMetadata.splitAddlDocsTitle(docTitle)[0], docTitle);
		}
		for ( String mdataName : metadataNames ) {
			if ( OmeMetadata.OME_FILENAME.equals(mdataName) ) {
				// No more OME metadata for this cruise
				cruise.setOmeTimestamp(null);
			}
			else {
				// Remove this additional document from this cruise
				String docTitle = addlDocsMap.get(mdataName);
				if ( (docTitle == null) || ! addlDocs.remove(docTitle) )
					throw new IllegalArgumentException("Document " +
							mdataName + " is not associated with cruise " +
							cruiseExpocode);
				// Remove it from the names to titles map as well
				addlDocsMap.remove(mdataName);
			}
			// Delete this OME metadata or additional documents file on the server
			mdataHandler.removeMetadata(username, cruiseExpocode, mdataName);
		}

		// Create the commit message
		StringBuilder sb = new StringBuilder();
		sb.append("Removed metadata document(s): ");
		boolean first = true;
		for ( String name : metadataNames ) {
			if ( first )
				first = false;
			else
				sb.append(", ");
			sb.append(name);
		}
		sb.append(" from cruise ");
		sb.append(cruiseExpocode);
		String message = sb.toString();

		// Save the updated cruise
		dataStore.getCruiseFileHandler()
				 .saveCruiseInfoToFile(cruise, message);

		// Create the updated metadata listing for the cruise
		DashboardMetadataList mdataList = new DashboardMetadataList();
		mdataList.setUsername(username);
		// Assign the OME file if it is present
		if ( ! cruise.getOmeTimestamp().isEmpty() ) {
			DashboardMetadata mdata = mdataHandler.getMetadataInfo(
					cruiseExpocode, OmeMetadata.OME_FILENAME);
			mdataList.setOmeMetadata(mdata);
		}
		// Add any remaining additional documents
		for ( String mdataName : addlDocsMap.keySet() ) {
			DashboardMetadata mdata = mdataHandler.getMetadataInfo(
					cruiseExpocode, mdataName);
			if ( mdata != null )
				mdataList.put(mdataName, mdata);
		}
		// Return the updated metadata listing
		return mdataList;
	}

}

	