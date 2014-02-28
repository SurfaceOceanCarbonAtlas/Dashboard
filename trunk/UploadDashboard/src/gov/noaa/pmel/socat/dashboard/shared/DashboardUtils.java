/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.TreeSet;

import com.googlecode.gwt.crypto.client.TripleDesCipher;

/**
 * Static dashboard utility functions and constants
 * for use on both the client and server side.
 *  
 * @author Karl Smith
 */
public class DashboardUtils {

	// Cruise upload action strings
	public static final String REQUEST_PREVIEW_TAG = "REQUEST PREVIEW TAG";
	public static final String REQUEST_NEW_CRUISE_TAG = "REQUEST NEW CRUISE TAG";
	public static final String REQUEST_OVERWRITE_CRUISE_TAG = "REQUEST OVERWRITE CRUISE TAG";

	// Cruise upload result strings
	public static final String FILE_PREVIEW_HEADER_TAG = "FILE PREVIEW HEADER TAG";
	public static final String NO_EXPOCODE_HEADER_TAG = "NO EXPOCODE HEADER TAG";
	public static final String FILE_EXISTS_HEADER_TAG = "FILE EXISTS HEADER TAG";
	public static final String CANNOT_OVERWRITE_HEADER_TAG = "CANNOT OVERWRITE HEADER TAG";
	public static final String NO_FILE_HEADER_TAG = "NO FILE HEADER TAG";
	public static final String FILE_CREATED_HEADER_TAG = "FILE CREATED HEADER TAG";
	public static final String FILE_UPDATED_HEADER_TAG = "FILE UPDATED HEADER TAG";

	/*
	 * Only valid characters for an expocode are upper-case alphanumeric, 
	 * underscore, and hyphen; the latter two are for the very rare case 
	 * of valid duplicate expocodes. 
	 */
	public static final String VALID_EXPOCODE_CHARACTERS = 
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-";
	public static final int MIN_EXPOCODE_LENGTH = 12;
	public static final int MAX_EXPOCODE_LENGTH = 14;

	// Recognized data formats
	public static final String CRUISE_FORMAT_COMMA = "data are comma-separated values";
	public static final String CRUISE_FORMAT_TAB = "data are tab-separated values";

	// Maximum number of rows shown in a page of a data grid (table)
	public static final int MAX_ROWS_PER_GRID_PAGE = 50;

	// Data check strings
	public static final String CHECK_STATUS_NOT_CHECKED = "";
	public static final String CHECK_STATUS_ACCEPTABLE = "Acceptable";
	public static final String CHECK_STATUS_WARNINGS_PREFIX = "Warnings:";
	public static final String CHECK_STATUS_ERRORS_PREFIX = "Errors:";
	public static final String CHECK_STATUS_UNACCEPTABLE = "Unacceptable";

	// Cruise QC strings
	public static final String QC_STATUS_NOT_SUBMITTED = "";
	public static final String QC_STATUS_SUBMITTED = "Submitted";
	public static final String QC_STATUS_ACCEPTED_A = "Flag A";
	public static final String QC_STATUS_ACCEPTED_B = "Flag B";
	public static final String QC_STATUS_ACCEPTED_C = "Flag C";
	public static final String QC_STATUS_ACCEPTED_D = "Flag D";
	public static final String QC_STATUS_ACCEPTED_E = "Flag E";
	public static final String QC_STATUS_UNACCEPTABLE = "Flag F";
	public static final String QC_STATUS_SUSPENDED = "Suspended";
	public static final String QC_STATUS_EXCLUDED = "Excluded";

	// Archival options
	public static final String ARCHIVE_STATUS_NOT_SUBMITTED = "";
	public static final String ARCHIVE_STATUS_WITH_SOCAT = "With next SOCAT";
	public static final String ARCHIVE_STATUS_SENT_CDIAC = "Sent to CDIAC";
	public static final String ARCHIVE_STATUS_OWNER_ARCHIVE = "Owner to archive";

	/**
	 * Header names of the standard data columns that a user might provide
	 */
	public static final EnumMap<DataColumnType,String> STD_HEADER_NAMES = 
			new EnumMap<DataColumnType,String>(DataColumnType.class);
	static {
		STD_HEADER_NAMES.put(DataColumnType.UNKNOWN, "(unknown)");
		STD_HEADER_NAMES.put(DataColumnType.EXPOCODE, "expocode");
		STD_HEADER_NAMES.put(DataColumnType.CRUISE_NAME, "cruise name");
		STD_HEADER_NAMES.put(DataColumnType.TIMESTAMP, "timestamp");
		STD_HEADER_NAMES.put(DataColumnType.DATE, "date");
		STD_HEADER_NAMES.put(DataColumnType.YEAR, "year");
		STD_HEADER_NAMES.put(DataColumnType.MONTH, "month");
		STD_HEADER_NAMES.put(DataColumnType.DAY, "day");
		STD_HEADER_NAMES.put(DataColumnType.TIME, "time");
		STD_HEADER_NAMES.put(DataColumnType.HOUR, "hh");
		STD_HEADER_NAMES.put(DataColumnType.MINUTE, "mm");
		STD_HEADER_NAMES.put(DataColumnType.SECOND, "ss");
		STD_HEADER_NAMES.put(DataColumnType.LONGITUDE, "longitude");
		STD_HEADER_NAMES.put(DataColumnType.LATITUDE, "latitude");
		STD_HEADER_NAMES.put(DataColumnType.SAMPLE_DEPTH, "sample_depth");
		STD_HEADER_NAMES.put(DataColumnType.SALINITY, "sal");
		STD_HEADER_NAMES.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, "Tequ");
		STD_HEADER_NAMES.put(DataColumnType.SEA_SURFACE_TEMPERATURE, "SST");
		STD_HEADER_NAMES.put(DataColumnType.EQUILIBRATOR_PRESSURE, "Pequ");
		STD_HEADER_NAMES.put(DataColumnType.SEA_LEVEL_PRESSURE, "SLP");
		STD_HEADER_NAMES.put(DataColumnType.XCO2WATER_EQU, "xCO2water_equ_dry");
		STD_HEADER_NAMES.put(DataColumnType.XCO2WATER_SST, "xCO2water_SST_dry");
		STD_HEADER_NAMES.put(DataColumnType.PCO2WATER_EQU, "pCO2water_equ_wet");
		STD_HEADER_NAMES.put(DataColumnType.PCO2WATER_SST, "pCO2water_SST_wet");
		STD_HEADER_NAMES.put(DataColumnType.FCO2WATER_EQU, "fCO2water_equ_wet");
		STD_HEADER_NAMES.put(DataColumnType.FCO2WATER_SST, "fCO2water_SST_wet");
		STD_HEADER_NAMES.put(DataColumnType.XCO2_ATM, "xCO2_air");
		STD_HEADER_NAMES.put(DataColumnType.PCO2_ATM, "pCO2_air");
		STD_HEADER_NAMES.put(DataColumnType.FCO2_ATM, "fCO2_air");
		STD_HEADER_NAMES.put(DataColumnType.SHIP_SPEED, "ship_speed");
		STD_HEADER_NAMES.put(DataColumnType.SHIP_DIRECTION, "ship_dir");
		STD_HEADER_NAMES.put(DataColumnType.WIND_SPEED_TRUE, "wind_speed_true");
		STD_HEADER_NAMES.put(DataColumnType.WIND_SPEED_RELATIVE, "wind_speed_rel");
		STD_HEADER_NAMES.put(DataColumnType.WIND_DIRECTION_TRUE, "wind_dir_true");
		STD_HEADER_NAMES.put(DataColumnType.WIND_DIRECTION_RELATIVE, "wind_dir_rel");
	}

	/*
	 * known data units of the standard data columns
	 */
	public static final ArrayList<String> NO_UNITS = new ArrayList<String>(Arrays.asList(""));
	public static final ArrayList<String> LONGITUDE_UNITS = new ArrayList<String>(Arrays.asList("deg.E"));
	public static final ArrayList<String> LATITUDE_UNITS = new ArrayList<String>(Arrays.asList("deg.N"));
	public static final ArrayList<String> DEPTH_UNITS = new ArrayList<String>(Arrays.asList("meters"));
	public static final ArrayList<String> SALINITY_UNITS = new ArrayList<String>(Arrays.asList("PSU"));
	public static final ArrayList<String> TEMPERATURE_UNITS = new ArrayList<String>(Arrays.asList("deg.C", "Kelvin", "deg.F"));
	public static final ArrayList<String> PRESSURE_UNITS = new ArrayList<String>(Arrays.asList("hPa", "kPa"));
	public static final ArrayList<String> XCO2_UNITS = new ArrayList<String>(Arrays.asList("umol/mol"));
	public static final ArrayList<String> PCO2_UNITS = new ArrayList<String>(Arrays.asList("uatm"));
	public static final ArrayList<String> FCO2_UNITS = new ArrayList<String>(Arrays.asList("uatm"));
	public static final ArrayList<String> DIRECTION_UNITS = new ArrayList<String>(Arrays.asList("deg.clk.N"));
	public static final ArrayList<String> SHIP_SPEED_UNITS = new ArrayList<String>(Arrays.asList("knots", "km/h", "mph"));
	public static final ArrayList<String> WIND_SPEED_UNITS = new ArrayList<String>(Arrays.asList("m/s"));

	/**
	 * Available data units for the standard data columns that a user might provide.
	 * The first unit is the standard unit.
	 */
	public static final EnumMap<DataColumnType,ArrayList<String>> STD_DATA_UNITS = 
			new EnumMap<DataColumnType,ArrayList<String>>(DataColumnType.class);
	static {
		STD_DATA_UNITS.put(DataColumnType.UNKNOWN, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.EXPOCODE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.CRUISE_NAME, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.TIMESTAMP, new ArrayList<String>(Arrays.asList(
				"yyyy-mm-dd hh:mm:ss", "mm/dd/yyyy hh:mm:ss", "dd/mm/yyyy hh:mm:ss")));
		STD_DATA_UNITS.put(DataColumnType.DATE, new ArrayList<String>(Arrays.asList(
				"yyyy-mm-dd", "mm/dd/yyyy", "dd/mm/yyyy")));
		STD_DATA_UNITS.put(DataColumnType.YEAR, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.MONTH, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.DAY, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.TIME, new ArrayList<String>(Arrays.asList("hh:mm:ss")));
		STD_DATA_UNITS.put(DataColumnType.HOUR, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.MINUTE, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SECOND, NO_UNITS);
		STD_DATA_UNITS.put(DataColumnType.LONGITUDE, LONGITUDE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.LATITUDE, LATITUDE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SAMPLE_DEPTH, DEPTH_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SALINITY, SALINITY_UNITS);
		STD_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, TEMPERATURE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SEA_SURFACE_TEMPERATURE, TEMPERATURE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.EQUILIBRATOR_PRESSURE, PRESSURE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SEA_LEVEL_PRESSURE, PRESSURE_UNITS);
		STD_DATA_UNITS.put(DataColumnType.XCO2WATER_EQU, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.XCO2WATER_SST, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2WATER_EQU, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2WATER_SST, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2WATER_EQU, FCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2WATER_SST, FCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.XCO2_ATM, XCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.PCO2_ATM, PCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.FCO2_ATM, FCO2_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SHIP_SPEED, SHIP_SPEED_UNITS);
		STD_DATA_UNITS.put(DataColumnType.SHIP_DIRECTION, DIRECTION_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_SPEED_TRUE, WIND_SPEED_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_SPEED_RELATIVE, WIND_SPEED_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_TRUE, DIRECTION_UNITS);
		STD_DATA_UNITS.put(DataColumnType.WIND_DIRECTION_RELATIVE, DIRECTION_UNITS);
	}

	/**
	 * Standard descriptions of all known column types in the standard unit.
	 */
	public static final EnumMap<DataColumnType,String> STD_DESCRIPTIONS =
			new EnumMap<DataColumnType,String>(DataColumnType.class);
	static {
		STD_DESCRIPTIONS.put(DataColumnType.UNKNOWN, 
				"unknown data to be identified");
		STD_DESCRIPTIONS.put(DataColumnType.EXPOCODE, 
				"cruise expocode");
		STD_DESCRIPTIONS.put(DataColumnType.CRUISE_NAME, 
				"cruise name");
		STD_DESCRIPTIONS.put(DataColumnType.YEAR, 
				"year of the date (UTC) of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.MONTH, 
				"month of the date (UTC) of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.DAY, 
				"day of the date (UTC) of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.HOUR, 
				"hour of the time (UTC) of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.MINUTE, 
				"minute of the time (UTC) of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.SECOND, 
				"second of the time (UTC) of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.LATITUDE, 
				"measurement longitude in decimal degrees East");
		STD_DESCRIPTIONS.put(DataColumnType.LATITUDE, 
				"measurement latitude in decimal degrees North");
		STD_DESCRIPTIONS.put(DataColumnType.SAMPLE_DEPTH, 
				"water sampling depth in meters");
		STD_DESCRIPTIONS.put(DataColumnType.SEA_SURFACE_TEMPERATURE, 
				"measured sea surface temperature in degrees Celcius");
		STD_DESCRIPTIONS.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, 
				"equilibrator chamber temperature in degrees Celcius");
		STD_DESCRIPTIONS.put(DataColumnType.SALINITY, 
				"measured salinity on the practical salinity scale");
		STD_DESCRIPTIONS.put(DataColumnType.SEA_LEVEL_PRESSURE, 
				"measured sea-level atmospheric pressure in hectopascals");
		STD_DESCRIPTIONS.put(DataColumnType.EQUILIBRATOR_PRESSURE, 
				"equilibrator chamber pressure in hectopascals");
		STD_DESCRIPTIONS.put(DataColumnType.XCO2WATER_SST, 
				"measured xCO2 (water) in micromoles per mole using sea surface temperature (dry air)");
		STD_DESCRIPTIONS.put(DataColumnType.XCO2WATER_EQU, 
				"measured xCO2 (water) in micromoles per mole using equilibrator temperature (dry air)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2WATER_SST, 
				"measured fCO2 (water) in microatmospheres using sea surface temperature (wet air)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2WATER_EQU, 
				"measured fCO2 (water) in microatmospheres using equilibrator temperature (wet air)");
		STD_DESCRIPTIONS.put(DataColumnType.PCO2WATER_SST, 
				"measured pCO2 (water) in microatmospheres using sea surface temperature (wet air)");
		STD_DESCRIPTIONS.put(DataColumnType.PCO2WATER_EQU, 
				"measured pCO2 (water) in microatmospheres using equilibrator temperature (wet air)");
		STD_DESCRIPTIONS.put(DataColumnType.XCO2_ATM, 
				"measured atmospheric xCO2 in micromoles per mole");
		STD_DESCRIPTIONS.put(DataColumnType.PCO2_ATM, 
				"measured atmospheric pCO2 in microatmospheres");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_ATM, 
				"measured atmospheric fCO2 in microatmospheres");
		STD_DESCRIPTIONS.put(DataColumnType.SHIP_SPEED, 
				"measured ship speed in knots");
		STD_DESCRIPTIONS.put(DataColumnType.SHIP_DIRECTION, 
				"ship direction in degrees clockwise from north");
		STD_DESCRIPTIONS.put(DataColumnType.WIND_SPEED_TRUE, 
				"true wind speed in meters per second");
		STD_DESCRIPTIONS.put(DataColumnType.WIND_SPEED_RELATIVE, 
				"wind speed relative to the ship in meters per second");
		STD_DESCRIPTIONS.put(DataColumnType.WIND_DIRECTION_TRUE, 
				"true wind direction in degrees clockwise from north");
		STD_DESCRIPTIONS.put(DataColumnType.WIND_DIRECTION_RELATIVE, 
				"wind direction relative to the ship in degrees clockwise from north");
		STD_DESCRIPTIONS.put(DataColumnType.DELTA_PCO2, 
				"difference in atmospheric and water pCO2 values");
		STD_DESCRIPTIONS.put(DataColumnType.DELTA_FCO2, 
				"difference in atmospheric and water fCO2 values");
		STD_DESCRIPTIONS.put(DataColumnType.WOA_SALINITY,
				"sea surface salinity on the practical salinity scale interpolated from the World Ocean Atlas 2005 " +
				"(see: //http://www.nodc.noaa.gov/OC5/WOA05/pr_woa05.html)");
		STD_DESCRIPTIONS.put(DataColumnType.NCEP_SEA_LEVEL_PRESSURE,
				"sea level pressure in hectopascals interpolated from the NCEP/NCAR 40-Year Reanalysis Project " +
				"(see: http://www.esrl.noaa.gov/psd/data/gridded/data.ncep.reanalysis.surface.html)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_XCO2_TEQ_PEQ_SAL,
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_XCO2_SST_PEQ_SAL,
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_PCO2_TEQ_PEQ_SAL,
				"fCO2 in microatmospheres recomputed from measured pCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_PCO2_SST_PEQ_SAL,
				"fCO2 in microatmospheres recomputed from measured pCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_FCO2_TEQ_PEQ_SAL,
				"fCO2 in microatmospheres recomputed from measured fCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_FCO2_SST_PEQ_SAL,
				"fCO2 in microatmospheres recomputed from measured fCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_PCO2_TEQ_NCP_SAL,
				"fCO2 in microatmospheres recomputed from measured pCO2 (water) using equilibrator temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_PCO2_SST_NCP_SAL,
				"fCO2 in microatmospheres recomputed from measured pCO2 (water) using sea surface temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_XCO2_TEQ_PEQ_WOA,
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_XCO2_SST_PEQ_WOA,
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_XCO2_TEQ_NCP_SAL,
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_XCO2_SST_NCP_SAL,
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using sea surface temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_XCO2_TEQ_NCP_WOA,
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"NCEP sea level pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_FROM_XCO2_SST_NCP_WOA,
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using sea surface temperature, " +
				"NCEP sea level pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_REC,
				"fCO2 in microatmospheres recomputed from the most desireable measured CO2 data " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_REC_SOURCE,
				"algorithm number (1-14) for generating the fCO2Rec value " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.DELTA_TEMPERATURE,
				"difference in temperature in degrees Celcius between the equilibrator water and the sea surface water (TEqu - SST)");
		STD_DESCRIPTIONS.put(DataColumnType.REGION_ID,
				"SOCAT region ID for the location of this measurement " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		STD_DESCRIPTIONS.put(DataColumnType.SECONDS_1970, 
				"time of measurement in seconds since Jan 1, 1970 00:00:00 UTC");
		STD_DESCRIPTIONS.put(DataColumnType.DAYS_1970,
				"time of measurement in days since Jan 1, 1970 00:00:00 UTC");
		STD_DESCRIPTIONS.put(DataColumnType.DAY_OF_YEAR,
				"time of measurement in days since Jan 1 00:00 UTC of the year of the measurement");
		STD_DESCRIPTIONS.put(DataColumnType.CALC_SHIP_SPEED,
				"calculated ship speed in knots using the previous and/or subsequent data measurement");
		STD_DESCRIPTIONS.put(DataColumnType.ETOPO2,
				"bathymetric depth in meters interpolated from the ETOPO2 2 arc-minute Gridded Global Relief Data " +
				"(see: http://www.ngdc.noaa.gov/mgg/global/etopo2.html)");
		STD_DESCRIPTIONS.put(DataColumnType.GVCO2,
				"atmospheric xCO2 in micromoles per mole interpolated from GlobalView-CO2, 2012 1979-01-01 to 2012-01-01 data " +
				"(see: http://www.esrl.noaa.gov/gmd/ccgg/globalview/index.html)");
		STD_DESCRIPTIONS.put(DataColumnType.DISTANCE_TO_LAND,
				"estimated distance in km to major land mass (up to 1000 km)");
		STD_DESCRIPTIONS.put(DataColumnType.FCO2_REC_WOCE_FLAG,
				"WOCE quality-control flag (2=okay,3=questionable,4=bad) for the fCO2Rec value " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
	}

	/**
	 * Generate the encrypted password for a given plain-text username 
	 * and password.  This is intended to only be a first level of
	 * encryption.
	 * 
	 * @param username
	 * 		plain-text username to use
	 * @param password
	 * 		plain-text password to use 
	 * @return
	 * 		encrypted password, or an empty string if an error occurs 
	 */
	public static String passhashFromPlainText(String username, String password) {
		// Make sure something reasonable Strings are given
		if ( (username.length() < 4) || (password.length() < 7) ) {
			return "";
		}

		// This salt is just to make sure the keys are long enough
		String salt = "4z#Ni!q?F7b0m9nK(uDF[g%T3pD_";

		// Encrypt the password
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey((username.substring(0,4) + password + salt)
			  .substring(0,24).getBytes());
		String passhash;
		try {
			passhash = cipher.encrypt((password + salt).substring(0,32));
		} catch (Exception ex) {
			passhash = "";
		}

		return passhash;
	}

	/**
	 * Decodes a (JSON-like) encoded array of numbers into a byte array. 
	 * Numeric values are separated by a comma, which may have whitespace
	 * around it.
	 * 
	 * @param arrayStr
	 * 		JSON-encoded array of byte values to use
	 * @return
	 * 		a byte array represented arrayStr
	 * @throws NumberFormatException
	 * 		if arrayStr does not start with '[', does not end with ']', 
	 * 		or contains values inappropriate for the byte type
	 */
	public static byte[] decodeByteArray(String arrayStr) 
										throws NumberFormatException {
		if ( ! ( arrayStr.startsWith("[") && arrayStr.endsWith("]") ) )
			throw new NumberFormatException(
					"Encoded byte array not enclosed in brackets");
		String[] pieces = arrayStr.substring(1, arrayStr.length()-1)
								  .split("\\s*,\\s*", -1);
		if ( (pieces.length == 1) && pieces[0].trim().isEmpty() )
			return new byte[0];
		byte[] byteArray = new byte[pieces.length];
		for (int k = 0; k < pieces.length; k++)
			byteArray[k] = Byte.parseByte(pieces[k].trim());
		return byteArray;
	}

	/**
	 * JSON-encodes an ArrayList of Integers suitable for decoding 
	 * with {@link #decodeIntegerArrayList(String)}
	 * 
	 * @param intList
	 * 		list of integer values to encode
	 * @return
	 * 		the encoded list of integer values
	 */
	public static String encodeIntegerArrayList(ArrayList<Integer> intList) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		boolean firstValue = true;
		for ( Integer intVal : intList ) {
			if ( firstValue )
				firstValue = false;
			else
				sb.append(", ");
			sb.append(intVal.toString());
		}
		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * Decodes a (JSON-like) encoded array of numbers into an ArrayList of 
	 * Integers.  Will decode an encoded string produced by 
	 * {@link #encodeIntegerArrayList(ArrayList)}  
	 * 
	 * @param arrayStr
	 * 		JSON-encoded array of integer values to use
	 * @return
	 * 		the decoded ArrayList of Integers; never null but may be empty
	 * @throws NumberFormatException
	 * 		if arrayStr does not start with '[', does not end with ']', 
	 * 		or contains values inappropriate for the integer type
	 */
	public static ArrayList<Integer> decodeIntegerArrayList(String arrayStr) 
										throws NumberFormatException {
		if ( ! ( arrayStr.startsWith("[") && arrayStr.endsWith("]") ) )
			throw new NumberFormatException(
					"Encoded integer array not enclosed in brackets");
		String[] pieces = arrayStr.substring(1, arrayStr.length()-1)
								  .split("\\s*,\\s*", -1);
		if ( (pieces.length == 1) && pieces[0].trim().isEmpty() )
			return new ArrayList<Integer>(0);
		Integer[] intArray = new Integer[pieces.length];
		for (int k = 0; k < pieces.length; k++)
			intArray[k] = Integer.parseInt(pieces[k].trim());
		return new ArrayList<Integer>(Arrays.asList(intArray));
	}

	/**
	 * Encodes an ArrayList of strings suitable for decoding using 
	 * {@link #decodeStringArrayList(String)}.  Characters within
	 * the strings are copied as-is, thus newline characters, or
	 * the character sequence double quote - comma - double quote, 
	 * within a string will likely cause problems when reading or 
	 * decoding the encoded string.
	 * 
	 * @param strList
	 * 		the ArrayList of strings to encode
	 * @return
	 * 		the encoded string array
	 */
	public static String encodeStringArrayList(ArrayList<String> strList) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		boolean firstValue = true;
		for ( String strVal : strList ) {
			if ( firstValue )
				firstValue = false;
			else
				sb.append(", ");
			sb.append("\"");
			sb.append(strVal);
			sb.append("\"");
		}
		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * Decodes a (somewhat-JSON-like) encoded string array, like that 
	 * produced by {@link #encodeStringArrayList(ArrayList)}, into an 
	 * ArrayList of strings.  Each string must be enclosed in double 
	 * quotes; escaped characters within a string are not recognized 
	 * or modified.  Strings must be separated by commas.  Whitespace 
	 * around the comma is allowed.
	 * 
	 * @param arrayStr
	 * 		the encoded string array
	 * @return
	 * 		the decoded ArrayList of strings; never null, but may
	 * 		be empty (if the encoded string array contains no strings)
	 * @throws IllegalArgumentException
	 * 		if arrayStr does not start with '[', does not end with ']', 
	 * 		or contains strings not enclosed within double quotes.
	 */
	public static ArrayList<String> decodeStringArrayList(String arrayStr) 
									throws IllegalArgumentException {
		if ( ! ( arrayStr.startsWith("[") && arrayStr.endsWith("]") ) )
			throw new IllegalArgumentException(
					"Encoded string array not enclosed in brackets");
		// Locate the double quote at the start of the first string 
		// and at the end of the last string
		int firstIndex = arrayStr.indexOf("\"");
		int lastIndex = arrayStr.lastIndexOf("\"");
		// Check for values not in double quotes within the brackets
		if ( (firstIndex < 1) || (lastIndex == firstIndex) ||
			 ( (firstIndex > 1) && 
				! arrayStr.substring(1, firstIndex).trim().isEmpty() ) ||
			 ( (lastIndex > 1) && (lastIndex < arrayStr.length() - 2) && 
				! arrayStr.substring(lastIndex+1, 
								arrayStr.length()-1).trim().isEmpty() ) ) {
			// Check for an empty array
			if ( (firstIndex < 1) && 
				 arrayStr.substring(1, arrayStr.length()-1).trim().isEmpty() )
				return new ArrayList<String>(0);
			throw new IllegalArgumentException("Strings in encoded " +
					"string array are not enclosed in double quotes");
		}
		// Split up the substring between the first and last double quote
		String[] pieces = arrayStr.substring(firstIndex+1, lastIndex)
								  .split("\"\\s*,\\s*\"", -1);
		// Return an ArrayList<String> generated from the array of Strings
		return new ArrayList<String>(Arrays.asList(pieces));
	}

	/**
	 * Encodes an ArrayList of HashSets of Integers suitable for decoding 
	 * with {@link #decodeSetsArrayList(String)}
	 * 
	 * @param setsList
	 * 		list of sets of integer values to encode
	 * @return
	 * 		the encoded list of sets of integer values
	 */
	public static String encodeSetsArrayList(ArrayList<HashSet<Integer>> setsList) 
											throws IllegalArgumentException {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		boolean firstValue = true;
		for ( HashSet<Integer> setVal : setsList ) {
			if ( firstValue )
				firstValue = false;
			else
				sb.append(", ");
			// Go to the trouble of sorting the list before creating the string
			// in order to simplify human reading and detecting real differences
			sb.append(encodeIntegerArrayList(
					new ArrayList<Integer>(new TreeSet<Integer>(setVal))));
		}
		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * Decodes a (somewhat-JSON-like) encoded array of sets of integers, 
	 * like that produced by {@link #encodeSetsArrayList(ArrayList)}, 
	 * into an ArrayList of HashSets of Integers.  Each set must be 
	 * comma-separated integer values enclosed in brackets (like that 
	 * produced {@link #encodeIntegerArrayList(ArrayList)}, and each set 
	 * must be by separated by a comma.  Whitespace around brackets and 
	 * commas is allowed.
	 * 
	 * @param arrayStr
	 * 		the encoded sets of integers array
	 * @return
	 * 		the decoded ArrayList of HashSets of Integers; never null, 
	 * 		but may be empty (if the encoded array contains no sets)
	 * @throws IllegalArgumentException
	 * 		if arrayStr does not start with '[', does not end with ']', 
	 * 		or contains sets not enclosed within '[' and ']'.
	 */
	public static ArrayList<HashSet<Integer>> decodeSetsArrayList(String arrayStr) 
											throws IllegalArgumentException {
		if ( ! ( arrayStr.startsWith("[") && arrayStr.endsWith("]") ) )
			throw new IllegalArgumentException(
					"Encoded string array not enclosed in brackets");
		// Locate the opening bracket of the first set
		int firstIndex = arrayStr.indexOf("[", 1);
		// Locate the closing bracket of the last set
		int lastIndex = arrayStr.lastIndexOf("]", arrayStr.length() - 2);
		if ( (firstIndex < 0) || (lastIndex < 0) ) {
			if ( (firstIndex < 0) && (lastIndex < 0) &&
				 arrayStr.substring(1, arrayStr.length() - 1).trim().isEmpty() ) {
				// no sets; return an empty list
				return new ArrayList<HashSet<Integer>>(0);
			}
			// Not empty, but 
			throw new IllegalArgumentException(
					"Sets in encoded sets array not enclosed in brackets");
		}
		// Split the string into each of the sets
		String[] pieces = arrayStr.substring(firstIndex+1, lastIndex)
								  .split("\\]\\s*,\\s*\\[", -1);
		// Create the list to return
		ArrayList<HashSet<Integer>> setsList = 
				new ArrayList<HashSet<Integer>>(pieces.length);
		// Convert each of the set strings and add to the list
		for ( String setStr : pieces ) {
			setsList.add(new HashSet<Integer>(
					decodeIntegerArrayList("[" + setStr + "]")));
		}
		return setsList;
	}

	/**
	 * Returns the basename of a filename.  Does this by returning only the
	 * portion of the string after the last slash or backslash character 
	 * (either one if both present).
	 * 
	 * If null is given, or if the name ends in a slash or backslash, an empty 
	 * string is returned.  Whitespace is trimmed from the returned name.
	 */
	public static String baseName(String filename) {
		if ( filename == null )
			return "";

		String basename = filename;
		int idx = basename.lastIndexOf('/');
		if ( idx >= 0 ) {
			idx++;
			if ( basename.length() == idx )
				return "";
			else
				basename = basename.substring(idx);
		}
		idx = basename.lastIndexOf('\\');
		if ( idx >= 0 ) {
			idx++;
			if ( basename.length() == idx )
				return "";
			else
				basename = basename.substring(idx);
		}
		return basename.trim();
	}

	/**
	 * Determines if two Doubles are close to the same value.
	 * The absolute of the average value, absAver, and the 
	 * absolute value in the difference in values, absDiff,
	 * of first and second are determined.
	 *  
	 * The difference between is considered negligible if: 
	 *     absDiff < absAver * rtol + atol 
	 * 
	 * @param first 
	 * 		value to compare
	 * @param second 
	 * 		value to compare
	 * @param rtol
	 * 		relative tolerance of the difference
	 * @param atol
	 * 		absolute tolerance of the difference
	 * @return 
	 * 		true is first and second are both NaN, both Infinite
	 * 		(regardless of whether positive or negative), or 
	 * 		have values whose difference is "negligible".
	 */
	public static boolean closeTo(Double first, Double second, 
									double rtol, double atol) {

		// NaN (only) matches NaN
		if ( first.isNaN() ) {
			return second.isNaN();
		}
		if ( second.isNaN() ) {
			return false;
		}

		// Positive or negative infinity (only) matches 
		// positive or negative infinity
		if ( first.isInfinite() ) {
			return second.isInfinite();
		}
		if ( second.isInfinite() ) {
			return false;
		}

		// Check if values are close
		double absDiff = Math.abs(first - second);
		double absAver = Math.abs((first + second) * 0.5);
		return ( absDiff < absAver * rtol + atol );
	}

}