/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a list of uploaded cruises for a user,
 * mapped by cruise expocode.
 * 
 * @author Karl Smith
 */
public class DashboardCruiseList extends HashMap<String,DashboardCruise> 
								implements Serializable, IsSerializable {

	private static final long serialVersionUID = -8484865552946287748L;

	String username;
	// The following indicated whether or not the above user 
	// has manager or admin privileges; a bit of a hack.
	boolean manager;

	/**
	 * Creates without a user or any cruises
	 */
	public DashboardCruiseList() {
		super();
		username = "";
		manager = false;
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

	/**
	 * @return 
	 * 		if this user is a manager/admin
	 */
	public boolean isManager() {
		return manager;
	}

	/**
	 * @param manager 
	 * 		set if this user is a manager/admin
	 */
	public void setManager(boolean manager) {
		this.manager = manager;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = username.hashCode();
		result = result * prime + Boolean.valueOf(manager).hashCode();
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

		if ( manager != other.manager )
			return false;

		if ( ! super.equals(other) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		String repr = "DashboardCruiseList[ username=" + username;
		repr += ", \n    manager=" + Boolean.valueOf(manager).toString();
		for ( String expocode : keySet() ) {
			repr += ", \n    " + expocode + ":" + get(expocode).toString();
		}
		repr += " ]";
		return repr;
	}

}
