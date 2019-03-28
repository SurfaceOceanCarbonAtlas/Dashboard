package gov.noaa.pmel.dashboard.test.actualdata;

import gov.noaa.pmel.dashboard.client.DatasetPreviewPage;
import gov.noaa.pmel.dashboard.handlers.PreviewPlotsHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Test of generating the preview plots, which also tests the
 * complete process of generating the DSG files.  Uses an
 * existing UploadDashboard installation with user-provided
 * data and metadata for the cruise given in EXPOCODE.
 *
 * @author Karl Smith
 */
public class PreviewPlotsHandlerTest {

    private static final String EXPOCODE = "09AR20130113";

    @Test
    public void testCreatePreviewPlots() throws IOException {
        System.setProperty("CATALINA_BASE", System.getenv("HOME"));
        System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "SocatUploadDashboard");
        final String timetag = "testing";
        DashboardConfigStore configStore = DashboardConfigStore.get(false);
        PreviewPlotsHandler plotsHandler = configStore.getPreviewPlotsHandler();
        File dsgFilesDir = plotsHandler.getDatasetPreviewDsgDir(EXPOCODE);
        File plotsDir = plotsHandler.getDatasetPreviewPlotsDir(EXPOCODE);

        plotsHandler.createPreviewPlots(EXPOCODE, timetag);

        File dsgFile = new File(dsgFilesDir, EXPOCODE + "_" + timetag + ".nc");
        assertTrue(dsgFile.exists());
        dsgFile.delete();

        File plotFile;
        for (String imgName : new String[] {
                DatasetPreviewPage.LAT_VS_LON_IMAGE_NAME,
                DatasetPreviewPage.LAT_LON_IMAGE_NAME,
                DatasetPreviewPage.SAMPLE_VS_TIME_IMAGE_NAME,
                DatasetPreviewPage.TIME_SERIES_IMAGE_NAME,
                DatasetPreviewPage.PRESSURES_IMAGE_NAME,
                DatasetPreviewPage.TEMPERATURES_IMAGE_NAME,
                DatasetPreviewPage.SALINITIES_IMAGE_NAME,
                DatasetPreviewPage.XCO2S_IMAGE_NAME,
                DatasetPreviewPage.DT_XCO2_FCO2_IMAGE_NAME,
                DatasetPreviewPage.REC_FCO2_VS_TIME_IMAGE_NAME,
                DatasetPreviewPage.REC_FCO2_VS_SST_IMAGE_NAME,
                DatasetPreviewPage.REC_FCO2_VS_SAL_IMAGE_NAME,
                DatasetPreviewPage.REPORT_REC_FCO2_IMAGE_NAME,
                DatasetPreviewPage.REC_FCO2_DELTA_IMAGE_NAME,
                DatasetPreviewPage.REC_FCO2_SOURCES_IMAGE_NAME }) {
            plotFile = new File(plotsDir, EXPOCODE + "_" + imgName + "_" + timetag + ".png");
            assertTrue("Plot for " + imgName + " does not exist", plotFile.exists());
            plotFile.delete();
        }
    }

}
