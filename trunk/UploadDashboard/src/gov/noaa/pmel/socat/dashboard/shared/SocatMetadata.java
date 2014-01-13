/**
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class for working with metadata values of interest from SOCAT. 
 * 
 * @author Karl Smith
 */
public class SocatMetadata implements Serializable, IsSerializable {

	private static final long serialVersionUID = -8760815956964862162L;

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
		beginTime = DashboardUtils.INVALID_DATE;
		endTime = DashboardUtils.INVALID_DATE;
		scienceGroup = "";
		origDOI = "";
		metadataHRef = "";
		socatDOI = "";
		socatDOIHRef = "";
		cruiseFlag = "";
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
	 * 		never null but could be {@link DashboardUtils#INVALID_DATE} if not assigned.
	 */
	public Date getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime 
	 * 		the beginning time for the cruise to set;
	 * 		if null, {@link DashboardUtils#INVALID_DATE} is assigned
	 */
	public void setBeginTime(Date beginTime) {
		if ( beginTime == null )
			this.beginTime = DashboardUtils.INVALID_DATE;
		else 
			this.beginTime = beginTime;
	}

	/**
	 * @return
	 * 		the ending time for the cruise;
	 * 		never null but could be {@link DashboardUtils#INVALID_DATE} if not assigned.
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime 
	 * 		the ending time for the cruise to set;
	 * 		if null, {@link DashboardUtils#INVALID_DATE} is assigned
	 */
	public void setEndTime(Date endTime) {
		if ( endTime == null )
			this.endTime = DashboardUtils.INVALID_DATE;
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

}
