/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;

import gov.noaa.pmel.socat.dashboard.ome.OmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.TimeZone;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

/**
 * Tests for methods in {@link gov.noaa.pmel.socat.dashboard.ome}
 * 
 * @author Karl Smith
 */
public class OmeMetadataTest {

	private static final String ACTUAL_OME_XML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<x_tags>" +
			"  <User>" +
			"    <Name>Catherine Cosca</Name>" +
			"    <Organization>...</Organization>" +
			"    <Address>...</Address>" +
			"    <Phone>...</Phone>" +
			"    <Email>...</Email>" +
			"  </User>" +
			"  <Investigator>" +
			"    <Name>Cosca, Catherine E.</Name>" +
			"    <Organization>PMEL</Organization>" +
			"    <Address>...</Address>" +
			"    <Phone>...</Phone>" +
			"    <Email>...</Email>" +
			"  </Investigator>" +
			"  <Investigator>" +
			"    <Name>Feely, Richard A.</Name>" +
			"    <Organization>PMEL</Organization>" +
			"    <Address>...</Address>" +
			"    <Phone>...</Phone>" +
			"    <Email>...</Email>" +
			"  </Investigator>" +
			"  <Investigator>" +
			"    <Name>Alin, Simone R.</Name>" +
			"    <Organization>PMEL</Organization>" +
			"    <Address>...</Address>" +
			"  </Investigator>" +
			"  <Investigator>" +
			"    <Name>Lebon, Geoffrey T.</Name>" +
			"  </Investigator>" +
			"  <Dataset_Info>" +
			"    <Funding_Info>NOAA Oceanic and Atmospheric Research</Funding_Info>" +
			"    <Submission_Dates>" +
			"      <Initial_Submission>2013/12/31</Initial_Submission>" +
			"    </Submission_Dates>" +
			"  </Dataset_Info>" +
			"  <Cruise_Info>" +
			"    <Experiment>" +
			"      <Experiment_Name>SH1201</Experiment_Name>" +
			"      <Experiment_Type>VOS Lines</Experiment_Type>" +
			"      <Cruise>" +
			"        <Cruise_ID>332220120220</Cruise_ID>" +
			"        <Geographical_Coverage>" +
			"          <Geographical_Region>North American West Coast</Geographical_Region>" +
			"          <Bounds>" +
			"            <Westernmost_Longitude>-125.702</Westernmost_Longitude>" +
			"            <Easternmost_Longitude>-122.978</Easternmost_Longitude>" +
			"            <Northernmost_Latitude>49.027</Northernmost_Latitude>" +
			"            <Southernmost_Latitude>48.183</Southernmost_Latitude>" +
			"          </Bounds>" +
			"        </Geographical_Coverage>" +
			"        <Temporal_Coverage>" +
			"          <Start_Date>20120220</Start_Date>" +
			"          <End_Date>20120229</End_Date>" +
			"        </Temporal_Coverage>" +
			"      </Cruise>" +
			"    </Experiment>" +
			"    <Vessel>" +
			"      <Vessel_Name>Bell M. Shimada</Vessel_Name>" +
			"      <Vessel_ID>3322</Vessel_ID>" +
			"      <Vessel_Owner>NOAA</Vessel_Owner>" +
			"    </Vessel>" +
			"  </Cruise_Info>" +
			"  <Variables_Info>" +
			"    <Variable>" +
			"      <Variable_Name>xCO2W_PPM</Variable_Name>" +
			"      <Description_of_Variable>PPM</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>xCO2A_PPM</Variable_Name>" +
			"      <Description_of_Variable>PPM</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>xCO2A_INTERPOLATED_PPM</Variable_Name>" +
			"      <Description_of_Variable>PPM</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>PRES_EQUIL_hPa</Variable_Name>" +
			"      <Description_of_Variable>HectoPascals</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>PRES_SEALEVEL_hPa</Variable_Name>" +
			"      <Description_of_Variable>HectoPascals</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>EqTEMP_C</Variable_Name>" +
			"      <Description_of_Variable>Degrees C</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>SST(TSG)_C</Variable_Name>" +
			"      <Description_of_Variable>Degrees C</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>SAL(TSG)_PERMIL</Variable_Name>" +
			"      <Description_of_Variable>Permil</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>fCO2W@SST_uATM</Variable_Name>" +
			"      <Description_of_Variable>microatmospheres</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>fCO2A_uATM</Variable_Name>" +
			"      <Description_of_Variable>microatmospheres</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>dfCO2_uATM</Variable_Name>" +
			"      <Description_of_Variable>microatmospheres</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>QC_FLAG</Variable_Name>" +
			"      <Description_of_Variable>WOCE QC Flag</Description_of_Variable>" +
			"    </Variable>" +
			"    <Variable>" +
			"      <Variable_Name>QC_SUBFLAG</Variable_Name>" +
			"      <Description_of_Variable>QC SUBFLAG</Description_of_Variable>" +
			"    </Variable>" +
			"  </Variables_Info>" +
			"  <Method_Description>" +
			"    <Equilibrator_Design>" +
			"      <Depth_of_Sea_Water_Intake>3</Depth_of_Sea_Water_Intake>" +
			"      <Location_of_Sea_Water_Intake>Bow</Location_of_Sea_Water_Intake>" +
			"      <Equilibrator_Type>Showerhead</Equilibrator_Type>" +
			"      <Equilibrator_Volume>0.5</Equilibrator_Volume>" +
			"      <Water_Flow_Rate>3.5</Water_Flow_Rate>" +
			"      <Headspace_Gas_Flow_Rate>60</Headspace_Gas_Flow_Rate>" +
			"      <Vented>Yes</Vented>" +
			"      <Drying_Method_for_CO2_in_water>Thermoelectric condensor; Perma Pure (Naphion); magnesium perchlorate.  80% dry</Drying_Method_for_CO2_in_water>" +
			"    </Equilibrator_Design>" +
			"    <CO2_in_Marine_Air>" +
			"      <Measurement>yes, 6 measurements every 3 hours</Measurement>" +
			"      <Location_and_Height>Bow, 10m above water line</Location_and_Height>" +
			"    </CO2_in_Marine_Air>" +
			"    <CO2_Sensors>" +
			"      <CO2_Sensor>" +
			"        <Measurement_Method>Infrared absorption of dry gas</Measurement_Method>" +
			"        <Manufacturer>Licor</Manufacturer>" +
			"        <Model>Licor 7000, Serial # IRG4-0233</Model>" +
			"        <Frequency>Every 180 seconds</Frequency>" +
			"        <Resolution_Water>1 uatm</Resolution_Water>" +
			"        <Uncertainty_Water>2 uatm</Uncertainty_Water>" +
			"        <Resolution_Air>.1 ppm</Resolution_Air>" +
			"        <Manufacturer_of_Calibration_Gas>LL55884 - 301.13 ppm, LL83547 - 450.34 ppm, LL83516 - 552.68 ppm, LL154359 - 579.58 ppm, CO2 system was calibrated every 8.5 hours.</Manufacturer_of_Calibration_Gas>" +
			"        <CO2_Sensor_Calibration>Standards are from ESRL in Boulder CO; directly traceable to WMO scale; every 6 hours</CO2_Sensor_Calibration>" +
			"        <Environmental_Control>CO2 system, built by General Oceanics, was installed in the computer lab of the NOAA Ship Bell Shimada.  The system was maintained by the CO2 group at PMEL, and operated by the ship's survey technician.  The Licor infrared sensor was zeroed with a 99.9% nitrogen gas and spanned with the high standard gas.</Environmental_Control>" +
			"        <Method_References>Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, T. Johannson, A. Olsen, R. A. Feely, and C. E. Cosca (2009), Recommendations for autonomous underway pCO2 measuring systems and data reduction routines, Deep-Sea Res II, 56, 512-522.</Method_References>" +
			"      </CO2_Sensor>" +
			"    </CO2_Sensors>" +
			"    <Sea_Surface_Temperature>" +
			"      <Location>Bow intake, ~3m below water line</Location>" +
			"      <Manufacturer>Seabird</Manufacturer>" +
			"      <Model>SBE45, maintained by the ship</Model>" +
			"      <Accuracy>.01 °C</Accuracy>" +
			"      <Precision>.001 °C</Precision>" +
			"      <Calibration>Calibrated annually at Seabird</Calibration>" +
			"    </Sea_Surface_Temperature>" +
			"    <Equilibrator_Temperature>" +
			"      <Location>In the equilibrator of the CO2 system located in the ship's computer lab.</Location>" +
			"      <Manufacturer>Hart Scientific</Manufacturer>" +
			"      <Model>1521; serial number A8B280</Model>" +
			"      <Accuracy>0.0025 °C</Accuracy>" +
			"      <Precision>0.002 °C</Precision>" +
			"      <Calibration>Calibrated at Hart Scientific</Calibration>" +
			"      <Warming>0.33 °C</Warming>" +
			"      <Other_Comments>Equilibrator temperature was measured with a Hart Scientific model 1521 digital thermometer, serial number A8B280, with an NIST traceable model 5610 thermistor probe, serial number A8C0309 located inside the equilibrator.</Other_Comments>" +
			"    </Equilibrator_Temperature>" +
			"    <Equilibrator_Pressure>" +
			"      <Location>In the equilibrator of the CO2 system located in the ship's computer lab.</Location>" +
			"      <Manufacturer>Setra</Manufacturer>" +
			"      <Model>239</Model>" +
			"      <Accuracy>+/- .15% full scale</Accuracy>" +
			"      <Precision>+/- .005% full scale</Precision>" +
			"      <Calibration>Factory calibration</Calibration>" +
			"      <Other_Comments>The equilibrator was passively vented to a secondary equilibrator, and the Licor sample output was vented to the laboratory when CO2 measurements were made.  Pressure in the laboratory was measured with a GE Druck barometer with an accuracy of  ± 0.01 %fs.</Other_Comments>" +
			"    </Equilibrator_Pressure>" +
			"    <Atmospheric_Pressure>" +
			"      <Location>On flying bridge of the ship; approximated 10 m above sea level</Location>" +
			"      <Manufacturer>GE</Manufacturer>" +
			"      <Model>Druck</Model>" +
			"      <Accuracy>± 0.01 %fs</Accuracy>" +
			"      <Precision>± 0.005 %fs</Precision>" +
			"    </Atmospheric_Pressure>" +
			"    <Sea_Surface_Salinity>" +
			"      <Location>Bow intake, ~3m below water line</Location>" +
			"      <Manufacturer>Seabird</Manufacturer>" +
			"      <Model>SBE45; maintained by the ship</Model>" +
			"      <Accuracy>0.005 PSU</Accuracy>" +
			"      <Precision>0.0002 PSU</Precision>" +
			"      <Calibration>Calibrated annually at Seabird</Calibration>" +
			"    </Sea_Surface_Salinity>" +
			"  </Method_Description>" +
			"  <Citation>Cosca, C., R. Feely, S. Alin, and G. Lebon. 2013. Sea Surface and Atmospheric fCO2 measurements from the NOAA Ship Bell M. Shimada 2012 VOS project line, cruise SH1201.</Citation>" +
			"  <Data_Set_Link>" +
			"    <URL>www.pmel.noaa.gov/co2/</URL>" +
			"    <Label>PMEL Underway pCO2 data</Label>" +
			"  </Data_Set_Link>" +
			"  <Data_Link>" +
			"    <URL>SH1201.csv</URL>" +
			"  </Data_Link>" +
			"  <form_type>underway</form_type>" +
			"</x_tags>";
	
	private static final String ACTUAL_EXPOCODE = "332220120220";
	private static final String ACTUAL_CRUISE_NAME = "SH1201";
	private static final String ACTUAL_VESSEL_NAME = "Bell M. Shimada";
	private static final String ACTUAL_INVESTIGATORS_STRING = 
			"Cosca, Catherine E. ; Feely, Richard A. ; Alin, Simone R. ; Lebon, Geoffrey T.";
/*
	private static final ArrayList<String> ACTUAL_INVESTIGATORS = 
			new ArrayList<String>(Arrays.asList(ACTUAL_INVESTIGATORS_STRING.split(" ; ")));
	private static final ArrayList<String> ACTUAL_ORGANIZATIONS = 
			new ArrayList<String>(Arrays.asList("PMEL", "PMEL", "PMEL", ""));
*/
	private static final String ACTUAL_ORGANIZATIONS_STRING = "PMEL ; ";
	private static final double ACTUAL_WEST_LON = -125.702;
	private static final double ACTUAL_EAST_LON = -122.978;
	private static final double ACTUAL_SOUTH_LAT = 48.183;
	private static final double ACTUAL_NORTH_LAT = 49.027;
	private static final String ACTUAL_START_STRING = "20120220";
	private static final String ACTUAL_END_STRING = "20120229";
	private static final SimpleDateFormat DATE_FRMT = new SimpleDateFormat("yyyyMMdd");
	static {
		DATE_FRMT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	// private static final String ACTUAL_ORIG_DATA_REF = "www.pmel.noaa.gov/co2/SH1201.csv";
	private static final String ACTUAL_ORIG_DATA_REF = "www.pmel.noaa.gov/co2/";

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#getCruiseName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#setCruiseName(java.lang.String)}.
	 */
	@Test
	public void testGetSetCruiseName() {
/*
		OmeMetadata mdata = new OmeMetadata();
		assertEquals("", mdata.getCruiseName());
		mdata.setCruiseName(ACTUAL_CRUISE_NAME);
		assertEquals(ACTUAL_CRUISE_NAME, mdata.getCruiseName());
		mdata.setCruiseName(null);
		assertEquals("", mdata.getCruiseName());
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#getVesselName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#setVesselName(java.lang.String)}.
	 */
	@Test
	public void testGetSetVesselName() {
/*
		OmeMetadata mdata = new OmeMetadata();
		assertEquals("", mdata.getVesselName());
		mdata.setVesselName(ACTUAL_VESSEL_NAME);
		assertEquals(ACTUAL_VESSEL_NAME, mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setVesselName(null);
		assertEquals("", mdata.getVesselName());
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#getInvestigators()
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#setInvestigators(java.util.ArrayList)}.
	 */
	@Test
	public void testGetSetInvestigators() {
/*
		OmeMetadata mdata = new OmeMetadata();
		assertEquals(0, mdata.getInvestigators().size());
		mdata.setInvestigators(ACTUAL_INVESTIGATORS);
		assertEquals(ACTUAL_INVESTIGATORS, mdata.getInvestigators());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setInvestigators(null);
		assertEquals(0, mdata.getInvestigators().size());
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#getOrganizations()
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#setOrganizations(java.util.ArrayList)}.
	 */
	@Test
	public void testGetSetOrganizations() {
/*
		OmeMetadata mdata = new OmeMetadata();
		assertEquals(0, mdata.getOrganizations().size());
		mdata.setOrganizations(ACTUAL_ORGANIZATIONS);
		assertEquals(ACTUAL_ORGANIZATIONS, mdata.getOrganizations());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setOrganizations(null);
		assertEquals(0, mdata.getOrganizations().size());
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#getWestmostLongitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#setWestmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetWestmostLongitude() {
/*
		OmeMetadata mdata = new OmeMetadata();
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		mdata.setWestmostLongitude(ACTUAL_WEST_LON);
		assertEquals(ACTUAL_WEST_LON, mdata.getWestmostLongitude(), 1.0E-4);
		assertEquals(0, mdata.getOrganizations().size());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setWestmostLongitude(null);
		assertTrue( mdata.getWestmostLongitude().isNaN() );
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#getEastmostLongitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#setEastmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetEastmostLongitude() {
/*
		OmeMetadata mdata = new OmeMetadata();
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		mdata.setEastmostLongitude(ACTUAL_EAST_LON);
		assertEquals(ACTUAL_EAST_LON, mdata.getEastmostLongitude(), 1.0E-4);
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals(0, mdata.getOrganizations().size());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setEastmostLongitude(null);
		assertTrue( mdata.getEastmostLongitude().isNaN() );
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#getSouthmostLatitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#setSouthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSouthmostLatitude() {
/*
		OmeMetadata mdata = new OmeMetadata();
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		mdata.setSouthmostLatitude(ACTUAL_SOUTH_LAT);
		assertEquals(ACTUAL_SOUTH_LAT, mdata.getSouthmostLatitude(), 1.0E-4);
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals(0, mdata.getOrganizations().size());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setSouthmostLatitude(null);
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#getNorthmostLatitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#setNorthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetNorthmostLatitude() {
/*
		OmeMetadata mdata = new OmeMetadata();
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		mdata.setNorthmostLatitude(ACTUAL_NORTH_LAT);
		assertEquals(ACTUAL_NORTH_LAT, mdata.getNorthmostLatitude(), 1.0E-4);
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals(0, mdata.getOrganizations().size());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setNorthmostLatitude(null);
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#getStartDate()}
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#setStartDate(java.util.Date)}.
	 * @throws ParseException 
	 */
	@Test
	public void testGetSetStartDate() throws ParseException {
/*
		final Date myStartDate = DATE_FRMT.parse(ACTUAL_START_STRING);
		OmeMetadata mdata = new OmeMetadata();
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getStartDate());
		mdata.setStartDate(myStartDate);
		assertEquals(myStartDate, mdata.getStartDate());
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals(0, mdata.getOrganizations().size());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setStartDate(null);
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getStartDate());
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#getEndDate()}
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#setEndDate(java.util.Date)}.
	 * @throws ParseException 
	 */
	@Test
	public void testGetSetEndDate() throws ParseException {
/*
		final Date myEndDate = DATE_FRMT.parse(ACTUAL_END_STRING);
		OmeMetadata mdata = new OmeMetadata();
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndDate());
		mdata.setEndDate(myEndDate);
		assertEquals(myEndDate, mdata.getEndDate());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getStartDate());
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals(0, mdata.getOrganizations().size());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setEndDate(null);
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndDate());
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#getOrigDataRef()}
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#setOrigDataRef(java.lang.String)}.
	 */
	@Test
	public void testGetSetOrigDataRef() {
/*
		OmeMetadata mdata = new OmeMetadata();
		assertEquals("", mdata.getOrigDataRef());
		mdata.setOrigDataRef(ACTUAL_ORIG_DATA_REF);
		assertEquals(ACTUAL_ORIG_DATA_REF, mdata.getOrigDataRef());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndDate());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getStartDate());
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals(0, mdata.getOrganizations().size());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setOrigDataRef(null);
		assertEquals("", mdata.getOrigDataRef());
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#equals(java.lang.Object)}.
	 * @throws ParseException 
	 */
	@Test
	public void testHashCodeEqualsObject() throws ParseException {
/*
		final Date myStartDate = DATE_FRMT.parse(ACTUAL_START_STRING);
		final Date myEndDate = DATE_FRMT.parse(ACTUAL_END_STRING);

		OmeMetadata mdata = new OmeMetadata();
		assertFalse( mdata.equals(null) );
		assertFalse( mdata.equals(ACTUAL_EXPOCODE) );

		OmeMetadata other = new OmeMetadata();
		assertFalse( mdata == other );
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setExpocode(ACTUAL_EXPOCODE);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setExpocode(ACTUAL_EXPOCODE);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setCruiseName(ACTUAL_CRUISE_NAME);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setCruiseName(ACTUAL_CRUISE_NAME);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setVesselName(ACTUAL_VESSEL_NAME);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setVesselName(ACTUAL_VESSEL_NAME);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setInvestigators(ACTUAL_INVESTIGATORS);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setInvestigators(ACTUAL_INVESTIGATORS);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setOrganizations(ACTUAL_ORGANIZATIONS);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setOrganizations(ACTUAL_ORGANIZATIONS);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setWestmostLongitude(ACTUAL_WEST_LON);
		// Floating point values not used in hashCode
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setWestmostLongitude(ACTUAL_WEST_LON);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setEastmostLongitude(ACTUAL_EAST_LON);
		// Floating point values not used in hashCode
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setEastmostLongitude(ACTUAL_EAST_LON);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setSouthmostLatitude(ACTUAL_SOUTH_LAT);
		// Floating point values not used in hashCode
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setSouthmostLatitude(ACTUAL_SOUTH_LAT);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setNorthmostLatitude(ACTUAL_NORTH_LAT);
		// Floating point values not used in hashCode
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setNorthmostLatitude(ACTUAL_NORTH_LAT);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setStartDate(myStartDate);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setStartDate(myStartDate);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setEndDate(myEndDate);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setEndDate(myEndDate);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setOrigDataRef(ACTUAL_ORIG_DATA_REF);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setOrigDataRef(ACTUAL_ORIG_DATA_REF);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#assignFromOmeXmlDoc(org.jdom2.Document)}.
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	@Test
	public void testAssignFromOmeXmlDoc() throws JDOMException, IOException {
		Document omeDoc = (new SAXBuilder()).build(
				new ByteArrayInputStream(ACTUAL_OME_XML.getBytes()));
		OmeMetadata mdata = new OmeMetadata();
		mdata.setExpocode(ACTUAL_EXPOCODE);
		mdata.assignFromOmeXmlDoc(omeDoc);
/*
		assertEquals(ACTUAL_EXPOCODE, mdata.getExpocode());
		assertEquals(ACTUAL_CRUISE_NAME, mdata.getCruiseName());
		assertEquals(ACTUAL_VESSEL_NAME, mdata.getVesselName());
		assertEquals(ACTUAL_INVESTIGATORS, mdata.getInvestigators());
		assertEquals(ACTUAL_ORGANIZATIONS, mdata.getOrganizations());
		assertEquals(ACTUAL_WEST_LON, mdata.getWestmostLongitude(), 1.0E-4);
		assertEquals(ACTUAL_EAST_LON, mdata.getEastmostLongitude(), 1.0E-4);
		assertEquals(ACTUAL_SOUTH_LAT, mdata.getSouthmostLatitude(), 1.0E-4);
		assertEquals(ACTUAL_NORTH_LAT, mdata.getNorthmostLatitude(), 1.0E-4);
		assertEquals(ACTUAL_ORIG_DATA_REF, mdata.getOrigDataRef());

		assertEquals(ACTUAL_START_STRING, DATE_FRMT.format(mdata.getStartDate()));
		assertEquals(ACTUAL_END_STRING, DATE_FRMT.format(mdata.getEndDate()));
*/
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#createMinimalOmeXmlDoc()}.
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	@Test
	public void testCreateMinimalOmeXmlDoc() throws IOException, JDOMException {
		final String uploadTimestamp = "2012-04-23 11:24 -0800";

		Document omeDoc = (new SAXBuilder()).build(
				new ByteArrayInputStream(ACTUAL_OME_XML.getBytes()));
		OmeMetadata mdata = new OmeMetadata();
		mdata.setExpocode(ACTUAL_EXPOCODE);
		mdata.setUploadTimestamp(uploadTimestamp);
		mdata.assignFromOmeXmlDoc(omeDoc);
		Document minOmeDoc = mdata.createMinimalOmeXmlDoc();

		OmeMetadata other = new OmeMetadata();
		other.setExpocode(ACTUAL_EXPOCODE);
		other.setUploadTimestamp(uploadTimestamp);
		other.assignFromOmeXmlDoc(minOmeDoc);

/*
		assertEquals(mdata, other);
*/
	}
	
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.ome.OmeMetadata#createSocatMetadata()}.
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	@Test
	public void testCreateSocatMetadata() throws IOException, JDOMException {
		final String uploadTimestamp = "2012-04-23 11:24 -0800";
		final Double socatVersion = 3.0;
		final String addlDocs = "addlDoc1.doc ; addlDoc2.pdf";
		final LinkedHashSet<String> addlDocsSet = 
				new LinkedHashSet<String>(Arrays.asList(addlDocs.split(" ; ")));
		final String qcFlag = "C";

		Document omeDoc = (new SAXBuilder()).build(
				new ByteArrayInputStream(ACTUAL_OME_XML.getBytes()));
		OmeMetadata mdata = new OmeMetadata();
		mdata.setExpocode(ACTUAL_EXPOCODE);
		mdata.setUploadTimestamp(uploadTimestamp);
		mdata.assignFromOmeXmlDoc(omeDoc);
		SocatMetadata socatMData = mdata.createSocatMetadata(socatVersion, addlDocsSet, qcFlag);
		assertEquals(socatVersion, socatMData.getSocatVersion(), 1.0E-4);
		assertEquals(addlDocs, socatMData.getAddlDocs());
		assertEquals(ACTUAL_EXPOCODE, socatMData.getExpocode());
		assertEquals(ACTUAL_CRUISE_NAME, socatMData.getCruiseName());
		assertEquals(ACTUAL_VESSEL_NAME, socatMData.getVesselName());
		assertEquals(ACTUAL_INVESTIGATORS_STRING, socatMData.getScienceGroup());
		assertEquals(ACTUAL_ORGANIZATIONS_STRING, socatMData.getOrganization());
		assertEquals(ACTUAL_WEST_LON, socatMData.getWestmostLongitude(), 1.0E-6);
		assertEquals(ACTUAL_EAST_LON, socatMData.getEastmostLongitude(), 1.0E-6);
		assertEquals(ACTUAL_SOUTH_LAT, socatMData.getSouthmostLatitude(), 1.0E-6);
		assertEquals(ACTUAL_NORTH_LAT, socatMData.getNorthmostLatitude(), 1.0E-6);
		assertEquals(ACTUAL_START_STRING, DATE_FRMT.format(socatMData.getBeginTime()));
		assertEquals(ACTUAL_END_STRING, DATE_FRMT.format(socatMData.getEndTime()));
		assertEquals(ACTUAL_ORIG_DATA_REF, socatMData.getOrigDataRef());
	}

}
