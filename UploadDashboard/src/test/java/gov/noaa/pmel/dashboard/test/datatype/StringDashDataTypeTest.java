/**
 *
 */
package gov.noaa.pmel.dashboard.test.datatype;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.DoubleDashDataType;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for methods in {@link StringDashDataType} and some methods in {@link DashDataType}
 *
 * @author Karl Smith
 */
public class StringDashDataTypeTest {

    private static final String VAR_NAME = "dataset_name";
    private static final Double SORT_ORDER = 3.14159;
    private static final String DISPLAY_NAME = "cruise name";
    private static final String DESCRIPTION = "name of the cruise/dataset";
    private static final ArrayList<String> UNITS = new ArrayList<String>(Arrays.asList("expocode", "actual"));
    private static final String STANDARD_NAME = "name";
    private static final String CATEGORY_NAME = DashboardServerUtils.IDENTIFIER_CATEGORY;
    private static final String FILE_UNIT = "IDValue";

    /**
     * Test method for {@link StringDashDataType#getDataClassName()}
     */
    @Test
    public void testGetDataClassName() {
        StringDashDataType dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertEquals(String.class.getSimpleName(), dtype.getDataClassName());
    }

    /**
     * Test method for {@link StringDashDataType#dataValueOf(String)}.
     */
    @Test
    public void testDataValueOfString() {
        StringDashDataType dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        String testVal = " my dataset ID ";
        assertEquals(testVal.trim(), dtype.dataValueOf(testVal));
        boolean caught;
        try {
            dtype.dataValueOf(null);
            caught = false;
        } catch ( IllegalArgumentException ex ) {
            caught = true;
        }
        assertTrue(caught);
    }

    /**
     * Test method for {@link DashDataType#boundsCheckStandardValue(Comparable)}.
     */
    @Test
    public void testBoundsCheckStandardValue() {
        StringDashDataType dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);

        ADCMessage msg = dtype.boundsCheckStandardValue(null);
        assertNull(msg);

        msg = dtype.boundsCheckStandardValue("");
        assertNull(msg);

        msg = dtype.boundsCheckStandardValue("ABCD20161226");
        assertNull(msg);
    }

    /**
     * Test method for {@link DashDataType#getVarName()},
     * {@link DashDataType#getSortOrder()},
     * {@link DashDataType#getDisplayName()},
     * {@link DashDataType#getDescription()},
     * {@link DashDataType#getUnits()},
     * {@link DashDataType#getStandardName()}, and
     * {@link DashDataType#getCategoryName()}.
     */
    @Test
    public void testGetVarName() {
        StringDashDataType dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertEquals(VAR_NAME, dtype.getVarName());
        assertEquals(SORT_ORDER, dtype.getSortOrder());
        assertEquals(DISPLAY_NAME, dtype.getDisplayName());
        assertEquals(DESCRIPTION, dtype.getDescription());
        assertEquals(UNITS, dtype.getUnits());
        assertNotSame(UNITS, dtype.getUnits());
        assertEquals(STANDARD_NAME, dtype.getStandardName());
        assertEquals(CATEGORY_NAME, dtype.getCategoryName());
    }

    /**
     * Test method for {@link DashDataType#hashCode()} and {@link DashDataType#equals(Object)}.
     */
    @Test
    public void testHashCodeEqualsObject() {
        StringDashDataType dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null);
        assertFalse(dtype.equals(null));
        assertFalse(dtype.equals(VAR_NAME));
        StringDashDataType other = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        other = new StringDashDataType("blob", SORT_ORDER, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));

        other = new StringDashDataType(VAR_NAME, SORT_ORDER + 1.0, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));

        other = new StringDashDataType(VAR_NAME, SORT_ORDER, "blob",
                null, false, null, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));

        dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, false, null, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, false, null, null, null, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, null, null, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, null, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, null, null, null, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, null, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
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

        StringDashDataType dtype = new StringDashDataType(EQUAL_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);

        StringDashDataType other = new StringDashDataType(EQUAL_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertEquals(0, dtype.compareTo(other));

        DoubleDashDataType another = new DoubleDashDataType(LESS_VARNAME, SORT_ORDER + 1.0, LESS_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) < 0);
        assertTrue(another.compareTo(dtype) > 0);

        another = new DoubleDashDataType(GREATER_VARNAME, SORT_ORDER - 1.0, GREATER_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) > 0);
        assertTrue(another.compareTo(dtype) < 0);

        another = new DoubleDashDataType(LESS_VARNAME, SORT_ORDER, GREATER_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) < 0);
        assertTrue(another.compareTo(dtype) > 0);

        another = new DoubleDashDataType(GREATER_VARNAME, SORT_ORDER, LESS_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) > 0);
        assertTrue(another.compareTo(dtype) < 0);

        another = new DoubleDashDataType(GREATER_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) < 0);
        assertTrue(another.compareTo(dtype) > 0);

        another = new DoubleDashDataType(LESS_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) > 0);
        assertTrue(another.compareTo(dtype) < 0);

        another = new DoubleDashDataType(EQUAL_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) > 0);
        assertTrue(another.compareTo(dtype) < 0);
    }

    /**
     * Test method for {@link DashDataType#duplicate()} and
     * {@link StringDashDataType#StringDashDataType(DataColumnType,
     * String, String, String, String, String, String, String)}
     */
    @Test
    public void testDuplicateStringDashDataType() {
        StringDashDataType dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        DataColumnType colType = new DataColumnType(VAR_NAME, SORT_ORDER, DISPLAY_NAME, DESCRIPTION, true, UNITS);
        assertEquals(colType, dtype.duplicate());
        StringDashDataType other = new StringDashDataType(colType, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertEquals(dtype, other);
    }

    /**
     * Test method for {@link DashDataType#typeNameEquals(String)},
     * {@link DashDataType#typeNameEquals(DashDataType)}, and
     * {@link DashDataType#typeNameEquals(DataColumnType)},
     */
    @Test
    public void testTypeNameEquals() {
        StringDashDataType dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.typeNameEquals(VAR_NAME));
        assertTrue(dtype.typeNameEquals(VAR_NAME.toUpperCase()));
        assertTrue(dtype.typeNameEquals(VAR_NAME.toLowerCase()));
        assertTrue(dtype.typeNameEquals(VAR_NAME.replaceAll("_", "")));
        assertTrue(dtype.typeNameEquals(DISPLAY_NAME));
        assertFalse(dtype.typeNameEquals((String) null));
        assertFalse(dtype.typeNameEquals(DESCRIPTION));

        DoubleDashDataType other = new DoubleDashDataType(DISPLAY_NAME, SORT_ORDER, VAR_NAME,
                null, false, null, null, null, null,
                null, null, null, null);
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
        StringDashDataType dtype = new StringDashDataType("WOCE", 1.0, "WOCE flag",
                "WOCE flag", false, DashboardUtils.NO_UNITS, "WOCE_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue(dtype.isQCType());
        dtype = new StringDashDataType("WOCE_xCO2", 1.0, "WOCE xCO2",
                "WOCE flag for xCO2", false, DashboardUtils.NO_UNITS, "WOCE_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue(dtype.isQCType());
        dtype = new StringDashDataType("xCO2_woce", 1.0, "xCO2 WOCE flag",
                "WOCE flag for xCO2", false, DashboardUtils.NO_UNITS, "WOCE_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue(dtype.isQCType());
        dtype = new StringDashDataType("QC_xCO2", 1.0, "QC xCO2",
                "QC flag for xCO2", false, DashboardUtils.NO_UNITS, "QC_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue(dtype.isQCType());
        dtype = new StringDashDataType("xCO2_qc", 1.0, "xCO2 QC flag",
                "QC flag for xCO2", false, DashboardUtils.NO_UNITS, "QC_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue(dtype.isQCType());
        dtype = new StringDashDataType("QC", 1.0, "QC flag",
                "QC flag", false, DashboardUtils.NO_UNITS, "QC_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue(dtype.isQCType());
        StringDashDataType other = new StringDashDataType("QC_Comment", 1.0, "QC flag comment",
                "Comment for QC flag", false, DashboardUtils.NO_UNITS, "Comment",
                null, null, null, null, null, null);
        assertTrue(other.isCommentType());
        assertTrue(other.isCommentTypeFor(dtype));
    }

    /**
     * Test method for {@link DashDataType#toPropertyValue()}, and
     * {@link DashDataType#fromPropertyValue(String, String)}.
     */
    @Test
    public void testToFromPropertyValue() {
        StringDashDataType dtype = new StringDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);

        String propValStr = dtype.toPropertyValue();
        assertFalse(propValStr.contains(VAR_NAME));
        assertTrue(propValStr.contains(String.class.getSimpleName()));
        assertTrue(propValStr.contains(SORT_ORDER.toString()));
        assertTrue(propValStr.contains(DISPLAY_NAME));
        assertTrue(propValStr.contains(DESCRIPTION));
        assertTrue(propValStr.contains("true"));
        for (String value : UNITS) {
            assertTrue(propValStr.contains(value));
        }
        assertTrue(propValStr.contains(STANDARD_NAME));
        assertTrue(propValStr.contains(FILE_UNIT));
        assertFalse(propValStr.contains(DashDataType.MIN_QUESTIONABLE_VALUE_TAG));
        assertFalse(propValStr.contains(DashDataType.MIN_ACCEPTABLE_VALUE_TAG));
        assertFalse(propValStr.contains(DashDataType.MAX_ACCEPTABLE_VALUE_TAG));
        assertFalse(propValStr.contains(DashDataType.MAX_QUESTIONABLE_VALUE_TAG));

        DashDataType<?> other = DashDataType.fromPropertyValue(VAR_NAME, propValStr);
        assertEquals(dtype, other);
    }

}
