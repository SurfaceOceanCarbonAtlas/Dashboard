/**
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class for working with metadata values of interest,
 * including those derived from dataset data.
 * 
 * @author Karl Smith
 */
public class DsgMetadata {

	// Maps of variable names to values
	TreeMap<DashDataType,Character> charValuesMap;
	TreeMap<DashDataType,String> stringValuesMap;
	TreeMap<DashDataType,Double> doubleValuesMap;
	TreeMap<DashDataType,Date> dateValuesMap;

	/**
	 * Generates a DsgMetadata object with the given known types.
	 * Only the data class types 
	 * 	{@link DashboardUtils#CHAR_DATA_CLASS_NAME}, 
	 * 	{@link DashboardUtils#STRING_DATA_CLASS_NAME}, 
	 * 	{@link DashboardUtils#DOUBLE_DATA_CLASS_NAME}, and
	 * 	{@link DashboardUtils#DATE_DATA_CLASS_NAME}
	 * are accepted at this time.
	 * Sets the values to the default values:
	 * 	{@link DashboardUtils#CHAR_MISSING_VALUE} for other 
	 * 		{@link DashboardUtils#CHAR_DATA_CLASS_NAME} values, 
	 * 	{@link DashboardUtils#STRING_MISSING_VALUE} for other 
	 * 		{@link DashboardUtils#STRING_DATA_CLASS_NAME} values, 
	 * 	{@link DashboardUtils#FP_MISSING_VALUE} for 
	 * 		{@link DashboardUtils#DOUBLE_DATA_CLASS_NAME} values, and
	 * 	{@link DashboardUtils#DATE_MISSING_VALUE} for 
	 * 		{@link DashboardUtils#DATE_DATA_CLASS_NAME} values.
	 * 
	 * @param knownTypes
	 * 		all known data types;
	 * 		cannot be null or empty
	 */
	public DsgMetadata(KnownDataTypes knownTypes) {
		if ( (knownTypes == null) || knownTypes.isEmpty() )
			throw new IllegalArgumentException("known data types cannot be null or empty");
		charValuesMap = new TreeMap<DashDataType,Character>();
		stringValuesMap = new TreeMap<DashDataType,String>();
		doubleValuesMap = new TreeMap<DashDataType,Double>();
		dateValuesMap = new TreeMap<DashDataType,Date>();

		for ( DashDataType dtype : knownTypes.getKnownTypesSet() ) {
			if ( DashboardUtils.CHAR_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				charValuesMap.put(dtype, DashboardUtils.CHAR_MISSING_VALUE);
			}
			else if ( DashboardUtils.STRING_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				stringValuesMap.put(dtype, DashboardUtils.STRING_MISSING_VALUE);
			}
			else if ( DashboardUtils.DOUBLE_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				doubleValuesMap.put(dtype, DashboardUtils.FP_MISSING_VALUE);
			}
			else if ( DashboardUtils.DATE_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				dateValuesMap.put(dtype, DashboardUtils.DATE_MISSING_VALUE);
			}
			else {
				throw new IllegalArgumentException("Unknown data class name '" + 
						dtype.getDataClassName() + "' associated with type '" + dtype.getVarName() + "'");
			}
		}
	}

	/**
	 * @return
	 * 		the map of variable names and values for character variables;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<DashDataType,Character> getCharVariables() {
		return charValuesMap;
	}

	/**
	 * Updates the given character type variable with the given value.
	 * 
	 * @param dtype
	 * 		the data type of the value
	 * @param value
	 * 		the value to assign; 
	 * 		if null, {@link DashboardUtils#CHAR_MISSING_VALUE} is assigned
	 * @throws IllegalArgumentException
	 * 		if the data type variable is not a known data type in this metadata
	 */
	public void setCharVariableValue(DashDataType dtype, Character value) throws IllegalArgumentException {
		if ( ! charValuesMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown metadata character variable " + dtype.getVarName());
		if ( value == null )
			charValuesMap.put(dtype, DashboardUtils.CHAR_MISSING_VALUE);
		else
			charValuesMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the map of variable names and values for String variables;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<DashDataType,String> getStringVariables() {
		return stringValuesMap;
	}

	/**
	 * Updates the given String type variable with the given value.
	 * 
	 * @param dtype
	 * 		the data type of the value
	 * @param value
	 * 		the value to assign; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 * @throws IllegalArgumentException
	 * 		if the data type variable is not a known data type in this metadata
	 */
	public void setStringVariableValue(DashDataType dtype, String value) throws IllegalArgumentException {
		if ( ! stringValuesMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown metadata string variable " + dtype.getVarName());
		if ( value == null )
			stringValuesMap.put(dtype, DashboardUtils.STRING_MISSING_VALUE);
		else
			stringValuesMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the map of variable names and values for Double variables;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<DashDataType,Double> getDoubleVariables() {
		return doubleValuesMap;
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
	 * 		if the data type variable is not a known data type in this metadata
	 */
	public void setDoubleVariableValue(DashDataType dtype, Double value) throws IllegalArgumentException {
		if ( ! doubleValuesMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown metadata double variable " + dtype.getVarName());
		if ( (value == null) || value.isNaN() || value.isInfinite() )
			doubleValuesMap.put(dtype, DashboardUtils.FP_MISSING_VALUE);
		else
			doubleValuesMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the map of variable names and values for Date variables;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<DashDataType,Date> getDateVariables() {
		return dateValuesMap;
	}

	/**
	 * Updates the given Date type variable with the given value.
	 * 
	 * @param dtype
	 * 		the data type of the value
	 * @param value
	 * 		the value to assign; 
	 * 		if null, {@link DashboardUtils#DATE_MISSING_VALUE} is assigned
	 * @throws IllegalArgumentException
	 * 		if the data type variable is not a known data type in this metadata
	 */
	public void setDateVariableValue(DashDataType dtype, Date value) throws IllegalArgumentException {
		if ( ! dateValuesMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown metadata Date variable " + dtype.getVarName());
		if ( value == null )
			dateValuesMap.put(dtype, DashboardUtils.DATE_MISSING_VALUE);
		else
			dateValuesMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the dataset ID; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getDatasetId() {
		String value = stringValuesMap.get(DashboardServerUtils.DATASET_ID);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param datasetId 
	 * 		the dataset ID to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setDatasetId(String datasetId) {
		String value;
		if ( datasetId != null )
			value = datasetId;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(DashboardServerUtils.DATASET_ID, value);
	}

	/**
	 * @return
	 * 		the dataset name; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getDatasetName() {
		String value = stringValuesMap.get(DashboardServerUtils.DATASET_NAME);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param datasetName
	 * 		the dataset name to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setDatasetName(String datasetName) {
		String value;
		if ( datasetName != null )
			value = datasetName;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(DashboardServerUtils.DATASET_NAME, value);
	}

	/**
	 * @return
	 * 		the platform name; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getPlatformName() {
		String value = stringValuesMap.get(DashboardServerUtils.PLATFORM_NAME);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param platformName 
	 * 		the platform name to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setPlatformName(String platformName) {
		String value;
		if ( platformName != null )
			value = platformName;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(DashboardServerUtils.PLATFORM_NAME, value);
	}

	/**
	 * @return
	 * 		the name of the organization/institution;
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getOrganizationName() {
		String value = stringValuesMap.get(DashboardServerUtils.ORGANIZATION_NAME);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param organizationName 
	 * 		the name of the organization/institution to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setOrganizationName(String organizationName) {
		String value;
		if ( organizationName != null )
			value = organizationName;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(DashboardServerUtils.ORGANIZATION_NAME, value);
	}

	/**
	 * @return
	 * 		the investigator names;
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getInvestigatorNames() {
		String value = stringValuesMap.get(DashboardServerUtils.INVESTIGATOR_NAMES);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param investigatorNames 
	 * 		the investigator names to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setInvestigatorNames(String investigatorNames) {
		String value;
		if ( investigatorNames != null )
			value = investigatorNames;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(DashboardServerUtils.INVESTIGATOR_NAMES, value);
	}

	/**
	 * @return
	 * 		the platform type; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getPlatformType() {
		String value = stringValuesMap.get(DashboardServerUtils.PLATFORM_TYPE);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param platformType 
	 * 		the platform type to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setPlatformType(String platformType) {
		String value;
		if ( platformType != null )
			value = platformType;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(DashboardServerUtils.PLATFORM_TYPE, value);
	}

	/**
	 * Get the version status - the version number followed by an 'N', 
	 * indicating the dataset is new in this version, or a 'U', 
	 * indicating the dataset is an update from a previous 
	 * version.  Updates within a version do NOT change an 'N' 
	 * to a 'U'.
	 * 
	 * @return
	 * 		the version associated with this instance; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getVersion() {
		String value = stringValuesMap.get(DashboardServerUtils.VERSION);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * Set the version status - the version number followed by an 'N', 
	 * indicating the dataset is new in this version, or a 'U', 
	 * indicating the dataset is an update from a previous 
	 * version.  Updates within a version do NOT change an 'N' 
	 * to a 'U'.
	 * 
	 * @param version 
	 * 		the version to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setVersion(String version) {
		String value;
		if ( version != null )
			value = version;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(DashboardServerUtils.VERSION, value);
	}

	/**
	 * @return
	 * 		the west-most longitude for the cruise;
	 * 		never null could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getWestmostLongitude() {
		Double value = doubleValuesMap.get(DashboardServerUtils.WESTERNMOST_LONGITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param westmostLongitude 
	 * 		the west-most longitude to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setWestmostLongitude(Double westmostLongitude) {
		Double value;
		if ( westmostLongitude != null )
			value = westmostLongitude;
		else
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValuesMap.put(DashboardServerUtils.WESTERNMOST_LONGITUDE, value);
	}

	/**
	 * @return
	 * 		the east-most longitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getEastmostLongitude() {
		Double value = doubleValuesMap.get(DashboardServerUtils.EASTERNMOST_LONGITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param eastmostLongitude 
	 * 		the east-most longitude to set;
	 * 		if null, {@link DashboardUtils.FP_MISSING_VALUE} is assigned
	 */
	public void setEastmostLongitude(Double eastmostLongitude) {
		Double value;
		if ( eastmostLongitude != null )
			value = eastmostLongitude;
		else
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValuesMap.put(DashboardServerUtils.EASTERNMOST_LONGITUDE, value);
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getSouthmostLatitude() {
		Double value = doubleValuesMap.get(DashboardServerUtils.SOUTHERNMOST_LATITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param southmostLatitude 
	 * 		the south-most latitude to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setSouthmostLatitude(Double southmostLatitude) {
		Double value;
		if ( southmostLatitude != null )
			value = southmostLatitude;
		else
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValuesMap.put(DashboardServerUtils.SOUTHERNMOST_LATITUDE, value);
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getNorthmostLatitude() {
		Double value = doubleValuesMap.get(DashboardServerUtils.NORTHERNMOST_LATITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param northmostLatitude 
	 * 		the north-most latitude to set;
	 * 		if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
	 */
	public void setNorthmostLatitude(Double northmostLatitude) {
		Double value;
		if ( northmostLatitude != null )
			value = northmostLatitude;
		else
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValuesMap.put(DashboardServerUtils.NORTHERNMOST_LATITUDE, value);
	}

	/**
	 * @return
	 * 		the beginning time for the cruise;
	 * 		never null but could be {@link DashboardUtils#DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getBeginTime() {
		Date value = dateValuesMap.get(DashboardServerUtils.TIME_COVERAGE_START);
		if ( value == null )
			value = DashboardUtils.DATE_MISSING_VALUE;
		return value;
	}

	/**
	 * @param beginTime 
	 * 		the beginning time for the cruise to set;
	 * 		if null, {@link DashboardUtils#DATE_MISSING_VALUE} is assigned
	 */
	public void setBeginTime(Date beginTime) {
		Date value;
		if ( beginTime != null )
			value = beginTime;
		else
			value = DashboardUtils.DATE_MISSING_VALUE;
		dateValuesMap.put(DashboardServerUtils.TIME_COVERAGE_START, value);
	}

	/**
	 * @return
	 * 		the ending time for the cruise;
	 * 		never null but could be {@link DashboardUtils#DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getEndTime() {
		Date value = dateValuesMap.get(DashboardServerUtils.TIME_COVERAGE_END);
		if ( value == null )
			value = DashboardUtils.DATE_MISSING_VALUE;
		return value;
	}

	/**
	 * @param endTime 
	 * 		the ending time for the cruise to set;
	 * 		if null, {@link DashboardUtils#DATE_MISSING_VALUE} is assigned
	 */
	public void setEndTime(Date endTime) {
		Date value;
		if ( endTime != null )
			value = endTime;
		else
			value = DashboardUtils.DATE_MISSING_VALUE;
		dateValuesMap.put(DashboardServerUtils.TIME_COVERAGE_END, value);
	}

	/**
	 * @return
	 * 		the maximum length of String values given in the fields 
	 * 		of this instance, rounded up to the nearest multiple of 32
	 * 		(and never less than 32).
	 */
	public int getMaxStringLength() {
		int maxLength = 32;
		for ( String value : stringValuesMap.values() ) {
			if ( maxLength < value.length() )
				maxLength = value.length();
		}
		maxLength = 32 * ((maxLength + 31) / 32);
		return maxLength;
	}

	@Override 
	public int hashCode() {
		final int prime = 37;
		int result = charValuesMap.hashCode();
		result = result * prime + stringValuesMap.hashCode();
		result = result * prime + dateValuesMap.hashCode();
		// Consider only the keys of the floating-point fields set
		// since floating point values do not have to be exactly 
		// the same for equals to return true. 
		result = result * prime + doubleValuesMap.keySet().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof DsgMetadata) )
			return false;
		DsgMetadata other = (DsgMetadata) obj;

		// Date comparisons
		if ( ! dateValuesMap.equals(other.dateValuesMap) )
			return false;

		// Character comparisons
		if ( ! charValuesMap.equals(other.charValuesMap) )
			return false;

		// String comparisons
		if ( ! stringValuesMap.equals(other.stringValuesMap) )
			return false;

		// Floating-point comparisons - values don't have to be exactly the same
		if ( ! doubleValuesMap.keySet().equals(other.doubleValuesMap.keySet()) )
			return false;

		for ( Entry<DashDataType,Double> entry : doubleValuesMap.entrySet() ) {
			DashDataType dtype = entry.getKey();
			Double thisval = entry.getValue();
			Double otherval = other.doubleValuesMap.get(dtype);
			if ( dtype.getVarName().toUpperCase().contains("LONGITUDE") ) {
				// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
				if ( ! DashboardUtils.longitudeCloseTo(thisval, otherval, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
					return false;				
			}
			else {
				if ( ! DashboardUtils.closeTo(thisval, otherval, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
					return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		String repr = "DsgMetadata[\n";
		for ( Entry<DashDataType,Character> entry : charValuesMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=\"" + entry.getValue() + "\"\n";
		for ( Entry<DashDataType,String> entry : stringValuesMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=\"" + entry.getValue() + "\"\n";
		for ( Entry<DashDataType,Double> entry : doubleValuesMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getValue().toString() + "\n";
		for ( Entry<DashDataType,Date> entry : dateValuesMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getValue().toString() + "\n";
		repr += "]";
		return repr;
	}

}
