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

	private DataColumnType dataType;

	/**
	 * Create with (copies of) the given values.
	 * 
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
	 */
	public DashDataType(String varName, String dataClassName, String description, 
			String standardName, String categoryName, Collection<String> units) {
		String myVarName;
		if ( varName != null )
			myVarName = varName.trim();
		else
			myVarName = "";
		if ( myVarName.isEmpty() )
			throw new IllegalArgumentException("varName is null or blank");
		dataType = new DataColumnType(myVarName, dataClassName, description, standardName, categoryName, units);
	}

	/**
	 * Creates with (copies of) the values from the given DataColumnType
	 * 
	 * @param dtype
	 * 		create with the variable name, data class type, description, standard name, 
	 * 		category name, and units from this data column type
	 */
	public DashDataType(DataColumnType dtype) {
		this(dtype.getVarName(), dtype.getDataClassName(), dtype.getDescription(), 
				dtype.getStandardName(), dtype.getCategoryName(), dtype.getUnits());
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
	 * Checks if the upper-cased variable name of this DashDataType 
	 * is equal to that of another.
	 * 
	 * @param other
	 * 		data column type to compare to
	 * @return
	 * 		whether the "types" match
	 */
	public boolean typeNameEquals(DashDataType other) {
		if ( this == other )
			return true;
		if ( other == null )
			return false;
		return dataType.typeNameEquals(other.dataType);
	}

	/**
	 * Checks if the upper-cased variable name of this DashDataType 
	 * is equal to that of the given DataColumnType.
	 * 
	 * @param other
	 * 		data column type to compare to
	 * @return
	 * 		whether the "types" match
	 */
	public boolean typeNameEquals(DataColumnType other) {
		return dataType.typeNameEquals(other);
	}

	/**
	 * Checks if the variable name and data class name of this DashDataType 
	 * is equal to that of another.
	 * 
	 * @param other
	 * 		data column type to compare to
	 * @return
	 * 		whether the types names match
	 */
	public boolean typeEquals(DashDataType other) {
		if ( this == other )
			return true;
		if ( other == null )
			return false;
		return dataType.typeEquals(other.dataType);
	}

	/**
	 * Checks if the variable name and data class name of this DashDataType 
	 * is equal to that of the given DataColumnType.
	 * 
	 * @param other
	 * 		data column type to compare to
	 * @return
	 * 		whether the types names match
	 */
	public boolean typeEquals(DataColumnType other) {
		return dataType.typeEquals(other);
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
	 * @return
	 * 		a JSON description string that can be used as a Property values
	 * 		with a key that is the variable name.
	 */
	public String toPropertyValue() {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("dataClassName", dataType.getDataClassName());
		String value = dataType.getDescription();
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(value) )
			jsonObj.addProperty("description", value);
		value = dataType.getStandardName();
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(value) )
			jsonObj.addProperty("standardName", value);
		value = dataType.getCategoryName();
		if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(value) )
			jsonObj.addProperty("categoryName", value);
		ArrayList<String> units = dataType.getUnits();
		if ( ! DataColumnType.NO_UNITS.equals(units) ) {
			JsonArray jsonArr = new JsonArray();
			for ( String val : dataType.getUnits() )
				jsonArr.add(val);
			jsonObj.add("units", jsonArr);
		}
		return jsonObj.toString();
	}

	/**
	 * Create a DashDataType with the given variable name (Property key)
	 * using the given JSON description string (Property value)
	 * given by jsonDesc where:
	 * 		tag: "dataClassName" gives the data class name,
	 * 		tag: "description" gives the data type description,
	 * 		tag: "standardName" gives the standard name,
	 * 		tag: "categoryName" gives the category name, and
	 * 		tag: "units" gives the units array.
	 * The data class name must be given, but other tags may be omitted 
	 * in which case the DashDataType default value is assigned.
	 * 
	 * @param varName
	 * 		the variable name for the DashDataType
	 * @param jsonDesc
	 * 		the JSON description string to parse
	 * @return
	 * 		the newly created DashDataType
	 * @throws IllegalArgumentException
	 * 		if the JSON description string cannot be parsed.
	 */
	static public DashDataType fromPropertyValue(String varName, String jsonDesc) {
		JsonParser parser = new JsonParser();
		String dataClassName = null;
		String description = null;
		String standardName = null;
		String categoryName = null;
		LinkedHashSet<String> units = null;
		try {
			JsonObject jsonObj = parser.parse(jsonDesc).getAsJsonObject();
			for ( Entry<String, JsonElement> prop : jsonObj.entrySet() ) {
				String tag = prop.getKey();
				try {
					if ( "dataClassName".equals(tag) ) {
						dataClassName = prop.getValue().getAsString();
					}
					else if ( "description".equals(tag) ) {
						description = prop.getValue().getAsString();
					}
					else if ( "standardName".equals(tag) ) {
						standardName = prop.getValue().getAsString();
					}
					else if ( "categoryName".equals(tag) ) {
						categoryName = prop.getValue().getAsString();
					}
				} catch ( Exception ex ) {
					throw new IllegalArgumentException("value of \"" + tag + 
							"\" is not a string");
				}
				try {
					if ( "units".equals(tag) ) {
						units = new LinkedHashSet<String>();
						for ( JsonElement jsonElem : prop.getValue().getAsJsonArray() ) {
							units.add(jsonElem.getAsString());
						}
					}
				} catch ( Exception ex ) {
					throw new IllegalArgumentException("value of \"" + tag + 
							"\" is not an JSON array of strings");
				}
			}
		} catch ( Exception ex ) {
			throw new IllegalArgumentException("Problems parsing the JSON description '" + 
					jsonDesc + "' : " + ex.getMessage());
		}
		return new DashDataType(varName, dataClassName, description, standardName, categoryName, units);
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
		return "DashDataType[varName=" + dataType.getVarName() + 
				", dataClassName=" + dataType.getDataClassName() + 
				", description=" + dataType.getDescription() + 
				", standardName=" + dataType.getStandardName() + 
				", categoryName=" + dataType.getCategoryName() + 
				", units=" + dataType.getUnits().toString() + 
				"]";
	}

}
