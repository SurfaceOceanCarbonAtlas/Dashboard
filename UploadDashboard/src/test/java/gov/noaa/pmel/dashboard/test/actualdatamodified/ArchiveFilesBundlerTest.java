package gov.noaa.pmel.dashboard.test.actualdatamodified;

import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
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
            File zipfile = bundler.getOrigZipBundleFile(expocode);
            assertTrue(zipfile.exists());
        }
    }

}
