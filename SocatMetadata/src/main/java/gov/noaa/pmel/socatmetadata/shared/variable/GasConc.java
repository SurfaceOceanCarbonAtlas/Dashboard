package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Information about measurements of a gas concentration.  Instances of this class are assumed
 * to be atmospheric gas concentration as aqueous gas concentrations should be the subclass AquGasConc.
 */
public class GasConc extends DataVar implements Serializable, IsSerializable {

    private static final long serialVersionUID = 7215341143268280879L;

    protected String dryingMethod;
    protected String waterVaporCorrection;

    /**
     * Create with all field empty.
     */
    public GasConc() {
        super();
        dryingMethod = "";
        waterVaporCorrection = "";
    }

    /**
     * Create using values in the given variable. If a DataVar is given, all DataVar fields are copied.
     * If a GasConc is given, all GasConc fields are copied.
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

    /**
     * Deeply copies the values in this GasConc object to the given GasConc object.
     *
     * @param dup
     *         the GasConc object to copy values into;
     *         if null, a new GasConc object is created for copying values into
     *
     * @return the updated GasConc object
     */
    public GasConc duplicate(GasConc dup) {
        if ( dup == null )
            dup = new GasConc();
        super.duplicate(dup);
        dup.dryingMethod = dryingMethod;
        dup.waterVaporCorrection = waterVaporCorrection;
        return dup;
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
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + dryingMethod.hashCode();
        result = result * prime + waterVaporCorrection.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst("DataVar", "GasConc");
        return repr.substring(0, repr.length() - 1) +
                ", dryingMethod='" + dryingMethod + '\'' +
                ", waterVaporCorrection='" + waterVaporCorrection + '\'' +
                '}';
    }

}
