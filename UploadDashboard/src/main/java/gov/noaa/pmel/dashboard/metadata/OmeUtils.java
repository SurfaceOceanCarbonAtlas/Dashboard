package gov.noaa.pmel.dashboard.metadata;

import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.translate.CdiacReader;
import gov.noaa.pmel.sdimetadata.translate.OcadsWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class OmeUtils {

    private static final HashMap<String,CdiacReader.VarType> DASH_TYPE_TO_CDIAC_TYPE;

    static {
        DASH_TYPE_TO_CDIAC_TYPE = new HashMap<String,CdiacReader.VarType>();
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_WATER_TEQU_WET.getVarName(), CdiacReader.VarType.FCO2_WATER_EQU);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_WATER_SST_WET.getVarName(), CdiacReader.VarType.FCO2_WATER_SST);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_WATER_TEQU_WET.getVarName(), CdiacReader.VarType.PCO2_WATER_EQU);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_WATER_SST_WET.getVarName(), CdiacReader.VarType.PCO2_WATER_SST);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_WATER_TEQU_DRY.getVarName(), CdiacReader.VarType.XCO2_WATER_EQU);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_WATER_SST_DRY.getVarName(), CdiacReader.VarType.XCO2_WATER_SST);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_ATM_DRY_ACTUAL.getVarName(), CdiacReader.VarType.FCO2_ATM_ACTUAL);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.FCO2_ATM_DRY_INTERP.getVarName(), CdiacReader.VarType.FCO2_ATM_INTERP);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_ATM_DRY_ACTUAL.getVarName(), CdiacReader.VarType.PCO2_ATM_ACTUAL);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PCO2_ATM_DRY_INTERP.getVarName(), CdiacReader.VarType.PCO2_ATM_INTERP);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_ATM_DRY_ACTUAL.getVarName(), CdiacReader.VarType.XCO2_ATM_ACTUAL);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.XCO2_ATM_DRY_INTERP.getVarName(), CdiacReader.VarType.XCO2_ATM_INTERP);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.SST.getVarName(), CdiacReader.VarType.SEA_SURFACE_TEMPERATURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.TEQU.getVarName(), CdiacReader.VarType.EQUILIBRATOR_TEMPERATURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PATM.getVarName(), CdiacReader.VarType.SEA_LEVEL_PRESSURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.PEQU.getVarName(), CdiacReader.VarType.EQUILIBRATOR_PRESSURE);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.SALINITY.getVarName(), CdiacReader.VarType.SALINITY);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.WOCE_CO2_WATER.getVarName(), CdiacReader.VarType.WOCE_CO2_WATER);
        DASH_TYPE_TO_CDIAC_TYPE.put(SocatTypes.WOCE_CO2_ATM.getVarName(), CdiacReader.VarType.WOCE_CO2_ATM);
    }

    /**
     * Using the given data column names and types for a datasets, creates an SDIMetadata object
     * from CDIAC OME metadata XML.
     *
     * @param xmlReader
     *         read CDIAC OME metadata XML from here
     * @param dataColNames
     *         data column names for this dataset
     * @param dataColTypes
     *         data column types for this dataset
     *
     * @return SDIMetadata object created from the CDIAC OME metadata file contents
     *
     * @throws IOException
     *         if an error occurs when reading the CDIAC OME metadata file
     * @throws IllegalArgumentException
     *         if the contents of the CDIAC OME metadata file are invalid
     */
    public static SDIMetadata createSdiMetadataFromCdiacOme(Reader xmlReader,
            ArrayList<String> dataColNames, ArrayList<DataColumnType> dataColTypes)
            throws IOException, IllegalArgumentException {
        // Read the CDIAC XML into an XML Document in memory
        CdiacReader reader;
        try {
            reader = new CdiacReader(xmlReader);
        } finally {
            xmlReader.close();
        }
        // Make sure all data column names used are mapped to the correct CdiacReader.VarType
        for (int k = 0; k < dataColNames.size(); k++) {
            CdiacReader.VarType vtype = DASH_TYPE_TO_CDIAC_TYPE.get(dataColTypes.get(k).getVarName());
            if ( vtype != null )
                reader.associateColumnNameWithVarType(dataColNames.get(k), vtype);
        }
        // Create an SDIMetadata object from the contents of the CDIAC XML Document
        return reader.createSDIMetadata();
    }

    /**
     * Creates an OCADS OME metdata file from the contents of the given SDIMetadata object.
     *
     * @param omeFile
     *         OCADS OME metadata file to create
     * @param sdiMData
     *         metadata to write
     *
     * @throws IOException
     *         if there is a problem writing the file
     */
    public static void createOcadsOmeFromSdiMetadata(File omeFile, SDIMetadata sdiMData) throws IOException {
        OcadsWriter ocadsWriter = new OcadsWriter(new FileWriter(omeFile));
        try {
            ocadsWriter.writeOcadsXml(sdiMData);
        } finally {
            ocadsWriter.close();
        }
    }

    private static final HashSet<String> ALLOWED_UNITS = new HashSet<String>(Arrays.asList(
            "microatmospheres",
            "microatmosphere",
            "uatm",
            "µatm",
            "umol/mol",
            "µmol/mol",
            "ppm"
    ));

    /**
     * Using the contents of the give SDIMetadata, recommend a QC flag/status for this dataset.
     *
     * @param sdiMData
     *         metadata to examine
     *
     * @return the automation-suggested dataset QC flag, an appropriate acceptable status
     *         ({@link DatasetQCStatus.Status#isAcceptable(DatasetQCStatus.Status)} returns true).
     *
     * @throws IllegalArgumentException
     *         if there are problems with the given Metadata, or
     *         if the metadata indicates the dataset in unacceptable
     */
    public static DatasetQCStatus.Status suggestDatasetQCFlag(SDIMetadata sdiMData) throws IllegalArgumentException {
        throw new IllegalArgumentException("Not yet implemented");
        /*
        DatasetQCStatus.Status autoSuggest;
        HashMap<String,GasSensor> co2SensorsMap = new HashMap<String,GasSensor>();
        for (Instrument instrument : sdiMData.getInstruments()) {
            if ( instrument instanceof GasSensor ) {
                GasSensor sensor = (GasSensor) instrument;
                for (CalibrationGas gas : sensor.getCalibrationGases()) {
                    if ( "CO2".equals(gas.getType()) ) {
                        if ( co2SensorsMap.put(sensor.getName(), sensor) != null )
                            throw new IllegalArgumentException("Unexpected duplicate name '" +
                                    sensor.getName() + "' for a CO2 sensor");
                        break;
                    }
                }
            }
        }
        ArrayList<AquGasConc> co2vars = new ArrayList<AquGasConc>();
        ArrayList<GasSensor> co2sensors = new ArrayList<GasSensor>();
        for (Variable variable : sdiMData.getVariables()) {
            if ( variable instanceof AquGasConc ) {
                AquGasConc gasConc = (AquGasConc) variable;
                GasSensor gasSensor = null;
                for (String instName : gasConc.getInstrumentNames()) {
                    GasSensor sensor = co2SensorsMap.get(instName);
                    if ( sensor != null ) {
                        if ( gasSensor != null )
                            throw new IllegalArgumentException("More than one CO2 sensor (" + instName +
                                    " and " + gasSensor.getName() + ") for aqueous CO2 measurement variable " +
                                    gasConc.getFullName());
                        gasSensor = sensor;
                    }
                }
                if ( gasSensor != null ) {
                    co2vars.add(gasConc);
                    co2sensors.add(gasSensor);
                }
            }
        }
        if ( co2vars.size() < 1 )
            throw new IllegalArgumentException("No aqueous CO2 measurement variable found");
        double minAccuracy = 100.0;
        for (AquGasConc gasConc : co2vars) {
            NumericString accuracy = gasConc.getAccuracy();
            if ( !ALLOWED_UNITS.contains(accuracy.getUnitString()) )
                throw new IllegalArgumentException("Unexpected units of '" + accuracy.getUnitString() +
                        "' for the accuracy of an aqueous CO2 measurement variable ");
            if ( minAccuracy > accuracy.getNumericValue() )
                minAccuracy = accuracy.getNumericValue();
        }
        // Start guess using the accuracy of CO2 measurements
        if ( minAccuracy <= 2.0 )
            autoSuggest = DatasetQCStatus.Status.ACCEPTED_B;
        else if ( minAccuracy <= 5.0 )
            autoSuggest = DatasetQCStatus.Status.ACCEPTED_D;
        else if ( minAccuracy <= 10.0 )
            autoSuggest = DatasetQCStatus.Status.ACCEPTED_E;
        else
            throw new IllegalArgumentException("Unacceptably large accuracy for the aqueous CO2 measurements");

        // TODO: Check the number of non-zero standard gasses
        // TODO: Check the frequency of calibration
        // TODO: Check the accuracy of water temperature measurements
        // TODO: Check the accuracy of air pressure measurements
        // TODO: other checks?

        return autoSuggest;
        */
    }

}
