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

	XCO2_WATER_TEQU, 
	XCO2_WATER_SST, 
	PCO2_WATER_TEQU, 
	PCO2_WATER_SST, 
	FCO2_WATER_TEQU, 
	FCO2_WATER_SST, 

	XCO2_ATM_ACTUAL,
	XCO2_ATM_INTERP,
	PCO2_ATM_ACTUAL,
	PCO2_ATM_INTERP,
	FCO2_ATM_ACTUAL,
	FCO2_ATM_INTERP,

	DELTA_XCO2,
	DELTA_PCO2,
	DELTA_FCO2,

	RELATIVE_HUMIDITY,
	SPECIFIC_HUMIDITY,
	SHIP_SPEED, 
	SHIP_DIRECTION, 
	WIND_SPEED_TRUE,
	WIND_SPEED_RELATIVE,
	WIND_DIRECTION_TRUE,
	WIND_DIRECTION_RELATIVE,

	/**
	 * WOCE_GEOPOSITION is a WOCE flag on the longitude, latitude, date, and time
	 */
	WOCE_GEOPOSITION,
	WOCE_SAMPLE_DEPTH,
	WOCE_SALINITY,
	WOCE_EQUILIBRATOR_TEMPERATURE,
	WOCE_SEA_SURFACE_TEMPERATURE,
	WOCE_ATMOSPHERIC_TEMPERATURE,
	WOCE_EQUILIBRATOR_PRESSURE,
	WOCE_SEA_LEVEL_PRESSURE,

	WOCE_XCO2_WATER_TEQU,
	WOCE_XCO2_WATER_SST,
	WOCE_PCO2_WATER_TEQU,
	WOCE_PCO2_WATER_SST,
	WOCE_FCO2_WATER_TEQU,
	WOCE_FCO2_WATER_SST,

	WOCE_XCO2_ATM_ACTUAL,
	WOCE_XCO2_ATM_INTERP,
	WOCE_PCO2_ATM_ACTUAL,
	WOCE_PCO2_ATM_INTERP,
	WOCE_FCO2_ATM_ACTUAL,
	WOCE_FCO2_ATM_INTERP,

	WOCE_DELTA_XCO2,
	WOCE_DELTA_PCO2,
	WOCE_DELTA_FCO2,

	WOCE_RELATIVE_HUMIDITY,
	WOCE_SPECIFIC_HUMIDITY,
	WOCE_SHIP_SPEED,
	WOCE_SHIP_DIRECTION,
	WOCE_WIND_SPEED_TRUE,
	WOCE_WIND_SPEED_RELATIVE,
	WOCE_WIND_DIRECTION_TRUE,
	WOCE_WIND_DIRECTION_RELATIVE,
	
	/**
	 * COMMENT is a user-provided comment about this data point measurements,
	 * and (if not empty) will be saved as a WOCE no-flag comment that includes
	 * the user's column name.  The contents of this column are not validated.
	 * Multiple columns may have this type.
	 */
	COMMENT,
	/**
	 * OTHER is for supplementary data in the user's original data file but 
	 * not part of SOCAT.  A description of each column with this type must 
	 * be part of the metadata, but the values are not validated or used. 
	 * Multiple columns may have this type.
	 */
	OTHER,
	/**
	 * FCO2_REC is for the recommended recomputed fCO2 values column.  
	 * Setting a WOCE flag on this column prevents any recalculation of
	 * the recommended recomputed fCO2 value.  Since this is a computed 
	 * field and not user-supplied, this type is not available for normal 
	 * user-specification of column types.
	 */
	FCO2_REC,
	/**
	 * WOCE_FCO2_REC is for transferring the fCO2_rec WOCE flag from SOCAT v2.
	 * Since fCO2_REC is a computed field and not user-supplied, WOCE_FCO2_REC 
	 * is not available for normal user-specification of column types.
	 */
	WOCE_FCO2_REC,
}
