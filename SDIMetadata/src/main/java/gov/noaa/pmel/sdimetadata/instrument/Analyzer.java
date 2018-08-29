package gov.noaa.pmel.sdimetadata.instrument;

/**
 * Base class for an analyzing instrument.
 */
public class Analyzer extends Instrument {

    @Override
    public Analyzer clone() {
        return (Analyzer) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( ! (obj instanceof Analyzer) )
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Instrument", "Analyzer");
    }

}
