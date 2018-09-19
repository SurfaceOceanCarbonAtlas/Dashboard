package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.Coverage;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.xml.CdiacReader;
import gov.noaa.pmel.sdimetadata.xml.DocumentHandler;
import gov.noaa.pmel.sdimetadata.xml.OcadsWriter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static gov.noaa.pmel.sdimetadata.xml.DocumentHandler.SEP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OcadsWriterTest {

    private class MyDocHandler extends DocumentHandler {
        MyDocHandler(String xmlString) {
            Document omeDoc = null;
            try {
                omeDoc = (new SAXBuilder()).build(new StringReader(xmlString));
            } catch ( Exception ex ) {
                throw new RuntimeException(ex);
            }
            rootElement = omeDoc.getRootElement();
            if ( rootElement == null )
                throw new RuntimeException("No root element found");
        }
    }

    @Test
    public void writeSDIMetadata() {
        // Create the SDIMetadata from the AOML CDIAC XML
        CdiacReader cdiacReader = new CdiacReader(new StringReader(CdiacReaderTest.AOML_CDIAC_XML_DATA_STRING));
        SDIMetadata metadata = cdiacReader.createSDIMetadata();
        // Create the OCADS XML from this SDIMetadata
        String xmlString = null;
        try {
            StringWriter xmlWriter = new StringWriter();
            OcadsWriter ocadsWriter = new OcadsWriter();
            ocadsWriter.writeSDIMetadata(metadata, xmlWriter);
            xmlString = xmlWriter.getBuffer().toString();
        } catch ( Exception ex ) {
            fail("Problems creating the OCADS XML contents: " + ex.getMessage());
        }
        MyDocHandler docHandler = null;
        try {
            docHandler = new MyDocHandler(xmlString);
        } catch ( Exception ex ) {
            fail("Problems interpreting the OCADS XML contents: " + ex.getMessage());
        }

        assertEquals("", docHandler.getElementText("related" + SEP + "name"));

        assertEquals("2016-01-20", docHandler.getElementText("submissiondate"));
        assertEquals(0, docHandler.getElementList("update").size());

        List<Element> elemList = docHandler.getElementList("datasubmitter");
        assertEquals(1, elemList.size());
        Element elem = elemList.get(0);
        assertEquals("Robert Castle", elem.getChildTextTrim("name"));
        assertEquals("NOAA/Atlantic Oceanographic & Meteorological Laboratory", elem.getChildTextTrim("organization"));
        assertEquals("4301 Rickenbacker Causeway; Miami, FL 33149", elem.getChildTextTrim("deliverypoint1"));
        assertEquals(null, elem.getChildTextTrim("deliverypoint2"));
        assertEquals(null, elem.getChildTextTrim("city"));
        assertEquals(null, elem.getChildTextTrim("administrativeArea"));
        assertEquals(null, elem.getChildTextTrim("zip"));
        assertEquals(null, elem.getChildTextTrim("country"));
        assertEquals("305-361-4418", elem.getChildTextTrim("phone"));
        assertEquals("Robert.Castle@noaa.gov", elem.getChildTextTrim("email"));
        assertEquals(null, elem.getChildTextTrim("ID"));
        assertEquals(null, elem.getChildTextTrim("IDtype"));

        elemList = docHandler.getElementList("person");
        assertEquals(1, elemList.size());
        elem = elemList.get(0);
        assertEquals("Rik Wanninkhof", elem.getChildTextTrim("name"));
        assertEquals("NOAA/AOML", elem.getChildTextTrim("organization"));
        assertEquals("4301 Rickenbacker Causeway; Miami Fl, 33149", elem.getChildTextTrim("deliverypoint1"));
        assertEquals(null, elem.getChildTextTrim("deliverypoint2"));
        assertEquals(null, elem.getChildTextTrim("city"));
        assertEquals(null, elem.getChildTextTrim("administrativeArea"));
        assertEquals(null, elem.getChildTextTrim("zip"));
        assertEquals(null, elem.getChildTextTrim("country"));
        assertEquals("305-361-4379", elem.getChildTextTrim("phone"));
        assertEquals("Rik.Wanninkhof@noaa.gov", elem.getChildTextTrim("email"));
        assertEquals(null, elem.getChildTextTrim("ID"));
        assertEquals(null, elem.getChildTextTrim("IDtype"));
        assertEquals("investigator", elem.getChildTextTrim("role"));

        assertEquals("", docHandler.getElementText("title"));
        assertEquals("", docHandler.getElementText("abstract"));
        assertEquals("", docHandler.getElementText("purpose"));

        assertEquals("2015-01-15", docHandler.getElementText("startdate"));
        assertEquals("2015-01-29", docHandler.getElementText("enddate"));
        assertEquals("-158.0", docHandler.getElementText("westbd"));
        assertEquals("-122.6", docHandler.getElementText("eastbd"));
        assertEquals("-21.2", docHandler.getElementText("southbd"));
        assertEquals("38.0", docHandler.getElementText("northbd"));
        assertEquals(Coverage.WGS84, docHandler.getElementText("spatialReference"));
        assertEquals(0, docHandler.getElementList("geographicName").size());

        assertEquals("NOAA Climate Observation Office/Climate Observations Division",
                docHandler.getElementText("fundingAgency" + SEP + "agency"));
        assertEquals("", docHandler.getElementText("fundingAgency" + SEP + "title"));
        assertEquals("", docHandler.getElementText("fundingAgency" + SEP + "ID"));

        assertEquals("", docHandler.getElementText("researchProject"));

        assertEquals("Ronald H. Brown", docHandler.getElementText("Platform" + SEP + "PlatformName"));
        assertEquals("33RO", docHandler.getElementText("Platform" + SEP + "PlatformID"));
        assertEquals("Ship", docHandler.getElementText("Platform" + SEP + "PlatformType"));
        assertEquals("NOAA", docHandler.getElementText("Platform" + SEP + "PlatformOwner"));
        assertEquals("", docHandler.getElementText("Platform" + SEP + "PlatformCountry"));

        assertEquals("33RO20150114", docHandler.getElementText("expocode"));
        assertEquals("RB1501A", docHandler.getElementText("cruiseID"));
        assertEquals("", docHandler.getElementText("section"));

        assertEquals("Wanninkhof, R., R. D. Castle, and J. Shannahoff. 2013. " +
                "Underway pCO2 measurements aboard the R/V Ronald H. Brown during the 2014 cruises. " +
                "http://cdiac.ornl.gov/ftp/oceans/VOS_Ronald_Brown/RB2013/. Carbon Dioxide Information " +
                "Analysis Center, Oak Ridge National Laboratory, US Department of Energy, Oak Ridge, Tennessee. " +
                "doi: 10.3334/CDIAC/OTG.VOS_RB_2012", docHandler.getElementText("citation"));

    }

}

