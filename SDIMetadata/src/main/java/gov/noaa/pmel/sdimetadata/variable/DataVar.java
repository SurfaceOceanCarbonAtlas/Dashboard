package gov.noaa.pmel.sdimetadata.variable;

import gov.noaa.pmel.sdimetadata.person.Person;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Information about a generic data variable in a dataset.
 */
public class DataVar extends Variable implements Cloneable {

    protected String observeType;
    protected MethodType measureMethod;
    protected String methodDescription;
    protected String methodReference;
    protected String samplingLocation;
    protected String samplingElevation;
    protected String storageMethod;
    protected String replication;
    protected Person researcher;
    protected ArrayList<String> samplerNames;
    protected ArrayList<String> analyzerNames;

    /**
     * Create with all fields empty.
     */
    public DataVar() {
        super();
        observeType = "";
        measureMethod = MethodType.UNSPECIFIED;
        methodDescription = "";
        methodReference = "";
        samplingLocation = "";
        samplingElevation = "";
        storageMethod = "";
        replication = "";
        researcher = new Person();
        samplerNames = new ArrayList<String>();
        analyzerNames = new ArrayList<String>();
    }

    /**
     * Create using the basic values in the given variable
     */
    public DataVar(Variable var) {
        this();
        if ( var != null ) {
            colName = var.colName;
            fullName = var.fullName;
            varUnit = var.varUnit;
            flagColName = var.flagColName;
            accuracy = var.accuracy.clone();
            precision = var.precision.clone();
            addnInfo = new ArrayList<String>(var.addnInfo);
        }
    }

    /**
     * @return list of field names that are currently invalid
     */
    @Override
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalid = new HashSet<String>();
        if ( colName.isEmpty() )
            invalid.add("colName");
        if ( fullName.isEmpty() )
            invalid.add("fullName");
        if ( observeType.isEmpty() )
            invalid.add("observeType");
        if ( !accuracy.isValid() )
            invalid.add("accuracy");
        switch ( measureMethod ) {
            case UNSPECIFIED:
                invalid.add("measureMethod");
                break;
            case COMPUTED:
                if ( methodDescription.isEmpty() )
                    invalid.add("methodDescription");
                break;
            default:
                if ( samplerNames.isEmpty() && analyzerNames.isEmpty() ) {
                    invalid.add("samplerNames");
                    invalid.add("analyzerNames");
                }
        }
        return invalid;
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
     *         assign as the sampling location for this variable; if null, an empty string is assigned
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
     *         assign as the sampling height / depth for this variable; if null, an empty string is assigned
     */
    public void setSamplingElevation(String samplingElevation) {
        this.samplingElevation = (samplingElevation != null) ? samplingElevation.trim() : "";
    }

    /**
     * @return information about sample storage prior to measuring this variable; never null but may be empty
     */
    public String getStorageMethod() {
        return storageMethod;
    }

    /**
     * @param storageMethod
     *         assign as information about sample storage prior to measuring this variable;
     *         if null, an empty string is assigned
     */
    public void setStorageMethod(String storageMethod) {
        this.storageMethod = (storageMethod != null) ? storageMethod.trim() : "";
    }

    /**
     * @return replication information about this variable; never null but may be empty
     */
    public String getReplication() {
        return replication;
    }

    /**
     * @param replication
     *         assign as replication information about this variable; if null, an empty string is assigned
     */
    public void setReplication(String replication) {
        this.replication = (replication != null) ? replication.trim() : "";
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

    @Override
    public DataVar clone() {
        DataVar dup = (DataVar) super.clone();
        dup.observeType = observeType;
        dup.measureMethod = measureMethod;
        dup.methodDescription = methodDescription;
        dup.methodReference = methodReference;
        dup.samplingLocation = samplingLocation;
        dup.samplingElevation = samplingElevation;
        dup.storageMethod = storageMethod;
        dup.replication = replication;
        dup.researcher = researcher.clone();
        dup.samplerNames = new ArrayList<String>(samplerNames);
        dup.analyzerNames = new ArrayList<String>(analyzerNames);
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof DataVar) )
            return false;
        if ( !super.equals(obj) )
            return false;

        DataVar dataVar = (DataVar) obj;

        if ( !observeType.equals(dataVar.observeType) )
            return false;
        if ( measureMethod != dataVar.measureMethod )
            return false;
        if ( !methodDescription.equals(dataVar.methodDescription) )
            return false;
        if ( !methodReference.equals(dataVar.methodReference) )
            return false;
        if ( !samplingLocation.equals(dataVar.samplingLocation) )
            return false;
        if ( !samplingElevation.equals(dataVar.samplingElevation) )
            return false;
        if ( !storageMethod.equals(dataVar.storageMethod) )
            return false;
        if ( !replication.equals(dataVar.replication) )
            return false;
        if ( !researcher.equals(dataVar.researcher) )
            return false;
        if ( !samplerNames.equals(dataVar.samplerNames) )
            return false;
        if ( !analyzerNames.equals(dataVar.analyzerNames) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + observeType.hashCode();
        result = result * prime + measureMethod.hashCode();
        result = result * prime + methodDescription.hashCode();
        result = result * prime + methodReference.hashCode();
        result = result * prime + samplingLocation.hashCode();
        result = result * prime + samplingElevation.hashCode();
        result = result * prime + storageMethod.hashCode();
        result = result * prime + replication.hashCode();
        result = result * prime + researcher.hashCode();
        result = result * prime + samplerNames.hashCode();
        result = result * prime + analyzerNames.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst("Variable", "DataVar");
        return repr.substring(0, repr.length() - 1) +
                ", observeType='" + observeType + '\'' +
                ", measureMethod=" + measureMethod +
                ", methodDescription='" + methodDescription + '\'' +
                ", methodReference='" + methodReference + '\'' +
                ", samplingLocation='" + samplingLocation + '\'' +
                ", samplingElevation='" + samplingElevation + '\'' +
                ", storageMethod='" + storageMethod + '\'' +
                ", replication='" + replication + '\'' +
                ", researcher=" + researcher +
                ", samplerNames=" + samplerNames +
                ", analyzerNames=" + analyzerNames +
                '}';
    }
}

