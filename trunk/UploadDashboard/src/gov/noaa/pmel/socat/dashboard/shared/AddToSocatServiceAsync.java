/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.HashSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Client side interface for adding and modifying cruises in SOCAT 
 * from the dashboard.
 * 
 * @author Karl Smith
 */
public interface AddToSocatServiceAsync {

	/**
	 * Client-side interface for adding a listing of cruises 
	 * to the SOCAT database.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param cruiseExpocodes
	 * 		expocodes of cruises to add to SOCAT
	 * @param archiveStatus
	 * 		archive status to apply to all cruises without a DOI
	 * @param localTimestamp
	 * 		client local timestamp of this request 
	 * @param repeatSend
	 * 		if the request is to send to CDIAC ASAP,
	 * 		should cruises already sent be resent?
	 * @param callback
	 * 		the callback to make when complete; the onFailure method 
	 * 		of the callback will be called if authentication failed, 
	 * 		if a dashboard cruise does not exist for any of the 
	 * 		expocodes, or if the addition of a cruise or change in
	 * 		archive status failed.
	 */
	void addCruisesToSocat(String username, HashSet<String> cruiseExpocodes, 
			String archiveStatus, String localTimestamp, boolean repeatSend,
			AsyncCallback<Void> callback);

}
