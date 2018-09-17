package gov.noaa.pmel.sdimetadata.xml;

import gov.noaa.pmel.sdimetadata.SDIMetadata;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.Writer;

public class OcadsWriter extends DocumentHandler {

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
    private static final String SUBMITTER_NAME_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + NAME_ELEMENT_NAME;
    private static final String SUBMITTER_ORGANIZATION_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + ORGANIZATION_ELEMENT_NAME;
    private static final String SUBMITTER_FIRST_STREET_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + FIRST_STREET_ELEMENT_NAME;
    private static final String SUBMITTER_SECOND_STREET_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + SECOND_STREET_ELEMENT_NAME;
    private static final String SUBMITTER_CITY_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + CITY_ELEMENT_NAME;
    private static final String SUBMITTER_REGION_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + REGION_ELEMENT_NAME;
    private static final String SUBMITTER_ZIP_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + ZIP_ELEMENT_NAME;
    private static final String SUBMITTER_COUNTRY_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + COUNTRY_ELEMENT_NAME;
    private static final String SUBMITTER_EMAIL_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + EMAIL_ELEMENT_NAME;
    private static final String SUBMITTER_PHONE_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + PHONE_ELEMENT_NAME;
    private static final String SUBMITTER_ID_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + ID_ELEMENT_NAME;
    private static final String SUBMITTER_ID_TYPE_ELEMENT_NAME = SUBMITTER_ELEMENT_NAME + "\t" + ID_TYPE_ELEMENT_NAME;

    private static final String INVESTIGATOR_ELEMENT_NAME = "person";
    private static final String INVESTIGATOR_ROLE_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + "role";
    private static final String INVESTIGATOR_NAME_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + NAME_ELEMENT_NAME;
    private static final String INVESTIGATOR_ORGANIZATION_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + ORGANIZATION_ELEMENT_NAME;
    private static final String INVESTIGATOR_FIRST_STREET_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + FIRST_STREET_ELEMENT_NAME;
    private static final String INVESTIGATOR_SECOND_STREET_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + SECOND_STREET_ELEMENT_NAME;
    private static final String INVESTIGATOR_CITY_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + CITY_ELEMENT_NAME;
    private static final String INVESTIGATOR_REGION_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + REGION_ELEMENT_NAME;
    private static final String INVESTIGATOR_ZIP_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + ZIP_ELEMENT_NAME;
    private static final String INVESTIGATOR_COUNTRY_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + COUNTRY_ELEMENT_NAME;
    private static final String INVESTIGATOR_EMAIL_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + EMAIL_ELEMENT_NAME;
    private static final String INVESTIGATOR_PHONE_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + PHONE_ELEMENT_NAME;
    private static final String INVESTIGATOR_ID_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + ID_ELEMENT_NAME;
    private static final String INVESTIGATOR_ID_TYPE_ELEMENT_NAME = INVESTIGATOR_ELEMENT_NAME + "\t" + ID_TYPE_ELEMENT_NAME;

    private static final String TITLE_ELEMENT_NAME = "title";
    private static final String ABSTRACT_ELEMENT_NAME = "abstract";
    private static final String PURPOSE_ELEMENT_NAME = "purpose";
    private static final String RESEARCH_PROJECT_ELEMENT_NAME = "researchProject";

    private static final String FUNDING_AGENCY_ELEMENT_NAME = "fundingAgency";
    private static final String FUNDING_AGENCY_NAME_ELEMENT_NAME = FUNDING_AGENCY_ELEMENT_NAME + "\t" + "agency";
    private static final String FUNDING_AGENCY_TITLE_ELEMENT_NAME = FUNDING_AGENCY_ELEMENT_NAME + "\t" + "title";
    private static final String FUNDING_AGENCY_ID_ELEMENT_NAME = FUNDING_AGENCY_ELEMENT_NAME + "\t" + "ID";

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

    // TODO: complete the ELEMENT_NAME tags

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

