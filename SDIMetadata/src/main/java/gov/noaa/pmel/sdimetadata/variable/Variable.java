package gov.noaa.pmel.sdimetadata.variable;

import gov.noaa.pmel.sdimetadata.person.Person;

import java.util.ArrayList;

/**
 * Information about a generic data variable in a dataset.
 */
public class Variable implements Cloneable {

    protected String colName;
    protected String fullName;
    protected String unit;
    protected String observeType;
    protected MethodType measureMethod;
    protected String methodDescription;
    protected String methodReference;
    protected String samplingLocation;
    protected String poison;
    protected String samplerName;
    protected String sensorName;
    protected Double uncertainty;
    protected String uncertaintyUnit;
    protected String flagType;
    protected Person researcher;
    protected ArrayList<String> addnInfo;

    /**
     * Create with all fields empty or NaN.
     */
    public Variable() {
        colName = "";
        fullName = "";
        unit = "";
        observeType = "";
        measureMethod = MethodType.UNSPECIFIED;
        methodDescription = "";
        methodReference = "";
        samplingLocation = "";
        poison = "";
        samplerName = "";
        sensorName = "";
        uncertainty = Double.NaN;
        uncertaintyUnit = "";
        flagType = "";
        researcher = new Person();
        addnInfo = new ArrayList<String>();
    }

    /**
     * @return column name of this variable; never null but may be an empty string
     */
    public String getColName() {
        return colName;
    }

    /**
     * @param colName
     *         assign as the column name of this variable; if null, an empty string is assigned
     */
    public void setColName(String colName) {
        this.colName = (colName != null) ? colName.trim() : "";
    }

    /**
     * @return full name of this variable; never null but may be an empty string
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName
     *         assign as the full name of this variable; if null, an empty string is assigned
     */
    public void setFullName(String fullName) {
        this.fullName = (fullName != null) ? fullName.trim() : "";
    }


    /**
     * @return the unit of values for this variable; never null but may be an empty string
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit
     *         assign as the unit for values of this variable; if null, an empty string is assigned
     */
    public void setUnit(String unit) {
        this.unit = (unit != null) ? unit.trim() : "";
    }

    /**
     * @return the observation type of this variable; never null but may be empty
     */
    public String getObserveType() {
        return observeType;
    }

    /**
     * @param observeType
     *         assign as the observation type of this variable; if null, an empty string is assigned
     */
    public void setObserveType(String observeType) {
        this.observeType = (observeType != null) ? observeType.trim() : "";
    }

    /**
     * @return the method of measuring this variable; never null but may be {@link MethodType#UNSPECIFIED}
     */
    public MethodType getMeasureMethod() {
        return measureMethod;
    }

    /**
     * @param measureMethod
     *         assign as the method of measuring this variable; if null, {@link MethodType#UNSPECIFIED} is assigned
     */
    public void setMeasureMethod(MethodType measureMethod) {
        this.measureMethod = (measureMethod != null) ? measureMethod : MethodType.UNSPECIFIED;
    }

    /**
     * @return description of the method for computing this variable; never null but may be empty
     */
    public String getMethodDescription() {
        return methodDescription;
    }

    /**
     * @param methodDescription
     *         assign as the method for computing this variable; if null, an empty string is assigned
     */
    public void setMethodDescription(String methodDescription) {
        this.methodDescription = (methodDescription != null) ? methodDescription.trim() : "";
    }

    /**
     * @return the reference for the method used to obtain the values of this variable; never null but may be empty
     */
    public String getMethodReference() {
        return methodReference;
    }

    /**
     * @param methodReference
     *         assign as the reference for the method used to obtain the values of this variable;
     *         if null, an empty string is assigned
     */
    public void setMethodReference(String methodReference) {
        this.methodReference = (methodReference != null) ? methodReference.trim() : "";
    }

    /**
     * @return sampling location for this variable; never null but may be an empty string
     */
    public String getSamplingLocation() {
        return samplingLocation;
    }

    /**
     * @param samplingLocation
     *         assign as the sampling location for this variable; if null, an emptry string is assigned
     */
    public void setSamplingLocation(String samplingLocation) {
        this.samplingLocation = (samplingLocation != null) ? samplingLocation.trim() : "";
    }

    /**
     * @return the poison used in the samples from which this variable was obtained; never null but may be empty
     */
    public String getPoison() {
        return poison;
    }

    /**
     * @param poison
     *         assign as the poison used in the samples from which this variable was obtained;
     *         if null, an empty string is assigned
     */
    public void setPoison(String poison) {
        this.poison = (poison != null) ? poison.trim() : "";
    }

    /**
     * @return the name of the sampling instrument used for this variable; never null but may be empty
     */
    public String getSamplerName() {
        return samplerName;
    }

    /**
     * @param samplerName
     *         assign as the name of the sampling instrument used for this variable;
     *         if null, an empty string is assigned
     */
    public void setSamplerName(String samplerName) {
        this.samplerName = (samplerName != null) ? samplerName.trim() : "";
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
     * @return the uncertainty (accuracy) in values of this variable; never null but may be Double.NaN
     */
    public Double getUncertainty() {
        return uncertainty;
    }

    /**
     * @param uncertainty
     *         assign as the uncertainty (accuracy) in values of this variable; if null, Double.NaN is assigned
     *
     * @throws IllegalArgumentException
     *         if the uncertainty is given (not null) but is infinite or not positive
     */
    public void setUncertainty(Double uncertainty) throws IllegalArgumentException {
        if ( uncertainty != null ) {
            if ( uncertainty.isInfinite() )
                throw new IllegalArgumentException("uncertainty is an infinite value");
            if ( uncertainty <= 0.0 )
                throw new IllegalArgumentException("uncertainty is not a positive value");
            this.uncertainty = uncertainty;
        }
        else
            this.uncertainty = Double.NaN;
    }

    /**
     * @return the unit of the uncertainty (accuracy) value for this variable; never null but may be an empty string
     */
    public String getUncertaintyUnit() {
        return uncertaintyUnit;
    }

    /**
     * @param uncertaintyUnit
     *         assign as the unit of the uncertainty (accuracy) value of this variable;
     *         if null, an empty string is assigned
     */
    public void setUncertaintyUnit(String uncertaintyUnit) {
        this.uncertaintyUnit = (uncertaintyUnit != null) ? uncertaintyUnit.trim() : "";
    }

    /**
     * @return the type of QC flag for this variable; never null but may be empty
     */
    public String getFlagType() {
        return flagType;
    }

    /**
     * @param flagType
     *         assign as the type of QC flag for this variable; if null, an empty string is assigned
     */
    public void setFlagType(String flagType) {
        this.flagType = (flagType != null) ? flagType.trim() : "";
    }

    /**
     * @return reference to the investigator responsible for obtaining this variable;
     *         never null but may not be a valid investigator reference
     */
    public Person getResearcher() {
        return researcher.clone();
    }

    /**
     * @param researcher
     *         assign as the reference to the investigator responsible for obtaining this variable;
     *         if null, an invalid reference (a Person with all-empty fields) is assigned
     */
    public void setResearcher(Person researcher) {
        this.researcher = (researcher != null) ? researcher.clone() : new Person();
    }

    /**
     * @return the list of additional information strings; never null but may be empty.
     *         Any strings given are guaranteed to be strings with content (not blank).
     */
    public ArrayList<String> getAddnInfo() {
        return new ArrayList<String>(addnInfo);
    }

    /**
     * @param addnInfo
     *         assign as the list of additional information strings; if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if any information string given is null or blank
     */
    public void setAddnInfo(Iterable<String> addnInfo) throws IllegalArgumentException {
        this.addnInfo.clear();
        if ( addnInfo != null ) {
            for (String info : addnInfo) {
                if ( info == null )
                    throw new IllegalArgumentException("null information string given");
                info = info.trim();
                if ( info.isEmpty() )
                    throw new IllegalArgumentException("blank information string given");
                this.addnInfo.add(info);
            }
        }
    }

    /**
     * @return whether all the required fields are assigned with valid values.
     */
    public boolean isValid() {
        if ( colName.isEmpty() )
            return false;
        if ( fullName.isEmpty() )
            return false;
        if ( unit.isEmpty() )
            return false;
        if ( observeType.isEmpty() )
            return false;
        if ( measureMethod.equals(MethodType.UNSPECIFIED) )
            return false;
        if ( measureMethod.equals(MethodType.COMPUTED) && methodDescription.isEmpty() )
            return false;
        if ( samplingLocation.isEmpty() )
            return false;
        if ( (!measureMethod.equals(MethodType.COMPUTED)) && samplerName.isEmpty() && sensorName.isEmpty() )
            return false;
        if ( uncertainty.isNaN() )
            return false;
        if ( uncertaintyUnit.isEmpty() )
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
        dup.colName = colName;
        dup.fullName = fullName;
        dup.unit = unit;
        dup.observeType = observeType;
        dup.measureMethod = measureMethod;
        dup.methodDescription = methodDescription;
        dup.methodReference = methodReference;
        dup.samplingLocation = samplingLocation;
        dup.poison = poison;
        dup.samplerName = samplerName;
        dup.sensorName = sensorName;
        dup.uncertainty = uncertainty;
        dup.uncertaintyUnit = uncertaintyUnit;
        dup.flagType = flagType;
        dup.researcher = researcher.clone();
        dup.addnInfo = new ArrayList<String>(addnInfo);
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof Variable) )
            return false;

        Variable variable = (Variable) obj;

        if ( !colName.equals(variable.colName) )
            return false;
        if ( !fullName.equals(variable.fullName) )
            return false;
        if ( !unit.equals(variable.unit) )
            return false;
        if ( !observeType.equals(variable.observeType) )
            return false;
        if ( measureMethod != variable.measureMethod )
            return false;
        if ( !methodDescription.equals(variable.methodDescription) )
            return false;
        if ( !methodReference.equals(variable.methodReference) )
            return false;
        if ( !samplingLocation.equals(variable.samplingLocation) )
            return false;
        if ( !poison.equals(variable.poison) )
            return false;
        if ( !samplerName.equals(variable.samplerName) )
            return false;
        if ( !sensorName.equals(variable.sensorName) )
            return false;
        if ( !uncertainty.equals(variable.uncertainty) )
            return false;
        if ( !uncertaintyUnit.equals(variable.uncertaintyUnit) )
            return false;
        if ( !flagType.equals(variable.flagType) )
            return false;
        if ( !researcher.equals(variable.researcher) )
            return false;
        if ( !addnInfo.equals(variable.addnInfo) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = colName.hashCode();
        result = result * prime + fullName.hashCode();
        result = result * prime + unit.hashCode();
        result = result * prime + observeType.hashCode();
        result = result * prime + measureMethod.hashCode();
        result = result * prime + methodDescription.hashCode();
        result = result * prime + methodReference.hashCode();
        result = result * prime + samplingLocation.hashCode();
        result = result * prime + poison.hashCode();
        result = result * prime + samplerName.hashCode();
        result = result * prime + sensorName.hashCode();
        result = result * prime + uncertainty.hashCode();
        result = result * prime + uncertaintyUnit.hashCode();
        result = result * prime + flagType.hashCode();
        result = result * prime + researcher.hashCode();
        result = result * prime + addnInfo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "colName='" + colName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", unit='" + unit + '\'' +
                ", observeType='" + observeType + '\'' +
                ", measureMethod=" + measureMethod +
                ", methodDescription='" + methodDescription + '\'' +
                ", methodReference='" + methodReference + '\'' +
                ", samplingLocation='" + samplingLocation + '\'' +
                ", poison='" + poison + '\'' +
                ", samplerName='" + samplerName + '\'' +
                ", sensorName='" + sensorName + '\'' +
                ", uncertainty=" + uncertainty +
                ", uncertaintyUnit='" + uncertaintyUnit + '\'' +
                ", flagType='" + flagType + '\'' +
                ", researcher=" + researcher +
                ", addnInfo=" + addnInfo +
                '}';
    }

}

