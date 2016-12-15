/**
 * 
 */
package gov.noaa.pmel.dashboard.test.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.TreeSet;

import org.junit.Test;

import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Unit tests for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes}.
 * @author Karl Smith
 */
public class KnownDataTypesTest {

	private static final ArrayList<String> USERS_VARNAMES = new ArrayList<String>(Arrays.asList(
			DashboardServerUtils.UNKNOWN.getVarName(),
			DashboardServerUtils.OTHER.getVarName(),
			DashboardServerUtils.DATASET_ID.getVarName(),
			DashboardServerUtils.PLATFORM_NAME.getVarName(),
			DashboardServerUtils.PLATFORM_TYPE.getVarName(),
			DashboardServerUtils.ORGANIZATION_NAME.getVarName(),
			DashboardServerUtils.INVESTIGATOR_NAMES.getVarName(),
			DashboardServerUtils.DATASET_NAME.getVarName(),
			DashboardServerUtils.LONGITUDE.getVarName(),
			DashboardServerUtils.LATITUDE.getVarName(),
			DashboardServerUtils.SAMPLE_DEPTH.getVarName(),
			DashboardServerUtils.TIMESTAMP.getVarName(),
			DashboardServerUtils.DATE.getVarName(),
			DashboardServerUtils.YEAR.getVarName(),
			DashboardServerUtils.MONTH_OF_YEAR.getVarName(),
			DashboardServerUtils.DAY_OF_MONTH.getVarName(),
			DashboardServerUtils.TIME_OF_DAY.getVarName(),
			DashboardServerUtils.HOUR_OF_DAY.getVarName(),
			DashboardServerUtils.MINUTE_OF_HOUR.getVarName(),
			DashboardServerUtils.SECOND_OF_MINUTE.getVarName(),
			DashboardServerUtils.DAY_OF_YEAR.getVarName(),
			DashboardServerUtils.SECOND_OF_DAY.getVarName()
	));

	private static final ArrayList<String> METADATA_FILES_VARNAMES = new ArrayList<String>(Arrays.asList(
			DashboardServerUtils.DATASET_ID.getVarName(),
			DashboardServerUtils.PLATFORM_NAME.getVarName(),
			DashboardServerUtils.PLATFORM_TYPE.getVarName(),
			DashboardServerUtils.ORGANIZATION_NAME.getVarName(),
			DashboardServerUtils.INVESTIGATOR_NAMES.getVarName(),
			DashboardServerUtils.WESTERNMOST_LONGITUDE.getVarName(),
			DashboardServerUtils.EASTERNMOST_LONGITUDE.getVarName(),
			DashboardServerUtils.SOUTHERNMOST_LATITUDE.getVarName(),
			DashboardServerUtils.NORTHERNMOST_LATITUDE.getVarName(),
			DashboardServerUtils.TIME_COVERAGE_START.getVarName(),
			DashboardServerUtils.TIME_COVERAGE_END.getVarName(),
			DashboardServerUtils.STATUS.getVarName(),
			DashboardServerUtils.VERSION.getVarName()
	));

	private static final ArrayList<String> DATA_FILES_VARNAMES = new ArrayList<String>(Arrays.asList(
			DashboardServerUtils.SAMPLE_NUMBER.getVarName(),
			DashboardServerUtils.LONGITUDE.getVarName(),
			DashboardServerUtils.LATITUDE.getVarName(),
			DashboardServerUtils.SAMPLE_DEPTH.getVarName(),
			DashboardServerUtils.YEAR.getVarName(),
			DashboardServerUtils.MONTH_OF_YEAR.getVarName(),
			DashboardServerUtils.DAY_OF_MONTH.getVarName(),
			DashboardServerUtils.HOUR_OF_DAY.getVarName(),
			DashboardServerUtils.MINUTE_OF_HOUR.getVarName(),
			DashboardServerUtils.SECOND_OF_MINUTE.getVarName(),
			DashboardServerUtils.TIME.getVarName(),
			DashboardServerUtils.WOCE_AUTOCHECK.getVarName()
	));

	private static final TreeSet<DashDataType> USERS_TYPES_SET = new TreeSet<DashDataType>(Arrays.asList(
			DashboardServerUtils.UNKNOWN,
			DashboardServerUtils.OTHER,
			DashboardServerUtils.DATASET_ID,
			DashboardServerUtils.PLATFORM_NAME,
			DashboardServerUtils.PLATFORM_TYPE,
			DashboardServerUtils.ORGANIZATION_NAME,
			DashboardServerUtils.INVESTIGATOR_NAMES,
			DashboardServerUtils.DATASET_NAME,
			DashboardServerUtils.LONGITUDE,
			DashboardServerUtils.LATITUDE,
			DashboardServerUtils.SAMPLE_DEPTH,
			DashboardServerUtils.TIMESTAMP,
			DashboardServerUtils.DATE,
			DashboardServerUtils.YEAR,
			DashboardServerUtils.MONTH_OF_YEAR,
			DashboardServerUtils.DAY_OF_MONTH,
			DashboardServerUtils.TIME_OF_DAY,
			DashboardServerUtils.HOUR_OF_DAY,
			DashboardServerUtils.MINUTE_OF_HOUR,
			DashboardServerUtils.SECOND_OF_MINUTE,
			DashboardServerUtils.DAY_OF_YEAR,
			DashboardServerUtils.SECOND_OF_DAY
	));

	private static final TreeSet<DashDataType> METADATA_FILES_TYPES_SET = new TreeSet<DashDataType>(Arrays.asList(
			DashboardServerUtils.DATASET_ID,
			DashboardServerUtils.PLATFORM_NAME,
			DashboardServerUtils.PLATFORM_TYPE,
			DashboardServerUtils.ORGANIZATION_NAME,
			DashboardServerUtils.INVESTIGATOR_NAMES,
			DashboardServerUtils.WESTERNMOST_LONGITUDE,
			DashboardServerUtils.EASTERNMOST_LONGITUDE,
			DashboardServerUtils.SOUTHERNMOST_LATITUDE,
			DashboardServerUtils.NORTHERNMOST_LATITUDE,
			DashboardServerUtils.TIME_COVERAGE_START,
			DashboardServerUtils.TIME_COVERAGE_END,
			DashboardServerUtils.STATUS,
			DashboardServerUtils.VERSION
	));

	private static final TreeSet<DashDataType> DATA_FILES_TYPES_SET = new TreeSet<DashDataType>(Arrays.asList(
			DashboardServerUtils.SAMPLE_NUMBER,
			DashboardServerUtils.LONGITUDE,
			DashboardServerUtils.LATITUDE,
			DashboardServerUtils.SAMPLE_DEPTH,
			DashboardServerUtils.YEAR,
			DashboardServerUtils.MONTH_OF_YEAR,
			DashboardServerUtils.DAY_OF_MONTH,
			DashboardServerUtils.HOUR_OF_DAY,
			DashboardServerUtils.MINUTE_OF_HOUR,
			DashboardServerUtils.SECOND_OF_MINUTE,
			DashboardServerUtils.TIME,
			DashboardServerUtils.WOCE_AUTOCHECK
	));


	static final String[] ADDN_TYPES_VAR_NAMES = new String[] { 
		"xCO2_atm_dry_interp", "rank", "socat_doi" };
	static final Double[] ADDN_TYPES_SORT_ORDERS = new Double[] {
		34.0, 56.0, 78.0
	};
	static final String[] ADDN_TYPES_DISPLAY_NAMES = new String[] {
		"xCO2 atm dry", "ranking", "SOCAT DOI" };
	static final String[] ADDN_TYPES_CLASS_NAMES = new String[] { 
		DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
		DashboardUtils.INT_DATA_CLASS_NAME, 
		DashboardUtils.STRING_DATA_CLASS_NAME };
	static final String[] ADDN_TYPES_DESCRIPTIONS = new String[] { 
		"mole fraction CO2 in sea level air", 
		"personal ranking", 
		"DOI of SOCAT-enhanced datafile" };
	static final String[] ADDN_TYPES_STANDARD_NAMES = new String[] { 
		"xCO2_atm", "", "DOI" };
	static final String[] ADDN_TYPES_CATEGORY_NAMES = new String[] { 
		"CO2", "", "Identifier" };

	static final ArrayList<String> MOL_FRACTION_UNITS = 
			new ArrayList<String>(Arrays.asList("umol/mol", "mmol/mol"));

	static final DashDataType[] ADDN_DATA_TYPES = new DashDataType[] {
		new DashDataType(ADDN_TYPES_VAR_NAMES[0], ADDN_TYPES_SORT_ORDERS[0], 
				ADDN_TYPES_DISPLAY_NAMES[0], ADDN_TYPES_CLASS_NAMES[0], 
				ADDN_TYPES_DESCRIPTIONS[0], ADDN_TYPES_STANDARD_NAMES[0], 
				ADDN_TYPES_CATEGORY_NAMES[0], MOL_FRACTION_UNITS),
		new DashDataType(ADDN_TYPES_VAR_NAMES[1], ADDN_TYPES_SORT_ORDERS[0], 
				ADDN_TYPES_DISPLAY_NAMES[1], ADDN_TYPES_CLASS_NAMES[1], 
				ADDN_TYPES_DESCRIPTIONS[1], null, null, null),
		new DashDataType(ADDN_TYPES_VAR_NAMES[2], ADDN_TYPES_SORT_ORDERS[0], 
				ADDN_TYPES_DISPLAY_NAMES[2], ADDN_TYPES_CLASS_NAMES[2], 
				ADDN_TYPES_DESCRIPTIONS[2], ADDN_TYPES_STANDARD_NAMES[2], 
				ADDN_TYPES_CATEGORY_NAMES[2], null)
	};

	static final String ADDN_TYPES_PROPERTIES_STRING = 
			ADDN_TYPES_VAR_NAMES[0] + " = " + ADDN_DATA_TYPES[0].toPropertyValue() + "\n" +
			ADDN_TYPES_VAR_NAMES[1] + " = " + ADDN_DATA_TYPES[1].toPropertyValue() + "\n" +
			ADDN_TYPES_VAR_NAMES[2] + " = " + ADDN_DATA_TYPES[2].toPropertyValue() + "\n";
			
	/**
	 * Test method for 
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#addStandardTypesForUsers()},
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#isEmpty()}, and
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#containsTypeName(java.lang.String)}.
	 */
	@Test
	public void testAddStandardTypesForClient() {
		KnownDataTypes types = new KnownDataTypes();
		assertTrue( types.isEmpty() );
		KnownDataTypes other = types.addStandardTypesForUsers();
		assertFalse( types.isEmpty() );
		assertTrue( types == other );
		assertEquals(USERS_VARNAMES.size(), types.getKnownTypesSet().size());
		for ( String varName : USERS_VARNAMES )
			assertTrue( types.containsTypeName(varName) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#addStandardTypesForMetadataFiles()},
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#containsTypeName(java.lang.String)}.
	 */
	@Test
	public void testAddStandardTypesForMetadataFiles() {
		KnownDataTypes types = new KnownDataTypes();
		KnownDataTypes other = types.addStandardTypesForMetadataFiles();
		assertTrue( types == other );
		for ( String varName : METADATA_FILES_VARNAMES )
			assertTrue( types.containsTypeName(varName) );
		assertFalse( types.containsTypeName(DashboardUtils.UNKNOWN.getVarName()) );
		assertFalse( types.containsTypeName(DashboardUtils.OTHER.getVarName()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#addStandardTypesForDataFiles()},
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#containsTypeName(java.lang.String)}.
	 */
	@Test
	public void testAddStandardTypesForDataFiles() {
		KnownDataTypes types = new KnownDataTypes();
		KnownDataTypes other = types.addStandardTypesForDataFiles();
		assertTrue( types == other );
		for ( String varName : DATA_FILES_VARNAMES )
			assertTrue( types.containsTypeName(varName) );
		assertFalse( types.containsTypeName(DashboardUtils.UNKNOWN.getVarName()) );
		assertFalse( types.containsTypeName(DashboardUtils.OTHER.getVarName()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#getKnownTypesSet()}.
	 */
	@Test
	public void testGetKnownTypesSet() {
		KnownDataTypes types = new KnownDataTypes().addStandardTypesForUsers();
		assertEquals(USERS_TYPES_SET, types.getKnownTypesSet());
		types = new KnownDataTypes().addStandardTypesForMetadataFiles();
		assertEquals(METADATA_FILES_TYPES_SET, types.getKnownTypesSet());
		types = new KnownDataTypes().addStandardTypesForDataFiles();
		assertEquals(DATA_FILES_TYPES_SET, types.getKnownTypesSet());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#getDataColumnType(java.lang.String)}.
	 */
	@Test
	public void testGetDataColumnType() {
		KnownDataTypes types = new KnownDataTypes();
		DataColumnType expoType = types.getDataColumnType("DATASET_ID");
		assertNull( expoType );
		types.addStandardTypesForUsers();
		expoType = types.getDataColumnType("DATASET_ID");
		DashDataType other = new DashDataType(expoType);
		assertEquals(DashboardServerUtils.DATASET_ID, other);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#getKnownTypesSet()}.
	 */
	@Test
	public void testGetKnownTypesList() {
		KnownDataTypes types = new KnownDataTypes();
		TreeSet<DashDataType> knownSet = types.getKnownTypesSet();
		assertEquals(0, knownSet.size());
		types.addStandardTypesForMetadataFiles();
		knownSet = types.getKnownTypesSet();
		assertEquals(METADATA_FILES_TYPES_SET, knownSet);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#addTypesFromProperties(java.util.Properties)}.
	 */
	@Test
	public void testAddTypesFromProperties() throws IOException {
		KnownDataTypes clientTypes = new KnownDataTypes();
		clientTypes.addStandardTypesForUsers();
		StringReader reader = new StringReader(ADDN_TYPES_PROPERTIES_STRING);
		Properties props = new Properties();
		props.load(reader);
		assertEquals(new HashSet<String>(Arrays.asList(ADDN_TYPES_VAR_NAMES)), props.keySet());
		KnownDataTypes other = clientTypes.addTypesFromProperties(props);
		assertTrue( clientTypes == other );
		assertEquals(USERS_VARNAMES.size() + ADDN_TYPES_VAR_NAMES.length, clientTypes.getKnownTypesSet().size());
		for (int k = 0; k < ADDN_TYPES_VAR_NAMES.length; k++) {
			assertEquals(ADDN_DATA_TYPES[k].duplicate(), clientTypes.getDataColumnType(ADDN_TYPES_VAR_NAMES[k]));
		}
	}

}
