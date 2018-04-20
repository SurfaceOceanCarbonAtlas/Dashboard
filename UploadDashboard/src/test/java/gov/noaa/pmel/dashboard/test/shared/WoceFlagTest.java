/**
 *
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.*;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.WoceFlag;
import gov.noaa.pmel.dashboard.shared.WoceType;

import org.junit.Test;

/**
 * Unit tests for methods of {@link gov.noaa.pmel.dashboard.shared.WoceFlag}
 *
 * @author Karl Smith
 */
public class WoceFlagTest {

    private static final String MY_WOCE_NAME = "WOCE_CO2_atm";
    private static final Character MY_WOCE_FLAG = '3';
    private static final Integer MY_COLUMN_INDEX = 5;
    private static final Integer MY_ROW_INDEX = 15;
    private static final String MY_COMMENT = "my comment";

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceFlag#getFlag()} and
     * {@link gov.noaa.pmel.dashboard.shared.WoceFlag#setFlag(java.lang.Character)}.
     */
    @Test
    public void testGetSetFlag() {
        WoceFlag wflag = new WoceFlag();
        assertEquals(DashboardUtils.CHAR_MISSING_VALUE, wflag.getFlag());
        wflag.setFlag(MY_WOCE_FLAG);
        assertEquals(MY_WOCE_FLAG, wflag.getFlag());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, wflag.getWoceName());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, wflag.getColumnIndex());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, wflag.getRowIndex());
        wflag.setFlag(null);
        assertEquals(DashboardUtils.CHAR_MISSING_VALUE, wflag.getFlag());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceFlag#getComment()} and
     * {@link gov.noaa.pmel.dashboard.shared.WoceFlag#setComment(java.lang.String)}.
     */
    @Test
    public void testGetSetComment() {
        WoceFlag wflag = new WoceFlag();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, wflag.getComment());
        wflag.setComment(MY_COMMENT);
        assertEquals(MY_COMMENT, wflag.getComment());
        assertEquals(DashboardUtils.CHAR_MISSING_VALUE, wflag.getFlag());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, wflag.getWoceName());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, wflag.getColumnIndex());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, wflag.getRowIndex());
        wflag.setComment(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, wflag.getComment());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceFlag#WoceFlag(java.lang.String, java.lang.Integer, java.lang.Integer)}.
     */
    @Test
    public void testWoceFlagStringIntegerInteger() {
        WoceFlag wflag = new WoceFlag(MY_WOCE_NAME, MY_COLUMN_INDEX, MY_ROW_INDEX);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, wflag.getComment());
        assertEquals(DashboardUtils.CHAR_MISSING_VALUE, wflag.getFlag());
        assertEquals(MY_WOCE_NAME, wflag.getWoceName());
        assertEquals(MY_COLUMN_INDEX, wflag.getColumnIndex());
        assertEquals(MY_ROW_INDEX, wflag.getRowIndex());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceFlag#hashCode()} and
     * {@link gov.noaa.pmel.dashboard.shared.WoceFlag#equals(java.lang.Object)}.
     */
    @Test
    public void testHashCodeEquals() {
        WoceFlag first = new WoceFlag();
        assertFalse(first.equals(null));
        assertFalse(first.equals(new WoceType()));

        WoceFlag second = new WoceFlag();
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setFlag(MY_WOCE_FLAG);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setFlag(MY_WOCE_FLAG);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setComment(MY_COMMENT);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setComment(MY_COMMENT);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setWoceName(MY_WOCE_NAME);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setWoceName(MY_WOCE_NAME);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setColumnIndex(MY_COLUMN_INDEX);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setColumnIndex(MY_COLUMN_INDEX);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setRowIndex(MY_ROW_INDEX);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setRowIndex(MY_ROW_INDEX);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.WoceFlag#compareTo(gov.noaa.pmel.dashboard.shared.WoceType)}.
     */
    @Test
    public void testCompareTo() {
        WoceFlag first = new WoceFlag("WOCE_CO2_atm", 5, 25);
        first.setFlag('3');
        first.setComment("BBBB");

        WoceFlag second = new WoceFlag("WOCE_CO2_water", 4, 15);
        second.setFlag('2');
        second.setComment("AAAA");
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);

        second.setWoceName("WOCE_CO2_atm");
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setFlag('4');
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);

        second.setFlag('3');
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setColumnIndex(6);
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);

        second.setColumnIndex(5);
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setComment("CCCC");
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);

        second.setComment("BBBB");
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setRowIndex(35);
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);

        second.setRowIndex(25);
        assertTrue(first.compareTo(second) == 0);
        assertTrue(second.compareTo(first) == 0);

        WoceType another = new WoceType("WOCE_CO2_atm", 5, 25);
        assertTrue(first.compareTo(another) > 0);
        assertTrue(another.compareTo(first) == 0);

        first.setFlag(null);
        first.setComment(null);
        assertTrue(first.compareTo(another) == 0);
        assertTrue(another.compareTo(first) == 0);
    }


}
