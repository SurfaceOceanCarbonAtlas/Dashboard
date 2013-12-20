/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.TreeSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Client side interface for obtaining a user's list of metadata files.
 * @author Karl Smith
 */
public interface MetadataListServiceAsync {

	/**
	 * Client side request to get the list of metadata documents for a cruise. 
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param cruiseExpocode 
	 * 		get the metadata documents for this cruise
	 * @param callback
	 * 		the callback to make with the metadata list;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed
	 */
	void getMetadataList(String username, String passhash,
			String cruiseExpocode, 
			AsyncCallback<DashboardMetadataList> callback);

	/**
	 * Client side request to remove (delete) metadata documents 
	 * from a cruise. 
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param cruiseExpocode
	 * 		remove metdata documents from this cruise
	 * @param metadataNames
	 * 		metadata documents to remove
	 * @param callback
	 * 		the callback to make with the updated metadata list;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed or if a metadata document in
	 * 		the given list is not associated with the specified
	 * 		cruise
	 */
	void removeMetadata(String username, String passhash,
			String cruiseExpocode, TreeSet<String> metadataNames, 
			AsyncCallback<DashboardMetadataList> callback);

}
