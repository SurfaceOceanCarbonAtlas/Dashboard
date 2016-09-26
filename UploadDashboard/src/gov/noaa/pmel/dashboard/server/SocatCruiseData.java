/**
 */
package gov.noaa.pmel.dashboard.server;


import gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.WoceEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Class for working with data values of interest, both PI-provided
 * values and computed values, from a SOCAT cruise data measurement.
 * Note that WOCE flags are ignored in the hashCode and equals methods.
 * 
 * @author Karl Smith
 */
public class SocatCruiseData {

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

	// Integer types
	public static final DashDataType FCO2_SOURCE = new DashDataType("fCO2_source", 
			KnownDataTypes.INT_DATA_CLASS_NAME, "Algorithm number for recommended fCO2", 
			null, KnownDataTypes.IDENTIFIER_CATEGORY, DataColumnType.NO_UNITS);

	// Character types
	public static final DashDataType REGION_ID = new DashDataType("region_id", 
			KnownDataTypes.CHAR_DATA_CLASS_NAME, "SOCAT region ID", 
			null, KnownDataTypes.LOCATION_CATEGORY, DataColumnType.NO_UNITS);
	public static final DashDataType WOCE_CO2_WATER = new DashDataType("WOCE_CO2_water", 
			KnownDataTypes.CHAR_DATA_CLASS_NAME, "WOCE flag for water CO2", 
			null, KnownDataTypes.QUALITY_CATEGORY, DataColumnType.NO_UNITS);
	public static final DashDataType WOCE_CO2_ATM = new DashDataType("WOCE_CO2_atm", 
			KnownDataTypes.CHAR_DATA_CLASS_NAME, "WOCE flag for air CO2", 
			null, KnownDataTypes.QUALITY_CATEGORY, DataColumnType.NO_UNITS);

	// Double types
	public static final DashDataType SALINITY = new DashDataType("sal", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "salinity", 
			"sea_surface_salinity", KnownDataTypes.SALINITY_CATEGORY, SALINITY_UNITS);
	public static final DashDataType WOA_SALINITY = new DashDataType("woa_sss", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "salinity from World Ocean Atlas", 
			"sea_surface_salinity", KnownDataTypes.SALINITY_CATEGORY, SALINITY_UNITS);

	public static final DashDataType TEQU = new DashDataType("Temperature_equi", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "equilibrator chamber temperature", 
			null, KnownDataTypes.TEMPERATURE_CATEGORY, TEMPERATURE_UNITS);
	public static final DashDataType SST = new DashDataType("temp", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "sea surface temperature", 
			"sea_surface_temperature", KnownDataTypes.TEMPERATURE_CATEGORY, TEMPERATURE_UNITS);
	public static final DashDataType TATM = new DashDataType("Temperature_atm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "sea-level air temperature", 
			"air_temperature_at_sea_level", KnownDataTypes.TEMPERATURE_CATEGORY, TEMPERATURE_UNITS);

	public static final DashDataType PEQU = new DashDataType("Pressure_equi", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "equilibrator chamber pressure", 
			null, KnownDataTypes.PRESSURE_CATEGORY, PRESSURE_UNITS);
	public static final DashDataType PATM = new DashDataType("Pressure_atm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "sea-level air pressure", 
			"air_pressure_at_sea_level", KnownDataTypes.PRESSURE_CATEGORY, PRESSURE_UNITS);
	public static final DashDataType NCEP_SLP = new DashDataType("pressure_ncep_slp", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "sea level air pressure from NCEP/NCAR reanalysis", 
			"air_pressure_at_sea_level", KnownDataTypes.PRESSURE_CATEGORY, PRESSURE_UNITS);

	public static final DashDataType XCO2_WATER_TEQU_DRY = new DashDataType("xCO2_water_equi_temp_dry_ppm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "water xCO2 dry using equi temp", 
			"mole_fraction_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, XCO2_UNITS);
	public static final DashDataType XCO2_WATER_SST_DRY = new DashDataType("xCO2_water_sst_dry_ppm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "water xCO2 dry using sst", 
			"mole_fraction_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, XCO2_UNITS);
	public static final DashDataType XCO2_WATER_TEQU_WET = new DashDataType("xCO2_water_equi_temp_wet_ppm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "water xCO2 wet using equi temp", 
			"mole_fraction_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, XCO2_UNITS);
	public static final DashDataType XCO2_WATER_SST_WET = new DashDataType("xCO2_water_sst_wet_ppm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "water xCO2 wet using sst", 
			"mole_fraction_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, XCO2_UNITS);
	public static final DashDataType PCO2_WATER_TEQU_WET = new DashDataType("pCO2_water_equi_temp", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "water pCO2 wet using equi temp", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, PCO2_UNITS);
	public static final DashDataType PCO2_WATER_SST_WET = new DashDataType("pCO2_water_sst_100humidity_uatm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "water pCO2 wet using sst", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, PCO2_UNITS);
	public static final DashDataType FCO2_WATER_TEQU_WET = new DashDataType("fCO2_water_equi_uatm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "water fCO2 wet using equi temp", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_WATER_SST_WET = new DashDataType("fCO2_water_sst_100humidity_uatm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "water fCO2 wet using sst", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType XCO2_ATM_DRY_ACTUAL = new DashDataType("xCO2_atm_dry_actual", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "actual air xCO2 dry", 
			"mole_fraction_of_carbon_dioxide_in_air", KnownDataTypes.CO2_CATEGORY, XCO2_UNITS);
	public static final DashDataType XCO2_ATM_DRY_INTERP = new DashDataType("xCO2_atm_dry_interp", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "interpolated air xCO2 dry", 
			"mole_fraction_of_carbon_dioxide_in_air", KnownDataTypes.CO2_CATEGORY, XCO2_UNITS);
	public static final DashDataType PCO2_ATM_DRY_ACTUAL = new DashDataType("pCO2_atm_wet_actual", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "actual air pCO2 wet", 
			"surface_partial_pressure_of_carbon_dioxide_in_air", KnownDataTypes.CO2_CATEGORY, PCO2_UNITS);
	public static final DashDataType PCO2_ATM_DRY_INTERP = new DashDataType("pCO2_atm_wet_interp", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "interpolated air pCO2 wet", 
			"surface_partial_pressure_of_carbon_dioxide_in_air", KnownDataTypes.CO2_CATEGORY, PCO2_UNITS);
	public static final DashDataType FCO2_ATM_DRY_ACTUAL = new DashDataType("fCO2_atm_wet_actual", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "actual air fCO2 wet", 
			"surface_partial_pressure_of_carbon_dioxide_in_air", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_ATM_DRY_INTERP = new DashDataType("fCO2_atm_wet_interp", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "interpolated air fCO2 wet", 
			"surface_partial_pressure_of_carbon_dioxide_in_air", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType DELTA_XCO2 = new DashDataType("delta_xCO2", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "water xCO2 minus atmospheric xCO2", 
			null, KnownDataTypes.CO2_CATEGORY, XCO2_UNITS);
	public static final DashDataType DELTA_PCO2 = new DashDataType("delta_pCO2", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "water pCO2 minus atmospheric pCO2", 
			null, KnownDataTypes.CO2_CATEGORY, PCO2_UNITS);
	public static final DashDataType DELTA_FCO2 = new DashDataType("delta_fCO2", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "water fCO2 minus atmospheric fCO2", 
			null, KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType XH2O_EQU = new DashDataType("xH2O_equi", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "xH2O in equil air sample", 
			"mole_fraction_of_water_in_air", KnownDataTypes.WATER_VAPOR_CATEGORY, XH2O_UNITS);
	public static final DashDataType RELATIVE_HUMIDITY = new DashDataType("relative_humidity", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "relative humidity", 
			"relative_humidity", KnownDataTypes.WATER_VAPOR_CATEGORY, DataColumnType.NO_UNITS);
	public static final DashDataType SPECIFIC_HUMIDITY = new DashDataType("specific_humidity", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "specific humidity", 
			"specific_humidity", KnownDataTypes.WATER_VAPOR_CATEGORY, DataColumnType.NO_UNITS);

	public static final DashDataType SHIP_SPEED = new DashDataType("ship_speed", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "measured ship speed", 
			"platform_speed_wrt_ground", KnownDataTypes.PLATFORM_CATEGORY, SHIP_SPEED_UNITS);
	public static final DashDataType SHIP_DIRECTION = new DashDataType("ship_dir", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "ship direction", 
			"platform_course", KnownDataTypes.PLATFORM_CATEGORY, DIRECTION_UNITS);
	public static final DashDataType WIND_SPEED_TRUE = new DashDataType("wind_speed_true", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "true wind speed", 
			"wind_speed", KnownDataTypes.WIND_CATEGORY, WIND_SPEED_UNITS);
	public static final DashDataType WIND_SPEED_RELATIVE = new DashDataType("wind_speed_rel", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "relative wind speed", 
			"wind_speed", KnownDataTypes.WIND_CATEGORY, WIND_SPEED_UNITS);
	public static final DashDataType WIND_DIRECTION_TRUE = new DashDataType("wind_dir_true", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "true wind direction", 
			"wind_from_direction", KnownDataTypes.WIND_CATEGORY, DIRECTION_UNITS);
	public static final DashDataType WIND_DIRECTION_RELATIVE = new DashDataType("wind_dir_rel", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "relative wind direction", 
			"wind_from_direction", KnownDataTypes.WIND_CATEGORY, DIRECTION_UNITS);

	public static final DashDataType FCO2_FROM_XCO2_TEQU = new DashDataType("fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from xCO2_water_equi_temp_dry_ppm, Pressure_equi, sal", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_XCO2_SST = new DashDataType("fCO2_insitu_from_xCO2_water_sst_dry_ppm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from xCO2_water_sst_dry_ppm, Pressure_equi, sal", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_PCO2_TEQU = new DashDataType("fCO2_from_pCO2_water_water_equi_temp", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from pCO2_water_equi_temp, Pressure_equi, sal", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_PCO2_SST = new DashDataType("fCO2_from_pCO2_water_sst_100humidity_uatm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from pCO2_water_sst_100humidity_uatm, Pressure_equi, sal", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_FCO2_TEQU = new DashDataType("fCO2_insitu_from_fCO2_water_equi_uatm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from fCO2_water_equi_temp, Pressure_equi, sal", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_FCO2_SST = new DashDataType("fCO2_insitu_from_fCO2_water_sst_100humidty_uatm", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from fCO2_water_sst_100humidity_uatm, Pressure_equi, sal", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_PCO2_TEQU_NCEP = new DashDataType("fCO2_from_pCO2_water_water_equi_temp_ncep", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from pCO2_water_equi_temp, NCEP SLP, sal", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_PCO2_SST_NCEP = new DashDataType("fCO2_from_pCO2_water_sst_100humidity_uatm_ncep", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from pCO2_water_sst_100humidity_uatm, NCEP SLP, sal", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_XCO2_TEQU_WOA = new DashDataType("fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_woa", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from xCO2_water_equi_temp_dry_ppm, Pressure_equi, WOA SSS", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_XCO2_SST_WOA = new DashDataType("fCO2_insitu_from_xCO2_water_sst_dry_ppm_woa", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from xCO2_water_sst_dry_ppm, Pressure_equi, WOA SSS", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_XCO2_TEQU_NCEP = new DashDataType("fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from xCO2_water_equi_temp_dry_ppm, NCEP SLP, sal", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_XCO2_SST_NCEP = new DashDataType("fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from xCO2_water_sst_dry_ppm, NCEP SLP, sal", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FOC2_FROM_XCO2_TEQU_NCEP_WOA = new DashDataType("fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep_woa", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from xCO2_water_equi_temp_dry_ppm, NCEP SLP, WOA SSS", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);
	public static final DashDataType FCO2_FROM_XCO2_SST_NCEP_WOA = new DashDataType("fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep_woa", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 from xCO2_water_sst_dry_ppm, NCEP SLP, WOA SSS", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType FCO2_REC = new DashDataType("fCO2_recommended", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "fCO2 recommended", 
			"surface_partial_pressure_of_carbon_dioxide_in_sea_water", KnownDataTypes.CO2_CATEGORY, FCO2_UNITS);

	public static final DashDataType DELTA_TEMP = new DashDataType("delta_temp", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "Equilibrator Temp - SST", 
			null, KnownDataTypes.TEMPERATURE_CATEGORY, TEMPERATURE_UNITS);
	public static final DashDataType CALC_SPEED = new DashDataType("calc_speed", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "calculated ship speed", 
			"platform_speed_wrt_ground", KnownDataTypes.PLATFORM_CATEGORY, SHIP_SPEED_UNITS);
	public static final DashDataType ETOPO2_DEPTH = new DashDataType("etopo2", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "bathymetry from ETOPO2", 
			"sea_floor_depth", KnownDataTypes.BATHYMETRY_CATEGORY, KnownDataTypes.DEPTH_UNITS);
	public static final DashDataType GVCO2 = new DashDataType("gvCO2", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "GlobalView xCO2", 
			"mole_fraction_of_carbon_dioxide_in_air", KnownDataTypes.CO2_CATEGORY, XCO2_UNITS);
	public static final DashDataType DIST_TO_LAND = new DashDataType("dist_to_land", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "distance to land", 
			null, KnownDataTypes.LOCATION_CATEGORY, DISTANCE_UNITS);
	public static final DashDataType DAY_OF_YEAR = new DashDataType("day_of_year", 
			KnownDataTypes.DOUBLE_DATA_CLASS_NAME, "day of the year", 
			null, KnownDataTypes.TIME_CATEGORY, DAYS_UNITS);

	private LinkedHashMap<DashDataType,Character> charValsMap;
	private LinkedHashMap<DashDataType,Integer> intValsMap;
	private LinkedHashMap<DashDataType,Double> doubleValsMap;

	/**
	 * Generates a SocatCruiseData object with the given known types.
	 * Only the data class types 
	 * 	{@link KnownDataTypes#CHAR_DATA_CLASS_NAME},
	 * 	{@link KnownDataTypes#INT_DATA_CLASS_NAME}, and 
	 * 	{@link KnownDataTypes#DOUBLE_DATA_CLASS_NAME}
	 * are accepted at this time.
	 * Sets the values to the default values:
	 * 	{@link WoceEvent#WOCE_NOT_CHECKED} for WOCE flags (starts with "WOCE_"),
	 * 	{@link DataLocation#GLOBAL_REGION_ID} for {@link #REGION_ID},
	 * 	{@link DashboardUtils#CHAR_MISSING_VALUE} for other {@link KnownDataTypes#CHAR_DATA_CLASS_NAME} values.
	 * 	{@link DashboardUtils#INT_MISSING_VALUE} for {@link KnownDataTypes#INT_DATA_CLASS_NAME} values, and
	 * 	{@link DashboardUtils#FP_MISSING_VALUE} for {@link KnownDataTypes#DOUBLE_DATA_CLASS_NAME} values
	 * 
	 * @param knownTypes
	 * 		collection of all known types;
	 * 		cannot be null or empty
	 */
	public SocatCruiseData(KnownDataTypes knownTypes) {
		if ( (knownTypes == null) || (knownTypes.size() < 1) )
			throw new IllegalArgumentException("known data types cannot be null or empty");
		charValsMap = new LinkedHashMap<DashDataType,Character>();
		intValsMap = new LinkedHashMap<DashDataType,Integer>();
		doubleValsMap = new LinkedHashMap<DashDataType,Double>(96);

		for ( DashDataType dtype : knownTypes.getKnownTypesSet() ) {
			if ( KnownDataTypes.CHAR_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				if ( dtype.getVarName().toUpperCase().startsWith("WOCE_") ) {
					// WOCE flag
					charValsMap.put(dtype, WoceEvent.WOCE_NOT_CHECKED);
				}
				else if ( dtype.typeNameEquals(REGION_ID) ) {
					// Region ID
					charValsMap.put(dtype, DataLocation.GLOBAL_REGION_ID);
				}
				else {
					charValsMap.put(dtype, DashboardUtils.CHAR_MISSING_VALUE);
				}
			}
			else if ( KnownDataTypes.INT_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				intValsMap.put(dtype, DashboardUtils.INT_MISSING_VALUE);
			}
			else if ( KnownDataTypes.DOUBLE_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				doubleValsMap.put(dtype, DashboardUtils.FP_MISSING_VALUE);
			}
			else {
				throw new IllegalArgumentException("Unknown data class name '" + 
						dtype.getDataClassName() + "' associated with type '" + dtype.getVarName() + "'");
			}
		}
	}

	/**
	 * Creates from a list of data column types and corresponding data strings.
	 * This assumes the data in the strings are in the standard units for each
	 * type, and the missing value is "NaN", an empty string, or null.
	 * 
	 * An exception is thrown if a data column with type 
	 * {@link KnownDataTypes#UNKNOWN} is encountered; otherwise data columns
	 * with types not present in knownTypes are ignored.  The data types
	 * {@link DashDataType#UNKNOWN}, {@link DashDataType#OTHER}, and any
	 * metadata types should not be in knownTypes.
	 * 
	 * @param knownTypes
	 * 		list of known data types
	 * @param columnTypes
	 * 		types of the data values - only the variable name and data class 
	 * 		type is used
	 * @param sampleNum
	 * 		sequence number (starting with one) of this sample in the data set
	 * @param dataValues
	 * 		data value strings
	 * @throws IllegalArgumentException
	 * 		if the number of data types and data values do not match, 
	 * 		if a data column has the type {@link DashDataType#UNKNOWN}, 
	 * 		if a data column has a type matching a known data type but
	 * 			with a different data class type, or
	 * 		if a data value string cannot be parsed for the expected type 
	 */
	public SocatCruiseData(KnownDataTypes knownTypes, List<DashDataType> columnTypes, 
			int sampleNum, List<String> dataValues) throws IllegalArgumentException {
		// Initialize to an empty data record with the given known types
		this(knownTypes);
		// Verify the number of types and values match
		int numColumns = columnTypes.size();
		if ( dataValues.size() != numColumns )
			throw new IllegalArgumentException("Number of column types (" +
					numColumns + ") does not match the number of data values (" +
					dataValues.size() + ")");
		// Add values to the empty record
		if ( intValsMap.containsKey(KnownDataTypes.SAMPLE_NUMBER) )
			intValsMap.put(KnownDataTypes.SAMPLE_NUMBER, Integer.valueOf(sampleNum));
		for (int k = 0; k < numColumns; k++) {
			// Make sure the data type is valid
			DashDataType dtype = columnTypes.get(k);
			if ( KnownDataTypes.UNKNOWN.typeNameEquals(dtype) )
				throw new IllegalArgumentException("Data column number " + 
						Integer.toString(k+1) + " has type UNKNOWN");
			// Skip over missing values since the empty data record
			// is initialized with the missing value for data type
			String value = dataValues.get(k);
			if ( (value == null) || value.isEmpty() || value.equals("NaN") )
				continue;
			// Check if this data type is in the known list
			DataColumnType stdType = knownTypes.getDataColumnType(dtype.getVarName());
			if ( stdType == null )
				continue;
			if ( ! stdType.getDataClassName().equals(dtype.getDataClassName()) )
				throw new IllegalArgumentException("Data column type " + dtype.getVarName() + 
						" has data class " + dtype.getDataClassName() + 
						" instead of " + stdType.getDataClassName());
			// Assign the value
			if ( intValsMap.containsKey(dtype) ) {
				try {
					intValsMap.put(dtype, Integer.parseInt(value));
				} catch ( Exception ex ) {
					throw new IllegalArgumentException("Unable to parse '" + 
							value + "' as an Integer: " + ex.getMessage());
				}
			}
			else if ( doubleValsMap.containsKey(dtype) ) {
				try {
					doubleValsMap.put(dtype, Double.parseDouble(value));
				} catch ( Exception ex ) {
					throw new IllegalArgumentException("Unable to parse '" + 
							value + "' as a Double: " + ex.getMessage());
				}
			}
			else if ( charValsMap.containsKey(dtype) ) {
				if ( value.length() != 1 )
					throw new IllegalArgumentException("More than one character in '" + value + "'");
				charValsMap.put(dtype, value.charAt(0));
			}
			else {
				throw new RuntimeException("Unexpected failure to place data type " + dtype.toString());
			}
		}
	}

	/**
	 * Creates a list of these data objects from the values and data column
	 * types given in a dashboard cruise with data.  This assumes the data
	 * is in the standard units for each type, and the missing value is
	 * "NaN", and empty string, or null.
	 * 
	 * An exception is thrown if a data column with type 
	 * {@link DashDataType#UNKNOWN} is encountered; otherwise data columns
	 * with types not present in knownTypes are ignored.  The data types
	 * {@link DashDataType#UNKNOWN}, {@link DashDataType#OTHER}, and any
	 * metadata types should not be in knownTypes.
	 * 
	 * @param knownTypes
	 * 		list of known data types
	 * @param cruise
	 * 		dashboard cruise with data
	 * @return
	 * 		list of these data objects
	 * @throws IllegalArgumentException
	 * 		if a row of data values has an unexpected number of values,
	 * 		if a data column has the type {@link DashDataType#UNKNOWN}, 
	 * 		if a data column has a type matching a known data type but
	 * 			with a different data class type, or
	 * 		if a data value string cannot be parsed for the expected type 
	 */
	public static ArrayList<SocatCruiseData> dataListFromDashboardCruise(
			KnownDataTypes knownTypes, DashboardCruiseWithData cruise) 
					throws IllegalArgumentException {
		// Get the required data from the cruise
		ArrayList<ArrayList<String>> dataValsTable = cruise.getDataValues();
		ArrayList<DataColumnType> dataColTypes = cruise.getDataColTypes();
		// Create the list of DashDataType objects - assumes data already standardized
		ArrayList<DashDataType> dataTypes = new ArrayList<DashDataType>(dataColTypes.size());
		for ( DataColumnType dctype : dataColTypes )
			dataTypes.add( new DashDataType(dctype) );
		// Create the list of SOCAT cruise data objects, and populate
		// it with data from each row of the table
		ArrayList<SocatCruiseData> socatDataList = 
				new ArrayList<SocatCruiseData>(dataValsTable.size());
		for (int k = 0; k < dataValsTable.size(); k++) {
			socatDataList.add( new SocatCruiseData(knownTypes, 
					dataTypes, k+1, dataValsTable.get(k)) );
		}
		return socatDataList;
	}

	/**
	 * @return
	 * 		the map of variable names and values for String variables;
	 * 		the actual map in this instance is returned.
	 */
	public LinkedHashMap<DashDataType,Character> getCharacterVariables() {
		return charValsMap;
	}

	/**
	 * Updates the given Character type variable with the given value.
	 * 
	 * @param dtype
	 * 		the data type of the value
	 * @param value
	 * 		the value to assign; 
	 * 		if null, {@link DashboardUtils#CHAR_MISSING_VALUE} is assigned
	 * @throws IllegalArgumentException
	 * 		if the data type variable is not a known data type in this data
	 */
	public void setCharacterVariableValue(DashDataType dtype, Character value) throws IllegalArgumentException {
		if ( ! charValsMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown data character variable " + dtype.getVarName());
		if ( value == null )
			charValsMap.put(dtype, DashboardUtils.CHAR_MISSING_VALUE);
		else
			charValsMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the map of variable names and values for Double variables;
	 * 		the actual map in this instance is returned.
	 */
	public LinkedHashMap<DashDataType,Double> getDoubleVariables() {
		return doubleValsMap;
	}

	/**
	 * Updates the given Double type variable with the given value.
	 * 
	 * @param dtype
	 * 		the data type of the value
	 * @param value
	 * 		the value to assign; 
	 * 		if null, NaN, or infinite, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 * @throws IllegalArgumentException
	 * 		if the data type variable is not a known data type in this data
	 */
	public void setDoubleVariableValue(DashDataType dtype, Double value) throws IllegalArgumentException {
		if ( ! doubleValsMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown data double variable " + dtype.getVarName());
		if ( (value == null) || value.isNaN() || value.isInfinite() )
			doubleValsMap.put(dtype, DashboardUtils.FP_MISSING_VALUE);
		else
			doubleValsMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the map of variable names and values for Integer variables;
	 * 		the actual map in this instance is returned.
	 */
	public LinkedHashMap<DashDataType,Integer> getIntegerVariables() {
		return intValsMap;
	}

	/**
	 * Updates the given Integer type variable with the given value.
	 * 
	 * @param dtype
	 * 		the data type of the value
	 * @param value
	 * 		the value to assign; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 * @throws IllegalArgumentException
	 * 		if the data type variable is not a known data type in this data
	 */
	public void setIntegerVariableValue(DashDataType dtype, Integer value) throws IllegalArgumentException {
		if ( ! intValsMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown data double variable " + dtype.getVarName());
		if ( value == null )
			intValsMap.put(dtype, DashboardUtils.INT_MISSING_VALUE);
		else
			intValsMap.put(dtype, value);
	}

	/**
	 * @return 
	 * 		the year of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getYear() {
		Integer value = intValsMap.get(KnownDataTypes.YEAR);
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param year 
	 * 		the year of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setYear(Integer year) {
		Integer value = year;
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(KnownDataTypes.YEAR, value);
	}

	/**
	 * @return 
	 * 		the month of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getMonth() {
		Integer value = intValsMap.get(KnownDataTypes.MONTH_OF_YEAR);
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param month 
	 * 		the month of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setMonth(Integer month) {
		Integer value = month;
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(KnownDataTypes.MONTH_OF_YEAR, value);
	}

	/**
	 * @return 
	 * 		the day of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getDay() {
		Integer value = intValsMap.get(KnownDataTypes.DAY_OF_MONTH);
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param day 
	 * 		the day of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setDay(Integer day) {
		Integer value = day;
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(KnownDataTypes.DAY_OF_MONTH, value);
	}

	/**
	 * @return 
	 * 		the hour of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getHour() {
		Integer value = intValsMap.get(KnownDataTypes.HOUR_OF_DAY);
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param hour 
	 * 		the hour of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setHour(Integer hour) {
		Integer value = hour;
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(KnownDataTypes.HOUR_OF_DAY, value);
	}

	/**
	 * @return 
	 * 		the minute of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getMinute() {
		Integer value = intValsMap.get(KnownDataTypes.MINUTE_OF_HOUR);
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param minute 
	 * 		the minute of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setMinute(Integer minute) {
		Integer value = minute;
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(KnownDataTypes.MINUTE_OF_HOUR, value);
	}

	/**
	 * @return 
	 * 		the second of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getSecond() {
		Double value = doubleValsMap.get(KnownDataTypes.SECOND_OF_MINUTE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param second 
	 * 		the second of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setSecond(Double second) {
		Double value = second;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(KnownDataTypes.SECOND_OF_MINUTE, value);
	}

	/**
	 * @return 
	 * 		the longitude of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getLongitude() {
		Double value = doubleValsMap.get(KnownDataTypes.LONGITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param longitude 
	 * 		the longitude of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setLongitude(Double longitude) {
		Double value = longitude;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(KnownDataTypes.LONGITUDE, value);
	}

	/**
	 * @return 
	 * 		the latitude of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getLatitude() {
		Double value = doubleValsMap.get(KnownDataTypes.LATITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param latitude 
	 * 		the latitude of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setLatitude(Double latitude) {
		Double value = latitude;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(KnownDataTypes.LATITUDE, value);
	}

	/**
	 * @return 
	 * 		the sampling depth;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getSampleDepth() {
		Double value = doubleValsMap.get(KnownDataTypes.SAMPLE_DEPTH);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param sampleDepth
	 * 		the sampling depth to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setSampleDepth(Double sampleDepth) {
		Double value = sampleDepth;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(KnownDataTypes.SAMPLE_DEPTH, value);
	}

	/**
	 * @return 
	 * 		the WOCE flags for aqueous CO2;
	 * 		never null but could be {@link WoceEvent#WOCE_NOT_CHECKED} if not assigned
	 */
	public Character getWoceCO2Water() {
		Character value = charValsMap.get(WOCE_CO2_WATER);
		if ( value == null )
			value = WoceEvent.WOCE_NOT_CHECKED;
		return value;
	}

	/**
	 * @param woceCO2Water 
	 * 		the WOCE flags for aqueous CO2 to set;
	 * 		if null, {@link WoceEvent#WOCE_NOT_CHECKED} is assigned
	 */
	public void setWoceCO2Water(Character woceCO2Water) {
		Character value = woceCO2Water;
		if ( value == null )
			value = WoceEvent.WOCE_NOT_CHECKED;
		charValsMap.put(WOCE_CO2_WATER, value);
	}

	/**
	 * @return 
	 * 		the WOCE flag for atmospheric CO2;
	 * 		never null but could be {@link WoceEvent#WOCE_NOT_CHECKED} if not assigned
	 */
	public Character getWoceCO2Atm() {
		Character value = charValsMap.get(WOCE_CO2_ATM);
		if ( value == null )
			value = WoceEvent.WOCE_NOT_CHECKED;
		return value;
	}

	/**
	 * @param woceCO2Atm 
	 * 		the WOCE flag for atmospheric CO2 to set;
	 * 		if null, {@link #WoceEvent#WOCE_NOT_CHECKED} is assigned
	 */
	public void setWoceCO2Atm(Character woceCO2Atm) {
		Character value = woceCO2Atm;
		if ( value == null )
			value = WoceEvent.WOCE_NOT_CHECKED;
		charValsMap.put(WOCE_CO2_ATM, value);
	}

	/**
	 * @return 
	 * 		the region ID;
	 * 		never null but could be {@link DataLocation#GLOBAL_REGION_ID} if not assigned
	 */
	public Character getRegionID() {
		Character value = charValsMap.get(REGION_ID);
		if ( value == null )
			value = DataLocation.GLOBAL_REGION_ID;
		return value;
	}

	/**
	 * @param regionID 
	 * 		the region ID to set;
	 * 		if null, {@link DataLocation#GLOBAL_REGION_ID} is assigned
	 */
	public void setRegionID(Character regionID) {
		Character value = regionID;
		if ( value == null )
			value = DataLocation.GLOBAL_REGION_ID;
		charValsMap.put(REGION_ID, value);
	}

	/**
	 * @return 
	 * 		the method used to create the recomputed fCO2;
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getFCO2Source() {
		Integer value = intValsMap.get(FCO2_SOURCE);
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param fCO2Source 
	 * 		the method used to create the recomputed fCO2 to set;
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setFCO2Source(Integer fCO2Source) {
		Integer value = fCO2Source;
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(FCO2_SOURCE, value);
	}

	/**
	 * @return 
	 * 		the sea surface salinity;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getSalinity() {
		Double value = doubleValsMap.get(SALINITY);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param salinity 
	 * 		the sea surface salinity to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setSalinity(Double salinity) {
		Double value = salinity;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(SALINITY, value);
	}

	/**
	 * @return 
	 * 		the equilibrator temperature;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getTEqu() {
		Double value = doubleValsMap.get(TEQU);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param tEqu 
	 * 		the equilibrator temperature to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setTEqu(Double tEqu) {
		Double value = tEqu;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(TEQU, value);
	}

	/**
	 * @return 
	 * 		the sea surface temperature;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getSst() {
		Double value = doubleValsMap.get(SST);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param sst 
	 * 		the sea surface temperature to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setSst(Double sst) {
		Double value = sst;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(SST, value);
	}

	/**
	 * @return 
	 * 		the atmospheric sea-level pressure;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getPAtm() {
		Double value = doubleValsMap.get(PATM);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param pAtm 
	 * 		the atmospheric sea-level pressure to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setPAtm(Double pAtm) {
		Double value = pAtm;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(PATM, value);
	}

	/**
	 * @return 
	 * 		the equilibrator pressure;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getPEqu() {
		Double value = doubleValsMap.get(PEQU);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param pEqu 
	 * 		the equilibrator pressure to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setPEqu(Double pEqu) {
		Double value = pEqu;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(PEQU, value);
	}

	/**
	 * @return 
	 * 		xCO2 water TEqu dry;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getXCO2WaterTEquDry() {
		Double value = doubleValsMap.get(XCO2_WATER_TEQU_DRY);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param xCO2WaterTEquDry 
	 * 		xCO2 water TEqu dry to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setXCO2WaterTEquDry(Double xCO2WaterTEquDry) {
		Double value = xCO2WaterTEquDry;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(XCO2_WATER_TEQU_DRY, value);
	}

	/**
	 * @return 
	 * 		xCO2 water SST dry;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getXCO2WaterSstDry() {
		Double value = doubleValsMap.get(XCO2_WATER_SST_DRY);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param xCO2WaterSstDry 
	 * 		xCO2 water SST dry to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setXCO2WaterSstDry(Double xCO2WaterSstDry) {
		Double value = xCO2WaterSstDry;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(XCO2_WATER_SST_DRY, value);
	}

	/**
	 * @return 
	 * 		pCO2 water TEqu wet;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getPCO2WaterTEquWet() {
		Double value = doubleValsMap.get(PCO2_WATER_TEQU_WET);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param pCO2WaterTEquWet 
	 * 		pCO2 water TEqu wet to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setPCO2WaterTEquWet(Double pCO2WaterTEquWet) {
		Double value = pCO2WaterTEquWet;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(PCO2_WATER_TEQU_WET, value);
	}

	/**
	 * @return 
	 * 		pCO2 water SST wet;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getPCO2WaterSstWet() {
		Double value = doubleValsMap.get(PCO2_WATER_SST_WET);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param pCO2WaterSstWet 
	 * 		pCO2 water SST wet to set
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setPCO2WaterSstWet(Double pCO2WaterSstWet) {
		Double value = pCO2WaterSstWet;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(PCO2_WATER_SST_WET, value);
	}

	/**
	 * @return 
	 * 		fCO2 water TEqu wet;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getFCO2WaterTEquWet() {
		Double value = doubleValsMap.get(FCO2_WATER_TEQU_WET);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param fCO2WaterTEquWet 
	 * 		fCO2 water TEqu wet to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2WaterTEquWet(Double fCO2WaterTEquWet) {
		Double value = fCO2WaterTEquWet;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(FCO2_WATER_TEQU_WET, value);
	}

	/**
	 * @return 
	 * 		fCO2 water SST wet;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getFCO2WaterSstWet() {
		Double value = doubleValsMap.get(FCO2_WATER_SST_WET);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param fCO2WaterSstWet 
	 * 		fCO2 water SST wet to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2WaterSstWet(Double fCO2WaterSstWet) {
		Double value = fCO2WaterSstWet;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(FCO2_WATER_SST_WET, value);
	}

	/**
	 * @return 
	 * 		the WOA sea surface salinity;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getWoaSalinity() {
		Double value = doubleValsMap.get(WOA_SALINITY);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param woaSss 
	 * 		the WOA sea surface salinity to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setWoaSalinity(Double woaSss) {
		Double value = woaSss;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(WOA_SALINITY, value);
	}

	/**
	 * @return 
	 * 		the NCEP sea level pressure;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getNcepSlp() {
		Double value = doubleValsMap.get(NCEP_SLP);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param ncepSlp 
	 * 		the NCEP sea level pressure to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setNcepSlp(Double ncepSlp) {
		Double value = ncepSlp;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(NCEP_SLP, value);
	}

	/**
	 * @return 
	 * 		the recomputed fCO2;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2Rec() {
		Double value = doubleValsMap.get(FCO2_REC);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param fCO2Rec 
	 * 		the recomputed fCO2 to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2Rec(Double fCO2Rec) {
		Double value = fCO2Rec;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(FCO2_REC, value);
	}

	/**
	 * @return 
	 * 		the ETOPO2 depth;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getEtopo2Depth() {
		Double value = doubleValsMap.get(ETOPO2_DEPTH);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param etopo2
	 * 		the ETOPO2 depth to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setEtopo2Depth(Double etopo2) {
		Double value = etopo2;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(ETOPO2_DEPTH, value);
	}

	/**
	 * @return 
	 * 		the GlobablView CO2;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getGvCO2() {
		Double value = doubleValsMap.get(GVCO2);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param gvCO2 
	 * 		the GlobablView CO2 to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setGvCO2(Double gvCO2) {
		Double value = gvCO2;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(GVCO2, value);
	}

	/**
	 * @return 
	 * 		the distance to nearest major land mass;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getDistToLand() {
		Double value = doubleValsMap.get(DIST_TO_LAND);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param distToLand 
	 * 		the distance to nearest major land mass to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setDistToLand(Double distToLand) {
		Double value = distToLand;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(DIST_TO_LAND, value);
	}

	@Override 
	public int hashCode() {
		// Ignore WOCE flag differences.
		// Do not use floating-point fields since they do not 
		// have to be exactly the same for equals to return true.
		LinkedHashMap<DashDataType,Character> nonWoceCharValsMap = 
				new LinkedHashMap<DashDataType,Character>(charValsMap.size());
		for ( Entry<DashDataType,Character> entry : charValsMap.entrySet() ) {
			if ( ! entry.getKey().getVarName().toUpperCase().startsWith("WOCE_") )
				nonWoceCharValsMap.put(entry.getKey(), entry.getValue());
		}
		final int prime = 37;
		int result = nonWoceCharValsMap.hashCode();
		result = result * prime + intValsMap.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof SocatCruiseData) )
			return false;
		SocatCruiseData other = (SocatCruiseData) obj;

		// Integer comparisons
		if ( ! intValsMap.equals(other.intValsMap) )
			return false;

		// Character comparisons - ignore WOCE flag differences
		if ( ! charValsMap.keySet().equals(other.charValsMap.keySet()) )
			return false;
		for ( Entry<DashDataType,Character> entry : charValsMap.entrySet() ) {
			DashDataType dtype = entry.getKey();
			if ( ! dtype.getVarName().toUpperCase().startsWith("WOCE_") ) {
				if ( ! entry.getValue().equals(other.charValsMap.get(dtype)) )
					return false;
			}
		}

		// Floating-point comparisons - values don't have to be exactly the same
		if ( ! doubleValsMap.keySet().equals(other.doubleValsMap.keySet()) )
			return false;
		for ( Entry<DashDataType,Double> entry : doubleValsMap.entrySet() ) {
			DashDataType dtype = entry.getKey();
			Double thisval = entry.getValue();
			Double otherval = other.doubleValsMap.get(dtype);

			if ( dtype.typeNameEquals(KnownDataTypes.SECOND_OF_MINUTE) ) {
				// Match seconds not given (FP_MISSING_VALUE) with zero seconds
				if ( ! DashboardUtils.closeTo(thisval, otherval, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
					if ( ! ( thisval.equals(DashboardUtils.FP_MISSING_VALUE) && otherval.equals(Double.valueOf(0.0)) ) ) {
						if ( ! ( thisval.equals(Double.valueOf(0.0)) && otherval.equals(DashboardUtils.FP_MISSING_VALUE) ) ) {
							return false;
						}
					}
				}
			}
			else if ( dtype.getVarName().toUpperCase().contains("LONGITUDE") ) {
				// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
				if ( ! DashboardUtils.longitudeCloseTo(thisval, otherval, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
					return false;				
			}
			else {
				if ( ! DashboardUtils.closeTo(thisval, otherval, 
						DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
					return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		String repr = "SocatCruiseData[\n";
		for ( Entry<DashDataType,Integer> entry : intValsMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getValue().toString() + "\n";
		for ( Entry<DashDataType,Double> entry : doubleValsMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getKey().toString() + "\n";
		for ( Entry<DashDataType,Character> entry : charValsMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getKey().toString() + "\n";
		repr += "]";
		return repr;
	}

}
