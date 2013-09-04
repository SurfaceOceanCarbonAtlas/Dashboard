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

	private static final long serialVersionUID = 4785180378067329280L;

	String username;
	ArrayList<DashboardCruise> cruises;

	/**
	 * Create without a user and without any cruises
	 */
	public DashboardCruiseListing() {
		this.username = null;
		this.cruises = null;
	}

	/**
	 * Create for the given user and list of cruises
	 * 
	 * @param username
	 * @param cruises
	 */
	public DashboardCruiseListing(String username, 
					ArrayList<DashboardCruise> cruises) {
		this.username = username;
		this.cruises = cruises;
	}

	/**
	 * @return 
	 * 		the username; may be null
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @param username 
	 * 		the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return 
	 * 		the list of cruises; may be null
	 */
	public ArrayList<DashboardCruise> getCruises() {
		return this.cruises;
	}

	/**
	 * @param cruises 
	 * 		the list of cruises to set
	 */
	public void setCruises(ArrayList<DashboardCruise> cruises) {
		this.cruises = cruises;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = 0;
		if ( username != null )
			result = this.username.hashCode();
		result *= prime;
		if ( cruises != null )
			result += cruises.hashCode();
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

		if ( username == null ) {
			if ( other.username != null )
				return false;
		} 
		else if ( ! username.equals(other.username) )
			return false;

		if ( cruises == null ) {
			if ( other.cruises != null )
				return false;
		} 
		else if ( ! cruises.equals(other.cruises) )
			return false;

		return true;
	}

}
