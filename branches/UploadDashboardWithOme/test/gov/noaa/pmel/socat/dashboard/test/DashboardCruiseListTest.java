/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;

import org.junit.Test;

/**
 * @author Karl Smith
 */
public class DashboardCruiseListTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#getUsername()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#setUsername(java.lang.String)}.
	 */
	@Test
	public void testGetSetUsername() {
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
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#getSocatVersion()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#setSocatVersion(java.lang.String)}.
	 */
	@Test
	public void testGetSetSocatVersion() {
		String mySocatVersion = "4";
		DashboardCruiseList cruiseList = new DashboardCruiseList();
		assertEquals("", cruiseList.getSocatVersion());
		cruiseList.setSocatVersion(mySocatVersion);
		assertEquals(mySocatVersion, cruiseList.getSocatVersion());
		assertEquals("", cruiseList.getUsername());
		assertEquals(0, cruiseList.size());
		cruiseList.setSocatVersion(null);
		assertEquals("", cruiseList.getSocatVersion());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#isManager()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#setManager(boolean)}.
	 */
	@Test
	public void testIsSetManager() {
		DashboardCruiseList cruiseList = new DashboardCruiseList();
		assertFalse( cruiseList.isManager());
		cruiseList.setManager(true);
		assertTrue( cruiseList.isManager());
		assertEquals("", cruiseList.getSocatVersion());
		assertEquals("", cruiseList.getUsername());
		assertEquals(0, cruiseList.size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		String myUsername = "SocatUser";
		String mySocatVersion = "4";
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
		assertTrue( firstList.hashCode() == secondList.hashCode() );
		assertTrue( firstList.equals(secondList) );

		firstList.setUsername(myUsername);
		assertFalse( firstList.hashCode() == secondList.hashCode() );
		assertFalse( firstList.equals(secondList) );
		secondList.setUsername(myUsername);
		assertTrue( firstList.hashCode() == secondList.hashCode() );
		assertTrue( firstList.equals(secondList) );

		firstList.setSocatVersion(mySocatVersion);
		assertFalse( firstList.hashCode() == secondList.hashCode() );
		assertFalse( firstList.equals(secondList) );
		secondList.setSocatVersion(mySocatVersion);
		assertTrue( firstList.hashCode() == secondList.hashCode() );
		assertTrue( firstList.equals(secondList) );

		firstList.setManager(true);
		assertFalse( firstList.hashCode() == secondList.hashCode() );
		assertFalse( firstList.equals(secondList) );
		secondList.setManager(true);
		assertTrue( firstList.hashCode() == secondList.hashCode() );
		assertTrue( firstList.equals(secondList) );

		firstList.put(cruise.getExpocode(), cruise);
		assertTrue( firstList.hashCode() != secondList.hashCode() );
		assertFalse( firstList.equals(secondList) );
		secondList.put(sameCruise.getExpocode(), sameCruise);
		assertTrue( firstList.hashCode() == secondList.hashCode() );
		assertTrue( firstList.equals(secondList) );

		firstList.put(otherCruise.getExpocode(), otherCruise);
		secondList.clear();
		secondList.put(otherCruise.getExpocode(), otherCruise);
		secondList.put(sameCruise.getExpocode(), sameCruise);
		assertTrue( firstList.hashCode() == secondList.hashCode() );
		assertTrue( firstList.equals(secondList) );
	}

}
