package gov.noaa.pmel.socatmetadata.shared.person;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Same as Investigator except requires contact information to be valid.
 */
public class Submitter extends Investigator implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = 9119658494313912857L;

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
    public Object duplicate(Object dup) {
        Submitter submitter;
        if ( dup == null )
            submitter = new Submitter();
        else
            submitter = (Submitter) dup;
        super.duplicate(submitter);
        return submitter;
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
        return super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
    }

    @Override
    public String getSimpleName() {
        return "Submitter";
    }
}
