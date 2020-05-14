package gov.noaa.pmel.socatmetadata.shared.instrument;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Base class for an analyzing instrument.
 */
public class Analyzer extends Instrument implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -1355870685018481799L;

    private String calibration;

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
        if ( getManufacturer().isEmpty() )
            invalids.add("manufacturer");
        if ( getModel().isEmpty() )
            invalids.add("model");
        if ( calibration.isEmpty() )
            invalids.add("calibration");
        return invalids;
    }

    @Override
    public Object duplicate(Object dup) {
        Analyzer analyzer;
        if ( dup == null )
            analyzer = new Analyzer();
        else
            analyzer = (Analyzer) dup;
        super.duplicate(analyzer);
        analyzer.calibration = calibration;
        return analyzer;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + calibration.hashCode();
        return result;
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
    public String toString() {
        String repr = super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
        return repr.substring(0, repr.length() - 2) +
                ", calibration='" + calibration + "'" +
                " }";
    }

    @Override
    public String getSimpleName() {
        return "Analyzer";
    }
}
