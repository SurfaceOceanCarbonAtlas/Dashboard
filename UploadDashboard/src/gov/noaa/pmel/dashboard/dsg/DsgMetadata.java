/**
 */
package gov.noaa.pmel.dashboard.dsg;

import java.util.Map.Entry;
import java.util.TreeMap;

import gov.noaa.pmel.dashboard.datatype.CharDashDataType;
import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

/**
 * Class for working with metadata values of interest,
 * including those derived from dataset data.
 * 
 * @author Karl Smith
 */
public class DsgMetadata {

	// Maps of variable names to values
	TreeMap<StringDashDataType,String> stringValuesMap;
	TreeMap<CharDashDataType,Character> charValuesMap;
	TreeMap<IntDashDataType,Integer> intValuesMap;
	TreeMap<DoubleDashDataType,Double> doubleValuesMap;

	/**
	 * Generates a DsgMetata object with the given known types.  Only the 
	 * data types {@link CharDashDataType}, {@link StringDashDataType}, 
	 * {@link IntDashDataType}, and {@link DoubleDashDataType} are accepted 
	 * at this time.  Sets the values to the default values for each type: 
	 * {@link DashboardUtils#STRING_MISSING_VALUE} for {@link StringDashDataType}, 
	 * {@link DashboardUtils#CHAR_MISSING_VALUE} for {@link CharDashDataType}, 
	 * {@link DashboardUtils#INT_MISSING_VALUE} for {@link IntDashDataType}, and 
	 * {@link DashboardUtils#FP_MISSING_VALUE} for {@link DoubleDashDataType}.
	 * 
	 * @param knownTypes
	 * 		collection of all known metadata types; cannot be null or empty
	 */
	public DsgMetadata(KnownDataTypes knownTypes) {
		if ( (knownTypes == null) || knownTypes.isEmpty() )
			throw new IllegalArgumentException("known metadata types cannot be null or empty");
		stringValuesMap = new TreeMap<StringDashDataType,String>();
		charValuesMap = new TreeMap<CharDashDataType,Character>();
		intValuesMap = new TreeMap<IntDashDataType,Integer>();
		doubleValuesMap = new TreeMap<DoubleDashDataType,Double>();

		for ( DashDataType<?> dtype : knownTypes.getKnownTypesSet() ) {
			if ( dtype instanceof StringDashDataType ) {
				stringValuesMap.put((StringDashDataType) dtype, DashboardUtils.STRING_MISSING_VALUE);
			}
			else if ( dtype instanceof CharDashDataType ) {
				charValuesMap.put((CharDashDataType) dtype, DashboardUtils.CHAR_MISSING_VALUE);
			}
			else if ( dtype instanceof IntDashDataType ) {
				intValuesMap.put((IntDashDataType) dtype, DashboardUtils.INT_MISSING_VALUE);
			}
			else if ( dtype instanceof DoubleDashDataType ) {
				doubleValuesMap.put((DoubleDashDataType) dtype, DashboardUtils.FP_MISSING_VALUE);
			}
			else {
				throw new IllegalArgumentException("Unknown metadata class name \"" + 
						dtype.getDataClassName() + "\" associated with type \"" + 
						dtype.getVarName() + "\"");
			}
		}
	}

	/**
	 * @return
	 * 		the map of variable names and values for String variables;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<StringDashDataType,String> getStringVariables() {
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
	public void setStringVariableValue(StringDashDataType dtype, String value) throws IllegalArgumentException {
		if ( ! stringValuesMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown metadata string variable " + dtype.getVarName());
		if ( value == null )
			stringValuesMap.put(dtype, DashboardUtils.STRING_MISSING_VALUE);
		else
			stringValuesMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the map of variable names and values for character variables;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<CharDashDataType,Character> getCharVariables() {
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
	public void setCharVariableValue(CharDashDataType dtype, Character value) 
											throws IllegalArgumentException {
		if ( ! charValuesMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown metadata character variable " + dtype.getVarName());
		if ( value == null )
			charValuesMap.put(dtype, DashboardUtils.CHAR_MISSING_VALUE);
		else
			charValuesMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the map of variable names and values for integer variables;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<IntDashDataType,Integer> getIntVariables() {
		return intValuesMap;
	}

	/**
	 * Updates the given integer type variable with the given value.
	 * 
	 * @param dtype
	 * 		the data type of the value
	 * @param value
	 * 		the value to assign; 
	 * 		if null, {@link DashboardUtils#INT_MISSING_VALUE} is assigned
	 * @throws IllegalArgumentException
	 * 		if the data type variable is not a known data type in this metadata
	 */
	public void setIntVariableValue(IntDashDataType dtype, Integer value) 
											throws IllegalArgumentException {
		if ( ! intValuesMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown metadata integer variable " + dtype.getVarName());
		if ( value == null )
			intValuesMap.put(dtype, DashboardUtils.INT_MISSING_VALUE);
		else
			intValuesMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the map of variable names and values for Double variables;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<DoubleDashDataType,Double> getDoubleVariables() {
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
	public void setDoubleVariableValue(DoubleDashDataType dtype, Double value) throws IllegalArgumentException {
		if ( ! doubleValuesMap.containsKey(dtype) )
			throw new IllegalArgumentException("Unknown metadata double variable " + dtype.getVarName());
		if ( (value == null) || value.isNaN() || value.isInfinite() )
			doubleValuesMap.put(dtype, DashboardUtils.FP_MISSING_VALUE);
		else
			doubleValuesMap.put(dtype, value);
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
	 * 		the beginning time for the cruise, in units of "seconds since 1970-01-01T00:00:00";
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned.
	 */
	public Double getBeginTime() {
		Double value = doubleValuesMap.get(DashboardServerUtils.TIME_COVERAGE_START);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param beginTime 
	 * 		the beginning time for the cruise to set, in units of "seconds since 1970-01-01T00:00:00";
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setBeginTime(Double beginTime) {
		Double value;
		if ( beginTime != null )
			value = beginTime;
		else
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValuesMap.put(DashboardServerUtils.TIME_COVERAGE_START, value);
	}

	/**
	 * @return
	 * 		the ending time for the cruise, in units of "seconds since 1970-01-01T00:00:00";
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned.
	 */
	public Double getEndTime() {
		Double value = doubleValuesMap.get(DashboardServerUtils.TIME_COVERAGE_END);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param endTime 
	 * 		the ending time for the cruise to set, in units of "seconds since 1970-01-01T00:00:00";
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setEndTime(Double endTime) {
		Double value;
		if ( endTime != null )
			value = endTime;
		else
			value = DashboardUtils.FP_MISSING_VALUE;
		doubleValuesMap.put(DashboardServerUtils.TIME_COVERAGE_END, value);
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
		int result = stringValuesMap.hashCode();
		result = result * prime + charValuesMap.hashCode();
		result = result * prime + intValuesMap.hashCode();
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

		// String comparisons
		if ( ! stringValuesMap.equals(other.stringValuesMap) )
			return false;

		// Character comparisons
		if ( ! charValuesMap.equals(other.charValuesMap) )
			return false;

		// Integer comparisons
		if ( ! intValuesMap.equals(other.intValuesMap) )
			return false;

		// Floating-point comparisons - values don't have to be exactly the same
		if ( ! doubleValuesMap.keySet().equals(other.doubleValuesMap.keySet()) )
			return false;

		for ( Entry<DoubleDashDataType,Double> entry : doubleValuesMap.entrySet() ) {
			DoubleDashDataType dtype = entry.getKey();
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
		for ( Entry<StringDashDataType,String> entry : stringValuesMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=\"" + entry.getValue() + "\"\n";
		for ( Entry<CharDashDataType,Character> entry : charValuesMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=\'" + entry.getValue().toString() + "\'\n";
		for ( Entry<IntDashDataType,Integer> entry : intValuesMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getValue().toString() + "\n";
		for ( Entry<DoubleDashDataType,Double> entry : doubleValuesMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getValue().toString() + "\n";
		repr += "]";
		return repr;
	}

}
