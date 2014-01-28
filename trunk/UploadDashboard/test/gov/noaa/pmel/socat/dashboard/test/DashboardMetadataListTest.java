/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.*;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList;

import org.junit.Test;

/**
 * Test of {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList}
 * @author Karl Smith
 */
public class DashboardMetadataListTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList#DashboardMetadataList()}.
	 */
	@Test
	public void testDashboardMetadataList() {
		DashboardMetadataList mdataList = new DashboardMetadataList();
		assertNotNull( mdataList );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList#getUsername()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList#setUsername(java.lang.String)}.
	 */
	@Test
	public void testGetSetUsername() {
		String myUsername = "SocatUser";
		DashboardMetadataList mdataList = new DashboardMetadataList();
		assertEquals("", mdataList.getUsername());
		mdataList.setUsername(myUsername);
		assertEquals(myUsername, mdataList.getUsername());
		assertEquals(0, mdataList.size());
		mdataList.setUsername(null);
		assertEquals("", mdataList.getUsername());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList#getOmeMetadata()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList#setOmeMetadata(gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata)}.
	 */
	@Test
	public void testGetSetOmeMetadata() {
		DashboardMetadata myOmeMetadata = new DashboardMetadata();
		myOmeMetadata.setExpocode("XXXX20060515");
		myOmeMetadata.setFilename("XXXX20060515_OME.xml");
		
		DashboardMetadataList mdataList = new DashboardMetadataList();
		assertNull( mdataList.getOmeMetadata() );
		mdataList.setOmeMetadata(myOmeMetadata);
		assertEquals(myOmeMetadata, mdataList.getOmeMetadata());
		assertEquals("", mdataList.getUsername());
		// Actually, the OME metadata filename is suppose to refer to one 
		// of the files in the list, so the following is a bit misleading.
		assertEquals(0, mdataList.size());
		mdataList.setOmeMetadata(null);
		assertEquals(null, mdataList.getOmeMetadata());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		String myUsername = "SocatUser";
		String myOwner = "Cathy";
		DashboardMetadata myOmeMetadata = new DashboardMetadata();
		myOmeMetadata.setExpocode("CYNS20120124");
		myOmeMetadata.setFilename("CYNS20120124_OME.xml");
		String myFilename = "CYNS20120124_NatalieSchulte_2013.doc";
		String myTimestamp = "2013-12-11 10:09:08";
		String otherFilename = "CYNS20120124_NatalieSchulte_2013.pdf";

		DashboardMetadata mdata = new DashboardMetadata();
		mdata.setOwner(myOwner);
		mdata.setFilename(myFilename);
		mdata.setUploadTimestamp(myTimestamp);

		DashboardMetadata sameMData = new DashboardMetadata();
		sameMData.setOwner(myOwner);
		sameMData.setFilename(myFilename);
		sameMData.setUploadTimestamp(myTimestamp);

		DashboardMetadata otherMData = new DashboardMetadata();
		otherMData.setOwner(myOwner);
		otherMData.setFilename(otherFilename);
		otherMData.setUploadTimestamp(myTimestamp);

		DashboardMetadataList firstMDataList = new DashboardMetadataList();
		assertFalse( firstMDataList.equals(null) );
		assertFalse( firstMDataList.equals(mdata) );
		DashboardMetadataList secondMDataList = new DashboardMetadataList();
		assertEquals(firstMDataList.hashCode(), secondMDataList.hashCode());
		assertEquals(firstMDataList, secondMDataList);

		firstMDataList.setUsername(myUsername);
		assertFalse( firstMDataList.hashCode() == secondMDataList.hashCode() );
		assertFalse( firstMDataList.equals(secondMDataList) );
		secondMDataList.setUsername(myUsername);
		assertEquals(firstMDataList.hashCode(), secondMDataList.hashCode());
		assertEquals(firstMDataList, secondMDataList);

		firstMDataList.setOmeMetadata(myOmeMetadata);
		assertFalse( firstMDataList.hashCode() == secondMDataList.hashCode() );
		assertFalse( firstMDataList.equals(secondMDataList) );
		secondMDataList.setOmeMetadata(myOmeMetadata);
		assertEquals(firstMDataList.hashCode(), secondMDataList.hashCode());
		assertEquals(firstMDataList, secondMDataList);

		firstMDataList.put(mdata.getFilename(), mdata);
		assertFalse( firstMDataList.hashCode() == secondMDataList.hashCode() );
		assertFalse( firstMDataList.equals(secondMDataList) );
		secondMDataList.put(sameMData.getFilename(), sameMData);
		assertEquals(firstMDataList.hashCode(), secondMDataList.hashCode());
		assertEquals(firstMDataList, secondMDataList);
		secondMDataList.put(otherMData.getFilename(), otherMData);
		assertFalse( firstMDataList.hashCode() == secondMDataList.hashCode() );
		assertFalse( firstMDataList.equals(secondMDataList) );

		secondMDataList.clear();
		assertEquals(myUsername, secondMDataList.getUsername());
		secondMDataList.put(otherMData.getFilename(), otherMData);
		assertFalse( firstMDataList.hashCode() == secondMDataList.hashCode() );
		assertFalse( firstMDataList.equals(secondMDataList) );
		firstMDataList.put(otherMData.getFilename(), otherMData);
		secondMDataList.put(sameMData.getFilename(), sameMData);
		assertEquals(firstMDataList.hashCode(), secondMDataList.hashCode());
		assertEquals(firstMDataList, secondMDataList);
	}

}
