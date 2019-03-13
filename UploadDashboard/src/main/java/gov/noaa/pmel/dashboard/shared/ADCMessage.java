/**
 *
 */
package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

import java.io.Serializable;

/**
 * Parts of a automated data checker Message object that needs to be transferred to the client for display.
 *
 * @author Karl Smith
 */
public class ADCMessage implements Serializable, IsSerializable {

    private static final long serialVersionUID = 7102386783315203802L;

    protected Severity severity;
    protected Integer rowNumber;
    protected Integer colNumber;
    protected String colName;
    protected String generalComment;
    protected String detailedComment;

    /**
     * Create an empty message of unknown severity
     */
    public ADCMessage() {
        severity = Severity.UNASSIGNED;
        rowNumber = DashboardUtils.INT_MISSING_VALUE;
        colNumber = DashboardUtils.INT_MISSING_VALUE;
        colName = DashboardUtils.STRING_MISSING_VALUE;
        generalComment = DashboardUtils.STRING_MISSING_VALUE;
        detailedComment = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return the severity of the message; never null.
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * @param severity
     *         the severity of the message to set; if null, {@link Severity#UNASSIGNED} is assigned.
     */
    public void setSeverity(Severity severity) {
        if ( severity == null )
            this.severity = Severity.UNASSIGNED;
        else
            this.severity = severity;
    }

    /**
     * @return the data row number; never null, but may be {@link DashboardUtils#INT_MISSING_VALUE} if not available.
     */
    public Integer getRowNumber() {
        return rowNumber;
    }

    /**
     * @param rowNumber
     *         the data row number to set; if null or invalid (not in [1,999999]),
     *         {@link DashboardUtils#INT_MISSING_VALUE} is assigned.
     */
    public void setRowNumber(Integer rowNumber) {
        if ( (rowNumber == null) || (rowNumber < 1) || (rowNumber > 999999) )
            this.rowNumber = DashboardUtils.INT_MISSING_VALUE;
        else
            this.rowNumber = rowNumber;
    }

    /**
     * @return the input data column number; never null but may be
     *         {@link DashboardUtils#INT_MISSING_VALUE} if not available.
     */
    public Integer getColNumber() {
        return colNumber;
    }

    /**
     * @param colNumber
     *         the input data column number to set; if null or invalid (not in [1,999]),
     *         {@link DashboardUtils#INT_MISSING_VALUE} is assigned.
     */
    public void setColNumber(Integer colNumber) {
        if ( (colNumber == null) || (colNumber < 1) || (colNumber > 999) )
            this.colNumber = DashboardUtils.INT_MISSING_VALUE;
        else
            this.colNumber = colNumber;
    }

    /**
     * @return the input data column name; never null, but may be
     *         {@link DashboardUtils#STRING_MISSING_VALUE} if not available.
     */
    public String getColName() {
        return colName;
    }

    /**
     * @param colName
     *         the input data column name to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned.
     */
    public void setColName(String colName) {
        if ( colName == null )
            this.colName = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.colName = colName;
    }

    /**
     * @return the automated data checker general explanation of the issue; never null,
     *         but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not available.
     */
    public String getGeneralComment() {
        return generalComment;
    }

    /**
     * @param generalComment
     *         the automated data checker general explanation of the issue to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned.
     */
    public void setGeneralComment(String generalComment) {
        if ( generalComment == null )
            this.generalComment = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.generalComment = generalComment;
    }

    /**
     * @return the automated data checker detailed explanation of the issue; never null,
     *         but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not available.
     */
    public String getDetailedComment() {
        return detailedComment;
    }

    /**
     * @param detailedComment
     *         the automated data checker detailed explanation of the issue to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned.
     */
    public void setDetailedComment(String detailedComment) {
        if ( detailedComment == null )
            this.detailedComment = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.detailedComment = detailedComment;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = severity.hashCode();
        result = result * prime + rowNumber.hashCode();
        result = result * prime + colNumber.hashCode();
        result = result * prime + colName.hashCode();
        result = result * prime + generalComment.hashCode();
        result = result * prime + detailedComment.hashCode();
        // Do not use floating point values for the hash code
        // since they do not have to be exactly equal
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;

        if ( !(obj instanceof ADCMessage) )
            return false;
        ADCMessage other = (ADCMessage) obj;

        if ( !severity.equals(other.severity) )
            return false;
        if ( !rowNumber.equals(other.rowNumber) )
            return false;
        if ( !colNumber.equals(other.colNumber) )
            return false;
        if ( !colName.equals(other.colName) )
            return false;
        if ( !generalComment.equals(other.generalComment) )
            return false;
        if ( !detailedComment.equals(other.detailedComment) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "ADCMessage[severity=" + severity.toString() +
                ", rowNumber=" + rowNumber.toString() +
                ", colNumber=" + colNumber.toString() +
                ", colName=" + colName +
                ", generalComment=" + generalComment +
                ", detailedComment=" + detailedComment + "]";
    }

}
