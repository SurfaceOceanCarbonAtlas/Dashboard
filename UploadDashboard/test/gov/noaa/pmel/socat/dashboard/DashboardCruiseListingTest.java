/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;

import java.util.ArrayList;

import org.junit.Test;

/**
 * @author Karl Smith
 */
public class DashboardCruiseListingTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing#DashboardCruiseListing()}.
	 */
	@Test
	public void testDashboardCruiseListing() {
		DashboardCruiseListing cruiseList = new DashboardCruiseListing();
		assertNotNull(cruiseList);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing#DashboardCruiseListing(java.lang.String, java.util.ArrayList)}.
	 */
	@Test
	public void testDashboardCruiseListingStringArrayListOfDashboardCruise() {
		String myUsername = "SocatUser";
		ArrayList<DashboardCruise> myCruises = new ArrayList<DashboardCruise>();
		myCruises.add(new DashboardCruise());
		DashboardCruiseListing cruiseList = new DashboardCruiseListing(myUsername, myCruises);
		assertNotNull(cruiseList);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing#getUsername()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing#setUsername(java.lang.String)}.
	 */
	@Test
	public void testSetGetUsername() {
		String myUsername = "SocatUser";
		DashboardCruiseListing cruiseList = new DashboardCruiseListing();
		assertNull( cruiseList.getUsername() );
		cruiseList.setUsername(myUsername);
		assertEquals(myUsername, cruiseList.getUsername());
		cruiseList.setUsername(null);
		assertNull( cruiseList.getUsername() );
		cruiseList = new DashboardCruiseListing(myUsername, new ArrayList<DashboardCruise>());
		assertEquals(myUsername, cruiseList.getUsername());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing#getCruises()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing#setCruises(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetCruises() {
		ArrayList<DashboardCruise> myCruises = new ArrayList<DashboardCruise>();
		DashboardCruise myCruise = new DashboardCruise();
		myCruise.setExpocode("ABCD20050728");
		myCruises.add(myCruise);
		DashboardCruiseListing cruiseList = new DashboardCruiseListing();
		assertNull( cruiseList.getCruises() );
		cruiseList.setCruises(myCruises);
		assertEquals(1, cruiseList.getCruises().size());
		assertEquals(myCruise, cruiseList.getCruises().get(0));
		assertNull( cruiseList.getUsername() );
		cruiseList.setCruises(null);
		assertNull( cruiseList.getCruises() );
		String myUsername = "SocatUser";
		cruiseList = new DashboardCruiseListing(myUsername, myCruises);
		assertEquals(myCruises, cruiseList.getCruises());
		assertEquals(myUsername, cruiseList.getUsername());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		String myUsername = "SocatUser";
		String myExpocode = "ABCD20050728";
		ArrayList<DashboardCruise> firstCruises = new ArrayList<DashboardCruise>();
		DashboardCruise myCruise = new DashboardCruise();
		myCruise.setExpocode(myExpocode);
		firstCruises.add(myCruise);
		ArrayList<DashboardCruise> secondCruises = new ArrayList<DashboardCruise>();
		myCruise = new DashboardCruise();
		myCruise.setExpocode(myExpocode);
		secondCruises.add(myCruise);
		ArrayList<DashboardCruise> otherCruises = new ArrayList<DashboardCruise>();
		myCruise = new DashboardCruise();
		myCruise.setExpocode("XXXX20030918");
		otherCruises.add(myCruise);

		DashboardCruiseListing firstListing = new DashboardCruiseListing();
		assertFalse( firstListing.equals(null) );
		assertFalse( firstListing.equals(firstCruises) );
		DashboardCruiseListing secondListing = new DashboardCruiseListing();
		assertEquals(firstListing.hashCode(), secondListing.hashCode());
		assertEquals(firstListing, secondListing);

		firstListing.setUsername(myUsername);
		assertTrue( firstListing.hashCode() != secondListing.hashCode() );
		assertFalse( firstListing.equals(secondListing) );
		secondListing.setUsername(myUsername);
		assertEquals(firstListing.hashCode(), secondListing.hashCode());
		assertEquals(firstListing, secondListing);

		firstListing.setCruises(firstCruises);
		assertTrue( firstListing.hashCode() != secondListing.hashCode() );
		assertFalse( firstListing.equals(secondListing) );
		secondListing.setCruises(secondCruises);
		assertEquals(firstListing.hashCode(), secondListing.hashCode());
		assertEquals(firstListing, secondListing);
		secondListing.setCruises(otherCruises);
		assertTrue( firstListing.hashCode() != secondListing.hashCode() );
		assertFalse( firstListing.equals(secondListing) );
	}

}
