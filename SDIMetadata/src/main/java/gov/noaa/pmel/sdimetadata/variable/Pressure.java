package gov.noaa.pmel.sdimetadata.variable;

/**
 * Describes a pressure data variable in a dataset.
 * Same as Variable except the unit for precision and accuracy are set to hectopascals and cannot be modified,
 * and the default unit for the variable is hectopascals (but can be modified).
 */
public class Pressure extends Variable implements Cloneable {

    public static final String HECTOPASCALS_UNIT = "hPa";

    public Pressure() {
        super();
        unit = HECTOPASCALS_UNIT;
        uncertaintyUnit = HECTOPASCALS_UNIT;
    }

    /**
     * @param unit
     *         assign as the unit for values of this variable; if null, hectopascals is assigned
     */
    @Override
    public void setUnit(String unit) {
        this.unit = (unit != null) ? unit.trim() : HECTOPASCALS_UNIT;
    }

    /**
     * @throws UnsupportedOperationException
     *         always
     */
    @Override
    public void setUncertaintyUnit(String uncertaintyUnit) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("uncertainty units are unmodifiable");
    }

    @Override
    public Pressure clone() {
        return (Pressure) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof Pressure) )
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Variable", "Pressure");
    }

}
