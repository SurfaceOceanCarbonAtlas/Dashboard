/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

/**
 * @author Karl Smith
 */
public class DashboardDatasetListTest {

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetList#getUsername()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetList#setUsername(java.lang.String)}.
	 */
	@Test
	public void testGetSetUsername() {
		String myUsername = "SocatUser";
		DashboardDatasetList cruiseList = new DashboardDatasetList();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseList.getUsername());
		cruiseList.setUsername(myUsername);
		assertEquals(myUsername, cruiseList.getUsername());
		assertEquals(0, cruiseList.size());
		cruiseList.setUsername(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseList.getUsername());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetList#isManager()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetList#setManager(boolean)}.
	 */
	@Test
	public void testIsSetManager() {
		DashboardDatasetList cruiseList = new DashboardDatasetList();
		assertFalse( cruiseList.isManager());
		cruiseList.setManager(true);
		assertTrue( cruiseList.isManager());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruiseList.getUsername());
		assertEquals(0, cruiseList.size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetList#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDatasetList#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		String myUsername = "SocatUser";
		String myExpocode = "ABCD20050728";
		DashboardDataset cruise = new DashboardDataset();
		cruise.setOwner(myUsername);
		cruise.setDatasetId(myExpocode);
		DashboardDataset sameCruise = new DashboardDataset();
		sameCruise.setOwner(myUsername);
		sameCruise.setDatasetId(myExpocode);
		DashboardDataset otherCruise = new DashboardDataset();
		otherCruise.setOwner(myUsername);
		otherCruise.setDatasetId("XXXX20030918");

		DashboardDatasetList firstList = new DashboardDatasetList();
		assertFalse( firstList.equals(null) );
		assertFalse( firstList.equals(cruise) );
		DashboardDatasetList secondList = new DashboardDatasetList();
		assertTrue( firstList.hashCode() == secondList.hashCode() );
		assertTrue( firstList.equals(secondList) );

		firstList.setUsername(myUsername);
		assertFalse( firstList.hashCode() == secondList.hashCode() );
		assertFalse( firstList.equals(secondList) );
		secondList.setUsername(myUsername);
		assertTrue( firstList.hashCode() == secondList.hashCode() );
		assertTrue( firstList.equals(secondList) );

		firstList.setManager(true);
		assertFalse( firstList.hashCode() == secondList.hashCode() );
		assertFalse( firstList.equals(secondList) );
		secondList.setManager(true);
		assertTrue( firstList.hashCode() == secondList.hashCode() );
		assertTrue( firstList.equals(secondList) );

		firstList.put(cruise.getDatasetId(), cruise);
		assertTrue( firstList.hashCode() != secondList.hashCode() );
		assertFalse( firstList.equals(secondList) );
		secondList.put(sameCruise.getDatasetId(), sameCruise);
		assertTrue( firstList.hashCode() == secondList.hashCode() );
		assertTrue( firstList.equals(secondList) );

		firstList.put(otherCruise.getDatasetId(), otherCruise);
		secondList.clear();
		secondList.put(otherCruise.getDatasetId(), otherCruise);
		secondList.put(sameCruise.getDatasetId(), sameCruise);
		assertTrue( firstList.hashCode() == secondList.hashCode() );
		assertTrue( firstList.equals(secondList) );
	}

}
