/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Renames dashboard cruise files, as well as SOCAT files and 
 * database flags if the cruise has been submitted.
 * 
 * @author Karl Smith
 */
public class CruiseRenamer {

	CruiseFileHandler cruiseHandler;
	CheckerMessageHandler msgHandler;
	MetadataFileHandler metadataHandler;
	DsgNcFileHandler dsgHandler;
	DatabaseRequestHandler databaseHandler;
	String socatVersion;
	
	/**
	 * @param configStore
	 * 		create with the handlers given in the dashboard data store.
	 */
	public CruiseRenamer(DashboardConfigStore configStore) {
		cruiseHandler = configStore.getCruiseFileHandler();
		msgHandler = configStore.getCheckerMsgHandler();
		metadataHandler = configStore.getMetadataFileHandler();
		dsgHandler = configStore.getDsgNcFileHandler();
		databaseHandler = configStore.getDatabaseRequestHandler();
		socatVersion = configStore.getSocatUploadVersion();
	}

	/**
	 * Appropriately renames dashboard cruise files, as well as SOCAT files and 
	 * database flags if the cruise has been submitted.  If an exception is thrown,
	 * the system is likely have a corrupt mix of renamed and original-name files.
	 * 
	 * @param oldExpocode
	 * 		current expocode for the cruise
	 * @param newExpocode
	 * 		new expocode to use for the cruise
	 * @param username
	 * 		username to associate with the rename QC and WOCE events
	 * @throws IllegalArgumentException
	 * 		if the username is not an admin,
	 * 		if either expocode is invalid,
	 * 		if cruise files for the old expocode do not exist,
	 * 		if any files for the new expocode already exist
	 * @throws IOException
	 * 		if updating a file with the new expocode throws one
	 * @throws SQLException 
	 * 		if username is not a known user, or
	 * 		if accessing or updating the database throws one
	 */
	public void renameCruise(String oldExpocode, String newExpocode, String username) 
			throws IllegalArgumentException, IOException, SQLException {
		// check and standardized the expocodes
		String oldExpo = DashboardServerUtils.checkExpocode(oldExpocode);
		String newExpo = DashboardServerUtils.checkExpocode(newExpocode);
		// rename the cruise data and info files; update the expocode in the data file
		cruiseHandler.renameCruiseFiles(oldExpo, newExpo);
		// rename the SanityChecker messages file, if it exists
		msgHandler.renameMsgsFile(oldExpo, newExpo);
		// rename metadata files; update the expocode in the OME metadata
		metadataHandler.renameMetadataFiles(oldExpo, newExpo);
		// rename the DSG and decimated DSG files; update the expocode in these files
		dsgHandler.renameDsgFiles(oldExpo, newExpo);
		// generate a rename QC comment and modify expocodes for the flags
		databaseHandler.renameCruiseFlags(oldExpo, newExpo, socatVersion, username);
	}

}
