package gov.noaa.pmel.sdimetadata;

import java.util.TreeSet;

/**
 * Information about the longitude, latitude, and time coverage of data in a dataset
 */
public class Coverage implements Cloneable {

    public static final String WGS84 = "WGS 84";
    /**
     * 1900-01-01 00:00:00 in units of seconds since 1970-01-01 00:00:00
     */
    public static final Double MIN_DATA_TIME = -2208988800.000;

    protected Double westernLongitude;
    protected Double easternLongitude;
    protected Double southernLatitude;
    protected Double northernLatitude;
    protected Double earliestDataTime;
    protected Double latestDataTime;
    protected String spatialReference;
    protected TreeSet<String> geographicNames;

    /**
     * Create with all numberic values assigned as Double.NaN,
     * the spatial reference set to WGS 84, and no geographic names.
     */
    public Coverage() {
        westernLongitude = Double.NaN;
        easternLongitude = Double.NaN;
        southernLatitude = Double.NaN;
        northernLatitude = Double.NaN;
        earliestDataTime = Double.NaN;
        latestDataTime = Double.NaN;
        spatialReference = WGS84;
        geographicNames = new TreeSet<String>();
    }

    /**
     * Create with the given values assigned by their corresponding setters,
     * the spatial reference set to WGS 84, and no geographic names.
     */
    public Coverage(Double westernLongitude, Double easternLongitude,
            Double southernLatitude, Double northernLatitude,
            Double earliestDataTime, Double latestDataTime) {
        this();
        setWesternLongitude(westernLongitude);
        setEasternLongitude(easternLongitude);
        setSouthernLatitude(southernLatitude);
        setNorthernLatitude(northernLatitude);
        setEarliestDataTime(earliestDataTime);
        setLatestDataTime(latestDataTime);
    }

    /**
     * @return the western longitude limit, in units of decimal degrees east in the range (180.0,180.0];
     *         never null but may be Double.NaN
     */
    public Double getWesternLongitude() {
        return westernLongitude;
    }

    /**
     * @param westernLongitude
     *         assign as the western longitude limit, in units of decimal degrees east;
     *         if null or invalid (not in the range [-540.0,540.0]), Double.NaN will be assigned
     */
    public void setWesternLongitude(Double westernLongitude) {
        if ( (westernLongitude != null) && (westernLongitude >= -540.0) && (westernLongitude <= 540.0) ) {
            this.westernLongitude = westernLongitude;
            while ( this.westernLongitude <= -180.0 ) {
                this.westernLongitude += 360.0;
            }
            while ( this.westernLongitude > 180.0 ) {
                this.westernLongitude -= 360.0;
            }
        }
        else
            this.westernLongitude = Double.NaN;
    }

    /**
     * @return the eastern longitude limit, in units of decimal degrees east in the range (-180.0,180.0];
     *         never null but may be Double.NaN
     */
    public Double getEasternLongitude() {
        return easternLongitude;
    }

    /**
     * @param easternLongitude
     *         assign as the eastern longitude limit, in units of decimal degrees east;
     *         if null or invalid (not in the range [-540.0,540.0]), Double.NaN will be assigned
     */
    public void setEasternLongitude(Double easternLongitude) {
        if ( (easternLongitude != null) && (easternLongitude >= -540.0) && (easternLongitude <= 540.0) ) {
            this.easternLongitude = easternLongitude;
            while ( this.easternLongitude <= -180.0 ) {
                this.easternLongitude += 360.0;
            }
            while ( this.easternLongitude > 180.0 ) {
                this.easternLongitude -= 360.0;
            }
        }
        else
            this.easternLongitude = Double.NaN;
    }

    /**
     * @return the southern latitude limit, in units of decimal degrees north;
     *         never null but may be Double.NaN
     */
    public Double getSouthernLatitude() {
        return southernLatitude;
    }

    /**
     * @param southernLatitude
     *         assign as the southern latitude limit, in units of decimal degrees north;
     *         if null or invalid, Double.NaN will be assigned
     */
    public void setSouthernLatitude(Double southernLatitude) {
        if ( (southernLatitude != null) && (southernLatitude >= -90.0) && (southernLatitude <= 90.0) )
            this.southernLatitude = southernLatitude;
        else
            this.southernLatitude = Double.NaN;
    }

    /**
     * @return the northern latitude limit, in units of decimal degrees north;
     *         never null but may be Double.NaN
     */
    public Double getNorthernLatitude() {
        return northernLatitude;
    }

    /**
     * @param northernLatitude
     *         assign as the northern latitude limit, in units of decimal degrees north;
     *         if null or invalid, Double.NaN will be assigned
     */
    public void setNorthernLatitude(Double northernLatitude) {
        if ( (northernLatitude != null) && (northernLatitude >= -90.0) && (northernLatitude <= 90.0) )
            this.northernLatitude = northernLatitude;
        else
            this.northernLatitude = Double.NaN;
    }

    /**
     * @return the earliest (oldest) data time value, in units of second since 01-JAN-1970 00:00:00;
     *         never null but may be Double.NaN
     */
    public Double getEarliestDataTime() {
        return earliestDataTime;
    }

    /**
     * @param earliestDataTime
     *         assign as the earliest (oldest) data time value, in units of second since 01-JAN-1970 00:00:00;
     *         if null or invalid (before 1900-01-01 or after the current time), Double.NaN will be assigned
     */
    public void setEarliestDataTime(Double earliestDataTime) {
        if ( (earliestDataTime != null) && (earliestDataTime >= MIN_DATA_TIME) &&
                (earliestDataTime <= (System.currentTimeMillis() / 1000.0)) )
            this.earliestDataTime = earliestDataTime;
        else
            this.earliestDataTime = Double.NaN;
    }

    /**
     * @return the latest (newest) data time value, in units of second since 01-JAN-1970 00:00:00;
     *         never null but may be Double.NaN
     */
    public Double getLatestDataTime() {
        return latestDataTime;
    }

    /**
     * @param latestDataTime
     *         assign as the latest (newest) data time value, in units of second since 01-JAN-1970 00:00:00;
     *         if null or invalid (before 1900-01-01 or after the current time), Double.NaN will be assigned
     */
    public void setLatestDataTime(Double latestDataTime) {
        if ( (latestDataTime != null) && (latestDataTime >= MIN_DATA_TIME) &&
                (latestDataTime <= (System.currentTimeMillis() / 1000.0)) )
            this.latestDataTime = latestDataTime;
        else
            this.latestDataTime = Double.NaN;
    }

    /**
     * @return the spatial reference; never null but may be empty
     */
    public String getSpatialReference() {
        return spatialReference;
    }

    /**
     * @param spatialReference
     *         assign as the spatial reference; if null, WGS 84 is assigned
     */
    public void setSpatialReference(String spatialReference) {
        this.spatialReference = (spatialReference != null) ? spatialReference.trim() : WGS84;
    }

    /**
     * @return the set of geographic names; never null but may be empty.
     *         Any names given are guaranteed to be non-blank strings.
     */
    public TreeSet<String> getGeographicNames() {
        return new TreeSet<String>(geographicNames);
    }

    /**
     * @param geographicNames
     *         assign as the list of geographic names; if null, an empty set is assigned
     *
     * @throws IllegalArgumentException
     *         if any name given is null or blank
     */
    public void setGeographicNames(Iterable<String> geographicNames) throws IllegalArgumentException {
        this.geographicNames.clear();
        if ( geographicNames != null ) {
            for (String name : geographicNames) {
                if ( name == null )
                    throw new IllegalArgumentException("null geographic region name given");
                name = name.trim();
                if ( name.isEmpty() )
                    throw new IllegalArgumentException("blank geographic region name given");
                this.geographicNames.add(name);
            }
        }
    }

    /**
     * @return if the coverage bounds are all valid
     */
    public boolean isValid() {
        if ( westernLongitude.isNaN() || easternLongitude.isNaN() || southernLatitude.isNaN() ||
                northernLatitude.isNaN() || earliestDataTime.isNaN() || latestDataTime.isNaN() )
            return false;
        // due to modulo nature of longitudes, the western longitude and be larger than the eastern longitude
        if ( southernLatitude > northernLatitude )
            return false;
        if ( earliestDataTime > latestDataTime )
            return false;
        return true;
    }

    @Override
    public Coverage clone() {
        Coverage coverage;
        try {
            coverage = (Coverage) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        coverage.westernLongitude = westernLongitude;
        coverage.easternLongitude = easternLongitude;
        coverage.southernLatitude = southernLatitude;
        coverage.northernLatitude = northernLatitude;
        coverage.earliestDataTime = earliestDataTime;
        coverage.latestDataTime = latestDataTime;
        coverage.spatialReference = spatialReference;
        coverage.geographicNames = new TreeSet<String>(geographicNames);
        return coverage;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Coverage) )
            return false;

        Coverage other = (Coverage) obj;

        if ( !westernLongitude.equals(other.westernLongitude) )
            return false;
        if ( !easternLongitude.equals(other.easternLongitude) )
            return false;
        if ( !southernLatitude.equals(other.southernLatitude) )
            return false;
        if ( !northernLatitude.equals(other.northernLatitude) )
            return false;
        if ( !earliestDataTime.equals(other.earliestDataTime) )
            return false;
        if ( !latestDataTime.equals(other.latestDataTime) )
            return false;
        if ( !spatialReference.equals(other.spatialReference) )
            return false;
        if ( !geographicNames.equals(other.geographicNames) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = westernLongitude.hashCode();
        result = result * prime + easternLongitude.hashCode();
        result = result * prime + southernLatitude.hashCode();
        result = result * prime + northernLatitude.hashCode();
        result = result * prime + earliestDataTime.hashCode();
        result = result * prime + latestDataTime.hashCode();
        result = result * prime + spatialReference.hashCode();
        result = result * prime + geographicNames.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Coverage{" +
                "westernLongitude=" + westernLongitude +
                ", easternLongitude=" + easternLongitude +
                ", southernLatitude=" + southernLatitude +
                ", northernLatitude=" + northernLatitude +
                ", earliestDataTime=" + earliestDataTime +
                ", latestDataTime=" + latestDataTime +
                ", spatialReference='" + spatialReference + "'" +
                ", geographicNames=" + geographicNames +
                '}';
    }

}

