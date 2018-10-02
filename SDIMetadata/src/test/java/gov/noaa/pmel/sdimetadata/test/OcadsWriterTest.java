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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class OcadsWriterTest {

    private class MyDocHandler extends DocumentHandler {
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

        assertEquals("", docHandler.getElementText(null, "related" + SEP + "name"));

        assertEquals("2016-01-20", docHandler.getElementText(null, "submissiondate"));
        assertEquals(0, docHandler.getElementList(null, "update").size());

        {
            List<Element> elemList = docHandler.getElementList(null, "datasubmitter");
            assertEquals(1, elemList.size());
            Element elem = elemList.get(0);
            assertEquals("Robert Castle", elem.getChildTextTrim("name"));
            assertEquals("NOAA/Atlantic Oceanographic & Meteorological Laboratory",
                    elem.getChildTextTrim("organization"));
            assertEquals("4301 Rickenbacker Causeway; Miami, FL 33149", elem.getChildTextTrim("deliverypoint1"));
            assertNull(elem.getChildTextTrim("deliverypoint2"));
            assertNull(elem.getChildTextTrim("city"));
            assertNull(elem.getChildTextTrim("administrativeArea"));
            assertNull(elem.getChildTextTrim("zip"));
            assertNull(elem.getChildTextTrim("country"));
            assertEquals("305-361-4418", elem.getChildTextTrim("phone"));
            assertEquals("Robert.Castle@noaa.gov", elem.getChildTextTrim("email"));
            assertNull(elem.getChildTextTrim("ID"));
            assertNull(elem.getChildTextTrim("IDtype"));
        }

        {
            List<Element> elemList = docHandler.getElementList(null, "person");
            assertEquals(1, elemList.size());
            Element elem = elemList.get(0);
            assertEquals("Rik Wanninkhof", elem.getChildTextTrim("name"));
            assertEquals("NOAA/AOML", elem.getChildTextTrim("organization"));
            assertEquals("4301 Rickenbacker Causeway; Miami Fl, 33149", elem.getChildTextTrim("deliverypoint1"));
            assertNull(elem.getChildTextTrim("deliverypoint2"));
            assertNull(elem.getChildTextTrim("city"));
            assertNull(elem.getChildTextTrim("administrativeArea"));
            assertNull(elem.getChildTextTrim("zip"));
            assertNull(elem.getChildTextTrim("country"));
            assertEquals("305-361-4379", elem.getChildTextTrim("phone"));
            assertEquals("Rik.Wanninkhof@noaa.gov", elem.getChildTextTrim("email"));
            assertNull(elem.getChildTextTrim("ID"));
            assertNull(elem.getChildTextTrim("IDtype"));
            assertEquals("investigator", elem.getChildTextTrim("role"));
        }

        assertEquals("", docHandler.getElementText(null, "title"));
        assertEquals("", docHandler.getElementText(null, "abstract"));
        assertEquals("", docHandler.getElementText(null, "purpose"));

        assertEquals("2015-01-15", docHandler.getElementText(null, "startdate"));
        assertEquals("2015-01-29", docHandler.getElementText(null, "enddate"));
        assertEquals("-158.0", docHandler.getElementText(null, "westbd"));
        assertEquals("-122.6", docHandler.getElementText(null, "eastbd"));
        assertEquals("-21.2", docHandler.getElementText(null, "southbd"));
        assertEquals("38.0", docHandler.getElementText(null, "northbd"));
        assertEquals(Coverage.WGS84, docHandler.getElementText(null, "spatialReference"));
        assertEquals(0, docHandler.getElementList(null, "geographicName").size());

        assertEquals("NOAA Climate Observation Office/Climate Observations Division",
                docHandler.getElementText(null, "fundingAgency" + SEP + "agency"));
        assertEquals("", docHandler.getElementText(null, "fundingAgency" + SEP + "title"));
        assertEquals("", docHandler.getElementText(null, "fundingAgency" + SEP + "ID"));

        assertEquals("", docHandler.getElementText(null, "researchProject"));

        assertEquals("Ronald H. Brown", docHandler.getElementText(null, "Platform" + SEP + "PlatformName"));
        assertEquals("33RO", docHandler.getElementText(null, "Platform" + SEP + "PlatformID"));
        assertEquals("Ship", docHandler.getElementText(null, "Platform" + SEP + "PlatformType"));
        assertEquals("NOAA", docHandler.getElementText(null, "Platform" + SEP + "PlatformOwner"));
        assertEquals("", docHandler.getElementText(null, "Platform" + SEP + "PlatformCountry"));

        assertEquals("33RO20150114", docHandler.getElementText(null, "expocode"));
        assertEquals("RB1501A", docHandler.getElementText(null, "cruiseID"));
        assertEquals("", docHandler.getElementText(null, "section"));

        assertEquals("Wanninkhof, R., R. D. Castle, and J. Shannahoff. 2013. " +
                        "Underway pCO2 measurements aboard the R/V Ronald H. Brown during the 2014 cruises. " +
                        "http://cdiac.ornl.gov/ftp/oceans/VOS_Ronald_Brown/RB2013/. Carbon Dioxide Information " +
                        "Analysis Center, Oak Ridge National Laboratory, US Department of Energy, Oak Ridge, Tennessee. " +
                        "doi: 10.3334/CDIAC/OTG.VOS_RB_2012",
                docHandler.getElementText(null, "citation"));

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
                docHandler.getElementText(null, "reference"));

        assertEquals("Port of Call: Honolulu, HI\n" +
                        "Port of Call: San Francisco, CA\n" +
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
                docHandler.getElementText(null, "suppleInfo"));

        List<Element> variables = docHandler.getElementList(null, "variable");
        assertEquals(13, variables.size());

        final String abbrev = "variable" + SEP + "abbrev";
        final String fullname = "variable" + SEP + "fullname";
        final String unit = "variable" + SEP + "unit";
        final String uncertainty = "variable" + SEP + "uncertainty";
        final String flag = "variable" + SEP + "flag";
        final String detailedInfo = "variable" + SEP + "detailedInfo";

        final String observationType = "variable" + SEP + "observationType";
        final String insitu = "variable" + SEP + "insitu";
        final String measured = "variable" + SEP + "measured";
        final String calcMethod = "variable" + SEP + "calcMethod";
        final String samplingInstrument = "variable" + SEP + "samplingInstrument";
        final String analyzingInstrument = "variable" + SEP + "analyzingInstrument";
        final String duration = "variable" + SEP + "duration";
        final String replicate = "variable" + SEP + "replicate";
        final String methodReference = "variable" + SEP + "methodReference";
        final String researcherName = "variable" + SEP + "researcherName";
        final String researcherInstitution = "variable" + SEP + "researcherInstitution";
        final String internal = "variable" + SEP + "internal";

        // final String manipulationMethod = "variable" + SEP + "manipulationMethod"; - nothing in SDIMetadata for this
        // final String biologicalSubject = "variable" + SEP + "biologicalSubject"; - nothing in SDIMetadata for this
        // final String speciesID = "variable" + SEP + "speciesID"; - nothing in SDIMetadata for this
        // final String lifeStage = "variable" + SEP + "lifeStage"; - nothing in SDIMetadata for this

        final String locationSeawaterIntake = "variable" + SEP + "locationSeawaterIntake";
        final String depthSeawaterIntake = "variable" + SEP + "DepthSeawaterIntake";
        final String dryMethod = "variable" + SEP + "equilibrator" + SEP + "dryMethod";
        final String equilibratorType = "variable" + SEP + "equilibrator" + SEP + "type";
        final String equilibratorVolume = "variable" + SEP + "equilibrator" + SEP + "volume";
        final String equilibratorVented = "variable" + SEP + "equilibrator" + SEP + "vented";
        final String equilibratorWaterFlowRate = "variable" + SEP + "equilibrator" + SEP + "waterFlowRate";
        final String equilibratorGasFlowRate = "variable" + SEP + "equilibrator" + SEP + "gasFlowRate";
        final String temperatureEquilibratorMethod = "variable" + SEP + "equilibrator" + SEP + "temperatureEquilibratorMethod";
        final String pressureEquilibratorMethod = "variable" + SEP + "equilibrator" + SEP + "pressureEquilibratorMethod";
        final String gasDetectorManufacturer = "variable" + SEP + "gasDetector" + SEP + "manufacturer";
        final String gasDetectorModel = "variable" + SEP + "gasDetector" + SEP + "model";
        final String gasDetectorResolution = "variable" + SEP + "gasDetector" + SEP + "resolution";
        final String gasDetectorUncertainty = "variable" + SEP + "gasDetector" + SEP + "uncertainty";
        final String standardizationDescription = "variable" + SEP + "standardization" + SEP + "description";
        final String standardizationFrequency = "variable" + SEP + "standardization" + SEP + "frequency";
        final String standardizationTemperatureStd = "variable" + SEP + "standardization" + SEP + "temperatureStd";
        final String waterVaporCorrection = "variable" + SEP + "waterVaporCorrection";
        final String temperatureCorrection = "variable" + SEP + "temperatureCorrection";
        final String co2ReportTemperature = "variable" + SEP + "co2ReportTemperature";
        final String seawatervol = "variable" + SEP + "seawatervol";
        final String headspacevol = "variable" + SEP + "headspacevol";
        final String temperatureMeasure = "variable" + SEP + "temperatureMeasure";

        final String standardGas = "variable" + SEP + "standardization" + SEP + "standardgas";
        final String standardGasManufacturer = standardGas + SEP + "manufacturer";
        final String standardGasConcentration = standardGas + SEP + "concentration";
        final String standardGasUncertainty = standardGas + SEP + "uncertainty";

        {
            Element var = variables.get(0);
            assertEquals("xCO2_EQU_ppm", docHandler.getElementText(var, abbrev));
            assertEquals("Mole fraction of CO2 in the equilibrator headspace (dry) at equilibrator temperature (ppm)",
                    docHandler.getElementText(var, fullname));
            assertEquals("", docHandler.getElementText(var, unit));
            assertEquals("Given in column: WOCE_QC_FLAG", docHandler.getElementText(var, flag));
            assertEquals("1 microatmospheres", docHandler.getElementText(var, uncertainty));
            assertEquals("Resolution/Precision: 0.01 microatmosphere\n" +
                            "Frequency: Every 150 seconds",
                    docHandler.getElementText(var, detailedInfo));
            assertEquals("Surface Underway", docHandler.getElementText(var, observationType));
            assertEquals("Measured in-situ", docHandler.getElementText(var, insitu));
            assertEquals("Measured in-situ", docHandler.getElementText(var, measured));
            assertEquals("Infrared absorption of dry sample gas.", docHandler.getElementText(var, calcMethod));
            assertEquals("Equilibrator: Primary equlibrator is vented through a secondary equilibrator",
                    docHandler.getElementText(var, samplingInstrument));
            assertEquals("CO2 Sensor: Manufacturer: LI-COR; Model: LI-6262; Calibration: The analyzer is " +
                            "calibrated every 3.25 hours with standards from ESRL in Boulder, CO that are directly " +
                            "traceable to the WMO scale.  The zero gas is 99.9% nitrogen.; The instrument is located " +
                            "in an air-conditioned laboratory.  99.9% Nitrogen gas and the high standard (Std 4) are " +
                            "used to set the zero and span of the LI-COR analyzer.",
                    docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                            "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                            "Recommendations for autonomous underway pCO2 measuring systems \n" +
                            "and data reduction routines, Deep-Sea Res II, 56, 512-522.",
                    docHandler.getElementText(var, methodReference));
            assertEquals("Bow", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("Sampling Depth: 5 meters", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("Sprayhead above dynamic pool, with thermal jacket",
                    docHandler.getElementText(var, equilibratorType));
            assertEquals("0.95 L (0.4 L water, 0.55 L headspace)", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("Yes", docHandler.getElementText(var, equilibratorVented));
            assertEquals("1.5 - 2.0 L/min", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("70 - 150 ml/min", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                            "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                    docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            List<Element> elemList = docHandler.getElementList(var, standardGas);
            assertEquals(0, elemList.size());
            // assertEquals(4, elemList.size());
            for (Element stdgas : elemList) {
                assertEquals("", docHandler.getElementText(stdgas, standardGasManufacturer));
                assertEquals("", docHandler.getElementText(stdgas, standardGasConcentration));
                assertEquals("", docHandler.getElementText(stdgas, standardGasUncertainty));
            }
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("4", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(1);
            assertEquals("xCO2_ATM_ppm", docHandler.getElementText(var, abbrev));
            assertEquals("Mole fraction of CO2 measured in dry outside air (ppm)",
                    docHandler.getElementText(var, fullname));
            assertEquals("", docHandler.getElementText(var, unit));
            assertEquals("", docHandler.getElementText(var, flag));
            assertEquals("0.2 ppm", docHandler.getElementText(var, uncertainty));
            assertEquals("Drying Method: Gas stream passes through a thermoelectric condenser (~5 °C) and " +
                            "then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).\n" +
                            "Sampling location: Bow tower ~10 m above the sea surface.\n" +
                            "Resolution/Precision: 0.01 ppm\n" +
                            "Measurement: Yes, 5 readings in a group every 3.25 hours.",
                    docHandler.getElementText(var, detailedInfo));
            assertEquals("Surface Underway", docHandler.getElementText(var, observationType));
            assertEquals("Measured in-situ", docHandler.getElementText(var, insitu));
            assertEquals("Measured in-situ", docHandler.getElementText(var, measured));
            assertEquals("Infrared absorption of dry sample gas.", docHandler.getElementText(var, calcMethod));
            assertEquals("", docHandler.getElementText(var, samplingInstrument));
            assertEquals("CO2 Sensor: Manufacturer: LI-COR; Model: LI-6262; Calibration: The analyzer is " +
                            "calibrated every 3.25 hours with standards from ESRL in Boulder, CO that are directly " +
                            "traceable to the WMO scale.  The zero gas is 99.9% nitrogen.; The instrument is located " +
                            "in an air-conditioned laboratory.  99.9% Nitrogen gas and the high standard (Std 4) " +
                            "are used to set the zero and span of the LI-COR analyzer.",
                    docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                            "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                            "Recommendations for autonomous underway pCO2 measuring systems \n" +
                            "and data reduction routines, Deep-Sea Res II, 56, 512-522.",
                    docHandler.getElementText(var, methodReference));
            assertEquals("", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, equilibratorType));
            assertEquals("", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("", docHandler.getElementText(var, equilibratorVented));
            assertEquals("", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            assertEquals(0, docHandler.getElementList(var, standardGas).size());
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("0", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(2);
            assertEquals("xCO2_ATM_interpolated_ppm", docHandler.getElementText(var, abbrev));
            assertEquals("Mole fraction of CO2 in outside air associated with each water analysis.  " +
                            "These values are interpolated between the bracketing averaged good xCO2_ATM analyses (ppm)",
                    docHandler.getElementText(var, fullname));
            assertEquals("", docHandler.getElementText(var, unit));
            assertEquals("", docHandler.getElementText(var, flag));
            assertEquals("0.2 ppm", docHandler.getElementText(var, uncertainty));
            assertEquals("Drying Method: Gas stream passes through a thermoelectric condenser (~5 °C) and " +
                            "then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).\n" +
                            "Sampling location: Bow tower ~10 m above the sea surface.\n" +
                            "Resolution/Precision: 0.01 ppm\n" +
                            "Measurement: Yes, 5 readings in a group every 3.25 hours.",
                    docHandler.getElementText(var, detailedInfo));
            assertEquals("Surface Underway", docHandler.getElementText(var, observationType));
            assertEquals("Measured in-situ", docHandler.getElementText(var, insitu));
            assertEquals("Measured in-situ", docHandler.getElementText(var, measured));
            assertEquals("Infrared absorption of dry sample gas.", docHandler.getElementText(var, calcMethod));
            assertEquals("", docHandler.getElementText(var, samplingInstrument));
            assertEquals("CO2 Sensor: Manufacturer: LI-COR; Model: LI-6262; Calibration: The analyzer is " +
                            "calibrated every 3.25 hours with standards from ESRL in Boulder, CO that are directly " +
                            "traceable to the WMO scale.  The zero gas is 99.9% nitrogen.; The instrument is located " +
                            "in an air-conditioned laboratory.  99.9% Nitrogen gas and the high standard (Std 4) " +
                            "are used to set the zero and span of the LI-COR analyzer.",
                    docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                            "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                            "Recommendations for autonomous underway pCO2 measuring systems \n" +
                            "and data reduction routines, Deep-Sea Res II, 56, 512-522.",
                    docHandler.getElementText(var, methodReference));
            assertEquals("", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, equilibratorType));
            assertEquals("", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("", docHandler.getElementText(var, equilibratorVented));
            assertEquals("", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            assertEquals(0, docHandler.getElementList(var, standardGas).size());
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("0", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(3);
            assertEquals("PRES_EQU_hPa", docHandler.getElementText(var, abbrev));
            assertEquals("Barometric pressure in the equilibrator headspace (hectopascals)",
                    docHandler.getElementText(var, fullname));
            assertEquals("hPa", docHandler.getElementText(var, unit));
            assertEquals("", docHandler.getElementText(var, flag));
            assertEquals("0.05 hPa", docHandler.getElementText(var, uncertainty));
            assertEquals("Sampling location: Attached to CO2 analyzer exit to lab.\n" +
                    "Resolution/Precision: 0.015 hPa", docHandler.getElementText(var, detailedInfo));
            assertEquals("Surface Underway", docHandler.getElementText(var, observationType));
            assertEquals("Measured in-situ", docHandler.getElementText(var, insitu));
            assertEquals("Measured in-situ", docHandler.getElementText(var, measured));
            assertEquals("", docHandler.getElementText(var, calcMethod));
            assertEquals("", docHandler.getElementText(var, samplingInstrument));
            assertEquals("Equilibrator Pressure Sensor: Manufacturer: Setra; Model: 270; Calibration: Factory " +
                            "calibration.; Pressure reading from the Setra-270 on the exit of the analyzer was added " +
                            "to the differential pressure reading from Setra-239 attached to the equilibrator headspace " +
                            "to yield the equlibrator pressure.",
                    docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("", docHandler.getElementText(var, methodReference));
            assertEquals("", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, equilibratorType));
            assertEquals("", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("", docHandler.getElementText(var, equilibratorVented));
            assertEquals("", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            assertEquals(0, docHandler.getElementList(var, standardGas).size());
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("0", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(4);
            assertEquals("PRES_ATM@SSP_hPa", docHandler.getElementText(var, abbrev));
            assertEquals("Barometric pressure measured outside, corrected to sea level (hectopascals)",
                    docHandler.getElementText(var, fullname));
            assertEquals("hPa", docHandler.getElementText(var, unit));
            assertEquals("", docHandler.getElementText(var, flag));
            assertEquals("0.2 hPa", docHandler.getElementText(var, uncertainty));
            assertEquals("Pressure Correction: Normalized: yes\n" +
                    "Sampling location: On bulkhead exterior on the port side of the radio room aft of the bridge " +
                    "at ~14 m above the sea surface.\n" +
                    "Resolution/Precision: 0.08 hPa", docHandler.getElementText(var, detailedInfo));
            assertEquals("Surface Underway", docHandler.getElementText(var, observationType));
            assertEquals("Measured in-situ", docHandler.getElementText(var, insitu));
            assertEquals("Measured in-situ", docHandler.getElementText(var, measured));
            assertEquals("", docHandler.getElementText(var, calcMethod));
            assertEquals("", docHandler.getElementText(var, samplingInstrument));
            assertEquals("Atmospheric Pressure Sensor: Manufacturer: Vaisala; Model: PTB330; Calibration: " +
                            "Factory calibration; Manufacturer's resolution is taken as precision. Maintained by ship.",
                    docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("", docHandler.getElementText(var, methodReference));
            assertEquals("", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, equilibratorType));
            assertEquals("", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("", docHandler.getElementText(var, equilibratorVented));
            assertEquals("", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            assertEquals(0, docHandler.getElementList(var, standardGas).size());
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("0", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(5);
            assertEquals("TEMP_EQU_C", docHandler.getElementText(var, abbrev));
            assertEquals("Water temperature in equilibrator (degrees Celsius)",
                    docHandler.getElementText(var, fullname));
            assertEquals("deg C", docHandler.getElementText(var, unit));
            assertEquals("", docHandler.getElementText(var, flag));
            assertEquals("0.025 °C", docHandler.getElementText(var, uncertainty));
            assertEquals("Sampling location: In Hydro Lab, inserted into equilibrator ~ 5 cm below water line.\n" +
                    "Resolution/Precision: 0.01 °C", docHandler.getElementText(var, detailedInfo));
            assertEquals("Surface Underway", docHandler.getElementText(var, observationType));
            assertEquals("Measured in-situ", docHandler.getElementText(var, insitu));
            assertEquals("Measured in-situ", docHandler.getElementText(var, measured));
            assertEquals("", docHandler.getElementText(var, calcMethod));
            assertEquals("", docHandler.getElementText(var, samplingInstrument));
            assertEquals("Equilibrator Temperature Sensor: Manufacturer: Hart; Model: 1521; " +
                            "Calibration: Factory calibration; Warming: 0.1 - 0.6 °C",
                    docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("", docHandler.getElementText(var, methodReference));
            assertEquals("", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, equilibratorType));
            assertEquals("", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("", docHandler.getElementText(var, equilibratorVented));
            assertEquals("", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            assertEquals(0, docHandler.getElementList(var, standardGas).size());
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("0", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(6);
            assertEquals("SST_C", docHandler.getElementText(var, abbrev));
            assertEquals("Sea surface temperature (degrees Celsius)", docHandler.getElementText(var, fullname));
            assertEquals("deg C", docHandler.getElementText(var, unit));
            assertEquals("", docHandler.getElementText(var, flag));
            assertEquals("0.01 °C", docHandler.getElementText(var, uncertainty));
            assertEquals("Sampling location: Bow thruster room, before sea water pump, ~5 m below water line.\n" +
                    "Resolution/Precision: 0.001 °C", docHandler.getElementText(var, detailedInfo));
            assertEquals("Surface Underway", docHandler.getElementText(var, observationType));
            assertEquals("Measured in-situ", docHandler.getElementText(var, insitu));
            assertEquals("Measured in-situ", docHandler.getElementText(var, measured));
            assertEquals("", docHandler.getElementText(var, calcMethod));
            assertEquals("", docHandler.getElementText(var, samplingInstrument));
            assertEquals("Water Temperature Sensor: Manufacturer: Seabird; Model: SBE-21; Calibration: " +
                            "Factory calibration; Manufacturer's resolution is taken as precision. Maintained by ship.",
                    docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("", docHandler.getElementText(var, methodReference));
            assertEquals("", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, equilibratorType));
            assertEquals("", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("", docHandler.getElementText(var, equilibratorVented));
            assertEquals("", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            assertEquals(0, docHandler.getElementList(var, standardGas).size());
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("0", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(7);
            assertEquals("SAL_permil", docHandler.getElementText(var, abbrev));
            assertEquals("Sea surface salinity on Practical Salinity Scale (permil)",
                    docHandler.getElementText(var, fullname));
            assertEquals("", docHandler.getElementText(var, unit));
            assertEquals("", docHandler.getElementText(var, flag));
            assertEquals("0.005 permil", docHandler.getElementText(var, uncertainty));
            assertEquals("Sampling location: Attached to underway system at sea water input.\n" +
                            "Resolution/Precision: 0.0002 permil",
                    docHandler.getElementText(var, detailedInfo));
            assertEquals("Surface Underway", docHandler.getElementText(var, observationType));
            assertEquals("Measured in-situ", docHandler.getElementText(var, insitu));
            assertEquals("Measured in-situ", docHandler.getElementText(var, measured));
            assertEquals("", docHandler.getElementText(var, calcMethod));
            assertEquals("", docHandler.getElementText(var, samplingInstrument));
            assertEquals("Salinity Sensor: Manufacturer: Seabird; Model: SBE-45; Calibration: Factory " +
                            "calibration.; Manufacturer's resolution is taken as precision.",
                    docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("", docHandler.getElementText(var, methodReference));
            assertEquals("", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, equilibratorType));
            assertEquals("", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("", docHandler.getElementText(var, equilibratorVented));
            assertEquals("", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            assertEquals(0, docHandler.getElementList(var, standardGas).size());
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("0", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(8);
            assertEquals("fCO2_SW@SST_uatm", docHandler.getElementText(var, abbrev));
            assertEquals("Fugacity of CO2 in sea water at SST and 100% humidity (microatmospheres)",
                    docHandler.getElementText(var, fullname));
            assertEquals("", docHandler.getElementText(var, unit));
            assertEquals("Given in column: WOCE_QC_FLAG", docHandler.getElementText(var, flag));
            assertEquals("1 microatmospheres", docHandler.getElementText(var, uncertainty));
            assertEquals("Resolution/Precision: 0.01 microatmosphere\n" +
                            "Frequency: Every 150 seconds",
                    docHandler.getElementText(var, detailedInfo));
            assertEquals("Surface Underway", docHandler.getElementText(var, observationType));
            assertEquals("Measured in-situ", docHandler.getElementText(var, insitu));
            assertEquals("Measured in-situ", docHandler.getElementText(var, measured));
            assertEquals("Infrared absorption of dry sample gas.", docHandler.getElementText(var, calcMethod));
            assertEquals("Equilibrator: Primary equlibrator is vented through a secondary equilibrator",
                    docHandler.getElementText(var, samplingInstrument));
            assertEquals("CO2 Sensor: Manufacturer: LI-COR; Model: LI-6262; Calibration: The analyzer is " +
                            "calibrated every 3.25 hours with standards from ESRL in Boulder, CO that are directly " +
                            "traceable to the WMO scale.  The zero gas is 99.9% nitrogen.; The instrument is located " +
                            "in an air-conditioned laboratory.  99.9% Nitrogen gas and the high standard (Std 4) " +
                            "are used to set the zero and span of the LI-COR analyzer.",
                    docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                            "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                            "Recommendations for autonomous underway pCO2 measuring systems \n" +
                            "and data reduction routines, Deep-Sea Res II, 56, 512-522.",
                    docHandler.getElementText(var, methodReference));
            assertEquals("Bow", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("Sampling Depth: 5 meters", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("Sprayhead above dynamic pool, with thermal jacket",
                    docHandler.getElementText(var, equilibratorType));
            assertEquals("0.95 L (0.4 L water, 0.55 L headspace)", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("Yes", docHandler.getElementText(var, equilibratorVented));
            assertEquals("1.5 - 2.0 L/min", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("70 - 150 ml/min", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                            "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                    docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            List<Element> elemList = docHandler.getElementList(var, standardGas);
            assertEquals(0, elemList.size());
            // assertEquals(4, elemList.size());
            for (Element stdgas : elemList) {
                assertEquals("", docHandler.getElementText(stdgas, standardGasManufacturer));
                assertEquals("", docHandler.getElementText(stdgas, standardGasManufacturer));
                assertEquals("", docHandler.getElementText(stdgas, standardGasConcentration));
                assertEquals("", docHandler.getElementText(stdgas, standardGasUncertainty));
            }
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("4", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(9);
            assertEquals("fCO2_ATM_interpolated_uatm", docHandler.getElementText(var, abbrev));
            assertEquals("Fugacity of CO2 in air corresponding to the interpolated xCO2 at SST " +
                            "and 100% humidity (microatmospheres)",
                    docHandler.getElementText(var, fullname));
            assertEquals("", docHandler.getElementText(var, unit));
            assertEquals("", docHandler.getElementText(var, flag));
            assertEquals("0.2 ppm", docHandler.getElementText(var, uncertainty));
            assertEquals("Drying Method: Gas stream passes through a thermoelectric condenser (~5 °C) and " +
                            "then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).\n" +
                            "Sampling location: Bow tower ~10 m above the sea surface.\n" +
                            "Resolution/Precision: 0.01 ppm\n" +
                            "Measurement: Yes, 5 readings in a group every 3.25 hours.",
                    docHandler.getElementText(var, detailedInfo));
            assertEquals("Surface Underway", docHandler.getElementText(var, observationType));
            assertEquals("Measured in-situ", docHandler.getElementText(var, insitu));
            assertEquals("Measured in-situ", docHandler.getElementText(var, measured));
            assertEquals("Infrared absorption of dry sample gas.", docHandler.getElementText(var, calcMethod));
            assertEquals("", docHandler.getElementText(var, samplingInstrument));
            assertEquals("CO2 Sensor: Manufacturer: LI-COR; Model: LI-6262; Calibration: The analyzer is " +
                            "calibrated every 3.25 hours with standards from ESRL in Boulder, CO that are directly " +
                            "traceable to the WMO scale.  The zero gas is 99.9% nitrogen.; The instrument is located " +
                            "in an air-conditioned laboratory.  99.9% Nitrogen gas and the high standard (Std 4) " +
                            "are used to set the zero and span of the LI-COR analyzer.",
                    docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                            "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                            "Recommendations for autonomous underway pCO2 measuring systems \n" +
                            "and data reduction routines, Deep-Sea Res II, 56, 512-522.",
                    docHandler.getElementText(var, methodReference));
            assertEquals("", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, equilibratorType));
            assertEquals("", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("", docHandler.getElementText(var, equilibratorVented));
            assertEquals("", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            assertEquals(0, docHandler.getElementList(var, standardGas).size());
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("0", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(10);
            assertEquals("dfCO2_uatm", docHandler.getElementText(var, abbrev));
            assertEquals("Sea water fCO2 minus interpolated air fCO2 (microatmospheres)",
                    docHandler.getElementText(var, fullname));
            assertEquals("", docHandler.getElementText(var, unit));
            assertEquals("", docHandler.getElementText(var, flag));
            assertEquals("", docHandler.getElementText(var, uncertainty));
            assertEquals("", docHandler.getElementText(var, detailedInfo));
            assertEquals("", docHandler.getElementText(var, observationType));
            assertEquals("", docHandler.getElementText(var, insitu));
            assertEquals("", docHandler.getElementText(var, measured));
            assertEquals("", docHandler.getElementText(var, calcMethod));
            assertEquals("", docHandler.getElementText(var, samplingInstrument));
            assertEquals("", docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("", docHandler.getElementText(var, methodReference));
            assertEquals("", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, equilibratorType));
            assertEquals("", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("", docHandler.getElementText(var, equilibratorVented));
            assertEquals("", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            assertEquals(0, docHandler.getElementList(var, standardGas).size());
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(11);
            assertEquals("WOCE_QC_FLAG", docHandler.getElementText(var, abbrev));
            assertEquals("Quality control flag for fCO2 values (2=good, 3=questionable)",
                    docHandler.getElementText(var, fullname));
            assertEquals("", docHandler.getElementText(var, unit));
            assertEquals("", docHandler.getElementText(var, flag));
            assertEquals("", docHandler.getElementText(var, uncertainty));
            assertEquals("", docHandler.getElementText(var, detailedInfo));
            assertEquals("", docHandler.getElementText(var, observationType));
            assertEquals("", docHandler.getElementText(var, insitu));
            assertEquals("", docHandler.getElementText(var, measured));
            assertEquals("", docHandler.getElementText(var, calcMethod));
            assertEquals("", docHandler.getElementText(var, samplingInstrument));
            assertEquals("", docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("", docHandler.getElementText(var, methodReference));
            assertEquals("", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, equilibratorType));
            assertEquals("", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("", docHandler.getElementText(var, equilibratorVented));
            assertEquals("", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            assertEquals(0, docHandler.getElementList(var, standardGas).size());
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("", docHandler.getElementText(var, internal));
        }

        {
            Element var = variables.get(12);
            assertEquals("QC_SUBFLAG", docHandler.getElementText(var, abbrev));
            assertEquals("Quality control subflag for fCO2 values, provides explanation when QC flag=3",
                    docHandler.getElementText(var, fullname));
            assertEquals("", docHandler.getElementText(var, unit));
            assertEquals("", docHandler.getElementText(var, flag));
            assertEquals("", docHandler.getElementText(var, uncertainty));
            assertEquals("", docHandler.getElementText(var, detailedInfo));
            assertEquals("", docHandler.getElementText(var, observationType));
            assertEquals("", docHandler.getElementText(var, insitu));
            assertEquals("", docHandler.getElementText(var, measured));
            assertEquals("", docHandler.getElementText(var, calcMethod));
            assertEquals("", docHandler.getElementText(var, samplingInstrument));
            assertEquals("", docHandler.getElementText(var, analyzingInstrument));
            assertEquals("", docHandler.getElementText(var, duration));
            assertEquals("", docHandler.getElementText(var, replicate));
            assertEquals("", docHandler.getElementText(var, methodReference));
            assertEquals("", docHandler.getElementText(var, locationSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, depthSeawaterIntake));
            assertEquals("", docHandler.getElementText(var, equilibratorType));
            assertEquals("", docHandler.getElementText(var, equilibratorVolume));
            assertEquals("", docHandler.getElementText(var, equilibratorVented));
            assertEquals("", docHandler.getElementText(var, equilibratorWaterFlowRate));
            assertEquals("", docHandler.getElementText(var, equilibratorGasFlowRate));
            assertEquals("", docHandler.getElementText(var, temperatureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, pressureEquilibratorMethod));
            assertEquals("", docHandler.getElementText(var, dryMethod));
            assertEquals("", docHandler.getElementText(var, gasDetectorManufacturer));
            assertEquals("", docHandler.getElementText(var, gasDetectorModel));
            assertEquals("", docHandler.getElementText(var, gasDetectorResolution));
            assertEquals("", docHandler.getElementText(var, gasDetectorUncertainty));
            assertEquals("", docHandler.getElementText(var, standardizationDescription));
            assertEquals("", docHandler.getElementText(var, standardizationFrequency));
            assertEquals("", docHandler.getElementText(var, standardizationTemperatureStd));
            assertEquals(0, docHandler.getElementList(var, standardGas).size());
            assertEquals("", docHandler.getElementText(var, seawatervol));
            assertEquals("", docHandler.getElementText(var, headspacevol));
            assertEquals("", docHandler.getElementText(var, temperatureMeasure));
            assertEquals("", docHandler.getElementText(var, waterVaporCorrection));
            assertEquals("", docHandler.getElementText(var, co2ReportTemperature));
            assertEquals("", docHandler.getElementText(var, temperatureCorrection));
            assertEquals("", docHandler.getElementText(var, researcherName));
            assertEquals("", docHandler.getElementText(var, researcherInstitution));
            assertEquals("", docHandler.getElementText(var, internal));
        }

    }

}

