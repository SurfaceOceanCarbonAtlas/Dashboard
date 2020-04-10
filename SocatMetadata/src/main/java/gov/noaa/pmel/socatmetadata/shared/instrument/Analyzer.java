package gov.noaa.pmel.socatmetadata.shared.instrument;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Base class for an analyzing instrument.
 */
public class Analyzer extends Instrument implements Serializable, IsSerializable {

    private static final long serialVersionUID = 7541206728125008817L;

    protected String calibration;

    public Analyzer() {
        super();
        calibration = "";
    }

    /**
     * @return calibration comment; never null but may be empty
     */
    public String getCalibration() {
        return calibration;
    }

    /**
     * @param calibration
     *         assign as the calibration comment; if null, an empty string is assigned
     */
    public void setCalibration(String calibration) {
        this.calibration = (calibration != null) ? calibration.trim() : "";
    }

    @Override
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalids = super.invalidFieldNames();
        if ( manufacturer.isEmpty() )
            invalids.add("manufacturer");
        if ( model.isEmpty() )
            invalids.add("model");
        if ( calibration.isEmpty() )
            invalids.add("calibration");
        return invalids;
    }

    /**
     * Deeply copies the values in this Analyzer object to the given Analyzer object.
     *
     * @param dup
     *         the Analyzer object to copy values into;
     *         if null, a new Analyzer object is created for copying values into
     *
     * @return the updated Analyzer object
     */
    public Analyzer duplicate(Analyzer dup) {
        if ( dup == null )
            dup = new Analyzer();
        super.duplicate(dup);
        dup.calibration = calibration;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Analyzer) )
            return false;
        if ( !super.equals(obj) )
            return false;

        Analyzer other = (Analyzer) obj;

        if ( !calibration.equals(other.calibration) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + calibration.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst("Instrument", "Analyzer");
        return repr.substring(0, repr.length() - 1) +
                ", calibration='" + calibration + '\'' +
                '}';
    }

}
