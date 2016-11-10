/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Karl Smith
 */
public class DashboardServerUtils {

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
	public static final DashDataType VESSEL_TYPE = new DashDataType(DashboardUtils.VESSEL_TYPE);
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

	/** 
	 * Value of userRealName to use to skip sending the email request in 
	 * {@link ArchiveFilesBundler#sendOrigFilesBundle(String, String, String, String)} 
	 */
	public static final String NOMAIL_USER_REAL_NAME = "nobody";

	/** 
	 * Value of userEmail to use to skip sending the email request in 
	 * {@link ArchiveFilesBundler#sendOrigFilesBundle(String, String, String, String)} 
	 */
	public static final String NOMAIL_USER_EMAIL = "nobody@nowhere";

	/**
	 * NODC codes (all upper-case) for Moorings and Fixed Buoys 
	 */
	private static final HashSet<String> FIXED_PLATFORM_NODC_CODES = 
			new HashSet<String>(Arrays.asList("067F", "147F", "187F", "247F", 
					"267F", "297F", "3119", "3164", "317F", "33GO", "33TT", 
					"357F", "48MB", "497F", "747F", "767F", "907F", "GH7F"));

	/**
	 * NODC codes (all upper-case) for Drifting Buoys 
	 */
	private static final HashSet<String> DRIFTING_BUOY_NODC_CODES = 
			new HashSet<String>(Arrays.asList("09DB", "18DZ", "35DR", "49DZ", 
					"61DB", "74DZ", "91DB", "99DB"));

	/** Pattern for getKeyForName */
	private static final Pattern stripPattern = Pattern.compile("[^a-z0-9]+");

	/**
	 * Computes a key for the given name which is case-insensitive and ignores 
	 * non-alphanumeric characters.  The value returned is equivalent to 
	 * <pre>name.toLowerCase().replaceAll("[^a-z0-9]+", "")</pre>
	 * 
	 * @param name
	 * 		name to use
	 * @return
	 * 		key for the name
	 */
	public static String getKeyForName(String name) {
		return stripPattern.matcher(name.toLowerCase()).replaceAll("");
	}

	// Pattern for checking for invalid characters in the expocode
	private static final Pattern invalidExpocodePattern = 
			Pattern.compile("[^" + DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]");

	/**
	 * Checks and standardized a given expocode.
	 * 
	 * @param expocode
	 * 		expocode to check
	 * @return
	 * 		standardized (uppercase) expocode
	 * @throws IllegalArgumentException
	 * 		if the expocode is unreasonable
	 * 		(invalid characters, too short, too long)
	 */
	public static String checkExpocode(String expocode) throws IllegalArgumentException {
		if ( expocode == null )
			throw new IllegalArgumentException("Expocode not given");
		// Do some automatic clean-up
		String upperExpo = expocode.trim().toUpperCase();
		// Make sure it is the proper length
		if ( (upperExpo.length() < DashboardUtils.MIN_EXPOCODE_LENGTH) || 
			 (upperExpo.length() > DashboardUtils.MAX_EXPOCODE_LENGTH) )
			throw new IllegalArgumentException(
					"Invalid Expocode length");
		// Make sure there are no invalid characters
		Matcher mat = invalidExpocodePattern.matcher(upperExpo);
		if ( mat.find() )
			throw new IllegalArgumentException(
					"Invalid characters in the Expocode");
		return upperExpo;
	}

	/**
	 * Checks the validity of the given "NODC code" (first four characters of a standard expocode).
	 * This does not actually check that the value is listed in the NODC registry of ships.
	 * 
	 * @param nodccode
	 * 		expocode start to check
	 * @return
	 * 		false if nodccode is not exactly four characters from 
	 * 		{@link DashboardUtils#VALID_EXPOCODE_CHARACTERS};
	 * 		otherwise true
	 */
	public static boolean isLikeNODCCode(String nodccode) {
		if ( (nodccode == null) || (nodccode.length() != 4) )
			return false;
		Matcher mat = invalidExpocodePattern.matcher(nodccode);
		if ( mat.find() )
			return false;
		return true;
	}

	/**
	 * Guesses the vessel type from the vessel name or the expocode.
	 * If the vessel name or NODC code from the expocode is that of
	 * a mooring or drifting buoy, the that type is returned; 
	 * otherwise it is assumed to be a ship.
	 * 
	 * @param expocode
	 * 		expocode of the dataset
	 * @param vesselName
	 * 		vessel name for the dataset
	 * @return
	 * 		one of "Mooring", "Drifting Buoy", or "Ship"
	 */
	public static String guessVesselType(String expocode, String vesselName) {
		if ( "Mooring".equalsIgnoreCase(vesselName) )
			return "Mooring";
		if ( "Drifting Buoy".equalsIgnoreCase(vesselName) )
			return "Drifting Buoy";
		if ( "Bouy".equalsIgnoreCase(vesselName) )
			return "Mooring";

		String nodc = expocode.substring(0, 4).toUpperCase();
		if ( FIXED_PLATFORM_NODC_CODES.contains(nodc) )
			return "Mooring";
		if ( DRIFTING_BUOY_NODC_CODES.contains(nodc) )
			return "Drifting Buoy";

		return "Ship";
	}

	// Unit arrays for static types in this class
	public static final ArrayList<String> SALINITY_UNITS = 
			new ArrayList<String>(Arrays.asList("PSU"));

	public static final ArrayList<String> TEMPERATURE_UNITS = 
			new ArrayList<String>(Arrays.asList("degrees C"));

	public static final ArrayList<String> PRESSURE_UNITS = 
			new ArrayList<String>(Arrays.asList("hPa", "kPa", "mmHg"));

	public static final ArrayList<String> XCO2_UNITS = 
			new ArrayList<String>(Arrays.asList("umol/mol"));

	public static final ArrayList<String> PCO2_UNITS = 
			new ArrayList<String>(Arrays.asList("uatm"));

	public static final ArrayList<String> FCO2_UNITS = 
			new ArrayList<String>(Arrays.asList("uatm"));

	public static final ArrayList<String> DISTANCE_UNITS = 
			new ArrayList<String>(Arrays.asList("km"));

	// Additional metadata
	public static final DashDataType VERSION = new DashDataType("version", 
			200.0, "version", DashboardUtils.STRING_DATA_CLASS_NAME, 
			"version number with status", null, 
			null, DashboardUtils.NO_UNITS);

	public static final DashDataType ALL_REGION_IDS = new DashDataType("all_region_ids", 
			201.0, "all region IDs", DashboardUtils.STRING_DATA_CLASS_NAME, 
			"Sorted unique region IDs", null, 
			null, DashboardUtils.NO_UNITS);

	public static final DashDataType ENHANCED_DOI = new DashDataType("enhanced_data_doi", 
			202.0, "enhanced-data DOI", DashboardUtils.STRING_DATA_CLASS_NAME, 
			"DOI of enhanced data", null, 
			null, DashboardUtils.NO_UNITS);

	// Additional data provided by the user
	public static final DashDataType SALINITY = new DashDataType("sal", 
			600.0, "salinity", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"salinity", "sea_surface_salinity", 
			DashboardUtils.SALINITY_CATEGORY, SALINITY_UNITS);

	public static final DashDataType TEQU = new DashDataType("Temperature_equi", 
			610.0, "T_equ", 
			DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"equilibrator chamber temperature", null, 
			DashboardUtils.TEMPERATURE_CATEGORY, TEMPERATURE_UNITS);

	public static final DashDataType SST = new DashDataType("temp", 
			611.0, "SST", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"sea surface temperature", "sea_surface_temperature", 
			DashboardUtils.TEMPERATURE_CATEGORY, TEMPERATURE_UNITS);

	public static final DashDataType PEQU = new DashDataType("Pressure_equi", 
			620.0, "P_equ", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"equilibrator chamber pressure", null, 
			DashboardUtils.PRESSURE_CATEGORY, PRESSURE_UNITS);

	public static final DashDataType PATM = new DashDataType("Pressure_atm", 
			621.0, "P_atm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"sea-level air pressure", "air_pressure_at_sea_level", 
			DashboardUtils.PRESSURE_CATEGORY, PRESSURE_UNITS);

	public static final DashDataType XCO2_WATER_TEQU_DRY = new DashDataType("xCO2_water_equi_temp_dry_ppm", 
			630.0, "xCO2_water_Tequ_dry", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water xCO2 dry using equi temp", "mole_fraction_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType XCO2_WATER_SST_DRY = new DashDataType("xCO2_water_sst_dry_ppm", 
			631.0, "xCO2_water_SST_dry", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water xCO2 dry using sst", "mole_fraction_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType PCO2_WATER_TEQU_WET = new DashDataType("pCO2_water_equi_temp", 
			632.0, "pCO2_water_Tequ_wet", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water pCO2 wet using equi temp", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType PCO2_WATER_SST_WET = new DashDataType("pCO2_water_sst_100humidity_uatm", 
			633.0, "pCO2_water_SST_wet", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water pCO2 wet using sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType FCO2_WATER_TEQU_WET = new DashDataType("fCO2_water_equi_uatm", 
			634.0, "fCO2_water_Tequ_wet", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water fCO2 wet using equi temp", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_WATER_SST_WET = new DashDataType("fCO2_water_sst_100humidity_uatm", 
			635.0, "fCO2_water_SST_wet", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water fCO2 wet using sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType WOCE_CO2_WATER = new DashDataType("WOCE_CO2_water", 
			650.0, "WOCE CO2_water", DashboardUtils.CHAR_DATA_CLASS_NAME, 
			"WOCE flag for aqueous CO2", null, 
			DashboardUtils.QUALITY_CATEGORY, DashboardUtils.NO_UNITS);

	/** 
	 * User-provided comment for WOCE_CO2_WATER;
	 * user type only, used for generating WOCE events from user-provided data.
	 */
	public static final DashDataType COMMENT_WOCE_CO2_WATER = new DashDataType("comment_WOCE_CO2_water",
			651.0, "comment WOCE CO2_water", DashboardUtils.STRING_DATA_CLASS_NAME, 
			"comment about WOCE_CO2_water flag", null, 
			null, DashboardUtils.NO_UNITS);

	public static final DashDataType WOCE_CO2_ATM = new DashDataType("WOCE_CO2_atm", 
			652.0, "WOCE_CO2_atm", DashboardUtils.CHAR_DATA_CLASS_NAME, 
			"WOCE flag for atmospheric CO2", null, 
			DashboardUtils.QUALITY_CATEGORY, DashboardUtils.NO_UNITS);

	/** 
	 * User-provided comment for WOCE_CO2_ATM;
	 * user type only, used for generating WOCE events from user-provided data.
	 */
	public static final DashDataType COMMENT_WOCE_CO2_ATM = new DashDataType("comment_WOCE_CO2_atm", 
			653.0, "comment WOCE CO2_atm", DashboardUtils.STRING_DATA_CLASS_NAME, 
			"comment about WOCE_CO2_atm flag", null, 
			null, DashboardUtils.NO_UNITS);

	// Computed or looked-up values
	public static final DashDataType WOA_SALINITY = new DashDataType("woa_sss", 
			700.0, "WOA SSS", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"salinity from World Ocean Atlas", "sea_surface_salinity", 
			DashboardUtils.SALINITY_CATEGORY, SALINITY_UNITS);

	public static final DashDataType NCEP_SLP = new DashDataType("pressure_ncep_slp", 
			701.0, "NCEP SLP", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"sea level air pressure from NCEP/NCAR reanalysis", "air_pressure_at_sea_level", 
			DashboardUtils.PRESSURE_CATEGORY, PRESSURE_UNITS);

	public static final DashDataType REGION_ID = new DashDataType("region_id", 
			702.0, "Region ID", DashboardUtils.CHAR_DATA_CLASS_NAME, "SOCAT region ID", 
			null, DashboardUtils.LOCATION_CATEGORY, DashboardUtils.NO_UNITS);

	public static final DashDataType ETOPO2_DEPTH = new DashDataType("etopo2", 
			705.0, "ETOPO2 depth", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"bathymetry from ETOPO2", "sea_floor_depth", 
			DashboardUtils.BATHYMETRY_CATEGORY, DashboardUtils.DEPTH_UNITS);

	public static final DashDataType GVCO2 = new DashDataType("gvCO2", 
			706.0, "GlobalView CO2", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"GlobalView xCO2", "mole_fraction_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType DIST_TO_LAND = new DashDataType("dist_to_land", 
			707.0, "dist to land", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"distance to major land mass", null, 
			DashboardUtils.LOCATION_CATEGORY, DISTANCE_UNITS);

	public static final DashDataType FCO2_REC = new DashDataType("fCO2_recommended", 
			710.0, "fCO2_rec", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 recommended", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_SOURCE = new DashDataType("fCO2_source", 
			711.0, "fCO2 src", DashboardUtils.INT_DATA_CLASS_NAME, 
			"Algorithm number for recommended fCO2", null, 
			DashboardUtils.IDENTIFIER_CATEGORY, DashboardUtils.NO_UNITS);

	/** mapping of old user data type (enumerated variable) names to new data type names */
	public static final HashMap<String,String> RENAMED_DATA_TYPES;

	/** mapping from old unit names to new unit names */
	public static final HashMap<String,String> RENAMED_UNITS;

	static {
		RENAMED_DATA_TYPES = new HashMap<String,String>();
		RENAMED_DATA_TYPES.put("ATMOSPHERIC_TEMPERATURE", "Temperature_atm");
		RENAMED_DATA_TYPES.put("CRUISE_NAME", "dataset_name");
		RENAMED_DATA_TYPES.put("GROUP_NAME", "organization");
		RENAMED_DATA_TYPES.put("EQUILIBRATOR_PRESSURE", "Pressure_equi");
		RENAMED_DATA_TYPES.put("EQUILIBRATOR_TEMPERATURE", "Temperature_equi");
		RENAMED_DATA_TYPES.put("INVESTIGATOR_NAMES", "investigators");
		RENAMED_DATA_TYPES.put("SEA_LEVEL_PRESSURE", "Pressure_atm");
		RENAMED_DATA_TYPES.put("SEA_SURFACE_TEMPERATURE", "temp");
		RENAMED_DATA_TYPES.put("SECOND_OF_DAY", "sec_of_day");
		RENAMED_DATA_TYPES.put("SHIP_DIRECTION", "ship_dir");
		RENAMED_DATA_TYPES.put("SHIP_NAME", "vessel_name");
		RENAMED_DATA_TYPES.put("TIME", "time_of_day");
		RENAMED_DATA_TYPES.put("TIMESTAMP", "date_time");
		RENAMED_DATA_TYPES.put("WIND_DIRECTION_TRUE", "wind_dir_true");
		RENAMED_DATA_TYPES.put("WIND_DIRECTION_RELATIVE", "wind_dir_rel");
		RENAMED_DATA_TYPES.put("WIND_SPEED_RELATIVE", "wind_speed_rel");
		RENAMED_DATA_TYPES.put("XH2O_EQU", "xH2O_equi");

		RENAMED_UNITS = new HashMap<String,String>();
		RENAMED_UNITS.put("deg.E", "degrees_east");
		RENAMED_UNITS.put("deg.W", "degrees_west");
		RENAMED_UNITS.put("deg.N", "degrees_north");
		RENAMED_UNITS.put("deg.S", "degrees_south");
		RENAMED_UNITS.put("deg.C", "degrees C");
		RENAMED_UNITS.put("deg.clk.N", "degrees");
	}

}
