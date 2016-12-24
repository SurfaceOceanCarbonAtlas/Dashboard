/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Standardizer for character values.
 * 
 * @author Karl Smith
 */
public class CharStandardizer extends ValueStandardizer {

	/**
	 * Create a standardizer for a Character data column type.  This standardizer 
	 * returns null for missing value strings; otherwise just returns the single
	 * Character represented by the string.  Any request for unit conversion will 
	 * throw a NotStandardizedException.
	 *  
	 * @param dtype
	 * 		Character data column type to standardize
	 * @throws IllegalArgumentException
	 * 		if the data column type is not a Character type, or
	 * 		if any unit conversion is requested.
	 */
	public CharStandardizer(DataColumnType dtype) throws IllegalArgumentException {
		super(dtype);
		if ( ! DashboardUtils.CHAR_DATA_CLASS_NAME.equals(dataType.getDataClassName()) )
			throw new IllegalArgumentException("data class name not " + DashboardUtils.CHAR_DATA_CLASS_NAME);
		if ( ( (fromUnit == null) && (toUnit != null) ) ||
			 ( (fromUnit != null) && ( ! fromUnit.equals(toUnit) ) ) )
			throw new IllegalArgumentException("unit conversion of characters not supported");
	}

	/**
	 * Standardized the given string representation of a character by identifying 
	 * missing values and then selecting the single character represented by this string.
	 * 
	 * @param strVal
	 * 		the given string representation of the character
	 * @return
	 * 		null if the string matches (case insensitive, trimmed) a missing value for this type; 
	 * 		otherwise the single character of the string, after trimming, is returned.
	 * 		If the given string representation is a single whitespace character that
	 * 		is not interpreted as a missing value, then that character is returned.
	 * @throws IllegalArgumentException
	 * 		if null is given, or
	 * 		if the string represents neither a missing value nor a single character
	 */
	@Override
	public Character getStandardValue(String strVal) throws IllegalArgumentException {
		if ( strVal == null )
			throw new IllegalArgumentException("null string given");
		String standardVal = strVal.trim();
		if ( isMissingValue(standardVal) )
			return null;
		if ( standardVal.length() != 1 ) {
			// Just in case the value is a single whitespace character not considered a missing value
			if ( strVal.length() == 1 )
				return strVal.charAt(0);
			throw new IllegalArgumentException("'" + standardVal + "' is neither a single character nor a missing value");
		}
		return standardVal.charAt(0);
	}

}
