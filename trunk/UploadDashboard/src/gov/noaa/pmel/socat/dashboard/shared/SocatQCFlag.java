/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class for a SOCAT QC flag. 
 * Also the parent class for a SOCAT WOCE flag.
 * 
 * @author Karl Smith
 */
public class SocatQCFlag implements Serializable, IsSerializable {

	private static final long serialVersionUID = -8566869393964074443L;

	Character flag;
	String expocode;
	Double socatVersion;
	Character regionID;
	Date flagDate;
	String reviewer;
	String comment;

	/**
	 * Creates an empty flag
	 */
	public SocatQCFlag() {
		flag = SocatCruiseData.CHAR_MISSING_VALUE;
		expocode = "";
		socatVersion = 0.0;
		regionID = SocatCruiseData.CHAR_MISSING_VALUE;
		flagDate = SocatMetadata.DATE_MISSING_VALUE;
		reviewer = "";
		comment = "";
	}

	/**
	 * @return 
	 * 		the flag; never null but may be {@link SocatCruiseData#CHAR_MISSING_VALUE}
	 */
	public Character getFlag() {
		return flag;
	}

	/**
	 * @param flag 
	 * 		the flag to set; if null {@link SocatCruiseData#CHAR_MISSING_VALUE} is assigned
	 */
	public void setFlag(Character flag) {
		if ( flag == null )
			this.flag = SocatCruiseData.CHAR_MISSING_VALUE;
		else
			this.flag = flag;
	}

	/**
	 * @return 
	 * 		the expocode; never null but may be empty
	 */
	public String getExpocode() {
		return expocode;
	}

	/**
	 * @param expocode 
	 * 		the expocode to set; if null, a empty string is assigned
	 */
	public void setExpocode(String expocode) {
		if ( expocode == null )
			this.expocode = "";
		else
			this.expocode = expocode;
	}

	/**
	 * @return 
	 * 		the SOCAT version; never null but may be zero
	 */
	public Double getSocatVersion() {
		return socatVersion;
	}

	/**
	 * @param socatVersion 
	 * 		the SOCAT version to set; if null or negative, zero is assigned
	 */
	public void setSocatVersion(Double socatVersion) {
		if ( (socatVersion == null) || (socatVersion < 0.0) )
			this.socatVersion = 0.0;
		else
			this.socatVersion = socatVersion;
	}

	/**
	 * @return 
	 * 		the region ID; never null but may be {@link SocatCruiseData#CHAR_MISSING_VALUE}
	 */
	public Character getRegionID() {
		return regionID;
	}

	/**
	 * @param regionID 
	 * 		the region ID to set; if null, {@link SocatCruiseData#CHAR_MISSING_VALUE} is assigned
	 */
	public void setRegionID(Character regionID) {
		if ( regionID == null )
			this.regionID = SocatCruiseData.CHAR_MISSING_VALUE;
		else
			this.regionID = regionID;
	}

	/**
	 * @return 
	 * 		the date of the flag; never null 
	 * 		but may be {@link SocatMetadata#DATE_MISSING_VALUE}
	 */
	public Date getFlagDate() {
		return flagDate;
	}

	/**
	 * @param flagDate 
	 * 		the date of the flag to set; if null, {@link SocatMetadata#DATE_MISSING_VALUE}
	 */
	public void setFlagDate(Date flagDate) {
		if ( flagDate == null )
			this.flagDate = SocatMetadata.DATE_MISSING_VALUE;
		else
			this.flagDate = flagDate;
	}

	/**
	 * @return 
	 * 		the reviewer; never null but may be empty
	 */
	public String getReviewer() {
		return reviewer;
	}

	/**
	 * @param reviewer 
	 * 		the reviewer to set; if null, an empty string is assigned
	 */
	public void setReviewer(String reviewer) {
		if ( reviewer == null )
			this.reviewer = "";
		else
			this.reviewer = reviewer;
	}

	/**
	 * @return 
	 * 		the comment; never null but may be empty
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment 
	 * 		the comment to set; if null an empty string is assigned
	 */
	public void setComment(String comment) {
		if ( comment == null )
			this.comment = "";
		else
			this.comment = comment;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = flag.hashCode();
		result = result * prime + expocode.hashCode();
		// Ignore socatVersion as it is floating point and does not have to be exact
		result = result * prime + regionID.hashCode();
		result = result * prime + flagDate.hashCode();
		result = result * prime + reviewer.hashCode();
		result = result * prime + comment.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof SocatQCFlag) )
			return false;
		SocatQCFlag other = (SocatQCFlag) obj;

		if ( ! flagDate.equals(other.flagDate) )
			return false;
		if ( ! flag.equals(other.flag) )
			return false;
		if ( ! expocode.equals(other.expocode) )
			return false;
		if ( ! regionID.equals(other.regionID) )
			return false;
		if ( ! reviewer.equals(other.reviewer) )
			return false;
		if ( ! comment.equals(other.comment) )
			return false;
		if ( ! DashboardUtils.closeTo(socatVersion, other.socatVersion, 0.0, 1.0E-3) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "SocatQCFlag" +
				"[\n    flag='" + flag.toString() + "'" +
				",\n    expocode=" + expocode + 
				",\n    socatVersion=" + String.format("%#.1f", socatVersion) + 
				",\n    regionID='" + regionID.toString() + "'" + 
				",\n    flagDate=" + flagDate.toString() + 
				",\n    reviewer=" + reviewer + 
				",\n    comment=" + comment + 
				"]";
	}

}
