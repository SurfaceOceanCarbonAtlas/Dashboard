/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

	// IOOS categories
	public static final String BATHYMETRY_CATEGORY = "Bathymetry";
	public static final String IDENTIFIER_CATEGORY = "Identifier";
	public static final String LOCATION_CATEGORY = "Location";
	public static final String TIME_CATEGORY = "Time";
	public static final String QUALITY_CATEGORY = "Quality";

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
			new ArrayList<String>(Arrays.asList("deg.E", "deg.W"));

	/** Units of latitude */
	public static final ArrayList<String> LATITUDE_UNITS = 
			new ArrayList<String>(Arrays.asList("deg.N", "deg.S"));

	/** Unit of depth */
	public static final ArrayList<String> DEPTH_UNITS = 
			new ArrayList<String>(Arrays.asList("meters"));

	/** Unit of completely specified time ("seconds since 1970-01-01T00:00:00Z") */
	public static final ArrayList<String> TIME_UNITS = 
			new ArrayList<String>(Arrays.asList("seconds since 1970-01-01T00:00:00Z"));

	/**
	 * Unique identifier for the dataset.
	 * For SOCAT, the expocode is NODCYYYYMMDD where NODC is the ship code 
	 * and YYYY-MM-DD is the start date for the cruise; and possibly followed
	 * by -1 or -2 for non-ship vessels - where NODC is does not distinguish
	 * different vessels.  (metadata)
	 */
	public static final DataColumnType EXPOCODE = new DataColumnType("expocode", 
			"String", "expocode", null, IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);
	
	/**
	 * User-provided name for the dataset (metadata)
	 */
	public static final DataColumnType DATASET_NAME = new DataColumnType("dataset_name", 
			"String", "dataset name", null, IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);

	public static final DataColumnType VESSEL_NAME = new DataColumnType("vessel_name", 
			"String", "vessel name", "platform_name", IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);

	public static final DataColumnType ORGANIZATION_NAME = new DataColumnType("organization", 
			"String", "organization", null, IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);
	
	public static final DataColumnType INVESTIGATOR_NAMES = new DataColumnType("investigators", 
			"String", "investigators", null, IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);

	public static final DataColumnType WESTERNMOST_LONGITUDE = new DataColumnType("geospatial_lon_min",
			"Double", "westernmost longitude", "geospatial_lon_min", LOCATION_CATEGORY, LONGITUDE_UNITS);

	public static final DataColumnType EASTERNMOST_LONGITUDE = new DataColumnType("geospatial_lon_max",
			"Double", "easternmost longitude", "geospatial_lon_max", LOCATION_CATEGORY, LONGITUDE_UNITS);

	public static final DataColumnType SOUTHERNMOST_LATITUDE = new DataColumnType("geospatial_lat_min",
			"Double", "southernmost latitude", "geospatial_lat_min", LOCATION_CATEGORY, LATITUDE_UNITS);

	public static final DataColumnType NORTHERNMOST_LATITUDE = new DataColumnType("geospatial_lat_max",
			"Double", "northernmost latitude", "geospatial_lat_max", LOCATION_CATEGORY, LATITUDE_UNITS);

	public static final DataColumnType TIME_COVERAGE_START = new DataColumnType("time_coverage_start",
			"Date", "beginning time", "time_coverage_start", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DataColumnType TIME_COVERAGE_END = new DataColumnType("time_converage_end",
			"Date", "ending time", "time_converage_end", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DataColumnType QC_FLAG = new DataColumnType("qc_flag", 
			"String", "QC flag", null, QUALITY_CATEGORY, DataColumnType.NO_UNITS);

	public static final DataColumnType SAMPLE_NUMBER = new DataColumnType("sample_number",
			"Integer", "sample number", null, IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);

	/**
	 * Date and time or the measurement
	 */
	public static final DataColumnType TIMESTAMP = new DataColumnType("date_time", 
			"String", "date and time", null, null, TIMESTAMP_UNITS);

	/**
	 * Date of the measurement - no time.
	 */
	public static final DataColumnType DATE = new DataColumnType("date", 
			"String", "date", null, null, DATE_UNITS);

	public static final DataColumnType YEAR = new DataColumnType("year", 
			"Integer", "year", "year", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DataColumnType MONTH_OF_YEAR = new DataColumnType("month", 
			"Integer", "month of year", "month_of_year", TIME_CATEGORY, DataColumnType.NO_UNITS);
	
	public static final DataColumnType DAY_OF_MONTH = new DataColumnType("day", 
			"Integer", "day of month", "day_of_month", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DataColumnType TIME_OF_DAY = new DataColumnType("time_of_day", 
			"String", "time of day", null, null, TIME_OF_DAY_UNITS);

	public static final DataColumnType HOUR_OF_DAY = new DataColumnType("hour", 
			"Integer", "hour of day", "hour_of_day", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DataColumnType MINUTE_OF_HOUR = new DataColumnType("minute", 
			"Integer", "minute of hour", "minute_if_hour", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DataColumnType SECOND_OF_MINUTE = new DataColumnType("second", 
			"Double", "second of minute", "second_of_minute", TIME_CATEGORY, DataColumnType.NO_UNITS);

	/**
	 * DAY_OF_YEAR, along with YEAR, and possibly SECOND_OF_DAY,
	 * may be used to specify the date and time of the measurement.
	 */
	public static final DataColumnType DAY_OF_YEAR = new DataColumnType("day_of_year", 
			"Double", "day of year", "day_of_year", TIME_CATEGORY, DAY_OF_YEAR_UNITS);

	/**
	 * SECOND_OF_DAY, along with YEAR and DAY_OF_YEAR may
	 * be used to specify date and time of the measurement
	 */
	public static final DataColumnType SECOND_OF_DAY = new DataColumnType("sec_of_day", 
			"Double", "second of day", "second_of_day", TIME_CATEGORY, DataColumnType.NO_UNITS);

	public static final DataColumnType LONGITUDE = new DataColumnType("longitude", 
			"Double", "longitude", "longitude", LOCATION_CATEGORY, LONGITUDE_UNITS);

	public static final DataColumnType LATITUDE = new DataColumnType("latitude", 
			"Double", "latitude", "latitude", LOCATION_CATEGORY, LATITUDE_UNITS);

	public static final DataColumnType SAMPLE_DEPTH = new DataColumnType("sample_depth", 
			"Double", "sample depth", "depth", BATHYMETRY_CATEGORY, DEPTH_UNITS);

	public static final DataColumnType TIME = new DataColumnType("time", 
			"Double", "time", "time", TIME_CATEGORY, TIME_UNITS);


	private LinkedHashMap<String,DataColumnType> knownTypes;

	/**
	 * Creates withonly the well-known data column types
	 * UNKNOWN and OTHER (defined in DataColumnType).
	 */
	public KnownDataTypes() {
		// Give plenty of capacity for expansion
		// since this is a LinkedHashMap, extra capacity not really a problem
		knownTypes = new LinkedHashMap<String,DataColumnType>(64);
		addDataType(DataColumnType.UNKNOWN);
		addDataType(DataColumnType.OTHER);
	}

	/**
	 * Adds the default well-known data column types for the client
	 * (metadata):
	 * 		EXPOCODE, DATASET_NAME, VESSEL_NAME, ORGANIZATION_NAME, 
	 * 		INVESTIGATOR_NAMES, QC_FLAG
	 * (data):
	 * 		TIMESTAMP, DATE, YEAR, MONTH_OF_YEAR, DAY_OF_MONTH,
	 * 		TIME_OF_DAY, HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE,
	 * 		DAY_OF_YEAR, SECOND_OF_DAY, LONGITUDE, LATITUDE, SAMPLE_DEPTH
	 * This should be called before adding any custom types.
	 */
	public void addStandardTypesForClient() {
		addDataType(DataColumnType.UNKNOWN);
		addDataType(DataColumnType.OTHER);
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
	}

	/**
	 * Adds the default well-known data column types for the server,
	 * for generating the NetCDF DSG files:
	 * (metadata):
	 * 		EXPOCODE, DATASET_NAME, VESSEL_NAME, ORGANIZATION_NAME, 
	 * 		INVESTIGATOR_NAMES, QC_FLAG
	 * (data):
	 * 		YEAR, MONTH_OF_YEAR, DAY_OF_MONTH, HOUR_OF_DAY, 
	 * 		MINUTE_OF_HOUR, SECOND_OF_MINUTE,
	 * 		LONGITUDE, LATITUDE, SAMPLE_DEPTH, TIME.
	 * This should be called before adding any custom types.
	 */
	public void addStandardTypesForServer() {
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
	}

	/**
	 * Adds the given data type to this collection of known data 
	 * types.  Only the upper-cased varName is used to differentiate 
	 * known data types.  The given instance of the DataColumnType is 
	 * added to the internal collection of known data types.
	 * 
	 * @param dtype
	 * 		new data type to add to the known list
	 * @return
	 * 		existing known data type that was replaced;
	 * 		null if there was no existing known data type with matching name 
	 */
	private DataColumnType addDataType(DataColumnType dtype) {
		return knownTypes.put(dtype.getVarName().toUpperCase(), dtype);
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
	 * @return
	 * 		the current list of known data types.  This is a shallow copy
	 * 		of the known data types; the DataColumnType objects returned
	 * 		in the list are those actually stored in this instance.
	 */
	public ArrayList<DataColumnType> getKnownTypesList() {
		return new ArrayList<DataColumnType>(knownTypes.values());
	}

	/**
	 * Returns a copy of the known data type whose variable 
	 * name matches (comparing upper-cased) the given variable name.  
	 * The selected unit will be zero and the select missing values 
	 * will be an empty string (default missing values).
	 * 
	 * @param varName
	 * 		data column type variable name to find
	 * @return
	 * 		copy of the known data column type that matches, or
	 * 		null if the name does not match that of a known type
	 */
	public DataColumnType getDataColumnType(String varName) {
		DataColumnType dtype = knownTypes.get(varName.toUpperCase());
		if ( dtype == null )
			return null;
		return dtype.duplicate();
	}

	/**
	 * Create additional known data types from values in a Properties file.
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
	 * 		The data class name must be given, but other tags may
	 * 		be omitted in which case the DataColumnType default value 
	 * 		is assigned.
	 * @throws IllegalArgumentException
	 * 		if the variable name of a data type to add to the known lists 
	 * 			matches the variable name of a data type in either of the 
	 * 			known lists (using {@link #containsTypeName(String)},
	 * 		if the JSON description cannot be parsed, or
	 * 		if the dataClassName tag is not given in the JSON description.
	 * @throws IOException
	 * 		if problems occur when reading the Properties file
	 */
	public static void addTypesFromProperties(File knownTypesFile, 
			KnownDataTypes clientTypes, KnownDataTypes serverTypes) 
					throws IllegalArgumentException, IOException {
		Properties typeProps = new Properties();
		try {
			FileReader reader = new FileReader(knownTypesFile);
			try {
				typeProps.load(reader);
			} finally {
				reader.close();
			}
		} catch ( Exception ex ) {
			throw new IOException("Problems reading " + knownTypesFile.getPath() + ": " + ex.getMessage());
		}
		JsonParser parser = new JsonParser();
		for ( String name : typeProps.stringPropertyNames() ) {
			if ( clientTypes.containsTypeName(name) || serverTypes.containsTypeName(name) )
				throw new IllegalArgumentException("Duplicate known data type name \"" + name + "\"");
			String dataClassName = null;
			String description = null;
			String standardName = null;
			String categoryName = null;
			LinkedHashSet<String> units = null;
			String jsonVal = typeProps.getProperty(name);
			try {
				JsonObject jsonObj = parser.parse(jsonVal).getAsJsonObject();
				for ( Entry<String, JsonElement> prop : jsonObj.entrySet() ) {
					String tag = prop.getKey();
					try {
						if ( "dataClassName".equals(tag) ) {
							dataClassName = prop.getValue().getAsString();
						}
						else if ( "description".equals(tag) ) {
							description = prop.getValue().getAsString();
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
						jsonVal + "' : " + ex.getMessage());
			}
			if ( dataClassName == null )
				throw new IllegalArgumentException("\"dataClassName\" tag is not given "
						+ "in the JSON description of \"" + name + "\"");
			clientTypes.addDataType(new DataColumnType(name, dataClassName, 
					description, standardName, categoryName, units));
			serverTypes.addDataType(new DataColumnType(name, dataClassName, 
					description, standardName, categoryName, units));
		}
	}

	public int size() {
		return knownTypes.size();
	}

	@Override
	public String toString() {
		String strval = "KnownDataTypes[\n";
		for ( DataColumnType dtype : knownTypes.values() )
			strval += "    " + dtype.toString() + "\n";
		strval += "]";
		return strval;
	}

}
