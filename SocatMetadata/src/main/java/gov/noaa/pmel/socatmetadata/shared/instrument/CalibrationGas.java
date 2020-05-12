package gov.noaa.pmel.socatmetadata.shared.instrument;


import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Describes a standard gas mixture used for calibration of instruments.
 */
public class CalibrationGas implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -7465872060159352093L;

    public static final String GAS_CONCENTRATION_UNIT = "ppm";

    protected String id;
    protected String type;
    protected String supplier;
    protected String useFrequency;
    protected NumericString concentration;
    protected NumericString accuracy;

    /**
     * Assign with all fields empty or NaN
     */
    public CalibrationGas() {
        id = "";
        type = "";
        supplier = "";
        useFrequency = "";
        concentration = new NumericString(null, GAS_CONCENTRATION_UNIT);
        accuracy = new NumericString(null, GAS_CONCENTRATION_UNIT);
    }

    /**
     * @param id
     *         assign as the ID for the calibration gas, e.g., LL83539; if null, an empty string is assigned
     * @param type
     *         assign as the type of gas being calibrated, e.g., CO2; if null, an empty string is assigned
     * @param supplier
     *         assign as the supplier or manufacturer of the calibration gas; if null, an empty string is assigned
     * @param concStr
     *         assign as the concentration, in {@link #GAS_CONCENTRATION_UNIT}, of the gas being calibrated;
     *         if null or blank, an empty string is assigned
     * @param accStr
     *         assign as the accuracy, in {@link #GAS_CONCENTRATION_UNIT}, of the concentration of the gas
     *         being calibrated;  if null or blank, an empty string is assigned
     * @param useFrequency
     *         frequency of calibration with this gas
     *
     * @throws IllegalArgumentException
     *         if the concentration given, if not null and not blank, does not represent a non-negative finite number,
     *         or if the accuracy given, if not null and not blank, does not represent a positive finite number
     */
    public CalibrationGas(String id, String type, String supplier, String concStr, String accStr, String useFrequency)
            throws IllegalArgumentException {
        this();
        setId(id);
        setType(type);
        setSupplier(supplier);
        setUseFrequency(useFrequency);
        String strVal = (concStr != null) ? concStr.trim() : "";
        if ( !strVal.isEmpty() )
            setConcentration(new NumericString(strVal, GAS_CONCENTRATION_UNIT));
        strVal = (accStr != null) ? accStr.trim() : "";
        if ( !strVal.isEmpty() )
            setAccuracy(new NumericString(strVal, GAS_CONCENTRATION_UNIT));
    }

    /**
     * @return set of field names that are currently invalid
     */
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalid = new HashSet<String>();
        if ( type.isEmpty() )
            invalid.add("type");
        if ( supplier.isEmpty() )
            invalid.add("supplier");
        if ( !concentration.isValid() )
            invalid.add("concentration");
        return invalid;
    }

    /**
     * @return ID for the calibration gas, e.g., LL83539; never null but may be empty
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *         assign as the ID for the calibration gas, e.g., LL83539; if null, an empty string is assigned
     */
    public void setId(String id) {
        this.id = (id != null) ? id.trim() : "";
    }

    /**
     * @return type of gas being calibrated, e.g., CO2; never null but may be empty
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *         assign as the type of gas being calibrated, e.g., CO2; if null, an empty string is assigned
     */
    public void setType(String type) {
        this.type = (type != null) ? type.trim() : "";
    }

    /**
     * @return supplier or manufacturer of the calibration gas; never null but may be empty
     */
    public String getSupplier() {
        return supplier;
    }

    /**
     * @param supplier
     *         assign as the supplier or manufacturer of the calibration gas; if null, an empty string is assigned
     */
    public void setSupplier(String supplier) {
        this.supplier = (supplier != null) ? supplier.trim() : "";
    }

    /**
     * @return frequency of calibration using this gas; never null but may be empty
     */
    public String getUseFrequency() {
        return useFrequency;
    }

    /**
     * @param useFrequency
     *         assign as the frequency of calibration using this gas; if null, an empty string is assigned
     */
    public void setUseFrequency(String useFrequency) {
        this.useFrequency = (useFrequency != null) ? useFrequency.trim() : "";
    }

    /**
     * @return concentration of the gas being calibrated; never null but may be empty.
     *         If not empty, guaranteed to represent a non-negative finite number.
     */
    public NumericString getConcentration() {
        return (NumericString) (concentration.duplicate(null));
    }

    /**
     * @param concentration
     *         assign as the concentration of the gas being calibrated; if null, an empty string is assigned
     *
     * @throws IllegalArgumentException
     *         if the concentration given, if not null, does not represent a non-negative finite number
     */
    public void setConcentration(NumericString concentration) throws IllegalArgumentException {
        if ( concentration != null ) {
            if ( !concentration.isNonNegative() )
                throw new IllegalArgumentException("concentration specified is not a finite non-negative number");
            this.concentration = (NumericString) (concentration.duplicate(null));
        }
        else
            this.concentration = new NumericString(null, GAS_CONCENTRATION_UNIT);
    }

    /**
     * @return accuracy of the concentration of the gas being calibrated; never null but may be empty.
     *         If not empty, guaranteed to represent a positive finite number.
     */
    public NumericString getAccuracy() {
        return (NumericString) (accuracy.duplicate(null));
    }

    /**
     * @param accuracy
     *         assign as the accuracy of the concentration of the gas being calibrated;
     *         if null, an empty string is assigned
     *
     * @throws IllegalArgumentException
     *         if the accuracy given, if not null, does not represent a positive finite number, or
     *         if the unit of the accuracy given is not {@link #GAS_CONCENTRATION_UNIT}
     */
    public void setAccuracy(NumericString accuracy) throws IllegalArgumentException {
        if ( accuracy != null ) {
            if ( !accuracy.isPositive() )
                throw new IllegalArgumentException("accuracy specified is not a finite positive number");
            if ( !GAS_CONCENTRATION_UNIT.equals(accuracy.getUnitString()) )
                throw new IllegalArgumentException(
                        "accuracy specified is not in units of " + GAS_CONCENTRATION_UNIT);
            this.accuracy = (NumericString) (accuracy.duplicate(null));
        }
        else
            this.accuracy = new NumericString(null, GAS_CONCENTRATION_UNIT);
    }

    /**
     * @return if this calibration gas is a non-zero gas standard
     *
     * @throws IllegalArgumentException
     *         if the gas concentration or the accuracy of the gas concentration is not specified (is NaN)
     */
    public boolean isNonZero() throws IllegalArgumentException {
        if ( !concentration.isValid() )
            throw new IllegalArgumentException("gas concentration is not given");
        if ( concentration.getNumericValue() == 0.0 )
            return false;
        if ( !accuracy.isValid() )
            throw new IllegalArgumentException("gas concentration accuracy is not given");
        return (concentration.getNumericValue() > accuracy.getNumericValue());
    }

    @Override
    public Object duplicate(Object dup) {
        CalibrationGas gas;
        if ( dup == null )
            gas = new CalibrationGas();
        else
            gas = (CalibrationGas) dup;
        gas.id = id;
        gas.type = type;
        gas.supplier = supplier;
        gas.useFrequency = useFrequency;
        gas.concentration = (NumericString) (concentration.duplicate(null));
        gas.accuracy = (NumericString) (accuracy.duplicate(null));
        return gas;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof CalibrationGas) )
            return false;

        CalibrationGas that = (CalibrationGas) obj;

        if ( !id.equals(that.id) )
            return false;
        if ( !type.equals(that.type) )
            return false;
        if ( !supplier.equals(that.supplier) )
            return false;
        if ( !useFrequency.equals(that.useFrequency) )
            return false;
        if ( !concentration.equals(that.concentration) )
            return false;
        if ( !accuracy.equals(that.accuracy) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = id.hashCode();
        result = result * prime + type.hashCode();
        result = result * prime + supplier.hashCode();
        result = result * prime + useFrequency.hashCode();
        result = result * prime + concentration.hashCode();
        result = result * prime + accuracy.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CalibrationGas" +
                "{ id='" + id + "'" +
                ", type='" + type + "'" +
                ", supplier='" + supplier + "'" +
                ", useFrequency='" + useFrequency + "'" +
                ", concentration=" + concentration +
                ", accuracy=" + accuracy +
                " }";
    }

}
