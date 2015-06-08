/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CruiseModifier;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Restores the cruise DSG files to the data currently given in the text data file.  
 * Removes WOCE flags and QC flags for the current SOCAT upload version and restores 
 * any old WOCE flags applicable to the current data.  Adds a QC comment that the 
 * cruise data and WOCE flags were restored.
 * 
 * @author Karl Smith
 */
public class RestoreCruise {

	/**
	 * Restores the cruise DSG files to the data currently given in the text data file.  
	 * Removes WOCE flags and QC flags for the current SOCAT upload version and restores 
	 * any old WOCE flags applicable to the current data.  Adds a QC comment that the 
	 * cruise data and WOCE flags were restored.  Optionally, adds a QC update ('U') flag
	 * indicating the metadata was updated.
	 * 
	 * @param args
	 * 		Expocode - expocode of the cruise to restore.
	 * 		Username - dashboard admin requesting this update.
	 * 		MetadataUpdated? - Y or yes if metadata was updated and a 'U' flag should be added
	 */
	public static void main(String[] args) {
		if ( args.length != 3 ) {
			System.err.println("Arguments:  Expocode  Username  MetadataUpdated?");
			System.err.println();
			System.err.println("Restores the cruise DSG files to the data currently given in ");
			System.err.println("the text data and properties file.  Removes WOCE flags and QC ");
			System.err.println("flags for the current SOCAT upload version and restores any ");
			System.err.println("old WOCE flags applicable to the current data.  If the QC flag ");
			System.err.println("from the revised QC flags does not match that in the properties ");
			System.err.println("file, a global v2 QC flag is added to resolve the issue.  Adds ");
			System.err.println("a QC comment that the cruise data and WOCE flags were restored.  ");
			System.err.println("Optionally, adds a QC update ('U') flag indicating the metadata ");
			System.err.println("was updated.");
			System.err.println();
			System.err.println("Expocode");
			System.err.println("    expocode of the cruise to be restored. ");
			System.err.println("Username");
			System.err.println("    dashboard admin requesting this update. ");
			System.err.println("MetadataUpdated?");
			System.err.println("    Y or yes if metadata was updated and a 'U' flag should be added. ");
			System.err.println();
			System.exit(1);
		}
		String expo = args[0];
		String username = args[1];
		boolean metadataUpdated;
		if ( "Y".equalsIgnoreCase(args[2]) || "YES".equalsIgnoreCase(args[2]) )
			metadataUpdated = true;
		else
			metadataUpdated = false;

		DashboardConfigStore configStore = null;
		try {
			configStore = DashboardConfigStore.get();
		} catch (Exception ex) {
			System.err.println("Problems obtaining the default dashboard " +
					"configuration: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		CruiseFileHandler cruiseHandler = configStore.getCruiseFileHandler();
		String removeSocatVersion = configStore.getSocatUploadVersion();
		ResubmitCruises resubmitter = new ResubmitCruises(configStore);
		DatabaseRequestHandler dbHandler = configStore.getDatabaseRequestHandler();
		CruiseModifier restorer = new CruiseModifier(configStore);
		DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

		try {
			// Get the QC flag to restore from the cruise info file
			DashboardCruise cruise = null;
			try {
				cruise = cruiseHandler.getCruiseFromInfoFile(expo);
			} catch (Exception ex) {
				System.err.println(expo + ": problems reaading the cruise properties file - " + ex.getMessage());
				ex.printStackTrace();
				System.err.println("========================================");
				System.exit(1);
			}
			String textSocatVersion = cruise.getVersion();
			if ( textSocatVersion.equals(removeSocatVersion) ) {
				System.err.println(expo + ": SOCAT version in the properties file is the current SOCAT version " + removeSocatVersion);
				System.err.println("========================================");
				System.exit(1);
			}
			Character oldQCFlag = SocatQCEvent.STATUS_FLAG_MAP.get(cruise.getQcStatus());
			if ( oldQCFlag == null ) {
				System.err.println(expo + ": problems interpreting the cruise qc status - " + cruise.getQcStatus());
				System.err.println("========================================");
				System.exit(1);
			}
			// Resubmit the cruise using the reverted text data
			try {
				System.out.println("Resubmitting " + expo + " from current text data");
				resubmitter.resubmitCruise(expo, username);
			} catch (Exception ex) {
				System.err.println(expo + ": problems resubmitting the cruise - " + ex.getMessage());
				ex.printStackTrace();
				System.err.println("========================================");
				System.exit(1);
			}
			// Remove any QC and WOCE flags for the current upload version
			// (including those generated by the above operations)
			try {
				System.out.println("Removing QC and WOCE flags for version " + removeSocatVersion);
				dbHandler.removeFlagsForCruiseVersion(expo, removeSocatVersion);
			} catch (Exception ex) {
				System.err.println(expo + ": problems removing current QC and WOCE flags - " + ex.getMessage());
				ex.printStackTrace();
				System.err.println("========================================");
				System.exit(1);
			}
			// Restore any applicable old WOCE flags for this cruise
			String restoredVersion = null;
			try {
				System.out.println("Restoring WOCE flags for the current data");
				restorer.restoreWoceFlags(expo);
				restoredVersion = restorer.getRestoredSocatVersion();
			} catch (Exception ex) {
				System.err.println(expo + ": problems restoring the WOCE flags - " + ex.getMessage());
				ex.printStackTrace();
				System.err.println("========================================");
				System.exit(1);
			}
			if ( ! restoredVersion.equals(textSocatVersion) ) {
				System.err.println(expo + ": unexpected mismatch of restored SOCAT versions: DSG file " + 
						restoredVersion + "; properties file " + textSocatVersion);
				System.err.println("========================================");
				System.exit(1);
			}
			// Add a QC comment regarding the restoring of old version data
			SocatQCEvent qcEvent = new SocatQCEvent();
			qcEvent.setExpocode(expo);
			qcEvent.setFlag(SocatQCEvent.QC_COMMENT);
			qcEvent.setFlagDate(new Date());
			qcEvent.setRegionID(DataLocation.GLOBAL_REGION_ID);
			qcEvent.setSocatVersion(removeSocatVersion);
			qcEvent.setUsername(username);
			qcEvent.setComment("Cruise data and WOCE flags restored to SOCAT version " + 
					restoredVersion + ".  QC and WOCE flags for SOCAT version " + 
					removeSocatVersion + " were removed.");
			try {
				System.out.println("Adding restored QC comment for " + expo);
				dbHandler.addQCEvent(qcEvent);
			} catch (Exception ex) {
				System.err.println(expo + ": problems adding a QC comment - " + ex.getMessage());
				ex.printStackTrace();
				System.err.println("========================================");
				System.exit(1);
			}
			// Get the revised QC flag
			Character qcFlag = null;
			try {
				qcFlag = dbHandler.getQCFlag(expo);
			} catch (Exception ex) {
				System.err.println(expo + ": problems getting the revised QC flag - " + ex.getMessage());
				ex.printStackTrace();
				System.err.println("========================================");
				System.exit(1);
			}
			if ( ! qcFlag.equals(oldQCFlag) ) {
				SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
				Date oldQCTime = null;
				try {
					oldQCTime = dateParser.parse("2013-12-31 12:00:00");
				} catch (ParseException ex) {
					System.err.println("Unexpected error parsing 2013-12-31 12:00:00");
					ex.printStackTrace();
					System.exit(1);
				}
				qcFlag = oldQCFlag;
				qcEvent.setExpocode(expo);
				qcEvent.setFlag(qcFlag);
				qcEvent.setFlagDate(oldQCTime);
				qcEvent.setRegionID(DataLocation.GLOBAL_REGION_ID);
				qcEvent.setSocatVersion("2.0");
				qcEvent.setUsername(username);
				qcEvent.setComment("Adding global QC flag to that assigned in v2 to fix unresolved conflicts");
				try {
					System.out.println("Adding global QC flag of " + qcFlag + " to resolve v2 conflicts");
					dbHandler.addQCEvent(qcEvent);
				} catch (Exception ex) {
					System.err.println(expo + ": problems adding a QC conflict resolution flag - " + ex.getMessage());
					ex.printStackTrace();
					System.err.println("========================================");
					System.exit(1);
				}
			}
			if ( metadataUpdated ) {
				qcFlag = SocatQCEvent.QC_UPDATED_FLAG;
				qcEvent.setExpocode(expo);
				qcEvent.setFlag(qcFlag);
				qcEvent.setFlagDate(new Date());
				qcEvent.setRegionID(DataLocation.GLOBAL_REGION_ID);
				qcEvent.setSocatVersion(removeSocatVersion);
				qcEvent.setUsername(username);
				qcEvent.setComment("Metadata had been updated.  Restored data and WOCE flags were not changed.");
				try {
					System.out.println("Adding global QC flag of U marking that the metadata was updated");
					dbHandler.addQCEvent(qcEvent);
				} catch (Exception ex) {
					System.err.println(expo + ": problems adding a QC update flag - " + ex.getMessage());
					ex.printStackTrace();
					System.err.println("========================================");
					System.exit(1);
				}
			}
			// Assign the revised flag to the DSG files
			try {
				System.out.println("Updating the QC flag in the DSG files to " + qcFlag);
				dsgHandler.getDsgNcFile(expo).updateQCFlag(qcFlag);
				dsgHandler.getDecDsgNcFile(expo).updateQCFlag(qcFlag);
			} catch (Exception ex) {
				System.err.println(expo + ": problems updating the QC flag - " + ex.getMessage());
				ex.printStackTrace();
				System.err.println("========================================");
				System.exit(1);
			}
		} finally {
			configStore.shutdown();
		}

		System.exit(0);
	}

}
