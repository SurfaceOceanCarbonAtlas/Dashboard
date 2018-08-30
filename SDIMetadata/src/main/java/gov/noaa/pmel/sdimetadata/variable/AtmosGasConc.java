package gov.noaa.pmel.sdimetadata.variable;

public class AtmosGasConc extends Variable implements Cloneable {

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
        return super.toString()
                    .replaceFirst("Variable", "AtmosGasConc");
    }

}

