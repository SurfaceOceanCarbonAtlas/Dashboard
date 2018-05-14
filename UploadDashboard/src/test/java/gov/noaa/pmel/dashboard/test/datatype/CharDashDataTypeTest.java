/**
 *
 */
package gov.noaa.pmel.dashboard.test.datatype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import gov.noaa.pmel.dashboard.datatype.CharDashDataType;
import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.StringDashDataType;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

/**
 * Tests for methods in {@link gov.noaa.pmel.dashboard.datatype.CharDashDataType}
 * and {@link gov.noaa.pmel.dashboard.datatype.DashDataType}
 *
 * @author Karl Smith
 */
public class CharDashDataTypeTest {

    private static final String VAR_NAME = "QC_xCO2_water";
    private static final Double SORT_ORDER = 3.14159;
    private static final String DISPLAY_NAME = "QC xCO2 water";
    private static final String DESCRIPTION = "mole fraction of carbon dioxide in water";
    private static final ArrayList<String> UNITS = new ArrayList<String>(Arrays.asList("WOCE", "bottle"));
    private static final String STANDARD_NAME = "woce_flag";
    private static final String CATEGORY_NAME = DashboardServerUtils.QUALITY_CATEGORY;
    private static final Character[] BOUNDS = new Character[] { '1', '2', '4', '9' };
    private static final String FILE_UNIT = "flagValue";

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.datatype.CharDashDataType#getDataClassName()}
     */
    @Test
    public void testGetDataClassName() {
        CharDashDataType dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, BOUNDS[0].toString(), BOUNDS[1].toString(),
                BOUNDS[2].toString(), BOUNDS[3].toString());
        assertNotNull ( dtype );
        assertEquals(Character.class.getSimpleName(), dtype.getDataClassName());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.datatype.CharDashDataType#dataValueOf(java.lang.String)}.
     */
    @Test
    public void testDataValueOfString() {
        CharDashDataType dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, BOUNDS[0].toString(), BOUNDS[1].toString(),
                BOUNDS[2].toString(), BOUNDS[3].toString());
        Character testVal = '3';
        assertEquals(testVal, dtype.dataValueOf("   " + testVal + "   "));
        boolean caught;
        try {
            dtype.dataValueOf(null);
            caught = false;
        } catch ( IllegalArgumentException ex ) {
            caught = true;
        }
        assertTrue( caught );
        try {
            dtype.dataValueOf("24");
            caught = false;
        } catch ( IllegalArgumentException ex ) {
            caught = true;
        }
        assertTrue( caught );
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.datatype.CharDashDataType#boundsCheckStandardValue(java.lang.Character)}.
     */
    @Test
    public void testBoundsCheckStandardValue() {
        CharDashDataType dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, false, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, BOUNDS[0].toString(), BOUNDS[1].toString(),
                BOUNDS[2].toString(), BOUNDS[3].toString());

        ADCMessage msg = dtype.boundsCheckStandardValue(null);
        assertNull( msg );

        Character testVal = '3';
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNull( msg );

        testVal = '0';
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull( msg );
        assertEquals(Severity.ERROR, msg.getSeverity());
        assertTrue( msg.getGeneralComment().contains(DashDataType.UNREASONABLY_SMALL_MSG));
        assertTrue( msg.getDetailedComment().contains(DashDataType.UNREASONABLY_SMALL_MSG));

        testVal = 'A';
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull( msg );
        assertEquals(Severity.ERROR, msg.getSeverity());
        assertTrue( msg.getGeneralComment().contains(DashDataType.UNREASONABLY_LARGE_MSG));
        assertTrue( msg.getDetailedComment().contains(DashDataType.UNREASONABLY_LARGE_MSG));

        testVal = '1';
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull( msg );
        assertEquals(Severity.WARNING, msg.getSeverity());
        assertTrue( msg.getGeneralComment().contains(DashDataType.QUESTIONABLY_SMALL_MSG));
        assertTrue( msg.getDetailedComment().contains(DashDataType.QUESTIONABLY_SMALL_MSG));

        testVal = '5';
        msg = dtype.boundsCheckStandardValue(testVal);
        assertNotNull( msg );
        assertEquals(Severity.WARNING, msg.getSeverity());
        assertTrue( msg.getGeneralComment().contains(DashDataType.QUESTIONABLY_LARGE_MSG));
        assertTrue( msg.getDetailedComment().contains(DashDataType.QUESTIONABLY_LARGE_MSG));
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.datatype.DashDataType#getVarName()},
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#getSortOrder()},
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#getDisplayName()},
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#getDescription()},
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#getUnits()},
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#getStandardName()}, and
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#getCAtegoryName()}.
     */
    @Test
    public void testGetVarName() {
        CharDashDataType dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, BOUNDS[0].toString(), BOUNDS[1].toString(),
                BOUNDS[2].toString(), BOUNDS[3].toString());
        assertEquals(VAR_NAME, dtype.getVarName());
        assertEquals(SORT_ORDER, dtype.getSortOrder());
        assertEquals(DISPLAY_NAME, dtype.getDisplayName());
        assertEquals(DESCRIPTION, dtype.getDescription());
        assertEquals(UNITS, dtype.getUnits());
        assertFalse( UNITS == dtype.getUnits() );
        assertEquals(STANDARD_NAME, dtype.getStandardName());
        assertEquals(CATEGORY_NAME, dtype.getCategoryName());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.datatype.DashDataType#hashCode()} and
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#equals(java.lang.Object)}.
     */
    @Test
    public void testHashCodeEqualsObject() {
        CharDashDataType dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, null, false, null, null, null, null, null, null, null, null);
        assertFalse( dtype.equals(null) );
        assertFalse( dtype.equals(VAR_NAME) );
        CharDashDataType other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, null, false, null, null, null, null, null, null, null, null);
        assertTrue( dtype.hashCode() == other.hashCode() );
        assertTrue( dtype.equals(other) );
        assertFalse( dtype == other );

        other = new CharDashDataType("blob", SORT_ORDER,
                DISPLAY_NAME, null, false, null, null, null, null, null, null, null, null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );

        other = new CharDashDataType(VAR_NAME, SORT_ORDER + 1.0,
                DISPLAY_NAME, null, false, null, null, null, null, null, null, null, null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );

        other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                "blob", null, false, null, null, null, null, null, null, null, null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );

        dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, false, null, null,
                null, null, null, null, null, null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );
        other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, false, null, null,
                null, null, null, null, null, null);
        assertTrue( dtype.hashCode() == other.hashCode() );
        assertTrue( dtype.equals(other) );
        assertFalse( dtype == other );

        dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, null,
                null, null, null, null, null, null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );
        other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, null,
                null, null, null, null, null, null);
        assertTrue( dtype.hashCode() == other.hashCode() );
        assertTrue( dtype.equals(other) );
        assertFalse( dtype == other );

        dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, null, null,
                null, null, null, null, null, null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );
        other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, null, null,
                null, null, null, null, null, null);
        assertTrue( dtype.hashCode() == other.hashCode() );
        assertTrue( dtype.equals(other) );
        assertFalse( dtype == other );

        dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                null, null, null, null, null, null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );
        other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                null, null, null, null, null, null);
        assertTrue( dtype.hashCode() == other.hashCode() );
        assertTrue( dtype.equals(other) );
        assertFalse( dtype == other );

        dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, null, null, null, null, null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );
        other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, null, null, null, null, null);
        assertTrue( dtype.hashCode() == other.hashCode() );
        assertTrue( dtype.equals(other) );
        assertFalse( dtype == other );

        dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, FILE_UNIT, null, null, null, null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );
        other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, FILE_UNIT, null, null, null, null);
        assertTrue( dtype.hashCode() == other.hashCode() );
        assertTrue( dtype.equals(other) );
        assertFalse( dtype == other );

        dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, FILE_UNIT, BOUNDS[0].toString(),
                null, null, null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );
        other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, FILE_UNIT, BOUNDS[0].toString(),
                null, null, null);
        assertTrue( dtype.hashCode() == other.hashCode() );
        assertTrue( dtype.equals(other) );
        assertFalse( dtype == other );

        dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, FILE_UNIT, BOUNDS[0].toString(),
                BOUNDS[1].toString(), null, null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );
        other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, FILE_UNIT, BOUNDS[0].toString(),
                BOUNDS[1].toString(), null, null);
        assertTrue( dtype.hashCode() == other.hashCode() );
        assertTrue( dtype.equals(other) );
        assertFalse( dtype == other );

        dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, FILE_UNIT, BOUNDS[0].toString(),
                BOUNDS[1].toString(), BOUNDS[2].toString(), null);
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );
        other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, FILE_UNIT, BOUNDS[0].toString(),
                BOUNDS[1].toString(), BOUNDS[2].toString(), null);
        assertTrue( dtype.hashCode() == other.hashCode() );
        assertTrue( dtype.equals(other) );
        assertFalse( dtype == other );

        dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, FILE_UNIT, BOUNDS[0].toString(),
                BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());
        assertFalse( dtype.hashCode() == other.hashCode() );
        assertFalse( dtype.equals(other) );
        other = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME,
                CATEGORY_NAME, FILE_UNIT, BOUNDS[0].toString(),
                BOUNDS[1].toString(), BOUNDS[2].toString(), BOUNDS[3].toString());
        assertTrue( dtype.hashCode() == other.hashCode() );
        assertTrue( dtype.equals(other) );
        assertFalse( dtype == other );
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.datatype.DashDataType#compareTo(gov.noaa.pmel.dashboard.datatype.DashDataType)}.
     */
    @Test
    public void testCompareTo() {
        final String LESS_VARNAME = "AAAA";
        final String EQUAL_VARNAME = "BBBB";
        final String GREATER_VARNAME = "CCCC";
        final String LESS_DISPLAYNAME = "FFFF";
        final String EQUAL_DISPLAYNAME = "GGGG";
        final String GREATER_DISPLAYNAME = "HHHH";

        CharDashDataType dtype = new CharDashDataType(EQUAL_VARNAME, SORT_ORDER,
                EQUAL_DISPLAYNAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, BOUNDS[0].toString(), BOUNDS[1].toString(),
                BOUNDS[2].toString(), BOUNDS[3].toString());

        CharDashDataType other = new CharDashDataType(EQUAL_VARNAME, SORT_ORDER,
                EQUAL_DISPLAYNAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, BOUNDS[0].toString(), BOUNDS[1].toString(),
                BOUNDS[2].toString(), BOUNDS[3].toString());
        assertTrue( dtype.compareTo(other) == 0 );

        StringDashDataType another = new StringDashDataType(LESS_VARNAME, SORT_ORDER + 1.0,
                LESS_DISPLAYNAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, null, null, null, null);
        assertTrue( dtype.compareTo(another) < 0 );
        assertTrue( another.compareTo(dtype) > 0 );

        another = new StringDashDataType(GREATER_VARNAME, SORT_ORDER - 1.0,
                GREATER_DISPLAYNAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, null, null, null, null);
        assertTrue( dtype.compareTo(another) > 0 );
        assertTrue( another.compareTo(dtype) < 0 );

        another = new StringDashDataType(LESS_VARNAME, SORT_ORDER,
                GREATER_DISPLAYNAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, null, null, null, null);
        assertTrue( dtype.compareTo(another) < 0 );
        assertTrue( another.compareTo(dtype) > 0 );

        another = new StringDashDataType(GREATER_VARNAME, SORT_ORDER,
                LESS_DISPLAYNAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, null, null, null, null);
        assertTrue( dtype.compareTo(another) > 0 );
        assertTrue( another.compareTo(dtype) < 0 );

        another = new StringDashDataType(GREATER_VARNAME, SORT_ORDER,
                EQUAL_DISPLAYNAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, null, null, null, null);
        assertTrue( dtype.compareTo(another) < 0 );
        assertTrue( another.compareTo(dtype) > 0 );

        another = new StringDashDataType(LESS_VARNAME, SORT_ORDER,
                EQUAL_DISPLAYNAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, null, null, null, null);
        assertTrue( dtype.compareTo(another) > 0 );
        assertTrue( another.compareTo(dtype) < 0 );

        another = new StringDashDataType(EQUAL_VARNAME, SORT_ORDER,
                EQUAL_DISPLAYNAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, null, null, null, null);
        assertTrue( dtype.compareTo(another) < 0 );
        assertTrue( another.compareTo(dtype) > 0 );
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.datatype.DashDataType#duplicate()} and
     * {@link gov.noaa.pmel.dashboard.datatype.CharDashDataType#CharDashDataType(gov.noaa.pmel.dashboard.shared.DataColumnType,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
     */
    @Test
    public void testDuplicateCharDashDataType() {
        CharDashDataType dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, BOUNDS[0].toString(), BOUNDS[1].toString(),
                BOUNDS[2].toString(), BOUNDS[3].toString());
        DataColumnType colType = new DataColumnType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS);
        assertEquals(colType, dtype.duplicate());
        CharDashDataType other = new CharDashDataType(colType,
                STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, BOUNDS[0].toString(), BOUNDS[1].toString(),
                BOUNDS[2].toString(), BOUNDS[3].toString());
        assertEquals(dtype, other);
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.datatype.DashDataType#typeNameEquals(java.lang.String)},
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#typeNameEquals(gov.noaa.pmel.dashboard.datatype.DashDataType)}, and
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#typeNameEquals(gov.noaa.pmel.dashboard.shared.DataColumnType)},
     */
    @Test
    public void testTypeNameEquals() {
        CharDashDataType dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, BOUNDS[0].toString(), BOUNDS[1].toString(),
                BOUNDS[2].toString(), BOUNDS[3].toString());
        assertTrue( dtype.typeNameEquals(VAR_NAME) );
        assertTrue( dtype.typeNameEquals(VAR_NAME.toUpperCase()) );
        assertTrue( dtype.typeNameEquals(VAR_NAME.toLowerCase()) );
        assertTrue( dtype.typeNameEquals(VAR_NAME.replaceAll("_", "")) );
        assertTrue( dtype.typeNameEquals(DISPLAY_NAME) );
        assertFalse( dtype.typeNameEquals((String) null) );
        assertFalse( dtype.typeNameEquals(DESCRIPTION) );

        StringDashDataType other = new StringDashDataType(DISPLAY_NAME, SORT_ORDER,
                VAR_NAME, null, false, null, null, null, null, null, null, null, null);
        assertTrue( dtype.typeNameEquals(other) );
        assertTrue( other.typeNameEquals(dtype) );
        assertFalse( dtype.typeNameEquals((DashDataType<?>) null) );

        assertTrue(dtype.typeNameEquals(other.duplicate()) );
        assertFalse( dtype.typeNameEquals((DataColumnType) null) );
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.datatype.DashDataType#isQCType()},
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#isCommentType()}, and
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#isCommentTypeFor(gov.noaa.pmel.dashboard.datatype.DashDataType)}.
     */
    @Test
    public void testIsType() {
        // CharDashDataType can never be a comment
        CharDashDataType dtype = new CharDashDataType("WOCE", 1.0, "WOCE flag",
                "WOCE flag", false, DashboardUtils.NO_UNITS, "WOCE_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue( dtype.isQCType() );

        dtype = new CharDashDataType("WOCE", 1.0, "WOCE flag",
                "WOCE flag", false, DashboardUtils.NO_UNITS, "WOCE_flag",
                DashboardServerUtils.IDENTIFIER_CATEGORY, null, null, null, null, null);
        assertFalse( dtype.isQCType() );

        dtype = new CharDashDataType("WOCE_xCO2", 1.0, "WOCE xCO2",
                "WOCE flag for xCO2", false, DashboardUtils.NO_UNITS, "WOCE_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue( dtype.isQCType() );

        dtype = new CharDashDataType("xCO2_woce", 1.0, "xCO2 WOCE flag",
                "WOCE flag for xCO2", false, DashboardUtils.NO_UNITS, "WOCE_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue( dtype.isQCType() );

        dtype = new CharDashDataType("QC_xCO2", 1.0, "QC xCO2",
                "QC flag for xCO2", false, DashboardUtils.NO_UNITS, "QC_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue( dtype.isQCType() );

        dtype = new CharDashDataType("xCO2_qc", 1.0, "xCO2 QC flag",
                "QC flag for xCO2", false, DashboardUtils.NO_UNITS, "QC_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue( dtype.isQCType() );

        dtype = new CharDashDataType("QC", 1.0, "QC flag",
                "QC flag", false, DashboardUtils.NO_UNITS, "QC_flag",
                DashboardServerUtils.QUALITY_CATEGORY, null, null, null, null, null);
        assertTrue( dtype.isQCType() );

        CharDashDataType other = new CharDashDataType("QC_Comment", 1.0, "QC flag comment",
                "Comment for QC flag", false, DashboardUtils.NO_UNITS, "Comment",
                null, null, null, null, null, null);
        assertFalse( other.isCommentType() );
        assertFalse( other.isCommentTypeFor(dtype) );
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.datatype.DashDataType#toPropertyValue()}, and
     * {@link gov.noaa.pmel.dashboard.datatype.DashDataType#fromPropertyValue(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testToFromPropertyValue() {
        CharDashDataType dtype = new CharDashDataType(VAR_NAME, SORT_ORDER,
                DISPLAY_NAME, DESCRIPTION, true, UNITS, STANDARD_NAME, CATEGORY_NAME,
                FILE_UNIT, BOUNDS[0].toString(), BOUNDS[1].toString(),
                BOUNDS[2].toString(), BOUNDS[3].toString());

        String propValStr = dtype.toPropertyValue();
        assertFalse( propValStr.contains(VAR_NAME) );
        assertTrue( propValStr.contains(Character.class.getSimpleName()) );
        assertTrue( propValStr.contains(SORT_ORDER.toString()) );
        assertTrue( propValStr.contains(DISPLAY_NAME) );
        assertTrue( propValStr.contains(DESCRIPTION) );
        assertTrue( propValStr.contains("true") );
        for ( String value : UNITS )
            assertTrue( propValStr.contains(value) );
        assertTrue( propValStr.contains(STANDARD_NAME) );
        assertTrue( propValStr.contains(FILE_UNIT) );
        assertTrue( propValStr.contains(BOUNDS[0].toString()) );
        assertTrue( propValStr.contains(BOUNDS[1].toString()) );
        assertTrue( propValStr.contains(BOUNDS[2].toString()) );
        assertTrue( propValStr.contains(BOUNDS[3].toString()) );

        DashDataType<?> other = DashDataType.fromPropertyValue(VAR_NAME, propValStr);
        assertEquals(dtype, other);
    }

}
