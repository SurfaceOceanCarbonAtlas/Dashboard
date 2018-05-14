/**
 *
 */
package gov.noaa.pmel.dashboard.datatype;

import java.util.Arrays;
import java.util.HashSet;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;

/**
 * Interprets string representations of data values based on an input
 * unit/format as well as missing values.  Converts the values, if
 * needed, to that specified by an output unit/format.
 *
 * @author Karl Smith
 */
public abstract class ValueConverter<T> {

    /**
     * Default missing value strings; includes the empty string,
     * "NA", "N/A", "NAN", "NULL" and various numbers of dashes.
     */
    private static final HashSet<String> DEFAULT_MISSING_VALUES_SET =
            new HashSet<String>(Arrays.asList("", "NA", "N/A", "NAN",
                    "NULL", "-", "--", "---", "----", "-----"));
    /**
     * Default missing value numbers of the -999, -9999, and -99999 variety.
     */
    private static final Double[] DEFAULT_MISSING_NUMBERS_ARRAY =
            new Double[] {-999.0, -999.9, -999.99, -999.999,
                    -9999.0, -9999.9, -9999.99, -99999.0, -99999.9};

    protected String fromUnit;
    protected String toUnit;
    protected String missVal;

    /**
     * Interprets string representations of data values based on the given input
     * unit/format as well as the given missing value string.  Converts the values,
     * if needed, to that specified by the output unit/format.
     * <br /><br />
     * It is acceptable for both the input unit and output unit to be null.  In such
     * cases, the value returned, if the string does not represent a missing value,
     * is from a simple parsing of the trimmed string value.
     *
     * @param inputUnit
     *         unit or format of the string representation of input data
     * @param outputUnit
     *         unit or format of the output data value
     * @param missingValue
     *         missing data value; if null, standard missing values are used
     * @throws IllegalArgumentException
     *         if the data standardization is not supported
     * @throws IllegalStateException
     *         if the data standardization cannot be performed at this time because
     *         standardized data from another column (not standardized) is needed
     */
    protected ValueConverter(String inputUnit, String outputUnit, String missingValue)
            throws IllegalArgumentException, IllegalStateException {
        fromUnit = inputUnit;
        toUnit = outputUnit;
        missVal = missingValue;
    }

    /**
     * Checks if a given string matches (compared case-insensitive) a missing value.
     * If a missing value string is specified (not null) in the constructor, only that
     * value is checked.  Otherwise, the default set of missing values is checked.
     * A string is one of the default missing values if the trimmed and upper-cased
     * value matches one of the string in {@link #DEFAULT_MISSING_VALUES_SET}, or
     * if requested, if the string representation is a floating-point value given in
     * the array {@link #DEFAULT_MISSING_NUMBERS_ARRAY}.
     *
     * @param valueString
     *         string representation to check
     * @param checkNumbers
     *         include default numeric missing values?
     * @return if this string represents a missing value
     * @throws IllegalArgumentException
     *         if the given string is null
     */
    protected boolean isMissingValue(String valueString, boolean checkNumbers)
                                                throws IllegalArgumentException {
        if ( valueString == null )
            throw new IllegalArgumentException("no value given");
        if ( missVal == null ) {
            String trimVal = valueString.trim();
            if ( DEFAULT_MISSING_VALUES_SET.contains(trimVal.toUpperCase()) )
                return true;
            if ( checkNumbers ) {
                try {
                    Double value = Double.valueOf(trimVal);
                    for ( Double mvdbl : DEFAULT_MISSING_NUMBERS_ARRAY ) {
                        if ( DashboardUtils.closeTo(value, mvdbl,
                                DashboardUtils.MAX_RELATIVE_ERROR,
                                DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
                            return true;
                        }
                    }
                } catch ( Exception ex ) {
                    // not numeric
                }
            }
        }
        else if ( missVal.equalsIgnoreCase(valueString) ) {
            return true;
        }
        return false;
    }

    /**
     * @param valueString
     *         the string representation of the data value in the input unit/format
     * @return null if the string representation matches a missing value;
     *         otherwise, the data value convert to the output unit/format
     * @throws IllegalArgumentException
     *         if the given value is null, or
     *         if the given string cannot be interpreted
     * @throws IllegalStateException
     *         if unit/format conversion of the data value cannot be performed
     */
    public abstract T convertValueOf(String valueString) throws IllegalArgumentException, IllegalStateException;

    @Override
    public String toString() {
        return "ValueConverter" +
                "[ fromUnit=" + fromUnit +
                ", toUnit=" + toUnit +
                ", missVal=" + missVal +
                " ]";
    }

}
