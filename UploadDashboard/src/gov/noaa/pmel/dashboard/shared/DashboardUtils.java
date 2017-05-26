/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import com.googlecode.gwt.crypto.client.TripleDesCipher;

/**
 * Static dashboard utility functions and constants
 * for use on both the client and server side.
 * 
 * @author Karl Smith
 */
public class DashboardUtils {

	// Cruise upload action strings
	public static final String REQUEST_PREVIEW_TAG = "REQUEST PREVIEW TAG";
	public static final String REQUEST_NEW_CRUISE_TAG = "REQUEST NEW CRUISE TAG";
	public static final String REQUEST_OVERWRITE_CRUISE_TAG = "REQUEST OVERWRITE CRUISE TAG";

	// Recognized data formats
	public static final String CRUISE_FORMAT_COMMA = "COMMA-SEPARATED VALUES";
	public static final String CRUISE_FORMAT_SEMICOLON = "SEMICOLON-SEPARATED VALUES";
	public static final String CRUISE_FORMAT_TAB = "TAB-SEPARATED VALUES";

	// Cruise upload result strings
	public static final String FILE_PREVIEW_HEADER_TAG = "FILE PREVIEW HEADER TAG";
	public static final String FILE_INVALID_HEADER_TAG = "FILE INVALID HEADER TAG";
	public static final String NO_EXPOCODE_HEADER_TAG = "NO EXPOCODE HEADER TAG";
	public static final String NO_SHIPNAME_HEADER_TAG = "NO SHIPNAME HEADER TAG";
	public static final String NO_PINAMES_HEADER_TAG = "NO PINAMES HEADER TAG";
	public static final String CANNOT_OVERWRITE_HEADER_TAG = "CANNOT OVERWRITE HEADER TAG";
	public static final String NO_DATASET_HEADER_TAG = "NO DATASET HEADER TAG";
	public static final String UNEXPECTED_FAILURE_HEADER_TAG = "UNEXPECTED FAILURE HEADER TAG";
	public static final String FILE_CREATED_HEADER_TAG = "FILE CREATED HEADER TAG";
	public static final String END_OF_ERROR_MESSAGE_TAG = "END_OF_ERROR MESSAGE_TAG";

	/*
	 * Only valid characters for an expocode are upper-case alphanumeric, 
	 * and a hyphen.  The first four or five identify the ship/platform, 
	 * the next four is a year, the next two are a month of year number, 
	 * and the next two are a day of month number.  For the very rare case 
	 * of valid duplicate expocodes (up to this point), a hyphen and number
	 * can follow.
	 */
	public static final String VALID_EXPOCODE_CHARACTERS = 
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-";
	public static final int MIN_EXPOCODE_LENGTH = 12;
	public static final int MAX_EXPOCODE_LENGTH = 15;

	// Maximum number of rows shown in a page of a data grid (table)
	public static final int MAX_ROWS_PER_GRID_PAGE = 50;

	// Maximum number of error messages in an acceptable cruise
	public static final int MAX_ACCEPTABLE_ERRORS = 50;

	// Data check strings
	public static final String CHECK_STATUS_NOT_CHECKED = "";
	public static final String CHECK_STATUS_ACCEPTABLE = "No warnings";
	public static final String CHECK_STATUS_WARNINGS_PREFIX = "Warnings:";
	public static final String CHECK_STATUS_ERRORS_PREFIX = "Errors:";
	public static final String CHECK_STATUS_UNACCEPTABLE = "Unacceptable";
	public static final String GEOPOSITION_ERRORS_MSG = "(lat/lon/time errors!)";

	// Archival options
	public static final String ARCHIVE_STATUS_NOT_SUBMITTED = "";
	public static final String ARCHIVE_STATUS_WITH_SOCAT = "With next SOCAT";
	public static final String ARCHIVE_STATUS_SENT_TO_PREFIX = "Sent to ";
	public static final String ARCHIVE_STATUS_SENT_TO_OCADS = "Sent to OCADS";
	public static final String ARCHIVE_STATUS_SENT_TO_CDIAC = "Sent to CDIAC";
	public static final String ARCHIVE_STATUS_OWNER_ARCHIVE = "Owner to archive";
	public static final String ARCHIVE_STATUS_ARCHIVED = "Archived";

	/**
	 *  Missing value for floating-point variables - not null or NaN
	 */
	public static final Double FP_MISSING_VALUE = -1.0E+34;

	/**
	 *  Missing value for integer variables - not null
	 */
	public static final Integer INT_MISSING_VALUE = -99;

	/**
	 * Missing value for String variables - not null
	 */
	public static final String STRING_MISSING_VALUE = "";

	/**
	 * Missing value for Character variables - not null
	 */
	public static final Character CHAR_MISSING_VALUE = ' ';

	/**
	 * Date used as a missing value - not null; 
	 * corresponds to Jan 2, 3000 00:00:00 GMT
	 */
	public static final Date DATE_MISSING_VALUE = new Date(32503766400429L);

	/** 
	 * Maximum relative error between two floating point values 
	 * still considered the same value for practical purposes. 
	 * Typically used for rtol in {@link #closeTo(Double, Double, double, double)}
	 */
	public static final double MAX_RELATIVE_ERROR = 1.0E-6;

	/** 
	 * Maximum absolute error between two floating point values 
	 * still considered the same value for practical purposes. 
	 * Typically used for atol in {@link #closeTo(Double, Double, double, double)}
	 */
	public static final double MAX_ABSOLUTE_ERROR = 1.0E-6;

	/** Max "distance", in kilometers, still considered a crossover */
	public static final double MAX_CROSSOVER_DIST = 80.0;
	/** "Distance" contribution, in kilometers, for every 24h time difference */
	public static final double SEAWATER_SPEED = 30.0;
	/** Maximum difference in FCO2_rec for a high-quality crossover */
	public static final double MAX_FCO2_DIFF = 5.0;
	/** Maximum difference in SST for a high-quality crossover */
	public static final double MAX_TEMP_DIFF = 0.3;
	/** Authalic radius, in kilometers, of Earth */
	public static final double EARTH_AUTHALIC_RADIUS = 6371.007;
	/** Max allowable difference in time, in seconds, between two crossover data points */
	public static final double MAX_TIME_DIFF = Math.ceil(24.0 * 60.0 * 60.0 * MAX_CROSSOVER_DIST / SEAWATER_SPEED);
	/** Max allowable difference in latitude, in degrees, between two crossover data points */
	public static final double MAX_LAT_DIFF = (MAX_CROSSOVER_DIST / EARTH_AUTHALIC_RADIUS) * (180.0 / Math.PI);

	/** Sanity Checker "username" for flags */
	public static final String SANITY_CHECKER_USERNAME = "automated.data.checker";
	/** Sanity Checker "realname" for flags */
	public static final String SANITY_CHECKER_REALNAME = "automated data checker";

	/**
	 * The "upload filename" for all OME metadata files.
	 */
	public static final String OME_FILENAME = "OME.xml";

	/**
	 * THe PDF version of the OME XML files.
	 */
	public static final String OME_PDF_FILENAME = "OME.pdf";

	/**
	 * The "upload filename" for all PI-provided OME metadata files 
	 * that are not used for anything other than generating a supplemental 
	 * document.
	 * 
	 * The use of this name is just a temporary measure 
	 * until the CDIAC OME brought into the dashboard.
	 */
	public static final String PI_OME_FILENAME = "PI_OME.xml";

	/**
	 * The PDF version of the PI OME XML file.
	 */
	public static final String PI_OME_PDF_FILENAME = "PI_OME.pdf";

	public static final Character GLOBAL_REGION_ID = 'G';
	public static final Character NORTH_PACIFIC_REGION_ID = 'N';
	public static final Character TROPICAL_PACIFIC_REGION_ID = 'T';
	public static final Character NORTH_ATLANTIC_REGION_ID = 'A';
	public static final Character TROPICAL_ATLANTIC_REGION_ID = 'Z';
	public static final Character INDIAN_REGION_ID = 'I';
	public static final Character COASTAL_REGION_ID = 'C';
	public static final Character SOUTHERN_OCEANS_REGION_ID = 'O';
	public static final Character ARCTIC_REGION_ID = 'R';

	public static final HashMap<Character,String> REGION_NAMES;
	static {
		REGION_NAMES = new HashMap<Character,String>();
		REGION_NAMES.put(GLOBAL_REGION_ID, "Global");
		REGION_NAMES.put(NORTH_PACIFIC_REGION_ID, "North Pacific");
		REGION_NAMES.put(TROPICAL_PACIFIC_REGION_ID, "Tropical Pacific");
		REGION_NAMES.put(NORTH_ATLANTIC_REGION_ID, "North Atlantic");
		REGION_NAMES.put(TROPICAL_ATLANTIC_REGION_ID, "Tropical Atlantic");
		REGION_NAMES.put(INDIAN_REGION_ID, "Indian");
		REGION_NAMES.put(COASTAL_REGION_ID, "Coastal");
		REGION_NAMES.put(SOUTHERN_OCEANS_REGION_ID, "Southern Oceans");
		REGION_NAMES.put(ARCTIC_REGION_ID, "Artic");
	}

	// All possible QC flags
	public static final Character QC_A_FLAG = 'A';
	public static final Character QC_B_FLAG = 'B';
	public static final Character QC_C_FLAG = 'C';
	public static final Character QC_D_FLAG = 'D';
	public static final Character QC_E_FLAG = 'E';
	public static final Character QC_COMMENT = 'H';
	public static final Character QC_NEW_FLAG = 'N';
	public static final Character QC_CONFLICT_FLAG = 'Q';
	public static final Character QC_RENAMED_FLAG = 'R';
	public static final Character QC_SUSPEND_FLAG = 'S';
	public static final Character QC_UPDATED_FLAG = 'U';
	public static final Character QC_EXCLUDE_FLAG = 'X';

	// Cruise QC strings - cruises that can be modified
	public static final String QC_STATUS_NOT_SUBMITTED = "";
	public static final String QC_STATUS_SUSPENDED = "Suspended";
	public static final String QC_STATUS_EXCLUDED = "Excluded";
	// Cruise QC strings - cruises that cannot be modified
	public static final String QC_STATUS_SUBMITTED = "Submitted";
	public static final String QC_STATUS_ACCEPTED_A = "Flag A";
	public static final String QC_STATUS_ACCEPTED_B = "Flag B";
	public static final String QC_STATUS_ACCEPTED_C = "Flag C";
	public static final String QC_STATUS_ACCEPTED_D = "Flag D";
	public static final String QC_STATUS_ACCEPTED_E = "Flag E";
	public static final String QC_STATUS_CONFLICT = "Conflict";
	public static final String QC_STATUS_RENAMED = "Renamed";

	/**
	 * Map of QC status flag characters to QC status strings
	 */
	public static final HashMap<Character,String> FLAG_STATUS_MAP;
	static {
		FLAG_STATUS_MAP = new HashMap<Character,String>();
		FLAG_STATUS_MAP.put(QC_A_FLAG, QC_STATUS_ACCEPTED_A);
		FLAG_STATUS_MAP.put(QC_B_FLAG, QC_STATUS_ACCEPTED_B);
		FLAG_STATUS_MAP.put(QC_C_FLAG, QC_STATUS_ACCEPTED_C);
		FLAG_STATUS_MAP.put(QC_D_FLAG, QC_STATUS_ACCEPTED_D);
		FLAG_STATUS_MAP.put(QC_E_FLAG, QC_STATUS_ACCEPTED_E);
		FLAG_STATUS_MAP.put(QC_NEW_FLAG, QC_STATUS_SUBMITTED);
		FLAG_STATUS_MAP.put(QC_CONFLICT_FLAG, QC_STATUS_CONFLICT);
		FLAG_STATUS_MAP.put(QC_RENAMED_FLAG, QC_STATUS_RENAMED);
		FLAG_STATUS_MAP.put(QC_SUSPEND_FLAG, QC_STATUS_SUSPENDED);
		FLAG_STATUS_MAP.put(QC_UPDATED_FLAG, QC_STATUS_SUBMITTED);
		FLAG_STATUS_MAP.put(QC_EXCLUDE_FLAG, QC_STATUS_EXCLUDED);
		// Map old 'F' flag to suspended
		FLAG_STATUS_MAP.put('F', QC_STATUS_SUSPENDED);
	}

	/**
	 * Map of QC status strings to QC status flag characters 
	 * QC_STATUS_SUBMITTED is mapped to QC_UPDATED_FLAG
	 */
	public static final HashMap<String,Character> STATUS_FLAG_MAP;
	static {
		STATUS_FLAG_MAP = new HashMap<String,Character>();
		STATUS_FLAG_MAP.put(QC_STATUS_ACCEPTED_A, QC_A_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_ACCEPTED_B, QC_B_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_ACCEPTED_C, QC_C_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_ACCEPTED_D, QC_D_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_ACCEPTED_E, QC_E_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_CONFLICT, QC_CONFLICT_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_RENAMED, QC_RENAMED_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_SUSPENDED, QC_SUSPEND_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_SUBMITTED, QC_UPDATED_FLAG);
		STATUS_FLAG_MAP.put(QC_STATUS_EXCLUDED, QC_EXCLUDE_FLAG);
	}

	public static final String PI_PROVIDED_WOCE_COMMENT_START = "PI provided WOCE-";

	// flags for WOCE events of current cruises
	public static final Character WOCE_GOOD = '2';
	public static final Character WOCE_NOT_CHECKED = '2';
	public static final Character WOCE_QUESTIONABLE = '3';
	public static final Character WOCE_BAD = '4';
	public static final Character WOCE_NO_DATA = '9';

	// flags for WOCE events of cruises that has been updated
	public static final Character OLD_WOCE_GOOD = 'G';
	public static final Character OLD_WOCE_NOT_CHECKED = 'G';
	public static final Character OLD_WOCE_QUESTIONABLE = 'Q';
	public static final Character OLD_WOCE_BAD = 'B';
	public static final Character OLD_WOCE_NO_DATA = 'M';

	// flag for cruise rename WOCE events
	public static final Character WOCE_RENAME = 'R';

	// Supported data class names
	public static final String CHAR_DATA_CLASS_NAME = "Character";
	public static final String DATE_DATA_CLASS_NAME = "Date";
	public static final String DOUBLE_DATA_CLASS_NAME = "Double";
	public static final String INT_DATA_CLASS_NAME = "Integer";
	public static final String STRING_DATA_CLASS_NAME = "String";

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

	/** For data without any specific units */
	public static final ArrayList<String> NO_UNITS = 
			new ArrayList<String>(Arrays.asList(""));

	/** Formats for date-time stamps */
	public static final ArrayList<String> TIMESTAMP_UNITS = 
			new ArrayList<String>(Arrays.asList(
					"yyyy-mm-dd hh:mm:ss", 
					"mm-dd-yyyy hh:mm:ss", 
					"dd-mm-yyyy hh:mm:ss", 
					"mm-dd-yy hh:mm:ss", 
					"dd-mm-yy hh:mm:ss"));

	/** Formats for dates */
	public static final ArrayList<String> DATE_UNITS = 
			new ArrayList<String>(Arrays.asList(
					"yyyy-mm-dd", 
					"mm-dd-yyyy", 
					"dd-mm-yyyy", 
					"mm-dd-yy", 
					"dd-mm-yy"));

	/** Formats for time-of-day */
	public static final ArrayList<String> TIME_OF_DAY_UNITS = 
			new ArrayList<String>(Arrays.asList("hh:mm:ss"));

	/** Units for day-of-year (value of the first day of the year) */
	public static final ArrayList<String> DAY_OF_YEAR_UNITS = 
			new ArrayList<String>(Arrays.asList("Jan1=1.0", "Jan1=0.0"));

	/** Units for longitude */
	public static final ArrayList<String> LONGITUDE_UNITS = 
			new ArrayList<String>(Arrays.asList("degrees_east", "degrees_west"));

	/** Units of latitude */
	public static final ArrayList<String> LATITUDE_UNITS = 
			new ArrayList<String>(Arrays.asList("degrees_north", "degrees_south"));

	/** Unit of depth */
	public static final ArrayList<String> DEPTH_UNITS = 
			new ArrayList<String>(Arrays.asList("meters"));

	/** Unit of completely specified time ("seconds since 1970-01-01T00:00:00Z") */
	public static final ArrayList<String> TIME_UNITS = 
			new ArrayList<String>(Arrays.asList("seconds since 1970-01-01T00:00:00Z"));



	/** 
	 * GEOPOSITION is a marker data type used in automated data checking
	 * to indicate an severe error in the combination of lon/lat/time. 
	 */
	public static final DataColumnType GEOPOSITION = new DataColumnType("geoposition", 
			-1.0, "(geoposition)", null, null, null, null, NO_UNITS);

	/**
	 * UNKNOWN needs to be respecified as one of the (other) data column types.
	 */
	public static final DataColumnType UNKNOWN = new DataColumnType("unknown", 
			0.0, "(unknown)", null, null, null, null, NO_UNITS);

	/**
	 * OTHER is for supplementary data in the user's original data file but 
	 * otherwise not used.  A description of each column with this type must 
	 * be part of the metadata, but the values are not validated or used. 
	 * Multiple columns may have this type.
	 */
	public static final DataColumnType OTHER = new DataColumnType("other",
			1.0, "other", null, null, null, null, NO_UNITS);



	/**
	 * Unique identifier for the dataset.
	 * For SOCAT, the expocode is NODCYYYYMMDD where NODC is the ship code 
	 * and YYYY-MM-DD is the start date for the cruise; and possibly followed
	 * by -1 or -2 for non-ship platforms - where NODC is does not distinguish
	 * different platform names.
	 */
	public static final DataColumnType EXPOCODE = new DataColumnType("expocode", 
			100.0, "expocode", STRING_DATA_CLASS_NAME, "expocode", null, 
			IDENTIFIER_CATEGORY, NO_UNITS);
	
	/**
	 * User-provided name for the dataset
	 */
	public static final DataColumnType DATASET_NAME = new DataColumnType("dataset_name", 
			101.0, "dataset", STRING_DATA_CLASS_NAME, "dataset name", null, 
			IDENTIFIER_CATEGORY, NO_UNITS);

	public static final DataColumnType PLATFORM_NAME = new DataColumnType("platform_name", 
			102.0, "platform name", STRING_DATA_CLASS_NAME, "platform name", "platform_name",
			PLATFORM_CATEGORY, NO_UNITS);

	public static final DataColumnType ORGANIZATION_NAME = new DataColumnType("organization", 
			103.0, "organization", STRING_DATA_CLASS_NAME, "organization", null, 
			IDENTIFIER_CATEGORY, NO_UNITS);
	
	public static final DataColumnType INVESTIGATOR_NAMES = new DataColumnType("investigators", 
			104.0, "PI names", STRING_DATA_CLASS_NAME, "investigators", null, 
			IDENTIFIER_CATEGORY, NO_UNITS);

	public static final DataColumnType PLATFORM_TYPE = new DataColumnType("platform_type", 
			105.0, "platform type", STRING_DATA_CLASS_NAME, "platform type", null, 
			IDENTIFIER_CATEGORY, NO_UNITS);


	public static final DataColumnType WESTERNMOST_LONGITUDE = new DataColumnType("geospatial_lon_min", 
			110.0, "westmost lon", DOUBLE_DATA_CLASS_NAME, "westernmost longitude", "geospatial_lon_min", 
			LOCATION_CATEGORY, LONGITUDE_UNITS);

	public static final DataColumnType EASTERNMOST_LONGITUDE = new DataColumnType("geospatial_lon_max", 
			111.0, "eastmost lon", DOUBLE_DATA_CLASS_NAME, "easternmost longitude", "geospatial_lon_max", 
			LOCATION_CATEGORY, LONGITUDE_UNITS);

	public static final DataColumnType SOUTHERNMOST_LATITUDE = new DataColumnType("geospatial_lat_min", 
			112.0, "southmost lat", DOUBLE_DATA_CLASS_NAME, "southernmost latitude", "geospatial_lat_min", 
			LOCATION_CATEGORY, LATITUDE_UNITS);

	public static final DataColumnType NORTHERNMOST_LATITUDE = new DataColumnType("geospatial_lat_max", 
			113.0, "northmost lat", DOUBLE_DATA_CLASS_NAME, "northernmost latitude", "geospatial_lat_max", 
			LOCATION_CATEGORY, LATITUDE_UNITS);

	public static final DataColumnType TIME_COVERAGE_START = new DataColumnType("time_coverage_start", 
			114.0, "begin time", DATE_DATA_CLASS_NAME, "beginning time", "time_coverage_start", 
			TIME_CATEGORY, NO_UNITS);

	public static final DataColumnType TIME_COVERAGE_END = new DataColumnType("time_coverage_end", 
			115.0, "end time", DATE_DATA_CLASS_NAME, "ending time", "time_coverage_end", 
			TIME_CATEGORY, NO_UNITS);

	public static final DataColumnType QC_FLAG = new DataColumnType("qc_flag", 
			120.0, "QC flag", STRING_DATA_CLASS_NAME, "QC flag", null, 
			QUALITY_CATEGORY, NO_UNITS);



	public static final DataColumnType SAMPLE_NUMBER = new DataColumnType("sample_number", 
			500.0, "sample num", INT_DATA_CLASS_NAME, "sample number", null, 
			IDENTIFIER_CATEGORY, NO_UNITS);

	public static final DataColumnType LONGITUDE = new DataColumnType("longitude", 
			501.0, "longitude", DOUBLE_DATA_CLASS_NAME, "longitude", "longitude", 
			LOCATION_CATEGORY, LONGITUDE_UNITS);

	public static final DataColumnType LATITUDE = new DataColumnType("latitude", 
			502.0, "latitude", DOUBLE_DATA_CLASS_NAME, "latitude", "latitude", 
			LOCATION_CATEGORY, LATITUDE_UNITS);

	public static final DataColumnType SAMPLE_DEPTH = new DataColumnType("sample_depth", 
			503.0, "sample depth", DOUBLE_DATA_CLASS_NAME, "sample depth", "depth", 
			BATHYMETRY_CATEGORY, DEPTH_UNITS);

	/**
	 * Completely specified time (seconds since 1970-01-01T00:00:00Z) 
	 * used in file data.  Computed value; not a user type
	 */
	public static final DataColumnType TIME = new DataColumnType("time", 
			504.0, "time", DOUBLE_DATA_CLASS_NAME, "time", "time", 
			TIME_CATEGORY, TIME_UNITS);

	/**
	 * Date and time or the measurement
	 */
	public static final DataColumnType TIMESTAMP = new DataColumnType("date_time", 
			505.0, "date time", STRING_DATA_CLASS_NAME, "date and time", null, 
			null, TIMESTAMP_UNITS);

	/**
	 * Date of the measurement - no time.
	 */
	public static final DataColumnType DATE = new DataColumnType("date", 
			506.0, "date", STRING_DATA_CLASS_NAME, "date", null, 
			null, DATE_UNITS);

	public static final DataColumnType YEAR = new DataColumnType("year", 
			507.0, "year", INT_DATA_CLASS_NAME, "year", "year", 
			TIME_CATEGORY, NO_UNITS);

	public static final DataColumnType MONTH_OF_YEAR = new DataColumnType("month", 
			508.0, "month of year", INT_DATA_CLASS_NAME, "month of year", "month_of_year",
			TIME_CATEGORY, NO_UNITS);
	
	public static final DataColumnType DAY_OF_MONTH = new DataColumnType("day", 
			509.0, "day of month", INT_DATA_CLASS_NAME, "day of month", "day_of_month", 
			TIME_CATEGORY, NO_UNITS);

	public static final DataColumnType TIME_OF_DAY = new DataColumnType("time_of_day", 
			510.0, "time of day", STRING_DATA_CLASS_NAME, "time of day", null, 
			null, TIME_OF_DAY_UNITS);

	public static final DataColumnType HOUR_OF_DAY = new DataColumnType("hour", 
			511.0, "hour of day", INT_DATA_CLASS_NAME, "hour of day", "hour_of_day", 
			TIME_CATEGORY, NO_UNITS);

	public static final DataColumnType MINUTE_OF_HOUR = new DataColumnType("minute", 
			512.0, "minute of hour", INT_DATA_CLASS_NAME, "minute of hour", "minute_of_hour", 
			TIME_CATEGORY, NO_UNITS);

	public static final DataColumnType SECOND_OF_MINUTE = new DataColumnType("second", 
			513.0, "sec of minute", DOUBLE_DATA_CLASS_NAME, "second of minute", "second_of_minute", 
			TIME_CATEGORY, NO_UNITS);

	/**
	 * DAY_OF_YEAR, along with YEAR, and possibly SECOND_OF_DAY,
	 * may be used to specify the date and time of the measurement.
	 */
	public static final DataColumnType DAY_OF_YEAR = new DataColumnType("day_of_year", 
			514.0, "day of year", DOUBLE_DATA_CLASS_NAME, "day of year", "day_of_year", 
			TIME_CATEGORY, DAY_OF_YEAR_UNITS);

	/**
	 * SECOND_OF_DAY, along with YEAR and DAY_OF_YEAR may
	 * be used to specify date and time of the measurement
	 */
	public static final DataColumnType SECOND_OF_DAY = new DataColumnType("sec_of_day", 
			515.0, "sec of day", DOUBLE_DATA_CLASS_NAME, "second of day", "second_of_day", 
			TIME_CATEGORY, NO_UNITS);



	/**
	 * "Cleans" a username for use by substituting characters that are  
	 * problematic (such as space characters).  
	 * Also converts all alphabetic characters to lowercase.
	 * An empty string is returned if username is null.
	 * 
	 * @param username
	 * 		username to clean
	 * @return
	 * 		clean version of username
	 */
	public static String cleanUsername(String username) {
		if ( username == null )
			return "";
		return username.replace(' ', '_').toLowerCase();
	}

	/**
	 * Generate the encrypted password for a given plain-text username 
	 * and password.  This is intended to only be a first level of
	 * encryption.
	 * 
	 * @param username
	 * 		plain-text username to use
	 * @param password
	 * 		plain-text password to use 
	 * @return
	 * 		encrypted password, or an empty string if an error occurs 
	 */
	public static String passhashFromPlainText(String username, String password) {
		// This salt is just to make sure the keys are long enough
		String salt = "4z#Ni!q?F7b0m9nK(uDF[g%T3pD_";

		// Make sure something reasonable Strings are given
		if ( (username.length() < 7) || (password.length() < 7) ) {
			return "";
		}
		String name = cleanUsername(username);

		// Encrypt the password
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey((name.substring(0,6) + password + salt)
			  .substring(0,24).getBytes());
		String passhash;
		try {
			passhash = cipher.encrypt((password + salt).substring(0,32));
		} catch (Exception ex) {
			passhash = "";
		}

		return passhash;
	}

	/**
	 * Decodes a (JSON-like) encoded array of numbers into a byte array. 
	 * Numeric values are separated by a comma, which may have whitespace
	 * around it.
	 * 
	 * @param arrayStr
	 * 		JSON-encoded array of byte values to use
	 * @return
	 * 		a byte array represented arrayStr
	 * @throws NumberFormatException
	 * 		if arrayStr does not start with '[', does not end with ']', 
	 * 		or contains values inappropriate for the byte type
	 */
	public static byte[] decodeByteArray(String arrayStr) 
										throws NumberFormatException {
		if ( ! ( arrayStr.startsWith("[") && arrayStr.endsWith("]") ) )
			throw new NumberFormatException(
					"Encoded byte array not enclosed in brackets");
		String[] pieces = arrayStr.substring(1, arrayStr.length()-1)
								  .split("\\s*,\\s*", -1);
		if ( (pieces.length == 1) && pieces[0].trim().isEmpty() )
			return new byte[0];
		byte[] byteArray = new byte[pieces.length];
		for (int k = 0; k < pieces.length; k++)
			byteArray[k] = Byte.parseByte(pieces[k].trim());
		return byteArray;
	}

	/**
	 * Encodes an ArrayList of Integers suitable for decoding 
	 * with {@link #decodeIntegerArrayList(String)}
	 * 
	 * @param intList
	 * 		list of integer values to encode
	 * @return
	 * 		the encoded list of integer values
	 */
	public static String encodeIntegerArrayList(ArrayList<Integer> intList) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		boolean firstValue = true;
		for ( Integer intVal : intList ) {
			if ( firstValue )
				firstValue = false;
			else
				sb.append(", ");
			sb.append(intVal.toString());
		}
		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * Decodes a encoded array of numbers produced by 
	 * {@link #encodeIntegerArrayList(ArrayList)}
	 * into an ArrayList of Integers.
	 * 
	 * @param arrayStr
	 * 		encoded array of integer values to use
	 * @return
	 * 		the decoded ArrayList of Integers; never null but may be empty
	 * @throws NumberFormatException
	 * 		if arrayStr does not start with '[', does not end with ']', 
	 * 		or contains values inappropriate for the integer type
	 */
	public static ArrayList<Integer> decodeIntegerArrayList(String arrayStr) 
										throws NumberFormatException {
		if ( ! ( arrayStr.startsWith("[") && arrayStr.endsWith("]") ) )
			throw new NumberFormatException(
					"Encoded integer array not enclosed in brackets");
		String[] pieces = arrayStr.substring(1, arrayStr.length()-1)
								  .split("\\s*,\\s*", -1);
		if ( (pieces.length == 1) && pieces[0].trim().isEmpty() )
			return new ArrayList<Integer>(0);
		ArrayList<Integer> intList = new ArrayList<Integer>(pieces.length);
		for ( String strVal : pieces )
			intList.add(Integer.parseInt(strVal.trim()));
		return intList;
	}

	/**
	 * Encodes an ArrayList of strings suitable for decoding using 
	 * {@link #decodeStringArrayList(String)}.  Characters within
	 * the strings are copied as-is, thus newline characters, or
	 * the character sequence double quote - comma - double quote, 
	 * within a string will likely cause problems when reading or 
	 * decoding the encoded string.
	 * 
	 * @param strList
	 * 		the ArrayList of strings to encode
	 * @return
	 * 		the encoded string array
	 */
	public static String encodeStringArrayList(ArrayList<String> strList) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		boolean firstValue = true;
		for ( String strVal : strList ) {
			if ( firstValue )
				firstValue = false;
			else
				sb.append(", ");
			sb.append("\"");
			sb.append(strVal);
			sb.append("\"");
		}
		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * Decodes an encoded string array produced by 
	 * {@link #encodeStringArrayList(ArrayList)}, into an 
	 * ArrayList of strings.  Each string must be enclosed in double 
	 * quotes; escaped characters within a string are not recognized 
	 * or modified.  Strings must be separated by commas.  Whitespace 
	 * around the comma is allowed.
	 * 
	 * @param arrayStr
	 * 		the encoded string array
	 * @return
	 * 		the decoded ArrayList of strings; never null, but may
	 * 		be empty (if the encoded string array contains no strings)
	 * @throws IllegalArgumentException
	 * 		if arrayStr does not start with '[', does not end with ']', 
	 * 		or contains strings not enclosed within double quotes.
	 */
	public static ArrayList<String> decodeStringArrayList(String arrayStr) 
									throws IllegalArgumentException {
		if ( ! ( arrayStr.startsWith("[") && arrayStr.endsWith("]") ) )
			throw new IllegalArgumentException(
					"Encoded string array not enclosed in brackets");
		String contents = arrayStr.substring(1, arrayStr.length() - 1);
		if ( contents.trim().isEmpty() )
			return new ArrayList<String>(0);
		int firstIndex = contents.indexOf("\"");
		int lastIndex = contents.lastIndexOf("\"");
		if ( (firstIndex < 0) || (lastIndex == firstIndex) ||
			 ( ! contents.substring(0, firstIndex).trim().isEmpty() ) ||
			 ( ! contents.substring(lastIndex+1).trim().isEmpty() ) )
			throw new IllegalArgumentException("Strings in encoded " +
					"string array are not enclosed in double quotes");
		String[] pieces = contents.substring(firstIndex+1, lastIndex)
								  .split("\"\\s*,\\s*\"", -1);
		return new ArrayList<String>(Arrays.asList(pieces));
	}

	/**
	 * Encodes a set of WoceType objects suitable for decoding 
	 * with {@link #decodeWoceTypeSet(String)}
	 * 
	 * @param intList
	 * 		list of integer values to encode
	 * @return
	 * 		the encoded list of integer values
	 */
	public static String encodeWoceTypeSet(TreeSet<WoceType> woceSet) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		boolean firstValue = true;
		for ( WoceType woce : woceSet ) {
			if ( firstValue )
				firstValue = false;
			else
				sb.append(", ");
			sb.append("[ ");
			sb.append(woce.getColumnIndex().toString());
			sb.append(", ");
			sb.append(woce.getRowIndex().toString());
			sb.append(", \"");
			sb.append(woce.getWoceName());
			sb.append("\" ]");
		}
		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * Decodes an encoded set of WoceType's produced by 
	 * {@link #encodeStringArrayList(ArrayList)}, into a 
	 * TreeSet of WoceTypes.
	 * 
	 * @param woceSetStr
	 * 		the encoded set of WoceType's
	 * @return
	 * 		the decoded TreeSet of WoceType's; never null, but may
	 * 		be empty (if the encoded set does not contain any WoceType's)
	 * @throws IllegalArgumentException
	 * 		if woceSetStr does not start with '[', does not end with ']', 
	 * 		or contains an invalid encoded WoceType.
	 */
	public static TreeSet<WoceType> decodeWoceTypeSet(String woceSetStr) {
		if ( ! ( woceSetStr.startsWith("[") && woceSetStr.endsWith("]") ) )
			throw new IllegalArgumentException(
					"Encoded WoceType set not enclosed in brackets");
		String contents = woceSetStr.substring(1, woceSetStr.length() - 1);
		if ( contents.trim().isEmpty() )
			return new TreeSet<WoceType>();
		int firstIndex = contents.indexOf("[");
		int lastIndex = contents.lastIndexOf("]");
		if ( (firstIndex < 0) || (lastIndex < 0) || 
			 ( ! contents.substring(0, firstIndex).trim().isEmpty() ) ||
			 ( ! contents.substring(lastIndex+1).trim().isEmpty() ) )
			throw new IllegalArgumentException("Invalid encoding of a set of WoceTypes: " +
					"a WoceType not enclosed in brackets");
		String[] pieces = contents.substring(firstIndex+1, lastIndex)
								  .split("\\]\\s*,\\s*\\[", -1);
		TreeSet<WoceType> woceSet = new TreeSet<WoceType>();
		for ( String encWoce : pieces ) {
			String[] woceParts = encWoce.split(",", 3);
			try {
				if ( woceParts.length != 3 )
					throw new IllegalArgumentException("incomplete WoceType description");
				Integer colIndex = Integer.parseInt(woceParts[0].trim());
				Integer rowIndex = Integer.parseInt(woceParts[1].trim());
				firstIndex = woceParts[2].indexOf("\"");
				lastIndex = woceParts[2].lastIndexOf("\"");
				if ( (firstIndex < 1) || (lastIndex == firstIndex) ||
					 ( ! woceParts[2].substring(0, firstIndex).trim().isEmpty() ) ||
					 ( ! woceParts[2].substring(lastIndex+1).trim().isEmpty() ) )
					throw new IllegalArgumentException("WOCE name not enclosed in double quotes");
				String woceName = woceParts[2].substring(firstIndex+1, lastIndex);
				woceSet.add(new WoceType(woceName, colIndex, rowIndex));
			} catch ( Exception ex ) {
				throw new IllegalArgumentException("Invalid encoding of a set of WoceTypes: " + 
						ex.getMessage(), ex);
			}
		}
		return woceSet;
	}

	/**
	 * Returns the basename of a filename.  Does this by returning only the
	 * portion of the string after the last slash or backslash character 
	 * (either one if both present).
	 * 
	 * If null is given, or if the name ends in a slash or backslash, an empty 
	 * string is returned.  Whitespace is trimmed from the returned name.
	 */
	public static String baseName(String filename) {
		if ( filename == null )
			return "";

		String basename = filename;
		int idx = basename.lastIndexOf('/');
		if ( idx >= 0 ) {
			idx++;
			if ( basename.length() == idx )
				return "";
			else
				basename = basename.substring(idx);
		}
		idx = basename.lastIndexOf('\\');
		if ( idx >= 0 ) {
			idx++;
			if ( basename.length() == idx )
				return "";
			else
				basename = basename.substring(idx);
		}
		return basename.trim();
	}

	/**
	 * Determines if two longitudes are close to the same value
	 * modulo 360.0.  The absolute of the average value, absAver, 
	 * and the absolute value in the difference in values, absDiff,
	 * of first and second are determined.
	 *  
	 * The difference between is considered negligible if: 
	 *     absDiff < absAver * rtol + atol 
	 * 
	 * This comparison is made to the values as given as well as for
	 * each value with 360.0 added to it.  
	 * (So not a complete modulo 360 check.)
	 * 
	 * @param first 
	 * 		value to compare
	 * @param second 
	 * 		value to compare
	 * @param rtol
	 * 		relative tolerance of the difference
	 * @param atol
	 * 		absolute tolerance of the difference
	 * @return 
	 * 		true is first and second are both NaN, both Infinite
	 * 		(regardless of whether positive or negative), or 
	 * 		have values whose difference is "negligible".
	 */
	public static boolean longitudeCloseTo(Double first, Double second, 
										double rtol, double atol) {
		// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
		if ( closeTo(first, second, rtol, atol) )
			return true;
		if ( closeTo(first + 360.0, second, rtol, atol) )
			return true;
		if ( closeTo(first, second + 360.0, rtol, atol) )
			return true;
		return false;
	}

	/**
	 * Determines if two Doubles are close to the same value.
	 * The absolute of the average value, absAver, and the 
	 * absolute value in the difference in values, absDiff,
	 * of first and second are determined.
	 *  
	 * The difference between is considered negligible if: 
	 *     absDiff < absAver * rtol + atol 
	 * 
	 * @param first 
	 * 		value to compare
	 * @param second 
	 * 		value to compare
	 * @param rtol
	 * 		relative tolerance of the difference
	 * @param atol
	 * 		absolute tolerance of the difference
	 * @return 
	 * 		true is first and second are both NaN, both Infinite
	 * 		(regardless of whether positive or negative), or 
	 * 		have values whose difference is "negligible".
	 */
	public static boolean closeTo(Double first, Double second, 
									double rtol, double atol) {

		// NaN (only) matches NaN
		if ( first.isNaN() ) {
			return second.isNaN();
		}
		if ( second.isNaN() ) {
			return false;
		}

		// Positive or negative infinity (only) matches 
		// positive or negative infinity
		if ( first.isInfinite() ) {
			return second.isInfinite();
		}
		if ( second.isInfinite() ) {
			return false;
		}

		// Check if they are the same value
		if ( first.equals(second) )
			return true;

		// Check if values are close
		double absDiff = Math.abs(first - second);
		double absAver = Math.abs((first + second) * 0.5);
		return ( absDiff < absAver * rtol + atol );
	}

}