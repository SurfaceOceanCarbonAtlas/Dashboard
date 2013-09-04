/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;

import java.util.Date;

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
		assertFalse( cruise.isSelected() );
		cruise.setExpocode(null);
		assertEquals("", cruise.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getUploadFilename()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getDataCheckDate()}.
	 */
	@Test
	public void testSetGetUploadFilename() {
		String myFilename = "myUploadFilename.tsv";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals("", cruise.getUploadFilename());
		cruise.setUploadFilename(myFilename);
		assertEquals(myFilename, cruise.getUploadFilename());
		assertEquals("", cruise.getExpocode() );
		assertFalse( cruise.isSelected() );
		cruise.setUploadFilename(null);
		assertEquals("", cruise.getUploadFilename());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getDataCheckDate()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setDataCheckDate(java.util.Date)}.
	 */
	@Test
	public void testSetGetDataCheckDate() {
		Date myDate = new Date(System.currentTimeMillis());
		DashboardCruise cruise = new DashboardCruise();
		assertNull( cruise.getDataCheckDate() );
		cruise.setDataCheckDate(myDate);
		assertEquals(myDate, cruise.getDataCheckDate());
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getExpocode() );
		assertFalse( cruise.isSelected() );
		cruise.setDataCheckDate(null);
		assertNull( cruise.getDataCheckDate() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getMetaCheckDate()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setMetaCheckDate(java.util.Date)}.
	 */
	@Test
	public void testSetGetMetaCheckDate() {
		Date myDate = new Date(System.currentTimeMillis());
		DashboardCruise cruise = new DashboardCruise();
		assertNull( cruise.getMetaCheckDate() );
		cruise.setMetaCheckDate(myDate);
		assertEquals(myDate, cruise.getMetaCheckDate());
		assertNull( cruise.getDataCheckDate() );
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getExpocode() );
		assertFalse( cruise.isSelected() );
		cruise.setMetaCheckDate(null);
		assertNull( cruise.getMetaCheckDate() );
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
		assertNull( cruise.getMetaCheckDate() );
		assertNull( cruise.getDataCheckDate() );
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getExpocode() );
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
		assertNull( cruise.getMetaCheckDate() );
		assertNull( cruise.getDataCheckDate() );
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getExpocode() );
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
		String myExpocode = "ABCD20050728";
		String myFilename = "myUploadFilename.tsv";
		Date myDataCheckDate = new Date(System.currentTimeMillis() - 1000000);
		Date myMetaCheckDate = new Date(System.currentTimeMillis() - 2000000);
		String myQCStatus = "Submitted";
		String myArchiveStatus = "Next SOCAT release";

		DashboardCruise firstCruise = new DashboardCruise();
		assertFalse( firstCruise.equals(null) );
		assertFalse( firstCruise.equals(myDataCheckDate) );
		DashboardCruise secondCruise = new DashboardCruise();
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setSelected(true);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setSelected(true);
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

		firstCruise.setDataCheckDate(myDataCheckDate);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setDataCheckDate(myDataCheckDate);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setMetaCheckDate(myMetaCheckDate);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setMetaCheckDate(myMetaCheckDate);
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
