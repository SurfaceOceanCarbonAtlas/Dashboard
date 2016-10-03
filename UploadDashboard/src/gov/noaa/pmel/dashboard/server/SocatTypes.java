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

	public static final DashDataType TATM = new DashDataType("Temperature_atm", 
			612.0, "T_atm", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"sea-level air temperature", "air_temperature_at_sea_level", 
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

	public static final DashDataType XCO2_WATER_TEQU_WET = new DashDataType("xCO2_water_equi_temp_wet_ppm", 
			632.0, "xCO2_water_Tequ_wet", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water xCO2 wet using equi temp", "mole_fraction_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType XCO2_WATER_SST_WET = new DashDataType("xCO2_water_sst_wet_ppm", 
			633.0, "xCO2_water_SST_wet", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water xCO2 wet using sst", "mole_fraction_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType PCO2_WATER_TEQU_WET = new DashDataType("pCO2_water_equi_temp", 
			634.0, "pCO2_water_Tequ_wet", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water pCO2 wet using equi temp", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType PCO2_WATER_SST_WET = new DashDataType("pCO2_water_sst_100humidity_uatm", 
			635.0, "pCO2_water_SST_wet", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water pCO2 wet using sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType FCO2_WATER_TEQU_WET = new DashDataType("fCO2_water_equi_uatm", 
			636.0, "fCO2_water_Tequ_wet", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water fCO2 wet using equi temp", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_WATER_SST_WET = new DashDataType("fCO2_water_sst_100humidity_uatm", 
			637.0, "fCO2_water_SST_wet", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water fCO2 wet using sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);


	public static final DashDataType XCO2_ATM_DRY_ACTUAL = new DashDataType("xCO2_atm_dry_actual", 
			640.0, "xCO2_atm_dry_actual", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"actual air xCO2 dry", "mole_fraction_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType PCO2_ATM_WET_ACTUAL = new DashDataType("pCO2_atm_wet_actual", 
			641.0, "pCO2_atm_wet_actual", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"actual air pCO2 wet", "surface_partial_pressure_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType FCO2_ATM_WET_ACTUAL = new DashDataType("fCO2_atm_wet_actual", 
			642.0, "fCO2_atm_wet_actual", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"actual air fCO2 wet", "surface_partial_pressure_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType XCO2_ATM_DRY_INTERP = new DashDataType("xCO2_atm_dry_interp", 
			643.0, "xCO2_atm_dry_interp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"interpolated air xCO2 dry", "mole_fraction_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType PCO2_ATM_WET_INTERP = new DashDataType("pCO2_atm_wet_interp", 
			644.0, "pCO2_atm_wet_interp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"interpolated air pCO2 wet", "surface_partial_pressure_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType FCO2_ATM_WET_INTERP = new DashDataType("fCO2_atm_wet_interp", 
			645.0, "fCO2_atm_wet_interp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"interpolated air fCO2 wet", "surface_partial_pressure_of_carbon_dioxide_in_air", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType DELTA_XCO2 = new DashDataType("delta_xCO2", 
			646.0, "delta_xCO2", 
			DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water xCO2 minus atmospheric xCO2", null, 
			DashboardUtils.CO2_CATEGORY, XCO2_UNITS);

	public static final DashDataType DELTA_PCO2 = new DashDataType("delta_pCO2", 
			647.0, "delta_pCO2", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water pCO2 minus atmospheric pCO2", null, 
			DashboardUtils.CO2_CATEGORY, PCO2_UNITS);

	public static final DashDataType DELTA_FCO2 = new DashDataType("delta_fCO2", 
			648.0, "delta_fCO2", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"water fCO2 minus atmospheric fCO2", null, 
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

	
	public static final DashDataType XH2O_EQU = new DashDataType("xH2O_equi", 
			660.0, "xH2O_equi", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"xH2O in equil air sample", "mole_fraction_of_water_in_air", 
			DashboardUtils.WATER_VAPOR_CATEGORY, XH2O_UNITS);

	public static final DashDataType RELATIVE_HUMIDITY = new DashDataType("relative_humidity", 
			661.0, "rel humidity", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"relative humidity", "relative_humidity", 
			DashboardUtils.WATER_VAPOR_CATEGORY, DashboardUtils.NO_UNITS);

	public static final DashDataType SPECIFIC_HUMIDITY = new DashDataType("specific_humidity", 
			662.0, "spec humidity", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"specific humidity", "specific_humidity", 
			DashboardUtils.WATER_VAPOR_CATEGORY, DashboardUtils.NO_UNITS);

	
	public static final DashDataType SHIP_SPEED = new DashDataType("ship_speed", 
			670.0, "ship speed", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"measured ship speed", "platform_speed_wrt_ground", 
			DashboardUtils.PLATFORM_CATEGORY, SHIP_SPEED_UNITS);

	public static final DashDataType SHIP_DIRECTION = new DashDataType("ship_dir", 
			671.0, "ship dir", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"ship direction", "platform_course", 
			DashboardUtils.PLATFORM_CATEGORY, DIRECTION_UNITS);

	public static final DashDataType WIND_SPEED_TRUE = new DashDataType("wind_speed_true", 
			672.0, "true wind speed", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"true wind speed", "wind_speed", 
			DashboardUtils.WIND_CATEGORY, WIND_SPEED_UNITS);

	public static final DashDataType WIND_DIRECTION_TRUE = new DashDataType("wind_dir_true", 
			673.0, "true wind dir", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"true wind direction", "wind_from_direction", 
			DashboardUtils.WIND_CATEGORY, DIRECTION_UNITS);

	public static final DashDataType WIND_SPEED_RELATIVE = new DashDataType("wind_speed_rel", 
			674.0, "rel wind speed", DashboardUtils.DOUBLE_DATA_CLASS_NAME, "relative wind speed", 
			"wind_speed", DashboardUtils.WIND_CATEGORY, WIND_SPEED_UNITS);

	public static final DashDataType WIND_DIRECTION_RELATIVE = new DashDataType("wind_dir_rel", 
			675.0, "rel wind dir", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"relative wind direction", "wind_from_direction", 
			DashboardUtils.WIND_CATEGORY, DIRECTION_UNITS);



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

	public static final DashDataType DELTA_TEMP = new DashDataType("delta_temp", 
			703.0, "delta_temp", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"Equilibrator Temp - SST", null, 
			DashboardUtils.TEMPERATURE_CATEGORY, TEMPERATURE_UNITS);

	public static final DashDataType CALC_SPEED = new DashDataType("calc_speed", 
			704.0, "calc ship speed", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"calculated ship speed", "platform_speed_wrt_ground", 
			DashboardUtils.PLATFORM_CATEGORY, SHIP_SPEED_UNITS);

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


	public static final DashDataType FCO2_FROM_XCO2_TEQU = new DashDataType("fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm", 
			720.0, "fCO2 from xCO2_water_Tequ_dry, Pequ, sal", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_equi_temp_dry_ppm, Pressure_equi, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_SST = new DashDataType("fCO2_insitu_from_xCO2_water_sst_dry_ppm", 
			721.0, "fCO2 from xCO2_water_SST_dry, Pequ, sal", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_sst_dry_ppm, Pressure_equi, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_PCO2_TEQU = new DashDataType("fCO2_from_pCO2_water_water_equi_temp", 
			722.0, "fCO2 from pCO2_water_Tequ_wet, Pequ, sal", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from pCO2_water_equi_temp, Pressure_equi, sal","surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_PCO2_SST = new DashDataType("fCO2_from_pCO2_water_sst_100humidity_uatm", 
			723.0, "fCO2 from pCO2_water_SST_wet, Pequ, sal", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from pCO2_water_sst_100humidity_uatm, Pressure_equi, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_FCO2_TEQU = new DashDataType("fCO2_insitu_from_fCO2_water_equi_uatm", 
			724.0, "fCO2 from fCO2_water_Tequ_wet, Pequ, sal", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from fCO2_water_equi_temp, Pressure_equi, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_FCO2_SST = new DashDataType("fCO2_insitu_from_fCO2_water_sst_100humidty_uatm", 
			725.0, "fCO2 from fCO2_water_SST_wet, Pequ, sal", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from fCO2_water_sst_100humidity_uatm, Pressure_equi, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_PCO2_TEQU_NCEP = new DashDataType("fCO2_from_pCO2_water_water_equi_temp_ncep", 
			726.0, "fCO2 from pCO2_water_Tequ_wet, NCEP SLP, sal", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from pCO2_water_equi_temp, NCEP SLP, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_PCO2_SST_NCEP = new DashDataType("fCO2_from_pCO2_water_sst_100humidity_uatm_ncep", 
			727.0, "fCO2 from pCO2_water_SST_wet, NCEP SLP, sal", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from pCO2_water_sst_100humidity_uatm, NCEP SLP, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_TEQU_WOA = new DashDataType("fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_woa", 
			728.0, "fCO2 from xCO2_water_Tequ_dry, Pequ, WOA SSS", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_equi_temp_dry_ppm, Pressure_equi, WOA SSS", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_SST_WOA = new DashDataType("fCO2_insitu_from_xCO2_water_sst_dry_ppm_woa", 
			729.0, "fCO2 from xCO2_water_SST_dry, Pequ, WOA SSS", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_sst_dry_ppm, Pressure_equi, WOA SSS", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_TEQU_NCEP = new DashDataType("fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep", 
			730.0, "fCO2 from xCO2_water_Tequ_dry, NCEP SLP, sal", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_equi_temp_dry_ppm, NCEP SLP, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_SST_NCEP = new DashDataType("fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep", 
			731.0, "fCO2 from xCO2_water_SST_dry, NCEP SLP, sal", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_sst_dry_ppm, NCEP SLP, sal", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FOC2_FROM_XCO2_TEQU_NCEP_WOA = new DashDataType("fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep_woa", 
			732.0, "fCO2 from xCO2_water_Tequ_dry, NCEP SLP, WOA SSS", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_equi_temp_dry_ppm, NCEP SLP, WOA SSS", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_SST_NCEP_WOA = new DashDataType("fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep_woa", 
			733.0, "fCO2 from xCO2_water_SST_dry, NCEP SLP, WOA SSS", DashboardUtils.DOUBLE_DATA_CLASS_NAME, 
			"fCO2 from xCO2_water_sst_dry_ppm, NCEP SLP, WOA SSS", "surface_partial_pressure_of_carbon_dioxide_in_sea_water", 
			DashboardUtils.CO2_CATEGORY, FCO2_UNITS);



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
		typeProps.setProperty(PCO2_ATM_WET_ACTUAL.getVarName(), PCO2_ATM_WET_ACTUAL.toPropertyValue());
		typeProps.setProperty(FCO2_ATM_WET_ACTUAL.getVarName(), FCO2_ATM_WET_ACTUAL.toPropertyValue());
		typeProps.setProperty(XCO2_ATM_DRY_INTERP.getVarName(), XCO2_ATM_DRY_INTERP.toPropertyValue());
		typeProps.setProperty(PCO2_ATM_WET_INTERP.getVarName(), PCO2_ATM_WET_INTERP.toPropertyValue());
		typeProps.setProperty(FCO2_ATM_WET_INTERP.getVarName(), FCO2_ATM_WET_INTERP.toPropertyValue());
		typeProps.setProperty(DELTA_XCO2.getVarName(), DELTA_XCO2.toPropertyValue());
		typeProps.setProperty(DELTA_PCO2.getVarName(), DELTA_PCO2.toPropertyValue());
		typeProps.setProperty(DELTA_FCO2.getVarName(), DELTA_FCO2.toPropertyValue());
		typeProps.setProperty(WOCE_CO2_WATER.getVarName(), WOCE_CO2_WATER.toPropertyValue());
		typeProps.setProperty(COMMENT_WOCE_CO2_WATER.getVarName(), COMMENT_WOCE_CO2_WATER.toPropertyValue());
		typeProps.setProperty(WOCE_CO2_ATM.getVarName(), WOCE_CO2_ATM.toPropertyValue());
		typeProps.setProperty(COMMENT_WOCE_CO2_ATM.getVarName(), COMMENT_WOCE_CO2_ATM.toPropertyValue());
		typeProps.setProperty(XH2O_EQU.getVarName(), XH2O_EQU.toPropertyValue());
		typeProps.setProperty(RELATIVE_HUMIDITY.getVarName(), RELATIVE_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SPECIFIC_HUMIDITY.getVarName(), SPECIFIC_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SHIP_SPEED.getVarName(), SHIP_SPEED.toPropertyValue());
		typeProps.setProperty(SHIP_DIRECTION.getVarName(), SHIP_DIRECTION.toPropertyValue());
		typeProps.setProperty(WIND_SPEED_TRUE.getVarName(), WIND_SPEED_TRUE.toPropertyValue());
		typeProps.setProperty(WIND_DIRECTION_TRUE.getVarName(), WIND_DIRECTION_TRUE.toPropertyValue());
		typeProps.setProperty(WIND_SPEED_RELATIVE.getVarName(), WIND_SPEED_RELATIVE.toPropertyValue());
		typeProps.setProperty(WIND_DIRECTION_RELATIVE.getVarName(), WIND_DIRECTION_RELATIVE.toPropertyValue());
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
		typeProps.setProperty(PCO2_ATM_WET_ACTUAL.getVarName(), PCO2_ATM_WET_ACTUAL.toPropertyValue());
		typeProps.setProperty(FCO2_ATM_WET_ACTUAL.getVarName(), FCO2_ATM_WET_ACTUAL.toPropertyValue());
		typeProps.setProperty(XCO2_ATM_DRY_INTERP.getVarName(), XCO2_ATM_DRY_INTERP.toPropertyValue());
		typeProps.setProperty(PCO2_ATM_WET_INTERP.getVarName(), PCO2_ATM_WET_INTERP.toPropertyValue());
		typeProps.setProperty(FCO2_ATM_WET_INTERP.getVarName(), FCO2_ATM_WET_INTERP.toPropertyValue());
		typeProps.setProperty(DELTA_XCO2.getVarName(), DELTA_XCO2.toPropertyValue());
		typeProps.setProperty(DELTA_PCO2.getVarName(), DELTA_PCO2.toPropertyValue());
		typeProps.setProperty(DELTA_FCO2.getVarName(), DELTA_FCO2.toPropertyValue());
		typeProps.setProperty(WOCE_CO2_WATER.getVarName(), WOCE_CO2_WATER.toPropertyValue());
		typeProps.setProperty(WOCE_CO2_ATM.getVarName(), WOCE_CO2_ATM.toPropertyValue());
		typeProps.setProperty(XH2O_EQU.getVarName(), XH2O_EQU.toPropertyValue());
		typeProps.setProperty(RELATIVE_HUMIDITY.getVarName(), RELATIVE_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SPECIFIC_HUMIDITY.getVarName(), SPECIFIC_HUMIDITY.toPropertyValue());
		typeProps.setProperty(SHIP_SPEED.getVarName(), SHIP_SPEED.toPropertyValue());
		typeProps.setProperty(SHIP_DIRECTION.getVarName(), SHIP_DIRECTION.toPropertyValue());
		typeProps.setProperty(WIND_SPEED_TRUE.getVarName(), WIND_SPEED_TRUE.toPropertyValue());
		typeProps.setProperty(WIND_DIRECTION_TRUE.getVarName(), WIND_DIRECTION_TRUE.toPropertyValue());
		typeProps.setProperty(WIND_SPEED_RELATIVE.getVarName(), WIND_SPEED_RELATIVE.toPropertyValue());
		typeProps.setProperty(WIND_DIRECTION_RELATIVE.getVarName(), WIND_DIRECTION_RELATIVE.toPropertyValue());
		typeProps.setProperty(WOA_SALINITY.getVarName(), WOA_SALINITY.toPropertyValue());
		typeProps.setProperty(NCEP_SLP.getVarName(), NCEP_SLP.toPropertyValue());
		typeProps.setProperty(DELTA_TEMP.getVarName(), DELTA_TEMP.toPropertyValue());
		typeProps.setProperty(CALC_SPEED.getVarName(), CALC_SPEED.toPropertyValue());
		typeProps.setProperty(ETOPO2_DEPTH.getVarName(), ETOPO2_DEPTH.toPropertyValue());
		typeProps.setProperty(GVCO2.getVarName(), GVCO2.toPropertyValue());
		typeProps.setProperty(REGION_ID.getVarName(), REGION_ID.toPropertyValue());
		typeProps.setProperty(DIST_TO_LAND.getVarName(), DIST_TO_LAND.toPropertyValue());
		typeProps.setProperty(FCO2_REC.getVarName(), FCO2_REC.toPropertyValue());
		typeProps.setProperty(FCO2_SOURCE.getVarName(), FCO2_SOURCE.toPropertyValue());
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
		KNOWN_SOCAT_DATA_FILE_TYPES.addTypesFromProperties(typeProps);
	}

}
