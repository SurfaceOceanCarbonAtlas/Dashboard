package gov.noaa.pmel.sdimetadata.variable;

import gov.noaa.pmel.sdimetadata.person.Person;
import gov.noaa.pmel.sdimetadata.util.NumericString;

import java.util.ArrayList;

/**
 * Information about a generic data variable in a dataset.
 */
public class Variable implements Cloneable {

    protected String colName;
    protected String fullName;
    protected String varUnit;
    protected String observeType;
    protected MethodType measureMethod;
    protected String methodDescription;
    protected String methodReference;
    protected String samplingLocation;
    protected String samplingElevation;
    protected String flagType;
    protected NumericString accuracy;
    protected NumericString precision;
    protected Person researcher;
    protected ArrayList<String> samplerNames;
    protected ArrayList<String> analyzerNames;
    protected ArrayList<String> addnInfo;

    /**
     * Create with all fields empty.
     */
    public Variable() {
        colName = "";
        fullName = "";
        varUnit = "";
        observeType = "";
        measureMethod = MethodType.UNSPECIFIED;
        methodDescription = "";
        methodReference = "";
        samplingLocation = "";
        samplingElevation = "";
        flagType = "";
        accuracy = new NumericString();
        precision = new NumericString();
        researcher = new Person();
        samplerNames = new ArrayList<String>();
        analyzerNames = new ArrayList<String>();
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
     * @return sampling height / depth for this variable; never null but may be an empty string
     */
    public String getSamplingElevation() {
        return samplingElevation;
    }

    /**
     * @param samplingElevation
     *         assign as the sampling height / depth for this variable; if null, an emptry string is assigned
     */
    public void setSamplingElevation(String samplingElevation) {
        this.samplingElevation = (samplingElevation != null) ? samplingElevation.trim() : "";
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
     * @return the accuracy (uncertainty) in values of this variable; never null but may be empty.
     * If not empty, guaranteed to represent a finite positive number.
     */
    public NumericString getAccuracy() {
        return accuracy.clone();
    }

    /**
     * @param accuracy
     *         assign as the accuracy (uncertainty) in values of this variable;
     *         if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if the accuracy is given (not null) but is not a finite positive number
     */
    public void setAccuracy(NumericString accuracy) throws IllegalArgumentException {
        if ( accuracy != null ) {
            if ( ! accuracy.isPositive() )
                throw new IllegalArgumentException("accuracy is not a finite positive number");
            this.accuracy = accuracy.clone();
        }
        else
            this.accuracy = new NumericString();
    }

    /**
     * @return the precision (resolution) in values of this variable; never null but may be empty.
     * If not empty, guaranteed to represent a finite positive number.
     */
    public NumericString getPrecision() {
        return precision.clone();
    }

    /**
     * @param precision
     *         assign as the precision (resolution) in values of this variable;
     *         if null, an empty NumericString is assigned
     *
     * @throws IllegalArgumentException
     *         if the precision is given (not null) but is not a finite positive number
     */
    public void setPrecision(NumericString precision) {
        if ( precision != null ) {
            if ( ! precision.isPositive() )
                throw new IllegalArgumentException("precision is not a finite positive number");
            this.precision = precision.clone();
        }
        else
            this.precision = new NumericString();
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
     * @return the list of names of analyzers used to collect this variable; never null but may be empty.
     *         Any strings given are guaranteed to be strings with content (not blank).
     */
    public ArrayList<String> getSamplerNames() {
        return new ArrayList<String>(samplerNames);
    }

    /**
     * @param samplerNames
     *         assign as the list of names of samplers used to collect this variable;
     *         if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if any sampler name given is null or blank
     */
    public void setSamplerNames(Iterable<String> samplerNames) throws IllegalArgumentException {
        this.samplerNames.clear();
        if ( samplerNames != null ) {
            for (String name : samplerNames) {
                if ( name == null )
                    throw new IllegalArgumentException("null sampler name given");
                name = name.trim();
                if ( name.isEmpty() )
                    throw new IllegalArgumentException("blank sampler name given");
                this.samplerNames.add(name);
            }
        }
    }

    /**
     * @return the list of names of analyzers used to measure this variable; never null but may be empty.
     *         Any strings given are guaranteed to be strings with content (not blank).
     */
    public ArrayList<String> getAnalyzerNames() {
        return new ArrayList<String>(analyzerNames);
    }

    /**
     * @param analyzerNames
     *         assign as the list of names of analyzers used to measure this variable;
     *         if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if any analyzer name given is null or blank
     */
    public void setAnalyzerNames(Iterable<String> analyzerNames) throws IllegalArgumentException {
        this.analyzerNames.clear();
        if ( analyzerNames != null ) {
            for (String name : analyzerNames) {
                if ( name == null )
                    throw new IllegalArgumentException("null analyzer name given");
                name = name.trim();
                if ( name.isEmpty() )
                    throw new IllegalArgumentException("blank analyzer name given");
                this.analyzerNames.add(name);
            }
        }
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
        if ( observeType.isEmpty() )
            return false;
        if ( ! accuracy.isValid() )
            return false;
        switch ( measureMethod ) {
            case UNSPECIFIED:
                return false;
            case COMPUTED:
                if ( methodDescription.isEmpty() )
                    return false;
                break;
            default:
                if ( samplerNames.isEmpty() && analyzerNames.isEmpty() )
                    return false;
        }

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
        dup.varUnit = varUnit;
        dup.observeType = observeType;
        dup.measureMethod = measureMethod;
        dup.methodDescription = methodDescription;
        dup.methodReference = methodReference;
        dup.samplingLocation = samplingLocation;
        dup.samplingElevation = samplingElevation;
        dup.flagType = flagType;
        dup.accuracy = accuracy.clone();
        dup.precision = precision.clone();
        dup.researcher = researcher.clone();
        dup.samplerNames = new ArrayList<String>(samplerNames);
        dup.analyzerNames = new ArrayList<String>(analyzerNames);
        dup.addnInfo = new ArrayList<String>(addnInfo);
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof Variable) )
            return false;

        Variable variable = (Variable) obj;

        if ( !colName.equals(variable.colName) )
            return false;
        if ( !fullName.equals(variable.fullName) )
            return false;
        if ( !varUnit.equals(variable.varUnit) )
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
        if ( !samplingElevation.equals(variable.samplingElevation) )
            return false;
        if ( !flagType.equals(variable.flagType) )
            return false;
        if ( !accuracy.equals(variable.accuracy) )
            return false;
        if ( !precision.equals(variable.precision) )
            return false;
        if ( !researcher.equals(variable.researcher) )
            return false;
        if ( !samplerNames.equals(variable.samplerNames) )
            return false;
        if ( !analyzerNames.equals(variable.analyzerNames) )
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
        result = result * prime + varUnit.hashCode();
        result = result * prime + observeType.hashCode();
        result = result * prime + measureMethod.hashCode();
        result = result * prime + methodDescription.hashCode();
        result = result * prime + methodReference.hashCode();
        result = result * prime + samplingLocation.hashCode();
        result = result * prime + samplingElevation.hashCode();
        result = result * prime + flagType.hashCode();
        result = result * prime + accuracy.hashCode();
        result = result * prime + precision.hashCode();
        result = result * prime + researcher.hashCode();
        result = result * prime + samplerNames.hashCode();
        result = result * prime + analyzerNames.hashCode();
        result = result * prime + addnInfo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "colName='" + colName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", varUnit='" + varUnit + '\'' +
                ", observeType='" + observeType + '\'' +
                ", measureMethod=" + measureMethod +
                ", methodDescription='" + methodDescription + '\'' +
                ", methodReference='" + methodReference + '\'' +
                ", samplingLocation='" + samplingLocation + '\'' +
                ", samplingElevation='" + samplingElevation + '\'' +
                ", flagType='" + flagType + '\'' +
                ", accuracy=" + accuracy +
                ", precision=" + precision +
                ", researcher=" + researcher +
                ", samplerNames=" + samplerNames +
                ", analyzerNames=" + analyzerNames +
                ", addnInfo=" + addnInfo +
                '}';
    }

}

