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

	private static final long serialVersionUID = 1150858903836366315L;

	DataColumnType dataType;
	String columnName;
	ArrayList<DatumLocation> locations;

	/**
	 * Creates an empty flag
	 */
	public SocatWoceEvent() {
		super();
		dataType = DataColumnType.UNKNOWN;
		columnName = "";
		locations = new ArrayList<DatumLocation>();
	}

	/**
	 * @return 
	 * 		the data type; 
	 * 		never null but may be {@link DataColumnType#UNKNOWN}
	 */
	public DataColumnType getDataType() {
		return dataType;
	}

	/**
	 * @param dataType 
	 * 		the data type to set;
	 * 		if null, {@link DataColumnType#UNKNOWN} is assigned.
	 */
	public void setDataType(DataColumnType dataType) {
		if ( dataType == null )
			this.dataType = DataColumnType.UNKNOWN;
		else
			this.dataType = dataType;
	}

	/**
	 * @return 
	 * 		the data column name;
	 * 		never null but may be empty
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName 
	 * 		the data column name to set;
	 * 		if null, an empty string is assigned.
	 */
	public void setColumnName(String columnName) {
		if ( columnName == null )
			this.columnName = "";
		else
			this.columnName = columnName;
	}

	/**
	 * @return 
	 * 		the list of locations associated with this WOCE flag;
	 * 		never null but may be empty.  The actual ArrayList 
	 * 		in this object is returned.
	 */
	public ArrayList<DatumLocation> getLocations() {
		return locations;
	}

	/**
	 * @param locations 
	 * 		the locations to set;  if null, the set of locations 
	 * 		is cleared.  The actual objects in the provided list 
	 * 		are reused (shallow copy).
	 */
	public void setLocations(ArrayList<DatumLocation> locations) {
		this.locations.clear();
		if ( locations != null )
			this.locations.addAll(locations);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = super.hashCode();
		result = result * prime + dataType.hashCode();
		result = result * prime + columnName.hashCode();
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
		if ( ! dataType.equals(other.dataType) )
			return false;
		if ( ! columnName.equals(other.columnName) )
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
				",\n    dataType=" + dataType.toString() + 
				",\n    columnName=" + columnName.toString() + 
				",\n    locations=" + locations.toString() + 
				",\n    username=" + username + 
				",\n    realname=" + realname + 
				",\n    comment=" + comment + 
				"]";
	}

}
