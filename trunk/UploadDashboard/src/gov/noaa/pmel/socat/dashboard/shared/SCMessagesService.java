/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Server-side interface for working with SanityChecker messages.
 * 
 * @author Karl Smith
 */
@RemoteServiceRelativePath("SCMessagesService")
public interface SCMessagesService extends RemoteService {

	/**
	 * After validating the user, returns the set of sanity checker 
	 * data messages for a given cruise.
	 * 
	 * @param username
	 * 		name of the user making the request
	 * @param passhash
	 * 		password hash of the user making the request
	 * @param cruiseExpocode
	 * 		get data messages for the cruise with this expocode 
	 * @return
	 * 		list of data messages for the cruise; never null, but may be empty 
	 * 		if the SanityChecker did not generate any data messages. 
	 * @throws IllegalArgumentException
	 * 		if authentication fails, 
	 * 		if the cruise expocode is invalid, or
	 * 		if the SanityChecker has never been run on this cruise.
	 */
	SCMessageList getDataMessages(String username, String passhash, 
			String cruiseExpocode) throws IllegalArgumentException;

}
