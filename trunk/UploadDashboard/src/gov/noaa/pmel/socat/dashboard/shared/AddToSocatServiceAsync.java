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
	 * @param passhash
	 * 		encrypted password to use
	 * @param cruiseExpocodes
	 * 		expocodes of cruises to add to SOCAT
	 * @param archiveStatus
	 * 		archive status to apply to all cruises without a DOI
	 * @param callback
	 * 		the callback to make when complete; the onFailure method 
	 * 		of the callback will be called if authentication failed, 
	 * 		if a dashboard cruise does not exist for any of the 
	 * 		expocodes, or if the addition of a cruise failed.
	 */
	void addCruisesToSocat(String username, String passhash,
			HashSet<String> cruiseExpocodes, String archiveStatus,
			AsyncCallback<Void> callback);

	/**
	 * Client-side interface for changing the archive status
	 * of cruises.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param cruiseExpocodes
	 * 		expocodes of cruises
	 * @param archiveStatus
	 * 		archive status for the cruises
	 * @param localTimestamp
	 * 		client local timestamp of this request 
	 * @param repeatSend
	 * 		if the request is to send to CDIAC ASAP,
	 * 		should cruises already sent be resent?
	 * @param callback
	 * 		the callback to make when complete; the onFailure method 
	 * 		of the callback will be called if authentication failed, 
	 * 		if the dashboard cruise does not exist for the given 
	 * 		expocode, or if the change in cruise archive status failed.
	 */
	void setCruiseArchiveStatus(String username, String passhash,
			HashSet<String> cruiseExpocodes, String archiveStatus, 
			String localTimestamp, boolean repeatSend, 
			AsyncCallback<Void> callback);

}
