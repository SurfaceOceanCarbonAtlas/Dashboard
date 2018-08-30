package gov.noaa.pmel.sdimetadata.instrument;


import gov.noaa.pmel.sdimetadata.util.NumericString;

/**
 * Describes a standard gas mixture used for calibration of instruments.
 */
public class CalibrationGas implements Cloneable {

    public static final String GAS_CONCENTRATION_UNIT = "umol/mol";

    protected String id;
    protected String type;
    protected String supplier;
    protected NumericString concentration;
    protected NumericString accuracy;

    /**
     * Assign with all fields empty or NaN
     */
    public CalibrationGas() {
        id = "";
        type = "";
        supplier = "";
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
     *         assign as the concentration, in umol/mol, of the gas being calibrated;
     *         if null or blank, an empty string is assigned
     * @param accStr
     *         assign as the accuracy, in umol/mol, of the concentration of the gas being calibrated;
     *         if null or blank, an empty string is assigned
     *
     * @throws IllegalArgumentException
     *         if the concentration given, if not null and not blank, does not represent a non-negative finite number,
     *         or if the accuracy given, if not null and not blank, does not represent a positive finite number
     */
    public CalibrationGas(String id, String type, String supplier, String concStr, String accStr)
            throws IllegalArgumentException {
        this();
        setId(id);
        setType(type);
        setSupplier(supplier);
        String strVal = (concStr != null) ? concStr.trim() : "";
        if ( !strVal.isEmpty() )
            setConcentration(new NumericString(strVal, GAS_CONCENTRATION_UNIT));
        strVal = (accStr != null) ? accStr.trim() : "";
        if ( !strVal.isEmpty() )
            setAccuracy(new NumericString(strVal, GAS_CONCENTRATION_UNIT));
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
     * @return concentration of the gas being calibrated; never null but may be empty.
     *         If not empty, guaranteed to represent a non-negative finite number.
     */
    public NumericString getConcentration() {
        return concentration.clone();
    }

    /**
     * @param concentration
     *         assign as the concentration of the gas being calibrated; if null, an empty string is assigned
     *
     * @throws IllegalArgumentException
     *         if the concentration given, if not null, does not represent a non-negative finite number, or
     *         if the unit of the concentration given is not {@link #GAS_CONCENTRATION_UNIT}
     */
    public void setConcentration(NumericString concentration) throws IllegalArgumentException {
        if ( concentration != null ) {
            if ( !concentration.isNonNegative() )
                throw new IllegalArgumentException("concentration specified is not a finite non-negative number");
            if ( !GAS_CONCENTRATION_UNIT.equals(concentration.getUnitString()) )
                throw new IllegalArgumentException(
                        "concentration specified is not in units of " + GAS_CONCENTRATION_UNIT);
            this.concentration = concentration.clone();
        }
        else
            this.concentration = new NumericString(null, GAS_CONCENTRATION_UNIT);
    }

    /**
     * @return accuracy of the concentration of the gas being calibrated; never null but may be empty.
     *         If not empty, guaranteed to represent a positive finite number.
     */
    public NumericString getAccuracy() {
        return accuracy.clone();
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
            this.accuracy = accuracy.clone();
        }
        else
            this.accuracy = new NumericString(null, GAS_CONCENTRATION_UNIT);
    }

    /**
     * @return if all the required fields are appropriately assigned
     */
    public boolean isValid() {
        if ( id.isEmpty() )
            return false;
        if ( type.isEmpty() )
            return false;
        if ( supplier.isEmpty() )
            return false;
        if ( !concentration.isValid() )
            return false;
        if ( !accuracy.isValid() )
            return false;

        return true;
    }

    /**
     * @return if this calibration gas is a non-zero gas standard
     *
     * @throws IllegalStateException
     *         if the gas concentration or the accuracy of the gas concentration is not specified (is NaN)
     */
    public boolean isNonZero() throws IllegalStateException {
        if ( !concentration.isValid() )
            throw new IllegalStateException("gas concentration is not given");
        if ( !accuracy.isValid() )
            throw new IllegalStateException("gas concentration accuracy is not given");
        return (concentration.numericValue() > accuracy.numericValue());
    }

    @Override
    public CalibrationGas clone() {
        CalibrationGas dup;
        try {
            dup = (CalibrationGas) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.id = id;
        dup.type = type;
        dup.supplier = supplier;
        dup.concentration = concentration.clone();
        dup.accuracy = accuracy.clone();
        return dup;
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
        result = result * prime + concentration.hashCode();
        result = result * prime + accuracy.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CalibrationGas{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", supplier='" + supplier + '\'' +
                ", concentration=" + concentration +
                ", accuracy=" + accuracy +
                '}';
    }

}

