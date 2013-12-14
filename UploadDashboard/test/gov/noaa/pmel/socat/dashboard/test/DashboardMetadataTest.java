/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import org.junit.Test;

/**
 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata}.
 * @author Karl Smith
 */
public class DashboardMetadataTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#DashboardMetadata()}.
	 */
	@Test
	public void testDashboardMetadata() {
		DashboardMetadata mdata = new DashboardMetadata();
		assertNotNull( mdata );
	}

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
		mdata.setOwner(null);
		assertFalse( mdata.isSelected() );
		assertEquals("", mdata.getOwner());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#getFilename()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#setFilename(java.lang.String)}.
	 */
	@Test
	public void testGetSetFilename() {
		String myFilename = "CYNS20120124_NatalieSchulte_2013.doc";
		DashboardMetadata mdata = new DashboardMetadata();
		assertEquals("", mdata.getFilename());
		mdata.setFilename(myFilename);
		assertEquals(myFilename, mdata.getFilename());
		assertEquals("", mdata.getOwner());
		assertFalse( mdata.isSelected() );
		mdata.setFilename(null);
		assertEquals("", mdata.getFilename());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#getUploadTimestamp()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#setUploadTimestamp(java.lang.String)}.
	 */
	@Test
	public void testGetUploadTimestamp() {
		String myTimestamp = "2013-12-11 10:09:08";
		DashboardMetadata mdata = new DashboardMetadata();
		assertEquals("", mdata.getUploadTimestamp());
		mdata.setUploadTimestamp(myTimestamp);
		assertEquals(myTimestamp, mdata.getUploadTimestamp());
		assertEquals("", mdata.getFilename());
		assertEquals("", mdata.getOwner());
		assertFalse( mdata.isSelected() );
		mdata.setUploadTimestamp(null);
		assertEquals("", mdata.getUploadTimestamp());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		String myOwner = "SocatUser";
		String myFilename = "CYNS20120124_NatalieSchulte_2013.doc";
		String myTimestamp = "2013-12-11 10:09:08";

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
	}

}
