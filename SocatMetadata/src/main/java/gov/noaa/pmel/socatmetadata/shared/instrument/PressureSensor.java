package gov.noaa.pmel.socatmetadata.shared.instrument;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.variable.DataVar;

import java.io.Serializable;

/**
 * Basic information about an instrument that is a pressure sensor.  Specific details about values measured
 * by the sensor are part of {@link DataVar}.
 */
public class PressureSensor extends Analyzer implements Serializable, IsSerializable {

    private static final long serialVersionUID = -310572386325156332L;

    /**
     * Deeply copies the values in this PressureSensor object to the given PressureSensor object.
     *
     * @param dup
     *         the PressureSensor object to copy values into;
     *         if null, a new PressureSensor object is created for copying values into
     *
     * @return the updated PressureSensor object
     */
    public PressureSensor duplicate(PressureSensor dup) {
        if ( dup == null )
            dup = new PressureSensor();
        super.duplicate(dup);
        return dup;
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
