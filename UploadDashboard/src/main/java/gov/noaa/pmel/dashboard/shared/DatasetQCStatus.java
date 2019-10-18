package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Represents the QC status for a dataset.  Traditionally these were the single letters (now
 * FLAG_... values).  This class provides more flexibility that is needed for automated QC
 * as well as the possibility of using values indicating the accuracy in the fCO2 values.
 */
public class DatasetQCStatus implements Comparable<DatasetQCStatus>, Serializable, IsSerializable {

    private static final long serialVersionUID = -5185613156609609049L;

    /**
     * Values for the DatasetQCStatus fields
     */
    public enum Status implements Serializable, IsSerializable {
        PRIVATE,
        SUSPENDED,
        EXCLUDED,
        NEW_AWAITING_QC,
        UPDATED_AWAITING_QC,
        CONFLICTED,
        ACCEPTED_A,
        ACCEPTED_B,
        ACCEPTED_C,
        ACCEPTED_D,
        ACCEPTED_E,
        COMMENT,
        RENAMED;

        /**
         * Returns the "key" for the given string.  The "key" is the string with all lowercase
         * characters converted to uppercase, and then only preserving alphanumeric characters;
         * i.e., repr.toUpperCase.replaceAll("[^A-Z0-9]", "")
         *
         * @param repr
         *         string to use; if null, null is returned
         *
         * @return key for the given string; can be null (if repr is null) or
         *         empty (if repr does not contain any alphanumeric characters)
         */
        static String keyString(String repr) {
            if ( repr == null )
                return null;
            return repr.toUpperCase().replaceAll("[^A-Z0-9]", "");
        }

        /**
         * Returns the status value represented in a variety of string forms: status
         * flag (one-character strings), status strings (human-friendly strings),
         * and {@link #toString()} strings.  String matching is case-insensitive and
         * ignores any characters that are not alphanumeric.
         *
         * @param repr
         *         string representation of the status; if null or does not contain
         *         any alphanumeric characters, {@link #PRIVATE} is returned
         *
         * @return status value of the string representations, or
         *         null if the string representation is not valid
         */
        public static Status fromString(String repr) {
            if ( repr == null )
                return PRIVATE;
            String key = keyString(repr);
            if ( key.isEmpty() )
                return PRIVATE;
            return KEYSTRING_STATUS_MAP.get(key);
        }

        /**
         * @param value
         *         Status value to check; can be null
         *
         * @return if the given Status value is one of the Accepted_* values
         */
        public static boolean isAcceptable(Status value) {
            return (ACCEPTED_A.equals(value) ||
                    ACCEPTED_B.equals(value) ||
                    ACCEPTED_C.equals(value) ||
                    ACCEPTED_D.equals(value) ||
                    ACCEPTED_E.equals(value));
        }
    }

    private static final String FLAG_SEPARATOR = "-";
    private static final String PIFLAG_PREFIX = "pi";
    private static final String AUTOFLAG_PREFIX = "auto";

    private static final String STATUS_STRING_SEPARATOR = "; ";
    private static final String PISTATUS_STRING_PREFIX = "PI suggested ";
    private static final String AUTOSTATUS_STRING_PREFIX = "automation suggested ";

    // Dataset status flags - datasets that can be modified
    private static final String FLAG_PRIVATE = "P";
    private static final String FLAG_SUSPENDED = "S";
    private static final String FLAG_EXCLUDED = "X";
    // Dataset status flags - datasets that cannot be modified
    public static final String FLAG_NEW_AWAITING_QC = "N";
    public static final String FLAG_UPDATED_AWAITING_QC = "U";
    private static final String FLAG_CONFLICTED = "Q";
    private static final String FLAG_ACCEPTED_A = "A";
    private static final String FLAG_ACCEPTED_B = "B";
    private static final String FLAG_ACCEPTED_C = "C";
    private static final String FLAG_ACCEPTED_D = "D";
    private static final String FLAG_ACCEPTED_E = "E";
    // Flag for "only a comment"
    public static final String FLAG_COMMENT = "H";
    // Flag recording the renaming of a dataset (to and from)
    public static final String FLAG_RENAMED = "R";

    // Dataset status strings - datasets that can be modified
    private static final String STATUS_STRING_PRIVATE = "Private";
    private static final String STATUS_STRING_SUSPENDED = "Suspended";
    private static final String STATUS_STRING_EXCLUDED = "Excluded";
    // Dataset status strings - datasets that cannot be modified
    private static final String STATUS_STRING_NEW_AWAITING_QC = "Submitted new";
    private static final String STATUS_STRING_UPDATED_AWAITING_QC = "Submitted update";
    private static final String STATUS_STRING_CONFLICTED = "Conflicted";
    private static final String STATUS_STRING_ACCEPTED_A = "Flag A";
    private static final String STATUS_STRING_ACCEPTED_B = "Flag B";
    private static final String STATUS_STRING_ACCEPTED_C = "Flag C";
    private static final String STATUS_STRING_ACCEPTED_D = "Flag D";
    private static final String STATUS_STRING_ACCEPTED_E = "Flag E";
    // Not actually used at this time, but for completeness....
    private static final String STATUS_STRING_COMMENT = "Comment";
    private static final String STATUS_STRING_RENAMED = "Renamed";

    /**
     * Status values that can be assigned to the suggested fields.
     */
    private static final HashSet<Status> SUGGESTED_STATUS_VALUES;

    /**
     * Map of Status values to status flags (one-character strings).
     */
    private static final HashMap<Status,String> STATUS_FLAG_MAP;

    /**
     * Map of Status values to status strings (human-readable strings).
     */
    private static final HashMap<Status,String> STATUS_STRING_MAP;

    /**
     * Map of key strings generated from status flags (one-character strings), status strings
     * (human-readable strings), and {@link Status#toString()} values to Status values.
     * The key strings are generated from these string using {@link Status#keyString(String)}.
     */
    private static final HashMap<String,Status> KEYSTRING_STATUS_MAP;

    static {
        SUGGESTED_STATUS_VALUES = new HashSet<Status>();
        SUGGESTED_STATUS_VALUES.add(Status.PRIVATE);
        SUGGESTED_STATUS_VALUES.add(Status.SUSPENDED);
        SUGGESTED_STATUS_VALUES.add(Status.EXCLUDED);
        SUGGESTED_STATUS_VALUES.add(Status.ACCEPTED_A);
        SUGGESTED_STATUS_VALUES.add(Status.ACCEPTED_B);
        SUGGESTED_STATUS_VALUES.add(Status.ACCEPTED_C);
        SUGGESTED_STATUS_VALUES.add(Status.ACCEPTED_D);
        SUGGESTED_STATUS_VALUES.add(Status.ACCEPTED_E);

        STATUS_FLAG_MAP = new HashMap<Status,String>();
        STATUS_FLAG_MAP.put(Status.PRIVATE, FLAG_PRIVATE);
        STATUS_FLAG_MAP.put(Status.SUSPENDED, FLAG_SUSPENDED);
        STATUS_FLAG_MAP.put(Status.EXCLUDED, FLAG_EXCLUDED);
        STATUS_FLAG_MAP.put(Status.NEW_AWAITING_QC, FLAG_NEW_AWAITING_QC);
        STATUS_FLAG_MAP.put(Status.UPDATED_AWAITING_QC, FLAG_UPDATED_AWAITING_QC);
        STATUS_FLAG_MAP.put(Status.ACCEPTED_A, FLAG_ACCEPTED_A);
        STATUS_FLAG_MAP.put(Status.ACCEPTED_B, FLAG_ACCEPTED_B);
        STATUS_FLAG_MAP.put(Status.ACCEPTED_C, FLAG_ACCEPTED_C);
        STATUS_FLAG_MAP.put(Status.ACCEPTED_D, FLAG_ACCEPTED_D);
        STATUS_FLAG_MAP.put(Status.ACCEPTED_E, FLAG_ACCEPTED_E);
        STATUS_FLAG_MAP.put(Status.CONFLICTED, FLAG_CONFLICTED);
        STATUS_FLAG_MAP.put(Status.RENAMED, FLAG_RENAMED);
        STATUS_FLAG_MAP.put(Status.COMMENT, FLAG_COMMENT);

        STATUS_STRING_MAP = new HashMap<Status,String>();
        STATUS_STRING_MAP.put(Status.PRIVATE, STATUS_STRING_PRIVATE);
        STATUS_STRING_MAP.put(Status.SUSPENDED, STATUS_STRING_SUSPENDED);
        STATUS_STRING_MAP.put(Status.EXCLUDED, STATUS_STRING_EXCLUDED);
        STATUS_STRING_MAP.put(Status.NEW_AWAITING_QC, STATUS_STRING_NEW_AWAITING_QC);
        STATUS_STRING_MAP.put(Status.UPDATED_AWAITING_QC, STATUS_STRING_UPDATED_AWAITING_QC);
        STATUS_STRING_MAP.put(Status.ACCEPTED_A, STATUS_STRING_ACCEPTED_A);
        STATUS_STRING_MAP.put(Status.ACCEPTED_B, STATUS_STRING_ACCEPTED_B);
        STATUS_STRING_MAP.put(Status.ACCEPTED_C, STATUS_STRING_ACCEPTED_C);
        STATUS_STRING_MAP.put(Status.ACCEPTED_D, STATUS_STRING_ACCEPTED_D);
        STATUS_STRING_MAP.put(Status.ACCEPTED_E, STATUS_STRING_ACCEPTED_E);
        STATUS_STRING_MAP.put(Status.CONFLICTED, STATUS_STRING_CONFLICTED);
        STATUS_STRING_MAP.put(Status.RENAMED, STATUS_STRING_RENAMED);
        STATUS_STRING_MAP.put(Status.COMMENT, STATUS_STRING_COMMENT);

        KEYSTRING_STATUS_MAP = new HashMap<String,Status>();
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_PRIVATE), Status.PRIVATE);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_SUSPENDED), Status.SUSPENDED);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_EXCLUDED), Status.EXCLUDED);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_NEW_AWAITING_QC), Status.NEW_AWAITING_QC);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_UPDATED_AWAITING_QC), Status.UPDATED_AWAITING_QC);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_ACCEPTED_A), Status.ACCEPTED_A);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_ACCEPTED_B), Status.ACCEPTED_B);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_ACCEPTED_C), Status.ACCEPTED_C);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_ACCEPTED_D), Status.ACCEPTED_D);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_ACCEPTED_E), Status.ACCEPTED_E);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_CONFLICTED), Status.CONFLICTED);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_RENAMED), Status.RENAMED);
        KEYSTRING_STATUS_MAP.put(Status.keyString(FLAG_COMMENT), Status.COMMENT);

        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_PRIVATE), Status.PRIVATE);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_SUSPENDED), Status.SUSPENDED);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_EXCLUDED), Status.EXCLUDED);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_NEW_AWAITING_QC), Status.NEW_AWAITING_QC);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_UPDATED_AWAITING_QC), Status.UPDATED_AWAITING_QC);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_ACCEPTED_A), Status.ACCEPTED_A);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_ACCEPTED_B), Status.ACCEPTED_B);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_ACCEPTED_C), Status.ACCEPTED_C);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_ACCEPTED_D), Status.ACCEPTED_D);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_ACCEPTED_E), Status.ACCEPTED_E);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_CONFLICTED), Status.CONFLICTED);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_RENAMED), Status.RENAMED);
        KEYSTRING_STATUS_MAP.put(Status.keyString(STATUS_STRING_COMMENT), Status.COMMENT);

        for (Status status : Status.values()) {
            KEYSTRING_STATUS_MAP.put(Status.keyString(status.toString()), status);
        }
    }

    private Status actual;
    private Status piSuggested;
    private Status autoSuggested;
    private ArrayList<String> comments;

    /**
     * Create with all QC status fields set to {@link Status#PRIVATE} and no comment
     */
    public DatasetQCStatus() {
        actual = Status.PRIVATE;
        piSuggested = Status.PRIVATE;
        autoSuggested = Status.PRIVATE;
        comments = new ArrayList<String>();
    }

    /**
     * Create with the given Status as the actual QC status field.
     * The given comment is assigned as the comment associated with this flag.
     * The PI-suggested and automation-suggested fields are set to {@link Status#PRIVATE}
     *
     * @param flag
     *         actual dataset QC status to assign;
     *         if null, {@link Status#PRIVATE} is assigned
     * @param comment
     *         comment associated with this flag; if null or empty, there will be no comments
     */
    public DatasetQCStatus(Status flag, String comment) {
        this();
        if ( flag != null )
            actual = flag;
        if ( comment != null ) {
            String trimmedComment = comment.trim();
            if ( !trimmedComment.isEmpty() )
                comments.add(trimmedComment);
        }
    }

    /**
     * Create a duplicate of this instance.
     * (Cloneable not supported by GWT.)
     *
     * @param qcFlag
     *         dataset QC status to duplicate;
     *         if null, all QC status fields are set to {@link Status#PRIVATE}
     */
    public DatasetQCStatus(DatasetQCStatus qcFlag) {
        this();
        if ( qcFlag != null ) {
            actual = qcFlag.actual;
            piSuggested = qcFlag.piSuggested;
            autoSuggested = qcFlag.autoSuggested;
            comments.clear();
            comments.addAll(qcFlag.comments);
        }
    }

    /**
     * @return the actual QC status; never null
     */
    public Status getActual() {
        return actual;
    }

    /**
     * @param flag
     *         the actual QC status to assign; if null, {@link Status#PRIVATE} is assigned
     */
    public void setActual(Status flag) {
        if ( flag == null )
            actual = Status.PRIVATE;
        else
            actual = flag;
    }

    /**
     * @return the PI-suggested QC status; never null
     */
    public Status getPiSuggested() {
        return piSuggested;
    }

    /**
     * @param flag
     *         the PI-suggested QC status to assign; if null, {@link Status#PRIVATE} is assigned
     */
    public void setPiSuggested(Status flag) {
        if ( flag == null )
            piSuggested = Status.PRIVATE;
        else
            piSuggested = flag;
    }

    /**
     * @return the automation-suggested QC status; never null
     */
    public Status getAutoSuggested() {
        return autoSuggested;
    }

    /**
     * @param flag
     *         the automation-suggested QC status to assign; if null, {@link Status#PRIVATE} is assigned
     */
    public void setAutoSuggested(Status flag) {
        if ( flag == null )
            autoSuggested = Status.PRIVATE;
        else
            autoSuggested = flag;
    }

    /**
     * @return a copy of the list of comments associated with this QC; never null but could be empty
     */
    public ArrayList<String> getComments() {
        return new ArrayList<String>(comments);
    }

    /**
     * @param comments
     *         the comments associated with this QC to assign;
     *         if null or empty, there will be no comments associated with this QC.
     *         Any null or blank comments will be ignored.
     */
    public void setComments(ArrayList<String> comments) {
        this.comments.clear();
        if ( comments != null ) {
            for (String comment : comments) {
                if ( comment != null ) {
                    comment = comment.trim();
                    if ( !comment.isEmpty() )
                        this.comments.add(comment);
                }
            }
        }
    }

    /**
     * @param comment
     *         comment to add to the end of the list of comments
     *
     * @throws IllegalArgumentException
     *         if comment is null or blank
     */
    public void addComment(String comment) throws IllegalArgumentException {
        if ( comment == null )
            throw new IllegalArgumentException("null comment given to addComment");
        String trimmedComment = comment.trim();
        if ( trimmedComment.isEmpty() )
            throw new IllegalArgumentException("blank comment given to addComment");
        comments.add(trimmedComment);
    }

    /**
     * @return the complete dataset QC status as a status flag (terse string); never null or empty.
     *         PI-suggested and automation-suggested substrings are added only if their values
     *         are not {@link Status#PRIVATE}.  Examples: <br />
     *         "N-piB" (submitted new with PI-suggested flag of B) <br />
     *         "U-auB" (submitted update with automation-suggested flag of B) <br />
     *         "P" (all QC status fields {@link Status#PRIVATE})
     *
     * @throws IllegalArgumentException
     *         if any QC status field is invalid
     */
    public String flagString() throws IllegalArgumentException {
        String valueString = STATUS_FLAG_MAP.get(actual);
        if ( valueString == null )
            throw new IllegalArgumentException("Unknown actual QC status " + actual);
        if ( !Status.PRIVATE.equals(piSuggested) ) {
            String piVal = STATUS_FLAG_MAP.get(piSuggested);
            if ( piVal == null )
                throw new IllegalArgumentException("Unknown PI-suggested QC status " + piSuggested);
            if ( !SUGGESTED_STATUS_VALUES.contains(piSuggested) )
                throw new IllegalArgumentException("Invalid PI-suggested QC status " + piSuggested);
            valueString += FLAG_SEPARATOR + PIFLAG_PREFIX + piVal;
        }
        if ( !Status.PRIVATE.equals(autoSuggested) ) {
            String autoVal = STATUS_FLAG_MAP.get(autoSuggested);
            if ( autoVal == null )
                throw new IllegalArgumentException("Unknown automation-suggested QC status " + autoSuggested);
            if ( !SUGGESTED_STATUS_VALUES.contains(autoSuggested) )
                throw new IllegalArgumentException("Invalid automation-suggested QC status " + autoSuggested);
            valueString += FLAG_SEPARATOR + AUTOFLAG_PREFIX + autoVal;
        }
        return valueString;
    }

    /**
     * @return the complete dataset QC status as a status string (human-friendly); never null or empty.
     *         PI-suggested and automation-suggested substrings are added only if their values
     *         are not {@link Status#PRIVATE}.  Examples: <br />
     *         "Submitted new; PI suggested Flag B" <br />
     *         "Submitted update; automation suggested Flag B" <br />
     *         "Private"
     *
     * @throws IllegalArgumentException
     *         if any QC status field is invalid
     */
    public String statusString() throws IllegalArgumentException {
        String valueString = STATUS_STRING_MAP.get(actual);
        if ( valueString == null )
            throw new IllegalArgumentException("Unknown QC status " + actual);
        if ( !Status.PRIVATE.equals(piSuggested) ) {
            String piVal = STATUS_STRING_MAP.get(piSuggested);
            if ( piVal == null )
                throw new IllegalArgumentException("Unknown PI-suggested QC status " + piSuggested);
            if ( !SUGGESTED_STATUS_VALUES.contains(piSuggested) )
                throw new IllegalArgumentException("Invalid PI-suggested QC status " + piSuggested);
            valueString += STATUS_STRING_SEPARATOR + PISTATUS_STRING_PREFIX + piVal;
        }
        if ( !Status.PRIVATE.equals(autoSuggested) ) {
            String autoVal = STATUS_STRING_MAP.get(autoSuggested);
            if ( autoVal == null )
                throw new IllegalArgumentException("Unknown automation-suggested QC status " + autoSuggested);
            if ( !SUGGESTED_STATUS_VALUES.contains(autoSuggested) )
                throw new IllegalArgumentException("Invalid automation-suggested QC status " + autoSuggested);
            valueString += STATUS_STRING_SEPARATOR + AUTOSTATUS_STRING_PREFIX + autoVal;
        }
        return valueString;
    }

    /**
     * Return a new DatasetQCStatus represented by the given status flag or status string
     *
     * @param reprString
     *         status flag or status string representing a DatasetQCStatus;
     *         if null or empty, a DatasetQCStatus with all fields set to {@link Status#PRIVATE} is returned.
     *
     * @return new DatasetQCStatus represented by the given status flag or status string
     *
     * @throws IllegalArgumentException
     *         if the status flag or status string cannot be interpreted
     */
    public static DatasetQCStatus fromString(String reprString) throws IllegalArgumentException {
        DatasetQCStatus flag = new DatasetQCStatus();
        if ( reprString == null )
            return flag;
        if ( reprString.trim().isEmpty() )
            return flag;
        String[] pieces = reprString.trim().split(FLAG_SEPARATOR);
        if ( pieces.length == 1 )
            pieces = reprString.trim().split(STATUS_STRING_SEPARATOR);
        for (String strval : pieces) {
            if ( strval.startsWith(PISTATUS_STRING_PREFIX) ) {
                String substr = strval.substring(PISTATUS_STRING_PREFIX.length());
                Status value = Status.fromString(substr);
                if ( value == null )
                    throw new IllegalArgumentException("Unable to interpret the PI-suggested status string '" +
                            substr + "' in '" + reprString + "'");
                if ( !SUGGESTED_STATUS_VALUES.contains(value) )
                    throw new IllegalArgumentException("Invalid PI-suggested QC status " + value);
                flag.setPiSuggested(value);
            }
            else if ( strval.startsWith(AUTOSTATUS_STRING_PREFIX) ) {
                String substr = strval.substring(AUTOSTATUS_STRING_PREFIX.length());
                Status value = Status.fromString(substr);
                if ( value == null )
                    throw new IllegalArgumentException("Unable to interpret the automation-suggested status string '" +
                            substr + "' in '" + reprString + "'");
                if ( !SUGGESTED_STATUS_VALUES.contains(value) )
                    throw new IllegalArgumentException("Invalid automation-suggested QC status " + value);
                flag.setAutoSuggested(value);
            }
            else if ( strval.startsWith(PIFLAG_PREFIX) ) {
                String substr = strval.substring(PIFLAG_PREFIX.length());
                Status value = Status.fromString(substr);
                if ( value == null )
                    throw new IllegalArgumentException("Unable to interpret the PI-suggested status flag '" +
                            substr + "' in '" + reprString + "'");
                if ( !SUGGESTED_STATUS_VALUES.contains(value) )
                    throw new IllegalArgumentException("Invalid PI-suggested QC status " + value);
                flag.setPiSuggested(value);
            }
            else if ( strval.startsWith(AUTOFLAG_PREFIX) ) {
                String substr = strval.substring(AUTOFLAG_PREFIX.length());
                Status value = Status.fromString(substr);
                if ( value == null )
                    throw new IllegalArgumentException("Unable to interpret the automation-suggested status flag '" +
                            substr + "' in '" + reprString + "'");
                if ( !SUGGESTED_STATUS_VALUES.contains(value) )
                    throw new IllegalArgumentException("Invalid automation-suggested QC status " + value);
                flag.setAutoSuggested(value);
            }
            else {
                Status value = Status.fromString(strval);
                if ( value == null )
                    throw new IllegalArgumentException("Unable to interpret actual status flag or status string '" +
                            strval + "' in '" + reprString + "'");
                flag.setActual(value);
            }
        }

        return flag;
    }

    /**
     * @return if this dataset QC status indicates the dataset is private (has never been submitted for QC)
     */
    public boolean isPrivate() {
        return Status.PRIVATE.equals(actual);
    }

    /**
     * @return if this dataset QC status indicates the dataset is new awaiting QC
     */
    public boolean isNewAwaitingQC() {
        return Status.NEW_AWAITING_QC.equals(actual);
    }

    /**
     * @return if this dataset QC status indicates the dataset is updated awaiting QC
     */
    public boolean isUpdatedAwaitingQC() {
        return Status.UPDATED_AWAITING_QC.equals(actual);
    }

    /**
     * @return if this dataset QC status indicates the dataset is new or updated awaiting QC, or is conflicted
     */
    public boolean isAwaitingQC() {
        if ( Status.NEW_AWAITING_QC.equals(actual) )
            return true;
        if ( Status.UPDATED_AWAITING_QC.equals(actual) )
            return true;
        if ( Status.CONFLICTED.equals(actual) )
            return true;
        return false;
    }

    /**
     * @return if this dataset QC status indicates the dataset can be edited (not ready for the next release)
     */
    public boolean isEditable() {
        if ( Status.PRIVATE.equals(actual) )
            return true;
        if ( Status.SUSPENDED.equals(actual) )
            return true;
        if ( Status.EXCLUDED.equals(actual) )
            return true;
        return false;
    }

    /**
     * @return if this dataset QC status indicates the dataset has conflicting regional QC flags
     */
    public boolean isConflicted() {
        return Status.CONFLICTED.equals(actual);
    }

    /**
     * @return if this dataset QC status indicates the dataset is acceptable for the next release
     */
    public boolean isAcceptable() {
        return Status.isAcceptable(actual);
    }

    /**
     * @return if this is a QC comment flag
     */
    public boolean isCommentFlag() {
        return Status.COMMENT.equals(actual);
    }

    /**
     * @return if this is a dataset rename flag
     */
    public boolean isRenameFlag() {
        return Status.RENAMED.equals(actual);
    }

    @Override
    public int compareTo(DatasetQCStatus other) {
        int value = actual.compareTo(other.actual);
        if ( value != 0 )
            return value;
        value = autoSuggested.compareTo(other.autoSuggested);
        if ( value != 0 )
            return value;
        value = piSuggested.compareTo(other.piSuggested);
        if ( value != 0 )
            return value;
        Integer numComments = comments.size();
        value = numComments.compareTo(other.comments.size());
        if ( value != 0 )
            return value;
        for (int k = 0; k < numComments; k++) {
            value = comments.get(k).compareTo(other.comments.get(k));
            if ( value != 0 )
                return value;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof DatasetQCStatus) )
            return false;

        DatasetQCStatus other = (DatasetQCStatus) obj;

        if ( !actual.equals(other.actual) )
            return false;
        if ( !autoSuggested.equals(other.autoSuggested) )
            return false;
        if ( !piSuggested.equals(other.piSuggested) )
            return false;
        if ( !comments.equals(other.comments) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int value = actual.hashCode();
        value = value * prime + autoSuggested.hashCode();
        value = value * prime + piSuggested.hashCode();
        value = value * prime + comments.hashCode();
        return value;
    }

    @Override
    public String toString() {
        return "DatasetQCStatus" +
                "[ actual=" + actual +
                ", autoSuggested=" + autoSuggested +
                ", piSuggested=" + piSuggested +
                ", comments=" + comments.toString() +
                " ]";
    }

}
