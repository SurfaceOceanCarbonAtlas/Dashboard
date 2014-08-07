/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a WOCE event occurring on a number of values in a data column. 
 * 
 * @author Karl Smith
 */
public class SocatWoceEvent extends SocatEvent implements Serializable, IsSerializable {

	private static final long serialVersionUID = 1084266395207635831L;

	String dataVarName;
	ArrayList<DataLocation> locations;

	/**
	 * Creates an empty flag
	 */
	public SocatWoceEvent() {
		super();
		dataVarName = "";
		locations = new ArrayList<DataLocation>();
	}

	/**
	 * @return 
	 * 		the data variable name in the DSG file;
	 * 		never null but may be empty
	 */
	public String getDataVarName() {
		return dataVarName;
	}

	/**
	 * @param dataVarName 
	 * 		the data variable name in the DSG file to set;
	 * 		if null, an empty string is assigned.
	 */
	public void setDataVarName(String dataVarName) {
		if ( dataVarName == null )
			this.dataVarName = "";
		else
			this.dataVarName = dataVarName;
	}

	/**
	 * @return 
	 * 		the list of locations associated with this WOCE flag;
	 * 		never null but may be empty.  The actual ArrayList 
	 * 		in this object is returned.
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
		result = result * prime + dataVarName.hashCode();
		result = result * prime + locations.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof SocatWoceEvent) )
			return false;
		SocatWoceEvent other = (SocatWoceEvent) obj;

		if ( ! super.equals(other) )
			return false;
		if ( ! dataVarName.equals(other.dataVarName) )
			return false;
		if ( ! locations.equals(other.locations) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "SocatWoceEvent" +
				"[\n    flag='" + flag.toString() + "'" +
				",\n    flagDate=" + flagDate.toString() + 
				",\n    expocode=" + expocode + 
				",\n    socatVersion=" + socatVersion.toString() + 
				",\n    dataVarName=" + dataVarName.toString() + 
				",\n    locations=" + locations.toString() + 
				",\n    username=" + username + 
				",\n    realname=" + realname + 
				",\n    comment=" + comment + 
				"]";
	}

}
