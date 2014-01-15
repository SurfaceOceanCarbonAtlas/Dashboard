/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents the metadata files for a cruise requested by a user. 
 * The keys of the map are the metadata filenames. 
 * 
 * @author Karl Smith
 */
public class DashboardMetadataList extends HashMap<String,DashboardMetadata> 
								implements Serializable, IsSerializable {

	private static final long serialVersionUID = 1648041641148179121L;

	String username;
	String omeFilename;

	/**
	 * Creates without a user and without any metadata files
	 */
	public DashboardMetadataList() {
		super();
		username = "";
		omeFilename = "";
	}

	/**
	 * @return 
	 * 		the username; never null, but may be empty
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username 
	 * 		the username to set; if null, an empty string is assigned
	 */
	public void setUsername(String username) {
		if ( username != null )
			this.username = username;
		else
			this.username = "";
	}

	/**
	 * @return 
	 * 		the OME metadata filename; never null, but may be empty
	 */
	public String getOmeFilename() {
		return omeFilename;
	}

	/**
	 * @param username 
	 * 		the username to set; if null, an empty string is assigned
	 */
	public void setOmeFilename(String omeFilename) {
		if ( omeFilename != null )
			this.omeFilename = omeFilename;
		else
			this.omeFilename = "";
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = username.hashCode();
		result = result * prime + omeFilename.hashCode();
		result = result * prime + super.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof DashboardMetadataList) )
			return false;
		DashboardMetadataList other = (DashboardMetadataList) obj;

		if ( ! username.equals(other.username) )
			return false;

		if ( ! omeFilename.equals(other.omeFilename) )
			return false;

		if ( ! super.equals(other) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		String repr = "DashboardMetadataList" +
				"[ username=" + username +
				",\n    omeFilename=" + omeFilename;
		for ( String expoFilename : keySet() ) {
			repr += ",\n    " + expoFilename + ":" + get(expoFilename).toString();
		}
		repr += " ]";
		return repr;
	}

}
