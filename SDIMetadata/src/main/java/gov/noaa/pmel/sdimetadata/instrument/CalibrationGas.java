package gov.noaa.pmel.sdimetadata.instrument;

/**
 * Described a standard gas mixture used for calibration of instruments.
 */
public class CalibrationGas implements Cloneable {

    protected String name;
    protected String type;
    protected String supplier;
    protected Double concUMolPerMol;
    protected Double accuracyUMolPerMol;

}
