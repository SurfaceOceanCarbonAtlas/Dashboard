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
	 * 		the callback to make with the updated cruise listing;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed, if a dashboard cruise does 
	 * 		not exist for any of the expocodes, or if the addition 
	 * 		of a cruise failed.
	 */
	void addCruisesToSocat(String username, String passhash,
			HashSet<String> cruiseExpocodes, String archiveStatus,
			AsyncCallback<Void> callback);

	/**
	 * Client-side interface for setting the archive status 
	 * for a cruise.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param expocode
	 * 		expocode of cruise to use
	 * @param archiveStatus
	 * 		archive status to assign to the cruise
	 * @param callback
	 * 		the callback to make with the updated cruise listing;
	 * 		the onFailure method of the callback will be called
	 * 		if authentication failed or if the change to the cruise
	 * 		failed.
	 */
	void setCruiseArchiveStatus(String username, String passhash, 
			String expocode, String archiveStatus, 
			AsyncCallback<Void> callback);

}
