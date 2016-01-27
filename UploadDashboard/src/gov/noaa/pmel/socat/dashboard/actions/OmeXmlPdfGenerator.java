/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

/**
 * Converts OME XML documents to a human-friendly PDF format.
 * 
 * @author Karl Smith
 */
public class OmeXmlPdfGenerator {

	private MetadataFileHandler metadataHandler;
	private File fopResourcesDir;
	private File xsltFile;
	private FopFactory fopFactory;
	private TransformerFactory transFactory;

	/**
	 * Converts OME XML documents provided by the given metadata file handler 
	 * to a human-friendly PDF format.  Uses the "omexml.xsl" stylesheet
	 * file, along with any other required resource files, in the given
	 * resources directory to format the OME XML.
	 * 
	 * @param resourcesDir
	 * 		directory containing the omexml.xsl stylesheet file as well as
	 * 		and other required Fop resource files
	 * @param metaFileHandler
	 * 		handler for dashboard OME metadata files
	 * @throws IllegalArgumentException
	 * 		if resourcesDir does not exist or is not a directory, or
	 * 		if the omexml.csl file under resourcesDir does not exist, or
	 * 		if metaFileHandler is null
	 * @throws IOException
=	 * 		if FopFactory.newInstance fails, or
	 * 		if the TranformerFactor.newIntance fails
	 */
	public OmeXmlPdfGenerator(File resourcesDir, MetadataFileHandler metaFileHandler) 
			throws IllegalArgumentException, IOException {
		if ( metaFileHandler == null )
			throw new IllegalArgumentException("MetadataFileHandler passed to OmeXmlPdfGenerator is null");
		metadataHandler = metaFileHandler;
		if ( ! resourcesDir.isDirectory() )
			throw new IOException("Does not exist or is not a directory: " + resourcesDir.getPath());
		fopResourcesDir = resourcesDir;
		xsltFile = new File(fopResourcesDir, "omefo.xsl");
		if ( ! xsltFile.canRead() )
			throw new IllegalArgumentException("Cannot read XSL file: " + xsltFile.getPath());
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
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		File xmlFile = metadataHandler.getMetadataFile(upperExpo, DashboardMetadata.PI_OME_FILENAME);
		if ( ! xmlFile.exists() )
			throw new IllegalArgumentException("PI-provided OME XMl file does not exist for " + upperExpo);
		File pdfFile = metadataHandler.getMetadataFile(upperExpo, DashboardMetadata.PI_OME_PDF_FILENAME);
		BufferedOutputStream pdfOut;
		try {
			pdfOut = new BufferedOutputStream(new FileOutputStream(pdfFile));
		} catch (Exception ex) {
			throw new IOException("Cannot create a new PDF for the PI-provided OME: " + ex.getMessage());
		}
		try {
			FOUserAgent foUserAgent;
			try {
				foUserAgent = fopFactory.newFOUserAgent();
				foUserAgent.setTitle(upperExpo + " OME Metadata");
			} catch (Throwable ex) {
				throw new IOException("Unable to create the FOUserAgent: " + ex.getMessage());
			}
			Fop fop;
			try {
				fop = foUserAgent.newFop(MimeConstants.MIME_PDF, pdfOut);
			} catch (Throwable ex) {
				throw new IOException("Unable to create the Fop: " + ex.getMessage());
			}
			Transformer transformer;
			try {
				transformer = transFactory.newTransformer(new StreamSource(xsltFile));
			} catch (Throwable ex) {
				throw new IOException("Unable to create the Tranformer: " + ex.getMessage());
			}
			transformer.setParameter("versionParam", "1.0");
			StreamSource src = new StreamSource(xmlFile);
			SAXResult res;
			try {
				res = new SAXResult(fop.getDefaultHandler());
			} catch (Throwable ex) {
				throw new IOException("Unable to get the default fop handler: " + ex.getMessage());
			}
			try {
				transformer.transform(src, res);
			} catch (Throwable ex) {
				throw new IOException("Unable to to transform: " + ex.getMessage());
			}
		} finally {
			pdfOut.close();
		}
	}

}
