/**
 *
 */
package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Represents a QC flag. Used for combining, ordering, and searching QC flags for a dataset.
 *
 * @author Karl Smith
 */
public class QCFlag implements Comparable<QCFlag>, Serializable, IsSerializable {

    private static final long serialVersionUID = 1802598804328743505L;

    /**
     * WOCE-like enumerated type indicating the severity of a QC flag value
     */
    public enum Severity implements Serializable, IsSerializable {
        UNASSIGNED,
        ACCEPTABLE,
        WARNING,
        ERROR,
        CRITICAL
    }

    protected String flagName;
    protected String flagValue;
    protected Severity severity;
    protected Integer columnIndex;
    protected Integer rowIndex;
    protected String comment;

    /**
     * Create with a flagName of {@link DashboardUtils#STRING_MISSING_VALUE}, flagValue of {@link
     * DashboardUtils#STRING_MISSING_VALUE}, severity of {@link Severity#UNASSIGNED}, column of {@link
     * DashboardUtils#INT_MISSING_VALUE}, row of {@link DashboardUtils#INT_MISSING_VALUE}, and comment of {@link
     * DashboardUtils#STRING_MISSING_VALUE}.
     */
    public QCFlag() {
        flagName = DashboardUtils.STRING_MISSING_VALUE;
        flagValue = DashboardUtils.STRING_MISSING_VALUE;
        severity = Severity.UNASSIGNED;
        columnIndex = DashboardUtils.INT_MISSING_VALUE;
        rowIndex = DashboardUtils.INT_MISSING_VALUE;
        comment = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * Create with given flag name, flag value, column index, and row index as described by {@link
     * #setFlagName(String)}, {@link #setFlagValue(String)}, {@link #setSeverity(QCFlag.Severity)}, {@link
     * #setColumnIndex(Integer)}, and {@link #setRowIndex(Integer)}. The comment is set to {@link
     * DashboardUtils#STRING_MISSING_VALUE}.
     */
    public QCFlag(String flagName, String flagValue, Severity severity,
            Integer columnIndex, Integer rowIndex) {
        setFlagName(flagName);
        setFlagValue(flagValue);
        setSeverity(severity);
        setColumnIndex(columnIndex);
        setRowIndex(rowIndex);
        comment = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return the QC flag name; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getFlagName() {
        return flagName;
    }

    /**
     * @param flagName
     *         the QC flag name to set; if null {@link DashboardUtils#STRING_MISSING_VALUE} will be assigned
     */
    public void setFlagName(String flagName) {
        if ( flagName != null )
            this.flagName = flagName;
        else
            this.flagName = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return the QC flag value; never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getFlagValue() {
        return flagValue;
    }

    /**
     * @param flagValue
     *         the QC flag value to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} will be assigned
     */
    public void setFlagValue(String flagValue) {
        if ( flagValue != null )
            this.flagValue = flagValue;
        else
            this.flagValue = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return the severity of the QC flag; never null but may be {@link QCFlag.Severity#UNASSIGNED} if not assigned
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * @param severity
     *         the severity to set for this QC flag value; if null, {@link QCFlag.Severity#UNASSIGNED} will be assigned
     */
    public void setSeverity(Severity severity) {
        if ( severity != null )
            this.severity = severity;
        else
            this.severity = Severity.UNASSIGNED;
    }

    /**
     * @return the index of the column for this QC flag; never null, but may be {@link DashboardUtils#INT_MISSING_VALUE}
     * if not assigned
     */
    public Integer getColumnIndex() {
        return columnIndex;
    }

    /**
     * @param columnIndex
     *         the index of the column to set for this QC flag; if null {@link DashboardUtils#INT_MISSING_VALUE} will be
     *         assigned
     */
    public void setColumnIndex(Integer columnIndex) {
        if ( columnIndex != null )
            this.columnIndex = columnIndex;
        else
            this.columnIndex = DashboardUtils.INT_MISSING_VALUE;
    }

    /**
     * @return the index of the row for this QC flag; never null, but may be {@link DashboardUtils#INT_MISSING_VALUE} if
     * not assigned
     */
    public Integer getRowIndex() {
        return rowIndex;
    }

    /**
     * @param rowIndex
     *         the index of the row to set for this QC flag; if null {@link DashboardUtils#INT_MISSING_VALUE} will be
     *         assigned
     */
    public void setRowIndex(Integer rowIndex) {
        if ( rowIndex != null )
            this.rowIndex = rowIndex;
        else
            this.rowIndex = DashboardUtils.INT_MISSING_VALUE;
    }

    /**
     * @return the QC comment; never null but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment
     *         the QC comment to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setComment(String comment) {
        if ( comment != null )
            this.comment = comment;
        else
            this.comment = DashboardUtils.STRING_MISSING_VALUE;
    }

    @Override
    public int compareTo(QCFlag other) {
        int result = this.flagName.compareTo(other.flagName);
        if ( result != 0 )
            return result;
        result = this.flagValue.compareTo(other.flagValue);
        if ( result != 0 )
            return result;
        // Presumably a flag name and value directly corresponds
        // to a severity, but go ahead and check it
        result = this.severity.compareTo(other.severity);
        if ( result != 0 )
            return result;
        result = this.columnIndex.compareTo(other.columnIndex);
        if ( result != 0 )
            return result;
        result = this.comment.compareTo(other.comment);
        if ( result != 0 )
            return result;
        result = this.rowIndex.compareTo(other.rowIndex);
        if ( result != 0 )
            return result;
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = comment.hashCode();
        result = prime * result + flagName.hashCode();
        result = prime * result + flagValue.hashCode();
        result = prime * result + severity.hashCode();
        result = prime * result + rowIndex.hashCode();
        result = prime * result + columnIndex.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( !(obj instanceof QCFlag) )
            return false;

        QCFlag other = (QCFlag) obj;
        if ( !columnIndex.equals(other.columnIndex) )
            return false;
        if ( !rowIndex.equals(other.rowIndex) )
            return false;
        if ( !flagValue.equals(other.flagValue) )
            return false;
        if ( !severity.equals(other.severity) )
            return false;
        if ( !flagName.equals(other.flagName) )
            return false;
        if ( !comment.equals(other.comment) )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "QCFlag[" +
                "flagName=" + flagName + ", " +
                "flagValue='" + flagValue + "', " +
                "severity='" + severity.toString() + "', " +
                "column=" + columnIndex.toString() + ", " +
                "row=" + rowIndex.toString() + ", " +
                "comment=\"" + comment + "\" " +
                "]";
    }

}
