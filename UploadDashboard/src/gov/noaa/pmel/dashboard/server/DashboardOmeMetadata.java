/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TimeZone;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import uk.ac.uea.socat.omemetadata.OmeMetadata;

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

	private static final long serialVersionUID = 7018407686296151274L;

	/**
	 * String separating each PI listed in scienceGroup, each organization 
	 * listed in organizations, and each additional document filename listed 
	 * in addlDocs.  This is cannot be a semicolon due to Ferret issues.
	 */
	private static final String NAMES_SEPARATOR = " : ";

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
		filename = DashboardUtils.OME_FILENAME;

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
					"the OME XML contents in " + mdataFile.getPath() + 
					"\n    " + ex.getMessage());
		}

		// Create the OmeMetadata object associated with this instance
		// from the OME XML contents
		try {
			omeMData = new OmeMetadata(expocode);
			omeMData.assignFromOmeXmlDoc(omeDoc);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Problem with " + mdataFile.getPath() +
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
		filename = DashboardUtils.OME_FILENAME;
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
		filename = DashboardUtils.OME_FILENAME;
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
	 * Any PI or vessel names will be anglicized.  
	 * The SOCAT version status and QC flag are not assigned.
	 * 
	 * @return
	 *		created SocatMetadata object 
	 */
	public SocatMetadata createSocatMetadata() throws IllegalArgumentException {

		// We cannot create a SocatMetadata object if there are conflicts
		if ( isConflicted() ) {
			throw new IllegalArgumentException("The Metadata contains conflicts");
		}

		DashboardConfigStore confStore;
		try {
			confStore = DashboardConfigStore.get(false);
		} catch ( Exception ex ) {
			throw new RuntimeException("Unexpected failure to get the configuration information");
		}
		SocatMetadata scMData = new SocatMetadata(confStore.getKnownMetadataTypes());
		
		scMData.setExpocode(expocode);
		scMData.setDatasetName(omeMData.getExperimentName());

		// Anglicize the vessel name for NetCDF/LAS
		String vesselName = omeMData.getVesselName();
		scMData.setVesselName(anglicizeName(vesselName));

		// Set the vessel type - could be missing
		String vesselType;
		try {
			vesselType = omeMData.getValue(OmeMetadata.PLATFORM_TYPE_STRING);
		} catch ( Exception ex ) {
			vesselType = null;
		}
		if ( (vesselType == null) || vesselType.trim().isEmpty() )
			vesselType = DashboardServerUtils.guessVesselType(expocode, vesselName);
		scMData.setVesselType(vesselType);

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
			if ( scienceGroup.length() > 0 )
				scienceGroup.append(NAMES_SEPARATOR);
			// Anglicize investigator names for NetCDF/LAS
			scienceGroup.append(anglicizeName(investigator));
		}
		scMData.setInvestigatorNames(scienceGroup.toString());

		HashSet<String> usedOrganizations = new HashSet<String>();
		StringBuffer orgGroup = new StringBuffer();
		for ( String org : omeMData.getOrganizations() ) {
			if ( (null != org) && usedOrganizations.add(org) ) {
				if ( orgGroup.length() > 0 )
					orgGroup.append(NAMES_SEPARATOR);
				// Anglicize organizations names for NetCDF/LAS
				orgGroup.append(anglicizeName(org));
			}
		}
		scMData.setOrganizationName(orgGroup.toString());

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

	// Use the Unicode code points to define these characters
	// so we know exactly what value is being used in the String
	public static final String acute = "\u00B4";
	public static final String AGrave = "\u00C0";
	public static final String AAcute = "\u00C1";
	public static final String AHat = "\u00C2";
	public static final String ATilde = "\u00C3";
	public static final String AUmlaut = "\u00C4";
	public static final String ARing = "\u00C5";
	public static final String AEMerge = "\u00C6";
	public static final String CCedilla = "\u00C7";
	public static final String EGrave = "\u00C8";
	public static final String EAcute = "\u00C9";
	public static final String EHat = "\u00CA";
	public static final String EUmlaut = "\u00CB";
	public static final String IGrave = "\u00CC"; 
	public static final String IAcute = "\u00CD";
	public static final String IHat = "\u00CE";
	public static final String IUmlaut = "\u00CF";
	public static final String DBar = "\u00D0";
	public static final String NTilde = "\u00D1";
	public static final String OGrave = "\u00D2";
	public static final String OAcute = "\u00D3";
	public static final String OHat = "\u00D4";
	public static final String OTilde = "\u00D5";
	public static final String OUmlaut = "\u00D6";
	public static final String OStroke = "\u00D8";
	public static final String UGrave = "\u00D9";
	public static final String UAcute = "\u00DA";
	public static final String UHat = "\u00DB";
	public static final String UUmlaut = "\u00DC";
	public static final String YAcute = "\u00DD";
	public static final String Thorn = "\u00DE";
	public static final String eszett = "\u00DF";
	public static final String aGrave = "\u00E0";
	public static final String aAcute = "\u00E1";
	public static final String aHat = "\u00E2";
	public static final String aTilde = "\u00E3";
	public static final String aUmlaut = "\u00E4";
	public static final String aRing = "\u00E5";
	public static final String aeMerge = "\u00E6";
	public static final String cCedilla = "\u00E7";
	public static final String eGrave = "\u00E8";
	public static final String eAcute = "\u00E9";
	public static final String eHat = "\u00EA";
	public static final String eUmlaut = "\u00EB";
	public static final String iGrave = "\u00EC"; 
	public static final String iAcute = "\u00ED";
	public static final String iHat = "\u00EE";
	public static final String iUmlaut = "\u00EF";
	public static final String dBar = "\u00F0";
	public static final String nTilde = "\u00F1";
	public static final String oGrave = "\u00F2";
	public static final String oAcute = "\u00F3";
	public static final String oHat = "\u00F4";
	public static final String oTilde = "\u00F5";
	public static final String oUmlaut = "\u00F6";
	public static final String oStroke = "\u00F8";
	public static final String uGrave = "\u00F9";
	public static final String uAcute = "\u00FA";
	public static final String uHat = "\u00FB";
	public static final String uUmlaut = "\u00FC";
	public static final String yAcute = "\u00FD";
	public static final String thorn = "\u00FE";
	public static final String yUmlaut = "\u00FF";

	private static final HashMap<Character,String> ANGLICIZE_MAP;
	static {
		ANGLICIZE_MAP = new HashMap<Character,String>();
		ANGLICIZE_MAP.put("'".charAt(0), " ");
		ANGLICIZE_MAP.put("`".charAt(0), " ");
		ANGLICIZE_MAP.put(acute.charAt(0), " ");
		ANGLICIZE_MAP.put(AGrave.charAt(0), "A");
		ANGLICIZE_MAP.put(AAcute.charAt(0), "A");
		ANGLICIZE_MAP.put(AHat.charAt(0), "A");
		ANGLICIZE_MAP.put(ATilde.charAt(0), "A");
		ANGLICIZE_MAP.put(AUmlaut.charAt(0), "Ae");
		ANGLICIZE_MAP.put(ARing.charAt(0), "Aa");
		ANGLICIZE_MAP.put(AEMerge.charAt(0), "AE");
		ANGLICIZE_MAP.put(CCedilla.charAt(0), "C");
		ANGLICIZE_MAP.put(EGrave.charAt(0), "E");
		ANGLICIZE_MAP.put(EAcute.charAt(0), "E");
		ANGLICIZE_MAP.put(EHat.charAt(0), "E");
		ANGLICIZE_MAP.put(EUmlaut.charAt(0), "E");
		ANGLICIZE_MAP.put(IGrave.charAt(0), "I");
		ANGLICIZE_MAP.put(IAcute.charAt(0), "I");
		ANGLICIZE_MAP.put(IHat.charAt(0), "I");
		ANGLICIZE_MAP.put(IUmlaut.charAt(0), "I");
		ANGLICIZE_MAP.put(DBar.charAt(0), "Th");
		ANGLICIZE_MAP.put(NTilde.charAt(0), "N");
		ANGLICIZE_MAP.put(OGrave.charAt(0), "O");
		ANGLICIZE_MAP.put(OAcute.charAt(0), "O");
		ANGLICIZE_MAP.put(OHat.charAt(0), "O");
		ANGLICIZE_MAP.put(OTilde.charAt(0), "O");
		ANGLICIZE_MAP.put(OUmlaut.charAt(0), "Oe");
		ANGLICIZE_MAP.put(OStroke.charAt(0), "O");
		ANGLICIZE_MAP.put(UGrave.charAt(0), "U");
		ANGLICIZE_MAP.put(UAcute.charAt(0), "U");
		ANGLICIZE_MAP.put(UHat.charAt(0), "U");
		ANGLICIZE_MAP.put(UUmlaut.charAt(0), "Ue");
		ANGLICIZE_MAP.put(YAcute.charAt(0), "Y");
		ANGLICIZE_MAP.put(Thorn.charAt(0), "Th");
		ANGLICIZE_MAP.put(eszett.charAt(0), "ss");
		ANGLICIZE_MAP.put(aGrave.charAt(0), "a");
		ANGLICIZE_MAP.put(aAcute.charAt(0), "a");
		ANGLICIZE_MAP.put(aHat.charAt(0), "a");
		ANGLICIZE_MAP.put(aTilde.charAt(0), "a");
		ANGLICIZE_MAP.put(aUmlaut.charAt(0), "ae");
		ANGLICIZE_MAP.put(aRing.charAt(0), "aa");
		ANGLICIZE_MAP.put(aeMerge.charAt(0), "ae");
		ANGLICIZE_MAP.put(cCedilla.charAt(0), "c");
		ANGLICIZE_MAP.put(eGrave.charAt(0), "e");
		ANGLICIZE_MAP.put(eAcute.charAt(0), "e");
		ANGLICIZE_MAP.put(eHat.charAt(0), "e");
		ANGLICIZE_MAP.put(eUmlaut.charAt(0), "e");
		ANGLICIZE_MAP.put(iGrave.charAt(0), "i"); 
		ANGLICIZE_MAP.put(iAcute.charAt(0), "i");
		ANGLICIZE_MAP.put(iHat.charAt(0), "i");
		ANGLICIZE_MAP.put(iUmlaut.charAt(0), "i");
		ANGLICIZE_MAP.put(dBar.charAt(0), "th");
		ANGLICIZE_MAP.put(nTilde.charAt(0), "n");
		ANGLICIZE_MAP.put(oGrave.charAt(0), "o");
		ANGLICIZE_MAP.put(oAcute.charAt(0), "o");
		ANGLICIZE_MAP.put(oHat.charAt(0), "o");
		ANGLICIZE_MAP.put(oTilde.charAt(0), "o");
		ANGLICIZE_MAP.put(oUmlaut.charAt(0), "oe");
		ANGLICIZE_MAP.put(oStroke.charAt(0), "oe");
		ANGLICIZE_MAP.put(uGrave.charAt(0), "u");
		ANGLICIZE_MAP.put(uAcute.charAt(0), "u");
		ANGLICIZE_MAP.put(uHat.charAt(0), "u");
		ANGLICIZE_MAP.put(uUmlaut.charAt(0), "ue");
		ANGLICIZE_MAP.put(yAcute.charAt(0), "y");
		ANGLICIZE_MAP.put(thorn.charAt(0), "th");
		ANGLICIZE_MAP.put(yUmlaut.charAt(0), "e");
	}

	/**
	 * Returns a new String with replacements for any extended characters,
	 * apostrophes, grave symbols, or acute symbols.  Extended characters
	 * may be replaced by more than one character.  Apostrophes, 
	 * grave symbols, and acute symbols are replaced by spaces.
	 * 
	 * @param name
	 * 		String to be copied; can be null
	 * @return
	 * 		copy of the String with character replacements, 
	 * 		or null if the String was null
	 */
	public static String anglicizeName(String name) {
		if ( name == null )
			return null;
		StringBuilder builder = new StringBuilder();
		for ( char letter : name.toCharArray() ) {
			String replacement = ANGLICIZE_MAP.get(letter);
			if ( replacement != null ) {
				builder.append(replacement);
			}
			else {
				builder.append(letter);
			}
		}
		return builder.toString();
	}

	/**
	 * Correctly spelled "anglicized" PI names (that are currently known).
	 * Note that these are single PI names only; each name in a multiple-name 
	 * science group needs to be checked individually.
	 */
	private static final HashMap<String,String> PI_NAME_CORRECTIONS;
	static {
		PI_NAME_CORRECTIONS = new HashMap<String,String>();
		PI_NAME_CORRECTIONS.put("Begovic, M.", "B" + eAcute + "govic, M.");
		PI_NAME_CORRECTIONS.put("Copin-Montegut, C.", "Copin-Mont" + eAcute + "gut, C.");
		PI_NAME_CORRECTIONS.put("Gonzalez-Davila, M.", "Gonz" + aAcute + "lez-D" +  aAcute + "vila, M.");
		PI_NAME_CORRECTIONS.put("Jutterstrom, S.", "Jutterstr" + oUmlaut + "m, S.");
		PI_NAME_CORRECTIONS.put("Jutterstroem, S.", "Jutterstr" + oUmlaut + "m, S.");
		PI_NAME_CORRECTIONS.put("Koertzinger, A.", "K" + oUmlaut + "rtzinger, A.");
		PI_NAME_CORRECTIONS.put("Lefevre, N.", "Lef" + eGrave + "vre, N.");
		PI_NAME_CORRECTIONS.put("Perez, F.F.", "P" + eAcute + "rez, F.F.");
		PI_NAME_CORRECTIONS.put("Rios, A.F.", "R" + iAcute + "os, A.F.");
		PI_NAME_CORRECTIONS.put("Treguer, P.", "Tr" + eAcute + "guer, P.");
	}

	/**
	 * Corrects the spelling of "anglicized" PI names where known corrections exist.
	 * If the name is not recognized, the given string is returned.
	 * Note that these are single PI names only; each name in a multiple-name 
	 * science group needs to be checked individually.
	 * 
	 * @param anglicizedName
	 * 		anglicized PI name to be corrected; can be null
	 * @return
	 * 		correctly spelled PI name, or null if the given String was null
	 */
	public static String correctInvestigatorName(String anglicizedName) {
		if ( anglicizedName == null )
			return null;
		String name = PI_NAME_CORRECTIONS.get(anglicizedName);
		if ( name == null )
			return anglicizedName;
		return name;
	}

	/**
	 * Correctly spelled "anglicized" vessel names (that are currently known). 
	 */
	private static final HashMap<String,String> VESSEL_NAME_CORRECTIONS = 
			new HashMap<String,String>();
	static {
		VESSEL_NAME_CORRECTIONS.put("Haakon Mosby", "H" + aRing + "kon Mosby");
		VESSEL_NAME_CORRECTIONS.put("Hesperides", "Hesp" + eAcute + "rides");
		VESSEL_NAME_CORRECTIONS.put("Ka imimoana", "Ka'imimoana");
		VESSEL_NAME_CORRECTIONS.put("L Astrolabe", "L'Astrolabe");
		VESSEL_NAME_CORRECTIONS.put("L Atalante", "L'Atalante");
	}

	/**
	 * Corrects the spelling of "anglicized" vessel names where known corrections exist.
	 * If the name is not recognized, the given string is returned.
	 * 
	 * @param anglicizedName
	 * 		anglicized vessel name to be corrected; can be null
	 * @return
	 * 		correctly spelled vessel name, or null if the given String was null
	 */
	public static String correctVesselName(String anglicizedName) {
		if ( anglicizedName == null )
			return null;
		String name = VESSEL_NAME_CORRECTIONS.get(anglicizedName);
		if ( name == null )
			return anglicizedName;
		return name;
	}

}
