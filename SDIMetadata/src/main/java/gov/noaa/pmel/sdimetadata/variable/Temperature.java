package gov.noaa.pmel.sdimetadata.variable;

/**
 * Describes a temperature data variable in a dataset.
 * Same as Variable except the unit for precision and accuracy are set to degrees Celsius and cannot be modified,
 * and the default unit for the variable is degrees Celsius (but can be modified).
 */
public class Temperature extends Variable implements Cloneable {

    public static final String DEGREES_CELSIUS_UNIT = "deg C";

    /**
     * Create with all fields empty or NaN, except for units which are set to degrees Celsius.
     */
    public Temperature() {
        super();
        unit = DEGREES_CELSIUS_UNIT;
        uncertaintyUnit = DEGREES_CELSIUS_UNIT;
    }

    /**
     * @param unit
     *         assign as the unit for values of this variable; if null, degrees Celsius is assigned
     */
    @Override
    public void setUnit(String unit) {
        this.unit = (unit != null) ? unit.trim() : DEGREES_CELSIUS_UNIT;
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
    public Temperature clone() {
        return (Temperature) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
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

