/**
 *
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCFlag;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * @author Karl Smith
 */
public class DashboardServerUtils {

    public static final int MIN_DATASET_ID_LENGTH = 12;
    public static final int MAX_DATASET_ID_LENGTH = 14;

    public static final String AUTOMATED_DATA_CHECKER_USERNAME = "automated.data.checker";
    public static final String AUTOMATED_DATA_CHECKER_REALNAME = "automated data checker";

    // QC flag name used for dataset QC flags
    public static final String DATASET_QCFLAG_NAME = "dataset";

    // All possible dataset QC flags
    public static final String DATASET_QCFLAG_COMMENT = "H";
    public static final String DATASET_QCFLAG_SUSPEND = "S";
    public static final String DATASET_QCFLAG_EXCLUDE = "X";
    public static final String DATASET_QCFLAG_NEW = "N";
    public static final String DATASET_QCFLAG_UPDATED = "U";
    public static final String DATASET_QCFLAG_A = "A";
    public static final String DATASET_QCFLAG_B = "B";
    public static final String DATASET_QCFLAG_C = "C";
    public static final String DATASET_QCFLAG_D = "D";
    public static final String DATASET_QCFLAG_E = "E";
    public static final String DATASET_QCFLAG_CONFLICT = "Q";
    // QCFLAG_RENAMED also used for data QC flags
    public static final String QCFLAG_RENAMED = "R";

    // Dataset QC strings - datasets that can be modified
    public static final String DATASET_STATUS_NOT_SUBMITTED = "";
    public static final String DATASET_STATUS_SUSPENDED = "Suspended";
    public static final String DATASET_STATUS_EXCLUDED = "Excluded";
    // Dataset QC strings - datasets that cannot be modified
    public static final String DATASET_STATUS_SUBMITTED = "Submitted";
    public static final String DATASET_STATUS_ACCEPTED_A = "Flag A";
    public static final String DATASET_STATUS_ACCEPTED_B = "Flag B";
    public static final String DATASET_STATUS_ACCEPTED_C = "Flag C";
    public static final String DATASET_STATUS_ACCEPTED_D = "Flag D";
    public static final String DATASET_STATUS_ACCEPTED_E = "Flag E";
    public static final String DATASET_STATUS_CONFLICT = "Conflict";
    public static final String DATASET_STATUS_RENAMED = "Renamed";

    /**
     * Map of dataset QC flag values to dataset status values.
     * {@link #DATASET_QCFLAG_COMMENT} not included as it does not affect the status.
     */
    public static final HashMap<String,String> DATASET_FLAG_STATUS_MAP;

    static {
        DATASET_FLAG_STATUS_MAP = new HashMap<String,String>();
        DATASET_FLAG_STATUS_MAP.put(DATASET_QCFLAG_SUSPEND, DATASET_STATUS_SUSPENDED);
        DATASET_FLAG_STATUS_MAP.put(DATASET_QCFLAG_EXCLUDE, DATASET_STATUS_EXCLUDED);
        DATASET_FLAG_STATUS_MAP.put(DATASET_QCFLAG_NEW, DATASET_STATUS_SUBMITTED);
        DATASET_FLAG_STATUS_MAP.put(DATASET_QCFLAG_UPDATED, DATASET_STATUS_SUBMITTED);
        DATASET_FLAG_STATUS_MAP.put(DATASET_QCFLAG_A, DATASET_STATUS_ACCEPTED_A);
        DATASET_FLAG_STATUS_MAP.put(DATASET_QCFLAG_B, DATASET_STATUS_ACCEPTED_B);
        DATASET_FLAG_STATUS_MAP.put(DATASET_QCFLAG_C, DATASET_STATUS_ACCEPTED_C);
        DATASET_FLAG_STATUS_MAP.put(DATASET_QCFLAG_D, DATASET_STATUS_ACCEPTED_D);
        DATASET_FLAG_STATUS_MAP.put(DATASET_QCFLAG_E, DATASET_STATUS_ACCEPTED_E);
        DATASET_FLAG_STATUS_MAP.put(DATASET_QCFLAG_CONFLICT, DATASET_STATUS_CONFLICT);
        DATASET_FLAG_STATUS_MAP.put(QCFLAG_RENAMED, DATASET_STATUS_RENAMED);
    }

    /**
     * Map of dataset QC status values to dataset QC flag values.
     * {@link #DATASET_STATUS_NOT_SUBMITTED} not included since there is no QC.
     * {@link #DATASET_STATUS_SUBMITTED} is mapped to {@link #DATASET_QCFLAG_UPDATED}.
     */
    public static final HashMap<String,String> DATASET_STATUS_FLAG_MAP;

    static {
        DATASET_STATUS_FLAG_MAP = new HashMap<String,String>();
        DATASET_STATUS_FLAG_MAP.put(DATASET_STATUS_SUSPENDED, DATASET_QCFLAG_SUSPEND);
        DATASET_STATUS_FLAG_MAP.put(DATASET_STATUS_EXCLUDED, DATASET_QCFLAG_EXCLUDE);
        DATASET_STATUS_FLAG_MAP.put(DATASET_STATUS_SUBMITTED, DATASET_QCFLAG_UPDATED);
        DATASET_STATUS_FLAG_MAP.put(DATASET_STATUS_ACCEPTED_A, DATASET_QCFLAG_A);
        DATASET_STATUS_FLAG_MAP.put(DATASET_STATUS_ACCEPTED_B, DATASET_QCFLAG_B);
        DATASET_STATUS_FLAG_MAP.put(DATASET_STATUS_ACCEPTED_C, DATASET_QCFLAG_C);
        DATASET_STATUS_FLAG_MAP.put(DATASET_STATUS_ACCEPTED_D, DATASET_QCFLAG_D);
        DATASET_STATUS_FLAG_MAP.put(DATASET_STATUS_ACCEPTED_E, DATASET_QCFLAG_E);
        DATASET_STATUS_FLAG_MAP.put(DATASET_STATUS_CONFLICT, DATASET_QCFLAG_CONFLICT);
        DATASET_STATUS_FLAG_MAP.put(DATASET_STATUS_RENAMED, QCFLAG_RENAMED);
    }

    // WOCE-type data QC flag values
    public static final String WOCE_ACCEPTABLE = "2";
    public static final String WOCE_QUESTIONABLE = "3";
    public static final String WOCE_BAD = "4";

    // replacement values for WOCE-type data QC flags for data being replaced in an update
    public static final String OLD_WOCE_ACCEPTABLE = "G";
    public static final String OLD_WOCE_QUESTIONABLE = "Q";
    public static final String OLD_WOCE_BAD = "B";

    // all possible region IDs except for Global, which is in DashboardUtils
    public static final String REGION_ID_NORTH_PACIFIC = "N";
    public static final String REGION_ID_TROPICAL_PACIFIC = "T";
    public static final String REGION_ID_NORTH_ATLANTIC = "A";
    public static final String REGION_ID_TROPICAL_ATLANTIC = "Z";
    public static final String REGION_ID_INDIAN = "I";
    public static final String REGION_ID_COASTAL = "C";
    public static final String REGION_ID_SOUTHERN_OCEANS = "O";
    public static final String REGION_ID_ARCTIC = "R";

    public static final HashMap<String,String> REGION_NAMES;

    static {
        REGION_NAMES = new HashMap<String,String>();
        REGION_NAMES.put(DashboardUtils.REGION_ID_GLOBAL, "Global");
        REGION_NAMES.put(REGION_ID_NORTH_PACIFIC, "North Pacific");
        REGION_NAMES.put(REGION_ID_TROPICAL_PACIFIC, "Tropical Pacific");
        REGION_NAMES.put(REGION_ID_NORTH_ATLANTIC, "North Atlantic");
        REGION_NAMES.put(REGION_ID_TROPICAL_ATLANTIC, "Tropical Atlantic");
        REGION_NAMES.put(REGION_ID_INDIAN, "Indian");
        REGION_NAMES.put(REGION_ID_COASTAL, "Coastal");
        REGION_NAMES.put(REGION_ID_SOUTHERN_OCEANS, "Southern Oceans");
        REGION_NAMES.put(REGION_ID_ARCTIC, "Arctic");
    }

    /**
     * Authalic radius, in kilometers, of Earth
     */
    public static final double EARTH_AUTHALIC_RADIUS = 6371.007;

    /**
     * "Distance" contribution, in kilometers, for every 24h time difference
     */
    public static final double SEAWATER_SPEED = 30.0;

    /**
     * Max "distance", in kilometers, still considered a crossover
     */
    public static final double MAX_CROSSOVER_DIST = 80.0;

    /**
     * Maximum difference in FCO2_rec for a high-quality crossover
     */
    public static final double MAX_FCO2_DIFF = 5.0;

    /**
     * Maximum difference in SST for a high-quality crossover
     */
    public static final double MAX_TEMP_DIFF = 0.3;

    /**
     * Max allowable difference in time, in seconds, between two crossover data points
     */
    public static final double MAX_TIME_DIFF = Math.ceil(24.0 * 60.0 * 60.0 * MAX_CROSSOVER_DIST / SEAWATER_SPEED);

    /**
     * Max allowable difference in latitude, in decimal degrees, between two crossover data points
     */
    public static final double MAX_LAT_DIFF = (MAX_CROSSOVER_DIST / EARTH_AUTHALIC_RADIUS) * (180.0 / Math.PI);

    // Some suggested categories
    public static final String BATHYMETRY_CATEGORY = "Bathymetry";
    public static final String IDENTIFIER_CATEGORY = "Identifier";
    public static final String LOCATION_CATEGORY = "Location";
    public static final String PLATFORM_CATEGORY = "Platform";
    public static final String QUALITY_CATEGORY = "Quality";
    public static final String TIME_CATEGORY = "Time";

    /**
     * Unit of completely specified time ("seconds since 1970-01-01T00:00:00Z")
     */
    public static final List<String> TIME_UNITS =
            Collections.singletonList("seconds since 1970-01-01T00:00:00Z");

    /**
     * Data type is only user-provided data; not used in DSG files.
     */
    public static final List<DashDataType.Role> USER_ONLY_ROLES =
            Collections.singletonList(DashDataType.Role.USER_DATA);

    /**
     * Data type is only data in DSG files; not (directly) provided by the user.
     */
    public static final List<DashDataType.Role> FILE_DATA_ONLY_ROLES =
            Collections.singletonList(DashDataType.Role.FILE_DATA);

    /**
     * Data type is only metadata in DSG files; not (directly) provided by the user.
     */
    public static final List<DashDataType.Role> FILE_METADATA_ONLY_ROLES =
            Collections.singletonList(DashDataType.Role.FILE_METADATA);

    /**
     * Data type is user-provided data also in DSG files.
     */
    public static final List<DashDataType.Role> USER_FILE_DATA_ROLES =
            Arrays.asList(DashDataType.Role.USER_DATA, DashDataType.Role.FILE_DATA);

    /**
     * Data type is user-provided metadata also in DSG files.
     */
    public static final List<DashDataType.Role> USER_FILE_METADATA_ROLES =
            Arrays.asList(DashDataType.Role.USER_DATA, DashDataType.Role.FILE_METADATA);

    /**
     * UNASSIGNED needs to be respecified as one of the (other) data column types.
     */
    public static final StringDashDataType UNKNOWN = new StringDashDataType(DashboardUtils.UNKNOWN,
            null, null, null,
            null, null, null, null, USER_ONLY_ROLES);

    /**
     * OTHER is for supplementary data in the user's original data file but otherwise not used.
     * A description of each column with this type must be part of the metadata, but the values
     * are not validated or used. Multiple columns may have this type.
     */
    public static final StringDashDataType OTHER = new StringDashDataType(DashboardUtils.OTHER,
            null, null, null,
            null, null, null, null, USER_ONLY_ROLES);

    /**
     * Unique identifier for the dataset
     * <p>
     * For SOCAT, the dataset ID is NODCYYYYMMDD (the expocode) where NODC is the ship code and YYYY-MM-DD
     * is the start date for the cruise; possibly followed by -1 or -2 for non-ship platforms, such as
     * moorings, where NODC does not distinguish different platform names.
     */
    public static final StringDashDataType DATASET_ID = new StringDashDataType(DashboardUtils.DATASET_ID,
            "expocode", IDENTIFIER_CATEGORY, null,
            null, null, null, null, USER_FILE_METADATA_ROLES);

    /**
     * Consecutive numbering of the samples in a dataset
     */
    public static final IntDashDataType SAMPLE_NUMBER = new IntDashDataType("sample_number",
            51.0, "sample num", "sample number", false,
            DashboardUtils.NO_UNITS, null, IDENTIFIER_CATEGORY, null,
            "1", null, null, null, FILE_DATA_ONLY_ROLES);

    /**
     * Completely specified sampling time (seconds since 1970-01-01T00:00:00Z) used in file data; computed value.
     */
    public static final DoubleDashDataType TIME = new DoubleDashDataType("time",
            52.0, "time", "sample time", false,
            TIME_UNITS, "time", TIME_CATEGORY, null,
            null, null, null, null, FILE_DATA_ONLY_ROLES);

    /**
     * User-provided name for the dataset
     */
    public static final StringDashDataType DATASET_NAME = new StringDashDataType("dataset_name",
            100.0, "cruise/dataset name", "investigator's name for this dataset", false,
            DashboardUtils.NO_UNITS, "dataset_name", IDENTIFIER_CATEGORY, null,
            null, null, null, null, USER_FILE_METADATA_ROLES);

    public static final StringDashDataType PLATFORM_NAME = new StringDashDataType("platform_name",
            101.0, "platform name", "platform name", false,
            DashboardUtils.NO_UNITS, "platform_name", PLATFORM_CATEGORY, null,
            null, null, null, null, USER_FILE_METADATA_ROLES);

    public static final StringDashDataType PLATFORM_TYPE = new StringDashDataType("platform_type",
            102.0, "platform type", "platform type", false,
            DashboardUtils.NO_UNITS, "platform_type", PLATFORM_CATEGORY, null,
            null, null, null, null, USER_FILE_METADATA_ROLES);

    public static final StringDashDataType INVESTIGATOR_NAMES = new StringDashDataType("investigators",
            103.0, "PI names", "investigators", false,
            DashboardUtils.NO_UNITS, "investigators", IDENTIFIER_CATEGORY, null,
            null, null, null, null, USER_FILE_METADATA_ROLES);

    public static final StringDashDataType ORGANIZATION_NAME = new StringDashDataType("organization",
            104.0, "PI organizations", "organization for each PI", false,
            DashboardUtils.NO_UNITS, "organization", IDENTIFIER_CATEGORY, null,
            null, null, null, null, USER_FILE_METADATA_ROLES);

    public static final DoubleDashDataType WESTERNMOST_LONGITUDE = new DoubleDashDataType("geospatial_lon_min",
            110.0, "westernmost lon", "westernmost longitude", false,
            DashboardUtils.LONGITUDE_UNITS, "geospatial_lon_min", LOCATION_CATEGORY, "degrees_east",
            "-540.0", "-180.0", "360.0", "540.0", FILE_METADATA_ONLY_ROLES);

    public static final DoubleDashDataType EASTERNMOST_LONGITUDE = new DoubleDashDataType("geospatial_lon_max",
            111.0, "easternmost lon", "easternmost longitude", false,
            DashboardUtils.LONGITUDE_UNITS, "geospatial_lon_max", LOCATION_CATEGORY, "degrees_east",
            "-540.0", "-180.0", "360.0", "540.0", FILE_METADATA_ONLY_ROLES);

    public static final DoubleDashDataType SOUTHERNMOST_LATITUDE = new DoubleDashDataType("geospatial_lat_min",
            112.0, "southernmost lat", "southernmost latitude", false,
            DashboardUtils.LATITUDE_UNITS, "geospatial_lat_min", LOCATION_CATEGORY, "degrees_north",
            "-90.0", null, null, "90.0", FILE_METADATA_ONLY_ROLES);

    public static final DoubleDashDataType NORTHERNMOST_LATITUDE = new DoubleDashDataType("geospatial_lat_max",
            113.0, "northernmost lat", "northernmost latitude", false,
            DashboardUtils.LATITUDE_UNITS, "geospatial_lat_max", LOCATION_CATEGORY, "degrees_north",
            "-90.0", null, null, "90.0", FILE_METADATA_ONLY_ROLES);

    public static final DoubleDashDataType TIME_COVERAGE_START = new DoubleDashDataType("time_coverage_start",
            114.0, "start time", "starting time", false,
            TIME_UNITS, "time_coverage_start", LOCATION_CATEGORY, null,
            null, null, null, null, FILE_METADATA_ONLY_ROLES);

    public static final DoubleDashDataType TIME_COVERAGE_END = new DoubleDashDataType("time_coverage_end",
            115.0, "end time", "ending time", false,
            TIME_UNITS, "time_coverage_end", LOCATION_CATEGORY, null,
            null, null, null, null, FILE_METADATA_ONLY_ROLES);

    public static final StringDashDataType SOURCE_DOI = new StringDashDataType("source_doi",
            120.0, "DOI", "DOI of the source dataset", false,
            DashboardUtils.NO_UNITS, null, IDENTIFIER_CATEGORY, null,
            null, null, null, null, USER_ONLY_ROLES);

    public static final StringDashDataType ENHANCED_DOI = new StringDashDataType("socat_doi",
            121.0, "SOCAT DOI", "DOI of the SOCAT-enhanced dataset", false,
            DashboardUtils.NO_UNITS, null, IDENTIFIER_CATEGORY, null,
            null, null, null, null, FILE_METADATA_ONLY_ROLES);

    public static final StringDashDataType DATASET_QC_FLAG = new StringDashDataType("qc_flag",
            122.0, "Dataset QC Flag", "QC assessment of the dataset", false,
            DashboardUtils.NO_UNITS, null, IDENTIFIER_CATEGORY, null,
            null, null, null, null, FILE_METADATA_ONLY_ROLES);

    public static final StringDashDataType VERSION = new StringDashDataType("socat_version",
            123.0, "socat_version", "SOCAT version", false,
            DashboardUtils.NO_UNITS, null, IDENTIFIER_CATEGORY, null,
            null, null, null, null, FILE_METADATA_ONLY_ROLES);

    public static final StringDashDataType ALL_REGION_IDS = new StringDashDataType("all_region_ids",
            124.0, "all Region IDs", "Sorted unique region IDs", false,
            DashboardUtils.NO_UNITS, null, DashboardServerUtils.LOCATION_CATEGORY, null,
            null, null, null, null, FILE_METADATA_ONLY_ROLES);


    public static final StringDashDataType SAMPLE_ID = new StringDashDataType("sample_id",
            300.0, "sample ID", "unique ID for this sample in the dataset", false,
            DashboardUtils.NO_UNITS, null, IDENTIFIER_CATEGORY, null,
            null, null, null, null, USER_ONLY_ROLES);

    public static final DoubleDashDataType LONGITUDE = new DoubleDashDataType(DashboardUtils.LONGITUDE,
            "longitude", LOCATION_CATEGORY, "degrees_east",
            "-540.0", "-180.0", "360.0", "540.0", USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType LATITUDE = new DoubleDashDataType(DashboardUtils.LATITUDE,
            "latitude", LOCATION_CATEGORY, "degrees_north",
            "-90.0", null, null, "90.0", USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType SAMPLE_DEPTH = new DoubleDashDataType(DashboardUtils.SAMPLE_DEPTH,
            "depth", BATHYMETRY_CATEGORY, null,
            "0.0", null, null, "16000", USER_FILE_DATA_ROLES);

    public static final StringDashDataType REGION_ID = new StringDashDataType("region_id",
            304.0, "Region ID", "SOCAT region ID", false,
            DashboardUtils.NO_UNITS, null, DashboardServerUtils.LOCATION_CATEGORY, null,
            null, null, null, null, FILE_DATA_ONLY_ROLES);

    /**
     * Date and time of the measurement
     */
    public static final StringDashDataType TIMESTAMP = new StringDashDataType(DashboardUtils.TIMESTAMP,
            "timestamp", TIME_CATEGORY, null,
            null, null, null, null, USER_ONLY_ROLES);

    /**
     * Date of the measurement - no time.
     */
    public static final StringDashDataType DATE = new StringDashDataType(DashboardUtils.DATE,
            "date", TIME_CATEGORY, null,
            null, null, null, null, USER_ONLY_ROLES);

    public static final IntDashDataType YEAR = new IntDashDataType(DashboardUtils.YEAR,
            "year", TIME_CATEGORY, null,
            "1900", "1950", "2050", "2100", USER_FILE_DATA_ROLES);

    public static final IntDashDataType MONTH_OF_YEAR = new IntDashDataType(DashboardUtils.MONTH_OF_YEAR,
            "month_of_year", TIME_CATEGORY, null,
            "1", null, null, "12", USER_FILE_DATA_ROLES);

    public static final IntDashDataType DAY_OF_MONTH = new IntDashDataType(DashboardUtils.DAY_OF_MONTH,
            "day_of_month", TIME_CATEGORY, null,
            "1", null, null, "31", USER_FILE_DATA_ROLES);

    public static final StringDashDataType TIME_OF_DAY = new StringDashDataType(DashboardUtils.TIME_OF_DAY,
            "time_of_day", TIME_CATEGORY, null,
            null, null, null, null, USER_ONLY_ROLES);

    public static final IntDashDataType HOUR_OF_DAY = new IntDashDataType(DashboardUtils.HOUR_OF_DAY,
            "hour_of_day", TIME_CATEGORY, null,
            "0", null, null, "24", USER_FILE_DATA_ROLES);

    public static final IntDashDataType MINUTE_OF_HOUR = new IntDashDataType(DashboardUtils.MINUTE_OF_HOUR,
            "minute_of_hour", TIME_CATEGORY, null,
            "0", null, null, "60", USER_FILE_DATA_ROLES);

    public static final DoubleDashDataType SECOND_OF_MINUTE = new DoubleDashDataType(DashboardUtils.SECOND_OF_MINUTE,
            "second_of_minute", TIME_CATEGORY, null,
            "0.0", null, null, "60.0", USER_FILE_DATA_ROLES);

    /**
     * DAY_OF_YEAR, along with YEAR, and possibly SECOND_OF_DAY,
     * may be used to specify the date and time of the measurement.
     */
    public static final DoubleDashDataType DAY_OF_YEAR = new DoubleDashDataType(DashboardUtils.DAY_OF_YEAR,
            "day_of_year", TIME_CATEGORY, null,
            "1.0", null, null, "367.0", USER_ONLY_ROLES);

    /**
     * SECOND_OF_DAY, along with YEAR and DAY_OF_YEAR may be used to specify date and time of the measurement
     */
    public static final DoubleDashDataType SECOND_OF_DAY = new DoubleDashDataType(DashboardUtils.SECOND_OF_DAY,
            "second_of_day", TIME_CATEGORY, null,
            "0.0", null, null, "86400.0", USER_ONLY_ROLES);

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
            new HashSet<String>(Arrays.asList("067F", "08FS", "09FS", "147F", "187F", "18FX", "247F", "24FS",
                    "267F", "26FS", "297F", "3119", "3164", "317F", "32FS", "33GO", "33TT", "357F", "48MB",
                    "497F", "49FS", "747F", "74FS", "767F", "77FS", "907F", "91FS", "GH7F"));

    /**
     * NODC codes (all upper-case) for Drifting Buoys
     */
    private static final HashSet<String> DRIFTING_BUOY_NODC_CODES =
            new HashSet<String>(Arrays.asList("09DB", "18DZ", "35DR", "49DZ", "61DB", "74DZ", "91DB", "99DB"));

    /**
     * Guesses the platform type from the platform name or the expocode. If the platform name or NODC code from the
     * expocode is that of a mooring or drifting buoy, that type is returned; otherwise it is assumed to be a ship.
     *
     * @param expocode
     *         expocode of the dataset; cannot be null
     * @param platformName
     *         platform name for the dataset; cannot be null
     *
     * @return one of "Mooring", "Drifting Buoy", or "Ship"
     */
    public static String guessPlatformType(String expocode, String platformName) {
        if ( platformName.toUpperCase().contains("MOORING") )
            return "Mooring";
        if ( platformName.toUpperCase().contains("DRIFTING BUOY") )
            return "Drifting Buoy";
        if ( platformName.toUpperCase().contains("BUOY") )
            return "Mooring";

        String nodc = expocode.substring(0, 4).toUpperCase();
        if ( DashboardServerUtils.FIXED_PLATFORM_NODC_CODES.contains(nodc) )
            return "Mooring";
        if ( DashboardServerUtils.DRIFTING_BUOY_NODC_CODES.contains(nodc) )
            return "Drifting Buoy";
        return "Ship";
    }

    /**
     * Pattern for {@link #getDatasetIDFromName(String)}
     */
    private static final Pattern datasetIdStripPattern = Pattern.compile("[^\\p{javaUpperCase}\\p{Digit}-]+");

    /**
     * Returns the dataset ID for the given dataset name by converting characters in the name to uppercase and ignoring
     * anything that is not an uppercase letter, a digit, or a hyphen ('-').  The value returned is equivalent to
     * <pre>name.toUpperCase().replaceAll("[^\p{javaUpperCase}\p{Digit}-]+", "")</pre>
     *
     * @param name
     *         dataset name
     *
     * @return dataset ID for the given dataset name
     */
    public static String getDatasetIDFromName(String name) {
        return datasetIdStripPattern.matcher(name.toUpperCase()).replaceAll("");
    }

    /**
     * Pattern for {@link #getKeyForName(String)}
     */
    private static final Pattern keyStripPattern = Pattern.compile("[^\\p{javaUpperCase}\\p{Digit}]+");

    /**
     * Computes a key for the given name by converting characters in the name to uppercase, ignoring anything that is
     * not uppercase or a digit, then converting characters to lowercase.  (This eliminates the degree symbol, or
     * something similar, that is part of javaLowerCase.)  The value returned is equivalent to
     * <pre>name.toUpperCase().replaceAll("[^\p{javaUpperCase}\p{Digit}]+", "").toLowerCase()</pre>
     *
     * @param name
     *         name to use
     *
     * @return key for the given name
     */
    public static String getKeyForName(String name) {
        return keyStripPattern.matcher(name.toUpperCase()).replaceAll("").toLowerCase();
    }

    /**
     * Checks and standardized a given dataset ID.
     *
     * @param datasetID
     *         dataset ID to check
     *
     * @return standardized (uppercase) dataset ID
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is null, too short, too long, or contains invalid characters
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

    /**
     * "Cleans" a username for use by substituting characters that are problematic (such as space characters). Also
     * converts all alphabetic characters to lowercase. An empty string is returned if username is null.
     *
     * @param username
     *         username to clean
     *
     * @return clean version of username
     */
    public static String cleanUsername(String username) {
        if ( username == null )
            return "";
        return username.replace(' ', '_').toLowerCase();
    }

    /**
     * Encodes a set of QCFlag objects suitable for decoding with {@link #decodeQCFlagSet(String)}.
     *
     * @param qcSet
     *         set of QCFlag values to encode
     *
     * @return the encoded list of QCFlag values
     */
    public static String encodeQCFlagSet(TreeSet<QCFlag> qcSet) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        boolean firstValue = true;
        for (QCFlag flag : qcSet) {
            if ( firstValue )
                firstValue = false;
            else
                sb.append(", ");
            sb.append("[ ");
            sb.append(flag.getRowIndex().toString());
            sb.append(", ");
            sb.append(flag.getColumnIndex().toString());
            sb.append(", \"");
            sb.append(flag.getSeverity().toString());
            sb.append("\", \"");
            sb.append(flag.getFlagValue());
            sb.append("\", \"");
            sb.append(flag.getFlagName());
            sb.append("\" ]");
        }
        sb.append(" ]");
        return sb.toString();
    }

    /**
     * Decodes an encoded QCFlag set produced by {@link #encodeQCFlagSet(java.util.TreeSet)}, into a TreeSet of
     * QCFlags.
     *
     * @param qcFlagSetStr
     *         the encoded set of QCFlag objects
     *
     * @return the decoded TreeSet ofQCFlag objects; never null, but may be empty (if the encoded set does not specify
     *         any QCFlag objects)
     *
     * @throws IllegalArgumentException
     *         if qcFlagSetStr does not start with '[', does not end with ']', or contains an invalid encoded QCFlag.
     */
    public static TreeSet<QCFlag> decodeQCFlagSet(String qcFlagSetStr) {
        if ( !(qcFlagSetStr.startsWith("[") && qcFlagSetStr.endsWith("]")) )
            throw new IllegalArgumentException("Encoded QCFlag set not enclosed in brackets");
        String contents = qcFlagSetStr.substring(1, qcFlagSetStr.length() - 1);
        if ( contents.trim().isEmpty() )
            return new TreeSet<QCFlag>();
        int firstIndex = contents.indexOf("[");
        int lastIndex = contents.lastIndexOf("]");
        if ( (firstIndex < 0) || (lastIndex < 0) ||
                (!contents.substring(0, firstIndex).trim().isEmpty()) ||
                (!contents.substring(lastIndex + 1).trim().isEmpty()) )
            throw new IllegalArgumentException("A QCFlag encoding is not enclosed in brackets");
        String[] pieces = contents.substring(firstIndex + 1, lastIndex)
                                  .split("\\]\\s*,\\s*\\[", -1);
        TreeSet<QCFlag> flagSet = new TreeSet<QCFlag>();
        for (String encFlag : pieces) {
            String[] flagParts = encFlag.split(",", 5);
            try {
                if ( flagParts.length != 5 )
                    throw new IllegalArgumentException("incomplete QCFlag description");

                Integer rowIndex = Integer.parseInt(flagParts[0].trim());

                Integer colIndex = Integer.parseInt(flagParts[1].trim());

                firstIndex = flagParts[2].indexOf("\"");
                lastIndex = flagParts[2].lastIndexOf("\"");
                if ( (firstIndex < 1) || (lastIndex == firstIndex) ||
                        (!flagParts[2].substring(0, firstIndex).trim().isEmpty()) ||
                        (!flagParts[2].substring(lastIndex + 1).trim().isEmpty()) )
                    throw new IllegalArgumentException("severity not enclosed in double quotes");
                Severity severity = Severity.valueOf(flagParts[2].substring(firstIndex + 1, lastIndex));

                firstIndex = flagParts[3].indexOf("\"");
                lastIndex = flagParts[3].lastIndexOf("\"");
                if ( (firstIndex < 1) || (lastIndex == firstIndex) ||
                        (!flagParts[3].substring(0, firstIndex).trim().isEmpty()) ||
                        (!flagParts[3].substring(lastIndex + 1).trim().isEmpty()) )
                    throw new IllegalArgumentException("flag value not enclosed in double quotes");
                String flagValue = flagParts[3].substring(firstIndex + 1, lastIndex);

                firstIndex = flagParts[4].indexOf("\"");
                lastIndex = flagParts[4].lastIndexOf("\"");
                if ( (firstIndex < 1) || (lastIndex == firstIndex) ||
                        (!flagParts[4].substring(0, firstIndex).trim().isEmpty()) ||
                        (!flagParts[4].substring(lastIndex + 1).trim().isEmpty()) )
                    throw new IllegalArgumentException("flag name not enclosed in double quotes");
                String flagName = flagParts[4].substring(firstIndex + 1, lastIndex);

                flagSet.add(new QCFlag(flagName, flagValue, severity, colIndex, rowIndex));
            } catch ( Exception ex ) {
                throw new IllegalArgumentException("Invalid encoding of a set of QCFlag objects: " +
                        ex.getMessage(), ex);
            }
        }
        return flagSet;
    }

    /**
     * Returns the minimum and maximum valid values from the given data array. Missing values (those very close to
     * {@link DashboardUtils#FP_MISSING_VALUE}) are ignored.
     *
     * @param data
     *         find the minimum and maximum valid values of this data
     *
     * @return (minVal, maxVal) where minVal is the minimum, maxVal is the maximum, or
     *         ({@link DashboardUtils#FP_MISSING_VALUE}, {@link DashboardUtils#FP_MISSING_VALUE}) if all data is
     *         missing.
     */
    public static double[] getMinMaxValidData(double[] data) {
        double maxVal = DashboardUtils.FP_MISSING_VALUE;
        double minVal = DashboardUtils.FP_MISSING_VALUE;
        for (double val : data) {
            if ( DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, val, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                continue;
            if ( (maxVal == DashboardUtils.FP_MISSING_VALUE) || (minVal == DashboardUtils.FP_MISSING_VALUE) ) {
                maxVal = val;
                minVal = val;
            }
            else if ( maxVal < val ) {
                maxVal = val;
            }
            else if ( minVal > val ) {
                minVal = val;
            }
        }
        return new double[] { minVal, maxVal };
    }

    /**
     * Returns the approximate distance between two locations.  Uses the haversine formula, and
     * {@link #EARTH_AUTHALIC_RADIUS} for the radius of a spherical Earth, to compute the great
     * circle distance between the two locations.
     *
     * @param lon
     *         longitude, in decimal degrees east, of the first data location
     * @param lat
     *         latitude, in decimal degrees north, of the first data location
     * @param otherLon
     *         longitude, in decimal degrees east, of the other data location
     * @param otherLat
     *         latitude, in decimal degrees north, of the other data location
     *
     * @return the approximate distance, in kilometers, between the two locations
     */
    public static double distanceBetween(double lon, double lat, double otherLon, double otherLat) {
        // Convert longitude and latitude degrees to radians
        double lat1 = lat * Math.PI / 180.0;
        double lat2 = otherLat * Math.PI / 180.0;
        double lon1 = lon * Math.PI / 180.0;
        double lon2 = otherLon * Math.PI / 180.0;
        /*
         * Use the haversine formula to compute the great circle distance,
         * in radians, between the two (longitude, latitude) points.
         */
        double dellat = Math.sin(0.5 * (lat2 - lat1));
        dellat *= dellat;
        double dellon = Math.sin(0.5 * (lon2 - lon1));
        dellon *= dellon * Math.cos(lat1) * Math.cos(lat2);
        double distance = 2.0 * Math.asin(Math.sqrt(dellon + dellat));
        // Convert the great circle distance from radians to kilometers
        distance *= EARTH_AUTHALIC_RADIUS;

        return distance;
    }

    /**
     * Returns the location-time "distance" between two location-time points.  Uses {@link #SEAWATER_SPEED} for
     * converting differences in time into a "distance".  Uses {@link #distanceBetween(double, double, double, double)},
     * which uses the haversine formula and {@link #EARTH_AUTHALIC_RADIUS} for the radius of a spherical Earth,
     * to compute the great circle distance from the longitudes and latitudes.
     *
     * @param lon
     *         longitude, in decimal degrees east, of the first data location
     * @param lat
     *         latitude, in decimal degrees north, of the first data location
     * @param time
     *         time, in seconds since Jan 1, 1970 00:00:00, of the first data location
     * @param otherLon
     *         longitude, in decimal degrees east, of the other data location
     * @param otherLat
     *         latitude, in decimal degrees north, of the other data location
     * @param otherTime
     *         time, in seconds since Jan 1, 1970 00:00:00, of the other data location
     *
     * @return the "distance", in kilometers, between the two location-time points
     */
    public static double distanceBetween(double lon, double lat, double time,
            double otherLon, double otherLat, double otherTime) {
        // Get the surface distance in kilometers
        double distance = distanceBetween(lon, lat, otherLon, otherLat);
        // Get the time difference in days (24 hours)
        double deltime = (otherTime - time) / (24.0 * 60.0 * 60.0);
        // Convert to the time difference to kilometers
        deltime *= SEAWATER_SPEED;
        // Combine the time distance with the surface distance
        distance = Math.sqrt(distance * distance + deltime * deltime);

        return distance;
    }

}
