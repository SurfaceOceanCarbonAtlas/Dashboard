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
 * 
 * @author Karl Smith
 */
public class SocatCruiseData implements Serializable, IsSerializable {

	private static final long serialVersionUID = -9113258429689025721L;

	private static final double MAX_RELATIVE_ERROR = 1.0E-6;
	private static final double MAX_ABSOLUTE_ERROR = 1.0E-4;

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
	Double sal;
	// Sea surface temperature
	Double sst;
	// Equilibrator temperature
	Double tEqu;
	// Atmospheric pressure / sea level pressure
	Double slp;
	// Equilibrator pressure
	Double pEqu;
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
	Double xCO2Air;
	Double pCO2Air;
	Double fCO2Air;
	// Ship speed in knots
	Double shipSpeed; 
	// Ship direction in degrees clockwise from N
	Double shipDirection; 
	// Wind speed in m/s
	Double windSpeedTrue;
	Double windSpeedRelative;
	// Wind direction is degrees clockwise from N
	Double windDirectionTrue;
	Double windDirectionRelative;
	// WOCE flags
	Character timestampWoce;
	Character longitudeWoce;
	Character latitudeWoce;
	Character depthWoce;
	Character salinityWoce;
	Character tEquWoce;
	Character sstWoce;
	Character pEquWoce;
	Character slpWoce;
	Character xCO2WaterEquWoce;
	Character xCO2WaterSSTWoce;
	Character pCO2WaterEquWoce;
	Character pCO2WaterSSTWoce;
	Character fCO2WaterEquWoce;
	Character fCO2WaterSSTWoce;
	Character xCO2AirWoce;
	Character pCO2AirWoce;
	Character fCO2AirWoce;

	// The following are provided by Ferret calculations using the above data

	// Difference between air and water CO2 values
	Double deltaXCO2;
	Double deltaPCO2;
	Double deltaFCO2;
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
    // "Best" recomputed fCO2 value
	Double fCO2Rec;
    // Marker 1-14 of which of the recomputed fCO2 value was selected as "best"
	Integer fCO2Source;
	// TEqu - SST
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
	// days since Jan 1 of that year; 1.0 == Jan 1 00:00
	Double dayOfYear;

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
		sal = FP_MISSING_VALUE;
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
		xCO2Air = FP_MISSING_VALUE;
		pCO2Air = FP_MISSING_VALUE;
		fCO2Air = FP_MISSING_VALUE;
		shipSpeed = FP_MISSING_VALUE; 
		shipDirection = FP_MISSING_VALUE; 
		windSpeedTrue = FP_MISSING_VALUE;
		windSpeedRelative = FP_MISSING_VALUE;
		windDirectionTrue = FP_MISSING_VALUE;
		windDirectionRelative = FP_MISSING_VALUE;
		timestampWoce = CHAR_MISSING_VALUE;
		longitudeWoce = CHAR_MISSING_VALUE;
		latitudeWoce = CHAR_MISSING_VALUE;
		depthWoce = CHAR_MISSING_VALUE;
		salinityWoce = CHAR_MISSING_VALUE;
		tEquWoce = CHAR_MISSING_VALUE;
		sstWoce = CHAR_MISSING_VALUE;
		pEquWoce = CHAR_MISSING_VALUE;
		slpWoce = CHAR_MISSING_VALUE;
		xCO2WaterEquWoce = CHAR_MISSING_VALUE;
		xCO2WaterSSTWoce = CHAR_MISSING_VALUE;
		pCO2WaterEquWoce = CHAR_MISSING_VALUE;
		pCO2WaterSSTWoce = CHAR_MISSING_VALUE;
		fCO2WaterEquWoce = CHAR_MISSING_VALUE;
		fCO2WaterSSTWoce = CHAR_MISSING_VALUE;
		xCO2AirWoce = CHAR_MISSING_VALUE;
		pCO2AirWoce = CHAR_MISSING_VALUE;
		fCO2AirWoce = CHAR_MISSING_VALUE;
		deltaXCO2 = FP_MISSING_VALUE;
		deltaPCO2 = FP_MISSING_VALUE;
		deltaFCO2 = FP_MISSING_VALUE;
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
			DataColumnType type = columnTypes.get(k);
			try {
				if ( type.equals(DataColumnType.EXPOCODE) ||
					 type.equals(DataColumnType.CRUISE_NAME) ||
					 type.equals(DataColumnType.TIMESTAMP) ||
					 type.equals(DataColumnType.DATE) ||
					 type.equals(DataColumnType.TIME) ||
					 type.equals(DataColumnType.COMMENT) ) {
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
					this.sal = Double.valueOf(value);
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
				else if ( type.equals(DataColumnType.XCO2WATER_EQU) ) {
					this.xCO2WaterTEqu = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XCO2WATER_SST) ) {
					this.xCO2WaterSst = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.PCO2WATER_EQU) ) {
					this.pCO2WaterTEqu = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.PCO2WATER_SST) ) {
					this.pCO2WaterSst = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.FCO2WATER_EQU) ) {
					this.fCO2WaterTEqu = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.FCO2WATER_SST) ) {
					this.fCO2WaterSst = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.XCO2AIR) ) {
					this.xCO2Air = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.PCO2AIR) ) {
					this.pCO2Air = Double.valueOf(value);
				}
				else if ( type.equals(DataColumnType.FCO2AIR) ) {
					this.fCO2Air = Double.valueOf(value);
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
				else if ( type.equals(DataColumnType.TIMESTAMP_WOCE) ) {
					this.timestampWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.LONGITUDE_WOCE) ) {
					this.longitudeWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.LATITUDE_WOCE) ) {
					this.latitudeWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.DEPTH_WOCE) ) {
					this.depthWoce = value.charAt(0);
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
				else if ( type.equals(DataColumnType.XCO2WATER_EQU_WOCE) ) {
					this.xCO2WaterEquWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.XCO2WATER_SST_WOCE) ) {
					this.xCO2WaterSSTWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.PCO2WATER_EQU_WOCE) ) {
					this.pCO2WaterEquWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.PCO2WATER_SST_WOCE) ) {
					this.pCO2WaterSSTWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.FCO2WATER_EQU_WOCE) ) {
					this.fCO2WaterEquWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.FCO2WATER_SST_WOCE) ) {
					this.fCO2WaterSSTWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.XCO2AIR_WOCE) ) {
					this.xCO2AirWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.PCO2AIR_WOCE) ) {
					this.pCO2AirWoce = value.charAt(0);
				}
				else if ( type.equals(DataColumnType.FCO2AIR_WOCE) ) {
					this.fCO2AirWoce = value.charAt(0);
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
	public Double getSal() {
		return sal;
	}

	/**
	 * @param sal
	 * 		the sea surface salinity to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setSal(Double sal) {
		if ( sal == null )
			this.sal = FP_MISSING_VALUE;
		else
			this.sal = sal;
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
	public Double getTEqu() {
		return tEqu;
	}

	/**
	 * @param tEqu
	 * 		the equilibrator temperature to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setTEqu(Double tEqu) {
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
	public Double getPEqu() {
		return pEqu;
	}

	/**
	 * @param pEqu
	 * 		the equilibrator pressure to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setPEqu(Double pEqu) {
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
	public Double getXCO2WaterSst() {
		return xCO2WaterSst;
	}

	/**
	 * @param xCO2WaterSst 
	 * 		the xCO2WaterSst to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setXCO2WaterSst(Double xCO2WaterSst) {
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
	public Double getXCO2WaterTEqu() {
		return xCO2WaterTEqu;
	}

	/**
	 * @param xCO2WaterTEqu 
	 * 		the xCO2WaterTEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setXCO2WaterTEqu(Double xCO2WaterTEqu) {
		if ( xCO2WaterTEqu == null )
			this.xCO2WaterTEqu = FP_MISSING_VALUE;
		else
			this.xCO2WaterTEqu = xCO2WaterTEqu;
	}

	/**
	 * @return 
	 * 		the fCO2WaterSst;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getFCO2WaterSst() {
		return fCO2WaterSst;
	}

	/**
	 * @param fCO2WaterSst 
	 * 		the fCO2WaterSst to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2WaterSst(Double fCO2WaterSst) {
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
	public Double getFCO2WaterTEqu() {
		return fCO2WaterTEqu;
	}

	/**
	 * @param fCO2WaterTEqu 
	 * 		the fCO2WaterTEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2WaterTEqu(Double fCO2WaterTEqu) {
		if ( fCO2WaterTEqu == null )
			this.fCO2WaterTEqu = FP_MISSING_VALUE;
		else
			this.fCO2WaterTEqu = fCO2WaterTEqu;
	}

	/**
	 * @return 
	 * 		the pCO2WaterSst;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getPCO2WaterSst() {
		return pCO2WaterSst;
	}

	/**
	 * @param pCO2WaterSst 
	 * 		the pCO2WaterSst to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setPCO2WaterSst(Double pCO2WaterSst) {
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
	public Double getPCO2WaterTEqu() {
		return pCO2WaterTEqu;
	}

	/**
	 * @param pCO2WaterTEqu 
	 * 		the pCO2WaterTEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setPCO2WaterTEqu(Double pCO2WaterTEqu) {
		if ( pCO2WaterTEqu == null )
			this.pCO2WaterTEqu = FP_MISSING_VALUE;
		else
			this.pCO2WaterTEqu = pCO2WaterTEqu;
	}

	/**
	 * @return 
	 * 		the atmospheric xCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getXCO2Air() {
		return xCO2Air;
	}

	/**
	 * @param xCO2Air 
	 * 		the atmospheric xCO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setXCO2Air(Double xCO2Air) {
		if ( xCO2Air == null )
			this.xCO2Air = FP_MISSING_VALUE;
		else
			this.xCO2Air = xCO2Air;
	}

	/**
	 * @return 
	 * 		the atmospheric pCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getPCO2Air() {
		return pCO2Air;
	}

	/**
	 * @param pCO2Air 
	 * 		the atmospheric pCO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setPCO2Air(Double pCO2Air) {
		if ( pCO2Air == null )
			this.pCO2Air = FP_MISSING_VALUE;
		else
			this.pCO2Air = pCO2Air;
	}

	/**
	 * @return 
	 * 		the atmospheric fCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getFCO2Air() {
		return fCO2Air;
	}

	/**
	 * @param fCO2Air 
	 * 		the atmospheric fCO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2Air(Double fCO2Air) {
		if ( fCO2Air == null )
			this.fCO2Air = FP_MISSING_VALUE;
		else
			this.fCO2Air = fCO2Air;
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
	 * 		the timestamp WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getTimestampWoce() {
		return timestampWoce;
	}

	/**
	 * @param timestampWoce 
	 * 		the timestamp WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setTimestampWoce(Character timestampWoce) {
		if ( timestampWoce == null )
			this.timestampWoce = CHAR_MISSING_VALUE;
		else
			this.timestampWoce = timestampWoce;
	}

	/**
	 * @return 
	 * 		the longitude WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getLongitudeWoce() {
		return longitudeWoce;
	}

	/**
	 * @param longitudeWoce 
	 * 		the longitude WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setLongitudeWoce(Character longitudeWoce) {
		if ( longitudeWoce == null )
			this.longitudeWoce = CHAR_MISSING_VALUE;
		else
			this.longitudeWoce = longitudeWoce;
	}

	/**
	 * @return 
	 * 		the latitude WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getLatitudeWoce() {
		return latitudeWoce;
	}

	/**
	 * @param latitudeWoce 
	 * 		the latitude WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setLatitudeWoce(Character latitudeWoce) {
		if ( latitudeWoce == null )
			this.latitudeWoce = CHAR_MISSING_VALUE;
		else
			this.latitudeWoce = latitudeWoce;
	}

	/**
	 * @return 
	 * 		the depth WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getDepthWoce() {
		return depthWoce;
	}

	/**
	 * @param depthWoce 
	 * 		the depth WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setDepthWoce(Character depthWoce) {
		if ( depthWoce == null )
			this.depthWoce = CHAR_MISSING_VALUE;
		else
			this.depthWoce = depthWoce;
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
	public Character getTEquWoce() {
		return tEquWoce;
	}

	/**
	 * @param tEquWoce 
	 * 		the equilibrator temperature WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setTEquWoce(Character tEquWoce) {
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
	public Character getPEquWoce() {
		return pEquWoce;
	}

	/**
	 * @param pEquWoce 
	 * 		the equilibrator pressure WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setPEquWoce(Character pEquWoce) {
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
	public Character getXCO2WaterEquWoce() {
		return xCO2WaterEquWoce;
	}

	/**
	 * @param xCO2WaterEquWoce 
	 * 		the xCO2WaterEqu WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setXCO2WaterEquWoce(Character xCO2WaterEquWoce) {
		if ( xCO2WaterEquWoce == null )
			this.xCO2WaterEquWoce = CHAR_MISSING_VALUE;
		else
			this.xCO2WaterEquWoce = xCO2WaterEquWoce;
	}

	/**
	 * @return 
	 * 		the xCO2WaterSST WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getXCO2WaterSSTWoce() {
		return xCO2WaterSSTWoce;
	}

	/**
	 * @param xCO2WaterSSTWoce 
	 * 		the xCO2WaterSST WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setXCO2WaterSSTWoce(Character xCO2WaterSSTWoce) {
		if ( xCO2WaterSSTWoce == null )
			this.xCO2WaterSSTWoce = CHAR_MISSING_VALUE;
		else
			this.xCO2WaterSSTWoce = xCO2WaterSSTWoce;
	}

	/**
	 * @return 
	 * 		the pCO2WaterEqu WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getPCO2WaterEquWoce() {
		return pCO2WaterEquWoce;
	}

	/**
	 * @param pCO2WaterEquWoce 
	 * 		the pCO2WaterEqu WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setPCO2WaterEquWoce(Character pCO2WaterEquWoce) {
		if ( pCO2WaterEquWoce == null )
			this.pCO2WaterEquWoce = CHAR_MISSING_VALUE;
		else
			this.pCO2WaterEquWoce = pCO2WaterEquWoce;
	}

	/**
	 * @return 
	 * 		the pCO2WaterSST WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getPCO2WaterSSTWoce() {
		return pCO2WaterSSTWoce;
	}

	/**
	 * @param pCO2WaterSSTWoce 
	 * 		the pCO2WaterSST WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setPCO2WaterSSTWoce(Character pCO2WaterSSTWoce) {
		if ( pCO2WaterSSTWoce == null )
			this.pCO2WaterSSTWoce = CHAR_MISSING_VALUE;
		else
			this.pCO2WaterSSTWoce = pCO2WaterSSTWoce;
	}

	/**
	 * @return 
	 * 		the fCO2WaterEqu WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getFCO2WaterEquWoce() {
		return fCO2WaterEquWoce;
	}

	/**
	 * @param fCO2WaterEquWoce 
	 * 		the fCO2WaterEqu WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setFCO2WaterEquWoce(Character fCO2WaterEquWoce) {
		if ( fCO2WaterEquWoce == null )
			this.fCO2WaterEquWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2WaterEquWoce = fCO2WaterEquWoce;
	}

	/**
	 * @return 
	 * 		the fCO2WaterSST WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getFCO2WaterSSTWoce() {
		return fCO2WaterSSTWoce;
	}

	/**
	 * @param fCO2WaterSSTWoce 
	 * 		the fCO2WaterSST WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setFCO2WaterSSTWoce(Character fCO2WaterSSTWoce) {
		if ( fCO2WaterSSTWoce == null )
			this.fCO2WaterSSTWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2WaterSSTWoce = fCO2WaterSSTWoce;
	}

	/**
	 * @return 
	 * 		the xCO2Air WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getXCO2AirWoce() {
		return xCO2AirWoce;
	}

	/**
	 * @param xCO2AirWoce 
	 * 		the xCO2Air WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setXCO2AirWoce(Character xCO2AirWoce) {
		if ( xCO2AirWoce == null )
			this.xCO2AirWoce = CHAR_MISSING_VALUE;
		else
			this.xCO2AirWoce = xCO2AirWoce;
	}

	/**
	 * @return 
	 * 		the pCO2Air WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getPCO2AirWoce() {
		return pCO2AirWoce;
	}

	/**
	 * @param pCO2AirWoce 
	 * 		the pCO2Air WOCE flag to set;
	 * 		if null, {@link #CHAR_MISSING_VALUE} is assigned
	 */
	public void setPCO2AirWoce(Character pCO2AirWoce) {
		if ( pCO2AirWoce == null )
			this.pCO2AirWoce = CHAR_MISSING_VALUE;
		else
			this.pCO2AirWoce = pCO2AirWoce;
	}

	/**
	 * @return 
	 * 		the fCO2Air WOCE flag;
	 * 		never null but could be {@link #CHAR_MISSING_VALUE} if not assigned
	 */
	public Character getFCO2AirWoce() {
		return fCO2AirWoce;
	}

	/**
	 * @param fCO2AirWoce 
	 * 		the fCO2Air WOCE flag to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2AirWoce(Character fCO2AirWoce) {
		if ( fCO2AirWoce == null )
			this.fCO2AirWoce = CHAR_MISSING_VALUE;
		else
			this.fCO2AirWoce = fCO2AirWoce;
	}

	/**
	 * @return 
	 * 		the difference between air and water xCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDeltaXCO2() {
		return deltaXCO2;
	}

	/**
	 * @param deltaXCO2 
	 * 		the difference between air and water xCO2 to set;
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
	 * 		the difference between air and water pCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDeltaPCO2() {
		return deltaPCO2;
	}

	/**
	 * @param deltaPCO2 
	 * 		the difference between air and water pCO2 to set;
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
	 * 		the difference between air and water fCO2;
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDeltaFCO2() {
		return deltaFCO2;
	}

	/**
	 * @param deltaFCO2 
	 * 		the difference between air and water fCO2 to set;
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
	public Double getFCO2FromXCO2TEqu() {
		return fCO2FromXCO2TEqu;
	}

	/**
	 * @param fCO2FromXCO2TEqu 
	 * 		the fCO2 from xCO2 TEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromXCO2TEqu(Double fCO2FromXCO2TEqu) {
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
	public Double getFCO2FromXCO2Sst() {
		return fCO2FromXCO2Sst;
	}

	/**
	 * @param fCO2FromXCO2Sst 
	 * 		the fCO2 from xCO2 SST to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromXCO2Sst(Double fCO2FromXCO2Sst) {
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
	public Double getFCO2FromPCO2TEqu() {
		return fCO2FromPCO2TEqu;
	}

	/**
	 * @param fCO2FromPCO2TEqu 
	 * 		the fCO2 from pCO2 TEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromPCO2TEqu(Double fCO2FromPCO2TEqu) {
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
	public Double getFCO2FromPCO2Sst() {
		return fCO2FromPCO2Sst;
	}

	/**
	 * @param fCO2FromPCO2Sst 
	 * 		the fCO2 from pCO2 SST to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromPCO2Sst(Double fCO2FromPCO2Sst) {
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
	public Double getFCO2FromFCO2TEqu() {
		return fCO2FromFCO2TEqu;
	}

	/**
	 * @param fCO2FromFCO2TEqu
	 * 		the fCO2 from fCO2 TEqu to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromFCO2TEqu(Double fCO2FromFCO2TEqu) {
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
	public Double getFCO2FromFCO2Sst() {
		return fCO2FromFCO2Sst;
	}

	/**
	 * @param fCO2FromFCO2Sst 
	 * 		the fCO2 from fCO2 SST to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromFCO2Sst(Double fCO2FromFCO2Sst) {
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
	public Double getFCO2FromPCO2TEquNcep() {
		return fCO2FromPCO2TEquNcep;
	}

	/**
	 * @param fCO2FromPCO2TEquNcep 
	 * 		the fCO2 from pCO2 TEqu NCEP to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromPCO2TEquNcep(Double fCO2FromPCO2TEquNcep) {
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
	public Double getFCO2FromPCO2SstNcep() {
		return fCO2FromPCO2SstNcep;
	}

	/**
	 * @param fCO2FromPCO2SstNcep 
	 * 		the fCO2 from pCO2 SST NCEP to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromPCO2SstNcep(Double fCO2FromPCO2SstNcep) {
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
	public Double getFCO2FromXCO2TEquWoa() {
		return fCO2FromXCO2TEquWoa;
	}

	/**
	 * @param fCO2FromXCO2TEquWoa 
	 * 		the fCO2 from xCO2 TEqu WOA to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromXCO2TEquWoa(Double fCO2FromXCO2TEquWoa) {
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
	public Double getFCO2FromXCO2SstWoa() {
		return fCO2FromXCO2SstWoa;
	}

	/**
	 * @param fCO2FromXCO2SstWoa 
	 * 		the fCO2 from xCO2 SST WOA to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromXCO2SstWoa(Double fCO2FromXCO2SstWoa) {
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
	public Double getFCO2FromXCO2TEquNcep() {
		return fCO2FromXCO2TEquNcep;
	}

	/**
	 * @param fCO2FromXCO2TEquNcep 
	 * 		the fCO2 from xCO2 TEqu NCEP to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromXCO2TEquNcep(Double fCO2FromXCO2TEquNcep) {
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
	public Double getFCO2FromXCO2SstNcep() {
		return fCO2FromXCO2SstNcep;
	}

	/**
	 * @param fCO2 from xCO2 SST NCEP 
	 * 		the fCO2 from xCO2 SST NCEP to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromXCO2SstNcep(Double fCO2FromXCO2SstNcep) {
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
	public Double getFCO2FromXCO2TEquNcepWoa() {
		return fCO2FromXCO2TEquNcepWoa;
	}

	/**
	 * @param fCO2FromXCO2TEquNcepWoa 
	 * 		the fCO2 from xCO2 TEqu NCEP WOA to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromXCO2TEquNcepWoa(Double fCO2FromXCO2TEquNcepWoa) {
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
	public Double getFCO2FromXCO2SstNcepWoa() {
		return fCO2FromXCO2SstNcepWoa;
	}

	/**
	 * @param fCO2FromXCO2SstNcepWoa 
	 * 		the fCO2 from xCO2 SST NCEP WOA to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2FromXCO2SstNcepWoa(Double fCO2FromXCO2SstNcepWoa) {
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
	public Double getFCO2Rec() {
		return fCO2Rec;
	}

	/**
	 * @param fCO2Rec 
	 * 		the recomputed fCO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setFCO2Rec(Double fCO2Rec) {
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
	public Integer getFCO2Source() {
		return fCO2Source;
	}

	/**
	 * @param fCO2Source
	 * 		the method used to create the recomputed fCO2 to set;
	 * 		if null, {@link #INT_MISSING_VALUE} is assigned
	 */
	public void setFCO2Source(Integer fCO2Source) {
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
	public Double getGVCO2() {
		return gvCO2;
	}

	/**
	 * @param gvCO2 
	 * 		the GlobablView CO2 to set;
	 * 		if null, {@link #FP_MISSING_VALUE} is assigned
	 */
	public void setGVCO2(Double gvCO2) {
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
	 * 		the fractional day of the year (0.0 == Jan 1 00:00);
	 * 		never null but could be {@link #FP_MISSING_VALUE} if not assigned
	 */
	public Double getDayOfYear() {
		return dayOfYear;
	}

	/**
	 * @param dayOfYear 
	 * 		the fractional day of the year (0.0 == Jan 1 00:00) to set;
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
		// Do not use floating-point fields since they do not 
		// have to be exactly the same for equals to return true.
		final int prime = 37;
		int result = year.hashCode();
		result = result * prime + month.hashCode();
		result = result * prime + day.hashCode();
		result = result * prime + hour.hashCode();
		result = result * prime + minute.hashCode();
		result = result * prime + fCO2Source.hashCode();
		result = result * prime + timestampWoce.hashCode();
		result = result * prime + longitudeWoce.hashCode();
		result = result * prime + latitudeWoce.hashCode();
		result = result * prime + depthWoce.hashCode();
		result = result * prime + salinityWoce.hashCode();
		result = result * prime + tEquWoce.hashCode();
		result = result * prime + sstWoce.hashCode();
		result = result * prime + pEquWoce.hashCode();
		result = result * prime + slpWoce.hashCode();
		result = result * prime + xCO2WaterEquWoce.hashCode();
		result = result * prime + xCO2WaterSSTWoce.hashCode();
		result = result * prime + pCO2WaterEquWoce.hashCode();
		result = result * prime + pCO2WaterSSTWoce.hashCode();
		result = result * prime + fCO2WaterEquWoce.hashCode();
		result = result * prime + fCO2WaterSSTWoce.hashCode();
		result = result * prime + xCO2AirWoce.hashCode();
		result = result * prime + pCO2AirWoce.hashCode();
		result = result * prime + fCO2AirWoce.hashCode();
		result = result * prime + regionID.hashCode();
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
		if ( ! timestampWoce.equals(other.timestampWoce) ) 
			return false;
		if ( ! longitudeWoce.equals(other.longitudeWoce) ) 
			return false;
		if ( ! latitudeWoce.equals(other.latitudeWoce) ) 
			return false;
		if ( ! depthWoce.equals(other.depthWoce) ) 
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
		if ( ! xCO2WaterEquWoce.equals(other.xCO2WaterEquWoce) ) 
			return false;
		if ( ! xCO2WaterSSTWoce.equals(other.xCO2WaterSSTWoce) ) 
			return false;
		if ( ! pCO2WaterEquWoce.equals(other.pCO2WaterEquWoce) ) 
			return false;
		if ( ! pCO2WaterSSTWoce.equals(other.pCO2WaterSSTWoce) ) 
			return false;
		if ( ! fCO2WaterEquWoce.equals(other.fCO2WaterEquWoce) ) 
			return false;
		if ( ! fCO2WaterSSTWoce.equals(other.fCO2WaterSSTWoce) ) 
			return false;
		if ( ! xCO2AirWoce.equals(other.xCO2AirWoce) ) 
			return false;
		if ( ! pCO2AirWoce.equals(other.pCO2AirWoce) ) 
			return false;
		if ( ! fCO2AirWoce.equals(other.fCO2AirWoce) ) 
			return false;
		if ( ! regionID.equals(other.regionID) )
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
		if ( ! DashboardUtils.closeTo(sal, other.sal, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
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
		if ( ! DashboardUtils.closeTo(xCO2Air, other.xCO2Air, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pCO2Air, other.pCO2Air, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2Air, other.fCO2Air, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
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
		if ( ! DashboardUtils.closeTo(deltaXCO2, other.deltaXCO2, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(deltaPCO2, other.deltaPCO2, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(deltaFCO2, other.deltaFCO2, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
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
				",\n    sal=" + sal.toString() +
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
				",\n    xCO2Air=" + xCO2Air.toString() +
				",\n    pCO2Air=" + pCO2Air.toString() +
				",\n    fCO2Air=" + fCO2Air.toString() +
				",\n    shipSpeed=" + shipSpeed.toString() +
				",\n    shipDirection=" + shipDirection.toString() +
				",\n    windSpeedTrue=" + windSpeedTrue.toString() +
				",\n    windSpeedRelative=" + windSpeedRelative.toString() +
				",\n    windDirectionTrue=" + windDirectionTrue.toString() +
				",\n    windDirectionRelative=" + windDirectionRelative.toString() +
				",\n    timestampWoce=" + timestampWoce.toString() +
				",\n    longitudeWoce=" + longitudeWoce.toString() +
				",\n    latitudeWoce=" + latitudeWoce.toString() +
				",\n    depthWoce=" + depthWoce.toString() +
				",\n    salinityWoce=" + salinityWoce.toString() +
				",\n    tEquWoce=" + tEquWoce.toString() +
				",\n    sstWoce=" + sstWoce.toString() +
				",\n    pEquWoce=" + pEquWoce.toString() +
				",\n    slpWoce=" + slpWoce.toString() +
				",\n    xCO2WaterEquWoce=" + xCO2WaterEquWoce.toString() +
				",\n    xCO2WaterSSTWoce=" + xCO2WaterSSTWoce.toString() +
				",\n    pCO2WaterEquWoce=" + pCO2WaterEquWoce.toString() +
				",\n    pCO2WaterSSTWoce=" + pCO2WaterSSTWoce.toString() +
				",\n    fCO2WaterEquWoce=" + fCO2WaterEquWoce.toString() +
				",\n    fCO2WaterSSTWoce=" + fCO2WaterSSTWoce.toString() +
				",\n    xCO2AirWoce=" + xCO2AirWoce.toString() +
				",\n    pCO2AirWoce=" + pCO2AirWoce.toString() +
				",\n    fCO2AirWoce=" + fCO2AirWoce.toString() +
				",\n    deltaXCO2=" + deltaXCO2.toString() +
				",\n    deltaPCO2=" + deltaPCO2.toString() +
				",\n    deltaFCO2=" + deltaFCO2.toString() +
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
				" ]";
	}

}
