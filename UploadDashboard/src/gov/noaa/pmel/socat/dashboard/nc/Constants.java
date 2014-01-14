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
        aMap.put("year", DashboardUtils.STD_DATA_UNITS.get(DataColumnType.YEAR));
        aMap.put("month",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.MONTH));
        aMap.put("day",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.DAY));
        aMap.put("hour",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.HOUR));
        aMap.put("minute",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.MINUTE));
        aMap.put("second",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SECOND));
        aMap.put("longitude",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.LONGITUDE));
        aMap.put("latitude",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.LATITUDE));
        aMap.put("sampleDepth",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SAMPLE_DEPTH));
        aMap.put("sst",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SEA_SURFACE_TEMPERATURE));
        aMap.put("tEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.EQUILIBRATOR_TEMPERATURE)); // Guess
        aMap.put("sal",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SALINITY));
        aMap.put("pAtm",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SEA_LEVEL_PRESSURE));  // This is wrong!
        aMap.put("pEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.EQUILIBRATOR_PRESSURE)); // Guess
        aMap.put("xCO2WaterSst",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.XCO2WATER_SST));
        aMap.put("xCO2WaterTEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.XCO2WATER_EQU));
        aMap.put("fCO2WaterSst",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_EQU));
        aMap.put("fCO2WaterTEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_SST));
        aMap.put("pCO2WaterSst",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.PCO2WATER_SST));
        aMap.put("pCO2WaterTEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.PCO2WATER_EQU));
        aMap.put("woaSss",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SEA_SURFACE_TEMPERATURE)); // Not specific to WOA.
        aMap.put("ncepSlp",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.SEA_LEVEL_PRESSURE)); // not specific to ncep.
        aMap.put("fCO2FromXCO2TEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_EQU)); // not specific to from xc02equ
        aMap.put("fCO2FromXCO2Sst",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_SST)); // Same problem
        aMap.put("fCO2FromPCO2TEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_EQU)); // WRONG!
        aMap.put("fCO2FromPCO2Sst",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("fCO2FromFCO2TEqu",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_EQU)); //WRONG!
        aMap.put("fCO2FromFCO2Sst",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_EQU));// WRONG!
        aMap.put("fCO2FromPCO2TEquNcep",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_SST)); //WRONG!
        aMap.put("fCO2FromPCO2SstNcep",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("fCO2FromXCO2TEquWoa",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_EQU)); // WRONG!
        aMap.put("fCO2FromXCO2SstWoa",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("fCO2FromXCO2TEquNcep",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_EQU));// WRONG!
        aMap.put("fCO2FromXCO2SstNcep",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("fCO2FromXCO2TEquNcepWoa",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_EQU)); // WRONG!
        aMap.put("fCO2FromXCO2SstNcepWoa",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("fCO2Rec",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_EQU)); // What's this?
        aMap.put("fCO2Source",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("deltaT",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.HOUR)); // WRONG!
        aMap.put("regionID",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.UNKNOWN)); // WRONG!
        aMap.put("calcSpeed",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.UNKNOWN));// WRONG!
        aMap.put("etopo2",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.UNKNOWN));// WRONG!
        aMap.put("gvCO2",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.UNKNOWN));// WRONG!
        aMap.put("distToLand",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.UNKNOWN));// WRONG!
        aMap.put("woceFlag",DashboardUtils.STD_DATA_UNITS.get(DataColumnType.UNKNOWN));// WRONG!
        UNITS = Collections.unmodifiableMap(aMap);
    }

    public static final Map<String, String> DESCRIPTION;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("year", DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.YEAR));
        aMap.put("month",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.MONTH));
        aMap.put("day",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.DAY));
        aMap.put("hour",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.HOUR));
        aMap.put("minute",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.MINUTE));
        aMap.put("second",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.SECOND));
        aMap.put("longitude",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.LONGITUDE));
        aMap.put("latitude",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.LATITUDE));
        aMap.put("sampleDepth",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.SAMPLE_DEPTH));
        aMap.put("sst",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.SEA_SURFACE_TEMPERATURE));
        aMap.put("tEqu",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.EQUILIBRATOR_TEMPERATURE)); // Guess
        aMap.put("sal",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.SALINITY));
        aMap.put("pAtm",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.SEA_LEVEL_PRESSURE));  // This is wrong!
        aMap.put("pEqu",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.EQUILIBRATOR_PRESSURE)); // Guess
        aMap.put("xCO2WaterSst",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.XCO2WATER_SST));
        aMap.put("xCO2WaterTEqu",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.XCO2WATER_EQU));
        aMap.put("fCO2WaterSst",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_EQU));
        aMap.put("fCO2WaterTEqu",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_SST));
        aMap.put("pCO2WaterSst",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.PCO2WATER_SST));
        aMap.put("pCO2WaterTEqu",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.PCO2WATER_EQU));
        aMap.put("woaSss",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.SEA_SURFACE_TEMPERATURE)); // Not specific to WOA.
        aMap.put("ncepSlp",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.SEA_LEVEL_PRESSURE)); // not specific to ncep.
        aMap.put("fCO2FromXCO2TEqu",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_EQU)); // not specific to from xc02equ
        aMap.put("fCO2FromXCO2Sst",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_SST)); // Same problem
        aMap.put("fCO2FromPCO2TEqu",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_EQU)); // WRONG!
        aMap.put("fCO2FromPCO2Sst",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("fCO2FromFCO2TEqu",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_EQU)); //WRONG!
        aMap.put("fCO2FromFCO2Sst",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_EQU));// WRONG!
        aMap.put("fCO2FromPCO2TEquNcep",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_SST)); //WRONG!
        aMap.put("fCO2FromPCO2SstNcep",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("fCO2FromXCO2TEquWoa",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_EQU)); // WRONG!
        aMap.put("fCO2FromXCO2SstWoa",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("fCO2FromXCO2TEquNcep",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_EQU));// WRONG!
        aMap.put("fCO2FromXCO2SstNcep",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("fCO2FromXCO2TEquNcepWoa",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_EQU)); // WRONG!
        aMap.put("fCO2FromXCO2SstNcepWoa",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("fCO2Rec",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_EQU)); // What's this?
        aMap.put("fCO2Source",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.FCO2WATER_SST)); // WRONG!
        aMap.put("deltaT",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.HOUR)); // WRONG!
        aMap.put("regionID",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.UNKNOWN)); // WRONG!
        aMap.put("calcSpeed",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.UNKNOWN));// WRONG!
        aMap.put("etopo2",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.UNKNOWN));// WRONG!
        aMap.put("gvCO2",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.UNKNOWN));// WRONG!
        aMap.put("distToLand",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.UNKNOWN));// WRONG!
        aMap.put("woceFlag",DashboardUtils.STD_DESCRIPTIONS.get(DataColumnType.UNKNOWN));// WRONG!
        DESCRIPTION = Collections.unmodifiableMap(aMap);
    }
}
