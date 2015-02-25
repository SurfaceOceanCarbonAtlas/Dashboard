/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Server side specification of data column specification services
 * @author Karl Smith
 */
@RemoteServiceRelativePath("DataSpecsService")
public interface DataSpecsService extends RemoteService {

	/**
	 * Reads the saved cruise file and returns the current data
	 * column specifications as well as some initial cruise data
	 * to assist in identifying cruise data columns.
	 *  
	 * @param username
	 * 		username for validation
	 * @param expocode
	 * 		generate report for this cruise
	 * @return
	 * 		current cruise data column specifications and 
	 * 		initial (partial) cruise data
	 * @throws IllegalArgumentException
	 * 		if authentication fails, if expocode is invalid,
	 * 		if the cruise does not exist, or if there are 
	 * 		problems obtaining the data for the cruise
	 */
	DashboardCruiseWithData getCruiseDataColumnSpecs(String username, 
			String expocode) throws IllegalArgumentException;

	/**
	 * Reads the saved cruise file and returns the specified
	 * rows of cruise data.  The outer list contains the rows 
	 * of cruise data; the inner list contains the columns of
	 * cruise data for that row.  (Thus, each row is all data 
	 * measured for a given sample, and each column is data 
	 * of a given type measured for all samples.)
	 * 
	 * @param username
	 * 		username for validation
	 * @param expocode
	 * 		get data for this cruise
	 * @param firstRow
	 * 		index of the first row of data to return
	 * @param numRows
	 * 		number of rows of data to return
	 * @return
	 * 		rows of data for a cruise.
	 * @throws IllegalArgumentException
	 * 		if authentication fails, if expocode is invalid,
	 * 		if the cruise does not exist, or if there are 
	 * 		problems obtaining the specified data for the cruise
	 */
	ArrayList<ArrayList<String>> getCruiseData(String username,
			String expocode, int firstRow, int numRows)
					throws IllegalArgumentException;

	/**
	 * Updates the data column specifications for a cruise 
	 * to those provided.  This triggers the SanityChecker
	 * to run using the new data column specifications.
	 * 
	 * @param username
	 * 		username for validation
	 * @param newSpecs
	 * 		cruise data column types to assign.  The expocode
	 * 		in this object specifies the cruise to update.
	 * 		Any cruise data in this object is ignored.
	 * @return
	 * 		the updated cruise with (abbreviated) data after 
	 * 		processing through the SanityChecker
	 * @throws IllegalArgumentException
	 * 		if authentication fails, if expocode is invalid,
	 * 		if the cruise does not exist, or if there are 
	 * 		problems obtaining or evaluating the data for 
	 * 		the cruise
	 */
	DashboardCruiseWithData updateCruiseDataColumnSpecs(String username, 
			DashboardCruise newSpecs) throws IllegalArgumentException;

	/**
	 * Updates the data column specifications for the cruises with the 
	 * given expocodes.  Column types are assigned from column names-to-types
	 * saved for this user, and the SanityChecker is run using these new
	 * column types.  Any exceptions thrown in the column assignment or
	 * sanity checking for a cruise only halt the process for that cruise
	 * but otherwise is silently ignored.
	 * 
	 * @param username
	 * 		username for validation
	 * @param cruiseExpocodes
	 * 		process cruises with these expocodes
	 * @throws IllegalArgumentException
	 * 		if authentication fails
	 */
	void updateCruiseDataColumns(String username, ArrayList<String> cruiseExpocodes) 
			throws IllegalArgumentException;

}
