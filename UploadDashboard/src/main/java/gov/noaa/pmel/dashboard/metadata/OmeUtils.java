package gov.noaa.pmel.dashboard.metadata;

import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.handlers.SpellingHandler;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.instrument.CalibrationGas;
import gov.noaa.pmel.sdimetadata.instrument.GasSensor;
import gov.noaa.pmel.sdimetadata.instrument.Instrument;
import gov.noaa.pmel.sdimetadata.translate.CdiacReader;
import gov.noaa.pmel.sdimetadata.translate.OcadsWriter;
import gov.noaa.pmel.sdimetadata.util.NumericString;
import gov.noaa.pmel.sdimetadata.variable.AirPressure;
import gov.noaa.pmel.sdimetadata.variable.AquGasConc;
import gov.noaa.pmel.sdimetadata.variable.Temperature;
import gov.noaa.pmel.sdimetadata.variable.Variable;

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
            "uatmospheres",
            SpellingHandler.mu + "atmospheres",
            "uatm",
            SpellingHandler.mu + "atm",
            "micromole_per_mole",
            "umol_per_mol",
            SpellingHandler.mu + "mol_per_mol",
            "umol/mol",
            SpellingHandler.mu + "mol/mol",
            "ppm"
    ));

    private static final HashSet<DataColumnType> OBVIOUS_DATA_COLUMN_TYPES = new HashSet<DataColumnType>(Arrays.asList(
            DashboardServerUtils.DATASET_ID.duplicate(),
            DashboardServerUtils.DATASET_NAME.duplicate(),
            DashboardServerUtils.INVESTIGATOR_NAMES.duplicate(),
            DashboardServerUtils.ORGANIZATION_NAME.duplicate(),
            DashboardServerUtils.PLATFORM_NAME.duplicate(),
            DashboardServerUtils.PLATFORM_TYPE.duplicate(),
            DashboardServerUtils.SOURCE_DOI.duplicate(),
            DashboardServerUtils.LONGITUDE.duplicate(),
            DashboardServerUtils.LATITUDE.duplicate(),
            DashboardServerUtils.SAMPLE_DEPTH.duplicate(),
            DashboardServerUtils.TIMESTAMP.duplicate(),
            DashboardServerUtils.DATE.duplicate(),
            DashboardServerUtils.YEAR.duplicate(),
            DashboardServerUtils.MONTH_OF_YEAR.duplicate(),
            DashboardServerUtils.DAY_OF_MONTH.duplicate(),
            DashboardServerUtils.TIME_OF_DAY.duplicate(),
            DashboardServerUtils.HOUR_OF_DAY.duplicate(),
            DashboardServerUtils.MINUTE_OF_HOUR.duplicate(),
            DashboardServerUtils.SECOND_OF_MINUTE.duplicate(),
            DashboardServerUtils.DAY_OF_YEAR.duplicate(),
            DashboardServerUtils.SECOND_OF_DAY.duplicate()
    ));

    /**
     * Using the contents of the given SDIMetadata, recommend a QC flag/status for this dataset.
     * This does NOT check actual data values that affect the dataset QC flag (e.g., the range
     * of fCO2 values to compare against calibration gas concentrations, or amount of warming
     * between in-situ temperatures and equilibrator temperatures).
     *
     * @param sdiMData
     *         metadata to examine
     * @param dataset
     *         dataset associated with this metadata
     *
     * @return the automation-suggested dataset QC flag, an appropriate acceptable status
     *         ({@link DatasetQCStatus.Status#isAcceptable(DatasetQCStatus.Status)} returns true).
     *
     * @throws IllegalArgumentException
     *         if there are problems with the given Metadata, or
     *         if the metadata indicates the dataset in unacceptable
     */
    public static DatasetQCStatus.Status suggestDatasetQCFlag(SDIMetadata sdiMData,
            DashboardDataset dataset) throws IllegalArgumentException {
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
        HashSet<String> varColNames = new HashSet<String>();
        ArrayList<AirPressure> pressvars = new ArrayList<AirPressure>();
        ArrayList<Temperature> tempvars = new ArrayList<Temperature>();
        ArrayList<AquGasConc> co2vars = new ArrayList<AquGasConc>();
        ArrayList<GasSensor> co2sensors = new ArrayList<GasSensor>();
        for (Variable variable : sdiMData.getVariables()) {
            if ( !varColNames.add(variable.getColName()) )
                throw new IllegalArgumentException(
                        "A column name is used to describe multiple columns the OME metadata");
            if ( variable instanceof AirPressure ) {
                pressvars.add((AirPressure) variable);
            }
            else if ( variable instanceof Temperature ) {
                tempvars.add((Temperature) variable);
            }
            else if ( variable instanceof AquGasConc ) {
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

        // Start guess using the accuracy of CO2 measurements
        double co2Accuracy = 999.0;
        for (AquGasConc gasConc : co2vars) {
            NumericString accuracy = gasConc.getAccuracy();
            if ( !ALLOWED_UNITS.contains(accuracy.getUnitString()) )
                throw new IllegalArgumentException("Unexpected units of '" + accuracy.getUnitString() +
                        "' for the accuracy of an aqueous CO2 measurement variable ");
            if ( co2Accuracy > accuracy.getNumericValue() )
                co2Accuracy = accuracy.getNumericValue();
        }
        DatasetQCStatus.Status autoSuggest;
        if ( co2Accuracy <= 2.0 )
            autoSuggest = DatasetQCStatus.Status.ACCEPTED_B;
        else if ( co2Accuracy <= 5.0 )
            autoSuggest = DatasetQCStatus.Status.ACCEPTED_C;
        else
            throw new IllegalArgumentException("Unacceptably large accuracy for the aqueous CO2 measurements");
        // Alternative sensors with CO2 accuracy within 10.0 could be ACCEPTED_E.  However, they need a clear
        // and detailed description of the calibration, so an automation-suggested flag is not possible.

        // Rough judgement on whether metadata is complete
        boolean acceptable = true;
        HashSet<String> invalids = sdiMData.invalidFieldNames();
        if ( invalids.size() > 0 ) {
            acceptable = false;
        }
        else {
            ArrayList<DataColumnType> colTypes = dataset.getDataColTypes();
            ArrayList<String> colNames = dataset.getUserColNames();
            for (int k = 0; k < colNames.size(); k++) {
                // Some columns containing (repeated) metadata may have been added
                // only for the dashboard - instead of adding it in the prologue.
                // At this time, do not worry about these not being described as well as
                // the "obvious" names/types, although ideally all columns should be described.
                if ( OBVIOUS_DATA_COLUMN_TYPES.contains(colTypes.get(k)) )
                    continue;
                String name = colNames.get(k);
                if ( !varColNames.contains(name) ) {
                    acceptable = false;
                    break;
                }
            }
        }
        if ( !acceptable )
            autoSuggest = DatasetQCStatus.Status.ACCEPTED_D;

        // Alternative sensors with CO2 accuracy within 5.0 are not subject to the temperature
        // and pressure restrictions.  However, they need a clear and detailed description of
        // the calibration, so an automation-suggested flag is not possible.  So failing to give
        // a flag is an acceptable action.

        // Accuracy of temperatures within 0.2 ºC (0.05 ºC for ACCEPTED_B)
        double tempAccuracy = 999.0;
        for (Temperature temperature : tempvars) {
            NumericString accuracy = temperature.getAccuracy();
            // Temperature variables must units of ºC
            if ( tempAccuracy > accuracy.getNumericValue() )
                tempAccuracy = accuracy.getNumericValue();
        }
        if ( tempAccuracy > 0.2 )
            throw new IllegalArgumentException("Unacceptably large accuracy in temperature measurements");
        if ( autoSuggest.equals(DatasetQCStatus.Status.ACCEPTED_B) && (tempAccuracy > 0.05) )
            autoSuggest = DatasetQCStatus.Status.ACCEPTED_C;

        // Accuracy of air pressure within 5.0 hPa (2.0 hPa for ACCEPTED_B)
        // TODO: If equilibrator pressure is the sum of an external pressure sensor and
        //  a differential pressure sensor, the accuracy should be combined (root sum of squares).
        double pressAccuracy = 999.0;
        for (AirPressure pressure : pressvars) {
            NumericString accuracy = pressure.getAccuracy();
            // AirPressures variables must units of hPa
            if ( pressAccuracy > accuracy.getNumericValue() )
                pressAccuracy = accuracy.getNumericValue();
        }
        if ( pressAccuracy > 5.0 )
            throw new IllegalArgumentException("Unacceptably large accuracy in pressure measurements");
        if ( autoSuggest.equals(DatasetQCStatus.Status.ACCEPTED_B) && (pressAccuracy > 2.0) )
            autoSuggest = DatasetQCStatus.Status.ACCEPTED_C;

        int numNonZeroCalibGases = -1;
        int numCalibGases = -1;
        for (GasSensor sensor : co2sensors) {
            ArrayList<CalibrationGas> calibGasList = sensor.getCalibrationGases();
            int numNonZeroGases = 0;
            for (CalibrationGas gas : calibGasList) {
                try {
                    if ( gas.isNonZero() )
                        numNonZeroGases++;
                } catch ( Exception ex ) {
                    // No concentration or no accuracy - ignore this gas
                }
            }
            if ( numNonZeroCalibGases < numNonZeroGases ) {
                numNonZeroCalibGases = numNonZeroGases;
                numCalibGases = calibGasList.size();
            }
        }
        // For ACCEPTED_B, at least two non-zero concentration calibration gases spanning entire range of fCO2 values
        // For ACCEPTED_C or ACCEPTED_D, at least two calibration gases, one of which can be zero concentration
        if ( numCalibGases < 2 )
            throw new IllegalArgumentException("Not enough calibration gases");
        if ( numNonZeroCalibGases < 1 )
            throw new IllegalArgumentException("Not enough non-zero concentration calibration gases");
        if ( autoSuggest.equals(DatasetQCStatus.Status.ACCEPTED_B) && (numNonZeroCalibGases < 2) )
            autoSuggest = DatasetQCStatus.Status.ACCEPTED_C;

        // ACCEPTED_B must be:
        //     from IR, GC, or Spectroscopy,
        //     continuously measured and frequently calibrated,
        //     calibration gas concentrations spans the entire range of fCO2 values
        //     warming between SST and Tequ less than 1 deg C

        // ACCEPTED_C (not alternative sensor) must be either:
        //     from IR, GC, or Spectroscopy,
        //     continuously measured and frequently calibrated,
        //     [0.8,1.2] times the calibration gas concentrations spans the entire range of fCO2 values
        //     warming between SST and Tequ less than 3 deg C

        return autoSuggest;
    }

}
