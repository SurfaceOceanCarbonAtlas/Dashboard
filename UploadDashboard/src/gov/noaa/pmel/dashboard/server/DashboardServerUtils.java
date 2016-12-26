/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import java.util.regex.Pattern;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
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

	/** Sanity Checker "username" for flags */
	public static final String SANITY_CHECKER_USERNAME = "automated.data.checker";

	/** Sanity Checker "realname" for flags */
	public static final String SANITY_CHECKER_REALNAME = "automated data checker";

	// flags for QC/WOCE events of datasets that has been updated
	public static final Character OLD_FLAG_NO_INFO = 'U';
	public static final Character OLD_FLAG_ACCEPTABLE = 'G';
	public static final Character OLD_WOCE_QUESTIONABLE = 'Q';
	public static final Character OLD_WOCE_BAD = 'B';

	// flag and variable name for dataset rename events
	public static final Character FLAG_RENAME = 'R';
	public static final String RENAME_VARNAME = "Rename";

	// Some suggested categories
	public static final String BATHYMETRY_CATEGORY = "Bathymetry";
	public static final String CO2_CATEGORY = "CO2";
	public static final String IDENTIFIER_CATEGORY = "Identifier";
	public static final String LOCATION_CATEGORY = "Location";
	public static final String PLATFORM_CATEGORY = "Platform";
	public static final String PRESSURE_CATEGORY = "Pressure";
	public static final String QUALITY_CATEGORY = "Quality";
	public static final String SALINITY_CATEGORY = "Salinity";
	public static final String TEMPERATURE_CATEGORY = "Temperature";
	public static final String TIME_CATEGORY = "Time";
	public static final String WATER_VAPOR_CATEGORY = "Water Vapor";
	public static final String WIND_CATEGORY = "Wind";

	/**
	 * UNKNOWN needs to be respecified as one of the (other) data column types.
	 */
	public static final StringDashDataType UNKNOWN = new StringDashDataType(DashboardUtils.UNKNOWN, 
			null, null, null, null, null, null);

	/**
	 * OTHER is for supplementary data in the user's original data file but 
	 * otherwise not used.  A description of each column with this type must 
	 * be part of the metadata, but the values are not validated or used. 
	 * Multiple columns may have this type.
	 */
	public static final StringDashDataType OTHER = new StringDashDataType(DashboardUtils.OTHER, 
			null, null, null, null, null, null);

	/**
	 * Unique identifier for the dataset 
	 * (metadata derived from user data column for the dataset name)
	 */
	public static final StringDashDataType DATASET_ID = new StringDashDataType(DashboardUtils.DATASET_ID, 
			null, null, null, null, null, null);

	/**
	 * Consecutive numbering of the samples after merging and ordering.
	 */
	public static final IntDashDataType SAMPLE_NUMBER = new IntDashDataType(DashboardUtils.SAMPLE_NUMBER, 
			null, null, "1", null, null, null);

	/**
	 * Completely specified time (seconds since 1970-01-01T00:00:00Z) of a sample.
	 * Computed value; not a user type
	 */
	public static final DoubleDashDataType TIME = new DoubleDashDataType(DashboardUtils.TIME, 
			"time", TIME_CATEGORY, null, null, null, null);

	/**
	 * User-provided name for the dataset
	 */
	public static final StringDashDataType DATASET_NAME = new StringDashDataType(DashboardUtils.DATASET_NAME, 
			"dataset_name", IDENTIFIER_CATEGORY, null, null, null, null);

	public static final StringDashDataType PLATFORM_NAME = new StringDashDataType(DashboardUtils.PLATFORM_NAME, 
			"platform_name", IDENTIFIER_CATEGORY, null, null, null, null);
	public static final StringDashDataType PLATFORM_TYPE = new StringDashDataType(DashboardUtils.PLATFORM_TYPE, 
			"platform_name", IDENTIFIER_CATEGORY, null, null, null, null);
			
	public static final StringDashDataType ORGANIZATION_NAME = new StringDashDataType(DashboardUtils.ORGANIZATION_NAME, 
			"organization", IDENTIFIER_CATEGORY, null, null, null, null);
	public static final StringDashDataType INVESTIGATOR_NAMES = new StringDashDataType(DashboardUtils.INVESTIGATOR_NAMES, 
			"investigators", IDENTIFIER_CATEGORY, null, null, null, null);
	public static final LonLatDashDataType WESTERNMOST_LONGITUDE = new LonLatDashDataType(DashboardUtils.WESTERNMOST_LONGITUDE, 
			"geospatial_lon_min", LOCATION_CATEGORY, null, null, null, null);
	public static final LonLatDashDataType EASTERNMOST_LONGITUDE = new LonLatDashDataType(DashboardUtils.EASTERNMOST_LONGITUDE, 
			"geospatial_lon_max", LOCATION_CATEGORY, null, null, null, null);
	public static final LonLatDashDataType SOUTHERNMOST_LATITUDE = new LonLatDashDataType(DashboardUtils.SOUTHERNMOST_LATITUDE, 
			"geospatial_lat_min", LOCATION_CATEGORY, null, null, null, null);
	public static final LonLatDashDataType NORTHERNMOST_LATITUDE = new LonLatDashDataType(DashboardUtils.NORTHERNMOST_LATITUDE, 
			"geospatial_lat_max", LOCATION_CATEGORY, null, null, null, null);
	public static final TimeDashDataType TIME_COVERAGE_START = new TimeDashDataType(DashboardUtils.TIME_COVERAGE_START, 
			"time_coverage_start", LOCATION_CATEGORY, null, null, null, null);
	public static final TimeDashDataType TIME_COVERAGE_END = new TimeDashDataType(DashboardUtils.TIME_COVERAGE_END, 
			"time_coverage_end", LOCATION_CATEGORY, null, null, null, null);
	public static final StringDashDataType STATUS = new StringDashDataType(DashboardUtils.STATUS, 
			"status", IDENTIFIER_CATEGORY, null, null, null, null);
	public static final StringDashDataType VERSION = new StringDashDataType(DashboardUtils.VERSION, 
			"cersion", IDENTIFIER_CATEGORY, null, null, null, null);

	/**
	 * User-provided unique ID for a sample in a dataset (user data type only). 
	 * Used when merging files of different data types measured for a sample.
	 */
	public static final DashDataType SAMPLE_ID = new DashDataType(DashboardUtils.SAMPLE_ID);

	public static final DashDataType LONGITUDE = new DashDataType(DashboardUtils.LONGITUDE);
	public static final DashDataType LATITUDE = new DashDataType(DashboardUtils.LATITUDE);
	public static final DashDataType SAMPLE_DEPTH = new DashDataType(DashboardUtils.SAMPLE_DEPTH);

	/**
	 * Date and time or the measurement
	 */
	public static final DashDataType TIMESTAMP = new DashDataType(DashboardUtils.TIMESTAMP);

	/**
	 * Date of the measurement - no time.
	 */
	public static final DashDataType DATE = new DashDataType(DashboardUtils.DATE);

	public static final DashDataType YEAR = new DashDataType(DashboardUtils.YEAR);
	public static final DashDataType MONTH_OF_YEAR = new DashDataType(DashboardUtils.MONTH_OF_YEAR);
	public static final DashDataType DAY_OF_MONTH = new DashDataType(DashboardUtils.DAY_OF_MONTH);
	public static final DashDataType TIME_OF_DAY = new DashDataType(DashboardUtils.TIME_OF_DAY);
	public static final DashDataType HOUR_OF_DAY = new DashDataType(DashboardUtils.HOUR_OF_DAY);
	public static final DashDataType MINUTE_OF_HOUR = new DashDataType(DashboardUtils.MINUTE_OF_HOUR);
	public static final DashDataType SECOND_OF_MINUTE = new DashDataType(DashboardUtils.SECOND_OF_MINUTE);

	/**
	 * DAY_OF_YEAR, along with YEAR, and possibly SECOND_OF_DAY,
	 * may be used to specify the date and time of the measurement.
	 */
	public static final DashDataType DAY_OF_YEAR = new DashDataType(DashboardUtils.DAY_OF_YEAR);

	/**
	 * SECOND_OF_DAY, along with YEAR and DAY_OF_YEAR may
	 * be used to specify date and time of the measurement
	 */
	public static final DashDataType SECOND_OF_DAY = new DashDataType(DashboardUtils.SECOND_OF_DAY);

	/**
	 * WOCE flag from the automated data checker.
	 */
	public static final DashDataType WOCE_AUTOCHECK = new DashDataType(DashboardUtils.WOCE_AUTOCHECK);

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

}
