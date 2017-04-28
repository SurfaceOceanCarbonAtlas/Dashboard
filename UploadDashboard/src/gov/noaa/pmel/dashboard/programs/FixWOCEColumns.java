/**
 * 
 */
package gov.noaa.pmel.dashboard.programs;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

import gov.noaa.pmel.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.server.SocatTypes;
import gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.WoceEvent;
import gov.noaa.pmel.dashboard.shared.WoceFlag;
import gov.noaa.pmel.dashboard.shared.WoceType;

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
			System.err.println("    Expocode  DupWOCEColName  CO2WaterColNameStart  CO2AtmColNameStart");
			System.err.println("or");
			System.err.println("    Expocode  WOCEWaterColName  WOCEAtmColName");
			System.err.println();
			System.err.println("In the first case (four arguments), duplicate WOCE column names are given by ");
			System.err.println("the value given by DupWOCEColName.  The names for these columns following the ");
			System.err.println("columns with names starting with the values given by the CO2WaterColNameStart ");
			System.err.println("and CO2AtmColNameStart arguments (if not empty) are changed by appending these ");
			System.err.println("names to the duplicated name.  The types of these columns are changed to the ");
			System.err.println("appropriate WOCE flag type and these WOCE flags are added as PI-provided WOCE ");
			System.err.println("flags. ");
			System.err.println(); 
			System.err.println("In the second case (three arguments), the WOCE column names are the given ");
			System.err.println("unique names.  The types of these columns are changed to the appropriate ");
			System.err.println("WOCE flag type, and these WOCE flags are added as PI-provided WOCE flags. ");
			System.err.println();
			System.err.println("In either case, the DSG files need to be updated, using the UpdateWOCEFlags ");
			System.err.println("program/app.");
			System.exit(1);
		}

		String expocode = args[0];
		String dupColName;
		String co2WaterColNameStart;
		String co2AtmColNameStart;
		String woceWaterColName;
		String woceAtmColName;
		if ( args.length == 4 ) {
			dupColName = args[1];
			co2WaterColNameStart = args[2];
			co2AtmColNameStart = args[3];
			woceWaterColName = "";
			woceAtmColName = "";
		}
		else {
			dupColName = null;
			co2WaterColNameStart = null;
			co2AtmColNameStart = null;
			woceWaterColName = args[1];
			woceAtmColName = args[2];
		}

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
								woceWaterColName = (dupColName + "_" + co2WaterColNameStart)
													.replaceAll("\\s+", "_")
													.replaceAll("_+", "_");
								newUserColNames.add(woceWaterColName);
								commitMsg += "column " + dupColName + " following " + co2WaterColNameStart + 
										" changed to " + woceWaterColName + "; ";
							}
							else if ( prevColName.startsWith(co2AtmColNameStart) ) {
								// New unique name for WOCE CO2 atm
								woceAtmColName = (dupColName + "_" + co2AtmColNameStart)
												.replaceAll("\\s+", "_")
												.replaceAll("_+", "_");
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
						if ( woceWaterColName.equals(userColNames.get(k)) ) {
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
						if ( woceAtmColName.equals(userColNames.get(k)) ) {
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

				// Assign any user-provided WOCE-3 and WOCE-4 flags
				TreeSet<WoceType> woceFours = new TreeSet<WoceType>();
				TreeSet<WoceType> woceThrees = new TreeSet<WoceType>();
				ArrayList<DataColumnType> dataColTypes = cruiseData.getDataColTypes();
				int numCols = dataColTypes.size();
				for (int k = 0; k < numCols; k++) {
					DataColumnType colType = dataColTypes.get(k);
					if ( ! colType.isWoceType() )
						continue;
					for (int rowIdx = 0; rowIdx < cruiseData.getNumDataRows(); rowIdx++) {
						try {
							int value = Integer.parseInt(cruiseData.getDataValues().get(rowIdx).get(k));
							if ( value == 4 )
								woceFours.add(new WoceType(colType.getVarName(), null, rowIdx));
							else if ( value == 3 )
								woceThrees.add(new WoceType(colType.getVarName(), null, rowIdx));
							// Only handle 3 and 4
						} catch (NumberFormatException ex) {
							// Assuming a missing value
						}
					}
				}
				cruiseData.setUserWoceFours(woceFours);
				cruiseData.setUserWoceThrees(woceThrees);

				// Save the new data column names and types to file
				cruiseHandler.saveCruiseInfoToFile(cruiseData, commitMsg);
				cruiseHandler.saveCruiseDataToFile(cruiseData, commitMsg);

				// Ordered set of all WOCE flags for this dataset
				TreeSet<WoceFlag> woceFlagSet = new TreeSet<WoceFlag>();

				// Create WOCE flags for any PI-provided WOCE-3 flags 
				for ( WoceType uwoce : woceThrees ) {
					// Do not associate any data column with this WOCE flag 
					// (the previous column is not the problematic data for the WOCE)
					WoceFlag info = new WoceFlag(uwoce.getWoceName(), null, uwoce.getRowIndex());
					info.setFlag(DashboardUtils.WOCE_QUESTIONABLE);
					// Assume there are no user-provided comments for these WOCE flags
					info.setComment(DashboardUtils.PI_PROVIDED_WOCE_COMMENT_START + "3 flag");
					woceFlagSet.add(info);
				}

				// Create WOCE flags for any PI-provided WOCE-4 flags 
				for ( WoceType uwoce : woceFours ) {
					// Do not associate any data column with this WOCE flag 
					// (the previous column is not the problematic data for the WOCE)
					WoceFlag info = new WoceFlag(uwoce.getWoceName(), null, uwoce.getRowIndex());
					info.setFlag(DashboardUtils.WOCE_BAD);
					// Assume there are no user-provided comments for these WOCE flags
					info.setComment(DashboardUtils.PI_PROVIDED_WOCE_COMMENT_START + "4 flag");
					woceFlagSet.add(info);
				}

				if ( ! woceFlagSet.isEmpty() ) {
					// List of WOCE events (up to four events - WOCE-3 and/or WOCE-4 on CO2_water and/or CO2_atm)
					ArrayList<WoceEvent> woceList = new ArrayList<WoceEvent>();

					String version = cruiseData.getVersion();
					Date now = new Date();

					// Get the longitudes, latitude, times, and regions IDs 
					// from the full-data DSG file for this cruise
					double[][] lonlattime;
					char[] regionIDs;
					try {
						DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
						lonlattime = dsgHandler.readLonLatTimeDataValues(expocode);
						regionIDs = dsgHandler.readCharVarDataValues(expocode, SocatTypes.REGION_ID.getVarName());
					} catch ( Exception ex ) {
						throw new IllegalArgumentException("cannot read DSG file: " + ex.getMessage() + 
								"\nOriginal data properties file changed but no WOCE flags changed");
					}
					double[] longitudes = lonlattime[0];
					double[] latitudes = lonlattime[1];
					double[] times = lonlattime[2];

					String lastWoceName = null;
					Character lastFlag = null;
					String lastComment = null;
					ArrayList<DataLocation> locations = null;
					for ( WoceFlag info : woceFlagSet ) {

						// Check if a new WOCE event is needed
						String woceName = info.getWoceName();
						Character flag = info.getFlag();
						String comment = info.getComment();
						if ( ( ! woceName.equals(lastWoceName) ) ||
								( ! flag.equals(lastFlag) ) ||
								( ! comment.equals(lastComment) ) ) {
							lastWoceName = woceName;
							lastFlag = flag;
							lastComment = comment;

							WoceEvent woceEvent = new WoceEvent();
							woceEvent.setWoceName(woceName);
							woceEvent.setExpocode(expocode);
							woceEvent.setVersion(version);
							woceEvent.setFlag(flag);
							woceEvent.setFlagDate(now);
							woceEvent.setUsername(DashboardUtils.SANITY_CHECKER_USERNAME);
							woceEvent.setRealname(DashboardUtils.SANITY_CHECKER_REALNAME);
							woceEvent.setComment(comment);

							// Directly modify the locations ArrayList in this object
							locations = woceEvent.getLocations();
							woceList.add(woceEvent);
						}

						// Add a location for the current WOCE event
						DataLocation dataLoc = new DataLocation();
						int rowIdx = info.getRowIndex();
						dataLoc.setRowNumber(rowIdx + 1);
						dataLoc.setDataDate(new Date(Math.round(times[rowIdx] * 1000.0)));
						dataLoc.setLatitude(latitudes[rowIdx]);
						dataLoc.setLongitude(longitudes[rowIdx]);
						dataLoc.setRegionID(regionIDs[rowIdx]);
						locations.add(dataLoc);
					}

					// Add these WOCE events to the database
					try {
						DatabaseRequestHandler databaseHandler = configStore.getDatabaseRequestHandler();
						for ( WoceEvent woceEvent : woceList ) {
							databaseHandler.addWoceEvent(woceEvent);
						}
					} catch ( Exception ex ) {
						throw new IllegalArgumentException("Unable to add a WOCE flag to the database: " + ex.getMessage() + 
								"\nOriginal data properties file changed; any previous WOCE flags added to database");
					}
				}

			} finally {
				DashboardConfigStore.shutdown();
			}
		} catch ( Exception ex ) {
			System.err.println("ERROR - " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		System.exit(0);
	}

}
