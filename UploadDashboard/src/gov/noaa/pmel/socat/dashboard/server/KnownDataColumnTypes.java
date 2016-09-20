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
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * Well-ordered set of known data column types that can be extended as needed.
 *  
 * @author Karl Smith
 */
public class KnownDataColumnTypes {

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
	/** Formats for times */
	private static final ArrayList<String> TIME_UNITS = 
			new ArrayList<String>(Arrays.asList("hh:mm:ss"));
	/** Units for day-of-year (value of the first day of the year) */
	private static final ArrayList<String> DAY_OF_YEAR_UNITS = 
			new ArrayList<String>(Arrays.asList("Jan1=1.0", "Jan1=0.0"));
	/** Units for longitudes */
	private static final ArrayList<String> LONGITUDE_UNITS = 
			new ArrayList<String>(Arrays.asList("deg.E", "deg.W"));
	/** Units of latitudes */
	private static final ArrayList<String> LATITUDE_UNITS = 
			new ArrayList<String>(Arrays.asList("deg.N", "deg.S"));
	/** Units of depths */
	private static final ArrayList<String> DEPTH_UNITS = 
			new ArrayList<String>(Arrays.asList("meters"));

	/**
	 * EXPOCODE is NODCYYYYMMDD where NODC is the ship code and 
	 * YYYY-MM-DD is the start date (metadata)
	 */
	public static final DataColumnType EXPOCODE = new DataColumnType("expocode", null);
	
	/**
	 * User-provided name for the cruise or dataset (metadata)
	 */
	public static final DataColumnType CRUISE_NAME = new DataColumnType("cruise_name", null);

	/**
	 * Name of the ship or vessel (metadata)
	 */
	public static final DataColumnType SHIP_NAME = new DataColumnType("ship_name", null);

	/**
	 * Name of the group (metadata)
	 */
	public static final DataColumnType GROUP_NAME = new DataColumnType("group_name", null);
	
	/**
	 * Name(s) of the investigator(s) (metadata)
	 */
	public static final DataColumnType INVESTIGATOR_NAMES = new DataColumnType("PI_names", null);

	/**
	 * Date and time or the measurement
	 */
	public static final DataColumnType TIMESTAMP = new DataColumnType("date_time", TIMESTAMP_UNITS);

	/**
	 * Date of the measurement (no time).
	 */
	public static final DataColumnType DATE = new DataColumnType("date", DATE_UNITS);

	/**
	 * Year of the date of the measurement.
	 */
	public static final DataColumnType YEAR = new DataColumnType("year", null);

	/**
	 * Month of the date of the measurement.
	 */
	public static final DataColumnType MONTH = new DataColumnType("month", null);
	
	/**
	 * Day of the date of the measurement.
	 */
	public static final DataColumnType DAY = new DataColumnType("day", null);

	/**
	 * Time of the measurement (no date).
	 */
	public static final DataColumnType TIME = new DataColumnType("time", TIME_UNITS);

	/**
	 * Hour of the time of the measurement.
	 */
	public static final DataColumnType HOUR = new DataColumnType("hour", null);

	/**
	 * Minute of the time of the measurement.
	 */
	public static final DataColumnType MINUTE = new DataColumnType("minute", null);

	/**
	 * Second of the time of the measurement.
	 */
	public static final DataColumnType SECOND = new DataColumnType("second", null);

	/**
	 * DAY_OF_YEAR, along with YEAR, and possibly SECOND_OF_DAY,
	 * may be used to specify the date and time of the measurement.
	 */
	public static final DataColumnType DAY_OF_YEAR = new DataColumnType("day_of_year", DAY_OF_YEAR_UNITS);

	/**
	 * SECOND_OF_DAY, along with YEAR and DAY_OF_YEAR may
	 * be used to specify date and time of the measurement
	 */
	public static final DataColumnType SECOND_OF_DAY = new DataColumnType("sec_of_day", null);

	/**
	 * Longitude of the measurement.
	 */
	public static final DataColumnType LONGITUDE = new DataColumnType("longitude", LONGITUDE_UNITS);

	/**
	 * Latitude of the measurement.
	 */
	public static final DataColumnType LATITUDE = new DataColumnType("latitude", LATITUDE_UNITS);

	/**
	 * Sampling depth of the measurement.
	 */
	public static final DataColumnType SAMPLE_DEPTH = new DataColumnType("sample_depth", DEPTH_UNITS);

	private LinkedHashSet<DataColumnType> knownTypes;

	/**
	 * Creates with the default well-known data column types:
	 *     UNKNOWN, OTHER, EXPOCODE, CRUISE_NAME, SHIP_NAME, GROUP_NAME,
	 *     INVESTIGATOR_NAMES, TIMESTAMP, DATE, YEAR, MONTH, DAY,
	 *     TIME, HOUR, MINUTE, SECOND, DAY_OF_YEAR, SECOND_OF_DAY, 
	 *     LONGITUDE, LATITUDE, and SAMPLE_DEPTH.
	 */
	public KnownDataColumnTypes() {
		// Give plenty of capacity for expansion
		// since this is a LinkedHashMap, extra capacity not really a problem
		knownTypes = new LinkedHashSet<DataColumnType>(64);
		knownTypes.add(DataColumnType.UNKNOWN);
		knownTypes.add(DataColumnType.OTHER);
		knownTypes.add(EXPOCODE);
		knownTypes.add(CRUISE_NAME);
		knownTypes.add(SHIP_NAME);
		knownTypes.add(GROUP_NAME);
		knownTypes.add(INVESTIGATOR_NAMES);
		knownTypes.add(TIMESTAMP);
		knownTypes.add(DATE);
		knownTypes.add(YEAR);
		knownTypes.add(MONTH);
		knownTypes.add(DAY);
		knownTypes.add(TIME);
		knownTypes.add(HOUR);
		knownTypes.add(MINUTE);
		knownTypes.add(SECOND);
		knownTypes.add(DAY_OF_YEAR);
		knownTypes.add(SECOND_OF_DAY);
		knownTypes.add(LONGITUDE);
		knownTypes.add(LATITUDE);
		knownTypes.add(SAMPLE_DEPTH);
	}

	/**
	 * Create additional known column types from values in a Properties file.
	 * Only the data column type name and list of units are assigned in the known 
	 * data types.
	 * 
	 * @param knownTypesFile
	 * 		properties file of data column types to add to the known list 
	 * 		with the simple line format:
	 * 			type=unit1,unit2,... 
	 * @throws IllegalArgumentException
	 * 		if a data column type name matches, compared case-insensitive, 
	 * 		an existing known data column type name.
	 * @throws IOException
	 * 		if problems reading the Properties file
	 */
	public void addTypeFromProperties(File knownTypesFile) throws IllegalArgumentException, IOException {
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
			for ( DataColumnType dtype : knownTypes ) {
				if ( dtype.getName().equalsIgnoreCase(name) )
					throw new IllegalArgumentException("Duplicate known data column type name '" + name + "'");
			}
			String unitsString = typeProps.getProperty(name).trim();
			if ( ! unitsString.isEmpty() ) {
				LinkedHashSet<String> units = new LinkedHashSet<String>();
				for ( String val : unitsString.split(",") ) {
					units.add(val.trim());
				}
				knownTypes.add(new DataColumnType(name, units));
			}
			else {
				knownTypes.add(new DataColumnType(name, null));
			}
		}
	}

	@Override
	public String toString() {
		String strval = "KnownDataColumnTypes[\n";
		for ( DataColumnType dtype : knownTypes )
			strval += "    " + dtype.toString() + "\n";
		strval += "]";
		return strval;
	}

	
}
