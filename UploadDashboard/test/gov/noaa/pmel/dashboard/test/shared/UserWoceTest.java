/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.*;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.UserWoce;

import org.junit.Test;

/**
 * Unit tests for methods in {@link gov.noaa.pmel.dashboard.shared.UserWoce}
 * 
 * @author Karl Smith
 */
public class UserWoceTest {

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.UserWoce#getRowIndex()} and
	 * {@link gov.noaa.pmel.dashboard.shared.UserWoce#setRowIndex(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetRowIndex() {
		final Integer myRowIndex = 25;
		UserWoce uwoce = new UserWoce();
		assertEquals(DashboardUtils.INT_MISSING_VALUE, uwoce.getRowIndex());
		uwoce.setRowIndex(myRowIndex);
		assertEquals(myRowIndex, uwoce.getRowIndex());
		uwoce.setRowIndex(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, uwoce.getRowIndex());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.UserWoce#getWoceName()} and
	 * {@link gov.noaa.pmel.dashboard.shared.UserWoce#setWoceName(java.lang.String)}.
	 */
	@Test
	public void testGetSetWoceName() {
		final String myWoceName = "WOCE_CO2_atm";
		UserWoce uwoce = new UserWoce();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, uwoce.getWoceName());
		uwoce.setWoceName(myWoceName);
		assertEquals(myWoceName, uwoce.getWoceName());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, uwoce.getRowIndex());
		uwoce.setWoceName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, uwoce.getWoceName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.UserWoce#UserWoce(java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public void testUserWoceIntegerString() {
		final Integer myRowIndex = 25;
		final String myWoceName = "WOCE_CO2_atm";
		UserWoce uwoce = new UserWoce(myRowIndex, myWoceName);
		assertEquals(myRowIndex, uwoce.getRowIndex());
		assertEquals(myWoceName, uwoce.getWoceName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.UserWoce#compareTo(gov.noaa.pmel.dashboard.shared.UserWoce)}.
	 */
	@Test
	public void testCompareTo() {
		UserWoce first = new UserWoce(25, "WOCE_CO2_atm");
		UserWoce second = new UserWoce(35, "WOCE_CO2_water");
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );
		second = new UserWoce(25, "WOCE_CO2_water");
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );
		second = new UserWoce(35, "WOCE_CO2_atm");
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );
		second = new UserWoce(35, "AAAAA");
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );
		second = new UserWoce(25, "AAAAA");
		assertTrue( first.compareTo(second) > 0 );
		assertTrue( second.compareTo(first) < 0 );
		second = new UserWoce(25, "WOCE_CO2_atm");
		assertTrue( first.compareTo(second) == 0 );
		assertTrue( second.compareTo(first) == 0 );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.UserWoce#hashCode()} and
	 * {@link gov.noaa.pmel.dashboard.shared.UserWoce#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		UserWoce first = new UserWoce();
		assertFalse( first.equals(null) );
		assertFalse( first.equals(DashboardUtils.INT_MISSING_VALUE) );
		assertFalse( first.equals(DashboardUtils.STRING_MISSING_VALUE) );

		UserWoce second = new UserWoce();
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );
		assertFalse( first == second );

		first.setRowIndex(25);
		assertFalse( first.hashCode() == second.hashCode() );
		assertFalse( first.equals(second) );
		second.setRowIndex(25);
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );

		first.setWoceName("WOCE_CO2_water");
		assertFalse( first.hashCode() == second.hashCode() );
		assertFalse( first.equals(second) );
		second.setWoceName("WOCE_CO2_water");
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );
	}

}
