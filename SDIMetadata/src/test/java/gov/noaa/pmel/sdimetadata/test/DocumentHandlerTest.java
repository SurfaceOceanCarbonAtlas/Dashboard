package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.person.Person;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
import gov.noaa.pmel.sdimetadata.util.NumericString;
import gov.noaa.pmel.sdimetadata.xml.DocumentHandler;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DocumentHandlerTest {

    private DocumentHandler docHandler;

    @Before
    public void setUp() throws Exception {
        class MyDocHandler extends DocumentHandler {
            MyDocHandler(String xmlString) throws Exception {
                Document omeDoc = (new SAXBuilder()).build(new StringReader(xmlString));
                rootElement = omeDoc.getRootElement();
                if ( rootElement == null )
                    throw new RuntimeException("No root element found");
            }
        }
        docHandler = new MyDocHandler(AOML_CDIAC_XML_DATA_STRING);
    }

    @Test
    public void guessPlatformType() {
        String name = "Ronald H. Brown";
        String datasetId = "33RO20150114";
        assertEquals("Ship", DocumentHandler.guessPlatformType(name, datasetId));

        name = "MySpecialPlatform";
        datasetId = "316420100523-1";
        assertEquals("Mooring", DocumentHandler.guessPlatformType(name, datasetId));

        name = "MySpecialPlatform";
        datasetId = "35DR20100523-2";
        assertEquals("Drifting Buoy", DocumentHandler.guessPlatformType(name, datasetId));

        name = "my special buoy that does not drift in the ocean";
        datasetId = "MySpecialID";
        assertEquals("Mooring", DocumentHandler.guessPlatformType(name, datasetId));

        name = "my special drifting buoy in the ocean";
        datasetId = "MySpecialID";
        assertEquals("Drifting Buoy", DocumentHandler.guessPlatformType(name, datasetId));

        name = "My Mooring with an incorrect expocode";
        datasetId = "35DR20100523";
        assertEquals("Mooring", DocumentHandler.guessPlatformType(name, datasetId));

        name = "my special mooring in the ocean";
        datasetId = "";
        assertEquals("Mooring", DocumentHandler.guessPlatformType(name, datasetId));

        name = "";
        datasetId = "";
        assertEquals("Ship", DocumentHandler.guessPlatformType(name, datasetId));
    }

    @Test
    public void getPersonNames() {
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
    public void getListOfLines() {
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
    public void getDatestamp() {
        final String year = "2010";
        final String month = "3";
        final String day = "24";
        final Datestamp stamp = new Datestamp(year, month, day);

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
    public void getNumericString() {
        final String numVal = "345.0";
        final String unitVal = "umol / mol";
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
    public void getElementList() {
        List<Element> elems = docHandler.getElementList("Investigator");
        assertEquals(1, elems.size());
        assertEquals("Rik Wanninkhof", elems.get(0).getChildTextTrim("Name"));
        elems = docHandler.getElementList("Variables_Info" + "\t" + "Variable");
        assertEquals(13, elems.size());
        assertEquals("xCO2_EQU_ppm", elems.get(0).getChildTextTrim("Variable_Name"));
        elems = docHandler.getElementList("Cruise_Info" + "\t" + "Experiment" + "\t" + "Experiment_Name");
        assertEquals(1, elems.size());
        assertEquals("RB1501A", elems.get(0).getTextTrim());
        elems = docHandler.getElementList("Cruise_Info" + "\t" + "Experiment" + "\t" + "garbage");
        assertEquals(0, elems.size());
        elems = docHandler.getElementList("");
        assertEquals(0, elems.size());
    }

    @Test
    public void getElementText() {
        assertEquals("Robert Castle", docHandler.getElementText("User" + "\t" + "Name"));
        assertEquals("", docHandler.getElementText("Cruise_Info"));
        assertEquals("", docHandler.getElementText("User" + "\t" + "garbage"));
        assertEquals("", docHandler.getElementText(""));
    }

    private static final String AOML_CDIAC_XML_DATA_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<?xml-stylesheet type=\"text/xsl\" href=\"http://www.aoml.noaa.gov/ocd/gcc/xml/xmlunderway2.xsl\"?>\n" +
            "<x_tags xml:space=\"preserve\">\n" +
            "  <User>\n" +
            "    <Name>Robert Castle</Name>\n" +
            "    <Organization>NOAA/Atlantic Oceanographic &amp; Meteorological Laboratory</Organization>\n" +
            "    <Address>4301 Rickenbacker Causeway; Miami, FL 33149</Address>\n" +
            "    <Phone>305-361-4418</Phone>\n" +
            "    <Email>Robert.Castle@noaa.gov</Email>\n" +
            "  </User>\n" +
            "  <Investigator>\n" +
            "    <Name>Rik Wanninkhof</Name>\n" +
            "    <Organization>NOAA/AOML</Organization>\n" +
            "    <Address>4301 Rickenbacker Causeway; Miami Fl, 33149</Address>\n" +
            "    <Phone>305-361-4379</Phone>\n" +
            "    <Email>Rik.Wanninkhof@noaa.gov</Email>\n" +
            "  </Investigator>\n" +
            "  <Dataset_Info>\n" +
            "    <Funding_Info>NOAA Climate Observation Office/Climate Observations Division</Funding_Info>\n" +
            "    <Submission_Dates>\n" +
            "      <Initial_Submission>20160120</Initial_Submission>\n" +
            "      <Revised_Submission>20160120</Revised_Submission>\n" +
            "    </Submission_Dates>\n" +
            "  </Dataset_Info>\n" +
            "  <Cruise_Info>\n" +
            "    <Experiment>\n" +
            "      <Experiment_Name>RB1501A</Experiment_Name>\n" +
            "      <Experiment_Type>Research Cruise</Experiment_Type>\n" +
            "      <Cruise>\n" +
            "        <Cruise_ID>33RO20150114</Cruise_ID>\n" +
            "        <Cruise_Info>CALWATER II Leg 1</Cruise_Info>\n" +
            "        <Geographical_Coverage>\n" +
            "          <Bounds>\n" +
            "            <Westernmost_Longitude>-158.0</Westernmost_Longitude>\n" +
            "            <Easternmost_Longitude>-122.6</Easternmost_Longitude>\n" +
            "            <Northernmost_Latitude>38.0</Northernmost_Latitude>\n" +
            "            <Southernmost_Latitude>-21.2</Southernmost_Latitude>\n" +
            "          </Bounds>\n" +
            "        </Geographical_Coverage>\n" +
            "        <Temporal_Coverage>\n" +
            "          <Start_Date>20150115</Start_Date>\n" +
            "          <End_Date>20150129</End_Date>\n" +
            "        </Temporal_Coverage>\n" +
            "        <Ports_of_Call>Honolulu, HI</Ports_of_Call>\n" +
            "        <Ports_of_Call>San Francisco, CA</Ports_of_Call>\n" +
            "      </Cruise>\n" +
            "    </Experiment>\n" +
            "    <Vessel>\n" +
            "      <Vessel_Name>Ronald H. Brown</Vessel_Name>\n" +
            "      <Vessel_ID>33RO</Vessel_ID>\n" +
            "      <Vessel_Owner>NOAA</Vessel_Owner>\n" +
            "    </Vessel>\n" +
            "  </Cruise_Info>\n" +
            "  <Variables_Info>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>xCO2_EQU_ppm</Variable_Name>\n" +
            "      <Description_of_Variable>Mole fraction of CO2 in the equilibrator headspace (dry) at equilibrator temperature (ppm)</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>xCO2_ATM_ppm</Variable_Name>\n" +
            "      <Description_of_Variable>Mole fraction of CO2 measured in dry outside air (ppm)</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>xCO2_ATM_interpolated_ppm</Variable_Name>\n" +
            "      <Description_of_Variable>Mole fraction of CO2 in outside air associated with each water analysis.  These values are interpolated between the bracketing averaged good xCO2_ATM analyses (ppm)</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>PRES_EQU_hPa</Variable_Name>\n" +
            "      <Description_of_Variable>Barometric pressure in the equilibrator headspace (hectopascals)</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>PRES_ATM@SSP_hPa</Variable_Name>\n" +
            "      <Description_of_Variable>Barometric pressure measured outside, corrected to sea level (hectopascals)</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>TEMP_EQU_C</Variable_Name>\n" +
            "      <Description_of_Variable>Water temperature in equilibrator (degrees Celsius)</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>SST_C</Variable_Name>\n" +
            "      <Description_of_Variable>Sea surface temperature (degrees Celsius)</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>SAL_permil</Variable_Name>\n" +
            "      <Description_of_Variable>Sea surface salinity on Practical Salinity Scale (permil)</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>fCO2_SW@SST_uatm</Variable_Name>\n" +
            "      <Description_of_Variable>Fugacity of CO2 in sea water at SST and 100% humidity (microatmospheres)</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>fCO2_ATM_interpolated_uatm</Variable_Name>\n" +
            "      <Description_of_Variable>Fugacity of CO2 in air corresponding to the interpolated xCO2 at SST and 100% humidity (microatmospheres)</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>dfCO2_uatm</Variable_Name>\n" +
            "      <Description_of_Variable>Sea water fCO2 minus interpolated air fCO2 (microatmospheres)</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>WOCE_QC_FLAG</Variable_Name>\n" +
            "      <Description_of_Variable>Quality control flag for fCO2 values (2=good, 3=questionable)</Description_of_Variable> </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>QC_SUBFLAG</Variable_Name>\n" +
            "      <Description_of_Variable>Quality control subflag for fCO2 values, provides explanation when QC flag=3</Description_of_Variable>\n" +
            "    </Variable>\n" +
            "  </Variables_Info>\n" +
            "  <Method_Description>\n" +
            "    <Equilibrator_Design>\n" +
            "      <Depth_of_Sea_Water_Intake>5 meters</Depth_of_Sea_Water_Intake>\n" +
            "      <Location_of_Sea_Water_Intake>Bow</Location_of_Sea_Water_Intake>\n" +
            "      <Equilibrator_Type>Sprayhead above dynamic pool, with thermal jacket</Equilibrator_Type>\n" +
            "      <Equilibrator_Volume>0.95 L (0.4 L water, 0.55 L headspace)</Equilibrator_Volume>\n" +
            "      <Water_Flow_Rate>1.5 - 2.0 L/min</Water_Flow_Rate>\n" +
            "      <Headspace_Gas_Flow_Rate>70 - 150 ml/min</Headspace_Gas_Flow_Rate>\n" +
            "      <Vented>Yes</Vented>\n" +
            "      <Drying_Method_for_CO2_in_water>Gas stream passes through a thermoelectric condenser (~5 &#176;C) and then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).</Drying_Method_for_CO2_in_water>\n" +
            "      <Additional_Information>Primary equlibrator is vented through a secondary equilibrator</Additional_Information>\n" +
            "    </Equilibrator_Design>\n" +
            "    <CO2_in_Marine_Air>\n" +
            "      <Measurement>Yes, 5 readings in a group every 3.25 hours.</Measurement>\n" +
            "      <Location_and_Height>Bow tower ~10 m above the sea surface.</Location_and_Height>\n" +
            "      <Drying_Method>Gas stream passes through a thermoelectric condenser (~5 &#176;C) and then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).</Drying_Method>\n" +
            "    </CO2_in_Marine_Air>\n" +
            "    <CO2_Sensors>\n" +
            "      <CO2_Sensor>\n" +
            "        <Measurement_Method>Infrared absorption of dry sample gas.</Measurement_Method>\n" +
            "        <Manufacturer>LI-COR</Manufacturer>\n" +
            "        <Model>LI-6262</Model>\n" +
            "        <Frequency>Every 150 seconds</Frequency>\n" +
            "        <Resolution_Water> 0.01 microatmosphere</Resolution_Water>\n" +
            "        <Uncertainty_Water>&#177; 1 microatmospheres</Uncertainty_Water>\n" +
            "        <Resolution_Air>0.01 ppm</Resolution_Air>\n" +
            "        <Uncertainty_Air>&#177; 0.2 ppm</Uncertainty_Air>\n" +
            "        <Manufacturer_of_Calibration_Gas>ESRL in Boulder, CO.&#xD;  Std 1: CA04957, 282.55 ppm; Std 2: CC105863, 380.22 ppm; Std 3: CB09696, 453.04 ppm; Std 4: CB09032, 539.38 ppm</Manufacturer_of_Calibration_Gas>\n" +
            "        <No_Of_Non_Zero_Gas_Stds>4</No_Of_Non_Zero_Gas_Stds>\n" +
            "        <CO2_Sensor_Calibration>The analyzer is calibrated every 3.25 hours with standards from ESRL in Boulder, CO that are directly traceable to the WMO scale.  The zero gas is 99.9% nitrogen.</CO2_Sensor_Calibration>\n" +
            "        <Other_Comments>The instrument is located in an air-conditioned laboratory.  99.9% Nitrogen gas and the high standard (Std 4) are used to set the zero and span of the LI-COR analyzer.</Other_Comments>\n" +
            "        <Method_References>Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
            "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
            "Recommendations for autonomous underway pCO2 measuring systems \n" +
            "and data reduction routines, Deep-Sea Res II, 56, 512-522.</Method_References>\n" +
            "      </CO2_Sensor>\n" +
            "    </CO2_Sensors>\n" +
            "    <Sea_Surface_Temperature>\n" +
            "      <Location>Bow thruster room, before sea water pump, ~5 m below water line.</Location>\n" +
            "      <Manufacturer>Seabird</Manufacturer>\n" +
            "      <Model>SBE-21</Model>\n" +
            "      <Accuracy_degC>&#177; 0.01 &#176;C</Accuracy_degC>\n" +
            "      <Precision_degC>0.001 &#176;C</Precision_degC>\n" +
            "      <Calibration>Factory calibration</Calibration>\n" +
            "      <Other_Comments>Manufacturer's resolution is taken as precision. Maintained by ship.</Other_Comments>\n" +
            "    </Sea_Surface_Temperature>\n" +
            "    <Equilibrator_Temperature>\n" +
            "      <Location>In Hydro Lab, inserted into equilibrator ~ 5 cm below water line.</Location>\n" +
            "      <Manufacturer>Hart</Manufacturer>\n" +
            "      <Model>1521</Model>\n" +
            "      <Accuracy_degC>&#177; 0.025 &#176;C</Accuracy_degC>\n" +
            "      <Precision_degC>&#177; 0.01 &#176;C</Precision_degC>\n" +
            "      <Calibration>Factory calibration</Calibration>\n" +
            "      <Warming>0.1 - 0.6 &#176;C</Warming>\n" +
            "    </Equilibrator_Temperature>\n" +
            "    <Equilibrator_Pressure>\n" +
            "      <Location>Attached to CO2 analyzer exit to lab.</Location>\n" +
            "      <Manufacturer>Setra</Manufacturer>\n" +
            "      <Model>270</Model>\n" +
            "      <Accuracy_hPa>&#177; 0.05 hPa</Accuracy_hPa>\n" +
            "      <Precision_hPa>0.015 hPa</Precision_hPa>\n" +
            "      <Calibration>Factory calibration.</Calibration>\n" +
            "      <Other_Comments>Pressure reading from the Setra-270 on the exit of the analyzer was added to the differential pressure reading from Setra-239 attached to the equilibrator headspace to yield the equlibrator pressure.</Other_Comments>\n" +
            "    </Equilibrator_Pressure>\n" +
            "    <Atmospheric_Pressure>\n" +
            "      <Location>On bulkhead exterior on the port side of the radio room aft of the bridge at ~14 m above the sea surface.</Location>\n" +
            "      <Manufacturer>Vaisala</Manufacturer>\n" +
            "      <Model>PTB330</Model>\n" +
            "      <Accuracy>&#177; 0.2 hPa</Accuracy>\n" +
            "      <Precision>&#177; 0.08 hPa</Precision>\n" +
            "      <Calibration>Factory calibration</Calibration>\n" +
            "      <Normalized>yes</Normalized>\n" +
            "      <Other_Comments>Manufacturer's resolution is taken as precision. Maintained by ship.</Other_Comments>\n" +
            "    </Atmospheric_Pressure>\n" +
            "    <Sea_Surface_Salinity>\n" +
            "      <Location>Attached to underway system at sea water input.</Location>\n" +
            "      <Manufacturer>Seabird</Manufacturer>\n" +
            "      <Model>SBE-45</Model>\n" +
            "      <Accuracy>&#177; 0.005 permil</Accuracy>\n" +
            "      <Precision>0.0002 permil</Precision>\n" +
            "      <Calibration>Factory calibration.</Calibration>\n" +
            "      <Other_Comments>Manufacturer's resolution is taken as precision.</Other_Comments>\n" +
            "    </Sea_Surface_Salinity>\n" +
            "    <Other_Sensors>\n" +
            "      <Sensor>\n" +
            "      <Location>Attached to equilibrator headspace</Location>\n" +
            "      <Manufacturer>Setra</Manufacturer>\n" +
            "      <Model>239</Model>\n" +
            "      <Accuracy>&#177; 0.052 hPa</Accuracy>\n" +
            "      <Resolution>0.01 hPa</Resolution>\n" +
            "      <Calibration>Factory calibration</Calibration>\n" +
            "      <Other_Comments>Pressure reading from the Setra-270 on the exit of the analyzer was added to the differential pressure reading from Setra-239 attached to the equilibrator headspace to yield the equlibrator pressure.</Other_Comments>\n" +
            "      </Sensor>\n" +
            "    </Other_Sensors>\n" +
            "  </Method_Description>\n" +
            "  <Data_set_References>DOE (1994). Handbook of methods for the analysis of the various&#xD;\n" +
            "        parameters of the carbon dioxide system in sea water; version&#xD;\n" +
            "        2. DOE.&#xD;\n" +
            "Feely, R. A., R. Wanninkhof, H. B. Milburn, C. E. Cosca, M. Stapp and&#xD;\n" +
            "        P. P. Murphy (1998) A new automated underway system for making&#xD;\n" +
            "        high precision pCO2 measurements onboard research ships.&#xD;\n" +
            "        Analytica Chim. Acta 377: 185-191.&#xD;\n" +
            "Ho, D. T., R. Wanninkhof, J. Masters, R. A. Feely and C. E. Cosca&#xD;\n" +
            "        (1997). Measurement of underway fCO2 in the Eastern&#xD;\n" +
            "        Equatorial Pacific on NOAA ships BALDRIGE and DISCOVERER,&#xD;\n" +
            "        NOAA data report ERL AOML-30, 52 pp., NTIS Springfield.&#xD;\n" +
            "Pierrot, D., C. Neill, K. Sullivan, R. Castle, R. Wanninkhof, H.&#xD;\n" +
            "        Luger, T. Johannessen, A. Olsen, R. A. Feely, and C. E.&#xD;\n" +
            "        Cosca (2009), Recommendations for autonomous underway pCO2&#xD;\n" +
            "        measuring systems and data-reduction routines.  Deep Sea&#xD;\n" +
            "        Research II, 56: 512-522.&#xD;\n" +
            "Wanninkhof, R. and K. Thoning (1993) Measurement of fugacity of CO2 in &#xD;\n" +
            "        surface water using continuous and discrete sampling methods.&#xD;\n" +
            "        Mar. Chem. 44(2-4): 189-205.&#xD;\n" +
            "Weiss, R. F. (1970) The solubility of nitrogen, oxygen and argon in&#xD;\n" +
            "        water and seawater. Deep-Sea Research 17: 721-735.&#xD;\n" +
            "Weiss, R. F. (1974) Carbon dioxide in water and seawater: the&#xD;\n" +
            "        solubility of a non-ideal gas.  Mar. Chem. 2: 203-215.&#xD;\n" +
            "Weiss, R. F., R. A. Jahnke and C. D. Keeling (1982) Seasonal effects&#xD;\n" +
            "        of temperature and salinity on the partial pressure of CO2 in&#xD;\n" +
            "        seawater. Nature 300: 511-513.</Data_set_References>\n" +
            "  <Additional_Information>(1.) It was determined that there was a 2.68 minute offset between the SST data record from the SBE-21 in the bow and the Hart 1521 temperature sensor in the equilibrator.  The SST data were interpolated using this offset to determine the SST at the time of the equilibrator measurement.  (2.) A total of 6011 measurements were taken with 5661 flagged as good, 342 flagged as questionable, and 8 flagged as bad.  All measurements flagged as 4 (bad) have been removed from the final data file.  (3.) There was a 17-1/2 hour dropout of EqT readings at \n" +
            "the start of the cruise.  New values were determined using a relation between equilibrator temperature and SST.  The equation used was EqT = 0.9734*SST + 0.7735, n = 124, r^2 = 0.9630.  All of these values have been flagged 3.  (4.) On 1/22 at 1730, an emergency shutdown of the system\n" +
            "occurred due to water getting into the atm condenser. The survey tech cleared out the water and restarted the system on 1/26 at 0519.  No data was acquired during the shutdown period.</Additional_Information>\n" +
            "  <Citation>Wanninkhof, R., R. D. Castle, and J. Shannahoff. 2013. Underway pCO2 measurements aboard the R/V Ronald H. Brown during the 2014 cruises. http://cdiac.ornl.gov/ftp/oceans/VOS_Ronald_Brown/RB2013/. Carbon Dioxide Information Analysis Center, Oak Ridge National Laboratory, US Department of Energy, Oak Ridge, Tennessee. doi: 10.3334/CDIAC/OTG.VOS_RB_2012</Citation>\n" +
            "  <Data_Set_Link>\n" +
            "    <URL>http://www.aoml.noaa.gov/ocd/gcc/rvbrown_introduction.php</URL>\n" +
            "    <Link_Note>All AOML fCO2 underway data from the R/V Ronald H. Brown are posted on this site.</Link_Note>\n" +
            "  </Data_Set_Link>\n" +
            "  <form_type>underway</form_type>\n" +
            "</x_tags>\n";

}

