/**
 * 
 */
package gov.noaa.pmel.dashboard.test.server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import org.junit.Test;

/**
 * Unit tests for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes}.
 * @author Karl Smith
 */
public class KnownDataTypesTest {

	private static final ArrayList<String> USERS_VARNAMES = new ArrayList<String>(Arrays.asList(
			DataColumnType.UNKNOWN.getVarName(),
			DataColumnType.OTHER.getVarName(),
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

	private static final ArrayList<DataColumnType> METADATA_FILES_TYPES_LIST = new ArrayList<DataColumnType>(Arrays.asList(
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
	));

	static final String[] ADDN_TYPES_VAR_NAMES = new String[] { "xCO2_atm_dry_interp", "rank", "socat_doi" };
	static final String[] ADDN_TYPES_CLASS_NAMES = new String[] { "Double", "Integer", "String" };
	static final String[] ADDN_TYPES_DESCRIPTIONS = new String[] { "mole fraction CO2 in sea level air", "personal ranking", "DOI of SOCAT-enhanced datafile" };
	static final String[] ADDN_TYPES_STANDARD_NAMES = new String[] { "xCO2_atm", "", "DOI" };
	static final String[] ADDN_TYPES_CATEGORY_NAMES = new String[] { "CO2", "", "Identifier" };

	static final ArrayList<String> MOL_FRACTION_UNITS = new ArrayList<String>(Arrays.asList("umol/mol", "mmol/mol"));
	static final String MOL_FRACTION_UNITS_JSON_STRING = "[ \"umol/mol\", \"mmol/mol\" ]";

	static final DataColumnType[] ADDN_TYPES_DATA_COLUMNS = new DataColumnType[] {
		new DataColumnType(ADDN_TYPES_VAR_NAMES[0], ADDN_TYPES_CLASS_NAMES[0], ADDN_TYPES_DESCRIPTIONS[0], 
				ADDN_TYPES_STANDARD_NAMES[0], ADDN_TYPES_CATEGORY_NAMES[0], MOL_FRACTION_UNITS),
		new DataColumnType(ADDN_TYPES_VAR_NAMES[1], ADDN_TYPES_CLASS_NAMES[1], ADDN_TYPES_DESCRIPTIONS[1], 
				null, null, null),
		new DataColumnType(ADDN_TYPES_VAR_NAMES[2], ADDN_TYPES_CLASS_NAMES[2], ADDN_TYPES_DESCRIPTIONS[2], 
				ADDN_TYPES_STANDARD_NAMES[2], ADDN_TYPES_CATEGORY_NAMES[2], null)
	};

	static final String ADDN_TYPES_PROPERTIES_STRING = 
			ADDN_TYPES_VAR_NAMES[0] + " = { " +
					"\"dataClassName\": \"" + ADDN_TYPES_CLASS_NAMES[0] + "\", " +
					"\"description\": \""+ ADDN_TYPES_DESCRIPTIONS[0] + "\", " +
					"\"standardName\": \"" + ADDN_TYPES_STANDARD_NAMES[0] + "\", " +
					"\"categoryName\": \"" + ADDN_TYPES_CATEGORY_NAMES[0] + "\", " +
					"\"units\": " + MOL_FRACTION_UNITS_JSON_STRING + " }\n" +
			ADDN_TYPES_VAR_NAMES[1] + " = { " +
					"\"dataClassName\": \"" + ADDN_TYPES_CLASS_NAMES[1] + "\", " +
					"\"description\": \""+ ADDN_TYPES_DESCRIPTIONS[1] + "\" }\n" +
			ADDN_TYPES_VAR_NAMES[2] + " = { " +
					"\"dataClassName\": \"" + ADDN_TYPES_CLASS_NAMES[2] + "\", " +
					"\"description\": \""+ ADDN_TYPES_DESCRIPTIONS[2] + "\", " +
					"\"standardName\": \"" + ADDN_TYPES_STANDARD_NAMES[2] + "\", " +
					"\"categoryName\": \"" + ADDN_TYPES_CATEGORY_NAMES[2] + "\" }\n";
			
	/**
	 * Test method for 
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#addStandardTypesForUsers()},
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#size()}, and
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#containsTypeName(java.lang.String)}.
	 */
	@Test
	public void testAddStandardTypesForClient() {
		KnownDataTypes types = new KnownDataTypes();
		types.addStandardTypesForUsers();
		assertEquals(USERS_VARNAMES.size(), types.size());
		for ( String varName : USERS_VARNAMES )
			assertTrue( types.containsTypeName(varName) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#addStandardTypesForMetadataFiles()},
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#size()}, and
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#containsTypeName(java.lang.String)}.
	 */
	@Test
	public void testAddStandardTypesForMetadataFiles() {
		KnownDataTypes types = new KnownDataTypes();
		types.addStandardTypesForMetadataFiles();
		assertEquals(METADATA_FILES_VARNAMES.size(), types.size());
		for ( String varName : METADATA_FILES_VARNAMES )
			assertTrue( types.containsTypeName(varName) );
		assertFalse( types.containsTypeName(DataColumnType.UNKNOWN.getVarName()) );
		assertFalse( types.containsTypeName(DataColumnType.OTHER.getVarName()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#addStandardTypesForDataFiles()},
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#size()}, and
	 * {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#containsTypeName(java.lang.String)}.
	 */
	@Test
	public void testAddStandardTypesForDataFiles() {
		KnownDataTypes types = new KnownDataTypes();
		types.addStandardTypesForDataFiles();
		assertEquals(DATA_FILES_VARNAMES.size(), types.size());
		for ( String varName : DATA_FILES_VARNAMES )
			assertTrue( types.containsTypeName(varName) );
		assertFalse( types.containsTypeName(DataColumnType.UNKNOWN.getVarName()) );
		assertFalse( types.containsTypeName(DataColumnType.OTHER.getVarName()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.KnownDataTypes#getKnownTypesList()}.
	 */
	@Test
	public void testGetKnownTypesList() {
		KnownDataTypes types = new KnownDataTypes();
		ArrayList<DataColumnType> knownList = types.getKnownTypesList();
		assertEquals(0, knownList.size());
		types.addStandardTypesForMetadataFiles();
		knownList = types.getKnownTypesList();
		assertEquals(METADATA_FILES_TYPES_LIST, knownList);
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
		assertEquals(KnownDataTypes.EXPOCODE, expoType);
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
		clientTypes.addTypesFromProperties(props);
		assertEquals(USERS_VARNAMES.size() + ADDN_TYPES_VAR_NAMES.length, clientTypes.size());
		for (int k = 0; k < ADDN_TYPES_VAR_NAMES.length; k++) {
			assertEquals(ADDN_TYPES_DATA_COLUMNS[k], clientTypes.getDataColumnType(ADDN_TYPES_VAR_NAMES[k]));
		}
	}

}
