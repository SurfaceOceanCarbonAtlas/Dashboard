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
		aMap.put("woceFlag",DashboardUtils.NO_UNITS);

		UNITS = Collections.unmodifiableMap(aMap);
	}

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
				"measurement longitude");
		aMap.put("latitude", 
				"measurement latitude");
		aMap.put("sampleDepth", 
				"water sampling depth");
		aMap.put("sst", 
				"measured sea surface temperature");
		aMap.put("tEqu", 
				"equilibrator chamber temperature");
		aMap.put("sal", 
				"measured salinity");
		aMap.put("pAtm", 
				"measured sea-level atmospheric pressure");
		aMap.put("pEqu", 
				"equilibrator chamber pressure");
		aMap.put("xCO2WaterSst", 
				"measured xCO2 (water) using sea surface temperature (dry air)");
		aMap.put("xCO2WaterTEqu", 
				"measured xCO2 (water) using equilibrator temperature (dry air)");
		aMap.put("fCO2WaterSst", 
				"measured pCO2 (water) using sea surface temperature (wet air)");
		aMap.put("fCO2WaterTEqu", 
				"measured pCO2 (water) using equilibrator temperature (wet air)");
		aMap.put("pCO2WaterSst", 
				"measured fCO2 (water) using sea surface temperature (wet air)");
		aMap.put("pCO2WaterTEqu", 
				"measured fCO2 (water) using equilibrator temperature (wet air)");
		aMap.put("woaSss",
				"sea surface salinity interpolated from the World Ocean Atlas 2005 " +
				"(see: //http://www.nodc.noaa.gov/OC5/WOA05/pr_woa05.html)");
		aMap.put("ncepSlp",
				"sea level pressure interpolated from the NCEP/NCAR 40-Year Reanalysis Project " +
				"(see: http://www.esrl.noaa.gov/psd/data/gridded/data.ncep.reanalysis.surface.html)");
		aMap.put("fCO2FromXCO2TEqu",
				"fCO2 recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2Sst",
				"fCO2 recomputed from measured xCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromPCO2TEqu",
				"fCO2 recomputed from measured pCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromPCO2Sst",
				"fCO2 recomputed from measured pCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromFCO2TEqu",
				"fCO2 recomputed from measured fCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromFCO2Sst",
				"fCO2 recomputed from measured fCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromPCO2TEquNcep",
				"fCO2 recomputed from measured pCO2 (water) using equilibrator temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromPCO2SstNcep",
				"fCO2 recomputed from measured pCO2 (water) using sea surface temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2TEquWoa",
				"fCO2 recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"equilibrator pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2SstWoa",
				"fCO2 recomputed from measured xCO2 (water) using sea surface temperature, " +
				"equilibrator pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2TEquNcep",
				"fCO2 recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2SstNcep",
				"fCO2 recomputed from measured xCO2 (water) using sea surface temperature, " +
				"NCEP sea level pressure, and measured salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2TEquNcepWoa",
				"fCO2 recomputed from measured xCO2 (water) using equilibrator temperature, " +
				"NCEP sea level pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2FromXCO2SstNcepWoa",
				"fCO2 recomputed from measured xCO2 (water) using sea surface temperature, " +
				"NCEP sea level pressure, and WOA sea surface salinity" +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2Rec",
				"fCO2 recomputed from the most desireable measured CO2 data " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("fCO2Source",
				"algorithm number (1-14) for generating the fCO2Rec value " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("deltaT",
				"difference in temperature between the equilibrator water and the sea surface water (TEqu - SST)");
		aMap.put("regionID",
				"SOCAT region ID for the location of this measurement " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");
		aMap.put("calcSpeed",
				"calculated ship speed using the previous and subsequent data measurement");
		aMap.put("etopo2",
				"bathymetry interpolated from the ETOPO2 2 arc-minute Gridded Global Relief Data " +
				"(see: http://www.ngdc.noaa.gov/mgg/global/etopo2.html)");
		aMap.put("gvCO2",
				"atmospheric xCO2 interpolated from GlobalView-CO2, 2012 1979-01-01 to 2012-01-01 data " +
				"(see: http://www.esrl.noaa.gov/gmd/ccgg/globalview/index.html)");
		aMap.put("distToLand",
				"estimated distance to major land mass (up to 1000 km) ");
		aMap.put("woceFlag",
				"WOCE quality-control flag (2=okay,3=questionable,4=bad) for the fCO2Rec value " +
				"(see: doi:10.5194/essd-5-125-2013  http://www.earth-syst-sci-data.net/5/125/2013/)");

		DESCRIPTION = Collections.unmodifiableMap(aMap);
	}
}
