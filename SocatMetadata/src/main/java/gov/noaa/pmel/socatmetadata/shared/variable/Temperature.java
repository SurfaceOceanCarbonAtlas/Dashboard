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
        super.setVarUnit(DEGREES_CELSIUS_UNIT);
        setAccuracyUnit(DEGREES_CELSIUS_UNIT);
        setPrecisionUnit(DEGREES_CELSIUS_UNIT);
    }

    /**
     * Create using as many of the values in the given variable subclass as possible.
     */
    public Temperature(Variable var) {
        super(var);
        super.setVarUnit(DEGREES_CELSIUS_UNIT);
        setAccuracyUnit(DEGREES_CELSIUS_UNIT);
        setPrecisionUnit(DEGREES_CELSIUS_UNIT);
    }

    /**
     * @param varUnit
     *         assign as the unit for values of this variable; if null or blank, degrees Celsius is assigned
     */
    @Override
    public void setVarUnit(String varUnit) {
        if ( (varUnit == null) || varUnit.trim().isEmpty() )
            super.setVarUnit(DEGREES_CELSIUS_UNIT);
        else
            super.setVarUnit(varUnit);
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
        else {
            super.setAccuracy(null);
            setAccuracyUnit(DEGREES_CELSIUS_UNIT);
        }
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
        else {
            super.setPrecision(null);
            setPrecisionUnit(DEGREES_CELSIUS_UNIT);
        }
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
    public int hashCode() {
        return super.hashCode();
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
    public String toString() {
        return super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
    }

    @Override
    public String getSimpleName() {
        return "Temperature";
    }

}
