package gov.noaa.pmel.socat.dashboard.nc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {

	/**
	 * Standard units of each variable appropriate for netCDF files
	 */
	public static final Map<String, String> UNITS;
	static {
		String no_units = "";
		String longitude_units = "degrees_east";
		String latitude_units = "degrees_north";
		String depth_units = "meters";
		String temperature_units = "degrees C";
		String salinity_units = "PSU";
		String pressure_units = "hectopascals";
		String xco2_units = "micromole per mole";
		String fco2_units = "microatmospheres";
		String pco2_units = "microatmospheres";
		String speed_units = "knots";
		String distance_units = "kilometers";
		String seconds_units = "seconds";
		String days_units = "days";

		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("year", no_units);
		aMap.put("month", no_units);
		aMap.put("day", no_units);
		aMap.put("hour", no_units);
		aMap.put("minute", no_units);
		aMap.put("second", no_units);
		aMap.put("longitude", longitude_units);
		aMap.put("latitude", latitude_units);
		aMap.put("sampleDepth", depth_units);
		aMap.put("sst", temperature_units);
		aMap.put("tEqu", temperature_units);
		aMap.put("sal", salinity_units);
		aMap.put("pAtm", pressure_units);
		aMap.put("pEqu", pressure_units);
		aMap.put("xCO2WaterSst", xco2_units);
		aMap.put("xCO2WaterTEqu", xco2_units);
		aMap.put("fCO2WaterSst", fco2_units);
		aMap.put("fCO2WaterTEqu", fco2_units);
		aMap.put("pCO2WaterSst", pco2_units);
		aMap.put("pCO2WaterTEqu", pco2_units);
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
		aMap.put("fCO2Source", no_units); 
		aMap.put("deltaT", temperature_units);
		aMap.put("regionID", no_units);
		aMap.put("calcSpeed", speed_units);
		aMap.put("etopo2", depth_units);
		aMap.put("gvCO2", xco2_units);
		aMap.put("distToLand", distance_units);
        aMap.put("time", seconds_units);
		aMap.put("days1970", days_units);
		aMap.put("dayOfYear", days_units);
		aMap.put("woceFlag", no_units);

		UNITS = Collections.unmodifiableMap(aMap);
	}

	/**
	 *  Full length names of the variables for netCDF files
	 */
	public static final Map<String, String> LONG_NAME;
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
        aMap.put("sampleDepth", "sample depth");
        aMap.put("sst", "sea surface temperature");
        aMap.put("tEqu", "equilibrator chamber temperature");
        aMap.put("sal", "salinity");
        aMap.put("pAtm", "sea-level atmospheric pressure");
        aMap.put("pEqu", "equilibrator chamber pressure");
        aMap.put("xCO2WaterSst", "xCO2 using sst");
        aMap.put("xCO2WaterTEqu", "xCO2 using equi temp");
        aMap.put("fCO2WaterSst", "fCO2 using sst");
        aMap.put("fCO2WaterTEqu", "fCO2 using equi temp");
        aMap.put("pCO2WaterSst", "pCO2 using sst");
        aMap.put("pCO2WaterTEqu", "pCO2 using equi temp");
        aMap.put("woaSss", "salinity from World Ocean Atlas 2005");
        aMap.put("ncepSlp", "sea level pressure from NCEP/NCAR reanalysis");
        aMap.put("fCO2FromXCO2TEqu", "fco2 rec xCO2_water_equi_temp_dry_ppm, Temperature_equi, sal");
        aMap.put("fCO2FromXCO2Sst", "fco2 rec xCO2_water_sst_dry_ppm, Temperature_equi, sal");
        aMap.put("fCO2FromPCO2TEqu", "fco2 rec pCO2_water_equi_temp, Pressure_equi, sal");
        aMap.put("fCO2FromPCO2Sst", "fco2 rec pCO2_water_sst_100humidity_uatm, Pressure_equi, sal");
        aMap.put("fCO2FromFCO2TEqu", "fco2 rec fCO2_water_equi_tem, Pressure_equi, sal");
        aMap.put("fCO2FromFCO2Sst", "fco2 rec fCO2_water_sst_100humidity_uatm, Pressure_equi, sal");
        aMap.put("fCO2FromPCO2TEquNcep", "fco2 rec pCO2_water_equi_temp, ncepSlp, sal");
        aMap.put("fCO2FromPCO2SstNcep", "fco2 rec pCO2_water_sst_100humidity_uatm, ncepSlp, sal");
        aMap.put("fCO2FromXCO2TEquWoa", "fco2 rec xCO2_water_equi_temp_dry_ppm, Pressure_equi, WOASss");
        aMap.put("fCO2FromXCO2SstWoa", "fco2 rec xCO2_water_sst_dry_ppm, Pressure_equi, WOASss");
        aMap.put("fCO2FromXCO2TEquNcep", "fco2 rec xCO2_water_equi_temp_dry_ppm, ncepSlp, sal");
        aMap.put("fCO2FromXCO2SstNcep", "fco2 rec xCO2_water_sst_dry_ppm, ncepSlp, sal");
        aMap.put("fCO2FromXCO2TEquNcepWoa", "fco2 rec xCO2_water_equi_temp_dry_ppm, ncepSlp, WOASss");
        aMap.put("fCO2FromXCO2SstNcepWoa", "fco2 rec xCO2_water_sst_dry_ppm, ncepSlp, WOASss");
        aMap.put("fCO2Rec", "fCO2 recommended");
        aMap.put("fCO2Source", "Algorithm number for recommended fCO2");
        aMap.put("deltaT", "Equilibrator Temp - SST");
        aMap.put("regionID", "SOCAT region ID");
        aMap.put("time", "Datetime");
		aMap.put("days1970", "days since Jan 1 1970");
		aMap.put("dayOfYear", "days since Jan 1 of the year");
		aMap.put("calcSpeed", "calculated ship speed");
        aMap.put("etopo2", "bathymetry from ETOPO2");
        aMap.put("gvCO2", "GlobalView xCO2");
        aMap.put("distToLand", "Distance to Land");
        aMap.put("woceFlag", "WOCE Flag");

        LONG_NAME = Collections.unmodifiableMap(aMap);
    }

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
        aMap.put("sst", "temp");
        aMap.put("tEqu", "Temperature_equi");
        aMap.put("sal", "sal");
        aMap.put("pAtm", "Pressure_atm");
        aMap.put("pEqu", "Pressure_equi");
        aMap.put("xCO2WaterSst", "xCO2_water_sst_dry_ppm");
        aMap.put("xCO2WaterTEqu", "xCO2_water_equi_temp_dry_ppm");
        aMap.put("fCO2WaterSst", "fCO2_water_sst_100humidity_uatm");
        aMap.put("fCO2WaterTEqu", "fCO2_water_equi_uatm");
        aMap.put("pCO2WaterSst", "pCO2_water_sst_100humidity_uatm");
        aMap.put("pCO2WaterTEqu", "pCO2_water_equi_temp");
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
        aMap.put("regionID", "regionID");
        aMap.put("time", "time");
		aMap.put("days1970", "days1970");
		aMap.put("dayOfYear", "dayOfYear");
        aMap.put("calcSpeed", "calcSpeed");
        aMap.put("etopo2", "etopo2");
        aMap.put("gvCO2", "gvCO2");
        aMap.put("distToLand", "distToLand");
        aMap.put("woceFlag", "WOCE_flag");

        SHORT_NAME = Collections.unmodifiableMap(aMap);
    }

}
