package gov.noaa.pmel.sdimetadata.variable;

/**
 * Information about measurements of a gas concentration in the atmosphere.
 */
public class AtmosGasConc extends DataVar implements Cloneable {

    @Override
    public AtmosGasConc clone() {
        return (AtmosGasConc) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( !(obj instanceof AtmosGasConc) )
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("DataVar", "AtmosGasConc");
    }

}

