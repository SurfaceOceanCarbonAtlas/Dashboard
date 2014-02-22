/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Class for the one special metadata file per cruise that must be present,
 * has a known format, and contains values needed by the SOCAT database.
 * Ideally this would extend both DashboardMetadata and SocatMetadata. 
 *  
 * @author Karl Smith
 */
public class OmeMetadata extends DashboardMetadata {

	private static final long serialVersionUID = 3030376135083874816L;

	public static final String OME_PIS_SEPARATOR = "; ";
	public static final String OME_HREFS_SEPARATOR = " ; ";

	// The following come from the OME metadata 
	String cruiseName;
	String vesselName;
	String scienceGroup;
	String origDataRef;

	// TODO: add more fields when they are identified in the OME XML file.

	// This may be the reference to this document to be archived
	String metadataHRef;

	/**
	 * Creates an empty OME metadata document
	 */
	public OmeMetadata() {
		super();
		cruiseName = "";
		vesselName = "";
		scienceGroup = "";
		origDataRef = "";
		metadataHRef = "";
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
		// Initialize to an empty OME metadata document
		this();

		if ( mdata == null )
			throw new IllegalArgumentException("No metadata file given");
		// Copy the expocode, uploadTimestamp, and owner 
		// from the given DashboardMetadata object
		expocode = mdata.getExpocode();
		uploadTimestamp = mdata.getUploadTimestamp();
		owner = mdata.getOwner();

		// The filename, however, is always the OME filename
		filename = OME_FILENAME;

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

		// Verify expocode, and assign cruiseName, vesselName, scienceGroup, 
		// and origDataRef from the OME XML contents
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
		this();

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
					if ( scienceGroup.isEmpty() )
						scienceGroup = metaVals[k];
					else
						scienceGroup += OME_PIS_SEPARATOR + metaVals[k];
				}
			}
			else if ( "PI_2".equals(colNames[k]) ||
					  "PI 2".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( scienceGroup.isEmpty() )
						scienceGroup = metaVals[k];
					else
						scienceGroup += OME_PIS_SEPARATOR + metaVals[k];
				}
			}
			else if ( "PI_3".equals(colNames[k]) ||
					  "PI 3".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( scienceGroup.isEmpty() )
						scienceGroup = metaVals[k];
					else
						scienceGroup += OME_PIS_SEPARATOR + metaVals[k];
				}
			}
			else if ( "doi".equals(colNames[k]) ||
					  "orig_doi".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) )
					origDataRef = metaVals[k];
			}
			else if ( "metadata_hyperlink".equals(colNames[k]) ||
					  "Metadata".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( metadataHRef.isEmpty() )
						metadataHRef = metaVals[k];
					else
						metadataHRef += OME_HREFS_SEPARATOR + metaVals[k];
				}
			}
			else if ( "Metadata_hyperlink_2".equals(colNames[k]) ||
					  "Metadata 2".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( metadataHRef.isEmpty() )
						metadataHRef = metaVals[k];
					else
						metadataHRef += OME_HREFS_SEPARATOR + metaVals[k];
				}
			}
			else if ( "Metadata_hyperlink_3".equals(colNames[k]) ||
					  "Metadata 3".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( metadataHRef.isEmpty() )
						metadataHRef = metaVals[k];
					else
						metadataHRef += OME_HREFS_SEPARATOR + metaVals[k];
				}
			}
			// otherwise ignore the data in this column
		}
		if ( expocode.isEmpty() )
			throw new IllegalArgumentException("Expocode is not given");
		// Assign the standard OME "upload filename"
		filename = OME_FILENAME;  
		// Use the current server time for the upload timestamp
		uploadTimestamp = timestamp;
		// Leave the owner blank so anyone can take possession of it
	}

	/**
	 * Validates that the expocode given for this metadata object matches the 
	 * expocode given in the given OME XML document, then assigns cruiseName, 
	 * vesselName, scienceGroup, and origDataRef from this document.  The 
	 * metadataHRef, socatDOI, socatHRef, and cruiseFlag are not modified
	 * by this method.
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
 		 *    <Name>Last, First M.</Name>
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
		Element childElem = experimentElem.getChild("Cruise");
		if ( childElem == null )
			throw new IllegalArgumentException(
					"No Cruise_Info->Experiment->Cruise " +
					"element in the OME XML contents");
		String name = childElem.getChildTextTrim("Cruise_ID"); 
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
		childElem = cruiseInfoElem.getChild("Vessel");
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

		// Get the science group from <Investigator><Name>
		scienceGroup = "";
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
			if ( scienceGroup.isEmpty() )
				scienceGroup = name;
			else
				scienceGroup += OME_PIS_SEPARATOR + name;
		}
		if ( ! investigatorFound )
			throw new IllegalArgumentException(
					"No Investigator element in the OME XML contents");
		if ( scienceGroup.isEmpty() )
			throw new IllegalArgumentException(
					"No investigator names given in the OME document");

		// Get the original data http reference from <Data_Set_Link><URL> and <Data_Link><URL>
		// Note that it is very likely there is not an original data reference
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
	 * Fields not read by that methos are not saved in the document
	 * produced by this method.
	 * <b>
	 * This is intended to be used only for generating pseudo-OME XML 
	 * metadata documents from Benjamin's metadata spreadsheet TSV table. 
	 * </b>
	 * @return
	 * 		the generated pseudo-OME XML document
	 */
	public Document createMinimalOmeXmlDoc() {
		// expocode goes in <Cruise_Info><Experiment><Cruise><Cruise_ID>
		Element cruiseIdElem = new Element("Cruise_ID");
		cruiseIdElem.setText(expocode);
		Element cruiseElem = new Element("Cruise");
		cruiseElem.addContent(cruiseIdElem);

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

		// names in scienceGroup go in separate <Investigator><Name>
		String[] piNames = scienceGroup.split(OME_PIS_SEPARATOR);
		for ( String name : piNames ) {
			Element nameElem = new Element("Name");
			nameElem.setText(name);
			Element investigatorElem = new Element("Investigator");
			investigatorElem.addContent(nameElem);

			rootElem.addContent(investigatorElem);
		}

		// Put origDataRef, if there is one, in <Data_Link><URL>
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
	 * Save the pseudo-OME XML document (created by 
	 * {@link #createMinimalOmeXmlDoc()}) as the document file for this 
	 * metadata.  The parent directory for this file is expected to exist 
	 * and this will overwrite any existing metadata file.
	 * <b>
	 * This is intended to be used only for generating pseudo-OME XML 
	 * metadata documents from Benjamin's metadata spreadsheet TSV table. 
	 * </b>
	 * @throws IOException
	 * 		if opening for writing, or writing to, the metadata document 
	 * 		file generates one
	 * @throws IllegalArgumentException
	 * 		if the expocode or uploadFilename in this object is invalid
	 */
	public void saveAsMinimalOmeXmlDoc() 
							throws IOException, IllegalArgumentException {
		// Get the metadata document file
		MetadataFileHandler mdataHandler;
		try {
			mdataHandler = DashboardDataStore.get().getMetadataFileHandler();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the metadata handler");
		}
		File mdataFile = mdataHandler.getMetadataFile(expocode, filename);

		// Generate the pseudo-OME XML document
		Document omeDoc = createMinimalOmeXmlDoc();

		// Save the XML document to the metadata document file
		FileOutputStream out = new FileOutputStream(mdataFile);
		try {
			(new XMLOutputter(Format.getPrettyFormat())).output(omeDoc, out);
		} finally {
			out.close();
		}
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
	 * 		the science group; never null but could be empty
	 */
	public String getScienceGroup() {
		return scienceGroup;
	}

	/**
	 * @param scienceGroup 
	 * 		the science group to set;
	 * 		if null, an empty string is assigned
	 */
	public void setScienceGroup(String scienceGroup) {
		if ( scienceGroup == null )
			this.scienceGroup = "";
		else
			this.scienceGroup = scienceGroup;
	}

	/**
	 * @return 
	 * 		the DOI of the original cruise data; never null but could be empty
	 */
	public String getOrigDataRef() {
		return origDataRef;
	}

	/**
	 * @param origDOI 
	 * 		the DOI of the original cruise data to set;
	 * 		if null, an empty string is assigned
	 */
	public void setOrigDataRef(String origDataRef) {
		if ( origDataRef == null )
			this.origDataRef = "";
		else
			this.origDataRef = origDataRef;
	}

	/**
	 * @return 
	 * 		the metadata http reference; never null but could be empty
	 */
	public String getMetadataHRef() {
		return metadataHRef;
	}

	/**
	 * @param metadataHRef 
	 * 		the metadata http reference to set;
	 * 		if null, an empty string is assigned
	 */
	public void setMetadataHRef(String metadataHRef) {
		if ( metadataHRef == null )
			this.metadataHRef = "";
		else
			this.metadataHRef = metadataHRef;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + cruiseName.hashCode();
		result = result * prime + vesselName.hashCode();
		result = result * prime + scienceGroup.hashCode();
		result = result * prime + origDataRef.hashCode();
		result = result * prime + metadataHRef.hashCode();
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
		if ( ! cruiseName.equals(other.cruiseName) )
			return false;
		if ( ! vesselName.equals(other.vesselName) )
			return false;
		if ( ! scienceGroup.equals(other.scienceGroup) )
			return false;
		if ( ! origDataRef.equals(other.origDataRef) )
			return false;
		if ( ! metadataHRef.equals(other.metadataHRef) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OmeMetadata[ expocode=" + expocode + 
				",\n    cruiseName=" + cruiseName + 
				",\n    vesselName=" + vesselName + 
				",\n    scienceGroup=" + scienceGroup +
				",\n    origDataRef=" + origDataRef + 
				",\n    metadataHRef=" + metadataHRef + 
				",\n    filename=" + filename + 
				",\n    uploadTimestamp=" + uploadTimestamp + 
				",\n    owner=" + owner + 
				",\n    selected=" + selected + 
				" ]";
	}


}
