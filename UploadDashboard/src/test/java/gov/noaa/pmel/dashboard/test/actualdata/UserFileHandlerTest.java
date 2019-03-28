package gov.noaa.pmel.dashboard.test.actualdata;

import gov.noaa.pmel.dashboard.handlers.UserFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test of methods in {@link UserFileHandler}
 */
public class UserFileHandlerTest {
    /**
     * Test of {@link UserFileHandler#getDatasetListing(String)}
     */
    @Test
    public void testGetDatasetListing() {
        final String username = "wjoubert";

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

        UserFileHandler userHandler = configStore.getUserFileHandler();
        DashboardDatasetList datasetList = userHandler.getDatasetListing(username);
        assertTrue(datasetList.size() > 0);
        for (String expocode : datasetList.keySet()) {
            assertTrue(expocode.startsWith("91AH201"));
        }
    }
}
