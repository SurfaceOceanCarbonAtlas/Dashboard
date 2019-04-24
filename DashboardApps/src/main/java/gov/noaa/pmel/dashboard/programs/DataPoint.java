package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Simple class for collecting and sorting time/lat/lon/sst/sal/fco2rec data
 */
public class DataPoint implements Comparable<DataPoint> {
    private static final Double MISSING_VALUE = -999.0;

    /**
     * Jan 1, 1940 - reasonable lower limit on data dates
     */
    private static final Date EARLIEST_DATE;
    private static final SimpleDateFormat DATETIMESTAMPER;

    static {
        DATETIMESTAMPER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DATETIMESTAMPER.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            EARLIEST_DATE = DATETIMESTAMPER.parse("1940-01-01 00:00:00");
        } catch ( ParseException ex ) {
            throw new RuntimeException(ex);
        }
    }

    private final Date datetime;
    final Double latitude;
    final Double longitude;
    final Double sst;
    final Double sal;
    final Double fco2rec;

    /**
     * @param expocode
     *         dataset expocode, only used for error message when raising exceptions
     * @param sectime
     *         measurement time in seconds since Jan 1, 1970 00:00:00; must be a valid value
     * @param latitude
     *         measurement latitude in decimal degrees north; must be a valid value
     * @param longitude
     *         measurment longitude in decimal degrees east in the range [-180,180]; must be a valid value
     * @param sst
     *         measurement SST in degrees C; if null or {@link DashboardUtils#FP_MISSING_VALUE},
     *         {@link #MISSING_VALUE} is assigned
     * @param sal
     *         measurement salinity in PSU; if null or {@link DashboardUtils#FP_MISSING_VALUE},
     *         {@link #MISSING_VALUE} is assigned
     * @param fco2rec
     *         measurement recommended fCO2; must be valid value
     *
     * @throws IllegalArgumentException
     *         if the sectime, latitude, longitude, sst, sal, or fco2rec values are invalid
     */
    public DataPoint(String expocode, Double sectime, Double latitude, Double longitude,
            Double sst, Double sal, Double fco2rec) throws IllegalArgumentException {
        if ( sectime == null )
            throw new IllegalArgumentException("null time for " + expocode);
        datetime = new Date(Math.round(sectime * 1000.0));
        if ( datetime.before(EARLIEST_DATE) || datetime.after(new Date()) )
            throw new IllegalArgumentException("invalid time of " + datetime + " for " + expocode);

        if ( latitude == null )
            throw new IllegalArgumentException("null latitude for " + expocode);
        if ( (latitude < -90.0) || (latitude > 90.0) )
            throw new IllegalArgumentException("invalid latitude of " + latitude + " for " + expocode);
        this.latitude = latitude;

        if ( longitude == null )
            throw new IllegalArgumentException("null longitude for " + expocode);
        if ( (longitude < -180.0) || (longitude > 180.0) )
            throw new IllegalArgumentException("invalid longitude of " + longitude + " for " + expocode);
        this.longitude = longitude;

        if ( fco2rec == null )
            throw new IllegalArgumentException("null fco2rec for " + expocode);
        if ( (fco2rec < 0.0) || (fco2rec > 100000.0) )
            throw new IllegalArgumentException("invalid fCO2rec of " + fco2rec + " for " + expocode);
        this.fco2rec = fco2rec;

        if ( (sst == null) || DashboardUtils.FP_MISSING_VALUE.equals(sst) ) {
            this.sst = MISSING_VALUE;
        }
        else {
            if ( (sst < -10.0) || (sst > 60.0) )
                throw new IllegalArgumentException("invalid SST of " + sst + " for " + expocode);
            this.sst = sst;
        }

        if ( (sal == null) || DashboardUtils.FP_MISSING_VALUE.equals(sal) ) {
            this.sal = MISSING_VALUE;
        }
        else {
            if ( (sal < -10.0) || (sal > 100.0) )
                throw new IllegalArgumentException("invalid salinity of " + sal + " for " + expocode);
            this.sal = sal;
        }
    }

    /**
     * @return the date and time String in the format "yyyy-MM-dd HH:mm:ss"
     */
    public String getDateTimeString() {
        return DATETIMESTAMPER.format(datetime);
    }

    @Override
    public int compareTo(DataPoint other) {
        // the primary sort must be on datetime
        int result = datetime.compareTo(other.datetime);
        if ( result != 0 )
            return result;
        result = latitude.compareTo(other.latitude);
        if ( result != 0 )
            return result;
        result = longitude.compareTo(other.longitude);
        if ( result != 0 )
            return result;
        result = fco2rec.compareTo(other.fco2rec);
        if ( result != 0 )
            return result;
        result = sst.compareTo(other.sst);
        if ( result != 0 )
            return result;
        result = sal.compareTo(other.sal);
        if ( result != 0 )
            return result;
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        result = prime * result + datetime.hashCode();
        result = prime * result + latitude.hashCode();
        result = prime * result + longitude.hashCode();
        result = prime * result + fco2rec.hashCode();
        result = prime * result + sst.hashCode();
        result = prime * result + sal.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( !(obj instanceof DataPoint) )
            return false;
        DataPoint other = (DataPoint) obj;
        if ( !datetime.equals(other.datetime) )
            return false;
        if ( !latitude.equals(other.latitude) )
            return false;
        if ( !longitude.equals(other.longitude) )
            return false;
        if ( !fco2rec.equals(other.fco2rec) )
            return false;
        if ( !sst.equals(other.sst) )
            return false;
        if ( !sal.equals(other.sal) )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[ datetime=" + getDateTimeString() +
                ", latitude=" + String.format("%#.6f", latitude) +
                ", longitude=" + String.format("%#.6f", longitude) +
                ", fco2rec=" + String.format("%#.6f", fco2rec) +
                ", sst =" + String.format("%#.6f", sst) +
                ", sal =" + String.format("%#.6f", sal) +
                " ]";
    }

}
