/**
 * 
 */
package gov.noaa.pmel.dashboard.test.actions;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.dashboard.actions.OmePdfGenerator;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

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
public class OmePdfGeneratorTest {

	private static final String[] EXPOCODE_ARRAY = {
		"06AQ20151030",
		"33GG20131126",
		"33HH20151027",
		"33HH20151112",
		"33LG20150516",
		"33LG20150621",
		"33RO20150806",
		"33RO20150822",
		"61TG20150905",
		"61TG20151012",
		"642B20150808",
		"642B20150819",
		"74EQ20140320",
		"74EQ20141027",
		"77OG20050601",
		"MLCE20150521",
		"MLCE20150602",
		"PANC20150826",
		"PANC20150924",
		"PAT520151007",
		"PAT520151021"
	};

	@Test
	public void testCreatePiOmePdf() throws IOException {
		System.setProperty("CATALINA_BASE", System.getenv("HOME"));
		DashboardConfigStore confStore = DashboardConfigStore.get(false);
		MetadataFileHandler metaHandler = confStore.getMetadataFileHandler();
		OmePdfGenerator omePdfGenerator = confStore.getOmePdfGenerator();
		for ( String expo : EXPOCODE_ARRAY ) {
			File pdfFile = metaHandler.getMetadataFile(expo, DashboardUtils.PI_OME_PDF_FILENAME);
			assertNotNull( pdfFile );
			// Make sure the PDF file does not exist, then generate it
			pdfFile.delete();
			omePdfGenerator.createPiOmePdf(expo);
			assertTrue( pdfFile.exists() );
		}
	}

}
