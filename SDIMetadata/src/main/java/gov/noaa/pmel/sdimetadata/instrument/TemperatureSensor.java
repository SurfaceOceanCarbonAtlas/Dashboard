package gov.noaa.pmel.sdimetadata.instrument;

/**
 * Basic information about an instrument that is a temperature sensor.  Specific details about values measured
 * by the sensor are part of {@link gov.noaa.pmel.sdimetadata.variable.Variable}.
 */
public class TemperatureSensor extends Analyzer implements Cloneable {

    @Override
    public boolean isValid() {
        if ( name.isEmpty() || manufacturer.isEmpty() || model.isEmpty() )
            return false;
        return true;
    }

    @Override
    public TemperatureSensor clone() {
        return (TemperatureSensor) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof TemperatureSensor) )
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Analyzer", "TemperatureSensor");
    }

}

