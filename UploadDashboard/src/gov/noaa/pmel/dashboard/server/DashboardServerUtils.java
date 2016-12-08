/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Karl Smith
 */
public class DashboardServerUtils {

	/** Marker data type used to indicate an severe error in the combination of lon/lat/time */
	public static final DashDataType GEOPOSITION = new DashDataType(DashboardUtils.GEOPOSITION);

	/**
	 * UNKNOWN needs to be respecified as one of the (other) data column types.
	 */
	public static final DashDataType UNKNOWN = new DashDataType(DashboardUtils.UNKNOWN);

	/**
	 * OTHER is for supplementary data in the user's original data file but 
	 * otherwise not used.  A description of each column with this type must 
	 * be part of the metadata, but the values are not validated or used. 
	 * Multiple columns may have this type.
	 */
	public static final DashDataType OTHER = new DashDataType(DashboardUtils.OTHER);

	/**
	 * Unique identifier for the dataset.
	 * For SOCAT, the expocode is NODCYYYYMMDD where NODC is the ship code 
	 * and YYYY-MM-DD is the start date for the cruise; and possibly followed
	 * by -1 or -2 for non-ship platforms - where NODC is does not distinguish
	 * different platform names.  (metadata)
	 */
	public static final DashDataType EXPOCODE = new DashDataType(DashboardUtils.EXPOCODE);

	/**
	 * User-provided name for the dataset (metadata)
	 */
	public static final DashDataType DATASET_NAME = new DashDataType(DashboardUtils.DATASET_NAME);

	public static final DashDataType PLATFORM_NAME = new DashDataType(DashboardUtils.PLATFORM_NAME);
	public static final DashDataType ORGANIZATION_NAME = new DashDataType(DashboardUtils.ORGANIZATION_NAME);
	public static final DashDataType INVESTIGATOR_NAMES = new DashDataType(DashboardUtils.INVESTIGATOR_NAMES);
	public static final DashDataType PLATFORM_TYPE = new DashDataType(DashboardUtils.PLATFORM_TYPE);
	public static final DashDataType WESTERNMOST_LONGITUDE = new DashDataType(DashboardUtils.WESTERNMOST_LONGITUDE);
	public static final DashDataType EASTERNMOST_LONGITUDE = new DashDataType(DashboardUtils.EASTERNMOST_LONGITUDE);
	public static final DashDataType SOUTHERNMOST_LATITUDE = new DashDataType(DashboardUtils.SOUTHERNMOST_LATITUDE);
	public static final DashDataType NORTHERNMOST_LATITUDE = new DashDataType(DashboardUtils.NORTHERNMOST_LATITUDE);
	public static final DashDataType TIME_COVERAGE_START = new DashDataType(DashboardUtils.TIME_COVERAGE_START);
	public static final DashDataType TIME_COVERAGE_END = new DashDataType(DashboardUtils.TIME_COVERAGE_END);
	public static final DashDataType QC_FLAG = new DashDataType(DashboardUtils.QC_FLAG);
	public static final DashDataType SAMPLE_NUMBER = new DashDataType(DashboardUtils.SAMPLE_NUMBER);

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

	public static final DashDataType LONGITUDE = new DashDataType(DashboardUtils.LONGITUDE);
	public static final DashDataType LATITUDE = new DashDataType(DashboardUtils.LATITUDE);
	public static final DashDataType SAMPLE_DEPTH = new DashDataType(DashboardUtils.SAMPLE_DEPTH);
	public static final DashDataType TIME = new DashDataType(DashboardUtils.TIME);

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

	/**
	 * NODC codes (all upper-case) for Moorings and Fixed Buoys 
	 */
	private static final HashSet<String> FIXED_PLATFORM_NODC_CODES = 
			new HashSet<String>(Arrays.asList("067F", "147F", "187F", "247F", 
					"267F", "297F", "3119", "3164", "317F", "33GO", "33TT", 
					"357F", "48MB", "497F", "747F", "767F", "907F", "GH7F"));

	/**
	 * NODC codes (all upper-case) for Drifting Buoys 
	 */
	private static final HashSet<String> DRIFTING_BUOY_NODC_CODES = 
			new HashSet<String>(Arrays.asList("09DB", "18DZ", "35DR", "49DZ", 
					"61DB", "74DZ", "91DB", "99DB"));

	/** Pattern for getKeyForName */
	private static final Pattern stripPattern = Pattern.compile("[^a-z0-9]+");

	/**
	 * Computes a key for the given name which is case-insensitive and ignores 
	 * non-alphanumeric characters.  The value returned is equivalent to 
	 * <pre>name.toLowerCase().replaceAll("[^a-z0-9]+", "")</pre>
	 * 
	 * @param name
	 * 		name to use
	 * @return
	 * 		key for the name
	 */
	public static String getKeyForName(String name) {
		return stripPattern.matcher(name.toLowerCase()).replaceAll("");
	}

	// Pattern for checking for invalid characters in the expocode
	private static final Pattern invalidExpocodePattern = 
			Pattern.compile("[^" + DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]");

	/**
	 * Checks and standardized a given expocode.
	 * 
	 * @param expocode
	 * 		expocode to check
	 * @return
	 * 		standardized (uppercase) expocode
	 * @throws IllegalArgumentException
	 * 		if the expocode is unreasonable
	 * 		(invalid characters, too short, too long)
	 */
	public static String checkExpocode(String expocode) throws IllegalArgumentException {
		if ( expocode == null )
			throw new IllegalArgumentException("Expocode not given");
		// Do some automatic clean-up
		String upperExpo = expocode.trim().toUpperCase();
		// Make sure it is the proper length
		if ( (upperExpo.length() < DashboardUtils.MIN_EXPOCODE_LENGTH) || 
			 (upperExpo.length() > DashboardUtils.MAX_EXPOCODE_LENGTH) )
			throw new IllegalArgumentException(
					"Invalid Expocode length");
		// Make sure there are no invalid characters
		Matcher mat = invalidExpocodePattern.matcher(upperExpo);
		if ( mat.find() )
			throw new IllegalArgumentException(
					"Invalid characters in the Expocode");
		return upperExpo;
	}

	/**
	 * Checks the validity of the given "NODC code" (first four characters of a standard expocode).
	 * This does not actually check that the value is listed in the NODC registry of ships.
	 * 
	 * @param nodccode
	 * 		expocode start to check
	 * @return
	 * 		false if nodccode is not exactly four characters from 
	 * 		{@link DashboardUtils#VALID_EXPOCODE_CHARACTERS};
	 * 		otherwise true
	 */
	public static boolean isLikeNODCCode(String nodccode) {
		if ( (nodccode == null) || (nodccode.length() != 4) )
			return false;
		Matcher mat = invalidExpocodePattern.matcher(nodccode);
		if ( mat.find() )
			return false;
		return true;
	}

	/**
	 * Guesses the platform type from the platform name or the expocode.
	 * If the platform name or NODC code from the expocode is that of
	 * a mooring or drifting buoy, that type is returned; 
	 * otherwise it is assumed to be a ship.
	 * 
	 * @param expocode
	 * 		expocode of the dataset
	 * @param platformName
	 * 		platform name for the dataset
	 * @return
	 * 		one of "Mooring", "Drifting Buoy", or "Ship"
	 */
	public static String guessPlatformType(String expocode, String platformName) {
		if ( "Mooring".equalsIgnoreCase(platformName) )
			return "Mooring";
		if ( "Drifting Buoy".equalsIgnoreCase(platformName) )
			return "Drifting Buoy";
		if ( "Bouy".equalsIgnoreCase(platformName) )
			return "Mooring";

		String nodc = expocode.substring(0, 4).toUpperCase();
		if ( DashboardServerUtils.FIXED_PLATFORM_NODC_CODES.contains(nodc) )
			return "Mooring";
		if ( DashboardServerUtils.DRIFTING_BUOY_NODC_CODES.contains(nodc) )
			return "Drifting Buoy";

		return "Ship";
	}

}
