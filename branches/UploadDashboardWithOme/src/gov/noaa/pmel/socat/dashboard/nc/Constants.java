package gov.noaa.pmel.socat.dashboard.nc;

import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Constant maps of SocatCruiseData variable names to short names, 
 * units, and long names for the NetCDF DSG files.
 */
public class Constants {

	public static final String expocode_VARNAME = "expocode";
	public static final String cruiseName_VARNAME = "cruiseName";
	public static final String vesselName_VARNAME = "vesselName";
	public static final String organization_VARNAME = "organization";
	public static final String westmostLongitude_VARNAME = "westmostLongitude";
	public static final String eastmostLongitude_VARNAME = "eastmostLongitude";
	public static final String southmostLatitude_VARNAME = "southmostLatitude";
	public static final String northmostLatitude_VARNAME = "northmostLatitude";
	public static final String beginTime_VARNAME = "beginTime";
	public static final String endTime_VARNAME = "endTime";
	public static final String scienceGroup_VARNAME = "scienceGroup";
	public static final String origDataRef_VARNAME = "origDataRef";
	public static final String addlDocs_VARNAME = "addlDocs";
	public static final String socatDOI_VARNAME = "socatDOI";
	public static final String socatDOIHRef_VARNAME = "socatDOIHRef";
	public static final String socatVersion_VARNAME = "socatVersion";
	public static final String qcFlag_VARNAME = "qcFlag";

	public static final String rowNum_VARNAME = "rowNum";
	public static final String year_VARNAME = "year";
	public static final String month_VARNAME = "month";
	public static final String day_VARNAME = "day";
	public static final String hour_VARNAME = "hour";
	public static final String minute_VARNAME = "minute";
	public static final String second_VARNAME = "second";
	public static final String time_VARNAME = "time";

	public static final String longitude_VARNAME = "longitude";
	public static final String latitude_VARNAME = "latitude";
	public static final String sampleDepth_VARNAME = "sampleDepth";
	public static final String salinity_VARNAME = "salinity";
	public static final String tEqu_VARNAME = "tEqu";
	public static final String sst_VARNAME = "sst";
	public static final String tAtm_VARNAME = "tAtm";
	public static final String pEqu_VARNAME = "pEqu";
	public static final String slp_VARNAME = "slp";

	public static final String xCO2WaterTEquDry_VARNAME = "xCO2WaterTEquDry";
	public static final String xCO2WaterSstDry_VARNAME = "xCO2WaterSstDry";
	public static final String xCO2WaterTEquWet_VARNAME = "xCO2WaterTEquWet";
	public static final String xCO2WaterSstWet_VARNAME = "xCO2WaterSstWet";
	public static final String pCO2WaterTEquWet_VARNAME = "pCO2WaterTEquWet";
	public static final String pCO2WaterSstWet_VARNAME = "pCO2WaterSstWet";
	public static final String fCO2WaterTEquWet_VARNAME = "fCO2WaterTEquWet";
	public static final String fCO2WaterSstWet_VARNAME = "fCO2WaterSstWet";

	public static final String xCO2AtmDryActual_VARNAME = "xCO2AtmDryActual";
	public static final String xCO2AtmDryInterp_VARNAME = "xCO2AtmDryInterp";
	public static final String pCO2AtmWetActual_VARNAME = "pCO2AtmWetActual";
	public static final String pCO2AtmWetInterp_VARNAME = "pCO2AtmWetInterp";
	public static final String fCO2AtmWetActual_VARNAME = "fCO2AtmWetActual";
	public static final String fCO2AtmWetInterp_VARNAME = "fCO2AtmWetInterp";

	public static final String deltaXCO2_VARNAME = "deltaXCO2";
	public static final String deltaPCO2_VARNAME = "deltaPCO2";
	public static final String deltaFCO2_VARNAME = "deltaFCO2";

	public static final String xH2OEqu_VARNAME = "xH2OEqu";
	public static final String relativeHumidity_VARNAME = "relativeHumidity";
	public static final String specificHumidity_VARNAME = "specificHumidity";
	public static final String shipSpeed_VARNAME = "shipSpeed"; 
	public static final String shipDirection_VARNAME = "shipDirection";
	public static final String windSpeedTrue_VARNAME = "windSpeedTrue";
	public static final String windSpeedRelative_VARNAME = "windSpeedRelative";
	public static final String windDirectionTrue_VARNAME = "windDirectionTrue";
	public static final String windDirectionRelative_VARNAME = "windDirectionRelative";

	public static final String woceCO2Water_VARNAME = "woceCO2Water";
	public static final String woceCO2Atm_VARNAME = "woceCO2Atm";

	public static final String woaSss_VARNAME = "woaSss";
	public static final String ncepSlp_VARNAME = "ncepSlp";

	public static final String fCO2FromXCO2TEqu_VARNAME = "fCO2FromXCO2TEqu";
	public static final String fCO2FromXCO2Sst_VARNAME = "fCO2FromXCO2Sst";
	public static final String fCO2FromPCO2TEqu_VARNAME = "fCO2FromPCO2TEqu";
	public static final String fCO2FromPCO2Sst_VARNAME = "fCO2FromPCO2Sst";
	public static final String fCO2FromFCO2TEqu_VARNAME = "fCO2FromFCO2TEqu";
	public static final String fCO2FromFCO2Sst_VARNAME = "fCO2FromFCO2Sst";
	public static final String fCO2FromPCO2TEquNcep_VARNAME = "fCO2FromPCO2TEquNcep";
	public static final String fCO2FromPCO2SstNcep_VARNAME = "fCO2FromPCO2SstNcep";
	public static final String fCO2FromXCO2TEquWoa_VARNAME = "fCO2FromXCO2TEquWoa";
	public static final String fCO2FromXCO2SstWoa_VARNAME = "fCO2FromXCO2SstWoa";
	public static final String fCO2FromXCO2TEquNcep_VARNAME = "fCO2FromXCO2TEquNcep";
	public static final String fCO2FromXCO2SstNcep_VARNAME = "fCO2FromXCO2SstNcep";
	public static final String fCO2FromXCO2TEquNcepWoa_VARNAME = "fCO2FromXCO2TEquNcepWoa";
	public static final String fCO2FromXCO2SstNcepWoa_VARNAME = "fCO2FromXCO2SstNcepWoa";

	public static final String fCO2Rec_VARNAME = "fCO2Rec";
	public static final String fCO2Source_VARNAME = "fCO2Source";
	public static final String deltaT_VARNAME = "deltaT";
	public static final String regionID_VARNAME = "regionID";
	public static final String calcSpeed_VARNAME = "calcSpeed";
	public static final String etopo2Depth_VARNAME = "etopo2Depth";
	public static final String gvCO2_VARNAME = "gvCO2";
	public static final String distToLand_VARNAME = "distToLand";
	public static final String dayOfYear_VARNAME = "dayOfYear";

	public static final String geoposition_VARNAME = "geoposition";

	/**
	 * Variable names for netCDF files
	 */
	public static final Map<String, String> SHORT_NAMES;
	static {
		HashMap<String, String> shortNameMap = new HashMap<String, String>();

		shortNameMap.put(expocode_VARNAME, "expocode");
		shortNameMap.put(cruiseName_VARNAME, "dataset_name");
		shortNameMap.put(vesselName_VARNAME, "vessel_name");
		shortNameMap.put(organization_VARNAME, "organization");
		shortNameMap.put(westmostLongitude_VARNAME, "geospatial_lon_min");
		shortNameMap.put(eastmostLongitude_VARNAME, "geospatial_lon_max");
		shortNameMap.put(southmostLatitude_VARNAME, "geospatial_lat_min");
		shortNameMap.put(northmostLatitude_VARNAME, "geospatial_lat_max");
		shortNameMap.put(beginTime_VARNAME, "time_coverage_start");
		shortNameMap.put(endTime_VARNAME, "time_converage_end");
		shortNameMap.put(scienceGroup_VARNAME, "investigators");
		shortNameMap.put(origDataRef_VARNAME, "orig_data_ref");
		shortNameMap.put(addlDocs_VARNAME, "addl_docs");
		shortNameMap.put(socatDOI_VARNAME, "socat_data_doi");
		shortNameMap.put(socatDOIHRef_VARNAME, "socat_data_ref");
		shortNameMap.put(socatVersion_VARNAME, "socat_version");
		shortNameMap.put(qcFlag_VARNAME, "qc_flag");

		shortNameMap.put(rowNum_VARNAME, "sample_number");
		shortNameMap.put(year_VARNAME, "year");
		shortNameMap.put(month_VARNAME, "month");
		shortNameMap.put(day_VARNAME, "day");
		shortNameMap.put(hour_VARNAME, "hour");
		shortNameMap.put(minute_VARNAME, "minute");
		shortNameMap.put(second_VARNAME, "second");
		shortNameMap.put(time_VARNAME, "time");

		shortNameMap.put(longitude_VARNAME, "longitude");
		shortNameMap.put(latitude_VARNAME, "latitude");
		shortNameMap.put(sampleDepth_VARNAME, "sample_depth");
		shortNameMap.put(salinity_VARNAME, "sal");
		shortNameMap.put(tEqu_VARNAME, "Temperature_equi");
		shortNameMap.put(sst_VARNAME, "temp");
		shortNameMap.put(tAtm_VARNAME, "Temperature_atm");
		shortNameMap.put(pEqu_VARNAME, "Pressure_equi");
		shortNameMap.put(slp_VARNAME, "Pressure_atm");

		shortNameMap.put(xCO2WaterTEquDry_VARNAME, "xCO2_water_equi_temp_dry_ppm");
		shortNameMap.put(xCO2WaterSstDry_VARNAME, "xCO2_water_sst_dry_ppm");
		shortNameMap.put(xCO2WaterTEquWet_VARNAME, "xCO2_water_equi_temp_wet_ppm");
		shortNameMap.put(xCO2WaterSstWet_VARNAME, "xCO2_water_sst_wet_ppm");
		shortNameMap.put(pCO2WaterTEquWet_VARNAME, "pCO2_water_equi_temp");
		shortNameMap.put(pCO2WaterSstWet_VARNAME, "pCO2_water_sst_100humidity_uatm");
		shortNameMap.put(fCO2WaterTEquWet_VARNAME, "fCO2_water_equi_uatm");
		shortNameMap.put(fCO2WaterSstWet_VARNAME, "fCO2_water_sst_100humidity_uatm");

		shortNameMap.put(xCO2AtmDryActual_VARNAME, "xCO2_atm_dry_actual");
		shortNameMap.put(xCO2AtmDryInterp_VARNAME, "xCO2_atm_dry_interp");
		shortNameMap.put(pCO2AtmWetActual_VARNAME, "pCO2_atm_wet_actual");
		shortNameMap.put(pCO2AtmWetInterp_VARNAME, "pCO2_atm_wet_interp");
		shortNameMap.put(fCO2AtmWetActual_VARNAME, "fCO2_atm_wet_actual");
		shortNameMap.put(fCO2AtmWetInterp_VARNAME, "fCO2_atm_wet_interp");

		shortNameMap.put(deltaXCO2_VARNAME, "delta_xCO2");
		shortNameMap.put(deltaPCO2_VARNAME, "delta_pCO2");
		shortNameMap.put(deltaFCO2_VARNAME, "delta_fCO2");

		shortNameMap.put(xH2OEqu_VARNAME, "xH2O_equi");
		shortNameMap.put(relativeHumidity_VARNAME, "relative_humidity");
		shortNameMap.put(specificHumidity_VARNAME, "specific_humidity");
		shortNameMap.put(shipSpeed_VARNAME, "ship_speed"); 
		shortNameMap.put(shipDirection_VARNAME, "ship_dir");
		shortNameMap.put(windSpeedTrue_VARNAME, "wind_speed_true");
		shortNameMap.put(windSpeedRelative_VARNAME, "wind_speed_rel");
		shortNameMap.put(windDirectionTrue_VARNAME, "wind_dir_true");
		shortNameMap.put(windDirectionRelative_VARNAME, "wind_dir_rel");

		shortNameMap.put(woceCO2Water_VARNAME, "WOCE_CO2_water");
		shortNameMap.put(woceCO2Atm_VARNAME, "WOCE_CO2_atm");

		shortNameMap.put(woaSss_VARNAME, "woa_sss");
		shortNameMap.put(ncepSlp_VARNAME, "pressure_ncep_slp");

		shortNameMap.put(fCO2FromXCO2TEqu_VARNAME, "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm");
		shortNameMap.put(fCO2FromXCO2Sst_VARNAME, "fCO2_insitu_from_xCO2_water_sst_dry_ppm");
		shortNameMap.put(fCO2FromPCO2TEqu_VARNAME, "fCO2_from_pCO2_water_water_equi_temp");
		shortNameMap.put(fCO2FromPCO2Sst_VARNAME, "fCO2_from_pCO2_water_sst_100humidity_uatm");
		shortNameMap.put(fCO2FromFCO2TEqu_VARNAME, "fCO2_insitu_from_fCO2_water_equi_uatm");
		shortNameMap.put(fCO2FromFCO2Sst_VARNAME, "fCO2_insitu_from_fCO2_water_sst_100humidty_uatm");
		shortNameMap.put(fCO2FromPCO2TEquNcep_VARNAME, "fCO2_from_pCO2_water_water_equi_temp_ncep");
		shortNameMap.put(fCO2FromPCO2SstNcep_VARNAME, "fCO2_from_pCO2_water_sst_100humidity_uatm_ncep");
		shortNameMap.put(fCO2FromXCO2TEquWoa_VARNAME, "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_woa");
		shortNameMap.put(fCO2FromXCO2SstWoa_VARNAME, "fCO2_insitu_from_xCO2_water_sst_dry_ppm_woa");
		shortNameMap.put(fCO2FromXCO2TEquNcep_VARNAME, "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep");
		shortNameMap.put(fCO2FromXCO2SstNcep_VARNAME, "fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep");
		shortNameMap.put(fCO2FromXCO2TEquNcepWoa_VARNAME, "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep_woa");
		shortNameMap.put(fCO2FromXCO2SstNcepWoa_VARNAME, "fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep_woa");

		shortNameMap.put(fCO2Rec_VARNAME, "fCO2_recommended");
		shortNameMap.put(fCO2Source_VARNAME, "fCO2_source");
		shortNameMap.put(deltaT_VARNAME, "delta_temp");
		shortNameMap.put(regionID_VARNAME, "region_id");
		shortNameMap.put(calcSpeed_VARNAME, "calc_speed");
		shortNameMap.put(etopo2Depth_VARNAME, "etopo2");
		shortNameMap.put(gvCO2_VARNAME, "gvCO2");
		shortNameMap.put(distToLand_VARNAME, "dist_to_land");
		shortNameMap.put(dayOfYear_VARNAME, "day_of_year");

		SHORT_NAMES = Collections.unmodifiableMap(shortNameMap);
	}

	/**
	 *  Full length names of the variables for netCDF files
	 */
	public static final Map<String, String> LONG_NAMES;
	static {
		HashMap<String, String> longNameMap = new HashMap<String, String>();

		longNameMap.put(expocode_VARNAME, "expocode");
		longNameMap.put(cruiseName_VARNAME, "dataset name");
		longNameMap.put(vesselName_VARNAME, "vessel name");
		longNameMap.put(organization_VARNAME, "organization");
		longNameMap.put(westmostLongitude_VARNAME, "westernmost longitude");
		longNameMap.put(eastmostLongitude_VARNAME, "easternmost longitude");
		longNameMap.put(southmostLatitude_VARNAME, "southernmost latitude");
		longNameMap.put(northmostLatitude_VARNAME, "northernmost latitude");
		longNameMap.put(beginTime_VARNAME, "beginning time");
		longNameMap.put(endTime_VARNAME, "ending time");
		longNameMap.put(scienceGroup_VARNAME, "investigators");
		longNameMap.put(origDataRef_VARNAME, "original data reference");
		longNameMap.put(addlDocs_VARNAME, "additional documents");
		longNameMap.put(socatDOI_VARNAME, "SOCAT data DOI");
		longNameMap.put(socatDOIHRef_VARNAME, "SOCAT data reference");
		longNameMap.put(socatVersion_VARNAME, "SOCAT version");
		longNameMap.put(qcFlag_VARNAME, "QC flag");

		longNameMap.put(rowNum_VARNAME, "sample number");
		longNameMap.put(year_VARNAME, "year");
		longNameMap.put(month_VARNAME, "month of year");
		longNameMap.put(day_VARNAME, "day of month");
		longNameMap.put(hour_VARNAME, "hour of day");
		longNameMap.put(minute_VARNAME, "minute of hour");
		longNameMap.put(second_VARNAME, "second of minute");
		longNameMap.put(time_VARNAME, "time");

		longNameMap.put(longitude_VARNAME, "longitude");
		longNameMap.put(latitude_VARNAME, "latitude");
		longNameMap.put(sampleDepth_VARNAME, "sample depth");
		longNameMap.put(salinity_VARNAME, "salinity");
		longNameMap.put(tEqu_VARNAME, "equilibrator chamber temperature");
		longNameMap.put(sst_VARNAME, "sea surface temperature");
		longNameMap.put(tAtm_VARNAME, "sea-level air temperature");
		longNameMap.put(pEqu_VARNAME, "equilibrator chamber pressure");
		longNameMap.put(slp_VARNAME, "sea-level air pressure");

		longNameMap.put(xCO2WaterTEquDry_VARNAME, "water xCO2 dry using equi temp");
		longNameMap.put(xCO2WaterSstDry_VARNAME, "water xCO2 dry using sst");
		longNameMap.put(xCO2WaterTEquWet_VARNAME, "water xCO2 wet using equi temp");
		longNameMap.put(xCO2WaterSstWet_VARNAME, "water xCO2 wet using sst");
		longNameMap.put(pCO2WaterTEquWet_VARNAME, "water pCO2 wet using equi temp");
		longNameMap.put(pCO2WaterSstWet_VARNAME, "water pCO2 wet using sst");
		longNameMap.put(fCO2WaterTEquWet_VARNAME, "water fCO2 wet using equi temp");
		longNameMap.put(fCO2WaterSstWet_VARNAME, "water fCO2 wet using sst");

		longNameMap.put(xCO2AtmDryActual_VARNAME, "actual air xCO2 dry");
		longNameMap.put(xCO2AtmDryInterp_VARNAME, "interpolated air xCO2 dry ");
		longNameMap.put(pCO2AtmWetActual_VARNAME, "actual air pCO2 wet");
		longNameMap.put(pCO2AtmWetInterp_VARNAME, "interpolated air pCO2 wet");
		longNameMap.put(fCO2AtmWetActual_VARNAME, "actual air fCO2 wet");
		longNameMap.put(fCO2AtmWetInterp_VARNAME, "interpolated air fCO2 wet");

		longNameMap.put(deltaXCO2_VARNAME, "water xCO2 minus atmospheric xCO2");
		longNameMap.put(deltaPCO2_VARNAME, "water pCO2 minus atmospheric pCO2");
		longNameMap.put(deltaFCO2_VARNAME, "water fCO2 minus atmospheric fCO2");

		longNameMap.put(xH2OEqu_VARNAME, "xH2O in equil air sample");
		longNameMap.put(relativeHumidity_VARNAME, "relative humidity");
		longNameMap.put(specificHumidity_VARNAME, "specific humidity");
		longNameMap.put(shipSpeed_VARNAME, "measured ship speed");
		longNameMap.put(shipDirection_VARNAME, "ship direction"); 
		longNameMap.put(windSpeedTrue_VARNAME, "true wind speed");
		longNameMap.put(windSpeedRelative_VARNAME, "relative wind speed");
		longNameMap.put(windDirectionTrue_VARNAME, "true wind direction");
		longNameMap.put(windDirectionRelative_VARNAME, "relative wind direction");

		longNameMap.put(woceCO2Water_VARNAME, "WOCE flag for water CO2");
		longNameMap.put(woceCO2Atm_VARNAME, "WOCE flag for air CO2");

		longNameMap.put(woaSss_VARNAME, "salinity from World Ocean Atlas 2005");
		longNameMap.put(ncepSlp_VARNAME, "sea level air pressure from NCEP/NCAR reanalysis");

		longNameMap.put(fCO2FromXCO2TEqu_VARNAME, "fCO2 from xCO2_water_equi_temp_dry_ppm, Pressure_equi, sal");
		longNameMap.put(fCO2FromXCO2Sst_VARNAME, "fCO2 from xCO2_water_sst_dry_ppm, Pressure_equi, sal");
		longNameMap.put(fCO2FromPCO2TEqu_VARNAME, "fCO2 from pCO2_water_equi_temp, Pressure_equi, sal");
		longNameMap.put(fCO2FromPCO2Sst_VARNAME, "fCO2 from pCO2_water_sst_100humidity_uatm, Pressure_equi, sal");
		longNameMap.put(fCO2FromFCO2TEqu_VARNAME, "fCO2 from fCO2_water_equi_temp, Pressure_equi, sal");
		longNameMap.put(fCO2FromFCO2Sst_VARNAME, "fCO2 from fCO2_water_sst_100humidity_uatm, Pressure_equi, sal");
		longNameMap.put(fCO2FromPCO2TEquNcep_VARNAME, "fCO2 from pCO2_water_equi_temp, NCEP SLP, sal");
		longNameMap.put(fCO2FromPCO2SstNcep_VARNAME, "fCO2 from pCO2_water_sst_100humidity_uatm, NCEP SLP, sal");
		longNameMap.put(fCO2FromXCO2TEquWoa_VARNAME, "fCO2 from xCO2_water_equi_temp_dry_ppm, Pressure_equi, WOA SSS");
		longNameMap.put(fCO2FromXCO2SstWoa_VARNAME, "fCO2 from xCO2_water_sst_dry_ppm, Pressure_equi, WOA SSS");
		longNameMap.put(fCO2FromXCO2TEquNcep_VARNAME, "fCO2 from xCO2_water_equi_temp_dry_ppm, NCEP SLP, sal");
		longNameMap.put(fCO2FromXCO2SstNcep_VARNAME, "fCO2 from xCO2_water_sst_dry_ppm, NCEP SLP, sal");
		longNameMap.put(fCO2FromXCO2TEquNcepWoa_VARNAME, "fCO2 from xCO2_water_equi_temp_dry_ppm, NCEP SLP, WOA SSS");
		longNameMap.put(fCO2FromXCO2SstNcepWoa_VARNAME, "fCO2 from xCO2_water_sst_dry_ppm, NCEP SLP, WOA SSS");

		longNameMap.put(fCO2Rec_VARNAME, "fCO2 recommended");
		longNameMap.put(fCO2Source_VARNAME, "Algorithm number for recommended fCO2");
		longNameMap.put(deltaT_VARNAME, "Equilibrator Temp - SST");
		longNameMap.put(regionID_VARNAME, "SOCAT region ID");
		longNameMap.put(calcSpeed_VARNAME, "calculated ship speed");
		longNameMap.put(etopo2Depth_VARNAME, "bathymetry from ETOPO2");
		longNameMap.put(gvCO2_VARNAME, "GlobalView xCO2");
		longNameMap.put(distToLand_VARNAME, "distance to land");
		longNameMap.put(dayOfYear_VARNAME, "day of the year");

		LONG_NAMES = Collections.unmodifiableMap(longNameMap);
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
		final String xh2o_units = "mmol/mol";
		final String ship_speed_units = "knots";
		final String wind_speed_units = "m/s";
		final String direction_units = "degrees";
		final String distance_units = "km";
		final String seconds_time_units = "seconds since 1970-01-01T00:00:00Z";
		final String days_units = "days";

		HashMap<String, String> unitsMap = new HashMap<String, String>();
		unitsMap.put(westmostLongitude_VARNAME, longitude_units);
		unitsMap.put(eastmostLongitude_VARNAME, longitude_units);
		unitsMap.put(southmostLatitude_VARNAME, latitude_units);
		unitsMap.put(northmostLatitude_VARNAME, latitude_units);
		unitsMap.put(beginTime_VARNAME, seconds_time_units);
		unitsMap.put(endTime_VARNAME, seconds_time_units);

		unitsMap.put(time_VARNAME, seconds_time_units);

		unitsMap.put(longitude_VARNAME, longitude_units);
		unitsMap.put(latitude_VARNAME, latitude_units);
		unitsMap.put(sampleDepth_VARNAME, depth_units);
		unitsMap.put(salinity_VARNAME, salinity_units);
		unitsMap.put(tEqu_VARNAME, temperature_units);
		unitsMap.put(sst_VARNAME, temperature_units);
		unitsMap.put(tAtm_VARNAME, temperature_units);
		unitsMap.put(pEqu_VARNAME, pressure_units);
		unitsMap.put(slp_VARNAME, pressure_units);

		unitsMap.put(xCO2WaterTEquDry_VARNAME, xco2_units);
		unitsMap.put(xCO2WaterSstDry_VARNAME, xco2_units);
		unitsMap.put(xCO2WaterTEquWet_VARNAME, xco2_units);
		unitsMap.put(xCO2WaterSstWet_VARNAME, xco2_units);
		unitsMap.put(pCO2WaterTEquWet_VARNAME, pco2_units);
		unitsMap.put(pCO2WaterSstWet_VARNAME, pco2_units);
		unitsMap.put(fCO2WaterTEquWet_VARNAME, fco2_units);
		unitsMap.put(fCO2WaterSstWet_VARNAME, fco2_units);

		unitsMap.put(xCO2AtmDryActual_VARNAME, xco2_units);
		unitsMap.put(xCO2AtmDryInterp_VARNAME, xco2_units);
		unitsMap.put(pCO2AtmWetActual_VARNAME, pco2_units);
		unitsMap.put(pCO2AtmWetInterp_VARNAME, pco2_units);
		unitsMap.put(fCO2AtmWetActual_VARNAME, fco2_units);
		unitsMap.put(fCO2AtmWetInterp_VARNAME, fco2_units);

		unitsMap.put(deltaXCO2_VARNAME, xco2_units);
		unitsMap.put(deltaPCO2_VARNAME, pco2_units);
		unitsMap.put(deltaFCO2_VARNAME, fco2_units);

		unitsMap.put(xH2OEqu_VARNAME, xh2o_units);
		unitsMap.put(shipSpeed_VARNAME, ship_speed_units);
		unitsMap.put(shipDirection_VARNAME, direction_units); 
		unitsMap.put(windSpeedTrue_VARNAME, wind_speed_units);
		unitsMap.put(windSpeedRelative_VARNAME, wind_speed_units);
		unitsMap.put(windDirectionTrue_VARNAME, direction_units);
		unitsMap.put(windDirectionRelative_VARNAME, direction_units);

		unitsMap.put(woaSss_VARNAME, salinity_units);
		unitsMap.put(ncepSlp_VARNAME, pressure_units);

		unitsMap.put(fCO2FromXCO2TEqu_VARNAME, fco2_units);
		unitsMap.put(fCO2FromXCO2Sst_VARNAME, fco2_units);
		unitsMap.put(fCO2FromPCO2TEqu_VARNAME, fco2_units);
		unitsMap.put(fCO2FromPCO2Sst_VARNAME, fco2_units);
		unitsMap.put(fCO2FromFCO2TEqu_VARNAME, fco2_units);
		unitsMap.put(fCO2FromFCO2Sst_VARNAME, fco2_units);
		unitsMap.put(fCO2FromPCO2TEquNcep_VARNAME, fco2_units);
		unitsMap.put(fCO2FromPCO2SstNcep_VARNAME, fco2_units);
		unitsMap.put(fCO2FromXCO2TEquWoa_VARNAME, fco2_units);
		unitsMap.put(fCO2FromXCO2SstWoa_VARNAME, fco2_units);
		unitsMap.put(fCO2FromXCO2TEquNcep_VARNAME, fco2_units);
		unitsMap.put(fCO2FromXCO2SstNcep_VARNAME, fco2_units);
		unitsMap.put(fCO2FromXCO2TEquNcepWoa_VARNAME, fco2_units);
		unitsMap.put(fCO2FromXCO2SstNcepWoa_VARNAME, fco2_units);

		unitsMap.put(fCO2Rec_VARNAME, fco2_units);
		unitsMap.put(deltaT_VARNAME, temperature_units);
		unitsMap.put(calcSpeed_VARNAME, ship_speed_units);
		unitsMap.put(etopo2Depth_VARNAME, depth_units);
		unitsMap.put(gvCO2_VARNAME, xco2_units);
		unitsMap.put(distToLand_VARNAME, distance_units);
		unitsMap.put(dayOfYear_VARNAME, days_units);

		UNITS = Collections.unmodifiableMap(unitsMap);
	}

	/**
	 * Standardized names for netCDF files
	 */
	public static final Map<String, String> STANDARD_NAMES;
	static {
		HashMap<String, String> stdNamesMap = new HashMap<String, String>();

		stdNamesMap.put(vesselName_VARNAME, "platform_name");
		stdNamesMap.put(westmostLongitude_VARNAME, "geospatial_lon_min");
		stdNamesMap.put(eastmostLongitude_VARNAME, "geospatial_lon_max");
		stdNamesMap.put(southmostLatitude_VARNAME, "geospatial_lat_min");
		stdNamesMap.put(northmostLatitude_VARNAME, "geospatial_lat_max");
		stdNamesMap.put(beginTime_VARNAME, "time_coverage_start");
		stdNamesMap.put(endTime_VARNAME, "time_converage_end");

		stdNamesMap.put(time_VARNAME, "time");

		stdNamesMap.put(longitude_VARNAME, "longitude");
		stdNamesMap.put(latitude_VARNAME, "latitude");
		stdNamesMap.put(sampleDepth_VARNAME, "depth");
		stdNamesMap.put(salinity_VARNAME, "sea_surface_salinity");
		stdNamesMap.put(sst_VARNAME, "sea_surface_temperature");
		stdNamesMap.put(tAtm_VARNAME, "air_temperature_at_sea_level");
		stdNamesMap.put(slp_VARNAME, "air_pressure_at_sea_level");

		stdNamesMap.put(xCO2WaterTEquDry_VARNAME, "mole_fraction_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(xCO2WaterSstDry_VARNAME, "mole_fraction_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(xCO2WaterTEquWet_VARNAME, "mole_fraction_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(xCO2WaterSstWet_VARNAME, "mole_fraction_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2WaterTEquWet_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2WaterSstWet_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(pCO2WaterTEquWet_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(pCO2WaterSstWet_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");

		stdNamesMap.put(xCO2AtmDryActual_VARNAME, "mole_fraction_of_carbon_dioxide_in_air");
		stdNamesMap.put(xCO2AtmDryInterp_VARNAME, "mole_fraction_of_carbon_dioxide_in_air");
		stdNamesMap.put(pCO2AtmWetActual_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_air");
		stdNamesMap.put(pCO2AtmWetInterp_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_air");
		stdNamesMap.put(fCO2AtmWetActual_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_air");
		stdNamesMap.put(fCO2AtmWetInterp_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_air");

		stdNamesMap.put(xH2OEqu_VARNAME, "mole_fraction_of_water_in_air");
		stdNamesMap.put(relativeHumidity_VARNAME, "relative_humidity");
		stdNamesMap.put(specificHumidity_VARNAME, "specific_humidity");
		stdNamesMap.put(shipSpeed_VARNAME, "platform_speed_wrt_ground"); 
		stdNamesMap.put(shipDirection_VARNAME, "platform_course");
		stdNamesMap.put(windSpeedTrue_VARNAME, "wind_speed");
		stdNamesMap.put(windSpeedRelative_VARNAME, "wind_speed");
		stdNamesMap.put(windDirectionTrue_VARNAME, "wind_from_direction");
		stdNamesMap.put(windDirectionRelative_VARNAME, "wind_from_direction");

		stdNamesMap.put(woaSss_VARNAME, "sea_surface_salinity");
		stdNamesMap.put(ncepSlp_VARNAME, "air_pressure_at_sea_level");

		stdNamesMap.put(fCO2FromXCO2TEqu_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromXCO2Sst_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromPCO2TEqu_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromPCO2Sst_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromFCO2TEqu_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromFCO2Sst_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromPCO2TEquNcep_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromPCO2SstNcep_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromXCO2TEquWoa_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromXCO2SstWoa_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromXCO2TEquNcep_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromXCO2SstNcep_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromXCO2TEquNcepWoa_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(fCO2FromXCO2SstNcepWoa_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");

		stdNamesMap.put(fCO2Rec_VARNAME, "surface_partial_pressure_of_carbon_dioxide_in_sea_water");
		stdNamesMap.put(calcSpeed_VARNAME, "platform_speed_wrt_ground");
		stdNamesMap.put(etopo2Depth_VARNAME, "sea_floor_depth");
		stdNamesMap.put(gvCO2_VARNAME, "mole_fraction_of_carbon_dioxide_in_air");

		STANDARD_NAMES = Collections.unmodifiableMap(stdNamesMap);
	}

	/**
	 * IOOS categories for netCDF files
	 */
	public static final Map<String, String> IOOS_CATEGORIES;
	static {
		String bathymetry_category = "Bathymetry";
		String co2_category = "CO2";
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

		ioosCatMap.put(expocode_VARNAME, identifier_category);
		ioosCatMap.put(cruiseName_VARNAME, identifier_category);
		ioosCatMap.put(vesselName_VARNAME, identifier_category);
		ioosCatMap.put(organization_VARNAME, identifier_category);
		ioosCatMap.put(westmostLongitude_VARNAME, location_category);
		ioosCatMap.put(eastmostLongitude_VARNAME, location_category);
		ioosCatMap.put(southmostLatitude_VARNAME, location_category);
		ioosCatMap.put(northmostLatitude_VARNAME, location_category);
		ioosCatMap.put(beginTime_VARNAME, time_category);
		ioosCatMap.put(endTime_VARNAME, time_category);
		ioosCatMap.put(scienceGroup_VARNAME, identifier_category);
		ioosCatMap.put(origDataRef_VARNAME, identifier_category);
		ioosCatMap.put(socatDOI_VARNAME, identifier_category);
		ioosCatMap.put(socatDOIHRef_VARNAME, identifier_category);
		ioosCatMap.put(socatVersion_VARNAME, identifier_category);
		ioosCatMap.put(qcFlag_VARNAME, quality_category);

		ioosCatMap.put(rowNum_VARNAME, identifier_category);
		ioosCatMap.put(year_VARNAME, time_category);
		ioosCatMap.put(month_VARNAME, time_category);
		ioosCatMap.put(day_VARNAME, time_category);
		ioosCatMap.put(hour_VARNAME, time_category);
		ioosCatMap.put(minute_VARNAME, time_category);
		ioosCatMap.put(second_VARNAME, time_category);
		ioosCatMap.put(time_VARNAME, time_category);

		ioosCatMap.put(longitude_VARNAME, location_category);
		ioosCatMap.put(latitude_VARNAME, location_category);
		ioosCatMap.put(sampleDepth_VARNAME, bathymetry_category);
		ioosCatMap.put(salinity_VARNAME, salinity_category);
		ioosCatMap.put(tEqu_VARNAME, temperature_category);
		ioosCatMap.put(sst_VARNAME, temperature_category);
		ioosCatMap.put(tAtm_VARNAME, temperature_category);
		ioosCatMap.put(pEqu_VARNAME, pressure_category);
		ioosCatMap.put(slp_VARNAME, pressure_category);

		ioosCatMap.put(xCO2WaterSstDry_VARNAME, co2_category);
		ioosCatMap.put(xCO2WaterTEquDry_VARNAME, co2_category);
		ioosCatMap.put(xCO2WaterSstWet_VARNAME, co2_category);
		ioosCatMap.put(xCO2WaterTEquWet_VARNAME, co2_category);
		ioosCatMap.put(fCO2WaterSstWet_VARNAME, co2_category);
		ioosCatMap.put(fCO2WaterTEquWet_VARNAME, co2_category);
		ioosCatMap.put(pCO2WaterSstWet_VARNAME, co2_category);
		ioosCatMap.put(pCO2WaterTEquWet_VARNAME, co2_category);

		ioosCatMap.put(xCO2AtmDryActual_VARNAME, co2_category);
		ioosCatMap.put(xCO2AtmDryInterp_VARNAME, co2_category);
		ioosCatMap.put(pCO2AtmWetActual_VARNAME, co2_category);
		ioosCatMap.put(pCO2AtmWetInterp_VARNAME, co2_category);
		ioosCatMap.put(fCO2AtmWetActual_VARNAME, co2_category);
		ioosCatMap.put(fCO2AtmWetInterp_VARNAME, co2_category);

		ioosCatMap.put(deltaXCO2_VARNAME, co2_category);
		ioosCatMap.put(deltaPCO2_VARNAME, co2_category);
		ioosCatMap.put(deltaFCO2_VARNAME, co2_category);

		ioosCatMap.put(xH2OEqu_VARNAME, humidity_category);
		ioosCatMap.put(relativeHumidity_VARNAME, humidity_category);
		ioosCatMap.put(specificHumidity_VARNAME, humidity_category);
		ioosCatMap.put(windSpeedTrue_VARNAME, wind_category);
		ioosCatMap.put(windSpeedRelative_VARNAME, wind_category);
		ioosCatMap.put(windDirectionTrue_VARNAME, wind_category);
		ioosCatMap.put(windDirectionRelative_VARNAME, wind_category);

		ioosCatMap.put(woceCO2Water_VARNAME, quality_category);
		ioosCatMap.put(woceCO2Atm_VARNAME, quality_category);

		ioosCatMap.put(woaSss_VARNAME, salinity_category);
		ioosCatMap.put(ncepSlp_VARNAME, pressure_category);

		ioosCatMap.put(fCO2FromXCO2TEqu_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromXCO2Sst_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromPCO2TEqu_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromPCO2Sst_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromFCO2TEqu_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromFCO2Sst_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromPCO2TEquNcep_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromPCO2SstNcep_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromXCO2TEquWoa_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromXCO2SstWoa_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromXCO2TEquNcep_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromXCO2SstNcep_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromXCO2TEquNcepWoa_VARNAME, co2_category);
		ioosCatMap.put(fCO2FromXCO2SstNcepWoa_VARNAME, co2_category);

		ioosCatMap.put(fCO2Rec_VARNAME, co2_category);
		ioosCatMap.put(fCO2Source_VARNAME, identifier_category);
		ioosCatMap.put(deltaT_VARNAME, temperature_category);
		ioosCatMap.put(regionID_VARNAME, identifier_category);
		ioosCatMap.put(etopo2Depth_VARNAME, bathymetry_category);
		ioosCatMap.put(gvCO2_VARNAME, co2_category);
		ioosCatMap.put(distToLand_VARNAME, location_category);
		ioosCatMap.put(dayOfYear_VARNAME, time_category);

		IOOS_CATEGORIES = Collections.unmodifiableMap(ioosCatMap);
	}

	/**
	 * Data variable names for the NetCDF files from an all-uppercase name.
	 */
	public static final Map<String, String> VARIABLE_NAMES;
	static {
		HashMap<String, String> varNamesMap = new HashMap<String, String>();
		for ( String varName : SHORT_NAMES.values() )
			varNamesMap.put(varName.toUpperCase(), varName);
		// GEOPOSITION is from WOCE flagging from plots of only lon/lat/time 
		varNamesMap.put(geoposition_VARNAME.toUpperCase(), geoposition_VARNAME);
		VARIABLE_NAMES = Collections.unmodifiableMap(varNamesMap);
	}

	/**
	 * Variable names for netCDF files from the type of user-provided data columns.
	 * These are for generating WOCE events, only those types and variable names 
	 * that a reviewer could WOCE are provided.
	 */
	public static final Map<DataColumnType, String> TYPE_TO_VARNAME_MAP;
	static {
		EnumMap<DataColumnType,String> typeToNameMap = 
				new EnumMap<DataColumnType,String>(DataColumnType.class);

		// UNKNOWN, metadata-types, WOCE flags, COMMENT, and OTHER not available to WOCE

		// Map all time-related types to "time"; other variables not visible
		typeToNameMap.put(DataColumnType.TIMESTAMP, "time");
		typeToNameMap.put(DataColumnType.DATE, "time");
		typeToNameMap.put(DataColumnType.TIME, "time");
		typeToNameMap.put(DataColumnType.YEAR, "time");
		typeToNameMap.put(DataColumnType.MONTH, "time");
		typeToNameMap.put(DataColumnType.DAY, "time");
		typeToNameMap.put(DataColumnType.HOUR, "time");
		typeToNameMap.put(DataColumnType.MINUTE, "time");
		typeToNameMap.put(DataColumnType.SECOND, "time");
		typeToNameMap.put(DataColumnType.DAY_OF_YEAR, "time");
		typeToNameMap.put(DataColumnType.SECOND_OF_DAY, "time");

		typeToNameMap.put(DataColumnType.LONGITUDE, "longitude");
		typeToNameMap.put(DataColumnType.LATITUDE, "latitude");
		typeToNameMap.put(DataColumnType.SAMPLE_DEPTH, "sample_depth");
		typeToNameMap.put(DataColumnType.SALINITY, "sal");
		typeToNameMap.put(DataColumnType.EQUILIBRATOR_TEMPERATURE, "Temperature_equi");
		typeToNameMap.put(DataColumnType.SEA_SURFACE_TEMPERATURE, "temp");
		typeToNameMap.put(DataColumnType.ATMOSPHERIC_TEMPERATURE, "Temperature_atm");
		typeToNameMap.put(DataColumnType.EQUILIBRATOR_PRESSURE, "Pressure_equi");
		typeToNameMap.put(DataColumnType.SEA_LEVEL_PRESSURE, "Pressure_atm");

		typeToNameMap.put(DataColumnType.XCO2_WATER_TEQU_DRY, "xCO2_water_equi_temp_dry_ppm");
		typeToNameMap.put(DataColumnType.XCO2_WATER_SST_DRY, "xCO2_water_sst_dry_ppm");
		typeToNameMap.put(DataColumnType.XCO2_WATER_TEQU_WET, "xCO2_water_equi_temp_wet_ppm");
		typeToNameMap.put(DataColumnType.XCO2_WATER_SST_WET, "xCO2_water_sst_wet_ppm");
		typeToNameMap.put(DataColumnType.PCO2_WATER_TEQU_WET, "pCO2_water_equi_temp");
		typeToNameMap.put(DataColumnType.PCO2_WATER_SST_WET, "pCO2_water_sst_100humidity_uatm");
		typeToNameMap.put(DataColumnType.FCO2_WATER_TEQU_WET, "fCO2_water_equi_uatm");
		typeToNameMap.put(DataColumnType.FCO2_WATER_SST_WET, "fCO2_water_sst_100humidity_uatm");

		typeToNameMap.put(DataColumnType.XCO2_ATM_DRY_ACTUAL, "xCO2_atm_dry_actual");
		typeToNameMap.put(DataColumnType.XCO2_ATM_DRY_INTERP, "xCO2_atm_dry_interp");
		typeToNameMap.put(DataColumnType.PCO2_ATM_WET_ACTUAL, "pCO2_atm_wet_actual");
		typeToNameMap.put(DataColumnType.PCO2_ATM_WET_INTERP, "pCO2_atm_wet_interp");
		typeToNameMap.put(DataColumnType.FCO2_ATM_WET_ACTUAL, "fCO2_atm_wet_actual");
		typeToNameMap.put(DataColumnType.FCO2_ATM_WET_INTERP, "fCO2_atm_wet_interp");

		typeToNameMap.put(DataColumnType.DELTA_XCO2, "delta_xCO2");
		typeToNameMap.put(DataColumnType.DELTA_PCO2, "delta_pCO2");
		typeToNameMap.put(DataColumnType.DELTA_FCO2, "delta_fCO2");

		typeToNameMap.put(DataColumnType.XH2O_EQU, "xH2O_equi");
		typeToNameMap.put(DataColumnType.RELATIVE_HUMIDITY, "relative_humidity");
		typeToNameMap.put(DataColumnType.SPECIFIC_HUMIDITY, "specific_humidity");
		typeToNameMap.put(DataColumnType.SHIP_SPEED, "ship_speed"); 
		typeToNameMap.put(DataColumnType.SHIP_DIRECTION, "ship_dir");
		typeToNameMap.put(DataColumnType.WIND_SPEED_TRUE, "wind_speed_true");
		typeToNameMap.put(DataColumnType.WIND_SPEED_RELATIVE, "wind_speed_rel");
		typeToNameMap.put(DataColumnType.WIND_DIRECTION_TRUE, "wind_dir_true");
		typeToNameMap.put(DataColumnType.WIND_DIRECTION_RELATIVE, "wind_dir_rel");

		TYPE_TO_VARNAME_MAP = Collections.unmodifiableMap(typeToNameMap);
	}

}
