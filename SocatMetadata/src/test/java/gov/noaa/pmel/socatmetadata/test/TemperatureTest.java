package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.util.NumericString;
import gov.noaa.pmel.socatmetadata.shared.variable.Temperature;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TemperatureTest {

    private static final NumericString EMPTY_TEMPERATURE = new NumericString(null, Temperature.DEGREES_CELSIUS_UNIT);
    private static final String COL_NAME = "SST";
    private static final String KELVIN_UNIT = "K";
    private static final NumericString ACCURACY = new NumericString("0.01", Temperature.DEGREES_CELSIUS_UNIT);
    private static final NumericString PRECISION = new NumericString("0.001", Temperature.DEGREES_CELSIUS_UNIT);

    @Test
    public void testGetSetVarUnit() {
        Temperature pressure = new Temperature();
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, pressure.getVarUnit());
        pressure.setVarUnit(KELVIN_UNIT);
        assertEquals(KELVIN_UNIT, pressure.getVarUnit());
        pressure.setVarUnit(null);
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, pressure.getVarUnit());
        pressure.setVarUnit("\t");
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, pressure.getVarUnit());
    }

    @Test
    public void testGetSetAccuracy() {
        Temperature pressure = new Temperature();
        assertEquals(EMPTY_TEMPERATURE, pressure.getAccuracy());
        pressure.setAccuracy(ACCURACY);
        assertEquals(ACCURACY, pressure.getAccuracy());
        assertNotSame(ACCURACY, pressure.getAccuracy());
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, pressure.getVarUnit());
        pressure.setAccuracy(null);
        assertEquals(EMPTY_TEMPERATURE, pressure.getAccuracy());
        try {
            pressure.setAccuracy(new NumericString("0.0", Temperature.DEGREES_CELSIUS_UNIT));
            fail("call to setAccuracy with zero deg C succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        /*
        try {
            pressure.setAccuracy(new NumericString("0.01", KELVIN_UNIT));
            fail("call to setAccuracy with Kelvin unit succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            ((DataVar) pressure).setAccuracy(new NumericString("0.01", KELVIN_UNIT));
            fail("call to casted setAccuracy with Kelvin unit succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        */
    }

    @Test
    public void testGetSetPresision() {
        Temperature pressure = new Temperature();
        assertEquals(EMPTY_TEMPERATURE, pressure.getPrecision());
        pressure.setPrecision(PRECISION);
        assertEquals(PRECISION, pressure.getPrecision());
        assertNotSame(PRECISION, pressure.getPrecision());
        assertEquals(EMPTY_TEMPERATURE, pressure.getAccuracy());
        assertEquals(Temperature.DEGREES_CELSIUS_UNIT, pressure.getVarUnit());
        pressure.setPrecision(null);
        assertEquals(EMPTY_TEMPERATURE, pressure.getPrecision());
        try {
            pressure.setPrecision(new NumericString("0.0", Temperature.DEGREES_CELSIUS_UNIT));
            fail("call to setPrecision with zero deg C succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        /*
        try {
            pressure.setPrecision(new NumericString("0.01", KELVIN_UNIT));
            fail("call to setPrecision with Kelvin unit succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            ((DataVar) pressure).setPrecision(new NumericString("0.01", KELVIN_UNIT));
            fail("call to casted setPrecision with Kelvin unit succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        */
    }

    @Test
    public void testClone() {
        Temperature pressure = new Temperature();
        Temperature dup = pressure.clone();
        assertEquals(pressure, dup);
        assertNotSame(pressure, dup);

        pressure.setColName(COL_NAME);
        pressure.setVarUnit(KELVIN_UNIT);
        pressure.setAccuracy(ACCURACY);
        pressure.setPrecision(PRECISION);
        assertNotEquals(pressure, dup);

        dup = pressure.clone();
        assertEquals(pressure, dup);
        assertNotSame(pressure, dup);
        assertNotSame(pressure.getAccuracy(), dup.getAccuracy());
        assertNotSame(pressure.getPrecision(), dup.getPrecision());
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

        first.setVarUnit(KELVIN_UNIT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setVarUnit(KELVIN_UNIT);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAccuracy(ACCURACY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAccuracy(ACCURACY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPrecision(PRECISION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPrecision(PRECISION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}

