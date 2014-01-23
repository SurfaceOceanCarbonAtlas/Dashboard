package gov.noaa.pmel.socat.dashboard.nc;

import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {

	public static final Map<String, List<String>> UNITS;
	static {
		Map<String, List<String>> aMap = new HashMap<String, List<String>>();
		aMap.put("year",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.YEAR));
		aMap.put("month",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.MONTH));
		aMap.put("day",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.DAY));
		aMap.put("hour",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.HOUR));
		aMap.put("minute",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.MINUTE));
		aMap.put("second",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SECOND));
		aMap.put("longitude",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.LONGITUDE));
		aMap.put("latitude",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.LATITUDE));
		aMap.put("sampleDepth",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SAMPLE_DEPTH));
		aMap.put("sst",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SEA_SURFACE_TEMPERATURE));
		aMap.put("tEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.EQUILIBRATOR_TEMPERATURE));
		aMap.put("sal",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SALINITY));
		aMap.put("pAtm",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SEA_LEVEL_PRESSURE));
		aMap.put("pEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.EQUILIBRATOR_PRESSURE));
		aMap.put("xCO2WaterSst",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.XCO2WATER_SST));
		aMap.put("xCO2WaterTEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.XCO2WATER_EQU));
		aMap.put("fCO2WaterSst",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_EQU));
		aMap.put("fCO2WaterTEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_SST));
		aMap.put("pCO2WaterSst",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.PCO2WATER_SST));
		aMap.put("pCO2WaterTEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.PCO2WATER_EQU));

		// The following provided not provided by the user, so they do not have a DataColumnType,
		// but their standard units are in DashboardUtils
		aMap.put("woaSss",DashboardUtils.SALINITY_UNITS);
		aMap.put("ncepSlp",DashboardUtils.PRESSURE_UNITS);
		aMap.put("fCO2FromXCO2TEqu",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromXCO2Sst",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromPCO2TEqu",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromPCO2Sst",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromFCO2TEqu",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromFCO2Sst",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromPCO2TEquNcep",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromPCO2SstNcep",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromXCO2TEquWoa",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromXCO2SstWoa",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromXCO2TEquNcep",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromXCO2SstNcep",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromXCO2TEquNcepWoa",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2FromXCO2SstNcepWoa",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2Rec",DashboardUtils.FCO2_UNITS);
		aMap.put("fCO2Source",DashboardUtils.NO_UNITS); 
		aMap.put("deltaT",DashboardUtils.TEMPERATURE_UNITS);
		aMap.put("regionID",DashboardUtils.NO_UNITS);
		aMap.put("calcSpeed",DashboardUtils.SPEED_UNITS);
		aMap.put("etopo2",DashboardUtils.DEPTH_UNITS);
		aMap.put("gvCO2",DashboardUtils.XCO2_UNITS);
		aMap.put("distToLand",DashboardUtils.DISTANCE_UNITS);
        aMap.put("time", DashboardUtils.SECONDS_UNITS);
		aMap.put("days1970", DashboardUtils.DAYS_UNITS);
		aMap.put("dayOfYear", DashboardUtils.DAYS_UNITS);
		aMap.put("woceFlag",DashboardUtils.NO_UNITS);

		UNITS = Collections.unmodifiableMap(aMap);
	}
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
        aMap.put("fCO2FromXCO2TEqu", "fco2_rec  xCO2_water_equi_temp_dry_ppm, Temperature_equi, sal");
        aMap.put("fCO2FromXCO2Sst", "fco2_rec  xCO2_water_sst_dry_ppm, Temperature_equi, sal");
        aMap.put("fCO2FromPCO2TEqu", "fco2_rec  pCO2_water_equi_temp, Pressure_equi, sal");
        aMap.put("fCO2FromPCO2Sst", "fco2_rec  pCO2_water_sst_100humidity_uatm, Pressure_equi, sal");
        aMap.put("fCO2FromFCO2TEqu", "fco2_rec  fCO2_water_equi_tem, Pressure_equi, sal");
        aMap.put("fCO2FromFCO2Sst", "fco2_rec  fCO2_water_sst_100humidity_uatm, Pressure_equi, sal");
        aMap.put("fCO2FromPCO2TEquNcep", "fco2_rec  pCO2_water_equi_temp, ncepSlp, sal");
        aMap.put("fCO2FromPCO2SstNcep", "fco2_rec  pCO2_water_sst_100humidity_uatm, ncepSlp, sal");
        aMap.put("fCO2FromXCO2TEquWoa", "fco2_rec  xCO2_water_equi_temp_dry_ppm, Pressure_equi, WOASss");
        aMap.put("fCO2FromXCO2SstWoa", "fco2_rec  xCO2_water_sst_dry_ppm, Pressure_equi, WOASss");
        aMap.put("fCO2FromXCO2TEquNcep", "fco2_rec  xCO2_water_equi_temp_dry_ppm, ncepSlp, sal");
        aMap.put("fCO2FromXCO2SstNcep", "fco2_rec  xCO2_water_sst_dry_ppm, ncepSlp, sal");
        aMap.put("fCO2FromXCO2TEquNcepWoa", "fco2_rec  xCO2_water_equi_temp_dry_ppm, ncepSlp, WOASss");
        aMap.put("fCO2FromXCO2SstNcepWoa", "fco2_rec  xCO2_water_sst_dry_ppm, ncepSlp, WOASss");
        aMap.put("fCO2Rec", "fCO2 recomputed");
        aMap.put("fCO2Source", "Algorithm number for recomputing fCO2");
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
        aMap.put("fCO2Rec", "fCO2_recomputed");
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
    // For the time being do no use in the netCDF file.
    // If we do use them, do we want the ":", "(" and "(" characters that seem to mess up parsers in there?
	public static final Map<String, String> DESCRIPTION;
	static {
		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("year", 
				"year of the date (UTC) of the measurement");
		aMap.put("month", 
				"month of the date (UTC) of the measurement");
		aMap.put("day", 
				"day of the date (UTC) of the measurement");
		aMap.put("hour", 
				"hour of the time (UTC) of the measurement");
		aMap.put("minute", 
				"minute of the time (UTC) of the measurement");
		aMap.put("second", 
				"second of the time (UTC) of the measurement");
		aMap.put("longitude", 
				"measurement longitude in decimal degrees East");
		aMap.put("latitude", 
				"measurement latitude in decimal degrees North");
		aMap.put("sampleDepth", 
				"water sampling depth in meters");
		aMap.put("sst", 
				"measured sea surface temperature in degrees Celcius");
		aMap.put("tEqu", 
				"equilibrator chamber temperature in degrees Celcius");
		aMap.put("sal", 
				"measured salinity on the practical salinity scale");
		aMap.put("pAtm", 
				"measured sea-level atmospheric pressure in hectopascals");
		aMap.put("pEqu", 
				"equilibrator chamber pressure in hectopascals");
		aMap.put("xCO2WaterSst", 
				"measured xCO2 (water) in micromoles per mole using sea surface temperature (dry air)");
		aMap.put("xCO2WaterTEqu", 
				"measured xCO2 (water) in micromoles per mole using equilibrator temperature (dry air)");
		aMap.put("fCO2WaterSst", 
				"measured fCO2 (water) in microatmospheres using sea surface temperature (wet air)");
		aMap.put("fCO2WaterTEqu", 
				"measured fCO2 (water) in microatmospheres using equilibrator temperature (wet air)");
		aMap.put("pCO2WaterSst", 
				"measured pCO2 (water) in microatmospheres using sea surface temperature (wet air)");
		aMap.put("pCO2WaterTEqu", 
				"measured pCO2 (water) in microatmospheres using equilibrator temperature (wet air)");
		aMap.put("woaSss",
				"sea surface salinity on the practical salinity scale interpolated from the World Ocean Atlas 2005 " +
				"(see: //http://www.nodc.noaa.gov/OC5/WOA05/pr_woa05.html)");
		aMap.put("ncepSlp",
				"sea level pressure in hectopascals interpolated from the NCEP/NCAR 40-Year Reanalysis Project " +
				"(see: http://www.esrl.noaa.gov/psd/data/gridded/data.ncep.reanalysis.surface.html)");
		aMap.put("fCO2FromXCO2TEqu",
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2Sst",
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromPCO2TEqu",
				"fCO2 in microatmospheres recomputed from measured pCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromPCO2Sst",
				"fCO2 in microatmospheres recomputed from measured pCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromFCO2TEqu",
				"fCO2 in microatmospheres recomputed from measured fCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromFCO2Sst",
				"fCO2 in microatmospheres recomputed from measured fCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromPCO2TEquNcep",
				"fCO2 in microatmospheres recomputed from measured pCO2 (water) using equilibrator temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromPCO2SstNcep",
				"fCO2 in microatmospheres recomputed from measured pCO2 (water) using sea surface temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2TEquWoa",
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2SstWoa",
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2TEquNcep",
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2SstNcep",
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using sea surface temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2TEquNcepWoa",
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"NCEP sea level pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2SstNcepWoa",
				"fCO2 in microatmospheres recomputed from measured xCO2 (water) using sea surface temperature, " +
				"NCEP sea level pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2Rec",
				"fCO2 in microatmospheres recomputed from the most desireable measured CO2 data " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2Source",
				"algorithm number (1-14) for generating the fCO2Rec value " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("deltaT",
				"difference in temperature in degrees Celcius between the equilibrator water and the sea surface water (TEqu - SST)");
		aMap.put("regionID",
				"SOCAT region ID for the location of this measurement " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("time", 
				"seconds since Jan 1, 1970 00:00:00 UTC");
		aMap.put("days1970",
				"fractional number of days since Jan 1, 1970 00:00:00 UTC");
		aMap.put("dayOfYear",
				"fractional number of days since Jan 1 00:00 UTC of that year");
		aMap.put("calcSpeed",
				"calculated ship speed in knots using the previous and subsequent data measurement");
		aMap.put("etopo2",
				"bathymetry in meters interpolated from the ETOPO2 2 arc-minute Gridded Global Relief Data " +
				"(see: http://www.ngdc.noaa.gov/mgg/global/etopo2.html)");
		aMap.put("gvCO2",
				"atmospheric xCO2 in micromoles per mole interpolated from GlobalView-CO2, 2012 1979-01-01 to 2012-01-01 data " +
				"(see: http://www.esrl.noaa.gov/gmd/ccgg/globalview/index.html)");
		aMap.put("distToLand",
				"estimated distance in km to major land mass (up to 1000 km)");
		aMap.put("woceFlag",
				"WOCE quality-control flag (2=okay,3=questionable,4=bad) for the fCO2Rec value " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		DESCRIPTION = Collections.unmodifiableMap(aMap);
	}
}
