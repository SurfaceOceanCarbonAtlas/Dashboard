/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.actions.DashboardCruiseChecker;
import gov.noaa.pmel.socat.dashboard.actions.DashboardCruiseSubmitter;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.UserFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardServicesInterface;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SCMessageList;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of DashboardServicesInterface
 * 
 * @author Karl Smith
 */
public class DashboardServices extends RemoteServiceServlet
								implements DashboardServicesInterface {

	private static final long serialVersionUID = 1832877865268819150L;

	private String username = null;
	private DashboardDataStore dataStore = null;

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
	}

	/**
	 * Validates the given request by retrieving the current username from the request
	 * and verifying that username with the Dashboard data store.  If pageUsername is
	 * given, also checks these usernames are the same.
	 * Assigns the username and dataStore fields in this instance.
	 * 
	 * @param pageUsername
	 * 		if not null, check that this matches the current page username
	 * @return
	 * 		true if the request obtained a valid username; otherwise false
	 * @throws IllegalArgumentException
	 * 		if unable to obtain the dashboard data store
	 */
	private boolean validateRequest(String pageUsername) throws IllegalArgumentException {
		username = null;
		HttpServletRequest request = getThreadLocalRequest();
		try {
			username = request.getUserPrincipal().getName().trim();
		} catch (Exception ex) {
			// Probably null pointer exception
			return false;
		}
		if ( (pageUsername != null) && ! pageUsername.equals(username) )
			return false;

		dataStore = null;
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unexpected configuration error: " + ex.getMessage());
		}
		return dataStore.validateUser(username);
	}

	@Override
	public DashboardCruiseList getCruiseList() throws IllegalArgumentException {
		// Get the dashboard data store and current username
		if ( ! validateRequest(null) ) 
			throw new IllegalArgumentException("Invalid user request");
		DashboardCruiseList cruiseList = dataStore.getUserFileHandler().getCruiseListing(username);
		Logger.getLogger("CruiseListService").info("cruise list returned for " + username);
		return cruiseList;
	}

	@Override
	public DashboardCruiseList deleteCruises(String pageUsername, 
			TreeSet<String> expocodeSet, Boolean deleteMetadata) 
					throws IllegalArgumentException {
		// Get the dashboard data store and current username, and validate that username
		if ( ! validateRequest(pageUsername) ) 
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
		// Get the dashboard data store and current username, and validate that username
		if ( ! validateRequest(pageUsername) ) 
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
		// Get the dashboard data store and current username, and validate that username
		if ( ! validateRequest(pageUsername) ) 
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
		// Get the dashboard data store and current username, and validate that username
		if ( ! validateRequest(pageUsername) ) 
			throw new IllegalArgumentException("Invalid user request");

		// Create the set of updated cruise information to return
		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		DashboardCruiseList cruiseList = new DashboardCruiseList();
		cruiseList.setUsername(username);
		cruiseList.setSocatVersion(dataStore.getSocatUploadVersion());
		cruiseList.setManager(dataStore.isManager(username));
		for ( String cruiseExpocode : expocodeSet ) {
			cruiseList.put(cruiseExpocode, cruiseHandler.getCruiseFromInfoFile(cruiseExpocode));
		}
		Logger.getLogger("CruiseListService").info("returned updated cruise information for " + username);
		return cruiseList;
	}

	@Override
	public DashboardCruiseList deleteAddlDoc(String pageUsername, String deleteFilename, 
			String expocode, TreeSet<String> allExpocodes) throws IllegalArgumentException {
		// Get the dashboard data store and current username, and validate that username
		if ( ! validateRequest(pageUsername) ) 
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
		cruiseList.setUsername(username);
		cruiseList.setSocatVersion(dataStore.getSocatUploadVersion());
		cruiseList.setManager(dataStore.isManager(username));
		for ( String cruiseExpocode : allExpocodes ) {
			cruiseList.put(cruiseExpocode, cruiseHandler.getCruiseFromInfoFile(cruiseExpocode));
		}
		Logger.getLogger("CruiseListService").info("returned updated cruise information for " + username);
		return cruiseList;
	}

	@Override
	public DashboardCruiseWithData getCruiseDataColumnSpecs(String pageUsername,
			String expocode) throws IllegalArgumentException {
		// Get the dashboard data store and current username, and validate that username
		if ( ! validateRequest(pageUsername) ) 
			throw new IllegalArgumentException("Invalid user request");

		// Get the cruise with the first maximum-needed number of rows
		DashboardCruiseWithData cruiseData = dataStore.getCruiseFileHandler()
				.getCruiseDataFromFiles(expocode, 0, 
						DashboardUtils.MAX_ROWS_PER_GRID_PAGE);
		if ( cruiseData == null )
			throw new IllegalArgumentException("cruise " + expocode + " does not exist");

		// Remove any metadata preamble to reduced data transmitted
		cruiseData.getPreamble().clear();

		Logger.getLogger("DataSpecsService").info("cruise data columns specs returned for " + 
				expocode + " for " + username);
		// Return the cruise with the partial data
		return cruiseData;
	}

	@Override
	public ArrayList<ArrayList<String>> getCruiseData(String pageUsername, String expocode, 
			int firstRow, int numRows) throws IllegalArgumentException {
		// Get the dashboard data store and current username, and validate that username
		if ( ! validateRequest(pageUsername) ) 
			throw new IllegalArgumentException("Invalid user request");

		// Get the cruise data with exactly the data rows desired
		DashboardCruiseWithData cruiseWithData = dataStore.getCruiseFileHandler()
									.getCruiseDataFromFiles(expocode, firstRow, numRows);
		if ( cruiseWithData == null )
			throw new IllegalArgumentException("cruise " + expocode + " does not exist");
		ArrayList<ArrayList<String>> cruiseDataRows = cruiseWithData.getDataValues();
		if ( cruiseDataRows.size() != numRows )
			throw new IllegalArgumentException("invalid requested row numbers: " + 
					firstRow + " - " + (firstRow+numRows-1));
		Logger.getLogger("DataSpecsService").info("cruise data " + Integer.toString(firstRow) + 
				" - " + Integer.toString(firstRow+numRows-1) + " returned for " + 
				expocode + " for " + username);
		return cruiseDataRows;
	}

	@Override
	public DashboardCruiseWithData updateCruiseDataColumnSpecs(String pageUsername,
			DashboardCruise newSpecs) throws IllegalArgumentException {
		// Get the dashboard data store and current username, and validate that username
		if ( ! validateRequest(pageUsername) ) 
			throw new IllegalArgumentException("Invalid user request");

		// Retrieve all the current cruise data
		DashboardCruiseWithData cruiseData = dataStore.getCruiseFileHandler()
						.getCruiseDataFromFiles(newSpecs.getExpocode(), 0, -1);
		// Revise the cruise data column types and units 
		if ( newSpecs.getDataColTypes().size() != cruiseData.getDataColTypes().size() )
			throw new IllegalArgumentException("Unexpected number of data columns (" +
					newSpecs.getDataColTypes().size() + " instead of " + 
					cruiseData.getDataColTypes().size());
		cruiseData.setDataColTypes(newSpecs.getDataColTypes());
		cruiseData.setDataColUnits(newSpecs.getDataColUnits());
		cruiseData.setMissingValues(newSpecs.getMissingValues());

		// Run the SanityCheck on the updated cruise.
		// Assigns the data check status and the WOCE-3 and WOCE-4 data flags.
		dataStore.getDashboardCruiseChecker().checkCruise(cruiseData);

		// Save and commit the updated cruise columns
		dataStore.getCruiseFileHandler().saveCruiseInfoToFile(cruiseData, 
				"Cruise data column types, units, and missing values for " + 
				cruiseData.getExpocode() + " updated by " + username);
		// Update the user-specific data column names to types, units, and missing values 
		dataStore.getUserFileHandler().updateUserDataColumnTypes(cruiseData, username);
		
		// Remove all but the first maximum-needed number of rows of cruise data 
		// to minimize the payload of the returned cruise data
		int numRows = cruiseData.getNumDataRows();
		if ( numRows > DashboardUtils.MAX_ROWS_PER_GRID_PAGE )
			cruiseData.getDataValues()
					  .subList(DashboardUtils.MAX_ROWS_PER_GRID_PAGE, numRows)
					  .clear();

		Logger.getLogger("DataSpecsService").info("cruise data columns specs updated for " + 
				cruiseData.getExpocode() + " by " + username);
		// Return the updated truncated cruise data for redisplay 
		// in the DataColumnSpecsPage
		return cruiseData;
	}

	@Override
	public void updateCruiseDataColumns(String pageUsername, 
			ArrayList<String> cruiseExpocodes) throws IllegalArgumentException {
		// Get the dashboard data store and current username, and validate that username
		if ( ! validateRequest(pageUsername) ) 
			throw new IllegalArgumentException("Invalid user request");

		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		UserFileHandler userHandler = dataStore.getUserFileHandler();
		DashboardCruiseChecker cruiseChecker = dataStore.getDashboardCruiseChecker();
		Logger dataSpecsLogger = Logger.getLogger("DataSpecsService");

		for ( String expocode : cruiseExpocodes ) {
			try {
				// Retrieve all the current cruise data
				DashboardCruiseWithData cruiseData = cruiseHandler.getCruiseDataFromFiles(expocode, 0, -1);

				// Identify the columns from stored names-to-types for this user
				userHandler.assignDataColumnTypes(cruiseData);
				// Save and commit these column assignments in case the sanity checker has problems
				cruiseHandler.saveCruiseInfoToFile(cruiseData, "Column types for " + expocode + 
						" updated by " + username + " from post-processing a multiple-dataset upload");
			
				// Run the SanityCheck on the updated cruise.  Saves the SanityChecker messages,
				// and assigns the data check status and the WOCE-3 and WOCE-4 data flags.
				cruiseChecker.checkCruise(cruiseData);

				// Save and commit the updated cruise information
				cruiseHandler.saveCruiseInfoToFile(cruiseData, "Data status and WOCE flags for " + expocode + 
						" updated by " + username + " from post-processing a multiple-dataset upload");
				dataSpecsLogger.info("Updated data column specs for " + expocode + " for " + username);
			} catch (Exception ex) {
				// ignore problems (such as unidentified columns)
				dataSpecsLogger.error("Unable to update data column specs for " + expocode + ": " + ex.getMessage());
				continue;
			}
		}
	}

	@Override
	public SCMessageList getDataMessages(String pageUsername, String expocode) 
			throws IllegalArgumentException {
		// Get the dashboard data store and current username, and validate that username
		if ( ! validateRequest(pageUsername) ) 
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

	@Override
	public void submitCruiseForQC(String pageUsername, HashSet<String> cruiseExpocodes, 
			String archiveStatus, String localTimestamp, boolean repeatSend) 
					throws IllegalArgumentException {
		// Get the dashboard data store and current username, and validate that username
		if ( ! validateRequest(pageUsername) ) 
			throw new IllegalArgumentException("Invalid user request");

		// Submit the cruises for QC and possibly send to CDIAC
		DashboardCruiseSubmitter submitter = new DashboardCruiseSubmitter(dataStore);
		submitter.submitCruises(cruiseExpocodes, archiveStatus, 
								localTimestamp, repeatSend, username);
		Logger.getLogger("AddToSocatService").info("cruises " + cruiseExpocodes.toString() + 
				" submitted by " + username);
	}

}
