/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.HashSet;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Set of sanity checker SCMessages for a cruise, 
 * along with some cruise information.
 * 
 * @author Karl Smith
 */
public class SCMessageList extends HashSet<SCMessage> 
							implements Serializable, IsSerializable {

	private static final long serialVersionUID = 4794968047533549199L;

	private String username;
	private String expocode;

	public SCMessageList() {
		super();
		username = "";
		expocode = "";
	}

	/**
	 * @return 
	 * 		the username; never null but may be empty
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username 
	 * 		the username to set;
	 * 		if null, an empty string is assigned
	 */
	public void setUsername(String username) {
		if ( username == null )
			this.username = "";
		else
			this.username = username;
	}

	/**
	 * @return 
	 * 		the cruise expocode; never null but may be empty
	 */
	public String getExpocode() {
		return expocode;
	}

	/**
	 * @param expocode 
	 * 		the expocode to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setExpocode(String expocode) {
		if ( expocode == null )
			this.expocode = "";
		else
			this.expocode = expocode;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + expocode.hashCode();
		result = result * prime + username.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof SCMessageList) )
			return false;
		SCMessageList other = (SCMessageList) obj;

		if ( ! expocode.equals(other.expocode) )
			return false;
		if ( ! username.equals(other.username) )
			return false;
		if ( ! super.equals(other) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SCMessageList" +
				"[\n    username=" + username + 
				",\n    expocode=" + expocode + 
				",\n    " + super.toString() + 
				" \n]";
	}

}
