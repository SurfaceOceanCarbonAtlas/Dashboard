/**
 *
 */
package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Types of the data columns in a user-provided data file.
 * Includes information about this data column type to present and assign on the client side.
 *
 * @author Karl Smith
 */
public class DataColumnType implements Comparable<DataColumnType>, Serializable, IsSerializable {

    private static final long serialVersionUID = -6281568935061988193L;

    protected String varName;
    protected Double sortOrder;
    protected String displayName;
    protected String description;
    protected ArrayList<String> units;
    protected boolean isCritical;
    protected Integer selectedUnitIndex;
    protected String selectedMissingValue;

    /**
     * Create an empty data column type; all strings are set to {@link DashboardUtils#STRING_MISSING_VALUE},
     * the sort order is set to {@link DashboardUtils#FP_MISSING_VALUE}, the units list is a copy of
     * {@link DashboardUtils#NO_UNITS}, the index of the selected unit is zero, and is set to not critical.
     */
    public DataColumnType() {
        varName = DashboardUtils.STRING_MISSING_VALUE;
        sortOrder = DashboardUtils.FP_MISSING_VALUE;
        displayName = DashboardUtils.STRING_MISSING_VALUE;
        description = DashboardUtils.STRING_MISSING_VALUE;
        isCritical = false;
        units = new ArrayList<String>(DashboardUtils.NO_UNITS);
        selectedUnitIndex = 0;
        selectedMissingValue = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * Create a data column type with the given values.  A new ArrayList of units is created from the given
     * ArrayList of units.  The index of the selected unit is zero and the selected missing value is set to
     * {@link DashboardUtils#STRING_MISSING_VALUE} (interpreted as default missing values).
     *
     * @param varName
     *         name for a variable of this type; cannot be null or blank
     * @param sortOrder
     *         value giving the sort order for this type;
     *         if null, NaN, or infinite, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
     * @param displayName
     *         displayed name for this types; if null or blank, varName is used
     * @param description
     *         description of a variable of this type;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     * @param isCritical
     *         if this type must be present and a valid value
     * @param units
     *         unit strings associated with this type;
     *         if null or empty, {@link DashboardUtils#NO_UNITS} is used
     *
     * @throws IllegalArgumentException
     *         if the variable name is null or blank
     */
    public DataColumnType(String varName, Double sortOrder, String displayName, String description,
            boolean isCritical, ArrayList<String> units) throws IllegalArgumentException {
        if ( (varName == null) || varName.trim().isEmpty() )
            throw new IllegalArgumentException("data type variable name is invalid");
        this.varName = varName;
        if ( (sortOrder == null) || sortOrder.isNaN() || sortOrder.isInfinite() )
            this.sortOrder = DashboardUtils.FP_MISSING_VALUE;
        else
            this.sortOrder = sortOrder;
        if ( (displayName == null) || displayName.trim().isEmpty() )
            this.displayName = varName;
        else
            this.displayName = displayName;
        if ( description != null )
            this.description = description;
        else
            this.description = DashboardUtils.STRING_MISSING_VALUE;
        this.isCritical = isCritical;
        if ( (units != null) && (units.size() > 0) )
            this.units = new ArrayList<String>(units);
        else
            this.units = new ArrayList<String>(DashboardUtils.NO_UNITS);
        this.selectedUnitIndex = 0;
        this.selectedMissingValue = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return the variable name for this data column type;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getVarName() {
        return varName;
    }

    /**
     * @param varName
     *         the variable name to set for this of this data column type;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setVarName(String varName) {
        if ( varName != null )
            this.varName = varName;
        else
            this.varName = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return the sort order value for this data column type;
     *         never null but may be {@link DashboardUtils#FP_MISSING_VALUE}
     */
    public Double getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder
     *         the sort order value to set for this of this data column type;
     *         if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
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
     * @return the displayed name for this data column type;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName
     *         the displayed name to set for this of this data column type;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setDisplayName(String displayName) {
        if ( displayName != null )
            this.displayName = displayName;
        else
            this.displayName = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return description of a variable of this type;
     *         never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *         description of a variable of this type to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setDescription(String description) {
        if ( description != null )
            this.description = description;
        else
            this.description = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return if this data column type is required to be present and all its data values be valid
     */
    public boolean isCritical() {
        return isCritical;
    }

    /**
     * @param isCritical
     *         set if this data column type is required to be present and all its data values be valid
     */
    public void setCritical(boolean isCritical) {
        this.isCritical = isCritical;
    }

    /**
     * @return the units associated with this data column type;
     *         never null or empty, but may only contain {@link DashboardUtils#STRING_MISSING_VALUE}.
     *         The actual ArrayList in this object is returned.
     */
    public ArrayList<String> getUnits() {
        return units;
    }

    /**
     * @param units
     *         the list of units to associate with this data column type (copied);
     *         if null or empty, a copy of {@link DashboardUtils#NO_UNITS} is assigned.
     */
    public void setUnits(ArrayList<String> units) {
        if ( (units != null) && (units.size() > 0) )
            this.units = new ArrayList<String>(units);
        else
            this.units = new ArrayList<String>(DashboardUtils.NO_UNITS);
    }

    /**
     * @return the index in the units list of the selected unit;
     *         if the index is not valid for the current list of units, zero is returned
     */
    public Integer getSelectedUnitIndex() {
        if ( (selectedUnitIndex < 0) || (selectedUnitIndex >= units.size()) )
            return 0;
        return selectedUnitIndex;
    }

    /**
     * @param selectedUnitIndex
     *         the index in the units list of the selected unit to set; if null, zero is assigned
     */
    public void setSelectedUnitIndex(Integer selectedUnitIndex) {
        if ( selectedUnitIndex != null )
            this.selectedUnitIndex = selectedUnitIndex;
        else
            this.selectedUnitIndex = 0;
    }

    /**
     * Assigns the selected unit using the given unit name. Name comparisons are case-insensitive.
     *
     * @param unitName
     *         name of the selected unit
     *
     * @return true if the unit name was found and the selected unit index was assigned; otherwise false
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
     * @return the selected missing value, as a string. A value of {@link DashboardUtils#STRING_MISSING_VALUE}
     *         is to be interpreted as the default missing values.
     */
    public String getSelectedMissingValue() {
        return selectedMissingValue;
    }

    /**
     * @param selectedMissingValue
     *         the selected missing value, as a string, to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned.
     *         {@link DashboardUtils#STRING_MISSING_VALUE} is to be interpreted as the default missing values.
     */
    public void setSelectedMissingValue(String selectedMissingValue) {
        if ( selectedMissingValue != null )
            this.selectedMissingValue = selectedMissingValue;
        else
            this.selectedMissingValue = DashboardUtils.STRING_MISSING_VALUE;
    }

    /**
     * @return a deep copy of this data column type instance.
     *         Copies are made of any mutable data (namely, the list of units).
     */
    public DataColumnType duplicate() {
        DataColumnType dup = new DataColumnType(varName, sortOrder, displayName,
                description, isCritical, units);
        dup.selectedUnitIndex = selectedUnitIndex;
        dup.selectedMissingValue = selectedMissingValue;
        return dup;
    }

    /**
     * Checks if the variable or displayed name of this data column type is equal,
     * ignoring case and non-alphanumeric characters, to the given name.
     *
     * @param name
     *         data column name to compare to
     *
     * @return whether the type names match
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
     * Checks if the variable or displayed name of this data column type is equal, ignoring case and
     * non-alphanumeric characters, to either of those of another data column type.
     *
     * @param other
     *         data column type to compare to
     *
     * @return whether the type names match
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
        result = description.compareTo(other.description);
        if ( result != 0 )
            return result;
        result = Boolean.valueOf(isCritical).compareTo(other.isCritical);
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
        // Ignore floating-point values as they do not have to be exactly equal
        final int prime = 37;
        int result = varName.hashCode();
        result = result * prime + displayName.hashCode();
        result = result * prime + description.hashCode();
        result = result * prime + Boolean.valueOf(isCritical).hashCode();
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

        if ( !(obj instanceof DataColumnType) )
            return false;

        DataColumnType other = (DataColumnType) obj;
        if ( !varName.equals(other.varName) )
            return false;
        if ( !displayName.equals(other.displayName) )
            return false;
        if ( !description.equals(other.description) )
            return false;
        if ( isCritical != other.isCritical )
            return false;
        if ( !selectedUnitIndex.equals(other.selectedUnitIndex) )
            return false;
        if ( !selectedMissingValue.equals(other.selectedMissingValue) )
            return false;
        if ( !units.equals(other.units) )
            return false;
        // Floating point only needs to be insignificantly different
        if ( !DashboardUtils.closeTo(sortOrder, other.sortOrder, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DataColumnType[ " +
                "varName=\"" + varName + "\", " +
                "sortOrder=" + sortOrder.toString() + ", " +
                "displayName=\"" + displayName + "\", " +
                "description=\"" + description + "\", " +
                "isCritical=" + Boolean.toString(isCritical) + ", " +
                "units=" + units.toString() + ", " +
                "selectedUnitIndex=" + selectedUnitIndex.toString() + ", " +
                "selectedMissingValue=\"" + selectedMissingValue + "\" ]";
    }

}
