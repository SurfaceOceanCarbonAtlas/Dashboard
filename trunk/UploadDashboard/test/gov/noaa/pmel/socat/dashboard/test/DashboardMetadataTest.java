/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import org.junit.Test;

/**
 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata}.
 * @author Karl Smith
 */
public class DashboardMetadataTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#isSelected()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#setSelected(boolean)}.
	 */
	@Test
	public void testSetIsSelected() {
		DashboardMetadata mdata = new DashboardMetadata();
		assertFalse( mdata.isSelected() );
		mdata.setSelected(true);
		assertTrue( mdata.isSelected() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#getExpocode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testGetSetExpocode() {
		String myExpocode = "CYNS20120124";
		DashboardMetadata mdata = new DashboardMetadata();
		assertEquals("", mdata.getExpocode());
		mdata.setExpocode(myExpocode);
		assertEquals(myExpocode, mdata.getExpocode());
		assertFalse( mdata.isSelected() );
		mdata.setExpocode(null);
		assertEquals("", mdata.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#getOwner()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#setOwner(java.lang.String)}.
	 */
	@Test
	public void testGetSetOwner() {
		String myOwner = "SocatUser";
		DashboardMetadata mdata = new DashboardMetadata();
		assertEquals("", mdata.getOwner());
		mdata.setOwner(myOwner);
		assertEquals(myOwner, mdata.getOwner());
		assertEquals("", mdata.getExpocode());
		assertFalse( mdata.isSelected() );
		mdata.setOwner(null);
		assertEquals("", mdata.getOwner());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#getFilename()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#setFilename(java.lang.String)}.
	 */
	@Test
	public void testGetSetFilename() {
		String myFilename = "NatalieSchulte_2013.doc";
		DashboardMetadata mdata = new DashboardMetadata();
		assertEquals("", mdata.getFilename());
		mdata.setFilename(myFilename);
		assertEquals(myFilename, mdata.getFilename());
		assertEquals("", mdata.getOwner());
		assertEquals("", mdata.getExpocode());
		assertFalse( mdata.isSelected() );
		mdata.setFilename(null);
		assertEquals("", mdata.getFilename());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#getUploadTimestamp()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#setUploadTimestamp(java.lang.String)}.
	 */
	@Test
	public void testGetSetUploadTimestamp() {
		String myTimestamp = "2013-12-11 10:09";
		DashboardMetadata mdata = new DashboardMetadata();
		assertEquals("", mdata.getUploadTimestamp());
		mdata.setUploadTimestamp(myTimestamp);
		assertEquals(myTimestamp, mdata.getUploadTimestamp());
		assertEquals("", mdata.getFilename());
		assertEquals("", mdata.getOwner());
		assertEquals("", mdata.getExpocode());
		assertFalse( mdata.isSelected() );
		mdata.setUploadTimestamp(null);
		assertEquals("", mdata.getUploadTimestamp());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#isConflicted()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#setConflicted(boolean)}.
	 */
	@Test
	public void testIsSetConflicted() {
		DashboardMetadata mdata = new DashboardMetadata();
		assertEquals(false, mdata.isConflicted());
		mdata.setConflicted(true);
		assertEquals(true, mdata.isConflicted());
		assertEquals("", mdata.getUploadTimestamp());
		assertEquals("", mdata.getFilename());
		assertEquals("", mdata.getOwner());
		assertEquals("", mdata.getExpocode());
		assertFalse( mdata.isSelected() );
		mdata.setConflicted(false);
		assertEquals(false, mdata.isConflicted());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		String myExpocode = "CYNS20120124";
		String myOwner = "SocatUser";
		String myFilename = "NatalieSchulte_2013.doc";
		String myTimestamp = "2013-12-11 10:09";

		DashboardMetadata firstMData = new DashboardMetadata();
		assertFalse( firstMData.equals(null) );
		assertFalse( firstMData.equals(myFilename) );
		DashboardMetadata secondMData = new DashboardMetadata();
		assertEquals(firstMData.hashCode(), firstMData.hashCode());
		assertEquals(firstMData, secondMData);

		firstMData.setSelected(true);
		assertTrue( firstMData.hashCode() != secondMData.hashCode() );
		assertFalse( firstMData.equals(secondMData) );
		secondMData.setSelected(true);
		assertEquals(firstMData.hashCode(), secondMData.hashCode());
		assertEquals(firstMData, secondMData);

		firstMData.setExpocode(myExpocode);
		assertTrue( firstMData.hashCode() != secondMData.hashCode() );
		assertFalse( firstMData.equals(secondMData) );
		secondMData.setExpocode(myExpocode);
		assertEquals(firstMData.hashCode(), secondMData.hashCode());
		assertEquals(firstMData, secondMData);

		firstMData.setOwner(myOwner);
		assertTrue( firstMData.hashCode() != secondMData.hashCode() );
		assertFalse( firstMData.equals(secondMData) );
		secondMData.setOwner(myOwner);
		assertEquals(firstMData.hashCode(), secondMData.hashCode());
		assertEquals(firstMData, secondMData);

		firstMData.setFilename(myFilename);
		assertTrue( firstMData.hashCode() != secondMData.hashCode() );
		assertFalse( firstMData.equals(secondMData) );
		secondMData.setFilename(myFilename);
		assertEquals(firstMData.hashCode(), secondMData.hashCode());
		assertEquals(firstMData, secondMData);

		firstMData.setUploadTimestamp(myTimestamp);
		assertTrue( firstMData.hashCode() != secondMData.hashCode() );
		assertFalse( firstMData.equals(secondMData) );
		secondMData.setUploadTimestamp(myTimestamp);
		assertEquals(firstMData.hashCode(), secondMData.hashCode());
		assertEquals(firstMData, secondMData);

		firstMData.setConflicted(true);
		assertTrue( firstMData.hashCode() != secondMData.hashCode() );
		assertFalse( firstMData.equals(secondMData) );
		secondMData.setConflicted(true);
		assertEquals(firstMData.hashCode(), secondMData.hashCode());
		assertEquals(firstMData, secondMData);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#getAddlDocsTitle()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#splitAddlDocsTitle(java.lang.String)}
	 */
	@Test
	public void testGetAddnDocsTitle() {
		String myExpocode = "CYNS20120124";
		String myOwner = "SocatUser";
		String myFilename = "NatalieSchulte_2013.doc";
		String myTimestamp = "2013-12-11 10:09";

		DashboardMetadata mdata = new DashboardMetadata();
		assertEquals("", mdata.getAddlDocsTitle());
		String[] nameTimePair = DashboardMetadata.splitAddlDocsTitle(mdata.getAddlDocsTitle());
		assertEquals(2, nameTimePair.length);
		assertEquals("", nameTimePair[0]);
		assertEquals("", nameTimePair[1]);
		mdata.setSelected(true);
		mdata.setExpocode(myExpocode);
		mdata.setOwner(myOwner);
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
