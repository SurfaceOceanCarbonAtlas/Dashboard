/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList;
import gov.noaa.pmel.socat.dashboard.shared.MetadataListService;

import java.io.IOException;
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
		String omeFilename = cruise.getOmeFilename();
		if ( ! omeFilename.isEmpty() ) {
			mdata = mdataHandler.getMetadataInfo(cruiseExpocode, omeFilename);
			if ( mdata != null ) 
				mdataList.setOmeMetadata(mdata);
		}
		// Get the information for additional documents 
		for ( String mdataName : cruise.getAddlDocNames() ) {
			mdata = mdataHandler.getMetadataInfo(cruiseExpocode, mdataName);
			if ( (mdata != null) && (! mdata.getFilename().equals(omeFilename)) ) 
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

		// Work directly with the set of filenames in the cruise object
		String cruiseOmeFilename = cruise.getOmeFilename();
		TreeSet<String> addlDocNames = cruise.getAddlDocNames(); 
		for ( String mdataName : metadataNames ) {
			if ( cruiseOmeFilename.equals(mdataName) ) {
				cruise.setOmeFilename(null);
				cruiseOmeFilename = "";
				// Should not be in addlDocNames, but just in case
				addlDocNames.remove(mdataName);
			}
			else if ( ! addlDocNames.remove(mdataName) )
				throw new IllegalArgumentException("Document " +
						mdataName + " is not associated with cruise " +
						cruiseExpocode);
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
		DashboardMetadata mdata;
		if ( ! cruiseOmeFilename.isEmpty() ) {
			mdata = mdataHandler.getMetadataInfo(cruiseExpocode, cruiseOmeFilename);
			mdataList.setOmeMetadata(mdata);
		}
		else {
			mdataList.setOmeMetadata(null);
		}
		for ( String mdataName : addlDocNames ) {
			mdata = mdataHandler.getMetadataInfo(cruiseExpocode, mdataName);
			if ( (mdata != null) && (! mdataName.equals(cruiseOmeFilename)) )
				mdataList.put(mdataName, mdata);
		}
		// Return the updated metadata listing
		return mdataList;
	}

}

	