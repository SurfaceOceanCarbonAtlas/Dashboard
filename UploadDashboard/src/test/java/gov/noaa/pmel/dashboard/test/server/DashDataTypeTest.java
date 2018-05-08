/**
 *
 */
package gov.noaa.pmel.dashboard.test.server;

import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for {@link gov.noaa.pmel.dashboard.server.DashDataType}
 *
 * @author Karl Smith
 */
public class DashDataTypeTest {

    private static final String VAR_NAME = "xCO2_atm_dry";
    private static final Double SORT_ORDER = 3.14159;
    private static final String DISPLAY_NAME = "xCO2 atm dry";
    private static final String DATA_CLASS_NAME = "Double";
    private static final String DESCRIPTION = "mole fraction of carbon dioxide";
    private static final String STANDARD_NAME = "mole_fraction_co2";
    private static final String CATEGORY_NAME = "CO2";
    private static final ArrayList<String> UNITS = new ArrayList<String>(Arrays.asList("umol/mol", "mmol/mol"));

    /**
     * Test method for
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#DashDataType(String, Double, String, String, String,
     * String, String, java.util.Collection)}
     * as well as
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#getVarName()},
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#getSortOrder()},
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#getDisplayName()},
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#getDataClassName()},
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#getDescription()},
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#getStandardName()},
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#getCategoryName()}, and
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#getUnits()}.
     */
    @Test
    public void testDashDataType() {
        DashDataType stdtype = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                                DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        assertFalse(UNITS == stdtype.getUnits());
        assertEquals(UNITS, stdtype.getUnits());
        assertEquals(CATEGORY_NAME, stdtype.getCategoryName());
        assertEquals(STANDARD_NAME, stdtype.getStandardName());
        assertEquals(DESCRIPTION, stdtype.getDescription());
        assertEquals(DATA_CLASS_NAME, stdtype.getDataClassName());
        assertEquals(DISPLAY_NAME, stdtype.getDisplayName());
        assertEquals(SORT_ORDER, stdtype.getSortOrder());
        assertEquals(VAR_NAME, stdtype.getVarName());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#typeNameEquals(java.lang.String)}
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#typeNameEquals(gov.noaa.pmel.dashboard.shared.DataColumnType)},
     * and {@link gov.noaa.pmel.dashboard.server.DashDataType#typeNameEquals(gov.noaa.pmel.dashboard.server.DashDataType)}.
     */
    @Test
    public void testTypeNameEquals() {
        DashDataType stdtype = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                                DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);

        assertTrue(stdtype.typeNameEquals(DISPLAY_NAME.toUpperCase()));
        assertTrue(stdtype.typeNameEquals(DISPLAY_NAME.toLowerCase()));
        assertTrue(stdtype.typeNameEquals(VAR_NAME.toUpperCase()));
        assertTrue(stdtype.typeNameEquals(VAR_NAME.toLowerCase()));
        assertFalse(stdtype.typeNameEquals(DATA_CLASS_NAME));
        assertFalse(stdtype.typeNameEquals(DESCRIPTION));
        assertFalse(stdtype.typeNameEquals(STANDARD_NAME));
        assertFalse(stdtype.typeNameEquals(CATEGORY_NAME));

        DashDataType other;
        other = new DashDataType(VAR_NAME.toLowerCase(), SORT_ORDER - 1.0, "Blob",
                                 "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(other));
        assertTrue(other.typeNameEquals(stdtype));

        other = new DashDataType(VAR_NAME.toUpperCase(), SORT_ORDER + 1.0, "Blob",
                                 "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(other));
        assertTrue(other.typeNameEquals(stdtype));

        other = new DashDataType("Blob", SORT_ORDER - 2.0, DISPLAY_NAME.toLowerCase(),
                                 "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(other));
        assertTrue(other.typeNameEquals(stdtype));

        other = new DashDataType("Blob", SORT_ORDER + 2.0, DISPLAY_NAME.toUpperCase(),
                                 "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(other));
        assertTrue(other.typeNameEquals(stdtype));

        other = new DashDataType(DISPLAY_NAME.toLowerCase(), SORT_ORDER - 3.0, "Blob",
                                 "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(other));
        assertTrue(other.typeNameEquals(stdtype));

        other = new DashDataType(DISPLAY_NAME.toUpperCase(), SORT_ORDER + 3.0, "Blob",
                                 "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(other));
        assertTrue(other.typeNameEquals(stdtype));

        other = new DashDataType("Blob", SORT_ORDER - 4.0, VAR_NAME.toLowerCase(),
                                 "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(other));
        assertTrue(other.typeNameEquals(stdtype));

        other = new DashDataType("Blob", SORT_ORDER + 4.0, VAR_NAME.toUpperCase(),
                                 "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(other));
        assertTrue(other.typeNameEquals(stdtype));

        other = new DashDataType("Blob", SORT_ORDER, "Blob", DATA_CLASS_NAME,
                                 DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        assertFalse(stdtype.typeNameEquals(other));
        assertFalse(other.typeNameEquals(stdtype));

        DataColumnType another;
        another = new DataColumnType("Blob", SORT_ORDER - 5.0, DISPLAY_NAME.toLowerCase(),
                                     "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(another));

        another = new DataColumnType(VAR_NAME.toUpperCase(), SORT_ORDER + 5.0, "Blob",
                                     "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(another));

        another = new DataColumnType(DISPLAY_NAME.toUpperCase(), SORT_ORDER - 6.0, "Blob",
                                     "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(another));

        another = new DataColumnType("Blob", SORT_ORDER + 6.0, VAR_NAME.toLowerCase(),
                                     "Integer", null, null, null, DashboardUtils.NO_UNITS);
        assertTrue(stdtype.typeNameEquals(another));

        another = new DataColumnType("Blob", SORT_ORDER, "Blob", DATA_CLASS_NAME,
                                     DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        assertFalse(stdtype.typeNameEquals(another));
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#duplicate()}.
     */
    @Test
    public void testDuplicate() {
        DashDataType stdtype = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                                DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        DataColumnType dup = stdtype.duplicate();
        DataColumnType other = new DataColumnType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                                  DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        assertEquals(other, dup);
        assertFalse(dup.getUnits() == stdtype.getUnits());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#isWoceType()},
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#isWoceCommentFor(gov.noaa.pmel.dashboard.server.DashDataType)}, and
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#isWoceCommentFor(gov.noaa.pmel.dashboard.shared.DataColumnType)}.
     */
    @Test
    public void testWoceTypeWoceComment() {
        DashDataType dtype1 = new DashDataType("WOCE_flag", 100.0, "WOCE flag",
                                               DashboardUtils.CHAR_DATA_CLASS_NAME, null, null,
                                               DashboardUtils.QUALITY_CATEGORY, null);
        assertTrue(dtype1.isWoceType());
        assertFalse(dtype1.isWoceCommentFor((DashDataType) null));
        assertFalse(dtype1.isWoceCommentFor((DataColumnType) null));

        DashDataType dtype2 = new DashDataType("wOCe_of_nothing", 100.0, "Unused WOCE flag",
                                               DashboardUtils.CHAR_DATA_CLASS_NAME, null, null,
                                               DashboardUtils.QUALITY_CATEGORY, null);
        assertTrue(dtype2.isWoceType());

        DashDataType ctype = new DashDataType("Comment_WOCE_flag", 101.0, "WOCE flag comment",
                                              DashboardUtils.STRING_DATA_CLASS_NAME, null, null, null, null);
        assertTrue(ctype.isWoceCommentFor(dtype1));
        assertFalse(ctype.isWoceCommentFor(dtype2));
        assertTrue(ctype.isWoceCommentFor(dtype1.duplicate()));
        assertFalse(ctype.isWoceCommentFor(dtype2.duplicate()));
        assertTrue(ctype.isWoceCommentFor((DashDataType) null));
        assertTrue(ctype.isWoceCommentFor((DataColumnType) null));

        ctype = new DashDataType("Comment_WOCE_of_Nothing", 101.0, "WOCE flag comment",
                                 DashboardUtils.STRING_DATA_CLASS_NAME, null, null, null, null);
        assertFalse(ctype.isWoceCommentFor(dtype1));
        assertTrue(ctype.isWoceCommentFor(dtype2));
        assertFalse(ctype.isWoceCommentFor(dtype1.duplicate()));
        assertTrue(ctype.isWoceCommentFor(dtype2.duplicate()));
        assertTrue(ctype.isWoceCommentFor((DashDataType) null));
        assertTrue(ctype.isWoceCommentFor((DataColumnType) null));

        ctype = new DashDataType("Comment_WOCE_of_Nothing", 101.0, "WOCE flag comment",
                                 DashboardUtils.INT_DATA_CLASS_NAME, null, null, null, null);
        assertFalse(ctype.isWoceCommentFor(dtype1));
        assertFalse(ctype.isWoceCommentFor(dtype2));
        assertFalse(ctype.isWoceCommentFor(dtype1.duplicate()));
        assertFalse(ctype.isWoceCommentFor(dtype2.duplicate()));
        assertFalse(ctype.isWoceCommentFor((DashDataType) null));
        assertFalse(ctype.isWoceCommentFor((DataColumnType) null));

        dtype2 = new DashDataType("WOCE", 100.0, "WOCE flag",
                                  DashboardUtils.CHAR_DATA_CLASS_NAME, null, null,
                                  DashboardUtils.QUALITY_CATEGORY, null);
        assertFalse(dtype2.isWoceType());

        ctype = new DashDataType("Comment_WOCE", 101.0, "WOCE flag comment",
                                 DashboardUtils.STRING_DATA_CLASS_NAME, null, null, null, null);
        assertFalse(ctype.isWoceCommentFor(dtype1));
        assertFalse(ctype.isWoceCommentFor(dtype1.duplicate()));
        boolean errCaught = false;
        try {
            ctype.isWoceCommentFor(dtype2);
        } catch (IllegalArgumentException ex) {
            errCaught = true;
        }
        if ( !errCaught )
            fail("Did not detect type passed to isWoceCommentFor was not a WOCE type");
        errCaught = false;
        try {
            ctype.isWoceCommentFor(dtype2.duplicate());
        } catch (IllegalArgumentException ex) {
            errCaught = true;
        }
        if ( !errCaught )
            fail("Did not detect type passed to isWoceCommentFor was not a WOCE type");

        dtype2 = new DashDataType("tWOCE_flag", 100.0, "WOCE flag invalid name",
                                  DashboardUtils.CHAR_DATA_CLASS_NAME, null, null,
                                  DashboardUtils.QUALITY_CATEGORY, null);
        assertFalse(dtype2.isWoceType());

        dtype2 = new DashDataType("WOCE_flag", 100.0, "WOCE flag invalid class name",
                                  DashboardUtils.INT_DATA_CLASS_NAME, null, null,
                                  DashboardUtils.QUALITY_CATEGORY, null);
        assertFalse(dtype2.isWoceType());

        dtype2 = new DashDataType("WOCE_flag", 100.0, "WOCE flag invalid category",
                                  DashboardUtils.CHAR_DATA_CLASS_NAME, null, null,
                                  DashboardUtils.IDENTIFIER_CATEGORY, null);
        assertFalse(dtype2.isWoceType());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#toPropertyValue()}
     * and {@link gov.noaa.pmel.dashboard.server.DashDataType#fromPropertyValue(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testToFromPropertyValue() {
        DashDataType stdtype = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                                DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        String jsonDesc = stdtype.toPropertyValue();
        assertFalse(jsonDesc.contains(VAR_NAME));
        assertTrue(jsonDesc.contains("sort_order"));
        assertTrue(jsonDesc.contains(SORT_ORDER.toString()));
        assertTrue(jsonDesc.contains("display_name"));
        assertTrue(jsonDesc.contains(DISPLAY_NAME));
        assertTrue(jsonDesc.contains("data_class"));
        assertTrue(jsonDesc.contains(DATA_CLASS_NAME));
        assertTrue(jsonDesc.contains("description"));
        assertTrue(jsonDesc.contains(DESCRIPTION));
        assertTrue(jsonDesc.contains("standard_name"));
        assertTrue(jsonDesc.contains(STANDARD_NAME));
        assertTrue(jsonDesc.contains("category_name"));
        assertTrue(jsonDesc.contains(CATEGORY_NAME));
        assertTrue(jsonDesc.contains("units"));
        for (String val : UNITS) {
            assertTrue(jsonDesc.contains(val));
        }

        DashDataType other = DashDataType.fromPropertyValue(VAR_NAME, jsonDesc);
        assertEquals(stdtype, other);

        stdtype = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                   DATA_CLASS_NAME, null, null, null, null);
        jsonDesc = stdtype.toPropertyValue();
        assertFalse(jsonDesc.contains(VAR_NAME));
        assertTrue(jsonDesc.contains("sort_order"));
        assertTrue(jsonDesc.contains(SORT_ORDER.toString()));
        assertTrue(jsonDesc.contains("display_name"));
        assertTrue(jsonDesc.contains(DISPLAY_NAME));
        assertTrue(jsonDesc.contains("data_class"));
        assertTrue(jsonDesc.contains(DATA_CLASS_NAME));
        assertFalse(jsonDesc.contains("description"));
        assertFalse(jsonDesc.contains("standard_name"));
        assertFalse(jsonDesc.contains("category_name"));
        assertFalse(jsonDesc.contains("units"));

        other = DashDataType.fromPropertyValue(VAR_NAME, jsonDesc);
        assertEquals(stdtype, other);
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.server.DashDataType#hashCode()}
     * and {@link gov.noaa.pmel.dashboard.server.DashDataType#equals(java.lang.Object)}
     * as well as
     * {@link gov.noaa.pmel.dashboard.server.DashDataType#DashDataType(DataColumnType)}
     */
    @Test
    public void testHashCodeEquals() {
        DashDataType stdtype = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                                DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        assertFalse(stdtype.equals(null));
        assertFalse(stdtype.equals(VAR_NAME));

        DashDataType other = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                              DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        assertTrue(stdtype.hashCode() == other.hashCode());
        assertTrue(stdtype.equals(other));

        other = new DashDataType("blob", SORT_ORDER, DISPLAY_NAME,
                                 DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        assertFalse(stdtype.hashCode() == other.hashCode());
        assertFalse(stdtype.equals(other));

        other = new DashDataType(VAR_NAME, SORT_ORDER + 1.0, DISPLAY_NAME,
                                 DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        // hash code ignores floating point values
        assertTrue(stdtype.hashCode() == other.hashCode());
        assertFalse(stdtype.equals(other));

        other = new DashDataType(VAR_NAME, SORT_ORDER, "Blob",
                                 DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        assertFalse(stdtype.hashCode() == other.hashCode());
        assertFalse(stdtype.equals(other));

        other = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                 "Integer", DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        assertFalse(stdtype.hashCode() == other.hashCode());
        assertFalse(stdtype.equals(other));

        other = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                 DATA_CLASS_NAME, null, STANDARD_NAME, CATEGORY_NAME, UNITS);
        assertFalse(stdtype.hashCode() == other.hashCode());
        assertFalse(stdtype.equals(other));

        other = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                 DATA_CLASS_NAME, DESCRIPTION, null, CATEGORY_NAME, UNITS);
        assertFalse(stdtype.hashCode() == other.hashCode());
        assertFalse(stdtype.equals(other));

        other = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                 DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, null, UNITS);
        assertFalse(stdtype.hashCode() == other.hashCode());
        assertFalse(stdtype.equals(other));

        other = new DashDataType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                 DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, DashboardUtils.NO_UNITS);
        assertFalse(stdtype.hashCode() == other.hashCode());
        assertFalse(stdtype.equals(other));

        DataColumnType another = new DataColumnType(VAR_NAME, SORT_ORDER, DISPLAY_NAME,
                                                    DATA_CLASS_NAME, DESCRIPTION, STANDARD_NAME, CATEGORY_NAME, UNITS);
        assertFalse(stdtype.equals(another));

        other = new DashDataType(another);
        assertTrue(stdtype.hashCode() == other.hashCode());
        assertEquals(stdtype, other);
    }


}
