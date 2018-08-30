package gov.noaa.pmel.sdimetadata.variable;

public class AqueousGasConc extends Variable implements Cloneable {
    protected String reportTemperature;
    protected String temperatureCorrection;
    protected String pressureCorrection;
    protected String waterVaporCorrection;

    /**
     * Create with all field empty
     */
    public AqueousGasConc() {
        super();
        reportTemperature = "";
        temperatureCorrection = "";
        pressureCorrection = "";
        waterVaporCorrection = "";
    }


}
