package gov.noaa.pmel.sdimetadata.variable;

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


}
