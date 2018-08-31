package gov.noaa.pmel.sdimetadata.variable;

import gov.noaa.pmel.sdimetadata.util.NumericString;

/**
 * Information about a temperature measurement.
 * The unit for accuracy and precision are set to degrees Celsius and cannot be modified,
 * and the default unit for the variable is degrees Celsius (but can be modified).
 */
public class Temperature extends DataVar implements Cloneable {

    public static final String DEGREES_CELSIUS_UNIT = "deg C";

    /**
     * Create with all fields empty or NaN, except for units which are set to degrees Celsius.
     */
    public Temperature() {
        super();
        varUnit = DEGREES_CELSIUS_UNIT;
        accuracy.setUnitString(DEGREES_CELSIUS_UNIT);
        precision.setUnitString(DEGREES_CELSIUS_UNIT);
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
     * @apiNote also throws IllegalArgumentException if the unit string is not {@link Temperature#DEGREES_CELSIUS_UNIT}
     */
    @Override
    public void setAccuracy(NumericString accuracy) throws IllegalArgumentException {
        if ( accuracy != null ) {
            if ( !DEGREES_CELSIUS_UNIT.equals(accuracy.getUnitString()) )
                throw new IllegalArgumentException("unit of accuracy is not " + DEGREES_CELSIUS_UNIT);
            super.setAccuracy(accuracy);
        }
        else
            this.accuracy = new NumericString(null, DEGREES_CELSIUS_UNIT);
    }

    /**
     * @apiNote also throws IllegalArgumentException if the unit string is not {@link Temperature#DEGREES_CELSIUS_UNIT}
     */
    @Override
    public void setPrecision(NumericString precision) throws IllegalArgumentException {
        if ( precision != null ) {
            if ( !DEGREES_CELSIUS_UNIT.equals(precision.getUnitString()) )
                throw new IllegalArgumentException("unit of precision is not " + DEGREES_CELSIUS_UNIT);
            super.setPrecision(precision);
        }
        else
            this.precision = new NumericString(null, DEGREES_CELSIUS_UNIT);
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
        return super.toString().replaceFirst("DataVar", "Temperature");
    }

}

