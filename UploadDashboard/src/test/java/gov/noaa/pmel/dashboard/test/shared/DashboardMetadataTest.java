/**
 *
 */
package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata}.
 *
 * @author Karl Smith
 */
public class DashboardMetadataTest {

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#isSelected()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#setSelected(boolean)}.
     */
    @Test
    public void testSetIsSelected() {
        DashboardMetadata mdata = new DashboardMetadata();
        assertFalse(mdata.isSelected());
        mdata.setSelected(true);
        assertTrue(mdata.isSelected());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#getExpocode()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#setExpocode(java.lang.String)}.
     */
    @Test
    public void testGetSetExpocode() {
        String myExpocode = "CYNS20120124";
        DashboardMetadata mdata = new DashboardMetadata();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
        mdata.setExpocode(myExpocode);
        assertEquals(myExpocode, mdata.getExpocode());
        assertFalse(mdata.isSelected());
        mdata.setExpocode(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#getOwner()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#setOwner(java.lang.String)}.
     */
    @Test
    public void testGetSetOwner() {
        String myOwner = "SocatUser";
        DashboardMetadata mdata = new DashboardMetadata();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOwner());
        mdata.setOwner(myOwner);
        assertEquals(myOwner, mdata.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
        assertFalse(mdata.isSelected());
        mdata.setOwner(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOwner());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#getFilename()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#setFilename(java.lang.String)}.
     */
    @Test
    public void testGetSetFilename() {
        String myFilename = "NatalieSchulte_2013.doc";
        DashboardMetadata mdata = new DashboardMetadata();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getFilename());
        mdata.setFilename(myFilename);
        assertEquals(myFilename, mdata.getFilename());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
        assertFalse(mdata.isSelected());
        mdata.setFilename(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getFilename());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#getUploadTimestamp()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#setUploadTimestamp(java.lang.String)}.
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
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
        assertFalse(mdata.isSelected());
        mdata.setUploadTimestamp(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getUploadTimestamp());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#isConflicted()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#setConflicted(boolean)}.
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
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
        assertFalse(mdata.isSelected());
        mdata.setConflicted(false);
        assertEquals(false, mdata.isConflicted());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#getVersion()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#setVersion(String)}.
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
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
        assertFalse(mdata.isSelected());
        mdata.setVersion(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVersion());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#getDOI()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#setDOI(String)}.
     */
    @Test
    public void testGetSetDOI() {
        String doi = "DOI12345";
        DashboardMetadata mdata = new DashboardMetadata();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDOI());
        mdata.setDOI(doi);
        assertEquals(doi, mdata.getDOI());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVersion());
        assertEquals(false, mdata.isConflicted());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getUploadTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getFilename());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getExpocode());
        assertFalse(mdata.isSelected());
        mdata.setDOI(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDOI());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#hashCode()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#equals(java.lang.Object)}.
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
        assertEquals(firstMData, secondMData);

        firstMData.setSelected(true);
        assertTrue(firstMData.hashCode() != secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setSelected(true);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertEquals(firstMData, secondMData);

        firstMData.setExpocode(myExpocode);
        assertTrue(firstMData.hashCode() != secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setExpocode(myExpocode);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertEquals(firstMData, secondMData);

        firstMData.setOwner(myOwner);
        assertTrue(firstMData.hashCode() != secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setOwner(myOwner);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertEquals(firstMData, secondMData);

        firstMData.setFilename(myFilename);
        assertTrue(firstMData.hashCode() != secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setFilename(myFilename);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertEquals(firstMData, secondMData);

        firstMData.setUploadTimestamp(myTimestamp);
        assertTrue(firstMData.hashCode() != secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setUploadTimestamp(myTimestamp);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertEquals(firstMData, secondMData);

        firstMData.setConflicted(true);
        assertTrue(firstMData.hashCode() != secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setConflicted(true);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertEquals(firstMData, secondMData);

        firstMData.setVersion(myVersion);
        assertTrue(firstMData.hashCode() != secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setVersion(myVersion);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertEquals(firstMData, secondMData);

        firstMData.setDOI(myDOI);
        assertTrue(firstMData.hashCode() != secondMData.hashCode());
        assertFalse(firstMData.equals(secondMData));
        secondMData.setDOI(myDOI);
        assertEquals(firstMData.hashCode(), secondMData.hashCode());
        assertEquals(firstMData, secondMData);
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#getAddlDocsTitle()}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardMetadata#splitAddlDocsTitle(java.lang.String)}
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
        mdata.setExpocode(myExpocode);
        mdata.setOwner(myOwner);
        mdata.setVersion(myVersion);
        mdata.setDOI(myDOI);
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

}
