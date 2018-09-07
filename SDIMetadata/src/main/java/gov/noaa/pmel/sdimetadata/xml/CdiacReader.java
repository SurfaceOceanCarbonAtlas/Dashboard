package gov.noaa.pmel.sdimetadata.xml;

import gov.noaa.pmel.sdimetadata.Coverage;
import gov.noaa.pmel.sdimetadata.MiscInfo;
import gov.noaa.pmel.sdimetadata.Platform;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.instrument.Analyzer;
import gov.noaa.pmel.sdimetadata.instrument.Sampler;
import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Person;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
import gov.noaa.pmel.sdimetadata.variable.Variable;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class CdiacReader {

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
    private static final String SENSOR_ELEMENT_NAME = "Sensor";

    private static final String DATA_SET_REFS_ELEMENT_NAME = "Data_set_References";
    private static final String ADD_INFO_ELEMENT_NAME = "Additional_Information";
    private static final String CITATION_ELEMENT_NAME = "Citation";
    private static final String MEAS_CALIB_REPORT_ELEMENT_NAME = "Measurement_and_Calibration_Report";
    private static final String PRELIM_QC_ELEMENT_NAME = "Preliminary_Quality_control";

    private static final String FORM_TYPE_ELEMENT_NAME = "form_type";

    private static final SimpleDateFormat DATE_NUMBER_PARSER = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat DATE_SLASH_PARSER = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    static {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        DATE_NUMBER_PARSER.setTimeZone(utc);
        DATE_NUMBER_PARSER.setLenient(false);
        DATE_SLASH_PARSER.setTimeZone(utc);
        DATE_SLASH_PARSER.setLenient(false);
        DATE_FORMATTER.setTimeZone(utc);
        DATE_FORMATTER.setLenient(false);
    }

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
        mdata.setMiscInfo(getMiscInfo());
        mdata.setSubmitter(getSubmitter());
        mdata.setInvestigators(getInvestigators());
        mdata.setPlatform(getPlatform());
        mdata.setCoverage(getCoverage());
        mdata.setVariables(getVariables());
        mdata.setSamplers(getSamplers());
        mdata.setAnalyzers(getAnalyzers());
        return mdata;
    }

    /**
     * Get the list of all child elements matching a name path
     *
     * @param fullElementListName
     *         tab-seperated names giving the path from the root element to the desired elements; cannot be null
     *
     * @return list of all child elements matching the name path;
     *         null is returned if no elements matching the path are found
     */
    private List<Element> getElementList(String fullElementListName) {
        Element elem = rootElement;
        String[] names = fullElementListName.split("\t");
        for (int k = 0; k < names.length - 1; k++) {
            elem = elem.getChild(names[k]);
            if ( null == elem )
                return null;
        }
        return elem.getChildren(names[names.length - 1]);
    }

    /**
     * Get the text from a specified element under the root element.
     *
     * @param fullElementName
     *         tab-seperated names giving the path from the root element
     *         to the element containing the text; cannot be null
     *
     * @return trimmed text of the specified element;
     *         null is returned if the element is not found or if the element does not contain text
     */
    private String getElementText(String fullElementName) {
        Element elem = rootElement;
        for (String name : fullElementName.split("\t")) {
            elem = elem.getChild(name);
            if ( null == elem )
                return null;
        }
        return elem.getTextTrim();
    }

    /**
     * Determine the lastName, firstName, and middle fields from a given full name.
     *
     * @param fullname
     *         the full name of the person; if null, all fields in the returned Person will be empty
     *
     * @return a Person with just the lastName, firstName and middle fields assigned; never null but fields may be empty
     */
    private Person getPersonNames(String fullname) {
        Person person = new Person();
        if ( fullname != null ) {
            String[] pieces = fullname.split(" ");
            if ( pieces.length > 0 ) {
                if ( pieces[0].endsWith("'") || pieces[0].endsWith(";") ) {
                    person.setLastName(pieces[0].substring(0, pieces[0].length() - 1));
                    if ( pieces.length > 1 )
                        person.setFirstName(pieces[pieces.length - 1]);
                    String middle = "";
                    for (int k = 2; k < pieces.length; k++) {
                        middle += pieces[k - 1];
                    }
                    person.setMiddle(middle);
                }
                else if ( pieces.length > 1 ) {
                    person.setFirstName(pieces[0]);
                    person.setLastName(pieces[pieces.length - 1]);
                    String middle = "";
                    for (int k = 2; k < pieces.length; k++) {
                        middle += pieces[k - 1];
                    }
                    person.setMiddle(middle);
                }
                else {
                    person.setLastName(pieces[0]);
                }
            }
        }
        return person;
    }

    /**
     * @param address
     *         full street address containing line breaks to separate street address lines;
     *         if null, an empty list is returned
     *
     * @return list of trimmed street address lines; never null but may be empty
     */
    private ArrayList<String> getStreetList(String address) {
        ArrayList<String> streets = new ArrayList<String>();
        if ( address != null ) {
            for (String val : address.split("\n\r")) {
                val = val.trim();
                if ( !val.isEmpty() )
                    streets.add(val);
            }
        }
        return streets;
    }


    /**
     * @param datestring
     *         date stamp as yyyyMMdd or yyyy/MM/dd or yyyy-MM-dd; if null or empty, null is returned
     *
     * @return datestamp representing this date, or null if the date string is invalid
     */
    private Datestamp getDatestamp(String datestring) {
        if ( (null == datestring) || datestring.isEmpty() )
            return null;
        String hypenstring;
        try {
            // Convert yyyyMMdd to yyyy-MM-dd, checking if valid
            hypenstring = DATE_FORMATTER.format(DATE_NUMBER_PARSER.parse(datestring));
        } catch ( ParseException ex ) {
            hypenstring = null;
        }
        if ( null == hypenstring ) {
            // Convert yyyy/MM/dd to yyyy-MM-dd, checking if valid
            try {
                hypenstring = DATE_FORMATTER.format(DATE_SLASH_PARSER.parse(datestring));
            } catch ( ParseException ex ) {
                // leave hypenstring as null
            }
        }
        if ( null == hypenstring ) {
            // Check if given as yyyy-MM-dd and is valid
            try {
                hypenstring = DATE_FORMATTER.format(DATE_FORMATTER.parse(datestring));
            } catch ( ParseException ex ) {
                // leave hypenstring as null
            }
        }
        if ( null == hypenstring )
            return null;

        String[] pieces = hypenstring.split("-");
        if ( pieces.length != 3 )
            throw new RuntimeException("Unexpected hyphenated date of: " + hypenstring);
        return new Datestamp(Integer.valueOf(pieces[0]), Integer.valueOf(pieces[1]), Integer.valueOf(pieces[2]));
    }

    /**
     * @return the miscellaneous information read from this CDIAC XML file; never null
     */
    private MiscInfo getMiscInfo() {
        MiscInfo info = new MiscInfo();

        // Dataset ID / Expocode
        String expocode = getElementText(EXPOCODE_ELEMENT_NAME);
        if ( null == expocode )
            expocode = getElementText(CRUISE_ID_ELEMENT_NAME);
        if ( expocode != null )
            info.setDatasetId(expocode);

        // Funding information all glummed together in CDIAC XML - stick under agency name
        info.setFundingAgency(getElementText(FUNDING_INFO_ELEMENT_NAME));

        ArrayList<Datestamp> history = new ArrayList<Datestamp>();
        Datestamp stamp = getDatestamp(getElementText(INITIAL_SUBMISSION_ELEMENT_NAME));
        if ( stamp != null )
            history.add(stamp);
        List<Element> elemList = getElementList(REVISED_SUBMISSION_ELEMENT_NAME);
        if ( elemList != null ) {
            for (Element elem : elemList) {
                stamp = getDatestamp(elem.getTextTrim());
                if ( (stamp != null) && !elemList.contains(elem) )
                    elemList.add(elem);
            }
        }
        info.setHistory(history);

        // TODO:

        return info;
    }

    /**
     * @return information about the metadata/dataset submitter read from this CDIAC XML file; never null
     */
    private Submitter getSubmitter() {
        Submitter submitter = new Submitter(getPersonNames(getElementText(USER_NAME_ELEMENT_NAME)));
        submitter.setStreets(getStreetList(getElementText(USER_ADDRESS_ELEMENT_NAME)));
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
        List<Element> invList = getElementList(INVESTIGATOR_ELEMENT_NAME);
        if ( invList != null ) {
            for (Element inv : invList) {
                Investigator pi = new Investigator(getPersonNames(inv.getChildTextTrim(NAME_ELEMENT_NAME)));
                pi.setStreets(getStreetList(inv.getChildTextTrim(ADDRESS_ELEMENT_NAME)));
                pi.setOrganization(inv.getChildTextTrim(ORGANIZATION_ELEMENT_NAME));
                pi.setPhone(inv.getChildTextTrim(PHONE_ELEMENT_NAME));
                pi.setEmail(inv.getChildTextTrim(EMAIL_ELEMENT_NAME));
                // CDIAC XML does not have the ID or ID type
                piList.add(pi);
            }
        }
        return piList;
    }

    private Platform getPlatform() {
        Platform platform = new Platform();

        // TODO:

        return platform;
    }

    private Coverage getCoverage() {
        Coverage coverage = new Coverage();

        // TODO:

        return coverage;
    }

    private ArrayList<Variable> getVariables() {
        ArrayList<Variable> vars = new ArrayList<Variable>();

        // TODO:

        return vars;
    }

    private ArrayList<Sampler> getSamplers() {
        ArrayList<Sampler> equilibrators = new ArrayList<Sampler>(1);

        // TODO:

        return equilibrators;
    }

    private ArrayList<Analyzer> getAnalyzers() {
        ArrayList<Analyzer> sensors = new ArrayList<Analyzer>();

        // TODO:

        return sensors;
    }

}
