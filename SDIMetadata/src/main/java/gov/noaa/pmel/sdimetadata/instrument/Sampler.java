package gov.noaa.pmel.sdimetadata.instrument;

/**
 * Base class for a sampling instrument.
 */
public class Sampler extends Instrument implements Cloneable {

    @Override
    public Sampler clone() {
        return (Sampler) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Sampler) )
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Instrument", "Sampler");
    }

}
