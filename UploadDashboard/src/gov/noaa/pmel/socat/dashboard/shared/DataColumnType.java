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
	 * EXPOCODE is NODCYYYYMMDD where NODC is the ship code and 
	 * YYYY-MM-DD is the start date (metadata)
	 */
	EXPOCODE,
	/**
	 * CRUISE_NAME is the user-provided name for the cruise or dataset (metadata)
	 */
	CRUISE_NAME,
	/**
	 * SHIP_NAME is the name of the ship or vessel (metadata)
	 */
	SHIP_NAME,
	/**
	 * GROUP_NAME is the user-provided name of the group (metadata)
	 */
	GROUP_NAME,

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
	/**
	 * DAY_OF_YEAR is possibly floating point with time.
	 */
	DAY_OF_YEAR,
	/**
	 * SECOND_OF_DAY, along with YEAR and DAY_OF_YEAR may
	 * be used to specify date and time.
	 */
	SECOND_OF_DAY,

	LONGITUDE, 
	LATITUDE, 
	SAMPLE_DEPTH, 
	SALINITY, 
	EQUILIBRATOR_TEMPERATURE, 
	SEA_SURFACE_TEMPERATURE, 
	ATMOSPHERIC_TEMPERATURE,
	EQUILIBRATOR_PRESSURE, 
	SEA_LEVEL_PRESSURE, 

	XCO2_WATER_TEQU_DRY, 
	XCO2_WATER_SST_DRY, 
	XCO2_WATER_TEQU_WET, 
	XCO2_WATER_SST_WET, 
	PCO2_WATER_TEQU_WET, 
	PCO2_WATER_SST_WET, 
	FCO2_WATER_TEQU_WET, 
	FCO2_WATER_SST_WET, 

	XCO2_ATM_DRY_ACTUAL,
	XCO2_ATM_DRY_INTERP,
	PCO2_ATM_WET_ACTUAL,
	PCO2_ATM_WET_INTERP,
	FCO2_ATM_WET_ACTUAL,
	FCO2_ATM_WET_INTERP,

	DELTA_XCO2,
	DELTA_PCO2,
	DELTA_FCO2,

	XH2O_EQU,
	RELATIVE_HUMIDITY,
	SPECIFIC_HUMIDITY,
	SHIP_SPEED, 
	SHIP_DIRECTION, 
	WIND_SPEED_TRUE,
	WIND_SPEED_RELATIVE,
	WIND_DIRECTION_TRUE,
	WIND_DIRECTION_RELATIVE,

	/**
	 * WOCE flag on any/all aqueous CO2 values.
	 */
	WOCE_CO2_WATER,
	/**
	 * WOCE flag on any/all atmospheric CO2 values.
	 */
	WOCE_CO2_ATM,

	/**
	 * Comment to go with the WOCE flag on aqueous CO2 values.
	 */
	COMMENT_WOCE_CO2_WATER,
	/**
	 * Comment to go with the WOCE flag on atmospheric CO2 values.
	 */
	COMMENT_WOCE_CO2_ATM,
	
	/**
	 * OTHER is for supplementary data in the user's original data file but 
	 * not part of SOCAT.  A description of each column with this type must 
	 * be part of the metadata, but the values are not validated or used. 
	 * Multiple columns may have this type.
	 */
	OTHER,
}
