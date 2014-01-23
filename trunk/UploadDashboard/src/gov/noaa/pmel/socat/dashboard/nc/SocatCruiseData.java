/**
 */
package gov.noaa.pmel.socat.dashboard.nc;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class for working with data values of interest 
 * from a SOCAT cruise data measurement.
 * 
 * @author Karl Smith
 */
public class SocatCruiseData implements Serializable, IsSerializable {

	private static final long serialVersionUID = 3506155014333889793L;

	private static final double MAX_RELATIVE_ERROR = 1.0E-6;
	private static final double MAX_ABSOLUTE_ERROR = 1.0E-4;

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
	// Sea surface temperature
	Double sst;
	// Equilibrator temperature
	Double tEqu;
	// salinity
	Double sal;
	// Atmospheric pressure / sea level pressure
	Double pAtm;
	// Equilibrator pressure
	Double pEqu;
	// Six possible CO2 measurements reported; 
	// typically only one or two actually reported
	Double xCO2WaterSst;
	Double xCO2WaterTEqu;
	Double fCO2WaterSst;
	Double fCO2WaterTEqu;
	Double pCO2WaterSst;
	Double pCO2WaterTEqu;

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
    // "Best" recomputed fCO2 value
	Double fCO2Rec;
    // Marker 1-14 of which of the recomputed fCO2 value was selected as "best"
	Integer fCO2Source;
	// TEqu - SST
	Double deltaT;
	// one-letter ID of the cruise region in which this measurement lies
	String regionID;
	// calculated ship speed from adjacent measurements
	Double calcSpeed;
	// ETOPO2 depth
	Double etopo2;
	// GlobalView xCO2 value
	Double gvCO2;
	// distance to closest land mass (up to 1000 km)
	Double distToLand;
	// days since Jan 1, 1970 00:00:00
	Double days1970;
	// days since Jan 1 of that year; 0.0 - 367.0, 0.0 == Jan 1 00:00
	Double dayOfYear;
	// overall data WOCE flag; 
	// may be assigned by the SanityChecker for questionable (3) or bad (4) data
	// and to be assigned by QC'ers
	Integer woceFlag;

	/**
	 * Generates an empty SOCAT data record
	 */
	public SocatCruiseData() {
		year = -1;
		month = -1;
		day = -1;
		hour = -1;
		minute = -1;
		second = Double.NaN;
		longitude = Double.NaN;
		latitude = Double.NaN;
		sampleDepth = Double.NaN;
		sst = Double.NaN;
		tEqu = Double.NaN;
		sal = Double.NaN;
		pAtm = Double.NaN;
		pEqu = Double.NaN;
		xCO2WaterSst = Double.NaN;
		xCO2WaterTEqu = Double.NaN;
		fCO2WaterSst = Double.NaN;
		fCO2WaterTEqu = Double.NaN;
		pCO2WaterSst = Double.NaN;
		pCO2WaterTEqu = Double.NaN;
		woaSss = Double.NaN;
		ncepSlp = Double.NaN;
		fCO2FromXCO2TEqu = Double.NaN;
		fCO2FromXCO2Sst = Double.NaN;
		fCO2FromPCO2TEqu = Double.NaN;
		fCO2FromPCO2Sst = Double.NaN;
		fCO2FromFCO2TEqu = Double.NaN;
		fCO2FromFCO2Sst = Double.NaN;
		fCO2FromPCO2TEquNcep = Double.NaN;
		fCO2FromPCO2SstNcep = Double.NaN;
		fCO2FromXCO2TEquWoa = Double.NaN;
		fCO2FromXCO2SstWoa = Double.NaN;
		fCO2FromXCO2TEquNcep = Double.NaN;
		fCO2FromXCO2SstNcep = Double.NaN;
		fCO2FromXCO2TEquNcepWoa = Double.NaN;
		fCO2FromXCO2SstNcepWoa = Double.NaN;
		fCO2Rec = Double.NaN;
		fCO2Source = 0;
		deltaT = Double.NaN;
		regionID = "";
		calcSpeed = Double.NaN;
		etopo2 = Double.NaN;
		gvCO2 = Double.NaN;
		distToLand = Double.NaN;
		days1970 = Double.NaN;
		dayOfYear = Double.NaN;
		woceFlag = 0;
	}

	/**
	 * Generates a SOCAT cruise data objects from a list of data column 
	 * types and matching data strings.  This assumes the data in the 
	 * strings are in the standard units for each type, and the missing
	 * value is "NaN", and empty string, or null.  The data column types 
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
			// initialized to the missing value for that type.
			// The string should always be "NaN", but allow some flexibility
			String value = dataValues.get(k);
			if ( (value == null) || value.isEmpty() || value.equals("NaN") )
				continue;
			// Only pay attention to the column types needed
			DataColumnType type = columnTypes.get(k);
			try {
				if ( type == DataColumnType.YEAR ) {
					this.year = Integer.valueOf(value);
				}
				else if ( type == DataColumnType.MONTH ) {
					this.month = Integer.valueOf(value);
				}
				else if ( type == DataColumnType.DAY ) {
					this.day = Integer.valueOf(value);
				}
				else if ( type == DataColumnType.HOUR ) {
					this.hour = Integer.valueOf(value);
				}
				else if ( type == DataColumnType.MINUTE ) {
					this.minute = Integer.valueOf(value);
				}
				else if ( type == DataColumnType.SECOND ) {
					this.second = Double.valueOf(value);
				}
				else if ( type == DataColumnType.LONGITUDE ) {
					this.longitude = Double.valueOf(value);
				}
				else if ( type == DataColumnType.LATITUDE ) {
					this.latitude = Double.valueOf(value);
				}
				else if ( type == DataColumnType.SAMPLE_DEPTH ) {
					this.sampleDepth = Double.valueOf(value);
				}
				else if ( type == DataColumnType.SALINITY ) {
					this.sal = Double.valueOf(value);
				}
				else if ( type == DataColumnType.EQUILIBRATOR_TEMPERATURE ) {
					this.tEqu = Double.valueOf(value);
				}
				else if ( type == DataColumnType.SEA_SURFACE_TEMPERATURE ) {
					this.sst = Double.valueOf(value);
				}
				else if ( type == DataColumnType.EQUILIBRATOR_PRESSURE ) {
					this.pEqu = Double.valueOf(value);
				}
				else if ( type == DataColumnType.SEA_LEVEL_PRESSURE ) {
					this.pAtm = Double.valueOf(value);
				}
				else if ( type == DataColumnType.XCO2WATER_EQU ) {
					this.xCO2WaterTEqu = Double.valueOf(value);
				}
				else if ( type == DataColumnType.XCO2WATER_SST ) {
					this.xCO2WaterSst = Double.valueOf(value);
				}
				else if ( type == DataColumnType.PCO2WATER_EQU ) {
					this.pCO2WaterTEqu = Double.valueOf(value);
				}
				else if ( type == DataColumnType.PCO2WATER_SST ) {
					this.pCO2WaterSst = Double.valueOf(value);
				}
				else if ( type == DataColumnType.FCO2WATER_EQU ) {
					this.fCO2WaterTEqu = Double.valueOf(value);
				}
				else if ( type == DataColumnType.FCO2WATER_SST ) {
					this.fCO2WaterSst = Double.valueOf(value);
				}
				// Ignore it if not one of the above types
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
	 * 		never null but could be -1 if not assigned
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * @param year 
	 * 		the year of the data measurement to set; 
	 * 		if null, -1 is assigned
	 */
	public void setYear(Integer year) {
		if ( year == null )
			this.year = -1;
		else
			this.year = year;
	}

	/**
	 * @return 
	 * 		the month of the data measurement; 
	 * 		never null but could be -1 if not assigned
	 */
	public Integer getMonth() {
		return month;
	}

	/**
	 * @param month 
	 * 		the month of the data measurement to set; 
	 * 		if null, -1 is assigned
	 */
	public void setMonth(Integer month) {
		if ( month == null )
			this.month = -1;
		else
			this.month = month;
	}

	/**
	 * @return 
	 * 		the day of the data measurement; 
	 * 		never null but could be -1 if not assigned
	 */
	public Integer getDay() {
		return day;
	}

	/**
	 * @param day 
	 * 		the day of the data measurement to set; 
	 * 		if null, -1 is assigned
	 */
	public void setDay(Integer day) {
		if ( day == null )
			this.day = -1;
		else
			this.day = day;
	}

	/**
	 * @return 
	 * 		the hour of the data measurement; 
	 * 		never null but could be -1 if not assigned
	 */
	public Integer getHour() {
		return hour;
	}

	/**
	 * @param hour 
	 * 		the hour of the data measurement to set; 
	 * 		if null, -1 is assigned
	 */
	public void setHour(Integer hour) {
		if ( hour == null )
			this.hour = -1;
		else
			this.hour = hour;
	}

	/**
	 * @return 
	 * 		the minute of the data measurement; 
	 * 		never null but could be -1 if not assigned
	 */
	public Integer getMinute() {
		return minute;
	}

	/**
	 * @param minute 
	 * 		the minute of the data measurement to set; 
	 * 		if null, -1 is assigned
	 */
	public void setMinute(Integer minute) {
		if ( minute == null )
			this.minute = -1;
		else
			this.minute = minute;
	}

	/**
	 * @return 
	 * 		the second of the data measurement; 
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getSecond() {
		return second;
	}

	/**
	 * @param second 
	 * 		the second of the data measurement to set; 
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setSecond(Double second) {
		if ( second == null )
			this.second = Double.NaN;
		else
			this.second = second;
	}

	/**
	 * @return 
	 * 		the longitude of the data measurement; 
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude 
	 * 		the longitude of the data measurement to set; 
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setLongitude(Double longitude) {
		if ( longitude == null )
			this.longitude = Double.NaN;
		else
			this.longitude = longitude;
	}

	/**
	 * @return 
	 * 		the latitude of the data measurement; 
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude 
	 * 		the latitude of the data measurement to set; 
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setLatitude(Double latitude) {
		if ( latitude == null )
			this.latitude = Double.NaN;
		else
			this.latitude = latitude;
	}

	/**
	 * @return 
	 * 		the sampling depth;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getSampleDepth() {
		return sampleDepth;
	}

	/**
	 * @param sampleDepth 
	 * 		the sampling depth to set
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setSampleDepth(Double sampleDepth) {
		if ( sampleDepth == null )
			this.sampleDepth = Double.NaN;
		else
			this.sampleDepth = sampleDepth;
	}

	/**
	 * @return 
	 * 		the sea surface temperature;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getSst() {
		return sst;
	}

	/**
	 * @param sst 
	 * 		the sea surface temperature to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setSst(Double sst) {
		if ( sst == null )
			this.sst = Double.NaN;
		else
			this.sst = sst;
	}

	/**
	 * @return 
	 * 		the equilibrator temperature;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getTEqu() {
		return tEqu;
	}

	/**
	 * @param tEqu
	 * 		the equilibrator temperature to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setTEqu(Double tEqu) {
		if ( tEqu == null )
			this.tEqu = Double.NaN;
		else
			this.tEqu = tEqu;
	}

	/**
	 * @return 
	 * 		the sea surface salinity;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getSal() {
		return sal;
	}

	/**
	 * @param sal
	 * 		the sea surface salinity to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setSal(Double sal) {
		if ( sal == null )
			this.sal = Double.NaN;
		else
			this.sal = sal;
	}

	/**
	 * @return 
	 * 		the atmospheric pressure;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getPAtm() {
		return pAtm;
	}

	/**
	 * @param pAtm 
	 * 		the atmospheric pressure to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setPAtm(Double pAtm) {
		if ( pAtm == null )
			this.pAtm = Double.NaN;
		else
			this.pAtm = pAtm;
	}

	/**
	 * @return 
	 * 		the equilibrator pressure;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getPEqu() {
		return pEqu;
	}

	/**
	 * @param pEqu
	 * 		the equilibrator pressure to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setPEqu(Double pEqu) {
		if ( pEqu == null )
			this.pEqu = Double.NaN;
		else
			this.pEqu = pEqu;
	}

	/**
	 * @return 
	 * 		the xCO2WaterSst;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getXCO2WaterSst() {
		return xCO2WaterSst;
	}

	/**
	 * @param xCO2WaterSst 
	 * 		the xCO2WaterSst to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setXCO2WaterSst(Double xCO2WaterSst) {
		if ( xCO2WaterSst == null )
			this.xCO2WaterSst = Double.NaN;
		else
			this.xCO2WaterSst = xCO2WaterSst;
	}

	/**
	 * @return 
	 * 		the xCO2WaterTEqu;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getXCO2WaterTEqu() {
		return xCO2WaterTEqu;
	}

	/**
	 * @param xCO2WaterTEqu 
	 * 		the xCO2WaterTEqu to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setXCO2WaterTEqu(Double xCO2WaterTEqu) {
		if ( xCO2WaterTEqu == null )
			this.xCO2WaterTEqu = Double.NaN;
		else
			this.xCO2WaterTEqu = xCO2WaterTEqu;
	}

	/**
	 * @return 
	 * 		the fCO2WaterSst;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2WaterSst() {
		return fCO2WaterSst;
	}

	/**
	 * @param fCO2WaterSst 
	 * 		the fCO2WaterSst to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2WaterSst(Double fCO2WaterSst) {
		if ( fCO2WaterSst == null )
			this.fCO2WaterSst = Double.NaN;
		else
			this.fCO2WaterSst = fCO2WaterSst;
	}

	/**
	 * @return 
	 * 		the fCO2WaterTEqu;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2WaterTEqu() {
		return fCO2WaterTEqu;
	}

	/**
	 * @param fCO2WaterTEqu 
	 * 		the fCO2WaterTEqu to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2WaterTEqu(Double fCO2WaterTEqu) {
		if ( fCO2WaterTEqu == null )
			this.fCO2WaterTEqu = Double.NaN;
		else
			this.fCO2WaterTEqu = fCO2WaterTEqu;
	}

	/**
	 * @return 
	 * 		the pCO2WaterSst;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getPCO2WaterSst() {
		return pCO2WaterSst;
	}

	/**
	 * @param pCO2WaterSst 
	 * 		the pCO2WaterSst to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setPCO2WaterSst(Double pCO2WaterSst) {
		if ( pCO2WaterSst == null )
			this.pCO2WaterSst = Double.NaN;
		else
			this.pCO2WaterSst = pCO2WaterSst;
	}

	/**
	 * @return 
	 * 		the pCO2WaterTEqu;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getPCO2WaterTEqu() {
		return pCO2WaterTEqu;
	}

	/**
	 * @param pCO2WaterTEqu 
	 * 		the pCO2WaterTEqu to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setPCO2WaterTEqu(Double pCO2WaterTEqu) {
		if ( pCO2WaterTEqu == null )
			this.pCO2WaterTEqu = Double.NaN;
		else
			this.pCO2WaterTEqu = pCO2WaterTEqu;
	}

	/**
	 * @return 
	 * 		the WOA sea surface salinity;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getWoaSss() {
		return woaSss;
	}

	/**
	 * @param woaSss 
	 * 		the WOA sea surface salinity to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setWoaSss(Double woaSss) {
		if ( woaSss == null )
			this.woaSss = Double.NaN;
		else
			this.woaSss = woaSss;
	}

	/**
	 * @return 
	 * 		the NCEP sea level pressure;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getNcepSlp() {
		return ncepSlp;
	}

	/**
	 * @param ncepSlp 
	 * 		the NCEP sea level pressure to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setNcepSlp(Double ncepSlp) {
		if ( ncepSlp == null )
			this.ncepSlp = Double.NaN;
		else
			this.ncepSlp = ncepSlp;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 TEqu;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromXCO2TEqu() {
		return fCO2FromXCO2TEqu;
	}

	/**
	 * @param fCO2FromXCO2TEqu 
	 * 		the fCO2 from xCO2 TEqu to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromXCO2TEqu(Double fCO2FromXCO2TEqu) {
		if ( fCO2FromXCO2TEqu == null )
			this.fCO2FromXCO2TEqu = Double.NaN;
		else
			this.fCO2FromXCO2TEqu = fCO2FromXCO2TEqu;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 SST;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromXCO2Sst() {
		return fCO2FromXCO2Sst;
	}

	/**
	 * @param fCO2FromXCO2Sst 
	 * 		the fCO2 from xCO2 SST to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromXCO2Sst(Double fCO2FromXCO2Sst) {
		if ( fCO2FromXCO2Sst == null )
			this.fCO2FromXCO2Sst = Double.NaN;
		else
			this.fCO2FromXCO2Sst = fCO2FromXCO2Sst;
	}

	/**
	 * @return 
	 * 		the fCO2 from pCO2 TEqu;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromPCO2TEqu() {
		return fCO2FromPCO2TEqu;
	}

	/**
	 * @param fCO2FromPCO2TEqu 
	 * 		the fCO2 from pCO2 TEqu to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromPCO2TEqu(Double fCO2FromPCO2TEqu) {
		if ( fCO2FromPCO2TEqu == null )
			this.fCO2FromPCO2TEqu = Double.NaN;
		else
			this.fCO2FromPCO2TEqu = fCO2FromPCO2TEqu;
	}

	/**
	 * @return 
	 * 		the fCO2 from pCO2 SST;
	 * 		never null but could be {@link Double#NaN} if not assigned 
	 */
	public Double getFCO2FromPCO2Sst() {
		return fCO2FromPCO2Sst;
	}

	/**
	 * @param fCO2FromPCO2Sst 
	 * 		the fCO2 from pCO2 SST to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromPCO2Sst(Double fCO2FromPCO2Sst) {
		if ( fCO2FromPCO2Sst == null )
			this.fCO2FromPCO2Sst = Double.NaN;
		else
			this.fCO2FromPCO2Sst = fCO2FromPCO2Sst;
	}

	/**
	 * @return 
	 * 		the fCO2 from fCO2 TEqu;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromFCO2TEqu() {
		return fCO2FromFCO2TEqu;
	}

	/**
	 * @param fCO2FromFCO2TEqu
	 * 		the fCO2 from fCO2 TEqu to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromFCO2TEqu(Double fCO2FromFCO2TEqu) {
		if ( fCO2FromFCO2TEqu == null )
			this.fCO2FromFCO2TEqu = Double.NaN;
		else
			this.fCO2FromFCO2TEqu = fCO2FromFCO2TEqu;
	}

	/**
	 * @return 
	 * 		the fCO2 from fCO2 SST;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromFCO2Sst() {
		return fCO2FromFCO2Sst;
	}

	/**
	 * @param fCO2FromFCO2Sst 
	 * 		the fCO2 from fCO2 SST to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromFCO2Sst(Double fCO2FromFCO2Sst) {
		if ( fCO2FromFCO2Sst == null )
			this.fCO2FromFCO2Sst = Double.NaN;
		else
			this.fCO2FromFCO2Sst = fCO2FromFCO2Sst;
	}

	/**
	 * @return 
	 * 		the fCO2 from pCO2 TEqu NCEP;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromPCO2TEquNcep() {
		return fCO2FromPCO2TEquNcep;
	}

	/**
	 * @param fCO2FromPCO2TEquNcep 
	 * 		the fCO2 from pCO2 TEqu NCEP to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromPCO2TEquNcep(Double fCO2FromPCO2TEquNcep) {
		if ( fCO2FromPCO2TEquNcep == null )
			this.fCO2FromPCO2TEquNcep = Double.NaN;
		else
			this.fCO2FromPCO2TEquNcep = fCO2FromPCO2TEquNcep;
	}

	/**
	 * @return 
	 * 		the fCO2 from pCO2 SST NCEP;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromPCO2SstNcep() {
		return fCO2FromPCO2SstNcep;
	}

	/**
	 * @param fCO2FromPCO2SstNcep 
	 * 		the fCO2 from pCO2 SST NCEP to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromPCO2SstNcep(Double fCO2FromPCO2SstNcep) {
		if ( fCO2FromPCO2SstNcep == null )
			this.fCO2FromPCO2SstNcep = Double.NaN;
		else
			this.fCO2FromPCO2SstNcep = fCO2FromPCO2SstNcep;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 TEqu WOA;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromXCO2TEquWoa() {
		return fCO2FromXCO2TEquWoa;
	}

	/**
	 * @param fCO2FromXCO2TEquWoa 
	 * 		the fCO2 from xCO2 TEqu WOA to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromXCO2TEquWoa(Double fCO2FromXCO2TEquWoa) {
		if ( fCO2FromXCO2TEquWoa == null )
			this.fCO2FromXCO2TEquWoa = Double.NaN;
		else
			this.fCO2FromXCO2TEquWoa = fCO2FromXCO2TEquWoa;
	}

	/**
	 * @return 
	 * 		the fCO2 from XCO2 SST WOA;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromXCO2SstWoa() {
		return fCO2FromXCO2SstWoa;
	}

	/**
	 * @param fCO2FromXCO2SstWoa 
	 * 		the fCO2 from xCO2 SST WOA to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromXCO2SstWoa(Double fCO2FromXCO2SstWoa) {
		if ( fCO2FromXCO2SstWoa == null )
			this.fCO2FromXCO2SstWoa = Double.NaN;
		else
			this.fCO2FromXCO2SstWoa = fCO2FromXCO2SstWoa;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 TEqu NCEP;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromXCO2TEquNcep() {
		return fCO2FromXCO2TEquNcep;
	}

	/**
	 * @param fCO2FromXCO2TEquNcep 
	 * 		the fCO2 from xCO2 TEqu NCEP to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromXCO2TEquNcep(Double fCO2FromXCO2TEquNcep) {
		if ( fCO2FromXCO2TEquNcep == null )
			this.fCO2FromXCO2TEquNcep = Double.NaN;
		else
			this.fCO2FromXCO2TEquNcep = fCO2FromXCO2TEquNcep;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 SST NCEP;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromXCO2SstNcep() {
		return fCO2FromXCO2SstNcep;
	}

	/**
	 * @param fCO2 from xCO2 SST NCEP 
	 * 		the fCO2 from xCO2 SST NCEP to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromXCO2SstNcep(Double fCO2FromXCO2SstNcep) {
		if ( fCO2FromXCO2SstNcep == null )
			this.fCO2FromXCO2SstNcep = Double.NaN;
		else
			this.fCO2FromXCO2SstNcep = fCO2FromXCO2SstNcep;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 TEqu NCEP WOA;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromXCO2TEquNcepWoa() {
		return fCO2FromXCO2TEquNcepWoa;
	}

	/**
	 * @param fCO2FromXCO2TEquNcepWoa 
	 * 		the fCO2 from xCO2 TEqu NCEP WOA to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromXCO2TEquNcepWoa(Double fCO2FromXCO2TEquNcepWoa) {
		if ( fCO2FromXCO2TEquNcepWoa == null )
			this.fCO2FromXCO2TEquNcepWoa = Double.NaN;
		else
			this.fCO2FromXCO2TEquNcepWoa = fCO2FromXCO2TEquNcepWoa;
	}

	/**
	 * @return 
	 * 		the fCO2 from xCO2 SST NCEP WOA;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2FromXCO2SstNcepWoa() {
		return fCO2FromXCO2SstNcepWoa;
	}

	/**
	 * @param fCO2FromXCO2SstNcepWoa 
	 * 		the fCO2 from xCO2 SST NCEP WOA to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2FromXCO2SstNcepWoa(Double fCO2FromXCO2SstNcepWoa) {
		if ( fCO2FromXCO2SstNcepWoa == null )
			this.fCO2FromXCO2SstNcepWoa = Double.NaN;
		else
			this.fCO2FromXCO2SstNcepWoa = fCO2FromXCO2SstNcepWoa;
	}

	/**
	 * @return 
	 * 		the recomputed fCO2;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getFCO2Rec() {
		return fCO2Rec;
	}

	/**
	 * @param fCO2Rec 
	 * 		the recomputed fCO2 to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setFCO2Rec(Double fCO2Rec) {
		if ( fCO2Rec == null )
			this.fCO2Rec = Double.NaN;
		else
			this.fCO2Rec = fCO2Rec;
	}

	/**
	 * @return 
	 * 		the method used to create the recomputed fCO2;
	 * 		never null but could be zero if not assigned
	 */
	public Integer getFCO2Source() {
		return fCO2Source;
	}

	/**
	 * @param fCO2Source
	 * 		the method used to create the recomputed fCO2 to set;
	 * 		if null, zero is assigned
	 */
	public void setFCO2Source(Integer fCO2Source) {
		if ( fCO2Source == null )
			this.fCO2Source = 0;
		else
			this.fCO2Source = fCO2Source;
	}

	/**
	 * @return 
	 * 		the difference between sea surface and equilibrator temperature;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getDeltaT() {
		return deltaT;
	}

	/**
	 * @param deltaT
	 * 		the difference between sea surface and equilibrator temperature to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setDeltaT(Double deltaT) {
		if ( deltaT == null )
			this.deltaT = Double.NaN;
		else
			this.deltaT = deltaT;
	}

	/**
	 * @return 
	 * 		the region ID;
	 * 		never null but could be empty if not assigned
	 */
	public String getRegionID() {
		return regionID;
	}

	/**
	 * @param regionID 
	 * 		the region ID to set;
	 * 		if null, an empty String is assigned
	 */
	public void setRegionID(String regionID) {
		if ( regionID == null )
			this.regionID = "";
		else
			this.regionID = regionID;
	}

	/**
	 * @return 
	 * 		the calculated speed of the ship;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getCalcSpeed() {
		return calcSpeed;
	}

	/**
	 * @param calcSpeed 
	 * 		the calculated speed of the ship to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setCalcSpeed(Double calcSpeed) {
		if ( calcSpeed == null )
			this.calcSpeed = Double.NaN;
		else
			this.calcSpeed = calcSpeed;
	}

	/**
	 * @return 
	 * 		the ETOPO2 depth;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getEtopo2() {
		return etopo2;
	}

	/**
	 * @param etopo2
	 * 		the ETOPO2 depth to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setEtopo2(Double etopo2) {
		if ( etopo2 == null )
			this.etopo2 = Double.NaN;
		else
			this.etopo2 = etopo2;
	}

	/**
	 * @return 
	 * 		the GlobablView CO2;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getGVCO2() {
		return gvCO2;
	}

	/**
	 * @param gvCO2 
	 * 		the GlobablView CO2 to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setGVCO2(Double gvCO2) {
		if ( gvCO2 == null )
			this.gvCO2 = Double.NaN;
		else
			this.gvCO2 = gvCO2;
	}

	/**
	 * @return 
	 * 		the distance to nearest major land mass;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getDistToLand() {
		return distToLand;
	}

	/**
	 * @param distToLand 
	 * 		the distance to nearest major land mass to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setDistToLand(Double distToLand) {
		if ( distToLand == null )
			this.distToLand = Double.NaN;
		else
			this.distToLand = distToLand;
	}

	/**
	 * @return 
	 * 		the fractional hours since Jan 1, 1970 00:00;
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getDays1970() {
		return days1970;
	}

	/**
	 * @param days1970 
	 * 		the fractional hours since Jan 1, 1970 00:00 to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setDays1970(Double days1970) {
		if ( days1970 == null )
			this.days1970 = Double.NaN;
		else
			this.days1970 = days1970;
	}

	/**
	 * @return 
	 * 		the fractional day of the year (0.0 == Jan 1 00:00);
	 * 		never null but could be {@link Double#NaN} if not assigned
	 */
	public Double getDayOfYear() {
		return dayOfYear;
	}

	/**
	 * @param dayOfYear 
	 * 		the fractional day of the year (0.0 == Jan 1 00:00) to set;
	 * 		if null, {@link Double#NaN} is assigned
	 */
	public void setDayOfYear(Double dayOfYear) {
		if ( dayOfYear == null )
			this.dayOfYear = Double.NaN;
		else
			this.dayOfYear = dayOfYear;
	}

	/**
	 * @return 
	 * 		the WOCE flag;
	 * 		never null but could be zero if not assigned
	 */
	public Integer getWoceFlag() {
		return woceFlag;
	}

	/**
	 * @param woceFlag 
	 * 		the WOCE Flag to set;
	 * 		if null, zero is assigned
	 */
	public void setWoceFlag(Integer woceFlag) {
		if ( woceFlag == null )
			this.woceFlag = 0;
		else
			this.woceFlag = woceFlag;
	}

	@Override 
	public int hashCode() {
		// Do not use floating-point fields since they do not 
		// have to be exactly the same for equals to return true.
		final int prime = 67;
		int result = year.hashCode();
		result = result * prime + month.hashCode();
		result = result * prime + day.hashCode();
		result = result * prime + hour.hashCode();
		result = result * prime + minute.hashCode();
		result = result * prime + fCO2Source.hashCode();
		result = result * prime + regionID.hashCode();
		result = result * prime + woceFlag.hashCode();
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
		if ( ! fCO2Source.equals(other.fCO2Source) ) 
			return false;
		if ( ! woceFlag.equals(other.woceFlag) )
			return false;

		// String comparisons
		if ( ! regionID.equals(other.regionID) )
			return false;

		// Match seconds not given (NaN) with zero seconds
		if ( ! DashboardUtils.closeTo(second, other.second, 0.0, 1.0E-3) ) {
			if ( ! (second.isNaN() && DashboardUtils.closeTo(0.0, other.second, 0.0, 1.0E-3)) )
				if ( ! (other.second.isNaN() && DashboardUtils.closeTo(second, 0.0, 0.0, 1.0E-3)) )
					return false;
		}

		// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
		if ( ! longitude.isNaN() ) {
			if ( other.longitude.isNaN() )
				return false;
			if ( ! DashboardUtils.closeTo(this.longitude, other.longitude, 0.0, MAX_ABSOLUTE_ERROR) )
				if ( ! DashboardUtils.closeTo(this.longitude + 360.0, other.longitude, 0.0, MAX_ABSOLUTE_ERROR) )
					if ( ! DashboardUtils.closeTo(this.longitude, other.longitude + 360.0, 0.0, MAX_ABSOLUTE_ERROR) )
						return false;
		}
		else {
			if ( ! other.longitude.isNaN() )
				return false;
		}

		// rest of the Double comparisons
		if ( ! DashboardUtils.closeTo(latitude, other.latitude, 0.0, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(sampleDepth, other.sampleDepth, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(sst, other.sst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(tEqu, other.tEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(sal, other.sal, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pAtm, other.pAtm, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pEqu, other.pEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(xCO2WaterSst, other.xCO2WaterSst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(xCO2WaterTEqu, other.xCO2WaterTEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2WaterSst, other.fCO2WaterSst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(fCO2WaterTEqu, other.fCO2WaterTEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pCO2WaterSst, other.pCO2WaterSst, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(pCO2WaterTEqu, other.pCO2WaterTEqu, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
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
		if ( ! DashboardUtils.closeTo(etopo2, other.etopo2, MAX_RELATIVE_ERROR, MAX_ABSOLUTE_ERROR) )
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
				",\n    sst=" + sst.toString() +
				",\n    tEqu=" + tEqu.toString() +
				",\n    sal=" + sal.toString() +
				",\n    pAtm=" + pAtm.toString() +
				",\n    pEqu=" + pEqu.toString() +
				",\n    xCO2WaterSst=" + xCO2WaterSst.toString() +
				",\n    xCO2WaterTEqu=" + xCO2WaterTEqu.toString() +
				",\n    fCO2WaterSst=" + fCO2WaterSst.toString() +
				",\n    fCO2WaterTEqu=" + fCO2WaterTEqu.toString() +
				",\n    pCO2WaterSst=" + pCO2WaterSst.toString() +
				",\n    pCO2WaterTEqu=" + pCO2WaterTEqu.toString() +
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
				",\n    etopo2=" + etopo2.toString() +
				",\n    gvCO2=" + gvCO2.toString() +
				",\n    distToLand=" + distToLand.toString() +
				",\n    days1970=" + days1970.toString() +
				",\n    dayOfYear=" + dayOfYear.toString() +
				",\n    woceFlag=" + woceFlag.toString() +
				" ]";
	}

}
