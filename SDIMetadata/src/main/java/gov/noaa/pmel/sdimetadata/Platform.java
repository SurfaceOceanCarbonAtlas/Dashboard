package gov.noaa.pmel.sdimetadata;

import java.util.HashSet;

/**
 * Basic information about the platform (ship, mooring) used by the dataset.
 */
public class Platform implements Cloneable {

    protected String platformId;
    protected String platformName;
    protected String platformType;
    protected String platformOwner;
    protected String platformCountry;

    /**
     * Create with all values empty
     */
    public Platform() {
        platformId = "";
        platformName = "";
        platformType = "";
        platformOwner = "";
        platformCountry = "";
    }

    /**
     * @return list of field names that are currently invalid
     */
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalid = new HashSet<String>();
        if ( platformId.isEmpty() )
            invalid.add("platformId");
        if ( platformName.isEmpty() )
            invalid.add("platformName");
        if ( platformType.isEmpty() )
            invalid.add("platformType");
        return invalid;
    }

    /**
     * @return the unique ID for this platform; never null but may be empty
     */
    public String getPlatformId() {
        return platformId;
    }

    /**
     * @param platformId
     *         assign as the unique ID for this platform; if null, an empty string is assigned
     */
    public void setPlatformId(String platformId) {
        this.platformId = (platformId != null) ? platformId.trim() : "";
    }

    /**
     * @return the name for this platform; never null but may be empty
     */
    public String getPlatformName() {
        return platformName;
    }

    /**
     * @param platformName
     *         assign as the name for this platform; if null, an empty string is assigned
     */
    public void setPlatformName(String platformName) {
        this.platformName = (platformName != null) ? platformName.trim() : "";
    }

    /**
     * @return the type of this platform (ship, mooring, drifting buoy); never null but may be empty
     */
    public String getPlatformType() {
        return platformType;
    }

    /**
     * @param platformType
     *         assign as the type of this platform (ship, mooring, drifting buoy); if null, an empty string is assigned
     */
    public void setPlatformType(String platformType) {
        this.platformType = (platformType != null) ? platformType.trim() : "";
    }

    /**
     * @return the owner of this platform; never null but may be empty
     */
    public String getPlatformOwner() {
        return platformOwner;
    }

    /**
     * @param platformOwner
     *         assign as the owner of this platform; if null, an empty string is assigned
     */
    public void setPlatformOwner(String platformOwner) {
        this.platformOwner = (platformOwner != null) ? platformOwner.trim() : "";
    }

    /**
     * @return the country under which this platform is registered; never null but may be empty
     */
    public String getPlatformCountry() {
        return platformCountry;
    }

    /**
     * @param platformCountry
     *         assign as the country under which this platform is registered; if null, an empty string is assigned
     */
    public void setPlatformCountry(String platformCountry) {
        this.platformCountry = (platformCountry != null) ? platformCountry.trim() : "";
    }

    @Override
    public Platform clone() {
        Platform dup;
        try {
            dup = (Platform) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.platformId = platformId;
        dup.platformName = platformName;
        dup.platformType = platformType;
        dup.platformOwner = platformOwner;
        dup.platformCountry = platformCountry;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Platform) )
            return false;

        Platform platform = (Platform) obj;

        if ( !platformId.equals(platform.platformId) )
            return false;
        if ( !platformName.equals(platform.platformName) )
            return false;
        if ( !platformType.equals(platform.platformType) )
            return false;
        if ( !platformOwner.equals(platform.platformOwner) )
            return false;
        if ( !platformCountry.equals(platform.platformCountry) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = platformId.hashCode();
        result = result * prime + platformName.hashCode();
        result = result * prime + platformType.hashCode();
        result = result * prime + platformOwner.hashCode();
        result = result * prime + platformCountry.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Platform{" +
                "platformId='" + platformId + '\'' +
                ", platformName='" + platformName + '\'' +
                ", platformType='" + platformType + '\'' +
                ", platformOwner='" + platformOwner + '\'' +
                ", platformCountry='" + platformCountry + '\'' +
                '}';
    }

}

