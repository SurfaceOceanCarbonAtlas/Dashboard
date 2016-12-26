/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import java.util.Arrays;
import java.util.HashSet;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Base class for standardizing various types of data.
 * 
 * @author Karl Smith
 */
public abstract class ValueStandardizer {

	/** 
	 * Default missing value strings in lower case: includes 
	 * the empty string, "na", "n/a", "nan", "null" and various numbers of dashes.
	 */
	protected static final HashSet<String> DEFAULT_MISSING_VALUE_LCSTRINGS_SET =
			new HashSet<String>(Arrays.asList("", "na", "n/a", "nan", "null",
					"-", "--", "---", "----", "-----"));
	/**
	 * Default missing value numbers of the -999, -9999, and -99999 variety.
	 */
	protected static final Double[] DEFAULT_MISSING_VALUE_NUMBERS_ARRAY = 
			new Double[] {-999.0, -999.9, -999.99, -999.999,
					-9999.0, -9999.9, -9999.99, -99999.0, -99999.9};

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
	 * @throws IllegalArgumentException
	 * 		if the data column type is invalid for standardizing 
	 * 		with this particular standardizer subclass
	 * @throws IllegalStateException
	 * 		if the data column cannot be standardized at this time 
	 * 		because standardized data from another column (not yet 
	 * 		standardized) is needed
	 */
	protected ValueStandardizer(DataColumnType dtype) {
		if ( dtype == null )
			throw new IllegalArgumentException("null data column type given");
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
		else
			missVal = missVal.trim();
	}

	/**
	 * Checks if a given string (after trimming) matches (compared case-insensitive) 
	 * a missing value.  If a missing value string is specified in this standardizer, 
	 * only that (trimmed) value is used.  Otherwise, the default set of missing 
	 * values is checked.  The default set of missing values are given by the strings 
	 * in {@link #DEFAULT_MISSING_VALUE_LCSTRINGS_SET} and numbers in 
	 * {@link #DEFAULT_MISSING_VALUE_NUMBERS_ARRAY}.
	 * 
	 * @param strVal
	 * 		string representation to check
	 * @return
	 * 		if this string represents a missing value
	 * @throws NullPointerException
	 * 		if the given string is null
	 */
	protected boolean isMissingValue(String strVal) throws NullPointerException {
		String trimVal = strVal.trim();
		if ( missVal == null ) {
			if ( DEFAULT_MISSING_VALUE_LCSTRINGS_SET.contains(trimVal.toLowerCase()) )
				return true;
			try {
				Double value = Double.valueOf(trimVal);
				for ( Double mvdbl : DEFAULT_MISSING_VALUE_NUMBERS_ARRAY ) {
					if ( DashboardUtils.closeTo(value, mvdbl, 
							DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
						return true;
					}
				}
			} catch ( Exception ex ) {
				// not numeric
			}
		}
		else if ( missVal.equalsIgnoreCase(trimVal) ) {
			return true;
		}
		return false;
	}

	/**
	 * Interpret the string representation of a value and return an appropriate 
	 * object as the standard value for the data column type associated with 
	 * this standardizer.  This includes detecting missing values and, if 
	 * appropriate, converting to standard units.
	 * 
	 * @param strVal
	 * 		the string representation of the (non-standard) value
	 * @return
	 * 		null if the string representation matches a missing value for this type;
	 * 		otherwise, the standard value object for this string value and type 
	 * @throws NullPointerException
	 * 		if the given string is null
	 * @throws IllegalArgumentException
	 * 		if the given string cannot be interpreted as the required data type.
	 * @throws IllegalStateException
	 * 		if unit-conversion of the value cannot be performed
	 */
	public abstract Object getStandardValue(String strVal);

}
