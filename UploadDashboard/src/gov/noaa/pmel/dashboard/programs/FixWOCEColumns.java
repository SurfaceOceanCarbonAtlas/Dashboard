/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import java.util.ArrayList;

import gov.noaa.pmel.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
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
		if ( (args.length < 4) || (args.length > 5) ) {
			System.err.println();
			System.err.println("Arguments:  ");
			System.err.println("    Expocode  DupWOCEColName  CO2WaterColNameStart  CO2AtmColNameStart  UndoWOCEEvent1,UndoWOCEEvent2,...");
			System.err.println("or");
			System.err.println("    Expocode  WOCEWaterColName  WOCEAtmColName  UndoWOCEEvent1,UndoWOCEEvent2,...");
			System.err.println();
			System.err.println("In the first case (five arguments), the duplicated WOCE column names following the ");
			System.err.println("columns with names starting with CO2WaterColNameStart and CO2AtmColNameStart are changed ");
			System.err.println("by appending these names.  The types of these columns are changed to the appropriate WOCE ");
			System.err.println("flag type, these WOCE flags are added as PI-provided WOCE flags, and the specified WOCE ");
			System.err.println("events undone. ");
			System.err.println(); 
			System.err.println("In the second case (four arguments), the WOCE column names are the given unique names. ");
			System.err.println("The types of these columns are changed to the appropriate WOCE flag type, these WOCE flags ");
			System.err.println("are added as PI-provided WOCE flags, and the specified WOCE events undone. ");
			System.err.println();
			System.exit(1);
		}

		String expocode = args[0];
		String dupColName;
		String co2WaterColNameStart;
		String co2AtmColNameStart;
		String woceWaterColName;
		String woceAtmColName;
		String[] undoEvents;
		if ( args.length == 5 ) {
			dupColName = args[1];
			co2WaterColNameStart = args[2];
			co2AtmColNameStart = args[3];
			woceWaterColName = "";
			woceAtmColName = "";
			undoEvents = args[4].split(",");
		}
		else {
			dupColName = null;
			co2WaterColNameStart = null;
			co2AtmColNameStart = null;
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

			KnownDataTypes dataTypes = configStore.getKnownDataFileTypes();
			DataColumnType woceCO2WaterType = dataTypes.getDataColumnType("WOCE_CO2_water");
			if ( woceCO2WaterType == null )
				throw new RuntimeException("WOCE_CO2_water not a recognized data file data column type");
			DataColumnType woceCO2AtmType = dataTypes.getDataColumnType("WOCE_CO2_atm");
			if ( woceCO2AtmType == null )
				throw new RuntimeException("WOCE_CO2_atm not a recognized data file data column type");

			CruiseFileHandler cruiseHandler = configStore.getCruiseFileHandler();
			DashboardCruiseWithData cruiseData = cruiseHandler.getCruiseDataFromFiles(expocode, 0, -1);

			String commitMsg = "";

			// If needed, make unique WOCE column names
			if ( dupColName != null ) {
				ArrayList<String> userColNames = cruiseData.getUserColNames();
				int numCols = userColNames.size();
				ArrayList<String> newUserColNames = new ArrayList<String>(numCols);
				// First column name cannot be made unique, and should not be WOCE in this scheme, 
				// because there is no previous column
				newUserColNames.add(userColNames.get(0));
				for (int k = 1; k < numCols; k++) {
					String thisColName = userColNames.get(k);
					if ( dupColName.equals(thisColName) ) {
						String prevColName = userColNames.get(k-1);
						if ( prevColName.startsWith(co2WaterColNameStart) ) {
							// New unique name for WOCE CO2 water
							woceWaterColName = dupColName + "_" + co2WaterColNameStart;
							newUserColNames.add(woceWaterColName);
							commitMsg += "column " + dupColName + " following " + co2WaterColNameStart + 
									" changed to " + woceWaterColName + "; ";
						}
						else if ( prevColName.startsWith(co2AtmColNameStart) ) {
							// New unique name for WOCE CO2 atm
							woceAtmColName = dupColName + "_" + co2AtmColNameStart;
							newUserColNames.add(woceAtmColName);
							commitMsg += "column " + dupColName + " following " + co2AtmColNameStart + 
									" changed to " + woceAtmColName + "; ";
						}
						else {
							// Some other WOCE flag; copy over the existing (duplicate) name
							newUserColNames.add(thisColName);
						}
					}
					else {
						// Copy over the existing name
						newUserColNames.add(thisColName);
					}
				}
				if ( woceWaterColName.isEmpty() && woceAtmColName.isEmpty() )
					throw new IllegalArgumentException("No duplicated WOCE column names changed\n" +
							"No changes made to this dataset.");

				// Update the names in the DashboardCruiseWithData object
				cruiseData.setUserColNames(newUserColNames);
			}
			
			// Add the WOCE flags given in the named columns
			if ( ! woceWaterColName.isEmpty() ) {
				ArrayList<String> userColNames = cruiseData.getUserColNames();
				ArrayList<DataColumnType> dataColTypes = cruiseData.getDataColTypes();
				int numCols = dataColTypes.size();
				ArrayList<DataColumnType> newDataColTypes = new ArrayList<DataColumnType>(numCols);
				boolean colFound = false;
				for (int k = 0; k < numCols; k++) {
					if ( woceWaterColName.equals(userColNames) ) {
						// Change to the WOCE CO2 water type
						if ( ! DashboardUtils.OTHER.typeNameEquals(dataColTypes.get(k)) )
							throw new IllegalArgumentException("WOCE CO2 water column does not have type OTHER (colNum = " + 
									Integer.toString(k+1) + ", type = " + dataColTypes.get(k).getDisplayName() + ")\n" +
									"No changes made to this dataset.");
						if ( colFound )
							throw new IllegalArgumentException("More than one WOCE CO2 water column named " + 
									woceWaterColName + "\nNo changes made to this dataset.");
						newDataColTypes.add(woceCO2WaterType);
						colFound = true;
					}
					else {
						// Copy over the existing data type
						newDataColTypes.add(dataColTypes.get(k));
					}
				}
				if ( ! colFound )
					throw new IllegalArgumentException("No column found with name " +
							woceWaterColName + "\nNo changes made to this dataset.");
				commitMsg += "type of column " + woceWaterColName + " changed to WOCE_CO2_water; ";
				cruiseData.setDataColTypes(newDataColTypes);
			}
			if ( ! woceAtmColName.isEmpty() ) {
				ArrayList<String> userColNames = cruiseData.getUserColNames();
				ArrayList<DataColumnType> dataColTypes = cruiseData.getDataColTypes();
				int numCols = dataColTypes.size();
				ArrayList<DataColumnType> newDataColTypes = new ArrayList<DataColumnType>(numCols);
				boolean colFound = false;
				for (int k = 0; k < numCols; k++) {
					if ( woceAtmColName.equals(userColNames) ) {
						// Change to the WOCE CO2 atm type
						if ( ! DashboardUtils.OTHER.typeNameEquals(dataColTypes.get(k)) )
							throw new IllegalArgumentException("WOCE CO2 atm column does not have type OTHER (colNum = " + 
									Integer.toString(k+1) + ", type = " + dataColTypes.get(k).getDisplayName() + ")\n" +
									"No changes made to this dataset.");
						if ( colFound )
							throw new IllegalArgumentException("More than one WOCE CO2 atm column named " + 
									woceAtmColName + "\nNo changes made to this dataset.");
						newDataColTypes.add(woceCO2AtmType);
						colFound = true;
					}
					else {
						// Copy over the existing data type
						newDataColTypes.add(dataColTypes.get(k));
					}
				}
				if ( ! colFound )
					throw new IllegalArgumentException("No column found with name " +
							woceAtmColName + "\nNo changes made to this dataset.");
				commitMsg += "type of column " + woceAtmColName + " changed to WOCE_CO2_atm; ";
				cruiseData.setDataColTypes(newDataColTypes);
			}

			if ( commitMsg.isEmpty() )
				throw new IllegalArgumentException("No changes made to this dataset.");

			// Save the new data column names and types to file
			cruiseHandler.saveCruiseInfoToFile(cruiseData, commitMsg);
			cruiseHandler.saveCruiseDataToFile(cruiseData, commitMsg);

			// Create WOCE Events for these new WOCE columns

			// Iterate through the WOCE events to update the WOCE flags, skipping the specified events
			
		} finally {
			DashboardConfigStore.shutdown();
		}

		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
