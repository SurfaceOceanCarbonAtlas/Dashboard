/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Server side interface for most dashboard server functions.
 * 
 * @author Karl Smith
 */
@RemoteServiceRelativePath("DashboardServices")
public interface DashboardServicesInterface extends RemoteService {

	/**
	 * Logs out the current user.
	 */
	void logoutUser();
	
	/**
	 * Gets the current user's list of cruises.
	 * 
	 * @return
	 * 		the list of cruises for the current user
	 * @throws IllegalArgumentException
	 * 		if problems getting the cruise list
	 */
	DashboardCruiseList getCruiseList() throws IllegalArgumentException;

	/**
	 * Deletes all files for the indicated cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		cruises to be deleted
	 * @param deleteMetadata
	 * 		also delete metadata and additional documents?
	 * @return
	 * 		the updated list of cruises for the current user
	 * @throws IllegalArgumentException
	 * 		if problems deleting a cruise
	 */
	DashboardCruiseList deleteCruises(String username, TreeSet<String> expocodeSet, 
			Boolean deleteMetadata) throws IllegalArgumentException;

	/**
	 * Adds the indicated cruises to the current user's list of cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param wildExpocode
	 * 		expocode, possibly with wildcards * and ?, to use
	 * @return
	 * 		the updated list of cruises for the current user
	 * @throws IllegalArgumentException
	 * 		if the cruise to be added does not exist
	 */
	DashboardCruiseList addCruisesToList(String username, String wildExpocode) 
			throws IllegalArgumentException;

	/**
	 * Removes the indicated cruises from the current user's 
	 * list of cruises (but does not delete any files for these cruises).
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		cruises to be removed
	 * @return
	 * 		the updated list of cruises for the user
	 * @throws IllegalArgumentException
	 * 		if problems removing a cruise
	 */
	DashboardCruiseList removeCruisesFromList(String username, TreeSet<String> expocodeSet) 
			throws IllegalArgumentException;

	/**
	 * Returns the latest cruise information for the indicated cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		set of all cruises to include in the returned
	 * 		updated cruise information
	 * @return
	 * 		updated cruise information for the indicated cruises
	 * @throws IllegalArgumentException
	 * 		if a cruise does not exist
	 */
	DashboardCruiseList getUpdatedCruises(String username, TreeSet<String> expocodeSet) 
			throws IllegalArgumentException;

	/**
	 * Delete an ancillary document for a cruise.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param deleteFilename
	 * 		remove the ancillary document with this name
	 * @param expocode
	 * 		remove the ancillary document from this cruise
	 * @param allExpocodes
	 * 		set of all cruises to include in the returned 
	 * 		updated cruise information
	 * @return
	 * 		updated cruise information for the indicated cruises 
	 * @throws IllegalArgumentException
	 * 		if the metadata document does not exist 
	 */
	DashboardCruiseList deleteAddlDoc(String username, String deleteFilename, 
			String expocode, TreeSet<String> allExpocodes) throws IllegalArgumentException;

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

	/**
	 * Returns the set of sanity checker data messages for a given cruise.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocode
	 * 		get data messages for this cruise
	 * @return
	 * 		list of data messages for the cruise; 
	 * 		never null, but may be empty if the SanityChecker 
	 * 		did not generate any data messages. 
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is invalid, or
	 * 		if the SanityChecker has never been run on this cruise.
	 */
	SCMessageList getDataMessages(String username, String expocode) 
			throws IllegalArgumentException;

	/**
	 * Provides the absolute path to the OME.xml file for a cruise
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param activeExpocode
	 * 		get the OME for this cruise
	 * @param previousExpocode
	 * 		if not empty, initialize with metadata from this cruise
	 * @returns
	 * 		the absolute path to the OME.xml file for activeExpocode
	 * @throws IllegalArgumentException
	 * 		if authentication failed, or
	 * 		if the appropriate content for the OME could not be found
	 */
	String getOmeXmlPath(String pageUsername, String activeExpocode, 
			String previousExpocode) throws IllegalArgumentException;

	/**
	 * Requests that the preview images for a cruise be generated.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocode
	 * 		get data messages for this cruise
	 * @param timetag
	 * 		tag to be added to the end of the plot file names
	 * 		(before the filename extension) to make them specific
	 * 		to the time the request was made
	 * @param firstCall
	 * 		is this the first request for the preview images?
	 * 		If true, the process to generate the images are started.
	 * 		If false, just checks if all the images have been created.
	 * @return
	 * 		true if all the images have been created
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is invalid, or
	 * 		if the images cannot be created (probably because of bad data)
	 */
	boolean buildPreviewImages(String username, String expocode, String timetag,
			boolean firstCall) throws IllegalArgumentException;

	/**
	 * Submits cruises named in the given listing for QC.
	 * 
	 * @param username
	 * 		name of user making this request - for validation
	 * @param cruiseExpocodes
	 * 		expocodes of cruises to submit
	 * @param archiveStatus
	 * 		archive status to apply
	 * @param localTimestamp
	 * 		client local timestamp of this request 
	 * @param repeatSend
	 * 		if the archive request is to send to CDIAC ASAP,
	 * 		should cruises already sent be resent?
	 * @throws IllegalArgumentException
	 * 		if authentication failed, if the dashboard cruise does 
	 * 		not exist for any of the given expocodes, or if submitting 
	 * 		the cruise data failed
	 */
	void submitCruiseForQC(String username, HashSet<String> cruiseExpocodes, 
			String archiveStatus, String localTimestamp, boolean repeatSend)
					throws IllegalArgumentException;

}
