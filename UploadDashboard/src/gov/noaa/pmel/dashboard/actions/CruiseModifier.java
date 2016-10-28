/**
 * 
 */
package gov.noaa.pmel.dashboard.actions;

import gov.noaa.pmel.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.handlers.UserFileHandler;
import gov.noaa.pmel.dashboard.server.CruiseDsgNcFile;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.SocatCruiseData;
import gov.noaa.pmel.dashboard.server.SocatMetadata;
import gov.noaa.pmel.dashboard.server.SocatTypes;
import gov.noaa.pmel.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.WoceEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Methods for revising dataset information, such as dataset owner and expocode.
 * Also restores a dataset to a previous version currently given in the text data file.
 * (Removes WOCE flags and QC flags for the current SOCAT upload version and restores 
 *  any old WOCE flags applicable to the current data.  Adds a QC comment that the 
 *  cruise data and WOCE flags were restored.)
 * 
 * TODO: The current restoration code only restores WOCE_CO2_water; need to add 
 *       WOCE_CO2_atm if this is needed for v5 (or later) datasets.
 * 
 * @author Karl Smith
 */
public class CruiseModifier {

	private static final SimpleDateFormat DATETIMESTAMPER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/** Jan 1, 1940 - reasonable lower limit on data dates */
	private static final Date EARLIEST_DATE;
	static {
		DATETIMESTAMPER.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			EARLIEST_DATE = DATETIMESTAMPER.parse("1940-01-01 00:00:00");
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Class for collecting and sorting time/lat/lon/fco2rec data
	 * for possible later WOCE-4 flagging.
	 */
	private static class DataInfo implements Comparable<DataInfo> {
		final String expocode;
		final int num;
		final char regionID;
		final Date datetime;
		final Double latitude;
		final Double longitude;
		final Double fco2rec;

		/**
		 * @param expocode
		 * 		dataset expocode (ignored for comparisons)
		 * @param num
		 * 		row number of this datapoint in the dataset (ignored for comparisons)
		 * @param regionId
		 * 		region ID of this datapoint (ignored for comparisons)
		 * @param sectime
		 * 		measurement time in seconds since Jan 1, 1970 00:00:00
		 * @param latitude
		 * 		measurement latitude in decimal degrees north
		 * @param longitude
		 * 		measurment longitude in decimal degrees east in the range [-180,180]
		 * @param fco2rec
		 * 		measurement recommended fCO2
		 * @throws IllegalArgumentException
		 * 		if the sectime, latitude, longitude, or fco2rec values are invalid
		 */
		DataInfo(String expocode, int num, char regionID, Double sectime, Double latitude, 
				Double longitude, Double fco2rec) throws IllegalArgumentException {
			if ( expocode == null )
				throw new IllegalArgumentException("null expocode");
			this.expocode = expocode;

			if ( num <= 0 )
				throw new IllegalArgumentException("invalid row number of " + Integer.toString(num) + " for " + expocode);
			this.num = num;

			if ( DashboardUtils.REGION_NAMES.get(regionID) == null )
				throw new IllegalArgumentException("invalid region ID of '" + regionID + "' for " + expocode);
			this.regionID = regionID;

			if ( sectime == null )
				throw new IllegalArgumentException("null time for " + expocode);
			this.datetime = new Date(Math.round(sectime * 1000.0));
			Date now = new Date();
			if ( this.datetime.before(EARLIEST_DATE) || this.datetime.after(now) )
				throw new IllegalArgumentException("invalid time of " + this.datetime.toString() + " for " + expocode);

			if ( latitude == null )
				throw new IllegalArgumentException("null latitude for " + expocode);
			if ( (latitude < -90.0) || (latitude > 90.0) )
				throw new IllegalArgumentException("invalid latitude of " + latitude + " for " + expocode);
			this.latitude = latitude;

			if ( longitude == null )
				throw new IllegalArgumentException("null longitude for " + expocode);
			if ( (longitude < -180.0) || (longitude > 180.0) )
				throw new IllegalArgumentException("invalid longitude of " + longitude + " for " + expocode);
			this.longitude = longitude;

			if ( fco2rec == null )
				throw new IllegalArgumentException("null fco2rec for " + expocode);
			if ( ( ! DashboardUtils.FP_MISSING_VALUE.equals(fco2rec) ) && 
				 ( (fco2rec < 0.0) || (fco2rec > 100000.0) ) )
				throw new IllegalArgumentException("invalid fCO2rec of " + fco2rec + " in " + expocode);
			this.fco2rec = fco2rec;
		}

		@Override
		public int compareTo(DataInfo other) {
			// the primary sort must be on datetime
			int result = this.datetime.compareTo(other.datetime);
			if ( result != 0 )
				return result;
			result = this.latitude.compareTo(other.latitude);
			if ( result != 0 )
				return result;
			result = this.longitude.compareTo(other.longitude);
			if ( result != 0 )
				return result;
			result = this.fco2rec.compareTo(other.fco2rec);
			if ( result != 0 )
				return result;
			// Ignore expocode, num, and regionID
			return 0;
		}

		@Override
		public int hashCode() {
			final int prime = 37;
			int result = 1;
			result = prime * result + datetime.hashCode();
			result = prime * result + latitude.hashCode();
			result = prime * result + longitude.hashCode();
			result = prime * result + fco2rec.hashCode();
			// Ignore expocode, num, and regionID
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) 
				return true;
			if ( obj == null ) 
				return false;
			if ( ! (obj instanceof DataInfo) )
				return false;
			DataInfo other = (DataInfo) obj;
			if ( ! datetime.equals(other.datetime) )
				return false;
			if ( ! latitude.equals(other.latitude) ) 
				return false;
			if ( ! longitude.equals(other.longitude) ) 
				return false;
			if ( ! fco2rec.equals(other.fco2rec) ) 
				return false;
			// Ignore expocode, num, and regionID
			return true;
		}

		@Override
		public String toString() {
			return  "[ expocode=" + expocode +
					", num=" + Integer.toString(num) +
					", regionID=" + Character.toString(regionID) +
					", datetime=" + DATETIMESTAMPER.format(datetime) + 
					", latitude=" + String.format("%#.6f", latitude) + 
					", longitude=" + String.format("%#.6f", longitude) + 
					", fco2rec=" + String.format("%#.6f", fco2rec) + 
					" ]";
		}

	}

	DashboardConfigStore configStore;
	String restoredSocatVersion;

	/**
	 * Modifies information about datasets or restores previous versions of data or WOCE flags for datasets.
	 * @param configStore
	 * 		configuration store to use
	 */
	public CruiseModifier(DashboardConfigStore configStore) {
		this.configStore = configStore;
		this.restoredSocatVersion = null;
	}

	/**
	 * Changes the owner of the data and metadata files for a dataset.
	 * The dataset is added to the list of datasets for the new owner.
	 * 
	 * @param expocode
	 * 		change the owner of the data and metadata files for the dataset with this expocode 
	 * @param newOwner
	 * 		change the owner of the data and metadata files to this username
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid,
	 * 		if the new owner username is not recognized,
	 * 		if there is no data file for the indicated dataset
	 */
	public void changeCruiseOwner(String expocode, String newOwner) 
									throws IllegalArgumentException {
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		if ( ! configStore.validateUser(newOwner) )
			throw new IllegalArgumentException("Unknown dashboard user " + newOwner);

		CruiseFileHandler cruiseHandler = configStore.getCruiseFileHandler();
		DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(upperExpo);
		String oldOwner = cruise.getOwner();
		cruise.setOwner(newOwner);
		cruiseHandler.saveCruiseInfoToFile(cruise, "Owner of " + upperExpo + 
				" data file changed from " + oldOwner + " to " + newOwner);

		MetadataFileHandler metaHandler = configStore.getMetadataFileHandler();
		ArrayList<DashboardMetadata> metaList = metaHandler.getMetadataFiles(upperExpo);
		for ( DashboardMetadata mdata : metaList ) {
			String oldMetaOwner = mdata.getOwner();
			mdata.setOwner(newOwner);
			metaHandler.saveMetadataInfo(mdata, "Owner of " + upperExpo + 
					" metadata file changed from " + oldMetaOwner + " to " + newOwner, false);
		}

		UserFileHandler userHandler = configStore.getUserFileHandler();
		String commitMsg = "Dataset " + upperExpo + " moved from " + oldOwner + " to " + newOwner;

		// Add this cruise to the list for the new owner
		DashboardCruiseList cruiseList = userHandler.getCruiseListing(newOwner);
		if ( cruiseList.put(upperExpo, cruise) == null ) {
			userHandler.saveCruiseListing(cruiseList, commitMsg);
		}

		// Rely on update-on-read to remove the cruise from the list of the old owner 
		// (and others) if they no longer should be able to see this cruise 
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
		configStore.getCruiseFileHandler().renameCruiseFiles(oldExpo, newExpo);
		// rename the SanityChecker messages file, if it exists
		configStore.getCheckerMsgHandler().renameMsgsFile(oldExpo, newExpo);
		// TODO: rename the WOCE messages file, if it exists
		// rename metadata files; update the expocode in the OME metadata
		configStore.getMetadataFileHandler().renameMetadataFiles(oldExpo, newExpo);
		// rename the DSG and decimated DSG files; update the expocode in these files
		configStore.getDsgNcFileHandler().renameDsgFiles(oldExpo, newExpo);
		// generate a rename QC comment and modify expocodes for the flags
		configStore.getDatabaseRequestHandler().renameCruiseFlags(
				oldExpo, newExpo, configStore.getSocatUploadVersion(), username);
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
	 * TODO: The current restoration code only restores WOCE_CO2_water; need to add
	 *       WOCE_CO2_atm if this is needed for v5 (or later) datasets,
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
		DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
		CruiseDsgNcFile dsgFile = dsgHandler.getDsgNcFile(expocode);
		dsgFile.readMetadata(configStore.getKnownMetadataTypes());
		SocatMetadata metaData = dsgFile.getMetadata();
		restoredSocatVersion = metaData.getSocatVersion();
		// Remove the terminal 'N' or 'U'
		restoredSocatVersion = restoredSocatVersion.substring(0, restoredSocatVersion.length()-1);

		// Read longitudes, latitude, and times for all data
		double[][] lonlattimes = dsgFile.readLonLatTimeDataValues();
		double[] longitudes = lonlattimes[0];
		double[] latitudes = lonlattimes[1];
		double[] times = lonlattimes[2];
		int numData = times.length;
		char[] currentWoceFlags = dsgFile.readCharVarDataValues(SocatTypes.WOCE_CO2_WATER.getVarName());
		char[] revisedWoceFlags = Arrays.copyOf(currentWoceFlags, currentWoceFlags.length);

		// Get all WOCE events for this expocode, order so the latest are last
		DatabaseRequestHandler databaseHandler = configStore.getDatabaseRequestHandler();
		for ( WoceEvent woceEvent : databaseHandler.getWoceEvents(expocode, false) ) {
			// SOCAT version for this WOCE event must match that of the data
			if ( ! restoredSocatVersion.equals(woceEvent.getVersion()) )
				continue;
			// Skip WOCE events generated by the automated data-checker;
			// they should have been regenerated when resubmitted
			if ( woceEvent.getUsername().equals(DashboardUtils.SANITY_CHECKER_USERNAME) )
				continue;
			// Only restore WOCE events not for WOCE_CO2_water
			// TODO: need to do WOCE_CO2_atm as well if this is needed for v5 or later
			if ( ! woceEvent.getWoceName().equals(SocatTypes.WOCE_CO2_WATER.getVarName()) )
				continue;
			// Find "old" WOCE events and check if it should be restored
			Character woceFlag = woceEvent.getFlag();
			if ( woceFlag.equals(DashboardUtils.OLD_WOCE_BAD) ||
				 woceFlag.equals(DashboardUtils.OLD_WOCE_QUESTIONABLE) ||
				 woceFlag.equals(DashboardUtils.OLD_WOCE_NOT_CHECKED) ||
				 woceFlag.equals(DashboardUtils.OLD_WOCE_GOOD) ||
				 woceFlag.equals(DashboardUtils.OLD_WOCE_NO_DATA) ) {

				// Get the data associated with this WOCE event
				String dataName = woceEvent.getVarName();
				double[] dataVals;
				if ( dataName.isEmpty() || dataName.equals(DashboardServerUtils.GEOPOSITION.getVarName()) )
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
			else if ( woceFlag.equals(DashboardUtils.WOCE_BAD) ||
					  woceFlag.equals(DashboardUtils.WOCE_QUESTIONABLE) ||
					  woceFlag.equals(DashboardUtils.WOCE_NOT_CHECKED) ||
					  woceFlag.equals(DashboardUtils.WOCE_GOOD) ||
					  woceFlag.equals(DashboardUtils.WOCE_NO_DATA) ) {
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
			dsgFile.writeCharVarDataValues(SocatTypes.WOCE_CO2_WATER.getVarName(), revisedWoceFlags);
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
	 * TODO: The current restoration code only restores WOCE_CO2_water; need to add
	 *       WOCE_CO2_atm if this is needed for v5 (or later) datasets,
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
		DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
		CruiseDsgNcFile dsgFile = dsgHandler.getDsgNcFile(expocode);
		dsgFile.readMetadata(configStore.getKnownMetadataTypes());
		SocatMetadata metaData = dsgFile.getMetadata();
		restoredSocatVersion = metaData.getSocatVersion();
		// Remove the terminal 'N' or 'U'
		restoredSocatVersion = restoredSocatVersion.substring(0, restoredSocatVersion.length()-1);
		
		// Read longitudes, latitude, and times for all data
		double[][] lonlattime = dsgFile.readLonLatTimeDataValues();
		double[] longitudes = lonlattime[0];
		double[] latitudes = lonlattime[1];
		double[] times = lonlattime[2];
		int numData = times.length;
		char[] regionIDs = dsgFile.readCharVarDataValues(SocatTypes.REGION_ID.getVarName());
		char[] currentWoceFlags = dsgFile.readCharVarDataValues(SocatTypes.WOCE_CO2_WATER.getVarName());
		char[] revisedWoceFlags = Arrays.copyOf(currentWoceFlags, currentWoceFlags.length);

		// Get all WOCE events for this expocode, order so the latest are last
		DatabaseRequestHandler databaseHandler = configStore.getDatabaseRequestHandler();
		for ( WoceEvent woceEvent : databaseHandler.getWoceEvents(expocode, false) ) {
			// Skip WOCE events generated by the automated data-checker;
			// they should have been regenerated, if appropriate, when resubmitted
			if ( woceEvent.getUsername().equals(DashboardUtils.SANITY_CHECKER_USERNAME) )
				continue;
			// Only restore WOCE events not for WOCE_CO2_water
			// TODO: need to do WOCE_CO2_atm as well if this is needed for v5 or later
			if ( ! woceEvent.getWoceName().equals(SocatTypes.WOCE_CO2_WATER.getVarName()) )
				continue;

			// Find "old" WOCE events and check if it should be regenerated
			Character woceFlag = woceEvent.getFlag();
			if ( woceFlag.equals(DashboardUtils.OLD_WOCE_BAD) ||
				 woceFlag.equals(DashboardUtils.OLD_WOCE_QUESTIONABLE) ||
				 woceFlag.equals(DashboardUtils.OLD_WOCE_NOT_CHECKED) ||
				 woceFlag.equals(DashboardUtils.OLD_WOCE_GOOD) ||
				 woceFlag.equals(DashboardUtils.OLD_WOCE_NO_DATA) ) {

				// Get the data associated with this WOCE event
				String dataName = woceEvent.getVarName();
				double[] dataVals;
				if ( dataName.isEmpty() || dataName.equals(DashboardServerUtils.GEOPOSITION.getVarName()) )
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
					if ( woceFlag.equals(DashboardUtils.OLD_WOCE_GOOD) ) {
						newFlag = DashboardUtils.WOCE_GOOD;
					}
					else if ( woceFlag.equals(DashboardUtils.OLD_WOCE_NOT_CHECKED) ) {
						newFlag = DashboardUtils.WOCE_NOT_CHECKED;
					}
					else if ( woceFlag.equals(DashboardUtils.OLD_WOCE_QUESTIONABLE) ) {
						newFlag = DashboardUtils.WOCE_QUESTIONABLE;
					}
					else if ( woceFlag.equals(DashboardUtils.OLD_WOCE_BAD) ) {
						newFlag = DashboardUtils.WOCE_BAD;
					}
					else if ( woceFlag.equals(DashboardUtils.OLD_WOCE_NO_DATA) ) {
						newFlag = DashboardUtils.WOCE_NO_DATA;
					}
					else {
						throw new IllegalArgumentException("Unexpected \"old\" WOCE flag of '" + woceFlag + "'");
					}

					WoceEvent newWoceEvent = new WoceEvent();
					newWoceEvent.setExpocode(expocode);
					newWoceEvent.setVersion(restoredSocatVersion);
					newWoceEvent.setFlag(newFlag);
					newWoceEvent.setWoceName(woceEvent.getWoceName());
					newWoceEvent.setFlagDate(woceEvent.getFlagDate());
					newWoceEvent.setUsername(woceEvent.getUsername());
					newWoceEvent.setRealname(woceEvent.getRealname());
					newWoceEvent.setVarName(woceEvent.getVarName());
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
			else if ( woceFlag.equals(DashboardUtils.WOCE_BAD) ||
					  woceFlag.equals(DashboardUtils.WOCE_QUESTIONABLE) ||
					  woceFlag.equals(DashboardUtils.WOCE_NOT_CHECKED) ||
					  woceFlag.equals(DashboardUtils.WOCE_GOOD) ||
					  woceFlag.equals(DashboardUtils.WOCE_NO_DATA) ) {
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
			dsgFile.writeCharVarDataValues(SocatTypes.WOCE_CO2_WATER.getVarName(), revisedWoceFlags);
			// Generate the decimated DSG file from the updated full-data DSG file
			dsgHandler.decimateCruise(expocode);
			System.out.println("WOCE flags updated in the DSG files for " + expocode);
		}
		return changed;
	}

	/**
	 * Applies WOCE-4 flags to any duplicated lon/lat/time/fCO2_rec data 
	 * points found within a data set.  Add the WOCE event to the database, 
	 * modifies the full-data DSG file, and recreates the decimated-data
	 * DSG file.  Does not flag ERDDAP as this may be called repeatedly
	 * with different expocodes. 
	 * 
	 * @param expocode
	 * 		examine the data of the cruise with this expocode 
	 * @return
	 * 		list of messages describing the duplicate lon/lat/time/fCO2_rec 
	 * 		data points given a WOCE-4 flag
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid,
	 * @throws IOException 
	 * 		if unable to read or update a DSG NC file, or
	 * 		if unable to read or update the database
	 * 
	 */
	public ArrayList<String> woceDuplicateDatapoints(String expocode)
			throws IllegalArgumentException, IOException {
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);

		// Get the metadata and data from the DSG file
		CruiseDsgNcFile dsgFile = configStore.getDsgNcFileHandler().getDsgNcFile(upperExpo);
		ArrayList<String> unknownVars = dsgFile.readMetadata(configStore.getKnownMetadataTypes());
		if ( unknownVars.size() > 0 ) {
			String msg = "Unassigned metadata variables: ";
			for (String var : unknownVars)
				msg += var + "; ";
			System.err.println(msg);
		}
		unknownVars = dsgFile.readData(configStore.getKnownDataFileTypes());
		if ( unknownVars.size() > 0 ) {
			String msg = "Unassigned data variables: ";
			for (String var : unknownVars)
				msg += var + "; ";
			System.err.println(msg);
		}

		// Get the SOCAT version from the DSG metadata
		SocatMetadata socatMeta = dsgFile.getMetadata();
		String socatVersion = socatMeta.getSocatVersion();

		// Get the computed values of time in seconds since 1970-01-01 00:00:00
		double[] sectimes = dsgFile.readDoubleVarDataValues(DashboardServerUtils.TIME.getVarName());

		// Create the set for holding previous lon/lat/time/fCO2_rec data
		TreeSet<DataInfo> prevDatInf = new TreeSet<DataInfo>();
		// Create a list for holding any duplicate lon/lat/tim/fCO2_rec data
		ArrayList<DataInfo> dupDatInf = new ArrayList<DataInfo>();
		// Process all the data points that are not already WOCE-4, 
		// looking for duplicate lon/lat/time/fCO2_rec
		int j = -1;
		for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
			j++;
			Character woceFlag = dataVals.getWoceCO2Water();
			if ( woceFlag.equals(DashboardUtils.WOCE_GOOD) || 
				 woceFlag.equals(DashboardUtils.WOCE_NOT_CHECKED) ||
				 woceFlag.equals(DashboardUtils.WOCE_QUESTIONABLE) ) {
				DataInfo datinf = new DataInfo(upperExpo, j+1, dataVals.getRegionID(), sectimes[j], 
						dataVals.getLatitude(), dataVals.getLongitude(), dataVals.getfCO2Rec());
				if ( ! prevDatInf.add(datinf) ) {
					dupDatInf.add(datinf);
				}
			}
		}

		ArrayList<String> warnMsgs = new ArrayList<String>(dupDatInf.size());
		if ( ! dupDatInf.isEmpty() ) {
			// Assign the WOCE-4 flag for duplicates
			ArrayList<DataLocation> locations = new ArrayList<DataLocation>(dupDatInf.size());
			for ( DataInfo datinf : dupDatInf ) {
				DataLocation loc = new DataLocation();
				loc.setDataDate(datinf.datetime);
				loc.setDataValue(datinf.fco2rec);
				loc.setLatitude(datinf.latitude);
				loc.setLongitude(datinf.longitude);
				loc.setRegionID(datinf.regionID);
				loc.setRowNumber(datinf.num);
				locations.add(loc);
			}
			WoceEvent woceEvent = new WoceEvent();
			woceEvent.setExpocode(upperExpo);
			woceEvent.setVersion(socatVersion);
			woceEvent.setWoceName(SocatTypes.WOCE_CO2_WATER.getVarName());
			woceEvent.setFlag(DashboardUtils.WOCE_BAD);
			woceEvent.setFlagDate(new Date());
			woceEvent.setComment("duplicate lon/lat/time/fCO2_rec data points detected by automation");
			woceEvent.setUsername(DashboardUtils.SANITY_CHECKER_USERNAME);
			woceEvent.setRealname(DashboardUtils.SANITY_CHECKER_REALNAME);
			woceEvent.setVarName(SocatTypes.FCO2_REC.getVarName());
			woceEvent.setLocations(locations);
			// Add the WOCE event to the database
			try {
				configStore.getDatabaseRequestHandler().addWoceEvent(woceEvent);
			} catch (SQLException ex) {
				throw new IOException("Problem assigning WOCE-4 flags in database: " + ex.getMessage());
			}
			// Assign the WOCE-4 flags in the full-data DSG file
			ArrayList<String> issues = dsgFile.assignWoceFlags(woceEvent);
			if ( ! issues.isEmpty() ) {
				for ( String msg : issues ) {
					System.err.println(msg);
				}
				throw new IOException("Problem assigning WOCE-4 flags in the full-data DSG file");
			}
			// Re-create the decimated-data DSG file
			try {
				configStore.getDsgNcFileHandler().decimateCruise(upperExpo);
			} catch (Exception ex) {
				throw new IOException("Unable to decimate the updated full-data DSG file: " + ex.getMessage());
			}
			// Report WOCE-4 of duplicates
			for ( DataInfo datinf : dupDatInf ) {
				warnMsgs.add("WOCE-4 assigned to duplicate datapoint: " + datinf.toString());
			}
		}

		return warnMsgs;		
	}

}
