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

	private static final long serialVersionUID = 2333369801131318590L;

	/**
	 * Date used as a missing value; 
	 * corresponds to Jan 2, 3000 00:00:00 GMT
	 */
	public static final Date DATE_MISSING_VALUE = new Date(32503766400429L);

	/**
	 * String separating each PI listed in scienceGroup, each organization 
	 * listed in organizations, and each additional document filename listed 
	 * in addlDocs.  This is cannot be a semicolon due to Ferret issues.
	 */
	public static final String NAMES_SEPARATOR = " : ";

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
	String addlDocs;
	String socatDOI;
	String socatDOIHRef;
	String socatVersion;
	String qcFlag;

	/**
	 * Generates an empty SocatMetadata object.
	 */
	public SocatMetadata() {
		expocode = "";
		cruiseName = "";
		vesselName = "";
		organization = "";
		westmostLongitude = SocatCruiseData.FP_MISSING_VALUE;
		eastmostLongitude = SocatCruiseData.FP_MISSING_VALUE;
		southmostLatitude = SocatCruiseData.FP_MISSING_VALUE;
		northmostLatitude = SocatCruiseData.FP_MISSING_VALUE;
		beginTime = DATE_MISSING_VALUE;
		endTime = DATE_MISSING_VALUE;
		scienceGroup = "";
		origDataRef = "";
		addlDocs = "";
		socatDOI = "";
		socatDOIHRef = "";
		socatVersion = "";
		qcFlag = " ";
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
	 * 		never null could be {@link SocatCruiseData#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getWestmostLongitude() {
		return westmostLongitude;
	}

	/**
	 * @param westmostLongitude 
	 * 		the west-most longitude to set;
	 * 		if null, {@link SocatCruiseData#FP_MISSING_VALUE} is assigned
	 */
	public void setWestmostLongitude(Double westmostLongitude) {
		if ( westmostLongitude == null )
			this.westmostLongitude = SocatCruiseData.FP_MISSING_VALUE;
		else 
			this.westmostLongitude = westmostLongitude;
	}

	/**
	 * @return
	 * 		the east-most longitude for the cruise;
	 * 		never null but could be {@link SocatCruiseData#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getEastmostLongitude() {
		return eastmostLongitude;
	}

	/**
	 * @param eastmostLongitude 
	 * 		the east-most longitude to set;
	 * 		if null, {@link SocatCruiseData.FP_MISSING_VALUE} is assigned
	 */
	public void setEastmostLongitude(Double eastmostLongitude) {
		if ( eastmostLongitude == null )
			this.eastmostLongitude = SocatCruiseData.FP_MISSING_VALUE;
		else
			this.eastmostLongitude = eastmostLongitude;
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be {@link SocatCruiseData#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getSouthmostLatitude() {
		return southmostLatitude;
	}

	/**
	 * @param southmostLatitude 
	 * 		the south-most latitude to set;
	 * 		if null, {@link SocatCruiseData#FP_MISSING_VALUE} is assigned
	 */
	public void setSouthmostLatitude(Double southmostLatitude) {
		if ( southmostLatitude == null )
			this.southmostLatitude = SocatCruiseData.FP_MISSING_VALUE;
		else
			this.southmostLatitude = southmostLatitude;
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be {@link SocatCruiseData#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getNorthmostLatitude() {
		return northmostLatitude;
	}

	/**
	 * @param northmostLatitude 
	 * 		the north-most latitude to set;
	 * 		if null, {@link SocatCruiseData#FP_MISSING_VALUE} is assigned
	 */
	public void setNorthmostLatitude(Double northmostLatitude) {
		if ( northmostLatitude == null )
			this.northmostLatitude = SocatCruiseData.FP_MISSING_VALUE;
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
	 * 		the additional document names associated with this instance; 
	 * 		never null but could be empty if not assigned
	 */
	public String getAddlDocs() {
		return addlDocs;
	}

	/**
	 * @param addlDocs 
	 * 		the additional document names to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setAddlDocs(String addlDocs) {
		if ( addlDocs == null )
			this.addlDocs = "";
		else
			this.addlDocs = addlDocs;
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
	 * 		the SOCAT version associated with this instance;
	 * 		never null but could be empty if not assigned
	 */
	public String getSocatVersion() {
		return socatVersion;
	}

	/**
	 * @param restoredSocatVersion 
	 * 		the SOCAT version to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setSocatVersion(String socatVersion) {
		if ( socatVersion == null )
			this.socatVersion = "";
		else
			this.socatVersion = socatVersion;
	}

	/**
	 * @return
	 * 		the QC flag;
	 * 		never null but could be a string with a single blank character if not assigned
	 */
	public String getQcFlag() {
		return qcFlag;
	}

	/**
	 * @param qcFlag 
	 * 		the QC flag to set; 
	 * 		if null, a string with a single blank character is assigned
	 */
	public void setQcFlag(String qcFlag) {
		if ( qcFlag == null )
			this.qcFlag = " ";
		else
			this.qcFlag = qcFlag;
	}

	/**
	 * @return
	 * 		the maximum length of String values given in the fields of this instance
	 */
	public int getMaxStringLength() {
		int maxLength = 12;
		if ( maxLength < expocode.length() )
			maxLength = expocode.length();
		if ( maxLength < cruiseName.length() ) 
			maxLength = cruiseName.length();
		if ( maxLength < vesselName.length() ) 
			maxLength = vesselName.length();
		if ( maxLength < organization.length() ) 
			maxLength = organization.length();
		if ( maxLength < scienceGroup.length() ) 
			maxLength = scienceGroup.length();
		if ( maxLength < origDataRef.length() ) 
			maxLength = origDataRef.length();
		if ( maxLength < addlDocs.length() )
			maxLength = addlDocs.length();
		if ( maxLength < socatDOI.length() ) 
			maxLength = socatDOI.length();
		if ( maxLength < socatDOIHRef.length() ) 
			maxLength = socatDOIHRef.length();
		if ( maxLength < socatVersion.length() )
			maxLength = socatVersion.length();
		if ( maxLength < qcFlag.length() )
			maxLength = qcFlag.length();
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
		result = result * prime + addlDocs.hashCode();
		result = result * prime + socatDOI.hashCode();
		result = result * prime + socatDOIHRef.hashCode();
		result = result * prime + socatVersion.hashCode();
		result = result * prime + qcFlag.hashCode();
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
		if ( ! addlDocs.equals(other.addlDocs) )
			return false;
		if ( ! socatDOI.equals(other.socatDOI) )
			return false;
		if ( ! socatDOIHRef.equals(other.socatDOIHRef) )
			return false;
		if ( ! socatVersion.equals(other.socatVersion) )
			return false;
		if ( ! qcFlag.equals(other.qcFlag) )
			return false;

		// Floating-point comparisons
		if ( ! DashboardUtils.closeTo(southmostLatitude, 
				other.southmostLatitude, 0.0, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(northmostLatitude, 
				other.northmostLatitude, 0.0, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
			return false;

		// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
		if ( ! DashboardUtils.longitudeCloseTo(westmostLongitude, other.westmostLongitude, 0.0, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.longitudeCloseTo(eastmostLongitude, other.eastmostLongitude, 0.0, SocatCruiseData.MAX_ABSOLUTE_ERROR) )
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
				",\n    addlDocs=" + addlDocs + 
				",\n    socatDOI=" + socatDOI + 
				",\n    socatDOIHRef=" + socatDOIHRef + 
				",\n    restoredSocatVersion=" + socatVersion + 
				",\n    qcFlag=" + qcFlag + 
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
	public static final String yAcute = "\u00FD";

	/**
	 * Standardized known alternate or misspelled PI names.  Note that 
	 * some of these "standard" names are intentionally misspelled and 
	 * {@link #PI_NAME_CORRECTIONS} should be used for the proper spelling.  
	 * Also note that these are single PI names only; each name in a 
	 * multiple-name science group needs to be checked individually.
	 */
	public static final HashMap<String,String> PI_RENAME_MAP;
	static {
		PI_RENAME_MAP = new HashMap<String,String>();
		PI_RENAME_MAP.put("", "unknown");
		PI_RENAME_MAP.put("Abdirahman Omar", "Omar, A.");
		PI_RENAME_MAP.put("Adrienne J. Sutton", "Sutton, A.");
		PI_RENAME_MAP.put("Adrienne Sutton", "Sutton, A.");
		PI_RENAME_MAP.put("Agneta Fransson", "Fransson, A.");
		PI_RENAME_MAP.put("Aida F. Rios", "Rios A.F.");
		PI_RENAME_MAP.put("Akihiko Murata", "Murata, A.");
		PI_RENAME_MAP.put("Akira Nakadate", "Nakadate, A.");
		PI_RENAME_MAP.put("Alan Poisson", "Poisson, A.");
		PI_RENAME_MAP.put("Alberto Borges", "Borges, A.");
		PI_RENAME_MAP.put("Andrew Watson", "Watson, A.");
		PI_RENAME_MAP.put("Are Olsen", "Olsen, A.");
		PI_RENAME_MAP.put("Arne Koertzinger", "Koertzinger, A.");
		PI_RENAME_MAP.put("Arne K" + oUmlaut + "tzinger", "Koertzinger, A.");
		PI_RENAME_MAP.put("B" + eAcute + "govic, M.", "Begovic, M.");
		PI_RENAME_MAP.put("B" + yAcute + "govic, M.", "Begovic, M.");
		PI_RENAME_MAP.put("Bernd Schneider", "Schneider, B.");
		PI_RENAME_MAP.put("Bronte Tilbrook", "Tilbrook, B.");
		PI_RENAME_MAP.put("Catherine Goyet", "Goyet, C.");
		PI_RENAME_MAP.put("Cathy Cosca", "Cosca, C.");
		PI_RENAME_MAP.put("Christopher Sabine", "Sabine, C.");
		PI_RENAME_MAP.put("Christopher W. Hunt", "Hunt C.W.");
		PI_RENAME_MAP.put("Claire Copin-Montegut", "Copin-Montegut, C.");
		PI_RENAME_MAP.put("Copin-Mont" + eAcute + "gut, C.", "Copin-Montegut, C.");
		PI_RENAME_MAP.put("Copin-Mont" + yAcute + "gut, C.", "Copin-Montegut, C.");
		PI_RENAME_MAP.put("C. S. Wong", "Wong, C.S.");
		PI_RENAME_MAP.put("D. Vandemark", "Vandemark, D.");
		PI_RENAME_MAP.put("David Hydes", "Hydes. D.");
		PI_RENAME_MAP.put("de Baar, H.J.W", "de Baar, H.J.W.");
		PI_RENAME_MAP.put("Dorothee Bakker", "Bakker, D.");
		PI_RENAME_MAP.put("Douglas Wallace", "Wallace, D.");
		PI_RENAME_MAP.put("Doug Vandemark", "Vandemark, D.");
		PI_RENAME_MAP.put("Fiz F. Perez", "Perez, F.F.");
		PI_RENAME_MAP.put("Goyet, C", "Goyet, C.");
		PI_RENAME_MAP.put("Helmuth Thomas", "Thomas, H.");
		PI_RENAME_MAP.put("Hein de Baar", "de Baar, H.J.W.");
		PI_RENAME_MAP.put("Hisayuki Inoue", "Inoue, H.");
		PI_RENAME_MAP.put("Ingunn Skjelvan", "Skjelvan, I.");
		PI_RENAME_MAP.put("J. Magdalena Santana-Casiano", "Santana-Casiano, J.M.");
		PI_RENAME_MAP.put("Jane Robertson", "Robertson, J.");
		PI_RENAME_MAP.put("Jaqueline Boutin", "Boutin, J.");
		PI_RENAME_MAP.put("Jeremy Matthis", "Mathis, J.");
		PI_RENAME_MAP.put("Joe Salisbury", "Salisbury, J.");
		PI_RENAME_MAP.put("Kim Currie", "Currie, K.I.");
		PI_RENAME_MAP.put("K" + oUmlaut + "tzinger, A.", "Koertzinger, A.");
		PI_RENAME_MAP.put("Liliane Merlivat", "Merlivat, L.");
		PI_RENAME_MAP.put("Ludger Mintrop", "Mintrop, L.");
		PI_RENAME_MAP.put("Mario Hoppema", "Hoppema, M.");
		PI_RENAME_MAP.put("Melchor Gonzalez-Davila", "Gonzalez-Davila, M.");
		PI_RENAME_MAP.put("Melissa Chierici"," Chierici, M.");
		PI_RENAME_MAP.put("Michel Frankignoulle", "Frankignoulle, M.");
		PI_RENAME_MAP.put("Michel Stoll", "Stoll, M.");
		PI_RENAME_MAP.put("Milena Begovic", "Begovic, M.");
		PI_RENAME_MAP.put("Naoami Greenwood", "Greenwood, N.");
		PI_RENAME_MAP.put("Nathalie Lefevre", "Lefevre, N.");
		PI_RENAME_MAP.put("Nick Hardman-Mountford", "Hardman-Mountford, N.J.");
		PI_RENAME_MAP.put("Nicolas Metzl", "Metzl, N.");
		PI_RENAME_MAP.put("OMEX Project Members", "OMEX Project Members");
		PI_RENAME_MAP.put("Pedro Monteiro", "Monteiro, P.");
		PI_RENAME_MAP.put("Ray Weiss", "Weiss, R.");
		PI_RENAME_MAP.put("Richard Bellerby", "Bellerby, R.");
		PI_RENAME_MAP.put("Richard Feely", "Feely, R.");
		PI_RENAME_MAP.put("Rik Wanninkhof", "Wanninkhof, R.");
		PI_RENAME_MAP.put("Robert Key", "Key, R.");
		PI_RENAME_MAP.put("S. Saito", "Saito, S.");
		PI_RENAME_MAP.put("Sara Jutterstrom", "Jutterstrom, S.");
		PI_RENAME_MAP.put("Taro Takahashi", "Takahashi, T.");
		PI_RENAME_MAP.put("Tobias Steinhoff", "Steinhoff, T.");
		PI_RENAME_MAP.put("Tr" + eAcute + "guer, P.", "Treguer, P.");
		PI_RENAME_MAP.put("Tr" + yAcute + "guer, P.", "Treguer, P.");
		PI_RENAME_MAP.put("Truls Johannessen", "Johannessen, T.");
		PI_RENAME_MAP.put("Tsuneo Ono", "Ono, T.");
		PI_RENAME_MAP.put("Tsurushima Nobuo", "Nobuo, T.");
		PI_RENAME_MAP.put("Ute Schuster", "Schuster, U.");
		PI_RENAME_MAP.put("Vassilis Kitidis", "Kitidis, V.");
		PI_RENAME_MAP.put("Wannikhof, R.", "Wanninkhof, R.");
		PI_RENAME_MAP.put("Wei-Jun Cai", "Cai, W.-J.");
		PI_RENAME_MAP.put("W.-J. Cai", "Cai, W.-J.");
		PI_RENAME_MAP.put("Yukihiro Nojiri", "Nojiri, Y.");
		PI_RENAME_MAP.put("Yves Dandonneau", "Dandonneau, Y.");
	}

	/**
	 * Correctly spelled "standard" PI names that are intentionally misspelled.
	 * Note that these are single PI names only; each name in a multiple-name 
	 * science group needs to be checked individually.
	 */
	public static final HashMap<String,String> PI_NAME_CORRECTIONS;
	static {
		PI_NAME_CORRECTIONS = new HashMap<String,String>();
		PI_NAME_CORRECTIONS.put("Begovic, M.", "B" + eAcute + "govic, M.");
		PI_NAME_CORRECTIONS.put("Copin-Montegut, C.", "Copin-Mont" + eAcute + "gut, C.");
		PI_NAME_CORRECTIONS.put("Gonzalez-Davila, M.", "Gonz" + aAcute + "lez-D" +  aAcute + "vila, M.");
		PI_NAME_CORRECTIONS.put("Jutterstrom, S.", "Jutterstr" + oUmlaut + "m, S.");
		PI_NAME_CORRECTIONS.put("Koertzinger, A.", "K" + oUmlaut + "rtzinger, A.");
		PI_NAME_CORRECTIONS.put("Lefevre, N.", "Lef" + eGrave + "vre, N.");
		PI_NAME_CORRECTIONS.put("Perez, F.F.", "P" + eAcute + "rez, F.F.");
		PI_NAME_CORRECTIONS.put("Rios, A.F.", "R" + iAcute + "os, A.F.");
		PI_NAME_CORRECTIONS.put("Treguer, P.", "Tr" + eAcute + "guer, P.");
	}

	/**
	 * Standardized known alternate or misspelled vessel names.
	 * Note that some of these "standard" name are intentionally 
	 * misspelled and {@link #VESSEL_NAME_CORRECTIONS} should be
	 * used for the proper spelling.
	 */
	public static final HashMap<String,String> VESSEL_RENAME_MAP;
	static {
		VESSEL_RENAME_MAP = new HashMap<String,String>();
		VESSEL_RENAME_MAP.put("Almirante irizar", "Almirante Irizar");
		VESSEL_RENAME_MAP.put("Atlantic  Companion", "Atlantic Companion");
		VESSEL_RENAME_MAP.put("A. V. Humboldt", "A.V. Humboldt");
		VESSEL_RENAME_MAP.put("CAPE HATTERAS", "Cape Hatteras");
		VESSEL_RENAME_MAP.put("CEFAS ENDEAVOUR", "Cefas Endeavour");
		VESSEL_RENAME_MAP.put("Drifting Buoy", "Drifting buoy");
		VESSEL_RENAME_MAP.put("Drifting Bouy", "Drifting buoy");
		VESSEL_RENAME_MAP.put("Drifting buoy", "Drifting buoy");
		VESSEL_RENAME_MAP.put("G. O. Sars", "G.O. Sars");
		VESSEL_RENAME_MAP.put("GULF CHALLENGER", "Gulf Challenger");
		VESSEL_RENAME_MAP.put("HENRY B. BIGELOW", "Henry B. Bigelow");
		VESSEL_RENAME_MAP.put("Ka'imimoana", "Ka imimoana");
		VESSEL_RENAME_MAP.put("L'Astrolabe", "L Astrolabe");
		VESSEL_RENAME_MAP.put("L'Atalante", "L Atalante");
		VESSEL_RENAME_MAP.put("L'Atlante", "L Atalante");
		VESSEL_RENAME_MAP.put("L. M. Gould", "Laurence M. Gould");
		VESSEL_RENAME_MAP.put("L.M. Gould", "Laurence M. Gould");
		VESSEL_RENAME_MAP.put("PMEL/Natalie Schulte", "Natalie Schulte");
		VESSEL_RENAME_MAP.put("Ronald Brown", "Ronald H. Brown");
		VESSEL_RENAME_MAP.put("R/V AEGAEO", "R/V Aegaeo");
		VESSEL_RENAME_MAP.put("S. A. Agulhas", "S.A. Agulhas");
		VESSEL_RENAME_MAP.put("Station M", "Mooring");
		VESSEL_RENAME_MAP.put("Tethys 2", "Tethys II");
		VESSEL_RENAME_MAP.put("Tethyss II", "Tethys II");
		VESSEL_RENAME_MAP.put("Trans Future-5", "Trans Future 5");
		VESSEL_RENAME_MAP.put("Unknown", "unknown");
	}

	/**
	 * Correctly spelled "standard" vessel names that are intentionally misspelled. 
	 */
	public static final HashMap<String,String> VESSEL_NAME_CORRECTIONS = 
			new HashMap<String,String>();
	static {
		VESSEL_NAME_CORRECTIONS.put("Haakon Mosby", "H" + aRing + "kon Mosby");
		VESSEL_NAME_CORRECTIONS.put("Hesperides", "Hesp" + eAcute + "rides");
		VESSEL_NAME_CORRECTIONS.put("Ka imimoana", "Ka'imimoana");
		VESSEL_NAME_CORRECTIONS.put("L Astrolabe", "L'Astrolabe");
		VESSEL_NAME_CORRECTIONS.put("L Atalante", "L'Atalante");
	}

}
