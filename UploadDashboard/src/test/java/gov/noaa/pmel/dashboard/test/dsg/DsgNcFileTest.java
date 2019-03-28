/**
 *
 */
package gov.noaa.pmel.dashboard.test.dsg;

import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.dsg.StdUserDataArray;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.test.datatype.KnownDataTypesTest;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests of methods of {@link DsgNcFile}
 *
 * @author Karl Smith
 */
public class DsgNcFileTest {

    public DsgNcFile dsgNcFile = null;

    /**
     * Test method for successfully creating a DSG file using {@link DsgNcFile#createFromUserData}.
     */
    @Test
    public void testCreate() throws Exception {
        ArrayList<String> userColumnNames = new ArrayList<String>(Arrays.asList(
                "depth,dataset,vessel,month,day,year,hour,minute,lat,lon,SST,sal,xCO2_SST,pCO2_Tequ,P_atm,speed"
                        .split(",")));
        ArrayList<DataColumnType> testTypes = new ArrayList<DataColumnType>(Arrays.asList(
                DashboardServerUtils.SAMPLE_DEPTH.duplicate(),
                DashboardServerUtils.DATASET_NAME.duplicate(),
                DashboardServerUtils.PLATFORM_NAME.duplicate(),
                DashboardServerUtils.MONTH_OF_YEAR.duplicate(),
                DashboardServerUtils.DAY_OF_MONTH.duplicate(),
                DashboardServerUtils.YEAR.duplicate(),
                DashboardServerUtils.HOUR_OF_DAY.duplicate(),
                DashboardServerUtils.MINUTE_OF_HOUR.duplicate(),
                DashboardServerUtils.LATITUDE.duplicate(),
                DashboardServerUtils.LONGITUDE.duplicate(),
                SocatTypes.SST.duplicate(),
                SocatTypes.SALINITY.duplicate(),
                SocatTypes.XCO2_WATER_SST_DRY.duplicate(),
                SocatTypes.PCO2_WATER_TEQU_WET.duplicate(),
                SocatTypes.PATM.duplicate(),
                KnownDataTypesTest.SHIP_SPEED.duplicate()));
        String[] dataValueStrings = {
                "5,31B520060606,GM0606,6,10,2006,23,48,29.0514,-92.759,28.78,33.68,409.7,392.5,1009.281,0.3",
                "5,31B520060606,GM0606,6,10,2006,23,49,29.0513,-92.759,28.9,33.56,405.5,388.3,1009.298,0.3",
                "5,31B520060606,GM0606,6,10,2006,23,50,29.0518,-92.7591,28.94,33.48,402.1,385.1,1009.314,2",
                "5,31B520060606,GM0606,6,10,2006,23,51,29.0517,-92.7592,28.99,33.44,399.7,382.7,1009.302,0.3",
                "5,31B520060606,GM0606,6,10,2006,23,52,29.0516,-92.7592,28.9,33.39,397.9,381,1009.29,0.3",
                "5,31B520060606,GM0606,6,10,2006,23,53,29.0516,-92.7593,28.93,33.38,397.1,380.3,1009.283,0.3",
                "5,31B520060606,GM0606,6,10,2006,23,54,29.0515,-92.7593,28.96,33.38,395.8,379,1009.272,0.3",
                "5,31B520060606,GM0606,6,10,2006,23,55,29.051,-92.76,28.88,33.38,395.7,378.9,1009.264,3",
                "5,31B520060606,GM0606,6,10,2006,23,56,29.0502,-92.7597,29.08,33.4,395.3,378.3,1009.264,3.1",
                "5,31B520060606,GM0606,6,10,2006,23,57,29.0494,-92.7593,29.35,33.3,392.1,375.1,1009.255,3.1",
                "5,31B520060606,GM0606,6,10,2006,23,58,29.0486,-92.759,29.34,33.28,391,374,1009.246,3.1",
                "5,31B520060606,GM0606,6,10,2006,23,59,29.0478,-92.7587,29.29,33.28,390.5,373.6,1009.223,3.1",
                "5,31B520060606,GM0606,6,11,2006,0,00,29.0478,-92.7538,29.29,33.32,390.9,374,1009.23,17.6",
                "5,31B520060606,GM0606,6,11,2006,0,01,29.0492,-92.7522,29.35,33.41,390.3,373.3,1009.255,7.8",
                "5,31B520060606,GM0606,6,11,2006,0,02,29.0506,-92.7505,29.39,33.47,393,375.9,1009.266,7.8",
                "5,31B520060606,GM0606,6,11,2006,0,03,29.052,-92.7489,29.43,33.55,395.7,378.4,1009.28,7.8",
                "5,31B520060606,GM0606,6,11,2006,0,04,29.0534,-92.7472,29.73,33.64,399.7,382,1009.3,7.8",
                "5,31B520060606,GM0606,6,11,2006,0,05,29.0577,-92.7492,29.84,33.64,402.9,385,1009.302,16.9",
                "5,31B520060606,GM0606,6,11,2006,0,06,29.0587,-92.7512,29.67,33.55,406.9,388.9,1009.305,8.2",
                "5,31B520060606,GM0606,6,11,2006,0,07,29.0597,-92.7533,29.66,33.52,408.1,390.2,1009.308,8.2",
                "5,31B520060606,GM0606,6,11,2006,0,08,29.0608,-92.7553,29.82,33.42,408.1,390,1009.306,8.2",
                "5,31B520060606,GM0606,6,11,2006,0,09,29.0618,-92.7574,29.81,33.31,408.2,390,1009.31,8.2",
                "5,31B520060606,GM0606,6,11,2006,0,10,29.0648,-92.7623,29.82,33.22,405.9,387.9,1009.304,20.8",
                "5,31B520060606,GM0606,6,11,2006,0,11,29.0641,-92.7641,29.9,33.14,404,386,1009.26,7.1",
                "5,31B520060606,GM0606,6,11,2006,0,12,29.0634,-92.766,29.89,32.97,402.9,384.9,1009.237,7.1"
        };
        String expocode = "31B520060606";
        ArrayList<ArrayList<String>> testValues = new ArrayList<ArrayList<String>>();
        for (String valsString : dataValueStrings) {
            ArrayList<String> dataVals = new ArrayList<String>(Arrays.asList(valsString.split(",", -1)));
            testValues.add(dataVals);
        }

        // Create the DashboardDatasetData from the above data
        DashboardDatasetData cruise = new DashboardDatasetData();
        cruise.setDatasetId(expocode);
        cruise.setUserColNames(userColumnNames);
        cruise.setDataColTypes(testTypes);
        cruise.setDataValues(testValues);
        ArrayList<Integer> rowNums = new ArrayList<Integer>(testValues.size());
        for (int k = 1; k <= testValues.size(); k++) {
            rowNums.add(k);
        }
        cruise.setRowNums(rowNums);

        StdUserDataArray stdUserData = new StdUserDataArray(cruise, KnownDataTypesTest.TEST_KNOWN_USER_DATA_TYPES);

        // Create the DsgMetadata for this cruise
        DsgMetadata metadata = new DsgMetadata(KnownDataTypesTest.TEST_KNOWN_METADATA_FILE_TYPES);
        metadata.setDatasetId(expocode);
        metadata.setDatasetName(expocode);
        metadata.setPlatformName("GM0606");
        metadata.setInvestigatorNames("Public, Nancy S.; Public, John Q.");
        metadata.setPlatformType("Battleship");
        metadata.setSouthmostLatitude(20.04);
        metadata.setNorthmostLatitude(29.07);
        metadata.setWestmostLongitude(-92.77);
        metadata.setEastmostLongitude(-92.74);
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        metadata.setBeginTime(dateFmt.parse("2006-06-10 23:48 UTC").getTime() / 1000.0);
        metadata.setEndTime(dateFmt.parse("2006-06-11 00:12 UTC").getTime() / 1000.0);

        File parentDir = new File("/var/tmp/junit");
        if ( !parentDir.exists() )
            parentDir.mkdir();
        dsgNcFile = new DsgNcFile(parentDir, expocode + ".nc");
        dsgNcFile.createFromUserData(metadata, stdUserData, KnownDataTypesTest.TEST_KNOWN_DATA_FILE_TYPES);
        assertTrue(dsgNcFile.exists());
        assertEquals(expocode, dsgNcFile.getMetadata().getDatasetId());
        assertEquals(dataValueStrings.length, dsgNcFile.getStdDataArray().getNumSamples());
    }

    /**
     * Test method for checking expected failures to a DSG file using {@link DsgNcFile#createFromUserData}.
     */
    @Test
    public void testBadMissingValuesFail() throws Exception {
        ArrayList<String> userColumnNames = new ArrayList<String>(Arrays.asList(
                "depth,dataset,vessel,month,day,year,hour,minute,lat,lon,SST,sal,xCO2_SST,pCO2_Tequ,P_atm,speed"
                        .split(",")));
        ArrayList<DataColumnType> testTypes = new ArrayList<DataColumnType>(Arrays.asList(
                DashboardServerUtils.SAMPLE_DEPTH.duplicate(),
                DashboardServerUtils.DATASET_NAME.duplicate(),
                DashboardServerUtils.PLATFORM_NAME.duplicate(),
                DashboardServerUtils.MONTH_OF_YEAR.duplicate(),
                DashboardServerUtils.DAY_OF_MONTH.duplicate(),
                DashboardServerUtils.YEAR.duplicate(),
                DashboardServerUtils.HOUR_OF_DAY.duplicate(),
                DashboardServerUtils.MINUTE_OF_HOUR.duplicate(),
                DashboardServerUtils.LATITUDE.duplicate(),
                DashboardServerUtils.LONGITUDE.duplicate(),
                SocatTypes.SST.duplicate(),
                SocatTypes.SALINITY.duplicate(),
                SocatTypes.XCO2_WATER_SST_DRY.duplicate(),
                SocatTypes.PCO2_WATER_TEQU_WET.duplicate(),
                SocatTypes.PATM.duplicate(),
                KnownDataTypesTest.SHIP_SPEED.duplicate()));
        String[][] badTimeDataValueStringsSets = {
                {
                        "5,11B520060606,GM0606,2,28,2006,23,48,29.0514,-92.759,28.78,33.68,409.7,392.5,1009.281,0.3",
                        "5,11B520060606,GM0606,2,29,2006,23,49,29.0513,-92.759,28.9,33.56,405.5,388.3,1009.298,0.3",
                        "5,11B520060606,GM0606,3,1,2006,23,50,29.0518,-92.7591,28.94,33.48,402.1,385.1,1009.314,2"
                },
                {
                        "5,11B520060606,GM0606,2,28,2006,23,48,29.0514,-92.759,28.78,33.68,409.7,392.5,1009.281,0.3",
                        "5,11B520060606,GM0606,2,NaN,2006,23,49,29.0513,-92.759,28.9,33.56,405.5,388.3,1009.298,0.3",
                        "5,11B520060606,GM0606,3,1,2006,23,50,29.0518,-92.7591,28.94,33.48,402.1,385.1,1009.314,2"
                }
        };
        String expocode = "11B520060606";
        for (String[] dataValueStrings : badTimeDataValueStringsSets) {
            ArrayList<ArrayList<String>> testValues = new ArrayList<ArrayList<String>>();
            for (String valsString : dataValueStrings) {
                ArrayList<String> dataVals = new ArrayList<String>(Arrays.asList(valsString.split(",", -1)));
                testValues.add(dataVals);
            }

            // Create the DashboardDatasetData from the above data
            DashboardDatasetData cruise = new DashboardDatasetData();
            cruise.setDatasetId(expocode);
            cruise.setUserColNames(userColumnNames);
            cruise.setDataColTypes(testTypes);
            cruise.setDataValues(testValues);
            ArrayList<Integer> rowNums = new ArrayList<Integer>(testValues.size());
            for (int k = 1; k <= testValues.size(); k++) {
                rowNums.add(k);
            }
            cruise.setRowNums(rowNums);

            StdUserDataArray stdUserData = new StdUserDataArray(cruise, KnownDataTypesTest.TEST_KNOWN_USER_DATA_TYPES);

            // Create the DsgMetadata for this cruise
            DsgMetadata metadata = new DsgMetadata(KnownDataTypesTest.TEST_KNOWN_METADATA_FILE_TYPES);
            metadata.setDatasetId(expocode);
            metadata.setDatasetName(expocode);
            metadata.setPlatformName("GM0606");
            metadata.setInvestigatorNames("Public, Nancy S.; Public, John Q.");
            metadata.setPlatformType("Battleship");
            metadata.setSouthmostLatitude(20.04);
            metadata.setNorthmostLatitude(29.07);
            metadata.setWestmostLongitude(-92.77);
            metadata.setEastmostLongitude(-92.74);
            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
            metadata.setBeginTime(dateFmt.parse("2006-02-28 23:48 UTC").getTime() / 1000.0);
            metadata.setEndTime(dateFmt.parse("2006-03-01 23:50 UTC").getTime() / 1000.0);

            File parentDir = new File("/var/tmp/junit");
            if ( !parentDir.exists() )
                parentDir.mkdir();
            dsgNcFile = new DsgNcFile(parentDir, expocode + ".nc");
            try {
                dsgNcFile.createFromUserData(metadata, stdUserData, KnownDataTypesTest.TEST_KNOWN_DATA_FILE_TYPES);
            } catch ( IllegalArgumentException ex ) {
                dsgNcFile.delete();
            }
            assertFalse(dsgNcFile.exists());
        }
    }

}
