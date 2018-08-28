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

    private static final String VAR_NAME = "SLP";
    private static final String KILOPASCALS_UNIT = "kPa";

    @Test
    public void testGetSetVarUnit() {
        Pressure pressure = new Pressure();
        assertEquals(Pressure.HECTOPASCALS_UNIT, pressure.getVarUnit());
        pressure.setVarUnit(KILOPASCALS_UNIT);
        assertEquals(KILOPASCALS_UNIT, pressure.getVarUnit());
        pressure.setVarUnit(null);
        assertEquals(Pressure.HECTOPASCALS_UNIT, pressure.getVarUnit());
        pressure.setVarUnit("\t");
        assertEquals("", pressure.getVarUnit());
    }

    @Test
    public void testGetSetPrecisionUnit() {
        Pressure pressure = new Pressure();
        assertEquals(Pressure.HECTOPASCALS_UNIT, pressure.getPrecisionUnit());
        try {
            pressure.setPrecisionUnit(KILOPASCALS_UNIT);
            fail("call to setPrecisionUnit succeeded");
        } catch ( UnsupportedOperationException ex ) {
            // Expected result
        }
        try {
            ((Variable) pressure).setPrecisionUnit(KILOPASCALS_UNIT);
            fail("call to setPrecisionUnit from cast succeeded");
        } catch ( UnsupportedOperationException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetAccuracyUnit() {
        Pressure pressure = new Pressure();
        assertEquals(Pressure.HECTOPASCALS_UNIT, pressure.getAccuracyUnit());
        try {
            pressure.setAccuracyUnit(KILOPASCALS_UNIT);
            fail("call to setAccuracyUnit succeeded");
        } catch ( UnsupportedOperationException ex ) {
            // Expected result
        }
        try {
            ((Variable) pressure).setAccuracyUnit(KILOPASCALS_UNIT);
            fail("call to setAccuracyUnit succeeded");
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

        pressure.setVarName(VAR_NAME);
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

        first.setVarName(VAR_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setVarName(VAR_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        Variable var = new Variable();
        var.setVarName(VAR_NAME);
        assertNotEquals(first.hashCode(), var.hashCode());
        assertFalse(first.equals(var));
        assertNotEquals(var.hashCode(), second.hashCode());
        assertFalse(var.equals(second));
        var.setVarUnit(Pressure.HECTOPASCALS_UNIT);
        var.setPrecisionUnit(Pressure.HECTOPASCALS_UNIT);
        var.setAccuracyUnit(Pressure.HECTOPASCALS_UNIT);
        assertEquals(first.hashCode(), var.hashCode());
        assertFalse(first.equals(var));
        assertEquals(var.hashCode(), second.hashCode());
        assertTrue(var.equals(second));
    }

}
