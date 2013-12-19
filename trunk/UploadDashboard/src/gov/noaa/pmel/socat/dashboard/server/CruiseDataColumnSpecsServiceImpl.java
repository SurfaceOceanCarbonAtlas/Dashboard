/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecsService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of the CruiseDataColumnSpecsService
 * @author Karl Smith
 */
public class CruiseDataColumnSpecsServiceImpl extends RemoteServiceServlet
									implements CruiseDataColumnSpecsService {

	private static final long serialVersionUID = 3851933149162963899L;

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

		// Get the cruise with the first 25 rows of data
		DashboardCruiseWithData cruiseData = dataStore.getCruiseFileHandler()
									.getCruiseDataFromFiles(expocode, 0, 25);
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
		// TODO?: revise the data descriptions to the standard if it is a 
		// well-known type (not delete, unknown, or supplemental) ?

		// TODO: run the SanityChecker on the cruise data 
		//       with the updated cruise column specifications
		//       Need to add something (set of row,column pairs?)
		//       to DashboardCruiseWithData to indicate questionable 
		//       and bad data values, and columns with minor or 
		//       major problems.
		cruiseData.setDataCheckStatus(DashboardUtils.CHECK_STATUS_ACCEPTABLE);

		// Save and commit the updated cruise columns
		dataStore.getCruiseFileHandler().saveCruiseToInfoFile(cruiseData, 
				"Cruise column types and units for " +  cruiseData.getExpocode() + 
				" updated by " + username);
		
		// Remove all but the first 25 rows of cruise data 
		// to minimize the payload of the returned cruise data
		if ( cruiseData.getNumDataRows() > 25 )
			cruiseData.getDataValues().subList(0,25).clear();

		// Return the updated truncated cruise data for redisplay 
		// in the CruiseDataColumnSpecsPage
		return cruiseData;
	}

}
