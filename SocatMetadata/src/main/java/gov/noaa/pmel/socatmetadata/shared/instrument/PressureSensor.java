package gov.noaa.pmel.socatmetadata.shared.instrument;

import gov.noaa.pmel.socatmetadata.shared.variable.DataVar;

import java.io.Serializable;

/**
 * Basic information about an instrument that is a pressure sensor.  Specific details about values measured
 * by the sensor are part of {@link DataVar}.
 */
public class PressureSensor extends Analyzer implements Cloneable, Serializable {

    private static final long serialVersionUID = 7680352102825379553L;

    @Override
    public PressureSensor clone() {
        return (PressureSensor) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof PressureSensor) )
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Analyzer", "PressureSensor");
    }

}
