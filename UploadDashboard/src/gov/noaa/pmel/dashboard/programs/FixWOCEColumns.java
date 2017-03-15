/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData;

/**
 * App to add WOCE flags given in data columns under a possibly duplicate column name.
 * For example: the data column names may be: ..., xCO2SW, QF, xCO2Atm, QF, ...
 * and the QF columns marked as OTHER due to duplicate name problems, when, in fact, 
 * they are WOCE flags for the data in the previous column.  So the changes needed are:
 * 		column names changed to unique names based on the previous column name
 * 		(WOCE) types assigned based on the previous column type
 * 		add PI-provided WOCE flags using these WOCE column(s)
 * 
 * This problem was discovered due to a reviewer giving WOCE flags to incorrect data rows
 * based on what was in the metadata documents.  So this should also allow reversing specific
 * WOCE events.
 *   
 * @author Karl Smith
 */
public class FixWOCEColumns {

	/**
	 * @param args
	 * 		expocode  dup_WOCE_colname  undo_WOCEEvent1,undo_WOCEEvent2,...
	 * or
	 * 		expocode  WOCE_water_colname  WOCE_atm_colname  undo_WOCEEvent1,undo_WOCEEvent2,...
	 * 
	 * In the first case (three arguments), the duplicate column names and types are changed 
	 * using the previous column names and types, the appropriate WOCE flags added, and the 
	 * specified WOCE events undone.
	 * 
	 * In the second case (four arguments), the column names are already unique but the types
	 * are assigned using the preivous column types, the appropriate WOCE flags added, and the
	 * specified WOCE events undone.
	 */
	public static void main(String[] args) {
		if ( (args.length < 3) || (args.length > 4) ) {
			System.err.println();
			System.err.println("Arguments:  ");
			System.err.println("    Expocode  DupWOCEColName  UndoWOCEEvent1,UndoWOCEEvent2,...");
			System.err.println("or");
			System.err.println("    Expocode  WOCEWaterColName  WOCEAtmColName  UndoWOCEEvent1,UndoWOCEEvent2,...");
			System.err.println();
			System.err.println("In the first case (three arguments), the duplicate column names and types are changed ");
			System.err.println("using the previous column names and types, the appropriate WOCE flags added, and the "); 
			System.err.println("specified WOCE events undone. ");
			System.err.println(); 
			System.err.println("In the second case (four arguments), the column names are already unique but the types ");
			System.err.println("are assigned using the preivous column types, the appropriate WOCE flags added, and the ");
			System.err.println("specified WOCE events undone. ");
			System.err.println();
			System.exit(1);
		}

		String expocode = args[0];
		String dupColName;
		String woceWaterColName;
		String woceAtmColName;
		String[] undoEvents;
		if ( args.length == 3 ) {
			dupColName = args[1];
			woceWaterColName = null;
			woceAtmColName = null;
			undoEvents = args[2].split(",");
		}
		else {
			dupColName = null;
			woceWaterColName = args[1];
			woceAtmColName = args[2];
			undoEvents = args[3].split(",");
		}

		boolean success = true;

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {

			CruiseFileHandler cruiseHandler = configStore.getCruiseFileHandler();
			DashboardCruiseWithData cruiseData = cruiseHandler.getCruiseDataFromFiles(expocode, 0, -1);

		} finally {
			DashboardConfigStore.shutdown();
		}

		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
