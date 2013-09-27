/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.ArrayList;
import java.util.Arrays;

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
	public static final String ARCHIVE_STATUS_OWNER_TO_ARCHIVE = "Waiting on owner";
	public static final String ARCHIVE_STATUS_SUBMITTED_PREFIX = "Submitted to ";
	public static final String ARCHIVE_STATUS_ARCHIVED_PREFIX = "DOI ";

	// Standard data column numbers, 
	// which are also indices into the arrays that follow
	/**
	 * The delete data type indicates data that should be deleted 
	 * from the data file; only the user can specify a column 
	 * to be this type.
	 */
	public static final int DELETE_DATA_STD_COLUMN_NUM = 0;
	/**
	 * The unknown data type indicates data that the user 
	 * needs specify as one of the other standard types.
	 */
	public static final int UNKNOWN_DATA_STD_COLUMN_NUM = 1;
	/**
	 * The timestamp data type has both date and time.
	 */
	public static final int TIMESTAMP_STD_COLUMN_NUM = 2;
	/**
	 * The date data type has only the date; no time.
	 */
	public static final int DATE_STD_COLUMN_NUM = 3;
	public static final int YEAR_STD_COLUMN_NUM = 4;
	public static final int MONTH_STD_COLUMN_NUM = 5;
	public static final int DAY_STD_COLUMN_NUM = 6;
	/**
	 * The time data type has only the time; no date.
	 */
	public static final int TIME_STD_COLUMN_NUM = 7;
	public static final int HOUR_STD_COLUMN_NUM = 8;
	public static final int MINUTE_STD_COLUMN_NUM = 9;
	public static final int SECOND_STD_COLUMN_NUM = 10;
	public static final int LONGITUDE_STD_COLUMN_NUM = 11;
	public static final int LATITUDE_STD_COLUMN_NUM = 12;
	public static final int SAMPLE_DEPTH_STD_COLUMN_NUM = 13;
	public static final int SAMPLE_SAL_STD_COLUMN_NUM = 14;
	public static final int TEQU_STD_COLUMN_NUM = 15;
	public static final int SST_STD_COLUMN_NUM = 16;
	public static final int PEQU_STD_COLUMN_NUM = 17;
	public static final int PPPP_STD_COLUMN_NUM = 18;
	public static final int XCO2_EQU_STD_COLUMN_NUM = 19;
	public static final int XCO2_SST_STD_COLUMN_NUM = 20;
	public static final int PCO2_EQU_STD_COLUMN_NUM = 21;
	public static final int PCO2_SST_STD_COLUMN_NUM = 22;
	public static final int FCO2_EQU_STD_COLUMN_NUM = 23;
	public static final int FCO2_SST_STD_COLUMN_NUM = 24;
	/**
	 * The supplemental data type indicates data that is 
	 * carried along but otherwise ignored.
	 */
	public static final int SUPPLEMENTAL_DATA_STD_COLUMN_NUM = 25;

	/**
	 * data types of the standard data columns
	 */
	public static final ArrayList<String> STD_DATA_TYPES = 
			new ArrayList<String>(Arrays.asList(
					"delete", 
					"unknown", 
					"timestamp", 
					"date", 
					"year", 
					"month", 
					"day", 
					"time", 
					"hour", 
					"minute", 
					"second", 
					"longitude", 
					"latitude", 
					"depth", 
					"salinity", 
					"temperature", 
					"temperature", 
					"pressure", 
					"pressure", 
					"xCO2", 
					"xCO2", 
					"pCO2", 
					"pCO2", 
					"fCO2", 
					"fCO2", 
					"supplemental" 
			));
	/**
	 * standard header names of the standard data columns
	 */
	public static final ArrayList<String> STD_DATA_HEADER_NAMES = 
			new ArrayList<String>(Arrays.asList(
					"delete", 
					"unknown", 
					"timestamp", 
					"date", 
					"yr", 
					"mon", 
					"day", 
					"time", 
					"hh", 
					"mm", 
					"ss", 
					"longitude", 
					"latitude", 
					"sample_depth", 
					"sal", 
					"Tequ", 
					"SST", 
					"Pequ", 
					"PPPP", 
					"xCO2water_equ_dry", 
					"xCO2water_SST_dry", 
					"pCO2water_equ_wet", 
					"pCO2water_SST_wet", 
					"fCO2water_equ_wet", 
					"fCO2water_SST_wet", 
					"supplemental" 
			));
	/**
	 * standard data descriptions of the standard data columns
	 */
	public static final ArrayList<String> STD_DATA_DESCRIPTIONS = 
			new ArrayList<String>(Arrays.asList(
					"data to be deleted", 
					"unknown data to be identified", 
					"date and time of the measurement", 
					"date of the measurement", 
					"year of the date of the measurement", 
					"month of the date of the measurement", 
					"day of the date of the measurement", 
					"time of the measurement", 
					"hour of the time of the measurement", 
					"minute of the time of the measurement", 
					"second of the time of the measurement", 
					"measurement longitude", 
					"measurement latitude", 
					"water sampling depth", 
					"measured sea surface salinity", 
					"equilibrator chamber temperature", 
					"measured sea surface temperature", 
					"equilibrator chamber pressure", 
					"measured atmospheric pressure", 
					"measured xCO2 (water) using equilibrator temperature (dry air)", 
					"measured xCO2 (water) using sea surface temperature (dry air)", 
					"measured pCO2 (water) using equilibrator temperature (wet air)", 
					"measured pCO2 (water) using sea surface temperature (wet air)", 
					"measured fCO2 (water) using equilibrator temperature (wet air)",
					"measured fCO2 (water) using sea surface temperature (wet air)", 
					"supplemental data to be kept" 
			));
	/**
	 * arrays of known units for the standard data columns 
	 */
	public final static ArrayList<ArrayList<String>> STD_DATA_UNITS = 
			new ArrayList<ArrayList<String>>(Arrays.asList(
					new ArrayList<String>(Arrays.asList("")), 
					new ArrayList<String>(Arrays.asList("")),
					new ArrayList<String>(Arrays.asList(
							"YYYY-MM-DD HH:MM:SS", "MON DAY YEAR HH:MM:SS", "DAY MON YEAR HH:MM:SS")), 
					new ArrayList<String>(Arrays.asList("YYYY-MM-DD", "MON DAY YEAR", "DAY MON YEAR")), 
					new ArrayList<String>(Arrays.asList("")), 
					new ArrayList<String>(Arrays.asList("")), 
					new ArrayList<String>(Arrays.asList("")), 
					new ArrayList<String>(Arrays.asList("HH:MM:SS")), 
					new ArrayList<String>(Arrays.asList("")),
					new ArrayList<String>(Arrays.asList("")),
					new ArrayList<String>(Arrays.asList("")),
					new ArrayList<String>(Arrays.asList("decimal deg. E")), 
					new ArrayList<String>(Arrays.asList("decimal deg. N")),
					new ArrayList<String>(Arrays.asList("meters")), 
					new ArrayList<String>(Arrays.asList("PSU")), 
					new ArrayList<String>(Arrays.asList("deg. C")),
					new ArrayList<String>(Arrays.asList("deg. C")),
					new ArrayList<String>(Arrays.asList("hPa", "mbar")),
					new ArrayList<String>(Arrays.asList("hPa", "mbar")),
					new ArrayList<String>(Arrays.asList("micromole per mole")),
					new ArrayList<String>(Arrays.asList("micromole per mole")),
					new ArrayList<String>(Arrays.asList("microatmospheres")),
					new ArrayList<String>(Arrays.asList("microatmospheres")), 
					new ArrayList<String>(Arrays.asList("microatmospheres")),
					new ArrayList<String>(Arrays.asList("microatmospheres")), 
					new ArrayList<String>(Arrays.asList(""))
			));

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
	 * Decodes a JSON-encoded array of numbers into a byte array.
	 * May not be a complete implementation. 
	 * 
	 * @param arrayStr
	 * 		JSON-encoded array of byte values to use
	 * @return
	 * 		a byte array represented arrayStr
	 * @throws NumberFormatException
	 * 		if keyStr does not start with '[', does not end with ']', 
	 * 		or contains values inappropriate for the byte type
	 */
	public static byte[] decodeByteArray(String arrayStr) 
										throws NumberFormatException {
		if ( ! ( arrayStr.startsWith("[") && arrayStr.endsWith("]") ) )
			throw new NumberFormatException(
					"Encoded byte array not enclosed in brackets");
		String[] pieces = arrayStr.substring(1, arrayStr.length()-1)
								  .split("\\s*,\\s*");
		if ( (pieces.length == 1) && pieces[0].trim().isEmpty() )
			return new byte[0];
		byte[] byteArray = new byte[pieces.length];
		for (int k = 0; k < pieces.length; k++)
			byteArray[k] = Byte.parseByte(pieces[k].trim());
		return byteArray;
	}

}