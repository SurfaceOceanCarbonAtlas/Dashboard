/**
 *
 */
package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Parts of a automated data checker Message object that needs to be transferred to the client for display.
 *
 * @author Karl Smith
 */
public class ADCMessage implements Serializable, IsSerializable {

    private static final long serialVersionUID = -4524821044077234564L;

    protected Severity severity;
    protected Integer rowNumber;
    protected Double longitude;
    protected Double latitude;
    protected Double depth;
    protected String timestamp;
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
        longitude = DashboardUtils.FP_MISSING_VALUE;
        latitude = DashboardUtils.FP_MISSING_VALUE;
        depth = DashboardUtils.FP_MISSING_VALUE;
        timestamp = DashboardUtils.STRING_MISSING_VALUE;
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
     *         the data row number to set; if null or invalid (not in [1,999999]), {@link
     *         DashboardUtils#INT_MISSING_VALUE} is assigned.
     */
    public void setRowNumber(Integer rowNumber) {
        if ( (rowNumber == null) || (rowNumber < 1) || (rowNumber > 999999) )
            this.rowNumber = DashboardUtils.INT_MISSING_VALUE;
        else
            this.rowNumber = rowNumber;
    }

    /**
     * @return the longitude; never null, but may be {@link DashboardUtils#FP_MISSING_VALUE} if not available.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude
     *         the longitude to set; if null or invalid (not in [-540,540]), {@link DashboardUtils#FP_MISSING_VALUE} is
     *         assigned.
     */
    public void setLongitude(Double longitude) {
        if ( (longitude == null) || longitude.isInfinite() || longitude.isNaN() ||
                (longitude < -540.0) || (longitude > 540.0) )
            this.longitude = DashboardUtils.FP_MISSING_VALUE;
        else
            this.longitude = longitude;
    }

    /**
     * @return the latitude; never null, but may be {@link DashboardUtils#FP_MISSING_VALUE} if not available.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude
     *         the latitude to set; if null or invalid (not in [-90,90]), {@link DashboardUtils#FP_MISSING_VALUE} is
     *         assigned.
     */
    public void setLatitude(Double latitude) {
        if ( (latitude == null) || latitude.isInfinite() || latitude.isNaN() ||
                (latitude < -90.0) || (latitude > 90.0) )
            this.latitude = DashboardUtils.FP_MISSING_VALUE;
        else
            this.latitude = latitude;
    }

    /**
     * @return the sample depth; never null, but may be {@link DashboardUtils#FP_MISSING_VALUE} if not available.
     */
    public Double getDepth() {
        return depth;
    }

    /**
     * @param depth
     *         the sample depth to set; if null or invalid (not in [0,16000]), {@link DashboardUtils#FP_MISSING_VALUE}
     *         is assigned.
     */
    public void setDepth(Double depth) {
        if ( (depth == null) || depth.isInfinite() || depth.isNaN() ||
                (depth < 0.0) || (depth > 16000) )
            this.depth = DashboardUtils.FP_MISSING_VALUE;
        else
            this.depth = depth;
    }

    /**
     * @return the timestamp; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not available.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     *         the timestamp to set; if null,
     *
     * @link DashboardUtils#STRING_MISSING_VALUE} is assigned.
     */
    public void setTimestamp(String timestamp) {
        if ( timestamp == null )
            this.timestamp = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.timestamp = timestamp;
    }

    /**
     * @return the input data column number; never null but may be {@link DashboardUtils#INT_MISSING_VALUE} if not
     * available.
     */
    public Integer getColNumber() {
        return colNumber;
    }

    /**
     * @param colNumber
     *         the input data column number to set; if null or invalid (not in [1,999]), {@link
     *         DashboardUtils#INT_MISSING_VALUE} is assigned.
     */
    public void setColNumber(Integer colNumber) {
        if ( (colNumber == null) || (colNumber < 1) || (colNumber > 999) )
            this.colNumber = DashboardUtils.INT_MISSING_VALUE;
        else
            this.colNumber = colNumber;
    }

    /**
     * @return the input data column name; never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE} if not
     * available.
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
     * @return the automated data checker general explanation of the issue; never null, but may be {@link
     * DashboardUtils#STRING_MISSING_VALUE} if not available.
     */
    public String getGeneralComment() {
        return generalComment;
    }

    /**
     * @param generalComment
     *         the automated data checker general explanation of the issue to set; if null, {@link
     *         DashboardUtils#STRING_MISSING_VALUE} is assigned.
     */
    public void setGeneralComment(String generalComment) {
        if ( generalComment == null )
            this.generalComment = DashboardUtils.STRING_MISSING_VALUE;
        else
            this.generalComment = generalComment;
    }

    /**
     * @return the automated data checker detailed explanation of the issue; never null, but may be {@link
     * DashboardUtils#STRING_MISSING_VALUE} if not available.
     */
    public String getDetailedComment() {
        return detailedComment;
    }

    /**
     * @param detailedComment
     *         the automated data checker detailed explanation of the issue to set; if null, {@link
     *         DashboardUtils#STRING_MISSING_VALUE} is assigned.
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
        result = result * prime + timestamp.hashCode();
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
        if ( !timestamp.equals(other.timestamp) )
            return false;
        if ( !colNumber.equals(other.colNumber) )
            return false;
        if ( !colName.equals(other.colName) )
            return false;
        if ( !generalComment.equals(other.generalComment) )
            return false;
        if ( !detailedComment.equals(other.detailedComment) )
            return false;
        if ( !DashboardUtils.closeTo(depth, other.depth, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
            return false;
        if ( !DashboardUtils.closeTo(latitude, other.latitude, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
            return false;
        if ( !DashboardUtils.longitudeCloseTo(longitude, other.longitude, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "ADCMessage[severity=" + severity.toString() +
                ", rowNumber=" + rowNumber.toString() +
                ", longitude=" + longitude.toString() +
                ", latitude=" + latitude.toString() +
                ", depth=" + depth.toString() +
                ", timestamp=" + timestamp +
                ", colNumber=" + colNumber.toString() +
                ", colName=" + colName +
                ", generalComment=" + generalComment +
                ", detailedComment=" + detailedComment + "]";
    }

    /**
     * Compare using the severities of the messages. Note that this is inconsistent with {@link
     * ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage.
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
     * Compare using the row numbers of the messages. Note that this is inconsistent with {@link
     * ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage.
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
     * Compare using the longitudes of the messages. Note that this is inconsistent with {@link
     * ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage. This also does not allow some
     * "slop" for two floating point values to be equal, nor does it take into account the modulo 360 nature of
     * longitudes.
     */
    public static Comparator<ADCMessage> longitudeComparator = new Comparator<ADCMessage>() {
        @Override
        public int compare(ADCMessage msg1, ADCMessage msg2) {
            if ( msg1 == msg2 )
                return 0;
            if ( msg1 == null )
                return -1;
            if ( msg2 == null )
                return 1;
            return msg1.longitude.compareTo(msg2.longitude);
        }
    };

    /**
     * Compare using the latitudes of the messages. Note that this is inconsistent with {@link
     * ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage. This also does not allow some
     * "slop" for two floating point values to be equal.
     */
    public static Comparator<ADCMessage> latitudeComparator = new Comparator<ADCMessage>() {
        @Override
        public int compare(ADCMessage msg1, ADCMessage msg2) {
            if ( msg1 == msg2 )
                return 0;
            if ( msg1 == null )
                return -1;
            if ( msg2 == null )
                return 1;
            return msg1.latitude.compareTo(msg2.latitude);
        }
    };

    /**
     * Compare using the depths of the messages. Note that this is inconsistent with {@link ADCMessage#equals(Object)}
     * in that this is only examining one field of ADCMessage. This also does not allow some "slop" for two floating
     * point values to be equal.
     */
    public static Comparator<ADCMessage> depthComparator = new Comparator<ADCMessage>() {
        @Override
        public int compare(ADCMessage msg1, ADCMessage msg2) {
            if ( msg1 == msg2 )
                return 0;
            if ( msg1 == null )
                return -1;
            if ( msg2 == null )
                return 1;
            return msg1.depth.compareTo(msg2.depth);
        }
    };

    /**
     * Compare using the timestamp strings of the messages. Note that this is inconsistent with {@link
     * ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage.
     */
    public static Comparator<ADCMessage> timestampComparator = new Comparator<ADCMessage>() {
        @Override
        public int compare(ADCMessage msg1, ADCMessage msg2) {
            if ( msg1 == msg2 )
                return 0;
            if ( msg1 == null )
                return -1;
            if ( msg2 == null )
                return 1;
            return msg1.timestamp.compareTo(msg2.timestamp);
        }
    };

    /**
     * Compare using the column numbers of the messages. Note that this is inconsistent with {@link
     * ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage.
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
     * Compare using the column names of the messages. Note that this is inconsistent with {@link
     * ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage.
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
     * Compare using the detailed comments of the messages. Note that this is inconsistent with {@link
     * ADCMessage#equals(Object)} in that this is only examining one field of ADCMessage.
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
