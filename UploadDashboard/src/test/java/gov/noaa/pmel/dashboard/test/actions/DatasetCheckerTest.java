package gov.noaa.pmel.dashboard.test.actions;

import gov.noaa.pmel.dashboard.actions.DatasetChecker;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.dsg.StdUserDataArray;
import gov.noaa.pmel.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.dashboard.qc.RowColumn;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DataQCFlag.Severity;
import gov.noaa.pmel.dashboard.test.datatype.KnownDataTypesTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test of the methods in {@link DatasetChecker}
 *
 * @author Karl Smith
 */
public class DatasetCheckerTest {

    // private static final data strings given at the end

    /**
     * Test of {@link DatasetChecker#DatasetChecker(KnownDataTypes, CheckerMessageHandler)
     * and {@link DatasetChecker#standardizeDataset(DashboardDatasetData, DsgMetadata)
     */
    @Test
    public void testGoodDatasetCheck() {
        CheckerMessageHandler msgHandler = new CheckerMessageHandler("/var/tmp/junit", null, null);
        DatasetChecker dataChecker = new DatasetChecker(KnownDataTypesTest.TEST_KNOWN_USER_DATA_TYPES, msgHandler);

        DashboardDatasetData goodDataset = new DashboardDatasetData();
        goodDataset.setDatasetId("33RO20030715");
        goodDataset.setDataColTypes(gqbDataColTypes);
        goodDataset.setUserColNames(gqbUserColumnNames);
        ArrayList<ArrayList<String>> goodCruiseData = new ArrayList<ArrayList<String>>(goodDataTSVStrings.length);
        for (String dataString : goodDataTSVStrings) {
            goodCruiseData.add(new ArrayList<String>(Arrays.asList(dataString.split("\t", -1))));
        }
        goodDataset.setDataValues(goodCruiseData);
        ArrayList<Integer> goodRowNums = new ArrayList<Integer>(goodCruiseData.size());
        for (int k = 1; k <= goodCruiseData.size(); k++) {
            goodRowNums.add(k);
        }
        goodDataset.setRowNums(goodRowNums);
        goodDataset.setNumDataRows(goodRowNums.size());

        // TODO: add metadata - verify values assigned from data
        StdUserDataArray goodStdUserData = dataChecker.standardizeDataset(goodDataset, null);
        ArrayList<ADCMessage> goodMsgList = goodStdUserData.getStandardizationMessages();
        assertEquals(0, goodMsgList.size());
    }

    /**
     * Test of {@link DatasetChecker#DatasetChecker(KnownDataTypes, CheckerMessageHandler)
     * and {@link DatasetChecker#standardizeDataset(DashboardDatasetData, DsgMetadata)
     */
    @Test
    public void testQuestionableDatasetCheck() {
        CheckerMessageHandler msgHandler = new CheckerMessageHandler("/var/tmp/junit", null, null);
        DatasetChecker dataChecker = new DatasetChecker(KnownDataTypesTest.TEST_KNOWN_USER_DATA_TYPES, msgHandler);

        DashboardDatasetData questDataset = new DashboardDatasetData();
        questDataset.setDatasetId("33RO20030715");
        questDataset.setDataColTypes(gqbDataColTypes);
        questDataset.setUserColNames(gqbUserColumnNames);
        ArrayList<ArrayList<String>> questCruiseData = new ArrayList<ArrayList<String>>(questDataTSVStrings.length);
        for (String dataString : questDataTSVStrings) {
            questCruiseData.add(new ArrayList<String>(Arrays.asList(dataString.split("\t", -1))));
        }
        questDataset.setDataValues(questCruiseData);
        ArrayList<Integer> questRowNums = new ArrayList<Integer>(questCruiseData.size());
        for (int k = 1; k <= questCruiseData.size(); k++) {
            questRowNums.add(k);
        }
        questDataset.setRowNums(questRowNums);
        questDataset.setNumDataRows(questRowNums.size());

        // TODO: add metadata - verify values assigned from data
        StdUserDataArray questStdUserData = dataChecker.standardizeDataset(questDataset, null);
        ArrayList<ADCMessage> questMsgList = questStdUserData.getStandardizationMessages();
        assertEquals(7, questMsgList.size());
        for (int k = 0; k < questMsgList.size(); k++) {
            ADCMessage msg = questMsgList.get(k);
            assertEquals(Severity.WARNING, msg.getSeverity());
            assertEquals(Integer.valueOf(k + 3), msg.getRowNumber());
            assertEquals(Integer.valueOf(23), msg.getColNumber());
            assertEquals("xco2teq", msg.getColName());
        }
    }

    /**
     * Test of {@link DatasetChecker#DatasetChecker(KnownDataTypes, CheckerMessageHandler)
     * and {@link DatasetChecker#standardizeDataset(DashboardDatasetData, DsgMetadata)
     */
    @Test
    public void testBadDatasetCheck() {
        CheckerMessageHandler msgHandler = new CheckerMessageHandler("/var/tmp/junit", null, null);
        DatasetChecker dataChecker = new DatasetChecker(KnownDataTypesTest.TEST_KNOWN_USER_DATA_TYPES, msgHandler);

        DashboardDatasetData badDataset = new DashboardDatasetData();
        badDataset.setDatasetId("33RO20030715");
        badDataset.setDataColTypes(gqbDataColTypes);
        badDataset.setUserColNames(gqbUserColumnNames);
        ArrayList<ArrayList<String>> badCruiseData = new ArrayList<ArrayList<String>>(badDataTSVStrings.length);
        for (String dataString : badDataTSVStrings) {
            badCruiseData.add(new ArrayList<String>(Arrays.asList(dataString.split("\t", -1))));
        }
        badDataset.setDataValues(badCruiseData);
        ArrayList<Integer> badRowNums = new ArrayList<Integer>(badCruiseData.size());
        for (int k = 1; k <= badCruiseData.size(); k++) {
            badRowNums.add(k);
        }
        badDataset.setRowNums(badRowNums);
        badDataset.setNumDataRows(badRowNums.size());

        // TODO: add metadata - verify values assigned from data
        StdUserDataArray badStdUserData = dataChecker.standardizeDataset(badDataset, null);
        ArrayList<ADCMessage> badMsgList = badStdUserData.getStandardizationMessages();
        // Invalid time (Feb 31) in row 3, invalid latitudes (>90) in rows 3-9
        TreeSet<RowColumn> expectedCriticals = new TreeSet<RowColumn>(Arrays.asList(
                new RowColumn(3, 4),
                new RowColumn(3, 5),
                new RowColumn(3, 6),
                new RowColumn(3, 7),
                new RowColumn(3, 8),
                new RowColumn(3, 9),
                new RowColumn(3, 11),
                new RowColumn(4, 11),
                new RowColumn(5, 11),
                new RowColumn(6, 11),
                new RowColumn(7, 11),
                new RowColumn(8, 11),
                new RowColumn(9, 11)
        ));
        TreeSet<RowColumn> expectedErrors = new TreeSet<RowColumn>(Arrays.asList(
                new RowColumn(4, 4),
                new RowColumn(4, 5),
                new RowColumn(4, 6),
                new RowColumn(4, 7),
                new RowColumn(4, 8),
                new RowColumn(4, 9),
                new RowColumn(4, 10),
                new RowColumn(4, 11),
                new RowColumn(10, 4),
                new RowColumn(10, 5),
                new RowColumn(10, 6),
                new RowColumn(10, 7),
                new RowColumn(10, 8),
                new RowColumn(10, 9),
                new RowColumn(10, 10),
                new RowColumn(10, 11),
                new RowColumn(25, 4),
                new RowColumn(25, 5),
                new RowColumn(25, 6),
                new RowColumn(25, 7),
                new RowColumn(25, 8),
                new RowColumn(25, 9),
                new RowColumn(25, 10),
                new RowColumn(25, 11)
        ));
        // The invalid time gives both bad time and mis-ordered messages for each date/time column
        // (not great, but okay for now)
        TreeSet<RowColumn> criticals = new TreeSet<RowColumn>();
        TreeSet<RowColumn> errors = new TreeSet<RowColumn>();
        for (ADCMessage msg : badMsgList) {
            if ( Severity.CRITICAL.equals(msg.getSeverity()) )
                criticals.add(new RowColumn(msg.getRowNumber(), msg.getColNumber()));
            else if ( Severity.ERROR.equals(msg.getSeverity()) )
                errors.add(new RowColumn(msg.getRowNumber(), msg.getColNumber()));
            else
                fail("Unexpected severity of " + msg.getSeverity());
        }
        assertEquals(expectedCriticals, criticals);
        assertEquals(expectedErrors, errors);
    }

    /**
     * Test of {@link DatasetChecker#DatasetChecker(KnownDataTypes, CheckerMessageHandler)
     * and {@link DatasetChecker#standardizeDataset(DashboardDatasetData, DsgMetadata)
     */
    @Test
    public void testDupsDatasetCheck() {
        CheckerMessageHandler msgHandler = new CheckerMessageHandler("/var/tmp/junit", null, null);
        DatasetChecker dataChecker = new DatasetChecker(KnownDataTypesTest.TEST_KNOWN_USER_DATA_TYPES, msgHandler);

        DashboardDatasetData dupsDataset = new DashboardDatasetData();
        dupsDataset.setDatasetId("320G20120508");
        dupsDataset.setDataColTypes(dupsDataColTypes);
        dupsDataset.setUserColNames(dupsUserColumnNames);
        ArrayList<ArrayList<String>> dupsCruiseData = new ArrayList<ArrayList<String>>(dupsDataTSVStrings.length);
        for (String dataString : dupsDataTSVStrings) {
            dupsCruiseData.add(new ArrayList<String>(Arrays.asList(dataString.split("\t", -1))));
        }
        dupsDataset.setDataValues(dupsCruiseData);
        ArrayList<Integer> dupsRowNums = new ArrayList<Integer>(dupsCruiseData.size());
        for (int k = 1; k <= dupsCruiseData.size(); k++) {
            dupsRowNums.add(k);
        }
        dupsDataset.setRowNums(dupsRowNums);
        dupsDataset.setNumDataRows(dupsRowNums.size());

        // TODO: add metadata - verify values assigned from data
        StdUserDataArray dupsStdUserData = dataChecker.standardizeDataset(dupsDataset, null);
        ArrayList<ADCMessage> dupsMsgList = dupsStdUserData.getStandardizationMessages();

        assertEquals(1, dupsMsgList.size());
        for (ADCMessage msg : dupsMsgList) {
            assertEquals(8, (int) msg.getColNumber());
            assertEquals(220, (int) msg.getRowNumber());
            assertEquals(Severity.ERROR, msg.getSeverity());
        }
    }

    /**
     * Test of {@link DatasetChecker#DatasetChecker(KnownDataTypes, CheckerMessageHandler)
     * and {@link DatasetChecker#standardizeDataset(DashboardDatasetData, DsgMetadata)
     */
    @Test
    public void testSpeedDatasetCheck() {
        CheckerMessageHandler msgHandler = new CheckerMessageHandler("/var/tmp/junit", null, null);
        DatasetChecker dataChecker = new DatasetChecker(KnownDataTypesTest.TEST_KNOWN_USER_DATA_TYPES, msgHandler);

        DashboardDatasetData speedDataset = new DashboardDatasetData();
        speedDataset.setDatasetId("76XL20091101");
        speedDataset.setDataColTypes(speedDataColTypes);
        speedDataset.setUserColNames(speedUserColumnNames);
        ArrayList<ArrayList<String>> speedCruiseData = new ArrayList<ArrayList<String>>(speedDataTSVStrings.length);
        for (String dataString : speedDataTSVStrings) {
            speedCruiseData.add(new ArrayList<String>(Arrays.asList(dataString.split("\t", -1))));
        }
        speedDataset.setDataValues(speedCruiseData);
        ArrayList<Integer> speedRowNums = new ArrayList<Integer>(speedCruiseData.size());
        for (int k = 1; k <= speedCruiseData.size(); k++) {
            speedRowNums.add(k);
        }
        speedDataset.setRowNums(speedRowNums);
        speedDataset.setNumDataRows(speedRowNums.size());

        // TODO: add metadata - verify values assigned from data
        StdUserDataArray speedStdUserData = dataChecker.standardizeDataset(speedDataset, null);
        ArrayList<ADCMessage> speedMsgList = speedStdUserData.getStandardizationMessages();
        TreeSet<Integer> warnRowNums = new TreeSet<Integer>();
        TreeSet<Integer> warnColNums = new TreeSet<Integer>();
        TreeSet<Integer> errRowNums = new TreeSet<Integer>();
        TreeSet<Integer> errColNums = new TreeSet<Integer>();
        int numWarns = 0;
        int numErrs = 0;
        for (ADCMessage msg : speedMsgList) {
            if ( Severity.WARNING.equals(msg.getSeverity()) ) {
                warnRowNums.add(msg.getRowNumber());
                warnColNums.add(msg.getColNumber());
                numWarns++;
            }
            else if ( Severity.ERROR.equals(msg.getSeverity()) ) {
                errRowNums.add(msg.getRowNumber());
                errColNums.add(msg.getColNumber());
                numErrs++;
            }
            else {
                fail("Unexpected severity of " + msg.getSeverity());
            }
        }

        TreeSet<Integer> expectedColNums = new TreeSet<Integer>(Arrays.asList(3, 4, 5, 6));
        TreeSet<Integer> expectedWarnRowNums = new TreeSet<Integer>(Arrays.asList(
                3, 34, 44, 72, 86, 97, 102, 154, 216
        ));
        TreeSet<Integer> expectedErrRowNums = new TreeSet<Integer>(Arrays.asList(
                155, 156, 157, 158, 159, 160, 161, 163, 166, 167, 168, 170, 171, 172, 175,
                176, 177, 178, 180, 182, 183, 184, 185, 186, 187, 191, 192, 193, 194, 195,
                196, 198, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 211, 213, 214,
                215, 217, 219, 220, 221
        ));

        assertEquals(expectedColNums, warnColNums);
        assertEquals(expectedColNums, errColNums);
        assertEquals(expectedWarnRowNums, warnRowNums);
        assertEquals(expectedErrRowNums, errRowNums);
        assertEquals(expectedColNums.size() * expectedWarnRowNums.size(), numWarns);
        assertEquals(expectedColNums.size() * expectedErrRowNums.size(), numErrs);
    }


    private static final ArrayList<DataColumnType> gqbDataColTypes = new ArrayList<DataColumnType>(Arrays.asList(
            DashboardServerUtils.DATASET_NAME.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.YEAR.duplicate(),
            DashboardServerUtils.MONTH_OF_YEAR.duplicate(),
            DashboardServerUtils.DAY_OF_MONTH.duplicate(),
            DashboardServerUtils.HOUR_OF_DAY.duplicate(),
            DashboardServerUtils.MINUTE_OF_HOUR.duplicate(),
            DashboardServerUtils.SECOND_OF_MINUTE.duplicate(),
            DashboardServerUtils.LONGITUDE.duplicate(),
            DashboardServerUtils.LATITUDE.duplicate(),
            DashboardServerUtils.SAMPLE_DEPTH.duplicate(),

            SocatTypes.SALINITY.duplicate(),
            SocatTypes.SST.duplicate(),
            SocatTypes.TEQU.duplicate(),
            SocatTypes.PATM.duplicate(),
            SocatTypes.PEQU.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),

            SocatTypes.XCO2_WATER_TEQU_DRY.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            SocatTypes.FCO2_WATER_SST_WET.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.OTHER.duplicate()
    ));

    private static final ArrayList<String> gqbUserColumnNames = new ArrayList<String>(Arrays.asList(
            "cruise name", "DOI", "ID", "year", "month", "day", "hour", "min", "sec", "lon", "lat", "depth",
            "sal", "sst", "Tequi", "Patm", "Pequi", "WOA sal", "NCEP SLP", "ETOPO2 depth", "distToLand", "gvco2",
            "xco2teq", "xco2sst", "pco2teq", "pco2sst", "fco2teq", "fco2sst", "fco2rec", "fco2recsrc", "WOCEfco2"
    ));

    private static final String[] goodDataTSVStrings = {
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t15\t00.00\t342.76300\t32.48200\t5.\t" +
                    "36.550\t20.440\t21.020\t1017.860\t1017.600\t36.749\t1018.500\t3553.\t663.\t371.151\t" +
                    "396.070\tNaN\tNaN\tNaN\tNaN\t378.410\t377.552\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t20\t00.00\t342.74500\t32.47400\t5.\t" +
                    "36.560\t20.230\t20.760\t1017.480\t1017.430\t36.749\t1018.500\t3553.\t663.\t371.151\t" +
                    "384.430\tNaN\tNaN\tNaN\tNaN\t368.120\t367.309\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t24\t00.0\t342.72900\t32.46600\t5.\t" +
                    "36.590\t19.960\t20.610\t1017.440\t1017.490\t36.749\t1018.500\t3803.\t663.\t371.151\t" +
                    "378.350\tNaN\tNaN\tNaN\tNaN\t360.740\t359.769\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t29\t00.0\t342.71201\t32.45900\t5.\t" +
                    "36.530\t20.060\t20.410\t1017.460\t1017.400\t36.749\t1018.500\t3803.\t664.\t371.151\t" +
                    "374.490\tNaN\tNaN\tNaN\tNaN\t361.200\t360.716\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t45\t00.\t342.64700\t32.43000\t5.\t" +
                    "36.550\t20.690\t20.900\t1017.440\t1017.450\t36.749\t1018.500\t3929.\t665.\t371.151\t" +
                    "375.110\tNaN\tNaN\tNaN\tNaN\t363.510\t363.222\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t50\t00.\t342.62900\t32.42300\t5.\t" +
                    "36.550\t20.770\t21.010\t1017.630\t1017.460\t36.749\t1018.500\t3947.\t666.\t371.151\t" +
                    "376.850\tNaN\tNaN\tNaN\tNaN\t364.720\t364.389\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t54\t00\t342.60999\t32.41500\t5.\t" +
                    "36.550\t20.740\t21.030\t1017.440\t1017.530\t36.749\t1018.500\t3947.\t666.\t371.150\t" +
                    "377.920\tNaN\tNaN\tNaN\tNaN\t365.180\t364.666\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t59\t00\t342.59299\t32.40800\t5.\t" +
                    "36.540\t20.770\t20.970\t1017.510\t1017.520\t36.749\t1018.500\t3987.\t667.\t371.150\t" +
                    "377.520\tNaN\tNaN\tNaN\tNaN\t366.070\t365.698\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t15\t00\t342.52301\t32.38300\t5.\t" +
                    "36.600\t21.250\t21.420\t1017.440\t1017.480\t36.749\t1018.500\t4087.\t669.\t371.150\t" +
                    "379.350\tNaN\tNaN\tNaN\tNaN\t367.980\t367.678\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t20\t00\t342.50400\t32.37600\t5.\t" +
                    "36.610\t21.590\t21.670\t1017.540\t1017.470\t36.749\t1018.500\t4087.\t669.\t371.150\t" +
                    "381.540\tNaN\tNaN\tNaN\tNaN\t371.160\t371.067\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t24\t00\t342.48599\t32.37000\t5.\t" +
                    "36.650\t21.790\t21.880\t1017.500\t1017.440\t36.749\t1018.500\t4139.\t670.\t371.150\t" +
                    "385.330\tNaN\tNaN\tNaN\tNaN\t374.620\t374.463\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t29\t00\t342.46899\t32.36300\t5.\t" +
                    "36.650\t21.980\t22.070\t1017.610\t1017.480\t36.749\t1018.500\t4199.\t671.\t371.150\t" +
                    "388.780\tNaN\tNaN\tNaN\tNaN\t377.840\t377.719\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t45\t00\t342.39999\t32.33600\t5.\t" +
                    "36.710\t22.270\t22.540\t1017.680\t1017.600\t36.749\t1018.500\t4223.\t673.\t371.150\t" +
                    "398.980\tNaN\tNaN\tNaN\tNaN\t384.990\t384.450\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t50\t00\t342.38300\t32.33000\t5.\t" +
                    "36.700\t22.400\t22.540\t1017.730\t1017.630\t36.749\t1018.500\t4257.\t673.\t371.150\t" +
                    "399.330\tNaN\tNaN\tNaN\tNaN\t387.220\t386.921\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t54\t00\t342.36401\t32.32200\t5.\t" +
                    "36.710\t22.660\t22.740\t1017.700\t1017.690\t36.749\t1018.500\t4273.\t674.\t371.150\t" +
                    "400.970\tNaN\tNaN\tNaN\tNaN\t389.570\t389.396\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t59\t00\t342.34601\t32.31500\t5.\t" +
                    "36.760\t22.890\t22.970\t1017.750\t1017.490\t36.749\t1018.500\t4273.\t674.\t371.150\t" +
                    "403.580\tNaN\tNaN\tNaN\tNaN\t391.830\t391.705\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t15\t00\t342.27802\t32.28800\t5.\t" +
                    "36.820\t23.140\t23.310\t1017.610\t1017.560\t36.749\t1018.500\t4355.\t677.\t371.150\t" +
                    "410.450\tNaN\tNaN\tNaN\tNaN\t397.060\t396.664\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t20\t00\t342.25900\t32.28000\t5.\t" +
                    "36.830\t23.130\t23.310\t1017.630\t1017.700\t36.749\t1018.500\t4365.\t677.\t371.150\t" +
                    "411.160\tNaN\tNaN\tNaN\tNaN\t397.600\t397.238\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t24\t00\t342.24100\t32.27300\t5.\t" +
                    "36.830\t23.140\t23.320\t1017.720\t1017.570\t36.749\t1018.500\t4365.\t678.\t371.150\t" +
                    "411.400\tNaN\tNaN\tNaN\tNaN\t397.750\t397.411\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t29\t00\t342.22400\t32.26600\t5.\t" +
                    "36.830\t23.160\t23.340\t1017.630\t1017.540\t36.749\t1018.500\t4389.\t678.\t371.150\t" +
                    "411.730\tNaN\tNaN\tNaN\tNaN\t398.030\t397.705\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t45\t00\t342.15601\t32.24300\t5.\t" +
                    "36.860\t23.270\t23.460\t1017.650\t1017.590\t36.749\t1018.500\t4411.\t681.\t371.150\t" +
                    "413.210\tNaN\tNaN\tNaN\tNaN\t399.270\t398.906\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t50\t00\t342.13800\t32.23900\t5.\t" +
                    "36.870\t23.280\t23.470\t1017.730\t1017.640\t36.749\t1018.500\t4411.\t682.\t371.150\t" +
                    "413.500\tNaN\tNaN\tNaN\tNaN\t399.660\t399.199\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t54\t00\t342.12000\t32.23300\t5.\t" +
                    "36.870\t23.270\t23.460\t1017.840\t1017.780\t36.749\t1018.500\t4453.\t683.\t371.150\t" +
                    "413.630\tNaN\tNaN\tNaN\tNaN\t399.800\t399.387\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t59\t00\t342.10300\t32.22500\t5.\t" +
                    "36.860\t23.250\t23.450\t1017.880\t1017.880\t36.749\t1018.500\t4453.\t683.\t371.150\t" +
                    "413.650\tNaN\tNaN\tNaN\tNaN\t399.690\t399.285\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t19\t15\t00\t342.03900\t32.19500\t5.\t" +
                    "36.830\t23.140\t23.350\t1017.970\t1017.920\t36.749\t1018.500\t4503.\t685.\t371.149\t" +
                    "412.220\tNaN\tNaN\tNaN\tNaN\t398.230\t397.819\t1\t2",
    };

    private static final String[] questDataTSVStrings = {
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t15\t00\t342.76300\t32.48200\t5.\t" +
                    "36.550\t20.440\t21.020\t1017.860\t1017.600\t36.749\t1018.500\t3553.\t663.\t371.151\t" +
                    "396.070\tNaN\tNaN\tNaN\tNaN\t378.410\t377.552\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t20\t00\t342.74500\t32.47400\t5.\t" +
                    "36.560\t20.230\t20.760\t1017.480\t1017.430\t36.749\t1018.500\t3553.\t663.\t371.151\t" +
                    "384.430\tNaN\tNaN\tNaN\tNaN\t368.120\t367.309\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t24\t00\t342.72900\t32.46600\t5.\t" +
                    "36.590\t19.960\t20.610\t1017.440\t1017.490\t36.749\t1018.500\t3803.\t663.\t371.151\t" +
                    "78.350\tNaN\tNaN\tNaN\tNaN\t360.740\t359.769\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t29\t00\t342.71201\t32.45900\t5.\t" +
                    "36.530\t20.060\t20.410\t1017.460\t1017.400\t36.749\t1018.500\t3803.\t664.\t371.151\t" +
                    "74.490\tNaN\tNaN\tNaN\tNaN\t361.200\t360.716\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t45\t00\t342.64700\t32.43000\t5.\t" +
                    "36.550\t20.690\t20.900\t1017.440\t1017.450\t36.749\t1018.500\t3929.\t665.\t371.151\t" +
                    "75.110\tNaN\tNaN\tNaN\tNaN\t363.510\t363.222\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t50\t00\t342.62900\t32.42300\t5.\t" +
                    "36.550\t20.770\t21.010\t1017.630\t1017.460\t36.749\t1018.500\t3947.\t666.\t371.151\t" +
                    "76.850\tNaN\tNaN\tNaN\tNaN\t364.720\t364.389\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t54\t00\t342.60999\t32.41500\t5.\t" +
                    "36.550\t20.740\t21.030\t1017.440\t1017.530\t36.749\t1018.500\t3947.\t666.\t371.150\t" +
                    "77.920\tNaN\tNaN\tNaN\tNaN\t365.180\t364.666\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t59\t00\t342.59299\t32.40800\t5.\t" +
                    "36.540\t20.770\t20.970\t1017.510\t1017.520\t36.749\t1018.500\t3987.\t667.\t371.150\t" +
                    "77.520\tNaN\tNaN\tNaN\tNaN\t366.070\t365.698\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t15\t00\t342.52301\t32.38300\t5.\t" +
                    "36.600\t21.250\t21.420\t1017.440\t1017.480\t36.749\t1018.500\t4087.\t669.\t371.150\t" +
                    "79.350\tNaN\tNaN\tNaN\tNaN\t367.980\t367.678\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t20\t00\t342.50400\t32.37600\t5.\t" +
                    "36.610\t21.590\t21.670\t1017.540\t1017.470\t36.749\t1018.500\t4087.\t669.\t371.150\t" +
                    "381.540\tNaN\tNaN\tNaN\tNaN\t371.160\t371.067\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t24\t00\t342.48599\t32.37000\t5.\t" +
                    "36.650\t21.790\t21.880\t1017.500\t1017.440\t36.749\t1018.500\t4139.\t670.\t371.150\t" +
                    "385.330\tNaN\tNaN\tNaN\tNaN\t374.620\t374.463\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t29\t00\t342.46899\t32.36300\t5.\t" +
                    "36.650\t21.980\t22.070\t1017.610\t1017.480\t36.749\t1018.500\t4199.\t671.\t371.150\t" +
                    "388.780\tNaN\tNaN\tNaN\tNaN\t377.840\t377.719\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t45\t00\t342.39999\t32.33600\t5.\t" +
                    "36.710\t22.270\t22.540\t1017.680\t1017.600\t36.749\t1018.500\t4223.\t673.\t371.150\t" +
                    "398.980\tNaN\tNaN\tNaN\tNaN\t384.990\t384.450\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t50\t00\t342.38300\t32.33000\t5.\t" +
                    "36.700\t22.400\t22.540\t1017.730\t1017.630\t36.749\t1018.500\t4257.\t673.\t371.150\t" +
                    "399.330\tNaN\tNaN\tNaN\tNaN\t387.220\t386.921\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t54\t00\t342.36401\t32.32200\t5.\t" +
                    "36.710\t22.660\t22.740\t1017.700\t1017.690\t36.749\t1018.500\t4273.\t674.\t371.150\t" +
                    "400.970\tNaN\tNaN\tNaN\tNaN\t389.570\t389.396\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t59\t00\t342.34601\t32.31500\t5.\t" +
                    "36.760\t22.890\t22.970\t1017.750\t1017.490\t36.749\t1018.500\t4273.\t674.\t371.150\t" +
                    "403.580\tNaN\tNaN\tNaN\tNaN\t391.830\t391.705\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t15\t00\t342.27802\t32.28800\t5.\t" +
                    "36.820\t23.140\t23.310\t1017.610\t1017.560\t36.749\t1018.500\t4355.\t677.\t371.150\t" +
                    "410.450\tNaN\tNaN\tNaN\tNaN\t397.060\t396.664\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t20\t00\t342.25900\t32.28000\t5.\t" +
                    "36.830\t23.130\t23.310\t1017.630\t1017.700\t36.749\t1018.500\t4365.\t677.\t371.150\t" +
                    "411.160\tNaN\tNaN\tNaN\tNaN\t397.600\t397.238\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t24\t00\t342.24100\t32.27300\t5.\t" +
                    "36.830\t23.140\t23.320\t1017.720\t1017.570\t36.749\t1018.500\t4365.\t678.\t371.150\t" +
                    "411.400\tNaN\tNaN\tNaN\tNaN\t397.750\t397.411\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t29\t00\t342.22400\t32.26600\t5.\t" +
                    "36.830\t23.160\t23.340\t1017.630\t1017.540\t36.749\t1018.500\t4389.\t678.\t371.150\t" +
                    "411.730\tNaN\tNaN\tNaN\tNaN\t398.030\t397.705\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t45\t00\t342.15601\t32.24300\t5.\t" +
                    "36.860\t23.270\t23.460\t1017.650\t1017.590\t36.749\t1018.500\t4411.\t681.\t371.150\t" +
                    "413.210\tNaN\tNaN\tNaN\tNaN\t399.270\t398.906\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t50\t00\t342.13800\t32.23900\t5.\t" +
                    "36.870\t23.280\t23.470\t1017.730\t1017.640\t36.749\t1018.500\t4411.\t682.\t371.150\t" +
                    "413.500\tNaN\tNaN\tNaN\tNaN\t399.660\t399.199\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t54\t00\t342.12000\t32.23300\t5.\t" +
                    "36.870\t23.270\t23.460\t1017.840\t1017.780\t36.749\t1018.500\t4453.\t683.\t371.150\t" +
                    "413.630\tNaN\tNaN\tNaN\tNaN\t399.800\t399.387\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t59\t00\t342.10300\t32.22500\t5.\t" +
                    "36.860\t23.250\t23.450\t1017.880\t1017.880\t36.749\t1018.500\t4453.\t683.\t371.150\t" +
                    "413.650\tNaN\tNaN\tNaN\tNaN\t399.690\t399.285\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t19\t15\t00\t342.03900\t32.19500\t5.\t" +
                    "36.830\t23.140\t23.350\t1017.970\t1017.920\t36.749\t1018.500\t4503.\t685.\t371.149\t" +
                    "412.220\tNaN\tNaN\tNaN\tNaN\t398.230\t397.819\t1\t2",
    };

    private static final String[] badDataTSVStrings = {
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t15\t00\t342.76300\t32.48200\t5.\t" +
                    "36.550\t20.440\t21.020\t1017.860\t1017.600\t36.749\t1018.500\t3553.\t663.\t371.151\t" +
                    "396.070\tNaN\tNaN\tNaN\tNaN\t378.410\t377.552\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t20\t00\t342.74500\t32.47400\t5.\t" +
                    "36.560\t20.230\t20.760\t1017.480\t1017.430\t36.749\t1018.500\t3553.\t663.\t371.151\t" +
                    "384.430\tNaN\tNaN\tNaN\tNaN\t368.120\t367.309\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t02\t31\t16\t24\t00\t342.72900\t132.46600\t5.\t" +
                    "36.590\t19.960\t20.610\t1017.440\t1017.490\t36.749\t1018.500\t3803.\t663.\t371.151\t" +
                    "378.350\tNaN\tNaN\tNaN\tNaN\t360.740\t359.769\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t29\t00\t342.71201\t132.45900\t5.\t" +
                    "36.530\t20.060\t20.410\t1017.460\t1017.400\t36.749\t1018.500\t3803.\t664.\t371.151\t" +
                    "374.490\tNaN\tNaN\tNaN\tNaN\t361.200\t360.716\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t45\t00\t342.64700\t132.43000\t5.\t" +
                    "36.550\t20.690\t20.900\t1017.440\t1017.450\t36.749\t1018.500\t3929.\t665.\t371.151\t" +
                    "375.110\tNaN\tNaN\tNaN\tNaN\t363.510\t363.222\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t50\t00\t342.62900\t132.42300\t5.\t" +
                    "36.550\t20.770\t21.010\t1017.630\t1017.460\t36.749\t1018.500\t3947.\t666.\t371.151\t" +
                    "376.850\tNaN\tNaN\tNaN\tNaN\t364.720\t364.389\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t54\t00\t342.60999\t132.41500\t5.\t" +
                    "36.550\t20.740\t21.030\t1017.440\t1017.530\t36.749\t1018.500\t3947.\t666.\t371.150\t" +
                    "377.920\tNaN\tNaN\tNaN\tNaN\t365.180\t364.666\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t59\t00\t342.59299\t132.40800\t5.\t" +
                    "36.540\t20.770\t20.970\t1017.510\t1017.520\t36.749\t1018.500\t3987.\t667.\t371.150\t" +
                    "377.520\tNaN\tNaN\tNaN\tNaN\t366.070\t365.698\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t15\t00\t342.52301\t132.38300\t5.\t" +
                    "36.600\t21.250\t21.420\t1017.440\t1017.480\t36.749\t1018.500\t4087.\t669.\t371.150\t" +
                    "379.350\tNaN\tNaN\tNaN\tNaN\t367.980\t367.678\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t20\t00\t342.50400\t32.37600\t5.\t" +
                    "36.610\t21.590\t21.670\t1017.540\t1017.470\t36.749\t1018.500\t4087.\t669.\t371.150\t" +
                    "381.540\tNaN\tNaN\tNaN\tNaN\t371.160\t371.067\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t24\t00\t342.48599\t32.37000\t5.\t" +
                    "36.650\t21.790\t21.880\t1017.500\t1017.440\t36.749\t1018.500\t4139.\t670.\t371.150\t" +
                    "385.330\tNaN\tNaN\tNaN\tNaN\t374.620\t374.463\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t29\t00\t342.46899\t32.36300\t5.\t" +
                    "36.650\t21.980\t22.070\t1017.610\t1017.480\t36.749\t1018.500\t4199.\t671.\t371.150\t" +
                    "388.780\tNaN\tNaN\tNaN\tNaN\t377.840\t377.719\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t45\t00\t342.39999\t32.33600\t5.\t" +
                    "36.710\t22.270\t22.540\t1017.680\t1017.600\t36.749\t1018.500\t4223.\t673.\t371.150\t" +
                    "398.980\tNaN\tNaN\tNaN\tNaN\t384.990\t384.450\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t50\t00\t342.38300\t32.33000\t5.\t" +
                    "36.700\t22.400\t22.540\t1017.730\t1017.630\t36.749\t1018.500\t4257.\t673.\t371.150\t" +
                    "399.330\tNaN\tNaN\tNaN\tNaN\t387.220\t386.921\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t54\t00\t342.36401\t32.32200\t5.\t" +
                    "36.710\t22.660\t22.740\t1017.700\t1017.690\t36.749\t1018.500\t4273.\t674.\t371.150\t" +
                    "400.970\tNaN\tNaN\tNaN\tNaN\t389.570\t389.396\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t59\t00\t342.34601\t32.31500\t5.\t" +
                    "36.760\t22.890\t22.970\t1017.750\t1017.490\t36.749\t1018.500\t4273.\t674.\t371.150\t" +
                    "403.580\tNaN\tNaN\tNaN\tNaN\t391.830\t391.705\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t15\t00\t342.27802\t32.28800\t5.\t" +
                    "36.820\t23.140\t23.310\t1017.610\t1017.560\t36.749\t1018.500\t4355.\t677.\t371.150\t" +
                    "410.450\tNaN\tNaN\tNaN\tNaN\t397.060\t396.664\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t20\t00\t342.25900\t32.28000\t5.\t" +
                    "36.830\t23.130\t23.310\t1017.630\t1017.700\t36.749\t1018.500\t4365.\t677.\t371.150\t" +
                    "411.160\tNaN\tNaN\tNaN\tNaN\t397.600\t397.238\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t24\t00\t342.24100\t32.27300\t5.\t" +
                    "36.830\t23.140\t23.320\t1017.720\t1017.570\t36.749\t1018.500\t4365.\t678.\t371.150\t" +
                    "411.400\tNaN\tNaN\tNaN\tNaN\t397.750\t397.411\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t29\t00\t342.22400\t32.26600\t5.\t" +
                    "36.830\t23.160\t23.340\t1017.630\t1017.540\t36.749\t1018.500\t4389.\t678.\t371.150\t" +
                    "411.730\tNaN\tNaN\tNaN\tNaN\t398.030\t397.705\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t45\t00\t342.15601\t32.24300\t5.\t" +
                    "36.860\t23.270\t23.460\t1017.650\t1017.590\t36.749\t1018.500\t4411.\t681.\t371.150\t" +
                    "413.210\tNaN\tNaN\tNaN\tNaN\t399.270\t398.906\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t50\t00\t342.13800\t32.23900\t5.\t" +
                    "36.870\t23.280\t23.470\t1017.730\t1017.640\t36.749\t1018.500\t4411.\t682.\t371.150\t" +
                    "413.500\tNaN\tNaN\tNaN\tNaN\t399.660\t399.199\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t54\t00\t342.12000\t32.23300\t5.\t" +
                    "36.870\t23.270\t23.460\t1017.840\t1017.780\t36.749\t1018.500\t4453.\t683.\t371.150\t" +
                    "413.630\tNaN\tNaN\tNaN\tNaN\t399.800\t399.387\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t59\t00\t342.10300\t32.22500\t5.\t" +
                    "36.860\t23.250\t23.450\t1017.880\t1017.880\t36.749\t1018.500\t4453.\t683.\t371.150\t" +
                    "413.650\tNaN\tNaN\tNaN\tNaN\t399.690\t399.285\t1\t2",
            "33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t19\t15\t00\t2.03900\t32.19500\t5.\t" +
                    "36.830\t23.140\t23.350\t1017.970\t1017.920\t36.749\t1018.500\t4503.\t685.\t371.149\t" +
                    "412.220\tNaN\tNaN\tNaN\tNaN\t398.230\t397.819\t1\t2",
    };

    private static final ArrayList<DataColumnType> dupsDataColTypes = new ArrayList<DataColumnType>(Arrays.asList(
            DashboardServerUtils.DATASET_NAME.duplicate(),
            DashboardServerUtils.DATE.duplicate(),
            DashboardServerUtils.TIME_OF_DAY.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.LATITUDE.duplicate(),
            DashboardServerUtils.LONGITUDE.duplicate(),
            SocatTypes.PCO2_WATER_SST_WET.duplicate(),
            SocatTypes.SST.duplicate(),
            SocatTypes.SALINITY.duplicate(),
            DashboardServerUtils.SAMPLE_DEPTH.duplicate()
    ));

    static {
        dupsDataColTypes.get(1).setSelectedUnit("mm-dd-yyyy");
    }

    private static final ArrayList<String> dupsUserColumnNames = new ArrayList<String>(Arrays.asList(
            "cruise name", "date (mm/dd/yyyy)", "time (hh:mm)", "date_local", "time_local",
            "lat", "lon", "pco2_sst", "sst", "sal", "depth"
    ));

    private static final String[] dupsDataTSVStrings = {
            "12BHM03\t5/8/2012\t14:26\t5/8/2012\t10:26 AM\t27.61918\t-82.66427\t625.8\t26.16\t34.17\t5",
            "12BHM03\t5/8/2012\t14:26\t5/8/2012\t10:26 AM\t27.61918\t-82.66427\t487.2\t26.03\t34.17\t5",
            "12BHM03\t5/8/2012\t14:29\t5/8/2012\t10:29 AM\t27.61768\t-82.67247\t417.7\t26.05\t34.17\t5",
            "12BHM03\t5/8/2012\t14:34\t5/8/2012\t10:34 AM\t27.61513\t-82.68602\t397.8\t26\t34.17\t5",
            "12BHM03\t5/8/2012\t14:45\t5/8/2012\t10:45 AM\t27.60948\t-82.71577\t416.5\t25.92\t34.18\t5",
            "12BHM03\t5/8/2012\t15:00\t5/8/2012\t11:00 AM\t27.60738\t-82.75163\t418.2\t25.84\t34.2\t5",
            "12BHM03\t5/8/2012\t15:15\t5/8/2012\t11:15 AM\t27.61103\t-82.7913\t395.8\t25.76\t35.41\t5",
            "12BHM03\t5/8/2012\t15:30\t5/8/2012\t11:30 AM\t27.6073\t-82.83165\t483.4\t25.63\t35.6\t5",
            "12BHM03\t5/8/2012\t15:45\t5/8/2012\t11:45 AM\t27.60917\t-82.87022\t464.3\t25.68\t35.83\t5",
            "12BHM03\t5/8/2012\t16:00\t5/8/2012\t12:00 PM\t27.61655\t-82.90633\t455.6\t25.68\t35.87\t5",
            "12BHM03\t5/8/2012\t16:15\t5/8/2012\t12:15 PM\t27.62488\t-82.94678\t445.7\t25.66\t35.95\t5",
            "12BHM03\t5/8/2012\t16:30\t5/8/2012\t12:30 PM\t27.63325\t-82.98722\t434.1\t25.71\t36.08\t5",
            "12BHM03\t5/8/2012\t16:32\t5/8/2012\t12:32 PM\t27.63428\t-82.99258\t429.2\t25.73\t36.13\t5",
            "12BHM03\t5/8/2012\t16:32\t5/8/2012\t12:32 PM\t27.63428\t-82.99258\t428\t25.71\t36.13\t5",
            "12BHM03\t5/8/2012\t16:32\t5/8/2012\t12:32 PM\t27.63428\t-82.99258\t428.7\t25.68\t36.13\t5",
            "12BHM03\t5/8/2012\t16:32\t5/8/2012\t12:32 PM\t27.63428\t-82.99258\t428.5\t25.66\t36.13\t5",
            "12BHM03\t5/8/2012\t16:32\t5/8/2012\t12:32 PM\t27.63428\t-82.99258\t428.8\t25.66\t36.13\t5",
            "12BHM03\t5/8/2012\t16:32\t5/8/2012\t12:32 PM\t27.63428\t-82.99258\t429.1\t25.71\t36.13\t5",
            "12BHM03\t5/8/2012\t16:32\t5/8/2012\t12:32 PM\t27.63428\t-82.99258\t425.6\t25.5\t36.13\t5",
            "12BHM03\t5/8/2012\t16:33\t5/8/2012\t12:33 PM\t27.63482\t-82.99527\t425\t25.55\t36.11\t5",
            "12BHM03\t5/8/2012\t16:37\t5/8/2012\t12:37 PM\t27.63715\t-83.00592\t422.9\t25.58\t36.14\t5",
            "12BHM03\t5/8/2012\t16:39\t5/8/2012\t12:39 PM\t27.63817\t-83.0113\t421.1\t25.44\t36.17\t5",
            "12BHM03\t5/8/2012\t16:44\t5/8/2012\t12:44 PM\t27.64087\t-83.02473\t423.4\t25.55\t36.15\t5",
            "12BHM03\t5/8/2012\t16:55\t5/8/2012\t12:55 PM\t27.64567\t-83.05455\t427.5\t25.76\t36.14\t5",
            "12BHM03\t5/8/2012\t17:10\t5/8/2012\t1:10 PM\t27.65548\t-83.09485\t425.8\t25.89\t36.19\t5",
            "12BHM03\t5/8/2012\t17:25\t5/8/2012\t1:25 PM\t27.66382\t-83.13585\t415.2\t25.36\t36.2\t5",
            "12BHM03\t5/8/2012\t17:40\t5/8/2012\t1:40 PM\t27.6723\t-83.17688\t419\t25.55\t36.22\t5",
            "12BHM03\t5/8/2012\t17:55\t5/8/2012\t1:55 PM\t27.68095\t-83.21818\t432.7\t26.64\t36.24\t5",
            "12BHM03\t5/8/2012\t18:10\t5/8/2012\t2:10 PM\t27.68953\t-83.25972\t413.8\t25.26\t36.28\t5",
            "12BHM03\t5/8/2012\t18:25\t5/8/2012\t2:25 PM\t27.69895\t-83.30092\t415.2\t25.52\t36.3\t5",
            "12BHM03\t5/8/2012\t18:40\t5/8/2012\t2:40 PM\t27.7069\t-83.34292\t416\t25.55\t36.35\t5",
            "12BHM03\t5/8/2012\t18:55\t5/8/2012\t2:55 PM\t27.71528\t-83.38512\t422.8\t25.92\t36.38\t5",
            "12BHM03\t5/8/2012\t19:10\t5/8/2012\t3:10 PM\t27.72407\t-83.4273\t430.1\t26.4\t36.39\t5",
            "12BHM03\t5/8/2012\t19:25\t5/8/2012\t3:25 PM\t27.73152\t-83.46982\t440.4\t27.05\t36.42\t5",
            "12BHM03\t5/8/2012\t19:40\t5/8/2012\t3:40 PM\t27.73962\t-83.51213\t444\t26.99\t36.44\t5",
            "12BHM03\t5/8/2012\t19:55\t5/8/2012\t3:55 PM\t27.75022\t-83.55462\t422.8\t25.63\t36.42\t5",
            "12BHM03\t5/8/2012\t20:10\t5/8/2012\t4:10 PM\t27.7591\t-83.59767\t435.7\t26.99\t36.39\t5",
            "12BHM03\t5/8/2012\t20:25\t5/8/2012\t4:25 PM\t27.76807\t-83.6408\t426.9\t26.56\t36.39\t5",
            "12BHM03\t5/8/2012\t20:40\t5/8/2012\t4:40 PM\t27.77685\t-83.68385\t423\t26.48\t36.4\t5",
            "12BHM03\t5/8/2012\t20:55\t5/8/2012\t4:55 PM\t27.78577\t-83.72687\t414\t25.84\t36.36\t5",
            "12BHM03\t5/8/2012\t21:10\t5/8/2012\t5:10 PM\t27.79462\t-83.76977\t421.8\t26.13\t36.4\t5",
            "12BHM03\t5/8/2012\t21:25\t5/8/2012\t5:25 PM\t27.80345\t-83.81303\t425\t26.51\t36.41\t5",
            "12BHM03\t5/8/2012\t21:40\t5/8/2012\t5:40 PM\t27.81235\t-83.85622\t394.4\t26.08\t36.44\t5",
            "12BHM03\t5/8/2012\t21:55\t5/8/2012\t5:55 PM\t27.82122\t-83.89928\t418.9\t26.86\t36.27\t5",
            "12BHM03\t5/8/2012\t22:10\t5/8/2012\t6:10 PM\t27.83007\t-83.94212\t412.4\t26.35\t36.17\t5",
            "12BHM03\t5/8/2012\t22:25\t5/8/2012\t6:25 PM\t27.83887\t-83.9847\t416.1\t26.67\t36.15\t5",
            "12BHM03\t5/8/2012\t22:40\t5/8/2012\t6:40 PM\t27.84628\t-84.02023\t409.3\t25.97\t36.2\t5",
            "12BHM03\t5/8/2012\t22:55\t5/8/2012\t6:55 PM\t27.85502\t-84.06257\t409.1\t25.92\t36.16\t5",
            "12BHM03\t5/8/2012\t23:10\t5/8/2012\t7:10 PM\t27.86262\t-84.09953\t412.4\t26.32\t36.1\t5",
            "12BHM03\t5/8/2012\t23:25\t5/8/2012\t7:25 PM\t27.87178\t-84.14398\t408.9\t26.16\t36.14\t5",
            "12BHM03\t5/8/2012\t23:40\t5/8/2012\t7:40 PM\t27.88092\t-84.18862\t409.1\t26.21\t36.08\t5",
            "12BHM03\t5/8/2012\t23:55\t5/8/2012\t7:55 PM\t27.89022\t-84.23358\t405.8\t26.13\t36.08\t5",
            "12BHM03\t5/9/2012\t00:10\t5/8/2012\t8:10 PM\t27.89948\t-84.27862\t411.3\t26.35\t36.11\t5",
            "12BHM03\t5/9/2012\t00:25\t5/8/2012\t8:25 PM\t27.90867\t-84.3234\t410.7\t26.21\t36.16\t5",
            "12BHM03\t5/9/2012\t00:40\t5/8/2012\t8:40 PM\t27.9175\t-84.3666\t408.5\t26.16\t36.13\t5",
            "12BHM03\t5/9/2012\t00:55\t5/8/2012\t8:55 PM\t27.92613\t-84.40855\t410.7\t26.37\t36.11\t5",
            "12BHM03\t5/9/2012\t01:10\t5/8/2012\t9:10 PM\t27.93487\t-84.45065\t403.4\t25.95\t36.11\t5",
            "12BHM03\t5/9/2012\t01:25\t5/8/2012\t9:25 PM\t27.94347\t-84.49228\t403.1\t25.97\t36.12\t5",
            "12BHM03\t5/9/2012\t01:40\t5/8/2012\t9:40 PM\t27.95193\t-84.53352\t406\t26.16\t36.27\t5",
            "12BHM03\t5/9/2012\t01:55\t5/8/2012\t9:55 PM\t27.9606\t-84.57552\t407.4\t26.29\t36.26\t5",
            "12BHM03\t5/9/2012\t02:10\t5/8/2012\t10:10 PM\t27.96913\t-84.61745\t408.3\t26.56\t36.28\t5",
            "12BHM03\t5/9/2012\t02:25\t5/8/2012\t10:25 PM\t27.97775\t-84.65895\t403.8\t26.32\t36.26\t5",
            "12BHM03\t5/9/2012\t02:40\t5/8/2012\t10:40 PM\t27.98613\t-84.69992\t404.3\t26.53\t36.23\t5",
            "12BHM03\t5/9/2012\t02:55\t5/8/2012\t10:55 PM\t27.99478\t-84.7416\t407.1\t26.56\t36.22\t5",
            "12BHM03\t5/9/2012\t03:10\t5/8/2012\t11:10 PM\t28.00282\t-84.7814\t403.4\t26.32\t36.19\t5",
            "12BHM03\t5/9/2012\t03:25\t5/8/2012\t11:25 PM\t28.011\t-84.8206\t402.5\t26.24\t36.19\t5",
            "12BHM03\t5/9/2012\t03:40\t5/8/2012\t11:40 PM\t28.01897\t-84.85983\t403.4\t26.45\t36.21\t5",
            "12BHM03\t5/9/2012\t03:55\t5/8/2012\t11:55 PM\t28.02693\t-84.89868\t403.7\t26.43\t36.2\t5",
            "12BHM03\t5/9/2012\t04:10\t5/9/2012\t12:10 AM\t28.03488\t-84.937\t403.5\t26.37\t36.15\t5",
            "12BHM03\t5/9/2012\t04:25\t5/9/2012\t12:25 AM\t28.04253\t-84.97502\t403\t26.32\t36.17\t5",
            "12BHM03\t5/9/2012\t04:40\t5/9/2012\t12:40 AM\t28.04958\t-85.01295\t401.5\t26.24\t36.17\t5",
            "12BHM03\t5/9/2012\t04:55\t5/9/2012\t12:55 AM\t28.05653\t-85.05185\t401.5\t26.35\t36.2\t5",
            "12BHM03\t5/9/2012\t05:10\t5/9/2012\t1:10 AM\t28.06392\t-85.09072\t399.7\t26.27\t36.26\t5",
            "12BHM03\t5/9/2012\t05:25\t5/9/2012\t1:25 AM\t28.072\t-85.12967\t398.4\t26.11\t36.32\t5",
            "12BHM03\t5/9/2012\t05:40\t5/9/2012\t1:40 AM\t28.08042\t-85.16915\t397\t26.03\t36.28\t5",
            "12BHM03\t5/9/2012\t05:55\t5/9/2012\t1:55 AM\t28.08867\t-85.20888\t398.9\t26.11\t36.24\t5",
            "12BHM03\t5/9/2012\t06:10\t5/9/2012\t2:10 AM\t28.09773\t-85.24822\t398\t25.95\t36.26\t5",
            "12BHM03\t5/9/2012\t06:25\t5/9/2012\t2:25 AM\t28.10715\t-85.28817\t398.3\t25.95\t36.27\t5",
            "12BHM03\t5/9/2012\t06:40\t5/9/2012\t2:40 AM\t28.11545\t-85.32833\t398.4\t25.97\t36.29\t5",
            "12BHM03\t5/9/2012\t06:55\t5/9/2012\t2:55 AM\t28.12342\t-85.36815\t401.4\t25.47\t36.31\t5",
            "12BHM03\t5/9/2012\t07:10\t5/9/2012\t3:10 AM\t28.13167\t-85.40775\t402.5\t25.55\t36.3\t5",
            "12BHM03\t5/9/2012\t07:25\t5/9/2012\t3:25 AM\t28.1398\t-85.44782\t400.8\t25.29\t36.26\t5",
            "12BHM03\t5/9/2012\t07:40\t5/9/2012\t3:40 AM\t28.14798\t-85.48753\t400.5\t25.26\t36.2\t5",
            "12BHM03\t5/9/2012\t07:55\t5/9/2012\t3:55 AM\t28.1562\t-85.52717\t400.4\t25.31\t36.26\t5",
            "12BHM03\t5/9/2012\t08:10\t5/9/2012\t4:10 AM\t28.1646\t-85.56813\t405.9\t25.68\t36.25\t5",
            "12BHM03\t5/9/2012\t08:25\t5/9/2012\t4:25 AM\t28.17308\t-85.60933\t405.9\t25.68\t36.23\t5",
            "12BHM03\t5/9/2012\t08:40\t5/9/2012\t4:40 AM\t28.18152\t-85.65045\t408.8\t25.92\t36.25\t5",
            "12BHM03\t5/9/2012\t08:55\t5/9/2012\t4:55 AM\t28.18983\t-85.69142\t410.9\t26.08\t36.23\t5",
            "12BHM03\t5/9/2012\t09:10\t5/9/2012\t5:10 AM\t28.19835\t-85.73295\t406.7\t25.76\t36.22\t5",
            "12BHM03\t5/9/2012\t09:25\t5/9/2012\t5:25 AM\t28.20695\t-85.77457\t408.1\t25.81\t36.22\t5",
            "12BHM03\t5/9/2012\t09:40\t5/9/2012\t5:40 AM\t28.21562\t-85.81655\t409.8\t25.89\t36.22\t5",
            "12BHM03\t5/9/2012\t09:55\t5/9/2012\t5:55 AM\t28.22443\t-85.85913\t409.3\t25.92\t36.19\t5",
            "12BHM03\t5/9/2012\t10:10\t5/9/2012\t6:10 AM\t28.23307\t-85.90192\t403\t25.52\t36.12\t5",
            "12BHM03\t5/9/2012\t10:25\t5/9/2012\t6:25 AM\t28.2418\t-85.94457\t410.7\t25.95\t36.09\t5",
            "12BHM03\t5/9/2012\t10:40\t5/9/2012\t6:40 AM\t28.25063\t-85.98707\t407.7\t25.71\t36.1\t5",
            "12BHM03\t5/9/2012\t10:55\t5/9/2012\t6:55 AM\t28.25935\t-86.03008\t405.9\t25.58\t36.12\t5",
            "12BHM03\t5/9/2012\t11:10\t5/9/2012\t7:10 AM\t28.26823\t-86.0732\t408.6\t25.84\t36.14\t5",
            "12BHM03\t5/9/2012\t11:25\t5/9/2012\t7:25 AM\t28.27713\t-86.11643\t411.6\t26.05\t36.16\t5",
            "12BHM03\t5/9/2012\t11:40\t5/9/2012\t7:40 AM\t28.28607\t-86.1599\t411.5\t26.05\t36.2\t5",
            "12BHM03\t5/9/2012\t11:55\t5/9/2012\t7:55 AM\t28.29493\t-86.20335\t411.9\t26.11\t36\t5",
            "12BHM03\t5/9/2012\t12:10\t5/9/2012\t8:10 AM\t28.30373\t-86.24592\t412.6\t25.68\t35.81\t5",
            "12BHM03\t5/9/2012\t12:25\t5/9/2012\t8:25 AM\t28.3126\t-86.28923\t402.9\t25.79\t35.73\t5",
            "12BHM03\t5/9/2012\t12:40\t5/9/2012\t8:40 AM\t28.3212\t-86.3315\t403\t25.76\t35.73\t5",
            "12BHM03\t5/9/2012\t12:55\t5/9/2012\t8:55 AM\t28.32993\t-86.3735\t402\t25.79\t35.76\t5",
            "12BHM03\t5/9/2012\t13:10\t5/9/2012\t9:10 AM\t28.3384\t-86.41483\t401.2\t25.71\t35.77\t5",
            "12BHM03\t5/9/2012\t13:25\t5/9/2012\t9:25 AM\t28.3467\t-86.45577\t406.2\t25.97\t35.73\t5",
            "12BHM03\t5/9/2012\t13:40\t5/9/2012\t9:40 AM\t28.35513\t-86.49642\t410.6\t26.21\t35.71\t5",
            "12BHM03\t5/9/2012\t13:55\t5/9/2012\t9:55 AM\t28.36347\t-86.53763\t409.8\t26.32\t35.82\t5",
            "12BHM03\t5/9/2012\t14:10\t5/9/2012\t10:10 AM\t28.37198\t-86.57925\t403.5\t25.84\t35.98\t5",
            "12BHM03\t5/9/2012\t14:25\t5/9/2012\t10:25 AM\t28.38045\t-86.62015\t399.4\t26.08\t35.83\t5",
            "12BHM03\t5/9/2012\t14:40\t5/9/2012\t10:40 AM\t28.38885\t-86.66103\t401.1\t26.13\t35.89\t5",
            "12BHM03\t5/9/2012\t14:55\t5/9/2012\t10:55 AM\t28.3973\t-86.70253\t399.5\t26.13\t35.8\t5",
            "12BHM03\t5/9/2012\t15:10\t5/9/2012\t11:10 AM\t28.40582\t-86.74405\t400.7\t26.11\t35.59\t5",
            "12BHM03\t5/9/2012\t15:25\t5/9/2012\t11:25 AM\t28.41845\t-86.78308\t401.3\t26.08\t35.64\t5",
            "12BHM03\t5/9/2012\t15:40\t5/9/2012\t11:40 AM\t28.43092\t-86.82265\t400.8\t26.05\t35.74\t5",
            "12BHM03\t5/9/2012\t15:55\t5/9/2012\t11:55 AM\t28.43918\t-86.8654\t402.5\t26.08\t35.67\t5",
            "12BHM03\t5/9/2012\t16:19\t5/9/2012\t12:19 PM\t28.45352\t-86.93425\t450.5\t26.32\t35.55\t5",
            "12BHM03\t5/9/2012\t16:19\t5/9/2012\t12:19 PM\t28.45352\t-86.93425\t435.8\t26.35\t35.55\t5",
            "12BHM03\t5/9/2012\t16:34\t5/9/2012\t12:34 PM\t28.46278\t-86.97747\t434.2\t26.32\t35.64\t5",
            "12BHM03\t5/9/2012\t16:34\t5/9/2012\t12:34 PM\t28.46278\t-86.97747\t432.2\t26.29\t35.64\t5",
            "12BHM03\t5/9/2012\t16:49\t5/9/2012\t12:49 PM\t28.47183\t-87.02058\t423.5\t26.21\t35.65\t5",
            "12BHM03\t5/9/2012\t17:04\t5/9/2012\t1:04 PM\t28.47843\t-87.06495\t419.1\t26.27\t35.59\t5",
            "12BHM03\t5/9/2012\t17:19\t5/9/2012\t1:19 PM\t28.48557\t-87.10973\t416.1\t26.05\t35.55\t5",
            "12BHM03\t5/9/2012\t17:34\t5/9/2012\t1:34 PM\t28.49217\t-87.15338\t415.1\t26\t35.68\t5",
            "12BHM03\t5/9/2012\t17:49\t5/9/2012\t1:49 PM\t28.49693\t-87.19663\t415.8\t26.03\t35.69\t5",
            "12BHM03\t5/9/2012\t18:04\t5/9/2012\t2:04 PM\t28.50335\t-87.23788\t415.6\t26.11\t35.74\t5",
            "12BHM03\t5/9/2012\t18:19\t5/9/2012\t2:19 PM\t28.51295\t-87.27778\t417\t26.24\t35.78\t5",
            "12BHM03\t5/9/2012\t18:34\t5/9/2012\t2:34 PM\t28.52227\t-87.3171\t418.1\t26.35\t35.61\t5",
            "12BHM03\t5/9/2012\t18:49\t5/9/2012\t2:49 PM\t28.5313\t-87.35705\t419\t26.67\t35.73\t5",
            "12BHM03\t5/9/2012\t19:04\t5/9/2012\t3:04 PM\t28.53968\t-87.39763\t431\t26.64\t35.9\t5",
            "12BHM03\t5/9/2012\t19:19\t5/9/2012\t3:19 PM\t28.54792\t-87.43833\t412\t26.59\t35.96\t5",
            "12BHM03\t5/9/2012\t19:34\t5/9/2012\t3:34 PM\t28.55628\t-87.4786\t411.1\t26.67\t36.23\t5",
            "12BHM03\t5/9/2012\t19:49\t5/9/2012\t3:49 PM\t28.56427\t-87.51822\t413.2\t26.53\t36.29\t5",
            "12BHM03\t5/9/2012\t20:04\t5/9/2012\t4:04 PM\t28.56853\t-87.55315\t409.4\t26.21\t36.2\t5",
            "12BHM03\t5/9/2012\t20:10\t5/9/2012\t4:10 PM\t28.5649\t-87.55468\t408.6\t26.21\t36.18\t5",
            "12BHM03\t5/9/2012\t20:10\t5/9/2012\t4:10 PM\t28.5649\t-87.55468\t408.9\t26.16\t36.18\t5",
            "12BHM03\t5/9/2012\t20:10\t5/9/2012\t4:10 PM\t28.5649\t-87.55468\t409.2\t26.16\t36.18\t5",
            "12BHM03\t5/9/2012\t20:10\t5/9/2012\t4:10 PM\t28.5649\t-87.55468\t410.6\t26.13\t36.18\t5",
            "12BHM03\t5/9/2012\t20:10\t5/9/2012\t4:10 PM\t28.5649\t-87.55468\t411.9\t26.11\t36.18\t5",
            "12BHM03\t5/9/2012\t20:10\t5/9/2012\t4:10 PM\t28.5649\t-87.55468\t412.6\t26.08\t36.18\t5",
            "12BHM03\t5/9/2012\t20:10\t5/9/2012\t4:10 PM\t28.5649\t-87.55468\t412.4\t26.13\t36.18\t5",
            "12BHM03\t5/9/2012\t20:10\t5/9/2012\t4:10 PM\t28.5649\t-87.55468\t413.6\t26.13\t36.18\t5",
            "12BHM03\t5/9/2012\t20:12\t5/9/2012\t4:12 PM\t28.56352\t-87.55587\t415.4\t26.11\t36.16\t5",
            "12BHM03\t5/9/2012\t20:19\t5/9/2012\t4:19 PM\t28.55865\t-87.56042\t413.6\t26.16\t36.13\t5",
            "12BHM03\t5/9/2012\t20:24\t5/9/2012\t4:24 PM\t28.5555\t-87.56387\t413.3\t26.03\t36.07\t5",
            "12BHM03\t5/9/2012\t20:35\t5/9/2012\t4:35 PM\t28.54958\t-87.57165\t412.7\t25.97\t36.03\t5",
            "12BHM03\t5/9/2012\t20:50\t5/9/2012\t4:50 PM\t28.54465\t-87.58355\t411.8\t25.97\t35.96\t5",
            "12BHM03\t5/9/2012\t21:05\t5/9/2012\t5:05 PM\t28.54183\t-87.59608\t412\t26\t35.97\t5",
            "12BHM03\t5/9/2012\t21:20\t5/9/2012\t5:20 PM\t28.53952\t-87.609\t411.1\t25.97\t35.99\t5",
            "12BHM03\t5/9/2012\t21:35\t5/9/2012\t5:35 PM\t28.53702\t-87.62115\t413\t25.97\t35.98\t5",
            "12BHM03\t5/9/2012\t21:50\t5/9/2012\t5:50 PM\t28.5336\t-87.63238\t410.6\t25.95\t36.09\t5",
            "12BHM03\t5/9/2012\t22:05\t5/9/2012\t6:05 PM\t28.53037\t-87.64455\t410.2\t25.92\t36.16\t5",
            "12BHM03\t5/9/2012\t22:20\t5/9/2012\t6:20 PM\t28.52615\t-87.65943\t409\t25.89\t36.2\t5",
            "12BHM03\t5/9/2012\t22:35\t5/9/2012\t6:35 PM\t28.52267\t-87.6742\t409\t25.89\t36.24\t5",
            "12BHM03\t5/9/2012\t22:50\t5/9/2012\t6:50 PM\t28.52167\t-87.6882\t409.3\t25.92\t36.27\t5",
            "12BHM03\t5/9/2012\t23:05\t5/9/2012\t7:05 PM\t28.52328\t-87.70242\t407.3\t25.95\t36.24\t5",
            "12BHM03\t5/9/2012\t23:20\t5/9/2012\t7:20 PM\t28.52493\t-87.71668\t410.3\t26.16\t36.16\t5",
            "12BHM03\t5/9/2012\t23:35\t5/9/2012\t7:35 PM\t28.52663\t-87.72958\t410.4\t26.27\t36.09\t5",
            "12BHM03\t5/9/2012\t23:39\t5/9/2012\t7:39 PM\t28.52703\t-87.73267\t406.9\t26.27\t36.08\t5",
            "12BHM03\t5/9/2012\t23:40\t5/9/2012\t7:40 PM\t28.52698\t-87.73348\t403.5\t26.19\t36.07\t5",
            "12BHM03\t5/9/2012\t23:40\t5/9/2012\t7:40 PM\t28.52698\t-87.73348\t401.8\t26.21\t36.07\t5",
            "12BHM03\t5/10/2012\t00:00\t5/9/2012\t8:00 PM\t28.52897\t-87.74925\t400.5\t26.16\t35.94\t5",
            "12BHM03\t5/10/2012\t00:04\t5/9/2012\t8:04 PM\t28.52935\t-87.75255\t401.6\t26.19\t35.94\t5",
            "12BHM03\t5/10/2012\t00:09\t5/9/2012\t8:09 PM\t28.52988\t-87.75648\t403.3\t26.13\t35.9\t5",
            "12BHM03\t5/10/2012\t00:20\t5/9/2012\t8:20 PM\t28.53103\t-87.7653\t402.7\t26.03\t35.91\t5",
            "12BHM03\t5/10/2012\t00:35\t5/9/2012\t8:35 PM\t28.53218\t-87.7766\t402.6\t26.03\t35.5\t5",
            "12BHM03\t5/10/2012\t00:50\t5/9/2012\t8:50 PM\t28.53375\t-87.7876\t404.9\t26.03\t34.95\t5",
            "12BHM03\t5/10/2012\t01:05\t5/9/2012\t9:05 PM\t28.53515\t-87.79933\t403.4\t26.08\t34.47\t5",
            "12BHM03\t5/10/2012\t01:20\t5/9/2012\t9:20 PM\t28.53775\t-87.7998\t385.3\t24.95\t34.55\t5",
            "12BHM03\t5/10/2012\t01:35\t5/9/2012\t9:35 PM\t28.54248\t-87.80402\t379.8\t25.13\t34.63\t5",
            "12BHM03\t5/10/2012\t01:50\t5/9/2012\t9:50 PM\t28.57\t-87.82323\t374.1\t25.58\t34.68\t5",
            "12BHM03\t5/10/2012\t02:05\t5/9/2012\t10:05 PM\t28.59762\t-87.84277\t379.7\t25.79\t35.15\t5",
            "12BHM03\t5/10/2012\t02:20\t5/9/2012\t10:20 PM\t28.62662\t-87.86312\t392.8\t25.79\t35.96\t5",
            "12BHM03\t5/10/2012\t02:35\t5/9/2012\t10:35 PM\t28.63368\t-87.87072\t395.4\t25.79\t35.96\t5",
            "12BHM03\t5/10/2012\t02:50\t5/9/2012\t10:50 PM\t28.63052\t-87.86942\t396\t25.76\t36\t5",
            "12BHM03\t5/10/2012\t03:05\t5/9/2012\t11:05 PM\t28.6284\t-87.86793\t399.5\t25.87\t36.03\t5",
            "12BHM03\t5/10/2012\t03:20\t5/9/2012\t11:20 PM\t28.62668\t-87.86678\t398.2\t25.79\t36.06\t5",
            "12BHM03\t5/10/2012\t03:35\t5/9/2012\t11:35 PM\t28.62488\t-87.8655\t385.9\t24.81\t36.08\t5",
            "12BHM03\t5/10/2012\t03:50\t5/9/2012\t11:50 PM\t28.62218\t-87.86453\t444.5\t25.26\t36.09\t5",
            "12BHM03\t5/10/2012\t04:05\t5/10/2012\t12:05 AM\t28.6088\t-87.86513\t433.4\t25.23\t36.08\t5",
            "12BHM03\t5/10/2012\t04:20\t5/10/2012\t12:20 AM\t28.59702\t-87.86647\t429.1\t25.18\t36.04\t5",
            "12BHM03\t5/10/2012\t04:35\t5/10/2012\t12:35 AM\t28.59863\t-87.8693\t426.2\t25.18\t36.01\t5",
            "12BHM03\t5/10/2012\t04:50\t5/10/2012\t12:50 AM\t28.60277\t-87.87783\t416.9\t25.18\t35.99\t5",
            "12BHM03\t5/10/2012\t05:05\t5/10/2012\t1:05 AM\t28.61908\t-87.87608\t412.5\t25.18\t36.13\t5",
            "12BHM03\t5/10/2012\t05:20\t5/10/2012\t1:20 AM\t28.63192\t-87.86977\t411.4\t25.21\t36.25\t5",
            "12BHM03\t5/10/2012\t05:35\t5/10/2012\t1:35 AM\t28.63078\t-87.86845\t409.2\t25.21\t36.24\t5",
            "12BHM03\t5/10/2012\t05:50\t5/10/2012\t1:50 AM\t28.62712\t-87.86863\t409.6\t25.31\t36.24\t5",
            "12BHM03\t5/10/2012\t06:05\t5/10/2012\t2:05 AM\t28.62685\t-87.86842\t405.9\t25.42\t36.25\t5",
            "12BHM03\t5/10/2012\t06:20\t5/10/2012\t2:20 AM\t28.62702\t-87.86877\t407.7\t25.26\t36.24\t5",
            "12BHM03\t5/10/2012\t06:35\t5/10/2012\t2:35 AM\t28.62737\t-87.86878\t409\t25.15\t36.29\t5",
            "12BHM03\t5/10/2012\t06:50\t5/10/2012\t2:50 AM\t28.62747\t-87.86773\t414.9\t25.1\t36.26\t5",
            "12BHM03\t5/10/2012\t07:05\t5/10/2012\t3:05 AM\t28.62517\t-87.8674\t416.9\t25.08\t36.29\t5",
            "12BHM03\t5/10/2012\t07:20\t5/10/2012\t3:20 AM\t28.62143\t-87.86937\t419.7\t25.23\t36.28\t5",
            "12BHM03\t5/10/2012\t07:35\t5/10/2012\t3:35 AM\t28.6279\t-87.89782\t418.1\t25.15\t36.25\t5",
            "12BHM03\t5/10/2012\t07:50\t5/10/2012\t3:50 AM\t28.63453\t-87.92675\t417.6\t25.34\t36.16\t5",
            "12BHM03\t5/10/2012\t08:05\t5/10/2012\t4:05 AM\t28.641\t-87.9552\t415.7\t25.44\t36.01\t5",
            "12BHM03\t5/10/2012\t08:20\t5/10/2012\t4:20 AM\t28.64743\t-87.9837\t414.6\t25.52\t35.88\t5",
            "12BHM03\t5/10/2012\t08:35\t5/10/2012\t4:35 AM\t28.65367\t-88.0114\t415.7\t25.52\t35.65\t5",
            "12BHM03\t5/10/2012\t08:50\t5/10/2012\t4:50 AM\t28.6623\t-88.0376\t414\t25.55\t35.47\t5",
            "12BHM03\t5/10/2012\t09:05\t5/10/2012\t5:05 AM\t28.67952\t-88.0618\t412\t25.52\t35.36\t5",
            "12BHM03\t5/10/2012\t09:20\t5/10/2012\t5:20 AM\t28.69478\t-88.08327\t410.8\t25.55\t35.34\t5",
            "12BHM03\t5/10/2012\t09:35\t5/10/2012\t5:35 AM\t28.71357\t-88.1044\t410.2\t25.63\t35.25\t5",
            "12BHM03\t5/10/2012\t09:50\t5/10/2012\t5:50 AM\t28.73583\t-88.12753\t408.6\t25.55\t35.56\t5",
            "12BHM03\t5/10/2012\t10:05\t5/10/2012\t6:05 AM\t28.759\t-88.1504\t410.1\t25.55\t35.73\t5",
            "12BHM03\t5/10/2012\t10:20\t5/10/2012\t6:20 AM\t28.78187\t-88.17292\t410.4\t25.5\t35.71\t5",
            "12BHM03\t5/10/2012\t10:35\t5/10/2012\t6:35 AM\t28.8021\t-88.19738\t409.8\t25.42\t35.68\t5",
            "12BHM03\t5/10/2012\t10:50\t5/10/2012\t6:50 AM\t28.81793\t-88.2234\t409.6\t25.34\t35.61\t5",
            "12BHM03\t5/10/2012\t11:05\t5/10/2012\t7:05 AM\t28.80315\t-88.25852\t410.1\t25.34\t34.25\t5",
            "12BHM03\t5/10/2012\t11:20\t5/10/2012\t7:20 AM\t28.78737\t-88.29237\t412.3\t25.34\t33.23\t5",
            "12BHM03\t5/10/2012\t11:35\t5/10/2012\t7:35 AM\t28.77142\t-88.32683\t413.1\t25.34\t32.4\t5",
            "12BHM03\t5/10/2012\t11:50\t5/10/2012\t7:50 AM\t28.75427\t-88.36057\t412.2\t25.34\t32.64\t5",
            "12BHM03\t5/10/2012\t12:05\t5/10/2012\t8:05 AM\t28.74002\t-88.38895\t411.8\t25.34\t33.94\t5",
            "12BHM03\t5/10/2012\t12:20\t5/10/2012\t8:20 AM\t28.73823\t-88.39355\t411.7\t25.44\t34.07\t5",
            "12BHM03\t5/10/2012\t12:24\t5/10/2012\t8:24 AM\t28.73873\t-88.39473\t409.8\t25.5\t16.91\t5",
            "12BHM03\t5/10/2012\t12:24\t5/10/2012\t8:24 AM\t28.73873\t-88.39473\t410.3\t25.5\t16.91\t5",
            "12BHM03\t5/10/2012\t12:24\t5/10/2012\t8:24 AM\t28.73873\t-88.39473\t409.8\t25.5\t16.91\t5",
            "12BHM03\t5/10/2012\t12:24\t5/10/2012\t8:24 AM\t28.73873\t-88.39473\t409.2\t25.5\t16.91\t5",
            "12BHM03\t5/10/2012\t12:24\t5/10/2012\t8:24 AM\t28.73873\t-88.39473\t407.9\t25.47\t16.91\t5",
            "12BHM03\t5/10/2012\t12:24\t5/10/2012\t8:24 AM\t28.73873\t-88.39473\t407.1\t25.44\t16.91\t5",
            "12BHM03\t5/10/2012\t12:24\t5/10/2012\t8:24 AM\t28.73873\t-88.39473\t1.$\t25.26\t16.91\t5",
            "12BHM03\t5/10/2012\t12:24\t5/10/2012\t8:24 AM\t28.73873\t-88.39473\t442.1\t24.3\t16.91\t5",
            "12BHM03\t5/11/2012\t00:11\t5/10/2012\t8:11 PM\t28.97742\t-87.87017\t426.6\t24.19\t34.33\t5",
            "12BHM03\t5/11/2012\t00:19\t5/10/2012\t8:19 PM\t28.9787\t-87.8691\t422.3\t24.22\t34.31\t5",
            "12BHM03\t5/11/2012\t00:24\t5/10/2012\t8:24 PM\t28.97877\t-87.86858\t418.8\t24.19\t34.3\t5",
            "12BHM03\t5/11/2012\t00:35\t5/10/2012\t8:35 PM\t28.97835\t-87.86735\t420.4\t24.42\t34.31\t5",
            "12BHM03\t5/11/2012\t00:50\t5/10/2012\t8:50 PM\t28.9784\t-87.86595\t416.6\t24.53\t34.32\t5",
            "12BHM03\t5/11/2012\t01:05\t5/10/2012\t9:05 PM\t28.97823\t-87.86447\t415.5\t24.55\t34.31\t5",
            "12BHM03\t5/11/2012\t01:20\t5/10/2012\t9:20 PM\t28.97608\t-87.86168\t415\t24.58\t34.36\t5",
            "12BHM03\t5/11/2012\t01:35\t5/10/2012\t9:35 PM\t28.96532\t-87.85408\t414\t24.63\t34.49\t5",
            "12BHM03\t5/11/2012\t01:50\t5/10/2012\t9:50 PM\t28.95868\t-87.84798\t408.3\t24.68\t34.55\t5",
            "12BHM03\t5/11/2012\t02:05\t5/10/2012\t10:05 PM\t28.96728\t-87.85353\t409.1\t24.68\t34.46\t5",
            "12BHM03\t5/11/2012\t02:20\t5/10/2012\t10:20 PM\t28.97508\t-87.86337\t409.5\t24.71\t34.37\t5",
            "12BHM03\t5/11/2012\t02:35\t5/10/2012\t10:35 PM\t28.97458\t-87.86675\t410.2\t24.66\t34.43\t5",
            "12BHM03\t5/11/2012\t02:50\t5/10/2012\t10:50 PM\t28.97328\t-87.86485\t406.1\t24.55\t34.45\t5",
            "12BHM03\t5/11/2012\t03:05\t5/10/2012\t11:05 PM\t28.97303\t-87.86355\t403.3\t24.58\t34.48\t5",
            "12BHM03\t5/11/2012\t03:20\t5/10/2012\t11:20 PM\t28.9724\t-87.86223\t402\t24.55\t34.48\t5",
            "12BHM03\t5/11/2012\t03:35\t5/10/2012\t11:35 PM\t28.97188\t-87.86047\t401.3\t24.5\t34.49\t5",
            "12BHM03\t5/11/2012\t03:50\t5/10/2012\t11:50 PM\t28.98423\t-87.86007\t404.4\t24.5\t34.71\t5",
            "12BHM03\t5/11/2012\t04:05\t5/11/2012\t12:05 AM\t28.99363\t-87.85695\t406.4\t24.5\t34.34\t5",
            "12BHM03\t5/11/2012\t04:20\t5/11/2012\t12:20 AM\t29.0025\t-87.85348\t401.9\t24.4\t34.04\t5",
            "12BHM03\t5/11/2012\t04:35\t5/11/2012\t12:35 AM\t29.0108\t-87.84858\t398.3\t24.27\t33.64\t5",
            "12BHM03\t5/11/2012\t04:50\t5/11/2012\t12:50 AM\t29.01835\t-87.84283\t392.8\t24.04\t33.25\t5",
            "12BHM03\t5/11/2012\t05:05\t5/11/2012\t1:05 AM\t29.02352\t-87.83288\t397.3\t24.11\t33.1\t5",
            "12BHM03\t5/11/2012\t05:20\t5/11/2012\t1:20 AM\t29.01375\t-87.83197\t394.1\t24.19\t33.14\t5",
            "12BHM03\t5/11/2012\t05:35\t5/11/2012\t1:35 AM\t29.01933\t-87.83867\t390.3\t24.22\t33.11\t5",
            "12BHM03\t5/11/2012\t05:50\t5/11/2012\t1:50 AM\t29.02847\t-87.84538\t385.5\t24.3\t32.99\t5",
            "12BHM03\t5/11/2012\t06:05\t5/11/2012\t2:05 AM\t29.0377\t-87.85278\t382.8\t24.32\t33.06\t5",
            "12BHM03\t5/11/2012\t06:20\t5/11/2012\t2:20 AM\t29.04713\t-87.86117\t379.9\t24.3\t33.19\t5",
            "12BHM03\t5/11/2012\t06:35\t5/11/2012\t2:35 AM\t29.05647\t-87.87048\t456.7\t25.02\t33.16\t5",
            "12BHM03\t5/11/2012\t06:50\t5/11/2012\t2:50 AM\t29.06585\t-87.8798\t442.7\t25\t33.15\t5",
            "12BHM03\t5/11/2012\t07:05\t5/11/2012\t3:05 AM\t29.07583\t-87.88877\t444.6\t25.05\t33.12\t5",
            "12BHM03\t5/11/2012\t07:20\t5/11/2012\t3:20 AM\t29.08618\t-87.89737\t435\t25.31\t33.06\t5",
            "12BHM03\t5/11/2012\t07:35\t5/11/2012\t3:35 AM\t29.096\t-87.90572\t428.3\t25.76\t32.95\t5",
            "12BHM03\t5/11/2012\t07:50\t5/11/2012\t3:50 AM\t29.10577\t-87.91453\t425.3\t25.52\t32.82\t5",
            "12BHM03\t5/11/2012\t08:05\t5/11/2012\t4:05 AM\t29.11545\t-87.92372\t429.5\t25.79\t32.73\t5",
            "12BHM03\t5/11/2012\t08:20\t5/11/2012\t4:20 AM\t29.125\t-87.93353\t432.1\t25.89\t32.69\t5",
            "12BHM03\t5/11/2012\t08:35\t5/11/2012\t4:35 AM\t29.13483\t-87.94343\t435.3\t25.92\t32.68\t5",
            "12BHM03\t5/11/2012\t08:50\t5/11/2012\t4:50 AM\t29.14515\t-87.95153\t433\t25.73\t32.69\t5",
            "12BHM03\t5/11/2012\t09:05\t5/11/2012\t5:05 AM\t29.14523\t-87.95112\t435.9\t25.76\t32.67\t5",
            "12BHM03\t5/11/2012\t09:20\t5/11/2012\t5:20 AM\t29.14238\t-87.9435\t428.3\t25.92\t32.65\t5",
            "12BHM03\t5/11/2012\t09:35\t5/11/2012\t5:35 AM\t29.13868\t-87.92923\t435.3\t26.29\t32.68\t5",
            "12BHM03\t5/11/2012\t09:50\t5/11/2012\t5:50 AM\t29.13487\t-87.91533\t407.2\t25.97\t32.67\t5",
            "12BHM03\t5/11/2012\t10:05\t5/11/2012\t6:05 AM\t29.13127\t-87.90152\t407.4\t25.92\t32.76\t5",
            "12BHM03\t5/11/2012\t10:20\t5/11/2012\t6:20 AM\t29.12683\t-87.88505\t430.8\t25.97\t32.84\t5",
            "12BHM03\t5/11/2012\t10:35\t5/11/2012\t6:35 AM\t29.12235\t-87.8691\t422.7\t26.03\t32.82\t5",
            "12BHM03\t5/11/2012\t11:38\t5/11/2012\t7:38 AM\t29.1167\t-87.8882\t424.6\t26.08\t32.85\t5",
            "12BHM03\t5/11/2012\t11:38\t5/11/2012\t7:38 AM\t29.1167\t-87.8882\t419.1\t25.97\t32.85\t5",
            "12BHM03\t5/11/2012\t11:38\t5/11/2012\t7:38 AM\t29.1167\t-87.8882\t403.2\t26.16\t32.85\t5",
            "12BHM03\t5/11/2012\t11:39\t5/11/2012\t7:39 AM\t29.11748\t-87.88813\t409.8\t25.92\t32.85\t5",
            "12BHM03\t5/11/2012\t11:43\t5/11/2012\t7:43 AM\t29.119\t-87.88585\t416.5\t25.89\t32.84\t5",
            "12BHM03\t5/11/2012\t11:44\t5/11/2012\t7:44 AM\t29.11937\t-87.88543\t412.5\t25.76\t32.83\t5",
            "12BHM03\t5/11/2012\t11:47\t5/11/2012\t7:47 AM\t29.12053\t-87.88442\t411.9\t25.92\t32.82\t5",
            "12BHM03\t5/12/2012\t05:13\t5/12/2012\t1:13 AM\t29.57015\t-86.58022\t411.7\t25.92\t35.71\t5",
            "12BHM03\t5/12/2012\t05:13\t5/12/2012\t1:13 AM\t29.57015\t-86.58022\t408.8\t25.68\t35.71\t5",
            "12BHM03\t5/12/2012\t05:19\t5/12/2012\t1:19 AM\t29.57157\t-86.57485\t405.3\t25.6\t35.71\t5",
            "12BHM03\t5/12/2012\t05:24\t5/12/2012\t1:24 AM\t29.57248\t-86.57087\t404.1\t25.55\t35.71\t5",
            "12BHM03\t5/12/2012\t05:35\t5/12/2012\t1:35 AM\t29.57347\t-86.56297\t396.1\t25.5\t35.73\t5",
            "12BHM03\t5/12/2012\t05:50\t5/12/2012\t1:50 AM\t29.57252\t-86.56168\t403.3\t25.5\t35.59\t5",
            "12BHM03\t5/12/2012\t06:05\t5/12/2012\t2:05 AM\t29.57127\t-86.55297\t401.3\t25.52\t35.59\t5",
            "12BHM03\t5/12/2012\t06:20\t5/12/2012\t2:20 AM\t29.56642\t-86.58323\t399.4\t25.6\t35.57\t5",
            "12BHM03\t5/12/2012\t06:35\t5/12/2012\t2:35 AM\t29.5679\t-86.5841\t404.1\t25.87\t35.57\t5",
            "12BHM03\t5/12/2012\t06:50\t5/12/2012\t2:50 AM\t29.5703\t-86.58363\t400.1\t25.81\t35.58\t5",
            "12BHM03\t5/12/2012\t07:05\t5/12/2012\t3:05 AM\t29.57967\t-86.56982\t396.6\t25.68\t35.58\t5",
            "12BHM03\t5/12/2012\t07:20\t5/12/2012\t3:20 AM\t29.58473\t-86.57928\t399.9\t25.89\t35.56\t5",
            "12BHM03\t5/12/2012\t07:35\t5/12/2012\t3:35 AM\t29.58877\t-86.59565\t400.4\t25.63\t35.57\t5",
            "12BHM03\t5/12/2012\t07:50\t5/12/2012\t3:50 AM\t29.59287\t-86.61242\t397.3\t25.26\t35.62\t5",
            "12BHM03\t5/12/2012\t08:05\t5/12/2012\t4:05 AM\t29.59588\t-86.62218\t396.6\t25.18\t35.62\t5",
            "12BHM03\t5/12/2012\t08:20\t5/12/2012\t4:20 AM\t29.59832\t-86.63753\t396\t25.18\t35.62\t5",
            "12BHM03\t5/12/2012\t08:35\t5/12/2012\t4:35 AM\t29.6026\t-86.65497\t397.1\t25.08\t35.59\t5",
            "12BHM03\t5/12/2012\t08:50\t5/12/2012\t4:50 AM\t29.60702\t-86.67225\t394.7\t25.05\t35.58\t5",
            "12BHM03\t5/12/2012\t09:05\t5/12/2012\t5:05 AM\t29.61382\t-86.68873\t397.9\t25.1\t35.59\t5",
            "12BHM03\t5/12/2012\t09:20\t5/12/2012\t5:20 AM\t29.6217\t-86.7049\t397.7\t25.13\t35.56\t5",
            "12BHM03\t5/12/2012\t09:35\t5/12/2012\t5:35 AM\t29.63035\t-86.7206\t402.4\t25.21\t35.56\t5",
            "12BHM03\t5/12/2012\t09:50\t5/12/2012\t5:50 AM\t29.63952\t-86.73633\t398.2\t25.13\t35.56\t5",
            "12BHM03\t5/12/2012\t10:05\t5/12/2012\t6:05 AM\t29.6486\t-86.75203\t390.7\t25.08\t35.36\t5",
            "12BHM03\t5/12/2012\t10:20\t5/12/2012\t6:20 AM\t29.65775\t-86.7677\t395.3\t25.02\t35.4\t5",
            "12BHM03\t5/12/2012\t10:35\t5/12/2012\t6:35 AM\t29.66697\t-86.78342\t385\t25.02\t35.41\t5",
            "12BHM03\t5/12/2012\t10:50\t5/12/2012\t6:50 AM\t29.67257\t-86.7874\t392.6\t25.08\t35.39\t5",
            "12BHM03\t5/12/2012\t11:05\t5/12/2012\t7:05 AM\t29.67712\t-86.75543\t390.5\t25.08\t35.58\t5",
            "12BHM03\t5/12/2012\t11:20\t5/12/2012\t7:20 AM\t29.68168\t-86.72223\t389.5\t25.13\t35.56\t5",
            "12BHM03\t5/12/2012\t11:35\t5/12/2012\t7:35 AM\t29.68632\t-86.68832\t390.7\t25.1\t35.53\t5",
            "12BHM03\t5/12/2012\t11:50\t5/12/2012\t7:50 AM\t29.69102\t-86.6543\t380.9\t25.15\t35.5\t5",
            "12BHM03\t5/12/2012\t12:05\t5/12/2012\t8:05 AM\t29.6956\t-86.62085\t389.1\t25.18\t35.5\t5",
            "12BHM03\t5/12/2012\t12:20\t5/12/2012\t8:20 AM\t29.7004\t-86.5871\t393\t25.21\t35.51\t5",
            "12BHM03\t5/12/2012\t12:25\t5/12/2012\t8:25 AM\t29.70188\t-86.5757\t397.1\t25.18\t35.52\t5",
            "12BHM03\t5/13/2012\t21:55\t5/13/2012\t5:55 PM\t29.64283\t-86.44035\t394.8\t25.29\t35.86\t5",
            "12BHM03\t5/13/2012\t21:59\t5/13/2012\t5:59 PM\t29.64363\t-86.45135\t385.3\t25.23\t35.87\t5",
            "12BHM03\t5/13/2012\t22:04\t5/13/2012\t6:04 PM\t29.64467\t-86.46512\t384.4\t25.55\t35.88\t5",
            "12BHM03\t5/13/2012\t22:15\t5/13/2012\t6:15 PM\t29.64703\t-86.49497\t390.4\t25.31\t35.85\t5",
            "12BHM03\t5/13/2012\t22:30\t5/13/2012\t6:30 PM\t29.65027\t-86.53587\t389.7\t25.21\t35.88\t5",
            "12BHM03\t5/13/2012\t22:45\t5/13/2012\t6:45 PM\t29.6511\t-86.57402\t388.9\t25.26\t35.88\t5",
            "12BHM03\t5/13/2012\t23:00\t5/13/2012\t7:00 PM\t29.64625\t-86.60087\t384.2\t25.26\t34.9\t5",
            "12BHM03\t5/13/2012\t23:15\t5/13/2012\t7:15 PM\t29.63672\t-86.60635\t384.8\t25.26\t34.79\t5",
            "12BHM03\t5/13/2012\t23:30\t5/13/2012\t7:30 PM\t29.62802\t-86.61602\t390.3\t25.34\t34.85\t5",
            "12BHM03\t5/13/2012\t23:45\t5/13/2012\t7:45 PM\t29.62515\t-86.62863\t384.7\t25.31\t34.84\t5",
            "12BHM03\t5/14/2012\t00:00\t5/13/2012\t8:00 PM\t29.61593\t-86.63688\t386.7\t25.36\t34.81\t5",
            "12BHM03\t5/14/2012\t00:15\t5/13/2012\t8:15 PM\t29.58937\t-86.6675\t389.4\t25.39\t35.03\t5",
            "12BHM03\t5/14/2012\t00:30\t5/13/2012\t8:30 PM\t29.5626\t-86.69882\t390.2\t25.36\t34.56\t5",
            "12BHM03\t5/14/2012\t00:45\t5/13/2012\t8:45 PM\t29.53553\t-86.72998\t388\t25.5\t34.36\t5",
            "12BHM03\t5/14/2012\t01:00\t5/13/2012\t9:00 PM\t29.51265\t-86.75055\t386.9\t25.66\t34.55\t5",
            "12BHM03\t5/14/2012\t01:15\t5/13/2012\t9:15 PM\t29.50322\t-86.74277\t390\t25.79\t34.61\t5",
            "12BHM03\t5/14/2012\t01:30\t5/13/2012\t9:30 PM\t29.4946\t-86.73508\t385\t25.97\t34.78\t5",
            "12BHM03\t5/14/2012\t01:45\t5/13/2012\t9:45 PM\t29.48615\t-86.7279\t386.8\t25.95\t34.85\t5",
            "12BHM03\t5/14/2012\t02:00\t5/13/2012\t10:00 PM\t29.47752\t-86.7203\t385.1\t26\t35.3\t5",
            "12BHM03\t5/14/2012\t02:15\t5/13/2012\t10:15 PM\t29.46973\t-86.7132\t386.5\t26.08\t35.82\t5",
            "12BHM03\t5/14/2012\t02:30\t5/13/2012\t10:30 PM\t29.46212\t-86.7054\t394\t26.13\t36.1\t5",
            "12BHM03\t5/14/2012\t02:45\t5/13/2012\t10:45 PM\t29.45492\t-86.69767\t381.5\t26.13\t36.15\t5",
            "12BHM03\t5/14/2012\t03:00\t5/13/2012\t11:00 PM\t29.4479\t-86.69\t389.7\t26.03\t36.12\t5",
            "12BHM03\t5/14/2012\t03:15\t5/13/2012\t11:15 PM\t29.44032\t-86.68167\t393\t26.08\t36.09\t5",
            "12BHM03\t5/14/2012\t03:30\t5/13/2012\t11:30 PM\t29.43168\t-86.67275\t391.9\t26.16\t36.17\t5",
            "12BHM03\t5/14/2012\t03:45\t5/13/2012\t11:45 PM\t29.42282\t-86.6639\t396.8\t26.29\t36.15\t5",
            "12BHM03\t5/14/2012\t04:00\t5/14/2012\t12:00 AM\t29.41405\t-86.655\t389.7\t26.32\t36.14\t5",
            "12BHM03\t5/14/2012\t04:15\t5/14/2012\t12:15 AM\t29.40602\t-86.6484\t396.3\t26.35\t36.17\t5",
            "12BHM03\t5/14/2012\t04:30\t5/14/2012\t12:30 AM\t29.39518\t-86.64213\t394\t26.11\t0.31\t5",
            "12BHM03\t5/14/2012\t04:45\t5/14/2012\t12:45 AM\t29.38413\t-86.63578\t396.3\t26.13\t0.28\t5",
            "12BHM03\t5/14/2012\t05:00\t5/14/2012\t1:00 AM\t29.37292\t-86.62908\t395.7\t26.05\t36.04\t5",
            "12BHM03\t5/14/2012\t05:15\t5/14/2012\t1:15 AM\t29.36195\t-86.62295\t392.4\t26.19\t0.3\t5",
            "12BHM03\t5/14/2012\t05:30\t5/14/2012\t1:30 AM\t29.35008\t-86.61772\t396.7\t26.29\t36.12\t5",
            "12BHM03\t5/14/2012\t05:45\t5/14/2012\t1:45 AM\t29.33682\t-86.61323\t397.4\t26.32\t17.57\t5",
            "12BHM03\t5/14/2012\t06:00\t5/14/2012\t2:00 AM\t29.32397\t-86.60852\t395.8\t26.43\t35.73\t5",
            "12BHM03\t5/14/2012\t06:15\t5/14/2012\t2:15 AM\t29.3208\t-86.60915\t397.5\t26.53\t35.79\t5",
            "12BHM03\t5/14/2012\t06:30\t5/14/2012\t2:30 AM\t29.32493\t-86.61108\t402.1\t26.64\t35.8\t5",
            "12BHM03\t5/14/2012\t06:45\t5/14/2012\t2:45 AM\t29.33607\t-86.62738\t396.1\t26.48\t35.93\t5",
            "12BHM03\t5/14/2012\t07:00\t5/14/2012\t3:00 AM\t29.34975\t-86.64772\t389.6\t26.45\t35.99\t5",
            "12BHM03\t5/14/2012\t07:15\t5/14/2012\t3:15 AM\t29.36343\t-86.66785\t390.5\t26.45\t36.14\t5",
            "12BHM03\t5/14/2012\t07:30\t5/14/2012\t3:30 AM\t29.3771\t-86.68788\t403\t26.37\t36.17\t5",
            "12BHM03\t5/14/2012\t07:45\t5/14/2012\t3:45 AM\t29.39053\t-86.70783\t394.2\t26.32\t36.14\t5",
            "12BHM03\t5/14/2012\t08:00\t5/14/2012\t4:00 AM\t29.40407\t-86.72788\t391.9\t26.16\t36.02\t5",
            "12BHM03\t5/14/2012\t08:15\t5/14/2012\t4:15 AM\t29.41773\t-86.7481\t390.2\t26.13\t35.81\t5",
            "12BHM03\t5/14/2012\t08:30\t5/14/2012\t4:30 AM\t29.4321\t-86.76927\t397\t26.16\t35.04\t5",
            "12BHM03\t5/14/2012\t08:45\t5/14/2012\t4:45 AM\t29.44467\t-86.78208\t395.3\t25.97\t34.69\t5",
            "12BHM03\t5/14/2012\t09:00\t5/14/2012\t5:00 AM\t29.46012\t-86.79017\t396.3\t25.95\t34.29\t5",
            "12BHM03\t5/14/2012\t09:15\t5/14/2012\t5:15 AM\t29.4803\t-86.80037\t401.1\t25.76\t34.24\t5",
            "12BHM03\t5/14/2012\t09:30\t5/14/2012\t5:30 AM\t29.49888\t-86.81305\t398\t25.79\t34.1\t5",
            "12BHM03\t5/14/2012\t09:45\t5/14/2012\t5:45 AM\t29.51627\t-86.828\t397.3\t25.79\t34.05\t5",
            "12BHM03\t5/14/2012\t10:00\t5/14/2012\t6:00 AM\t29.53385\t-86.84328\t399\t26\t34.04\t5",
            "12BHM03\t5/14/2012\t10:15\t5/14/2012\t6:15 AM\t29.54647\t-86.85313\t397\t25.97\t34.15\t5",
            "12BHM03\t5/14/2012\t10:30\t5/14/2012\t6:30 AM\t29.53328\t-86.84332\t397.8\t25.97\t34.03\t5",
            "12BHM03\t5/14/2012\t10:45\t5/14/2012\t6:45 AM\t29.51893\t-86.8327\t396.9\t25.92\t34.05\t5",
            "12BHM03\t5/14/2012\t11:00\t5/14/2012\t7:00 AM\t29.50387\t-86.82173\t397.4\t25.79\t34.07\t5",
            "12BHM03\t5/14/2012\t11:15\t5/14/2012\t7:15 AM\t29.48873\t-86.81067\t398.2\t25.71\t34.12\t5",
            "12BHM03\t5/14/2012\t11:30\t5/14/2012\t7:30 AM\t29.47347\t-86.79938\t402.9\t25.76\t34.27\t5",
            "12BHM03\t5/14/2012\t11:45\t5/14/2012\t7:45 AM\t29.45877\t-86.78843\t398.9\t25.76\t34.45\t5",
            "12BHM03\t5/14/2012\t12:00\t5/14/2012\t8:00 AM\t29.43957\t-86.78152\t390.9\t25.71\t34.65\t5",
            "12BHM03\t5/14/2012\t12:15\t5/14/2012\t8:15 AM\t29.43727\t-86.77813\t391.8\t25.68\t34.97\t5",
            "12BHM03\t5/14/2012\t12:30\t5/14/2012\t8:30 AM\t29.43805\t-86.77627\t400.9\t25.66\t34.63\t5",
            "12BHM03\t5/14/2012\t12:45\t5/14/2012\t8:45 AM\t29.42863\t-86.7862\t398.6\t25.6\t34.77\t5",
            "12BHM03\t5/14/2012\t13:00\t5/14/2012\t9:00 AM\t29.42952\t-86.79903\t398.1\t25.52\t34.95\t5",
            "12BHM03\t5/14/2012\t13:15\t5/14/2012\t9:15 AM\t29.43335\t-86.79723\t401.4\t25.81\t34.86\t5",
            "12BHM03\t5/14/2012\t13:30\t5/14/2012\t9:30 AM\t29.42208\t-86.78767\t398.6\t25.79\t35.05\t5",
            "12BHM03\t5/14/2012\t13:45\t5/14/2012\t9:45 AM\t29.41182\t-86.77792\t396.5\t25.79\t35.29\t5",
            "12BHM03\t5/14/2012\t14:00\t5/14/2012\t10:00 AM\t29.40087\t-86.768\t396.8\t25.81\t35.72\t5",
            "12BHM03\t5/14/2012\t14:15\t5/14/2012\t10:15 AM\t29.4053\t-86.76428\t396.9\t26.08\t35.63\t5",
            "12BHM03\t5/14/2012\t14:30\t5/14/2012\t10:30 AM\t29.43952\t-86.78098\t395.5\t25.87\t34.83\t5",
            "12BHM03\t5/14/2012\t14:45\t5/14/2012\t10:45 AM\t29.44243\t-86.78223\t394.3\t25.76\t34.84\t5",
            "12BHM03\t5/14/2012\t15:00\t5/14/2012\t11:00 AM\t29.44327\t-86.78028\t392.1\t25.68\t34.95\t5",
            "12BHM03\t5/14/2012\t15:15\t5/14/2012\t11:15 AM\t29.42837\t-86.80083\t387.5\t25.63\t34.9\t5",
            "12BHM03\t5/14/2012\t15:30\t5/14/2012\t11:30 AM\t29.41118\t-86.82702\t386.3\t25.66\t34.43\t5",
            "12BHM03\t5/14/2012\t15:45\t5/14/2012\t11:45 AM\t29.3951\t-86.85173\t386.8\t25.6\t34.44\t5",
            "12BHM03\t5/14/2012\t16:00\t5/14/2012\t12:00 PM\t29.37937\t-86.87557\t387.5\t25.58\t34.51\t5",
            "12BHM03\t5/14/2012\t16:15\t5/14/2012\t12:15 PM\t29.36407\t-86.89907\t388.1\t25.71\t34.52\t5",
            "12BHM03\t5/14/2012\t16:30\t5/14/2012\t12:30 PM\t29.34767\t-86.92428\t389.4\t25.71\t34.53\t5",
            "12BHM03\t5/14/2012\t16:45\t5/14/2012\t12:45 PM\t29.32568\t-86.95715\t389\t25.66\t34.66\t5",
            "12BHM03\t5/14/2012\t17:00\t5/14/2012\t1:00 PM\t29.30372\t-86.99003\t396.7\t25.79\t34.85\t5",
            "12BHM03\t5/14/2012\t17:15\t5/14/2012\t1:15 PM\t29.28117\t-87.02352\t396.7\t25.52\t35.14\t5",
            "12BHM03\t5/14/2012\t17:30\t5/14/2012\t1:30 PM\t29.2587\t-87.05698\t398.1\t25.68\t35.92\t5",
            "12BHM03\t5/14/2012\t17:45\t5/14/2012\t1:45 PM\t29.23697\t-87.08947\t402.5\t25.6\t35.75\t5",
            "12BHM03\t5/14/2012\t18:00\t5/14/2012\t2:00 PM\t29.21445\t-87.12287\t404\t25.44\t35.59\t5",
            "12BHM03\t5/14/2012\t18:15\t5/14/2012\t2:15 PM\t29.19145\t-87.1573\t404.6\t25.34\t35.55\t5",
            "12BHM03\t5/14/2012\t18:30\t5/14/2012\t2:30 PM\t29.16802\t-87.19213\t394.8\t25.18\t35.51\t5",
            "12BHM03\t5/14/2012\t18:45\t5/14/2012\t2:45 PM\t29.14448\t-87.22732\t378.3\t25.34\t35.36\t5",
            "12BHM03\t5/14/2012\t19:00\t5/14/2012\t3:00 PM\t29.12073\t-87.26272\t387.2\t25.36\t35.33\t5",
            "12BHM03\t5/14/2012\t19:15\t5/14/2012\t3:15 PM\t29.11917\t-87.26592\t403.2\t25\t35.31\t5",
            "12BHM03\t5/14/2012\t19:30\t5/14/2012\t3:30 PM\t29.11972\t-87.26573\t408\t24.87\t35.33\t5",
            "12BHM03\t5/14/2012\t19:45\t5/14/2012\t3:45 PM\t29.1194\t-87.27815\t411.1\t24.92\t35.32\t5",
            "12BHM03\t5/14/2012\t20:00\t5/14/2012\t4:00 PM\t29.12122\t-87.28947\t404\t24.87\t35.31\t5",
            "12BHM03\t5/14/2012\t20:15\t5/14/2012\t4:15 PM\t29.124\t-87.3017\t404.4\t24.79\t35.3\t5",
            "12BHM03\t5/14/2012\t20:30\t5/14/2012\t4:30 PM\t29.12672\t-87.30755\t402.7\t24.89\t35.29\t5",
            "12BHM03\t5/14/2012\t20:45\t5/14/2012\t4:45 PM\t29.11953\t-87.26572\t398.8\t24.89\t35.33\t5",
            "12BHM03\t5/14/2012\t21:00\t5/14/2012\t5:00 PM\t29.11822\t-87.26432\t396.1\t24.74\t35.33\t5",
            "12BHM03\t5/14/2012\t21:15\t5/14/2012\t5:15 PM\t29.11773\t-87.26418\t395.2\t24.61\t35.33\t5",
            "12BHM03\t5/14/2012\t21:30\t5/14/2012\t5:30 PM\t29.11777\t-87.26508\t408.1\t25.05\t35.32\t5",
            "12BHM03\t5/14/2012\t21:45\t5/14/2012\t5:45 PM\t29.11358\t-87.26652\t406.9\t25.08\t35.32\t5",
            "12BHM03\t5/14/2012\t22:00\t5/14/2012\t6:00 PM\t29.09773\t-87.2687\t404.3\t25.23\t35.37\t5",
            "12BHM03\t5/14/2012\t22:15\t5/14/2012\t6:15 PM\t29.08373\t-87.27035\t403.6\t25.26\t35.38\t5",
            "12BHM03\t5/14/2012\t22:30\t5/14/2012\t6:30 PM\t29.06795\t-87.27333\t397.6\t25.44\t35.43\t5",
            "12BHM03\t5/14/2012\t22:45\t5/14/2012\t6:45 PM\t29.05258\t-87.27752\t394.3\t25.21\t35.49\t5",
            "12BHM03\t5/14/2012\t23:00\t5/14/2012\t7:00 PM\t29.04275\t-87.28658\t395.6\t25.26\t35.51\t5",
            "12BHM03\t5/14/2012\t23:15\t5/14/2012\t7:15 PM\t29.03808\t-87.30302\t396.8\t25.26\t35.46\t5",
            "12BHM03\t5/14/2012\t23:30\t5/14/2012\t7:30 PM\t29.03305\t-87.31987\t393.1\t25.05\t35.42\t5",
            "12BHM03\t5/14/2012\t23:45\t5/14/2012\t7:45 PM\t29.0281\t-87.3366\t394.7\t25.1\t35.36\t5",
            "12BHM03\t5/15/2012\t00:00\t5/14/2012\t8:00 PM\t29.02318\t-87.3535\t397.1\t25.05\t35.32\t5",
            "12BHM03\t5/15/2012\t00:15\t5/14/2012\t8:15 PM\t29.02347\t-87.36718\t427.1\t24.76\t35.3\t5"
    };

    private static final ArrayList<DataColumnType> speedDataColTypes = new ArrayList<DataColumnType>(Arrays.asList(
            DashboardServerUtils.DATASET_ID.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            DashboardServerUtils.DATE.duplicate(),
            DashboardServerUtils.TIME_OF_DAY.duplicate(),
            DashboardServerUtils.LONGITUDE.duplicate(),
            DashboardServerUtils.LATITUDE.duplicate(),
            SocatTypes.FCO2_WATER_SST_WET.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            SocatTypes.PATM.duplicate(),
            SocatTypes.SST.duplicate(),
            SocatTypes.SALINITY.duplicate()
    ));

    static {
        speedDataColTypes.get(2).setSelectedUnit("dd-mm-yyyy");
        speedDataColTypes.get(8).setSelectedMissingValue("0");
    }

    private static final ArrayList<String> speedUserColumnNames = new ArrayList<String>(Arrays.asList(
            "CruiseID", "JD_GMT", "DATE_UTC__ddmmyyyy", "TIME_UTC_hhmmss", "longitudedecdegE", "latitudedecdegN",
            "fCO2recuatm", "GVCO2umolmol", "PPPPhPa", "SSTdegC", "SAL_permil"
    ));

    private static final String[] speedDataTSVStrings = {
            "76XL20091101\t2455137\t01112009\t07:19:00\t-177.05\t-49.05\t393.6672\t385.24\t1004.69\t9.05\t34.16",
            "76XL20091101\t2455137\t01112009\t07:29:00\t-177.02\t-49.07\t394.8096\t385.17\t1004.75\t9.06\t34.15",
            "76XL20091101\t2455137\t01112009\t07:39:00\t-176.59\t-49.09\t394.0416\t385.04\t1004.81\t9.11\t34.14",
            "76XL20091101\t2455137\t01112009\t08:59:00\t-176.38\t-49.23\t393.1872\t384.36\t1003.16\t9.26\t34.13",
            "76XL20091101\t2455137\t01112009\t09:09:00\t-176.35\t-49.25\t394.0704\t384.22\t1002.72\t9.29\t34.14",
            "76XL20091101\t2455137\t01112009\t09:49:00\t-176.24\t-49.33\t381.2928\t384.33\t1001.82\t9.83\t34.17",
            "76XL20091101\t2455137\t01112009\t09:59:00\t-176.22\t-49.34\t381.0048\t384.36\t1001.99\t9.83\t34.16",
            "76XL20091101\t2455137\t01112009\t10:09:00\t-176.19\t-49.36\t381.1392\t384.4\t1001.49\t9.82\t34.15",
            "76XL20091101\t2455137\t01112009\t10:49:00\t-176.08\t-49.43\t380.9184\t384.51\t1000.64\t9.79\t34.15",
            "76XL20091101\t2455137\t01112009\t10:59:00\t-176.05\t-49.45\t381.0432\t384.54\t1000.55\t9.78\t34.15",
            "76XL20091101\t2455137\t01112009\t11:39:00\t-175.54\t-49.53\t379.0176\t384.33\t999.42\t9.83\t34.15",
            "76XL20091101\t2455137\t01112009\t12:19:00\t-175.43\t-50\t373.824\t384.01\t998.84\t9.88\t34.18",
            "76XL20091101\t2455137\t01112009\t12:29:00\t-175.4\t-50.02\t373.3632\t383.95\t998.86\t9.91\t34.17",
            "76XL20091101\t2455137\t01112009\t12:39:00\t-175.37\t-50.04\t375.1104\t383.82\t0\t9.92\t34.17",
            "76XL20091101\t2455137\t01112009\t13:09:00\t-175.28\t-50.1\t370.9632\t383.57\t0\t9.87\t34.17",
            "76XL20091101\t2455137\t01112009\t13:39:00\t-175.2\t-50.15\t374.8704\t383.83\t0\t9.51\t34.19",
            "76XL20091101\t2455137\t01112009\t13:49:00\t-175.17\t-50.17\t373.968\t383.89\t0\t9.48\t34.21",
            "76XL20091101\t2455137\t01112009\t14:59:00\t-174.57\t-50.3\t367.3536\t384.46\t0\t9.63\t34.17",
            "76XL20091101\t2455137\t01112009\t15:29:00\t-174.48\t-50.36\t367.1808\t384.52\t0\t9.52\t34.2",
            "76XL20091101\t2455137\t01112009\t15:39:00\t-174.46\t-50.38\t366.6048\t384.45\t0\t9.52\t34.18",
            "76XL20091101\t2455137\t01112009\t16:09:00\t-174.37\t-50.44\t364.7712\t384.32\t0\t9.62\t34.27",
            "76XL20091101\t2455137\t01112009\t16:49:00\t-174.25\t-50.51\t364.2528\t384.15\t0\t9.63\t34.29",
            "76XL20091101\t2455137\t01112009\t17:19:00\t-174.17\t-50.57\t362.64\t384.06\t0\t9.58\t34.32",
            "76XL20091101\t2455137\t01112009\t17:59:00\t-174.06\t-51.04\t369.5136\t384.1\t0\t9.27\t34.28",
            "76XL20091101\t2455137\t01112009\t18:29:00\t-173.57\t-51.1\t369.5904\t384.12\t0\t9.86\t34.47",
            "76XL20091101\t2455137\t01112009\t18:39:00\t-173.55\t-51.12\t369.9264\t384.14\t0\t9.9\t34.48",
            "76XL20091101\t2455137\t01112009\t19:09:00\t-173.46\t-51.17\t366.7488\t384.17\t0\t9.91\t34.47",
            "76XL20091101\t2455137\t01112009\t19:19:00\t-173.43\t-51.19\t366.5664\t384.18\t0\t9.94\t34.5",
            "76XL20091101\t2455137\t01112009\t19:49:00\t-173.35\t-51.25\t368.208\t384.2\t0\t9.93\t34.51",
            "76XL20091101\t2455137\t01112009\t21:59:00\t-172.58\t-51.46\t370.5696\t384.82\t0\t9.79\t34.39",
            "76XL20091101\t2455137\t01112009\t22:29:00\t-172.49\t-51.52\t372.9504\t385.18\t0\t9.21\t34.24",
            "76XL20091101\t2455137\t01112009\t22:39:00\t-172.47\t-51.53\t372.432\t385.36\t0\t9.23\t34.25",
            "76XL20091101\t2455137\t01112009\t23:09:00\t-172.38\t-51.59\t370.5888\t385.72\t0\t9.21\t34.26",
            "76XL20091101\t2455137\t01112009\t23:19:00\t-172.35\t-52\t371.4048\t385.68\t0\t9.1\t34.24",
            "76XL20091101\t2455137\t01112009\t23:49:00\t-172.26\t-52.06\t376.3872\t385.55\t0\t8.82\t34.2",
            "76XL20091101\t2455138\t02112009\t00:59:00\t-172.05\t-52.18\t379.0176\t385.23\t0\t8.86\t34.22",
            "76XL20091101\t2455138\t02112009\t01:09:00\t-172.02\t-52.2\t381.0432\t385.16\t0\t8.76\t34.2",
            "76XL20091101\t2455138\t02112009\t01:39:00\t-171.53\t-52.25\t380.6112\t384.67\t0\t8.64\t34.17",
            "76XL20091101\t2455138\t02112009\t02:19:00\t-171.41\t-52.32\t376.8384\t384.06\t0\t8.69\t34.21",
            "76XL20091101\t2455138\t02112009\t02:49:00\t-171.32\t-52.38\t374.8608\t383.57\t0\t8.69\t34.21",
            "76XL20091101\t2455138\t02112009\t02:59:00\t-171.29\t-52.39\t376.4544\t383.45\t0\t8.63\t34.19",
            "76XL20091101\t2455138\t02112009\t04:09:00\t-171.08\t-52.53\t378.0288\t383.33\t0\t8.49\t34.17",
            "76XL20091101\t2455138\t02112009\t04:39:00\t-170.6\t-52.58\t374.112\t383.4\t0\t8.47\t34.17",
            "76XL20091101\t2455138\t02112009\t04:49:00\t-170.57\t-53\t375.5232\t383.42\t0\t8.48\t34.17",
            "76XL20091101\t2455138\t02112009\t05:19:00\t-170.48\t-53.06\t374.0256\t383.55\t0\t8.56\t34.19",
            "76XL20091101\t2455138\t02112009\t05:49:00\t-170.39\t-53.11\t372.4992\t383.86\t0\t8.62\t34.19",
            "76XL20091101\t2455138\t02112009\t05:59:00\t-170.36\t-53.13\t373.8048\t383.94\t0\t8.61\t34.2",
            "76XL20091101\t2455138\t02112009\t07:49:00\t-170.04\t-53.33\t371.5968\t384.64\t0\t8.73\t34.27",
            "76XL20091101\t2455138\t02112009\t08:19:00\t-169.55\t-53.38\t376.176\t384.48\t0\t8.62\t34.25",
            "76XL20091101\t2455138\t02112009\t08:59:00\t-169.43\t-53.45\t374.5632\t384.22\t0\t8.4\t34.2",
            "76XL20091101\t2455138\t02112009\t09:39:00\t-169.31\t-53.52\t377.4624\t384.13\t0\t8.34\t34.2",
            "76XL20091101\t2455138\t02112009\t10:49:00\t-169.1\t-54.04\t381.9936\t384.82\t0\t7.74\t34.12",
            "76XL20091101\t2455138\t02112009\t11:29:00\t-168.58\t-54.11\t382.2912\t385.28\t0\t7.61\t34.12",
            "76XL20091101\t2455138\t02112009\t11:59:00\t-168.49\t-54.16\t385.1808\t384.82\t0\t6.82\t33.98",
            "76XL20091101\t2455138\t02112009\t12:09:00\t-168.46\t-54.17\t385.7376\t384.7\t0\t6.52\t33.94",
            "76XL20091101\t2455138\t02112009\t12:39:00\t-168.36\t-54.22\t385.1136\t384.24\t0\t6.5\t33.92",
            "76XL20091101\t2455138\t02112009\t14:19:00\t-168.06\t-54.4\t383.5968\t383.55\t0\t6.51\t33.97",
            "76XL20091101\t2455138\t02112009\t14:49:00\t-167.57\t-54.45\t380.9184\t383.63\t0\t7.12\t34.08",
            "76XL20091101\t2455138\t02112009\t14:59:00\t-167.54\t-54.47\t381.456\t383.67\t0\t7.1\t34.08",
            "76XL20091101\t2455138\t02112009\t15:29:00\t-167.44\t-54.52\t384.5376\t383.75\t0\t6.46\t33.97",
            "76XL20091101\t2455138\t02112009\t15:59:00\t-167.35\t-54.57\t383.5872\t383.8\t0\t6.06\t33.91",
            "76XL20091101\t2455138\t02112009\t16:09:00\t-167.32\t-54.59\t383.0496\t383.81\t0\t6.07\t33.91",
            "76XL20091101\t2455138\t02112009\t16:39:00\t-167.23\t-55.05\t382.3392\t383.85\t0\t6.07\t33.9",
            "76XL20091101\t2455138\t02112009\t17:59:00\t-166.57\t-55.19\t381.0912\t383.88\t0\t6.01\t33.88",
            "76XL20091101\t2455138\t02112009\t18:29:00\t-166.48\t-55.25\t381.0144\t383.83\t0\t6.02\t33.88",
            "76XL20091101\t2455138\t02112009\t19:09:00\t-166.36\t-55.32\t381.648\t383.78\t0\t6.02\t33.88",
            "76XL20091101\t2455138\t02112009\t19:39:00\t-166.26\t-55.38\t381.552\t383.76\t0\t6.02\t33.91",
            "76XL20091101\t2455138\t02112009\t19:49:00\t-166.23\t-55.4\t381.1392\t383.78\t0\t6.01\t33.91",
            "76XL20091101\t2455138\t02112009\t20:59:00\t-166.08\t-55.48\t379.4976\t383.97\t0\t5.98\t33.91",
            "76XL20091101\t2455138\t02112009\t21:39:00\t-165.59\t-55.53\t381.7728\t384.08\t0\t5.97\t33.91",
            "76XL20091101\t2455138\t02112009\t22:09:00\t-165.49\t-55.59\t381.888\t384.19\t0\t6.03\t33.91",
            "76XL20091101\t2455138\t02112009\t22:19:00\t-165.46\t-56.01\t383.4912\t384.22\t0\t6.02\t33.9",
            "76XL20091101\t2455138\t02112009\t22:49:00\t-165.37\t-56.06\t383.7696\t384.34\t0\t6.05\t33.89",
            "76XL20091101\t2455138\t02112009\t23:59:00\t-165.14\t-56.19\t384.2112\t384.25\t0\t6.04\t33.88",
            "76XL20091101\t2455139\t03112009\t00:09:00\t-165.1\t-56.21\t384.192\t384.19\t0\t6.05\t33.88",
            "76XL20091101\t2455139\t03112009\t00:39:00\t-165\t-56.26\t384.528\t383.93\t0\t6\t33.88",
            "76XL20091101\t2455139\t03112009\t01:19:00\t-164.48\t-56.33\t388.2144\t383.61\t0\t5.89\t33.94",
            "76XL20091101\t2455139\t03112009\t01:49:00\t-164.37\t-56.39\t386.5632\t383.49\t0\t5.74\t33.94",
            "76XL20091101\t2455139\t03112009\t01:59:00\t-164.34\t-56.4\t386.112\t383.5\t973.86\t5.72\t33.93",
            "76XL20091101\t2455139\t03112009\t03:09:00\t-164.11\t-56.53\t384.9888\t383.55\t973.71\t5.29\t33.88",
            "76XL20091101\t2455139\t03112009\t03:19:00\t-164.09\t-56.55\t384.3552\t383.56\t973.94\t5.11\t33.87",
            "76XL20091101\t2455139\t03112009\t04:09:00\t-163.53\t-57.04\t383.3952\t383.63\t974.02\t5.05\t33.86",
            "76XL20091101\t2455139\t03112009\t04:19:00\t-163.49\t-57.06\t383.4528\t383.64\t974.17\t5\t33.86",
            "76XL20091101\t2455139\t03112009\t05:09:00\t-163.33\t-57.14\t381.9072\t383.72\t974.55\t4.9\t33.85",
            "76XL20091101\t2455139\t03112009\t06:49:00\t-163.01\t-57.32\t389.0976\t383.9\t974.65\t4.47\t33.83",
            "76XL20091101\t2455139\t03112009\t06:59:00\t-162.58\t-57.34\t389.664\t383.92\t974.87\t4.45\t33.83",
            "76XL20091101\t2455139\t03112009\t07:49:00\t-162.42\t-57.43\t380.9376\t384.06\t974.94\t4.28\t33.84",
            "76XL20091101\t2455139\t03112009\t07:59:00\t-162.39\t-57.44\t378.6336\t384.12\t974.54\t4.22\t33.84",
            "76XL20091101\t2455139\t03112009\t09:09:00\t-162.16\t-57.57\t384.6816\t384.72\t974.93\t4.1\t33.84",
            "76XL20091101\t2455139\t03112009\t09:19:00\t-162.12\t-57.59\t385.1712\t384.85\t975\t4.12\t33.84",
            "76XL20091101\t2455139\t03112009\t10:09:00\t-161.56\t-58.07\t385.68\t385.09\t975.29\t3.82\t33.85",
            "76XL20091101\t2455139\t03112009\t10:19:00\t-161.52\t-58.09\t385.4976\t385.07\t975.21\t3.83\t33.85",
            "76XL20091101\t2455139\t03112009\t11:09:00\t-161.36\t-58.18\t381.0144\t385\t975.63\t3.69\t33.86",
            "76XL20091101\t2455139\t03112009\t11:19:00\t-161.33\t-58.2\t381.2832\t384.98\t975.96\t3.71\t33.85",
            "76XL20091101\t2455139\t03112009\t12:39:00\t-161.05\t-58.34\t386.7456\t384.43\t976.4\t3.4\t33.85",
            "76XL20091101\t2455139\t03112009\t12:49:00\t-161.02\t-58.36\t385.2192\t384.26\t976.2\t3.51\t33.85",
            "76XL20091101\t2455139\t03112009\t12:59:00\t-160.59\t-58.38\t380.8704\t384.17\t976.58\t3.53\t33.85",
            "76XL20091101\t2455139\t03112009\t13:29:00\t-160.48\t-58.43\t383.7216\t383.83\t977.38\t3.6\t33.85",
            "76XL20091101\t2455139\t03112009\t13:39:00\t-160.45\t-58.45\t383.328\t383.74\t977.33\t3.59\t33.85",
            "76XL20091101\t2455139\t03112009\t13:49:00\t-160.41\t-58.46\t383.8944\t383.57\t977.74\t3.54\t33.85",
            "76XL20091101\t2455139\t03112009\t15:09:00\t-160.12\t-58.6\t384.9984\t384.99\t979.09\t2.69\t33.83",
            "76XL20091101\t2455139\t03112009\t15:19:00\t-160.08\t-59.01\t384.5088\t385.27\t978.94\t2.63\t33.83",
            "76XL20091101\t2455139\t03112009\t16:09:00\t-159.49\t-59.1\t384.1152\t385.76\t980.6\t2.67\t33.84",
            "76XL20091101\t2455139\t03112009\t16:19:00\t-159.46\t-59.12\t384.4992\t385.68\t980.83\t2.56\t33.84",
            "76XL20091101\t2455139\t03112009\t17:09:00\t-159.27\t-59.2\t384.096\t385.44\t981.8\t2.31\t33.86",
            "76XL20091101\t2455139\t03112009\t17:19:00\t-159.23\t-59.21\t384.8544\t385.36\t982.3\t2.32\t33.87",
            "76XL20091101\t2455139\t03112009\t18:29:00\t-158.58\t-59.32\t382.8288\t384.95\t983.31\t1.38\t33.84",
            "76XL20091101\t2455139\t03112009\t18:39:00\t-158.54\t-59.34\t381.8112\t384.9\t983.8\t1.37\t33.84",
            "76XL20091101\t2455139\t03112009\t18:49:00\t-158.51\t-59.35\t381.2832\t384.8\t983.69\t1.5\t33.86",
            "76XL20091101\t2455139\t03112009\t19:29:00\t-158.36\t-59.42\t382.512\t384.54\t984.7\t1.9\t33.89",
            "76XL20091101\t2455139\t03112009\t19:39:00\t-158.33\t-59.44\t383.3472\t384.49\t984.58\t1.87\t33.89",
            "76XL20091101\t2455139\t03112009\t19:49:00\t-158.29\t-59.45\t383.3184\t384.39\t985.1\t1.76\t33.89",
            "76XL20091101\t2455139\t03112009\t21:19:00\t-158.05\t-59.56\t383.9232\t384.24\t986.85\t1.43\t33.87",
            "76XL20091101\t2455139\t03112009\t22:09:00\t-158.05\t-59.56\t383.9232\t384.24\t987.48\t1.17\t33.85",
            "76XL20091101\t2455139\t03112009\t22:19:00\t-158.05\t-59.56\t383.9232\t384.24\t987.12\t1.16\t33.84",
            "76XL20091101\t2455139\t03112009\t23:09:00\t-158.05\t-59.56\t383.9232\t384.24\t988.03\t1.04\t33.87",
            "76XL20091101\t2455139\t03112009\t23:19:00\t-158.05\t-59.56\t383.9232\t384.24\t988.17\t1.07\t33.88",
            "76XL20091101\t2455140\t04112009\t00:39:00\t-156.45\t-60.33\t393.552\t384.23\t989.11\t0.97\t33.96",
            "76XL20091101\t2455140\t04112009\t00:39:30\t-156.45\t-60.33\t391.7856\t384.2\t989.11\t0.97\t33.96",
            "76XL20091101\t2455140\t04112009\t00:49:00\t-156.45\t-60.33\t393.552\t384.23\t988.82\t0.88\t33.95",
            "76XL20091101\t2455140\t04112009\t00:49:30\t-156.45\t-60.33\t391.7856\t384.2\t988.82\t0.88\t33.95",
            "76XL20091101\t2455140\t04112009\t01:29:00\t-156.26\t-60.38\t388.56\t384.13\t989.39\t0.84\t33.94",
            "76XL20091101\t2455140\t04112009\t01:39:00\t-156.22\t-60.39\t389.3568\t384.14\t989.56\t0.81\t33.95",
            "76XL20091101\t2455140\t04112009\t01:49:00\t-156.22\t-60.39\t389.3568\t384.14\t989.71\t0.73\t33.95",
            "76XL20091101\t2455140\t04112009\t02:59:00\t-156.22\t-60.39\t389.3568\t384.14\t990.36\t0.57\t33.97",
            "76XL20091101\t2455140\t04112009\t03:09:00\t-156.22\t-60.39\t389.3568\t384.14\t990.37\t0.56\t33.97",
            "76XL20091101\t2455140\t04112009\t03:19:00\t-156.22\t-60.39\t389.3568\t384.14\t990.34\t0.57\t33.97",
            "76XL20091101\t2455140\t04112009\t03:59:00\t-155.17\t-60.57\t405.1392\t384.22\t990.31\t0.56\t34.02",
            "76XL20091101\t2455140\t04112009\t13:39:00\t-151.01\t-62.08\t389.232\t384.25\t995.02\t-0.02\t34.02",
            "76XL20091101\t2455140\t04112009\t13:49:00\t-151.01\t-62.08\t389.232\t384.25\t995.17\t-0.03\t34.02",
            "76XL20091101\t2455140\t04112009\t14:19:00\t-151.01\t-62.08\t389.232\t384.25\t995.27\t-0.02\t34.01",
            "76XL20091101\t2455140\t04112009\t14:29:00\t-151.01\t-62.08\t389.232\t384.25\t995.5\t-0.04\t34",
            "76XL20091101\t2455140\t04112009\t15:09:00\t-151.01\t-62.08\t389.232\t384.25\t995.92\t-0.09\t33.99",
            "76XL20091101\t2455140\t04112009\t15:59:00\t-151.01\t-62.08\t389.232\t384.25\t996.16\t-0.12\t33.97",
            "76XL20091101\t2455140\t04112009\t16:09:00\t-151.01\t-62.08\t389.232\t384.25\t996.15\t-0.12\t33.97",
            "76XL20091101\t2455140\t04112009\t16:59:00\t-151.01\t-62.08\t389.232\t384.25\t996.89\t-0.17\t33.93",
            "76XL20091101\t2455140\t04112009\t17:09:00\t-151.01\t-62.08\t389.232\t384.25\t997.12\t-0.18\t33.93",
            "76XL20091101\t2455140\t04112009\t18:19:00\t-151.01\t-62.08\t389.232\t384.25\t998.3\t-0.22\t33.92",
            "76XL20091101\t2455140\t04112009\t18:29:00\t-151.01\t-62.08\t389.232\t384.25\t998.62\t-0.22\t33.92",
            "76XL20091101\t2455140\t04112009\t18:39:00\t-151.01\t-62.08\t389.232\t384.25\t998.84\t-0.21\t33.91",
            "76XL20091101\t2455140\t04112009\t19:19:00\t-151.01\t-62.08\t389.232\t384.25\t999.02\t-0.21\t33.9",
            "76XL20091101\t2455140\t04112009\t19:29:00\t-151.01\t-62.08\t389.232\t384.25\t998.55\t-0.2\t33.89",
            "76XL20091101\t2455140\t04112009\t20:59:00\t-147.47\t-62.58\t361.6032\t383.29\t998.73\t-0.21\t33.89",
            "76XL20091101\t2455140\t04112009\t21:09:00\t-147.47\t-62.58\t361.6032\t383.29\t998.58\t-0.21\t33.89",
            "76XL20091101\t2455140\t04112009\t21:59:00\t-147.47\t-62.58\t361.6032\t383.29\t998.47\t-0.22\t33.89",
            "76XL20091101\t2455140\t04112009\t22:09:00\t-147.47\t-62.58\t361.6032\t383.29\t997.92\t-0.22\t33.89",
            "76XL20091101\t2455140\t04112009\t22:59:00\t-147.47\t-62.58\t361.6032\t383.29\t997.66\t-0.22\t33.87",
            "76XL20091101\t2455140\t04112009\t23:09:00\t-147.47\t-62.58\t361.6032\t383.29\t997.98\t-0.22\t33.86",
            "76XL20091101\t2455141\t05112009\t00:19:00\t-147.47\t-62.58\t361.6032\t383.29\t997.21\t-0.36\t33.83",
            "76XL20091101\t2455141\t05112009\t00:29:00\t-147.47\t-62.58\t361.6032\t383.29\t997.28\t-0.41\t33.83",
            "76XL20091101\t2455141\t05112009\t01:19:00\t-147.47\t-62.58\t361.6032\t383.29\t997.15\t-0.42\t33.85",
            "76XL20091101\t2455141\t05112009\t01:29:00\t-147.47\t-62.58\t361.6032\t383.29\t997.48\t-0.39\t33.86",
            "76XL20091101\t2455141\t05112009\t03:09:00\t-147.47\t-62.58\t361.6032\t383.29\t997.37\t-0.51\t33.82",
            "76XL20091101\t2455141\t05112009\t03:59:00\t-136.48\t-62.38\t371.0496\t383.67\t997.73\t-0.49\t33.81",
            "76XL20091101\t2455141\t05112009\t03:59:30\t-147.47\t-62.58\t361.6032\t383.29\t997.73\t-0.49\t33.81",
            "76XL20091101\t2455141\t05112009\t04:09:00\t-136.43\t-62.38\t371.6832\t383.59\t996.84\t-0.51\t33.81",
            "76XL20091101\t2455141\t05112009\t04:09:30\t-147.47\t-62.58\t361.6032\t383.29\t996.84\t-0.51\t33.81",
            "76XL20091101\t2455141\t05112009\t04:40:00\t-136.26\t-62.38\t371.7408\t383.24\t997.83\t-0.5\t33.81",
            "76XL20091101\t2455141\t05112009\t04:49:00\t-147.47\t-62.58\t361.6032\t383.29\t997.72\t-0.53\t33.81",
            "76XL20091101\t2455141\t05112009\t04:59:00\t-136.16\t-62.38\t371.136\t382.98\t997.75\t-0.54\t33.81",
            "76XL20091101\t2455141\t05112009\t04:59:30\t-147.47\t-62.58\t361.6032\t383.29\t997.75\t-0.54\t33.81",
            "76XL20091101\t2455141\t05112009\t05:09:00\t-147.47\t-62.58\t361.6032\t383.29\t997.92\t-0.54\t33.8",
            "76XL20091101\t2455141\t05112009\t05:20:00\t-136.04\t-62.37\t370.6944\t383.04\t997.42\t-0.56\t33.79",
            "76XL20091101\t2455141\t05112009\t06:10:00\t-135.39\t-62.34\t370.7232\t383.26\t997.32\t-0.58\t33.79",
            "76XL20091101\t2455141\t05112009\t06:19:00\t-135.35\t-62.34\t370.8864\t383.29\t997.33\t-0.61\t33.8",
            "76XL20091101\t2455141\t05112009\t06:19:30\t-147.47\t-62.58\t361.6032\t383.29\t997.33\t-0.61\t33.8",
            "76XL20091101\t2455141\t05112009\t06:29:00\t-135.3\t-62.34\t370.416\t383.35\t996.79\t-0.6\t33.79",
            "76XL20091101\t2455141\t05112009\t06:29:30\t-147.47\t-62.58\t361.6032\t383.29\t996.79\t-0.6\t33.79",
            "76XL20091101\t2455141\t05112009\t07:19:00\t-147.47\t-62.58\t361.6032\t383.29\t996.32\t-0.57\t33.79",
            "76XL20091101\t2455141\t05112009\t07:29:00\t-135.04\t-62.34\t375.2928\t383.47\t995.67\t-0.58\t33.79",
            "76XL20091101\t2455141\t05112009\t07:29:30\t-147.47\t-62.58\t361.6032\t383.29\t995.67\t-0.58\t33.79",
            "76XL20091101\t2455141\t05112009\t08:00:00\t-134.5\t-62.34\t373.728\t383.46\t996.24\t-0.61\t33.78",
            "76XL20091101\t2455141\t05112009\t08:10:00\t-134.46\t-62.34\t374.2656\t383.45\t995.97\t-0.61\t33.78",
            "76XL20091101\t2455141\t05112009\t08:19:00\t-134.42\t-62.34\t372.96\t383.45\t996.21\t-0.62\t33.78",
            "76XL20091101\t2455141\t05112009\t08:19:30\t-147.47\t-62.58\t361.6032\t383.29\t996.21\t-0.62\t33.78",
            "76XL20091101\t2455141\t05112009\t08:59:00\t-134.23\t-62.34\t372.7008\t383.44\t995.54\t-0.64\t33.78",
            "76XL20091101\t2455141\t05112009\t08:59:30\t-147.47\t-62.58\t361.6032\t383.29\t995.54\t-0.64\t33.78",
            "76XL20091101\t2455141\t05112009\t09:20:00\t-134.13\t-62.35\t374.1504\t383.43\t994.98\t-0.64\t33.77",
            "76XL20091101\t2455141\t05112009\t09:39:00\t-134.04\t-62.35\t372.8256\t383.35\t995.16\t-0.64\t33.75",
            "76XL20091101\t2455141\t05112009\t09:39:30\t-147.47\t-62.58\t361.6032\t383.29\t995.16\t-0.64\t33.75",
            "76XL20091101\t2455141\t05112009\t09:49:00\t-147.47\t-62.58\t361.6032\t383.29\t995.28\t-0.65\t33.73",
            "76XL20091101\t2455141\t05112009\t09:59:00\t-133.54\t-62.35\t371.3568\t383.23\t995.26\t-0.69\t33.72",
            "76XL20091101\t2455141\t05112009\t09:59:30\t-147.47\t-62.58\t361.6032\t383.29\t995.26\t-0.69\t33.72",
            "76XL20091101\t2455141\t05112009\t10:39:00\t-133.38\t-62.36\t374.112\t383.02\t994.71\t-0.66\t33.71",
            "76XL20091101\t2455141\t05112009\t10:39:30\t-147.47\t-62.58\t361.6032\t383.29\t994.71\t-0.66\t33.71",
            "76XL20091101\t2455141\t05112009\t10:49:00\t-133.33\t-62.36\t374.8032\t382.94\t994.67\t-0.66\t33.71",
            "76XL20091101\t2455141\t05112009\t10:49:30\t-147.47\t-62.58\t361.6032\t383.29\t994.67\t-0.66\t33.71",
            "76XL20091101\t2455141\t05112009\t10:59:00\t-147.47\t-62.58\t361.6032\t383.29\t994.71\t-0.66\t33.72",
            "76XL20091101\t2455141\t05112009\t12:19:00\t-147.47\t-62.58\t361.6032\t383.29\t995.32\t-0.47\t33.71",
            "76XL20091101\t2455141\t05112009\t12:29:00\t-147.47\t-62.58\t361.6032\t383.29\t995.76\t-0.69\t33.71",
            "76XL20091101\t2455141\t05112009\t13:09:00\t-132.25\t-62.36\t369.792\t383.06\t996.24\t-0.69\t33.72",
            "76XL20091101\t2455141\t05112009\t13:09:30\t-147.47\t-62.58\t361.6032\t383.29\t996.24\t-0.69\t33.72",
            "76XL20091101\t2455141\t05112009\t13:19:00\t-132.21\t-62.36\t370.6944\t383.12\t996.68\t-0.69\t33.73",
            "76XL20091101\t2455141\t05112009\t13:19:30\t-147.47\t-62.58\t361.6032\t383.29\t996.68\t-0.69\t33.73",
            "76XL20091101\t2455141\t05112009\t13:29:00\t-132.16\t-62.36\t371.1744\t383.23\t997.22\t-0.69\t33.73",
            "76XL20091101\t2455141\t05112009\t13:29:30\t-147.47\t-62.58\t361.6032\t383.29\t997.22\t-0.69\t33.73",
            "76XL20091101\t2455141\t05112009\t14:39:00\t-147.47\t-62.58\t361.6032\t383.29\t999.28\t-0.73\t33.69",
            "76XL20091101\t2455141\t05112009\t14:42:00\t-131.38\t-62.36\t364.1856\t383.66\t999.53\t-0.73\t33.69",
            "76XL20091101\t2455141\t05112009\t14:49:00\t-131.35\t-62.36\t363.9744\t383.63\t999.87\t-0.73\t33.7",
            "76XL20091101\t2455141\t05112009\t14:49:30\t-147.47\t-62.58\t361.6032\t383.29\t999.87\t-0.73\t33.7",
            "76XL20091101\t2455141\t05112009\t14:59:00\t-131.29\t-62.36\t364.1472\t383.59\t1000.57\t-0.73\t33.69",
            "76XL20091101\t2455141\t05112009\t14:59:30\t-147.47\t-62.58\t361.6032\t383.29\t1000.57\t-0.73\t33.69",
            "76XL20091101\t2455141\t05112009\t15:39:00\t-131.08\t-62.36\t364.0128\t383.47\t1002.14\t-0.73\t33.7",
            "76XL20091101\t2455141\t05112009\t15:39:30\t-147.47\t-62.58\t361.6032\t383.29\t1002.14\t-0.73\t33.7",
            "76XL20091101\t2455141\t05112009\t15:49:00\t-131.02\t-62.36\t365.1648\t383.45\t1002.84\t-0.72\t33.71",
            "76XL20091101\t2455141\t05112009\t15:49:30\t-147.47\t-62.58\t361.6032\t383.29\t1002.84\t-0.72\t33.71",
            "76XL20091101\t2455141\t05112009\t15:59:00\t-130.57\t-62.36\t366.4416\t383.4\t1002.62\t-0.73\t33.71",
            "76XL20091101\t2455141\t05112009\t15:59:30\t-147.47\t-62.58\t361.6032\t383.29\t1002.62\t-0.73\t33.71",
            "76XL20091101\t2455141\t05112009\t16:11:00\t-130.51\t-62.36\t365.5872\t383.38\t1002.93\t-0.73\t33.71",
            "76XL20091101\t2455141\t05112009\t16:39:00\t-130.36\t-62.35\t362.6688\t383.3\t1003.13\t-0.74\t33.63",
            "76XL20091101\t2455141\t05112009\t16:39:30\t-147.47\t-62.58\t361.6032\t383.29\t1003.13\t-0.74\t33.63",
            "76XL20091101\t2455141\t05112009\t16:49:00\t-147.47\t-62.58\t361.6032\t383.29\t1003.6\t-0.73\t33.61",
            "76XL20091101\t2455141\t05112009\t16:49:30\t-130.31\t-62.35\t361.6032\t383.27\t1003.6\t-0.73\t33.61",
            "76XL20091101\t2455141\t05112009\t16:59:00\t-147.47\t-62.58\t361.6032\t383.29\t1004.56\t-0.75\t33.65",
            "76XL20091101\t2455141\t05112009\t16:59:30\t-130.25\t-62.34\t361.6512\t383.25\t1004.56\t-0.75\t33.65",
            "76XL20091101\t2455141\t05112009\t18:19:00\t-147.47\t-62.58\t361.6032\t383.29\t1007.22\t-0.76\t33.64",
            "76XL20091101\t2455141\t05112009\t18:19:30\t-129.41\t-62.35\t366.9792\t383.13\t1007.22\t-0.76\t33.64",
            "76XL20091101\t2455141\t05112009\t18:29:00\t-129.36\t-62.35\t381.8496\t383.15\t1007.67\t-0.73\t33.69",
            "76XL20091101\t2455141\t05112009\t18:29:30\t-138.5\t-62.45\t389.7984\t383.16\t1007.67\t-0.73\t33.7",
            "76XL20091101\t2455141\t05112009\t19:00:00\t-129.19\t-62.34\t389.2224\t383.22\t1009.17\t-0.76\t33.64",
            "76XL20091101\t2455141\t05112009\t19:19:00\t-138.5\t-62.45\t389.7984\t383.16\t1009.43\t-0.72\t33.71",
            "76XL20091101\t2455141\t05112009\t19:29:00\t-138.5\t-62.45\t389.7984\t383.16\t1009.57\t-0.73\t33.71",
            "76XL20091101\t2455141\t05112009\t20:19:00\t-138.5\t-62.45\t389.7984\t383.16\t1010.9\t-0.74\t33.63",
            "76XL20091101\t2455141\t05112009\t20:59:00\t-138.5\t-62.45\t389.7984\t383.16\t1012.01\t-0.73\t33.61",
            "76XL20091101\t2455141\t05112009\t21:39:00\t-138.5\t-62.45\t389.7984\t383.16\t1012.89\t-0.75\t33.65",
            "76XL20091101\t2455141\t05112009\t21:49:00\t-138.5\t-62.45\t389.7984\t383.16\t1012.91\t-0.76\t33.64",
            "76XL20091101\t2455141\t05112009\t21:59:00\t-138.5\t-62.45\t389.7984\t383.16\t1013.05\t-0.73\t33.61",
            "76XL20091101\t2455141\t05112009\t22:39:00\t-138.5\t-62.45\t389.7984\t383.16\t1014.49\t-0.75\t33.65",
            "76XL20091101\t2455142\t06112009\t04:49:00\t-124.05\t-63.07\t366.3264\t383.04\t1022.32\t-0.41\t33.54",
            "76XL20091101\t2455142\t06112009\t06:12:00\t-123.2\t-63.11\t360.72\t382.76\t1024.41\t-0.5\t33.49",
            "76XL20091101\t2455142\t06112009\t06:19:00\t-123.17\t-63.12\t359.9808\t382.77\t1024.3\t-0.51\t33.48",
            "76XL20091101\t2455142\t06112009\t07:00:00\t-122.55\t-63.14\t366.7584\t382.78\t1025.27\t0.33\t33.08",
            "76XL20091101\t2455142\t06112009\t07:19:00\t-122.45\t-63.15\t414.4224\t382.79\t1025.78\t3.3\t31.88",
            "76XL20091101\t2455142\t06112009\t09:29:00\t-121.37\t-63.24\t396.576\t383.28\t1027.48\t13.5\t31.13"
    };

}
