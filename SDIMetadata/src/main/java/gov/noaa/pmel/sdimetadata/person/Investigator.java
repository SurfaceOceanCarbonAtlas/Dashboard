package gov.noaa.pmel.sdimetadata.person;

import java.util.ArrayList;

/**
 * Standard information about a person.
 * Super class for Submitter.
 */
public class Investigator implements Cloneable {
    protected String lastName;
    protected String firstName;
    protected String middleInitials;
    protected String organization;
    protected ArrayList<String> streets;
    protected String city;
    protected String region;
    protected String zipCode;
    protected String country;
    protected String phone;
    protected String email;
    protected String piId;
    protected String piIdType;

    /**
     * Create with all fields empty.
     */
    public Investigator() {
        lastName = "";
        firstName = "";
        middleInitials = "";
        organization = "";
        streets = new ArrayList<String>(2);
        city = "";
        region = "";
        zipCode = "";
        country = "";
        phone = "";
        email = "";
        piId = "";
        piIdType = "";
    }

    /**
     * @return the last name; never null but may be empty
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *         assign as the last name; if null, an empty string is assigned
     */
    public void setLastName(String lastName) {
        this.lastName = (lastName != null) ? lastName.trim() : "";
    }

    /**
     * @return the first name; never null but may be empty
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *         assign as the first name; if null, an empty string is assigned
     */
    public void setFirstName(String firstName) {
        this.firstName = (firstName != null) ? firstName.trim() : "";
    }

    /**
     * @return the middle initial(s); never null but may be empty
     */
    public String getMiddleInitials() {
        return middleInitials;
    }

    /**
     * @param middleInitials
     *         assign as the middle initial(s); if null, an empty string is assigned
     */
    public void setMiddleInitials(String middleInitials) {
        this.middleInitials = (middleInitials != null) ? middleInitials.trim() : "";
    }

    /**
     * @return the organization name; never null but may be empty
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization
     *         assign as the organization; if null, an empty string is assigned
     */
    public void setOrganization(String organization) {
        this.organization = (organization != null) ? organization.trim() : "";
    }

    /**
     * @return the street / delivery point portion of the address; never null but may be empty
     */
    public ArrayList<String> getStreets() {
        return new ArrayList<String>(streets);
    }

    /**
     * @param streets
     *         assign as the street / delivery point portion of the address; if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if the given list contains a null or blank string
     */
    public void setStreets(Iterable<String> streets) throws IllegalArgumentException {
        this.streets.clear();
        if ( streets != null ) {
            for (String loc : streets) {
                if ( loc == null )
                    throw new IllegalArgumentException("null street String given");
                loc = loc.trim();
                if ( loc.isEmpty() )
                    throw new IllegalArgumentException("blank street String given");
                this.streets.add(loc);
            }
        }
    }

    /**
     * @return the city; never null but may be empty
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city
     *         assign as the city; if null, an empty string is assigned
     */
    public void setCity(String city) {
        this.city = (city != null) ? city.trim() : "";
    }

    /**
     * @return the region / state; never null but may be empty
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param region
     *         assign as the region / state; if null, an empty string is assigned
     */
    public void setRegion(String region) {
        this.region = (region != null) ? region.trim() : "";
    }

    /**
     * @return the ZIP code; never null but may be empty
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * @param zipCode
     *         assign as the ZIP code; if null, an empty string is assigned
     */
    public void setZipCode(String zipCode) {
        this.zipCode = (zipCode != null) ? zipCode.trim() : "";
    }

    /**
     * @return the country; never null but may be empty
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country
     *         assign as the country; if null, an empty string is assigned
     */
    public void setCountry(String country) {
        this.country = (country != null) ? country.trim() : "";
    }

    /**
     * @return the phone number; never null but may be empty
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *         assign as the phone number; if null, an empty string is assigned
     */
    public void setPhone(String phone) {
        this.phone = (phone != null) ? phone.trim() : "";
    }

    /**
     * @return the e-mail address; never null but may be empty
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *         assign as the e-mail address; if null, an empty string is assigned
     */
    public void setEmail(String email) {
        this.email = (email != null) ? email.trim() : "";
    }

    /**
     * @return the investigator ID; never null but may be empty
     */
    public String getPiId() {
        return piId;
    }

    /**
     * @param piId
     *         assign as the investigator ID; if null, an empty string is assigned
     */
    public void setPiId(String piId) {
        this.piId = (piId != null) ? piId.trim() : "";
    }

    /**
     * @return the type / issuer of the investigator ID; never null but may be empty
     */
    public String getPiIdType() {
        return piIdType;
    }

    /**
     * @param piIdType
     *         assign as the type / issuer of the investigator ID; if null, an empty string is assigned
     */
    public void setPiIdType(String piIdType) {
        this.piIdType = (piIdType != null) ? piIdType.trim() : "";
    }

    /**
     * @return whether all the required fields are assigned with valid values.
     *         For Investigator, the only requirement is non-blank first and last names.
     */
    public boolean isValid() {
        if ( lastName.isEmpty() )
            return false;
        if ( firstName.isEmpty() )
            return false;
        return true;
    }

    @Override
    public Investigator clone() {
        Investigator dup;
        try {
            dup = (Investigator) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.lastName = lastName;
        dup.firstName = firstName;
        dup.middleInitials = middleInitials;
        dup.organization = organization;
        dup.streets = new ArrayList<String>(streets);
        dup.city = city;
        dup.region = region;
        dup.zipCode = zipCode;
        dup.country = country;
        dup.phone = phone;
        dup.email = email;
        dup.piId = piId;
        dup.piIdType = piIdType;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof Investigator) )
            return false;

        Investigator other = (Investigator) obj;

        if ( !lastName.equals(other.lastName) )
            return false;
        if ( !firstName.equals(other.firstName) )
            return false;
        if ( !middleInitials.equals(other.middleInitials) )
            return false;
        if ( !organization.equals(other.organization) )
            return false;
        if ( !streets.equals(other.streets) )
            return false;
        if ( !city.equals(other.city) )
            return false;
        if ( !region.equals(other.region) )
            return false;
        if ( !zipCode.equals(other.zipCode) )
            return false;
        if ( !country.equals(other.country) )
            return false;
        if ( !phone.equals(other.phone) )
            return false;
        if ( !email.equals(other.email) )
            return false;
        if ( !piId.equals(other.piId) )
            return false;
        if ( !piIdType.equals(other.piIdType) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = lastName.hashCode();
        result = result * prime + firstName.hashCode();
        result = result * prime + middleInitials.hashCode();
        result = result * prime + organization.hashCode();
        result = result * prime + streets.hashCode();
        result = result * prime + city.hashCode();
        result = result * prime + region.hashCode();
        result = result * prime + zipCode.hashCode();
        result = result * prime + country.hashCode();
        result = result * prime + phone.hashCode();
        result = result * prime + email.hashCode();
        result = result * prime + piId.hashCode();
        result = result * prime + piIdType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Investigator{" +
                "lastName='" + lastName + "'" +
                ", firstName='" + firstName + "'" +
                ", middleInitials='" + middleInitials + "'" +
                ", organization='" + organization + "'" +
                ", streets=" + streets +
                ", city='" + city + "'" +
                ", region='" + region + "'" +
                ", zipCode='" + zipCode + "'" +
                ", country='" + country + "'" +
                ", phone='" + phone + "'" +
                ", email='" + email + "'" +
                ", piId='" + piId + "'" +
                ", piIdType='" + piIdType + "'" +
                '}';
    }

}

