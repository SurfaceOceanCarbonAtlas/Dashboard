package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import gov.noaa.pmel.socatmetadata.shared.variable.AirPressure;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AirPressureTest {

    private static final String EMPTY_STR = "";
    private static final NumericString EMPTY_PRESSURE = new NumericString(null, AirPressure.HECTOPASCALS_UNIT);
    private static final String COL_NAME = "SLP";
    private static final String KILOPASCALS_UNIT = "kPa";
    private static final String PRESSURE_CORRECTION = "normalized to sea level pressure";
    private static final NumericString ACCURACY = new NumericString("0.01", AirPressure.HECTOPASCALS_UNIT);
    private static final NumericString PRECISION = new NumericString("0.001", AirPressure.HECTOPASCALS_UNIT);

    @Test
    public void testGetSetPressureCorrection() {
        AirPressure pressure = new AirPressure();
        assertEquals(EMPTY_STR, pressure.getPressureCorrection());
        pressure.setPressureCorrection(PRESSURE_CORRECTION);
        assertEquals(PRESSURE_CORRECTION, pressure.getPressureCorrection());
        pressure.setPressureCorrection(null);
        assertEquals(EMPTY_STR, pressure.getPressureCorrection());
        pressure.setPressureCorrection("\t");
        assertEquals(EMPTY_STR, pressure.getPressureCorrection());
    }

    @Test
    public void testGetSetVarUnit() {
        AirPressure pressure = new AirPressure();
        assertEquals(AirPressure.HECTOPASCALS_UNIT, pressure.getVarUnit());
        pressure.setVarUnit(KILOPASCALS_UNIT);
        assertEquals(KILOPASCALS_UNIT, pressure.getVarUnit());
        assertEquals(EMPTY_STR, pressure.getPressureCorrection());
        pressure.setVarUnit(null);
        assertEquals(AirPressure.HECTOPASCALS_UNIT, pressure.getVarUnit());
        pressure.setVarUnit("\t");
        assertEquals(AirPressure.HECTOPASCALS_UNIT, pressure.getVarUnit());
    }

    @Test
    public void testGetSetAccuracy() {
        AirPressure pressure = new AirPressure();
        assertEquals(EMPTY_PRESSURE, pressure.getAccuracy());
        pressure.setAccuracy(ACCURACY);
        assertEquals(ACCURACY, pressure.getAccuracy());
        assertNotSame(ACCURACY, pressure.getAccuracy());
        assertEquals(AirPressure.HECTOPASCALS_UNIT, pressure.getVarUnit());
        assertEquals(EMPTY_STR, pressure.getPressureCorrection());
        pressure.setAccuracy(null);
        assertEquals(EMPTY_PRESSURE, pressure.getAccuracy());
        try {
            pressure.setAccuracy(new NumericString("0.0", AirPressure.HECTOPASCALS_UNIT));
            fail("call to setAccuracy with zero hPa succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        /*
        try {
            pressure.setAccuracy(new NumericString("0.01", KILOPASCALS_UNIT));
            fail("call to setAccuracy with kPa unit succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            ((DataVar) pressure).setAccuracy(new NumericString("0.01", KILOPASCALS_UNIT));
            fail("call to casted setAccuracy with kPa unit succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        */
    }

    @Test
    public void testGetSetPresision() {
        AirPressure pressure = new AirPressure();
        assertEquals(EMPTY_PRESSURE, pressure.getPrecision());
        pressure.setPrecision(PRECISION);
        assertEquals(PRECISION, pressure.getPrecision());
        assertNotSame(PRECISION, pressure.getPrecision());
        assertEquals(EMPTY_PRESSURE, pressure.getAccuracy());
        assertEquals(AirPressure.HECTOPASCALS_UNIT, pressure.getVarUnit());
        assertEquals(EMPTY_STR, pressure.getPressureCorrection());
        pressure.setPrecision(null);
        assertEquals(EMPTY_PRESSURE, pressure.getPrecision());
        try {
            pressure.setPrecision(new NumericString("0.0", AirPressure.HECTOPASCALS_UNIT));
            fail("call to setPrecision with zero hPa succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        /*
        try {
            pressure.setPrecision(new NumericString("0.01", KILOPASCALS_UNIT));
            fail("call to setPrecision with kPa unit succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            ((DataVar) pressure).setPrecision(new NumericString("0.01", KILOPASCALS_UNIT));
            fail("call to casted setPrecision with kPa unit succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        */
    }

    @Test
    public void testDuplicate() {
        AirPressure pressure = new AirPressure();
        AirPressure dup = pressure.duplicate(null);
        assertEquals(pressure, dup);
        assertNotSame(pressure, dup);

        pressure.setPressureCorrection(PRESSURE_CORRECTION);
        pressure.setColName(COL_NAME);
        pressure.setVarUnit(KILOPASCALS_UNIT);
        pressure.setAccuracy(ACCURACY);
        pressure.setPrecision(PRECISION);
        assertNotEquals(pressure, dup);

        dup = pressure.duplicate(null);
        assertEquals(pressure, dup);
        assertNotSame(pressure, dup);
        assertNotSame(pressure.getAccuracy(), dup.getAccuracy());
        assertNotSame(pressure.getPrecision(), dup.getPrecision());
    }

    @Test
    public void testHashCodeEquals() {
        AirPressure first = new AirPressure();
        assertFalse(first.equals(null));
        assertFalse(first.equals(AirPressure.HECTOPASCALS_UNIT));

        AirPressure second = new AirPressure();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setColName(COL_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setColName(COL_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPressureCorrection(PRESSURE_CORRECTION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPressureCorrection(PRESSURE_CORRECTION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setVarUnit(KILOPASCALS_UNIT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setVarUnit(KILOPASCALS_UNIT);
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
