/**
 *
 */
package gov.noaa.pmel.dashboard.test.server;

import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.server.SocatCruiseData;
import gov.noaa.pmel.dashboard.server.SocatTypes;
import gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for methods in gov.noaa.pmel.dashboard.nc.SocatCruiseData
 *
 * @author Karl Smith
 */
public class SocatCruiseDataTest {

    static final KnownDataTypes KNOWN_DATA_TYPES;

    static {
        KNOWN_DATA_TYPES = new KnownDataTypes();
        KNOWN_DATA_TYPES.addStandardTypesForDataFiles();
        Properties addnTypeProps = new Properties();
        addnTypeProps.setProperty(SocatTypes.SST.getVarName(),
                                  SocatTypes.SST.toPropertyValue());
        addnTypeProps.setProperty(SocatTypes.SALINITY.getVarName(),
                                  SocatTypes.SALINITY.toPropertyValue());
        addnTypeProps.setProperty(SocatTypes.XCO2_WATER_SST_DRY.getVarName(),
                                  SocatTypes.XCO2_WATER_SST_DRY.toPropertyValue());
        addnTypeProps.setProperty(SocatTypes.PCO2_WATER_TEQU_WET.getVarName(),
                                  SocatTypes.PCO2_WATER_TEQU_WET.toPropertyValue());
        addnTypeProps.setProperty(SocatTypes.PATM.getVarName(),
                                  SocatTypes.PATM.toPropertyValue());
        addnTypeProps.setProperty(CruiseDsgNcFileTest.SHIP_SPEED.getVarName(),
                                  CruiseDsgNcFileTest.SHIP_SPEED.toPropertyValue());
        addnTypeProps.setProperty(SocatTypes.WOCE_CO2_WATER.getVarName(),
                                  SocatTypes.WOCE_CO2_WATER.toPropertyValue());
        KNOWN_DATA_TYPES.addTypesFromProperties(addnTypeProps);
    }

    static final ArrayList<DataColumnType> TEST_USER_TYPES = new ArrayList<DataColumnType>(Arrays.asList(
            DashboardServerUtils.EXPOCODE.duplicate(),
            DashboardServerUtils.DATASET_NAME.duplicate(),
            DashboardServerUtils.MONTH_OF_YEAR.duplicate(),
            DashboardServerUtils.DAY_OF_MONTH.duplicate(),
            DashboardServerUtils.YEAR.duplicate(),
            DashboardServerUtils.HOUR_OF_DAY.duplicate(),
            DashboardServerUtils.MINUTE_OF_HOUR.duplicate(),
            DashboardServerUtils.LATITUDE.duplicate(),
            DashboardServerUtils.LONGITUDE.duplicate(),
            SocatTypes.SST.duplicate(),
            SocatTypes.SALINITY.duplicate(),
            SocatTypes.XCO2_WATER_SST_DRY.duplicate(),
            SocatTypes.PCO2_WATER_TEQU_WET.duplicate(),
            SocatTypes.PATM.duplicate(),
            CruiseDsgNcFileTest.SHIP_SPEED.duplicate()));
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
        for (String valsString : dataValueStrings) {
            ArrayList<String> dataVals = new ArrayList<String>(Arrays.asList(valsString.split(",", -1)));
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
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#SocatCruiseData(KnownDataTypes, java.util.List, int, java.util.List)}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#dataListFromDashboardCruise(gov.noaa.pmel.dashboard.server.KnownDataTypes,
     * gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData)}.
     */
    @Test
    public void testSocatCruiseDataList() {
        DashboardCruiseWithData cruise = new DashboardCruiseWithData();
        cruise.setDataColTypes(TEST_USER_TYPES);
        cruise.setDataValues(TEST_VALUES);
        ArrayList<Integer> rowNums = new ArrayList<Integer>(TEST_USER_TYPES.size());
        for (int k = 1; k <= TEST_USER_TYPES.size(); k++) {
            rowNums.add(k);
        }
        cruise.setRowNums(rowNums);
        ArrayList<SocatCruiseData> dataList = SocatCruiseData.dataListFromDashboardCruise(KNOWN_DATA_TYPES, cruise);
        for (int k = 0; k < dataList.size(); k++) {
            rowNums.add(k + 1);
            SocatCruiseData dataRow = dataList.get(k);
            assertEquals(EXPECTED_YEARS.get(k), dataRow.getYear());
            assertEquals(EXPECTED_MONTHS.get(k), dataRow.getMonth());
            assertEquals(EXPECTED_DAYS.get(k), dataRow.getDay());
            assertEquals(EXPECTED_HOURS.get(k), dataRow.getHour());
            assertEquals(EXPECTED_MINUTES.get(k), dataRow.getMinute());
            assertEquals(EXPECTED_LATITUDES.get(k), dataRow.getLatitude());
            assertEquals(EXPECTED_LONGITUDES.get(k), dataRow.getLongitude());
            assertEquals(EXPECTED_SSTS.get(k), dataRow.getSst());
            assertEquals(EXPECTED_SALS.get(k), dataRow.getSalinity());
            assertEquals(EXPECTED_XCO2WATER_SSTS.get(k), dataRow.getXCO2WaterSstDry());
            assertEquals(EXPECTED_PCO2WATER_TEQUS.get(k), dataRow.getPCO2WaterTEquWet());
            assertEquals(EXPECTED_SLPS.get(k), dataRow.getPAtm());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getSecond());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getSampleDepth());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getTEqu());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getPEqu());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getXCO2WaterTEquDry());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getFCO2WaterTEquWet());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getFCO2WaterSstWet());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getPCO2WaterSstWet());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getWoaSalinity());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getNcepSlp());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getfCO2Rec());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getEtopo2Depth());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getGvCO2());
            assertEquals(DashboardUtils.FP_MISSING_VALUE, dataRow.getDistToLand());
            assertEquals(DashboardUtils.INT_MISSING_VALUE, dataRow.getFCO2Source());
            assertEquals(DashboardUtils.GLOBAL_REGION_ID, dataRow.getRegionID());
            assertEquals(DashboardUtils.WOCE_NOT_CHECKED, dataRow.getWoceCO2Water());
            assertEquals(DashboardUtils.WOCE_NOT_CHECKED, dataRow.getWoceCO2Atm());
        }
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getIntegerVariables()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setIntegerVariableValue(gov.noaa.pmel.dashboard.server.DashDataType, java.lang.Integer)}.
     */
    @Test
    public void testGetSetIntegerVariableValue() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        Integer value = 123;
        data.setIntegerVariableValue(DashboardServerUtils.SAMPLE_NUMBER, value);
        TreeMap<DashDataType,Integer> intMap = data.getIntegerVariables();
        assertEquals(value, intMap.get(DashboardServerUtils.SAMPLE_NUMBER));
        data.setIntegerVariableValue(DashboardServerUtils.SAMPLE_NUMBER, null);
        intMap = data.getIntegerVariables();
        assertEquals(DashboardUtils.INT_MISSING_VALUE, intMap.get(DashboardServerUtils.SAMPLE_NUMBER));
        boolean errCaught = false;
        try {
            data.setIntegerVariableValue(DashboardServerUtils.TIME, value);
        } catch (IllegalArgumentException ex) {
            errCaught = true;
        }
        assertTrue(errCaught);
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getCharacterVariables()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setCharacterVariableValue(gov.noaa.pmel.dashboard.server.DashDataType, java.lang.Character)}.
     */
    @Test
    public void testGetSetCharacterVariableValue() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        Character value = 'K';
        data.setCharacterVariableValue(SocatTypes.WOCE_CO2_WATER, value);
        TreeMap<DashDataType,Character> charMap = data.getCharacterVariables();
        assertEquals(value, charMap.get(SocatTypes.WOCE_CO2_WATER));
        data.setCharacterVariableValue(SocatTypes.WOCE_CO2_WATER, null);
        charMap = data.getCharacterVariables();
        assertEquals(DashboardUtils.CHAR_MISSING_VALUE, charMap.get(SocatTypes.WOCE_CO2_WATER));
        boolean errCaught = false;
        try {
            data.setCharacterVariableValue(DashboardServerUtils.TIME, value);
        } catch (IllegalArgumentException ex) {
            errCaught = true;
        }
        assertTrue(errCaught);
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getDoubleVariables()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setDoubleVariableValue(gov.noaa.pmel.dashboard.server.DashDataType, java.lang.Double)}.
     */
    @Test
    public void testGetSetDoubleVariableValue() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        Double value = ( new Date() ).getTime() / 1000.0;
        data.setDoubleVariableValue(DashboardServerUtils.TIME, value);
        TreeMap<DashDataType,Double> doubleMap = data.getDoubleVariables();
        assertEquals(value, doubleMap.get(DashboardServerUtils.TIME));
        data.setDoubleVariableValue(DashboardServerUtils.TIME, null);
        doubleMap = data.getDoubleVariables();
        assertEquals(DashboardUtils.FP_MISSING_VALUE, doubleMap.get(DashboardServerUtils.TIME));
        boolean errCaught = false;
        try {
            data.setDoubleVariableValue(DashboardServerUtils.SAMPLE_NUMBER, value);
        } catch (IllegalArgumentException ex) {
            errCaught = true;
        }
        assertTrue(errCaught);
    }

    static final Integer SAMPLE_NUMBER = 123;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getSampleNumber()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setSampleNumber(java.lang.Integer)}.
     */
    @Test
    public void testGetSetSampleNumber() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setSampleNumber(SAMPLE_NUMBER);
        assertEquals(SAMPLE_NUMBER, data.getSampleNumber());
        data.setSampleNumber(null);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
    }

    static final Integer YEAR = 2014;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getYear()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setYear(java.lang.Integer)}.
     */
    @Test
    public void testGetSetYear() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        data.setYear(YEAR);
        assertEquals(YEAR, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setYear(null);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
    }

    static final Integer MONTH = 1;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getMonth()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setMonth(java.lang.Integer)}.
     */
    @Test
    public void testGetSetMonth() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
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
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getDay()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setDay(java.lang.Integer)}.
     */
    @Test
    public void testGetSetDay() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
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
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getHour()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setHour(java.lang.Integer)}.
     */
    @Test
    public void testGetSetHour() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
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
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getMinute()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setMinute(java.lang.Integer)}.
     */
    @Test
    public void testGetSetMinute() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
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
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getSecond()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setSecond(java.lang.Double)}.
     */
    @Test
    public void testGetSetSecond() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
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
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getLongitude()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setLongitude(java.lang.Double)}.
     */
    @Test
    public void testGetSetLongitude() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
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
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getLatitude()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setLatitude(java.lang.Double)}.
     */
    @Test
    public void testGetSetLatitude() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
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
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getSampleDepth()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setSampleDepth(java.lang.Double)}.
     */
    @Test
    public void testGetSetSampleDepth() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
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

    static final Double SST = 15.7;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getSst()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setSst(java.lang.Double)}.
     */
    @Test
    public void testGetsetSST() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        data.setSst(SST);
        assertEquals(SST, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setSst(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
    }

    static final Double T_EQU = 16.0;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getTEqu()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setTEqu(java.lang.Double)}.
     */
    @Test
    public void testGetsetTEqu() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        data.setTEqu(T_EQU);
        assertEquals(T_EQU, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setTEqu(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
    }

    static final Double SAL = 31.6;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getSalinity()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setSalinity(java.lang.Double)}.
     */
    @Test
    public void testGetSetSal() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        data.setSalinity(SAL);
        assertEquals(SAL, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setSalinity(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
    }

    static final Double P_ATM = 1003.3;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getPAtm()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setPAtm(java.lang.Double)}.
     */
    @Test
    public void testGetSetPAtm() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        data.setPAtm(P_ATM);
        assertEquals(P_ATM, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setPAtm(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
    }

    static final Double P_EQU = 1003.7;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getPEqu()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setPEqu(java.lang.Double)}.
     */
    @Test
    public void testGetsetPEqu() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        data.setPEqu(P_EQU);
        assertEquals(P_EQU, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setPEqu(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
    }

    static final Double X_CO2_WATER_SST = 451.3;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getXCO2WaterSstDry()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setXCO2WaterSstDry(java.lang.Double)}.
     */
    @Test
    public void testGetSetXCO2WaterSst() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        data.setXCO2WaterSstDry(X_CO2_WATER_SST);
        assertEquals(X_CO2_WATER_SST, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setXCO2WaterSstDry(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
    }

    static final Double X_CO2_WATER_T_EQU = 450.9;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getXCO2WaterTEquDry()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setXCO2WaterTEquDry(java.lang.Double)}.
     */
    @Test
    public void testGetSetXCO2WaterTEqu() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        data.setXCO2WaterTEquDry(X_CO2_WATER_T_EQU);
        assertEquals(X_CO2_WATER_T_EQU, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setXCO2WaterTEquDry(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
    }

    static final Double F_CO2_WATER_SST = 451.6;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getFCO2WaterSstWet()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setFCO2WaterSstWet(java.lang.Double)}.
     */
    @Test
    public void testGetSetFCO2WaterSst() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        data.setFCO2WaterSstWet(F_CO2_WATER_SST);
        assertEquals(F_CO2_WATER_SST, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setFCO2WaterSstWet(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
    }

    static final Double F_CO2_WATER_T_EQU = 451.2;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getFCO2WaterTEquWet()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setFCO2WaterTEquWet(java.lang.Double)}.
     */
    @Test
    public void testGetSetFCO2WaterTEqu() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
        data.setFCO2WaterTEquWet(F_CO2_WATER_T_EQU);
        assertEquals(F_CO2_WATER_T_EQU, data.getFCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setFCO2WaterTEquWet(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
    }

    static final Double P_CO2_WATER_SST = 451.9;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getPCO2WaterSstWet()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setPCO2WaterSstWet(java.lang.Double)}.
     */
    @Test
    public void testGetSetPCO2WaterSst() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterSstWet());
        data.setPCO2WaterSstWet(P_CO2_WATER_SST);
        assertEquals(P_CO2_WATER_SST, data.getPCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setPCO2WaterSstWet(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterSstWet());
    }

    static final Double P_CO2_WATER_T_EQU = 451.5;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getPCO2WaterTEquWet()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setPCO2WaterTEquWet(java.lang.Double)}.
     */
    @Test
    public void testGetSetPCO2WaterTEqu() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterTEquWet());
        data.setPCO2WaterTEquWet(P_CO2_WATER_T_EQU);
        assertEquals(P_CO2_WATER_T_EQU, data.getPCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setPCO2WaterTEquWet(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterTEquWet());
    }

    static final Double WOA_SSS = 31.5;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getWoaSalinity()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setWoaSalinity(java.lang.Double)}.
     */
    @Test
    public void testGetsetWoaSalinity() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getWoaSalinity());
        data.setWoaSalinity(WOA_SSS);
        assertEquals(WOA_SSS, data.getWoaSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setWoaSalinity(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getWoaSalinity());
    }

    static final Double NCEP_SLP = 1003.5;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getNcepSlp()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setNcepSlp(java.lang.Double)}.
     */
    @Test
    public void testGetSetNcepSlp() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getNcepSlp());
        data.setNcepSlp(NCEP_SLP);
        assertEquals(NCEP_SLP, data.getNcepSlp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getWoaSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setNcepSlp(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getNcepSlp());
    }

    static final Double F_CO2_REC = 453.4;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getfCO2Rec()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setfCO2Rec(java.lang.Double)}.
     */
    @Test
    public void testGetSetFCO2Rec() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getfCO2Rec());
        data.setfCO2Rec(F_CO2_REC);
        assertEquals(F_CO2_REC, data.getfCO2Rec());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getNcepSlp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getWoaSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setfCO2Rec(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getfCO2Rec());
    }

    static final Integer F_CO2_SOURCE = 15;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getFCO2Source()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setFCO2Source(java.lang.Integer)}.
     */
    @Test
    public void testGetsetFCO2Source() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getFCO2Source());
        data.setFCO2Source(F_CO2_SOURCE);
        assertEquals(F_CO2_SOURCE, data.getFCO2Source());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getfCO2Rec());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getNcepSlp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getWoaSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setFCO2Source(null);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getFCO2Source());
    }

    static final Character REGION_ID = 'C';

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getRegionID()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setRegionID(Character)}
     */
    @Test
    public void testGetSetRegionID() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.GLOBAL_REGION_ID, data.getRegionID());
        data.setRegionID(REGION_ID);
        assertEquals(REGION_ID, data.getRegionID());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getFCO2Source());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getfCO2Rec());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getNcepSlp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getWoaSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setRegionID(null);
        assertEquals(DashboardUtils.GLOBAL_REGION_ID, data.getRegionID());
    }

    static final Double ETOPO2 = 293.5;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getEtopo2Depth()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setEtopo2Depth(java.lang.Double)}.
     */
    @Test
    public void testGetSetEtopo2Depth() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getEtopo2Depth());
        data.setEtopo2Depth(ETOPO2);
        assertEquals(ETOPO2, data.getEtopo2Depth());
        assertEquals(DashboardUtils.GLOBAL_REGION_ID, data.getRegionID());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getFCO2Source());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getfCO2Rec());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getNcepSlp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getWoaSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setEtopo2Depth(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getEtopo2Depth());
    }

    static final Double GVCO2 = 428.4;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getGvCO2()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setGvCO2(java.lang.Double)}.
     */
    @Test
    public void testGetSetGVCO2() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getGvCO2());
        data.setGvCO2(GVCO2);
        assertEquals(GVCO2, data.getGvCO2());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getEtopo2Depth());
        assertEquals(DashboardUtils.GLOBAL_REGION_ID, data.getRegionID());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getFCO2Source());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getfCO2Rec());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getNcepSlp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getWoaSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setGvCO2(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getGvCO2());
    }

    static final Double DIST_TO_LAND = 232.5;

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#getDistToLand()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#setDistToLand(java.lang.Double)}.
     */
    @Test
    public void testGetSetDistToLand() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getDistToLand());
        data.setDistToLand(DIST_TO_LAND);
        assertEquals(DIST_TO_LAND, data.getDistToLand());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getGvCO2());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getEtopo2Depth());
        assertEquals(DashboardUtils.GLOBAL_REGION_ID, data.getRegionID());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getFCO2Source());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getfCO2Rec());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getNcepSlp());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getWoaSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterTEquWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getFCO2WaterSstWet());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterTEquDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getXCO2WaterSstDry());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getPAtm());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSalinity());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getTEqu());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSst());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSampleDepth());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getSecond());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMinute());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getHour());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getDay());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getMonth());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getYear());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, data.getSampleNumber());
        data.setDistToLand(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, data.getDistToLand());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#hashCode()}
     * and {@link gov.noaa.pmel.dashboard.server.SocatCruiseData#equals(java.lang.Object)}.
     */
    @Test
    public void testHashCodeEqualsObject() {
        SocatCruiseData data = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertFalse(data.equals(null));
        assertFalse(data.equals(YEAR));

        SocatCruiseData other = new SocatCruiseData(KNOWN_DATA_TYPES);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        data.setSampleNumber(SAMPLE_NUMBER);
        assertFalse(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setSampleNumber(SAMPLE_NUMBER);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        data.setYear(YEAR);
        assertFalse(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setYear(YEAR);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        data.setMonth(MONTH);
        assertFalse(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setMonth(MONTH);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        data.setDay(DAY);
        assertFalse(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setDay(DAY);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        data.setHour(HOUR);
        assertFalse(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setHour(HOUR);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        data.setMinute(MINUTE);
        assertFalse(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setMinute(MINUTE);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setSecond(SECOND);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setSecond(SECOND);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setLongitude(LONGITUDE);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setLongitude(LONGITUDE);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setLatitude(LATITUDE);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setLatitude(LATITUDE);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setSampleDepth(SAMPLE_DEPTH);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setSampleDepth(SAMPLE_DEPTH);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setSst(SST);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setSst(SST);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setTEqu(T_EQU);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setTEqu(T_EQU);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setSalinity(SAL);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setSalinity(SAL);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setPAtm(P_ATM);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setPAtm(P_ATM);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setPEqu(P_EQU);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setPEqu(P_EQU);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setXCO2WaterSstDry(X_CO2_WATER_SST);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setXCO2WaterSstDry(X_CO2_WATER_SST);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setXCO2WaterTEquDry(X_CO2_WATER_T_EQU);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setXCO2WaterTEquDry(X_CO2_WATER_T_EQU);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setFCO2WaterSstWet(F_CO2_WATER_SST);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setFCO2WaterSstWet(F_CO2_WATER_SST);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setFCO2WaterTEquWet(F_CO2_WATER_T_EQU);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setFCO2WaterTEquWet(F_CO2_WATER_T_EQU);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setPCO2WaterSstWet(P_CO2_WATER_SST);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setPCO2WaterSstWet(P_CO2_WATER_SST);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setPCO2WaterTEquWet(P_CO2_WATER_T_EQU);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setPCO2WaterTEquWet(P_CO2_WATER_T_EQU);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setWoaSalinity(WOA_SSS);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setWoaSalinity(WOA_SSS);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setNcepSlp(NCEP_SLP);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setNcepSlp(NCEP_SLP);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setfCO2Rec(F_CO2_REC);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setfCO2Rec(F_CO2_REC);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        data.setFCO2Source(F_CO2_SOURCE);
        assertFalse(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setFCO2Source(F_CO2_SOURCE);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        data.setRegionID(REGION_ID);
        assertFalse(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setRegionID(REGION_ID);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setEtopo2Depth(ETOPO2);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setEtopo2Depth(ETOPO2);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setGvCO2(GVCO2);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setGvCO2(GVCO2);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));

        // hashCode ignores floating-point values
        data.setDistToLand(DIST_TO_LAND);
        assertTrue(data.hashCode() == other.hashCode());
        assertFalse(data.equals(other));
        other.setDistToLand(DIST_TO_LAND);
        assertTrue(data.hashCode() == other.hashCode());
        assertTrue(data.equals(other));
    }

}
