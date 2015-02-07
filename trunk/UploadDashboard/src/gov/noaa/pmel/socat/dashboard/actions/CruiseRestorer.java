/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Restores the cruise DSG files to the data currently given in the text data file.  
 * Removes WOCE flags and QC flags for the current SOCAT upload version and restores 
 * any old WOCE flags applicable to the current data.  Adds a QC comment that the 
 * cruise data and WOCE flags were restored.
 * 
 * @author Karl Smith
 */
public class CruiseRestorer {

	private static final String LONGITUDE_VAR_NAME = Constants.SHORT_NAMES.get(Constants.longitude_VARNAME);
	private static final String LATITUDE_VAR_NAME = Constants.SHORT_NAMES.get(Constants.latitude_VARNAME);
	private static final String TIME_VAR_NAME = Constants.SHORT_NAMES.get(Constants.time_VARNAME);
	private static final String WOCE_CO2_WATER_NAME = Constants.SHORT_NAMES.get(Constants.woceCO2Water_VARNAME);

	DatabaseRequestHandler databaseHandler;
	DsgNcFileHandler dsgHandler;
	String socatVersion;

	/**
	 * Restores cruises using the handlers provided by the given DashboardDataStore
	 * 
	 * @param dataStore
	 * 		DashboardDataStore to use
	 * 		
	 */
	public CruiseRestorer(DashboardDataStore dataStore) {
		databaseHandler = dataStore.getDatabaseRequestHandler();
		dsgHandler = dataStore.getDsgNcFileHandler();
		socatVersion = null;
	}

	/**
	 * Restores any WOCE flags that match the current data, and updates the current WOCE
	 * flags assigned in the full-data and decimated data DSG files to the latest values
	 * from the database.
	 * 
	 * @param expocode
	 * 		restore and update WOCE flags for this cruise
	 * @return
	 * 		true is any WOCE flags in the DSG files changed;
	 * 		otherwise, false
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 * @throws SQLException
	 * 		if problems reading from the database
	 * @throws IOException
	 * 		if problems reading or writing to the DSG files
	 */
	public boolean restoreWoceFlags(String expocode) 
			throws IllegalArgumentException, SQLException, IOException {
		CruiseDsgNcFile dsgFile = dsgHandler.getDsgNcFile(expocode);
		dsgFile.read(true);
		SocatMetadata metaData = dsgFile.getMetadata();
		socatVersion = metaData.getSocatVersion();

		// Read longitudes, latitude, and times for all data
		double[] longitudes = dsgFile.readDoubleVarDataValues(LONGITUDE_VAR_NAME);
		int numData = longitudes.length;
		double[] latitudes = dsgFile.readDoubleVarDataValues(LATITUDE_VAR_NAME);
		if ( latitudes.length != numData )
			throw new RuntimeException("Unexpected mismatch in number of longitudes and latitudes");
		double[] times = dsgFile.readDoubleVarDataValues(TIME_VAR_NAME);
		if ( times.length != numData )
			throw new RuntimeException("Unexpected mismatch in the number of longitudes and times");
		char[] currentWoceFlags = dsgFile.readCharVarDataValues(WOCE_CO2_WATER_NAME);
		char[] revisedWoceFlags = Arrays.copyOf(currentWoceFlags, currentWoceFlags.length);

		// Get all WOCE events for this expocode, order so the latest are last
		for ( SocatWoceEvent woceEvent : databaseHandler.getWoceEvents(expocode, false) ) {
			// SOCAT version for this WOCE event must match that of the data
			if ( ! socatVersion.equals(woceEvent.getSocatVersion()) )
				continue;
			Character woceFlag = woceEvent.getFlag();
			if ( woceFlag.equals(SocatWoceEvent.OLD_WOCE_BAD) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_QUESTIONABLE) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_NOT_CHECKED) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_GOOD) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_NO_DATA) ) {
				// An "old" WOCE flag check if it should be restored

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
						revisedWoceFlags[dataLoc.getRowNumber() - 1] = newFlag;
					}
				}
			}
			else if ( woceFlag.equals(SocatWoceEvent.WOCE_BAD) ||
					  woceFlag.equals(SocatWoceEvent.WOCE_QUESTIONABLE) ||
					  woceFlag.equals(SocatWoceEvent.WOCE_NOT_CHECKED) ||
					  woceFlag.equals(SocatWoceEvent.WOCE_GOOD) ||
					  woceFlag.equals(SocatWoceEvent.WOCE_NO_DATA) ) {
				// A current WOCE flag - make sure it is assigned in the revised WOCE flags 
				// (may overwrite a restored flag assigned earlier)
				for ( DataLocation dataLoc : woceEvent.getLocations() ) {
					revisedWoceFlags[dataLoc.getRowNumber() - 1] = woceFlag;
				}
			}
		}
		boolean changed = false;
		if ( ! Arrays.equals(revisedWoceFlags, currentWoceFlags) ) {
			changed = true;
			// Update the full-data DSG file with the reassigned WOCE flags
			dsgFile.writeCharVarDataValues(WOCE_CO2_WATER_NAME, revisedWoceFlags);
			// Generate the decimated DSG file from the updated full-data DSG file
			dsgHandler.decimateCruise(expocode);
		}
		return changed;
	}

	/**
	 * @return
	 * 		the SOCAT version from the full-data DSG file of the cruise 
	 * 		specified in the latest call to @link {@link #restoreWoceFlags(String)}
	 */
	public String getRestoredSocatVersion() {
		return socatVersion;
	}

}
