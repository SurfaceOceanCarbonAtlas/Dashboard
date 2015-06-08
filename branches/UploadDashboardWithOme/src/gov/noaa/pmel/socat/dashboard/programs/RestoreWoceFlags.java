/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.CruiseModifier;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.util.Date;

/**
 * Restores non-automated WOCE flags for the current data of a cruise. 
 * This is intended for cruises that are suspended and resubmitted with 
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
	 * 		Expocode - restore WOCE flags in the cruise with this expocode
	 * 		Username - dashboard user requesting this update (for the QC comment).
	 */
	public static void main(String[] args) {
		if ( args.length != 2 ) {
			System.err.println("Arguments:  Expocode  Username ");
			System.err.println();
			System.err.println("Restores non-automated WOCE flags for the data currently given ");
			System.err.println("in the full-data DSG file for the indicated cruise.  If any WOCE ");
			System.err.println("flags change as a result of this action, a QC comment is added ");
			System.err.println("that WOCE flags were restored from resubmitted unchanged data.");
			System.err.println("This application does not generate new WOCE events, it only ");
			System.err.println("changes the WOCE flag for an existing WOCE event from an ");;
			System.err.println("\"old\" WOCE flag to the appropriate standard WOCE flag. ");
			System.err.println();
			System.err.println("NB: Use with caution as it only checks the data values recorded ");
			System.err.println("    in the WOCE event locations against data in the corresponding ");
			System.err.println("    DSG records.  However, this application does use the row ");
			System.err.println("    numbers given in the WOCE locations making this comparison, ");
			System.err.println("    and requires all WOCE locations to still be valid, thus greatly ");
			System.err.println("    reducing the chance of restoring WOCE flags that no longer ");
			System.err.println("    apply to the current dataset.");
			System.err.println();
			System.err.println("Expocode");
			System.err.println("    restore WOCE flags in the cruise with this expocode ");
			System.err.println("Username");
			System.err.println("    dashboard user requesting this update. ");
			System.err.println();
			System.exit(1);
		}
		String expocode = args[0].trim();
		String username = args[1].trim();

		DashboardConfigStore configStore = null;
		try {
			configStore = DashboardConfigStore.get();
		} catch (Exception ex) {
			System.err.println("Problems obtaining the default dashboard configuration: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		CruiseModifier restorer = new CruiseModifier(configStore);
		DatabaseRequestHandler dbHandler = configStore.getDatabaseRequestHandler();

		try {
			boolean changed = false;
			String socatVersion = null;
			try {
				System.out.println("Restoring any matching old non-automated WOCE flags for " + expocode);
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
				qcEvent.setComment("non-automated WOCE flags restored for resubmitted data");
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
			configStore.shutdown();
		}

		System.exit(0);
	}

}
