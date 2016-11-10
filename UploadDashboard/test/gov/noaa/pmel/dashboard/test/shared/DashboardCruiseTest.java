/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.WoceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import org.junit.Test;

/**
 * Unit tests for methods of {@link gov.noaa.pmel.dashboard.shared.DashboardCruise}.
 * 
 * @author Karl Smith
 */
public class DashboardCruiseTest {

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#DashboardCruise()}.
	 */
	@Test
	public void testDashboardCruise() {
		DashboardCruise cruise = new DashboardCruise();
		assertNotNull( cruise );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#isSelected()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setSelected(boolean)}.
	 */
	@Test
	public void testSetIsSelected() {
		DashboardCruise cruise = new DashboardCruise();
		assertFalse( cruise.isSelected() );
		cruise.setSelected(true);
		assertTrue( cruise.isSelected() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getOwner()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setOwner(java.lang.String)}.
	 */
	@Test
	public void testSetGetOwner() {
		String myOwner = "SocatUser";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		cruise.setOwner(myOwner);
		assertEquals(myOwner, cruise.getOwner() );
		assertFalse( cruise.isSelected() );
		cruise.setOwner(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getExpocode()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setExpocode(java.lang.String)}.
	 */
	@Test
	public void testSetGetExpocode() {
		String myExpocode = "ABCD20050728";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode());
		cruise.setExpocode(myExpocode);
		assertEquals(myExpocode, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setExpocode(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getDataCheckStatus()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setDataCheckStatus(java.lang.String)}.
	 */
	@Test
	public void testSetGetDataCheckStatus() {
		String myDataStatus = "Acceptable";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		cruise.setDataCheckStatus(myDataStatus);
		assertEquals(myDataStatus, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setDataCheckStatus(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getOmeTimestamp()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setOmeTimestamp(java.lang.String)}.
	 */
	@Test
	public void testSetGetOmeTimestamp() {
		String myOmeFilename = "2014-02-21 9:22";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		cruise.setOmeTimestamp(myOmeFilename);
		assertEquals(myOmeFilename, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setOmeTimestamp(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getAddlDocs()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setAddlDocs(java.util.TreeSet)}.
	 */
	@Test
	public void testSetGetAddlDocNames() {
		TreeSet<String> myMetaNames = new TreeSet<String>(Arrays.asList(
				"ABCD20050728.txt; 2014-02-21 9:23", 
				"ABCD20050728_2.doc; 2014-02-21 9:24", 
				"ABCD20050728_3.pdf; 2014-02-21 9:25"));
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getAddlDocs().size());
		cruise.setAddlDocs(myMetaNames);
		assertEquals(myMetaNames, cruise.getAddlDocs());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setAddlDocs(null);
		assertEquals(0, cruise.getAddlDocs().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getQcStatus()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setQcStatus(java.lang.String)}.
	 */
	@Test
	public void testSetGetQCStatus() {
		String myQCStatus = "Submitted";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		cruise.setQcStatus(myQCStatus);
		assertEquals(myQCStatus, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setQcStatus(null);
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getArchiveStatus()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setArchiveStatus(java.lang.String)}.
	 */
	@Test
	public void testSetGetArchiveStatus() {
		String myArchiveStatus = "Next SOCAT release";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		cruise.setArchiveStatus(myArchiveStatus);
		assertEquals(myArchiveStatus, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setArchiveStatus(null);
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getUploadFilename()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setUploadFilename(java.lang.String)}.
	 */
	@Test
	public void testSetGetUploadFilename() {
		String myFilename = "myUploadFilename.tsv";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		cruise.setUploadFilename(myFilename);
		assertEquals(myFilename, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setUploadFilename(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getNumDataRows()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setNumDataRows(int)}.
	 */
	@Test
	public void testSetGetNumDataRows() {
		int myNumDataRows = 2581;
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getNumDataRows());
		cruise.setNumDataRows(myNumDataRows);
		assertEquals(myNumDataRows, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getDataColTypes()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setDataColTypes(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetDataColTypes() {
		ArrayList<DataColumnType> myDataTypes = 
				new ArrayList<DataColumnType>(Arrays.asList(
					DashboardUtils.TIMESTAMP,
					DashboardUtils.LONGITUDE,
					DashboardUtils.LATITUDE,
					DashboardUtils.SAMPLE_DEPTH
				));
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getDataColTypes().size());
		cruise.setDataColTypes(myDataTypes);
		assertEquals(myDataTypes, cruise.getDataColTypes());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setDataColTypes(null);
		assertEquals(0, cruise.getDataColTypes().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getUserColNames()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setUserColNames(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetUserColNames() {
		ArrayList<String> myUserColNames = new ArrayList<String>(
				Arrays.asList("time", "lon", "lat", "salinity", "temp", "pres", "xco2")); 
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getUserColNames().size());
		cruise.setUserColNames(myUserColNames);
		assertEquals(myUserColNames, cruise.getUserColNames());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setUserColNames(null);
		assertEquals(0, cruise.getUserColNames().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getNumErrorRows()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setNumErrorRows(int)}.
	 */
	@Test
	public void testSetGetNumErrorRows() {
		int myNumErrorMsgs = 4;
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getNumErrorRows());
		cruise.setNumErrorRows(myNumErrorMsgs);
		assertEquals(myNumErrorMsgs, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getNumWarnRows()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setNumWarnRows(int)}.
	 */
	@Test
	public void testSetGetNumWarnRows() {
		int myNumWarnMsgs = 14;
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getNumWarnRows());
		cruise.setNumWarnRows(myNumWarnMsgs);
		assertEquals(myNumWarnMsgs, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getCheckerWoceThrees()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setCheckerWoceThrees(java.util.Collection)}.
	 */
	@Test
	public void testSetGetCheckerWoceThrees() {
		TreeSet<WoceType> myWoceThrees = new TreeSet<WoceType>(Arrays.asList(
				new WoceType("WOCE_CO2_water", 5, 2), 
				new WoceType("WOCE_CO2_water", 8, 12),
				new WoceType("WOCE_CO2_water", 3, 22)
		));
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getCheckerWoceThrees().size());
		cruise.setCheckerWoceThrees(myWoceThrees);
		assertEquals(myWoceThrees, cruise.getCheckerWoceThrees());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setCheckerWoceThrees(null);
		assertEquals(0, cruise.getCheckerWoceThrees().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getCheckerWoceFours()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setCheckerWoceFours(java.util.Collection)}.
	 */
	@Test
	public void testSetGetCheckerWoceFours() {
		TreeSet<WoceType> myWoceFours = new TreeSet<WoceType>(Arrays.asList(
				new WoceType("WOCE_CO2_water", 7, 5),
				new WoceType("WOCE_CO2_water", 3, 15),
				new WoceType("WOCE_CO2_water", 3, 25)
		));
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getCheckerWoceFours().size());
		cruise.setCheckerWoceFours(myWoceFours);
		assertEquals(myWoceFours, cruise.getCheckerWoceFours());
		assertEquals(0, cruise.getCheckerWoceThrees().size());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setCheckerWoceFours(null);
		assertEquals(0, cruise.getCheckerWoceFours().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getUserWoceThrees()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setUserWoceThrees(java.util.Collection)}.
	 */
	@Test
	public void testSetGetUserWoceThrees() {
		TreeSet<WoceType> userWoceThrees = new TreeSet<WoceType>(Arrays.asList(
				new WoceType("WOCE_CO2_water", 4, 31),
				new WoceType("WOCE_CO2_water", 5, 35),
				new WoceType("WOCE_CO2_atm", 12, 35)
		));
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getUserWoceThrees().size());
		cruise.setUserWoceThrees(userWoceThrees);
		assertEquals(userWoceThrees, cruise.getUserWoceThrees());
		assertEquals(0, cruise.getCheckerWoceFours().size());
		assertEquals(0, cruise.getCheckerWoceThrees().size());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setUserWoceThrees(null);
		assertEquals(0, cruise.getUserWoceThrees().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getUserWoceFours()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setUserWoceFours(java.util.Collection)}.
	 */
	@Test
	public void testSetGetUserWoceFours() {
		TreeSet<WoceType> userWoceFours = new TreeSet<WoceType>(Arrays.asList(
				new WoceType("WOCE_CO2_water", 13, 43),
				new WoceType("WOCE_CO2_atm", 13, 44),
				new WoceType("WOCE_CO2_water", 8, 45) 
		));
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(0, cruise.getUserWoceFours().size());
		cruise.setUserWoceFours(userWoceFours);
		assertEquals(userWoceFours, cruise.getUserWoceFours());
		assertEquals(0, cruise.getUserWoceThrees().size());
		assertEquals(0, cruise.getCheckerWoceFours().size());
		assertEquals(0, cruise.getCheckerWoceThrees().size());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setUserWoceFours(null);
		assertEquals(0, cruise.getUserWoceFours().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getUploadTimestamp()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setUploadTimestamp(java.lang.String)}.
	 */
	@Test
	public void testSetGetUploadTimestamp() {
		String uploadTimestamp = "2015-10-20 13:14:15";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadTimestamp());
		cruise.setUploadTimestamp(uploadTimestamp);
		assertEquals(uploadTimestamp, cruise.getUploadTimestamp());
		assertEquals(0, cruise.getUserWoceFours().size());
		assertEquals(0, cruise.getUserWoceThrees().size());
		assertEquals(0, cruise.getCheckerWoceFours().size());
		assertEquals(0, cruise.getCheckerWoceThrees().size());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setUploadTimestamp(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadTimestamp());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getOrigDOI()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setOrigDOI(java.lang.String)}.
	 */
	@Test
	public void testSetGetOrigDOI() {
		String origDOI = "ORIGDOI12345";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOrigDoi());
		cruise.setOrigDoi(origDOI);
		assertEquals(origDOI, cruise.getOrigDoi());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadTimestamp());
		assertEquals(0, cruise.getUserWoceFours().size());
		assertEquals(0, cruise.getUserWoceThrees().size());
		assertEquals(0, cruise.getCheckerWoceFours().size());
		assertEquals(0, cruise.getCheckerWoceThrees().size());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setOrigDoi(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOrigDoi());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#getSocatDOI()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setSocatDOI(java.lang.String)}.
	 */
	@Test
	public void testSetGetSocatDOI() {
		String socatDOI = "SOCATDOI12345";
		DashboardCruise cruise = new DashboardCruise();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getSocatDoi());
		cruise.setSocatDoi(socatDOI);
		assertEquals(socatDOI, cruise.getSocatDoi());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOrigDoi());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadTimestamp());
		assertEquals(0, cruise.getUserWoceFours().size());
		assertEquals(0, cruise.getUserWoceThrees().size());
		assertEquals(0, cruise.getCheckerWoceFours().size());
		assertEquals(0, cruise.getCheckerWoceThrees().size());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.QC_STATUS_NOT_SUBMITTED, cruise.getQcStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getExpocode() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertFalse( cruise.isSelected() );
		cruise.setSocatDoi(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getSocatDoi());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		String myOwner = "SocatUser";
		String myExpocode = "ABCD20050728";
		String myDataStatus = "Acceptable";
		String myOmeTimestamp = "2014-02-21 9:22";
		TreeSet<String> myMetaNames = new TreeSet<String>(Arrays.asList(
				"ABCD20050728.txt; 2014-02-21 9:23", 
				"ABCD20050728_2.doc; 2014-02-21 9:24", 
				"ABCD20050728_3.pdf; 2014-02-21 9:25"));
		String myQCStatus = "Submitted";
		String myArchiveStatus = "Next SOCAT release";
		String myFilename = "myUploadFilename.tsv";
		String myUploadTimestamp = "2015-10-20 13:14:15";
		String myOrigDOI = "OrigDOI12345";
		String mySocatDOI = "SOCATDOI12345";
		int myNumDataRows = 2581;
		int myNumErrorMsgs = 4;
		int myNumWarnMsgs = 14;
		ArrayList<DataColumnType> myDataColTypes = 
				new ArrayList<DataColumnType>(Arrays.asList(
					DashboardUtils.TIMESTAMP,
					DashboardUtils.LONGITUDE,
					DashboardUtils.LATITUDE,
					DashboardUtils.SAMPLE_DEPTH
				));
		ArrayList<String> myUserColNames = new ArrayList<String>(
				Arrays.asList("time", "lon", "lat", "depth")); 
		TreeSet<WoceType> myWoceThrees = new TreeSet<WoceType>(Arrays.asList(
				new WoceType("WOCE_CO2_water", 5, 2), 
				new WoceType("WOCE_CO2_water", 8, 12),
				new WoceType("WOCE_CO2_water", 3, 22)
		));
		TreeSet<WoceType> myWoceFours = new TreeSet<WoceType>(Arrays.asList(
				new WoceType("WOCE_CO2_water", 7, 5),
				new WoceType("WOCE_CO2_water", 3, 15),
				new WoceType("WOCE_CO2_water", 3, 25)
		));
		TreeSet<WoceType> userWoceThrees = new TreeSet<WoceType>(Arrays.asList(
				new WoceType("WOCE_CO2_water", 4, 31),
				new WoceType("WOCE_CO2_water", 5, 35),
				new WoceType("WOCE_CO2_atm", 12, 35)
		));
		TreeSet<WoceType> userWoceFours = new TreeSet<WoceType>(Arrays.asList(
				new WoceType("WOCE_CO2_water", 13, 43),
				new WoceType("WOCE_CO2_atm", 13, 44),
				new WoceType("WOCE_CO2_water", 8, 45) 
		));

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

		firstCruise.setOmeTimestamp(myOmeTimestamp);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setOmeTimestamp(myOmeTimestamp);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setAddlDocs(myMetaNames);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setAddlDocs(myMetaNames);
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

		firstCruise.setNumErrorRows(myNumErrorMsgs);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setNumErrorRows(myNumErrorMsgs);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setNumWarnRows(myNumWarnMsgs);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setNumWarnRows(myNumWarnMsgs);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setDataColTypes(myDataColTypes);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setDataColTypes(myDataColTypes);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setUserColNames(myUserColNames);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setUserColNames(myUserColNames);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setCheckerWoceThrees(myWoceThrees);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setCheckerWoceThrees(myWoceThrees);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setCheckerWoceFours(myWoceFours);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setCheckerWoceFours(myWoceFours);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setUserWoceThrees(userWoceThrees);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setUserWoceThrees(userWoceThrees);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setUserWoceFours(userWoceFours);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setUserWoceFours(userWoceFours);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setUploadTimestamp(myUploadTimestamp);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setUploadTimestamp(myUploadTimestamp);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setOrigDoi(myOrigDOI);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setOrigDoi(myOrigDOI);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setSocatDoi(mySocatDOI);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setSocatDoi(mySocatDOI);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#isSelected()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardCruise#setSelected(boolean)}.
	 */
	@Test
	public void testSetIsEditable() {
		DashboardCruise cruise = new DashboardCruise();
		assertTrue( cruise.isEditable() );


		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_WITH_NEXT_RELEASE);

		cruise.setQcStatus(DashboardUtils.QC_STATUS_NOT_SUBMITTED);
		assertNotNull( cruise.isEditable() );
		assertTrue( cruise.isEditable() );

		cruise.setQcStatus(DashboardUtils.QC_STATUS_EXCLUDED);
		assertNotNull( cruise.isEditable() );
		assertTrue( cruise.isEditable() );

		cruise.setQcStatus(DashboardUtils.QC_STATUS_SUSPENDED);
		assertNotNull( cruise.isEditable() );
		assertTrue( cruise.isEditable() );

		cruise.setQcStatus(DashboardUtils.QC_STATUS_SUBMITTED);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setQcStatus(DashboardUtils.QC_STATUS_ACCEPTED_A);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setQcStatus(DashboardUtils.QC_STATUS_ACCEPTED_B);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setQcStatus(DashboardUtils.QC_STATUS_ACCEPTED_C);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setQcStatus(DashboardUtils.QC_STATUS_ACCEPTED_D);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setQcStatus(DashboardUtils.QC_STATUS_ACCEPTED_E);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setQcStatus(DashboardUtils.QC_STATUS_CONFLICT);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setQcStatus(DashboardUtils.QC_STATUS_RENAMED);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );


		cruise.setQcStatus(DashboardUtils.QC_STATUS_SUBMITTED);

		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_WITH_NEXT_RELEASE);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_SENT_FOR_ARHCIVAL);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_OWNER_TO_ARCHIVE);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_ARCHIVED);
		assertNull( cruise.isEditable() );
	}

}
