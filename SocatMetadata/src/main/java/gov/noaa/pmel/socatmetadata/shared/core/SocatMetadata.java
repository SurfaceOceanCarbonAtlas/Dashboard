package gov.noaa.pmel.socatmetadata.shared.core;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;
import gov.noaa.pmel.socatmetadata.shared.person.Investigator;
import gov.noaa.pmel.socatmetadata.shared.person.Submitter;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class SocatMetadata implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -7072506917048565345L;

    private Submitter submitter;
    private ArrayList<Investigator> investigators;
    private Platform platform;
    private Coverage coverage;
    private ArrayList<Instrument> instruments;
    private ArrayList<Variable> variables;
    private MiscInfo miscInfo;

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
        for (String name : miscInfo.invalidFieldNames()) {
            invalid.add("miscInfo." + name);
        }

        // TODO: verify researcher and instrument names in variables match some entry in investigators and instruments

        return invalid;
    }

    /**
     * @return the submitter of this dataset; never null but may contain invalid values
     */
    public Submitter getSubmitter() {
        return (Submitter) (submitter.duplicate(null));
    }

    /**
     * @param submitter
     *         assign as the submitter of this dataset; if null, a Submitter is created invalid values
     */
    public void setSubmitter(Submitter submitter) {
        this.submitter = (submitter != null) ? (Submitter) (submitter.duplicate(null)) : new Submitter();
    }

    /**
     * @return the list of investigators (PIs) involved with this dataset; never null but may be empty
     */
    public ArrayList<Investigator> getInvestigators() {
        ArrayList<Investigator> piList = new ArrayList<Investigator>(investigators.size());
        for (Investigator pi : investigators) {
            piList.add((Investigator) (pi.duplicate(null)));
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
                this.investigators.add((Investigator) (pi.duplicate(null)));
            }
        }
    }

    /**
     * @return the platform for this dataset; never null but may contain invalid values
     */
    public Platform getPlatform() {
        return (Platform) (platform.duplicate(null));
    }

    /**
     * @param platform
     *         assign as the platform for this dataset; if null, a Platform with invalid values is assigned
     */
    public void setPlatform(Platform platform) {
        this.platform = (platform != null) ? (Platform) (platform.duplicate(null)) : new Platform();
    }

    /**
     * @return the coverage of this dataset; never null but may contain invalid values
     */
    public Coverage getCoverage() {
        return (Coverage) (coverage.duplicate(null));
    }

    /**
     * @param coverage
     *         assign as the coverage of this dataset; if null, a Coverage with invalid values is assigned
     */
    public void setCoverage(Coverage coverage) {
        this.coverage = (coverage != null) ? (Coverage) (coverage.duplicate(null)) : new Coverage();
    }

    /**
     * @return the list of instruments used in this dataset; never null but may be empty
     */
    public ArrayList<Instrument> getInstruments() {
        ArrayList<Instrument> instList = new ArrayList<Instrument>(instruments.size());
        for (Instrument inst : instruments) {
            instList.add((Instrument) (inst.duplicate(null)));
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
                this.instruments.add((Instrument) (inst.duplicate(null)));
            }
        }
    }

    /**
     * @return the list of variable in this dataset; never null but may be empty
     */
    public ArrayList<Variable> getVariables() {
        ArrayList<Variable> varList = new ArrayList<Variable>(variables.size());
        for (Variable var : variables) {
            varList.add((Variable) (var.duplicate(null)));
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
                this.variables.add((Variable) (var.duplicate(null)));
            }
        }
    }

    /**
     * @return the miscellaneous information about a dataset; never null but may be empty
     */
    public MiscInfo getMiscInfo() {
        return (MiscInfo) (miscInfo.duplicate(null));
    }

    /**
     * @param miscInfo
     *         assign as the miscellaneous information about a dataset; if null, an empty MiscInfo is assigned
     */
    public void setMiscInfo(MiscInfo miscInfo) {
        this.miscInfo = (miscInfo != null) ? (MiscInfo) (miscInfo.duplicate(null)) : new MiscInfo();
    }

    @Override
    public Object duplicate(Object dup) {
        SocatMetadata metadata;
        if ( dup == null )
            metadata = new SocatMetadata();
        else
            metadata = (SocatMetadata) dup;
        metadata.setSubmitter(submitter);
        metadata.setInvestigators(investigators);
        metadata.setPlatform(platform);
        metadata.setCoverage(coverage);
        metadata.setInstruments(instruments);
        metadata.setVariables(variables);
        metadata.setMiscInfo(miscInfo);
        return metadata;
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
