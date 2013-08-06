/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents an uploaded cruise and its current status.
 * 
 * @author Karl Smith
 */
public class DashboardCruise implements IsSerializable {

	String cruiseExpocode;

	/**
	 * @return 
	 * 		the cruise expocode
	 */
	public String getCruiseExpocode() {
		return this.cruiseExpocode;
	}

	/**
	 * @param cruiseExpocode 
	 * 		the cruise expocode to set
	 */
	public void setCruiseExpocode(String cruiseExpocode) {
		this.cruiseExpocode = cruiseExpocode;
	}

}
