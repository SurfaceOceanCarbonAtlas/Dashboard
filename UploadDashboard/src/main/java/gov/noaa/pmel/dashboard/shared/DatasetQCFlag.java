package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Represents the QC flag for a dataset.  Traditionally these were the single letters.
 * This class provides the possibility of more flexibility that is needed for automated QC
 * as well as the possibility of using values indicating the accuracy in the fCO2 values.
 */
public class DatasetQCFlag implements Serializable, IsSerializable, Comparable<DatasetQCFlag> {

    private static final long serialVersionUID = -1515101157313114699L;

    /**
     * Values for the flags in DatasetQCFlag
     */
    public enum Status implements Serializable, IsSerializable {
        NOT_GIVEN,
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
        RENAMED
    }

    private static final String FLAG_SEPARATOR = "-";
    private static final String PIFLAG_PREFIX = "pi";
    private static final String AUTOFLAG_PREFIX = "auto";

    private static final String STATUS_STRING_SEPARATOR = "; ";
    private static final String PISTATUS_STRING_PREFIX = "PI suggested ";
    private static final String AUTOSTATUS_STRING_PREFIX = "automation suggested ";

    // Dataset QC flags - datasets that can be modified
    private static final String FLAG_NOT_GIVEN = "";
    private static final String FLAG_SUSPENDED = "S";
    private static final String FLAG_EXCLUDED = "X";
    // Dataset QC flags - datasets that cannot be modified
    public static final String FLAG_NEW_AWAITING_QC = "N";
    public static final String FLAG_UPDATED_AWAITING_QC = "U";
    private static final String FLAG_CONFLICTED = "Q";
    private static final String FLAG_ACCEPTED_A = "A";
    private static final String FLAG_ACCEPTED_B = "B";
    private static final String FLAG_ACCEPTED_C = "C";
    private static final String FLAG_ACCEPTED_D = "D";
    private static final String FLAG_ACCEPTED_E = "E";
    // QC flag for only a comment
    public static final String FLAG_COMMENT = "H";
    // Internal QC for recording dataset renaming
    public static final String FLAG_RENAMED = "R";

    // Dataset QC strings - datasets that can be modified
    private static final String STATUS_STRING_NOT_GIVEN = "";
    private static final String STATUS_STRING_SUSPENDED = "Suspended";
    private static final String STATUS_STRING_EXCLUDED = "Excluded";
    // Dataset QC strings - datasets that cannot be modified
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
    // Internal QC for recording dataset renaming
    private static final String STATUS_STRING_RENAMED = "Renamed";

    /**
     * Map of dataset QC status to flags (one-character strings).
     */
    private static final HashMap<Status,String> STATUS_FLAG_MAP;

    /**
     * Map of dataset QC status to status strings (human-readable strings).
     */
    private static final HashMap<Status,String> STATUS_STRING_MAP;

    /**
     * Map of dataset QC flags (one-character strings) and status strings
     * (human-readable strings) to dataset QC status
     */
    private static final HashMap<String,Status> STRING_STATUS_MAP;

    static {
        STATUS_FLAG_MAP = new HashMap<Status,String>();
        STATUS_FLAG_MAP.put(Status.NOT_GIVEN, FLAG_NOT_GIVEN);
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
        STATUS_STRING_MAP.put(Status.NOT_GIVEN, STATUS_STRING_NOT_GIVEN);
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

        STRING_STATUS_MAP = new HashMap<String,Status>();
        STRING_STATUS_MAP.put(FLAG_NOT_GIVEN, Status.NOT_GIVEN);
        STRING_STATUS_MAP.put(FLAG_SUSPENDED, Status.SUSPENDED);
        STRING_STATUS_MAP.put(FLAG_EXCLUDED, Status.EXCLUDED);
        STRING_STATUS_MAP.put(FLAG_NEW_AWAITING_QC, Status.NEW_AWAITING_QC);
        STRING_STATUS_MAP.put(FLAG_UPDATED_AWAITING_QC, Status.UPDATED_AWAITING_QC);
        STRING_STATUS_MAP.put(FLAG_ACCEPTED_A, Status.ACCEPTED_A);
        STRING_STATUS_MAP.put(FLAG_ACCEPTED_B, Status.ACCEPTED_B);
        STRING_STATUS_MAP.put(FLAG_ACCEPTED_C, Status.ACCEPTED_C);
        STRING_STATUS_MAP.put(FLAG_ACCEPTED_D, Status.ACCEPTED_D);
        STRING_STATUS_MAP.put(FLAG_ACCEPTED_E, Status.ACCEPTED_E);
        STRING_STATUS_MAP.put(FLAG_CONFLICTED, Status.CONFLICTED);
        STRING_STATUS_MAP.put(FLAG_RENAMED, Status.RENAMED);
        STRING_STATUS_MAP.put(FLAG_COMMENT, Status.COMMENT);

        STRING_STATUS_MAP.put(STATUS_STRING_NOT_GIVEN, Status.NOT_GIVEN);
        STRING_STATUS_MAP.put(STATUS_STRING_SUSPENDED, Status.SUSPENDED);
        STRING_STATUS_MAP.put(STATUS_STRING_EXCLUDED, Status.EXCLUDED);
        STRING_STATUS_MAP.put(STATUS_STRING_NEW_AWAITING_QC, Status.NEW_AWAITING_QC);
        STRING_STATUS_MAP.put(STATUS_STRING_UPDATED_AWAITING_QC, Status.UPDATED_AWAITING_QC);
        STRING_STATUS_MAP.put(STATUS_STRING_ACCEPTED_A, Status.ACCEPTED_A);
        STRING_STATUS_MAP.put(STATUS_STRING_ACCEPTED_B, Status.ACCEPTED_B);
        STRING_STATUS_MAP.put(STATUS_STRING_ACCEPTED_C, Status.ACCEPTED_C);
        STRING_STATUS_MAP.put(STATUS_STRING_ACCEPTED_D, Status.ACCEPTED_D);
        STRING_STATUS_MAP.put(STATUS_STRING_ACCEPTED_E, Status.ACCEPTED_E);
        STRING_STATUS_MAP.put(STATUS_STRING_CONFLICTED, Status.CONFLICTED);
        STRING_STATUS_MAP.put(STATUS_STRING_RENAMED, Status.RENAMED);
        STRING_STATUS_MAP.put(STATUS_STRING_COMMENT, Status.COMMENT);
    }

    private Status actualFlag;
    private Status piFlag;
    private Status autoFlag;

    /**
     * Create with all QC flags set to {@link Status#NOT_GIVEN}
     */
    public DatasetQCFlag() {
        actualFlag = Status.NOT_GIVEN;
        piFlag = Status.NOT_GIVEN;
        autoFlag = Status.NOT_GIVEN;
    }

    /**
     * Create with the given QC flag as the actual dataset flag.
     * The automation-suggested and the PI-suggested QC flags are set to {@link Status#NOT_GIVEN}
     *
     * @param flag
     *         actual dataset QC flag to assign;
     *         if null, {@link Status#NOT_GIVEN} is assigned
     */
    public DatasetQCFlag(Status flag) {
        this();
        if ( flag != null )
            actualFlag = flag;
    }

    /**
     * Create a duplicate of the given dataset QC flag.
     * (Cloneable and Object.clone not supported by GWT.)
     *
     * @param qcFlag
     *         dataset QC flag to duplicate
     */
    public DatasetQCFlag(DatasetQCFlag qcFlag) {
        this();
        if ( qcFlag != null ) {
            actualFlag = qcFlag.actualFlag;
            piFlag = qcFlag.piFlag;
            autoFlag = qcFlag.autoFlag;
        }
    }

    /**
     * @return the actual QC flag for this dataset;
     *         never null but may be {@link Status#NOT_GIVEN}
     */
    public Status getActualFlag() {
        return actualFlag;
    }

    /**
     * @param flag
     *         the actual QC flag to assign for this dataset;
     *         if null, {@link Status#NOT_GIVEN} is assigned
     */
    public void setActualFlag(Status flag) {
        if ( flag == null )
            actualFlag = Status.NOT_GIVEN;
        else
            actualFlag = flag;
    }

    /**
     * @return the PI-suggested QC flag for this dataset;
     *         never null but may be {@link Status#NOT_GIVEN}
     */
    public Status getPiFlag() {
        return piFlag;
    }

    /**
     * @param flag
     *         the PI-suggested QC flag to assign fo this dataset;
     *         if null, {@link Status#NOT_GIVEN} is assigned
     */
    public void setPiFlag(Status flag) {
        if ( flag == null )
            piFlag = Status.NOT_GIVEN;
        else
            piFlag = flag;
    }

    /**
     * @return the automation-suggested QC flag for this dataset;
     *         never null but may be {@link Status#NOT_GIVEN}
     */
    public Status getAutoFlag() {
        return autoFlag;
    }

    /**
     * @param flag
     *         the automation-suggested QC flag to assign for this dataset;
     *         if null, {@link Status#NOT_GIVEN} is assigned
     */
    public void setAutoFlag(Status flag) {
        if ( flag == null )
            autoFlag = Status.NOT_GIVEN;
        else
            autoFlag = flag;
    }

    /**
     * @return the complete dataset QC flag as a (terse) String; never null.
     *         If the dataset is awaiting QC, it may include PI-suggested and
     *         automation-suggested QC flags; e.g., N-piB (new with PI-suggested
     *         flag of B) or U-auB (updated with automation-suggested flag of B)
     *
     * @throws IllegalArgumentException
     *         if one of the QC flags is unknown, or if a PI or automation suggested QC flag
     *         is not {@link Status#NOT_GIVEN} and the actual dataset QC flag indicates
     *         that it is not awaiting QC (see: {@link #isAwaitingQC()})
     */
    public String flagString() throws IllegalArgumentException {
        String valueString = STATUS_FLAG_MAP.get(actualFlag);
        if ( valueString == null )
            throw new IllegalArgumentException("Unknown QC flag " + actualFlag);
        if ( !piFlag.equals(Status.NOT_GIVEN) ) {
            if ( !isAwaitingQC() )
                throw new IllegalArgumentException("PI-suggested QC flag given for a dataset not awaiting QC");
            String piVal = STATUS_FLAG_MAP.get(piFlag);
            if ( piVal == null )
                throw new IllegalArgumentException("Unknown PI-suggested QC flag " + piFlag);
            valueString += FLAG_SEPARATOR + PIFLAG_PREFIX + piVal;
        }
        if ( !autoFlag.equals(Status.NOT_GIVEN) ) {
            if ( !isAwaitingQC() )
                throw new IllegalArgumentException("automation-suggested QC flag given for a dataset not awaiting QC");
            String autoVal = STATUS_FLAG_MAP.get(autoFlag);
            if ( autoVal == null )
                throw new IllegalArgumentException("Unknown automation-suggested QC flag " + autoFlag);
            valueString += FLAG_SEPARATOR + AUTOFLAG_PREFIX + autoVal;
        }
        return valueString;
    }

    /**
     * Create and return a DatasetQCFlag corresponding to the given flag or status string
     *
     * @param reprString
     *         flag or status string representing the DatasetQCFlag
     *
     * @return DatasetQCFlag object represented by the given flag or status string
     *
     * @throws IllegalArgumentException
     *         if the flag string cannot be interpreted
     */
    public static DatasetQCFlag fromString(String reprString) throws IllegalArgumentException {
        DatasetQCFlag flag = new DatasetQCFlag();
        if ( reprString == null )
            return flag;
        if ( reprString.trim().isEmpty() )
            return flag;
        String[] pieces = reprString.trim().split(FLAG_SEPARATOR);
        if ( pieces.length == 1 )
            pieces = reprString.trim().split(STATUS_STRING_SEPARATOR);
        for (String strval : pieces) {
            Status value;
            if ( strval.startsWith(PISTATUS_STRING_PREFIX) ) {
                value = STRING_STATUS_MAP.get(strval.substring(PISTATUS_STRING_PREFIX.length()));
                if ( value != null )
                    flag.setPiFlag(value);
            }
            else if ( strval.startsWith(AUTOSTATUS_STRING_PREFIX) ) {
                value = STRING_STATUS_MAP.get(strval.substring(AUTOSTATUS_STRING_PREFIX.length()));
                if ( value != null )
                    flag.setAutoFlag(value);
            }
            else if ( strval.startsWith(PIFLAG_PREFIX) ) {
                value = STRING_STATUS_MAP.get(strval.substring(PIFLAG_PREFIX.length()));
                if ( value != null )
                    flag.setPiFlag(value);
            }
            else if ( strval.startsWith(AUTOFLAG_PREFIX) ) {
                value = STRING_STATUS_MAP.get(strval.substring(AUTOFLAG_PREFIX.length()));
                if ( value != null )
                    flag.setAutoFlag(value);
            }
            else {
                value = STRING_STATUS_MAP.get(strval);
                if ( value != null )
                    flag.setActualFlag(value);
            }
            if ( value == null )
                throw new IllegalArgumentException("Unable to interpret '" + strval +
                        "' of flag or status string '" + reprString + "'");
        }

        return flag;
    }

    /**
     * @return the complete dataset QC flag as a status (human-friendly) String; never null.
     *         If the dataset is awaiting QC, it may include PI-suggested and
     *         automation-suggested QC flags; e.g., "submitted; PI suggested Flag B"
     *         or "submitted; automation suggested Flag B"
     *
     * @throws IllegalArgumentException
     *         if one of the QC flags is unknown, or if a PI or automation suggested QC flag
     *         is not {@link Status#NOT_GIVEN} and the actual dataset QC flag indicates
     *         that it is not awaiting QC (see: {@link #isAwaitingQC()})
     */
    public String statusString() throws IllegalArgumentException {
        String valueString = STATUS_STRING_MAP.get(actualFlag);
        if ( valueString == null )
            throw new IllegalArgumentException("Unknown QC status " + actualFlag);
        if ( !piFlag.equals(Status.NOT_GIVEN) ) {
            if ( !isAwaitingQC() )
                throw new IllegalArgumentException("PI-suggested QC flag given for a dataset not awaiting QC");
            String piVal = STATUS_STRING_MAP.get(piFlag);
            if ( piVal == null )
                throw new IllegalArgumentException("Unknown PI-suggested QC status " + piFlag);
            valueString += STATUS_STRING_SEPARATOR + PISTATUS_STRING_PREFIX + piVal;
        }
        if ( !autoFlag.equals(Status.NOT_GIVEN) ) {
            if ( !isAwaitingQC() )
                throw new IllegalArgumentException("automation-suggested QC flag given for a dataset not awaiting QC");
            String autoVal = STATUS_STRING_MAP.get(autoFlag);
            if ( autoVal == null )
                throw new IllegalArgumentException("Unknown automation-suggested QC status " + autoFlag);
            valueString += STATUS_STRING_SEPARATOR + AUTOSTATUS_STRING_PREFIX + autoVal;
        }
        return valueString;
    }

    /**
     * @return if this flag indicates the dataset has never been submitted for QC
     */
    public boolean isUnsubmitted() {
        return Status.NOT_GIVEN.equals(actualFlag);
    }

    /**
     * @return if this flag indicates the dataset is new awaiting QC
     */
    public boolean isNewAwaitingQC() {
        return Status.NEW_AWAITING_QC.equals(actualFlag);
    }

    /**
     * @return if this flag indicates the dataset is updated awaiting QC
     */
    public boolean isUpdatedAwaitingQC() {
        return Status.UPDATED_AWAITING_QC.equals(actualFlag);
    }

    /**
     * @return if this flag indicates the dataset is new or updated awaiting QC, or is conflicted
     */
    public boolean isAwaitingQC() {
        if ( Status.NEW_AWAITING_QC.equals(actualFlag) )
            return true;
        if ( Status.UPDATED_AWAITING_QC.equals(actualFlag) )
            return true;
        if ( Status.CONFLICTED.equals(actualFlag) )
            return true;
        return false;
    }

    /**
     * @return if this flag indicates the dataset can be edited (not ready for SOCAT release)
     */
    public boolean isEditable() {
        if ( Status.NOT_GIVEN.equals(actualFlag) )
            return true;
        if ( Status.SUSPENDED.equals(actualFlag) )
            return true;
        if ( Status.EXCLUDED.equals(actualFlag) )
            return true;
        return false;
    }

    /**
     * @return if this flag indicates the dataset has conflicting regional QC flags
     */
    public boolean isConflicted() {
        return Status.CONFLICTED.equals(actualFlag);
    }

    /**
     * @return if this flag indicates the dataset is acceptable for the SOCAT release
     */
    public boolean isAcceptable() {
        if ( Status.ACCEPTED_A.equals(actualFlag) )
            return true;
        if ( Status.ACCEPTED_B.equals(actualFlag) )
            return true;
        if ( Status.ACCEPTED_C.equals(actualFlag) )
            return true;
        if ( Status.ACCEPTED_D.equals(actualFlag) )
            return true;
        if ( Status.ACCEPTED_E.equals(actualFlag) )
            return true;
        return false;
    }

    /**
     * @return if this is a QC comment flag
     */
    public boolean isCommentFlag() {
        return Status.COMMENT.equals(actualFlag);
    }

    /**
     * @return if this is a dataset rename flag
     */
    public boolean isRenameFlag() {
        return Status.RENAMED.equals(actualFlag);
    }

    @Override
    public int compareTo(DatasetQCFlag other) {
        int value = actualFlag.compareTo(other.actualFlag);
        if ( value != 0 )
            return value;
        value = autoFlag.compareTo(other.autoFlag);
        if ( value != 0 )
            return value;
        value = piFlag.compareTo(other.piFlag);
        if ( value != 0 )
            return value;

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof DatasetQCFlag) )
            return false;

        DatasetQCFlag other = (DatasetQCFlag) obj;

        if ( !actualFlag.equals(other.actualFlag) )
            return false;
        if ( !autoFlag.equals(other.autoFlag) )
            return false;
        if ( !piFlag.equals(other.piFlag) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int value = actualFlag.hashCode();
        value = value * prime + autoFlag.hashCode();
        value = value * prime + piFlag.hashCode();
        return value;
    }

    @Override
    public String toString() {
        return "DatasetQCFlag" +
                "[ actualFlag=" + actualFlag +
                ", autoFlag=" + autoFlag +
                ", piFlag=" + piFlag +
                " ]";
    }

}
