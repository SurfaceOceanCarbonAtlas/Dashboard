/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * Well-ordered set of known data types that can be extended as needed.
 *  
 * @author Karl Smith
 */
public class KnownDataTypes {

	private LinkedHashMap<String,DashDataType> knownTypes;

	/**
	 * Creates with no well-know data types.
	 */
	public KnownDataTypes() {
		// Give plenty of capacity;
		// since this is a LinkedHashMap, extra capacity not really a problem
		knownTypes = new LinkedHashMap<String,DashDataType>(96);
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
	 * Adds the default well-known data column types for the users
	 * to select from.
	 * 		UNKNOWN, OTHER,
	 * 		EXPOCODE, DATASET_NAME, VESSEL_NAME, ORGANIZATION_NAME, 
	 * 		INVESTIGATOR_NAMES,
	 * 		TIMESTAMP, DATE, YEAR, MONTH_OF_YEAR, DAY_OF_MONTH,
	 * 		TIME_OF_DAY, HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE,
	 * 		DAY_OF_YEAR, SECOND_OF_DAY, LONGITUDE, LATITUDE, SAMPLE_DEPTH
	 * This should be called before adding any custom types.
	 * 
	 * @return
	 * 		this instance (as a convenience for chaining)
	 */
	public KnownDataTypes addStandardTypesForUsers() {
		addDataType(DashboardServerUtils.UNKNOWN);
		addDataType(DashboardServerUtils.OTHER);
		addDataType(DashboardServerUtils.EXPOCODE);
		addDataType(DashboardServerUtils.DATASET_NAME);
		addDataType(DashboardServerUtils.VESSEL_NAME);
		addDataType(DashboardServerUtils.ORGANIZATION_NAME);
		addDataType(DashboardServerUtils.INVESTIGATOR_NAMES);
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
		addDataType(DashboardServerUtils.LONGITUDE);
		addDataType(DashboardServerUtils.LATITUDE);
		addDataType(DashboardServerUtils.SAMPLE_DEPTH);
		return this;
	}

	/**
	 * Adds the default well-known metadata column types for the generating 
	 * the NetCDF DSG files.
	 * 		EXPOCODE, DATASET_NAME, VESSEL_NAME, ORGANIZATION_NAME, 
	 * 		INVESTIGATOR_NAMES, WESTERNMOST_LONGITUDE, EASTERNMOST_LONGITUDE, 
	 * 		SOUTHERNMOST_LATITUDE, NORTHERNMOST_LATITUDE, TIME_COVERAGE_START, 
	 * 		TIME_COVERAGE_END, QC_FLAG
	 * This should be called before adding any custom types.
	 * 
	 * @return
	 * 		this instance (as a convenience for chaining)
	 */
	public KnownDataTypes addStandardTypesForMetadataFiles() {
		addDataType(DashboardServerUtils.EXPOCODE);
		addDataType(DashboardServerUtils.DATASET_NAME);
		addDataType(DashboardServerUtils.VESSEL_NAME);
		addDataType(DashboardServerUtils.ORGANIZATION_NAME);
		addDataType(DashboardServerUtils.INVESTIGATOR_NAMES);
		addDataType(DashboardServerUtils.WESTERNMOST_LONGITUDE);
		addDataType(DashboardServerUtils.EASTERNMOST_LONGITUDE);
		addDataType(DashboardServerUtils.SOUTHERNMOST_LATITUDE);
		addDataType(DashboardServerUtils.NORTHERNMOST_LATITUDE);
		addDataType(DashboardServerUtils.TIME_COVERAGE_START);
		addDataType(DashboardServerUtils.TIME_COVERAGE_END);
		addDataType(DashboardServerUtils.QC_FLAG);
		return this;
	}

	/**
	 * Adds the default well-known data column types for the generating 
	 * the NetCDF DSG files.
	 * 		SAMPLE_NUMBER, YEAR, MONTH_OF_YEAR, DAY_OF_MONTH, HOUR_OF_DAY, 
	 * 		MINUTE_OF_HOUR, SECOND_OF_MINUTE, TIME, LONGITUDE, LATITUDE, 
	 * 		SAMPLE_DEPTH
	 * This should be called before adding any custom types.
	 * 
	 * @return
	 * 		this instance (as a convenience for chaining)
	 */
	public KnownDataTypes addStandardTypesForDataFiles() {
		addDataType(DashboardServerUtils.SAMPLE_NUMBER);
		addDataType(DashboardServerUtils.YEAR);
		addDataType(DashboardServerUtils.MONTH_OF_YEAR);
		addDataType(DashboardServerUtils.DAY_OF_MONTH);
		addDataType(DashboardServerUtils.HOUR_OF_DAY);
		addDataType(DashboardServerUtils.MINUTE_OF_HOUR);
		addDataType(DashboardServerUtils.SECOND_OF_MINUTE);
		addDataType(DashboardServerUtils.TIME);
		addDataType(DashboardServerUtils.LONGITUDE);
		addDataType(DashboardServerUtils.LATITUDE);
		addDataType(DashboardServerUtils.SAMPLE_DEPTH);
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
	 * Determines if the type of a given data column type 
	 * exists in the list of known data types.
	 * 
	 * @param dataColType
	 * 		search for a type like this data column type 
	 * @return
	 * 		if the given type is known
	 */
	public boolean containsType(DataColumnType dctype) {
		if ( containsTypeName(dctype.getVarName()) )
			return true;
		if ( containsTypeName(dctype.getDisplayName()) )
			return true;
		return false;
	}

	/**
	 * Determines is a given data type exists 
	 * in the list of known data types.
	 * 
	 * @param dtype
	 * 		data type to search for 
	 * @return
	 * 		if the given data type is known
	 */
	public boolean containsType(DashDataType dtype) {
		if ( containsTypeName(dtype.getVarName()) )
			return true;
		if ( containsTypeName(dtype.getDisplayName()) )
			return true;
		return false;
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
	 * 		the current set of known data types.
	 */
	public LinkedHashSet<DashDataType> getKnownTypesSet() {
		return new LinkedHashSet<DashDataType>(knownTypes.values());
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
		// Remove duplicated data types (with different keys) by first forming a set
		for ( DashDataType dtype : new LinkedHashSet<DashDataType>(knownTypes.values()) )
			strval += "    " + dtype.toString() + "\n";
		strval += "]";
		return strval;
	}

}
