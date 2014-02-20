/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Types of the data columns in a user-provided cruise data file.
 * 
 * @author Karl Smith
 */
public enum DataColumnType implements Serializable, IsSerializable {
	/**
	 * The unknown data type indicates data that the user 
	 * needs specify as one of the other standard types.
	 */
	UNKNOWN, 
	EXPOCODE,
	CRUISE_NAME,
	/**
	 * The timestamp data type has both date and time.
	 */
	TIMESTAMP, 
	/**
	 * The date data type has only the date; no time.
	 */
	DATE, 
	YEAR, 
	MONTH, 
	DAY, 
	/**
	 * The time data type has only the time; no date.
	 */
	TIME, 
	HOUR, 
	MINUTE, 
	SECOND, 
	LONGITUDE, 
	LATITUDE, 
	SAMPLE_DEPTH, 
	SALINITY, 
	EQUILIBRATOR_TEMPERATURE, 
	SEA_SURFACE_TEMPERATURE, 
	EQUILIBRATOR_PRESSURE, 
	SEA_LEVEL_PRESSURE, 
	XCO2WATER_EQU, 
	XCO2WATER_SST, 
	PCO2WATER_EQU, 
	PCO2WATER_SST, 
	FCO2WATER_EQU, 
	FCO2WATER_SST, 
	XCO2_ATM, 
	PCO2_ATM, 
	FCO2_ATM, 
	SHIP_SPEED, 
	SHIP_DIRECTION, 
	WIND_SPEED_TRUE,
	WIND_SPEED_RELATIVE,
	WIND_DIRECTION_TRUE,
	WIND_DIRECTION_RELATIVE,
	// The following are computed fields and are not usually provided by the user
	DELTA_PCO2,
	DELTA_FCO2,
	WOA_SALINITY, 
	NCEP_SEA_LEVEL_PRESSURE, 
	FCO2REC_FROM_XCO2_TEQ_PEQ_SAL, 
	FCO2REC_FROM_XCO2_SST_PEQ_SAL, 
	FCO2REC_FROM_PCO2_TEQ_PEQU_SAL, 
	FCO2REC_FROM_PCO2_SST_PEQ_SAL, 
	FCO2REC_FROM_FCO2_TEQ_PEQ_SAL, 
	FCO2REC_FROM_FCO2_SST_PEQ_SAL, 
	FCO2REC_FROM_PCO2_TEQ_NCEP_SAL, 
	FCO2REC_FROM_PCO2_SST_NCEP_SAL, 
	FCO2REC_FROM_XCO2_TEQ_PEQ_WOA, 
	FCO2REC_FROM_XCO2_SST_PEQ_WOA, 
	FCO2REC_FROM_XCO2_TEQ_NCEP_SAL, 
	FCO2REC_FROM_XCO2_SST_NCEP_SAL, 
	FCO2REC_FROM_XCO2_TEQ_NCEP_WOA, 
	FCO2REC_FROM_XCO2_SST_NCEP_WOA, 
	FCO2REC, 
	FCO2REC_SOURCE, 
	DELTA_TEMPERATURE, 
	REGION_ID, 
	SECONDS_1970, 
	DAYS_1970, 
	DAY_OF_YEAR, 
	CALC_SHIP_SPEED, 
	ETOPO2, 
	GVCO2, 
	DISTANCE_TO_LAND, 
	FCO2REC_WOCE_FLAG,
}
