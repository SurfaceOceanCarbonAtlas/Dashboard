/**
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.util.Collections;
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

	public static final String SOCAT_VERSION_VARNAME = "socat_version";
	public static final String ALL_REGION_IDS_VARNAME = "all_region_ids";
	public static final String SOCAT_DOI_VARNAME = "socat_doi";

	// Maps of variable names to values
	LinkedHashMap<String,String> stringValuesMap;
	LinkedHashMap<String,Double> doubleValuesMap;
	LinkedHashMap<String,Date> dateValuesMap;

	/*
	stringValuesMap.put(KnownDataTypes.EXPOCODE.getVarName(), "");
	stringValuesMap.put(KnownDataTypes.DATASET_NAME.getVarName(), "");
	stringValuesMap.put(KnownDataTypes.VESSEL_NAME.getVarName(), "");
	stringValuesMap.put(KnownDataTypes.ORGANIZATION_NAME.getVarName(), "");
	stringValuesMap.put(KnownDataTypes.INVESTIGATOR_NAMES.getVarName(), "");
	stringValuesMap.put(SOCAT_VERSION_VARNAME, "");
	stringValuesMap.put(ALL_REGION_IDS_VARNAME, "");
	stringValuesMap.put(SOCAT_DOI_VARNAME, "");
	stringValuesMap.put(KnownDataTypes.QC_FLAG.getVarName(), " ");
	doubleValuesMap.put(KnownDataTypes.WESTERNMOST_LONGITUDE.getVarName(), DashboardUtils.FP_MISSING_VALUE);
	doubleValuesMap.put(KnownDataTypes.EASTERNMOST_LONGITUDE.getVarName(), DashboardUtils.FP_MISSING_VALUE);
	doubleValuesMap.put(KnownDataTypes.SOUTHERNMOST_LATITUDE.getVarName(), DashboardUtils.FP_MISSING_VALUE);
	doubleValuesMap.put(KnownDataTypes.NORTHERNMOST_LATITUDE.getVarName(), DashboardUtils.FP_MISSING_VALUE);
	dateValuesMap.put(KnownDataTypes.TIME_COVERAGE_START.getVarName(), DashboardUtils.DATE_MISSING_VALUE);
	dateValuesMap.put(KnownDataTypes.TIME_COVERAGE_END.getVarName(), DashboardUtils.DATE_MISSING_VALUE);
	*/

	/**
	 * Generates a SocatMetadata object with the given known types.
	 * Only the data class types 
	 * 	{@link KnownDataTypes#STRING_DATA_CLASS_NAME}, 
	 * 	{@link KnownDataTypes#DOUBLE_DATA_CLASS_NAME}, and 
	 * 	{@link KnownDataTypes#DATE_DATA_CLASS_NAME} are
	 * accepted at this time.
	 * Sets the values to the default values:
	 * 	{@link DashboardUtils#CHAR_MISSING_VALUE}.toString() for QC_FLAG,
	 * 	{@link DashboardUtils#STRING_MISSING_VALUE} for other {@link KnownDataTypes#STRING_DATA_CLASS_NAME} values, 
	 * 	{@link DashboardUtils#FP_MISSING_VALUE} for {@link KnownDataTypes#DOUBLE_DATA_CLASS_NAME} values, and
	 * 	{@link DashboardUtils#DATE_MISSING_VALUE} for {@link KnownDataTypes#DATE_DATA_CLASS_NAME} values.
	 * 
	 * @param knownTypes
	 * 		all known data types
	 */
	public SocatMetadata(KnownDataTypes knownTypes) {
		stringValuesMap = new LinkedHashMap<String,String>();
		doubleValuesMap = new LinkedHashMap<String,Double>();
		dateValuesMap = new LinkedHashMap<String,Date>();

		if ( knownTypes != null ) {
			for ( DataColumnType dtype : knownTypes.getKnownTypesList() ) {
				if ( KnownDataTypes.STRING_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
					if ( dtype.typeEquals(KnownDataTypes.QC_FLAG) ) {
						// Single blank character for QC_FLAG
						stringValuesMap.put(dtype.getVarName(), DashboardUtils.CHAR_MISSING_VALUE.toString());
					}
					else {
						stringValuesMap.put(dtype.getVarName(), DashboardUtils.STRING_MISSING_VALUE);
					}
				}
				else if ( KnownDataTypes.DOUBLE_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
					doubleValuesMap.put(dtype.getVarName(), DashboardUtils.FP_MISSING_VALUE);
				}
				else if ( KnownDataTypes.DATE_DATA_CLASS_NAME.equals(dtype.getDataClassName()) ) {
					dateValuesMap.put(dtype.getVarName(), DashboardUtils.DATE_MISSING_VALUE);
				}
				else {
					throw new IllegalArgumentException("Unknown data class name '" + 
							dtype.getDataClassName() + "' for type '" + dtype.getVarName() + "'");
				}
			}
		}
	}

	/**
	 * @return
	 * 		the map of variable names and values for String variables;
	 * 		a read-only view of the internal map is returned.
	 */
	public Map<String,String> getStringVariables() {
		return Collections.unmodifiableMap(stringValuesMap);
	}

	/**
	 * @return
	 * 		the map of variable names and values for Double variables;
	 * 		a read-only view of the internal map is returned.
	 */
	public Map<String,Double> getDoubleVariables() {
		return Collections.unmodifiableMap(doubleValuesMap);
	}

	/**
	 * @return
	 * 		the map of variable names and values for Date variables;
	 * 		a read-only view of the internal map is returned.
	 */
	public Map<String,Date> getDateVariables() {
		return Collections.unmodifiableMap(dateValuesMap);
	}

	/**
	 * @return
	 * 		the expocode; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getExpocode() {
		String value = stringValuesMap.get(KnownDataTypes.EXPOCODE.getVarName());
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
		stringValuesMap.put(KnownDataTypes.EXPOCODE.getVarName(), value);
	}

	/**
	 * @return
	 * 		the dataset name; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getDatasetName() {
		String value = stringValuesMap.get(KnownDataTypes.DATASET_NAME.getVarName());
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
		stringValuesMap.put(KnownDataTypes.DATASET_NAME.getVarName(), value);
	}

	/**
	 * @return
	 * 		the vessel name; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getVesselName() {
		String value = stringValuesMap.get(KnownDataTypes.VESSEL_NAME.getVarName());
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
		stringValuesMap.put(KnownDataTypes.VESSEL_NAME.getVarName(), value);
	}

	/**
	 * @return
	 * 		the name of the organization/institution;
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getOrganizationName() {
		String value = stringValuesMap.get(KnownDataTypes.ORGANIZATION_NAME.getVarName());
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
		stringValuesMap.put(KnownDataTypes.ORGANIZATION_NAME.getVarName(), value);
	}

	/**
	 * @return
	 * 		the investigator names;
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getInvestigatorNames() {
		String value = stringValuesMap.get(KnownDataTypes.INVESTIGATOR_NAMES.getVarName());
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
		stringValuesMap.put(KnownDataTypes.INVESTIGATOR_NAMES.getVarName(), value);
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
		String value = stringValuesMap.get(SOCAT_VERSION_VARNAME);
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
		stringValuesMap.put(SOCAT_VERSION_VARNAME, value);
	}

	/**
	 * @return
	 * 		the String of all region IDs;
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getAllRegionIDs() {
		String value = stringValuesMap.get(ALL_REGION_IDS_VARNAME);
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
		stringValuesMap.put(ALL_REGION_IDS_VARNAME, value);
	}

	/**
	 * @return
	 * 		the SOCAT DOI for this dataset; 
	 * 		never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
	 */
	public String getSocatDOI() {
		String value = stringValuesMap.get(SOCAT_DOI_VARNAME);
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
		stringValuesMap.put(SOCAT_DOI_VARNAME, value);
	}

	/**
	 * @return
	 * 		the QC flag;
	 * 		never null but could be {@link DashboardUtils#CHAR_MISSING_VALUE}.toString() if not assigned
	 */
	public String getQcFlag() {
		String value = stringValuesMap.get(KnownDataTypes.QC_FLAG.getVarName());
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
		stringValuesMap.put(KnownDataTypes.QC_FLAG.getVarName(), value);
	}

	/**
	 * @return
	 * 		the west-most longitude for the cruise;
	 * 		never null could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getWestmostLongitude() {
		Double value = doubleValuesMap.get(KnownDataTypes.WESTERNMOST_LONGITUDE.getVarName());
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
		doubleValuesMap.put(KnownDataTypes.WESTERNMOST_LONGITUDE.getVarName(), value);
	}

	/**
	 * @return
	 * 		the east-most longitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getEastmostLongitude() {
		Double value = doubleValuesMap.get(KnownDataTypes.EASTERNMOST_LONGITUDE.getVarName());
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
		doubleValuesMap.put(KnownDataTypes.EASTERNMOST_LONGITUDE.getVarName(), value);
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getSouthmostLatitude() {
		Double value = doubleValuesMap.get(KnownDataTypes.SOUTHERNMOST_LATITUDE.getVarName());
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
		doubleValuesMap.put(KnownDataTypes.SOUTHERNMOST_LATITUDE.getVarName(), value);
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getNorthmostLatitude() {
		Double value = doubleValuesMap.get(KnownDataTypes.NORTHERNMOST_LATITUDE.getVarName());
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
		doubleValuesMap.put(KnownDataTypes.NORTHERNMOST_LATITUDE.getVarName(), value);
	}

	/**
	 * @return
	 * 		the beginning time for the cruise;
	 * 		never null but could be {@link DashboardUtils#DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getBeginTime() {
		Date value = dateValuesMap.get(KnownDataTypes.TIME_COVERAGE_START.getVarName());
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
		dateValuesMap.put(KnownDataTypes.TIME_COVERAGE_START.getVarName(), value);
	}

	/**
	 * @return
	 * 		the ending time for the cruise;
	 * 		never null but could be {@link DashboardUtils#DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getEndTime() {
		Date value = dateValuesMap.get(KnownDataTypes.TIME_COVERAGE_END.getVarName());
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
		dateValuesMap.put(KnownDataTypes.TIME_COVERAGE_END.getVarName(), value);
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
		if ( doubleValuesMap.size() != other.doubleValuesMap.size() )
			return false;
		for ( Entry<String,Double> entry : doubleValuesMap.entrySet() ) {
			String key = entry.getKey();
			Double thisval = entry.getValue();
			Double otherval = other.doubleValuesMap.get(key);
			if ( key.toUpperCase().contains("LONGITUDE") ) {
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
		for ( Entry<String,String> entry : stringValuesMap.entrySet() )
			repr += "    " + entry.getKey() + "=" + entry.getValue() + "\n";
		for ( Entry<String,Double> entry : doubleValuesMap.entrySet() )
			repr += "    " + entry.getKey() + "=" + entry.getKey().toString() + "\n";
		for ( Entry<String,Date> entry : dateValuesMap.entrySet() )
			repr += "    " + entry.getKey() + "=" + entry.getKey().toString() + "\n";
		repr += "]";
		return repr;
	}

}
