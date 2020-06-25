package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;

import java.io.Serializable;

/**
 * Information about measurements of a gas concentration.  Instances of this class are assumed
 * to be atmospheric gas concentration as aqueous gas concentrations should be the subclass AquGasConc.
 */
public class GasConc extends InstData implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -5300357921096259559L;

    private String dryingMethod;
    private String waterVaporCorrection;

    /**
     * Create with all field empty.
     */
    public GasConc() {
        super();
        dryingMethod = "";
        waterVaporCorrection = "";
    }

    /**
     * Create using as many of the values in the given variable subclass as possible.
     */
    public GasConc(Variable var) {
        super(var);
        if ( var instanceof GasConc ) {
            GasConc conc = (GasConc) var;
            dryingMethod = conc.dryingMethod;
            waterVaporCorrection = conc.waterVaporCorrection;
        }
        else {
            dryingMethod = "";
            waterVaporCorrection = "";
        }
    }

    /**
     * @return description of drying method used; never null but may be empty
     */
    public String getDryingMethod() {
        return dryingMethod;
    }

    /**
     * @param dryingMethod
     *         assign as description of drying method used; if null, an empty string is assigned
     */
    public void setDryingMethod(String dryingMethod) {
        this.dryingMethod = (dryingMethod != null) ? dryingMethod.trim() : "";
    }

    /**
     * @return water vapor corrections made; never null but may be empty
     */
    public String getWaterVaporCorrection() {
        return waterVaporCorrection;
    }

    /**
     * @param waterVaporCorrection
     *         assign as water vapor corrections made; if null, an empty string is assigned
     */
    public void setWaterVaporCorrection(String waterVaporCorrection) {
        this.waterVaporCorrection = (waterVaporCorrection != null) ? waterVaporCorrection.trim() : "";
    }

    @Override
    public Object duplicate(Object dup) {
        GasConc conc;
        if ( dup == null )
            conc = new GasConc();
        else
            conc = (GasConc) dup;
        super.duplicate(conc);
        conc.dryingMethod = dryingMethod;
        conc.waterVaporCorrection = waterVaporCorrection;
        return conc;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + dryingMethod.hashCode();
        result = result * prime + waterVaporCorrection.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( !(obj instanceof GasConc) )
            return false;
        if ( !super.equals(obj) )
            return false;

        GasConc other = (GasConc) obj;

        if ( !dryingMethod.equals(other.dryingMethod) )
            return false;
        if ( !waterVaporCorrection.equals(other.waterVaporCorrection) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
        return repr.substring(0, repr.length() - 2) +
                ", dryingMethod='" + dryingMethod + "'" +
                ", waterVaporCorrection='" + waterVaporCorrection + "'" +
                " }";
    }

    @Override
    public String getSimpleName() {
        return "GasConc";
    }

}
