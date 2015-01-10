/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadata;

/**
 * Class for the one special metadata file per cruise that must be present,
 * has a known format, and contains user-provided values needed by the SOCAT 
 * database.  Extends DashboardMetadata, but uses 
 * {@link uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadata}
 * to work with the actual metadata.
 * 
 * @author Karl Smith
 */
public class DashboardOmeMetadata extends DashboardMetadata {

	private static final long serialVersionUID = 1935026048888928520L;

	private static final SimpleDateFormat DATE_PARSER = 
			new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	static {
		DATE_PARSER.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private OmeMetadata omeMData;

	/**
	 * Creates from the contents of the OME XML file specified in the 
	 * DashboardMetadata given. 
	 * 
	 * @param mdata
	 * 		OME XML file to read.  The expocode, upload timestamp, and owner 
	 * 		are copied from this object, and the file specified is read to 
	 * 		populate the OmeMetadata member of this object.
	 * @throws IllegalArgumentException
	 * 		if mdata is null, or
	 * 		if the information in the DashboardMetadata is invalid, or
	 * 		if the contents of the metadata document are not valid
	 */
	public DashboardOmeMetadata(DashboardMetadata mdata) 
											throws IllegalArgumentException {
		// Initialize to an empty OME metadata document with the standard OME filename
		super();
		filename = OME_FILENAME;

		if ( mdata == null )
			throw new IllegalArgumentException("No metadata file given");

		// Copy the expocode, uploadTimestamp, and owner 
		// from the given DashboardMetadata object
		expocode = mdata.getExpocode();
		uploadTimestamp = mdata.getUploadTimestamp();
		owner = mdata.getOwner();

		// Read this metadata document as an XML file
		MetadataFileHandler mdataHandler;
		try {
			mdataHandler = DashboardDataStore.get().getMetadataFileHandler();
		} catch (Exception ex) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the metadata handler");
		}
		File mdataFile = mdataHandler.getMetadataFile(expocode, mdata.getFilename());
		Document omeDoc;
		try {
			omeDoc = (new SAXBuilder()).build(mdataFile);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problems interpreting " +
					"the OME XML contents in " + mdataFile.getName() + 
					"\n    " + ex.getMessage());
		}

		// Create the OmeMetadata object associated with this instance
		// from the OME XML contents
		try {
			omeMData = new OmeMetadata(expocode);
			omeMData.assignFromOmeXmlDoc(omeDoc);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problem with " + mdataFile.getName() +
					"\n    " + ex.getMessage(), ex);
		}
		// If conflicted or otherwise draft, set the conflicted flags in SocatMetadata
		setConflicted(omeMData.isDraft());
	}

	/**
	 * Creates with the given expocode and timestamp, and from the contents 
	 * of the given OME XML document.  The owner is left empty.
	 * 
	 * @param expocode
	 * 		expocode for this metadata
	 * @param timestamp
	 * 		upload timestamp for this metadata
	 * @param omeDoc
	 * 		document containing the metadata contents
	 * @throws IllegalArgumentException
	 * 		if expocode is invalid, or
	 * 		if the contents of the metadata document are not valid
	 */
	public DashboardOmeMetadata(String expocode, String timestamp, Document omeDoc) 
											throws IllegalArgumentException {
		super();
		this.filename = OME_FILENAME;
		this.expocode = DashboardServerUtils.checkExpocode(expocode);
		// Use the setter in case of null
		setUploadTimestamp(timestamp);

		// Read the document to create the OmeMetadata member of this object
		try {
			omeMData = new OmeMetadata(this.expocode);
			omeMData.assignFromOmeXmlDoc(omeDoc);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problems with the provided XML document:" +
					"\n    " + ex.getMessage(), ex);
		}
		// If conflicted or otherwise draft, set the conflicted flags in SocatMetadata
		setConflicted(omeMData.isDraft());
	}

	/**
	 * Create a SocatMetadata object from the data in this object.
	 * 
	 * @param socatVersion
	 * 		SOCAT version to assign
	 * @param addlDocs
	 * 		additional documents to assign
	 * @param qcFlag
	 * 		dataset QC flag to assign
	 * @return
	 *		created SocatMetadata object 
	 */
	public SocatMetadata createSocatMetadata(String socatVersion, 
			Set<String> addlDocs, String qcFlag) throws IllegalArgumentException {
		
		// We cannot create a SocatMetadata object if there are conflicts
		if (isConflicted()) {
			throw new IllegalArgumentException("The Metadata contains conflicts");
		}
		
		SocatMetadata scMData = new SocatMetadata();
		
		scMData.setExpocode(expocode);
		scMData.setCruiseName(omeMData.getExperimentName());
		scMData.setVesselName(omeMData.getVesselName());

		try {
			scMData.setWestmostLongitude(Double.parseDouble(omeMData.getWestmostLongitude()));
		} catch (NumberFormatException | NullPointerException e) {
			scMData.setWestmostLongitude(null);				
		}

		try {
			scMData.setEastmostLongitude(Double.parseDouble(omeMData.getEastmostLongitude()));
		} catch (NumberFormatException | NullPointerException e) {
			scMData.setEastmostLongitude(null);
		}

		try {
			scMData.setSouthmostLatitude(Double.parseDouble(omeMData.getSouthmostLatitude()));
		} catch (NumberFormatException | NullPointerException e) {
			scMData.setSouthmostLatitude(null);
		}

		try {
			scMData.setNorthmostLatitude(Double.parseDouble(omeMData.getNorthmostLatitude()));
		} catch (NumberFormatException | NullPointerException e) {
			scMData.setNorthmostLatitude(null);
		}
		
		try {
			scMData.setBeginTime(DATE_PARSER.parse(omeMData.getTemporalCoverageStartDate() + " 00:00:00"));
		} catch (ParseException e) {
			scMData.setBeginTime(null);
		}

		try {
			scMData.setEndTime(DATE_PARSER.parse(omeMData.getTemporalCoverageEndDate() + " 23:59:59"));
		} catch (ParseException e) {
			scMData.setEndTime(null);
		}
		
		StringBuffer scienceGroup = new StringBuffer();
		for ( String investigator : omeMData.getInvestigators() ) {
			if (scienceGroup.length() == 0) {
				scienceGroup.append(investigator);
			} else {
				scienceGroup.append(SocatMetadata.NAMES_SEPARATOR);
				scienceGroup.append(investigator);
			}
		}
		scMData.setScienceGroup(scienceGroup.toString());
		
		HashSet<String> usedOrganizations = new HashSet<String>();
		StringBuffer orgGroup = new StringBuffer();
		for ( String org : omeMData.getOrganizations() ) {
			if ( (null != org) && usedOrganizations.add(org) ) {
				if (orgGroup.length() == 0) {
					orgGroup.append(org);
				} else {
					orgGroup.append(SocatMetadata.NAMES_SEPARATOR);
					orgGroup.append(org);
				}
				
			}
		}
		scMData.setOrganization(orgGroup.toString());

		// Add names of any ancillary documents
		String docsString = "";
		if ( addlDocs != null ) {
			for ( String docName : addlDocs ) {
				if ( docsString.isEmpty() )
					docsString = docName;
				else
					docsString += SocatMetadata.NAMES_SEPARATOR + docName;
			}
		}
		scMData.setAddlDocs(docsString);

		// Add SOCAT version and QC flag
		scMData.setSocatVersion(socatVersion);
		scMData.setQcFlag(qcFlag);

		return scMData;
	}

	/**
	 * Assigns the expocode associated with this DashboardMetadata as well
	 * as the expocode stored in the OME information represented by this
	 * DashboardMetadata.
	 * 
	 * @param newExpocode
	 * 		new expocode to use
	 */
	public void changeExpocode(String newExpocode) {
		omeMData.changeExpocode(newExpocode);
		setExpocode(newExpocode);
	}
	
	/**
	 * Generated an OME XML document that contains the contents
	 * of the data contained in this OME metadata object. 
	 * 
	 * @return
	 * 		the generated OME XML document
	 */
	public Document createOmeXmlDoc() {
		return omeMData.createOmeXmlDoc();
	}

}
