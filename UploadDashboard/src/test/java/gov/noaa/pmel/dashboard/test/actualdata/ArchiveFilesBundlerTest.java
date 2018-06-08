package gov.noaa.pmel.dashboard.test.actualdata;

import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test of methods in {@link ArchiveFilesBundler}
 */
public class ArchiveFilesBundlerTest {

    @Test
    public void testGenerateOrigFileBundles() throws IOException {
        final List<String> expoList = Arrays.asList("PAT520151021", "26NA20160612");

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
            File bagitFile = bundler.createBagitFilesBundle(expocode, null);
            assertTrue(bagitFile.exists());
        }
    }
}
