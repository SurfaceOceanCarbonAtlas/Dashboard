/**
 * 
 */
package gov.noaa.pmel.dashboard.test.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Properties;
import java.util.TreeMap;

import org.junit.Test;

import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.DsgMetadata;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

/**
 * Unit test for methods in gov.noaa.pmel.dashboard.shared.DsgMetadata.
 * The convenience getters and setters still work properly when their type 
 * is not part of the known types.
 * 
 * @author Karl Smith
 */
public class DsgMetadataTest {

	public static final DashDataType QC_FLAG_TYPE = new DashDataType("qc_flag", 
			120.0, "QC flag", DashboardUtils.CHAR_DATA_CLASS_NAME, "QC flag", null, 
			DashboardUtils.QUALITY_CATEGORY, DashboardUtils.NO_UNITS);
	public static final Properties ADDN_TYPES_PROPERTIES;
	static {
		ADDN_TYPES_PROPERTIES = new Properties();
		ADDN_TYPES_PROPERTIES.setProperty(QC_FLAG_TYPE.getVarName(), QC_FLAG_TYPE.toPropertyValue());
	}

	static final Character QC_FLAG_VALUE = 'C';
	static final String EXPOCODE = "XXXX20140113";
	static final String CRUISE_NAME = "My Cruise";
	static final String PLATFORM_NAME = "My Vessel";
	static final String ORGANIZATION_NAME = "PMEL/NOAA";
	static final String INVESTIGATOR_NAMES = "Smith, K. : Doe, J.";
	static final String PLATFORM_TYPE = "Battleship";
	static final Double WESTMOST_LONGITUDE = -160.0;
	static final Double EASTMOST_LONGITUDE = -135.0;
	static final Double SOUTHMOST_LATITUDE = 15.0;
	static final Double NORTHMOST_LATITUDE = 50.0;
	static final Date BEGIN_TIME = new Date();
	static final Date END_TIME = new Date(BEGIN_TIME.getTime() + 1000000L);
	static final String VERSION_STATUS = "2.5N";

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getCharVariables()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setCharVariableValue(gov.noaa.pmel.dashboard.server.DashDataType,java.lang.Character)}.
	 */
	@Test
	public void testGetSetCharVariableValue() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		mdata.setCharVariableValue(QC_FLAG_TYPE, QC_FLAG_VALUE);
		TreeMap<DashDataType,Character> charMap = mdata.getCharVariables();
		assertEquals(QC_FLAG_VALUE, charMap.get(QC_FLAG_TYPE));
		mdata.setCharVariableValue(QC_FLAG_TYPE, null);
		charMap = mdata.getCharVariables();
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, charMap.get(QC_FLAG_TYPE));
		boolean errCaught = false;
		try {
			mdata.setCharVariableValue(DashboardServerUtils.DATASET_ID, QC_FLAG_VALUE);
		} catch ( IllegalArgumentException ex ) {
			errCaught = true;
		}
		assertTrue( errCaught );
	}

		/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getStringVariables()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setStringVariableValue(gov.noaa.pmel.dashboard.server.DashDataType,java.lang.String)}.
	 */
	@Test
	public void testGetSetStringVariableValue() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		mdata.setStringVariableValue(DashboardServerUtils.DATASET_ID, EXPOCODE);
		TreeMap<DashDataType,String> stringMap = mdata.getStringVariables();
		assertEquals(EXPOCODE, stringMap.get(DashboardServerUtils.DATASET_ID));
		mdata.setStringVariableValue(DashboardServerUtils.DATASET_ID, null);
		stringMap = mdata.getStringVariables();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, stringMap.get(DashboardServerUtils.DATASET_ID));
		boolean errCaught = false;
		try {
			mdata.setStringVariableValue(DashboardServerUtils.EASTERNMOST_LONGITUDE, EXPOCODE);
		} catch ( IllegalArgumentException ex ) {
			errCaught = true;
		}
		assertTrue( errCaught );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getDoubleVariables()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setDoubleVariableValue(gov.noaa.pmel.dashboard.server.DashDataType,java.lang.Double)}.
	 */
	@Test
	public void testGetSetDoubleVariableValue() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		Double value = Double.valueOf(EASTMOST_LONGITUDE);
		mdata.setDoubleVariableValue(DashboardServerUtils.EASTERNMOST_LONGITUDE, value);
		TreeMap<DashDataType,Double> doubleMap = mdata.getDoubleVariables();
		assertEquals(value, doubleMap.get(DashboardServerUtils.EASTERNMOST_LONGITUDE));
		mdata.setDoubleVariableValue(DashboardServerUtils.EASTERNMOST_LONGITUDE, null);
		doubleMap = mdata.getDoubleVariables();
		assertEquals(DashboardUtils.FP_MISSING_VALUE, doubleMap.get(DashboardServerUtils.EASTERNMOST_LONGITUDE));
		boolean errCaught = false;
		try {
			mdata.setDoubleVariableValue(DashboardServerUtils.DATASET_ID, value);
		} catch ( IllegalArgumentException ex ) {
			errCaught = true;
		}
		assertTrue( errCaught );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getDateVariables()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setDateVariableValue(gov.noaa.pmel.dashboard.server.DashDataType,java.util.Date)}.
	 */
	@Test
	public void testGetSetDateVariableValue() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		mdata.setDateVariableValue(DashboardServerUtils.TIME_COVERAGE_START, BEGIN_TIME);
		TreeMap<DashDataType,Date> dateMap = mdata.getDateVariables();
		assertEquals(BEGIN_TIME, dateMap.get(DashboardServerUtils.TIME_COVERAGE_START));
		mdata.setDateVariableValue(DashboardServerUtils.TIME_COVERAGE_START, null);
		dateMap = mdata.getDateVariables();
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, dateMap.get(DashboardServerUtils.TIME_COVERAGE_START));
		boolean errCaught = false;
		try {
			mdata.setDateVariableValue(DashboardServerUtils.DATASET_ID, BEGIN_TIME);
		} catch ( IllegalArgumentException ex ) {
			errCaught = true;
		}
		assertTrue( errCaught );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getDatasetId()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setDatasetId(java.lang.String)}.
	 */
	@Test
	public void testGetSetExpocode() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setDatasetId(EXPOCODE);
		assertEquals(EXPOCODE, mdata.getDatasetId());
		mdata.setDatasetId(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getDatasetName()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setDatasetName(java.lang.String)}.
	 */
	@Test
	public void testGetSetDatasetName() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		mdata.setDatasetName(CRUISE_NAME);
		assertEquals(CRUISE_NAME, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setDatasetName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getPlatformName()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setPlatformName(java.lang.String)}.
	 */
	@Test
	public void testGetSetPlatformName() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
		mdata.setPlatformName(PLATFORM_NAME);
		assertEquals(PLATFORM_NAME, mdata.getPlatformName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setPlatformName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getOrganizationName()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setOrganizationName(java.lang.String)}.
	 */
	@Test
	public void testGetSetOrganization() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		mdata.setOrganizationName(ORGANIZATION_NAME);
		assertEquals(ORGANIZATION_NAME, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setOrganizationName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getInvestigatorNames()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setInvestigatorNames(java.lang.String)}.
	 */
	@Test
	public void testGetSetInvestigatorNames() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		mdata.setInvestigatorNames(INVESTIGATOR_NAMES);
		assertEquals(INVESTIGATOR_NAMES, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setInvestigatorNames(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getPlatformType()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setPlatformType(java.lang.String)}.
	 */
	@Test
	public void testGetSetPlatformType() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
		mdata.setPlatformType(PLATFORM_TYPE);
		assertEquals(PLATFORM_TYPE, mdata.getPlatformType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setPlatformType(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getWestmostLongitude()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setWestmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetWestmostLongitude() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		mdata.setWestmostLongitude(WESTMOST_LONGITUDE);
		assertTrue( WESTMOST_LONGITUDE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setWestmostLongitude(null);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getEastmostLongitude()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setEastmostLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetEastmostLongitude() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		mdata.setEastmostLongitude(EASTMOST_LONGITUDE);
		assertTrue( EASTMOST_LONGITUDE.equals(mdata.getEastmostLongitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setEastmostLongitude(null);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getSouthmostLatitude()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setSouthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSouthmostLatitude() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		mdata.setSouthmostLatitude(SOUTHMOST_LATITUDE);
		assertTrue( SOUTHMOST_LATITUDE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setSouthmostLatitude(null);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getNorthmostLatitude()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setNorthmostLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetNorthmostLatitude() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		mdata.setNorthmostLatitude(NORTHMOST_LATITUDE);
		assertTrue( NORTHMOST_LATITUDE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setNorthmostLatitude(null);
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getBeginTime()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setBeginTime(java.util.Date)}.
	 */
	@Test
	public void testSetBeginTime() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getBeginTime());
		mdata.setBeginTime(BEGIN_TIME);
		assertEquals(BEGIN_TIME, mdata.getBeginTime());
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setBeginTime(null);
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getBeginTime());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getEndTime()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setEndTime(java.util.Date)}.
	 */
	@Test
	public void testGetSetEndTime() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getEndTime());
		mdata.setEndTime(END_TIME);
		assertEquals(END_TIME, mdata.getEndTime());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setEndTime(null);
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getEndTime());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#getVersion()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#setVersion(java.lang.String)}.
	 */
	@Test
	public void testGetSetVersion() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);
		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVersion());
		mdata.setVersion(VERSION_STATUS);
		assertEquals(VERSION_STATUS, mdata.getVersion());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getEndTime());
		assertEquals(DashboardUtils.DATE_MISSING_VALUE, mdata.getBeginTime());
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getNorthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getSouthmostLatitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getEastmostLongitude()) );
		assertTrue( DashboardUtils.FP_MISSING_VALUE.equals(mdata.getWestmostLongitude()) );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
		mdata.setVersion(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVersion());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DsgMetadata#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.server.DsgMetadata#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
		knownTypes.addTypesFromProperties(ADDN_TYPES_PROPERTIES);

		DsgMetadata mdata = new DsgMetadata(knownTypes);
		assertFalse( mdata.equals(null) );
		assertFalse( mdata.equals(EXPOCODE) );

		DsgMetadata other = new DsgMetadata(knownTypes);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setCharVariableValue(QC_FLAG_TYPE, QC_FLAG_VALUE);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setCharVariableValue(QC_FLAG_TYPE, QC_FLAG_VALUE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setDatasetId(EXPOCODE);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setDatasetId(EXPOCODE);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setDatasetName(CRUISE_NAME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setDatasetName(CRUISE_NAME);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );

		mdata.setPlatformName(PLATFORM_NAME);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setPlatformName(PLATFORM_NAME);
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

		mdata.setPlatformType(PLATFORM_TYPE);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setPlatformType(PLATFORM_TYPE);
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

		mdata.setVersion(VERSION_STATUS);
		assertFalse( mdata.hashCode() == other.hashCode());
		assertFalse( mdata.equals(other) );
		other.setVersion(VERSION_STATUS);
		assertEquals(mdata.hashCode(), other.hashCode());
		assertTrue( mdata.equals(other) );
	}

}
