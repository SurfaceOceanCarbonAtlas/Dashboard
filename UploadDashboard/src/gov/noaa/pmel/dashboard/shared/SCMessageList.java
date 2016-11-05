/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Set of sanity checker SCMessages for a cruise, 
 * along with some cruise information.
 * 
 * @author Karl Smith
 */
public class SCMessageList extends HashSet<SCMessage> implements Serializable, IsSerializable {

	private static final long serialVersionUID = 2963465761569124080L;

	protected String username;
	protected String expocode;
	protected ArrayList<String> summaries;

	public SCMessageList() {
		super();
		username = DashboardUtils.STRING_MISSING_VALUE;
		expocode = DashboardUtils.STRING_MISSING_VALUE;
		summaries = new ArrayList<String>();
	}

	/**
	 * @return 
	 * 		the username; 
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username 
	 * 		the username to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setUsername(String username) {
		if ( username == null )
			this.username = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.username = username;
	}

	/**
	 * @return 
	 * 		the cruise expocode; 
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getExpocode() {
		return expocode;
	}

	/**
	 * @param expocode 
	 * 		the expocode to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setExpocode(String expocode) {
		if ( expocode == null )
			this.expocode = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.expocode = expocode;
	}

	/**
	 * @return 
	 * 		the summary messages; never null but may be empty.
	 * 		The actual list contained in this object is returned.
	 */
	public ArrayList<String> getSummaries() {
		return summaries;
	}

	/**
	 * @param summaries 
	 * 		the summary messages to assign.  The current list of
	 * 		summary messages is cleared, and then the contents of
	 * 		this list, if not null, is added to the list.
	 */
	public void setSummaries(ArrayList<String> summaries) {
		this.summaries.clear();
		if ( summaries != null )
			this.summaries.addAll(summaries);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + expocode.hashCode();
		result = result * prime + username.hashCode();
		result = result * prime + summaries.hashCode();
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
		if ( ! summaries.equals(other.summaries) )
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
				",\n    summaries=" + summaries.toString() + 
				",\n    " + super.toString() + 
				" \n]";
	}

}
