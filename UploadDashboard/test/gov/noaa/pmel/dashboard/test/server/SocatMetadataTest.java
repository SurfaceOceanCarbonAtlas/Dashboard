/**
 * 
 */
package gov.noaa.pmel.dashboard.test.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.server.SocatMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.Date;
import java.util.TreeMap;

import org.junit.Test;

/**
 * Unit test for methods in gov.noaa.pmel.dashboard.shared.SocatMetadata.
 * The convenience getters and setters still work properly when their type 
 * is not part of the known types.
 * 
 * @author Karl Smith
 */
public class SocatMetadataTest {

	static final String EXPOCODE = "XXXX20140113";
	static final String CRUISE_NAME = "My Cruise";
	static final String VESSEL_NAME = "My Vessel";
	static final String ORGANIZATION_NAME = "PMEL/NOAA";
	static final String VESSEL_TYPE = "Battleship";
	static final Double WESTMOST_LONGITUDE = -160.0;
	static final Double EASTMOST_LONGITUDE = -135.0;
	static final Double SOUTHMOST_LATITUDE = 15.0;
	static final Double NORTHMOST_LATITUDE = 50.0;
	static final Date BEGIN_TIME = new Date();
	static final Date END_TIME = new Date(BEGIN_TIME.getTime() + 1000000L);
	static final String INVESTIGATOR_NAMES = "Smith, K. : Doe, J.";
	static final String SOCAT_VERSION = "3.0U";
	static final String ALL_REGION_IDS = "NT";
	static final String SOCAT_DOI = "doi:pangaea012345";
	static final String QC_FLAG = "C";

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getStringVariables()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setStringVariableValue(gov.noaa.pmel.dashboard.server.DashDataType,java.lang.String)}.
	 */
	@Test
	public void testGetSetStringVariableValue() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		mdata.setStringVariableValue(DashboardServerUtils.EXPOCODE, EXPOCODE);
		TreeMap<DashDataType,String> stringMap = mdata.getStringVariables();
		assertEquals(EXPOCODE, stringMap.get(DashboardServerUtils.EXPOCODE));
		mdata.setStringVariableValue(DashboardServerUtils.EXPOCODE, null);
		stringMap = mdata.getStringVariables();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, stringMap.get(DashboardServerUtils.EXPOCODE));
		boolean errCaught = false;
		try {
			mdata.setStringVariableValue(DashboardServerUtils.EASTERNMOST_LONGITUDE, EXPOCODE);
		} catch ( IllegalArgumentException ex ) {
			errCaught = true;
		}
		assertTrue( errCaught );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getDoubleVariables()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setDoubleVariableValue(gov.noaa.pmel.dashboard.server.DashDataType,java.lang.Double)}.
	 */
	@Test
	public void testGetSetDoubleVariableValue() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		Double value = Double.valueOf(EASTMOST_LONGITUDE);
		mdata.setDoubleVariableValue(DashboardServerUtils.EASTERNMOST_LONGITUDE, value);
		TreeMap<DashDataType,Double> doubleMap = mdata.getDoubleVariables();
		assertEquals(value, doubleMap.get(DashboardServerUtils.EASTERNMOST_LONGITUDE));
		mdata.setDoubleVariableValue(DashboardServerUtils.EASTERNMOST_LONGITUDE, null);
		doubleMap = mdata.getDoubleVariables();
		assertEquals(DashboardUtils.FP_MISSING_VALUE, doubleMap.get(DashboardServerUtils.EASTERNMOST_LONGITUDE));
		boolean errCaught = false;
		try {
			mdata.setDoubleVariableValue(DashboardServerUtils.EXPOCODE, value);
		} catch ( IllegalArgumentException ex ) {
			errCaught = true;
		}
		assertTrue( errCaught );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getDateVariables()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setDateVariableValue(gov.noaa.pmel.dashboard.server.DashDataType,java.util.Date)}.
	 */
	@Test
	public void testGetSetDateVariableValue() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		mdata.setDateVariableValue(DashboardServerUtils.TIME_COVERAGE_START, BEGIN_TIME);
		TreeMap<DashDataType,Date> dateMap = mdata.getDateVariables();
		assertEquals(BEGIN_TIME, dateMap.get(DashboardServerUtils.TIME_COVERAGE_START));
		mdata.setDateVariableValue(DashboardServerUtils.TIME_COVERAGE_START, null);
		dateMap = mdata.getDateVariables();
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, dateMap.get(DashboardServerUtils.TIME_COVERAGE_START));
		boolean errCaught = false;
		try {
			mdata.setDateVariableValue(DashboardServerUtils.EXPOCODE, BEGIN_TIME);
		} catch ( IllegalArgumentException ex ) {
			errCaught = true;
		}
		assertTrue( errCaught );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getExpocode()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testGetSetExpocode() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setExpocode(EXPOCODE);
		assertEquals(EXPOCODE, mdata.getExpocode());
		mdata.setExpocode(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getDatasetName()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setDatasetName(java.lang.String)}.
	 */
	@Test
	public void testGetSetDatasetName() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		mdata.setDatasetName(CRUISE_NAME);
		assertEquals(CRUISE_NAME, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setDatasetName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getVesselName()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setVesselName(java.lang.String)}.
	 */
	@Test
	public void testGetSetVesselName() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		mdata.setVesselName(VESSEL_NAME);
		assertEquals(VESSEL_NAME, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setVesselName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getOrganizationName()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setOrganizationName(java.lang.String)}.
	 */
	@Test
	public void testGetSetOrganization() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		mdata.setOrganizationName(ORGANIZATION_NAME);
		assertEquals(ORGANIZATION_NAME, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setOrganizationName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getInvestigatorNames()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setInvestigatorNames(java.lang.String)}.
	 */
	@Test
	public void testGetSetInvestigatorNames() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		mdata.setInvestigatorNames(INVESTIGATOR_NAMES);
		assertEquals(INVESTIGATOR_NAMES, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setInvestigatorNames(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getVesselType()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setVesselType(java.lang.String)}.
	 */
	@Test
	public void testGetSetVesselType() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
		mdata.setVesselType(VESSEL_TYPE);
		assertEquals(VESSEL_TYPE, mdata.getVesselType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setVesselType(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getSocatVersion()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setSocatVersion(java.lang.String)}.
	 */
	@Test
	public void testGetSetSocatVersion() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatVersion());
		mdata.setSocatVersion(SOCAT_VERSION);
		assertEquals(SOCAT_VERSION, mdata.getSocatVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setSocatVersion(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatVersion());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getAllRegionIDs()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setAllRegionIDs(java.lang.String)}.
	 */
	@Test
	public void testGetSetAllRegionIDs() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
		mdata.setAllRegionIDs(ALL_REGION_IDS);
		assertEquals(ALL_REGION_IDS, mdata.getAllRegionIDs());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setAllRegionIDs(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getSocatDOI()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setSocatDOI(java.lang.String)}.
	 */
	@Test
	public void testGetSetSocatDOI() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatDOI());
		mdata.setSocatDOI(SOCAT_DOI);
		assertEquals(SOCAT_DOI, mdata.getSocatDOI());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setSocatDOI(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatDOI());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getQcFlag()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setQcFlag(java.lang.Character)}.
	 */
	@Test
	public void testGetSetQCFlag() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE.toString(), mdata.getQcFlag());
		mdata.setQcFlag(QC_FLAG);
		assertEquals(QC_FLAG, mdata.getQcFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatDOI());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setQcFlag(null);
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE.toString(), mdata.getQcFlag());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getWestmostLongitude()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setWestmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetWestmostLongitude() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		mdata.setWestmostLongitude(WESTMOST_LONGITUDE);
		assertTrue( WESTMOST_LONGITUDE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE.toString(), mdata.getQcFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatDOI());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setWestmostLongitude(null);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getEastmostLongitude()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setEastmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetEastmostLongitude() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		mdata.setEastmostLongitude(EASTMOST_LONGITUDE);
		assertTrue( EASTMOST_LONGITUDE.equals(mdata.getEastmostLongitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE.toString(), mdata.getQcFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatDOI());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setEastmostLongitude(null);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getSouthmostLatitude()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setSouthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSouthmostLatitude() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		mdata.setSouthmostLatitude(SOUTHMOST_LATITUDE);
		assertTrue( SOUTHMOST_LATITUDE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE.toString(), mdata.getQcFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatDOI());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setSouthmostLatitude(null);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getNorthmostLatitude()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setNorthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetNorthmostLatitude() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		mdata.setNorthmostLatitude(NORTHMOST_LATITUDE);
		assertTrue( NORTHMOST_LATITUDE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE.toString(), mdata.getQcFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatDOI());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setNorthmostLatitude(null);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getBeginTime()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setBeginTime(java.util.Date)}.
	 */
	@Test
	public void testSetBeginTime() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getBeginTime());
		mdata.setBeginTime(BEGIN_TIME);
		assertEquals(BEGIN_TIME, mdata.getBeginTime());
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE.toString(), mdata.getQcFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatDOI());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setBeginTime(null);
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getBeginTime());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#getEndTime()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#setEndTime(java.util.Date)}.
	 */
	@Test
	public void testGetSetEndTime() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getEndTime());
		mdata.setEndTime(END_TIME);
		assertEquals(END_TIME, mdata.getEndTime());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE.toString(), mdata.getQcFlag());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatDOI());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSocatVersion());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVesselName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
		mdata.setEndTime(null);
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getEndTime());
	}


	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.SocatMetadata#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.server.SocatMetadata#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();

		SocatMetadata mdata = new SocatMetadata(knownTypes);
		assertFalse( mdata.equals(null) );
		assertFalse( mdata.equals(EXPOCODE) );

		SocatMetadata other = new SocatMetadata(knownTypes);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setExpocode(EXPOCODE);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setExpocode(EXPOCODE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setDatasetName(CRUISE_NAME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setDatasetName(CRUISE_NAME);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setVesselName(VESSEL_NAME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setVesselName(VESSEL_NAME);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setOrganizationName(ORGANIZATION_NAME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setOrganizationName(ORGANIZATION_NAME);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setInvestigatorNames(INVESTIGATOR_NAMES);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setInvestigatorNames(INVESTIGATOR_NAMES);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setVesselType(VESSEL_TYPE);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setVesselType(VESSEL_TYPE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setSocatVersion(SOCAT_VERSION);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setSocatVersion(SOCAT_VERSION);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setAllRegionIDs(ALL_REGION_IDS);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setAllRegionIDs(ALL_REGION_IDS);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setSocatDOI(SOCAT_DOI);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setSocatDOI(SOCAT_DOI);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setQcFlag(QC_FLAG);
		assertFalse( mdata.hashCode() == other.hashCode() );
		assertFalse( mdata.equals(other) );
		other.setQcFlag(QC_FLAG);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		// hashCode ignores floating point values
		mdata.setWestmostLongitude(WESTMOST_LONGITUDE);
		assertTrue( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setWestmostLongitude(WESTMOST_LONGITUDE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		// hashCode ignores floating point values
		mdata.setEastmostLongitude(EASTMOST_LONGITUDE);
		assertTrue( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setEastmostLongitude(EASTMOST_LONGITUDE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		// hashCode ignores floating point values
		mdata.setSouthmostLatitude(SOUTHMOST_LATITUDE);
		assertTrue( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setSouthmostLatitude(SOUTHMOST_LATITUDE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		// hashCode ignores floating point values
		mdata.setNorthmostLatitude(NORTHMOST_LATITUDE);
		assertTrue( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setNorthmostLatitude(NORTHMOST_LATITUDE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setBeginTime(BEGIN_TIME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setBeginTime(BEGIN_TIME);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setEndTime(END_TIME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setEndTime(END_TIME);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );
	}

}
