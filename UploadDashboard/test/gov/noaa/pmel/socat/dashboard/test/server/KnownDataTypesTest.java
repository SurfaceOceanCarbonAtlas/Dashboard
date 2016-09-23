/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test.server;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import gov.noaa.pmel.socat.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import org.junit.Test;

/**
 * Unit tests for {@link gov.noaa.pmel.socat.dashboard.server.KnownDataTypes}.
 * @author Karl Smith
 */
public class KnownDataTypesTest {

	private static final ArrayList<String> CLIENT_NAMES = new ArrayList<String>(Arrays.asList(
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

	private static final ArrayList<String> SERVER_NAMES = new ArrayList<String>(Arrays.asList(
			DataColumnType.UNKNOWN.getVarName(),
			DataColumnType.OTHER.getVarName(),
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
			KnownDataTypes.QC_FLAG.getVarName(),
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
	
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.KnownDataTypes#containsTypeName(java.lang.String)}.
	 */
	@Test
	public void testContainsTypeName() {
		KnownDataTypes types = new KnownDataTypes();
		assertEquals(2, types.size());
		assertTrue( types.containsTypeName(DataColumnType.UNKNOWN.getVarName().toUpperCase()) );
		assertTrue( types.containsTypeName(DataColumnType.OTHER.getVarName().toLowerCase()) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.KnownDataTypes#addStandardTypesForClient()}.
	 */
	@Test
	public void testAddStandardTypesForClient() {
		KnownDataTypes types = new KnownDataTypes();
		types.addStandardTypesForClient();
		assertEquals(CLIENT_NAMES.size(), types.size());
		for ( String varName : CLIENT_NAMES )
			assertTrue( types.containsTypeName(varName) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.KnownDataTypes#addStandardTypesForServer()}.
	 */
	@Test
	public void testAddStandardTypesForServer() {
		KnownDataTypes types = new KnownDataTypes();
		types.addStandardTypesForServer();
		assertEquals(SERVER_NAMES.size(), types.size());
		for ( String varName : SERVER_NAMES )
			assertTrue( types.containsTypeName(varName) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.KnownDataTypes#getKnownTypesList()}.
	 */
	@Test
	public void testGetKnownTypesList() {
		KnownDataTypes types = new KnownDataTypes();
		ArrayList<DataColumnType> knownList = types.getKnownTypesList();
		ArrayList<DataColumnType> expected = new ArrayList<DataColumnType>(
				Arrays.asList(DataColumnType.UNKNOWN, DataColumnType.OTHER));
		assertEquals(expected, knownList);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.server.KnownDataTypes#getDataColumnType(java.lang.String)}.
	 */
	@Test
	public void testGetDataColumnType() {
		KnownDataTypes types = new KnownDataTypes();
		types.addStandardTypesForClient();
		DataColumnType expoType = types.getDataColumnType("EXPOCODE");
		assertEquals(KnownDataTypes.EXPOCODE, expoType);
	}

}
