/**
 *
 */
package gov.noaa.pmel.dashboard.test.dsg;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for methods in gov.noaa.pmel.dashboard.shared.DsgMetadata.
 * The convenience getters and setters still work properly when their type
 * is not part of the known types.
 *
 * @author Karl Smith
 */
public class DsgMetadataTest {

    static final String EXPOCODE = "XXXX20140113";
    static final String CRUISE_NAME = "My Cruise";
    static final String PLATFORM_NAME = "My Vessel";
    static final String ORGANIZATION_NAME = "PMEL/NOAA";
    static final String PLATFORM_TYPE = "Battleship";
    static final Double WESTMOST_LONGITUDE = -160.0;
    static final Double EASTMOST_LONGITUDE = -135.0;
    static final Double SOUTHMOST_LATITUDE = 15.0;
    static final Double NORTHMOST_LATITUDE = 50.0;
    static final Double BEGIN_TIME = new Date().getTime() / 1000.0;
    static final Double END_TIME = BEGIN_TIME + 600.0;
    static final String INVESTIGATOR_NAMES = "Smith, K. : Doe, J.";
    static final String SOCAT_VERSION = "3.0U";
    static final String ALL_REGION_IDS = "NT";
    static final String OCADS_DOI = "doi:ocads012345";
    static final String SOCAT_DOI = "doi:pangaea012345";
    static final String QC_FLAG = "C";

    /**
     * Test method for {@link DsgMetadata#getValuesMap()}
     * and {@link DsgMetadata#setValue(DashDataType, Object)}
     */
    @Test
    public void testGetSetStringVariableValue() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);

        mdata.setValue(DashboardServerUtils.DATASET_ID, EXPOCODE);
        TreeMap<DashDataType<?>,Object> valuesMap = mdata.getValuesMap();
        assertEquals(EXPOCODE, valuesMap.get(DashboardServerUtils.DATASET_ID));

        mdata.setValue(DashboardServerUtils.DATASET_ID, null);
        valuesMap = mdata.getValuesMap();
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, valuesMap.get(DashboardServerUtils.DATASET_ID));

        mdata.setValue(DashboardServerUtils.EASTERNMOST_LONGITUDE, EASTMOST_LONGITUDE);
        valuesMap = mdata.getValuesMap();
        assertEquals(EASTMOST_LONGITUDE, valuesMap.get(DashboardServerUtils.EASTERNMOST_LONGITUDE));

        mdata.setValue(DashboardServerUtils.EASTERNMOST_LONGITUDE, null);
        valuesMap = mdata.getValuesMap();
        assertEquals(DashboardUtils.FP_MISSING_VALUE, valuesMap.get(DashboardServerUtils.EASTERNMOST_LONGITUDE));

        boolean errCaught = false;
        try {
            mdata.setValue(DashboardServerUtils.LONGITUDE, EASTMOST_LONGITUDE);
        } catch ( IllegalArgumentException ex ) {
            errCaught = true;
        }
        assertTrue(errCaught);
    }

    /**
     * Test method for {@link DsgMetadata#getDatasetId()} and {@link DsgMetadata#setDatasetId(String)}.
     */
    @Test
    public void testGetSetDatasetId() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setDatasetId(EXPOCODE);
        assertEquals(EXPOCODE, mdata.getDatasetId());
        mdata.setDatasetId(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
    }

    /**
     * Test method for {@link DsgMetadata#getDatasetName()} and {@link DsgMetadata#setDatasetName(String)}.
     */
    @Test
    public void testGetSetDatasetName() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        mdata.setDatasetName(CRUISE_NAME);
        assertEquals(CRUISE_NAME, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setDatasetName(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
    }

    /**
     * Test method for {@link DsgMetadata#getPlatformName()} and {@link DsgMetadata#setPlatformName(String)}.
     */
    @Test
    public void testGetSetPlatformName() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        mdata.setPlatformName(PLATFORM_NAME);
        assertEquals(PLATFORM_NAME, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setPlatformName(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
    }

    /**
     * Test method for {@link DsgMetadata#getOrganizationName()} and {@link DsgMetadata#setOrganizationName(String)}.
     */
    @Test
    public void testGetSetOrganization() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        mdata.setOrganizationName(ORGANIZATION_NAME);
        assertEquals(ORGANIZATION_NAME, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setOrganizationName(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
    }

    /**
     * Test method for {@link DsgMetadata#getInvestigatorNames()} and {@link DsgMetadata#setInvestigatorNames(String)}.
     */
    @Test
    public void testGetSetInvestigatorNames() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        mdata.setInvestigatorNames(INVESTIGATOR_NAMES);
        assertEquals(INVESTIGATOR_NAMES, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setInvestigatorNames(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
    }

    /**
     * Test method for {@link DsgMetadata#getPlatformType()} and {@link DsgMetadata#setPlatformType(String)}.
     */
    @Test
    public void testGetSetPlatformType() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
        mdata.setPlatformType(PLATFORM_TYPE);
        assertEquals(PLATFORM_TYPE, mdata.getPlatformType());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setPlatformType(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
    }

    /**
     * Test method for {@link DsgMetadata#getWestmostLongitude()} and {@link DsgMetadata#setWestmostLongitude(Double)}.
     */
    @Test
    public void testGetSetWestmostLongitude() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getWestmostLongitude());
        mdata.setWestmostLongitude(WESTMOST_LONGITUDE);
        assertEquals(WESTMOST_LONGITUDE, mdata.getWestmostLongitude());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setWestmostLongitude(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getWestmostLongitude());
    }

    /**
     * Test method for {@link DsgMetadata#getEastmostLongitude()} and {@link DsgMetadata#setEastmostLongitude(Double)}.
     */
    @Test
    public void testGetSetEastmostLongitude() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEastmostLongitude());
        mdata.setEastmostLongitude(EASTMOST_LONGITUDE);
        assertEquals(EASTMOST_LONGITUDE, mdata.getEastmostLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getWestmostLongitude());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setEastmostLongitude(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEastmostLongitude());
    }

    /**
     * Test method for {@link DsgMetadata#getSouthmostLatitude()} and {@link DsgMetadata#setSouthmostLatitude(Double)}.
     */
    @Test
    public void testGetSetSouthmostLatitude() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getSouthmostLatitude());
        mdata.setSouthmostLatitude(SOUTHMOST_LATITUDE);
        assertEquals(SOUTHMOST_LATITUDE, mdata.getSouthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEastmostLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getWestmostLongitude());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setSouthmostLatitude(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getSouthmostLatitude());
    }

    /**
     * Test method for {@link DsgMetadata#getNorthmostLatitude()} and {@link DsgMetadata#setNorthmostLatitude(Double)}.
     */
    @Test
    public void testGetSetNorthmostLatitude() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getNorthmostLatitude());
        mdata.setNorthmostLatitude(NORTHMOST_LATITUDE);
        assertEquals(NORTHMOST_LATITUDE, mdata.getNorthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getSouthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEastmostLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getWestmostLongitude());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setNorthmostLatitude(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getNorthmostLatitude());
    }

    /**
     * Test method for {@link DsgMetadata#getBeginTime()} and {@link DsgMetadata#setBeginTime(Double)}.
     */
    @Test
    public void testSetBeginTime() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getBeginTime());
        mdata.setBeginTime(BEGIN_TIME);
        assertEquals(BEGIN_TIME, mdata.getBeginTime());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getNorthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getSouthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEastmostLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getWestmostLongitude());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setBeginTime(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getBeginTime());
    }

    /**
     * Test method for {@link DsgMetadata#getEndTime()} and {@link DsgMetadata#setEndTime(Double)}.
     */
    @Test
    public void testGetSetEndTime() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEndTime());
        mdata.setEndTime(END_TIME);
        assertEquals(END_TIME, mdata.getEndTime());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getBeginTime());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getNorthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getSouthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEastmostLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getWestmostLongitude());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setEndTime(null);
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEndTime());
    }

    /**
     * Test method for {@link DsgMetadata#getSourceDOI()} and {@link DsgMetadata#setSourceDOI(String)}
     */
    @Test
    public void testGetSetSourceDOI() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSourceDOI());
        mdata.setSourceDOI(OCADS_DOI);
        assertEquals(OCADS_DOI, mdata.getSourceDOI());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEndTime());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getBeginTime());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getNorthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getSouthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEastmostLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getWestmostLongitude());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setSourceDOI(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSourceDOI());
    }

    /**
     * Test method for {@link DsgMetadata#getDatasetQCFlag()} and {@link DsgMetadata#setDatasetQCFlag(String)}
     */
    @Test
    public void testGetSetDatasetQCFlag() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetQCFlag());
        mdata.setDatasetQCFlag(QC_FLAG);
        assertEquals(QC_FLAG, mdata.getDatasetQCFlag());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSourceDOI());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEndTime());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getBeginTime());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getNorthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getSouthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEastmostLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getWestmostLongitude());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setDatasetQCFlag(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetQCFlag());
    }

    /**
     * Test method for {@link DsgMetadata#getVersion()} and {@link DsgMetadata#setVersion(String)}.
     */
    @Test
    public void testGetSetVersion() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVersion());
        mdata.setVersion(SOCAT_VERSION);
        assertEquals(SOCAT_VERSION, mdata.getVersion());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetQCFlag());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSourceDOI());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEndTime());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getBeginTime());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getNorthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getSouthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEastmostLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getWestmostLongitude());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setVersion(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVersion());
    }

    /**
     * Test method for {@link DsgMetadata#getAllRegionIDs()} and {@link DsgMetadata#setAllRegionIDs(String)}.
     */
    @Test
    public void testGetSetAllRegionIDs() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
        mdata.setAllRegionIDs(ALL_REGION_IDS);
        assertEquals(ALL_REGION_IDS, mdata.getAllRegionIDs());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getVersion());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetQCFlag());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getSourceDOI());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEndTime());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getBeginTime());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getNorthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getSouthmostLatitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getEastmostLongitude());
        assertEquals(DashboardUtils.FP_MISSING_VALUE, mdata.getWestmostLongitude());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformType());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getInvestigatorNames());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getOrganizationName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getPlatformName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetName());
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getDatasetId());
        mdata.setAllRegionIDs(null);
        assertEquals(DashboardUtils.STRING_MISSING_VALUE, mdata.getAllRegionIDs());
    }

    /**
     * Test method for {@link DsgMetadata#getMaxStringLength()}
     */
    @Test
    public void testGetMaxStringLength() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertEquals(32, mdata.getMaxStringLength());
        mdata.setDatasetId(EXPOCODE);
        assertEquals(32, mdata.getMaxStringLength());
        mdata.setDatasetName("MyRidiculouslyLongNameForADataset.tsv");
        assertEquals(64, mdata.getMaxStringLength());
        mdata.setDatasetName("AShortName.tsv");
        assertEquals(32, mdata.getMaxStringLength());
    }

    /**
     * Test method for {@link DsgMetadata#assignLonLatTimeLimits(Double[], Double[], Double[], Set)}
     */
    @Test
    public void testAssignLonLatTimeLimits() {
        HashSet<Integer> errRowIdxs = new HashSet<Integer>(Arrays.asList(0, 3, 14));
        Double[] sampleLons = new Double[] {
                145.0, 165.0, 155.0, -999.0, -180.0,
                160.0, -175.0, 175.0, -170.0, -165.0,
                -175.0, -180.0, 180.0, 175.0, -999.0
        };
        Double[] expectedWestEastLon = new Double[] { 155.0, -165.0 };
        Double[] sampleLats = new Double[] {
                20.0, 10.0, 5.0, -999.0, 7.5,
                10.0, 12.5, 15.0, 12.5, 10.0,
                5.0, 0.0, -5.0, -10.0, -999.0
        };
        Double[] expectedSouthNorthLat = new Double[] { -10.0, 15.0 };
        Double[] sampleTimes = new Double[] {
                -99999.0, 105060.0, 105120.0, -99999.0, 105240.0,
                105300.0, 105360.0, 105420.0, 105480.0, 105540.0,
                105600.0, 105660.0, 105720.0, 105780.0, 105840.0
        };
        Double[] expectedBeginEndTime = new Double[] { 105060.0, 105780.0 };

        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();
        DsgMetadata mdata = new DsgMetadata(knownTypes);
        mdata.assignLonLatTimeLimits(sampleLons, sampleLats, sampleTimes, errRowIdxs);
        assertEquals(expectedWestEastLon[0], mdata.getWestmostLongitude());
        assertEquals(expectedWestEastLon[1], mdata.getEastmostLongitude());
        assertEquals(expectedSouthNorthLat[0], mdata.getSouthmostLatitude());
        assertEquals(expectedSouthNorthLat[1], mdata.getNorthmostLatitude());
        assertEquals(expectedBeginEndTime[0], mdata.getBeginTime());
        assertEquals(expectedBeginEndTime[1], mdata.getEndTime());

        sampleLons = new Double[] {
                -999.0, 25.0, 65.0, -999.0, 105.0,
                145.0, -175.0, -135.0, -95.0, -55.0,
                -15.0, 20.0, 50.0, 65.0, -999.0
        };
        expectedWestEastLon = new Double[] { -180.0, 180.0 };
        sampleLats = new Double[] {
                -999.0, 89.0, 88.0, -999.0, 89.5,
                89.0, 88.5, 89.0, 89.5, 89.0,
                89.5, 89.0, 88.5, 89.0, -999.0
        };
        expectedSouthNorthLat = new Double[] { 88.0, 89.5 };

        mdata.assignLonLatTimeLimits(sampleLons, sampleLats, sampleTimes, errRowIdxs);
        assertEquals(expectedWestEastLon[0], mdata.getWestmostLongitude());
        assertEquals(expectedWestEastLon[1], mdata.getEastmostLongitude());
        assertEquals(expectedSouthNorthLat[0], mdata.getSouthmostLatitude());
        assertEquals(expectedSouthNorthLat[1], mdata.getNorthmostLatitude());
        assertEquals(expectedBeginEndTime[0], mdata.getBeginTime());
        assertEquals(expectedBeginEndTime[1], mdata.getEndTime());
    }

    /**
     * Test method for {@link DsgMetadata#hashCode()} and {@link DsgMetadata#equals(Object)}.
     */
    @Test
    public void testHashCodeEqualsObject() {
        KnownDataTypes knownTypes = new KnownDataTypes().addStandardTypesForMetadataFiles();

        DsgMetadata mdata = new DsgMetadata(knownTypes);
        assertFalse(mdata.equals(null));
        assertFalse(mdata.equals(EXPOCODE));

        DsgMetadata other = new DsgMetadata(knownTypes);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setDatasetId(EXPOCODE);
        assertNotEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setDatasetId(EXPOCODE);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setDatasetName(CRUISE_NAME);
        assertNotEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setDatasetName(CRUISE_NAME);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setPlatformName(PLATFORM_NAME);
        assertNotEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setPlatformName(PLATFORM_NAME);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setOrganizationName(ORGANIZATION_NAME);
        assertNotEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setOrganizationName(ORGANIZATION_NAME);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setInvestigatorNames(INVESTIGATOR_NAMES);
        assertNotEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setInvestigatorNames(INVESTIGATOR_NAMES);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setPlatformType(PLATFORM_TYPE);
        assertNotEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setPlatformType(PLATFORM_TYPE);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setVersion(SOCAT_VERSION);
        assertNotEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setVersion(SOCAT_VERSION);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setAllRegionIDs(ALL_REGION_IDS);
        assertNotEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setAllRegionIDs(ALL_REGION_IDS);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setSourceDOI(OCADS_DOI);
        assertNotEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setSourceDOI(OCADS_DOI);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setDatasetQCFlag(QC_FLAG);
        assertNotEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setDatasetQCFlag(QC_FLAG);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        // hashCode ignores floating point values
        mdata.setWestmostLongitude(WESTMOST_LONGITUDE);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setWestmostLongitude(WESTMOST_LONGITUDE);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        // hashCode ignores floating point values
        mdata.setEastmostLongitude(EASTMOST_LONGITUDE);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setEastmostLongitude(EASTMOST_LONGITUDE);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        // hashCode ignores floating point values
        mdata.setSouthmostLatitude(SOUTHMOST_LATITUDE);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setSouthmostLatitude(SOUTHMOST_LATITUDE);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        // hashCode ignores floating point values
        mdata.setNorthmostLatitude(NORTHMOST_LATITUDE);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setNorthmostLatitude(NORTHMOST_LATITUDE);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setBeginTime(BEGIN_TIME);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setBeginTime(BEGIN_TIME);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));

        mdata.setEndTime(END_TIME);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertFalse(mdata.equals(other));
        other.setEndTime(END_TIME);
        assertEquals(mdata.hashCode(), other.hashCode());
        assertTrue(mdata.equals(other));
    }

}
