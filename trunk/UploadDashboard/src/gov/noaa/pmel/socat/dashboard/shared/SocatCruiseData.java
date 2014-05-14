/**
 */
package gov.noaa.pmel.socat.dashboard.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class for working with data values of interest, both PI-provided
 * values and computed values, from a SOCAT cruise data measurement.
 * 
 * @author Karl Smith
 */
public class SocatCruiseData implements Serializable, IsSerializable {

	private static final long serialVersionUID = -4839643565178804765L;

	static final double MAX_RELATIVE_ERROR = 1.0E-6;
	static final double MAX_ABSOLUTE_ERROR = 1.0E-6;

	/**
	 *  Missing value for floating-point variables - not NaN for Ferret
	 */
	public static final Double FP_MISSING_VALUE = -1.0E+34;
	/**
	 *  Missing value for integer variables
	 */
	public static final Integer INT_MISSING_VALUE = -1;
	/**
	 *  Missing value for single character variables (regionID, all the WOCE flags)
	 */
	public static final Character CHAR_MISSING_VALUE = ' ';

	// Time of measurement
	Integer year;
	Integer month;
	Integer day;
	Integer hour;
	Integer minute;
	Double second;

	// Longitude of measurement
	Double longitude;
	// Latitude of measurement
	Double latitude;
	// Sampling depth for the measurement
	Double sampleDepth;
	// salinity
	Double salinity;
	// Equilibrator temperature
	Double tEqu;
	// Sea surface temperature
	Double sst;
	// Equilibrator pressure
	Double pEqu;
	// Atmospheric pressure / sea level pressure
	Double slp;

	// Six possible water CO2 measurements reported; 
	// typically only one or two actually reported
	Double xCO2WaterSst;
	Double xCO2WaterTEqu;
	Double pCO2WaterSst;
	Double pCO2WaterTEqu;
	Double fCO2WaterSst;
	Double fCO2WaterTEqu;

	// Three possible air CO2 measurements reported;
	// typically one or none actually reported
	Double xCO2Atm;
	Double pCO2Atm;
	Double fCO2Atm;
	// CO2Water - (average) CO2Atm; only user-provided
	Double deltaXCO2;
	Double deltaPCO2;
	Double deltaFCO2;

	// Humdity
	Double relativeHumidity;
	Double specificHumidity;
	// Ship speed in knots
	Double shipSpeed; 
	// Ship direction in degrees clockwise from N
	Double shipDirection; 
	// Wind speed in m/s
	Double windSpeedTrue;
	Double windSpeedRelative;
	// Wind direction in degrees clockwise from N
	Double windDirectionTrue;
	Double windDirectionRelative;

	// WOCE flags on user-provided values
	Character geopositionWoce;
	Character sampleDepthWoce;
	Character salinityWoce;
	Character tEquWoce;
	Character sstWoce;
	Character pEquWoce;
	Character slpWoce;

	Character xCO2WaterTEquWoce;
	Character xCO2WaterSstWoce;
	Character pCO2WaterTEquWoce;
	Character pCO2WaterSstWoce;
	Character fCO2WaterTEquWoce;
	Character fCO2WaterSstWoce;

	Character xCO2AtmWoce;
	Character pCO2AtmWoce;
	Character fCO2AtmWoce;
	Character deltaXCO2Woce;
	Character deltaPCO2Woce;
	Character deltaFCO2Woce;

	Character relativeHumidityWoce;
	Character specificHumidityWoce;
	Character shipSpeedWoce;
	Character shipDirectionWoce;
	Character windSpeedTrueWoce;
	Character windSpeedRelativeWoce;
	Character windDirectionTrueWoce;
	Character windDirectionRelativeWoce;

	// The following are provided by Ferret calculations using the above data

	// WOA Sea Surface Salinity at this measurement's time and location
	Double woaSss;
	// NCEP sea level pressure at this measurement's time and location
	Double ncepSlp;

	// Fourteen different recomputed fCO2 possibilities 
	// depending on what was and was not provided by the PI
	Double fCO2FromXCO2TEqu;
	Double fCO2FromXCO2Sst;
	Double fCO2FromPCO2TEqu;
	Double fCO2FromPCO2Sst;
	Double fCO2FromFCO2TEqu;
	Double fCO2FromFCO2Sst;
	Double fCO2FromPCO2TEquNcep;
	Double fCO2FromPCO2SstNcep;
	Double fCO2FromXCO2TEquWoa;
	Double fCO2FromXCO2SstWoa;
	Double fCO2FromXCO2TEquNcep;
	Double fCO2FromXCO2SstNcep;
	Double fCO2FromXCO2TEquNcepWoa;
	Double fCO2FromXCO2SstNcepWoa;

	// Recommended recomputed fCO2 value
	Double fCO2Rec;
    // Marker 1-14 indicating which of the recomputed fCO2 values was the recommended one
	Integer fCO2Source;
	// tEqu - SST
	Double deltaT;
	// ID of the cruise region in which this measurement lies
	Character regionID;
	// calculated ship speed from adjacent measurements
	Double calcSpeed;
	// ETOPO2 depth
	Double etopo2Depth;
	// GlobalView xCO2 value
	Double gvCO2;
	// distance to closest land mass (up to 1000 km)
	Double distToLand;
	// days since Jan 1, 1970 00:00:00
	Double days1970;
	// days of the year; Jan 1 00:00 == 1.0
	Double dayOfYear;

	// WOCE flags on computed values
	Character fCO2FromXCO2TEquWoce;
	Character fCO2FromXCO2SstWoce;
	Character fCO2FromPCO2TEquWoce;
	Character fCO2FromPCO2SstWoce;
	Character fCO2FromFCO2TEquWoce;
	Character fCO2FromFCO2SstWoce;
	Character fCO2FromPCO2TEquNcepWoce;
	Character fCO2FromPCO2SstNcepWoce;
	Character fCO2FromXCO2TEquWoaWoce;
	Character fCO2FromXCO2SstWoaWoce;
	Character fCO2FromXCO2TEquNcepWoce;
	Character fCO2FromXCO2SstNcepWoce;
	Character fCO2FromXCO2TEquNcepWoaWoce;
	Character fCO2FromXCO2SstNcepWoaWoce;

	Character fCO2RecWoce;
	Character deltaTWoce;
	Character calcSpeedWoce;

	/**
	 * Generates an empty SOCAT data record
	 */
	public SocatCruiseData() {
		year = INT_MISSING_VALUE;
		month = INT_MISSING_VALUE;
		day = INT_MISSING_VALUE;
		hour = INT_MISSING_VALUE;
		minute = INT_MISSING_VALUE;
		second = FP_MISSING_VALUE;

		longitude = FP_MISSING_VALUE;
		latitude = FP_MISSING_VALUE;
		sampleDepth = FP_MISSING_VALUE;
		salinity = FP_MISSING_VALUE;
		sst = FP_MISSING_VALUE;
		tEqu = FP_MISSING_VALUE;
		slp = FP_MISSING_VALUE;
		pEqu = FP_MISSING_VALUE;

		xCO2WaterSst = FP_MISSING_VALUE;
		xCO2WaterTEqu = FP_MISSING_VALUE;
		fCO2WaterSst = FP_MISSING_VALUE;
		fCO2WaterTEqu = FP_MISSING_VALUE;
		pCO2WaterSst = FP_MISSING_VALUE;
		pCO2WaterTEqu = FP_MISSING_VALUE;

		xCO2Atm = FP_MISSING_VALUE;
		pCO2Atm = FP_MISSING_VALUE;
		fCO2Atm = FP_MISSING_VALUE;
		deltaXCO2 = FP_MISSING_VALUE;
		deltaPCO2 = FP_MISSING_VALUE;
		deltaFCO2 = FP_MISSING_VALUE;

		relativeHumidity = FP_MISSING_VALUE;
		specificHumidity = FP_MISSING_VALUE;
		shipSpeed = FP_MISSING_VALUE; 
		shipDirection = FP_MISSING_VALUE; 
		windSpeedTrue = FP_MISSING_VALUE;
		windSpeedRelative = FP_MISSING_VALUE;
		windDirectionTrue = FP_MISSING_VALUE;
		windDirectionRelative = FP_MISSING_VALUE;

		geopositionWoce = CHAR_MISSING_VALUE;
		sampleDepthWoce = CHAR_MISSING_VALUE;
		salinityWoce = CHAR_MISSING_VALUE;
		tEquWoce = CHAR_MISSING_VALUE;
		sstWoce = CHAR_MISSING_VALUE;
		pEquWoce = CHAR_MISSING_VALUE;
		slpWoce = CHAR_MISSING_VALUE;

		xCO2WaterTEquWoce = CHAR_MISSING_VALUE;
		xCO2WaterSstWoce = CHAR_MISSING_VALUE;
		pCO2WaterTEquWoce = CHAR_MISSING_VALUE;
		pCO2WaterSstWoce = CHAR_MISSING_VALUE;
		fCO2WaterTEquWoce = CHAR_MISSING_VALUE;
		fCO2WaterSstWoce = CHAR_MISSING_VALUE;

		xCO2AtmWoce = CHAR_MISSING_VALUE;
		pCO2AtmWoce = CHAR_MISSING_VALUE;
		fCO2AtmWoce = CHAR_MISSING_VALUE;
		deltaXCO2Woce = CHAR_MISSING_VALUE;
		deltaPCO2Woce = CHAR_MISSING_VALUE;
		deltaFCO2Woce = CHAR_MISSING_VALUE;

		relativeHumidityWoce = CHAR_MISSING_VALUE;
		specificHumidityWoce = CHAR_MISSING_VALUE;
		shipSpeedWoce = CHAR_MISSING_VALUE;
		shipDirectionWoce = CHAR_MISSING_VALUE;
		windSpeedTrueWoce = CHAR_MISSING_VALUE;
		windSpeedRelativeWoce = CHAR_MISSING_VALUE;
		windDirectionTrueWoce = CHAR_MISSING_VALUE;
		windDirectionRelativeWoce = CHAR_MISSING_VALUE;

		woaSss = FP_MISSING_VALUE;
		ncepSlp = FP_MISSING_VALUE;

		fCO2FromXCO2TEqu = FP_MISSING_VALUE;
		fCO2FromXCO2Sst = FP_MISSING_VALUE;
		fCO2FromPCO2TEqu = FP_MISSING_VALUE;
		fCO2FromPCO2Sst = FP_MISSING_VALUE;
		fCO2FromFCO2TEqu = FP_MISSING_VALUE;
		fCO2FromFCO2Sst = FP_MISSING_VALUE;
		fCO2FromPCO2TEquNcep = FP_MISSING_VALUE;
		fCO2FromPCO2SstNcep = FP_MISSING_VALUE;
		fCO2FromXCO2TEquWoa = FP_MISSING_VALUE;
		fCO2FromXCO2SstWoa = FP_MISSING_VALUE;
		fCO2FromXCO2TEquNcep = FP_MISSING_VALUE;
		fCO2FromXCO2SstNcep = FP_MISSING_VALUE;
		fCO2FromXCO2TEquNcepWoa = FP_MISSING_VALUE;
		fCO2FromXCO2SstNcepWoa = FP_MISSING_VALUE;

		fCO2Rec = FP_MISSING_VALUE;
		fCO2Source = INT_MISSING_VALUE;
		deltaT = FP_MISSING_VALUE;
		regionID = CHAR_MISSING_VALUE;
		calcSpeed = FP_MISSING_VALUE;
		etopo2Depth = FP_MISSING_VALUE;
		gvCO2 = FP_MISSING_VALUE;
		distToLand = FP_MISSING_VALUE;
		days1970 = FP_MISSING_VALUE;
		dayOfYear = FP_MISSING_VALUE;

		fCO2FromXCO2TEquWoce = CHAR_MISSING_VALUE;
		fCO2FromXCO2SstWoce = CHAR_MISSING_VALUE;
		fCO2FromPCO2TEquWoce = CHAR_MISSING_VALUE;
		fCO2FromPCO2SstWoce = CHAR_MISSING_VALUE;
		fCO2FromFCO2TEquWoce = CHAR_MISSING_VALUE;
		fCO2FromFCO2SstWoce = CHAR_MISSING_VALUE;
		fCO2FromPCO2TEquNcepWoce = CHAR_MISSING_VALUE;
		fCO2FromPCO2SstNcepWoce = CHAR_MISSING_VALUE;
		fCO2FromXCO2TEquWoaWoce = CHAR_MISSING_VALUE;
		fCO2FromXCO2SstWoaWoce = CHAR_MISSING_VALUE;
		fCO2FromXCO2TEquNcepWoce = CHAR_MISSING_VALUE;
		fCO2FromXCO2SstNcepWoce = CHAR_MISSING_VALUE;
		fCO2FromXCO2TEquNcepWoaWoce = CHAR_MISSING_VALUE;
		fCO2FromXCO2SstNcepWoaWoce = CHAR_MISSING_VALUE;

		fCO2RecWoce = CHAR_MISSING_VALUE;
		deltaTWoce = CHAR_MISSING_VALUE;
		calcSpeedWoce = CHAR_MISSING_VALUE;
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
	 * @param dataValues
	 * 		data values
	 * @throws IllegalArgumentException
	 * 		if the number of data types and data values do not match, or
	 * 		if a data value string cannot be parsed for the expected type 
	 */
	public SocatCruiseData(List<DataColumnType> columnTypes, 
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
					 type.equals(DataColumnType.TIMESTAMP) ||
					 type.equals(DataColumnType.DATE) ||
					 type.equals(DataColumnType.TIME) ||
					 type.equals(DataColumnType.COMMENT) ||
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
				else if ( type.equals(DataColumnType.EQUILIBRATOR_PRESSURE) ) {
					this.pEqu = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.SEA_LEVEL_PRESSURE) ) {
					this.slp = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XCO2_WATER_TEQU) ) {
					this.xCO2WaterTEqu = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XCO2_WATER_SST) ) {
					this.xCO2WaterSst = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.PCO2_WATER_TEQU) ) {
					this.pCO2WaterTEqu = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.PCO2_WATER_SST) ) {
					this.pCO2WaterSst = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.FCO2_WATER_TEQU) ) {
					this.fCO2WaterTEqu = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.FCO2_WATER_SST) ) {
					this.fCO2WaterSst = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XCO2_ATM) ) {
					this.xCO2Atm = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.PCO2_ATM) ) {
					this.pCO2Atm = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.FCO2_ATM) ) {
					this.fCO2Atm = Double.valueOf(value);
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
				else if ( type.equals(DataColumnType.GEOPOSITION_WOCE) ) {
					this.geopositionWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.SAMPLE_DEPTH_WOCE) ) {
					this.sampleDepthWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.SALINITY_WOCE) ) {
					this.salinityWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.EQUILIBRATOR_TEMPERATURE_WOCE) ) {
					this.tEquWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.SEA_SURFACE_TEMPERATURE_WOCE) ) {
					this.sstWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.EQUILIBRATOR_PRESSURE_WOCE) ) {
					this.pEquWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.SEA_LEVEL_PRESSURE_WOCE) ) {
					this.slpWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.XCO2_WATER_TEQU_WOCE) ) {
					this.xCO2WaterTEquWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.XCO2_WATER_SST_WOCE) ) {
					this.xCO2WaterSstWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.PCO2_WATER_TEQU_WOCE) ) {
					this.pCO2WaterTEquWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.PCO2_WATER_SST_WOCE) ) {
					this.pCO2WaterSstWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.FCO2_WATER_TEQU_WOCE) ) {
					this.fCO2WaterTEquWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.FCO2_WATER_SST_WOCE) ) {
					this.fCO2WaterSstWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.XCO2_ATM_WOCE) ) {
					this.xCO2AtmWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.PCO2_ATM_WOCE) ) {
					this.pCO2AtmWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.FCO2_ATM_WOCE) ) {
					this.fCO2AtmWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.DELTA_XCO2_WOCE) ) {
					this.deltaXCO2Woce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.DELTA_PCO2_WOCE) ) {
					this.deltaPCO2Woce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.DELTA_FCO2_WOCE) ) {
					this.deltaFCO2Woce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.RELATIVE_HUMIDITY_WOCE) ) {
					this.relativeHumidityWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.SPECIFIC_HUMIDITY_WOCE) ) {
					this.specificHumidityWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.SHIP_SPEED_WOCE) ) {
					this.shipSpeedWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.SHIP_DIRECTION_WOCE) ) {
					this.shipDirectionWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.WIND_SPEED_TRUE_WOCE) ) {
					this.windSpeedTrueWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.WIND_SPEED_RELATIVE_WOCE) ) {
					this.windSpeedRelativeWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.WIND_DIRECTION_TRUE_WOCE) ) {
					this.windDirectionTrueWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.WIND_DIRECTION_RELATIVE_WOCE) ) {
					this.windDirectionRelativeWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.FCO2_REC_WOCE) ) {
					this.fCO2RecWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.FCO2_REC) ) {
					// Ignore the value - it will be recalculated
					;
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
		for ( ArrayList<String> dataVals : dataValsTable ) {
			socatDataList.add(new SocatCruiseData(dataTypes, dataVals));
		}
		// Assign the WOCE flags in the SocatCruiseData objects
		// assigned by the SanityChecker (and includes user-provided WOCE flags) 
		ArrayList<HashSet<Integer>> woceThrees = cruise.getWoceThreeRowIndices();
		ArrayList<HashSet<Integer>> woceFours = cruise.getWoceFourRowIndices();
		int k = -1;
		for ( DataColumnType colType : cruise.getDataColTypes() ) {
			k++;
			if ( colType.equals(DataColumnType.TIMESTAMP) ||
				 colType.equals(DataColumnType.DATE) ||
				 colType.equals(DataColumnType.YEAR) ||
				 colType.equals(DataColumnType.MONTH) ||
				 colType.equals(DataColumnType.DAY) ||
				 colType.equals(DataColumnType.TIME) ||
				 colType.equals(DataColumnType.HOUR) ||
				 colType.equals(DataColumnType.MINUTE) ||
				 colType.equals(DataColumnType.SECOND) ||
				 colType.equals(DataColumnType.DAY_OF_YEAR) ||
				 colType.equals(DataColumnType.SECOND_OF_DAY) || 
				 colType.equals(DataColumnType.LONGITUDE) || 
				 colType.equals(DataColumnType.LATITUDE) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					// The WOCE flags for all of these columns should be the same
					// but just in case not...
					SocatCruiseData rowData = socatDataList.get(rowIdx);
					if ( ! rowData.geopositionWoce.equals('4') )
						rowData.geopositionWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).geopositionWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.SAMPLE_DEPTH) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).sampleDepthWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).sampleDepthWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.SALINITY) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).salinityWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).salinityWoce = '4';
				}
			}
			else if ( 	 colType.equals(DataColumnType.EQUILIBRATOR_TEMPERATURE) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).tEquWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).tEquWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.SEA_SURFACE_TEMPERATURE) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).sstWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).sstWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.EQUILIBRATOR_PRESSURE) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).pEquWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).pEquWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.SEA_LEVEL_PRESSURE) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).slpWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).slpWoce = '4';
				}
			}

			else if ( colType.equals(DataColumnType.XCO2_WATER_TEQU) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).xCO2WaterTEquWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).xCO2WaterTEquWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.XCO2_WATER_SST) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).xCO2WaterSstWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).xCO2WaterSstWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.PCO2_WATER_TEQU) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).pCO2WaterTEquWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).pCO2WaterTEquWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.PCO2_WATER_SST) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).pCO2WaterSstWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).pCO2WaterSstWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.FCO2_WATER_TEQU) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).fCO2WaterTEquWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).fCO2WaterTEquWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.FCO2_WATER_SST) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).fCO2WaterSstWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).fCO2WaterSstWoce = '4';
				}
			}

			else if ( colType.equals(DataColumnType.XCO2_ATM) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).xCO2AtmWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).xCO2AtmWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.PCO2_ATM) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).pCO2AtmWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).pCO2AtmWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.FCO2_ATM) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).fCO2AtmWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).fCO2AtmWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.DELTA_XCO2) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).deltaXCO2Woce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).deltaXCO2Woce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.DELTA_PCO2) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).deltaPCO2Woce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).deltaPCO2Woce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.DELTA_FCO2) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).deltaFCO2Woce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).deltaFCO2Woce = '4';
				}
			}

			else if ( colType.equals(DataColumnType.RELATIVE_HUMIDITY) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).relativeHumidityWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).relativeHumidityWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.SPECIFIC_HUMIDITY) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).specificHumidityWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).specificHumidityWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.SHIP_SPEED) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).shipSpeedWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).shipSpeedWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.SHIP_DIRECTION) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).shipDirectionWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).shipDirectionWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.WIND_SPEED_TRUE) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).windSpeedTrueWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).windSpeedTrueWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.WIND_SPEED_RELATIVE) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).windSpeedRelativeWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).windSpeedRelativeWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.WIND_DIRECTION_TRUE) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).windDirectionTrueWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).windDirectionTrueWoce = '4';
				}
			}
			else if ( colType.equals(DataColumnType.WIND_DIRECTION_RELATIVE) ) {
				for ( Integer rowIdx : woceThrees.get(k) ) {
					socatDataList.get(rowIdx).windDirectionRelativeWoce = '3';
				}
				for ( Integer rowIdx : woceFours.get(k) ) {
					socatDataList.get(rowIdx).windDirectionRelativeWoce = '4';
				}
			}
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
	 * @param year 
	 * 		the year of the data measurement to set; 
	 * 		if null, {@link #INT_MISSING_VALUE} is assigned
	 */
	public void setYear(Integer year) {
		if ( year == null )
			this.year = INT_MISSING_VALUE;
		else
			this.year = year;
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
	 * @param longitude 
	 * 		the longitude of the data measurement to set; 
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setLongitude(Double longitude) {
		if ( longitude == null )
			this.longitude = FP_MISSING_VALUE;
		else
			this.longitude = longitude;
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
	 * @param latitude 
	 * 		the latitude of the data measurement to set; 
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setLatitude(Double latitude) {
		if ( latitude == null )
			this.latitude = FP_MISSING_VALUE;
		else
			this.latitude = latitude;
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
	 * @param sampleDepth 
	 * 		the sampling depth to set
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setSampleDepth(Double sampleDepth) {
		if ( sampleDepth == null )
			this.sampleDepth = FP_MISSING_VALUE;
		else
			this.sampleDepth = sampleDepth;
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
	 * @param salinity
	 * 		the sea surface salinity to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setSalinity(Double salinity) {
		if ( salinity == null )
			this.salinity = FP_MISSING_VALUE;
		else
			this.salinity = salinity;
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
	 * @param sst 
	 * 		the sea surface temperature to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setSst(Double sst) {
		if ( sst == null )
			this.sst = FP_MISSING_VALUE;
		else
			this.sst = sst;
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
	 * @param tEqu
	 * 		the equilibrator temperature to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void settEqu(Double tEqu) {
		if ( tEqu == null )
			this.tEqu = FP_MISSING_VALUE;
		else
			this.tEqu = tEqu;
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
	 * @param slp 
	 * 		the atmospheric sea-level pressure to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setSlp(Double slp) {
		if ( slp == null )
			this.slp = FP_MISSING_VALUE;
		else
			this.slp = slp;
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
	 * @param pEqu
	 * 		the equilibrator pressure to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setpEqu(Double pEqu) {
		if ( pEqu == null )
			this.pEqu = FP_MISSING_VALUE;
		else
			this.pEqu = pEqu;
	}

	/**
	 * @return 
	 * 		the xCO2WaterSst;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getxCO2WaterSst() {
		return xCO2WaterSst;
	}

	/**
	 * @param xCO2WaterSst 
	 * 		the xCO2WaterSst to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setxCO2WaterSst(Double xCO2WaterSst) {
		if ( xCO2WaterSst == null )
			this.xCO2WaterSst = FP_MISSING_VALUE;
		else
			this.xCO2WaterSst = xCO2WaterSst;
	}

	/**
	 * @return 
	 * 		the xCO2WaterTEqu;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getxCO2WaterTEqu() {
		return xCO2WaterTEqu;
	}

	/**
	 * @param xCO2WaterTEqu 
	 * 		the xCO2WaterTEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setxCO2WaterTEqu(Double xCO2WaterTEqu) {
		if ( xCO2WaterTEqu == null )
			this.xCO2WaterTEqu = FP_MISSING_VALUE;
		else
			this.xCO2WaterTEqu = xCO2WaterTEqu;
	}

	/**
	 * @return 
	 * 		the pCO2WaterSst;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getpCO2WaterSst() {
		return pCO2WaterSst;
	}

	/**
	 * @param pCO2WaterSst 
	 * 		the pCO2WaterSst to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setpCO2WaterSst(Double pCO2WaterSst) {
		if ( pCO2WaterSst == null )
			this.pCO2WaterSst = FP_MISSING_VALUE;
		else
			this.pCO2WaterSst = pCO2WaterSst;
	}

	/**
	 * @return 
	 * 		the pCO2WaterTEqu;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getpCO2WaterTEqu() {
		return pCO2WaterTEqu;
	}

	/**
	 * @param pCO2WaterTEqu 
	 * 		the pCO2WaterTEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setpCO2WaterTEqu(Double pCO2WaterTEqu) {
		if ( pCO2WaterTEqu == null )
			this.pCO2WaterTEqu = FP_MISSING_VALUE;
		else
			this.pCO2WaterTEqu = pCO2WaterTEqu;
	}

	/**
	 * @return 
	 * 		the fCO2WaterSst;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2WaterSst() {
		return fCO2WaterSst;
	}

	/**
	 * @param fCO2WaterSst 
	 * 		the fCO2WaterSst to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2WaterSst(Double fCO2WaterSst) {
		if ( fCO2WaterSst == null )
			this.fCO2WaterSst = FP_MISSING_VALUE;
		else
			this.fCO2WaterSst = fCO2WaterSst;
	}

	/**
	 * @return 
	 * 		the fCO2WaterTEqu;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2WaterTEqu() {
		return fCO2WaterTEqu;
	}

	/**
	 * @param fCO2WaterTEqu 
	 * 		the fCO2WaterTEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2WaterTEqu(Double fCO2WaterTEqu) {
		if ( fCO2WaterTEqu == null )
			this.fCO2WaterTEqu = FP_MISSING_VALUE;
		else
			this.fCO2WaterTEqu = fCO2WaterTEqu;
	}

	/**
	 * @return 
	 * 		the atmospheric xCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getxCO2Atm() {
		return xCO2Atm;
	}

	/**
	 * @param xCO2Atm 
	 * 		the atmospheric xCO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setxCO2Atm(Double xCO2Atm) {
		if ( xCO2Atm == null )
			this.xCO2Atm = FP_MISSING_VALUE;
		else
			this.xCO2Atm = xCO2Atm;
	}

	/**
	 * @return 
	 * 		the atmospheric pCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getpCO2Atm() {
		return pCO2Atm;
	}

	/**
	 * @param pCO2Atm 
	 * 		the atmospheric pCO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setpCO2Atm(Double pCO2Atm) {
		if ( pCO2Atm == null )
			this.pCO2Atm = FP_MISSING_VALUE;
		else
			this.pCO2Atm = pCO2Atm;
	}

	/**
	 * @return 
	 * 		the atmospheric fCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2Atm() {
		return fCO2Atm;
	}

	/**
	 * @param fCO2Atm 
	 * 		the atmospheric fCO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2Atm(Double fCO2Atm) {
		if ( fCO2Atm == null )
			this.fCO2Atm = FP_MISSING_VALUE;
		else
			this.fCO2Atm = fCO2Atm;
	}

	/**
	 * @return 
	 * 		the relative humidity;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getRelativeHumidity() {
		return relativeHumidity;
	}

	/**
	 * @param relativeHumidity 
	 * 		the relative humidity to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setRelativeHumidity(Double relativeHumidity) {
		if ( relativeHumidity == null )
			this.relativeHumidity = FP_MISSING_VALUE;
		else
			this.relativeHumidity = relativeHumidity;
	}

	/**
	 * @return 
	 * 		the specific humidity;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getSpecificHumidity() {
		return specificHumidity;
	}

	/**
	 * @param specificHumidity 
	 * 		the specific humidity to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setSpecificHumidity(Double specificHumidity) {
		if ( specificHumidity == null )
			this.specificHumidity = FP_MISSING_VALUE;
		else
			this.specificHumidity = specificHumidity;
	}

	/**
	 * @return 
	 * 		the difference water xCO2 - average air xCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDeltaXCO2() {
		return deltaXCO2;
	}

	/**
	 * @param deltaXCO2 
	 * 		the difference water xCO2 - average air xCO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setDeltaXCO2(Double deltaXCO2) {
		if ( deltaXCO2 == null )
			this.deltaXCO2 = FP_MISSING_VALUE;
		else
			this.deltaXCO2 = deltaXCO2;
	}

	/**
	 * @return 
	 * 		the difference water pCO2 - average air pCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDeltaPCO2() {
		return deltaPCO2;
	}

	/**
	 * @param deltaPCO2 
	 * 		the difference water pCO2 - average air pCO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setDeltaPCO2(Double deltaPCO2) {
		if ( deltaPCO2 == null )
			this.deltaPCO2 = FP_MISSING_VALUE;
		else
			this.deltaPCO2 = deltaPCO2;
	}

	/**
	 * @return 
	 * 		the difference water fCO2 - average air fCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDeltaFCO2() {
		return deltaFCO2;
	}

	/**
	 * @param deltaFCO2 
	 * 		the difference water fCO2 - average air fCO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setDeltaFCO2(Double deltaFCO2) {
		if ( deltaFCO2 == null )
			this.deltaFCO2 = FP_MISSING_VALUE;
		else
			this.deltaFCO2 = deltaFCO2;
	}

	/**
	 * @return 
	 * 		the ship speed in knots;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getShipSpeed() {
		return shipSpeed;
	}

	/**
	 * @param shipSpeed 
	 * 		the ship speed in knots to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setShipSpeed(Double shipSpeed) {
		if ( shipSpeed == null )
			this.shipSpeed = FP_MISSING_VALUE;
		else
			this.shipSpeed = shipSpeed;
	}

	/**
	 * @return 
	 * 		the ship direction in degrees clockwise from N;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getShipDirection() {
		return shipDirection;
	}

	/**
	 * @param shipDirection 
	 * 		the ship direction in degrees clockwise from N to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setShipDirection(Double shipDirection) {
		if ( shipDirection == null )
			this.shipDirection = FP_MISSING_VALUE;
		else
			this.shipDirection = shipDirection;
	}

	/**
	 * @return 
	 * 		the true wind speed in m/s;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getWindSpeedTrue() {
		return windSpeedTrue;
	}

	/**
	 * @param windSpeedTrue 
	 * 		the true wind speed in m/s to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setWindSpeedTrue(Double windSpeedTrue) {
		if ( windSpeedTrue == null )
			this.windSpeedTrue = FP_MISSING_VALUE;
		else
			this.windSpeedTrue = windSpeedTrue;
	}

	/**
	 * @return 
	 * 		the relative wind speed in m/s;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getWindSpeedRelative() {
		return windSpeedRelative;
	}

	/**
	 * @param windSpeedRelative
	 * 		the relative wind speed in m/s to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setWindSpeedRelative(Double windSpeedRelative) {
		if ( windSpeedRelative == null )
			this.windSpeedRelative = FP_MISSING_VALUE;
		else
			this.windSpeedRelative = windSpeedRelative;
	}

	/**
	 * @return 
	 * 		the true wind direction in degrees clockwise from N;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getWindDirectionTrue() {
		return windDirectionTrue;
	}

	/**
	 * @param windDirectionTrue 
	 * 		the true wind direction in degrees clockwise from N to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setWindDirectionTrue(Double windDirectionTrue) {
		if ( windDirectionTrue == null )
			this.windDirectionTrue = FP_MISSING_VALUE;
		else
			this.windDirectionTrue = windDirectionTrue;
	}

	/**
	 * @return 
	 * 		the relative wind direction in degrees clockwise from N;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getWindDirectionRelative() {
		return windDirectionRelative;
	}

	/**
	 * @param windDirectionRelative 
	 * 		the relative wind direction in degrees clockwise from N to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setWindDirectionRelative(Double windDirectionRelative) {
		if ( windDirectionRelative == null )
			this.windDirectionRelative = FP_MISSING_VALUE;
		else
			this.windDirectionRelative = windDirectionRelative;
	}

	/**
	 * @return 
	 * 		the geoposition WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getGeopositionWoce() {
		return geopositionWoce;
	}

	/**
	 * @param geopositionWoce 
	 * 		the geoposition WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setGeopositionWoce(Character geopositionWoce) {
		if ( geopositionWoce == null )
			this.geopositionWoce = CHAR_MISSING_VALUE;
		else
			this.geopositionWoce = geopositionWoce;
	}

	/**
	 * @return 
	 * 		the sampling depth WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getSampleDepthWoce() {
		return sampleDepthWoce;
	}

	/**
	 * @param sampleDepthWoce 
	 * 		the sampling depth WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setSampleDepthWoce(Character sampleDepthWoce) {
		if ( sampleDepthWoce == null )
			this.sampleDepthWoce = CHAR_MISSING_VALUE;
		else
			this.sampleDepthWoce = sampleDepthWoce;
	}

	/**
	 * @return 
	 * 		the salinity WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getSalinityWoce() {
		return salinityWoce;
	}

	/**
	 * @param salinityWoce 
	 * 		the salinity WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setSalinityWoce(Character salinityWoce) {
		if ( salinityWoce == null )
			this.salinityWoce = CHAR_MISSING_VALUE;
		else
			this.salinityWoce = salinityWoce;
	}

	/**
	 * @return 
	 * 		the equilibrator temperature WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character gettEquWoce() {
		return tEquWoce;
	}

	/**
	 * @param tEquWoce 
	 * 		the equilibrator temperature WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void settEquWoce(Character tEquWoce) {
		if ( tEquWoce == null )
			this.tEquWoce = CHAR_MISSING_VALUE;
		else
			this.tEquWoce = tEquWoce;
	}

	/**
	 * @return 
	 * 		the SST WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getSstWoce() {
		return sstWoce;
	}

	/**
	 * @param sstWoce 
	 * 		the SST WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setSstWoce(Character sstWoce) {
		if ( sstWoce == null )
			this.sstWoce = CHAR_MISSING_VALUE;
		else
			this.sstWoce = sstWoce;
	}

	/**
	 * @return 
	 * 		the equilibrator pressure WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getpEquWoce() {
		return pEquWoce;
	}

	/**
	 * @param pEquWoce 
	 * 		the equilibrator pressure WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setpEquWoce(Character pEquWoce) {
		if ( pEquWoce == null )
			this.pEquWoce = CHAR_MISSING_VALUE;
		else
			this.pEquWoce = pEquWoce;
	}

	/**
	 * @return 
	 * 		the SLP WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getSlpWoce() {
		return slpWoce;
	}

	/**
	 * @param slpWoce 
	 * 		the SLP WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setSlpWoce(Character slpWoce) {
		if ( slpWoce == null )
			this.slpWoce = CHAR_MISSING_VALUE;
		else
			this.slpWoce = slpWoce;
	}

	/**
	 * @return 
	 * 		the xCO2WaterEqu WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getxCO2WaterTEquWoce() {
		return xCO2WaterTEquWoce;
	}

	/**
	 * @param xCO2WaterTEquWoce 
	 * 		the xCO2WaterEqu WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setxCO2WaterTEquWoce(Character xCO2WaterTEquWoce) {
		if ( xCO2WaterTEquWoce == null )
			this.xCO2WaterTEquWoce = CHAR_MISSING_VALUE;
		else
			this.xCO2WaterTEquWoce = xCO2WaterTEquWoce;
	}

	/**
	 * @return 
	 * 		the xCO2WaterSst WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getxCO2WaterSstWoce() {
		return xCO2WaterSstWoce;
	}

	/**
	 * @param xCO2WaterSstWoce 
	 * 		the xCO2WaterSst WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setxCO2WaterSstWoce(Character xCO2WaterSstWoce) {
		if ( xCO2WaterSstWoce == null )
			this.xCO2WaterSstWoce = CHAR_MISSING_VALUE;
		else
			this.xCO2WaterSstWoce = xCO2WaterSstWoce;
	}

	/**
	 * @return 
	 * 		the pCO2WaterEqu WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getpCO2WaterTEquWoce() {
		return pCO2WaterTEquWoce;
	}

	/**
	 * @param pCO2WaterTEquWoce 
	 * 		the pCO2WaterEqu WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setpCO2WaterTEquWoce(Character pCO2WaterTEquWoce) {
		if ( pCO2WaterTEquWoce == null )
			this.pCO2WaterTEquWoce = CHAR_MISSING_VALUE;
		else
			this.pCO2WaterTEquWoce = pCO2WaterTEquWoce;
	}

	/**
	 * @return 
	 * 		the pCO2WaterSst WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getpCO2WaterSstWoce() {
		return pCO2WaterSstWoce;
	}

	/**
	 * @param pCO2WaterSstWoce 
	 * 		the pCO2WaterSst WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setpCO2WaterSstWoce(Character pCO2WaterSstWoce) {
		if ( pCO2WaterSstWoce == null )
			this.pCO2WaterSstWoce = CHAR_MISSING_VALUE;
		else
			this.pCO2WaterSstWoce = pCO2WaterSstWoce;
	}

	/**
	 * @return 
	 * 		the fCO2WaterEqu WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2WaterTEquWoce() {
		return fCO2WaterTEquWoce;
	}

	/**
	 * @param fCO2WaterTEquWoce 
	 * 		the fCO2WaterEqu WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2WaterTEquWoce(Character fCO2WaterTEquWoce) {
		if ( fCO2WaterTEquWoce == null )
			this.fCO2WaterTEquWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2WaterTEquWoce = fCO2WaterTEquWoce;
	}

	/**
	 * @return 
	 * 		the fCO2WaterSst WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2WaterSstWoce() {
		return fCO2WaterSstWoce;
	}

	/**
	 * @param fCO2WaterSstWoce 
	 * 		the fCO2WaterSst WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2WaterSstWoce(Character fCO2WaterSstWoce) {
		if ( fCO2WaterSstWoce == null )
			this.fCO2WaterSstWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2WaterSstWoce = fCO2WaterSstWoce;
	}

	/**
	 * @return 
	 * 		the xCO2Atm WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getxCO2AtmWoce() {
		return xCO2AtmWoce;
	}

	/**
	 * @param xCO2AtmWoce 
	 * 		the xCO2Atm WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setxCO2AtmWoce(Character xCO2AtmWoce) {
		if ( xCO2AtmWoce == null )
			this.xCO2AtmWoce = CHAR_MISSING_VALUE;
		else
			this.xCO2AtmWoce = xCO2AtmWoce;
	}

	/**
	 * @return 
	 * 		the pCO2Atm WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getpCO2AtmWoce() {
		return pCO2AtmWoce;
	}

	/**
	 * @param pCO2AtmWoce 
	 * 		the pCO2Atm WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setpCO2AtmWoce(Character pCO2AtmWoce) {
		if ( pCO2AtmWoce == null )
			this.pCO2AtmWoce = CHAR_MISSING_VALUE;
		else
			this.pCO2AtmWoce = pCO2AtmWoce;
	}

	/**
	 * @return 
	 * 		the fCO2Atm WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2AtmWoce() {
		return fCO2AtmWoce;
	}

	/**
	 * @param fCO2AtmWoce 
	 * 		the fCO2Atm WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2AtmWoce(Character fCO2AtmWoce) {
		if ( fCO2AtmWoce == null )
			this.fCO2AtmWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2AtmWoce = fCO2AtmWoce;
	}

	/**
	 * @return 
	 * 		the deltaXCO2 WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getDeltaXCO2Woce() {
		return deltaXCO2Woce;
	}

	/**
	 * @param deltaXCO2Woce 
	 * 		the deltaXCO2 WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setDeltaXCO2Woce(Character deltaXCO2Woce) {
		if ( deltaXCO2Woce == null )
			this.deltaXCO2Woce = CHAR_MISSING_VALUE;
		else
			this.deltaXCO2Woce = deltaXCO2Woce;
	}

	/**
	 * @return 
	 * 		the deltaPCO2 WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getDeltaPCO2Woce() {
		return deltaPCO2Woce;
	}

	/**
	 * @param deltaPCO2Woce 
	 * 		the deltaPCO2 WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setDeltaPCO2Woce(Character deltaPCO2Woce) {
		if ( deltaPCO2Woce == null )
			this.deltaPCO2Woce = CHAR_MISSING_VALUE;
		else
			this.deltaPCO2Woce = deltaPCO2Woce;
	}

	/**
	 * @return 
	 * 		the deltaFCO2 WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getDeltaFCO2Woce() {
		return deltaFCO2Woce;
	}

	/**
	 * @param deltaFCO2Woce 
	 * 		the deltaFCO2 WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setDeltaFCO2Woce(Character deltaFCO2Woce) {
		if ( deltaFCO2Woce == null )
			this.deltaFCO2Woce = CHAR_MISSING_VALUE;
		else
			this.deltaFCO2Woce = deltaFCO2Woce;
	}

	/**
	 * @return 
	 * 		the relative humidity WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getRelativeHumidityWoce() {
		return relativeHumidityWoce;
	}

	/**
	 * @param relativeHumidityWoce 
	 * 		the relative humidity WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setRelativeHumidityWoce(Character relativeHumidityWoce) {
		if ( relativeHumidityWoce == null )
			this.relativeHumidityWoce = CHAR_MISSING_VALUE;
		else
			this.relativeHumidityWoce = relativeHumidityWoce;
	}

	/**
	 * @return 
	 * 		the specific humidity WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getSpecificHumidityWoce() {
		return specificHumidityWoce;
	}

	/**
	 * @param specificHumidityWoce 
	 * 		the specific humidity WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setSpecificHumidityWoce(Character specificHumidityWoce) {
		if ( specificHumidityWoce == null )
			this.specificHumidityWoce = CHAR_MISSING_VALUE;
		else
			this.specificHumidityWoce = specificHumidityWoce;
	}

	/**
	 * @return 
	 * 		the ship speed WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getShipSpeedWoce() {
		return shipSpeedWoce;
	}

	/**
	 * @param shipSpeedWoce 
	 * 		the ship speed WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setShipSpeedWoce(Character shipSpeedWoce) {
		if ( shipSpeedWoce == null )
			this.shipSpeedWoce = CHAR_MISSING_VALUE;
		else
			this.shipSpeedWoce = shipSpeedWoce;
	}

	/**
	 * @return 
	 * 		the ship direction WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getShipDirectionWoce() {
		return shipDirectionWoce;
	}

	/**
	 * @param shipDirectionWoce 
	 * 		the ship direction WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setShipDirectionWoce(Character shipDirectionWoce) {
		if ( shipDirectionWoce == null )
			this.shipDirectionWoce = CHAR_MISSING_VALUE;
		else
			this.shipDirectionWoce = shipDirectionWoce;
	}

	/**
	 * @return 
	 * 		the true wind speed WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getWindSpeedTrueWoce() {
		return windSpeedTrueWoce;
	}

	/**
	 * @param windSpeedTrueWoce 
	 * 		the true wind speed WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setWindSpeedTrueWoce(Character windSpeedTrueWoce) {
		if ( windSpeedTrueWoce == null )
			this.windSpeedTrueWoce = CHAR_MISSING_VALUE;
		else
			this.windSpeedTrueWoce = windSpeedTrueWoce;
	}

	/**
	 * @return 
	 * 		the relative wind speed WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getWindSpeedRelativeWoce() {
		return windSpeedRelativeWoce;
	}

	/**
	 * @param windSpeedRelativeWoce 
	 * 		the relative wind speed WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setWindSpeedRelativeWoce(Character windSpeedRelativeWoce) {
		if ( windSpeedRelativeWoce == null )
			this.windSpeedRelativeWoce = CHAR_MISSING_VALUE;
		else
			this.windSpeedRelativeWoce = windSpeedRelativeWoce;
	}

	/**
	 * @return 
	 * 		the true wind direction WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getWindDirectionTrueWoce() {
		return windDirectionTrueWoce;
	}

	/**
	 * @param windDirectionTrueWoce 
	 * 		the true wind direction WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setWindDirectionTrueWoce(Character windDirectionTrueWoce) {
		if ( windDirectionTrueWoce == null )
			this.windDirectionTrueWoce = CHAR_MISSING_VALUE;
		else
			this.windDirectionTrueWoce = windDirectionTrueWoce;
	}

	/**
	 * @return 
	 * 		the relative wind direction WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getWindDirectionRelativeWoce() {
		return windDirectionRelativeWoce;
	}

	/**
	 * @param windDirectionRelativeWoce 
	 * 		the relative wind direction WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setWindDirectionRelativeWoce(Character windDirectionRelativeWoce) {
		if ( windDirectionRelativeWoce == null )
			this.windDirectionRelativeWoce = CHAR_MISSING_VALUE;
		else
			this.windDirectionRelativeWoce = windDirectionRelativeWoce;
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
	 * @param woaSss 
	 * 		the WOA sea surface salinity to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setWoaSss(Double woaSss) {
		if ( woaSss == null )
			this.woaSss = FP_MISSING_VALUE;
		else
			this.woaSss = woaSss;
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
	 * @param ncepSlp 
	 * 		the NCEP sea level pressure to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setNcepSlp(Double ncepSlp) {
		if ( ncepSlp == null )
			this.ncepSlp = FP_MISSING_VALUE;
		else
			this.ncepSlp = ncepSlp;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 TEqu;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromXCO2TEqu() {
		return fCO2FromXCO2TEqu;
	}

	/**
	 * @param fCO2FromXCO2TEqu 
	 * 		the fCO2 from xCO2 TEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2TEqu(Double fCO2FromXCO2TEqu) {
		if ( fCO2FromXCO2TEqu == null )
			this.fCO2FromXCO2TEqu = FP_MISSING_VALUE;
		else
			this.fCO2FromXCO2TEqu = fCO2FromXCO2TEqu;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 SST;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromXCO2Sst() {
		return fCO2FromXCO2Sst;
	}

	/**
	 * @param fCO2FromXCO2Sst 
	 * 		the fCO2 from xCO2 SST to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2Sst(Double fCO2FromXCO2Sst) {
		if ( fCO2FromXCO2Sst == null )
			this.fCO2FromXCO2Sst = FP_MISSING_VALUE;
		else
			this.fCO2FromXCO2Sst = fCO2FromXCO2Sst;
	}

	/**
	 * @return 
	 * 		the fCO2 from pCO2 TEqu;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromPCO2TEqu() {
		return fCO2FromPCO2TEqu;
	}

	/**
	 * @param fCO2FromPCO2TEqu 
	 * 		the fCO2 from pCO2 TEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromPCO2TEqu(Double fCO2FromPCO2TEqu) {
		if ( fCO2FromPCO2TEqu == null )
			this.fCO2FromPCO2TEqu = FP_MISSING_VALUE;
		else
			this.fCO2FromPCO2TEqu = fCO2FromPCO2TEqu;
	}

	/**
	 * @return 
	 * 		the fCO2 from pCO2 SST;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned 
	 */
	public Double getfCO2FromPCO2Sst() {
		return fCO2FromPCO2Sst;
	}

	/**
	 * @param fCO2FromPCO2Sst 
	 * 		the fCO2 from pCO2 SST to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromPCO2Sst(Double fCO2FromPCO2Sst) {
		if ( fCO2FromPCO2Sst == null )
			this.fCO2FromPCO2Sst = FP_MISSING_VALUE;
		else
			this.fCO2FromPCO2Sst = fCO2FromPCO2Sst;
	}

	/**
	 * @return 
	 * 		the fCO2 from fCO2 TEqu;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromFCO2TEqu() {
		return fCO2FromFCO2TEqu;
	}

	/**
	 * @param fCO2FromFCO2TEqu
	 * 		the fCO2 from fCO2 TEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromFCO2TEqu(Double fCO2FromFCO2TEqu) {
		if ( fCO2FromFCO2TEqu == null )
			this.fCO2FromFCO2TEqu = FP_MISSING_VALUE;
		else
			this.fCO2FromFCO2TEqu = fCO2FromFCO2TEqu;
	}

	/**
	 * @return 
	 * 		the fCO2 from fCO2 SST;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromFCO2Sst() {
		return fCO2FromFCO2Sst;
	}

	/**
	 * @param fCO2FromFCO2Sst 
	 * 		the fCO2 from fCO2 SST to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromFCO2Sst(Double fCO2FromFCO2Sst) {
		if ( fCO2FromFCO2Sst == null )
			this.fCO2FromFCO2Sst = FP_MISSING_VALUE;
		else
			this.fCO2FromFCO2Sst = fCO2FromFCO2Sst;
	}

	/**
	 * @return 
	 * 		the fCO2 from pCO2 TEqu NCEP;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromPCO2TEquNcep() {
		return fCO2FromPCO2TEquNcep;
	}

	/**
	 * @param fCO2FromPCO2TEquNcep 
	 * 		the fCO2 from pCO2 TEqu NCEP to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromPCO2TEquNcep(Double fCO2FromPCO2TEquNcep) {
		if ( fCO2FromPCO2TEquNcep == null )
			this.fCO2FromPCO2TEquNcep = FP_MISSING_VALUE;
		else
			this.fCO2FromPCO2TEquNcep = fCO2FromPCO2TEquNcep;
	}

	/**
	 * @return 
	 * 		the fCO2 from pCO2 SST NCEP;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromPCO2SstNcep() {
		return fCO2FromPCO2SstNcep;
	}

	/**
	 * @param fCO2FromPCO2SstNcep 
	 * 		the fCO2 from pCO2 SST NCEP to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromPCO2SstNcep(Double fCO2FromPCO2SstNcep) {
		if ( fCO2FromPCO2SstNcep == null )
			this.fCO2FromPCO2SstNcep = FP_MISSING_VALUE;
		else
			this.fCO2FromPCO2SstNcep = fCO2FromPCO2SstNcep;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 TEqu WOA;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromXCO2TEquWoa() {
		return fCO2FromXCO2TEquWoa;
	}

	/**
	 * @param fCO2FromXCO2TEquWoa 
	 * 		the fCO2 from xCO2 TEqu WOA to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2TEquWoa(Double fCO2FromXCO2TEquWoa) {
		if ( fCO2FromXCO2TEquWoa == null )
			this.fCO2FromXCO2TEquWoa = FP_MISSING_VALUE;
		else
			this.fCO2FromXCO2TEquWoa = fCO2FromXCO2TEquWoa;
	}

	/**
	 * @return 
	 * 		the fCO2 from XCO2 SST WOA;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromXCO2SstWoa() {
		return fCO2FromXCO2SstWoa;
	}

	/**
	 * @param fCO2FromXCO2SstWoa 
	 * 		the fCO2 from xCO2 SST WOA to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2SstWoa(Double fCO2FromXCO2SstWoa) {
		if ( fCO2FromXCO2SstWoa == null )
			this.fCO2FromXCO2SstWoa = FP_MISSING_VALUE;
		else
			this.fCO2FromXCO2SstWoa = fCO2FromXCO2SstWoa;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 TEqu NCEP;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromXCO2TEquNcep() {
		return fCO2FromXCO2TEquNcep;
	}

	/**
	 * @param fCO2FromXCO2TEquNcep 
	 * 		the fCO2 from xCO2 TEqu NCEP to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2TEquNcep(Double fCO2FromXCO2TEquNcep) {
		if ( fCO2FromXCO2TEquNcep == null )
			this.fCO2FromXCO2TEquNcep = FP_MISSING_VALUE;
		else
			this.fCO2FromXCO2TEquNcep = fCO2FromXCO2TEquNcep;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 SST NCEP;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromXCO2SstNcep() {
		return fCO2FromXCO2SstNcep;
	}

	/**
	 * @param fCO2 from xCO2 SST NCEP 
	 * 		the fCO2 from xCO2 SST NCEP to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2SstNcep(Double fCO2FromXCO2SstNcep) {
		if ( fCO2FromXCO2SstNcep == null )
			this.fCO2FromXCO2SstNcep = FP_MISSING_VALUE;
		else
			this.fCO2FromXCO2SstNcep = fCO2FromXCO2SstNcep;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 TEqu NCEP WOA;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromXCO2TEquNcepWoa() {
		return fCO2FromXCO2TEquNcepWoa;
	}

	/**
	 * @param fCO2FromXCO2TEquNcepWoa 
	 * 		the fCO2 from xCO2 TEqu NCEP WOA to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2TEquNcepWoa(Double fCO2FromXCO2TEquNcepWoa) {
		if ( fCO2FromXCO2TEquNcepWoa == null )
			this.fCO2FromXCO2TEquNcepWoa = FP_MISSING_VALUE;
		else
			this.fCO2FromXCO2TEquNcepWoa = fCO2FromXCO2TEquNcepWoa;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 SST NCEP WOA;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2FromXCO2SstNcepWoa() {
		return fCO2FromXCO2SstNcepWoa;
	}

	/**
	 * @param fCO2FromXCO2SstNcepWoa 
	 * 		the fCO2 from xCO2 SST NCEP WOA to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2SstNcepWoa(Double fCO2FromXCO2SstNcepWoa) {
		if ( fCO2FromXCO2SstNcepWoa == null )
			this.fCO2FromXCO2SstNcepWoa = FP_MISSING_VALUE;
		else
			this.fCO2FromXCO2SstNcepWoa = fCO2FromXCO2SstNcepWoa;
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
	 * @param fCO2Rec 
	 * 		the recomputed fCO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2Rec(Double fCO2Rec) {
		if ( fCO2Rec == null )
			this.fCO2Rec = FP_MISSING_VALUE;
		else
			this.fCO2Rec = fCO2Rec;
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
	 * @param fCO2Source
	 * 		the method used to create the recomputed fCO2 to set;
	 * 		if null, {@link #INT_MISSING_VALUE} is assigned
	 */
	public void setfCO2Source(Integer fCO2Source) {
		if ( fCO2Source == null )
			this.fCO2Source = INT_MISSING_VALUE;
		else
			this.fCO2Source = fCO2Source;
	}

	/**
	 * @return 
	 * 		the difference between sea surface and equilibrator temperature;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDeltaT() {
		return deltaT;
	}

	/**
	 * @param deltaT
	 * 		the difference between sea surface and equilibrator temperature to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setDeltaT(Double deltaT) {
		if ( deltaT == null )
			this.deltaT = FP_MISSING_VALUE;
		else
			this.deltaT = deltaT;
	}

	/**
	 * @return 
	 * 		the region ID;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getRegionID() {
		return regionID;
	}

	/**
	 * @param regionID 
	 * 		the region ID to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setRegionID(Character regionID) {
		if ( regionID == null )
			this.regionID = CHAR_MISSING_VALUE;
		else
			this.regionID = regionID;
	}

	/**
	 * @return 
	 * 		the calculated speed of the ship;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getCalcSpeed() {
		return calcSpeed;
	}

	/**
	 * @param calcSpeed 
	 * 		the calculated speed of the ship to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setCalcSpeed(Double calcSpeed) {
		if ( calcSpeed == null )
			this.calcSpeed = FP_MISSING_VALUE;
		else
			this.calcSpeed = calcSpeed;
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
	 * @param etopo2Depth
	 * 		the ETOPO2_DEPTH depth to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setEtopo2Depth(Double etopo2Depth) {
		if ( etopo2Depth == null )
			this.etopo2Depth = FP_MISSING_VALUE;
		else
			this.etopo2Depth = etopo2Depth;
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
	 * @param gvCO2 
	 * 		the GlobablView CO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setGvCO2(Double gvCO2) {
		if ( gvCO2 == null )
			this.gvCO2 = FP_MISSING_VALUE;
		else
			this.gvCO2 = gvCO2;
	}

	/**
	 * @return 
	 * 		the distance to nearest major land mass;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDistToLand() {
		return distToLand;
	}

	/**
	 * @param distToLand 
	 * 		the distance to nearest major land mass to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setDistToLand(Double distToLand) {
		if ( distToLand == null )
			this.distToLand = FP_MISSING_VALUE;
		else
			this.distToLand = distToLand;
	}

	/**
	 * @return 
	 * 		the fractional hours since Jan 1, 1970 00:00;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDays1970() {
		return days1970;
	}

	/**
	 * @param days1970 
	 * 		the fractional hours since Jan 1, 1970 00:00 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setDays1970(Double days1970) {
		if ( days1970 == null )
			this.days1970 = FP_MISSING_VALUE;
		else
			this.days1970 = days1970;
	}

	/**
	 * @return 
	 * 		the fractional day of the year (Jan 1 00:00 == 1.0);
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDayOfYear() {
		return dayOfYear;
	}

	/**
	 * @param dayOfYear 
	 * 		the fractional day of the year (Jan 1 00:00 == 1.0) to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setDayOfYear(Double dayOfYear) {
		if ( dayOfYear == null )
			this.dayOfYear = FP_MISSING_VALUE;
		else
			this.dayOfYear = dayOfYear;
	}

	/**
	 * @return 
	 * 		the fCO2FromXCO2TEqu WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromXCO2TEquWoce() {
		return fCO2FromXCO2TEquWoce;
	}

	/**
	 * @param fCO2FromXCO2TEquWoce 
	 * 		the fCO2FromXCO2TEqu WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2TEquWoce(Character fCO2FromXCO2TEquWoce) {
		if ( fCO2FromXCO2TEquWoce == null )
			this.fCO2FromXCO2TEquWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromXCO2TEquWoce = fCO2FromXCO2TEquWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromXCO2Sst WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromXCO2SstWoce() {
		return fCO2FromXCO2SstWoce;
	}

	/**
	 * @param fCO2FromXCO2SstWoce 
	 * 		the fCO2FromXCO2Sst WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2SstWoce(Character fCO2FromXCO2SstWoce) {
		if ( fCO2FromXCO2SstWoce == null )
			this.fCO2FromXCO2SstWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromXCO2SstWoce = fCO2FromXCO2SstWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromPCO2TEqu WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromPCO2TEquWoce() {
		return fCO2FromPCO2TEquWoce;
	}

	/**
	 * @param fCO2FromPCO2TEquWoce 
	 * 		the fCO2FromPCO2TEqu WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromPCO2TEquWoce(Character fCO2FromPCO2TEquWoce) {
		if ( fCO2FromPCO2TEquWoce == null )
			this.fCO2FromPCO2TEquWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromPCO2TEquWoce = fCO2FromPCO2TEquWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromPCO2Sst WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromPCO2SstWoce() {
		return fCO2FromPCO2SstWoce;
	}

	/**
	 * @param fCO2FromPCO2SstWoce 
	 * 		the fCO2FromPCO2Sst WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromPCO2SstWoce(Character fCO2FromPCO2SstWoce) {
		if ( fCO2FromPCO2SstWoce == null )
			this.fCO2FromPCO2SstWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromPCO2SstWoce = fCO2FromPCO2SstWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromFCO2TEqu WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromFCO2TEquWoce() {
		return fCO2FromFCO2TEquWoce;
	}

	/**
	 * @param fCO2FromFCO2TEquWoce 
	 * 		the fCO2FromFCO2TEqu WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromFCO2TEquWoce(Character fCO2FromFCO2TEquWoce) {
		if ( fCO2FromFCO2TEquWoce == null )
			this.fCO2FromFCO2TEquWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromFCO2TEquWoce = fCO2FromFCO2TEquWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromFCO2Sst WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromFCO2SstWoce() {
		return fCO2FromFCO2SstWoce;
	}

	/**
	 * @param fCO2FromFCO2SstWoce 
	 * 		the fCO2FromFCO2Sst WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromFCO2SstWoce(Character fCO2FromFCO2SstWoce) {
		if ( fCO2FromFCO2SstWoce == null )
			this.fCO2FromFCO2SstWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromFCO2SstWoce = fCO2FromFCO2SstWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromPCO2TEquNcep WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromPCO2TEquNcepWoce() {
		return fCO2FromPCO2TEquNcepWoce;
	}

	/**
	 * @param fCO2FromPCO2TEquNcepWoce 
	 * 		the fCO2FromPCO2TEquNcep WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromPCO2TEquNcepWoce(Character fCO2FromPCO2TEquNcepWoce) {
		if ( fCO2FromPCO2TEquNcepWoce == null )
			this.fCO2FromPCO2TEquNcepWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromPCO2TEquNcepWoce = fCO2FromPCO2TEquNcepWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromPCO2SstNcep WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromPCO2SstNcepWoce() {
		return fCO2FromPCO2SstNcepWoce;
	}

	/**
	 * @param fCO2FromPCO2SstNcepWoce 
	 * 		the fCO2FromPCO2SstNcep WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromPCO2SstNcepWoce(Character fCO2FromPCO2SstNcepWoce) {
		if ( fCO2FromPCO2SstNcepWoce == null )
			this.fCO2FromPCO2SstNcepWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromPCO2SstNcepWoce = fCO2FromPCO2SstNcepWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromXCO2TEquWoa WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromXCO2TEquWoaWoce() {
		return fCO2FromXCO2TEquWoaWoce;
	}

	/**
	 * @param fCO2FromXCO2TEquWoaWoce 
	 * 		the fCO2FromXCO2TEquWoa WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2TEquWoaWoce(Character fCO2FromXCO2TEquWoaWoce) {
		if ( fCO2FromXCO2TEquWoaWoce == null )
			this.fCO2FromXCO2TEquWoaWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromXCO2TEquWoaWoce = fCO2FromXCO2TEquWoaWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromXCO2SstWoa WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromXCO2SstWoaWoce() {
		return fCO2FromXCO2SstWoaWoce;
	}

	/**
	 * @param fCO2FromXCO2SstWoaWoce 
	 * 		the fCO2FromXCO2SstWoa WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2SstWoaWoce(Character fCO2FromXCO2SstWoaWoce) {
		if ( fCO2FromXCO2SstWoaWoce == null )
			this.fCO2FromXCO2SstWoaWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromXCO2SstWoaWoce = fCO2FromXCO2SstWoaWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromXCO2TEquNcep WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromXCO2TEquNcepWoce() {
		return fCO2FromXCO2TEquNcepWoce;
	}

	/**
	 * @param fCO2FromXCO2TEquNcepWoce 
	 * 		the fCO2FromXCO2TEquNcep WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2TEquNcepWoce(Character fCO2FromXCO2TEquNcepWoce) {
		if ( fCO2FromXCO2TEquNcepWoce == null )
			this.fCO2FromXCO2TEquNcepWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromXCO2TEquNcepWoce = fCO2FromXCO2TEquNcepWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromXCO2SstNcep WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromXCO2SstNcepWoce() {
		return fCO2FromXCO2SstNcepWoce;
	}

	/**
	 * @param fCO2FromXCO2SstNcepWoce 
	 * 		the fCO2FromXCO2SstNcep WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2SstNcepWoce(Character fCO2FromXCO2SstNcepWoce) {
		if ( fCO2FromXCO2SstNcepWoce == null )
			this.fCO2FromXCO2SstNcepWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromXCO2SstNcepWoce = fCO2FromXCO2SstNcepWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromXCO2TEquNcepWoa WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromXCO2TEquNcepWoaWoce() {
		return fCO2FromXCO2TEquNcepWoaWoce;
	}

	/**
	 * @param fCO2FromXCO2TEquNcepWoaWoce 
	 * 		the fCO2FromXCO2TEquNcepWoa WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2TEquNcepWoaWoce(Character fCO2FromXCO2TEquNcepWoaWoce) {
		if ( fCO2FromXCO2TEquNcepWoaWoce == null )
			this.fCO2FromXCO2TEquNcepWoaWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromXCO2TEquNcepWoaWoce = fCO2FromXCO2TEquNcepWoaWoce;
	}

	/**
	 * @return 
	 * 		the fCO2FromXCO2SstNcepWoa WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2FromXCO2SstNcepWoaWoce() {
		return fCO2FromXCO2SstNcepWoaWoce;
	}

	/**
	 * @param fCO2FromXCO2SstNcepWoaWoce 
	 * 		the fCO2FromXCO2SstNcepWoa WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2FromXCO2SstNcepWoaWoce(Character fCO2FromXCO2SstNcepWoaWoce) {
		if ( fCO2FromXCO2SstNcepWoaWoce == null )
			this.fCO2FromXCO2SstNcepWoaWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2FromXCO2SstNcepWoaWoce = fCO2FromXCO2SstNcepWoaWoce;
	}

	/**
	 * @return 
	 * 		the fCO2Rec WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getfCO2RecWoce() {
		return fCO2RecWoce;
	}

	/**
	 * @param fCO2RecWoce 
	 * 		the fCO2Rec WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setfCO2RecWoce(Character fCO2RecWoce) {
		if ( fCO2RecWoce == null )
			this.fCO2RecWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2RecWoce = fCO2RecWoce;
	}

	/**
	 * @return 
	 * 		the deltaT WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getDeltaTWoce() {
		return deltaTWoce;
	}

	/**
	 * @param deltaTWoce 
	 * 		the deltaT WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setDeltaTWoce(Character deltaTWoce) {
		if ( deltaTWoce == null )
			this.deltaTWoce = CHAR_MISSING_VALUE;
		else
			this.deltaTWoce = deltaTWoce;
	}

	/**
	 * @return 
	 * 		the calculated ship speed WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getCalcSpeedWoce() {
		return calcSpeedWoce;
	}

	/**
	 * @param calcSpeedWoce 
	 * 		the calculated ship speed WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setCalcSpeedWoce(Character calcSpeedWoce) {
		if ( calcSpeedWoce == null )
			this.calcSpeedWoce = CHAR_MISSING_VALUE;
		else
			this.calcSpeedWoce = calcSpeedWoce;
	}

	@Override 
	public int hashCode() {
		// Do not use floating-point fields since they do not 
		// have to be exactly the same for equals to return true.
		final int prime = 37;
		int result = 0;
		// Do all the WOCE flags first in case of overflow
		result = result * prime + geopositionWoce.hashCode();
		result = result * prime + sampleDepthWoce.hashCode();
		result = result * prime + salinityWoce.hashCode();
		result = result * prime + tEquWoce.hashCode();
		result = result * prime + sstWoce.hashCode();
		result = result * prime + pEquWoce.hashCode();
		result = result * prime + slpWoce.hashCode();

		result = result * prime + xCO2WaterTEquWoce.hashCode();
		result = result * prime + xCO2WaterSstWoce.hashCode();
		result = result * prime + pCO2WaterTEquWoce.hashCode();
		result = result * prime + pCO2WaterSstWoce.hashCode();
		result = result * prime + fCO2WaterTEquWoce.hashCode();
		result = result * prime + fCO2WaterSstWoce.hashCode();

		result = result * prime + xCO2AtmWoce.hashCode();
		result = result * prime + pCO2AtmWoce.hashCode();
		result = result * prime + fCO2AtmWoce.hashCode();
		result = result * prime + deltaXCO2Woce.hashCode();
		result = result * prime + deltaPCO2Woce.hashCode();
		result = result * prime + deltaFCO2Woce.hashCode();

		result = result * prime + relativeHumidityWoce.hashCode();
		result = result * prime + specificHumidityWoce.hashCode();
		result = result * prime + shipSpeedWoce.hashCode();
		result = result * prime + shipDirectionWoce.hashCode();
		result = result * prime + windSpeedTrueWoce.hashCode();
		result = result * prime + windSpeedRelativeWoce.hashCode();
		result = result * prime + windDirectionTrueWoce.hashCode();
		result = result * prime + windDirectionRelativeWoce.hashCode();

		result = result * prime + fCO2FromXCO2TEquWoce.hashCode();
		result = result * prime + fCO2FromXCO2SstWoce.hashCode();
		result = result * prime + fCO2FromPCO2TEquWoce.hashCode();
		result = result * prime + fCO2FromPCO2SstWoce.hashCode();
		result = result * prime + fCO2FromFCO2TEquWoce.hashCode();
		result = result * prime + fCO2FromFCO2SstWoce.hashCode();
		result = result * prime + fCO2FromPCO2TEquNcepWoce.hashCode();
		result = result * prime + fCO2FromPCO2SstNcepWoce.hashCode();
		result = result * prime + fCO2FromXCO2TEquWoaWoce.hashCode();
		result = result * prime + fCO2FromXCO2SstWoaWoce.hashCode();
		result = result * prime + fCO2FromXCO2TEquNcepWoce.hashCode();
		result = result * prime + fCO2FromXCO2SstNcepWoce.hashCode();
		result = result * prime + fCO2FromXCO2TEquNcepWoaWoce.hashCode();
		result = result * prime + fCO2FromXCO2SstNcepWoaWoce.hashCode();

		result = result * prime + fCO2RecWoce.hashCode();
		result = result * prime + deltaTWoce.hashCode();
		result = result * prime + calcSpeedWoce.hashCode();

		result = result * prime + regionID.hashCode();
		result = result * prime + year.hashCode();
		result = result * prime + month.hashCode();
		result = result * prime + day.hashCode();
		result = result * prime + hour.hashCode();
		result = result * prime + minute.hashCode();
		result = result * prime + fCO2Source.hashCode();
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

		// Character comparisons
		if ( ! fCO2Source.equals(other.fCO2Source) ) 
			return false;
		if ( ! regionID.equals(other.regionID) )
			return false;

		if ( ! geopositionWoce.equals(other.geopositionWoce) ) 
			return false;
		if ( ! sampleDepthWoce.equals(other.sampleDepthWoce) ) 
			return false;
		if ( ! salinityWoce.equals(other.salinityWoce) ) 
			return false;
		if ( ! tEquWoce.equals(other.tEquWoce) ) 
			return false;
		if ( ! sstWoce.equals(other.sstWoce) ) 
			return false;
		if ( ! pEquWoce.equals(other.pEquWoce) ) 
			return false;
		if ( ! slpWoce.equals(other.slpWoce) ) 
			return false;

		if ( ! xCO2WaterTEquWoce.equals(other.xCO2WaterTEquWoce) ) 
			return false;
		if ( ! xCO2WaterSstWoce.equals(other.xCO2WaterSstWoce) ) 
			return false;
		if ( ! pCO2WaterTEquWoce.equals(other.pCO2WaterTEquWoce) ) 
			return false;
		if ( ! pCO2WaterSstWoce.equals(other.pCO2WaterSstWoce) ) 
			return false;
		if ( ! fCO2WaterTEquWoce.equals(other.fCO2WaterTEquWoce) ) 
			return false;
		if ( ! fCO2WaterSstWoce.equals(other.fCO2WaterSstWoce) ) 
			return false;

		if ( ! xCO2AtmWoce.equals(other.xCO2AtmWoce) ) 
			return false;
		if ( ! pCO2AtmWoce.equals(other.pCO2AtmWoce) ) 
			return false;
		if ( ! fCO2AtmWoce.equals(other.fCO2AtmWoce) ) 
			return false;
		if ( ! deltaXCO2Woce.equals(other.deltaXCO2Woce) ) 
			return false;
		if ( ! deltaPCO2Woce.equals(other.deltaPCO2Woce) ) 
			return false;
		if ( ! deltaFCO2Woce.equals(other.deltaFCO2Woce) ) 
			return false;

		if ( ! relativeHumidityWoce.equals(other.relativeHumidityWoce) ) 
			return false;
		if ( ! specificHumidityWoce.equals(other.specificHumidityWoce) ) 
			return false;
		if ( ! shipSpeedWoce.equals(other.shipSpeedWoce) ) 
			return false;
		if ( ! shipDirectionWoce.equals(other.shipDirectionWoce) ) 
			return false;
		if ( ! windSpeedTrueWoce.equals(other.windSpeedTrueWoce) ) 
			return false;
		if ( ! windSpeedRelativeWoce.equals(other.windSpeedRelativeWoce) ) 
			return false;
		if ( ! windDirectionTrueWoce.equals(other.windDirectionTrueWoce) ) 
			return false;
		if ( ! windDirectionRelativeWoce.equals(other.windDirectionRelativeWoce) ) 
			return false;

		if ( ! fCO2FromXCO2TEquWoce.equals(other.fCO2FromXCO2TEquWoce) ) 
			return false;
		if ( ! fCO2FromXCO2SstWoce.equals(other.fCO2FromXCO2SstWoce) ) 
			return false;
		if ( ! fCO2FromPCO2TEquWoce.equals(other.fCO2FromPCO2TEquWoce) ) 
			return false;
		if ( ! fCO2FromPCO2SstWoce.equals(other.fCO2FromPCO2SstWoce) ) 
			return false;
		if ( ! fCO2FromFCO2TEquWoce.equals(other.fCO2FromFCO2TEquWoce) ) 
			return false;
		if ( ! fCO2FromFCO2SstWoce.equals(other.fCO2FromFCO2SstWoce) ) 
			return false;
		if ( ! fCO2FromPCO2TEquNcepWoce.equals(other.fCO2FromPCO2TEquNcepWoce) ) 
			return false;
		if ( ! fCO2FromPCO2SstNcepWoce.equals(other.fCO2FromPCO2SstNcepWoce) ) 
			return false;
		if ( ! fCO2FromXCO2TEquWoaWoce.equals(other.fCO2FromXCO2TEquWoaWoce) ) 
			return false;
		if ( ! fCO2FromXCO2SstWoaWoce.equals(other.fCO2FromXCO2SstWoaWoce) ) 
			return false;
		if ( ! fCO2FromXCO2TEquNcepWoce.equals(other.fCO2FromXCO2TEquNcepWoce) ) 
			return false;
		if ( ! fCO2FromXCO2SstNcepWoce.equals(other.fCO2FromXCO2SstNcepWoce) ) 
			return false;
		if ( ! fCO2FromXCO2TEquNcepWoaWoce.equals(other.fCO2FromXCO2TEquNcepWoaWoce) ) 
			return false;
		if ( ! fCO2FromXCO2SstNcepWoaWoce.equals(other.fCO2FromXCO2SstNcepWoaWoce) ) 
			return false;

		if ( ! fCO2RecWoce.equals(other.fCO2RecWoce) ) 
			return false;
		if ( ! deltaTWoce.equals(other.deltaTWoce) ) 
			return false;
		if ( ! calcSpeedWoce.equals(other.calcSpeedWoce) ) 
			return false;

		// Match seconds not given (FP_MISSING_VALUE) with zero seconds
		if ( ! DashboardUtils.closeTo(second, other.second, 0.0, 1.0E-3) )
			if ( ! (second.equals(FP_MISSING_VALUE) && DashboardUtils.closeTo(0.0, other.second, 0.0, 1.0E-3)) )
				if ( ! (other.second.equals(FP_MISSING_VALUE) && DashboardUtils.closeTo(second, 0.0, 0.0, 1.0E-3)) )
					return false;

		// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
		if ( ! DashboardUtils.closeTo(this.longitude, other.longitude, 0.0, MAX_ABSOLUTE_ERROR) )
			if ( ! DashboardUtils.closeTo(this.longitude + 360.0, other.longitude, 0.0, MAX_ABSOLUTE_ERROR) )
				if ( ! DashboardUtils.closeTo(this.longitude, other.longitude + 360.0, 0.0, MAX_ABSOLUTE_ERROR) )
					return false;

		// rest of the Double comparisons
		if ( ! DashboardUtils.closeTo(latitude, other.latitude, 0.0, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(sampleDepth, other.sampleDepth, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(salinity, other.salinity, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(sst, other.sst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(tEqu, other.tEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(slp, other.slp, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pEqu, other.pEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;

		if ( ! DashboardUtils.closeTo(xCO2WaterSst, other.xCO2WaterSst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(xCO2WaterTEqu, other.xCO2WaterTEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pCO2WaterSst, other.pCO2WaterSst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pCO2WaterTEqu, other.pCO2WaterTEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2WaterSst, other.fCO2WaterSst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2WaterTEqu, other.fCO2WaterTEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;

		if ( ! DashboardUtils.closeTo(xCO2Atm, other.xCO2Atm, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pCO2Atm, other.pCO2Atm, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2Atm, other.fCO2Atm, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(deltaXCO2, other.deltaXCO2, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(deltaPCO2, other.deltaPCO2, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(deltaFCO2, other.deltaFCO2, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
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
		if ( ! DashboardUtils.closeTo(days1970, other.days1970, 0.0, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(dayOfYear, other.dayOfYear, 0.0, MAX_ABSOLUTE_ERROR) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "SocatCruiseData[ year=" + year.toString() +
				",\n    month=" + month.toString() +
				",\n    day=" + day.toString() +
				",\n    hour=" + hour.toString() +
				",\n    minute=" + minute.toString() +
				",\n    second=" + second.toString() +

				",\n    longitude=" + longitude.toString() +
				",\n    latitude=" + latitude.toString() +
				",\n    sampleDepth=" + sampleDepth.toString() +
				",\n    salinity=" + salinity.toString() +
				",\n    sst=" + sst.toString() +
				",\n    tEqu=" + tEqu.toString() +
				",\n    slp=" + slp.toString() +
				",\n    pEqu=" + pEqu.toString() +

				",\n    xCO2WaterSst=" + xCO2WaterSst.toString() +
				",\n    xCO2WaterTEqu=" + xCO2WaterTEqu.toString() +
				",\n    pCO2WaterSst=" + pCO2WaterSst.toString() +
				",\n    pCO2WaterTEqu=" + pCO2WaterTEqu.toString() +
				",\n    fCO2WaterSst=" + fCO2WaterSst.toString() +
				",\n    fCO2WaterTEqu=" + fCO2WaterTEqu.toString() +

				",\n    xCO2Atm=" + xCO2Atm.toString() +
				",\n    pCO2Atm=" + pCO2Atm.toString() +
				",\n    fCO2Atm=" + fCO2Atm.toString() +
				",\n    deltaXCO2=" + deltaXCO2.toString() +
				",\n    deltaPCO2=" + deltaPCO2.toString() +
				",\n    deltaFCO2=" + deltaFCO2.toString() +

				",\n    relativeHumidity=" + relativeHumidity.toString() +
				",\n    specificHumidity=" + specificHumidity.toString() +
				",\n    shipSpeed=" + shipSpeed.toString() +
				",\n    shipDirection=" + shipDirection.toString() +
				",\n    windSpeedTrue=" + windSpeedTrue.toString() +
				",\n    windSpeedRelative=" + windSpeedRelative.toString() +
				",\n    windDirectionTrue=" + windDirectionTrue.toString() +
				",\n    windDirectionRelative=" + windDirectionRelative.toString() +

				",\n    geopositionWoce=" + geopositionWoce.toString() +
				",\n    sampleDepthWoce=" + sampleDepthWoce.toString() +
				",\n    salinityWoce=" + salinityWoce.toString() +
				",\n    tEquWoce=" + tEquWoce.toString() +
				",\n    sstWoce=" + sstWoce.toString() +
				",\n    pEquWoce=" + pEquWoce.toString() +
				",\n    slpWoce=" + slpWoce.toString() +

				",\n    xCO2WaterTEquWoce=" + xCO2WaterTEquWoce.toString() +
				",\n    xCO2WaterSstWoce=" + xCO2WaterSstWoce.toString() +
				",\n    pCO2WaterTEquWoce=" + pCO2WaterTEquWoce.toString() +
				",\n    pCO2WaterSstWoce=" + pCO2WaterSstWoce.toString() +
				",\n    fCO2WaterTEquWoce=" + fCO2WaterTEquWoce.toString() +
				",\n    fCO2WaterSstWoce=" + fCO2WaterSstWoce.toString() +

				",\n    xCO2AtmWoce=" + xCO2AtmWoce.toString() +
				",\n    pCO2AtmWoce=" + pCO2AtmWoce.toString() +
				",\n    fCO2AtmWoce=" + fCO2AtmWoce.toString() +
				",\n    deltaXCO2Woce=" + deltaXCO2Woce.toString() +
				",\n    deltaPCO2Woce=" + deltaPCO2Woce.toString() +
				",\n    deltaFCO2Woce=" + deltaFCO2Woce.toString() +

				",\n    relativeHumidityWoce=" + relativeHumidityWoce.toString() +
				",\n    specificHumidityWoce=" + specificHumidityWoce.toString() +
				",\n    shipSpeedWoce=" + shipSpeedWoce.toString() +
				",\n    shipDirectionWoce=" + shipDirectionWoce.toString() +
				",\n    windSpeedTrueWoce=" + windSpeedTrueWoce.toString() +
				",\n    windSpeedRelativeWoce=" + windSpeedRelativeWoce.toString() +
				",\n    windDirectionTrueWoce=" + windDirectionTrueWoce.toString() +
				",\n    windDirectionRelativeWoce=" + windDirectionRelativeWoce.toString() +

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
				",\n    days1970=" + days1970.toString() +
				",\n    dayOfYear=" + dayOfYear.toString() +

				",\n    fCO2FromXCO2TEquWoce=" + fCO2FromXCO2TEquWoce.toString() +
				",\n    fCO2FromXCO2SstWoce=" + fCO2FromXCO2SstWoce.toString() +
				",\n    fCO2FromPCO2TEquWoce=" + fCO2FromPCO2TEquWoce.toString() +
				",\n    fCO2FromPCO2SstWoce=" + fCO2FromPCO2SstWoce.toString() +
				",\n    fCO2FromFCO2TEquWoce=" + fCO2FromFCO2TEquWoce.toString() +
				",\n    fCO2FromFCO2SstWoce=" + fCO2FromFCO2SstWoce.toString() +
				",\n    fCO2FromPCO2TEquNcepWoce=" + fCO2FromPCO2TEquNcepWoce.toString() +
				",\n    fCO2FromPCO2SstNcepWoce=" + fCO2FromPCO2SstNcepWoce.toString() +
				",\n    fCO2FromXCO2TEquWoaWoce=" + fCO2FromXCO2TEquWoaWoce.toString() +
				",\n    fCO2FromXCO2SstWoaWoce=" + fCO2FromXCO2SstWoaWoce.toString() +
				",\n    fCO2FromXCO2TEquNcepWoce=" + fCO2FromXCO2TEquNcepWoce.toString() +
				",\n    fCO2FromXCO2SstNcepWoce=" + fCO2FromXCO2SstNcepWoce.toString() +
				",\n    fCO2FromXCO2TEquNcepWoaWoce=" + fCO2FromXCO2TEquNcepWoaWoce.toString() +
				",\n    fCO2FromXCO2SstNcepWoaWoce=" + fCO2FromXCO2SstNcepWoaWoce.toString() +

				",\n    fCO2RecWoce=" + fCO2RecWoce.toString() +
				",\n    deltaTWoce=" + deltaTWoce.toString() +
				",\n    calcSpeedWoce=" + calcSpeedWoce.toString() +
				" ]";
	}

}
