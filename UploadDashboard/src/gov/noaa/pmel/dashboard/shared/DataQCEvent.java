/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents QC of some number of data values in a data column. 
 * Note that the inherited id field is ignored in the hashCode and equals methods.
 * 
 * @author Karl Smith
 */
public class DataQCEvent extends QCEvent implements Serializable, IsSerializable {

	private static final long serialVersionUID = 5186999389519204821L;

	protected String varName;
	protected ArrayList<DataLocation> locations;

	/**
	 * Creates an empty data QC event where id is zero, the flag date 
	 * is {@link DashboardUtils#DATE_MISSING_VALUE}, there are 
	 * no data locations, and all other values (strings) are 
	 * {@link DashboardUtils#STRING_MISSING_VALUE}.
	 */
	public DataQCEvent() {
		super();
		varName = DashboardUtils.STRING_MISSING_VALUE;
		locations = new ArrayList<DataLocation>();
	}

	/**
	 * @return 
	 * 		the flagged data variable name in the DSG file;
	 * 		never null, but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * @param varName 
	 * 		the flagged data variable name in the DSG file to set;
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
	 * 		the list of locations associated with this data QC event;
	 * 		never null, but may be empty.
	 * 		The actual ArrayList in this object is returned.
	 */
	public ArrayList<DataLocation> getLocations() {
		return locations;
	}

	/**
	 * @param locations 
	 * 		the locations to set;  
	 * 		the list of locations is cleared and then, 
	 * 		if locations is not null, the given locations are added to the list.  
	 * 		The actual objects in the provided list are reused (shallow copy).
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

		if ( ! (obj instanceof DataQCEvent) )
			return false;
		DataQCEvent other = (DataQCEvent) obj;

		if ( ! super.equals(other) )
			return false;
		if ( ! varName.equals(other.varName) )
			return false;
		if ( ! locations.equals(other.locations) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "DataQCEvent" +
				"[\n    id=" + id.toString() +
				",\n    flagDate=" + flagDate.toString() + 
				",\n    flagName=" + flagName + 
				",\n    flagValue='" + flagValue + "'" +
				",\n    datasetId=" + datasetId + 
				",\n    version=" + version + 
				",\n    username=" + username + 
				",\n    realname=" + realname + 
				",\n    comment=" + comment + 
				",\n    varName=" + varName + 
				",\n    locations=" + locations.toString() + 
				"]";
	}

}
