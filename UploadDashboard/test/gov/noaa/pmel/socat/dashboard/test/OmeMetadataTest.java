/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.server.OmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

/**
 * Tests for methods in {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata}
 * 
 * @author Karl Smith
 */
public class OmeMetadataTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#getCruiseName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#setCruiseName(java.lang.String)}.
	 */
	@Test
	public void testGetSetCruiseName() {
		final String actualCruiseName = "SH1201";
		OmeMetadata mdata = new OmeMetadata();
		assertEquals("", mdata.getCruiseName());
		mdata.setCruiseName(actualCruiseName);
		assertEquals(actualCruiseName, mdata.getCruiseName());
		mdata.setCruiseName(null);
		assertEquals("", mdata.getCruiseName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#getVesselName()}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#setVesselName(java.lang.String)}.
	 */
	@Test
	public void testGetSetVesselName() {
		final String actualVesselName = "Bell M. Shimada";
		OmeMetadata mdata = new OmeMetadata();
		assertEquals("", mdata.getVesselName());
		mdata.setVesselName(actualVesselName);
		assertEquals(actualVesselName, mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setVesselName(null);
		assertEquals("", mdata.getVesselName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#getInvestigators()
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#setInvestigators(java.util.ArrayList)}.
	 */
	@Test
	public void testGetSetInvestigators() {
		final ArrayList<String> actualInvestigators = new ArrayList<String>(Arrays.asList(
				"Cosca, Catherine E.", "Feely, Richard A.", "Alin, Simone R.", "Lebon, Geoffrey T."));
		OmeMetadata mdata = new OmeMetadata();
		assertEquals(0, mdata.getInvestigators().size());
		mdata.setInvestigators(actualInvestigators);
		assertEquals(actualInvestigators, mdata.getInvestigators());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setInvestigators(null);
		assertEquals(0, mdata.getInvestigators().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#getOrganizations()
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#setOrganizations(java.util.ArrayList)}.
	 */
	@Test
	public void testGetSetOrganizations() {
		final ArrayList<String> actualOrganizations = new ArrayList<String>(Arrays.asList(
				"PMEL", "PMEL", "PMEL", ""));
		OmeMetadata mdata = new OmeMetadata();
		assertEquals(0, mdata.getOrganizations().size());
		mdata.setOrganizations(actualOrganizations);
		assertEquals(actualOrganizations, mdata.getOrganizations());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setOrganizations(null);
		assertEquals(0, mdata.getOrganizations().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#getWestmostLongitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#setWestmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetWestmostLongitude() {
		final double westmostLongitude = -125.702;
		OmeMetadata mdata = new OmeMetadata();
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		mdata.setWestmostLongitude(westmostLongitude);
		assertEquals(westmostLongitude, mdata.getWestmostLongitude(), 1.0E-4);
		assertEquals(0, mdata.getOrganizations().size());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setWestmostLongitude(null);
		assertTrue( mdata.getWestmostLongitude().isNaN() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#getEastmostLongitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#setEastmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetEastmostLongitude() {
		final double eastmostLongitude = -122.978;
		OmeMetadata mdata = new OmeMetadata();
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		mdata.setEastmostLongitude(eastmostLongitude);
		assertEquals(eastmostLongitude, mdata.getEastmostLongitude(), 1.0E-4);
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals(0, mdata.getOrganizations().size());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setEastmostLongitude(null);
		assertTrue( mdata.getEastmostLongitude().isNaN() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#getSouthmostLatitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#setSouthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSouthmostLatitude() {
		final double southmostLatitude = 48.183;
		OmeMetadata mdata = new OmeMetadata();
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		mdata.setSouthmostLatitude(southmostLatitude);
		assertEquals(southmostLatitude, mdata.getSouthmostLatitude(), 1.0E-4);
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals(0, mdata.getOrganizations().size());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setSouthmostLatitude(null);
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#getNorthmostLatitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#setNorthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetNorthmostLatitude() {
		final double northmostLatitude = 49.027;
		OmeMetadata mdata = new OmeMetadata();
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		mdata.setNorthmostLatitude(northmostLatitude);
		assertEquals(northmostLatitude, mdata.getNorthmostLatitude(), 1.0E-4);
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertEquals(0, mdata.getOrganizations().size());
		assertEquals(0, mdata.getInvestigators().size());
		assertEquals("", mdata.getVesselName());
		assertEquals("", mdata.getCruiseName());
		mdata.setNorthmostLatitude(null);
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#getStartDate()}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#setStartDate(java.util.Date)}.
	 * @throws ParseException 
	 */
	@Test
	public void testGetSetStartDate() throws ParseException {
		final Date myStartDate = (new SimpleDateFormat("yyyyMMddZ")).parse("20120220+0000");
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
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#getEndDate()}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#setEndDate(java.util.Date)}.
	 * @throws ParseException 
	 */
	@Test
	public void testGetSetEndDate() throws ParseException {
		final Date myEndDate = (new SimpleDateFormat("yyyyMMddZ")).parse("20120229+0000");
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
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#getOrigDataRef()}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#setOrigDataRef(java.lang.String)}.
	 */
	@Test
	public void testGetSetOrigDataRef() {
		final String actualOrigDataRef = "www.pmel.noaa.gov/co2/SH1201.csv";
		OmeMetadata mdata = new OmeMetadata();
		assertEquals("", mdata.getOrigDataRef());
		mdata.setOrigDataRef(actualOrigDataRef);
		assertEquals(actualOrigDataRef, mdata.getOrigDataRef());
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
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#equals(java.lang.Object)}.
	 * @throws ParseException 
	 */
	@Test
	public void testHashCodeEqualsObject() throws ParseException {
		final String myExpocode = "332220120220";
		final String myCruiseName = "SH1201";
		final String myVesselName = "Bell M. Shimada";
		final ArrayList<String> myInvestigators = new ArrayList<String>(Arrays.asList(
				"Cosca, Catherine E.", "Feely, Richard A.", "Alin, Simone R.", "Lebon, Geoffrey T."));
		final ArrayList<String> myOrganizations = new ArrayList<String>(Arrays.asList( 
				"NOAA", "NOAA", "NOAA", ""));
		final double myWestmostLongitude = -125.702;
		final double myEastmostLongitude = -122.978;
		final double mySouthmostLatitude = 48.183;
		final double myNorthmostLatitude = 49.027;
		final Date myStartDate = (new SimpleDateFormat("yyyyMMddZ")).parse("20120220+0000");
		final Date myEndDate = (new SimpleDateFormat("yyyyMMddZ")).parse("20120229+0000");
		final String myOrigDataRef = "www.pmel.noaa.gov/co2/SH1201.csv";

		OmeMetadata mdata = new OmeMetadata();
		assertFalse( mdata.equals(null) );
		assertFalse( mdata.equals(myExpocode) );

		OmeMetadata other = new OmeMetadata();
		assertFalse( mdata == other );
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setExpocode(myExpocode);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setExpocode(myExpocode);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setCruiseName(myCruiseName);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setCruiseName(myCruiseName);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setVesselName(myVesselName);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setVesselName(myVesselName);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setInvestigators(myInvestigators);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setInvestigators(myInvestigators);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setOrganizations(myOrganizations);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setOrganizations(myOrganizations);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setWestmostLongitude(myWestmostLongitude);
		// Floating point values not used in hashCode
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setWestmostLongitude(myWestmostLongitude);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setEastmostLongitude(myEastmostLongitude);
		// Floating point values not used in hashCode
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setEastmostLongitude(myEastmostLongitude);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setSouthmostLatitude(mySouthmostLatitude);
		// Floating point values not used in hashCode
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setSouthmostLatitude(mySouthmostLatitude);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );

		mdata.setNorthmostLatitude(myNorthmostLatitude);
		// Floating point values not used in hashCode
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setNorthmostLatitude(myNorthmostLatitude);
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

		mdata.setOrigDataRef(myOrigDataRef);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setOrigDataRef(myOrigDataRef);
		assertTrue( mdata.hashCode() == other.hashCode() );
		assertTrue( mdata.equals(other) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#OmeMetadata(java.lang.String[], java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testOmeMetadataStringArrayStringString() {
		final String[] actualMetadataHeaders = {
			"Cruise Label", "ship/platform", "PI", "PI_2", "PI_3", "metadata_hyperlink", 
			"Metadata_hyperlink_2", "Metadata_hyperlink_3", "doi", "Expocode created", 
			"# Samples", "Station IDs", "Longitude Range", "Latitude Range", "Time Period"
		};

		final String actualMetadataString = "AR2007_09\tAlbert Rickmers\t" + 
			"Richard Feely\tNaN\tNaN\thttp://www.socat.info/metadata/AR2007_10_Readme.doc\t" +
			"NaN\tNaN\t10.3334/CDIAC/otg.VOS_Albert_Rickmers_2007\t54WA20060923\t7496\t" + 
			"2165109 ~ 2172604\t175�E ~ 240.1�E\t36.1�S ~ 30.8�N\tSep 2006 ~ 04 Oct 2006";

		final String uploadTimestamp = "2012-04-23 11:24 -0800";

		final String actualCruiseExpocode = "54WA20060923";
		final String actualCruiseName = "AR2007_09";
		final String actualVesselName = "Albert Rickmers";
		final ArrayList<String> actualInvestigators = new ArrayList<String>(Arrays.asList("Richard Feely"));
		final ArrayList<String> actualOrganizations = new ArrayList<String>(Arrays.asList(""));
		final String actualOrigDataRef = "10.3334/CDIAC/otg.VOS_Albert_Rickmers_2007";

		OmeMetadata mdata = new OmeMetadata(actualMetadataHeaders, actualMetadataString, uploadTimestamp);
		assertEquals(actualCruiseExpocode, mdata.getExpocode());
		assertEquals(actualCruiseName, mdata.getCruiseName());
		assertEquals(actualVesselName, mdata.getVesselName());
		assertEquals(actualInvestigators, mdata.getInvestigators());
		assertEquals(actualOrganizations, mdata.getOrganizations());
		assertTrue( mdata.getWestmostLongitude().isNaN() );
		assertTrue( mdata.getEastmostLongitude().isNaN() );
		assertTrue( mdata.getSouthmostLatitude().isNaN() );
		assertTrue( mdata.getNorthmostLatitude().isNaN() );
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getStartDate());
		assertEquals(SocatMetadata.DATE_MISSING_VALUE, mdata.getEndDate());
		assertEquals(actualOrigDataRef, mdata.getOrigDataRef());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#assignFromOmeXmlDoc(org.jdom2.Document)}.
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	@Test
	public void testAssignFromOmeXmlDoc() throws JDOMException, IOException {
		final String actualOmeXml = 
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
		
		final String actualExpocode = "332220120220";
		final String actualCruiseName = "SH1201";
		final String actualVesselName = "Bell M. Shimada";
		final ArrayList<String> actualInvestigators = new ArrayList<String>(Arrays.asList( 
				"Cosca, Catherine E.", "Feely, Richard A.", "Alin, Simone R.", "Lebon, Geoffrey T."));
		final ArrayList<String> actualOrganizations = new ArrayList<String>(Arrays.asList( 
				"PMEL", "PMEL", "PMEL", ""));
		final double actualWestLon = -125.702;
		final double actualEastLon = -122.978;
		final double actualSouthLat = 48.183;
		final double actualNorthLat = 49.027;
		final String actualStartString = "20120220";
		final String actualEndString = "20120229";
		final String actualOrigDataRef = "www.pmel.noaa.gov/co2/SH1201.csv";

		Document omeDoc = (new SAXBuilder()).build(
				new ByteArrayInputStream(actualOmeXml.getBytes()));
		OmeMetadata mdata = new OmeMetadata();
		mdata.setExpocode(actualExpocode);
		mdata.assignFromOmeXmlDoc(omeDoc);
		
		assertEquals(actualExpocode, mdata.getExpocode());
		assertEquals(actualCruiseName, mdata.getCruiseName());
		assertEquals(actualVesselName, mdata.getVesselName());
		assertEquals(actualInvestigators, mdata.getInvestigators());
		assertEquals(actualOrganizations, mdata.getOrganizations());
		assertEquals(actualWestLon, mdata.getWestmostLongitude(), 1.0E-4);
		assertEquals(actualEastLon, mdata.getEastmostLongitude(), 1.0E-4);
		assertEquals(actualSouthLat, mdata.getSouthmostLatitude(), 1.0E-4);
		assertEquals(actualNorthLat, mdata.getNorthmostLatitude(), 1.0E-4);
		assertEquals(actualOrigDataRef, mdata.getOrigDataRef());

		SimpleDateFormat dateFrmt = new SimpleDateFormat("yyyyMMdd");
		dateFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		assertEquals(actualStartString, dateFrmt.format(mdata.getStartDate()));
		assertEquals(actualEndString, dateFrmt.format(mdata.getEndDate()));
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.OmeMetadata#createMinimalOmeXmlDoc()}.
	 * @throws IOException 
	 */
	@Test
	public void testCreateMinimalOmeXmlDoc() throws IOException {
		final String[] actualMetadataHeaders = {
				"Cruise Label", "ship/platform", "PI", "PI_2", "PI_3", "metadata_hyperlink", 
				"Metadata_hyperlink_2", "Metadata_hyperlink_3", "doi", "Expocode created", 
				"# Samples", "Station IDs", "Longitude Range", "Latitude Range", "Time Period"
			};

			final String actualMetadataString = "AR2007_09\tAlbert Rickmers\t" + 
				"Richard Feely\tNaN\tNaN\thttp://www.socat.info/metadata/AR2007_10_Readme.doc\t" +
				"NaN\tNaN\t10.3334/CDIAC/otg.VOS_Albert_Rickmers_2007\t54WA20060923\t7496\t" + 
				"2165109 ~ 2172604\t175�E ~ 240.1�E\t36.1�S ~ 30.8�N\tSep 2006 ~ 04 Oct 2006";

			final String uploadTimestamp = "2012-04-23 11:24 -0800";

			OmeMetadata mdata = new OmeMetadata(actualMetadataHeaders, actualMetadataString, uploadTimestamp);
			Document minOmeDoc = mdata.createMinimalOmeXmlDoc();

			OmeMetadata other = new OmeMetadata();
			other.setExpocode(mdata.getExpocode());
			other.setFilename(mdata.getFilename());
			other.setUploadTimestamp(uploadTimestamp);
			other.assignFromOmeXmlDoc(minOmeDoc);

			assertEquals(mdata, other);
	}
	
}
