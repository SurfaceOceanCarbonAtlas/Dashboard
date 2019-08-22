package gov.noaa.pmel.dashboard.test.metadata;

import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.metadata.OmeUtils;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import gov.noaa.pmel.sdimetadata.SDIMetadata;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class OmeUtilsTest {

    /**
     * Test of {@link OmeUtils#createSdiMetadataFromCdiacOme(Reader, ArrayList, ArrayList)} and
     * {@link OmeUtils#suggestDatasetQCFlag(SDIMetadata, DashboardDataset)}
     */
    @Test
    public void testSuggestDatasetQCFlag() {
        DashboardDataset dset = new DashboardDataset();
        dset.setUserColNames(DATA_COLUMN_NAMES);
        dset.setDataColTypes(DATA_COLUMN_TYPES);

        StringReader reader = new StringReader(AOML_CDIAC_XML_DATA_STRING);
        SDIMetadata mdata = null;
        try {
            mdata = OmeUtils.createSdiMetadataFromCdiacOme(reader, dset.getUserColNames(), dset.getDataColTypes());
        } catch ( Exception ex ) {
            fail("Unable to create the SDIMetadata object from CDIAC OME XML: " + ex.getMessage());
        }
        assertNotNull(mdata);

        DatasetQCStatus.Status status = null;
        try {
            status = OmeUtils.suggestDatasetQCFlag(mdata, dset);
        } catch ( Exception ex ) {
            fail("Unable to make an automation-suggested dataset QC flag: " + ex.getMessage());
        }
        assertEquals(DatasetQCStatus.Status.ACCEPTED_B, status);
    }

    private static final ArrayList<String> DATA_COLUMN_NAMES = new ArrayList<String>(Arrays.asList(
            "Expocode",
            "YD_UTC",
            "DATE_UTC__ddmmyyyy",
            "TIME_UTC_hh:mm:ss",
            "LAT_dec_degree",
            "LONG_dec_degree",
            "xCO2_EQU_ppm",
            "xCO2_ATM_ppm",
            "xCO2_ATM_interpolated_ppm",
            "PRES_EQU_hPa",
            "PRES_ATM@SSP_hPa",
            "TEMP_EQU_C",
            "SST_C",
            "SAL_permil",
            "fCO2_SW@SST_uatm",
            "fCO2_ATM_interpolated_uatm",
            "dfCO2_uatm",
            "WOCE_QC_FLAG",
            "QC_SUBFLAG"
    ));

    private static final DoubleDashDataType FCO2_ATM_WET_INTERP = new DoubleDashDataType("fCO2_atm_wet_interp",
            645.0, "fCO2_atm_wet_interp", "interpolated air fCO2 wet", false,
            SocatTypes.FCO2_UNITS, "surface_partial_pressure_of_carbon_dioxide_in_air", SocatTypes.CO2_CATEGORY, null,
            "0.0", "80.0", "1200.0", "50000.0", DashboardServerUtils.USER_ONLY_ROLES);

    private static final DoubleDashDataType DELTA_FCO2 = new DoubleDashDataType("delta_fCO2",
            648.0, "delta fCO2", "water fCO2 minus atmospheric fCO2", false,
            SocatTypes.FCO2_UNITS, "delta fCO2", SocatTypes.CO2_CATEGORY, null,
            "-35000.0", "-850.0", "850.0", "35000.0", DashboardServerUtils.USER_ONLY_ROLES);

    private static final ArrayList<DataColumnType> DATA_COLUMN_TYPES = new ArrayList<DataColumnType>(Arrays.asList(
            DashboardServerUtils.DATASET_ID.duplicate(),
            DashboardServerUtils.DAY_OF_YEAR.duplicate(),
            DashboardServerUtils.DATE.duplicate(),
            DashboardServerUtils.TIME_OF_DAY.duplicate(),
            DashboardServerUtils.LATITUDE.duplicate(),
            DashboardServerUtils.LONGITUDE.duplicate(),
            SocatTypes.XCO2_WATER_TEQU_DRY.duplicate(),
            SocatTypes.XCO2_ATM_DRY_ACTUAL.duplicate(),
            SocatTypes.XCO2_ATM_DRY_INTERP.duplicate(),
            SocatTypes.PEQU.duplicate(),
            SocatTypes.PATM.duplicate(),
            SocatTypes.TEQU.duplicate(),
            SocatTypes.SST.duplicate(),
            SocatTypes.SALINITY.duplicate(),
            SocatTypes.FCO2_WATER_SST_WET.duplicate(),
            FCO2_ATM_WET_INTERP.duplicate(),
            DELTA_FCO2.duplicate(),
            SocatTypes.WOCE_CO2_WATER.duplicate(),
            SocatTypes.COMMENT_WOCE_CO2_WATER.duplicate()
    ));

    private static final String AOML_CDIAC_XML_DATA_STRING = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<x_tags>\n" +
            "  <User>\n" +
            "    <Name>Sullivan, Kevin</Name>\n" +
            "    <Organization>NOAA/AOML CIMAS</Organization>\n" +
            "    <Address>4301 Rickenbacker Causeway, Miami, Fl 33149</Address>\n" +
            "    <Phone>(305) 361-4382</Phone>\n" +
            "    <Email>kevin.sullivan@noaa.gov</Email>\n" +
            "  </User>\n" +
            "  <Investigator>\n" +
            "    <Name>Wanninkhof, Rik</Name>\n" +
            "    <Organization>NOAA/Atlantic Oceanographic &amp; Meteorological Laboratory</Organization>\n" +
            "    <Address>4301 Rickenbacker Causeway, Miami Fl, 33149</Address>\n" +
            "    <Phone>305-361-4379</Phone>\n" +
            "    <Email>Rik.Wanninkhof@noaa.gov</Email>\n" +
            "  </Investigator>\n" +
            "  <Investigator>\n" +
            "    <Name>Pierrot, Denis</Name>\n" +
            "    <Organization>NOAA/Atlantic Oceanographic &amp; Meteorological Laboratory</Organization>\n" +
            "    <Address>4301 Rickenbacker Causeway, Miami Fl, 33149</Address>\n" +
            "    <Phone>305-361-4441</Phone>\n" +
            "    <Email>Denis.Pierrot@noaa.gov</Email>\n" +
            "  </Investigator>\n" +
            "  <Dataset_Info>\n" +
            "    <Funding_Info>NOAA Climate Program Office; NOAA Ocean Acidification Program</Funding_Info>\n" +
            "    <Submission_Dates>\n" +
            "      <Initial_Submission>20181220</Initial_Submission>\n" +
            "      <Revised_Submission>20181220</Revised_Submission>\n" +
            "    </Submission_Dates>\n" +
            "  </Dataset_Info>\n" +
            "  <Cruise_Info>\n" +
            "    <Experiment>\n" +
            "      <Experiment_Name>GU1806</Experiment_Name>\n" +
            "      <Experiment_Type>Research Cruise</Experiment_Type>\n" +
            "      <Platform_Type>Ship</Platform_Type>\n" +
            "      <Co2_Instrument_type>Equilibrator-IR or CRDS or GC</Co2_Instrument_type>\n" +
            "      <Cruise>\n" +
            "        <Cruise_ID>33GG20181110</Cruise_ID>\n" +
            "        <Cruise_Info>AOML_SOOP_CO2, Bryde's Whales</Cruise_Info>\n" +
            "        <Geographical_Coverage>\n" +
            "          <Bounds>\n" +
            "            <Westernmost_Longitude>-88.6</Westernmost_Longitude>\n" +
            "            <Easternmost_Longitude>-82.9</Easternmost_Longitude>\n" +
            "            <Northernmost_Latitude>30.4</Northernmost_Latitude>\n" +
            "            <Southernmost_Latitude>27.2</Southernmost_Latitude>\n" +
            "          </Bounds>\n" +
            "        </Geographical_Coverage>\n" +
            "        <Temporal_Coverage>\n" +
            "          <Start_Date>20181110</Start_Date>\n" +
            "          <End_Date>20181204</End_Date>\n" +
            "        </Temporal_Coverage>\n" +
            "        <Ports_of_Call>Pascagoula, MS</Ports_of_Call>\n" +
            "        <Ports_of_Call>Pensacola, FL </Ports_of_Call>\n" +
            "      </Cruise>\n" +
            "    </Experiment>\n" +
            "    <Vessel>\n" +
            "      <Vessel_Name>R/V Gordon Gunter</Vessel_Name>\n" +
            "      <Vessel_ID>33GG</Vessel_ID>\n" +
            "      <Vessel_Owner>NOAA</Vessel_Owner>\n" +
            "    </Vessel>\n" +
            "  </Cruise_Info>\n" +
            "  <Variables_Info>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>xCO2_EQU_ppm</Variable_Name>\n" +
            "      <Description_of_Variable>Mole fraction of CO2 in the equilibrator headspace (dry) at equilibrator temperature (ppm)</Description_of_Variable>\n" +
            "      <Unit_of_Variable>ppm</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>xCO2_ATM_ppm</Variable_Name>\n" +
            "      <Description_of_Variable>Mole fraction of CO2 measured in dry outside air (ppm)</Description_of_Variable>\n" +
            "      <Unit_of_Variable>ppm</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>xCO2_ATM_interpolated_ppm</Variable_Name>\n" +
            "      <Description_of_Variable>Mole fraction of CO2 in outside air associated with each water analysis.  These values are interpolated between the bracketing averaged good xCO2_ATM analyses (ppm)</Description_of_Variable>\n" +
            "      <Unit_of_Variable>ppm</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>PRES_EQU_hPa</Variable_Name>\n" +
            "      <Description_of_Variable>Barometric pressure in the equilibrator headspace (hPa)</Description_of_Variable>\n" +
            "      <Unit_of_Variable>hPa</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>PRES_ATM@SSP_hPa</Variable_Name>\n" +
            "      <Description_of_Variable>Barometric pressure measured outside, corrected to sea level (hPa)</Description_of_Variable>\n" +
            "      <Unit_of_Variable> hPa </Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>TEMP_EQU_C</Variable_Name>\n" +
            "      <Description_of_Variable>Water temperature in equilibrator (&#176;C)</Description_of_Variable>\n" +
            "      <Unit_of_Variable>Degree C</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>SST_C</Variable_Name>\n" +
            "      <Description_of_Variable>Sea surface temperature (&#176;C)</Description_of_Variable>\n" +
            "      <Unit_of_Variable>Degree C</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>SAL_permil</Variable_Name>\n" +
            "      <Description_of_Variable>Sea surface salinity on Practical Salinity Scale (o/oo)</Description_of_Variable>\n" +
            "      <Unit_of_Variable>ppt</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>fCO2_SW@SST_uatm</Variable_Name>\n" +
            "      <Description_of_Variable>Fugacity of CO2 in sea water at SST and 100% humidity (&#956;atm)</Description_of_Variable>\n" +
            "      <Unit_of_Variable>&#956;atm</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>fCO2_ATM_interpolated_uatm</Variable_Name>\n" +
            "      <Description_of_Variable>Fugacity of CO2 in air corresponding to the interpolated xCO2 at SST and 100% humidity (&#956;atm)</Description_of_Variable>\n" +
            "      <Unit_of_Variable>&#956;atm</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>dfCO2_uatm</Variable_Name>\n" +
            "      <Description_of_Variable>Sea water fCO2 minus interpolated air fCO2 (&#956;atm)</Description_of_Variable>\n" +
            "      <Unit_of_Variable>&#956;atm</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>WOCE_QC_FLAG</Variable_Name>\n" +
            "      <Description_of_Variable>Quality control flag for fCO2 values (2=good, 3=questionable)</Description_of_Variable>\n" +
            "      <Unit_of_Variable>None</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "    <Variable>\n" +
            "      <Variable_Name>QC_SUBFLAG</Variable_Name>\n" +
            "      <Description_of_Variable>Quality control subflag for fCO2 values, provides explanation when QC flag=3</Description_of_Variable>\n" +
            "      <Unit_of_Variable>None</Unit_of_Variable>\n" +
            "    </Variable>\n" +
            "  </Variables_Info>\n" +
            "  <Method_Description>\n" +
            "    <Equilibrator_Design>\n" +
            "      <Depth_of_Sea_Water_Intake>5 meters</Depth_of_Sea_Water_Intake>\n" +
            "      <Location_of_Sea_Water_Intake>Bow</Location_of_Sea_Water_Intake>\n" +
            "      <Equilibrator_Type>Spray head above dynamic pool, no thermal jacket</Equilibrator_Type>\n" +
            "      <Equilibrator_Volume>0.95 L (0.4 L water, 0.55 L headspace)</Equilibrator_Volume>\n" +
            "      <Water_Flow_Rate>1.5 - 2.0 L/min</Water_Flow_Rate>\n" +
            "      <Headspace_Gas_Flow_Rate>70 - 150 ml/min</Headspace_Gas_Flow_Rate>\n" +
            "      <Vented>Yes</Vented>\n" +
            "      <Drying_Method_for_CO2_in_water>Gas stream passes through a thermoelectric condenser (~5 &#176;C) and then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).</Drying_Method_for_CO2_in_water>\n" +
            "      <Additional_Information>Primary equilibrator is vented through a secondary equilibrator.</Additional_Information>\n" +
            "    </Equilibrator_Design>\n" +
            "    <CO2_in_Marine_Air>\n" +
            "      <Measurement>Yes, 5 readings in a group every 3 hours</Measurement>\n" +
            "      <Location_and_Height>Bow mast, ~18 meters above sea surface</Location_and_Height>\n" +
            "      <Drying_Method>Gas stream passes through a thermoelectric condenser (~5 &#176;C) and then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).</Drying_Method>\n" +
            "    </CO2_in_Marine_Air>\n" +
            "    <CO2_Sensors>\n" +
            "      <CO2_Sensor>\n" +
            "        <Measurement_Method>IR</Measurement_Method>\n" +
            "        <Manufacturer>LI-COR</Manufacturer>\n" +
            "        <Model>7000</Model>\n" +
            "        <Frequency>Every 140 seconds, except during calibration</Frequency>\n" +
            "        <Resolution_Water>&#177; 0.01 &#956;atm</Resolution_Water>\n" +
            "        <Uncertainty_Water>&#177; 2 &#956;atm</Uncertainty_Water>\n" +
            "        <Resolution_Air>&#177; 0.01 &#956;atm</Resolution_Air>\n" +
            "        <Uncertainty_Air>&#177; 0.5 &#956;atm</Uncertainty_Air>\n" +
            "        <Manufacturer_of_Calibration_Gas>\n" +
            "          Std 1: LL100000, 0.00 ppm, owned by AOML, used every ~4.5 hours. \n" +
            "          Std 2: JA02140, 234.21 ppm, owned by AOML, used every ~4.5 hours.\n" +
            "          Std 3: JA02689, 406.90 ppm, owned by AOML, used every ~4.5 hours.\n" +
            "          Std 4: JB03276, 471.65 ppm, owned by AOML, used every ~4.5 hours.\n" +
            "        </Manufacturer_of_Calibration_Gas>\n" +
            "        <No_Of_Non_Zero_Gas_Stds>3</No_Of_Non_Zero_Gas_Stds>\n" +
            "        <CO2_Sensor_Calibration>The analyzer is calibrated every 4 hours with field standards that in turn were calibrated with primary standards that are directly traceable to the WMO X2007 scale. The zero gas is ultra-high purity air.</CO2_Sensor_Calibration>\n" +
            "        <Sensor_Calibration></Sensor_Calibration>\n" +
            "        <Other_Comments>Instrument is located in an air-conditioned laboratory.  Ultra-High Purity air (0.0 ppm CO2) and the high standard gas are used to zero and span the LI-COR analyzer.</Other_Comments>\n" +
            "        <Method_References>Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, T. Johannessen, A. Olsen, R. A. Feely, and C. E. Cosca (2009), Recommendations for autonomous underway pCO2 measuring systems and data reduction routines, Deep-Sea Res II, 56, 512-522.</Method_References>\n" +
            "        <Details_Co2_Sensing>details of CO2 sensing (not required)</Details_Co2_Sensing>\n" +
            "        <Measured_Co2_Params>xco2(dry)</Measured_Co2_Params>\n" +
            "      </CO2_Sensor>\n" +
            "    </CO2_Sensors>\n" +
            "    <Sea_Surface_Temperature>\n" +
            "      <Location>In engine room, about 2 m after the seachest, before the SW pumps.</Location>\n" +
            "      <Manufacturer>Seabird, Inc.</Manufacturer>\n" +
            "      <Model>SBE 38</Model>\n" +
            "      <Accuracy_degC>0.001</Accuracy_degC>\n" +
            "      <Precision_degC>0.0003</Precision_degC>\n" +
            "      <Calibration>Factory calibration</Calibration>\n" +
            "      <Other_Comments>Manufacturer's Resolution is taken as Precision; Maintained by ship.</Other_Comments>\n" +
            "    </Sea_Surface_Temperature>\n" +
            "    <Equilibrator_Temperature>\n" +
            "      <Location>Inserted into equilibrator ~5 cm below water level</Location>\n" +
            "      <Manufacturer>Hart</Manufacturer>\n" +
            "      <Model>1521</Model>\n" +
            "      <Accuracy_degC>0.025</Accuracy_degC>\n" +
            "      <Precision_degC>0.001</Precision_degC>\n" +
            "      <Calibration>Factory calibration</Calibration>\n" +
            "      <Other_Comments>Resolution is taken as Precision.</Other_Comments>\n" +
            "    </Equilibrator_Temperature>\n" +
            "    <Equilibrator_Pressure>\n" +
            "      <Location>Attached to equilibrator headspace.</Location>\n" +
            "      <Manufacturer>Setra</Manufacturer>\n" +
            "      <Model>270</Model>\n" +
            "      <Accuracy_hPa>0.05</Accuracy_hPa>\n" +
            "      <Precision_hPa>0.015</Precision_hPa>\n" +
            "      <Calibration>Factory calibration</Calibration>\n" +
            "      <Other_Comments>Manufacturer's Resolution is taken as Precision.</Other_Comments>\n" +
            "    </Equilibrator_Pressure>\n" +
            "    <Atmospheric_Pressure>\n" +
            "      <Location>Next to the bridge, ~15 m above the sea surface water</Location>\n" +
            "      <Manufacturer>RMYoung</Manufacturer>\n" +
            "      <Model>61201</Model>\n" +
            "      <Accuracy>&#177; 0.5 hPa</Accuracy>\n" +
            "      <Precision> 0.01 hPa</Precision>\n" +
            "      <Calibration>Factory calibration</Calibration>\n" +
            "      <Normalized>yes</Normalized>\n" +
            "      <Other_Comments>Manufacturer's Resolution is taken as Precision; Maintained by ship.</Other_Comments>\n" +
            "    </Atmospheric_Pressure>\n" +
            "    <Sea_Surface_Salinity>\n" +
            "      <Location>In Chem lab, next to CO2 system</Location>\n" +
            "      <Manufacturer>Seabird</Manufacturer>\n" +
            "      <Model>SBE 45</Model>\n" +
            "      <Accuracy>&#177; 0.005 o/oo</Accuracy>\n" +
            "      <Precision>0.0002 o/oo</Precision>\n" +
            "      <Calibration>Factory calibration</Calibration>\n" +
            "      <Other_Comments>Manufacturer's Resolution is taken as Precision; Maintained by ship.</Other_Comments>\n" +
            "    </Sea_Surface_Salinity>\n" +
            "  </Method_Description>\n" +
            "  <Data_set_References></Data_set_References>\n" +
            "  <Additional_Information> The analytical system operated fine during this cruise.  The water flow sensor was not responding; however, water flow through the equilibrator was confirmed visually by ship's personnel and by the various temperature sensors. \n" +
            "  Several times during the cruise, the ship's sensors were not recorded; so the values were interpolated from surrounding values.  The ship had an unscheduled 4-day stop in Pensacola starting mid-day of 21 Nov, 2018, because of rough weather.  \n" +
            "  Original Data Location: http://www.aoml.noaa.gov/ocd/ocdweb/gunter/gunter_introduction.html\n" +
            "  Full unprocessed data files from analytical instrument including flow information and ship's meteorological and TSG data at time of sampling can be obtained upon request. </Additional_Information>\n" +
            "  <Citation></Citation>\n" +
            "  <Preliminary_Quality_control>NA</Preliminary_Quality_control>\n" +
            "  <form_type>underway</form_type>\n" +
            "</x_tags>\n";
}
