/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Class for the one special metadata file per cruise that must be present,
 * has a known format, and contains values needed by the SOCAT database.
 *  
 * @author Karl Smith
 */
public class OmeMetadata extends DashboardMetadata {

	private static final long serialVersionUID = 8817540931987107642L;

	// The following come from the OME metadata 
	String cruiseName;
	String vesselName;
	String scienceGroup;
	String origDataRef;

	// This may be the reference to this document to be archived
	String metadataHRef;

	// The following come from SOCAT
	String socatDOI;
	String socatHRef;
	String cruiseFlag;

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
		socatDOI = "";
		socatHRef = "";
		cruiseFlag = "";
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
	 * 		if the information in the DashboardMetadata is invalid, or
	 * 		if the contents of the metadata document are not valid
	 */
	public OmeMetadata(DashboardMetadata mdata) {
		// Initialize to an empty OME metadata document
		this();

		// Copy expocode, filename, uploadTimestamp, and owner 
		// from the given DashboardMetadata object
		expocode = mdata.getExpocode();
		filename = mdata.getFilename();
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
		File mdataFile = mdataHandler.getMetadataFile(expocode, filename);
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
						scienceGroup += "; " + metaVals[k];
				}
			}
			else if ( "PI_2".equals(colNames[k]) ||
					  "PI 2".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( scienceGroup.isEmpty() )
						scienceGroup = metaVals[k];
					else
						scienceGroup += "; " + metaVals[k];
				}
			}
			else if ( "PI_3".equals(colNames[k]) ||
					  "PI 3".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( scienceGroup.isEmpty() )
						scienceGroup = metaVals[k];
					else
						scienceGroup += "; " + metaVals[k];
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
						metadataHRef += " ; " + metaVals[k];
				}
			}
			else if ( "Metadata_hyperlink_2".equals(colNames[k]) ||
					  "Metadata 2".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( metadataHRef.isEmpty() )
						metadataHRef = metaVals[k];
					else
						metadataHRef += " ; " + metaVals[k];
				}
			}
			else if ( "Metadata_hyperlink_3".equals(colNames[k]) ||
					  "Metadata 3".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( metadataHRef.isEmpty() )
						metadataHRef = metaVals[k];
					else
						metadataHRef += " ; " + metaVals[k];
				}
			}
			else if ( "socat_doi".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) )
					socatDOI = metaVals[k];
			}
			else if ( "socat_doi_href".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) )
					socatHRef = metaVals[k];
			}
			// otherwise ignore the data in this column
			// cruiseFlag is left as the default
		}
		if ( expocode.isEmpty() )
			throw new IllegalArgumentException("Expocode is not given");
		// Assign the standard OME document filename from the expocode
		filename = expocode + "_OME.xml";  
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
				scienceGroup += "; " + name;
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

	/**
	 * @return 
	 * 		the SOCAT DOI; never null but could be empty
	 */
	public String getSocatDOI() {
		return socatDOI;
	}

	/**
	 * @param socatDOI 
	 * 		the SOCAT DOI to set;
	 * 		if null, an empty string is assigned
	 */
	public void setSocatDOI(String socatDOI) {
		if ( socatDOI == null )
			this.socatDOI = "";
		else
			this.socatDOI = socatDOI;
	}

	/**
	 * @return 
	 * 		the SOCAT http reference; never null but could be empty
	 */
	public String getSocatHRef() {
		return socatHRef;
	}

	/**
	 * @param socatDOIHRef 
	 * 		the SOCAT http reference to set;
	 * 		if null, an empty string is assigned
	 */
	public void setSocatHRef(String socatHRef) {
		if ( socatHRef == null )
			this.socatHRef = "";
		else
			this.socatHRef = socatHRef;
	}

	/**
	 * @return 
	 * 		the cruise flag; never null but could be empty
	 */
	public String getCruiseFlag() {
		return cruiseFlag;
	}

	/**
	 * @param cruiseFlag 
	 * 		the cruise flag to set;
	 * 		if null, an empty string is assigned
	 */
	public void setCruiseFlag(String cruiseFlag) {
		if ( cruiseFlag == null )
			this.cruiseFlag = "";
		else
			this.cruiseFlag = cruiseFlag;
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
		result = result * prime + socatDOI.hashCode();
		result = result * prime + socatHRef.hashCode();
		result = result * prime + cruiseFlag.hashCode();
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
		if ( ! socatDOI.equals(other.socatDOI) )
			return false;
		if ( ! socatHRef.equals(other.socatHRef) )
			return false;
		if ( ! cruiseFlag.equals(other.cruiseFlag) )
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
				",\n    socatDOI=" + socatDOI + 
				",\n    socatHRef=" + socatHRef + 
				",\n    cruiseFlag=" + cruiseFlag + 
				",\n    filename=" + filename + 
				",\n    uploadTimestamp=" + uploadTimestamp + 
				",\n    owner=" + owner + 
				",\n    selected=" + selected + 
				" ]";
	}


}
