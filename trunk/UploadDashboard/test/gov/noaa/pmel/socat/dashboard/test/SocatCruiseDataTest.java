/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * Unit test for methods in gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData
 *  
 * @author Karl Smith
 */
public class SocatCruiseDataTest {

	static final ArrayList<DataColumnType> TEST_TYPES = new ArrayList<DataColumnType>(Arrays.asList(
			DataColumnType.IGNORE,
			DataColumnType.IGNORE,
			DataColumnType.MONTH, 
			DataColumnType.DAY, 
			DataColumnType.YEAR, 
			DataColumnType.HOUR, 
			DataColumnType.MINUTE, 
			DataColumnType.LATITUDE, 
			DataColumnType.LONGITUDE, 
			DataColumnType.SEA_SURFACE_TEMPERATURE,
			DataColumnType.SALINITY,
			DataColumnType.XCO2WATER_SST,
			DataColumnType.PCO2WATER_EQU,
			DataColumnType.SEA_LEVEL_PRESSURE,
			DataColumnType.SUPPLEMENTAL));
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
	static final ArrayList<Double> EXPECTED_PCO2WATER_EQUS = new ArrayList<Double>();
	static final ArrayList<Double> EXPECTED_SLPS = new ArrayList<Double>();

	static {
		// No seconds, and pressure is in kPa instead of hPa, but shouldn't matter for these tests
		// First two "ignores" are Expocode and Cruise ID, last "supplemental" is ship speed in knots.
		// The last line is a fake entry to test missing values.
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
			"31B520060606,GM0606,6,11,2006,0,13,NaN,NaN,NaN,NaN,NaN,NaN,NaN,0.0"
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
			EXPECTED_PCO2WATER_EQUS.add(Double.valueOf(dataVals.get(12)));
			EXPECTED_SLPS.add(Double.valueOf(dataVals.get(13)));
		}
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#SocatCruiseData(java.util.List, java.util.List)}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#dataListFromDashboardCruise(gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData)}.
	 */
	@Test
	public void testSocatCruiseDataList() {
		DashboardCruiseWithData cruise = new DashboardCruiseWithData();
		cruise.setDataColTypes(TEST_TYPES);
		cruise.setDataValues(TEST_VALUES);
		ArrayList<SocatCruiseData> dataList = SocatCruiseData.dataListFromDashboardCruise(cruise);
		for (int k = 0; k < TEST_TYPES.size(); k++) {
			assertEquals(EXPECTED_YEARS.get(k), dataList.get(k).getYear());
			assertEquals(EXPECTED_MONTHS.get(k), dataList.get(k).getMonth());
			assertEquals(EXPECTED_DAYS.get(k), dataList.get(k).getDay());
			assertEquals(EXPECTED_HOURS.get(k), dataList.get(k).getHour());
			assertEquals(EXPECTED_MINUTES.get(k), dataList.get(k).getMinute());
			assertEquals(EXPECTED_LATITUDES.get(k), dataList.get(k).getLatitude());
			assertEquals(EXPECTED_LONGITUDES.get(k), dataList.get(k).getLongitude());
			assertEquals(EXPECTED_SSTS.get(k), dataList.get(k).getSst());
			assertEquals(EXPECTED_SALS.get(k), dataList.get(k).getSal());
			assertEquals(EXPECTED_XCO2WATER_SSTS.get(k), dataList.get(k).getXCO2WaterSst());
			assertEquals(EXPECTED_PCO2WATER_EQUS.get(k), dataList.get(k).getPCO2WaterTEqu());
			assertEquals(EXPECTED_SLPS.get(k), dataList.get(k).getPAtm());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getSecond());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getSampleDepth());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getTEqu());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getPEqu());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getXCO2WaterTEqu());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2WaterSst());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2WaterTEqu());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getPCO2WaterSst());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getWoaSss());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getNcepSlp());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromXCO2TEqu());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromXCO2Sst());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromPCO2TEqu());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromPCO2Sst());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromFCO2TEqu());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromFCO2Sst());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromPCO2TEquNcep());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromPCO2SstNcep());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromXCO2TEquWoa());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromXCO2SstWoa());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromXCO2TEquNcep());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromXCO2SstNcep());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromXCO2TEquNcepWoa());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2FromXCO2SstNcepWoa());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getFCO2Rec());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getDeltaT());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getCalcSpeed());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getEtopo2());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getGVCO2());
			assertEquals(SocatCruiseData.FP_MISSING_VALUE, dataList.get(k).getDistToLand());
			assertEquals(SocatCruiseData.INT_MISSING_VALUE, dataList.get(k).getFCO2Source());
			assertEquals(SocatCruiseData.INT_MISSING_VALUE, dataList.get(k).getWoceFlag());
			assertEquals("", dataList.get(k).getRegionID());
		}
	}

	static final Integer YEAR = 2014;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getYear()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setYear(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetYear() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setYear(YEAR);
		assertEquals(YEAR, data.getYear());
		data.setYear(null);
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
	}

	static final Integer MONTH = 1;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getMonth()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setMonth(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetMonth() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		data.setMonth(MONTH);
		assertEquals(MONTH, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setMonth(null);
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
	}

	static final Integer DAY = 13;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getDay()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setDay(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetDay() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		data.setDay(DAY);
		assertEquals(DAY, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setDay(null);
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
	}

	static final Integer HOUR = 19;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getHour()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setHour(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetHour() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		data.setHour(HOUR);
		assertEquals(HOUR, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setHour(null);
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
	}

	static final Integer MINUTE = 35;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getMinute()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setMinute(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetMinute() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		data.setMinute(MINUTE);
		assertEquals(MINUTE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setMinute(null);
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
	}

	static final Double SECOND = 18.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getSecond()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setSecond(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSecond() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		data.setSecond(SECOND);
		assertEquals(SECOND, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setSecond(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
	}

	static final Double LONGITUDE = -125.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getLongitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLongitude() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		data.setLongitude(LONGITUDE);
		assertEquals(LONGITUDE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setLongitude(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
	}

	static final Double LATITUDE = 46.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getLatitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLatitude() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		data.setLatitude(LATITUDE);
		assertEquals(LATITUDE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setLatitude(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
	}

	static final Double SAMPLE_DEPTH = 5.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getSampleDepth()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setSampleDepth(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSampleDepth() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		data.setSampleDepth(SAMPLE_DEPTH);
		assertEquals(SAMPLE_DEPTH, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setSampleDepth(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
	}

	static final Double SST = 15.7;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getSst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setSst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSst() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		data.setSst(SST);
		assertEquals(SST, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setSst(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
	}

	static final Double T_EQU = 16.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getTEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setTEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetTEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		data.setTEqu(T_EQU);
		assertEquals(T_EQU, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setTEqu(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
	}

	static final Double SAL = 31.6;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getSal()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setSal(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSal() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		data.setSal(SAL);
		assertEquals(SAL, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setSal(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
	}

	static final Double P_ATM = 1003.3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getPAtm()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setPAtm(java.lang.Double)}.
	 */
	@Test
	public void testGetSetPAtm() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		data.setPAtm(P_ATM);
		assertEquals(P_ATM, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setPAtm(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
	}

	static final Double P_EQU = 1003.7;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getPEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setPEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetPEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		data.setPEqu(P_EQU);
		assertEquals(P_EQU, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setPEqu(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
	}

	static final Double X_CO2_WATER_SST = 451.3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getXCO2WaterSst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setXCO2WaterSst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetXCO2WaterSst() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		data.setXCO2WaterSst(X_CO2_WATER_SST);
		assertEquals(X_CO2_WATER_SST, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setXCO2WaterSst(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
	}

	static final Double X_CO2_WATER_T_EQU = 450.9;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getXCO2WaterTEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setXCO2WaterTEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetXCO2WaterTEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		data.setXCO2WaterTEqu(X_CO2_WATER_T_EQU);
		assertEquals(X_CO2_WATER_T_EQU, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setXCO2WaterTEqu(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
	}

	static final Double F_CO2_WATER_SST = 451.6;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2WaterSst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2WaterSst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2WaterSst() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		data.setFCO2WaterSst(F_CO2_WATER_SST);
		assertEquals(F_CO2_WATER_SST, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2WaterSst(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
	}

	static final Double F_CO2_WATER_T_EQU = 451.2;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2WaterTEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2WaterTEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2WaterTEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		data.setFCO2WaterTEqu(F_CO2_WATER_T_EQU);
		assertEquals(F_CO2_WATER_T_EQU, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2WaterTEqu(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
	}

	static final Double P_CO2_WATER_SST = 451.9;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getPCO2WaterSst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setPCO2WaterSst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetPCO2WaterSst() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		data.setPCO2WaterSst(P_CO2_WATER_SST);
		assertEquals(P_CO2_WATER_SST, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setPCO2WaterSst(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
	}

	static final Double P_CO2_WATER_T_EQU = 451.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getPCO2WaterTEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setPCO2WaterTEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetPCO2WaterTEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		data.setPCO2WaterTEqu(P_CO2_WATER_T_EQU);
		assertEquals(P_CO2_WATER_T_EQU, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setPCO2WaterTEqu(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
	}

	static final Double WOA_SSS = 31.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getWoaSss()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setWoaSss(java.lang.Double)}.
	 */
	@Test
	public void testGetSetWoaSss() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		data.setWoaSss(WOA_SSS);
		assertEquals(WOA_SSS, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setWoaSss(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
	}

	static final Double NCEP_SLP = 1003.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getNcepSlp()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setNcepSlp(java.lang.Double)}.
	 */
	@Test
	public void testGetSetNcepSlp() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		data.setNcepSlp(NCEP_SLP);
		assertEquals(NCEP_SLP, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setNcepSlp(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
	}

	static final Double F_CO2_FROM_X_CO2_T_EQU = 452.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromXCO2TEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromXCO2TEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2TEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		data.setFCO2FromXCO2TEqu(F_CO2_FROM_X_CO2_T_EQU);
		assertEquals(F_CO2_FROM_X_CO2_T_EQU, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromXCO2TEqu(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
	}

	static final Double F_CO2_FROM_X_CO2_SST = 452.1;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromXCO2Sst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromXCO2Sst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2Sst() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		data.setFCO2FromXCO2Sst(F_CO2_FROM_X_CO2_SST);
		assertEquals(F_CO2_FROM_X_CO2_SST, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromXCO2Sst(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
	}

	static final Double F_CO2_FROM_P_CO2_T_EQU = 452.2;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromPCO2TEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromPCO2TEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromPCO2TEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		data.setFCO2FromPCO2TEqu(F_CO2_FROM_P_CO2_T_EQU);
		assertEquals(F_CO2_FROM_P_CO2_T_EQU, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromPCO2TEqu(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
	}

	static final Double F_CO2_FROM_P_CO2_SST = 452.3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromPCO2Sst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromPCO2Sst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromPCO2Sst() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		data.setFCO2FromPCO2Sst(F_CO2_FROM_P_CO2_SST);
		assertEquals(F_CO2_FROM_P_CO2_SST, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromPCO2Sst(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
	}

	static final Double F_CO2_FROM_F_CO2_T_EQU = 452.4;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromFCO2TEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromFCO2TEqu(java.lang.Double)}.
	 */
	@Test
	public void testSetFCO2FromFCO2TEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		data.setFCO2FromFCO2TEqu(F_CO2_FROM_F_CO2_T_EQU);
		assertEquals(F_CO2_FROM_F_CO2_T_EQU, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromFCO2TEqu(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
	}

	static final Double F_CO2_FROM_F_CO2_SST = 452.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromFCO2Sst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromFCO2Sst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromFCO2Sst() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		data.setFCO2FromFCO2Sst(F_CO2_FROM_F_CO2_SST);
		assertEquals(F_CO2_FROM_F_CO2_SST, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromFCO2Sst(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
	}

	static final Double F_CO2_FROM_P_CO2_T_EQU_NCEP = 452.6;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromPCO2TEquNcep()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromPCO2TEquNcep(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromPCO2TEquNcep() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		data.setFCO2FromPCO2TEquNcep(F_CO2_FROM_P_CO2_T_EQU_NCEP);
		assertEquals(F_CO2_FROM_P_CO2_T_EQU_NCEP, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromPCO2TEquNcep(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
	}

	static final Double F_CO2_FROM_P_CO2_SST_NCEP = 452.7;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromPCO2SstNcep()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromPCO2SstNcep(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromPCO2SstNcep() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		data.setFCO2FromPCO2SstNcep(F_CO2_FROM_P_CO2_SST_NCEP);
		assertEquals(F_CO2_FROM_P_CO2_SST_NCEP, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromPCO2SstNcep(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
	}

	static final Double F_CO2_FROM_X_CO2_T_EQU_WOA = 452.8;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromXCO2TEquWoa()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromXCO2TEquWoa(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2TEquWoa() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		data.setFCO2FromXCO2TEquWoa(F_CO2_FROM_X_CO2_T_EQU_WOA);
		assertEquals(F_CO2_FROM_X_CO2_T_EQU_WOA, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromXCO2TEquWoa(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
	}

	static final Double F_CO2_FROM_X_CO2_SST_WOA = 452.9;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromXCO2SstWoa()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromXCO2SstWoa(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2SstWoa() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		data.setFCO2FromXCO2SstWoa(F_CO2_FROM_X_CO2_SST_WOA);
		assertEquals(F_CO2_FROM_X_CO2_SST_WOA, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromXCO2SstWoa(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
	}

	static final Double F_CO2_FROM_X_CO2_T_EQU_NCEP = 453.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromXCO2TEquNcep()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromXCO2TEquNcep(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2TEquNcsp() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		data.setFCO2FromXCO2TEquNcep(F_CO2_FROM_X_CO2_T_EQU_NCEP);
		assertEquals(F_CO2_FROM_X_CO2_T_EQU_NCEP, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromXCO2TEquNcep(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
	}

	static final Double F_CO2_FROM_X_CO2_SST_NCEP = 453.1;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromXCO2SstNcep()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromXCO2SstNcep(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2SstNcep() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		data.setFCO2FromXCO2SstNcep(F_CO2_FROM_X_CO2_SST_NCEP);
		assertEquals(F_CO2_FROM_X_CO2_SST_NCEP, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromXCO2SstNcep(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
	}

	static final Double F_CO2_FROM_X_CO2_T_EQU_NCEP_WOA = 453.2;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromXCO2TEquNcepWoa()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromXCO2TEquNcepWoa(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2TEquNcepWoa() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
		data.setFCO2FromXCO2TEquNcepWoa(F_CO2_FROM_X_CO2_T_EQU_NCEP_WOA);
		assertEquals(F_CO2_FROM_X_CO2_T_EQU_NCEP_WOA, data.getFCO2FromXCO2TEquNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromXCO2TEquNcepWoa(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
	}

	static final Double F_CO2_FROM_X_CO2_SST_NCEP_WOA = 453.3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2FromXCO2SstNcepWoa()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2FromXCO2SstNcepWoa(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2SstNcepWoa() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcepWoa());
		data.setFCO2FromXCO2SstNcepWoa(F_CO2_FROM_X_CO2_SST_NCEP_WOA);
		assertEquals(F_CO2_FROM_X_CO2_SST_NCEP_WOA, data.getFCO2FromXCO2SstNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2FromXCO2SstNcepWoa(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcepWoa());
	}

	static final Double F_CO2_REC = 453.4;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2Rec()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2Rec(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2Rec() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2Rec());
		data.setFCO2Rec(F_CO2_REC);
		assertEquals(F_CO2_REC, data.getFCO2Rec());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2Rec(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2Rec());
	}

	static final Integer F_CO2_SOURCE = 15;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getFCO2Source()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setFCO2Source(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetFCO2Source() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getFCO2Source());
		data.setFCO2Source(F_CO2_SOURCE);
		assertEquals(F_CO2_SOURCE, data.getFCO2Source());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2Rec());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setFCO2Source(null);
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getFCO2Source());
	}

	static final Double DELTA_T = 0.3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getDeltaT()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setDeltaT(java.lang.Double)}.
	 */
	@Test
	public void testGetSetDeltaT() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getDeltaT());
		data.setDeltaT(DELTA_T);
		assertEquals(DELTA_T, data.getDeltaT());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getFCO2Source());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2Rec());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setDeltaT(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getDeltaT());
	}

	static final String REGION_ID = "C";
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getRegionID()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setRegionID(java.lang.String)}.
	 */
	@Test
	public void testGetSetRegionID() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals("", data.getRegionID());
		data.setRegionID(REGION_ID);
		assertEquals(REGION_ID, data.getRegionID());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getDeltaT());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getFCO2Source());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2Rec());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setRegionID(null);
		assertEquals("", data.getRegionID());
	}

	static final Double CALC_SPEED = 2.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getCalcSpeed()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setCalcSpeed(java.lang.Double)}.
	 */
	@Test
	public void testGetSetCalcSpeed() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getCalcSpeed());
		data.setCalcSpeed(CALC_SPEED);
		assertEquals(CALC_SPEED, data.getCalcSpeed());
		assertEquals("", data.getRegionID());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getDeltaT());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getFCO2Source());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2Rec());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setCalcSpeed(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getCalcSpeed());
	}

	static final Double ETOPO2 = 293.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getEtopo2()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setEtopo2(java.lang.Double)}.
	 */
	@Test
	public void testGetSetEtopo2() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getEtopo2());
		data.setEtopo2(ETOPO2);
		assertEquals(ETOPO2, data.getEtopo2());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getCalcSpeed());
		assertEquals("", data.getRegionID());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getDeltaT());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getFCO2Source());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2Rec());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setEtopo2(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getEtopo2());
	}

	static final Double GVCO2 = 428.4;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getGVCO2()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setGVCO2(java.lang.Double)}.
	 */
	@Test
	public void testGetSetGVCO2() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getGVCO2());
		data.setGVCO2(GVCO2);
		assertEquals(GVCO2, data.getGVCO2());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getEtopo2());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getCalcSpeed());
		assertEquals("", data.getRegionID());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getDeltaT());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getFCO2Source());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2Rec());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setGVCO2(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getGVCO2());
	}

	static final Double DIST_TO_LAND = 232.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getDistToLand()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setDistToLand(java.lang.Double)}.
	 */
	@Test
	public void testGetSetDistToLand() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getDistToLand());
		data.setDistToLand(DIST_TO_LAND);
		assertEquals(DIST_TO_LAND, data.getDistToLand());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getGVCO2());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getEtopo2());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getCalcSpeed());
		assertEquals("", data.getRegionID());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getDeltaT());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getFCO2Source());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2Rec());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setDistToLand(null);
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getDistToLand());
	}

	static final Integer WOCE_FLAG = 3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#getWoceFlag()}
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#setWoceFlag(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetWoceFlag() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getWoceFlag() );
		data.setWoceFlag(WOCE_FLAG);
		assertEquals(WOCE_FLAG, data.getWoceFlag());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getDistToLand());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getGVCO2());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getEtopo2());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getCalcSpeed());
		assertEquals("", data.getRegionID());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getDeltaT());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getFCO2Source());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2Rec());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcepWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2SstWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEquWoa());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2SstNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEquNcep());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromFCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromPCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2Sst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2FromXCO2TEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getNcepSlp());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getWoaSss());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getFCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getXCO2WaterSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getPAtm());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSal());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getTEqu());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSst());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSampleDepth());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLatitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getLongitude());
		assertEquals(SocatCruiseData.FP_MISSING_VALUE, data.getSecond());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMinute());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getHour());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getDay());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getMonth());
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getYear());
		data.setWoceFlag(null);
		assertEquals(SocatCruiseData.INT_MISSING_VALUE, data.getWoceFlag() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#hashCode()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.nc.SocatCruiseData#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		SocatCruiseData data = new SocatCruiseData();
		assertFalse( data.equals(null) );
		assertFalse( data.equals(YEAR) );

		SocatCruiseData other = new SocatCruiseData();
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		data.setYear(YEAR);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setYear(YEAR);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		data.setMonth(MONTH);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setMonth(MONTH);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		data.setDay(DAY);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setDay(DAY);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		data.setHour(HOUR);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setHour(HOUR);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		data.setMinute(MINUTE);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setMinute(MINUTE);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setSecond(SECOND);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setSecond(SECOND);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setLongitude(LONGITUDE);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setLongitude(LONGITUDE);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setLatitude(LATITUDE);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setLatitude(LATITUDE);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setSampleDepth(SAMPLE_DEPTH);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setSampleDepth(SAMPLE_DEPTH);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setSst(SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setSst(SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setTEqu(T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setTEqu(T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setSal(SAL);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setSal(SAL);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setPAtm(P_ATM);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setPAtm(P_ATM);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setPEqu(P_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setPEqu(P_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setXCO2WaterSst(X_CO2_WATER_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setXCO2WaterSst(X_CO2_WATER_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setXCO2WaterTEqu(X_CO2_WATER_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setXCO2WaterTEqu(X_CO2_WATER_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2WaterSst(F_CO2_WATER_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2WaterSst(F_CO2_WATER_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2WaterTEqu(F_CO2_WATER_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2WaterTEqu(F_CO2_WATER_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setPCO2WaterSst(P_CO2_WATER_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setPCO2WaterSst(P_CO2_WATER_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setPCO2WaterTEqu(P_CO2_WATER_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setPCO2WaterTEqu(P_CO2_WATER_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setWoaSss(WOA_SSS);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setWoaSss(WOA_SSS);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setNcepSlp(NCEP_SLP);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setNcepSlp(NCEP_SLP);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromXCO2TEqu(F_CO2_FROM_X_CO2_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromXCO2TEqu(F_CO2_FROM_X_CO2_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromXCO2Sst(F_CO2_FROM_X_CO2_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromXCO2Sst(F_CO2_FROM_X_CO2_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromPCO2TEqu(F_CO2_FROM_P_CO2_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromPCO2TEqu(F_CO2_FROM_P_CO2_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromPCO2Sst(F_CO2_FROM_P_CO2_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromPCO2Sst(F_CO2_FROM_P_CO2_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromFCO2TEqu(F_CO2_FROM_F_CO2_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromFCO2TEqu(F_CO2_FROM_F_CO2_T_EQU);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromFCO2Sst(F_CO2_FROM_F_CO2_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromFCO2Sst(F_CO2_FROM_F_CO2_SST);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromPCO2TEquNcep(F_CO2_FROM_P_CO2_T_EQU_NCEP);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromPCO2TEquNcep(F_CO2_FROM_P_CO2_T_EQU_NCEP);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromPCO2SstNcep(F_CO2_FROM_P_CO2_SST_NCEP);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromPCO2SstNcep(F_CO2_FROM_P_CO2_SST_NCEP);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromXCO2TEquWoa(F_CO2_FROM_X_CO2_T_EQU_WOA);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromXCO2TEquWoa(F_CO2_FROM_X_CO2_T_EQU_WOA);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromXCO2SstWoa(F_CO2_FROM_X_CO2_SST_WOA);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromXCO2SstWoa(F_CO2_FROM_X_CO2_SST_WOA);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromXCO2TEquNcep(F_CO2_FROM_X_CO2_T_EQU_NCEP);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromXCO2TEquNcep(F_CO2_FROM_X_CO2_T_EQU_NCEP);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromXCO2SstNcep(F_CO2_FROM_X_CO2_SST_NCEP);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromXCO2SstNcep(F_CO2_FROM_X_CO2_SST_NCEP);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromXCO2TEquNcepWoa(F_CO2_FROM_X_CO2_T_EQU_NCEP_WOA);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromXCO2TEquNcepWoa(F_CO2_FROM_X_CO2_T_EQU_NCEP_WOA);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2FromXCO2SstNcepWoa(F_CO2_FROM_X_CO2_SST_NCEP_WOA);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2FromXCO2SstNcepWoa(F_CO2_FROM_X_CO2_SST_NCEP_WOA);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setFCO2Rec(F_CO2_REC);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setFCO2Rec(F_CO2_REC);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		data.setFCO2Source(F_CO2_SOURCE);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setFCO2Source(F_CO2_SOURCE);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setDeltaT(DELTA_T);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setDeltaT(DELTA_T);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		data.setRegionID(REGION_ID);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setRegionID(REGION_ID);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setCalcSpeed(CALC_SPEED);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setCalcSpeed(CALC_SPEED);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setEtopo2(ETOPO2);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setEtopo2(ETOPO2);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setGVCO2(GVCO2);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setGVCO2(GVCO2);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		// hashCode ignores floating-point values
		data.setDistToLand(DIST_TO_LAND);
		assertEquals(data.hashCode(), other.hashCode());
		assertFalse( data.equals(other) );
		other.setDistToLand(DIST_TO_LAND);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );

		data.setWoceFlag(WOCE_FLAG);
		assertFalse( data.hashCode() == other.hashCode() );
		assertFalse( data.equals(other) );
		other.setWoceFlag(WOCE_FLAG);
		assertEquals(data.hashCode(), other.hashCode());
		assertTrue( data.equals(other) );
	}

}
