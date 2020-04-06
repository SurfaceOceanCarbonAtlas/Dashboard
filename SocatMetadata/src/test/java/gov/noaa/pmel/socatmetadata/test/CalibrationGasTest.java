package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.instrument.CalibrationGas;
import gov.noaa.pmel.socatmetadata.util.NumericString;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CalibrationGasTest {

    private static final String EMPTY_STR = "";
    private static final NumericString EMPTY_CONC =
            new NumericString(null, CalibrationGas.GAS_CONCENTRATION_UNIT);
    private static final String GAS_ID = "LL835339";
    private static final String GAS_TYPE = "CO2";
    private static final String SUPPLIER = "NOAA/ESRL, Global Monitoring Division";
    private static final String FREQUENCY = "~ every 19 h";
    private static final NumericString CONCENTRATION =
            new NumericString("245.43", CalibrationGas.GAS_CONCENTRATION_UNIT);
    private static final NumericString ACCURACY =
            new NumericString("0.01", CalibrationGas.GAS_CONCENTRATION_UNIT);
    private static final NumericString ZERO_CONC =
            new NumericString("0.005", CalibrationGas.GAS_CONCENTRATION_UNIT);

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
    public void testGetSetFrequency() {
        CalibrationGas gas = new CalibrationGas();
        assertEquals(EMPTY_STR, gas.getUseFrequency());
        gas.setUseFrequency(FREQUENCY);
        assertEquals(FREQUENCY, gas.getUseFrequency());
        assertEquals(EMPTY_STR, gas.getSupplier());
        assertEquals(EMPTY_STR, gas.getType());
        assertEquals(EMPTY_STR, gas.getId());
        gas.setUseFrequency(null);
        assertEquals(EMPTY_STR, gas.getUseFrequency());
        gas.setUseFrequency("\t");
        assertEquals(EMPTY_STR, gas.getUseFrequency());
    }

    @Test
    public void testGetSetConcentration() {
        CalibrationGas gas = new CalibrationGas();
        assertEquals(EMPTY_CONC, gas.getConcentration());
        gas.setConcentration(CONCENTRATION);
        assertEquals(CONCENTRATION, gas.getConcentration());
        assertEquals(EMPTY_STR, gas.getUseFrequency());
        assertEquals(EMPTY_STR, gas.getSupplier());
        assertEquals(EMPTY_STR, gas.getType());
        assertEquals(EMPTY_STR, gas.getId());
        gas.setConcentration(null);
        assertEquals(EMPTY_CONC, gas.getConcentration());
        try {
            gas.setConcentration(new NumericString("-1.0", CalibrationGas.GAS_CONCENTRATION_UNIT));
            fail("calling setConcentration with a negative value succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetAccuracy() {
        CalibrationGas gas = new CalibrationGas();
        assertEquals(EMPTY_CONC, gas.getAccuracy());
        gas.setAccuracy(ACCURACY);
        assertEquals(ACCURACY, gas.getAccuracy());
        assertEquals(EMPTY_CONC, gas.getConcentration());
        assertEquals(EMPTY_STR, gas.getUseFrequency());
        assertEquals(EMPTY_STR, gas.getSupplier());
        assertEquals(EMPTY_STR, gas.getType());
        assertEquals(EMPTY_STR, gas.getId());
        gas.setAccuracy(null);
        assertEquals(EMPTY_CONC, gas.getAccuracy());
        try {
            gas.setAccuracy(new NumericString("0.0", CalibrationGas.GAS_CONCENTRATION_UNIT));
            fail("calling setAccuracy with zero succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            gas.setAccuracy(new NumericString("-1.0", CalibrationGas.GAS_CONCENTRATION_UNIT));
            fail("calling setAccuracy with a negative value succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testCalibrationGas() {
        CalibrationGas gas = new CalibrationGas(null, null, null, null, null, null);
        assertEquals(new CalibrationGas(), gas);
        gas = new CalibrationGas(GAS_ID, GAS_TYPE, SUPPLIER, CONCENTRATION.getValueString(), ACCURACY.getValueString(),
                FREQUENCY);
        assertEquals(GAS_ID, gas.getId());
        assertEquals(GAS_TYPE, gas.getType());
        assertEquals(SUPPLIER, gas.getSupplier());
        assertEquals(CONCENTRATION, gas.getConcentration());
        assertEquals(ACCURACY, gas.getAccuracy());
        assertEquals(FREQUENCY, gas.getUseFrequency());
    }

    @Test
    public void testIsNonZero() {
        CalibrationGas gas = new CalibrationGas();
        try {
            gas.isNonZero();
            fail("calling isNonZero on a unassigned CalibrationGas succeeded");
        } catch ( IllegalStateException ex ) {
            // Expected result
        }
        gas.setConcentration(new NumericString("0.0", null));
        assertFalse(gas.isNonZero());
        gas.setConcentration(CONCENTRATION);
        try {
            gas.isNonZero();
            fail("calling isNonZero in a CalibrationGas with only concentration assigned succeeded");
        } catch ( IllegalStateException ex ) {
            // Expected result
        }
        gas.setAccuracy(ACCURACY);
        assertTrue(gas.isNonZero());
        gas.setConcentration(ZERO_CONC);
        assertFalse(gas.isNonZero());
    }

    @Test
    public void testInvalidFieldNames() {
        CalibrationGas gas = new CalibrationGas();
        assertEquals(new HashSet<String>(
                Arrays.asList("type", "supplier", "concentration")), gas.invalidFieldNames());
        gas = new CalibrationGas(GAS_ID, GAS_TYPE, SUPPLIER, null, null, null);
        assertEquals(new HashSet<String>(Arrays.asList("concentration")), gas.invalidFieldNames());
        gas.setConcentration(CONCENTRATION);
        assertEquals(new HashSet<String>(), gas.invalidFieldNames());
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
        gas.setUseFrequency(FREQUENCY);
        gas.setConcentration(CONCENTRATION);
        gas.setAccuracy(ACCURACY);
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

        first.setUseFrequency(FREQUENCY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setUseFrequency(FREQUENCY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setConcentration(CONCENTRATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setConcentration(CONCENTRATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAccuracy(ACCURACY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAccuracy(ACCURACY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}
