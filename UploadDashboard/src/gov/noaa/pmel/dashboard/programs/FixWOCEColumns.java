/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import java.util.ArrayList;

import gov.noaa.pmel.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

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
	 * In the second case (four arguments), the column names are assumed to already be unique. 
	 * The types are assigned using the previous column types, the appropriate WOCE flags added, 
	 * and the specified WOCE events undone.
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

			// If needed, make unique WOCE column names
			if ( dupColName != null ) {
				ArrayList<String> userColNames = cruiseData.getUserColNames();
				int numCols = userColNames.size();
				ArrayList<String> newUserColNames = new ArrayList<String>(numCols);
				// First column name cannot be made unique, and should not be WOCE in this scheme, 
				// because there is no previous column
				newUserColNames.add(userColNames.get(0));
				ArrayList<DataColumnType> dataColTypes = cruiseData.getDataColTypes();
				for (int k = 1; k < numCols; k++) {
					if ( dupColName.equalsIgnoreCase(userColNames.get(k)) ) {
						if ( ! DashboardUtils.OTHER.typeNameEquals(dataColTypes.get(k)) )
							throw new IllegalArgumentException("Duplicate name WOCE column type is not OTHER (" + 
									Integer.toString(k+1) + ", " + dataColTypes.get(k).getDisplayName() + ")\n" +
									"No changes made to this dataset.");
						// Make unique name using previous column name
						newUserColNames.add(dupColName + "_" + userColNames.get(k-1));
						DataColumnType prevType = dataColTypes.get(k-1);
						// Check if this is a WOCE on an aqueous or atmospheric CO2 value
						// TODO:
					}
					else {
						// Copy over the existing name and data type
						newUserColNames.add(userColNames.get(k));
					}
				}
				// Update the names and types in the DashboardCruiseWithData object as well as in the files
				cruiseData.setUserColNames(newUserColNames);
				String commitMsg = "Renamed WOCE data columns with name " + dupColName + " to make unique names";
				cruiseHandler.saveCruiseInfoToFile(cruiseData, commitMsg);
				cruiseHandler.saveCruiseDataToFile(cruiseData, commitMsg);
			}
			
			// Add the WOCE flags given in these columns
			if ( woceWaterColName != null ) {
				
			}
			if ( woceAtmColName != null ) {
				
			}
			
			// Iterate through the WOCE events to update the WOCE flags, skipping the specified events
			
		} finally {
			DashboardConfigStore.shutdown();
		}

		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
