/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CruiseRestorer;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.util.Date;

/**
 * Restores WOCE flags for the current data of a cruise, 
 * such as when a cruise is suspended and resubmitted with 
 * the same data but updated metadata.
 * 
 * @author Karl Smith
 */
public class RestoreWoceFlags {

	/**
	 * Restores WOCE flags for the current data of a cruise, 
	 * such as when a cruise is suspended and resubmitted with 
	 * the same data but updated metadata.
	 * 
	 * @param args
	 * 		Expocode - expocode of the cruise to restore.
	 * 		Username - dashboard user requesting this update (for the QC comment).
	 */
	public static void main(String[] args) {
		if ( args.length != 2 ) {
			System.err.println("Arguments:  Expocode  Username ");
			System.err.println();
			System.err.println("Restores WOCE flags for the data currently given in the full-data ");
			System.err.println("DSG file for the indicated cruise.  If any WOCE flags change as a ");
			System.err.println("result of this action, a QC comment is added that WOCE flags were ");
			System.err.println("restored from resubmitted unchanged data.");
			System.err.println();
			System.err.println("NB: Use with caution as it only checks the data values recorded in the ");
			System.err.println("    WOCE event locations against data in the corresponding DSG records. ");
			System.err.println();
			System.err.println("Expocode");
			System.err.println("    expocode of the cruise to be restored. ");
			System.err.println("Username");
			System.err.println("    dashboard admin requesting this update. ");
			System.err.println();
			System.exit(1);
		}
		String expocode = args[0];
		String username = args[1];

		DashboardDataStore dataStore = null;
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			System.err.println("Problems obtaining the default dashboard " +
					"configuration: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		CruiseRestorer restorer = new CruiseRestorer(dataStore);
		DatabaseRequestHandler dbHandler = dataStore.getDatabaseRequestHandler();

		try {
			boolean changed = false;
			String socatVersion = null;
			try {
				System.out.println("Restoring any matching old WOCE flags for " + expocode);
				changed = restorer.restoreWoceFlags(expocode);
				socatVersion = restorer.getRestoredSocatVersion();
			} catch (Exception ex) {
				System.err.println(expocode + ": problems restoring the WOCE flags - " + ex.getMessage());
				ex.printStackTrace();
				System.err.println("========================================");
				System.exit(1);
			}
			if ( changed ) {
				// Add a QC comment regarding the restoration of WOCE flags
				SocatQCEvent qcEvent = new SocatQCEvent();
				qcEvent.setExpocode(expocode);
				qcEvent.setFlag(SocatQCEvent.QC_COMMENT);
				qcEvent.setFlagDate(new Date());
				qcEvent.setRegionID(DataLocation.GLOBAL_REGION_ID);
				qcEvent.setSocatVersion(socatVersion);
				qcEvent.setUsername(username);
				qcEvent.setComment("WOCE flags restored from resubmitted unchanged data");
				try {
					System.out.println("Adding restored-WOCE-flags QC comment for " + expocode);
					dbHandler.addQCEvent(qcEvent);
				} catch (Exception ex) {
					System.err.println(expocode + ": problems adding a QC comment - " + ex.getMessage());
					ex.printStackTrace();
					System.err.println("========================================");
					System.exit(1);
				}
			}
		} finally {
			dataStore.shutdown();
		}

		System.exit(0);
	}

}
