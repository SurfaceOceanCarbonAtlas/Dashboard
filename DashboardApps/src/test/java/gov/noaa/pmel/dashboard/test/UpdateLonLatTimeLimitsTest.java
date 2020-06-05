package gov.noaa.pmel.dashboard.test;

import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.programs.UpdateLonLatTimeLimits;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UpdateLonLatTimeLimitsTest {

    @Test
    public void testUpdateLimits() {
        final String expocode = "PAT520151021";

        System.setProperty("CATALINA_BASE", System.getenv("HOME") + "/Tomcat");
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
            // First mess up the limits
            MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
            DashboardMetadata omeInfo = metadataHandler.getMetadataInfo(expocode, DashboardServerUtils.OME_FILENAME);
            DashboardOmeMetadata omeMData = metadataHandler.getOmeFromFile(omeInfo);
            omeMData.setWestmostLongitude(25.0);
            omeMData.setEastmostLongitude(25.0);
            omeMData.setSouthmostLatitude(25.0);
            omeMData.setNorthmostLatitude(25.0);
            omeMData.setDataBeginTime(25.0);
            omeMData.setDataEndTime(25.0);
            metadataHandler.saveOmeToFile(omeMData, null);

            UpdateLonLatTimeLimits updater = new UpdateLonLatTimeLimits(configStore);
            updater.updateLimits(expocode);

            omeMData = metadataHandler.getOmeFromFile(omeInfo);
            assertEquals(134.960, omeMData.getWestmostLongitude(), 1.0E-6);
            assertEquals(173.247, omeMData.getEastmostLongitude(), 1.0E-6);
            assertEquals(-40.719, omeMData.getSouthmostLatitude(), 1.0E-6);
            assertEquals(34.545, omeMData.getNorthmostLatitude(), 1.0E-6);
            assertEquals("2015-10-21", omeMData.getBeginDatestamp());
            assertEquals("2015-11-01", omeMData.getEndDatestamp());
        } catch ( Exception ex ) {
            ex.printStackTrace();
            fail("Exception raised: " + ex.getMessage());
        } finally {
            DashboardConfigStore.shutdown();
        }
    }

}
