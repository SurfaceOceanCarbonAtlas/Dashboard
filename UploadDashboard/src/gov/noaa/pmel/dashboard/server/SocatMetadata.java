/**
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class for working with metadata values of interest from SOCAT,
 * including those derived from cruise data.
 * 
 * @author Karl Smith
 */
public class SocatMetadata {

	// Maps of variable names to values
	TreeMap<DashDataType,String> stringValuesMap;
	TreeMap<DashDataType,Double> doubleValuesMap;
	TreeMap<DashDataType,Date> dateValuesMap;

	/**
	 * Generates a SocatMetadata object with the given known types.
	 * Only the data class types 
	 * 	{@link DashboardUtils#STRING_DATA_CLASS_NAME}, 
	 * 	{@link DashboardUtils#DOUBLE_DATA_CLASS_NAME}, and
	 * 	{@link DashboardUtils#DATE_DATA_CLASS_NAME}
	 * are accepted at this time.
	 * Sets the values to the default values:
	 * 	{@link DashboardUtils#CHAR_MISSING_VALUE}.toString() for QC_FLAG,
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
	public SocatMetadata(KnownDataTypes knownTypes) {
		if ( (knownTypes == null) || knownTypes.isEmpty() )
			throw new IllegalArgumentException("known data types cannot be null or empty");
		stringValuesMap = new TreeMap<DashDataType,String>();
		doubleValuesMap = new TreeMap<DashDataType,Double>();
		dateValuesMap = new TreeMap<DashDataType,Date>();

		for ( DashDataType dtype : knownTypes.getKnownTypesSet() ) {
			if ( DashboardUtils.STRING_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				if ( dtype.typeNameEquals(DashboardServerUtils.QC_FLAG) ) {
					// Single blank character for QC_FLAG
					stringValuesMap.put(dtype, DashboardUtils.CHAR_MISSING_VALUE.toString());
				}
				else {
					stringValuesMap.put(dtype, DashboardUtils.STRING_MISSING_VALUE);
				}
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
	 * 		the expocode; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getExpocode() {
		String value = stringValuesMap.get(DashboardServerUtils.EXPOCODE);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param expocode 
	 * 		the expocode to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setExpocode(String expocode) {
		String value;
		if ( expocode != null )
			value = expocode;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(DashboardServerUtils.EXPOCODE, value);
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
	 * 		the vessel name; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getVesselName() {
		String value = stringValuesMap.get(DashboardServerUtils.VESSEL_NAME);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param vesselName 
	 * 		the vessel name to set;
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setVesselName(String vesselName) {
		String value;
		if ( vesselName != null )
			value = vesselName;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(DashboardServerUtils.VESSEL_NAME, value);
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
	 * Get the SOCAT version - the version number followed by an 'N', 
	 * indicating the dataset is new in this SOCAT version, or a 'U', 
	 * indicating the dataset is an update from a previous SOCAT 
	 * version.  Updates within a SOCAT version do NOT change an 'N' 
	 * to a 'U'.
	 * 
	 * @return
	 * 		the SOCAT version associated with this instance; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getSocatVersion() {
		String value = stringValuesMap.get(SocatTypes.SOCAT_VERSION);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * Set the SOCAT version - the version number followed by an 'N', 
	 * indicating the dataset is new in this SOCAT version, or a 'U', 
	 * indicating the dataset is an update from a previous SOCAT 
	 * version.  Updates within a SOCAT version do NOT change an 'N' 
	 * to a 'U'.
	 * 
	 * @param version 
	 * 		the SOCAT version to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setSocatVersion(String socatVersion) {
		String value;
		if ( socatVersion != null )
			value = socatVersion;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(SocatTypes.SOCAT_VERSION, value);
	}

	/**
	 * @return
	 * 		the String of all region IDs;
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getAllRegionIDs() {
		String value = stringValuesMap.get(SocatTypes.ALL_REGION_IDS);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param allRegionIDs
	 * 		the String of all region IDs to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setAllRegionIDs(String allRegionIDs) {
		String value;
		if ( allRegionIDs != null )
			value = allRegionIDs;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(SocatTypes.ALL_REGION_IDS, value);
	}

	/**
	 * @return
	 * 		the SOCAT DOI for this dataset; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getSocatDOI() {
		String value = stringValuesMap.get(SocatTypes.SOCAT_DOI);
		if ( value == null )
			value = DashboardUtils.STRING_MISSING_VALUE;
		return value;
	}

	/**
	 * @param socatDOI
	 * 		the SOCAT DOI for this dataset to set; 
	 * 		if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
	 */
	public void setSocatDOI(String socatDOI) {
		String value;
		if ( socatDOI != null )
			value = socatDOI;
		else
			value = DashboardUtils.STRING_MISSING_VALUE;
		stringValuesMap.put(SocatTypes.SOCAT_DOI, value);
	}

	/**
	 * @return
	 * 		the QC flag;
	 * 		never null but could be {@link DashboardUtils#CHAR_MISSING_VALUE}.toString() if not assigned
	 */
	public String getQcFlag() {
		String value = stringValuesMap.get(DashboardServerUtils.QC_FLAG);
		if ( value == null )
			value = DashboardUtils.CHAR_MISSING_VALUE.toString();
		return value;
	}

	/**
	 * @param qcFlag 
	 * 		the QC flag to set; 
	 * 		if null, a string with a single blank character is assigned
	 */
	public void setQcFlag(String qcFlag) {
		String value;
		if ( qcFlag != null )
			value = qcFlag;
		else
			value = DashboardUtils.CHAR_MISSING_VALUE.toString();
		stringValuesMap.put(DashboardServerUtils.QC_FLAG, value);
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
		// Do not consider floating-point fields since they do not 
		// have to be exactly the same for equals to return true.
		final int prime = 37;
		int result = stringValuesMap.hashCode();
		result = result * prime + dateValuesMap.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;

		if ( ! (obj instanceof SocatMetadata) )
			return false;
		SocatMetadata other = (SocatMetadata) obj;

		// Date comparisons
		if ( ! dateValuesMap.equals(other.dateValuesMap) )
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
		String repr = "SocatMetadata[\n";
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
