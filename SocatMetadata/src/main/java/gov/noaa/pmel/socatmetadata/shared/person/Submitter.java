package gov.noaa.pmel.socatmetadata.shared.person;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Same as Investigator except requires contact information to be valid.
 */
public class Submitter extends Investigator implements Serializable, IsSerializable {

    private static final long serialVersionUID = -715419822436833443L;

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

    /**
     * Deeply copies the values in this Submitter object to the given Submitter object.
     * A new Submitter object will be created if null is given.
     *
     * @param dup
     *         the Submitter object to copy values into;
     *         if null, a new Submitter object is created for copying values into
     *
     * @return the updated Submitter object
     */
    public Submitter duplicate(Submitter dup) {
        if ( dup == null )
            dup = new Submitter();
        super.duplicate(dup);
        return dup;
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
