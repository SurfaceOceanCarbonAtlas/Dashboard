/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import gov.noaa.pmel.socat.dashboard.shared.CruiseDataColumnType;
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
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setMetadataCheckStatus(null);
		assertEquals("", cruise.getMetadataCheckStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getQcStatus()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setQcStatus(java.lang.String)}.
	 */
	@Test
	public void testSetGetQCStatus() {
		String myQCStatus = "Submitted";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals("", cruise.getQcStatus());
		cruise.setQcStatus(myQCStatus);
		assertEquals(myQCStatus, cruise.getQcStatus());
		assertEquals("", cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setQcStatus(null);
		assertEquals("", cruise.getQcStatus());
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
		assertEquals("", cruise.getQcStatus());
		assertEquals("", cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setArchiveStatus(null);
		assertEquals("", cruise.getArchiveStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getUploadFilename()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setUploadFilename(java.lang.String)}.
	 */
	@Test
	public void testSetGetUploadFilename() {
		String myFilename = "myUploadFilename.tsv";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals("", cruise.getUploadFilename());
		cruise.setUploadFilename(myFilename);
		assertEquals(myFilename, cruise.getUploadFilename());
		assertEquals("", cruise.getArchiveStatus());
		assertEquals("", cruise.getQcStatus());
		assertEquals("", cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setUploadFilename(null);
		assertEquals("", cruise.getUploadFilename());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getNumDataRows()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setNumDataRows(int)}.
	 */
	@Test
	public void testSetGetNumDataRows() {
		int myNumDataRows = 2581;
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getNumDataRows());
		cruise.setNumDataRows(myNumDataRows);
		assertEquals(myNumDataRows, cruise.getNumDataRows());
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getArchiveStatus());
		assertEquals("", cruise.getQcStatus());
		assertEquals("", cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getDataColTypes()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setDataColTypes(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetDataColTypes() {
		ArrayList<CruiseDataColumnType> myDataColTypes = 
				new ArrayList<CruiseDataColumnType>(Arrays.asList(
					CruiseDataColumnType.TIMESTAMP,
					CruiseDataColumnType.LONGITUDE,
					CruiseDataColumnType.LATITUDE,
					CruiseDataColumnType.SAMPLE_SALINITY,
					CruiseDataColumnType.EQUILIBRATOR_TEMPERATURE,
					CruiseDataColumnType.EQUILIBRATOR_PRESSURE,
					CruiseDataColumnType.XCO2_EQU
				));
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getDataColTypes().size());
		cruise.setDataColTypes(myDataColTypes);
		assertEquals(myDataColTypes, cruise.getDataColTypes());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getArchiveStatus());
		assertEquals("", cruise.getQcStatus());
		assertEquals("", cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setDataColTypes(null);
		assertEquals(0, cruise.getDataColTypes().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getUserColIndices()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setUserColIndices(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetUserColIndices() {
		ArrayList<Integer> myUserColIndices = 
				new ArrayList<Integer>(Arrays.asList(3, 1, 2, 6, 4, 5, 0)); 
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getUserColIndices().size());
		cruise.setUserColIndices(myUserColIndices);
		assertEquals(myUserColIndices, cruise.getUserColIndices());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getArchiveStatus());
		assertEquals("", cruise.getQcStatus());
		assertEquals("", cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setUserColIndices(null);
		assertEquals(0, cruise.getUserColIndices().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getUserColNames()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setUserColNames(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetUserColNames() {
		ArrayList<String> myUserColNames = new ArrayList<String>(
				Arrays.asList("time", "lon", "lat", "sal", "temp", "pres", "xco2")); 
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getUserColNames().size());
		cruise.setUserColNames(myUserColNames);
		assertEquals(myUserColNames, cruise.getUserColNames());
		assertEquals(0, cruise.getUserColIndices().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getArchiveStatus());
		assertEquals("", cruise.getQcStatus());
		assertEquals("", cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setUserColNames(null);
		assertEquals(0, cruise.getUserColNames().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getDataColUnits()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setDataColUnits(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetDataColUnits() {
		ArrayList<String> myDataColUnits = new ArrayList<String>(
				Arrays.asList("UTC", "deg E", "deg N", "PSU", "deg C", "mm Hg", "umol/mol")); 
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getDataColUnits().size());
		cruise.setDataColUnits(myDataColUnits);
		assertEquals(myDataColUnits, cruise.getDataColUnits());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getUserColIndices().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getArchiveStatus());
		assertEquals("", cruise.getQcStatus());
		assertEquals("", cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setDataColUnits(null);
		assertEquals(0, cruise.getDataColUnits().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#getDataColDescriptions()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#setDataColDescriptions(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetDataColDescriptions() {
		ArrayList<String> myDataColDescriptions = new ArrayList<String>(Arrays.asList(
				"time of sample", 
				"longitude of sample", 
				"latitude of sample", 
				"salinity of sample", 
				"equilibrator temperature", 
				"equilibrator pressure", 
				"measured CO2 of sample")); 
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getDataColDescriptions().size());
		cruise.setDataColDescriptions(myDataColDescriptions);
		assertEquals(myDataColDescriptions, cruise.getDataColDescriptions());
		assertEquals(0, cruise.getDataColUnits().size());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getUserColIndices().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals("", cruise.getUploadFilename());
		assertEquals("", cruise.getArchiveStatus());
		assertEquals("", cruise.getQcStatus());
		assertEquals("", cruise.getMetadataCheckStatus());
		assertEquals("", cruise.getDataCheckStatus());
		assertEquals("", cruise.getExpocode() );
		assertEquals("", cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setDataColDescriptions(null);
		assertEquals(0, cruise.getDataColDescriptions().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#hashCode()}
	 * and {@link gov.noaa.pmel.socat.dashboard.shared.DashboardCruise#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		String myOwner = "SocatUser";
		String myExpocode = "ABCD20050728";
		String myDataStatus = "Acceptable";
		String myMetaStatus = "Questionable";
		String myQCStatus = "Submitted";
		String myArchiveStatus = "Next SOCAT release";
		String myFilename = "myUploadFilename.tsv";
		int myNumDataRows = 2581;
		ArrayList<CruiseDataColumnType> myDataColTypes = 
				new ArrayList<CruiseDataColumnType>(Arrays.asList(
					CruiseDataColumnType.TIMESTAMP,
					CruiseDataColumnType.LONGITUDE,
					CruiseDataColumnType.LATITUDE,
					CruiseDataColumnType.SAMPLE_SALINITY,
					CruiseDataColumnType.EQUILIBRATOR_TEMPERATURE,
					CruiseDataColumnType.EQUILIBRATOR_PRESSURE,
					CruiseDataColumnType.XCO2_EQU
				));
		ArrayList<Integer> myUserColIndices = 
				new ArrayList<Integer>(Arrays.asList(3, 1, 2, 6, 4, 5, 0)); 
		ArrayList<String> myUserColNames = new ArrayList<String>(
				Arrays.asList("time", "lon", "lat", "sal", "temp", "pres", "xco2")); 
		ArrayList<String> myDataColUnits = new ArrayList<String>(
				Arrays.asList("UTC", "deg E", "deg N", "PSU", "deg C", "mm Hg", "umol/mol")); 
		ArrayList<String> myDataColDescriptions = new ArrayList<String>(Arrays.asList(
				"time of sample", 
				"longitude of sample", 
				"latitude of sample", 
				"salinity of sample", 
				"equilibrator temperature", 
				"equilibrator pressure", 
				"measured CO2 of sample")); 

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

		firstCruise.setQcStatus(myQCStatus);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setQcStatus(myQCStatus);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setArchiveStatus(myArchiveStatus);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setArchiveStatus(myArchiveStatus);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setUploadFilename(myFilename);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setUploadFilename(myFilename);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setNumDataRows(myNumDataRows);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setNumDataRows(myNumDataRows);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setDataColTypes(myDataColTypes);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setDataColTypes(myDataColTypes);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setUserColIndices(myUserColIndices);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setUserColIndices(myUserColIndices);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setUserColNames(myUserColNames);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setUserColNames(myUserColNames);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setDataColUnits(myDataColUnits);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setDataColUnits(myDataColUnits);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setDataColDescriptions(myDataColDescriptions);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setDataColDescriptions(myDataColDescriptions);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);
	}

}
