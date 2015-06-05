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
 * Regenerates appropriate, non-automated WOCE flags for the current data 
 * of a cruise.  This is intended for cruises whose resubmitted data is a 
 * reordering of the previous version, possibly with some minor changes.
 * 
 * @author Karl Smith
 */
public class RegenerateWoceFlags {

	/**
	 * Regenerate WOCE flags for the current data of a cruise, 
	 * such as after resubmitting a reordering of the previous
	 * version of the data.
	 * 
	 * @param args
	 * 		Expocode - expocode of the cruise to restore.
	 * 		Username - dashboard user requesting this update (for the QC comment).
	 */
	public static void main(String[] args) {
		if ( args.length != 4 ) {
			System.err.println();
			System.err.println("Arguments:  Expocode  Username  MaxTimeDiff  MaxValueDiff");
			System.err.println();
			System.err.println("Regenerates appropriate, non-automated WOCE flags for the data ");
			System.err.println("currently given in the full-data DSG file for the indicated ");
			System.err.println("cruise.  If any WOCE flags change as a result of this action, ");
			System.err.println("a QC comment is added that WOCE flags were regenerated from a ");
			System.err.println("previous version of the data.  This application generates new ");
			System.err.println("WOCE events with information from old WOCE events for those ");
			System.err.println("locations that match. ");
			System.err.println();
			System.err.println("NB: Use with caution as it only checks the data values recorded ");
			System.err.println("    in the WOCE event locations against data in the corresponding ");
			System.err.println("    DSG records.  This application does not use the row numbers ");
			System.err.println("    given in the WOCE locations, nor does it require a match for ");
			System.err.println("    all locations, making this comparison more prone to assigning ");
			System.err.println("    invalid WOCE flags. ");
			System.err.println();
			System.err.println("Expocode");
			System.err.println("    regenerate WOCE flags in the cruise with this expocode ");
			System.err.println("Username");
			System.err.println("    dashboard user requesting this update. ");
			System.err.println("MaxTimeDiff");
			System.err.println("    data times can differ by up to this many seconds and still ");
			System.err.println("    match. Normally this should be 1.0, but if seconds are added, ");
			System.err.println("    this may need to be 60.0 ");
			System.err.println("MaxValueDiff");
			System.err.println("    data value causing the WOCE flag can differ by up to this much ");
			System.err.println("    in absolute value and still match.  Recommended value 1.0E-3 but ");
			System.err.println("    larger values may be needed for match updated fCO2_rec values. ");
			System.err.println();
			System.exit(1);
		}
		String expocode = args[0].trim();
		String username = args[1].trim();
		double maxTimeDiff = 1.0;
		try {
			maxTimeDiff = Double.valueOf(args[2]);
			if ( maxTimeDiff <= 0.0 )
				throw new IllegalArgumentException("not a positive value");
		} catch (Exception ex) {
			System.err.println("'" + args[2] + "' invalid: " + ex.getMessage());
			System.exit(1);
		}
		double maxValueDiff = 1.0E-3;
		try {
			maxValueDiff = Double.valueOf(args[3]);
			if ( maxValueDiff <= 0.0 )
				throw new IllegalArgumentException("not a positive value");
		} catch (Exception ex) {
			System.err.println("'" + args[3] + "' invalid: " + ex.getMessage());
			System.exit(1);
		}

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
				System.out.println("Regenerating any matching old non-automated WOCE flags for " + expocode);
				changed = restorer.regenerateWoceFlags(expocode, maxTimeDiff, maxValueDiff);
				socatVersion = restorer.getRestoredSocatVersion();
			} catch (Exception ex) {
				System.err.println(expocode + ": problems regenerating the WOCE flags - " + ex.getMessage());
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
				qcEvent.setComment("non-automated WOCE flags regenerated for resubmitted data");
				try {
					System.out.println("Adding regenerated-WOCE-flags QC comment for " + expocode);
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
