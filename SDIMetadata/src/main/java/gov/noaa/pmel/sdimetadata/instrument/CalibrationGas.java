package gov.noaa.pmel.sdimetadata.instrument;

import sun.plugin.dom.exception.InvalidStateException;

/**
 * Describes a standard gas mixture used for calibration of instruments.
 */
public class CalibrationGas implements Cloneable {

    protected String id;
    protected String type;
    protected String supplier;
    protected Double concUMolPerMol;
    protected Double accuracyUMolPerMol;

    /**
     * Assign with all fields empty or NaN
     */
    public CalibrationGas() {
        id = "";
        type = "";
        supplier = "";
        concUMolPerMol = Double.NaN;
        accuracyUMolPerMol = Double.NaN;
    }

    /**
     * Assign the the given arguments passed to their corresponding setters.
     */
    public CalibrationGas(String id, String type, String supplier, Double concUMolPerMol, Double accuracyUMolPerMol) {
        setId(id);
        setType(type);
        setSupplier(supplier);
        setConcUMolPerMol(concUMolPerMol);
        setAccuracyUMolPerMol(accuracyUMolPerMol);
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
     *         assign as the supplier or manufacturer of the calibration gas; if null, an emptry string is assigned
     */
    public void setSupplier(String supplier) {
        this.supplier = (supplier != null) ? supplier.trim() : "";
    }

    /**
     * @return concentration of the gas being calibrated, in micromole per mole (PPM); never null but may be NaN
     */
    public Double getConcUMolPerMol() {
        return concUMolPerMol;
    }

    /**
     * @param concUMolPerMol
     *         assign as the concentration of the gas being calibrated, in micromole per mole (PPM);
     *         if null, NaN is assigned
     *
     * @throws IllegalArgumentException
     *         if the concentration given (not null) is infinite or negative
     */
    public void setConcUMolPerMol(Double concUMolPerMol) throws IllegalArgumentException {
        if ( concUMolPerMol != null ) {
            if ( concUMolPerMol.isInfinite() )
                throw new IllegalArgumentException("infinite gas concentration given");
            if ( concUMolPerMol < 0.0 )
                throw new IllegalArgumentException("negative gas concentration given");
            this.concUMolPerMol = concUMolPerMol;
        }
        else
            this.concUMolPerMol = Double.NaN;
    }

    /**
     * @return accuracy of the concentration of the gas being calibrated, in micromole per mole (PPM);
     *         never null but may be NaN
     */
    public Double getAccuracyUMolPerMol() {
        return accuracyUMolPerMol;
    }

    /**
     * @param accuracyUMolPerMol
     *         assign as the accuracy of the concentration of the gas being calibrated,
     *         in micromole per mole (PPM); if null, NaN is assigned
     *
     * @throws IllegalArgumentException
     *         if the accuracy given (not null) is infinite or not positive
     */
    public void setAccuracyUMolPerMol(Double accuracyUMolPerMol) throws IllegalArgumentException {
        if ( accuracyUMolPerMol != null ) {
            if ( accuracyUMolPerMol.isInfinite() )
                throw new IllegalArgumentException("infinite gas concentration accuracy given");
            if ( accuracyUMolPerMol <= 0.0 )
                throw new IllegalArgumentException("gas concentration accuracy given is not positive");
            this.accuracyUMolPerMol = accuracyUMolPerMol;
        }
        else
            this.accuracyUMolPerMol = Double.NaN;
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
        if ( concUMolPerMol.isNaN() )
            return false;
        if ( accuracyUMolPerMol.isNaN() )
            return false;

        return true;
    }

    /**
     * @return if this calibration gas is a non-zero gas standard
     *
     * @throws InvalidStateException
     *         if the gas concentration or the accuracy of the gas concentration is not specified (is NaN)
     */
    public boolean isNonZero() throws InvalidStateException {
        if ( concUMolPerMol.isNaN() )
            throw new InvalidStateException("gas concentration is not given");
        if ( accuracyUMolPerMol.isNaN() )
            throw new InvalidStateException("gas concentration accuracy is not given");
        return (concUMolPerMol > accuracyUMolPerMol);
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
        dup.concUMolPerMol = concUMolPerMol;
        dup.accuracyUMolPerMol = accuracyUMolPerMol;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof CalibrationGas) )
            return false;

        CalibrationGas that = (CalibrationGas) obj;

        if ( !id.equals(that.id) )
            return false;
        if ( !type.equals(that.type) )
            return false;
        if ( !supplier.equals(that.supplier) )
            return false;
        if ( !concUMolPerMol.equals(that.concUMolPerMol) )
            return false;
        if ( !accuracyUMolPerMol.equals(that.accuracyUMolPerMol) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = id.hashCode();
        result = result * prime + type.hashCode();
        result = result * prime + supplier.hashCode();
        result = result * prime + concUMolPerMol.hashCode();
        result = result * prime + accuracyUMolPerMol.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CalibrationGas{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", supplier='" + supplier + '\'' +
                ", concUMolPerMol=" + concUMolPerMol +
                ", accuracyUMolPerMol=" + accuracyUMolPerMol +
                '}';
    }

}

