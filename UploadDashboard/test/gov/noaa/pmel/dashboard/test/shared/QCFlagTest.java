/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCFlag;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

/**
 * Unit tests for methods of {@link gov.noaa.pmel.dashboard.shared.QCFlag}
 * 
 * @author Karl Smith
 */
public class QCFlagTest {

	private static final String MY_FLAG_NAME = "WOCE_CO2_atm";
	private static final Character MY_FLAG_VALUE = '3';
	private static final Severity MY_SEVERITY = Severity.QUESTIONABLE;
	private static final Integer MY_COLUMN_INDEX = 5;
	private static final Integer MY_ROW_INDEX = 15;
	private static final String MY_COMMENT = "my comment";

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCFlag#getFlagName()} and
	 * {@link gov.noaa.pmel.dashboard.shared.QCFlag#setFlagName(java.lang.String)}.
	 */
	@Test
	public void testGetSetFlagName() {
		QCFlag flag = new QCFlag();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
		flag.setFlagName(MY_FLAG_NAME);
		assertEquals(MY_FLAG_NAME, flag.getFlagName());
		flag.setFlagName(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCFlag#getFlagValue()} and
	 * {@link gov.noaa.pmel.dashboard.shared.QCFlag#setFlagValue(java.lang.Character)}.
	 */
	@Test
	public void testGetSetFlagValue() {
		QCFlag flag = new QCFlag();
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, flag.getFlagValue());
		flag.setFlagValue(MY_FLAG_VALUE);
		assertEquals(MY_FLAG_VALUE, flag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
		flag.setFlagValue(null);
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, flag.getFlagValue());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCFlag#getSeverity()} and
	 * {@link gov.noaa.pmel.dashboard.shared.QCFlag#setSeverity(gov.noaa.pmel.dashboard.shared.QCFlag.Severity)}.
	 */
	@Test
	public void testGetSetSeverity() {
		QCFlag flag = new QCFlag();
		assertEquals(Severity.UNASSIGNED, flag.getSeverity());
		flag.setSeverity(MY_SEVERITY);
		assertEquals(MY_SEVERITY, flag.getSeverity());
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, flag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
		flag.setSeverity(null);
		assertEquals(Severity.UNASSIGNED, flag.getSeverity());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCFlag#getColumnIndex()} and
	 * {@link gov.noaa.pmel.dashboard.shared.QCFlag#setColumnIndex(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetColumnIndex() {
		QCFlag flag = new QCFlag();
		assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getColumnIndex());
		flag.setColumnIndex(MY_COLUMN_INDEX);
		assertEquals(MY_COLUMN_INDEX, flag.getColumnIndex());
		assertEquals(Severity.UNASSIGNED, flag.getSeverity());
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, flag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
		flag.setColumnIndex(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getColumnIndex());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCFlag#getRowIndex()} and
	 * {@link gov.noaa.pmel.dashboard.shared.QCFlag#setRowIndex(java.lang.Integer)}.
	 */
	@Test
	public void testGetSetRowIndex() {
		QCFlag flag = new QCFlag();
		assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getRowIndex());
		flag.setRowIndex(MY_ROW_INDEX);
		assertEquals(MY_ROW_INDEX, flag.getRowIndex());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getColumnIndex());
		assertEquals(Severity.UNASSIGNED, flag.getSeverity());
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, flag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
		flag.setRowIndex(null);
		assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getRowIndex());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCFlag#getComment()} and
	 * {@link gov.noaa.pmel.dashboard.shared.QCFlag#setComment(java.lang.String)}.
	 */
	@Test
	public void testGetSetComment() {
		QCFlag flag = new QCFlag();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getComment());
		flag.setComment(MY_COMMENT);
		assertEquals(MY_COMMENT, flag.getComment());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getRowIndex());
		assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getColumnIndex());
		assertEquals(Severity.UNASSIGNED, flag.getSeverity());
		assertEquals(DashboardUtils.CHAR_MISSING_VALUE, flag.getFlagValue());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
		flag.setComment(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getComment());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCFlag#QCFlag(
	 * java.lang.String, java.lang.Character, gov.noaa.pmel.dashboard.shared.QCFlag.Severity, 
	 * java.lang.Integer, java.lang.Integer)}.
	 */
	@Test
	public void testWoceFlagStringIntegerInteger() {
		QCFlag flag = new QCFlag(MY_FLAG_NAME, MY_FLAG_VALUE, MY_SEVERITY, MY_COLUMN_INDEX, MY_ROW_INDEX);
		assertEquals(MY_FLAG_NAME, flag.getFlagName());
		assertEquals(MY_FLAG_VALUE, flag.getFlagValue());
		assertEquals(MY_SEVERITY, flag.getSeverity());
		assertEquals(MY_COLUMN_INDEX, flag.getColumnIndex());
		assertEquals(MY_ROW_INDEX, flag.getRowIndex());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getComment());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCFlag#hashCode()} and
	 * {@link gov.noaa.pmel.dashboard.shared.QCFlag#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		QCFlag first = new QCFlag();
		assertFalse( first.equals(null) );
		assertFalse( first.equals(MY_FLAG_NAME) );

		QCFlag second = new QCFlag();
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );

		first.setFlagName(MY_FLAG_NAME);
		assertFalse( first.hashCode() == second.hashCode() );
		assertFalse( first.equals(second) );
		second.setFlagName(MY_FLAG_NAME);
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );

		first.setFlagValue(MY_FLAG_VALUE);
		assertFalse( first.hashCode() == second.hashCode() );
		assertFalse( first.equals(second) );
		second.setFlagValue(MY_FLAG_VALUE);
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );

		first.setSeverity(MY_SEVERITY);
		assertFalse( first.hashCode() == second.hashCode() );
		assertFalse( first.equals(second) );
		second.setSeverity(MY_SEVERITY);
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );

		first.setColumnIndex(MY_COLUMN_INDEX);
		assertFalse( first.hashCode() == second.hashCode() );
		assertFalse( first.equals(second) );
		second.setColumnIndex(MY_COLUMN_INDEX);
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );

		first.setRowIndex(MY_ROW_INDEX);
		assertFalse( first.hashCode() == second.hashCode() );
		assertFalse( first.equals(second) );
		second.setRowIndex(MY_ROW_INDEX);
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );

		first.setComment(MY_COMMENT);
		assertFalse( first.hashCode() == second.hashCode() );
		assertFalse( first.equals(second) );
		second.setComment(MY_COMMENT);
		assertTrue( first.hashCode() == second.hashCode() );
		assertTrue( first.equals(second) );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.QCFlag#compareTo(gov.noaa.pmel.dashboard.shared.QCFlag)}.
	 */
	@Test
	public void testCompareTo() {
		QCFlag first = new QCFlag("WOCE_CO2_atm", '3', Severity.QUESTIONABLE, 5, 25);
		first.setComment("BBBB");

		QCFlag second = new QCFlag("WOCE_CO2_water", '2', Severity.ACCEPTABLE, 4, 15);
		second.setComment("AAAA");
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );

		second.setFlagName("WOCE_CO2_atm");
		assertTrue( first.compareTo(second) > 0 );
		assertTrue( second.compareTo(first) < 0 );
		second.setFlagValue('4');
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );

		second.setFlagValue('3');
		assertTrue( first.compareTo(second) > 0 );
		assertTrue( second.compareTo(first) < 0 );
		second.setSeverity(Severity.BAD);
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );

		second.setSeverity(Severity.QUESTIONABLE);
		assertTrue( first.compareTo(second) > 0 );
		assertTrue( second.compareTo(first) < 0 );
		second.setColumnIndex(6);
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );

		second.setColumnIndex(5);
		assertTrue( first.compareTo(second) > 0 );
		assertTrue( second.compareTo(first) < 0 );
		second.setComment("CCCC");
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );

		second.setComment("BBBB");
		assertTrue( first.compareTo(second) > 0 );
		assertTrue( second.compareTo(first) < 0 );
		second.setRowIndex(35);
		assertTrue( first.compareTo(second) < 0 );
		assertTrue( second.compareTo(first) > 0 );

		second.setRowIndex(25);
		assertTrue( first.compareTo(second) == 0 );
		assertTrue( second.compareTo(first) == 0 );
	}

}
