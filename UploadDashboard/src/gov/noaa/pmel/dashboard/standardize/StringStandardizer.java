/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Standardizer for string values.
 * 
 * @author Karl Smith
 */
public class StringStandardizer extends ValueStandardizer {

	/**
	 * Create a standardizer for a String data column type.  This standardizer 
	 * returns null for missing value strings; otherwise just returns the given
	 * String after trimming.  Any request for unit conversion will throw a 
	 * NotStandardizedException.
	 *  
	 * @param dtype
	 * 		String data column type to standardize
	 * @throws IllegalArgumentException
	 * 		if the data column type is not a String type, or
	 * 		if any unit conversion is requested.
	 */
	public StringStandardizer(DataColumnType dtype) throws IllegalArgumentException {
		super(dtype);
		if ( ! DashboardUtils.STRING_DATA_CLASS_NAME.equals(dataType.getDataClassName()) )
			throw new IllegalArgumentException("data class name not " + DashboardUtils.STRING_DATA_CLASS_NAME);
		if ( ( (fromUnit == null) && (toUnit != null) ) ||
			 ( (fromUnit != null) && ( ! fromUnit.equals(toUnit) ) ) )
			throw new IllegalArgumentException("unit conversion of strings not supported");
	}

	/**
	 * Standardized the given string by trimming whitespace characters 
	 * from the ends of the string, and by identifying missing values.
	 * 
	 * @param strVal
	 * 		the given (non-standard) string value
	 * @return
	 * 		null if the string matches (case insensitive, trimmed) a missing value for this type; 
	 * 		otherwise the given string, after trimming, is returned.
	 * @throws IllegalArgumentException
	 * 		if null is given
	 */
	@Override
	public String getStandardValue(String strVal) throws IllegalArgumentException {
		if ( strVal == null )
			throw new IllegalArgumentException("null string given to standardize");
		String standardVal = strVal.trim();
		if ( isMissingValue(standardVal) )
			return null;
		return standardVal;
	}

}
