package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.variable.Pressure;
import gov.noaa.pmel.sdimetadata.variable.Variable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PressureTest {

    private static final String COL_NAME = "SLP";
    private static final String KILOPASCALS_UNIT = "kPa";

    @Test
    public void testGetSetUnit() {
        Pressure pressure = new Pressure();
        assertEquals(Pressure.HECTOPASCALS_UNIT, pressure.getUnit());
        pressure.setUnit(KILOPASCALS_UNIT);
        assertEquals(KILOPASCALS_UNIT, pressure.getUnit());
        pressure.setUnit(null);
        assertEquals(Pressure.HECTOPASCALS_UNIT, pressure.getUnit());
        pressure.setUnit("\t");
        assertEquals("", pressure.getUnit());
    }

    @Test
    public void testGetSetUncertaintyUnit() {
        Pressure pressure = new Pressure();
        assertEquals(Pressure.HECTOPASCALS_UNIT, pressure.getUncertaintyUnit());
        try {
            pressure.setUncertaintyUnit(KILOPASCALS_UNIT);
            fail("call to setUncertaintyUnit succeeded");
        } catch ( UnsupportedOperationException ex ) {
            // Expected result
        }
        try {
            ((Variable) pressure).setUncertaintyUnit(KILOPASCALS_UNIT);
            fail("call to setUncertaintyUnit succeeded");
        } catch ( UnsupportedOperationException ex ) {
            // Expected result
        }
    }

    @Test
    public void testClone() {
        Pressure pressure = new Pressure();
        Pressure dup = pressure.clone();
        assertEquals(pressure, dup);
        assertNotSame(pressure, dup);

        pressure.setColName(COL_NAME);
        assertNotEquals(pressure, dup);

        dup = pressure.clone();
        assertEquals(pressure, dup);
        assertNotSame(pressure, dup);
    }

    @Test
    public void testHashCodeEquals() {
        Pressure first = new Pressure();
        assertFalse(first.equals(null));
        assertFalse(first.equals(Pressure.HECTOPASCALS_UNIT));

        Pressure second = new Pressure();
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
        assertNotEquals(first.hashCode(), var.hashCode());
        assertFalse(first.equals(var));
        assertNotEquals(var.hashCode(), second.hashCode());
        assertFalse(var.equals(second));
        var.setUnit(Pressure.HECTOPASCALS_UNIT);
        var.setUncertaintyUnit(Pressure.HECTOPASCALS_UNIT);
        assertEquals(first.hashCode(), var.hashCode());
        assertFalse(first.equals(var));
        assertEquals(var.hashCode(), second.hashCode());
        assertTrue(var.equals(second));
    }

}
