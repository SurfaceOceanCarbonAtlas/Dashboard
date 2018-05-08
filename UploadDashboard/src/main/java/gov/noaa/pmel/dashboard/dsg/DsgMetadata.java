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

	// Maps of variable types to values
	TreeMap<DashDataType<?>,Object> valuesMap;

	/**
	 * Create with the given data types.  Sets all values for these types 
	 * to null, which corresponds to the missing value for each type.
	 * The data types given must be known subclasses of DashDataType valid 
	 * for metadata files: {@link StringDashDataType}, {@link CharDashDataType}, 
	 * {@link IntDashDataType}, or {@link DoubleDashDataType}.
	 * 
	 * @param knownTypes
	 * 		all known metadata types; cannot be null or empty
	 * @throws IllegalArgumentException
	 * 		if no data types are given, or
	 * 		if the data type not a known subclass type 
	 */
	public DsgMetadata(KnownDataTypes knownTypes) throws IllegalArgumentException {
		if ( (knownTypes == null) || knownTypes.isEmpty() )
			throw new IllegalArgumentException("no known metadata types");
		valuesMap = new TreeMap<DashDataType<?>,Object>();
		for ( DashDataType<?> dtype : knownTypes.getKnownTypesSet() ) {
			if ( ! ( (dtype instanceof StringDashDataType) ||
					 (dtype instanceof CharDashDataType) ||
					 (dtype instanceof IntDashDataType) ||
					 (dtype instanceof DoubleDashDataType) ) )
				throw new IllegalArgumentException("unknown data type for metadata: " + dtype.toString());
			valuesMap.put(dtype, null);
		}
	}

	/**
	 * @return
	 * 		the map of (metadata) data types to values;
	 * 		the actual map in this instance is returned.
	 */
	public TreeMap<DashDataType<?>,Object> getValuesMap() {
		return valuesMap;
	}

	/**
	 * Updates the value of the given (metadata) data type 
	 * to the given value.
	 * 
	 * @param dtype
	 * 		the data type of the value
	 * @param value
	 * 		the value to assign
	 * @throws IllegalArgumentException
	 * 		if the data type is not a known data type in this metadata, or
	 * 		if the value is not an appropriate object for this data type
	 */
	public void setValue(DashDataType<?> dtype, Object value) throws IllegalArgumentException {
		if ( ! valuesMap.containsKey(dtype) )
			throw new IllegalArgumentException("unknown metadata type " + dtype.toString()); 
		if ( dtype instanceof StringDashDataType ) {
			if ( (value != null) && ! (value instanceof String) )
				throw new IllegalArgumentException("invalid value (" + value + ") for data type " + dtype.toString());
		}
		else if ( dtype instanceof CharDashDataType ) {
			if ( (value != null) && ! (value instanceof Character) )
				throw new IllegalArgumentException("invalid value (" + value + ") for data type " + dtype.toString());
		}
		else if ( dtype instanceof IntDashDataType ) {
			if ( (value != null) && ! (value instanceof Integer) )
				throw new IllegalArgumentException("invalid value (" + value + ") for data type " + dtype.toString());
		}
		else if ( dtype instanceof DoubleDashDataType ) {
			if ( (value != null) && ! (value instanceof Double) )
				throw new IllegalArgumentException("invalid value (" + value + ") for data type " + dtype.toString());
		}
		else {
			// Should not happen since this type was found in valuesMap
			throw new IllegalArgumentException("unknown data type for metadata: " + dtype.toString());
		}
		valuesMap.put(dtype, value);
	}

	/**
	 * @return
	 * 		the dataset ID; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getDatasetId() {
		String value = (String) valuesMap.get(DashboardServerUtils.DATASET_ID);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param datasetId 
	 * 		the dataset ID to set; always successful
	 */
	public void setDatasetId(String datasetId) {
		valuesMap.put(DashboardServerUtils.DATASET_ID, datasetId);
	}

	/**
	 * @return
	 * 		the dataset name; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getDatasetName() {
		String value = (String) valuesMap.get(DashboardServerUtils.DATASET_NAME);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param datasetName
	 * 		the dataset name to set; always successful
	 */
	public void setDatasetName(String datasetName) {
		valuesMap.put(DashboardServerUtils.DATASET_NAME, datasetName);
	}

	/**
	 * @return
	 * 		the platform name; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getPlatformName() {
		String value = (String) valuesMap.get(DashboardServerUtils.PLATFORM_NAME);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param platformName 
	 * 		the platform name to set; always successful
	 */
	public void setPlatformName(String platformName) {
		valuesMap.put(DashboardServerUtils.PLATFORM_NAME, platformName);
	}

	/**
	 * @return
	 * 		the name of the organization/institution;
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getOrganizationName() {
		String value = (String) valuesMap.get(DashboardServerUtils.ORGANIZATION_NAME);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param organizationName 
	 * 		the name of the organization/institution to set; always successful
	 */
	public void setOrganizationName(String organizationName) {
		valuesMap.put(DashboardServerUtils.ORGANIZATION_NAME, organizationName);
	}

	/**
	 * @return
	 * 		the investigator names;
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getInvestigatorNames() {
		String value = (String) valuesMap.get(DashboardServerUtils.INVESTIGATOR_NAMES);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param investigatorNames 
	 * 		the investigator names to set; always successful
	 */
	public void setInvestigatorNames(String investigatorNames) {
		valuesMap.put(DashboardServerUtils.INVESTIGATOR_NAMES, investigatorNames);
	}

	/**
	 * @return
	 * 		the platform type; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getPlatformType() {
		String value = (String) valuesMap.get(DashboardServerUtils.PLATFORM_TYPE);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param platformType 
	 * 		the platform type to set; always successful
	 */
	public void setPlatformType(String platformType) {
		valuesMap.put(DashboardServerUtils.PLATFORM_TYPE, platformType);
	}

	/**
	 * @return
	 * 		the version associated with this instance; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getVersion() {
		String value = (String) valuesMap.get(DashboardServerUtils.VERSION);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param version 
	 * 		the version to set; always successful
	 */
	public void setVersion(String version) {
		valuesMap.put(DashboardServerUtils.VERSION, version);
	}

	/**
	 * @return
	 * 		the west-most longitude for the cruise;
	 * 		never null could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getWestmostLongitude() {
		Double value = (Double) valuesMap.get(DashboardServerUtils.WESTERNMOST_LONGITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param westmostLongitude 
	 * 		the west-most longitude to set; always successful
	 */
	public void setWestmostLongitude(Double westmostLongitude) {
		valuesMap.put(DashboardServerUtils.WESTERNMOST_LONGITUDE, westmostLongitude);
	}

	/**
	 * @return
	 * 		the east-most longitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getEastmostLongitude() {
		Double value = (Double) valuesMap.get(DashboardServerUtils.EASTERNMOST_LONGITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param eastmostLongitude 
	 * 		the east-most longitude to set; always successful
	 */
	public void setEastmostLongitude(Double eastmostLongitude) {
		valuesMap.put(DashboardServerUtils.EASTERNMOST_LONGITUDE, eastmostLongitude);
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getSouthmostLatitude() {
		Double value = (Double) valuesMap.get(DashboardServerUtils.SOUTHERNMOST_LATITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param southmostLatitude 
	 * 		the south-most latitude to set; always successful
	 */
	public void setSouthmostLatitude(Double southmostLatitude) {
		valuesMap.put(DashboardServerUtils.SOUTHERNMOST_LATITUDE, southmostLatitude);
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getNorthmostLatitude() {
		Double value = (Double) valuesMap.get(DashboardServerUtils.NORTHERNMOST_LATITUDE);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param northmostLatitude 
	 * 		the north-most latitude to set; always successful
	 */
	public void setNorthmostLatitude(Double northmostLatitude) {
		valuesMap.put(DashboardServerUtils.NORTHERNMOST_LATITUDE, northmostLatitude);
	}

	/**
	 * @return
	 * 		the beginning time for the cruise, in units of "seconds since 1970-01-01T00:00:00";
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getBeginTime() {
		Double value = (Double) valuesMap.get(DashboardServerUtils.TIME_COVERAGE_START);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param beginTime 
	 * 		the beginning time for the cruise to set, in units of 
	 * 		"seconds since 1970-01-01T00:00:00"; always successful
	 */
	public void setBeginTime(Double beginTime) {
		valuesMap.put(DashboardServerUtils.TIME_COVERAGE_START, beginTime);
	}

	/**
	 * @return
	 * 		the ending time for the cruise, in units of "seconds since 1970-01-01T00:00:00";
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getEndTime() {
		Double value = (Double) valuesMap.get(DashboardServerUtils.TIME_COVERAGE_END);
		if ( value == null )
			value = DashboardUtils.FP_MISSING_VALUE;
		return value;
	}

	/**
	 * @param endTime 
	 * 		the ending time for the cruise to set, in units of 
	 * 		"seconds since 1970-01-01T00:00:00"; always successful
	 */
	public void setEndTime(Double endTime) {
		valuesMap.put(DashboardServerUtils.TIME_COVERAGE_END, endTime);
	}

	/**
	 * @return
	 * 		the maximum length of String values given in the fields 
	 * 		of this instance, rounded up to the nearest multiple of 32
	 * 		(and never less than 32).
	 */
	public int getMaxStringLength() {
		int maxLength = 32;
		for ( Entry<DashDataType<?>,Object> entry : valuesMap.entrySet() ) {
			if ( entry.getKey() instanceof StringDashDataType ) {
				String value = (String) entry.getValue();
				if ( (value != null) && (maxLength < value.length()) )
					maxLength = value.length();
			}
		}
		maxLength = 32 * ((maxLength + 31) / 32);
		return maxLength;
	}

	@Override 
	public int hashCode() {
		int result = 0;
		for ( Entry<DashDataType<?>,Object> entry : valuesMap.entrySet() ) {
			// Consider only the keys of the floating-point fields set
			// since floating point values do not have to be exactly 
			// the same for equals to return true. 
			DashDataType<?> key = entry.getKey();
			if ( key instanceof DoubleDashDataType ) {
				result = result + key.hashCode();
			}
			else {
				result = result + entry.hashCode();
			}
		}
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

		if ( ! valuesMap.keySet().equals(other.valuesMap.keySet()) )
			return false;

		for ( Entry<DashDataType<?>,Object> entry : valuesMap.entrySet() ) {
			DashDataType<?> key = entry.getKey();
			if ( key instanceof DoubleDashDataType ) {
				// Floating-point comparisons - values don't have to be exactly the same
				Double thisVal = (Double) entry.getValue();
				Double otherVal = (Double) other.valuesMap.get(key);
				if ( thisVal == null ) {
					if ( otherVal != null )
						return false;
				}
				else if ( otherVal == null ) {
					return false;
				}
				else {
					if ( key.getVarName().toUpperCase().contains("LONGITUDE") ) {
						// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
						if ( ! DashboardUtils.longitudeCloseTo(thisVal, otherVal, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
							return false;				
					}
					else {
						if ( ! DashboardUtils.closeTo(thisVal, otherVal, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
							return false;
					}
				}
			}
			else {
				Object thisVal = entry.getValue();
				Object otherVal = other.valuesMap.get(key);
				if ( thisVal == null ) {
					if ( otherVal != null )
						return false;
				}
				else if ( otherVal == null ) {
					return false;
				}
				else if ( ! thisVal.equals(otherVal) ) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public String toString() {
		String repr = "DsgMetadata[\n";
		for ( Entry<DashDataType<?>,Object> entry : valuesMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=\"" + entry.getValue() + "\"\n";
		repr += "]";
		return repr;
	}

}
