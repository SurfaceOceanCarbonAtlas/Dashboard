/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents the OME metadata and the additional documents for a cruise. 
 * The keys of the map are the additional document filenames.  The OME
 * metadata file is NOT part of the additional documents list.
 * 
 * @author Karl Smith
 */
public class DashboardMetadataList extends HashMap<String,DashboardMetadata> 
								implements Serializable, IsSerializable {

	private static final long serialVersionUID = -174019076584974241L;

	String username;
	DashboardMetadata omeMetadata;

	/**
	 * Creates without a user and without any metadata files
	 */
	public DashboardMetadataList() {
		super();
		username = "";
		omeMetadata = null;
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
	 * 		the OME metadata; can be null
	 */
	public DashboardMetadata getOmeMetadata() {
		return omeMetadata;
	}

	/**
	 * @param omeMetadata 
	 * 		the OME metadata to set
	 */
	public void setOmeMetadata(DashboardMetadata omeMetadata) {
		this.omeMetadata = omeMetadata;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = username.hashCode();
		if ( omeMetadata != null )
			result = result * prime + omeMetadata.hashCode();
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

		if ( omeMetadata != null ) {
			if ( ! omeMetadata.equals(other.omeMetadata) )
				return false;
		}
		else if ( other.omeMetadata != null )
			return false;

		if ( ! super.equals(other) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		String repr = "DashboardMetadataList" +
				"[ username=" + username +
				",\n    omeMetadata=" + omeMetadata;
		for ( String expoFilename : keySet() ) {
			repr += ",\n    " + expoFilename + ":" + get(expoFilename).toString();
		}
		repr += " ]";
		return repr;
	}

}
