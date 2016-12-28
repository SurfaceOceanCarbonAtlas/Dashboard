/**
 * 
 */
package gov.noaa.pmel.dashboard.test.dsg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.TreeMap;

import org.junit.Test;

import gov.noaa.pmel.dashboard.datatype.CharDashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.dsg.DsgData;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Unit test for methods in gov.noaa.pmel.dashboard.server.DsgData
 *  
 * @author Karl Smith
 */
public class DsgDataTest {

	public static final CharDashDataType WOCE_FCO2_WATER = new CharDashDataType("woce_fco2_water", 
			401.0, "WOCE fCO2 water", "WOCE flag for aqueous fCO2", DashboardUtils.NO_UNITS, "WOCE_flag",
			DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null);
	
	static final KnownDataTypes KNOWN_DATA_TYPES;
	static {
		KNOWN_DATA_TYPES = new KnownDataTypes();
		KNOWN_DATA_TYPES.addStandardTypesForDataFiles();
		Properties addnTypeProps = new Properties();
		addnTypeProps.setProperty(DsgNcFileTest.SST.getVarName(), 
				DsgNcFileTest.SST.toPropertyValue());
		addnTypeProps.setProperty(DsgNcFileTest.SALINITY.getVarName(), 
				DsgNcFileTest.SALINITY.toPropertyValue());
		addnTypeProps.setProperty(DsgNcFileTest.XCO2_WATER_SST_DRY.getVarName(), 
				DsgNcFileTest.XCO2_WATER_SST_DRY.toPropertyValue());
		addnTypeProps.setProperty(DsgNcFileTest.PCO2_WATER_TEQU_WET.getVarName(), 
				DsgNcFileTest.PCO2_WATER_TEQU_WET.toPropertyValue());
		addnTypeProps.setProperty(DsgNcFileTest.PATM.getVarName(), 
				DsgNcFileTest.PATM.toPropertyValue());
		addnTypeProps.setProperty(DsgNcFileTest.SHIP_SPEED.getVarName(), 
				DsgNcFileTest.SHIP_SPEED.toPropertyValue());
		addnTypeProps.setProperty(WOCE_FCO2_WATER.getVarName(), 
				WOCE_FCO2_WATER.toPropertyValue());
		KNOWN_DATA_TYPES.addTypesFromProperties(addnTypeProps);
	}

	static final ArrayList<DataColumnType> TEST_USER_TYPES = new ArrayList<DataColumnType>(Arrays.asList(
			DashboardServerUtils.DATASET_ID.duplicate(),
			DashboardServerUtils.DATASET_NAME.duplicate(),
			DashboardServerUtils.MONTH_OF_YEAR.duplicate(), 
			DashboardServerUtils.DAY_OF_MONTH.duplicate(), 
			DashboardServerUtils.YEAR.duplicate(), 
			DashboardServerUtils.HOUR_OF_DAY.duplicate(), 
			DashboardServerUtils.MINUTE_OF_HOUR.duplicate(), 
			DashboardServerUtils.LATITUDE.duplicate(), 
			DashboardServerUtils.LONGITUDE.duplicate(), 
			DsgNcFileTest.SST.duplicate(),
			DsgNcFileTest.SALINITY.duplicate(),
			DsgNcFileTest.XCO2_WATER_SST_DRY.duplicate(),
			DsgNcFileTest.PCO2_WATER_TEQU_WET.duplicate(),
			DsgNcFileTest.PATM.duplicate(),
			DsgNcFileTest.SHIP_SPEED.duplicate()));
	static final ArrayList<ArrayList<String>> TEST_VALUES = new ArrayList<ArrayList<String>>();
	static final ArrayList<Integer> EXPECTED_YEARS = new ArrayList<Integer>();
	static final ArrayList<Integer> EXPECTED_MONTHS = new ArrayList<Integer>();
	static final ArrayList<Integer> EXPECTED_DAYS = new ArrayList<Integer>();
	static final ArrayList<Integer> EXPECTED_HOURS = new ArrayList<Integer>();
	static final ArrayList<Integer> EXPECTED_MINUTES = new ArrayList<Integer>();
	static final ArrayList<Double> EXPECTED_LATITUDES = new ArrayList<Double>();
	static final ArrayList<Double> EXPECTED_LONGITUDES = new ArrayList<Double>();
	static final ArrayList<Double> EXPECTED_SSTS = new ArrayList<Double>();
	static final ArrayList<Double> EXPECTED_SALS = new ArrayList<Double>();
	static final ArrayList<Double> EXPECTED_XCO2WATER_SSTS = new ArrayList<Double>();
	static final ArrayList<Double> EXPECTED_PCO2WATER_TEQUS = new ArrayList<Double>();
	static final ArrayList<Double> EXPECTED_SLPS = new ArrayList<Double>();

	static {
		// No seconds, and pressure is in kPa instead of hPa, but shouldn't matter for these tests
		String[] dataValueStrings = {
			"31B520060606,GM0606,6,10,2006,23,48,29.0514,-92.759,28.78,33.68,409.7,392.5,100.9281,0.3", 
			"31B520060606,GM0606,6,10,2006,23,49,29.0513,-92.759,28.9,33.56,405.5,388.3,100.9298,0.3", 
			"31B520060606,GM0606,6,10,2006,23,50,29.0518,-92.7591,28.94,33.48,402.1,385.1,100.9314,2", 
			"31B520060606,GM0606,6,10,2006,23,51,29.0517,-92.7592,28.99,33.44,399.7,382.7,100.9302,0.3", 
			"31B520060606,GM0606,6,10,2006,23,52,29.0516,-92.7592,28.9,33.39,397.9,381,100.929,0.3", 
			"31B520060606,GM0606,6,10,2006,23,53,29.0516,-92.7593,28.93,33.38,397.1,380.3,100.9283,0.3", 
			"31B520060606,GM0606,6,10,2006,23,54,29.0515,-92.7593,28.96,33.38,395.8,379,100.9272,0.3", 
			"31B520060606,GM0606,6,10,2006,23,55,29.051,-92.76,28.88,33.38,395.7,378.9,100.9264,3", 
			"31B520060606,GM0606,6,10,2006,23,56,29.0502,-92.7597,29.08,33.4,395.3,378.3,100.9264,3.1", 
			"31B520060606,GM0606,6,10,2006,23,57,29.0494,-92.7593,29.35,33.3,392.1,375.1,100.9255,3.1", 
			"31B520060606,GM0606,6,10,2006,23,58,29.0486,-92.759,29.34,33.28,391,374,100.9246,3.1", 
			"31B520060606,GM0606,6,10,2006,23,59,29.0478,-92.7587,29.29,33.28,390.5,373.6,100.9223,3.1", 
			"31B520060606,GM0606,6,11,2006,0,00,29.0478,-92.7538,29.29,33.32,390.9,374,100.923,17.6", 
			"31B520060606,GM0606,6,11,2006,0,01,29.0492,-92.7522,29.35,33.41,390.3,373.3,100.9255,7.8", 
			"31B520060606,GM0606,6,11,2006,0,02,29.0506,-92.7505,29.39,33.47,393,375.9,100.9266,7.8", 
			"31B520060606,GM0606,6,11,2006,0,03,29.052,-92.7489,29.43,33.55,395.7,378.4,100.928,7.8", 
			"31B520060606,GM0606,6,11,2006,0,04,29.0534,-92.7472,29.73,33.64,399.7,382,100.93,7.8", 
			"31B520060606,GM0606,6,11,2006,0,05,29.0577,-92.7492,29.84,33.64,402.9,385,100.9302,16.9", 
			"31B520060606,GM0606,6,11,2006,0,06,29.0587,-92.7512,29.67,33.55,406.9,388.9,100.9305,8.2", 
			"31B520060606,GM0606,6,11,2006,0,07,29.0597,-92.7533,29.66,33.52,408.1,390.2,100.9308,8.2", 
			"31B520060606,GM0606,6,11,2006,0,08,29.0608,-92.7553,29.82,33.42,408.1,390,100.9306,8.2", 
			"31B520060606,GM0606,6,11,2006,0,09,29.0618,-92.7574,29.81,33.31,408.2,390,100.931,8.2", 
			"31B520060606,GM0606,6,11,2006,0,10,29.0648,-92.7623,29.82,33.22,405.9,387.9,100.9304,20.8", 
			"31B520060606,GM0606,6,11,2006,0,11,29.0641,-92.7641,29.9,33.14,404,386,100.926,7.1", 
			"31B520060606,GM0606,6,11,2006,0,12,29.0634,-92.766,29.89,32.97,402.9,384.9,100.9237,7.1",
		};
		for ( String valsString : dataValueStrings ) {
			ArrayList<String> dataVals = new ArrayList<String>(Arrays.asList(valsString.split(",",-1)));
			TEST_VALUES.add(dataVals);
			EXPECTED_YEARS.add(Integer.valueOf(dataVals.get(4)));
			EXPECTED_MONTHS.add(Integer.valueOf(dataVals.get(2)));
			EXPECTED_DAYS.add(Integer.valueOf(dataVals.get(3)));
			EXPECTED_HOURS.add(Integer.valueOf(dataVals.get(5)));
			EXPECTED_MINUTES.add(Integer.valueOf(dataVals.get(6)));
			EXPECTED_LATITUDES.add(Double.valueOf(dataVals.get(7)));
			EXPECTED_LONGITUDES.add(Double.valueOf(dataVals.get(8)));
			EXPECTED_SSTS.add(Double.valueOf(dataVals.get(9)));
			EXPECTED_SALS.add(Double.valueOf(dataVals.get(10)));
			EXPECTED_XCO2WATER_SSTS.add(Double.valueOf(dataVals.get(11)));
			EXPECTED_PCO2WATER_TEQUS.add(Double.valueOf(dataVals.get(12)));
			EXPECTED_SLPS.add(Double.valueOf(dataVals.get(13)));
		}
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#DsgData(gov.noaa.pmel.dashboard.datatype.KnownDataTypes, 
	 * java.util.List, java.util.List)}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#dataListFromDashboardCruise(gov.noaa.pmel.dashboard.datatype.KnownDataTypes, 
	 * gov.noaa.pmel.dashboard.shared.DashboardDatasetData)}.
	 */
	@Test
	public void testSocatCruiseDataList() {
		DashboardDatasetData cruise = new DashboardDatasetData();
		cruise.setDataColTypes(TEST_USER_TYPES);
		cruise.setDataValues(TEST_VALUES);
		ArrayList<Integer> rowNums = new ArrayList<Integer>(TEST_USER_TYPES.size());
		for (int k = 1; k <= TEST_USER_TYPES.size(); k++)
			rowNums.add(k);
		cruise.setRowNums(rowNums);
		ArrayList<DsgData> dataList = DsgData.dataListFromDashboardCruise(KNOWN_DATA_TYPES, cruise);
		for (int k = 0; k < dataList.size(); k++) {
			rowNums.add(k+1);
			DsgData dataRow = dataList.get(k);
			TreeMap<DoubleDashDataType,Double> doubleValues = dataRow.getDoubleVariables();
			assertEquals(EXPECTED_YEARS.get(k), dataRow.getYear());
			assertEquals(EXPECTED_MONTHS.get(k), dataRow.getMonth());
			assertEquals(EXPECTED_DAYS.get(k), dataRow.getDay());
			assertEquals(EXPECTED_HOURS.get(k), dataRow.getHour());
			assertEquals(EXPECTED_MINUTES.get(k), dataRow.getMinute());
			assertEquals(EXPECTED_LATITUDES.get(k), dataRow.getLatitude());
			assertEquals(EXPECTED_LONGITUDES.get(k), dataRow.getLongitude());
			assertEquals(EXPECTED_SSTS.get(k), doubleValues.get(DsgNcFileTest.SST));
			assertEquals(EXPECTED_SALS.get(k), doubleValues.get(DsgNcFileTest.SALINITY));
			assertEquals(EXPECTED_XCO2WATER_SSTS.get(k), doubleValues.get(DsgNcFileTest.XCO2_WATER_SST_DRY));
			assertEquals(EXPECTED_PCO2WATER_TEQUS.get(k), doubleValues.get(DsgNcFileTest.PCO2_WATER_TEQU_WET));
			assertEquals(EXPECTED_SLPS.get(k), doubleValues.get(DsgNcFileTest.PATM));
			assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getSecond());
			assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getSampleDepth());
		}
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getIntegerVariables()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setIntegerVariableValue(gov.noaa.pmel.dashboard.datatype.DashDataType,java.lang.Integer)}.
	 */
	@Test
	public void testGetSetIntegerVariableValue() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		Integer value = 123;
		data.setIntegerVariableValue(DashboardServerUtils.SAMPLE_NUMBER, value);
		TreeMap<IntDashDataType,Integer> intMap = data.getIntegerVariables();
		assertEquals(value, intMap.get(DashboardServerUtils.SAMPLE_NUMBER));
		data.setIntegerVariableValue(DashboardServerUtils.SAMPLE_NUMBER, null);
		intMap = data.getIntegerVariables();
		assertEquals(DashboardUtils.INT_MISSING_VALUE, intMap.get(DashboardServerUtils.SAMPLE_NUMBER));
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getCharacterVariables()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setCharacterVariableValue(gov.noaa.pmel.dashboard.datatype.DashDataType,java.lang.Character)}.
	 */
	@Test
	public void testGetSetCharacterVariableValue() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		Character value = 'K';
		data.setCharacterVariableValue(WOCE_FCO2_WATER, value);
		TreeMap<CharDashDataType,Character> charMap = data.getCharacterVariables();
		assertEquals(value, charMap.get(WOCE_FCO2_WATER));
		data.setCharacterVariableValue(WOCE_FCO2_WATER, null);
		charMap = data.getCharacterVariables();
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, charMap.get(WOCE_FCO2_WATER));
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getDoubleVariables()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setDoubleVariableValue(gov.noaa.pmel.dashboard.datatype.DashDataType,java.lang.Double)}.
	 */
	@Test
	public void testGetSetDoubleVariableValue() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		Double value = (new Date()).getTime() / 1000.0;
		data.setDoubleVariableValue(DashboardServerUtils.TIME, value);
		TreeMap<DoubleDashDataType,Double> doubleMap = data.getDoubleVariables();
		assertEquals(value, doubleMap.get(DashboardServerUtils.TIME));
		data.setDoubleVariableValue(DashboardServerUtils.TIME, null);
		doubleMap = data.getDoubleVariables();
		assertEquals(DashboardUtils.FP_MISSING_VALUE, doubleMap.get(DashboardServerUtils.TIME));
	}

	static final Integer SAMPLE_NUMBER = 123;
	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getSampleNumber()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setSampleNumber(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetSampleNumber() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
		data.setSampleNumber(SAMPLE_NUMBER);
		assertEquals(SAMPLE_NUMBER, data.getSampleNumber());
		data.setSampleNumber(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
	}

	static final Integer YEAR = 2014;
	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getYear()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setYear(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetYear() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
		data.setYear(YEAR);
		assertEquals(YEAR, data.getYear());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
		data.setYear(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
	}

	static final Integer MONTH = 1;
	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getMonth()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setMonth(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetMonth() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
		data.setMonth(MONTH);
		assertEquals(MONTH, data.getMonth());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
		data.setMonth(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
	}

	static final Integer DAY = 13;
	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getDay()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setDay(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetDay() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
		data.setDay(DAY);
		assertEquals(DAY, data.getDay());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
		data.setDay(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
	}

	static final Integer HOUR = 19;
	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getHour()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setHour(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetHour() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
		data.setHour(HOUR);
		assertEquals(HOUR, data.getHour());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
		data.setHour(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
	}

	static final Integer MINUTE = 35;
	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getMinute()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setMinute(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetMinute() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
		data.setMinute(MINUTE);
		assertEquals(MINUTE, data.getMinute());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
		data.setMinute(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
	}

	static final Double SECOND = 18.0;
	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getSecond()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setSecond(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSecond() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
		data.setSecond(SECOND);
		assertEquals(SECOND, data.getSecond());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
		data.setSecond(null);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
	}

	static final Double LONGITUDE = -125.0;
	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getLongitude()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLongitude() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
		data.setLongitude(LONGITUDE);
		assertEquals(LONGITUDE, data.getLongitude());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
		data.setLongitude(null);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
	}

	static final Double LATITUDE = 46.5;
	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getLatitude()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLatitude() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
		data.setLatitude(LATITUDE);
		assertEquals(LATITUDE, data.getLatitude());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
		data.setLatitude(null);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
	}

	static final Double SAMPLE_DEPTH = 5.0;
	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#getSampleDepth()}
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#setSampleDepth(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSampleDepth() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
		data.setSampleDepth(SAMPLE_DEPTH);
		assertEquals(SAMPLE_DEPTH, data.getSampleDepth());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
		data.setSampleDepth(null);
		assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.dsg.DsgData#hashCode()} 
	 * and {@link gov.noaa.pmel.dashboard.dsg.DsgData#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		DsgData data = new DsgData(KNOWN_DATA_TYPES);
		assertFalse( data.equals(null) );
		assertFalse( data.equals(YEAR) );

		DsgData other = new DsgData(KNOWN_DATA_TYPES);
		assertTrue( data.hashCode() == other.hashCode() );
		assertTrue( data.equals(other) );

		data.setSampleNumber(SAMPLE_NUMBER);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setSampleNumber(SAMPLE_NUMBER);
		assertTrue( data.hashCode() == other.hashCode() );
		assertTrue( data.equals(other) );

		data.setYear(YEAR);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setYear(YEAR);
		assertTrue( data.hashCode() == other.hashCode() );
		assertTrue( data.equals(other) );

		data.setMonth(MONTH);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setMonth(MONTH);
		assertTrue( data.hashCode() == other.hashCode() );
		assertTrue( data.equals(other) );

		data.setDay(DAY);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setDay(DAY);
		assertTrue( data.hashCode() == other.hashCode() );
		assertTrue( data.equals(other) );

		data.setHour(HOUR);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setHour(HOUR);
		assertTrue( data.hashCode() == other.hashCode() );
		assertTrue( data.equals(other) );

		data.setMinute(MINUTE);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setMinute(MINUTE);
		assertTrue( data.hashCode() == other.hashCode() );
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setSecond(SECOND);
		assertTrue( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setSecond(SECOND);
		assertTrue( data.hashCode() == other.hashCode() );
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setLongitude(LONGITUDE);
		assertTrue( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setLongitude(LONGITUDE);
		assertTrue( data.hashCode() == other.hashCode() );
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setLatitude(LATITUDE);
		assertTrue( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setLatitude(LATITUDE);
		assertTrue( data.hashCode() == other.hashCode() );
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setSampleDepth(SAMPLE_DEPTH);
		assertTrue( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setSampleDepth(SAMPLE_DEPTH);
		assertTrue( data.hashCode() == other.hashCode() );
		assertTrue( data.equals(other) );
	}

}
