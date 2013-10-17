/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

/**
 * Types of the data columns in a user-provided cruise data file.
 * 
 * @author Karl Smith
 */
public enum CruiseDataColumnType {
	/**
	 * The delete data type indicates data that should be 
	 * deleted from the data file when updated; only the 
	 * user should assign a column to be this type.
	 */
	DELETE,
	/**
	 * The unknown data type indicates data that the user 
	 * needs specify as one of the other standard types.
	 */
	UNKNOWN,
	/**
	 * The timestamp data type has both date and time.
	 */
	TIMESTAMP,
	/**
	 * The date data type has only the date; no time.
	 */
	DATE,
	YEAR,
	MONTH,
	DAY,
	/**
	 * The time data type has only the time; no date.
	 */
	TIME,
	HOUR,
	MINUTE,
	SECOND,
	LONGITUDE,
	LATITUDE,
	SAMPLE_DEPTH,
	SAMPLE_SALINITY,
	EQUILIBRATOR_TEMPERATURE,
	SEA_SURFACE_TEMPERATURE,
	EQUILIBRATOR_PRESSURE,
	SEA_LEVEL_PRESSURE,
	XCO2_EQU,
	XCO2_SST,
	PCO2_EQU,
	PCO2_SST,
	FCO2_EQU,
	FCO2_SST,
	/**
	 * The supplemental data type indicates data 
	 * that is carried along but otherwise ignored.
	 */
	SUPPLEMENTAL;

	/**
	 * standard header names of the standard data columns
	 */
	public static final EnumMap<CruiseDataColumnType,String> STD_HEADER_NAMES = 
			new EnumMap<CruiseDataColumnType,String>(CruiseDataColumnType.class);
	static {
		STD_HEADER_NAMES.put(DELETE, "delete");
		STD_HEADER_NAMES.put(UNKNOWN, "unknown");
		STD_HEADER_NAMES.put(TIMESTAMP, "timestamp");
		STD_HEADER_NAMES.put(DATE, "date");
		STD_HEADER_NAMES.put(YEAR, "yr");
		STD_HEADER_NAMES.put(MONTH, "mon");
		STD_HEADER_NAMES.put(DAY, "day");
		STD_HEADER_NAMES.put(TIME, "time");
		STD_HEADER_NAMES.put(HOUR, "hh");
		STD_HEADER_NAMES.put(MINUTE, "mm");
		STD_HEADER_NAMES.put(SECOND, "ss");
		STD_HEADER_NAMES.put(LONGITUDE, "longitude");
		STD_HEADER_NAMES.put(LATITUDE, "latitude");
		STD_HEADER_NAMES.put(SAMPLE_DEPTH, "sample_depth");
		STD_HEADER_NAMES.put(SAMPLE_SALINITY, "sal");
		STD_HEADER_NAMES.put(EQUILIBRATOR_TEMPERATURE, "Tequ");
		STD_HEADER_NAMES.put(SEA_SURFACE_TEMPERATURE, "SST");
		STD_HEADER_NAMES.put(EQUILIBRATOR_PRESSURE, "Pequ");
		STD_HEADER_NAMES.put(SEA_LEVEL_PRESSURE, "PPPP");
		STD_HEADER_NAMES.put(XCO2_EQU, "xCO2water_equ_dry");
		STD_HEADER_NAMES.put(XCO2_SST, "xCO2water_SST_dry");
		STD_HEADER_NAMES.put(PCO2_EQU, "pCO2water_equ_wet");
		STD_HEADER_NAMES.put(PCO2_SST, "pCO2water_SST_wet");
		STD_HEADER_NAMES.put(FCO2_EQU, "fCO2water_equ_wet");
		STD_HEADER_NAMES.put(FCO2_SST, "fCO2water_SST_wet");
		STD_HEADER_NAMES.put(SUPPLEMENTAL, "supplemental");
	}

	/**
	 * standard data descriptions of the standard data columns
	 */
	public static final EnumMap<CruiseDataColumnType,String> STD_DESCRIPTIONS = 
			new EnumMap<CruiseDataColumnType,String>(CruiseDataColumnType.class);
	static {
		STD_DESCRIPTIONS.put(DELETE, "data to be deleted"); 
		STD_DESCRIPTIONS.put(UNKNOWN, "unknown data to be identified");
		STD_DESCRIPTIONS.put(TIMESTAMP, "date and time of the measurement");
		STD_DESCRIPTIONS.put(DATE, "date of the measurement");
		STD_DESCRIPTIONS.put(YEAR, "year of the date of the measurement");
		STD_DESCRIPTIONS.put(MONTH, "month of the date of the measurement");
		STD_DESCRIPTIONS.put(DAY, "day of the date of the measurement");
		STD_DESCRIPTIONS.put(TIME, "time of the measurement");
		STD_DESCRIPTIONS.put(HOUR, "hour of the time of the measurement");
		STD_DESCRIPTIONS.put(MINUTE, "minute of the time of the measurement");
		STD_DESCRIPTIONS.put(SECOND, "second of the time of the measurement");
		STD_DESCRIPTIONS.put(LONGITUDE, "measurement longitude");
		STD_DESCRIPTIONS.put(LATITUDE, "measurement latitude");
		STD_DESCRIPTIONS.put(SAMPLE_DEPTH, "water sampling depth");
		STD_DESCRIPTIONS.put(SAMPLE_SALINITY, "measured sea surface salinity");
		STD_DESCRIPTIONS.put(EQUILIBRATOR_TEMPERATURE, "equilibrator chamber temperature");
		STD_DESCRIPTIONS.put(SEA_SURFACE_TEMPERATURE, "measured sea surface temperature");
		STD_DESCRIPTIONS.put(EQUILIBRATOR_PRESSURE, "equilibrator chamber pressure");
		STD_DESCRIPTIONS.put(SEA_LEVEL_PRESSURE, "measured atmospheric pressure");
		STD_DESCRIPTIONS.put(XCO2_EQU, "measured xCO2 (water) using equilibrator temperature (dry air)");
		STD_DESCRIPTIONS.put(XCO2_SST, "measured xCO2 (water) using sea surface temperature (dry air)");
		STD_DESCRIPTIONS.put(PCO2_EQU, "measured pCO2 (water) using equilibrator temperature (wet air)");
		STD_DESCRIPTIONS.put(PCO2_SST, "measured pCO2 (water) using sea surface temperature (wet air)");
		STD_DESCRIPTIONS.put(FCO2_EQU, "measured fCO2 (water) using equilibrator temperature (wet air)");
		STD_DESCRIPTIONS.put(FCO2_SST, "measured fCO2 (water) using sea surface temperature (wet air)");
		STD_DESCRIPTIONS.put(SUPPLEMENTAL, "supplemental data to be kept");
	}

	/**
	 * known data units of the standard data columns
	 */
	public static final EnumMap<CruiseDataColumnType,ArrayList<String>> STD_DATA_UNITS = 
			new EnumMap<CruiseDataColumnType,ArrayList<String>>(CruiseDataColumnType.class);
	static {
		STD_DATA_UNITS.put(DELETE, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(UNKNOWN, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(TIMESTAMP, new ArrayList<String>(Arrays.asList(
				"YYYY-MM-DD HH:MM:SS", "MON DAY YEAR HH:MM:SS", "DAY MON YEAR HH:MM:SS")));
		STD_DATA_UNITS.put(DATE, new ArrayList<String>(Arrays.asList(
				"YYYY-MM-DD", "MON DAY YEAR", "DAY MON YEAR")));
		STD_DATA_UNITS.put(YEAR, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(MONTH, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(DAY, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(TIME, new ArrayList<String>(Arrays.asList("HH:MM:SS")));
		STD_DATA_UNITS.put(HOUR, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(MINUTE, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(SECOND, new ArrayList<String>(Arrays.asList("")));
		STD_DATA_UNITS.put(LONGITUDE, new ArrayList<String>(Arrays.asList("decimal deg. E")));
		STD_DATA_UNITS.put(LATITUDE, new ArrayList<String>(Arrays.asList("decimal deg. N")));
		STD_DATA_UNITS.put(SAMPLE_DEPTH, new ArrayList<String>(Arrays.asList("meters")));
		STD_DATA_UNITS.put(SAMPLE_SALINITY, new ArrayList<String>(Arrays.asList("PSU")));
		STD_DATA_UNITS.put(EQUILIBRATOR_TEMPERATURE, new ArrayList<String>(Arrays.asList("deg. C")));
		STD_DATA_UNITS.put(SEA_SURFACE_TEMPERATURE, new ArrayList<String>(Arrays.asList("deg. C")));
		STD_DATA_UNITS.put(EQUILIBRATOR_PRESSURE, new ArrayList<String>(Arrays.asList("hPa", "mbar")));
		STD_DATA_UNITS.put(SEA_LEVEL_PRESSURE, new ArrayList<String>(Arrays.asList("hPa", "mbar")));
		STD_DATA_UNITS.put(XCO2_EQU, new ArrayList<String>(Arrays.asList("micromole per mole")));
		STD_DATA_UNITS.put(XCO2_SST, new ArrayList<String>(Arrays.asList("micromole per mole")));
		STD_DATA_UNITS.put(PCO2_EQU, new ArrayList<String>(Arrays.asList("microatmospheres")));
		STD_DATA_UNITS.put(PCO2_SST, new ArrayList<String>(Arrays.asList("microatmospheres")));
		STD_DATA_UNITS.put(FCO2_EQU, new ArrayList<String>(Arrays.asList("microatmospheres")));
		STD_DATA_UNITS.put(FCO2_SST, new ArrayList<String>(Arrays.asList("microatmospheres")));
		STD_DATA_UNITS.put(SUPPLEMENTAL, new ArrayList<String>(Arrays.asList("")));
	}

}