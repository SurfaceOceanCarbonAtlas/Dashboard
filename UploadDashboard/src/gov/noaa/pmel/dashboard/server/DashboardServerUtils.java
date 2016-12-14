/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import java.util.regex.Pattern;

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
	 * Unique identifier for the dataset. (metadata)
	 */
	public static final DashDataType DATASET_ID = new DashDataType(DashboardUtils.DATASET_ID);

	public static final DashDataType PLATFORM_NAME = new DashDataType(DashboardUtils.PLATFORM_NAME);
	public static final DashDataType PLATFORM_TYPE = new DashDataType(DashboardUtils.PLATFORM_TYPE);
	public static final DashDataType ORGANIZATION_NAME = new DashDataType(DashboardUtils.ORGANIZATION_NAME);
	public static final DashDataType INVESTIGATOR_NAMES = new DashDataType(DashboardUtils.INVESTIGATOR_NAMES);
	public static final DashDataType WESTERNMOST_LONGITUDE = new DashDataType(DashboardUtils.WESTERNMOST_LONGITUDE);
	public static final DashDataType EASTERNMOST_LONGITUDE = new DashDataType(DashboardUtils.EASTERNMOST_LONGITUDE);
	public static final DashDataType SOUTHERNMOST_LATITUDE = new DashDataType(DashboardUtils.SOUTHERNMOST_LATITUDE);
	public static final DashDataType NORTHERNMOST_LATITUDE = new DashDataType(DashboardUtils.NORTHERNMOST_LATITUDE);
	public static final DashDataType TIME_COVERAGE_START = new DashDataType(DashboardUtils.TIME_COVERAGE_START);
	public static final DashDataType TIME_COVERAGE_END = new DashDataType(DashboardUtils.TIME_COVERAGE_END);
	public static final DashDataType STATUS = new DashDataType(DashboardUtils.STATUS);
	public static final DashDataType VERSION = new DashDataType(DashboardUtils.VERSION);

	/**
	 * User-provided name for the dataset (user data type)
	 */
	public static final DashDataType DATASET_NAME = new DashDataType(DashboardUtils.DATASET_NAME);

	public static final DashDataType SAMPLE_NUMBER = new DashDataType(DashboardUtils.SAMPLE_NUMBER);
	public static final DashDataType LONGITUDE = new DashDataType(DashboardUtils.LONGITUDE);
	public static final DashDataType LATITUDE = new DashDataType(DashboardUtils.LATITUDE);
	public static final DashDataType SAMPLE_DEPTH = new DashDataType(DashboardUtils.SAMPLE_DEPTH);
	public static final DashDataType TIME = new DashDataType(DashboardUtils.TIME);

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
