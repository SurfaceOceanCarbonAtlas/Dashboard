/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Client-side interface for working with SanityChecker messages.
 * 
 * @author Karl Smith
 */
public interface SCMessagesServiceAsync {

	/**
	 * After validating the user, returns a SCMessageList of sanity checker 
	 * data messages for a given cruise.
	 * 
	 * @param username
	 * 		name of the user making the request
	 * @param passhash
	 * 		password hash of the user making the request
	 * @param cruiseExpocode
	 * 		get data messages for the cruise with this expocode 
	 * @param callback
	 * 		callback to make with the list of data messages for the cruise. 
	 * 		The SCMessageList provided in the onSuccess method may be empty 
	 * 		if the SanityChecker did not generate any data messages.  
	 * 		The onFailure method of the callback will be called 
	 * 		if authentication fails, if the cruise expocode is invalid, or 
	 * 		if the SanityChecker has never been run on this cruise.
	 */
	void getDataMessages(String username, String passhash,
			String cruiseExpocode, AsyncCallback<SCMessageList> callback);

}
