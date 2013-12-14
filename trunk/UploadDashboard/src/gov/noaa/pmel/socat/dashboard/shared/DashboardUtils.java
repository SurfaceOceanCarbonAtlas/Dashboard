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
	public static final String CHECK_STATUS_QUESTIONABLE = "Questionable";
	public static final String CHECK_STATUS_FAILED = "Failed";

	public static final String QC_STATUS_NOT_SUBMITTED = "";
	public static final String QC_STATUS_AUTOFAIL = "Check fail";
	public static final String QC_STATUS_SUBMITTED = "Submitted";
	public static final String QC_STATUS_ACCEPTED_A = "QC Flag A";
	public static final String QC_STATUS_ACCEPTED_B = "QC Flag B";
	public static final String QC_STATUS_ACCEPTED_C = "QC Flag C";
	public static final String QC_STATUS_ACCEPTED_D = "QC Flag D";
	public static final String QC_STATUS_UNACCEPTABLE = "QC Flag F";
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
	public static final EnumMap<CruiseDataColumnType,String> STD_HEADER_NAMES = 
			new EnumMap<CruiseDataColumnType,String>(CruiseDataColumnType.class);
	static {
		STD_HEADER_NAMES.put(CruiseDataColumnType.IGNORE, "(ignore)");
		STD_HEADER_NAMES.put(CruiseDataColumnType.UNKNOWN, "(unknown)");
		STD_HEADER_NAMES.put(CruiseDataColumnType.TIMESTAMP, "timestamp");
		STD_HEADER_NAMES.put(CruiseDataColumnType.DATE, "date");
		STD_HEADER_NAMES.put(CruiseDataColumnType.YEAR, "yr");
		STD_HEADER_NAMES.put(CruiseDataColumnType.MONTH, "mon");
		STD_HEADER_NAMES.put(CruiseDataColumnType.DAY, "day");
		STD_HEADER_NAMES.put(CruiseDataColumnType.TIME, "time");
		STD_HEADER_NAMES.put(CruiseDataColumnType.HOUR, "hh");
		STD_HEADER_NAMES.put(CruiseDataColumnType.MINUTE, "mm");
		STD_HEADER_NAMES.put(CruiseDataColumnType.SECOND, "ss");
		STD_HEADER_NAMES.put(CruiseDataColumnType.LONGITUDE, "longitude");
		STD_HEADER_NAMES.put(CruiseDataColumnType.LATITUDE, "latitude");
		STD_HEADER_NAMES.put(CruiseDataColumnType.SAMPLE_DEPTH, "sample_depth");
		STD_HEADER_NAMES.put(CruiseDataColumnType.SAMPLE_SALINITY, "sal");
		STD_HEADER_NAMES.put(CruiseDataColumnType.EQUILIBRATOR_TEMPERATURE, "Tequ");
		STD_HEADER_NAMES.put(CruiseDataColumnType.SEA_SURFACE_TEMPERATURE, "SST");
		STD_HEADER_NAMES.put(CruiseDataColumnType.EQUILIBRATOR_PRESSURE, "Pequ");
		STD_HEADER_NAMES.put(CruiseDataColumnType.SEA_LEVEL_PRESSURE, "PPPP");
		STD_HEADER_NAMES.put(CruiseDataColumnType.XCO2_EQU, "xCO2water_equ_dry");
		STD_HEADER_NAMES.put(CruiseDataColumnType.XCO2_SST, "xCO2water_SST_dry");
		STD_HEADER_NAMES.put(CruiseDataColumnType.PCO2_EQU, "pCO2water_equ_wet");
		STD_HEADER_NAMES.put(CruiseDataColumnType.PCO2_SST, "pCO2water_SST_wet");
		STD_HEADER_NAMES.put(CruiseDataColumnType.FCO2_EQU, "fCO2water_equ_wet");
		STD_HEADER_NAMES.put(CruiseDataColumnType.FCO2_SST, "fCO2water_SST_wet");
		STD_HEADER_NAMES.put(CruiseDataColumnType.SUPPLEMENTAL, "(supplemental)");
	}

	/**
	 * standard data descriptions of the standard data columns
	 */
	public static final EnumMap<CruiseDataColumnType,String> STD_DESCRIPTIONS = 
			new EnumMap<CruiseDataColumnType,String>(CruiseDataColumnType.class);
	static {
		STD_DESCRIPTIONS.put(CruiseDataColumnType.IGNORE, "data to be ignored"); 
		STD_DESCRIPTIONS.put(CruiseDataColumnType.UNKNOWN, "unknown data to be identified");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.TIMESTAMP, "date and time of the measurement");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.DATE, "date of the measurement");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.YEAR, "year of the date of the measurement");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.MONTH, "month of the date of the measurement");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.DAY, "day of the date of the measurement");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.TIME, "time of the measurement");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.HOUR, "hour of the time of the measurement");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.MINUTE, "minute of the time of the measurement");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.SECOND, "second of the time of the measurement");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.LONGITUDE, "measurement longitude");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.LATITUDE, "measurement latitude");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.SAMPLE_DEPTH, "water sampling depth");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.SAMPLE_SALINITY, "measured sea surface salinity");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.EQUILIBRATOR_TEMPERATURE, "equilibrator chamber temperature");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.SEA_SURFACE_TEMPERATURE, "measured sea surface temperature");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.EQUILIBRATOR_PRESSURE, "equilibrator chamber pressure");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.SEA_LEVEL_PRESSURE, "measured atmospheric pressure");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.XCO2_EQU, "measured xCO2 (water) using equilibrator temperature (dry air)");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.XCO2_SST, "measured xCO2 (water) using sea surface temperature (dry air)");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.PCO2_EQU, "measured pCO2 (water) using equilibrator temperature (wet air)");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.PCO2_SST, "measured pCO2 (water) using sea surface temperature (wet air)");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.FCO2_EQU, "measured fCO2 (water) using equilibrator temperature (wet air)");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.FCO2_SST, "measured fCO2 (water) using sea surface temperature (wet air)");
		STD_DESCRIPTIONS.put(CruiseDataColumnType.SUPPLEMENTAL, "supplemental data to be kept");
	}

	/**
	 * known data units of the standard data columns
	 */
	public static final EnumMap<CruiseDataColumnType,ArrayList<String>> STD_DATA_UNITS = 
			new EnumMap<CruiseDataColumnType,ArrayList<String>>(CruiseDataColumnType.class);
	static {
		STD_DATA_UNITS.put(CruiseDataColumnType.IGNORE, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(CruiseDataColumnType.UNKNOWN, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(CruiseDataColumnType.TIMESTAMP, new ArrayList<String>(Arrays.asList(
				"YYYY-MM-DD HH:MM:SS", "MON DAY YEAR HH:MM:SS", "DAY MON YEAR HH:MM:SS")));
		STD_DATA_UNITS.put(CruiseDataColumnType.DATE, new ArrayList<String>(Arrays.asList(
				"YYYY-MM-DD", "MON DAY YEAR", "DAY MON YEAR")));
		STD_DATA_UNITS.put(CruiseDataColumnType.YEAR, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(CruiseDataColumnType.MONTH, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(CruiseDataColumnType.DAY, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(CruiseDataColumnType.TIME, new ArrayList<String>(Arrays.asList("HH:MM:SS")));
		STD_DATA_UNITS.put(CruiseDataColumnType.HOUR, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(CruiseDataColumnType.MINUTE, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(CruiseDataColumnType.SECOND, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(CruiseDataColumnType.LONGITUDE, new ArrayList<String>(Arrays.asList("decimal deg. E")));
		STD_DATA_UNITS.put(CruiseDataColumnType.LATITUDE, new ArrayList<String>(Arrays.asList("decimal deg. N")));
		STD_DATA_UNITS.put(CruiseDataColumnType.SAMPLE_DEPTH, new ArrayList<String>(Arrays.asList("meters")));
		STD_DATA_UNITS.put(CruiseDataColumnType.SAMPLE_SALINITY, new ArrayList<String>(Arrays.asList("PSU")));
		STD_DATA_UNITS.put(CruiseDataColumnType.EQUILIBRATOR_TEMPERATURE, new ArrayList<String>(Arrays.asList("deg. C")));
		STD_DATA_UNITS.put(CruiseDataColumnType.SEA_SURFACE_TEMPERATURE, new ArrayList<String>(Arrays.asList("deg. C")));
		STD_DATA_UNITS.put(CruiseDataColumnType.EQUILIBRATOR_PRESSURE, new ArrayList<String>(Arrays.asList("hPa", "mbar")));
		STD_DATA_UNITS.put(CruiseDataColumnType.SEA_LEVEL_PRESSURE, new ArrayList<String>(Arrays.asList("hPa", "mbar")));
		STD_DATA_UNITS.put(CruiseDataColumnType.XCO2_EQU, new ArrayList<String>(Arrays.asList("micromole per mole")));
		STD_DATA_UNITS.put(CruiseDataColumnType.XCO2_SST, new ArrayList<String>(Arrays.asList("micromole per mole")));
		STD_DATA_UNITS.put(CruiseDataColumnType.PCO2_EQU, new ArrayList<String>(Arrays.asList("microatmospheres")));
		STD_DATA_UNITS.put(CruiseDataColumnType.PCO2_SST, new ArrayList<String>(Arrays.asList("microatmospheres")));
		STD_DATA_UNITS.put(CruiseDataColumnType.FCO2_EQU, new ArrayList<String>(Arrays.asList("microatmospheres")));
		STD_DATA_UNITS.put(CruiseDataColumnType.FCO2_SST, new ArrayList<String>(Arrays.asList("microatmospheres")));
		STD_DATA_UNITS.put(CruiseDataColumnType.SUPPLEMENTAL, new ArrayList<String>(Arrays.asList("")));
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