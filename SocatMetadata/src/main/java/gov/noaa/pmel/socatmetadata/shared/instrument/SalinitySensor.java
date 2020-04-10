package gov.noaa.pmel.socatmetadata.shared.instrument;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.variable.DataVar;

import java.io.Serializable;

/**
 * Basic information about an instrument that is a salinity sensor.  Specific details about values measured
 * by the sensor are part of {@link DataVar}.
 */
public class SalinitySensor extends Analyzer implements Serializable, IsSerializable {

    private static final long serialVersionUID = -4648532040327736781L;

    /**
     * Deeply copies the values in this SalinitySensor object to the given SalinitySensor object.
     *
     * @param dup
     *         the SalinitySensor object to copy values into;
     *         if null, a new SalinitySensor object is created for copying values into
     *
     * @return the updated SalinitySensor object
     */
    public SalinitySensor duplicate(SalinitySensor dup) {
        if ( dup == null )
            dup = new SalinitySensor();
        super.duplicate(dup);
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof SalinitySensor) )
            return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Analyzer", "SalinitySensor");
    }

}
