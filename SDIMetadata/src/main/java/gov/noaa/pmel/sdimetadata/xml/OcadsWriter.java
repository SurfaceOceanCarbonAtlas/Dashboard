package gov.noaa.pmel.sdimetadata.xml;

import gov.noaa.pmel.sdimetadata.SDIMetadata;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.Writer;

public class OcadsWriter extends DocumentHandler {

    private static final String ACCESS_ID_ELEMENT_NAME = "related" + SEP + "name";
    private static final String SUBMISSION_DATE_ELEMENT_NAME = "submissionDate";
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
    private static final String WEBSITE_ELEMENT_NAME = "link_lanking";
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
    private static final String PLATFORM_ID_ELEMENT_NAME = PLATFORM_ELEMENT_NAME + "t" + "PlatformID";
    private static final String PLATFORM_TYPE_ELEMENT_NAME = PLATFORM_ELEMENT_NAME + "t" + "PlatformType";
    private static final String PLATFORM_OWNER_ELEMENT_NAME = PLATFORM_ELEMENT_NAME + "t" + "PlatformOwner";
    private static final String PLATFORM_COUNTRY_ELEMENT_NAME = PLATFORM_ELEMENT_NAME + "t" + "PlatformCountry";

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
    private static final String VARIABLE_SAMPLING_DEPTH_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "locationSeawaterIntake";

    private static final String EQUILIBRATOR_ELEMENT_NAME = VARIABLE_ELEMENT_NAME + SEP + "equilibrator";
    private static final String EQUILIBRATOR_TYPE_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "type";
    private static final String EQUILIBRATOR_VOLUME_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "volume";
    private static final String EQUILIBRATOR_VENTED_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "vented";
    private static final String EQUILIBRATOR_WATER_FLOW_RATE_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "waterFlowRate";
    private static final String EQUILIBRATOR_GAS_FLOW_RATE_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "gasFlowRate";
    private static final String EQUILIBRATOR_TEMPERATURE_EQUILIBRATION_ELEMENT_NAME = EQUILIBRATOR_ELEMENT_NAME + SEP + "gasFlowRate";

    private Writer xmlWriter;

    public OcadsWriter(Writer xmlWriter) {
        this.xmlWriter = xmlWriter;
        rootElement = new Element("metadata");
    }

    public void writeSDIMetadata(SDIMetadata mdata) throws IOException {

        // TODO: write everything under rootElement

        Document doc = new Document(rootElement);
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(doc, xmlWriter);
    }

}

