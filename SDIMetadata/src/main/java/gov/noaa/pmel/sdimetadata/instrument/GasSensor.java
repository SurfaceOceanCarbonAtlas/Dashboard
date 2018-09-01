package gov.noaa.pmel.sdimetadata.instrument;

import gov.noaa.pmel.sdimetadata.variable.DataVar;

import java.util.ArrayList;

/**
 * Basic information about an instrument that is a gas sensor.  Specific details about values
 * measured by the sensor are part of {@link DataVar} since a sensor can be used to measure
 * more than one variable (e.g., atmospheric and aqueous CO2) with differing details (e.g., accuracy).
 */
public class GasSensor extends Analyzer implements Cloneable {

    protected String calibrationFrequency;
    protected ArrayList<CalibrationGas> calibrationGases;

    public GasSensor() {
        super();
        calibrationFrequency = "";
        calibrationGases = new ArrayList<CalibrationGas>();
    }

    /**
     * @return the calibration frequency; never null but may be empty
     */
    public String getCalibrationFrequency() {
        return calibrationFrequency;
    }

    /**
     * @param calibrationFrequency
     *         assign as the calibration frequency; if null or blank, an empty string is assigned
     */
    public void setCalibrationFrequency(String calibrationFrequency) {
        this.calibrationFrequency = (calibrationFrequency != null) ? calibrationFrequency.trim() : "";
    }

    /**
     * @return the list of calibration gasses used; never null but may be empty.
     *         The list will not contain null entries.
     */
    public ArrayList<CalibrationGas> getCalibrationGases() {
        ArrayList<CalibrationGas> gasList = new ArrayList<CalibrationGas>(calibrationGases.size());
        for (CalibrationGas gas : calibrationGases) {
            gasList.add(gas.clone());
        }
        return gasList;
    }

    /**
     * @param calibrationGases
     *         assign as the list of calibration gasses; if null, an empty list is assigned.
     *
     * @throws IllegalArgumentException
     *         if any of the gasses given in the list are null
     */
    public void setCalibrationGases(Iterable<CalibrationGas> calibrationGases) throws IllegalArgumentException {
        this.calibrationGases.clear();
        if ( calibrationGases != null ) {
            for (CalibrationGas gas : calibrationGases) {
                if ( null == gas )
                    throw new IllegalArgumentException("null gas given");
                this.calibrationGases.add(gas.clone());
            }
        }
    }

    @Override
    public GasSensor clone() {
        GasSensor dup = (GasSensor) super.clone();
        dup.calibrationFrequency = calibrationFrequency;
        dup.calibrationGases = new ArrayList<CalibrationGas>(calibrationGases.size());
        for (CalibrationGas gas : calibrationGases) {
            dup.calibrationGases.add(gas.clone());
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
        if ( !calibrationFrequency.equals(other.calibrationFrequency) )
            return false;
        if ( !calibrationGases.equals(other.calibrationGases) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + calibrationFrequency.hashCode();
        result = result * prime + calibrationGases.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst("Analyzer", "GasSensor");
        return repr.substring(0, repr.length() - 1) +
                ", calibrationFrequency=" + calibrationFrequency +
                ", calibrationGases=" + calibrationGases +
                '}';
    }

}

