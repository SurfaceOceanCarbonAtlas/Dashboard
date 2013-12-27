/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import com.googlecode.gwt.crypto.client.TripleDesCipher;

/**
 * Static dashboard utility functions and constants
 * for use on both the client and server side.
 *  
 * @author Karl Smith
 */
public class DashboardUtils {

	public static final String REQUEST_CRUISE_LIST_ACTION = "GET CRUISE LIST";
	public static final String REQUEST_CRUISE_DELETE_ACTION = "DELETE CRUISE";
	public static final String REQUEST_CRUISE_ADD_ACTION = "ADD CRUISE TO LIST";
	public static final String REQUEST_CRUISE_REMOVE_ACTION = "REMOVE CRUISE FROM LIST";

	public static final String REQUEST_PREVIEW_TAG = "REQUEST PREVIEW TAG";
	public static final String REQUEST_NEW_CRUISE_TAG = "REQUEST NEW CRUISE TAG";
	public static final String REQUEST_OVERWRITE_CRUISE_TAG = "REQUEST OVERWRITE CRUISE TAG";

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

	public static final String CHECK_STATUS_NOT_CHECKED = "";
	public static final String CHECK_STATUS_ACCEPTABLE = "Acceptable";
	public static final String CHECK_STATUS_QUESTIONABLE = "Possibly has errors";
	public static final String CHECK_STATUS_ERRORS = "Has serious errors";
	public static final String CHECK_STATUS_UNACCEPTABLE = "Unacceptable";

	public static final String QC_STATUS_NOT_SUBMITTED = "";
	public static final String QC_STATUS_SUBMITTED = "Submitted";
	public static final String QC_STATUS_ACCEPTED_A = "QC Flag A";
	public static final String QC_STATUS_ACCEPTED_B = "QC Flag B";
	public static final String QC_STATUS_ACCEPTED_C = "QC Flag C";
	public static final String QC_STATUS_ACCEPTED_D = "QC Flag D";
	public static final String QC_STATUS_UNACCEPTABLE = "Unacceptable";
	public static final String QC_STATUS_SUSPENDED = "Suspended";
	public static final String QC_STATUS_EXCLUDED = "Excluded";

	public static final String ARCHIVE_STATUS_NOT_SUBMITTED = "";
	public static final String ARCHIVE_STATUS_WITH_SOCAT = "With next SOCAT";
	public static final String ARCHIVE_STATUS_SUBMIT_CDIAC = "Submitted to CDIAC";
	public static final String ARCHIVE_STATUS_OWNER_ARCHIVE = "Owner to archive";
	public static final String ARCHIVE_STATUS_ARCHIVED_PREFIX = "DOI ";

	/**
	 * standard header names of the standard data columns
	 */
	public static final EnumMap<DataColumnType,String> STD_HEADER_NAMES = 
			new EnumMap<DataColumnType,String>(DataColumnType.class);
	static {
		STD_HEADER_NAMES.put(DataColumnType.IGNORE, "(ignore)");
		STD_HEADER_NAMES.put(DataColumnType.UNKNOWN, "(unknown)");
		STD_HEADER_NAMES.put(DataColumnType.TIMESTAMP, "timestamp");
		STD_HEADER_NAMES.put(DataColumnType.DATE, "date");
		STD_HEADER_NAMES.put(DataColumnType.YEAR, "yr");
		STD_HEADER_NAMES.put(DataColumnType.MONTH, "mon");
		STD_HEADER_NAMES.put(DataColumnType.DAY, "day");
		STD_HEADER_NAMES.put(DataColumnType.TIME, "time");
		STD_HEADER_NAMES.put(DataColumnType.HOUR, "hh");
		STD_HEADER_NAMES.put(DataColumnType.MINUTE, "mm");
		STD_HEADER_NAMES.put(DataColumnType.SECOND, "ss");
		STD_HEADER_NAMES.put(DataColumnType.LONGITUDE, "longitude");
		STD_HEADER_NAMES.put(DataColumnType.LATITUDE, "latitude");
		STD_HEADER_NAMES.put(DataColumnType.SAMPLE_DEPTH, "sample_depth");
		STD_HEADER_NAMES.put(DataColumnType.SAMPLE_SALINITY, "sal");
		STD_HEADER_NAMES.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, "Tequ");
		STD_HEADER_NAMES.put(DataColumnType.SEA_SURFACE_TEMPERATURE, "SST");
		STD_HEADER_NAMES.put(DataColumnType.EQUILIBRATOR_PRESSURE, "Pequ");
		STD_HEADER_NAMES.put(DataColumnType.SEA_LEVEL_PRESSURE, "PPPP");
		STD_HEADER_NAMES.put(DataColumnType.USER_FCO2_REC, "fCO2_rec_user");
		STD_HEADER_NAMES.put(DataColumnType.USER_FCO2_SRC, "fCO2_src_user");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_AIR, "fCO2_air");
		STD_HEADER_NAMES.put(DataColumnType.WIND_SPEED, "wind_speed");
		STD_HEADER_NAMES.put(DataColumnType.WIND_DIRECTION, "wind_direction");
		STD_HEADER_NAMES.put(DataColumnType.XCO2WATER_EQU, "xCO2water_equ_dry");
		STD_HEADER_NAMES.put(DataColumnType.XCO2WATER_SST, "xCO2water_SST_dry");
		STD_HEADER_NAMES.put(DataColumnType.PCO2WATER_EQU, "pCO2water_equ_wet");
		STD_HEADER_NAMES.put(DataColumnType.PCO2WATER_SST, "pCO2water_SST_wet");
		STD_HEADER_NAMES.put(DataColumnType.FCO2WATER_EQU, "fCO2water_equ_wet");
		STD_HEADER_NAMES.put(DataColumnType.FCO2WATER_SST, "fCO2water_SST_wet");
		STD_HEADER_NAMES.put(DataColumnType.XCO2AIR_DRY, "xCO2air_dry");
		STD_HEADER_NAMES.put(DataColumnType.XCO2AIR_EQU, "xCO2air_equ");
		STD_HEADER_NAMES.put(DataColumnType.PCO2AIR_WET, "pCO2air_wet");
		STD_HEADER_NAMES.put(DataColumnType.FCO2AIR_WET, "fCO2air_wet");
		STD_HEADER_NAMES.put(DataColumnType.SUPPLEMENTAL, "(supplemental)");
	}

	/**
	 * column names used by the sanity checker
	 */
	public static final EnumMap<DataColumnType,String> CHECKER_NAMES = 
			new EnumMap<DataColumnType,String>(DataColumnType.class);
	static {
		CHECKER_NAMES.put(DataColumnType.IGNORE, "");
		CHECKER_NAMES.put(DataColumnType.UNKNOWN, "");
		CHECKER_NAMES.put(DataColumnType.TIMESTAMP, "date_time");
		CHECKER_NAMES.put(DataColumnType.DATE, "date");
		CHECKER_NAMES.put(DataColumnType.YEAR, "yr");
		CHECKER_NAMES.put(DataColumnType.MONTH, "mon");
		CHECKER_NAMES.put(DataColumnType.DAY, "day");
		CHECKER_NAMES.put(DataColumnType.TIME, "time");
		CHECKER_NAMES.put(DataColumnType.HOUR, "hh");
		CHECKER_NAMES.put(DataColumnType.MINUTE, "mm");
		CHECKER_NAMES.put(DataColumnType.SECOND, "ss");
		CHECKER_NAMES.put(DataColumnType.LONGITUDE, "longitude");
		CHECKER_NAMES.put(DataColumnType.LATITUDE, "latitude");
		CHECKER_NAMES.put(DataColumnType.SAMPLE_DEPTH, "depth");
		CHECKER_NAMES.put(DataColumnType.SAMPLE_SALINITY, "sal");
		CHECKER_NAMES.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, "temperature_equi");
		CHECKER_NAMES.put(DataColumnType.SEA_SURFACE_TEMPERATURE, "temp");
		CHECKER_NAMES.put(DataColumnType.EQUILIBRATOR_PRESSURE, "pressure_equi");
		CHECKER_NAMES.put(DataColumnType.SEA_LEVEL_PRESSURE, "pressure_atm");
		CHECKER_NAMES.put(DataColumnType.USER_FCO2_REC, "fCO2_rec");
		CHECKER_NAMES.put(DataColumnType.USER_FCO2_SRC, "fCO2_src");
		CHECKER_NAMES.put(DataColumnType.FCO2_AIR, "fCO2_air");
		CHECKER_NAMES.put(DataColumnType.WIND_SPEED, "wind_speed");
		CHECKER_NAMES.put(DataColumnType.WIND_DIRECTION, "wind_direction");
		CHECKER_NAMES.put(DataColumnType.XCO2WATER_EQU, "xCO2water_eq_dry");
		CHECKER_NAMES.put(DataColumnType.XCO2WATER_SST, "xCO2water_sst_dry");
		CHECKER_NAMES.put(DataColumnType.PCO2WATER_EQU, "pCO2water_eq_wet");
		CHECKER_NAMES.put(DataColumnType.PCO2WATER_SST, "pCO2water_sst_wet");
		CHECKER_NAMES.put(DataColumnType.FCO2WATER_EQU, "fCO2water_eq_wet");
		CHECKER_NAMES.put(DataColumnType.FCO2WATER_SST, "fCO2water_sst_wet");
		CHECKER_NAMES.put(DataColumnType.XCO2AIR_DRY, "xCO2air_dry");
		CHECKER_NAMES.put(DataColumnType.XCO2AIR_EQU, "xCO2air_eq");
		CHECKER_NAMES.put(DataColumnType.PCO2AIR_WET, "pCO2air_wet");
		CHECKER_NAMES.put(DataColumnType.FCO2AIR_WET, "fCO2air_wet");
		CHECKER_NAMES.put(DataColumnType.SUPPLEMENTAL, "");
	}
	/**
	 * standard data descriptions of the standard data columns
	 */
	public static final EnumMap<DataColumnType,String> STD_DESCRIPTIONS = 
			new EnumMap<DataColumnType,String>(DataColumnType.class);
	static {
		STD_DESCRIPTIONS.put(DataColumnType.IGNORE, "data to be ignored"); 
		STD_DESCRIPTIONS.put(DataColumnType.UNKNOWN, "unknown data to be identified");
		STD_DESCRIPTIONS.put(DataColumnType.TIMESTAMP, "date and time of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.DATE, "date of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.YEAR, "year of the date of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.MONTH, "month of the date of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.DAY, "day of the date of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.TIME, "time of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.HOUR, "hour of the time of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.MINUTE, "minute of the time of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.SECOND, "second of the time of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.LONGITUDE, "measurement longitude");
		STD_DESCRIPTIONS.put(DataColumnType.LATITUDE, "measurement latitude");
		STD_DESCRIPTIONS.put(DataColumnType.SAMPLE_DEPTH, "water sampling depth");
		STD_DESCRIPTIONS.put(DataColumnType.SAMPLE_SALINITY, "measured sea surface salinity");
		STD_DESCRIPTIONS.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, "equilibrator chamber temperature");
		STD_DESCRIPTIONS.put(DataColumnType.SEA_SURFACE_TEMPERATURE, "measured sea surface temperature");
		STD_DESCRIPTIONS.put(DataColumnType.EQUILIBRATOR_PRESSURE, "equilibrator chamber pressure");
		STD_DESCRIPTIONS.put(DataColumnType.SEA_LEVEL_PRESSURE, "measured atmospheric pressure");
		STD_DESCRIPTIONS.put(DataColumnType.USER_FCO2_REC, "user-provided recomputed fCO2");
		STD_DESCRIPTIONS.put(DataColumnType.USER_FCO2_SRC, "method of computing user-provided fCO2");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_AIR, "measured air fCO2");
		STD_DESCRIPTIONS.put(DataColumnType.WIND_SPEED, "wind speed");
		STD_DESCRIPTIONS.put(DataColumnType.WIND_DIRECTION, "wind direction");
		STD_DESCRIPTIONS.put(DataColumnType.XCO2WATER_EQU, "measured xCO2 (water) using equilibrator temperature (dry air)");
		STD_DESCRIPTIONS.put(DataColumnType.XCO2WATER_SST, "measured xCO2 (water) using sea surface temperature (dry air)");
		STD_DESCRIPTIONS.put(DataColumnType.PCO2WATER_EQU, "measured pCO2 (water) using equilibrator temperature (wet air)");
		STD_DESCRIPTIONS.put(DataColumnType.PCO2WATER_SST, "measured pCO2 (water) using sea surface temperature (wet air)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2WATER_EQU, "measured fCO2 (water) using equilibrator temperature (wet air)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2WATER_SST, "measured fCO2 (water) using sea surface temperature (wet air)");
		STD_DESCRIPTIONS.put(DataColumnType.XCO2AIR_DRY, " measured xCO2 (dry air)");
		STD_DESCRIPTIONS.put(DataColumnType.XCO2AIR_EQU, "measured xCO2 (dry air) using equilibrator temperature");
		STD_DESCRIPTIONS.put(DataColumnType.PCO2AIR_WET, "measured pCO2 (wet air)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2AIR_WET, "measured fCO2 (wet air)");
		STD_DESCRIPTIONS.put(DataColumnType.SUPPLEMENTAL, "supplemental data to be kept");
	}

	/**
	 * known data units of the standard data columns
	 */
	public static final EnumMap<DataColumnType,ArrayList<String>> STD_DATA_UNITS = 
			new EnumMap<DataColumnType,ArrayList<String>>(DataColumnType.class);
	static {
		STD_DATA_UNITS.put(DataColumnType.IGNORE, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(DataColumnType.UNKNOWN, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(DataColumnType.TIMESTAMP, new ArrayList<String>(Arrays.asList("YYYY-MM-DD HH:MM:SS Z")));
		STD_DATA_UNITS.put(DataColumnType.DATE, new ArrayList<String>(Arrays.asList("YYYY-MM-DD", "MM/DD/YYYY", "DD/MM/YYYY")));
		STD_DATA_UNITS.put(DataColumnType.YEAR, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(DataColumnType.MONTH, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(DataColumnType.DAY, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(DataColumnType.TIME, new ArrayList<String>(Arrays.asList("HH:MM:SS")));
		STD_DATA_UNITS.put(DataColumnType.HOUR, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(DataColumnType.MINUTE, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(DataColumnType.SECOND, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(DataColumnType.LONGITUDE, new ArrayList<String>(Arrays.asList("dec. deg. E")));
		STD_DATA_UNITS.put(DataColumnType.LATITUDE, new ArrayList<String>(Arrays.asList("dec. deg. N")));
		STD_DATA_UNITS.put(DataColumnType.SAMPLE_DEPTH, new ArrayList<String>(Arrays.asList("meters")));
		STD_DATA_UNITS.put(DataColumnType.SAMPLE_SALINITY, new ArrayList<String>(Arrays.asList("PSU")));
		STD_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, new ArrayList<String>(Arrays.asList("deg. C")));
		STD_DATA_UNITS.put(DataColumnType.SEA_SURFACE_TEMPERATURE, new ArrayList<String>(Arrays.asList("deg. C")));
		STD_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_PRESSURE, new ArrayList<String>(Arrays.asList("hPa", "mbar")));
		STD_DATA_UNITS.put(DataColumnType.SEA_LEVEL_PRESSURE, new ArrayList<String>(Arrays.asList("hPa", "mbar")));
		STD_DATA_UNITS.put(DataColumnType.USER_FCO2_REC, new ArrayList<String>(Arrays.asList("uatm")));
		STD_DATA_UNITS.put(DataColumnType.USER_FCO2_SRC, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(DataColumnType.FCO2_AIR, new ArrayList<String>(Arrays.asList("uatm")));
		STD_DATA_UNITS.put(DataColumnType.WIND_SPEED, new ArrayList<String>(Arrays.asList("kn", "km/h", "m/s", "mph")));
		STD_DATA_UNITS.put(DataColumnType.WIND_DIRECTION, new ArrayList<String>(Arrays.asList("dec. deg.")));
		STD_DATA_UNITS.put(DataColumnType.XCO2WATER_EQU, new ArrayList<String>(Arrays.asList("umol/mol")));
		STD_DATA_UNITS.put(DataColumnType.XCO2WATER_SST, new ArrayList<String>(Arrays.asList("umol/mol")));
		STD_DATA_UNITS.put(DataColumnType.PCO2WATER_EQU, new ArrayList<String>(Arrays.asList("uatm")));
		STD_DATA_UNITS.put(DataColumnType.PCO2WATER_SST, new ArrayList<String>(Arrays.asList("uatm")));
		STD_DATA_UNITS.put(DataColumnType.FCO2WATER_EQU, new ArrayList<String>(Arrays.asList("uatm")));
		STD_DATA_UNITS.put(DataColumnType.FCO2WATER_SST, new ArrayList<String>(Arrays.asList("uatm")));
		STD_DATA_UNITS.put(DataColumnType.XCO2AIR_DRY, new ArrayList<String>(Arrays.asList("umol/mol")));
		STD_DATA_UNITS.put(DataColumnType.XCO2AIR_EQU, new ArrayList<String>(Arrays.asList("umol/mol")));
		STD_DATA_UNITS.put(DataColumnType.PCO2AIR_WET, new ArrayList<String>(Arrays.asList("uatm")));
		STD_DATA_UNITS.put(DataColumnType.FCO2AIR_WET, new ArrayList<String>(Arrays.asList("uatm")));
		STD_DATA_UNITS.put(DataColumnType.SUPPLEMENTAL, new ArrayList<String>(Arrays.asList("")));
	}
	/**
	 * data units of the standard data columns used by the sanity checker
	 */
	public static final EnumMap<DataColumnType,ArrayList<String>> CHECKER_DATA_UNITS = 
			new EnumMap<DataColumnType,ArrayList<String>>(DataColumnType.class);
	static {
		CHECKER_DATA_UNITS.put(DataColumnType.IGNORE, new ArrayList<String>(Arrays.asList("")));
		CHECKER_DATA_UNITS.put(DataColumnType.UNKNOWN, new ArrayList<String>(Arrays.asList("")));
		CHECKER_DATA_UNITS.put(DataColumnType.TIMESTAMP, new ArrayList<String>(Arrays.asList("")));
		CHECKER_DATA_UNITS.put(DataColumnType.DATE, new ArrayList<String>(Arrays.asList("YYYY-MM-DD", "MM/DD/YYYY", "DD/MM/YYYY")));
		CHECKER_DATA_UNITS.put(DataColumnType.YEAR, new ArrayList<String>(Arrays.asList("")));
		CHECKER_DATA_UNITS.put(DataColumnType.MONTH, new ArrayList<String>(Arrays.asList("")));
		CHECKER_DATA_UNITS.put(DataColumnType.DAY, new ArrayList<String>(Arrays.asList("")));
		CHECKER_DATA_UNITS.put(DataColumnType.TIME, new ArrayList<String>(Arrays.asList("HH:MM:SS")));
		CHECKER_DATA_UNITS.put(DataColumnType.HOUR, new ArrayList<String>(Arrays.asList("")));
		CHECKER_DATA_UNITS.put(DataColumnType.MINUTE, new ArrayList<String>(Arrays.asList("")));
		CHECKER_DATA_UNITS.put(DataColumnType.SECOND, new ArrayList<String>(Arrays.asList("")));
		CHECKER_DATA_UNITS.put(DataColumnType.LONGITUDE, new ArrayList<String>(Arrays.asList("decimal_degrees")));
		CHECKER_DATA_UNITS.put(DataColumnType.LATITUDE, new ArrayList<String>(Arrays.asList("decimal_degress")));
		CHECKER_DATA_UNITS.put(DataColumnType.SAMPLE_DEPTH, new ArrayList<String>(Arrays.asList("meters")));
		CHECKER_DATA_UNITS.put(DataColumnType.SAMPLE_SALINITY, new ArrayList<String>(Arrays.asList("psu")));
		CHECKER_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, new ArrayList<String>(Arrays.asList("degC")));
		CHECKER_DATA_UNITS.put(DataColumnType.SEA_SURFACE_TEMPERATURE, new ArrayList<String>(Arrays.asList("degC")));
		CHECKER_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_PRESSURE, new ArrayList<String>(Arrays.asList("hPa", "mbar")));
		CHECKER_DATA_UNITS.put(DataColumnType.SEA_LEVEL_PRESSURE, new ArrayList<String>(Arrays.asList("hPa", "mbar")));
		CHECKER_DATA_UNITS.put(DataColumnType.USER_FCO2_REC, new ArrayList<String>(Arrays.asList("uatm")));
		CHECKER_DATA_UNITS.put(DataColumnType.USER_FCO2_SRC, new ArrayList<String>(Arrays.asList("")));
		CHECKER_DATA_UNITS.put(DataColumnType.FCO2_AIR, new ArrayList<String>(Arrays.asList("uatm")));
		CHECKER_DATA_UNITS.put(DataColumnType.WIND_SPEED, new ArrayList<String>(Arrays.asList("kn", "km/h", "m/s", "mph")));
		CHECKER_DATA_UNITS.put(DataColumnType.WIND_DIRECTION, new ArrayList<String>(Arrays.asList("decimal_degrees")));
		CHECKER_DATA_UNITS.put(DataColumnType.XCO2WATER_EQU, new ArrayList<String>(Arrays.asList("ppm")));
		CHECKER_DATA_UNITS.put(DataColumnType.XCO2WATER_SST, new ArrayList<String>(Arrays.asList("ppm")));
		CHECKER_DATA_UNITS.put(DataColumnType.PCO2WATER_EQU, new ArrayList<String>(Arrays.asList("uatm")));
		CHECKER_DATA_UNITS.put(DataColumnType.PCO2WATER_SST, new ArrayList<String>(Arrays.asList("uatm")));
		CHECKER_DATA_UNITS.put(DataColumnType.FCO2WATER_EQU, new ArrayList<String>(Arrays.asList("uatm")));
		CHECKER_DATA_UNITS.put(DataColumnType.FCO2WATER_SST, new ArrayList<String>(Arrays.asList("uatm")));
		CHECKER_DATA_UNITS.put(DataColumnType.XCO2AIR_DRY, new ArrayList<String>(Arrays.asList("ppm")));
		CHECKER_DATA_UNITS.put(DataColumnType.XCO2AIR_EQU, new ArrayList<String>(Arrays.asList("ppm")));
		CHECKER_DATA_UNITS.put(DataColumnType.PCO2AIR_WET, new ArrayList<String>(Arrays.asList("uatm")));
		CHECKER_DATA_UNITS.put(DataColumnType.FCO2AIR_WET, new ArrayList<String>(Arrays.asList("uatm")));
		CHECKER_DATA_UNITS.put(DataColumnType.SUPPLEMENTAL, new ArrayList<String>(Arrays.asList("")));
	}

	/**
	 * Generate the encrypted password for a given plain-text username 
	 * and password.  This is intended to only be a first level of
	 * encryption.
	 * 
	 * @param username
	 * 		plaintext username to use
	 * @param password
	 * 		plaintext password to use 
	 * @return
	 * 		encrypted password, or an empty string if an error occurs 
	 */
	public static String passhashFromPlainText(String username, String password) {
		// Make sure something reasonable Strings are given
		if ( (username.length() < 4) || (password.length() < 7) ) {
			return "";
		}

		// This salt is just to make sure the keys are long enough
		String salt = "4z#Ni!q?F7b0m9nK(uDF[g%T3pD_";

		// Encrypt the password
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey((username.substring(0,4) + password + salt)
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
	 * Generates the cruise-specific root filename of a metadata document.
	 * If the the upload filename starts with the cruise expocode, the upload 
	 * filename is just returned; otherwise the upload filename prefixed with 
	 * the cruise expocode and an underscore is returned.
	 * 
	 * @param cruiseExpocode
	 * 		expocode of the cruise associated with this metadata document
	 * @param uploadName
	 * 		user's name of the uploaded metadata document 
	 * @return
	 * 		cruise-specific metadata document filename
	 */
	public static String metadataFilename(String cruiseExpocode, String uploadName) {
		// Get the root filename, in case a path was given
		String rootName;
		int idx = uploadName.lastIndexOf("/");
		if ( idx >= 0 )
			rootName = uploadName.substring(idx+1);
		else
			rootName = uploadName;
		idx = rootName.lastIndexOf("\\");
		if ( idx >= 0 )
			rootName = rootName.substring(idx+1);
		// Check if the root filename already starts with the expocode
		if ( rootName.startsWith(cruiseExpocode) )
			return rootName;
		// Prefix with the cruise expocode and an underscore
		return cruiseExpocode + "_" + rootName;
	}

}