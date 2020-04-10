package gov.noaa.pmel.socatmetadata.shared.instrument;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Base class for a sampling instrument.
 */
public class Sampler extends Instrument implements Serializable, IsSerializable {

    private static final long serialVersionUID = 4446059962387017147L;

    HashSet<String> instrumentNames;

    /**
     * Create with all fields empty.
     */
    public Sampler() {
        super();
        instrumentNames = new HashSet<String>();
    }

    /**
     * @return the set of names of attached instruments (primarily sensors); never null but may be empty.
     *         The set will not contain any null or blank names.
     */
    public HashSet<String> getInstrumentNames() {
        return new HashSet<String>(instrumentNames);
    }

    /**
     * Calls {@link #setInstrumentNames(Iterable)}; added to satisfy JavaBean requirements.
     *
     * @param instrumentNames
     *         assign as the set of names of attached instruments (primarily sensors);
     *         if null, an empty set is assigned
     *
     * @throws IllegalArgumentException
     *         if the set contains null or blank strings
     */
    public void setInstrumentNames(HashSet<String> instrumentNames) throws IllegalArgumentException {
        setInstrumentNames((Iterable<String>) instrumentNames);
    }

    /**
     * @param instrumentNames
     *         assign as the set of names of attached instruments (primarily sensors);
     *         if null, an empty set is assigned
     *
     * @throws IllegalArgumentException
     *         if the set contains null or blank strings
     */
    public void setInstrumentNames(Iterable<String> instrumentNames) throws IllegalArgumentException {
        this.instrumentNames.clear();
        if ( instrumentNames != null ) {
            for (String name : instrumentNames) {
                if ( name == null )
                    throw new IllegalArgumentException("null instrument name given");
                name = name.trim();
                if ( name.isEmpty() )
                    throw new IllegalArgumentException("blank instrument name given");
                this.instrumentNames.add(name);
            }
        }
    }

    @Override
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalids = super.invalidFieldNames();
        if ( instrumentNames.isEmpty() ) {
            invalids.add("instrumentNames");
            return invalids;
        }
        return invalids;
    }

    /**
     * Deeply copies the values in this Sampler object to the given Sampler object.
     *
     * @param dup
     *         the Sampler object to copy values into;
     *         if null, a new Sampler object is created for copying values into
     *
     * @return the updated Sampler object
     */
    public Sampler duplicate(Sampler dup) {
        if ( dup == null )
            dup = new Sampler();
        super.duplicate(dup);
        dup.instrumentNames = new HashSet<String>(instrumentNames);
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Sampler) )
            return false;
        if ( !super.equals(obj) )
            return false;

        Sampler other = (Sampler) obj;

        if ( !instrumentNames.equals(other.instrumentNames) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + instrumentNames.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst("Instrument", "Sampler");
        repr = repr.substring(0, repr.length() - 1) +
                ", instrumentNames=" + instrumentNames +
                '}';
        return repr;
    }

}
