/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.TreeSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Server side interface for obtaining a user's list of metadata files.
 * @author Karl Smith
 */
@RemoteServiceRelativePath("MetadataListService")
public interface MetadataListService extends RemoteService {

	/**
	 * After authenticating the user using the given credentials,
	 * returns the metadata documents listing for a cruise.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param cruiseExpocode 
	 * 		get the metadata documents for this cruise
	 * @return
	 * 		the metadata listing for the cruise
	 * @throws IllegalArgumentException
	 * 		if authentication failed 
	 */
	public DashboardMetadataList getMetadataList(String username, String passhash,
			String cruiseExpocode) throws IllegalArgumentException;

	/**
	 * After authenticating the user using the given credentials,
	 * remove (deletes) metadata documents for a cruise.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param cruiseExpocode
	 * 		remove metdata documents from this cruise
	 * @param metadataNames
	 * 		metadata documents to remove
	 * @return
	 * 		the updated metadata listing for the cruise
	 * @throws IllegalArgumentException
	 * 		if authentication failed or if a metadata document in the 
	 * 		given set is not associated with the specified cruise 
	 */
	public DashboardMetadataList removeMetadata(String username, String passhash,
			String cruiseExpcode, TreeSet<String> metadataNames) 
									throws IllegalArgumentException;

}
