package gov.noaa.pmel.socatmetadata.shared.instrument;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;
import gov.noaa.pmel.socatmetadata.shared.variable.InstData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Basic information about an instrument that is a gas sensor.  Specific details about values
 * measured by the sensor are part of {@link InstData} since a sensor can be used to measure
 * more than one variable (e.g., atmospheric and aqueous CO2) with differing details (e.g., accuracy).
 */
public class GasSensor extends Analyzer implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -8883755394488095375L;

    private ArrayList<CalibrationGas> calibrationGases;

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
            gasList.add((CalibrationGas) gas.duplicate(null));
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
                this.calibrationGases.add((CalibrationGas) gas.duplicate(null));
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

    @Override
    public Object duplicate(Object dup) {
        GasSensor gasSensor;
        if ( dup == null )
            gasSensor = new GasSensor();
        else
            gasSensor = (GasSensor) dup;
        super.duplicate(gasSensor);
        gasSensor.calibrationGases = new ArrayList<CalibrationGas>(calibrationGases.size());
        for (CalibrationGas gas : calibrationGases) {
            gasSensor.calibrationGases.add((CalibrationGas) gas.duplicate(null));
        }
        return gasSensor;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + calibrationGases.hashCode();
        return result;
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
    public String toString() {
        String repr = super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
        return repr.substring(0, repr.length() - 2) +
                ", calibrationGases=" + calibrationGases +
                " }";
    }

    @Override
    public String getSimpleName() {
        return "GasSensor";
    }

}
