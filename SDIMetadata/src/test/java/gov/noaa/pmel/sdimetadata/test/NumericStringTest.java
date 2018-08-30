package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.NumericString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NumericStringTest {

    private static final double DELTA = 1.0E-8;
    private static final String POSVAL_STR = "+1.0E-3";
    private static final double POSVAL_NUM = 0.001;
    private static final String ZERVAL_STR = "000";
    private static final double ZERVAL_NUM = 0.0;
    private static final String NEGVAL_STR = "-5.0";
    private static final double NEGVAL_NUM = -5.0;

    @Test
    public void testGetNumericValue() {
        NumericString numstr = new NumericString(null);
        assertTrue(Double.isNaN(numstr.numericValue()));
        numstr = new NumericString("\n");
        assertTrue(Double.isNaN(numstr.numericValue()));
        numstr = new NumericString(POSVAL_STR);
        assertEquals(POSVAL_NUM, numstr.numericValue(), DELTA);
        numstr = new NumericString(ZERVAL_STR);
        assertEquals(ZERVAL_NUM, numstr.numericValue(), DELTA);
        numstr = new NumericString(NEGVAL_STR);
        assertEquals(NEGVAL_NUM, numstr.numericValue(), DELTA);
        try {
            new NumericString("-1.0ABCD");
            fail("initializing with a non-numeric value succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            new NumericString("NaN");
            fail("initializing with NaN succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            new NumericString("Inf");
            fail("initializing with Inf succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            new NumericString("+Inf");
            fail("initializing with +Inf succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            new NumericString("-Inf");
            fail("initializing with -Inf succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testIsMethods() {
        NumericString numstr = new NumericString(null);
        assertFalse(numstr.isValid());
        assertFalse(numstr.isPositive());
        assertFalse(numstr.isNonNegative());
        assertFalse(numstr.isNonPositive());
        assertFalse(numstr.isNegative());

        numstr = new NumericString("\n");
        assertFalse(numstr.isValid());
        assertFalse(numstr.isPositive());
        assertFalse(numstr.isNonNegative());
        assertFalse(numstr.isNonPositive());
        assertFalse(numstr.isNegative());

        numstr = new NumericString(POSVAL_STR);
        assertTrue(numstr.isValid());
        assertTrue(numstr.isPositive());
        assertTrue(numstr.isNonNegative());
        assertFalse(numstr.isNonPositive());
        assertFalse(numstr.isNegative());

        numstr = new NumericString(ZERVAL_STR);
        assertTrue(numstr.isValid());
        assertFalse(numstr.isPositive());
        assertTrue(numstr.isNonNegative());
        assertTrue(numstr.isNonPositive());
        assertFalse(numstr.isNegative());

        numstr = new NumericString(NEGVAL_STR);
        assertTrue(numstr.isValid());
        assertFalse(numstr.isPositive());
        assertFalse(numstr.isNonNegative());
        assertTrue(numstr.isNonPositive());
        assertTrue(numstr.isNegative());
    }

    @Test
    public void testHashCodeEquals() {
        NumericString numstr = new NumericString(null);
        assertFalse(numstr.equals(null));
        assertFalse(numstr.equals(ZERVAL_STR));
        assertEquals("".hashCode(), numstr.hashCode());
        assertTrue(numstr.equals(""));

        NumericString other = new NumericString("\n");
        assertEquals(numstr.hashCode(), other.hashCode());
        assertTrue(numstr.equals(other));
        assertTrue(other.equals(numstr));

        numstr = new NumericString(ZERVAL_STR);
        assertEquals(ZERVAL_STR.hashCode(), numstr.hashCode());
        assertTrue(numstr.equals(ZERVAL_STR));
        assertFalse(numstr.equals(ZERVAL_NUM));

        other = new NumericString(ZERVAL_STR);
        assertEquals(numstr.hashCode(), other.hashCode());
        assertTrue(numstr.equals(other));
        assertTrue(other.equals(numstr));

        assertNotEquals(numstr.hashCode(), "0".hashCode());
        assertFalse(numstr.equals("0"));

        other = new NumericString("0");
        assertNotEquals(numstr.hashCode(), other.hashCode());
        assertFalse(numstr.equals(other));
    }

    @Test
    public void testToString() {
        NumericString numstr = new NumericString(null);
        assertEquals("", numstr.toString());
        numstr = new NumericString("\n");
        assertEquals("", numstr.toString());
        numstr = new NumericString(POSVAL_STR);
        assertEquals(POSVAL_STR, numstr.toString());
        numstr = new NumericString(ZERVAL_STR);
        assertEquals(ZERVAL_STR, numstr.toString());
        numstr = new NumericString(NEGVAL_STR);
        assertEquals(NEGVAL_STR, numstr.toString());
    }
}