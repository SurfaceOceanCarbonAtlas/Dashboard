/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataSpecsService;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the DataSpecsService
 * @author Karl Smith
 */
public class DataSpecsServiceImpl extends RemoteServiceServlet
												implements DataSpecsService {

	private static final long serialVersionUID = -7581874380636284815L;

	@Override
	public DashboardCruiseWithData getCruiseDataColumnSpecs(String username,
			String passhash, String expocode) throws IllegalArgumentException {
		// Authenticate the user
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

		// Get the cruise with the first maximum-needed number of rows
		DashboardCruiseWithData cruiseData = dataStore.getCruiseFileHandler()
				.getCruiseDataFromFiles(expocode, 0, 
						DashboardUtils.MAX_ROWS_PER_GRID_PAGE);
		if ( cruiseData == null )
			throw new IllegalArgumentException(
					"cruise " + expocode + " does not exist");

		// Remove any metadata preamble to reduced data transmitted
		cruiseData.getPreamble().clear();

		// Return the cruise with the partial data
		return cruiseData;
	}

	@Override
	public ArrayList<ArrayList<String>> getCruiseData(String username,
				String passhash, String expocode, int firstRow, int numRows)
											throws IllegalArgumentException {
		// Authenticate the user
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

		// Get the cruise data with exactly the data rows desired
		DashboardCruiseWithData cruiseWithData = 
				dataStore.getCruiseFileHandler()
						 .getCruiseDataFromFiles(expocode, firstRow, numRows);
		if ( cruiseWithData == null )
			throw new IllegalArgumentException(
					"cruise " + expocode + " does not exist");
		ArrayList<ArrayList<String>> cruiseDataRows = cruiseWithData.getDataValues();
		if ( cruiseDataRows.size() != numRows )
			throw new IllegalArgumentException(
					"invalid requested row numbers: " + 
					firstRow + " - " + (firstRow + numRows));
		return cruiseDataRows;
	}

	@Override
	public DashboardCruiseWithData updateCruiseDataColumnSpecs(String username,
									String passhash, DashboardCruise newSpecs)
											throws IllegalArgumentException {
		// Authenticate the user
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

		// Retrieve all the current cruise data
		DashboardCruiseWithData cruiseData = dataStore.getCruiseFileHandler()
						.getCruiseDataFromFiles(newSpecs.getExpocode(), 0, -1);
		// Revise the cruise data column types and units 
		if ( newSpecs.getDataColTypes().size() != 
				cruiseData.getDataColTypes().size() )
			throw new IllegalArgumentException(
					"Unexpected number of data columns (" +
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
		dataStore.getUserFileHandler()
				 .updateUserDataColumnTypes(cruiseData, username);
		
		// Remove all but the first maximum-needed number of rows of cruise data 
		// to minimize the payload of the returned cruise data
		int numRows = cruiseData.getNumDataRows();
		if ( numRows > DashboardUtils.MAX_ROWS_PER_GRID_PAGE )
			cruiseData.getDataValues()
					  .subList(DashboardUtils.MAX_ROWS_PER_GRID_PAGE, numRows)
					  .clear();

		// Return the updated truncated cruise data for redisplay 
		// in the DataColumnSpecsPage
		return cruiseData;
	}

	@Override
	public void updateCruiseDataColumns(String username, String passhash,
			ArrayList<String> cruiseExpocodes) throws IllegalArgumentException {
		// Authenticate the user
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

		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		UserFileHandler userHandler = dataStore.getUserFileHandler();
		DashboardCruiseChecker cruiseChecker = dataStore.getDashboardCruiseChecker();

		for ( String expocode : cruiseExpocodes ) {
			try {
				// Retrieve all the current cruise data
				DashboardCruiseWithData cruiseData = 
						cruiseHandler.getCruiseDataFromFiles(expocode, 0, -1);

				// Identify the columns from stored names-to-types for this user
				userHandler.assignDataColumnTypes(cruiseData);
				// Save and commit these column assignments in case the sanity checker has problems
				cruiseHandler.saveCruiseInfoToFile(cruiseData, 
						"Column types for " + expocode + " updated by " + username + 
						" from post-processing a multiple-dataset upload");
			
				// Run the SanityCheck on the updated cruise.  Saves the SanityChecker messages,
				// and assigns the data check status and the WOCE-3 and WOCE-4 data flags.
				cruiseChecker.checkCruise(cruiseData);

				// Save and commit the updated cruise information
				cruiseHandler.saveCruiseInfoToFile(cruiseData, 
						"Data status and WOCE flags for " + expocode + " updated by " + 
						username + " from post-processing a multiple-dataset upload");
			} catch (Exception ex) {
				// Silently ignore problems (such as unidentified columns)
				continue;
			}
		}
	}

}
