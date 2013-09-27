/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecs;
import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnSpecsService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;

/**
 * Server side implementation of the CruiseDataColumnSpecsService
 * @author Karl Smith
 */
public class CruiseDataColumnSpecsServiceImpl extends RemoteServiceServlet
									implements CruiseDataColumnSpecsService {

	private static final long serialVersionUID = 1097219634256593888L;

	@Override
	public CruiseDataColumnSpecs getCruiseDataColumnSpecs(String username,
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

		// Get the cruise with all the data
		// so we know how many data rows are available.
		DashboardCruiseWithData cruiseData = dataStore.getCruiseFileHandler()
									.getCruiseDataFromFile(expocode, 0, -1);
		if ( cruiseData == null )
			throw new IllegalArgumentException(
					"cruise " + expocode + " does not exist");

		// Create and return the cruise data column specs from this cruise data
		return dataStore.getUserFileHandler()
						.createColumnSpecsForCruise(cruiseData);
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
						 .getCruiseDataFromFile(expocode, firstRow, numRows);
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
	public CruiseDataColumnSpecs updateCruiseDataColumnSpecs(String username,
							String passhash, CruiseDataColumnSpecs newSpecs)
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

		// TODO: save the updated cruise column specifications
		// TODO: run the SanityChecker on the data with the updated cruise column specifications
		// TODO: return the SanityChecker-updated cruise column specifications
		throw new IllegalArgumentException("Method not yet implemented");
	}

}
