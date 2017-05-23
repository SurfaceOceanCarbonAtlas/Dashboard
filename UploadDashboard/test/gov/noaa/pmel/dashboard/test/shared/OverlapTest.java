/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

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
	private static final ArrayList<Integer> firstRowNums = new ArrayList<Integer>(Arrays.asList(47, 48, 49, 50, 51));
	private static final ArrayList<Integer> secondRowNums = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
	private static final ArrayList<Double> lons = new ArrayList<Double>(Arrays.asList(125.25, 125.50, 125.75, 126.00, 126.25));
	private static final ArrayList<Double> lats = new ArrayList<Double>(Arrays.asList(45.35, 45.30, 45.25, 45.30, 45.35));
	private static final ArrayList<Long> times = new ArrayList<Long>(Arrays.asList(987654321L, 987754321L, 987854321L, 987954321L, 988054321L));

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#getExpocodes} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#setExpocodes}.
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
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#getRowNums} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#setRowNums}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetSetRowNums() {
		Overlap olap = new Overlap();
		ArrayList<Integer>[] rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertEquals(0, rowNums[0].size());
		assertEquals(0, rowNums[1].size());

		olap.setRowNums(new ArrayList[] { firstRowNums, secondRowNums });
		rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertEquals(firstRowNums, rowNums[0]);
		assertEquals(secondRowNums, rowNums[1]);

		String[] expos = olap.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		olap.setRowNums(null);
		rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertEquals(0, rowNums[0].size());
		assertEquals(0, rowNums[1].size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#getLons} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#setLons}.
	 */
	@Test
	public void testGetSetLons() {
		Overlap olap = new Overlap();
		ArrayList<Double> mylons = olap.getLons();
		assertEquals(0, mylons.size());

		olap.setLons(lons);
		mylons = olap.getLons();
		assertEquals(lons, mylons);

		ArrayList<Integer>[] rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertEquals(0, rowNums[0].size());
		assertEquals(0, rowNums[1].size());

		String[] expos = olap.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		olap.setLons(null);
		mylons = olap.getLons();
		assertEquals(0, mylons.size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#getLats} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#setLats}.
	 */
	@Test
	public void testGetSetLats() {
		Overlap olap = new Overlap();
		ArrayList<Double> mylats = olap.getLats();
		assertEquals(0, mylats.size());

		olap.setLats(lats);
		mylats = olap.getLats();
		assertEquals(lats, mylats);

		ArrayList<Double> mylons = olap.getLons();
		assertEquals(0, mylons.size());

		ArrayList<Integer>[] rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertEquals(0, rowNums[0].size());
		assertEquals(0, rowNums[1].size());

		String[] expos = olap.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		olap.setLats(null);
		mylats = olap.getLats();
		assertEquals(0, mylats.size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#getTimes} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#setTimes}.
	 */
	@Test
	public void testGetSetTimes() {
		Overlap olap = new Overlap();
		ArrayList<Long> mytimes = olap.getTimes();
		assertEquals(0, mytimes.size());

		olap.setTimes(times);
		mytimes = olap.getTimes();
		assertEquals(times, mytimes);

		ArrayList<Double> mylats = olap.getLats();
		assertEquals(0, mylats.size());

		ArrayList<Double> mylons = olap.getLons();
		assertEquals(0, mylons.size());

		ArrayList<Integer>[] rowNums = olap.getRowNums();
		assertEquals(2, rowNums.length);
		assertEquals(0, rowNums[0].size());
		assertEquals(0, rowNums[1].size());

		String[] expos = olap.getExpocodes();
		assertEquals(2, expos.length);
		assertNull(expos[0]);
		assertNull(expos[1]);

		olap.setTimes(null);
		mytimes = olap.getTimes();
		assertEquals(0, mytimes.size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#hashCode()} and 
	 * {@link gov.noaa.pmel.dashboard.shared.Overlap#equals(java.lang.Object)}.
	 * 
	 */
	@SuppressWarnings("unchecked")
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

		first.setRowNums(new ArrayList[] { firstRowNums, secondRowNums });
		assertFalse( first.hashCode() == second.hashCode());
		assertFalse( first.equals(second) );
		second.setRowNums(new ArrayList[] { firstRowNums, secondRowNums });
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

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.Overlap#addDuplicatePoint}
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAddDuplicatePoint() {
		Overlap first = new Overlap();
		first.setExpocodes(new String[] {firstExpo, secondExpo});
		first.setRowNums(new ArrayList[] { firstRowNums, secondRowNums });
		first.setLats(lats);
		first.setLons(lons);
		first.setTimes(times);

		Overlap second = new Overlap();
		second.setExpocodes(new String[] {firstExpo, secondExpo});
		for (int k = 0; k < firstRowNums.size(); k++) {
			second.addDuplicatePoint(firstRowNums.get(k), secondRowNums.get(k), 
					lons.get(k), lats.get(k), times.get(k));
		}

		assertEquals(first, second);
	}

}
