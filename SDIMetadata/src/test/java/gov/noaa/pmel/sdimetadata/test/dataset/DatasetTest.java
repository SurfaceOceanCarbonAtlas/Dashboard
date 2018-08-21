package gov.noaa.pmel.sdimetadata.test.dataset;

import gov.noaa.pmel.sdimetadata.dataset.Dataset;
import gov.noaa.pmel.sdimetadata.dataset.Datestamp;
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

public class DatasetTest {

    private static final String EMPTY_STR = "";
    private static final Datestamp EMPTY_DATESTAMP = new Datestamp();
    private static final String DATASET_ID = "33RO20150114";
    private static final String DATASET_NAME = "RB1501A";
    private static final String FUNDING_INFO = "NOAA Climate Observation Office/Climate Observations Division";
    private static final String DATASET_DOI = "10.3334/CDIAC/OTG.VOS_RB_2015";
    private static final String WEBSITE = "http://cdiac.ornl.gov/ftp/oceans/VOS_Ronald_Brown/RB2015/";
    private static final String CITATION = "R. Wanninkhof, R. D. Castle, and J. Shannahoff. 2013. " +
            "Underway pCO2 measurements aboard the R/V Ronald H. Brown during the 2014 cruises. " +
            "Carbon Dioxide Information Analysis Center, Oak Ridge National Laboratory, US Department of Energy, Oak Ridge, Tennessee. ";
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

    @Test
    public void testGetSetDatasetId() {
        Dataset dataset = new Dataset();
        assertEquals(EMPTY_STR, dataset.getDatasetId());
        dataset.setDatasetId(DATASET_ID);
        assertEquals(DATASET_ID, dataset.getDatasetId());
        dataset.setDatasetId(null);
        assertEquals(EMPTY_STR, dataset.getDatasetId());
    }

    @Test
    public void testGetSetDatasetName() {
        Dataset dataset = new Dataset();
        assertEquals(EMPTY_STR, dataset.getDatasetName());
        dataset.setDatasetName(DATASET_NAME);
        assertEquals(DATASET_NAME, dataset.getDatasetName());
        assertEquals(EMPTY_STR, dataset.getDatasetId());
        dataset.setDatasetName(null);
        assertEquals(EMPTY_STR, dataset.getDatasetName());
    }

    @Test
    public void testGetSetFunding() {
        Dataset dataset = new Dataset();
        assertEquals(EMPTY_STR, dataset.getFunding());
        dataset.setFunding(FUNDING_INFO);
        assertEquals(FUNDING_INFO, dataset.getFunding());
        assertEquals(EMPTY_STR, dataset.getDatasetName());
        assertEquals(EMPTY_STR, dataset.getDatasetId());
        dataset.setFunding(null);
        assertEquals(EMPTY_STR, dataset.getFunding());
    }

    @Test
    public void testGetSetDatasetDoi() {
        Dataset dataset = new Dataset();
        assertEquals(EMPTY_STR, dataset.getDatasetDoi());
        dataset.setDatasetDoi(DATASET_DOI);
        assertEquals(DATASET_DOI, dataset.getDatasetDoi());
        assertEquals(EMPTY_STR, dataset.getFunding());
        assertEquals(EMPTY_STR, dataset.getDatasetName());
        assertEquals(EMPTY_STR, dataset.getDatasetId());
        dataset.setDatasetDoi(null);
        assertEquals(EMPTY_STR, dataset.getDatasetDoi());
    }

    @Test
    public void testGetSetWebsite() {
        Dataset dataset = new Dataset();
        assertEquals(EMPTY_STR, dataset.getWebsite());
        dataset.setWebsite(WEBSITE);
        assertEquals(WEBSITE, dataset.getWebsite());
        assertEquals(EMPTY_STR, dataset.getDatasetDoi());
        assertEquals(EMPTY_STR, dataset.getFunding());
        assertEquals(EMPTY_STR, dataset.getDatasetName());
        assertEquals(EMPTY_STR, dataset.getDatasetId());
        dataset.setWebsite(null);
        assertEquals(EMPTY_STR, dataset.getWebsite());
    }

    @Test
    public void testGetSetCitation() {
        Dataset dataset = new Dataset();
        assertEquals(EMPTY_STR, dataset.getCitation());
        dataset.setCitation(CITATION);
        assertEquals(CITATION, dataset.getCitation());
        assertEquals(EMPTY_STR, dataset.getWebsite());
        assertEquals(EMPTY_STR, dataset.getDatasetDoi());
        assertEquals(EMPTY_STR, dataset.getFunding());
        assertEquals(EMPTY_STR, dataset.getDatasetName());
        assertEquals(EMPTY_STR, dataset.getDatasetId());
        dataset.setCitation(null);
        assertEquals(EMPTY_STR, dataset.getCitation());
    }

    @Test
    public void testGetSetAddnInfo() {
        Dataset dataset = new Dataset();
        assertEquals(0, dataset.getAddnInfo().size());
        dataset.setAddnInfo(ADDN_INFO_LIST);
        assertEquals(ADDN_INFO_LIST, dataset.getAddnInfo());
        assertNotSame(ADDN_INFO_LIST, dataset.getAddnInfo());
        assertEquals(EMPTY_STR, dataset.getCitation());
        assertEquals(EMPTY_STR, dataset.getWebsite());
        assertEquals(EMPTY_STR, dataset.getDatasetDoi());
        assertEquals(EMPTY_STR, dataset.getFunding());
        assertEquals(EMPTY_STR, dataset.getDatasetName());
        assertEquals(EMPTY_STR, dataset.getDatasetId());
        dataset.setAddnInfo(null);
        assertEquals(0, dataset.getAddnInfo().size());
        dataset.setAddnInfo(new HashSet<>());
        assertEquals(0, dataset.getAddnInfo().size());
        try {
            dataset.setAddnInfo(Arrays.asList("Some information", "\n", "More information"));
            fail("calling setAddnInfo with a list containing an blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            dataset.setAddnInfo(Arrays.asList("Some information", null, "More information"));
            fail("calling setAddnInfo with a list containing a null succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetStartDatestamp() {
        Dataset dataset = new Dataset();
        assertEquals(EMPTY_DATESTAMP, dataset.getStartDatestamp());
        dataset.setStartDatestamp(START_DATESTAMP);
        assertEquals(START_DATESTAMP, dataset.getStartDatestamp());
        assertNotSame(START_DATESTAMP, dataset.getStartDatestamp());
        assertEquals(0, dataset.getAddnInfo().size());
        assertEquals(EMPTY_STR, dataset.getCitation());
        assertEquals(EMPTY_STR, dataset.getWebsite());
        assertEquals(EMPTY_STR, dataset.getDatasetDoi());
        assertEquals(EMPTY_STR, dataset.getFunding());
        assertEquals(EMPTY_STR, dataset.getDatasetName());
        assertEquals(EMPTY_STR, dataset.getDatasetId());
        dataset.setStartDatestamp(null);
        assertEquals(EMPTY_DATESTAMP, dataset.getStartDatestamp());
        dataset.setStartDatestamp(START_DATESTAMP);
        assertEquals(START_DATESTAMP, dataset.getStartDatestamp());
        dataset.setStartDatestamp(EMPTY_DATESTAMP);
        assertEquals(EMPTY_DATESTAMP, dataset.getStartDatestamp());
        assertNotSame(EMPTY_DATESTAMP, dataset.getStartDatestamp());
    }

    @Test
    public void testGetSetEndDatestamp() {
        Dataset dataset = new Dataset();
        assertEquals(EMPTY_DATESTAMP, dataset.getEndDatestamp());
        dataset.setEndDatestamp(END_DATESTAMP);
        assertEquals(END_DATESTAMP, dataset.getEndDatestamp());
        assertNotSame(END_DATESTAMP, dataset.getEndDatestamp());
        assertEquals(EMPTY_DATESTAMP, dataset.getStartDatestamp());
        assertEquals(0, dataset.getAddnInfo().size());
        assertEquals(EMPTY_STR, dataset.getCitation());
        assertEquals(EMPTY_STR, dataset.getWebsite());
        assertEquals(EMPTY_STR, dataset.getDatasetDoi());
        assertEquals(EMPTY_STR, dataset.getFunding());
        assertEquals(EMPTY_STR, dataset.getDatasetName());
        assertEquals(EMPTY_STR, dataset.getDatasetId());
        dataset.setEndDatestamp(null);
        assertEquals(EMPTY_DATESTAMP, dataset.getEndDatestamp());
        dataset.setEndDatestamp(END_DATESTAMP);
        assertEquals(END_DATESTAMP, dataset.getEndDatestamp());
        dataset.setEndDatestamp(EMPTY_DATESTAMP);
        assertEquals(EMPTY_DATESTAMP, dataset.getEndDatestamp());
        assertNotSame(EMPTY_DATESTAMP, dataset.getEndDatestamp());
    }

    @Test
    public void testGetSetHistory() {
        Dataset dataset = new Dataset();
        assertEquals(0, dataset.getHistory().size());
        dataset.setHistory(HISTORY_LIST);
        ArrayList<Datestamp> history = dataset.getHistory();
        assertEquals(HISTORY_LIST, history);
        assertNotSame(HISTORY_LIST, history);
        for (int k = 0; k < HISTORY_LIST.size(); k++) {
            assertNotSame(HISTORY_LIST.get(k), history.get(k));
        }
        assertEquals(EMPTY_DATESTAMP, dataset.getEndDatestamp());
        assertEquals(EMPTY_DATESTAMP, dataset.getStartDatestamp());
        assertEquals(0, dataset.getAddnInfo().size());
        assertEquals(EMPTY_STR, dataset.getCitation());
        assertEquals(EMPTY_STR, dataset.getWebsite());
        assertEquals(EMPTY_STR, dataset.getDatasetDoi());
        assertEquals(EMPTY_STR, dataset.getFunding());
        assertEquals(EMPTY_STR, dataset.getDatasetName());
        assertEquals(EMPTY_STR, dataset.getDatasetId());
        dataset.setHistory(null);
        assertEquals(0, dataset.getHistory().size());
        dataset.setHistory(new HashSet<Datestamp>());
        assertEquals(0, dataset.getHistory().size());
        try {
            dataset.setHistory(Arrays.asList(HISTORY_LIST.get(0), EMPTY_DATESTAMP));
            fail("calling setHistory with an invalid datestamp succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            dataset.setHistory(Arrays.asList(HISTORY_LIST.get(0), null));
            fail("calling setHistory with an null datestamp succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetHashCodeEquals() {
        Dataset first = new Dataset();
        assertFalse(first.equals(null));
        assertFalse(first.equals(DATASET_ID));

        Dataset second = new Dataset();
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setDatasetId(DATASET_ID);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setDatasetId(DATASET_ID);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setDatasetName(DATASET_NAME);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setDatasetName(DATASET_NAME);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setFunding(FUNDING_INFO);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setFunding(FUNDING_INFO);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setDatasetDoi(DATASET_DOI);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setDatasetDoi(DATASET_DOI);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setWebsite(WEBSITE);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setWebsite(WEBSITE);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setCitation(CITATION);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setCitation(CITATION);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setAddnInfo(ADDN_INFO_LIST);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setAddnInfo(ADDN_INFO_LIST);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setStartDatestamp(START_DATESTAMP);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setStartDatestamp(START_DATESTAMP);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setEndDatestamp(END_DATESTAMP);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setEndDatestamp(END_DATESTAMP);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setHistory(HISTORY_LIST);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setHistory(HISTORY_LIST);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));
    }

    @Test
    public void testClone() {
        Dataset dataset = new Dataset();
        Dataset dup = dataset.clone();
        assertEquals(dataset, dup);
        assertNotSame(dataset, dup);

        dataset.setDatasetId(DATASET_ID);
        dataset.setDatasetName(DATASET_NAME);
        dataset.setFunding(FUNDING_INFO);
        dataset.setDatasetDoi(DATASET_DOI);
        dataset.setWebsite(WEBSITE);
        dataset.setCitation(CITATION);
        dataset.setAddnInfo(ADDN_INFO_LIST);
        dataset.setStartDatestamp(START_DATESTAMP);
        dataset.setEndDatestamp(END_DATESTAMP);
        dataset.setHistory(HISTORY_LIST);
        assertNotEquals(dataset, dup);

        dup = dataset.clone();
        assertEquals(dataset, dup);
        assertNotSame(dataset, dup);

        assertEquals(dataset.getAddnInfo(), dup.getAddnInfo());
        assertNotSame(dataset.getAddnInfo(), dup.getAddnInfo());

        ArrayList<Datestamp> history = dataset.getHistory();
        ArrayList<Datestamp> dupHistory = dup.getHistory();
        assertEquals(history, dupHistory);
        assertNotSame(history, dupHistory);
        for (int k = 0; k < history.size(); k++) {
            assertEquals(history.get(k), dupHistory.get(k));
            assertNotSame(history.get(k), dupHistory.get(k));
        }
    }

}

