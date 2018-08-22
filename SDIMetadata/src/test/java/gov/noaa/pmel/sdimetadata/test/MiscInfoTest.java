package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.Coverage;
import gov.noaa.pmel.sdimetadata.MiscInfo;
import gov.noaa.pmel.sdimetadata.Datestamp;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MiscInfoTest {

    private static final String EMPTY_STR = "";
    private static final Datestamp EMPTY_DATESTAMP = new Datestamp();
    private static final String DATASET_ID = "33RO20150114";
    private static final String DATASET_NAME = "RB1501A";
    private static final String FUNDING_INFO = "NOAA Climate Observation Office/Climate Observations Division";
    private static final String DATASET_DOI = "10.3334/CDIAC/OTG.VOS_RB_2015";
    private static final String WEBSITE = "http://cdiac.ornl.gov/ftp/oceans/VOS_Ronald_Brown/RB2015/";
    private static final String CITATION = "R. Wanninkhof, R. D. Castle, and J. Shannahoff. 2013. " +
            "Underway pCO2 measurements aboard the R/V Ronald H. Brown during the 2014 cruises. " +
            "Carbon Dioxide Information Analysis Center, Oak Ridge National Laboratory, US Department of Energy, Oak Ridge, Tennessee.";
    private static final ArrayList<String> ADDN_INFO_LIST = new ArrayList<String>(Arrays.asList(
            "It was determined that there was a 2.68 minute offset between the SST data record from the SBE-21 in the bow and the Hart 1521 temperature sensor in the equilibrator.  The SST data were interpolated using this offset to determine the SST at the time of the equilibrator measurement.",
            "A total of 6011 measurements were taken with 5661 flagged as good, 342 flagged as questionable, and 8 flagged as bad.  All measurements flagged as 4 (bad) have been removed from the final data file.",
            "There was a 17-1/2 hour dropout of EqT readings at the start of the cruise.  New values were determined using a relation between equilibrator temperature and SST.  The equation used was EqT = 0.9734*SST + 0.7735, n = 124, r^2 = 0.9630.  All of these values have been flagged 3.",
            "On 1/22 at 1730, an emergency shutdown of the system occurred due to water getting into the atm condenser.  The survey tech cleared out the water and restarted the system on 1/26 at 0519.  No data was acquired during the shutdown period."
    ));
    private static final Datestamp START_DATESTAMP = new Datestamp(2015, 1, 13);
    private static final Datestamp END_DATESTAMP = new Datestamp(2015, 1, 30);
    private static final ArrayList<Datestamp> HISTORY_LIST = new ArrayList<Datestamp>(Arrays.asList(
            new Datestamp(2016, 1, 20),
            new Datestamp(2017, 2, 24)
    ));
    /*
    private static final Coverage COVERAGE = new Coverage(146.23, -120.45, 15.36, 45.03,
            1421150400.000, 1422532800.000, "Pacific Ocean", "sea surface");
    private static final Coverage BAD_STARTTIME_COVERAGE = new Coverage(146.23, -120.45, 15.36, 45.03,
            1421107000.000, 1422532800.000, "Pacific Ocean", "sea surface");
    private static final Coverage BAD_ENDTIME_COVERAGE = new Coverage(146.23, -120.45, 15.36, 45.03,
            1421150400.000, 1422662600.000, "Pacific Ocean", "sea surface");
     */

    @Test
    public void testGetSetDatasetId() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setDatasetId(DATASET_ID);
        assertEquals(DATASET_ID, miscInfo.getDatasetId());
        miscInfo.setDatasetId(null);
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
    }

    @Test
    public void testGetSetDatasetName() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        miscInfo.setDatasetName(DATASET_NAME);
        assertEquals(DATASET_NAME, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setDatasetName(null);
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
    }

    @Test
    public void testGetSetFunding() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getFunding());
        miscInfo.setFunding(FUNDING_INFO);
        assertEquals(FUNDING_INFO, miscInfo.getFunding());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setFunding(null);
        assertEquals(EMPTY_STR, miscInfo.getFunding());
    }

    @Test
    public void testGetSetDatasetDoi() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        miscInfo.setDatasetDoi(DATASET_DOI);
        assertEquals(DATASET_DOI, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getFunding());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setDatasetDoi(null);
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
    }

    @Test
    public void testGetSetWebsite() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        miscInfo.setWebsite(WEBSITE);
        assertEquals(WEBSITE, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getFunding());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setWebsite(null);
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
    }

    @Test
    public void testGetSetCitation() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        miscInfo.setCitation(CITATION);
        assertEquals(CITATION, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getFunding());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setCitation(null);
        assertEquals(EMPTY_STR, miscInfo.getCitation());
    }

    @Test
    public void testGetSetAddnInfo() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(0, miscInfo.getAddnInfo().size());
        miscInfo.setAddnInfo(ADDN_INFO_LIST);
        assertEquals(ADDN_INFO_LIST, miscInfo.getAddnInfo());
        assertNotSame(ADDN_INFO_LIST, miscInfo.getAddnInfo());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getFunding());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setAddnInfo(null);
        assertEquals(0, miscInfo.getAddnInfo().size());
        miscInfo.setAddnInfo(new HashSet<>());
        assertEquals(0, miscInfo.getAddnInfo().size());
        try {
            miscInfo.setAddnInfo(Arrays.asList("Some information", "\n", "More information"));
            fail("calling setAddnInfo with a list containing an blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            miscInfo.setAddnInfo(Arrays.asList("Some information", null, "More information"));
            fail("calling setAddnInfo with a list containing a null succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetStartDatestamp() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_DATESTAMP, miscInfo.getStartDatestamp());
        miscInfo.setStartDatestamp(START_DATESTAMP);
        assertEquals(START_DATESTAMP, miscInfo.getStartDatestamp());
        assertNotSame(START_DATESTAMP, miscInfo.getStartDatestamp());
        assertEquals(0, miscInfo.getAddnInfo().size());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getFunding());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setStartDatestamp(null);
        assertEquals(EMPTY_DATESTAMP, miscInfo.getStartDatestamp());
        miscInfo.setStartDatestamp(START_DATESTAMP);
        assertEquals(START_DATESTAMP, miscInfo.getStartDatestamp());
        miscInfo.setStartDatestamp(EMPTY_DATESTAMP);
        assertEquals(EMPTY_DATESTAMP, miscInfo.getStartDatestamp());
        assertNotSame(EMPTY_DATESTAMP, miscInfo.getStartDatestamp());
    }

    @Test
    public void testGetSetEndDatestamp() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_DATESTAMP, miscInfo.getEndDatestamp());
        miscInfo.setEndDatestamp(END_DATESTAMP);
        assertEquals(END_DATESTAMP, miscInfo.getEndDatestamp());
        assertNotSame(END_DATESTAMP, miscInfo.getEndDatestamp());
        assertEquals(EMPTY_DATESTAMP, miscInfo.getStartDatestamp());
        assertEquals(0, miscInfo.getAddnInfo().size());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getFunding());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setEndDatestamp(null);
        assertEquals(EMPTY_DATESTAMP, miscInfo.getEndDatestamp());
        miscInfo.setEndDatestamp(END_DATESTAMP);
        assertEquals(END_DATESTAMP, miscInfo.getEndDatestamp());
        miscInfo.setEndDatestamp(EMPTY_DATESTAMP);
        assertEquals(EMPTY_DATESTAMP, miscInfo.getEndDatestamp());
        assertNotSame(EMPTY_DATESTAMP, miscInfo.getEndDatestamp());
    }

    @Test
    public void testGetSetHistory() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(0, miscInfo.getHistory().size());
        miscInfo.setHistory(HISTORY_LIST);
        ArrayList<Datestamp> history = miscInfo.getHistory();
        assertEquals(HISTORY_LIST, history);
        assertNotSame(HISTORY_LIST, history);
        for (int k = 0; k < HISTORY_LIST.size(); k++) {
            assertNotSame(HISTORY_LIST.get(k), history.get(k));
        }
        assertEquals(EMPTY_DATESTAMP, miscInfo.getEndDatestamp());
        assertEquals(EMPTY_DATESTAMP, miscInfo.getStartDatestamp());
        assertEquals(0, miscInfo.getAddnInfo().size());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getFunding());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setHistory(null);
        assertEquals(0, miscInfo.getHistory().size());
        miscInfo.setHistory(new HashSet<Datestamp>());
        assertEquals(0, miscInfo.getHistory().size());
        try {
            miscInfo.setHistory(Arrays.asList(HISTORY_LIST.get(0), EMPTY_DATESTAMP));
            fail("calling setHistory with an invalid datestamp succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            miscInfo.setHistory(Arrays.asList(HISTORY_LIST.get(0), null));
            fail("calling setHistory with an null datestamp succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testIsValid() {
        MiscInfo miscInfo = new MiscInfo();
        assertFalse(miscInfo.isValid());

        miscInfo.setDatasetId(DATASET_ID);
        miscInfo.setStartDatestamp(START_DATESTAMP);
        miscInfo.setEndDatestamp(END_DATESTAMP);
        assertTrue(miscInfo.isValid());
    }

    @Test
    public void testClone() {
        MiscInfo miscInfo = new MiscInfo();
        MiscInfo dup = miscInfo.clone();
        assertEquals(miscInfo, dup);
        assertNotSame(miscInfo, dup);

        miscInfo.setDatasetId(DATASET_ID);
        miscInfo.setDatasetName(DATASET_NAME);
        miscInfo.setFunding(FUNDING_INFO);
        miscInfo.setDatasetDoi(DATASET_DOI);
        miscInfo.setWebsite(WEBSITE);
        miscInfo.setCitation(CITATION);
        miscInfo.setAddnInfo(ADDN_INFO_LIST);
        miscInfo.setStartDatestamp(START_DATESTAMP);
        miscInfo.setEndDatestamp(END_DATESTAMP);
        miscInfo.setHistory(HISTORY_LIST);
        assertNotEquals(miscInfo, dup);

        dup = miscInfo.clone();
        assertEquals(miscInfo, dup);
        assertNotSame(miscInfo, dup);

        assertEquals(miscInfo.getAddnInfo(), dup.getAddnInfo());
        assertNotSame(miscInfo.getAddnInfo(), dup.getAddnInfo());

        ArrayList<Datestamp> history = miscInfo.getHistory();
        ArrayList<Datestamp> dupHistory = dup.getHistory();
        assertEquals(history, dupHistory);
        assertNotSame(history, dupHistory);
        for (int k = 0; k < history.size(); k++) {
            assertEquals(history.get(k), dupHistory.get(k));
            assertNotSame(history.get(k), dupHistory.get(k));
        }
    }

    @Test
    public void testGetHashCodeEquals() {
        MiscInfo first = new MiscInfo();
        assertFalse(first.equals(null));
        assertFalse(first.equals(DATASET_ID));

        MiscInfo second = new MiscInfo();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setDatasetId(DATASET_ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setDatasetId(DATASET_ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setDatasetName(DATASET_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setDatasetName(DATASET_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFunding(FUNDING_INFO);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFunding(FUNDING_INFO);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setDatasetDoi(DATASET_DOI);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setDatasetDoi(DATASET_DOI);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setWebsite(WEBSITE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setWebsite(WEBSITE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setCitation(CITATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCitation(CITATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAddnInfo(ADDN_INFO_LIST);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAddnInfo(ADDN_INFO_LIST);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setStartDatestamp(START_DATESTAMP);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setStartDatestamp(START_DATESTAMP);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setEndDatestamp(END_DATESTAMP);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setEndDatestamp(END_DATESTAMP);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setHistory(HISTORY_LIST);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setHistory(HISTORY_LIST);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}

