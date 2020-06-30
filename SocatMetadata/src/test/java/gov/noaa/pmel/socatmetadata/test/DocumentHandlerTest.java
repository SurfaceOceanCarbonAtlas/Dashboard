package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import gov.noaa.pmel.socatmetadata.shared.person.Person;
import gov.noaa.pmel.socatmetadata.shared.platform.PlatformType;
import gov.noaa.pmel.socatmetadata.translate.DocumentHandler;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gov.noaa.pmel.socatmetadata.translate.DocumentHandler.SEP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DocumentHandlerTest {

    private static final class MyDocHandler extends DocumentHandler {
        MyDocHandler(String xmlString) {
            Document omeDoc;
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
    public void testGuessPlatformType() {
        String name = "Ronald H. Brown";
        String datasetId = "33RO20150114";
        assertEquals(PlatformType.SHIP, DocumentHandler.guessPlatformType(name, datasetId));

        name = "MySpecialPlatform";
        datasetId = "316420100523-1";
        assertEquals(PlatformType.MOORING, DocumentHandler.guessPlatformType(name, datasetId));

        name = "MySpecialPlatform";
        datasetId = "35DR20100523-2";
        assertEquals(PlatformType.DRIFTING_BUOY, DocumentHandler.guessPlatformType(name, datasetId));

        name = "my special buoy that does not drift in the ocean";
        datasetId = "MySpecialID";
        assertEquals(PlatformType.MOORING, DocumentHandler.guessPlatformType(name, datasetId));

        name = "my special drifting buoy in the ocean";
        datasetId = "MySpecialID";
        assertEquals(PlatformType.DRIFTING_BUOY, DocumentHandler.guessPlatformType(name, datasetId));

        name = "My Mooring with an incorrect expocode";
        datasetId = "35DR20100523";
        assertEquals(PlatformType.MOORING, DocumentHandler.guessPlatformType(name, datasetId));

        name = "my special mooring in the ocean";
        datasetId = "";
        assertEquals(PlatformType.MOORING, DocumentHandler.guessPlatformType(name, datasetId));

        name = "";
        datasetId = "";
        assertEquals(PlatformType.SHIP, DocumentHandler.guessPlatformType(name, datasetId));
    }

    @Test
    public void testGetPersonNames() {
        assertEquals(new Person("Brown", "H.M.S.", "Ronald H.", null, null, null),
                DocumentHandler.getPersonNames("H.M.S. Ronald H. Brown"));
        assertEquals(new Person("Brown", "Ronald", "H.", null, null, null),
                DocumentHandler.getPersonNames("Brown, Ronald H."));
        assertEquals(new Person("Brown", "Ronald", null, null, null, null),
                DocumentHandler.getPersonNames("Ronald Brown"));
        assertEquals(new Person("Brown", "Ronald", null, null, null, null),
                DocumentHandler.getPersonNames("Brown, Ronald"));
        assertEquals(new Person("Brown", null, null, null, null, null),
                DocumentHandler.getPersonNames("Brown"));
        assertEquals(new Person(null, null, null, null, null, null),
                DocumentHandler.getPersonNames(" \t "));
        assertEquals(new Person(null, null, null, null, null, null),
                DocumentHandler.getPersonNames(null));
    }

    @Test
    public void testGetListOfLines() {
        final ArrayList<String> lines = new ArrayList<String>(Arrays.asList(
                "first line of information",
                "second line\twith a tab",
                "third line part; next line part",
                "another line"
        ));
        String concat = lines.get(0) + "\n" + lines.get(1) + "\r\n" + lines.get(2) + "\r" + lines.get(3);
        assertEquals(lines, DocumentHandler.getListOfLines(concat));
        concat = lines.get(0) + "\n\n" + lines.get(1) + "\r\n" + lines.get(2) + "\r\r" + lines.get(3);
        assertEquals(lines, DocumentHandler.getListOfLines(concat));
        assertEquals(new ArrayList<String>(), DocumentHandler.getListOfLines(""));
        assertEquals(new ArrayList<String>(), DocumentHandler.getListOfLines(null));
    }

    @Test
    public void testGetDatestamp() {
        final String year = "2010";
        final String month = "3";
        final String day = "24";
        final Datestamp stamp = new Datestamp(year, month, day, "0", "0", "0");

        String concat = year + "0" + month + day;
        assertEquals(stamp, DocumentHandler.getDatestamp(concat));
        concat = year + month + day;
        assertNull(DocumentHandler.getDatestamp(concat));
        concat = year + "-" + month + "-" + day;
        assertEquals(stamp, DocumentHandler.getDatestamp(concat));
        concat = year + "/" + month + "/" + day;
        assertEquals(stamp, DocumentHandler.getDatestamp(concat));
        assertNull(DocumentHandler.getDatestamp("Mar 24, 2010"));
        assertNull(DocumentHandler.getDatestamp(null));
        assertNull(DocumentHandler.getDatestamp("\t"));
    }

    @Test
    public void testGetNumericString() {
        final String numVal = "345.0";
        final String unitVal = "umol/mol";
        final NumericString numstr = new NumericString(numVal, unitVal);
        final NumericString invalid = new NumericString();

        assertEquals(numstr, DocumentHandler.getNumericString(numVal, unitVal));
        String concat = numVal + " " + unitVal;
        assertEquals(numstr, DocumentHandler.getNumericString(concat, null));
        assertEquals(invalid, DocumentHandler.getNumericString(concat, ""));
        concat = numVal + " (" + unitVal + ")";
        assertEquals(numstr, DocumentHandler.getNumericString(concat, null));
        concat = numVal + "[" + unitVal + "] ";
        assertEquals(numstr, DocumentHandler.getNumericString(concat, null));
        concat = numVal + " {" + unitVal + "}";
        assertEquals(numstr, DocumentHandler.getNumericString(concat, null));
        concat = numVal + " ({" + unitVal + "})";
        assertEquals(numstr, DocumentHandler.getNumericString(concat, null));
    }

    @Test
    public void testGetElementList() {
        MyDocHandler docHandler = new MyDocHandler(CdiacReaderTest.AOML_CDIAC_XML_DATA_STRING);
        List<Element> elems = docHandler.getElementList(null, "Investigator");
        assertEquals(1, elems.size());
        assertEquals("Rik Wanninkhof", elems.get(0).getChildTextTrim("Name"));
        elems = docHandler.getElementList(null, "Variables_Info" + SEP + "Variable");
        assertEquals(13, elems.size());
        assertEquals("SST_C", elems.get(6).getChildTextTrim("Variable_Name"));
        List<Element> sstElems =
                docHandler.getElementList(elems.get(6), "Variables_Info" + SEP + "Variable" + SEP + "Variable_Name");
        assertEquals(1, sstElems.size());
        assertEquals("SST_C", sstElems.get(0).getTextTrim());
        elems = docHandler.getElementList(null, "Cruise_Info" + SEP + "Experiment" + SEP + "Experiment_Name");
        assertEquals(1, elems.size());
        assertEquals("RB1501A", elems.get(0).getTextTrim());
        elems = docHandler.getElementList(null, "Cruise_Info" + SEP + "Experiment" + SEP + "garbage");
        assertEquals(0, elems.size());
        elems = docHandler.getElementList(null, "");
        assertEquals(0, elems.size());
    }

    @Test
    public void testGetElementText() {
        MyDocHandler docHandler = new MyDocHandler(CdiacReaderTest.AOML_CDIAC_XML_DATA_STRING);
        assertEquals("Robert Castle", docHandler.getElementText(null, "User" + SEP + "Name"));
        assertEquals("", docHandler.getElementText(null, "Cruise_Info"));
        assertEquals("", docHandler.getElementText(null, "User" + SEP + "garbage"));
        assertEquals("", docHandler.getElementText(null, ""));
        List<Element> elems = docHandler.getElementList(null, "Investigator");
        assertEquals("Rik Wanninkhof", docHandler.getElementText(elems.get(0), "Investigator" + SEP + "Name"));
        elems = docHandler.getElementList(null, "Variables_Info" + SEP + "Variable");
        assertEquals(13, elems.size());
        assertEquals("SST_C",
                docHandler.getElementText(elems.get(6), "Variables_Info" + SEP + "Variable" + SEP + "Variable_Name"));
    }

    @Test
    public void testSetElementText() {
        MyDocHandler docHandler = new MyDocHandler(EMPTY_OCADS_XML_DATA_STRING);
        String name = "Expocode";
        String value = "316420100523";
        docHandler.setElementText(null, name, value);
        assertEquals(value, docHandler.getElementText(null, name));

        name = "person" + SEP + "name";
        value = "Ronald H. Brown";
        docHandler.setElementText(null, name, value);
        assertEquals(value, docHandler.getElementText(null, name));

        String otherval = "John Smith";
        docHandler.setElementText(null, name, otherval);
        List<Element> elemList = docHandler.getElementList(null, name);
        assertEquals(1, elemList.size());
        assertEquals(otherval, elemList.get(0).getText());

        docHandler.setElementText(null, name, null);
        elemList = docHandler.getElementList(null, name);
        assertEquals(1, elemList.size());
        assertEquals("", elemList.get(0).getText());

        String childName = "first";
        String fullName = name + SEP + childName;
        value = "Ronald";
        docHandler.setElementText(elemList.get(0), fullName, value);
        elemList = docHandler.getElementList(null, name);
        assertEquals(1, elemList.size());
        Element elem = elemList.get(0).getChild(childName);
        assertNotNull(elem);
        assertEquals(value, elem.getText());

        name = "some" + SEP + "name";
        docHandler.setElementText(null, name, null);
        assertEquals(0, docHandler.getElementList(null, name).size());
        assertEquals(0, docHandler.getElementList(null, "some").size());
        docHandler.setElementText(null, name, "\t");
        assertEquals(0, docHandler.getElementList(null, name).size());
        assertEquals(0, docHandler.getElementList(null, "some").size());

        name = fullName + SEP + "some" + SEP + "name";
        docHandler.setElementText(elem, name, null);
        assertEquals(0, docHandler.getElementList(null, name).size());
        assertEquals(0, docHandler.getElementList(null, fullName + SEP + "some").size());
        docHandler.setElementText(elem, name, "\t");
        assertEquals(0, docHandler.getElementList(null, name).size());
        assertEquals(0, docHandler.getElementList(null, fullName + SEP + "some").size());
    }

    @Test
    public void testAddListElement() {
        MyDocHandler docHandler = new MyDocHandler(EMPTY_OCADS_XML_DATA_STRING);
        String name = "investigators" + SEP + "investigator" + SEP + "name";
        String value = "Ronald H. Brown";
        String otherval = "John Smith";
        Element elem = docHandler.addListElement(null, name);
        elem.setText(value);
        elem = docHandler.addListElement(null, name);
        elem.setText(otherval);
        List<Element> elemList = docHandler.getElementList(null, name);
        assertEquals(2, elemList.size());
        assertEquals(value, elemList.get(0).getText());
        assertEquals(otherval, elemList.get(1).getText());

        name = "update";
        value = "2018-01-23";
        otherval = "2018-08-03";
        elem = docHandler.addListElement(null, name);
        elem.setText(value);
        elem = docHandler.addListElement(null, name);
        elem.setText(otherval);
        elemList = docHandler.getElementList(null, name);
        assertEquals(2, elemList.size());
        assertEquals(value, elemList.get(0).getText());
        assertEquals(otherval, elemList.get(1).getText());

        String parentName = "investigators" + SEP + "investigator";
        elemList = docHandler.getElementList(null, parentName);
        assertEquals(1, elemList.size());
        String childName = "street" + SEP + "first";
        name = parentName + SEP + childName;
        elem = docHandler.addListElement(elemList.get(0), name);
        value = "123 Main St";
        elem.setText(value);
        elem = docHandler.addListElement(elemList.get(0), name);
        otherval = "Suite 405";
        elem.setText(otherval);
        elemList = docHandler.getElementList(null, name);
        assertEquals(2, elemList.size());
        assertEquals(value, elemList.get(0).getText());
        assertEquals(otherval, elemList.get(1).getText());
    }

    static final String EMPTY_OCADS_XML_DATA_STRING = "<?xml-stylesheet href=\"xmlblob.xsl\" type=\"text/xsl\"?>" +
            "<metadata></metadata>";

}
