package gov.noaa.pmel.socatmetadata.shared.instrument;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;
import gov.noaa.pmel.socatmetadata.shared.variable.DataVar;

import java.io.Serializable;

/**
 * Basic information about an instrument that is a temperature sensor.  Specific details about values measured
 * by the sensor are part of {@link DataVar}.
 */
public class TemperatureSensor extends Analyzer implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -3711132123163488056L;

    @Override
    public Object duplicate(Object dup) {
        TemperatureSensor sensor;
        if ( dup == null )
            sensor = new TemperatureSensor();
        else
            sensor = (TemperatureSensor) dup;
        super.duplicate(sensor);
        return sensor;
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
