/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;

import org.junit.Test;

/**
 * @author Karl Smith
 */
public class DashboardCruiseTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#DashboardCruise()}.
	 */
	@Test
	public void testDashboardCruise() {
		DashboardCruise cruise = new DashboardCruise();
		assertNotNull( cruise );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#isSelected()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setSelected(boolean)}.
	 */
	@Test
	public void testSetIsSelected() {
		DashboardCruise cruise = new DashboardCruise();
		assertFalse( cruise.isSelected() );
		cruise.setSelected(true);
		assertTrue( cruise.isSelected() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getOwner()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setOwner(java.lang.String)}.
	 */
	@Test
	public void testSetGetOwner() {
		String myOwner = "SocatUser";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals("", cruise.getOwner());
		cruise.setOwner(myOwner);
		assertEquals(myOwner, cruise.getOwner() );
		assertFalse( cruise.isSelected() );
		cruise.setOwner(null);
		assertEquals("", cruise.getOwner());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getExpocode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testSetGetExpocode() {
		String myExpocode = "ABCD20050728";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals("", cruise.getExpocode());
		cruise.setExpocode(myExpocode);
		assertEquals(myExpocode, cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setExpocode(null);
		assertEquals("", cruise.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getUploadFilename()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getDataCheckStatus()}.
	 */
	@Test
	public void testSetGetUploadFilename() {
		String myFilename = "myUploadFilename.tsv";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals("", cruise.getUploadFilename());
		cruise.setUploadFilename(myFilename);
		assertEquals(myFilename, cruise.getUploadFilename());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setUploadFilename(null);
		assertEquals("", cruise.getUploadFilename());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getDataCheckStatus()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setDataCheckStatus(java.lang.String)}.
	 */
	@Test
	public void testSetGetDataCheckStatus() {
		String myDataStatus = "Acceptable";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals("", cruise.getDataCheckStatus());
		cruise.setDataCheckStatus(myDataStatus);
		assertEquals(myDataStatus, cruise.getDataCheckStatus());
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setDataCheckStatus(null);
		assertEquals("", cruise.getDataCheckStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getMetadataCheckStatus()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setMetadataCheckStatus(java.lang.String)}.
	 */
	@Test
	public void testSetGetMetadataCheckStatus() {
		String myMetaStatus = "Questionable";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals("", cruise.getMetadataCheckStatus());
		cruise.setMetadataCheckStatus(myMetaStatus);
		assertEquals(myMetaStatus, cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setMetadataCheckStatus(null);
		assertEquals("", cruise.getMetadataCheckStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getQCStatus()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setQCStatus(java.lang.String)}.
	 */
	@Test
	public void testSetGetQCStatus() {
		String myQCStatus = "Submitted";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals("", cruise.getQCStatus());
		cruise.setQCStatus(myQCStatus);
		assertEquals(myQCStatus, cruise.getQCStatus());
		assertEquals("", cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setQCStatus(null);
		assertEquals("", cruise.getQCStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getArchiveStatus()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setArchiveStatus(java.lang.String)}.
	 */
	@Test
	public void testSetGetArchiveStatus() {
		String myArchiveStatus = "Next SOCAT release";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals("", cruise.getArchiveStatus());
		cruise.setArchiveStatus(myArchiveStatus);
		assertEquals(myArchiveStatus, cruise.getArchiveStatus());
		assertEquals("", cruise.getQCStatus());
		assertEquals("", cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setArchiveStatus(null);
		assertEquals("", cruise.getArchiveStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		String myOwner = "SocatUser";
		String myExpocode = "ABCD20050728";
		String myFilename = "myUploadFilename.tsv";
		String myDataStatus = "Acceptable";
		String myMetaStatus = "Questionable";
		String myQCStatus = "Submitted";
		String myArchiveStatus = "Next SOCAT release";

		DashboardCruise firstCruise = new DashboardCruise();
		assertFalse( firstCruise.equals(null) );
		assertFalse( firstCruise.equals(myDataStatus) );
		DashboardCruise secondCruise = new DashboardCruise();
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setSelected(true);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setSelected(true);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setOwner(myOwner);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setOwner(myOwner);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setExpocode(myExpocode);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setExpocode(myExpocode);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setUploadFilename(myFilename);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setUploadFilename(myFilename);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setDataCheckStatus(myDataStatus);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setDataCheckStatus(myDataStatus);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setMetadataCheckStatus(myMetaStatus);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setMetadataCheckStatus(myMetaStatus);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setQCStatus(myQCStatus);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setQCStatus(myQCStatus);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setArchiveStatus(myArchiveStatus);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setArchiveStatus(myArchiveStatus);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);
	}

}
