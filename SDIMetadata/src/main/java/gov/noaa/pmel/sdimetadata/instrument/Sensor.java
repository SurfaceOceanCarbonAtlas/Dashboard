package gov.noaa.pmel.sdimetadata.instrument;

/**
 * Basic information about an instrument that is a sensor.  Specific details about values measured by the sensor
 * are part of {@link gov.noaa.pmel.sdimetadata.variable.Variable} since a sensor can be used to measure more than
 * one variable (e.g., atmospheric and aqueous CO2) with differing details (e.g., uncertainty).
 */
public class Sensor extends Instrument implements Cloneable {

    @Override
    public boolean isValid() {
        if ( name.isEmpty() || manufacturer.isEmpty() || model.isEmpty() )
            return false;
        return true;
    }

    @Override
    public Sensor clone() {
        return (Sensor) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof Sensor) )
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Instrument", "Sensor");
    }

}

