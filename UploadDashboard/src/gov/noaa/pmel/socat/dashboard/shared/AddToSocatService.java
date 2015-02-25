/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.HashSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Server side interface for adding and modifying cruises in SOCAT 
 * from the dashboard.
 * 
 * @author Karl Smith
 */
@RemoteServiceRelativePath("AddToSocatService")
public interface AddToSocatService extends RemoteService {

	/**
	 * Adds cruises named in the given listing to the SOCAT database.
	 * 
	 * @param username
	 * 		name of user making this request - for validation
	 * @param cruiseExpocodes
	 * 		expocodes of cruises to add to SOCAT
	 * @param archiveStatus
	 * 		archive status to apply to all cruises without a DOI
	 * @param localTimestamp
	 * 		client local timestamp of this request 
	 * @param repeatSend
	 * 		if the request is to send to CDIAC ASAP,
	 * 		should cruises already sent be resent?
	 * @throws IllegalArgumentException
	 * 		if authentication failed, if the dashboard cruise does 
	 * 		not exist for any of the given expocodes, or if adding 
	 * 		the cruise data failed
	 */
	void addCruisesToSocat(String username, HashSet<String> cruiseExpocodes, 
			String archiveStatus, String localTimestamp, boolean repeatSend)
					throws IllegalArgumentException;

}
