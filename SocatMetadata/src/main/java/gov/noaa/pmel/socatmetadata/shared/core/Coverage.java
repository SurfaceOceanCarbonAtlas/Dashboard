package gov.noaa.pmel.socatmetadata.shared.core;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Information about the longitude, latitude, and time coverage of data in a dataset
 */
public class Coverage implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = 5771797461681037171L;

    public static final String LONGITUDE_UNITS = "dec deg E";
    public static final String LATITUDE_UNITS = "dec deg N";
    public static final String WGS84 = "WGS 84";

    private NumericString westernLongitude;
    private NumericString easternLongitude;
    private NumericString southernLatitude;
    private NumericString northernLatitude;
    private Datestamp earliestDataDate;
    private Datestamp latestDataDate;
    private Datestamp startDatestamp;
    private Datestamp endDatestamp;
    private String spatialReference;
    private MultiNames geographicNames;

    /**
     * Create with empty longitudes, empty latitudes, invalid times,
     * the spatial reference set to WGS 84, and no geographic names.
     */
    public Coverage() {
        westernLongitude = new NumericString(null, LONGITUDE_UNITS);
        easternLongitude = new NumericString(null, LONGITUDE_UNITS);
        southernLatitude = new NumericString(null, LATITUDE_UNITS);
        northernLatitude = new NumericString(null, LATITUDE_UNITS);
        earliestDataDate = new Datestamp();
        latestDataDate = new Datestamp();
        startDatestamp = new Datestamp();
        endDatestamp = new Datestamp();
        spatialReference = WGS84;
        geographicNames = new MultiNames();
    }

    /**
     * @param today
     *         a Datestamp representing the current day; if null, {@link Datestamp#DEFAULT_TODAY_DATESTAMP} is used
     *
     * @return list of field names that are currently invalid
     */
    public HashSet<String> invalidFieldNames(Datestamp today) {
        HashSet<String> invalid = new HashSet<String>();
        if ( !westernLongitude.isValid() )
            invalid.add("westernLongitude");
        if ( !easternLongitude.isValid() )
            invalid.add("easternLongitude");

        if ( southernLatitude.isValid() && northernLatitude.isValid() ) {
            if ( southernLatitude.getNumericValue() > northernLatitude.getNumericValue() ) {
                invalid.add("southernLatitude");
                invalid.add("northernLatitude");
            }
        }
        else {
            if ( !southernLatitude.isValid() )
                invalid.add("southernLatitude");
            if ( !northernLatitude.isValid() )
                invalid.add("northernLatitude");
        }

        if ( earliestDataDate.isValid(today) ) {
            if ( latestDataDate.isValid(today) ) {
                if ( earliestDataDate.after(latestDataDate) ) {
                    invalid.add("earliestDataDate");
                    invalid.add("latestDataDate");
                }
            }
            else
                invalid.add("latestDataDate");
        }
        else {
            invalid.add("earliestDataDate");
            if ( !latestDataDate.isValid(today) )
                invalid.add("latestDataDate");
        }

        if ( startDatestamp.isValid(today) ) {
            if ( endDatestamp.isValid(today) ) {
                if ( startDatestamp.after(endDatestamp) ) {
                    invalid.add("startDatestamp");
                    invalid.add("endDatestamp");
                }
            }
            else {
                invalid.add("endDatestamp");
            }
        }
        else {
            invalid.add("startDatestamp");
            if ( !endDatestamp.isValid(today) )
                invalid.add("endDatestamp");
        }

        // If cruise start date is after first data date, assume the problem is with the cruise start date
        if ( startDatestamp.isValid(today) && earliestDataDate.isValid(today) &&
                earliestDataDate.before(startDatestamp) ) {
            invalid.add("startDatestamp");
        }
        // If cruise start date is after first data date, assume the problem is with the cruise start date
        if ( endDatestamp.isValid(today) && latestDataDate.isValid(today) &&
                latestDataDate.after(endDatestamp) ) {
            invalid.add("endDatestamp");
        }


        return invalid;
    }

    /**
     * @return the western longitude limit; never null but may be empty. If not empty, the longitude is guaranteed
     *         to be a numeric value in the range [-360.0,360.0] and the units will be {@link #LONGITUDE_UNITS}.
     */
    public NumericString getWesternLongitude() {
        return new NumericString(westernLongitude);
    }

    /**
     * @param westernLongitude
     *         assign as the western longitude limit; if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if the numeric string of the given longitude is not empty and not in the range [-360,360], or
     *         if the unit string of the given longitude is not {@link #LONGITUDE_UNITS}
     */
    public void setWesternLongitude(NumericString westernLongitude) throws IllegalArgumentException {
        if ( (westernLongitude != null) && westernLongitude.isValid() ) {
            double val = westernLongitude.getNumericValue();
            if ( (val < -360.0) || (val > 360.0) )
                throw new IllegalArgumentException("westernmost longitude is not in [-360.0,360.0]");
            if ( !LONGITUDE_UNITS.equals(westernLongitude.getUnitString()) )
                throw new IllegalArgumentException("westernmost longitude units are not " + LONGITUDE_UNITS);
            this.westernLongitude = new NumericString(westernLongitude);
        }
        else
            this.westernLongitude = new NumericString(null, LONGITUDE_UNITS);
    }

    /**
     * @return the eastern longitude limit; never null but may be empty. If not empty, the longitude is guaranteed
     *         to be a numeric value in the range [-360.0,360.0] and the units will be {@link #LONGITUDE_UNITS}
     */
    public NumericString getEasternLongitude() {
        return new NumericString(easternLongitude);
    }

    /**
     * @param easternLongitude
     *         assign as the eastern longitude limit; if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if the numeric string of the given longitude is not empty and not in the range [-360,360], or
     *         if the unit string of the given longitude is not {@link #LONGITUDE_UNITS}
     */
    public void setEasternLongitude(NumericString easternLongitude) throws IllegalArgumentException {
        if ( (easternLongitude != null) && easternLongitude.isValid() ) {
            double val = easternLongitude.getNumericValue();
            if ( (val < -360.0) || (val > 360.0) )
                throw new IllegalArgumentException("easternmost longitude is not in [-360.0,360.0]");
            if ( !LONGITUDE_UNITS.equals(easternLongitude.getUnitString()) )
                throw new IllegalArgumentException("easternmost longitude units are not " + LONGITUDE_UNITS);
            this.easternLongitude = new NumericString(easternLongitude);
        }
        else
            this.easternLongitude = new NumericString(null, LONGITUDE_UNITS);
    }

    /**
     * @return the southern latitude limit; never null but may be empty. If not empty, the latitude is guaranteed
     *         to be a numeric value in the range [-90.0,90.0] and the units will be {@link #LATITUDE_UNITS}
     */
    public NumericString getSouthernLatitude() {
        return new NumericString(southernLatitude);
    }

    /**
     * @param southernLatitude
     *         assign as the southern latitude limit; if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if the numeric string of the given latitude is not empty and not in the range [-90,90], or
     *         if the unit string of the given latitude is not {@link #LATITUDE_UNITS}
     */
    public void setSouthernLatitude(NumericString southernLatitude) throws IllegalArgumentException {
        if ( (southernLatitude != null) && southernLatitude.isValid() ) {
            double val = southernLatitude.getNumericValue();
            if ( (val < -90.0) || (val > 90.0) )
                throw new IllegalArgumentException("southernmost latitude is not in [-90.0,90.0]");
            if ( !LATITUDE_UNITS.equals(southernLatitude.getUnitString()) )
                throw new IllegalArgumentException("southernLatitude longitude units are not " + LATITUDE_UNITS);
            this.southernLatitude = new NumericString(southernLatitude);
        }
        else
            this.southernLatitude = new NumericString(null, LATITUDE_UNITS);
    }

    /**
     * @return the northern latitude limit; never null but may be empty. If not empty, the latitude is guaranteed
     *         to be a numeric value in the range [-90.0,90.0] and the units will be {@link #LATITUDE_UNITS}
     */
    public NumericString getNorthernLatitude() {
        return new NumericString(northernLatitude);
    }

    /**
     * @param northernLatitude
     *         assign as the northern latitude limit; if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if the numeric string of the given latitude is not empty and not in the range [-90,90], or
     *         if the unit string of the given latitude is not {@link #LATITUDE_UNITS}
     */
    public void setNorthernLatitude(NumericString northernLatitude) {
        if ( (northernLatitude != null) && northernLatitude.isValid() ) {
            double val = northernLatitude.getNumericValue();
            if ( (val < -90.0) || (val > 90.0) )
                throw new IllegalArgumentException("northernmost latitude is not in [-90.0,90.0]");
            if ( !LATITUDE_UNITS.equals(northernLatitude.getUnitString()) )
                throw new IllegalArgumentException("northernLatitude longitude units are not " + LATITUDE_UNITS);
            this.northernLatitude = new NumericString(northernLatitude);
        }
        else
            this.northernLatitude = new NumericString(null, LATITUDE_UNITS);
    }

    /**
     * @return the earliest (oldest) data time value; never null but may be an invalid date
     */
    public Datestamp getEarliestDataDate() {
        return new Datestamp(earliestDataDate);
    }

    /**
     * @param earliestDataDate
     *         assign as the earliest (oldest) data time value;
     *         if null, an invalid Datestamp is assigned
     */
    public void setEarliestDataDate(Datestamp earliestDataDate) {
        this.earliestDataDate = new Datestamp(earliestDataDate);
    }

    /**
     * @return the latest (newest) data time value; never null but may be an invalid date
     */
    public Datestamp getLatestDataDate() {
        return new Datestamp(latestDataDate);
    }

    /**
     * @param latestDataDate
     *         assign as the latest (newest) data time value;
     *         if null, a invalid Datestamp is assigned
     */
    public void setLatestDataDate(Datestamp latestDataDate) {
        this.latestDataDate = new Datestamp(latestDataDate);
    }

    /**
     * @return the starting date for this dataset; never null but may be an invalid Datestamp
     */
    public Datestamp getStartDatestamp() {
        return new Datestamp(startDatestamp);
    }

    /**
     * @param startDatestamp
     *         assign as the starting date for this dataset;
     *         if null, an invalid Datestamp will be assigned.
     */
    public void setStartDatestamp(Datestamp startDatestamp) {
        this.startDatestamp = new Datestamp(startDatestamp);
    }

    /**
     * @return the ending date for this dataset; never null but may be an invalid Datestamp
     */
    public Datestamp getEndDatestamp() {
        return new Datestamp(endDatestamp);
    }

    /**
     * @param endDatestamp
     *         assign as the ending date for this dataset;
     *         if null, an invalid Datestamp will be assigned.
     */
    public void setEndDatestamp(Datestamp endDatestamp) {
        this.endDatestamp = new Datestamp(endDatestamp);
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
        if ( this.spatialReference.isEmpty() )
            this.spatialReference = WGS84;
    }

    /**
     * @return the set of geographic names; never null but may be empty.
     */
    public MultiNames getGeographicNames() {
        return new MultiNames(geographicNames);
    }

    /**
     * @param geographicNames
     *         assign as the set of geographic names; if null, an empty name set is assigned
     */
    public void setGeographicNames(MultiNames geographicNames) {
        this.geographicNames = new MultiNames(geographicNames);
    }

    @Override
    public Object duplicate(Object dup) {
        Coverage coverage;
        if ( dup == null )
            coverage = new Coverage();
        else
            coverage = (Coverage) dup;
        coverage.westernLongitude = new NumericString(westernLongitude);
        coverage.easternLongitude = new NumericString(easternLongitude);
        coverage.southernLatitude = new NumericString(southernLatitude);
        coverage.northernLatitude = new NumericString(northernLatitude);
        coverage.earliestDataDate = new Datestamp(earliestDataDate);
        coverage.latestDataDate = new Datestamp(latestDataDate);
        coverage.startDatestamp = new Datestamp(startDatestamp);
        coverage.endDatestamp = new Datestamp(endDatestamp);
        coverage.spatialReference = spatialReference;
        coverage.geographicNames = new MultiNames(geographicNames);
        return coverage;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = westernLongitude.hashCode();
        result = result * prime + easternLongitude.hashCode();
        result = result * prime + southernLatitude.hashCode();
        result = result * prime + northernLatitude.hashCode();
        result = result * prime + earliestDataDate.hashCode();
        result = result * prime + latestDataDate.hashCode();
        result = result * prime + startDatestamp.hashCode();
        result = result * prime + endDatestamp.hashCode();
        result = result * prime + spatialReference.hashCode();
        result = result * prime + geographicNames.hashCode();
        return result;
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
        if ( !earliestDataDate.equals(other.earliestDataDate) )
            return false;
        if ( !latestDataDate.equals(other.latestDataDate) )
            return false;
        if ( !startDatestamp.equals(other.startDatestamp) )
            return false;
        if ( !endDatestamp.equals(other.endDatestamp) )
            return false;
        if ( !spatialReference.equals(other.spatialReference) )
            return false;
        if ( !geographicNames.equals(other.geographicNames) )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Coverage" +
                "{ westernLongitude=" + westernLongitude +
                ", easternLongitude=" + easternLongitude +
                ", southernLatitude=" + southernLatitude +
                ", northernLatitude=" + northernLatitude +
                ", earliestDataDate=" + earliestDataDate +
                ", latestDataDate=" + latestDataDate +
                ", startDatestamp=" + startDatestamp +
                ", endDatestamp=" + endDatestamp +
                ", spatialReference='" + spatialReference + "'" +
                ", geographicNames=" + geographicNames +
                " }";
    }

}
