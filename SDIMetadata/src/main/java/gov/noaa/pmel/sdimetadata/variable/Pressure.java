package gov.noaa.pmel.sdimetadata.variable;

/**
 * Describes a pressure data variable in a dataset.
 * Same as Variable except the unit for accuracy and precision are set to hectopascals and cannot be modified,
 * and the default unit for the variable is hectopascals (but can be modified).
 */
public class Pressure extends Variable implements Cloneable {

    public static final String HECTOPASCALS_UNIT = "hPa";

    public Pressure() {
        super();
        varUnit = HECTOPASCALS_UNIT;
        apUnit = HECTOPASCALS_UNIT;
    }

    /**
     * @param varUnit
     *         assign as the unit for values of this variable; if null or blank, hectopascals is assigned
     */
    @Override
    public void setVarUnit(String varUnit) {
        this.varUnit = (varUnit != null) ? varUnit.trim() : HECTOPASCALS_UNIT;
        if ( this.varUnit.isEmpty() )
            this.varUnit = HECTOPASCALS_UNIT;
    }

    /**
     * @throws UnsupportedOperationException
     *         always
     */
    @Override
    public void setApUnit(String apUnit) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("unit for accuracy and precision is unmodifiable");
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
