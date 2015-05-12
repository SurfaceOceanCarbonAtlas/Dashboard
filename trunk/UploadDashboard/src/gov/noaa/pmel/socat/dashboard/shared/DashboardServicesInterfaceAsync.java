/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Client side interface for most dashboard server functions.
 * 
 * @author Karl Smith
 */
public interface DashboardServicesInterfaceAsync {

	/**
	 * Client side request to log out the current user.
	 * 
	 * @param callback
	 * 		the callback to make after logout.
	 */
	void logoutUser(AsyncCallback<Void> callback);

	/**
	 * Client side request to get the current user's list of cruises.
	 * 
	 * @param callback
	 * 		the callback to make with the cruise list.
	 */
	void getCruiseList(AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to deletes all files for the indicated cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		cruises to be deleted
	 * @param deleteMetadata
	 * 		also delete metadata and additional documents?
	 * @return
	 * 		the updated list of cruises for the current user
	 * @param callback
	 * 		the callback to make with the updated cruise list for the current user
	 */
	void deleteCruises(String username, TreeSet<String> expocodeSet, 
			Boolean deleteMetadata, AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to add the indicated cruises 
	 * to the current user's list of cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param wildExpocode
	 * 		expocode, possibly with wildcards * and ?, to use
	 * @param callback
	 * 		the callback to make with the current user's updated cruise list
	 */
	void addCruisesToList(String username, String wildExpocode,
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to remove the indicated cruises from the current 
	 * user's list of cruises (but does not delete any files for these cruises).
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		cruises to be removed
	 * @param callback
	 * 		the callback to make with the current user's updated cruise list
	 */	
	void removeCruisesFromList(String username, TreeSet<String> expocodeSet, 
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to return the latest cruise information 
	 * for the indicated cruises.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocodeSet
	 * 		set of all cruises to include in the returned
	 * 		updated cruise information
	 * @param callback
	 * 		the callback to make with the updated cruise information
	 */
	void getUpdatedCruises(String username, TreeSet<String> expocodeSet,
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Client side request to remove (delete) an ancillary document 
	 * for a cruise.
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
	 * @param callback
	 * 		the callback to make with the updated cruise information
	 */
	void deleteAddlDoc(String username, String deleteFilename, 
			String expocode, TreeSet<String> allExpocodes,
			AsyncCallback<DashboardCruiseList> callback);

	/**
	 * Reads the saved cruise file and returns the current data
	 * column specifications as well as some initial cruise data
	 * to assist in identifying cruise data columns.
	 *  
	 * @param username
	 * 		username for validation
	 * @param expocode
	 * 		generate report for this cruise
	 * @param callback
	 * 		callback to make with the current cruise data 
	 * 		column specifications and initial (partial) cruise data.
	 * 		The fail method is invoked if authentication fails, 
	 * 		if expocode is invalid, if the cruise does not exist, 
	 * 		or if there are problems obtaining the data for the cruise
	 */
	void getCruiseDataColumnSpecs(String username, String expocode, 
			AsyncCallback<DashboardCruiseWithData> callback);

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
	 * @param callback
	 * 		callback to make with rows of data for a cruise.
	 * 		The fail method is invoked if authentication fails, 
	 * 		if expocode is invalid, if the cruise does not exist, 
	 * 		or if there are problems obtaining the specified data 
	 * 		for the cruise
	 */
	void getCruiseData(String username, String expocode, int firstRow, int numRows, 
			AsyncCallback<ArrayList<ArrayList<String>>> callback);

	/**
	 * Updates the data column specifications for a cruise to those 
	 * provided.  This triggers the SanityChecker to run using the 
	 * new data column specifications.
	 * 
	 * @param username
	 * 		username for validation
	 * @param newSpecs
	 * 		cruise data column types to assign.  The expocode in this 
	 * 		object specifies the cruise to update.  Any cruise data in 
	 * 		this object is ignored.
	 * @param callback
	 * 		callback to make with the the updated cruise with  
	 * 		(abbreviated) data after processing through the SanityChecker 
	 * 		after processing through the SanityChecker.  The fail method 
	 * 		is invoked if authentication fails, if expocode is invalid, 
	 * 		if the cruise does not exist, or if there are problems 
	 * 		obtaining or evaluating the data for the cruise
	 */
	void updateCruiseDataColumnSpecs(String username, DashboardCruise newSpecs, 
			AsyncCallback<DashboardCruiseWithData> callback);

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
	 * @param callback
	 * 		callback to make after processing is complete.
	 * 		The fail method is invoked if authentication fails.
	 */
	void updateCruiseDataColumns(String username, 
			ArrayList<String> cruiseExpocodes, AsyncCallback<Void> callback);

	/**
	 * Client side request to send the set of sanity checker 
	 * data messages for a given cruise.
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param expocode
	 * 		get data messages for this cruise
	 * @param callback
	 * 		the callback to make with list of sanity checker data messages
	 */
	void getDataMessages(String username, String expocode,
			AsyncCallback<SCMessageList> callback);

	/**
	 * Client-side interface for getting the absolute path 
	 * to the OME.xml file for a cruise
	 * 
	 * @param username
	 * 		name of the current user - for validation
	 * @param activeExpocode
	 * 		get the OME for this cruise
	 * @param previousExpocode
	 * 		if not empty, initialize with metadata from this cruise
	 * @param callback
	 * 		the callback to make with the absolute path to the OME.xml file 
	 * 		for activeExpocode; the onFailure method of the callback will be 
	 * 		called if authentication failed, or if the appropriate content 
	 * 		for the OME could not be found
	 */
	void getOmeXmlPath(String pageUsername, String activeExpocode, 
			String previousExpocode, AsyncCallback<String> callback);

	/**
	 * Client side request to generate the preview images for a cruise.
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
	 * @param callback
	 * 		callback to make indicating the image-generating status
	 * 		(true if done generating plots)
	 */
	void buildPreviewImages(String username, String expocode, String timetag,
			boolean firstCall, AsyncCallback<Boolean> callback);

	/**
	 * Client-side interface for submitting cruises for QC.
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
	 * @param callback
	 * 		the callback to make when complete; the onFailure method 
	 * 		of the callback will be called if authentication failed, 
	 * 		if a dashboard cruise does not exist for any of the 
	 * 		expocodes, or if the submitting of a cruise or change in
	 * 		archive status failed.
	 */
	void submitCruiseForQC(String username, HashSet<String> cruiseExpocodes, 
			String archiveStatus, String localTimestamp, boolean repeatSend,
			AsyncCallback<Void> callback);

}
