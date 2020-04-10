package gov.noaa.pmel.socatmetadata.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.instrument.Analyzer;
import gov.noaa.pmel.socatmetadata.shared.instrument.Equilibrator;
import gov.noaa.pmel.socatmetadata.shared.instrument.GasSensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;
import gov.noaa.pmel.socatmetadata.shared.instrument.PressureSensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.SalinitySensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.Sampler;
import gov.noaa.pmel.socatmetadata.shared.instrument.TemperatureSensor;
import gov.noaa.pmel.socatmetadata.shared.person.Investigator;
import gov.noaa.pmel.socatmetadata.shared.person.Submitter;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import gov.noaa.pmel.socatmetadata.shared.variable.AirPressure;
import gov.noaa.pmel.socatmetadata.shared.variable.AquGasConc;
import gov.noaa.pmel.socatmetadata.shared.variable.BioDataVar;
import gov.noaa.pmel.socatmetadata.shared.variable.DataVar;
import gov.noaa.pmel.socatmetadata.shared.variable.GasConc;
import gov.noaa.pmel.socatmetadata.shared.variable.Temperature;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class SocatMetadata implements Serializable, IsSerializable {

    private static final long serialVersionUID = 5603918291161606319L;

    protected Submitter submitter;
    protected ArrayList<Investigator> investigators;
    protected Platform platform;
    protected Coverage coverage;
    protected ArrayList<Instrument> instruments;
    protected ArrayList<Variable> variables;
    protected MiscInfo miscInfo;

    /**
     * Create with empty or invalid values for all fields.
     */
    public SocatMetadata() {
        submitter = new Submitter();
        investigators = new ArrayList<Investigator>();
        platform = new Platform();
        coverage = new Coverage();
        instruments = new ArrayList<Instrument>();
        variables = new ArrayList<Variable>();
        miscInfo = new MiscInfo();
    }

    /**
     * @param today
     *         a Datestamp representing the current day; if null, {@link Datestamp#DEFAULT_TODAY_DATESTAMP} is used
     *
     * @return list of field names that are currently invalid
     */
    public HashSet<String> invalidFieldNames(Datestamp today) {
        HashSet<String> invalid = new HashSet<String>();

        for (String name : submitter.invalidFieldNames()) {
            invalid.add("submitter." + name);
        }
        for (int k = 0; k < investigators.size(); k++) {
            for (String name : investigators.get(k).invalidFieldNames()) {
                invalid.add("investigators[" + k + "]." + name);
            }
        }
        for (String name : platform.invalidFieldNames()) {
            invalid.add("platform." + name);
        }
        for (String name : coverage.invalidFieldNames(today)) {
            invalid.add("coverage." + name);
        }
        for (int k = 0; k < instruments.size(); k++) {
            HashSet<String> invalidNames;
            for (String name : instruments.get(k).invalidFieldNames()) {
                invalid.add("instruments[" + k + "]." + name);
            }
        }
        for (int k = 0; k < variables.size(); k++) {
            for (String name : variables.get(k).invalidFieldNames()) {
                invalid.add("variables[" + k + "]." + name);
            }
        }
        for (String name : miscInfo.invalidFieldNames(today)) {
            invalid.add("miscInfo." + name);
        }

        // check that the data times are all within the specified time range
        if ( !(invalid.contains("miscInfo.startDatestamp") || invalid.contains("coverage.earliestDataDate")) ) {
            if ( miscInfo.getStartDatestamp().after(coverage.getEarliestDataDate()) ) {
                invalid.add("miscInfo.startDatestamp");
                invalid.add("coverage.earliestDataDate");
            }
        }
        if ( !(invalid.contains("miscInfo.endDatestamp") || invalid.contains("coverage.latestDataDate")) ) {
            if ( miscInfo.getEndDatestamp().before(coverage.getLatestDataDate()) ) {
                invalid.add("miscInfo.endDatestamp");
                invalid.add("coverage.latestDataDate");
            }
        }

        // TODO: verify researcher and instrument names in variables match some entry in investigators and instruments

        return invalid;
    }

    /**
     * @return the submitter of this dataset; never null but may contain invalid values
     */
    public Submitter getSubmitter() {
        return submitter.duplicate(null);
    }

    /**
     * @param submitter
     *         assign as the submitter of this dataset; if null, a Submitter is created invalid values
     */
    public void setSubmitter(Submitter submitter) {
        this.submitter = (submitter != null) ? submitter.duplicate(null) : new Submitter();
    }

    /**
     * @return the list of investigators (PIs) involved with this dataset; never null but may be empty
     */
    public ArrayList<Investigator> getInvestigators() {
        ArrayList<Investigator> piList = new ArrayList<Investigator>(investigators.size());
        for (Investigator pi : investigators) {
            if ( pi.getClass() == Submitter.class )
                piList.add(((Submitter) pi).duplicate(null));
            else
                piList.add(pi.duplicate(null));
        }
        return piList;
    }

    /**
     * Calls {@link #setInvestigators(Iterable)}; added to satisfy JavaBean requirements.
     *
     * @param investigators
     *         assign as the list of investigators (PIs) involved with this dataset;
     *         if null, and empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if an investigator in the list is null
     */
    public void setInvestigators(ArrayList<Investigator> investigators) throws IllegalArgumentException {
        setInvestigators((Iterable<Investigator>) investigators);
    }

    /**
     * @param investigators
     *         assign as the list of investigators (PIs) involved with this dataset;
     *         if null, and empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if an investigator in the list is null
     */
    public void setInvestigators(Iterable<Investigator> investigators) throws IllegalArgumentException {
        this.investigators.clear();
        if ( investigators != null ) {
            for (Investigator pi : investigators) {
                if ( null == pi )
                    throw new IllegalArgumentException("null investigator given");
                if ( pi.getClass() == Submitter.class )
                    this.investigators.add(((Submitter) pi).duplicate(null));
                else
                    this.investigators.add(pi.duplicate(null));
            }
        }
    }

    /**
     * @return the platform for this dataset; never null but may contain invalid values
     */
    public Platform getPlatform() {
        return platform.duplicate(null);
    }

    /**
     * @param platform
     *         assign as the platform for this dataset; if null, a Platform with invalid values is assigned
     */
    public void setPlatform(Platform platform) {
        this.platform = (platform != null) ? platform.duplicate(null) : new Platform();
    }

    /**
     * @return the coverage of this dataset; never null but may contain invalid values
     */
    public Coverage getCoverage() {
        return coverage.duplicate(null);
    }

    /**
     * @param coverage
     *         assign as the coverage of this dataset; if null, a Coverage with invalid values is assigned
     */
    public void setCoverage(Coverage coverage) {
        this.coverage = (coverage != null) ? coverage.duplicate(null) : new Coverage();
    }

    /**
     * @return the list of instruments used in this dataset; never null but may be empty
     */
    public ArrayList<Instrument> getInstruments() {
        ArrayList<Instrument> instList = new ArrayList<Instrument>(instruments.size());
        for (Instrument inst : instruments) {
            // Deal with type erasure in a GWT-friendly way
            if ( inst.getClass() == Equilibrator.class )
                instList.add(((Equilibrator) inst).duplicate(null));
            else if ( inst.getClass() == Sampler.class )
                instList.add(((Sampler) inst).duplicate(null));
            else if ( inst.getClass() == GasSensor.class )
                instList.add(((GasSensor) inst).duplicate(null));
            else if ( inst.getClass() == PressureSensor.class )
                instList.add(((PressureSensor) inst).duplicate(null));
            else if ( inst.getClass() == SalinitySensor.class )
                instList.add(((SalinitySensor) inst).duplicate(null));
            else if ( inst.getClass() == TemperatureSensor.class )
                instList.add(((TemperatureSensor) inst).duplicate(null));
            else if ( inst.getClass() == Analyzer.class )
                instList.add(((Analyzer) inst).duplicate(null));
            else
                instList.add(inst.duplicate(null));
        }
        return instList;
    }

    /**
     * Calls {@link #setInstruments(Iterable)}(Iterable)}; added to satisfy JavaBean requirements.
     *
     * @param instruments
     *         assign as the list of instruments used in this dataset; if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if an instrument in the list is null
     */
    public void setInstruments(ArrayList<Instrument> instruments) throws IllegalArgumentException {
        setInstruments((Iterable<Instrument>) instruments);
    }

    /**
     * @param instruments
     *         assign as the list of instruments used in this dataset; if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if an instrument in the list is null
     */
    public void setInstruments(Iterable<Instrument> instruments) throws IllegalArgumentException {
        this.instruments.clear();
        if ( instruments != null ) {
            for (Instrument inst : instruments) {
                if ( null == inst )
                    throw new IllegalArgumentException("null instrument given");
                // Deal with type erasure in a GWT-friendly way
                if ( inst.getClass() == Equilibrator.class )
                    this.instruments.add(((Equilibrator) inst).duplicate(null));
                else if ( inst.getClass() == Sampler.class )
                    this.instruments.add(((Sampler) inst).duplicate(null));
                else if ( inst.getClass() == GasSensor.class )
                    this.instruments.add(((GasSensor) inst).duplicate(null));
                else if ( inst.getClass() == PressureSensor.class )
                    this.instruments.add(((PressureSensor) inst).duplicate(null));
                else if ( inst.getClass() == SalinitySensor.class )
                    this.instruments.add(((SalinitySensor) inst).duplicate(null));
                else if ( inst.getClass() == TemperatureSensor.class )
                    this.instruments.add(((TemperatureSensor) inst).duplicate(null));
                else if ( inst.getClass() == Analyzer.class )
                    this.instruments.add(((Analyzer) inst).duplicate(null));
                else
                    this.instruments.add(inst.duplicate(null));
            }
        }
    }

    /**
     * @return the list of variable in this dataset; never null but may be empty
     */
    public ArrayList<Variable> getVariables() {
        ArrayList<Variable> varList = new ArrayList<Variable>(variables.size());
        for (Variable var : variables) {
            // Deal with type erasure in a GWT-friendly way
            if ( var.getClass() == AquGasConc.class )
                varList.add(((AquGasConc) var).duplicate(null));
            else if ( var.getClass() == GasConc.class )
                varList.add(((GasConc) var).duplicate(null));
            else if ( var.getClass() == AirPressure.class )
                varList.add(((AirPressure) var).duplicate(null));
            else if ( var.getClass() == BioDataVar.class )
                varList.add(((BioDataVar) var).duplicate(null));
            else if ( var.getClass() == Temperature.class )
                varList.add(((Temperature) var).duplicate(null));
            else if ( var.getClass() == DataVar.class )
                varList.add(((DataVar) var).duplicate(null));
            else
                varList.add(var.duplicate(null));
        }
        return varList;
    }

    /**
     * Calls {@link #setVariables(Iterable)}; added to satisfy JavaBean requirements.
     *
     * @param variables
     *         assign as the list of variables in this dataset; if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         is a variable in the list is null
     */
    public void setVariables(ArrayList<Variable> variables) throws IllegalArgumentException {
        setVariables((Iterable<Variable>) variables);
    }

    /**
     * @param variables
     *         assign as the list of variables in this dataset; if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         is a variable in the list is null
     */
    public void setVariables(Iterable<Variable> variables) throws IllegalArgumentException {
        this.variables.clear();
        if ( variables != null ) {
            for (Variable var : variables) {
                if ( null == var )
                    throw new IllegalArgumentException("null variable given");
                // Deal with type erasure in a GWT-friendly way
                if ( var.getClass() == AquGasConc.class )
                    this.variables.add(((AquGasConc) var).duplicate(null));
                else if ( var.getClass() == GasConc.class )
                    this.variables.add(((GasConc) var).duplicate(null));
                else if ( var.getClass() == AirPressure.class )
                    this.variables.add(((AirPressure) var).duplicate(null));
                else if ( var.getClass() == BioDataVar.class )
                    this.variables.add(((BioDataVar) var).duplicate(null));
                else if ( var.getClass() == Temperature.class )
                    this.variables.add(((Temperature) var).duplicate(null));
                else if ( var.getClass() == DataVar.class )
                    this.variables.add(((DataVar) var).duplicate(null));
                else
                    this.variables.add(var.duplicate(null));
            }
        }
    }

    public MiscInfo getMiscInfo() {
        return miscInfo.duplicate(null);
    }

    public void setMiscInfo(MiscInfo miscInfo) {
        this.miscInfo = (miscInfo != null) ? miscInfo.duplicate(null) : new MiscInfo();
    }

    /**
     * Deeply copies the values in this SocatMetadata object to the given SocatMetadata object.
     *
     * @param dup
     *         the SocatMetadata object to copy values into;
     *         if null, a new SocatMetadata object is created for copying values into
     *
     * @return the updated SocatMetadata object
     */
    public SocatMetadata duplicate(SocatMetadata dup) {
        if ( dup == null )
            dup = new SocatMetadata();
        dup.setSubmitter(submitter);
        dup.setInvestigators(investigators);
        dup.setPlatform(platform);
        dup.setCoverage(coverage);
        dup.setInstruments(instruments);
        dup.setVariables(variables);
        dup.setMiscInfo(miscInfo);
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof SocatMetadata) )
            return false;

        SocatMetadata other = (SocatMetadata) obj;

        if ( !submitter.equals(other.submitter) )
            return false;
        if ( !investigators.equals(other.investigators) )
            return false;
        if ( !platform.equals(other.platform) )
            return false;
        if ( !coverage.equals(other.coverage) )
            return false;
        if ( !instruments.equals(other.instruments) )
            return false;
        if ( !variables.equals(other.variables) )
            return false;
        return miscInfo.equals(other.miscInfo);
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = submitter.hashCode();
        result = result * prime + investigators.hashCode();
        result = result * prime + platform.hashCode();
        result = result * prime + coverage.hashCode();
        result = result * prime + instruments.hashCode();
        result = result * prime + variables.hashCode();
        result = result * prime + miscInfo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SocatMetadata{" +
                "submitter=" + submitter +
                ", investigators=" + investigators +
                ", platform=" + platform +
                ", coverage=" + coverage +
                ", instruments=" + instruments +
                ", variables=" + variables +
                ", miscInfo=" + miscInfo +
                '}';
    }

}
