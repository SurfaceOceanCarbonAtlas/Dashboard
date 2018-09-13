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
import java.util.Arrays;
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
    private static final String MOORING_ID_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + "\t" + "Mooring_ID";

    private static final String CRUISE_ELEMENT_NAME = EXPERIMENT_ELEMENT_NAME + "\t" + "Cruise";
    private static final String EXPOCODE_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Expocode";
    private static final String CRUISE_ID_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Cruise_ID";
    private static final String SUB_CRUISE_INFO_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Cruise_Info";
    private static final String SECTION_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Section";
    private static final String PORT_OF_CALL_ELEMENT_NAME = CRUISE_ELEMENT_NAME + "\t" + "Ports_of_Call";

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
    private static final String VESSEL_COUNTRY_ELEMENT_NAME = VESSEL_ELEMENT_NAME + "\t" + "Country";
    private static final String VESSEL_OWNER_ELEMENT_NAME = VESSEL_ELEMENT_NAME + "\t" + "Vessel_Owner";

    private static final String VARIABLES_INFO_ELEMENT_NAME = "Variables_Info";
    // <Variables_Info> element contains multiple <Variable> elements, each containing <Variable_Name>, <Description_of_Variable>,
    // and possibly <Unit_of_Variable> (or unit of variable may be part of the description)
    private static final String VARIABLE_ELEMENT_NAME = VARIABLES_INFO_ELEMENT_NAME + "\t" + "Variable";
    private static final String VARIABLES_NAME_ELEMENT_NAME = "Variable_Name";
    private static final String VARIABLES_DESCRIPTION_ELEMENT_NAME = "Description_of_Variable";
    private static final String VARIABLES_UNIT_OF_VARIABLE_ELEMENT_NAME = "Unit_of_Variable";


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

    private static final String CO2_SENSOR_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "CO2_Sensors" + "\t" + "CO2_Sensor";
    private static final String CO2_MEASUREMENT_METHOD_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "Measurement_Method";
    private static final String CO2_SENSOR_MANUFACTURER_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String CO2_SENSOR_MODEL_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
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
    private static final String CO2_SENSOR_COMMENTS_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + COMMENTS_ELEMENT_NAME;
    private static final String CO2_SENSOR_NUM_NONZERO_GASSES_ELEMENT_NAME = CO2_SENSOR_ELEMENT_NAME + "\t" + "No_Of_Non_Zero_Gas_Stds";

    private static final String SST_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Sea_Surface_Temperature";
    private static final String SST_LOCATION_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + LOCATION_ELEMENT_NAME;
    private static final String SST_MANUFACTURER_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String SST_MODEL_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
    private static final String SST_ACCURACY_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + ACCURACY_ELEMENT_NAME;
    private static final String SST_UNCERTAINTY_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + UNCERTAINTY_ELEMENT_NAME;
    private static final String SST_PRECISION_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + PRECISION_ELEMENT_NAME;
    private static final String SST_RESOLUTION_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + RESOLUTION_ELEMENT_NAME;
    private static final String SST_CALIBRATION_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + CALIBRATION_ELEMENT_NAME;
    private static final String SST_COMMENTS_ELEMENT_NAME = SST_ELEMENT_NAME + "\t" + COMMENTS_ELEMENT_NAME;

    private static final String EQU_TEMP_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Equilibrator_Temperature";
    private static final String EQT_LOCATION_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + LOCATION_ELEMENT_NAME;
    private static final String EQT_MANUFACTURER_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String EQT_MODEL_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
    private static final String EQT_ACCURACY_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + ACCURACY_ELEMENT_NAME;
    private static final String EQT_UNCERTAINTY_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + UNCERTAINTY_ELEMENT_NAME;
    private static final String EQT_PRECISION_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + PRECISION_ELEMENT_NAME;
    private static final String EQT_RESOLUTION_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + RESOLUTION_ELEMENT_NAME;
    private static final String EQT_CALIBRATION_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + CALIBRATION_ELEMENT_NAME;
    private static final String EQT_WARMING_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + "Warming";
    private static final String EQT_COMMENTS_ELEMENT_NAME = EQU_TEMP_ELEMENT_NAME + "\t" + COMMENTS_ELEMENT_NAME;

    private static final String EQU_PRESSURE_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Equilibrator_Pressure";
    private static final String EQP_LOCATION_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + LOCATION_ELEMENT_NAME;
    private static final String EQP_MANUFACTURER_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String EQP_MODEL_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
    private static final String EQP_ACCURACY_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + ACCURACY_ELEMENT_NAME;
    private static final String EQP_UNCERTAINTY_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + UNCERTAINTY_ELEMENT_NAME;
    private static final String EQP_PRECISION_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + PRECISION_ELEMENT_NAME;
    private static final String EQP_RESOLUTION_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + RESOLUTION_ELEMENT_NAME;
    private static final String EQP_CALIBRATION_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + CALIBRATION_ELEMENT_NAME;
    private static final String EQP_COMMENTS_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + COMMENTS_ELEMENT_NAME;
    private static final String EQP_NORMALIZED_ELEMENT_NAME = EQU_PRESSURE_ELEMENT_NAME + "\t" + "Normalized";

    private static final String ATM_PRESSURE_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Atmospheric_Pressure";
    private static final String ATM_LOCATION_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + LOCATION_ELEMENT_NAME;
    private static final String ATM_MANUFACTURER_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String ATM_MODEL_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
    private static final String ATM_ACCURACY_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + ACCURACY_ELEMENT_NAME;
    private static final String ATM_UNCERTAINTY_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + UNCERTAINTY_ELEMENT_NAME;
    private static final String ATM_PRECISION_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + PRECISION_ELEMENT_NAME;
    private static final String ATM_RESOLUTION_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + RESOLUTION_ELEMENT_NAME;
    private static final String ATM_CALIBRATION_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + CALIBRATION_ELEMENT_NAME;
    private static final String ATM_COMMENTS_ELEMENT_NAME = ATM_PRESSURE_ELEMENT_NAME + "\t" + COMMENTS_ELEMENT_NAME;

    private static final String SSS_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Sea_Surface_Salinity";
    private static final String SSS_LOCATION_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + LOCATION_ELEMENT_NAME;
    private static final String SSS_MANUFACTURER_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + MANUFACTURER_ELEMENT_NAME;
    private static final String SSS_MODEL_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + MODEL_ELEMENT_NAME;
    private static final String SSS_ACCURACY_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + ACCURACY_ELEMENT_NAME;
    private static final String SSS_UNCERTAINTY_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + UNCERTAINTY_ELEMENT_NAME;
    private static final String SSS_PRECISION_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + PRECISION_ELEMENT_NAME;
    private static final String SSS_RESOLUTION_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + RESOLUTION_ELEMENT_NAME;
    private static final String SSS_CALIBRATION_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + CALIBRATION_ELEMENT_NAME;
    private static final String SSS_COMMENTS_ELEMENT_NAME = SSS_ELEMENT_NAME + "\t" + COMMENTS_ELEMENT_NAME;

    private static final String OTHER_SENSORS_ELEMENT_NAME = METHOD_DESCRIPTION_ELEMENT_NAME + "\t" + "Other_Sensors" + "\t" + "Sensor";
    // <Other_Sensors> contains multiple <Sensor> elements, each with with <Manufacturer>, <Model>, <Accuracy> or <Uncertainty>,
    // <Precision> or <Resolution>, <Calibration>, and <Other_Comments> elements

    private static final String DATA_SET_REFS_ELEMENT_NAME = "Data_set_References";
    private static final String ADDN_INFO_ELEMENT_NAME = "Additional_Information";
    private static final String CITATION_ELEMENT_NAME = "Citation";

    private static final String DATA_SET_LINK_ELEMENT_NAME = "Data_Set_Link";
    private static final String DATA_SET_LINK_URL_ELEMENT_NAME = DATA_SET_LINK_ELEMENT_NAME + "\t" + "URL";
    private static final String DATA_SET_LINK_NOTE_ELEMENT_NAME = DATA_SET_LINK_ELEMENT_NAME + "\t" + "Link_Note";

    /**
     * Create from XML content provided by the given reader.
     *
     * @param xmlReader
     *         read the XML from here
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
     *         one of "Ship", "Mooring", or "Drifting Buoy"
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

            ArrayList<String> addnInfo;
            String strVal;
            NumericString numStr;

            // TODO:
            // var.setAccuracy();
            // var.setPrecision();
            // var.setFlagColName();
            // var.setAddnInfo();
            // DataVar - setSamplingLocation, setSamplingElevation, ....
            // AirPressure - DataVar + setPressureCorrection
            // GasConc - DataVar + setDryingMethod, setWaterVaporCorrection
            // AquGasConc - GasConc + setReportTemperature, setTemperatureCorrection

            VarType type = VarType.getVarTypeFromColumnName(colName);
            switch ( type ) {
                case OTHER:
                    break;
                case FCO2_WATER_EQU:
                    co2WaterVarIndices.add(k);
                    AquGasConc fco2WaterEqu = new AquGasConc(var);
                    addnInfo = new ArrayList<String>();
                    fco2WaterEqu.setMeasureMethod(MethodType.MEASURED_INSITU);
                    fco2WaterEqu.setSamplerNames(Arrays.asList("Equilibrator"));
                    fco2WaterEqu.setAnalyzerNames(Arrays.asList("CO2 Sensor"));
                    fco2WaterEqu.setReportTemperature("Equilibrator temperature");
                    if ( PlatformType.MOORING.equals(platformType) )
                        fco2WaterEqu.setObserveType("Time series");
                    else
                        fco2WaterEqu.setObserveType("Surface Underway");
                    strVal = getElementText(CO2_WATER_UNC_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        fco2WaterEqu.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(CO2_WATER_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        fco2WaterEqu.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    strVal = getElementText(CO2_FREQUENCY_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Frequency: " + strVal);
                    fco2WaterEqu.setMethodReference(getElementText(METHOD_REFS_ELEMENT_NAME));
                    fco2WaterEqu.setSamplingLocation(getElementText(INTAKE_LOCATION_ELEMENT_NAME));
                    fco2WaterEqu.setSamplingElevation("Sampling Depth: " + getElementText(INTAKE_DEPTH_ELEMENT_NAME));
                    fco2WaterEqu.setDryingMethod(getElementText(DRYING_METHOD_ELEMENT_NAME));

                    // TODO:

                    fco2WaterEqu.setAddnInfo(addnInfo);
                    var = fco2WaterEqu;
                    break;
                case FCO2_WATER_SST:
                    co2WaterVarIndices.add(k);
                    AquGasConc fco2WaterSst = new AquGasConc(var);
                    addnInfo = new ArrayList<String>();
                    fco2WaterSst.setMeasureMethod(MethodType.MEASURED_INSITU);
                    fco2WaterSst.setSamplerNames(Arrays.asList("Equilibrator"));
                    fco2WaterSst.setAnalyzerNames(Arrays.asList("CO2 Sensor"));
                    fco2WaterSst.setReportTemperature("Sea surface temperature");
                    if ( PlatformType.MOORING.equals(platformType) )
                        fco2WaterSst.setObserveType("Time series");
                    else
                        fco2WaterSst.setObserveType("Surface Underway");
                    strVal = getElementText(CO2_WATER_UNC_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        fco2WaterSst.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(CO2_WATER_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        fco2WaterSst.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    strVal = getElementText(CO2_FREQUENCY_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Frequency: " + strVal);
                    fco2WaterSst.setMethodReference(getElementText(METHOD_REFS_ELEMENT_NAME));
                    fco2WaterSst.setSamplingLocation(getElementText(INTAKE_LOCATION_ELEMENT_NAME));
                    fco2WaterSst.setSamplingElevation("Sampling Depth: " + getElementText(INTAKE_DEPTH_ELEMENT_NAME));
                    fco2WaterSst.setDryingMethod(getElementText(DRYING_METHOD_ELEMENT_NAME));

                    // TODO:

                    fco2WaterSst.setAddnInfo(addnInfo);
                    var = fco2WaterSst;
                    break;
                case PCO2_WATER_EQU:
                    co2WaterVarIndices.add(k);
                    AquGasConc pco2WaterEqu = new AquGasConc(var);
                    addnInfo = new ArrayList<String>();
                    pco2WaterEqu.setMeasureMethod(MethodType.MEASURED_INSITU);
                    pco2WaterEqu.setSamplerNames(Arrays.asList("Equilibrator"));
                    pco2WaterEqu.setAnalyzerNames(Arrays.asList("CO2 Sensor"));
                    pco2WaterEqu.setReportTemperature("Equilibrator temperature");
                    if ( PlatformType.MOORING.equals(platformType) )
                        pco2WaterEqu.setObserveType("Time series");
                    else
                        pco2WaterEqu.setObserveType("Surface Underway");
                    strVal = getElementText(CO2_WATER_UNC_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        pco2WaterEqu.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(CO2_WATER_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        pco2WaterEqu.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    strVal = getElementText(CO2_FREQUENCY_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Frequency: " + strVal);
                    pco2WaterEqu.setMethodReference(getElementText(METHOD_REFS_ELEMENT_NAME));
                    pco2WaterEqu.setSamplingLocation(getElementText(INTAKE_LOCATION_ELEMENT_NAME));
                    pco2WaterEqu.setSamplingElevation("Sampling Depth: " + getElementText(INTAKE_DEPTH_ELEMENT_NAME));
                    pco2WaterEqu.setDryingMethod(getElementText(DRYING_METHOD_ELEMENT_NAME));

                    // TODO:

                    pco2WaterEqu.setAddnInfo(addnInfo);
                    var = pco2WaterEqu;
                    break;
                case PCO2_WATER_SST:
                    co2WaterVarIndices.add(k);
                    AquGasConc pco2WaterSst = new AquGasConc(var);
                    addnInfo = new ArrayList<String>();
                    pco2WaterSst.setMeasureMethod(MethodType.MEASURED_INSITU);
                    pco2WaterSst.setSamplerNames(Arrays.asList("Equilibrator"));
                    pco2WaterSst.setAnalyzerNames(Arrays.asList("CO2 Sensor"));
                    pco2WaterSst.setReportTemperature("Sea surface temperature");
                    if ( PlatformType.MOORING.equals(platformType) )
                        pco2WaterSst.setObserveType("Time series");
                    else
                        pco2WaterSst.setObserveType("Surface Underway");
                    strVal = getElementText(CO2_WATER_UNC_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        pco2WaterSst.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(CO2_WATER_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        pco2WaterSst.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    strVal = getElementText(CO2_FREQUENCY_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Frequency: " + strVal);
                    pco2WaterSst.setMethodReference(getElementText(METHOD_REFS_ELEMENT_NAME));
                    pco2WaterSst.setSamplingLocation(getElementText(INTAKE_LOCATION_ELEMENT_NAME));
                    pco2WaterSst.setSamplingElevation("Sampling Depth: " + getElementText(INTAKE_DEPTH_ELEMENT_NAME));
                    pco2WaterSst.setDryingMethod(getElementText(DRYING_METHOD_ELEMENT_NAME));

                    // TODO:

                    pco2WaterSst.setAddnInfo(addnInfo);
                    var = pco2WaterSst;
                    break;
                case XCO2_WATER_EQU:
                    co2WaterVarIndices.add(k);
                    AquGasConc xco2WaterEqu = new AquGasConc(var);
                    addnInfo = new ArrayList<String>();
                    xco2WaterEqu.setMeasureMethod(MethodType.MEASURED_INSITU);
                    xco2WaterEqu.setSamplerNames(Arrays.asList("Equilibrator"));
                    xco2WaterEqu.setAnalyzerNames(Arrays.asList("CO2 Sensor"));
                    xco2WaterEqu.setReportTemperature("Equilibrator temperature");
                    if ( PlatformType.MOORING.equals(platformType) )
                        xco2WaterEqu.setObserveType("Time series");
                    else
                        xco2WaterEqu.setObserveType("Surface Underway");
                    strVal = getElementText(CO2_WATER_UNC_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        xco2WaterEqu.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(CO2_WATER_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        xco2WaterEqu.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    strVal = getElementText(CO2_FREQUENCY_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Frequency: " + strVal);
                    xco2WaterEqu.setMethodReference(getElementText(METHOD_REFS_ELEMENT_NAME));
                    xco2WaterEqu.setSamplingLocation(getElementText(INTAKE_LOCATION_ELEMENT_NAME));
                    xco2WaterEqu.setSamplingElevation("Sampling Depth: " + getElementText(INTAKE_DEPTH_ELEMENT_NAME));
                    xco2WaterEqu.setDryingMethod(getElementText(DRYING_METHOD_ELEMENT_NAME));

                    // TODO:

                    xco2WaterEqu.setAddnInfo(addnInfo);
                    var = xco2WaterEqu;
                    break;
                case XCO2_WATER_SST:
                    co2WaterVarIndices.add(k);
                    AquGasConc xco2WaterSst = new AquGasConc(var);
                    addnInfo = new ArrayList<String>();
                    xco2WaterSst.setMeasureMethod(MethodType.MEASURED_INSITU);
                    xco2WaterSst.setSamplerNames(Arrays.asList("Equilibrator"));
                    xco2WaterSst.setAnalyzerNames(Arrays.asList("CO2 Sensor"));
                    xco2WaterSst.setReportTemperature("Sea surface temperature");
                    if ( PlatformType.MOORING.equals(platformType) )
                        xco2WaterSst.setObserveType("Time series");
                    else
                        xco2WaterSst.setObserveType("Surface Underway");
                    strVal = getElementText(CO2_WATER_UNC_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        xco2WaterSst.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(CO2_WATER_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        xco2WaterSst.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    strVal = getElementText(CO2_FREQUENCY_ELEMENT_NAME);
                    if ( !strVal.isEmpty() )
                        addnInfo.add("Frequency: " + strVal);
                    xco2WaterSst.setMethodReference(getElementText(METHOD_REFS_ELEMENT_NAME));
                    xco2WaterSst.setSamplingLocation(getElementText(INTAKE_LOCATION_ELEMENT_NAME));
                    xco2WaterSst.setSamplingElevation("Sampling Depth: " + getElementText(INTAKE_DEPTH_ELEMENT_NAME));
                    xco2WaterSst.setDryingMethod(getElementText(DRYING_METHOD_ELEMENT_NAME));

                    // TODO:

                    xco2WaterSst.setAddnInfo(addnInfo);
                    var = xco2WaterSst;
                    break;
                case FCO2_ATM_ACTUAL:
                    co2AtmVarIndices.add(k);
                    GasConc fco2AtmActual = new GasConc(var);
                    addnInfo = new ArrayList<String>();
                    fco2AtmActual.setMeasureMethod(MethodType.MEASURED_INSITU);
                    fco2AtmActual.setSamplerNames(Arrays.asList("Equilibrator"));
                    fco2AtmActual.setAnalyzerNames(Arrays.asList("CO2 Sensor"));
                    if ( PlatformType.MOORING.equals(platformType) )
                        fco2AtmActual.setObserveType("Time series");
                    else
                        fco2AtmActual.setObserveType("Surface Underway");
                    strVal = getElementText(CO2_AIR_UNC_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        fco2AtmActual.setAccuracy(numStr);
                    else
                        addnInfo.add("Accuracy/Uncertainty: " + strVal);
                    strVal = getElementText(CO2_AIR_RES_ELEMENT_NAME);
                    numStr = getNumericString(strVal, null);
                    if ( numStr.isValid() )
                        fco2AtmActual.setPrecision(numStr);
                    else
                        addnInfo.add("Precision/Resolution: " + strVal);
                    fco2AtmActual.setMethodReference(getElementText(METHOD_REFS_ELEMENT_NAME));
                    fco2AtmActual.setSamplingLocation(getElementText(MARINE_AIR_LOCATION_ELEMENT_NAME));
                    fco2AtmActual.setDryingMethod(getElementText(MARINE_AIR_DRYING_ELEMENT_NAME));

                    // TODO:

                    strVal = getElementText(MARINE_AIR_MEASUREMENT_ELEMENT_NAME);
                    if ( ! strVal.isEmpty() )
                        addnInfo.add("Measurement: " + strVal);
                    fco2AtmActual.setAddnInfo(addnInfo);
                    var = fco2AtmActual;
                    break;
                case FCO2_ATM_INTERP:
                    co2AtmVarIndices.add(k);
                    GasConc fco2AtmInterp = new GasConc(var);
                    // TODO:
                    var = fco2AtmInterp;
                    break;
                case PCO2_ATM_ACTUAL:
                    co2AtmVarIndices.add(k);
                    GasConc pco2AtmActual = new GasConc(var);
                    // TODO:
                    var = pco2AtmActual;
                    break;
                case PCO2_ATM_INTERP:
                    co2AtmVarIndices.add(k);
                    GasConc pco2AtmInterp = new GasConc(var);
                    // TODO:
                    var = pco2AtmInterp;
                    break;
                case XCO2_ATM_ACTUAL:
                    co2AtmVarIndices.add(k);
                    GasConc xco2AtmActual = new GasConc(var);
                    // TODO:
                    var = xco2AtmActual;
                    break;
                case XCO2_ATM_INTERP:
                    co2AtmVarIndices.add(k);
                    GasConc xco2AtmInterp = new GasConc(var);
                    // TODO:
                    var = xco2AtmInterp;
                    break;
                case SEA_SURFACE_TEMPERATURE:
                    Temperature sst = new Temperature(var);
                    // TODO:
                    var = sst;
                    break;
                case EQUILIBRATOR_TEMPERATURE:
                    Temperature tequ = new Temperature(var);
                    // TODO:
                    var = tequ;
                    break;
                case SEA_LEVEL_PRESSURE:
                    AirPressure slp = new AirPressure(var);
                    // TODO:
                    var = slp;
                    break;
                case EQUILIBRATOR_PRESSURE:
                    AirPressure pequ = new AirPressure(var);
                    // TODO:
                    var = pequ;
                    break;
                case SALINITY:
                    DataVar sal = new DataVar(var);
                    // TODO:
                    var = sal;
                    break;
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

        return Arrays.asList(sampler);
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
        co2Sensor.setAddnInfo(getListOfLines(getElementText(CO2_SENSOR_COMMENTS_ELEMENT_NAME)));
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
        sstSensor.setName("SST Sensor");
        sstSensor.setManufacturer(getElementText(SST_MANUFACTURER_ELEMENT_NAME));
        sstSensor.setModel(getElementText(SST_MODEL_ELEMENT_NAME));
        sstSensor.setCalibration(getElementText(SST_CALIBRATION_ELEMENT_NAME));
        sstSensor.setAddnInfo(getListOfLines(getElementText(SST_COMMENTS_ELEMENT_NAME)));
        sensors.add(sstSensor);

        TemperatureSensor teqSensor = new TemperatureSensor();
        teqSensor.setName("T_equi Sensor");
        teqSensor.setManufacturer(getElementText(EQT_MANUFACTURER_ELEMENT_NAME));
        teqSensor.setModel(getElementText(EQT_MODEL_ELEMENT_NAME));
        teqSensor.setCalibration(getElementText(EQT_CALIBRATION_ELEMENT_NAME));
        ArrayList<String> addnInfo = getListOfLines(getElementText(EQT_COMMENTS_ELEMENT_NAME));
        String warming = getElementText(EQT_WARMING_ELEMENT_NAME);
        if ( !warming.isEmpty() )
            addnInfo.add(0, "Warming: " + warming);
        teqSensor.setAddnInfo(addnInfo);
        sensors.add(teqSensor);

        PressureSensor slpSensor = new PressureSensor();
        slpSensor.setName("SLP Sensor");
        slpSensor.setManufacturer(getElementText(ATM_MANUFACTURER_ELEMENT_NAME));
        slpSensor.setModel(getElementText(ATM_MODEL_ELEMENT_NAME));
        slpSensor.setCalibration(getElementText(ATM_CALIBRATION_ELEMENT_NAME));
        slpSensor.setAddnInfo(getListOfLines(getElementText(ATM_COMMENTS_ELEMENT_NAME)));
        sensors.add(slpSensor);

        PressureSensor peqSensor = new PressureSensor();
        peqSensor.setName("P_equi Sensor");
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
