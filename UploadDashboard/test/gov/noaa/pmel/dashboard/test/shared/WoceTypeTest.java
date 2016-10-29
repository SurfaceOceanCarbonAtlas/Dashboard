/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.*;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.WoceType;

import org.junit.Test;

/**
 * Unit tests for methods in {@link gov.noaa.pmel.dashboard.shared.WoceType}
 * 
 * @author Karl Smith
 */
public class WoceTypeTest {

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceType#getColumnIndex()} and
	 * {@link gov.noaa.pmel.dashboard.shared.WoceType#setColumnIndex(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetColumnIndex() {
		final Integer myColumnIndex = 5;
		WoceType uwoce = new WoceType();
		assertEquals(DashboardUtils.INT_MISSING_VALUE, uwoce.getColumnIndex());
		uwoce.setColumnIndex(myColumnIndex);
		assertEquals(myColumnIndex, uwoce.getColumnIndex());
		uwoce.setColumnIndex(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, uwoce.getColumnIndex());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceType#getRowIndex()} and
	 * {@link gov.noaa.pmel.dashboard.shared.WoceType#setRowIndex(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetRowIndex() {
		final Integer myRowIndex = 25;
		WoceType uwoce = new WoceType();
		assertEquals(DashboardUtils.INT_MISSING_VALUE, uwoce.getRowIndex());
		uwoce.setRowIndex(myRowIndex);
		assertEquals(myRowIndex, uwoce.getRowIndex());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, uwoce.getColumnIndex());
		uwoce.setRowIndex(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, uwoce.getRowIndex());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceType#getWoceName()} and
	 * {@link gov.noaa.pmel.dashboard.shared.WoceType#setWoceName(java.lang.String)}.
	 */
	@Test
	public void testGetSetWoceName() {
		final String myWoceName = "WOCE_CO2_atm";
		WoceType uwoce = new WoceType();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, uwoce.getWoceName());
		uwoce.setWoceName(myWoceName);
		assertEquals(myWoceName, uwoce.getWoceName());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, uwoce.getRowIndex());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, uwoce.getColumnIndex());
		uwoce.setWoceName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, uwoce.getWoceName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceType#UserWoce(java.lang.Integer, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public void testUserWoceIntegerString() {
		final Integer myColumnIndex = 5;
		final Integer myRowIndex = 25;
		final String myWoceName = "WOCE_CO2_atm";
		WoceType uwoce = new WoceType(myColumnIndex, myRowIndex, myWoceName);
		assertEquals(myColumnIndex, uwoce.getColumnIndex());
		assertEquals(myRowIndex, uwoce.getRowIndex());
		assertEquals(myWoceName, uwoce.getWoceName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceType#compareTo(gov.noaa.pmel.dashboard.shared.WoceType)}.
	 */
	@Test
	public void testCompareTo() {
		WoceType first = new WoceType(5, 25, "WOCE_CO2_atm");
		WoceType second = new WoceType(5, 25, "WOCE_CO2_atm");
		assertTrue( first.compareTo(second) == 0 );
		assertTrue( second.compareTo(first) == 0 );
		assertFalse( first == second );

		second = new WoceType(6, 25, "WOCE_CO2_atm");
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );
		second = new WoceType(5, 35, "WOCE_CO2_atm");
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );
		second = new WoceType(5, 25, "WOCE_CO2_water");
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );
		second = new WoceType(4, 35, "WOCE_CO2_water");
		assertTrue( first.compareTo(second) > 0 );
		assertTrue( second.compareTo(first) < 0 );
		second = new WoceType(5, 15, "WOCE_CO2_water");
		assertTrue( first.compareTo(second) > 0 );
		assertTrue( second.compareTo(first) < 0 );
		second = new WoceType(4, 25, "WOCE_CO2_water");
		assertTrue( first.compareTo(second) > 0 );
		assertTrue( second.compareTo(first) < 0 );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceType#hashCode()} and
	 * {@link gov.noaa.pmel.dashboard.shared.WoceType#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		WoceType first = new WoceType();
		assertFalse( first.equals(null) );
		assertFalse( first.equals(DashboardUtils.INT_MISSING_VALUE) );
		assertFalse( first.equals(DashboardUtils.STRING_MISSING_VALUE) );

		WoceType second = new WoceType();
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );
		assertFalse( first == second );

		first.setColumnIndex(5);
		assertFalse( first.hashCode() == second.hashCode() );
		assertFalse( first.equals(second) );
		second.setColumnIndex(5);
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );

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
