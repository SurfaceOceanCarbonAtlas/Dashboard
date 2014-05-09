/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a QC event giving a flag (or just a comment) on a region of a cruise. 
 * 
 * @author Karl Smith
 */
public class SocatQCEvent extends SocatEvent implements Serializable, IsSerializable {

	private static final long serialVersionUID = -3545981144437072881L;

	Character regionID;

	/**
	 * Creates an empty flag
	 */
	public SocatQCEvent() {
		super();
		regionID = SocatCruiseData.CHAR_MISSING_VALUE;
	}

	/**
	 * @return 
	 * 		the region ID for this QC flag; 
	 * 		never null but may be {@link SocatCruiseData#CHAR_MISSING_VALUE}
	 */
	public Character getRegionID() {
		return regionID;
	}

	/**
	 * @param regionID 
	 * 		the region ID to set for this QC flag; 
	 * 		if null, {@link SocatCruiseData#CHAR_MISSING_VALUE} is assigned
	 */
	public void setRegionID(Character regionID) {
		if ( regionID == null )
			this.regionID = SocatCruiseData.CHAR_MISSING_VALUE;
		else
			this.regionID = regionID;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + regionID.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof SocatQCEvent) )
			return false;
		SocatQCEvent other = (SocatQCEvent) obj;

		if ( ! super.equals(other) )
			return false;
		if ( ! regionID.equals(other.regionID) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "SocatQCEvent" +
				"[\n    flag='" + flag.toString() + "'" +
				",\n    flagDate=" + flagDate.toString() + 
				",\n    expocode=" + expocode + 
				",\n    socatVersion=" + socatVersion.toString() + 
				",\n    regionID='" + regionID.toString() + "'" + 
				",\n    username=" + username + 
				",\n    realname=" + realname + 
				",\n    comment=" + comment + 
				"]";
	}

}
