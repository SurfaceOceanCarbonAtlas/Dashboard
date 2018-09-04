package gov.noaa.pmel.sdimetadata.variable;

import java.util.HashSet;

/**
 * Information about measurements of a gas concentration in a body of water.
 */
public class AqueousGasConc extends DataVar implements Cloneable {

    protected String reportTemperature;
    protected String temperatureCorrection;
    protected String pressureCorrection;
    protected String waterVaporCorrection;

    /**
     * Create with all fields empty
     */
    public AqueousGasConc() {
        super();
        reportTemperature = "";
        temperatureCorrection = "";
        pressureCorrection = "";
        waterVaporCorrection = "";
    }

    @Override
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalids = super.invalidFieldNames();
        if ( reportTemperature.isEmpty() )
            invalids.add("reportTemperature");
        return invalids;
    }

    /**
     * @return the water temperature variable (SST, Tequ) at which the gas concentration is reported;
     *         never null but may be empty
     */
    public String getReportTemperature() {
        return reportTemperature;
    }

    /**
     * @param reportTemperature
     *         assign as the water temperature variable (SST, Tequ) at which the gas concentration is reported;
     *         if null or blank, an empty string is assigned
     */
    public void setReportTemperature(String reportTemperature) {
        this.reportTemperature = (reportTemperature != null) ? reportTemperature.trim() : "";
    }

    /**
     * @return temperature corrections applied; never null but may be empty
     */
    public String getTemperatureCorrection() {
        return temperatureCorrection;
    }

    /**
     * @param temperatureCorrection
     *         assign as temperature corrections applied; if null or blank, an empty string is assigned
     */
    public void setTemperatureCorrection(String temperatureCorrection) {
        this.temperatureCorrection = (temperatureCorrection != null) ? temperatureCorrection.trim() : "";
    }

    /**
     * @return pressure corrections made; never null but may be empty
     */
    public String getPressureCorrection() {
        return pressureCorrection;
    }

    /**
     * @param pressureCorrection
     *         assign as pressures corrections made; if null or blank, an empty string is assigned
     */
    public void setPressureCorrection(String pressureCorrection) {
        this.pressureCorrection = (pressureCorrection != null) ? pressureCorrection.trim() : "";
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
    public AqueousGasConc clone() {
        AqueousGasConc dup = (AqueousGasConc) super.clone();
        dup.reportTemperature = reportTemperature;
        dup.temperatureCorrection = temperatureCorrection;
        dup.pressureCorrection = pressureCorrection;
        dup.waterVaporCorrection = waterVaporCorrection;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof AqueousGasConc) )
            return false;
        if ( !super.equals(obj) )
            return false;

        AqueousGasConc other = (AqueousGasConc) obj;

        if ( !reportTemperature.equals(other.reportTemperature) )
            return false;
        if ( !temperatureCorrection.equals(other.temperatureCorrection) )
            return false;
        if ( !pressureCorrection.equals(other.pressureCorrection) )
            return false;
        if ( !waterVaporCorrection.equals(other.waterVaporCorrection) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = result * prime + reportTemperature.hashCode();
        result = result * prime + temperatureCorrection.hashCode();
        result = result * prime + pressureCorrection.hashCode();
        result = result * prime + waterVaporCorrection.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String repr = super.toString().replaceFirst("DataVar", "AqueousGasConc");
        return repr.substring(0, repr.length() - 1) +
                ", reportTemperature='" + reportTemperature + '\'' +
                ", temperatureCorrection='" + temperatureCorrection + '\'' +
                ", pressureCorrection='" + pressureCorrection + '\'' +
                ", waterVaporCorrection='" + waterVaporCorrection + '\'' +
                '}';
    }

}

