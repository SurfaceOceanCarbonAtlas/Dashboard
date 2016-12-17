/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import java.util.HashMap;
import java.util.Properties;
import java.util.TreeSet;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * Known data types that can be extended as needed.
 * Provides an ordered set of known types.
 *  
 * @author Karl Smith
 */
public class KnownDataTypes {

	private HashMap<String,DashDataType> knownTypes;

	/**
	 * Creates with no well-know data types.
	 */
	public KnownDataTypes() {
		// Give extra capacity for added types
		knownTypes = new HashMap<String,DashDataType>(96);
	}

	/**
	 * Adds the given data type to this collection of known data 
	 * types.  The given instance of the DashDataType is added to 
	 * the internal collection of known data types.
	 * 
	 * @param dtype
	 * 		new data type to add to the known list
	 * @throws IllegalArgumentException
	 * 		if the data type already is a known type
	 */
	private void addDataType(DashDataType dtype) throws IllegalArgumentException {
		String varKey = DashboardServerUtils.getKeyForName(dtype.getVarName());
		DashDataType oldVal = knownTypes.put(varKey, dtype);
		if ( oldVal != null )
			throw new IllegalArgumentException(oldVal.toString() + " matches " + dtype.toString());
		String displayKey = DashboardServerUtils.getKeyForName(dtype.getDisplayName());
		if ( ! displayKey.equals(varKey) ) {
			oldVal = knownTypes.put(displayKey, dtype);
			if ( oldVal != null )
				throw new IllegalArgumentException(oldVal.toString() + " matches " + dtype.toString());
		}
	}

	/**
	 * Adds the default well-known data column types for users
	 * to select from.
	 * 		UNKNOWN, OTHER, DATASET_NAME, PLATFORM_NAME, 
	 * 		PLATFORM_TYPE, ORGANIZATION_NAME, INVESTIGATOR_NAMES, 
	 * 		SAMPLE_ID, LONGITUDE, LATITUDE, SAMPLE_DEPTH, TIMESTAMP, 
	 * 		DATE, YEAR, MONTH_OF_YEAR, DAY_OF_MONTH, TIME_OF_DAY, 
	 * 		HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE, 
	 * 		DAY_OF_YEAR, SECOND_OF_DAY 
	 * This should be called before adding any custom types.
	 * 
	 * @return
	 * 		this instance (as a convenience for chaining)
	 */
	public KnownDataTypes addStandardTypesForUsers() {
		addDataType(DashboardServerUtils.UNKNOWN);
		addDataType(DashboardServerUtils.OTHER);
		addDataType(DashboardServerUtils.DATASET_NAME);
		addDataType(DashboardServerUtils.PLATFORM_NAME);
		addDataType(DashboardServerUtils.PLATFORM_TYPE);
		addDataType(DashboardServerUtils.ORGANIZATION_NAME);
		addDataType(DashboardServerUtils.INVESTIGATOR_NAMES);
		addDataType(DashboardServerUtils.SAMPLE_ID);
		addDataType(DashboardServerUtils.LONGITUDE);
		addDataType(DashboardServerUtils.LATITUDE);
		addDataType(DashboardServerUtils.SAMPLE_DEPTH);
		addDataType(DashboardServerUtils.TIMESTAMP);
		addDataType(DashboardServerUtils.DATE);
		addDataType(DashboardServerUtils.YEAR);
		addDataType(DashboardServerUtils.MONTH_OF_YEAR);
		addDataType(DashboardServerUtils.DAY_OF_MONTH);
		addDataType(DashboardServerUtils.TIME_OF_DAY);
		addDataType(DashboardServerUtils.HOUR_OF_DAY);
		addDataType(DashboardServerUtils.MINUTE_OF_HOUR);
		addDataType(DashboardServerUtils.SECOND_OF_MINUTE);
		addDataType(DashboardServerUtils.DAY_OF_YEAR);
		addDataType(DashboardServerUtils.SECOND_OF_DAY);
		return this;
	}

	/**
	 * Adds the default well-known metadata column types for 
	 * generating the NetCDF DSG files.
	 * 		DATASET_ID, DATASET_NAME, PLATFORM_NAME, 
	 * 		PLATFORM_TYPE, ORGANIZATION_NAME, INVESTIGATOR_NAMES, 
	 * 		WESTERNMOST_LONGITUDE, EASTERNMOST_LONGITUDE, 
	 * 		SOUTHERNMOST_LATITUDE, NORTHERNMOST_LATITUDE, 
	 * 		TIME_COVERAGE_START, TIME_COVERAGE_END, 
	 * 		STATUS, VERSION 
	 * This should be called before adding any custom types.
	 * 
	 * @return
	 * 		this instance (as a convenience for chaining)
	 */
	public KnownDataTypes addStandardTypesForMetadataFiles() {
		addDataType(DashboardServerUtils.DATASET_ID);
		addDataType(DashboardServerUtils.DATASET_NAME);
		addDataType(DashboardServerUtils.PLATFORM_NAME);
		addDataType(DashboardServerUtils.PLATFORM_TYPE);
		addDataType(DashboardServerUtils.ORGANIZATION_NAME);
		addDataType(DashboardServerUtils.INVESTIGATOR_NAMES);
		addDataType(DashboardServerUtils.WESTERNMOST_LONGITUDE);
		addDataType(DashboardServerUtils.EASTERNMOST_LONGITUDE);
		addDataType(DashboardServerUtils.SOUTHERNMOST_LATITUDE);
		addDataType(DashboardServerUtils.NORTHERNMOST_LATITUDE);
		addDataType(DashboardServerUtils.TIME_COVERAGE_START);
		addDataType(DashboardServerUtils.TIME_COVERAGE_END);
		addDataType(DashboardServerUtils.STATUS);
		addDataType(DashboardServerUtils.VERSION);
		return this;
	}

	/**
	 * Adds the default well-known data column types for 
	 * generating the NetCDF DSG files.
	 * 		SAMPLE_NUMBER, TIME, LONGITUDE, LATITUDE, SAMPLE_DEPTH, 
	 * 		YEAR, MONTH_OF_YEAR, DAY_OF_MONTH, HOUR_OF_DAY, 
	 * 		MINUTE_OF_HOUR, SECOND_OF_MINUTE, WOCE_AUTOCHECK 
	 * This should be called before adding any custom types.
	 * 
	 * @return
	 * 		this instance (as a convenience for chaining)
	 */
	public KnownDataTypes addStandardTypesForDataFiles() {
		addDataType(DashboardServerUtils.SAMPLE_NUMBER);
		addDataType(DashboardServerUtils.TIME);
		addDataType(DashboardServerUtils.LONGITUDE);
		addDataType(DashboardServerUtils.LATITUDE);
		addDataType(DashboardServerUtils.SAMPLE_DEPTH);
		addDataType(DashboardServerUtils.YEAR);
		addDataType(DashboardServerUtils.MONTH_OF_YEAR);
		addDataType(DashboardServerUtils.DAY_OF_MONTH);
		addDataType(DashboardServerUtils.HOUR_OF_DAY);
		addDataType(DashboardServerUtils.MINUTE_OF_HOUR);
		addDataType(DashboardServerUtils.SECOND_OF_MINUTE);
		addDataType(DashboardServerUtils.WOCE_AUTOCHECK);
		return this;
	}

	/**
	 * Create additional known data types from values in a Properties object.
	 * 
	 * @param knownTypesFile
	 * 		properties file of data types to add to the known list; 
	 * 		uses the simple line format:
	 * 			varName={JSON description}
	 * 		where varName is the variable name of the type and 
	 * 		{JSON description} is a JSON string describing the type 
	 * 		as documented by {@link DashDataType#fromPropertyValue(String, String)}
	 * @throws IllegalArgumentException
	 * 		if the data type to add is already known,
	 * 		if the JSON description cannot be parsed.
	 * 
	 * @return
	 * 		this instance (as a convenience for chaining)
	 */
	public KnownDataTypes addTypesFromProperties(Properties typeProps) throws IllegalArgumentException {
		for ( String name : typeProps.stringPropertyNames() ) {
			if ( containsTypeName(name) )
				throw new IllegalArgumentException("Duplicate user-known data type \"" + name + "\"");
			String value = typeProps.getProperty(name);
			addDataType( DashDataType.fromPropertyValue(name, value) );
		}
		return this;
	}

	/**
	 * Determines if a given data type name exists in the list
	 * of known data types.
	 * 
	 * @param typeName
	 * 		search for a data type with this name 
	 * @return
	 * 		if the given data type name is known
	 */
	public boolean containsTypeName(String typeName) {
		return knownTypes.containsKey(DashboardServerUtils.getKeyForName(typeName));
	}

	/**
	 * Returns a new data column type based on the data type with a type
	 * matching the given type name. 
	 * The selected unit will be zero and the select missing value will be 
	 * {@link DashboardUtils#STRING_MISSING_VALUE} (default missing values).
	 * 
	 * @param varName
	 * 		type name to find
	 * @return
	 * 		data column type matching the given type name, or
	 * 		null if the name does not match that of a known type
	 */
	public DataColumnType getDataColumnType(String typeName) {
		DashDataType dtype = knownTypes.get(DashboardServerUtils.getKeyForName(typeName));
		if ( dtype == null )
			return null;
		return dtype.duplicate();
	}

	/**
	 * @return
	 * 		the sorted current set of known data types.
	 */
	public TreeSet<DashDataType> getKnownTypesSet() {
		return new TreeSet<DashDataType>(knownTypes.values());
	}

	/**
	 * @return
	 * 		if there are no known data types
	 */
	public boolean isEmpty() {
		return knownTypes.isEmpty();
	}

	@Override
	public String toString() {
		String strval = "KnownDataTypes[\n";
		// Do not show the keys, only the unique data types
		for ( DashDataType dtype : getKnownTypesSet() )
			strval += "    " + dtype.toString() + "\n";
		strval += "]";
		return strval;
	}

}
