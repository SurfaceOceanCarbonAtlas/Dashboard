/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.*;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;

import org.junit.Test;

/**
 * Unit test for methods in gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData
 *  
 * @author Karl Smith
 */
public class SocatCruiseDataTest {

	static final Integer NEGATIVE_ONE = -1;
	static final Integer ZERO = 0;

	static final Integer YEAR = 2014;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getYear()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setYear(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetYear() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setYear(YEAR);
		assertEquals(YEAR, data.getYear());
		data.setYear(null);
		assertEquals(NEGATIVE_ONE, data.getYear());
	}

	static final Integer MONTH = 1;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getMonth()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setMonth(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetMonth() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(NEGATIVE_ONE, data.getMonth());
		data.setMonth(MONTH);
		assertEquals(MONTH, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setMonth(null);
		assertEquals(NEGATIVE_ONE, data.getMonth());
	}

	static final Integer DAY = 13;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getDay()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setDay(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetDay() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(NEGATIVE_ONE, data.getDay());
		data.setDay(DAY);
		assertEquals(DAY, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setDay(null);
		assertEquals(NEGATIVE_ONE, data.getDay());
	}

	static final Integer HOUR = 19;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getHour()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setHour(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetHour() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(NEGATIVE_ONE, data.getHour());
		data.setHour(HOUR);
		assertEquals(HOUR, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setHour(null);
		assertEquals(NEGATIVE_ONE, data.getHour());
	}

	static final Integer MINUTE = 35;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getMinute()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setMinute(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetMinute() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(NEGATIVE_ONE, data.getMinute());
		data.setMinute(MINUTE);
		assertEquals(MINUTE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setMinute(null);
		assertEquals(NEGATIVE_ONE, data.getMinute());
	}

	static final Double SECOND = 18.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getSecond()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setSecond(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSecond() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getSecond().isNaN() );
		data.setSecond(SECOND);
		assertEquals(SECOND, data.getSecond());
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setSecond(null);
		assertTrue( data.getSecond().isNaN() );
	}

	static final Double LONGITUDE = -125.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getLongitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setLongitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLongitude() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getLongitude().isNaN() );
		data.setLongitude(LONGITUDE);
		assertEquals(LONGITUDE, data.getLongitude());
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setLongitude(null);
		assertTrue( data.getLongitude().isNaN() );
	}

	static final Double LATITUDE = 46.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getLatitude()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setLatitude(java.lang.Double)}.
	 */
	@Test
	public void testGetSetLatitude() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getLatitude().isNaN() );
		data.setLatitude(LATITUDE);
		assertEquals(LATITUDE, data.getLatitude());
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setLatitude(null);
		assertTrue( data.getLatitude().isNaN() );
	}

	static final Double SAMPLE_DEPTH = 5.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getSampleDepth()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setSampleDepth(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSampleDepth() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getSampleDepth().isNaN() );
		data.setSampleDepth(SAMPLE_DEPTH);
		assertEquals(SAMPLE_DEPTH, data.getSampleDepth());
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setSampleDepth(null);
		assertTrue( data.getSampleDepth().isNaN() );
	}

	static final Double SST = 15.7;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getSst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setSst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSst() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getSst().isNaN() );
		data.setSst(SST);
		assertEquals(SST, data.getSst());
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setSst(null);
		assertTrue( data.getSst().isNaN() );
	}

	static final Double T_EQU = 16.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getTEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setTEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetTEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getTEqu().isNaN() );
		data.setTEqu(T_EQU);
		assertEquals(T_EQU, data.getTEqu());
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setTEqu(null);
		assertTrue( data.getTEqu().isNaN() );
	}

	static final Double SAL = 31.6;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getSal()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setSal(java.lang.Double)}.
	 */
	@Test
	public void testGetSetSal() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getSal().isNaN() );
		data.setSal(SAL);
		assertEquals(SAL, data.getSal());
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setSal(null);
		assertTrue( data.getSal().isNaN() );
	}

	static final Double P_ATM = 1003.3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getPAtm()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setPAtm(java.lang.Double)}.
	 */
	@Test
	public void testGetSetPAtm() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getPAtm().isNaN() );
		data.setPAtm(P_ATM);
		assertEquals(P_ATM, data.getPAtm());
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setPAtm(null);
		assertTrue( data.getPAtm().isNaN() );
	}

	static final Double P_EQU = 1003.7;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getPEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setPEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetPEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getPEqu().isNaN() );
		data.setPEqu(P_EQU);
		assertEquals(P_EQU, data.getPEqu());
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setPEqu(null);
		assertTrue( data.getPEqu().isNaN() );
	}

	static final Double X_CO2_WATER_SST = 451.3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getXCO2WaterSst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setXCO2WaterSst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetXCO2WaterSst() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getXCO2WaterSst().isNaN() );
		data.setXCO2WaterSst(X_CO2_WATER_SST);
		assertEquals(X_CO2_WATER_SST, data.getXCO2WaterSst());
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setXCO2WaterSst(null);
		assertTrue( data.getXCO2WaterSst().isNaN() );
	}

	static final Double X_CO2_WATER_T_EQU = 450.9;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getXCO2WaterTEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setXCO2WaterTEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetXCO2WaterTEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		data.setXCO2WaterTEqu(X_CO2_WATER_T_EQU);
		assertEquals(X_CO2_WATER_T_EQU, data.getXCO2WaterTEqu());
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setXCO2WaterTEqu(null);
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
	}

	static final Double F_CO2_WATER_SST = 451.6;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2WaterSst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2WaterSst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2WaterSst() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2WaterSst().isNaN() );
		data.setFCO2WaterSst(F_CO2_WATER_SST);
		assertEquals(F_CO2_WATER_SST, data.getFCO2WaterSst());
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2WaterSst(null);
		assertTrue( data.getFCO2WaterSst().isNaN() );
	}

	static final Double F_CO2_WATER_T_EQU = 451.2;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2WaterTEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2WaterTEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2WaterTEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		data.setFCO2WaterTEqu(F_CO2_WATER_T_EQU);
		assertEquals(F_CO2_WATER_T_EQU, data.getFCO2WaterTEqu());
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2WaterTEqu(null);
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
	}

	static final Double P_CO2_WATER_SST = 451.9;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getPCO2WaterSst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setPCO2WaterSst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetPCO2WaterSst() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getPCO2WaterSst().isNaN() );
		data.setPCO2WaterSst(P_CO2_WATER_SST);
		assertEquals(P_CO2_WATER_SST, data.getPCO2WaterSst());
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setPCO2WaterSst(null);
		assertTrue( data.getPCO2WaterSst().isNaN() );
	}

	static final Double P_CO2_WATER_T_EQU = 451.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getPCO2WaterTEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setPCO2WaterTEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetPCO2WaterTEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		data.setPCO2WaterTEqu(P_CO2_WATER_T_EQU);
		assertEquals(P_CO2_WATER_T_EQU, data.getPCO2WaterTEqu());
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setPCO2WaterTEqu(null);
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
	}

	static final Double WOA_SSS = 31.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getWoaSss()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setWoaSss(java.lang.Double)}.
	 */
	@Test
	public void testGetSetWoaSss() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getWoaSss().isNaN() );
		data.setWoaSss(WOA_SSS);
		assertEquals(WOA_SSS, data.getWoaSss());
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setWoaSss(null);
		assertTrue( data.getWoaSss().isNaN() );
	}

	static final Double NCEP_SLP = 1003.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getNcepSlp()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setNcepSlp(java.lang.Double)}.
	 */
	@Test
	public void testGetSetNcepSlp() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getNcepSlp().isNaN() );
		data.setNcepSlp(NCEP_SLP);
		assertEquals(NCEP_SLP, data.getNcepSlp());
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setNcepSlp(null);
		assertTrue( data.getNcepSlp().isNaN() );
	}

	static final Double F_CO2_FROM_X_CO2_T_EQU = 452.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromXCO2TEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromXCO2TEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2TEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		data.setFCO2FromXCO2TEqu(F_CO2_FROM_X_CO2_T_EQU);
		assertEquals(F_CO2_FROM_X_CO2_T_EQU, data.getFCO2FromXCO2TEqu());
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromXCO2TEqu(null);
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
	}

	static final Double F_CO2_FROM_X_CO2_SST = 452.1;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromXCO2Sst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromXCO2Sst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2Sst() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		data.setFCO2FromXCO2Sst(F_CO2_FROM_X_CO2_SST);
		assertEquals(F_CO2_FROM_X_CO2_SST, data.getFCO2FromXCO2Sst());
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromXCO2Sst(null);
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
	}

	static final Double F_CO2_FROM_P_CO2_T_EQU = 452.2;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromPCO2TEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromPCO2TEqu(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromPCO2TEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		data.setFCO2FromPCO2TEqu(F_CO2_FROM_P_CO2_T_EQU);
		assertEquals(F_CO2_FROM_P_CO2_T_EQU, data.getFCO2FromPCO2TEqu());
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromPCO2TEqu(null);
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
	}

	static final Double F_CO2_FROM_P_CO2_SST = 452.3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromPCO2Sst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromPCO2Sst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromPCO2Sst() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		data.setFCO2FromPCO2Sst(F_CO2_FROM_P_CO2_SST);
		assertEquals(F_CO2_FROM_P_CO2_SST, data.getFCO2FromPCO2Sst());
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromPCO2Sst(null);
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
	}

	static final Double F_CO2_FROM_F_CO2_T_EQU = 452.4;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromFCO2TEqu()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromFCO2TEqu(java.lang.Double)}.
	 */
	@Test
	public void testSetFCO2FromFCO2TEqu() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		data.setFCO2FromFCO2TEqu(F_CO2_FROM_F_CO2_T_EQU);
		assertEquals(F_CO2_FROM_F_CO2_T_EQU, data.getFCO2FromFCO2TEqu());
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromFCO2TEqu(null);
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
	}

	static final Double F_CO2_FROM_F_CO2_SST = 452.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromFCO2Sst()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromFCO2Sst(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromFCO2Sst() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		data.setFCO2FromFCO2Sst(F_CO2_FROM_F_CO2_SST);
		assertEquals(F_CO2_FROM_F_CO2_SST, data.getFCO2FromFCO2Sst());
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromFCO2Sst(null);
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
	}

	static final Double F_CO2_FROM_P_CO2_T_EQU_NCEP = 452.6;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromPCO2TEquNcep()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromPCO2TEquNcep(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromPCO2TEquNcep() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		data.setFCO2FromPCO2TEquNcep(F_CO2_FROM_P_CO2_T_EQU_NCEP);
		assertEquals(F_CO2_FROM_P_CO2_T_EQU_NCEP, data.getFCO2FromPCO2TEquNcep());
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromPCO2TEquNcep(null);
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
	}

	static final Double F_CO2_FROM_P_CO2_SST_NCEP = 452.7;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromPCO2SstNcep()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromPCO2SstNcep(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromPCO2SstNcep() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		data.setFCO2FromPCO2SstNcep(F_CO2_FROM_P_CO2_SST_NCEP);
		assertEquals(F_CO2_FROM_P_CO2_SST_NCEP, data.getFCO2FromPCO2SstNcep());
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromPCO2SstNcep(null);
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
	}

	static final Double F_CO2_FROM_X_CO2_T_EQU_WOA = 452.8;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromXCO2TEquWoa()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromXCO2TEquWoa(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2TEquWoa() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		data.setFCO2FromXCO2TEquWoa(F_CO2_FROM_X_CO2_T_EQU_WOA);
		assertEquals(F_CO2_FROM_X_CO2_T_EQU_WOA, data.getFCO2FromXCO2TEquWoa());
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromXCO2TEquWoa(null);
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
	}

	static final Double F_CO2_FROM_X_CO2_SST_WOA = 452.9;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromXCO2SstWoa()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromXCO2SstWoa(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2SstWoa() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		data.setFCO2FromXCO2SstWoa(F_CO2_FROM_X_CO2_SST_WOA);
		assertEquals(F_CO2_FROM_X_CO2_SST_WOA, data.getFCO2FromXCO2SstWoa());
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromXCO2SstWoa(null);
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
	}

	static final Double F_CO2_FROM_X_CO2_T_EQU_NCEP = 453.0;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromXCO2TEquNcep()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromXCO2TEquNcep(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2TEquNcsp() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		data.setFCO2FromXCO2TEquNcep(F_CO2_FROM_X_CO2_T_EQU_NCEP);
		assertEquals(F_CO2_FROM_X_CO2_T_EQU_NCEP, data.getFCO2FromXCO2TEquNcep());
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromXCO2TEquNcep(null);
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
	}

	static final Double F_CO2_FROM_X_CO2_SST_NCEP = 453.1;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromXCO2SstNcep()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromXCO2SstNcep(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2SstNcep() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		data.setFCO2FromXCO2SstNcep(F_CO2_FROM_X_CO2_SST_NCEP);
		assertEquals(F_CO2_FROM_X_CO2_SST_NCEP, data.getFCO2FromXCO2SstNcep());
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromXCO2SstNcep(null);
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
	}

	static final Double F_CO2_FROM_X_CO2_T_EQU_NCEP_WOA = 453.2;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromXCO2TEquNcepWoa()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromXCO2TEquNcepWoa(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2TEquNcepWoa() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
		data.setFCO2FromXCO2TEquNcepWoa(F_CO2_FROM_X_CO2_T_EQU_NCEP_WOA);
		assertEquals(F_CO2_FROM_X_CO2_T_EQU_NCEP_WOA, data.getFCO2FromXCO2TEquNcepWoa());
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromXCO2TEquNcepWoa(null);
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
	}

	static final Double F_CO2_FROM_X_CO2_SST_NCEP_WOA = 453.3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2FromXCO2SstNcepWoa()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2FromXCO2SstNcepWoa(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2FromXCO2SstNcepWoa() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2FromXCO2SstNcepWoa().isNaN() );
		data.setFCO2FromXCO2SstNcepWoa(F_CO2_FROM_X_CO2_SST_NCEP_WOA);
		assertEquals(F_CO2_FROM_X_CO2_SST_NCEP_WOA, data.getFCO2FromXCO2SstNcepWoa());
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2FromXCO2SstNcepWoa(null);
		assertTrue( data.getFCO2FromXCO2SstNcepWoa().isNaN() );
	}

	static final Double F_CO2_REC = 453.4;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2Rec()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2Rec(java.lang.Double)}.
	 */
	@Test
	public void testGetSetFCO2Rec() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getFCO2Rec().isNaN() );
		data.setFCO2Rec(F_CO2_REC);
		assertEquals(F_CO2_REC, data.getFCO2Rec());
		assertTrue( data.getFCO2FromXCO2SstNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2Rec(null);
		assertTrue( data.getFCO2Rec().isNaN() );
	}

	static final Integer F_CO2_SOURCE = 15;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getFCO2Source()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setFCO2Source(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetFCO2Source() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(ZERO, data.getFCO2Source());
		data.setFCO2Source(F_CO2_SOURCE);
		assertEquals(F_CO2_SOURCE, data.getFCO2Source());
		assertTrue( data.getFCO2Rec().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setFCO2Source(null);
		assertEquals(ZERO, data.getFCO2Source());
	}

	static final Double DELTA_T = 0.3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getDeltaT()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setDeltaT(java.lang.Double)}.
	 */
	@Test
	public void testGetSetDeltaT() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getDeltaT().isNaN() );
		data.setDeltaT(DELTA_T);
		assertEquals(DELTA_T, data.getDeltaT());
		assertEquals(ZERO, data.getFCO2Source());
		assertTrue( data.getFCO2Rec().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setDeltaT(null);
		assertTrue( data.getDeltaT().isNaN() );
	}

	static final String REGION_ID = "C";
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getRegionID()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setRegionID(java.lang.String)}.
	 */
	@Test
	public void testGetSetRegionID() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals("", data.getRegionID());
		data.setRegionID(REGION_ID);
		assertEquals(REGION_ID, data.getRegionID());
		assertTrue( data.getDeltaT().isNaN() );
		assertEquals(ZERO, data.getFCO2Source());
		assertTrue( data.getFCO2Rec().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setRegionID(null);
		assertEquals("", data.getRegionID());
	}

	static final Double CALC_SPEED = 2.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getCalcSpeed()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setCalcSpeed(java.lang.Double)}.
	 */
	@Test
	public void testGetSetCalcSpeed() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getCalcSpeed().isNaN() );
		data.setCalcSpeed(CALC_SPEED);
		assertEquals(CALC_SPEED, data.getCalcSpeed());
		assertEquals("", data.getRegionID());
		assertTrue( data.getDeltaT().isNaN() );
		assertEquals(ZERO, data.getFCO2Source());
		assertTrue( data.getFCO2Rec().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setCalcSpeed(null);
		assertTrue( data.getCalcSpeed().isNaN() );
	}

	static final Double ETOPO2 = 293.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getEtopo2()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setEtopo2(java.lang.Double)}.
	 */
	@Test
	public void testGetSetEtopo2() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getEtopo2().isNaN() );
		data.setEtopo2(ETOPO2);
		assertEquals(ETOPO2, data.getEtopo2());
		assertTrue( data.getCalcSpeed().isNaN() );
		assertEquals("", data.getRegionID());
		assertTrue( data.getDeltaT().isNaN() );
		assertEquals(ZERO, data.getFCO2Source());
		assertTrue( data.getFCO2Rec().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setEtopo2(null);
		assertTrue( data.getEtopo2().isNaN() );
	}

	static final Double GVCO2 = 428.4;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getGVCO2()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setGVCO2(java.lang.Double)}.
	 */
	@Test
	public void testGetSetGVCO2() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getGVCO2().isNaN() );
		data.setGVCO2(GVCO2);
		assertEquals(GVCO2, data.getGVCO2());
		assertTrue( data.getEtopo2().isNaN() );
		assertTrue( data.getCalcSpeed().isNaN() );
		assertEquals("", data.getRegionID());
		assertTrue( data.getDeltaT().isNaN() );
		assertEquals(ZERO, data.getFCO2Source());
		assertTrue( data.getFCO2Rec().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setGVCO2(null);
		assertTrue( data.getGVCO2().isNaN() );
	}

	static final Double DIST_TO_LAND = 232.5;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getDistToLand()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setDistToLand(java.lang.Double)}.
	 */
	@Test
	public void testGetSetDistToLand() {
		SocatCruiseData data = new SocatCruiseData();
		assertTrue( data.getDistToLand().isNaN() );
		data.setDistToLand(DIST_TO_LAND);
		assertEquals(DIST_TO_LAND, data.getDistToLand());
		assertTrue( data.getGVCO2().isNaN() );
		assertTrue( data.getEtopo2().isNaN() );
		assertTrue( data.getCalcSpeed().isNaN() );
		assertEquals("", data.getRegionID());
		assertTrue( data.getDeltaT().isNaN() );
		assertEquals(ZERO, data.getFCO2Source());
		assertTrue( data.getFCO2Rec().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setDistToLand(null);
		assertTrue( data.getDistToLand().isNaN() );
	}

	static final Integer WOCE_FLAG = 3;
	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#getWoceFlag()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#setWoceFlag(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetWoceFlag() {
		SocatCruiseData data = new SocatCruiseData();
		assertEquals(ZERO, data.getWoceFlag() );
		data.setWoceFlag(WOCE_FLAG);
		assertEquals(WOCE_FLAG, data.getWoceFlag());
		assertTrue( data.getDistToLand().isNaN() );
		assertTrue( data.getGVCO2().isNaN() );
		assertTrue( data.getEtopo2().isNaN() );
		assertTrue( data.getCalcSpeed().isNaN() );
		assertEquals("", data.getRegionID());
		assertTrue( data.getDeltaT().isNaN() );
		assertEquals(ZERO, data.getFCO2Source());
		assertTrue( data.getFCO2Rec().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcepWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromXCO2SstWoa().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEquWoa().isNaN() );
		assertTrue( data.getFCO2FromPCO2SstNcep().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEquNcep().isNaN() );
		assertTrue( data.getFCO2FromFCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromFCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromPCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromPCO2TEqu().isNaN() );
		assertTrue( data.getFCO2FromXCO2Sst().isNaN() );
		assertTrue( data.getFCO2FromXCO2TEqu().isNaN() );
		assertTrue( data.getNcepSlp().isNaN() );
		assertTrue( data.getWoaSss().isNaN() );
		assertTrue( data.getPCO2WaterTEqu().isNaN() );
		assertTrue( data.getPCO2WaterSst().isNaN() );
		assertTrue( data.getFCO2WaterTEqu().isNaN() );
		assertTrue( data.getFCO2WaterSst().isNaN() );
		assertTrue( data.getXCO2WaterTEqu().isNaN() );
		assertTrue( data.getXCO2WaterSst().isNaN() );
		assertTrue( data.getPEqu().isNaN() );
		assertTrue( data.getPAtm().isNaN() );
		assertTrue( data.getSal().isNaN() );
		assertTrue( data.getTEqu().isNaN() );
		assertTrue( data.getSst().isNaN() );
		assertTrue( data.getSampleDepth().isNaN() );
		assertTrue( data.getLatitude().isNaN() );
		assertTrue( data.getLongitude().isNaN() );
		assertTrue( data.getSecond().isNaN() );
		assertEquals(NEGATIVE_ONE, data.getMinute());
		assertEquals(NEGATIVE_ONE, data.getHour());
		assertEquals(NEGATIVE_ONE, data.getDay());
		assertEquals(NEGATIVE_ONE, data.getMonth());
		assertEquals(NEGATIVE_ONE, data.getYear());
		data.setWoceFlag(null);
		assertEquals(ZERO, data.getWoceFlag() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#hashCode()} 
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData#equals(java.lang.Object)}.
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
