/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Immutable data column type for defining standard types which can safely 
 * be used as keys.  Do to its limitations, cannot be a shared class.
 * 
 * @author Karl Smith
 */
public class DashDataType implements Comparable<DashDataType> {

	private static final String SORT_ORDER_TAG = "sort_order";
	private static final String DISPLAY_NAME_TAG = "display_name";
	private static final String DATA_CLASS_NAME_TAG = "data_class";
	private static final String DESCRIPTION_TAG = "description";
	private static final String STANDARD_NAME_TAG = "standard_name";
	private static final String CATEGORY_NAME_TAG = "category_name";
	private static final String UNITS_TAG = "units";
	
	private DataColumnType dataType;

	/**
	 * Create with (copies of) the given values.
	 * 
	 * @param varName
	 * 		name for a variable of this type; 
	 * 		cannot be null or blank
	 * @param sortOrder
	 * 		value giving the sort order for this type;
	 * 		cannot be null, NaN, or infinite 
	 * @param displayName
	 * 		displayed name for this column type;
	 * 		cannot be null or blank
	 * @param dataClassName
	 * 		name of the class for a variable of this type
	 * 		(e.g., Character, Double, Integer, String);
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param description
	 * 		description of a variable of this type;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param standardName
	 * 		standard name for a variable of this type
	 * 		(can duplicate standard names of other data column types);
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param categoryName
	 * 		category name for a variable of this type;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @param units
	 * 		unit strings associated with this type (copied);
	 * 		if null or empty, a list with only {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @throws IllegalArgumentException
	 * 		if the variable name, sort order, or display name is invalid
	 */
	public DashDataType(String varName, Double sortOrder, String displayName, 
			String dataClassName, String description, String standardName, 
			String categoryName, Collection<String> units) throws IllegalArgumentException {
		if ( (varName == null) || varName.trim().isEmpty() )
			throw new IllegalArgumentException("variable name is null or blank");
		if ( (sortOrder == null) || sortOrder.isNaN() || sortOrder.isInfinite() )
			throw new IllegalArgumentException("sorting order is null or blank");
		if ( (displayName == null) || displayName.trim().isEmpty() )
			throw new IllegalArgumentException("display name is null or blank");
		dataType = new DataColumnType(varName, sortOrder, displayName, 
				dataClassName, description, standardName, categoryName, units);
	}

	/**
	 * Creates with (copies of) the values from the given DataColumnType
	 * 
	 * @param dtype
	 * 		create with the information from this data column type
	 * @throws IllegalArgumentException
	 * 		if the variable name is null or blank
	 */
	public DashDataType(DataColumnType dtype) {
		this(dtype.getVarName(), dtype.getSortOrder(), dtype.getDisplayName(), 
			 dtype.getDataClassName(), dtype.getDescription(), 
			 dtype.getStandardName(), dtype.getCategoryName(), dtype.getUnits());
	}

	/**
	 * @return 
	 * 		the variable name for this data type;
	 * 		never null or blank
	 */
	public String getVarName() {
		return dataType.getVarName();
	}

	/**
	 * @return 
	 * 		the sorting order for this data type;
	 * 		never null but may be {@link DashboardUtils#FP_MISSING_VALUE}
	 */
	public Double getSortOrder() {
		return dataType.getSortOrder();
	}

	/**
	 * @return 
	 * 		the displayed name for this data type;
	 * 		never null or blank
	 */
	public String getDisplayName() {
		return dataType.getDisplayName();
	}

	/**
	 * @return 
	 * 		the data class name for this data type
	 * 		(e.g., Character, Double, Integer, String);
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getDataClassName() {
		return dataType.getDataClassName();
	}
	
	/**
	 * @return 
	 * 		description of a variable of this type;
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getDescription() {
		return dataType.getDescription();
	}

	/**
	 * @return 
	 * 		standard name of a variable of this type;
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getStandardName() {
		return dataType.getStandardName();
	}

	/**
	 * @return
	 * 		category name of a variable of this type;
	 * 		never null but may be {@link DashboardUtils#STRING_MISSING_VALUE}
	 */
	public String getCategoryName() {
		return dataType.getCategoryName();
	}

	/**
	 * @return 
	 * 		a copy of the units associated with this data type;
	 * 		never null or empty, but may only contain 
	 * 		{@link DashboardUtils#STRING_MISSING_VALUE}.
	 */
	public ArrayList<String> getUnits() {
		return new ArrayList<String>(dataType.getUnits());
	}

	/**
	 * Checks if the variable or displayed name of this data type 
	 * is equal, ignoring case and non-alphanumeric characters, 
	 * to the given name.
	 * 
	 * @param other
	 * 		data type to compare to
	 * @return
	 * 		whether the type names match
	 */
	public boolean typeNameEquals(String name) {
		if ( name == null )
			return false;
		String otherKey = DashboardServerUtils.getKeyForName(name);
		if ( DashboardServerUtils.getKeyForName(dataType.getVarName()).equals(otherKey) )
			return true;
		if ( DashboardServerUtils.getKeyForName(dataType.getDisplayName()).equals(otherKey) )
			return true;
		return false;
	}

	/**
	 * Checks if the variable or displayed name of this data type
	 * is equal, ignoring case and non-alphanumeric characters, 
	 * to either of those of the given data column type.
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
	 * Checks if the variable or displayed name of this data type
	 * is equal, ignoring case and non-alphanumeric characters, 
	 * to either of those of another data type.
	 * 
	 * @param other
	 * 		data type to compare to
	 * @return
	 * 		whether the type names match
	 */
	public boolean typeNameEquals(DashDataType other) {
		if ( this == other )
			return true;
		if ( other == null )
			return false;
		return typeNameEquals(other.dataType);
	}

	/**
	 * @return
	 * 		a DataColumnType constructed from the values in this DashDataType.
	 * 		Any mutable values in the DataColumnType are deep copies of the values.
	 */
	public DataColumnType duplicate() {
		return dataType.duplicate();
	}

	/**
	 * A QC flag type has a category name of {@link DashboardUtils#QUALITY_CATEGORY},
	 * a data class name of {@link DashboardUtils#CHAR_DATA_CLASS_NAME}, and has a
	 * variable name that starts with (case insensitive) "QC_" or "WOCE_" (or is
	 * just "QC" or "WOCE").
	 * 
	 * @return
	 * 		if this is a QC flag type
	 */
	public boolean isQCType() {
		return dataType.isQCType();
	}

	/**
	 * A (general) comment type has a data class name of 
	 * {@link DashboardUtils#STRING_DATA_CLASS_NAME} and a variable 
	 * name that contains (case insensitive) the word "COMMENT". 
	 * 
	 * @return
	 * 		if this type is a comment for the given data type
	 */
	public boolean isCommentType() {
		return dataType.isCommentType();
	}

	/**
	 * A comment type for another data type has a data class name of 
	 * {@link DashboardUtils#STRING_DATA_CLASS_NAME} and a variable name 
	 * that is (case insensitive) either: "COMMENT_" followed by the other 
	 * data variable name, or the other data variable name followed by 
	 * "_COMMENT". 
	 * 
	 * @param dtype
	 * 		given data type; cannot be null
	 * @return
	 * 		if this type is a comment for the given data type
	 */
	public boolean isCommentTypeFor(DataColumnType dtype) {
		return dataType.isCommentTypeFor(dtype);
	}

	/**
	 * A comment type for another data type has a data class name of 
	 * {@link DashboardUtils#STRING_DATA_CLASS_NAME} and a variable name 
	 * that is (case insensitive) either: "COMMENT_" followed by the other 
	 * data variable name, or the other data variable name followed by 
	 * "_COMMENT". 
	 * 
	 * @param dtype
	 * 		given data type; cannot be null
	 * @return
	 * 		if this type is a comment for the given data type
	 */
	public boolean isCommentTypeFor(DashDataType dtype) {
		return dataType.isCommentTypeFor(dtype.dataType);
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
		Double dval = dataType.getSortOrder();
		if ( ! DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, dval,  
				DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
			jsonObj.addProperty(SORT_ORDER_TAG, dval.toString());
		String value = dataType.getDisplayName();
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(value) )
			jsonObj.addProperty(DISPLAY_NAME_TAG, value);
		value = dataType.getDataClassName();
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(value) )
			jsonObj.addProperty(DATA_CLASS_NAME_TAG, value);
		value = dataType.getDescription();
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(value) )
			jsonObj.addProperty(DESCRIPTION_TAG, value);
		value = dataType.getStandardName();
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(value) )
			jsonObj.addProperty(STANDARD_NAME_TAG, value);
		value = dataType.getCategoryName();
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(value) )
			jsonObj.addProperty(CATEGORY_NAME_TAG, value);
		ArrayList<String> units = dataType.getUnits();
		if ( ! DashboardUtils.NO_UNITS.equals(units) ) {
			JsonArray jsonArr = new JsonArray();
			for ( String val : dataType.getUnits() )
				jsonArr.add(val);
			jsonObj.add(UNITS_TAG, jsonArr);
		}
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
	 * 		<li>tag {@link #STANDARD_NAME_TAG} gives the standard name,</li>
	 * 		<li>tag {@link #CATEGORY_NAME_TAG} gives the category name, and</li>
	 * 		<li>tag {@link #UNITS_TAG} gives the units array.</li>
	 * </ul>
	 * The sort order tag must be given and given a valid value.  
	 * The display name can be omitted in which case the variable name 
	 * is used.  Other tags can be omitted, in which case the default 
	 * values as described by {@link #DashDataType(String, Double, String, 
	 * String, String, String, String, Collection)} is used.  
	 * 
	 * @param varName
	 * 		the variable name for the DashDataType
	 * @param jsonDesc
	 * 		the JSON description string to parse
	 * @return
	 * 		the newly created DashDataType
	 * @throws IllegalArgumentException
	 * 		if the variable name is null or empty, or
	 * 		if the JSON description string cannot be parsed.
	 */
	static public DashDataType fromPropertyValue(String varName, 
			String jsonDesc) throws IllegalArgumentException {
		if ( (varName == null) || varName.trim().isEmpty() ) 
			throw new IllegalArgumentException("invalid variable name");

		JsonParser parser = new JsonParser();
		Double sortOrder = null;
		String displayName = null;
		String dataClassName = null;
		String description = null;
		String standardName = null;
		String categoryName = null;
		LinkedHashSet<String> units = null;
		try {
			String sortOrderStr = null;
			JsonObject jsonObj = parser.parse(jsonDesc).getAsJsonObject();
			for ( Entry<String, JsonElement> prop : jsonObj.entrySet() ) {
				String tag = prop.getKey();
				boolean identified = false;
				try {
					if ( SORT_ORDER_TAG.equals(tag) ) {
						sortOrderStr = prop.getValue().getAsString();
						identified = true;
					}
					else if ( DISPLAY_NAME_TAG.equals(tag) ) {
						displayName = prop.getValue().getAsString();
						identified = true;
					}
					else if ( DATA_CLASS_NAME_TAG.equals(tag) ) {
						dataClassName = prop.getValue().getAsString();
						identified = true;
					}
					else if ( DESCRIPTION_TAG.equals(tag) ) {
						description = prop.getValue().getAsString();
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
				} catch ( Exception ex ) {
					throw new IllegalArgumentException("value of \"" + tag + 
							"\" is not a string", ex);
				}
				if ( ( ! identified ) && UNITS_TAG.equals(tag) ) {
					try {
						units = new LinkedHashSet<String>();
						for ( JsonElement jsonElem : prop.getValue().getAsJsonArray() ) {
							units.add(jsonElem.getAsString());
							identified = true;
						}
					} catch ( Exception ex ) {
						throw new IllegalArgumentException("value of \"" + tag + 
								"\" is not an JSON string array of strings", ex);
					}
				}
				if ( ! identified )
					throw new IllegalArgumentException("unrecognized tag \"" + tag + "\"");
			}
			if ( sortOrderStr == null )
				throw new IllegalArgumentException("sort order tag \"" + SORT_ORDER_TAG + "\" is not given");
			try {
				sortOrder = Double.valueOf(sortOrderStr);
			}
			catch ( NumberFormatException ex ) {
				throw new IllegalArgumentException("invalid value for sort order tag: " + ex.getMessage());
			}
		} catch ( Exception ex ) {
			throw new IllegalArgumentException("Invalid JSON description of \"" + 
					varName + "\" : " + ex.getMessage(), ex);
		}
		if ( displayName == null )
			displayName = varName;
		return new DashDataType(varName, sortOrder, displayName, 
				dataClassName, description, standardName, categoryName, units);
	}

	@Override
	public int compareTo(DashDataType other) {
		return dataType.compareTo(other.dataType);
	}

	@Override
	public int hashCode() {
		return dataType.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof DashDataType) )
			return false;
		DashDataType other = (DashDataType) obj;
		if ( ! dataType.equals(other.dataType) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DashDataType[ " +
				"varName=\"" + dataType.getVarName() + "\", " +
				"sortOrder=" + dataType.getSortOrder().toString() + ", " +
				"displayName=\"" + dataType.getDisplayName() + "\", " +
				"dataClassName=\"" + dataType.getDataClassName() + "\", " +
				"description=\"" + dataType.getDescription() + "\", " +
				"standardName=\"" + dataType.getStandardName() + "\", " +
				"categoryName=\"" + dataType.getCategoryName() + "\", " +
				"units=" + dataType.getUnits().toString() + " ]";
	}

}
