/**
 */
package gov.noaa.pmel.dashboard.server;


import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class for working with data values of interest, both PI-provided
 * values and computed values, from a SOCAT cruise data measurement.
 * Note that WOCE flags are ignored in the hashCode and equals methods.
 * 
 * @author Karl Smith
 */
public class DsgData {

	private TreeMap<DashDataType,Integer> intValsMap;
	private TreeMap<DashDataType,Character> charValsMap;
	private TreeMap<DashDataType,Double> doubleValsMap;

	/**
	 * Generates a DsgData object with the given known types.
	 * Only the data class types 
	 * 	{@link DashboardUtils#CHAR_DATA_CLASS_NAME},
	 * 	{@link DashboardUtils#INT_DATA_CLASS_NAME}, and 
	 * 	{@link DashboardUtils#DOUBLE_DATA_CLASS_NAME}
	 * are accepted at this time.
	 * Sets the values to the default values:
	 * 	{@link DashboardUtils#WOCE_NOT_CHECKED} for WOCE flags (starts with "WOCE_"),
	 * 	{@link DashboardUtils#CHAR_MISSING_VALUE} for other {@link DashboardUtils#CHAR_DATA_CLASS_NAME} values.
	 * 	{@link DashboardUtils#INT_MISSING_VALUE} for {@link DashboardUtils#INT_DATA_CLASS_NAME} values, and
	 * 	{@link DashboardUtils#FP_MISSING_VALUE} for {@link DashboardUtils#DOUBLE_DATA_CLASS_NAME} values
	 * 
	 * @param knownTypes
	 * 		collection of all known types;
	 * 		cannot be null or empty
	 */
	public DsgData(KnownDataTypes knownTypes) {
		if ( (knownTypes == null) || knownTypes.isEmpty() )
			throw new IllegalArgumentException("known data types cannot be null or empty");
		intValsMap = new TreeMap<DashDataType,Integer>();
		charValsMap = new TreeMap<DashDataType,Character>();
		doubleValsMap = new TreeMap<DashDataType,Double>();

		for ( DashDataType dtype : knownTypes.getKnownTypesSet() ) {
			if ( DashboardUtils.INT_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				intValsMap.put(dtype, DashboardUtils.INT_MISSING_VALUE);
			}
			else if ( DashboardUtils.CHAR_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				if ( dtype.isWoceType() ) {
					// WOCE flag
					charValsMap.put(dtype, DashboardUtils.FLAG_UNKNOWN);
				}
				else {
					charValsMap.put(dtype, DashboardUtils.CHAR_MISSING_VALUE);
				}
			}
			else if ( DashboardUtils.DOUBLE_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				doubleValsMap.put(dtype, DashboardUtils.FP_MISSING_VALUE);
			}
			else {
				throw new IllegalArgumentException("Unknown data class name '" + 
						dtype.getDataClassName() + "' associated with type '" + dtype.getVarName() + "'");
			}
		}
	}

	/**
	 * Creates from a list of data column types and corresponding data strings.
	 * This assumes the data in the strings are in the standard units for each
	 * type, and the missing value is "NaN", an empty string, or null.
	 * 
	 * An exception is thrown if a data column with type 
	 * {@link DashboardServerUtils#UNKNOWN} is encountered; otherwise data columns
	 * with types not present in knownTypes are ignored.  The data types
	 * {@link DashDataType#UNKNOWN}, {@link DashDataType#OTHER}, and any
	 * metadata types should not be in knownTypes.
	 * 
	 * @param knownTypes
	 * 		list of known data types
	 * @param columnTypes
	 * 		types of the data values - only the variable name and data class 
	 * 		type is used
	 * @param sampleNum
	 * 		sequence number (starting with one) of this sample in the data set
	 * @param dataValues
	 * 		data value strings
	 * @throws IllegalArgumentException
	 * 		if the number of data types and data values do not match, 
	 * 		if a data column has the type {@link DashDataType#UNKNOWN}, 
	 * 		if a data column has a type matching a known data type but
	 * 			with a different data class type, or
	 * 		if a data value string cannot be parsed for the expected type 
	 */
	public DsgData(KnownDataTypes knownTypes, List<DashDataType> columnTypes, 
			int sampleNum, List<String> dataValues) throws IllegalArgumentException {
		// Initialize to an empty data record with the given known types
		this(knownTypes);
		// Verify the number of types and values match
		int numColumns = columnTypes.size();
		if ( dataValues.size() != numColumns )
			throw new IllegalArgumentException("Number of column types (" +
					numColumns + ") does not match the number of data values (" +
					dataValues.size() + ")");
		// Add values to the empty record
		if ( intValsMap.containsKey(DashboardServerUtils.SAMPLE_NUMBER) )
			intValsMap.put(DashboardServerUtils.SAMPLE_NUMBER, Integer.valueOf(sampleNum));
		for (int k = 0; k < numColumns; k++) {
			// Make sure the data type is valid
			DashDataType dtype = columnTypes.get(k);
			if ( DashboardServerUtils.UNKNOWN.typeNameEquals(dtype) )
				throw new IllegalArgumentException("Data column number " + 
						Integer.toString(k+1) + " has type UNKNOWN");
			// Skip over missing values since the empty data record
			// is initialized with the missing value for data type
			String value = dataValues.get(k);
			if ( (value == null) || value.isEmpty() || value.equals("NaN") )
				continue;
			// Check if this data type is in the known list
			DataColumnType stdType = knownTypes.getDataColumnType(dtype.getVarName());
			if ( stdType == null )
				continue;
			if ( ! stdType.getDataClassName().equals(dtype.getDataClassName()) )
				throw new IllegalArgumentException("Data column type " + dtype.getVarName() + 
						" has data class " + dtype.getDataClassName() + 
						" instead of " + stdType.getDataClassName());
			// Assign the value
			if ( intValsMap.containsKey(dtype) ) {
				try {
					intValsMap.put(dtype, Integer.parseInt(value));
				} catch ( Exception ex ) {
					throw new IllegalArgumentException("Unable to parse '" + 
							value + "' as an Integer: " + ex.getMessage());
				}
			}
			else if ( charValsMap.containsKey(dtype) ) {
				if ( value.length() != 1 )
					throw new IllegalArgumentException("More than one character in '" + value + "'");
				charValsMap.put(dtype, value.charAt(0));
			}
			else if ( doubleValsMap.containsKey(dtype) ) {
				try {
					doubleValsMap.put(dtype, Double.parseDouble(value));
				} catch ( Exception ex ) {
					throw new IllegalArgumentException("Unable to parse '" + 
							value + "' as a Double: " + ex.getMessage());
				}
			}
			else {
				throw new RuntimeException("Unexpected failure to place data type " + dtype.toString());
			}
		}
	}

	/**
	 * Creates a list of these data objects from the values and data column
	 * types given in a dashboard cruise with data.  This assumes the data
	 * is in the standard units for each type, and the missing value is
	 * "NaN", and empty string, or null.
	 * 
	 * An exception is thrown if a data column with type 
	 * {@link DashDataType#UNKNOWN} is encountered; otherwise data columns
	 * with types not present in knownTypes are ignored.  The data types
	 * {@link DashDataType#UNKNOWN}, {@link DashDataType#OTHER}, and any
	 * metadata types should not be in knownTypes.
	 * 
	 * @param knownTypes
	 * 		list of known data types
	 * @param cruise
	 * 		dashboard cruise with data
	 * @return
	 * 		list of these data objects
	 * @throws IllegalArgumentException
	 * 		if a row of data values has an unexpected number of values,
	 * 		if a data column has the type {@link DashDataType#UNKNOWN}, 
	 * 		if a data column has a type matching a known data type but
	 * 			with a different data class type, or
	 * 		if a data value string cannot be parsed for the expected type 
	 */
	public static ArrayList<DsgData> dataListFromDashboardCruise(
			KnownDataTypes knownTypes, DashboardDatasetData cruise) 
					throws IllegalArgumentException {
		// Get the required data from the cruise
		ArrayList<ArrayList<String>> dataValsTable = cruise.getDataValues();
		ArrayList<DataColumnType> dataColTypes = cruise.getDataColTypes();
		// Create the list of DashDataType objects - assumes data already standardized
		ArrayList<DashDataType> dataTypes = new ArrayList<DashDataType>(dataColTypes.size());
		for ( DataColumnType dctype : dataColTypes )
			dataTypes.add( new DashDataType(dctype) );
		// Create the list of DSG cruise data objects, 
		// and populate it with data from each row of the table
		ArrayList<DsgData> dsgDataList = new ArrayList<DsgData>(dataValsTable.size());
		for (int k = 0; k < dataValsTable.size(); k++) {
			dsgDataList.add( new DsgData(knownTypes, dataTypes, k+1, dataValsTable.get(k)) );
		}
		return dsgDataList;
	}

	/**
	 * @return
	 * 		the map of variable names and values for Integer variables;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<DashDataType,Integer> getIntegerVariables() {
		return intValsMap;
	}

	/**
	 * Updates the given Integer type variable with the given value.
	 * 
	 * @param dtype
	 * 		the data type of the value
	 * @param value
	 * 		the value to assign; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 * @throws IllegalArgumentException
	 * 		if the data type variable is not a known data type in this data
	 */
	public void setIntegerVariableValue(DashDataType dtype, Integer value) throws IllegalArgumentException {
		if ( ! intValsMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown data double variable " + dtype.getVarName());
		if ( value == null )
			intValsMap.put(dtype, DashboardUtils.INT_MISSING_VALUE);
		else
			intValsMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the map of variable names and values for String variables;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<DashDataType,Character> getCharacterVariables() {
		return charValsMap;
	}

	/**
	 * Updates the given Character type variable with the given value.
	 * 
	 * @param dtype
	 * 		the data type of the value
	 * @param value
	 * 		the value to assign; 
	 * 		if null, {@link DashboardUtils#CHAR_MISSING_VALUE} is assigned
	 * @throws IllegalArgumentException
	 * 		if the data type variable is not a known data type in this data
	 */
	public void setCharacterVariableValue(DashDataType dtype, Character value) throws IllegalArgumentException {
		if ( ! charValsMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown data character variable " + dtype.getVarName());
		if ( value == null )
			charValsMap.put(dtype, DashboardUtils.CHAR_MISSING_VALUE);
		else
			charValsMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the map of variable names and values for Double variables;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<DashDataType,Double> getDoubleVariables() {
		return doubleValsMap;
	}

	/**
	 * Updates the given Double type variable with the given value.
	 * 
	 * @param dtype
	 * 		the data type of the value
	 * @param value
	 * 		the value to assign; 
	 * 		if null, NaN, or infinite, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 * @throws IllegalArgumentException
	 * 		if the data type variable is not a known data type in this data
	 */
	public void setDoubleVariableValue(DashDataType dtype, Double value) throws IllegalArgumentException {
		if ( ! doubleValsMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown data double variable " + dtype.getVarName());
		if ( (value == null) || value.isNaN() || value.isInfinite() )
			doubleValsMap.put(dtype, DashboardUtils.FP_MISSING_VALUE);
		else
			doubleValsMap.put(dtype, value);
	}

	/**
	 * @return 
	 * 		the sample number; 
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned or not positive
	 */
	public Integer getSampleNumber() {
		Integer value = intValsMap.get(DashboardServerUtils.SAMPLE_NUMBER);
		if ( (value == null) || (value < 1) )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param sampleNumber 
	 * 		the sample number to set; 
	 * 		if null or not positive, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setSampleNumber(Integer sampleNumber) {
		Integer value = sampleNumber;
		if ( (value == null) || (value < 1) )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(DashboardServerUtils.SAMPLE_NUMBER, value);
	}

	/**
	 * @return 
	 * 		the year of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getYear() {
		Integer value = intValsMap.get(DashboardServerUtils.YEAR);
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param year 
	 * 		the year of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setYear(Integer year) {
		Integer value = year;
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(DashboardServerUtils.YEAR, value);
	}

	/**
	 * @return 
	 * 		the month of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getMonth() {
		Integer value = intValsMap.get(DashboardServerUtils.MONTH_OF_YEAR);
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param month 
	 * 		the month of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setMonth(Integer month) {
		Integer value = month;
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(DashboardServerUtils.MONTH_OF_YEAR, value);
	}

	/**
	 * @return 
	 * 		the day of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getDay() {
		Integer value = intValsMap.get(DashboardServerUtils.DAY_OF_MONTH);
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param day 
	 * 		the day of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setDay(Integer day) {
		Integer value = day;
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(DashboardServerUtils.DAY_OF_MONTH, value);
	}

	/**
	 * @return 
	 * 		the hour of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getHour() {
		Integer value = intValsMap.get(DashboardServerUtils.HOUR_OF_DAY);
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param hour 
	 * 		the hour of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setHour(Integer hour) {
		Integer value = hour;
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(DashboardServerUtils.HOUR_OF_DAY, value);
	}

	/**
	 * @return 
	 * 		the minute of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#INT_MISSING_VALUE} if not assigned
	 */
	public Integer getMinute() {
		Integer value = intValsMap.get(DashboardServerUtils.MINUTE_OF_HOUR);
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		return value;
	}

	/**
	 * @param minute 
	 * 		the minute of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 */
	public void setMinute(Integer minute) {
		Integer value = minute;
		if ( value == null )
			value = DashboardUtils.INT_MISSING_VALUE;
		intValsMap.put(DashboardServerUtils.MINUTE_OF_HOUR, value);
	}

	/**
	 * @return 
	 * 		the second of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getSecond() {
		Double value = doubleValsMap.get(DashboardServerUtils.SECOND_OF_MINUTE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param second 
	 * 		the second of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setSecond(Double second) {
		Double value = second;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(DashboardServerUtils.SECOND_OF_MINUTE, value);
	}

	/**
	 * @return 
	 * 		the longitude of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getLongitude() {
		Double value = doubleValsMap.get(DashboardServerUtils.LONGITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param longitude 
	 * 		the longitude of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setLongitude(Double longitude) {
		Double value = longitude;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(DashboardServerUtils.LONGITUDE, value);
	}

	/**
	 * @return 
	 * 		the latitude of the data measurement; 
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getLatitude() {
		Double value = doubleValsMap.get(DashboardServerUtils.LATITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param latitude 
	 * 		the latitude of the data measurement to set; 
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setLatitude(Double latitude) {
		Double value = latitude;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(DashboardServerUtils.LATITUDE, value);
	}

	/**
	 * @return 
	 * 		the sampling depth;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned
	 */
	public Double getSampleDepth() {
		Double value = doubleValsMap.get(DashboardServerUtils.SAMPLE_DEPTH);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param sampleDepth
	 * 		the sampling depth to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setSampleDepth(Double sampleDepth) {
		Double value = sampleDepth;
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValsMap.put(DashboardServerUtils.SAMPLE_DEPTH, value);
	}

	/**
	 * @return
	 * 		the generic WOCE flag;
	 * 		never null but could be {@link DashboardUtils#WOCE_NOT_CHECKED} if not assigned.
	 */
	public Character getGenericWoceFlag() {
		Character value = charValsMap.get(DashboardServerUtils.GENERIC_WOCE_FLAG);
		if ( value == null )
			value = DashboardUtils.WOCE_NOT_CHECKED;
		return value;
	}

	/**
	 * @param genericWoceFlag
	 * 		the generic WOCE flag to set;
	 * 		if null, {@link DashboardUtils#WOCE_NOT_CHECKED} is assigned.
	 */
	public void setGenericWoceFlag(Character genericWoceFlag) {
		Character value = genericWoceFlag;
		if ( value == null )
			value = DashboardUtils.WOCE_NOT_CHECKED;
		charValsMap.put(DashboardServerUtils.GENERIC_WOCE_FLAG, value);
	}

	@Override 
	public int hashCode() {
		final int prime = 37;
		int result = intValsMap.hashCode();
		// Ignore WOCE flag differences.
		TreeMap<DashDataType,Character> nonWoceCharValsMap = 
				new TreeMap<DashDataType,Character>();
		for ( Entry<DashDataType,Character> entry : charValsMap.entrySet() ) {
			if ( ! entry.getKey().isWoceType() ) {
				nonWoceCharValsMap.put(entry.getKey(), entry.getValue());
			}
		}
		result = result * prime + nonWoceCharValsMap.hashCode();
		// Do not use floating-point fields since they do not 
		// have to be exactly the same for equals to return true.
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof DsgData) )
			return false;
		DsgData other = (DsgData) obj;

		// Integer comparisons
		if ( ! intValsMap.equals(other.intValsMap) )
			return false;

		// Character comparisons - ignore WOCE flag differences
		if ( ! charValsMap.keySet().equals(other.charValsMap.keySet()) )
			return false;
		for ( Entry<DashDataType,Character> entry : charValsMap.entrySet() ) {
			DashDataType dtype = entry.getKey();
			if ( ! dtype.isWoceType() ) {
				if ( ! entry.getValue().equals(other.charValsMap.get(dtype)) )
					return false;
			}
		}

		// Floating-point comparisons - values don't have to be exactly the same
		if ( ! doubleValsMap.keySet().equals(other.doubleValsMap.keySet()) )
			return false;
		for ( Entry<DashDataType,Double> entry : doubleValsMap.entrySet() ) {
			DashDataType dtype = entry.getKey();
			Double thisval = entry.getValue();
			Double otherval = other.doubleValsMap.get(dtype);

			if ( dtype.typeNameEquals(DashboardServerUtils.SECOND_OF_MINUTE) ) {
				// Match seconds not given (FP_MISSING_VALUE) with zero seconds
				if ( ! DashboardUtils.closeTo(thisval, otherval, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
					if ( ! ( thisval.equals(DashboardUtils.FP_MISSING_VALUE) && otherval.equals(Double.valueOf(0.0)) ) ) {
						if ( ! ( thisval.equals(Double.valueOf(0.0)) && otherval.equals(DashboardUtils.FP_MISSING_VALUE) ) ) {
							return false;
						}
					}
				}
			}
			else if ( dtype.getVarName().toUpperCase().contains("LONGITUDE") ) {
				// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
				if ( ! DashboardUtils.longitudeCloseTo(thisval, otherval, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
					return false;				
			}
			else {
				if ( ! DashboardUtils.closeTo(thisval, otherval, 
						DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR) )
					return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		String repr = "DsgData[\n";
		for ( Entry<DashDataType,Integer> entry : intValsMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getValue().toString() + "\n";
		for ( Entry<DashDataType,Character> entry : charValsMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "='" + entry.getValue().toString() + "'\n";
		for ( Entry<DashDataType,Double> entry : doubleValsMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getValue().toString() + "\n";
		repr += "]";
		return repr;
	}

}
