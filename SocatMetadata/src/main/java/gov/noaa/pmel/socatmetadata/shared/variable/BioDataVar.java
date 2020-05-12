package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;

import java.io.Serializable;
import java.util.HashSet;

public class BioDataVar extends DataVar implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -5768266523048459705L;

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
        HashSet<String> invalids = super.invalidFieldNames();
        if ( speciesId.isEmpty() )
            invalids.add("speciesId");
        // Do not worry about accuracy (of the count) at this time
        invalids.remove("accuracy");
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
    public Object duplicate(Object dup) {
        BioDataVar var;
        if ( dup == null )
            var = new BioDataVar();
        else
            var = (BioDataVar) dup;
        super.duplicate(var);
        var.biologicalSubject = biologicalSubject;
        var.speciesId = speciesId;
        var.lifeStage = lifeStage;
        return var;
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
        String repr = super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
        return repr.substring(0, repr.length() - 2) +
                ", biologicalSubject='" + biologicalSubject + "'" +
                ", speciesId='" + speciesId + "'" +
                ", lifeStage='" + lifeStage + "'" +
                " }";
    }

    @Override
    public String getSimpleName() {
        return "BioDataVar";
    }

}
