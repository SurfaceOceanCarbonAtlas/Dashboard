/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * Well-ordered set of known data types that can be extended as needed.
 * The well-known data types are
 * 
 * (metadata):
 * 		EXPOCODE, DATASET_NAME, VESSEL_NAME, ORGANIZATION_NAME,
 * 		INVESTIGATOR_NAMES, WESTMOST_LONGITUDE, EASTMOST_LONGITUDE,
 * 		SOUTHMOST_LATITUDE, NORTHMOST_LATITUDE, TIME_COVERAGE_START,
 * 		TIME_COVERAGE_END, QC_FLAG
 * 
 * (data):
 * 		SAMPLE_NUMBER, TIMESTAMP, DATE, YEAR, MONTH_OF_YEAR, DAY_OF_MONTH, 
 * 		TIME_OF_DAY, HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE, 
 * 		DAY_OF_YEAR, SECOND_OF_DAY, LONGITUDE, LATITUDE, SAMPLE_DEPTH, TIME
 * 
 * as well as UNKNOWN and OTHER defined in DataColumnType.
 *  
 * @author Karl Smith
 */
public class KnownDataTypes {

	/** mapping from old unit names to new unit names */
	public static final HashMap<String,String> RENAMED_UNITS;
	static {
		RENAMED_UNITS = new HashMap<String,String>();
		RENAMED_UNITS.put("deg.E", "degrees_east");
		RENAMED_UNITS.put("deg.W", "degrees_west");
		RENAMED_UNITS.put("deg.N", "degrees_north");
		RENAMED_UNITS.put("deg.S", "degrees_south");
		RENAMED_UNITS.put("deg.C", "degrees C");
		RENAMED_UNITS.put("deg.clk.N", "degrees");
	}

	/** Marker data type used to indicate an severe error in the combination of lon/lat/time */
	public static final DashDataType GEOPOSITION = new DashDataType(DashboardUtils.GEOPOSITION);

	/**
	 * UNKNOWN needs to be respecified as one of the (other) data column types.
	 */
	public static final DashDataType UNKNOWN = new DashDataType(DashboardUtils.UNKNOWN);

	/**
	 * OTHER is for supplementary data in the user's original data file but 
	 * otherwise not used.  A description of each column with this type must 
	 * be part of the metadata, but the values are not validated or used. 
	 * Multiple columns may have this type.
	 */
	public static final DashDataType OTHER = new DashDataType(DashboardUtils.OTHER);

	/**
	 * Unique identifier for the dataset.
	 * For SOCAT, the expocode is NODCYYYYMMDD where NODC is the ship code 
	 * and YYYY-MM-DD is the start date for the cruise; and possibly followed
	 * by -1 or -2 for non-ship vessels - where NODC is does not distinguish
	 * different vessels.  (metadata)
	 */
	public static final DashDataType EXPOCODE = new DashDataType(DashboardUtils.EXPOCODE);
	
	/**
	 * User-provided name for the dataset (metadata)
	 */
	public static final DashDataType DATASET_NAME = new DashDataType(DashboardUtils.DATASET_NAME);

	public static final DashDataType VESSEL_NAME = new DashDataType(DashboardUtils.VESSEL_NAME);
	public static final DashDataType ORGANIZATION_NAME = new DashDataType(DashboardUtils.ORGANIZATION_NAME);
	public static final DashDataType INVESTIGATOR_NAMES = new DashDataType(DashboardUtils.INVESTIGATOR_NAMES);
	public static final DashDataType WESTERNMOST_LONGITUDE = new DashDataType(DashboardUtils.WESTERNMOST_LONGITUDE);
	public static final DashDataType EASTERNMOST_LONGITUDE = new DashDataType(DashboardUtils.EASTERNMOST_LONGITUDE);
	public static final DashDataType SOUTHERNMOST_LATITUDE = new DashDataType(DashboardUtils.SOUTHERNMOST_LATITUDE);
	public static final DashDataType NORTHERNMOST_LATITUDE = new DashDataType(DashboardUtils.NORTHERNMOST_LATITUDE);
	public static final DashDataType TIME_COVERAGE_START = new DashDataType(DashboardUtils.TIME_COVERAGE_START);
	public static final DashDataType TIME_COVERAGE_END = new DashDataType(DashboardUtils.TIME_COVERAGE_END);
	public static final DashDataType QC_FLAG = new DashDataType(DashboardUtils.QC_FLAG);

	public static final DashDataType SAMPLE_NUMBER = new DashDataType(DashboardUtils.SAMPLE_NUMBER);

	/**
	 * Date and time or the measurement
	 */
	public static final DashDataType TIMESTAMP = new DashDataType(DashboardUtils.TIMESTAMP);

	/**
	 * Date of the measurement - no time.
	 */
	public static final DashDataType DATE = new DashDataType(DashboardUtils.DATE);

	public static final DashDataType YEAR = new DashDataType(DashboardUtils.YEAR);
	public static final DashDataType MONTH_OF_YEAR = new DashDataType(DashboardUtils.MONTH_OF_YEAR);
	public static final DashDataType DAY_OF_MONTH = new DashDataType(DashboardUtils.DAY_OF_MONTH);
	public static final DashDataType TIME_OF_DAY = new DashDataType(DashboardUtils.TIME_OF_DAY);
	public static final DashDataType HOUR_OF_DAY = new DashDataType(DashboardUtils.HOUR_OF_DAY);
	public static final DashDataType MINUTE_OF_HOUR = new DashDataType(DashboardUtils.MINUTE_OF_HOUR);
	public static final DashDataType SECOND_OF_MINUTE = new DashDataType(DashboardUtils.SECOND_OF_MINUTE);

	/**
	 * DAY_OF_YEAR, along with YEAR, and possibly SECOND_OF_DAY,
	 * may be used to specify the date and time of the measurement.
	 */
	public static final DashDataType DAY_OF_YEAR = new DashDataType(DashboardUtils.DAY_OF_YEAR);

	/**
	 * SECOND_OF_DAY, along with YEAR and DAY_OF_YEAR may
	 * be used to specify date and time of the measurement
	 */
	public static final DashDataType SECOND_OF_DAY = new DashDataType(DashboardUtils.SECOND_OF_DAY);

	public static final DashDataType LONGITUDE = new DashDataType(DashboardUtils.LONGITUDE);
	public static final DashDataType LATITUDE = new DashDataType(DashboardUtils.LATITUDE);
	public static final DashDataType SAMPLE_DEPTH = new DashDataType(DashboardUtils.SAMPLE_DEPTH);
	public static final DashDataType TIME = new DashDataType(DashboardUtils.TIME);

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
	 * types.  Only the upper-cased varName is used to differentiate 
	 * known data types.  The given instance of the DashDataType is 
	 * added to the internal collection of known data types.
	 * 
	 * @param dtype
	 * 		new data type to add to the known list
	 * @return
	 * 		existing known data type that was replaced;
	 * 		null if there was no existing known data type with matching name 
	 */
	private void addDataType(DashDataType dtype) {
		knownTypes.put(dtype.getVarName().toUpperCase(), dtype);
	}

	/**
	 * Adds the default well-known data column types for the users
	 * to select from.
	 * 		UNKNOWN, OTHER,
	 * 		EXPOCODE, DATASET_NAME, VESSEL_NAME, ORGANIZATION_NAME, 
	 * 		INVESTIGATOR_NAMES, QC_FLAG,
	 * 		TIMESTAMP, DATE, YEAR, MONTH_OF_YEAR, DAY_OF_MONTH,
	 * 		TIME_OF_DAY, HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE,
	 * 		DAY_OF_YEAR, SECOND_OF_DAY, LONGITUDE, LATITUDE, SAMPLE_DEPTH
	 * This should be called before adding any custom types.
	 * 
	 * @return
	 * 		this instance (as a convenience for chaining)
	 */
	public KnownDataTypes addStandardTypesForUsers() {
		addDataType(UNKNOWN);
		addDataType(OTHER);
		addDataType(EXPOCODE);
		addDataType(DATASET_NAME);
		addDataType(VESSEL_NAME);
		addDataType(ORGANIZATION_NAME);
		addDataType(INVESTIGATOR_NAMES);
		addDataType(QC_FLAG);
		addDataType(TIMESTAMP);
		addDataType(DATE);
		addDataType(YEAR);
		addDataType(MONTH_OF_YEAR);
		addDataType(DAY_OF_MONTH);
		addDataType(TIME_OF_DAY);
		addDataType(HOUR_OF_DAY);
		addDataType(MINUTE_OF_HOUR);
		addDataType(SECOND_OF_MINUTE);
		addDataType(DAY_OF_YEAR);
		addDataType(SECOND_OF_DAY);
		addDataType(LONGITUDE);
		addDataType(LATITUDE);
		addDataType(SAMPLE_DEPTH);
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
		addDataType(EXPOCODE);
		addDataType(DATASET_NAME);
		addDataType(VESSEL_NAME);
		addDataType(ORGANIZATION_NAME);
		addDataType(INVESTIGATOR_NAMES);
		addDataType(WESTERNMOST_LONGITUDE);
		addDataType(EASTERNMOST_LONGITUDE);
		addDataType(SOUTHERNMOST_LATITUDE);
		addDataType(NORTHERNMOST_LATITUDE);
		addDataType(TIME_COVERAGE_START);
		addDataType(TIME_COVERAGE_END);
		addDataType(QC_FLAG);
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
		addDataType(SAMPLE_NUMBER);
		addDataType(YEAR);
		addDataType(MONTH_OF_YEAR);
		addDataType(DAY_OF_MONTH);
		addDataType(HOUR_OF_DAY);
		addDataType(MINUTE_OF_HOUR);
		addDataType(SECOND_OF_MINUTE);
		addDataType(TIME);
		addDataType(LONGITUDE);
		addDataType(LATITUDE);
		addDataType(SAMPLE_DEPTH);
		return this;
	}

	/**
	 * Create additional known data types from values in a Properties object.
	 * 
	 * @param knownTypesFile
	 * 		properties file of data types to add to the known list; 
	 * 		uses the simple line format:
	 * 			varName={JSON description}
	 * 		where {JSON description} is a JSON string giving:
	 * 			the data class name (tag: "dataClassName"),
	 * 			the description (tag: "description"),
	 * 			the standard name (tag: "standardName"),
	 * 			the category name (tag: "categoryName"), and
	 * 			the units array (tag: "units").
	 * 		Tags may be omitted in which case the DashDataType default value is assigned.
	 * @throws IllegalArgumentException
	 * 		if the variable name of a data type to add as already known
	 * 			(using {@link #containsTypeName(String)},
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
	 * Determines is a given data type name exists in the list
	 * of known data types.  This only compares the upper-cased 
	 * varName values in each data type.
	 * 
	 * @param typeName
	 * 		search for a data column type with this name 
	 * @return
	 * 		if the known data column types contains the given data column type name
	 */
	public boolean containsTypeName(String typeName) {
		return knownTypes.containsKey(typeName.toUpperCase());
	}

	/**
	 * Returns a new data column type based on the data type with a matching 
	 * type (comparing only the upper-cased varName values.  The selected 
	 * unit will be zero and the select missing value will be 
	 * {@link DashboardUtils#STRING_MISSING_VALUE} (default missing values).
	 * 
	 * @param varName
	 * 		data column type variable name to find
	 * @return
	 * 		copy of the known data column type that matches, or
	 * 		null if the name does not match that of a known type
	 */
	public DataColumnType getDataColumnType(String varName) {
		DashDataType dtype = knownTypes.get(varName.toUpperCase());
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
	 * 		the number of known data types in this instance
	 */
	public int size() {
		return knownTypes.size();
	}

	@Override
	public String toString() {
		String strval = "KnownDataTypes[\n";
		for ( DashDataType dtype : knownTypes.values() )
			strval += "    " + dtype.toString() + "\n";
		strval += "]";
		return strval;
	}

}
