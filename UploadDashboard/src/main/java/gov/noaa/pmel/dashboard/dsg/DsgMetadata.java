package gov.noaa.pmel.dashboard.dsg;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class for working with metadata values of interest, including those derived from dataset data,
 * that will be put into the DSG files for this dataset.
 *
 * @author Karl Smith
 */
public class DsgMetadata {

    // Maps of variable types to values
    TreeMap<DashDataType<?>,Object> valuesMap;

    /**
     * Create with the given data types.  Sets all values for these types to the appropriate missing value
     * for each type.  The data types given must be known subclasses of DashDataType valid for metadata files:
     * {@link StringDashDataType}, {@link IntDashDataType}, or {@link DoubleDashDataType}.
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
     *         if the data type is not a known data type in this metadata, or
     *         if the value is not an appropriate object for this data type
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
     * @return the name of the organization/institution;
     *         never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getOrganizationName() {
        String value = (String) valuesMap.get(DashboardServerUtils.ORGANIZATION_NAME);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param organizationName
     *         the name of the organization/institution to set;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
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
     * @return the investigator names;
     *         never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
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
     * @return the west-most longitude for the cruise;
     *         never null could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
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
     * @return the east-most longitude for the cruise;
     *         never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
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
     * @return the south-most latitude for the cruise;
     *         never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
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
     * @return the south-most latitude for the cruise;
     *         never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
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
     * @return the beginning (earliest) time for the data, in units of "seconds since 1970-01-01T00:00:00";
     *         never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
     */
    public Double getBeginTime() {
        Double value = (Double) valuesMap.get(DashboardServerUtils.TIME_COVERAGE_START);
        if ( value == null )
            value = DashboardUtils.FP_MISSING_VALUE;
        return value;
    }

    /**
     * @param beginTime
     *         the beginning (earliest) time for the data to set, in units of "seconds since 1970-01-01T00:00:00";
     *         if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
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
     * @return the ending (latest) time for the data, in units of "seconds since 1970-01-01T00:00:00";
     *         never null but could be {@link DashboardUtils#FP_MISSING_VALUE} if not assigned.
     */
    public Double getEndTime() {
        Double value = (Double) valuesMap.get(DashboardServerUtils.TIME_COVERAGE_END);
        if ( value == null )
            value = DashboardUtils.FP_MISSING_VALUE;
        return value;
    }

    /**
     * @param endTime
     *         the ending (latest) time for the data to set, in units of "seconds since 1970-01-01T00:00:00";
     *         if null, {@link DashboardUtils#FP_MISSING_VALUE} is assigned
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
     * @return the DOI for source dataset;
     *         never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getSourceDOI() {
        String value = (String) valuesMap.get(DashboardServerUtils.SOURCE_DOI);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param sourceDOI
     *         the DOI for the source dataset;
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setSourceDOI(String sourceDOI) {
        String value;
        if ( sourceDOI != null )
            value = sourceDOI;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.SOURCE_DOI, value);
    }

    /**
     * @return the DOI for the enhanced dataset;
     *         never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getEnhancedDOI() {
        String value = (String) valuesMap.get(DashboardServerUtils.ENHANCED_DOI);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param enhancedDOI
     *         the DOI to set for the enhanced dataset
     *         if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setEnhancedDOI(String enhancedDOI) {
        String value;
        if ( enhancedDOI != null )
            value = enhancedDOI;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.ENHANCED_DOI, value);
    }

    /**
     * @return the dataset QC flag;
     *         never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getDatasetQCFlag() {
        String value = (String) valuesMap.get(DashboardServerUtils.DATASET_QC_FLAG);
        if ( value == null )
            value = DashboardUtils.STRING_MISSING_VALUE;
        return value;
    }

    /**
     * @param datasetQCFlag
     *         the dataset QC flag to set; if null, {@link DashboardUtils#STRING_MISSING_VALUE} is assigned
     */
    public void setDatasetQCFlag(String datasetQCFlag) {
        String value;
        if ( datasetQCFlag != null )
            value = datasetQCFlag;
        else
            value = DashboardUtils.STRING_MISSING_VALUE;
        valuesMap.put(DashboardServerUtils.DATASET_QC_FLAG, value);
    }

    /**
     * @return the version associated with this instance;
     *         never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
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

    /**
     * @return the String of all region IDs;
     *         never null but could be {@link DashboardUtils#STRING_MISSING_VALUE} if not assigned
     */
    public String getAllRegionIDs() {
        String value = (String) valuesMap.get(DashboardServerUtils.ALL_REGION_IDS);
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
        valuesMap.put(DashboardServerUtils.ALL_REGION_IDS, value);
    }

    /**
     * @return the maximum length of String values given in the fields of this instance,
     *         rounded up to the nearest multiple of 32 (and never less than 32).
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

    public void assignLonLatTimeLimits(Double[] sampleLongitudes, Double[] sampleLatitudes,
            Double[] sampleTimes, Set<Integer> errRows) throws IllegalArgumentException {
        int numRows = sampleTimes.length;
        if ( (sampleLongitudes.length != numRows) || (sampleLatitudes.length != numRows) )
            throw new IllegalArgumentException("Number of longitudes, latitudes, and times do not match");
        Double beginTime = null;
        Double endTime = null;
        Double southLat = null;
        Double northLat = null;
        Double westLon = null;
        Double eastLon = null;
        Double lastLon = null;
        double lonAdjust = 0.0;
        for (int k = 0; k < numRows; k++) {
            // Ignore any rows marked as bad
            if ( errRows.contains(k) )
                continue;
            Double mylon = sampleLongitudes[k];
            Double mylat = sampleLatitudes[k];
            Double mytime = sampleTimes[k];
            if ( (mylon == null) || mylon.isInfinite() || mylon.isNaN() )
                throw new IllegalArgumentException("Invalid longitude on row number " + Integer.toString(k + 1));
            if ( (mylat == null) || mylat.isInfinite() || mylat.isNaN() )
                throw new IllegalArgumentException("Invalid longitude on row number " + Integer.toString(k + 1));
            if ( (mytime == null) || mytime.isInfinite() || mytime.isNaN() )
                throw new IllegalArgumentException("Invalid longitude on row number " + Integer.toString(k + 1));
            // These should be ordered from earliest to latest time, but just in case....
            if ( (beginTime == null) || (beginTime > mytime) )
                beginTime = mytime;
            if ( (endTime == null) || (endTime < mytime) )
                endTime = mytime;
            if ( (southLat == null) || (southLat > mylat) )
                southLat = mylat;
            if ( (northLat == null) || (northLat < mylat) )
                northLat = mylat;
            // Initially adjust longitudes to [-180,180]
            while ( mylon < -180.0 ) {
                mylon += 360.0;
            }
            while ( mylon > 180.0 ) {
                mylon -= 360.0;
            }
            if ( lastLon == null ) {
                westLon = mylon;
                eastLon = mylon;
                lastLon = mylon;
            }
            else {
                double delta = mylon - lastLon;
                lastLon = mylon;
                // If crosses the modulo meridian, delta will be close to 360.
                // Instead adjust the longitudes so they can be compared as if no modulo.
                if ( delta < -180.0 ) {
                    // Crossed modulo going east - add 360 to the longitude adjustment
                    delta += 360.0;
                    lonAdjust += 360.0;
                }
                else if ( delta > 180.0 ) {
                    // Crossed modulo going west - subtract 360 from the longitude adjustment
                    delta -= 360.0;
                    lonAdjust -= 360.0;
                }
                if ( delta > 0.0 ) {
                    // moved east - check adjusted longitude against eastLon limit
                    if ( eastLon < (mylon + lonAdjust) )
                        eastLon = mylon + lonAdjust;
                }
                else if ( delta < 0.0 ) {
                    // moved west - check adjusted longitude agains westLon limit
                    if ( westLon > (mylon + lonAdjust) )
                        westLon = mylon + lonAdjust;
                }
            }
        }
        if ( (eastLon != null) && (westLon != null) ) {
            if ( (eastLon - westLon) >= 360.0 ) {
                eastLon = 180.0;
                westLon = -180.0;
            }
            else {
                // return the longitude limits to [-180,180]
                while ( eastLon < -180.0 ) {
                    eastLon += 360.0;
                }
                while ( eastLon > 180.0 ) {
                    eastLon -= 360.0;
                }
                while ( westLon < -180.0 ) {
                    westLon += 360.0;
                }
                while ( westLon > 180.0 ) {
                    westLon -= 360.0;
                }
            }
        }
        setBeginTime(beginTime);
        setEndTime(endTime);
        setSouthmostLatitude(southLat);
        setNorthmostLatitude(northLat);
        setWestmostLongitude(westLon);
        setEastmostLongitude(eastLon);
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
