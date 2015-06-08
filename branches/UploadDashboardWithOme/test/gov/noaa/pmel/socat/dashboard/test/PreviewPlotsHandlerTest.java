package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.client.CruisePreviewPage;
import gov.noaa.pmel.socat.dashboard.handlers.PreviewPlotsHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * Test of generating the preview plots, which also tests the 
 * complete process of generating the DSG files.  Uses an 
 * existing SocatUploadDashboard installation with user-provided
 * data and metadata for the cruise given in EXPOCODE.
 * 
 * @author Karl Smith
 */
public class PreviewPlotsHandlerTest {

	private static final String EXPOCODE = "09AR20130113";

	@Test
	public void testCreatePreviewPlots() throws IOException {
		final String timetag = "testing";
		DashboardConfigStore configStore = DashboardConfigStore.get();
		PreviewPlotsHandler plotsHandler = configStore.getPreviewPlotsHandler();
		File dsgFilesDir = plotsHandler.getCruisePreviewDsgDir(EXPOCODE);
		File plotsDir = plotsHandler.getCruisePreviewPlotsDir(EXPOCODE);

		plotsHandler.createPreviewPlots(EXPOCODE, timetag);

		File dsgFile = new File(dsgFilesDir, EXPOCODE + "_" + timetag + ".nc");
		assertTrue( dsgFile.exists() );
		dsgFile.delete();

		File plotFile;
		for ( String imgName : new String[] {
				CruisePreviewPage.LAT_VS_LON_IMAGE_NAME,
				CruisePreviewPage.LAT_LON_IMAGE_NAME,
				CruisePreviewPage.SAMPLE_VS_TIME_IMAGE_NAME,
				CruisePreviewPage.TIME_SERIES_IMAGE_NAME,
				CruisePreviewPage.PRESSURES_IMAGE_NAME,
				CruisePreviewPage.TEMPERATURES_IMAGE_NAME,
				CruisePreviewPage.SALINITIES_IMAGE_NAME,
				CruisePreviewPage.XCO2S_IMAGE_NAME,
				CruisePreviewPage.REC_FCO2_VS_TIME_IMAGE_NAME,
				CruisePreviewPage.REC_FCO2_VS_SST_IMAGE_NAME,
				CruisePreviewPage.REC_FCO2_VS_SAL_IMAGE_NAME,
				CruisePreviewPage.REC_FCO2_DELTA_IMAGE_NAME,
				CruisePreviewPage.REC_FCO2_SOURCES_IMAGE_NAME } ) {
			plotFile = new File(plotsDir, EXPOCODE + "_" + imgName + "_" + timetag + ".gif");
			assertTrue( "Plot for " + imgName + " does not exist", plotFile.exists() );
			plotFile.delete();
		}
	}

}
