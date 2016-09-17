/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Types of the data columns in a user-provided cruise data file.
 * 
 * @author Karl Smith
 */
public class DataColumnType implements Serializable, IsSerializable {

	private static final long serialVersionUID = 8785256066854181603L;

	/**
	 * UNKNOWN needs to be respecified as one of the (other) data column types.
	 */
	public static final DataColumnType UNKNOWN = new DataColumnType("(unknown)", null);

	private String name;
	private ArrayList<String> units;

	/**
	 * Create an empty data column type 
	 * (empty name string and empty unit string)
	 */
	public DataColumnType() {
		this.name = "";
		// Use the default initial capacity since a name and units should be added
		this.units = new ArrayList<String>();
		this.units.add("");
	}

	/**
	 * Create a data column type with the given name and list of units.
	 * 
	 * @param name
	 * 		name of the data column type; 
	 * 		if null, an empty string is assigned
	 * @param units
	 * 		unit strings associated with this type;
	 * 		if null or empty, a single empty unit string is assigned
	 */
	public DataColumnType(String name, Collection<String> units) {
		if ( name != null ) {
			this.name = name;
		}
		else {
			this.name = "";
		}
		if ( (units != null) && (units.size() > 0) ) {
			this.units = new ArrayList<String>(units);
		}
		else {
			// Assume no units are going to be added
			this.units = new ArrayList<String>(1);
			this.units.add("");
		}
	}

	/**
	 * @return 
	 * 		the name for this data column type;
	 * 		never null but may be empty
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 
	 * 		the name of this data column type to set;
	 * 		if null, an empty string is assigned
	 */
	public void setName(String name) {
		if ( name != null ) {
			this.name = name;
		}
		else {
			this.name = "";
		}
	}

	/**
	 * @return 
	 * 		the units associated with this data column type;
	 * 		never null or empty, but may have a single empty string.
	 * 		The actual ArrayList in this object is returned.
	 */
	public ArrayList<String> getUnits() {
		return units;
	}

	/**
	 * @param units 
	 * 		the list of units to associate with this data column type; 
	 * 		if null or empty, a single empty unit string is assigned
	 */
	public void setUnits(Collection<String> units) {
		this.units.clear();
		if ( (units != null) && (units.size() > 0) ) {
			this.units.addAll(units);
		}
		else {
			this.units.add("");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = name.hashCode();
		result = result * prime + units.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		if ( ! (obj instanceof DataColumnType) )
			return false;

		DataColumnType other = (DataColumnType) obj;
		if ( ! name.equals(other.name) )
			return false;
		if ( ! units.equals(other.units) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataColumnType[name=" + name + ", units=" + units.toString() + "]";
	}

}
