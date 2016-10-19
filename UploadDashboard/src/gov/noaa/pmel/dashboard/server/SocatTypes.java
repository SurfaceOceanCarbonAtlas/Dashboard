/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * SOCAT standard types required in various classes.
 * 
 * @author Karl Smith
 */
public class SocatTypes {

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
	public static final DashDataType SOCAT_VERSION = new DashDataType("socat_version", 
			200.0, "SOCAT version", DashboardUtils.STRING_DATA_CLASS_NAME, 
			"SOCAT Version number with status", null, 
			null, DashboardUtils.NO_UNITS);

	public static final DashDataType ALL_REGION_IDS = new DashDataType("all_region_ids", 
			201.0, "all Region IDs", DashboardUtils.STRING_DATA_CLASS_NAME, 
			"Sorted unique region IDs", null, 
			null, DashboardUtils.NO_UNITS);

	public static final DashDataType SOCAT_DOI = new DashDataType("socat_doi", 
			202.0, "SOCAT DOI", DashboardUtils.STRING_DATA_CLASS_NAME, 
			"DOI of SOCAT-enhanced data", null, 
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
