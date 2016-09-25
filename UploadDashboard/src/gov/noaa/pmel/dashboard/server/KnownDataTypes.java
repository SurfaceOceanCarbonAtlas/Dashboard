/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.util.ArrayList;
import java.util.Arrays;
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

	public static final String CHAR_DATA_CLASS_NAME = "Character";
	public static final String DATE_DATA_CLASS_NAME = "Date";
	public static final String DOUBLE_DATA_CLASS_NAME = "Double";
	public static final String INT_DATA_CLASS_NAME = "Integer";
	public static final String STRING_DATA_CLASS_NAME = "String";

	// Some suggested categories
	public static final String BATHYMETRY_CATEGORY = "Bathymetry";
	public static final String CO2_CATEGORY = "CO2";
	public static final String IDENTIFIER_CATEGORY = "Identifier";
	public static final String LOCATION_CATEGORY = "Location";
	public static final String PLATFORM_CATEGORY = "Platform";
	public static final String PRESSURE_CATEGORY = "Pressure";
	public static final String QUALITY_CATEGORY = "Quality";
	public static final String SALINITY_CATEGORY = "Salinity";
	public static final String TEMPERATURE_CATEGORY = "Temperature";
	public static final String TIME_CATEGORY = "Time";
	public static final String WATER_VAPOR_CATEGORY = "Water Vapor";
	public static final String WIND_CATEGORY = "Wind";

	/** Formats for date-time stamps */
	public static final ArrayList<String> TIMESTAMP_UNITS = 
			new ArrayList<String>(Arrays.asList(
					"yyyy-mm-dd hh:mm:ss", 
					"mm-dd-yyyy hh:mm:ss", 
					"dd-mm-yyyy hh:mm:ss", 
					"mm-dd-yy hh:mm:ss", 
					"dd-mm-yy hh:mm:ss"));

	/** Formats for dates */
	public static final ArrayList<String> DATE_UNITS = 
			new ArrayList<String>(Arrays.asList(
					"yyyy-mm-dd", 
					"mm-dd-yyyy", 
					"dd-mm-yyyy", 
					"mm-dd-yy", 
					"dd-mm-yy"));

	/** Formats for time-of-day */
	public static final ArrayList<String> TIME_OF_DAY_UNITS = 
			new ArrayList<String>(Arrays.asList("hh:mm:ss"));

	/** Units for day-of-year (value of the first day of the year) */
	public static final ArrayList<String> DAY_OF_YEAR_UNITS = 
			new ArrayList<String>(Arrays.asList("Jan1=1.0", "Jan1=0.0"));

	/** Units for longitude */
	public static final ArrayList<String> LONGITUDE_UNITS = 
			new ArrayList<String>(Arrays.asList("degrees_east", "degrees_west"));

	/** Units of latitude */
	public static final ArrayList<String> LATITUDE_UNITS = 
			new ArrayList<String>(Arrays.asList("degrees_north", "degrees_south"));

	/** Unit of depth */
	public static final ArrayList<String> DEPTH_UNITS = 
			new ArrayList<String>(Arrays.asList("meters"));

	/** Unit of completely specified time ("seconds since 1970-01-01T00:00:00Z") */
	public static final ArrayList<String> TIME_UNITS = 
			new ArrayList<String>(Arrays.asList("seconds since 1970-01-01T00:00:00Z"));

	/** Marker data type used to indicate an severe error in a time or position */
	public static final DataColumnType TIME_LOCATION = new DataColumnType("time_location", 
			null, null, null, null, DataColumnType.NO_UNITS);

	/**
	 * UNKNOWN needs to be respecified as one of the (other) data column types.
	 */
	public static final DashDataType UNKNOWN = new DashDataType(DataColumnType.UNKNOWN);

	/**
	 * OTHER is for supplementary data in the user's original data file but 
	 * otherwise not used.  A description of each column with this type must 
	 * be part of the metadata, but the values are not validated or used. 
	 * Multiple columns may have this type.
	 */
	public static final DashDataType OTHER = new DashDataType(DataColumnType.OTHER);

	/**
	 * Unique identifier for the dataset.
	 * For SOCAT, the expocode is NODCYYYYMMDD where NODC is the ship code 
	 * and YYYY-MM-DD is the start date for the cruise; and possibly followed
	 * by -1 or -2 for non-ship vessels - where NODC is does not distinguish
	 * different vessels.  (metadata)
	 */
	public static final DashDataType EXPOCODE = new DashDataType("expocode", 
			STRING_DATA_CLASS_NAME, "expocode", null, IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);
	
	/**
	 * User-provided name for the dataset (metadata)
	 */
	public static final DashDataType DATASET_NAME = new DashDataType("dataset_name", 
			STRING_DATA_CLASS_NAME, "dataset name", null, IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType VESSEL_NAME = new DashDataType("vessel_name", 
			STRING_DATA_CLASS_NAME, "vessel name", "platform_name", PLATFORM_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType ORGANIZATION_NAME = new DashDataType("organization", 
			STRING_DATA_CLASS_NAME, "organization", null, IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);
	
	public static final DashDataType INVESTIGATOR_NAMES = new DashDataType("investigators", 
			STRING_DATA_CLASS_NAME, "investigators", null, IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType WESTERNMOST_LONGITUDE = new DashDataType("geospatial_lon_min",
			DOUBLE_DATA_CLASS_NAME, "westernmost longitude", "geospatial_lon_min", LOCATION_CATEGORY, LONGITUDE_UNITS);

	public static final DashDataType EASTERNMOST_LONGITUDE = new DashDataType("geospatial_lon_max",
			DOUBLE_DATA_CLASS_NAME, "easternmost longitude", "geospatial_lon_max", LOCATION_CATEGORY, LONGITUDE_UNITS);

	public static final DashDataType SOUTHERNMOST_LATITUDE = new DashDataType("geospatial_lat_min",
			DOUBLE_DATA_CLASS_NAME, "southernmost latitude", "geospatial_lat_min", LOCATION_CATEGORY, LATITUDE_UNITS);

	public static final DashDataType NORTHERNMOST_LATITUDE = new DashDataType("geospatial_lat_max",
			DOUBLE_DATA_CLASS_NAME, "northernmost latitude", "geospatial_lat_max", LOCATION_CATEGORY, LATITUDE_UNITS);

	public static final DashDataType TIME_COVERAGE_START = new DashDataType("time_coverage_start",
			DATE_DATA_CLASS_NAME, "beginning time", "time_coverage_start", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType TIME_COVERAGE_END = new DashDataType("time_converage_end",
			DATE_DATA_CLASS_NAME, "ending time", "time_converage_end", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType QC_FLAG = new DashDataType("qc_flag", 
			STRING_DATA_CLASS_NAME, "QC flag", null, QUALITY_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType SAMPLE_NUMBER = new DashDataType("sample_number",
			INT_DATA_CLASS_NAME, "sample number", null, IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);

	/**
	 * Date and time or the measurement
	 */
	public static final DashDataType TIMESTAMP = new DashDataType("date_time", 
			STRING_DATA_CLASS_NAME, "date and time", null, null, TIMESTAMP_UNITS);

	/**
	 * Date of the measurement - no time.
	 */
	public static final DashDataType DATE = new DashDataType("date", 
			STRING_DATA_CLASS_NAME, "date", null, null, DATE_UNITS);

	public static final DashDataType YEAR = new DashDataType("year", 
			INT_DATA_CLASS_NAME, "year", "year", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType MONTH_OF_YEAR = new DashDataType("month", 
			INT_DATA_CLASS_NAME, "month of year", "month_of_year", TIME_CATEGORY, DataColumnType.NO_UNITS);
	
	public static final DashDataType DAY_OF_MONTH = new DashDataType("day", 
			INT_DATA_CLASS_NAME, "day of month", "day_of_month", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType TIME_OF_DAY = new DashDataType("time_of_day", 
			STRING_DATA_CLASS_NAME, "time of day", null, null, TIME_OF_DAY_UNITS);

	public static final DashDataType HOUR_OF_DAY = new DashDataType("hour", 
			INT_DATA_CLASS_NAME, "hour of day", "hour_of_day", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType MINUTE_OF_HOUR = new DashDataType("minute", 
			INT_DATA_CLASS_NAME, "minute of hour", "minute_if_hour", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType SECOND_OF_MINUTE = new DashDataType("second", 
			DOUBLE_DATA_CLASS_NAME, "second of minute", "second_of_minute", TIME_CATEGORY, DataColumnType.NO_UNITS);

	/**
	 * DAY_OF_YEAR, along with YEAR, and possibly SECOND_OF_DAY,
	 * may be used to specify the date and time of the measurement.
	 */
	public static final DashDataType DAY_OF_YEAR = new DashDataType("day_of_year", 
			DOUBLE_DATA_CLASS_NAME, "day of year", "day_of_year", TIME_CATEGORY, DAY_OF_YEAR_UNITS);

	/**
	 * SECOND_OF_DAY, along with YEAR and DAY_OF_YEAR may
	 * be used to specify date and time of the measurement
	 */
	public static final DashDataType SECOND_OF_DAY = new DashDataType("sec_of_day", 
			DOUBLE_DATA_CLASS_NAME, "second of day", "second_of_day", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType LONGITUDE = new DashDataType("longitude", 
			DOUBLE_DATA_CLASS_NAME, "longitude", "longitude", LOCATION_CATEGORY, LONGITUDE_UNITS);

	public static final DashDataType LATITUDE = new DashDataType("latitude", 
			DOUBLE_DATA_CLASS_NAME, "latitude", "latitude", LOCATION_CATEGORY, LATITUDE_UNITS);

	public static final DashDataType SAMPLE_DEPTH = new DashDataType("sample_depth", 
			DOUBLE_DATA_CLASS_NAME, "sample depth", "depth", BATHYMETRY_CATEGORY, DEPTH_UNITS);

	public static final DashDataType TIME = new DashDataType("time", 
			DOUBLE_DATA_CLASS_NAME, "time", "time", TIME_CATEGORY, TIME_UNITS);

/*
	// Map WOCE on all time-related types to "time"; other variables not visible
	typeToNameMap.put(DataColumnType.TIMESTAMP, "time");
	typeToNameMap.put(DataColumnType.DATE, "time");
	typeToNameMap.put(DataColumnType.TIME, "time");
	typeToNameMap.put(DataColumnType.YEAR, "time");
	typeToNameMap.put(DataColumnType.MONTH, "time");
	typeToNameMap.put(DataColumnType.DAY, "time");
	typeToNameMap.put(DataColumnType.HOUR, "time");
	typeToNameMap.put(DataColumnType.MINUTE, "time");
	typeToNameMap.put(DataColumnType.SECOND, "time");
	typeToNameMap.put(DataColumnType.DAY_OF_YEAR, "time");
	typeToNameMap.put(DataColumnType.SECOND_OF_DAY, "time");
*/

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
