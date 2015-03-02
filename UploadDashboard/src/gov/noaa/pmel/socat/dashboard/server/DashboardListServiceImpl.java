/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardListService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SCMessageList;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of DashboardListService
 * @author Karl Smith
 */
public class DashboardListServiceImpl extends RemoteServiceServlet
									implements DashboardListService {

	private static final long serialVersionUID = 5135385626064223202L;

	private String username = null;
	private DashboardDataStore dataStore = null;

	/**
	 * Validates the given request by retrieving the current username from the request.
	 * Assigns the username and dataStore fields in this instance.
	 * 
	 * @return
	 * 		true if the request obtained a valid username; otherwise false
	 * @throws IllegalArgumentException
	 * 		if unable to obtain the dashboard data store
	 */
	private boolean validateRequest() throws IllegalArgumentException {
		username = null;
		dataStore = null;
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unexpected configuration error: " + ex.getMessage());
		}
		HttpServletRequest request = getThreadLocalRequest();
		try {
			username = request.getUserPrincipal().getName().trim();
		} catch (Exception ex) {
			// Probably null pointer exception - leave username null
			return false;
		}
		return dataStore.validateUser(username);
	}

	@Override
	public DashboardCruiseList getCruiseList() throws IllegalArgumentException {
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		DashboardCruiseList cruiseList = dataStore.getUserFileHandler().getCruiseListing(username);
		Logger.getLogger("CruiseListService").info("cruise list returned for " + username);
		return cruiseList;
	}

	@Override
	public void logoutUser() {
		HttpServletRequest request = getThreadLocalRequest();
		username = null;
		try {
			username = request.getUserPrincipal().getName().trim();
		} catch (Exception ex) {
			// Probably null pointer exception - leave username null
		}
		HttpSession session = request.getSession(false);
		try {
			session.invalidate();
		} catch ( Exception ex ) {
			// Log but otherwise ignore this error
			Logger.getLogger("CruiseListService").error("session.invalidate failed: " + ex.getMessage());
		}
		try {
			request.logout();
		} catch ( Exception ex ) {
			Logger.getLogger("CruiseListService").error("request.logout failed: " + ex.getMessage());
		}
		Logger.getLogger("CruiseListService").info("logged out " + username);
		username = null;
	}

	@Override
	public DashboardCruiseList deleteCruises(String pageUsername, 
			TreeSet<String> expocodeSet, Boolean deleteMetadata) 
					throws IllegalArgumentException {
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		// Check that the username matches that which was displayed on the page
		if ( ! username.equals(pageUsername) )
			throw new IllegalArgumentException("Invalid user request");
		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		// Delete each of the cruises in the given set
		for ( String expocode : expocodeSet ) {
			try {
				cruiseHandler.deleteCruiseFiles(expocode, username, deleteMetadata);
			} catch (FileNotFoundException ex) {
				// Cruise already deleted? - ignore
				;
			}
			// IllegalArgumentException for other problems escape as-is
			Logger.getLogger("CruiseListService").info("cruise " + expocode + " deleted by " + username);
		}

		// Return the current list of cruises, which should 
		// detect the missing cruises and update itself
		DashboardCruiseList cruiseList = dataStore.getUserFileHandler().getCruiseListing(username);
		Logger.getLogger("CruiseListService").info("cruise list returned for " + username);
		return cruiseList;
	}

	@Override
	public DashboardCruiseList addCruisesToList(String pageUsername, 
			String wildExpocode) throws IllegalArgumentException {
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		// Check that the username matches that which was displayed on the page
		if ( ! username.equals(pageUsername) )
			throw new IllegalArgumentException("Invalid user request");
		// Add the cruises to the user's list and return the updated list
		DashboardCruiseList cruiseList = dataStore.getUserFileHandler()
				.addCruisesToListing(wildExpocode, username);
		Logger.getLogger("CruiseListService").info("added cruises " + wildExpocode + " for " + username);
		return cruiseList;
	}

	@Override
	public DashboardCruiseList removeCruisesFromList(String pageUsername,
			TreeSet<String> expocodeSet) throws IllegalArgumentException {
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		// Check that the username matches that which was displayed on the page
		if ( ! username.equals(pageUsername) )
			throw new IllegalArgumentException("Invalid user request");
		// Remove the cruises from the user's list and return the updated list
		DashboardCruiseList cruiseList = dataStore.getUserFileHandler()
				.removeCruisesFromListing(expocodeSet, username);
		Logger.getLogger("CruiseListService").info("removed cruises " + expocodeSet.toString() + " for " + username);
		return cruiseList;
	}

	@Override
	public DashboardCruiseList getUpdatedCruises(String pageUsername, TreeSet<String> expocodeSet)
			throws IllegalArgumentException {
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		// Check that the username matches that which was displayed on the page
		if ( ! username.equals(pageUsername) )
			throw new IllegalArgumentException("Invalid user request");
		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		// Create the set of updated cruise information to return
		DashboardCruiseList cruiseList = new DashboardCruiseList();
		cruiseList.setUsername(username);
		for ( String cruiseExpocode : expocodeSet ) {
			cruiseList.put(cruiseExpocode, cruiseHandler.getCruiseFromInfoFile(cruiseExpocode));
		}
		Logger.getLogger("CruiseListService").info("returned updated cruise information for " + username);
		return cruiseList;
	}

	@Override
	public DashboardCruiseList deleteAddlDoc(String pageUsername, String deleteFilename, 
			String expocode, TreeSet<String> allExpocodes) throws IllegalArgumentException {
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		// Check that the username matches that which was displayed on the page
		if ( ! username.equals(pageUsername) )
			throw new IllegalArgumentException("Invalid user request");
		// Get the current metadata documents for the cruise
		DashboardCruise cruise = dataStore.getCruiseFileHandler()
										  .getCruiseFromInfoFile(expocode);
		MetadataFileHandler mdataHandler = dataStore.getMetadataFileHandler();

		if ( DashboardMetadata.OME_FILENAME.equals(deleteFilename) ) {
			if ( ! Boolean.TRUE.equals(cruise.isEditable()) ) 
				throw new IllegalArgumentException("Cannot delete the OME metadata for a submitted cruise");
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

		Logger.getLogger("CruiseListService").info("deleted metadata " + deleteFilename + 
				" from " + expocode + " for " + username);

		// Save the updated cruise
		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		cruiseHandler.saveCruiseInfoToFile(cruise, "Removed metadata document " + 
									deleteFilename + " from cruise " + expocode);
		// If submitted cruise, add QC update ('U') global flag about removal of metadata
		if ( ! Boolean.TRUE.equals(cruise.isEditable()) ) {
			SocatQCEvent qcEvent = new SocatQCEvent();
			qcEvent.setExpocode(expocode);
			qcEvent.setFlag(SocatQCEvent.QC_UPDATED_FLAG);
			qcEvent.setFlagDate(new Date());
			qcEvent.setRegionID(DataLocation.GLOBAL_REGION_ID);
			qcEvent.setSocatVersion(dataStore.getSocatUploadVersion());
			qcEvent.setUsername(username);
			String comment = "Deleted metadata file \"" + deleteFilename + 
					"\".  Data and WOCE flags were not changed.";
			qcEvent.setComment(comment);
			try {
				// Add the 'U' QC flag
				dataStore.getDatabaseRequestHandler().addQCEvent(qcEvent);
				dataStore.getDsgNcFileHandler().updateQCFlag(qcEvent);
				// Update the dashboard status for the 'U' QC flag
				cruise.setQcStatus(SocatQCEvent.QC_STATUS_SUBMITTED);
				if ( cruise.isEditable() == null ) {
					cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_WITH_SOCAT);
				}
				dataStore.getCruiseFileHandler().saveCruiseInfoToFile(cruise, comment);
				Logger.getLogger("CruiseListService").info("updated QC status for " + expocode);
			} catch (Exception ex) {
				// Should not fail.  If does, record but otherwise ignore the failure.
				Logger.getLogger("CruiseListService").error("failed to update QC status for " + 
						expocode + " after deleting metadata " + deleteFilename + 
						" from " + expocode + " for " + username + ": " + ex.getMessage());
			}
		}

		// Create the set of updated cruise information to return
		DashboardCruiseList cruiseList = new DashboardCruiseList();
		for ( String cruiseExpocode : allExpocodes ) {
			cruiseList.put(cruiseExpocode, cruiseHandler.getCruiseFromInfoFile(cruiseExpocode));
		}
		Logger.getLogger("CruiseListService").info("returned updated cruise information for " + username);
		return cruiseList;
	}

	@Override
	public SCMessageList getDataMessages(String pageUsername, String expocode) 
			throws IllegalArgumentException {
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		// Check that the username matches that which was displayed on the page
		if ( ! username.equals(pageUsername) )
			throw new IllegalArgumentException("Invalid user request");
		// Get the list of saved sanity checker Message objects for this cruise
		SCMessageList scMsgList;
		try {
			scMsgList = dataStore.getCheckerMsgHandler().getCruiseMessages(expocode);
		} catch (FileNotFoundException ex) {
			throw new IllegalArgumentException("The sanity checker has never been run on cruise " + expocode);
		}
		scMsgList.setUsername(username);
		Logger.getLogger("CruiseListService").info("returned sanity checker messages for " + expocode + " for " + username);
		return scMsgList;
	}

}
