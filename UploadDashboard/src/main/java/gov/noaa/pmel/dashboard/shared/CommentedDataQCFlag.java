package gov.noaa.pmel.dashboard.shared;

/**
 * Represents a QC flag along with a comment about the flag.
 */
public class CommentedDataQCFlag extends DataQCFlag {

    private static final long serialVersionUID = 6129255299969059445L;

    String comment;

    /**
     * Create with
     * <ul>
     * <li> flagName of {@link DashboardUtils#STRING_MISSING_VALUE}, </li>
     * <li> flagValue of {@link DashboardUtils#STRING_MISSING_VALUE}, </li>
     * <li> severity of {@link Severity#UNASSIGNED}, </li>
     * <li> column of {@link DashboardUtils#INT_MISSING_VALUE}, and </li>
     * <li> row of {@link DashboardUtils#INT_MISSING_VALUE} </li>
     * <li> comment of {@link DashboardUtils#STRING_MISSING_VALUE} </li>
     * </ul>
     */
    public CommentedDataQCFlag() {
        super();
        this.comment = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * Create with given values for flag name, flag value, severity, column index, row index, and comment as described
     * by {@link #setFlagName(String)}, {@link #setFlagValue(String)}, {@link #setSeverity(DataQCFlag.Severity)},
     * {@link #setColumnIndex(Integer)}, {@link #setRowIndex(Integer)}, and {@link #setComment(String)}
     */
    public CommentedDataQCFlag(String flagName, String flagValue, Severity severity,
            Integer colIdx, Integer rowIdx, String comment) {
        super(flagName, flagValue, severity, colIdx, rowIdx);
        setComment(comment);
    }

    /**
     * Created with the values in the given {@link DataQCFlag} along with the given comment.
     *
     * @param qcflag
     *         get the flag value from here; cannot be null
     * @param comment
     *         comment to assign as described by {@link #setComment(String)}
     */
    public CommentedDataQCFlag(DataQCFlag qcflag, String comment) {
        super(qcflag.flagName, qcflag.flagValue, qcflag.severity, qcflag.columnIndex, qcflag.rowIndex);
        setComment(comment);
    }

    /**
     * @return the QC flag comment; never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not
     *         assigned
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment
     *         the QC flag comment to assign; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setComment(String comment) {
        if ( comment == null )
            this.comment = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.comment = comment;
    }

    /**
     * @see {@link DataQCFlag#compareTo(DataQCFlag)}
     *         <p>
     *         If other is a CommentedDataQCFlag, the comment strings are also compared;
     *         otherwise this comment string is compared to {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    @Override
    public int compareTo(DataQCFlag other) {
        int result = super.compareTo(other);
        if ( result != 0 )
            return result;
        if ( other instanceof CommentedDataQCFlag ) {
            CommentedDataQCFlag commqc = (CommentedDataQCFlag) other;
            result = comment.compareTo(commqc.comment);
        }
        else {
            result = comment.compareTo(DashboardUtils.STRING_MISSING_VALUE);
        }
        if ( result != 0 )
            return result;
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        // A CommentedDataQCFlag without a comment is equal to its superclass DataQCFlag
        // so do not alter the superclass hash code; otherwise the comments must
        // be the same so factor in the comment hash code.
        if ( !DashboardUtils.STRING_MISSING_VALUE.equals(comment) )
            result = prime * result + comment.hashCode();
        return result;
    }

    /**
     * @see {@link DataQCFlag#equals(Object)}
     *         <p>
     *         Note that if this CommentQCFlag does not have a comment,
     *         it will be considered equal to its superclass DataQCFlag object.
     */
    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;

        if ( !super.equals(obj) )
            return false;

        // If no comment, CommentQCFlag equal to its superclass object
        if ( DashboardUtils.STRING_MISSING_VALUE.equals(comment) )
            return true;

        // Otherwise must be a CommentQCFlag with the same comment
        if ( !(obj instanceof CommentedDataQCFlag) )
            return false;

        CommentedDataQCFlag other = (CommentedDataQCFlag) obj;
        if ( !comment.equals(other.comment) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        String repr = "Commented" + super.toString();
        repr = repr.substring(0, repr.length() - 2);
        repr = repr + ", comment=" + comment + " ]";
        return repr;
    }

}

