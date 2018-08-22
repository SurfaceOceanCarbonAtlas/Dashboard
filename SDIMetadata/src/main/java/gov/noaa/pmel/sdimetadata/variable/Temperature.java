package gov.noaa.pmel.sdimetadata.variable;

/**
 * Describes a temperature data variable in a dataset.
 * Same as Variable except the units are set to degrees Celsius and cannot be modified.
 */
public class Temperature extends Variable implements Cloneable {

    public static final String DEGREES_CELSIUS_UNIT = "deg C";

    public Temperature() {
        super();
        varUnit = DEGREES_CELSIUS_UNIT;
        precisionUnit = DEGREES_CELSIUS_UNIT;
        accuracyUnit = DEGREES_CELSIUS_UNIT;
    }

    @Override
    public void setVarUnit(String varUnit) {
        throw new UnsupportedOperationException("units are unmodifiable");
    }

    @Override
    public void setPrecisionUnit(String precisionUnit) {
        throw new UnsupportedOperationException("units are unmodifiable");
    }

    @Override
    public void setAccuracyUnit(String accuracyUnit) {
        throw new UnsupportedOperationException("units are unmodifiable");
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

