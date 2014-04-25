/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Class for the one special metadata file per cruise that must be present,
 * has a known format, and contains user-provided values needed by the SOCAT 
 * database.  
 *  
 * @author Karl Smith
 */
public class OmeMetadata extends DashboardMetadata {

	private static final long serialVersionUID = -3905881094341061538L;

	private static final SimpleDateFormat DATE_PARSER = 
			new SimpleDateFormat("yyyyMMdd HH:mmZ");
	private static final SimpleDateFormat DATE_FORMATTER =
			new SimpleDateFormat("yyyyMMdd");

	// data values from the OME metadata 
	String cruiseName;
	String vesselName;
	ArrayList<String> investigators;
	ArrayList<String> organizations;
	Double westmostLongitude;
	Double eastmostLongitude;
	Double southmostLatitude;
	Double northmostLatitude;
	Date startDate;
	Date endDate;
	String origDataRef;

	// TODO: add more fields when they are identified in the OME XML file.

	/**
	 * Creates an empty OME metadata document; 
	 * only the standard OME filename is assigned.
	 */
	public OmeMetadata() {
		super();
		filename = OME_FILENAME;

		cruiseName = "";
		vesselName = "";
		investigators = new ArrayList<String>();
		organizations = new ArrayList<String>();
		westmostLongitude = Double.NaN;
		eastmostLongitude = Double.NaN;
		southmostLatitude = Double.NaN;
		northmostLatitude = Double.NaN;
		startDate = SocatMetadata.DATE_MISSING_VALUE;
		endDate = SocatMetadata.DATE_MISSING_VALUE;
		origDataRef = "";
	}

	/**
	 * Creates from the contents of the OME XML file specified in the 
	 * DashboardMetadata given. 
	 * 
	 * @param mdata
	 * 		OME XML file to read.  The expocode, filename, upload timestamp 
	 * 		and owner are copied from this object, and the file specified is 
	 * 		read to populate the fields of this object.
	 * @throws IllegalArgumentException
	 * 		if mdata is null, or
	 * 		if the information in the DashboardMetadata is invalid, or
	 * 		if the contents of the metadata document are not valid
	 */
	public OmeMetadata(DashboardMetadata mdata) {
		// Initialize to an empty OME metadata document with the standard OME filename
		this();

		if ( mdata == null )
			throw new IllegalArgumentException("No metadata file given");

		// Copy the expocode, uploadTimestamp, and owner 
		// from the given DashboardMetadata object
		expocode = mdata.getExpocode();
		uploadTimestamp = mdata.getUploadTimestamp();
		owner = mdata.getOwner();

		// Read the metadata document as an XML file
		MetadataFileHandler mdataHandler;
		try {
			mdataHandler = DashboardDataStore.get().getMetadataFileHandler();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the metadata handler");
		}
		File mdataFile = mdataHandler.getMetadataFile(expocode, mdata.getFilename());
		Document omeDoc;
		try {
			omeDoc = (new SAXBuilder()).build(mdataFile);
		} catch (JDOMException | IOException ex ) {
			throw new IllegalArgumentException("Problems interpreting " +
					"the OME XML contents in " + mdataFile.getPath() + 
					":\n    " + ex.getMessage());
		}

		// Verify expocode and assign from the OME XML contents
		try {
			assignFromOmeXmlDoc(omeDoc);
		} catch ( IllegalArgumentException ex ) {
			throw new IllegalArgumentException(
					ex.getMessage() + " in " + mdataFile.getPath(), ex);
		}
	}

	/**
	 * Creates from a line of tab-separated values from Benjamin's 
	 * metadata spreadsheet TSV table.  The owner of the metadata 
	 * is left empty so anyone can take possession of it.
	 * 
	 * @param colNames 
	 * 		array of column names for the metadata in dataLine
	 * @param metadataLine 
	 * 		metadata for a cruise as a string of tab-separated values
	 * @param timestamp
	 * 		upload time-stamp string to use 
	 * @throws IllegalArgumentException
	 * 		if the number of metadata values does not match the number 
	 * 		of column names or if the expocode is not defined
	 */
	public OmeMetadata(String[] colNames, String metadataLine, String timestamp) {
		// Initialize to an empty OME metadata document 
		// with the standard OME filename and the given timestamp
		this();
		uploadTimestamp = timestamp;
		// Leave the owner blank so anyone can take possession of it

		// Parse the metadata string - split on tabs
		String[] metaVals = metadataLine.split("\\t", -1);
		if ( metaVals.length != colNames.length ) {
			throw new IllegalArgumentException("Number of metadata values (" + 
					Integer.toString(metaVals.length) + 
					") does not match the number of column names (" + 
					Integer.toString(colNames.length) + ")");
		}
		// Interpret the data based on the column names
		for (int k = 0; k < metaVals.length; k++) {
			metaVals[k] = metaVals[k].trim();
			// Treat blank entries the same as "NaN" entries
			if ( metaVals[k].isEmpty() )
				metaVals[k] = "NaN";
			if ( "Expocode created".equals(colNames[k]) ||
				 "Expocode".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) )
					expocode = metaVals[k];
			}
			else if ( "Cruise Label".equals(colNames[k]) ||
					  "Cruise".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) )
					cruiseName = metaVals[k];
			}
			else if ( "ship/platform".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) )
					vesselName = metaVals[k];
			}
			else if ( "PI".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					investigators.add(metaVals[k]);
					organizations.add("");
				}
			}
			else if ( "PI_2".equals(colNames[k]) ||
					  "PI 2".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					investigators.add(metaVals[k]);
					organizations.add("");
				}
			}
			else if ( "PI_3".equals(colNames[k]) ||
					  "PI 3".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					investigators.add(metaVals[k]);
					organizations.add("");
				}
			}
			else if ( "doi".equals(colNames[k]) ||
					  "orig_doi".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) )
					origDataRef = metaVals[k];
			}
			// otherwise ignore the data in this column
			// longitude/latitude/time limits are derived from the data
		}
		if ( expocode.isEmpty() )
			throw new IllegalArgumentException("Expocode is not given");
	}

	/**
	 * Validates that the expocode given for this metadata object matches the 
	 * expocode given in the given OME XML document, then assigns the fields
	 * in this object from this document.
	 * 
	 * @param omeDoc
	 * 		OME XML Document to use
	 */
	public void assignFromOmeXmlDoc(Document omeDoc) {
		/*
		 * Example contents of the OME XML file:
		 * ------------------------------------------------
		 * <?xml version="1.0" encoding="UTF-8"?>
		 * <x_tags>
		 *   ...
		 *   <Investigator>
 		 *     <Name>Last, First M.</Name>
		 *     <Organization>...</Organization>
		 *     <Address>...</Address>
		 *     <Phone>...</Phone>
		 *     <Email>...</Email>
		 *   </Investigator>
		 *   ... (more Investigator elements) ...
		 *   <Cruise_Info>
		 *     <Experiment>
		 *       <Experiment_Name>SH1201</Experiment_Name>
		 *       <Experiment_Type>VOS Lines</Experiment_Type>
		 *       <Cruise>
		 *         <Cruise_ID>332220120220</Cruise_ID>
		 *         <Geographical_Coverage>
		 *           <Geographical_Region>North American West Coast</Geographical_Region>
		 *           <Bounds>
		 *             <Westernmost_Longitude>-125.702</Westernmost_Longitude>
		 *             <Easternmost_Longitude>-122.978</Easternmost_Longitude>
		 *             <Northernmost_Latitude>49.027</Northernmost_Latitude>
		 *             <Southernmost_Latitude>48.183</Southernmost_Latitude>
		 *           </Bounds>
		 *         </Geographical_Coverage>
		 *         <Temporal_Coverage>
		 *           <Start_Date>20120220</Start_Date>
		 *           <End_Date>20120229</End_Date>
		 *         </Temporal_Coverage>
		 *       </Cruise>
		 *     </Experiment>
		 *     <Vessel>
		 *       <Vessel_Name>Bell M. Shimada</Vessel_Name>
		 *       <Vessel_ID>3322</Vessel_ID>
		 *       <Vessel_Owner>NOAA</Vessel_Owner>
		 *     </Vessel>
		 *   </Cruise_Info>
		 *   <Variables_Info>
		 *     ... 
		 *   </Variables_Info>
		 *   <Method_Description>
		 *     ...
		 *   </Method_Description>
		 *   <Citation>...</Citation>
		 *   <Data_Set_Link>
		 *     <URL>www.pmel.noaa.gov/co2/</URL>
		 *     <Label>PMEL Underway pCO2 data</Label>
		 *   </Data_Set_Link>
		 *   <Data_Link>
		 *     <URL>SH1201.csv</URL>
		 *   </Data_Link>
		 *   <form_type>underway</form_type>
		 * </x_tags>
		 * ------------------------------------------------
		 */

		Element rootElem = omeDoc.getRootElement();
		// Validate the expocode from <Cruise_Info><Experiment><Cruise><Cruise_ID>
		Element cruiseInfoElem = rootElem.getChild("Cruise_Info");
		if ( cruiseInfoElem == null )
			throw new IllegalArgumentException(
					"No Cruise_Info element in the OME XML contents");
		Element experimentElem = cruiseInfoElem.getChild("Experiment");
		if ( experimentElem == null )
			throw new IllegalArgumentException(
					"No Cruise_Info->Experiment element in the OME XML contents");
		Element cruiseElem = experimentElem.getChild("Cruise");
		if ( cruiseElem == null )
			throw new IllegalArgumentException(
					"No Cruise_Info->Experiment->Cruise " +
					"element in the OME XML contents");
		String name = cruiseElem.getChildTextTrim("Cruise_ID"); 
		if ( name == null )
			throw new IllegalArgumentException(
					"No Cruise_Info->Experiment->Cruise->Cruise_ID " +
					"element in the OME XML contents");
		if ( ! expocode.equals(name.toUpperCase()) )
			throw new IllegalArgumentException("Expocode of cruise (" + 
					expocode + ") does not match that the Cruise ID in " +
					"the OME document (" + name + ")");

		// Get the cruise name from <Cruise_Info><Experiment><Experiment_Name>
		name = experimentElem.getChildTextTrim("Experiment_Name");
		if ( name == null )
			throw new IllegalArgumentException(
					"No Cruise_Info->Experiment->Experiment_Name " +
					"element in the OME XML contents");
		if ( name.isEmpty() )
			throw new IllegalArgumentException(
					"No cruise name given in the OME document");
		cruiseName = name;

		// Get the vessel name from <Cruise_Info><Vessel><Vessel_Name>
		Element childElem = cruiseInfoElem.getChild("Vessel");
		if ( childElem == null )
			throw new IllegalArgumentException(
					"No Cruise_Info->Vessel element in the OME XML contents");
		name = childElem.getChildTextTrim("Vessel_Name");
		if ( name == null )
			throw new IllegalArgumentException(
					"No Cruise_Info->Vessel->Vessel_Name " +
					"element in the OME XML contents");
		if ( name.isEmpty() ) 
			throw new IllegalArgumentException(
					"No ship/vessel name given in the OME document");
		vesselName = name;

		// Get the science group from <Investigator><Name> and 
		// the organization from <Investigator><Organization>
		investigators.clear();
		organizations.clear();
		boolean investigatorFound = false;
		for ( Element investElem : rootElem.getChildren("Investigator") ) {
			investigatorFound = true;
			name = investElem.getChildTextTrim("Name");
			if ( name == null )
				throw new IllegalArgumentException("No Name element for " +
						"an Investigator element in the OME XML contents");
			if ( name.isEmpty() )
				throw new IllegalArgumentException("No name given for " +
						"an investigator in the OME document");
			investigators.add(name);
			// Okay to have no organization given, but keep the lists matched
			name = investElem.getChildTextTrim("Organization");
			if ( name == null )
				name = "";
			organizations.add(name);
		}
		if ( ! investigatorFound )
			throw new IllegalArgumentException(
					"No Investigator element in the OME XML contents");
		if ( investigators.isEmpty() )
			throw new IllegalArgumentException(
					"No investigator names given in the OME document");

		// Okay to have these missing; may be (re)assigned from the data
		westmostLongitude = Double.NaN;
		eastmostLongitude = Double.NaN;
		southmostLatitude = Double.NaN;
		northmostLatitude = Double.NaN;
		childElem = cruiseElem.getChild("Geographical_Coverage");
		if ( childElem != null ) {
			childElem = childElem.getChild("Bounds");
			if ( childElem != null ) {
				name = childElem.getChildTextTrim("Westernmost_Longitude");
				if ( (name != null) && ! name.isEmpty() ) {
					try {
						westmostLongitude = Double.valueOf(name);
					} catch ( NumberFormatException ex ) {
						throw new IllegalArgumentException(
								"Invalid value for <Westernmost_Longitude>");
					}
				}
				name = childElem.getChildTextTrim("Easternmost_Longitude");
				if ( (name != null) && ! name.isEmpty() ) {
					try {
						eastmostLongitude = Double.valueOf(name);
					} catch ( NumberFormatException ex ) {
						throw new IllegalArgumentException(
								"Invalid value for <Easternmost_Longitude>");
					}
				}
				name = childElem.getChildTextTrim("Southernmost_Latitude");
				if ( (name != null) && ! name.isEmpty() ) {
					try {
						southmostLatitude = Double.valueOf(name);
					} catch ( NumberFormatException ex ) {
						throw new IllegalArgumentException(
								"Invalid value for <Southernmost_Latitude>");
					}
				}
				name = childElem.getChildTextTrim("Northernmost_Latitude");
				if ( (name != null) && ! name.isEmpty() ) {
					try {
						northmostLatitude = Double.valueOf(name);
					} catch ( NumberFormatException ex ) {
						throw new IllegalArgumentException(
								"Invalid value for <Northernmost_Latitude>");
					}
				}
			}
		}

		// Okay to have these missing; may be (re)assigned from the data
		startDate = SocatMetadata.DATE_MISSING_VALUE;
		endDate = SocatMetadata.DATE_MISSING_VALUE;
		childElem = cruiseElem.getChild("Temporal_Coverage");
		if ( childElem != null ) {
			name = childElem.getChildTextTrim("Start_Date");
			if ( (name != null) && ! name.isEmpty() ) {
				try {
					// Just the UTC date, so set the time to 00:00
					startDate = DATE_PARSER.parse(name + " 00:00+0000");
				} catch ( ParseException ex ) {
					throw new IllegalArgumentException(
							"Invalid value for <Start_Date>");
				}
			}
			name = childElem.getChildTextTrim("End_Date");
			if ( (name != null) && ! name.isEmpty() ) {
				try {
					// Just the UTC date, so set the time to 23:59
					endDate = DATE_PARSER.parse(name + " 23:59+0000");
				} catch ( ParseException ex ) {
					throw new IllegalArgumentException(
							"Invalid value for <End_Date>");
				}
			}
		}

		// Get the original data http reference from <Data_Set_Link><URL> and <Data_Link><URL>
		// Note that it is very likely there is not an original data reference
		// TODO: this might need adjusting
		origDataRef = "";
		childElem = rootElem.getChild("Data_Set_Link");
		if ( childElem != null ) {
			name = childElem.getChildTextTrim("URL");
			if ( name != null )
				origDataRef = name;
		}
		childElem = rootElem.getChild("Data_Link");
		if ( childElem != null ) {
			name = childElem.getChildTextTrim("URL");
			if ( name != null ) {
				if ( origDataRef.isEmpty() || origDataRef.endsWith("/") )  
					origDataRef += name;
				else 
					origDataRef += "/" + name;
			}
		}
	}

	/**
	 * Generated an pseudo-OME XML document that contains the contents
	 * of the fields read by {@link #assignFromOmeXmlDoc(Document)}.
	 * Fields not read by that method are not saved in the document
	 * produced by this method.
	 * 
	 * @return
	 * 		the generated pseudo-OME XML document
	 */
	public Document createMinimalOmeXmlDoc() {
		/*
		 * Example contents of the OME XML file:
		 * ------------------------------------------------
		 * <?xml version="1.0" encoding="UTF-8"?>
		 * <x_tags>
		 *   ...
		 *   <Investigator>
 		 *     <Name>Last, First M.</Name>
		 *     <Organization>...</Organization>
		 *     <Address>...</Address>
		 *     <Phone>...</Phone>
		 *     <Email>...</Email>
		 *   </Investigator>
		 *   ... (more Investigator elements) ...
		 *   <Cruise_Info>
		 *     <Experiment>
		 *       <Experiment_Name>SH1201</Experiment_Name>
		 *       <Experiment_Type>VOS Lines</Experiment_Type>
		 *       <Cruise>
		 *         <Cruise_ID>332220120220</Cruise_ID>
		 *         <Geographical_Coverage>
		 *           <Geographical_Region>North American West Coast</Geographical_Region>
		 *           <Bounds>
		 *             <Westernmost_Longitude>-125.702</Westernmost_Longitude>
		 *             <Easternmost_Longitude>-122.978</Easternmost_Longitude>
		 *             <Northernmost_Latitude>49.027</Northernmost_Latitude>
		 *             <Southernmost_Latitude>48.183</Southernmost_Latitude>
		 *           </Bounds>
		 *         </Geographical_Coverage>
		 *         <Temporal_Coverage>
		 *           <Start_Date>20120220</Start_Date>
		 *           <End_Date>20120229</End_Date>
		 *         </Temporal_Coverage>
		 *       </Cruise>
		 *     </Experiment>
		 *     <Vessel>
		 *       <Vessel_Name>Bell M. Shimada</Vessel_Name>
		 *       <Vessel_ID>3322</Vessel_ID>
		 *       <Vessel_Owner>NOAA</Vessel_Owner>
		 *     </Vessel>
		 *   </Cruise_Info>
		 *   <Variables_Info>
		 *     ... 
		 *   </Variables_Info>
		 *   <Method_Description>
		 *     ...
		 *   </Method_Description>
		 *   <Citation>...</Citation>
		 *   <Data_Set_Link>
		 *     <URL>www.pmel.noaa.gov/co2/</URL>
		 *     <Label>PMEL Underway pCO2 data</Label>
		 *   </Data_Set_Link>
		 *   <Data_Link>
		 *     <URL>SH1201.csv</URL>
		 *   </Data_Link>
		 *   <form_type>underway</form_type>
		 * </x_tags>
		 * ------------------------------------------------
		 */

		Element cruiseElem = new Element("Cruise");

		// expocode goes in <Cruise_Info><Experiment><Cruise><Cruise_ID>
		Element cruiseIdElem = new Element("Cruise_ID");
		cruiseIdElem.setText(expocode);
		cruiseElem.addContent(cruiseIdElem);

		// Bounds on longitude and latitude go in <Cruise_Info><Experiment><Cruise><Geographical_Coverage><Bounds>
		Element boundsElem = new Element("Bounds");
		boolean somethingAdded = false;
		if ( ! westmostLongitude.isNaN() ) { 
			Element westElem = new Element("Westernmost_Longitude");
			westElem.setText(String.format("%#.3f", westmostLongitude));
			boundsElem.addContent(westElem);
			somethingAdded = true;
		}
		if ( ! eastmostLongitude.isNaN() ) { 
			Element eastElem = new Element("Easternmost_Longitude");
			eastElem.setText(String.format("%#.3f", eastmostLongitude));
			boundsElem.addContent(eastElem);
			somethingAdded = true;
		}
		if ( ! southmostLatitude.isNaN() ) { 
			Element eastElem = new Element("Southernmost_Latitude");
			eastElem.setText(String.format("%#.3f", southmostLatitude));
			boundsElem.addContent(eastElem);
			somethingAdded = true;
		}
		if ( ! northmostLatitude.isNaN() ) { 
			Element eastElem = new Element("Northernmost_Latitude");
			eastElem.setText(String.format("%#.3f", northmostLatitude));
			boundsElem.addContent(eastElem);
			somethingAdded = true;
		}
		if ( somethingAdded ) {
			Element geoElem = new Element("Geographical_Coverage");
			geoElem.addContent(boundsElem);
			cruiseElem.addContent(geoElem);
		}

		// Start and end date go in <Cruise_Info><Experiment><Cruise><Temporal_Coverage>
		Element timeElem = new Element("Temporal_Coverage");
		somethingAdded = false;
		if ( ! startDate.equals(SocatMetadata.DATE_MISSING_VALUE) ) {
			Element startElem = new Element("Start_Date");
			startElem.addContent(DATE_FORMATTER.format(startDate));
			timeElem.addContent(startElem);
			somethingAdded = true;
		}
		if ( ! endDate.equals(SocatMetadata.DATE_MISSING_VALUE) ) {
			Element endElem = new Element("End_Date");
			endElem.addContent(DATE_FORMATTER.format(endDate));
			timeElem.addContent(endElem);
			somethingAdded = true;
		}
		if ( somethingAdded ) {
			cruiseElem.addContent(timeElem);
		}

		Element experimentElem = new Element("Experiment");
		experimentElem.addContent(cruiseElem);

		// cruiseName goes in <Cruise_Info><Experiment><Experiment_Name>
		Element experimentNameElem = new Element("Experiment_Name");
		experimentNameElem.setText(cruiseName);
		experimentElem.addContent(experimentNameElem);

		Element cruiseInfoElem = new Element("Cruise_Info");
		cruiseInfoElem.addContent(experimentElem);

		// vesselName goes in <Cruise_Info><Vessel><Vessel_Name>
		Element vesselNameElem = new Element("Vessel_Name");
		vesselNameElem.setText(vesselName);
		Element vesselElem = new Element("Vessel");
		vesselElem.addContent(vesselNameElem);
		cruiseInfoElem.addContent(vesselElem);
		
		Element rootElem = new Element("x_tags");
		rootElem.addContent(cruiseInfoElem);

		// names in investigators and organizations go in <Name> and <Organization>
		// under separate <Investigator> elements
		for (int k = 0; k < investigators.size(); k++) {
			String piName = investigators.get(k);
			String orgName;
			try {
				orgName = organizations.get(k);
			} catch ( IndexOutOfBoundsException ex) {
				orgName = "";
			}

			Element nameElem = new Element("Name");
			nameElem.setText(piName);
			Element orgElem = new Element("Organization");
			orgElem.setText(orgName);

			Element investigatorElem = new Element("Investigator");
			investigatorElem.addContent(nameElem);
			investigatorElem.addContent(orgElem);

			rootElem.addContent(investigatorElem);
		}

		// Put origDataRef, if there is one, in <Data_Link><URL>
		// TODO: this might need adjusting
		if ( ! origDataRef.isEmpty() ) {
			Element urlElem = new Element("URL");
			urlElem.setText(origDataRef);
			Element dataLinkElem = new Element("Data_Link");
			dataLinkElem.addContent(urlElem);

			rootElem.addContent(dataLinkElem);
		}

		// Return the document created from the root element
		return new Document(rootElem);
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
	public SocatMetadata createSocatMetadata(Double socatVersion, 
							Set<String> addlDocs, String qcFlag) {
		SocatMetadata scMData = new SocatMetadata();
		scMData.setExpocode(expocode);
		scMData.setCruiseName(cruiseName);
		scMData.setVesselName(vesselName);
		scMData.setWestmostLongitude(westmostLongitude);
		scMData.setEastmostLongitude(eastmostLongitude);
		scMData.setSouthmostLatitude(southmostLatitude);
		scMData.setNorthmostLatitude(northmostLatitude);
		scMData.setBeginTime(startDate);
		scMData.setEndTime(endDate);
		scMData.setOrigDataRef(origDataRef);
		// PIs as a single string
		String scienceGroup = "";
		for ( String piName : investigators ) {
			if ( scienceGroup.isEmpty() )
				scienceGroup = piName;
			else
				scienceGroup += SocatMetadata.NAMES_SEPARATOR + piName;
		}
		scMData.setScienceGroup(scienceGroup);
		// Organizations as a single string
		// Order unique organizations as given; no longer one-to-one with PIs
		String orgGroup = "";
		for ( String orgName : new LinkedHashSet<String>(organizations) ) {
			if ( orgGroup.isEmpty() )
				orgGroup = orgName;
			else
				orgGroup += SocatMetadata.NAMES_SEPARATOR + orgName;
		}
		scMData.setOrganization(orgGroup);

		// TODO: add and initialize more fields when they are identified in the OME XML file

		// Add names of any ancillary documents
		String docsString = "";
		for ( String docName : addlDocs ) {
			if ( docsString.isEmpty() )
				docsString = docName;
			else
				docsString += SocatMetadata.NAMES_SEPARATOR + docName;
		}
		scMData.setAddlDocs(docsString);

		// Add SOCAT version and QC flag
		scMData.setSocatVersion(socatVersion);
		scMData.setQcFlag(qcFlag);

		return scMData;
	}

	/**
	 * @return 
	 * 		the cruise name; never null but could be empty
	 */
	public String getCruiseName() {
		return cruiseName;
	}

	/**
	 * @param cruiseName 
	 * 		the cruise name to set;
	 * 		if null, an empty string is assigned
	 */
	public void setCruiseName(String cruiseName) {
		if ( cruiseName == null )
			this.cruiseName = "";
		else
			this.cruiseName = cruiseName;
	}

	/**
	 * @return 
	 * 		the vessel name; never null but could be empty
	 */
	public String getVesselName() {
		return vesselName;
	}

	/**
	 * @param vesselName 
	 * 		the vessel name to set;
	 * 		if null, an empty string is assigned
	 */
	public void setVesselName(String vesselName) {
		if ( vesselName == null )
			this.vesselName = "";
		else
			this.vesselName = vesselName;
	}

	/**
	 * @return 
	 * 		the list of PIs; never null but could be empty.
	 * 		The actual list in this instance is returned.
	 */
	public ArrayList<String> getInvestigators() {
		return investigators;
	}

	/**
	 * @param investigators 
	 * 		the list of PIs to set;
	 * 		if null, an empty list is assigned
	 */
	public void setInvestigators(ArrayList<String> investigators) {
		this.investigators.clear();
		if ( investigators != null )
			this.investigators.addAll(investigators);
	}

	/**
	 * @return 
	 * 		the list of organizations/institutions;
	 * 		never null but could be empty if not assigned.
	 * 		The actual list in this instance is returned.
	 */
	public ArrayList<String> getOrganizations() {
		return organizations;
	}

	/**
	 * @param organizations 
	 * 		the list of organizations/institutions to set;
	 * 		if null, an empty list is assigned.
	 */
	public void setOrganizations(ArrayList<String> organizations) {
		this.organizations.clear();
		if ( organizations != null )
			this.organizations.addAll(organizations);
	}

	/**
	 * @return
	 * 		the west-most longitude for the cruise;
	 * 		never null could be Double.NaN if not assigned.
	 */
	public Double getWestmostLongitude() {
		return westmostLongitude;
	}

	/**
	 * @param westmostLongitude 
	 * 		the west-most longitude to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setWestmostLongitude(Double westmostLongitude) {
		if ( westmostLongitude == null )
			this.westmostLongitude = Double.NaN;
		else 
			this.westmostLongitude = westmostLongitude;
	}

	/**
	 * @return
	 * 		the east-most longitude for the cruise;
	 * 		never null but could be Double.NaN if not assigned.
	 */
	public Double getEastmostLongitude() {
		return eastmostLongitude;
	}

	/**
	 * @param eastmostLongitude 
	 * 		the east-most longitude to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setEastmostLongitude(Double eastmostLongitude) {
		if ( eastmostLongitude == null )
			this.eastmostLongitude = Double.NaN;
		else
			this.eastmostLongitude = eastmostLongitude;
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be Double.NaN if not assigned.
	 */
	public Double getSouthmostLatitude() {
		return southmostLatitude;
	}

	/**
	 * @param southmostLatitude 
	 * 		the south-most latitude to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setSouthmostLatitude(Double southmostLatitude) {
		if ( southmostLatitude == null )
			this.southmostLatitude = Double.NaN;
		else
			this.southmostLatitude = southmostLatitude;
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be Double.NaN if not assigned.
	 */
	public Double getNorthmostLatitude() {
		return northmostLatitude;
	}

	/**
	 * @param northmostLatitude 
	 * 		the north-most latitude to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setNorthmostLatitude(Double northmostLatitude) {
		if ( northmostLatitude == null )
			this.northmostLatitude = Double.NaN;
		else
			this.northmostLatitude = northmostLatitude;
	}

	/**
	 * @return
	 * 		the start date for the cruise;
	 * 		never null but could be {@link SocatMetadata#DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate 
	 * 		the start date for the cruise to set;
	 * 		if null, {@link SocatMetadata#DATE_MISSING_VALUE} is assigned
	 */
	public void setStartDate(Date startDate) {
		if ( startDate == null )
			this.startDate = SocatMetadata.DATE_MISSING_VALUE;
		else 
			this.startDate = startDate;
	}

	/**
	 * @return
	 * 		the ending date for the cruise;
	 * 		never null but could be {@link SocatMetadata#DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate 
	 * 		the ending date for the cruise to set;
	 * 		if null, {@link SocatMetadata#DATE_MISSING_VALUE} is assigned
	 */
	public void setEndDate(Date endDate) {
		if ( endDate == null )
			this.endDate = SocatMetadata.DATE_MISSING_VALUE;
		else 
			this.endDate = endDate;
	}

	/**
	 * @return 
	 * 		the DOI of the original cruise data; never null but could be empty
	 */
	public String getOrigDataRef() {
		return origDataRef;
	}

	/**
	 * @param origDataRef 
	 * 		the DOI of the original cruise data to set;
	 * 		if null, an empty string is assigned
	 */
	public void setOrigDataRef(String origDataRef) {
		if ( origDataRef == null )
			this.origDataRef = "";
		else
			this.origDataRef = origDataRef;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + cruiseName.hashCode();
		result = result * prime + vesselName.hashCode();
		result = result * prime + investigators.hashCode();
		result = result * prime + organizations.hashCode();
		result = result * prime + startDate.hashCode();
		result = result * prime + endDate.hashCode();
		result = result * prime + origDataRef.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! ( obj instanceof OmeMetadata ) )
			return false;
		OmeMetadata other = (OmeMetadata) obj;

		if ( ! super.equals(other) )
			return false;
		// Date comparisons
		if ( ! startDate.equals(other.startDate) )
			return false;
		if ( ! endDate.equals(other.endDate) )
			return false;
		// String comparisons
		if ( ! cruiseName.equals(other.cruiseName) )
			return false;
		if ( ! vesselName.equals(other.vesselName) )
			return false;
		if ( ! origDataRef.equals(other.origDataRef) )
			return false;
		// ArrayList<String> comparisons
		if ( ! investigators.equals(other.investigators) )
			return false;
		if ( ! organizations.equals(other.organizations) )
			return false;
		// Double comparisons
		if ( ! DashboardUtils.closeTo(westmostLongitude, 
				other.westmostLongitude, 0.0, 1.0E-4) )
			return false;
		if ( ! DashboardUtils.closeTo(eastmostLongitude, 
				other.eastmostLongitude, 0.0, 1.0E-4) )
			return false;
		if ( ! DashboardUtils.closeTo(southmostLatitude, 
				other.southmostLatitude, 0.0, 1.0E-4) )
			return false;
		if ( ! DashboardUtils.closeTo(northmostLatitude, 
				other.northmostLatitude, 0.0, 1.0E-4) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "OmeMetadata[ expocode=" + expocode + 
				",\n    cruiseName=" + cruiseName + 
				",\n    vesselName=" + vesselName + 
				",\n    investigators=" + investigators.toString() +
				",\n    organizations=" + organizations.toString() + 
				",\n    westmostLongitude=" + westmostLongitude.toString() + 
				",\n    eastmostLongitude=" + eastmostLongitude.toString() + 
				",\n    southmostLatitude=" + southmostLatitude.toString() + 
				",\n    northmostLatitude=" + northmostLatitude.toString() + 
				",\n    startDate=" + startDate.toString() + 
				",\n    endDate=" + endDate.toString() + 
				",\n    origDataRef=" + origDataRef + 
				",\n    filename=" + filename + 
				",\n    uploadTimestamp=" + uploadTimestamp + 
				",\n    owner=" + owner + 
				",\n    selected=" + selected + 
				" ]";
	}

}
