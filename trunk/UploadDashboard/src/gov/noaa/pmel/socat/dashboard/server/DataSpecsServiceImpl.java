/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.actions.DashboardCruiseChecker;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.UserFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataSpecsService;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the DataSpecsService
 * @author Karl Smith
 */
public class DataSpecsServiceImpl extends RemoteServiceServlet
									implements DataSpecsService {

	private static final long serialVersionUID = 4906173745164245169L;

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
	public DashboardCruiseWithData getCruiseDataColumnSpecs(String pageUsername,
			String expocode) throws IllegalArgumentException {
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		// Check that the username matches that which was displayed on the page
		if ( ! username.equals(pageUsername) )
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
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		// Check that the username matches that which was displayed on the page
		if ( ! username.equals(pageUsername) )
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
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		// Check that the username matches that which was displayed on the page
		if ( ! username.equals(pageUsername) )
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
		// Get the dashboard data store and current username
		if ( ! validateRequest() ) 
			throw new IllegalArgumentException("Invalid user request");
		// Check that the username matches that which was displayed on the page
		if ( ! username.equals(pageUsername) )
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

}
