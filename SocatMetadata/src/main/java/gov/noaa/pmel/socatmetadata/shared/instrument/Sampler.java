package gov.noaa.pmel.socatmetadata.shared.instrument;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;
import gov.noaa.pmel.socatmetadata.shared.core.MultiNames;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Base class for a sampling instrument.
 */
public class Sampler extends Instrument implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -1170130500429810550L;

    private MultiNames instrumentNames;

    /**
     * Create with all fields empty.
     */
    public Sampler() {
        super();
        instrumentNames = new MultiNames();
    }

    /**
     * Create using as many of the values in the given instrument subclass as possible.
     */
    public Sampler(Instrument instr) {
        super(instr);
        if ( instr instanceof Sampler ) {
            Sampler other = (Sampler) instr;
            instrumentNames = new MultiNames(other.instrumentNames);
        }
        else {
            instrumentNames = new MultiNames();
        }

    }

    /**
     * @return the name set of attached instruments (primarily sensors); never null but may be empty.
     */
    public MultiNames getInstrumentNames() {
        return new MultiNames(instrumentNames);
    }

    /**
     * @param instrumentNames
     *         assign as the name set of attached instruments (primarily sensors);
     *         if null, an empty name set is assigned
     */
    public void setInstrumentNames(MultiNames instrumentNames) {
        this.instrumentNames = new MultiNames(instrumentNames);
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

    @Override
    public Object duplicate(Object dup) {
        Sampler sampler;
        if ( dup == null )
            sampler = new Sampler();
        else
            sampler = (Sampler) dup;
        super.duplicate(sampler);
        sampler.instrumentNames = new MultiNames(instrumentNames);
        return sampler;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + instrumentNames.hashCode();
        return result;
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
    public String toString() {
        String repr = super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
        repr = repr.substring(0, repr.length() - 2) +
                ", instrumentNames=" + instrumentNames +
                " }";
        return repr;
    }

    @Override
    public String getSimpleName() {
        return "Sampler";
    }

}
