/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Restores the cruise DSG files to the data currently given in the text data file.  
 * Removes WOCE flags and QC flags for the current SOCAT upload version and restores 
 * any old WOCE flags applicable to the current data.  Adds a QC comment that the 
 * cruise data and WOCE flags were restored.
 * 
 * @author Karl Smith
 */
public class CruiseModifier {

	private static final String LONGITUDE_VAR_NAME = Constants.SHORT_NAMES.get(Constants.longitude_VARNAME);
	private static final String LATITUDE_VAR_NAME = Constants.SHORT_NAMES.get(Constants.latitude_VARNAME);
	private static final String TIME_VAR_NAME = Constants.SHORT_NAMES.get(Constants.time_VARNAME);
	private static final String REGION_ID_VAR_NAME = Constants.SHORT_NAMES.get(Constants.regionID_VARNAME);
	private static final String WOCE_CO2_WATER_NAME = Constants.SHORT_NAMES.get(Constants.woceCO2Water_VARNAME);

	CruiseFileHandler cruiseHandler;
	CheckerMessageHandler msgHandler;
	MetadataFileHandler metadataHandler;
	DsgNcFileHandler dsgHandler;
	DatabaseRequestHandler databaseHandler;
	String socatUploadVersion;
	String restoredSocatVersion;

	/**
	 * Restores cruises using the handlers provided by the given DashboardDataStore
	 * 
	 * @param configStore
	 * 		DashboardDataStore to use
	 * 		
	 */
	public CruiseModifier(DashboardConfigStore configStore) {
		cruiseHandler = configStore.getCruiseFileHandler();
		msgHandler = configStore.getCheckerMsgHandler();
		metadataHandler = configStore.getMetadataFileHandler();
		dsgHandler = configStore.getDsgNcFileHandler();
		databaseHandler = configStore.getDatabaseRequestHandler();
		socatUploadVersion = configStore.getSocatUploadVersion();
		restoredSocatVersion = null;
	}

	/**
	 * Appropriately renames dashboard cruise files, as well as SOCAT files and 
	 * database flags if the cruise has been submitted.  If an exception is thrown,
	 * the system is likely have a corrupt mix of renamed and original-name files.
	 * 
	 * @param oldExpocode
	 * 		current expocode for the cruise
	 * @param newExpocode
	 * 		new expocode to use for the cruise
	 * @param username
	 * 		username to associate with the rename QC and WOCE events
	 * @throws IllegalArgumentException
	 * 		if the username is not an admin,
	 * 		if either expocode is invalid,
	 * 		if cruise files for the old expocode do not exist,
	 * 		if any files for the new expocode already exist
	 * @throws IOException
	 * 		if updating a file with the new expocode throws one
	 * @throws SQLException 
	 * 		if username is not a known user, or
	 * 		if accessing or updating the database throws one
	 */
	public void renameCruise(String oldExpocode, String newExpocode, String username) 
			throws IllegalArgumentException, IOException, SQLException {
		// check and standardized the expocodes
		String oldExpo = DashboardServerUtils.checkExpocode(oldExpocode);
		String newExpo = DashboardServerUtils.checkExpocode(newExpocode);
		// rename the cruise data and info files; update the expocode in the data file
		cruiseHandler.renameCruiseFiles(oldExpo, newExpo);
		// rename the SanityChecker messages file, if it exists
		msgHandler.renameMsgsFile(oldExpo, newExpo);
		// TODO: rename the WOCE messages file, if it exists
		// rename metadata files; update the expocode in the OME metadata
		metadataHandler.renameMetadataFiles(oldExpo, newExpo);
		// rename the DSG and decimated DSG files; update the expocode in these files
		dsgHandler.renameDsgFiles(oldExpo, newExpo);
		// generate a rename QC comment and modify expocodes for the flags
		databaseHandler.renameCruiseFlags(oldExpo, newExpo, socatUploadVersion, username);
	}

	/**
	 * @return
	 * 		the SOCAT version from the full-data DSG file of the cruise 
	 * 		specified in the latest call to {@link #restoreWoceFlags(String)}
	 * 		or {@link #regenerateWoceFlags(String)}
	 */
	public String getRestoredSocatVersion() {
		return restoredSocatVersion;
	}

	/**
	 * Restores any non-automated WOCE events that match the current data, and updates 
	 * the current WOCE flags assigned in the full-data and decimated data DSG files 
	 * to the latest values from the database.  All data, including row numbers, in 
	 * WOCE locations must match, and all WOCE locations for an event must match for 
	 * a WOCE event to be restored. 
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
		restoredSocatVersion = metaData.getSocatVersion();

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
			if ( ! restoredSocatVersion.equals(woceEvent.getSocatVersion()) )
				continue;
			// Skip WOCE events generated by the automated data-checker;
			// they should have been regenerated when resubmitted
			if ( woceEvent.getUsername().equals(SocatEvent.SANITY_CHECKER_USERNAME) )
				continue;
			// Find "old" WOCE events and check if it should be restored
			Character woceFlag = woceEvent.getFlag();
			if ( woceFlag.equals(SocatWoceEvent.OLD_WOCE_BAD) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_QUESTIONABLE) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_NOT_CHECKED) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_GOOD) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_NO_DATA) ) {

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
			System.out.println("WOCE flags updated in the DSG files for " + expocode);
		}
		return changed;
	}

	/**
	 * Regenerates any non-automated WOCE flags, creating new WOCE events, that match 
	 * the current data, and updates the current WOCE flags assigned in the full-data 
	 * and decimated data DSG files to the latest values from the database.  The row 
	 * number of old WOCE locations are not used, and not all WOCE locations do not 
	 * have to match in order for a WOCE flag to be regenerated. 
	 * 
	 * @param expocode
	 * 		restore and update WOCE flags for this cruise
	 * @param maxTimeDiff
	 * 		data times can differ by up to this many seconds and still match.
	 * 		Normally this should be 0.5 or 1.0, but when seconds are added, 
	 * 		this may need to be 60.0
	 * @param maxValueDiff
	 * 		data value causing the WOCE flag can differ by up to this much
	 * 		in absolute value and still match.  Recommended value 1.0E-3 but
	 * 		larger values may be needed for match updated fCO2_rec values
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
	public boolean regenerateWoceFlags(String expocode, double maxTimeDiff, double maxValueDiff)
		throws IllegalArgumentException, SQLException, IOException {
		CruiseDsgNcFile dsgFile = dsgHandler.getDsgNcFile(expocode);
		dsgFile.read(true);
		SocatMetadata metaData = dsgFile.getMetadata();
		restoredSocatVersion = metaData.getSocatVersion();
		
		// Read longitudes, latitude, and times for all data
		double[] longitudes = dsgFile.readDoubleVarDataValues(LONGITUDE_VAR_NAME);
		int numData = longitudes.length;
		double[] latitudes = dsgFile.readDoubleVarDataValues(LATITUDE_VAR_NAME);
		if ( latitudes.length != numData )
			throw new RuntimeException("Unexpected mismatch in number of longitudes and latitudes");
		double[] times = dsgFile.readDoubleVarDataValues(TIME_VAR_NAME);
		if ( times.length != numData )
			throw new RuntimeException("Unexpected mismatch in the number of longitudes and times");
		char[] regionIDs = dsgFile.readCharVarDataValues(REGION_ID_VAR_NAME);
		char[] currentWoceFlags = dsgFile.readCharVarDataValues(WOCE_CO2_WATER_NAME);
		char[] revisedWoceFlags = Arrays.copyOf(currentWoceFlags, currentWoceFlags.length);

		// Get all WOCE events for this expocode, order so the latest are last
		for ( SocatWoceEvent woceEvent : databaseHandler.getWoceEvents(expocode, false) ) {
			// Skip WOCE events generated by the automated data-checker;
			// they should have been regenerated, if appropriate, when resubmitted
			if ( woceEvent.getUsername().equals(SocatEvent.SANITY_CHECKER_USERNAME) )
				continue;

			// Find "old" WOCE events and check if it should be regenerated
			Character woceFlag = woceEvent.getFlag();
			if ( woceFlag.equals(SocatWoceEvent.OLD_WOCE_BAD) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_QUESTIONABLE) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_NOT_CHECKED) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_GOOD) ||
				 woceFlag.equals(SocatWoceEvent.OLD_WOCE_NO_DATA) ) {

				// Get the data associated with this WOCE event
				String dataName = woceEvent.getDataVarName();
				double[] dataVals;
				if ( dataName.isEmpty() || dataName.equals(Constants.geoposition_VARNAME))
					dataVals = null;
				else
					dataVals = dsgFile.readDoubleVarDataValues(dataName);

				// Get any data locations that still match
				ArrayList<DataLocation> newLocations = new ArrayList<DataLocation>();
				int rowNum = 0;
				for ( DataLocation dataLoc : woceEvent.getLocations() ) {
					Double woceLocTime = dataLoc.getDataDate().getTime() / 1000.0;
					Double woceLocLat = dataLoc.getLatitude();
					Double woceLocLon = dataLoc.getLongitude();
					Double woceLocDataValue = dataLoc.getDataValue();
					// Ignore row number as the data may be (probably is) reordered
					// Check for a data point where:
					//    the data date is within allowedTimeDiff seconds of that for the WOCE location,
					//    the latitude is within 1.0E-5 degrees of that for the WOCE location,
					//    the longitude is within 1.0E-5 degrees of that for the WOCE location, and
					//    the data value, if given, is within 1.0E-5 relative, and maxValueDiff absolute,
					//        of that for the WOCE location
					boolean matchFound = false;
					int rowNumStart = rowNum;
					while ( rowNum < numData ) {
						if ( DashboardUtils.closeTo(times[rowNum], woceLocTime, 0.0, maxTimeDiff) &&
							 DashboardUtils.closeTo(latitudes[rowNum], woceLocLat, 0.0, 1.0E-5) &&
							 DashboardUtils.longitudeCloseTo(longitudes[rowNum], woceLocLon, 0.0, 1.0E-5) &&
							 ( (dataVals == null) ||
							   DashboardUtils.closeTo(dataVals[rowNum], woceLocDataValue, 1.0E-5, maxValueDiff) ) ) {
							matchFound = true;
							break;
						}
						rowNum++;
					}
					if ( ! matchFound ) {
						rowNum = 0;
						while ( rowNum < rowNumStart ) {
							if ( DashboardUtils.closeTo(times[rowNum], woceLocTime, 0.0, 1.0) &&
								 DashboardUtils.closeTo(latitudes[rowNum], woceLocLat, 0.0, 1.0E-5) &&
								 DashboardUtils.longitudeCloseTo(longitudes[rowNum], woceLocLon, 0.0, 1.0E-5) &&
								 ( (dataVals == null) ||
									DashboardUtils.closeTo(dataVals[rowNum], woceLocDataValue, 1.0E-5, 1.0E-5) ) ) {
								matchFound = true;
								break;
							}
							rowNum++;
						}
					}
					if ( matchFound ) {
						DataLocation newLoc = new DataLocation();
						newLoc.setDataDate(new Date(Math.round(times[rowNum] * 1000.0)));
						newLoc.setLatitude(latitudes[rowNum]);
						newLoc.setLongitude(longitudes[rowNum]);
						newLoc.setRegionID(regionIDs[rowNum]);
						if ( dataVals != null )
							newLoc.setDataValue(dataVals[rowNum]);
						rowNum++;
						newLoc.setRowNumber(rowNum);
						newLocations.add(newLoc);
					}
				}
				if ( ! newLocations.isEmpty() ) {
					Character newFlag;
					if ( woceFlag.equals(SocatWoceEvent.OLD_WOCE_GOOD) ) {
						newFlag = SocatWoceEvent.WOCE_GOOD;
					}
					else if ( woceFlag.equals(SocatWoceEvent.OLD_WOCE_NOT_CHECKED) ) {
						newFlag = SocatWoceEvent.WOCE_NOT_CHECKED;
					}
					else if ( woceFlag.equals(SocatWoceEvent.OLD_WOCE_QUESTIONABLE) ) {
						newFlag = SocatWoceEvent.WOCE_QUESTIONABLE;
					}
					else if ( woceFlag.equals(SocatWoceEvent.OLD_WOCE_BAD) ) {
						newFlag = SocatWoceEvent.WOCE_BAD;
					}
					else if ( woceFlag.equals(SocatWoceEvent.OLD_WOCE_NO_DATA) ) {
						newFlag = SocatWoceEvent.WOCE_NO_DATA;
					}
					else {
						throw new IllegalArgumentException("Unexpected \"old\" WOCE flag of '" + woceFlag + "'");
					}

					SocatWoceEvent newWoceEvent = new SocatWoceEvent();
					newWoceEvent.setExpocode(expocode);
					newWoceEvent.setSocatVersion(restoredSocatVersion);
					newWoceEvent.setFlag(newFlag);
					newWoceEvent.setFlagDate(woceEvent.getFlagDate());
					newWoceEvent.setUsername(woceEvent.getUsername());
					newWoceEvent.setRealname(woceEvent.getRealname());
					newWoceEvent.setDataVarName(woceEvent.getDataVarName());
					newWoceEvent.setLocations(newLocations);
					newWoceEvent.setComment("(reinstated WOCE flags) " + woceEvent.getComment());
					databaseHandler.addWoceEvent(newWoceEvent);

					System.out.println("regenerated WOCE flags of '" + newFlag + "' in WOCE event " + 
							Long.toString(newWoceEvent.getId()) + " for " + expocode);
					for ( DataLocation dataLoc : newLocations ) {
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
			System.out.println("WOCE flags updated in the DSG files for " + expocode);
		}
		return changed;
	}

}
