package gov.noaa.pmel.sdimetadata.variable;

import java.util.HashSet;

/**
 * Information about measurements of a gas concentration in a body of water.
 */
public class AquGasConc extends GasConc implements Cloneable {

    protected String reportTemperature;
    protected String temperatureCorrection;

    /**
     * Create with all fields empty
     */
    public AquGasConc() {
        super();
        reportTemperature = "";
        temperatureCorrection = "";
    }

    /**
     * Create using values in the given variable. If a DataVar is given, all DataVar fields are copied.
     * If a GasConc is given, all GasConc fields are copied. If an AquGasConc is given, all AquGasConc fields are
     * copied.
     */
    public AquGasConc(Variable var) {
        super(var);
        if ( (var != null) && (var instanceof AquGasConc) ) {
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
    public AquGasConc clone() {
        AquGasConc dup = (AquGasConc) super.clone();
        dup.reportTemperature = reportTemperature;
        dup.temperatureCorrection = temperatureCorrection;
        return dup;
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
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + reportTemperature.hashCode();
        result = result * prime + temperatureCorrection.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = "Aqu" + super.toString();
        return repr.substring(0, repr.length() - 1) +
                ", reportTemperature='" + reportTemperature + '\'' +
                ", temperatureCorrection='" + temperatureCorrection + '\'' +
                '}';
    }

}

