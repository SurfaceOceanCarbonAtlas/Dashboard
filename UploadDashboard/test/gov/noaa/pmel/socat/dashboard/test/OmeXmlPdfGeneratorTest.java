/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.socat.dashboard.actions.OmeXmlPdfGenerator;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * Test of generating a PDF from an OME XML file.  Uses an 
 * existing SocatUploadDashboard installation with user-provided
 * PI_OME.xml for the cruise given in EXPOCODE.
 * 
 * @author Karl Smith
 */
public class OmeXmlPdfGeneratorTest {

	private static final String EXPOCODE = "33LG20150621";

	@Test
	public void testCreatePiOmePdf() throws IOException {
		DashboardConfigStore configStore = DashboardConfigStore.get(false);
		OmeXmlPdfGenerator omePdfGenerator = configStore.getOmePdfGenerator();
		omePdfGenerator.createPiOmePdf(EXPOCODE);
		File pdfFile = configStore.getMetadataFileHandler().getMetadataFile(EXPOCODE, DashboardMetadata.PI_OME_PDF_FILENAME);
		assertTrue( pdfFile.exists() );
	}

}
