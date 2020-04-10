package gov.noaa.pmel.socatmetadata.shared.instrument;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.variable.DataVar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Basic information about an instrument that is a gas sensor.  Specific details about values
 * measured by the sensor are part of {@link DataVar} since a sensor can be used to measure
 * more than one variable (e.g., atmospheric and aqueous CO2) with differing details (e.g., accuracy).
 */
public class GasSensor extends Analyzer implements Serializable, IsSerializable {

    private static final long serialVersionUID = -2656836964235855123L;

    protected ArrayList<CalibrationGas> calibrationGases;

    public GasSensor() {
        super();
        calibrationGases = new ArrayList<CalibrationGas>();
    }

    /**
     * @return the list of calibration gases used; never null but may be empty.
     *         The list will not contain null entries.
     */
    public ArrayList<CalibrationGas> getCalibrationGases() {
        ArrayList<CalibrationGas> gasList = new ArrayList<CalibrationGas>(calibrationGases.size());
        for (CalibrationGas gas : calibrationGases) {
            gasList.add(gas.duplicate(null));
        }
        return gasList;
    }

    /**
     * Calls {@link #setCalibrationGases(Iterable)}; added to satisfy JavaBean requirements.
     *
     * @param calibrationGases
     *         assign as the list of calibration gases; if null, an empty list is assigned.
     *
     * @throws IllegalArgumentException
     *         if any of the gases given in the list are null
     */
    public void setCalibrationGases(ArrayList<CalibrationGas> calibrationGases) throws IllegalArgumentException {
        setCalibrationGases((Iterable<CalibrationGas>) calibrationGases);
    }

    /**
     * @param calibrationGases
     *         assign as the list of calibration gases; if null, an empty list is assigned.
     *
     * @throws IllegalArgumentException
     *         if any of the gases given in the list are null
     */
    public void setCalibrationGases(Iterable<CalibrationGas> calibrationGases) throws IllegalArgumentException {
        this.calibrationGases.clear();
        if ( calibrationGases != null ) {
            for (CalibrationGas gas : calibrationGases) {
                if ( null == gas )
                    throw new IllegalArgumentException("null gas given");
                this.calibrationGases.add(gas.duplicate(null));
            }
        }
    }

    @Override
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalid = super.invalidFieldNames();
        if ( calibrationGases.isEmpty() ) {
            invalid.add("calibrationGases");
        }
        else {
            for (int k = 0; k < calibrationGases.size(); k++) {
                for (String name : calibrationGases.get(k).invalidFieldNames()) {
                    invalid.add("calibrationGases[" + k + "]." + name);
                }
            }
        }
        return invalid;
    }

    /**
     * Deeply copies the values in this GasSensor object to the given GasSensor object.
     *
     * @param dup
     *         the GasSensor object to copy values into;
     *         if null, a new GasSensor object is created for copying values into
     *
     * @return the updated GasSensor object
     */
    public GasSensor duplicate(GasSensor dup) {
        if ( dup == null )
            dup = new GasSensor();
        super.duplicate(dup);
        dup.calibrationGases = new ArrayList<CalibrationGas>(calibrationGases.size());
        for (CalibrationGas gas : calibrationGases) {
            dup.calibrationGases.add(gas.duplicate(null));
        }
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof GasSensor) )
            return false;
        if ( !super.equals(obj) )
            return false;

        GasSensor other = (GasSensor) obj;
        if ( !calibrationGases.equals(other.calibrationGases) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + calibrationGases.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst("Analyzer", "GasSensor");
        return repr.substring(0, repr.length() - 1) +
                ", calibrationGases=" + calibrationGases +
                '}';
    }

}
