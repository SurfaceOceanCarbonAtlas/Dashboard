/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Base class for standardizing various types of data.
 * 
 * @author Karl Smith
 */
public abstract class ValueStandardizer {

	protected DashDataType dataType;
	protected String fromUnit;
	protected String toUnit;
	protected String missVal;

	/**
	 * Create a standardizer for the given data column type.  This standardizer 
	 * will appropriately interpret string representations of values, including 
	 * strings representing the missing value(s), for the data column type.  If 
	 * appropriate, values will be converted to standard units (the first unit 
	 * given in the list of units).
	 *  
	 * @param dtype
	 * 		data column type to standardize
	 * @throws NotStandardizedException
	 * 		if the data column type is invalid for standardizing, or 
	 * 		if the data column type cannot be standardized without additional data
	 */
	protected ValueStandardizer(DataColumnType dtype) throws NotStandardizedException {
		dataType = new DashDataType(dtype);
		fromUnit = dtype.getUnits().get(dtype.getSelectedUnitIndex());
		if ( DashboardUtils.STRING_MISSING_VALUE.equals(fromUnit) )
			fromUnit = null;
		toUnit = dtype.getUnits().get(0);
		if ( DashboardUtils.STRING_MISSING_VALUE.equals(toUnit) )
			toUnit = null;
		missVal = dtype.getSelectedMissingValue();
		if ( DashboardUtils.STRING_MISSING_VALUE.equals(missVal) )
			missVal = null;
	}

	/**
	 * Interpret the string representation of a value and return an appropriate 
	 * object for the standard value for the data column type associated with 
	 * this standardizer.  This includes detecting missing values and, if 
	 * appropriate, converting to standard units.
	 * 
	 * @param strVal
	 * 		the string representation of the (non-standard) value
	 * @return
	 * 		the standard value Object; 
	 * 		null is returned if the string representation matches 
	 * 		a missing value for this type.
	 * @throws NotStandardizedException
	 * 		if the given string representation is null, or
	 * 		if the value cannot be standardized
	 */
	public abstract Object getStandardValue(String strVal) throws NotStandardizedException;

}
