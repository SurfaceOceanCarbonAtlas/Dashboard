package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.MiscInfo;
import gov.noaa.pmel.sdimetadata.util.Datestamp;
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
    private static final ArrayList<String> EMPTY_NAMELIST = new ArrayList<String>();
    private static final HashSet<String> EMPTY_NAMESET = new HashSet<String>();
    private static final Datestamp EMPTY_DATESTAMP = new Datestamp();
    private static final ArrayList<Datestamp> EMPTY_STAMPLIST = new ArrayList<Datestamp>();

    private static final String DATASET_ID = "33RO20150114";
    private static final String DATASET_NAME = "RB1501A";
    private static final String SECTION_NAME = "Winter Leg";
    private static final String FUNDING_AGENCY = "NOAA Climate Observation Office/Climate Observations Division";
    private static final String FUNDING_TITLE = "NOAA Ocean Acidification Monitoring Network";
    private static final String FUNDING_ID = "NOAA.FY15.AOML.001";
    private static final String RESEARCH_PROJECT = "None";
    private static final String DATASET_DOI = "10.3334/CDIAC/OTG.VOS_RB_2015";
    private static final String ACCESS_ID = "23432093";
    private static final String WEBSITE = "http://cdiac.ornl.gov/ftp/oceans/VOS_Ronald_Brown/RB2015/";
    private static final String DOWNLOAD_URL = "ftp://cdiac.ornl.gov/ftp/oceans/VOS_Ronald_Brown/RB2015/RB1501A.tsv";
    private static final String CITATION = "R. Wanninkhof, R. D. Castle, and J. Shannahoff. 2015. " +
            "Underway pCO2 measurements aboard the R/V Ronald H. Brown during the 2015 cruises. " +
            "Carbon Dioxide Information Analysis Center, Oak Ridge National Laboratory, " +
            "US Department of Energy, Oak Ridge, Tennessee.";
    private static final String SYNOPSIS = "This archival package contains underway measurements of CO2, salinity, " +
            "sea surface temperature, and other parameters collected in 2015 on the NOAA Ship Ronald H. Brown " +
            "in the Pacific Ocean between Hawaii and San Francisco.";
    private static final String PURPOSE = "The major objectives of the project were to characterize and map the key " +
            "indicators of ocean acidification (OA) in the Pacific Ocean, and to continue a time series documenting " +
            "the distribution of surface and atmospheric fCO2, salinity, temperature, and other parameters.";
    private static final ArrayList<String> REFERENCES = new ArrayList<String>(Arrays.asList(
            "DOE (1994). Handbook of methods for the analysis of the various parameters of the carbon dioxide system " +
                    "in sea water; version 2. DOE.",
            "Feely, R. A., R. Wanninkhof, H. B. Milburn, C. E. Cosca, M. Stapp and P. P. Murphy (1998)  A new " +
                    "automated underway system for making high precision pCO2 measurements onboard research ships. " +
                    "Analytica Chim. Acta 377: 185-191.",
            "Ho, D. T., R. Wanninkhof, J. Masters, R. A. Feely and C. E. Cosca (1997). Measurement of underway " +
                    "fCO2 in the Eastern Equatorial Pacific on NOAA ships BALDRIGE and DISCOVERER, " +
                    "NOAA data report ERL AOML-30, 52 pp., NTIS Springfield.",
            "Pierrot, D., C. Neill, K. Sullivan, R. Castle, R. Wanninkhof, H. Luger, T. Johannessen, A. Olsen, " +
                    "R. A. Feely, and C. E. Cosca (2009), Recommendations for autonomous underway pCO2 measuring " +
                    "systems and data-reduction routines.  Deep Sea Research II, 56: 512-522.",
            "Wanninkhof, R. and K. Thoning (1993) Measurement of fugacity of CO2 in surface water using continuous " +
                    "and discrete sampling methods.  Mar. Chem. 44(2-4): 189-205.",
            "Weiss, R. F. (1970) The solubility of nitrogen, oxygen and argon in water and seawater. " +
                    "Deep-Sea Research 17: 721-735.",
            "Weiss, R. F. (1974) Carbon dioxide in water and seawater: the solubility of a non-ideal gas.  " +
                    "Mar. Chem. 2: 203-215.",
            "Weiss, R. F., R. A. Jahnke and C. D. Keeling (1982) Seasonal effects of temperature and salinity " +
                    "on the partial pressure of CO2 in seawater. Nature 300: 511-513."
    ));
    private static final ArrayList<String> PORTS_OF_CALL = new ArrayList<String>(Arrays.asList(
            "Honolulu, HI",
            "San Francisco, CA"
    ));
    private static final ArrayList<String> ADDN_INFO_LIST = new ArrayList<String>(Arrays.asList(
            "It was determined that there was a 2.68 minute offset between the SST data record from the SBE-21 " +
                    "in the bow and the Hart 1521 temperature sensor in the equilibrator.  The SST data were " +
                    "interpolated using this offset to determine the SST at the time of the equilibrator measurement.",
            "A total of 6011 measurements were taken with 5661 flagged as good, 342 flagged as questionable, and 8 " +
                    "flagged as bad.  All measurements flagged as 4 (bad) have been removed from the final data file.",
            "There was a 17-1/2 hour dropout of EqT readings at the start of the cruise.  New values were determined " +
                    "using a relation between equilibrator temperature and SST.  The equation used was " +
                    "EqT = 0.9734*SST + 0.7735, n = 124, r^2 = 0.9630.  All of these values have been flagged 3.",
            "On 1/22 at 1730, an emergency shutdown of the system occurred due to water getting into the atm " +
                    "condenser.  The survey tech cleared out the water and restarted the system on 1/26 at 0519.  " +
                    "No data was acquired during the shutdown period."
    ));
    private static final Datestamp START_DATESTAMP = new Datestamp("2015", "1", "13");
    private static final Datestamp END_DATESTAMP = new Datestamp("2015", "1", "30");
    private static final ArrayList<Datestamp> HISTORY_LIST = new ArrayList<Datestamp>(Arrays.asList(
            new Datestamp("2016", "1", "20"),
            new Datestamp("2017", "2", "24")
    ));

    @Test
    public void testGetSetDatasetId() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setDatasetId(DATASET_ID);
        assertEquals(DATASET_ID, miscInfo.getDatasetId());
        miscInfo.setDatasetId(null);
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setDatasetId("\t");
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
        miscInfo.setDatasetName("\t");
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
    }

    @Test
    public void testGetSetSectionName() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        miscInfo.setSectionName(SECTION_NAME);
        assertEquals(SECTION_NAME, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setSectionName(null);
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        miscInfo.setSectionName("\t");
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
    }

    @Test
    public void testGetSetFundingAgency() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        miscInfo.setFundingAgency(FUNDING_AGENCY);
        assertEquals(FUNDING_AGENCY, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setFundingAgency(null);
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        miscInfo.setFundingAgency("\t");
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
    }

    @Test
    public void testGetSetFundingTitle() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        miscInfo.setFundingTitle(FUNDING_TITLE);
        assertEquals(FUNDING_TITLE, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setFundingTitle(null);
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        miscInfo.setFundingTitle("\t");
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
    }

    @Test
    public void testGetSetFundingId() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        miscInfo.setFundingId(FUNDING_ID);
        assertEquals(FUNDING_ID, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setFundingId(null);
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        miscInfo.setFundingId("\t");
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
    }

    @Test
    public void testGetSetResearchProject() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        miscInfo.setResearchProject(RESEARCH_PROJECT);
        assertEquals(RESEARCH_PROJECT, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setResearchProject(null);
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        miscInfo.setResearchProject("\t");
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
    }

    @Test
    public void testGetSetDatasetDoi() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        miscInfo.setDatasetDoi(DATASET_DOI);
        assertEquals(DATASET_DOI, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setDatasetDoi(null);
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        miscInfo.setDatasetDoi("\t");
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
    }

    @Test
    public void testGetSetAccessId() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        miscInfo.setAccessId(ACCESS_ID);
        assertEquals(ACCESS_ID, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setAccessId(null);
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        miscInfo.setAccessId("\t");
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
    }

    @Test
    public void testGetSetWebsite() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        miscInfo.setWebsite(WEBSITE);
        assertEquals(WEBSITE, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setWebsite(null);
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        miscInfo.setWebsite("\t");
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
    }

    @Test
    public void testGetSetDownloadUrl() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
        miscInfo.setDownloadUrl(DOWNLOAD_URL);
        assertEquals(DOWNLOAD_URL, miscInfo.getDownloadUrl());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setDownloadUrl(null);
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
        miscInfo.setDownloadUrl("\t");
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
    }

    @Test
    public void testGetSetCitation() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        miscInfo.setCitation(CITATION);
        assertEquals(CITATION, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setCitation(null);
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        miscInfo.setCitation("\t");
        assertEquals(EMPTY_STR, miscInfo.getCitation());
    }

    @Test
    public void testGetSetSynopsis() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getSynopsis());
        miscInfo.setSynopsis(SYNOPSIS);
        assertEquals(SYNOPSIS, miscInfo.getSynopsis());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setSynopsis(null);
        assertEquals(EMPTY_STR, miscInfo.getSynopsis());
        miscInfo.setSynopsis("\t");
        assertEquals(EMPTY_STR, miscInfo.getSynopsis());
    }

    @Test
    public void testGetSetPurpose() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STR, miscInfo.getPurpose());
        miscInfo.setPurpose(PURPOSE);
        assertEquals(PURPOSE, miscInfo.getPurpose());
        assertEquals(EMPTY_STR, miscInfo.getSynopsis());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setPurpose(null);
        assertEquals(EMPTY_STR, miscInfo.getPurpose());
        miscInfo.setPurpose("\t");
        assertEquals(EMPTY_STR, miscInfo.getPurpose());
    }

    @Test
    public void testGetSetReferences() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_NAMELIST, miscInfo.getReferences());
        miscInfo.setReferences(REFERENCES);
        ArrayList<String> nameList = miscInfo.getReferences();
        assertEquals(REFERENCES, nameList);
        assertNotSame(REFERENCES, nameList);
        assertNotSame(nameList, miscInfo.getReferences());
        assertEquals(EMPTY_STR, miscInfo.getPurpose());
        assertEquals(EMPTY_STR, miscInfo.getSynopsis());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setReferences(null);
        assertEquals(EMPTY_NAMELIST, miscInfo.getReferences());
        miscInfo.setReferences(EMPTY_NAMESET);
        assertEquals(EMPTY_NAMELIST, miscInfo.getReferences());
        try {
            miscInfo.setReferences(Arrays.asList("Some information", "\n", "More information"));
            fail("calling setReferences with a list containing an blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            miscInfo.setReferences(Arrays.asList("Some information", null, "More information"));
            fail("calling setReferences with a list containing a null succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetPortsOfCall() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_NAMELIST, miscInfo.getPortsOfCall());
        miscInfo.setPortsOfCall(PORTS_OF_CALL);
        ArrayList<String> nameList = miscInfo.getPortsOfCall();
        assertEquals(PORTS_OF_CALL, nameList);
        assertNotSame(PORTS_OF_CALL, nameList);
        assertNotSame(nameList, miscInfo.getPortsOfCall());
        assertEquals(EMPTY_NAMELIST, miscInfo.getReferences());
        assertEquals(EMPTY_STR, miscInfo.getPurpose());
        assertEquals(EMPTY_STR, miscInfo.getSynopsis());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setPortsOfCall(null);
        assertEquals(EMPTY_NAMELIST, miscInfo.getPortsOfCall());
        miscInfo.setPortsOfCall(EMPTY_NAMESET);
        assertEquals(EMPTY_NAMELIST, miscInfo.getPortsOfCall());
        try {
            miscInfo.setPortsOfCall(Arrays.asList("Some information", "\n", "More information"));
            fail("calling setPortsOfCall with a list containing an blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            miscInfo.setPortsOfCall(Arrays.asList("Some information", null, "More information"));
            fail("calling setPortsOfCall with a list containing a null succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetAddnInfo() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_NAMELIST, miscInfo.getAddnInfo());
        miscInfo.setAddnInfo(ADDN_INFO_LIST);
        ArrayList<String> nameList = miscInfo.getAddnInfo();
        assertEquals(ADDN_INFO_LIST, nameList);
        assertNotSame(ADDN_INFO_LIST, nameList);
        assertNotSame(nameList, miscInfo.getAddnInfo());
        assertEquals(EMPTY_NAMELIST, miscInfo.getPortsOfCall());
        assertEquals(EMPTY_NAMELIST, miscInfo.getReferences());
        assertEquals(EMPTY_STR, miscInfo.getPurpose());
        assertEquals(EMPTY_STR, miscInfo.getSynopsis());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setAddnInfo(null);
        assertEquals(EMPTY_NAMELIST, miscInfo.getAddnInfo());
        miscInfo.setAddnInfo(EMPTY_NAMESET);
        assertEquals(EMPTY_NAMELIST, miscInfo.getAddnInfo());
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
        Datestamp stamp = miscInfo.getStartDatestamp();
        assertEquals(START_DATESTAMP, stamp);
        assertNotSame(START_DATESTAMP, stamp);
        assertNotSame(stamp, miscInfo.getStartDatestamp());
        assertEquals(EMPTY_NAMELIST, miscInfo.getAddnInfo());
        assertEquals(EMPTY_NAMELIST, miscInfo.getPortsOfCall());
        assertEquals(EMPTY_NAMELIST, miscInfo.getReferences());
        assertEquals(EMPTY_STR, miscInfo.getPurpose());
        assertEquals(EMPTY_STR, miscInfo.getSynopsis());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setStartDatestamp(null);
        assertEquals(EMPTY_DATESTAMP, miscInfo.getStartDatestamp());
        miscInfo.setStartDatestamp(EMPTY_DATESTAMP);
        assertEquals(EMPTY_DATESTAMP, miscInfo.getStartDatestamp());
    }

    @Test
    public void testGetSetEndDatestamp() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_DATESTAMP, miscInfo.getEndDatestamp());
        miscInfo.setEndDatestamp(END_DATESTAMP);
        Datestamp stamp = miscInfo.getEndDatestamp();
        assertEquals(END_DATESTAMP, stamp);
        assertNotSame(END_DATESTAMP, stamp);
        assertNotSame(stamp, miscInfo.getEndDatestamp());
        assertEquals(EMPTY_DATESTAMP, miscInfo.getStartDatestamp());
        assertEquals(EMPTY_NAMELIST, miscInfo.getAddnInfo());
        assertEquals(EMPTY_NAMELIST, miscInfo.getPortsOfCall());
        assertEquals(EMPTY_NAMELIST, miscInfo.getReferences());
        assertEquals(EMPTY_STR, miscInfo.getPurpose());
        assertEquals(EMPTY_STR, miscInfo.getSynopsis());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setEndDatestamp(null);
        assertEquals(EMPTY_DATESTAMP, miscInfo.getEndDatestamp());
        miscInfo.setEndDatestamp(EMPTY_DATESTAMP);
        assertEquals(EMPTY_DATESTAMP, miscInfo.getEndDatestamp());
    }

    @Test
    public void testGetSetHistory() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(EMPTY_STAMPLIST, miscInfo.getHistory());
        miscInfo.setHistory(HISTORY_LIST);
        ArrayList<Datestamp> history = miscInfo.getHistory();
        assertEquals(HISTORY_LIST, history);
        assertNotSame(HISTORY_LIST, history);
        for (int k = 0; k < HISTORY_LIST.size(); k++) {
            assertNotSame(HISTORY_LIST.get(k), history.get(k));
        }
        assertNotSame(history, miscInfo.getHistory());
        assertEquals(EMPTY_DATESTAMP, miscInfo.getEndDatestamp());
        assertEquals(EMPTY_DATESTAMP, miscInfo.getStartDatestamp());
        assertEquals(EMPTY_NAMELIST, miscInfo.getAddnInfo());
        assertEquals(EMPTY_NAMELIST, miscInfo.getPortsOfCall());
        assertEquals(EMPTY_NAMELIST, miscInfo.getReferences());
        assertEquals(EMPTY_STR, miscInfo.getPurpose());
        assertEquals(EMPTY_STR, miscInfo.getSynopsis());
        assertEquals(EMPTY_STR, miscInfo.getCitation());
        assertEquals(EMPTY_STR, miscInfo.getDownloadUrl());
        assertEquals(EMPTY_STR, miscInfo.getWebsite());
        assertEquals(EMPTY_STR, miscInfo.getAccessId());
        assertEquals(EMPTY_STR, miscInfo.getDatasetDoi());
        assertEquals(EMPTY_STR, miscInfo.getResearchProject());
        assertEquals(EMPTY_STR, miscInfo.getFundingId());
        assertEquals(EMPTY_STR, miscInfo.getFundingTitle());
        assertEquals(EMPTY_STR, miscInfo.getFundingAgency());
        assertEquals(EMPTY_STR, miscInfo.getSectionName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetName());
        assertEquals(EMPTY_STR, miscInfo.getDatasetId());
        miscInfo.setHistory(null);
        assertEquals(EMPTY_STAMPLIST, miscInfo.getHistory());
        miscInfo.setHistory(EMPTY_STAMPLIST);
        assertEquals(EMPTY_STAMPLIST, miscInfo.getHistory());
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
    public void testInvalidFieldNames() {
        MiscInfo miscInfo = new MiscInfo();
        assertEquals(new HashSet<String>(Arrays.asList(
                "datasetId", "startDatestamp", "endDatestamp")), miscInfo.invalidFieldNames());
        miscInfo.setDatasetId(DATASET_ID);
        assertEquals(new HashSet<String>(Arrays.asList(
                "startDatestamp", "endDatestamp")), miscInfo.invalidFieldNames());
        miscInfo.setStartDatestamp(START_DATESTAMP);
        assertEquals(new HashSet<String>(Arrays.asList("endDatestamp")), miscInfo.invalidFieldNames());
        miscInfo.setEndDatestamp(END_DATESTAMP);
        assertEquals(EMPTY_NAMESET, miscInfo.invalidFieldNames());
    }

    @Test
    public void testClone() {
        MiscInfo miscInfo = new MiscInfo();
        MiscInfo dup = miscInfo.clone();
        assertEquals(miscInfo, dup);
        assertNotSame(miscInfo, dup);

        miscInfo.setDatasetId(DATASET_ID);
        miscInfo.setDatasetName(DATASET_NAME);
        miscInfo.setSectionName(SECTION_NAME);
        miscInfo.setFundingAgency(FUNDING_AGENCY);
        miscInfo.setFundingTitle(FUNDING_TITLE);
        miscInfo.setFundingId(FUNDING_ID);
        miscInfo.setResearchProject(RESEARCH_PROJECT);
        miscInfo.setDatasetDoi(DATASET_DOI);
        miscInfo.setAccessId(ACCESS_ID);
        miscInfo.setWebsite(WEBSITE);
        miscInfo.setDownloadUrl(DOWNLOAD_URL);
        miscInfo.setCitation(CITATION);
        miscInfo.setSynopsis(SYNOPSIS);
        miscInfo.setPurpose(PURPOSE);
        miscInfo.setReferences(REFERENCES);
        miscInfo.setPortsOfCall(PORTS_OF_CALL);
        miscInfo.setAddnInfo(ADDN_INFO_LIST);
        miscInfo.setStartDatestamp(START_DATESTAMP);
        miscInfo.setEndDatestamp(END_DATESTAMP);
        miscInfo.setHistory(HISTORY_LIST);
        assertNotEquals(miscInfo, dup);

        dup = miscInfo.clone();
        assertEquals(miscInfo, dup);
        assertNotSame(miscInfo, dup);

        assertNotSame(miscInfo.getReferences(), dup.getReferences());
        assertNotSame(miscInfo.getPortsOfCall(), dup.getPortsOfCall());
        assertNotSame(miscInfo.getAddnInfo(), dup.getAddnInfo());
        assertNotSame(miscInfo.getStartDatestamp(), dup.getStartDatestamp());
        assertNotSame(miscInfo.getEndDatestamp(), dup.getEndDatestamp());

        ArrayList<Datestamp> history = miscInfo.getHistory();
        ArrayList<Datestamp> dupHistory = dup.getHistory();
        assertNotSame(history, dupHistory);
        for (int k = 0; k < history.size(); k++) {
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

        first.setSectionName(SECTION_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSectionName(SECTION_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFundingAgency(FUNDING_AGENCY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFundingAgency(FUNDING_AGENCY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFundingTitle(FUNDING_TITLE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFundingTitle(FUNDING_TITLE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFundingId(FUNDING_ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFundingId(FUNDING_ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setResearchProject(RESEARCH_PROJECT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setResearchProject(RESEARCH_PROJECT);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setDatasetDoi(DATASET_DOI);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setDatasetDoi(DATASET_DOI);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAccessId(ACCESS_ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAccessId(ACCESS_ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setWebsite(WEBSITE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setWebsite(WEBSITE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setDownloadUrl(DOWNLOAD_URL);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setDownloadUrl(DOWNLOAD_URL);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSynopsis(SYNOPSIS);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSynopsis(SYNOPSIS);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPurpose(PURPOSE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPurpose(PURPOSE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setCitation(CITATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCitation(CITATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setReferences(REFERENCES);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setReferences(REFERENCES);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPortsOfCall(PORTS_OF_CALL);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPortsOfCall(PORTS_OF_CALL);
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

