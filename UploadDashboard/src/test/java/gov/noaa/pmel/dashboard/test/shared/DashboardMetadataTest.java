/**
 *
 */
package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for methods of {@link DashboardMetadata}.
 *
 * @author Karl Smith
 */
public class DashboardMetadataTest {

    /**
     * Test method for {@link DashboardMetadata#isSelected()} and {@link DashboardMetadata#setSelected(boolean)}.
     */
    @Test
    public void testSetIsSelected() {
        DashboardMetadata mdata = new DashboardMetadata();
        assertFalse(mdata.isSelected());
        mdata.setSelected(true);
        assertTrue(mdata.isSelected());
    }

    /**
     * Test method for {@link DashboardMetadata#getDatasetId()} and {@link DashboardMetadata#setDatasetId(String)}.
     */
    @Test
    public void testGetSetDatasetId() {
        String myExpocode = "CYNS20120124";
        DashboardMetadata mdata = new DashboardMetadata();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setDatasetId(myExpocode);
        assertEquals(myExpocode, mdata.getDatasetId());
        assertFalse(mdata.isSelected());
        mdata.setDatasetId(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
    }

    /**
     * Test method for {@link DashboardMetadata#getOwner()} and {@link DashboardMetadata#setOwner(String)}.
     */
    @Test
    public void testGetSetOwner() {
        String myOwner = "SocatUser";
        DashboardMetadata mdata = new DashboardMetadata();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOwner());
        mdata.setOwner(myOwner);
        assertEquals(myOwner, mdata.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        assertFalse(mdata.isSelected());
        mdata.setOwner(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOwner());
    }

    /**
     * Test method for {@link DashboardMetadata#getFilename()} and {@link DashboardMetadata#setFilename(String)}.
     */
    @Test
    public void testGetSetFilename() {
        String myFilename = "NatalieSchulte_2013.doc";
        DashboardMetadata mdata = new DashboardMetadata();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getFilename());
        mdata.setFilename(myFilename);
        assertEquals(myFilename, mdata.getFilename());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        assertFalse(mdata.isSelected());
        mdata.setFilename(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getFilename());
    }

    /**
     * Test method for {@link DashboardMetadata#getUploadTimestamp()}
     * and {@link DashboardMetadata#setUploadTimestamp(String)}.
     */
    @Test
    public void testGetSetUploadTimestamp() {
        String myTimestamp = "2013-12-11 10:09";
        DashboardMetadata mdata = new DashboardMetadata();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getUploadTimestamp());
        mdata.setUploadTimestamp(myTimestamp);
        assertEquals(myTimestamp, mdata.getUploadTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getFilename());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        assertFalse(mdata.isSelected());
        mdata.setUploadTimestamp(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getUploadTimestamp());
    }

    /**
     * Test method for {@link DashboardMetadata#isConflicted()} and {@link DashboardMetadata#setConflicted(boolean)}.
     */
    @Test
    public void testIsSetConflicted() {
        DashboardMetadata mdata = new DashboardMetadata();
        assertEquals(false, mdata.isConflicted());
        mdata.setConflicted(true);
        assertEquals(true, mdata.isConflicted());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getUploadTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getFilename());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        assertFalse(mdata.isSelected());
        mdata.setConflicted(false);
        assertEquals(false, mdata.isConflicted());
    }

    /**
     * Test method for {@link DashboardMetadata#getVersion()} and {@link DashboardMetadata#setVersion(String)}.
     */
    @Test
    public void testGetSetVersion() {
        String version = "3.0";
        DashboardMetadata mdata = new DashboardMetadata();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVersion());
        mdata.setVersion(version);
        assertEquals(version, mdata.getVersion());
        assertEquals(false, mdata.isConflicted());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getUploadTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getFilename());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        assertFalse(mdata.isSelected());
        mdata.setVersion(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVersion());
    }

    /**
     * Test method for {@link DashboardMetadata#getAddlDocsTitle()}
     * and {@link DashboardMetadata#splitAddlDocsTitle(String)}
     */
    @Test
    public void testGetAddnDocsTitle() {
        String myExpocode = "CYNS20120124";
        String myOwner = "SocatUser";
        String myFilename = "NatalieSchulte_2013.doc";
        String myTimestamp = "2013-12-11 10:09";
        String myVersion = "3.0";
        String myDOI = "DOI12345";

        DashboardMetadata mdata = new DashboardMetadata();
        assertEquals("", mdata.getAddlDocsTitle());
        String[] nameTimePair = DashboardMetadata.splitAddlDocsTitle(mdata.getAddlDocsTitle());
        assertEquals(2, nameTimePair.length);
        assertEquals("", nameTimePair[0]);
        assertEquals("", nameTimePair[1]);
        mdata.setSelected(true);
        mdata.setDatasetId(myExpocode);
        mdata.setOwner(myOwner);
        mdata.setVersion(myVersion);
        assertEquals("", mdata.getAddlDocsTitle());
        mdata.setFilename(myFilename);
        assertEquals(myFilename, mdata.getAddlDocsTitle());
        nameTimePair = DashboardMetadata.splitAddlDocsTitle(mdata.getAddlDocsTitle());
        assertEquals(2, nameTimePair.length);
        assertEquals(myFilename, nameTimePair[0]);
        assertEquals("", nameTimePair[1]);
        mdata.setUploadTimestamp(myTimestamp);
        nameTimePair = DashboardMetadata.splitAddlDocsTitle(mdata.getAddlDocsTitle());
        assertEquals(2, nameTimePair.length);
        assertEquals(myFilename, nameTimePair[0]);
        assertEquals(myTimestamp, nameTimePair[1]);
        mdata.setFilename(null);
        assertEquals("", mdata.getAddlDocsTitle());
    }

    /**
     * Test method for {@link DashboardMetadata#hashCode()} and {@link DashboardMetadata#equals(Object)}.
     */
    @Test
    public void testHashCodeEqualsObject() {
        String myExpocode = "CYNS20120124";
        String myOwner = "SocatUser";
        String myFilename = "NatalieSchulte_2013.doc";
        String myTimestamp = "2013-12-11 10:09";
        String myVersion = "3.0";
        String myDOI = "DOI12345";

        DashboardMetadata firstMData = new DashboardMetadata();
        assertFalse(firstMData.equals(null));
        assertFalse(firstMData.equals(myFilename));
        DashboardMetadata secondMData = new DashboardMetadata();
        assertEquals(firstMData.hashCode(), firstMData.hashCode());
        assertTrue(firstMData.equals(secondMData));

        firstMData.setSelected(true);
        assertNotEquals(firstMData.hashCode(), secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setSelected(true);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertTrue(firstMData.equals(secondMData));

        firstMData.setDatasetId(myExpocode);
        assertNotEquals(firstMData.hashCode(), secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setDatasetId(myExpocode);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertTrue(firstMData.equals(secondMData));

        firstMData.setOwner(myOwner);
        assertNotEquals(firstMData.hashCode(), secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setOwner(myOwner);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertTrue(firstMData.equals(secondMData));

        firstMData.setFilename(myFilename);
        assertNotEquals(firstMData.hashCode(), secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setFilename(myFilename);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertTrue(firstMData.equals(secondMData));

        firstMData.setUploadTimestamp(myTimestamp);
        assertNotEquals(firstMData.hashCode(), secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setUploadTimestamp(myTimestamp);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertTrue(firstMData.equals(secondMData));

        firstMData.setConflicted(true);
        assertNotEquals(firstMData.hashCode(), secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setConflicted(true);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertTrue(firstMData.equals(secondMData));

        firstMData.setVersion(myVersion);
        assertNotEquals(firstMData.hashCode(), secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setVersion(myVersion);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertTrue(firstMData.equals(secondMData));
    }

}
