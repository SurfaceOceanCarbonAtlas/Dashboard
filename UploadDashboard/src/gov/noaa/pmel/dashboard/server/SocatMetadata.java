/**
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class for working with metadata values of interest from SOCAT,
 * including those derived from cruise data.
 * 
 * @author Karl Smith
 */
public class SocatMetadata {

	public static final DashDataType SOCAT_VERSION = new DashDataType("socat_version", 
			KnownDataTypes.STRING_DATA_CLASS_NAME, "SOCAT Version number with status", 
			null, null, DataColumnType.NO_UNITS);
	public static final DashDataType ALL_REGION_IDS = new DashDataType("all_region_ids", 
			KnownDataTypes.STRING_DATA_CLASS_NAME, "Sorted unique region IDs", 
			null, null, DataColumnType.NO_UNITS);
	public static final DashDataType SOCAT_DOI = new DashDataType("socat_doi", 
			KnownDataTypes.STRING_DATA_CLASS_NAME, "SOCAT DOI", 
			null, null, DataColumnType.NO_UNITS);

	// Maps of variable names to values
	LinkedHashMap<DashDataType,String> stringValuesMap;
	LinkedHashMap<DashDataType,Double> doubleValuesMap;
	LinkedHashMap<DashDataType,Date> dateValuesMap;

	/**
	 * Generates a SocatMetadata object with the given known types.
	 * Only the data class types 
	 * 	{@link KnownDataTypes#STRING_DATA_CLASS_NAME}, 
	 * 	{@link KnownDataTypes#DOUBLE_DATA_CLASS_NAME}, and
	 * 	{@link KnownDataTypes#DATE_DATA_CLASS_NAME}
	 * are accepted at this time.
	 * Sets the values to the default values:
	 * 	{@link DashboardUtils#CHAR_MISSING_VALUE}.toString() for QC_FLAG,
	 * 	{@link DashboardUtils#STRING_MISSING_VALUE} for other 
	 * 		{@link KnownDataTypes#STRING_DATA_CLASS_NAME} values, 
	 * 	{@link DashboardUtils#FP_MISSING_VALUE} for 
	 * 		{@link KnownDataTypes#DOUBLE_DATA_CLASS_NAME} values, and
	 * 	{@link DashboardUtils#DATE_MISSING_VALUE} for 
	 * 		{@link KnownDataTypes#DATE_DATA_CLASS_NAME} values.
	 * 
	 * @param knownTypes
	 * 		all known data types;
	 * 		cannot be null or empty
	 */
	public SocatMetadata(KnownDataTypes knownTypes) {
		if ( (knownTypes == null) || (knownTypes.size() < 1) )
			throw new IllegalArgumentException("known data types cannot be null or empty");
		stringValuesMap = new LinkedHashMap<DashDataType,String>();
		doubleValuesMap = new LinkedHashMap<DashDataType,Double>();
		dateValuesMap = new LinkedHashMap<DashDataType,Date>();

		for ( DashDataType dtype : knownTypes.getKnownTypesSet() ) {
			if ( KnownDataTypes.STRING_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				if ( dtype.typeNameEquals(KnownDataTypes.QC_FLAG) ) {
					// Single blank character for QC_FLAG
					stringValuesMap.put(dtype, DashboardUtils.CHAR_MISSING_VALUE.toString());
				}
				else {
					stringValuesMap.put(dtype, DashboardUtils.STRING_MISSING_VALUE);
				}
			}
			else if ( KnownDataTypes.DOUBLE_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
				doubleValuesMap.put(dtype, DashboardUtils.FP_MISSING_VALUE);
			}
			else if ( KnownDataTypes.DATE_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
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
	public LinkedHashMap<DashDataType,String> getStringVariables() {
		return stringValuesMap;
	}

	/**
	 * @return
	 * 		the map of variable names and values for Double variables;
	 * 		the actual map in this instance is returned.
	 */
	public Map<DashDataType,Double> getDoubleVariables() {
		return doubleValuesMap;
	}

	/**
	 * @return
	 * 		the map of variable names and values for Date variables;
	 * 		the actual map in this instance is returned.
	 */
	public Map<DashDataType,Date> getDateVariables() {
		return dateValuesMap;
	}

	/**
	 * @return
	 * 		the expocode; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getExpocode() {
		String value = stringValuesMap.get(KnownDataTypes.EXPOCODE);
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
		stringValuesMap.put(KnownDataTypes.EXPOCODE, value);
	}

	/**
	 * @return
	 * 		the dataset name; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getDatasetName() {
		String value = stringValuesMap.get(KnownDataTypes.DATASET_NAME);
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
		stringValuesMap.put(KnownDataTypes.DATASET_NAME, value);
	}

	/**
	 * @return
	 * 		the vessel name; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getVesselName() {
		String value = stringValuesMap.get(KnownDataTypes.VESSEL_NAME);
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
		stringValuesMap.put(KnownDataTypes.VESSEL_NAME, value);
	}

	/**
	 * @return
	 * 		the name of the organization/institution;
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getOrganizationName() {
		String value = stringValuesMap.get(KnownDataTypes.ORGANIZATION_NAME);
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
		stringValuesMap.put(KnownDataTypes.ORGANIZATION_NAME, value);
	}

	/**
	 * @return
	 * 		the investigator names;
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getInvestigatorNames() {
		String value = stringValuesMap.get(KnownDataTypes.INVESTIGATOR_NAMES);
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
		stringValuesMap.put(KnownDataTypes.INVESTIGATOR_NAMES, value);
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
		String value = stringValuesMap.get(SOCAT_VERSION);
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
		stringValuesMap.put(SOCAT_VERSION, value);
	}

	/**
	 * @return
	 * 		the String of all region IDs;
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getAllRegionIDs() {
		String value = stringValuesMap.get(ALL_REGION_IDS);
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
		stringValuesMap.put(ALL_REGION_IDS, value);
	}

	/**
	 * @return
	 * 		the SOCAT DOI for this dataset; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getSocatDOI() {
		String value = stringValuesMap.get(SOCAT_DOI);
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
		stringValuesMap.put(SOCAT_DOI, value);
	}

	/**
	 * @return
	 * 		the QC flag;
	 * 		never null but could be {@link DashboardUtils#CHAR_MISSING_VALUE}.toString() if not assigned
	 */
	public String getQcFlag() {
		String value = stringValuesMap.get(KnownDataTypes.QC_FLAG);
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
		stringValuesMap.put(KnownDataTypes.QC_FLAG, value);
	}

	/**
	 * @return
	 * 		the west-most longitude for the cruise;
	 * 		never null could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getWestmostLongitude() {
		Double value = doubleValuesMap.get(KnownDataTypes.WESTERNMOST_LONGITUDE);
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
		doubleValuesMap.put(KnownDataTypes.WESTERNMOST_LONGITUDE, value);
	}

	/**
	 * @return
	 * 		the east-most longitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getEastmostLongitude() {
		Double value = doubleValuesMap.get(KnownDataTypes.EASTERNMOST_LONGITUDE);
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
		doubleValuesMap.put(KnownDataTypes.EASTERNMOST_LONGITUDE, value);
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getSouthmostLatitude() {
		Double value = doubleValuesMap.get(KnownDataTypes.SOUTHERNMOST_LATITUDE);
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
		doubleValuesMap.put(KnownDataTypes.SOUTHERNMOST_LATITUDE, value);
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getNorthmostLatitude() {
		Double value = doubleValuesMap.get(KnownDataTypes.NORTHERNMOST_LATITUDE);
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
		doubleValuesMap.put(KnownDataTypes.NORTHERNMOST_LATITUDE, value);
	}

	/**
	 * @return
	 * 		the beginning time for the cruise;
	 * 		never null but could be {@link DashboardUtils#DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getBeginTime() {
		Date value = dateValuesMap.get(KnownDataTypes.TIME_COVERAGE_START);
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
		dateValuesMap.put(KnownDataTypes.TIME_COVERAGE_START, value);
	}

	/**
	 * @return
	 * 		the ending time for the cruise;
	 * 		never null but could be {@link DashboardUtils#DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getEndTime() {
		Date value = dateValuesMap.get(KnownDataTypes.TIME_COVERAGE_END);
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
		dateValuesMap.put(KnownDataTypes.TIME_COVERAGE_END, value);
	}

	/**
	 * @return
	 * 		the maximum length of String values given in the fields 
	 * 		of this instance, rounded up to the nearest multiple of 16
	 * 		(and never less than 16).
	 */
	public int getMaxStringLength() {
		int maxLength = 16;
		for ( String value : stringValuesMap.values() ) {
			if ( maxLength < value.length() )
				maxLength = value.length();
		}
		maxLength += (maxLength % 16);
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
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getValue() + "\n";
		for ( Entry<DashDataType,Double> entry : doubleValuesMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getKey().toString() + "\n";
		for ( Entry<DashDataType,Date> entry : dateValuesMap.entrySet() )
			repr += "    " + entry.getKey().getVarName() + "=" + entry.getKey().toString() + "\n";
		repr += "]";
		return repr;
	}

}
