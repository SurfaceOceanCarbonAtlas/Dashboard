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

	// Recognized data formats
	public static final String CRUISE_FORMAT_COMMA = "COMMA-SEPARATED VALUES";
	public static final String CRUISE_FORMAT_TAB = "TAB-SEPARATED VALUES";

	// Cruise upload result strings
	public static final String FILE_PREVIEW_HEADER_TAG = "FILE PREVIEW HEADER TAG";
	public static final String FILE_INVALID_HEADER_TAG = "FILE INVALID HEADER TAG";
	public static final String NO_EXPOCODE_HEADER_TAG = "NO EXPOCODE HEADER TAG";
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
	public static final String ARCHIVE_STATUS_SENT_CDIAC = "Sent to CDIAC";
	public static final String ARCHIVE_STATUS_OWNER_ARCHIVE = "Owner to archive";
	public static final String ARCHIVE_STATUS_ARCHIVED = "Archived";

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
		STD_HEADER_NAMES.put(DataColumnType.SECOND_OF_DAY, "sec_of_day");

		STD_HEADER_NAMES.put(DataColumnType.LONGITUDE, "longitude");
		STD_HEADER_NAMES.put(DataColumnType.LATITUDE, "latitude");
		STD_HEADER_NAMES.put(DataColumnType.SAMPLE_DEPTH, "sample_depth");
		STD_HEADER_NAMES.put(DataColumnType.SALINITY, "salinity");
		STD_HEADER_NAMES.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, "T_equ");
		STD_HEADER_NAMES.put(DataColumnType.SEA_SURFACE_TEMPERATURE, "SST");
		STD_HEADER_NAMES.put(DataColumnType.ATMOSPHERIC_TEMPERATURE, "Temperature_atm");
		STD_HEADER_NAMES.put(DataColumnType.EQUILIBRATOR_PRESSURE, "P_equ");
		STD_HEADER_NAMES.put(DataColumnType.SEA_LEVEL_PRESSURE, "Pressure_atm");

		STD_HEADER_NAMES.put(DataColumnType.XCO2_WATER_TEQU_DRY, "xCO2_water_Tequ_dry");
		STD_HEADER_NAMES.put(DataColumnType.XCO2_WATER_SST_DRY, "xCO2_water_SST_dry");
		STD_HEADER_NAMES.put(DataColumnType.XCO2_WATER_TEQU_WET, "xCO2_water_Tequ_wet");
		STD_HEADER_NAMES.put(DataColumnType.XCO2_WATER_SST_WET, "xCO2_water_SST_wet");
		STD_HEADER_NAMES.put(DataColumnType.PCO2_WATER_TEQU_WET, "pCO2_water_Tequ_wet");
		STD_HEADER_NAMES.put(DataColumnType.PCO2_WATER_SST_WET, "pCO2_water_SST_wet");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_WATER_TEQU_WET, "fCO2_water_Tequ_wet");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_WATER_SST_WET, "fCO2_water_SST_wet");

		STD_HEADER_NAMES.put(DataColumnType.XCO2_ATM_DRY_ACTUAL, "xCO2_atm_dry_actual");
		STD_HEADER_NAMES.put(DataColumnType.XCO2_ATM_DRY_INTERP, "xCO2_atm_dry_interp");
		STD_HEADER_NAMES.put(DataColumnType.PCO2_ATM_WET_ACTUAL, "pCO2_atm_wet_actual");
		STD_HEADER_NAMES.put(DataColumnType.PCO2_ATM_WET_INTERP, "pCO2_atm_wet_interp");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_ATM_WET_ACTUAL, "fCO2_atm_wet_actual");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_ATM_WET_INTERP, "fCO2_atm_wet_interp");

		STD_HEADER_NAMES.put(DataColumnType.DELTA_XCO2, "delta_xCO2");
		STD_HEADER_NAMES.put(DataColumnType.DELTA_PCO2, "delta_pCO2");
		STD_HEADER_NAMES.put(DataColumnType.DELTA_FCO2, "delta_fCO2");

		STD_HEADER_NAMES.put(DataColumnType.XH2O_EQU, "xH2O_equ");
		STD_HEADER_NAMES.put(DataColumnType.RELATIVE_HUMIDITY, "relative_humidity");
		STD_HEADER_NAMES.put(DataColumnType.SPECIFIC_HUMIDITY, "specific_humidity");
		STD_HEADER_NAMES.put(DataColumnType.SHIP_SPEED, "ship_speed");
		STD_HEADER_NAMES.put(DataColumnType.SHIP_DIRECTION, "ship_dir");
		STD_HEADER_NAMES.put(DataColumnType.WIND_SPEED_TRUE, "wind_speed_true");
		STD_HEADER_NAMES.put(DataColumnType.WIND_SPEED_RELATIVE, "wind_speed_rel");
		STD_HEADER_NAMES.put(DataColumnType.WIND_DIRECTION_TRUE, "wind_dir_true");
		STD_HEADER_NAMES.put(DataColumnType.WIND_DIRECTION_RELATIVE, "wind_dir_rel");

		STD_HEADER_NAMES.put(DataColumnType.WOCE_CO2_WATER, "WOCE_CO2_water");
		STD_HEADER_NAMES.put(DataColumnType.WOCE_CO2_ATM, "WOCE_CO2_atm");
		STD_HEADER_NAMES.put(DataColumnType.COMMENT_WOCE_CO2_WATER, "comment_WOCE_CO2_water");
		STD_HEADER_NAMES.put(DataColumnType.COMMENT_WOCE_CO2_ATM, "comment_WOCE_CO2_atm");

		STD_HEADER_NAMES.put(DataColumnType.OTHER, "other");
	}

	/*
	 * known data units of the standard data columns
	 */
	public static final ArrayList<String> NO_UNITS = 
			new ArrayList<String>(Arrays.asList(""));
	public static final ArrayList<String> TIMESTAMP_UNITS = new ArrayList<String>(Arrays.asList(
			"yyyy-mm-dd hh:mm:ss", "mm-dd-yyyy hh:mm:ss", "dd-mm-yyyy hh:mm:ss",
			"mm-dd-yy hh:mm:ss", "dd-mm-yy hh:mm:ss"));
	public static final ArrayList<String> DATE_UNITS = new ArrayList<String>(Arrays.asList(
			"yyyy-mm-dd", "mm-dd-yyyy", "dd-mm-yyyy", "mm-dd-yy", "dd-mm-yy"));
	public static final ArrayList<String> TIME_UNITS = 
			new ArrayList<String>(Arrays.asList("hh:mm:ss"));
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
			new ArrayList<String>(Arrays.asList("deg.C"));
	public static final ArrayList<String> PRESSURE_UNITS = 
			new ArrayList<String>(Arrays.asList("hPa", "kPa", "mmHg"));
	public static final ArrayList<String> XCO2_UNITS = 
			new ArrayList<String>(Arrays.asList("umol/mol"));
	public static final ArrayList<String> PCO2_UNITS = 
			new ArrayList<String>(Arrays.asList("uatm"));
	public static final ArrayList<String> FCO2_UNITS = 
			new ArrayList<String>(Arrays.asList("uatm"));
	public static final ArrayList<String> DIRECTION_UNITS = 
			new ArrayList<String>(Arrays.asList("deg.clk.N"));
	public static final ArrayList<String> SHIP_SPEED_UNITS = 
			new ArrayList<String>(Arrays.asList("knots", "km/h", "m/s", "mph"));
	public static final ArrayList<String> WIND_SPEED_UNITS = 
			new ArrayList<String>(Arrays.asList("m/s"));
	public static final ArrayList<String> XH2O_UNITS = 
			new ArrayList<String>(Arrays.asList("mmol/mol", "umol/mol"));

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

		STD_DATA_UNITS.put(DataColumnType.TIMESTAMP, TIMESTAMP_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DATE, DATE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.YEAR, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.MONTH, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DAY, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.TIME, TIME_UNITS);
		STD_DATA_UNITS.put(DataColumnType.HOUR, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.MINUTE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SECOND, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DAY_OF_YEAR, DAY_OF_YEAR_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SECOND_OF_DAY, NO_UNITS);

		STD_DATA_UNITS.put(DataColumnType.LONGITUDE, LONGITUDE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.LATITUDE, LATITUDE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SAMPLE_DEPTH, DEPTH_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SALINITY, SALINITY_UNITS);
		STD_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, TEMPERATURE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SEA_SURFACE_TEMPERATURE, TEMPERATURE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.ATMOSPHERIC_TEMPERATURE, TEMPERATURE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_PRESSURE, PRESSURE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SEA_LEVEL_PRESSURE, PRESSURE_UNITS);

		STD_DATA_UNITS.put(DataColumnType.XCO2_WATER_TEQU_DRY, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.XCO2_WATER_SST_DRY, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.XCO2_WATER_TEQU_WET, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.XCO2_WATER_SST_WET, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2_WATER_TEQU_WET, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2_WATER_SST_WET, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2_WATER_TEQU_WET, FCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2_WATER_SST_WET, FCO2_UNITS);

		STD_DATA_UNITS.put(DataColumnType.XCO2_ATM_DRY_ACTUAL, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.XCO2_ATM_DRY_INTERP, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2_ATM_WET_ACTUAL, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2_ATM_WET_INTERP, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2_ATM_WET_ACTUAL, FCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2_ATM_WET_INTERP, FCO2_UNITS);

		STD_DATA_UNITS.put(DataColumnType.DELTA_XCO2, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DELTA_PCO2, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DELTA_FCO2, FCO2_UNITS);

		STD_DATA_UNITS.put(DataColumnType.XH2O_EQU, XH2O_UNITS);
		STD_DATA_UNITS.put(DataColumnType.RELATIVE_HUMIDITY, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SPECIFIC_HUMIDITY, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SHIP_SPEED, SHIP_SPEED_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SHIP_DIRECTION, DIRECTION_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_SPEED_TRUE, WIND_SPEED_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_SPEED_RELATIVE, WIND_SPEED_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_TRUE, DIRECTION_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_RELATIVE, DIRECTION_UNITS);

		STD_DATA_UNITS.put(DataColumnType.WOCE_CO2_WATER, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WOCE_CO2_ATM, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.COMMENT_WOCE_CO2_WATER, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.COMMENT_WOCE_CO2_ATM, NO_UNITS);

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
		if ( DashboardUtils.closeTo(first, second, rtol, atol) )
			return true;
		if ( DashboardUtils.closeTo(first + 360.0, second, rtol, atol) )
			return true;
		if ( DashboardUtils.closeTo(first, second + 360.0, rtol, atol) )
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

		// Check if values are close
		double absDiff = Math.abs(first - second);
		double absAver = Math.abs((first + second) * 0.5);
		return ( absDiff < absAver * rtol + atol );
	}

}