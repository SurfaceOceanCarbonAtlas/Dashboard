package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.instrument.CalibrationGas;
import org.junit.Test;
import sun.plugin.dom.exception.InvalidStateException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CalibrationGasTest {

    private static final String EMPTY_STR = "";
    private static final double DELTA = 1.0E-6;
    private static final String GAS_ID = "LL835339";
    private static final String GAS_TYPE = "CO2";
    private static final String SUPPLIER = "NOAA/ESRL, Global Monitoring Division";
    private static final double CONCENTRATION = 245.43;
    private static final double ACCURACY = 0.01;

    @Test
    public void testGetSetId() {
        CalibrationGas gas = new CalibrationGas();
        assertEquals(EMPTY_STR, gas.getId());
        gas.setId(GAS_ID);
        assertEquals(GAS_ID, gas.getId());
        gas.setId(null);
        assertEquals(EMPTY_STR, gas.getId());
        gas.setId("\t");
        assertEquals(EMPTY_STR, gas.getId());
    }

    @Test
    public void testGetSetType() {
        CalibrationGas gas = new CalibrationGas();
        assertEquals(EMPTY_STR, gas.getType());
        gas.setType(GAS_TYPE);
        assertEquals(GAS_TYPE, gas.getType());
        assertEquals(EMPTY_STR, gas.getId());
        gas.setType(null);
        assertEquals(EMPTY_STR, gas.getType());
        gas.setType("\t");
        assertEquals(EMPTY_STR, gas.getType());
    }

    @Test
    public void testGetSetSupplier() {
        CalibrationGas gas = new CalibrationGas();
        assertEquals(EMPTY_STR, gas.getSupplier());
        gas.setSupplier(SUPPLIER);
        assertEquals(SUPPLIER, gas.getSupplier());
        assertEquals(EMPTY_STR, gas.getType());
        assertEquals(EMPTY_STR, gas.getId());
        gas.setSupplier(null);
        assertEquals(EMPTY_STR, gas.getSupplier());
        gas.setSupplier("\t");
        assertEquals(EMPTY_STR, gas.getSupplier());
    }

    @Test
    public void testGetSetConcUMolPerMol() {
        CalibrationGas gas = new CalibrationGas();
        assertTrue(gas.getConcUMolPerMol().isNaN());
        gas.setConcUMolPerMol(CONCENTRATION);
        assertEquals(CONCENTRATION, gas.getConcUMolPerMol(), DELTA);
        assertEquals(EMPTY_STR, gas.getSupplier());
        assertEquals(EMPTY_STR, gas.getType());
        assertEquals(EMPTY_STR, gas.getId());
        gas.setConcUMolPerMol(null);
        assertTrue(gas.getConcUMolPerMol().isNaN());
        gas.setConcUMolPerMol(Double.NaN);
        assertTrue(gas.getConcUMolPerMol().isNaN());
        gas.setConcUMolPerMol(0.0);
        assertEquals(0.0, gas.getConcUMolPerMol(), 0.0);
        try {
            gas.setConcUMolPerMol(-1.0);
            fail("calling setConcUMolPerMol with a negative value succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            gas.setConcUMolPerMol(Double.NEGATIVE_INFINITY);
            fail("calling setConcUMolPerMol with negative infinity succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            gas.setConcUMolPerMol(Double.POSITIVE_INFINITY);
            fail("calling setConcUMolPerMol with positive infinity succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetAccuracyUMolPerMol() {
        CalibrationGas gas = new CalibrationGas();
        assertTrue(gas.getAccuracyUMolPerMol().isNaN());
        gas.setAccuracyUMolPerMol(ACCURACY);
        assertEquals(ACCURACY, gas.getAccuracyUMolPerMol(), DELTA);
        assertTrue(gas.getConcUMolPerMol().isNaN());
        assertEquals(EMPTY_STR, gas.getSupplier());
        assertEquals(EMPTY_STR, gas.getType());
        assertEquals(EMPTY_STR, gas.getId());
        gas.setAccuracyUMolPerMol(null);
        assertTrue(gas.getAccuracyUMolPerMol().isNaN());
        gas.setAccuracyUMolPerMol(Double.NaN);
        assertTrue(gas.getAccuracyUMolPerMol().isNaN());
        try {
            gas.setAccuracyUMolPerMol(0.0);
            fail("calling setAccuracyUMolPerMol with zero succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            gas.setAccuracyUMolPerMol(-1.0);
            fail("calling setAccuracyUMolPerMol with a negative value succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            gas.setAccuracyUMolPerMol(Double.NEGATIVE_INFINITY);
            fail("calling setAccuracyUMolPerMol with negative infinity succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            gas.setAccuracyUMolPerMol(Double.POSITIVE_INFINITY);
            fail("calling setAccuracyUMolPerMol with positive infinity succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testCalibrationGas() {
        CalibrationGas gas = new CalibrationGas(null, null, null, null, null);
        assertEquals(new CalibrationGas(), gas);
        gas = new CalibrationGas(GAS_ID, GAS_TYPE, SUPPLIER, CONCENTRATION, ACCURACY);
        assertEquals(GAS_ID, gas.getId());
        assertEquals(GAS_TYPE, gas.getType());
        assertEquals(SUPPLIER, gas.getSupplier());
        assertEquals(CONCENTRATION, gas.getConcUMolPerMol(), DELTA);
        assertEquals(ACCURACY, gas.getAccuracyUMolPerMol(), DELTA);
    }

    @Test
    public void testIsNonZero() {
        CalibrationGas gas = new CalibrationGas();
        try {
            gas.isNonZero();
            fail("calling isNonZero on a unassigned CalibrationGas succeeded");
        } catch ( InvalidStateException ex ) {
            // Expected result
        }
        gas.setConcUMolPerMol(CONCENTRATION);
        try {
            gas.isNonZero();
            fail("calling isNonZero in a CalibrationGas with only concentration assigned succeeded");
        } catch ( InvalidStateException ex ) {
            // Expected result
        }
        gas.setAccuracyUMolPerMol(ACCURACY);
        assertTrue(gas.isNonZero());
        gas.setConcUMolPerMol(ACCURACY / 10.0);
        assertFalse(gas.isNonZero());
    }

    @Test
    public void testIsValid() {
        CalibrationGas gas = new CalibrationGas();
        assertFalse(gas.isValid());
        gas = new CalibrationGas(GAS_ID, GAS_TYPE, SUPPLIER, CONCENTRATION, ACCURACY);
        assertTrue(gas.isValid());
    }

    @Test
    public void testClone() {
        CalibrationGas gas = new CalibrationGas();
        CalibrationGas dup = gas.clone();
        assertEquals(gas, dup);
        assertNotSame(gas, dup);

        gas.setId(GAS_ID);
        gas.setType(GAS_TYPE);
        gas.setSupplier(SUPPLIER);
        gas.setConcUMolPerMol(CONCENTRATION);
        gas.setAccuracyUMolPerMol(ACCURACY);
        assertNotEquals(gas, dup);

        dup = gas.clone();
        assertEquals(gas, dup);
        assertNotSame(gas, dup);
    }

    @Test
    public void testHashCodeEquals() {
        CalibrationGas first = new CalibrationGas();
        assertFalse(first.equals(null));
        assertFalse(first.equals(GAS_ID));

        CalibrationGas second = new CalibrationGas();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setId(GAS_ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setId(GAS_ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setType(GAS_TYPE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setType(GAS_TYPE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSupplier(SUPPLIER);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSupplier(SUPPLIER);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setConcUMolPerMol(CONCENTRATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setConcUMolPerMol(CONCENTRATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAccuracyUMolPerMol(ACCURACY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAccuracyUMolPerMol(ACCURACY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}
