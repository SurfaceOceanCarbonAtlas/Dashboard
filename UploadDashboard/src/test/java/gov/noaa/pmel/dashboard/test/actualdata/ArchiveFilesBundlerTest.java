package gov.noaa.pmel.dashboard.test.actualdata;

import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;
import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler.BundleType;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test of methods in {@link ArchiveFilesBundler}
 */
public class ArchiveFilesBundlerTest {

    @Test
    public void testSendOrigFileBundles() throws IOException {
        final List<String> expoList = Arrays.asList("PAT520151021", "26NA20160612", "11SS20150807");

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

        ArchiveFilesBundler bundler = configStore.getArchiveFilesBundler();
        for (String expocode : expoList) {
            // Does not email anything out
            String msg = bundler.sendOrigFilesBundle(expocode, null,
                    DashboardServerUtils.NOMAIL_USER_REAL_NAME, DashboardServerUtils.NOMAIL_USER_EMAIL);
            assertEquals("Data files archival bundle created but not emailed", msg);
            File zipfile = bundler.getZipBundleFile(expocode, BundleType.ORIG_FILE_BAGIT_ZIP);
            assertTrue(zipfile.exists());
        }
    }

    @Test
    public void testCreateEnhancedFilesBundle() throws IOException {
        final List<String> expoList = Arrays.asList("PAT520151021", "26NA20160612", "11SS20150807");

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

        ArchiveFilesBundler bundler = configStore.getArchiveFilesBundler();
        for (String expocode : expoList) {
            ArrayList<String> warnings = bundler.createEnhancedFilesBundle(expocode);
            assertEquals(0, warnings.size());
            File zipFile = bundler.getZipBundleFile(expocode, BundleType.ENHANCED_FILE_PLAIN_ZIP);
            assertTrue(zipFile.exists());
        }
    }

}
