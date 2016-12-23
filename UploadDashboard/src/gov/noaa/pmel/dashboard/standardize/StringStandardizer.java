/**
 * 
 */
package gov.noaa.pmel.dashboard.standardize;

import java.util.Arrays;
import java.util.HashSet;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Standardizer for string values.
 * 
 * @author Karl Smith
 */
public class StringStandardizer extends ValueStandardizer {

	private static final HashSet<String> DEFAULT_MISSING_VALUE_LC_STRINGS =
			new HashSet<String>(Arrays.asList("na", "n/a", "null",
					"-", "--", "---", "----", "-----"));

	/**
	 * Create a standardizer for a String data column type.  This standardizer 
	 * returns null for missing value strings; otherwise just returns the given
	 * String.  Any request for unit conversion will throw a NotStandardizedException.
	 *  
	 * @param dtype
	 * 		String data column type to standardize
	 * @throws NotStandardizedException
	 * 		if the data column type is not a String type, or
	 * 		if any unit conversion is requested.
	 */
	public StringStandardizer(DataColumnType dtype) throws NotStandardizedException {
		super(dtype);
		if ( ! DashboardUtils.STRING_DATA_CLASS_NAME.equals(dataType.getDataClassName()) )
			throw new NotStandardizedException("data class name not " + DashboardUtils.STRING_DATA_CLASS_NAME);
		boolean converting = false;
		if ( fromUnit == null ) {
			if ( toUnit != null )
				converting = true;
		}
		else if ( ! fromUnit.equals(toUnit) )
			converting = true;
		if ( converting )
			throw new NotStandardizedException("unit conversion of strings not supported");
	}

	@Override
	/**
	 * @param strVal
	 * 		the given (non-standard) string value
	 * @return
	 * 		null if the string matches (case insensitive) a missing value for this type; 
	 * 		otherwise the given string is returned.
	 * @throws NotStandardizedException
	 * 		if null is given
	 */
	public String getStandardValue(String strVal) throws NotStandardizedException {
		if ( strVal == null )
			throw new NotStandardizedException("null string given");
		if ( missVal == null ) {
			if ( DEFAULT_MISSING_VALUE_LC_STRINGS.contains(strVal.toLowerCase()) )
				return null;
		}
		else if ( missVal.equalsIgnoreCase(strVal) )
			return null;
		return strVal;
	}

}
