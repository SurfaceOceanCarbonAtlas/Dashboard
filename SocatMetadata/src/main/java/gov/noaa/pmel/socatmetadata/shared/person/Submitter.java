package gov.noaa.pmel.socatmetadata.shared.person;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Same as Investigator except requires contact information to be valid.
 */
public class Submitter extends Investigator implements Cloneable, Serializable {

    private static final long serialVersionUID = 5181726974657440228L;

    /**
     * Create with all fields empty
     */
    public Submitter() {
        super();
    }

    /**
     * Create with Person fields assigned from the given person and all other fields empty
     *
     * @param person
     *         assign lastName, firstName, id, idType, and organization fields from here; cannot be null
     */
    public Submitter(Person person) {
        super(person);
    }

    /**
     * @return set of field names that are currently invalid
     */
    @Override
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalid = super.invalidFieldNames();
        // At this time only check streets for address - address from CDIAC not parsed
        if ( streets.isEmpty() )
            invalid.add("streets");
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
