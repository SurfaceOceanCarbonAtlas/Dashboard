package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;

import java.io.Serializable;

/**
 * Information about an air pressure measurement.
 * The default unit is hectopascals instead of empty.
 * Also provides a pressure correction field.
 */
public class AirPressure extends InstDataVar implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -7446507959702017867L;

    public static final String HECTOPASCALS_UNIT = "hPa";

    private String pressureCorrection;

    /**
     * Create with all fields empty except for units which are {@link #HECTOPASCALS_UNIT}
     */
    public AirPressure() {
        super();
        super.setVarUnit(HECTOPASCALS_UNIT);
        setAccuracyUnit(HECTOPASCALS_UNIT);
        setPrecisionUnit(HECTOPASCALS_UNIT);
        pressureCorrection = "";
    }

    /**
     * Create using as many of the values in the given variable subclass as possible.
     */
    public AirPressure(Variable var) {
        super(var);
        if ( var instanceof AirPressure ) {
            AirPressure press = (AirPressure) var;
            pressureCorrection = press.pressureCorrection;
        }
        else {
            super.setVarUnit(HECTOPASCALS_UNIT);
            setAccuracyUnit(HECTOPASCALS_UNIT);
            setPrecisionUnit(HECTOPASCALS_UNIT);
            pressureCorrection = "";
        }
    }

    /**
     * @return the pressure correction information; never null but may be empty
     */
    public String getPressureCorrection() {
        return pressureCorrection;
    }

    /**
     * @param pressureCorrection
     *         assign as the pressure correction string; if null, and empty string is assigned
     */
    public void setPressureCorrection(String pressureCorrection) {
        this.pressureCorrection = (pressureCorrection != null) ? pressureCorrection.trim() : "";
    }

    /**
     * @param varUnit
     *         assign as the unit for values of this variable; if null or blank, hectopascals is assigned
     */
    @Override
    public void setVarUnit(String varUnit) {
        if ( (varUnit == null) || (varUnit.trim().isEmpty()) )
            super.setVarUnit(HECTOPASCALS_UNIT);
        else
            super.setVarUnit(varUnit);
    }

    /**
     * @param accuracy
     *         assign as the accuracy (uncertainty) in values of this variable;
     *         if null, an NumericString with an empty numeric value but units of hectopascals is assigned
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
            setAccuracyUnit(HECTOPASCALS_UNIT);
        }
    }

    /**
     * @param precision
     *         assign as the precision (resolution) in values of this variable;
     *         if null, an NumericString with an empty numeric value but units of hectopascals is assigned
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
            setPrecisionUnit(HECTOPASCALS_UNIT);
        }
    }

    @Override
    public Object duplicate(Object dup) {
        AirPressure press;
        if ( dup == null )
            press = new AirPressure();
        else
            press = (AirPressure) dup;
        super.duplicate(press);
        press.pressureCorrection = pressureCorrection;
        return press;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + pressureCorrection.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof AirPressure) )
            return false;
        if ( !super.equals(obj) )
            return false;

        AirPressure other = (AirPressure) obj;

        if ( !pressureCorrection.equals(other.pressureCorrection) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
        return repr.substring(0, repr.length() - 2) +
                ", pressureCorrection='" + pressureCorrection + "'" +
                " }";
    }

    @Override
    public String getSimpleName() {
        return "AirPressure";
    }

}
