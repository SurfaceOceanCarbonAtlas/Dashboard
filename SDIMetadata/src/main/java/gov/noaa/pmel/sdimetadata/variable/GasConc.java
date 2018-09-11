package gov.noaa.pmel.sdimetadata.variable;

/**
 * Information about measurements of a gas concentration.  Instances of this class are assumed
 * to be atmospheric gas concentration as aqueous gas concentrations should be the subclass AquGasConc.
 */
public class GasConc extends DataVar implements Cloneable {

    protected String dryingMethod;
    protected String waterVaporCorrection;

    public GasConc() {
        super();
        dryingMethod = "";
        waterVaporCorrection = "";
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
    public GasConc clone() {
        GasConc dup = (GasConc) super.clone();
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

