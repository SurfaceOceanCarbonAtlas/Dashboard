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
	 * The ignore data type indicates data that should be 
	 * completely ignored.
	 */
	IGNORE,
	/**
	 * The unknown data type indicates data that the user 
	 * needs specify as one of the other standard types.
	 */
	UNKNOWN,
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
	SAMPLE_SALINITY,
	EQUILIBRATOR_TEMPERATURE,
	SEA_SURFACE_TEMPERATURE,
	EQUILIBRATOR_PRESSURE,
	SEA_LEVEL_PRESSURE,
	USER_FCO2_REC,
	USER_FCO2_SRC,
	FCO2_AIR,
	WIND_SPEED,
	WIND_DIRECTION,
	XCO2WATER_EQU,
	XCO2WATER_SST,
	PCO2WATER_EQU,
	PCO2WATER_SST,
	FCO2WATER_EQU,
	FCO2WATER_SST,
	XCO2AIR_DRY,
	XCO2AIR_EQU,
	PCO2AIR_WET,
	FCO2AIR_WET,
	/**
	 * The supplemental data type indicates data 
	 * that is carried along but otherwise ignored.
	 */
	SUPPLEMENTAL
}