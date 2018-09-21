package gov.noaa.pmel.sdimetadata.xml;

import gov.noaa.pmel.sdimetadata.Coverage;
import gov.noaa.pmel.sdimetadata.MiscInfo;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.instrument.Analyzer;
import gov.noaa.pmel.sdimetadata.instrument.Equilibrator;
import gov.noaa.pmel.sdimetadata.instrument.Sampler;
import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Person;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import gov.noaa.pmel.sdimetadata.platform.Platform;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
import gov.noaa.pmel.sdimetadata.variable.AirPressure;
import gov.noaa.pmel.sdimetadata.variable.AquGasConc;
import gov.noaa.pmel.sdimetadata.variable.DataVar;
import gov.noaa.pmel.sdimetadata.variable.GasConc;
import gov.noaa.pmel.sdimetadata.variable.MethodType;
import gov.noaa.pmel.sdimetadata.variable.Variable;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;

public class OcadsWriter extends DocumentHandler {

    private static final String ACCESS_ID_ELEMENT_NAME = "related" + SEP + "name";
    private static final String SUBMISSION_DATE_ELEMENT_NAME = "submissiondate";
    private static final String UPDATE_DATE_ELEMENT_NAME = "update";

    private static final String NAME_ELEMENT_NAME = "name";
    private static final String ORG_ELEMENT_NAME = "organization";
    private static final String FIRST_STREET_ELEMENT_NAME = "deliverypoint1";
    private static final String SECOND_STREET_ELEMENT_NAME = "deliverypoint2";
    private static final String CITY_ELEMENT_NAME = "city";
    private static final String REGION_ELEMENT_NAME = "administrativeArea";
    private static final String ZIP_ELEMENT_NAME = "zip";
    private static final String COUNTRY_ELEMENT_NAME = "country";
    private static final String EMAIL_ELEMENT_NAME = "email";
    private static final String PHONE_ELEMENT_NAME = "phone";
    private static final String ID_ELEMENT_NAME = "ID";
    private static final String ID_TYPE_ELEMENT_NAME = "IDtype";

    private static final String SUBMITTER_ELEMENT_NAME = "datasubmitter";
    private static final String SUBMITTER_NAME_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + NAME_ELEMENT_NAME;
    private static final String SUBMITTER_ORG_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + ORG_ELEMENT_NAME;
    private static final String SUBMITTER_FIRST_STREET_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + FIRST_STREET_ELEMENT_NAME;
    private static final String SUBMITTER_SECOND_STREET_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + SECOND_STREET_ELEMENT_NAME;
    private static final String SUBMITTER_CITY_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + CITY_ELEMENT_NAME;
    private static final String SUBMITTER_REGION_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + REGION_ELEMENT_NAME;
    private static final String SUBMITTER_ZIP_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + ZIP_ELEMENT_NAME;
    private static final String SUBMITTER_COUNTRY_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + COUNTRY_ELEMENT_NAME;
    private static final String SUBMITTER_EMAIL_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + EMAIL_ELEMENT_NAME;
    private static final String SUBMITTER_PHONE_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + PHONE_ELEMENT_NAME;
    private static final String SUBMITTER_ID_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + ID_ELEMENT_NAME;
    private static final String SUBMITTER_ID_TYPE_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + ID_TYPE_ELEMENT_NAME;

    private static final String INVESTIGATOR_ELEMENT_NAME = "person";
    private static final String INVESTIGATOR_ROLE_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + "role";
    private static final String INVESTIGATOR_NAME_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + NAME_ELEMENT_NAME;
    private static final String INVESTIGATOR_ORG_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + ORG_ELEMENT_NAME;
    private static final String INVESTIGATOR_FIRST_STREET_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + FIRST_STREET_ELEMENT_NAME;
    private static final String INVESTIGATOR_SECOND_STREET_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + SECOND_STREET_ELEMENT_NAME;
    private static final String INVESTIGATOR_CITY_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + CITY_ELEMENT_NAME;
    private static final String INVESTIGATOR_REGION_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + REGION_ELEMENT_NAME;
    private static final String INVESTIGATOR_ZIP_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + ZIP_ELEMENT_NAME;
    private static final String INVESTIGATOR_COUNTRY_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + COUNTRY_ELEMENT_NAME;
    private static final String INVESTIGATOR_EMAIL_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + EMAIL_ELEMENT_NAME;
    private static final String INVESTIGATOR_PHONE_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + PHONE_ELEMENT_NAME;
    private static final String INVESTIGATOR_ID_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + ID_ELEMENT_NAME;
    private static final String INVESTIGATOR_ID_TYPE_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + ID_TYPE_ELEMENT_NAME;

    // private static final String TITLE_ELEMENT_NAME = "title";
    private static final String SYNOPSIS_ELEMENT_NAME = "abstract";
    private static final String PURPOSE_ELEMENT_NAME = "purpose";
    private static final String RESEARCH_PROJECT_ELEMENT_NAME = "researchProject";

    private static final String FUNDING_AGENCY_ELEMENT_NAME = "fundingAgency";
    private static final String FUNDING_AGENCY_NAME_ELEMENT_NAME = FUNDING_AGENCY_ELEMENT_NAME + SEP + "agency";
    private static final String FUNDING_AGENCY_TITLE_ELEMENT_NAME = FUNDING_AGENCY_ELEMENT_NAME + SEP + "title";
    private static final String FUNDING_AGENCY_ID_ELEMENT_NAME = FUNDING_AGENCY_ELEMENT_NAME + SEP + "ID";

    private static final String DATASET_ID_ELEMENT_NAME = "expocode";
    private static final String DATASET_NAME_ELEMENT_NAME = "cruiseID";
    private static final String SECTION_NAME_ELEMENT_NAME = "section";

    private static final String CITATION_ELEMENT_NAME = "citation";
    private static final String REFERENCE_ELEMENT_NAME = "reference";

    private static final String ADDN_INFO_ELEMENT_NAME = "suppleInfo";
    private static final String WEBSITE_ELEMENT_NAME = "link_landing";
    private static final String DOWNLOAD_URL_ELEMENT_NAME = "link_download";

    private static final String DATA_START_DATE_ELEMENT_NAME = "startdate";
    private static final String DATA_END_DATE_ELEMENT_NAME = "enddate";
    private static final String WESTERNMOST_LONGITUDE_ELEMENT_NAME = "westbd";
    private static final String EASTERNMOST_LONGITUDE_ELEMENT_NAME = "eastbd";
    private static final String SOUTHERNMOST_LATITUDE_ELEMENT_NAME = "southbd";
    private static final String NORTHERNMOST_LATITUDE_ELEMENT_NAME = "northbd";
    private static final String SPATIAL_REFERENCE_ELEMENT_NAME = "spatialReference";
    private static final String GEOGRAPHIC_NAME_ELEMENT_NAME = "geographicName";

    private static final String PLATFORM_ELEMENT_NAME = "Platform";
    private static final String PLATFORM_NAME_ELEMENT_NAME = PLATFORM_ELEMENT_NAME + SEP + "PlatformName";
    private static final String PLATFORM_ID_ELEMENT_NAME = PLATFORM_ELEMENT_NAME + SEP + "PlatformID";
    private static final String PLATFORM_TYPE_ELEMENT_NAME = PLATFORM_ELEMENT_NAME + SEP + "PlatformType";
    private static final String PLATFORM_OWNER_ELEMENT_NAME = PLATFORM_ELEMENT_NAME + SEP + "PlatformOwner";
    private static final String PLATFORM_COUNTRY_ELEMENT_NAME = PLATFORM_ELEMENT_NAME + SEP + "PlatformCountry";

    private static final String VARIABLE_ELEMENT_NAME = "variable";
    private static final String VARIABLE_COLUMN_NAME_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "abbrev";
    private static final String VARIABLE_FULL_NAME_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "fullname";
    private static final String VARIABLE_UNIT_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "unit";
    private static final String VARIABLE_OBS_TYPE_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "observationType";
    private static final String VARIABLE_IN_SITU_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "insitu";
    private static final String VARIABLE_MEASURED_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "measured";
    private static final String VARIABLE_CALC_METHOD_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "calcMethod";
    private static final String VARIABLE_SAMPLING_INST_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "samplingInstrument";
    private static final String VARIABLE_ANALYZING_INST_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "analyzingInstrument";
    private static final String VARIABLE_REPLICATE_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "replicate";
    private static final String VARIABLE_UNCERTAINTY_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "uncertainty";
    private static final String VARIABLE_FLAG_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "flag";
    private static final String VARIABLE_METHOD_REFERENCE_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "methodReference";
    private static final String VARIABLE_RESEARCHER_NAME_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "researcherName";
    private static final String VARIABLE_RESEARCHER_ORGANIZATION_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "researcherInstitution";
    private static final String VARIABLE_ADDN_INFO_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "detailedInfo";
    private static final String VARIABLE_INTERNAL_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "internal";

    private static final String VARIABLE_SAMPLING_LOCATION_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "locationSeawaterIntake";
    private static final String VARIABLE_SAMPLING_DEPTH_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "DepthSeawaterIntake";
    private static final String VARIABLE_WATER_VAPOR_CORRECTION_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "waterVaportCorrection";
    private static final String VARIABLE_TEMPERATURE_CORRECTION_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "temperatureCorrection";
    private static final String VARIABLE_REPORT_TEMPERATURE_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "co2ReportTemperature";

    private static final String VARIABLE_STORAGE_METHOD_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "storageMethod";
    private static final String VARIABLE_ANALYSIS_WATER_VOLUME_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "seawatervol";
    private static final String VARIABLE_ANALYSIS_HEADSPACE_VOLUME_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "headspacevol";
    private static final String VARIABLE_ANALYSIS_TEMPERATURE_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "temperatureMeasure";

    private static final String EQUILIBRATOR_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "equilibrator";
    private static final String EQUILIBRATOR_TYPE_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "type";
    private static final String EQUILIBRATOR_VOLUME_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "volume";
    private static final String EQUILIBRATOR_VENTED_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "vented";
    private static final String EQUILIBRATOR_WATER_FLOW_RATE_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "waterFlowRate";
    private static final String EQUILIBRATOR_GAS_FLOW_RATE_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "gasFlowRate";
    private static final String EQUILIBRATOR_TEMPERATURE_EQUI_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "temperatureEquilibratorMethod";
    private static final String EQUILIBRATOR_PRESSURE_EQUI_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "pressureEquilibratorMethod";
    private static final String EQUILIBRATOR_DRYING_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "dryMethod";

    private static final String GAS_SENSOR_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "gasDetector";
    private static final String GAS_SENSOR_MANUFACTURER_ELEMENT_NAME = GAS_SENSOR_ELEMENT_NAME + SEP + "manufacturer";
    private static final String GAS_SENSOR_MODEL_ELEMENT_NAME = GAS_SENSOR_ELEMENT_NAME + SEP + "model";
    private static final String GAS_SENSOR_RESOLUTION_ELEMENT_NAME = GAS_SENSOR_ELEMENT_NAME + SEP + "resolution";
    private static final String GAS_SENSOR_UNCERTAINTY_ELEMENT_NAME = GAS_SENSOR_ELEMENT_NAME + SEP + "uncertainty";

    private static final String STANDARDIZATION_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "standardization";
    private static final String STANDARDIZATION_DESCRIPTION_ELEMENT_NAME = STANDARDIZATION_ELEMENT_NAME + SEP + "description";
    private static final String STANDARDIZATION_FREQUENCY_ELEMENT_NAME = STANDARDIZATION_ELEMENT_NAME + SEP + "frequency";

    private static final String STANDARD_GAS_ELEMENT_NAME = STANDARDIZATION_ELEMENT_NAME + SEP + "standardgas";
    private static final String STANDARD_GAS_MANUFACTURER_ELEMENT_NAME = STANDARD_GAS_ELEMENT_NAME + SEP + "manufacturer";
    private static final String STANDARD_GAS_CONCENTRATION_ELEMENT_NAME = STANDARD_GAS_ELEMENT_NAME + SEP + "concentration";
    private static final String STANDARD_GAS_UNCERTAINTY_ELEMENT_NAME = STANDARD_GAS_ELEMENT_NAME + SEP + "uncertainty";

    /**
     * Create a new document containing only the root element for OCADS XML content
     */
    public OcadsWriter() {
        rootElement = new Element("metadata");
    }

    /**
     * Write the contents of the given metadata in OCADS XML to the given writer.
     *
     * @param mdata
     *         write the contents of this metadata
     * @param xmlWriter
     *         write OCADS XML to this writer
     *
     * @throws IOException
     *         if writing to the given writer throws one
     */
    public void writeSDIMetadata(SDIMetadata mdata, Writer xmlWriter) throws IOException {
        MiscInfo info = mdata.getMiscInfo();
        setElementText(null, ACCESS_ID_ELEMENT_NAME, info.getAccessId());
        ArrayList<Datestamp> history = info.getHistory();
        if ( history.size() > 0 )
            setElementText(null, SUBMISSION_DATE_ELEMENT_NAME, history.get(0).stampString());
        for (int k = 1; k < history.size(); k++) {
            Element elem = addListElement(null, UPDATE_DATE_ELEMENT_NAME);
            elem.setText(history.get(k).stampString());
        }

        addInvestigatorFields(null, mdata.getSubmitter());
        for (Investigator pi : mdata.getInvestigators()) {
            Element ancestor = addListElement(null, INVESTIGATOR_ELEMENT_NAME);
            addInvestigatorFields(ancestor, pi);
        }

        // setElementText(null, TITLE_ELEMENT_NAME, ?
        setElementText(null, SYNOPSIS_ELEMENT_NAME, info.getSynopsis());
        setElementText(null, PURPOSE_ELEMENT_NAME, info.getPurpose());

        Coverage coverage = mdata.getCoverage();
        Datestamp stamp = DocumentHandler.getDatestamp(coverage.getEarliestDataTime());
        setElementText(null, DATA_START_DATE_ELEMENT_NAME, stamp.stampString());
        stamp = DocumentHandler.getDatestamp(coverage.getLatestDataTime());
        setElementText(null, DATA_END_DATE_ELEMENT_NAME, stamp.stampString());
        setElementText(null, WESTERNMOST_LONGITUDE_ELEMENT_NAME, coverage.getWesternLongitude().getValueString());
        setElementText(null, EASTERNMOST_LONGITUDE_ELEMENT_NAME, coverage.getEasternLongitude().getValueString());
        setElementText(null, SOUTHERNMOST_LATITUDE_ELEMENT_NAME, coverage.getSouthernLatitude().getValueString());
        setElementText(null, NORTHERNMOST_LATITUDE_ELEMENT_NAME, coverage.getNorthernLatitude().getValueString());
        setElementText(null, SPATIAL_REFERENCE_ELEMENT_NAME, coverage.getSpatialReference());
        for (String region : coverage.getGeographicNames()) {
            addListElement(null, GEOGRAPHIC_NAME_ELEMENT_NAME).setText(region);
        }

        setElementText(null, FUNDING_AGENCY_NAME_ELEMENT_NAME, info.getFundingAgency());
        setElementText(null, FUNDING_AGENCY_TITLE_ELEMENT_NAME, info.getFundingTitle());
        setElementText(null, FUNDING_AGENCY_ID_ELEMENT_NAME, info.getFundingId());
        setElementText(null, RESEARCH_PROJECT_ELEMENT_NAME, info.getResearchProject());

        Platform platform = mdata.getPlatform();
        setElementText(null, PLATFORM_NAME_ELEMENT_NAME, platform.getPlatformName());
        setElementText(null, PLATFORM_ID_ELEMENT_NAME, platform.getPlatformId());
        setElementText(null, PLATFORM_TYPE_ELEMENT_NAME, platform.getPlatformType().toString());
        setElementText(null, PLATFORM_OWNER_ELEMENT_NAME, platform.getPlatformOwner());
        setElementText(null, PLATFORM_COUNTRY_ELEMENT_NAME, platform.getPlatformCountry());

        setElementText(null, DATASET_ID_ELEMENT_NAME, info.getDatasetId());
        setElementText(null, DATASET_NAME_ELEMENT_NAME, info.getDatasetName());
        setElementText(null, SECTION_NAME_ELEMENT_NAME, info.getSectionName());

        setElementText(null, CITATION_ELEMENT_NAME, info.getCitation());

        StringBuilder strBldr = new StringBuilder();
        for (String ref : info.getReferences()) {
            if ( strBldr.length() > 0 )
                strBldr.append("\n");
            strBldr.append(ref);
        }
        setElementText(null, REFERENCE_ELEMENT_NAME, strBldr.toString());

        strBldr = new StringBuilder();
        for (String port : info.getPortsOfCall()) {
            if ( strBldr.length() > 0 )
                strBldr.append("\n");
            strBldr.append("Port of Call: ");
            strBldr.append(port);

        }
        for (String addn : info.getAddnInfo()) {
            if ( strBldr.length() > 0 )
                strBldr.append("\n");
            strBldr.append(addn);
        }
        setElementText(null, ADDN_INFO_ELEMENT_NAME, strBldr.toString());

        setElementText(null, WEBSITE_ELEMENT_NAME, info.getWebsite());
        setElementText(null, DOWNLOAD_URL_ELEMENT_NAME, info.getDownloadUrl());

        ArrayList<Sampler> samplers = mdata.getSamplers();
        ArrayList<Analyzer> sensors = mdata.getAnalyzers();
        for (Variable var : mdata.getVariables()) {
            Element ancestor = addListElement(null, VARIABLE_ELEMENT_NAME);
            addVariableFields(ancestor, var);
            if ( var instanceof DataVar )
                addDataVariableAddnFields(ancestor, (DataVar) var, samplers, sensors);
            if ( var instanceof AirPressure )
                addAirPressureAddnFields(ancestor, (AirPressure) var);
            if ( var instanceof GasConc )
                addGasConcAddnFields(ancestor, (GasConc) var);
            if ( var instanceof AquGasConc )
                addAquGasConcAddnFields(ancestor, (AquGasConc) var);
        }

        Document doc = new Document(rootElement);
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(doc, xmlWriter);
    }

    /**
     * Add the OCADS XML for this Investigator.  If the Investigator given is
     * an instance of the subclass Submitter, Submitter tags are used instead of Investigator tags.
     *
     * @param ancestor
     *         add under this element; if a Submitter, this should be null
     * @param pi
     *         use the information from this investigator or submitter
     */
    private void addInvestigatorFields(Element ancestor, Investigator pi) {
        boolean issubmitter;
        if ( pi instanceof Submitter ) {
            issubmitter = true;
        }
        else {
            issubmitter = false;
            setElementText(ancestor, INVESTIGATOR_ROLE_ELEMENT_NAME, "investigator");
        }
        String strVal = pi.getFirstName() + " " + pi.getMiddle();
        strVal = strVal.trim() + " " + pi.getLastName();
        setElementText(ancestor, issubmitter ? SUBMITTER_NAME_ELEMENT_NAME : INVESTIGATOR_NAME_ELEMENT_NAME,
                strVal);
        setElementText(ancestor, issubmitter ? SUBMITTER_ORG_ELEMENT_NAME : INVESTIGATOR_ORG_ELEMENT_NAME,
                pi.getOrganization());
        ArrayList<String> strList = pi.getStreets();
        if ( strList.size() > 0 )
            setElementText(ancestor,
                    issubmitter ? SUBMITTER_FIRST_STREET_ELEMENT_NAME : INVESTIGATOR_FIRST_STREET_ELEMENT_NAME,
                    strList.get(0));
        if ( strList.size() > 1 ) {
            strVal = strList.get(1);
            for (int k = 2; k < strList.size(); k++) {
                strVal += "\n" + strList.get(k);
            }
            setElementText(ancestor,
                    issubmitter ? SUBMITTER_SECOND_STREET_ELEMENT_NAME : INVESTIGATOR_SECOND_STREET_ELEMENT_NAME,
                    strVal);
        }
        setElementText(ancestor, issubmitter ? SUBMITTER_CITY_ELEMENT_NAME : INVESTIGATOR_CITY_ELEMENT_NAME,
                pi.getCity());
        setElementText(ancestor, issubmitter ? SUBMITTER_REGION_ELEMENT_NAME : INVESTIGATOR_REGION_ELEMENT_NAME,
                pi.getRegion());
        setElementText(ancestor, issubmitter ? SUBMITTER_ZIP_ELEMENT_NAME : INVESTIGATOR_ZIP_ELEMENT_NAME,
                pi.getZipCode());
        setElementText(ancestor, issubmitter ? SUBMITTER_COUNTRY_ELEMENT_NAME : INVESTIGATOR_COUNTRY_ELEMENT_NAME,
                pi.getCountry());
        setElementText(ancestor, issubmitter ? SUBMITTER_EMAIL_ELEMENT_NAME : INVESTIGATOR_EMAIL_ELEMENT_NAME,
                pi.getEmail());
        setElementText(ancestor, issubmitter ? SUBMITTER_PHONE_ELEMENT_NAME : INVESTIGATOR_PHONE_ELEMENT_NAME,
                pi.getPhone());
        setElementText(ancestor, issubmitter ? SUBMITTER_ID_ELEMENT_NAME : INVESTIGATOR_ID_ELEMENT_NAME,
                pi.getId());
        setElementText(ancestor, issubmitter ? SUBMITTER_ID_TYPE_ELEMENT_NAME : INVESTIGATOR_ID_TYPE_ELEMENT_NAME,
                pi.getIdType());
    }

    /**
     * Add the OCADS XML for the fields found in Variable
     *
     * @param ancestor
     *         add under this element
     * @param var
     *         use the information given in this variable
     */
    private void addVariableFields(Element ancestor, Variable var) {
        setElementText(ancestor, VARIABLE_COLUMN_NAME_ELEMENT_NAME, var.getColName());
        setElementText(ancestor, VARIABLE_FULL_NAME_ELEMENT_NAME, var.getFullName());
        setElementText(ancestor, VARIABLE_UNIT_ELEMENT_NAME, var.getVarUnit());
        setElementText(ancestor, VARIABLE_UNCERTAINTY_ELEMENT_NAME, var.getAccuracy().asOneString());
        String strVal = var.getFlagColName();
        if ( !strVal.isEmpty() )
            setElementText(ancestor, VARIABLE_FLAG_ELEMENT_NAME, "Given in column: " + strVal);

        StringBuilder strBldr = new StringBuilder();
        strVal = var.getMissVal();
        if ( !strVal.isEmpty() ) {
            if ( strBldr.length() > 0 )
                strBldr.append("\n");
            strBldr.append("Missing Value: ");
            strBldr.append(strVal);
        }
        strVal = var.getPrecision().asOneString();
        if ( !strVal.isEmpty() ) {
            if ( strBldr.length() > 0 )
                strBldr.append("\n");
            strBldr.append("Resolution/Precision: ");
            strBldr.append(strVal);
        }
        for (String addn : var.getAddnInfo()) {
            if ( strBldr.length() > 0 )
                strBldr.append("\n");
            strBldr.append(addn);
        }
        setElementText(ancestor, VARIABLE_ADDN_INFO_ELEMENT_NAME, strBldr.toString());
    }

    /**
     * Add the OCADS XML for the additional fields found in DataVar
     *
     * @param ancestor
     *         add under this element
     * @param var
     *         use the information given in this data variable
     * @param samplers
     *         list of samplers used in this dataset
     * @param sensors
     *         list of analyzers used in this dataset
     */
    private void addDataVariableAddnFields(Element ancestor, DataVar var,
            ArrayList<Sampler> samplers, ArrayList<Analyzer> sensors) {
        setElementText(ancestor, VARIABLE_OBS_TYPE_ELEMENT_NAME, var.getObserveType());
        switch ( var.getMeasureMethod() ) {
            case MEASURED_INSITU:
                setElementText(ancestor, VARIABLE_IN_SITU_ELEMENT_NAME, "Measured in-situ");
                setElementText(ancestor, VARIABLE_MEASURED_ELEMENT_NAME, "Measured in-situ");
                break;
            case MEASURED_DISCRETE:
                setElementText(ancestor, VARIABLE_IN_SITU_ELEMENT_NAME, "Measured from collected sample");
                setElementText(ancestor, VARIABLE_MEASURED_ELEMENT_NAME, "Measured from collected sample");
                break;
            case COMPUTED:
                setElementText(ancestor, VARIABLE_IN_SITU_ELEMENT_NAME, "Computed");
                setElementText(ancestor, VARIABLE_MEASURED_ELEMENT_NAME, "Computed");
                break;
            default:
        }
        setElementText(ancestor, VARIABLE_CALC_METHOD_ELEMENT_NAME, var.getMethodDescription());
        setElementText(ancestor, VARIABLE_METHOD_REFERENCE_ELEMENT_NAME, var.getMethodReference());

        HashSet<String> strSet = var.getSamplerNames();
        if ( !strSet.isEmpty() ) {
            for (Sampler inst : samplers) {
                if ( !strSet.contains(inst.getName()) )
                    continue;
                addSamplerElements(ancestor, var, inst);
            }
        }
        strSet = var.getAnalyzerNames();
        if ( !strSet.isEmpty() ) {
            for (Analyzer inst : sensors) {
                if ( !strSet.contains(inst.getName()) )
                    continue;
                addAnalyzerElements(ancestor, var, inst);
            }
        }

        if ( (var instanceof AquGasConc) && MethodType.MEASURED_INSITU.equals(var.getMeasureMethod()) ) {
            // These tags are only defined for "autonomous" (in-situ) aqueous CO2 measurements
            setElementText(ancestor, VARIABLE_SAMPLING_LOCATION_ELEMENT_NAME, var.getSamplingLocation());
            setElementText(ancestor, VARIABLE_SAMPLING_DEPTH_ELEMENT_NAME, var.getSamplingElevation());
        }
        else {
            String loc = var.getSamplingLocation();
            String elev = var.getSamplingElevation();
            if ( !(loc.isEmpty() && elev.isEmpty()) ) {
                String addnInfo = getElementText(ancestor, VARIABLE_ADDN_INFO_ELEMENT_NAME);
                StringBuilder strBldr = new StringBuilder();
                if ( !loc.isEmpty() ) {
                    strBldr.append("Sampling location: ");
                    strBldr.append(loc);
                }
                if ( !elev.isEmpty() ) {
                    if ( strBldr.length() > 0 )
                        strBldr.append("\n");
                    strBldr.append("Sampling elevation: ");
                    strBldr.append(elev);
                }
                if ( !addnInfo.isEmpty() ) {
                    strBldr.append("\n");
                    strBldr.append(addnInfo);
                }
                setElementText(ancestor, VARIABLE_ADDN_INFO_ELEMENT_NAME, strBldr.toString());
            }
        }

        // TODO:

        if ( (var instanceof AquGasConc) && MethodType.MEASURED_DISCRETE.equals(var.getMeasureMethod()) ) {
            // These tags are only defined for values from stored samples for CO2 and pH - but not doing pH at this time
            setElementText(ancestor, VARIABLE_STORAGE_METHOD_ELEMENT_NAME, var.getStorageMethod());
            setElementText(ancestor, VARIABLE_ANALYSIS_TEMPERATURE_ELEMENT_NAME, var.getAnalysisTemperature());
        }
        else {
            String store = var.getStorageMethod();
            String mtemp = var.getAnalysisTemperature();
            if ( !(store.isEmpty() && mtemp.isEmpty()) ) {
                String addnInfo = getElementText(ancestor, VARIABLE_ADDN_INFO_ELEMENT_NAME);
                StringBuilder strBldr = new StringBuilder();
                if ( !store.isEmpty() ) {
                    strBldr.append("Storage Method: ");
                    strBldr.append(store);
                }
                if ( !mtemp.isEmpty() ) {
                    if ( strBldr.length() > 0 )
                        strBldr.append("\n");
                    strBldr.append("Measurement Temperature: ");
                    strBldr.append(mtemp);
                }
                if ( !addnInfo.isEmpty() ) {
                    strBldr.append("\n");
                    strBldr.append(addnInfo);
                }
                setElementText(ancestor, VARIABLE_ADDN_INFO_ELEMENT_NAME, strBldr.toString());
            }
        }
        setElementText(ancestor, VARIABLE_REPLICATE_ELEMENT_NAME, var.getReplication());
        Person pi = var.getResearcher();
        String fullname = pi.getFirstName() + " " + pi.getMiddle();
        fullname = fullname.trim() + " " + pi.getLastName();
        setElementText(ancestor, VARIABLE_RESEARCHER_NAME_ELEMENT_NAME, fullname);
        setElementText(ancestor, VARIABLE_RESEARCHER_ORGANIZATION_ELEMENT_NAME, pi.getOrganization());

        if ( var instanceof AquGasConc ) {
            switch ( var.getMeasureMethod() ) {
                case MEASURED_INSITU:
                    setElementText(ancestor, VARIABLE_INTERNAL_ELEMENT_NAME, "4");
                    break;
                case MEASURED_DISCRETE:
                    setElementText(ancestor, VARIABLE_INTERNAL_ELEMENT_NAME, "5");
                    break;
                default:
                    setElementText(ancestor, VARIABLE_INTERNAL_ELEMENT_NAME, "0");
                    break;
            }
        }
        else {
            // Not handling DIC, TA, or pH at this time
            setElementText(ancestor, VARIABLE_INTERNAL_ELEMENT_NAME, "0");
        }
    }

    /**
     * Add the OCADS XML for the additional fields found in AirPressure
     *
     * @param ancestor
     *         add under this element
     * @param var
     *         use the information given in this air pressure variable
     */
    private void addAirPressureAddnFields(Element ancestor, AirPressure var) {
        String pressureCorrection = var.getPressureCorrection();
        if ( pressureCorrection.isEmpty() )
            return;
        String addnInfo = getElementText(ancestor, VARIABLE_ADDN_INFO_ELEMENT_NAME);
        StringBuilder strBldr = new StringBuilder();
        strBldr.append("Pressure Correction: ");
        strBldr.append(pressureCorrection);
        if ( !addnInfo.isEmpty() ) {
            strBldr.append("\n");
            strBldr.append(addnInfo);
        }
        setElementText(ancestor, VARIABLE_ADDN_INFO_ELEMENT_NAME, strBldr.toString());
    }

    /**
     * Add the OCADS XML for the additional fields found in GasConc
     *
     * @param ancestor
     *         add under this element
     * @param var
     *         use the information given in this gas concentration variable
     */
    private void addGasConcAddnFields(Element ancestor, GasConc var) {
        if ( (var instanceof AquGasConc) && MethodType.MEASURED_INSITU.equals(var.getMeasureMethod()) ) {
            // Only "autonomous" (in-situ) aqueous CO2 has these fields
            setElementText(ancestor, EQUILIBRATOR_DRYING_ELEMENT_NAME, var.getDryingMethod());
            setElementText(ancestor, VARIABLE_WATER_VAPOR_CORRECTION_ELEMENT_NAME, var.getWaterVaporCorrection());
        }
        else {
            String dryMethod = var.getDryingMethod();
            String waterVaporCorrection = var.getWaterVaporCorrection();
            if ( !(dryMethod.isEmpty() && waterVaporCorrection.isEmpty()) ) {
                String addnInfo = getElementText(ancestor, VARIABLE_ADDN_INFO_ELEMENT_NAME);
                StringBuilder strBldr = new StringBuilder();
                if ( !dryMethod.isEmpty() ) {
                    strBldr.append("Drying Method: ");
                    strBldr.append(dryMethod);
                }
                if ( !waterVaporCorrection.isEmpty() ) {
                    if ( strBldr.length() > 0 )
                        strBldr.append("\n");
                    strBldr.append("Water Vapor Correction: ");
                    strBldr.append(waterVaporCorrection);
                }
                if ( !addnInfo.isEmpty() ) {
                    strBldr.append("\n");
                    strBldr.append(addnInfo);
                }
                setElementText(ancestor, VARIABLE_ADDN_INFO_ELEMENT_NAME, strBldr.toString());
            }
        }
    }

    /**
     * Add the OCADS XML for the additional fields found in AquGasConc
     *
     * @param ancestor
     *         add under this element
     * @param var
     *         use the information given in this aqueous gas concentration
     */
    private void addAquGasConcAddnFields(Element ancestor, AquGasConc var) {
        setElementText(ancestor, VARIABLE_REPORT_TEMPERATURE_ELEMENT_NAME, var.getReportTemperature());
        setElementText(ancestor, VARIABLE_TEMPERATURE_CORRECTION_ELEMENT_NAME, var.getTemperatureCorrection());
    }

    /**
     * Add the OCADS XML describing the given sampling instrument.
     *
     * @param ancestor
     * add under this element
     * @param var
     * use the information given in the variable
     * @param inst
     */
    private void addSamplerElements(Element ancestor, DataVar var, Sampler inst) {
        if ( (var instanceof AquGasConc) && (inst instanceof Equilibrator) ) {
            // These tags are only available for aqueous CO2
            Equilibrator equil = (Equilibrator) inst;
            if ( MethodType.MEASURED_INSITU.equals(var.getMeasureMethod()) ) {
                // These tags are only available for "autonimous" (in-situ) aqueous CO2
                setElementText(ancestor, EQUILIBRATOR_TYPE_ELEMENT_NAME, equil.getEquilibratorType());
                StringBuilder strBldr = new StringBuilder();
                strBldr.append(equil.getChamberVol());
                String vol = equil.getChamberWaterVol();
                if ( !vol.isEmpty() ) {
                    if ( strBldr.length() > 0 )
                        strBldr.append("; ");
                    strBldr.append("Water Volume: ");
                    strBldr.append(vol);
                }
                vol = equil.getChamberGasVol();
                if ( !vol.isEmpty() ) {
                    if ( strBldr.length() > 0 )
                        strBldr.append("; ");
                    strBldr.append("Gas Volume: ");
                    strBldr.append(vol);

                }
                setElementText(ancestor, EQUILIBRATOR_VOLUME_ELEMENT_NAME, strBldr.toString());
                setElementText(ancestor, EQUILIBRATOR_VENTED_ELEMENT_NAME, equil.getVenting());
                setElementText(ancestor, EQUILIBRATOR_WATER_FLOW_RATE_ELEMENT_NAME, equil.getWaterFlowRate());
                setElementText(ancestor, EQUILIBRATOR_GAS_FLOW_RATE_ELEMENT_NAME, equil.getGasFlowRate());
                //TODO: equilibrator sensors
                // setElementText(ancestor, EQUILIBRATOR_TEMPERATURE_EQUI_ELEMENT_NAME, equil.???);
                // setElementText(ancestor, EQUILIBRATOR_PRESSURE_EQUI_ELEMENT_NAME, equil.???);
            }
            else if ( MethodType.MEASURED_DISCRETE.equals(var.getMeasureMethod()) ) {
                // These tags are only available for discrete sampled aqueous CO2
                String vol = equil.getChamberWaterVol();
                if ( vol.isEmpty() )
                    vol = "Water volume of: " + equil.getChamberVol();
                setElementText(ancestor, VARIABLE_ANALYSIS_WATER_VOLUME_ELEMENT_NAME, vol);
                vol = equil.getChamberGasVol();
                if ( vol.isEmpty() )
                    vol = "Gas volume of: " + equil.getChamberVol();
                setElementText(ancestor, VARIABLE_ANALYSIS_HEADSPACE_VOLUME_ELEMENT_NAME, vol);
            }
        }
        // Always describe everything under the generic sampling instrument tag
        StringBuilder strBldr = new StringBuilder();
        String str = inst.getManufacturer();
        if ( !str.isEmpty() ) {
            strBldr.append("Manufacturer: ");
            strBldr.append(str);
        }
        str = inst.getModel();
        if ( !str.isEmpty() ) {
            if ( strBldr.length() > 0 )
                strBldr.append("; ");
            strBldr.append("Model: ");
            strBldr.append(str);
        }
        str = inst.getId();
        if ( ! str.isEmpty() ) {
            if ( strBldr.length() > 0 )
                strBldr.append("; ");
            strBldr.append("ID/Serial: ");
            strBldr.append(str);
        }
        for (String addn : inst.getAddnInfo()) {
            if ( strBldr.length() > 0 )
                strBldr.append("; ");
            strBldr.append(addn);
        }
        String instDesc = strBldr.toString();

        strBldr = new StringBuilder();
        str = getElementText(ancestor, VARIABLE_SAMPLING_INST_ELEMENT_NAME);
        if ( !str.isEmpty() ) {
            strBldr.append(str);
            strBldr.append("\n");
        }
        strBldr.append(inst.getName());
        if ( ! instDesc.isEmpty() ) {
            strBldr.append(": ");
            strBldr.append(instDesc);
        }
        setElementText(ancestor, VARIABLE_SAMPLING_INST_ELEMENT_NAME, strBldr.toString());
    }

    private void addAnalyzerElements(Element ancestor, DataVar var, Analyzer inst) {
        // Always describe everything under the generic analyzing instrument tag
        StringBuilder strBldr = new StringBuilder();
        String str = inst.getManufacturer();
        if ( !str.isEmpty() ) {
            strBldr.append("Manufacturer: ");
            strBldr.append(str);
        }
        str = inst.getModel();
        if ( !str.isEmpty() ) {
            if ( strBldr.length() > 0 )
                strBldr.append("; ");
            strBldr.append("Model: ");
            strBldr.append(str);
        }
        str = inst.getId();
        if ( ! str.isEmpty() ) {
            if ( strBldr.length() > 0 )
                strBldr.append("; ");
            strBldr.append("ID/Serial: ");
            strBldr.append(str);
        }
        str = inst.getCalibration();
        if ( ! str.isEmpty() ) {
            if ( strBldr.length() > 0 )
                strBldr.append("; ");
            strBldr.append("Calibration: ");
            strBldr.append(str);
        }
        for (String addn : inst.getAddnInfo()) {
            if ( strBldr.length() > 0 )
                strBldr.append("; ");
            strBldr.append(addn);
        }
        String instDesc = strBldr.toString();

        strBldr = new StringBuilder();
        str = getElementText(ancestor, VARIABLE_ANALYZING_INST_ELEMENT_NAME);
        if ( !str.isEmpty() ) {
            strBldr.append(str);
            strBldr.append("\n");
        }
        strBldr.append(inst.getName());
        if ( ! instDesc.isEmpty() ) {
            strBldr.append(": ");
            strBldr.append(instDesc);
        }
        setElementText(ancestor, VARIABLE_ANALYZING_INST_ELEMENT_NAME, strBldr.toString());
    }

}

