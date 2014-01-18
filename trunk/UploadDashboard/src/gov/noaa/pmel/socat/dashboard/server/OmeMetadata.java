/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import java.util.Date;

import com.google.gwt.i18n.shared.DateTimeFormat;

import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

/**
 * Class for the one special metadata file per cruise that must be present,
 * has a known format, and contains values needed by the SOCAT database.
 *  
 * @author Karl Smith
 */
public class OmeMetadata extends DashboardMetadata {

	private static final long serialVersionUID = -2085073604011920949L;

	// The following come from the OME metadata 
	String cruiseName;
	String vesselName;
	String scienceGroup;
	String origDataRef;
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
	 * 		DashboardMetadata specifying the OME XML file to read.
	 * 		The upload timestamp and owner are copied from the DashboardMetadata.
	 */
	public OmeMetadata(DashboardMetadata mdata) {
		this();
		// TODO:
	}

	/**
	 * Creates from a line of tab-separated values from Benjamin's metadata spreadsheet.
	 * 
	 * @param dataLine 
	 * 		metadata for a cruise as a string of tab-separated values
	 * @param colNames 
	 * 		array of column names for the metadata in dataLine
	 * @throws IllegalArgumentException
	 * 		if the number of metadata values does not match the number 
	 * 		of column names or if the expocode is not defined
	 */
	public OmeMetadata(String dataLine, String[] colNames) {
		// Initialize to an empty OME metadata document
		this();
		// Parse the metadata string - split on tabs
		String[] metaVals = dataLine.split("\\t", -1);
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
					if ( scienceGroup == null )
						scienceGroup = metaVals[k];
					else
						scienceGroup += "; " + metaVals[k];
				}
			}
			else if ( "PI_2".equals(colNames[k]) ||
					  "PI 2".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( scienceGroup == null )
						scienceGroup = metaVals[k];
					else
						scienceGroup += "; " + metaVals[k];
				}
			}
			else if ( "PI_3".equals(colNames[k]) ||
					  "PI 3".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( scienceGroup == null )
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
					if ( metadataHRef == null )
						metadataHRef = metaVals[k];
					else
						metadataHRef += " ; " + metaVals[k];
				}
			}
			else if ( "Metadata_hyperlink_2".equals(colNames[k]) ||
					  "Metadata 2".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( metadataHRef == null )
						metadataHRef = metaVals[k];
					else
						metadataHRef += " ; " + metaVals[k];
				}
			}
			else if ( "Metadata_hyperlink_3".equals(colNames[k]) ||
					  "Metadata 3".equals(colNames[k]) )  {
				if ( ! "NaN".equals(metaVals[k]) ) {
					if ( metadataHRef == null )
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
		uploadTimestamp = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm Z")
										.format(new Date());
		// Leave the owner blank so anyone can take possession of it
	}

	/**
	 * @return 
	 * 		the cruise name
	 */
	public String getCruiseName() {
		return cruiseName;
	}

	/**
	 * @param cruiseName 
	 * 		the cruise name to set
	 */
	public void setCruiseName(String cruiseName) {
		this.cruiseName = cruiseName;
	}

	/**
	 * @return 
	 * 		the vessel name
	 */
	public String getVesselName() {
		return vesselName;
	}

	/**
	 * @param vesselName 
	 * 		the vessel name to set
	 */
	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	/**
	 * @return 
	 * 		the science group
	 */
	public String getScienceGroup() {
		return scienceGroup;
	}

	/**
	 * @param scienceGroup 
	 * 		the science group to set
	 */
	public void setScienceGroup(String scienceGroup) {
		this.scienceGroup = scienceGroup;
	}

	/**
	 * @return 
	 * 		the DOI of the original cruise data
	 */
	public String getOrigDataRef() {
		return origDataRef;
	}

	/**
	 * @param origDOI 
	 * 		the DOI of the original cruise data to set
	 */
	public void setOrigDataRef(String origDataRef) {
		this.origDataRef = origDataRef;
	}

	/**
	 * @return 
	 * 		the metadata http reference
	 */
	public String getMetadataHRef() {
		return metadataHRef;
	}

	/**
	 * @param metadataHRef 
	 * 		the metadata http reference to set
	 */
	public void setMetadataHRef(String metadataHRef) {
		this.metadataHRef = metadataHRef;
	}

	/**
	 * @return 
	 * 		the SOCAT DOI
	 */
	public String getSocatDOI() {
		return socatDOI;
	}

	/**
	 * @param socatDOI 
	 * 		the SOCAT DOI to set
	 */
	public void setSocatDOI(String socatDOI) {
		this.socatDOI = socatDOI;
	}

	/**
	 * @return 
	 * 		the SOCAT http reference
	 */
	public String getSocatHRef() {
		return socatHRef;
	}

	/**
	 * @param socatDOIHRef 
	 * 		the SOCAT http reference to set
	 */
	public void setSocatHRef(String socatHRef) {
		this.socatHRef = socatHRef;
	}

	/**
	 * @return 
	 * 		the cruise flag
	 */
	public String getCruiseFlag() {
		return cruiseFlag;
	}

	/**
	 * @param cruiseFlag 
	 * 		the cruise flag to set
	 */
	public void setCruiseFlag(String cruiseFlag) {
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

}
