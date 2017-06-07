/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import gov.noaa.pmel.dashboard.datatype.CharDashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

/**
 * @author Karl Smith
 */
public class DashboardServerUtils {

	/** Minimum length for a valid dataset ID */
	public static final int MIN_DATASET_ID_LENGTH = 4;

	/** Maximum length for a valid dataset ID */
	public static final int MAX_DATASET_ID_LENGTH = 128;

	/** Authalic radius, in kilometers, of Earth */
	public static final double EARTH_AUTHALIC_RADIUS = 6371.007;

	/** Sanity Checker "username" for flags */
	public static final String SANITY_CHECKER_USERNAME = "automated.data.checker";

	/** Sanity Checker "realname" for flags */
	public static final String SANITY_CHECKER_REALNAME = "automated data checker";

	/** General-purpose flag value for no information available at this time */
	public static final Character FLAG_NO_INFO = '1';
	/** General-purpose flag value for acceptable data */
	public static final Character FLAG_ACCEPTABLE = '2';
	/** WOCE-type flag value for questionable data */
	public static final Character WOCE_QUESTIONABLE = '3';
	/** WOCE-type flag value for bad data */
	public static final Character WOCE_BAD = '4';

	// flags for QC/WOCE events of datasets that has been updated
	public static final Character OLD_FLAG_NO_INFO = 'U';
	public static final Character OLD_FLAG_ACCEPTABLE = 'G';
	public static final Character OLD_WOCE_QUESTIONABLE = 'Q';
	public static final Character OLD_WOCE_BAD = 'B';

	// flag and variable name for dataset rename events
	public static final Character FLAG_RENAME = 'R';
	public static final String RENAME_VARNAME = "Rename";

	/** 
	 * GEOPOSITION_VARNAME is a marker used in automated data checking
	 * to indicate an severe error in the combination of lon/lat/depth/time. 
	 */
	public static final String GEOPOSITION_VARNAME = "geoposition";

	// Some suggested categories
	public static final String BATHYMETRY_CATEGORY = "Bathymetry";
	public static final String IDENTIFIER_CATEGORY = "Identifier";
	public static final String LOCATION_CATEGORY = "Location";
	public static final String PLATFORM_CATEGORY = "Platform";
	public static final String QUALITY_CATEGORY = "Quality";
	public static final String TIME_CATEGORY = "Time";

	/** Unit of completely specified time ("seconds since 1970-01-01T00:00:00Z") */
	public static final ArrayList<String> TIME_UNITS = 
			new ArrayList<String>(Arrays.asList("seconds since 1970-01-01T00:00:00Z"));

	/**
	 * UNASSIGNED needs to be respecified as one of the (other) data column types.
	 */
	public static final StringDashDataType UNKNOWN = new StringDashDataType(DashboardUtils.UNKNOWN, 
			null, null, null, null, null, null, null);

	/**
	 * OTHER is for supplementary data in the user's original data file but 
	 * otherwise not used.  A description of each column with this type must 
	 * be part of the metadata, but the values are not validated or used. 
	 * Multiple columns may have this type.
	 */
	public static final StringDashDataType OTHER = new StringDashDataType(DashboardUtils.OTHER, 
			null, null, null, null, null, null, null);

	/**
	 * Unique identifier for the dataset 
	 * (metadata derived from user data column for the dataset name)
	 */
	public static final StringDashDataType DATASET_ID = new StringDashDataType("dataset_id", 
			50.0, "dataset ID", "unique ID for this dataset", false, DashboardUtils.NO_UNITS, 
			null, IDENTIFIER_CATEGORY, null, null, null, null, null);

	/**
	 * Consecutive numbering of the samples after merging and ordering.
	 */
	public static final IntDashDataType SAMPLE_NUMBER = new IntDashDataType("sample_number", 
			51.0, "sample num", "sample number", false, DashboardUtils.NO_UNITS, null, 
			IDENTIFIER_CATEGORY, null, "1", null, null, null);

	/**
	 * Completely specified sampling time (seconds since 1970-01-01T00:00:00Z) 
	 * used in file data; computed value.
	 */
	public static final DoubleDashDataType TIME = new DoubleDashDataType("time", 
			52.0, "time", "sample time", false, TIME_UNITS, "time", 
			TIME_CATEGORY, null, null, null, null, null);

	/**
	 * User-provided name for the dataset
	 */
	public static final StringDashDataType DATASET_NAME = new StringDashDataType(DashboardUtils.DATASET_NAME, 
			"cruise_name", IDENTIFIER_CATEGORY, null, null, null, null, null);

	public static final StringDashDataType PLATFORM_NAME = new StringDashDataType(DashboardUtils.PLATFORM_NAME, 
			"platform_name", PLATFORM_CATEGORY, null, null, null, null, null);
	public static final StringDashDataType PLATFORM_TYPE = new StringDashDataType(DashboardUtils.PLATFORM_TYPE, 
			"platform_name", PLATFORM_CATEGORY, null, null, null, null, null);
	public static final StringDashDataType ORGANIZATION_NAME = new StringDashDataType(DashboardUtils.ORGANIZATION_NAME, 
			"organization", PLATFORM_CATEGORY, null, null, null, null, null);
	public static final StringDashDataType INVESTIGATOR_NAMES = new StringDashDataType(DashboardUtils.INVESTIGATOR_NAMES, 
			"investigators", IDENTIFIER_CATEGORY, null, null, null, null, null);

	public static final DoubleDashDataType WESTERNMOST_LONGITUDE = new DoubleDashDataType("geospatial_lon_min", 
			110.0, "westmost lon", "westernmost longitude", false, DashboardUtils.LONGITUDE_UNITS, 
			"geospatial_lon_min", LOCATION_CATEGORY, "degrees_east", "-540.0", "-180.0", "360.0", "540.0");
	public static final DoubleDashDataType EASTERNMOST_LONGITUDE = new DoubleDashDataType("geospatial_lon_max", 
			111.0, "eastmost lon", "easternmost longitude", false, DashboardUtils.LONGITUDE_UNITS, 
			"geospatial_lon_max", LOCATION_CATEGORY, "degrees_east", "-540.0", "-180.0", "360.0", "540.0");
	public static final DoubleDashDataType SOUTHERNMOST_LATITUDE = new DoubleDashDataType("geospatial_lat_min", 
			112.0, "southmost lat", "southernmost latitude", false, DashboardUtils.LATITUDE_UNITS, 
			"geospatial_lat_min", LOCATION_CATEGORY, "degrees_north", "-90.0", null, null, "90.0");
	public static final DoubleDashDataType NORTHERNMOST_LATITUDE = new DoubleDashDataType("geospatial_lat_max", 
			113.0, "northmost lat", "northernmost latitude", false, DashboardUtils.LATITUDE_UNITS, 
			"geospatial_lat_max", LOCATION_CATEGORY, "degrees_north", "-90.0", null, null, "90.0");
	public static final DoubleDashDataType TIME_COVERAGE_START = new DoubleDashDataType("time_coverage_start", 
			114.0, "start time", "starting time", false, TIME_UNITS, 
			"time_coverage_start", LOCATION_CATEGORY, null, null, null, null, null);
	public static final DoubleDashDataType TIME_COVERAGE_END = new DoubleDashDataType("time_coverage_end", 
			115.0, "end time", "ending time", false, TIME_UNITS, 
			"time_coverage_end", LOCATION_CATEGORY, null, null, null, null, null);
	public static final StringDashDataType STATUS = new StringDashDataType("status",
			120.0, "status", "status", false, DashboardUtils.NO_UNITS, 
			null, IDENTIFIER_CATEGORY, null, null, null, null, null);
	public static final StringDashDataType VERSION = new StringDashDataType("version",
			121.0, "version", "version", false, DashboardUtils.NO_UNITS, 
			null, IDENTIFIER_CATEGORY, null, null, null, null, null);

	/**
	 * User-provided unique ID for a sample in a dataset (user data type only). 
	 * Used when merging files of different data types measured for a sample.
	 */
	public static final StringDashDataType SAMPLE_ID = new StringDashDataType(DashboardUtils.SAMPLE_ID, 
			null, IDENTIFIER_CATEGORY, null, null, null, null, null);

	public static final DoubleDashDataType LONGITUDE = new DoubleDashDataType(DashboardUtils.LONGITUDE, 
			"longitude", LOCATION_CATEGORY, "degrees_east", "-540.0", "-180.0", "360.0", "540.0");
	public static final DoubleDashDataType LATITUDE = new DoubleDashDataType(DashboardUtils.LATITUDE, 
			"latitude", LOCATION_CATEGORY, "degrees_north", "-90.0", null, null, "90.0");
	public static final DoubleDashDataType SAMPLE_DEPTH = new DoubleDashDataType(DashboardUtils.SAMPLE_DEPTH, 
			"depth", BATHYMETRY_CATEGORY, "meters", "0.0", null, null, "16000");

	/**
	 * Date and time of the measurement
	 */
	public static final StringDashDataType TIMESTAMP = new StringDashDataType(DashboardUtils.TIMESTAMP, 
			"timestamp", TIME_CATEGORY, null, null, null, null, null);

	/**
	 * Date of the measurement - no time.
	 */
	public static final StringDashDataType DATE = new StringDashDataType(DashboardUtils.DATE, 
			"date", TIME_CATEGORY, null, null, null, null, null);

	public static final IntDashDataType YEAR = new IntDashDataType(DashboardUtils.YEAR, 
			"year", TIME_CATEGORY, null, "1900", "1950", "2050", "2100");
	public static final IntDashDataType MONTH_OF_YEAR = new IntDashDataType(DashboardUtils.MONTH_OF_YEAR, 
			"month_of_year", TIME_CATEGORY, "1", null, null, "12", null);
	public static final IntDashDataType DAY_OF_MONTH = new IntDashDataType(DashboardUtils.DAY_OF_MONTH, 
			"day_of_month", TIME_CATEGORY, "1", null, null, "31", null);
	public static final StringDashDataType TIME_OF_DAY = new StringDashDataType(DashboardUtils.TIME_OF_DAY, 
			"time_of_day", TIME_CATEGORY, null, null, null, null, null);
	public static final IntDashDataType HOUR_OF_DAY = new IntDashDataType(DashboardUtils.HOUR_OF_DAY, 
			"hour_of_day", TIME_CATEGORY, null, "0", null, null, "24");
	public static final IntDashDataType MINUTE_OF_HOUR = new IntDashDataType(DashboardUtils.MINUTE_OF_HOUR, 
			"minute_of_hour", TIME_CATEGORY, null, "0", null, null, "60");
	public static final DoubleDashDataType SECOND_OF_MINUTE = new DoubleDashDataType(DashboardUtils.SECOND_OF_MINUTE, 
			"second_of_minute", TIME_CATEGORY, null, "0.0", null, null, "60.0");

	/**
	 * DAY_OF_YEAR, along with YEAR, and possibly SECOND_OF_DAY,
	 * may be used to specify the date and time of the measurement.
	 */
	public static final DoubleDashDataType DAY_OF_YEAR = new DoubleDashDataType(DashboardUtils.DAY_OF_YEAR, 
			"day_of_year", TIME_CATEGORY, null, "1.0", null, null, "367.0");

	/**
	 * SECOND_OF_DAY, along with YEAR and DAY_OF_YEAR may
	 * be used to specify date and time of the measurement
	 */
	public static final DoubleDashDataType SECOND_OF_DAY = new DoubleDashDataType(DashboardUtils.SECOND_OF_DAY, 
			"second_of_day", TIME_CATEGORY, null, "0.0", null, null, "86400.0");

	/**
	 * WOCE flag from the automated data checker.
	 */
	public static final CharDashDataType WOCE_AUTOCHECK = new CharDashDataType("WOCE_autocheck",
			500.0, "WOCE autocheck", "WOCE flag from automated data checking", false,
			DashboardUtils.NO_UNITS, "WOCE_flag", QUALITY_CATEGORY, null, "1", "2", "4", "9");

	/** 
	 * Value of userRealName to use to skip sending the email request in 
	 * {@link ArchiveFilesBundler#sendOrigFilesBundle(String, String, String, String)} 
	 */
	public static final String NOMAIL_USER_REAL_NAME = "nobody";

	/** 
	 * Value of userEmail to use to skip sending the email request in 
	 * {@link ArchiveFilesBundler#sendOrigFilesBundle(String, String, String, String)} 
	 */
	public static final String NOMAIL_USER_EMAIL = "nobody@nowhere";

	/** Pattern for {@link #getDatasetIDFromName(string)} */
	private static final Pattern uppercaseStripPattern = Pattern.compile("[^\\p{javaUpperCase}\\p{Digit}]+");

	/**
	 * Returns the dataset ID for the given dataset / cruise name by converting
	 * characters in the name to uppercase and ignoring anything that is not 
	 * uppercase of a digit.  The value returned is equivalent to 
	 * <pre>name.toUpperCase().replaceAll("[^\p{javaUpperCase}\p{Digit}]+", "")</pre>
	 * 
	 * @param name
	 * 		dataset name
	 * @return
	 * 		dataset ID for the given dataset name
	 */
	public static String getDatasetIDFromName(String name) {
		return uppercaseStripPattern.matcher(name.toUpperCase()).replaceAll("");
	}

	/**
	 * Computes a key for the given name by converting characters in the name 
	 * to uppercase, ignoring anything that is not uppercase or a digit, 
	 * (see {@link #getDatasetIDFromName(String)}) and then converting characters 
	 * to lowercase.  (This eliminates the degree symbol, or something similar, 
	 * that is part of javaLowerCase.)
	 * 
	 * @param name
	 * 		name to use
	 * @return
	 * 		key for the given name
	 */
	public static String getKeyForName(String name) {
		return getDatasetIDFromName(name).toLowerCase();
	}

	/**
	 * Checks and standardized a given dataset ID.
	 * 
	 * @param datasetID
	 * 		dataset ID to check
	 * @return
	 * 		standardized (uppercase) dataset ID
	 * @throws IllegalArgumentException
	 * 		if the dataset ID is null, too short, too long, or contains invalid characters
	 */
	public static String checkDatasetID(String datasetID) throws IllegalArgumentException {
		if ( datasetID == null )
			throw new IllegalArgumentException("No dataset ID given");
		String cleanID = datasetID.trim();
		if ( cleanID.length() < MIN_DATASET_ID_LENGTH )
			throw new IllegalArgumentException("Dataset ID too short");
		if ( cleanID.length() > MAX_DATASET_ID_LENGTH )
			throw new IllegalArgumentException("Dataset ID too long");
		String upperID = getDatasetIDFromName(cleanID);
		if ( upperID.length() != cleanID.length() )
			throw new IllegalArgumentException("Invalid characters in the dataset ID");
		return upperID;
	}

	/**
	 * Returns the minimum and maximum valid values from the given data array.
	 * Missing values (those very close to {@link DashboardUtils#FP_MISSING_VALUE})
	 * are ignored.
	 * 
	 * @param data
	 * 		find the minimum and maximum valid values of this data
	 * @return
	 * 		(minVal, maxVal) where minVal is the minimum, maxVal is the maximum, or
	 * 		({@link DashboardUtils#FP_MISSING_VALUE}, {@link DashboardUtils#FP_MISSING_VALUE})
	 * 		if all data is missing.
	 */
	public static double[] getMinMaxValidData(double[] data) {
		double maxVal = DashboardUtils.FP_MISSING_VALUE;
		double minVal = DashboardUtils.FP_MISSING_VALUE;
		for ( double val : data ) {
			if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, val, 
					DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
				continue;
			if ( (maxVal == DashboardUtils.FP_MISSING_VALUE) ||
				 (minVal == DashboardUtils.FP_MISSING_VALUE) ) {
				maxVal = val;
				minVal = val;
			}
			else if ( maxVal < val ) {
				maxVal = val;
			}
			else if ( minVal > val ) {
				minVal = val;
			}
		}
		return new double[] {minVal, maxVal};
	}

	/**
	 * Returns the distance between two locations.  Uses the haversine formula, 
	 * and {@link DashboardUtils#EARTH_AUTHALIC_RADIUS} for the radius of a 
	 * spherical Earth, to compute the great circle distance from the 
	 * longitudes and latitudes.
	 * 
	 * @param lon
	 * 		longitude, in degrees, of the first data location
	 * @param lat
	 * 		latitude, in degrees, of the first data location
	 * @param otherlon
	 * 		longitude, in degrees, of the other data location
	 * @param otherlat
	 * 		latitude, in degrees, of the other data location
	 * @return
	 *      the location-time distance between this location-time point
	 *      and other in kilometers
	 */
	public static double distanceBetween(double lon, double lat, double otherLon, double otherLat) {
		// Convert longitude and latitude degrees to radians
		double lat1 = lat * Math.PI / 180.0;
		double lat2 = otherLat * Math.PI / 180.0;
		double lon1 = lon * Math.PI / 180.0;
		double lon2 = otherLon * Math.PI / 180.0;
		/*
		 * Use the haversine formula to compute the great circle distance, 
		 * in radians, between the two (longitude, latitude) points. 
		 */
		double dellat = Math.sin(0.5 * (lat2 - lat1));
		dellat *= dellat;
		double dellon = Math.sin(0.5 * (lon2 - lon1));
		dellon *= dellon * Math.cos(lat1) * Math.cos(lat2);
		double distance = 2.0 * Math.asin(Math.sqrt(dellon + dellat));
		// Convert the great circle distance from radians to kilometers
		distance *= EARTH_AUTHALIC_RADIUS;

		return distance;
	}

}
