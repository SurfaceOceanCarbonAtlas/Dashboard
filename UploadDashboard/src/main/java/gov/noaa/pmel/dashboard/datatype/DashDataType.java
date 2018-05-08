/**
 * 
 */
package gov.noaa.pmel.dashboard.datatype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import gov.noaa.pmel.dashboard.dsg.StdUserDataArray;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

/**
 * Base class for defining immutable standard data types.  Includes information 
 * for converting string representations, bounds-checking values, and creating 
 * variables in NetCDF files.
 * 
 * @author Karl Smith
 */
public abstract class DashDataType<T extends Comparable<T>> implements Comparable<DashDataType<?>> {

	public static final String UNREASONABLY_SMALL_MSG = " is less than the reasonable limit of ";
	public static final String QUESTIONABLY_SMALL_MSG = " is less than the acceptable limit of ";
	public static final String QUESTIONABLY_LARGE_MSG = " is more than the acceptable limit of ";
	public static final String UNREASONABLY_LARGE_MSG = " is more than the reasonable limit of ";

	public static final String DATA_CLASS_NAME_TAG = "data_class";
	public static final String SORT_ORDER_TAG = "sort_order";
	public static final String DISPLAY_NAME_TAG = "display_name";
	public static final String DESCRIPTION_TAG = "description";
	public static final String IS_CRITICAL_TAG = "is_critial";
	public static final String STANDARD_NAME_TAG = "standard_name";
	public static final String CATEGORY_NAME_TAG = "category_name";
	public static final String FILE_STD_UNIT_TAG = "file_std_unit";
	public static final String UNITS_TAG = "units";
	public static final String MIN_QUESTIONABLE_VALUE_TAG = "min_question_value";
	public static final String MIN_ACCEPTABLE_VALUE_TAG = "min_accept_value";
	public static final String MAX_ACCEPTABLE_VALUE_TAG = "max_accept_value";
	public static final String MAX_QUESTIONABLE_VALUE_TAG = "max_question_value";

	protected String varName;
	protected Double sortOrder;
	protected String displayName;
	protected String description;
	protected boolean isCritical;
	protected ArrayList<String> units;
	protected String standardName;
	protected String categoryName;
	protected String fileStdUnit;
	protected T minQuestionVal;
	protected T minAcceptVal;
	protected T maxAcceptVal;
	protected T maxQuestionVal;

	/**
	 * Create with the given values.  The values of minQuestionVal, minAcceptVal, 
	 * maxAcceptVal, and maxQuestionVal are set to null in this base class. 
	 * Subclasses need to expand their constructors to: have String arguments 
	 * representing these values, assign these values from those arguments, and 
	 * validate the value assigned using {@link #validateLimits()}).  The meaning
	 * of these values are:
	 * <dl>
	 * <dt>minQuestionVal</dt>
	 * 		<dd>minimum questionable value for standardized values of this data 
	 * 		minimum column type (less than this value is bad); if null, there is 
	 * 		no questionable value</dd>
	 * <dt>minAcceptVal</dt>
	 * 		<dd>minimum acceptable value for standardized values of this data 
	 * 		column type (less than this value is questionable or bad); if null, 
	 * 		there is no minimum acceptable value, although there still might be 
	 * 		a minimum questionable value.</dd>
	 * <dt>maxAcceptVal</dt>
	 * 		<dd>maximum acceptable value for standardized values of this data 
	 * 		column type (larger than this value is questionable or bad); if null, 
	 * 		there is no maximum acceptable value, although there still might be 
	 * 		a maximum questionable value.</dd>
	 * <dt>maxQuestionVal</dt>
	 * 		<dd>maximum questionable value for standardized values of this data 
	 * 		column type (larger than this value is bad); if null, there is no 
	 * 		maximum questionable value.</dd>
	 * </dl>
	 * 
	 * @param varName
	 * 		NetCDF variable name for this data column type; 
	 * 		cannot be null or blank
	 * @param sortOrder
	 * 		value used for ordering data column types;
	 * 		cannot be null, NaN, or infinite 
	 * @param displayName
	 * 		name displayed in the Dashboard UI for this data column type;
	 * 		cannot be null or blank
	 * @param description
	 * 		brief description of this data column type; if null, 
	 * 		{@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param isCritical
	 * 		if this data type is required to be present and all
	 * 		values must be valid
	 * @param units
	 * 		unit strings associated with this data column type.  
	 * 		A new ArrayList is created from the values in the given 
	 * 		collection.  The first unit in the list is considered the 
	 * 		standard unit;  values in other units will be converted to 
	 * 		this unit when standardized.  If null or empty, a list with 
	 * 		only {@link DashboardUtils#STRING_MISSING_VALUE} is created.
	 * @param standardName
	 * 		standard name for this data column type (duplicate standard 
	 * 		names between data column types is acceptable);  if null, 
	 * 		{@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param categoryName
	 * 		category name for this data column type; if null, 
	 * 		{@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param fileStdUnit
	 * 		name of the standard unit (corresponding to the first unit 
	 * 		of the units array) to be used in the DSG files; if null,
	 * 		the first unit string will be used
	 * @throws IllegalArgumentException
	 * 		if the variable name, sort order, or display name is invalid;
	 * 		if (in subclasses) the relationship
	 * 			minQuestionVal <= minAcceptVal <= maxAcceptVal <= maxQuestionVal,
	 * 		for those values that are not null, is violated.
	 */
	protected DashDataType(String varName, Double sortOrder, String displayName, 
			String description, boolean isCritical, Collection<String> units, 
			String standardName, String categoryName, String fileStdUnit) 
											throws IllegalArgumentException {
		if ( (varName == null) || varName.trim().isEmpty() )
			throw new IllegalArgumentException("data type variable name is invalid");
		this.varName = varName.trim();

		if ( (sortOrder == null) || sortOrder.isNaN() || sortOrder.isInfinite() )
			throw new IllegalArgumentException("data type ordering value is invalid");
		this.sortOrder = sortOrder;

		if ( (displayName == null) || displayName.trim().isEmpty() )
			throw new IllegalArgumentException("data type display name is invalid");
		this.displayName = displayName;

		if ( description != null)
			this.description = description.trim();
		else
			this.description = DashboardUtils.STRING_MISSING_VALUE;

		this.isCritical = isCritical;

		if ( units != null )
			this.units = new ArrayList<String>(units);
		else
			this.units = new ArrayList<String>(DashboardUtils.NO_UNITS);

		if ( standardName != null)
			this.standardName = standardName.trim();
		else
			this.standardName = DashboardUtils.STRING_MISSING_VALUE;

		if ( categoryName != null)
			this.categoryName = categoryName.trim();
		else
			this.categoryName = DashboardUtils.STRING_MISSING_VALUE;

		if ( fileStdUnit == null )
			this.fileStdUnit = this.units.get(0);
		else
			this.fileStdUnit = fileStdUnit;

		this.minQuestionVal = null;
		this.minAcceptVal = null;
		this.maxAcceptVal = null;
		this.maxQuestionVal = null;
	}

	/**
	 * @return
	 * 		the simple class name of the data that this data column type provides.
	 */
	public abstract String getDataClassName();

	/**
	 * @param strRepr
	 * 		string representation of the value to return
	 * @return
	 * 		value represented by the string
	 * @throws IllegalArgumentException
	 * 		if the string representation cannot be interpreted
	 */
	public abstract T dataValueOf(String strRepr) throws IllegalArgumentException;

	/**
	 * Returns a value converter to interpreting string representations to data 
	 * values of this type.  This converter use the given missing value string
	 * to interpret missing values.  If required, values should also be converted
	 * to the standard unit (the first unit in the units array) for this data type.
	 * 
	 * @param inputUnit
	 * 		unit/format of the input data string values.  If null, the value is
	 * 		unitless, and in this case the standard value should also be unitless 
	 * 		(empty string).  The value returned, if the string does not represent
	 * 		a missing value, should just be the value returned by 
	 * 		{@link #dataValueOf(String)}.
	 * @param missingValue
	 * 		missing value for this converter; if null, standard missing values 
	 * 		are to be used
	 * @param stdArray
	 * 		the standardized data that has been converted so far.  This is provided 
	 * 		for data conversions that require standardized data from other data 
	 * 		columns.
	 * @return
	 * 		a value converter, using the given missing value string, for converting 
	 * 		the data value strings in the given units to data values in standard units
	 * @throws IllegalArgumentException
	 * 		if there is no converter for performing this conversion
	 * @throws IllegalStateException
	 * 		if the converter depends on other data that has not yet been standardized
	 */
	public abstract ValueConverter<T> getStandardizer(String inputUnit, 
			String missingValue, StdUserDataArray stdArray) 
					throws IllegalArgumentException, IllegalStateException;

	/**
	 * Verifies that the values minQuestionVal, minAcceptVal, maxAcceptVal, and maxQuestionVal,
	 * for those values that are not null, satisfies the relationship: 
	 * 			minQuestionVal <= minAcceptVal <= maxAcceptVal <= maxQuestionVal
	 * @throws IllegalArgumentException
	 */
	protected void validateLimits() throws IllegalArgumentException {
		if ( minAcceptVal != null ) {
			if ( (minQuestionVal != null) && (minAcceptVal.compareTo(minQuestionVal) < 0) )
				throw new IllegalArgumentException("minimum acceptable value is less than minimum questionable value");
		}

		if ( maxAcceptVal != null ) {
			if ( (minAcceptVal != null) && (maxAcceptVal.compareTo(minAcceptVal) < 0) )
				throw new IllegalArgumentException("maximum acceptable value is less than minimum acceptable value");
			if ( (minQuestionVal != null) && (maxAcceptVal.compareTo(minQuestionVal) < 0) )
				throw new IllegalArgumentException("maximum acceptable value is less than minimum questionable value");
		}

		if ( maxQuestionVal != null ) {
			if ( (maxAcceptVal != null) && (maxQuestionVal.compareTo(maxAcceptVal) < 0) )
				throw new IllegalArgumentException("maximum questionable value is less than maximum acceptable value");
			if ( (minAcceptVal != null) && (maxQuestionVal.compareTo(minAcceptVal) < 0) )
				throw new IllegalArgumentException("maximum questionable value is less than minimum acceptable value");
			if ( (minQuestionVal != null) && (maxQuestionVal.compareTo(minQuestionVal) < 0) )
				throw new IllegalArgumentException("maximum questionable value is less than minimum questionable value");
		}
	}

	/**
	 * Perform bounds checking on the given standard value of this data type.
	 * 
	 * @param stdVal
	 * 		standard value to check
	 * @return
	 * 		null if the given value is null (missing) or within the acceptable range;
	 * 		otherwise, an message giving the severity and describing the problem 
	 * 		(both general comment and detailed comments are assigned).
	 */
	public ADCMessage boundsCheckStandardValue(T stdVal) {
		// If the value is missing, make no comment
		if ( stdVal == null )
			return null;
		// Check if unreasonable small
		if ( (minQuestionVal != null) && (minQuestionVal.compareTo(stdVal) > 0) ) {
			ADCMessage msg = new ADCMessage();
			if ( isCritical )
				msg.setSeverity(Severity.CRITICAL);
			else
				msg.setSeverity(Severity.ERROR);
			msg.setGeneralComment(displayName + 
					UNREASONABLY_SMALL_MSG + minQuestionVal.toString());
			msg.setDetailedComment(displayName + " value of " + stdVal.toString() + 
					UNREASONABLY_SMALL_MSG + minQuestionVal.toString());
			return msg;
		}
		// Check if unreasonably large
		if ( (maxQuestionVal != null) && (maxQuestionVal.compareTo(stdVal) < 0) ) {
			ADCMessage msg = new ADCMessage();
			if ( isCritical )
				msg.setSeverity(Severity.CRITICAL);
			else
				msg.setSeverity(Severity.ERROR);
			msg.setGeneralComment(displayName + 
					UNREASONABLY_LARGE_MSG + maxQuestionVal.toString());
			msg.setDetailedComment(displayName + " value of " + stdVal.toString() + 
					UNREASONABLY_LARGE_MSG + maxQuestionVal.toString());
			return msg;
		}
		// Check if questionably small
		if ( (minAcceptVal != null) && (minAcceptVal.compareTo(stdVal) > 0) ) {
			ADCMessage msg = new ADCMessage();
			msg.setSeverity(Severity.WARNING);
			msg.setGeneralComment(displayName + 
					QUESTIONABLY_SMALL_MSG + minAcceptVal.toString());
			msg.setDetailedComment(displayName + " value of " + stdVal.toString() + 
					QUESTIONABLY_SMALL_MSG + minAcceptVal.toString());
			return msg;
		}
		// Check if questionably large
		if ( (maxAcceptVal != null) && (maxAcceptVal.compareTo(stdVal) < 0) ) {
			ADCMessage msg = new ADCMessage();
			msg.setSeverity(Severity.WARNING);
			msg.setGeneralComment(displayName + 
					QUESTIONABLY_LARGE_MSG + maxAcceptVal.toString());
			msg.setDetailedComment(displayName + " value of " + stdVal.toString() + 
					QUESTIONABLY_LARGE_MSG + maxAcceptVal.toString());
			return msg;
		}
		return null;
	}

	/**
	 * @return 
	 * 		the variable name for this data type; never null or blank
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * @return 
	 * 		the sorting order for this data type; never null, NaN, or infinite
	 */
	public Double getSortOrder() {
		return sortOrder;
	}

	/**
	 * @return 
	 * 		the displayed name for this data type; never null or blank
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return 
	 * 		description of a variable of this type; never null 
	 * 		but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return 
	 * 		standard name of a variable of this type; never null 
	 * 		but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getStandardName() {
		return standardName;
	}

	/**
	 * @return
	 * 		category name of a variable of this type; never null 
	 * 		but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @return
	 * 		if this data type is required to be present 
	 * 		and all value required to be valid
	 */
	public boolean isCritical() {
		return isCritical;
	}

	/**
	 * @return 
	 * 		a copy of the units associated with this data type; never null or empty, 
	 * 		but may only contain {@link DashboardUtils#STRING_MISSING_VALUE}.
	 */
	public ArrayList<String> getUnits() {
		return new ArrayList<String>(units);
	}

	/**
	 * @return
	 * 		name of the standard unit (corresponding to the first unit 
	 * 		in the unit array) to be used as the unit string in DSG files;
	 * 		never null
	 */
	public String getFileStdUnit() {
		return fileStdUnit;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = 0;

		// Most significant items last (in case there is overflow)
		if ( minQuestionVal != null)
			result += minQuestionVal.hashCode();

		result *= prime;
		if ( minAcceptVal != null)
			result += minAcceptVal.hashCode();

		result *= prime;
		if ( maxAcceptVal != null)
			result += maxAcceptVal.hashCode();

		result *= prime;
		if ( maxQuestionVal != null)
			result += maxQuestionVal.hashCode();

		for ( String val : units )
			result = prime * result + val.hashCode();
		result = prime * result + Integer.hashCode(units.size());

		result = result * prime + fileStdUnit.hashCode();
		result = prime * result + categoryName.hashCode();
		result = prime * result + standardName.hashCode();
		result = result * prime + Boolean.valueOf(isCritical).hashCode();
		result = prime * result + description.hashCode();
		result = prime * result + getDataClassName().hashCode();
		result = prime * result + varName.hashCode();
		result = prime * result + displayName.hashCode();
		result = prime * result + sortOrder.hashCode();

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		if ( getClass() != obj.getClass() )
			return false;
		DashDataType<?> other = (DashDataType<?>) obj;

		if ( ! sortOrder.equals(other.sortOrder) )
			return false;
		if ( isCritical != other.isCritical() )
			return false;
		if ( ! displayName.equals(other.displayName) )
			return false;
		if ( ! varName.equals(other.varName) )
			return false;
		if ( ! getDataClassName().equals(other.getDataClassName()) )
			return false;
		if ( ! description.equals(other.description) )
			return false;
		if ( ! standardName.equals(other.standardName) )
			return false;
		if ( ! categoryName.equals(other.categoryName) )
			return false;
		if ( ! fileStdUnit.equals(other.fileStdUnit) )
			return false;

		if ( ! units.equals(other.units) )
			return false;

		if ( minQuestionVal == null ) {
			if ( other.minQuestionVal != null )
				return false;
		}
		else if ( ! minQuestionVal.equals(other.minQuestionVal) )
			return false;

		if ( minAcceptVal == null ) {
			if ( other.minAcceptVal != null )
				return false;
		}
		else if ( ! minAcceptVal.equals(other.minAcceptVal) )
			return false;

		if ( maxAcceptVal == null ) {
			if ( other.maxAcceptVal != null )
				return false;
		}
		else if ( ! maxAcceptVal.equals(other.maxAcceptVal) )
			return false;

		if ( maxQuestionVal == null ) {
			if ( other.maxQuestionVal != null )
				return false;
		}
		else if ( ! maxQuestionVal.equals(other.maxQuestionVal) )
			return false;

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(DashDataType<?> other) {
		// This first check should be all that is needed
		int result = sortOrder.compareTo(other.sortOrder);
		if ( result != 0 )
			return result;

		// But to be complete....
		result = displayName.compareTo(other.displayName);
		if ( result != 0 )
			return result;
		result = varName.compareTo(other.varName);
		if ( result != 0 )
			return result;
		result = Boolean.valueOf(isCritical).compareTo(Boolean.valueOf(other.isCritical));
		if ( result != 0 )
			return result;

		// This check should ensures the cast of the limits will succeed
		result = getDataClassName().compareTo(other.getDataClassName());
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
		result = fileStdUnit.compareTo(other.fileStdUnit);

		result = Integer.compare(units.size(), other.units.size());
		if ( result != 0 )
			return result;
		for (int k = 0; k < units.size(); k++) {
			result = units.get(k).compareTo(other.units.get(k));
			if ( result != 0 )
				return result;
		}

		if ( minQuestionVal == null ) {
			if ( other.minQuestionVal != null )
				return -1;
		}
		else if ( other.minQuestionVal == null ) {
			return 1;
		}
		else {
			result = minQuestionVal.compareTo((T) other.minQuestionVal);
			if ( result != 0 )
				return result;
		}
		
		if ( minAcceptVal == null ) {
			if ( other.minAcceptVal != null )
				return -1;
		}
		else if ( other.minAcceptVal == null ) {
			return 1;
		}
		else {
			result = minAcceptVal.compareTo((T) other.minAcceptVal);
			if ( result != 0 )
				return result;
		}
		
		if ( maxAcceptVal == null ) {
			if ( other.maxAcceptVal != null )
				return -1;
		}
		else if ( other.maxAcceptVal == null ) {
			return 1;
		}
		else {
			result = maxAcceptVal.compareTo((T) other.maxAcceptVal);
			if ( result != 0 )
				return result;
		}
		
		if ( maxQuestionVal == null ) {
			if ( other.maxQuestionVal != null )
				return -1;
		}
		else if ( other.maxQuestionVal == null ) {
			return 1;
		}
		else {
			result = maxQuestionVal.compareTo((T) other.maxQuestionVal);
			if ( result != 0 )
				return result;
		}

		return 0;
	}

	@Override
	public String toString() {
		return "DashDataType[varName=" + varName + 
				", sortOrder=" + sortOrder.toString() + 
				", displayName=" + displayName + 
				", description=" + description + 
				", isCritical=" + isCritical +
				", units=" + units + 
				", standardName=" + standardName + 
				", categoryName=" + categoryName + 
				", fileStdUnit=" + fileStdUnit + 
				", minQuestionVal=" + minQuestionVal + 
				", minAcceptVal=" + minAcceptVal + 
				", maxAcceptVal=" + maxAcceptVal + 
				", maxQuestionVal=" + maxQuestionVal + 
				"]";
	}

	/**
	 * Checks if the variable or displayed name of this data type is equal, 
	 * ignoring case and non-alphanumeric characters, to the given name.
	 * 
	 * @param name
	 * 		name to use
	 * @return
	 * 		whether the type names match
	 */
	public boolean typeNameEquals(String name) {
		if ( name == null )
			return false;
		String otherKey = DashboardServerUtils.getKeyForName(name);
		if ( DashboardServerUtils.getKeyForName(varName).equals(otherKey) )
			return true;
		if ( DashboardServerUtils.getKeyForName(displayName).equals(otherKey) )
			return true;
		return false;
	}

	/**
	 * Checks if the variable or displayed name of this data type is equal, 
	 * ignoring case and non-alphanumeric characters, to either of those of 
	 * the given data column type.
	 * 
	 * @param other
	 * 		data column type to compare to
	 * @return
	 * 		whether the type names match
	 */
	public boolean typeNameEquals(DataColumnType other) {
		if ( other == null )
			return false;
		if ( typeNameEquals(other.getVarName()) )
			return true;
		if ( typeNameEquals(other.getDisplayName()) )
			return true;
		return false;
	}

	/**
	 * Checks if the variable or displayed name of this data type is equal, 
	 * ignoring case and non-alphanumeric characters, to either of those of 
	 * the given data column type.
	 * 
	 * @param other
	 * 		data type to compare to
	 * @return
	 * 		whether the type names match
	 */
	public boolean typeNameEquals(DashDataType<?> other) {
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
	 * 		a new DataColumnType constructed from the values in this DashDataType.
	 */
	public DataColumnType duplicate() {
		return new DataColumnType(varName, sortOrder, displayName, description, isCritical, units);
	}

	/**
	 * A QC flag type is a {@link CharDashDataType} with a category name of 
	 * {@link DashboardServerUtils#QUALITY_CATEGORY} and a variable name that 
	 * (case-insensitive) either starts with "QC_" or "WOCE_", ends with "_QC" 
	 * or "_WOCE", or is just "QC" or "WOCE".
	 * 
	 * @return
	 * 		if this is a QC flag type
	 */
	public boolean isQCType() {
		if ( ! (this instanceof CharDashDataType) )
			return false;
		if ( ! categoryName.equals(DashboardServerUtils.QUALITY_CATEGORY) )
			return false;
		String ucvarname = varName.toUpperCase();
		if ( ucvarname.equals("QC") || ucvarname.equals("WOCE") )
			return true;
		if ( ucvarname.startsWith("QC_") || ucvarname.startsWith("WOCE_") )
			return true;
		if ( ucvarname.endsWith("_QC") ||ucvarname.endsWith("_WOCE") )
			return true;
		return false;
	}

	/**
	 * A QC flag type for another data type is a {@link CharDashDataType} with 
	 * a category name of {@link DashboardServerUtils#QUALITY_CATEGORY} and a 
	 * variable name that is (case insensitive): "WOCE_" or "QC_" followed 
	 * by the other data variable name, the other data variable name 
	 * followed by "_WOCE" or "_QC", or is the other data variable name with 
	 * "_WOCE_" or "_QC_" inserted.
	 * 
	 * @param dtype
	 * 		given data type; cannot be null
	 * @return
	 * 		if this type is a QC flag type for the given data type
	 */
	public boolean isQCTypeFor(DashDataType<?> dtype) {
		if ( ! (this instanceof CharDashDataType) )
			return false;
		if ( ! categoryName.equals(DashboardServerUtils.QUALITY_CATEGORY) )
			return false;
		String ucname = dtype.varName.toUpperCase();
		String ucvarname = varName.toUpperCase();
		if ( ucvarname.equals("WOCE_" + ucname) )
			return true;
		if ( ucvarname.equals("QC_" + ucname) )
			return true;
		if ( ucvarname.equals(ucname + "_WOCE") )
			return true;
		if ( ucvarname.equals(ucname + "_QC") )
			return true;
		int idx = ucvarname.indexOf("_WOCE_");
		if ( (idx >= 0) && ucname.equals(ucvarname.substring(0, idx) + ucvarname.substring(idx+9)) )
			return true;
		idx = ucvarname.indexOf("_QC_");
		if ( (idx >= 0) && ucname.equals(ucvarname.substring(0, idx) + ucvarname.substring(idx+9)) )
			return true;
		return false;
	}

	/**
	 * A (general) comment type is a {@link StringDashDataType} with a variable name 
	 * that (case-insensitive) starts with "COMMENT_", ends with "_COMMENT", 
	 * contains "_COMMENT_", or is just "COMMENT".
	 * 
	 * @return
	 * 		if this type is a comment
	 */
	public boolean isCommentType() {
		if ( ! (this instanceof StringDashDataType) )
			return false;
		String ucvarname = varName.toUpperCase();
		if ( ucvarname.equals("COMMENT") )
			return true;
		if ( ucvarname.startsWith("COMMENT_") )
			return true;
		if ( ucvarname.endsWith("_COMMENT") )
			return true;
		if ( ucvarname.contains("_COMMENT_") )
			return true;
		return false;
	}

	/**
	 * A comment type for another data type is a {@link StringDashDataType}  
	 * with a variable name that is (case insensitive): "COMMENT_" followed 
	 * by the other data variable name, the other data variable name 
	 * followed by "_COMMENT", or is the other data variable name with 
	 * "_COMMENT_" inserted.
	 * 
	 * @param dtype
	 * 		given data type; cannot be null
	 * @return
	 * 		if this type is a comment for the given data type
	 */
	public boolean isCommentTypeFor(DashDataType<?> dtype) {
		if ( ! (this instanceof StringDashDataType) )
			return false;
		String ucname = dtype.varName.toUpperCase();
		String ucvarname = varName.toUpperCase();
		if ( ucvarname.equals("COMMENT_" + ucname) )
			return true;
		if ( ucvarname.equals(ucname + "_COMMENT") )
			return true;
		int idx = ucvarname.indexOf("_COMMENT_");
		if ( (idx >= 0) && ucname.equals(ucvarname.substring(0, idx) + ucvarname.substring(idx+9)) )
			return true;
		return false;
	}

	/**
	 * Creates a JSON description string of this data types that can be
	 * used as a Property value with a key that is the variable name 
	 * of this data type.  The data types can be regenerated from the
	 * variable name and this JSON description string using 
	 * {@link #fromPropertyValue(String, String)}
	 * 
	 * @return
	 * 		the JSON description string of this data type
	 */
	public String toPropertyValue() {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty(DATA_CLASS_NAME_TAG, getDataClassName());
		jsonObj.addProperty(SORT_ORDER_TAG, sortOrder.toString());
		jsonObj.addProperty(DISPLAY_NAME_TAG, displayName);
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(description) )
			jsonObj.addProperty(DESCRIPTION_TAG, description);
		if ( isCritical ) 
			jsonObj.addProperty(IS_CRITICAL_TAG, Boolean.valueOf(isCritical).toString());
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(fileStdUnit) )
			jsonObj.addProperty(FILE_STD_UNIT_TAG, fileStdUnit);
		if ( ! DashboardUtils.NO_UNITS.equals(units) ) {
			JsonArray jsonArr = new JsonArray();
			for ( String val : units )
				jsonArr.add(val);
			jsonObj.add(UNITS_TAG, jsonArr);
		}
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(standardName) )
			jsonObj.addProperty(STANDARD_NAME_TAG, standardName);
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(categoryName) )
			jsonObj.addProperty(CATEGORY_NAME_TAG, categoryName);
		if ( minQuestionVal != null )
			jsonObj.addProperty(MIN_QUESTIONABLE_VALUE_TAG, minQuestionVal.toString());
		if ( minAcceptVal != null )
			jsonObj.addProperty(MIN_ACCEPTABLE_VALUE_TAG, minAcceptVal.toString());
		if ( maxAcceptVal != null )
			jsonObj.addProperty(MAX_ACCEPTABLE_VALUE_TAG, maxAcceptVal.toString());
		if ( maxQuestionVal != null )
			jsonObj.addProperty(MAX_QUESTIONABLE_VALUE_TAG, maxQuestionVal.toString());
		return jsonObj.toString();
	}

	/**
	 * Create a DashDataType with the given variable name (Property key)
	 * using the given JSON description string (Property value) where: 
	 * <ul>
	 * 		<li>tag {@link #SORT_ORDER_TAG} gives the sort order value, </li>
	 * 		<li>tag {@link #DISPLAY_NAME_TAG} gives the data display name,</li>
	 * 		<li>tag {@link #DATA_CLASS_NAME_TAG} gives the data class name,</li>
	 * 		<li>tag {@link #DESCRIPTION_TAG} gives the data type description,</li>
	 * 		<li>tag {@link #IS_CRITICAL_TAG} indicates if this data type must be present and valid,</li>
	 * 		<li>tag {@link #UNITS_TAG} gives the units array,</li>
	 * 		<li>tag {@link #STANDARD_NAME_TAG} gives the standard name,</li>
	 * 		<li>tag {@link #CATEGORY_NAME_TAG} gives the category name,</li>
	 * 		<li>tag {@link #FILE_STD_UNIT_TAG} gives name of the standard unit (first unit in units array) for DSG files,</li>
	 * 		<li>tag {@link #MIN_QUESTIONABLE_VALUE_TAG} gives the minimum questionable value,</li>
	 * 		<li>tag {@link #MIN_ACCEPTABLE_VALUE_TAG} gives the minimum acceptable value,</li>
	 * 		<li>tag {@link #MAX_ACCEPTABLE_VALUE_TAG} gives the maximum acceptable value, and</li>
	 * 		<li>tag {@link #MAX_QUESTIONABLE_VALUE_TAG} gives the maximum questionable value,</li>
	 * </ul>
	 * The data class name tag, sort order tag, and display name tag must all be 
	 * given with valid values.  Other tags can be omitted, in which case null is 
	 * passed for that parameter to the constructor of the appropriate subclass 
	 * (based on the data class name) of {@link #DashDataType}.  
	 * 
	 * @param varName
	 * 		the variable name for the DashDataType
	 * @param jsonDesc
	 * 		the JSON description string to parse
	 * @return
	 * 		the newly created DashDataType subclass
	 * @throws IllegalArgumentException
	 * 		if the variable name is null or empty, 
	 * 		if the JSON description string cannot be parsed, or
	 * 		if a required tag is missing from the JSON description
	 */
	static public DashDataType<?> fromPropertyValue(String varName, 
			String jsonDesc) throws IllegalArgumentException {
		if ( (varName == null) || varName.trim().isEmpty() ) 
			throw new IllegalArgumentException("null or empty variable name given");
		try {
			String dataClassName = null;
			String sortOrderStr = null;
			String displayName = null;
			String description = null;
			boolean isCritical = false;
			LinkedHashSet<String> units = null;
			String standardName = null;
			String categoryName = null;
			String fileStdUnit = null;
			String minQuestionStrVal = null;
			String minAcceptStrVal = null;
			String maxAcceptStrVal = null;
			String maxQuestionStrVal = null;

			JsonParser parser = new JsonParser();
			JsonObject jsonObj = parser.parse(jsonDesc).getAsJsonObject();
			for ( Entry<String, JsonElement> prop : jsonObj.entrySet() ) {
				String tag = prop.getKey();
				boolean identified = false;
				try {
					if ( DATA_CLASS_NAME_TAG.equals(tag) ) {
						dataClassName = prop.getValue().getAsString();
						identified = true;
					}
					else if ( SORT_ORDER_TAG.equals(tag) ) {
						sortOrderStr = prop.getValue().getAsString();
						identified = true;
					}
					else if ( DISPLAY_NAME_TAG.equals(tag) ) {
						displayName = prop.getValue().getAsString();
						identified = true;
					}
					else if ( DESCRIPTION_TAG.equals(tag) ) {
						description = prop.getValue().getAsString();
						identified = true;
					}
					else if ( IS_CRITICAL_TAG.equals(tag) ) {
						isCritical = Boolean.valueOf(prop.getValue().getAsString());
						identified = true;
					}
					else if ( STANDARD_NAME_TAG.equals(tag) ) {
						standardName = prop.getValue().getAsString();
						identified = true;
					}
					else if ( CATEGORY_NAME_TAG.equals(tag) ) {
						categoryName = prop.getValue().getAsString();
						identified = true;
					}
					else if ( FILE_STD_UNIT_TAG.equals(tag) ) {
						fileStdUnit = prop.getValue().getAsString();
						identified = true;
					}
					else if ( MIN_QUESTIONABLE_VALUE_TAG.equals(tag) ) {
						minQuestionStrVal = prop.getValue().getAsString();
						identified = true;
					}
					else if ( MIN_ACCEPTABLE_VALUE_TAG.equals(tag) ) {
						minAcceptStrVal = prop.getValue().getAsString();
						identified = true;
					}
					else if ( MAX_ACCEPTABLE_VALUE_TAG.equals(tag) ) {
						maxAcceptStrVal = prop.getValue().getAsString();
						identified = true;
					}
					else if ( MAX_QUESTIONABLE_VALUE_TAG.equals(tag) ) {
						maxQuestionStrVal = prop.getValue().getAsString();
						identified = true;
					}
				} catch ( Exception ex ) {
					throw new IllegalArgumentException("value of \"" + tag + 
							"\" is not a string", ex);
				}
				if ( ( ! identified ) && UNITS_TAG.equals(tag) ) {
					try {
						units = new LinkedHashSet<String>();
						for ( JsonElement jsonElem : prop.getValue().getAsJsonArray() )
							units.add(jsonElem.getAsString());
						identified = true;
					} catch ( Exception ex ) {
						throw new IllegalArgumentException("value of \"" + UNITS_TAG + 
								"\" is not an JSON string array of strings", ex);
					}
				}
				if ( ! identified )
					throw new IllegalArgumentException("unrecognized tag \"" + tag + "\"");
			}

			if ( dataClassName == null )
				throw new IllegalArgumentException("data class name tag \"" + 
						DATA_CLASS_NAME_TAG + "\" is not given");
			if ( displayName == null )
				throw new IllegalArgumentException("display name tag \"" + 
						DISPLAY_NAME_TAG + "\" is not given");
			if ( sortOrderStr == null )
				throw new IllegalArgumentException("sort order tag \"" + 
						SORT_ORDER_TAG + "\" is not given");
			Double sortOrder;
			try {
				sortOrder = Double.valueOf(sortOrderStr);
			}
			catch ( NumberFormatException ex ) {
				throw new IllegalArgumentException("invalid value for sort order tag \"" + 
						SORT_ORDER_TAG + "\": " + ex.getMessage());
			}

			if ( dataClassName.equals(String.class.getSimpleName()) ) {
				return new StringDashDataType(varName, sortOrder, displayName, description, 
						isCritical, units, standardName, categoryName, fileStdUnit, 
						minQuestionStrVal, minAcceptStrVal, maxAcceptStrVal, maxQuestionStrVal);
			}

			if ( dataClassName.equals(Character.class.getSimpleName()) ) {
				return new CharDashDataType(varName, sortOrder, displayName, description, 
						isCritical, units, standardName, categoryName, fileStdUnit, 
						minQuestionStrVal, minAcceptStrVal, maxAcceptStrVal, maxQuestionStrVal);
			}

			if ( dataClassName.equals(Integer.class.getSimpleName()) ) {
				return new IntDashDataType(varName, sortOrder, displayName, description, 
						isCritical, units, standardName, categoryName, fileStdUnit, 
						minQuestionStrVal, minAcceptStrVal, maxAcceptStrVal, maxQuestionStrVal);
			}

			if ( dataClassName.equals(Double.class.getSimpleName()) ) {
				return new DoubleDashDataType(varName, sortOrder, displayName, description, 
						isCritical, units, standardName, categoryName, fileStdUnit, 
						minQuestionStrVal, minAcceptStrVal, maxAcceptStrVal, maxQuestionStrVal);
			}

			throw new IllegalArgumentException("Unknown data class name \"" + dataClassName + "\"");

		} catch ( Exception ex ) {
			throw new IllegalArgumentException("Invalid JSON description of \"" + 
					varName + "\" : " + ex.getMessage(), ex);
		}
	}

}
