package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.Coverage;
import gov.noaa.pmel.socatmetadata.shared.MiscInfo;
import gov.noaa.pmel.socatmetadata.shared.SocatMetadata;
import gov.noaa.pmel.socatmetadata.shared.instrument.Analyzer;
import gov.noaa.pmel.socatmetadata.shared.instrument.CalibrationGas;
import gov.noaa.pmel.socatmetadata.shared.instrument.Equilibrator;
import gov.noaa.pmel.socatmetadata.shared.instrument.GasSensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;
import gov.noaa.pmel.socatmetadata.shared.instrument.PressureSensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.SalinitySensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.Sampler;
import gov.noaa.pmel.socatmetadata.shared.instrument.TemperatureSensor;
import gov.noaa.pmel.socatmetadata.shared.person.Investigator;
import gov.noaa.pmel.socatmetadata.shared.person.Person;
import gov.noaa.pmel.socatmetadata.shared.person.Submitter;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import gov.noaa.pmel.socatmetadata.shared.platform.PlatformType;
import gov.noaa.pmel.socatmetadata.translate.CdiacReader;
import gov.noaa.pmel.socatmetadata.translate.CdiacReader.VarType;
import gov.noaa.pmel.socatmetadata.shared.util.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.util.NumericString;
import gov.noaa.pmel.socatmetadata.shared.variable.AirPressure;
import gov.noaa.pmel.socatmetadata.shared.variable.AquGasConc;
import gov.noaa.pmel.socatmetadata.shared.variable.DataVar;
import gov.noaa.pmel.socatmetadata.shared.variable.GasConc;
import gov.noaa.pmel.socatmetadata.shared.variable.MethodType;
import gov.noaa.pmel.socatmetadata.shared.variable.Temperature;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CdiacReaderTest {

    @Test
    public void testGetVarTypeFromColumnName() {
        CdiacReader reader = new CdiacReader(new StringReader(AOML_CDIAC_XML_DATA_STRING));
        reader.associateColumnNameWithVarType("mynameforsst", VarType.SEA_SURFACE_TEMPERATURE);
        assertEquals(VarType.FCO2_WATER_EQU, reader.getVarTypeFromColumnName("fCO2_equ_w"));
        assertEquals(VarType.FCO2_WATER_SST, reader.getVarTypeFromColumnName("fCO2_SST_100_hum [uatm]"));
        assertEquals(VarType.PCO2_WATER_EQU, reader.getVarTypeFromColumnName("pCO2 SW Equi"));
        assertEquals(VarType.PCO2_WATER_SST, reader.getVarTypeFromColumnName("pCO2w-SST (uatm)"));
        assertEquals(VarType.XCO2_WATER_EQU, reader.getVarTypeFromColumnName("xco2sw ( ppm )!"));
        assertEquals(VarType.XCO2_WATER_SST, reader.getVarTypeFromColumnName("xCO2,Water,SST"));
        assertEquals(VarType.FCO2_ATM_ACTUAL, reader.getVarTypeFromColumnName("fCO2_Atm-Wet"));
        assertEquals(VarType.FCO2_ATM_INTERP, reader.getVarTypeFromColumnName("fCO2-Atm (Interp)"));
        assertEquals(VarType.PCO2_ATM_ACTUAL, reader.getVarTypeFromColumnName("pCO2AtmActual"));
        assertEquals(VarType.PCO2_ATM_INTERP, reader.getVarTypeFromColumnName("pCO2AtmInterp"));
        assertEquals(VarType.XCO2_ATM_ACTUAL, reader.getVarTypeFromColumnName("xCO2AirDry (umol/mol)"));
        assertEquals(VarType.XCO2_ATM_INTERP, reader.getVarTypeFromColumnName("xCO2 AtmI nte rp"));
        assertEquals(VarType.SEA_SURFACE_TEMPERATURE, reader.getVarTypeFromColumnName("SST °C"));
        assertEquals(VarType.EQUILIBRATOR_TEMPERATURE, reader.getVarTypeFromColumnName("Equ Temp"));
        assertEquals(VarType.SEA_LEVEL_PRESSURE, reader.getVarTypeFromColumnName("PPPP"));
        assertEquals(VarType.EQUILIBRATOR_PRESSURE, reader.getVarTypeFromColumnName("PRS EQ"));
        assertEquals(VarType.SALINITY, reader.getVarTypeFromColumnName("Sal <permil>"));
        assertEquals(VarType.WOCE_CO2_WATER, reader.getVarTypeFromColumnName("WOCE {Water}"));
        assertEquals(VarType.WOCE_CO2_ATM, reader.getVarTypeFromColumnName("QC CO2 Air"));
        assertEquals(VarType.OTHER, reader.getVarTypeFromColumnName("Sta"));
        assertEquals(VarType.OTHER, reader.getVarTypeFromColumnName("Day"));
        assertEquals(VarType.OTHER, reader.getVarTypeFromColumnName("Lon"));
        assertEquals(VarType.OTHER, reader.getVarTypeFromColumnName("Lat"));
        assertEquals(VarType.OTHER, reader.getVarTypeFromColumnName("Depth"));
        assertEquals(VarType.OTHER, reader.getVarTypeFromColumnName("Time"));
        assertEquals(VarType.OTHER, reader.getVarTypeFromColumnName("WOCE SST"));
        assertEquals(VarType.OTHER, reader.getVarTypeFromColumnName(""));
        assertEquals(VarType.SEA_SURFACE_TEMPERATURE, reader.getVarTypeFromColumnName("My Name (for SST)"));
    }

    @Test
    public void testCreateSocatMetadata() {
        CdiacReader reader = new CdiacReader(new StringReader(AOML_CDIAC_XML_DATA_STRING));
        SocatMetadata mdata = reader.createSocatMetadata();

        Submitter submitter = mdata.getSubmitter();
        assertEquals(submitter.toString(), "Castle", submitter.getLastName());
        assertEquals(submitter.toString(), "Robert", submitter.getFirstName());
        assertEquals(submitter.toString(), "", submitter.getMiddle());
        assertEquals(submitter.toString(), "", submitter.getId());
        assertEquals(submitter.toString(), "", submitter.getIdType());
        assertEquals(submitter.toString(), "NOAA/Atlantic Oceanographic & Meteorological Laboratory",
                submitter.getOrganization());
        ArrayList<String> strList = submitter.getStreets();
        assertEquals(submitter.toString(), 1, strList.size());
        assertEquals(submitter.toString(), "4301 Rickenbacker Causeway; Miami, FL 33149",
                submitter.getStreets().get(0));
        assertEquals(submitter.toString(), "", submitter.getCity());
        assertEquals(submitter.toString(), "", submitter.getRegion());
        assertEquals(submitter.toString(), "", submitter.getZipCode());
        assertEquals(submitter.toString(), "", submitter.getCountry());
        assertEquals(submitter.toString(), "305-361-4418", submitter.getPhone());
        assertEquals(submitter.toString(), "Robert.Castle@noaa.gov", submitter.getEmail());

        ArrayList<Investigator> invList = mdata.getInvestigators();
        assertEquals(invList.toString(), 1, invList.size());
        Investigator investigator = invList.get(0);
        assertEquals(investigator.toString(), "Wanninkhof", investigator.getLastName());
        assertEquals(investigator.toString(), "Rik", investigator.getFirstName());
        assertEquals(investigator.toString(), "", investigator.getMiddle());
        assertEquals(investigator.toString(), "", investigator.getId());
        assertEquals(investigator.toString(), "", investigator.getIdType());
        assertEquals(investigator.toString(), "NOAA/AOML", investigator.getOrganization());
        strList = investigator.getStreets();
        assertEquals(investigator.toString(), 1, strList.size());
        assertEquals(investigator.toString(), "4301 Rickenbacker Causeway; Miami Fl, 33149",
                investigator.getStreets().get(0));
        assertEquals(investigator.toString(), "", investigator.getCity());
        assertEquals(investigator.toString(), "", investigator.getRegion());
        assertEquals(investigator.toString(), "", investigator.getZipCode());
        assertEquals(investigator.toString(), "", investigator.getCountry());
        assertEquals(investigator.toString(), "305-361-4379", investigator.getPhone());
        assertEquals(investigator.toString(), "Rik.Wanninkhof@noaa.gov", investigator.getEmail());

        MiscInfo info = mdata.getMiscInfo();
        assertEquals(info.toString(), "33RO20150114", info.getDatasetId());
        assertEquals(info.toString(), "RB1501A", info.getDatasetName());
        assertEquals(info.toString(), "", info.getSectionName());
        assertEquals(info.toString(), "NOAA Climate Observation Office/Climate Observations Division",
                info.getFundingAgency());
        assertEquals(info.toString(), "", info.getFundingTitle());
        assertEquals(info.toString(), "", info.getFundingId());
        assertEquals(info.toString(), "", info.getResearchProject());
        assertEquals(info.toString(), "", info.getDatasetDoi());
        assertEquals(info.toString(), "", info.getAccessId());
        assertEquals(info.toString(), "http://www.aoml.noaa.gov/ocd/gcc/rvbrown_introduction.php", info.getWebsite());
        assertEquals(info.toString(), "", info.getDownloadUrl());
        assertEquals(info.toString(), "Wanninkhof, R., R. D. Castle, and J. Shannahoff. 2013. " +
                "Underway pCO2 measurements aboard the R/V Ronald H. Brown during the 2014 cruises. " +
                "http://cdiac.ornl.gov/ftp/oceans/VOS_Ronald_Brown/RB2013/. Carbon Dioxide Information " +
                "Analysis Center, Oak Ridge National Laboratory, US Department of Energy, Oak Ridge, Tennessee. " +
                "doi: 10.3334/CDIAC/OTG.VOS_RB_2012", info.getCitation());
        assertEquals(info.toString(), "", info.getSynopsis());
        assertEquals(info.toString(), "", info.getPurpose());
        assertEquals(info.toString(), new ArrayList<String>(Arrays.asList(
                "DOE (1994). Handbook of methods for the analysis of the various",
                "parameters of the carbon dioxide system in sea water; version",
                "2. DOE.",
                "Feely, R. A., R. Wanninkhof, H. B. Milburn, C. E. Cosca, M. Stapp and",
                "P. P. Murphy (1998) A new automated underway system for making",
                "high precision pCO2 measurements onboard research ships.",
                "Analytica Chim. Acta 377: 185-191.",
                "Ho, D. T., R. Wanninkhof, J. Masters, R. A. Feely and C. E. Cosca",
                "(1997). Measurement of underway fCO2 in the Eastern",
                "Equatorial Pacific on NOAA ships BALDRIGE and DISCOVERER,",
                "NOAA data report ERL AOML-30, 52 pp., NTIS Springfield.",
                "Pierrot, D., C. Neill, K. Sullivan, R. Castle, R. Wanninkhof, H.",
                "Luger, T. Johannessen, A. Olsen, R. A. Feely, and C. E.",
                "Cosca (2009), Recommendations for autonomous underway pCO2",
                "measuring systems and data-reduction routines.  Deep Sea",
                "Research II, 56: 512-522.",
                "Wanninkhof, R. and K. Thoning (1993) Measurement of fugacity of CO2 in",
                "surface water using continuous and discrete sampling methods.",
                "Mar. Chem. 44(2-4): 189-205.",
                "Weiss, R. F. (1970) The solubility of nitrogen, oxygen and argon in",
                "water and seawater. Deep-Sea Research 17: 721-735.",
                "Weiss, R. F. (1974) Carbon dioxide in water and seawater: the",
                "solubility of a non-ideal gas.  Mar. Chem. 2: 203-215.",
                "Weiss, R. F., R. A. Jahnke and C. D. Keeling (1982) Seasonal effects",
                "of temperature and salinity on the partial pressure of CO2 in",
                "seawater. Nature 300: 511-513."
        )), info.getReferences());
        assertEquals(info.toString(), new ArrayList<String>(Arrays.asList("Honolulu, HI", "San Francisco, CA")),
                info.getPortsOfCall());
        assertEquals(info.toString(), new ArrayList<String>(Arrays.asList(
                "Experiment Type: Research Cruise",
                "Cruise Info: CALWATER II Leg 1",
                "Website Note: All AOML fCO2 underway data from the R/V Ronald H. Brown are posted on this site.",
                "(1.) It was determined that there was a 2.68 minute offset between the SST data record from the " +
                        "SBE-21 in the bow and the Hart 1521 temperature sensor in the equilibrator.  The SST data " +
                        "were interpolated using this offset to determine the SST at the time of the equilibrator " +
                        "measurement.  (2.) A total of 6011 measurements were taken with 5661 flagged as good, 342 " +
                        "flagged as questionable, and 8 flagged as bad.  All measurements flagged as 4 (bad) have " +
                        "been removed from the final data file.  (3.) There was a 17-1/2 hour dropout of EqT readings at",
                "the start of the cruise.  New values were determined using a relation between equilibrator " +
                        "temperature and SST.  The equation used was EqT = 0.9734*SST + 0.7735, n = 124, " +
                        "r^2 = 0.9630.  All of these values have been flagged 3.  (4.) On 1/22 at 1730, an emergency " +
                        "shutdown of the system",
                "occurred due to water getting into the atm condenser. The survey tech cleared out the water and " +
                        "restarted the system on 1/26 at 0519.  No data was acquired during the shutdown period."
        )), info.getAddnInfo());
        assertEquals(info.toString(), new Datestamp("2015", "01", "15"), info.getStartDatestamp());
        assertEquals(info.toString(), new Datestamp("2015", "01", "29"), info.getEndDatestamp());
        assertEquals(info.toString(), new ArrayList<Datestamp>(Arrays.asList(
                new Datestamp("2016", "01", "20")
        )), info.getHistory());

        Platform platform = mdata.getPlatform();
        assertEquals(platform.toString(), "33RO", platform.getPlatformId());
        assertEquals(platform.toString(), "Ronald H. Brown", platform.getPlatformName());
        assertEquals(platform.toString(), PlatformType.SHIP, platform.getPlatformType());
        assertEquals(platform.toString(), "NOAA", platform.getPlatformOwner());
        assertEquals(platform.toString(), "", platform.getPlatformCountry());

        Coverage coverage = mdata.getCoverage();
        assertEquals(coverage.toString(), new NumericString("-158.0", Coverage.LONGITUDE_UNITS),
                coverage.getWesternLongitude());
        assertEquals(coverage.toString(), new NumericString("-122.6", Coverage.LONGITUDE_UNITS),
                coverage.getEasternLongitude());
        assertEquals(coverage.toString(), new NumericString("-21.2", Coverage.LATITUDE_UNITS),
                coverage.getSouthernLatitude());
        assertEquals(coverage.toString(), new NumericString("38.0", Coverage.LATITUDE_UNITS),
                coverage.getNorthernLatitude());
        Datestamp stamp = new Datestamp("2015", "01", "15");
        Date time = stamp.getEarliestTime();
        assertEquals(coverage.toString(), time, coverage.getEarliestDataTime());
        stamp = new Datestamp("2015", "01", "29");
        time = stamp.getEarliestTime();
        assertEquals(coverage.toString(), new Date(time.getTime() + 24L * 60L * 60L * 1000L - 1000L),
                coverage.getLatestDataTime());
        assertEquals(coverage.toString(), Coverage.WGS84, coverage.getSpatialReference());
        assertEquals(coverage.toString(), 0, coverage.getGeographicNames().size());

        ArrayList<Instrument> instruments = mdata.getInstruments();
        assertEquals(instruments.toString(), 8, instruments.size());

        {
            Instrument inst = instruments.get(0);
            assertEquals(inst.toString(), "Equilibrator", inst.getName());
            assertEquals(inst.toString(), "", inst.getId());
            assertEquals(inst.toString(), "", inst.getManufacturer());
            assertEquals(inst.toString(), "", inst.getModel());
            strList = inst.getAddnInfo();
            assertEquals(inst.toString(), 1, strList.size());
            assertEquals(inst.toString(), "Primary equlibrator is vented through a secondary equilibrator",
                    strList.get(0));

            assertTrue(inst instanceof Sampler);
            Sampler sampler = (Sampler) inst;
            assertEquals(new HashSet<String>(Arrays.asList(
                    "Equilibrator Pressure Sensor",
                    "Equilibrator Temperature Sensor"
            )), sampler.getInstrumentNames());

            assertTrue(inst instanceof Equilibrator);
            Equilibrator equilibrator = (Equilibrator) inst;
            assertEquals(equilibrator.toString(), "Sprayhead above dynamic pool, with thermal jacket",
                    equilibrator.getEquilibratorType());
            assertEquals(equilibrator.toString(), "0.95 L", equilibrator.getChamberVol());
            assertEquals(equilibrator.toString(), "0.4 L", equilibrator.getChamberWaterVol());
            assertEquals(equilibrator.toString(), "0.55 L", equilibrator.getChamberGasVol());
            assertEquals(equilibrator.toString(), "1.5 - 2.0 L/min", equilibrator.getWaterFlowRate());
            assertEquals(equilibrator.toString(), "70 - 150 ml/min", equilibrator.getGasFlowRate());
            assertEquals(equilibrator.toString(), "Yes", equilibrator.getVenting());
        }

        {
            Instrument inst = instruments.get(1);
            assertEquals(inst.toString(), "CO2 Sensor", inst.getName());
            assertEquals(inst.toString(), "", inst.getId());
            assertEquals(inst.toString(), "LI-COR", inst.getManufacturer());
            assertEquals(inst.toString(), "LI-6262", inst.getModel());
            strList = inst.getAddnInfo();
            assertEquals(inst.toString(), 2, strList.size());
            assertEquals(inst.toString(), "Number of non-zero gases: 4", strList.get(0));
            assertEquals(inst.toString(), "The instrument is located in an air-conditioned laboratory.  " +
                    "99.9% Nitrogen gas and the high standard (Std 4) are used to set the zero and span " +
                    "of the LI-COR analyzer.", strList.get(1));

            assertTrue(inst instanceof Analyzer);
            Analyzer sensor = (Analyzer) inst;
            assertEquals(inst.toString(), "The analyzer is calibrated every 3.25 hours with standards from ESRL " +
                            "in Boulder, CO that are directly traceable to the WMO scale.  The zero gas is 99.9% nitrogen.",
                    sensor.getCalibration());

            assertTrue(sensor instanceof GasSensor);
            GasSensor gasSensor = (GasSensor) sensor;
            ArrayList<CalibrationGas> gasList = gasSensor.getCalibrationGases();
            assertEquals(gasSensor.toString(), 2, gasList.size());
            CalibrationGas gas = gasList.get(0);
            assertEquals(gas.toString(), "ESRL in Boulder, CO.", gas.getId());
            assertEquals(gas.toString(), "CO2", gas.getType());
            assertEquals(gas.toString(), "", gas.getSupplier());
            assertEquals(gas.toString(), new NumericString(null, CalibrationGas.GAS_CONCENTRATION_UNIT),
                    gas.getConcentration());
            assertEquals(gas.toString(), new NumericString(null, CalibrationGas.GAS_CONCENTRATION_UNIT),
                    gas.getAccuracy());
            gas = gasList.get(1);
            assertEquals(gas.toString(), "Std 1: CA04957, 282.55 ppm; Std 2: CC105863, 380.22 ppm; " +
                            "Std 3: CB09696, 453.04 ppm; Std 4: CB09032, 539.38 ppm",
                    gas.getId());
            assertEquals(gas.toString(), "CO2", gas.getType());
            assertEquals(gas.toString(), "", gas.getSupplier());
            assertEquals(gas.toString(), new NumericString(null, CalibrationGas.GAS_CONCENTRATION_UNIT),
                    gas.getConcentration());
            assertEquals(gas.toString(), new NumericString(null, CalibrationGas.GAS_CONCENTRATION_UNIT),
                    gas.getAccuracy());
        }

        {
            Instrument inst = instruments.get(2);
            assertEquals(inst.toString(), "Water Temperature Sensor", inst.getName());
            assertEquals(inst.toString(), "", inst.getId());
            assertEquals(inst.toString(), "Seabird", inst.getManufacturer());
            assertEquals(inst.toString(), "SBE-21", inst.getModel());
            strList = inst.getAddnInfo();
            assertEquals(inst.toString(), 1, strList.size());
            assertEquals(inst.toString(), "Manufacturer's resolution is taken as precision. Maintained by ship.",
                    strList.get(0));

            assertTrue(inst instanceof Analyzer);
            Analyzer sensor = (Analyzer) inst;
            assertEquals(sensor.toString(), "Factory calibration", sensor.getCalibration());

            assertTrue(sensor instanceof TemperatureSensor);
        }

        {
            Instrument inst = instruments.get(3);
            assertEquals(inst.toString(), "Equilibrator Temperature Sensor", inst.getName());
            assertEquals(inst.toString(), "", inst.getId());
            assertEquals(inst.toString(), "Hart", inst.getManufacturer());
            assertEquals(inst.toString(), "1521", inst.getModel());
            strList = inst.getAddnInfo();
            assertEquals(inst.toString(), 1, strList.size());
            assertEquals(inst.toString(), "Warming: 0.1 - 0.6 °C", strList.get(0));

            assertTrue(inst instanceof Analyzer);
            Analyzer sensor = (Analyzer) inst;
            assertEquals(sensor.toString(), "Factory calibration", sensor.getCalibration());

            assertTrue(sensor instanceof TemperatureSensor);
        }

        {
            Instrument inst = instruments.get(4);
            assertEquals(inst.toString(), "Atmospheric Pressure Sensor", inst.getName());
            assertEquals(inst.toString(), "", inst.getId());
            assertEquals(inst.toString(), "Vaisala", inst.getManufacturer());
            assertEquals(inst.toString(), "PTB330", inst.getModel());
            strList = inst.getAddnInfo();
            assertEquals(inst.toString(), 1, strList.size());
            assertEquals(inst.toString(), "Manufacturer's resolution is taken as precision. Maintained by ship.",
                    strList.get(0));

            assertTrue(inst instanceof Analyzer);
            Analyzer sensor = (Analyzer) inst;
            assertEquals(sensor.toString(), "Factory calibration", sensor.getCalibration());

            assertTrue(sensor instanceof PressureSensor);
        }

        {
            Instrument inst = instruments.get(5);
            assertEquals(inst.toString(), "Equilibrator Pressure Sensor", inst.getName());
            assertEquals(inst.toString(), "", inst.getId());
            assertEquals(inst.toString(), "Setra", inst.getManufacturer());
            assertEquals(inst.toString(), "270", inst.getModel());
            strList = inst.getAddnInfo();
            assertEquals(inst.toString(), 1, strList.size());
            assertEquals(inst.toString(), "Pressure reading from the Setra-270 on the exit of the analyzer was added " +
                    "to the differential pressure reading from Setra-239 attached to the equilibrator headspace " +
                    "to yield the equlibrator pressure.", strList.get(0));

            assertTrue(inst instanceof Analyzer);
            Analyzer sensor = (Analyzer) inst;
            assertEquals(sensor.toString(), "Factory calibration.", sensor.getCalibration());

            assertTrue(sensor instanceof PressureSensor);
        }

        {
            Instrument inst = instruments.get(6);
            assertEquals(inst.toString(), "Salinity Sensor", inst.getName());
            assertEquals(inst.toString(), "", inst.getId());
            assertEquals(inst.toString(), "Seabird", inst.getManufacturer());
            assertEquals(inst.toString(), "SBE-45", inst.getModel());
            strList = inst.getAddnInfo();
            assertEquals(inst.toString(), 1, strList.size());
            assertEquals(inst.toString(), "Manufacturer's resolution is taken as precision.", strList.get(0));

            assertTrue(inst instanceof Analyzer);
            Analyzer sensor = (Analyzer) inst;
            assertEquals(sensor.toString(), "Factory calibration.", sensor.getCalibration());

            assertTrue(sensor instanceof SalinitySensor);
        }

        {
            Instrument inst = instruments.get(7);
            assertEquals(inst.toString(), "Other Sensor 1", inst.getName());
            assertEquals(inst.toString(), "", inst.getId());
            assertEquals(inst.toString(), "Setra", inst.getManufacturer());
            assertEquals(inst.toString(), "239", inst.getModel());
            strList = inst.getAddnInfo();
            assertEquals(inst.toString(), 4, strList.size());
            assertEquals(inst.toString(), "Location: Attached to equilibrator headspace", strList.get(0));
            assertEquals(inst.toString(), "Accuracy/Uncertainty: ± 0.052 hPa", strList.get(1));
            assertEquals(inst.toString(), "Precision/Resolution: 0.01 hPa", strList.get(2));
            assertEquals(inst.toString(), "Pressure reading from the Setra-270 on the exit of the analyzer was added " +
                    "to the differential pressure reading from Setra-239 attached to the equilibrator headspace " +
                    "to yield the equlibrator pressure.", strList.get(3));

            assertTrue(inst instanceof Analyzer);
            Analyzer sensor = (Analyzer) inst;
            assertEquals(sensor.toString(), "Factory calibration", sensor.getCalibration());
        }

        ArrayList<Variable> variables = mdata.getVariables();

        assertEquals(variables.toString(), 13, variables.size());

        {
            Variable var = variables.get(0);
            assertEquals(var.toString(), "xCO2_EQU_ppm", var.getColName());
            assertEquals(var.toString(), "Mole fraction of CO2 in the equilibrator headspace (dry) " +
                    "at equilibrator temperature (ppm)", var.getFullName());
            assertEquals(var.toString(), "", var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "WOCE_QC_FLAG", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("1", "microatmospheres"), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("0.01", "microatmosphere"), var.getPrecision());
            strList = var.getAddnInfo();
            assertEquals(var.toString(), 1, strList.size());
            assertEquals(var.toString(), "Frequency: Every 150 seconds", strList.get(0));

            assertTrue(var instanceof DataVar);
            DataVar dataVar = (DataVar) var;
            assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
            assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
            assertEquals(dataVar.toString(), "Infrared absorption of dry sample gas.", dataVar.getMethodDescription());
            assertEquals(dataVar.toString(),
                    "Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                            "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                            "Recommendations for autonomous underway pCO2 measuring systems \n" +
                            "and data reduction routines, Deep-Sea Res II, 56, 512-522.", dataVar.getMethodReference());
            assertEquals(dataVar.toString(), "Bow", dataVar.getSamplingLocation());
            assertEquals(dataVar.toString(), "Sampling Depth: 5 meters", dataVar.getSamplingElevation());
            assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
            assertEquals(dataVar.toString(), "", dataVar.getReplication());
            HashSet<String> strSet = dataVar.getInstrumentNames();
            assertEquals(dataVar.toString(), 2, strSet.size());
            assertTrue(dataVar.toString(), strSet.contains("Equilibrator"));
            assertTrue(dataVar.toString(), strSet.contains("CO2 Sensor"));
            assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

            assertTrue(dataVar instanceof GasConc);
            GasConc gasConc = (GasConc) dataVar;
            assertEquals(gasConc.toString(), "Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                            "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                    gasConc.getDryingMethod());
            assertEquals(gasConc.toString(), "", gasConc.getWaterVaporCorrection());

            assertTrue(gasConc instanceof AquGasConc);
            AquGasConc aquGasConc = (AquGasConc) gasConc;
            assertEquals(aquGasConc.toString(), "equilibrator temperature", aquGasConc.getReportTemperature());
            assertEquals(aquGasConc.toString(), "", aquGasConc.getAnalysisTemperature());
            assertEquals(aquGasConc.toString(), "", aquGasConc.getTemperatureCorrection());
        }

        {
            Variable var = variables.get(1);
            assertEquals(var.toString(), "xCO2_ATM_ppm", var.getColName());
            assertEquals(var.toString(), "Mole fraction of CO2 measured in dry outside air (ppm)", var.getFullName());
            assertEquals(var.toString(), "", var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("0.2", "ppm"), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("0.01", "ppm"), var.getPrecision());
            strList = var.getAddnInfo();
            assertEquals(var.toString(), 1, strList.size());
            assertEquals(var.toString(), "Measurement: Yes, 5 readings in a group every 3.25 hours.", strList.get(0));

            assertTrue(var instanceof DataVar);
            DataVar dataVar = (DataVar) var;
            assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
            assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
            assertEquals(dataVar.toString(), "Infrared absorption of dry sample gas.", dataVar.getMethodDescription());
            assertEquals(dataVar.toString(),
                    "Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                            "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                            "Recommendations for autonomous underway pCO2 measuring systems \n" +
                            "and data reduction routines, Deep-Sea Res II, 56, 512-522.", dataVar.getMethodReference());
            assertEquals(dataVar.toString(), "Bow tower ~10 m above the sea surface.", dataVar.getSamplingLocation());
            assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
            assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
            assertEquals(dataVar.toString(), "", dataVar.getReplication());
            HashSet<String> strSet = dataVar.getInstrumentNames();
            assertEquals(dataVar.toString(), 1, strSet.size());
            assertTrue(dataVar.toString(), strSet.contains("CO2 Sensor"));
            assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

            assertTrue(dataVar instanceof GasConc);
            GasConc gasConc = (GasConc) dataVar;
            assertEquals(gasConc.toString(), "Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                            "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                    gasConc.getDryingMethod());
            assertEquals(gasConc.toString(), "", gasConc.getWaterVaporCorrection());

            assertFalse(gasConc instanceof AquGasConc);
        }

        {
            Variable var = variables.get(2);
            assertEquals(var.toString(), "xCO2_ATM_interpolated_ppm", var.getColName());
            assertEquals(var.toString(), "Mole fraction of CO2 in outside air associated with each water analysis.  " +
                            "These values are interpolated between the bracketing averaged good xCO2_ATM analyses (ppm)",
                    var.getFullName());
            assertEquals(var.toString(), "", var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("0.2", "ppm"), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("0.01", "ppm"), var.getPrecision());
            strList = var.getAddnInfo();
            assertEquals(var.toString(), 1, strList.size());
            assertEquals(var.toString(), "Measurement: Yes, 5 readings in a group every 3.25 hours.", strList.get(0));

            assertTrue(var instanceof DataVar);
            DataVar dataVar = (DataVar) var;
            assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
            assertEquals(dataVar.toString(), MethodType.COMPUTED, dataVar.getMeasureMethod());
            assertEquals(dataVar.toString(), "Infrared absorption of dry sample gas.", dataVar.getMethodDescription());
            assertEquals(dataVar.toString(),
                    "Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                            "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                            "Recommendations for autonomous underway pCO2 measuring systems \n" +
                            "and data reduction routines, Deep-Sea Res II, 56, 512-522.", dataVar.getMethodReference());
            assertEquals(dataVar.toString(), "Bow tower ~10 m above the sea surface.", dataVar.getSamplingLocation());
            assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
            assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
            assertEquals(dataVar.toString(), "", dataVar.getReplication());
            HashSet<String> strSet = dataVar.getInstrumentNames();
            assertEquals(dataVar.toString(), 1, strSet.size());
            assertTrue(dataVar.toString(), strSet.contains("CO2 Sensor"));
            assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

            assertTrue(dataVar instanceof GasConc);
            GasConc gasConc = (GasConc) dataVar;
            assertEquals(gasConc.toString(), "Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                            "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                    gasConc.getDryingMethod());
            assertEquals(gasConc.toString(), "", gasConc.getWaterVaporCorrection());

            assertFalse(var instanceof AquGasConc);
        }

        {
            Variable var = variables.get(3);
            assertEquals(var.toString(), "PRES_EQU_hPa", var.getColName());
            assertEquals(var.toString(), "Barometric pressure in the equilibrator headspace (hectopascals)",
                    var.getFullName());
            assertEquals(var.toString(), AirPressure.HECTOPASCALS_UNIT, var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("0.05", "hPa"), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("0.015", "hPa"), var.getPrecision());
            assertEquals(var.toString(), 0, var.getAddnInfo().size());

            assertTrue(var instanceof DataVar);
            DataVar dataVar = (DataVar) var;
            assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
            assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
            assertEquals(dataVar.toString(), "", dataVar.getMethodDescription());
            assertEquals(dataVar.toString(), "", dataVar.getMethodReference());
            assertEquals(dataVar.toString(), "Attached to CO2 analyzer exit to lab.", dataVar.getSamplingLocation());
            assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
            assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
            assertEquals(dataVar.toString(), "", dataVar.getReplication());
            HashSet<String> strSet = dataVar.getInstrumentNames();
            assertEquals(dataVar.toString(), 1, strSet.size());
            assertTrue(dataVar.toString(), strSet.contains("Equilibrator Pressure Sensor"));
            assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

            assertTrue(dataVar instanceof AirPressure);
            AirPressure pressure = (AirPressure) dataVar;
            assertEquals(pressure.toString(), "", pressure.getPressureCorrection());
        }

        {
            Variable var = variables.get(4);
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
            DataVar dataVar = (DataVar) var;
            assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
            assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
            assertEquals(dataVar.toString(), "", dataVar.getMethodDescription());
            assertEquals(dataVar.toString(), "", dataVar.getMethodReference());
            assertEquals(dataVar.toString(),
                    "On bulkhead exterior on the port side of the radio room aft of the bridge " +
                            "at ~14 m above the sea surface.", dataVar.getSamplingLocation());
            assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
            assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
            assertEquals(dataVar.toString(), "", dataVar.getReplication());
            HashSet<String> strSet = dataVar.getInstrumentNames();
            assertEquals(dataVar.toString(), 1, strSet.size());
            assertTrue(dataVar.toString(), strSet.contains("Atmospheric Pressure Sensor"));
            assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

            assertTrue(dataVar instanceof AirPressure);
            AirPressure pressure = (AirPressure) dataVar;
            assertEquals(pressure.toString(), "Normalized: yes", pressure.getPressureCorrection());
        }

        {
            Variable var = variables.get(5);
            assertEquals(var.toString(), "TEMP_EQU_C", var.getColName());
            assertEquals(var.toString(), "Water temperature in equilibrator (degrees Celsius)", var.getFullName());
            assertEquals(var.toString(), Temperature.DEGREES_CELSIUS_UNIT, var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("0.025", "°C"), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("0.01", "°C"), var.getPrecision());
            assertEquals(var.toString(), 0, var.getAddnInfo().size());

            assertTrue(var instanceof DataVar);
            DataVar dataVar = (DataVar) var;
            assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
            assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
            assertEquals(dataVar.toString(), "", dataVar.getMethodDescription());
            assertEquals(dataVar.toString(), "", dataVar.getMethodReference());
            assertEquals(dataVar.toString(), "In Hydro Lab, inserted into equilibrator ~ 5 cm below water line.",
                    dataVar.getSamplingLocation());
            assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
            assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
            assertEquals(dataVar.toString(), "", dataVar.getReplication());
            HashSet<String> strSet = dataVar.getInstrumentNames();
            assertEquals(dataVar.toString(), 1, strSet.size());
            assertTrue(dataVar.toString(), strSet.contains("Equilibrator Temperature Sensor"));
            assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

            assertTrue(dataVar instanceof Temperature);
        }

        {
            Variable var = variables.get(6);
            assertEquals(var.toString(), "SST_C", var.getColName());
            assertEquals(var.toString(), "Sea surface temperature (degrees Celsius)", var.getFullName());
            assertEquals(var.toString(), Temperature.DEGREES_CELSIUS_UNIT, var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("0.01", "°C"), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("0.001", "°C"), var.getPrecision());
            assertEquals(var.toString(), 0, var.getAddnInfo().size());

            assertTrue(var instanceof DataVar);
            DataVar dataVar = (DataVar) var;
            assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
            assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
            assertEquals(dataVar.toString(), "", dataVar.getMethodDescription());
            assertEquals(dataVar.toString(), "", dataVar.getMethodReference());
            assertEquals(dataVar.toString(), "Bow thruster room, before sea water pump, ~5 m below water line.",
                    dataVar.getSamplingLocation());
            assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
            assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
            assertEquals(dataVar.toString(), "", dataVar.getReplication());
            HashSet<String> strSet = dataVar.getInstrumentNames();
            assertEquals(dataVar.toString(), 1, strSet.size());
            assertTrue(dataVar.toString(), strSet.contains("Water Temperature Sensor"));
            assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

            assertTrue(dataVar instanceof Temperature);
        }

        {
            Variable var = variables.get(7);
            assertEquals(var.toString(), "SAL_permil", var.getColName());
            assertEquals(var.toString(), "Sea surface salinity on Practical Salinity Scale (permil)",
                    var.getFullName());
            assertEquals(var.toString(), "", var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("0.005 ", "permil"), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("0.0002", "permil"), var.getPrecision());
            assertEquals(var.toString(), 0, var.getAddnInfo().size());

            assertTrue(var instanceof DataVar);
            DataVar dataVar = (DataVar) var;
            assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
            assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
            assertEquals(dataVar.toString(), "", dataVar.getMethodDescription());
            assertEquals(dataVar.toString(), "", dataVar.getMethodReference());
            assertEquals(dataVar.toString(), "Attached to underway system at sea water input.",
                    dataVar.getSamplingLocation());
            assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
            assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
            assertEquals(dataVar.toString(), "", dataVar.getReplication());
            HashSet<String> strSet = dataVar.getInstrumentNames();
            assertEquals(dataVar.toString(), 1, strSet.size());
            assertTrue(dataVar.toString(), strSet.contains("Salinity Sensor"));
            assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

            assertFalse(dataVar instanceof Temperature);
            assertFalse(dataVar instanceof AirPressure);
        }

        {
            Variable var = variables.get(8);
            assertEquals(var.toString(), "fCO2_SW@SST_uatm", var.getColName());
            assertEquals(var.toString(), "Fugacity of CO2 in sea water at SST and 100% humidity (microatmospheres)",
                    var.getFullName());
            assertEquals(var.toString(), "", var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "WOCE_QC_FLAG", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("1", "microatmospheres"), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("0.01", "microatmosphere"), var.getPrecision());
            strList = var.getAddnInfo();
            assertEquals(var.toString(), 1, strList.size());
            assertEquals(var.toString(), "Frequency: Every 150 seconds", strList.get(0));

            assertTrue(var instanceof DataVar);
            DataVar dataVar = (DataVar) var;
            assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
            assertEquals(dataVar.toString(), MethodType.MEASURED_INSITU, dataVar.getMeasureMethod());
            assertEquals(dataVar.toString(), "Infrared absorption of dry sample gas.", dataVar.getMethodDescription());
            assertEquals(dataVar.toString(),
                    "Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                            "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                            "Recommendations for autonomous underway pCO2 measuring systems \n" +
                            "and data reduction routines, Deep-Sea Res II, 56, 512-522.", dataVar.getMethodReference());
            assertEquals(dataVar.toString(), "Bow", dataVar.getSamplingLocation());
            assertEquals(dataVar.toString(), "Sampling Depth: 5 meters", dataVar.getSamplingElevation());
            assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
            assertEquals(dataVar.toString(), "", dataVar.getReplication());
            HashSet<String> strSet = dataVar.getInstrumentNames();
            assertEquals(dataVar.toString(), 2, strSet.size());
            assertTrue(dataVar.toString(), strSet.contains("Equilibrator"));
            assertTrue(dataVar.toString(), strSet.contains("CO2 Sensor"));
            assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

            assertTrue(dataVar instanceof GasConc);
            GasConc gasConc = (GasConc) dataVar;
            assertEquals(gasConc.toString(), "Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                            "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                    gasConc.getDryingMethod());
            assertEquals(gasConc.toString(), "", gasConc.getWaterVaporCorrection());

            assertTrue(gasConc instanceof AquGasConc);
            AquGasConc aquGasConc = (AquGasConc) gasConc;
            assertEquals(aquGasConc.toString(), "SST", aquGasConc.getReportTemperature());
            assertEquals(aquGasConc.toString(), "", aquGasConc.getAnalysisTemperature());
            assertEquals(aquGasConc.toString(), "", aquGasConc.getTemperatureCorrection());
        }

        {
            Variable var = variables.get(9);
            assertEquals(var.toString(), "fCO2_ATM_interpolated_uatm", var.getColName());
            assertEquals(var.toString(), "Fugacity of CO2 in air corresponding to the interpolated xCO2 " +
                    "at SST and 100% humidity (microatmospheres)", var.getFullName());
            assertEquals(var.toString(), "", var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("0.2", "ppm"), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("0.01", "ppm"), var.getPrecision());
            strList = var.getAddnInfo();
            assertEquals(var.toString(), 1, strList.size());
            assertEquals(var.toString(), "Measurement: Yes, 5 readings in a group every 3.25 hours.", strList.get(0));

            assertTrue(var instanceof DataVar);
            DataVar dataVar = (DataVar) var;
            assertEquals(dataVar.toString(), "Surface Underway", dataVar.getObserveType());
            assertEquals(dataVar.toString(), MethodType.COMPUTED, dataVar.getMeasureMethod());
            assertEquals(dataVar.toString(), "Infrared absorption of dry sample gas.", dataVar.getMethodDescription());
            assertEquals(dataVar.toString(),
                    "Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, \n" +
                            "T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), \n" +
                            "Recommendations for autonomous underway pCO2 measuring systems \n" +
                            "and data reduction routines, Deep-Sea Res II, 56, 512-522.", dataVar.getMethodReference());
            assertEquals(dataVar.toString(), "Bow tower ~10 m above the sea surface.", dataVar.getSamplingLocation());
            assertEquals(dataVar.toString(), "", dataVar.getSamplingElevation());
            assertEquals(dataVar.toString(), "", dataVar.getStorageMethod());
            assertEquals(dataVar.toString(), "", dataVar.getReplication());
            HashSet<String> strSet = dataVar.getInstrumentNames();
            assertEquals(dataVar.toString(), 1, strSet.size());
            assertTrue(dataVar.toString(), strSet.contains("CO2 Sensor"));
            assertEquals(dataVar.toString(), new Person(), dataVar.getResearcher());

            assertTrue(dataVar instanceof GasConc);
            GasConc gasConc = (GasConc) dataVar;
            assertEquals(gasConc.toString(), "Gas stream passes through a thermoelectric condenser (~5 °C) and then " +
                            "through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).",
                    gasConc.getDryingMethod());
            assertEquals(gasConc.toString(), "", gasConc.getWaterVaporCorrection());

            assertFalse(gasConc instanceof AquGasConc);
        }


        {
            Variable var = variables.get(10);
            assertEquals(var.toString(), "dfCO2_uatm", var.getColName());
            assertEquals(var.toString(), "Sea water fCO2 minus interpolated air fCO2 (microatmospheres)",
                    var.getFullName());
            assertEquals(var.toString(), "", var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("", ""), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("", ""), var.getPrecision());
            assertEquals(var.toString(), 0, var.getAddnInfo().size());

            assertFalse(var instanceof DataVar);
        }

        {
            Variable var = variables.get(11);
            assertEquals(var.toString(), "WOCE_QC_FLAG", var.getColName());
            assertEquals(var.toString(), "Quality control flag for fCO2 values (2=good, 3=questionable)",
                    var.getFullName());
            assertEquals(var.toString(), "", var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("", ""), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("", ""), var.getPrecision());
            assertEquals(var.toString(), 0, var.getAddnInfo().size());

            assertFalse(var instanceof DataVar);
        }

        {
            Variable var = variables.get(12);
            assertEquals(var.toString(), "QC_SUBFLAG", var.getColName());
            assertEquals(var.toString(), "Quality control subflag for fCO2 values, provides explanation when QC flag=3",
                    var.getFullName());
            assertEquals(var.toString(), "", var.getVarUnit());
            assertEquals(var.toString(), "", var.getMissVal());
            assertEquals(var.toString(), "", var.getFlagColName());
            assertEquals(var.toString(), new NumericString("", ""), var.getAccuracy());
            assertEquals(var.toString(), new NumericString("", ""), var.getPrecision());
            assertEquals(var.toString(), 0, var.getAddnInfo().size());

            assertFalse(var instanceof DataVar);
        }

    }

    static final String AOML_CDIAC_XML_DATA_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
