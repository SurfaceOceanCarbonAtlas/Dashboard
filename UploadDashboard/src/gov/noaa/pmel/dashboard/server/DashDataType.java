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
public class DashDataType {

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
	 * @param displayName
	 * 		displayed name for this column type;
	 * 		if null or blank, varName is used
	 * @param varName
	 * 		name for a variable of this type; 
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
	 * 		if the variable name is null or blank
	 */
	public DashDataType(String displayName, String varName, String dataClassName, 
			String description, String standardName, String categoryName, 
				Collection<String> units) throws IllegalArgumentException {
		dataType = new DataColumnType(displayName, varName, dataClassName, 
				description, standardName, categoryName, units);
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
		this(dtype.getDisplayName(), dtype.getVarName(), dtype.getDataClassName(), 
				dtype.getDescription(), dtype.getStandardName(), 
				dtype.getCategoryName(), dtype.getUnits());
	}

	/**
	 * @return 
	 * 		the displayed name for this data column type;
	 * 		never null or blank
	 */
	public String getDisplayName() {
		return dataType.getDisplayName();
	}

	/**
	 * @return 
	 * 		the variable name for this data column type;
	 * 		never null or blank
	 */
	public String getVarName() {
		return dataType.getVarName();
	}

	/**
	 * @return 
	 * 		the data class name for this data column type
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
	 * 		a copy of the units associated with this data column type;
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
	 * 		data column type to compare to
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
	 * to either of those of this given data column type.
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
	 * 		<li>tag {@value #DISPLAY_NAME_TAG} gives the data display name,</li>
	 * 		<li>tag {@value #DATA_CLASS_NAME_TAG} gives the data class name,</li>
	 * 		<li>tag {@value #DESCRIPTION_TAG} gives the data type description,</li>
	 * 		<li>tag {@value #STANDARD_NAME_TAG} gives the standard name,</li>
	 * 		<li>tag {@value #CATEGORY_NAME_TAG} gives the category name, and</li>
	 * 		<li>tag {@value #UNITS_TAG} gives the units array.</li>
	 * </ul>
	 * Tags can be omitted, in which case the default values as described by
	 * {@link #DashDataType(String, String, String, String, String, String, Collection)}
	 * is used.
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
		String displayName = null;
		String dataClassName = null;
		String description = null;
		String standardName = null;
		String categoryName = null;
		LinkedHashSet<String> units = null;
		try {
			JsonObject jsonObj = parser.parse(jsonDesc).getAsJsonObject();
			for ( Entry<String, JsonElement> prop : jsonObj.entrySet() ) {
				String tag = prop.getKey();
				boolean identified = false;
				try {
					if ( DISPLAY_NAME_TAG.equals(tag) ) {
						displayName = prop.getValue().getAsString();
						identified = true;
					}
					if ( DATA_CLASS_NAME_TAG.equals(tag) ) {
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
		} catch ( Exception ex ) {
			throw new IllegalArgumentException("Invalid JSON description of \"" + 
					varName + "\" : " + ex.getMessage(), ex);
		}
		return new DashDataType(displayName, varName, dataClassName, 
				description, standardName, categoryName, units);
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
				"displayName=\"" + dataType.getDisplayName() + "\", " +
				"varName=\"" + dataType.getVarName() + "\", " +
				"dataClassName=\"" + dataType.getDataClassName() + "\", " +
				"description=\"" + dataType.getDescription() + "\", " +
				"standardName=\"" + dataType.getStandardName() + "\", " +
				"categoryName=\"" + dataType.getCategoryName() + "\", " +
				"units=" + dataType.getUnits().toString() + " ]";
	}

}
