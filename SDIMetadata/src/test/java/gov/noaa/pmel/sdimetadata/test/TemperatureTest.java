package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.variable.Temperature;
import gov.noaa.pmel.sdimetadata.variable.Variable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TemperatureTest {

    private static final String COL_NAME = "SST";
    private static final String KELVIN_UNIT = "K";

    @Test
    public void testGetSetVarUnit() {
        Temperature temperature = new Temperature();
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, temperature.getVarUnit());
        temperature.setVarUnit(KELVIN_UNIT);
        assertEquals(KELVIN_UNIT, temperature.getVarUnit());
        temperature.setVarUnit(null);
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, temperature.getVarUnit());
        temperature.setVarUnit("\t");
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, temperature.getVarUnit());
    }

    @Test
    public void testGetSetAPUnit() {
        Temperature temperature = new Temperature();
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, temperature.getApUnit());
        try {
            temperature.setApUnit(KELVIN_UNIT);
            fail("call to setApUnit succeeded");
        } catch ( UnsupportedOperationException ex ) {
            // Expected result
        }
        try {
            ((Variable) temperature).setApUnit(KELVIN_UNIT);
            fail("call to setApUnit succeeded");
        } catch ( UnsupportedOperationException ex ) {
            // Expected result
        }
    }

    @Test
    public void testClone() {
        Temperature temperature = new Temperature();
        Temperature dup = temperature.clone();
        assertEquals(temperature, dup);
        assertNotSame(temperature, dup);

        temperature.setColName(COL_NAME);
        assertNotEquals(temperature, dup);

        dup = temperature.clone();
        assertEquals(temperature, dup);
        assertNotSame(temperature, dup);
    }

    @Test
    public void testHashCodeEquals() {
        Temperature first = new Temperature();
        assertFalse(first.equals(null));
        assertFalse(first.equals(Temperature.DEGREES_CELSIUS_UNIT));

        Temperature second = new Temperature();
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

        var.setVarUnit(Temperature.DEGREES_CELSIUS_UNIT);
        var.setApUnit(Temperature.DEGREES_CELSIUS_UNIT);
        assertEquals(first.hashCode(), var.hashCode());
        assertFalse(first.equals(var));
        assertEquals(var.hashCode(), second.hashCode());
        assertTrue(var.equals(second));
    }

}

