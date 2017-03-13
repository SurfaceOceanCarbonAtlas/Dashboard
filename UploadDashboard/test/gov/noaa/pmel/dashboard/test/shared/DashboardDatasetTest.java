/**
 * 
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import org.junit.Test;

import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCFlag;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

/**
 * Unit tests for methods of {@link gov.noaa.pmel.dashboard.shared.DashboardDataset}.
 * 
 * @author Karl Smith
 */
public class DashboardDatasetTest {

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#isSelected()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setSelected(boolean)}.
	 */
	@Test
	public void testSetIsSelected() {
		DashboardDataset cruise = new DashboardDataset();
		assertFalse( cruise.isSelected() );
		cruise.setSelected(true);
		assertTrue( cruise.isSelected() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getVersion()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setVersion(java.lang.String)}.
	 */
	@Test
	public void testSetGetVersion() {
		String myVersion = "2.5";
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		cruise.setVersion(myVersion);
		assertEquals(myVersion, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setVersion(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getOwner()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setOwner(java.lang.String)}.
	 */
	@Test
	public void testSetGetOwner() {
		String myOwner = "SocatUser";
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		cruise.setOwner(myOwner);
		assertEquals(myOwner, cruise.getOwner() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setOwner(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getDatasetId()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setDatasetId(java.lang.String)}.
	 */
	@Test
	public void testSetGetExpocode() {
		String myExpocode = "ABCD20050728";
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
		cruise.setDatasetId(myExpocode);
		assertEquals(myExpocode, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setDatasetId(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getDataCheckStatus()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setDataCheckStatus(java.lang.String)}.
	 */
	@Test
	public void testSetGetDataCheckStatus() {
		String myDataStatus = "Acceptable";
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		cruise.setDataCheckStatus(myDataStatus);
		assertEquals(myDataStatus, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setDataCheckStatus(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getOmeTimestamp()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setOmeTimestamp(java.lang.String)}.
	 */
	@Test
	public void testSetGetOmeTimestamp() {
		String myOmeFilename = "2014-02-21 9:22";
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		cruise.setOmeTimestamp(myOmeFilename);
		assertEquals(myOmeFilename, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setOmeTimestamp(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getAddlDocs()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setAddlDocs(java.util.TreeSet)}.
	 */
	@Test
	public void testSetGetAddlDocNames() {
		TreeSet<String> myMetaNames = new TreeSet<String>(Arrays.asList(
				"ABCD20050728.txt; 2014-02-21 9:23", 
				"ABCD20050728_2.doc; 2014-02-21 9:24", 
				"ABCD20050728_3.pdf; 2014-02-21 9:25"));
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(0, cruise.getAddlDocs().size());
		cruise.setAddlDocs(myMetaNames);
		assertEquals(myMetaNames, cruise.getAddlDocs());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setAddlDocs(null);
		assertEquals(0, cruise.getAddlDocs().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getSubmitStatus()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setSubmitStatus(java.lang.String)}.
	 */
	@Test
	public void testSetGetQCStatus() {
		String myQCStatus = "Submitted";
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		cruise.setSubmitStatus(myQCStatus);
		assertEquals(myQCStatus, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setSubmitStatus(null);
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getArchiveStatus()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setArchiveStatus(java.lang.String)}.
	 */
	@Test
	public void testSetGetArchiveStatus() {
		String myArchiveStatus = "Next year";
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		cruise.setArchiveStatus(myArchiveStatus);
		assertEquals(myArchiveStatus, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setArchiveStatus(null);
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getUploadFilename()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setUploadFilename(java.lang.String)}.
	 */
	@Test
	public void testSetGetUploadFilename() {
		String myFilename = "myUploadFilename.tsv";
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		cruise.setUploadFilename(myFilename);
		assertEquals(myFilename, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setUploadFilename(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getNumDataRows()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setNumDataRows(int)}.
	 */
	@Test
	public void testSetGetNumDataRows() {
		int myNumDataRows = 2581;
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(0, cruise.getNumDataRows());
		cruise.setNumDataRows(myNumDataRows);
		assertEquals(myNumDataRows, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getDataColTypes()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setDataColTypes(java.util.ArrayList)}.
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
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(0, cruise.getDataColTypes().size());
		cruise.setDataColTypes(myDataTypes);
		assertEquals(myDataTypes, cruise.getDataColTypes());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setDataColTypes(null);
		assertEquals(0, cruise.getDataColTypes().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getUserColNames()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setUserColNames(java.util.ArrayList)}.
	 */
	@Test
	public void testSetGetUserColNames() {
		ArrayList<String> myUserColNames = new ArrayList<String>(
				Arrays.asList("time", "lon", "lat", "salinity", "temp", "pres", "xco2")); 
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(0, cruise.getUserColNames().size());
		cruise.setUserColNames(myUserColNames);
		assertEquals(myUserColNames, cruise.getUserColNames());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setUserColNames(null);
		assertEquals(0, cruise.getUserColNames().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getNumErrorRows()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setNumErrorRows(int)}.
	 */
	@Test
	public void testSetGetNumErrorRows() {
		int myNumErrorMsgs = 4;
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(0, cruise.getNumErrorRows());
		cruise.setNumErrorRows(myNumErrorMsgs);
		assertEquals(myNumErrorMsgs, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getNumWarnRows()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setNumWarnRows(int)}.
	 */
	@Test
	public void testSetGetNumWarnRows() {
		int myNumWarnMsgs = 14;
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(0, cruise.getNumWarnRows());
		cruise.setNumWarnRows(myNumWarnMsgs);
		assertEquals(myNumWarnMsgs, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getCheckerFlags()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setCheckerFlags(java.util.Collection)}.
	 */
	@Test
	public void testSetGetCheckerFlags() {
		TreeSet<QCFlag> checkerFlags = new TreeSet<QCFlag>(Arrays.asList(
				new QCFlag("WOCE_CO2_water", '3', Severity.WARNING, 5, 2), 
				new QCFlag("WOCE_CO2_water", '3', Severity.WARNING, 8, 12),
				new QCFlag("WOCE_CO2_water", '4', Severity.ERROR, 3, 22)
		));
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(0, cruise.getCheckerFlags().size());
		cruise.setCheckerFlags(checkerFlags);
		assertEquals(checkerFlags, cruise.getCheckerFlags());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setCheckerFlags(null);
		assertEquals(0, cruise.getCheckerFlags().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getUserFlags()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setUserFlags(java.util.Collection)}.
	 */
	@Test
	public void testSetGetUserFlags() {
		TreeSet<QCFlag> userFlags = new TreeSet<QCFlag>(Arrays.asList(
				new QCFlag("WOCE_CO2_water", '4', Severity.ERROR, 4, 31),
				new QCFlag("WOCE_CO2_water", '3', Severity.WARNING, 5, 35),
				new QCFlag("WOCE_CO2_atm", '3', Severity.WARNING, 12, 35)
		));
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(0, cruise.getUserFlags().size());
		cruise.setUserFlags(userFlags);
		assertEquals(userFlags, cruise.getUserFlags());
		assertEquals(0, cruise.getCheckerFlags().size());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setUserFlags(null);
		assertEquals(0, cruise.getUserFlags().size());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getUploadTimestamp()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setUploadTimestamp(java.lang.String)}.
	 */
	@Test
	public void testSetGetUploadTimestamp() {
		String uploadTimestamp = "2015-10-20 13:14:15";
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadTimestamp());
		cruise.setUploadTimestamp(uploadTimestamp);
		assertEquals(uploadTimestamp, cruise.getUploadTimestamp());
		assertEquals(0, cruise.getUserFlags().size());
		assertEquals(0, cruise.getCheckerFlags().size());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setUploadTimestamp(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadTimestamp());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getOrigDOI()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setOrigDOI(java.lang.String)}.
	 */
	@Test
	public void testSetGetOrigDOI() {
		String origDOI = "ORIGDOI12345";
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDoi());
		cruise.setDoi(origDOI);
		assertEquals(origDOI, cruise.getDoi());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadTimestamp());
		assertEquals(0, cruise.getUserFlags().size());
		assertEquals(0, cruise.getCheckerFlags().size());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setDoi(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDoi());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#getArchiveDate()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setArchiveDate(java.lang.String)}.
	 */
	@Test
	public void testSetGetArchiveDate() {
		String myArchiveDate = "15-JAN-2016 13:30-5:00";
		DashboardDataset cruise = new DashboardDataset();
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getArchiveDate());
		cruise.setArchiveDate(myArchiveDate);
		assertEquals(myArchiveDate, cruise.getArchiveDate());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDoi());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadTimestamp());
		assertEquals(0, cruise.getUserFlags().size());
		assertEquals(0, cruise.getCheckerFlags().size());
		assertEquals(0, cruise.getNumWarnRows());
		assertEquals(0, cruise.getNumErrorRows());
		assertEquals(0, cruise.getUserColNames().size());
		assertEquals(0, cruise.getDataColTypes().size());
		assertEquals(0, cruise.getNumDataRows());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
		assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
		assertEquals(DashboardUtils.STATUS_NOT_SUBMITTED, cruise.getSubmitStatus());
		assertEquals(0, cruise.getAddlDocs().size());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId() );
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
		assertFalse( cruise.isSelected() );
		cruise.setArchiveDate(null);
		assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getArchiveDate());
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#hashCode()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#equals(java.lang.Object)}.
	 */
	@Test
	public void testHashCodeEquals() {
		String myVersion = "2.5";
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
		String myArchiveDate = "15-JAN-2016 13:30-5:00";
		String myFilename = "myUploadFilename.tsv";
		String myUploadTimestamp = "2015-10-20 13:14:15";
		String myOrigDOI = "OrigDOI12345";
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
		TreeSet<QCFlag> checkerFlags = new TreeSet<QCFlag>(Arrays.asList(
				new QCFlag("WOCE_CO2_water", '3', Severity.WARNING, 5, 2), 
				new QCFlag("WOCE_CO2_water", '4', Severity.ERROR, 8, 12),
				new QCFlag("WOCE_CO2_water", '3', Severity.WARNING, 3, 22)
		));
		TreeSet<QCFlag> userFlags = new TreeSet<QCFlag>(Arrays.asList(
				new QCFlag("WOCE_CO2_water", '4', Severity.ERROR, 4, 31),
				new QCFlag("WOCE_CO2_water", '3', Severity.WARNING, 5, 35),
				new QCFlag("WOCE_CO2_atm", '3', Severity.WARNING, 12, 35)
		));

		DashboardDataset firstCruise = new DashboardDataset();
		assertFalse( firstCruise.equals(null) );
		assertFalse( firstCruise.equals(myDataStatus) );
		DashboardDataset secondCruise = new DashboardDataset();
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setSelected(true);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setSelected(true);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setVersion(myVersion);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setVersion(myVersion);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setOwner(myOwner);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setOwner(myOwner);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setDatasetId(myExpocode);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setDatasetId(myExpocode);
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

		firstCruise.setSubmitStatus(myQCStatus);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setSubmitStatus(myQCStatus);
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

		firstCruise.setCheckerFlags(checkerFlags);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setCheckerFlags(checkerFlags);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setUserFlags(userFlags);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setUserFlags(userFlags);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setUploadTimestamp(myUploadTimestamp);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setUploadTimestamp(myUploadTimestamp);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setDoi(myOrigDOI);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setDoi(myOrigDOI);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);

		firstCruise.setArchiveDate(myArchiveDate);
		assertTrue( firstCruise.hashCode() != secondCruise.hashCode() );
		assertFalse( firstCruise.equals(secondCruise) );
		secondCruise.setArchiveDate(myArchiveDate);
		assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
		assertEquals(firstCruise, secondCruise);
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#isSelected()}
	 * and {@link gov.noaa.pmel.dashboard.shared.DashboardDataset#setSelected(boolean)}.
	 */
	@Test
	public void testSetIsEditable() {
		DashboardDataset cruise = new DashboardDataset();
		assertTrue( cruise.isEditable() );


		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_WITH_NEXT_RELEASE);

		cruise.setSubmitStatus(DashboardUtils.STATUS_NOT_SUBMITTED);
		assertNotNull( cruise.isEditable() );
		assertTrue( cruise.isEditable() );

		cruise.setSubmitStatus(DashboardUtils.STATUS_EXCLUDED);
		assertNotNull( cruise.isEditable() );
		assertTrue( cruise.isEditable() );

		cruise.setSubmitStatus(DashboardUtils.STATUS_SUSPENDED);
		assertNotNull( cruise.isEditable() );
		assertTrue( cruise.isEditable() );

		cruise.setSubmitStatus(DashboardUtils.STATUS_SUBMITTED);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setSubmitStatus(DashboardUtils.STATUS_ACCEPTED);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setSubmitStatus(DashboardUtils.STATUS_CONFLICT);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setSubmitStatus(DashboardUtils.STATUS_RENAMED);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );


		cruise.setSubmitStatus(DashboardUtils.STATUS_SUBMITTED);

		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_WITH_NEXT_RELEASE);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_SENT_FOR_ARCHIVAL);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_OWNER_TO_ARCHIVE);
		assertNotNull( cruise.isEditable() );
		assertFalse( cruise.isEditable() );

		cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_ARCHIVED);
		assertNull( cruise.isEditable() );
	}

}
