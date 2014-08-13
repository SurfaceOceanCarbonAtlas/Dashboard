/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
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
	
	/**
	 * @param dataStore
	 * 		create with the handlers given in the dashboard data store.
	 */
	public CruiseRenamer(DashboardDataStore dataStore) {
		cruiseHandler = dataStore.getCruiseFileHandler();
		msgHandler = dataStore.getCheckerMsgHandler();
		metadataHandler = dataStore.getMetadataFileHandler();
		dsgHandler = dataStore.getDsgNcFileHandler();
		databaseHandler = dataStore.getDatabaseRequestHandler();
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
	 * @param socatVersion
	 * 		SOCAT version to associate with the rename QC and WOCE events
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
	public void renameCruise(String oldExpocode, String newExpocode, 
			String socatVersion, String username) 
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

	/**
	 * Renames a cruise (changes the expocode).  All files will be moved to the
	 * new locations specified by the new expocode.  If appropriate, file contents
	 * are updated for the new expocode.  If the cruise has been submitted for QC,
	 * QC and WOCE flags are updated and rename events are added.
	 *  
	 * @param args
	 * 		Username - name of the dashboard admin user requesting this update.
	 * 		OldExpocode - expocode of the cruise to rename
	 * 		NewExpocode - new expocode for the cruise
	 */
	public static void main(String[] args) {
		if ( args.length != 3 ) {
			System.err.println("Arguments:  Username  OldExpocode  NewExpocode");
			System.err.println();
			System.err.println("Renames the cruise with OldExpocode to NewExpocode.  ");
			System.err.println("All files will be moved, and updated if appropriate. ");
			System.err.println("If the cruise had been submitted for QC, QC and WOCE ");
			System.err.println("flags will be updated and rename events will be added. ");
			System.err.println();
			System.err.println("Username is the dashboard admin requesting this update.");
			System.err.println();
			System.exit(1);
		}

		String username = args[0];
		String oldExpocode = args[1];
		String newExpocode = args[2];

		// Get the default dashboard configuration
		DashboardDataStore dataStore = null;		
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard " +
					"configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			try {
				if ( ! dataStore.isAdmin(username) ) {
					System.err.println(username + " is not an admin for the dashboard");
					System.exit(1);
				}
				CruiseRenamer renamer = new CruiseRenamer(dataStore);
				renamer.renameCruise(oldExpocode, newExpocode, 
						dataStore.getSocatUploadVersion(), username);
			} catch (Exception ex) {
				System.err.println("Error renaming " + oldExpocode + " to " + 
						newExpocode + " : " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}
		} finally {
			dataStore.shutdown();
		}
		System.exit(0);
	}

}
