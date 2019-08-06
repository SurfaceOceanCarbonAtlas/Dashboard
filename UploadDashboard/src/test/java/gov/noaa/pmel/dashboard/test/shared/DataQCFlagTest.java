package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataQCFlag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for methods of {@link DataQCFlag}
 *
 * @author Karl Smith
 */
public class DataQCFlagTest {

    private static final String MY_FLAG_NAME = "WOCE_CO2_atm";
    private static final String MY_FLAG_VALUE = "3";
    private static final DataQCFlag.Severity MY_SEVERITY = DataQCFlag.Severity.WARNING;
    private static final Integer MY_COLUMN_INDEX = 5;
    private static final Integer MY_ROW_INDEX = 15;

    /**
     * Test method for {@link DataQCFlag#getFlagName()} and {@link DataQCFlag#setFlagName(String)}.
     */
    @Test
    public void testGetSetFlagName() {
        DataQCFlag flag = new DataQCFlag();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
        flag.setFlagName(MY_FLAG_NAME);
        assertEquals(MY_FLAG_NAME, flag.getFlagName());
        flag.setFlagName(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
    }

    /**
     * Test method for {@link DataQCFlag#getFlagValue()} and {@link DataQCFlag#setFlagValue(String)}.
     */
    @Test
    public void testGetSetFlagValue() {
        DataQCFlag flag = new DataQCFlag();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagValue());
        flag.setFlagValue(MY_FLAG_VALUE);
        assertEquals(MY_FLAG_VALUE, flag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
        flag.setFlagValue(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagValue());
    }

    /**
     * Test method for {@link DataQCFlag#getSeverity()} and {@link DataQCFlag#setSeverity(DataQCFlag.Severity)}.
     */
    @Test
    public void testGetSetSeverity() {
        DataQCFlag flag = new DataQCFlag();
        assertEquals(DataQCFlag.Severity.UNASSIGNED, flag.getSeverity());
        flag.setSeverity(MY_SEVERITY);
        assertEquals(MY_SEVERITY, flag.getSeverity());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
        flag.setSeverity(null);
        assertEquals(DataQCFlag.Severity.UNASSIGNED, flag.getSeverity());
    }

    /**
     * Test method for {@link DataQCFlag#getColumnIndex()} and {@link DataQCFlag#setColumnIndex(Integer)}.
     */
    @Test
    public void testGetSetColumnIndex() {
        DataQCFlag flag = new DataQCFlag();
        assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getColumnIndex());
        flag.setColumnIndex(MY_COLUMN_INDEX);
        assertEquals(MY_COLUMN_INDEX, flag.getColumnIndex());
        assertEquals(DataQCFlag.Severity.UNASSIGNED, flag.getSeverity());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
        flag.setColumnIndex(null);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getColumnIndex());
    }

    /**
     * Test method for {@link DataQCFlag#getRowIndex()} and {@link DataQCFlag#setRowIndex(Integer)}.
     */
    @Test
    public void testGetSetRowIndex() {
        DataQCFlag flag = new DataQCFlag();
        assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getRowIndex());
        flag.setRowIndex(MY_ROW_INDEX);
        assertEquals(MY_ROW_INDEX, flag.getRowIndex());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getColumnIndex());
        assertEquals(DataQCFlag.Severity.UNASSIGNED, flag.getSeverity());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, flag.getFlagName());
        flag.setRowIndex(null);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, flag.getRowIndex());
    }

    /**
     * Test method for {@link DataQCFlag#DataQCFlag(String, String, DataQCFlag.Severity, Integer, Integer)}
     */
    @Test
    public void testWoceFlagStringIntegerInteger() {
        DataQCFlag flag = new DataQCFlag(MY_FLAG_NAME, MY_FLAG_VALUE, MY_SEVERITY, MY_COLUMN_INDEX, MY_ROW_INDEX);
        assertEquals(MY_FLAG_NAME, flag.getFlagName());
        assertEquals(MY_FLAG_VALUE, flag.getFlagValue());
        assertEquals(MY_SEVERITY, flag.getSeverity());
        assertEquals(MY_COLUMN_INDEX, flag.getColumnIndex());
        assertEquals(MY_ROW_INDEX, flag.getRowIndex());
    }

    /**
     * Test method for {@link DataQCFlag#hashCode()} and {@link DataQCFlag#equals(Object)}.
     */
    @Test
    public void testHashCodeEquals() {
        DataQCFlag first = new DataQCFlag();
        assertFalse(first.equals(null));
        assertFalse(first.equals(DashboardUtils.STRING_MISSING_VALUE));

        DataQCFlag second = new DataQCFlag();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFlagName(MY_FLAG_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFlagName(MY_FLAG_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFlagValue(MY_FLAG_VALUE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFlagValue(MY_FLAG_VALUE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSeverity(MY_SEVERITY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSeverity(MY_SEVERITY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setColumnIndex(MY_COLUMN_INDEX);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setColumnIndex(MY_COLUMN_INDEX);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setRowIndex(MY_ROW_INDEX);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setRowIndex(MY_ROW_INDEX);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

    /**
     * Test method for {@link DataQCFlag#compareTo(DataQCFlag)}.
     */
    @Test
    public void testCompareTo() {
        DataQCFlag first = new DataQCFlag("WOCE_CO2_atm", "3", DataQCFlag.Severity.WARNING, 5, 25);
        DataQCFlag second = new DataQCFlag("WOCE_CO2_water", "2", DataQCFlag.Severity.ACCEPTABLE, 4, 15);

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        second.setFlagName("QC_Patm");
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setFlagName("WOCE_CO2_atm");

        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setFlagValue("4");
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        second.setFlagValue("3");

        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setSeverity(DataQCFlag.Severity.ERROR);
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        second.setSeverity(DataQCFlag.Severity.WARNING);

        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setColumnIndex(6);
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        second.setColumnIndex(5);

        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.setRowIndex(35);
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        second.setRowIndex(25);

        assertEquals(0, first.compareTo(second));
        assertEquals(0, second.compareTo(first));
    }

}
