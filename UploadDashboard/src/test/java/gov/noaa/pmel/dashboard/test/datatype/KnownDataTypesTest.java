/**
 *
 */
package gov.noaa.pmel.dashboard.test.datatype;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for methods of {@link KnownDataTypes}.
 *
 * @author Karl Smith
 */
public class KnownDataTypesTest {

    public static final ArrayList<String> SHIP_SPEED_UNITS = new ArrayList<String>(
            Arrays.asList("knots", "km/h", "m/s", "mph"));

    public static final DoubleDashDataType SHIP_SPEED = new DoubleDashDataType("ship_speed",
            670.0, "ship speed", "measured ship speed", false,
            SHIP_SPEED_UNITS, "platform_speed_wrt_ground", DashboardServerUtils.PLATFORM_CATEGORY, null,
            "0.0", null, "50.0", "200.0", DashboardServerUtils.USER_FILE_DATA_ROLES);

    /**
     * Known data types for users for unit tests
     */
    public static final KnownDataTypes TEST_KNOWN_USER_DATA_TYPES;

    /**
     * Known metadata types for files for unit tests
     */
    public static final KnownDataTypes TEST_KNOWN_METADATA_FILE_TYPES;

    /**
     * Known data types for files for unit tests
     */
    public static final KnownDataTypes TEST_KNOWN_DATA_FILE_TYPES;

    static {
        TEST_KNOWN_METADATA_FILE_TYPES = new KnownDataTypes();
        TEST_KNOWN_METADATA_FILE_TYPES.addStandardTypesForMetadataFiles();

        Properties typeProps = new Properties();
        typeProps.setProperty(KnownDataTypesTest.SHIP_SPEED.getVarName(),
                KnownDataTypesTest.SHIP_SPEED.toPropertyValue());

        TEST_KNOWN_USER_DATA_TYPES = new KnownDataTypes();
        TEST_KNOWN_USER_DATA_TYPES.addStandardTypesForUsers();
        TEST_KNOWN_USER_DATA_TYPES.addTypesFromProperties(typeProps, DashDataType.Role.USER_DATA, null);

        TEST_KNOWN_DATA_FILE_TYPES = new KnownDataTypes();
        TEST_KNOWN_DATA_FILE_TYPES.addStandardTypesForDataFiles();
        TEST_KNOWN_DATA_FILE_TYPES.addTypesFromProperties(typeProps, DashDataType.Role.FILE_DATA, null);
    }


    /**
     * The expected standard data types for users
     */
    private static final TreeSet<DashDataType<?>> STD_USERS_TYPES = new TreeSet<DashDataType<?>>(Arrays.asList(
            DashboardServerUtils.UNKNOWN,
            DashboardServerUtils.OTHER,
            DashboardServerUtils.DATASET_ID,
            DashboardServerUtils.DATASET_NAME,
            DashboardServerUtils.PLATFORM_NAME,
            DashboardServerUtils.PLATFORM_TYPE,
            DashboardServerUtils.ORGANIZATION_NAME,
            DashboardServerUtils.INVESTIGATOR_NAMES,
            DashboardServerUtils.SAMPLE_ID,
            DashboardServerUtils.LONGITUDE,
            DashboardServerUtils.LATITUDE,
            DashboardServerUtils.SAMPLE_DEPTH,
            DashboardServerUtils.TIMESTAMP,
            DashboardServerUtils.DATE,
            DashboardServerUtils.YEAR,
            DashboardServerUtils.MONTH_OF_YEAR,
            DashboardServerUtils.DAY_OF_MONTH,
            DashboardServerUtils.TIME_OF_DAY,
            DashboardServerUtils.HOUR_OF_DAY,
            DashboardServerUtils.MINUTE_OF_HOUR,
            DashboardServerUtils.SECOND_OF_MINUTE,
            DashboardServerUtils.DAY_OF_YEAR,
            DashboardServerUtils.SECOND_OF_DAY,

            SocatTypes.SALINITY,
            SocatTypes.TEQU,
            SocatTypes.SST,
            SocatTypes.PEQU,
            SocatTypes.PATM,
            SocatTypes.XCO2_WATER_TEQU_DRY,
            SocatTypes.XCO2_WATER_SST_DRY,
            SocatTypes.PCO2_WATER_TEQU_WET,
            SocatTypes.PCO2_WATER_SST_WET,
            SocatTypes.FCO2_WATER_TEQU_WET,
            SocatTypes.FCO2_WATER_SST_WET,
            SocatTypes.WOCE_CO2_WATER,
            SocatTypes.COMMENT_WOCE_CO2_WATER,
            SocatTypes.WOCE_CO2_ATM,
            SocatTypes.COMMENT_WOCE_CO2_ATM
    ));

    /**
     * The expected standard metadata types for DSG files
     */
    private static final TreeSet<DashDataType<?>> STD_METADATA_FILES_TYPES = new TreeSet<DashDataType<?>>(Arrays.asList(
            DashboardServerUtils.DATASET_ID,
            DashboardServerUtils.DATASET_NAME,
            DashboardServerUtils.ENHANCED_DOI,
            DashboardServerUtils.PLATFORM_NAME,
            DashboardServerUtils.PLATFORM_TYPE,
            DashboardServerUtils.ORGANIZATION_NAME,
            DashboardServerUtils.INVESTIGATOR_NAMES,
            DashboardServerUtils.WESTERNMOST_LONGITUDE,
            DashboardServerUtils.EASTERNMOST_LONGITUDE,
            DashboardServerUtils.SOUTHERNMOST_LATITUDE,
            DashboardServerUtils.NORTHERNMOST_LATITUDE,
            DashboardServerUtils.TIME_COVERAGE_START,
            DashboardServerUtils.TIME_COVERAGE_END,
            DashboardServerUtils.DATASET_QC_FLAG,
            DashboardServerUtils.ALL_REGION_IDS,
            DashboardServerUtils.VERSION
    ));

    /**
     * The expected standard data types for DSG files
     */
    private static final TreeSet<DashDataType<?>> STD_DATA_FILES_TYPES = new TreeSet<DashDataType<?>>(Arrays.asList(
            DashboardServerUtils.SAMPLE_NUMBER,
            DashboardServerUtils.TIME,
            DashboardServerUtils.LONGITUDE,
            DashboardServerUtils.LATITUDE,
            DashboardServerUtils.SAMPLE_DEPTH,
            DashboardServerUtils.YEAR,
            DashboardServerUtils.MONTH_OF_YEAR,
            DashboardServerUtils.DAY_OF_MONTH,
            DashboardServerUtils.HOUR_OF_DAY,
            DashboardServerUtils.MINUTE_OF_HOUR,
            DashboardServerUtils.SECOND_OF_MINUTE,
            DashboardServerUtils.REGION_ID,

            SocatTypes.SALINITY,
            SocatTypes.TEQU,
            SocatTypes.SST,
            SocatTypes.PEQU,
            SocatTypes.PATM,
            SocatTypes.XCO2_WATER_TEQU_DRY,
            SocatTypes.XCO2_WATER_SST_DRY,
            SocatTypes.PCO2_WATER_TEQU_WET,
            SocatTypes.PCO2_WATER_SST_WET,
            SocatTypes.FCO2_WATER_TEQU_WET,
            SocatTypes.FCO2_WATER_SST_WET,
            SocatTypes.WOCE_CO2_WATER,
            SocatTypes.WOCE_CO2_ATM,
            SocatTypes.WOA_SALINITY,
            SocatTypes.NCEP_SLP,
            SocatTypes.DELTA_TEMP,
            SocatTypes.CALC_SPEED,
            SocatTypes.ETOPO2_DEPTH,
            SocatTypes.GVCO2,
            SocatTypes.DIST_TO_LAND,
            SocatTypes.FCO2_REC,
            SocatTypes.FCO2_SOURCE
    ));

    private static final HashSet<String> STD_USERS_VARNAMES;
    private static final HashSet<String> STD_METADATA_FILES_VARNAMES;
    private static final HashSet<String> STD_DATA_FILES_VARNAMES;

    static {
        STD_USERS_VARNAMES = new HashSet<String>(STD_USERS_TYPES.size());
        for (DashDataType<?> dtype : STD_USERS_TYPES) {
            STD_USERS_VARNAMES.add(dtype.getVarName());
        }
        STD_METADATA_FILES_VARNAMES = new HashSet<String>(STD_METADATA_FILES_TYPES.size());
        for (DashDataType<?> dtype : STD_METADATA_FILES_TYPES) {
            STD_METADATA_FILES_VARNAMES.add(dtype.getVarName());
        }
        STD_DATA_FILES_VARNAMES = new HashSet<String>(STD_DATA_FILES_TYPES.size());
        for (DashDataType<?> dtype : STD_DATA_FILES_TYPES) {
            STD_DATA_FILES_VARNAMES.add(dtype.getVarName());
        }
    }

    private static final String[] ADDN_TYPES_VAR_NAMES = new String[] { "xCO2_atm_dry_interp", "rank", "socat_doi" };
    private static final Double[] ADDN_TYPES_SORT_ORDERS = new Double[] { 34.0, 56.0, 78.0 };
    private static final String[] ADDN_TYPES_DISPLAY_NAMES = new String[] { "xCO2 atm dry", "ranking", "SOCAT DOI" };
    private static final String[] ADDN_TYPES_DESCRIPTIONS = new String[] {
            "mole fraction CO2 in sea level air",
            "personal ranking",
            "DOI of SOCAT-enhanced datafile"
    };
    private static final String[] ADDN_TYPES_STANDARD_NAMES = new String[] { "xCO2_atm", "", "DOI" };
    private static final String[] ADDN_TYPES_CATEGORY_NAMES = new String[] { "CO2", "", "Identifier" };

    private static final ArrayList<String> MOL_FRACTION_UNITS = new ArrayList<String>(
            Arrays.asList("umol/mol", "mmol/mol"));

    private static final DashDataType<?>[] ADDN_DATA_TYPES = new DashDataType<?>[] {
            new DoubleDashDataType(ADDN_TYPES_VAR_NAMES[0], ADDN_TYPES_SORT_ORDERS[0], ADDN_TYPES_DISPLAY_NAMES[0],
                    ADDN_TYPES_DESCRIPTIONS[0], false, MOL_FRACTION_UNITS, ADDN_TYPES_STANDARD_NAMES[0],
                    ADDN_TYPES_CATEGORY_NAMES[0], null, null, null, null, null,
                    DashboardServerUtils.USER_ONLY_ROLES),
            new IntDashDataType(ADDN_TYPES_VAR_NAMES[1], ADDN_TYPES_SORT_ORDERS[1], ADDN_TYPES_DISPLAY_NAMES[1],
                    ADDN_TYPES_DESCRIPTIONS[1], false, MOL_FRACTION_UNITS, ADDN_TYPES_STANDARD_NAMES[1],
                    ADDN_TYPES_CATEGORY_NAMES[1], null, null, null, null, null,
                    DashboardServerUtils.USER_ONLY_ROLES),
            new StringDashDataType(ADDN_TYPES_VAR_NAMES[2], ADDN_TYPES_SORT_ORDERS[2], ADDN_TYPES_DISPLAY_NAMES[2],
                    ADDN_TYPES_DESCRIPTIONS[2], false, MOL_FRACTION_UNITS, ADDN_TYPES_STANDARD_NAMES[2],
                    ADDN_TYPES_CATEGORY_NAMES[2], null, null, null, null, null,
                    DashboardServerUtils.USER_ONLY_ROLES),
    };

    private static final String ADDN_TYPES_PROPERTIES_STRING =
            ADDN_TYPES_VAR_NAMES[0] + " = " + ADDN_DATA_TYPES[0].toPropertyValue() + "\n" +
                    ADDN_TYPES_VAR_NAMES[1] + " = " + ADDN_DATA_TYPES[1].toPropertyValue() + "\n" +
                    ADDN_TYPES_VAR_NAMES[2] + " = " + ADDN_DATA_TYPES[2].toPropertyValue() + "\n";

    /**
     * Test method for {@link KnownDataTypes#addStandardTypesForUsers()},
     * {@link KnownDataTypes#isEmpty()}, and {@link KnownDataTypes#containsTypeName(String)}.
     */
    @Test
    public void testAddStandardTypesForUsers() {
        KnownDataTypes types = new KnownDataTypes();
        assertTrue(types.isEmpty());
        KnownDataTypes other = types.addStandardTypesForUsers();
        assertFalse(types.isEmpty());
        assertSame(types, other);
        assertEquals(STD_USERS_VARNAMES.size(), types.getKnownTypesSet().size());
        for (String varName : STD_USERS_VARNAMES) {
            assertTrue(types.containsTypeName(varName));
        }
    }

    /**
     * Test method for {@link KnownDataTypes#addStandardTypesForMetadataFiles()},
     * {@link KnownDataTypes#containsTypeName(String)}.
     */
    @Test
    public void testAddStandardTypesForMetadataFiles() {
        KnownDataTypes types = new KnownDataTypes();
        KnownDataTypes other = types.addStandardTypesForMetadataFiles();
        assertSame(types, other);
        for (String varName : STD_METADATA_FILES_VARNAMES) {
            assertTrue(types.containsTypeName(varName));
        }
        assertFalse(types.containsTypeName(DashboardUtils.UNKNOWN.getVarName()));
        assertFalse(types.containsTypeName(DashboardUtils.OTHER.getVarName()));
    }

    /**
     * Test method for {@link KnownDataTypes#addStandardTypesForDataFiles()},
     * {@link KnownDataTypes#containsTypeName(String)}.
     */
    @Test
    public void testAddStandardTypesForDataFiles() {
        KnownDataTypes types = new KnownDataTypes();
        KnownDataTypes other = types.addStandardTypesForDataFiles();
        assertSame(types, other);
        for (String varName : STD_DATA_FILES_VARNAMES) {
            assertTrue(types.containsTypeName(varName));
        }
        assertFalse(types.containsTypeName(DashboardUtils.UNKNOWN.getVarName()));
        assertFalse(types.containsTypeName(DashboardUtils.OTHER.getVarName()));
    }

    /**
     * Test method for {@link KnownDataTypes#getKnownTypesSet()}.
     */
    @Test
    public void testGetKnownTypesSet() {
        KnownDataTypes types = new KnownDataTypes().addStandardTypesForUsers();
        assertEquals(STD_USERS_TYPES, types.getKnownTypesSet());
        types = new KnownDataTypes().addStandardTypesForMetadataFiles();
        assertEquals(STD_METADATA_FILES_TYPES, types.getKnownTypesSet());
        types = new KnownDataTypes().addStandardTypesForDataFiles();
        assertEquals(STD_DATA_FILES_TYPES, types.getKnownTypesSet());
    }

    /**
     * Test method for {@link KnownDataTypes#getDataType(String)} and {@link KnownDataTypes#getDataType(DataColumnType)}
     */
    @Test
    public void testGetDataType() {
        KnownDataTypes types = new KnownDataTypes();
        assertNull(types.getDataType("EXPOCODE"));
        types.addStandardTypesForUsers();
        assertEquals(DashboardServerUtils.DATASET_ID, types.getDataType("EXPOCODE"));
        DataColumnType dctype = new DataColumnType("expocode", 5.0, "weird name",
                "weird unique name", false, DashboardUtils.NO_UNITS);
        assertEquals(DashboardServerUtils.DATASET_ID, types.getDataType(dctype));
        dctype = new DataColumnType("weird name", 5.0, "expocode",
                "weird unique name", false, DashboardUtils.NO_UNITS);
        assertEquals(DashboardServerUtils.DATASET_ID, types.getDataType(dctype));
        assertNull(types.getDataType(SHIP_SPEED.duplicate()));
    }

    /**
     * Test method for {@link KnownDataTypes#getKnownTypesSet()}.
     */
    @Test
    public void testGetKnownTypesList() {
        KnownDataTypes types = new KnownDataTypes();
        TreeSet<DashDataType<?>> knownSet = types.getKnownTypesSet();
        assertEquals(0, knownSet.size());
        types.addStandardTypesForMetadataFiles();
        knownSet = types.getKnownTypesSet();
        assertEquals(STD_METADATA_FILES_TYPES, knownSet);
    }

    /**
     * Test method for {@link KnownDataTypes#addTypesFromProperties(Properties, DashDataType.Role, org.apache.logging.log4j.Logger)}
     */
    @Test
    public void testAddTypesFromProperties() throws IOException {
        KnownDataTypes clientTypes = new KnownDataTypes();
        clientTypes.addStandardTypesForUsers();
        StringReader reader = new StringReader(ADDN_TYPES_PROPERTIES_STRING);
        Properties props = new Properties();
        props.load(reader);
        assertEquals(new HashSet<String>(Arrays.asList(ADDN_TYPES_VAR_NAMES)), props.keySet());
        KnownDataTypes other = clientTypes.addTypesFromProperties(props, DashDataType.Role.USER_DATA, null);
        assertSame(clientTypes, other);
        assertEquals(STD_USERS_VARNAMES.size() + ADDN_TYPES_VAR_NAMES.length, clientTypes.getKnownTypesSet().size());
        for (int k = 0; k < ADDN_TYPES_VAR_NAMES.length; k++) {
            assertEquals(ADDN_DATA_TYPES[k], clientTypes.getDataType(ADDN_TYPES_VAR_NAMES[k]));
        }
    }

    /**
     * Test method to produce the properties file of default known data types
     */
    @Test
    public void testCreateKnownTypesPropertiesFile() throws IOException {
        KnownDataTypes usertypes = (new KnownDataTypes()).addStandardTypesForUsers();
        KnownDataTypes metatypes = (new KnownDataTypes()).addStandardTypesForMetadataFiles();
        KnownDataTypes dataTypes = (new KnownDataTypes()).addStandardTypesForDataFiles();
        TreeSet<DashDataType<?>> typesSet = usertypes.getKnownTypesSet();
        typesSet.addAll(metatypes.getKnownTypesSet());
        typesSet.addAll(dataTypes.getKnownTypesSet());
        File propsfile = new File("/var/tmp/junit/KnownTypes.properties");
        PrintWriter propWriter = new PrintWriter(propsfile);
        try {
            propWriter.println("# All known data types");
            propWriter.println("# Standard types are present for reference but have been commented out");
            for (DashDataType<?> dtype : typesSet) {
                String propval = dtype.toPropertyValue().replaceAll(":", "\\\\:").replaceAll("=", "\\\\=");
                propWriter.println("# " + dtype.getVarName() + "=" + propval);
            }
        } finally {
            propWriter.close();
        }
        assertTrue(propsfile.exists());
    }
}
