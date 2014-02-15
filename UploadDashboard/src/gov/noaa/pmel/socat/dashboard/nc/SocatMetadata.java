/**
 */
package gov.noaa.pmel.socat.dashboard.nc;

import gov.noaa.pmel.socat.dashboard.server.OmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class for working with metadata values of interest from SOCAT. 
 * 
 * @author Karl Smith
 */
public class SocatMetadata implements Serializable, IsSerializable {

	private static final long serialVersionUID = 3548714248034745621L;

	// Jan 2, 3000 00:00:00 GMT
	public static final Date DATE_MISSING_VALUE = new Date(32503766400429L);

	String expocode;
	String cruiseName;
	String vesselName;
	Double westmostLongitude;
	Double eastmostLongitude;
	Double southmostLatitude;
	Double northmostLatitude;
	Date beginTime;
	Date endTime;
	String scienceGroup;
	String origDOI;
	String metadataHRef;
	String socatDOI;
	String socatDOIHRef;
	String cruiseFlag;

	/**
	 * Generates an empty SocatMetadata object.
	 */
	public SocatMetadata() {
		expocode = "";
		cruiseName = "";
		vesselName = "";
		westmostLongitude = Double.NaN;
		eastmostLongitude = Double.NaN;
		southmostLatitude = Double.NaN;
		northmostLatitude = Double.NaN;
		beginTime = DATE_MISSING_VALUE;
		endTime = DATE_MISSING_VALUE;
		scienceGroup = "";
		origDOI = "";
		metadataHRef = "";
		socatDOI = "";
		socatDOIHRef = "";
		cruiseFlag = "";
	}

	/**
	 * Generate from the data in an OmeMetadata object.
	 * 
	 * @param omeMData
	 * 		initialize from this data
	 */
	public SocatMetadata(OmeMetadata omeMData) {
		this();
		expocode = omeMData.getExpocode();
		cruiseName = omeMData.getCruiseName();
		vesselName = omeMData.getVesselName();
		scienceGroup = omeMData.getScienceGroup();
		origDOI = omeMData.getOrigDataRef();
		metadataHRef = omeMData.getMetadataHRef();
		// TODO: add and initialize more fields when they are 
		// identified in the OME XML file and added to OmeMetadata.
	}

	/**
	 * @return
	 * 		the expocode associated with this instance;
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
	 * 		the cruise name associated with this instance;
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
	 * 		the vessel (ship) name associated with this instance; 
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
	 * 		the original data DOI associated with this instance; 
	 * 		never null but could be empty if not assigned
	 */
	public String getOrigDOI() {
		return origDOI;
	}

	/**
	 * @param origDOI 
	 * 		the original data DOI to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setOrigDOI(String origDOI) {
		if ( origDOI == null )
			this.origDOI = "";
		else
			this.origDOI = origDOI;
	}

	/**
	 * @return
	 * 		the metadata href(s) associated with this instance; 
	 * 		never null but could be empty if not assigned
	 */
	public String getMetadataHRef() {
		return metadataHRef;
	}

	/**
	 * @param metadataHRef 
	 * 		the metadata href(s) to set; 
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
	 * 		never null but could be empty if not assigned
	 */
	public String getCruiseFlag() {
		return cruiseFlag;
	}

	/**
	 * @param cruiseFlag 
	 * 		the cruise flag value to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setCruiseFlag(String cruiseFlag) {
		if ( cruiseFlag == null )
			this.cruiseFlag = "";
		else
			this.cruiseFlag = cruiseFlag;
	}

	/**
	 * @return
	 * 		the maximum length of String values given in the fields of this instance
	 */
	public int getMaxStringLength() {
		int maxlength = -1;
		Field[] fields = this.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			f.setAccessible(true);
			if ( f.getType().equals(String.class) && !Modifier.isStatic(f.getModifiers())) {
				String s;
				try {
					s = (String) f.get(this);
					if ( s.length() > maxlength ) {
						maxlength = s.length();
					}
				} catch (IllegalArgumentException e) {
					// Carry on.  The value from other strings will be fine.
				} catch (IllegalAccessException e) {
					// Carry on.  The value from other strings will be fine.
				}            
			}
		}
		return maxlength;
	}

	@Override 
	public int hashCode() {
		// Do not consider floating-point fields since they do not 
		// have to be exactly the same for equals to return true.
		final int prime = 37;
		int result = expocode.hashCode();
		result = result * prime + cruiseName.hashCode();
		result = result * prime + vesselName.hashCode();
		result = result * prime + beginTime.hashCode();
		result = result * prime + endTime.hashCode();
		result = result * prime + scienceGroup.hashCode();
		result = result * prime + origDOI.hashCode();
		result = result * prime + metadataHRef.hashCode();
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

		if ( ! scienceGroup.equals(other.scienceGroup) )
			return false;

		if ( ! origDOI.equals(other.origDOI) ) 
			return false;

		if ( ! metadataHRef.equals(other.metadataHRef) )
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
				",\n    westmostLongitude=" + westmostLongitude.toString() + 
				",\n    eastmostLongitude=" + eastmostLongitude.toString() + 
				",\n    southmostLatitude=" + southmostLatitude.toString() + 
				",\n    northmostLatitude=" + northmostLatitude.toString() + 
				",\n    beginTime=" + beginTime.toString() + 
				",\n    endTime=" + endTime.toString() + 
				",\n    origDOI=" + origDOI + 
				",\n    metadataHRef=" + metadataHRef + 
				",\n    socatDOI=" + socatDOI + 
				",\n    socatDOIHRef=" + socatDOIHRef + 
				",\n    cruiseFlag=" + cruiseFlag + 
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
