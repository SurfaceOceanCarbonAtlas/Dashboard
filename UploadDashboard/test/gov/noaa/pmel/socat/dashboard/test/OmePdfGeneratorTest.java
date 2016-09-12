/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.actions.OmePdfGenerator;
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
public class OmePdfGeneratorTest {

	private static final String LOG4J_PROPERTIES_FILE = 
			"/home/flat/ksmith/Socat/Tomcat/content/SocatUploadDashboard/log4j.properties";
	private static final String METADATA_DOCS_DIR = 
			"/home/flat/ksmith/Socat/Tomcat/content/SocatUploadDashboard/MetadataDocs";
	private static final String OME_XML_PDF_RESOURCES = 
			"/home/flat/ksmith/Socat/Tomcat/content/SocatUploadDashboard";
	private static final String SVN_USERNAME = "ksmith";
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
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILE);
		MetadataFileHandler metaHandler = new MetadataFileHandler(METADATA_DOCS_DIR, SVN_USERNAME, null);
		for ( String expo : EXPOCODE_ARRAY ) {
			File pdfFile = metaHandler.getMetadataFile(expo, DashboardMetadata.PI_OME_PDF_FILENAME);
			// Make sure the PDF file does not exist, then generate it
			pdfFile.delete();
			OmePdfGenerator omePdfGenerator = new OmePdfGenerator(
					new File(OME_XML_PDF_RESOURCES), metaHandler, null);
			omePdfGenerator.createPiOmePdf(expo);
			assertTrue( pdfFile.exists() );
		}
	}

}
