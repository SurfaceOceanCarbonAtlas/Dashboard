/**
 * 
 */
package gov.noaa.pmel.dashboard.test.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;

import org.junit.Test;

/**
 * Unit tests for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes}.
 * @author Karl Smith
 */
public class KnownDataTypesTest {

	private static final ArrayList<String> USERS_VARNAMES = new ArrayList<String>(Arrays.asList(
			DashboardUtils.UNKNOWN.getVarName(),
			DashboardUtils.OTHER.getVarName(),
			KnownDataTypes.EXPOCODE.getVarName(),
			KnownDataTypes.DATASET_NAME.getVarName(),
			KnownDataTypes.VESSEL_NAME.getVarName(),
			KnownDataTypes.ORGANIZATION_NAME.getVarName(),
			KnownDataTypes.INVESTIGATOR_NAMES.getVarName(),
			KnownDataTypes.QC_FLAG.getVarName(),
			KnownDataTypes.TIMESTAMP.getVarName(),
			KnownDataTypes.DATE.getVarName(),
			KnownDataTypes.YEAR.getVarName(),
			KnownDataTypes.MONTH_OF_YEAR.getVarName(),
			KnownDataTypes.DAY_OF_MONTH.getVarName(),
			KnownDataTypes.TIME_OF_DAY.getVarName(),
			KnownDataTypes.HOUR_OF_DAY.getVarName(),
			KnownDataTypes.MINUTE_OF_HOUR.getVarName(),
			KnownDataTypes.SECOND_OF_MINUTE.getVarName(),
			KnownDataTypes.DAY_OF_YEAR.getVarName(),
			KnownDataTypes.SECOND_OF_DAY.getVarName(),
			KnownDataTypes.LONGITUDE.getVarName(),
			KnownDataTypes.LATITUDE.getVarName(),
			KnownDataTypes.SAMPLE_DEPTH.getVarName()
	));

	private static final ArrayList<String> METADATA_FILES_VARNAMES = new ArrayList<String>(Arrays.asList(
			KnownDataTypes.EXPOCODE.getVarName(),
			KnownDataTypes.DATASET_NAME.getVarName(),
			KnownDataTypes.VESSEL_NAME.getVarName(),
			KnownDataTypes.ORGANIZATION_NAME.getVarName(),
			KnownDataTypes.INVESTIGATOR_NAMES.getVarName(),
			KnownDataTypes.WESTERNMOST_LONGITUDE.getVarName(),
			KnownDataTypes.EASTERNMOST_LONGITUDE.getVarName(),
			KnownDataTypes.SOUTHERNMOST_LATITUDE.getVarName(),
			KnownDataTypes.NORTHERNMOST_LATITUDE.getVarName(),
			KnownDataTypes.TIME_COVERAGE_START.getVarName(),
			KnownDataTypes.TIME_COVERAGE_END.getVarName(),
			KnownDataTypes.QC_FLAG.getVarName()
	));

	private static final ArrayList<String> DATA_FILES_VARNAMES = new ArrayList<String>(Arrays.asList(
			KnownDataTypes.SAMPLE_NUMBER.getVarName(),
			KnownDataTypes.YEAR.getVarName(),
			KnownDataTypes.MONTH_OF_YEAR.getVarName(),
			KnownDataTypes.DAY_OF_MONTH.getVarName(),
			KnownDataTypes.HOUR_OF_DAY.getVarName(),
			KnownDataTypes.MINUTE_OF_HOUR.getVarName(),
			KnownDataTypes.SECOND_OF_MINUTE.getVarName(),
			KnownDataTypes.TIME.getVarName(),
			KnownDataTypes.LONGITUDE.getVarName(),
			KnownDataTypes.LATITUDE.getVarName(),
			KnownDataTypes.SAMPLE_DEPTH.getVarName()
	));

	private static final LinkedHashSet<DashDataType> METADATA_FILES_TYPES_SET = 
			new LinkedHashSet<DashDataType>( Arrays.asList(
					KnownDataTypes.EXPOCODE,
					KnownDataTypes.DATASET_NAME,
					KnownDataTypes.VESSEL_NAME,
					KnownDataTypes.ORGANIZATION_NAME,
					KnownDataTypes.INVESTIGATOR_NAMES,
					KnownDataTypes.WESTERNMOST_LONGITUDE,
					KnownDataTypes.EASTERNMOST_LONGITUDE,
					KnownDataTypes.SOUTHERNMOST_LATITUDE,
					KnownDataTypes.NORTHERNMOST_LATITUDE,
					KnownDataTypes.TIME_COVERAGE_START,
					KnownDataTypes.TIME_COVERAGE_END,
					KnownDataTypes.QC_FLAG
					) );

	static final String[] ADDN_TYPES_DISPLAY_NAMES = new String[] {
		"xCO2 atm dry", "ranking", "SOCAT DOI" };
	static final String[] ADDN_TYPES_VAR_NAMES = new String[] { 
		"xCO2_atm_dry_interp", "rank", "socat_doi" };
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
		new DashDataType(ADDN_TYPES_DISPLAY_NAMES[0], ADDN_TYPES_VAR_NAMES[0], 
				ADDN_TYPES_CLASS_NAMES[0], ADDN_TYPES_DESCRIPTIONS[0], 
				ADDN_TYPES_STANDARD_NAMES[0], ADDN_TYPES_CATEGORY_NAMES[0], 
				MOL_FRACTION_UNITS),
		new DashDataType(ADDN_TYPES_DISPLAY_NAMES[1], ADDN_TYPES_VAR_NAMES[1], 
				ADDN_TYPES_CLASS_NAMES[1], ADDN_TYPES_DESCRIPTIONS[1], 
				null, null, null),
		new DashDataType(ADDN_TYPES_DISPLAY_NAMES[2], ADDN_TYPES_VAR_NAMES[2], 
				ADDN_TYPES_CLASS_NAMES[2], ADDN_TYPES_DESCRIPTIONS[2], 
				ADDN_TYPES_STANDARD_NAMES[2], ADDN_TYPES_CATEGORY_NAMES[2], null)
	};

	static final String ADDN_TYPES_PROPERTIES_STRING = 
			ADDN_TYPES_VAR_NAMES[0] + " = " + ADDN_DATA_TYPES[0].toPropertyValue() + "\n" +
			ADDN_TYPES_VAR_NAMES[1] + " = " + ADDN_DATA_TYPES[1].toPropertyValue() + "\n" +
			ADDN_TYPES_VAR_NAMES[2] + " = " + ADDN_DATA_TYPES[2].toPropertyValue() + "\n";
			
	/**
	 * Test method for 
	 * {@link gov.noaa.pmel.dashboard.server.DashboardServerUtils#getKeyForName(java.lang.String)}
	 */
	@Test
	public void testGetKeyForName() {
		assertEquals("xco2atmdryinterp", DashboardServerUtils.getKeyForName("xCO2_atm_dry_interp"));
		assertEquals("xco2atmdryinterp", DashboardServerUtils.getKeyForName("xCO2 Atm Dry Interp"));
		assertEquals("xco2atmdryinterp", DashboardServerUtils.getKeyForName("xCO2; Atm; Dry; Interp"));
		assertEquals("xco2atmdryinterp", DashboardServerUtils.getKeyForName("xCO2, Atm, Dry, Interp"));
		assertEquals("other", DashboardServerUtils.getKeyForName("(other)"));
	}

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
		KnownDataTypes types = new KnownDataTypes().addStandardTypesForMetadataFiles();
		assertEquals(METADATA_FILES_TYPES_SET, types.getKnownTypesSet());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#getDataColumnType(java.lang.String)}.
	 */
	@Test
	public void testGetDataColumnType() {
		KnownDataTypes types = new KnownDataTypes();
		DataColumnType expoType = types.getDataColumnType("EXPOCODE");
		assertNull( expoType );
		types.addStandardTypesForUsers();
		expoType = types.getDataColumnType("EXPOCODE");
		DashDataType other = new DashDataType(expoType);
		assertEquals(KnownDataTypes.EXPOCODE, other);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#getKnownTypesSet()}.
	 */
	@Test
	public void testGetKnownTypesList() {
		KnownDataTypes types = new KnownDataTypes();
		LinkedHashSet<DashDataType> knownSet = types.getKnownTypesSet();
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
