package gov.noaa.pmel.sdimetadata.instrument;

import gov.noaa.pmel.sdimetadata.variable.DataVar;

/**
 * Basic information about an instrument that is a gas sensor.  Specific details about values measured by the sensor
 * are part of {@link DataVar} since a sensor can be used to measure more than
 * one variable (e.g., atmospheric and aqueous CO2) with differing details (e.g., accuracy).
 */
public class GasSensor extends Analyzer implements Cloneable {

    @Override
    public boolean isValid() {
        if ( name.isEmpty() || manufacturer.isEmpty() || model.isEmpty() )
            return false;
        return true;
    }

    @Override
    public GasSensor clone() {
        return (GasSensor) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof GasSensor) )
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Analyzer", "GasSensor");
    }

}

