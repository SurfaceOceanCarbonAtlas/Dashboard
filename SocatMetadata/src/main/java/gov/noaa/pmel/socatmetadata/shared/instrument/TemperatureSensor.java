package gov.noaa.pmel.socatmetadata.shared.instrument;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.variable.DataVar;

import java.io.Serializable;

/**
 * Basic information about an instrument that is a temperature sensor.  Specific details about values measured
 * by the sensor are part of {@link DataVar}.
 */
public class TemperatureSensor extends Analyzer implements Serializable, IsSerializable {

    private static final long serialVersionUID = -2523382927819320769L;

    /**
     * Deeply copies the values in this Person object to the given Person object.
     *
     * @param dup
     *         the Person object to copy values into;
     *         if null, a new Person object is created for copying values into
     *
     * @return the updated Person object
     */
    public TemperatureSensor duplicate(TemperatureSensor dup) {
        if ( dup == null )
            dup = new TemperatureSensor();
        super.duplicate(dup);
        return dup;
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
