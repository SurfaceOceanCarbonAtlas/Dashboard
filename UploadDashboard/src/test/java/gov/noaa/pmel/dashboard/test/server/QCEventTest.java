/**
 *
 */
package gov.noaa.pmel.dashboard.test.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.server.QCEvent;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for methods in QCEvent
 *
 * @author Karl Smith
 */
public class QCEventTest {

    private static final Long DEFAULT_QC_ID = 0L;
    private static final Long MY_QC_ID = 123L;
    private static final Date MY_FLAG_DATE = new Date();
    private static final String MY_QCFLAG_NAME = "dataset";
    private static final String MY_QCFLAG_VALUE = "auto-B";
    private static final String MY_DATASET_ID = "26NA20140427";
    private static final String MY_REGION_ID = "TPac";
    private static final String MY_VERSION = "3.0";
    private static final String MY_USERNAME = "Karl.Smith";
    private static final String MY_REALNAME = "Karl M. Smith";
    private static final String MY_COMMENT = "from QCEvent unit test";

    /**
     * Test method for {@link QCEvent#getId()} and {@link QCEvent#setId(Long)}
     */
    @Test
    public void testGetSetId() {
        QCEvent myflag = new QCEvent();
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setId(MY_QC_ID);
        assertEquals(MY_QC_ID, myflag.getId());
        myflag.setId(null);
        assertEquals(DEFAULT_QC_ID, myflag.getId());
    }

    /**
     * Test method for {@link QCEvent#getFlagDate()} and {@link QCEvent#setFlagDate(Date)}.
     */
    @Test
    public void testGetSetFlagDate() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        myflag.setFlagDate(MY_FLAG_DATE);
        assertEquals(MY_FLAG_DATE, myflag.getFlagDate());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setFlagDate(null);
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
    }

    /**
     * Test method for {@link QCEvent#getFlagName()} and {@link QCEvent#setFlagName(String)}
     */
    @Test
    public void testGetSetFlagName() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
        myflag.setFlagName(MY_QCFLAG_NAME);
        assertEquals(MY_QCFLAG_NAME, myflag.getFlagName());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setFlagName(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
    }

    /**
     * Test method for {@link QCEvent#getFlagValue()} and {@link QCEvent#setFlagValue(String)}
     */
    @Test
    public void testGetSetFlagValue() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagValue());
        myflag.setFlagValue(MY_QCFLAG_VALUE);
        assertEquals(MY_QCFLAG_VALUE, myflag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setFlagValue(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagValue());
    }

    /**
     * Test method for {@link QCEvent#getDatasetId()} and {@link QCEvent#setDatasetId(String)}
     */
    @Test
    public void testGetSetDatasetId() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
        myflag.setDatasetId(MY_DATASET_ID);
        assertEquals(MY_DATASET_ID, myflag.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setDatasetId(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
    }

    /**
     * Test method for {@link QCEvent#getRegionId()} and {@link QCEvent#setRegionId(String)}
     */
    @Test
    public void testGetSetRegionID() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.REGION_ID_GLOBAL, myflag.getRegionId());
        myflag.setRegionId(MY_REGION_ID);
        assertEquals(MY_REGION_ID, myflag.getRegionId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setRegionId(null);
        assertEquals(DashboardUtils.REGION_ID_GLOBAL, myflag.getRegionId());
    }

    /**
     * Test method for {@link QCEvent#getVersion()} and {@link QCEvent#setVersion(String)}
     */
    @Test
    public void testGetSetVersion() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        myflag.setVersion(MY_VERSION);
        assertEquals(MY_VERSION, myflag.getVersion());
        assertEquals(DashboardUtils.REGION_ID_GLOBAL, myflag.getRegionId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setVersion(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
    }

    /**
     * Test method for {@link QCEvent#getUsername()} and {@link QCEvent#setUsername(String)}.
     */
    @Test
    public void testGetSetUsername() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
        myflag.setUsername(MY_USERNAME);
        assertEquals(MY_USERNAME, myflag.getUsername());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        assertEquals(DashboardUtils.REGION_ID_GLOBAL, myflag.getRegionId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setUsername(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
    }

    /**
     * Test method for {@link QCEvent#getRealname()} and {@link QCEvent#setRealname(String)}.
     */
    @Test
    public void testGetSetRealname() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
        myflag.setRealname(MY_REALNAME);
        assertEquals(MY_REALNAME, myflag.getRealname());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        assertEquals(DashboardUtils.REGION_ID_GLOBAL, myflag.getRegionId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setRealname(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
    }

    /**
     * Test method for {@link QCEvent#getComment()} and {@link QCEvent#setComment(String)}.
     */
    @Test
    public void testGetSetComment() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
        myflag.setComment(MY_COMMENT);
        assertEquals(MY_COMMENT, myflag.getComment());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        assertEquals(DashboardUtils.REGION_ID_GLOBAL, myflag.getRegionId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagValue());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getFlagName());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setComment(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
    }

    /**
     * Test method for {@link QCEvent#hashCode()} and {@link QCEvent#equals(Object)}.
     */
    @Test
    public void testHashCodeEqualsObject() {
        QCEvent myflag = new QCEvent();
        assertFalse(myflag.equals(null));
        assertFalse(myflag.equals(DashboardUtils.STRING_MISSING_VALUE));

        QCEvent otherflag = new QCEvent();
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        // ID ignored for hashCode and equals
        myflag.setId(MY_QC_ID);
        assertFalse(myflag.getId().equals(otherflag.getId()));
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setFlagDate(MY_FLAG_DATE);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setFlagDate(MY_FLAG_DATE);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setFlagName(MY_QCFLAG_NAME);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setFlagName(MY_QCFLAG_NAME);
        assertEquals(myflag.hashCode(), otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setFlagName(MY_QCFLAG_VALUE);
        assertNotEquals(myflag.hashCode(), otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setFlagName(MY_QCFLAG_VALUE);
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
    }

}
