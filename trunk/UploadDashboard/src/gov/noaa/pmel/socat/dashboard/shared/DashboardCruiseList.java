/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.HashMap;

/**
 * Represents a list of uploaded cruises for a user,
 * mapped by cruise expocode.
 * 
 * @author Karl Smith
 */
public class DashboardCruiseList extends HashMap<String,DashboardCruise> {

	private static final long serialVersionUID = 4798888572810557981L;

	String username;

	/**
	 * Creates without a user or any cruises
	 */
	public DashboardCruiseList() {
		super();
		username = "";
	}

	/**
	 * @return 
	 * 		the username; never null, but may be blank
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username 
	 * 		the username to set; if null, set to an empty string
	 */
	public void setUsername(String username) {
		if ( username == null )
			this.username = "";
		else
			this.username = username;
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

		if ( ! (obj instanceof DashboardCruiseList) )
			return false;
		DashboardCruiseList other = (DashboardCruiseList) obj;

		if ( ! username.equals(other.username) )
			return false;

		if ( ! super.equals(other) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		String repr = "DashboardCruiseList[ username=" + username;
		for ( String expocode : keySet() ) {
			repr += ", \n    " + expocode + ":" + get(expocode).toString();
		}
		repr += " ]";
		return repr;
	}

}
