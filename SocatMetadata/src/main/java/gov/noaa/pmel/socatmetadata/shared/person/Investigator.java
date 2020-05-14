package gov.noaa.pmel.socatmetadata.shared.person;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;
import gov.noaa.pmel.socatmetadata.shared.core.MultiString;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Full information about an investigator.
 * Super class for Submitter.
 */
public class Investigator extends Person implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = 8464640869413581049L;

    private MultiString streets;
    private String city;
    private String region;
    private String zipCode;
    private String country;
    private String phone;
    private String email;

    /**
     * Create with all fields empty.
     */
    public Investigator() {
        super();
        streets = new MultiString();
        city = "";
        region = "";
        zipCode = "";
        country = "";
        phone = "";
        email = "";
    }

    /**
     * Create with as many fields assigned from the given Person as possible.
     */
    public Investigator(Person person) {
        super(person);
        if ( person instanceof Investigator ) {
            Investigator pi = (Investigator) person;
            streets = new MultiString(pi.streets);
            city = pi.city;
            region = pi.region;
            zipCode = pi.zipCode;
            country = pi.country;
            phone = pi.phone;
            email = pi.email;
        }
        else {
            streets = new MultiString();
            city = "";
            region = "";
            zipCode = "";
            country = "";
            phone = "";
            email = "";
        }
    }

    /**
     * @return the street / delivery point portion of the address; never null but may be empty
     */
    public MultiString getStreets() {
        return new MultiString(streets);
    }

    /**
     * @param streets
     *         assign as the street / delivery point portion of the address; if null, an empty list is assigned
     */
    public void setStreets(MultiString streets) {
        this.streets = new MultiString(streets);
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
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalids = super.invalidFieldNames();
        if ( getOrganization().isEmpty() )
            invalids.add("organization");
        return invalids;
    }

    @Override
    public Object duplicate(Object dup) {
        Investigator inv;
        if ( dup == null )
            inv = new Investigator();
        else
            inv = (Investigator) dup;
        super.duplicate(inv);
        inv.streets = new MultiString(streets);
        inv.city = city;
        inv.region = region;
        inv.zipCode = zipCode;
        inv.country = country;
        inv.phone = phone;
        inv.email = email;
        return inv;
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
    public String toString() {
        String repr = super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
        return repr.substring(0, repr.length() - 2) +
                ", streets=" + streets +
                ", city='" + city + "'" +
                ", region='" + region + "'" +
                ", zipCode='" + zipCode + "'" +
                ", country='" + country + "'" +
                ", phone='" + phone + "'" +
                ", email='" + email + "'" +
                " }";
    }

    @Override
    public String getSimpleName() {
        return "Investigator";
    }

}
