/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.actions.OmeXmlPdfGenerator;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

/**
 * Test of generating a PDF from an OME XML file.  Uses an 
 * existing SocatUploadDashboard installation with user-provided
 * PI_OME.xml for the cruise given in EXPOCODE.
 * 
 * @author Karl Smith
 */
public class OmeXmlPdfGeneratorTest {

	private static final String LOG4J_PROPERTIES_FILE = "/home/flat/ksmith/content/SocatUploadDashboard/log4j.properties";
	private static final String METADATA_DOCS_DIR = "/home/flat/ksmith/content/SocatUploadDashboard/MetadataDocs";
	private static final String OME_XML_PDF_RESOURCES = "/home/flat/ksmith/content/SocatUploadDashboard/OmeXmlPdfResources";
	private static final String EXPOCODE = "33LG20150621";

	@Test
	public void testCreatePiOmePdf() throws IOException {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILE);
		MetadataFileHandler metaHandler = new MetadataFileHandler(METADATA_DOCS_DIR, null, null);
		File pdfFile = metaHandler.getMetadataFile(EXPOCODE, DashboardMetadata.PI_OME_PDF_FILENAME);
		// Make sure the PDF file does not exist, then generate it
		pdfFile.delete();
		OmeXmlPdfGenerator omePdfGenerator = new OmeXmlPdfGenerator(new File(OME_XML_PDF_RESOURCES), metaHandler);
		omePdfGenerator.createPiOmePdf(EXPOCODE);
		assertTrue( pdfFile.exists() );
	}

}
