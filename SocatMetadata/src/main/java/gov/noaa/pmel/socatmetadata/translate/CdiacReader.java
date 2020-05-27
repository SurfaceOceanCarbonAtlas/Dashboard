package gov.noaa.pmel.socatmetadata.translate;

import gov.noaa.pmel.socatmetadata.shared.core.Coverage;
import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.core.MiscInfo;
import gov.noaa.pmel.socatmetadata.shared.core.MultiNames;
import gov.noaa.pmel.socatmetadata.shared.core.MultiString;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import gov.noaa.pmel.socatmetadata.shared.core.SocatMetadata;
import gov.noaa.pmel.socatmetadata.shared.instrument.Analyzer;
import gov.noaa.pmel.socatmetadata.shared.instrument.CalibrationGas;
import gov.noaa.pmel.socatmetadata.shared.instrument.Equilibrator;
import gov.noaa.pmel.socatmetadata.shared.instrument.GasSensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;
import gov.noaa.pmel.socatmetadata.shared.instrument.PressureSensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.SalinitySensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.TemperatureSensor;
import gov.noaa.pmel.socatmetadata.shared.person.Investigator;
import gov.noaa.pmel.socatmetadata.shared.person.Submitter;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import gov.noaa.pmel.socatmetadata.shared.platform.PlatformType;
import gov.noaa.pmel.socatmetadata.shared.variable.AirPressure;
import gov.noaa.pmel.socatmetadata.shared.variable.AquGasConc;
import gov.noaa.pmel.socatmetadata.shared.variable.GasConc;
import gov.noaa.pmel.socatmetadata.shared.variable.GenData;
import gov.noaa.pmel.socatmetadata.shared.variable.InstData;
import gov.noaa.pmel.socatmetadata.shared.variable.MethodType;
import gov.noaa.pmel.socatmetadata.shared.variable.Temperature;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CdiacReader extends DocumentHandler {

    public enum VarType {
        OTHER,
        FCO2_WATER_EQU,
        FCO2_WATER_SST,
        PCO2_WATER_EQU,
        PCO2_WATER_SST,
        XCO2_WATER_EQU,
        XCO2_WATER_SST,
        FCO2_ATM_ACTUAL,
        FCO2_ATM_INTERP,
        PCO2_ATM_ACTUAL,
        PCO2_ATM_INTERP,
        XCO2_ATM_ACTUAL,
        XCO2_ATM_INTERP,
        SEA_SURFACE_TEMPERATURE,
        EQUILIBRATOR_TEMPERATURE,
        SEA_LEVEL_PRESSURE,
        EQUILIBRATOR_PRESSURE,
        SALINITY,
        WOCE_CO2_WATER,
        WOCE_CO2_ATM
    }

    private static final String NAME_ELEMENT_NAME = "Name";
    private static final String ORG_ELEMENT_NAME = "Organization";
    private static final String ADDRESS_ELEMENT_NAME = "Address";
    private static final String PHONE_ELEMENT_NAME = "Phone";
    private static final String EMAIL_ELEMENT_NAME = "Email";

    private static final String USER_ELEMENT_NAME = "User";
    private static final String USER_NAME_ELEMENT_NAME = USER_ELEMENT_NAME + SEP + NAME_ELEMENT_NAME;
    private static final String USER_ORG_ELEMENT_NAME = USER_ELEMENT_NAME + SEP + ORG_ELEMENT_NAME;
    private static final String USER_ADDRESS_ELEMENT_NAME = USER_ELEMENT_NAME + SEP + ADDRESS_ELEMENT_NAME;
    private static final String USER_PHONE_ELEMENT_NAME = USER_ELEMENT_NAME + SEP + PHONE_ELEMENT_NAME;
    private static final String USER_EMAIL_ELEMENT_NAME = USER_ELEMENT_NAME + SEP + EMAIL_ELEMENT_NAME;

    private static final String INVESTIGATOR_ELEMENT_NAME = "Investigator";
    private static final String INVESTIGATOR_NAME_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + NAME_ELEMENT_NAME;
    private static final String INVESTIGATOR_ORG_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + ORG_ELEMENT_NAME;
    private static final String INVESTIGATOR_ADDRESS_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + ADDRESS_ELEMENT_NAME;
    private static final String INVESTIGATOR_PHONE_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + PHONE_ELEMENT_NAME;
    private static final String INVESTIGATOR_EMAIL_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + EMAIL_ELEMENT_NAME;

    private static final String DATASET_INFO_ELEMENT_NAME = "Dataset_Info";
    private static final String FUNDING_INFO_ELEMENT_NAME = DATASET_INFO_ELEMENT_NAME + SEP + "Funding_Info";

    private static final String SUBMISSION_DATES_ELEMENT_NAME = DATASET_INFO_ELEMENT_NAME + SEP + "Submission_Dates";
    private static final String INITIAL_SUBMISSION_ELEMENT_NAME = SUBMISSION_DATES_ELEMENT_NAME + SEP + "Initial_Submission";
    private static final String REVISED_SUBMISSION_ELEMENT_NAME = SUBMISSION_DATES_ELEMENT_NAME + SEP + "Revised_Submission";

    private static final String CRUISE_INFO_ELEMENT_NAME = "Cruise_Info";
    private static final String EXPERIMENT_ELEMENT_NAME = CRUISE_INFO_ELEMENT_NAME + SEP + "Experiment";
    private static final String EXPERIMENT_NAME_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + SEP + "Experiment_Name";
    private static final String EXPERIMENT_TYPE_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + SEP + "Experiment_Type";
    private static final String PLATFORM_TYPE_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + SEP + "Platform_Type";
    private static final String MOORING_ID_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + SEP + "Mooring_ID";

    private static final String CRUISE_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + SEP + "Cruise";
    private static final String EXPOCODE_ELEMENT_NAME = CRUISE_ELEMENT_NAME + SEP + "Expocode";
    private static final String CRUISE_ID_ELEMENT_NAME = CRUISE_ELEMENT_NAME + SEP + "Cruise_ID";
    private static final String SUB_CRUISE_INFO_ELEMENT_NAME = CRUISE_ELEMENT_NAME + SEP + "Cruise_Info";
    private static final String SECTION_ELEMENT_NAME = CRUISE_ELEMENT_NAME + SEP + "Section";
    private static final String PORT_OF_CALL_ELEMENT_NAME = CRUISE_ELEMENT_NAME + SEP + "Ports_of_Call";

    private static final String GEO_COVERAGE_ELEMENT_NAME = CRUISE_ELEMENT_NAME + SEP + "Geographical_Coverage";
    private static final String GEO_REGION_ELEMENT_NAME = GEO_COVERAGE_ELEMENT_NAME + SEP + "Geographical_Region";
    private static final String BOUNDS_ELEMENT_NAME = GEO_COVERAGE_ELEMENT_NAME + SEP + "Bounds";
    private static final String WEST_BOUND_ELEMENT_NAME = BOUNDS_ELEMENT_NAME + SEP + "Westernmost_Longitude";
    private static final String EAST_BOUND_ELEMENT_NAME = BOUNDS_ELEMENT_NAME + SEP + "Easternmost_Longitude";
    private static final String NORTH_BOUND_ELEMENT_NAME = BOUNDS_ELEMENT_NAME + SEP + "Northernmost_Latitude";
    private static final String SOUTH_BOUND_ELEMENT_NAME = BOUNDS_ELEMENT_NAME + SEP + "Southernmost_Latitude";

    private static final String TEMP_COVERAGE_ELEMENT_NAME = CRUISE_ELEMENT_NAME + SEP + "Temporal_Coverage";
    private static final String TEMP_START_DATE_ELEMENT_NAME = TEMP_COVERAGE_ELEMENT_NAME + SEP + "Start_Date";
    private static final String TEMP_END_DATE_ELEMENT_NAME = TEMP_COVERAGE_ELEMENT_NAME + SEP + "End_Date";
    private static final String START_DATE_ELEMENT_NAME = CRUISE_ELEMENT_NAME + SEP + "Start_Date";
    private static final String END_DATE_ELEMENT_NAME = CRUISE_ELEMENT_NAME + SEP + "End_Date";

    private static final String VESSEL_ELEMENT_NAME = CRUISE_INFO_ELEMENT_NAME + SEP + "Vessel";
    private static final String VESSEL_NAME_ELEMENT_NAME = VESSEL_ELEMENT_NAME + SEP + "Vessel_Name";
    private static final String VESSEL_ID_ELEMENT_NAME = VESSEL_ELEMENT_NAME + SEP + "Vessel_ID";
    private static final String VESSEL_COUNTRY_ELEMENT_NAME = VESSEL_ELEMENT_NAME + SEP + "Country";
    private static final String VESSEL_OWNER_ELEMENT_NAME = VESSEL_ELEMENT_NAME + SEP + "Vessel_Owner";

    private static final String VARIABLES_INFO_ELEMENT_NAME = "Variables_Info";
    private static final String VARIABLE_ELEMENT_NAME = VARIABLES_INFO_ELEMENT_NAME + SEP + "Variable";
    private static final String VARIABLES_NAME_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "Variable_Name";
    private static final String VARIABLES_DESCRIPTION_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "Description_of_Variable";
    private static final String VARIABLES_UNIT_OF_VARIABLE_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "Unit_of_Variable";


    private static final String METHOD_DESCRIPTION_ELEMENT_NAME = "Method_Description";
    private static final String EQUILIBRATOR_DESIGN_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + SEP + "Equilibrator_Design";
    private static final String INTAKE_DEPTH_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + SEP + "Depth_of_Sea_Water_Intake";
    private static final String INTAKE_LOCATION_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + SEP + "Location_of_Sea_Water_Intake";
    private static final String EQUI_TYPE_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + SEP + "Equilibrator_Type";
    private static final String EQUI_VOLUME_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + SEP + "Equilibrator_Volume";
    private static final String WATER_FLOW_RATE_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + SEP + "Water_Flow_Rate";
    private static final String GAS_FLOW_RATE_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + SEP + "Headspace_Gas_Flow_Rate";
    private static final String VENTED_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + SEP + "Vented";
    private static final String DRYING_METHOD_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + SEP + "Drying_Method_for_CO2_in_water";
    private static final String EQUI_ADDITIONAL_INFO_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + SEP + "Additional_Information";

    private static final String CO2_MARINE_AIR_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + SEP + "CO2_in_Marine_Air";
    private static final String MARINE_AIR_MEASUREMENT_ELEMENT_NAME = CO2_MARINE_AIR_ELEMENT_NAME + SEP + "Measurement";
    private static final String MARINE_AIR_LOCATION_ELEMENT_NAME = CO2_MARINE_AIR_ELEMENT_NAME + SEP + "Location_and_Height";
    private static final String MARINE_AIR_DRYING_ELEMENT_NAME = CO2_MARINE_AIR_ELEMENT_NAME + SEP + "Drying_Method";

    private static final String LOCATION_ELEMENT_NAME = "Location";
    private static final String MANUFACTURER_ELEMENT_NAME = "Manufacturer";
    private static final String MODEL_ELEMENT_NAME = "Model";
    private static final String COMMENTS_ELEMENT_NAME = "Other_Comments";
    private static final String ACCURACY_ELEMENT_NAME = "Accuracy";
    private static final String UNCERTAINTY_ELEMENT_NAME = "Uncertainty";
    private static final String PRECISION_ELEMENT_NAME = "Precision";
    private static final String RESOLUTION_ELEMENT_NAME = "Resolution";
    private static final String CALIBRATION_ELEMENT_NAME = "Calibration";

    private static final String CO2_SENSOR_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + SEP + "CO2_Sensors" + SEP + "CO2_Sensor";
    private static final String CO2_MEASUREMENT_METHOD_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Measurement_Method";
    private static final String CO2_SENSOR_MANUFACTURER_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + MANUFACTURER_ELEMENT_NAME;
    private static final String CO2_SENSOR_MODEL_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + MODEL_ELEMENT_NAME;
    private static final String CO2_FREQUENCY_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Frequency";
    private static final String CO2_WATER_RES_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Resolution_Water";
    private static final String CO2_WATER_UNC_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Uncertainty_Water";
    private static final String CO2_AIR_RES_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Resolution_Air";
    private static final String CO2_AIR_UNC_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Uncertainty_Air";
    private static final String CO2_CALIBRATION_MANUFACTURER_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Manufacturer_of_Calibration_Gas";
    private static final String CO2_SENSOR_CALIBRATION_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "CO2_Sensor_Calibration";
    private static final String ENVIRONMENTAL_CONTROL_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Environmental_Control";
    private static final String METHOD_REFS_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Method_References";
    private static final String DETAILS_OF_CO2_SENSING_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Details_Co2_Sensing";
    private static final String ANALYSIS_OF_COMPARISON_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Analysis_of_Co2_Comparision";
    private static final String MEASURED_CO2_PARAMS_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "Measured_Co2_Params";
    private static final String CO2_SENSOR_COMMENTS_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + COMMENTS_ELEMENT_NAME;
    private static final String CO2_SENSOR_NUM_NONZERO_GASSES_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + SEP + "No_Of_Non_Zero_Gas_Stds";

    private static final String SST_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + SEP + "Sea_Surface_Temperature";
    private static final String SST_LOCATION_ELEMENT_NAME = SST_ELEMENT_NAME + SEP + LOCATION_ELEMENT_NAME;
    private static final String SST_MANUFACTURER_ELEMENT_NAME = SST_ELEMENT_NAME + SEP + MANUFACTURER_ELEMENT_NAME;
    private static final String SST_MODEL_ELEMENT_NAME = SST_ELEMENT_NAME + SEP + MODEL_ELEMENT_NAME;
    private static final String SST_ACCURACY_ELEMENT_NAME = SST_ELEMENT_NAME + SEP + ACCURACY_ELEMENT_NAME;
    private static final String SST_ACCURACY_DEGC_ELEMENT_NAME = SST_ELEMENT_NAME + SEP + ACCURACY_ELEMENT_NAME + "_degC";
    private static final String SST_UNCERTAINTY_ELEMENT_NAME = SST_ELEMENT_NAME + SEP + UNCERTAINTY_ELEMENT_NAME;
    private static final String SST_PRECISION_ELEMENT_NAME = SST_ELEMENT_NAME + SEP + PRECISION_ELEMENT_NAME;
    private static final String SST_PRECISION_DEGC_ELEMENT_NAME = SST_ELEMENT_NAME + SEP + PRECISION_ELEMENT_NAME + "_degC";
    private static final String SST_RESOLUTION_ELEMENT_NAME = SST_ELEMENT_NAME + SEP + RESOLUTION_ELEMENT_NAME;
    private static final String SST_CALIBRATION_ELEMENT_NAME = SST_ELEMENT_NAME + SEP + CALIBRATION_ELEMENT_NAME;
    private static final String SST_COMMENTS_ELEMENT_NAME = SST_ELEMENT_NAME + SEP + COMMENTS_ELEMENT_NAME;

    private static final String EQU_TEMP_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + SEP + "Equilibrator_Temperature";
    private static final String EQT_LOCATION_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + LOCATION_ELEMENT_NAME;
    private static final String EQT_MANUFACTURER_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + MANUFACTURER_ELEMENT_NAME;
    private static final String EQT_MODEL_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + MODEL_ELEMENT_NAME;
    private static final String EQT_ACCURACY_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + ACCURACY_ELEMENT_NAME;
    private static final String EQT_ACCURACY_DEGC_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + ACCURACY_ELEMENT_NAME + "_degC";
    private static final String EQT_UNCERTAINTY_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + UNCERTAINTY_ELEMENT_NAME;
    private static final String EQT_PRECISION_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + PRECISION_ELEMENT_NAME;
    private static final String EQT_PRECISION_DEGC_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + PRECISION_ELEMENT_NAME + "_degC";
    private static final String EQT_RESOLUTION_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + RESOLUTION_ELEMENT_NAME;
    private static final String EQT_CALIBRATION_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + CALIBRATION_ELEMENT_NAME;
    private static final String EQT_COMMENTS_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + COMMENTS_ELEMENT_NAME;
    private static final String EQT_WARMING_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + SEP + "Warming";

    private static final String ATM_PRESSURE_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + SEP + "Atmospheric_Pressure";
    private static final String ATM_LOCATION_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + LOCATION_ELEMENT_NAME;
    private static final String ATM_MANUFACTURER_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + MANUFACTURER_ELEMENT_NAME;
    private static final String ATM_MODEL_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + MODEL_ELEMENT_NAME;
    private static final String ATM_ACCURACY_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + ACCURACY_ELEMENT_NAME;
    private static final String ATM_ACCURACY_HPA_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + ACCURACY_ELEMENT_NAME + "_hPa";
    private static final String ATM_UNCERTAINTY_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + UNCERTAINTY_ELEMENT_NAME;
    private static final String ATM_PRECISION_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + PRECISION_ELEMENT_NAME;
    private static final String ATM_PRECISION_HPA_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + PRECISION_ELEMENT_NAME + "_hPa";
    private static final String ATM_RESOLUTION_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + RESOLUTION_ELEMENT_NAME;
    private static final String ATM_CALIBRATION_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + CALIBRATION_ELEMENT_NAME;
    private static final String ATM_COMMENTS_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + COMMENTS_ELEMENT_NAME;
    private static final String ATM_NORMALIZED_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + SEP + "Normalized";

    private static final String EQU_PRESSURE_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + SEP + "Equilibrator_Pressure";
    private static final String EQP_LOCATION_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + LOCATION_ELEMENT_NAME;
    private static final String EQP_MANUFACTURER_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + MANUFACTURER_ELEMENT_NAME;
    private static final String EQP_MODEL_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + MODEL_ELEMENT_NAME;
    private static final String EQP_ACCURACY_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + ACCURACY_ELEMENT_NAME;
    private static final String EQP_ACCURACY_HPA_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + ACCURACY_ELEMENT_NAME + "_hPa";
    private static final String EQP_UNCERTAINTY_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + UNCERTAINTY_ELEMENT_NAME;
    private static final String EQP_PRECISION_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + PRECISION_ELEMENT_NAME;
    private static final String EQP_PRECISION_HPA_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + PRECISION_ELEMENT_NAME + "_hPa";
    private static final String EQP_RESOLUTION_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + RESOLUTION_ELEMENT_NAME;
    private static final String EQP_CALIBRATION_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + CALIBRATION_ELEMENT_NAME;
    private static final String EQP_COMMENTS_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + COMMENTS_ELEMENT_NAME;
    private static final String EQP_NORMALIZED_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + SEP + "Normalized";

    private static final String SSS_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + SEP + "Sea_Surface_Salinity";
    private static final String SSS_LOCATION_ELEMENT_NAME = SSS_ELEMENT_NAME + SEP + LOCATION_ELEMENT_NAME;
    private static final String SSS_MANUFACTURER_ELEMENT_NAME = SSS_ELEMENT_NAME + SEP + MANUFACTURER_ELEMENT_NAME;
    private static final String SSS_MODEL_ELEMENT_NAME = SSS_ELEMENT_NAME + SEP + MODEL_ELEMENT_NAME;
    private static final String SSS_ACCURACY_ELEMENT_NAME = SSS_ELEMENT_NAME + SEP + ACCURACY_ELEMENT_NAME;
    private static final String SSS_UNCERTAINTY_ELEMENT_NAME = SSS_ELEMENT_NAME + SEP + UNCERTAINTY_ELEMENT_NAME;
    private static final String SSS_PRECISION_ELEMENT_NAME = SSS_ELEMENT_NAME + SEP + PRECISION_ELEMENT_NAME;
    private static final String SSS_RESOLUTION_ELEMENT_NAME = SSS_ELEMENT_NAME + SEP + RESOLUTION_ELEMENT_NAME;
    private static final String SSS_CALIBRATION_ELEMENT_NAME = SSS_ELEMENT_NAME + SEP + CALIBRATION_ELEMENT_NAME;
    private static final String SSS_COMMENTS_ELEMENT_NAME = SSS_ELEMENT_NAME + SEP + COMMENTS_ELEMENT_NAME;

    private static final String OTHER_SENSORS_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + SEP + "Other_Sensors" + SEP + "Sensor";
    private static final String OTHER_SENSORS_LOCATION_ELEMENT_NAME = OTHER_SENSORS_ELEMENT_NAME + SEP + LOCATION_ELEMENT_NAME;
    private static final String OTHER_SENSORS_MANUFACTURER_ELEMENT_NAME = OTHER_SENSORS_ELEMENT_NAME + SEP + MANUFACTURER_ELEMENT_NAME;
    private static final String OTHER_SENSORS_MODEL_ELEMENT_NAME = OTHER_SENSORS_ELEMENT_NAME + SEP + MODEL_ELEMENT_NAME;
    private static final String OTHER_SENSORS_COMMENTS_ELEMENT_NAME = OTHER_SENSORS_ELEMENT_NAME + SEP + COMMENTS_ELEMENT_NAME;
    private static final String OTHER_SENSORS_ACCURACY_ELEMENT_NAME = OTHER_SENSORS_ELEMENT_NAME + SEP + ACCURACY_ELEMENT_NAME;
    private static final String OTHER_SENSORS_UNCERTAINTY_ELEMENT_NAME = OTHER_SENSORS_ELEMENT_NAME + SEP + UNCERTAINTY_ELEMENT_NAME;
    private static final String OTHER_SENSORS_PRECISION_ELEMENT_NAME = OTHER_SENSORS_ELEMENT_NAME + SEP + PRECISION_ELEMENT_NAME;
    private static final String OTHER_SENSORS_RESOLUTION_ELEMENT_NAME = OTHER_SENSORS_ELEMENT_NAME + SEP + RESOLUTION_ELEMENT_NAME;
    private static final String OTHER_SENSORS_CALIBRATION_ELEMENT_NAME = OTHER_SENSORS_ELEMENT_NAME + SEP + CALIBRATION_ELEMENT_NAME;

    private static final String DATA_SET_REFS_ELEMENT_NAME = "Data_set_References";
    private static final String ADDN_INFO_ELEMENT_NAME = "Additional_Information";
    private static final String CITATION_ELEMENT_NAME = "Citation";

    private static final String DATA_SET_LINK_ELEMENT_NAME = "Data_Set_Link";
    private static final String DATA_SET_LINK_URL_ELEMENT_NAME = DATA_SET_LINK_ELEMENT_NAME + SEP + "URL";
    private static final String DATA_SET_LINK_NOTE_ELEMENT_NAME = DATA_SET_LINK_ELEMENT_NAME + SEP + "Link_Note";

    private static final Pattern STRIP_PATTERN = Pattern.compile("[^\\p{javaUpperCase}\\p{Digit}]+");
    private static final HashMap<String,VarType> DEFAULT_KEY_TO_TYPE_MAP;
    // Assignment at the end of this file

    HashMap<String,VarType> keyToTypeMap;

    /**
     * Create from CDIAC XML content provided by the given reader.
     *
     * @param xmlReader
     *         read the CDIAC XML from here
     *
     * @throws IllegalArgumentException
     *         if there is a problem interpreting the XML read
     */
    public CdiacReader(Reader xmlReader) throws IllegalArgumentException {
        Document omeDoc;
        try {
            omeDoc = (new SAXBuilder()).build(xmlReader);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems interpreting the XML contents: " + ex.getMessage());
        }
        rootElement = omeDoc.getRootElement();
        if ( rootElement == null )
            throw new IllegalArgumentException("No root element found");

        this.keyToTypeMap = new HashMap<String,VarType>(DEFAULT_KEY_TO_TYPE_MAP);
    }

    /**
     * Adds an association of a column name to a variable type.  The name key is generated from
     * the name (remove anything not alphanumeric and convert to lowercase), and the resulting
     * key added to the current map of name keys to variable types.
     *
     * @param colName
     *         name of the column
     * @param type
     *         variable type
     *
     * @return previous variable type associated with the name key, or
     *         null if there was no variable type previously associated with the name key
     */
    public VarType associateColumnNameWithVarType(String colName, VarType type) {
        String key = STRIP_PATTERN.matcher(colName.toUpperCase()).replaceAll("").toLowerCase();
        return keyToTypeMap.put(key, type);
    }

    /**
     * @param colName
     *         column name for the variable; cannot be null
     *
     * @return guessed type of this variable
     */
    public VarType getVarTypeFromColumnName(String colName) {
        String key = STRIP_PATTERN.matcher(colName.toUpperCase()).replaceAll("").toLowerCase();
        VarType type = keyToTypeMap.get(key);
        if ( type == null )
            type = VarType.OTHER;
        return type;
    }

    /**
     * @return an SocatMetadata object populated with information found in this CDIAC XML file; never null
     */
    public SocatMetadata createSocatMetadata() {
        SocatMetadata mdata = new SocatMetadata();
        MiscInfo misc = getMiscInfo();
        mdata.setMiscInfo(misc);
        mdata.setSubmitter(getSubmitter());
        mdata.setInvestigators(getInvestigators());
        Platform platform = getPlatform(misc.getDatasetId());
        mdata.setPlatform(platform);
        mdata.setCoverage(getCoverage());
        mdata.setVariables(getVariables(platform.getPlatformType()));
        mdata.setInstruments(getInstruments());
        return mdata;
    }

    /**
     * @return the miscellaneous information read from this CDIAC XML file; never null
     */
    private MiscInfo getMiscInfo() {
        MiscInfo info = new MiscInfo();

        // Dataset ID / Expocode
        String expocode = getElementText(null, EXPOCODE_ELEMENT_NAME);
        if ( expocode.isEmpty() )
            expocode = getElementText(null, CRUISE_ID_ELEMENT_NAME);
        info.setDatasetId(expocode);

        info.setDatasetName(getElementText(null, EXPERIMENT_NAME_ELEMENT_NAME));
        info.setSectionName(getElementText(null, SECTION_ELEMENT_NAME));

        // Funding information all glummed together in CDIAC XML - stick under agency name
        info.setFundingAgency(getElementText(null, FUNDING_INFO_ELEMENT_NAME));

        ArrayList<Datestamp> history = new ArrayList<Datestamp>();
        Datestamp stamp = getDatestamp(getElementText(null, INITIAL_SUBMISSION_ELEMENT_NAME));
        if ( stamp != null )
            history.add(stamp);
        for (Element elem : getElementList(null, REVISED_SUBMISSION_ELEMENT_NAME)) {
            stamp = getDatestamp(elem.getTextTrim());
            if ( (stamp != null) && !history.contains(stamp) )
                history.add(stamp);
        }
        info.setHistory(history);

        MultiString portsOfCall = new MultiString();
        for (Element portElem : getElementList(null, PORT_OF_CALL_ELEMENT_NAME)) {
            String port = portElem.getTextTrim();
            if ( !port.isEmpty() )
                portsOfCall.append(port);
        }
        info.setPortsOfCall(portsOfCall);

        info.setReferences(new MultiString(getElementText(null, DATA_SET_REFS_ELEMENT_NAME)));
        info.setCitation(getElementText(null, CITATION_ELEMENT_NAME));
        info.setWebsite(getElementText(null, DATA_SET_LINK_URL_ELEMENT_NAME));

        MultiString addnInfo = new MultiString();
        String text;
        text = getElementText(null, EXPERIMENT_TYPE_ELEMENT_NAME);
        if ( !text.isEmpty() )
            addnInfo.append("Experiment Type: " + text);
        text = getElementText(null, SUB_CRUISE_INFO_ELEMENT_NAME);
        if ( !text.isEmpty() )
            addnInfo.append("Cruise Info: " + text);
        text = getElementText(null, MOORING_ID_ELEMENT_NAME);
        if ( !text.isEmpty() )
            addnInfo.append("Mooring ID: " + text);
        text = getElementText(null, DATA_SET_LINK_NOTE_ELEMENT_NAME);
        if ( !text.isEmpty() )
            addnInfo.append("Website Note: " + text);
        addnInfo.append(getElementText(null, ADDN_INFO_ELEMENT_NAME));
        info.setAddnInfo(addnInfo);

        return info;
    }

    /**
     * @return information about the metadata/dataset submitter read from this CDIAC XML file; never null
     */
    private Submitter getSubmitter() {
        Submitter submitter = new Submitter(getPersonNames(getElementText(null, USER_NAME_ELEMENT_NAME)));
        submitter.setStreets(new MultiString(getElementText(null, USER_ADDRESS_ELEMENT_NAME)));
        // CDIAC XML does not separate streets, city, region, zip, country
        submitter.setOrganization(getElementText(null, USER_ORG_ELEMENT_NAME));
        submitter.setPhone(getElementText(null, USER_PHONE_ELEMENT_NAME));
        submitter.setEmail(getElementText(null, USER_EMAIL_ELEMENT_NAME));
        // CDIAC XML does not have the ID or ID type
        return submitter;
    }

    /**
     * @return information about the investigators read from this CDIAC XML file; never null
     */
    private ArrayList<Investigator> getInvestigators() {
        ArrayList<Investigator> piList = new ArrayList<Investigator>();
        for (Element inv : getElementList(null, INVESTIGATOR_ELEMENT_NAME)) {
            Investigator pi = new Investigator(getPersonNames(getElementText(inv, INVESTIGATOR_NAME_ELEMENT_NAME)));
            pi.setStreets(new MultiString(getElementText(inv, INVESTIGATOR_ADDRESS_ELEMENT_NAME)));
            // CDIAC XML does not separate streets, city, region, zip, country
            pi.setOrganization(getElementText(inv, INVESTIGATOR_ORG_ELEMENT_NAME));
            pi.setPhone(getElementText(inv, INVESTIGATOR_PHONE_ELEMENT_NAME));
            pi.setEmail(getElementText(inv, INVESTIGATOR_EMAIL_ELEMENT_NAME));
            // CDIAC XML does not have the ID or ID type
            piList.add(pi);
        }
        return piList;
    }

    /**
     * @param datasetId
     *         dataset ID; if it matches the expocode pattern, the first four characters
     *         are used as the platform NODC code to help identify the platform type
     *
     * @return information about the platform; never null
     */
    private Platform getPlatform(String datasetId) {
        Platform platform = new Platform();
        platform.setPlatformId(getElementText(null, VESSEL_ID_ELEMENT_NAME));
        String name = getElementText(null, VESSEL_NAME_ELEMENT_NAME);
        platform.setPlatformName(name);
        PlatformType type = PlatformType.parse(getElementText(null, PLATFORM_TYPE_ELEMENT_NAME));
        if ( PlatformType.UNKNOWN.equals(type) )
            type = guessPlatformType(name, datasetId);
        platform.setPlatformType(type);
        platform.setPlatformOwner(getElementText(null, VESSEL_OWNER_ELEMENT_NAME));
        platform.setPlatformCountry(getElementText(null, VESSEL_COUNTRY_ELEMENT_NAME));
        return platform;
    }

    /**
     * @return information about the data coverage; never null.  Data time limits are the earliest day
     *         of the start date and latest day of the end date, and so are not more accurate than the day.
     */
    private Coverage getCoverage() {
        Coverage coverage = new Coverage();

        coverage.setWesternLongitude(
                getNumericString(getElementText(null, WEST_BOUND_ELEMENT_NAME), Coverage.LONGITUDE_UNITS));
        coverage.setEasternLongitude(
                getNumericString(getElementText(null, EAST_BOUND_ELEMENT_NAME), Coverage.LONGITUDE_UNITS));
        coverage.setSouthernLatitude(
                getNumericString(getElementText(null, SOUTH_BOUND_ELEMENT_NAME), Coverage.LATITUDE_UNITS));
        coverage.setNorthernLatitude(
                getNumericString(getElementText(null, NORTH_BOUND_ELEMENT_NAME), Coverage.LATITUDE_UNITS));

        // If the "cruise" start and end dates are not specified, use the temporal coverage dates

        Datestamp timestamp = getDatestamp(getElementText(null, TEMP_START_DATE_ELEMENT_NAME));
        if ( timestamp != null )
            coverage.setEarliestDataDate(timestamp);

        Datestamp datestamp = getDatestamp(getElementText(null, START_DATE_ELEMENT_NAME));
        if ( datestamp == null )
            datestamp = timestamp;
        coverage.setStartDatestamp(datestamp);

        timestamp = getDatestamp(getElementText(null, TEMP_END_DATE_ELEMENT_NAME));
        if ( timestamp != null )
            coverage.setLatestDataDate(timestamp);

        datestamp = getDatestamp(getElementText(null, END_DATE_ELEMENT_NAME));
        if ( datestamp == null )
            datestamp = timestamp;
        coverage.setEndDatestamp(datestamp);

        MultiNames regions = new MultiNames();
        for (Element regElem : getElementList(null, GEO_REGION_ELEMENT_NAME)) {
            String name = regElem.getTextTrim();
            if ( !name.isEmpty() )
                regions.add(name);
        }
        coverage.setGeographicNames(regions);

        return coverage;
    }

    /**
     * @param platformType
     *         use to set the observation type (Mooring -> "Time Series"; othewise "Surface Underway")
     */
    private ArrayList<Variable> getVariables(PlatformType platformType) {
        ArrayList<Variable> varList = new ArrayList<Variable>();
        ArrayList<Integer> co2WaterVarIndices = new ArrayList<Integer>();
        ArrayList<Integer> co2AtmVarIndices = new ArrayList<Integer>();
        String woceCO2WaterVarNames = null;
        String woceCO2AtmVarNames = null;
        int k = 0;
        for (Element varElem : getElementList(null, VARIABLE_ELEMENT_NAME)) {
            Variable var = new Variable();
            String colName = getElementText(varElem, VARIABLES_NAME_ELEMENT_NAME);
            var.setColName(colName);
            var.setFullName(getElementText(varElem, VARIABLES_DESCRIPTION_ELEMENT_NAME));
            var.setVarUnit(getElementText(varElem, VARIABLES_UNIT_OF_VARIABLE_ELEMENT_NAME));

            VarType type = getVarTypeFromColumnName(colName);
            switch ( type ) {
                case OTHER:
                    break;
                case FCO2_WATER_EQU:
                case PCO2_WATER_EQU:
                case XCO2_WATER_EQU: {
                    co2WaterVarIndices.add(k);
                    AquGasConc co2WaterEqu = new AquGasConc(var);
                    MultiString addnInfo = new MultiString();
                    co2WaterEqu.setReportTemperature("equilibrator temperature");
                    co2WaterEqu.setMeasureMethod(MethodType.MEASURED_INSITU);
                    co2WaterEqu.setInstrumentNames(new MultiNames("Equilibrator, CO2 Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        co2WaterEqu.setObserveType("Time Series");
                    else
                        co2WaterEqu.setObserveType("Surface Underway");
                    String strVal = getElementText(null, CO2_WATER_UNC_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2WaterEqu.setAccuracy(numStr);
                    else
                        addnInfo.append("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(null, CO2_WATER_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2WaterEqu.setPrecision(numStr);
                    else
                        addnInfo.append("Precision/Resolution: " + strVal);
                    strVal = getElementText(null, CO2_FREQUENCY_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.append("Frequency: " + strVal);
                    co2WaterEqu.setMethodReference(getElementText(null, METHOD_REFS_ELEMENT_NAME));
                    co2WaterEqu.setMethodDescription(getElementText(null, CO2_MEASUREMENT_METHOD_ELEMENT_NAME));
                    co2WaterEqu.setSamplingLocation(getElementText(null, INTAKE_LOCATION_ELEMENT_NAME));
                    co2WaterEqu.setSamplingElevation(
                            "Sampling Depth: " + getElementText(null, INTAKE_DEPTH_ELEMENT_NAME));
                    co2WaterEqu.setDryingMethod(getElementText(null, DRYING_METHOD_ELEMENT_NAME));
                    strVal = getElementText(null, DETAILS_OF_CO2_SENSING_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.append("Details of CO2 Sensing: " + strVal);
                    co2WaterEqu.setAddnInfo(addnInfo);
                    var = co2WaterEqu;
                    break;
                }
                case FCO2_WATER_SST:
                case PCO2_WATER_SST:
                case XCO2_WATER_SST: {
                    co2WaterVarIndices.add(k);
                    AquGasConc co2WaterSst = new AquGasConc(var);
                    MultiString addnInfo = new MultiString();
                    co2WaterSst.setReportTemperature("SST");
                    co2WaterSst.setMeasureMethod(MethodType.MEASURED_INSITU);
                    co2WaterSst.setInstrumentNames(new MultiNames("Equilibrator, CO2 Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        co2WaterSst.setObserveType("Time Series");
                    else
                        co2WaterSst.setObserveType("Surface Underway");
                    String strVal = getElementText(null, CO2_WATER_UNC_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2WaterSst.setAccuracy(numStr);
                    else
                        addnInfo.append("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(null, CO2_WATER_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2WaterSst.setPrecision(numStr);
                    else
                        addnInfo.append("Precision/Resolution: " + strVal);
                    strVal = getElementText(null, CO2_FREQUENCY_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.append("Frequency: " + strVal);
                    co2WaterSst.setMethodReference(getElementText(null, METHOD_REFS_ELEMENT_NAME));
                    co2WaterSst.setMethodDescription(getElementText(null, CO2_MEASUREMENT_METHOD_ELEMENT_NAME));
                    co2WaterSst.setSamplingLocation(getElementText(null, INTAKE_LOCATION_ELEMENT_NAME));
                    co2WaterSst.setSamplingElevation(
                            "Sampling Depth: " + getElementText(null, INTAKE_DEPTH_ELEMENT_NAME));
                    co2WaterSst.setDryingMethod(getElementText(null, DRYING_METHOD_ELEMENT_NAME));
                    strVal = getElementText(null, DETAILS_OF_CO2_SENSING_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.append("Details of CO2 Sensing: " + strVal);
                    co2WaterSst.setAddnInfo(addnInfo);
                    var = co2WaterSst;
                    break;
                }
                case FCO2_ATM_ACTUAL:
                case PCO2_ATM_ACTUAL:
                case XCO2_ATM_ACTUAL: {
                    co2AtmVarIndices.add(k);
                    GasConc co2AtmActual = new GasConc(var);
                    MultiString addnInfo = new MultiString();
                    co2AtmActual.setMeasureMethod(MethodType.MEASURED_INSITU);
                    co2AtmActual.setInstrumentNames(new MultiNames("CO2 Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        co2AtmActual.setObserveType("Time Series");
                    else
                        co2AtmActual.setObserveType("Surface Underway");
                    String strVal = getElementText(null, CO2_AIR_UNC_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2AtmActual.setAccuracy(numStr);
                    else
                        addnInfo.append("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(null, CO2_AIR_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2AtmActual.setPrecision(numStr);
                    else
                        addnInfo.append("Precision/Resolution: " + strVal);
                    co2AtmActual.setMethodReference(getElementText(null, METHOD_REFS_ELEMENT_NAME));
                    co2AtmActual.setMethodDescription(getElementText(null, CO2_MEASUREMENT_METHOD_ELEMENT_NAME));
                    co2AtmActual.setSamplingLocation(getElementText(null, MARINE_AIR_LOCATION_ELEMENT_NAME));
                    co2AtmActual.setDryingMethod(getElementText(null, MARINE_AIR_DRYING_ELEMENT_NAME));
                    strVal = getElementText(null, DETAILS_OF_CO2_SENSING_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.append("Details of CO2 Sensing: " + strVal);
                    strVal = getElementText(null, MARINE_AIR_MEASUREMENT_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.append("Measurement: " + strVal);
                    co2AtmActual.setAddnInfo(addnInfo);
                    var = co2AtmActual;
                    break;
                }
                case FCO2_ATM_INTERP:
                case PCO2_ATM_INTERP:
                case XCO2_ATM_INTERP: {
                    co2AtmVarIndices.add(k);
                    GasConc co2AtmInterp = new GasConc(var);
                    MultiString addnInfo = new MultiString();
                    co2AtmInterp.setMeasureMethod(MethodType.COMPUTED);
                    co2AtmInterp.setInstrumentNames(new MultiNames("CO2 Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        co2AtmInterp.setObserveType("Time Series");
                    else
                        co2AtmInterp.setObserveType("Surface Underway");
                    String strVal = getElementText(null, CO2_AIR_UNC_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2AtmInterp.setAccuracy(numStr);
                    else
                        addnInfo.append("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(null, CO2_AIR_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2AtmInterp.setPrecision(numStr);
                    else
                        addnInfo.append("Precision/Resolution: " + strVal);
                    co2AtmInterp.setMethodReference(getElementText(null, METHOD_REFS_ELEMENT_NAME));
                    co2AtmInterp.setMethodDescription(getElementText(null, CO2_MEASUREMENT_METHOD_ELEMENT_NAME));
                    co2AtmInterp.setSamplingLocation(getElementText(null, MARINE_AIR_LOCATION_ELEMENT_NAME));
                    co2AtmInterp.setDryingMethod(getElementText(null, MARINE_AIR_DRYING_ELEMENT_NAME));
                    strVal = getElementText(null, DETAILS_OF_CO2_SENSING_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.append("Details of CO2 Sensing: " + strVal);
                    strVal = getElementText(null, MARINE_AIR_MEASUREMENT_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.append("Measurement: " + strVal);
                    co2AtmInterp.setAddnInfo(addnInfo);
                    var = co2AtmInterp;
                    break;
                }
                case SEA_SURFACE_TEMPERATURE: {
                    Temperature sst = new Temperature(var);
                    MultiString addnInfo = new MultiString();
                    sst.setMeasureMethod(MethodType.MEASURED_INSITU);
                    sst.setInstrumentNames(new MultiNames("Water Temperature Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        sst.setObserveType("Time Series");
                    else
                        sst.setObserveType("Surface Underway");
                    String strVal = getElementText(null, SST_ACCURACY_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, SST_ACCURACY_DEGC_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, SST_UNCERTAINTY_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        sst.setAccuracy(numStr);
                    else
                        addnInfo.append("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(null, SST_PRECISION_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, SST_PRECISION_DEGC_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, SST_RESOLUTION_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        sst.setPrecision(numStr);
                    else
                        addnInfo.append("Precision/Resolution: " + strVal);
                    sst.setSamplingLocation(getElementText(null, SST_LOCATION_ELEMENT_NAME));
                    sst.setAddnInfo(addnInfo);
                    var = sst;
                    break;
                }
                case EQUILIBRATOR_TEMPERATURE: {
                    Temperature tequ = new Temperature(var);
                    MultiString addnInfo = new MultiString();
                    tequ.setMeasureMethod(MethodType.MEASURED_INSITU);
                    tequ.setInstrumentNames(new MultiNames("Equilibrator Temperature Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        tequ.setObserveType("Time Series");
                    else
                        tequ.setObserveType("Surface Underway");
                    String strVal = getElementText(null, EQT_ACCURACY_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, EQT_ACCURACY_DEGC_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, EQT_UNCERTAINTY_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        tequ.setAccuracy(numStr);
                    else
                        addnInfo.append("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(null, EQT_PRECISION_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, EQT_PRECISION_DEGC_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, EQT_RESOLUTION_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        tequ.setPrecision(numStr);
                    else
                        addnInfo.append("Precision/Resolution: " + strVal);
                    tequ.setSamplingLocation(getElementText(null, EQT_LOCATION_ELEMENT_NAME));
                    tequ.setAddnInfo(addnInfo);
                    var = tequ;
                    break;
                }
                case SEA_LEVEL_PRESSURE: {
                    AirPressure slp = new AirPressure(var);
                    MultiString addnInfo = new MultiString();
                    slp.setMeasureMethod(MethodType.MEASURED_INSITU);
                    slp.setInstrumentNames(new MultiNames("Atmospheric Pressure Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        slp.setObserveType("Time Series");
                    else
                        slp.setObserveType("Surface Underway");
                    String strVal = getElementText(null, ATM_ACCURACY_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, ATM_ACCURACY_HPA_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, ATM_UNCERTAINTY_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        slp.setAccuracy(numStr);
                    else
                        addnInfo.append("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(null, ATM_PRECISION_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, ATM_PRECISION_HPA_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, ATM_RESOLUTION_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        slp.setPrecision(numStr);
                    else
                        addnInfo.append("Precision/Resolution: " + strVal);
                    slp.setSamplingLocation(getElementText(null, ATM_LOCATION_ELEMENT_NAME));
                    strVal = getElementText(null, ATM_NORMALIZED_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        slp.setPressureCorrection("Normalized: " + strVal);
                    slp.setAddnInfo(addnInfo);
                    var = slp;
                    break;
                }
                case EQUILIBRATOR_PRESSURE: {
                    AirPressure pequ = new AirPressure(var);
                    MultiString addnInfo = new MultiString();
                    pequ.setMeasureMethod(MethodType.MEASURED_INSITU);
                    pequ.setInstrumentNames(new MultiNames("Equilibrator Pressure Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        pequ.setObserveType("Time Series");
                    else
                        pequ.setObserveType("Surface Underway");
                    String strVal = getElementText(null, EQP_ACCURACY_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, EQP_ACCURACY_HPA_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, EQP_UNCERTAINTY_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        pequ.setAccuracy(numStr);
                    else
                        addnInfo.append("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(null, EQP_PRECISION_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, EQP_PRECISION_HPA_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, EQP_RESOLUTION_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        pequ.setPrecision(numStr);
                    else
                        addnInfo.append("Precision/Resolution: " + strVal);
                    pequ.setSamplingLocation(getElementText(null, EQP_LOCATION_ELEMENT_NAME));
                    strVal = getElementText(null, EQP_NORMALIZED_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        pequ.setPressureCorrection("Normalized: " + strVal);
                    pequ.setAddnInfo(addnInfo);
                    var = pequ;
                    break;
                }
                case SALINITY: {
                    InstData sal = new InstData(var);
                    MultiString addnInfo = new MultiString();
                    sal.setMeasureMethod(MethodType.MEASURED_INSITU);
                    sal.setInstrumentNames(new MultiNames("Salinity Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        sal.setObserveType("Time Series");
                    else
                        sal.setObserveType("Surface Underway");
                    String strVal = getElementText(null, SSS_ACCURACY_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, SSS_UNCERTAINTY_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        sal.setAccuracy(numStr);
                    else
                        addnInfo.append("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(null, SSS_PRECISION_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(null, SSS_RESOLUTION_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        sal.setPrecision(numStr);
                    else
                        addnInfo.append("Precision/Resolution: " + strVal);
                    sal.setSamplingLocation(getElementText(null, SSS_LOCATION_ELEMENT_NAME));
                    sal.setAddnInfo(addnInfo);
                    var = sal;
                    break;
                }
                case WOCE_CO2_WATER:
                    if ( woceCO2WaterVarNames == null )
                        woceCO2WaterVarNames = colName;
                    else
                        woceCO2WaterVarNames += ", " + colName;
                    break;
                case WOCE_CO2_ATM:
                    if ( woceCO2AtmVarNames == null )
                        woceCO2AtmVarNames = colName;
                    else
                        woceCO2AtmVarNames += ", " + colName;
                    break;
                default:
                    throw new RuntimeException("Unexpected VarType of " + type);
            }

            varList.add(var);
            k++;
        }
        // Mention any WOCE flags
        if ( woceCO2WaterVarNames != null ) {
            for (int idx : co2WaterVarIndices) {
                GenData genData = (GenData) (varList.get(idx));
                genData.setFlagColName(woceCO2WaterVarNames);
            }
        }
        if ( woceCO2AtmVarNames != null ) {
            for (int idx : co2AtmVarIndices) {
                GenData genData = (GenData) (varList.get(idx));
                genData.setFlagColName(woceCO2AtmVarNames);
            }
        }

        return varList;
    }

    /**
     * Pattern for equilibrator chamber volume descriptions.
     */
    private static final Pattern AOML_VOLUME_DESCRIPTION_PATTERN =
            Pattern.compile("(\\d*\\.?\\d*)\\s*L\\s*\\(\\s*" +
                    "(\\d*\\.?\\d*)\\s*L\\s*water\\s*,\\s*" +
                    "(\\d*\\.?\\d*)\\s*L\\s*headspace\\s*\\)"
            );

    /**
     * Pattern for calibration gas (with ID) descriptions.
     */
    private static final Pattern AOML_CALIBRATION_GAS_DESCRIPTION_PATTERN =
            Pattern.compile("Std\\.?\\s*\\d\\s*:\\s*(\\p{Alnum}+)\\s*,\\s*" +
                    "(\\d*\\.?\\d*)\\s*ppm\\s*,\\s*" +
                    "([\\p{Alnum}\\s]+),\\s" +
                    "used every (\\p{Print}+)"
            );

    /**
     * Pattern for calibration gas (without ID) descriptions.
     */
    private static final Pattern AOML_NO_ID_CALIBRATION_GAS_DESCRIPTION_PATTERN =
            Pattern.compile("Std\\.?\\s*\\d\\s*:\\s*" +
                    "(\\d*\\.?\\d*)\\s*ppm\\s*,\\s*" +
                    "([\\p{Alnum}\\s]+),\\s" +
                    "used every (\\p{Print}+)"
            );

    /**
     * Pattern for unused calibration gas descriptions.
     */
    private static final Pattern AOML_UNUSED_CALIBRATION_GAS_DESCRIPTION_PATTERN =
            Pattern.compile("Std\\.?\\s*\\d\\s*:\\s*(\\p{Alnum}+)\\s*,\\s*" +
                    "(\\d*\\.?\\d*)\\s*ppm\\s*,\\s*" +
                    "([\\p{Alnum}\\s]+),\\s" +
                    "was\\s*not\\s*used\\s*\\.?"
            );

    /**
     * @return list of instrument information; never null.
     */
    private ArrayList<Instrument> getInstruments() {
        ArrayList<Instrument> instruments = new ArrayList<Instrument>();

        Equilibrator equilibrator = new Equilibrator();
        equilibrator.setName("Equilibrator");
        // equilibrator.setId(id); - not specified
        // equilibrator.setManufacturer(manufacturer); - not specified
        // equilibrator.setModel(model); - not specified

        // The following are always added below
        equilibrator.setInstrumentNames(
                new MultiNames("Equilibrator Temperature Sensor, Equilibrator Pressure Sensor"));

        equilibrator.setEquilibratorType(getElementText(null, EQUI_TYPE_ELEMENT_NAME));
        String volumeDesc = getElementText(null, EQUI_VOLUME_ELEMENT_NAME);
        Matcher matcher = AOML_VOLUME_DESCRIPTION_PATTERN.matcher(volumeDesc);
        if ( matcher.matches() ) {
            equilibrator.setChamberVol(matcher.group(1) + " L");
            equilibrator.setChamberWaterVol(matcher.group(2) + " L");
            equilibrator.setChamberGasVol(matcher.group(3) + " L");
        }
        else
            equilibrator.setChamberVol(volumeDesc);
        equilibrator.setWaterFlowRate(getElementText(null, WATER_FLOW_RATE_ELEMENT_NAME));
        equilibrator.setGasFlowRate(getElementText(null, GAS_FLOW_RATE_ELEMENT_NAME));
        equilibrator.setVenting(getElementText(null, VENTED_ELEMENT_NAME));
        equilibrator.setAddnInfo(new MultiString(getElementText(null, EQUI_ADDITIONAL_INFO_ELEMENT_NAME)));

        instruments.add(equilibrator);

        GasSensor co2Sensor = new GasSensor();
        co2Sensor.setName("CO2 Sensor");
        co2Sensor.setManufacturer(getElementText(null, CO2_SENSOR_MANUFACTURER_ELEMENT_NAME));
        co2Sensor.setModel(getElementText(null, CO2_SENSOR_MODEL_ELEMENT_NAME));
        co2Sensor.setCalibration(getElementText(null, CO2_SENSOR_CALIBRATION_ELEMENT_NAME));
        MultiString addnInfo = new MultiString();
        String strVal;
        strVal = getElementText(null, CO2_SENSOR_NUM_NONZERO_GASSES_ELEMENT_NAME);
        if ( !strVal.isEmpty() )
            addnInfo.append("Number of non-zero gases: " + strVal);
        strVal = getElementText(null, MEASURED_CO2_PARAMS_ELEMENT_NAME);
        if ( !strVal.isEmpty() )
            addnInfo.append("Measured CO2 Parameters: " + strVal);
        strVal = getElementText(null, ENVIRONMENTAL_CONTROL_ELEMENT_NAME);
        if ( !strVal.isEmpty() )
            addnInfo.append("Environmental Control: " + strVal);
        strVal = getElementText(null, ANALYSIS_OF_COMPARISON_ELEMENT_NAME);
        if ( !strVal.isEmpty() )
            addnInfo.append("Analysis of CO2 Comparison: " + strVal);
        addnInfo.append(getElementText(null, CO2_SENSOR_COMMENTS_ELEMENT_NAME));
        co2Sensor.setAddnInfo(addnInfo);
        // All the calibration gas information is stuck together in the following ...
        String calGasInfo = getElementText(null, CO2_CALIBRATION_MANUFACTURER_ELEMENT_NAME);
        // See if the gasses are separated by newlines
        ArrayList<String> calGasInfoList = getListOfLines(calGasInfo);
        if ( calGasInfoList.size() == 1 ) {
            // All in one line; see if separated by .Std
            String[] pieces = calGasInfo.split("\\.\\s*Std");
            if ( pieces.length > 1 ) {
                calGasInfoList.clear();
                calGasInfoList.add(pieces[0].trim() + ".");
                for (int k = 1; k < pieces.length - 1; k++) {
                    calGasInfoList.add("Std " + pieces[k].trim() + ".");
                }
                calGasInfoList.add("Std " + pieces[pieces.length - 1].trim());
            }
        }
        ArrayList<CalibrationGas> gasList = new ArrayList<CalibrationGas>(calGasInfoList.size());
        for (String gasInfo : calGasInfoList) {
            String id = "";
            String supplier = "";
            String concStr = "";
            String accStr = "";
            String useFreq = "";
            matcher = AOML_CALIBRATION_GAS_DESCRIPTION_PATTERN.matcher(gasInfo);
            if ( matcher.matches() ) {
                id = matcher.group(1);
                concStr = matcher.group(2);
                supplier = matcher.group(3);
                useFreq = "used every " + matcher.group(4);
            }
            else {
                matcher = AOML_NO_ID_CALIBRATION_GAS_DESCRIPTION_PATTERN.matcher(gasInfo);
                if ( matcher.matches() ) {
                    concStr = matcher.group(1);
                    supplier = matcher.group(2);
                    useFreq = "used every " + matcher.group(3);
                }
                else {
                    // Matches the unused pattern, so ignore this entry
                    matcher = AOML_UNUSED_CALIBRATION_GAS_DESCRIPTION_PATTERN.matcher(gasInfo);
                    if ( matcher.matches() )
                        continue;
                    // Doesn't match a pattern, so stick the whole string under the ID
                    id = gasInfo;
                }
            }
            if ( !concStr.isEmpty() ) {
                // Assume the concentration is accurate within one of its last reported digit
                int dotIdx = concStr.indexOf(".");
                if ( (dotIdx >= 0) && (dotIdx + 1 < concStr.length()) ) {
                    accStr = "0.";
                    for (int k = dotIdx + 2; k < concStr.length(); k++) {
                        accStr += "0";
                    }
                    accStr += "1";
                }
                else
                    accStr = "1";
            }
            gasList.add(new CalibrationGas(id, "CO2", supplier, concStr, accStr, useFreq));
        }
        co2Sensor.setCalibrationGases(gasList);
        instruments.add(co2Sensor);

        TemperatureSensor sstSensor = new TemperatureSensor();
        sstSensor.setName("Water Temperature Sensor");
        sstSensor.setManufacturer(getElementText(null, SST_MANUFACTURER_ELEMENT_NAME));
        sstSensor.setModel(getElementText(null, SST_MODEL_ELEMENT_NAME));
        sstSensor.setCalibration(getElementText(null, SST_CALIBRATION_ELEMENT_NAME));
        sstSensor.setAddnInfo(new MultiString(getElementText(null, SST_COMMENTS_ELEMENT_NAME)));
        instruments.add(sstSensor);

        TemperatureSensor teqSensor = new TemperatureSensor();
        teqSensor.setName("Equilibrator Temperature Sensor");
        teqSensor.setManufacturer(getElementText(null, EQT_MANUFACTURER_ELEMENT_NAME));
        teqSensor.setModel(getElementText(null, EQT_MODEL_ELEMENT_NAME));
        teqSensor.setCalibration(getElementText(null, EQT_CALIBRATION_ELEMENT_NAME));
        addnInfo = new MultiString();
        strVal = getElementText(null, EQT_WARMING_ELEMENT_NAME);
        if ( !strVal.isEmpty() )
            addnInfo.append("Warming: " + strVal);
        addnInfo.append(getElementText(null, EQT_COMMENTS_ELEMENT_NAME));
        teqSensor.setAddnInfo(addnInfo);
        instruments.add(teqSensor);

        PressureSensor slpSensor = new PressureSensor();
        slpSensor.setName("Atmospheric Pressure Sensor");
        slpSensor.setManufacturer(getElementText(null, ATM_MANUFACTURER_ELEMENT_NAME));
        slpSensor.setModel(getElementText(null, ATM_MODEL_ELEMENT_NAME));
        slpSensor.setCalibration(getElementText(null, ATM_CALIBRATION_ELEMENT_NAME));
        slpSensor.setAddnInfo(new MultiString(getElementText(null, ATM_COMMENTS_ELEMENT_NAME)));
        instruments.add(slpSensor);

        PressureSensor peqSensor = new PressureSensor();
        peqSensor.setName("Equilibrator Pressure Sensor");
        peqSensor.setManufacturer(getElementText(null, EQP_MANUFACTURER_ELEMENT_NAME));
        peqSensor.setModel(getElementText(null, EQP_MODEL_ELEMENT_NAME));
        peqSensor.setCalibration(getElementText(null, EQP_CALIBRATION_ELEMENT_NAME));
        peqSensor.setAddnInfo(new MultiString(getElementText(null, EQP_COMMENTS_ELEMENT_NAME)));
        instruments.add(peqSensor);

        SalinitySensor salSensor = new SalinitySensor();
        salSensor.setName("Salinity Sensor");
        salSensor.setManufacturer(getElementText(null, SSS_MANUFACTURER_ELEMENT_NAME));
        salSensor.setModel(getElementText(null, SSS_MODEL_ELEMENT_NAME));
        salSensor.setCalibration(getElementText(null, SSS_CALIBRATION_ELEMENT_NAME));
        salSensor.setAddnInfo(new MultiString(getElementText(null, SSS_COMMENTS_ELEMENT_NAME)));
        instruments.add(salSensor);

        int k = 0;
        for (Element elem : getElementList(null, OTHER_SENSORS_ELEMENT_NAME)) {
            k++;
            Analyzer otherSensor = new Analyzer();
            otherSensor.setName("Other Sensor " + k);
            otherSensor.setManufacturer(getElementText(elem, OTHER_SENSORS_MANUFACTURER_ELEMENT_NAME));
            otherSensor.setModel(getElementText(elem, OTHER_SENSORS_MODEL_ELEMENT_NAME));
            otherSensor.setCalibration(getElementText(elem, OTHER_SENSORS_CALIBRATION_ELEMENT_NAME));
            addnInfo = new MultiString();
            strVal = getElementText(elem, OTHER_SENSORS_LOCATION_ELEMENT_NAME);
            if ( !strVal.isEmpty() )
                addnInfo.append("Location: " + strVal);
            strVal = getElementText(elem, OTHER_SENSORS_ACCURACY_ELEMENT_NAME);
            if ( strVal.isEmpty() )
                strVal = getElementText(elem, OTHER_SENSORS_UNCERTAINTY_ELEMENT_NAME);
            if ( !strVal.isEmpty() )
                addnInfo.append("Accuracy/Uncertainty: " + strVal);
            strVal = getElementText(elem, OTHER_SENSORS_PRECISION_ELEMENT_NAME);
            if ( strVal.isEmpty() )
                strVal = getElementText(elem, OTHER_SENSORS_RESOLUTION_ELEMENT_NAME);
            if ( !strVal.isEmpty() )
                addnInfo.append("Precision/Resolution: " + strVal);
            addnInfo.append(getElementText(elem, OTHER_SENSORS_COMMENTS_ELEMENT_NAME));
            otherSensor.setAddnInfo(addnInfo);
            instruments.add(otherSensor);
        }

        return instruments;
    }

    static {
        DEFAULT_KEY_TO_TYPE_MAP = new HashMap<String,VarType>();

        DEFAULT_KEY_TO_TYPE_MAP.put("fco2eq", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equ", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equi", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equil", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equilwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equiuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equiwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equiwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equw", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2equwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2eqwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2eqwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2sweq", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swequ", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swequatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swequi", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swequiuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swequiwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swequiwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swequtempuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swequtuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swequuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swequwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swequwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2sweqwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2sweqwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swteq", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swtequ", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swtequatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swtequi", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swtequiuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swtequiwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swtequiwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swtequuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swtequwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swtequwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swteqwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swteqwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2teq", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2tequ", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2tequatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2tequi", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2tequiuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2tequiwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2tequiwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2tequuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2tequwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2tequwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2teqwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2teqwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watequatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watereq", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterequ", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterequatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterequi", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterequiuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterequiwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterequiwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterequuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterequwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterequwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watereqwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watereqwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterteq", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watertequ", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watertequatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watertequi", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watertequiuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watertequiwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watertequiwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watertequuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watertequwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watertequwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterteqwet", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2waterteqwetuatm", VarType.FCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2wequil", VarType.FCO2_WATER_EQU);

        DEFAULT_KEY_TO_TYPE_MAP.put("co2fsst", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("f1013uatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco21013uatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmpressuresstcorruatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP
                .put("fco2forairinequilibriumwithseawateratseasurfacetemperatureatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2inseawater", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2inseawateruatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2insitu", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2insituwet", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2ocesstuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2recuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2seatsst100humidityatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2seauatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2seawetistempuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2seawettinsitu", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2sst", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2sst100humuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2sstuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2sstwet", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2sstwetuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2sw", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swsatuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swsst", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swsstuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swsstwet", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swsstwetuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swsat", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swuatmuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2uatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2w", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2wat", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watersst", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watersstuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watersstwet", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watersstwetatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watersstwetuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2wateruatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watsstuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2watuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2wetswuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2winsitu", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2wsst100humuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2wsstuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2wuatm", VarType.FCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("fpatm", VarType.FCO2_WATER_SST);      // ??????

        DEFAULT_KEY_TO_TYPE_MAP.put("co2pequ", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2eq", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2equ", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2equatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2equi", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2equiuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2equiwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2equiwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2equuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2equwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2equwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2eqwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2eqwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2sweq", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swequ", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swequatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swequi", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swequiuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swequiwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swequiwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swequuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swequwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swequwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2sweqwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2sweqwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swteq", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swtequ", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swtequatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swtequi", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swtequiuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swtequiwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swtequuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swtequwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swtequwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swteqwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swteqwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2teq", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2tequ", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2tequatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2tequi", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2tequiuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2tequiwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2tequiwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2tequuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2tequwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2tequwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2teqwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2teqwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2uatmfromproco2sn2909745pco2uatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watequatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watereq", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterequ", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterequatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterequi", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterequi100humidity", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterequiuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterequiwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterequiwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterequuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterequwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterequwetatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterequwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watereqwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watereqwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterteq", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watertequ", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watertequatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watertequi", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watertequiuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watertequiwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watertequuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watertequwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watertequwetuatm", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterteqwet", VarType.PCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2waterteqwetuatm", VarType.PCO2_WATER_EQU);

        DEFAULT_KEY_TO_TYPE_MAP.put("1mproco2concuatmsn299745", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("co2", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("co2ppm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("co2psst", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2380742525", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atinsitutempintaket", VarType.PCO2_WATER_SST);
        // DEFAULT_KEY_TO_TYPE_MAP.put("pco2atm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atminwater", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP
                .put("pco2forairinequilibriumwithseawateratseasurfacetemperatureatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2headuatam", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2headuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2icosatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2inseawaterwet", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2inseawaterwetppm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2insituatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2insitutmatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2mol", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2ocesstuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2seatsst100humidityatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2seawetistempuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2sst", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2sst100humuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2sstuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2sstwet", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2sstwetuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2sw", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swsatuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swsst", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swsstuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swsstwet", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swsstwetuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swwet", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2tailuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2uatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2uatminwater", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2wat", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watersst", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watersst100humidityuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watersstuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watersstwet", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watersstwetatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watersstwetuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2wateruatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watsstuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watuamt", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2watuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2wetsst", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2wsst100humuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2wsstuatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("prookconc", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("sami521mpco2", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("seawaterpco2uatm", VarType.PCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("sspco2", VarType.PCO2_WATER_SST);

        DEFAULT_KEY_TO_TYPE_MAP.put("co2umm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("co2x", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("molefractionofco2inairfromequilibratormolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2wuatm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("vco2sw", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("vco2swppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2cal", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2cor", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2dryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2dryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2eq", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2eqdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2eqdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2eqdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2eqmicromolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2eqppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2eqppmppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equ", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equi", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equidry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equidryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equidryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equil", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equildry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equippm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equitempdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equiumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equmolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equmomol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2equumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2inseawaterdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2inseawaterdryuatm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2oceequilumolmol1", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2ppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sea", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2seappm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2seappmdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2seappmv", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2seatteqmolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sw", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sweq", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sweqdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sweqdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sweqdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sweqppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequ", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequi", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequidry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequidryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequidryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequippm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequiumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequmolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swequumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swteq", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swteqdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swteqdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swteqdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swteqppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequ", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequi", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequidry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequidryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequidryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequippm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequiumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequmolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swtequumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2teq", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2teqdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2teqdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2teqdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2teqppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequ", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequi", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequidry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequidryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequidryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequippm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequiumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequmolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequppmv", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2tequumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2um", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2wat", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2wateqppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2wateqppmv", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watereq", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watereqdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watereqdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watereqdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watereqppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequ", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequdrymolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequi", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequidry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequidryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequidryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequippm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequitempdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequiumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequmolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterequumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterteq", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterteqdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterteqdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterteqdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2waterteqppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequ", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequdry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequdryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequdryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequi", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequidry", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequidryppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequidryumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequippm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequiumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequmolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watertequumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watumolmol", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2wppm", VarType.XCO2_WATER_EQU);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2wumolmol", VarType.XCO2_WATER_EQU);

        DEFAULT_KEY_TO_TYPE_MAP.put("co2sw", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("co2xcor", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("eq", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2dryair", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2dryswppm", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put(
                "xco2forairinequilibriumwiththeseawateratseasurfacetemperatureand101325hpaappliedpressureexpressedasmolmolindryair",
                VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2ocesstumolmol1", VarType.XCO2_WATER_SST);
        // DEFAULT_KEY_TO_TYPE_MAP.put("xco2ppm", VarType.XCO2_WATER_SST);
        // DEFAULT_KEY_TO_TYPE_MAP.put("xco2seappm", VarType.XCO2_WATER_SST);
        // DEFAULT_KEY_TO_TYPE_MAP.put("xco2seappmdry", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2seatsstmolmol", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sst", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sstdry", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sstdryppm", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sstdryumolmol", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sstppm", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2sstumolmol", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swdry", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swdryumolmol", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swppmv", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swsst", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swsstdry", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swsstdryppm", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swsstdryumolmol", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swsstppm", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swsstumolmol", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swwet", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2w", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watersst", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watersstdry", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watersstdrymolmol", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watersstdryppm", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watersstdryumolmol", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watersstppm", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watersstumolmol", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watsstumolmol", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watstd", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2watstdppm", VarType.XCO2_WATER_SST);
        DEFAULT_KEY_TO_TYPE_MAP.put("xeq", VarType.XCO2_WATER_SST);

        DEFAULT_KEY_TO_TYPE_MAP.put("fco2a", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2air", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airactual", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airactualuatm", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airatm", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airsat", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airsatuatm", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airuatm", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airwet", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airwetactual", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airwetactualuatm", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airwetuatm", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atm", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmactual", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmactualuatm", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmuatm", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmwet", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmwetactual", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmwetactualuatm", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmwetuatm", VarType.FCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2auatm", VarType.FCO2_ATM_ACTUAL);

        DEFAULT_KEY_TO_TYPE_MAP.put("airfco2", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("airfco2cal", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("co2fatm", VarType.FCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("fco2a", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airinerpuatm", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airinterp", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airinterpuatm", VarType.FCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("fco2airsatuatm", VarType.FCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("fco2airuatm", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airwetinterp", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2airwetinterpuatm", VarType.FCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("fco2atm", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmatm", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atminterp", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atminterpolated", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atminterpolateduatm", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atminterpuatm", VarType.FCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmuatm", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmuatminterpolateduatm", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmwetinterp", VarType.FCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2atmwetinterpuatm", VarType.FCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("fco2auatm", VarType.FCO2_ATM_INTERP);

        DEFAULT_KEY_TO_TYPE_MAP.put("atmpco2", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2air", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airactual", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airactualuatm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airatm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airsatuatm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airsatutam", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airuatm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airwet", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airwetactual", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airwetactualuatm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airwetuatm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atmactual", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atmactualuatm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atmmeasuredintheair", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atmuatm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atmwet", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atmwetactual", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atmwetactualuatm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atmwetuatm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2inairwet", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2inairwetppm", VarType.PCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2uatmmeasuredintheair", VarType.PCO2_ATM_ACTUAL);

        DEFAULT_KEY_TO_TYPE_MAP.put("airpco2", VarType.PCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("airpco2cal", VarType.PCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmosphericpco2ppm", VarType.PCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airinterp", VarType.PCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airinterpuatm", VarType.PCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("pco2airuatm", VarType.PCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airwetinterp", VarType.PCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2airwetinterpuatm", VarType.PCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("pco2atm", VarType.PCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atminterp", VarType.PCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atminterpuatm", VarType.PCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atmwetinterp", VarType.PCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2atmwetinterpuatm", VarType.PCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2auatm", VarType.PCO2_ATM_INTERP);

        DEFAULT_KEY_TO_TYPE_MAP.put("airxco2", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmxco2dryppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2a", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2air", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airactual", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airactualppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airactualumolmol", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airaveppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airdry", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airdryactual", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airdryactualppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airdryactualumolmol", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airdryppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airdryumolmol", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airumolmol", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airwet", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2amicromolmol", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2appm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmactual", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmactualppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmactualumolmol", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmdry", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmdryactual", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmdryactualppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmdryactualumolmol", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmdryppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmdryumolmol", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmppmdry", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmppmmeasured", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmppmppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmppmv", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmumolmol", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2aumolmol", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2dryairmolmol", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2dryairppm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2inairdry", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2inairdryuatm", VarType.XCO2_ATM_ACTUAL);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2indriedairactualumolmol", VarType.XCO2_ATM_ACTUAL);

        DEFAULT_KEY_TO_TYPE_MAP.put("air", VarType.XCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("airxco2", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("airxco2cal", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmxco2dryrunningmeanppm", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("co2atm", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("co2xatm", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xair", VarType.XCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("xco2a", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2ainterpolatedppm", VarType.XCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("xco2air", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airdryppmv", VarType.XCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("xco2airdryumolmol", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airint", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airinterp", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airinterpppm", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airinterpumolmol", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airintppm", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airintumolmol", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2airmolmol", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmassigned", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmdryinterp", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmdryinterpumolmol", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmdryppmv", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atminterp", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atminterpolated", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atminterpolatedppm", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atminterpppm", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atminterpppmdry", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atminterpumolmol", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmintumolmol1", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmppminterp", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmppminterpolatedppm", VarType.XCO2_ATM_INTERP);
        // DEFAULT_KEY_TO_TYPE_MAP.put("xco2atmppmv", VarType.XCO2_ATM_INTERP);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2indryairppmv", VarType.XCO2_ATM_INTERP);

        DEFAULT_KEY_TO_TYPE_MAP.put("intaketemperature", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("intaketemperaturecelsius", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("istempdegc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sami521mtemp", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sbo37temp", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("seasurfacetemperature", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("seasurfacetemperaturec", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("seasurfacetemperaturedegc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("seasurfacetemperaturedegrc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("seasurfacetemperatureinsituc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("seatempdegc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("shiptempc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sst", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sst38", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sstc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sstcal", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sstdegc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sstdegreesc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sstk", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sstoc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sstsw", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("ssttsg", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("ssttsgc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("swtemperaturec", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tdegc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temp", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempdegc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temperature", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temperatureatseawaterintakeoc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temperaturec", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temperaturefrommicrocatat1mtemperature", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempheadoc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempintake", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempoc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempseawaterdegreesc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempsst", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temptailoc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tinsitu", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tinsituc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tinsitudegc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tintake", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tmpfb", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tmpfb11163", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tmpwatdegc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tsea", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tstc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("watertemp", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("watertempdegc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("watertempdegreesc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("watertemperature", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("watertemperaturedegc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("waterttempc", VarType.SEA_SURFACE_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("waterttemperaturec", VarType.SEA_SURFACE_TEMPERATURE);

        DEFAULT_KEY_TO_TYPE_MAP.put("eqt", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqtemp", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqtempc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqtempcal", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqtempdegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqtmp", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqtmpdegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilibratortemperature", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilibratortemperaturec", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilibratortemperaturedegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilibratortemperaturedegrc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilitc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilt", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equiltemp", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equiltempdegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equitempdegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equtemp", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equtempdegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equtempsw", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("licortemp", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2tmp", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempeq", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempeqc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempeqdegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempeqdegreesc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempequ", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempequc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempequdegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempequi", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempequic", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempequidegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempequil", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tempequoc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temperatureatpco2equilibratoroc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temperatureequi", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temperatureequic", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temperatureequidegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temperatureofequilibration", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("temperatureofequilibrationdegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("teq", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("teqc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("teqdegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("teqic", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("teqoc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tequ", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tequc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tequdegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tequdegreesc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tequi", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tequic", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tequidegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tequil", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tequilc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tequildegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tmpeq", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tmpeqdegc", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("tsgtemp", VarType.EQUILIBRATOR_TEMPERATURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("ttechc", VarType.EQUILIBRATOR_TEMPERATURE);

        DEFAULT_KEY_TO_TYPE_MAP.put("airp", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("airpress", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("airpresshpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("airpressure", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("airpressurehpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("airpressurekpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("airpressurembar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("airpressuremmhg", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmosphericpressure", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmosphericpressurehpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmosphericpressurekpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmosphericpressuremb", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmosrmpresscal", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmosrmpresscalhpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmpre", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmpredbar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmprehpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmprembar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmprepatm", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmpres", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmpresmbar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmpress", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmpressmbar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmpressurehpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmpressurembar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmprs", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("atmprshpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("barometricpressurehpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("barometricpressurembar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("bpratm", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pair", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pairhpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pairkpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pairmbar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pairmmhg", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pam", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("patm", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("patmhpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("patmkpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("patmmb", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("patmmbar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("patmmmhg", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pppp", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pppphpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("ppppkpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("ppppmbar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("ppppmmhg", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pres", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presair", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presairhpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presairkpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presairmbar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presairmmhg", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presatm", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presatmhpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presatmkpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presatmmbar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presatmmmhg", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presatmssp", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presatmssphpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("preskpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presmb", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressatm", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressatmhpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressealevelhpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressmbar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presssealevelhpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressure", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressureatm", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressureatmhpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressureatmkpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressureatmmbar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressureatmmhg", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressurembar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("prsatm", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("prsatmhpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sealevelpressure", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sealevelpressurehpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sealevelpressurekpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sealevelpressurembar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("sealevelpressuremmhg", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("shipatmpresshpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("slp", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("slphpa", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("slpmbar", VarType.SEA_LEVEL_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("slpmmhg", VarType.SEA_LEVEL_PRESSURE);

        DEFAULT_KEY_TO_TYPE_MAP.put("atmosphericpressureatpco2systemmb", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("baropress", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqp", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqphpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqpre", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqprehpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqprembar", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqpress", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("eqpresshpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilibratorpressure", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilibratorpressurehpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilibratorpressurekpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilibratorpressurembar", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilibratorpressuremmhg", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilpres", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilpress", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilpresshpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equilpressurehpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equipressmbar", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equpresmbar", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equpress", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equpresssw", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("equpressure", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("licoratmpressure", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("licoratmpressurehpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("peq", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("peqhpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("peqkpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("peqmbar", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("peqmmhg", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pequ", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pequhpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pequi", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pequihpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pequikpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pequilib", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pequimbar", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pequimmhg", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pequkpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pequmbar", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("peqummhg", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("phpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("preseq", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("preseqhpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("preseqkpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("preseqmbar", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("preseqmmhg", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presequ", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presequhpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presequi", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presequihpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presequikpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presequilhpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presequimbar", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presequimmhg", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presequkpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presequmbar", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("preseqummhg", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("preslabhpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("preslicorhpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("presseq", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressequhpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressequilhpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressureequi", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressureequil", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("pressureofequilibrationhpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP
                .put("pressureofequilibrationthepressureintheequilibrationvesselhpa", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("prseq", VarType.EQUILIBRATOR_PRESSURE);
        DEFAULT_KEY_TO_TYPE_MAP.put("prseqhpa", VarType.EQUILIBRATOR_PRESSURE);

        DEFAULT_KEY_TO_TYPE_MAP.put("issal", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("psusw", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("sal", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salfb", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salfb35395", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinity", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinityatseawaterintake", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinityfrommicrocatat1msalinity", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinityperm", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinitypermil", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinityppt", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinitypss", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinitypsu", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinityqualityflagsalquality", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinitysource", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinitywoceflag", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salinsitupsu", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salperm", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salpermil", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salpsu", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("saltsg", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("saltsgpermil", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salttsg", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("salverified", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("sdsal", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("seasurfacesalinityinsitu", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("shipsalpsu", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("sinsitu", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("ssea", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("sss", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("sssnu", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("sssperm", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("ssspermil", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("ssspss", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("ssspss78", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("ssspsu", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("ssstsg", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("tsgsal", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("tsgsalcorr", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("tsgsalt", VarType.SALINITY);
        DEFAULT_KEY_TO_TYPE_MAP.put("tssal", VarType.SALINITY);

        DEFAULT_KEY_TO_TYPE_MAP.put("co2fwoceflag", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("co2swqf", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2flag", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2qcflag", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("fco2swqf", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("flag", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("pco2swqf", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qc", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qcco2aq", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qcco2sw", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qcco2water", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qcflag", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qcflagw", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qcwater", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qfco2sw", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qffco2sw", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qfpco2sw", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qfxco2", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("qfxco2sw", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("woceco2", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("woceco2aq", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("woceco2sw", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("woceco2water", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("woceflag", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("woceflags", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("woceqcflag", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("wocewater", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2dryswwoceflag", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2flag", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swqf", VarType.WOCE_CO2_WATER);
        DEFAULT_KEY_TO_TYPE_MAP.put("xco2swqfint", VarType.WOCE_CO2_WATER);

        DEFAULT_KEY_TO_TYPE_MAP.put("woceco2atm", VarType.WOCE_CO2_ATM);
        DEFAULT_KEY_TO_TYPE_MAP.put("qcair", VarType.WOCE_CO2_ATM);
        DEFAULT_KEY_TO_TYPE_MAP.put("qcco2air", VarType.WOCE_CO2_ATM);
        DEFAULT_KEY_TO_TYPE_MAP.put("qcco2atm", VarType.WOCE_CO2_ATM);
        DEFAULT_KEY_TO_TYPE_MAP.put("qcflagair", VarType.WOCE_CO2_ATM);
    }

}
