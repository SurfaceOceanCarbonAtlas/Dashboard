package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.noaa.pmel.socatmetadata.shared.core.Duplicable;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Information about measurements of a gas concentration in a body of water.
 */
public class AquGasConc extends GasConc implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = -6347862202375795785L;

    private String reportTemperature;
    private String temperatureCorrection;

    /**
     * Create with all fields empty
     */
    public AquGasConc() {
        super();
        reportTemperature = "";
        temperatureCorrection = "";
    }

    /**
     * Create using as many of the values in the given variable subclass as possible.
     */
    public AquGasConc(Variable var) {
        super(var);
        if ( var instanceof AquGasConc ) {
            AquGasConc conc = (AquGasConc) var;
            reportTemperature = conc.reportTemperature;
            temperatureCorrection = conc.temperatureCorrection;
        }
        else {
            reportTemperature = "";
            temperatureCorrection = "";
        }
    }

    @Override
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalids = super.invalidFieldNames();
        if ( reportTemperature.isEmpty() )
            invalids.add("reportTemperature");
        return invalids;
    }

    /**
     * @return the water temperature variable (20 deg C, SST, Tequ) at which the gas concentration is reported;
     *         never null but may be empty
     */
    public String getReportTemperature() {
        return reportTemperature;
    }

    /**
     * @param reportTemperature
     *         assign as the water temperature variable (20 deg C, SST, Tequ) at which the gas concentration
     *         is reported;  if null or blank, an empty string is assigned
     */
    public void setReportTemperature(String reportTemperature) {
        this.reportTemperature = (reportTemperature != null) ? reportTemperature.trim() : "";
    }

    /**
     * @return temperature effect corrections applied; never null but may be empty
     */
    public String getTemperatureCorrection() {
        return temperatureCorrection;
    }

    /**
     * @param temperatureCorrection
     *         assign as temperature effect corrections applied; if null or blank, an empty string is assigned
     */
    public void setTemperatureCorrection(String temperatureCorrection) {
        this.temperatureCorrection = (temperatureCorrection != null) ? temperatureCorrection.trim() : "";
    }

    @Override
    public Object duplicate(Object dup) {
        AquGasConc conc;
        if ( dup == null )
            conc = new AquGasConc();
        else
            conc = (AquGasConc) dup;
        super.duplicate(conc);
        conc.reportTemperature = reportTemperature;
        conc.temperatureCorrection = temperatureCorrection;
        return conc;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + reportTemperature.hashCode();
        result = result * prime + temperatureCorrection.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof AquGasConc) )
            return false;
        if ( !super.equals(obj) )
            return false;

        AquGasConc other = (AquGasConc) obj;

        if ( !reportTemperature.equals(other.reportTemperature) )
            return false;
        if ( !temperatureCorrection.equals(other.temperatureCorrection) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst(super.getSimpleName(), getSimpleName());
        return repr.substring(0, repr.length() - 2) +
                ", reportTemperature='" + reportTemperature + "'" +
                ", temperatureCorrection='" + temperatureCorrection + "'" +
                " }";
    }

    @Override
    public String getSimpleName() {
        return "AquGasConc";
    }

}
