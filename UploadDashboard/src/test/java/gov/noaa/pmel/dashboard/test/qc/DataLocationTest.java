package gov.noaa.pmel.dashboard.test.qc;

import gov.noaa.pmel.dashboard.qc.DataLocation;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link DataLocation} methods
 *
 * @author Karl Smith
 */
public class DataLocationTest {

    private static final Character MY_REGION_ID = 'T';
    private static final Integer MY_ROW_NUMBER = 345;
    private static final Date MY_DATA_DATE = new Date(3458139048000L);
    private static final Double MY_LONGITUDE = -179.45;
    private static final Double MY_LATITUDE = -2.65;
    private static final Double MY_DATA_VALUE = 1002.97;

    /**
     * Test method for {@link DataLocation#getRowNumber()} and {@link DataLocation#setRowNumber(Integer)}.
     */
    @Test
    public void testGetSetRowNumber() {
        DataLocation myflag = new DataLocation();
        assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
        myflag.setRowNumber(MY_ROW_NUMBER);
        assertEquals(MY_ROW_NUMBER, myflag.getRowNumber());
        myflag.setRowNumber(null);
        assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
    }

    /**
     * Test method for {@link DataLocation#getDataDate()} and {@link DataLocation#setDataDate(Date)}.
     */
    @Test
    public void testGetSetDataDate() {
        DataLocation myflag = new DataLocation();
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getDataDate());
        myflag.setDataDate(MY_DATA_DATE);
        assertEquals(MY_DATA_DATE, myflag.getDataDate());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
        myflag.setDataDate(null);
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getDataDate());
    }

    /**
     * Test method for {@link DataLocation#getLongitude()} and {@link DataLocation#setLongitude(Double)}.
     */
    @Test
    public void testGetSetLongitude() {
        DataLocation myflag = new DataLocation();
        assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLongitude());
        myflag.setLongitude(MY_LONGITUDE);
        assertEquals(MY_LONGITUDE, myflag.getLongitude());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getDataDate());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
        myflag.setLongitude(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLongitude());
    }

    /**
     * Test method for {@link DataLocation#getLatitude()} and {@link DataLocation#setLatitude(Double)}.
     */
    @Test
    public void testGetSetLatitude() {
        DataLocation myflag = new DataLocation();
        assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLatitude());
        myflag.setLatitude(MY_LATITUDE);
        assertEquals(MY_LATITUDE, myflag.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLongitude());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getDataDate());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
        myflag.setLatitude(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLatitude());
    }

    /**
     * Test method for {@link DataLocation#getDataValue()} and {@link DataLocation#setDataValue(Double)}.
     */
    @Test
    public void testGetSetDataValue() {
        DataLocation myflag = new DataLocation();
        assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getDataValue());
        myflag.setDataValue(MY_DATA_VALUE);
        assertEquals(MY_DATA_VALUE, myflag.getDataValue());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getLongitude());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getDataDate());
        assertEquals(DashboardUtils.INT_MISSING_VALUE, myflag.getRowNumber());
        myflag.setDataValue(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, myflag.getDataValue());
    }

    /**
     * Test method for {@link DataLocation#hashCode()} and {@link DataLocation#equals(Object)}.
     */
    @Test
    public void testHashCodeEquals() {
        DataLocation myflag = new DataLocation();
        assertFalse(myflag.equals(null));
        assertFalse(myflag.equals(new Date()));

        DataLocation otherflag = new DataLocation();
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setRowNumber(MY_ROW_NUMBER);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setRowNumber(MY_ROW_NUMBER);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setDataDate(MY_DATA_DATE);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setDataDate(MY_DATA_DATE);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setLongitude(MY_LONGITUDE);
        // longitude is ignored in the hash code
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setLongitude(MY_LONGITUDE);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setLatitude(MY_LATITUDE);
        // latitude is ignored in the hash code
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setLatitude(MY_LATITUDE);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setDataValue(MY_DATA_VALUE);
        // dataValue is ignored in the hash code
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setDataValue(MY_DATA_VALUE);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));
    }

}
