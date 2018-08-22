package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.datavar.Temperature;
import gov.noaa.pmel.sdimetadata.datavar.Variable;
import org.junit.Test;

import java.time.temporal.ValueRange;

import static org.junit.Assert.*;

public class TemperatureTest {

    private static final String VAR_NAME = "SST";

    @Test
    public void testGetSetVarUnit() {
        Temperature temperature = new Temperature();
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, temperature.getVarUnit());
        try {
            temperature.setVarUnit("something");
            fail("call to setVarUnit succeeded");
        } catch (UnsupportedOperationException ex) {
            // Expected result
        }
        try {
            ((Variable) temperature).setVarUnit("something");
            fail("call to setVarUnit from cast succeeded");
        } catch (UnsupportedOperationException ex) {
            // Expected result
        }
    }

    @Test
    public void testGetSetPrecisionUnit() {
        Temperature temperature = new Temperature();
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, temperature.getPrecisionUnit());
        try {
            temperature.setPrecisionUnit("something");
            fail("call to setPrecisionUnit succeeded");
        } catch (UnsupportedOperationException ex) {
            // Expected result
        }
        try {
            ((Variable) temperature).setPrecisionUnit("something");
            fail("call to setPrecisionUnit from cast succeeded");
        } catch (UnsupportedOperationException ex) {
            // Expected result
        }
    }

    @Test
    public void testGetSetAccuracyUnit() {
        Temperature temperature = new Temperature();
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, temperature.getAccuracyUnit());
        try {
            temperature.setAccuracyUnit("something");
            fail("call to setAccuracyUnit succeeded");
        } catch (UnsupportedOperationException ex) {
            // Expected result
        }
        try {
            ((Variable) temperature).setAccuracyUnit("something");
            fail("call to setAccuracyUnit succeeded");
        } catch (UnsupportedOperationException ex) {
            // Expected result
        }
    }

    @Test
    public void testClone() {
        Temperature temperature = new Temperature();
        Temperature dup = temperature.clone();
        assertEquals(temperature, dup);
        assertNotSame(temperature, dup);

        temperature.setVarName(VAR_NAME);
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
        var.setVarUnit(Temperature.DEGREES_CELSIUS_UNIT);
        var.setPrecisionUnit(Temperature.DEGREES_CELSIUS_UNIT);
        var.setAccuracyUnit(Temperature.DEGREES_CELSIUS_UNIT);
        assertEquals(first.hashCode(), var.hashCode());
        assertFalse(first.equals(var));
        assertEquals(var.hashCode(), second.hashCode());
        assertTrue(var.equals(second));
    }

}

