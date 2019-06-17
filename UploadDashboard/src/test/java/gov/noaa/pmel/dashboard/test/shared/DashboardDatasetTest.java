package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DataQCFlag;
import gov.noaa.pmel.dashboard.shared.DataQCFlag.Severity;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for methods of {@link DashboardDataset}.
 *
 * @author Karl Smith
 */
public class DashboardDatasetTest {

    /**
     * Test method for {@link DashboardDataset#isSelected()} and {@link DashboardDataset#setSelected(boolean)}.
     */
    @Test
    public void testSetIsSelected() {
        DashboardDataset cruise = new DashboardDataset();
        assertFalse(cruise.isSelected());
        cruise.setSelected(true);
        assertTrue(cruise.isSelected());
    }

    /**
     * Test method for {@link DashboardDataset#getVersion()} and {@link DashboardDataset#setVersion(String)}.
     */
    @Test
    public void testSetGetVersion() {
        String myVersion = "2.5";
        DashboardDataset cruise = new DashboardDataset();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        cruise.setVersion(myVersion);
        assertEquals(myVersion, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setVersion(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
    }

    /**
     * Test method for {@link DashboardDataset#getOwner()} and {@link DashboardDataset#setOwner(String)}.
     */
    @Test
    public void testSetGetOwner() {
        String myOwner = "SocatUser";
        DashboardDataset cruise = new DashboardDataset();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        cruise.setOwner(myOwner);
        assertEquals(myOwner, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setOwner(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
    }

    /**
     * Test method for {@link DashboardDataset#getDatasetId()} and {@link DashboardDataset#setDatasetId(String)}.
     */
    @Test
    public void testSetGetDatasetId() {
        String myExpocode = "ABCD20050728";
        DashboardDataset cruise = new DashboardDataset();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        cruise.setDatasetId(myExpocode);
        assertEquals(myExpocode, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setDatasetId(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
    }

    /**
     * Test method for {@link DashboardDataset#getDataCheckStatus()}
     * and {@link DashboardDataset#setDataCheckStatus(String)}.
     */
    @Test
    public void testSetGetDataCheckStatus() {
        String myDataStatus = "Acceptable";
        DashboardDataset cruise = new DashboardDataset();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        cruise.setDataCheckStatus(myDataStatus);
        assertEquals(myDataStatus, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setDataCheckStatus(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
    }

    /**
     * Test method for {@link DashboardDataset#getOmeTimestamp()} and {@link DashboardDataset#setOmeTimestamp(String)}.
     */
    @Test
    public void testSetGetOmeTimestamp() {
        String myOmeFilename = "2014-02-21 9:22";
        DashboardDataset cruise = new DashboardDataset();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        cruise.setOmeTimestamp(myOmeFilename);
        assertEquals(myOmeFilename, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setOmeTimestamp(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
    }

    /**
     * Test method for {@link DashboardDataset#getAddlDocs()} and {@link DashboardDataset#setAddlDocs(TreeSet)}.
     */
    @Test
    public void testSetGetAddlDocNames() {
        TreeSet<String> myMetaNames = new TreeSet<String>(Arrays.asList(
                "ABCD20050728.txt ; 2014-02-21 9:23",
                "ABCD20050728_2.doc ; 2014-02-21 9:24",
                "ABCD20050728_3.pdf ; 2014-02-21 9:25"));
        DashboardDataset cruise = new DashboardDataset();
        assertEquals(0, cruise.getAddlDocs().size());
        cruise.setAddlDocs(myMetaNames);
        assertEquals(myMetaNames, cruise.getAddlDocs());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setAddlDocs(null);
        assertEquals(0, cruise.getAddlDocs().size());
    }

    /**
     * Test method for {@link DashboardDataset#getSubmitStatus()} and {@link DashboardDataset#setSubmitStatus(DatasetQCStatus)}
     */
    @Test
    public void testSetGetSubmitStatus() {
        DatasetQCStatus emptyFlag = new DatasetQCStatus();
        DatasetQCStatus mySubmitStatus = new DatasetQCStatus(DatasetQCStatus.Status.UPDATED_AWAITING_QC);
        mySubmitStatus.setPiSuggested(DatasetQCStatus.Status.ACCEPTED_A);
        mySubmitStatus.setAutoSuggested(DatasetQCStatus.Status.ACCEPTED_B);
        DashboardDataset cruise = new DashboardDataset();
        assertEquals(emptyFlag, cruise.getSubmitStatus());
        cruise.setSubmitStatus(mySubmitStatus);
        assertNotSame(mySubmitStatus, cruise.getSubmitStatus());
        assertEquals(mySubmitStatus, cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setSubmitStatus(null);
        assertEquals(emptyFlag, cruise.getSubmitStatus());
    }

    /**
     * Test method for {@link DashboardDataset#getArchiveStatus()}
     * and {@link DashboardDataset#setArchiveStatus(String)}.
     */
    @Test
    public void testSetGetArchiveStatus() {
        String myArchiveStatus = "Next year";
        DashboardDataset cruise = new DashboardDataset();
        assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
        cruise.setArchiveStatus(myArchiveStatus);
        assertEquals(myArchiveStatus, cruise.getArchiveStatus());
        assertEquals(new DatasetQCStatus(), cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setArchiveStatus(null);
        assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
    }

    /**
     * Test method for {@link DashboardDataset#getUploadFilename()}
     * and {@link DashboardDataset#setUploadFilename(String)}.
     */
    @Test
    public void testSetGetUploadFilename() {
        String myFilename = "myUploadFilename.tsv";
        DashboardDataset cruise = new DashboardDataset();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
        cruise.setUploadFilename(myFilename);
        assertEquals(myFilename, cruise.getUploadFilename());
        assertEquals(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED, cruise.getArchiveStatus());
        assertEquals(new DatasetQCStatus(), cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setUploadFilename(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadFilename());
    }

    /**
     * Test method for {@link DashboardDataset#getNumDataRows()} and {@link DashboardDataset#setNumDataRows(int)}.
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
        assertEquals(new DatasetQCStatus(), cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
    }

    /**
     * Test method for {@link DashboardDataset#getDataColTypes()}
     * and {@link DashboardDataset#setDataColTypes(ArrayList)}.
     */
    @Test
    public void testSetGetDataColTypes() {
        ArrayList<DataColumnType> myDataTypes = new ArrayList<DataColumnType>(Arrays.asList(
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
        assertEquals(new DatasetQCStatus(), cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setDataColTypes(null);
        assertEquals(0, cruise.getDataColTypes().size());
    }

    /**
     * Test method for {@link DashboardDataset#getUserColNames()}
     * and {@link DashboardDataset#setUserColNames(ArrayList)}.
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
        assertEquals(new DatasetQCStatus(), cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setUserColNames(null);
        assertEquals(0, cruise.getUserColNames().size());
    }

    /**
     * Test method for {@link DashboardDataset#getNumErrorRows()} and {@link DashboardDataset#setNumErrorRows(int)}.
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
        assertEquals(new DatasetQCStatus(), cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
    }

    /**
     * Test method for {@link DashboardDataset#getNumWarnRows()} and {@link DashboardDataset#setNumWarnRows(int)}.
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
        assertEquals(new DatasetQCStatus(), cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
    }

    /**
     * Test method for {@link DashboardDataset#getCheckerFlags()}
     * and {@link DashboardDataset#setCheckerFlags(TreeSet)}.
     */
    @Test
    public void testSetGetCheckerFlags() {
        TreeSet<DataQCFlag> checkerFlags = new TreeSet<DataQCFlag>(Arrays.asList(
                new DataQCFlag("WOCE_CO2_water", "3", Severity.WARNING, 5, 2),
                new DataQCFlag("WOCE_CO2_water", "3", Severity.WARNING, 8, 12),
                new DataQCFlag("WOCE_CO2_water", "4", Severity.ERROR, 3, 22)
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
        assertEquals(new DatasetQCStatus(), cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setCheckerFlags(null);
        assertEquals(0, cruise.getCheckerFlags().size());
    }

    /**
     * Test method for {@link DashboardDataset#getUserFlags()} and {@link DashboardDataset#setUserFlags(TreeSet)}.
     */
    @Test
    public void testSetGetUserFlags() {
        TreeSet<DataQCFlag> userFlags = new TreeSet<DataQCFlag>(Arrays.asList(
                new DataQCFlag("WOCE_CO2_water", "4", Severity.ERROR, 4, 31),
                new DataQCFlag("WOCE_CO2_water", "3", Severity.WARNING, 5, 35),
                new DataQCFlag("WOCE_CO2_atm", "3", Severity.WARNING, 12, 35)
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
        assertEquals(new DatasetQCStatus(), cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setUserFlags(null);
        assertEquals(0, cruise.getUserFlags().size());
    }

    /**
     * Test method for {@link DashboardDataset#getUploadTimestamp()}
     * and {@link DashboardDataset#setUploadTimestamp(String)}.
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
        assertEquals(new DatasetQCStatus(), cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setUploadTimestamp(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getUploadTimestamp());
    }

    /**
     * Test method for {@link DashboardDataset#getArchiveTimestamps()} and {@link DashboardDataset#setArchiveTimestamps(ArrayList)}
     */
    @Test
    public void testSetGetArchiveDate() {
        ArrayList<String> myArchiveTimestamps = new ArrayList<String>(Arrays.asList(
                "15-JAN-2016 13:30-5:00",
                "25-JAN-2017 11:30-5:00"
        ));
        DashboardDataset cruise = new DashboardDataset();
        assertEquals(0, cruise.getArchiveTimestamps().size());
        cruise.setArchiveTimestamps(myArchiveTimestamps);
        assertEquals(myArchiveTimestamps, cruise.getArchiveTimestamps());
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
        assertEquals(new DatasetQCStatus(), cruise.getSubmitStatus());
        assertEquals(0, cruise.getAddlDocs().size());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOmeTimestamp());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDataCheckStatus());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getDatasetId());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getOwner());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, cruise.getVersion());
        assertFalse(cruise.isSelected());
        cruise.setArchiveTimestamps(null);
        assertEquals(0, cruise.getArchiveTimestamps().size());
    }

    /**
     * Test method for {@link DashboardDataset#hashCode()} and {@link DashboardDataset#equals(Object)}.
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
        DatasetQCStatus mySubmitStatus = new DatasetQCStatus(DatasetQCStatus.Status.UPDATED_AWAITING_QC);
        mySubmitStatus.setPiSuggested(DatasetQCStatus.Status.ACCEPTED_A);
        mySubmitStatus.setAutoSuggested(DatasetQCStatus.Status.ACCEPTED_B);
        String myArchiveStatus = "Next SOCAT release";
        ArrayList<String> myArchiveTimestamps = new ArrayList<String>(Arrays.asList(
                "15-JAN-2016 13:30-5:00",
                "25-JAN-2017 11:30-5:00"
        ));
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
        TreeSet<DataQCFlag> checkerFlags = new TreeSet<DataQCFlag>(Arrays.asList(
                new DataQCFlag("WOCE_CO2_water", "3", Severity.WARNING, 5, 2),
                new DataQCFlag("WOCE_CO2_water", "4", Severity.ERROR, 8, 12),
                new DataQCFlag("WOCE_CO2_water", "3", Severity.WARNING, 3, 22)
        ));
        TreeSet<DataQCFlag> userFlags = new TreeSet<DataQCFlag>(Arrays.asList(
                new DataQCFlag("WOCE_CO2_water", "4", Severity.ERROR, 4, 31),
                new DataQCFlag("WOCE_CO2_water", "3", Severity.WARNING, 5, 35),
                new DataQCFlag("WOCE_CO2_atm", "3", Severity.WARNING, 12, 35)
        ));

        DashboardDataset firstCruise = new DashboardDataset();
        assertFalse(firstCruise.equals(null));
        assertFalse(firstCruise.equals(myDataStatus));
        DashboardDataset secondCruise = new DashboardDataset();
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setSelected(true);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setSelected(true);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setVersion(myVersion);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setVersion(myVersion);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setOwner(myOwner);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setOwner(myOwner);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setDatasetId(myExpocode);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setDatasetId(myExpocode);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setDataCheckStatus(myDataStatus);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setDataCheckStatus(myDataStatus);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setOmeTimestamp(myOmeTimestamp);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setOmeTimestamp(myOmeTimestamp);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setAddlDocs(myMetaNames);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setAddlDocs(myMetaNames);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setSubmitStatus(mySubmitStatus);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setSubmitStatus(mySubmitStatus);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setArchiveStatus(myArchiveStatus);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setArchiveStatus(myArchiveStatus);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setUploadFilename(myFilename);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setUploadFilename(myFilename);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setNumDataRows(myNumDataRows);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setNumDataRows(myNumDataRows);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setNumErrorRows(myNumErrorMsgs);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setNumErrorRows(myNumErrorMsgs);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setNumWarnRows(myNumWarnMsgs);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setNumWarnRows(myNumWarnMsgs);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setDataColTypes(myDataColTypes);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setDataColTypes(myDataColTypes);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setUserColNames(myUserColNames);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setUserColNames(myUserColNames);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setCheckerFlags(checkerFlags);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setCheckerFlags(checkerFlags);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setUserFlags(userFlags);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setUserFlags(userFlags);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setUploadTimestamp(myUploadTimestamp);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setUploadTimestamp(myUploadTimestamp);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));

        firstCruise.setArchiveTimestamps(myArchiveTimestamps);
        assertNotEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertFalse(firstCruise.equals(secondCruise));
        secondCruise.setArchiveTimestamps(myArchiveTimestamps);
        assertEquals(firstCruise.hashCode(), secondCruise.hashCode());
        assertTrue(firstCruise.equals(secondCruise));
    }

    /**
     * Test method for {@link DashboardDataset#isEditable()}
     */
    @Test
    public void testIsEditable() {
        DashboardDataset cruise = new DashboardDataset();
        assertTrue(cruise.isEditable());

        cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_WITH_NEXT_RELEASE);

        cruise.setSubmitStatus(null);
        assertNotNull(cruise.isEditable());
        assertTrue(cruise.isEditable());

        DatasetQCStatus flag = new DatasetQCStatus();
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertTrue(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.EXCLUDED);
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertTrue(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.SUSPENDED);
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertTrue(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.NEW_AWAITING_QC);
        assertTrue(cruise.isEditable());
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.UPDATED_AWAITING_QC);
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.ACCEPTED_A);
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.ACCEPTED_B);
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.ACCEPTED_C);
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.ACCEPTED_D);
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.ACCEPTED_E);
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.CONFLICTED);
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.RENAMED);
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        flag.setActual(DatasetQCStatus.Status.NEW_AWAITING_QC);
        flag.setPiSuggested(DatasetQCStatus.Status.SUSPENDED);
        flag.setAutoSuggested(DatasetQCStatus.Status.SUSPENDED);
        cruise.setSubmitStatus(flag);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_NOT_SUBMITTED);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_WITH_NEXT_RELEASE);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_SENT_TO_START + "PANGAEA");
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_OWNER_TO_ARCHIVE);
        assertNotNull(cruise.isEditable());
        assertFalse(cruise.isEditable());

        cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_ARCHIVED);
        assertNull(cruise.isEditable());
    }

}
