package gov.noaa.pmel.sdimetadata.variable;

import java.util.HashSet;

public class BioDataVar extends DataVar implements Cloneable {

    String biologicalSubject;
    String speciesId;
    String lifeStage;

    /**
     * Create with all fields empty
     */
    public BioDataVar() {
        super();
        biologicalSubject = "";
        speciesId = "";
        lifeStage = "";
    }

    /**
     * Create with as many fields as possible assigned from the given variable.
     */
    public BioDataVar(Variable var) {
        super(var);
        if ( var instanceof BioDataVar ) {
            BioDataVar biovar = (BioDataVar) var;
            biologicalSubject = biovar.biologicalSubject;
            speciesId = biovar.speciesId;
            lifeStage = biovar.lifeStage;
        }
        else {
            biologicalSubject = "";
            speciesId = "";
            lifeStage = "";
        }
    }

    @Override
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalids = new HashSet<String>();
        if ( colName.isEmpty() )
            invalids.add("colName");
        if ( fullName.isEmpty() )
            invalids.add("fullName");
        if ( observeType.isEmpty() )
            invalids.add("observeType");
        switch ( measureMethod ) {
            case UNSPECIFIED:
                invalids.add("measureMethod");
                break;
            case COMPUTED:
                if ( methodDescription.isEmpty() )
                    invalids.add("methodDescription");
                break;
            default:
                if ( instrumentNames.isEmpty() )
                    invalids.add("instrumentNames");
        }
        if ( speciesId.isEmpty() )
            invalids.add("speciesId");
        return invalids;
    }

    /**
     * @return the biological subject; never null but may be empty
     */
    public String getBiologicalSubject() {
        return biologicalSubject;
    }

    /**
     * @param biologicalSubject
     *         assign as the biological subject; if null, an empty string is assigned
     */
    public void setBiologicalSubject(String biologicalSubject) {
        this.biologicalSubject = (biologicalSubject != null) ? biologicalSubject.trim() : "";
    }

    /**
     * @return the species ID; never null but may be empty
     */
    public String getSpeciesId() {
        return speciesId;
    }

    /**
     * @param speciesId
     *         assign as the species ID; if null, an empty string is assigned
     */
    public void setSpeciesId(String speciesId) {
        this.speciesId = (speciesId != null) ? speciesId.trim() : "";
    }

    /**
     * @return the life strage; never null but may be empty
     */
    public String getLifeStage() {
        return lifeStage;
    }

    /**
     * @param lifeStage
     *         assign as the life stage; if null, an empty string is assigned
     */
    public void setLifeStage(String lifeStage) {
        this.lifeStage = (lifeStage != null) ? lifeStage.trim() : "";
    }

    @Override
    public BioDataVar clone() {
        BioDataVar dup = (BioDataVar) super.clone();
        dup.biologicalSubject = biologicalSubject;
        dup.speciesId = speciesId;
        dup.lifeStage = lifeStage;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof BioDataVar) )
            return false;
        if ( !super.equals(obj) )
            return false;

        BioDataVar other = (BioDataVar) obj;

        if ( !biologicalSubject.equals(other.biologicalSubject) )
            return false;
        if ( !speciesId.equals(other.speciesId) )
            return false;
        if ( !lifeStage.equals(other.lifeStage) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + biologicalSubject.hashCode();
        result = result * prime + speciesId.hashCode();
        result = result * prime + lifeStage.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = "Bio" + super.toString();
        return repr.substring(0, repr.length() - 1) +
                ", biologicalSubject='" + biologicalSubject + '\'' +
                ", speciesId='" + speciesId + '\'' +
                ", lifeStage='" + lifeStage + '\'' +
                '}';
    }

}

