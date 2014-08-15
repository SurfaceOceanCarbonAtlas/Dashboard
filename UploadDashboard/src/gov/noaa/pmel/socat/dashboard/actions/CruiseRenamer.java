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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.TreeMap;

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
	 * @param dataStore
	 * 		create with the handlers given in the dashboard data store.
	 */
	public CruiseRenamer(DashboardDataStore dataStore) {
		cruiseHandler = dataStore.getCruiseFileHandler();
		msgHandler = dataStore.getCheckerMsgHandler();
		metadataHandler = dataStore.getMetadataFileHandler();
		dsgHandler = dataStore.getDsgNcFileHandler();
		databaseHandler = dataStore.getDatabaseRequestHandler();
		socatVersion = dataStore.getSocatUploadVersion();
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

	/**
	 * Renames cruises (changes the expocodes).  All files will be moved to the
	 * new locations specified by the new expocode.  If appropriate, file contents
	 * are updated for the new expocodes.  If a cruise has been submitted for QC,
	 * QC and WOCE flags are updated and rename events are added.
	 *  
	 * @param args
	 * 		Username - name of the dashboard admin user requesting this update.
	 * 		ExpocodesFile - file of old and new expocodes (one pair per line) for the cruises
	 */
	public static void main(String[] args) {
		if ( args.length != 2 ) {
			System.err.println("Arguments:  Username  ExpocodesFile");
			System.err.println();
			System.err.println("Renames cruise (changes the expocodes).  All files will be moved, ");
			System.err.println("and updated if appropriate.  If the cruise had been submitted for ");
			System.err.println("QC, the QC and WOCE flags will be updated and rename events will ");
			System.err.println("be added. ");
			System.err.println();
			System.err.println("Username is the dashboard admin requesting this update.");
			System.err.println("ExpocodesFile is a file of old and new expocode pairs, one pair per line");
			System.err.println();
			System.exit(1);
		}

		String username = args[0];
		File exposFile = new File(args[1]);

		TreeMap<String,String> oldNewExpoMap = new TreeMap<String,String>();
		try {
			BufferedReader exposReader = new BufferedReader(new FileReader(exposFile));
			try {
				String dataline = exposReader.readLine();
				while ( dataline != null ) {
					String[] expoPair = dataline.split("\\s+");
					if ( expoPair.length != 2 )
						throw new IllegalArgumentException("not an expocode pair: '" + dataline.trim() + "'");
					oldNewExpoMap.put(expoPair[0], expoPair[1]);
					dataline = exposReader.readLine();
				}
			} finally {
				exposReader.close();
			}
			if ( oldNewExpoMap.isEmpty() )
				throw new IOException("file is empty");
		} catch (Exception ex) {
			System.err.println("Problems reading the old and new expocodes from " + exposFile.getPath());
			System.exit(1);
		}

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
			if ( ! dataStore.isAdmin(username) ) {
				System.err.println(username + " is not an admin for the dashboard");
				System.exit(1);
			}
			CruiseRenamer renamer = new CruiseRenamer(dataStore);
			for ( Entry<String, String> expoEntry: oldNewExpoMap.entrySet() ) {
				String oldExpocode = expoEntry.getKey();
				String newExpocode = expoEntry.getValue();
				try {
					renamer.renameCruise(oldExpocode, newExpocode, username);
				} catch (Exception ex) {
					System.err.println("Error renaming " + oldExpocode + " to " + newExpocode);
					ex.printStackTrace();
				}
			}
		} finally {
			dataStore.shutdown();
		}
		System.exit(0);
	}

}
