/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

/**
 * Converts OME XML documents to a human-friendly PDF format.
 * 
 * @author Karl Smith
 */
public class OmeXmlPdfGenerator {

	private static final String FOP_RESOURCES_SUBDIR_NAME = "fopresources";
	private static final String OME_XSL_FILENAME = "ome_xml.xsl";
	
	private MetadataFileHandler metadataHandler;
	private File fopResourcesDir;
	private File xsltFile;
	private FopFactory fopFactory;
	private TransformerFactory transFactory;

	/**
	 * Converts OME XML documents provided by the given metadata file handler 
	 * to a human-friendly PDF format.  Uses the XSL file {@value #OME_XSL_FILENAME} 
	 * and other any other needed resources under {@value #FOP_RESOURCES_SUBDIR_NAME}.
	 * 
	 * @param metaFileHandler
	 * 		handler for dashboard OME metadata files
	 * 
	 * @throws IOException
	 * 		if the fopresources subdirectory does not exist, or
	 * 		if FopFactory.newInstance fails, or
	 * 		if the TranformerFactor.newIntance fails
	 */
	public OmeXmlPdfGenerator(MetadataFileHandler metaFileHandler) throws IOException {
		if ( metaFileHandler == null )
			throw new NullPointerException(
					"MetadataFileHandler passed to OmeXmlPdfGenerator is null");
		metadataHandler = metaFileHandler;
		fopResourcesDir = new File(FOP_RESOURCES_SUBDIR_NAME);
		if ( ! fopResourcesDir.isDirectory() )
			throw new IOException("Does not exist or is not a directory: " + fopResourcesDir.getPath());
		xsltFile = new File(fopResourcesDir, OME_XSL_FILENAME);
		if ( ! xsltFile.exists() )
			throw new IOException("Does not exist: " + xsltFile.getPath());
		try {
			fopFactory = FopFactory.newInstance(fopResourcesDir.toURI());
		} catch (Throwable ex) {
			throw new IOException("Unable to create the FopFactory: " + ex.getMessage());
		}
		try {
			transFactory = TransformerFactory.newInstance();
		} catch (Throwable ex) {
			throw new IOException("Unable to create the TransformerFactory: " + ex.getMessage());
		}
	}

	/**
	 * Convert the PI-provided OME XML file for the given dataset to a human-friendly PDF file.
	 * 
	 * @param expocode
	 * 		convert the PI-provided OME XML file for this dataset
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid or if the PI-provided OME XML file does not exist
	 * @throws IOException
	 * 		if the conversion fails
	 */
	public void createPiOmePdf(String expocode) throws IllegalArgumentException, IOException {
		// The following throws IllegalArgumentException if expocode is invalid
		File xmlFile = metadataHandler.getMetadataFile(expocode, DashboardMetadata.PI_OME_FILENAME);
		if ( ! xmlFile.exists() )
			throw new IllegalArgumentException("PI-provided OME XMl file does not exist for " + expocode);
		File pdfFile = metadataHandler.getMetadataFile(expocode, DashboardMetadata.PI_OME_PDF_FILENAME);
		BufferedOutputStream pdfOut = new BufferedOutputStream(new FileOutputStream(pdfFile));
		try {
			Fop fop;
			try {
				fop = fopFactory.newFop(MimeConstants.MIME_PDF, pdfOut);
			} catch (Exception ex) {
				throw new IOException("Unable to create the Fop: " + ex.getMessage());
			}
			Transformer transformer;
			try {
				transformer = transFactory.newTransformer(new StreamSource(xsltFile));
			} catch (Exception ex) {
				throw new IOException("Unable to create the Tranformer: " + ex.getMessage());
			}
			transformer.setParameter("versionParam", "1.0");
			StreamSource src = new StreamSource(xmlFile);
			SAXResult res;
			try {
				res = new SAXResult(fop.getDefaultHandler());
			} catch (Exception ex) {
				throw new IOException("Unable to get the default fop handler: " + ex.getMessage());
			}
			try {
				transformer.transform(src, res);
			} catch (Exception ex) {
				throw new IOException("Unable to to transform: " + ex.getMessage());
			}
		} finally {
			pdfOut.close();
		}
	}

}
