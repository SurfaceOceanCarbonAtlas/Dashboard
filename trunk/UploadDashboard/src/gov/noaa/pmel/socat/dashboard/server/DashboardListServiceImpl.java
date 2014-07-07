/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardListService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage;
import gov.noaa.pmel.socat.dashboard.shared.SCMessageList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of DashboardListService
 * @author Karl Smith
 */
public class DashboardListServiceImpl extends RemoteServiceServlet
									implements DashboardListService {

	private static final long serialVersionUID = -104314479885469162L;

	/**
	 * Validates the given user.
	 * 
	 * @param username
	 * 		name of user making this request
	 * @param passhash
	 * 		encrypted password to use
	 * @return
	 * 		the dashboard data store
	 * @throws IllegalArgumentException
	 * 		if authentication fails
	 */
	private DashboardDataStore validateUser(String username,
			String passhash) throws IllegalArgumentException {
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected configuration error: " + ex.getMessage());
		}
		if ( ! dataStore.validateUser(username, passhash) )
			throw new IllegalArgumentException(
					"Invalid authentication credentials");
		return dataStore;
	}

	@Override
	public boolean logoutUser(String username, String passhash) {
		// Validate and get the dashboard data store
		validateUser(username, passhash);
		getServletContext().removeAttribute("JSESSIONID");
		Logger.getLogger("CruiseListService").info("logged out " + username);
		return true;
	}

	@Override
	public DashboardCruiseList getCruiseList(String username, String passhash)
			throws IllegalArgumentException {
		// Validate and get the dashboard data store
		DashboardDataStore dataStore = validateUser(username, passhash);
		Logger.getLogger("CruiseListService").info("cruise list returned for " + username);
		// Return the current list of cruises for this user
		return dataStore.getUserFileHandler().getCruiseListing(username);
	}

	@Override
	public DashboardCruiseList deleteCruises(String username, String passhash,
			TreeSet<String> expocodeSet, Boolean deleteMetadata) 
											throws IllegalArgumentException {
		// Validate and get the dashboard data store
		DashboardDataStore dataStore = validateUser(username, passhash);
		// Delete each of the cruises in the given set
		for ( String expocode : expocodeSet ) {
			try {
				dataStore.getCruiseFileHandler()
						 .deleteCruiseFiles(expocode, username, deleteMetadata);

			} catch (FileNotFoundException ex) {
				// Cruise already deleted? - ignore
				;
			}
			// IllegalArgumentException for other problems escape as-is
		}

		// Return the current list of cruises, which should 
		// detect the missing cruises and update itself
		return dataStore.getUserFileHandler().getCruiseListing(username);
	}

	@Override
	public DashboardCruiseList addCruisesToList(String username,
			String passhash, String wildExpocode) throws IllegalArgumentException {
		// Validate and get the dashboard data store
		DashboardDataStore dataStore = validateUser(username, passhash);
		// Add the cruises to the user's list and return the updated list
		return dataStore.getUserFileHandler()
						.addCruisesToListing(wildExpocode, username);
	}

	@Override
	public DashboardCruiseList removeCruisesFromList(String username,
			String passhash, TreeSet<String> expocodeSet)
			throws IllegalArgumentException {
		// Validate and get the dashboard data store
		DashboardDataStore dataStore = validateUser(username, passhash);
		// Remove the cruises from the user's list and return the updated list
		return dataStore.getUserFileHandler()
						.removeCruisesFromListing(expocodeSet, username);
	}

	@Override
	public HashSet<DashboardCruise> getUpdatedCruises(String username,
			String passhash, TreeSet<String> expocodeSet)
			throws IllegalArgumentException {
		// Validate and get the dashboard data store
		DashboardDataStore dataStore = validateUser(username, passhash);
		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		// Create the set of updated cruise information to return
		HashSet<DashboardCruise> cruiseSet = 
				new HashSet<DashboardCruise>(expocodeSet.size());
		for ( String cruiseExpocode : expocodeSet ) {
			cruiseSet.add(cruiseHandler.getCruiseFromInfoFile(cruiseExpocode));
		}
		return cruiseSet;
	}

	@Override
	public HashSet<DashboardCruise> deleteAddlDoc(String username,
			String passhash, String deleteFilename, String expocode,
			TreeSet<String> allExpocodes) throws IllegalArgumentException {
		// Validate and get the dashboard data store
		DashboardDataStore dataStore = validateUser(username, passhash);
		// Get the current metadata documents for the cruise
		DashboardCruise cruise = dataStore.getCruiseFileHandler()
										  .getCruiseFromInfoFile(expocode);
		MetadataFileHandler mdataHandler = dataStore.getMetadataFileHandler();

		if ( DashboardMetadata.OME_FILENAME.equals(deleteFilename) ) {
			// No more OME metadata for this cruise
			cruise.setOmeTimestamp(null);
		}
		else {
			// Directly modify the additional documents list in this cruise
			TreeSet<String> addlDocs = cruise.getAddlDocs();
			// Find this additional document for this cruise
			String titleToRemove = null;
			for ( String docTitle : addlDocs ) {
				String name = DashboardMetadata.splitAddlDocsTitle(docTitle)[0];
				if ( name.equals(deleteFilename) ) {
					titleToRemove = docTitle;
					break;
				}
			}
			if ( (titleToRemove == null) || ! addlDocs.remove(titleToRemove) )
				throw new IllegalArgumentException("Document " + deleteFilename + 
						" is not associated with dataset " + expocode);
		}
		// Delete this OME metadata or additional documents file on the server
		mdataHandler.removeMetadata(username, expocode, deleteFilename);

		// Save the updated cruise
		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		cruiseHandler.saveCruiseInfoToFile(cruise, "Removed metadata document " + 
									deleteFilename + " from cruise " + expocode);

		// Create the set of updated cruise information to return
		HashSet<DashboardCruise> cruiseSet = 
				new HashSet<DashboardCruise>(allExpocodes.size());
		for ( String cruiseExpocode : allExpocodes ) {
			cruiseSet.add(cruiseHandler.getCruiseFromInfoFile(cruiseExpocode));
		}
		return cruiseSet;
	}

	@Override
	public SCMessageList getDataMessages(String username, String passhash,
			String expocode) throws IllegalArgumentException {
		// Validate and get the dashboard data store
		DashboardDataStore dataStore = validateUser(username, passhash);
		// Get the list of saved sanity checker Message objects for this cruise
		ArrayList<SCMessage> cruiseMsgs;
		try {
			cruiseMsgs = dataStore.getCruiseFileHandler()
								  .getCruiseMessages(expocode);
		} catch (FileNotFoundException ex) {
			throw new IllegalArgumentException("The sanity checker " +
					"has never been run on cruise " + expocode);
		}

		// Create the SCMessageList set of data messages for passing to the client 
		SCMessageList scMsgList = new SCMessageList();
		scMsgList.setUsername(username);
		scMsgList.setExpocode(expocode);
		scMsgList.addAll(cruiseMsgs);

		return scMsgList;
	}

}
