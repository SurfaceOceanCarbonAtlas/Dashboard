/**
 * 
 */
package gov.noaa.pmel.dashboard.test.datatype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.StdDataArray;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.test.dsg.DsgNcFileTest;

/**
 * Unit tests for methods in {@link gov.noaa.pmel.dashboard.datatype.StdDataArray}
 * 
 * @author Karl Smith
 */
public class StdDataArrayTest {

	static final String NO_VALUE_ERRMSG = "no value given";
	static final String INVALID_FP_VALUE_ERRMSG = "not a valid floating-point value";

	static final KnownDataTypes KNOWN_USER_TYPES;

	static {
		KNOWN_USER_TYPES = new KnownDataTypes();
		KNOWN_USER_TYPES.addStandardTypesForUsers();
		Properties typeProps = new Properties();
		typeProps.setProperty(DsgNcFileTest.SALINITY.getVarName(), DsgNcFileTest.SALINITY.toPropertyValue());
		typeProps.setProperty(DsgNcFileTest.SST.getVarName(), DsgNcFileTest.SST.toPropertyValue());
		typeProps.setProperty(DsgNcFileTest.PATM.getVarName(), DsgNcFileTest.PATM.toPropertyValue());
		typeProps.setProperty(DsgNcFileTest.XCO2_WATER_SST_DRY.getVarName(), DsgNcFileTest.XCO2_WATER_SST_DRY.toPropertyValue());
		typeProps.setProperty(DsgNcFileTest.PCO2_WATER_TEQU_WET.getVarName(), DsgNcFileTest.PCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(DsgNcFileTest.SHIP_SPEED.getVarName(), DsgNcFileTest.SHIP_SPEED.toPropertyValue());
		KNOWN_USER_TYPES.addTypesFromProperties(typeProps);
	}

	static final ArrayList<DashDataType<?>> DATA_COLUMN_DASH_TYPES = new ArrayList<DashDataType<?>>(Arrays.asList(
			DashboardServerUtils.DATASET_NAME, 
			DashboardServerUtils.PLATFORM_NAME, 
			DashboardServerUtils.MONTH_OF_YEAR, 
			DashboardServerUtils.DAY_OF_MONTH, 
			DashboardServerUtils.YEAR, 
			DashboardServerUtils.HOUR_OF_DAY, 
			DashboardServerUtils.MINUTE_OF_HOUR, 
			DashboardServerUtils.LATITUDE, 
			DashboardServerUtils.LONGITUDE, 
			DsgNcFileTest.SST, 
			DsgNcFileTest.SALINITY, 
			DsgNcFileTest.XCO2_WATER_SST_DRY, 
			DsgNcFileTest.PCO2_WATER_TEQU_WET, 
			DsgNcFileTest.PATM, 
			DsgNcFileTest.SHIP_SPEED
	));

	static final ArrayList<DataColumnType> DATA_COLUMN_TYPES = new ArrayList<DataColumnType>(Arrays.asList(
			DashboardServerUtils.DATASET_NAME.duplicate(),
			DashboardServerUtils.PLATFORM_NAME.duplicate(),
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

	static final ArrayList<String> USER_COLUMN_NAMES = new ArrayList<String>(
			Arrays.asList("Expocode,Cruise,Month,Day,Year,Hour,Minute,Latitude,Longitude,SST,Salinity,xCO2_water_SST,pCO2_water_Teq,P_atm,Speed".split(","))
	);
	static final ArrayList<ArrayList<String>> DATA_VALUE_STRINGS = new ArrayList<ArrayList<String>>(Arrays.asList(
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,48,29.0514,-92.759,28.78,33.68,409.7,392.5,9009.281,-999,extra".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,49,29.0513,-92.759,28.9,33.56,405.5,388.3,1009.298".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,50,29.0518,-92.7591,28.94,33.48,402.1,385.1,1009.314,garbage ".split(","))),  
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,51,29.0517,-92.7592,28.99,33.44,399.7,382.7,1009.302,0.3".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,52,29.0516,-92.7592,28.9,33.39,397.9,381,1009.29,0.3".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,53,29.0516,-92.7593,28.93,33.38,397.1,380.3,1009.283,0.3".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,54,29.0515,-92.7593,28.96,33.38,395.8,379,1009.272,0.3".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,55,29.051,-92.76,28.88,33.38,395.7,378.9,1009.264,3".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,56,29.0502,-92.7597,29.08,33.4,395.3,378.3,1009.264,3.1".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,57,29.0494,-92.7593,29.35,33.3,392.1,375.1,1009.255,3.1".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,58,29.0486,-92.759,29.34,33.28,391,374,1009.246,3.1".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,10,2006,23,59,29.0478,-92.7587,29.29,33.28,390.5,373.6,1009.223,3.1".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,00,29.0478,-92.7538,29.29,33.32,390.9,374,1009.23,17.6".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,01,29.0492,-92.7522,29.35,33.41,390.3,373.3,1009.255,7.8".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,02,29.0506,-92.7505,29.39,33.47,393,375.9,1009.266,7.8".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,03,29.052,-92.7489,29.43,33.55,395.7,378.4,1009.28,7.8".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,04,29.0534,-92.7472,29.73,33.64,399.7,382,1009.3,7.8".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,05,29.0577,-92.7492,29.84,33.64,402.9,385,1009.302,16.9".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,06,29.0587,-92.7512,29.67,33.55,406.9,388.9,1009.305,8.2".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,07,29.0597,-92.7533,29.66,33.52,408.1,390.2,1009.308,8.2".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,08,29.0608,-92.7553,29.82,33.42,408.1,390,1009.306,8.2".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,09,29.0618,-92.7574,29.81,33.31,408.2,390,1009.31,8.2".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,10,29.0648,-92.7623,29.82,33.22,405.9,387.9,1009.304,20.8".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,11,29.0641,-92.7641,29.9,33.14,404,386,1009.26,7.1".split(","))), 
			new ArrayList<String>(Arrays.asList("31B520060606,GM0606,6,11,2006,0,12,29.0634,-92.766,29.89,32.97,402.9,384.9,1009.237,7.1".split(",")))
	));
	
	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.datatype.StdDataArray#getDataTypes()}.
	 */
	@Test
	public void testGetDataTypes() {
		StdDataArray stdData = new StdDataArray(USER_COLUMN_NAMES, DATA_COLUMN_TYPES, KNOWN_USER_TYPES);
		List<DashDataType<?>> dataTypes = stdData.getDataTypes();
		int numColumns = DATA_COLUMN_TYPES.size();
		assertEquals(numColumns, dataTypes.size());
		for (int k = 0; k < numColumns; k++)
			assertEquals(DATA_COLUMN_DASH_TYPES.get(k), dataTypes.get(k));
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.datatype.StdDataArray#standardizeData(java.util.ArrayList)}
	 * and {@link gov.noaa.pmel.dashboard.datatype.StdDataArray#getStdVal(int, int)}.
	 */
	@Test
	public void testStandardizeData() {
		StdDataArray stdData = new StdDataArray(USER_COLUMN_NAMES, DATA_COLUMN_TYPES, KNOWN_USER_TYPES);
		Integer numDataCols = DATA_COLUMN_TYPES.size();

		// Errors in data: first row has too many values (one message), 
		// second row does not have enough values (two messages; one for the row, one for the value),
		// third row has value that cannot be interpreted (one messages)
		// Not the excessive large pressure in the first row should NOT generate an error at this time
		ArrayList<ADCMessage> msgList = stdData.standardizeData(DATA_VALUE_STRINGS);
		assertEquals(4, msgList.size());

		ADCMessage msg = msgList.get(0);
		assertEquals(ADCMessage.SCMsgSeverity.CRITICAL, msg.getSeverity());
		assertEquals(Integer.valueOf(1), msg.getRowNumber());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getColNumber());
		assertEquals(StdDataArray.INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG, msg.getGeneralComment());
		assertTrue( msg.getDetailedComment().contains(StdDataArray.INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG) );

		msg = msgList.get(1);
		assertEquals(ADCMessage.SCMsgSeverity.CRITICAL, msg.getSeverity());
		assertEquals(Integer.valueOf(2), msg.getRowNumber());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, msg.getColNumber());
		assertEquals(StdDataArray.INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG, msg.getGeneralComment());
		assertTrue( msg.getDetailedComment().contains(StdDataArray.INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG) );

		msg = msgList.get(2);
		assertEquals(ADCMessage.SCMsgSeverity.CRITICAL, msg.getSeverity());
		assertEquals(Integer.valueOf(2), msg.getRowNumber());
		assertEquals(Integer.valueOf(numDataCols), msg.getColNumber());
		assertEquals(NO_VALUE_ERRMSG, msg.getGeneralComment());
		assertEquals(NO_VALUE_ERRMSG, msg.getDetailedComment());

		msg = msgList.get(3);
		assertEquals(ADCMessage.SCMsgSeverity.CRITICAL, msg.getSeverity());
		assertEquals(Integer.valueOf(3), msg.getRowNumber());
		assertEquals(Integer.valueOf(numDataCols), msg.getColNumber());
		assertEquals(INVALID_FP_VALUE_ERRMSG, msg.getGeneralComment());
		assertTrue( msg.getDetailedComment().contains(INVALID_FP_VALUE_ERRMSG) );

		int numRows = DATA_VALUE_STRINGS.size();
		// First two columns are Strings
		for (int k = 0; k < 2; k++) {
			for (int j = 0; j < numRows; j++) {
				String value = (String) stdData.getStdVal(j, k);
				assertEquals(DATA_VALUE_STRINGS.get(j).get(k), value);
			}
		}
		// Next five columns are Integer
		for (int k = 2; k < 7; k++) {
			for (int j = 0; j < numRows; j++) {
				Integer value = (Integer) stdData.getStdVal(j, k);
				assertEquals(Integer.valueOf(DATA_VALUE_STRINGS.get(j).get(k)), value);
			}
		}
		// Rest of the columns are Double
		for (int k = 7; k < numDataCols-1; k++) {
			for (int j = 0; j < numRows; j++) {
				Double value = (Double) stdData.getStdVal(j, k);
				assertEquals(Double.valueOf(DATA_VALUE_STRINGS.get(j).get(k)), value, 1.0E-6);
			}
		}
		// Check the invalid values
		assertNull( stdData.getStdVal(0,numDataCols-1) );
		assertNull( stdData.getStdVal(1,numDataCols-1) );
		assertNull( stdData.getStdVal(2,numDataCols-1) );
		for (int j = 3; j < numRows; j++) {
			Double value = (Double) stdData.getStdVal(j, numDataCols-1);
			assertEquals(Double.valueOf(DATA_VALUE_STRINGS.get(j).get(numDataCols-1)), value, 1.0E-6);
		}

		ArrayList<ArrayList<String>> subset = new ArrayList<ArrayList<String>>(
				DATA_VALUE_STRINGS.subList(3, DATA_VALUE_STRINGS.size()) );
		msgList = stdData.standardizeData(subset);
		assertTrue( msgList.isEmpty() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.datatype.StdDataArray#hashCode()} and
	 * {@link gov.noaa.pmel.dashboard.datatype.StdDataArray#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		StdDataArray stdData = new StdDataArray(USER_COLUMN_NAMES, DATA_COLUMN_TYPES, KNOWN_USER_TYPES);
		assertFalse( stdData.equals(null) );
		assertFalse( stdData.equals(USER_COLUMN_NAMES) );

		StdDataArray other = new StdDataArray(USER_COLUMN_NAMES, DATA_COLUMN_TYPES, KNOWN_USER_TYPES);
		assertTrue( stdData.hashCode() == other.hashCode() );
		assertTrue( stdData.equals(other) );

		stdData.standardizeData(DATA_VALUE_STRINGS);
		assertFalse( stdData.hashCode() == other.hashCode() );
		assertFalse( stdData.equals(other) );

		other.standardizeData(DATA_VALUE_STRINGS);
		assertTrue( stdData.hashCode() == other.hashCode() );
		assertTrue( stdData.equals(other) );

		ArrayList<ArrayList<String>> subset = new ArrayList<ArrayList<String>>(
				DATA_VALUE_STRINGS.subList(3, DATA_VALUE_STRINGS.size()) );
		other.standardizeData(subset);
		assertFalse( stdData.hashCode() == other.hashCode() );
		assertFalse( stdData.equals(other) );
	}

}
