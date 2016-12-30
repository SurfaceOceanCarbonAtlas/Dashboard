/**
 * 
 */
package gov.noaa.pmel.dashboard.dsg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.ValueConverter;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

/**
 * A 2-D array of objects corresponding to the standardized values in a dataset, 
 * as well as 1-D arrays of information describing each data column.
 * 
 * @author Karl Smith
 */
public class StdDataArray {

	public static final String INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG = 
			"inconstistent number of data values";

	private int numSamples;
	private int numDataCols;
	private String[] userColNames;
	private DashDataType<?>[] dataTypes;
	private String[] userUnits;
	private String[] userMissVals;
	private Boolean[] standardized;
	private Object[][] stdObjects;
	private int longitudeIndex;
	private int latitudeIndex;
	private int sampleDepthIndex;
	private int timestampIndex;
	private int dateIndex;
	private int yearIndex;
	private int monthOfYearIndex;
	private int dayOfMonthIndex;
	private int timeOfDayIndex;
	private int hourOfDayIndex;
	private int minuteOfHourIndex;
	private int secondOfMinuteIndex;
	private int dayOfYearIndex;
	private int secondOfDayIndex;

	/**
	 * Create and assign the 1-D arrays of data column information (type, input unit, input 
	 * missing value) from the given data column descriptions.  The 2-D array of standard
	 * data objects is not created until {@link #standardizeData(ArrayList)} is called.
	 * 
	 * @param userColumnNames
	 * 		user's name for the data columns
	 * @param dataColumnTypes
	 * 		description of the data columns in each sample
	 * @param knownTypes
	 * 		all known data types that a user may provide
	 * @throws IllegalArgumentException
	 * 		if the specified number of samples is not positive,
	 * 		if dataColumnTypes is empty, or
	 * 		if a data column descriptions is not a known data type
	 */
	public StdDataArray(ArrayList<String> userColumnNames, ArrayList<DataColumnType> dataColumnTypes, 
			KnownDataTypes knownTypes) throws IllegalArgumentException {
		if ( (dataColumnTypes == null) || dataColumnTypes.isEmpty() )
			throw new IllegalArgumentException("no data column types given");
		numDataCols = dataColumnTypes.size();
		if ( userColumnNames.size() != numDataCols )
			throw new IllegalArgumentException("Different number of data column names (" + 
					userColumnNames.size() + ") and types (" +  numDataCols + ")");
		userColNames = new String[numDataCols];
		dataTypes = new DashDataType<?>[numDataCols];
		userUnits = new String[numDataCols];
		userMissVals = new String[numDataCols];
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
			userColNames[k] = userColumnNames.get(k);
			DataColumnType dataColType = dataColumnTypes.get(k);
			dataTypes[k] = knownTypes.getDataType(dataColType);
			if ( dataTypes[k] == null )
				throw new IllegalArgumentException("unknown data column type: " + 
						dataColType.getDisplayName());

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
				
			userUnits[k] = dataColType.getUnits().get(dataColType.getSelectedUnitIndex());
			if ( DashboardUtils.STRING_MISSING_VALUE.equals(userUnits[k]) )
				userUnits[k] = null;
			userMissVals[k] = dataColType.getSelectedMissingValue();
			if ( DashboardUtils.STRING_MISSING_VALUE.equals(userMissVals[k]) )
				userMissVals[k] = null;
		}
		standardized = new Boolean[numDataCols];
		for (int k = 0; k < numDataCols; k++)
			standardized[k] = null;
		numSamples = 0;
		stdObjects = null;
	}

	/**
	 * Create and assign the 2-D array of standard objects by interpreting the 
	 * list of lists of strings representations of these objects using data column 
	 * information provided in the constructor.  The list of lists of strings is 
	 * arranged such that each inner list gives each data column value for a 
	 * particular sample, and the outer list iterates through each sample.  
	 * <br /><br />
	 * Any data columns types matching {@link DashboardServerUtils#UNKNOWN} or 
	 * {@link DashboardServerUtils#OTHER} are ignored; {@link #getStdVal(int, int)} 
	 * will throw an IllegalArgumentException if a standard value is requested 
	 * from such a data column.
	 * <br /><br />
	 * No bounds checking of standardized data values is performed.
	 * 
	 * @param dataVals
	 * 		a list of list of data value strings where dataVals.get(j).get(k) is 
	 * 		the value of the k-th data column for the j-th sample.
	 * @return
	 * 		a list of automated data check messages describing problems (critical 
	 * 		errors) encountered when standardizing the data; never null but may 
	 * 		be empty.
	 * @throws IllegalArgumentException
	 * 		if there are no data samples (outer list is empty),
	 * 		if a required unit conversion is not supported, or
	 * 		if a standardizer for a given data type is not known
	 */
	public ArrayList<ADCMessage> standardizeData(ArrayList<ArrayList<String>> dataVals) 
													throws IllegalArgumentException {
		// Create the 2-D array 
		if ( dataVals.isEmpty() )
			throw new IllegalArgumentException("no data values given");
		numSamples = dataVals.size();
		stdObjects = new Object[numSamples][numDataCols];
		ArrayList<ADCMessage> msgList = new ArrayList<ADCMessage>();
		// Create a 2-D array of these Strings for efficiency
		String[][] strDataVals = new String[numSamples][numDataCols];
		for (int j = 0; j < numSamples; j++) {
			ArrayList<String> rowVals = dataVals.get(j);
			if ( rowVals.size() != numDataCols ) {
				// Generate a general message for this row - in case too long
				ADCMessage msg = new ADCMessage();
				msg.setSeverity(ADCMessage.SCMsgSeverity.CRITICAL);
				msg.setRowNumber(j+1);
				msg.setGeneralComment(INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG);
				msg.setDetailedComment(INCONSISTENT_NUMBER_OF_DATA_VALUES_MSG + "; " + 
						numDataCols + " expected but " + rowVals.size() + " found");
				msgList.add(msg);
				// Continue on, assuming the missing values are at the end
			}
			for (int k = 0; k < numDataCols; k++) {
				try {
					strDataVals[j][k] = rowVals.get(k);
				} catch ( IndexOutOfBoundsException ex ) {
					// Setting it to null will generate a "no value given" message
					strDataVals[j][k] = null;
				}
			}
		}
		// Standardize data columns that do not require values from other data columns
		boolean needsAnotherPass;
		do {
			needsAnotherPass = false;
			for (int k = 0; k < numDataCols; k++) {
				DashDataType<?> colType = dataTypes[k];
				if ( DashboardServerUtils.UNKNOWN.typeNameEquals(colType) ||
						DashboardServerUtils.OTHER.typeNameEquals(colType) ) {
					for (int j = 0; j < numSamples; j++) {
						stdObjects[j][k] = null;
					}
					standardized[k] = null;
				}
				else {
					try {
						ValueConverter<?> stdizer = colType.getStandardizer(userUnits[k], userMissVals[k], this);
						for (int j = 0; j < numSamples; j++) {
							try {
								stdObjects[j][k] = stdizer.convertValueOf(strDataVals[j][k]);
							} catch ( IllegalArgumentException ex ) {
								stdObjects[j][k] = null;
								ADCMessage msg = new ADCMessage();
								msg.setSeverity(ADCMessage.SCMsgSeverity.CRITICAL);
								msg.setRowNumber(j+1);
								msg.setColNumber(k+1);
								msg.setColName(userColNames[k]);
								msg.setGeneralComment(ex.getMessage());
								if ( strDataVals[j][k] == null )
									msg.setDetailedComment(ex.getMessage());
								else
									msg.setDetailedComment(ex.getMessage() + ": \"" + strDataVals[j][k] + "\"");
								msgList.add(msg);
							}
						}
						standardized[k] = true;
					} catch ( IllegalStateException ex ) {
						standardized[k] = false;
						needsAnotherPass = true;
					}
				}
			}
		} while ( needsAnotherPass );

		return msgList;
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
	 * @return
	 * 		an array containing the standardized longitudes; 
	 * 		missing values are null
	 * @throws IllegalStateException
	 * 		if there are no standardized longitudes
	 */
	public Double[] getSampleLongitudes() throws IllegalStateException {
		if ( (longitudeIndex < 0) || (longitudeIndex >= numDataCols) )
			throw new IllegalStateException("no longitude data column");
		if ( ! Boolean.TRUE.equals(standardized[longitudeIndex]) )
			throw new IllegalStateException("longitude data was not standardized");
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
		if ( (latitudeIndex < 0) || (latitudeIndex >= numDataCols) )
			throw new IllegalStateException("no latitude data column");
		if ( ! Boolean.TRUE.equals(standardized[latitudeIndex]) )
			throw new IllegalStateException("latitude data was not standardized");
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
		if ( (sampleDepthIndex < 0) || (sampleDepthIndex >= numDataCols) )
			throw new IllegalStateException("no sample depth data column");
		if ( ! Boolean.TRUE.equals(standardized[sampleDepthIndex]) )
			throw new IllegalStateException("sample depth data was not standardized");
		Double[] sampleDepths = new Double[numSamples];
		for (int j = 0; j < numSamples; j++)
			sampleDepths[j] = (Double) stdObjects[j][sampleDepthIndex];
		return sampleDepths;
	}

	/**
	 * @param idx
	 * 		index to test
	 * @return
	 * 		if the index is valid the corresponding data column has been standardized
	 */
	private boolean isUsableIndex(int idx) {
		if ( idx < 0 )
			return false;
		if ( idx >= numDataCols )
			return false;
		if ( ! Boolean.TRUE.equals(standardized[idx]) )
			return false;
		return true;
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
		GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

		if ( isUsableIndex(yearIndex) && isUsableIndex(monthOfYearIndex) && 
			 isUsableIndex(dayOfMonthIndex) && isUsableIndex(hourOfDayIndex) && 
			 isUsableIndex(minuteOfHourIndex) ) {
			// Get time using just year, month, day, hour, and minute; set second to zero
			for (int j = 0; j < numSamples; j++) {
				try {
					cal.clear();
					int year = ((Integer) stdObjects[j][yearIndex]).intValue();
					int month = ((Integer) stdObjects[j][monthOfYearIndex]).intValue();
					int day = ((Integer) stdObjects[j][dayOfMonthIndex]).intValue();
					int hour = ((Integer) stdObjects[j][hourOfDayIndex]).intValue();
					int min = ((Integer) stdObjects[j][minuteOfHourIndex]).intValue();
					cal.set(year,  month-1, day, hour, min, 0);
					sampleTimes[j] = cal.getTimeInMillis() / 1000.0;
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
					cal.clear();
					int year = ((Integer) stdObjects[j][yearIndex]).intValue();
					int month = ((Integer) stdObjects[j][monthOfYearIndex]).intValue();
					int day = ((Integer) stdObjects[j][dayOfMonthIndex]).intValue();
					String[] hms = ((String) stdObjects[j][timeOfDayIndex]).split(":");
					if ( hms.length > 3 )
						throw new Exception();
					int hour = Integer.parseInt(hms[0]);
					int min = Integer.parseInt(hms[1]);
					cal.set(year,  month-1, day, hour, min, 0);
					sampleTimes[j] = cal.getTimeInMillis() / 1000.0;

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
					cal.clear();
					int year = ((Integer) stdObjects[j][yearIndex]).intValue();
					cal.set(Calendar.YEAR, year);
					double dayOfYear = ((Double) stdObjects[j][yearIndex]).doubleValue();
					int intDayOfYear = (int) dayOfYear;
					cal.set(Calendar.DAY_OF_YEAR, intDayOfYear);
					sampleTimes[j] = cal.getTimeInMillis() / 1000.0;

					// add the fractional day in case the day of year is not an integer value
					sampleTimes[j] += (dayOfYear - (double) intDayOfYear) * 24.0 * 60.0 * 60.0;
					// add the seconds of day
					double secondOfDay = ((Double) stdObjects[j][secondOfDayIndex]).doubleValue();
					sampleTimes[j] += secondOfDay;
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
					cal.clear();
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
					cal.set(year,  month-1, day, hour, min, 0);
					sampleTimes[j] = cal.getTimeInMillis() / 1000.0;

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
					cal.clear();
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
					cal.set(year,  month-1, day, hour, min, 0);
					sampleTimes[j] = cal.getTimeInMillis() / 1000.0;

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
					cal.clear();
					String[] ymd = ((String) stdObjects[j][dateIndex]).split("-");
					if ( ymd.length != 3 )
						throw new Exception();
					int year = Integer.parseInt(ymd[0]);
					int month = Integer.parseInt(ymd[1]);
					int day = Integer.parseInt(ymd[2]);
					int hour = ((Integer) stdObjects[j][hourOfDayIndex]).intValue();
					int min = ((Integer) stdObjects[j][minuteOfHourIndex]).intValue();
					cal.set(year,  month-1, day, hour, min, 0);
					sampleTimes[j] = cal.getTimeInMillis() / 1000.0;
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
					cal.clear();
					int year = ((Integer) stdObjects[j][yearIndex]).intValue();
					cal.set(Calendar.YEAR, year);
					double dayOfYear = ((Double) stdObjects[j][yearIndex]).doubleValue();
					int intDayOfYear = (int) dayOfYear;
					cal.set(Calendar.DAY_OF_YEAR, intDayOfYear);
					sampleTimes[j] = cal.getTimeInMillis() / 1000.0;

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
	 * 		if either the sample index or the column index is invalid
	 * @throws IllegalArgumentException 
	 * 		if the value cannot be standardized
	 * @throws IllegalStateException 
	 * 		if the value has not been standardized
	 */
	public Object getStdVal(int sampleIdx, int columnIdx) 
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException {
		if ( (sampleIdx < 0) || (sampleIdx >= numSamples) )
			throw new IndexOutOfBoundsException("sample index is invalid: " + sampleIdx);
		if ( (columnIdx < 0) || (columnIdx >= numDataCols) )
			throw new IndexOutOfBoundsException("data column index is invalid: " + columnIdx);
		if ( standardized[columnIdx] == null )
			throw new IllegalArgumentException("value cannot be standardized");
		if ( ! standardized[columnIdx] )
			throw new IllegalStateException("value has not been standardized");
		return stdObjects[sampleIdx][columnIdx];
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = 0;
		for (int j = 0; j < numSamples; j++) {
			for (int k = 0; k < numDataCols; k++) {
				result *= prime;
				if ( stdObjects[j][k] != null )
					result += stdObjects[j][k].hashCode(); 
			}
		}
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
		for (int k = 0; k < numDataCols; k++) {
			result *= prime;
			if ( standardized[k] != null )
				result += standardized[k].hashCode();
		}
		for (int k = 0; k < numDataCols; k++) {
			result *= prime;
			if ( userMissVals[k] != null )
				result += userMissVals[k].hashCode();
		}
		for (int k = 0; k < numDataCols; k++) {
			result *= prime;
			if ( userUnits[k] != null )
				result += userUnits[k].hashCode();
		}
		for (int k = 0; k < numDataCols; k++) {
			result *= prime;
			if ( dataTypes[k] != null )
				result += dataTypes[k].hashCode();
		}
		for (int k = 0; k < numDataCols; k++) {
			result *= prime;
			if ( userColNames[k] != null )
				result += userColNames[k].hashCode();
		}
		result = prime * result + numDataCols;
		result = prime * result + numSamples;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof StdDataArray) ) {
			return false;
		}
		StdDataArray other = (StdDataArray) obj;
		if ( numSamples != other.numSamples ) {
			return false;
		}
		if ( numDataCols != other.numDataCols ) {
			return false;
		}

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

		for (int k = 0; k < numDataCols; k++) {
			if ( userColNames[k] == null ) {
				if ( other.userColNames[k] != null )
					return false;
			}
			else {
				if ( ! userColNames[k].equals(other.userColNames[k]) )
					return false;
			}
		}
		for (int k = 0; k < numDataCols; k++) {
			if ( userUnits[k] == null ) {
				if ( other.userUnits[k] != null )
					return false;
			}
			else {
				if ( ! userUnits[k].equals(other.userUnits[k]) )
					return false;
			}
		}
		for (int k = 0; k < numDataCols; k++) {
			if ( userMissVals[k] == null ) {
				if ( other.userMissVals[k] != null )
					return false;
			}
			else {
				if ( ! userMissVals[k].equals(other.userMissVals[k]) )
					return false;
			}
		}
		for (int k = 0; k < numDataCols; k++) {
			if ( standardized[k] == null ) {
				if ( other.standardized[k] != null )
					return false;
			}
			else {
				if ( ! standardized[k].equals(other.standardized[k]) )
					return false;
			}
		}
		for (int k = 0; k < numDataCols; k++) {
			if ( dataTypes[k] == null ) {
				if ( other.dataTypes[k] != null )
					return false;
			}
			else {
				if ( ! dataTypes[k].equals(other.dataTypes[k]) )
					return false;
			}
		}
		for (int j = 0; j < numSamples; j++) {
			for (int k = 0; k < numDataCols; k++) {
				if ( stdObjects[j][k] == null ) {
					if ( other.stdObjects[j][k] != null )
						return false;
				}
				else {
					if ( ! stdObjects[j][k].equals(other.stdObjects[j][k]) )
						return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String repr = "StdDataArray[numSamples=" + numSamples + ", numDataCols=" + numDataCols;
		List<String> namesList = Arrays.asList(userColNames);
		repr += "\n  userColNames=" + namesList.toString(); 
		List<DashDataType<?>> typesList = Arrays.asList(dataTypes);
		repr += "\n  userColTypes=" + typesList.toString(); 
		namesList = Arrays.asList(userUnits);
		repr += "\n  userUnits=" + namesList.toString();
		namesList = Arrays.asList(userMissVals);
		repr += "\n  userMissVals=" + namesList.toString();
		List<Boolean> boolList = Arrays.asList(standardized);

		repr += "\n  longitudeIndex=" + longitudeIndex;
		repr += ",  latitudeIndex=" + latitudeIndex;
		repr += ",  sampleDepthIndex=" + sampleDepthIndex;
		repr += ",  timestampIndex=" + timestampIndex;
		repr += ",  dateIndex=" + dateIndex;
		repr += ",  yearIndex=" + yearIndex;
		repr += ",  monthOfYearIndex=" + monthOfYearIndex;
		repr += ",  dayOfMonthIndex=" + dayOfMonthIndex;
		repr += ",  timeOfDayIndex=" + timeOfDayIndex;
		repr += ",  hourOfDayIndex=" + hourOfDayIndex;
		repr += ",  minuteOfHourIndex=" + minuteOfHourIndex;
		repr += ",  secondOfMinuteIndex=" + secondOfMinuteIndex;
		repr += ",  dayOfYearIndex=" + dayOfYearIndex;
		repr += ",  secondOfDayIndex=" + secondOfDayIndex;
		
		repr += "\n  standardized=" + boolList.toString();
		repr += "\n  stdObjects=[";
		for (int j = 0; j < numSamples; j++) {
			if ( j > 0 )
				repr += ",";
			repr += "\n    [ ";
			for (int k = 0; k < numDataCols; k++) {
				if ( k > 0 )
					repr += ", ";
				repr += String.valueOf(stdObjects[j][k]);
			}
			repr += " ]";
		}
		repr += "\n]";
		return repr;
	}

}
