package gov.noaa.pmel.dashboard.qc;

import java.util.HashMap;

/**
 * Represents the QC flag for a dataset.  Traditionally these were the single letters.
 * This class provides the possibility of more flexbility that is needed for automated QC
 * as well as the possibility of using values indicating the accuracy in the fCO2 values.
 */
public class DatasetQCFlag implements Comparable<DatasetQCFlag> {

    public enum FlagValue {
        NOT_GIVEN,
        SUSPENDED,
        EXCLUDED,
        NEW_WAITING_QC,
        UPDATED_WAITING_QC,
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
    private static final String AUTOFLAG_PREFIX = "au";

    private static final String STATUS_SEPARATOR = "; ";
    private static final String PISTATUS_PREFIX = "PI suggested ";
    private static final String AUTOSTATUS_PREFIX = "automation suggested ";

    // Dataset QC flags - datasets that can be modified
    private static final String FLAG_NOT_GIVEN = "";
    private static final String FLAG_SUSPENDED = "S";
    private static final String FLAG_EXCLUDED = "X";
    // Dataset QC flags - datasets that cannot be modified
    private static final String FLAG_NEW_WAITING_QC = "N";
    private static final String FLAG_UPDATED_WAITING_QC = "U";
    private static final String FLAG_CONFLICTED = "Q";
    private static final String FLAG_ACCEPTED_A = "A";
    private static final String FLAG_ACCEPTED_B = "B";
    private static final String FLAG_ACCEPTED_C = "C";
    private static final String FLAG_ACCEPTED_D = "D";
    private static final String FLAG_ACCEPTED_E = "E";
    // QC flag for only a comment
    private static final String FLAG_COMMENT = "H";
    // Internal QC for recording dataset renaming
    private static final String FLAG_RENAMED = "R";

    // Dataset QC strings - datasets that can be modified
    private static final String STATUS_NOT_SUBMITTED = "";
    private static final String STATUS_SUSPENDED = "Suspended";
    private static final String STATUS_EXCLUDED = "Excluded";
    // Dataset QC strings - datasets that cannot be modified
    private static final String STATUS_NEW_WAITING_QC = "Submitted";
    private static final String STATUS_UPDATED_WAITING_QC = "Submitted";
    private static final String STATUS_CONFLICTED = "Conflict";
    private static final String STATUS_ACCEPTED_A = "Flag A";
    private static final String STATUS_ACCEPTED_B = "Flag B";
    private static final String STATUS_ACCEPTED_C = "Flag C";
    private static final String STATUS_ACCEPTED_D = "Flag D";
    private static final String STATUS_ACCEPTED_E = "Flag E";
    // Internal QC for recording dataset renaming
    private static final String STATUS_RENAMED = "Renamed";

    /**
     * Map of dataset QC flags to dataset flag values (one-character strings).
     */
    private static final HashMap<FlagValue,String> FLAG_VALUE_MAP;

    /**
     * Map of dataset QC flags to dataset status strings (human-readable strings).
     * {@link FlagValue#COMMENT} is not included as it does not affect the status.
     */
    private static final HashMap<FlagValue,String> FLAG_STATUS_MAP;

    static {
        FLAG_VALUE_MAP = new HashMap<FlagValue,String>();
        FLAG_VALUE_MAP.put(FlagValue.NOT_GIVEN, FLAG_NOT_GIVEN);
        FLAG_VALUE_MAP.put(FlagValue.SUSPENDED, FLAG_SUSPENDED);
        FLAG_VALUE_MAP.put(FlagValue.EXCLUDED, FLAG_EXCLUDED);
        FLAG_VALUE_MAP.put(FlagValue.NEW_WAITING_QC, FLAG_NEW_WAITING_QC);
        FLAG_VALUE_MAP.put(FlagValue.UPDATED_WAITING_QC, FLAG_UPDATED_WAITING_QC);
        FLAG_VALUE_MAP.put(FlagValue.ACCEPTED_A, FLAG_ACCEPTED_A);
        FLAG_VALUE_MAP.put(FlagValue.ACCEPTED_B, FLAG_ACCEPTED_B);
        FLAG_VALUE_MAP.put(FlagValue.ACCEPTED_C, FLAG_ACCEPTED_C);
        FLAG_VALUE_MAP.put(FlagValue.ACCEPTED_D, FLAG_ACCEPTED_D);
        FLAG_VALUE_MAP.put(FlagValue.ACCEPTED_E, FLAG_ACCEPTED_E);
        FLAG_VALUE_MAP.put(FlagValue.CONFLICTED, FLAG_CONFLICTED);
        FLAG_VALUE_MAP.put(FlagValue.RENAMED, FLAG_RENAMED);
        FLAG_VALUE_MAP.put(FlagValue.COMMENT, FLAG_COMMENT);

        FLAG_STATUS_MAP = new HashMap<FlagValue,String>();
        FLAG_STATUS_MAP.put(FlagValue.NOT_GIVEN, STATUS_NOT_SUBMITTED);
        FLAG_STATUS_MAP.put(FlagValue.SUSPENDED, STATUS_SUSPENDED);
        FLAG_STATUS_MAP.put(FlagValue.EXCLUDED, STATUS_EXCLUDED);
        FLAG_STATUS_MAP.put(FlagValue.NEW_WAITING_QC, STATUS_NEW_WAITING_QC);
        FLAG_STATUS_MAP.put(FlagValue.UPDATED_WAITING_QC, STATUS_UPDATED_WAITING_QC);
        FLAG_STATUS_MAP.put(FlagValue.ACCEPTED_A, STATUS_ACCEPTED_A);
        FLAG_STATUS_MAP.put(FlagValue.ACCEPTED_B, STATUS_ACCEPTED_B);
        FLAG_STATUS_MAP.put(FlagValue.ACCEPTED_C, STATUS_ACCEPTED_C);
        FLAG_STATUS_MAP.put(FlagValue.ACCEPTED_D, STATUS_ACCEPTED_D);
        FLAG_STATUS_MAP.put(FlagValue.ACCEPTED_E, STATUS_ACCEPTED_E);
        FLAG_STATUS_MAP.put(FlagValue.CONFLICTED, STATUS_CONFLICTED);
        FLAG_STATUS_MAP.put(FlagValue.RENAMED, STATUS_RENAMED);
    }

    private FlagValue actualFlag;
    private FlagValue piFlag;
    private FlagValue autoFlag;

    /**
     * Create with the given QC flag as the actual dataset flag.
     * The automation-suggested and the PI-suggested QC flags are set to {@link FlagValue#NOT_GIVEN}
     *
     * @param flag
     *         actual dataset QC flag to assign;
     *         if null, {@link FlagValue#NOT_GIVEN} is assigned
     */
    public DatasetQCFlag(FlagValue flag) {
        if ( flag == null )
            actualFlag = FlagValue.NOT_GIVEN;
        else
            actualFlag = flag;
        piFlag = FlagValue.NOT_GIVEN;
        autoFlag = FlagValue.NOT_GIVEN;
    }

    /**
     * Assigns or clears the PI-suggested QC flag.  Giving null or {@link FlagValue#NOT_GIVEN} as the flag
     * clears the PI-suggested QC flag (assigns {@link FlagValue#NOT_GIVEN}).  If another flag is given,
     * the actual QC flag must indicate the dataset is awaiting QC.
     *
     * @param flag
     *         PI-suggested QC flag to assign; if null, {@link FlagValue#NOT_GIVEN} is assigned
     *
     * @throws IllegalArgumentException
     *         if the given flag and the actual QC flag are incompatible
     */
    public void setPiFlag(FlagValue flag) {
        if ( (flag == null) || flag.equals(FlagValue.NOT_GIVEN) ) {
            piFlag = FlagValue.NOT_GIVEN;
            return;
        }
        if ( !isAwaitingQC() )
            throw new IllegalArgumentException("Assigning PI-suggested QC flag on dataset not awaiting QC");
        piFlag = flag;
    }

    /**
     * Assigns or clears the automation-suggested QC flag.  Giving null or {@link FlagValue#NOT_GIVEN}
     * as the flag clears the automation-suggested QC flag (assigns {@link FlagValue#NOT_GIVEN}).
     * If another flag is given, the actual QC flag must indicate the dataset is awaiting QC.
     *
     * @param flag
     *         automation-suggested QC flag to assign; if null, {@link FlagValue#NOT_GIVEN} is assigned
     *
     * @throws IllegalArgumentException
     *         if the given flag and the actual QC flag are incompatible
     */
    public void setAutoFlag(FlagValue flag) throws IllegalArgumentException {
        if ( (flag == null) || flag.equals(FlagValue.NOT_GIVEN) ) {
            autoFlag = FlagValue.NOT_GIVEN;
            return;
        }
        if ( !isAwaitingQC() )
            throw new IllegalArgumentException("Assigning automation-suggested QC flag on dataset not awaiting QC");
        autoFlag = flag;
    }

    /**
     * @return the complete dataset QC flag as a (terse) String; never null.
     *         If the dataset is awaiting QC, it may include PI-suggested and
     *         automation-suggested QC flags; e.g., N-piB (new with PI-suggested
     *         flag of B) or U-auB (updated with automation-suggested flag of B)
     *
     * @throws IllegalStateException
     *         if one of the QC flags is unknown
     */
    public String flagString() throws IllegalStateException {
        String valueString = FLAG_VALUE_MAP.get(actualFlag);
        if ( valueString == null )
            throw new IllegalStateException("Unknown QC flag " + actualFlag);
        if ( !piFlag.equals(FlagValue.NOT_GIVEN) ) {
            // This should only occur if actualFlag is NEW_WAITING_QC or UPDATED_WAITING_QC
            String piVal = FLAG_VALUE_MAP.get(piFlag);
            if ( piVal == null )
                throw new IllegalStateException("Unknown PI-suggested QC flag " + piFlag);
            valueString += FLAG_SEPARATOR + PIFLAG_PREFIX + piVal;
        }
        if ( !autoFlag.equals(FlagValue.NOT_GIVEN) ) {
            // This should only occur if actualFlag is NEW_WAITING_QC or UPDATED_WAITING_QC
            String autoVal = FLAG_VALUE_MAP.get(autoFlag);
            if ( autoVal == null )
                throw new IllegalStateException("Unknown automation-suggested QC flag " + autoFlag);
            valueString += FLAG_SEPARATOR + AUTOFLAG_PREFIX + autoVal;
        }
        return valueString;
    }

    /**
     * @return the complete dataset QC flag as a status (human-friendly) String; never null.
     *         If the dataset is awaiting QC, it may include PI-suggested and
     *         automation-suggested QC flags; e.g., "submitted; PI suggested Flag B"
     *         or "submitted; automation suggested Flag B"
     *
     * @throws IllegalStateException
     *         if one of the QC flags is unknown
     */
    public String statusString() throws IllegalStateException {
        String valueString = FLAG_STATUS_MAP.get(actualFlag);
        if ( valueString == null )
            throw new IllegalStateException("Unknown QC status " + actualFlag);
        if ( !piFlag.equals(FlagValue.NOT_GIVEN) ) {
            // This should only occur if actualFlag is NEW_WAITING_QC or UPDATED_WAITING_QC
            String piVal = FLAG_STATUS_MAP.get(piFlag);
            if ( piVal == null )
                throw new IllegalStateException("Unknown PI-suggested QC status " + piFlag);
            valueString += STATUS_SEPARATOR + PISTATUS_PREFIX + piVal;
        }
        if ( !autoFlag.equals(FlagValue.NOT_GIVEN) ) {
            // This should only occur if actualFlag is NEW_WAITING_QC or UPDATED_WAITING_QC
            String autoVal = FLAG_STATUS_MAP.get(autoFlag);
            if ( autoVal == null )
                throw new IllegalStateException("Unknown automation-suggested QC status " + autoFlag);
            valueString += STATUS_SEPARATOR + AUTOSTATUS_PREFIX + autoVal;
        }
        return valueString;
    }

    /**
     * @return if this flag indicates the dataset is new awaiting QC
     */
    public boolean isNewAwaitingQC() {
        return FlagValue.NEW_WAITING_QC.equals(actualFlag);
    }

    /**
     * @return if this flag indicates the dataset is updated awaiting QC
     */
    public boolean isUpdatedAwaitingQC() {
        return FlagValue.UPDATED_WAITING_QC.equals(actualFlag);
    }

    /**
     * @return if this flag indicates the dataset is new or updated awaiting QC
     */
    public boolean isAwaitingQC() {
        if ( FlagValue.NEW_WAITING_QC.equals(actualFlag) )
            return true;
        if ( FlagValue.UPDATED_WAITING_QC.equals(actualFlag) )
            return true;
        return false;
    }

    /**
     * @return if this flag indicates the dataset can be edited (not ready for SOCAT release)
     */
    public boolean isEditable() {
        if ( FlagValue.NOT_GIVEN.equals(actualFlag) )
            return true;
        if ( FlagValue.SUSPENDED.equals(actualFlag) )
            return true;
        if ( FlagValue.EXCLUDED.equals(actualFlag) )
            return true;
        return false;
    }

    /**
     * @return if this flag indicates the dataset has conflicting regional QC flags
     */
    public boolean isConflicted() {
        return FlagValue.CONFLICTED.equals(actualFlag);
    }

    /**
     * @return if this flag indicates the dataset is acceptable for the SOCAT release
     */
    public boolean isAcceptable() {
        if ( FlagValue.ACCEPTED_A.equals(actualFlag) )
            return true;
        if ( FlagValue.ACCEPTED_B.equals(actualFlag) )
            return true;
        if ( FlagValue.ACCEPTED_C.equals(actualFlag) )
            return true;
        if ( FlagValue.ACCEPTED_D.equals(actualFlag) )
            return true;
        if ( FlagValue.ACCEPTED_E.equals(actualFlag) )
            return true;
        return false;
    }

    /**
     * @return if this is a QC comment flag
     */
    public boolean isCommentFlag() {
        return FlagValue.COMMENT.equals(actualFlag);
    }

    /**
     * @return if this is a dataset rename flag
     */
    public boolean isRenameFlag() {
        return FlagValue.RENAMED.equals(actualFlag);
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
