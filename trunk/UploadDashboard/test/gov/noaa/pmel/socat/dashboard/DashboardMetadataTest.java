/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

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
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#getUploadFilename()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#setUploadFilename(java.lang.String)}.
	 */
	@Test
	public void testGetSetUploadFilename() {
		String myUploadFilename = "NatalieSchulte_2013.doc";
		DashboardMetadata mdata = new DashboardMetadata();
		assertEquals("", mdata.getUploadFilename());
		mdata.setUploadFilename(myUploadFilename);
		assertEquals(myUploadFilename, mdata.getUploadFilename());
		assertEquals("", mdata.getOwner());
		assertFalse( mdata.isSelected() );
		mdata.setUploadFilename(null);
		assertEquals("", mdata.getUploadFilename());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#getExpocodeFilename()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#setExpocodeFilename(java.lang.String)}.
	 */
	@Test
	public void testGetSetExpocodeFilename() {
		String myExpocodeFilename = "CYNS20120124_metadata.doc";
		DashboardMetadata mdata = new DashboardMetadata();
		assertEquals("", mdata.getExpocodeFilename());
		mdata.setExpocodeFilename(myExpocodeFilename);
		assertEquals(myExpocodeFilename, mdata.getExpocodeFilename());
		assertEquals("", mdata.getUploadFilename());
		assertEquals("", mdata.getOwner());
		assertFalse( mdata.isSelected() );
		mdata.setExpocodeFilename(null);
		assertEquals("", mdata.getExpocodeFilename());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEqualsObject() {
		String myOwner = "SocatUser";
		String myUploadFilename = "NatalieSchulte_2013.doc";
		String myExpocodeFilename = "CYNS20120124_metadata.doc";

		DashboardMetadata firstMData = new DashboardMetadata();
		assertFalse( firstMData.equals(null) );
		assertFalse( firstMData.equals(myUploadFilename) );
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

		firstMData.setUploadFilename(myUploadFilename);
		assertTrue( firstMData.hashCode() != secondMData.hashCode() );
		assertFalse( firstMData.equals(secondMData) );
		secondMData.setUploadFilename(myUploadFilename);
		assertEquals(firstMData.hashCode(), secondMData.hashCode());
		assertEquals(firstMData, secondMData);

		firstMData.setExpocodeFilename(myExpocodeFilename);
		assertTrue( firstMData.hashCode() != secondMData.hashCode() );
		assertFalse( firstMData.equals(secondMData) );
		secondMData.setExpocodeFilename(myExpocodeFilename);
		assertEquals(firstMData.hashCode(), secondMData.hashCode());
		assertEquals(firstMData, secondMData);
	}

}
