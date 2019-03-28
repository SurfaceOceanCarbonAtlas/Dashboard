/**
 *
 */
package gov.noaa.pmel.dashboard.datatype;

import gov.noaa.pmel.dashboard.dsg.StdUserDataArray;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.util.Collection;

/**
 * Data column types that provides String data.
 *
 * @author Karl Smith
 */
public class StringDashDataType extends DashDataType<String> {

    /**
     * Create with the given values.
     *
     * @param varName
     *         NetCDF variable name for this data column type; cannot be null or blank
     * @param sortOrder
     *         value used for ordering data column types; cannot be null, NaN, or infinite
     * @param displayName
     *         name displayed in the Dashboard UI for this data column type; cannot be null or blank
     * @param description
     *         brief description of this data column type; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is
     *         assigned
     * @param isCritical
     *         if this data type is required to be present and all values must be valid
     * @param units
     *         unit strings associated with this data column type.  A new ArrayList is created from the values in the
     *         given collection. The first unit in the list is considered the standard unit; values in other units will
     *         be converted to this unit when standardized.  If null or empty, a list with only {@link
     *         DashboardUtils#STRING_MISSING_VALUE} is created.
     * @param standardName
     *         standard name for this data column type (duplicate standard names between data column types is
     *         acceptable);  if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     * @param categoryName
     *         category name for this data column type; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is
     *         assigned
     * @param fileStdUnit
     *         name of the standard unit (corresponding to the first unit of the units array) to be used in the DSG
     *         files; if null, the first unit string will be used
     * @param minQuestionStrVal
     *         string representation of the minimum questionable value for standardized values of this data column type
     *         (less than this value is bad); if null, there is no minimum questionable value
     * @param minAcceptStrVal
     *         string representation of the minimum acceptable value for standardized values of this data column type
     *         (less than this value is questionable or bad); if null, there is no minimum acceptable value, although
     *         there still might be a minimum questionable value.
     * @param maxAcceptStrVal
     *         string representation of the maximum acceptable value for standardized values of this data column type
     *         (larger than this value is questionable or bad); if null, there is no maximum acceptable value, although
     *         there still might be a maximum questionable value.
     * @param maxQuestionStrVal
     *         string representation of the maximum questionable value for standardized values of this data column type
     *         (larger than this value is bad); if null, there is no maximum questionable value
     * @param roles
     *         the roles for this data type; cannot be null or empty
     *
     * @throws IllegalArgumentException
     *         if the variable name, sort order, display name, or roles is invalid;
     *         if the relationship: minQuestionVal <= minAcceptVal <= maxAcceptVal <= maxQuestionVal,
     *         for those values that are not null, is violated
     */
    public StringDashDataType(String varName, Double sortOrder, String displayName, String description,
            boolean isCritical, Collection<String> units, String standardName, String categoryName,
            String fileStdUnit, String minQuestionStrVal, String minAcceptStrVal, String maxAcceptStrVal,
            String maxQuestionStrVal, Collection<Role> roles) throws IllegalArgumentException {
        super(varName, sortOrder, displayName, description,
                isCritical, units, standardName, categoryName, fileStdUnit, roles);
        if ( minQuestionStrVal != null ) {
            try {
                minQuestionVal = dataValueOf(minQuestionStrVal);
            } catch ( IllegalArgumentException ex ) {
                throw new IllegalArgumentException("invalid minimum questionable value: \"" +
                        minQuestionStrVal + "\"");
            }
        }
        if ( minAcceptStrVal != null ) {
            try {
                minAcceptVal = dataValueOf(minAcceptStrVal);
            } catch ( IllegalArgumentException ex ) {
                throw new IllegalArgumentException("invalid minimum acceptable value: \"" +
                        minAcceptStrVal + "\"");
            }
        }
        if ( maxAcceptStrVal != null ) {
            try {
                maxAcceptVal = dataValueOf(maxAcceptStrVal);
            } catch ( IllegalArgumentException ex ) {
                throw new IllegalArgumentException("invalid maximum acceptable value: \"" +
                        maxAcceptStrVal + "\"");
            }
        }
        if ( maxQuestionStrVal != null ) {
            try {
                maxQuestionVal = dataValueOf(maxQuestionStrVal);
            } catch ( IllegalArgumentException ex ) {
                throw new IllegalArgumentException("invalid maximum questionable value: \"" +
                        maxQuestionStrVal + "\"");
            }
        }
        validateLimits();
    }

    /**
     * Creates with (copies of) the values from the given DataColumnType along with other given values.
     *
     * @param dtype
     *         create with the variable name, ordering value, display name, description, and units from this data column
     *         type; cannot be null
     * @param standardName
     *         standard name for this data column type (duplicate standard names between data column types is
     *         acceptable);  if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     * @param categoryName
     *         category name for this data column type; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is
     *         assigned
     * @param fileStdUnit
     *         special name of the standard unit (corresponding to the first unit of the units array) to be used
     *         in the DSG files; if null, no special name will be used
     * @param minQuestionStrVal
     *         string representation of the minimum questionable value for standardized values of this data column type
     *         (less than this value is bad); if null, there is no minimum questionable value
     * @param minAcceptStrVal
     *         string representation of the minimum acceptable value for standardized values of this data column type
     *         (less than this value is questionable or bad); if null, there is no minimum acceptable value, although
     *         there still might be a minimum questionable value.
     * @param maxAcceptStrVal
     *         string representation of the maximum acceptable value for standardized values of this data column type
     *         (larger than this value is questionable or bad); if null, there is no maximum acceptable value, although
     *         there still might be a maximum questionable value.
     * @param maxQuestionStrVal
     *         string representation of the maximum questionable value for standardized values of this data column type
     *         (larger than this value is bad); if null, there is no maximum questionable value
     * @param roles
     *         the roles for this data type; cannot be null or empty
     *
     * @throws IllegalArgumentException
     *         if the variable name, sort order, display name, or roles is invalid;
     *         if the relationship: minQuestionVal <= minAcceptVal <= maxAcceptVal <= maxQuestionVal,
     *         for those values that are not null, is violated
     */
    public StringDashDataType(DataColumnType dtype, String standardName, String categoryName, String fileStdUnit,
            String minQuestionStrVal, String minAcceptStrVal, String maxAcceptStrVal, String maxQuestionStrVal,
            Collection<Role> roles) throws IllegalArgumentException {
        this(dtype.getVarName(), dtype.getSortOrder(), dtype.getDisplayName(), dtype.getDescription(),
                dtype.isCritical(), dtype.getUnits(), standardName, categoryName, fileStdUnit,
                minQuestionStrVal, minAcceptStrVal, maxAcceptStrVal, maxQuestionStrVal, roles);
    }

    @Override
    public String getDataClassName() {
        return String.class.getSimpleName();
    }

    @Override
    public String dataValueOf(String strVal) {
        if ( strVal == null )
            throw new IllegalArgumentException("no value given");
        return strVal.trim();
    }

    @Override
    public ValueConverter<String> getStandardizer(String inputUnit, String missingValue,
            StdUserDataArray stdArray) throws IllegalArgumentException, IllegalStateException {
        String outputUnit = units.get(0);
        if ( DashboardUtils.STRING_MISSING_VALUE.equals(outputUnit) )
            outputUnit = null;

        if ( (inputUnit == null) && (outputUnit == null) ) {
            ValueConverter<String> stdConverter = new ValueConverter<String>(inputUnit, outputUnit, missingValue) {
                @Override
                public String convertValueOf(String valueString) {
                    if ( isMissingValue(valueString, true) )
                        return null;
                    return dataValueOf(valueString);
                }
            };
            return stdConverter;
        }

        // timestamp, date, and time units handled here
        try {
            ValueConverter<String> stdConverter = new TimestampConverter(inputUnit, outputUnit, missingValue);
            return stdConverter;
        } catch ( IllegalArgumentException ex ) {
            // try another converter
        }

        throw new IllegalArgumentException("conversion from \"" +
                inputUnit + "\" to \"" + outputUnit + "\" is not supported");
    }

    @Override
    public String toString() {
        return "String" + super.toString();
    }

}
