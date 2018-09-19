package gov.noaa.pmel.sdimetadata.xml;

import gov.noaa.pmel.sdimetadata.Coverage;
import gov.noaa.pmel.sdimetadata.MiscInfo;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import gov.noaa.pmel.sdimetadata.platform.Platform;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class OcadsWriter extends DocumentHandler {

    private static final String ACCESS_ID_ELEMENT_NAME = "related" + SEP + "name";
    private static final String SUBMISSION_DATE_ELEMENT_NAME = "submissiondate";
    private static final String UPDATE_DATE_ELEMENT_NAME = "update";

    private static final String NAME_ELEMENT_NAME = "name";
    private static final String ORGANIZATION_ELEMENT_NAME = "organization";
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
    private static final String SUBMITTER_ORGANIZATION_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + SEP + ORGANIZATION_ELEMENT_NAME;
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
    private static final String INVESTIGATOR_ORGANIZATION_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + SEP + ORGANIZATION_ELEMENT_NAME;
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

    private static final String TITLE_ELEMENT_NAME = "title";
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
    private static final String VARIABLE_STORAGE_METHOD_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "storageMethod";
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
    private static final String VARIABLE_SAMPLING_DEPTH_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "locationSeawaterIntake";
    private static final String VARIABLE_WATER_VAPOR_CORRECTION_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "waterVaportCorrection";
    private static final String VARIABLE_TEMPERATURE_CORRECTION_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "temperatureCorrection";
    private static final String VARIABLE_REPORT_TEMPERATURE_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "co2ReportTemperature";

    private static final String VARIABLE_ANALYSIS_WATER_VOLUME_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "seawatervol";
    private static final String VARIABLE_ANALYSIS_HEADSPACE_VOLUME_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "headspacevol";
    private static final String VARIABLE_ANALYSIS_TEMPERATURE_MEASURE_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "temperatureMeasure";

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

        Submitter submitter = mdata.getSubmitter();
        String strVal = submitter.getFirstName() + " " + submitter.getMiddle();
        strVal = strVal.trim() + " " + submitter.getLastName();
        setElementText(null, SUBMITTER_NAME_ELEMENT_NAME, strVal);
        setElementText(null, SUBMITTER_ORGANIZATION_ELEMENT_NAME, submitter.getOrganization());
        ArrayList<String> strList = submitter.getStreets();
        if ( strList.size() > 0 )
            setElementText(null, SUBMITTER_FIRST_STREET_ELEMENT_NAME, strList.get(0));
        if ( strList.size() > 1 ) {
            strVal = strList.get(1);
            for (int k = 2; k < strList.size(); k++) {
                strVal += "\n" + strList.get(k);
            }
            setElementText(null, SUBMITTER_SECOND_STREET_ELEMENT_NAME, strVal);
        }
        setElementText(null, SUBMITTER_CITY_ELEMENT_NAME, submitter.getCity());
        setElementText(null, SUBMITTER_REGION_ELEMENT_NAME, submitter.getRegion());
        setElementText(null, SUBMITTER_ZIP_ELEMENT_NAME, submitter.getZipCode());
        setElementText(null, SUBMITTER_COUNTRY_ELEMENT_NAME, submitter.getCountry());
        setElementText(null, SUBMITTER_EMAIL_ELEMENT_NAME, submitter.getEmail());
        setElementText(null, SUBMITTER_PHONE_ELEMENT_NAME, submitter.getPhone());
        setElementText(null, SUBMITTER_ID_ELEMENT_NAME, submitter.getId());
        setElementText(null, SUBMITTER_ID_TYPE_ELEMENT_NAME, submitter.getIdType());

        for (Investigator pi : mdata.getInvestigators()) {
            Element ancestor = addListElement(null, INVESTIGATOR_ELEMENT_NAME);
            setElementText(ancestor, INVESTIGATOR_ROLE_ELEMENT_NAME, "investigator");
            strVal = pi.getFirstName() + " " + pi.getMiddle();
            strVal = strVal.trim() + " " + pi.getLastName();
            setElementText(ancestor, INVESTIGATOR_NAME_ELEMENT_NAME, strVal);
            setElementText(ancestor, INVESTIGATOR_ORGANIZATION_ELEMENT_NAME, pi.getOrganization());
            strList = pi.getStreets();
            if ( strList.size() > 0 )
                setElementText(ancestor, INVESTIGATOR_FIRST_STREET_ELEMENT_NAME, strList.get(0));
            if ( strList.size() > 1 ) {
                strVal = strList.get(1);
                for (int k = 2; k < strList.size(); k++) {
                    strVal += "\n" + strList.get(k);
                }
                setElementText(ancestor, INVESTIGATOR_SECOND_STREET_ELEMENT_NAME, strVal);
            }
            setElementText(ancestor, INVESTIGATOR_CITY_ELEMENT_NAME, pi.getCity());
            setElementText(ancestor, INVESTIGATOR_REGION_ELEMENT_NAME, pi.getRegion());
            setElementText(ancestor, INVESTIGATOR_ZIP_ELEMENT_NAME, pi.getZipCode());
            setElementText(ancestor, INVESTIGATOR_COUNTRY_ELEMENT_NAME, pi.getCountry());
            setElementText(ancestor, INVESTIGATOR_EMAIL_ELEMENT_NAME, pi.getEmail());
            setElementText(ancestor, INVESTIGATOR_PHONE_ELEMENT_NAME, pi.getPhone());
            setElementText(ancestor, INVESTIGATOR_ID_ELEMENT_NAME, pi.getId());
            setElementText(ancestor, INVESTIGATOR_ID_TYPE_ELEMENT_NAME, pi.getIdType());
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

        // TODO: write everything under rootElement

        Document doc = new Document(rootElement);
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(doc, xmlWriter);
    }

}

