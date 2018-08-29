package gov.noaa.pmel.sdimetadata.instrument;

import java.util.ArrayList;

/**
 * Describes an equilibrator for measuring the amount of a dissolved gas in water.
 */
public class Equilibrator extends Sampler implements Cloneable {
    protected Double intakeDepthM;
    protected String intakeLocation;
    protected String equilibratorType;
    protected Double chamberWaterVolL;
    protected Double chamberHeadspaceVolL;
    protected Double minWaterFlowLPM;
    protected Double maxWaterFlowLPM;
    protected Double minGasFlowLPM;
    protected Double maxGasFlowLPM;
    protected String venting;
    protected String dryingMethod;
    protected ArrayList<CalibrationGas> calibrationGases;

    /*
     * <Depth_of_Sea_Water_Intake>5 meters</Depth_of_Sea_Water_Intake>
     * <Location_of_Sea_Water_Intake>Bow</Location_of_Sea_Water_Intake>
     * <Equilibrator_Type>Sprayhead above dynamic pool, with thermal jacket</Equilibrator_Type>
     * <Equilibrator_Volume>0.95 L (0.4 L water, 0.55 L headspace)</Equilibrator_Volume>
     * <Water_Flow_Rate>1.5 - 2.0 L/min</Water_Flow_Rate>
     * <Headspace_Gas_Flow_Rate>70 - 150 ml/min</Headspace_Gas_Flow_Rate>
     * <Vented>Yes</Vented>\
     * <Drying_Method_for_CO2_in_water>Gas stream passes through a thermoelectric condenser (~5 &#176;C) and then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).</Drying_Method_for_CO2_in_water>\
     * <Additional_Information>Primary equlibrator is vented through a secondary equilibrator</Additional_Information>
     */

}
