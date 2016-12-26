/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Standardizer for integer values.
 * 
 * @author Karl Smith
 */
public class IntStandardizer extends ValueStandardizer {

	/**
	 * Create a standardizer for a Integer data column type.  This standardizer returns 
	 * null for missing value strings; otherwise parses strings as decimal integers. 
	 * Any request for unit conversion will throw a NotStandardizedException.
	 *  
	 * @param dtype
	 * 		Integer data column type to standardize
	 * @throws IllegalArgumentException
	 * 		if the data column type is not a Integer type, or
	 * 		if any unit conversion is requested.
	 */
	public IntStandardizer(DataColumnType dtype) throws IllegalArgumentException {
		super(dtype);
		if ( ! DashboardUtils.INT_DATA_CLASS_NAME.equals(dataType.getDataClassName()) )
			throw new IllegalArgumentException("data class name not " + DashboardUtils.INT_DATA_CLASS_NAME);
		if ( ( (fromUnit == null) && (toUnit != null) ) ||
			 ( (fromUnit != null) && ( ! fromUnit.equals(toUnit) ) ) )
			throw new IllegalArgumentException("unit conversion of integers not supported");
	}

	/**
	 * Standardized the given string representation of a decimal integer by first 
	 * identifying missing values.  If not a missing value, the string is parsed
	 * as a decimal integer (see {@link Integer#valueOf(String)}).
	 * 
	 * @param strVal
	 * 		the given string representation of the integer
	 * @return
	 * 		null if the string matches (case insensitive, trimmed) a missing value for this type; 
	 * 		otherwise the integer represented by the string 
	 * @throws IllegalArgumentException
	 * 		if null is given, or
	 * 		if the string represents neither a missing value nor a decimal integer
	 */
	@Override
	public Integer getStandardValue(String strVal) throws IllegalArgumentException {
		if ( strVal == null )
			throw new IllegalArgumentException("null string given to standardize");
		if ( isMissingValue(strVal) )
			return null;
		Integer value;
		try {
			value = Integer.valueOf(strVal.trim());
		} catch ( Exception ex ) {
			value = null;
		}
		if ( value == null )
			throw new IllegalArgumentException("\"" + strVal + "\" is neither an integer nor a missing value");
		return value;
	}

}
