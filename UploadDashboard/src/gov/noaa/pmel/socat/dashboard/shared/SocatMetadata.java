/**
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class for working with metadata values of interest from SOCAT,
 * including those derived from cruise data.
 * 
 * @author Karl Smith
 */
public class SocatMetadata implements Serializable, IsSerializable {

	private static final long serialVersionUID = 8632155495490906964L;

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
	String socatVersion;
	String allRegionIDs;
	String socatDOI;
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
		socatVersion = "";
		allRegionIDs = "";
		socatDOI = "";
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
	 * The SOCAT version number and status is the SOCAT version number 
	 * followed by an 'N', indicating the dataset is new in this
	 * SOCAT version, or a 'U', indicating the dataset is an update
	 * from a previous SOCAT version.  Updates within a SOCAT version
	 * do NOT change an 'N' to a 'U'.
	 * 
	 * @return
	 * 		the SOCAT version number and status associated with this instance;
	 * 		never null but could be empty if not assigned
	 */
	public String getSocatVersion() {
		return socatVersion;
	}

	/**
	 * The SOCAT version number and status is the SOCAT version number 
	 * followed by an 'N', indicating the dataset is new in this
	 * SOCAT version, or a 'U', indicating the dataset is an update
	 * from a previous SOCAT version.  Updates within a SOCAT version
	 * do NOT change an 'N' to a 'U'.
	 * 
	 * @param socatVersion 
	 * 		the SOCAT version number and status to set; 
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
	 * 		the String of all region IDs;
	 * 		never null but could be a empty string if not assigned
	 */
	public String getAllRegionIDs() {
		return allRegionIDs;
	}

	/**
	 * @param allRegionIDs
	 * 		the String of all region IDs to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setAllRegionIDs(String allRegionIDs) {
		if ( allRegionIDs == null )
			this.allRegionIDs = "";
		else
			this.allRegionIDs = allRegionIDs;
	}

	/**
	 * @return
	 * 		the SOCAT DOI for this dataset;
	 * 		never null but could be a empty string if not assigned
	 */
	public String getSocatDOI() {
		return socatDOI;
	}

	/**
	 * @param socatDOI
	 * 		the SOCAT DOI for this dataset to set; 
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
	 * 		the maximum length of String values given in the fields 
	 * 		of this instance, or 16, whichever is larger.
	 */
	public int getMaxStringLength() {
		int maxLength = 16;
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
		if ( maxLength < socatVersion.length() )
			maxLength = socatVersion.length();
		if ( maxLength < allRegionIDs.length() )
			maxLength = allRegionIDs.length();
		if ( maxLength < socatDOI.length() )
			maxLength = socatDOI.length();
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
		result = result * prime + socatVersion.hashCode();
		result = result * prime + allRegionIDs.hashCode();
		result = result * prime + socatDOI.hashCode();
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
		if ( ! socatVersion.equals(other.socatVersion) )
			return false;
		if ( ! allRegionIDs.equals(other.allRegionIDs) )
			return false;
		if ( ! socatDOI.equals(other.socatDOI) )
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
				",\n    socatVersion=" + socatVersion + 
				",\n    allRegionIDs=" + allRegionIDs + 
				",\n    socatDOI=" + socatDOI + 
				",\n    qcFlag=" + qcFlag + 
				" ]";
	}

}
