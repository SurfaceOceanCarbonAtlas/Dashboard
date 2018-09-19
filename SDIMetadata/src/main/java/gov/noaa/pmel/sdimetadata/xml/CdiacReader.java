package gov.noaa.pmel.sdimetadata.xml;

import gov.noaa.pmel.sdimetadata.Coverage;
import gov.noaa.pmel.sdimetadata.MiscInfo;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.instrument.Analyzer;
import gov.noaa.pmel.sdimetadata.instrument.CalibrationGas;
import gov.noaa.pmel.sdimetadata.instrument.Equilibrator;
import gov.noaa.pmel.sdimetadata.instrument.GasSensor;
import gov.noaa.pmel.sdimetadata.instrument.PressureSensor;
import gov.noaa.pmel.sdimetadata.instrument.SalinitySensor;
import gov.noaa.pmel.sdimetadata.instrument.Sampler;
import gov.noaa.pmel.sdimetadata.instrument.TemperatureSensor;
import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import gov.noaa.pmel.sdimetadata.platform.Platform;
import gov.noaa.pmel.sdimetadata.platform.PlatformType;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
import gov.noaa.pmel.sdimetadata.util.NumericString;
import gov.noaa.pmel.sdimetadata.variable.AirPressure;
import gov.noaa.pmel.sdimetadata.variable.AquGasConc;
import gov.noaa.pmel.sdimetadata.variable.DataVar;
import gov.noaa.pmel.sdimetadata.variable.GasConc;
import gov.noaa.pmel.sdimetadata.variable.MethodType;
import gov.noaa.pmel.sdimetadata.variable.Temperature;
import gov.noaa.pmel.sdimetadata.variable.VarType;
import gov.noaa.pmel.sdimetadata.variable.Variable;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

public class CdiacReader extends DocumentHandler {

    // Element final names for User as well as the Investigator
    private static final String NAME_ELEMENT_NAME = "Name";
    private static final String ORGANIZATION_ELEMENT_NAME = "Organization";
    private static final String ADDRESS_ELEMENT_NAME = "Address";
    private static final String PHONE_ELEMENT_NAME = "Phone";
    private static final String EMAIL_ELEMENT_NAME = "Email";

    private static final String USER_ELEMENT_NAME = "User";
    private static final String USER_NAME_ELEMENT_NAME = USER_ELEMENT_NAME + SEP + NAME_ELEMENT_NAME;
    private static final String USER_ORGANIZATION_ELEMENT_NAME = USER_ELEMENT_NAME + SEP + ORGANIZATION_ELEMENT_NAME;
    private static final String USER_ADDRESS_ELEMENT_NAME = USER_ELEMENT_NAME + SEP + ADDRESS_ELEMENT_NAME;
    private static final String USER_PHONE_ELEMENT_NAME = USER_ELEMENT_NAME + SEP + PHONE_ELEMENT_NAME;
    private static final String USER_EMAIL_ELEMENT_NAME = USER_ELEMENT_NAME + SEP + EMAIL_ELEMENT_NAME;

    // root element contains multiple <Investigator> elements, each containing <Name>, <Organization>, <Address>, <Phone>, and <Email>
    private static final String INVESTIGATOR_ELEMENT_NAME = "Investigator";

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
    // <Variables_Info> element contains multiple <Variable> elements, each containing <Variable_Name>, <Description_of_Variable>,
    // and possibly <Unit_of_Variable> (or unit of variable may be part of the description)
    private static final String VARIABLE_ELEMENT_NAME = VARIABLES_INFO_ELEMENT_NAME + SEP + "Variable";
    private static final String VARIABLES_NAME_ELEMENT_NAME = "Variable_Name";
    private static final String VARIABLES_DESCRIPTION_ELEMENT_NAME = "Description_of_Variable";
    private static final String VARIABLES_UNIT_OF_VARIABLE_ELEMENT_NAME = "Unit_of_Variable";


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

    // Element names for the sensors
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
    // <Other_Sensors> contains multiple <Sensor> elements, each with with <Manufacturer>, <Model>, <Accuracy> or <Uncertainty>,
    // <Precision> or <Resolution>, <Calibration>, and <Other_Comments> elements

    private static final String DATA_SET_REFS_ELEMENT_NAME = "Data_set_References";
    private static final String ADDN_INFO_ELEMENT_NAME = "Additional_Information";
    private static final String CITATION_ELEMENT_NAME = "Citation";

    private static final String DATA_SET_LINK_ELEMENT_NAME = "Data_Set_Link";
    private static final String DATA_SET_LINK_URL_ELEMENT_NAME = DATA_SET_LINK_ELEMENT_NAME + SEP + "URL";
    private static final String DATA_SET_LINK_NOTE_ELEMENT_NAME = DATA_SET_LINK_ELEMENT_NAME + SEP + "Link_Note";

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
    }

    /**
     * @return an SDIMetadata object populated with information found in this CDIAC XML file; never null
     */
    public SDIMetadata createSDIMetadata() {
        SDIMetadata mdata = new SDIMetadata();
        MiscInfo misc = getMiscInfo();
        mdata.setMiscInfo(misc);
        mdata.setSubmitter(getSubmitter());
        mdata.setInvestigators(getInvestigators());
        Platform platform = getPlatform(misc.getDatasetId());
        mdata.setPlatform(platform);
        mdata.setCoverage(getCoverage());
        mdata.setVariables(getVariables(platform.getPlatformType()));
        mdata.setSamplers(getSamplers());
        mdata.setAnalyzers(getAnalyzers());
        return mdata;
    }

    /**
     * @return the miscellaneous information read from this CDIAC XML file; never null
     */
    private MiscInfo getMiscInfo() {
        MiscInfo info = new MiscInfo();

        // Dataset ID / Expocode
        String expocode = getElementText(EXPOCODE_ELEMENT_NAME);
        if ( expocode.isEmpty() )
            expocode = getElementText(CRUISE_ID_ELEMENT_NAME);
        info.setDatasetId(expocode);

        info.setDatasetName(getElementText(EXPERIMENT_NAME_ELEMENT_NAME));
        info.setSectionName(getElementText(SECTION_ELEMENT_NAME));

        // Funding information all glummed together in CDIAC XML - stick under agency name
        info.setFundingAgency(getElementText(FUNDING_INFO_ELEMENT_NAME));

        ArrayList<Datestamp> history = new ArrayList<Datestamp>();
        Datestamp stamp = getDatestamp(getElementText(INITIAL_SUBMISSION_ELEMENT_NAME));
        if ( stamp != null )
            history.add(stamp);
        for (Element elem : getElementList(REVISED_SUBMISSION_ELEMENT_NAME)) {
            stamp = getDatestamp(elem.getTextTrim());
            if ( (stamp != null) && !history.contains(stamp) )
                history.add(stamp);
        }
        info.setHistory(history);

        info.setStartDatestamp(getDatestamp(getElementText(START_DATE_ELEMENT_NAME)));
        info.setEndDatestamp(getDatestamp(getElementText(END_DATE_ELEMENT_NAME)));

        ArrayList<String> portsOfCall = new ArrayList<String>();
        for (Element portElem : getElementList(PORT_OF_CALL_ELEMENT_NAME)) {
            String port = portElem.getTextTrim();
            if ( !port.isEmpty() )
                portsOfCall.add(port);
        }
        info.setPortsOfCall(portsOfCall);

        info.setReferences(getListOfLines(getElementText(DATA_SET_REFS_ELEMENT_NAME)));
        info.setCitation(getElementText(CITATION_ELEMENT_NAME));
        info.setWebsite(getElementText(DATA_SET_LINK_URL_ELEMENT_NAME));

        ArrayList<String> addnInfo = getListOfLines(getElementText(ADDN_INFO_ELEMENT_NAME));
        String text;
        text = getElementText(DATA_SET_LINK_NOTE_ELEMENT_NAME);
        if ( !text.isEmpty() )
            addnInfo.add(0, "Website Note: " + text);
        text = getElementText(MOORING_ID_ELEMENT_NAME);
        if ( !text.isEmpty() )
            addnInfo.add(0, "Mooring ID: " + text);
        text = getElementText(SUB_CRUISE_INFO_ELEMENT_NAME);
        if ( !text.isEmpty() )
            addnInfo.add(0, "Cruise Info: " + text);
        text = getElementText(EXPERIMENT_TYPE_ELEMENT_NAME);
        if ( !text.isEmpty() )
            addnInfo.add(0, "Experiment Type: " + text);
        info.setAddnInfo(addnInfo);

        return info;
    }

    /**
     * @return information about the metadata/dataset submitter read from this CDIAC XML file; never null
     */
    private Submitter getSubmitter() {
        Submitter submitter = new Submitter(getPersonNames(getElementText(USER_NAME_ELEMENT_NAME)));
        submitter.setStreets(getListOfLines(getElementText(USER_ADDRESS_ELEMENT_NAME)));
        // CDIAC XML does not separate streets, city, region, zip, country
        submitter.setOrganization(getElementText(USER_ORGANIZATION_ELEMENT_NAME));
        submitter.setPhone(getElementText(USER_PHONE_ELEMENT_NAME));
        submitter.setEmail(getElementText(USER_EMAIL_ELEMENT_NAME));
        // CDIAC XML does not have the ID or ID type
        return submitter;
    }

    /**
     * @return information about the investigators read from this CDIAC XML file; never null
     */
    private ArrayList<Investigator> getInvestigators() {
        ArrayList<Investigator> piList = new ArrayList<Investigator>();
        for (Element inv : getElementList(INVESTIGATOR_ELEMENT_NAME)) {
            Investigator pi = new Investigator(getPersonNames(inv.getChildTextTrim(NAME_ELEMENT_NAME)));
            pi.setStreets(getListOfLines(inv.getChildTextTrim(ADDRESS_ELEMENT_NAME)));
            // CDIAC XML does not separate streets, city, region, zip, country
            pi.setOrganization(inv.getChildTextTrim(ORGANIZATION_ELEMENT_NAME));
            pi.setPhone(inv.getChildTextTrim(PHONE_ELEMENT_NAME));
            pi.setEmail(inv.getChildTextTrim(EMAIL_ELEMENT_NAME));
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
        platform.setPlatformId(getElementText(VESSEL_ID_ELEMENT_NAME));
        String name = getElementText(VESSEL_NAME_ELEMENT_NAME);
        platform.setPlatformName(name);
        PlatformType type = PlatformType.parse(getElementText(PLATFORM_TYPE_ELEMENT_NAME));
        if ( PlatformType.UNKNOWN.equals(type) )
            type = guessPlatformType(name, datasetId);
        platform.setPlatformType(type);
        platform.setPlatformOwner(getElementText(VESSEL_OWNER_ELEMENT_NAME));
        platform.setPlatformCountry(getElementText(VESSEL_COUNTRY_ELEMENT_NAME));
        return platform;
    }

    /**
     * @return information about the data coverage; never null.  Data time limits are the earliest day
     *         of the start date and latest day of the end date, and so are not more accurate than the day.
     */
    private Coverage getCoverage() {
        Coverage coverage = new Coverage();

        coverage.setWesternLongitude(
                getNumericString(getElementText(WEST_BOUND_ELEMENT_NAME), Coverage.LONGITUDE_UNITS));
        coverage.setEasternLongitude(
                getNumericString(getElementText(EAST_BOUND_ELEMENT_NAME), Coverage.LONGITUDE_UNITS));
        coverage.setSouthernLatitude(
                getNumericString(getElementText(SOUTH_BOUND_ELEMENT_NAME), Coverage.LATITUDE_UNITS));
        coverage.setNorthernLatitude(
                getNumericString(getElementText(NORTH_BOUND_ELEMENT_NAME), Coverage.LATITUDE_UNITS));

        // CDIAC only has date stamps - use earliest and latest time of those days; should be reset from data
        coverage.setEarliestDataTime(getDatestamp(getElementText(TEMP_START_DATE_ELEMENT_NAME)).getEarliestTime());
        Date endDate = getDatestamp(getElementText(TEMP_END_DATE_ELEMENT_NAME)).getEarliestTime();
        endDate = new Date(endDate.getTime() + 24L * 60L * 60L * 1000L - 1000L);
        coverage.setLatestDataTime(endDate);

        TreeSet<String> regions = new TreeSet<String>();
        for (Element regElem : getElementList(GEO_REGION_ELEMENT_NAME)) {
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
        for (Element varElem : getElementList(VARIABLE_ELEMENT_NAME)) {
            Variable var = new Variable();
            String colName = varElem.getChildTextTrim(VARIABLES_NAME_ELEMENT_NAME);
            var.setColName(colName);
            var.setFullName(varElem.getChildTextTrim(VARIABLES_DESCRIPTION_ELEMENT_NAME));
            var.setVarUnit(varElem.getChildTextTrim(VARIABLES_UNIT_OF_VARIABLE_ELEMENT_NAME));

            VarType type = VarType.getVarTypeFromColumnName(colName);
            switch ( type ) {
                case OTHER:
                    break;
                case FCO2_WATER_EQU:
                case PCO2_WATER_EQU:
                case XCO2_WATER_EQU: {
                    co2WaterVarIndices.add(k);
                    AquGasConc co2WaterEqu = new AquGasConc(var);
                    ArrayList<String> addnInfo = new ArrayList<String>();
                    co2WaterEqu.setMeasureMethod(MethodType.MEASURED_INSITU);
                    co2WaterEqu.setSamplerNames(Collections.singletonList("Equilibrator"));
                    co2WaterEqu.setAnalyzerNames(Collections.singletonList("CO2 Sensor"));
                    co2WaterEqu.setReportTemperature("Equilibrator temperature");
                    if ( PlatformType.MOORING.equals(platformType) )
                        co2WaterEqu.setObserveType("Time Series");
                    else
                        co2WaterEqu.setObserveType("Surface Underway");
                    String strVal = getElementText(CO2_WATER_UNC_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2WaterEqu.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(CO2_WATER_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2WaterEqu.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    strVal = getElementText(CO2_FREQUENCY_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Frequency: " + strVal);
                    co2WaterEqu.setMethodReference(getElementText(METHOD_REFS_ELEMENT_NAME));
                    co2WaterEqu.setMethodDescription(getElementText(CO2_MEASUREMENT_METHOD_ELEMENT_NAME));
                    co2WaterEqu.setSamplingLocation(getElementText(INTAKE_LOCATION_ELEMENT_NAME));
                    co2WaterEqu.setSamplingElevation("Sampling Depth: " + getElementText(INTAKE_DEPTH_ELEMENT_NAME));
                    co2WaterEqu.setDryingMethod(getElementText(DRYING_METHOD_ELEMENT_NAME));
                    strVal = getElementText(DETAILS_OF_CO2_SENSING_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Details of CO2 Sensing: " + strVal);
                    co2WaterEqu.setAddnInfo(addnInfo);
                    var = co2WaterEqu;
                    break;
                }
                case FCO2_WATER_SST:
                case PCO2_WATER_SST:
                case XCO2_WATER_SST: {
                    co2WaterVarIndices.add(k);
                    AquGasConc co2WaterSst = new AquGasConc(var);
                    ArrayList<String> addnInfo = new ArrayList<String>();
                    co2WaterSst.setMeasureMethod(MethodType.MEASURED_INSITU);
                    co2WaterSst.setSamplerNames(Collections.singletonList("Equilibrator"));
                    co2WaterSst.setAnalyzerNames(Collections.singletonList("CO2 Sensor"));
                    co2WaterSst.setReportTemperature("Sea surface temperature");
                    if ( PlatformType.MOORING.equals(platformType) )
                        co2WaterSst.setObserveType("Time Series");
                    else
                        co2WaterSst.setObserveType("Surface Underway");
                    String strVal = getElementText(CO2_WATER_UNC_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2WaterSst.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(CO2_WATER_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2WaterSst.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    strVal = getElementText(CO2_FREQUENCY_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Frequency: " + strVal);
                    co2WaterSst.setMethodReference(getElementText(METHOD_REFS_ELEMENT_NAME));
                    co2WaterSst.setMethodDescription(getElementText(CO2_MEASUREMENT_METHOD_ELEMENT_NAME));
                    co2WaterSst.setSamplingLocation(getElementText(INTAKE_LOCATION_ELEMENT_NAME));
                    co2WaterSst.setSamplingElevation("Sampling Depth: " + getElementText(INTAKE_DEPTH_ELEMENT_NAME));
                    co2WaterSst.setDryingMethod(getElementText(DRYING_METHOD_ELEMENT_NAME));
                    strVal = getElementText(DETAILS_OF_CO2_SENSING_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Details of CO2 Sensing: " + strVal);
                    co2WaterSst.setAddnInfo(addnInfo);
                    var = co2WaterSst;
                    break;
                }
                case FCO2_ATM_ACTUAL:
                case PCO2_ATM_ACTUAL:
                case XCO2_ATM_ACTUAL: {
                    co2AtmVarIndices.add(k);
                    GasConc co2AtmActual = new GasConc(var);
                    ArrayList<String> addnInfo = new ArrayList<String>();
                    co2AtmActual.setMeasureMethod(MethodType.MEASURED_INSITU);
                    co2AtmActual.setAnalyzerNames(Collections.singletonList("CO2 Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        co2AtmActual.setObserveType("Time Series");
                    else
                        co2AtmActual.setObserveType("Surface Underway");
                    String strVal = getElementText(CO2_AIR_UNC_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2AtmActual.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(CO2_AIR_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2AtmActual.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    co2AtmActual.setMethodReference(getElementText(METHOD_REFS_ELEMENT_NAME));
                    co2AtmActual.setMethodDescription(getElementText(CO2_MEASUREMENT_METHOD_ELEMENT_NAME));
                    co2AtmActual.setSamplingLocation(getElementText(MARINE_AIR_LOCATION_ELEMENT_NAME));
                    co2AtmActual.setDryingMethod(getElementText(MARINE_AIR_DRYING_ELEMENT_NAME));
                    strVal = getElementText(DETAILS_OF_CO2_SENSING_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Details of CO2 Sensing: " + strVal);
                    strVal = getElementText(MARINE_AIR_MEASUREMENT_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Measurement: " + strVal);
                    co2AtmActual.setAddnInfo(addnInfo);
                    var = co2AtmActual;
                    break;
                }
                case FCO2_ATM_INTERP:
                case PCO2_ATM_INTERP:
                case XCO2_ATM_INTERP: {
                    co2AtmVarIndices.add(k);
                    GasConc co2AtmInterp = new GasConc(var);
                    ArrayList<String> addnInfo = new ArrayList<String>();
                    co2AtmInterp.setMeasureMethod(MethodType.COMPUTED);
                    co2AtmInterp.setAnalyzerNames(Collections.singletonList("CO2 Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        co2AtmInterp.setObserveType("Time Series");
                    else
                        co2AtmInterp.setObserveType("Surface Underway");
                    String strVal = getElementText(CO2_AIR_UNC_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2AtmInterp.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(CO2_AIR_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        co2AtmInterp.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    co2AtmInterp.setMethodReference(getElementText(METHOD_REFS_ELEMENT_NAME));
                    co2AtmInterp.setMethodDescription(getElementText(CO2_MEASUREMENT_METHOD_ELEMENT_NAME));
                    co2AtmInterp.setSamplingLocation(getElementText(MARINE_AIR_LOCATION_ELEMENT_NAME));
                    co2AtmInterp.setDryingMethod(getElementText(MARINE_AIR_DRYING_ELEMENT_NAME));
                    strVal = getElementText(DETAILS_OF_CO2_SENSING_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Details of CO2 Sensing: " + strVal);
                    strVal = getElementText(MARINE_AIR_MEASUREMENT_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Measurement: " + strVal);
                    co2AtmInterp.setAddnInfo(addnInfo);
                    var = co2AtmInterp;
                    break;
                }
                case SEA_SURFACE_TEMPERATURE: {
                    Temperature sst = new Temperature(var);
                    ArrayList<String> addnInfo = new ArrayList<String>();
                    sst.setMeasureMethod(MethodType.MEASURED_INSITU);
                    sst.setAnalyzerNames(Collections.singletonList("Water Temperature Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        sst.setObserveType("Time Series");
                    else
                        sst.setObserveType("Surface Underway");
                    String strVal = getElementText(SST_ACCURACY_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(SST_ACCURACY_DEGC_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(SST_UNCERTAINTY_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        sst.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(SST_PRECISION_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(SST_PRECISION_DEGC_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(SST_RESOLUTION_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        sst.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    sst.setSamplingLocation(getElementText(SST_LOCATION_ELEMENT_NAME));
                    sst.setAddnInfo(addnInfo);
                    var = sst;
                    break;
                }
                case EQUILIBRATOR_TEMPERATURE: {
                    Temperature tequ = new Temperature(var);
                    ArrayList<String> addnInfo = new ArrayList<String>();
                    tequ.setMeasureMethod(MethodType.MEASURED_INSITU);
                    tequ.setAnalyzerNames(Collections.singletonList("Equilibrator Temperature Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        tequ.setObserveType("Time Series");
                    else
                        tequ.setObserveType("Surface Underway");
                    String strVal = getElementText(EQT_ACCURACY_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(EQT_ACCURACY_DEGC_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(EQT_UNCERTAINTY_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        tequ.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(EQT_PRECISION_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(EQT_PRECISION_DEGC_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(EQT_RESOLUTION_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        tequ.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    tequ.setSamplingLocation(getElementText(EQT_LOCATION_ELEMENT_NAME));
                    tequ.setAddnInfo(addnInfo);
                    var = tequ;
                    break;
                }
                case SEA_LEVEL_PRESSURE: {
                    AirPressure slp = new AirPressure(var);
                    ArrayList<String> addnInfo = new ArrayList<String>();
                    slp.setMeasureMethod(MethodType.MEASURED_INSITU);
                    slp.setAnalyzerNames(Collections.singletonList("Atmospheric Pressure Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        slp.setObserveType("Time Series");
                    else
                        slp.setObserveType("Surface Underway");
                    String strVal = getElementText(ATM_ACCURACY_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(ATM_ACCURACY_HPA_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(ATM_UNCERTAINTY_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        slp.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(ATM_PRECISION_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(ATM_PRECISION_HPA_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(ATM_RESOLUTION_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        slp.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    slp.setSamplingLocation(getElementText(ATM_LOCATION_ELEMENT_NAME));
                    strVal = getElementText(ATM_NORMALIZED_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        slp.setPressureCorrection("Normalized: " + strVal);
                    slp.setAddnInfo(addnInfo);
                    var = slp;
                    break;
                }
                case EQUILIBRATOR_PRESSURE: {
                    AirPressure pequ = new AirPressure(var);
                    ArrayList<String> addnInfo = new ArrayList<String>();
                    pequ.setMeasureMethod(MethodType.MEASURED_INSITU);
                    pequ.setAnalyzerNames(Collections.singletonList("Equilibrator Pressure Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        pequ.setObserveType("Time Series");
                    else
                        pequ.setObserveType("Surface Underway");
                    String strVal = getElementText(EQP_ACCURACY_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(EQP_ACCURACY_HPA_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(EQP_UNCERTAINTY_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        pequ.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(EQP_PRECISION_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(EQP_PRECISION_HPA_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(EQP_RESOLUTION_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        pequ.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    pequ.setSamplingLocation(getElementText(EQP_LOCATION_ELEMENT_NAME));
                    strVal = getElementText(EQP_NORMALIZED_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        pequ.setPressureCorrection("Normalized: " + strVal);
                    pequ.setAddnInfo(addnInfo);
                    var = pequ;
                    break;
                }
                case SALINITY: {
                    DataVar sal = new DataVar(var);
                    ArrayList<String> addnInfo = new ArrayList<String>();
                    sal.setMeasureMethod(MethodType.MEASURED_INSITU);
                    sal.setAnalyzerNames(Collections.singletonList("Salinity Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        sal.setObserveType("Time Series");
                    else
                        sal.setObserveType("Surface Underway");
                    String strVal = getElementText(SSS_ACCURACY_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(SSS_UNCERTAINTY_ELEMENT_NAME);
                    NumericString numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        sal.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(SSS_PRECISION_ELEMENT_NAME);
                    if ( strVal.isEmpty() )
                        strVal = getElementText(SSS_RESOLUTION_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        sal.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    sal.setSamplingLocation(getElementText(SSS_LOCATION_ELEMENT_NAME));
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
                varList.get(idx).setFlagColName(woceCO2WaterVarNames);
            }
        }
        if ( woceCO2AtmVarNames != null ) {
            for (int idx : co2AtmVarIndices) {
                varList.get(idx).setFlagColName(woceCO2AtmVarNames);
            }
        }

        return varList;
    }

    /**
     * @return list of sampler information; never null.
     *         CDIAC underway metadata only describes a single equilibrator.
     */
    private List<Sampler> getSamplers() {
        // Only the one equilibrator in CDIAC underway forms
        Equilibrator sampler = new Equilibrator();
        sampler.setName("Equilibrator");
        // sampler.setId(id); - not specified
        // sampler.setManufacturer(manufacturer); - not specified
        // sampler.setModel(model); - not specified

        sampler.setEquilibratorType(getElementText(EQUI_TYPE_ELEMENT_NAME));
        sampler.setChamberVol(getElementText(EQUI_VOLUME_ELEMENT_NAME));
        // sampler.setChamberWaterVol(chamberWaterVol); - probably part of chamber volume
        // sampler.setChamberGasVol(chamberGasVol); - probably part of chamber volume
        sampler.setWaterFlowRate(getElementText(WATER_FLOW_RATE_ELEMENT_NAME));
        sampler.setGasFlowRate(getElementText(GAS_FLOW_RATE_ELEMENT_NAME));
        sampler.setVenting(getElementText(VENTED_ELEMENT_NAME));
        sampler.setAddnInfo(getListOfLines(getElementText(EQUI_ADDITIONAL_INFO_ELEMENT_NAME)));

        return Collections.singletonList(sampler);
    }

    /**
     * @return list of analyzer (sensor) information; never null.
     */
    private ArrayList<Analyzer> getAnalyzers() {
        ArrayList<Analyzer> sensors = new ArrayList<Analyzer>();

        GasSensor co2Sensor = new GasSensor();
        co2Sensor.setName("CO2 Sensor");
        co2Sensor.setManufacturer(getElementText(CO2_SENSOR_MANUFACTURER_ELEMENT_NAME));
        co2Sensor.setModel(getElementText(CO2_SENSOR_MODEL_ELEMENT_NAME));
        co2Sensor.setCalibration(getElementText(CO2_SENSOR_CALIBRATION_ELEMENT_NAME));
        ArrayList<String> addnInfo = getListOfLines(getElementText(CO2_SENSOR_COMMENTS_ELEMENT_NAME));
        String strVal = getElementText(ANALYSIS_OF_COMPARISON_ELEMENT_NAME);
        if ( !strVal.isEmpty() )
            addnInfo.add(0, "Analysis of CO2 Comparison: " + strVal);
        strVal = getElementText(ENVIRONMENTAL_CONTROL_ELEMENT_NAME);
        if ( !strVal.isEmpty() )
            addnInfo.add(0, "Environmental Control: " + strVal);
        strVal = getElementText(MEASURED_CO2_PARAMS_ELEMENT_NAME);
        if ( !strVal.isEmpty() )
            addnInfo.add(0, "Measured CO2 Parameters: " + strVal);
        co2Sensor.setAddnInfo(addnInfo);
        // All the calibration gas information is stuck together in the following ...
        String calGasInfo = getElementText(CO2_CALIBRATION_MANUFACTURER_ELEMENT_NAME);
        ArrayList<String> calGasInfoList = getListOfLines(calGasInfo);
        // ... except for the number of non-zero calibration gasses
        String numNonZeroGasses = getElementText(CO2_SENSOR_NUM_NONZERO_GASSES_ELEMENT_NAME);
        int numNonZero = numNonZeroGasses.isEmpty() ? 0 : Integer.parseInt(numNonZeroGasses);
        if ( (numNonZero > 0) && !calGasInfo.isEmpty() ) {
            ArrayList<CalibrationGas> gasList = new ArrayList<CalibrationGas>(numNonZero);
            if ( (calGasInfoList.size() == 1) && (numNonZero == 1) ) {
                gasList.add(new CalibrationGas(calGasInfo, "CO2", null, null, null));
            }
            else if ( calGasInfoList.size() == numNonZero ) {
                for (int k = 0; k < numNonZero; k++) {
                    gasList.add(new CalibrationGas(calGasInfoList.get(k), "CO2", null, null, null));
                }
            }
            else {
                for (int k = 1; k <= numNonZero; k++) {
                    gasList.add(new CalibrationGas(
                            "Calibration gas " + Integer.toString(k) + " mentioned in: " + calGasInfo,
                            "CO2", null, null, null));
                }
            }
            co2Sensor.setCalibrationGases(gasList);
        }
        sensors.add(co2Sensor);

        TemperatureSensor sstSensor = new TemperatureSensor();
        sstSensor.setName("Water Temperature Sensor");
        sstSensor.setManufacturer(getElementText(SST_MANUFACTURER_ELEMENT_NAME));
        sstSensor.setModel(getElementText(SST_MODEL_ELEMENT_NAME));
        sstSensor.setCalibration(getElementText(SST_CALIBRATION_ELEMENT_NAME));
        sstSensor.setAddnInfo(getListOfLines(getElementText(SST_COMMENTS_ELEMENT_NAME)));
        sensors.add(sstSensor);

        TemperatureSensor teqSensor = new TemperatureSensor();
        teqSensor.setName("Equilibrator Temperature Sensor");
        teqSensor.setManufacturer(getElementText(EQT_MANUFACTURER_ELEMENT_NAME));
        teqSensor.setModel(getElementText(EQT_MODEL_ELEMENT_NAME));
        teqSensor.setCalibration(getElementText(EQT_CALIBRATION_ELEMENT_NAME));
        addnInfo = getListOfLines(getElementText(EQT_COMMENTS_ELEMENT_NAME));
        strVal = getElementText(EQT_WARMING_ELEMENT_NAME);
        if ( !strVal.isEmpty() )
            addnInfo.add(0, "Warming: " + strVal);
        teqSensor.setAddnInfo(addnInfo);
        sensors.add(teqSensor);

        PressureSensor slpSensor = new PressureSensor();
        slpSensor.setName("Atmospheric Pressure Sensor");
        slpSensor.setManufacturer(getElementText(ATM_MANUFACTURER_ELEMENT_NAME));
        slpSensor.setModel(getElementText(ATM_MODEL_ELEMENT_NAME));
        slpSensor.setCalibration(getElementText(ATM_CALIBRATION_ELEMENT_NAME));
        slpSensor.setAddnInfo(getListOfLines(getElementText(ATM_COMMENTS_ELEMENT_NAME)));
        sensors.add(slpSensor);

        PressureSensor peqSensor = new PressureSensor();
        peqSensor.setName("Equilibrator Pressure Sensor");
        peqSensor.setManufacturer(getElementText(EQP_MANUFACTURER_ELEMENT_NAME));
        peqSensor.setModel(getElementText(EQP_MODEL_ELEMENT_NAME));
        peqSensor.setCalibration(getElementText(EQP_CALIBRATION_ELEMENT_NAME));
        peqSensor.setAddnInfo(getListOfLines(getElementText(EQP_COMMENTS_ELEMENT_NAME)));
        sensors.add(peqSensor);

        SalinitySensor salSensor = new SalinitySensor();
        salSensor.setName("Salinity Sensor");
        salSensor.setManufacturer(getElementText(SSS_MANUFACTURER_ELEMENT_NAME));
        salSensor.setModel(getElementText(SSS_MODEL_ELEMENT_NAME));
        salSensor.setCalibration(getElementText(SSS_CALIBRATION_ELEMENT_NAME));
        salSensor.setAddnInfo(getListOfLines(getElementText(SSS_COMMENTS_ELEMENT_NAME)));
        sensors.add(salSensor);

        int k = 0;
        for (Element elem : getElementList(OTHER_SENSORS_ELEMENT_NAME)) {
            k++;
            Analyzer otherSensor = new Analyzer();
            otherSensor.setName("Other Sensor " + Integer.toString(k));
            otherSensor.setManufacturer(elem.getChildTextTrim(MANUFACTURER_ELEMENT_NAME));
            otherSensor.setModel(elem.getChildTextTrim(MODEL_ELEMENT_NAME));
            otherSensor.setCalibration(elem.getChildTextTrim(CALIBRATION_ELEMENT_NAME));
            otherSensor.setAddnInfo(getListOfLines(elem.getChildTextTrim(COMMENTS_ELEMENT_NAME)));
            sensors.add(otherSensor);
        }

        return sensors;
    }

}
