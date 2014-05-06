/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
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

	// Cruise upload result strings
	public static final String FILE_PREVIEW_HEADER_TAG = "FILE PREVIEW HEADER TAG";
	public static final String NO_EXPOCODE_HEADER_TAG = "NO EXPOCODE HEADER TAG";
	public static final String FILE_EXISTS_HEADER_TAG = "FILE EXISTS HEADER TAG";
	public static final String CANNOT_OVERWRITE_HEADER_TAG = "CANNOT OVERWRITE HEADER TAG";
	public static final String NO_FILE_HEADER_TAG = "NO FILE HEADER TAG";
	public static final String FILE_CREATED_HEADER_TAG = "FILE CREATED HEADER TAG";
	public static final String FILE_UPDATED_HEADER_TAG = "FILE UPDATED HEADER TAG";

	/*
	 * Only valid characters for an expocode are upper-case alphanumeric, 
	 * underscore, and hyphen; the latter two are for the very rare case 
	 * of valid duplicate expocodes. 
	 */
	public static final String VALID_EXPOCODE_CHARACTERS = 
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-";
	public static final int MIN_EXPOCODE_LENGTH = 12;
	public static final int MAX_EXPOCODE_LENGTH = 14;

	// Recognized data formats
	public static final String CRUISE_FORMAT_COMMA = "data are comma-separated values";
	public static final String CRUISE_FORMAT_TAB = "data are tab-separated values";

	// Maximum number of rows shown in a page of a data grid (table)
	public static final int MAX_ROWS_PER_GRID_PAGE = 50;

	// Maximum number of error messages in an acceptable cruise
	public static final int MAX_ACCEPTABLE_ERRORS = 49;

	// Data check strings
	public static final String CHECK_STATUS_NOT_CHECKED = "";
	public static final String CHECK_STATUS_ACCEPTABLE = "No warnings";
	public static final String CHECK_STATUS_WARNINGS_PREFIX = "Warnings:";
	public static final String CHECK_STATUS_ERRORS_PREFIX = "Errors:";
	public static final String CHECK_STATUS_UNACCEPTABLE = "Unacceptable";

	// Cruise QC strings
	public static final String QC_STATUS_NOT_SUBMITTED = "";
	public static final String QC_STATUS_SUBMITTED = "Submitted";
	public static final String QC_STATUS_ACCEPTED_A = "Flag A";
	public static final String QC_STATUS_ACCEPTED_B = "Flag B";
	public static final String QC_STATUS_ACCEPTED_C = "Flag C";
	public static final String QC_STATUS_ACCEPTED_D = "Flag D";
	public static final String QC_STATUS_ACCEPTED_E = "Flag E";
	public static final String QC_STATUS_UNACCEPTABLE = "Flag F";
	public static final String QC_STATUS_SUSPENDED = "Suspended";
	public static final String QC_STATUS_EXCLUDED = "Excluded";

	// Archival options
	public static final String ARCHIVE_STATUS_NOT_SUBMITTED = "";
	public static final String ARCHIVE_STATUS_WITH_SOCAT = "With next SOCAT";
	public static final String ARCHIVE_STATUS_SENT_CDIAC = "Sent to CDIAC";
	public static final String ARCHIVE_STATUS_OWNER_ARCHIVE = "Owner to archive";
	public static final String ARCHIVE_STATUS_ARCHIVED = "Archived";

	// Sanity Checker "username" and "realname" for flags
	public static final String SANITY_CHECKER_USERNAME = "automated.data.checker";
	public static final String SANITY_CHECKER_REALNAME = "automated data checker";

	/**
	 * Header names of the standard data columns that a user might provide
	 */
	public static final EnumMap<DataColumnType,String> STD_HEADER_NAMES = 
			new EnumMap<DataColumnType,String>(DataColumnType.class);
	static {
		STD_HEADER_NAMES.put(DataColumnType.UNKNOWN, "(unknown)");
		STD_HEADER_NAMES.put(DataColumnType.EXPOCODE, "expocode");
		STD_HEADER_NAMES.put(DataColumnType.CRUISE_NAME, "cruise_name");
		STD_HEADER_NAMES.put(DataColumnType.SHIP_NAME, "ship_name");
		STD_HEADER_NAMES.put(DataColumnType.GROUP_NAME, "group_name");

		STD_HEADER_NAMES.put(DataColumnType.TIMESTAMP, "date_time");
		STD_HEADER_NAMES.put(DataColumnType.DATE, "date");
		STD_HEADER_NAMES.put(DataColumnType.YEAR, "year");
		STD_HEADER_NAMES.put(DataColumnType.MONTH, "month");
		STD_HEADER_NAMES.put(DataColumnType.DAY, "day");
		STD_HEADER_NAMES.put(DataColumnType.TIME, "time");
		STD_HEADER_NAMES.put(DataColumnType.HOUR, "hour");
		STD_HEADER_NAMES.put(DataColumnType.MINUTE, "minute");
		STD_HEADER_NAMES.put(DataColumnType.SECOND, "second");
		STD_HEADER_NAMES.put(DataColumnType.DAY_OF_YEAR, "day_of_year");

		STD_HEADER_NAMES.put(DataColumnType.LONGITUDE, "longitude");
		STD_HEADER_NAMES.put(DataColumnType.LATITUDE, "latitude");
		STD_HEADER_NAMES.put(DataColumnType.SAMPLE_DEPTH, "sample_depth");
		STD_HEADER_NAMES.put(DataColumnType.SALINITY, "salinity");
		STD_HEADER_NAMES.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, "T_equ");
		STD_HEADER_NAMES.put(DataColumnType.SEA_SURFACE_TEMPERATURE, "SST");
		STD_HEADER_NAMES.put(DataColumnType.EQUILIBRATOR_PRESSURE, "P_equ");
		STD_HEADER_NAMES.put(DataColumnType.SEA_LEVEL_PRESSURE, "SLP");

		STD_HEADER_NAMES.put(DataColumnType.XCO2_WATER_TEQU, "xCO2_water_Tequ_dry");
		STD_HEADER_NAMES.put(DataColumnType.XCO2_WATER_SST, "xCO2_water_SST_dry");
		STD_HEADER_NAMES.put(DataColumnType.PCO2_WATER_TEQU, "pCO2_water_Tequ_wet");
		STD_HEADER_NAMES.put(DataColumnType.PCO2_WATER_SST, "pCO2_water_SST_wet");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_WATER_TEQU, "fCO2_water_Tequ_wet");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_WATER_SST, "fCO2_water_SST_wet");

		STD_HEADER_NAMES.put(DataColumnType.XCO2_ATM, "xCO2_atm");
		STD_HEADER_NAMES.put(DataColumnType.PCO2_ATM, "pCO2_atm");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_ATM, "fCO2_atm");
		STD_HEADER_NAMES.put(DataColumnType.DELTA_XCO2, "delta_xCO2");
		STD_HEADER_NAMES.put(DataColumnType.DELTA_PCO2, "delta_pCO2");
		STD_HEADER_NAMES.put(DataColumnType.DELTA_FCO2, "delta_fCO2");

		STD_HEADER_NAMES.put(DataColumnType.RELATIVE_HUMIDITY, "relative_humidity");
		STD_HEADER_NAMES.put(DataColumnType.SPECIFIC_HUMIDITY, "specific_humidity");
		STD_HEADER_NAMES.put(DataColumnType.SHIP_SPEED, "ship_speed");
		STD_HEADER_NAMES.put(DataColumnType.SHIP_DIRECTION, "ship_dir");
		STD_HEADER_NAMES.put(DataColumnType.WIND_SPEED_TRUE, "wind_speed_true");
		STD_HEADER_NAMES.put(DataColumnType.WIND_SPEED_RELATIVE, "wind_speed_rel");
		STD_HEADER_NAMES.put(DataColumnType.WIND_DIRECTION_TRUE, "wind_dir_true");
		STD_HEADER_NAMES.put(DataColumnType.WIND_DIRECTION_RELATIVE, "wind_dir_rel");

		STD_HEADER_NAMES.put(DataColumnType.GEOPOSITION_WOCE, "geoposition_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.SAMPLE_DEPTH_WOCE, "sample_depth_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.SALINITY_WOCE, "salinity_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.EQUILIBRATOR_TEMPERATURE_WOCE, "T_equ_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.SEA_SURFACE_TEMPERATURE_WOCE, "SST_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.EQUILIBRATOR_PRESSURE_WOCE, "P_equ_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.SEA_LEVEL_PRESSURE_WOCE, "SLP_WOCE");

		STD_HEADER_NAMES.put(DataColumnType.XCO2_WATER_TEQU_WOCE, "xCO2_water_Tequ_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.XCO2_WATER_SST_WOCE, "xCO2_water_SST_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.PCO2_WATER_TEQU_WOCE, "pCO2_water_Tequ_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.PCO2_WATER_SST_WOCE, "pCO2_water_SST_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_WATER_TEQU_WOCE, "fCO2_water_Tequ_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_WATER_SST_WOCE, "fCO2_water_SST_WOCE");

		STD_HEADER_NAMES.put(DataColumnType.XCO2_ATM_WOCE, "xCO2_atm_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.PCO2_ATM_WOCE, "pCO2_atm_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_ATM_WOCE, "fCO2_atm_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.DELTA_XCO2_WOCE, "delta_xCO2_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.DELTA_PCO2_WOCE, "delta_pCO2_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.DELTA_FCO2_WOCE, "delta_fCO2_WOCE");

		STD_HEADER_NAMES.put(DataColumnType.RELATIVE_HUMIDITY_WOCE, "rel_humidity_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.SPECIFIC_HUMIDITY_WOCE, "spc_humidity_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.SHIP_SPEED_WOCE, "ship_speed_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.SHIP_DIRECTION_WOCE, "ship_dir_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.WIND_SPEED_TRUE_WOCE, "wind_speed_true_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.WIND_SPEED_RELATIVE_WOCE, "wind_speed_rel_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.WIND_DIRECTION_TRUE_WOCE, "wind_dir_true_WOCE");
		STD_HEADER_NAMES.put(DataColumnType.WIND_DIRECTION_RELATIVE_WOCE, "wind_dir_rel_WOCE");
		
		STD_HEADER_NAMES.put(DataColumnType.COMMENT, "WOCE_comment");
		STD_HEADER_NAMES.put(DataColumnType.OTHER, "other");
	}

	/*
	 * known data units of the standard data columns
	 */
	public static final ArrayList<String> NO_UNITS = 
			new ArrayList<String>(Arrays.asList(""));
	public static final ArrayList<String> DAY_OF_YEAR_UNITS = 
			new ArrayList<String>(Arrays.asList("Jan1=1.0", "Jan1=0.0"));
	public static final ArrayList<String> LONGITUDE_UNITS = 
			new ArrayList<String>(Arrays.asList("deg.E"));
	public static final ArrayList<String> LATITUDE_UNITS = 
			new ArrayList<String>(Arrays.asList("deg.N"));
	public static final ArrayList<String> DEPTH_UNITS = 
			new ArrayList<String>(Arrays.asList("meters"));
	public static final ArrayList<String> SALINITY_UNITS = 
			new ArrayList<String>(Arrays.asList("PSU"));
	public static final ArrayList<String> TEMPERATURE_UNITS = 
			new ArrayList<String>(Arrays.asList("deg.C", "Kelvin", "deg.F"));
	public static final ArrayList<String> PRESSURE_UNITS = 
			new ArrayList<String>(Arrays.asList("hPa", "kPa"));
	public static final ArrayList<String> XCO2_UNITS = 
			new ArrayList<String>(Arrays.asList("umol/mol"));
	public static final ArrayList<String> PCO2_UNITS = 
			new ArrayList<String>(Arrays.asList("uatm"));
	public static final ArrayList<String> FCO2_UNITS = 
			new ArrayList<String>(Arrays.asList("uatm"));
	public static final ArrayList<String> DIRECTION_UNITS = 
			new ArrayList<String>(Arrays.asList("deg.clk.N"));
	public static final ArrayList<String> SHIP_SPEED_UNITS = 
			new ArrayList<String>(Arrays.asList("knots", "km/h", "mph"));
	public static final ArrayList<String> WIND_SPEED_UNITS = 
			new ArrayList<String>(Arrays.asList("m/s"));

	/**
	 * Available data units for the standard data columns that a user might provide.
	 * The first unit is the standard unit.
	 */
	public static final EnumMap<DataColumnType,ArrayList<String>> STD_DATA_UNITS = 
			new EnumMap<DataColumnType,ArrayList<String>>(DataColumnType.class);
	static {
		STD_DATA_UNITS.put(DataColumnType.UNKNOWN, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.EXPOCODE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.CRUISE_NAME, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SHIP_NAME, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.GROUP_NAME, NO_UNITS);

		STD_DATA_UNITS.put(DataColumnType.TIMESTAMP, new ArrayList<String>(Arrays.asList(
				"yyyy-mm-dd hh:mm:ss", "mm/dd/yyyy hh:mm:ss", "dd/mm/yyyy hh:mm:ss")));
		STD_DATA_UNITS.put(DataColumnType.DATE, new ArrayList<String>(Arrays.asList(
				"yyyy-mm-dd", "mm/dd/yyyy", "dd/mm/yyyy")));
		STD_DATA_UNITS.put(DataColumnType.YEAR, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.MONTH, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DAY, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.TIME, new ArrayList<String>(Arrays.asList("hh:mm:ss")));
		STD_DATA_UNITS.put(DataColumnType.HOUR, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.MINUTE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SECOND, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DAY_OF_YEAR, DAY_OF_YEAR_UNITS);

		STD_DATA_UNITS.put(DataColumnType.LONGITUDE, LONGITUDE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.LATITUDE, LATITUDE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SAMPLE_DEPTH, DEPTH_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SALINITY, SALINITY_UNITS);
		STD_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, TEMPERATURE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SEA_SURFACE_TEMPERATURE, TEMPERATURE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_PRESSURE, PRESSURE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SEA_LEVEL_PRESSURE, PRESSURE_UNITS);

		STD_DATA_UNITS.put(DataColumnType.XCO2_WATER_TEQU, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.XCO2_WATER_SST, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2_WATER_TEQU, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2_WATER_SST, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2_WATER_TEQU, FCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2_WATER_SST, FCO2_UNITS);

		STD_DATA_UNITS.put(DataColumnType.XCO2_ATM, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2_ATM, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2_ATM, FCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DELTA_XCO2, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DELTA_PCO2, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DELTA_FCO2, FCO2_UNITS);

		STD_DATA_UNITS.put(DataColumnType.RELATIVE_HUMIDITY, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SPECIFIC_HUMIDITY, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SHIP_SPEED, SHIP_SPEED_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SHIP_DIRECTION, DIRECTION_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_SPEED_TRUE, WIND_SPEED_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_SPEED_RELATIVE, WIND_SPEED_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_TRUE, DIRECTION_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_RELATIVE, DIRECTION_UNITS);

		STD_DATA_UNITS.put(DataColumnType.GEOPOSITION_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SAMPLE_DEPTH_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SALINITY_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_TEMPERATURE_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SEA_SURFACE_TEMPERATURE_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_PRESSURE_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SEA_LEVEL_PRESSURE_WOCE, NO_UNITS);

		STD_DATA_UNITS.put(DataColumnType.XCO2_WATER_TEQU_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.XCO2_WATER_SST_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2_WATER_TEQU_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2_WATER_SST_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2_WATER_TEQU_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2_WATER_SST_WOCE, NO_UNITS);

		STD_DATA_UNITS.put(DataColumnType.XCO2_ATM_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2_ATM_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2_ATM_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DELTA_XCO2_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DELTA_PCO2_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DELTA_FCO2_WOCE, NO_UNITS);

		STD_DATA_UNITS.put(DataColumnType.RELATIVE_HUMIDITY_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SPECIFIC_HUMIDITY_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SHIP_SPEED_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SHIP_DIRECTION_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_SPEED_TRUE_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_SPEED_RELATIVE_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_TRUE_WOCE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_RELATIVE_WOCE, NO_UNITS);

		STD_DATA_UNITS.put(DataColumnType.COMMENT, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.OTHER, NO_UNITS);
	}

	/**
	 * "Cleans" a username for use by substituting characters that are  
	 * problematic (such as space characters).  
	 * Also converts all alphabetic characters to lowercase.
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
	 * JSON-encodes an ArrayList of Integers suitable for decoding 
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
	 * Decodes a (JSON-like) encoded array of numbers into an ArrayList of 
	 * Integers.  Will decode an encoded string produced by 
	 * {@link #encodeIntegerArrayList(ArrayList)}  
	 * 
	 * @param arrayStr
	 * 		JSON-encoded array of integer values to use
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
		Integer[] intArray = new Integer[pieces.length];
		for (int k = 0; k < pieces.length; k++)
			intArray[k] = Integer.parseInt(pieces[k].trim());
		return new ArrayList<Integer>(Arrays.asList(intArray));
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
	 * Decodes a (somewhat-JSON-like) encoded string array, like that 
	 * produced by {@link #encodeStringArrayList(ArrayList)}, into an 
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
		// Locate the double quote at the start of the first string 
		// and at the end of the last string
		int firstIndex = arrayStr.indexOf("\"");
		int lastIndex = arrayStr.lastIndexOf("\"");
		// Check for values not in double quotes within the brackets
		if ( (firstIndex < 1) || (lastIndex == firstIndex) ||
			 ( (firstIndex > 1) && 
				! arrayStr.substring(1, firstIndex).trim().isEmpty() ) ||
			 ( (lastIndex > 1) && (lastIndex < arrayStr.length() - 2) && 
				! arrayStr.substring(lastIndex+1, 
								arrayStr.length()-1).trim().isEmpty() ) ) {
			// Check for an empty array
			if ( (firstIndex < 1) && 
				 arrayStr.substring(1, arrayStr.length()-1).trim().isEmpty() )
				return new ArrayList<String>(0);
			throw new IllegalArgumentException("Strings in encoded " +
					"string array are not enclosed in double quotes");
		}
		// Split up the substring between the first and last double quote
		String[] pieces = arrayStr.substring(firstIndex+1, lastIndex)
								  .split("\"\\s*,\\s*\"", -1);
		// Return an ArrayList<String> generated from the array of Strings
		return new ArrayList<String>(Arrays.asList(pieces));
	}

	/**
	 * Encodes an ArrayList of HashSets of Integers suitable for decoding 
	 * with {@link #decodeSetsArrayList(String)}
	 * 
	 * @param setsList
	 * 		list of sets of integer values to encode
	 * @return
	 * 		the encoded list of sets of integer values
	 */
	public static String encodeSetsArrayList(ArrayList<HashSet<Integer>> setsList) 
											throws IllegalArgumentException {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		boolean firstValue = true;
		for ( HashSet<Integer> setVal : setsList ) {
			if ( firstValue )
				firstValue = false;
			else
				sb.append(", ");
			// Go to the trouble of sorting the list before creating the string
			// in order to simplify human reading and detecting real differences
			sb.append(encodeIntegerArrayList(
					new ArrayList<Integer>(new TreeSet<Integer>(setVal))));
		}
		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * Decodes a (somewhat-JSON-like) encoded array of sets of integers, 
	 * like that produced by {@link #encodeSetsArrayList(ArrayList)}, 
	 * into an ArrayList of HashSets of Integers.  Each set must be 
	 * comma-separated integer values enclosed in brackets (like that 
	 * produced {@link #encodeIntegerArrayList(ArrayList)}, and each set 
	 * must be by separated by a comma.  Whitespace around brackets and 
	 * commas is allowed.
	 * 
	 * @param arrayStr
	 * 		the encoded sets of integers array
	 * @return
	 * 		the decoded ArrayList of HashSets of Integers; never null, 
	 * 		but may be empty (if the encoded array contains no sets)
	 * @throws IllegalArgumentException
	 * 		if arrayStr does not start with '[', does not end with ']', 
	 * 		or contains sets not enclosed within '[' and ']'.
	 */
	public static ArrayList<HashSet<Integer>> decodeSetsArrayList(String arrayStr) 
											throws IllegalArgumentException {
		if ( ! ( arrayStr.startsWith("[") && arrayStr.endsWith("]") ) )
			throw new IllegalArgumentException(
					"Encoded string array not enclosed in brackets");
		// Locate the opening bracket of the first set
		int firstIndex = arrayStr.indexOf("[", 1);
		// Locate the closing bracket of the last set
		int lastIndex = arrayStr.lastIndexOf("]", arrayStr.length() - 2);
		if ( (firstIndex < 0) || (lastIndex < 0) ) {
			if ( (firstIndex < 0) && (lastIndex < 0) &&
				 arrayStr.substring(1, arrayStr.length() - 1).trim().isEmpty() ) {
				// no sets; return an empty list
				return new ArrayList<HashSet<Integer>>(0);
			}
			// Not empty, but 
			throw new IllegalArgumentException(
					"Sets in encoded sets array not enclosed in brackets");
		}
		// Split the string into each of the sets
		String[] pieces = arrayStr.substring(firstIndex+1, lastIndex)
								  .split("\\]\\s*,\\s*\\[", -1);
		// Create the list to return
		ArrayList<HashSet<Integer>> setsList = 
				new ArrayList<HashSet<Integer>>(pieces.length);
		// Convert each of the set strings and add to the list
		for ( String setStr : pieces ) {
			setsList.add(new HashSet<Integer>(
					decodeIntegerArrayList("[" + setStr + "]")));
		}
		return setsList;
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

		// Check if values are close
		double absDiff = Math.abs(first - second);
		double absAver = Math.abs((first + second) * 0.5);
		return ( absDiff < absAver * rtol + atol );
	}

}