package gov.noaa.pmel.sdimetadata.person;

/**
 * Same as Investigator except requires contact information to be valid.
 */
public class Submitter extends Investigator implements Cloneable {

    @Override
    public boolean isValid() {
        if ( lastName.isEmpty() )
            return false;
        if ( firstName.isEmpty() )
            return false;
        if ( streets.isEmpty() )
            return false;
        if ( city.isEmpty() )
            return false;
        if ( country.isEmpty() )
            return false;
        if ( phone.isEmpty() )
            return false;
        if ( email.isEmpty() )
            return false;
        return true;
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
