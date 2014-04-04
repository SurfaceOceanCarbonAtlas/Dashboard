package gov.noaa.pmel.socat.dashboard.nc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Constant maps of SocatCruiseData variable names to short names, 
 * units, and long names for the NetCDF DSG files.
 */
public class Constants {

	/**
	 * Variable names for netCDF files
	 */
	public static final Map<String, String> SHORT_NAME;
	static {
		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("year", "year");
		aMap.put("month", "month");
		aMap.put("day", "day");
		aMap.put("hour", "hour");
		aMap.put("minute", "minute");
		aMap.put("second", "second");
		aMap.put("longitude", "longitude");
		aMap.put("latitude", "latitude");
		aMap.put("sampleDepth", "sampleDepth");
		aMap.put("sal", "sal");
		aMap.put("sst", "temp");
		aMap.put("tEqu", "Temperature_equi");
		aMap.put("slp", "Pressure_atm");
		aMap.put("pEqu", "Pressure_equi");
		aMap.put("xCO2WaterSst", "xCO2_water_sst_dry_ppm");
		aMap.put("xCO2WaterTEqu", "xCO2_water_equi_temp_dry_ppm");
		aMap.put("fCO2WaterSst", "fCO2_water_sst_100humidity_uatm");
		aMap.put("fCO2WaterTEqu", "fCO2_water_equi_uatm");
		aMap.put("pCO2WaterSst", "pCO2_water_sst_100humidity_uatm");
		aMap.put("pCO2WaterTEqu", "pCO2_water_equi_temp");
		aMap.put("xCO2Air", "xCO2_air");
		aMap.put("pCO2Air", "pCO2_air");
		aMap.put("fCO2Air", "fCO2_air");
		aMap.put("shipSpeed", "ship_speed"); 
		aMap.put("shipDirection", "ship_dir");
		aMap.put("windSpeedTrue", "wind_speed_true");
		aMap.put("windSpeedRelative", "wind_speed_rel");
		aMap.put("windDirectionTrue", "wind_dir_true");
		aMap.put("windDirectionRelative", "wind_dir_rel");
		aMap.put("timestampWoce", "WOCE_time");
		aMap.put("longitudeWoce", "WOCE_longitude");
		aMap.put("latitudeWoce", "WOCE_latitude");
		aMap.put("depthWoce", "WOCE_sampleDepth");
		aMap.put("salinityWoce", "WOCE_sal");
		aMap.put("tEquWoce", "WOCE_Temperature_equi");
		aMap.put("sstWoce", "WOCE_temp");
		aMap.put("pEquWoce", "WOCE_Pressure_equi");
		aMap.put("slpWoce", "WOCE_Pressure_atm");
		aMap.put("xCO2WaterEquWoce", "WOCE_xCO2_water_equi");
		aMap.put("xCO2WaterSSTWoce", "WOCE_xCO2_water_sst");
		aMap.put("pCO2WaterEquWoce", "WOCE_fCO2_water_equi");
		aMap.put("pCO2WaterSSTWoce", "WOCE_fCO2_water_sst");
		aMap.put("fCO2WaterEquWoce", "WOCE_pCO2_water_equi");
		aMap.put("fCO2WaterSSTWoce", "WOCE_pCO2_water_sst");
		aMap.put("xCO2AirWoce", "WOCE_xCO2_air");
		aMap.put("pCO2AirWoce", "WOCE_pCO2_air");
		aMap.put("fCO2AirWoce", "WOCE_fCO2_air");
		aMap.put("deltaXCO2", "delta_xCO2");
		aMap.put("deltaPCO2", "delta_pCO2");
		aMap.put("deltaFCO2", "delta_fCO2");
		aMap.put("woaSss", "woa_sss");
		aMap.put("ncepSlp", "pressure_ncep_slp");
		aMap.put("fCO2FromXCO2TEqu", "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm");
		aMap.put("fCO2FromXCO2Sst", "fCO2_insitu_from_xCO2_water_sst_dry_ppm");
		aMap.put("fCO2FromPCO2TEqu", "fCO2_from_pCO2_water_water_equi_temp");
		aMap.put("fCO2FromPCO2Sst", "fCO2_from_pCO2_water_sst_100humidity_uatm");
		aMap.put("fCO2FromFCO2TEqu", "fCO2_insitu_from_fCO2_water_equi_uatm");
		aMap.put("fCO2FromFCO2Sst", "fCO2_insitu_from_fCO2_water_sst_100humidty_uatm");
		aMap.put("fCO2FromPCO2TEquNcep", "fCO2_from_pCO2_water_water_equi_temp_ncep");
		aMap.put("fCO2FromPCO2SstNcep", "fCO2_from_pCO2_water_sst_100humidity_uatm_ncep");
		aMap.put("fCO2FromXCO2TEquWoa", "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_woa");
		aMap.put("fCO2FromXCO2SstWoa", "fCO2_insitu_from_xCO2_water_sst_dry_ppm_woa");
		aMap.put("fCO2FromXCO2TEquNcep", "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep");
		aMap.put("fCO2FromXCO2SstNcep", "fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep");
		aMap.put("fCO2FromXCO2TEquNcepWoa", "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep_woa");
		aMap.put("fCO2FromXCO2SstNcepWoa", "fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep_woa");
		aMap.put("fCO2Rec", "fCO2_recommended");
		aMap.put("fCO2Source", "fco2_source");
		aMap.put("deltaT", "delta_temperature");
		aMap.put("regionID", "region_id");
		aMap.put("days1970", "days1970");
		aMap.put("dayOfYear", "dayOfYear");
		aMap.put("calcSpeed", "calcSpeed");
		aMap.put("etopo2Depth", "etopo2");
		aMap.put("gvCO2", "gvCO2");
		aMap.put("distToLand", "distToLand");

		SHORT_NAME = Collections.unmodifiableMap(aMap);
	}

	/**
	 *  Full length names of the variables for netCDF files
	 */
	public static final Map<String, String> LONG_NAME;
	static {
		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("year", "year");
		aMap.put("month", "month of year");
		aMap.put("day", "day of month");
		aMap.put("hour", "hour of day");
		aMap.put("minute", "minute of hour");
		aMap.put("second", "second of minute");
		aMap.put("longitude", "longitude");
		aMap.put("latitude", "latitude");
		aMap.put("sampleDepth", "sample depth");
		aMap.put("sal", "salinity");
		aMap.put("sst", "sea surface temperature");
		aMap.put("tEqu", "equilibrator chamber temperature");
		aMap.put("slp", "sea-level air pressure");
		aMap.put("pEqu", "equilibrator chamber pressure");
		aMap.put("xCO2WaterSst", "xCO2 using sst");
		aMap.put("xCO2WaterTEqu", "xCO2 using equi temp");
		aMap.put("pCO2WaterSst", "pCO2 using sst");
		aMap.put("pCO2WaterTEqu", "pCO2 using equi temp");
		aMap.put("fCO2WaterSst", "fCO2 using sst");
		aMap.put("fCO2WaterTEqu", "fCO2 using equi temp");
		aMap.put("xCO2Air", "air xCO2");
		aMap.put("pCO2Air", "air pCO2");
		aMap.put("fCO2Air", "air fCO2");
		aMap.put("shipSpeed", "measured ship speed");
		aMap.put("shipDirection", "ship direction"); 
		aMap.put("windSpeedTrue", "true wind speed");
		aMap.put("windSpeedRelative", "relative wind speed");
		aMap.put("windDirectionTrue", "true wind direction");
		aMap.put("windDirectionRelative", "relative wind direction");
		aMap.put("timestampWoce", "WOCE flag for date/time");
		aMap.put("longitudeWoce", "WOCE flag for longitude");
		aMap.put("latitudeWoce", "WOCE flag for latitude");
		aMap.put("depthWoce", "WOCE flag for sample depth");
		aMap.put("salinityWoce", "WOCE flag for salinity");
		aMap.put("tEquWoce", "WOCE flag for equilibrator temperature");
		aMap.put("sstWoce", "WOCE flag for sea surface temperature");
		aMap.put("pEquWoce", "WOCE flag for equilibrator pressure");
		aMap.put("slpWoce", "WOCE flag for sea-level pressure");
		aMap.put("xCO2WaterEquWoce", "WOCE flag for water xCO2 using equi temp");
		aMap.put("xCO2WaterSSTWoce", "WOCE flag for water xCO2 using sst");
		aMap.put("pCO2WaterEquWoce", "WOCE flag for water pCO2 using equi temp");
		aMap.put("pCO2WaterSSTWoce", "WOCE flag for water pCO2 using sst");
		aMap.put("fCO2WaterEquWoce", "WOCE flag for water fCO2 using equi temp");
		aMap.put("fCO2WaterSSTWoce", "WOCE flag for water fCO2 using sst");
		aMap.put("xCO2AirWoce", "WOCE flag for air xCO2");
		aMap.put("pCO2AirWoce", "WOCE flag for air pCO2");
		aMap.put("fCO2AirWoce", "WOCE flag for air fCO2");
		aMap.put("deltaXCO2", "air/water xCO2 difference");
		aMap.put("deltaPCO2", "air/water pCO2 difference");
		aMap.put("deltaFCO2", "air/water fCO2 difference");
		aMap.put("woaSss", "salinity from World Ocean Atlas 2005");
		aMap.put("ncepSlp", "sea level air pressure from NCEP/NCAR reanalysis");
		aMap.put("fCO2FromXCO2TEqu", "fco2 from xCO2_water_equi_temp_dry_ppm, Temperature_equi, sal");
		aMap.put("fCO2FromXCO2Sst", "fco2 from xCO2_water_sst_dry_ppm, Temperature_equi, sal");
		aMap.put("fCO2FromPCO2TEqu", "fco2 from pCO2_water_equi_temp, Pressure_equi, sal");
		aMap.put("fCO2FromPCO2Sst", "fco2 from pCO2_water_sst_100humidity_uatm, Pressure_equi, sal");
		aMap.put("fCO2FromFCO2TEqu", "fco2 from fCO2_water_equi_tem, Pressure_equi, sal");
		aMap.put("fCO2FromFCO2Sst", "fco2 from fCO2_water_sst_100humidity_uatm, Pressure_equi, sal");
		aMap.put("fCO2FromPCO2TEquNcep", "fco2 from pCO2_water_equi_temp, ncepSlp, sal");
		aMap.put("fCO2FromPCO2SstNcep", "fco2 from pCO2_water_sst_100humidity_uatm, ncepSlp, sal");
		aMap.put("fCO2FromXCO2TEquWoa", "fco2 from xCO2_water_equi_temp_dry_ppm, Pressure_equi, WOASss");
		aMap.put("fCO2FromXCO2SstWoa", "fco2 from xCO2_water_sst_dry_ppm, Pressure_equi, WOASss");
		aMap.put("fCO2FromXCO2TEquNcep", "fco2 from xCO2_water_equi_temp_dry_ppm, ncepSlp, sal");
		aMap.put("fCO2FromXCO2SstNcep", "fco2 from xCO2_water_sst_dry_ppm, ncepSlp, sal");
		aMap.put("fCO2FromXCO2TEquNcepWoa", "fco2 from xCO2_water_equi_temp_dry_ppm, ncepSlp, WOASss");
		aMap.put("fCO2FromXCO2SstNcepWoa", "fco2 from xCO2_water_sst_dry_ppm, ncepSlp, WOASss");
		aMap.put("fCO2Rec", "fCO2 recommended");
		aMap.put("fCO2Source", "Algorithm number for recommended fCO2");
		aMap.put("deltaT", "Equilibrator Temp - SST");
		aMap.put("regionID", "SOCAT region ID");
		aMap.put("days1970", "time");
		aMap.put("dayOfYear", "day of the year");
		aMap.put("calcSpeed", "calculated ship speed");
		aMap.put("etopo2Depth", "bathymetry from ETOPO2");
		aMap.put("gvCO2", "GlobalView xCO2");
		aMap.put("distToLand", "distance to land");

		LONG_NAME = Collections.unmodifiableMap(aMap);
	}

	/**
	 * Standard units of each variable appropriate for netCDF files
	 */
	public static final Map<String, String> UNITS;
	static {
		String longitude_units = "degrees_east";
		String latitude_units = "degrees_north";
		String depth_units = "meters";
		String temperature_units = "degrees C";
		String salinity_units = "PSU";
		String pressure_units = "hPa";
		String xco2_units = "umol/mol";
		String pco2_units = "uatm";
		String fco2_units = "uatm";
		String ship_speed_units = "knots";
		String wind_speed_units = "m/s";
		String direction_units = "degrees";

		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("longitude", longitude_units);
		aMap.put("latitude", latitude_units);
		aMap.put("sampleDepth", depth_units);
		aMap.put("sal", salinity_units);
		aMap.put("sst", temperature_units);
		aMap.put("tEqu", temperature_units);
		aMap.put("slp", pressure_units);
		aMap.put("pEqu", pressure_units);
		aMap.put("xCO2WaterSst", xco2_units);
		aMap.put("xCO2WaterTEqu", xco2_units);
		aMap.put("pCO2WaterSst", pco2_units);
		aMap.put("pCO2WaterTEqu", pco2_units);
		aMap.put("fCO2WaterSst", fco2_units);
		aMap.put("fCO2WaterTEqu", fco2_units);
		aMap.put("xCO2Air", xco2_units);
		aMap.put("pCO2Air", pco2_units);
		aMap.put("fCO2Air", fco2_units);
		aMap.put("shipSpeed", ship_speed_units);
		aMap.put("shipDirection", direction_units); 
		aMap.put("windSpeedTrue", wind_speed_units);
		aMap.put("windSpeedRelative", wind_speed_units);
		aMap.put("windDirectionTrue", direction_units);
		aMap.put("windDirectionRelative", direction_units);
		aMap.put("deltaXCO2", xco2_units);
		aMap.put("deltaPCO2", pco2_units);
		aMap.put("deltaFCO2", fco2_units);
		aMap.put("woaSss", salinity_units);
		aMap.put("ncepSlp", pressure_units);
		aMap.put("fCO2FromXCO2TEqu", fco2_units);
		aMap.put("fCO2FromXCO2Sst", fco2_units);
		aMap.put("fCO2FromPCO2TEqu", fco2_units);
		aMap.put("fCO2FromPCO2Sst", fco2_units);
		aMap.put("fCO2FromFCO2TEqu", fco2_units);
		aMap.put("fCO2FromFCO2Sst", fco2_units);
		aMap.put("fCO2FromPCO2TEquNcep", fco2_units);
		aMap.put("fCO2FromPCO2SstNcep", fco2_units);
		aMap.put("fCO2FromXCO2TEquWoa", fco2_units);
		aMap.put("fCO2FromXCO2SstWoa", fco2_units);
		aMap.put("fCO2FromXCO2TEquNcep", fco2_units);
		aMap.put("fCO2FromXCO2SstNcep", fco2_units);
		aMap.put("fCO2FromXCO2TEquNcepWoa", fco2_units);
		aMap.put("fCO2FromXCO2SstNcepWoa", fco2_units);
		aMap.put("fCO2Rec", fco2_units);
		aMap.put("deltaT", temperature_units);
		aMap.put("calcSpeed", ship_speed_units);
		aMap.put("etopo2Depth", depth_units);
		aMap.put("gvCO2", xco2_units);
		aMap.put("distToLand", "km");
		aMap.put("days1970", "days since 1970-01-01 00:00:00");
		aMap.put("dayOfYear", "days");

		UNITS = Collections.unmodifiableMap(aMap);
	}

	/**
	 * Standardized names for netCDF files
	 */
	public static final Map<String, String> STANDARD_NAMES;
	static {
		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("longitude", "longitude");
		aMap.put("latitude", "latitude");
		aMap.put("sampleDepth", "depth");
		aMap.put("sal", "sea_surface_salinity");
		aMap.put("sst", "sea_surface_temperature");
		aMap.put("slp", "air_pressure_at_sea_level");
		aMap.put("xCO2WaterSst", "mole_fraction_of_carbon_dioxide_in_sea_water");
		aMap.put("xCO2WaterTEqu", "mole_fraction_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2WaterSst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2WaterTEqu", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("pCO2WaterSst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("pCO2WaterTEqu", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("xCO2Air", "mole_fraction_of_carbon_dioxide_in_air");
		aMap.put("pCO2Air", "surface_partial_pressure_of_carbon_dioxide_in_air");
		aMap.put("fCO2Air", "surface_partial_pressure_of_carbon_dioxide_in_air");
		aMap.put("shipSpeed", "platform_speed_wrt_ground"); 
		aMap.put("shipDirection", "platform_course");
		aMap.put("windSpeedTrue", "wind_speed");
		aMap.put("windSpeedRelative", "wind_speed");
		aMap.put("windDirectionTrue", "wind_from_direction");
		aMap.put("windDirectionRelative", "wind_from_direction");
		aMap.put("woaSss", "sea_surface_salinity");
		aMap.put("ncepSlp", "air_pressure_at_sea_level");
		aMap.put("fCO2FromXCO2TEqu", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromXCO2Sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromPCO2TEqu", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromPCO2Sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromFCO2TEqu", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromFCO2Sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromPCO2TEquNcep", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromPCO2SstNcep", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromXCO2TEquWoa", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromXCO2SstWoa", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromXCO2TEquNcep", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromXCO2SstNcep", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromXCO2TEquNcepWoa", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2FromXCO2SstNcepWoa", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("fCO2Rec", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		aMap.put("days1970", "time");
		aMap.put("calcSpeed", "platform_speed_wrt_ground");
		aMap.put("etopo2Depth", "sea_floor_depth_below_geoid");
		aMap.put("gvCO2", "mole_fraction_of_carbon_dioxide_in_air");

		STANDARD_NAMES = Collections.unmodifiableMap(aMap);
	}

	/**
	 * Variable names for netCDF files
	 */
	public static final Map<String, String> IOOS_CATEGORIES;
	static {
		String bathymetry_category = "Bathymetry";
		String identifier_category = "Identifier";
		String location_category = "Location";
		String other_category = "Other";
		String pCO2_category = "CO2";
		String pressure_category = "Pressure";
		String quality_category = "Quality";
		String salinity_category = "Salinity";
		String temperature_category = "Temperature";
		String time_category = "Time";
		String wind_category = "Wind";

		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("year", time_category);
		aMap.put("month", time_category);
		aMap.put("day", time_category);
		aMap.put("hour", time_category);
		aMap.put("minute", time_category);
		aMap.put("second", time_category);
		aMap.put("longitude", location_category);
		aMap.put("latitude", location_category);
		aMap.put("sampleDepth", bathymetry_category);
		aMap.put("sal", salinity_category);
		aMap.put("sst", temperature_category);
		aMap.put("tEqu", temperature_category);
		aMap.put("slp", pressure_category);
		aMap.put("pEqu", pressure_category);
		aMap.put("xCO2WaterSst", pCO2_category);
		aMap.put("xCO2WaterTEqu", pCO2_category);
		aMap.put("fCO2WaterSst", pCO2_category);
		aMap.put("fCO2WaterTEqu", pCO2_category);
		aMap.put("pCO2WaterSst", pCO2_category);
		aMap.put("pCO2WaterTEqu", pCO2_category);
		aMap.put("xCO2Air", pCO2_category);
		aMap.put("pCO2Air", pCO2_category);
		aMap.put("fCO2Air", pCO2_category);
		aMap.put("shipSpeed", other_category); 
		aMap.put("shipDirection", other_category);
		aMap.put("windSpeedTrue", wind_category);
		aMap.put("windSpeedRelative", wind_category);
		aMap.put("windDirectionTrue", wind_category);
		aMap.put("windDirectionRelative", wind_category);
		aMap.put("timestampWoce", quality_category);
		aMap.put("longitudeWoce", quality_category);
		aMap.put("latitudeWoce", quality_category);
		aMap.put("depthWoce", quality_category);
		aMap.put("salinityWoce", quality_category);
		aMap.put("tEquWoce", quality_category);
		aMap.put("sstWoce", quality_category);
		aMap.put("pEquWoce", quality_category);
		aMap.put("slpWoce", quality_category);
		aMap.put("xCO2WaterEquWoce", quality_category);
		aMap.put("xCO2WaterSSTWoce", quality_category);
		aMap.put("pCO2WaterEquWoce", quality_category);
		aMap.put("pCO2WaterSSTWoce", quality_category);
		aMap.put("fCO2WaterEquWoce", quality_category);
		aMap.put("fCO2WaterSSTWoce", quality_category);
		aMap.put("xCO2AirWoce", quality_category);
		aMap.put("pCO2AirWoce", quality_category);
		aMap.put("fCO2AirWoce", quality_category);
		aMap.put("deltaXCO2", pCO2_category);
		aMap.put("deltaPCO2", pCO2_category);
		aMap.put("deltaFCO2", pCO2_category);
		aMap.put("woaSss", salinity_category);
		aMap.put("ncepSlp", pressure_category);
		aMap.put("fCO2FromXCO2TEqu", pCO2_category);
		aMap.put("fCO2FromXCO2Sst", pCO2_category);
		aMap.put("fCO2FromPCO2TEqu", pCO2_category);
		aMap.put("fCO2FromPCO2Sst", pCO2_category);
		aMap.put("fCO2FromFCO2TEqu", pCO2_category);
		aMap.put("fCO2FromFCO2Sst", pCO2_category);
		aMap.put("fCO2FromPCO2TEquNcep", pCO2_category);
		aMap.put("fCO2FromPCO2SstNcep", pCO2_category);
		aMap.put("fCO2FromXCO2TEquWoa", pCO2_category);
		aMap.put("fCO2FromXCO2SstWoa", pCO2_category);
		aMap.put("fCO2FromXCO2TEquNcep", pCO2_category);
		aMap.put("fCO2FromXCO2SstNcep", pCO2_category);
		aMap.put("fCO2FromXCO2TEquNcepWoa", pCO2_category);
		aMap.put("fCO2FromXCO2SstNcepWoa", pCO2_category);
		aMap.put("fCO2Rec", pCO2_category);
		aMap.put("fCO2Source", identifier_category);
		aMap.put("deltaT", temperature_category);
		aMap.put("regionID", identifier_category);
		aMap.put("days1970", time_category);
		aMap.put("dayOfYear", time_category);
		aMap.put("calcSpeed", other_category);
		aMap.put("etopo2Depth", bathymetry_category);
		aMap.put("gvCO2", pCO2_category);
		aMap.put("distToLand", location_category);

		IOOS_CATEGORIES = Collections.unmodifiableMap(aMap);
	}

}
