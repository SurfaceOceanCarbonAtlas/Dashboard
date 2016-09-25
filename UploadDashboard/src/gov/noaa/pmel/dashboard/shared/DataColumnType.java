/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Types of the data columns in a user-provided cruise data file.
 * Includes information about this data column type to present
 * and assign on the client side, as well as information for 
 * creating the variable in a NetCDF file on the server side.
 * 
 * @author Karl Smith
 */
public class DataColumnType implements Serializable, IsSerializable {

	private static final long serialVersionUID = 8150134653894607400L;

	/** For data without any specific units */
	public static final ArrayList<String> NO_UNITS = new ArrayList<String>(Arrays.asList(""));

	/**
	 * UNKNOWN needs to be respecified as one of the (other)DashboardUtils.STRING_MISSING_VALUEata column types.
	 */
	public static final DataColumnType UNKNOWN = new DataColumnType("(unknown)", null, null, null, null, NO_UNITS);

	/**
	 * OTHER is for supplementary data in the user's original data file but 
	 * otherwise not used.  A description of each column with this type must 
	 * be part of the metadata, but the values are not validated or used. 
	 * Multiple columns may have this type.
	 */
	public static final DataColumnType OTHER = new DataColumnType("other", null, null, null, null, NO_UNITS);

	private String varName;
	private String dataClassName;
	private String description;
	private String standardName;
	private String categoryName;
	private ArrayList<String> units;
	private Integer selectedUnitIndex;
	private String selectedMissingValue;

	/**
	 * Create an empty data column type: 
	 * varName, dataClassName, description, standardName, and categoryName are {@link DashboardUtils#STRING_MISSING_VALUE},
	 * units list is a copy of {@link #NO_UNITS},
	 * index of the selected unit is zero,
	 * selected missing value is {@link DashboardUtils#STRING_MISSING_VALUE} (interpreted as default missing values). 
	 */
	public DataColumnType() {
		varName = DashboardUtils.STRING_MISSING_VALUE;
		dataClassName = DashboardUtils.STRING_MISSING_VALUE;
		description = DashboardUtils.STRING_MISSING_VALUE;
		standardName = DashboardUtils.STRING_MISSING_VALUE;
		categoryName = DashboardUtils.STRING_MISSING_VALUE;
		units = new ArrayList<String>(NO_UNITS);
		selectedUnitIndex = Integer.valueOf(0);
		selectedMissingValue = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * Create a data column type with the given values.  The index of the 
	 * selected unit is zero and the selected missing value is
	 * {@link DashboardUtils#STRING_MISSING_VALUE} (interpreted as default missing values).
	 * 
	 * @param varName
	 * 		name for a variable of this type; 
	 * 		cannot be null or blank
	 * @param dataClassName
	 * 		name of the class for a variable of this type
	 * 		(e.g., Character, Double, Integer, String);
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param description
	 * 		description of a variable of this type;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param standardName
	 * 		standard name for a variable of this type
	 * 		(can duplicate standard names of other data column types);
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param categoryName
	 * 		category name for a variable of this type;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param units
	 * 		unit strings associated with this type (copied);
	 * 		if null or empty, a list with only {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public DataColumnType(String varName, String dataClassName, String description,
			String standardName, String categoryName, Collection<String> units) {
		if ( varName != null )
			this.varName = varName.trim();
		else
			this.varName = "";
		if ( this.varName.isEmpty() )
			throw new IllegalArgumentException("varName is null or blank");
		if ( dataClassName != null )
			this.dataClassName = dataClassName;
		else
			this.dataClassName = DashboardUtils.STRING_MISSING_VALUE;
		if ( description != null )
			this.description = description;
		else
			this.description = DashboardUtils.STRING_MISSING_VALUE;
		if ( standardName != null )
			this.standardName = standardName;
		else
			this.standardName = DashboardUtils.STRING_MISSING_VALUE;
		if ( categoryName != null )
			this.categoryName = categoryName;
		else
			this.categoryName = DashboardUtils.STRING_MISSING_VALUE;
		if ( (units != null) && (units.size() > 0) ) {
			this.units = new ArrayList<String>(units);
		}
		else {
			// Assume no units are going to be added
			this.units = new ArrayList<String>(NO_UNITS);
		}
		this.selectedUnitIndex = Integer.valueOf(0);
		this.selectedMissingValue = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * @return 
	 * 		the variable name for this data column type;
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * @param varName 
	 * 		the variable name to set for this of this data column type;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setVarName(String varName) {
		if ( varName != null ) {
			this.varName = varName;
		}
		else {
			this.varName = DashboardUtils.STRING_MISSING_VALUE;
		}
	}

	/**
	 * @return 
	 * 		the data class name for this data column type
	 * 		(e.g., Character, Double, Integer, String);
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getDataClassName() {
		return dataClassName;
	}

	/**
	 * @param dataClassName 
	 * 		the data class name to set for this data column type
	 * 		(e.g., Character, Double, Integer, String);
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setDataClassName(String dataClassName) {
		if ( dataClassName != null )
			this.dataClassName = dataClassName;
		else
			this.dataClassName = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * @return 
	 * 		description of a variable of this type;
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description 
	 * 		description of a variable of this type to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setDescription(String description) {
		if ( description != null )
			this.description = description;
		else
			this.description = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * @return 
	 * 		standard name of a variable of this type;
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getStandardName() {
		return standardName;
	}

	/**
	 * @param standardName 
	 * 		standard name of a variable of this type to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setStandardName(String standardName) {
		if ( standardName != null )
			this.standardName = standardName;
		else
			this.standardName = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * @return
	 * 		category name of a variable of this type;
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName 
	 * 		category name of a variable of this type;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setCategoryName(String categoryName) {
		if ( categoryName != null )
			this.categoryName = categoryName;
		else
			this.categoryName = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * @return 
	 * 		the units associated with this data column type;
	 * 		never null or empty, but may only contain 
	 * 		{@link DashboardUtils#STRING_MISSING_VALUE}.
	 * 		The actual ArrayList in this object is returned.
	 */
	public ArrayList<String> getUnits() {
		return units;
	}

	/**
	 * @param units 
	 * 		the list of units to associate with this data column type (copied); 
	 * 		if null or empty, a copy of {@link #NO_UNITS} is assigned.
	 */
	public void setUnits(Collection<String> units) {
		if ( (units != null) && (units.size() > 0) ) {
			this.units = new ArrayList<String>(units);
		}
		else {
			this.units = new ArrayList<String>(NO_UNITS);
		}
	}

	/**
	 * @return 
	 * 		the index in the units list of the selected unit;
	 * 		if the index is not valid for the current list of units,
	 * 		zero is returned
	 */
	public Integer getSelectedUnitIndex() {
		if ( (selectedUnitIndex < 0) || (selectedUnitIndex >= units.size()) )
			return 0;
		return selectedUnitIndex;
	}

	/**
	 * @param selectedUnitIndex 
	 * 		the index in the units list of the selected unit to set;
	 * 		if null, zero is assigned
	 */
	public void setSelectedUnitIndex(Integer selectedUnitIndex) {
		if ( selectedUnitIndex != null )
			this.selectedUnitIndex = selectedUnitIndex;
		else
			this.selectedUnitIndex = Integer.valueOf(0);
	}

	/**
	 * @return 
	 * 		the selected missing value, as a string.
	 * 		A value of {@link DashboardUtils#STRING_MISSING_VALUE} 
	 * 		is to be interpreted as the default missing values.
	 */
	public String getSelectedMissingValue() {
		return selectedMissingValue;
	}

	/**
	 * @param selectedMissingValue 
	 * 		the selected missing value, as a string, to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned.
	 * 		{@link DashboardUtils#STRING_MISSING_VALUE} is to be interpreted 
	 * 		as the default missing values.
	 */
	public void setSelectedMissingValue(String selectedMissingValue) {
		if ( selectedMissingValue != null )
			this.selectedMissingValue = selectedMissingValue;
		else
			this.selectedMissingValue = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * @return
	 * 		a deep of this data column type instance in which copies are 
	 * 		made of any mutable data (namely, the list of units).
	 */
	public DataColumnType duplicate() {
		DataColumnType dup = new DataColumnType(varName, dataClassName, description, standardName, categoryName, units);
		dup.selectedUnitIndex = selectedUnitIndex;
		dup.selectedMissingValue = selectedMissingValue;
		return dup;
	}

	/**
	 * Checks if the upper-cased variable name of this DataColumnType 
	 * is equal to that of another.
	 * 
	 * @param other
	 * 		data column type to compare to
	 * @return
	 * 		whether the types names match
	 */
	public boolean typeNameEquals(DataColumnType other) {
		if ( this == other )
			return true;
		if ( other == null )
			return false;
		return varName.toUpperCase().equals(other.varName.toUpperCase());
	}

	/**
	 * Checks if the variable name and data class name of this DataColumnType 
	 * is equal to that of another.
	 * 
	 * @param other
	 * 		data column type to compare to
	 * @return
	 * 		whether the types names match
	 */
	public boolean typeEquals(DataColumnType other) {
		if ( this == other )
			return true;
		if ( other == null )
			return false;
		if ( ! varName.equals(other.varName) )
			return false;
		if ( ! dataClassName.equals(other.dataClassName) )
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = varName.hashCode();
		result = result * prime + dataClassName.hashCode();
		result = result * prime + description.hashCode();
		result = result * prime + standardName.hashCode();
		result = result * prime + categoryName.hashCode();
		result = result * prime + selectedUnitIndex.hashCode();
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
		if ( ! varName.equals(other.varName) )
			return false;
		if ( ! dataClassName.equals(other.dataClassName) )
			return false;
		if ( ! description.equals(other.description) )
			return false;
		if ( ! standardName.equals(other.standardName) )
			return false;
		if ( ! categoryName.equals(other.categoryName) )
			return false;
		if ( ! selectedUnitIndex.equals(other.selectedUnitIndex) )
			return false;
		if ( ! selectedMissingValue.equals(other.selectedMissingValue) )
			return false;
		if ( ! units.equals(other.units) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataColumnType[varName=" + varName + 
				", dataClassName=" + dataClassName + 
				", description=" + description + 
				", standardName=" + standardName + 
				", categoryName=" + categoryName + 
				", units=" + units.toString() + 
				", selectedUnitIndex=" + selectedUnitIndex.toString() + 
				", selectedMissingValue=" + selectedMissingValue + 
				"]";
	}

}
