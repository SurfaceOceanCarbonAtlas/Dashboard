/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

/**
 * Temporary hack giving the SOCAT standard types.
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

	public static final ArrayList<String> DIRECTION_UNITS = 
			new ArrayList<String>(Arrays.asList("degrees"));

	public static final ArrayList<String> SHIP_SPEED_UNITS = 
			new ArrayList<String>(Arrays.asList("knots", "km/h", "m/s", "mph"));

	public static final ArrayList<String> WIND_SPEED_UNITS = 
			new ArrayList<String>(Arrays.asList("m/s"));

	public static final ArrayList<String> XH2O_UNITS = 
			new ArrayList<String>(Arrays.asList("mmol/mol", "umol/mol"));

	public static final ArrayList<String> DISTANCE_UNITS = 
			new ArrayList<String>(Arrays.asList("km"));

	public static final ArrayList<String> DAYS_UNITS = 
			new ArrayList<String>(Arrays.asList("days"));

	public static final DashDataType SOCAT_VERSION = new DashDataType("SOCAT version",
			"socat_version", DashboardUtils.STRING_DATA_CLASS_NAME, "SOCAT Version number with status", 
			null, null, DashboardUtils.NO_UNITS);

	public static final DashDataType ALL_REGION_IDS = new DashDataType("all Region IDs",
			"all_region_ids", DashboardUtils.STRING_DATA_CLASS_NAME, "Sorted unique region IDs", 
			null, null, DashboardUtils.NO_UNITS);

	public static final DashDataType SOCAT_DOI = new DashDataType("SOCAT DOI",
			"socat_doi", DashboardUtils.STRING_DATA_CLASS_NAME, "DOI of SOCAT-enhanced data", 
			null, null, DashboardUtils.NO_UNITS);

	public static final DashDataType WOCE_CO2_WATER = new DashDataType("WOCE CO2_water",
			"WOCE_CO2_water", DashboardUtils.CHAR_DATA_CLASS_NAME, 
			"WOCE flag for aqueous CO2", null, 
			DashboardUtils.QUALITY_CATEGORY, DashboardUtils.NO_UNITS);

	public static final DashDataType WOCE_CO2_ATM = new DashDataType("WOCE_CO2_atm", 
			"WOCE_CO2_atm", DashboardUtils.CHAR_DATA_CLASS_NAME, 
			"WOCE flag for atmospheric CO2", null, 
			DashboardUtils.QUALITY_CATEGORY, DashboardUtils.NO_UNITS);

	/** 
	 * User-provided comment for WOCE_CO2_WATER;
	 * user type only, used for generating WOCE events from user-provided data.
	 */
	public static final DashDataType COMMENT_WOCE_CO2_WATER = new DashDataType(
			"comment WOCE CO2_water", "comment_WOCE_CO2_water", 
			DashboardUtils.STRING_DATA_CLASS_NAME, "comment about WOCE_CO2_water flag", 
			null, null, DashboardUtils.NO_UNITS);

	/** 
	 * User-provided comment for WOCE_CO2_ATM;
	 * user type only, used for generating WOCE events from user-provided data.
	 */
	public static final DashDataType COMMENT_WOCE_CO2_ATM = new DashDataType(
			"comment WOCE CO2_atm", "comment_WOCE_CO2_atm", 
			DashboardUtils.STRING_DATA_CLASS_NAME, "comment about WOCE_CO2_atm flag", 
			null, null, DashboardUtils.NO_UNITS);

	public static final DashDataType SALINITY = new DashDataType("salinity", 
			"sal", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"salinity", "sea_surface_salinity", 
			DashboardUtils.SALINITY_CATEGORY, SALINITY_UNITS);

	public static final DashDataType WOA_SALINITY = new DashDataType("WOA SSS", 
			"woa_sss", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"salinity from World Ocean Atlas", "sea_surface_salinity", 
			DashboardUtils.SALINITY_CATEGORY, SALINITY_UNITS);

	public static final DashDataType TEQU = new DashDataType("T_equ", 
			"Temperature_equi", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"equilibrator chamber temperature", null, 
			DashboardUtils.TEMPERATURE_CATEGORY, TEMPERATURE_UNITS);

	public static final DashDataType SST = new DashDataType("SST", 
			"temp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"sea surface temperature", "sea_surface_temperature", 
			DashboardUtils.TEMPERATURE_CATEGORY, TEMPERATURE_UNITS);

	public static final DashDataType TATM = new DashDataType("T_atm", 
			"Temperature_atm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"sea-level air temperature", "air_temperature_at_sea_level", 
			DashboardUtils.TEMPERATURE_CATEGORY, TEMPERATURE_UNITS);

	public static final DashDataType PEQU = new DashDataType("P_equ", 
			"Pressure_equi", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"equilibrator chamber pressure", null, 
			DashboardUtils.PRESSURE_CATEGORY, PRESSURE_UNITS);

	public static final DashDataType PATM = new DashDataType("P_atm", 
			"Pressure_atm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"sea-level air pressure", "air_pressure_at_sea_level", 
			DashboardUtils.PRESSURE_CATEGORY, PRESSURE_UNITS);

	public static final DashDataType NCEP_SLP = new DashDataType("NCEP SLP", 
			"pressure_ncep_slp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"sea level air pressure from NCEP/NCAR reanalysis", "air_pressure_at_sea_level", 
			DashboardUtils.PRESSURE_CATEGORY, PRESSURE_UNITS);

	public static final DashDataType XCO2_WATER_TEQU_DRY = new DashDataType("xCO2_water_Tequ_dry", 
			"xCO2_water_equi_temp_dry_ppm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water xCO2 dry using equi temp", "mole_fraction_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType XCO2_WATER_SST_DRY = new DashDataType("xCO2_water_SST_dry", 
			"xCO2_water_sst_dry_ppm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water xCO2 dry using sst", "mole_fraction_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType XCO2_WATER_TEQU_WET = new DashDataType("xCO2_water_Tequ_wet", 
			"xCO2_water_equi_temp_wet_ppm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water xCO2 wet using equi temp", "mole_fraction_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType XCO2_WATER_SST_WET = new DashDataType("xCO2_water_SST_wet", 
			"xCO2_water_sst_wet_ppm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water xCO2 wet using sst", "mole_fraction_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType PCO2_WATER_TEQU_WET = new DashDataType("pCO2_water_Tequ_wet", 
			"pCO2_water_equi_temp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water pCO2 wet using equi temp", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType PCO2_WATER_SST_WET = new DashDataType("pCO2_water_SST_wet", 
			"pCO2_water_sst_100humidity_uatm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water pCO2 wet using sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType FCO2_WATER_TEQU_WET = new DashDataType("fCO2_water_Tequ_wet", 
			"fCO2_water_equi_uatm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water fCO2 wet using equi temp", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_WATER_SST_WET = new DashDataType("fCO2_water_SST_wet", 
			"fCO2_water_sst_100humidity_uatm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water fCO2 wet using sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType XCO2_ATM_DRY_ACTUAL = new DashDataType("xCO2_atm_dry_actual", 
			"xCO2_atm_dry_actual", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"actual air xCO2 dry", "mole_fraction_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType XCO2_ATM_DRY_INTERP = new DashDataType("xCO2_atm_dry_interp", 
			"xCO2_atm_dry_interp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"interpolated air xCO2 dry", "mole_fraction_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType PCO2_ATM_WET_ACTUAL = new DashDataType("pCO2_atm_wet_actual", 
			"pCO2_atm_wet_actual", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"actual air pCO2 wet", "surface_partial_pressure_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType PCO2_ATM_WET_INTERP = new DashDataType("pCO2_atm_wet_interp", 
			"pCO2_atm_wet_interp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"interpolated air pCO2 wet", "surface_partial_pressure_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType FCO2_ATM_WET_ACTUAL = new DashDataType("fCO2_atm_wet_actual", 
			"fCO2_atm_wet_actual", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"actual air fCO2 wet", "surface_partial_pressure_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_ATM_WET_INTERP = new DashDataType("fCO2_atm_wet_interp", 
			"fCO2_atm_wet_interp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"interpolated air fCO2 wet", "surface_partial_pressure_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType DELTA_XCO2 = new DashDataType("delta_xCO2", 
			"delta_xCO2", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water xCO2 minus atmospheric xCO2", null, 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType DELTA_PCO2 = new DashDataType("delta_pCO2", 
			"delta_pCO2", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water pCO2 minus atmospheric pCO2", null, 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType DELTA_FCO2 = new DashDataType("delta_fCO2", 
			"delta_fCO2", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water fCO2 minus atmospheric fCO2", null, 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType XH2O_EQU = new DashDataType("xH2O_equi", 
			"xH2O_equi", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"xH2O in equil air sample", "mole_fraction_of_water_in_air", 
			DashboardUtils.WATER_VAPOR_CATEGORY, XH2O_UNITS);

	public static final DashDataType RELATIVE_HUMIDITY = new DashDataType("rel humidity", 
			"relative_humidity", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"relative humidity", "relative_humidity", 
			DashboardUtils.WATER_VAPOR_CATEGORY, DashboardUtils.NO_UNITS);

	public static final DashDataType SPECIFIC_HUMIDITY = new DashDataType("spec humidity", 
			"specific_humidity", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"specific humidity", "specific_humidity", 
			DashboardUtils.WATER_VAPOR_CATEGORY, DashboardUtils.NO_UNITS);

	public static final DashDataType SHIP_SPEED = new DashDataType("ship speed",
			"ship_speed", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"measured ship speed", "platform_speed_wrt_ground", 
			DashboardUtils.PLATFORM_CATEGORY, SHIP_SPEED_UNITS);

	public static final DashDataType SHIP_DIRECTION = new DashDataType("ship dir",
			"ship_dir", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"ship direction", "platform_course", 
			DashboardUtils.PLATFORM_CATEGORY, DIRECTION_UNITS);

	public static final DashDataType WIND_SPEED_TRUE = new DashDataType("true wind speed", 
			"wind_speed_true", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"true wind speed", "wind_speed", 
			DashboardUtils.WIND_CATEGORY, WIND_SPEED_UNITS);

	public static final DashDataType WIND_SPEED_RELATIVE = new DashDataType("rel wind speed", 
			"wind_speed_rel", DashboardUtils.DOUBLE_DATA_CLASS_NAME, "relative wind speed", 
			"wind_speed", DashboardUtils.WIND_CATEGORY, WIND_SPEED_UNITS);

	public static final DashDataType WIND_DIRECTION_TRUE = new DashDataType("true wind dir", 
			"wind_dir_true", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"true wind direction", "wind_from_direction", 
			DashboardUtils.WIND_CATEGORY, DIRECTION_UNITS);

	public static final DashDataType WIND_DIRECTION_RELATIVE = new DashDataType("rel wind dir", 
			"wind_dir_rel", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"relative wind direction", "wind_from_direction", 
			DashboardUtils.WIND_CATEGORY, DIRECTION_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_TEQU = new DashDataType("fCO2 from xCO2_water_Tequ_dry, Pequ, sal", 
			"fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_equi_temp_dry_ppm, Pressure_equi, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_SST = new DashDataType("fCO2 from xCO2_water_SST_dry, Pequ, sal", 
			"fCO2_insitu_from_xCO2_water_sst_dry_ppm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_sst_dry_ppm, Pressure_equi, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_PCO2_TEQU = new DashDataType("fCO2 from pCO2_water_Tequ_wet, Pequ, sal", 
			"fCO2_from_pCO2_water_water_equi_temp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from pCO2_water_equi_temp, Pressure_equi, sal","surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_PCO2_SST = new DashDataType("fCO2 from pCO2_water_SST_dry, Pequ, sal", 
			"fCO2_from_pCO2_water_sst_100humidity_uatm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from pCO2_water_sst_100humidity_uatm, Pressure_equi, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_FCO2_TEQU = new DashDataType("fCO2 from fCO2_water_Tequ_wet, Pequ, sal", 
			"fCO2_insitu_from_fCO2_water_equi_uatm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from fCO2_water_equi_temp, Pressure_equi, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_FCO2_SST = new DashDataType("fCO2 from fCO2_water_SST_dry, Pequ, sal", 
			"fCO2_insitu_from_fCO2_water_sst_100humidty_uatm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from fCO2_water_sst_100humidity_uatm, Pressure_equi, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_PCO2_TEQU_NCEP = new DashDataType("fCO2 from pCO2_water_Tequ_wet, NCEP SLP, sal", 
			"fCO2_from_pCO2_water_water_equi_temp_ncep", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from pCO2_water_equi_temp, NCEP SLP, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_PCO2_SST_NCEP = new DashDataType("fCO2 from pCO2_water_SST_wet, NCEP SLP, sal", 
			"fCO2_from_pCO2_water_sst_100humidity_uatm_ncep", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from pCO2_water_sst_100humidity_uatm, NCEP SLP, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_TEQU_WOA = new DashDataType("fCO2 from xCO2_water_Tequ_dry, Pequ, WOA SSS", 
			"fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_woa", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_equi_temp_dry_ppm, Pressure_equi, WOA SSS", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_SST_WOA = new DashDataType("fCO2 from xCO2_water_SST_dry, Pequ, WOA SSS",
			"fCO2_insitu_from_xCO2_water_sst_dry_ppm_woa", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_sst_dry_ppm, Pressure_equi, WOA SSS", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_TEQU_NCEP = new DashDataType("fCO2 from xCO2_water_Tequ_dry, NCEP SLP, sal", 
			"fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_equi_temp_dry_ppm, NCEP SLP, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_SST_NCEP = new DashDataType("fCO2 from xCO2_water_SST_dry, NCEP SLP, sal", 
			"fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_sst_dry_ppm, NCEP SLP, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FOC2_FROM_XCO2_TEQU_NCEP_WOA = new DashDataType("fCO2 from xCO2_water_Tequ_dry, NCEP SLP, WOA SSS", 
			"fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep_woa", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_equi_temp_dry_ppm, NCEP SLP, WOA SSS", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_SST_NCEP_WOA = new DashDataType("fCO2 from xCO2_water_SST_dry, NCEP SLP, WOA SSS",
			"fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep_woa", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_sst_dry_ppm, NCEP SLP, WOA SSS", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_REC = new DashDataType("fCO2_rec", 
			"fCO2_recommended", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 recommended", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_SOURCE = new DashDataType("fCO2 src", 
			"fCO2_source", DashboardUtils.INT_DATA_CLASS_NAME, 
			"Algorithm number for recommended fCO2", null, 
			DashboardUtils.IDENTIFIER_CATEGORY, DashboardUtils.NO_UNITS);

	public static final DashDataType REGION_ID = new DashDataType("Region ID",
			"region_id", DashboardUtils.CHAR_DATA_CLASS_NAME, "SOCAT region ID", 
			null, DashboardUtils.LOCATION_CATEGORY, DashboardUtils.NO_UNITS);

	public static final DashDataType DELTA_TEMP = new DashDataType("delta_temp", 
			"delta_temp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"Equilibrator Temp - SST", null, 
			DashboardUtils.TEMPERATURE_CATEGORY, TEMPERATURE_UNITS);

	public static final DashDataType CALC_SPEED = new DashDataType("calc ship speed",
			"calc_speed", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"calculated ship speed", "platform_speed_wrt_ground", 
			DashboardUtils.PLATFORM_CATEGORY, SHIP_SPEED_UNITS);

	public static final DashDataType ETOPO2_DEPTH = new DashDataType("ETOPO2 depth",
			"etopo2", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"bathymetry from ETOPO2", "sea_floor_depth", 
			DashboardUtils.BATHYMETRY_CATEGORY, DashboardUtils.DEPTH_UNITS);

	public static final DashDataType GVCO2 = new DashDataType("GlobalView CO2", 
			"gvCO2", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"GlobalView xCO2", "mole_fraction_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType DIST_TO_LAND = new DashDataType("dist to land",
			"dist_to_land", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"distance to major land mass", null, 
			DashboardUtils.LOCATION_CATEGORY, DISTANCE_UNITS);

	public static final DashDataType DAY_OF_YEAR = new DashDataType("day of year",
			"day_of_year", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"day of the year", null, DashboardUtils.TIME_CATEGORY, DAYS_UNITS);

	/** mapping of old user data type (enumerated variable) names to new data type names */
	public static final HashMap<String,String> RENAMED_DATA_TYPES;

	/** mapping from old unit names to new unit names */
	public static final HashMap<String,String> RENAMED_UNITS;

	/** Known types for SOCAT dashboard users */
	public static final KnownDataTypes KNOWN_SOCAT_USER_TYPES;

	/** Known SOCAT metadata types for files */
	public static final KnownDataTypes KNOWN_SOCAT_METADATA_FILE_TYPES;

	/** Known SOCAT data types for files */
	public static final KnownDataTypes KNOWN_SOCAT_DATA_FILE_TYPES;

	static {
		RENAMED_DATA_TYPES = new HashMap<String,String>();
		RENAMED_DATA_TYPES.put("ATMOSPHERIC_TEMPERATURE", TATM.getVarName());
		RENAMED_DATA_TYPES.put("CRUISE_NAME", DashboardServerUtils.DATASET_NAME.getVarName());
		RENAMED_DATA_TYPES.put("GROUP_NAME", DashboardServerUtils.ORGANIZATION_NAME.getVarName());
		RENAMED_DATA_TYPES.put("EQUILIBRATOR_PRESSURE", PEQU.getVarName());
		RENAMED_DATA_TYPES.put("EQUILIBRATOR_TEMPERATURE", TEQU.getVarName());
		RENAMED_DATA_TYPES.put("INVESTIGATOR_NAMES", DashboardServerUtils.INVESTIGATOR_NAMES.getVarName());
		RENAMED_DATA_TYPES.put("SEA_LEVEL_PRESSURE", PATM.getVarName());
		RENAMED_DATA_TYPES.put("SEA_SURFACE_TEMPERATURE", SST.getVarName());
		RENAMED_DATA_TYPES.put("SECOND_OF_DAY", DashboardServerUtils.SECOND_OF_DAY.getVarName());
		RENAMED_DATA_TYPES.put("SHIP_DIRECTION", SHIP_DIRECTION.getVarName());
		RENAMED_DATA_TYPES.put("SHIP_NAME", DashboardServerUtils.VESSEL_NAME.getVarName());
		RENAMED_DATA_TYPES.put("TIME", DashboardServerUtils.TIME_OF_DAY.getVarName());
		RENAMED_DATA_TYPES.put("TIMESTAMP", DashboardServerUtils.TIMESTAMP.getVarName());
		RENAMED_DATA_TYPES.put("WIND_DIRECTION_TRUE", WIND_DIRECTION_TRUE.getVarName());
		RENAMED_DATA_TYPES.put("WIND_DIRECTION_RELATIVE", WIND_DIRECTION_RELATIVE.getVarName());
		RENAMED_DATA_TYPES.put("WIND_SPEED_RELATIVE", WIND_SPEED_RELATIVE.getVarName());
		RENAMED_DATA_TYPES.put("XH2O_EQU", XH2O_EQU.getVarName());

		RENAMED_UNITS = new HashMap<String,String>();
		RENAMED_UNITS.put("deg.E", "degrees_east");
		RENAMED_UNITS.put("deg.W", "degrees_west");
		RENAMED_UNITS.put("deg.N", "degrees_north");
		RENAMED_UNITS.put("deg.S", "degrees_south");
		RENAMED_UNITS.put("deg.C", "degrees C");
		RENAMED_UNITS.put("deg.clk.N", "degrees");

		KNOWN_SOCAT_USER_TYPES = new KnownDataTypes();
		KNOWN_SOCAT_USER_TYPES.addStandardTypesForUsers();
		Properties typeProps = new Properties();
		typeProps.setProperty(SALINITY.getVarName(), SALINITY.toPropertyValue());
		typeProps.setProperty(TEQU.getVarName(), TEQU.toPropertyValue());
		typeProps.setProperty(SST.getVarName(), SST.toPropertyValue());
		typeProps.setProperty(TATM.getVarName(), TATM.toPropertyValue());
		typeProps.setProperty(PEQU.getVarName(), PEQU.toPropertyValue());
		typeProps.setProperty(PATM.getVarName(), PATM.toPropertyValue());
		typeProps.setProperty(XCO2_WATER_TEQU_DRY.getVarName(), XCO2_WATER_TEQU_DRY.toPropertyValue());
		typeProps.setProperty(XCO2_WATER_SST_DRY.getVarName(), XCO2_WATER_SST_DRY.toPropertyValue());
		typeProps.setProperty(XCO2_WATER_TEQU_WET.getVarName(), XCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(XCO2_WATER_SST_WET.getVarName(), XCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(PCO2_WATER_TEQU_WET.getVarName(), PCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(PCO2_WATER_SST_WET.getVarName(), PCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(FCO2_WATER_TEQU_WET.getVarName(), FCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(FCO2_WATER_SST_WET.getVarName(), FCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(XCO2_ATM_DRY_ACTUAL.getVarName(), XCO2_ATM_DRY_ACTUAL.toPropertyValue());
		typeProps.setProperty(XCO2_ATM_DRY_INTERP.getVarName(), XCO2_ATM_DRY_INTERP.toPropertyValue());
		typeProps.setProperty(PCO2_ATM_WET_ACTUAL.getVarName(), PCO2_ATM_WET_ACTUAL.toPropertyValue());
		typeProps.setProperty(PCO2_ATM_WET_INTERP.getVarName(), PCO2_ATM_WET_INTERP.toPropertyValue());
		typeProps.setProperty(FCO2_ATM_WET_ACTUAL.getVarName(), FCO2_ATM_WET_ACTUAL.toPropertyValue());
		typeProps.setProperty(FCO2_ATM_WET_INTERP.getVarName(), FCO2_ATM_WET_INTERP.toPropertyValue());
		typeProps.setProperty(DELTA_XCO2.getVarName(), DELTA_XCO2.toPropertyValue());
		typeProps.setProperty(DELTA_PCO2.getVarName(), DELTA_PCO2.toPropertyValue());
		typeProps.setProperty(DELTA_FCO2.getVarName(), DELTA_FCO2.toPropertyValue());
		typeProps.setProperty(XH2O_EQU.getVarName(), XH2O_EQU.toPropertyValue());
		typeProps.setProperty(RELATIVE_HUMIDITY.getVarName(), RELATIVE_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SPECIFIC_HUMIDITY.getVarName(), SPECIFIC_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SHIP_SPEED.getVarName(), SHIP_SPEED.toPropertyValue());
		typeProps.setProperty(SHIP_DIRECTION.getVarName(), SHIP_DIRECTION.toPropertyValue());
		typeProps.setProperty(WIND_SPEED_TRUE.getVarName(), WIND_SPEED_TRUE.toPropertyValue());
		typeProps.setProperty(WIND_SPEED_RELATIVE.getVarName(), WIND_SPEED_RELATIVE.toPropertyValue());
		typeProps.setProperty(WIND_DIRECTION_TRUE.getVarName(), WIND_DIRECTION_TRUE.toPropertyValue());
		typeProps.setProperty(WIND_DIRECTION_RELATIVE.getVarName(), WIND_DIRECTION_RELATIVE.toPropertyValue());
		typeProps.setProperty(WOCE_CO2_WATER.getVarName(), WOCE_CO2_WATER.toPropertyValue());
		typeProps.setProperty(WOCE_CO2_ATM.getVarName(), WOCE_CO2_ATM.toPropertyValue());
		typeProps.setProperty(COMMENT_WOCE_CO2_WATER.getVarName(), COMMENT_WOCE_CO2_WATER.toPropertyValue());
		typeProps.setProperty(COMMENT_WOCE_CO2_ATM.getVarName(), COMMENT_WOCE_CO2_ATM.toPropertyValue());
		KNOWN_SOCAT_USER_TYPES.addTypesFromProperties(typeProps);

		KNOWN_SOCAT_METADATA_FILE_TYPES = new KnownDataTypes();
		KNOWN_SOCAT_METADATA_FILE_TYPES.addStandardTypesForMetadataFiles();
		typeProps = new Properties();
		typeProps.setProperty(SOCAT_VERSION.getVarName(), SOCAT_VERSION.toPropertyValue());
		typeProps.setProperty(ALL_REGION_IDS.getVarName(), ALL_REGION_IDS.toPropertyValue());
		typeProps.setProperty(SOCAT_DOI.getVarName(), SOCAT_DOI.toPropertyValue());
		KNOWN_SOCAT_METADATA_FILE_TYPES.addTypesFromProperties(typeProps);

		KNOWN_SOCAT_DATA_FILE_TYPES = new KnownDataTypes();
		KNOWN_SOCAT_DATA_FILE_TYPES.addStandardTypesForDataFiles();
		typeProps = new Properties();
		typeProps.setProperty(SALINITY.getVarName(), SALINITY.toPropertyValue());
		typeProps.setProperty(TEQU.getVarName(), TEQU.toPropertyValue());
		typeProps.setProperty(SST.getVarName(), SST.toPropertyValue());
		typeProps.setProperty(TATM.getVarName(), TATM.toPropertyValue());
		typeProps.setProperty(PEQU.getVarName(), PEQU.toPropertyValue());
		typeProps.setProperty(PATM.getVarName(), PATM.toPropertyValue());
		typeProps.setProperty(XCO2_WATER_TEQU_DRY.getVarName(), XCO2_WATER_TEQU_DRY.toPropertyValue());
		typeProps.setProperty(XCO2_WATER_SST_DRY.getVarName(), XCO2_WATER_SST_DRY.toPropertyValue());
		typeProps.setProperty(XCO2_WATER_TEQU_WET.getVarName(), XCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(XCO2_WATER_SST_WET.getVarName(), XCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(PCO2_WATER_TEQU_WET.getVarName(), PCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(PCO2_WATER_SST_WET.getVarName(), PCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(FCO2_WATER_TEQU_WET.getVarName(), FCO2_WATER_TEQU_WET.toPropertyValue());
		typeProps.setProperty(FCO2_WATER_SST_WET.getVarName(), FCO2_WATER_SST_WET.toPropertyValue());
		typeProps.setProperty(XCO2_ATM_DRY_ACTUAL.getVarName(), XCO2_ATM_DRY_ACTUAL.toPropertyValue());
		typeProps.setProperty(XCO2_ATM_DRY_INTERP.getVarName(), XCO2_ATM_DRY_INTERP.toPropertyValue());
		typeProps.setProperty(PCO2_ATM_WET_ACTUAL.getVarName(), PCO2_ATM_WET_ACTUAL.toPropertyValue());
		typeProps.setProperty(PCO2_ATM_WET_INTERP.getVarName(), PCO2_ATM_WET_INTERP.toPropertyValue());
		typeProps.setProperty(FCO2_ATM_WET_ACTUAL.getVarName(), FCO2_ATM_WET_ACTUAL.toPropertyValue());
		typeProps.setProperty(FCO2_ATM_WET_INTERP.getVarName(), FCO2_ATM_WET_INTERP.toPropertyValue());
		typeProps.setProperty(DELTA_XCO2.getVarName(), DELTA_XCO2.toPropertyValue());
		typeProps.setProperty(DELTA_PCO2.getVarName(), DELTA_PCO2.toPropertyValue());
		typeProps.setProperty(DELTA_FCO2.getVarName(), DELTA_FCO2.toPropertyValue());
		typeProps.setProperty(XH2O_EQU.getVarName(), XH2O_EQU.toPropertyValue());
		typeProps.setProperty(RELATIVE_HUMIDITY.getVarName(), RELATIVE_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SPECIFIC_HUMIDITY.getVarName(), SPECIFIC_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SHIP_SPEED.getVarName(), SHIP_SPEED.toPropertyValue());
		typeProps.setProperty(SHIP_DIRECTION.getVarName(), SHIP_DIRECTION.toPropertyValue());
		typeProps.setProperty(WIND_SPEED_TRUE.getVarName(), WIND_SPEED_TRUE.toPropertyValue());
		typeProps.setProperty(WIND_SPEED_RELATIVE.getVarName(), WIND_SPEED_RELATIVE.toPropertyValue());
		typeProps.setProperty(WIND_DIRECTION_TRUE.getVarName(), WIND_DIRECTION_TRUE.toPropertyValue());
		typeProps.setProperty(WIND_DIRECTION_RELATIVE.getVarName(), WIND_DIRECTION_RELATIVE.toPropertyValue());
		typeProps.setProperty(WOCE_CO2_WATER.getVarName(), WOCE_CO2_WATER.toPropertyValue());
		typeProps.setProperty(WOCE_CO2_ATM.getVarName(), WOCE_CO2_ATM.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_XCO2_TEQU.getVarName(), FCO2_FROM_XCO2_TEQU.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_XCO2_SST.getVarName(), FCO2_FROM_XCO2_SST.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_PCO2_TEQU.getVarName(), FCO2_FROM_PCO2_TEQU.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_PCO2_SST.getVarName(), FCO2_FROM_PCO2_SST.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_FCO2_TEQU.getVarName(), FCO2_FROM_FCO2_TEQU.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_FCO2_SST.getVarName(), FCO2_FROM_FCO2_SST.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_PCO2_TEQU_NCEP.getVarName(), FCO2_FROM_PCO2_TEQU_NCEP.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_PCO2_SST_NCEP.getVarName(), FCO2_FROM_PCO2_SST_NCEP.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_XCO2_TEQU_WOA.getVarName(), FCO2_FROM_XCO2_TEQU_WOA.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_XCO2_SST_WOA.getVarName(), FCO2_FROM_XCO2_SST_WOA.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_XCO2_TEQU_NCEP.getVarName(), FCO2_FROM_XCO2_TEQU_NCEP.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_XCO2_SST_NCEP.getVarName(), FCO2_FROM_XCO2_SST_NCEP.toPropertyValue());
		typeProps.setProperty(FOC2_FROM_XCO2_TEQU_NCEP_WOA.getVarName(), FOC2_FROM_XCO2_TEQU_NCEP_WOA.toPropertyValue());
		typeProps.setProperty(FCO2_FROM_XCO2_SST_NCEP_WOA.getVarName(), FCO2_FROM_XCO2_SST_NCEP_WOA.toPropertyValue());
		typeProps.setProperty(FCO2_REC.getVarName(), FCO2_REC.toPropertyValue());
		typeProps.setProperty(FCO2_SOURCE.getVarName(), FCO2_SOURCE.toPropertyValue());
		typeProps.setProperty(WOA_SALINITY.getVarName(), WOA_SALINITY.toPropertyValue());
		typeProps.setProperty(NCEP_SLP.getVarName(), NCEP_SLP.toPropertyValue());
		typeProps.setProperty(DELTA_TEMP.getVarName(), DELTA_TEMP.toPropertyValue());
		typeProps.setProperty(CALC_SPEED.getVarName(), CALC_SPEED.toPropertyValue());
		typeProps.setProperty(ETOPO2_DEPTH.getVarName(), ETOPO2_DEPTH.toPropertyValue());
		typeProps.setProperty(GVCO2.getVarName(), GVCO2.toPropertyValue());
		typeProps.setProperty(REGION_ID.getVarName(), REGION_ID.toPropertyValue());
		typeProps.setProperty(DIST_TO_LAND.getVarName(), DIST_TO_LAND.toPropertyValue());
		typeProps.setProperty(DAY_OF_YEAR.getVarName(), DAY_OF_YEAR.toPropertyValue());
		KNOWN_SOCAT_DATA_FILE_TYPES.addTypesFromProperties(typeProps);
	}

}
