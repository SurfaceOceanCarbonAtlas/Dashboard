package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;

import java.io.Serializable;

/**
 * Information about a temperature measurement.
 * The default unit is set to degrees Celsius.
 */
public class Temperature extends InstDataVar implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = 7362053649610620025L;

    public static final String DEGREES_CELSIUS_UNIT = "deg C";

    /**
     * Create with all fields empty, except for units which are set to {@link #DEGREES_CELSIUS_UNIT}
     */
    public Temperature() {
        super();
        super.setVarUnit(DEGREES_CELSIUS_UNIT);
    }

    /**
     * Create using as many of the values in the given variable subclass as possible,
     * except for units which are set to {@link #DEGREES_CELSIUS_UNIT}
     */
    public Temperature(Variable var) {
        super(var);
        super.setVarUnit(DEGREES_CELSIUS_UNIT);
    }

    /**
     * @param varUnit
     *         assign as the unit for values of this variable as well as accuracy and precision;
     *         if null or blank, degrees Celsius is assigned
     */
    @Override
    public void setVarUnit(String varUnit) {
        if ( (varUnit == null) || varUnit.trim().isEmpty() )
            super.setVarUnit(DEGREES_CELSIUS_UNIT);
        else
            super.setVarUnit(varUnit);
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
