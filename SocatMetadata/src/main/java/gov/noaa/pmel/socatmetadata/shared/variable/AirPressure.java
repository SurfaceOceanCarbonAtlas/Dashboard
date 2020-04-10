package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;

import java.io.Serializable;

/**
 * Information about an air pressure measurement.
 * The default unit is hectopascals instead of empty.
 * Also provides a pressure correction field.
 */
public class AirPressure extends DataVar implements Serializable, IsSerializable {

    private static final long serialVersionUID = -7149799335885804693L;

    public static final String HECTOPASCALS_UNIT = "hPa";

    protected String pressureCorrection;

    /**
     * Create with all fields empty except for units which are {@link #HECTOPASCALS_UNIT}
     */
    public AirPressure() {
        super();
        varUnit = HECTOPASCALS_UNIT;
        accuracy.setUnitString(HECTOPASCALS_UNIT);
        precision.setUnitString(HECTOPASCALS_UNIT);
        pressureCorrection = "";
    }

    /**
     * Create using values in the given variable. If a DataVar is given, all DataVar fields are copied.
     * If a AirPressure is given, all AirPressure fields are copied.
     */
    public AirPressure(Variable var) {
        super(var);
        if ( var instanceof AirPressure ) {
            AirPressure press = (AirPressure) var;
            pressureCorrection = press.pressureCorrection;
        }
        else {
            varUnit = HECTOPASCALS_UNIT;
            accuracy.setUnitString(HECTOPASCALS_UNIT);
            precision.setUnitString(HECTOPASCALS_UNIT);
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
        this.varUnit = (varUnit != null) ? varUnit.trim() : HECTOPASCALS_UNIT;
        if ( this.varUnit.isEmpty() )
            this.varUnit = HECTOPASCALS_UNIT;
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
        else
            this.accuracy = new NumericString(null, HECTOPASCALS_UNIT);
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
        else
            this.precision = new NumericString(null, HECTOPASCALS_UNIT);
    }

    /**
     * Deeply copies the values in this AirPressure object to the given AirPressure object.
     *
     * @param dup
     *         the AirPressure object to copy values into;
     *         if null, a new AirPressure object is created for copying values into
     *
     * @return the updated AirPressure object
     */
    public AirPressure duplicate(AirPressure dup) {
        if ( dup == null )
            dup = new AirPressure();
        super.duplicate(dup);
        dup.pressureCorrection = pressureCorrection;
        return dup;
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
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + pressureCorrection.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst("DataVar", "AirPressure");
        return repr.substring(0, repr.length() - 1) +
                ", pressureCorrection='" + pressureCorrection + "'}";
    }

}
