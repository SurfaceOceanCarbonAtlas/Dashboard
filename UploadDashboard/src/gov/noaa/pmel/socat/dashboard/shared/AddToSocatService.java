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
	 * After authenticating the user using the given credentials,
	 * adds cruises named in the given listing to the SOCAT database.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param cruiseExpocodes
	 * 		expocodes of cruises to add to SOCAT
	 * @param archiveStatus
	 * 		archive status to apply to all cruises without a DOI
	 * @return
	 * 		updated dashboard listing of all cruises for the user
	 * @throws IllegalArgumentException
	 * 		if authentication failed, if the dashboard cruise does 
	 * 		not exist for any of the given expocodes, or if adding 
	 * 		the cruise data failed
	 */
	void addCruisesToSocat(String username, String passhash, 
			HashSet<String> cruiseExpocodes, String archiveStatus) 
					throws IllegalArgumentException;

	/**
	 * After authenticating the user using the given credentials,
	 * changes the archive status for a cruise.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @param expocode
	 * 		expocode of cruise
	 * @param archiveStatus
	 * 		archive status for the cruise
	 */
	void setCruiseArchiveStatus(String username, String passhash,
			String expocode, String archiveStatus);

}
