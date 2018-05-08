/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a list of uploaded datasets for a user,
 * mapped by dataset ID.
 * 
 * @author Karl Smith
 */
public class DashboardDatasetList extends HashMap<String,DashboardDataset> implements Serializable, IsSerializable {

	private static final long serialVersionUID = 8265872123615522516L;

	protected String username;
	// The following indicates whether or not the above user 
	// has manager or admin privileges; a bit of a kludge.
	protected boolean manager;

	/**
	 * Creates without a user or any cruises
	 */
	public DashboardDatasetList() {
		super();
		username = DashboardUtils.STRING_MISSING_VALUE;
		manager = false;
	}

	/**
	 * @return 
	 * 		the username; 
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username 
	 * 		the username to set; 
	 * 		if null, sets to {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public void setUsername(String username) {
		if ( username == null )
			this.username = DashboardUtils.STRING_MISSING_VALUE;
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

		if ( ! (obj instanceof DashboardDatasetList) )
			return false;
		DashboardDatasetList other = (DashboardDatasetList) obj;

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
		String repr = "DashboardDatasetList[ username=" + username + 
					  ",\n    manager=" + Boolean.valueOf(manager).toString();
		for ( String cruiseId : keySet() )
			repr += ",\n    " + cruiseId + ":" + get(cruiseId).toString();
		repr += " ]";
		return repr;
	}

}
