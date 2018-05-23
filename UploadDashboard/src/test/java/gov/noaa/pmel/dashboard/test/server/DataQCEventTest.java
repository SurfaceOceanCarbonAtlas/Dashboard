/**
 *
 */
package gov.noaa.pmel.dashboard.test.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.server.DataLocation;
import gov.noaa.pmel.dashboard.server.DataQCEvent;
import gov.noaa.pmel.dashboard.server.QCEvent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for methods of {@link DataQCEvent}
 *
 * @author Karl Smith
 */
public class DataQCEventTest {

    private static final Long DEFAULT_QC_ID = 0L;
    private static final Long MY_QC_ID = 123L;
    private static final Date MY_FLAG_DATE = new Date();
    private static final String MY_WOCE_NAME = "WOCE_PO4_water";
    private static final String MY_WOCE_VALUE = "auto-3";
    private static final String MY_DATASET_ID = "26NA20140427";
    private static final String MY_REGION_ID = "TPac";
    private static final String MY_VERSION = "6.0";
    private static final String MY_USERNAME = "Karl.Smith";
    private static final String MY_REALNAME = "Karl M. Smith";
    private static final String MY_COMMENT = "from DataQCEvent unit test";
    private static final String MY_DATA_VAR_NAME = "Pressure_atm";
    private static final ArrayList<DataLocation> MY_LOCATIONS;

    static {
        MY_LOCATIONS = new ArrayList<DataLocation>(2);
        DataLocation loc = new DataLocation();
        loc.setRowNumber(345);
        loc.setDataDate(new Date(3458139048000L));
        loc.setLongitude(-179.5);
        loc.setLatitude(3.5);
        loc.setDepth(5.0);
        loc.setDataValue(1105.450);
        MY_LOCATIONS.add(loc);
        loc = new DataLocation();
        loc.setRowNumber(346);
        loc.setDataDate(new Date(3458139203000L));
        loc.setLongitude(-179.6);
        loc.setLatitude(3.4);
        loc.setDepth(7.5);
        loc.setDataValue(1105.453);
        MY_LOCATIONS.add(loc);
    }

    /**
     * Test method for {@link DataQCEvent#getVarName()} and {@link DataQCEvent#setVarName(String)}.
     */
    @Test
    public void testGetSetVarName() {
        DataQCEvent myflag = new DataQCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
        myflag.setVarName(MY_DATA_VAR_NAME);
        assertEquals(MY_DATA_VAR_NAME, myflag.getVarName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        assertEquals(DashboardUtils.REGION_ID_GLOBAL, myflag.getRegionId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setVarName(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
    }

    /**
     * Test method for {@link DataQCEvent#getLocations()} and {@link DataQCEvent#setLocations(ArrayList)}.
     */
    @Test
    public void testGetSetLocations() {
        DataQCEvent myflag = new DataQCEvent();
        assertEquals(0, myflag.getLocations().size());
        myflag.setLocations(MY_LOCATIONS);
        assertEquals(MY_LOCATIONS, myflag.getLocations());
        assertNotSame(MY_LOCATIONS, myflag.getLocations());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVarName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        assertEquals(DashboardUtils.REGION_ID_GLOBAL, myflag.getRegionId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setLocations(null);
        assertEquals(0, myflag.getLocations().size());
    }

    /**
     * Test method for {@link DataQCEvent#hashCode()} and {@link DataQCEvent#equals(Object)}.
     */
    @Test
    public void testHashCodeEqualsObject() {
        DataQCEvent myflag = new DataQCEvent();
        assertFalse(myflag.equals(null));
        assertFalse(myflag.equals(new QCEvent()));
        assertTrue((new QCEvent()).equals(myflag));

        DataQCEvent otherflag = new DataQCEvent();
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        // ID ignored for hashCode and equals
        myflag.setId(MY_QC_ID);
        assertNotEquals(myflag.getId(), otherflag.getId());
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setFlagDate(MY_FLAG_DATE);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setFlagDate(MY_FLAG_DATE);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setFlagName(MY_WOCE_NAME);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setFlagName(MY_WOCE_NAME);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setFlagValue(MY_WOCE_VALUE);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setFlagValue(MY_WOCE_VALUE);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setDatasetId(MY_DATASET_ID);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setDatasetId(MY_DATASET_ID);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setRegionId(MY_REGION_ID);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setRegionId(MY_REGION_ID);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setVersion(MY_VERSION);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setVersion(MY_VERSION);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setUsername(MY_USERNAME);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setUsername(MY_USERNAME);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setRealname(MY_REALNAME);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setRealname(MY_REALNAME);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setComment(MY_COMMENT);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setComment(MY_COMMENT);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setVarName(MY_DATA_VAR_NAME);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setVarName(MY_DATA_VAR_NAME);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setLocations(MY_LOCATIONS);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setLocations(MY_LOCATIONS);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));
    }

}
