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
                        "doi: 10.3334/CDIAC/OTG.VOS_RB_2012",
                docHandler.getElementText("citation"));

        assertEquals("DOE (1994). Handbook of methods for the analysis of the various\n" +
                        "parameters of the carbon dioxide system in sea water; version\n" +
                        "2. DOE.\n" +
                        "Feely, R. A., R. Wanninkhof, H. B. Milburn, C. E. Cosca, M. Stapp and\n" +
                        "P. P. Murphy (1998) A new automated underway system for making\n" +
                        "high precision pCO2 measurements onboard research ships.\n" +
                        "Analytica Chim. Acta 377: 185-191.\n" +
                        "Ho, D. T., R. Wanninkhof, J. Masters, R. A. Feely and C. E. Cosca\n" +
                        "(1997). Measurement of underway fCO2 in the Eastern\n" +
                        "Equatorial Pacific on NOAA ships BALDRIGE and DISCOVERER,\n" +
                        "NOAA data report ERL AOML-30, 52 pp., NTIS Springfield.\n" +
                        "Pierrot, D., C. Neill, K. Sullivan, R. Castle, R. Wanninkhof, H.\n" +
                        "Luger, T. Johannessen, A. Olsen, R. A. Feely, and C. E.\n" +
                        "Cosca (2009), Recommendations for autonomous underway pCO2\n" +
                        "measuring systems and data-reduction routines.  Deep Sea\n" +
                        "Research II, 56: 512-522.\n" +
                        "Wanninkhof, R. and K. Thoning (1993) Measurement of fugacity of CO2 in\n" +
                        "surface water using continuous and discrete sampling methods.\n" +
                        "Mar. Chem. 44(2-4): 189-205.\n" +
                        "Weiss, R. F. (1970) The solubility of nitrogen, oxygen and argon in\n" +
                        "water and seawater. Deep-Sea Research 17: 721-735.\n" +
                        "Weiss, R. F. (1974) Carbon dioxide in water and seawater: the\n" +
                        "solubility of a non-ideal gas.  Mar. Chem. 2: 203-215.\n" +
                        "Weiss, R. F., R. A. Jahnke and C. D. Keeling (1982) Seasonal effects\n" +
                        "of temperature and salinity on the partial pressure of CO2 in\n" +
                        "seawater. Nature 300: 511-513.",
                docHandler.getElementText("reference"));

        assertEquals("Port-of-Call: Honolulu, HI\n" +
                        "Port-of-Call: San Francisco, CA\n" +
                        "Experiment Type: Research Cruise\n" +
                        "Cruise Info: CALWATER II Leg 1\n" +
                        "Website Note: All AOML fCO2 underway data from the R/V Ronald H. Brown are posted on this site.\n" +
                        "(1.) It was determined that there was a 2.68 minute offset between the SST data record from the " +
                        "SBE-21 in the bow and the Hart 1521 temperature sensor in the equilibrator.  The SST data " +
                        "were interpolated using this offset to determine the SST at the time of the equilibrator " +
                        "measurement.  (2.) A total of 6011 measurements were taken with 5661 flagged as good, 342 " +
                        "flagged as questionable, and 8 flagged as bad.  All measurements flagged as 4 (bad) have " +
                        "been removed from the final data file.  (3.) There was a 17-1/2 hour dropout of EqT readings at\n" +
                        "the start of the cruise.  New values were determined using a relation between equilibrator " +
                        "temperature and SST.  The equation used was EqT = 0.9734*SST + 0.7735, n = 124, " +
                        "r^2 = 0.9630.  All of these values have been flagged 3.  (4.) On 1/22 at 1730, an emergency " +
                        "shutdown of the system\n" +
                        "occurred due to water getting into the atm condenser. The survey tech cleared out the water and " +
                        "restarted the system on 1/26 at 0519.  No data was acquired during the shutdown period.",
                docHandler.getElementText("suppleInfo"));

        List<Element> variables = docHandler.getElementList("variable");
        assertEquals(13, variables.size());

        final String abbrev = "abbrev";
        final String fullname = "fullname";
        final String unit = "unit";
        final String uncertainty = "uncertainty";
        final String flag = "flag";
        final String detailedInfo = "detailedInfo";
        Element var;


        var = variables.get(0);
        assertEquals("xCO2_EQU_ppm", var.getChildTextTrim(abbrev));
        assertEquals("Mole fraction of CO2 in the equilibrator headspace (dry) at equilibrator temperature (ppm)",
                var.getChildTextTrim(fullname));
        assertEquals(null, var.getChildTextTrim(unit));
        assertEquals("Given in column: WOCE_QC_FLAG", var.getChildTextTrim(flag));
        assertEquals("1 microatmospheres", var.getChildTextTrim(uncertainty));
        assertEquals("Resolution/Precision: 0.01 microatmosphere\n" +
                        "Frequency: Every 150 seconds",
                var.getChildTextTrim(detailedInfo));
        // TODO:


        var = variables.get(1);
        assertEquals("xCO2_ATM_ppm", var.getChildTextTrim(abbrev));
        assertEquals("Mole fraction of CO2 measured in dry outside air (ppm)", var.getChildTextTrim(fullname));
        assertEquals(null, var.getChildTextTrim(unit));
        assertEquals(null, var.getChildTextTrim(flag));
        assertEquals("0.2 ppm", var.getChildTextTrim(uncertainty));
        assertEquals("Resolution/Precision: 0.01 ppm\n" +
                        "Measurement: Yes, 5 readings in a group every 3.25 hours.",
                var.getChildTextTrim(detailedInfo));
        // TODO:


        var = variables.get(2);
        assertEquals("xCO2_ATM_interpolated_ppm", var.getChildTextTrim(abbrev));
        assertEquals("Mole fraction of CO2 in outside air associated with each water analysis.  " +
                        "These values are interpolated between the bracketing averaged good xCO2_ATM analyses (ppm)",
                var.getChildTextTrim(fullname));
        assertEquals(null, var.getChildTextTrim(unit));
        assertEquals(null, var.getChildTextTrim(flag));
        assertEquals("0.2 ppm", var.getChildTextTrim(uncertainty));
        assertEquals("Resolution/Precision: 0.01 ppm\n" +
                        "Measurement: Yes, 5 readings in a group every 3.25 hours.",
                var.getChildTextTrim(detailedInfo));
        // TODO:


        var = variables.get(3);
        assertEquals("PRES_EQU_hPa", var.getChildTextTrim(abbrev));
        assertEquals("Barometric pressure in the equilibrator headspace (hectopascals)",
                var.getChildTextTrim(fullname));
        assertEquals("hPa", var.getChildTextTrim(unit));
        assertEquals(null, var.getChildTextTrim(flag));
        assertEquals("0.05 hPa", var.getChildTextTrim(uncertainty));
        assertEquals("Resolution/Precision: 0.015 hPa", var.getChildTextTrim(detailedInfo));
        // TODO:


        var = variables.get(4);
        assertEquals("PRES_ATM@SSP_hPa", var.getChildTextTrim(abbrev));
        assertEquals("Barometric pressure measured outside, corrected to sea level (hectopascals)",
                var.getChildTextTrim(fullname));
        assertEquals("hPa", var.getChildTextTrim(unit));
        assertEquals(null, var.getChildTextTrim(flag));
        assertEquals("0.2 hPa", var.getChildTextTrim(uncertainty));
        assertEquals("Resolution/Precision: 0.08 hPa", var.getChildTextTrim(detailedInfo));
        // TODO:


        var = variables.get(5);
        assertEquals("TEMP_EQU_C", var.getChildTextTrim(abbrev));
        assertEquals("Water temperature in equilibrator (degrees Celsius)", var.getChildTextTrim(fullname));
        assertEquals("deg C", var.getChildTextTrim(unit));
        assertEquals(null, var.getChildTextTrim(flag));
        assertEquals("0.025 °C", var.getChildTextTrim(uncertainty));
        assertEquals("Resolution/Precision: 0.01 °C", var.getChildTextTrim(detailedInfo));
        // TODO:


        var = variables.get(6);
        assertEquals("SST_C", var.getChildTextTrim(abbrev));
        assertEquals("Sea surface temperature (degrees Celsius)", var.getChildTextTrim(fullname));
        assertEquals("deg C", var.getChildTextTrim(unit));
        assertEquals(null, var.getChildTextTrim(flag));
        assertEquals("0.01 °C", var.getChildTextTrim(uncertainty));
        assertEquals("Resolution/Precision: 0.001 °C", var.getChildTextTrim(detailedInfo));
        // TODO:


        var = variables.get(7);
        assertEquals("SAL_permil", var.getChildTextTrim(abbrev));
        assertEquals("Sea surface salinity on Practical Salinity Scale (permil)", var.getChildTextTrim(fullname));
        assertEquals(null, var.getChildTextTrim(unit));
        assertEquals(null, var.getChildTextTrim(flag));
        assertEquals("0.005 permil", var.getChildTextTrim(uncertainty));
        assertEquals("Resolution/Precision: 0.0002 permil", var.getChildTextTrim(detailedInfo));
        // TODO:


        var = variables.get(8);
        assertEquals("fCO2_SW@SST_uatm", var.getChildTextTrim(abbrev));
        assertEquals("Fugacity of CO2 in sea water at SST and 100% humidity (microatmospheres)",
                var.getChildTextTrim(fullname));
        assertEquals(null, var.getChildTextTrim(unit));
        assertEquals("Given in column: WOCE_QC_FLAG", var.getChildTextTrim(flag));
        assertEquals("1 microatmospheres", var.getChildTextTrim(uncertainty));
        assertEquals("Resolution/Precision: 0.01 microatmosphere\n" +
                        "Frequency: Every 150 seconds",
                var.getChildTextTrim(detailedInfo));
        // TODO:


        var = variables.get(9);
        assertEquals("fCO2_ATM_interpolated_uatm", var.getChildTextTrim(abbrev));
        assertEquals(
                "Fugacity of CO2 in air corresponding to the interpolated xCO2 at SST and 100% humidity (microatmospheres)",
                var.getChildTextTrim(fullname));
        assertEquals(null, var.getChildTextTrim(unit));
        assertEquals(null, var.getChildTextTrim(flag));
        assertEquals("0.2 ppm", var.getChildTextTrim(uncertainty));
        assertEquals("Resolution/Precision: 0.01 ppm\n" +
                        "Measurement: Yes, 5 readings in a group every 3.25 hours.",
                var.getChildTextTrim(detailedInfo));
        // TODO:


        /*
        var = variables.get(0);
        assertTrue(var instanceof DataVar);
        dataVar = (DataVar) var;
        assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
        assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
        assertEquals(dataVar.toString(), "Infrared absorption of dry sample gas.", dataVar.getMethodDescription());
        assertEquals(dataVar.toString(), "Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                "Recommendations for autonomous underway pCO2 measuring systems \n" +
                "and data reduction routines, Deep-Sea Res II, 56, 512-522.", dataVar.getMethodReference());
        assertEquals(dataVar.toString(), "Bow", dataVar.getSamplingLocation());
        assertEquals(dataVar.toString(), "Sampling Depth: 5 meters", dataVar.getSamplingElevation());
        assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
        assertEquals(dataVar.toString(), "", dataVar.getReplication());
        strList = dataVar.getSamplerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "Equilibrator", strList.get(0));
        strList = dataVar.getAnalyzerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "CO2 Sensor", strList.get(0));
        assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

        assertTrue(var instanceof GasConc);
        gasConc = (GasConc) var;
        assertEquals(gasConc.toString(), "Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                        "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                gasConc.getDryingMethod());
        assertEquals(gasConc.toString(), "", gasConc.getWaterVaporCorrection());

        assertTrue(var instanceof AquGasConc);
        aquGasConc = (AquGasConc) var;
        assertEquals(aquGasConc.toString(), "Equilibrator temperature", aquGasConc.getReportTemperature());
        assertEquals(aquGasConc.toString(), "", aquGasConc.getTemperatureCorrection());


        var = variables.get(1);
        assertTrue(var instanceof DataVar);
        dataVar = (DataVar) var;
        assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
        assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
        assertEquals(dataVar.toString(), "Infrared absorption of dry sample gas.", dataVar.getMethodDescription());
        assertEquals(dataVar.toString(), "Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                "Recommendations for autonomous underway pCO2 measuring systems \n" +
                "and data reduction routines, Deep-Sea Res II, 56, 512-522.", dataVar.getMethodReference());
        assertEquals(dataVar.toString(), "Bow tower ~10 m above the sea surface.", dataVar.getSamplingLocation());
        assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
        assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
        assertEquals(dataVar.toString(), "", dataVar.getReplication());
        assertEquals(dataVar.toString(), 0, dataVar.getSamplerNames().size());
        strList = dataVar.getAnalyzerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "CO2 Sensor", strList.get(0));
        assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

        assertTrue(var instanceof GasConc);
        gasConc = (GasConc) var;
        assertEquals(gasConc.toString(), "Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                        "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                gasConc.getDryingMethod());
        assertEquals(gasConc.toString(), "", gasConc.getWaterVaporCorrection());

        assertFalse(var instanceof AquGasConc);


        var = variables.get(2);
        assertTrue(var instanceof DataVar);
        dataVar = (DataVar) var;
        assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
        assertEquals(dataVar.toString(), MethodType.COMPUTED, dataVar.getMeasureMethod());
        assertEquals(dataVar.toString(), "Infrared absorption of dry sample gas.", dataVar.getMethodDescription());
        assertEquals(dataVar.toString(), "Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                "Recommendations for autonomous underway pCO2 measuring systems \n" +
                "and data reduction routines, Deep-Sea Res II, 56, 512-522.", dataVar.getMethodReference());
        assertEquals(dataVar.toString(), "Bow tower ~10 m above the sea surface.", dataVar.getSamplingLocation());
        assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
        assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
        assertEquals(dataVar.toString(), "", dataVar.getReplication());
        assertEquals(dataVar.toString(), 0, dataVar.getSamplerNames().size());
        strList = dataVar.getAnalyzerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "CO2 Sensor", strList.get(0));
        assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

        assertTrue(var instanceof GasConc);
        gasConc = (GasConc) var;
        assertEquals(gasConc.toString(), "Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                        "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                gasConc.getDryingMethod());
        assertEquals(gasConc.toString(), "", gasConc.getWaterVaporCorrection());

        assertFalse(var instanceof AquGasConc);


        var = variables.get(3);
        assertTrue(var instanceof DataVar);
        dataVar = (DataVar) var;
        assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
        assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
        assertEquals(dataVar.toString(), "", dataVar.getMethodDescription());
        assertEquals(dataVar.toString(), "", dataVar.getMethodReference());
        assertEquals(dataVar.toString(), "Attached to CO2 analyzer exit to lab.", dataVar.getSamplingLocation());
        assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
        assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
        assertEquals(dataVar.toString(), "", dataVar.getReplication());
        assertEquals(dataVar.toString(), 0, dataVar.getSamplerNames().size());
        strList = dataVar.getAnalyzerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "Equilibrator Pressure Sensor", strList.get(0));
        assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

        assertTrue(var instanceof AirPressure);
        pressure = (AirPressure) var;
        assertEquals(pressure.toString(), "", pressure.getPressureCorrection());


        var = variables.get(4);
        assertEquals(var.toString(), "PRES_ATM@SSP_hPa", var.getColName());
        assertEquals(var.toString(), "Barometric pressure measured outside, corrected to sea level (hectopascals)",
                var.getFullName());
        assertEquals(var.toString(), AirPressure.HECTOPASCALS_UNIT, var.getVarUnit());
        assertEquals(var.toString(), "", var.getMissVal());
        assertEquals(var.toString(), "", var.getFlagColName());
        assertEquals(var.toString(), new NumericString("0.2", "hPa"), var.getAccuracy());
        assertEquals(var.toString(), new NumericString("0.08", "hPa"), var.getPrecision());
        assertEquals(var.toString(), 0, var.getAddnInfo().size());

        assertTrue(var instanceof DataVar);
        dataVar = (DataVar) var;
        assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
        assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
        assertEquals(dataVar.toString(), "", dataVar.getMethodDescription());
        assertEquals(dataVar.toString(), "", dataVar.getMethodReference());
        assertEquals(dataVar.toString(), "On bulkhead exterior on the port side of the radio room aft of the bridge " +
                "at ~14 m above the sea surface.", dataVar.getSamplingLocation());
        assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
        assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
        assertEquals(dataVar.toString(), "", dataVar.getReplication());
        strList = dataVar.getSamplerNames();
        assertEquals(dataVar.toString(), 0, strList.size());
        strList = dataVar.getAnalyzerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "Atmospheric Pressure Sensor", strList.get(0));
        assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

        assertTrue(var instanceof AirPressure);
        pressure = (AirPressure) var;
        assertEquals(pressure.toString(), "Normalized: yes", pressure.getPressureCorrection());


        var = variables.get(5);
        assertTrue(var instanceof DataVar);
        dataVar = (DataVar) var;
        assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
        assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
        assertEquals(dataVar.toString(), "", dataVar.getMethodDescription());
        assertEquals(dataVar.toString(), "", dataVar.getMethodReference());
        assertEquals(dataVar.toString(), "In Hydro Lab, inserted into equilibrator ~ 5 cm below water line.",
                dataVar.getSamplingLocation());
        assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
        assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
        assertEquals(dataVar.toString(), "", dataVar.getReplication());
        strList = dataVar.getSamplerNames();
        assertEquals(dataVar.toString(), 0, strList.size());
        strList = dataVar.getAnalyzerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "Equilibrator Temperature Sensor", strList.get(0));
        assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

        assertTrue(var instanceof Temperature);


        var = variables.get(6);
        assertTrue(var instanceof DataVar);
        dataVar = (DataVar) var;
        assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
        assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
        assertEquals(dataVar.toString(), "", dataVar.getMethodDescription());
        assertEquals(dataVar.toString(), "", dataVar.getMethodReference());
        assertEquals(dataVar.toString(), "Bow thruster room, before sea water pump, ~5 m below water line.",
                dataVar.getSamplingLocation());
        assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
        assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
        assertEquals(dataVar.toString(), "", dataVar.getReplication());
        strList = dataVar.getSamplerNames();
        assertEquals(dataVar.toString(), 0, strList.size());
        strList = dataVar.getAnalyzerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "Water Temperature Sensor", strList.get(0));
        assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

        assertTrue(var instanceof Temperature);


        var = variables.get(7);
        assertTrue(var instanceof DataVar);
        dataVar = (DataVar) var;
        assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
        assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
        assertEquals(dataVar.toString(), "", dataVar.getMethodDescription());
        assertEquals(dataVar.toString(), "", dataVar.getMethodReference());
        assertEquals(dataVar.toString(), "Attached to underway system at sea water input.",
                dataVar.getSamplingLocation());
        assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
        assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
        assertEquals(dataVar.toString(), "", dataVar.getReplication());
        strList = dataVar.getSamplerNames();
        assertEquals(dataVar.toString(), 0, strList.size());
        strList = dataVar.getAnalyzerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "Salinity Sensor", strList.get(0));
        assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

        assertFalse(var instanceof Temperature);
        assertFalse(var instanceof AirPressure);


        var = variables.get(8);
        assertTrue(var instanceof DataVar);
        dataVar = (DataVar) var;
        assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
        assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
        assertEquals(dataVar.toString(), "Infrared absorption of dry sample gas.", dataVar.getMethodDescription());
        assertEquals(dataVar.toString(), "Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                "Recommendations for autonomous underway pCO2 measuring systems \n" +
                "and data reduction routines, Deep-Sea Res II, 56, 512-522.", dataVar.getMethodReference());
        assertEquals(dataVar.toString(), "Bow", dataVar.getSamplingLocation());
        assertEquals(dataVar.toString(), "Sampling Depth: 5 meters", dataVar.getSamplingElevation());
        assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
        assertEquals(dataVar.toString(), "", dataVar.getReplication());
        strList = dataVar.getSamplerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "Equilibrator", strList.get(0));
        strList = dataVar.getAnalyzerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "CO2 Sensor", strList.get(0));
        assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

        assertTrue(var instanceof GasConc);
        gasConc = (GasConc) var;
        assertEquals(gasConc.toString(), "Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                        "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                gasConc.getDryingMethod());
        assertEquals(gasConc.toString(), "", gasConc.getWaterVaporCorrection());

        assertTrue(var instanceof AquGasConc);
        aquGasConc = (AquGasConc) var;
        assertEquals(aquGasConc.toString(), "Sea surface temperature", aquGasConc.getReportTemperature());
        assertEquals(aquGasConc.toString(), "", aquGasConc.getTemperatureCorrection());


        var = variables.get(9);
        assertTrue(var instanceof DataVar);
        dataVar = (DataVar) var;
        assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
        assertEquals(dataVar.toString(), MethodType.COMPUTED, dataVar.getMeasureMethod());
        assertEquals(dataVar.toString(), "Infrared absorption of dry sample gas.", dataVar.getMethodDescription());
        assertEquals(dataVar.toString(), "Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                "Recommendations for autonomous underway pCO2 measuring systems \n" +
                "and data reduction routines, Deep-Sea Res II, 56, 512-522.", dataVar.getMethodReference());
        assertEquals(dataVar.toString(), "Bow tower ~10 m above the sea surface.", dataVar.getSamplingLocation());
        assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
        assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
        assertEquals(dataVar.toString(), "", dataVar.getReplication());
        assertEquals(dataVar.toString(), 0, dataVar.getSamplerNames().size());
        strList = dataVar.getAnalyzerNames();
        assertEquals(dataVar.toString(), 1, strList.size());
        assertEquals(dataVar.toString(), "CO2 Sensor", strList.get(0));
        assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

        assertTrue(var instanceof GasConc);
        gasConc = (GasConc) var;
        assertEquals(gasConc.toString(), "Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                        "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                gasConc.getDryingMethod());
        assertEquals(gasConc.toString(), "", gasConc.getWaterVaporCorrection());

        assertFalse(var instanceof AquGasConc);
         */

        var = variables.get(10);
        assertEquals("dfCO2_uatm", var.getChildTextTrim(abbrev));
        assertEquals("Sea water fCO2 minus interpolated air fCO2 (microatmospheres)", var.getChildTextTrim(fullname));
        assertEquals(null, var.getChildTextTrim(unit));
        assertEquals(null, var.getChildTextTrim(flag));
        assertEquals(null, var.getChildTextTrim(uncertainty));
        assertEquals(null, var.getChildTextTrim(detailedInfo));


        var = variables.get(11);
        assertEquals("WOCE_QC_FLAG", var.getChildTextTrim(abbrev));
        assertEquals("Quality control flag for fCO2 values (2=good, 3=questionable)", var.getChildTextTrim(fullname));
        assertEquals(null, var.getChildTextTrim(unit));
        assertEquals(null, var.getChildTextTrim(flag));
        assertEquals(null, var.getChildTextTrim(uncertainty));
        assertEquals(null, var.getChildTextTrim(detailedInfo));


        var = variables.get(12);
        assertEquals("QC_SUBFLAG", var.getChildTextTrim(abbrev));
        assertEquals("Quality control subflag for fCO2 values, provides explanation when QC flag=3",
                var.getChildTextTrim(fullname));
        assertEquals(null, var.getChildTextTrim(unit));
        assertEquals(null, var.getChildTextTrim(flag));
        assertEquals(null, var.getChildTextTrim(uncertainty));
        assertEquals(null, var.getChildTextTrim(detailedInfo));

    }

}

