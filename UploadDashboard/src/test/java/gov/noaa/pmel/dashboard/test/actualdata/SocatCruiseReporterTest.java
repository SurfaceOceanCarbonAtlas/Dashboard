package gov.noaa.pmel.dashboard.test.actualdata;

import gov.noaa.pmel.dashboard.actions.SocatCruiseReporter;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test methods of {@link SocatCruiseReporter}
 */
public class SocatCruiseReporterTest {

    /**
     * Test of {@link SocatCruiseReporter#generateReport(String, File)}
     */
    @Test
    public void testGenerateReport() throws IOException {
        final String expocode = "PAT520150211";
        final File reportFile = new File("/var/tmp/junit/PAT520150211.tsv");

        System.setProperty("CATALINA_BASE", System.getenv("HOME"));
        System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        SocatCruiseReporter reporter = new SocatCruiseReporter(configStore);
        ArrayList<String> msgs = reporter.generateReport(expocode, reportFile);
        assertEquals(0, msgs.size());

        BufferedReader reader = new BufferedReader(new FileReader(reportFile));
        try {
            ArrayList<String> datalines = new ArrayList<String>();
            boolean preamble = true;
            int expectedNumColumns = SocatCruiseReporter.SINGLE_CRUISE_DATA_REPORT_HEADER.split("\t").length;
            boolean hasValue[] = new boolean[expectedNumColumns];
            String dataline = reader.readLine();
            while ( dataline != null ) {
                String[] pieces = dataline.split("\t");
                if ( preamble ) {
                    // This is the header for the data
                    if ( pieces.length != 1 ) {
                        preamble = false;
                        assertEquals(SocatCruiseReporter.SINGLE_CRUISE_DATA_REPORT_HEADER, dataline);
                    }
                }
                else {
                    assertEquals(expectedNumColumns, pieces.length);
                    // First 12 must always have values
                    for (int k = 0; k < 12; k++) {
                        assertFalse("Nan".equals(pieces[k]));
                    }
                    // Rest may or may not depending on if air sample or water sample
                    for (int k = 0; k < expectedNumColumns; k++) {
                        hasValue[k] = hasValue[k] || !"NaN".equals(pieces[k]);
                    }
                }
                dataline = reader.readLine();
            }
            for (int k = 0; k < expectedNumColumns; k++) {
                // Only sample_depth and one of the pCO2_water_... is all-missing
                if ( (k == 12) || (k == 25) )
                    assertFalse("hasValue[" + k + "]", hasValue[k]);
                else
                    assertTrue("hasValue[" + k + "]", hasValue[k]);
            }
        } finally {
            reader.close();
        }
    }

}
