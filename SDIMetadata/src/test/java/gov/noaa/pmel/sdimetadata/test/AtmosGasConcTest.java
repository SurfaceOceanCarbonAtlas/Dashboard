package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.variable.AtmosGasConc;
import gov.noaa.pmel.sdimetadata.variable.Variable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class AtmosGasConcTest {

    private static final String COL_NAME = "xCO2_atm";

    @Test
    public void testClone() {
        AtmosGasConc gasConc = new AtmosGasConc();
        AtmosGasConc dup = gasConc.clone();
        assertEquals(gasConc, dup);
        assertNotSame(gasConc, dup);

        gasConc.setColName(COL_NAME);
        assertNotEquals(gasConc, dup);

        dup = gasConc.clone();
        assertEquals(gasConc, dup);
        assertNotSame(gasConc, dup);
    }

    @Test
    public void testHashCodeEquals() {
        AtmosGasConc first = new AtmosGasConc();
        assertFalse(first.equals(null));
        assertFalse(first.equals(COL_NAME));

        AtmosGasConc second = new AtmosGasConc();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setColName(COL_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setColName(COL_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        Variable var = new Variable();
        var.setColName(COL_NAME);
        assertEquals(first.hashCode(), var.hashCode());
        assertFalse(first.equals(var));
        assertEquals(var.hashCode(), second.hashCode());
        assertTrue(var.equals(second));
    }

}

