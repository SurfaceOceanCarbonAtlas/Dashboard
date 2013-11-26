/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;

import org.junit.Test;

/**
 * @author Karl Smith
 */
public class DashboardCruiseListTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#DashboardCruiseList()}.
	 */
	@Test
	public void testDashboardCruiseList() {
		DashboardCruiseList cruiseList = new DashboardCruiseList();
		assertNotNull(cruiseList);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#getUsername()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#setUsername(java.lang.String)}.
	 */
	@Test
	public void testSetGetUsername() {
		String myUsername = "SocatUser";
		DashboardCruiseList cruiseList = new DashboardCruiseList();
		assertEquals("", cruiseList.getUsername());
		cruiseList.setUsername(myUsername);
		assertEquals(myUsername, cruiseList.getUsername());
		assertEquals(0, cruiseList.size());
		cruiseList.setUsername(null);
		assertEquals("", cruiseList.getUsername());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		String myUsername = "SocatUser";
		String myExpocode = "ABCD20050728";
		DashboardCruise cruise = new DashboardCruise();
		cruise.setOwner(myUsername);
		cruise.setExpocode(myExpocode);
		DashboardCruise sameCruise = new DashboardCruise();
		sameCruise.setOwner(myUsername);
		sameCruise.setExpocode(myExpocode);
		DashboardCruise otherCruise = new DashboardCruise();
		otherCruise.setOwner(myUsername);
		otherCruise.setExpocode("XXXX20030918");

		DashboardCruiseList firstList = new DashboardCruiseList();
		assertFalse( firstList.equals(null) );
		assertFalse( firstList.equals(cruise) );
		DashboardCruiseList secondList = new DashboardCruiseList();
		assertEquals(firstList.hashCode(), secondList.hashCode());
		assertEquals(firstList, secondList);

		firstList.setUsername(myUsername);
		assertTrue( firstList.hashCode() != secondList.hashCode() );
		assertFalse( firstList.equals(secondList) );
		secondList.setUsername(myUsername);
		assertEquals(firstList.hashCode(), secondList.hashCode());
		assertEquals(firstList, secondList);

		firstList.put(cruise.getExpocode(), cruise);
		assertTrue( firstList.hashCode() != secondList.hashCode() );
		assertFalse( firstList.equals(secondList) );
		secondList.put(sameCruise.getExpocode(), sameCruise);
		assertEquals(firstList.hashCode(), secondList.hashCode());
		assertEquals(firstList, secondList);

		firstList.put(otherCruise.getExpocode(), otherCruise);
		secondList.clear();
		secondList.put(otherCruise.getExpocode(), otherCruise);
		secondList.put(sameCruise.getExpocode(), sameCruise);
		assertEquals(firstList.hashCode(), secondList.hashCode());
		assertEquals(firstList, secondList);
	}

}
