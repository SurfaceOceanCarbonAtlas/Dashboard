/**
 */
package gov.noaa.pmel.dashboard.dsg;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class for working with metadata values of interest, including those derived from dataset data.
 *
 * @author Karl Smith
 */
public class DsgMetadata {

    // Maps of variable types to values
    TreeMap<DashDataType<?>,Object> valuesMap;

    /**
     * Create with the given data types.  Sets all values for these types to the appropriate missing value for each
     * type.  The data types given must be known subclasses of DashDataType valid for metadata files: {@link
     * StringDashDataType}, {@link IntDashDataType}, or {@link DoubleDashDataType}.
     *
     * @param knownTypes
     *         all known metadata types; cannot be null or empty
     *
     * @throws IllegalArgumentException
     *         if no data types are given, or if the data type not a known subclass type
     */
    public DsgMetadata(KnownDataTypes knownTypes) throws IllegalArgumentException {
        if ( (knownTypes == null) || knownTypes.isEmpty() )
            throw new IllegalArgumentException("no known metadata types");
        valuesMap = new TreeMap<DashDataType<?>,Object>();
        for (DashDataType<?> dtype : knownTypes.getKnownTypesSet()) {
            if ( dtype instanceof StringDashDataType ) {
                valuesMap.put(dtype, DashboardUtils.STRING_MISSING_VALUE);
            }
            else if ( dtype instanceof IntDashDataType ) {
                valuesMap.put(dtype, DashboardUtils.INT_MISSING_VALUE);
            }
            else if ( dtype instanceof DoubleDashDataType ) {
                valuesMap.put(dtype, DashboardUtils.FP_MISSING_VALUE);
            }
            else
                throw new IllegalArgumentException("unknown data type for metadata: " + dtype.toString());
        }
    }

    /**
     * @return the map of (metadata) data types to values; the actual map in this instance is returned.
     */
    public TreeMap<DashDataType<?>,Object> getValuesMap() {
        return valuesMap;
    }

    /**
     * Updates the value of the given (metadata) data type to the given value.
     *
     * @param dtype
     *         the data type of the value
     * @param value
     *         the value to assign; if null, the appropriate missing value is assigned
     *
     * @throws IllegalArgumentException
     *         if the data type is not a known data type in this metadata, or if the value is not an appropriate object
     *         for this data type
     */
    public void setValue(DashDataType<?> dtype, Object value) throws IllegalArgumentException {
        if ( !valuesMap.containsKey(dtype) )
            throw new IllegalArgumentException("unknown metadata type " + dtype.toString());
        Object newValue;
        if ( dtype instanceof StringDashDataType ) {
            if ( value == null )
                newValue = DashboardUtils.STRING_MISSING_VALUE;
            else if ( value instanceof String )
                newValue = value;
            else
                throw new IllegalArgumentException("invalid value (" + value + ") for data type " + dtype.toString());
        }
        else if ( dtype instanceof IntDashDataType ) {
            if ( value == null )
                newValue = DashboardUtils.INT_MISSING_VALUE;
            else if ( value instanceof Integer )
                newValue = value;
            else
                throw new IllegalArgumentException("invalid value (" + value + ") for data type " + dtype.toString());
        }
        else if ( dtype instanceof DoubleDashDataType ) {
            if ( value == null )
                newValue = DashboardUtils.FP_MISSING_VALUE;
            else if ( value instanceof Double )
                newValue = value;
            else
                throw new IllegalArgumentException("invalid value (" + value + ") for data type " + dtype.toString());
        }
        else {
            // Should not happen since this type was found in valuesMap
            throw new IllegalArgumentException("unknown data type for metadata: " + dtype.toString());
        }
        valuesMap.put(dtype, newValue);
    }

    /**
     * @return the dataset ID; never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getDatasetId() {
        String value = (String) valuesMap.get(DashboardServerUtils.DATASET_ID);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param datasetId
     *         the dataset ID to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setDatasetId(String datasetId) {
        String value;
        if ( datasetId != null )
            value = datasetId;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.DATASET_ID, value);
    }

    /**
     * @return the dataset name; never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getDatasetName() {
        String value = (String) valuesMap.get(DashboardServerUtils.DATASET_NAME);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param datasetName
     *         the dataset name to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setDatasetName(String datasetName) {
        String value;
        if ( datasetName != null )
            value = datasetName;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.DATASET_NAME, value);
    }

    /**
     * @return the platform name; never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getPlatformName() {
        String value = (String) valuesMap.get(DashboardServerUtils.PLATFORM_NAME);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param platformName
     *         the platform name to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setPlatformName(String platformName) {
        String value;
        if ( platformName != null )
            value = platformName;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.PLATFORM_NAME, value);

    }

    /**
     * @return the name of the organization/institution; never null but could be {@link
     * DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getOrganizationName() {
        String value = (String) valuesMap.get(DashboardServerUtils.ORGANIZATION_NAME);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param organizationName
     *         the name of the organization/institution to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is
     *         assigned
     */
    public void setOrganizationName(String organizationName) {
        String value;
        if ( organizationName != null )
            value = organizationName;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.ORGANIZATION_NAME, value);
    }

    /**
     * @return the investigator names; never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not
     * assigned
     */
    public String getInvestigatorNames() {
        String value = (String) valuesMap.get(DashboardServerUtils.INVESTIGATOR_NAMES);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param investigatorNames
     *         the investigator names to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setInvestigatorNames(String investigatorNames) {
        String value;
        if ( investigatorNames != null )
            value = investigatorNames;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.INVESTIGATOR_NAMES, value);
    }

    /**
     * @return the platform type; never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getPlatformType() {
        String value = (String) valuesMap.get(DashboardServerUtils.PLATFORM_TYPE);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param platformType
     *         the platform type to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setPlatformType(String platformType) {
        String value;
        if ( platformType != null )
            value = platformType;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.PLATFORM_TYPE, value);
    }

    /**
     * @return the west-most longitude for the cruise; never null could be {@link DashboardUtils#FP_MISSING_VALUE} if
     * not assigned.
     */
    public Double getWestmostLongitude() {
        Double value = (Double) valuesMap.get(DashboardServerUtils.WESTERNMOST_LONGITUDE);
        if ( value == null )
            value = DashboardUtils.FP_MISSING_VALUE;
        return value;
    }

    /**
     * @param westmostLongitude
     *         the west-most longitude to set; if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
     */
    public void setWestmostLongitude(Double westmostLongitude) {
        Double value;
        if ( westmostLongitude != null )
            value = westmostLongitude;
        else
            value = DashboardUtils.FP_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.WESTERNMOST_LONGITUDE, value);
    }

    /**
     * @return the east-most longitude for the cruise; never null but could be {@link DashboardUtils#FP_MISSING_VALUE}
     * if not assigned.
     */
    public Double getEastmostLongitude() {
        Double value = (Double) valuesMap.get(DashboardServerUtils.EASTERNMOST_LONGITUDE);
        if ( value == null )
            value = DashboardUtils.FP_MISSING_VALUE;
        return value;
    }

    /**
     * @param eastmostLongitude
     *         the east-most longitude to set; if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
     */
    public void setEastmostLongitude(Double eastmostLongitude) {
        Double value;
        if ( eastmostLongitude != null )
            value = eastmostLongitude;
        else
            value = DashboardUtils.FP_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.EASTERNMOST_LONGITUDE, value);
    }

    /**
     * @return the south-most latitude for the cruise; never null but could be {@link DashboardUtils#FP_MISSING_VALUE}
     * if not assigned.
     */
    public Double getSouthmostLatitude() {
        Double value = (Double) valuesMap.get(DashboardServerUtils.SOUTHERNMOST_LATITUDE);
        if ( value == null )
            value = DashboardUtils.FP_MISSING_VALUE;
        return value;
    }

    /**
     * @param southmostLatitude
     *         the south-most latitude to set; if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
     */
    public void setSouthmostLatitude(Double southmostLatitude) {
        Double value;
        if ( southmostLatitude != null )
            value = southmostLatitude;
        else
            value = DashboardUtils.FP_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.SOUTHERNMOST_LATITUDE, value);
    }

    /**
     * @return the south-most latitude for the cruise; never null but could be {@link DashboardUtils#FP_MISSING_VALUE}
     * if not assigned.
     */
    public Double getNorthmostLatitude() {
        Double value = (Double) valuesMap.get(DashboardServerUtils.NORTHERNMOST_LATITUDE);
        if ( value == null )
            value = DashboardUtils.FP_MISSING_VALUE;
        return value;
    }

    /**
     * @param northmostLatitude
     *         the north-most latitude to set; if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
     */
    public void setNorthmostLatitude(Double northmostLatitude) {
        Double value;
        if ( northmostLatitude != null )
            value = northmostLatitude;
        else
            value = DashboardUtils.FP_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.NORTHERNMOST_LATITUDE, value);
    }

    /**
     * @return the beginning time for the cruise, in units of "seconds since 1970-01-01T00:00:00"; never null but could
     * be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
     */
    public Double getBeginTime() {
        Double value = (Double) valuesMap.get(DashboardServerUtils.TIME_COVERAGE_START);
        if ( value == null )
            value = DashboardUtils.FP_MISSING_VALUE;
        return value;
    }

    /**
     * @param beginTime
     *         the beginning time for the cruise to set, in units of "seconds since 1970-01-01T00:00:00"; if null,
     *         {@link DashboardUtils#FP_MISSING_VALUE} is assigned
     */
    public void setBeginTime(Double beginTime) {
        Double value;
        if ( beginTime != null )
            value = beginTime;
        else
            value = DashboardUtils.FP_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.TIME_COVERAGE_START, value);
    }

    /**
     * @return the ending time for the cruise, in units of "seconds since 1970-01-01T00:00:00"; never null but could be
     * {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
     */
    public Double getEndTime() {
        Double value = (Double) valuesMap.get(DashboardServerUtils.TIME_COVERAGE_END);
        if ( value == null )
            value = DashboardUtils.FP_MISSING_VALUE;
        return value;
    }

    /**
     * @param endTime
     *         the ending time for the cruise to set, in units of "seconds since 1970-01-01T00:00:00"; if null, {@link
     *         DashboardUtils#FP_MISSING_VALUE} is assigned
     */
    public void setEndTime(Double endTime) {
        Double value;
        if ( endTime != null )
            value = endTime;
        else
            value = DashboardUtils.FP_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.TIME_COVERAGE_END, value);
    }

    /**
     * @return the status associated with this instance; never null but could be {@link
     * DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getStatus() {
        String value = (String) valuesMap.get(DashboardServerUtils.STATUS);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param status
     *         the status to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setStatus(String status) {
        String value;
        if ( status != null )
            value = status;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.STATUS, value);
    }

    /**
     * @return the version associated with this instance; never null but could be {@link
     * DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getVersion() {
        String value = (String) valuesMap.get(DashboardServerUtils.VERSION);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param version
     *         the version to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setVersion(String version) {
        String value;
        if ( version != null )
            value = version;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.VERSION, value);
    }

    // Start of SOCAT-specific metadata

    /**
     * @return the String of all region IDs; never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not
     * assigned
     */
    public String getAllRegionIDs() {
        String value = (String) valuesMap.get(SocatTypes.ALL_REGION_IDS);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param allRegionIDs
     *         the String of all region IDs to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setAllRegionIDs(String allRegionIDs) {
        String value;
        if ( allRegionIDs != null )
            value = allRegionIDs;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(SocatTypes.ALL_REGION_IDS, value);
    }

    /**
     * @return the SOCAT DOI for this dataset; never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if
     * not assigned
     */
    public String getSocatDOI() {
        String value = (String) valuesMap.get(SocatTypes.SOCAT_DOI);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param socatDOI
     *         the SOCAT DOI for this dataset to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setSocatDOI(String socatDOI) {
        String value;
        if ( socatDOI != null )
            value = socatDOI;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(SocatTypes.SOCAT_DOI, value);
    }
    // End of SOCAT-specific metadata

    /**
     * @return the maximum length of String values given in the fields of this instance, rounded up to the nearest
     * multiple of 32 (and never less than 32).
     */
    public int getMaxStringLength() {
        int maxLength = 32;
        for (Entry<DashDataType<?>,Object> entry : valuesMap.entrySet()) {
            if ( entry.getKey() instanceof StringDashDataType ) {
                String value = (String) entry.getValue();
                if ( maxLength < value.length() )
                    maxLength = value.length();
            }
        }
        maxLength = 32 * ((maxLength + 31) / 32);
        return maxLength;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Entry<DashDataType<?>,Object> entry : valuesMap.entrySet()) {
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

        if ( !(obj instanceof DsgMetadata) )
            return false;
        DsgMetadata other = (DsgMetadata) obj;

        if ( !valuesMap.keySet().equals(other.valuesMap.keySet()) )
            return false;

        for (Entry<DashDataType<?>,Object> entry : valuesMap.entrySet()) {
            DashDataType<?> key = entry.getKey();
            if ( key instanceof DoubleDashDataType ) {
                // Floating-point comparisons - values don't have to be exactly the same
                Double thisVal = (Double) entry.getValue();
                Double otherVal = (Double) other.valuesMap.get(key);
                if ( key.getVarName().toUpperCase().contains("LONGITUDE") ) {
                    // Longitudes have modulo 360.0, so 359.999999 is close to 0.0
                    if ( !DashboardUtils.longitudeCloseTo(thisVal, otherVal, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) )
                        return false;
                }
                else if ( !DashboardUtils.closeTo(thisVal, otherVal, 0.0, DashboardUtils.MAX_ABSOLUTE_ERROR) ) {
                    return false;
                }
            }
            else if ( !entry.getValue().equals(other.valuesMap.get(key)) ) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        String repr = "DsgMetadata[\n";
        for (Entry<DashDataType<?>,Object> entry : valuesMap.entrySet()) {
            DashDataType<?> key = entry.getKey();
            repr += "    " + key.getVarName() + "=" + entry.getValue() + "\n";
        }
        repr += "]";
        return repr;
    }

}
