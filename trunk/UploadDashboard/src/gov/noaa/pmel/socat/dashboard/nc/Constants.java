package gov.noaa.pmel.socat.dashboard.nc;

import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;

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
		HashMap<String, String> shortNameMap = new HashMap<String, String>();

		shortNameMap.put("expocode", "expocode");
		shortNameMap.put("cruiseName", "dataset_name");
		shortNameMap.put("vesselName", "vessel_name");
		shortNameMap.put("organization", "organization");
		shortNameMap.put("westmostLongitude", "geospatial_lon_min");
		shortNameMap.put("eastmostLongitude", "geospatial_lon_max");
		shortNameMap.put("southmostLatitude", "geospatial_lat_min");
		shortNameMap.put("northmostLatitude", "geospatial_lat_max");
		shortNameMap.put("beginTime", "time_coverage_start");
		shortNameMap.put("endTime", "time_converage_end");
		shortNameMap.put("scienceGroup", "investigators");
		shortNameMap.put("origDataRef", "orig_data_ref");
		shortNameMap.put("addlDocs", "addl_docs");
		shortNameMap.put("socatDOI", "socat_data_doi");
		shortNameMap.put("socatDOIHRef", "socat_data_ref");
		shortNameMap.put("socatVersion", "socat_version");
		shortNameMap.put("qcFlag", "qc_flag");

		shortNameMap.put("year", "year");
		shortNameMap.put("month", "month");
		shortNameMap.put("day", "day");
		shortNameMap.put("hour", "hour");
		shortNameMap.put("minute", "minute");
		shortNameMap.put("second", "second");

		shortNameMap.put("longitude", "longitude");
		shortNameMap.put("latitude", "latitude");
		shortNameMap.put("sampleDepth", "sample_depth");
		shortNameMap.put("salinity", "sal");
		shortNameMap.put("tEqu", "Temperature_equi");
		shortNameMap.put("sst", "temp");
		shortNameMap.put("pEqu", "Pressure_equi");
		shortNameMap.put("slp", "Pressure_atm");

		shortNameMap.put("xCO2WaterTEqu", "xCO2_water_equi_temp_dry_ppm");
		shortNameMap.put("xCO2WaterSst", "xCO2_water_sst_dry_ppm");
		shortNameMap.put("pCO2WaterTEqu", "pCO2_water_equi_temp");
		shortNameMap.put("pCO2WaterSst", "pCO2_water_sst_100humidity_uatm");
		shortNameMap.put("fCO2WaterTEqu", "fCO2_water_equi_uatm");
		shortNameMap.put("fCO2WaterSst", "fCO2_water_sst_100humidity_uatm");

		shortNameMap.put("xCO2Atm", "xCO2_atm");
		shortNameMap.put("pCO2Atm", "pCO2_atm");
		shortNameMap.put("fCO2Atm", "fCO2_atm");
		shortNameMap.put("deltaXCO2", "delta_xCO2");
		shortNameMap.put("deltaPCO2", "delta_pCO2");
		shortNameMap.put("deltaFCO2", "delta_fCO2");

		shortNameMap.put("relativeHumidity", "relative_humidity");
		shortNameMap.put("specificHumidity", "specific_humidity");
		shortNameMap.put("shipSpeed", "ship_speed"); 
		shortNameMap.put("shipDirection", "ship_dir");
		shortNameMap.put("windSpeedTrue", "wind_speed_true");
		shortNameMap.put("windSpeedRelative", "wind_speed_rel");
		shortNameMap.put("windDirectionTrue", "wind_dir_true");
		shortNameMap.put("windDirectionRelative", "wind_dir_rel");

		shortNameMap.put("geopositionWoce", "WOCE_geoposition");
		shortNameMap.put("sampleDepthWoce", "WOCE_sample_depth");
		shortNameMap.put("salinityWoce", "WOCE_sal");
		shortNameMap.put("tEquWoce", "WOCE_Temperature_equi");
		shortNameMap.put("sstWoce", "WOCE_temp");
		shortNameMap.put("pEquWoce", "WOCE_Pressure_equi");
		shortNameMap.put("slpWoce", "WOCE_Pressure_atm");

		shortNameMap.put("xCO2WaterTEquWoce", "WOCE_xCO2_water_equi_temp_dry_ppm");
		shortNameMap.put("xCO2WaterSstWoce", "WOCE_xCO2_water_sst_dry_ppm");
		shortNameMap.put("pCO2WaterTEquWoce", "WOCE_pCO2_water_equi_temp");
		shortNameMap.put("pCO2WaterSstWoce", "WOCE_pCO2_water_sst_100humidity_uatm");
		shortNameMap.put("fCO2WaterTEquWoce", "WOCE_fCO2_water_equi_uatm");
		shortNameMap.put("fCO2WaterSstWoce", "WOCE_fCO2_water_sst_100humidity_uatm");

		shortNameMap.put("xCO2AtmWoce", "WOCE_xCO2_atm");
		shortNameMap.put("pCO2AtmWoce", "WOCE_pCO2_atm");
		shortNameMap.put("fCO2AtmWoce", "WOCE_fCO2_atm");
		shortNameMap.put("deltaXCO2Woce", "WOCE_delta_xCO2");
		shortNameMap.put("deltaPCO2Woce", "WOCE_delta_pCO2");
		shortNameMap.put("deltaFCO2Woce", "WOCE_delta_fCO2");

		shortNameMap.put("relativeHumidityWoce", "WOCE_rel_humidity");
		shortNameMap.put("specificHumidityWoce", "WOCE_spc_humidity");
		shortNameMap.put("shipSpeedWoce", "WOCE_ship_speed");
		shortNameMap.put("shipDirectionWoce", "WOCE_ship_dir");
		shortNameMap.put("windSpeedTrueWoce", "WOCE_wind_speed_true");
		shortNameMap.put("windSpeedRelativeWoce", "WOCE_wind_speed_rel");
		shortNameMap.put("windDirectionTrueWoce", "WOCE_wind_dir_true");
		shortNameMap.put("windDirectionRelativeWoce", "WOCE_wind_dir_rel");

		shortNameMap.put("woaSss", "woa_sss");
		shortNameMap.put("ncepSlp", "pressure_ncep_slp");

		shortNameMap.put("fCO2FromXCO2TEqu", "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm");
		shortNameMap.put("fCO2FromXCO2Sst", "fCO2_insitu_from_xCO2_water_sst_dry_ppm");
		shortNameMap.put("fCO2FromPCO2TEqu", "fCO2_from_pCO2_water_water_equi_temp");
		shortNameMap.put("fCO2FromPCO2Sst", "fCO2_from_pCO2_water_sst_100humidity_uatm");
		shortNameMap.put("fCO2FromFCO2TEqu", "fCO2_insitu_from_fCO2_water_equi_uatm");
		shortNameMap.put("fCO2FromFCO2Sst", "fCO2_insitu_from_fCO2_water_sst_100humidty_uatm");
		shortNameMap.put("fCO2FromPCO2TEquNcep", "fCO2_from_pCO2_water_water_equi_temp_ncep");
		shortNameMap.put("fCO2FromPCO2SstNcep", "fCO2_from_pCO2_water_sst_100humidity_uatm_ncep");
		shortNameMap.put("fCO2FromXCO2TEquWoa", "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_woa");
		shortNameMap.put("fCO2FromXCO2SstWoa", "fCO2_insitu_from_xCO2_water_sst_dry_ppm_woa");
		shortNameMap.put("fCO2FromXCO2TEquNcep", "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep");
		shortNameMap.put("fCO2FromXCO2SstNcep", "fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep");
		shortNameMap.put("fCO2FromXCO2TEquNcepWoa", "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep_woa");
		shortNameMap.put("fCO2FromXCO2SstNcepWoa", "fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep_woa");

		shortNameMap.put("fCO2Rec", "fCO2_recommended");
		shortNameMap.put("fCO2Source", "fCO2_source");
		shortNameMap.put("deltaT", "delta_temp");
		shortNameMap.put("regionID", "region_id");
		shortNameMap.put("calcSpeed", "calc_speed");
		shortNameMap.put("etopo2Depth", "etopo2");
		shortNameMap.put("gvCO2", "gvCO2");
		shortNameMap.put("distToLand", "dist_to_land");
		shortNameMap.put("days1970", "days_1970");
		shortNameMap.put("dayOfYear", "day_of_year");

		shortNameMap.put("fCO2FromXCO2TEquWoce", "WOCE_fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm");
		shortNameMap.put("fCO2FromXCO2SstWoce", "WOCE_fCO2_insitu_from_xCO2_water_sst_dry_ppm");
		shortNameMap.put("fCO2FromPCO2TEquWoce", "WOCE_fCO2_from_pCO2_water_water_equi_temp");
		shortNameMap.put("fCO2FromPCO2SstWoce", "WOCE_fCO2_from_pCO2_water_sst_100humidity_uatm");
		shortNameMap.put("fCO2FromFCO2TEquWoce", "WOCE_fCO2_insitu_from_fCO2_water_equi_uatm");
		shortNameMap.put("fCO2FromFCO2SstWoce", "WOCE_fCO2_insitu_from_fCO2_water_sst_100humidty_uatm");
		shortNameMap.put("fCO2FromPCO2TEquNcepWoce", "WOCE_fCO2_from_pCO2_water_water_equi_temp_ncep");
		shortNameMap.put("fCO2FromPCO2SstNcepWoce", "WOCE_fCO2_from_pCO2_water_sst_100humidity_uatm_ncep");
		shortNameMap.put("fCO2FromXCO2TEquWoaWoce", "WOCE_fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_woa");
		shortNameMap.put("fCO2FromXCO2SstWoaWoce", "WOCE_fCO2_insitu_from_xCO2_water_sst_dry_ppm_woa");
		shortNameMap.put("fCO2FromXCO2TEquNcepWoce", "WOCE_fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep");
		shortNameMap.put("fCO2FromXCO2SstNcepWoce", "WOCE_fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep");
		shortNameMap.put("fCO2FromXCO2TEquNcepWoaWoce", "WOCE_fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep_woa");
		shortNameMap.put("fCO2FromXCO2SstNcepWoaWoce", "WOCE_fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep_woa");
		shortNameMap.put("fCO2RecWoce", "WOCE_fCO2_recommended");
		shortNameMap.put("deltaTWoce", "WOCE_delta_temp");
		shortNameMap.put("calcSpeedWoce", "WOCE_calc_speed");

		SHORT_NAME = Collections.unmodifiableMap(shortNameMap);
	}

	/**
	 *  Full length names of the variables for netCDF files
	 */
	public static final Map<String, String> LONG_NAME;
	static {
		HashMap<String, String> longNameMap = new HashMap<String, String>();

		longNameMap.put("expocode", "expocode");
		longNameMap.put("cruiseName", "dataset name");
		longNameMap.put("vesselName", "vessel name");
		longNameMap.put("organization", "organization");
		longNameMap.put("westmostLongitude", "westernmost longitude");
		longNameMap.put("eastmostLongitude", "easternmost longitude");
		longNameMap.put("southmostLatitude", "southernmost latitude");
		longNameMap.put("northmostLatitude", "northernmost latitude");
		longNameMap.put("beginTime", "beginning time");
		longNameMap.put("endTime", "ending time");
		longNameMap.put("scienceGroup", "investigators");
		longNameMap.put("origDataRef", "original data reference");
		longNameMap.put("addlDocs", "additional documents");
		longNameMap.put("socatDOI", "SOCAT data DOI");
		longNameMap.put("socatDOIHRef", "SOCAT data reference");
		longNameMap.put("socatVersion", "SOCAT version");
		longNameMap.put("qcFlag", "QC flag");

		longNameMap.put("year", "year");
		longNameMap.put("month", "month of year");
		longNameMap.put("day", "day of month");
		longNameMap.put("hour", "hour of day");
		longNameMap.put("minute", "minute of hour");
		longNameMap.put("second", "second of minute");

		longNameMap.put("longitude", "longitude");
		longNameMap.put("latitude", "latitude");
		longNameMap.put("sampleDepth", "sample depth");
		longNameMap.put("salinity", "salinity");
		longNameMap.put("tEqu", "equilibrator chamber temperature");
		longNameMap.put("sst", "sea surface temperature");
		longNameMap.put("pEqu", "equilibrator chamber pressure");
		longNameMap.put("slp", "sea-level air pressure");

		longNameMap.put("xCO2WaterTEqu", "water xCO2 using equi temp");
		longNameMap.put("xCO2WaterSst", "water xCO2 using sst");
		longNameMap.put("pCO2WaterTEqu", "water pCO2 using equi temp");
		longNameMap.put("pCO2WaterSst", "water pCO2 using sst");
		longNameMap.put("fCO2WaterTEqu", "water fCO2 using equi temp");
		longNameMap.put("fCO2WaterSst", "water fCO2 using sst");

		longNameMap.put("xCO2Atm", "atmospheric xCO2");
		longNameMap.put("pCO2Atm", "atmospheric pCO2");
		longNameMap.put("fCO2Atm", "atmospheric fCO2");
		longNameMap.put("deltaXCO2", "water xCO2 minus atmospheric xCO2");
		longNameMap.put("deltaPCO2", "water pCO2 minus atmospheric pCO2");
		longNameMap.put("deltaFCO2", "water fCO2 minus atmospheric fCO2");

		longNameMap.put("relativeHumidity", "relative humidity");
		longNameMap.put("specificHumidity", "specific humidity");
		longNameMap.put("shipSpeed", "measured ship speed");
		longNameMap.put("shipDirection", "ship direction"); 
		longNameMap.put("windSpeedTrue", "true wind speed");
		longNameMap.put("windSpeedRelative", "relative wind speed");
		longNameMap.put("windDirectionTrue", "true wind direction");
		longNameMap.put("windDirectionRelative", "relative wind direction");

		longNameMap.put("geopositionWoce", "WOCE flag for date, time, longitude, latitude");
		longNameMap.put("sampleDepthWoce", "WOCE flag for sample depth");
		longNameMap.put("salinityWoce", "WOCE flag for salinity");
		longNameMap.put("tEquWoce", "WOCE flag for equilibrator temperature");
		longNameMap.put("sstWoce", "WOCE flag for sea surface temperature");
		longNameMap.put("pEquWoce", "WOCE flag for equilibrator pressure");
		longNameMap.put("slpWoce", "WOCE flag for sea-level pressure");

		longNameMap.put("xCO2WaterTEquWoce", "WOCE flag for water xCO2 using equi temp");
		longNameMap.put("xCO2WaterSstWoce", "WOCE flag for water xCO2 using sst");
		longNameMap.put("pCO2WaterTEquWoce", "WOCE flag for water pCO2 using equi temp");
		longNameMap.put("pCO2WaterSstWoce", "WOCE flag for water pCO2 using sst");
		longNameMap.put("fCO2WaterTEquWoce", "WOCE flag for water fCO2 using equi temp");
		longNameMap.put("fCO2WaterSstWoce", "WOCE flag for water fCO2 using sst");

		longNameMap.put("xCO2AtmWoce", "WOCE flag for atmospheric xCO2");
		longNameMap.put("pCO2AtmWoce", "WOCE flag for atmospheric pCO2");
		longNameMap.put("fCO2AtmWoce", "WOCE flag for atmospheric fCO2");
		longNameMap.put("deltaXCO2Woce", "WOCE flag for delta_xCO2");
		longNameMap.put("deltaPCO2Woce", "WOCE flag for delta_pCO2");
		longNameMap.put("deltaFCO2Woce", "WOCE flag for delta_fCO2");

		longNameMap.put("relativeHumidityWoce", "WOCE flag for relative humidity");
		longNameMap.put("specificHumidityWoce", "WOCE flag for specific humidity");
		longNameMap.put("shipSpeedWoce", "WOCE flag for measured ship speed");
		longNameMap.put("shipDirectionWoce", "WOCE flag for ship direction"); 
		longNameMap.put("windSpeedTrueWoce", "WOCE flag for true wind speed");
		longNameMap.put("windSpeedRelativeWoce", "WOCE flag for relative wind speed");
		longNameMap.put("windDirectionTrueWoce", "WOCE flag for true wind direction");
		longNameMap.put("windDirectionRelativeWoce", "WOCE flag for relative wind direction");

		longNameMap.put("woaSss", "salinity from World Ocean Atlas 2005");
		longNameMap.put("ncepSlp", "sea level air pressure from NCEP/NCAR reanalysis");

		longNameMap.put("fCO2FromXCO2TEqu", "fco2 from xCO2_water_equi_temp_dry_ppm, Pressure_equi, sal");
		longNameMap.put("fCO2FromXCO2Sst", "fco2 from xCO2_water_sst_dry_ppm, Pressure_equi, sal");
		longNameMap.put("fCO2FromPCO2TEqu", "fco2 from pCO2_water_equi_temp, Pressure_equi, sal");
		longNameMap.put("fCO2FromPCO2Sst", "fco2 from pCO2_water_sst_100humidity_uatm, Pressure_equi, sal");
		longNameMap.put("fCO2FromFCO2TEqu", "fco2 from fCO2_water_equi_temp, Pressure_equi, sal");
		longNameMap.put("fCO2FromFCO2Sst", "fco2 from fCO2_water_sst_100humidity_uatm, Pressure_equi, sal");
		longNameMap.put("fCO2FromPCO2TEquNcep", "fco2 from pCO2_water_equi_temp, NCEP SLP, sal");
		longNameMap.put("fCO2FromPCO2SstNcep", "fco2 from pCO2_water_sst_100humidity_uatm, NCEP SLP, sal");
		longNameMap.put("fCO2FromXCO2TEquWoa", "fco2 from xCO2_water_equi_temp_dry_ppm, Pressure_equi, WOA SSS");
		longNameMap.put("fCO2FromXCO2SstWoa", "fco2 from xCO2_water_sst_dry_ppm, Pressure_equi, WOA SSS");
		longNameMap.put("fCO2FromXCO2TEquNcep", "fco2 from xCO2_water_equi_temp_dry_ppm, NCEP SLP, sal");
		longNameMap.put("fCO2FromXCO2SstNcep", "fco2 from xCO2_water_sst_dry_ppm, NCEP SLP, sal");
		longNameMap.put("fCO2FromXCO2TEquNcepWoa", "fco2 from xCO2_water_equi_temp_dry_ppm, NCEP SLP, WOA SSS");
		longNameMap.put("fCO2FromXCO2SstNcepWoa", "fco2 from xCO2_water_sst_dry_ppm, NCEP SLP, WOA SSS");

		longNameMap.put("fCO2Rec", "fCO2 recommended");
		longNameMap.put("fCO2Source", "Algorithm number for recommended fCO2");
		longNameMap.put("deltaT", "Equilibrator Temp - SST");
		longNameMap.put("regionID", "SOCAT region ID");
		longNameMap.put("calcSpeed", "calculated ship speed");
		longNameMap.put("etopo2Depth", "bathymetry from ETOPO2");
		longNameMap.put("gvCO2", "GlobalView xCO2");
		longNameMap.put("distToLand", "distance to land");
		longNameMap.put("days1970", "time");
		longNameMap.put("dayOfYear", "day of the year");

		longNameMap.put("fCO2FromXCO2TEquWoce", "WOCE flag for fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm");
		longNameMap.put("fCO2FromXCO2SstWoce", "WOCE flag for fCO2_insitu_from_xCO2_water_sst_dry_ppm");
		longNameMap.put("fCO2FromPCO2TEquWoce", "WOCE flag for fCO2_from_pCO2_water_water_equi_temp");
		longNameMap.put("fCO2FromPCO2SstWoce", "WOCE flag for fCO2_from_pCO2_water_sst_100humidity_uatm");
		longNameMap.put("fCO2FromFCO2TEquWoce", "WOCE flag for fCO2_insitu_from_fCO2_water_equi_uatm");
		longNameMap.put("fCO2FromFCO2SstWoce", "WOCE flag for fCO2_insitu_from_fCO2_water_sst_100humidty_uatm");
		longNameMap.put("fCO2FromPCO2TEquNcepWoce", "WOCE flag for fCO2_from_pCO2_water_water_equi_temp_ncep");
		longNameMap.put("fCO2FromPCO2SstNcepWoce", "WOCE flag for fCO2_from_pCO2_water_sst_100humidity_uatm_ncep");
		longNameMap.put("fCO2FromXCO2TEquWoaWoce", "WOCE flag for fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_woa");
		longNameMap.put("fCO2FromXCO2SstWoaWoce", "WOCE flag for fCO2_insitu_from_xCO2_water_sst_dry_ppm_woa");
		longNameMap.put("fCO2FromXCO2TEquNcepWoce", "WOCE flag for fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep");
		longNameMap.put("fCO2FromXCO2SstNcepWoce", "WOCE flag for fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep");
		longNameMap.put("fCO2FromXCO2TEquNcepWoaWoce", "WOCE flag for fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep_woa");
		longNameMap.put("fCO2FromXCO2SstNcepWoaWoce", "WOCE flag for fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep_woa");
	
		longNameMap.put("fCO2RecWoce", "WOCE flag for fCO2_recommended");
		longNameMap.put("deltaTWoce", "WOCE flag for delta_temp");
		longNameMap.put("calcSpeedWoce", "WOCE flag for calculated ship speed");

		LONG_NAME = Collections.unmodifiableMap(longNameMap);
	}

	/**
	 * Standard units of each variable appropriate for netCDF files
	 */
	public static final Map<String, String> UNITS;
	static {
		final String longitude_units = "degrees_east";
		final String latitude_units = "degrees_north";
		final String depth_units = "meters";
		final String temperature_units = "degrees C";
		final String salinity_units = "PSU";
		final String pressure_units = "hPa";
		final String xco2_units = "umol/mol";
		final String pco2_units = "uatm";
		final String fco2_units = "uatm";
		final String ship_speed_units = "knots";
		final String wind_speed_units = "m/s";
		final String direction_units = "degrees";
		final String distance_units = "km";
		final String seconds_time_units = "seconds since 1970-01-01T00:00:00Z";
		final String day_time_units = "days since 1970-01-01T00:00:00Z";
		final String days_units = "days";

		HashMap<String, String> unitsMap = new HashMap<String, String>();
		unitsMap.put("westmostLongitude", longitude_units);
		unitsMap.put("eastmostLongitude", longitude_units);
		unitsMap.put("southmostLatitude", latitude_units);
		unitsMap.put("northmostLatitude", latitude_units);
		unitsMap.put("beginTime", seconds_time_units);
		unitsMap.put("endTime", seconds_time_units);

		unitsMap.put("longitude", longitude_units);
		unitsMap.put("latitude", latitude_units);
		unitsMap.put("sampleDepth", depth_units);
		unitsMap.put("salinity", salinity_units);
		unitsMap.put("tEqu", temperature_units);
		unitsMap.put("sst", temperature_units);
		unitsMap.put("pEqu", pressure_units);
		unitsMap.put("slp", pressure_units);

		unitsMap.put("xCO2WaterTEqu", xco2_units);
		unitsMap.put("xCO2WaterSst", xco2_units);
		unitsMap.put("pCO2WaterTEqu", pco2_units);
		unitsMap.put("pCO2WaterSst", pco2_units);
		unitsMap.put("fCO2WaterTEqu", fco2_units);
		unitsMap.put("fCO2WaterSst", fco2_units);

		unitsMap.put("xCO2Atm", xco2_units);
		unitsMap.put("pCO2Atm", pco2_units);
		unitsMap.put("fCO2Atm", fco2_units);
		unitsMap.put("deltaXCO2", xco2_units);
		unitsMap.put("deltaPCO2", pco2_units);
		unitsMap.put("deltaFCO2", fco2_units);

		unitsMap.put("shipSpeed", ship_speed_units);
		unitsMap.put("shipDirection", direction_units); 
		unitsMap.put("windSpeedTrue", wind_speed_units);
		unitsMap.put("windSpeedRelative", wind_speed_units);
		unitsMap.put("windDirectionTrue", direction_units);
		unitsMap.put("windDirectionRelative", direction_units);

		unitsMap.put("woaSss", salinity_units);
		unitsMap.put("ncepSlp", pressure_units);

		unitsMap.put("fCO2FromXCO2TEqu", fco2_units);
		unitsMap.put("fCO2FromXCO2Sst", fco2_units);
		unitsMap.put("fCO2FromPCO2TEqu", fco2_units);
		unitsMap.put("fCO2FromPCO2Sst", fco2_units);
		unitsMap.put("fCO2FromFCO2TEqu", fco2_units);
		unitsMap.put("fCO2FromFCO2Sst", fco2_units);
		unitsMap.put("fCO2FromPCO2TEquNcep", fco2_units);
		unitsMap.put("fCO2FromPCO2SstNcep", fco2_units);
		unitsMap.put("fCO2FromXCO2TEquWoa", fco2_units);
		unitsMap.put("fCO2FromXCO2SstWoa", fco2_units);
		unitsMap.put("fCO2FromXCO2TEquNcep", fco2_units);
		unitsMap.put("fCO2FromXCO2SstNcep", fco2_units);
		unitsMap.put("fCO2FromXCO2TEquNcepWoa", fco2_units);
		unitsMap.put("fCO2FromXCO2SstNcepWoa", fco2_units);

		unitsMap.put("fCO2Rec", fco2_units);
		unitsMap.put("deltaT", temperature_units);
		unitsMap.put("calcSpeed", ship_speed_units);
		unitsMap.put("etopo2Depth", depth_units);
		unitsMap.put("gvCO2", xco2_units);
		unitsMap.put("distToLand", distance_units);
		unitsMap.put("days1970", day_time_units);
		unitsMap.put("dayOfYear", days_units);

		UNITS = Collections.unmodifiableMap(unitsMap);
	}

	/**
	 * Standardized names for netCDF files
	 */
	public static final Map<String, String> STANDARD_NAMES;
	static {
		HashMap<String, String> stdNamesMap = new HashMap<String, String>();

		stdNamesMap.put("vesselName", "platform_name");
		stdNamesMap.put("westmostLongitude", "geospatial_lon_min");
		stdNamesMap.put("eastmostLongitude", "geospatial_lon_max");
		stdNamesMap.put("southmostLatitude", "geospatial_lat_min");
		stdNamesMap.put("northmostLatitude", "geospatial_lat_max");
		stdNamesMap.put("beginTime", "time_coverage_start");
		stdNamesMap.put("endTime", "time_converage_end");

		stdNamesMap.put("longitude", "longitude");
		stdNamesMap.put("latitude", "latitude");
		stdNamesMap.put("sampleDepth", "depth");
		stdNamesMap.put("salinity", "sea_surface_salinity");
		stdNamesMap.put("sst", "sea_surface_temperature");
		stdNamesMap.put("slp", "air_pressure_at_sea_level");

		stdNamesMap.put("xCO2WaterTEqu", "mole_fraction_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("xCO2WaterSst", "mole_fraction_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2WaterTEqu", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2WaterSst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("pCO2WaterTEqu", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("pCO2WaterSst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");

		stdNamesMap.put("xCO2Atm", "mole_fraction_of_carbon_dioxide_in_air");
		stdNamesMap.put("pCO2Atm", "surface_partial_pressure_of_carbon_dioxide_in_air");
		stdNamesMap.put("fCO2Atm", "surface_partial_pressure_of_carbon_dioxide_in_air");

		stdNamesMap.put("relativeHumidity", "relative_humidity");
		stdNamesMap.put("specificHumidity", "specific_humidity");
		stdNamesMap.put("shipSpeed", "platform_speed_wrt_ground"); 
		stdNamesMap.put("shipDirection", "platform_course");
		stdNamesMap.put("windSpeedTrue", "wind_speed");
		stdNamesMap.put("windSpeedRelative", "wind_speed");
		stdNamesMap.put("windDirectionTrue", "wind_from_direction");
		stdNamesMap.put("windDirectionRelative", "wind_from_direction");

		stdNamesMap.put("woaSss", "sea_surface_salinity");
		stdNamesMap.put("ncepSlp", "air_pressure_at_sea_level");

		stdNamesMap.put("fCO2FromXCO2TEqu", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromXCO2Sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromPCO2TEqu", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromPCO2Sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromFCO2TEqu", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromFCO2Sst", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromPCO2TEquNcep", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromPCO2SstNcep", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromXCO2TEquWoa", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromXCO2SstWoa", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromXCO2TEquNcep", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromXCO2SstNcep", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromXCO2TEquNcepWoa", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("fCO2FromXCO2SstNcepWoa", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");

		stdNamesMap.put("fCO2Rec", "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put("calcSpeed", "platform_speed_wrt_ground");
		stdNamesMap.put("etopo2Depth", "sea_floor_depth");
		stdNamesMap.put("gvCO2", "mole_fraction_of_carbon_dioxide_in_air");
		stdNamesMap.put("days1970", "time");

		STANDARD_NAMES = Collections.unmodifiableMap(stdNamesMap);
	}

	/**
	 * Variable names for netCDF files
	 */
	public static final Map<String, String> IOOS_CATEGORIES;
	static {
		String bathymetry_category = "Bathymetry";
		String co2_category = "CO2";
		// TODO: check humidity category name
		String humidity_category = "Humidity";
		String identifier_category = "Identifier";
		String location_category = "Location";
		String pressure_category = "Pressure";
		String quality_category = "Quality";
		String salinity_category = "Salinity";
		String temperature_category = "Temperature";
		String time_category = "Time";
		String wind_category = "Wind";

		HashMap<String, String> ioosCatMap = new HashMap<String, String>();

		ioosCatMap.put("expocode", identifier_category);
		ioosCatMap.put("cruiseName", identifier_category);
		ioosCatMap.put("vesselName", identifier_category);
		ioosCatMap.put("organization", identifier_category);
		ioosCatMap.put("westmostLongitude", location_category);
		ioosCatMap.put("eastmostLongitude", location_category);
		ioosCatMap.put("southmostLatitude", location_category);
		ioosCatMap.put("northmostLatitude", location_category);
		ioosCatMap.put("beginTime", time_category);
		ioosCatMap.put("endTime", time_category);
		ioosCatMap.put("scienceGroup", identifier_category);
		ioosCatMap.put("origDataRef", identifier_category);
		ioosCatMap.put("socatDOI", identifier_category);
		ioosCatMap.put("socatDOIHRef", identifier_category);
		ioosCatMap.put("socatVersion", identifier_category);
		ioosCatMap.put("qcFlag", quality_category);

		ioosCatMap.put("year", time_category);
		ioosCatMap.put("month", time_category);
		ioosCatMap.put("day", time_category);
		ioosCatMap.put("hour", time_category);
		ioosCatMap.put("minute", time_category);
		ioosCatMap.put("second", time_category);

		ioosCatMap.put("longitude", location_category);
		ioosCatMap.put("latitude", location_category);
		ioosCatMap.put("sampleDepth", bathymetry_category);
		ioosCatMap.put("salinity", salinity_category);
		ioosCatMap.put("tEqu", temperature_category);
		ioosCatMap.put("sst", temperature_category);
		ioosCatMap.put("pEqu", pressure_category);
		ioosCatMap.put("slp", pressure_category);

		ioosCatMap.put("xCO2WaterSst", co2_category);
		ioosCatMap.put("xCO2WaterTEqu", co2_category);
		ioosCatMap.put("fCO2WaterSst", co2_category);
		ioosCatMap.put("fCO2WaterTEqu", co2_category);
		ioosCatMap.put("pCO2WaterSst", co2_category);
		ioosCatMap.put("pCO2WaterTEqu", co2_category);

		ioosCatMap.put("xCO2Atm", co2_category);
		ioosCatMap.put("pCO2Atm", co2_category);
		ioosCatMap.put("fCO2Atm", co2_category);
		ioosCatMap.put("deltaXCO2", co2_category);
		ioosCatMap.put("deltaPCO2", co2_category);
		ioosCatMap.put("deltaFCO2", co2_category);

		ioosCatMap.put("relativeHumidity", humidity_category);
		ioosCatMap.put("specificHumidity", humidity_category);
		ioosCatMap.put("windSpeedTrue", wind_category);
		ioosCatMap.put("windSpeedRelative", wind_category);
		ioosCatMap.put("windDirectionTrue", wind_category);
		ioosCatMap.put("windDirectionRelative", wind_category);

		ioosCatMap.put("geopositionWoce", quality_category);
		ioosCatMap.put("sampleDepthWoce", quality_category);
		ioosCatMap.put("salinityWoce", quality_category);
		ioosCatMap.put("tEquWoce", quality_category);
		ioosCatMap.put("sstWoce", quality_category);
		ioosCatMap.put("pEquWoce", quality_category);
		ioosCatMap.put("slpWoce", quality_category);

		ioosCatMap.put("xCO2WaterTEquWoce", quality_category);
		ioosCatMap.put("xCO2WaterSstWoce", quality_category);
		ioosCatMap.put("pCO2WaterTEquWoce", quality_category);
		ioosCatMap.put("pCO2WaterSstWoce", quality_category);
		ioosCatMap.put("fCO2WaterTEquWoce", quality_category);
		ioosCatMap.put("fCO2WaterSstWoce", quality_category);

		ioosCatMap.put("xCO2AtmWoce", quality_category);
		ioosCatMap.put("pCO2AtmWoce", quality_category);
		ioosCatMap.put("fCO2AtmWoce", quality_category);
		ioosCatMap.put("deltaXCO2Woce", quality_category);
		ioosCatMap.put("deltaPCO2Woce", quality_category);
		ioosCatMap.put("deltaFCO2Woce", quality_category);

		ioosCatMap.put("relativeHumidityWoce", quality_category);
		ioosCatMap.put("specificHumidityWoce", quality_category);
		ioosCatMap.put("shipSpeedWoce", quality_category);
		ioosCatMap.put("shipDirectionWoce", quality_category);
		ioosCatMap.put("windSpeedTrueWoce", quality_category);
		ioosCatMap.put("windSpeedRelativeWoce", quality_category);
		ioosCatMap.put("windDirectionTrueWoce", quality_category);
		ioosCatMap.put("windDirectionRelativeWoce", quality_category);

		ioosCatMap.put("woaSss", salinity_category);
		ioosCatMap.put("ncepSlp", pressure_category);

		ioosCatMap.put("fCO2FromXCO2TEqu", co2_category);
		ioosCatMap.put("fCO2FromXCO2Sst", co2_category);
		ioosCatMap.put("fCO2FromPCO2TEqu", co2_category);
		ioosCatMap.put("fCO2FromPCO2Sst", co2_category);
		ioosCatMap.put("fCO2FromFCO2TEqu", co2_category);
		ioosCatMap.put("fCO2FromFCO2Sst", co2_category);
		ioosCatMap.put("fCO2FromPCO2TEquNcep", co2_category);
		ioosCatMap.put("fCO2FromPCO2SstNcep", co2_category);
		ioosCatMap.put("fCO2FromXCO2TEquWoa", co2_category);
		ioosCatMap.put("fCO2FromXCO2SstWoa", co2_category);
		ioosCatMap.put("fCO2FromXCO2TEquNcep", co2_category);
		ioosCatMap.put("fCO2FromXCO2SstNcep", co2_category);
		ioosCatMap.put("fCO2FromXCO2TEquNcepWoa", co2_category);
		ioosCatMap.put("fCO2FromXCO2SstNcepWoa", co2_category);

		ioosCatMap.put("fCO2Rec", co2_category);
		ioosCatMap.put("fCO2Source", identifier_category);
		ioosCatMap.put("deltaT", temperature_category);
		ioosCatMap.put("regionID", identifier_category);
		ioosCatMap.put("etopo2Depth", bathymetry_category);
		ioosCatMap.put("gvCO2", co2_category);
		ioosCatMap.put("distToLand", location_category);
		ioosCatMap.put("days1970", time_category);
		ioosCatMap.put("dayOfYear", time_category);

		ioosCatMap.put("fCO2FromXCO2TEquWoce", quality_category);
		ioosCatMap.put("fCO2FromXCO2SstWoce", quality_category);
		ioosCatMap.put("fCO2FromPCO2TEquWoce", quality_category);
		ioosCatMap.put("fCO2FromPCO2SstWoce", quality_category);
		ioosCatMap.put("fCO2FromFCO2TEquWoce", quality_category);
		ioosCatMap.put("fCO2FromFCO2SstWoce", quality_category);
		ioosCatMap.put("fCO2FromPCO2TEquNcepWoce", quality_category);
		ioosCatMap.put("fCO2FromPCO2SstNcepWoce", quality_category);
		ioosCatMap.put("fCO2FromXCO2TEquWoaWoce", quality_category);
		ioosCatMap.put("fCO2FromXCO2SstWoaWoce", quality_category);
		ioosCatMap.put("fCO2FromXCO2TEquNcepWoce", quality_category);
		ioosCatMap.put("fCO2FromXCO2SstNcepWoce", quality_category);
		ioosCatMap.put("fCO2FromXCO2TEquNcepWoaWoce", quality_category);
		ioosCatMap.put("fCO2FromXCO2SstNcepWoaWoce", quality_category);

		ioosCatMap.put("fCO2RecWoce", quality_category);
		ioosCatMap.put("deltaTWoce", quality_category);
		ioosCatMap.put("calcSpeedWoce", quality_category);

		IOOS_CATEGORIES = Collections.unmodifiableMap(ioosCatMap);
	}

	public static final Character NORTH_PACIFIC_REGION_ID = 'N';
	public static final Character TROPICAL_PACIFIC_REGION_ID = 'T';
	public static final Character NORTH_ATLANTIC_REGION_ID = 'A';
	public static final Character TROPICAL_ATLANTIC_REGION_ID = 'Z';
	public static final Character INDIAN_REGION_ID = 'I';
	public static final Character COASTAL_REGION_ID = 'C';
	public static final Character SOUTHERN_OCEANS_REGION_ID = 'O';
	public static final Character ARCTIC_REGION_ID = 'R';
	public static final Character GLOBAL_REGION_ID = 'G';

	public static final Map<Character,String> REGION_NAMES;
	static {
		HashMap<Character,String> regionNamesMap = new HashMap<Character,String>();
		regionNamesMap.put(SocatCruiseData.CHAR_MISSING_VALUE, "Unknown");
		regionNamesMap.put(NORTH_PACIFIC_REGION_ID, "North Pacific");
		regionNamesMap.put(TROPICAL_PACIFIC_REGION_ID, "Tropical Pacific");
		regionNamesMap.put(NORTH_ATLANTIC_REGION_ID, "North Atlantic");
		regionNamesMap.put(TROPICAL_ATLANTIC_REGION_ID, "Tropical Atlantic");
		regionNamesMap.put(INDIAN_REGION_ID, "Indian");
		regionNamesMap.put(COASTAL_REGION_ID, "Coastal");
		regionNamesMap.put(SOUTHERN_OCEANS_REGION_ID, "Southern Oceans");
		regionNamesMap.put(ARCTIC_REGION_ID, "Artic");
		regionNamesMap.put(GLOBAL_REGION_ID, "Global");
		REGION_NAMES = Collections.unmodifiableMap(regionNamesMap);
	}

}
