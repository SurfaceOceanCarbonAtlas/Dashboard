/**
 * 
 */
package gov.noaa.pmel.dashboard.dsg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gov.noaa.pmel.dashboard.datatype.CharDashDataType;
import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import ucar.nc2.time.Calendar;
import ucar.nc2.time.CalendarDate;

/**
 * A 2-D array of objects corresponding to the standardized values in a dataset, 
 * as well as 1-D arrays of information describing each data column.
 * 
 * @author Karl Smith
 */
public class StdDataArray {

	private static final Calendar BASE_CALENDAR = Calendar.proleptic_gregorian;
	/** 1970-01-01 00:00:00 */
	private static final CalendarDate BASE_DATE = CalendarDate.of(BASE_CALENDAR, 1970, 1, 1, 0, 0, 0);

	protected int numSamples;
	protected int numDataCols;
	protected DashDataType<?>[] dataTypes;
	protected Object[][] stdObjects;
	protected int longitudeIndex;
	protected int latitudeIndex;
	protected int sampleDepthIndex;
	protected int timestampIndex;
	protected int dateIndex;
	protected int yearIndex;
	protected int monthOfYearIndex;
	protected int dayOfMonthIndex;
	protected int timeOfDayIndex;
	protected int hourOfDayIndex;
	protected int minuteOfHourIndex;
	protected int secondOfMinuteIndex;
	protected int dayOfYearIndex;
	protected int secondOfDayIndex;

	/**
	 * Create and assign the 1-D arrays of data column types from the given data 
	 * column descriptions.  The 2-D array of standard data objects is not created.
	 * 
	 * @param dataColumnTypes
	 * 		user's description of the data columns in each sample
	 * @param knownTypes
	 * 		all known user data types
	 * @throws IllegalArgumentException
	 * 		if there are no user data column descriptions, 
	 * 		if there are no known user data types, 
	 * 		if a data column description is not a known user data type
	 */
	protected StdDataArray(List<DataColumnType> dataColumnTypes, 
			KnownDataTypes knownTypes) throws IllegalArgumentException {
		if ( (dataColumnTypes == null) || dataColumnTypes.isEmpty() )
			throw new IllegalArgumentException("no data column types given");
		if ( (knownTypes == null) || knownTypes.isEmpty() )
			throw new IllegalArgumentException("no known user data types given");
		numDataCols = dataColumnTypes.size();
		numSamples = 0;

		dataTypes = new DashDataType<?>[numDataCols];
		stdObjects = null;

		for (int k = 0; k < numDataCols; k++) {
			DataColumnType dataColType = dataColumnTypes.get(k);
			dataTypes[k] = knownTypes.getDataType(dataColType);
			if ( dataTypes[k] == null )
				throw new IllegalArgumentException("unknown data column type: " + 
						dataColType.getDisplayName());
		}

		assignColumnIndicesOfInterest();
	}

	/**
	 * Create with the given data file data types for each column and the given 
	 * standardized data objects for each data column value (second index) in each 
	 * sample (first index).  The data types given must be known subclasses of 
	 * DashDataType valid for data files: {@link CharDashDataType}, 
	 * {@link IntDashDataType}, or {@link DoubleDashDataType}.
	 * 
	 * @param dataColumnTypes
	 * 		types for the data columns in each sample
	 * @param stdDataValues
	 * 		standard values; the value at stdDataValues[j][k] is the appropriate
	 * 		object for the value of the k-th data column in the j-th sample.
	 * 		Missing values correspond to null objects.
	 * @throws IllegalArgumentException
	 * 		if not data column types are given, 
	 * 		if a data column type is not a known subclass type,
	 * 		if no data values are given,
	 * 		if the number of data columns in the array of data values does 
	 * 			not match the number of data column types, or
	 * 		if a data value object is not an appropriate object 
	 * 			for the data column type
	 */
	public StdDataArray(List<DashDataType<?>> dataColumnTypes, 
			Object[][] stdDataValues) throws IllegalArgumentException {
		if ( (dataColumnTypes == null) || (dataColumnTypes.isEmpty()) )
			throw new IllegalArgumentException("no data column types given");
		numDataCols = dataColumnTypes.size();
		if ( (stdDataValues == null) || (stdDataValues.length == 0) )
			throw new IllegalArgumentException("no standardized data values given");
		numSamples = stdDataValues.length;
		if ( stdDataValues[0].length != numDataCols )
			throw new IllegalArgumentException("Different number of data column values (" + 
					stdDataValues[0].length + ") and types (" +  numDataCols + ")");

		dataTypes = new DashDataType<?>[numDataCols];
		stdObjects = new Object[numSamples][numDataCols];

		for (int k = 0; k < numDataCols; k++) {
			DashDataType<?> dtype = dataColumnTypes.get(k);
			if ( dtype == null )
				throw new IllegalArgumentException(
						"no data type for column number" + Integer.toString(k+1));
			dataTypes[k] = dtype;

			// Catch invalid data column types and invalid data objects 
			// while assigning the standard data values
			if ( dtype instanceof CharDashDataType ) {
				for (int j = 0; j < numSamples; j++) {
					try {
						stdObjects[j][k] = (Character) stdDataValues[j][k];
					} catch ( Exception ex ) {
						throw new IllegalArgumentException("standard data object for sample number " + 
								Integer.toString(j+1) + ", column number " + Integer.toString(j+1) +
								" is invalid: " + ex.getMessage());
					}
				}
			}
			else if ( dtype instanceof IntDashDataType ) {
				for (int j = 0; j < numSamples; j++) {
					try {
						stdObjects[j][k] = (Integer) stdDataValues[j][k];
					} catch ( Exception ex ) {
						throw new IllegalArgumentException("standard data object for sample number " + 
								Integer.toString(j+1) + ", column number " + Integer.toString(j+1) +
								" is invalid: " + ex.getMessage());
					}
				}
			}
			else if ( dtype instanceof DoubleDashDataType ) {
				for (int j = 0; j < numSamples; j++) {
					try {
						stdObjects[j][k] = (Double) stdDataValues[j][k];
					} catch ( Exception ex ) {
						throw new IllegalArgumentException("standard data object for sample number " + 
								Integer.toString(j+1) + ", column number " + Integer.toString(j+1) +
								" is invalid: " + ex.getMessage());
					}
				}
			}
			else {
				throw new IllegalArgumentException("unknown data class type for " + 
						dtype.getDisplayName() + " (" + dtype.getDataClassName() + ")");
			}
		}

		assignColumnIndicesOfInterest();
	}

	/**
	 * Assigns the data column indices of interest (longitude, latitude, sample 
	 * depth, and various time types) from the assigned types of the data columns.
	 */
	private void assignColumnIndicesOfInterest() {
		longitudeIndex = DashboardUtils.INT_MISSING_VALUE;
		latitudeIndex = DashboardUtils.INT_MISSING_VALUE;
		sampleDepthIndex = DashboardUtils.INT_MISSING_VALUE;
		timestampIndex = DashboardUtils.INT_MISSING_VALUE;
		dateIndex = DashboardUtils.INT_MISSING_VALUE;
		yearIndex = DashboardUtils.INT_MISSING_VALUE;
		monthOfYearIndex = DashboardUtils.INT_MISSING_VALUE;
		dayOfMonthIndex = DashboardUtils.INT_MISSING_VALUE;
		timeOfDayIndex = DashboardUtils.INT_MISSING_VALUE;
		hourOfDayIndex = DashboardUtils.INT_MISSING_VALUE;
		minuteOfHourIndex = DashboardUtils.INT_MISSING_VALUE;
		secondOfMinuteIndex = DashboardUtils.INT_MISSING_VALUE;
		dayOfYearIndex = DashboardUtils.INT_MISSING_VALUE;
		secondOfDayIndex = DashboardUtils.INT_MISSING_VALUE;
		for (int k = 0; k < numDataCols; k++) {
			if ( DashboardServerUtils.LONGITUDE.typeNameEquals(dataTypes[k]) )
				longitudeIndex = k;
			else if ( DashboardServerUtils.LATITUDE.typeNameEquals(dataTypes[k]) )
				latitudeIndex = k;
			else if ( DashboardServerUtils.SAMPLE_DEPTH.typeNameEquals(dataTypes[k]) )
				sampleDepthIndex = k;
			else if ( DashboardServerUtils.TIMESTAMP.typeNameEquals(dataTypes[k]) )
				timestampIndex = k;
			else if ( DashboardServerUtils.DATE.typeNameEquals(dataTypes[k]) )
				dateIndex = k;
			else if ( DashboardServerUtils.YEAR.typeNameEquals(dataTypes[k]) )
				yearIndex = k;
			else if ( DashboardServerUtils.MONTH_OF_YEAR.typeNameEquals(dataTypes[k]) )
				monthOfYearIndex = k;
			else if ( DashboardServerUtils.DAY_OF_MONTH.typeNameEquals(dataTypes[k]) )
				dayOfMonthIndex = k;
			else if ( DashboardServerUtils.TIME_OF_DAY.typeNameEquals(dataTypes[k]) )
				timeOfDayIndex = k;
			else if ( DashboardServerUtils.HOUR_OF_DAY.typeNameEquals(dataTypes[k]) )
				hourOfDayIndex = k;
			else if ( DashboardServerUtils.MINUTE_OF_HOUR.typeNameEquals(dataTypes[k]) )
				minuteOfHourIndex = k;
			else if ( DashboardServerUtils.SECOND_OF_MINUTE.typeNameEquals(dataTypes[k]) )
				secondOfMinuteIndex = k;
			else if ( DashboardServerUtils.DAY_OF_YEAR.typeNameEquals(dataTypes[k]) )
				dayOfYearIndex = k;
			else if ( DashboardServerUtils.SECOND_OF_DAY.typeNameEquals(dataTypes[k]) )
				secondOfDayIndex = k;
		}
	}

	/**
	 * @return 
	 * 		the number of samples (rows) in the current standardized data
	 */
	public int getNumSamples() {
		return numSamples;
	}

	/**
	 * @return 
	 * 		the number of data columns
	 */
	public int getNumDataCols() {
		return numDataCols;
	}

	/**
	 * Determines is this data column is an appropriate index.  This version 
	 * of the method just checks that the value is in the appropriate range.
	 * Subclasses should override this method if further validation is required.
	 * 
	 * @param idx
	 * 		index to test
	 * @return
	 * 		if the index is valid
	 */
	protected boolean isUsableIndex(int idx) {
		if ( idx < 0 )
			return false;
		if ( idx >= numDataCols )
			return false;
		return true;
	}

	/**
	 * @return
	 * 		an array containing the standardized longitudes; 
	 * 		missing values are null
	 * @throws IllegalStateException
	 * 		if there are no standardized longitudes
	 */
	public Double[] getSampleLongitudes() throws IllegalStateException {
		if ( ! isUsableIndex(longitudeIndex) )
			throw new IllegalStateException("no valid longitude data column");
		Double[] sampleLongitudes = new Double[numSamples];
		for (int j = 0; j < numSamples; j++)
			sampleLongitudes[j] = (Double) stdObjects[j][longitudeIndex];
		return sampleLongitudes;
	}

	/**
	 * @return
	 * 		an array containing the standardized latitudes; 
	 * 		missing values are null
	 * @throws IllegalStateException
	 * 		if there are no standardized latitudes
	 */
	public Double[] getSampleLatitudes() throws IllegalStateException {
		if ( ! isUsableIndex(latitudeIndex) )
			throw new IllegalStateException("no valid latitude data column");
		Double[] sampleLatitudes = new Double[numSamples];
		for (int j = 0; j < numSamples; j++)
			sampleLatitudes[j] = (Double) stdObjects[j][latitudeIndex];
		return sampleLatitudes;
	}

	/**
	 * @return
	 * 		an array containing the standardized sample depths; 
	 * 		missing values are null
	 * @throws IllegalStateException
	 * 		if there are no standardized sample depths
	 */
	public Double[] getSampleDepths() throws IllegalStateException {
		if ( ! isUsableIndex(sampleDepthIndex) )
			throw new IllegalStateException("no valid sample depth data column");
		Double[] sampleDepths = new Double[numSamples];
		for (int j = 0; j < numSamples; j++)
			sampleDepths[j] = (Double) stdObjects[j][sampleDepthIndex];
		return sampleDepths;
	}

	/**
	 * Computes the fully-specified time, in units of "seconds since 1970-01-01T00:00:00Z" 
	 * from the standardized date and time data values that can be found in the data.
	 * One of the following combinations of date/time columns must be given; if more than 
	 * one time specification is found, the first specification in this list is used.
	 * <ul>
	 *   <li>YEAR, MONTH_OF_YEAR, DAY_OF_MONTH, HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE</li>
	 *   <li>YEAR, MONTH_OF_YEAR, DAY_OF_MONTH, HOUR_OF_DAY, MINUTE_OF_HOUR</li>
	 *   <li>YEAR, MONTH_OF_YEAR, DAY_OF_MONTH, TIME_OF_DAY</li>
	 *   <li>YEAR, DAY_OF_YEAR, SECOND_OF_DAY</li>
	 *   <li>TIMESTAMP</li>
	 *   <li>DATE, TIME_OF_DAY</li>
	 *   <li>DATE, HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE</li>
	 *   <li>DATE, HOUR_OF_DAY, MINUTE_OF_HOUR</li>
	 *   <li>YEAR, DAY_OF_YEAR</li>
	 * </ul>
	 * In the formats without seconds, or TIME_OF_DAY values without seconds, the seconds
	 * are set to zero.  The logic in this ordering is the most likely mistake is with the 
	 * interpretation of a date string (year-month-day, day-month-year, month-day-year), 
	 * especially if the user gave years with only the last two digits.
	 * 
	 * @return
	 * 		an array containing the sample times; missing values are null
	 * @throws IllegalStateException
	 * 		if specification of the sample date and time is incomplete
	 */
	public Double[] getSampleTimes() throws IllegalStateException {
		Double[] sampleTimes = new Double[numSamples];

		if ( isUsableIndex(yearIndex) && isUsableIndex(monthOfYearIndex) && 
			 isUsableIndex(dayOfMonthIndex) && isUsableIndex(hourOfDayIndex) && 
			 isUsableIndex(minuteOfHourIndex) ) {
			// Get time using just year, month, day, hour, and minute; set second to zero
			for (int j = 0; j < numSamples; j++) {
				try {
					int year = ((Integer) stdObjects[j][yearIndex]).intValue();
					int month = ((Integer) stdObjects[j][monthOfYearIndex]).intValue();
					int day = ((Integer) stdObjects[j][dayOfMonthIndex]).intValue();
					int hour = ((Integer) stdObjects[j][hourOfDayIndex]).intValue();
					int min = ((Integer) stdObjects[j][minuteOfHourIndex]).intValue();
					CalendarDate date = CalendarDate.of(BASE_CALENDAR, year, month, day, hour, min, 0);
					sampleTimes[j] = date.getDifferenceInMsecs(BASE_DATE) / 1000.0;
				} catch ( Exception ex ) {
					sampleTimes[j] = null;
				}
			}
			// If available, add the seconds
			if ( isUsableIndex(secondOfMinuteIndex) ) {
				for (int j = 0; j < numSamples; j++) {
					if ( sampleTimes[j] != null ) {
						try {
							double sec = ((Double) stdObjects[j][secondOfMinuteIndex]).doubleValue();
							sampleTimes[j] += sec;
						} catch ( Exception ex ) {
							// If a secondOfMinute value is missing, just leave as zero
						}
					}
				}
			}
		}
		else if ( isUsableIndex(yearIndex) && isUsableIndex(monthOfYearIndex) && 
				  isUsableIndex(dayOfMonthIndex) && isUsableIndex(timeOfDayIndex) ) {
			// Use year, month, day, and time string
			// Standard format of time string is HH:mm:ss.SSS
			for (int j = 0; j < numSamples; j++) {
				try {
					int year = ((Integer) stdObjects[j][yearIndex]).intValue();
					int month = ((Integer) stdObjects[j][monthOfYearIndex]).intValue();
					int day = ((Integer) stdObjects[j][dayOfMonthIndex]).intValue();
					String[] hms = ((String) stdObjects[j][timeOfDayIndex]).split(":");
					if ( hms.length > 3 )
						throw new Exception();
					int hour = Integer.parseInt(hms[0]);
					int min = Integer.parseInt(hms[1]);
					CalendarDate date = CalendarDate.of(BASE_CALENDAR, year, month, day, hour, min, 0);
					sampleTimes[j] = date.getDifferenceInMsecs(BASE_DATE) / 1000.0;

					if ( hms.length == 3 ) {
						double sec = Double.parseDouble(hms[2]);
						sampleTimes[j] += sec;
					}
				} catch ( Exception ex ) {
					sampleTimes[j] = null;
				}
			}
		}
		else if ( isUsableIndex(yearIndex) && isUsableIndex(dayOfYearIndex) && 
				  isUsableIndex(secondOfDayIndex) ) {
			// Use year, day of year (presumably an integer), and second of day
			for (int j = 0; j < numSamples; j++) {
				try {
					int year = ((Integer) stdObjects[j][yearIndex]).intValue();
					double dayOfYear = ((Double) stdObjects[j][yearIndex]).doubleValue();
					int intDayOfYear = (int) dayOfYear;
					CalendarDate date = CalendarDate.withDoy(BASE_CALENDAR, year, intDayOfYear, 0, 0, 0);
					sampleTimes[j] = date.getDifferenceInMsecs(BASE_DATE) / 1000.0;

					// add the fractional day in case the day of year is not an integer value
					sampleTimes[j] += (dayOfYear - (double) intDayOfYear) * 24.0 * 60.0 * 60.0;
					// add the seconds of day
					sampleTimes[j] = ((Double) stdObjects[j][secondOfDayIndex]).doubleValue();
				} catch ( Exception ex ) {
					sampleTimes[j] = null;
				}
			}
		}
		else if ( isUsableIndex(timestampIndex) ) {
			// Use full timestamp
			// Standard format of the timestamp is yyyy-MM-dd HH:mm:sss.SSS
			for (int j = 0; j < numSamples; j++) {
				try {
					String[] dateTime = ((String) stdObjects[j][timestampIndex]).split(" ");
					if ( dateTime.length != 2 )
						throw new Exception();
					String[] ymd = dateTime[0].split("-");
					if ( ymd.length != 3 )
						throw new Exception();
					int year = Integer.parseInt(ymd[0]);
					int month = Integer.parseInt(ymd[1]);
					int day = Integer.parseInt(ymd[2]);
					String[] hms = dateTime[1].split(":");
					if ( hms.length > 3 )
						throw new Exception();
					int hour = Integer.parseInt(hms[0]);
					int min = Integer.parseInt(hms[1]);
					CalendarDate date = CalendarDate.of(BASE_CALENDAR, year, month, day, hour, min, 0);
					sampleTimes[j] = date.getDifferenceInMsecs(BASE_DATE) / 1000.0;

					if ( hms.length == 3 ) {
						double sec = Double.parseDouble(hms[2]);
						sampleTimes[j] += sec;
					}
				} catch ( Exception ex ) {
					sampleTimes[j] = null;
				}
			}
		}
		else if ( isUsableIndex(dateIndex) && isUsableIndex(timeOfDayIndex) ) {
			// Use date string and time string
			// Standard format of the date is yyyy-MM-dd
			// Standard format of time string is HH:mm:ss.SSS
			for (int j = 0; j < numSamples; j++) {
				try {
					String[] ymd = ((String) stdObjects[j][dateIndex]).split("-");
					if ( ymd.length != 3 )
						throw new Exception();
					int year = Integer.parseInt(ymd[0]);
					int month = Integer.parseInt(ymd[1]);
					int day = Integer.parseInt(ymd[2]);
					String[] hms = ((String) stdObjects[j][timeOfDayIndex]).split(":");
					if ( hms.length > 3 )
						throw new Exception();
					int hour = Integer.parseInt(hms[0]);
					int min = Integer.parseInt(hms[1]);
					CalendarDate date = CalendarDate.of(BASE_CALENDAR, year, month, day, hour, min, 0);
					sampleTimes[j] = date.getDifferenceInMsecs(BASE_DATE) / 1000.0;

					if ( hms.length == 3 ) {
						double sec = Double.parseDouble(hms[2]);
						sampleTimes[j] += sec;
					}
				} catch ( Exception ex ) {
					sampleTimes[j] = null;
				}
			}
		}
		else if ( isUsableIndex(dateIndex) && isUsableIndex(hourOfDayIndex) && 
				  isUsableIndex(minuteOfHourIndex) ) {
			// Use date string, hour, and minute; set second to zero
			// Standard format of the date is yyyy-MM-dd
			for (int j = 0; j < numSamples; j++) {
				try {
					String[] ymd = ((String) stdObjects[j][dateIndex]).split("-");
					if ( ymd.length != 3 )
						throw new Exception();
					int year = Integer.parseInt(ymd[0]);
					int month = Integer.parseInt(ymd[1]);
					int day = Integer.parseInt(ymd[2]);
					int hour = ((Integer) stdObjects[j][hourOfDayIndex]).intValue();
					int min = ((Integer) stdObjects[j][minuteOfHourIndex]).intValue();
					CalendarDate date = CalendarDate.of(BASE_CALENDAR, year, month, day, hour, min, 0);
					sampleTimes[j] = date.getDifferenceInMsecs(BASE_DATE) / 1000.0;
				} catch ( Exception ex ) {
					sampleTimes[j] = null;
				}
			}
			// If available, add the seconds
			if ( isUsableIndex(secondOfMinuteIndex) ) {
				for (int j = 0; j < numSamples; j++) {
					if ( sampleTimes[j] != null ) {
						try {
							double sec = ((Double) stdObjects[j][secondOfMinuteIndex]).doubleValue();
							sampleTimes[j] += sec;
						} catch ( Exception ex ) {
							// If a secondOfMinute value is missing, just leave as zero
						}
					}
				}
			}
		}
		else if ( isUsableIndex(yearIndex) && isUsableIndex(dayOfYearIndex) ) {
			// Use year and day of year (presumably floating-point)
			for (int j = 0; j < numSamples; j++) {
				try {
					int year = ((Integer) stdObjects[j][yearIndex]).intValue();
					double dayOfYear = ((Double) stdObjects[j][yearIndex]).doubleValue();
					int intDayOfYear = (int) dayOfYear;
					CalendarDate date = CalendarDate.withDoy(BASE_CALENDAR, year, intDayOfYear, 0, 0, 0);
					sampleTimes[j] = date.getDifferenceInMsecs(BASE_DATE) / 1000.0;

					// add the fractional day
					sampleTimes[j] += (dayOfYear - (double) intDayOfYear) * 24.0 * 60.0 * 60.0;
				} catch ( Exception ex ) {
					sampleTimes[j] = null;
				}
			}
		}
		else
			throw new IllegalStateException("Incomplete specification of sample time");

		return sampleTimes;
	}

	/**
	 * @return
	 * 		if there is a valid {@link DashboardServerUtils#YEAR} data column
	 */
	public boolean hasYear() {
		return isUsableIndex(yearIndex);
	}

	/**
	 * @return
	 * 		if there is a valid {@link DashboardServerUtils#MONTH_OF_YEAR} data column
	 */
	public boolean hasMonthOfYear() {
		return isUsableIndex(monthOfYearIndex);
	}

	/**
	 * @return
	 * 		if there is a valid {@link DashboardServerUtils#DAY_OF_MONTH} data column
	 */
	public boolean hasDayOfMonth() {
		return isUsableIndex(dayOfMonthIndex);
	}

	/**
	 * @return
	 * 		if there is a valid {@link DashboardServerUtils#HOUR_OF_DAY} data column
	 */
	public boolean hasHourOfDay() {
		return isUsableIndex(hourOfDayIndex);
	}

	/**
	 * @return
	 * 		if there is a valid {@link DashboardServerUtils#MINUTE_OF_HOUR} data column
	 */
	public boolean hasMinuteOfHour() {
		return isUsableIndex(minuteOfHourIndex);
	}

	/**
	 * @return
	 * 		if there is a valid {@link DashboardServerUtils#SECOND_OF_MINUTE} data column
	 */
	public boolean hasSecondOfMinute() {
		return isUsableIndex(secondOfMinuteIndex);
	}

	/**
	 * @return
	 * 		an unmodifiable list of types for the data columns.
	 */
	public List<DashDataType<?>> getDataTypes() {
		return Collections.unmodifiableList(Arrays.asList(dataTypes));
	}

	/**
	 * Get the standard value object for the specified value (column index) 
	 * of the specified sample (row index).
	 * 
	 * @param sampleIdx
	 * 		index of the sample (row)
	 * @param columnIdx
	 * 		index of the data column
	 * @return
	 * 		standard value object; null is returned for "missing value" or
	 * 		values that could not be interpreted
	 * @throws IndexOutOfBoundsException
	 * 		if the sample index or the data column index is invalid
	 */
	public Object getStdVal(int sampleIdx, int columnIdx) throws IndexOutOfBoundsException{
		if ( (sampleIdx < 0) || (sampleIdx >= numSamples) )
			throw new IndexOutOfBoundsException("sample index is invalid: " + sampleIdx);
		if ( (columnIdx < 0) || (columnIdx >= numDataCols) )
			throw new IndexOutOfBoundsException("data column index is invalid: " + columnIdx);
		return stdObjects[sampleIdx][columnIdx];
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = Arrays.deepHashCode(stdObjects);
		result = prime * result + secondOfDayIndex;
		result = prime * result + dayOfYearIndex;
		result = prime * result + secondOfMinuteIndex;
		result = prime * result + minuteOfHourIndex;
		result = prime * result + hourOfDayIndex;
		result = prime * result + timeOfDayIndex;
		result = prime * result + dayOfMonthIndex;
		result = prime * result + monthOfYearIndex;
		result = prime * result + yearIndex;
		result = prime * result + dateIndex;
		result = prime * result + timestampIndex;
		result = prime * result + sampleDepthIndex;
		result = prime * result + latitudeIndex;
		result = prime * result + longitudeIndex;
		result = prime * result + Arrays.hashCode(dataTypes);
		result = prime * result + numDataCols;
		result = prime * result + numSamples;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! ( obj instanceof StdDataArray ) )
			return false;
		StdDataArray other = (StdDataArray) obj;

		if ( numDataCols != other.numDataCols )
			return false;
		if ( numSamples != other.numSamples )
			return false;

		if ( longitudeIndex != other.longitudeIndex )
			return false;
		if ( latitudeIndex != other.latitudeIndex )
			return false;
		if ( sampleDepthIndex != other.sampleDepthIndex )
			return false;
		if ( timestampIndex != other.timestampIndex )
			return false;
		if ( dateIndex != other.dateIndex )
			return false;
		if ( yearIndex != other.yearIndex )
			return false;
		if ( monthOfYearIndex != other.monthOfYearIndex )
			return false;
		if ( dayOfMonthIndex != other.dayOfMonthIndex )
			return false;
		if ( timeOfDayIndex != other.timeOfDayIndex )
			return false;
		if ( hourOfDayIndex != other.hourOfDayIndex )
			return false;
		if ( minuteOfHourIndex != other.minuteOfHourIndex )
			return false;
		if ( secondOfMinuteIndex != other.secondOfMinuteIndex )
			return false;
		if ( dayOfYearIndex != other.dayOfYearIndex )
			return false;
		if ( secondOfDayIndex != other.secondOfDayIndex )
			return false;

		if ( ! Arrays.equals(dataTypes, other.dataTypes) )
			return false;

		if ( ! Arrays.deepEquals(stdObjects, other.stdObjects) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		String repr = "StdDataArray[numSamples=" + numSamples + ", numDataCols=" + numDataCols;
		repr += ",\n  longitudeIndex=" + longitudeIndex;
		repr += ", latitudeIndex=" + latitudeIndex;
		repr += ", sampleDepthIndex=" + sampleDepthIndex;
		repr += ", timestampIndex=" + timestampIndex;
		repr += ", dateIndex=" + dateIndex;
		repr += ", yearIndex=" + yearIndex;
		repr += ", monthOfYearIndex=" + monthOfYearIndex;
		repr += ", dayOfMonthIndex=" + dayOfMonthIndex;
		repr += ", timeOfDayIndex=" + timeOfDayIndex;
		repr += ", hourOfDayIndex=" + hourOfDayIndex;
		repr += ", minuteOfHourIndex=" + minuteOfHourIndex;
		repr += ", secondOfMinuteIndex=" + secondOfMinuteIndex;
		repr += ", dayOfYearIndex=" + dayOfYearIndex;
		repr += ", secondOfDayIndex=" + secondOfDayIndex;
		repr += ",\n  dataTypes=" + Arrays.toString(dataTypes);
		repr += ",\n  stdObjects=[";
		for (int j = 0; j < numSamples; j++) {
			if ( j > 0 )
				repr += ",";
			repr += "\n    " + Arrays.toString(stdObjects[j]);
		}
		repr += "\n  ]\n]";
		return repr;
	}

}
