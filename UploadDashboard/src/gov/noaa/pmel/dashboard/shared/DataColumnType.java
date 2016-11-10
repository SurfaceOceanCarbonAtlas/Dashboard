/**
 * 
 */
package gov.noaa.pmel.dashboard.shared;

import java.io.Serializable;
import java.util.ArrayList;
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
public class DataColumnType implements Comparable<DataColumnType>, Serializable, IsSerializable {

	private static final long serialVersionUID = 8975368913536083085L;

	protected String varName;
	protected Double sortOrder;
	protected String displayName;
	protected String dataClassName;
	protected String description;
	protected String standardName;
	protected String categoryName;
	protected ArrayList<String> units;
	protected Integer selectedUnitIndex;
	protected String selectedMissingValue;

	/**
	 * Create an empty data column type; 
	 * the sort order is set to {@link DashboardUtils#FP_MISSING_VALUE},
	 * the units list is a copy of {@link DashboardUtils#NO_UNITS}, 
	 * the index of the selected unit is zero, 
	 * and all remaining String values are set to 
	 * {@link DashboardUtils#STRING_MISSING_VALUE},
	 */
	public DataColumnType() {
		varName = DashboardUtils.STRING_MISSING_VALUE;
		sortOrder = DashboardUtils.FP_MISSING_VALUE;
		displayName = DashboardUtils.STRING_MISSING_VALUE;
		dataClassName = DashboardUtils.STRING_MISSING_VALUE;
		description = DashboardUtils.STRING_MISSING_VALUE;
		standardName = DashboardUtils.STRING_MISSING_VALUE;
		categoryName = DashboardUtils.STRING_MISSING_VALUE;
		units = new ArrayList<String>(DashboardUtils.NO_UNITS);
		selectedUnitIndex = Integer.valueOf(0);
		selectedMissingValue = DashboardUtils.STRING_MISSING_VALUE;
	}

	/**
	 * Create a data column type with the given values.  The index 
	 * of the selected unit is zero and the selected missing value 
	 * is {@link DashboardUtils#STRING_MISSING_VALUE}
	 * (interpreted as default missing values).
	 * 
	 * @param varName
	 * 		name for a variable of this type; 
	 * 		cannot be null or blank
	 * @param sortOrder
	 * 		value giving the sort order for this type;
	 * 		if null, NaN, or infinite, 
	 * 		{@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 * @param displayName
	 * 		displayed name for this types;
	 * 		if null or blank, varName is used
	 * @param dataClassName
	 * 		name of the class for a variable of this type
	 * 		(e.g., Character, Double, Integer, String);
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param description
	 * 		description of a variable of this type;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param standardName
	 * 		standard name for a variable of this type
	 * 		(can be the same as that of other data column types);
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param categoryName
	 * 		category name for a variable of this type;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param units
	 * 		unit strings associated with this type (copied);
	 * 		if null or empty, {@link DashboardUtils#NO_UNITS} is used (copied)
	 * @throws IllegalArgumentException
	 * 		if the variable name is null or blank
	 */
	public DataColumnType(String varName, Double sortOrder, String displayName, 
			String dataClassName, String description, String standardName, 
			String categoryName, Collection<String> units) throws IllegalArgumentException {
		if ( (varName == null) || varName.trim().isEmpty() )
			throw new IllegalArgumentException("variable name is null or blank");
		this.varName = varName;
		if ( (sortOrder == null) || sortOrder.isNaN() || sortOrder.isInfinite() )
			this.sortOrder = DashboardUtils.FP_MISSING_VALUE;
		else
			this.sortOrder = sortOrder;
		if ( (displayName == null) || displayName.trim().isEmpty() )
			this.displayName = varName;
		else
			this.displayName = displayName;
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
			this.units = new ArrayList<String>(DashboardUtils.NO_UNITS);
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
	 * 		the sort order value for this data column type;
	 * 		never null but may be {@link DashboardUtils#FP_MISSING_VALUE}
	 */
	public Double getSortOrder() {
		return sortOrder;
	}

	/**
	 * @param varName 
	 * 		the variable name to set for this of this data column type;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setSortOrder(Double sortOrder) {
		if ( sortOrder != null ) {
			this.sortOrder = sortOrder;
		}
		else {
			this.sortOrder = DashboardUtils.FP_MISSING_VALUE;
		}
	}

	/**
	 * @return 
	 * 		the displayed name for this data column type;
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName 
	 * 		the displayed name to set for this of this data column type;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setDisplayName(String displayName) {
		if ( displayName != null ) {
			this.displayName = displayName;
		}
		else {
			this.displayName = DashboardUtils.STRING_MISSING_VALUE;
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
	 * 		if null or empty, a copy of {@link DashboardUtils#NO_UNITS} is assigned.
	 */
	public void setUnits(Collection<String> units) {
		if ( (units != null) && (units.size() > 0) ) {
			this.units = new ArrayList<String>(units);
		}
		else {
			this.units = new ArrayList<String>(DashboardUtils.NO_UNITS);
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
	 * Assigns the selected unit using the given unit name.
	 * Name comparisons are case-insensitively.
	 * 
	 * @param unitName
	 * 		name of the selected unit
	 * @return
	 * 		true if the unit name was found and the 
	 * 		selected unit index was assigned; 
	 * 		otherwise false
	 */
	public boolean setSelectedUnit(String unitName) {
		if ( unitName == null )
			return false;
		String upperName = unitName.toUpperCase();
		for (int k = 0; k < units.size(); k++) {
			if ( units.get(k).toUpperCase().equals(upperName) ) {
				selectedUnitIndex = k;
				return true;
			}
		}
		return false;
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
		DataColumnType dup = new DataColumnType(varName, sortOrder, displayName, 
				dataClassName, description, standardName, categoryName, units);
		dup.selectedUnitIndex = selectedUnitIndex;
		dup.selectedMissingValue = selectedMissingValue;
		return dup;
	}

	/**
	 * Checks if the variable or displayed name of this data column type 
	 * is equal, ignoring case and non-alphanumeric characters, 
	 * to the given name.
	 * 
	 * @param other
	 * 		data column type to compare to
	 * @return
	 * 		whether the type names match
	 */
	public boolean typeNameEquals(String name) {
		if ( name == null )
			return false;
		// Must use String.replaceAll for GWT translation to JavaScript
		String otherKey = name.toLowerCase().replaceAll("[^a-z0-9]+", "");
		if ( varName.toLowerCase().replaceAll("[^a-z0-9]+", "").equals(otherKey) )
			return true;
		if ( displayName.toLowerCase().replaceAll("[^a-z0-9]+", "").equals(otherKey) )
			return true;
		return false;
	}

	/**
	 * Checks if the variable or displayed name of this data column type 
	 * is equal, ignoring case and non-alphanumeric characters, 
	 * to either of those of another data column type.
	 * 
	 * @param other
	 * 		data column type to compare to
	 * @return
	 * 		whether the type names match
	 */
	public boolean typeNameEquals(DataColumnType other) {
		if ( this == other )
			return true;
		if ( other == null )
			return false;
		if ( typeNameEquals(other.varName) )
			return true;
		if ( typeNameEquals(other.displayName) )
			return true;
		return false;
	}

	/**
	 * @return
	 * 		if this is a WOCE flag type
	 */
	public boolean isWoceType() {
		if ( ! categoryName.equals(DashboardUtils.QUALITY_CATEGORY) )
			return false;
		if ( ! dataClassName.equals(DashboardUtils.CHAR_DATA_CLASS_NAME) )
			return false;
		if ( ! varName.toUpperCase().startsWith("WOCE_") )
			return false;
		return true;
	}

	/**
	 * Determines if this type is a WOCE comment for the given WOCE flag type.
	 * 
	 * @param woceType
	 * 		WOCE flag type to use; 
	 * 		if null, checks if this a comment for any possible WOCE flag type. 
	 * @return
	 * 		if this type is a WOCE comment for the given WOCE flag type
	 * @throws IllegalArgumentException
	 * 		if woceType is not null and not a WOCE flag type
	 */
	public boolean isWoceCommentFor(DataColumnType woceType) throws IllegalArgumentException {
		if ( woceType != null ) {
			if ( ! woceType.isWoceType() )
				throw new IllegalArgumentException("Argument to isWoceCommentFor is not a WOCE flag type: " + woceType.varName);
			if ( ! varName.toUpperCase().equals("COMMENT_" + woceType.varName.toUpperCase()) )
				return false;
		}
		else {
			if ( ! varName.toUpperCase().startsWith("COMMENT_WOCE_") )
				return false;
		}
		if ( ! dataClassName.equals(DashboardUtils.STRING_DATA_CLASS_NAME) )
			return false;
		return true;
	}

	@Override
	public int compareTo(DataColumnType other) {
		int result;
		result = sortOrder.compareTo(other.sortOrder);
		if ( result != 0 )
			return result;
		result = varName.compareTo(other.varName);
		if ( result != 0 )
			return result;
		result = displayName.compareTo(other.displayName);
		if ( result != 0 )
			return result;
		result = dataClassName.compareTo(other.dataClassName);
		if ( result != 0 )
			return result;
		result = description.compareTo(other.description);
		if ( result != 0 )
			return result;
		result = standardName.compareTo(other.standardName);
		if ( result != 0 )
			return result;
		result = categoryName.compareTo(other.categoryName);
		if ( result != 0 )
			return result;
		result = selectedMissingValue.compareTo(other.selectedMissingValue);
		if ( result != 0 )
			return result;
		result = selectedUnitIndex.compareTo(other.selectedUnitIndex);
		if ( result != 0 )
			return result;
		result = Integer.compare(units.size(), other.units.size());
		if ( result != 0 )
			return result;
		for (int k = 0; k < units.size(); k++) {
			result = units.get(k).compareTo(other.units.get(k));
			if ( result != 0 )
				return result;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		// Ignore floating-point as they do not have to be exactly equal
		final int prime = 37;
		int result = varName.hashCode();
		result = result * prime + displayName.hashCode();
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
		if ( ! displayName.equals(other.displayName) )
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
		// Floating point only needs to be insignificantly different
		if ( ! DashboardUtils.closeTo(sortOrder, other.sortOrder, 
				DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataColumnType[ " +
				"varName=\"" + varName + "\", " +
				"sortOrder=" + sortOrder.toString() + ", " +
				"displayName=\"" + displayName + "\", " +
				"dataClassName=\"" + dataClassName + "\", " +
				"description=\"" + description + "\", " +
				"standardName=\"" + standardName + "\", " +
				"categoryName=\"" + categoryName + "\", " +
				"units=" + units.toString() + ", " +
				"selectedUnitIndex=" + selectedUnitIndex.toString() + ", " +
				"selectedMissingValue=\"" + selectedMissingValue + "\" ]";
	}

}
