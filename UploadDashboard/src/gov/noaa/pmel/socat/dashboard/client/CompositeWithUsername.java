/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.client;

import com.google.gwt.user.client.ui.Composite;

/**
 * A Composite with a username property.
 * 
 * @author Karl Smith
 */
public class CompositeWithUsername extends Composite {

	private String username = "";

	/**
	 * @return 
	 * 		the username; never null but may be empty
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username 
	 * 		the username to set; if null, an empty string is assigned
	 */
	public void setUsername(String username) {
		if ( username == null )
			this.username = "";
		else
			this.username = username;
	}

}
