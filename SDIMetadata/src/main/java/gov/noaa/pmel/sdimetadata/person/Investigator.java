package gov.noaa.pmel.sdimetadata.person;

import java.util.ArrayList;

/**
 * Full information about an investigator.
 * Super class for Submitter.
 */
public class Investigator extends Person implements Cloneable {

    protected ArrayList<String> streets;
    protected String city;
    protected String region;
    protected String zipCode;
    protected String country;
    protected String phone;
    protected String email;

    /**
     * Create with all fields empty.
     */
    public Investigator() {
        super();
        streets = new ArrayList<String>(2);
        city = "";
        region = "";
        zipCode = "";
        country = "";
        phone = "";
        email = "";
    }

    /**
     * Create with Person fields assigned from the given person and all other fields empty
     *
     * @param person
     *         assign lastName, firstName, id, idType, and organization fields from here; cannot be null
     */
    public Investigator(Person person) {
        super(person.lastName, person.firstName, person.middle, person.id, person.idType, person.organization);
        streets = new ArrayList<String>(2);
        city = "";
        region = "";
        zipCode = "";
        country = "";
        phone = "";
        email = "";
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

    @Override
    public Investigator clone() {
        Investigator dup = (Investigator) super.clone();
        dup.streets = new ArrayList<String>(streets);
        dup.city = city;
        dup.region = region;
        dup.zipCode = zipCode;
        dup.country = country;
        dup.phone = phone;
        dup.email = email;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Investigator) )
            return false;
        if ( !super.equals(obj) )
            return false;

        Investigator pi = (Investigator) obj;

        if ( !streets.equals(pi.streets) )
            return false;
        if ( !city.equals(pi.city) )
            return false;
        if ( !region.equals(pi.region) )
            return false;
        if ( !zipCode.equals(pi.zipCode) )
            return false;
        if ( !country.equals(pi.country) )
            return false;
        if ( !phone.equals(pi.phone) )
            return false;
        if ( !email.equals(pi.email) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + streets.hashCode();
        result = result * prime + city.hashCode();
        result = result * prime + region.hashCode();
        result = result * prime + zipCode.hashCode();
        result = result * prime + country.hashCode();
        result = result * prime + phone.hashCode();
        result = result * prime + email.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst("Person", "Investigator");
        return repr.substring(0, repr.length() - 1) +
                ", streets=" + streets +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", country='" + country + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}

