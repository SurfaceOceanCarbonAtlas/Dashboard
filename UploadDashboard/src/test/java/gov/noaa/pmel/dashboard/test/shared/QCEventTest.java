/**
 *
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.dashboard.shared.DashboardEvent;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCEvent;

import java.util.Date;

import org.junit.Test;

/**
 * Tests for methods in QCEvent
 *
 * @author Karl Smith
 */
public class QCEventTest {

    private static final Long DEFAULT_QC_ID = 0L;
    private static final Long MY_QC_ID = 123L;
    private static final Character MY_QC_FLAG = 'B';
    private static final String MY_EXPOCODE = "26NA20140427";
    private static final String MY_SOCAT_VERSION = "3.0";
    private static final Character MY_REGION_ID = 'T';
    private static final Date MY_FLAG_DATE = new Date();
    private static final String MY_USERNAME = "Karl.Smith";
    private static final String MY_REALNAME = "Karl M. Smith";
    private static final String MY_COMMENT = "from QCEvent unit test";

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getID()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setId(java.lang.Long)}.
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
     * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#getFlag()}
     * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#setFlag(java.lang.String)}.
     */
    @Test
    public void testGetSetFlag() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.QC_COMMENT, myflag.getFlag());
        myflag.setFlag(MY_QC_FLAG);
        assertEquals(MY_QC_FLAG, myflag.getFlag());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setFlag(null);
        assertEquals(DashboardUtils.QC_COMMENT, myflag.getFlag());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getExpocode()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setExpocode(java.lang.String)}.
     */
    @Test
    public void testGetSetExpocode() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
        myflag.setExpocode(MY_EXPOCODE);
        assertEquals(MY_EXPOCODE, myflag.getExpocode());
        assertEquals(DashboardUtils.QC_COMMENT, myflag.getFlag());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setExpocode(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getVersion()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setVersion(java.lang.Double)}.
     */
    @Test
    public void testGetSetSocatVersion() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        myflag.setVersion(MY_SOCAT_VERSION);
        assertEquals(MY_SOCAT_VERSION, myflag.getVersion());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
        assertEquals(DashboardUtils.QC_COMMENT, myflag.getFlag());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setVersion(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#getRegionID()}
     * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#setRegionID(java.lang.String)}.
     */
    @Test
    public void testGetSetRegionID() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.GLOBAL_REGION_ID, myflag.getRegionID());
        myflag.setRegionID(MY_REGION_ID);
        assertEquals(MY_REGION_ID, myflag.getRegionID());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
        assertEquals(DashboardUtils.QC_COMMENT, myflag.getFlag());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setRegionID(null);
        assertEquals(DashboardUtils.GLOBAL_REGION_ID, myflag.getRegionID());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getFlagDate()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setFlagDate(java.util.Date)}.
     */
    @Test
    public void testGetSetFlagDate() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        myflag.setFlagDate(MY_FLAG_DATE);
        assertEquals(MY_FLAG_DATE, myflag.getFlagDate());
        assertEquals(DashboardUtils.GLOBAL_REGION_ID, myflag.getRegionID());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
        assertEquals(DashboardUtils.QC_COMMENT, myflag.getFlag());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setFlagDate(null);
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getUsername()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setUsername(java.lang.String)}.
     */
    @Test
    public void testGetSetUsername() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
        myflag.setUsername(MY_USERNAME);
        assertEquals(MY_USERNAME, myflag.getUsername());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DashboardUtils.GLOBAL_REGION_ID, myflag.getRegionID());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
        assertEquals(DashboardUtils.QC_COMMENT, myflag.getFlag());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setUsername(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getRealname()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setRealname(java.lang.String)}.
     */
    @Test
    public void testGetSetRealname() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
        myflag.setRealname(MY_REALNAME);
        assertEquals(MY_REALNAME, myflag.getRealname());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DashboardUtils.GLOBAL_REGION_ID, myflag.getRegionID());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
        assertEquals(DashboardUtils.QC_COMMENT, myflag.getFlag());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setRealname(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#getComment()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardEvent#setComment(java.lang.String)}.
     */
    @Test
    public void testGetSetComment() {
        QCEvent myflag = new QCEvent();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
        myflag.setComment(MY_COMMENT);
        assertEquals(MY_COMMENT, myflag.getComment());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getRealname());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getUsername());
        assertEquals(DashboardUtils.DATE_MISSING_VALUE, myflag.getFlagDate());
        assertEquals(DashboardUtils.GLOBAL_REGION_ID, myflag.getRegionID());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getVersion());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getExpocode());
        assertEquals(DashboardUtils.QC_COMMENT, myflag.getFlag());
        assertEquals(DEFAULT_QC_ID, myflag.getId());
        myflag.setComment(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, myflag.getComment());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.QCEvent#hashCode()}
     * and {@link gov.noaa.pmel.dashboard.shared.QCEvent#equals(java.lang.Object)}.
     */
    @Test
    public void testHashCodeEqualsObject() {
        QCEvent myflag = new QCEvent();
        assertFalse(myflag.equals(null));
        assertFalse(myflag.equals(new DashboardEvent()));

        QCEvent otherflag = new QCEvent();
        assertTrue(myflag.hashCode() == otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        // ID ignored for hashCode and equals
        myflag.setId(MY_QC_ID);
        assertFalse(myflag.getId().equals(otherflag.getId()));
        assertTrue(myflag.hashCode() == otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setFlag(MY_QC_FLAG);
        assertFalse(myflag.hashCode() == otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setFlag(MY_QC_FLAG);
        assertTrue(myflag.hashCode() == otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setExpocode(MY_EXPOCODE);
        assertFalse(myflag.hashCode() == otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setExpocode(MY_EXPOCODE);
        assertTrue(myflag.hashCode() == otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setVersion(MY_SOCAT_VERSION);
        assertFalse(myflag.hashCode() == otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setVersion(MY_SOCAT_VERSION);
        assertTrue(myflag.hashCode() == otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setRegionID(MY_REGION_ID);
        assertFalse(myflag.hashCode() == otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setRegionID(MY_REGION_ID);
        assertTrue(myflag.hashCode() == otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setFlagDate(MY_FLAG_DATE);
        assertFalse(myflag.hashCode() == otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setFlagDate(MY_FLAG_DATE);
        assertTrue(myflag.hashCode() == otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setUsername(MY_USERNAME);
        assertFalse(myflag.hashCode() == otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setUsername(MY_USERNAME);
        assertTrue(myflag.hashCode() == otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setRealname(MY_REALNAME);
        assertFalse(myflag.hashCode() == otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setRealname(MY_REALNAME);
        assertTrue(myflag.hashCode() == otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));

        myflag.setComment(MY_COMMENT);
        assertFalse(myflag.hashCode() == otherflag.hashCode());
        assertFalse(myflag.equals(otherflag));
        otherflag.setComment(MY_COMMENT);
        assertTrue(myflag.hashCode() == otherflag.hashCode());
        assertTrue(myflag.equals(otherflag));
    }

}
