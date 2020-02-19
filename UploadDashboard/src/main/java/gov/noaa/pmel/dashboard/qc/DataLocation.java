package gov.noaa.pmel.dashboard.qc;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.Comparator;
import java.util.Date;

/**
 * The location of a data point with a data value at that location. Used for indicating locations for QC flag events
 * which describes the data set and data column for this location and value. Also used for reordering data rows.
 *
 * @author Karl Smith
 */
public class DataLocation implements Comparable<DataLocation> {

    protected Integer rowNumber;
    protected Date dataDate;
    protected Double longitude;
    protected Double latitude;
    protected Double dataValue;

    /**
     * Creates an empty location (all values set to the appropriate missing value)
     */
    public DataLocation() {
        rowNumber = DashboardUtils.INT_MISSING_VALUE;
        dataDate = DashboardUtils.DATE_MISSING_VALUE;
        longitude = DashboardUtils.FP_MISSING_VALUE;
        latitude = DashboardUtils.FP_MISSING_VALUE;
        dataValue = DashboardUtils.FP_MISSING_VALUE;
    }

    /**
     * @return the data row number; never null but may be {@link DashboardUtils#INT_MISSING_VALUE}
     */
    public Integer getRowNumber() {
        return rowNumber;
    }

    /**
     * @param rowNumber
     *         the data row number to set; if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
     */
    public void setRowNumber(Integer rowNumber) {
        if ( rowNumber == null )
            this.rowNumber = DashboardUtils.INT_MISSING_VALUE;
        else
            this.rowNumber = rowNumber;
    }

    /**
     * @return the data date; never null but may be {@link DashboardUtils#DATE_MISSING_VALUE}
     */
    public Date getDataDate() {
        return dataDate;
    }

    /**
     * @param dataDate
     *         the data date to set; if null, {@link DashboardUtils#DATE_MISSING_VALUE} is assigned.
     */
    public void setDataDate(Date dataDate) {
        if ( dataDate == null )
            this.dataDate = DashboardUtils.DATE_MISSING_VALUE;
        else
            this.dataDate = dataDate;
    }

    /**
     * @return the longitude in the range [-180.0, 180.0);
     *         never null but may be {@link DashboardUtils#FP_MISSING_VALUE}
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude
     *         the longitude to set, which will be adjust the range [-180.0, 180.0);
     *         if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned.
     */
    public void setLongitude(Double longitude) {
        if ( longitude == null ) {
            this.longitude = DashboardUtils.FP_MISSING_VALUE;
        }
        else {
            this.longitude = longitude;
            while ( this.longitude >= 180.0 ) {
                this.longitude -= 360.0;
            }
            while ( this.longitude < -180.0 ) {
                this.longitude += 360.0;
            }
        }
    }

    /**
     * @return the latitude; never null but may be {@link DashboardUtils#FP_MISSING_VALUE}
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude
     *         the latitude to set; if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned.
     */
    public void setLatitude(Double latitude) {
        if ( latitude == null )
            this.latitude = DashboardUtils.FP_MISSING_VALUE;
        else
            this.latitude = latitude;
    }

    /**
     * @return the data value; never null but may be {@link DashboardUtils#FP_MISSING_VALUE}
     */
    public Double getDataValue() {
        return dataValue;
    }

    /**
     * @param dataValue
     *         the data value to set; if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned.
     */
    public void setDataValue(Double dataValue) {
        if ( dataValue == null )
            this.dataValue = DashboardUtils.FP_MISSING_VALUE;
        else
            this.dataValue = dataValue;
    }

    /**
     * Compares in the order:
     * <ol>
     * <li>date</li>
     * <li>longitude</li>
     * <li>latitude</li>
     * <li>data value</li>
     * <li>row number</li>
     * </ol>
     * All comparisons are made using the compareTo method of each type (Date, Double, Integer),
     * and as such do not account for longitude modulo or insignificant floating-point differences
     * as is done in the {@link #equals(Object)} method.  Missing values are compared using their
     * actual value (which should be low).
     */
    @Override
    public int compareTo(DataLocation other) {
        int result = dataDate.compareTo(other.dataDate);
        if ( result != 0 )
            return result;
        result = longitude.compareTo(other.longitude);
        if ( result != 0 )
            return result;
        result = latitude.compareTo(other.latitude);
        if ( result != 0 )
            return result;
        result = dataValue.compareTo(other.dataValue);
        if ( result != 0 )
            return result;
        result = rowNumber.compareTo(other.rowNumber);
        if ( result != 0 )
            return result;
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = rowNumber.hashCode();
        result = result * prime + dataDate.hashCode();
        // Ignore floating point values as they do not have to be exactly the same for equals
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;

        if ( !(obj instanceof DataLocation) )
            return false;
        DataLocation other = (DataLocation) obj;

        if ( !rowNumber.equals(other.rowNumber) )
            return false;
        if ( !dataDate.equals(other.dataDate) )
            return false;

        if ( !DashboardUtils.closeTo(dataValue, other.dataValue,
                DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
            return false;
        if ( !DashboardUtils.closeTo(latitude, other.latitude, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
            return false;
        if ( !DashboardUtils.longitudeCloseTo(longitude, other.longitude, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "DataLocation" +
                "[ rowNumber=" + rowNumber.toString() +
                ", dataTime=" + Long.toString(Math.round((dataDate.getTime() / 1000.0))) +
                ", longitude=" + longitude.toString() +
                ", latitude=" + latitude.toString() +
                ", dataValue=" + dataValue.toString() +
                "]";
    }

    /**
     * Comparator that ignores the row numbers when comparing.  Note that this is inconsistent with
     * {@link #compareTo(DataLocation)} in that row numbers are ignored.  This is also inconsistent with
     * {@link #equals(Object)} in that row numbers are ignored, longitude modulo is not considered,
     * and negligible differences in floating point values are not ignored.
     */
    public static final Comparator<DataLocation> IGNORE_ROW_NUM_COMPARATOR = new Comparator<DataLocation>() {
        @Override
        public int compare(DataLocation first, DataLocation second) {
            int result = first.dataDate.compareTo(second.dataDate);
            if ( result != 0 )
                return result;
            result = first.longitude.compareTo(second.longitude);
            if ( result != 0 )
                return result;
            result = first.latitude.compareTo(second.latitude);
            if ( result != 0 )
                return result;
            result = first.dataValue.compareTo(second.dataValue);
            if ( result != 0 )
                return result;
            return 0;
        }
    };

}
