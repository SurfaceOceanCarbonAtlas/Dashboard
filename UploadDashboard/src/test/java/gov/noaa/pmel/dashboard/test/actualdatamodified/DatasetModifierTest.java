package gov.noaa.pmel.dashboard.test.actualdatamodified;

import gov.noaa.pmel.dashboard.actions.DatasetModifier;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.handlers.UserFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetList;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for methods in {@link DatasetModifier}
 */
public class DatasetModifierTest {

    /**
     * Test for {@link DatasetModifier#changeDatasetOwner(String, String)}
     */
    @Test
    public void testChangeDatasetOwner() {
        final String expocode = "91AH20160715";
        final String metaName = "PI_OME.xml";
        final String newOwner = "are.olsen";

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

        DatasetModifier modifier = new DatasetModifier(configStore);
        DataFileHandler dataFileHandler = configStore.getDataFileHandler();
        MetadataFileHandler metaFileHandler = configStore.getMetadataFileHandler();
        UserFileHandler userFileHandler = configStore.getUserFileHandler();

        DashboardDataset dataset = dataFileHandler.getDatasetFromInfoFile(expocode);
        String origOwner = dataset.getOwner();
        assertNotEquals(newOwner, origOwner);
        DashboardMetadata metadata = metaFileHandler.getMetadataInfo(expocode, metaName);
        assertNotEquals(newOwner, metadata.getOwner());
        DashboardDatasetList datasetList = userFileHandler.getDatasetListing(origOwner);
        assertTrue(datasetList.containsKey(expocode));
        datasetList = userFileHandler.getDatasetListing(newOwner);
        assertFalse(datasetList.containsKey(expocode));

        modifier.changeDatasetOwner(expocode, newOwner);

        dataset = dataFileHandler.getDatasetFromInfoFile(expocode);
        assertEquals(newOwner, dataset.getOwner());
        metadata = metaFileHandler.getMetadataInfo(expocode, metaName);
        assertEquals(newOwner, metadata.getOwner());
        datasetList = userFileHandler.getDatasetListing(origOwner);
        assertFalse(datasetList.containsKey(expocode));
        datasetList = userFileHandler.getDatasetListing(newOwner);
        assertTrue(datasetList.containsKey(expocode));

        modifier.changeDatasetOwner(expocode, origOwner);

        dataset = dataFileHandler.getDatasetFromInfoFile(expocode);
        assertEquals(origOwner, dataset.getOwner());
        metadata = metaFileHandler.getMetadataInfo(expocode, metaName);
        assertEquals(origOwner, metadata.getOwner());
        datasetList = userFileHandler.getDatasetListing(origOwner);
        assertTrue(datasetList.containsKey(expocode));
        datasetList = userFileHandler.getDatasetListing(newOwner);
        assertFalse(datasetList.containsKey(expocode));
    }

    /**
     * Test for {@link DatasetModifier#renameDataset(String, String, String)}
     */
    @Test
    public void testRenameDataset() throws IOException, SQLException {
        final String origExpo = "74FS20150630";
        final String newExpo = "000020150630";
        final String adminUser = "karl.smith";

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

        DatasetModifier modifier = new DatasetModifier(configStore);
        DataFileHandler dataFileHandler = configStore.getDataFileHandler();
        MetadataFileHandler metaFileHandler = configStore.getMetadataFileHandler();

        // Verify the original data files exist
        DashboardDataset dataset = dataFileHandler.getDatasetFromInfoFile(origExpo);
        assertNotNull(dataset);
        String origOwner = dataset.getOwner();
        DashboardMetadata metadata = metaFileHandler.getMetadataInfo(origExpo, DashboardServerUtils.OME_FILENAME);
        assertNotNull(metadata);
        try {
            metaFileHandler.getOmeFromFile(metadata);
        } catch ( Exception ex ) {
            fail("Reading the original OME.xml metadata failed");
        }

        modifier.renameDataset(origExpo, newExpo, adminUser);

        dataset = dataFileHandler.getDatasetFromInfoFile(origExpo);
        assertNull(dataset);
        dataset = dataFileHandler.getDatasetFromInfoFile(newExpo);
        assertNotNull(dataset);
        assertEquals(origOwner, dataset.getOwner());
        metadata = metaFileHandler.getMetadataInfo(origExpo, DashboardServerUtils.OME_FILENAME);
        assertNull(metadata);
        metadata = metaFileHandler.getMetadataInfo(newExpo, DashboardServerUtils.OME_FILENAME);
        assertNotNull(metadata);
        assertEquals(origOwner, metadata.getOwner());
        try {
            metaFileHandler.getOmeFromFile(metadata);
        } catch ( Exception ex ) {
            fail("Reading the new OME.xml metadata after rename failed");
        }

        modifier.renameDataset(newExpo, origExpo, adminUser);

        dataset = dataFileHandler.getDatasetFromInfoFile(newExpo);
        assertNull(dataset);
        dataset = dataFileHandler.getDatasetFromInfoFile(origExpo);
        assertNotNull(dataset);
        assertEquals(origOwner, dataset.getOwner());
        metadata = metaFileHandler.getMetadataInfo(newExpo, DashboardServerUtils.OME_FILENAME);
        assertNull(metadata);
        metadata = metaFileHandler.getMetadataInfo(origExpo, DashboardServerUtils.OME_FILENAME);
        assertNotNull(metadata);
        assertEquals(origOwner, metadata.getOwner());
        try {
            metaFileHandler.getOmeFromFile(metadata);
        } catch ( Exception ex ) {
            fail("Reading the original OME.xml metadata after second rename failed");
        }
    }

}
