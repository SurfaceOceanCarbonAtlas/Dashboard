/**
 *
 */
package gov.noaa.pmel.dashboard.datatype;

import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Known data types that can be extended as needed. Provides an ordered set of known types.
 *
 * @author Karl Smith
 */
public class KnownDataTypes {

    /**
     * Map whose keys are both variable name keys and display name keys
     * (see {@link DashboardServerUtils#getKeyForName(String)}) for a data type.
     */
    private HashMap<String,DashDataType<?>> knownTypes;

    /**
     * Creates with no well-know data types.
     */
    public KnownDataTypes() {
        // Give extra capacity for added types
        knownTypes = new HashMap<String,DashDataType<?>>(64);
    }

    /**
     * Adds the given data type to this collection of known data types.
     *
     * @param dtype
     *         new data type to add to the known list;
     *         the given instance is added to the internal collection of known data types.
     *
     * @throws IllegalArgumentException
     *         if the display name of the given type matches the display name of another type in the set
     */
    private DashDataType<?> addDataType(DashDataType<?> dtype) throws IllegalArgumentException {
        String varKey = DashboardServerUtils.getKeyForName(dtype.getVarName());
        DashDataType<?> oldType = knownTypes.put(varKey, dtype);
        if ( oldType != null ) {
            // If a replacement, make sure the display name keyed entry is also replaced
            String displayKey = DashboardServerUtils.getKeyForName(oldType.getDisplayName());
            if ( !displayKey.equals(varKey) )
                knownTypes.remove(displayKey);
        }
        String displayKey = DashboardServerUtils.getKeyForName(dtype.getDisplayName());
        if ( !displayKey.equals(varKey) ) {
            DashDataType<?> otherOldType = knownTypes.put(displayKey, dtype);
            if ( otherOldType != null )
                throw new IllegalArgumentException("Two variable names have the same display name");
        }
        return oldType;
    }

    /**
     * Adds the default well-known data column types for users to select from:
     * <p>
     * UNKNOWN, OTHER,
     * DATASET_ID, DATASET_NAME, PLATFORM_NAME, PLATFORM_TYPE, ORGANIZATION_NAME, INVESTIGATOR_NAMES,
     * SAMPLE_ID, LONGITUDE, LATITUDE, SAMPLE_DEPTH, TIMESTAMP, DATE, YEAR, MONTH_OF_YEAR, DAY_OF_MONTH,
     * TIME_OF_DAY, HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE, DAY_OF_YEAR, SECOND_OF_DAY
     * <p>
     * SALINITY, TEQU, SST, PEQU, PATM, XCO2_WATER_TEQU_DRY, XCO2_WATER_SST_DRY, PCO2_WATER_TEQU_WET,
     * PCO2_WATER_SST_WET, FCO2_WATER_TEQU_WET, FCO2_WATER_SST_WET, WOCE_CO2_WATER, COMMENT_WOCE_CO2_WATER,
     * WOCE_CO2_ATM, COMMENT_WOCE_CO2_ATM
     * <p>
     * This should be called before adding any custom types.
     *
     * @return this instance (as a convenience for chaining)
     */
    public KnownDataTypes addStandardTypesForUsers() {
        addDataType(DashboardServerUtils.UNKNOWN);
        addDataType(DashboardServerUtils.OTHER);
        addDataType(DashboardServerUtils.DATASET_ID);
        addDataType(DashboardServerUtils.DATASET_NAME);
        addDataType(DashboardServerUtils.PLATFORM_NAME);
        addDataType(DashboardServerUtils.PLATFORM_TYPE);
        addDataType(DashboardServerUtils.ORGANIZATION_NAME);
        addDataType(DashboardServerUtils.INVESTIGATOR_NAMES);
        addDataType(DashboardServerUtils.SAMPLE_ID);
        addDataType(DashboardServerUtils.LONGITUDE);
        addDataType(DashboardServerUtils.LATITUDE);
        addDataType(DashboardServerUtils.SAMPLE_DEPTH);
        addDataType(DashboardServerUtils.TIMESTAMP);
        addDataType(DashboardServerUtils.DATE);
        addDataType(DashboardServerUtils.YEAR);
        addDataType(DashboardServerUtils.MONTH_OF_YEAR);
        addDataType(DashboardServerUtils.DAY_OF_MONTH);
        addDataType(DashboardServerUtils.TIME_OF_DAY);
        addDataType(DashboardServerUtils.HOUR_OF_DAY);
        addDataType(DashboardServerUtils.MINUTE_OF_HOUR);
        addDataType(DashboardServerUtils.SECOND_OF_MINUTE);
        addDataType(DashboardServerUtils.DAY_OF_YEAR);
        addDataType(DashboardServerUtils.SECOND_OF_DAY);

        // Add the types from SocatTypes
        addDataType(SocatTypes.SALINITY);
        addDataType(SocatTypes.TEQU);
        addDataType(SocatTypes.SST);
        addDataType(SocatTypes.PEQU);
        addDataType(SocatTypes.PATM);
        addDataType(SocatTypes.XCO2_WATER_TEQU_DRY);
        addDataType(SocatTypes.XCO2_WATER_SST_DRY);
        addDataType(SocatTypes.PCO2_WATER_TEQU_WET);
        addDataType(SocatTypes.PCO2_WATER_SST_WET);
        addDataType(SocatTypes.FCO2_WATER_TEQU_WET);
        addDataType(SocatTypes.FCO2_WATER_SST_WET);
        addDataType(SocatTypes.WOCE_CO2_WATER);
        addDataType(SocatTypes.COMMENT_WOCE_CO2_WATER);
        addDataType(SocatTypes.WOCE_CO2_ATM);
        addDataType(SocatTypes.COMMENT_WOCE_CO2_ATM);

        return this;
    }

    /**
     * Adds the default well-known metadata column types for generating the NetCDF DSG files:
     * <p>
     * DATASET_ID, DATASET_NAME, ENHANCED_DOI, PLATFORM_NAME, PLATFORM_TYPE, ORGANIZATION_NAME, INVESTIGATOR_NAMES,
     * WESTERNMOST_LONGITUDE, EASTERNMOST_LONGITUDE, SOUTHERNMOST_LATITUDE, NORTHERNMOST_LATITUDE,
     * TIME_COVERAGE_START, TIME_COVERAGE_END, DATASET_QC_FLAG, ALL_REGION_IDS, VERSION
     * <p>
     * This should be called before adding any custom types.
     *
     * @return this instance (as a convenience for chaining)
     */
    public KnownDataTypes addStandardTypesForMetadataFiles() {
        addDataType(DashboardServerUtils.DATASET_ID);
        addDataType(DashboardServerUtils.DATASET_NAME);
        addDataType(DashboardServerUtils.ENHANCED_DOI);
        addDataType(DashboardServerUtils.PLATFORM_NAME);
        addDataType(DashboardServerUtils.PLATFORM_TYPE);
        addDataType(DashboardServerUtils.INVESTIGATOR_NAMES);
        addDataType(DashboardServerUtils.ORGANIZATION_NAME);
        addDataType(DashboardServerUtils.WESTERNMOST_LONGITUDE);
        addDataType(DashboardServerUtils.EASTERNMOST_LONGITUDE);
        addDataType(DashboardServerUtils.SOUTHERNMOST_LATITUDE);
        addDataType(DashboardServerUtils.NORTHERNMOST_LATITUDE);
        addDataType(DashboardServerUtils.TIME_COVERAGE_START);
        addDataType(DashboardServerUtils.TIME_COVERAGE_END);
        addDataType(DashboardServerUtils.DATASET_QC_FLAG);
        addDataType(DashboardServerUtils.ALL_REGION_IDS);
        addDataType(DashboardServerUtils.VERSION);
        return this;
    }

    /**
     * Adds the default well-known data column types for generating the NetCDF DSG files:
     * <p>
     * SAMPLE_NUMBER, TIME, LONGITUDE, LATITUDE, SAMPLE_DEPTH, YEAR, MONTH_OF_YEAR,
     * DAY_OF_MONTH, HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE, REGION_ID
     * <p>
     * SALINITY, TEQU, SST, PEQU, PATM, XCO2_WATER_TEQU_DRY, XCO2_WATER_SST_DRY, PCO2_WATER_TEQU_WET,
     * PCO2_WATER_SST_WET, FCO2_WATER_TEQU_WET, FCO2_WATER_SST_WET, WOCE_CO2_WATER, WOCE_CO2_ATM,
     * WOA_SALINITY, NCEP_SLP, DELTA_TEMP, CALC_SPEED, ETOPO2_DEPTH, GVCO2, DIST_TO_LAND, FCO2_REC, FCO2_SOURCE
     * <p>
     * This should be called before adding any custom types.
     *
     * @return this instance (as a convenience for chaining)
     */
    public KnownDataTypes addStandardTypesForDataFiles() {
        addDataType(DashboardServerUtils.SAMPLE_NUMBER);
        addDataType(DashboardServerUtils.TIME);
        addDataType(DashboardServerUtils.LONGITUDE);
        addDataType(DashboardServerUtils.LATITUDE);
        addDataType(DashboardServerUtils.SAMPLE_DEPTH);
        addDataType(DashboardServerUtils.YEAR);
        addDataType(DashboardServerUtils.MONTH_OF_YEAR);
        addDataType(DashboardServerUtils.DAY_OF_MONTH);
        addDataType(DashboardServerUtils.HOUR_OF_DAY);
        addDataType(DashboardServerUtils.MINUTE_OF_HOUR);
        addDataType(DashboardServerUtils.SECOND_OF_MINUTE);
        addDataType(DashboardServerUtils.REGION_ID);

        // Add the types from SocatTypes
        addDataType(SocatTypes.SALINITY);
        addDataType(SocatTypes.TEQU);
        addDataType(SocatTypes.SST);
        addDataType(SocatTypes.PEQU);
        addDataType(SocatTypes.PATM);
        addDataType(SocatTypes.XCO2_WATER_TEQU_DRY);
        addDataType(SocatTypes.XCO2_WATER_SST_DRY);
        addDataType(SocatTypes.PCO2_WATER_TEQU_WET);
        addDataType(SocatTypes.PCO2_WATER_SST_WET);
        addDataType(SocatTypes.FCO2_WATER_TEQU_WET);
        addDataType(SocatTypes.FCO2_WATER_SST_WET);
        addDataType(SocatTypes.WOCE_CO2_WATER);
        addDataType(SocatTypes.WOCE_CO2_ATM);
        addDataType(SocatTypes.WOA_SALINITY);
        addDataType(SocatTypes.DELTA_TEMP);
        addDataType(SocatTypes.CALC_SPEED);
        addDataType(SocatTypes.NCEP_SLP);
        addDataType(SocatTypes.ETOPO2_DEPTH);
        addDataType(SocatTypes.GVCO2);
        addDataType(SocatTypes.DIST_TO_LAND);
        addDataType(SocatTypes.FCO2_REC);
        addDataType(SocatTypes.FCO2_SOURCE);

        return this;
    }

    /**
     * Create additional known data types from values in a Properties object.
     *
     * @param typeProps
     *         data types properties to add to the known list; uses the simple line format: varName={JSON description}
     *         where varName is the variable name of the type and {JSON description} is a JSON string describing the
     *         type as documented by {@link DashDataType#fromPropertyValue(String, String)}
     * @param role
     *         only add data types that have this role; if null, add all data types, regardless of role
     * @param logger
     *         if not null, log any replacement warnings here
     *
     * @return this instance (as a convenience for chaining)
     *
     * @throws IllegalArgumentException
     *         if the JSON description cannot be parsed
     */
    public KnownDataTypes addTypesFromProperties(Properties typeProps, DashDataType.Role role, Logger logger)
            throws IllegalArgumentException {
        for (String name : typeProps.stringPropertyNames()) {
            String value = typeProps.getProperty(name);
            DashDataType<?> dtype = DashDataType.fromPropertyValue(name, value);
            if ( dtype.hasRole(role) ) {
                DashDataType<?> oldType = addDataType(dtype);
                if ( (logger != null) && (oldType != null) && !oldType.equals(dtype) ) {
                    logger.warn("Data type: " + dtype.toString());
                    logger.warn("replacing data type: " + oldType.toString());
                }
            }
        }
        return this;
    }

    /**
     * Determines if a given data type variable or display name exists in the list of known data types.
     *
     * @param typeName
     *         search for a data type with this variable or display name
     *
     * @return if the given data type name is known
     */
    public boolean containsTypeName(String typeName) {
        return knownTypes.containsKey(DashboardServerUtils.getKeyForName(typeName));
    }

    /**
     * Returns a new data type matching the variable or display name.
     *
     * @param typeName
     *         variable or display name to find
     *
     * @return data type matching the given type name, or null if the name does not match that of a known type
     */
    public DashDataType<?> getDataType(String typeName) {
        return knownTypes.get(DashboardServerUtils.getKeyForName(typeName));
    }

    /**
     * Returns a new data type matching the variable or display name of the given data column type.
     *
     * @param dctype
     *         data column type to use
     *
     * @return data type matching the name in the given data column type, or null if the name does not match that of a
     *         known type
     */
    public DashDataType<?> getDataType(DataColumnType dctype) {
        DashDataType<?> dtype = getDataType(dctype.getVarName());
        if ( dtype == null )
            dtype = getDataType(dctype.getDisplayName());
        return dtype;
    }

    /**
     * @return the sorted current set of known data types.
     */
    public TreeSet<DashDataType<?>> getKnownTypesSet() {
        return new TreeSet<DashDataType<?>>(knownTypes.values());
    }

    /**
     * @return if there are no known data types
     */
    public boolean isEmpty() {
        return knownTypes.isEmpty();
    }

    @Override
    public String toString() {
        String strval = "KnownDataTypes[\n";
        // Do not show the keys, only the unique data types
        for (DashDataType<?> dtype : getKnownTypesSet()) {
            strval += "    " + dtype.toString() + "\n";
        }
        strval += "]";
        return strval;
    }

}
