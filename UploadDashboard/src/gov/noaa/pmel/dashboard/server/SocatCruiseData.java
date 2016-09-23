/**
 */
package gov.noaa.pmel.dashboard.server;


import gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.WoceEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class for working with data values of interest, both PI-provided
 * values and computed values, from a SOCAT cruise data measurement.
 * Note that WOCE flags are ignored in the hashCode and equals methods.
 * 
 * @author Karl Smith
 */
public class SocatCruiseData {

	// Double types
	public static final String SALINITY_VARNAME = "sal";
	public static final String WOA_SALINITY_VARNAME = "woa_sss";

	public static final String TEQU_VARNAME = "Temperature_equi";
	public static final String SST_VARNAME = "temp";
	public static final String TATM_VARNAME = "Temperature_atm";

	public static final String PEQU_VARNAME = "Pressure_equi";
	public static final String PATM_VARNAME = "Pressure_atm";
	public static final String NCEP_SLP_VARNAME = "pressure_ncep_slp";

	public static final String XCO2_WATER_TEQU_DRY_VARNAME = "xCO2_water_equi_temp_dry_ppm";
	public static final String XCO2_WATER_SST_DRY_VARNAME = "xCO2_water_sst_dry_ppm";
	public static final String XCO2_WATER_TEQU_WET_VARNAME = "xCO2_water_equi_temp_wet_ppm";
	public static final String XCO2_WATER_SST_WET_VARNAME = "xCO2_water_sst_wet_ppm";
	public static final String PCO2_WATER_TEQU_WET_VARNAME = "pCO2_water_equi_temp";
	public static final String PCO2_WATER_SST_WET_VARNAME = "pCO2_water_sst_100humidity_uatm";
	public static final String FCO2_WATER_TEQU_WET_VARNAME = "fCO2_water_equi_uatm";
	public static final String FCO2_WATER_SST_WET_VARNAME = "fCO2_water_sst_100humidity_uatm";

	public static final String XCO2_ATM_DRY_ACTUAL_VARNAME = "xCO2_atm_dry_actual";
	public static final String XCO2_ATM_DRY_INTERP_VARNAME = "xCO2_atm_dry_interp";
	public static final String PCO2_ATM_DRY_ACTUAL_VARNAME = "pCO2_atm_wet_actual";
	public static final String PCO2_ATM_DRY_INTERP_VARNAME = "pCO2_atm_wet_interp";
	public static final String FCO2_ATM_DRY_ACTUAL_VARNAME = "fCO2_atm_wet_actual";
	public static final String FCO2_ATM_DRY_INTERP_VARNAME = "fCO2_atm_wet_interp";

	public static final String DELTA_XCO2_VARNAME = "delta_xCO2";
	public static final String DELTA_PCO2_VARNAME = "delta_pCO2";
	public static final String DELTA_FCO2_VARNAME = "delta_fCO2";

	public static final String XH2O_EQU_VARNAME = "xH2O_equi";
	public static final String RELATIVE_HUMIDITY_VARNAME =  "relative_humidity";
	public static final String SPECIFIC_HUMIDITY_VARNAME = "specific_humidity";

	public static final String SHIP_SPEED_VARNAME = "ship_speed";
	public static final String SHIP_DIRECTION_VARNAME = "ship_dir";
	public static final String WIND_SPEED_TRUE_VARNAME = "wind_speed_true";
	public static final String WIND_SPEED_RELATIVE_VARNAME = "wind_speed_rel";
	public static final String WIND_DIRECTION_TRUE_VARNAME = "wind_dir_true";
	public static final String WIND_DIRECTION_RELATIVE_VARNAME = "wind_dir_rel";

	public static final String FCO2_FROM_XCO2_TEQU_VARNAME = "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm";
	public static final String FCO2_FROM_XCO2_SST_VARNMAE = "fCO2_insitu_from_xCO2_water_sst_dry_ppm";
	public static final String FCO2_FROM_PCO2_TEQU_VARNAME = "fCO2_from_pCO2_water_water_equi_temp";
	public static final String FCO2_FROM_PCO2_SST_VARNAME = "fCO2_from_pCO2_water_sst_100humidity_uatm";
	public static final String FCO2_FROM_FCO2_TEQU_VARNAME = "fCO2_insitu_from_fCO2_water_equi_uatm";
	public static final String FCO2_FROM_FCO2_SST_VARNAME = "fCO2_insitu_from_fCO2_water_sst_100humidty_uatm";
	public static final String FCO2_FROM_PCO2_TEQU_NCEP_VARNAME = "fCO2_from_pCO2_water_water_equi_temp_ncep";
	public static final String FCO2_FROM_PCO2_SST_NCEP_VARNAME = "fCO2_from_pCO2_water_sst_100humidity_uatm_ncep";
	public static final String FCO2_FROM_XCO2_TEQU_WOA_VARNAME = "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_woa";
	public static final String FCO2_FROM_XCO2_SST_WOA_VARNAME = "fCO2_insitu_from_xCO2_water_sst_dry_ppm_woa";
	public static final String FCO2_FROM_XCO2_TEQU_NCEP_VARNAME = "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep";
	public static final String FCO2_FROM_XCO2_SST_NCEP_VARNAME = "fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep";
	public static final String FOC2_FROM_XCO2_TEQU_NCEP_WOA_VARNAME = "fCO2_insitu_from_xCO2_water_equi_temp_dry_ppm_ncep_woa";
	public static final String FCO2_FROM_XCO2_SST_NCEP_WOA_VARNAME = "fCO2_insitu_from_xCO2_water_sst_dry_ppm_ncep_woa";

	public static final String FCO2_REC_VARNAME = "fCO2_recommended";

			// deltaT = tEqu - SST
	public static final String DELTA_TEMP_VARNAME = "delta_temp";

	public static final String CALC_SPEED_VARNAME = "calc_speed";
	public static final String ETOPO2_DEPTH_VARNAME = "etopo2";
	public static final String GVCO2_VARNAME = "gvCO2";
	public static final String DIST_TO_LAND_VARNAME = "dist_to_land";
	public static final String DAY_OF_YEAR_VARNAME = "day_of_year";

	// Integer types
	public static final String FCO2_SOURCE_VARNAME = "fCO2_source";

	// Character types
	public static final String REGION_ID_VARNAME = "region_id";
	public static final String WOCE_CO2_WATER_VARNAME = "WOCE_CO2_water";
	public static final String WOCE_CO2_ATM_VARNAME = "WOCE_CO2_atm";


	private LinkedHashMap<String,Integer> intValsMap;
	private LinkedHashMap<String,Double> doubleValsMap;
	private LinkedHashMap<String,Character> charValsMap;

	/**
	 * Generates an empty SOCAT data record
	 */
	public SocatCruiseData() {
		
		intValsMap = new LinkedHashMap<String,Integer>();

		intValsMap.put(KnownDataTypes.SAMPLE_NUMBER.getVarName(), DashboardUtils.INT_MISSING_VALUE);
		intValsMap.put(KnownDataTypes.YEAR.getVarName(), DashboardUtils.INT_MISSING_VALUE);
		intValsMap.put(KnownDataTypes.MONTH_OF_YEAR.getVarName(), DashboardUtils.INT_MISSING_VALUE);
		intValsMap.put(KnownDataTypes.DAY_OF_MONTH.getVarName(), DashboardUtils.INT_MISSING_VALUE);
		intValsMap.put(KnownDataTypes.HOUR_OF_DAY.getVarName(), DashboardUtils.INT_MISSING_VALUE);
		intValsMap.put(KnownDataTypes.MINUTE_OF_HOUR.getVarName(), DashboardUtils.INT_MISSING_VALUE);
		intValsMap.put(KnownDataTypes.SECOND_OF_MINUTE.getVarName(), DashboardUtils.INT_MISSING_VALUE);

		intValsMap.put(FCO2_SOURCE_VARNAME, DashboardUtils.INT_MISSING_VALUE);

		doubleValsMap = new LinkedHashMap<String,Double>(64);

		doubleValsMap.put(KnownDataTypes.LONGITUDE.getVarName(), DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(KnownDataTypes.LATITUDE.getVarName(), DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(KnownDataTypes.SAMPLE_DEPTH.getVarName(), DashboardUtils.FP_MISSING_VALUE);
		
		doubleValsMap.put(SALINITY_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(WOA_SALINITY_VARNAME, DashboardUtils.FP_MISSING_VALUE);

		doubleValsMap.put(TEQU_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(SST_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(TATM_VARNAME, DashboardUtils.FP_MISSING_VALUE);

		doubleValsMap.put(PEQU_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(PATM_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(NCEP_SLP_VARNAME, DashboardUtils.FP_MISSING_VALUE);

		doubleValsMap.put(XCO2_WATER_TEQU_DRY_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(XCO2_WATER_SST_DRY_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(XCO2_WATER_TEQU_WET_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(XCO2_WATER_SST_WET_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(PCO2_WATER_TEQU_WET_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(PCO2_WATER_SST_WET_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_WATER_TEQU_WET_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_WATER_SST_WET_VARNAME, DashboardUtils.FP_MISSING_VALUE);

		doubleValsMap.put(XCO2_ATM_DRY_ACTUAL_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(XCO2_ATM_DRY_INTERP_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(PCO2_ATM_DRY_ACTUAL_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(PCO2_ATM_DRY_INTERP_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_ATM_DRY_ACTUAL_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_ATM_DRY_INTERP_VARNAME, DashboardUtils.FP_MISSING_VALUE);

		doubleValsMap.put(DELTA_XCO2_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(DELTA_PCO2_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(DELTA_FCO2_VARNAME, DashboardUtils.FP_MISSING_VALUE);

		doubleValsMap.put(XH2O_EQU_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(RELATIVE_HUMIDITY_VARNAME,  DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(SPECIFIC_HUMIDITY_VARNAME, DashboardUtils.FP_MISSING_VALUE);

		doubleValsMap.put(SHIP_SPEED_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(SHIP_DIRECTION_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(WIND_SPEED_TRUE_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(WIND_SPEED_RELATIVE_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(WIND_DIRECTION_TRUE_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(WIND_DIRECTION_RELATIVE_VARNAME, DashboardUtils.FP_MISSING_VALUE);

		doubleValsMap.put(FCO2_FROM_XCO2_TEQU_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_XCO2_SST_VARNMAE, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_PCO2_TEQU_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_PCO2_SST_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_FCO2_TEQU_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_FCO2_SST_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_PCO2_TEQU_NCEP_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_PCO2_SST_NCEP_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_XCO2_TEQU_WOA_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_XCO2_SST_WOA_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_XCO2_TEQU_NCEP_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_XCO2_SST_NCEP_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FOC2_FROM_XCO2_TEQU_NCEP_WOA_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(FCO2_FROM_XCO2_SST_NCEP_WOA_VARNAME, DashboardUtils.FP_MISSING_VALUE);

		doubleValsMap.put(FCO2_REC_VARNAME, DashboardUtils.FP_MISSING_VALUE);

		doubleValsMap.put(DELTA_TEMP_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(CALC_SPEED_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(ETOPO2_DEPTH_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(GVCO2_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(DIST_TO_LAND_VARNAME, DashboardUtils.FP_MISSING_VALUE);
		doubleValsMap.put(DAY_OF_YEAR_VARNAME, DashboardUtils.FP_MISSING_VALUE);

		charValsMap = new LinkedHashMap<String,Character>();

		charValsMap.put(REGION_ID_VARNAME, DataLocation.GLOBAL_REGION_ID);
		charValsMap.put(WOCE_CO2_WATER_VARNAME, WoceEvent.WOCE_NOT_CHECKED);
		charValsMap.put(WOCE_CO2_ATM_VARNAME, WoceEvent.WOCE_NOT_CHECKED);
	}

	/**
	 * Generates a SOCAT cruise data objects from a list of data column 
	 * types and matching data strings.  This assumes the data in the 
	 * strings are in the standard units for each type, and the missing
	 * value is "NaN", an empty string, or null.  The data column types 
	 * {@link DataColumnType#TIMESTAMP}, {@link DataColumnType#DATE}, 
	 * and {@link DataColumnType#TIME} are ignored; the date and time 
	 * must be given using the {@link DataColumnType#YEAR}, 
	 * {@link DataColumnType#MONTH}, {@link DataColumnType#DAY},
	 * {@link DataColumnType#HOUR}, {@link DataColumnType#MINUTE},
	 * and {@link DataColumnType#SECOND} data column types.
	 * 
	 * @param columnTypes
	 * 		types of the data values
	 * @param rowNum
	 * 		sequence number (starting with one) of this data point
	 * 		in the data set
	 * @param dataValues
	 * 		data values
	 * @throws IllegalArgumentException
	 * 		if the number of data types and data values do not match, or
	 * 		if a data value string cannot be parsed for the expected type 
	 */
	public SocatCruiseData(List<DataColumnType> columnTypes, int rowNum,
			List<String> dataValues) throws IllegalArgumentException {
		// Initialize to an empty data record
		this();
		// Verify the number of types and values match
		int numColumns = columnTypes.size();
		if ( dataValues.size() != numColumns )
			throw new IllegalArgumentException("Number of column types (" +
					numColumns + ") does not match the number of data values (" +
					dataValues.size() + ")");
		// Add values to the empty record
		this.rowNum = rowNum;
		for (int k = 0; k < numColumns; k++) {
			// Skip over missing values since the empty data record
			// is initialized to the missing value for that type.
			String value = dataValues.get(k);
			if ( (value == null) || value.isEmpty() || value.equals("NaN") )
				continue;
			double secondOfDay = 0.0;
			DataColumnType type = columnTypes.get(k);
			try {
				if ( type.equals(DataColumnType.EXPOCODE) ||
					 type.equals(DataColumnType.CRUISE_NAME) ||
					 type.equals(DataColumnType.SHIP_NAME) ||
					 type.equals(DataColumnType.GROUP_NAME) ||
					 type.equals(DataColumnType.INVESTIGATOR_NAMES) ||
					 type.equals(DataColumnType.TIMESTAMP) ||
					 type.equals(DataColumnType.DATE) ||
					 type.equals(DataColumnType.TIME) ||
					 type.equals(DataColumnType.COMMENT_WOCE_CO2_WATER) ||
					 type.equals(DataColumnType.COMMENT_WOCE_CO2_ATM) ||
					 type.equals(DataColumnType.OTHER) ) {
					// Ignore these column types
					;
				}
				else if ( type.equals(DataColumnType.YEAR) ) {
					this.year = Integer.valueOf(value);
				}
				else if ( type.equals(DataColumnType.MONTH) ) {
					this.month = Integer.valueOf(value);
				}
				else if ( type.equals(DataColumnType.DAY) ) {
					this.day = Integer.valueOf(value);
				}
				else if ( type.equals(DataColumnType.HOUR) ) {
					this.hour = Integer.valueOf(value);
				}
				else if ( type.equals(DataColumnType.MINUTE) ) {
					this.minute = Integer.valueOf(value);
				}
				else if ( type.equals(DataColumnType.SECOND) ) {
					this.second = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.DAY_OF_YEAR) ) {
					this.dayOfYear = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.SECOND_OF_DAY) ) {
					secondOfDay = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.LONGITUDE) ) {
					this.longitude = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.LATITUDE) ) {
					this.latitude = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.SAMPLE_DEPTH) ) {
					this.sampleDepth = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.SALINITY) ) {
					this.salinity = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.EQUILIBRATOR_TEMPERATURE) ) {
					this.tEqu = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.SEA_SURFACE_TEMPERATURE) ) {
					this.sst = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.ATMOSPHERIC_TEMPERATURE) ) {
					this.tAtm = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.EQUILIBRATOR_PRESSURE) ) {
					this.pEqu = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.SEA_LEVEL_PRESSURE) ) {
					this.slp = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XCO2_WATER_TEQU_DRY) ) {
					this.xCO2WaterTEquDry = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XCO2_WATER_SST_DRY) ) {
					this.xCO2WaterSstDry = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XCO2_WATER_TEQU_WET) ) {
					this.xCO2WaterTEquWet = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XCO2_WATER_SST_WET) ) {
					this.xCO2WaterSstWet = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.PCO2_WATER_TEQU_WET) ) {
					this.pCO2WaterTEquWet = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.PCO2_WATER_SST_WET) ) {
					this.pCO2WaterSstWet = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.FCO2_WATER_TEQU_WET) ) {
					this.fCO2WaterTEquWet = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.FCO2_WATER_SST_WET) ) {
					this.fCO2WaterSstWet = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XCO2_ATM_DRY_ACTUAL) ) {
					this.xCO2AtmDryActual = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XCO2_ATM_DRY_INTERP) ) {
					this.xCO2AtmDryInterp = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.PCO2_ATM_WET_ACTUAL) ) {
					this.pCO2AtmWetActual = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.PCO2_ATM_WET_INTERP) ) {
					this.pCO2AtmWetInterp = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.FCO2_ATM_WET_ACTUAL) ) {
					this.fCO2AtmWetActual = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.FCO2_ATM_WET_INTERP) ) {
					this.fCO2AtmWetInterp = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.DELTA_XCO2) ) {
					this.deltaXCO2 = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.DELTA_PCO2) ) {
					this.deltaPCO2 = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.DELTA_FCO2) ) {
					this.deltaFCO2 = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XH2O_EQU) ) {
					this.xH2OEqu = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.RELATIVE_HUMIDITY) ) {
					this.relativeHumidity = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.SPECIFIC_HUMIDITY) ) {
					this.specificHumidity = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.SHIP_SPEED) ) {
					this.shipSpeed = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.SHIP_DIRECTION) ) {
					this.shipDirection = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.WIND_SPEED_TRUE) ) {
					this.windSpeedTrue = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.WIND_SPEED_RELATIVE) ) {
					this.windSpeedRelative = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.WIND_DIRECTION_TRUE) ) {
					this.windDirectionTrue = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.WIND_DIRECTION_RELATIVE) ) {
					this.windDirectionRelative = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.WOCE_CO2_WATER) ) {
					this.woceCO2Water = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.WOCE_CO2_ATM) ) {
					this.woceCO2Atm = value.charAt(0);
				}
				else {
					throw new IllegalArgumentException(
							"Unexpected data column type of " + type.name());
				}
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Unable to parse '" + 
						value + "' as a value of type " + type.name() + 
						"\n    " + ex.getMessage());
			}
			// Add any second-of-day value to the day-of-year value
			this.dayOfYear += secondOfDay / (24.0 * 60.0 * 60.0);
		}
	}

	/**
	 * Generates a list of SOCAT cruise data objects from the values
	 * and data column types given in a dashboard cruise with data.
	 * This assumes the data is in the standard units for each type, 
	 * and the missing value is "NaN", and empty string, or null.  
	 * The data column types {@link DataColumnType#TIMESTAMP}, 
	 * {@link DataColumnType#DATE}, and {@link DataColumnType#TIME} 
	 * are ignored; the date and time must be given using the 
	 * {@link DataColumnType#YEAR}, {@link DataColumnType#MONTH}, 
	 * {@link DataColumnType#DAY}, {@link DataColumnType#HOUR}, 
	 * {@link DataColumnType#MINUTE}, and {@link DataColumnType#SECOND} 
	 * data column types.
	 * 
	 * @param cruise
	 * 		dashboard cruise with data
	 * @return
	 * 		list of SOCAT cruise data objects
	 * @throws IllegalArgumentException
	 * 		if the number of data types and data values in a given row 
	 * 		do not match, or if a data value string cannot be parsed 
	 * 		for the expected type 
	 */
	public static ArrayList<SocatCruiseData> dataListFromDashboardCruise(
			DashboardCruiseWithData cruise) throws IllegalArgumentException {
		// Get the required data from the cruise
		ArrayList<ArrayList<String>> dataValsTable = cruise.getDataValues();
		ArrayList<DataColumnType> dataTypes = cruise.getDataColTypes();
		// Create the list of SOCAT cruise data objects, and populate
		// it with data from each row of the table
		ArrayList<SocatCruiseData> socatDataList = 
				new ArrayList<SocatCruiseData>(dataValsTable.size());
		for (int k = 0; k < dataValsTable.size(); k++) {
			socatDataList.add(
					new SocatCruiseData(dataTypes, k+1, dataValsTable.get(k)));
		}
		return socatDataList;
	}

	/**
	 * @return 
	 * 		the year of the data measurement; 
	 * 		never null but could be {@link #INT_MISSING_VALUE} if not assigned
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * @return 
	 * 		the month of the data measurement; 
	 * 		never null but could be {@link #INT_MISSING_VALUE} if not assigned
	 */
	public Integer getMonth() {
		return month;
	}

	/**
	 * @param month 
	 * 		the month of the data measurement to set; 
	 * 		if null, {@link #INT_MISSING_VALUE} is assigned
	 */
	public void setMonth(Integer month) {
		if ( month == null )
			this.month = INT_MISSING_VALUE;
		else
			this.month = month;
	}

	/**
	 * @return 
	 * 		the day of the data measurement; 
	 * 		never null but could be {@link #INT_MISSING_VALUE} if not assigned
	 */
	public Integer getDay() {
		return day;
	}

	/**
	 * @param day 
	 * 		the day of the data measurement to set; 
	 * 		if null, {@link #INT_MISSING_VALUE} is assigned
	 */
	public void setDay(Integer day) {
		if ( day == null )
			this.day = INT_MISSING_VALUE;
		else
			this.day = day;
	}

	/**
	 * @return 
	 * 		the hour of the data measurement; 
	 * 		never null but could be {@link #INT_MISSING_VALUE} if not assigned
	 */
	public Integer getHour() {
		return hour;
	}

	/**
	 * @param hour 
	 * 		the hour of the data measurement to set; 
	 * 		if null, {@link #INT_MISSING_VALUE} is assigned
	 */
	public void setHour(Integer hour) {
		if ( hour == null )
			this.hour = INT_MISSING_VALUE;
		else
			this.hour = hour;
	}

	/**
	 * @return 
	 * 		the minute of the data measurement; 
	 * 		never null but could be {@link #INT_MISSING_VALUE} if not assigned
	 */
	public Integer getMinute() {
		return minute;
	}

	/**
	 * @param minute 
	 * 		the minute of the data measurement to set; 
	 * 		if null, {@link #INT_MISSING_VALUE} is assigned
	 */
	public void setMinute(Integer minute) {
		if ( minute == null )
			this.minute = INT_MISSING_VALUE;
		else
			this.minute = minute;
	}

	/**
	 * @return 
	 * 		the second of the data measurement; 
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getSecond() {
		return second;
	}

	/**
	 * @param second 
	 * 		the second of the data measurement to set; 
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setSecond(Double second) {
		if ( second == null )
			this.second = FP_MISSING_VALUE;
		else
			this.second = second;
	}

	/**
	 * @return 
	 * 		the longitude of the data measurement; 
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @return 
	 * 		the latitude of the data measurement; 
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @return 
	 * 		the sampling depth;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getSampleDepth() {
		return sampleDepth;
	}

	/**
	 * @return 
	 * 		the sea surface salinity;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getSalinity() {
		return salinity;
	}

	/**
	 * @return 
	 * 		the equilibrator temperature;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double gettEqu() {
		return tEqu;
	}

	/**
	 * @return 
	 * 		the sea surface temperature;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getSst() {
		return sst;
	}

	/**
	 * @return 
	 * 		the atmospheric sea-level pressure;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getSlp() {
		return slp;
	}

	/**
	 * @return 
	 * 		the equilibrator pressure;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getpEqu() {
		return pEqu;
	}

	/**
	 * @return 
	 * 		the xCO2WaterTEquDry;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getxCO2WaterTEquDry() {
		return xCO2WaterTEquDry;
	}

	/**
	 * @return 
	 * 		the xCO2WaterSstDry;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getxCO2WaterSstDry() {
		return xCO2WaterSstDry;
	}

	/**
	 * @return 
	 * 		the xCO2WaterSstWet;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getxCO2WaterSstWet() {
		return xCO2WaterSstWet;
	}

	/**
	 * @return 
	 * 		the pCO2WaterTEquWet;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getpCO2WaterTEquWet() {
		return pCO2WaterTEquWet;
	}

	/**
	 * @return 
	 * 		the pCO2WaterSstWet;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getpCO2WaterSstWet() {
		return pCO2WaterSstWet;
	}

	/**
	 * @return 
	 * 		the fCO2WaterSstWet;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2WaterSstWet() {
		return fCO2WaterSstWet;
	}

	/**
	 * @return 
	 * 		the woceCO2Water;
	 * 		never null but could be {@link WoceEvent#WOCE_NOT_CHECKED} if not assigned
	 */
	public Character getWoceCO2Water() {
		return woceCO2Water;
	}

	/**
	 * @param woceCO2Water 
	 * 		the woceCO2Water to set;
	 * 		if null, {@link WoceEvent#WOCE_NOT_CHECKED} is assigned
	 */
	public void setWoceCO2Water(Character woceCO2Water) {
		if ( woceCO2Water == null )
			this.woceCO2Water = WoceEvent.WOCE_NOT_CHECKED;
		else
			this.woceCO2Water = woceCO2Water;
	}

	/**
	 * @return 
	 * 		the woceCO2Atm;
	 * 		never null but could be {@link WoceEvent#WOCE_NOT_CHECKED} if not assigned
	 */
	public Character getWoceCO2Atm() {
		return woceCO2Atm;
	}

	/**
	 * @param woceCO2Atm 
	 * 		the woceCO2Atm to set;
	 * 		if null, {@link #WoceEvent#WOCE_NOT_CHECKED} is assigned
	 */
	public void setWoceCO2Atm(Character woceCO2Atm) {
		if ( woceCO2Atm == null )
			this.woceCO2Atm = WoceEvent.WOCE_NOT_CHECKED;
		else
			this.woceCO2Atm = woceCO2Atm;
	}

	/**
	 * @return 
	 * 		the WOA sea surface salinity;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getWoaSss() {
		return woaSss;
	}

	/**
	 * @return 
	 * 		the NCEP sea level pressure;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getNcepSlp() {
		return ncepSlp;
	}

	/**
	 * @return 
	 * 		the recomputed fCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2Rec() {
		return fCO2Rec;
	}

	/**
	 * @return 
	 * 		the method used to create the recomputed fCO2;
	 * 		never null but could be {@link #INT_MISSING_VALUE} if not assigned
	 */
	public Integer getfCO2Source() {
		return fCO2Source;
	}

	/**
	 * @return 
	 * 		the region ID;
	 * 		never null but could be {@link DataLocation#GLOBAL_REGION_ID} if not assigned
	 */
	public Character getRegionID() {
		return regionID;
	}

	/**
	 * @return 
	 * 		the ETOPO2 depth;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getEtopo2Depth() {
		return etopo2Depth;
	}

	/**
	 * @return 
	 * 		the GlobablView CO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getGvCO2() {
		return gvCO2;
	}

	/**
	 * @return 
	 * 		the distance to nearest major land mass;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDistToLand() {
		return distToLand;
	}

	@Override 
	public int hashCode() {
		// Ignore WOCE flag differences.
		// Do not use floating-point fields since they do not 
		// have to be exactly the same for equals to return true.
		final int prime = 37;
		int result = 0;
		result = result * prime + regionID.hashCode();
		result = result * prime + year.hashCode();
		result = result * prime + month.hashCode();
		result = result * prime + day.hashCode();
		result = result * prime + hour.hashCode();
		result = result * prime + minute.hashCode();
		result = result * prime + fCO2Source.hashCode();
		result = result * prime + rowNum.hashCode();
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
		if ( ! rowNum.equals(other.rowNum) )
			return false;
		if ( ! year.equals(other.year) )
			return false;
		if ( ! month.equals(other.month) )
			return false;
		if ( ! day.equals(other.day) )
			return false;
		if ( ! hour.equals(other.hour) )
			return false;
		if ( ! minute.equals(other.minute) )
			return false;

		// Character comparisons - ignore WOCE flag differences
		if ( ! fCO2Source.equals(other.fCO2Source) ) 
			return false;
		if ( ! regionID.equals(other.regionID) )
			return false;

		// Match seconds not given (FP_MISSING_VALUE) with zero seconds
		if ( ! DashboardUtils.closeTo(second, other.second, 0.0, 1.0E-3) )
			if ( ! (second.equals(FP_MISSING_VALUE) && DashboardUtils.closeTo(0.0, other.second, 0.0, 1.0E-3)) )
				if ( ! (other.second.equals(FP_MISSING_VALUE) && DashboardUtils.closeTo(second, 0.0, 0.0, 1.0E-3)) )
					return false;

		// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
		if ( ! DashboardUtils.longitudeCloseTo(this.longitude, other.longitude, 0.0, MAX_ABSOLUTE_ERROR) )
			return false;

		// rest of the Double comparisons
		if ( ! DashboardUtils.closeTo(latitude, other.latitude, 0.0, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(sampleDepth, other.sampleDepth, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(salinity, other.salinity, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(tEqu, other.tEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(sst, other.sst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(tAtm, other.tAtm, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pEqu, other.pEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(slp, other.slp, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;

		if ( ! DashboardUtils.closeTo(xCO2WaterTEquDry, other.xCO2WaterTEquDry, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(xCO2WaterSstDry, other.xCO2WaterSstDry, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(xCO2WaterTEquWet, other.xCO2WaterTEquWet, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(xCO2WaterSstWet, other.xCO2WaterSstWet, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pCO2WaterTEquWet, other.pCO2WaterTEquWet, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pCO2WaterSstWet, other.pCO2WaterSstWet, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2WaterTEquWet, other.fCO2WaterTEquWet, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2WaterSstWet, other.fCO2WaterSstWet, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;

		if ( ! DashboardUtils.closeTo(xCO2AtmDryActual, other.xCO2AtmDryActual, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(xCO2AtmDryInterp, other.xCO2AtmDryInterp, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pCO2AtmWetActual, other.pCO2AtmWetActual, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pCO2AtmWetInterp, other.pCO2AtmWetInterp, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2AtmWetActual, other.fCO2AtmWetActual, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2AtmWetInterp, other.fCO2AtmWetInterp, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;

		if ( ! DashboardUtils.closeTo(deltaXCO2, other.deltaXCO2, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(deltaPCO2, other.deltaPCO2, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(deltaFCO2, other.deltaFCO2, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;

		if ( ! DashboardUtils.closeTo(xH2OEqu, other.xH2OEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(relativeHumidity, other.relativeHumidity, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(specificHumidity, other.specificHumidity, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(shipSpeed, other.shipSpeed, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(shipDirection, other.shipDirection, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(windSpeedTrue, other.windSpeedTrue, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(windSpeedRelative, other.windSpeedRelative, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(windDirectionTrue, other.windDirectionTrue, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(windDirectionRelative, other.windDirectionRelative, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;

		if ( ! DashboardUtils.closeTo(woaSss, other.woaSss, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(ncepSlp, other.ncepSlp, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;

		if ( ! DashboardUtils.closeTo(fCO2FromXCO2TEqu, other.fCO2FromXCO2TEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromXCO2Sst, other.fCO2FromXCO2Sst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromPCO2TEqu, other.fCO2FromPCO2TEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromPCO2Sst, other.fCO2FromPCO2Sst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromFCO2TEqu, other.fCO2FromFCO2TEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromFCO2Sst, other.fCO2FromFCO2Sst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromPCO2TEquNcep, other.fCO2FromPCO2TEquNcep, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromPCO2SstNcep, other.fCO2FromPCO2SstNcep, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromXCO2TEquWoa, other.fCO2FromXCO2TEquWoa, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromXCO2SstWoa, other.fCO2FromXCO2SstWoa, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromXCO2TEquNcep, other.fCO2FromXCO2TEquNcep, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromXCO2SstNcep, other.fCO2FromXCO2SstNcep, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromXCO2TEquNcepWoa, other.fCO2FromXCO2TEquNcepWoa, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2FromXCO2SstNcepWoa, other.fCO2FromXCO2SstNcepWoa, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;

		if ( ! DashboardUtils.closeTo(fCO2Rec, other.fCO2Rec, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(deltaT, other.deltaT, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(calcSpeed, other.calcSpeed, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(etopo2Depth, other.etopo2Depth, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(gvCO2, other.gvCO2, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(distToLand, other.distToLand, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(dayOfYear, other.dayOfYear, 0.0, MAX_ABSOLUTE_ERROR) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "SocatCruiseData" +
				"[\n    rowNum=" + rowNum.toString() +
				",\n    year=" + year.toString() +
				",\n    month=" + month.toString() +
				",\n    day=" + day.toString() +
				",\n    hour=" + hour.toString() +
				",\n    minute=" + minute.toString() +
				",\n    second=" + second.toString() +

				",\n    longitude=" + longitude.toString() +
				",\n    latitude=" + latitude.toString() +
				",\n    sampleDepth=" + sampleDepth.toString() +
				",\n    salinity=" + salinity.toString() +
				",\n    tEqu=" + tEqu.toString() +
				",\n    tAtm=" + tAtm.toString() +
				",\n    pEqu=" + pEqu.toString() +
				",\n    slp=" + slp.toString() +

				",\n    xCO2WaterTEquDry=" + xCO2WaterTEquDry.toString() +
				",\n    xCO2WaterSstDry=" + xCO2WaterSstDry.toString() +
				",\n    xCO2WaterTEquWet=" + xCO2WaterTEquWet.toString() +
				",\n    xCO2WaterSstWet=" + xCO2WaterSstWet.toString() +
				",\n    pCO2WaterTEquWet=" + pCO2WaterTEquWet.toString() +
				",\n    pCO2WaterSstWet=" + pCO2WaterSstWet.toString() +
				",\n    fCO2WaterTEquWet=" + fCO2WaterTEquWet.toString() +
				",\n    fCO2WaterSstWet=" + fCO2WaterSstWet.toString() +

				",\n    xCO2AtmDryActual=" + xCO2AtmDryActual.toString() +
				",\n    xCO2AtmDryInterp=" + xCO2AtmDryInterp.toString() +
				",\n    pCO2AtmWetActual=" + pCO2AtmWetActual.toString() +
				",\n    pCO2AtmWetInterp=" + pCO2AtmWetInterp.toString() +
				",\n    fCO2AtmWetActual=" + fCO2AtmWetActual.toString() +
				",\n    fCO2AtmWetInterp=" + fCO2AtmWetInterp.toString() +

				",\n    deltaXCO2=" + deltaXCO2.toString() +
				",\n    deltaPCO2=" + deltaPCO2.toString() +
				",\n    deltaFCO2=" + deltaFCO2.toString() +

				",\n    xH2OEqu=" + xH2OEqu.toString() +
				",\n    relativeHumidity=" + relativeHumidity.toString() +
				",\n    specificHumidity=" + specificHumidity.toString() +
				",\n    shipSpeed=" + shipSpeed.toString() +
				",\n    shipDirection=" + shipDirection.toString() +
				",\n    windSpeedTrue=" + windSpeedTrue.toString() +
				",\n    windSpeedRelative=" + windSpeedRelative.toString() +
				",\n    windDirectionTrue=" + windDirectionTrue.toString() +
				",\n    windDirectionRelative=" + windDirectionRelative.toString() +

				",\n    woceCO2Water=" + woceCO2Water.toString() +
				",\n    woceCO2Atm=" + woceCO2Atm.toString() +

				",\n    woaSss=" + woaSss.toString() +
				",\n    ncepSlp=" + ncepSlp.toString() +

				",\n    fCO2FromXCO2TEqu=" + fCO2FromXCO2TEqu.toString() +
				",\n    fCO2FromXCO2Sst=" + fCO2FromXCO2Sst.toString() +
				",\n    fCO2FromPCO2TEqu=" + fCO2FromPCO2TEqu.toString() +
				",\n    fCO2FromPCO2Sst=" + fCO2FromPCO2Sst.toString() +
				",\n    fCO2FromFCO2TEqu=" + fCO2FromFCO2TEqu.toString() +
				",\n    fCO2FromFCO2Sst=" + fCO2FromFCO2Sst.toString() +
				",\n    fCO2FromPCO2TEquNcep=" + fCO2FromPCO2TEquNcep.toString() +
				",\n    fCO2FromPCO2SstNcep=" + fCO2FromPCO2SstNcep.toString() +
				",\n    fCO2FromXCO2TEquWoa=" + fCO2FromXCO2TEquWoa.toString() +
				",\n    fCO2FromXCO2SstWoa=" + fCO2FromXCO2SstWoa.toString() +
				",\n    fCO2FromXCO2TEquNcep=" + fCO2FromXCO2TEquNcep.toString() +
				",\n    fCO2FromXCO2SstNcep=" + fCO2FromXCO2SstNcep.toString() +
				",\n    fCO2FromXCO2TEquNcep=" + fCO2FromXCO2TEquNcepWoa.toString() +
				",\n    fCO2FromXCO2SstNcep=" + fCO2FromXCO2SstNcepWoa.toString() +

				",\n    fCO2Rec=" + fCO2Rec.toString() +
				",\n    fCO2Source=" + fCO2Source.toString() +
				",\n    deltaT=" + deltaT.toString() +
				",\n    regionID=" + regionID +
				",\n    calcSpeed=" + calcSpeed.toString() +
				",\n    etopo2Depth=" + etopo2Depth.toString() +
				",\n    gvCO2=" + gvCO2.toString() +
				",\n    distToLand=" + distToLand.toString() +
				",\n    dayOfYear=" + dayOfYear.toString() +
				"\n]";
	}

}
