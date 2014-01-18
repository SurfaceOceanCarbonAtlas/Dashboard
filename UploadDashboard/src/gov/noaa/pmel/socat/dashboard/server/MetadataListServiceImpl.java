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
		// Assign the name of the OME metadata document
		String omeFilename = cruise.getOmeFilename();
		mdataList.setOmeFilename(omeFilename);
		// Get the information for the OME metadata document and add it to the list
		MetadataFileHandler mdataHandler = dataStore.getMetadataFileHandler();
		DashboardMetadata mdata;
		if ( ! omeFilename.isEmpty() ) {
			mdata = mdataHandler.getMetadataInfo(cruiseExpocode, omeFilename);
			if ( mdata == null ) 
				throw new IllegalArgumentException(
						"Metadata document " + omeFilename + " for cruise " + 
						cruiseExpocode + " does not exist");
			mdataList.put(omeFilename, mdata);
		}
		// Get the information for the other metadata documents and add them to the list
		for ( String mdataName : cruise.getMetadataFilenames() ) {
			mdata = mdataHandler.getMetadataInfo(cruiseExpocode, mdataName);
			if ( mdata == null )
				throw new IllegalArgumentException(
						"Metadata document " + mdataName + " for cruise " + 
						cruiseExpocode + " does not exist");
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
					"No metadata documents given for removal");

		// Get the current metadata documents for the cruise
		DashboardCruise cruise = dataStore.getCruiseFileHandler()
										  .getCruiseFromInfoFile(cruiseExpocode);
		MetadataFileHandler mdataHandler = dataStore.getMetadataFileHandler();

		// Work directly with the set of filenames in the cruise object
		String cruiseOmeFilename = cruise.getOmeFilename();
		TreeSet<String> cruiseMDataNames = cruise.getMetadataFilenames(); 
		for ( String mdataName : metadataNames ) {
			if ( cruiseOmeFilename.equals(mdataName) ) {
				cruise.setOmeFilename(null);
				cruiseOmeFilename = "";
			}
			else if ( ! cruiseMDataNames.remove(mdataName) )
				throw new IllegalArgumentException("Metadata document " +
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
		mdataList.setOmeFilename(cruiseOmeFilename);
		DashboardMetadata mdata;
		if ( ! cruiseOmeFilename.isEmpty() ) {
			mdata = mdataHandler.getMetadataInfo(cruiseExpocode, cruiseOmeFilename);
			if ( mdata == null )
				throw new IllegalArgumentException(
						"Metadata document " + cruiseOmeFilename + " for cruise " + 
						cruiseExpocode + " does not exist");
			mdataList.put(cruiseOmeFilename, mdata);
		}
		for ( String mdataName : cruiseMDataNames ) {
			mdata = mdataHandler.getMetadataInfo(cruiseExpocode, mdataName);
			if ( mdata == null )
				throw new IllegalArgumentException(
						"Metadata document " + mdataName + " for cruise " + 
						cruiseExpocode + " does not exist");
			mdataList.put(mdataName, mdata);
		}
		// Return the updated metadata listing
		return mdataList;
	}

}

	