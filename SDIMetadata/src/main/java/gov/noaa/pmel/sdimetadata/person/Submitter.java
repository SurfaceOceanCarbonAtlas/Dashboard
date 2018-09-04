package gov.noaa.pmel.sdimetadata.person;

import java.util.HashSet;

/**
 * Same as Investigator except requires contact information to be valid.
 */
public class Submitter extends Investigator implements Cloneable {

    /**
     * @return set of field names that are currently invalid
     */
    @Override
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalid = new HashSet<String>();
        if ( lastName.isEmpty() )
            invalid.add("lastName");
        if ( firstName.isEmpty() )
            invalid.add("firstName");
        if ( streets.isEmpty() )
            invalid.add("streets");
        if ( city.isEmpty() )
            invalid.add("city");
        if ( country.isEmpty() )
            invalid.add("country");
        if ( phone.isEmpty() )
            invalid.add("phone");
        if ( email.isEmpty() )
            invalid.add("email");
        return invalid;
    }

    @Override
    public Submitter clone() {
        return (Submitter) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Submitter) )
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Investigator", "Submitter");
    }

}
