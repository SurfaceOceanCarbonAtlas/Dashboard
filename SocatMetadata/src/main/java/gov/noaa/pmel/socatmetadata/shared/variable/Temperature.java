package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;

import java.io.Serializable;

/**
 * Information about a temperature measurement.
 * The default unit is set to degrees Celsius.
 */
public class Temperature extends InstDataVar implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = 1635226933184077100L;

    public static final String DEGREES_CELSIUS_UNIT = "deg C";

    /**
     * Create with all fields empty, except for units which are set to degrees Celsius.
     */
    public Temperature() {
        super();
        varUnit = DEGREES_CELSIUS_UNIT;
        accuracy.setUnitString(DEGREES_CELSIUS_UNIT);
        precision.setUnitString(DEGREES_CELSIUS_UNIT);
    }

    /**
     * Create using as many of the values in the given variable subclass as possible.
     */
    public Temperature(Variable var) {
        super(var);
        if ( !(var instanceof Temperature) ) {
            varUnit = DEGREES_CELSIUS_UNIT;
            accuracy.setUnitString(DEGREES_CELSIUS_UNIT);
            precision.setUnitString(DEGREES_CELSIUS_UNIT);
        }
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
     * @param accuracy
     *         assign as the accuracy (uncertainty) in values of this variable;
     *         if null, an NumericString with an empty numeric value but units of degrees Celsius is assigned
     *
     * @throws IllegalArgumentException
     *         if a numeric string is given but is not a finite positive number
     */
    @Override
    public void setAccuracy(NumericString accuracy) throws IllegalArgumentException {
        if ( accuracy != null ) {
            super.setAccuracy(accuracy);
        }
        else
            this.accuracy = new NumericString(null, DEGREES_CELSIUS_UNIT);
    }

    /**
     * @param precision
     *         assign as the precision (resolution) in values of this variable;
     *         if null, an NumericString with an empty numeric value but units of degrees Celsius is assigned
     *
     * @throws IllegalArgumentException
     *         if a numeric string is given but is not a finite positive number
     */
    @Override
    public void setPrecision(NumericString precision) throws IllegalArgumentException {
        if ( precision != null ) {
            super.setPrecision(precision);
        }
        else
            this.precision = new NumericString(null, DEGREES_CELSIUS_UNIT);
    }

    @Override
    public Object duplicate(Object dup) {
        Temperature temp;
        if ( dup == null )
            temp = new Temperature();
        else
            temp = (Temperature) dup;
        super.duplicate(temp);
        return temp;
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
        return super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
    }

    @Override
    public String getSimpleName() {
        return "Temperature";
    }

}
