/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.noaa.pmel.dashboard.shared.Overlap;

/**
 * Unit tests for {@link gov.noaa.pmel.dashboard.shared.Overlap}
 * 
 * @author Karl Smith
 */
public class OverlapTest {

	private static final String firstExpo = "AAAA19951201";
	private static final String secondExpo = "BBBB19951207";
	private static final Integer[] firstRowNums = new Integer[] { 47, 48, 49, 50, 51 };
	private static final Integer[] secondRowNums = new Integer[] { 1, 2, 3, 4, 5 };
	private static final Double[] lons = new Double[] { 125.25, 125.50, 125.75, 126.00, 126.25 };
	private static final Double[] lats = new Double[] { 45.35, 45.30, 45.25, 45.30, 45.35 };
	private static final Long[] times = new Long[]  { 987654321L, 987754321L, 987854321L, 987954321L, 988054321L };

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#getExpocodes()} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#setExpocodes(java.lang.String[])}.
	 */
	@Test
	public void testGetSetExpocodes() {
		Overlap olap = new Overlap();
		String[] expos = olap.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		olap.setExpocodes(new String[] {firstExpo, secondExpo});
		expos = olap.getExpocodes();
		assertEquals(2, expos.length);
		assertEquals(firstExpo, expos[0]);
		assertEquals(secondExpo, expos[1]);

		olap.setExpocodes(null);
		expos = olap.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#getRowNums()} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#setRowNums(java.lang.Integer[][])}.
	 */
	@Test
	public void testGetSetRowNums() {
		Overlap olap = new Overlap();
		Integer[][] rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertEquals(0, rowNums[0].length);
		assertEquals(0, rowNums[1].length);

		olap.setRowNums(new Integer[][] { firstRowNums, secondRowNums });
		rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertArrayEquals(firstRowNums, rowNums[0]);
		assertArrayEquals(secondRowNums, rowNums[1]);

		String[] expos = olap.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		olap.setRowNums(null);
		rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertEquals(0, rowNums[0].length);
		assertEquals(0, rowNums[1].length);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#getLons()} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#setLons(java.lang.Double[])}.
	 */
	@Test
	public void testGetSetLons() {
		Overlap olap = new Overlap();
		Double[] mylons = olap.getLons();
		assertEquals(0, mylons.length);

		olap.setLons(lons);
		mylons = olap.getLons();
		assertArrayEquals(lons, mylons);

		Integer[][] rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertEquals(0, rowNums[0].length);
		assertEquals(0, rowNums[1].length);

		String[] expos = olap.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		olap.setLons(null);
		mylons = olap.getLons();
		assertEquals(0, mylons.length);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#getLats()} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#setLats(java.lang.Double[])}.
	 */
	@Test
	public void testGetSetLats() {
		Overlap olap = new Overlap();
		Double[] mylats = olap.getLats();
		assertEquals(0, mylats.length);

		olap.setLats(lats);
		mylats = olap.getLats();
		assertArrayEquals(lats, mylats);

		Double[] mylons = olap.getLons();
		assertEquals(0, mylons.length);

		Integer[][] rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertEquals(0, rowNums[0].length);
		assertEquals(0, rowNums[1].length);

		String[] expos = olap.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		olap.setLats(null);
		mylats = olap.getLats();
		assertEquals(0, mylats.length);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#getTimes()} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#setTimes(java.lang.Long[])}.
	 */
	@Test
	public void testGetSetTimes() {
		Overlap olap = new Overlap();
		Long[] mytimes = olap.getTimes();
		assertEquals(0, mytimes.length);

		olap.setTimes(times);
		mytimes = olap.getTimes();
		assertArrayEquals(times, mytimes);

		Double[] mylats = olap.getLats();
		assertEquals(0, mylats.length);

		Double[] mylons = olap.getLons();
		assertEquals(0, mylons.length);

		Integer[][] rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertEquals(0, rowNums[0].length);
		assertEquals(0, rowNums[1].length);

		String[] expos = olap.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		olap.setTimes(null);
		mytimes = olap.getTimes();
		assertEquals(0, mytimes.length);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#hashCode()} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#equals(java.lang.Object)}.
	 * 
	 */
	@Test
	public void testHashCodeEqualsObject() {
		Overlap first = new Overlap();
		assertFalse( first.equals(null) );
		assertFalse( first.equals(firstExpo) );

		Overlap second = new Overlap();
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setExpocodes(new String[] {firstExpo, secondExpo});
		assertFalse( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setExpocodes(new String[] {firstExpo, secondExpo});
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setRowNums(new Integer[][] { firstRowNums, secondRowNums });
		assertFalse( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setRowNums(new Integer[][] { firstRowNums, secondRowNums });
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setLons(lons);
		// hashCode ignores floating-point values but pays attention to the number of values
		assertFalse( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setLons(lons);
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setLats(lats);
		// hashCode ignores floating-point values but pays attention to the number of values
		assertFalse( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setLats(lats);
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

		first.setTimes(times);
		assertFalse( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setTimes(times);
		assertTrue( first.hashCode() == second.hashCode());
		assertTrue( first.equals(second) );

	}

}
