/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.HashSet;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a list of cruise metadata files for a user.
 * 
 * @author Karl Smith
 */
public class DashboardMetadataList extends HashSet<DashboardMetadata> 
								implements Serializable, IsSerializable {

	private static final long serialVersionUID = 8821642387470578963L;

	String username;

	/**
	 * Creates without a user and without any metadata files
	 */
	public DashboardMetadataList() {
		super();
		username = "";
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

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = username.hashCode();
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

		if ( ! super.equals(other) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		String repr = "DashboardMetadataList[ username=" + username;
		for ( DashboardMetadata mdata : this ) {
			repr += ", \n    " + mdata.toString();
		}
		repr += " ]";
		return repr;
	}

}
