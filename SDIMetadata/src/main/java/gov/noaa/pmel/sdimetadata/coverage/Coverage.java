package gov.noaa.pmel.sdimetadata.coverage;

public class Coverage {
    protected Double westernLongitude;
    protected Double easternLongitude;
    protected Double southernLatitude;
    protected Double northernLatitude;
    protected Double earliestDataTime;
    protected Double latestDataTime;

    /**
     * Create with all values assigned as Double.NaN
     */
    public Coverage() {
        westernLongitude = Double.NaN;
        easternLongitude = Double.NaN;
        southernLatitude = Double.NaN;
        northernLatitude = Double.NaN;
        earliestDataTime = Double.NaN;
        latestDataTime = Double.NaN;
   }

    /**
     * @return the western longitude limit, in units of decimal degrees east;
     *         never null but may be Double.NaN
     */
    public Double getWesternLongitude() {
        return westernLongitude;
    }

    /**
     * @param westernLongitude
     *         assign as the western longitude limit, in units of decimal degrees east;
     *         if null, Double.NaN will be assigned
     */
    public void setWesternLongitude(Double westernLongitude) {
        this.westernLongitude = (westernLongitude != null) ? westernLongitude : Double.NaN;
    }

    /**
     * @return the eastern longitude limit, in units of decimal degrees east;
     *         never null but may be Double.NaN
     */
    public Double getEasternLongitude() {
        return easternLongitude;
    }

    /**
     * @param easternLongitude
     *         assign as the eastern longitude limit, in units of decimal degrees east;
     *         if null, Double.NaN will be assigned
     */
    public void setEasternLongitude(Double easternLongitude) {
        this.easternLongitude = (easternLongitude != null) ? easternLongitude : Double.NaN;
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
     *         if null, Double.NaN will be assigned
     */
    public void setSouthernLatitude(Double southernLatitude) {
        this.southernLatitude = (southernLatitude != null) ? southernLatitude : Double.NaN;
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
     *         if null, Double.NaN will be assigned
     */
    public void setNorthernLatitude(Double northernLatitude) {
        this.northernLatitude = (northernLatitude != null) ? northernLatitude : Double.NaN;
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
     *         if null, Double.NaN will be assigned
     */
    public void setEarliestDataTime(Double earliestDataTime) {
        this.earliestDataTime = (earliestDataTime != null) ? earliestDataTime : Double.NaN;
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
     *         if null, Double.NaN will be assigned
     */
    public void setLatestDataTime(Double latestDataTime) {
        this.latestDataTime = (latestDataTime != null) ? latestDataTime : Double.NaN;
    }

   @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
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
                '}';
    }

}

