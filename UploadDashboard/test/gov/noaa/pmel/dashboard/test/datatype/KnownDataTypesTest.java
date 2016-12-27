/**
 * 
 */
package gov.noaa.pmel.dashboard.test.datatype;

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

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Unit tests for {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes}.
 * @author Karl Smith
 */
public class KnownDataTypesTest {

	private static final TreeSet<DashDataType<?>> USERS_TYPES = new TreeSet<DashDataType<?>>(Arrays.asList(
			DashboardServerUtils.UNKNOWN,
			DashboardServerUtils.OTHER,
			DashboardServerUtils.DATASET_NAME,
			DashboardServerUtils.PLATFORM_NAME,
			DashboardServerUtils.PLATFORM_TYPE,
			DashboardServerUtils.ORGANIZATION_NAME,
			DashboardServerUtils.INVESTIGATOR_NAMES,
			DashboardServerUtils.SAMPLE_ID,
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

	private static final TreeSet<DashDataType<?>> METADATA_FILES_TYPES = new TreeSet<DashDataType<?>>(Arrays.asList(
			DashboardServerUtils.DATASET_ID,
			DashboardServerUtils.DATASET_NAME,
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

	private static final TreeSet<DashDataType<?>> DATA_FILES_TYPES = new TreeSet<DashDataType<?>>(Arrays.asList(
			DashboardServerUtils.SAMPLE_NUMBER,
			DashboardServerUtils.TIME,
			DashboardServerUtils.LONGITUDE,
			DashboardServerUtils.LATITUDE,
			DashboardServerUtils.SAMPLE_DEPTH,
			DashboardServerUtils.YEAR,
			DashboardServerUtils.MONTH_OF_YEAR,
			DashboardServerUtils.DAY_OF_MONTH,
			DashboardServerUtils.HOUR_OF_DAY,
			DashboardServerUtils.MINUTE_OF_HOUR,
			DashboardServerUtils.SECOND_OF_MINUTE,
			DashboardServerUtils.WOCE_AUTOCHECK
	));

	private static final HashSet<String> USERS_VARNAMES;
	private static final HashSet<String> METADATA_FILES_VARNAMES;
	private static final HashSet<String> DATA_FILES_VARNAMES;
	static {
		USERS_VARNAMES = new HashSet<String>(USERS_TYPES.size());
		for ( DashDataType<?> dtype : USERS_TYPES )
			USERS_VARNAMES.add(dtype.getVarName());
		METADATA_FILES_VARNAMES = new HashSet<String>(METADATA_FILES_TYPES.size());
		for ( DashDataType<?> dtype : METADATA_FILES_TYPES )
			METADATA_FILES_VARNAMES.add(dtype.getVarName());
		DATA_FILES_VARNAMES = new HashSet<String>(DATA_FILES_TYPES.size());
		for ( DashDataType<?> dtype : DATA_FILES_TYPES )
			DATA_FILES_VARNAMES.add(dtype.getVarName());
	}

	static final String[] ADDN_TYPES_CLASS_NAMES = new String[] { 
			Double.class.getSimpleName(), 
			Integer.class.getSimpleName(), 
			String.class.getSimpleName() };
	static final String[] ADDN_TYPES_VAR_NAMES = new String[] { 
			"xCO2_atm_dry_interp", "rank", "socat_doi" };
	static final Double[] ADDN_TYPES_SORT_ORDERS = new Double[] {
			34.0, 56.0, 78.0
	};
	static final String[] ADDN_TYPES_DISPLAY_NAMES = new String[] {
			"xCO2 atm dry", "ranking", "SOCAT DOI" };
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

	static final DashDataType<?>[] ADDN_DATA_TYPES = new DashDataType<?>[] {
		new DoubleDashDataType(ADDN_TYPES_VAR_NAMES[0], ADDN_TYPES_SORT_ORDERS[0], 
				ADDN_TYPES_DISPLAY_NAMES[0], ADDN_TYPES_DESCRIPTIONS[0], 
				MOL_FRACTION_UNITS, ADDN_TYPES_STANDARD_NAMES[0], 
				ADDN_TYPES_CATEGORY_NAMES[0], null, null, null, null),
		new IntDashDataType(ADDN_TYPES_VAR_NAMES[1], ADDN_TYPES_SORT_ORDERS[1], 
				ADDN_TYPES_DISPLAY_NAMES[1], ADDN_TYPES_DESCRIPTIONS[1], 
				MOL_FRACTION_UNITS, ADDN_TYPES_STANDARD_NAMES[1], 
				ADDN_TYPES_CATEGORY_NAMES[1], null, null, null, null),
		new StringDashDataType(ADDN_TYPES_VAR_NAMES[2], ADDN_TYPES_SORT_ORDERS[2], 
				ADDN_TYPES_DISPLAY_NAMES[2], ADDN_TYPES_DESCRIPTIONS[2], 
				MOL_FRACTION_UNITS, ADDN_TYPES_STANDARD_NAMES[2], 
				ADDN_TYPES_CATEGORY_NAMES[2], null, null, null, null),
	};

	static final String ADDN_TYPES_PROPERTIES_STRING = 
			ADDN_TYPES_VAR_NAMES[0] + " = " + ADDN_DATA_TYPES[0].toPropertyValue() + "\n" +
			ADDN_TYPES_VAR_NAMES[1] + " = " + ADDN_DATA_TYPES[1].toPropertyValue() + "\n" +
			ADDN_TYPES_VAR_NAMES[2] + " = " + ADDN_DATA_TYPES[2].toPropertyValue() + "\n";

	/**
	 * Test method for 
	 * {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#addStandardTypesForUsers()},
	 * {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#isEmpty()}, and
	 * {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#containsTypeName(java.lang.String)}.
	 */
	@Test
	public void testAddStandardTypesForUsers() {
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
	 * Test method for {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#addStandardTypesForMetadataFiles()},
	 * {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#containsTypeName(java.lang.String)}.
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
	 * Test method for {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#addStandardTypesForDataFiles()},
	 * {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#containsTypeName(java.lang.String)}.
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
	 * Test method for {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#getKnownTypesSet()}.
	 */
	@Test
	public void testGetKnownTypesSet() {
		KnownDataTypes types = new KnownDataTypes().addStandardTypesForUsers();
		assertEquals(USERS_TYPES, types.getKnownTypesSet());
		types = new KnownDataTypes().addStandardTypesForMetadataFiles();
		assertEquals(METADATA_FILES_TYPES, types.getKnownTypesSet());
		types = new KnownDataTypes().addStandardTypesForDataFiles();
		assertEquals(DATA_FILES_TYPES, types.getKnownTypesSet());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#getDataType(java.lang.String)}
	 * and {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#getDataType(gov.noaa.pmel.dashboard.shared.DataColumnType)}
	 */
	@Test
	public void testGetDataType() {
		KnownDataTypes types = new KnownDataTypes();
		assertNull( types.getDataType("DATASET_NAME") );
		types.addStandardTypesForUsers();
		assertEquals(DashboardServerUtils.DATASET_NAME, types.getDataType("DATASET_NAME"));
		DataColumnType dctype = new DataColumnType("dataset_name", 5.0, "cruise name", 
				"cruise/dataset name", DashboardUtils.NO_UNITS);
		assertEquals(DashboardServerUtils.DATASET_NAME, types.getDataType(dctype));
		dctype = new DataColumnType("cruise_name", 5.0, "dataset name", 
				"cruise/dataset name", DashboardUtils.NO_UNITS);
		assertEquals(DashboardServerUtils.DATASET_NAME, types.getDataType(dctype));
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#getKnownTypesSet()}.
	 */
	@Test
	public void testGetKnownTypesList() {
		KnownDataTypes types = new KnownDataTypes();
		TreeSet<DashDataType<?>> knownSet = types.getKnownTypesSet();
		assertEquals(0, knownSet.size());
		types.addStandardTypesForMetadataFiles();
		knownSet = types.getKnownTypesSet();
		assertEquals(METADATA_FILES_TYPES, knownSet);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.datatype.KnownDataTypes#addTypesFromProperties(java.util.Properties)}.
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
			assertEquals(ADDN_DATA_TYPES[k], clientTypes.getDataType(ADDN_TYPES_VAR_NAMES[k]));
		}
	}

}
