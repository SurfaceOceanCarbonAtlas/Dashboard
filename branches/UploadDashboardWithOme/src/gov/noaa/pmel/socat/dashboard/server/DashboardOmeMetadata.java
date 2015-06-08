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
import java.util.ArrayList;
import java.util.Date;
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

	private static final long serialVersionUID = 5517166413080961933L;

	private static final SimpleDateFormat TIMEPARSER = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat TIMESTAMPER = new SimpleDateFormat("yyyy-MM-dd");
	static {
		TimeZone utc = TimeZone.getTimeZone("UTC");
		TIMEPARSER.setTimeZone(utc);
		TIMESTAMPER.setTimeZone(utc);
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
	 * @param mdataHandler
	 * 		MetadataFileHandler to use get the given OME XML file
	 * @throws IllegalArgumentException
	 * 		if mdata is null, or
	 * 		if the information in the DashboardMetadata is invalid, or
	 * 		if the contents of the metadata document are not valid
	 */
	public DashboardOmeMetadata(DashboardMetadata mdata, 
			MetadataFileHandler mdataHandler) throws IllegalArgumentException {
		// Initialize to an empty OME metadata document with the standard OME filename
		super();
		filename = OME_FILENAME;

		if ( mdata == null )
			throw new IllegalArgumentException("No metadata file given");

		// Copy the expocode, uploadTimestamp, owner, and version
		// from the given DashboardMetadata object
		expocode = mdata.getExpocode();
		uploadTimestamp = mdata.getUploadTimestamp();
		owner = mdata.getOwner();
		version = mdata.getVersion();

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
		// If conflicted or incomplete for DSG files, set the conflicted flags in SocatMetadata
		setConflicted( ! omeMData.isAcceptable() );
	}

	/**
	 * Creates with the given expocode and timestamp, and from the contents 
	 * of the given OME XML document.  The owner and version is left empty.
	 * 
	 * @param expo
	 * 		expocode for this metadata
	 * @param timestamp
	 * 		upload timestamp for this metadata
	 * @param omeDoc
	 * 		document containing the metadata contents
	 * @throws IllegalArgumentException
	 * 		if expocode is invalid, or
	 * 		if the contents of the metadata document are not valid
	 */
	public DashboardOmeMetadata(String expo, String timestamp, Document omeDoc) 
											throws IllegalArgumentException {
		super();
		filename = OME_FILENAME;
		expocode = DashboardServerUtils.checkExpocode(expo);
		// Use the setter in case of null
		setUploadTimestamp(timestamp);

		// Read the document to create the OmeMetadata member of this object
		try {
			omeMData = new OmeMetadata(expocode);
			omeMData.assignFromOmeXmlDoc(omeDoc);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problems with the provided XML document:" +
					"\n    " + ex.getMessage(), ex);
		}
		// If conflicted or incomplete for DSG files, set the conflicted flags in SocatMetadata
		setConflicted( ! omeMData.isAcceptable() );
	}

	/**
	 * Creates using the given OmeMetadata.  The expocode is obtained from the OmeMetadata.
	 * 
	 * @param omeMeta
	 * 		the OmeMetadata contents of this metadata
	 * @param timestamp
	 * 		the upload timestamp for this metadata
	 * @param owner
	 * 		the owner of this metadata
	 * @param version
	 * 		the SOCAT version of this metadata
	 */
	public DashboardOmeMetadata(OmeMetadata omeMeta, String timestamp, String owner, String version) {
		super();
		filename = OME_FILENAME;
		expocode = DashboardServerUtils.checkExpocode(omeMeta.getExpocode());
		setUploadTimestamp(timestamp);
		setOwner(owner);
		setVersion(version);
		omeMData = omeMeta;
		// If conflicted or incomplete for DSG files, set the conflicted flags in SocatMetadata
		setConflicted( ! omeMData.isAcceptable() );
	}

	/**
	 * Create a SocatMetadata object from the data in this object.
	 * Any known non-standard PI or vessel names will be changed to
	 * the standard format (which might be intentionally misspelled).
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
		if ( isConflicted() ) {
			throw new IllegalArgumentException("The Metadata contains conflicts");
		}

		SocatMetadata scMData = new SocatMetadata();
		
		scMData.setExpocode(expocode);
		scMData.setCruiseName(omeMData.getExperimentName());

		// Check if the vessel name needs standardizing before assigning it
		String vesselName = omeMData.getVesselName();
		String stdVesselName = SocatMetadata.VESSEL_RENAME_MAP.get(vesselName);
		if ( stdVesselName == null )
			stdVesselName = vesselName;
		scMData.setVesselName(stdVesselName);

		try {
			scMData.setWestmostLongitude(Double.parseDouble(omeMData.getWestmostLongitude()));
		} catch (NumberFormatException | NullPointerException ex) {
			scMData.setWestmostLongitude(null);				
		}

		try {
			scMData.setEastmostLongitude(Double.parseDouble(omeMData.getEastmostLongitude()));
		} catch (NumberFormatException | NullPointerException ex) {
			scMData.setEastmostLongitude(null);
		}

		try {
			scMData.setSouthmostLatitude(Double.parseDouble(omeMData.getSouthmostLatitude()));
		} catch (NumberFormatException | NullPointerException ex) {
			scMData.setSouthmostLatitude(null);
		}

		try {
			scMData.setNorthmostLatitude(Double.parseDouble(omeMData.getNorthmostLatitude()));
		} catch (NumberFormatException | NullPointerException ex) {
			scMData.setNorthmostLatitude(null);
		}
		
		SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			scMData.setBeginTime(dateParser.parse(omeMData.getTemporalCoverageStartDate() + " 00:00:00"));
		} catch (ParseException ex) {
			scMData.setBeginTime(null);
		}
		try {
			scMData.setEndTime(dateParser.parse(omeMData.getTemporalCoverageEndDate() + " 23:59:59"));
		} catch (ParseException ex) {
			scMData.setEndTime(null);
		}

		StringBuffer scienceGroup = new StringBuffer();
		for ( String investigator : omeMData.getInvestigators() ) {
			// Check if any investigator names need to be standardized
			String piName = SocatMetadata.PI_RENAME_MAP.get(investigator);
			if ( piName == null )
				piName = investigator;
			if (scienceGroup.length() == 0) {
				scienceGroup.append(piName);
			} else {
				scienceGroup.append(SocatMetadata.NAMES_SEPARATOR);
				scienceGroup.append(piName);
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
		omeMData.setExpocode(newExpocode);
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

	/**
	 * Creates a new DashboardOmeMetadata from merging, where appropriate, 
	 * the OME content of this instance with the OME content of other.  
	 * The expocodes in other must be the same as in this instance.  
	 * Fields derived from the data are the same as those in this instance.
	 *  
	 * @param other
	 * 		merge with this OME content
	 * @return
	 * 		new DasboardOmeMetadata with merged content, where appropriate
	 * @throws IllegalArgumentException
	 * 		if the expocodes in this instance and other do not match
	 */
	public DashboardOmeMetadata mergeModifiable(DashboardOmeMetadata other) 
											throws IllegalArgumentException {
		OmeMetadata mergedOmeMData;
		try {
			// Merge the OmeMetadata documents - requires the expocodes be the same
			mergedOmeMData = OmeMetadata.merge(this.omeMData, other.omeMData);

			// Some fields should not have been merged; reset to the values in this instance
			// setExpcode sets
			//   cruise ID = dataset ID = expocode, 
			//   vessel ID = NODC code from expocode, 
			//   cruise start date = start date from expocode
			mergedOmeMData.setExpocode(this.expocode);

			String value = this.omeMData.getValue(OmeMetadata.END_DATE_STRING);
			if ( ! OmeMetadata.CONFLICT_STRING.equals(value) )
				mergedOmeMData.replaceValue(OmeMetadata.END_DATE_STRING, value, -1);

			value = this.omeMData.getValue(OmeMetadata.TEMP_START_DATE_STRING);
			if ( ! OmeMetadata.CONFLICT_STRING.equals(value) )
				mergedOmeMData.replaceValue(OmeMetadata.TEMP_START_DATE_STRING, value, -1);

			value = this.omeMData.getValue(OmeMetadata.TEMP_END_DATE_STRING);
			if ( ! OmeMetadata.CONFLICT_STRING.equals(value) )
				mergedOmeMData.replaceValue(OmeMetadata.TEMP_END_DATE_STRING, value, -1);

			value = this.omeMData.getValue(OmeMetadata.WEST_BOUND_STRING);
			if ( ! OmeMetadata.CONFLICT_STRING.equals(value) )
				mergedOmeMData.replaceValue(OmeMetadata.WEST_BOUND_STRING, value, -1);

			value = this.omeMData.getValue(OmeMetadata.EAST_BOUND_STRING);
			if ( ! OmeMetadata.CONFLICT_STRING.equals(value) )
				mergedOmeMData.replaceValue(OmeMetadata.EAST_BOUND_STRING, value, -1);

			value = this.omeMData.getValue(OmeMetadata.SOUTH_BOUND_STRING);
			if ( ! OmeMetadata.CONFLICT_STRING.equals(value) )
				mergedOmeMData.replaceValue(OmeMetadata.SOUTH_BOUND_STRING, value, -1);

			value = this.omeMData.getValue(OmeMetadata.NORTH_BOUND_STRING);
			if ( ! OmeMetadata.CONFLICT_STRING.equals(value) )
				mergedOmeMData.replaceValue(OmeMetadata.NORTH_BOUND_STRING, value, -1);

			mergedOmeMData.setDraft( ! mergedOmeMData.isAcceptable() );
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unable to merge OME documents: " + ex.getMessage(), ex);
		}
		return new DashboardOmeMetadata(mergedOmeMData, this.uploadTimestamp, this.owner, this.version);
	}

	/**
	 * @return 
	 * 		the westernmost longitude, in the range (-180,180]
	 * @throws IllegalArgumentException
	 * 		if the westernmost longitude is invalid
	 */
	public double getWestmostLongitude() throws IllegalArgumentException {
		double westLon;
		try {
			westLon = Double.parseDouble(omeMData.getWestmostLongitude());
			if ( (westLon < -540.0) || (westLon > 540.0) )
				throw new IllegalArgumentException("not in [-540,540]");
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid westmost longitude: " + ex.getMessage());
		}
		while ( westLon <= -180.0 )
			westLon += 360.0;
		while ( westLon > 180.0 )
			westLon -= 360.0;
		return westLon;
	}

	/**
	 * @return 
	 * 		the easternmost longitude, in the range (-180,180]
	 * @throws IllegalArgumentException
	 * 		if the easternmost longitude is invalid
	 */
	public double getEastmostLongitude() throws IllegalArgumentException {
		double eastLon;
		try {
			eastLon = Double.parseDouble(omeMData.getEastmostLongitude());
			if ( (eastLon < -540.0) || (eastLon > 540.0) )
				throw new IllegalArgumentException("not in [-540,540]");
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid eastmost longitude: " + ex.getMessage());
		}
		while ( eastLon <= -180.0 )
			eastLon += 360.0;
		while ( eastLon > 180.0 )
			eastLon -= 360.0;
		return eastLon;
	}

	/**
	 * @return 
	 * 		the southernmost latitude
	 * @throws IllegalArgumentException
	 * 		if the southernmost latitude is invalid
	 */
	public double getSouthmostLatitude() throws IllegalArgumentException {
		double southLat;
		try {
			southLat = Double.parseDouble(omeMData.getSouthmostLatitude());
			if ( (southLat < -90.0) || (southLat > 90.0) )
				throw new IllegalArgumentException("not in [-90,90]");
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid southmost latitude: " + ex.getMessage());
		}
		return southLat;
	}

	/**
	 * @return 
	 * 		the northernmost latitude
	 * @throws IllegalArgumentException
	 * 		if the northernmost latitude is invalid
	 */
	public double getNorthmostLatitude() throws IllegalArgumentException {
		double northLat;
		try {
			northLat = Double.parseDouble(omeMData.getNorthmostLatitude());
			if ( (northLat < -90.0) || (northLat > 90.0) )
				throw new IllegalArgumentException("not in [-90,90]");
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid northmost latitude: " + ex.getMessage());
		}
		return northLat;
	}

	/**
	 * @return
	 * 		the date stamp of the earliest data measurement
	 * @throws IllegalArgumentException
	 * 		if the date of the earliest data measurement is invalid
	 */
	public String getBeginDatestamp() throws IllegalArgumentException {
		String beginTimestamp;
		try {
			Date beginTime = TIMEPARSER.parse(omeMData.getTemporalCoverageStartDate());
			beginTimestamp = TIMESTAMPER.format(beginTime);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid begin time: " + ex.getMessage());
		}
		return beginTimestamp;
	}

	/**
	 * @return
	 * 		the date stamp of the latest data measurement
	 * @throws IllegalArgumentException
	 * 		if the date of the latest data measurement is invalid
	 */
	public String getEndDatestamp() throws IllegalArgumentException {
		String endTimestamp;
		try {
			Date endTime = TIMEPARSER.parse(omeMData.getTemporalCoverageEndDate());
			endTimestamp = TIMESTAMPER.format(endTime);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid begin time: " + ex.getMessage());
		}
		return endTimestamp;
	}

	/**
	 * @return
	 * 		the name given by the PI for this dataset; 
	 * 		never null but may be empty
	 */
	public String getCruiseName() {
		String cruiseName = omeMData.getExperimentName();
		if ( cruiseName == null )
			return "";
		return cruiseName;
	}

	/**
	 * @return
	 * 		the vessel name for this dataset; 
	 * 		never null but may be empty
	 */
	public String getVesselName() {
		String vesselName = omeMData.getVesselName();
		if ( vesselName == null )
			return "";
		return vesselName;
	}

	/**
	 * @return
	 * 		the semicolon-separated list of PI names for this dataset; 
	 * 		never null but may be empty
	 */
	public String getScienceGroup() {
		ArrayList<String> scienceGroup = omeMData.getInvestigators();
		if ( scienceGroup == null )
			return "";
		String piNames = "";
		boolean isFirst = true;
		for ( String name : scienceGroup ) {
			if ( isFirst )
				isFirst = false;
			else
				piNames += "; ";
			piNames += name;
		}
		return piNames;
	}

	/**
	 * @return
	 * 		the original reference(s) for this dataset; 
	 * 		never null but may be empty
	 */
	public String getOrigDataRef() {
		String dataSetRefs;
		try {
			dataSetRefs = omeMData.getValue(OmeMetadata.DATA_SET_REFS_STRING);
			if ( dataSetRefs == null )
				dataSetRefs = "";
		} catch (Exception ex) {
			// Should never happen
			dataSetRefs = "";
		}
		return dataSetRefs;
	}

}
