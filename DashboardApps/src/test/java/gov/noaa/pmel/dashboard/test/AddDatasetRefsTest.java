package gov.noaa.pmel.dashboard.test;

import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.programs.AddDatasetRefs;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test of AddDatasetRefs
 */
public class AddDatasetRefsTest {

    /**
     * Test of {@link AddDatasetRefs#updateReferencesForDataset(String, String, String, String, String)}
     */
    @Test
    public void testAddDois() {
        final String expocode = "09AR20140309";
        final String origDoi = "10.3334/CDIAC/OTG.VOS_AA_2014";
        final String origUrl = "https://accession.nodc.noaa.gov/0160488";
        // Add a space before the DOI, which will be removed when recorded,
        // in order to differentiate the DOI from the URL when searching the return message
        final String enhancedDoi = " 10.1594/PANGAEA.865506";
        final String enhancedUrl = "https://doi.pangaea.de/10.1594/PANGAEA.865506";

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
            DataFileHandler dataHandler = configStore.getDataFileHandler();
            DashboardDataset dataset;

            AddDatasetRefs updater = new AddDatasetRefs(dataHandler);
            String msg = updater.updateReferencesForDataset(expocode, null, "", null, "");
            assertNull(msg);

            msg = updater.updateReferencesForDataset(expocode, origDoi, null, null, null);
            assertNotNull(msg);
            assertTrue(msg.contains(expocode));
            assertTrue(msg.contains(origDoi));
            assertFalse(msg.contains(origUrl));
            assertTrue(msg.contains("archived"));
            assertFalse(msg.contains(enhancedDoi));
            assertFalse(msg.contains(enhancedUrl));
            dataset = dataHandler.getDatasetFromInfoFile(expocode);
            assertEquals(origDoi, dataset.getSourceDOI());

            msg = updater.updateReferencesForDataset(expocode, null, origUrl, null, null);
            assertNotNull(msg);
            assertTrue(msg.contains(expocode));
            assertFalse(msg.contains(origDoi));
            assertTrue(msg.contains(origUrl));
            assertTrue(msg.contains("archived"));
            assertFalse(msg.contains(enhancedDoi));
            assertFalse(msg.contains(enhancedUrl));
            dataset = dataHandler.getDatasetFromInfoFile(expocode);
            assertEquals(origUrl, dataset.getSourceURL());

            msg = updater.updateReferencesForDataset(expocode, origDoi, origUrl, null, null);
            assertNotNull(msg);
            assertTrue(msg.contains(expocode));
            assertTrue(msg.contains(origDoi));
            assertTrue(msg.contains(origUrl));
            assertTrue(msg.contains("archived"));
            assertFalse(msg.contains(enhancedDoi));
            assertFalse(msg.contains(enhancedUrl));
            dataset = dataHandler.getDatasetFromInfoFile(expocode);
            assertEquals(origDoi, dataset.getSourceDOI());
            assertEquals(origUrl, dataset.getSourceURL());

            msg = updater.updateReferencesForDataset(expocode, null, null, enhancedDoi, null);
            assertNotNull(msg);
            assertTrue(msg.contains(expocode));
            assertFalse(msg.contains(origDoi));
            assertFalse(msg.contains(origUrl));
            assertFalse(msg.contains("archived"));
            assertTrue(msg.contains(enhancedDoi));
            assertFalse(msg.contains(enhancedUrl));
            dataset = dataHandler.getDatasetFromInfoFile(expocode);
            assertEquals(enhancedDoi.trim(), dataset.getEnhancedDOI());

            msg = updater.updateReferencesForDataset(expocode, null, null, null, enhancedUrl);
            assertNotNull(msg);
            assertTrue(msg.contains(expocode));
            assertFalse(msg.contains(origDoi));
            assertFalse(msg.contains(origUrl));
            assertFalse(msg.contains("archived"));
            assertFalse(msg.contains(enhancedDoi));
            assertTrue(msg.contains(enhancedUrl));
            dataset = dataHandler.getDatasetFromInfoFile(expocode);
            assertEquals(enhancedUrl, dataset.getEnhancedURL());

            msg = updater.updateReferencesForDataset(expocode, null, null, enhancedDoi, enhancedUrl);
            assertNotNull(msg);
            assertTrue(msg.contains(expocode));
            assertFalse(msg.contains(origDoi));
            assertFalse(msg.contains(origUrl));
            assertFalse(msg.contains("archived"));
            assertTrue(msg.contains(enhancedDoi));
            assertTrue(msg.contains(enhancedUrl));
            dataset = dataHandler.getDatasetFromInfoFile(expocode);
            assertEquals(enhancedDoi.trim(), dataset.getEnhancedDOI());
            assertEquals(enhancedUrl, dataset.getEnhancedURL());

            msg = updater.updateReferencesForDataset(expocode, origDoi, origUrl, enhancedDoi, enhancedUrl);
            assertNotNull(msg);
            assertTrue(msg.contains(expocode));
            assertTrue(msg.contains(origDoi));
            assertTrue(msg.contains(origUrl));
            assertTrue(msg.contains("archived"));
            assertTrue(msg.contains(enhancedDoi));
            assertTrue(msg.contains(enhancedUrl));
            dataset = dataHandler.getDatasetFromInfoFile(expocode);
            assertEquals(origDoi, dataset.getSourceDOI());
            assertEquals(origUrl, dataset.getSourceURL());
            assertEquals(enhancedDoi.trim(), dataset.getEnhancedDOI());
            assertEquals(enhancedUrl, dataset.getEnhancedURL());

        } finally {
            DashboardConfigStore.shutdown();
        }
    }

}
