package gov.noaa.pmel.dashboard.test.actualdatamodified;

import gov.noaa.pmel.dashboard.actions.DatasetChecker;
import gov.noaa.pmel.dashboard.actions.DatasetSubmitter;
import gov.noaa.pmel.dashboard.actions.OmePdfGenerator;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.qc.QCEvent;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.MetadataUploadService;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Test for submitting a dataset with OME metadata
 */
public class SubmitProcessTest {

    /**
     * Test for submitting a dataset with OME metadata
     */
    @Test
    public void testSubmitProcess() {
        System.setProperty("CATALINA_BASE", System.getenv("HOME"));
        System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
        // Get the default dashboard configuration
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        DataFileHandler datasetHandler = configStore.getDataFileHandler();
        MetadataFileHandler metaFileHandler = configStore.getMetadataFileHandler();
        DatasetChecker dataChecker = configStore.getDashboardDatasetChecker();
        OmePdfGenerator omePdfGenerator = configStore.getOmePdfGenerator();
        String version = configStore.getUploadVersion();
        DatasetSubmitter datasetSubmitter = configStore.getDashboardDatasetSubmitter();
        DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
        DatabaseRequestHandler dbHandler = configStore.getDatabaseRequestHandler();

        DashboardDatasetData dsetData = null;
        DatasetQCStatus expectedStatus = new DatasetQCStatus();

        try {
            StringBuilder strBuilder = new StringBuilder();
            for (String str : TSV_DATA_STRINGS) {
                strBuilder.append(str);
            }
            StringReader reader = new StringReader(strBuilder.toString());
            try {
                dsetData = datasetHandler.assignDatasetDataFromInput(null, reader,
                        DashboardUtils.TAB_FORMAT_TAG, USERNAME, 0, -1);
                // The following are done by the DataUploadService (as well as creating an OME.xml stub)
                dsetData.setUploadFilename(FILENAME);
                dsetData.setUploadTimestamp(TIMESTAMP);
                expectedStatus.setPiSuggested(DatasetQCStatus.Status.ACCEPTED_A);
                expectedStatus.addComment("PI-recommended QC flag: A");
                dsetData.setSubmitStatus(expectedStatus);
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            System.err.println("Problems interpreting/adding the data for " + EXPOCODE + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        try {
            String msg = "Adding test dataset " + EXPOCODE;
            dsetData.setDataColTypes(DATA_COLUMN_TYPES);
            datasetHandler.saveDatasetInfoToFile(dsetData, msg);
            datasetHandler.saveDatasetDataToFile(dsetData, msg);
            dataChecker.standardizeDataset(dsetData, null);
        } catch ( Exception ex ) {
            System.err.println("Problems with the data check for " + EXPOCODE + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        assertEquals(DashboardUtils.CHECK_STATUS_ACCEPTABLE, dsetData.getDataCheckStatus());
        assertEquals(expectedStatus, dsetData.getSubmitStatus());

        DashboardMetadata metadata = null;
        try {
            StringBuilder strBuilder = new StringBuilder();
            for (String str : OME_METADATA_XML_STRING) {
                strBuilder.append(str);
            }
            {
                final StringReader reader = new StringReader(strBuilder.toString());
                InputStream inputStream = new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return reader.read();
                    }
                };
                try {
                    metaFileHandler.saveMetadataInputStream(EXPOCODE, USERNAME,
                            DashboardUtils.OME_FILENAME, TIMESTAMP, version, inputStream, true);
                } finally {
                    reader.close();
                }
            }
            {
                final StringReader reader = new StringReader(strBuilder.toString());
                InputStream inputStream = new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return reader.read();
                    }
                };
                try {
                    metadata = metaFileHandler.saveMetadataInputStream(EXPOCODE, USERNAME,
                            DashboardUtils.PI_OME_FILENAME, TIMESTAMP, version, inputStream, true);
                } finally {
                    reader.close();
                }
            }
        } catch ( Exception ex ) {
            System.err.println("Problems saving the metadata for " + EXPOCODE + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        DashboardDataset dataset = null;
        try {
            dataset = MetadataUploadService.processOmeMetadata(EXPOCODE, metadata,
                    metaFileHandler, datasetHandler, omePdfGenerator);
        } catch ( Exception ex ) {
            System.err.println("Problems interpreting the 'PI_OME.xml' for " +
                    EXPOCODE + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        expectedStatus.setAutoSuggested(DatasetQCStatus.Status.ACCEPTED_B);
        expectedStatus.addComment("(from automated QC) Accuracy of aqueous CO2 less than 2 uatm.  " +
                "Accuracy of temperature measurements 0.05 deg C or less.  " +
                "Accuracy of pressure measurements 2.0 hPa or less " +
                "(no attempt was made to adjust accuracy for differential pressure instruments).  " +
                "4 calibration gasses, 3 of which have non-zero concentrations.  " +
                "No attempt was made to find high-quality crossovers.");
        assertEquals(expectedStatus, dataset.getSubmitStatus());

        // Ideally this should detect that it completely overlaps with 33GG20181110
        try {
            datasetSubmitter.submitDatasets(Collections.singleton(EXPOCODE),
                    DashboardUtils.ARCHIVE_STATUS_WITH_NEXT_RELEASE, TIMESTAMP, false, USERNAME);
        } catch ( Exception ex ) {
            System.err.println("Problems submitting " + EXPOCODE + " for QC: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        dataset = datasetHandler.getDatasetFromInfoFile(EXPOCODE);
        expectedStatus.setActual(DatasetQCStatus.Status.NEW_AWAITING_QC);
        expectedStatus.addComment("Initial QC flag for new dataset");
        assertEquals(expectedStatus, dataset.getSubmitStatus());

        String comment = "suspending test dataset";
        try {
            DatasetQCStatus flag = dataset.getSubmitStatus();
            flag.setActual(DatasetQCStatus.Status.SUSPENDED);
            flag.addComment(comment);
            dataset.setSubmitStatus(flag);
            QCEvent qc = new QCEvent();
            qc.setUsername(USERNAME);
            qc.setFlagValue(flag.flagString());
            qc.setFlagDate(new Date());
            qc.setVersion(version);
            qc.setRegionId(DashboardUtils.REGION_ID_GLOBAL);
            qc.setComment(comment);
            qc.setDatasetId(EXPOCODE);
            dbHandler.addDatasetQCEvents(Collections.singletonList(qc));
            datasetHandler.saveDatasetInfoToFile(dataset, comment);
            dsgHandler.updateDatasetQCFlagAndVersionStatus(EXPOCODE, flag.flagString(), version + "N");
        } catch ( Exception ex ) {
            System.err.println("Problems suspending " + EXPOCODE + " from QC: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        dataset = datasetHandler.getDatasetFromInfoFile(EXPOCODE);
        expectedStatus.setActual(DatasetQCStatus.Status.SUSPENDED);
        expectedStatus.addComment(comment);
        assertEquals(expectedStatus, dataset.getSubmitStatus());

        try {
            // Remove the data and metadata files
            // The database will still have changes, and the parent directories will still exist
            datasetHandler.deleteDatasetFiles(EXPOCODE, USERNAME, true);
            // Remove the DSG files
            DsgNcFile dsgFile = dsgHandler.getDsgNcFile(EXPOCODE);
            dsgFile.delete();
            dsgFile = dsgHandler.getDecDsgNcFile(EXPOCODE);
            dsgFile.delete();
            dsgHandler.flagErddap(true, true);
        } catch ( Exception ex ) {
            System.err.println("Problems deleting data files for " + EXPOCODE + ": " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static final String EXPOCODE = "00KS20181110";
    private static final String USERNAME = "k.smith";
    private static final String FILENAME = "00KS20181110.tsv";
    private static final String TIMESTAMP = "2019-10-09 15:00 +8:00";

    private static final ArrayList<DataColumnType> DATA_COLUMN_TYPES = new ArrayList<DataColumnType>(Arrays.asList(
            DashboardServerUtils.DATASET_ID.duplicate(),
            DashboardServerUtils.DAY_OF_YEAR.duplicate(),
            DashboardServerUtils.DATE.duplicate(),
            DashboardServerUtils.TIME_OF_DAY.duplicate(),
            DashboardServerUtils.LATITUDE.duplicate(),
            DashboardServerUtils.LONGITUDE.duplicate(),
            SocatTypes.XCO2_WATER_TEQU_DRY.duplicate(),
            SocatTypes.XCO2_ATM_DRY_ACTUAL.duplicate(),
            SocatTypes.XCO2_ATM_DRY_INTERP.duplicate(),
            SocatTypes.PEQU.duplicate(),
            SocatTypes.PATM.duplicate(),
            SocatTypes.TEQU.duplicate(),
            SocatTypes.SST.duplicate(),
            SocatTypes.SALINITY.duplicate(),
            SocatTypes.FCO2_WATER_SST_WET.duplicate(),
            SocatTypes.FCO2_ATM_DRY_INTERP.duplicate(),
            DashboardServerUtils.OTHER.duplicate(),
            SocatTypes.WOCE_CO2_WATER.duplicate(),
            SocatTypes.COMMENT_WOCE_CO2_WATER.duplicate()
    ));

    static {
        if ( !DATA_COLUMN_TYPES.get(2).setSelectedUnit("dd-mm-yyyy") )
            throw new RuntimeException("Failed to assign the DATE unit ofr DATA_COLUMN_TYPES");
    }

    private static final String[] TSV_DATA_STRINGS = new String[] {
            "Expocode: 00KS20181110\n",
            "Platform Name: Test Ship\n",
            "Platform Type: Ship\n",
            "Group: TESTERS\n",
            "Investigators: TESTERS\n",
            "PI QC: A\n",
            "\n",
            "Expocode\tYD_UTC\tDATE_UTC__ddmmyyyy\tTIME_UTC_hh:mm:ss\tLAT_dec_degree\tLONG_dec_degree\txCO2_EQU_ppm\txCO2_ATM_ppm\txCO2_ATM_interpolated_ppm\tPRES_EQU_hPa\tPRES_ATM@SSP_hPa\tTEMP_EQU_C\tSST_C\tSAL_permil\tfCO2_SW@SST_uatm\tfCO2_ATM_interpolated_uatm\tdfCO2_uatm\tWOCE_QC_FLAG\tQC_SUBFLAG\n",
            "00KS20181110\t314.78848\t10112018\t18:55:25\t30.26070\t-88.50960\t-999\t410.672\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t314.78946\t10112018\t18:56:49\t30.25680\t-88.50920\t-999\t418.825\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable air value\n",
            "00KS20181110\t314.79043\t10112018\t18:58:13\t30.25290\t-88.50880\t-999\t411.092\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t314.79141\t10112018\t18:59:38\t30.24900\t-88.50830\t-999\t411.554\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t314.79238\t10112018\t19:01:02\t30.24520\t-88.50780\t-999\t414.848\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable air value\n",
            "00KS20181110\t314.81806\t10112018\t19:38:00\t30.15630\t-88.53730\t381.774\t-999\t411.15\t1022.9\t1021.54\t23.39\t23.4173\t32.7353\t373.97\t401.71\t-27.74\t2\t\n",
            "00KS20181110\t314.81976\t10112018\t19:40:27\t30.15060\t-88.53360\t367.876\t-999\t411.15\t1022.1\t1021.66\t23.46\t23.4772\t32.8039\t359.87\t401.72\t-41.85\t2\t\n",
            "00KS20181110\t314.82147\t10112018\t19:42:55\t30.14480\t-88.52990\t358.550\t-999\t411.15\t1022.7\t1021.58\t23.50\t23.53\t32.8932\t351.13\t401.66\t-50.53\t2\t\n",
            "00KS20181110\t314.82316\t10112018\t19:45:21\t30.13920\t-88.52620\t351.667\t-999\t411.16\t1021.8\t1021.66\t23.54\t23.5758\t32.9429\t344.14\t401.66\t-57.53\t2\t\n",
            "00KS20181110\t314.82485\t10112018\t19:47:47\t30.13340\t-88.52260\t349.018\t-999\t411.16\t1022.5\t1021.63\t24.07\t23.638\t33.023\t334.79\t401.61\t-66.83\t2\t\n",
            "00KS20181110\t314.82655\t10112018\t19:50:14\t30.12830\t-88.51820\t349.294\t-999\t411.16\t1022.9\t1021.37\t24.24\t23.7149\t33.1728\t333.77\t401.46\t-67.69\t2\t\n",
            "00KS20181110\t314.82824\t10112018\t19:52:40\t30.12350\t-88.51330\t349.639\t-999\t411.16\t1022.8\t1020.98\t24.34\t23.8111\t33.3809\t333.95\t401.24\t-67.29\t2\t\n",
            "00KS20181110\t314.82994\t10112018\t19:55:07\t30.11870\t-88.50810\t349.217\t-999\t411.17\t1022.2\t1021.48\t24.43\t23.8937\t33.4816\t333.19\t401.39\t-68.19\t2\t\n",
            "00KS20181110\t314.83166\t10112018\t19:57:35\t30.11370\t-88.50320\t346.628\t-999\t411.17\t1022.1\t1021.51\t24.46\t23.9238\t33.4841\t330.67\t401.38\t-70.71\t2\t\n",
            "00KS20181110\t314.83336\t10112018\t20:00:02\t30.10870\t-88.49830\t344.597\t-999\t411.17\t1022.3\t1020.78\t24.48\t23.9428\t33.4839\t328.78\t401.08\t-72.30\t2\t\n",
            "00KS20181110\t314.83505\t10112018\t20:02:28\t30.10350\t-88.49390\t343.706\t-999\t411.17\t1021.7\t1021.37\t24.50\t23.9624\t33.4854\t327.71\t401.30\t-73.59\t2\t\n",
            "00KS20181110\t314.83676\t10112018\t20:04:56\t30.09800\t-88.48960\t343.796\t-999\t411.18\t1022.0\t1021.42\t24.50\t23.9529\t33.4622\t327.76\t401.33\t-73.57\t2\t\n",
            "00KS20181110\t314.83845\t10112018\t20:07:22\t30.09270\t-88.48520\t344.359\t-999\t411.18\t1022.2\t1021.44\t24.47\t23.929\t33.4219\t328.47\t401.36\t-72.89\t2\t\n",
            "00KS20181110\t314.84016\t10112018\t20:09:50\t30.08730\t-88.48080\t343.945\t-999\t411.18\t1022.9\t1020.77\t24.48\t23.928\t33.4184\t328.15\t401.09\t-72.95\t2\t\n",
            "00KS20181110\t314.84185\t10112018\t20:12:16\t30.08190\t-88.47650\t340.521\t-999\t411.18\t1022.9\t1021.23\t24.47\t23.9111\t33.382\t324.79\t401.29\t-76.50\t2\t\n",
            "00KS20181110\t314.84354\t10112018\t20:14:42\t30.07650\t-88.47220\t336.942\t-999\t411.19\t1023.2\t1021.4\t24.46\t23.888\t33.3627\t321.30\t401.38\t-80.08\t2\t\n",
            "00KS20181110\t314.84524\t10112018\t20:17:09\t30.07110\t-88.46800\t335.323\t-999\t411.19\t1022.2\t1020.83\t24.45\t23.8797\t33.34\t319.46\t401.16\t-81.69\t2\t\n",
            "00KS20181110\t314.84693\t10112018\t20:19:35\t30.06570\t-88.46370\t335.935\t-999\t411.19\t1021.1\t1021.44\t24.46\t23.8842\t33.3357\t319.61\t401.40\t-81.79\t2\t\n",
            "00KS20181110\t314.84865\t10112018\t20:22:03\t30.06030\t-88.45940\t340.488\t-999\t411.19\t1022.7\t1021.33\t24.49\t23.9118\t33.3645\t324.42\t401.34\t-76.92\t2\t\n",
            "00KS20181110\t314.85035\t10112018\t20:24:30\t30.05490\t-88.45500\t347.212\t-999\t411.20\t1022.0\t1020.8\t24.55\t23.982\t33.4569\t330.70\t401.08\t-70.38\t2\t\n",
            "00KS20181110\t314.85205\t10112018\t20:26:57\t30.04950\t-88.45070\t349.241\t-999\t411.20\t1022.1\t1020.78\t24.73\t24.1527\t33.5846\t332.43\t400.96\t-68.53\t2\t\n",
            "00KS20181110\t314.85374\t10112018\t20:29:23\t30.04410\t-88.44630\t357.910\t-999\t411.20\t1022.6\t1021.15\t24.91\t24.3622\t33.7292\t341.17\t400.96\t-59.80\t2\t\n",
            "00KS20181110\t314.85569\t10112018\t20:32:12\t30.03860\t-88.44060\t373.291\t-999\t411.21\t1022.6\t1020.67\t25.07\t24.5236\t33.8696\t355.74\t400.66\t-44.91\t2\t\n",
            "00KS20181110\t314.85740\t10112018\t20:34:39\t30.03380\t-88.43560\t390.178\t-999\t411.21\t1022.4\t1021.11\t25.23\t24.6689\t34.0708\t371.42\t400.73\t-29.31\t2\t\n",
            "00KS20181110\t314.85911\t10112018\t20:37:07\t30.02880\t-88.43060\t403.668\t-999\t411.21\t1022.5\t1020.84\t25.29\t24.7352\t34.1023\t384.36\t400.58\t-16.22\t2\t\n",
            "00KS20181110\t314.86080\t10112018\t20:39:33\t30.02410\t-88.42570\t409.488\t-999\t411.21\t1022.6\t1020.88\t25.32\t24.775\t34.129\t390.08\t400.57\t-10.49\t2\t\n",
            "00KS20181110\t314.86250\t10112018\t20:42:00\t30.01930\t-88.42070\t410.150\t-999\t411.22\t1022.3\t1021.21\t25.35\t24.806\t34.1203\t390.59\t400.68\t-10.09\t2\t\n",
            "00KS20181110\t314.86422\t10112018\t20:44:29\t30.01460\t-88.41550\t407.275\t-999\t411.22\t1022.6\t1021.06\t25.42\t24.8663\t34.1175\t387.76\t400.58\t-12.82\t2\t\n",
            "00KS20181110\t314.86591\t10112018\t20:46:55\t30.01010\t-88.41050\t401.986\t-999\t411.22\t1022.5\t1020.97\t25.45\t24.9018\t34.1223\t382.75\t400.52\t-17.77\t2\t\n",
            "00KS20181110\t314.86763\t10112018\t20:49:23\t30.00540\t-88.40530\t396.638\t-999\t411.22\t1021.2\t1021.09\t25.46\t24.8993\t34.1082\t376.96\t400.57\t-23.61\t2\t\n",
            "00KS20181110\t314.86933\t10112018\t20:51:50\t30.00070\t-88.40020\t392.444\t-999\t411.23\t1022.7\t1021.25\t25.46\t24.9003\t34.1117\t373.56\t400.64\t-27.09\t2\t\n",
            "00KS20181110\t314.87102\t10112018\t20:54:16\t29.99610\t-88.39510\t390.071\t-999\t411.23\t1022.4\t1021\t25.48\t24.917\t34.1323\t371.12\t400.53\t-29.41\t2\t\n",
            "00KS20181110\t314.87271\t10112018\t20:56:42\t29.99150\t-88.39010\t388.853\t-999\t411.23\t1022.5\t1020.83\t25.50\t24.9291\t34.1463\t369.86\t400.46\t-30.60\t2\t\n",
            "00KS20181110\t314.87443\t10112018\t20:59:11\t29.98680\t-88.38490\t388.431\t-999\t411.23\t1023.0\t1020.71\t25.51\t24.9456\t34.1694\t369.74\t400.40\t-30.66\t2\t\n",
            "00KS20181110\t314.87615\t10112018\t21:01:39\t29.98210\t-88.37970\t387.967\t-999\t411.24\t1022.8\t1020.36\t25.52\t24.9521\t34.175\t369.16\t400.25\t-31.09\t2\t\n",
            "00KS20181110\t314.87784\t10112018\t21:04:05\t29.97760\t-88.37470\t388.318\t-999\t411.24\t1021.7\t1021.07\t25.52\t24.9579\t34.2104\t369.18\t400.54\t-31.36\t2\t\n",
            "00KS20181110\t314.87955\t10112018\t21:06:33\t29.97290\t-88.36970\t388.156\t-999\t411.24\t1022.1\t1020.57\t25.51\t24.9503\t34.2505\t369.22\t400.35\t-31.13\t2\t\n",
            "00KS20181110\t314.88125\t10112018\t21:09:00\t29.96820\t-88.36460\t388.446\t-999\t411.24\t1022.3\t1020.32\t25.56\t24.9933\t34.2819\t369.42\t400.22\t-30.79\t2\t\n",
            "00KS20181110\t314.88294\t10112018\t21:11:26\t29.96350\t-88.35970\t389.913\t-999\t411.25\t1021.7\t1020.58\t25.59\t25.0201\t34.3168\t370.52\t400.30\t-29.78\t2\t\n",
            "00KS20181110\t314.88463\t10112018\t21:13:52\t29.95890\t-88.35470\t391.932\t-999\t411.25\t1022.6\t1021.19\t25.62\t25.0482\t34.3578\t372.73\t400.53\t-27.80\t2\t\n",
            "00KS20181110\t314.88633\t10112018\t21:16:19\t29.95430\t-88.34960\t394.415\t-999\t411.25\t1021.8\t1020.71\t25.63\t25.0569\t34.3923\t374.76\t400.34\t-25.58\t2\t\n",
            "00KS20181110\t314.88802\t10112018\t21:18:45\t29.94960\t-88.34460\t396.707\t-999\t411.25\t1022.5\t1020.9\t25.64\t25.0738\t34.4285\t377.31\t400.40\t-23.10\t2\t\n",
            "00KS20181110\t314.88972\t10112018\t21:21:12\t29.94490\t-88.33960\t399.189\t-999\t411.26\t1022.1\t1020.78\t25.66\t25.0923\t34.4689\t379.48\t400.34\t-20.87\t2\t\n",
            "00KS20181110\t314.89141\t10112018\t21:23:38\t29.94030\t-88.33460\t400.837\t-999\t411.26\t1022.4\t1020.74\t25.68\t25.1169\t34.4912\t381.22\t400.31\t-19.09\t2\t\n",
            "00KS20181110\t314.89310\t10112018\t21:26:04\t29.93560\t-88.32970\t402.606\t-999\t411.26\t1021.7\t1020.92\t25.71\t25.1417\t34.5156\t382.53\t400.37\t-17.84\t2\t\n",
            "00KS20181110\t314.89479\t10112018\t21:28:30\t29.93090\t-88.32480\t404.044\t-999\t411.26\t1022.4\t1021.1\t25.73\t25.1716\t34.5411\t384.31\t400.42\t-16.11\t2\t\n",
            "00KS20181110\t314.89650\t10112018\t21:30:58\t29.92570\t-88.32060\t405.109\t-999\t411.27\t1015.8\t1021.32\t25.75\t25.1929\t34.5577\t382.77\t400.50\t-17.73\t2\t\n",
            "00KS20181110\t314.89843\t10112018\t21:33:44\t29.91840\t-88.32020\t405.993\t-999\t411.27\t1022.3\t1021.2\t25.75\t25.2165\t34.5457\t386.52\t400.43\t-13.92\t2\t\n",
            "00KS20181110\t314.90014\t10112018\t21:36:12\t29.91160\t-88.32010\t406.767\t-999\t411.27\t1022.0\t1021.37\t25.76\t25.2313\t34.58\t387.21\t400.49\t-13.29\t2\t\n",
            "00KS20181110\t314.90183\t10112018\t21:38:38\t29.90520\t-88.31960\t407.953\t-999\t411.28\t1021.9\t1021.08\t25.78\t25.2347\t34.614\t388.01\t400.38\t-12.37\t2\t\n",
            "00KS20181110\t314.90352\t10112018\t21:41:04\t29.89940\t-88.31660\t406.856\t-999\t411.28\t1022.0\t1020.64\t25.72\t25.166\t34.5507\t386.91\t400.25\t-13.35\t2\t\n",
            "00KS20181110\t314.90522\t10112018\t21:43:31\t29.89350\t-88.31350\t404.401\t-999\t411.28\t1022.3\t1020.9\t25.70\t25.1383\t34.5168\t384.58\t400.38\t-15.80\t2\t\n",
            "00KS20181110\t314.90693\t10112018\t21:45:59\t29.88760\t-88.31030\t400.318\t-999\t411.28\t1022.4\t1020.87\t25.64\t25.1108\t34.4386\t381.30\t400.39\t-19.09\t2\t\n",
            "00KS20181110\t314.90863\t10112018\t21:48:26\t29.88170\t-88.30720\t398.546\t-999\t411.29\t1022.6\t1021.07\t25.68\t25.1272\t34.4297\t379.28\t400.46\t-21.18\t2\t\n",
            "00KS20181110\t314.91034\t10112018\t21:50:53\t29.87570\t-88.30410\t398.656\t-999\t411.29\t1022.2\t1020.63\t25.68\t25.1006\t34.4181\t378.81\t400.31\t-21.50\t2\t\n",
            "00KS20181110\t314.91204\t10112018\t21:53:20\t29.86960\t-88.30200\t398.585\t-999\t411.29\t1022.6\t1021.07\t25.67\t25.0866\t34.4245\t378.83\t400.50\t-21.66\t2\t\n",
            "00KS20181110\t314.91373\t10112018\t21:55:46\t29.86300\t-88.30140\t398.131\t-999\t411.29\t1021.4\t1021.12\t25.61\t25.0307\t34.3865\t378.05\t400.56\t-22.51\t2\t\n",
            "00KS20181110\t314.91542\t10112018\t21:58:12\t29.85630\t-88.30100\t396.792\t-999\t411.30\t1022.6\t1020.99\t25.50\t24.9184\t34.32\t377.28\t400.59\t-23.31\t2\t\n",
            "00KS20181110\t314.91712\t10112018\t22:00:39\t29.84970\t-88.30000\t394.155\t-999\t411.30\t1021.9\t1020.28\t25.42\t24.8194\t34.2735\t374.26\t400.38\t-26.12\t2\t\n",
            "00KS20181110\t314.91882\t10112018\t22:03:06\t29.84380\t-88.29710\t391.467\t-999\t411.30\t1021.2\t1020.46\t25.36\t24.7629\t34.2491\t371.55\t400.50\t-28.95\t2\t\n",
            "00KS20181110\t314.92052\t10112018\t22:05:33\t29.83770\t-88.29430\t389.342\t-999\t411.30\t1021.9\t1020.18\t25.33\t24.7263\t34.2404\t369.71\t400.41\t-30.71\t2\t\n",
            "00KS20181110\t314.92221\t10112018\t22:07:59\t29.83170\t-88.29150\t387.621\t-999\t411.31\t1021.8\t1020.42\t25.29\t24.6848\t34.217\t368.04\t400.54\t-32.50\t2\t\n",
            "00KS20181110\t314.92390\t10112018\t22:10:25\t29.82560\t-88.28870\t385.958\t-999\t411.31\t1022.0\t1020.14\t25.22\t24.6177\t34.1437\t366.63\t400.48\t-33.85\t2\t\n",
            "00KS20181110\t314.92560\t10112018\t22:12:52\t29.81960\t-88.28570\t384.517\t-999\t411.31\t1021.7\t1019.85\t25.21\t24.6046\t34.1363\t365.11\t400.37\t-35.27\t2\t\n",
            "00KS20181110\t314.92730\t10112018\t22:15:19\t29.81370\t-88.28280\t382.573\t-999\t411.31\t1022.3\t1019.97\t25.23\t24.6189\t34.1411\t363.38\t400.41\t-37.04\t2\t\n",
            "00KS20181110\t314.92899\t10112018\t22:17:45\t29.80770\t-88.27980\t378.847\t-999\t411.32\t1021.6\t1019.71\t25.25\t24.6309\t34.1718\t359.45\t400.30\t-40.85\t2\t\n",
            "00KS20181110\t314.93068\t10112018\t22:20:11\t29.80180\t-88.27670\t374.185\t-999\t411.32\t1021.8\t1020.26\t25.22\t24.607\t34.1802\t355.21\t400.55\t-45.33\t2\t\n",
            "00KS20181110\t314.93240\t10112018\t22:22:39\t29.79570\t-88.27360\t371.483\t-999\t411.32\t1021.3\t1020.24\t25.18\t24.5625\t34.1962\t352.43\t400.57\t-48.14\t2\t\n",
            "00KS20181110\t314.93409\t10112018\t22:25:05\t29.78970\t-88.27170\t369.631\t-999\t411.32\t1021.5\t1020.59\t25.15\t24.5364\t34.1943\t350.82\t400.73\t-49.92\t2\t\n",
            "00KS20181110\t314.93578\t10112018\t22:27:31\t29.78390\t-88.27490\t367.100\t-999\t411.33\t1022.0\t1020.6\t25.16\t24.5539\t34.1969\t348.70\t400.73\t-52.03\t2\t\n",
            "00KS20181110\t314.93747\t10112018\t22:29:57\t29.77800\t-88.27850\t365.434\t-999\t411.33\t1021.0\t1020.57\t25.16\t24.5512\t34.1885\t346.72\t400.72\t-54.00\t2\t\n",
            "00KS20181110\t314.93940\t10112018\t22:32:44\t29.77130\t-88.28250\t364.654\t-999\t411.33\t1021.5\t1020.67\t25.16\t24.5538\t34.1458\t346.20\t400.76\t-54.57\t2\t\n",
            "00KS20181110\t314.94110\t10112018\t22:35:11\t29.76550\t-88.28600\t366.429\t-999\t411.34\t1021.7\t1020.65\t25.14\t24.5444\t34.091\t348.12\t400.76\t-52.64\t2\t\n",
            "00KS20181110\t314.94279\t10112018\t22:37:37\t29.75970\t-88.28960\t367.150\t-999\t411.34\t1021.3\t1020.56\t25.18\t24.5731\t34.0621\t348.47\t400.71\t-52.24\t2\t\n",
            "00KS20181110\t314.94449\t10112018\t22:40:04\t29.75390\t-88.29330\t365.636\t-999\t411.34\t1022.4\t1020.52\t25.35\t24.7468\t34.1097\t347.36\t400.57\t-53.21\t2\t\n",
            "00KS20181110\t314.94620\t10112018\t22:42:32\t29.74810\t-88.29700\t363.328\t-999\t411.34\t1021.8\t1020.53\t25.50\t24.9105\t34.1332\t345.07\t400.46\t-55.39\t2\t\n",
            "00KS20181110\t314.94789\t10112018\t22:44:58\t29.74240\t-88.30060\t362.498\t-999\t411.35\t1021.1\t1020.48\t25.62\t25.0299\t34.1738\t343.95\t400.35\t-56.40\t2\t\n",
            "00KS20181110\t314.94958\t10112018\t22:47:24\t29.73660\t-88.30420\t363.248\t-999\t411.35\t1021.3\t1020.52\t25.69\t25.1294\t34.0443\t345.11\t400.30\t-55.18\t2\t\n",
            "00KS20181110\t314.95127\t10112018\t22:49:50\t29.73090\t-88.30790\t363.177\t-999\t411.35\t1021.6\t1020.49\t25.83\t25.2618\t33.9699\t344.95\t400.19\t-55.24\t2\t\n",
            "00KS20181110\t314.95299\t10112018\t22:52:18\t29.72500\t-88.31170\t364.230\t-999\t411.35\t1021.4\t1020.4\t25.94\t25.3883\t33.9839\t346.05\t400.06\t-54.01\t2\t\n",
            "00KS20181110\t314.95468\t10112018\t22:54:44\t29.71900\t-88.31300\t365.025\t-999\t411.36\t1021.5\t1020.35\t25.99\t25.4429\t34.0017\t346.87\t400.00\t-53.13\t2\t\n",
            "00KS20181110\t314.95639\t10112018\t22:57:12\t29.71250\t-88.31270\t365.324\t-999\t411.36\t1021.8\t1020.27\t26.00\t25.3965\t34.0381\t346.43\t400.01\t-53.58\t2\t\n",
            "00KS20181110\t314.95809\t10112018\t22:59:39\t29.70590\t-88.31250\t364.443\t-999\t411.36\t1021.4\t1019.98\t26.00\t25.4268\t34.0703\t345.89\t399.87\t-53.98\t2\t\n",
            "00KS20181110\t314.95978\t10112018\t23:02:05\t29.69940\t-88.31240\t363.384\t-999\t411.36\t1021.7\t1020.28\t26.02\t25.4569\t34.131\t345.13\t399.97\t-54.84\t2\t\n",
            "00KS20181110\t314.96148\t10112018\t23:04:32\t29.69280\t-88.31210\t362.865\t-999\t411.37\t1021.3\t1020.2\t26.05\t25.4898\t34.1958\t344.52\t399.92\t-55.40\t2\t\n",
            "00KS20181110\t314.96318\t10112018\t23:06:59\t29.68620\t-88.31200\t362.316\t-999\t411.37\t1021.4\t1020.18\t26.09\t25.5371\t34.202\t344.11\t399.88\t-55.76\t2\t\n",
            "00KS20181110\t314.96487\t10112018\t23:09:25\t29.67970\t-88.31180\t362.910\t-999\t411.37\t1021.2\t1020.32\t26.08\t25.55\t34.3052\t344.95\t399.93\t-54.98\t2\t\n",
            "00KS20181110\t314.98020\t10112018\t23:31:29\t29.6219\t-88.3094\t-999\t412.331\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable/interpolated p\n",
            "00KS20181110\t314.98141\t10112018\t23:33:14\t29.6174\t-88.3092\t-999\t410.481\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable/interpolated p\n",
            "00KS20181110\t314.98238\t10112018\t23:34:38\t29.6137\t-88.3091\t-999\t410.439\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable/interpolated p\n",
            "00KS20181110\t314.98336\t10112018\t23:36:02\t29.6101\t-88.309\t-999\t411.698\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable/interpolated p\n",
            "00KS20181110\t314.98434\t10112018\t23:37:27\t29.6064\t-88.3088\t-999\t412.040\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable/interpolated p\n",
            "00KS20181110\t314.98770\t10112018\t23:42:17\t29.59390\t-88.30840\t361.947\t-999\t411.38\t1020.9\t1020.78\t26.08\t25.4904\t34.0902\t343.06\t400.16\t-57.10\t2\t\n",
            "00KS20181110\t314.98940\t10112018\t23:44:44\t29.58760\t-88.30830\t360.465\t-999\t411.37\t1021.1\t1020.31\t26.05\t25.4889\t34.062\t342.16\t399.97\t-57.81\t2\t\n",
            "00KS20181110\t314.99109\t10112018\t23:47:10\t29.58120\t-88.30810\t359.272\t-999\t411.37\t1022.0\t1020.57\t26.06\t25.4872\t34.0467\t341.16\t400.07\t-58.91\t2\t\n",
            "00KS20181110\t314.99280\t10112018\t23:49:38\t29.57480\t-88.30800\t358.607\t-999\t411.36\t1022.3\t1020.58\t26.05\t25.4675\t34.045\t340.50\t400.08\t-59.59\t2\t\n",
            "00KS20181110\t314.99448\t10112018\t23:52:03\t29.56850\t-88.30780\t356.581\t-999\t411.36\t1022.1\t1020.59\t25.91\t25.3484\t33.8978\t338.89\t400.17\t-61.28\t2\t\n",
            "00KS20181110\t314.99619\t10112018\t23:54:31\t29.56210\t-88.30760\t354.051\t-999\t411.35\t1022.1\t1020.69\t25.87\t25.2955\t33.8289\t336.33\t400.24\t-63.91\t2\t\n",
            "00KS20181110\t314.99787\t10112018\t23:56:56\t29.55580\t-88.30730\t352.822\t-999\t411.35\t1021.5\t1020.53\t25.82\t25.2527\t33.7983\t335.10\t400.21\t-65.11\t2\t\n",
            "00KS20181110\t314.99957\t10112018\t23:59:23\t29.54930\t-88.30710\t352.222\t-999\t411.34\t1022.1\t1020.7\t25.83\t25.2478\t33.8098\t334.51\t400.27\t-65.76\t2\t\n",
            "00KS20181110\t315.00126\t11112018\t0:01:49\t29.54280\t-88.30690\t352.494\t-999\t411.34\t1022.3\t1020.69\t25.86\t25.2766\t33.8645\t334.80\t400.24\t-65.44\t2\t\n",
            "00KS20181110\t315.00297\t11112018\t0:04:17\t29.53620\t-88.30670\t352.978\t-999\t411.33\t1022.2\t1020.7\t25.88\t25.2956\t33.9066\t335.20\t400.23\t-65.03\t2\t\n",
            "00KS20181110\t315.00468\t11112018\t0:06:44\t29.52970\t-88.30650\t353.383\t-999\t411.32\t1022.3\t1020.81\t25.89\t25.3144\t33.9445\t335.74\t400.25\t-64.51\t2\t\n",
            "00KS20181110\t315.00637\t11112018\t0:09:10\t29.52320\t-88.30630\t353.705\t-999\t411.32\t1022.6\t1021.11\t25.92\t25.3419\t34.0013\t336.09\t400.35\t-64.26\t2\t\n",
            "00KS20181110\t315.00809\t11112018\t0:11:39\t29.51640\t-88.30620\t354.088\t-999\t411.31\t1022.1\t1020.68\t25.93\t25.3576\t34.0457\t336.36\t400.16\t-63.80\t2\t\n",
            "00KS20181110\t315.00977\t11112018\t0:14:04\t29.50990\t-88.30590\t354.749\t-999\t411.31\t1021.8\t1021.03\t25.97\t25.3884\t34.1004\t336.73\t400.27\t-63.54\t2\t\n",
            "00KS20181110\t315.01147\t11112018\t0:16:31\t29.50320\t-88.30570\t355.415\t-999\t411.30\t1022.6\t1020.94\t25.99\t25.4171\t34.1655\t337.74\t400.21\t-62.46\t2\t\n",
            "00KS20181110\t315.01316\t11112018\t0:18:57\t29.49660\t-88.30540\t356.283\t-999\t411.30\t1022.6\t1020.88\t26.03\t25.4579\t34.2375\t338.56\t400.15\t-61.59\t2\t\n",
            "00KS20181110\t315.01486\t11112018\t0:21:24\t29.49000\t-88.30510\t357.241\t-999\t411.29\t1022.7\t1020.83\t26.06\t25.4959\t34.3019\t339.60\t400.09\t-60.50\t2\t\n",
            "00KS20181110\t315.01656\t11112018\t0:23:51\t29.48330\t-88.30490\t358.376\t-999\t411.28\t1022.6\t1020.99\t26.11\t25.5322\t34.3851\t340.41\t400.13\t-59.72\t2\t\n",
            "00KS20181110\t315.01825\t11112018\t0:26:17\t29.47670\t-88.30460\t359.656\t-999\t411.28\t1022.0\t1021.1\t26.14\t25.5643\t34.4729\t341.43\t400.14\t-58.71\t2\t\n",
            "00KS20181110\t315.01995\t11112018\t0:28:44\t29.46990\t-88.30420\t360.635\t-999\t411.27\t1022.7\t1020.89\t26.17\t25.5987\t34.5537\t342.65\t400.03\t-57.38\t2\t\n",
            "00KS20181110\t315.02166\t11112018\t0:31:11\t29.46330\t-88.30380\t361.901\t-999\t411.27\t1022.6\t1021.15\t26.21\t25.6295\t34.6314\t343.65\t400.10\t-56.45\t2\t\n",
            "00KS20181110\t315.02358\t11112018\t0:33:57\t29.45570\t-88.30330\t363.475\t-999\t411.26\t1022.6\t1021.34\t26.27\t25.6933\t34.7545\t345.16\t400.13\t-54.96\t2\t\n",
            "00KS20181110\t315.02528\t11112018\t0:36:24\t29.44900\t-88.30280\t365.069\t-999\t411.26\t1022.6\t1021.41\t26.31\t25.7415\t34.8485\t346.77\t400.11\t-53.34\t2\t\n",
            "00KS20181110\t315.02696\t11112018\t0:38:49\t29.44240\t-88.30240\t366.753\t-999\t411.25\t1022.7\t1020.97\t26.36\t25.7913\t34.9423\t348.37\t399.89\t-51.52\t2\t\n",
            "00KS20181110\t315.02866\t11112018\t0:41:16\t29.43570\t-88.30180\t368.575\t-999\t411.24\t1022.7\t1021.32\t26.42\t25.8573\t35.0501\t350.15\t399.98\t-49.83\t2\t\n",
            "00KS20181110\t315.03036\t11112018\t0:43:43\t29.42910\t-88.30120\t370.401\t-999\t411.24\t1022.8\t1021.35\t26.46\t25.9047\t35.133\t352.00\t399.95\t-47.95\t2\t\n",
            "00KS20181110\t315.03206\t11112018\t0:46:10\t29.42240\t-88.30080\t372.062\t-999\t411.23\t1022.8\t1021.47\t26.52\t25.9587\t35.2184\t353.45\t399.95\t-46.50\t2\t\n",
            "00KS20181110\t315.03376\t11112018\t0:48:37\t29.41570\t-88.30060\t373.626\t-999\t411.23\t1022.8\t1021.39\t26.56\t26.0125\t35.2919\t355.11\t399.87\t-44.76\t2\t\n",
            "00KS20181110\t315.03545\t11112018\t0:51:03\t29.40910\t-88.30050\t375.068\t-999\t411.22\t1023.0\t1021.07\t26.61\t26.0581\t35.3558\t356.45\t399.70\t-43.25\t2\t\n",
            "00KS20181110\t315.03715\t11112018\t0:53:30\t29.40250\t-88.30040\t376.612\t-999\t411.22\t1023.0\t1021.23\t26.65\t26.1063\t35.4305\t358.02\t399.72\t-41.71\t2\t\n",
            "00KS20181110\t315.03884\t11112018\t0:55:56\t29.39590\t-88.30030\t378.216\t-999\t411.21\t1023.0\t1021.22\t26.70\t26.165\t35.5187\t359.64\t399.67\t-40.03\t2\t\n",
            "00KS20181110\t315.04054\t11112018\t0:58:23\t29.38930\t-88.30010\t379.889\t-999\t411.21\t1022.8\t1021.5\t26.75\t26.2374\t35.6034\t361.46\t399.72\t-38.26\t2\t\n",
            "00KS20181110\t315.04223\t11112018\t1:00:49\t29.38290\t-88.30000\t381.825\t-999\t411.20\t1023.0\t1021.38\t26.83\t26.2529\t35.6881\t362.33\t399.66\t-37.32\t2\t\n",
            "00KS20181110\t315.04394\t11112018\t1:03:16\t29.37630\t-88.29990\t380.966\t-999\t411.19\t1023.2\t1021.35\t26.52\t25.9747\t35.4874\t362.30\t399.85\t-37.55\t2\t\n",
            "00KS20181110\t315.04564\t11112018\t1:05:43\t29.36980\t-88.29980\t375.580\t-999\t411.19\t1021.7\t1021.5\t26.44\t25.8843\t35.3868\t356.54\t399.98\t-43.44\t2\t\n",
            "00KS20181110\t315.04733\t11112018\t1:08:09\t29.36330\t-88.29970\t373.247\t-999\t411.18\t1022.6\t1021.13\t26.45\t25.8894\t35.3915\t354.56\t399.82\t-45.26\t2\t\n",
            "00KS20181110\t315.04903\t11112018\t1:10:36\t29.35670\t-88.29960\t371.915\t-999\t411.18\t1023.1\t1021.56\t26.48\t25.9049\t35.3816\t353.24\t399.97\t-46.74\t2\t\n",
            "00KS20181110\t315.05072\t11112018\t1:13:02\t29.35010\t-88.29960\t371.984\t-999\t411.17\t1022.7\t1021.31\t26.52\t25.9575\t35.4071\t353.32\t399.83\t-46.51\t2\t\n",
            "00KS20181110\t315.05242\t11112018\t1:15:29\t29.34360\t-88.29950\t373.073\t-999\t411.17\t1023.2\t1021.55\t26.57\t26.0117\t35.4343\t354.56\t399.88\t-45.31\t2\t\n",
            "00KS20181110\t315.05411\t11112018\t1:17:55\t29.33710\t-88.29930\t374.645\t-999\t411.16\t1023.3\t1021.43\t26.64\t26.0671\t35.4648\t355.82\t399.78\t-43.96\t2\t\n",
            "00KS20181110\t315.05581\t11112018\t1:20:22\t29.33060\t-88.29900\t375.906\t-999\t411.16\t1023.1\t1021.43\t26.59\t26.0441\t35.4276\t357.39\t399.79\t-42.40\t2\t\n",
            "00KS20181110\t315.05751\t11112018\t1:22:49\t29.32410\t-88.29870\t376.017\t-999\t411.15\t1023.3\t1021.51\t26.55\t25.9816\t35.3935\t357.26\t399.87\t-42.61\t2\t\n",
            "00KS20181110\t315.05920\t11112018\t1:25:15\t29.31770\t-88.29840\t374.795\t-999\t411.14\t1023.3\t1021.54\t26.31\t25.7536\t35.1895\t356.45\t400.05\t-43.60\t2\t\n",
            "00KS20181110\t315.06090\t11112018\t1:27:42\t29.31110\t-88.29810\t372.383\t-999\t411.14\t1022.9\t1021.22\t26.30\t25.7288\t35.176\t353.79\t399.93\t-46.14\t2\t\n",
            "00KS20181110\t315.06260\t11112018\t1:30:09\t29.30460\t-88.29780\t371.363\t-999\t411.13\t1022.2\t1021.37\t26.36\t25.7718\t35.1872\t352.28\t399.96\t-47.67\t2\t\n",
            "00KS20181110\t315.06454\t11112018\t1:32:56\t29.29710\t-88.29760\t371.363\t-999\t411.13\t1022.8\t1021.57\t26.40\t25.8184\t35.1772\t352.57\t399.99\t-47.43\t2\t\n",
            "00KS20181110\t315.06624\t11112018\t1:35:23\t29.29040\t-88.29750\t371.728\t-999\t411.12\t1021.8\t1021.31\t26.44\t25.8519\t35.1994\t352.43\t399.86\t-47.43\t2\t\n",
            "00KS20181110\t315.06793\t11112018\t1:37:49\t29.28380\t-88.29740\t371.617\t-999\t411.12\t1023.0\t1021.45\t26.40\t25.8198\t35.1284\t352.90\t399.93\t-47.04\t2\t\n",
            "00KS20181110\t315.06963\t11112018\t1:40:16\t29.27710\t-88.29730\t371.038\t-999\t411.11\t1023.4\t1021.27\t26.41\t25.8243\t35.1102\t352.40\t399.85\t-47.45\t2\t\n",
            "00KS20181110\t315.07133\t11112018\t1:42:43\t29.27040\t-88.29720\t370.837\t-999\t411.10\t1023.2\t1021.47\t26.42\t25.8393\t35.0989\t352.21\t399.92\t-47.71\t2\t\n",
            "00KS20181110\t315.07302\t11112018\t1:45:09\t29.26370\t-88.29710\t371.231\t-999\t411.10\t1023.4\t1021.61\t26.44\t25.8642\t35.1094\t352.71\t399.95\t-47.24\t2\t\n",
            "00KS20181110\t315.07472\t11112018\t1:47:36\t29.25700\t-88.29720\t371.655\t-999\t411.09\t1023.0\t1021.29\t26.48\t25.8923\t35.1408\t352.77\t399.79\t-47.02\t2\t\n",
            "00KS20181110\t315.07642\t11112018\t1:50:03\t29.25150\t-88.29730\t372.187\t-999\t411.09\t1023.0\t1021.72\t26.51\t25.9373\t35.1955\t353.48\t399.93\t-46.45\t2\t\n",
            "00KS20181110\t315.07811\t11112018\t1:52:29\t29.24960\t-88.29680\t372.733\t-999\t411.08\t1023.3\t1021.24\t26.52\t25.9579\t35.2691\t354.25\t399.71\t-45.46\t2\t\n",
            "00KS20181110\t315.07981\t11112018\t1:54:56\t29.24950\t-88.29600\t373.265\t-999\t411.08\t1023.3\t1021.63\t26.52\t25.9716\t35.2721\t354.96\t399.85\t-44.89\t2\t\n",
            "00KS20181110\t315.08152\t11112018\t1:57:23\t29.24970\t-88.29650\t373.377\t-999\t411.07\t1023.4\t1021.66\t26.42\t25.9814\t35.2926\t356.83\t399.85\t-43.02\t2\t\n",
            "00KS20181110\t315.08321\t11112018\t1:59:49\t29.25060\t-88.29760\t373.427\t-999\t411.06\t1023.2\t1021.54\t26.37\t25.9819\t35.2992\t357.60\t399.80\t-42.19\t2\t\n",
            "00KS20181110\t315.08492\t11112018\t2:02:17\t29.25160\t-88.29860\t373.074\t-999\t411.06\t1023.0\t1021.62\t26.33\t25.9839\t35.2969\t357.86\t399.82\t-41.96\t2\t\n",
            "00KS20181110\t315.08662\t11112018\t2:04:44\t29.25250\t-88.29800\t372.246\t-999\t411.05\t1023.6\t1021.41\t26.32\t25.973\t35.2861\t357.27\t399.74\t-42.47\t2\t\n",
            "00KS20181110\t315.08831\t11112018\t2:07:10\t29.25250\t-88.29680\t371.339\t-999\t411.05\t1023.1\t1021.64\t26.29\t25.9613\t35.2664\t356.52\t399.84\t-43.32\t2\t\n",
            "00KS20181110\t315.09001\t11112018\t2:09:37\t29.25200\t-88.29630\t370.561\t-999\t411.04\t1024.0\t1021.57\t26.30\t25.9673\t35.2659\t356.03\t399.80\t-43.77\t2\t\n",
            "00KS20181110\t315.09171\t11112018\t2:12:04\t29.25100\t-88.29570\t370.602\t-999\t411.04\t1022.9\t1021.46\t26.29\t25.9807\t35.2813\t356.03\t399.74\t-43.71\t2\t\n",
            "00KS20181110\t315.09340\t11112018\t2:14:30\t29.24910\t-88.29710\t370.592\t-999\t411.03\t1022.4\t1021.92\t26.26\t25.9759\t35.2773\t356.24\t399.92\t-43.68\t2\t\n",
            "00KS20181110\t315.09510\t11112018\t2:16:57\t29.25040\t-88.29970\t370.488\t-999\t411.03\t1022.5\t1021.86\t26.27\t25.9655\t35.275\t355.87\t399.90\t-44.03\t2\t\n",
            "00KS20181110\t315.09681\t11112018\t2:19:24\t29.25130\t-88.30010\t370.842\t-999\t411.02\t1023.6\t1021.9\t26.29\t25.9611\t35.2826\t356.22\t399.91\t-43.70\t2\t\n",
            "00KS20181110\t315.09848\t11112018\t2:21:49\t29.25200\t-88.29920\t369.883\t-999\t411.01\t1022.6\t1021.7\t26.22\t25.8984\t35.2077\t355.10\t399.88\t-44.78\t2\t\n",
            "00KS20181110\t315.10019\t11112018\t2:24:16\t29.25210\t-88.29810\t368.388\t-999\t411.01\t1023.2\t1021.81\t26.20\t25.8833\t35.1938\t353.96\t399.93\t-45.96\t2\t\n",
            "00KS20181110\t315.10199\t11112018\t2:26:52\t29.25270\t-88.29780\t367.510\t-999\t411.00\t1022.5\t1021.91\t26.19\t25.8754\t35.1952\t352.91\t399.97\t-47.06\t2\t\n",
            "00KS20181110\t315.10381\t11112018\t2:29:29\t29.25330\t-88.29810\t367.733\t-999\t411.00\t1023.5\t1021.89\t26.25\t25.9074\t35.2407\t353.02\t399.93\t-46.91\t2\t\n",
            "00KS20181110\t315.10588\t11112018\t2:32:28\t29.25420\t-88.29820\t369.106\t-999\t410.99\t1023.8\t1021.83\t26.27\t25.9492\t35.2636\t354.76\t399.87\t-45.11\t2\t\n",
            "00KS20181110\t315.10770\t11112018\t2:35:05\t29.25460\t-88.29850\t369.773\t-999\t410.98\t1023.3\t1021.62\t26.28\t25.9687\t35.2637\t355.36\t399.76\t-44.40\t2\t\n",
            "00KS20181110\t315.10950\t11112018\t2:37:41\t29.25530\t-88.29880\t370.328\t-999\t410.98\t1023.6\t1021.8\t26.28\t25.9782\t35.268\t356.14\t399.82\t-43.68\t2\t\n",
            "00KS20181110\t315.11131\t11112018\t2:40:17\t29.25620\t-88.29870\t370.488\t-999\t410.97\t1023.6\t1021.04\t26.27\t25.9758\t35.2644\t356.42\t399.51\t-43.09\t2\t\n",
            "00KS20181110\t315.11313\t11112018\t2:42:54\t29.25430\t-88.29630\t370.606\t-999\t410.97\t1023.1\t1021.07\t26.26\t25.9661\t35.2578\t356.36\t399.52\t-43.16\t2\t\n",
            "00KS20181110\t315.11494\t11112018\t2:45:31\t29.25030\t-88.29710\t370.434\t-999\t410.96\t1022.6\t1021.79\t26.26\t25.9602\t35.258\t355.93\t399.81\t-43.89\t2\t\n",
            "00KS20181110\t315.11675\t11112018\t2:48:07\t29.24730\t-88.29780\t370.538\t-999\t410.95\t1023.4\t1021.73\t26.28\t25.9663\t35.2713\t356.09\t399.78\t-43.69\t2\t\n",
            "00KS20181110\t315.11855\t11112018\t2:50:43\t29.24500\t-88.29850\t370.821\t-999\t410.95\t1022.4\t1021.69\t26.27\t25.9473\t35.268\t355.88\t399.77\t-43.90\t2\t\n",
            "00KS20181110\t315.12037\t11112018\t2:53:20\t29.24310\t-88.29870\t369.317\t-999\t410.94\t1023.6\t1021.41\t26.18\t25.8835\t35.2154\t355.32\t399.70\t-44.38\t2\t\n",
            "00KS20181110\t315.12219\t11112018\t2:55:57\t29.24100\t-88.29890\t368.694\t-999\t410.94\t1023.5\t1021.44\t26.18\t25.8912\t35.2325\t354.80\t399.70\t-44.90\t2\t\n",
            "00KS20181110\t315.12399\t11112018\t2:58:33\t29.23860\t-88.29950\t368.980\t-999\t410.93\t1023.7\t1021.51\t26.20\t25.9005\t35.2498\t354.97\t399.72\t-44.75\t2\t\n",
            "00KS20181110\t315.12580\t11112018\t3:01:09\t29.23610\t-88.29980\t369.145\t-999\t410.92\t1022.4\t1021.59\t26.16\t25.8846\t35.2195\t355.05\t399.76\t-44.70\t2\t\n",
            "00KS20181110\t315.12762\t11112018\t3:03:46\t29.23310\t-88.30060\t367.554\t-999\t410.92\t1023.3\t1021.52\t26.13\t25.8526\t35.1662\t353.83\t399.75\t-45.91\t2\t\n",
            "00KS20181110\t315.12943\t11112018\t3:06:23\t29.22990\t-88.30160\t366.881\t-999\t410.91\t1023.5\t1021.67\t26.13\t25.8452\t35.1603\t353.15\t399.81\t-46.66\t2\t\n",
            "00KS20181110\t315.13125\t11112018\t3:09:00\t29.22670\t-88.30260\t366.521\t-999\t410.91\t1023.3\t1021.49\t26.12\t25.846\t35.1735\t352.90\t399.73\t-46.83\t2\t\n",
            "00KS20181110\t315.13307\t11112018\t3:11:37\t29.22340\t-88.30360\t367.342\t-999\t410.90\t1021.6\t1021.54\t26.18\t25.8927\t35.2207\t352.84\t399.71\t-46.86\t2\t\n",
            "00KS20181110\t315.13487\t11112018\t3:14:13\t29.22020\t-88.30470\t368.052\t-999\t410.89\t1021.0\t1021.5\t26.19\t25.9037\t35.2054\t353.32\t399.67\t-46.36\t2\t\n",
            "00KS20181110\t315.13669\t11112018\t3:16:50\t29.21680\t-88.30590\t368.116\t-999\t410.89\t1022.4\t1021.52\t26.15\t25.8946\t35.1798\t354.37\t399.68\t-45.31\t2\t\n",
            "00KS20181110\t315.13850\t11112018\t3:19:26\t29.21350\t-88.30710\t368.079\t-999\t410.88\t1023.5\t1021.57\t26.16\t25.8891\t35.1843\t354.49\t399.70\t-45.22\t2\t\n",
            "00KS20181110\t315.14031\t11112018\t3:22:03\t29.21020\t-88.30830\t368.233\t-999\t410.88\t1022.6\t1021.26\t26.19\t25.9084\t35.1833\t354.13\t399.56\t-45.42\t2\t\n",
            "00KS20181110\t315.14213\t11112018\t3:24:40\t29.20700\t-88.30960\t368.348\t-999\t410.87\t1023.3\t1021.41\t26.22\t25.9251\t35.1692\t354.27\t399.60\t-45.33\t2\t\n",
            "00KS20181110\t315.14392\t11112018\t3:27:15\t29.20380\t-88.31090\t370.600\t-999\t410.86\t1022.8\t1021.63\t26.24\t25.9591\t35.1688\t356.46\t399.66\t-43.20\t2\t\n",
            "00KS20181110\t315.14574\t11112018\t3:29:52\t29.20070\t-88.31220\t371.017\t-999\t410.86\t1022.9\t1021.31\t26.22\t25.9503\t35.1806\t357.08\t399.53\t-42.45\t2\t\n",
            "00KS20181110\t315.14780\t11112018\t3:32:50\t29.19700\t-88.31380\t373.386\t-999\t410.85\t1023.6\t1021.49\t26.27\t25.9907\t35.2195\t359.43\t399.56\t-40.13\t2\t\n",
            "00KS20181110\t315.14962\t11112018\t3:35:27\t29.19380\t-88.31520\t374.992\t-999\t410.85\t1022.6\t1021.54\t26.28\t26.0031\t35.2425\t360.64\t399.57\t-38.93\t2\t\n",
            "00KS20181110\t315.15142\t11112018\t3:38:03\t29.19070\t-88.31650\t376.276\t-999\t410.84\t1023.3\t1021.51\t26.28\t26.0177\t35.2426\t362.36\t399.54\t-37.18\t2\t\n",
            "00KS20181110\t315.15324\t11112018\t3:40:40\t29.18750\t-88.31790\t376.407\t-999\t410.83\t1022.4\t1021.57\t26.30\t26.0212\t35.2379\t361.89\t399.55\t-37.67\t2\t\n",
            "00KS20181110\t315.15506\t11112018\t3:43:17\t29.18430\t-88.31930\t376.064\t-999\t410.83\t1022.8\t1021.46\t26.28\t26.011\t35.2165\t361.87\t399.51\t-37.64\t2\t\n",
            "00KS20181110\t315.15686\t11112018\t3:45:53\t29.18120\t-88.32060\t375.152\t-999\t410.82\t1022.8\t1021.44\t26.27\t25.9908\t35.1908\t360.84\t399.51\t-38.67\t2\t\n",
            "00KS20181110\t315.15868\t11112018\t3:48:30\t29.17810\t-88.32200\t374.004\t-999\t410.82\t1022.9\t1021.43\t26.21\t25.9505\t35.1709\t360.11\t399.53\t-39.42\t2\t\n",
            "00KS20181110\t315.16050\t11112018\t3:51:07\t29.17490\t-88.32330\t372.431\t-999\t410.81\t1023.3\t1021.4\t26.20\t25.9154\t35.1088\t358.37\t399.54\t-41.17\t2\t\n",
            "00KS20181110\t315.17639\t11112018\t4:14:00\t29.14700\t-88.33530\t-999\t413.111\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable air value\n",
            "00KS20181110\t315.17748\t11112018\t4:15:34\t29.14510\t-88.33620\t-999\t413.995\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable air value\n",
            "00KS20181110\t315.17856\t11112018\t4:17:08\t29.14330\t-88.33700\t-999\t411.640\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable air value\n",
            "00KS20181110\t315.17966\t11112018\t4:18:43\t29.14150\t-88.33790\t-999\t414.575\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable air value\n",
            "00KS20181110\t315.18075\t11112018\t4:20:17\t29.13960\t-88.33880\t-999\t412.422\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t3\tquestionable air value\n",
            "00KS20181110\t315.18422\t11112018\t4:25:17\t29.13370\t-88.34150\t377.032\t-999\t410.73\t1022.0\t1020.55\t26.23\t25.9565\t35.2871\t362.47\t399.09\t-36.62\t2\t\n",
            "00KS20181110\t315.18604\t11112018\t4:27:54\t29.13060\t-88.34290\t376.802\t-999\t410.73\t1021.7\t1020.85\t26.20\t25.9477\t35.2819\t362.49\t399.22\t-36.73\t2\t\n",
            "00KS20181110\t315.18786\t11112018\t4:30:31\t29.12740\t-88.34440\t375.579\t-999\t410.72\t1022.9\t1020.64\t26.21\t25.9439\t35.2904\t361.53\t399.13\t-37.60\t2\t\n",
            "00KS20181110\t315.18992\t11112018\t4:33:29\t29.12380\t-88.34610\t376.112\t-999\t410.71\t1021.8\t1020.7\t26.22\t25.9514\t35.3131\t361.60\t399.14\t-37.54\t2\t\n",
            "00KS20181110\t315.19172\t11112018\t4:36:05\t29.12070\t-88.34760\t376.867\t-999\t410.71\t1022.5\t1020.51\t26.22\t25.945\t35.3297\t362.48\t399.06\t-36.58\t2\t\n",
            "00KS20181110\t315.19353\t11112018\t4:38:41\t29.11760\t-88.34910\t377.310\t-999\t410.70\t1022.4\t1020.61\t26.21\t25.9322\t35.3441\t362.83\t399.11\t-36.27\t2\t\n",
            "00KS20181110\t315.19535\t11112018\t4:41:18\t29.11450\t-88.35060\t376.937\t-999\t410.69\t1022.0\t1020.53\t26.18\t25.9114\t35.3495\t362.49\t399.09\t-36.59\t2\t\n",
            "00KS20181110\t315.19715\t11112018\t4:43:54\t29.11150\t-88.35220\t375.735\t-999\t410.69\t1022.3\t1020.36\t26.16\t25.8787\t35.3456\t361.27\t399.04\t-37.77\t2\t\n",
            "00KS20181110\t315.19898\t11112018\t4:46:32\t29.10840\t-88.35370\t375.250\t-999\t410.68\t1022.3\t1020.7\t26.15\t25.8657\t35.3556\t360.76\t399.18\t-38.42\t2\t\n",
            "00KS20181110\t315.20079\t11112018\t4:49:08\t29.10530\t-88.35530\t374.928\t-999\t410.68\t1021.7\t1020.64\t26.14\t25.8554\t35.3504\t360.24\t399.16\t-38.92\t2\t\n",
            "00KS20181110\t315.20260\t11112018\t4:51:45\t29.10220\t-88.35700\t374.534\t-999\t410.67\t1022.3\t1020.72\t26.14\t25.8476\t35.3535\t359.96\t399.19\t-39.23\t2\t\n",
            "00KS20181110\t315.20441\t11112018\t4:54:21\t29.09920\t-88.35860\t374.302\t-999\t410.67\t1022.7\t1020.69\t26.11\t25.8345\t35.3535\t360.16\t399.18\t-39.02\t2\t\n",
            "00KS20181110\t315.20623\t11112018\t4:56:58\t29.09610\t-88.36020\t374.263\t-999\t410.66\t1022.9\t1020.91\t26.11\t25.8225\t35.3604\t360.01\t399.27\t-39.26\t2\t\n",
            "00KS20181110\t315.20803\t11112018\t4:59:34\t29.09300\t-88.36180\t374.263\t-999\t410.65\t1022.8\t1020.86\t26.09\t25.8148\t35.3544\t360.17\t399.25\t-39.08\t2\t\n",
            "00KS20181110\t315.20984\t11112018\t5:02:10\t29.09000\t-88.36340\t373.648\t-999\t410.65\t1022.7\t1020.69\t26.08\t25.8082\t35.3313\t359.60\t399.18\t-39.58\t2\t\n",
            "00KS20181110\t315.21166\t11112018\t5:04:47\t29.08740\t-88.36460\t373.577\t-999\t410.64\t1022.9\t1020.7\t26.12\t25.8325\t35.3461\t359.34\t399.16\t-39.82\t2\t\n",
            "00KS20181110\t315.21347\t11112018\t5:07:24\t29.08500\t-88.36560\t374.050\t-999\t410.64\t1020.9\t1020.48\t26.13\t25.8398\t35.3443\t359.02\t399.06\t-40.04\t2\t\n",
            "00KS20181110\t315.21529\t11112018\t5:10:01\t29.08270\t-88.36660\t374.351\t-999\t410.63\t1022.7\t1020.7\t26.11\t25.8276\t35.3422\t360.10\t399.15\t-39.06\t2\t\n",
            "00KS20181110\t315.21709\t11112018\t5:12:37\t29.08020\t-88.36770\t374.162\t-999\t410.62\t1023.0\t1020.76\t26.12\t25.8284\t35.3451\t359.88\t399.17\t-39.29\t2\t\n",
            "00KS20181110\t315.21891\t11112018\t5:15:14\t29.07750\t-88.36890\t373.776\t-999\t410.62\t1022.6\t1020.7\t26.14\t25.8467\t35.3588\t359.32\t399.13\t-39.81\t2\t\n",
            "00KS20181110\t315.22073\t11112018\t5:17:51\t29.07470\t-88.37010\t373.667\t-999\t410.61\t1022.0\t1020.57\t26.12\t25.8493\t35.3618\t359.36\t399.07\t-39.71\t2\t\n",
            "00KS20181110\t315.22255\t11112018\t5:20:28\t29.07200\t-88.37130\t373.455\t-999\t410.61\t1022.4\t1020.55\t26.14\t25.8541\t35.3781\t359.05\t399.05\t-40.00\t2\t\n",
            "00KS20181110\t315.22434\t11112018\t5:23:03\t29.06930\t-88.37250\t373.717\t-999\t410.60\t1022.3\t1020.48\t26.15\t25.8662\t35.388\t359.29\t399.01\t-39.71\t2\t\n",
            "00KS20181110\t315.22616\t11112018\t5:25:40\t29.06660\t-88.37370\t373.927\t-999\t410.59\t1022.1\t1020.43\t26.16\t25.8784\t35.3966\t359.45\t398.97\t-39.52\t2\t\n",
            "00KS20181110\t315.22799\t11112018\t5:28:18\t29.06380\t-88.37490\t373.839\t-999\t410.59\t1022.5\t1020.21\t26.16\t25.8714\t35.3954\t359.40\t398.88\t-39.48\t2\t\n",
            "00KS20181110\t315.22979\t11112018\t5:30:54\t29.06120\t-88.37620\t373.745\t-999\t410.58\t1021.0\t1020.44\t26.15\t25.8719\t35.393\t358.94\t398.97\t-40.03\t2\t\n",
            "00KS20181110\t315.23185\t11112018\t5:33:52\t29.05820\t-88.37750\t373.617\t-999\t410.57\t1022.7\t1020.46\t26.16\t25.8716\t35.378\t359.27\t398.97\t-39.70\t2\t\n",
            "00KS20181110\t315.23367\t11112018\t5:36:29\t29.05560\t-88.37880\t373.888\t-999\t410.57\t1022.7\t1020.37\t26.17\t25.882\t35.3833\t359.53\t398.92\t-39.39\t2\t\n",
            "00KS20181110\t315.23547\t11112018\t5:39:05\t29.05290\t-88.37990\t373.940\t-999\t410.56\t1022.7\t1020.47\t26.18\t25.8857\t35.3797\t359.47\t398.95\t-39.48\t2\t\n",
            "00KS20181110\t315.23728\t11112018\t5:41:41\t29.05020\t-88.38120\t374.320\t-999\t410.56\t1022.7\t1020.06\t26.17\t25.8926\t35.3658\t360.10\t398.78\t-38.67\t2\t\n",
            "00KS20181110\t315.23910\t11112018\t5:44:18\t29.04750\t-88.38250\t373.926\t-999\t410.55\t1021.4\t1020.36\t26.17\t25.8837\t35.3518\t359.12\t398.90\t-39.78\t2\t\n",
            "00KS20181110\t315.24090\t11112018\t5:46:54\t29.04480\t-88.38380\t373.735\t-999\t410.54\t1022.3\t1020.43\t26.17\t25.884\t35.359\t359.26\t398.92\t-39.66\t2\t\n",
            "00KS20181110\t315.24272\t11112018\t5:49:31\t29.04220\t-88.38500\t373.683\t-999\t410.54\t1022.5\t1020.42\t26.17\t25.8873\t35.3684\t359.34\t398.91\t-39.57\t2\t\n",
            "00KS20181110\t315.24453\t11112018\t5:52:07\t29.03950\t-88.38620\t373.847\t-999\t410.53\t1022.3\t1020.48\t26.19\t25.8971\t35.3803\t359.25\t398.92\t-39.67\t2\t\n",
            "00KS20181110\t315.24634\t11112018\t5:54:44\t29.03690\t-88.38750\t373.904\t-999\t410.53\t1022.3\t1020.55\t26.19\t25.9015\t35.3809\t359.37\t398.94\t-39.56\t2\t\n",
            "00KS20181110\t315.24816\t11112018\t5:57:21\t29.03410\t-88.38880\t373.934\t-999\t410.52\t1021.5\t1020.5\t26.20\t25.9069\t35.3909\t359.04\t398.91\t-39.87\t2\t\n",
            "00KS20181110\t315.24997\t11112018\t5:59:57\t29.03150\t-88.39020\t373.852\t-999\t410.51\t1021.9\t1020.38\t26.20\t25.9099\t35.4079\t359.15\t398.85\t-39.70\t2\t\n",
            "00KS20181110\t315.25177\t11112018\t6:02:33\t29.02890\t-88.39150\t373.694\t-999\t410.51\t1021.8\t1020.38\t26.22\t25.915\t35.4392\t358.72\t398.84\t-40.12\t2\t\n",
            "00KS20181110\t315.25359\t11112018\t6:05:10\t29.02630\t-88.39280\t374.418\t-999\t410.50\t1022.3\t1020.56\t26.25\t25.9458\t35.4863\t359.59\t398.89\t-39.30\t2\t\n",
            "00KS20181110\t315.25539\t11112018\t6:07:46\t29.02370\t-88.39410\t376.130\t-999\t410.50\t1023.2\t1020.43\t26.29\t25.9933\t35.529\t361.65\t398.79\t-37.14\t2\t\n",
            "00KS20181110\t315.25721\t11112018\t6:10:23\t29.02100\t-88.39520\t377.871\t-999\t410.49\t1022.1\t1020.57\t26.33\t26.0286\t35.5448\t362.82\t398.81\t-36.00\t2\t\n",
            "00KS20181110\t315.25902\t11112018\t6:12:59\t29.01830\t-88.39630\t378.738\t-999\t410.48\t1022.3\t1020.57\t26.31\t26.0294\t35.543\t364.06\t398.81\t-34.75\t2\t\n",
            "00KS20181110\t315.26083\t11112018\t6:15:36\t29.01550\t-88.39740\t378.787\t-999\t410.48\t1022.7\t1020.31\t26.32\t26.0248\t35.5531\t364.02\t398.70\t-34.68\t2\t\n",
            "00KS20181110\t315.26264\t11112018\t6:18:12\t29.01290\t-88.39840\t378.787\t-999\t410.47\t1022.4\t1020.39\t26.31\t26.0119\t35.5563\t363.87\t398.74\t-34.86\t2\t\n",
            "00KS20181110\t315.26446\t11112018\t6:20:49\t29.01030\t-88.39950\t378.806\t-999\t410.47\t1021.7\t1020.34\t26.28\t25.9943\t35.564\t363.85\t398.72\t-34.88\t2\t\n",
            "00KS20181110\t315.26626\t11112018\t6:23:25\t29.00770\t-88.40050\t378.270\t-999\t410.46\t1022.9\t1020.41\t26.26\t25.9686\t35.5698\t363.70\t398.77\t-35.07\t2\t\n",
            "00KS20181110\t315.26809\t11112018\t6:26:03\t29.00500\t-88.40150\t378.148\t-999\t410.45\t1022.6\t1020.43\t26.24\t25.9406\t35.5797\t363.36\t398.79\t-35.43\t2\t\n",
            "00KS20181110\t315.26988\t11112018\t6:28:38\t29.00240\t-88.40250\t376.798\t-999\t410.45\t1022.7\t1020.45\t26.22\t25.9162\t35.5858\t362.05\t398.81\t-36.76\t2\t\n",
            "00KS20181110\t315.27170\t11112018\t6:31:15\t28.99980\t-88.40360\t376.334\t-999\t410.44\t1022.5\t1020.48\t26.23\t25.9224\t35.6046\t361.46\t398.81\t-37.35\t2\t\n",
            "00KS20181110\t315.27376\t11112018\t6:34:13\t28.99680\t-88.40480\t376.294\t-999\t410.44\t1022.6\t1020.36\t26.23\t25.9281\t35.609\t361.55\t398.75\t-37.20\t2\t\n",
            "00KS20181110\t315.27558\t11112018\t6:36:50\t28.99420\t-88.40590\t376.847\t-999\t410.43\t1022.7\t1020.25\t26.23\t25.9224\t35.6271\t362.03\t398.71\t-36.68\t2\t\n",
            "00KS20181110\t315.27738\t11112018\t6:39:26\t28.99160\t-88.40700\t377.047\t-999\t410.42\t1022.3\t1020.36\t26.21\t25.8994\t35.621\t362.04\t398.76\t-36.72\t2\t\n",
            "00KS20181110\t315.27919\t11112018\t6:42:02\t28.98910\t-88.40800\t375.687\t-999\t410.42\t1022.6\t1020.51\t26.15\t25.8638\t35.5798\t361.26\t398.85\t-37.58\t2\t\n",
            "00KS20181110\t315.28101\t11112018\t6:44:39\t28.98640\t-88.40900\t372.937\t-999\t410.41\t1023.1\t1020.5\t26.16\t25.8514\t35.5472\t358.45\t398.85\t-40.39\t2\t\n",
            "00KS20181110\t315.28282\t11112018\t6:47:16\t28.98380\t-88.41000\t371.295\t-999\t410.41\t1022.7\t1020.34\t26.17\t25.8606\t35.534\t356.71\t398.77\t-42.06\t2\t\n",
            "00KS20181110\t315.28463\t11112018\t6:49:52\t28.98120\t-88.41110\t370.095\t-999\t410.40\t1021.3\t1020.44\t26.17\t25.8621\t35.5454\t355.08\t398.80\t-43.72\t2\t\n",
            "00KS20181110\t315.28645\t11112018\t6:52:29\t28.97860\t-88.41210\t370.064\t-999\t410.39\t1022.5\t1020.37\t26.18\t25.8651\t35.5522\t355.37\t398.77\t-43.40\t2\t\n",
            "00KS20181110\t315.28825\t11112018\t6:55:05\t28.97610\t-88.41310\t369.197\t-999\t410.39\t1022.6\t1020.42\t26.14\t25.8409\t35.551\t354.83\t398.80\t-43.96\t2\t\n",
            "00KS20181110\t315.29007\t11112018\t6:57:42\t28.97350\t-88.41400\t368.270\t-999\t410.38\t1022.8\t1020.38\t26.14\t25.8278\t35.5323\t353.82\t398.79\t-44.97\t2\t\n",
            "00KS20181110\t315.29368\t11112018\t7:02:54\t28.96830\t-88.41580\t367.121\t-999\t410.37\t1022.3\t1020.25\t26.18\t25.8682\t35.4888\t352.52\t398.69\t-46.18\t2\t\n",
            "00KS20181110\t315.29550\t11112018\t7:05:31\t28.96570\t-88.41670\t366.076\t-999\t410.36\t1022.3\t1020.15\t26.17\t25.8686\t35.4696\t351.67\t398.64\t-46.97\t2\t\n",
            "00KS20181110\t315.29730\t11112018\t7:08:07\t28.96320\t-88.41720\t365.457\t-999\t410.36\t1021.9\t1020.25\t26.19\t25.8778\t35.4457\t350.76\t398.67\t-47.91\t2\t\n",
            "00KS20181110\t315.29912\t11112018\t7:10:44\t28.96070\t-88.41770\t364.389\t-999\t410.35\t1022.2\t1020.09\t26.17\t25.8325\t35.396\t349.48\t398.64\t-49.15\t2\t\n",
            "00KS20181110\t315.30094\t11112018\t7:13:21\t28.95820\t-88.41820\t358.748\t-999\t410.35\t1022.1\t1020.1\t26.03\t25.7341\t34.8989\t344.73\t398.71\t-53.97\t2\t\n",
            "00KS20181110\t315.30275\t11112018\t7:15:58\t28.95580\t-88.41890\t353.218\t-999\t410.34\t1021.9\t1020.17\t26.09\t25.7582\t34.7617\t338.80\t398.71\t-59.91\t2\t\n",
            "00KS20181110\t315.30455\t11112018\t7:18:33\t28.95340\t-88.41980\t350.382\t-999\t410.33\t1021.7\t1020.18\t26.14\t25.8122\t34.7061\t336.03\t398.67\t-62.63\t2\t\n",
            "00KS20181110\t315.30637\t11112018\t7:21:10\t28.95090\t-88.42070\t349.566\t-999\t410.33\t1022.1\t1019.96\t26.18\t25.8653\t34.6749\t335.54\t398.53\t-62.99\t2\t\n",
            "00KS20181110\t315.30817\t11112018\t7:23:46\t28.94850\t-88.42170\t349.207\t-999\t410.32\t1021.8\t1019.93\t26.21\t25.8973\t34.6671\t335.11\t398.49\t-63.38\t2\t\n",
            "00KS20181110\t315.30999\t11112018\t7:26:23\t28.94600\t-88.42280\t348.406\t-999\t410.32\t1021.2\t1019.91\t26.21\t25.9013\t34.6343\t334.19\t398.47\t-64.28\t2\t\n",
            "00KS20181110\t315.31181\t11112018\t7:29:00\t28.94360\t-88.42380\t347.675\t-999\t410.31\t1022.4\t1020.1\t26.22\t25.9065\t34.5984\t333.82\t398.54\t-64.72\t2\t\n",
            "00KS20181110\t315.31361\t11112018\t7:31:36\t28.94110\t-88.42480\t346.224\t-999\t410.30\t1021.7\t1019.99\t26.21\t25.9072\t34.5593\t332.35\t398.49\t-66.14\t2\t\n",
            "00KS20181110\t315.31567\t11112018\t7:34:34\t28.93820\t-88.42600\t345.227\t-999\t410.30\t1021.4\t1020.01\t26.20\t25.8972\t34.5304\t331.30\t398.50\t-67.20\t2\t\n",
            "00KS20181110\t315.31749\t11112018\t7:37:11\t28.93580\t-88.42700\t345.306\t-999\t410.29\t1021.3\t1019.95\t26.22\t25.9005\t34.5285\t331.09\t398.46\t-67.37\t2\t\n",
            "00KS20181110\t315.31929\t11112018\t7:39:47\t28.93330\t-88.42800\t345.901\t-999\t410.29\t1022.4\t1019.98\t26.22\t25.9657\t34.5463\t332.95\t398.42\t-65.47\t2\t\n",
            "00KS20181110\t315.32111\t11112018\t7:42:24\t28.93080\t-88.42900\t353.374\t-999\t410.28\t1021.9\t1019.95\t26.51\t26.1847\t34.6928\t338.76\t398.23\t-59.47\t2\t\n",
            "00KS20181110\t315.32292\t11112018\t7:45:00\t28.92830\t-88.43000\t363.687\t-999\t410.27\t1021.8\t1019.87\t26.64\t26.3337\t34.7458\t348.80\t398.08\t-49.28\t2\t\n",
            "00KS20181110\t315.32473\t11112018\t7:47:37\t28.92590\t-88.43090\t371.241\t-999\t410.27\t1021.5\t1019.96\t26.65\t26.3691\t34.7036\t356.31\t398.08\t-41.77\t2\t\n",
            "00KS20181110\t315.32654\t11112018\t7:50:13\t28.92340\t-88.43200\t373.749\t-999\t410.26\t1022.0\t1019.77\t26.59\t26.314\t34.6013\t359.02\t398.04\t-39.02\t2\t\n",
            "00KS20181110\t315.32834\t11112018\t7:52:49\t28.92090\t-88.43300\t374.222\t-999\t410.26\t1021.2\t1019.65\t26.57\t26.297\t34.5771\t359.24\t398.00\t-38.76\t2\t\n",
            "00KS20181110\t315.33016\t11112018\t7:55:26\t28.91850\t-88.43410\t374.272\t-999\t410.25\t1021.9\t1019.69\t26.56\t26.2729\t34.5669\t359.34\t398.03\t-38.69\t2\t\n",
            "00KS20181110\t315.33378\t11112018\t8:00:39\t28.91350\t-88.43620\t371.139\t-999\t410.24\t1021.5\t1019.8\t26.42\t26.1458\t34.4616\t356.48\t398.16\t-41.68\t2\t\n",
            "00KS20181110\t315.33560\t11112018\t8:03:16\t28.91100\t-88.43730\t370.634\t-999\t410.23\t1021.8\t1019.78\t26.44\t26.1326\t34.4358\t355.59\t398.16\t-42.57\t2\t\n",
            "00KS20181110\t315.33741\t11112018\t8:05:52\t28.90850\t-88.43840\t370.687\t-999\t410.23\t1022.1\t1019.64\t26.43\t26.122\t34.4227\t355.74\t398.10\t-42.36\t2\t\n",
            "00KS20181110\t315.33922\t11112018\t8:08:29\t28.90600\t-88.43950\t370.482\t-999\t410.22\t1021.1\t1019.58\t26.39\t26.098\t34.3911\t355.46\t398.09\t-42.63\t2\t\n",
            "00KS20181110\t315.34103\t11112018\t8:11:05\t28.90350\t-88.44060\t369.333\t-999\t410.21\t1022.1\t1019.37\t26.36\t26.0665\t34.3356\t354.71\t398.02\t-43.31\t2\t\n",
            "00KS20181110\t315.34285\t11112018\t8:13:42\t28.90100\t-88.44170\t368.144\t-999\t410.21\t1022.0\t1019.38\t26.37\t26.0601\t34.3068\t353.28\t398.03\t-44.75\t2\t\n",
            "00KS20181110\t315.34465\t11112018\t8:16:18\t28.89860\t-88.44280\t367.740\t-999\t410.20\t1020.8\t1019.39\t26.37\t26.0703\t34.2957\t352.62\t398.02\t-45.40\t2\t\n",
            "00KS20181110\t315.34647\t11112018\t8:18:55\t28.89610\t-88.44390\t367.640\t-999\t410.20\t1022.0\t1019.56\t26.37\t26.0813\t34.2867\t353.11\t398.07\t-44.96\t2\t\n",
            "00KS20181110\t315.34829\t11112018\t8:21:32\t28.89370\t-88.44500\t367.629\t-999\t410.19\t1021.8\t1019.55\t26.39\t26.0851\t34.282\t352.77\t398.06\t-45.28\t2\t\n",
            "00KS20181110\t315.35010\t11112018\t8:24:09\t28.89130\t-88.44610\t367.387\t-999\t410.18\t1020.9\t1019.38\t26.37\t26.0726\t34.2765\t352.35\t397.99\t-45.65\t2\t\n",
            "00KS20181110\t315.35192\t11112018\t8:26:46\t28.88890\t-88.44720\t367.245\t-999\t410.18\t1022.1\t1019.52\t26.41\t26.0833\t34.2675\t352.17\t398.04\t-45.86\t2\t\n",
            "00KS20181110\t315.35373\t11112018\t8:29:22\t28.88650\t-88.44830\t367.396\t-999\t410.17\t1021.1\t1019.42\t26.38\t26.0783\t34.2471\t352.36\t397.99\t-45.64\t2\t\n",
            "00KS20181110\t315.35554\t11112018\t8:31:59\t28.88400\t-88.44950\t367.234\t-999\t410.17\t1021.7\t1019.4\t26.39\t26.0742\t34.2449\t352.20\t397.98\t-45.79\t2\t\n",
            "00KS20181110\t315.35759\t11112018\t8:34:56\t28.88130\t-88.45070\t367.001\t-999\t410.16\t1021.2\t1019.38\t26.37\t26.0505\t34.2202\t351.76\t397.99\t-46.23\t2\t\n",
            "00KS20181110\t315.35940\t11112018\t8:37:32\t28.87890\t-88.45170\t366.497\t-999\t410.15\t1021.1\t1019.47\t26.33\t26.0264\t34.2155\t351.50\t398.04\t-46.54\t2\t\n",
            "00KS20181110\t315.36122\t11112018\t8:40:09\t28.87670\t-88.45280\t365.913\t-999\t410.15\t1021.3\t1019.48\t26.32\t26.0014\t34.2071\t350.80\t398.05\t-47.26\t2\t\n",
            "00KS20181110\t315.36303\t11112018\t8:42:46\t28.87440\t-88.45390\t364.210\t-999\t410.14\t1021.5\t1019.47\t26.29\t25.9739\t34.137\t349.29\t398.06\t-48.77\t2\t\n",
            "00KS20181110\t315.36484\t11112018\t8:45:22\t28.87210\t-88.45500\t363.188\t-999\t410.14\t1019.7\t1019.52\t26.30\t25.9766\t34.1192\t347.56\t398.08\t-50.51\t2\t\n",
            "00KS20181110\t315.38072\t11112018\t9:08:14\t28.85170\t-88.46340\t-999\t409.910\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t315.38182\t11112018\t9:09:49\t28.85030\t-88.46400\t-999\t410.673\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t315.38292\t11112018\t9:11:24\t28.84900\t-88.46450\t-999\t409.887\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t315.38402\t11112018\t9:12:59\t28.84770\t-88.46500\t-999\t409.946\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t315.38510\t11112018\t9:14:33\t28.84640\t-88.46550\t-999\t409.964\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t315.38856\t11112018\t9:19:32\t28.84210\t-88.46700\t364.077\t-999\t410.10\t1019.4\t1019.07\t26.32\t25.9951\t34.0939\t348.27\t397.85\t-49.57\t2\t\n",
            "00KS20181110\t315.39039\t11112018\t9:22:10\t28.83980\t-88.46790\t363.412\t-999\t410.11\t1021.2\t1019.31\t26.32\t25.9928\t34.0721\t348.24\t397.95\t-49.72\t2\t\n",
            "00KS20181110\t315.39221\t11112018\t9:24:47\t28.83760\t-88.46870\t363.410\t-999\t410.11\t1020.3\t1019.28\t26.33\t25.9924\t34.0883\t347.76\t397.95\t-50.19\t2\t\n",
            "00KS20181110\t315.39402\t11112018\t9:27:23\t28.83530\t-88.46960\t363.335\t-999\t410.12\t1021.0\t1019.36\t26.33\t25.9941\t34.079\t347.96\t397.98\t-50.03\t2\t\n",
            "00KS20181110\t315.39583\t11112018\t9:30:00\t28.83310\t-88.47040\t362.940\t-999\t410.13\t1021.1\t1019.29\t26.31\t25.9794\t34.0541\t347.70\t397.97\t-50.27\t2\t\n",
            "00KS20181110\t315.39764\t11112018\t9:32:36\t28.83090\t-88.47130\t362.432\t-999\t410.14\t1020.3\t1019.05\t26.28\t25.9521\t34.0226\t347.00\t397.91\t-50.91\t2\t\n",
            "00KS20181110\t315.39969\t11112018\t9:35:33\t28.82850\t-88.47230\t361.775\t-999\t410.14\t1019.9\t1018.94\t26.27\t25.9271\t34.0137\t346.02\t397.89\t-51.87\t2\t\n",
            "00KS20181110\t315.40150\t11112018\t9:38:10\t28.82630\t-88.47310\t361.421\t-999\t410.15\t1021.1\t1019.15\t26.26\t25.9119\t34.0015\t346.03\t397.99\t-51.96\t2\t\n",
            "00KS20181110\t315.40331\t11112018\t9:40:46\t28.82420\t-88.47400\t361.328\t-999\t410.16\t1019.8\t1019.28\t26.26\t25.9096\t33.9987\t345.45\t398.05\t-52.60\t2\t\n",
            "00KS20181110\t315.40514\t11112018\t9:43:24\t28.82210\t-88.47480\t361.110\t-999\t410.17\t1019.6\t1019.15\t26.24\t25.9023\t33.9953\t345.37\t398.01\t-52.64\t2\t\n",
            "00KS20181110\t315.40694\t11112018\t9:46:00\t28.82000\t-88.47560\t360.771\t-999\t410.17\t1020.2\t1019.27\t26.24\t25.9006\t33.9969\t345.23\t398.07\t-52.84\t2\t\n",
            "00KS20181110\t315.40875\t11112018\t9:48:36\t28.81780\t-88.47640\t360.680\t-999\t410.18\t1020.1\t1019.27\t26.25\t25.9103\t34.0001\t345.10\t398.07\t-52.97\t2\t\n",
            "00KS20181110\t315.41057\t11112018\t9:51:13\t28.81570\t-88.47720\t360.939\t-999\t410.19\t1020.5\t1019.3\t26.27\t25.9276\t34.0206\t345.43\t398.08\t-52.64\t2\t\n",
            "00KS20181110\t315.41238\t11112018\t9:53:50\t28.81340\t-88.47810\t361.402\t-999\t410.19\t1020.5\t1019.05\t26.27\t25.9351\t34.0337\t345.99\t397.98\t-51.99\t2\t\n",
            "00KS20181110\t315.41419\t11112018\t9:56:26\t28.81130\t-88.47890\t361.954\t-999\t410.20\t1019.3\t1019.2\t26.28\t25.9398\t34.0564\t346.01\t398.04\t-52.03\t2\t\n",
            "00KS20181110\t315.41600\t11112018\t9:59:02\t28.80930\t-88.47970\t362.769\t-999\t410.21\t1020.5\t1019.15\t26.31\t25.9622\t34.0715\t347.08\t398.01\t-50.93\t2\t\n",
            "00KS20181110\t315.41781\t11112018\t10:01:39\t28.80710\t-88.48050\t363.281\t-999\t410.22\t1020.8\t1019.13\t26.30\t25.9593\t34.0709\t347.78\t398.01\t-50.23\t2\t\n",
            "00KS20181110\t315.41963\t11112018\t10:04:16\t28.80500\t-88.48120\t363.450\t-999\t410.22\t1020.8\t1019.17\t26.28\t25.9477\t34.0618\t348.08\t398.04\t-49.96\t2\t\n",
            "00KS20181110\t315.42144\t11112018\t10:06:52\t28.80290\t-88.48200\t363.087\t-999\t410.23\t1020.4\t1019.08\t26.27\t25.9333\t34.0516\t347.54\t398.03\t-50.49\t2\t\n",
            "00KS20181110\t315.42325\t11112018\t10:09:29\t28.80070\t-88.48280\t363.407\t-999\t410.24\t1020.4\t1019.09\t26.30\t25.9552\t34.076\t347.70\t398.02\t-50.32\t2\t\n",
            "00KS20181110\t315.42506\t11112018\t10:12:05\t28.80060\t-88.48480\t364.807\t-999\t410.25\t1020.6\t1019.14\t26.32\t25.9767\t34.0912\t349.12\t398.03\t-48.91\t2\t\n",
            "00KS20181110\t315.42687\t11112018\t10:14:42\t28.80390\t-88.48480\t365.087\t-999\t410.25\t1019.7\t1018.88\t26.34\t25.9838\t34.0974\t348.87\t397.93\t-49.06\t2\t\n",
            "00KS20181110\t315.42868\t11112018\t10:17:18\t28.80650\t-88.48460\t365.599\t-999\t410.26\t1021.1\t1019.14\t26.32\t25.9634\t34.084\t349.86\t398.05\t-48.19\t2\t\n",
            "00KS20181110\t315.43050\t11112018\t10:19:55\t28.80900\t-88.48440\t365.698\t-999\t410.27\t1020.0\t1019.15\t26.31\t25.9369\t34.0803\t349.33\t398.09\t-48.76\t2\t\n",
            "00KS20181110\t315.43231\t11112018\t10:22:32\t28.81160\t-88.48430\t365.616\t-999\t410.28\t1020.6\t1019.28\t26.30\t25.9279\t34.0897\t349.48\t398.15\t-48.67\t2\t\n",
            "00KS20181110\t315.43413\t11112018\t10:25:09\t28.81410\t-88.48420\t365.736\t-999\t410.28\t1020.3\t1019.11\t26.30\t25.9283\t34.0988\t349.50\t398.09\t-48.59\t2\t\n",
            "00KS20181110\t315.43594\t11112018\t10:27:45\t28.81650\t-88.48420\t365.643\t-999\t410.29\t1020.3\t1019.13\t26.27\t25.9154\t34.0923\t349.68\t398.12\t-48.43\t2\t\n",
            "00KS20181110\t315.43775\t11112018\t10:30:22\t28.81900\t-88.48410\t365.461\t-999\t410.30\t1020.5\t1019.05\t26.26\t25.9126\t34.0944\t349.69\t398.09\t-48.40\t2\t\n",
            "00KS20181110\t315.43957\t11112018\t10:32:59\t28.82130\t-88.48410\t365.549\t-999\t410.30\t1019.7\t1019.08\t26.30\t25.9289\t34.0983\t349.12\t398.10\t-48.98\t2\t\n",
            "00KS20181110\t315.44162\t11112018\t10:35:56\t28.82400\t-88.48400\t367.321\t-999\t410.31\t1021.2\t1019.05\t26.37\t25.995\t34.1204\t351.24\t398.05\t-46.81\t2\t\n",
            "00KS20181110\t315.44343\t11112018\t10:38:32\t28.82660\t-88.48390\t369.286\t-999\t410.32\t1020.3\t1019.14\t26.41\t26.0335\t34.1244\t352.74\t398.06\t-45.32\t2\t\n",
            "00KS20181110\t315.44525\t11112018\t10:41:10\t28.82930\t-88.48380\t370.280\t-999\t410.33\t1021.4\t1019.06\t26.36\t26.017\t34.11\t354.62\t398.05\t-43.42\t2\t\n",
            "00KS20181110\t315.44706\t11112018\t10:43:46\t28.83200\t-88.48380\t369.986\t-999\t410.33\t1020.4\t1019.04\t26.31\t25.9704\t34.076\t354.07\t398.08\t-44.01\t2\t\n",
            "00KS20181110\t315.44888\t11112018\t10:46:23\t28.83450\t-88.48390\t369.595\t-999\t410.34\t1020.5\t1019.14\t26.31\t25.9544\t34.0721\t353.49\t398.14\t-44.65\t2\t\n",
            "00KS20181110\t315.45068\t11112018\t10:48:59\t28.83700\t-88.48390\t369.502\t-999\t410.35\t1021.2\t1019.02\t26.32\t25.961\t34.0887\t353.59\t398.09\t-44.50\t2\t\n",
            "00KS20181110\t315.45250\t11112018\t10:51:36\t28.83860\t-88.48340\t369.551\t-999\t410.36\t1019.5\t1018.98\t26.30\t25.9551\t34.0882\t353.26\t398.09\t-44.83\t2\t\n",
            "00KS20181110\t315.45432\t11112018\t10:54:13\t28.83980\t-88.48290\t369.076\t-999\t410.36\t1020.2\t1019.17\t26.27\t25.9198\t34.0696\t353.00\t398.20\t-45.20\t2\t\n",
            "00KS20181110\t315.45612\t11112018\t10:56:49\t28.84120\t-88.48210\t368.198\t-999\t410.37\t1021.4\t1019.01\t26.23\t25.8868\t34.0625\t352.72\t398.17\t-45.45\t2\t\n",
            "00KS20181110\t315.45794\t11112018\t10:59:26\t28.84160\t-88.48050\t367.129\t-999\t410.38\t1020.9\t1019\t26.22\t25.869\t34.0596\t351.41\t398.19\t-46.78\t2\t\n",
            "00KS20181110\t315.45975\t11112018\t11:02:02\t28.84170\t-88.47900\t366.410\t-999\t410.39\t1020.4\t1018.92\t26.21\t25.8581\t34.0564\t350.53\t398.17\t-47.64\t2\t\n",
            "00KS20181110\t315.46156\t11112018\t11:04:39\t28.84190\t-88.47790\t365.404\t-999\t410.39\t1021.1\t1019.15\t26.22\t25.8577\t34.0451\t349.66\t398.27\t-48.61\t2\t\n",
            "00KS20181110\t315.46338\t11112018\t11:07:16\t28.84250\t-88.47750\t364.667\t-999\t410.40\t1020.4\t1019.07\t26.23\t25.8653\t34.0456\t348.66\t398.24\t-49.57\t2\t\n",
            "00KS20181110\t315.46519\t11112018\t11:09:52\t28.84310\t-88.47780\t364.383\t-999\t410.41\t1020.7\t1019.29\t26.22\t25.8734\t34.0465\t348.77\t398.33\t-49.56\t2\t\n",
            "00KS20181110\t315.46700\t11112018\t11:12:29\t28.84390\t-88.47810\t364.080\t-999\t410.42\t1020.8\t1019.1\t26.21\t25.8758\t34.0422\t348.71\t398.26\t-49.55\t2\t\n",
            "00KS20181110\t315.46881\t11112018\t11:15:05\t28.84480\t-88.47840\t363.783\t-999\t410.42\t1021.0\t1019.28\t26.17\t25.8813\t34.0431\t349.19\t398.33\t-49.14\t2\t\n",
            "00KS20181110\t315.47064\t11112018\t11:17:43\t28.84570\t-88.47870\t363.711\t-999\t410.43\t1021.4\t1019.13\t26.19\t25.8794\t34.0476\t348.93\t398.28\t-49.35\t2\t\n",
            "00KS20181110\t315.47244\t11112018\t11:20:19\t28.84610\t-88.47900\t363.911\t-999\t410.44\t1021.2\t1019.13\t26.18\t25.8791\t34.0482\t349.20\t398.29\t-49.09\t2\t\n",
            "00KS20181110\t315.47426\t11112018\t11:22:56\t28.84680\t-88.47940\t363.970\t-999\t410.44\t1020.6\t1019.26\t26.17\t25.8744\t34.0533\t349.13\t398.35\t-49.22\t2\t\n",
            "00KS20181110\t315.47608\t11112018\t11:25:33\t28.84790\t-88.47950\t364.090\t-999\t410.45\t1021.4\t1019.28\t26.17\t25.8758\t34.0516\t349.55\t398.36\t-48.82\t2\t\n",
            "00KS20181110\t315.47788\t11112018\t11:28:09\t28.84880\t-88.47960\t364.773\t-999\t410.46\t1020.9\t1019.1\t26.15\t25.8866\t34.0503\t350.49\t398.29\t-47.80\t2\t\n",
            "00KS20181110\t315.47970\t11112018\t11:30:46\t28.84980\t-88.47970\t365.600\t-999\t410.47\t1020.2\t1019.21\t26.16\t25.9033\t34.0455\t351.13\t398.33\t-47.20\t2\t\n",
            "00KS20181110\t315.48152\t11112018\t11:33:23\t28.85090\t-88.47920\t366.656\t-999\t410.47\t1020.9\t1019.32\t26.22\t25.9223\t34.0468\t351.74\t398.37\t-46.62\t2\t\n",
            "00KS20181110\t315.48355\t11112018\t11:36:19\t28.85120\t-88.47930\t367.800\t-999\t410.48\t1020.7\t1018.78\t26.25\t25.9245\t34.047\t352.34\t398.15\t-45.82\t2\t\n",
            "00KS20181110\t315.48536\t11112018\t11:38:55\t28.85110\t-88.47930\t368.487\t-999\t410.49\t1020.9\t1018.94\t26.24\t25.916\t34.0328\t353.09\t398.23\t-45.14\t2\t\n",
            "00KS20181110\t315.48718\t11112018\t11:41:32\t28.85100\t-88.47920\t368.885\t-999\t410.50\t1021.3\t1018.85\t26.25\t25.9156\t34.029\t353.46\t398.20\t-44.75\t2\t\n",
            "00KS20181110\t315.48899\t11112018\t11:44:09\t28.85090\t-88.47900\t369.186\t-999\t410.50\t1021.5\t1019.12\t26.25\t25.9269\t34.0333\t353.98\t398.31\t-44.33\t2\t\n",
            "00KS20181110\t315.49080\t11112018\t11:46:45\t28.85060\t-88.47840\t369.365\t-999\t410.51\t1020.8\t1019.05\t26.24\t25.9385\t34.0365\t354.24\t398.28\t-44.04\t2\t\n",
            "00KS20181110\t315.49260\t11112018\t11:49:21\t28.85030\t-88.47790\t369.848\t-999\t410.52\t1020.9\t1019.09\t26.31\t25.9445\t34.0403\t353.73\t398.30\t-44.57\t2\t\n",
            "00KS20181110\t315.49442\t11112018\t11:51:58\t28.85020\t-88.47760\t370.521\t-999\t410.53\t1021.4\t1018.8\t26.29\t25.9443\t34.0358\t354.86\t398.19\t-43.33\t2\t\n",
            "00KS20181110\t315.49625\t11112018\t11:54:36\t28.85010\t-88.47730\t370.458\t-999\t410.53\t1021.4\t1018.95\t26.29\t25.9537\t34.0405\t354.94\t398.25\t-43.31\t2\t\n",
            "00KS20181110\t315.49804\t11112018\t11:57:11\t28.85010\t-88.47690\t370.518\t-999\t410.54\t1021.7\t1019.19\t26.28\t25.9598\t34.0343\t355.36\t398.35\t-42.99\t2\t\n",
            "00KS20181110\t315.49986\t11112018\t11:59:48\t28.85000\t-88.47630\t370.627\t-999\t410.55\t1020.5\t1018.91\t26.29\t25.961\t34.0383\t354.89\t398.24\t-43.35\t2\t\n",
            "00KS20181110\t315.50168\t11112018\t12:02:25\t28.84980\t-88.47570\t370.898\t-999\t410.56\t1020.8\t1018.89\t26.29\t25.9718\t34.0346\t355.42\t398.23\t-42.81\t2\t\n",
            "00KS20181110\t315.50350\t11112018\t12:05:02\t28.84950\t-88.47520\t371.155\t-999\t410.56\t1020.9\t1019.13\t26.32\t25.9822\t34.0378\t355.39\t398.33\t-42.94\t2\t\n",
            "00KS20181110\t315.50530\t11112018\t12:07:38\t28.84940\t-88.47450\t371.721\t-999\t410.57\t1021.6\t1019.11\t26.35\t26.0019\t34.0481\t356.00\t398.31\t-42.31\t2\t\n",
            "00KS20181110\t315.50712\t11112018\t12:10:15\t28.84930\t-88.47390\t372.313\t-999\t410.58\t1018.2\t1019.32\t26.34\t26.0089\t34.0403\t355.61\t398.40\t-42.79\t2\t\n",
            "00KS20181110\t315.50894\t11112018\t12:12:52\t28.84920\t-88.47330\t372.432\t-999\t410.58\t1021.2\t1019.51\t26.34\t26.0098\t34.0384\t356.82\t398.48\t-41.66\t2\t\n",
            "00KS20181110\t315.51075\t11112018\t12:15:29\t28.84900\t-88.47270\t372.293\t-999\t410.59\t1020.7\t1018.89\t26.32\t26.0012\t34.0283\t356.69\t398.25\t-41.55\t2\t\n",
            "00KS20181110\t315.51257\t11112018\t12:18:06\t28.84900\t-88.47260\t372.158\t-999\t410.60\t1021.8\t1019.43\t26.32\t25.9969\t34.028\t356.89\t398.47\t-41.58\t2\t\n",
            "00KS20181110\t315.51439\t11112018\t12:20:43\t28.84900\t-88.47260\t372.126\t-999\t410.61\t1021.9\t1019.21\t26.33\t25.9918\t34.0393\t356.66\t398.40\t-41.73\t2\t\n",
            "00KS20181110\t315.51619\t11112018\t12:23:19\t28.84880\t-88.47210\t372.085\t-999\t410.61\t1021.7\t1019.52\t26.32\t25.9986\t34.0278\t356.81\t398.52\t-41.71\t2\t\n",
            "00KS20181110\t315.51801\t11112018\t12:25:56\t28.84860\t-88.47130\t372.032\t-999\t410.62\t1021.1\t1018.94\t26.32\t25.989\t34.0212\t356.40\t398.30\t-41.90\t2\t\n",
            "00KS20181110\t315.51981\t11112018\t12:28:32\t28.84840\t-88.47060\t372.031\t-999\t410.63\t1021.2\t1019.52\t26.31\t25.9783\t34.0206\t356.43\t398.55\t-42.12\t2\t\n",
            "00KS20181110\t315.52163\t11112018\t12:31:09\t28.84820\t-88.46960\t371.808\t-999\t410.64\t1021.3\t1019.36\t26.30\t25.9719\t34.0182\t356.32\t398.50\t-42.18\t2\t\n",
            "00KS20181110\t315.52345\t11112018\t12:33:46\t28.84800\t-88.46870\t371.645\t-999\t410.64\t1021.9\t1019.38\t26.31\t25.9675\t34.0271\t356.15\t398.52\t-42.37\t2\t\n",
            "00KS20181110\t315.52551\t11112018\t12:36:44\t28.84770\t-88.46790\t371.613\t-999\t410.65\t1021.2\t1019.51\t26.32\t25.9729\t34.037\t355.79\t398.58\t-42.78\t2\t\n",
            "00KS20181110\t315.52731\t11112018\t12:39:20\t28.84760\t-88.46730\t371.742\t-999\t410.66\t1022.3\t1019.72\t26.34\t25.9819\t34.049\t356.13\t398.66\t-42.53\t2\t\n",
            "00KS20181110\t315.52913\t11112018\t12:41:57\t28.84750\t-88.46660\t371.950\t-999\t410.67\t1021.7\t1019.47\t26.34\t25.9926\t34.0527\t356.28\t398.56\t-42.28\t2\t\n",
            "00KS20181110\t315.53094\t11112018\t12:44:33\t28.84730\t-88.46590\t371.898\t-999\t410.67\t1022.1\t1019.48\t26.32\t25.9895\t34.0486\t356.64\t398.57\t-41.93\t2\t\n",
            "00KS20181110\t315.53275\t11112018\t12:47:10\t28.84720\t-88.46510\t372.058\t-999\t410.68\t1023.1\t1019.52\t26.34\t25.9946\t34.0518\t356.91\t398.59\t-41.68\t2\t\n",
            "00KS20181110\t315.53457\t11112018\t12:49:47\t28.84700\t-88.46430\t372.139\t-999\t410.69\t1022.7\t1019.65\t26.35\t25.9938\t34.0578\t356.68\t398.65\t-41.97\t2\t\n",
            "00KS20181110\t315.53639\t11112018\t12:52:24\t28.84680\t-88.46330\t372.118\t-999\t410.70\t1022.7\t1019.81\t26.33\t25.9956\t34.0562\t357.00\t398.72\t-41.72\t2\t\n",
            "00KS20181110\t315.53819\t11112018\t12:55:00\t28.84670\t-88.46250\t372.315\t-999\t410.70\t1019.7\t1020\t26.36\t26.0001\t34.0643\t355.71\t398.80\t-43.10\t2\t\n",
            "00KS20181110\t315.54001\t11112018\t12:57:37\t28.84700\t-88.46130\t372.442\t-999\t410.71\t1021.7\t1020.02\t26.36\t26.0045\t34.0661\t356.61\t398.81\t-42.20\t2\t\n",
            "00KS20181110\t315.54183\t11112018\t13:00:14\t28.84960\t-88.45800\t372.181\t-999\t410.72\t1021.7\t1019.99\t26.36\t26.0075\t34.0618\t356.41\t398.81\t-42.40\t2\t\n",
            "00KS20181110\t315.54365\t11112018\t13:02:51\t28.85360\t-88.45260\t372.288\t-999\t410.73\t1018.9\t1020.07\t26.38\t26.0095\t34.0761\t355.22\t398.84\t-43.63\t2\t\n",
            "00KS20181110\t315.54546\t11112018\t13:05:28\t28.85810\t-88.44660\t371.578\t-999\t410.73\t1020.7\t1020.01\t26.34\t25.9593\t34.091\t355.06\t398.87\t-43.80\t2\t\n",
            "00KS20181110\t315.54727\t11112018\t13:08:04\t28.86270\t-88.44050\t369.753\t-999\t410.74\t1020.2\t1020.1\t26.40\t25.9303\t34.0894\t351.77\t398.93\t-47.16\t2\t\n",
            "00KS20181110\t315.54909\t11112018\t13:10:41\t28.86710\t-88.43380\t368.935\t-999\t410.75\t1020.6\t1020.09\t26.47\t25.9468\t34.0856\t350.29\t398.92\t-48.63\t2\t\n",
            "00KS20181110\t315.55089\t11112018\t13:13:17\t28.87140\t-88.42730\t368.266\t-999\t410.75\t1019.4\t1020.07\t26.45\t25.9103\t34.047\t349.00\t398.95\t-49.95\t2\t\n",
            "00KS20181110\t315.55271\t11112018\t13:15:54\t28.87570\t-88.42060\t366.864\t-999\t410.76\t1020.3\t1020.19\t26.45\t25.877\t34.0075\t347.50\t399.03\t-51.53\t2\t\n",
            "00KS20181110\t315.55453\t11112018\t13:18:31\t28.88000\t-88.41400\t366.237\t-999\t410.77\t1020.8\t1020.29\t26.50\t25.9073\t34.0271\t346.76\t399.05\t-52.30\t2\t\n",
            "00KS20181110\t315.55633\t11112018\t13:21:07\t28.88410\t-88.40720\t366.992\t-999\t410.78\t1021.1\t1020.31\t26.57\t25.9626\t34.1058\t347.31\t399.03\t-51.71\t2\t\n",
            "00KS20181110\t315.55816\t11112018\t13:23:45\t28.88830\t-88.40050\t367.447\t-999\t410.78\t1020.9\t1020.41\t26.59\t25.9775\t34.0999\t347.59\t399.06\t-51.48\t2\t\n",
            "00KS20181110\t315.55997\t11112018\t13:26:21\t28.89240\t-88.39380\t367.744\t-999\t410.79\t1021.1\t1020.37\t26.60\t25.9921\t34.0933\t348.00\t399.04\t-51.04\t2\t\n",
            "00KS20181110\t315.56177\t11112018\t13:28:57\t28.89650\t-88.38700\t367.823\t-999\t410.80\t1021.1\t1020.42\t26.61\t25.9977\t34.09\t348.00\t399.07\t-51.06\t2\t\n",
            "00KS20181110\t315.56360\t11112018\t13:31:35\t28.90070\t-88.38020\t368.265\t-999\t410.81\t1021.1\t1020.4\t26.63\t26.0069\t34.1062\t348.25\t399.06\t-50.81\t2\t\n",
            "00KS20181110\t315.56542\t11112018\t13:34:12\t28.90480\t-88.37340\t367.941\t-999\t410.81\t1021.0\t1020.56\t26.58\t25.9716\t34.1137\t348.16\t399.16\t-51.00\t2\t\n",
            "00KS20181110\t315.56748\t11112018\t13:37:10\t28.90950\t-88.36560\t368.356\t-999\t410.82\t1021.0\t1020.52\t26.60\t25.983\t34.2\t348.41\t399.14\t-50.73\t2\t\n",
            "00KS20181110\t315.56929\t11112018\t13:39:47\t28.91360\t-88.35870\t369.684\t-999\t410.83\t1020.9\t1020.63\t26.64\t25.9946\t34.2436\t349.18\t399.18\t-50.00\t2\t\n",
            "00KS20181110\t315.58521\t11112018\t14:02:42\t28.95110\t-88.30080\t-999\t410.916\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t315.58631\t11112018\t14:04:17\t28.95370\t-88.29690\t-999\t410.954\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t315.58740\t11112018\t14:05:51\t28.95630\t-88.29310\t-999\t410.913\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t315.58850\t11112018\t14:07:26\t28.95890\t-88.28920\t-999\t410.899\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t315.58959\t11112018\t14:09:01\t28.96160\t-88.28540\t-999\t410.827\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t-999\t2\t\n",
            "00KS20181110\t315.59307\t11112018\t14:14:01\t28.96990\t-88.27340\t353.949\t-999\t410.96\t1021.8\t1020.89\t26.12\t25.4649\t34.8111\t334.83\t399.82\t-64.99\t2\t\n",
            "00KS20181110\t315.59488\t11112018\t14:16:38\t28.97430\t-88.26710\t354.227\t-999\t410.98\t1022.1\t1020.96\t26.19\t25.4964\t35.0063\t334.61\t399.85\t-65.24\t2\t\n",
            "00KS20181110\t315.59670\t11112018\t14:19:15\t28.97850\t-88.26060\t356.502\t-999\t411.00\t1022.1\t1020.87\t26.18\t25.5187\t35.0932\t337.22\t399.81\t-62.59\t2\t\n",
            "00KS20181110\t315.59851\t11112018\t14:21:51\t28.98280\t-88.25430\t358.521\t-999\t411.02\t1022.6\t1020.85\t26.18\t25.5083\t35.1264\t339.16\t399.83\t-60.67\t2\t\n",
            "00KS20181110\t315.60034\t11112018\t14:24:29\t28.98720\t-88.24810\t359.698\t-999\t411.03\t1022.6\t1020.93\t26.14\t25.4832\t35.1023\t340.51\t399.90\t-59.39\t2\t\n",
            "00KS20181110\t315.60214\t11112018\t14:27:05\t28.99100\t-88.24200\t359.675\t-999\t411.05\t1021.2\t1020.89\t26.07\t25.519\t35.1778\t341.58\t399.88\t-58.30\t2\t\n",
            "00KS20181110\t315.60396\t11112018\t14:29:42\t28.99490\t-88.23590\t366.764\t-999\t411.07\t1022.3\t1020.91\t26.45\t25.7661\t35.7638\t346.49\t399.72\t-53.23\t2\t\n",
            "00KS20181110\t315.60576\t11112018\t14:32:18\t28.99870\t-88.22980\t377.042\t-999\t411.09\t1022.2\t1020.9\t26.55\t25.916\t35.8442\t356.85\t399.62\t-42.77\t2\t\n",
            "00KS20181110\t315.60759\t11112018\t14:34:56\t29.00270\t-88.22360\t382.179\t-999\t411.11\t1022.6\t1020.96\t26.56\t25.9432\t35.844\t362.11\t399.64\t-37.53\t2\t\n",
            "00KS20181110\t315.60965\t11112018\t14:37:54\t29.00720\t-88.21660\t384.928\t-999\t411.13\t1022.4\t1020.98\t26.60\t25.9608\t35.8402\t364.27\t399.65\t-35.39\t2\t\n",
            "00KS20181110\t315.61146\t11112018\t14:40:30\t29.01110\t-88.21040\t386.175\t-999\t411.15\t1021.0\t1020.93\t26.61\t25.9725\t35.8318\t364.95\t399.64\t-34.69\t2\t\n",
            "00KS20181110\t315.61328\t11112018\t14:43:07\t29.01510\t-88.20420\t386.929\t-999\t411.17\t1021.8\t1020.96\t26.63\t25.9829\t35.8261\t365.80\t399.67\t-33.87\t2\t\n",
            "00KS20181110\t315.61508\t11112018\t14:45:43\t29.01910\t-88.19800\t387.490\t-999\t411.19\t1020.8\t1020.97\t26.64\t25.9989\t35.8145\t366.04\t399.68\t-33.63\t2\t\n",
            "00KS20181110\t315.61691\t11112018\t14:48:21\t29.02310\t-88.19170\t388.083\t-999\t411.20\t1021.7\t1020.97\t26.66\t26.0195\t35.8155\t366.93\t399.68\t-32.75\t2\t\n",
            "00KS20181110\t315.61872\t11112018\t14:50:57\t29.02710\t-88.18540\t388.745\t-999\t411.22\t1021.4\t1021.05\t26.68\t26.0316\t35.8172\t367.31\t399.72\t-32.41\t2\t\n",
            "00KS20181110\t315.62053\t11112018\t14:53:34\t29.03120\t-88.17910\t388.642\t-999\t411.24\t1021.4\t1020.99\t26.68\t26.0422\t35.8159\t367.37\t399.70\t-32.33\t2\t\n",
            "00KS20181110\t315.62234\t11112018\t14:56:10\t29.03510\t-88.17290\t388.569\t-999\t411.26\t1020.3\t1021.07\t26.70\t26.0541\t35.8252\t366.76\t399.75\t-32.99\t2\t\n",
            "00KS20181110\t315.62417\t11112018\t14:58:48\t29.03920\t-88.16650\t388.738\t-999\t411.28\t1020.6\t1021.07\t26.70\t26.0594\t35.8254\t367.11\t399.76\t-32.65\t2\t\n",
            "00KS20181110\t315.62597\t11112018\t15:01:24\t29.04320\t-88.16020\t388.907\t-999\t411.30\t1021.0\t1021.08\t26.71\t26.0614\t35.8155\t367.29\t399.78\t-32.49\t2\t\n",
            "00KS20181110\t315.62779\t11112018\t15:04:01\t29.04720\t-88.15380\t389.550\t-999\t411.32\t1021.4\t1021.03\t26.71\t26.0631\t35.8079\t368.07\t399.78\t-31.71\t2\t\n",
            "00KS20181110\t315.62959\t11112018\t15:06:37\t29.05120\t-88.14750\t389.790\t-999\t411.33\t1021.5\t1020.99\t26.71\t26.0594\t35.8014\t368.27\t399.78\t-31.51\t2\t\n",
            "00KS20181110\t315.63141\t11112018\t15:09:14\t29.05520\t-88.14120\t390.452\t-999\t411.35\t1021.8\t1020.97\t26.72\t26.0692\t35.8069\t369.00\t399.78\t-30.78\t2\t\n",
            "00KS20181110\t315.63325\t11112018\t15:11:53\t29.05930\t-88.13470\t390.582\t-999\t411.37\t1021.7\t1021\t26.73\t26.0783\t35.8114\t369.07\t399.81\t-30.74\t2\t\n",
            "00KS20181110\t315.63506\t11112018\t15:14:29\t29.06330\t-88.12840\t390.598\t-999\t411.39\t1020.9\t1020.76\t26.75\t26.0867\t35.8118\t368.59\t399.72\t-31.14\t2\t\n",
            "00KS20181110\t315.63687\t11112018\t15:17:06\t29.06730\t-88.12210\t391.141\t-999\t411.41\t1022.0\t1020.76\t26.75\t26.0987\t35.8126\t369.70\t399.73\t-30.03\t2\t\n",
            "00KS20181110\t315.63869\t11112018\t15:19:43\t29.07130\t-88.11570\t391.440\t-999\t411.43\t1022.2\t1020.82\t26.75\t26.0955\t35.81\t370.00\t399.78\t-29.77\t2\t\n",
            "00KS20181110\t315.64050\t11112018\t15:22:19\t29.07530\t-88.10930\t391.630\t-999\t411.45\t1022.2\t1020.79\t26.75\t26.097\t35.8061\t370.21\t399.78\t-29.57\t2\t\n",
            "00KS20181110\t315.64233\t11112018\t15:24:57\t29.07940\t-88.10290\t391.264\t-999\t411.47\t1020.9\t1020.83\t26.71\t26.0865\t35.8011\t369.87\t399.82\t-29.96\t2\t\n",
            "00KS20181110\t315.64413\t11112018\t15:27:33\t29.08330\t-88.09650\t390.586\t-999\t411.48\t1021.2\t1020.81\t26.69\t26.0751\t35.8065\t369.49\t399.84\t-30.35\t2\t\n",
            "00KS20181110\t315.64595\t11112018\t15:30:10\t29.08740\t-88.09020\t390.604\t-999\t411.50\t1022.1\t1020.86\t26.70\t26.0741\t35.8208\t369.66\t399.88\t-30.22\t2\t\n",
            "00KS20181110\t315.64775\t11112018\t15:32:46\t29.09160\t-88.08410\t391.559\t-999\t411.52\t1021.9\t1020.88\t26.72\t26.0951\t35.834\t370.49\t399.89\t-29.40\t2\t\n",
            "00KS20181110\t315.64957\t11112018\t15:35:23\t29.09530\t-88.07750\t392.464\t-999\t411.54\t1022.4\t1020.84\t26.73\t26.1062\t35.8358\t371.54\t399.88\t-28.34\t2\t\n",
            "00KS20181110\t315.65164\t11112018\t15:38:22\t29.09950\t-88.06990\t392.915\t-999\t411.56\t1021.9\t1020.72\t26.73\t26.113\t35.833\t371.89\t399.85\t-27.96\t2\t\n",
            "00KS20181110\t315.65345\t11112018\t15:40:58\t29.10310\t-88.06330\t393.154\t-999\t411.58\t1021.4\t1020.72\t26.75\t26.109\t35.8298\t371.54\t399.87\t-28.34\t2\t\n",
            "00KS20181110\t315.65527\t11112018\t15:43:35\t29.10680\t-88.05670\t393.001\t-999\t411.60\t1022.1\t1020.71\t26.76\t26.103\t35.8272\t371.40\t399.89\t-28.50\t2\t\n",
            "00KS20181110\t315.65707\t11112018\t15:46:11\t29.11050\t-88.05010\t393.140\t-999\t411.62\t1021.9\t1020.73\t26.77\t26.1109\t35.8357\t371.41\t399.91\t-28.50\t2\t\n",
            "00KS20181110\t315.65890\t11112018\t15:48:49\t29.11420\t-88.04340\t393.258\t-999\t411.64\t1021.2\t1020.67\t26.78\t26.1178\t35.8503\t371.20\t399.90\t-28.70\t2\t\n",
            "00KS20181110\t315.66071\t11112018\t15:51:25\t29.11790\t-88.03690\t393.508\t-999\t411.65\t1022.1\t1020.8\t26.78\t26.1241\t35.8576\t371.88\t399.96\t-28.09\t2\t\n",
            "00KS20181110\t315.66252\t11112018\t15:54:02\t29.12160\t-88.03030\t393.686\t-999\t411.67\t1021.7\t1020.89\t26.78\t26.1219\t35.8613\t371.86\t400.02\t-28.16\t2\t\n",
            "00KS20181110\t315.66433\t11112018\t15:56:38\t29.12530\t-88.02360\t393.624\t-999\t411.69\t1022.1\t1020.91\t26.77\t26.1156\t35.8599\t372.02\t400.05\t-28.03\t2\t\n",
            "00KS20181110\t315.66616\t11112018\t15:59:16\t29.12900\t-88.01690\t393.501\t-999\t411.71\t1022.1\t1021.05\t26.76\t26.1011\t35.8502\t371.84\t400.14\t-28.30\t2\t\n",
            "00KS20181110\t315.66796\t11112018\t16:01:52\t29.13270\t-88.01020\t393.367\t-999\t411.73\t1021.4\t1020.99\t26.77\t26.1114\t35.8603\t371.45\t400.12\t-28.68\t2\t\n",
            "00KS20181110\t315.66978\t11112018\t16:04:29\t29.13650\t-88.00350\t394.302\t-999\t411.75\t1021.6\t1021.03\t26.82\t26.1487\t35.8977\t372.17\t400.13\t-27.96\t2\t\n",
            "00KS20181110\t315.67159\t11112018\t16:07:05\t29.14020\t-87.99690\t395.267\t-999\t411.77\t1022.3\t1021.11\t26.83\t26.1618\t35.9039\t373.38\t400.17\t-26.79\t2\t\n",
            "00KS20181110\t315.67329\t11112018\t16:09:32\t29.14370\t-87.99050\t395.991\t-999\t411.78\t1021.8\t1021.08\t26.83\t26.1533\t35.9054\t373.74\t400.18\t-26.44\t2\t\n",
            "00KS20181110\t315.67499\t11112018\t16:11:59\t29.14720\t-87.98420\t396.744\t-999\t411.80\t1022.2\t1021.06\t26.82\t26.1589\t35.9077\t374.86\t400.19\t-25.32\t2\t\n",
            "00KS20181110\t315.67669\t11112018\t16:14:26\t29.15070\t-87.97790\t397.105\t-999\t411.82\t1021.7\t1021.05\t26.83\t26.1645\t35.9162\t374.94\t400.19\t-25.26\t2\t\n",
            "00KS20181110\t315.67838\t11112018\t16:16:52\t29.15430\t-87.97160\t397.535\t-999\t411.83\t1021.5\t1021.11\t26.84\t26.17\t35.9301\t375.19\t400.23\t-25.04\t2\t\n",
            "00KS20181110\t315.68009\t11112018\t16:19:20\t29.15790\t-87.96530\t397.856\t-999\t411.85\t1021.4\t1021.01\t26.84\t26.1744\t35.9304\t375.52\t400.20\t-24.68\t2\t\n",
            "00KS20181110\t315.68178\t11112018\t16:21:46\t29.16160\t-87.95920\t398.086\t-999\t411.87\t1021.3\t1021.03\t26.84\t26.1725\t35.9304\t375.67\t400.23\t-24.56\t2\t\n",
            "00KS20181110\t315.68348\t11112018\t16:24:13\t29.16570\t-87.95330\t398.205\t-999\t411.89\t1021.4\t1020.98\t26.83\t26.1644\t35.9295\t375.86\t400.23\t-24.38\t2\t\n",
            "00KS20181110\t315.68519\t11112018\t16:26:40\t29.16970\t-87.94740\t397.698\t-999\t411.90\t1021.6\t1020.96\t26.82\t26.1487\t35.9281\t375.37\t400.25\t-24.88\t2\t\n",
            "00KS20181110\t315.68687\t11112018\t16:29:06\t29.17370\t-87.94140\t397.212\t-999\t411.92\t1021.7\t1021.03\t26.82\t26.1508\t35.9358\t374.99\t400.30\t-25.31\t2\t\n",
            "00KS20181110\t315.68859\t11112018\t16:31:34\t29.17780\t-87.93540\t397.512\t-999\t411.94\t1021.8\t1021.02\t26.84\t26.1648\t35.9455\t375.20\t400.30\t-25.10\t2\t\n",
            "00KS20181110\t315.69028\t11112018\t16:34:00\t29.18180\t-87.92950\t397.430\t-999\t411.96\t1022.0\t1020.97\t26.83\t26.1655\t35.9535\t375.37\t400.30\t-24.92\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.69221\t11112018\t16:36:47\t29.18640\t-87.92270\t397.598\t-999\t411.98\t1019.8\t1020.87\t26.85\t26.1859\t35.9627\t374.69\t400.26\t-25.57\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.69391\t11112018\t16:39:14\t29.19050\t-87.91670\t398.009\t-999\t411.99\t1021.5\t1020.88\t26.84\t26.1852\t35.9708\t375.88\t400.28\t-24.41\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.69560\t11112018\t16:41:40\t29.19460\t-87.91080\t398.702\t-999\t412.01\t1021.5\t1020.9\t26.84\t26.1845\t35.9789\t376.52\t400.31\t-23.79\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.69731\t11112018\t16:44:08\t29.19880\t-87.90480\t399.506\t-999\t412.03\t1022.2\t1020.92\t26.86\t26.1838\t35.987\t377.20\t400.33\t-23.13\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.69902\t11112018\t16:46:35\t29.20290\t-87.89880\t401.005\t-999\t412.05\t1021.6\t1020.9\t26.87\t26.1831\t35.9952\t378.21\t400.34\t-22.14\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.70071\t11112018\t16:49:01\t29.20710\t-87.89290\t402.958\t-999\t412.06\t1022.4\t1020.85\t26.86\t26.1824\t36.0032\t380.51\t400.34\t-19.83\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.70241\t11112018\t16:51:28\t29.21120\t-87.88690\t403.883\t-999\t412.08\t1022.6\t1020.77\t26.86\t26.1816\t36.0113\t381.45\t400.32\t-18.87\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.70411\t11112018\t16:53:55\t29.21530\t-87.88080\t404.837\t-999\t412.10\t1020.9\t1020.79\t26.84\t26.1809\t36.0194\t382.02\t400.35\t-18.33\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.70581\t11112018\t16:56:22\t29.21940\t-87.87500\t404.503\t-999\t412.12\t1022.5\t1020.74\t26.75\t26.1802\t36.0275\t383.84\t400.35\t-16.51\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.70751\t11112018\t16:58:49\t29.22350\t-87.86910\t404.219\t-999\t412.13\t1021.5\t1020.77\t26.66\t26.1795\t36.0356\t384.71\t400.38\t-15.67\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.70921\t11112018\t17:01:16\t29.22760\t-87.86330\t404.308\t-999\t412.15\t1021.6\t1020.81\t26.60\t26.1788\t36.0437\t385.84\t400.41\t-14.57\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.71091\t11112018\t17:03:43\t29.23180\t-87.85750\t404.335\t-999\t412.17\t1020.6\t1020.71\t26.59\t26.1781\t36.0518\t385.64\t400.39\t-14.75\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.71260\t11112018\t17:06:09\t29.23600\t-87.85200\t404.222\t-999\t412.19\t1021.8\t1020.66\t26.59\t26.1774\t36.0598\t385.99\t400.39\t-14.40\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.71431\t11112018\t17:08:36\t29.24010\t-87.84650\t403.937\t-999\t412.20\t1020.9\t1020.6\t26.62\t26.1767\t36.068\t384.84\t400.38\t-15.54\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.71601\t11112018\t17:11:03\t29.24430\t-87.84100\t402.917\t-999\t412.22\t1019.8\t1020.51\t26.67\t26.176\t36.0761\t382.58\t400.36\t-17.78\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.71771\t11112018\t17:13:30\t29.24840\t-87.83550\t401.011\t-999\t412.24\t1020.2\t1020.52\t26.71\t26.1752\t36.0842\t380.24\t400.38\t-20.14\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.71941\t11112018\t17:15:57\t29.25240\t-87.82990\t400.284\t-999\t412.26\t1019.7\t1020.45\t26.73\t26.1745\t36.0923\t379.01\t400.37\t-21.36\t3\tquestionable/interpolated SSS\n",
            "00KS20181110\t315.72110\t11112018\t17:18:23\t29.25640\t-87.82430\t400.181\t-999\t412.27\t1020.6\t1020.44\t26.77\t26.1738\t36.1003\t378.57\t400.38\t-21.81\t2\t\n",
            "00KS20181110\t315.72280\t11112018\t17:20:50\t29.26030\t-87.81860\t400.571\t-999\t412.29\t1020.6\t1020.41\t26.83\t26.1731\t36.1011\t377.93\t400.39\t-22.46\t2\t\n",
            "00KS20181110\t315.72450\t11112018\t17:23:17\t29.26420\t-87.81280\t401.214\t-999\t412.31\t1020.2\t1020.4\t26.85\t26.1736\t36.1031\t378.05\t400.40\t-22.35\t2\t\n",
            "00KS20181110\t315.72620\t11112018\t17:25:44\t29.26810\t-87.80700\t401.836\t-999\t412.33\t1020.3\t1020.35\t26.86\t26.1758\t36.1074\t378.54\t400.40\t-21.85\t2\t\n",
            "00KS20181110\t315.72791\t11112018\t17:28:11\t29.27200\t-87.80130\t401.390\t-999\t412.34\t1020.2\t1020.33\t26.82\t26.1384\t36.0927\t378.16\t400.43\t-22.28\t2\t\n",
            "00KS20181110\t315.72961\t11112018\t17:30:38\t29.27590\t-87.79560\t400.350\t-999\t412.36\t1021.4\t1020.28\t26.82\t26.142\t36.088\t377.69\t400.43\t-22.74\t2\t\n",
            "00KS20181110\t315.73131\t11112018\t17:33:05\t29.27980\t-87.78970\t399.673\t-999\t412.38\t1019.6\t1020.29\t26.85\t26.1657\t36.0803\t376.25\t400.43\t-24.19\t2\t\n",
            "00KS20181110\t315.73300\t11112018\t17:35:31\t29.28350\t-87.78380\t399.641\t-999\t412.40\t1021.4\t1020.23\t26.88\t26.1962\t36.0723\t376.89\t400.40\t-23.51\t2\t\n",
            "00KS20181110\t315.73493\t11112018\t17:38:18\t29.28790\t-87.77780\t399.910\t-999\t412.42\t1019.7\t1020.29\t26.92\t26.2235\t36.0694\t376.26\t400.42\t-24.16\t2\t\n",
            "00KS20181110\t315.73663\t11112018\t17:40:45\t29.29170\t-87.77250\t400.482\t-999\t412.43\t1022.0\t1020.24\t26.84\t26.153\t36.0357\t377.89\t400.47\t-22.59\t2\t\n",
            "00KS20181110\t315.73833\t11112018\t17:43:12\t29.29550\t-87.76710\t395.322\t-999\t412.45\t1021.3\t1020.28\t26.64\t25.9307\t35.6089\t372.55\t400.68\t-28.13\t2\t\n",
            "00KS20181110\t315.74003\t11112018\t17:45:39\t29.29880\t-87.76250\t391.390\t-999\t412.47\t1020.6\t1020.26\t26.61\t25.896\t35.5458\t368.53\t400.71\t-32.18\t2\t\n",
            "00KS20181110\t315.74174\t11112018\t17:48:06\t29.30170\t-87.75860\t389.020\t-999\t412.48\t1020.3\t1020.14\t26.60\t25.8969\t35.518\t366.37\t400.68\t-34.31\t2\t\n",
            "00KS20181110\t315.74343\t11112018\t17:50:32\t29.30420\t-87.75510\t387.930\t-999\t412.50\t1019.9\t1020\t26.58\t25.907\t35.5119\t365.67\t400.63\t-34.96\t2\t\n",
            "00KS20181110\t315.74513\t11112018\t17:52:59\t29.30650\t-87.75200\t387.010\t-999\t412.52\t1021.1\t1020\t26.53\t25.9085\t35.5032\t366.08\t400.65\t-34.57\t2\t\n",
            "00KS20181110\t315.74683\t11112018\t17:55:26\t29.30880\t-87.74880\t385.801\t-999\t412.54\t1021.4\t1019.96\t26.48\t25.8995\t35.4591\t365.72\t400.65\t-34.94\t2\t\n",
            "00KS20181110\t315.74853\t11112018\t17:57:53\t29.31110\t-87.74570\t384.932\t-999\t412.55\t1019.8\t1019.91\t26.44\t25.8968\t35.436\t364.91\t400.65\t-35.75\t2\t\n",
            "00KS20181110\t315.75023\t11112018\t18:00:20\t29.31340\t-87.74250\t384.003\t-999\t412.57\t1020.5\t1019.88\t26.43\t25.8942\t35.413\t364.41\t400.66\t-36.25\t2\t\n",
            "00KS20181110\t315.75193\t11112018\t18:02:47\t29.31580\t-87.73920\t383.427\t-999\t412.59\t1020.3\t1019.83\t26.42\t25.894\t35.3944\t363.94\t400.66\t-36.71\t2\t\n",
            "00KS20181110\t315.75363\t11112018\t18:05:14\t29.31800\t-87.73630\t382.941\t-999\t412.61\t1021.2\t1019.83\t26.43\t25.8999\t35.3853\t363.74\t400.67\t-36.93\t2\t\n",
            "00KS20181110\t315.75532\t11112018\t18:07:40\t29.31970\t-87.73390\t382.677\t-999\t412.62\t1020.7\t1019.78\t26.43\t25.91\t35.3752\t363.46\t400.66\t-37.19\t2\t\n",
            "00KS20181110\t315.75703\t11112018\t18:10:07\t29.32140\t-87.73160\t382.584\t-999\t412.64\t1020.0\t1019.77\t26.43\t25.9213\t35.3702\t363.29\t400.66\t-37.37\t2\t\n",
            "00KS20181110\t315.75873\t11112018\t18:12:34\t29.32310\t-87.72930\t382.549\t-999\t412.66\t1020.5\t1019.69\t26.42\t25.9287\t35.3668\t363.72\t400.64\t-36.92\t2\t\n",
            "00KS20181110\t315.76043\t11112018\t18:15:01\t29.32470\t-87.72690\t382.366\t-999\t412.68\t1020.0\t1019.62\t26.42\t25.9358\t35.3684\t363.47\t400.62\t-37.16\t2\t\n"
    };

    private static final String[] OME_METADATA_XML_STRING = new String[] {
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n",
            "<x_tags>\n",
            "  <User>\n",
            "    <Name>SubmitterLast, SubmitterFirst</Name>\n",
            "    <Organization>SubmitterOrganization</Organization>\n",
            "    <Address>SubmitterAddress</Address>\n",
            "    <Phone>SubmitterPhone</Phone>\n",
            "    <Email>SubmitterEmail</Email>\n",
            "  </User>\n",
            "  <Investigator>\n",
            "    <Name>PILast, PIFirst</Name>\n",
            "    <Organization>PIOrganization</Organization>\n",
            "    <Address>PIAddress</Address>\n",
            "    <Phone>PIPhone</Phone>\n",
            "    <Email>PIEmail</Email>\n",
            "  </Investigator>\n",
            "  <Dataset_Info>\n",
            "    <Funding_Info>Funding Information</Funding_Info>\n",
            "    <Submission_Dates>\n",
            "      <Initial_Submission>20181220</Initial_Submission>\n",
            "      <Revised_Submission>20181220</Revised_Submission>\n",
            "    </Submission_Dates>\n",
            "  </Dataset_Info>\n",
            "  <Cruise_Info>\n",
            "    <Experiment>\n",
            "      <Experiment_Name>GU1806</Experiment_Name>\n",
            "      <Experiment_Type>Research Cruise</Experiment_Type>\n",
            "      <Platform_Type>Ship</Platform_Type>\n",
            "      <Co2_Instrument_type>Equilibrator-IR or CRDS or GC</Co2_Instrument_type>\n",
            "      <Cruise>\n",
            "        <Cruise_ID>00KS20181110</Cruise_ID>\n",
            "        <Cruise_Info>AOML_SOOP_CO2, Bryde's Whales</Cruise_Info>\n",
            "        <Geographical_Coverage>\n",
            "          <Bounds>\n",
            "            <Westernmost_Longitude>-88.6</Westernmost_Longitude>\n",
            "            <Easternmost_Longitude>-82.9</Easternmost_Longitude>\n",
            "            <Northernmost_Latitude>30.4</Northernmost_Latitude>\n",
            "            <Southernmost_Latitude>27.2</Southernmost_Latitude>\n",
            "          </Bounds>\n",
            "        </Geographical_Coverage>\n",
            "        <Temporal_Coverage>\n",
            "          <Start_Date>20181110</Start_Date>\n",
            "          <End_Date>20181204</End_Date>\n",
            "        </Temporal_Coverage>\n",
            "        <Ports_of_Call>Pascagoula, MS</Ports_of_Call>\n",
            "        <Ports_of_Call>Pensacola, FL </Ports_of_Call>\n",
            "      </Cruise>\n",
            "    </Experiment>\n",
            "    <Vessel>\n",
            "      <Vessel_Name>Test Ship</Vessel_Name>\n",
            "      <Vessel_ID>00KS</Vessel_ID>\n",
            "      <Vessel_Owner>NOAA</Vessel_Owner>\n",
            "    </Vessel>\n",
            "  </Cruise_Info>\n",
            "  <Variables_Info>\n",
            "    <Variable>\n",
            "      <Variable_Name>xCO2_EQU_ppm</Variable_Name>\n",
            "      <Description_of_Variable>Mole fraction of CO2 in the equilibrator headspace (dry) at equilibrator temperature (ppm)</Description_of_Variable>\n",
            "      <Unit_of_Variable>ppm</Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>xCO2_ATM_ppm</Variable_Name>\n",
            "      <Description_of_Variable>Mole fraction of CO2 measured in dry outside air (ppm)</Description_of_Variable>\n",
            "      <Unit_of_Variable>ppm</Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>xCO2_ATM_interpolated_ppm</Variable_Name>\n",
            "      <Description_of_Variable>Mole fraction of CO2 in outside air associated with each water analysis.  These values are interpolated between the bracketing averaged good xCO2_ATM analyses (ppm)</Description_of_Variable>\n",
            "      <Unit_of_Variable>ppm</Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>PRES_EQU_hPa</Variable_Name>\n",
            "      <Description_of_Variable>Barometric pressure in the equilibrator headspace (hPa)</Description_of_Variable>\n",
            "      <Unit_of_Variable>hPa</Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>PRES_ATM@SSP_hPa</Variable_Name>\n",
            "      <Description_of_Variable>Barometric pressure measured outside, corrected to sea level (hPa)</Description_of_Variable>\n",
            "      <Unit_of_Variable> hPa </Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>TEMP_EQU_C</Variable_Name>\n",
            "      <Description_of_Variable>Water temperature in equilibrator (&#176;C)</Description_of_Variable>\n",
            "      <Unit_of_Variable>Degree C</Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>SST_C</Variable_Name>\n",
            "      <Description_of_Variable>Sea surface temperature (&#176;C)</Description_of_Variable>\n",
            "      <Unit_of_Variable>Degree C</Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>SAL_permil</Variable_Name>\n",
            "      <Description_of_Variable>Sea surface salinity on Practical Salinity Scale (o/oo)</Description_of_Variable>\n",
            "      <Unit_of_Variable>ppt</Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>fCO2_SW@SST_uatm</Variable_Name>\n",
            "      <Description_of_Variable>Fugacity of CO2 in sea water at SST and 100% humidity (&#956;atm)</Description_of_Variable>\n",
            "      <Unit_of_Variable>&#956;atm</Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>fCO2_ATM_interpolated_uatm</Variable_Name>\n",
            "      <Description_of_Variable>Fugacity of CO2 in air corresponding to the interpolated xCO2 at SST and 100% humidity (&#956;atm)</Description_of_Variable>\n",
            "      <Unit_of_Variable>&#956;atm</Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>dfCO2_uatm</Variable_Name>\n",
            "      <Description_of_Variable>Sea water fCO2 minus interpolated air fCO2 (&#956;atm)</Description_of_Variable>\n",
            "      <Unit_of_Variable>&#956;atm</Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>WOCE_QC_FLAG</Variable_Name>\n",
            "      <Description_of_Variable>Quality control flag for fCO2 values (2=good, 3=questionable)</Description_of_Variable>\n",
            "      <Unit_of_Variable>None</Unit_of_Variable>\n",
            "    </Variable>\n",
            "    <Variable>\n",
            "      <Variable_Name>QC_SUBFLAG</Variable_Name>\n",
            "      <Description_of_Variable>Quality control subflag for fCO2 values, provides explanation when QC flag=3</Description_of_Variable>\n",
            "      <Unit_of_Variable>None</Unit_of_Variable>\n",
            "    </Variable>\n",
            "  </Variables_Info>\n",
            "  <Method_Description>\n",
            "    <Equilibrator_Design>\n",
            "      <Depth_of_Sea_Water_Intake>5 meters</Depth_of_Sea_Water_Intake>\n",
            "      <Location_of_Sea_Water_Intake>Bow</Location_of_Sea_Water_Intake>\n",
            "      <Equilibrator_Type>Spray head above dynamic pool, no thermal jacket</Equilibrator_Type>\n",
            "      <Equilibrator_Volume>0.95 L (0.4 L water, 0.55 L headspace)</Equilibrator_Volume>\n",
            "      <Water_Flow_Rate>1.5 - 2.0 L/min</Water_Flow_Rate>\n",
            "      <Headspace_Gas_Flow_Rate>70 - 150 ml/min</Headspace_Gas_Flow_Rate>\n",
            "      <Vented>Yes</Vented>\n",
            "      <Drying_Method_for_CO2_in_water>Gas stream passes through a thermoelectric condenser (~5 &#176;C) and then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).</Drying_Method_for_CO2_in_water>\n",
            "      <Additional_Information>Primary equilibrator is vented through a secondary equilibrator.</Additional_Information>\n",
            "    </Equilibrator_Design>\n",
            "    <CO2_in_Marine_Air>\n",
            "      <Measurement>Yes, 5 readings in a group every 3 hours</Measurement>\n",
            "      <Location_and_Height>Bow mast, ~18 meters above sea surface</Location_and_Height>\n",
            "      <Drying_Method>Gas stream passes through a thermoelectric condenser (~5 &#176;C) and then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).</Drying_Method>\n",
            "    </CO2_in_Marine_Air>\n",
            "    <CO2_Sensors>\n",
            "      <CO2_Sensor>\n",
            "        <Measurement_Method>IR</Measurement_Method>\n",
            "        <Manufacturer>LI-COR</Manufacturer>\n",
            "        <Model>7000</Model>\n",
            "        <Frequency>Every 140 seconds, except during calibration</Frequency>\n",
            "        <Resolution_Water>&#177; 0.01 &#956;atm in fCO2_SW</Resolution_Water>\n",
            "        <Uncertainty_Water>&#177; 2 &#956;atm in fCO2_SW</Uncertainty_Water>\n",
            "        <Resolution_Air>&#177; 0.01 &#956;atm in fCO2_ATM</Resolution_Air>\n",
            "        <Uncertainty_Air>&#177; 0.5 &#956;atm in fCO2_ATM</Uncertainty_Air>\n",
            "        <Manufacturer_of_Calibration_Gas>\n",
            "          Std 1: LL100000, 0.00 ppm, owned by AOML, used every ~4.5 hours. \n",
            "          Std 2: JA02140, 234.21 ppm, owned by AOML, used every ~4.5 hours.\n",
            "          Std 3: JA02689, 406.90 ppm, owned by AOML, used every ~4.5 hours.\n",
            "          Std 4: JB03276, 471.65 ppm, owned by AOML, used every ~4.5 hours.\n",
            "        </Manufacturer_of_Calibration_Gas>\n",
            "        <No_Of_Non_Zero_Gas_Stds>3</No_Of_Non_Zero_Gas_Stds>\n",
            "        <CO2_Sensor_Calibration>The analyzer is calibrated every 4 hours with field standards that in turn were calibrated with primary standards that are directly traceable to the WMO X2007 scale. The zero gas is ultra-high purity air.</CO2_Sensor_Calibration>\n",
            "        <Sensor_Calibration></Sensor_Calibration>\n",
            "        <Other_Comments>Instrument is located in an air-conditioned laboratory.  Ultra-High Purity air (0.0 ppm CO2) and the high standard gas are used to zero and span the LI-COR analyzer.</Other_Comments>\n",
            "        <Method_References>Pierrot, D., C. Neil, K. Sullivan, R. Castle, R. Wanninkhof, H. Lueger, T. Johannessen, A. Olsen, R. A. Feely, and C. E. Cosca (2009), Recommendations for autonomous underway pCO2 measuring systems and data reduction routines, Deep-Sea Res II, 56, 512-522.</Method_References>\n",
            "        <Details_Co2_Sensing>details of CO2 sensing (not required)</Details_Co2_Sensing>\n",
            "        <Measured_Co2_Params>xco2(dry)</Measured_Co2_Params>\n",
            "      </CO2_Sensor>\n",
            "    </CO2_Sensors>\n",
            "    <Sea_Surface_Temperature>\n",
            "      <Location>In engine room, about 2 m after the seachest, before the SW pumps.</Location>\n",
            "      <Manufacturer>Seabird, Inc.</Manufacturer>\n",
            "      <Model>SBE 38</Model>\n",
            "      <Accuracy_degC>0.001</Accuracy_degC>\n",
            "      <Precision_degC>0.0003</Precision_degC>\n",
            "      <Calibration>Factory calibration</Calibration>\n",
            "      <Other_Comments>Manufacturer's Resolution is taken as Precision; Maintained by ship.</Other_Comments>\n",
            "    </Sea_Surface_Temperature>\n",
            "    <Equilibrator_Temperature>\n",
            "      <Location>Inserted into equilibrator ~5 cm below water level</Location>\n",
            "      <Manufacturer>Hart</Manufacturer>\n",
            "      <Model>1521</Model>\n",
            "      <Accuracy_degC>0.025</Accuracy_degC>\n",
            "      <Precision_degC>0.001</Precision_degC>\n",
            "      <Calibration>Factory calibration</Calibration>\n",
            "      <Other_Comments>Resolution is taken as Precision.</Other_Comments>\n",
            "    </Equilibrator_Temperature>\n",
            "    <Equilibrator_Pressure>\n",
            "      <Location>Attached to equilibrator headspace.</Location>\n",
            "      <Manufacturer>Setra</Manufacturer>\n",
            "      <Model>270</Model>\n",
            "      <Accuracy_hPa>0.05</Accuracy_hPa>\n",
            "      <Precision_hPa>0.015</Precision_hPa>\n",
            "      <Calibration>Factory calibration</Calibration>\n",
            "      <Other_Comments>Manufacturer's Resolution is taken as Precision.</Other_Comments>\n",
            "    </Equilibrator_Pressure>\n",
            "    <Atmospheric_Pressure>\n",
            "      <Location>Next to the bridge, ~15 m above the sea surface water</Location>\n",
            "      <Manufacturer>RMYoung</Manufacturer>\n",
            "      <Model>61201</Model>\n",
            "      <Accuracy>&#177; 0.5 hPa</Accuracy>\n",
            "      <Precision> 0.01 hPa</Precision>\n",
            "      <Calibration>Factory calibration</Calibration>\n",
            "      <Normalized>yes</Normalized>\n",
            "      <Other_Comments>Manufacturer's Resolution is taken as Precision; Maintained by ship.</Other_Comments>\n",
            "    </Atmospheric_Pressure>\n",
            "    <Sea_Surface_Salinity>\n",
            "      <Location>In Chem lab, next to CO2 system</Location>\n",
            "      <Manufacturer>Seabird</Manufacturer>\n",
            "      <Model>SBE 45</Model>\n",
            "      <Accuracy>&#177; 0.005 o/oo</Accuracy>\n",
            "      <Precision>0.0002 o/oo</Precision>\n",
            "      <Calibration>Factory calibration</Calibration>\n",
            "      <Other_Comments>Manufacturer's Resolution is taken as Precision; Maintained by ship.</Other_Comments>\n",
            "    </Sea_Surface_Salinity>\n",
            "  </Method_Description>\n",
            "  <Data_set_References></Data_set_References>\n",
            "  <Additional_Information> The analytical system operated fine during this cruise.  The water flow sensor was not responding; however, water flow through the equilibrator was confirmed visually by ship's personnel and by the various temperature sensors. \n",
            "  Several times during the cruise, the ship's sensors were not recorded; so the values were interpolated from surrounding values.  The ship had an unscheduled 4-day stop in Pensacola starting mid-day of 21 Nov, 2018, because of rough weather.  \n",
            "  Original Data Location: http://www.aoml.noaa.gov/ocd/ocdweb/gunter/gunter_introduction.html\n",
            "  Full unprocessed data files from analytical instrument including flow information and ship's meteorological and TSG data at time of sampling can be obtained upon request. </Additional_Information>\n",
            "  <Citation></Citation>\n",
            "  <Preliminary_Quality_control>NA</Preliminary_Quality_control>\n",
            "  <form_type>underway</form_type>\n",
            "</x_tags>\n"
    };

}
