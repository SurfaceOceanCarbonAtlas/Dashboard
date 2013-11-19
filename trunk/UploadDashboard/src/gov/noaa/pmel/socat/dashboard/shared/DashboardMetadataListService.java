/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.HashSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Server side interface for obtaining a user's list of metadata files.
 * @author Karl Smith
 */
@RemoteServiceRelativePath("metadataListService")
public interface DashboardMetadataListService extends RemoteService {

	/**
	 * After authenticating the user using the given credentials,
	 * returns the user's list of metadata files.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @return
	 * 		the metadata list
	 * @throws IllegalArgumentException
	 * 		if authentication failed
	 */
	DashboardMetadataList getMetadataList(String username, String passhash) 
											throws IllegalArgumentException;

	/**
	 * Associates metadata documents with cruises
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
	 * @throws IllegalArgumentException
	 * 		if authentication failed, if a cruise expocode is invalid, 
	 * 		if a metadata expocode filename is invalid, or if the update failed
	 */
	void associateMetadata(String username, String passhash, 
			HashSet<String> cruiseExpocodes, 
			HashSet<String> metadataExpocodeFilenames) 
										throws IllegalArgumentException;

}
