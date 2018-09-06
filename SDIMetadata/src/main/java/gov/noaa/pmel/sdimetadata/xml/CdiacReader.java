package gov.noaa.pmel.sdimetadata.xml;

import gov.noaa.pmel.sdimetadata.SDIMetadata;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class CdiacReader {

    private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("yyyyMMdd");

    static {
        DATE_PARSER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static final String ROOT_ELEMENT_NAME = "x_tags";

    // Element final names for User as well as the Investigator
    private static final String NAME_ELEMENT_NAME = "Name";
    private static final String ORGANIZATION_ELEMENT_NAME = "Organization";
    private static final String ADDRESS_ELEMENT_NAME = "Address";
    private static final String PHONE_ELEMENT_NAME = "Phone";
    private static final String EMAIL_ELEMENT_NAME = "Email";

    // Element names for variables
    private static final String VARIABLES_NAME_ELEMENT_NAME = "Variable_Name";
    private static final String VARIABLES_DESCRIPTION_ELEMENT_NAME = "Description_of_Variable";
    private static final String VARIABLES_UNIT_ELEMENT_NAME = "Unit";
    private static final String VARIABLES_UNIT_OF_VARIABLE_ELEMENT_NAME = "Unit_of_Variable";

    // Element names for the sensors
    private static final String LOCATION_ELEMENT_NAME = "Location";
    private static final String MANUFACTURER_ELEMENT_NAME = "Manufacturer";
    private static final String MODEL_ELEMENT_NAME = "Model";
    private static final String ACCURACY_ELEMENT_NAME = "Accuracy";
    private static final String UNCERTAINTY_ELEMENT_NAME = "Uncertainty";
    private static final String PRECISION_ELEMENT_NAME = "Precision";
    private static final String RESOLUTION_ELEMENT_NAME = "Resolution";
    private static final String CALIBRATION_ELEMENT_NAME = "Calibration";
    private static final String COMMENTS_ELEMENT_NAME = "Other_Comments";

    private static final String USER_ELEMENT_NAME = "User";
    private static final String USER_NAME_ELEMENT_NAME = USER_ELEMENT_NAME + "\t" + NAME_ELEMENT_NAME;
    private static final String USER_ORGANIZATION_ELEMENT_NAME = USER_ELEMENT_NAME + "\t" + ORGANIZATION_ELEMENT_NAME;
    private static final String USER_ADDRESS_ELEMENT_NAME = USER_ELEMENT_NAME + "\t" + ADDRESS_ELEMENT_NAME;
    private static final String USER_PHONE_ELEMENT_NAME = USER_ELEMENT_NAME + "\t" + PHONE_ELEMENT_NAME;
    private static final String USER_EMAIL_ELEMENT_NAME = USER_ELEMENT_NAME + "\t" + EMAIL_ELEMENT_NAME;

    // root element contains multiple <Investigator> elements, each containing <Name>, <Organization>, <Address>, <Phone>, and <Email>
    private static final String INVESTIGATOR_ELEMENT_NAME = "Investigator";

    private static final String DATASET_INFO_ELEMENT_NAME = "Dataset_Info";
    private static final String FUNDING_INFO_ELEMENT_NAME = DATASET_INFO_ELEMENT_NAME + "\t" + "Funding_Info";

    private static final String SUBMISSION_DATES_ELEMENT_NAME = DATASET_INFO_ELEMENT_NAME + "\t" + "Submission_Dates";
    private static final String INITIAL_SUBMISSION_ELEMENT_NAME = SUBMISSION_DATES_ELEMENT_NAME + "\t" + "Initial_Submission";
    private static final String REVISED_SUBMISSION_ELEMENT_NAME = SUBMISSION_DATES_ELEMENT_NAME + "\t" + "Revised_Submission";

    private static final String CRUISE_INFO_ELEMENT_NAME = "Cruise_Info";
    private static final String EXPERIMENT_ELEMENT_NAME = CRUISE_INFO_ELEMENT_NAME + "\t" + "Experiment";
    private static final String EXPERIMENT_NAME_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + "\t" + "Experiment_Name";
    private static final String EXPERIMENT_TYPE_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + "\t" + "Experiment_Type";
    private static final String PLATFORM_TYPE_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + "\t" + "Platform_Type";
    private static final String CO2_INSTRUMENT_TYPE_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + "\t" + "Co2_Instrument_type";
    private static final String MOORING_ID_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + "\t" + "Mooring_ID";

    private static final String CRUISE_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + "\t" + "Cruise";
    private static final String EXPOCODE_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Expocode";
    private static final String CRUISE_ID_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Cruise_ID";
    private static final String SUB_CRUISE_INFO_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Cruise_Info";
    private static final String SECTION_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Section";

    private static final String GEO_COVERAGE_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Geographical_Coverage";
    private static final String GEO_REGION_ELEMENT_NAME = GEO_COVERAGE_ELEMENT_NAME + "\t" + "Geographical_Region";
    private static final String BOUNDS_ELEMENT_NAME = GEO_COVERAGE_ELEMENT_NAME + "\t" + "Bounds";
    private static final String WEST_BOUND_ELEMENT_NAME = BOUNDS_ELEMENT_NAME + "\t" + "Westernmost_Longitude";
    private static final String EAST_BOUND_ELEMENT_NAME = BOUNDS_ELEMENT_NAME + "\t" + "Easternmost_Longitude";
    private static final String NORTH_BOUND_ELEMENT_NAME = BOUNDS_ELEMENT_NAME + "\t" + "Northernmost_Latitude";
    private static final String SOUTH_BOUND_ELEMENT_NAME = BOUNDS_ELEMENT_NAME + "\t" + "Southernmost_Latitude";

    private static final String TEMP_COVERAGE_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Temporal_Coverage";
    private static final String TEMP_START_DATE_ELEMENT_NAME = TEMP_COVERAGE_ELEMENT_NAME + "\t" + "Start_Date";
    private static final String TEMP_END_DATE_ELEMENT_NAME = TEMP_COVERAGE_ELEMENT_NAME + "\t" + "End_Date";
    private static final String START_DATE_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Start_Date";
    private static final String END_DATE_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "End_Date";

    private static final String VESSEL_ELEMENT_NAME = CRUISE_INFO_ELEMENT_NAME + "\t" + "Vessel";
    private static final String VESSEL_NAME_ELEMENT_NAME = VESSEL_ELEMENT_NAME + "\t" + "Vessel_Name";
    private static final String VESSEL_ID_ELEMENT_NAME = VESSEL_ELEMENT_NAME + "\t" + "Vessel_ID";
    private static final String COUNTRY_ELEMENT_NAME = VESSEL_ELEMENT_NAME + "\t" + "Country";
    private static final String OWNER_ELEMENT_NAME = VESSEL_ELEMENT_NAME + "\t" + "Vessel_Owner";

    private static final String VARIABLES_INFO_ELEMENT_NAME = "Variables_Info";
    // <Variables_Info> element contains multiple <Variable> elements, each containing <Variable_Name>, <Description_of_Variable>,
    // and possibly <Unit> or <Unit_of_Variable> (or unit of variable may be part of the description)
    private static final String VARIABLE_ELEMENT_NAME = "Variable";


    private static final String METHOD_DESCRIPTION_ELEMENT_NAME = "Method_Description";
    private static final String EQUILIBRATOR_DESIGN_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Equilibrator_Design";
    private static final String INTAKE_DEPTH_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + "\t" + "Depth_of_Sea_Water_Intake";
    private static final String INTAKE_LOCATION_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + "\t" + "Location_of_Sea_Water_Intake";
    private static final String EQUI_TYPE_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + "\t" + "Equilibrator_Type";
    private static final String EQUI_VOLUME_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + "\t" + "Equilibrator_Volume";
    private static final String WATER_FLOW_RATE_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + "\t" + "Water_Flow_Rate";
    private static final String GAS_FLOW_RATE_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + "\t" + "Headspace_Gas_Flow_Rate";
    private static final String VENTED_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + "\t" + "Vented";
    private static final String DRYING_METHOD_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + "\t" + "Drying_Method_for_CO2_in_water";
    private static final String EQUI_ADDITIONAL_INFO_ELEMENT_NAME = EQUILIBRATOR_DESIGN_ELEMENT_NAME + "\t" + "Additional_Information";

    private static final String CO2_MARINE_AIR_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "CO2_in_Marine_Air";
    private static final String MARINE_AIR_MEASUREMENT_ELEMENT_NAME = CO2_MARINE_AIR_ELEMENT_NAME + "\t" + "Measurement";
    private static final String MARINE_AIR_LOCATION_ELEMENT_NAME = CO2_MARINE_AIR_ELEMENT_NAME + "\t" + "Location_and_Height";
    private static final String MARINE_AIR_DRYING_ELEMENT_NAME = CO2_MARINE_AIR_ELEMENT_NAME + "\t" + "Drying_Method";

    private static final String CO2_SENSORS_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "CO2_Sensors";
    private static final String CO2_SENSOR_ELEMENT_NAME = CO2_SENSORS_ELEMENT_NAME + "\t" + "CO2_Sensor";
    private static final String CO2_MEASUREMENT_METHOD_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Measurement_Method";
    private static final String CO2_MANUFACTURER_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String CO2_MODEL_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
    private static final String CO2_FREQUENCY_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Frequency";
    private static final String CO2_WATER_RES_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Resolution_Water";
    private static final String CO2_WATER_UNC_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Uncertainty_Water";
    private static final String CO2_AIR_RES_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Resolution_Air";
    private static final String CO2_AIR_UNC_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Uncertainty_Air";
    private static final String CO2_CALIBRATION_MANUFACTURER_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Manufacturer_of_Calibration_Gas";
    private static final String CO2_SENSOR_CALIBRATION_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "CO2_Sensor_Calibration";
    private static final String ENVIRONMENTAL_CONTROL_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Environmental_Control";
    private static final String METHOD_REFS_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Method_References";
    private static final String DETAILS_OF_CO2_SENSING_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Details_Co2_Sensing";
    private static final String ANALYSIS_OF_COMPARISON_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Analysis_of_Co2_Comparision";
    private static final String MEASURED_CO2_PARAMS_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Measured_Co2_Params";

    private static final String SST_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Sea_Surface_Temperature";
    private static final String SST_LOCATION_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + LOCATION_ELEMENT_NAME;
    private static final String SST_MANUFACTURER_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String SST_MODEL_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
    private static final String SST_ACCURACY_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + ACCURACY_ELEMENT_NAME;
    private static final String SST_PRECISION_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + PRECISION_ELEMENT_NAME;
    private static final String SST_CALIBRATION_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + CALIBRATION_ELEMENT_NAME;
    private static final String SST_COMMENTS_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + COMMENTS_ELEMENT_NAME;

    private static final String EQU_TEMP_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Equilibrator_Temperature";
    private static final String EQT_LOCATION_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + LOCATION_ELEMENT_NAME;
    private static final String EQT_MANUFACTURER_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String EQT_MODEL_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
    private static final String EQT_ACCURACY_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + ACCURACY_ELEMENT_NAME;
    private static final String EQT_PRECISION_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + PRECISION_ELEMENT_NAME;
    private static final String EQT_CALIBRATION_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + CALIBRATION_ELEMENT_NAME;
    private static final String EQT_WARMING_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + "Warming";
    private static final String EQT_COMMENTS_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + COMMENTS_ELEMENT_NAME;

    private static final String EQU_PRESSURE_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Equilibrator_Pressure";
    private static final String EQP_LOCATION_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + LOCATION_ELEMENT_NAME;
    private static final String EQP_MANUFACTURER_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String EQP_MODEL_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
    private static final String EQP_ACCURACY_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + ACCURACY_ELEMENT_NAME;
    private static final String EQP_PRECISION_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + PRECISION_ELEMENT_NAME;
    private static final String EQP_CALIBRATION_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + CALIBRATION_ELEMENT_NAME;
    private static final String EQP_COMMENTS_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + COMMENTS_ELEMENT_NAME;
    private static final String EQP_NORMALIZED_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + "Normalized";

    private static final String ATM_PRESSURE_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Atmospheric_Pressure";
    private static final String ATM_LOCATION_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + LOCATION_ELEMENT_NAME;
    private static final String ATM_MANUFACTURER_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String ATM_MODEL_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
    private static final String ATM_ACCURACY_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + ACCURACY_ELEMENT_NAME;
    private static final String ATM_PRECISION_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + PRECISION_ELEMENT_NAME;
    private static final String ATM_CALIBRATION_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + CALIBRATION_ELEMENT_NAME;
    private static final String ATM_COMMENTS_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + COMMENTS_ELEMENT_NAME;

    private static final String SSS_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Sea_Surface_Salinity";
    private static final String SSS_LOCATION_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + LOCATION_ELEMENT_NAME;
    private static final String SSS_MANUFACTURER_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String SSS_MODEL_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
    private static final String SSS_ACCURACY_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + ACCURACY_ELEMENT_NAME;
    private static final String SSS_PRECISION_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + PRECISION_ELEMENT_NAME;
    private static final String SSS_CALIBRATION_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + CALIBRATION_ELEMENT_NAME;
    private static final String SSS_COMMENTS_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + COMMENTS_ELEMENT_NAME;

    private static final String OTHER_SENSORS_ELEMENT_NAME = "Other_Sensors";
    // <Other_Sensors> contains mutliple <Sensor> elements, each with with <Manufacturer>, <Model>, <Accuracy> or <Uncertainty>,
    // <Precision> or <Resolution>, <Calibration>, and <Other_Comments> elements
    private static final String SENSOR_ELEMENT_NAME = "Sensor";

    private static final String DATA_SET_REFS_ELEMENT_NAME = "Data_set_References";
    private static final String ADD_INFO_ELEMENT_NAME = "Additional_Information";
    private static final String CITATION_ELEMENT_NAME = "Citation";
    private static final String MEAS_CALIB_REPORT_ELEMENT_NAME = "Measurement_and_Calibration_Report";
    private static final String PRELIM_QC_ELEMENT_NAME = "Preliminary_Quality_control";

    private static final String FORM_TYPE_ELEMENT_NAME = "form_type";


    private Document omeDoc;
    private Element rootElement;

    /**
     * Read the XML contents of the given file.
     *
     * @param xmlfile
     *         read the XML in this file
     *
     * @throws IllegalArgumentException
     *         if there is a problem reading the file or its XML contents
     */
    public CdiacReader(File xmlfile) throws IllegalArgumentException {
        try {
            omeDoc = (new SAXBuilder()).build(xmlfile);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems interpreting the XML contents in: " + xmlfile.getPath() +
                    "\n    " + ex.getMessage());
        }
        rootElement = omeDoc.getRootElement();
        if ( rootElement == null )
            throw new IllegalArgumentException("No root element found in: " + xmlfile.getPath());

    }

    private String getElementText(String fullElementName) throws IllegalArgumentException {
        Element elem = rootElement;
        for (String name : fullElementName.split("\t")) {
            elem = elem.getChild(name);
            if ( elem == null )
                throw new IllegalArgumentException(
                        name + " element not found in " + fullElementName.replaceAll("\t", "->"));
        }
        return elem.getTextTrim();
    }

    public SDIMetadata createSDIMetadata() throws IllegalArgumentException {
        SDIMetadata mdata = new SDIMetadata();
        Element rootElem = omeDoc.getRootElement();

        // First get the Cruise ID / Expocode
        String expocode;
        try {
            expocode = getElementText(EXPOCODE_ELEMENT_NAME);
        } catch ( IllegalArgumentException ex ) {
            expocode = null;
        }
        if ( null == expocode ) {
            try {
                expocode = getElementText(CRUISE_ID_ELEMENT_NAME);
            } catch ( IllegalArgumentException ex ) {
                // Leave expocode as null
            }
        }
        if ( null == expocode )
            throw new IllegalArgumentException("Dataset expocode not found under " +
                    EXPOCODE_ELEMENT_NAME.replaceAll("\t", "->") + " or " +
                    CRUISE_ID_ELEMENT_NAME.replaceAll("\t", "->"));


        return mdata;
    }

}
