package gov.noaa.pmel.sdimetadata.xml;

import gov.noaa.pmel.sdimetadata.Coverage;
import gov.noaa.pmel.sdimetadata.MiscInfo;
import gov.noaa.pmel.sdimetadata.Platform;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.instrument.Analyzer;
import gov.noaa.pmel.sdimetadata.instrument.Equilibrator;
import gov.noaa.pmel.sdimetadata.instrument.PressureSensor;
import gov.noaa.pmel.sdimetadata.instrument.SalinitySensor;
import gov.noaa.pmel.sdimetadata.instrument.Sampler;
import gov.noaa.pmel.sdimetadata.instrument.TemperatureSensor;
import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
import gov.noaa.pmel.sdimetadata.variable.Variable;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
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

    private static final String OTHER_SENSORS_ELEMENT_NAME = "Other_Sensors";
    // <Other_Sensors> contains mutliple <Sensor> elements, each with with <Manufacturer>, <Model>, <Accuracy> or <Uncertainty>,
    // <Precision> or <Resolution>, <Calibration>, and <Other_Comments> elements
    private static final String SENSOR_ELEMENT_NAME = OTHER_SENSORS_ELEMENT_NAME + "\t" + "Sensor";

    private static final String DATA_SET_REFS_ELEMENT_NAME = "Data_set_References";
    private static final String ADD_INFO_ELEMENT_NAME = "Additional_Information";
    private static final String CITATION_ELEMENT_NAME = "Citation";

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
        Document omeDoc;
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

    /**
     * @return an SDIMetadata object populated with information found in this CDIAC XML file; never null
     */
    public SDIMetadata createSDIMetadata() {
        SDIMetadata mdata = new SDIMetadata();
        MiscInfo misc = getMiscInfo();
        mdata.setMiscInfo(misc);
        String datasetId = misc.getDatasetId();
        mdata.setSubmitter(getSubmitter());
        mdata.setInvestigators(getInvestigators());
        mdata.setPlatform(getPlatform(datasetId));
        mdata.setCoverage(getCoverage());
        mdata.setVariables(getVariables());
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

        ArrayList<String> addnInfo = getListOfLines(getElementText(ADD_INFO_ELEMENT_NAME));
        String text = getElementText(MOORING_ID_ELEMENT_NAME);
        if ( !text.isEmpty() )
            addnInfo.add(0, "Mooring ID: " + text);
        text = getElementText(SUB_CRUISE_INFO_ELEMENT_NAME);
        if ( !text.isEmpty() )
            addnInfo.add(0, "Cruise Info: " + text);
        text = getElementText(EXPERIMENT_TYPE_ELEMENT_NAME);
        if ( !text.isEmpty() )
            addnInfo.add(0, "Experiment Type: " + text);
        info.setAddnInfo(addnInfo);

        // TODO: references, citation, URL, link note
        return info;
    }

    /**
     * @return information about the metadata/dataset submitter read from this CDIAC XML file; never null
     */
    private Submitter getSubmitter() {
        Submitter submitter = new Submitter(getPersonNames(getElementText(USER_NAME_ELEMENT_NAME)));
        submitter.setStreets(getListOfLines(getElementText(USER_ADDRESS_ELEMENT_NAME)));
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
        String type = getElementText(PLATFORM_TYPE_ELEMENT_NAME);
        if ( type.isEmpty() )
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

    private ArrayList<Variable> getVariables() {
        ArrayList<Variable> varList = new ArrayList<Variable>();
        for (Element varElem : getElementList(VARIABLE_ELEMENT_NAME)) {
            Variable var = new Variable();
            var.setColName(varElem.getChildTextTrim(VARIABLES_NAME_ELEMENT_NAME));
            var.setFullName(varElem.getChildTextTrim(VARIABLES_DESCRIPTION_ELEMENT_NAME));
            var.setVarUnit(varElem.getChildTextTrim(VARIABLES_UNIT_OF_VARIABLE_ELEMENT_NAME));

            // TODO:
            // var.setAccuracy();
            // var.setPrecision();
            // var.setFlagColName();
            // var.setDryingMethod();
            // var.setAddnInfo();
            // DataVar - setSamplingLocation, setSamplingElevation, ....
            // AirPressure - DataVar + setPressureCorrection
            // GasConc - DataVar + setDryingMethod, setWaterVaporCorrection
            // AquGasConc - GasConc + setReportTemperature, setTemperatureCorrection

            varList.add(var);
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
        teqSensor.setAddnInfo(getListOfLines(getElementText(EQT_COMMENTS_ELEMENT_NAME)));
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

        // TODO:

        return sensors;
    }

}
