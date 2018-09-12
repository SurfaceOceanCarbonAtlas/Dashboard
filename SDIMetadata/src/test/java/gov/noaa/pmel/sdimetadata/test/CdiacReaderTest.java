package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.Coverage;
import gov.noaa.pmel.sdimetadata.Platform;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import gov.noaa.pmel.sdimetadata.instrument.Analyzer;
import gov.noaa.pmel.sdimetadata.instrument.CalibrationGas;
import gov.noaa.pmel.sdimetadata.instrument.Equilibrator;
import gov.noaa.pmel.sdimetadata.instrument.GasSensor;
import gov.noaa.pmel.sdimetadata.instrument.PressureSensor;
import gov.noaa.pmel.sdimetadata.instrument.SalinitySensor;
import gov.noaa.pmel.sdimetadata.instrument.Sampler;
import gov.noaa.pmel.sdimetadata.instrument.TemperatureSensor;
import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
import gov.noaa.pmel.sdimetadata.util.NumericString;
import gov.noaa.pmel.sdimetadata.xml.CdiacReader;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CdiacReaderTest {

    @Test
    public void testCreateSDIMetadata() {
        CdiacReader reader = new CdiacReader(new StringReader(AOML_CDIAC_XML_DATA_STRING));
        SDIMetadata mdata = reader.createSDIMetadata();

        Submitter submitter = mdata.getSubmitter();
        assertEquals("Castle", submitter.getLastName());
        assertEquals("Robert", submitter.getFirstName());
        assertEquals("", submitter.getMiddle());
        assertEquals("", submitter.getId());
        assertEquals("", submitter.getIdType());
        assertEquals("NOAA/Atlantic Oceanographic & Meteorological Laboratory", submitter.getOrganization());
        ArrayList<String> strList = submitter.getStreets();
        assertEquals(1, strList.size());
        assertEquals("4301 Rickenbacker Causeway; Miami, FL 33149", submitter.getStreets().get(0));
        assertEquals("", submitter.getCity());
        assertEquals("", submitter.getRegion());
        assertEquals("", submitter.getZipCode());
        assertEquals("", submitter.getCountry());
        assertEquals("305-361-4418", submitter.getPhone());
        assertEquals("Robert.Castle@noaa.gov", submitter.getEmail());

        ArrayList<Investigator> invList = mdata.getInvestigators();
        assertEquals(1, invList.size());
        Investigator investigator = invList.get(0);
        assertEquals("Wanninkhof", investigator.getLastName());
        assertEquals("Rik", investigator.getFirstName());
        assertEquals("", investigator.getMiddle());
        assertEquals("", investigator.getId());
        assertEquals("", investigator.getIdType());
        assertEquals("NOAA/AOML", investigator.getOrganization());
        strList = investigator.getStreets();
        assertEquals(1, strList.size());
        assertEquals("4301 Rickenbacker Causeway; Miami Fl, 33149", investigator.getStreets().get(0));
        assertEquals("", investigator.getCity());
        assertEquals("", investigator.getRegion());
        assertEquals("", investigator.getZipCode());
        assertEquals("", investigator.getCountry());
        assertEquals("305-361-4379", investigator.getPhone());
        assertEquals("Rik.Wanninkhof@noaa.gov", investigator.getEmail());

        Platform platform = mdata.getPlatform();
        assertEquals("33RO", platform.getPlatformId());
        assertEquals("Ronald H. Brown", platform.getPlatformName());
        assertEquals("Ship", platform.getPlatformType());
        assertEquals("NOAA", platform.getPlatformOwner());
        assertEquals("", platform.getPlatformCountry());

        Coverage coverage = mdata.getCoverage();
        assertEquals(new NumericString("-158.0", Coverage.LONGITUDE_UNITS), coverage.getWesternLongitude());
        assertEquals(new NumericString("-122.6", Coverage.LONGITUDE_UNITS), coverage.getEasternLongitude());
        assertEquals(new NumericString("-21.2", Coverage.LATITUDE_UNITS), coverage.getSouthernLatitude());
        assertEquals(new NumericString("38.0", Coverage.LATITUDE_UNITS), coverage.getNorthernLatitude());
        Datestamp stamp = new Datestamp("2015", "01", "15");
        Date time = stamp.getEarliestTime();
        assertEquals(time, coverage.getEarliestDataTime());
        stamp = new Datestamp("2015", "01", "29");
        time = stamp.getEarliestTime();
        assertEquals(new Date(time.getTime() + 24L * 60L * 60L * 1000L - 1000L), coverage.getLatestDataTime());
        assertEquals(Coverage.WGS84, coverage.getSpatialReference());
        assertEquals(0, coverage.getGeographicNames().size());

        ArrayList<Sampler> samplers = mdata.getSamplers();
        assertEquals(1, samplers.size());
        Sampler inst = samplers.get(0);
        assertEquals("Equilibrator", inst.getName());
        assertEquals("", inst.getId());
        assertEquals("", inst.getManufacturer());
        assertEquals("", inst.getModel());
        strList = inst.getAddnInfo();
        assertEquals(1, strList.size());
        assertEquals("Primary equlibrator is vented through a secondary equilibrator", strList.get(0));

        assertTrue(inst instanceof Equilibrator);
        Equilibrator equilibrator = (Equilibrator) inst;
        assertEquals("Sprayhead above dynamic pool, with thermal jacket", equilibrator.getEquilibratorType());
        assertEquals("0.95 L (0.4 L water, 0.55 L headspace)", equilibrator.getChamberVol());
        assertEquals("", equilibrator.getChamberWaterVol());
        assertEquals("", equilibrator.getChamberGasVol());
        assertEquals("1.5 - 2.0 L/min", equilibrator.getWaterFlowRate());
        assertEquals("70 - 150 ml/min", equilibrator.getGasFlowRate());
        assertEquals("Yes", equilibrator.getVenting());

        ArrayList<Analyzer> analyzers = mdata.getAnalyzers();
        assertEquals(7, analyzers.size());

        Analyzer sensor = analyzers.get(0);
        assertEquals("CO2 Sensor", sensor.getName());
        assertEquals("", sensor.getId());
        assertEquals("LI-COR", sensor.getManufacturer());
        assertEquals("LI-6262", sensor.getModel());
        assertEquals("The analyzer is calibrated every 3.25 hours with standards from ESRL in Boulder, CO that " +
                "are directly traceable to the WMO scale.  The zero gas is 99.9% nitrogen.", sensor.getCalibration());
        strList = sensor.getAddnInfo();
        assertEquals(1, strList.size());
        assertEquals("The instrument is located in an air-conditioned laboratory.  99.9% Nitrogen gas and the " +
                "high standard (Std 4) are used to set the zero and span of the LI-COR analyzer.", strList.get(0));

        assertTrue(sensor instanceof GasSensor);
        GasSensor gasSensor = (GasSensor) sensor;
        assertEquals("", gasSensor.getCalibrationFrequency());
        ArrayList<CalibrationGas> gasList = gasSensor.getCalibrationGases();
        assertEquals(4, gasList.size());
        for (int k = 0; k < 4; k++) {
            CalibrationGas gas = gasList.get(k);
            assertEquals("Calibration gas " + Integer.toString(k + 1) + " mentioned in: " +
                    "ESRL in Boulder, CO.\r  " +
                    "Std 1: CA04957, 282.55 ppm; Std 2: CC105863, 380.22 ppm; " +
                    "Std 3: CB09696, 453.04 ppm; Std 4: CB09032, 539.38 ppm", gas.getId());
            assertEquals("CO2", gas.getType());
            assertEquals("", gas.getSupplier());
            assertEquals(new NumericString(null, CalibrationGas.GAS_CONCENTRATION_UNIT), gas.getConcentration());
            assertEquals(new NumericString(null, CalibrationGas.GAS_CONCENTRATION_UNIT), gas.getAccuracy());
        }

        sensor = analyzers.get(1);
        assertEquals("SST Sensor", sensor.getName());
        assertEquals("", sensor.getId());
        assertEquals("Seabird", sensor.getManufacturer());
        assertEquals("SBE-21", sensor.getModel());
        assertEquals("Factory calibration", sensor.getCalibration());
        strList = sensor.getAddnInfo();
        assertEquals(1, strList.size());
        assertEquals("Manufacturer's resolution is taken as precision. Maintained by ship.", strList.get(0));
        assertTrue(sensor instanceof TemperatureSensor);

        sensor = analyzers.get(2);
        assertEquals("T_equi Sensor", sensor.getName());
        assertEquals("", sensor.getId());
        assertEquals("Hart", sensor.getManufacturer());
        assertEquals("1521", sensor.getModel());
        assertEquals("Factory calibration", sensor.getCalibration());
        strList = sensor.getAddnInfo();
        assertEquals(1, strList.size());
        assertEquals("Warming: 0.1 - 0.6 °C", strList.get(0));
        assertTrue(sensor instanceof TemperatureSensor);

        sensor = analyzers.get(3);
        assertEquals("SLP Sensor", sensor.getName());
        assertEquals("", sensor.getId());
        assertEquals("Vaisala", sensor.getManufacturer());
        assertEquals("PTB330", sensor.getModel());
        assertEquals("Factory calibration", sensor.getCalibration());
        strList = sensor.getAddnInfo();
        assertEquals(1, strList.size());
        assertEquals("Manufacturer's resolution is taken as precision. Maintained by ship.", strList.get(0));
        assertTrue(sensor instanceof PressureSensor);

        sensor = analyzers.get(4);
        assertEquals("P_equi Sensor", sensor.getName());
        assertEquals("", sensor.getId());
        assertEquals("Setra", sensor.getManufacturer());
        assertEquals("270", sensor.getModel());
        assertEquals("Factory calibration.", sensor.getCalibration());
        strList = sensor.getAddnInfo();
        assertEquals(1, strList.size());
        assertEquals("Pressure reading from the Setra-270 on the exit of the analyzer was added " +
                "to the differential pressure reading from Setra-239 attached to the equilibrator headspace " +
                "to yield the equlibrator pressure.", strList.get(0));
        assertTrue(sensor instanceof PressureSensor);

        sensor = analyzers.get(5);
        assertEquals("Salinity Sensor", sensor.getName());
        assertEquals("", sensor.getId());
        assertEquals("Seabird", sensor.getManufacturer());
        assertEquals("SBE-45", sensor.getModel());
        assertEquals("Factory calibration.", sensor.getCalibration());
        strList = sensor.getAddnInfo();
        assertEquals(1, strList.size());
        assertEquals("Manufacturer's resolution is taken as precision.", strList.get(0));
        assertTrue(sensor instanceof SalinitySensor);

        sensor = analyzers.get(6);
        assertEquals("Other Sensor 1", sensor.getName());
        assertEquals("", sensor.getId());
        assertEquals("Setra", sensor.getManufacturer());
        assertEquals("239", sensor.getModel());
        assertEquals("Factory calibration", sensor.getCalibration());
        strList = sensor.getAddnInfo();
        assertEquals(1, strList.size());
        assertEquals("Pressure reading from the Setra-270 on the exit of the analyzer was added " +
                "to the differential pressure reading from Setra-239 attached to the equilibrator headspace " +
                "to yield the equlibrator pressure.", strList.get(0));


        // TODO: Variables

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

