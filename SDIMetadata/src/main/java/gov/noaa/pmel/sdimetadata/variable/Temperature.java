package gov.noaa.pmel.sdimetadata.variable;

/**
 * Describes a temperature data variable in a dataset.
 * Same as Variable except the unit for accuracy and precision are set to degrees Celsius and cannot be modified,
 * and the default unit for the variable is degrees Celsius (but can be modified).
 */
public class Temperature extends Variable implements Cloneable {

    public static final String DEGREES_CELSIUS_UNIT = "deg C";

    /**
     * Create with all fields empty or NaN, except for units which are set to degrees Celsius.
     */
    public Temperature() {
        super();
        varUnit = DEGREES_CELSIUS_UNIT;
        apUnit = DEGREES_CELSIUS_UNIT;
    }

    /**
     * @param varUnit
     *         assign as the unit for values of this variable; if null or blank, degrees Celsius is assigned
     */
    @Override
    public void setVarUnit(String varUnit) {
        this.varUnit = (varUnit != null) ? varUnit.trim() : DEGREES_CELSIUS_UNIT;
        if ( this.varUnit.isEmpty() )
            this.varUnit = DEGREES_CELSIUS_UNIT;
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
    public Temperature clone() {
        return (Temperature) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Temperature) )
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Variable", "Temperature");
    }

}

