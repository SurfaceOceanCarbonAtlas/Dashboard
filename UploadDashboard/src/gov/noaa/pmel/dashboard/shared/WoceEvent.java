/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a WOCE event occurring on a number of values in a data column. 
 * Note that the inherited id field is ignored in the hashCode and equals methods.
 * 
 * @author Karl Smith
 */
public class WoceEvent extends DashboardEvent implements Serializable, IsSerializable {

	private static final long serialVersionUID = 8020489025485344257L;

	protected Character flag;
	protected String woceName;
	protected String varName;
	protected ArrayList<DataLocation> locations;

	/**
	 * Creates an empty WOCE event with flag {@link DashboardUtils#WOCE_NOT_CHECKED}
	 */
	public WoceEvent() {
		super();
		flag = DashboardUtils.WOCE_NOT_CHECKED;
		woceName = DashboardUtils.STRING_MISSING_VALUE;
		varName = DashboardUtils.STRING_MISSING_VALUE;
		locations = new ArrayList<DataLocation>();
	}

	/**
	 * @return 
	 * 		the flag; 
	 * 		never null, but may be {@link DashboardUtils#WOCE_NOT_CHECKED} if not assigned
	 */
	public Character getFlag() {
		return flag;
	}

	/**
	 * @param flag
	 * 		the flag to set; 
	 * 		if null, {@link DashboardUtils#WOCE_NOT_CHECKED} is assigned
	 */
	public void setFlag(Character flag) {
		if ( flag == null )
			this.flag = DashboardUtils.WOCE_NOT_CHECKED;
		else
			this.flag = flag;
	}

	/**
	 * @return 
	 * 		the WOCE name; 
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getWoceName() {
		return woceName;
	}

	/**
	 * @param woceName
	 * 		the WOCE name to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setWoceName(String woceName) {
		if ( woceName == null )
			this.woceName = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.woceName = woceName;
	}

	/**
	 * @return 
	 * 		the data variable name in the DSG file;
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * @param varName 
	 * 		the data variable name in the DSG file to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setVarName(String varName) {
		if ( varName == null )
			this.varName = DashboardUtils.STRING_MISSING_VALUE;
		else
			this.varName = varName;
	}

	/**
	 * @return 
	 * 		the list of locations associated with this WOCE flag;
	 * 		never null, but may be empty.
	 * 		The actual ArrayList in this object is returned.
	 */
	public ArrayList<DataLocation> getLocations() {
		return locations;
	}

	/**
	 * @param locations 
	 * 		the locations to set;  if null, the set of locations 
	 * 		is cleared.  The actual objects in the provided list 
	 * 		are reused (shallow copy).
	 */
	public void setLocations(ArrayList<DataLocation> locations) {
		this.locations.clear();
		if ( locations != null )
			this.locations.addAll(locations);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + flag.hashCode();
		result = result * prime + woceName.hashCode();
		result = result * prime + varName.hashCode();
		result = result * prime + locations.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof WoceEvent) )
			return false;
		WoceEvent other = (WoceEvent) obj;

		if ( ! super.equals(other) )
			return false;
		if ( ! flag.equals(other.flag) )
			return false;
		if ( ! woceName.equals(other.woceName) )
			return false;
		if ( ! varName.equals(other.varName) )
			return false;
		if ( ! locations.equals(other.locations) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "WoceEvent" +
				"[\n    id=" + id.toString() +
				",\n    woceName=" + woceName +
				",\n    flag='" + flag.toString() + "'" +
				",\n    flagDate=" + flagDate.toString() + 
				",\n    expocode=" + expocode + 
				",\n    version=" + version.toString() + 
				",\n    varName=" + varName.toString() + 
				",\n    locations=" + locations.toString() + 
				",\n    username=" + username + 
				",\n    realname=" + realname + 
				",\n    comment=" + comment + 
				"]";
	}

}
