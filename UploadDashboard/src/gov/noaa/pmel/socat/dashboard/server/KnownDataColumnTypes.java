/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * Well-ordered set of known data column types that can be extended as needed.
 * The well-known data column types are:
 *     UNKNOWN, OTHER, EXPOCODE, CRUISE_NAME, SHIP_NAME, GROUP_NAME,
 *     INVESTIGATOR_NAMES, TIMESTAMP, DATE, YEAR, MONTH, DAY,
 *     TIME, HOUR, MINUTE, SECOND, DAY_OF_YEAR, SECOND_OF_DAY, 
 *     LONGITUDE, LATITUDE, and SAMPLE_DEPTH.
 * (UNKNOWN and OTHER are defined in DataColumnType)
 *  
 * @author Karl Smith
 */
public class KnownDataColumnTypes {

	// IOOS categories
	private static final String BATHYMETRY_CATEGORY = "Bathymetry";
	private static final String IDENTIFIER_CATEGORY = "Identifier";
	private static final String LOCATION_CATEGORY = "Location";
	private static final String TIME_CATEGORY = "Time";
	private static final String QUALITY_CATEGORY = "Quality";

	/** Formats for date-time stamps */
	private static final ArrayList<String> TIMESTAMP_UNITS = 
			new ArrayList<String>(Arrays.asList(
					"yyyy-mm-dd hh:mm:ss", 
					"mm-dd-yyyy hh:mm:ss", 
					"dd-mm-yyyy hh:mm:ss", 
					"mm-dd-yy hh:mm:ss", 
					"dd-mm-yy hh:mm:ss"));

	/** Formats for dates */
	private static final ArrayList<String> DATE_UNITS = 
			new ArrayList<String>(Arrays.asList(
					"yyyy-mm-dd", 
					"mm-dd-yyyy", 
					"dd-mm-yyyy", 
					"mm-dd-yy", 
					"dd-mm-yy"));

	/** Formats for time-of-day */
	private static final ArrayList<String> TIME_OF_DAY_UNITS = 
			new ArrayList<String>(Arrays.asList("hh:mm:ss"));

	/** Units for day-of-year (value of the first day of the year) */
	private static final ArrayList<String> DAY_OF_YEAR_UNITS = 
			new ArrayList<String>(Arrays.asList("Jan1=1.0", "Jan1=0.0"));

	/** Units for longitude */
	private static final ArrayList<String> LONGITUDE_UNITS = 
			new ArrayList<String>(Arrays.asList("deg.E", "deg.W"));

	/** Units of latitude */
	private static final ArrayList<String> LATITUDE_UNITS = 
			new ArrayList<String>(Arrays.asList("deg.N", "deg.S"));

	/** Unit of depth */
	private static final ArrayList<String> DEPTH_UNITS = 
			new ArrayList<String>(Arrays.asList("meters"));

	/** Unit of completely specified time ("seconds since 1970-01-01T00:00:00Z") */
	private static final ArrayList<String> TIME_UNITS = 
			new ArrayList<String>(Arrays.asList("seconds since 1970-01-01T00:00:00Z"));

	/**
	 * Unique identifier for the dataset.
	 * For SOCAT, the expocode is NODCYYYYMMDD where NODC is the ship code 
	 * and YYYY-MM-DD is the start date for the cruise; and possibly followed
	 * by -1 or -2 for non-ship vessels - where NODC is does not distinguish
	 * different vessels.  (metadata)
	 */
	public static final DataColumnType EXPOCODE = new DataColumnType("expocode", 
			"String", "expocode", null, IDENTIFIER_CATEGORY, null);
	
	/**
	 * User-provided name for the dataset (metadata)
	 */
	public static final DataColumnType DATASET_NAME = new DataColumnType("dataset_name", 
			"String", "dataset name", null, IDENTIFIER_CATEGORY, null);

	public static final DataColumnType VESSEL_NAME = new DataColumnType("vessel_name", 
			"String", "vessel name", "platform_name", IDENTIFIER_CATEGORY, null);

	public static final DataColumnType ORGANIZATION_NAME = new DataColumnType("organization", 
			"String", "organization", null, IDENTIFIER_CATEGORY, null);
	
	public static final DataColumnType INVESTIGATOR_NAMES = new DataColumnType("investigators", 
			"String", "investigators", null, IDENTIFIER_CATEGORY, null);

	public static final DataColumnType WESTERNMOST_LONGITUDE = new DataColumnType("geospatial_lon_min",
			"String", "westernmost longitude", "geospatial_lon_min", LOCATION_CATEGORY, null);

	public static final DataColumnType EASTERNMOST_LONGITUDE = new DataColumnType("geospatial_lon_max",
			"String", "easternmost longitude", "geospatial_lon_max", LOCATION_CATEGORY, null);

	public static final DataColumnType SOUTHERNMOST_LATITUDE = new DataColumnType("geospatial_lat_min",
			"String", "southernmost latitude", "geospatial_lat_min", LOCATION_CATEGORY, null);

	public static final DataColumnType NORTHERNMOST_LATITUDE = new DataColumnType("geospatial_lat_max",
			"String", "northernmost latitude", "geospatial_lat_max", LOCATION_CATEGORY, null);

	public static final DataColumnType TIME_COVERAGE_START = new DataColumnType("time_coverage_start",
			"String", "beginning time", "time_coverage_start", TIME_CATEGORY, null);

	public static final DataColumnType TIME_COVERAGE_END = new DataColumnType("time_converage_end",
			"String", "ending time", "time_converage_end", TIME_CATEGORY, null);

	public static final DataColumnType QC_FLAG = new DataColumnType("qc_flag", 
			"String", "QC flag", null, QUALITY_CATEGORY, null);

	public static final DataColumnType SAMPLE_NUMBER = new DataColumnType("sample_number",
			"Integer", "sample number", null, IDENTIFIER_CATEGORY, null);

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
			"Integer", "year", "year", TIME_CATEGORY, null);

	public static final DataColumnType MONTH_OF_YEAR = new DataColumnType("month", 
			"Integer", "month of year", "month_of_year", TIME_CATEGORY, null);
	
	public static final DataColumnType DAY_OF_MONTH = new DataColumnType("day", 
			"Integer", "day of month", "day_of_month", TIME_CATEGORY, null);

	public static final DataColumnType TIME_OF_DAY = new DataColumnType("time_of_day", 
			"String", "time of day", null, null, TIME_OF_DAY_UNITS);

	public static final DataColumnType HOUR_OF_DAY = new DataColumnType("hour", 
			"Integer", "hour of day", "hour_of_day", TIME_CATEGORY, null);

	public static final DataColumnType MINUTE_OF_HOUR = new DataColumnType("minute", 
			"Integer", "minute of hour", "minute_if_hour", TIME_CATEGORY, null);

	public static final DataColumnType SECOND_OF_MINUTE = new DataColumnType("second", 
			"Double", "second of minute", "second_of_minute", TIME_CATEGORY, null);

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
			"Double", "second of day", "second_of_day", TIME_CATEGORY, null);

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
	public KnownDataColumnTypes() {
		// Give plenty of capacity for expansion
		// since this is a LinkedHashMap, extra capacity not really a problem
		knownTypes = new LinkedHashMap<String,DataColumnType>(64);
		addDataColumn(DataColumnType.UNKNOWN);
		addDataColumn(DataColumnType.OTHER);
	}

	/**
	 * Adds the default well-known data column types for the client:
	 *     EXPOCODE, DATASET_NAME, VESSEL_NAME, ORGANIZATION_NAME, 
	 *     INVESTIGATOR_NAMES, TIMESTAMP, DATE, YEAR, MONTH_OF_YEAR, 
	 *     DAY_OF_MONTH, TIME_OF_DAY, HOUR_OF_DAY, MINUTE_OF_HOUR, 
	 *     SECOND_OF_MINUTE, DAY_OF_YEAR, SECOND_OF_DAY, 
	 *     LONGITUDE, LATITUDE, and SAMPLE_DEPTH.
	 * This should be called before adding any custom types.
	 */
	public void addStandardTypesForClient() {
		addDataColumn(EXPOCODE);
		addDataColumn(DATASET_NAME);
		addDataColumn(VESSEL_NAME);
		addDataColumn(ORGANIZATION_NAME);
		addDataColumn(INVESTIGATOR_NAMES);
		addDataColumn(TIMESTAMP);
		addDataColumn(DATE);
		addDataColumn(YEAR);
		addDataColumn(MONTH_OF_YEAR);
		addDataColumn(DAY_OF_MONTH);
		addDataColumn(TIME_OF_DAY);
		addDataColumn(HOUR_OF_DAY);
		addDataColumn(MINUTE_OF_HOUR);
		addDataColumn(SECOND_OF_MINUTE);
		addDataColumn(DAY_OF_YEAR);
		addDataColumn(SECOND_OF_DAY);
		addDataColumn(LONGITUDE);
		addDataColumn(LATITUDE);
		addDataColumn(SAMPLE_DEPTH);
	}

	/**
	 * Adds the given data column type to this collection of known data 
	 * column types.  Only the upper-cased data column type name is used 
	 * to differentiate known column types.  The given instance of the 
	 * DataColumnType is added to the internal collection of known data 
	 * column types.
	 * 
	 * @param dtype
	 * 		new column type to add to the known list
	 * @return
	 * 		existing known column type that was replaced;
	 * 		null if there was no existing known data column type with matching name 
	 */
	private DataColumnType addDataColumn(DataColumnType dtype) {
		return knownTypes.put(dtype.getVarName().toUpperCase(), dtype);
	}

	/**
	 * Determines is a given data column type name exists in the list
	 * of known data column types.  The upper-cased name is compared
	 * to the upper-cased names in the known data column types.
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
	 * Create additional known column types from values in a Properties file.
	 * Only the data column type name and list of units are assigned in the 
	 * known data types.
	 * TODO: rework to include description, standard name, category name - probably needs to be in XML
	 * 
	 * @param knownTypesFile
	 * 		properties file of data column types to add to the known list 
	 * 		with the simple line format:
	 * 			typeName=className;unit1,unit2,... 
	 * @throws IllegalArgumentException
	 * 		if the name of a data column type to add to the known list 
	 * 		matches the name of a data column type in the known list 
	 * 		(using {@link #containsTypeName(String)};
	 * 		if the className is not given
	 * @throws IOException
	 * 		if problems occur when reading the Properties file
	 */
	public void addTypesFromProperties(File knownTypesFile) throws IllegalArgumentException, IOException {
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
		for ( String name : typeProps.stringPropertyNames() ) {
			if ( containsTypeName(name) )
				throw new IllegalArgumentException("Duplicate known data column type name '" + name + "'");
			String values[] = typeProps.getProperty(name).trim().split(";", 2);
			String className = "";
			String unitsString = "";
			if ( values.length > 0 )
				className = values[0].trim();
			if ( values.length > 1 )
				unitsString = values[1];
			if ( className.isEmpty() )
				throw new IllegalArgumentException("Invalid data class name for " + name);
			if ( ! unitsString.isEmpty() ) {
				LinkedHashSet<String> units = new LinkedHashSet<String>();
				for ( String val : unitsString.split(",") ) {
					units.add(val.trim());
				}
				addDataColumn(new DataColumnType(name, className, null, null, null, units));
			}
			else {
				addDataColumn(new DataColumnType(name, className, null, null, null, null));
			}
		}
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
	 * Returns a copy of the known data column type whose variable 
	 * name matches (comparing upper-cased) the given variable name.  
	 * The selected unit will be zero and the select missing values 
	 * will be an empty string (default missing values).
	 * 
	 * @param varName
	 * 		data column type variable name to find
	 * @return
	 * 		copy of the known data column type whose variable name
	 * 		matches the given variable name.
	 */
	public DataColumnType getDataColumnType(String typeName) {
		DataColumnType dtype = knownTypes.get(typeName.toUpperCase());
		if ( dtype == null )
			return null;
		return new DataColumnType(dtype.getVarName(), dtype.getDataClassName(), 
				dtype.getDescription(), dtype.getStandardName(), dtype.getCategoryName(), dtype.getUnits());
	}

	@Override
	public String toString() {
		String strval = "KnownDataColumnTypes[\n";
		for ( DataColumnType dtype : knownTypes.values() )
			strval += "    " + dtype.toString() + "\n";
		strval += "]";
		return strval;
	}

}
