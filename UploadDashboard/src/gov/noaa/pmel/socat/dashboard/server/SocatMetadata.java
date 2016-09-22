/**
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

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

	// Set for the order of all the variable names
	LinkedHashSet<String> varNamesSet;
	// Maps of variable names to values
	HashMap<String,String> stringValuesMap;
	HashMap<String,Double> doubleValuesMap;
	HashMap<String,Date> dateValuesMap;

	/**
	 * Generates an empty SocatMetadata object.
	 */
	public SocatMetadata() {
		String varName = KnownDataTypes.EXPOCODE.getVarName();
		varNamesSet.add(varName);
		stringValuesMap.put(varName, "");

		varName = KnownDataTypes.DATASET_NAME.getVarName();
		varNamesSet.add(varName);
		stringValuesMap.put(varName, "");

		varName = KnownDataTypes.VESSEL_NAME.getVarName();
		varNamesSet.add(varName);
		stringValuesMap.put(varName, "");

		varName = KnownDataTypes.ORGANIZATION_NAME.getVarName();
		varNamesSet.add(varName);
		stringValuesMap.put(varName, "");

		varName = KnownDataTypes.WESTERNMOST_LONGITUDE.getVarName();
		varNamesSet.add(varName);
		doubleValuesMap.put(varName, DashboardUtils.FP_MISSING_VALUE);

		varName = KnownDataTypes.EASTERNMOST_LONGITUDE.getVarName();
		varNamesSet.add(varName);
		doubleValuesMap.put(varName, DashboardUtils.FP_MISSING_VALUE);

		varName = KnownDataTypes.SOUTHERNMOST_LATITUDE.getVarName();
		varNamesSet.add(varName);
		doubleValuesMap.put(varName, DashboardUtils.FP_MISSING_VALUE);

		varName = KnownDataTypes.NORTHERNMOST_LATITUDE.getVarName();
		varNamesSet.add(varName);
		doubleValuesMap.put(varName, DashboardUtils.FP_MISSING_VALUE);

		varName = KnownDataTypes.TIME_COVERAGE_START.getVarName();
		varNamesSet.add(varName);
		dateValuesMap.put(varName, DashboardUtils.DATE_MISSING_VALUE);

		varName = KnownDataTypes.TIME_COVERAGE_END.getVarName();
		varNamesSet.add(varName);
		dateValuesMap.put(varName, DashboardUtils.DATE_MISSING_VALUE);

		varName = KnownDataTypes.INVESTIGATOR_NAMES.getVarName();
		varNamesSet.add(varName);
		stringValuesMap.put(varName, "");

		varName = SOCAT_VERSION_VARNAME;
		varNamesSet.add(varName);
		stringValuesMap.put(varName, "");

		varName = ALL_REGION_IDS_VARNAME;
		varNamesSet.add(varName);
		stringValuesMap.put(varName, "");

		varName = SOCAT_DOI_VARNAME;
		varNamesSet.add(varName);
		stringValuesMap.put(varName, "");

		varName = KnownDataTypes.QC_FLAG.getVarName();
		varNamesSet.add(varName);
		stringValuesMap.put(varName, "");
	}

	/**
	 * @param expocode 
	 * 		the expocode to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setExpocode(String expocode) {
		String value;
		if ( expocode != null )
			value = expocode;
		else
			value = "";
		stringValuesMap.put(KnownDataTypes.EXPOCODE.getVarName(), value);
	}

	/**
	 * @param datasetName
	 * 		the dataset name to set;
	 * 		if null, an empty string is assigned
	 */
	public void setDatasetName(String datasetName) {
		String value;
		if ( datasetName != null )
			value = datasetName;
		else
			value = "";
		stringValuesMap.put(KnownDataTypes.DATASET_NAME.getVarName(), value);
	}

	/**
	 * @param vesselName 
	 * 		the vesselName to set;
	 * 		if null, an empty string is assigned
	 */
	public void setVesselName(String vesselName) {
		String value;
		if ( vesselName != null )
			value = vesselName;
		else
			value = "";
		stringValuesMap.put(KnownDataTypes.VESSEL_NAME.getVarName(), value);
	}

	/**
	 * @param organization 
	 * 		the organization/institution to set;
	 * 		if null, an empty string is assigned
	 */
	public void setOrganization(String organization) {
		String value;
		if ( organization != null )
			value = organization;
		else
			value = "";
		stringValuesMap.put(KnownDataTypes.VESSEL_NAME.getVarName(), value);
	}

	/**
	 * @return
	 * 		the west-most longitude for the cruise;
	 * 		never null could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getWestmostLongitude() {
		return doubleValuesMap.get(KnownDataTypes.WESTERNMOST_LONGITUDE.getVarName());
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
		return doubleValuesMap.get(KnownDataTypes.EASTERNMOST_LONGITUDE.getVarName());
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
		doubleValuesMap.put(KnownDataTypes.WESTERNMOST_LONGITUDE.getVarName(), value);
	}

	/**
	 * @return
	 * 		the south-most latitude for the cruise;
	 * 		never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
	 */
	public Double getSouthmostLatitude() {
		return doubleValuesMap.get(KnownDataTypes.SOUTHERNMOST_LATITUDE.getVarName());
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
		return doubleValuesMap.get(KnownDataTypes.NORTHERNMOST_LATITUDE.getVarName());
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
		return beginTime;
	}

	/**
	 * @param beginTime 
	 * 		the beginning time for the cruise to set;
	 * 		if null, {@link DashboardUtils#DATE_MISSING_VALUE} is assigned
	 */
	public void setBeginTime(Date beginTime) {
		if ( beginTime == null )
			this.beginTime = DashboardUtils.DATE_MISSING_VALUE;
		else 
			this.beginTime = beginTime;
	}

	/**
	 * @return
	 * 		the ending time for the cruise;
	 * 		never null but could be {@link DashboardUtils#DATE_MISSING_VALUE} if not assigned.
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime 
	 * 		the ending time for the cruise to set;
	 * 		if null, {@link DashboardUtils#DATE_MISSING_VALUE} is assigned
	 */
	public void setEndTime(Date endTime) {
		if ( endTime == null )
			this.endTime = DashboardUtils.DATE_MISSING_VALUE;
		else 
			this.endTime = endTime;
	}

	/**
	 * @param scienceGroup 
	 * 		the science group to set;
	 * 		if null, an empty string is assigned
	 */
	public void setScienceGroup(String scienceGroup) {
		if ( scienceGroup == null )
			this.scienceGroup = "";
		else
			this.scienceGroup = scienceGroup;
	}

	/**
	 * The SOCAT version number and status is the SOCAT version number 
	 * followed by an 'N', indicating the dataset is new in this
	 * SOCAT version, or a 'U', indicating the dataset is an update
	 * from a previous SOCAT version.  Updates within a SOCAT version
	 * do NOT change an 'N' to a 'U'.
	 * 
	 * @return
	 * 		the SOCAT version number and status associated with this instance;
	 * 		never null but could be empty if not assigned
	 */
	public String getSocatVersion() {
		return socatVersion;
	}

	/**
	 * The SOCAT version number and status is the SOCAT version number 
	 * followed by an 'N', indicating the dataset is new in this
	 * SOCAT version, or a 'U', indicating the dataset is an update
	 * from a previous SOCAT version.  Updates within a SOCAT version
	 * do NOT change an 'N' to a 'U'.
	 * 
	 * @param version 
	 * 		the SOCAT version number and status to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setSocatVersion(String socatVersion) {
		if ( socatVersion == null )
			this.socatVersion = "";
		else
			this.socatVersion = socatVersion;
	}

	/**
	 * @return
	 * 		the String of all region IDs;
	 * 		never null but could be a empty string if not assigned
	 */
	public String getAllRegionIDs() {
		return allRegionIDs;
	}

	/**
	 * @param allRegionIDs
	 * 		the String of all region IDs to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setAllRegionIDs(String allRegionIDs) {
		if ( allRegionIDs == null )
			this.allRegionIDs = "";
		else
			this.allRegionIDs = allRegionIDs;
	}

	/**
	 * @param socatDOI
	 * 		the SOCAT DOI for this dataset to set; 
	 * 		if null, an empty string is assigned
	 */
	public void setSocatDOI(String socatDOI) {
		if ( socatDOI == null )
			this.socatDOI = "";
		else
			this.socatDOI = socatDOI;
	}

	/**
	 * @return
	 * 		the QC flag;
	 * 		never null but could be a string with a single blank character if not assigned
	 */
	public String getQcFlag() {
		return qcFlag;
	}

	/**
	 * @param qcFlag 
	 * 		the QC flag to set; 
	 * 		if null, a string with a single blank character is assigned
	 */
	public void setQcFlag(String qcFlag) {
		if ( qcFlag == null )
			this.qcFlag = " ";
		else
			this.qcFlag = qcFlag;
	}

	/**
	 * @return
	 * 		the maximum length of String values given in the fields 
	 * 		of this instance, or 16, whichever is larger.
	 */
	public int getMaxStringLength() {
		int maxLength = 16;
		if ( maxLength < expocode.length() )
			maxLength = expocode.length();
		if ( maxLength < cruiseName.length() ) 
			maxLength = cruiseName.length();
		if ( maxLength < vesselName.length() ) 
			maxLength = vesselName.length();
		if ( maxLength < organization.length() ) 
			maxLength = organization.length();
		if ( maxLength < scienceGroup.length() ) 
			maxLength = scienceGroup.length();
		if ( maxLength < socatVersion.length() )
			maxLength = socatVersion.length();
		if ( maxLength < allRegionIDs.length() )
			maxLength = allRegionIDs.length();
		if ( maxLength < socatDOI.length() )
			maxLength = socatDOI.length();
		if ( maxLength < qcFlag.length() )
			maxLength = qcFlag.length();
		return maxLength;
	}

	@Override 
	public int hashCode() {
		// Do not consider floating-point fields since they do not 
		// have to be exactly the same for equals to return true.
		final int prime = 37;
		int result = expocode.hashCode();
		result = result * prime + cruiseName.hashCode();
		result = result * prime + vesselName.hashCode();
		result = result * prime + organization.hashCode();
		result = result * prime + beginTime.hashCode();
		result = result * prime + endTime.hashCode();
		result = result * prime + scienceGroup.hashCode();
		result = result * prime + socatVersion.hashCode();
		result = result * prime + allRegionIDs.hashCode();
		result = result * prime + socatDOI.hashCode();
		result = result * prime + qcFlag.hashCode();
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
		if ( ! beginTime.equals(other.beginTime) )
			return false;
		if ( ! endTime.equals(other.endTime) )
			return false;

		// String comparisons
		if ( ! expocode.equals(other.expocode) )
			return false;
		if ( ! cruiseName.equals(other.cruiseName) )
			return false;
		if ( ! vesselName.equals(other.vesselName) )
			return false;
		if ( ! organization.equals(other.organization) )
			return false;
		if ( ! scienceGroup.equals(other.scienceGroup) )
			return false;
		if ( ! socatVersion.equals(other.socatVersion) )
			return false;
		if ( ! allRegionIDs.equals(other.allRegionIDs) )
			return false;
		if ( ! socatDOI.equals(other.socatDOI) )
			return false;
		if ( ! qcFlag.equals(other.qcFlag) )
			return false;

		// Floating-point comparisons
		if ( ! DashboardUtils.closeTo(southmostLatitude, 
				other.southmostLatitude, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.closeTo(northmostLatitude, 
				other.northmostLatitude, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
			return false;

		// Longitudes have modulo 360.0, so 359.999999 is close to 0.0
		if ( ! DashboardUtils.longitudeCloseTo(westmostLongitude, other.westmostLongitude, 
				0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
			return false;
		if ( ! DashboardUtils.longitudeCloseTo(eastmostLongitude, other.eastmostLongitude, 
				0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "SocatMetadata[ expocode=" + expocode +
				",\n    cruiseName=" + cruiseName +
				",\n    vesselName=" + vesselName + 
				",\n    organization=" + organization + 
				",\n    westmostLongitude=" + westmostLongitude.toString() + 
				",\n    eastmostLongitude=" + eastmostLongitude.toString() + 
				",\n    southmostLatitude=" + southmostLatitude.toString() + 
				",\n    northmostLatitude=" + northmostLatitude.toString() + 
				",\n    startDate=" + beginTime.toString() + 
				",\n    endDate=" + endTime.toString() + 
				",\n    scienceGroup=" + scienceGroup + 
				",\n    version=" + socatVersion + 
				",\n    allRegionIDs=" + allRegionIDs + 
				",\n    socatDOI=" + socatDOI + 
				",\n    qcFlag=" + qcFlag + 
				" ]";
	}

}
