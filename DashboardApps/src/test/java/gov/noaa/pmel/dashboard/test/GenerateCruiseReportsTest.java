package gov.noaa.pmel.dashboard.test;

import gov.noaa.pmel.dashboard.programs.GenerateCruiseReports;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GenerateCruiseReportsTest {

    /**
     * Test of {@link GenerateCruiseReports#createEnhancedFilesBundle(String, File)}
     */
    @Test
    public void testCreateEnhancedFilesBundle() throws IOException {
        final List<String> expoList = Arrays.asList("PAT520151021", "26NA20160612", "11SS20150807");
        System.setProperty("CATALINA_BASE", System.getenv("HOME") + "/Tomcat");
        System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        GenerateCruiseReports reporter = new GenerateCruiseReports(configStore);
        File outputDir = new File("/var/tmp/junit");
        for (String expocode : expoList) {
            ArrayList<String> warnings = reporter.createEnhancedFilesBundle(expocode, outputDir);
            assertEquals(0, warnings.size());
            File zipFile = reporter.getEnhancedZipBundleFile(expocode, outputDir);
            assertTrue(zipFile.exists());
        }
    }

    /**
     * Test of {@link GenerateCruiseReports#generateReport(String, File)}
     */
    @Test
    public void testGenerateSingleCruiseReport() throws IOException {
        final String expocode = "PAT520150211";
        final File reportFile = new File("/var/tmp/junit/PAT520150211.tsv");
        System.setProperty("CATALINA_BASE", System.getenv("HOME") + "/Tomcat");
        System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        GenerateCruiseReports reporter = new GenerateCruiseReports(configStore);
        ArrayList<String> msgs = reporter.generateReport(expocode, reportFile);
        assertEquals(0, msgs.size());

        BufferedReader reader = new BufferedReader(new FileReader(reportFile));
        try {
            boolean preamble = true;
            int expectedNumColumns = GenerateCruiseReports.SINGLE_CRUISE_DATA_REPORT_HEADER.split("\t").length;
            boolean[] hasValue = new boolean[expectedNumColumns];
            String dataline = reader.readLine();
            int dataLineIdx = 0;
            while ( dataline != null ) {
                String[] pieces = dataline.split("\t");
                if ( preamble ) {
                    // Check if this is the header for the data
                    if ( pieces.length != 1 ) {
                        preamble = false;
                        assertEquals(GenerateCruiseReports.SINGLE_CRUISE_DATA_REPORT_HEADER, dataline);
                    }
                }
                else {
                    assertEquals(expectedNumColumns, pieces.length);
                    // First 12 must always have values
                    for (int k = 0; k < 12; k++) {
                        assertNotEquals("NaN", pieces[k]);
                    }
                    // Rest may or may not depending on if air sample or water sample
                    for (int k = 0; k < expectedNumColumns; k++) {
                        hasValue[k] = hasValue[k] || !"NaN".equals(pieces[k]);
                    }
                    if ( dataLineIdx < EXPECTED_START_SINGLE_CRUISE_REPORT_DATA_STRINGS.length ) {
                        for (int k = 0; k < expectedNumColumns; k++) {
                            if ( k == 23 ) {
                                // GVCO2 varies somewhat as more data is assimilated into the model
                                double expval = Double.valueOf(
                                        EXPECTED_START_SINGLE_CRUISE_REPORT_DATA_STRINGS[dataLineIdx][k]);
                                double actval = Double.valueOf(pieces[k]);
                                assertEquals(expval, actval, 1.0);
                            }
                            else {
                                assertEquals("line[" + dataLineIdx + "][" + k + "]",
                                        EXPECTED_START_SINGLE_CRUISE_REPORT_DATA_STRINGS[dataLineIdx][k], pieces[k]);
                            }
                        }

                    }
                    dataLineIdx++;
                }
                dataline = reader.readLine();
            }
            for (int k = 0; k < expectedNumColumns; k++) {
                // Only sample_depth and one of the pCO2_water_... is all-missing
                if ( (k == 13) || (k == 26) )
                    assertFalse("hasValue[" + k + "]", hasValue[k]);
                else
                    assertTrue("hasValue[" + k + "]", hasValue[k]);
            }
        } finally {
            reader.close();
        }
    }

    /**
     * Test of {@link GenerateCruiseReports#generateReport(TreeSet, String, File)}
     */
    @Test
    public void testGenerateMultiCruiseReport() throws IOException {
        final TreeSet<String> expocodeList = new TreeSet<String>(Collections.singleton("PAT520150211"));
        final File reportFile = new File("/var/tmp/junit/PAT520150211_multi.tsv");

        System.setProperty("CATALINA_BASE", System.getenv("HOME") + "/Tomcat");
        System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        GenerateCruiseReports reporter = new GenerateCruiseReports(configStore);
        ArrayList<String> msgs = reporter.generateReport(expocodeList, "C", reportFile);
        assertEquals(0, msgs.size());

        BufferedReader reader = new BufferedReader(new FileReader(reportFile));
        try {
            boolean metadataHeaderFound = false;
            boolean metadataFound = false;
            boolean dataInfoFound = false;
            boolean dataHeaderFound = false;
            int expectedNumMetadataColumns = GenerateCruiseReports.MULTI_CRUISE_METADATA_REPORT_HEADER
                    .split("\t").length;
            int expectedNumDataColumns = GenerateCruiseReports.MULTI_CRUISE_DATA_REPORT_HEADER.split("\t").length;
            String dataline = reader.readLine();
            int dataLineIdx = 0;
            while ( dataline != null ) {
                String[] pieces = dataline.split("\t");
                if ( !metadataHeaderFound ) {
                    // Check if this is the header for the metadata
                    if ( pieces.length != 1 ) {
                        assertEquals(GenerateCruiseReports.MULTI_CRUISE_METADATA_REPORT_HEADER, dataline);
                        metadataHeaderFound = true;
                    }
                }
                else if ( !metadataFound ) {
                    assertEquals(expectedNumMetadataColumns, pieces.length);
                    if ( EXPECTED_MULTI_CRUISE_REPORT_METADATA_STRINGS[0].equals(pieces[0]) ) {
                        for (int k = 1; k < expectedNumMetadataColumns; k++) {
                            assertEquals("metadata[" + k + "]",
                                    EXPECTED_MULTI_CRUISE_REPORT_METADATA_STRINGS[k], pieces[k]);
                        }
                        metadataFound = true;
                    }
                }
                else if ( !dataInfoFound ) {
                    if ( pieces.length == 1 ) {
                        dataInfoFound = true;
                    }
                    else {
                        assertEquals(expectedNumMetadataColumns, pieces.length);
                    }
                }
                else if ( !dataHeaderFound ) {
                    if ( pieces.length != 1 ) {
                        assertEquals(GenerateCruiseReports.MULTI_CRUISE_DATA_REPORT_HEADER, dataline);
                        dataHeaderFound = true;
                    }
                }
                else {
                    assertEquals(expectedNumDataColumns, pieces.length);
                    assertTrue(dataLineIdx < EXPECTED_MULTI_CRUISE_REPORT_DATA_STRINGS.length);
                    for (int k = 0; k < expectedNumDataColumns; k++) {
                        if ( k == 23 ) {
                            // GVCO2 varies somewhat as more data is assimilated into the model
                            double expval = Double.valueOf(
                                    EXPECTED_MULTI_CRUISE_REPORT_DATA_STRINGS[dataLineIdx][k]);
                            double actval = Double.valueOf(pieces[k]);
                            assertEquals(expval, actval, 1.0);
                        }
                        else {
                            assertEquals("line[" + dataLineIdx + "][" + k + "]",
                                    EXPECTED_MULTI_CRUISE_REPORT_DATA_STRINGS[dataLineIdx][k], pieces[k]);
                        }
                    }
                    dataLineIdx++;
                }
                dataline = reader.readLine();
            }
        } finally {
            reader.close();
        }
    }

    private static final String[][] EXPECTED_START_SINGLE_CRUISE_REPORT_DATA_STRINGS = {
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "04", "50", "00.",
                    "153.16482", "-27.37763", "NaN", "NaN", "NaN", "NaN", "NaN", "NaN",
                    "35.000", "1017.300", "-1.", "12.", "397.322", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "05", "00", "00.",
                    "153.17413", "-27.36205", "NaN", "NaN", "NaN", "NaN", "1018.100", "1019.510",
                    "35.000", "1017.300", "2.", "14.", "397.324", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "05", "10", "00.",
                    "153.19017", "-27.33832", "NaN", "NaN", "NaN", "NaN", "1017.900", "1019.450",
                    "35.000", "1017.300", "3.", "18.", "397.326", "NaN", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "05", "20", "00.",
                    "153.21304", "-27.30566", "NaN", "NaN", "NaN", "NaN", "1017.400", "1019.420",
                    "35.000", "1017.300", "7.", "23.", "397.330", "NaN", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "05", "30", "00.",
                    "153.25189", "-27.27685", "NaN", "NaN", "NaN", "NaN", "1016.900", "1019.250",
                    "35.000", "1017.300", "13.", "28.", "397.333", "NaN", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "05", "40", "00.",
                    "153.30245", "-27.25335", "NaN", "NaN", "NaN", "NaN", "1016.800", "1019.080",
                    "35.000", "1017.300", "18.", "33.", "397.335", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "05", "50", "00.",
                    "153.33877", "-27.21704", "NaN", "NaN", "NaN", "NaN", "1016.800", "1019.140",
                    "35.000", "1017.300", "17.", "38.", "397.339", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "06", "00", "00.",
                    "153.34861", "-27.16214", "NaN", "NaN", "NaN", "NaN", "1017.000", "1019.220",
                    "35.000", "1017.300", "20.", "42.", "397.345", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "06", "10", "00.",
                    "153.33386", "-27.11192", "NaN", "NaN", "NaN", "NaN", "1017.500", "1019.500",
                    "35.000", "1019.300", "27.", "43.", "397.350", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "06", "20", "00.",
                    "153.30085", "-27.06665", "NaN", "NaN", "NaN", "NaN", "1017.500", "1019.530",
                    "35.000", "1019.300", "27.", "42.", "397.354", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "06", "30", "00.",
                    "153.25907", "-27.03458", "NaN", "NaN", "NaN", "NaN", "1017.700", "1019.570",
                    "35.000", "1019.300", "22.", "39.", "397.358", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "06", "40", "00.",
                    "153.23446", "-26.98598", "NaN", "NaN", "NaN", "NaN", "1017.700", "1019.530",
                    "35.465", "1019.300", "29.", "38.", "397.363", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "06", "50", "00.",
                    "153.20755", "-26.93720", "NaN", "NaN", "NaN", "NaN", "1017.700", "1019.520",
                    "35.465", "1019.300", "36.", "37.", "397.368", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "07", "00", "00.",
                    "153.17618", "-26.89210", "NaN", "NaN", "NaN", "NaN", "1017.800", "1019.710",
                    "35.465", "1019.300", "22.", "34.", "397.373", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "07", "10", "00.",
                    "153.15222", "-26.84550", "NaN", "NaN", "NaN", "NaN", "1017.700", "1019.820",
                    "35.465", "1019.300", "12.", "32.", "397.377", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "07", "20", "00.",
                    "153.17278", "-26.80341", "NaN", "NaN", "NaN", "NaN", "1017.500", "1019.690",
                    "35.465", "1019.300", "19.", "34.", "397.382", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "07", "30", "00.",
                    "153.17917", "-26.75427", "NaN", "NaN", "NaN", "NaN", "1017.600", "1019.720",
                    "35.465", "1019.300", "17.", "34.", "397.387", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "07", "40", "00.",
                    "153.17857", "-26.71362", "NaN", "NaN", "NaN", "NaN", "1018.400", "1019.650",
                    "35.465", "1019.300", "16.", "34.", "397.390", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "07", "50", "00.",
                    "153.18823", "-26.70561", "NaN", "35.505", "26.660", "26.890", "1017.600", "1019.800",
                    "35.465", "1019.300", "20.", "35.", "397.391", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "08", "00", "00.",
                    "153.21307", "-26.72282", "NaN", "35.512", "26.520", "26.700", "1017.800", "1019.820",
                    "35.465", "1019.300", "26.", "38.", "397.390", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "08", "10", "00.",
                    "153.25009", "-26.74654", "NaN", "35.505", "26.420", "26.580", "1017.800", "1019.850",
                    "35.465", "1019.300", "33.", "41.", "397.388", "393.030", "390.460", "NaN",
                    "379.180", "380.350", "377.990", "378.587", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "08", "20", "00.",
                    "153.29116", "-26.77391", "NaN", "35.505", "26.470", "26.620", "1017.800", "1019.810",
                    "35.465", "1019.300", "40.", "46.", "397.385", "389.430", "387.070", "NaN",
                    "375.850", "376.830", "374.670", "375.233", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "08", "30", "00.",
                    "153.33335", "-26.80172", "NaN", "35.491", "26.520", "26.670", "1017.700", "1019.780",
                    "35.465", "1019.300", "46.", "50.", "397.382", "387.730", "385.400", "NaN",
                    "374.180", "375.140", "373.000", "373.546", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "08", "40", "00.",
                    "153.37566", "-26.82955", "NaN", "35.486", "26.570", "26.720", "1017.700", "1019.810",
                    "35.465", "1019.300", "52.", "54.", "397.379", "388.120", "385.840", "NaN",
                    "374.570", "375.490", "373.390", "373.895", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "08", "50", "00.",
                    "153.41747", "-26.85665", "NaN", "35.484", "26.580", "26.730", "1017.700", "1019.930",
                    "35.465", "1019.300", "61.", "57.", "397.376", "389.040", "386.730", "NaN",
                    "375.400", "376.340", "374.220", "374.819", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "00", "00.",
                    "153.46018", "-26.88330", "NaN", "35.486", "26.600", "26.740", "1017.800", "1019.960",
                    "35.465", "1019.300", "73.", "60.", "397.374", "389.450", "387.200", "NaN",
                    "375.900", "376.790", "374.720", "375.377", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "10", "00.",
                    "153.50360", "-26.91055", "NaN", "35.479", "26.680", "26.820", "1017.800", "1019.910",
                    "35.465", "1019.300", "93.", "63.", "397.371", "389.950", "387.720", "NaN",
                    "376.350", "377.220", "375.170", "375.779", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "20", "00.",
                    "153.54090", "-26.94384", "NaN", "35.486", "26.710", "26.850", "1017.800", "1019.880",
                    "35.465", "1019.300", "119.", "65.", "397.367", "391.640", "389.400", "NaN",
                    "377.950", "378.820", "376.760", "377.373", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "30", "00.",
                    "153.55906", "-26.98875", "NaN", "35.477", "26.670", "26.820", "1017.600", "1019.950",
                    "35.465", "1019.300", "129.", "64.", "397.363", "390.800", "388.520", "NaN",
                    "377.050", "377.960", "375.860", "376.454", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "40", "00.",
                    "153.57301", "-27.03491", "NaN", "35.465", "26.750", "26.890", "1017.500", "1019.950",
                    "35.000", "1019.300", "126.", "63.", "397.358", "390.460", "388.250", "NaN",
                    "376.670", "377.520", "375.490", "376.232", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "50", "00.",
                    "153.58258", "-27.08256", "NaN", "35.459", "26.760", "26.900", "1017.700", "1020.130",
                    "35.000", "1019.300", "107.", "61.", "397.353", "389.600", "387.380", "NaN",
                    "375.890", "376.750", "374.710", "375.464", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "00", "00.",
                    "153.59879", "-27.12834", "NaN", "35.467", "26.710", "26.850", "1017.700", "1020.190",
                    "35.000", "1019.300", "100.", "60.", "397.348", "390.360", "388.110", "NaN",
                    "376.660", "377.540", "375.480", "376.258", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "10", "00.",
                    "153.61567", "-27.17472", "NaN", "35.463", "26.770", "26.910", "1017.900", "1020.380",
                    "35.000", "1019.300", "95.", "58.", "397.343", "390.230", "388.050", "NaN",
                    "376.620", "377.440", "375.440", "376.159", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "20", "00.",
                    "153.63157", "-27.22162", "NaN", "35.443", "26.880", "27.010", "1018.200", "1020.700",
                    "35.000", "1019.300", "92.", "58.", "397.338", "390.100", "387.960", "NaN",
                    "376.580", "377.370", "375.400", "376.238", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "30", "00.",
                    "153.64433", "-27.26958", "NaN", "35.420", "26.920", "27.060", "1018.400", "1020.790",
                    "35.000", "1019.300", "89.", "57.", "397.333", "388.310", "386.160", "NaN",
                    "374.840", "375.640", "373.670", "374.348", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "40", "00.",
                    "153.64879", "-27.31958", "NaN", "35.421", "26.900", "27.040", "1018.100", "1020.840",
                    "35.000", "1019.300", "86.", "55.", "397.328", "387.300", "385.130", "NaN",
                    "373.760", "374.580", "372.590", "373.409", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "50", "00.",
                    "153.65182", "-27.36975", "NaN", "35.407", "26.920", "27.060", "1017.900", "1020.770",
                    "35.000", "1019.300", "83.", "53.", "397.323", "386.480", "384.340", "NaN",
                    "372.900", "373.690", "371.730", "372.577", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "00", "00.",
                    "153.65411", "-27.41965", "NaN", "35.416", "26.970", "27.100", "1018.100", "1020.870",
                    "35.000", "1019.300", "75.", "51.", "397.318", "387.570", "385.450", "NaN",
                    "374.010", "374.780", "372.840", "373.793", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "10", "00.",
                    "153.65625", "-27.47013", "NaN", "35.414", "27.020", "27.150", "1018.100", "1020.970",
                    "35.000", "1019.300", "78.", "50.", "397.313", "388.370", "386.270", "NaN",
                    "374.790", "375.550", "373.620", "374.564", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "20", "00.",
                    "153.65835", "-27.52083", "NaN", "35.401", "27.020", "27.150", "1018.400", "1020.850",
                    "35.000", "1019.300", "77.", "49.", "397.307", "387.200", "385.090", "NaN",
                    "373.730", "374.500", "372.560", "373.390", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "30", "00.",
                    "153.66146", "-27.57162", "NaN", "35.401", "26.900", "27.040", "1018.300", "1020.770",
                    "35.000", "1019.300", "74.", "49.", "397.302", "386.560", "384.400", "NaN",
                    "373.130", "373.940", "371.960", "372.669", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "40", "00.",
                    "153.66581", "-27.62219", "NaN", "35.426", "26.760", "26.900", "1018.400", "1020.930",
                    "35.000", "1019.300", "72.", "49.", "397.297", "385.470", "383.260", "NaN",
                    "372.180", "373.040", "371.010", "371.785", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "50", "00.",
                    "153.66903", "-27.67299", "NaN", "35.449", "26.770", "26.910", "1018.400", "1021.000",
                    "35.000", "1019.300", "73.", "50.", "397.292", "386.070", "383.900", "NaN",
                    "372.790", "373.620", "371.620", "372.382", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "00", "00.",
                    "153.67099", "-27.72324", "NaN", "35.446", "26.760", "26.900", "1018.400", "1020.990",
                    "35.000", "1019.300", "74.", "50.", "397.286", "386.220", "384.040", "NaN",
                    "372.920", "373.750", "371.750", "372.531", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "10", "00.",
                    "153.67297", "-27.77275", "NaN", "35.445", "26.800", "26.940", "1018.400", "1020.980",
                    "35.000", "1017.800", "74.", "50.", "397.281", "386.900", "384.760", "NaN",
                    "373.620", "374.410", "372.440", "373.152", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "20", "00.",
                    "153.67533", "-27.82190", "NaN", "35.442", "26.820", "26.960", "1018.400", "1020.960",
                    "35.000", "1017.800", "74.", "50.", "397.276", "386.970", "384.820", "NaN",
                    "373.630", "374.440", "372.460", "373.197", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "30", "00.",
                    "153.67807", "-27.87116", "NaN", "35.442", "26.840", "26.980", "1018.700", "1021.030",
                    "35.000", "1017.800", "73.", "50.", "397.271", "387.230", "385.090", "NaN",
                    "373.990", "374.790", "372.820", "373.459", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "40", "00.",
                    "153.68191", "-27.92068", "NaN", "35.453", "26.770", "26.910", "1018.800", "1021.020",
                    "35.000", "1017.800", "68.", "51.", "397.266", "387.770", "385.590", "NaN",
                    "374.580", "375.410", "373.400", "374.030", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "50", "00.",
                    "153.68649", "-27.96986", "NaN", "35.452", "26.670", "26.820", "1018.900", "1021.010",
                    "35.000", "1017.800", "63.", "51.", "397.261", "386.770", "384.560", "NaN",
                    "373.680", "374.530", "372.500", "372.972", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "13", "00", "00.",
                    "153.69181", "-28.01902", "NaN", "35.445", "26.690", "26.830", "1018.900", "1020.930",
                    "35.000", "1017.800", "59.", "52.", "397.256", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "13", "10", "00.",
                    "153.69706", "-28.06811", "NaN", "35.418", "26.550", "26.690", "1018.700", "1020.940",
                    "35.000", "1017.800", "56.", "52.", "397.251", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "13", "20", "00.",
                    "153.70019", "-28.11687", "NaN", "35.425", "26.570", "26.710", "1018.500", "1020.930",
                    "35.000", "1017.800", "55.", "52.", "397.245", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "13", "30", "00.",
                    "153.70313", "-28.16572", "NaN", "35.427", "26.560", "26.710", "1018.200", "1020.830",
                    "35.000", "1017.800", "56.", "53.", "397.240", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "13", "40", "00.",
                    "153.70629", "-28.21419", "NaN", "35.426", "26.510", "26.650", "1018.100", "1020.820",
                    "35.000", "1017.800", "55.", "53.", "397.235", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "13", "50", "00.",
                    "153.70955", "-28.26205", "NaN", "35.422", "26.410", "26.560", "1018.200", "1020.860",
                    "35.000", "1017.800", "58.", "53.", "397.230", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "14", "00", "00.",
                    "153.71263", "-28.30977", "NaN", "35.435", "26.450", "26.590", "1018.400", "1020.860",
                    "35.000", "1017.800", "61.", "54.", "397.225", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "14", "10", "00.",
                    "153.71535", "-28.35720", "NaN", "35.459", "26.430", "26.570", "1018.500", "1020.890",
                    "35.000", "1017.800", "63.", "54.", "397.221", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "14", "20", "00.",
                    "153.71794", "-28.40479", "NaN", "35.460", "26.450", "26.590", "1018.600", "1020.830",
                    "35.000", "1017.800", "64.", "54.", "397.216", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "14", "30", "00.",
                    "153.72015", "-28.45240", "NaN", "35.464", "26.510", "26.650", "1018.500", "1020.710",
                    "35.000", "1017.800", "62.", "54.", "397.211", "NaN", "NaN", "NaN",
                    "NaN", "NaN", "NaN", "NaN", "0", "9" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "14", "40", "00.",
                    "153.72243", "-28.49971", "NaN", "35.462", "26.590", "26.730", "1018.700", "1020.730",
                    "35.000", "1017.800", "56.", "54.", "397.206", "385.850", "383.710", "NaN",
                    "372.850", "373.650", "371.680", "372.204", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "14", "50", "00.",
                    "153.72527", "-28.54788", "NaN", "35.452", "26.630", "26.770", "1018.600", "1020.750",
                    "35.000", "1017.800", "55.", "55.", "397.201", "386.610", "384.470", "NaN",
                    "373.530", "374.320", "372.360", "372.915", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "00", "00.",
                    "153.72816", "-28.59642", "NaN", "35.470", "26.590", "26.730", "1018.500", "1020.650",
                    "35.000", "1017.800", "57.", "55.", "397.196", "387.160", "384.980", "NaN",
                    "374.000", "374.820", "372.820", "373.438", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "10", "00.",
                    "153.72896", "-28.64545", "NaN", "35.470", "26.560", "26.700", "1018.500", "1020.600",
                    "35.000", "1017.800", "57.", "55.", "397.191", "387.890", "385.710", "NaN",
                    "374.760", "375.590", "373.580", "374.145", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "20", "00.",
                    "153.71960", "-28.69555", "NaN", "35.462", "26.510", "26.650", "1018.500", "1020.520",
                    "35.000", "1017.800", "54.", "54.", "397.185", "387.390", "385.200", "NaN",
                    "374.270", "375.110", "373.090", "373.670", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "30", "00.",
                    "153.70853", "-28.74583", "NaN", "35.420", "26.490", "26.630", "1018.300", "1020.470",
                    "35.000", "1017.800", "58.", "53.", "397.180", "386.310", "384.130", "NaN",
                    "373.180", "374.020", "372.010", "372.625", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "40", "00.",
                    "153.69745", "-28.79596", "NaN", "35.421", "26.430", "26.570", "1018.300", "1020.440",
                    "35.000", "1019.600", "53.", "52.", "397.175", "387.000", "384.780", "NaN",
                    "373.870", "374.740", "372.700", "373.324", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "50", "00.",
                    "153.68661", "-28.84639", "NaN", "35.470", "26.420", "26.560", "1018.400", "1020.410",
                    "35.000", "1019.600", "47.", "51.", "397.170", "388.900", "386.690", "NaN",
                    "375.760", "376.610", "374.580", "375.153", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "00", "00.",
                    "153.67578", "-28.89699", "NaN", "35.464", "26.410", "26.550", "1018.400", "1020.350",
                    "35.000", "1019.600", "48.", "50.", "397.164", "387.920", "385.730", "NaN",
                    "374.830", "375.650", "373.640", "374.192", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "10", "00.",
                    "153.66482", "-28.94776", "NaN", "35.476", "26.440", "26.580", "1018.500", "1020.410",
                    "35.000", "1019.600", "50.", "49.", "397.159", "387.910", "385.740", "NaN",
                    "374.860", "375.670", "373.680", "374.183", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "20", "00.",
                    "153.65394", "-28.99899", "NaN", "35.483", "26.460", "26.600", "1018.700", "1020.490",
                    "35.000", "1019.600", "51.", "47.", "397.154", "387.900", "385.750", "NaN",
                    "374.940", "375.740", "373.760", "374.189", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "30", "00.",
                    "153.64328", "-29.05032", "NaN", "35.478", "26.480", "26.610", "1019.000", "1020.620",
                    "35.000", "1019.600", "50.", "46.", "397.149", "387.760", "385.640", "NaN",
                    "374.930", "375.700", "373.740", "374.254", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "40", "00.",
                    "153.63309", "-29.10210", "NaN", "35.431", "26.460", "26.600", "1019.100", "1020.680",
                    "35.000", "1019.600", "53.", "45.", "397.143", "387.150", "385.000", "NaN",
                    "374.370", "375.170", "373.190", "373.537", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "50", "00.",
                    "153.62303", "-29.15368", "NaN", "35.469", "26.490", "26.630", "1019.200", "1020.680",
                    "35.000", "1019.600", "56.", "44.", "397.138", "387.300", "385.160", "NaN",
                    "374.520", "375.310", "373.340", "373.659", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "00", "00.",
                    "153.61297", "-29.20563", "NaN", "35.480", "26.520", "26.660", "1018.700", "1020.570",
                    "35.000", "1019.600", "56.", "45.", "397.132", "387.460", "385.320", "NaN",
                    "374.480", "375.280", "373.310", "373.749", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "10", "00.",
                    "153.60250", "-29.25762", "NaN", "35.466", "26.480", "26.620", "1018.100", "1020.440",
                    "35.000", "1019.600", "55.", "46.", "397.127", "386.370", "384.190", "NaN",
                    "373.190", "374.010", "372.010", "372.679", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "20", "00.",
                    "153.59082", "-29.30955", "NaN", "35.438", "26.420", "26.560", "1018.400", "1020.540",
                    "35.000", "1019.600", "55.", "48.", "397.122", "386.090", "383.900", "NaN",
                    "373.050", "373.900", "371.880", "372.491", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "30", "00.",
                    "153.57888", "-29.36065", "NaN", "35.476", "26.490", "26.630", "1018.500", "1020.630",
                    "35.000", "1019.600", "63.", "49.", "397.116", "387.490", "385.340", "NaN",
                    "374.450", "375.250", "373.280", "373.824", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "40", "00.",
                    "153.56723", "-29.41132", "NaN", "35.473", "26.520", "26.660", "1018.500", "1020.650",
                    "35.000", "1019.600", "70.", "50.", "397.111", "388.040", "385.890", "NaN",
                    "374.930", "375.730", "373.750", "374.339", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "50", "00.",
                    "153.55619", "-29.46222", "NaN", "35.491", "26.430", "26.570", "1018.500", "1020.680",
                    "35.000", "1019.600", "69.", "52.", "397.106", "387.260", "385.060", "NaN",
                    "374.230", "375.080", "373.050", "373.665", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "00", "00.",
                    "153.54545", "-29.51308", "NaN", "35.475", "26.310", "26.450", "1018.600", "1020.780",
                    "35.000", "1019.600", "68.", "53.", "397.101", "385.700", "383.460", "NaN",
                    "372.790", "373.680", "371.620", "372.287", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "10", "00.",
                    "153.53549", "-29.56413", "NaN", "35.478", "26.380", "26.520", "1018.600", "1020.790",
                    "35.000", "1021.200", "64.", "55.", "397.095", "387.210", "385.040", "NaN",
                    "374.280", "375.100", "373.100", "373.696", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "20", "00.",
                    "153.52550", "-29.61519", "NaN", "35.472", "26.420", "26.560", "1018.700", "1020.830",
                    "35.000", "1021.200", "62.", "57.", "397.090", "388.570", "386.410", "NaN",
                    "375.600", "376.410", "374.420", "374.994", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "30", "00.",
                    "153.51529", "-29.66628", "NaN", "35.473", "26.440", "26.580", "1018.800", "1020.940",
                    "35.000", "1021.200", "61.", "58.", "397.085", "389.000", "386.820", "NaN",
                    "376.030", "376.850", "374.850", "375.435", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "40", "00.",
                    "153.50521", "-29.71773", "NaN", "35.461", "26.440", "26.580", "1018.900", "1021.100",
                    "35.000", "1021.200", "61.", "60.", "397.079", "388.650", "386.470", "NaN",
                    "375.710", "376.530", "374.530", "375.158", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "50", "00.",
                    "153.49516", "-29.76937", "NaN", "35.457", "26.470", "26.610", "1019.000", "1021.230",
                    "35.000", "1021.200", "61.", "61.", "397.074", "389.260", "387.110", "NaN",
                    "376.380", "377.180", "375.200", "375.774", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "00", "00.",
                    "153.48537", "-29.82107", "NaN", "35.486", "26.530", "26.670", "1019.100", "1021.410",
                    "35.000", "1021.200", "54.", "62.", "397.069", "391.080", "388.930", "NaN",
                    "378.130", "378.920", "376.940", "377.554", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "10", "00.",
                    "153.47391", "-29.87247", "NaN", "35.505", "26.570", "26.700", "1019.000", "1021.440",
                    "35.000", "1021.200", "40.", "62.", "397.063", "392.120", "389.960", "NaN",
                    "379.070", "379.860", "377.880", "378.707", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "20", "00.",
                    "153.46122", "-29.92400", "NaN", "35.515", "26.550", "26.680", "1018.100", "1021.610",
                    "35.000", "1021.200", "30.", "61.", "397.058", "391.340", "389.160", "NaN",
                    "377.960", "378.770", "376.770", "378.034", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "30", "00.",
                    "153.44752", "-29.97532", "NaN", "35.501", "26.500", "26.640", "1017.700", "1021.790",
                    "35.000", "1021.200", "30.", "59.", "397.053", "391.010", "388.810", "NaN",
                    "377.480", "378.310", "376.290", "377.654", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "40", "00.",
                    "153.43304", "-30.02671", "NaN", "35.517", "26.510", "26.650", "1019.200", "1022.030",
                    "35.512", "1021.200", "37.", "58.", "397.047", "391.230", "389.060", "NaN",
                    "378.320", "379.130", "377.130", "377.951", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "50", "00.",
                    "153.41911", "-30.07822", "NaN", "35.498", "26.500", "26.640", "1019.700", "1022.210",
                    "35.512", "1021.200", "46.", "56.", "397.041", "391.680", "389.490", "NaN",
                    "378.910", "379.730", "377.720", "378.462", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "20", "00", "00.",
                    "153.40553", "-30.12974", "NaN", "35.498", "26.500", "26.640", "1019.700", "1022.260",
                    "35.512", "1021.200", "57.", "55.", "397.035", "391.750", "389.560", "NaN",
                    "379.000", "379.820", "377.800", "378.548", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "20", "10", "00.",
                    "153.39240", "-30.18146", "NaN", "35.487", "26.460", "26.600", "1020.000", "1022.310",
                    "35.512", "1021.200", "64.", "54.", "397.029", "390.390", "388.190", "NaN",
                    "377.800", "378.620", "376.600", "377.283", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "20", "20", "00.",
                    "153.37887", "-30.23326", "NaN", "35.479", "26.450", "26.590", "1020.000", "1022.330",
                    "35.512", "1021.200", "69.", "52.", "397.022", "389.290", "387.090", "NaN",
                    "376.760", "377.600", "375.570", "376.235", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "20", "30", "00.",
                    "153.36444", "-30.28536", "NaN", "35.504", "26.510", "26.650", "1020.300", "1022.510",
                    "35.512", "1021.200", "75.", "51.", "397.016", "391.270", "389.100", "NaN",
                    "378.750", "379.560", "377.560", "378.173", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "20", "40", "00.",
                    "153.34994", "-30.33747", "NaN", "35.506", "26.510", "26.650", "1020.500", "1022.710",
                    "35.512", "1021.200", "90.", "50.", "397.010", "391.830", "389.630", "NaN",
                    "379.350", "380.180", "378.150", "378.790", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "20", "50", "00.",
                    "153.33482", "-30.38936", "NaN", "35.478", "26.510", "26.650", "1020.700", "1022.900",
                    "35.512", "1021.200", "104.", "48.", "397.004", "391.180", "388.980", "NaN",
                    "378.810", "379.640", "377.610", "378.234", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "21", "00", "00.",
                    "153.31953", "-30.44297", "NaN", "35.421", "26.500", "26.640", "1020.800", "1023.110",
                    "35.512", "1021.200", "116.", "47.", "396.998", "389.140", "386.940", "NaN",
                    "376.870", "377.710", "375.680", "376.348", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "21", "10", "00.",
                    "153.30348", "-30.49967", "NaN", "35.485", "26.560", "26.700", "1020.700", "1023.060",
                    "35.512", "1021.200", "123.", "45.", "396.991", "391.530", "389.420", "NaN",
                    "379.190", "379.940", "378.000", "378.596", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "21", "20", "00.",
                    "153.28779", "-30.55717", "NaN", "35.520", "26.560", "26.700", "1020.700", "1023.140",
                    "35.512", "1021.200", "131.", "46.", "396.984", "391.660", "389.540", "NaN",
                    "379.310", "380.070", "378.120", "378.752", "1", "2" }
    };

    private static final String[] EXPECTED_MULTI_CRUISE_REPORT_METADATA_STRINGS = {
            "PAT520150211", "4.0N", "N/A", "Trans Future 5", "Nakaoka, S.-I.; Nojiri, Y.",
            "10.3334/CDIAC/OTG.VOS_TF5_2015", "https://accession.nodc.noaa.gov/0157329",
            "10.1594/PANGAEA.866284", "https://doi.pangaea.de/10.1594/PANGAEA.866284",
            "144.56E", "178.66E", "43.47S", "26.71S", "2015-02-11", "2015-02-24",
            "B", "NIES_Cruise_MetadataTF5_71S-85N.xlsx; PI_OME.pdf; PI_OME.xml" };

    private static final String[][] EXPECTED_MULTI_CRUISE_REPORT_DATA_STRINGS = {
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "08", "10", "00.",
                    "153.25009", "-26.74654", "NaN", "35.505", "26.420", "26.580", "1017.800", "1019.850",
                    "35.465", "1019.300", "33.", "41.", "397.388", "378.587", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "08", "20", "00.",
                    "153.29116", "-26.77391", "NaN", "35.505", "26.470", "26.620", "1017.800", "1019.810",
                    "35.465", "1019.300", "40.", "46.", "397.385", "375.233", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "08", "30", "00.",
                    "153.33335", "-26.80172", "NaN", "35.491", "26.520", "26.670", "1017.700", "1019.780",
                    "35.465", "1019.300", "46.", "50.", "397.382", "373.546", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "08", "40", "00.",
                    "153.37566", "-26.82955", "NaN", "35.486", "26.570", "26.720", "1017.700", "1019.810",
                    "35.465", "1019.300", "52.", "54.", "397.379", "373.895", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "08", "50", "00.",
                    "153.41747", "-26.85665", "NaN", "35.484", "26.580", "26.730", "1017.700", "1019.930",
                    "35.465", "1019.300", "61.", "57.", "397.376", "374.819", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "00", "00.",
                    "153.46018", "-26.88330", "NaN", "35.486", "26.600", "26.740", "1017.800", "1019.960",
                    "35.465", "1019.300", "73.", "60.", "397.374", "375.377", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "10", "00.",
                    "153.50360", "-26.91055", "NaN", "35.479", "26.680", "26.820", "1017.800", "1019.910",
                    "35.465", "1019.300", "93.", "63.", "397.371", "375.779", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "20", "00.",
                    "153.54090", "-26.94384", "NaN", "35.486", "26.710", "26.850", "1017.800", "1019.880",
                    "35.465", "1019.300", "119.", "65.", "397.367", "377.373", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "30", "00.",
                    "153.55906", "-26.98875", "NaN", "35.477", "26.670", "26.820", "1017.600", "1019.950",
                    "35.465", "1019.300", "129.", "64.", "397.363", "376.454", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "40", "00.",
                    "153.57301", "-27.03491", "NaN", "35.465", "26.750", "26.890", "1017.500", "1019.950",
                    "35.000", "1019.300", "126.", "63.", "397.358", "376.232", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "09", "50", "00.",
                    "153.58258", "-27.08256", "NaN", "35.459", "26.760", "26.900", "1017.700", "1020.130",
                    "35.000", "1019.300", "107.", "61.", "397.353", "375.464", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "00", "00.",
                    "153.59879", "-27.12834", "NaN", "35.467", "26.710", "26.850", "1017.700", "1020.190",
                    "35.000", "1019.300", "100.", "60.", "397.348", "376.258", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "10", "00.",
                    "153.61567", "-27.17472", "NaN", "35.463", "26.770", "26.910", "1017.900", "1020.380",
                    "35.000", "1019.300", "95.", "58.", "397.343", "376.159", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "20", "00.",
                    "153.63157", "-27.22162", "NaN", "35.443", "26.880", "27.010", "1018.200", "1020.700",
                    "35.000", "1019.300", "92.", "58.", "397.338", "376.238", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "30", "00.",
                    "153.64433", "-27.26958", "NaN", "35.420", "26.920", "27.060", "1018.400", "1020.790",
                    "35.000", "1019.300", "89.", "57.", "397.333", "374.348", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "40", "00.",
                    "153.64879", "-27.31958", "NaN", "35.421", "26.900", "27.040", "1018.100", "1020.840",
                    "35.000", "1019.300", "86.", "55.", "397.328", "373.409", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "10", "50", "00.",
                    "153.65182", "-27.36975", "NaN", "35.407", "26.920", "27.060", "1017.900", "1020.770",
                    "35.000", "1019.300", "83.", "53.", "397.323", "372.577", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "00", "00.",
                    "153.65411", "-27.41965", "NaN", "35.416", "26.970", "27.100", "1018.100", "1020.870",
                    "35.000", "1019.300", "75.", "51.", "397.318", "373.793", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "10", "00.",
                    "153.65625", "-27.47013", "NaN", "35.414", "27.020", "27.150", "1018.100", "1020.970",
                    "35.000", "1019.300", "78.", "50.", "397.313", "374.564", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "20", "00.",
                    "153.65835", "-27.52083", "NaN", "35.401", "27.020", "27.150", "1018.400", "1020.850",
                    "35.000", "1019.300", "77.", "49.", "397.307", "373.390", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "30", "00.",
                    "153.66146", "-27.57162", "NaN", "35.401", "26.900", "27.040", "1018.300", "1020.770",
                    "35.000", "1019.300", "74.", "49.", "397.302", "372.669", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "40", "00.",
                    "153.66581", "-27.62219", "NaN", "35.426", "26.760", "26.900", "1018.400", "1020.930",
                    "35.000", "1019.300", "72.", "49.", "397.297", "371.785", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "11", "50", "00.",
                    "153.66903", "-27.67299", "NaN", "35.449", "26.770", "26.910", "1018.400", "1021.000",
                    "35.000", "1019.300", "73.", "50.", "397.292", "372.382", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "00", "00.",
                    "153.67099", "-27.72324", "NaN", "35.446", "26.760", "26.900", "1018.400", "1020.990",
                    "35.000", "1019.300", "74.", "50.", "397.286", "372.531", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "10", "00.",
                    "153.67297", "-27.77275", "NaN", "35.445", "26.800", "26.940", "1018.400", "1020.980",
                    "35.000", "1017.800", "74.", "50.", "397.281", "373.152", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "20", "00.",
                    "153.67533", "-27.82190", "NaN", "35.442", "26.820", "26.960", "1018.400", "1020.960",
                    "35.000", "1017.800", "74.", "50.", "397.276", "373.197", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "30", "00.",
                    "153.67807", "-27.87116", "NaN", "35.442", "26.840", "26.980", "1018.700", "1021.030",
                    "35.000", "1017.800", "73.", "50.", "397.271", "373.459", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "40", "00.",
                    "153.68191", "-27.92068", "NaN", "35.453", "26.770", "26.910", "1018.800", "1021.020",
                    "35.000", "1017.800", "68.", "51.", "397.266", "374.030", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "12", "50", "00.",
                    "153.68649", "-27.96986", "NaN", "35.452", "26.670", "26.820", "1018.900", "1021.010",
                    "35.000", "1017.800", "63.", "51.", "397.261", "372.972", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "14", "40", "00.",
                    "153.72243", "-28.49971", "NaN", "35.462", "26.590", "26.730", "1018.700", "1020.730",
                    "35.000", "1017.800", "56.", "54.", "397.206", "372.204", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "14", "50", "00.",
                    "153.72527", "-28.54788", "NaN", "35.452", "26.630", "26.770", "1018.600", "1020.750",
                    "35.000", "1017.800", "55.", "55.", "397.201", "372.915", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "00", "00.",
                    "153.72816", "-28.59642", "NaN", "35.470", "26.590", "26.730", "1018.500", "1020.650",
                    "35.000", "1017.800", "57.", "55.", "397.196", "373.438", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "10", "00.",
                    "153.72896", "-28.64545", "NaN", "35.470", "26.560", "26.700", "1018.500", "1020.600",
                    "35.000", "1017.800", "57.", "55.", "397.191", "374.145", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "20", "00.",
                    "153.71960", "-28.69555", "NaN", "35.462", "26.510", "26.650", "1018.500", "1020.520",
                    "35.000", "1017.800", "54.", "54.", "397.185", "373.670", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "30", "00.",
                    "153.70853", "-28.74583", "NaN", "35.420", "26.490", "26.630", "1018.300", "1020.470",
                    "35.000", "1017.800", "58.", "53.", "397.180", "372.625", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "40", "00.",
                    "153.69745", "-28.79596", "NaN", "35.421", "26.430", "26.570", "1018.300", "1020.440",
                    "35.000", "1019.600", "53.", "52.", "397.175", "373.324", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "15", "50", "00.",
                    "153.68661", "-28.84639", "NaN", "35.470", "26.420", "26.560", "1018.400", "1020.410",
                    "35.000", "1019.600", "47.", "51.", "397.170", "375.153", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "00", "00.",
                    "153.67578", "-28.89699", "NaN", "35.464", "26.410", "26.550", "1018.400", "1020.350",
                    "35.000", "1019.600", "48.", "50.", "397.164", "374.192", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "10", "00.",
                    "153.66482", "-28.94776", "NaN", "35.476", "26.440", "26.580", "1018.500", "1020.410",
                    "35.000", "1019.600", "50.", "49.", "397.159", "374.183", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "20", "00.",
                    "153.65394", "-28.99899", "NaN", "35.483", "26.460", "26.600", "1018.700", "1020.490",
                    "35.000", "1019.600", "51.", "47.", "397.154", "374.189", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "30", "00.",
                    "153.64328", "-29.05032", "NaN", "35.478", "26.480", "26.610", "1019.000", "1020.620",
                    "35.000", "1019.600", "50.", "46.", "397.149", "374.254", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "40", "00.",
                    "153.63309", "-29.10210", "NaN", "35.431", "26.460", "26.600", "1019.100", "1020.680",
                    "35.000", "1019.600", "53.", "45.", "397.143", "373.537", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "16", "50", "00.",
                    "153.62303", "-29.15368", "NaN", "35.469", "26.490", "26.630", "1019.200", "1020.680",
                    "35.000", "1019.600", "56.", "44.", "397.138", "373.659", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "00", "00.",
                    "153.61297", "-29.20563", "NaN", "35.480", "26.520", "26.660", "1018.700", "1020.570",
                    "35.000", "1019.600", "56.", "45.", "397.132", "373.749", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "10", "00.",
                    "153.60250", "-29.25762", "NaN", "35.466", "26.480", "26.620", "1018.100", "1020.440",
                    "35.000", "1019.600", "55.", "46.", "397.127", "372.679", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "20", "00.",
                    "153.59082", "-29.30955", "NaN", "35.438", "26.420", "26.560", "1018.400", "1020.540",
                    "35.000", "1019.600", "55.", "48.", "397.122", "372.491", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "30", "00.",
                    "153.57888", "-29.36065", "NaN", "35.476", "26.490", "26.630", "1018.500", "1020.630",
                    "35.000", "1019.600", "63.", "49.", "397.116", "373.824", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "40", "00.",
                    "153.56723", "-29.41132", "NaN", "35.473", "26.520", "26.660", "1018.500", "1020.650",
                    "35.000", "1019.600", "70.", "50.", "397.111", "374.339", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "17", "50", "00.",
                    "153.55619", "-29.46222", "NaN", "35.491", "26.430", "26.570", "1018.500", "1020.680",
                    "35.000", "1019.600", "69.", "52.", "397.106", "373.665", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "00", "00.",
                    "153.54545", "-29.51308", "NaN", "35.475", "26.310", "26.450", "1018.600", "1020.780",
                    "35.000", "1019.600", "68.", "53.", "397.101", "372.287", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "10", "00.",
                    "153.53549", "-29.56413", "NaN", "35.478", "26.380", "26.520", "1018.600", "1020.790",
                    "35.000", "1021.200", "64.", "55.", "397.095", "373.696", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "20", "00.",
                    "153.52550", "-29.61519", "NaN", "35.472", "26.420", "26.560", "1018.700", "1020.830",
                    "35.000", "1021.200", "62.", "57.", "397.090", "374.994", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "30", "00.",
                    "153.51529", "-29.66628", "NaN", "35.473", "26.440", "26.580", "1018.800", "1020.940",
                    "35.000", "1021.200", "61.", "58.", "397.085", "375.435", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "40", "00.",
                    "153.50521", "-29.71773", "NaN", "35.461", "26.440", "26.580", "1018.900", "1021.100",
                    "35.000", "1021.200", "61.", "60.", "397.079", "375.158", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "18", "50", "00.",
                    "153.49516", "-29.76937", "NaN", "35.457", "26.470", "26.610", "1019.000", "1021.230",
                    "35.000", "1021.200", "61.", "61.", "397.074", "375.774", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "00", "00.",
                    "153.48537", "-29.82107", "NaN", "35.486", "26.530", "26.670", "1019.100", "1021.410",
                    "35.000", "1021.200", "54.", "62.", "397.069", "377.554", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "10", "00.",
                    "153.47391", "-29.87247", "NaN", "35.505", "26.570", "26.700", "1019.000", "1021.440",
                    "35.000", "1021.200", "40.", "62.", "397.063", "378.707", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "20", "00.",
                    "153.46122", "-29.92400", "NaN", "35.515", "26.550", "26.680", "1018.100", "1021.610",
                    "35.000", "1021.200", "30.", "61.", "397.058", "378.034", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "30", "00.",
                    "153.44752", "-29.97532", "NaN", "35.501", "26.500", "26.640", "1017.700", "1021.790",
                    "35.000", "1021.200", "30.", "59.", "397.053", "377.654", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "40", "00.",
                    "153.43304", "-30.02671", "NaN", "35.517", "26.510", "26.650", "1019.200", "1022.030",
                    "35.512", "1021.200", "37.", "58.", "397.047", "377.951", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "19", "50", "00.",
                    "153.41911", "-30.07822", "NaN", "35.498", "26.500", "26.640", "1019.700", "1022.210",
                    "35.512", "1021.200", "46.", "56.", "397.041", "378.462", "1", "2" },
            { "PAT520150211", "4.0N", "10.3334/CDIAC/OTG.VOS_TF5_2015", "10.1594/PANGAEA.866284",
                    "B", "2015", "02", "11", "20", "00", "00.",
                    "153.40553", "-30.12974", "NaN", "35.498", "26.500", "26.640", "1019.700", "1022.260",
                    "35.512", "1021.200", "57.", "55.", "397.035", "378.548", "1", "2" }
    };

}