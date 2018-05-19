/**
 *
 */
package gov.noaa.pmel.dashboard.test.datatype;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.IntDashDataType;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;
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
 * Tests for methods in {@link IntDashDataType}
 * and {@link DashDataType}
 *
 * @author Karl Smith
 */
public class IntDashDataTypeTest {

    private static final String VAR_NAME = "Day_Of_Year";
    private static final Double SORT_ORDER = 3.14159;
    private static final String DISPLAY_NAME = "Day of Year";
    private static final String DESCRIPTION = "day of the year";
    private static final ArrayList<String> UNITS = new ArrayList<String>(Arrays.asList("Jan1=1", "Jan1=0"));
    private static final String STANDARD_NAME = "day_of_year";
    private static final String CATEGORY_NAME = DashboardServerUtils.TIME_CATEGORY;
    private static final Integer[] BOUNDS = new Integer[] { -1, 1, 365, 367 };
    private static final String FILE_UNIT = "startsWithOne";

    /**
     * Test method for {@link IntDashDataType#getDataClassName()}
     */
    @Test
    public void testGetDataClassName() {
        IntDashDataType dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());
        assertEquals(Integer.class.getSimpleName(), dtype.getDataClassName());
    }

    /**
     * Test method for {@link IntDashDataType#dataValueOf(String)}.
     */
    @Test
    public void testDataValueOfString() {
        IntDashDataType dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());
        Integer testVal = 1234;
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
            dtype.dataValueOf("123.4");
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
        IntDashDataType dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());

        ADCMessage msg = dtype.boundsCheckStandardValue(null);
        assertNull(msg);

        Integer testVal = (BOUNDS[1] + BOUNDS[2]) / 2;
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNull(msg);

        testVal = BOUNDS[0] - 1;
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull(msg);
        assertEquals(Severity.CRITICAL, msg.getSeverity());
        assertTrue(msg.getGeneralComment().contains(DashDataType.UNREASONABLY_SMALL_MSG));
        assertTrue(msg.getDetailedComment().contains(DashDataType.UNREASONABLY_SMALL_MSG));

        testVal = BOUNDS[3] + 1;
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull(msg);
        assertEquals(Severity.CRITICAL, msg.getSeverity());
        assertTrue(msg.getGeneralComment().contains(DashDataType.UNREASONABLY_LARGE_MSG));
        assertTrue(msg.getDetailedComment().contains(DashDataType.UNREASONABLY_LARGE_MSG));

        testVal = (BOUNDS[0] + BOUNDS[1]) / 2;
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull(msg);
        assertEquals(Severity.WARNING, msg.getSeverity());
        assertTrue(msg.getGeneralComment().contains(DashDataType.QUESTIONABLY_SMALL_MSG));
        assertTrue(msg.getDetailedComment().contains(DashDataType.QUESTIONABLY_SMALL_MSG));

        testVal = (BOUNDS[2] + BOUNDS[3]) / 2;
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull(msg);
        assertEquals(Severity.WARNING, msg.getSeverity());
        assertTrue(msg.getGeneralComment().contains(DashDataType.QUESTIONABLY_LARGE_MSG));
        assertTrue(msg.getDetailedComment().contains(DashDataType.QUESTIONABLY_LARGE_MSG));
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
        IntDashDataType dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());
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
        IntDashDataType dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null);
        assertFalse(dtype.equals(null));
        assertFalse(dtype.equals(VAR_NAME));
        IntDashDataType other = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        other = new IntDashDataType("blob", SORT_ORDER, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));

        other = new IntDashDataType(VAR_NAME, SORT_ORDER + 1.0, DISPLAY_NAME,
                null, false, null, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));

        other = new IntDashDataType(VAR_NAME, SORT_ORDER, "blob",
                null, false, null, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));

        dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, false, null, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, false, null, null, null, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, null, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, null, null, null, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, null, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, null, null, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, null, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, null, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, null,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, null,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), null, null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), null, null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), null, null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), null, null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), null);
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), null);
        assertEquals(dtype.hashCode(), other.hashCode());
        assertTrue(dtype.equals(other));
        assertNotSame(dtype, other);

        dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());
        assertNotEquals(dtype.hashCode(), other.hashCode());
        assertFalse(dtype.equals(other));
        other = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());
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

        IntDashDataType dtype = new IntDashDataType(EQUAL_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());

        IntDashDataType other = new IntDashDataType(EQUAL_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());
        assertEquals(0, dtype.compareTo(other));

        StringDashDataType another = new StringDashDataType(LESS_VARNAME, SORT_ORDER + 1.0, LESS_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) < 0);
        assertTrue(another.compareTo(dtype) > 0);

        another = new StringDashDataType(GREATER_VARNAME, SORT_ORDER - 1.0, GREATER_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) > 0);
        assertTrue(another.compareTo(dtype) < 0);

        another = new StringDashDataType(LESS_VARNAME, SORT_ORDER, GREATER_DISPLAYNAME, DESCRIPTION,
                true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) < 0);
        assertTrue(another.compareTo(dtype) > 0);

        another = new StringDashDataType(GREATER_VARNAME, SORT_ORDER, LESS_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) > 0);
        assertTrue(another.compareTo(dtype) < 0);

        another = new StringDashDataType(GREATER_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) < 0);
        assertTrue(another.compareTo(dtype) > 0);

        another = new StringDashDataType(LESS_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) > 0);
        assertTrue(another.compareTo(dtype) < 0);

        another = new StringDashDataType(EQUAL_VARNAME, SORT_ORDER, EQUAL_DISPLAYNAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                null, null, null, null);
        assertTrue(dtype.compareTo(another) < 0);
        assertTrue(another.compareTo(dtype) > 0);
    }

    /**
     * Test method for {@link DashDataType#duplicate()} and
     * {@link IntDashDataType#IntDashDataType(DataColumnType, String, String, String, String, String, String, String)}
     */
    @Test
    public void testDuplicateIntDashDataType() {
        IntDashDataType dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());
        DataColumnType colType = new DataColumnType(VAR_NAME, SORT_ORDER, DISPLAY_NAME, DESCRIPTION, true, UNITS);
        assertEquals(colType, dtype.duplicate());
        IntDashDataType other = new IntDashDataType(colType, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());
        assertEquals(dtype, other);
    }

    /**
     * Test method for {@link DashDataType#typeNameEquals(String)},
     * {@link DashDataType#typeNameEquals(DashDataType)}, and
     * {@link DashDataType#typeNameEquals(DataColumnType)},
     */
    @Test
    public void testTypeNameEquals() {
        IntDashDataType dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());
        assertTrue(dtype.typeNameEquals(VAR_NAME));
        assertTrue(dtype.typeNameEquals(VAR_NAME.toUpperCase()));
        assertTrue(dtype.typeNameEquals(VAR_NAME.toLowerCase()));
        assertTrue(dtype.typeNameEquals(VAR_NAME.replaceAll("_", "")));
        assertTrue(dtype.typeNameEquals(DISPLAY_NAME));
        assertFalse(dtype.typeNameEquals((String) null));
        assertFalse(dtype.typeNameEquals(DESCRIPTION));

        StringDashDataType other = new StringDashDataType(DISPLAY_NAME, SORT_ORDER, VAR_NAME,
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
        // IntDashDataType can never be a QC/WOCE flag or a comment
        IntDashDataType dtype = new IntDashDataType("WOCE", 1.0, "WOCE flag",
                "WOCE flag", false, DashboardUtils.NO_UNITS, "WOCE_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertFalse(dtype.isQCType());
        dtype = new IntDashDataType("QC", 1.0, "QC flag",
                "QC flag", false, DashboardUtils.NO_UNITS, "QC_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertFalse(dtype.isQCType());
        IntDashDataType other = new IntDashDataType("QC_Comment", 1.0, "QC flag comment",
                "Comment for QC flag", false, DashboardUtils.NO_UNITS, "Comment",
                null, null, null, null, null, null);
        assertFalse(other.isCommentType());
        assertFalse(other.isCommentTypeFor(dtype));
    }

    /**
     * Test method for {@link DashDataType#toPropertyValue()}, and
     * {@link DashDataType#fromPropertyValue(String, String)}.
     */
    @Test
    public void testToFromPropertyValue() {
        IntDashDataType dtype = new IntDashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME, FILE_UNIT,
                BOUNDS[0].toString(), BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());

        String propValStr = dtype.toPropertyValue();
        assertFalse(propValStr.contains(VAR_NAME));
        assertTrue(propValStr.contains(Integer.class.getSimpleName()));
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

        DashDataType<?> other = DashDataType.fromPropertyValue(VAR_NAME, propValStr);
        assertEquals(dtype, other);
    }

}
