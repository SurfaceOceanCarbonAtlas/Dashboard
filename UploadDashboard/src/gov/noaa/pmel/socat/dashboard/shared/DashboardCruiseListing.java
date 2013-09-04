/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a list of uploaded cruises from a user
 * 
 * @author Karl Smith
 */
public class DashboardCruiseListing implements Serializable {

	private static final long serialVersionUID = -5585489320536194447L;

	String username;
	ArrayList<DashboardCruise> cruises;

	/**
	 * Creates without a user and without any cruises
	 */
	public DashboardCruiseListing() {
		username = "";
		cruises = new ArrayList<DashboardCruise>();
	}

	/**
	 * @return 
	 * 		the username; may be blank but will never be null
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
	 * 		the list of cruises; may be empty but will never be null.
	 * 		The actual list contained in this instance is returned.
	 */
	public ArrayList<DashboardCruise> getCruises() {
		return cruises;
	}

	/**
	 * @param cruises 
	 * 		the list of cruises to set; 
	 * 		if null, just clears the list of cruises
	 */
	public void setCruises(ArrayList<DashboardCruise> cruises) {
		this.cruises.clear();
		if ( cruises != null )
			this.cruises.addAll(cruises);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = this.username.hashCode();
		result = result * prime + cruises.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof DashboardCruiseListing) )
			return false;
		DashboardCruiseListing other = (DashboardCruiseListing) obj;

		if ( ! username.equals(other.username) )
			return false;

		if ( ! cruises.equals(other.cruises) )
			return false;

		return true;
	}

}
