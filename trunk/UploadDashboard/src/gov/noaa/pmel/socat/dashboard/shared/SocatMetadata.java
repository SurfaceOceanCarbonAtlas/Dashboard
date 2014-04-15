/**
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class for working with metadata values of interest from SOCAT,
 * including those derived from cruise data.
 * 
 * @author Karl Smith
 */
public class SocatMetadata implements Serializable, IsSerializable {

	private static final long serialVersionUID = 3013666093176935461L;

	/**
	 * Date used as a missing value; 
	 * corresponds to Jan 2, 3000 00:00:00 GMT
	 */
	public static final Date DATE_MISSING_VALUE = new Date(32503766400429L);

	/**
	 * String separating individual PI's listed in the science group String
	 */
	public static final String PIS_SEPARATOR = "; ";

	/**
	 * Value for no cruise QC flag.
	 */
	public static final Character NO_CRUISE_FLAG = ' ';

	String expocode;
	String cruiseName;
	String vesselName;
	String organization;
	Double westmostLongitude;
	Double eastmostLongitude;
	Double southmostLatitude;
	Double northmostLatitude;
	Date beginTime;
	Date endTime;
	String scienceGroup;
	String origDataRef;
	String socatDOI;
	String socatDOIHRef;
	Character cruiseFlag;

	/**
	 * Generates an empty SocatMetadata object.
	 */
	public SocatMetadata() {
		expocode = "";
		cruiseName = "";
		vesselName = "";
		organization = "";
		westmostLongitude = Double.NaN;
		eastmostLongitude = Double.NaN;
		southmostLatitude = Double.NaN;
		northmostLatitude = Double.NaN;
		beginTime = DATE_MISSING_VALUE;
		endTime = DATE_MISSING_VALUE;
		scienceGroup = "";
		origDataRef = "";
		socatDOI = "";
		socatDOIHRef = "";
		cruiseFlag = NO_CRUISE_FLAG;
	}

	/**
	 * @return
	 * 		the expocode;
	 * 		never null but could be empty if not assigned
	 */
	public String getExpocode() {
		return expocode;
	}

	/**
	 * @param expocode 
	 * 		the expocode to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setExpocode(String expocode) {
		if ( expocode == null )
			this.expocode = "";
		else
			this.expocode = expocode;
	}

	/**
	 * @return
	 * 		the cruise/dataset name;
	 * 		never null but could be empty if not assigned
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
	 * 		the vessel (ship) name; 
	 * 		never null but could be empty if not assigned
	 */
	public String getVesselName() {
		return vesselName;
	}

	/**
	 * @param vesselName 
	 * 		the vesselName to set;
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
	 * 		the organization/institution;
	 * 		never null but could be empty if not assigned
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * @param organization 
	 * 		the organization/institution to set;
	 * 		if null, an empty string is assigned
	 */
	public void setOrganization(String organization) {
		if ( organization == null )
			this.organization = "";
		else
			this.organization = organization;
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
	 * 		the beginning time for the cruise;
	 * 		never null but could be {@link #DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime 
	 * 		the beginning time for the cruise to set;
	 * 		if null, {@link #DATE_MISSING_VALUE} is assigned
	 */
	public void setBeginTime(Date beginTime) {
		if ( beginTime == null )
			this.beginTime = DATE_MISSING_VALUE;
		else 
			this.beginTime = beginTime;
	}

	/**
	 * @return
	 * 		the ending time for the cruise;
	 * 		never null but could be {@link #DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime 
	 * 		the ending time for the cruise to set;
	 * 		if null, {@link #DATE_MISSING_VALUE} is assigned
	 */
	public void setEndTime(Date endTime) {
		if ( endTime == null )
			this.endTime = DATE_MISSING_VALUE;
		else 
			this.endTime = endTime;
	}

	/**
	 * @return
	 * 		the science group associated with this instance; 
	 * 		never null but could be empty if not assigned
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
	 * 		the original data reference associated with this instance; 
	 * 		never null but could be empty if not assigned
	 */
	public String getOrigDataRef() {
		return origDataRef;
	}

	/**
	 * @param origDataRef 
	 * 		the original data reference to set; 
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
	 * 		the SOCAT enhanced data DOI associated with this instance; 
	 * 		never null but could be empty if not assigned
	 */
	public String getSocatDOI() {
		return socatDOI;
	}

	/**
	 * @param socatDOI 
	 * 		the SOCAT enhanced DOI to set; 
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
	 * 		the SOCAT enhanced data DOI http address associated with 
	 * 		this instance; never null but could be empty if not assigned
	 */
	public String getSocatDOIHRef() {
		return socatDOIHRef;
	}

	/**
	 * @param socatDOIHRef 
	 * 		the SOCAT enhance data DOI http address to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setSocatDOIHRef(String socatDOIHRef) {
		if ( socatDOIHRef == null )
			this.socatDOIHRef = "";
		else
			this.socatDOIHRef = socatDOIHRef;
	}

	/**
	 * @return
	 * 		the cruise flag value associated with this instance;
	 * 		never null but could be {@link #NO_CRUISE_FLAG} if not assigned
	 */
	public Character getCruiseFlag() {
		return cruiseFlag;
	}

	/**
	 * @param cruiseFlag 
	 * 		the cruise flag value to set; 
	 * 		if null, {@link #NO_CRUISE_FLAG} is assigned
	 */
	public void setCruiseFlag(Character cruiseFlag) {
		if ( cruiseFlag == null )
			this.cruiseFlag = NO_CRUISE_FLAG;
		else
			this.cruiseFlag = cruiseFlag;
	}

	/**
	 * @return
	 * 		the maximum length of String values given in the fields of this instance
	 */
	public int getMaxStringLength() {
		int maxLength = expocode.length();
		if ( maxLength < cruiseName.length() ) 
			maxLength = cruiseName.length();
		if ( maxLength < vesselName.length() ) 
			maxLength = vesselName.length();
		if ( maxLength < scienceGroup.length() ) 
			maxLength = scienceGroup.length();
		if ( maxLength < origDataRef.length() ) 
			maxLength = origDataRef.length();
		if ( maxLength < socatDOI.length() ) 
			maxLength = socatDOI.length();
		if ( maxLength < socatDOIHRef.length() ) 
			maxLength = socatDOIHRef.length();
		return maxLength;
	}

	@Override 
	public int hashCode() {
		// Do not consider floating-point fields since they do not 
		// have to be exactly the same for equals to return true.
		final int prime = 37;
		int result = expocode.hashCode();
		result = result * prime + cruiseName.hashCode();
		result = result * prime + vesselName.hashCode();
		result = result * prime + organization.hashCode();
		result = result * prime + beginTime.hashCode();
		result = result * prime + endTime.hashCode();
		result = result * prime + scienceGroup.hashCode();
		result = result * prime + origDataRef.hashCode();
		result = result * prime + socatDOI.hashCode();
		result = result * prime + socatDOIHRef.hashCode();
		result = result * prime + cruiseFlag.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof SocatMetadata) )
			return false;
		SocatMetadata other = (SocatMetadata) obj;

		// Date comparisons
		if ( ! beginTime.equals(other.beginTime) )
			return false;

		if ( ! endTime.equals(other.endTime) )
			return false;

		// String comparisons
		if ( ! expocode.equals(other.expocode) )
			return false;

		if ( ! cruiseName.equals(other.cruiseName) )
			return false;

		if ( ! vesselName.equals(other.vesselName) )
			return false;

		if ( ! organization.equals(other.organization) )
			return false;

		if ( ! scienceGroup.equals(other.scienceGroup) )
			return false;

		if ( ! origDataRef.equals(other.origDataRef) ) 
			return false;

		if ( ! socatDOI.equals(other.socatDOI) )
			return false;

		if ( ! socatDOIHRef.equals(other.socatDOIHRef) )
			return false;

		if ( ! cruiseFlag.equals(other.cruiseFlag) )
			return false;

		// Floating-point comparisons
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
		return "SocatMetadata[ expocode=" + expocode +
				",\n    cruiseName=" + cruiseName +
				",\n    vesselName=" + vesselName + 
				",\n    organization=" + organization + 
				",\n    westmostLongitude=" + westmostLongitude.toString() + 
				",\n    eastmostLongitude=" + eastmostLongitude.toString() + 
				",\n    southmostLatitude=" + southmostLatitude.toString() + 
				",\n    northmostLatitude=" + northmostLatitude.toString() + 
				",\n    startDate=" + beginTime.toString() + 
				",\n    endDate=" + endTime.toString() + 
				",\n    scienceGroup=" + scienceGroup + 
				",\n    origDataRef=" + origDataRef + 
				",\n    socatDOI=" + socatDOI + 
				",\n    socatDOIHRef=" + socatDOIHRef + 
				",\n    cruiseFlag=" + cruiseFlag.toString() + 
				" ]";
	}

	// Use the Unicode code points to define these characters
	// so we know exactly what value is being used in the String
	public static final String aAcute = "\u00E1";
	public static final String aRing = "\u00E5";
	public static final String eGrave = "\u00E8";
	public static final String eAcute = "\u00E9";
	public static final String iAcute = "\u00ED";
	public static final String oUmlaut = "\u00F6";

	public static final HashMap<String,String> VESSEL_NAME_CORRECTIONS = 
			new HashMap<String,String>();
	static {
		VESSEL_NAME_CORRECTIONS.put("Haakon Mosby", "H" + aRing + "kon Mosby");
		VESSEL_NAME_CORRECTIONS.put("Hesperides", "Hesp" + eAcute + "rides");
		VESSEL_NAME_CORRECTIONS.put("Ka imimoana", "Ka'imimoana");
		VESSEL_NAME_CORRECTIONS.put("L Astrolabe", "L'Astrolabe");
		VESSEL_NAME_CORRECTIONS.put("L Atalante", "L'Atalante");
	}

	public static final HashMap<String,String> SCIENCE_GROUP_NAME_CORRECTIONS = 
			new HashMap<String,String>();
	static {
		SCIENCE_GROUP_NAME_CORRECTIONS.put("Aida F. Rios", 
				"Aida F. R" + iAcute + "os");
		SCIENCE_GROUP_NAME_CORRECTIONS.put("Aida F. Rios; Fiz F. Perez", 
				"Aida F. R" + iAcute + "os; Fiz F. P" + eAcute + "rez");
		SCIENCE_GROUP_NAME_CORRECTIONS.put("Are Olsen; Sara Jutterstrom; Truls Johannessen",
				"Are Olsen; Sara Jutterstr" + oUmlaut + "m; Truls Johannessen");
		SCIENCE_GROUP_NAME_CORRECTIONS.put("Arne Koertzinger", 
				"Arne K" + oUmlaut + "rtzinger");
		SCIENCE_GROUP_NAME_CORRECTIONS.put("Fiz F. Perez", 
				"Fiz F. P" + eAcute + "rez");
		SCIENCE_GROUP_NAME_CORRECTIONS.put("Melchor Gonzalez-Davila; J. Magdalena Santana-Casiano",
				"Melchor Gonz" + aAcute + "lez-D" +  aAcute + "vila; J. Magdalena Santana-Casiano");
		SCIENCE_GROUP_NAME_CORRECTIONS.put("Nathalie Lefevre", 
				"Nathalie Lef" + eGrave + "vre");
		SCIENCE_GROUP_NAME_CORRECTIONS.put("Tobias Steinhoff; Arne Koertzinger", 
				"Tobias Steinhoff; Arne K" + oUmlaut + "rtzinger");
	}

	/**
	 * Corrects the spelling of ship and PI names intentionally misspelled 
	 * in SOCAT metadata because they contain characters that the SOCAT 
	 * database cannot always handle correctly.
	 */
	public static void correctSpellings(SocatMetadata metadata) {
		String newName = VESSEL_NAME_CORRECTIONS.get(metadata.getVesselName());
		if ( newName != null )
			metadata.setVesselName(newName);
		newName = SCIENCE_GROUP_NAME_CORRECTIONS.get(metadata.getScienceGroup());
		if ( newName != null )
			metadata.setScienceGroup(newName);
	}

}
