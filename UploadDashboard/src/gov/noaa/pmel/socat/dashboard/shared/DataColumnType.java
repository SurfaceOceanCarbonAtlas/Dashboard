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
 * Includes a list of possible units for this data type as well as
 * the selected unit and missing value(s).
 * 
 * @author Karl Smith
 */
public class DataColumnType implements Serializable, IsSerializable {

	private static final long serialVersionUID = 6936360462743605945L;

	/**
	 * UNKNOWN needs to be respecified as one of the (other) data column types.
	 */
	public static final DataColumnType UNKNOWN = new DataColumnType("(unknown)", null);

	/**
	 * OTHER is for supplementary data in the user's original data file but 
	 * otherwise not used.  A description of each column with this type must 
	 * be part of the metadata, but the values are not validated or used. 
	 * Multiple columns may have this type.
	 */
	public static final DataColumnType OTHER = new DataColumnType("other", null);

	private String name;
	private ArrayList<String> units;
	private Integer selectedUnit;
	private String selectedMissingValue;

	/**
	 * Create an empty data column type: 
	 * 		data type name is an empty string, 
	 * 		units list only contains an empty string,
	 * 		selected units is the first unit in the list (0),
	 * 		selected missing value is an empty string (default missing values). 
	 */
	public DataColumnType() {
		this.name = "";
		// Use the default initial capacity since a name and units should be added
		this.units = new ArrayList<String>();
		this.units.add("");
		this.selectedUnit = Integer.valueOf(0);
		this.selectedMissingValue = "";
	}

	/**
	 * Create a data column type with the given name and list of units.
	 * The select unit is the first unit is the list (0) and
	 * the selected missing value is an empty string (default missing values).
	 * 
	 * @param name
	 * 		name of the data column type; 
	 * 		if null, an empty string is assigned
	 * @param units
	 * 		unit strings associated with this type;
	 * 		if null or empty, a list with a single empty unit string is assigned
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
		this.selectedUnit = Integer.valueOf(0);
		this.selectedMissingValue = "";
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

	/**
	 * @return 
	 * 		the index in the units list of the selected unit
	 */
	public Integer getSelectedUnit() {
		return selectedUnit;
	}

	/**
	 * @param selectedUnit 
	 * 		the index in the units list of the selected unit to set;
	 * 		if null, zero is assigned
	 */
	public void setSelectedUnit(Integer selectedUnit) {
		if ( selectedUnit != null )
			this.selectedUnit = selectedUnit;
		else
			this.selectedUnit = Integer.valueOf(0);
	}

	/**
	 * @return 
	 * 		the selected missing value, as a string.
	 * 		An empty string is to be interpreted as the default missing values.
	 */
	public String getSelectedMissingValue() {
		return selectedMissingValue;
	}

	/**
	 * @param selectedMissingValue 
	 * 		the selected missing value, as a string, to set;
	 * 		if null, an empty string is assigned.
	 * 		An empty string is to be interpreted as the default missing values.
	 */
	public void setSelectedMissingValue(String selectedMissingValue) {
		if ( selectedMissingValue != null )
			this.selectedMissingValue = selectedMissingValue;
		else
			this.selectedMissingValue = "";
	}

	/**
	 * Case-insensitive check if the name of this data column type 
	 * matches the name of another data column type.
	 * 
	 * @param other
	 * 		data column type to compare to
	 * @return
	 * 		if the data column type names match, case-insensitive
	 */
	public boolean nameMatches(DataColumnType other) {
		if ( this == other )
			return true;
		if ( other == null )
			return false;
		return this.name.equalsIgnoreCase(other.name);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = name.hashCode();
		result = result * prime + selectedUnit.hashCode();
		result = result * prime + selectedMissingValue.hashCode();
		result = result * prime + units.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof DataColumnType) )
			return false;

		DataColumnType other = (DataColumnType) obj;
		if ( ! name.equals(other.name) )
			return false;
		if ( ! selectedUnit.equals(other.selectedUnit) )
			return false;
		if ( ! selectedMissingValue.equals(other.selectedMissingValue) )
			return false;
		if ( ! units.equals(other.units) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataColumnType[name=" + name + 
				", units=" + units.toString() + 
				", selectedUnit=" + selectedUnit.toString() + 
				", selectedMissingValue=" + selectedMissingValue + 
				"]";
	}

}
