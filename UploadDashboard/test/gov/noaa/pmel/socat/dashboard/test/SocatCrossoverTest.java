/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.*;
import gov.noaa.pmel.socat.dashboard.shared.SocatCrossover;

import java.util.Date;

import org.junit.Test;

/**
 * Unit tests for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover}
 * 
 * @author Karl Smith
 */
public class SocatCrossoverTest {

	private static final Double minDist = 23.56;
	private static final String firstExpo = "AAAA19951201";
	private static final Integer firstRowNum = 47;
	private static final Double firstLon = 125.250;
	private static final Double firstLat = 45.320;
	private static final Date firstTime = new Date();
	private static final Date firstMinTime = new Date(firstTime.getTime() - (20L * 24L * 60L * 60L * 1000L));
	private static final Date firstMaxTime =  new Date(firstTime.getTime() + (5L * 24L * 60L * 60L * 1000L));
	private static final String secondExpo = "BBBB19951207";
	private static final Integer secondRowNum = 123;
	private static final Double secondLon = 125.255;
	private static final Double secondLat = 45.315;
	private static final Date secondTime = new Date(firstTime.getTime() - (4L * 60L * 60L * 1000L));
	private static final Date secondMinTime = new Date(secondTime.getTime() - (10L * 24L * 60L * 60L * 1000L));
	private static final Date secondMaxTime =  new Date(secondTime.getTime() + (15L * 24L * 60L * 60L * 1000L));

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#getExpocodes()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#setExpocodes(java.lang.String[])}.
	 */
	@Test
	public void testGetSetExpocodes() {
		SocatCrossover cross = new SocatCrossover();
		String[] expos = cross.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		cross.setExpocodes(new String[] {firstExpo, secondExpo});
		expos = cross.getExpocodes();
		assertEquals(2, expos.length);
		assertEquals(firstExpo, expos[0]);
		assertEquals(secondExpo, expos[1]);

		cross.setExpocodes(null);
		expos = cross.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#getMinDistance()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#setMinDistance(java.lang.Double)}.
	 */
	@Test
	public void testGetSetMinDistance() {
		SocatCrossover cross = new SocatCrossover();
		assertNull(cross.getMinDistance());

		cross.setMinDistance(minDist);
		assertEquals(cross.getMinDistance(), minDist, 1.0E-7);

		String[] expos = cross.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		cross.setMinDistance(null);
		assertNull(cross.getMinDistance());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#getRowNumsAtMin()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#setRowNumsAtMin(java.lang.Integer[])}.
	 */
	@Test
	public void testGetSetRowNumsAtMin() {
		SocatCrossover cross = new SocatCrossover();
		Integer[] rowNums = cross.getRowNumsAtMin();
		assertEquals(2, rowNums.length);
		assertNull(rowNums[0]);
		assertNull(rowNums[1]);

		cross.setRowNumsAtMin(new Integer[] {firstRowNum, secondRowNum});
		rowNums = cross.getRowNumsAtMin();
		assertEquals(2, rowNums.length);
		assertEquals(firstRowNum, rowNums[0]);
		assertEquals(secondRowNum, rowNums[1]);

		assertNull(cross.getMinDistance());
		String[] expos = cross.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		cross.setRowNumsAtMin(null);
		rowNums = cross.getRowNumsAtMin();
		assertEquals(2, rowNums.length);
		assertNull(rowNums[0]);
		assertNull(rowNums[1]);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#getLonsAtMin()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#setLonsAtMin(java.lang.Double[])}.
	 */
	@Test
	public void testGetSetLonsAtMin() {
		SocatCrossover cross = new SocatCrossover();
		Double[] lons = cross.getLonsAtMin();
		assertEquals(2, lons.length);
		assertNull(lons[0]);
		assertNull(lons[1]);

		cross.setLonsAtMin(new Double[] {firstLon, secondLon});
		lons = cross.getLonsAtMin();
		assertEquals(2, lons.length);
		assertEquals(firstLon, lons[0]);
		assertEquals(secondLon, lons[1]);

		Integer[] rowNums = cross.getRowNumsAtMin();
		assertEquals(2, rowNums.length);
		assertNull(rowNums[0]);
		assertNull(rowNums[1]);
		assertNull(cross.getMinDistance());
		String[] expos = cross.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		cross.setLonsAtMin(null);
		lons = cross.getLonsAtMin();
		assertEquals(2, lons.length);
		assertNull(lons[0]);
		assertNull(lons[1]);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#getLatsAtMin()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#setLatsAtMin(java.lang.Double[])}.
	 */
	@Test
	public void testGetSetLatsAtMin() {
		SocatCrossover cross = new SocatCrossover();
		Double[] lats = cross.getLatsAtMin();
		assertEquals(2, lats.length);
		assertNull(lats[0]);
		assertNull(lats[1]);

		cross.setLatsAtMin(new Double[] {firstLat, secondLat});
		lats = cross.getLatsAtMin();
		assertEquals(2, lats.length);
		assertEquals(firstLat, lats[0]);
		assertEquals(secondLat, lats[1]);

		Double[] lons = cross.getLonsAtMin();
		assertEquals(2, lons.length);
		assertNull(lons[0]);
		assertNull(lons[1]);
		Integer[] rowNums = cross.getRowNumsAtMin();
		assertEquals(2, rowNums.length);
		assertNull(rowNums[0]);
		assertNull(rowNums[1]);
		assertNull(cross.getMinDistance());
		String[] expos = cross.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		cross.setLatsAtMin(null);
		lats = cross.getLatsAtMin();
		assertEquals(2, lats.length);
		assertNull(lats[0]);
		assertNull(lats[1]);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#getTimesAtMin()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#setTimesAtMin(java.util.Date[])}.
	 */
	@Test
	public void testGetSetTimesAtMin() {
		SocatCrossover cross = new SocatCrossover();
		Date[] times = cross.getTimesAtMin();
		assertEquals(2, times.length);
		assertNull(times[0]);
		assertNull(times[1]);

		cross.setTimesAtMin(new Date[] {firstTime, secondTime});
		times = cross.getTimesAtMin();
		assertEquals(2, times.length);
		assertEquals(firstTime, times[0]);
		assertEquals(secondTime, times[1]);

		Double[] lats = cross.getLatsAtMin();
		assertEquals(2, lats.length);
		assertNull(lats[0]);
		assertNull(lats[1]);
		Double[] lons = cross.getLonsAtMin();
		assertEquals(2, lons.length);
		assertNull(lons[0]);
		assertNull(lons[1]);
		Integer[] rowNums = cross.getRowNumsAtMin();
		assertEquals(2, rowNums.length);
		assertNull(rowNums[0]);
		assertNull(rowNums[1]);
		assertNull(cross.getMinDistance());
		String[] expos = cross.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		cross.setTimesAtMin(null);
		times = cross.getTimesAtMin();
		assertEquals(2, times.length);
		assertNull(times[0]);
		assertNull(times[1]);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#getCruiseMinTimes()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#setCruiseMinTimes(java.util.Date[])}.
	 */
	@Test
	public void testGetCruiseMinTimes() {
		SocatCrossover cross = new SocatCrossover();
		Date[] minTimes = cross.getCruiseMinTimes();
		assertEquals(2, minTimes.length);
		assertNull(minTimes[0]);
		assertNull(minTimes[1]);

		cross.setCruiseMinTimes(new Date[] {firstMinTime, secondMinTime});
		minTimes = cross.getCruiseMinTimes();
		assertEquals(2, minTimes.length);
		assertEquals(firstMinTime, minTimes[0]);
		assertEquals(secondMinTime, minTimes[1]);

		Date[] times = cross.getTimesAtMin();
		assertEquals(2, times.length);
		assertNull(times[0]);
		assertNull(times[1]);
		Double[] lats = cross.getLatsAtMin();
		assertEquals(2, lats.length);
		assertNull(lats[0]);
		assertNull(lats[1]);
		Double[] lons = cross.getLonsAtMin();
		assertEquals(2, lons.length);
		assertNull(lons[0]);
		assertNull(lons[1]);
		Integer[] rowNums = cross.getRowNumsAtMin();
		assertEquals(2, rowNums.length);
		assertNull(rowNums[0]);
		assertNull(rowNums[1]);
		assertNull(cross.getMinDistance());
		String[] expos = cross.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		cross.setCruiseMinTimes(null);
		minTimes = cross.getCruiseMinTimes();
		assertEquals(2, minTimes.length);
		assertNull(minTimes[0]);
		assertNull(minTimes[1]);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#getCruiseMaxTimes()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#setCruiseMaxTimes(java.util.Date[])}.
	 */
	@Test
	public void testGetCruiseMaxTimes() {
		SocatCrossover cross = new SocatCrossover();
		Date[] maxTimes = cross.getCruiseMaxTimes();
		assertEquals(2, maxTimes.length);
		assertNull(maxTimes[0]);
		assertNull(maxTimes[1]);

		cross.setCruiseMaxTimes(new Date[] {firstMaxTime, secondMaxTime});
		maxTimes = cross.getCruiseMaxTimes();
		assertEquals(2, maxTimes.length);
		assertEquals(firstMaxTime, maxTimes[0]);
		assertEquals(secondMaxTime, maxTimes[1]);

		Date[] minTimes = cross.getCruiseMinTimes();
		assertEquals(2, minTimes.length);
		assertNull(minTimes[0]);
		assertNull(minTimes[1]);
		Date[] times = cross.getTimesAtMin();
		assertEquals(2, times.length);
		assertNull(times[0]);
		assertNull(times[1]);
		Double[] lats = cross.getLatsAtMin();
		assertEquals(2, lats.length);
		assertNull(lats[0]);
		assertNull(lats[1]);
		Double[] lons = cross.getLonsAtMin();
		assertEquals(2, lons.length);
		assertNull(lons[0]);
		assertNull(lons[1]);
		Integer[] rowNums = cross.getRowNumsAtMin();
		assertEquals(2, rowNums.length);
		assertNull(rowNums[0]);
		assertNull(rowNums[1]);
		assertNull(cross.getMinDistance());
		String[] expos = cross.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		cross.setCruiseMaxTimes(null);
		maxTimes = cross.getCruiseMaxTimes();
		assertEquals(2, maxTimes.length);
		assertNull(maxTimes[0]);
		assertNull(maxTimes[1]);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.SocatCrossover#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		SocatCrossover first = new SocatCrossover();
		assertFalse( first.equals(null) );
		assertFalse( first.equals(firstExpo) );

		SocatCrossover second = new SocatCrossover();
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setExpocodes(new String[] {firstExpo, secondExpo});
		assertFalse( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setExpocodes(new String[] {firstExpo, secondExpo});
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setMinDistance(new Double(minDist));
		// hashCode ignores floating-point values
		assertTrue( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setMinDistance(new Double(minDist));
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setRowNumsAtMin(new Integer[] {firstRowNum, secondRowNum});
		assertFalse( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setRowNumsAtMin(new Integer[] {firstRowNum, secondRowNum});
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setLonsAtMin(new Double[] {firstLon, secondLon});
		// hashCode ignores floating-point values
		assertTrue( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setLonsAtMin(new Double[] {firstLon, secondLon});
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setLatsAtMin(new Double[] {firstLat, secondLat});
		// hashCode ignores floating-point values
		assertTrue( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setLatsAtMin(new Double[] {firstLat, secondLat});
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setTimesAtMin(new Date[] {firstTime, secondTime});
		assertFalse( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setTimesAtMin(new Date[] {firstTime, secondTime});
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setCruiseMinTimes(new Date[] {firstMinTime, secondMinTime});
		assertFalse( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setCruiseMinTimes(new Date[] {firstMinTime, secondMinTime});
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setCruiseMaxTimes(new Date[] {firstMaxTime, secondMaxTime});
		assertFalse( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setCruiseMaxTimes(new Date[] {firstMaxTime, secondMaxTime});
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );
	}

}
