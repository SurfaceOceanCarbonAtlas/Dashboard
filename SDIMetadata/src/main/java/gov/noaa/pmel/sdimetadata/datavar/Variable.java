package gov.noaa.pmel.sdimetadata.datavar;

public class Variable implements Cloneable {

    protected String varName;
    protected String varUnit;
    protected String description;
    protected String calibration;
    protected Double precision;
    protected String precisionUnit;
    protected Double accuracy;
    protected String accuracyUnit;
    protected String sensorName;

    public Variable() {
        varName = "";
        varUnit = "";
        description = "";
        calibration = "";
        precision = Double.NaN;
        precisionUnit = "";
        accuracy = Double.NaN;
        accuracyUnit = "";
        sensorName = "";
    }

    /**
     * @return name of this variable; never null but may be an empty string
     */
    public String getVarName() {
        return varName;
    }

    /**
     * @param varName
     *         assign as the name of this variable; if null, an empty string is assigned
     */
    public void setVarName(String varName) {
        this.varName = (varName != null) ? varName.trim() : "";
    }

    /**
     * @return the unit of values for this variable; never null but may be an empty string
     */
    public String getVarUnit() {
        return varUnit;
    }

    /**
     * @param varUnit
     *         assign as the unit for values of this variable; if null, an empty string is assigned
     */
    public void setVarUnit(String varUnit) {
        this.varUnit = (varUnit != null) ? varUnit.trim() : "";
    }

    /**
     * @return description of this variable; never null but may be an empty string
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *         assign as the description of this variable; if null, an empty string is assigned
     */
    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
    }

    /**
     * @return calibration information for this variable; never null but may be an empty string
     */
    public String getCalibration() {
        return calibration;
    }

    /**
     * @param calibration
     *         assign as the calibration information for this variable; if null, an empty string is assigned
     */
    public void setCalibration(String calibration) {
        this.calibration = (calibration != null) ? calibration.trim() : "";
    }

    /**
     * @return the precision/resolution in values of this variable; never null but may be Double.NaN
     */
    public Double getPrecision() {
        return precision;
    }

    /**
     * @param precision
     *         assign as the precision/resolution in values of this variable; if null, Double.NaN is assigned
     */
    public void setPrecision(Double precision) {
        this.precision = (precision != null) ? precision : Double.NaN;
    }

    /**
     * @return the unit of the precision value for this variable; never null but may be an empty string
     */
    public String getPrecisionUnit() {
        return precisionUnit;
    }

    /**
     * @param precisionUnit
     *         assign as the unit of the precision value of this variable; if null, an empty string is assigned
     */
    public void setPrecisionUnit(String precisionUnit) {
        this.precisionUnit = (precisionUnit != null) ? precisionUnit.trim() : "";
    }

    /**
     * @return the accuracy/uncertainty in values of this variable; never null but may be Double.NaN
     */
    public Double getAccuracy() {
        return accuracy;
    }

    /**
     * @param accuracy
     *         assign as the accuracy/uncertainty in values of this variable; if null, Double.NaN is assigned
     */
    public void setAccuracy(Double accuracy) {
        this.accuracy = (accuracy != null) ? accuracy : Double.NaN;
    }

    /**
     * @return the unit of the accuracy value for this variable; never null but may be an empty string
     */
    public String getAccuracyUnit() {
        return accuracyUnit;
    }

    /**
     * @param accuracyUnit
     *         assign as the unit of the accuracy value of this variable; if null, an empty string is assigned
     */
    public void setAccuracyUnit(String accuracyUnit) {
        this.accuracyUnit = (accuracyUnit != null) ? accuracyUnit.trim() : "";
    }

    /**
     * @return the name of the sensor measuring this variable; never null but may be an empty string
     */
    public String getSensorName() {
        return sensorName;
    }

    /**
     * @param sensorName
     *         assign as the name of the sensor measuring this variable; if null, an empty string is assigned
     */
    public void setSensorName(String sensorName) {
        this.sensorName = (sensorName != null) ? sensorName.trim() : "";
    }

    /**
     * @return whether all the required fields are assigned with valid values.
     */
    public boolean isValid() {
        if ( varName.isEmpty() )
            return false;
        if ( varUnit.isEmpty() )
            return false;
        if ( description.isEmpty() )
            return false;
        if ( precision.isNaN() )
            return false;
        if ( precisionUnit.isEmpty() )
            return false;
        if ( accuracy.isNaN() )
            return false;
        if ( accuracyUnit.isEmpty() )
            return false;
        if ( sensorName.isEmpty() )
            return false;
        return true;
    }

    @Override
    public Variable clone() {
        Variable dup;
        try {
            dup = (Variable) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.varName = varName;
        dup.varUnit = varUnit;
        dup.description = description;
        dup.calibration = calibration;
        dup.precision = precision;
        dup.precisionUnit = precisionUnit;
        dup.accuracy = accuracy;
        dup.accuracyUnit = accuracyUnit;
        dup.sensorName = sensorName;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof Variable) )
            return false;

        Variable variable = (Variable) obj;

        if ( !varName.equals(variable.varName) )
            return false;
        if ( !varUnit.equals(variable.varUnit) )
            return false;
        if ( !description.equals(variable.description) )
            return false;
        if ( !calibration.equals(variable.calibration) )
            return false;
        if ( !precision.equals(variable.precision) )
            return false;
        if ( !precisionUnit.equals(variable.precisionUnit) )
            return false;
        if ( !accuracy.equals(variable.accuracy) )
            return false;
        if ( !accuracyUnit.equals(variable.accuracyUnit) )
            return false;
        if ( !sensorName.equals(variable.sensorName) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = varName.hashCode();
        result = result * prime + varUnit.hashCode();
        result = result * prime + description.hashCode();
        result = result * prime + calibration.hashCode();
        result = result * prime + precision.hashCode();
        result = result * prime + precisionUnit.hashCode();
        result = result * prime + accuracy.hashCode();
        result = result * prime + accuracyUnit.hashCode();
        result = result * prime + sensorName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "varName='" + varName + '\'' +
                ", varUnit='" + varUnit + '\'' +
                ", description='" + description + '\'' +
                ", calibration='" + calibration + '\'' +
                ", precision=" + precision +
                ", precisionUnit='" + precisionUnit + '\'' +
                ", accuracy=" + accuracy +
                ", accuracyUnit='" + accuracyUnit + '\'' +
                ", sensorName='" + sensorName + '\'' +
                '}';
    }

}

