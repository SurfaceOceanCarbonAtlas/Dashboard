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
	 * UNKNOWN needs to be respecified as one of the (other) user-provided types.
	 */
	UNKNOWN, 
	/**
	 * EXPOCODE is the NODC ship code, start date year, month, day 
	 */
	EXPOCODE,
	/**
	 * CRUISE_NAME is the user-provided name for the cruise
	 */
	CRUISE_NAME,
	/**
	 * TIMESTAMP has both date and time.
	 */
	TIMESTAMP, 
	/**
	 * DATE has only the date; no time.
	 */
	DATE, 
	YEAR, 
	MONTH, 
	DAY, 
	/**
	 * TIME has only the time; no date.
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
	HUMIDITY,
	XCO2AIR, 
	PCO2AIR, 
	FCO2AIR, 
	SHIP_SPEED, 
	SHIP_DIRECTION, 
	WIND_SPEED_TRUE,
	WIND_SPEED_RELATIVE,
	WIND_DIRECTION_TRUE,
	WIND_DIRECTION_RELATIVE,

	// WOCE flags
	TIMESTAMP_WOCE,
	LONGITUDE_WOCE,
	LATITUDE_WOCE,
	DEPTH_WOCE,
	SALINITY_WOCE,
	EQUILIBRATOR_TEMPERATURE_WOCE,
	SEA_SURFACE_TEMPERATURE_WOCE,
	EQUILIBRATOR_PRESSURE_WOCE,
	SEA_LEVEL_PRESSURE_WOCE,
	XCO2WATER_EQU_WOCE,
	XCO2WATER_SST_WOCE,
	PCO2WATER_EQU_WOCE,
	PCO2WATER_SST_WOCE,
	FCO2WATER_EQU_WOCE,
	FCO2WATER_SST_WOCE,
	HUMIDITY_WOCE,
	XCO2AIR_WOCE, 
	PCO2AIR_WOCE, 
	FCO2AIR_WOCE, 
	OVERALL_WOCE,
	
	/**
	 * COMMENT is a user-provided comment about this data point measurements,
	 * and will be saved as a WOCE flag comment.
	 */
	COMMENT,
}
