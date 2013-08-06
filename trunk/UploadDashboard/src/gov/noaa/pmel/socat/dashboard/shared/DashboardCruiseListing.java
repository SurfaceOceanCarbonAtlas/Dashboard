/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a list of uploaded cruises from a user
 * 
 * @author Karl Smith
 */
public class DashboardCruiseListing implements IsSerializable {

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
	public DashboardCruiseListing(String username, ArrayList<DashboardCruise> cruises) {
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

}
