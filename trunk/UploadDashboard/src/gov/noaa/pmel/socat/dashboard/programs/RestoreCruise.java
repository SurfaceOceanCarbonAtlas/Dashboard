/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.IOException;
import java.sql.SQLException;
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

	private static final String LONGITUDE_VAR_NAME = Constants.SHORT_NAMES.get(Constants.longitude_VARNAME);
	private static final String LATITUDE_VAR_NAME = Constants.SHORT_NAMES.get(Constants.latitude_VARNAME);
	private static final String TIME_VAR_NAME = Constants.SHORT_NAMES.get(Constants.time_VARNAME);
	private static final String WOCE_CO2_WATER_NAME = Constants.SHORT_NAMES.get(Constants.woceCO2Water_VARNAME);

	DatabaseRequestHandler databaseHandler;
	DsgNcFileHandler dsgHandler;

	public RestoreCruise(DashboardDataStore dataStore) {
		databaseHandler = dataStore.getDatabaseRequestHandler();
		dsgHandler = dataStore.getDsgNcFileHandler();
	}

	public String restoreWoceFlags(String expocode) 
			throws IllegalArgumentException, SQLException, IOException {
		CruiseDsgNcFile dsgFile = dsgHandler.getDsgNcFile(expocode);
		dsgFile.read(true);
		SocatMetadata metaData = dsgFile.getMetadata();
		String socatVersion = metaData.getSocatVersion();

		// Read longitudes, latitude, and times for all data
		double[] longitudes = dsgFile.readDoubleVarDataValues(LONGITUDE_VAR_NAME);
		int numData = longitudes.length;
		double[] latitudes = dsgFile.readDoubleVarDataValues(LATITUDE_VAR_NAME);
		if ( latitudes.length != numData )
			throw new RuntimeException("Unexpected mismatch in number of longitudes and latitudes");
		double[] times = dsgFile.readDoubleVarDataValues(TIME_VAR_NAME);
		if ( times.length != numData )
			throw new RuntimeException("Unexpected mismatch in the number of longitudes and times");
		char[] woceFlags = dsgFile.readCharVarDataValues(WOCE_CO2_WATER_NAME);

		boolean changed = false;
		for ( SocatWoceEvent woceEvent : databaseHandler.getWoceEvents(expocode, false) ) {
			// SOCAT version for this WOCE event must match that of the data
			if ( ! socatVersion.equals(woceEvent.getSocatVersion()) )
				continue;
			// WOCE flag must be one of the "old" flags
			Character woceFlag = woceEvent.getFlag();
			if ( ! (woceFlag.equals(SocatWoceEvent.OLD_WOCE_BAD) ||
					woceFlag.equals(SocatWoceEvent.OLD_WOCE_QUESTIONABLE) ||
					woceFlag.equals(SocatWoceEvent.OLD_WOCE_NOT_CHECKED) ||
					woceFlag.equals(SocatWoceEvent.OLD_WOCE_GOOD) ||
					woceFlag.equals(SocatWoceEvent.OLD_WOCE_NO_DATA)) )
				continue;
			// Get the data associated with this WOCE event
			String dataName = woceEvent.getDataVarName();
			double[] dataVals;
			if ( dataName.isEmpty() || dataName.equals(Constants.geoposition_VARNAME))
				dataVals = null;
			else
				dataVals = dsgFile.readDoubleVarDataValues(dataName);

			// Verify that all the location data matches
			boolean matches = true;
			for ( DataLocation dataLoc : woceEvent.getLocations() ) {
				int rowNum = dataLoc.getRowNumber();
				if ( (rowNum < 1) || (rowNum > numData) ) {
					matches = false;
					break;
				}
				rowNum--;
				// Check that the data date is within one second of each other
				if ( ! DashboardUtils.closeTo(times[rowNum], dataLoc.getDataDate().getTime() / 1000.0, 0.0, 1.0) ) {
					matches = false;
					break;
				}
				// Check that the latitude is within 1.0E-5 degrees of each other
				if ( ! DashboardUtils.closeTo(latitudes[rowNum], dataLoc.getLatitude(), 0.0, 1.0E-5) ) {
					matches = false;
					break;
				}
				// Check that the longitude is within 1.0E-5 degrees of each other
				if ( ! DashboardUtils.longitudeCloseTo(longitudes[rowNum], dataLoc.getLongitude(), 0.0, 1.0E-5) ) {
					matches = false;
					break;
				}
				// If given, check that the data values are within 1.0E-5 of each other
				if ( dataVals != null ) {
					if ( ! DashboardUtils.closeTo(dataVals[rowNum], dataLoc.getDataValue(), 1.0E-5, 1.0E-5) ) {
						matches = false;
						break;
					}
				}
			}
			if ( matches ) {
				Character newFlag = databaseHandler.restoreWoceEvent(woceEvent);
				System.out.println("WOCE flag of '" + newFlag + "' assigned to WOCE event " + 
						Long.toString(woceEvent.getId()) + " for " + expocode);
				for ( DataLocation dataLoc : woceEvent.getLocations() ) {
					woceFlags[dataLoc.getRowNumber() - 1] = newFlag;
				}
				changed = true;
			}
		}
		if ( changed ) {
			// Update the full-data DSG file with the reassigned WOCE flags
			dsgFile.writeCharVarDataValues(WOCE_CO2_WATER_NAME, woceFlags);
			// Generate the decimated DSG file from the updated full-data DSG file
			dsgHandler.decimateCruise(expocode);
		}
		return socatVersion;
	}

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
			System.err.println("    expocodes of the cruise to be restored. ");
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

		DashboardDataStore dataStore = null;
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			System.err.println("Problems obtaining the default dashboard " +
					"configuration: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		String removeSocatVersion = dataStore.getSocatUploadVersion();
		ResubmitCruises resubmitter = new ResubmitCruises(dataStore);
		DatabaseRequestHandler dbHandler = dataStore.getDatabaseRequestHandler();
		RestoreCruise restorer = new RestoreCruise(dataStore);
		DsgNcFileHandler dsgHandler = dataStore.getDsgNcFileHandler();

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
				restoredVersion = restorer.restoreWoceFlags(expo);
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
			dataStore.shutdown();
		}

		System.exit(0);
	}

}
