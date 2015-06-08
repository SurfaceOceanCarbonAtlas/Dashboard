/**
 */
package gov.noaa.pmel.socat.dashboard.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class for working with data values of interest, both PI-provided
 * values and computed values, from a SOCAT cruise data measurement.
 * Note that WOCE flags are ignored in the hashCode and equals methods.
 * 
 * @author Karl Smith
 */
public class SocatCruiseData implements Serializable, IsSerializable {

	private static final long serialVersionUID = 1918665099211273971L;

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

	// Sequence number (starts with one) of this data point in the data set
	Integer rowNum;

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
	// Atmospheric temperature
	Double tAtm;
	// Equilibrator pressure
	Double pEqu;
	// Atmospheric pressure / sea level pressure
	Double slp;

	// Eight possible water CO2 measurements reported
	Double xCO2WaterTEquDry;
	Double xCO2WaterSstDry;
	Double xCO2WaterTEquWet;
	Double xCO2WaterSstWet;
	Double pCO2WaterTEquWet;
	Double pCO2WaterSstWet;
	Double fCO2WaterTEquWet;
	Double fCO2WaterSstWet;

	// Six possible air CO2 measurements reported
	Double xCO2AtmDryActual;
	Double xCO2AtmDryInterp;
	Double pCO2AtmWetActual;
	Double pCO2AtmWetInterp;
	Double fCO2AtmWetActual;
	Double fCO2AtmWetInterp;

	// CO2Water - (interpolated) CO2Atm; only user-provided
	Double deltaXCO2;
	Double deltaPCO2;
	Double deltaFCO2;

	// mole fraction water (mmol/mol) in equilibrator gas sample
	Double xH2OEqu;
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

	// WOCE flags on water CO2 values and air CO2 values
	Character woceCO2Water;
	Character woceCO2Atm;

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
	// days of the year; Jan 1 00:00 == 1.0
	Double dayOfYear;

	/**
	 * Generates an empty SOCAT data record
	 */
	public SocatCruiseData() {
		rowNum = INT_MISSING_VALUE;

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
		tEqu = FP_MISSING_VALUE;
		sst = FP_MISSING_VALUE;
		tAtm = FP_MISSING_VALUE;
		pEqu = FP_MISSING_VALUE;
		slp = FP_MISSING_VALUE;

		xCO2WaterSstDry = FP_MISSING_VALUE;
		xCO2WaterTEquDry = FP_MISSING_VALUE;
		xCO2WaterSstWet = FP_MISSING_VALUE;
		xCO2WaterTEquWet = FP_MISSING_VALUE;
		fCO2WaterSstWet = FP_MISSING_VALUE;
		fCO2WaterTEquWet = FP_MISSING_VALUE;
		pCO2WaterSstWet = FP_MISSING_VALUE;
		pCO2WaterTEquWet = FP_MISSING_VALUE;

		xCO2AtmDryActual = FP_MISSING_VALUE;
		xCO2AtmDryInterp = FP_MISSING_VALUE;
		pCO2AtmWetActual = FP_MISSING_VALUE;
		pCO2AtmWetInterp = FP_MISSING_VALUE;
		fCO2AtmWetActual = FP_MISSING_VALUE;
		fCO2AtmWetInterp = FP_MISSING_VALUE;

		deltaXCO2 = FP_MISSING_VALUE;
		deltaPCO2 = FP_MISSING_VALUE;
		deltaFCO2 = FP_MISSING_VALUE;

		xH2OEqu = FP_MISSING_VALUE;
		relativeHumidity = FP_MISSING_VALUE;
		specificHumidity = FP_MISSING_VALUE;
		shipSpeed = FP_MISSING_VALUE; 
		shipDirection = FP_MISSING_VALUE; 
		windSpeedTrue = FP_MISSING_VALUE;
		windSpeedRelative = FP_MISSING_VALUE;
		windDirectionTrue = FP_MISSING_VALUE;
		windDirectionRelative = FP_MISSING_VALUE;

		woceCO2Water = SocatWoceEvent.WOCE_NOT_CHECKED;
		woceCO2Atm = SocatWoceEvent.WOCE_NOT_CHECKED;

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
		regionID = DataLocation.GLOBAL_REGION_ID;
		calcSpeed = FP_MISSING_VALUE;
		etopo2Depth = FP_MISSING_VALUE;
		gvCO2 = FP_MISSING_VALUE;
		distToLand = FP_MISSING_VALUE;
		dayOfYear = FP_MISSING_VALUE;
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
	 * 		sequence number (starting with one) of this data point in the 
	 * 		data set; never null but could be {@link #INT_MISSING_VALUE} 
	 * 		if not assigned
	 */
	public Integer getRowNum() {
		return rowNum;
	}

	/**
	 * @param rowNum 
	 * 		sequence number (starting with one) of this data point in the 
	 * 		data set to assign; if null, {@link #INT_MISSING_VALUE} is assigned
	 */
	public void setRowNum(Integer rowNum) {
		if ( rowNum == null )
			this.rowNum = INT_MISSING_VALUE;
		else
			this.rowNum = rowNum;
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
	 * 		the atmospheric temperature;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double gettAtm() {
		return tAtm;
	}

	/**
	 * @param tAtm 
	 * 		the atmospheric temperature to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void settAtm(Double tAtm) {
		if ( tAtm == null )
			this.tAtm = FP_MISSING_VALUE;
		else
			this.tAtm = tAtm;
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
	 * 		the xCO2WaterTEquDry;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getxCO2WaterTEquDry() {
		return xCO2WaterTEquDry;
	}

	/**
	 * @param xCO2WaterTEquDry 
	 * 		the xCO2WaterTEquDry to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setxCO2WaterTEquDry(Double xCO2WaterTEquDry) {
		if ( xCO2WaterTEquDry == null )
			this.xCO2WaterTEquDry = FP_MISSING_VALUE;
		else
			this.xCO2WaterTEquDry = xCO2WaterTEquDry;
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
	 * @param xCO2WaterSstDry 
	 * 		the xCO2WaterSstDry to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setxCO2WaterSstDry(Double xCO2WaterSstDry) {
		if ( xCO2WaterSstDry == null )
			this.xCO2WaterSstDry = FP_MISSING_VALUE;
		else
			this.xCO2WaterSstDry = xCO2WaterSstDry;
	}

	/**
	 * @return 
	 * 		the xCO2WaterTEquWet;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getxCO2WaterTEquWet() {
		return xCO2WaterTEquWet;
	}

	/**
	 * @param xCO2WaterTEquWet 
	 * 		the xCO2WaterTEquWet to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setxCO2WaterTEquWet(Double xCO2WaterTEquWet) {
		if ( xCO2WaterTEquWet == null )
			this.xCO2WaterTEquWet = FP_MISSING_VALUE;
		else
			this.xCO2WaterTEquWet = xCO2WaterTEquWet;
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
	 * @param xCO2WaterSstWet 
	 * 		the xCO2WaterSstWet to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setxCO2WaterSstWet(Double xCO2WaterSstWet) {
		if ( xCO2WaterSstWet == null )
			this.xCO2WaterSstWet = FP_MISSING_VALUE;
		else
			this.xCO2WaterSstWet = xCO2WaterSstWet;
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
	 * @param pCO2WaterTEquWet 
	 * 		the pCO2WaterTEquWet to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setpCO2WaterTEquWet(Double pCO2WaterTEquWet) {
		if ( pCO2WaterTEquWet == null )
			this.pCO2WaterTEquWet = FP_MISSING_VALUE;
		else
			this.pCO2WaterTEquWet = pCO2WaterTEquWet;
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
	 * @param pCO2WaterSstWet 
	 * 		the pCO2WaterSstWet to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setpCO2WaterSstWet(Double pCO2WaterSstWet) {
		if ( pCO2WaterSstWet == null )
			this.pCO2WaterSstWet = FP_MISSING_VALUE;
		else
			this.pCO2WaterSstWet = pCO2WaterSstWet;
	}

	/**
	 * @return 
	 * 		the fCO2WaterTEquWet;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2WaterTEquWet() {
		return fCO2WaterTEquWet;
	}

	/**
	 * @param fCO2WaterTEquWet 
	 * 		the fCO2WaterTEquWet to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2WaterTEquWet(Double fCO2WaterTEquWet) {
		if ( fCO2WaterTEquWet == null )
			this.fCO2WaterTEquWet = FP_MISSING_VALUE;
		else
			this.fCO2WaterTEquWet = fCO2WaterTEquWet;
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
	 * @param fCO2WaterSstWet 
	 * 		the fCO2WaterSstWet to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2WaterSstWet(Double fCO2WaterSstWet) {
		if ( fCO2WaterSstWet == null )
			this.fCO2WaterSstWet = FP_MISSING_VALUE;
		else
			this.fCO2WaterSstWet = fCO2WaterSstWet;
	}

	/**
	 * @return 
	 * 		the xCO2AtmDryActual;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getxCO2AtmDryActual() {
		return xCO2AtmDryActual;
	}

	/**
	 * @param xCO2AtmDryActual 
	 * 		the xCO2AtmDryActual to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setxCO2AtmDryActual(Double xCO2AtmDryActual) {
		if ( xCO2AtmDryActual == null )
			this.xCO2AtmDryActual = FP_MISSING_VALUE;
		else
			this.xCO2AtmDryActual = xCO2AtmDryActual;
	}

	/**
	 * @return 
	 * 		the xCO2AtmDryInterp;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getxCO2AtmDryInterp() {
		return xCO2AtmDryInterp;
	}

	/**
	 * @param xCO2AtmDryInterp 
	 * 		the xCO2AtmDryInterp to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setxCO2AtmDryInterp(Double xCO2AtmDryInterp) {
		if ( xCO2AtmDryInterp == null )
			this.xCO2AtmDryInterp = FP_MISSING_VALUE;
		else
			this.xCO2AtmDryInterp = xCO2AtmDryInterp;
	}

	/**
	 * @return 
	 * 		the pCO2AtmWetActual;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getpCO2AtmWetActual() {
		return pCO2AtmWetActual;
	}

	/**
	 * @param pCO2AtmWetActual 
	 * 		the pCO2AtmWetActual to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setpCO2AtmWetActual(Double pCO2AtmWetActual) {
		if ( pCO2AtmWetActual == null )
			this.pCO2AtmWetActual = FP_MISSING_VALUE;
		else
			this.pCO2AtmWetActual = pCO2AtmWetActual;
	}

	/**
	 * @return 
	 * 		the pCO2AtmWetInterp;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getpCO2AtmWetInterp() {
		return pCO2AtmWetInterp;
	}

	/**
	 * @param pCO2AtmWetInterp 
	 * 		the pCO2AtmWetInterp to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setpCO2AtmWetInterp(Double pCO2AtmWetInterp) {
		if ( pCO2AtmWetInterp == null )
			this.pCO2AtmWetInterp = FP_MISSING_VALUE;
		else
			this.pCO2AtmWetInterp = pCO2AtmWetInterp;
	}

	/**
	 * @return 
	 * 		the fCO2AtmWetActual;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2AtmWetActual() {
		return fCO2AtmWetActual;
	}

	/**
	 * @param fCO2AtmWetActual 
	 * 		the fCO2AtmWetActual to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2AtmWetActual(Double fCO2AtmWetActual) {
		if ( fCO2AtmWetActual == null )
			this.fCO2AtmWetActual = FP_MISSING_VALUE;
		else
			this.fCO2AtmWetActual = fCO2AtmWetActual;
	}

	/**
	 * @return 
	 * 		the fCO2AtmWetInterp;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getfCO2AtmWetInterp() {
		return fCO2AtmWetInterp;
	}

	/**
	 * @param fCO2AtmWetInterp 
	 * 		the fCO2AtmWetInterp to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setfCO2AtmWetInterp(Double fCO2AtmWetInterp) {
		if ( fCO2AtmWetInterp == null )
			this.fCO2AtmWetInterp = FP_MISSING_VALUE;
		else
			this.fCO2AtmWetInterp = fCO2AtmWetInterp;
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
	 * 		the xH2OEqu;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getxH2OEqu() {
		return xH2OEqu;
	}

	/**
	 * @param xH2OEqu 
	 * 		the xH2OEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setxH2OEqu(Double xH2OEqu) {
		if ( xH2OEqu == null )
			this.xH2OEqu = FP_MISSING_VALUE;
		else
			this.xH2OEqu = xH2OEqu;
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
	 * 		the woceCO2Water;
	 * 		never null but could be {@link SocatWoceEvent#WOCE_NOT_CHECKED} if not assigned
	 */
	public Character getWoceCO2Water() {
		return woceCO2Water;
	}

	/**
	 * @param woceCO2Water 
	 * 		the woceCO2Water to set;
	 * 		if null, {@link SocatWoceEvent#WOCE_NOT_CHECKED} is assigned
	 */
	public void setWoceCO2Water(Character woceCO2Water) {
		if ( woceCO2Water == null )
			this.woceCO2Water = SocatWoceEvent.WOCE_NOT_CHECKED;
		else
			this.woceCO2Water = woceCO2Water;
	}

	/**
	 * @return 
	 * 		the woceCO2Atm;
	 * 		never null but could be {@link SocatWoceEvent#WOCE_NOT_CHECKED} if not assigned
	 */
	public Character getWoceCO2Atm() {
		return woceCO2Atm;
	}

	/**
	 * @param woceCO2Atm 
	 * 		the woceCO2Atm to set;
	 * 		if null, {@link #SocatWoceEvent#WOCE_NOT_CHECKED} is assigned
	 */
	public void setWoceCO2Atm(Character woceCO2Atm) {
		if ( woceCO2Atm == null )
			this.woceCO2Atm = SocatWoceEvent.WOCE_NOT_CHECKED;
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
	 * 		never null but could be {@link DataLocation#GLOBAL_REGION_ID} if not assigned
	 */
	public Character getRegionID() {
		return regionID;
	}

	/**
	 * @param regionID 
	 * 		the region ID to set;
	 * 		if null, {@link DataLocation#GLOBAL_REGION_ID} is assigned
	 */
	public void setRegionID(Character regionID) {
		if ( regionID == null )
			this.regionID = DataLocation.GLOBAL_REGION_ID;
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
