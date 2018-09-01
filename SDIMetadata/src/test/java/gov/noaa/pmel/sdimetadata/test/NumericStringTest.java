package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.util.NumericString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NumericStringTest {

    private static final String EMPTY_STR = "";
    private static final double DELTA = 1.0E-8;
    private static final String POSVAL_STR = "+1.0E-3";
    private static final double POSVAL_NUM = 0.001;
    private static final String ZERVAL_STR = "000";
    private static final double ZERVAL_NUM = 0.0;
    private static final String NEGVAL_STR = "-5.0";
    private static final double NEGVAL_NUM = -5.0;
    private static final String UNIT_STR = "deg C";

    @Test
    public void testGetSetValueString() {
        NumericString numstr = new NumericString();
        assertEquals(EMPTY_STR, numstr.getValueString());
        numstr.setValueString(POSVAL_STR);
        assertEquals(POSVAL_STR, numstr.getValueString());
        numstr.setValueString("\t" + NEGVAL_STR + "  ");
        assertEquals(NEGVAL_STR, numstr.getValueString());
        numstr.setValueString(null);
        assertEquals(EMPTY_STR, numstr.getValueString());
        numstr.setValueString("\t");
        assertEquals(EMPTY_STR, numstr.getValueString());
        try {
            numstr.setValueString("-1.0ABCD");
            fail("calling setValueString with a non-numeric value succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            numstr.setValueString("NaN");
            fail("calling setValueString with NaN succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            numstr.setValueString("Inf");
            fail("calling setValueString with Inf succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            numstr.setValueString("+Inf");
            fail("calling setValueString with +Inf succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            numstr.setValueString("-Inf");
            fail("calling setValueString with -Inf succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetUnitString() {
        NumericString numstr = new NumericString();
        assertEquals(EMPTY_STR, numstr.getUnitString());
        numstr.setUnitString(UNIT_STR);
        assertEquals(UNIT_STR, numstr.getUnitString());
        assertEquals(EMPTY_STR, numstr.getValueString());
        numstr.setUnitString(null);
        assertEquals(EMPTY_STR, numstr.getUnitString());
        numstr.setUnitString("\t" + UNIT_STR + "  ");
        assertEquals(UNIT_STR, numstr.getUnitString());
        numstr.setUnitString("\t");
        assertEquals(EMPTY_STR, numstr.getUnitString());
    }

    @Test
    public void testNumericString() {
        NumericString numstr = new NumericString(null, null);
        assertEquals(new NumericString(), numstr);
        numstr = new NumericString("\t", "\n");
        assertEquals(new NumericString(), numstr);
        numstr = new NumericString(POSVAL_STR, UNIT_STR);
        assertEquals(POSVAL_STR, numstr.getValueString());
        assertEquals(UNIT_STR, numstr.getUnitString());
        NumericString other = new NumericString(null, null);
        other.setValueString(POSVAL_STR);
        other.setUnitString(UNIT_STR);
        assertEquals(numstr, other);
    }

    @Test
    public void testGetNumericValue() {
        NumericString numstr = new NumericString();
        assertTrue(Double.isNaN(numstr.getNumericValue()));
        numstr.setValueString(POSVAL_STR);
        assertEquals(POSVAL_NUM, numstr.getNumericValue(), DELTA);
        numstr.setValueString(ZERVAL_STR);
        assertEquals(ZERVAL_NUM, numstr.getNumericValue(), DELTA);
        numstr.setValueString(NEGVAL_STR);
        assertEquals(NEGVAL_NUM, numstr.getNumericValue(), DELTA);
        numstr.setValueString("\t" + NEGVAL_STR + "  ");
        assertEquals(NEGVAL_NUM, numstr.getNumericValue(), DELTA);
        numstr = new NumericString(NEGVAL_STR, "E-3");
        assertEquals(NEGVAL_NUM, numstr.getNumericValue(), DELTA);
    }

    @Test
    public void testIsMethods() {
        NumericString numstr = new NumericString();
        assertFalse(numstr.isValid());
        assertFalse(numstr.isPositive());
        assertFalse(numstr.isNonNegative());
        assertFalse(numstr.isNonPositive());
        assertFalse(numstr.isNegative());

        numstr = new NumericString("\n", UNIT_STR);
        assertFalse(numstr.isValid());
        assertFalse(numstr.isPositive());
        assertFalse(numstr.isNonNegative());
        assertFalse(numstr.isNonPositive());
        assertFalse(numstr.isNegative());

        numstr = new NumericString(POSVAL_STR, UNIT_STR);
        assertTrue(numstr.isValid());
        assertTrue(numstr.isPositive());
        assertTrue(numstr.isNonNegative());
        assertFalse(numstr.isNonPositive());
        assertFalse(numstr.isNegative());

        numstr = new NumericString(ZERVAL_STR, UNIT_STR);
        assertTrue(numstr.isValid());
        assertFalse(numstr.isPositive());
        assertTrue(numstr.isNonNegative());
        assertTrue(numstr.isNonPositive());
        assertFalse(numstr.isNegative());

        numstr = new NumericString(NEGVAL_STR, UNIT_STR);
        assertTrue(numstr.isValid());
        assertFalse(numstr.isPositive());
        assertFalse(numstr.isNonNegative());
        assertTrue(numstr.isNonPositive());
        assertTrue(numstr.isNegative());
    }

    @Test
    public void testClone() {
        NumericString numstr = new NumericString();
        NumericString dup = numstr.clone();
        assertEquals(numstr, dup);
        assertNotSame(numstr, dup);

        numstr.setValueString(NEGVAL_STR);
        numstr.setUnitString(UNIT_STR);
        assertNotEquals(numstr, dup);

        dup = numstr.clone();
        assertEquals(numstr, dup);
        assertNotSame(numstr, dup);
        assertTrue(dup.isNegative());
    }

    @Test
    public void testHashCodeEquals() {
        NumericString first = new NumericString();
        assertFalse(first.equals(null));
        assertFalse(first.equals(EMPTY_STR));

        NumericString second = new NumericString();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setValueString(ZERVAL_STR);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setValueString(ZERVAL_STR);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setUnitString(UNIT_STR);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setUnitString(UNIT_STR);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}