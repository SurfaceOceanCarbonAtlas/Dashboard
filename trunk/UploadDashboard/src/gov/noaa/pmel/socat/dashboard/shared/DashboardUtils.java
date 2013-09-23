/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

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

	/**
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

	/**
	 * Standard column types.  The delete type indicates data that should 
	 * be deleted from the data file; only the user can specify a column 
	 * to be this type.  The unknown type indicates a column that the user 
	 * needs specify as one of the other standard types.  The supplemental 
	 * type indicates supplemental data that is carried along but otherwise
	 * ignored.
	 */
	public static final CruiseDataColumnType[] STANDARD_TYPES = new CruiseDataColumnType[] {
		new CruiseDataColumnType(-1, "delete", "delete", "data to be deleted"),
		new CruiseDataColumnType( 0, "unknown", "unknown", "unknown data"),
		new CruiseDataColumnType( 1, "timestamp", "timestamp", "date and time of the measurement"),
		new CruiseDataColumnType( 2, "date", "date", "date of the measurement"),
		new CruiseDataColumnType( 3, "time", "time", "time of the measurement"),
		new CruiseDataColumnType( 4, "year", "yr", "year of the time of the measurement"),
		new CruiseDataColumnType( 5, "month", "mon", "month of the time of the measurement"),
		new CruiseDataColumnType( 6, "day", "day", "day of the time of the measurement"),
		new CruiseDataColumnType( 7, "hour", "hh", "hour of the time of the measurement"),
		new CruiseDataColumnType( 8, "minute", "mm", "minute of the time of the measurement"),
		new CruiseDataColumnType( 9, "second", "ss", "second of the time of the measurement"),
		new CruiseDataColumnType(10, "longitude", "longitude", "measurement longitude"),
		new CruiseDataColumnType(11, "latitude", "latitude", "measurement latitude"),
		new CruiseDataColumnType(12, "depth", "sample_depth", "water sampling depth"),
		new CruiseDataColumnType(13, "salinity", "sal", "measured sea surface salinity"),
		new CruiseDataColumnType(14, "temperature", "Tequ", "equilibrator chamber temperature"),
		new CruiseDataColumnType(15, "temperature", "SST", "measured sea surface temperature"),
		new CruiseDataColumnType(16, "pressure", "Pequ", "equilibrator chamber pressure"),
		new CruiseDataColumnType(17, "pressure", "PPPP", "measured atmospheric pressure"),
		new CruiseDataColumnType(18, "xCO2", "xCO2water_equ_dry", "measured xCO2 (water) in micromole per mole at equilibrator temperature (dry air)"),
		new CruiseDataColumnType(19, "xCO2", "xCO2water_SST_dry", "measured xCO2 (water) in micromole per mole at sea surface temperature (dry air)"),
		new CruiseDataColumnType(20, "pCO2", "pCO2water_equ_wet", "measured pCO2 (water) in microatmospheres at equilibrator temperature (wet air)"),
		new CruiseDataColumnType(21, "pCO2", "pCO2water_SST_wet", "measured pCO2 (water) in microatmospheres at sea surface temperature (wet air)"),
		new CruiseDataColumnType(22, "fCO2", "fCO2water_equ_wet", "measured fCO2 (water) in microatmospheres at equilibrator temperature (wet air)"),
		new CruiseDataColumnType(23, "fCO2", "fCO2water_SST_wet", "measured fCO2 (water) in microatmospheres at sea surface temperature (wet air)"),
		new CruiseDataColumnType(99, "supplemental", "supplimental", "supplemental data")
	};

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