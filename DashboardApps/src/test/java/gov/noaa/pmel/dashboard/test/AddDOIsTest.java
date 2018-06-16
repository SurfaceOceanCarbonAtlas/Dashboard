package gov.noaa.pmel.dashboard.test;

import gov.noaa.pmel.dashboard.programs.AddDOIs;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test of AddDOIs
 */
public class AddDOIsTest {

    /**
     * Test of {@link AddDOIs#updateDOIsForDataset(String, String, String)}
     */
    @Test
    public void testAddDois() {
        final String expocode = "09AR20140309";
        final String origDoi = "10.3334/CDIAC/OTG.VOS_AA_201";
        final String enhancedDoi = "10.1594/PANGAEA.865506";

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

        try {

            AddDOIs updater = new AddDOIs(configStore.getDataFileHandler());
            String msg = updater.updateDOIsForDataset(expocode, null, "");
            assertNull(msg);

            msg = updater.updateDOIsForDataset(expocode, origDoi, null);
            assertNotNull(msg);
            assertTrue(msg.contains(expocode));
            assertTrue(msg.contains(origDoi));
            assertTrue(msg.contains("archived"));
            assertFalse(msg.contains(enhancedDoi));

            msg = updater.updateDOIsForDataset(expocode, "", enhancedDoi);
            assertNotNull(msg);
            assertTrue(msg.contains(expocode));
            assertFalse(msg.contains(origDoi));
            assertFalse(msg.contains("archived"));
            assertTrue(msg.contains(enhancedDoi));

            msg = updater.updateDOIsForDataset(expocode, origDoi, enhancedDoi);
            assertNotNull(msg);
            assertTrue(msg.contains(expocode));
            assertTrue(msg.contains(origDoi));
            assertTrue(msg.contains("archived"));
            assertTrue(msg.contains(enhancedDoi));

        } finally {
            DashboardConfigStore.shutdown();
        }
    }

}
