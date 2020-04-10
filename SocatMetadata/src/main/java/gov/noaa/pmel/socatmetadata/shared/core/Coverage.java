package gov.noaa.pmel.socatmetadata.shared.core;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Information about the longitude, latitude, and time coverage of data in a dataset
 */
public class Coverage implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = 5044426323221168294L;

    public static final String LONGITUDE_UNITS = "dec deg E";
    public static final String LATITUDE_UNITS = "dec deg N";
    public static final String WGS84 = "WGS 84";

    protected NumericString westernLongitude;
    protected NumericString easternLongitude;
    protected NumericString southernLatitude;
    protected NumericString northernLatitude;
    protected Datestamp earliestDataDate;
    protected Datestamp latestDataDate;
    protected String spatialReference;
    protected TreeSet<String> geographicNames;

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
        spatialReference = WGS84;
        geographicNames = new TreeSet<String>();
    }

    /**
     * Create with the given values, the spatial reference set to WGS 84, and no geographic names.
     *
     * @param westernLongitude
     *         westernmost longitude in decimal degrees east; if null or blank, an empty numeric string is assigned
     * @param easternLongitude
     *         easternmost longitude in decimal degrees east; if null or blank, an empty numeric string is assigned
     * @param southernLatitude
     *         southernmost latitude in decimal degrees north; if null or blank, an empty numeric string is assigned
     * @param northernLatitude
     *         northernmost latitude in decimal degrees north; if null or blank, an empty numeric string is assigned
     * @param earliestDataDate
     *         date of the earliest (oldest) data measurement; if null or blank, an invalid time is assigned
     * @param latestDataDate
     *         date of the latest (newest) data measurement; if null or blank, an invalid time is assigned
     *
     * @throws IllegalArgumentException
     *         if any of the values, if not null or blank, for the longitudes, latitudes, or times are invalid
     *         (longitudes outside the range [-360, 360] are considered in invalid)
     */
    public Coverage(String westernLongitude, String easternLongitude, String southernLatitude, String northernLatitude,
            Datestamp earliestDataDate, Datestamp latestDataDate) throws IllegalArgumentException {
        this();
        setWesternLongitude(new NumericString(westernLongitude, LONGITUDE_UNITS));
        setEasternLongitude(new NumericString(easternLongitude, LONGITUDE_UNITS));
        setSouthernLatitude(new NumericString(southernLatitude, LATITUDE_UNITS));
        setNorthernLatitude(new NumericString(northernLatitude, LATITUDE_UNITS));
        setEarliestDataDate(earliestDataDate);
        setLatestDataDate(latestDataDate);
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

        return invalid;
    }

    /**
     * @return the western longitude limit; never null but may be empty. If not empty, the longitude is guaranteed
     *         to be a numeric value in the range [-360.0,360.0] and the units will be {@link #LONGITUDE_UNITS}.
     */
    public NumericString getWesternLongitude() {
        return (NumericString) (westernLongitude.duplicate(null));
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
            this.westernLongitude = (NumericString) (westernLongitude.duplicate(null));
        }
        else
            this.westernLongitude = new NumericString(null, LONGITUDE_UNITS);
    }

    /**
     * @return the eastern longitude limit; never null but may be empty. If not empty, the longitude is guaranteed
     *         to be a numeric value in the range [-360.0,360.0] and the units will be {@link #LONGITUDE_UNITS}
     */
    public NumericString getEasternLongitude() {
        return (NumericString) (easternLongitude.duplicate(null));
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
            this.easternLongitude = (NumericString) (easternLongitude.duplicate(null));
        }
        else
            this.easternLongitude = new NumericString(null, LONGITUDE_UNITS);
    }

    /**
     * @return the southern latitude limit; never null but may be empty. If not empty, the latitude is guaranteed
     *         to be a numeric value in the range [-90.0,90.0] and the units will be {@link #LATITUDE_UNITS}
     */
    public NumericString getSouthernLatitude() {
        return (NumericString) (southernLatitude.duplicate(null));
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
            this.southernLatitude = (NumericString) (southernLatitude.duplicate(null));
        }
        else
            this.southernLatitude = new NumericString(null, LATITUDE_UNITS);
    }

    /**
     * @return the northern latitude limit; never null but may be empty. If not empty, the latitude is guaranteed
     *         to be a numeric value in the range [-90.0,90.0] and the units will be {@link #LATITUDE_UNITS}
     */
    public NumericString getNorthernLatitude() {
        return (NumericString) (northernLatitude.duplicate(null));
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
            this.northernLatitude = (NumericString) (northernLatitude.duplicate(null));
        }
        else
            this.northernLatitude = new NumericString(null, LATITUDE_UNITS);
    }

    /**
     * @return the earliest (oldest) data time value; never null but may be an invalid date
     */
    public Datestamp getEarliestDataDate() {
        return (Datestamp) (earliestDataDate.duplicate(null));
    }

    /**
     * @param earliestDataDate
     *         assign as the earliest (oldest) data time value;
     *         if null, an invalid Datestamp is assigned
     */
    public void setEarliestDataDate(Datestamp earliestDataDate) {
        if ( earliestDataDate != null )
            this.earliestDataDate = (Datestamp) (earliestDataDate.duplicate(null));
        else
            this.earliestDataDate = new Datestamp();
    }

    /**
     * @return the latest (newest) data time value; never null but may be an invalid date
     */
    public Datestamp getLatestDataDate() {
        return (Datestamp) (latestDataDate.duplicate(null));
    }

    /**
     * @param latestDataDate
     *         assign as the latest (newest) data time value;
     *         if null, a invalid Datestamp is assigned
     */
    public void setLatestDataDate(Datestamp latestDataDate) {
        if ( latestDataDate != null )
            this.latestDataDate = (Datestamp) (latestDataDate.duplicate(null));
        else
            this.latestDataDate = new Datestamp();
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
     *         Any names given are guaranteed to be non-blank strings.
     */
    public TreeSet<String> getGeographicNames() {
        return new TreeSet<String>(geographicNames);
    }

    /**
     * Calls {@link #setGeographicNames(Iterable)}; added to satisfy JavaBean requirements.
     *
     * @param geographicNames
     *         assign as the list of geographic names; if null, an empty set is assigned
     *
     * @throws IllegalArgumentException
     *         if any name given is null or blank
     */
    public void setGeographicNames(TreeSet<String> geographicNames) throws IllegalArgumentException {
        setGeographicNames((Iterable<String>) geographicNames);
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

    @Override
    public Object duplicate(Object dup) {
        Coverage coverage;
        if ( dup == null )
            coverage = new Coverage();
        else
            coverage = (Coverage) dup;
        coverage.westernLongitude = (NumericString) (westernLongitude.duplicate(null));
        coverage.easternLongitude = (NumericString) (easternLongitude.duplicate(null));
        coverage.southernLatitude = (NumericString) (southernLatitude.duplicate(null));
        coverage.northernLatitude = (NumericString) (northernLatitude.duplicate(null));
        coverage.earliestDataDate = (Datestamp) (earliestDataDate.duplicate(null));
        coverage.latestDataDate = (Datestamp) (latestDataDate.duplicate(null));
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
        if ( !earliestDataDate.equals(other.earliestDataDate) )
            return false;
        if ( !latestDataDate.equals(other.latestDataDate) )
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
        result = result * prime + earliestDataDate.hashCode();
        result = result * prime + latestDataDate.hashCode();
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
                ", earliestDataDate=" + earliestDataDate +
                ", latestDataDate=" + latestDataDate +
                ", spatialReference='" + spatialReference + "'" +
                ", geographicNames=" + geographicNames +
                '}';
    }

}
