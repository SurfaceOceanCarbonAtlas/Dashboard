/**
 *
 */
package gov.noaa.pmel.dashboard.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Static dashboard utility functions and constants for use on both the client and server side.
 *
 * @author Karl Smith
 */
public class DashboardUtils {

    // Cruise upload action strings
    public static final String PREVIEW_REQUEST_TAG = "PREVIEW REQUEST";
    public static final String NEW_DATASETS_REQUEST_TAG = "NEW DATASETS REQUEST";
    public static final String OVERWRITE_DATASETS_REQUEST_TAG = "OVERWRITE DATASETS REQUEST";

    // Recognized data formats
    public static final String COMMA_FORMAT_TAG = "COMMA-SEPARATED VALUES";
    public static final String SEMICOLON_FORMAT_TAG = "SEMICOLON-SEPARATED VALUES";
    public static final String TAB_FORMAT_TAG = "TAB-SEPARATED VALUES";

    // Cruise upload result strings
    public static final String FILE_PREVIEW_HEADER_TAG = "FILE PREVIEW HEADER TAG";
    public static final String INVALID_FILE_HEADER_TAG = "INVALID FILE HEADER TAG";
    public static final String DATASET_EXISTS_HEADER_TAG = "DATASET EXISTS HEADER TAG";
    public static final String DATASET_DOES_NOT_EXIST_HEADER_TAG = "DATASET DOES NOT EXIST HEADER TAG";
    public static final String NO_DATASET_ID_HEADER_TAG = "NO DATASET ID HEADER TAG";
    public static final String NO_PI_NAMES_HEADER_TAG = "NO PI NAMES HEADER TAG";
    public static final String NO_PLATFORM_NAME_HEADER_TAG = "NO PLATFORM NAME HEADER TAG";
    public static final String UNEXPECTED_FAILURE_HEADER_TAG = "UNEXPECTED FAILURE HEADER TAG";
    public static final String END_OF_ERROR_MESSAGE_TAG = "END_OF_ERROR MESSAGE_TAG";
    public static final String SUCCESS_HEADER_TAG = "SUCCESS HEADER TAG";

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

    // Status strings - datasets that can be modified
    public static final String STATUS_NOT_SUBMITTED = "";
    public static final String STATUS_SUSPENDED = "Suspended";
    public static final String STATUS_EXCLUDED = "Excluded";
    // Status strings - datasets that cannot be modified
    public static final String STATUS_SUBMITTED = "Submitted";
    public static final String STATUS_ACCEPTED = "Accepted";
    public static final String STATUS_CONFLICT = "Conflict";
    public static final String STATUS_RENAMED = "Renamed";

    // Archival options
    public static final String ARCHIVE_STATUS_NOT_SUBMITTED = "";
    public static final String ARCHIVE_STATUS_WITH_NEXT_RELEASE = "With next release";
    public static final String ARCHIVE_STATUS_SENT_FOR_ARCHIVAL = "Sent for archival";
    public static final String ARCHIVE_STATUS_OWNER_TO_ARCHIVE = "Owner to archive";
    public static final String ARCHIVE_STATUS_ARCHIVED = "Archived";

    /**
     * Missing value for floating-point variables - not null or NaN
     */
    public static final Double FP_MISSING_VALUE = -1.0E+34;

    /**
     * Missing value for integer variables - not null
     */
    public static final Integer INT_MISSING_VALUE = -99;

    /**
     * Missing value for String variables - not null
     */
    public static final String STRING_MISSING_VALUE = "";

    /**
     * Date used as a missing value - not null; corresponds to Jan 2, 1800 00:00:00 UTC
     */
    public static final Date DATE_MISSING_VALUE = new Date(-5364576000000L);

    /**
     * Maximum relative error between two floating point values still considered the same value for practical purposes.
     * Typically used for rtol in {@link #closeTo(Double, Double, double, double)}
     */
    public static final double MAX_RELATIVE_ERROR = 1.0E-6;

    /**
     * Maximum absolute error between two floating point values still considered the same value for practical purposes.
     * Typically used for atol in {@link #closeTo(Double, Double, double, double)}
     */
    public static final double MAX_ABSOLUTE_ERROR = 1.0E-6;

    /**
     * The "upload filename" for all OME metadata files.
     */
    public static final String OME_FILENAME = "OME.xml";

    /**
     * THe PDF version of the OME XML files.
     */
    public static final String OME_PDF_FILENAME = "OME.pdf";

    /**
     * The "upload filename" for all PI-provided OME metadata files that are not used for anything other than generating
     * a supplemental document.
     * <p>
     * The use of this name is just a temporary measure until the CDIAC OME brought into the dashboard.
     */
    public static final String PI_OME_FILENAME = "PI_OME.xml";

    /**
     * The PDF version of the PI OME XML file.
     */
    public static final String PI_OME_PDF_FILENAME = "PI_OME.pdf";

    /**
     * Global region ID; the default region ID for QC events.
     */
    public static final String REGION_ID_GLOBAL = "G";


    /**
     * For data without any specific units
     */
    public static final ArrayList<String> NO_UNITS =
            new ArrayList<String>(Arrays.asList(""));

    /**
     * Formats for date-time stamps
     */
    public static final ArrayList<String> TIMESTAMP_UNITS =
            new ArrayList<String>(Arrays.asList(
                    "yyyy-mm-dd hh:mm:ss",
                    "mm-dd-yyyy hh:mm:ss",
                    "dd-mm-yyyy hh:mm:ss",
                    "mm-dd-yy hh:mm:ss",
                    "dd-mm-yy hh:mm:ss"));

    /**
     * Formats for dates
     */
    public static final ArrayList<String> DATE_UNITS =
            new ArrayList<String>(Arrays.asList(
                    "yyyy-mm-dd",
                    "mm-dd-yyyy",
                    "dd-mm-yyyy",
                    "mm-dd-yy",
                    "dd-mm-yy"));

    /**
     * Formats for time-of-day
     */
    public static final ArrayList<String> TIME_OF_DAY_UNITS =
            new ArrayList<String>(Arrays.asList("hh:mm:ss"));

    /**
     * Units for day-of-year (value of the first day of the year)
     */
    public static final ArrayList<String> DAY_OF_YEAR_UNITS =
            new ArrayList<String>(Arrays.asList("Jan1=1.0", "Jan1=0.0"));

    /**
     * Units for longitude
     */
    public static final ArrayList<String> LONGITUDE_UNITS =
            new ArrayList<String>(Arrays.asList(
                    "deg E",
                    "deg min E",
                    "deg min sec E",
                    "DDD.MMSSss E",
                    "deg W",
                    "deg min W",
                    "deg min sec W",
                    "DDD.MMSSss W"
            ));

    /**
     * Units of latitude
     */
    public static final ArrayList<String> LATITUDE_UNITS =
            new ArrayList<String>(Arrays.asList(
                    "deg N",
                    "deg min N",
                    "deg min sec N",
                    "DD.MMSSss N",
                    "deg S",
                    "deg min S",
                    "deg min sec S",
                    "DD.MMSSss S"
            ));

    /**
     * Unit of depth
     */
    public static final ArrayList<String> DEPTH_UNITS =
            new ArrayList<String>(Arrays.asList("meters"));


    /**
     * UNKNOWN needs to be respecified as one of the (other) data column types.
     */
    public static final DataColumnType UNKNOWN = new DataColumnType("unknown",
            0.0, "(unknown)", "unknown type of data",
            false, NO_UNITS);

    /**
     * OTHER is for supplementary data in the user's original data file but otherwise not used.  A description of each
     * column with this type must be part of the metadata, but the values are not validated or used. Multiple columns
     * may have this type.
     */
    public static final DataColumnType OTHER = new DataColumnType("other",
            1.0, "other", "unchecked supplementary data",
            false, NO_UNITS);

    /**
     * Unique identifier for the dataset
     * <p>
     * For SOCAT, the dataset ID is NODCYYYYMMDD (the expocode) where NODC is the ship code and YYYY-MM-DD
     * is the start date for the cruise; possibly followed by -1 or -2 for non-ship platforms, such as
     * moorings, where NODC does not distinguish different platform names.
     */
    public static final DataColumnType DATASET_ID = new DataColumnType("expocode",
            50.0, "expocode", "unique identifier of a dataset",
            false, DashboardUtils.NO_UNITS);

    public static final DataColumnType LONGITUDE = new DataColumnType("longitude",
            301.0, "longitude", "sample longitude",
            true, LONGITUDE_UNITS);

    public static final DataColumnType LATITUDE = new DataColumnType("latitude",
            302.0, "latitude", "sample latitude",
            true, LATITUDE_UNITS);

    public static final DataColumnType SAMPLE_DEPTH = new DataColumnType("sample_depth",
            303.0, "sample depth", "sample depth",
            true, DEPTH_UNITS);

    /**
     * Date and time of the measurement
     */
    public static final DataColumnType TIMESTAMP = new DataColumnType("date_time",
            310.0, "date time", "sample date and time",
            false, TIMESTAMP_UNITS);

    /**
     * Date of the measurement - no time.
     */
    public static final DataColumnType DATE = new DataColumnType("date",
            311.0, "date", "sample date",
            false, DATE_UNITS);

    public static final DataColumnType YEAR = new DataColumnType("year",
            312.0, "year", "sample year",
            false, NO_UNITS);

    public static final DataColumnType MONTH_OF_YEAR = new DataColumnType("month",
            313.0, "month of year", "sample month of year",
            false, NO_UNITS);

    public static final DataColumnType DAY_OF_MONTH = new DataColumnType("day",
            314.0, "day of month", "sample day of month",
            false, NO_UNITS);

    public static final DataColumnType TIME_OF_DAY = new DataColumnType("time_of_day",
            315.0, "time of day", "sample time of day",
            false, TIME_OF_DAY_UNITS);

    public static final DataColumnType HOUR_OF_DAY = new DataColumnType("hour",
            316.0, "hour of day", "sample hour of day",
            false, NO_UNITS);

    public static final DataColumnType MINUTE_OF_HOUR = new DataColumnType("minute",
            317.0, "minute of hour", "sample minute of hour",
            false, NO_UNITS);

    public static final DataColumnType SECOND_OF_MINUTE = new DataColumnType("second",
            318.0, "sec of minute", "sample second of minute",
            false, NO_UNITS);

    /**
     * DAY_OF_YEAR, along with YEAR, and possibly SECOND_OF_DAY, may be used to specify the date and time of the
     * measurement.
     */
    public static final DataColumnType DAY_OF_YEAR = new DataColumnType("day_of_year",
            320.0, "day of year", "sample day of year",
            false, DAY_OF_YEAR_UNITS);

    /**
     * SECOND_OF_DAY, along with YEAR and DAY_OF_YEAR may be used to specify date and time of the measurement
     */
    public static final DataColumnType SECOND_OF_DAY = new DataColumnType("sec_of_day",
            321.0, "sec of day", "sample second of day",
            false, NO_UNITS);

    /**
     * Encodes an ArrayList of Strings suitable for decoding using {@link #decodeStringArrayList(String)}.
     * Characters within the strings are copied as-is, thus newline characters, or the character sequence
     * double quote - comma - double quote, within a string will likely cause problems when reading or
     * decoding the encoded string.
     * <p>
     * ArrayList is used instead of Collection to simplify the JavaScript GWT creates.
     *
     * @param strList
     *         the ArrayList of strings to encode
     *
     * @return the encoded string array
     */
    public static String encodeStringArrayList(ArrayList<String> strList) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        boolean firstValue = true;
        for (String strVal : strList) {
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
     * Decodes an string produced by {@link #encodeStringArrayList(ArrayList)}, into an ArrayList of strings. Each
     * string must be enclosed in double quotes; escaped characters within a string are not recognized or modified.
     * Strings must be separated by commas.  Whitespace around the comma is allowed.
     *
     * @param arrayStr
     *         the encoded string array
     *
     * @return the decoded ArrayList of strings; never null, but may be empty (if the encoded string array contains no
     *         strings)
     *
     * @throws IllegalArgumentException
     *         if arrayStr does not start with '[', does not end with ']', or contains strings not enclosed within
     *         double quotes.
     */
    public static ArrayList<String> decodeStringArrayList(String arrayStr) throws IllegalArgumentException {
        if ( !(arrayStr.startsWith("[") && arrayStr.endsWith("]")) )
            throw new IllegalArgumentException("Encoded string array not enclosed in brackets");
        String contents = arrayStr.substring(1, arrayStr.length() - 1);
        if ( contents.trim().isEmpty() )
            return new ArrayList<String>(0);
        int firstIndex = contents.indexOf("\"");
        int lastIndex = contents.lastIndexOf("\"");
        if ( (firstIndex < 0) || (lastIndex == firstIndex) ||
                (!contents.substring(0, firstIndex).trim().isEmpty()) ||
                (!contents.substring(lastIndex + 1).trim().isEmpty()) )
            throw new IllegalArgumentException("Strings in encoded string array are not enclosed in double quotes");
        String[] pieces = contents.substring(firstIndex + 1, lastIndex)
                                  .split("\"\\s*,\\s*\"", -1);
        return new ArrayList<String>(Arrays.asList(pieces));
    }

    /**
     * Encodes an TreeSet of Strings suitable for decoding using {@link #decodeStringTreeSet(String)}.  Characters
     * within the strings are copied as-is, thus newline characters, or the character sequence double quote - comma -
     * double quote, within a string will likely cause problems when reading or decoding the encoded string.
     * <p>
     * TreeSet is used instead of Collection to simplify the JavaScript GWT creates.
     *
     * @param strSet
     *         the TreeSet of strings to encode
     *
     * @return the encoded string array
     */
    public static String encodeStringTreeSet(TreeSet<String> strSet) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        boolean firstValue = true;
        for (String strVal : strSet) {
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
     * Decodes a string produced by {@link #encodeStringTreeSet(TreeSet)}, into an TreeSet of strings. Each string must
     * be enclosed in double quotes; escaped characters within a string are not recognized or modified.  Strings must be
     * separated by commas.  Whitespace around the comma is allowed.
     *
     * @param setStr
     *         the encoded string set
     *
     * @return the decoded TreeSet of strings; never null, but may be empty (if the encoded string tree set contains no
     *         strings)
     *
     * @throws IllegalArgumentException
     *         if arrayStr does not start with '[', does not end with ']', or contains strings not enclosed within
     *         double quotes.
     */
    public static TreeSet<String> decodeStringTreeSet(String setStr) throws IllegalArgumentException {
        if ( !(setStr.startsWith("[") && setStr.endsWith("]")) )
            throw new IllegalArgumentException("Encoded string set not enclosed in brackets");
        String contents = setStr.substring(1, setStr.length() - 1);
        if ( contents.trim().isEmpty() )
            return new TreeSet<String>();
        int firstIndex = contents.indexOf("\"");
        int lastIndex = contents.lastIndexOf("\"");
        if ( (firstIndex < 0) || (lastIndex == firstIndex) ||
                (!contents.substring(0, firstIndex).trim().isEmpty()) ||
                (!contents.substring(lastIndex + 1).trim().isEmpty()) )
            throw new IllegalArgumentException("Strings in encoded string set are not enclosed in double quotes");
        String[] pieces = contents.substring(firstIndex + 1, lastIndex)
                                  .split("\"\\s*,\\s*\"", -1);
        return new TreeSet<String>(Arrays.asList(pieces));
    }

    /**
     * Returns the basename of a filename.  Does this by returning only the portion of the string after the last slash
     * or backslash character (either one if both present).
     * <p>
     * If null is given, or if the name ends in a slash or backslash, an empty string is returned.  Whitespace is
     * trimmed from the returned name.
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
     * Determines if two Doubles are close to the same value. The absolute of the average value, absAver, and the
     * absolute value in the difference in values, absDiff, of first and second are determined.
     * <p>
     * The difference between is considered negligible if: absDiff < absAver * rtol + atol
     *
     * @param first
     *         value to compare
     * @param second
     *         value to compare
     * @param rtol
     *         relative tolerance of the difference
     * @param atol
     *         absolute tolerance of the difference
     *
     * @return true is first and second are both NaN, both Infinite (regardless of whether positive or negative), or
     *         have values whose difference is "negligible".
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
        return (absDiff < absAver * rtol + atol);
    }

    /**
     * Determines if two longitudes are close to the same value modulo 360.0.  The absolute of the average value,
     * absAver, and the absolute value in the difference in values, absDiff, of first and second are determined.
     * <p>
     * The difference between is considered negligible if: absDiff < absAver * rtol + atol
     * <p>
     * This comparison is made to the values as given as well as for each value with 360.0 added to it. (So not a
     * complete modulo 360 check.)
     *
     * @param first
     *         value to compare
     * @param second
     *         value to compare
     * @param rtol
     *         relative tolerance of the difference
     * @param atol
     *         absolute tolerance of the difference
     *
     * @return true is first and second are both NaN, both Infinite (regardless of whether positive or negative), or
     *         have values whose difference is "negligible".
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
     * Compare using the "selected" properties of the metadata documents. Note that this is inconsistent with
     * DashboardMetadata.equals in that this is only examining one field of DashboardMetadata.
     */
    public static Comparator<DashboardMetadata> metaSelectedComparator = new Comparator<DashboardMetadata>() {
        @Override
        public int compare(DashboardMetadata m1, DashboardMetadata m2) {
            if ( m1 == m2 )
                return 0;
            if ( m1 == null )
                return -1;
            if ( m2 == null )
                return 1;
            Boolean s1 = m1.isSelected();
            return s1.compareTo(m2.isSelected());
        }
    };

    /**
     * Compare using the dataset IDs associated with the metadata documents. Note that this is inconsistent with
     * DashboardMetadata.equals in that this is only examining one field of DashboardMetadata.
     */
    public static Comparator<DashboardMetadata> metaDatasetIdComparator = new Comparator<DashboardMetadata>() {
        @Override
        public int compare(DashboardMetadata m1, DashboardMetadata m2) {
            if ( m1 == m2 )
                return 0;
            if ( m1 == null )
                return -1;
            if ( m2 == null )
                return 1;
            return m1.getDatasetId().compareTo(m2.getDatasetId());
        }
    };

    /**
     * Compare using the filenames of the metadata documents. Note that this is inconsistent with
     * DashboardMetadata.equals in that this is only examining one field of DashboardMetadata.
     */
    public static Comparator<DashboardMetadata> metaFilenameComparator = new Comparator<DashboardMetadata>() {
        @Override
        public int compare(DashboardMetadata m1, DashboardMetadata m2) {
            if ( m1 == m2 )
                return 0;
            if ( m1 == null )
                return -1;
            if ( m2 == null )
                return 1;
            return m1.getFilename().compareTo(m2.getFilename());
        }
    };

    /**
     * Compare using the upload timestamp strings of the metadata documents. Note that this is inconsistent with
     * DashboardMetadata.equals in that this is only examining one field of DashboardMetadata.
     */
    public static Comparator<DashboardMetadata> metaTimestampComparator = new Comparator<DashboardMetadata>() {
        @Override
        public int compare(DashboardMetadata m1, DashboardMetadata m2) {
            if ( m1 == m2 )
                return 0;
            if ( m1 == null )
                return -1;
            if ( m2 == null )
                return 1;
            return m1.getUploadTimestamp().compareTo(m2.getUploadTimestamp());
        }
    };

    /**
     * Compare using the owners of the metadata documents. Note that this is inconsistent with DashboardMetadata.equals
     * in that this is only examining one field of DashboardMetadata.
     */
    public static Comparator<DashboardMetadata> metaOwnerComparator = new Comparator<DashboardMetadata>() {
        @Override
        public int compare(DashboardMetadata m1, DashboardMetadata m2) {
            if ( m1 == m2 )
                return 0;
            if ( m1 == null )
                return -1;
            if ( m2 == null )
                return 1;
            return m1.getOwner().compareTo(m2.getOwner());
        }
    };

    /**
     * Compare using the "selected" properties of the datasets. Note that this is inconsistent with {@link
     * DashboardDataset#equals(Object)} in that this is only examining one field of DashboardDataset.
     */
    public static Comparator<DashboardDataset> dataSelectedComparator = new Comparator<DashboardDataset>() {
        @Override
        public int compare(DashboardDataset d1, DashboardDataset d2) {
            if ( d1 == d2 )
                return 0;
            if ( d1 == null )
                return -1;
            if ( d2 == null )
                return 1;
            Boolean s1 = d1.isSelected();
            return s1.compareTo(d2.isSelected());
        }
    };

    /**
     * Compare using the IDs of the datasets. Note that this is inconsistent with {@link
     * DashboardDataset#equals(Object)} in that this is only examining one field of DashboardDataset.
     */
    public static Comparator<DashboardDataset> dataDatasetIdComparator = new Comparator<DashboardDataset>() {
        @Override
        public int compare(DashboardDataset d1, DashboardDataset d2) {
            if ( d1 == d2 )
                return 0;
            if ( d1 == null )
                return -1;
            if ( d2 == null )
                return 1;
            return d1.getDatasetId().compareTo(d2.getDatasetId());
        }
    };

    /**
     * Compare using the upload filenames of the datasets. Note that this is inconsistent with {@link
     * DashboardDataset#equals(Object)} in that this is only examining one field of DashboardDataset.
     */
    public static Comparator<DashboardDataset> dataFilenameComparator = new Comparator<DashboardDataset>() {
        @Override
        public int compare(DashboardDataset d1, DashboardDataset d2) {
            if ( d1 == d2 )
                return 0;
            if ( d1 == null )
                return -1;
            if ( d2 == null )
                return 1;
            return d1.getUploadFilename().compareTo(d2.getUploadFilename());
        }
    };

    /**
     * Compare using the upload timestamp strings of the datasets. Note that this is inconsistent with {@link
     * DashboardDataset#equals(Object)} in that this is only examining one field of DashboardDataset.
     */
    public static Comparator<DashboardDataset> dataTimestampComparator = new Comparator<DashboardDataset>() {
        @Override
        public int compare(DashboardDataset d1, DashboardDataset d2) {
            if ( d1 == d2 )
                return 0;
            if ( d1 == null )
                return -1;
            if ( d2 == null )
                return 1;
            return d1.getUploadTimestamp().compareTo(d2.getUploadTimestamp());
        }
    };

    /**
     * Compare using the owners of the datasets. Note that this is inconsistent with {@link
     * DashboardDataset#equals(Object)} in that this is only examining one field of DashboardDataset.
     */
    public static Comparator<DashboardDataset> dataOwnerComparator = new Comparator<DashboardDataset>() {
        @Override
        public int compare(DashboardDataset d1, DashboardDataset d2) {
            if ( d1 == d2 )
                return 0;
            if ( d1 == null )
                return -1;
            if ( d2 == null )
                return 1;
            return d1.getOwner().compareTo(d2.getOwner());
        }
    };

    /**
     * Compare using the data check status strings of the datasets. Note that this is inconsistent with {@link
     * DashboardDataset#equals(Object)} in that this is only examining one field of DashboardDataset.
     */
    public static Comparator<DashboardDataset> dataCheckComparator = new Comparator<DashboardDataset>() {
        @Override
        public int compare(DashboardDataset d1, DashboardDataset d2) {
            if ( d1 == d2 )
                return 0;
            if ( d1 == null )
                return -1;
            if ( d2 == null )
                return 1;
            return d1.getDataCheckStatus().compareTo(d2.getDataCheckStatus());
        }
    };

    /**
     * Compare using the OME metadata timestamp strings of the datasets. Note that this is inconsistent with {@link
     * DashboardDataset#equals(Object)} in that this is only examining one field of DashboardDataset.
     */
    public static Comparator<DashboardDataset> omeTimestampComparator = new Comparator<DashboardDataset>() {
        @Override
        public int compare(DashboardDataset d1, DashboardDataset d2) {
            if ( d1 == d2 )
                return 0;
            if ( d1 == null )
                return -1;
            if ( d2 == null )
                return 1;
            return d1.getOmeTimestamp().compareTo(d2.getOmeTimestamp());
        }
    };

    /**
     * Compare using the additional document "filename ; timestamp" strings of the datasets. Note that this is
     * inconsistent with {@link DashboardDataset#equals(Object)} in that this is only examining one field of
     * DashboardDataset.
     */
    public static Comparator<DashboardDataset> addlDocsComparator = new Comparator<DashboardDataset>() {
        @Override
        public int compare(DashboardDataset d1, DashboardDataset d2) {
            if ( d1 == d2 )
                return 0;
            if ( d1 == null )
                return -1;
            if ( d2 == null )
                return 1;
            Iterator<String> iter1 = d1.getAddlDocs().iterator();
            Iterator<String> iter2 = d2.getAddlDocs().iterator();
            while ( iter1.hasNext() && iter2.hasNext() ) {
                int result = iter1.next().compareTo(iter2.next());
                if ( result != 0 )
                    return result;
            }
            // The lists are the same up to the minimum number of strings given,
            // so the list with more items is larger; or they are equal if they
            // both have no more items
            if ( iter1.hasNext() )
                return 1;
            if ( iter2.hasNext() )
                return -1;
            return 0;
        }
    };

    /**
     * Compare using the version strings of the datasets. Note that this is inconsistent with {@link
     * DashboardDataset#equals(Object)} in that this is only examining one field of DashboardDataset.
     */
    public static Comparator<DashboardDataset> versionComparator = new Comparator<DashboardDataset>() {
        @Override
        public int compare(DashboardDataset d1, DashboardDataset d2) {
            if ( d1 == d2 )
                return 0;
            if ( d1 == null )
                return -1;
            if ( d2 == null )
                return 1;
            return d1.getVersion().compareTo(d2.getVersion());
        }
    };

    /**
     * Compare using the QC status strings of the datasets. Note that this is inconsistent with {@link
     * DashboardDataset#equals(Object)} in that this is only examining one field of DashboardDataset.
     */
    public static Comparator<DashboardDataset> submitStatusComparator = new Comparator<DashboardDataset>() {
        @Override
        public int compare(DashboardDataset d1, DashboardDataset d2) {
            if ( d1 == d2 )
                return 0;
            if ( d1 == null )
                return -1;
            if ( d2 == null )
                return 1;
            return d1.getSubmitStatus().compareTo(d2.getSubmitStatus());
        }
    };

    /**
     * Compare using the archive status strings of the datasets. Note that this is inconsistent with {@link
     * DashboardDataset#equals(Object)} in that this is only examining one field of DashboardDataset.
     */
    public static Comparator<DashboardDataset> archiveStatusComparator = new Comparator<DashboardDataset>() {
        @Override
        public int compare(DashboardDataset d1, DashboardDataset d2) {
            if ( d1 == d2 )
                return 0;
            if ( d1 == null )
                return -1;
            if ( d2 == null )
                return 1;
            return d1.getArchiveStatus().compareTo(d2.getArchiveStatus());
        }
    };


    /**
     * Compare using the severities of the messages. Note that this is inconsistent with
     * {@link ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage.
     */
    public static Comparator<ADCMessage> severityComparator = new Comparator<ADCMessage>() {
        @Override
        public int compare(ADCMessage msg1, ADCMessage msg2) {
            if ( msg1 == msg2 )
                return 0;
            if ( msg1 == null )
                return -1;
            if ( msg2 == null )
                return 1;
            return msg1.severity.compareTo(msg2.severity);
        }
    };

    /**
     * Compare using the row numbers of the messages. Note that this is inconsistent with
     * {@link ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage.
     */
    public static Comparator<ADCMessage> rowNumComparator = new Comparator<ADCMessage>() {
        @Override
        public int compare(ADCMessage msg1, ADCMessage msg2) {
            if ( msg1 == msg2 )
                return 0;
            if ( msg1 == null )
                return -1;
            if ( msg2 == null )
                return 1;
            return msg1.rowNumber.compareTo(msg2.rowNumber);
        }
    };

    /**
     * Compare using the column numbers of the messages. Note that this is inconsistent with
     * {@link ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage.
     */
    public static Comparator<ADCMessage> colNumComparator = new Comparator<ADCMessage>() {
        @Override
        public int compare(ADCMessage msg1, ADCMessage msg2) {
            if ( msg1 == msg2 )
                return 0;
            if ( msg1 == null )
                return -1;
            if ( msg2 == null )
                return 1;
            return msg1.colNumber.compareTo(msg2.colNumber);
        }
    };

    /**
     * Compare using the column names of the messages. Note that this is inconsistent with
     * {@link ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage.
     */
    public static Comparator<ADCMessage> colNameComparator = new Comparator<ADCMessage>() {
        @Override
        public int compare(ADCMessage msg1, ADCMessage msg2) {
            if ( msg1 == msg2 )
                return 0;
            if ( msg1 == null )
                return -1;
            if ( msg2 == null )
                return 1;
            return msg1.colName.compareTo(msg2.colName);
        }
    };

    /**
     * Compare using the detailed comments of the messages. Note that this is inconsistent with
     * {@link ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage.
     */
    public static Comparator<ADCMessage> explanationComparator = new Comparator<ADCMessage>() {
        @Override
        public int compare(ADCMessage msg1, ADCMessage msg2) {
            if ( msg1 == msg2 )
                return 0;
            if ( msg1 == null )
                return -1;
            if ( msg2 == null )
                return 1;
            return msg1.detailedComment.compareTo(msg2.detailedComment);
        }
    };

}
