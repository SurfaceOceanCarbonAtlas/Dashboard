package gov.noaa.pmel.dashboard.test.datatype;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DataQCFlag.Severity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for methods in {@link DoubleDashDataType} and {@link DashDataType}
 *
 * @author Karl Smith
 */
public class DoubleDashDataTypeTest {

    private static final String VAR_NAME = "xCO2_atm_dry";
    private static final Double SORT_ORDER = 3.14159;
    private static final String DISPLAY_NAME = "xCO2 atm dry";
    private static final String DESCRIPTION = "mole fraction of carbon dioxide in air";
    private static final ArrayList<String> UNITS = new ArrayList<String>(Arrays.asList("umol/mol", "mmol/mol"));
    private static final String STANDARD_NAME = "mole_fraction_co2";
    private static final String CATEGORY_NAME = "ATMOSPHERIC_CO2";
    private static final String FILE_UNIT = "ppm";
    private static final Double[] BOUNDS = new Double[] { 10.0, 50.0, 3000.0, 10000.0 };

    /**
     * Test method for {@link DoubleDashDataType#getDataClassName()}
     */
    @Test
    public void testGetDataClassName() {
        DoubleDashDataType dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(Double.class.getSimpleName(), dtype.getDataClassName());
    }

    /**
     * Test method for {@link DoubleDashDataType#dataValueOf(String)}.
     */
    @Test
    public void testDataValueOfString() {
        DoubleDashDataType dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        Double testVal = 1234.0;
        assertEquals(testVal, dtype.dataValueOf(testVal.toString()));
        boolean caught;
        try {
            dtype.dataValueOf(null);
            caught = false;
        } catch ( IllegalArgumentException ex ) {
            caught = true;
        }
        assertTrue(caught);
        try {
            dtype.dataValueOf("~1000");
            caught = false;
        } catch ( IllegalArgumentException ex ) {
            caught = true;
        }
        assertTrue(caught);
    }

    /**
     * Test method for {@link DashDataType#boundsCheckStandardValue(Comparable)}
     */
    @Test
    public void testBoundsCheckStandardValue() {
        DoubleDashDataType dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, false, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);

        ADCMessage msg = dtype.boundsCheckStandardValue(null);
        assertNull(msg);

        Double testVal = 0.5 * (BOUNDS[1] + BOUNDS[2]);
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNull(msg);

        testVal = BOUNDS[0] - 1.0;
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull(msg);
        assertEquals(Severity.ERROR, msg.getSeverity());
        assertTrue(msg.getGeneralComment().contains(DashDataType.UNREASONABLY_SMALL_MSG));
        assertTrue(msg.getDetailedComment().contains(DashDataType.UNREASONABLY_SMALL_MSG));

        testVal = BOUNDS[3] + 1.0;
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull(msg);
        assertEquals(Severity.ERROR, msg.getSeverity());
        assertTrue(msg.getGeneralComment().contains(DashDataType.UNREASONABLY_LARGE_MSG));
        assertTrue(msg.getDetailedComment().contains(DashDataType.UNREASONABLY_LARGE_MSG));

        testVal = 0.5 * (BOUNDS[0] + BOUNDS[1]);
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull(msg);
        assertEquals(Severity.WARNING, msg.getSeverity());
        assertTrue(msg.getGeneralComment().contains(DashDataType.QUESTIONABLY_SMALL_MSG));
        assertTrue(msg.getDetailedComment().contains(DashDataType.QUESTIONABLY_SMALL_MSG));

        testVal = 0.5 * (BOUNDS[2] + BOUNDS[3]);
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull(msg);
        assertEquals(Severity.WARNING, msg.getSeverity());
        assertTrue(msg.getGeneralComment().contains(DashDataType.QUESTIONABLY_LARGE_MSG));
        assertTrue(msg.getDetailedComment().contains(DashDataType.QUESTIONABLY_LARGE_MSG));
    }

    /**
     * Test method for {@link DashDataType#getVarName()}, {@link DashDataType#getSortOrder()},
     * {@link DashDataType#getDisplayName()}, {@link DashDataType#getDescription()},
     * {@link DashDataType#getUnits()}, {@link DashDataType#getStandardName()}, and
     * {@link DashDataType#getCategoryName()}.
     */
    @Test
    public void testGetVarName() {
        DoubleDashDataType dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(VAR_NAME, dtype.getVarName());
        assertEquals(SORT_ORDER, dtype.getSortOrder());
        assertEquals(DISPLAY_NAME, dtype.getDisplayName());
        assertEquals(DESCRIPTION, dtype.getDescription());
        assertEquals(UNITS, dtype.getUnits());
        assertNotSame(UNITS, dtype.getUnits());
        assertEquals(STANDARD_NAME, dtype.getStandardName());
        assertEquals(CATEGORY_NAME, dtype.getCategoryName());
        assertEquals(DashboardServerUtils.USER_FILE_DATA_ROLES, dtype.getRoles());
    }

    /**
     * Test method for {@link DashDataType#hashCode()} and {@link DashDataType#equals(Object)}.
     */
    @Test
    public void testHashCodeEqualsObject() {
        DoubleDashDataType dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertFalse(dtype.equals(null));
        assertFalse(dtype.equals(VAR_NAME));
        DoubleDashDataType other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        other = new DoubleDashDataType("blob", SORT_ORDER, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));

        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER + 1.0, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));

        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, "blob",
                null, false, null, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));

        dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, false, null, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, false, null, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, null, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, null, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_ONLY_ROLES);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_ONLY_ROLES);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);
    }

    /**
     * Test method for {@link DashDataType#compareTo(DashDataType)}.
     */
    @Test
    public void testCompareTo() {
        final String LESS_VARNAME = "AAAA";
        final String EQUAL_VARNAME = "BBBB";
        final String GREATER_VARNAME = "CCCC";
        final String LESS_DISPLAYNAME = "FFFF";
        final String EQUAL_DISPLAYNAME = "GGGG";
        final String GREATER_DISPLAYNAME = "HHHH";

        DoubleDashDataType dtype = new DoubleDashDataType(EQUAL_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);

        DoubleDashDataType other = new DoubleDashDataType(EQUAL_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(0, dtype.compareTo(other));

        StringDashDataType another = new StringDashDataType(LESS_VARNAME, SORT_ORDER + 1.0, LESS_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertTrue(dtype.compareTo(another) < 0);
        assertTrue(another.compareTo(dtype) > 0);

        another = new StringDashDataType(GREATER_VARNAME, SORT_ORDER - 1.0, GREATER_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertTrue(dtype.compareTo(another) > 0);
        assertTrue(another.compareTo(dtype) < 0);

        another = new StringDashDataType(LESS_VARNAME, SORT_ORDER, GREATER_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertTrue(dtype.compareTo(another) < 0);
        assertTrue(another.compareTo(dtype) > 0);

        another = new StringDashDataType(GREATER_VARNAME, SORT_ORDER, LESS_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertTrue(dtype.compareTo(another) > 0);
        assertTrue(another.compareTo(dtype) < 0);

        another = new StringDashDataType(GREATER_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertTrue(dtype.compareTo(another) < 0);
        assertTrue(another.compareTo(dtype) > 0);

        another = new StringDashDataType(LESS_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertTrue(dtype.compareTo(another) > 0);
        assertTrue(another.compareTo(dtype) < 0);

        another = new StringDashDataType(EQUAL_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertTrue(dtype.compareTo(another) < 0);
        assertTrue(another.compareTo(dtype) > 0);

        other = new DoubleDashDataType(EQUAL_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_ONLY_ROLES);
        assertTrue(dtype.compareTo(other) > 0);
        assertTrue(other.compareTo(dtype) < 0);

        other = new DoubleDashDataType(EQUAL_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_METADATA_ROLES);
        assertTrue(dtype.compareTo(other) > 0);
        assertTrue(other.compareTo(dtype) < 0);
    }

    /**
     * Test method for {@link DashDataType#duplicate()} and {@link DoubleDashDataType#DoubleDashDataType(DataColumnType,
     * String, String, String, String, String, String, String, java.util.Collection)}
     */
    @Test
    public void testDuplicateDoubleDashDataType() {
        DoubleDashDataType dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        DataColumnType colType = new DataColumnType(VAR_NAME, SORT_ORDER, DISPLAY_NAME, DESCRIPTION, true, UNITS);
        assertEquals(colType, dtype.duplicate());
        DoubleDashDataType other = new DoubleDashDataType(colType, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertEquals(dtype, other);
    }

    /**
     * Test method for {@link DashDataType#typeNameEquals(String)},
     * {@link DashDataType#typeNameEquals(DashDataType)}, and
     * {@link DashDataType#typeNameEquals(DataColumnType)}
     */
    @Test
    public void testTypeNameEquals() {
        DoubleDashDataType dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertTrue(dtype.typeNameEquals(VAR_NAME));
        assertTrue(dtype.typeNameEquals(VAR_NAME.toUpperCase()));
        assertTrue(dtype.typeNameEquals(VAR_NAME.toLowerCase()));
        assertTrue(dtype.typeNameEquals(VAR_NAME.replaceAll("_", "")));
        assertTrue(dtype.typeNameEquals(DISPLAY_NAME));
        assertFalse(dtype.typeNameEquals((String) null));
        assertFalse(dtype.typeNameEquals(DESCRIPTION));

        StringDashDataType other = new StringDashDataType(DISPLAY_NAME, SORT_ORDER, VAR_NAME,
                null, false, null, null, null, null,
                null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertTrue(dtype.typeNameEquals(other));
        assertTrue(other.typeNameEquals(dtype));
        assertFalse(dtype.typeNameEquals((DashDataType<?>) null));

        assertTrue(dtype.typeNameEquals(other.duplicate()));
        assertFalse(dtype.typeNameEquals((DataColumnType) null));
    }

    /**
     * Test method for {@link DashDataType#isQCType()},
     * {@link DashDataType#isCommentType()}, and
     * {@link DashDataType#isCommentTypeFor(DashDataType)}.
     */
    @Test
    public void testIsQCType() {
        // DoubleDashDataType can never be a QC/WOCE flag or a comment
        DoubleDashDataType dtype = new DoubleDashDataType("WOCE", 1.0, "WOCE flag",
                "WOCE flag", false, DashboardUtils.NO_UNITS, "WOCE_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertFalse(dtype.isQCType());
        dtype = new DoubleDashDataType("QC", 1.0, "QC flag",
                "QC flag", false, DashboardUtils.NO_UNITS, "QC_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertFalse(dtype.isQCType());
        DoubleDashDataType other = new DoubleDashDataType("QC_Comment", 1.0, "QC flag comment",
                "Comment for QC flag", false, DashboardUtils.NO_UNITS, "Comment",
                null, null, null, null, null, null,
                DashboardServerUtils.USER_FILE_DATA_ROLES);
        assertFalse(other.isCommentType());
        assertFalse(other.isCommentTypeFor(dtype));
    }

    /**
     * Test method for {@link DashDataType#toPropertyValue()}, and
     * {@link DashDataType#fromPropertyValue(String, String)}.
     */
    @Test
    public void testToFromPropertyValue() {
        DoubleDashDataType dtype = new DoubleDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString(),
                DashboardServerUtils.USER_FILE_DATA_ROLES);

        String propValStr = dtype.toPropertyValue();
        assertFalse(propValStr.contains(VAR_NAME));
        assertTrue(propValStr.contains(Double.class.getSimpleName()));
        assertTrue(propValStr.contains(SORT_ORDER.toString()));
        assertTrue(propValStr.contains(DISPLAY_NAME));
        assertTrue(propValStr.contains(DESCRIPTION));
        assertTrue(propValStr.contains("true"));
        for (String value : UNITS) {
            assertTrue(propValStr.contains(value));
        }
        assertTrue(propValStr.contains(STANDARD_NAME));
        assertTrue(propValStr.contains(FILE_UNIT));
        assertTrue(propValStr.contains(BOUNDS[0].toString()));
        assertTrue(propValStr.contains(BOUNDS[1].toString()));
        assertTrue(propValStr.contains(BOUNDS[2].toString()));
        assertTrue(propValStr.contains(BOUNDS[3].toString()));
        for (DashDataType.Role datarole : DashboardServerUtils.USER_FILE_DATA_ROLES) {
            assertTrue(propValStr.contains(datarole.name()));
        }

        DashDataType<?> other = DashDataType.fromPropertyValue(VAR_NAME, propValStr);
        assertEquals(dtype, other);
    }

}
