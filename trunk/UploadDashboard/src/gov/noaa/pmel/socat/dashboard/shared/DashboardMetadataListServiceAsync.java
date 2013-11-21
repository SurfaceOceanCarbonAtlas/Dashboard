/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.HashSet;
import java.util.TreeSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Client side interface for obtaining a user's list of metadata files.
 * @author Karl Smith
 */
public interface DashboardMetadataListServiceAsync {

	/**
	 * Client side request to get the user's list of metadata files. 
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param callback
	 * 		the callback to make with the metadata list;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed
	 */
	void getMetadataList(String username, String passhash,
			AsyncCallback<DashboardMetadataList> callback);

	/**
	 * Client side request to associate metadata documents with cruises
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param cruiseExpocodes
	 * 		associate metadata documents to the cruises with these expocodes
	 * @param metadataExpocodeFilenames
	 * 		expocode filenames of the metadata documents to associate 
	 * 		with the cruises
	 * @param callback
	 * 		the callback to make when done;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed, if a cruise expocode is invalid, 
	 * 		if a metadata expocode filename is invalid, or if the update failed
	 */
	void associateMetadata(String username, String passhash,
			TreeSet<String> cruiseExpocodes,
			HashSet<String> metadataExpocodeFilenames,
			AsyncCallback<Void> callback);

}
