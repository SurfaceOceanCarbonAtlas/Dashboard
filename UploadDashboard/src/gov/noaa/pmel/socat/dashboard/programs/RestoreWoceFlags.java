/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeSet;

/**
 * Restores old WOCE flags for current full-data DSG file.
 * 
 * @author Karl Smith
 */
public class RestoreWoceFlags {

	private static final String LONGITUDE_VAR_NAME = Constants.SHORT_NAMES.get(Constants.longitude_VARNAME);
	private static final String LATITUDE_VAR_NAME = Constants.SHORT_NAMES.get(Constants.latitude_VARNAME);
	private static final String TIME_VAR_NAME = Constants.SHORT_NAMES.get(Constants.time_VARNAME);
	private static final String WOCE_CO2_WATER_NAME = Constants.SHORT_NAMES.get(Constants.woceCO2Water_VARNAME);

	DatabaseRequestHandler databaseHandler;
	DsgNcFileHandler dsgHandler;

	public RestoreWoceFlags(DashboardDataStore dataStore) {
		databaseHandler = dataStore.getDatabaseRequestHandler();
		dsgHandler = dataStore.getDsgNcFileHandler();
	}

	public boolean restoreWoceFlags(String expocode) 
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
			// Decimated-data DSG file not updated (too complicated); manually decimate the data
			System.out.println("Need to regenerate the decimated-data DSG file for " + expocode);
		}
		return changed;
	}

	/**
	 * @param args
	 * 		ExpocodesFile - restore appropriate WOCE flags for cruises with expocodes given in this file
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("ExpocodesFile");
			System.err.println("    is a file containing expocodes, one per line, of cruises ");
			System.err.println("    to search for WOCE flags to be restored. ");
			System.err.println();
			System.exit(1);
		}
		String exposFilename = args[0];

		TreeSet<String> expocodes = new TreeSet<String>();
		try {
			BufferedReader reader = 
					new BufferedReader(new FileReader(exposFilename));
			try {
				String dataline = reader.readLine();
				while ( dataline != null ) {
					dataline = dataline.trim().toUpperCase();
					if ( ! dataline.isEmpty() )
						expocodes.add(dataline);
					dataline = reader.readLine();
				}
			} finally {
				reader.close();
			}
		} catch (Exception ex) {
			System.err.println("Problems reading the file of expocodes '" + 
					exposFilename + "': " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		DashboardDataStore dataStore = null;
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			System.err.println("Problems obtaining the default dashboard " +
					"configuration: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		RestoreWoceFlags restorer = new RestoreWoceFlags(dataStore);

		boolean success = true;
		try {
			for ( String expo : expocodes ) {
				try {
					restorer.restoreWoceFlags(expo);
				} catch (Exception ex) {
					System.err.println(expo + ": problems restoring the WOCE flags - " + ex.getMessage());
					ex.printStackTrace();
					success = false;
				}
			}
		} finally {
			dataStore.shutdown();
		}

		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
