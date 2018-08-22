package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.variable.Variable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class VariableTest {

    private static final String EMPTY_STRING = "";
    private static final double DELTA = 1.0E-6;
    private static final String VAR_NAME = "SST_C";
    private static final String VAR_UNIT = "degrees Celsius";
    private static final String DESCRIPTION = "Sea surface temperature";
    private static final String CALIBRATION = "Factory calibration";
    private static final double PRECISION = 0.001;
    private static final String PRECISION_UNIT = "degrees C";
    private static final double ACCURACY = 0.01;
    private static final String ACCURACY_UNIT = "deg C";
    private static final String SENSOR_NAME = "Ship's SST sensor";

    @Test
    public void testGetSetVarName() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getVarName());
        var.setVarName(VAR_NAME);
        assertEquals(VAR_NAME, var.getVarName());
        var.setVarName(null);
        assertEquals(EMPTY_STRING, var.getVarName());
    }

    @Test
    public void testGetSetVarUnit() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getVarUnit());
        var.setVarUnit(VAR_UNIT);
        assertEquals(VAR_UNIT, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getVarName());
        var.setVarUnit(null);
        assertEquals(EMPTY_STRING, var.getVarUnit());
    }

    @Test
    public void testGetSetDescription() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getDescription());
        var.setDescription(DESCRIPTION);
        assertEquals(DESCRIPTION, var.getDescription());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getVarName());
        var.setDescription(null);
        assertEquals(EMPTY_STRING, var.getDescription());
    }

    @Test
    public void testGetSetCalibration() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getCalibration());
        var.setCalibration(CALIBRATION);
        assertEquals(CALIBRATION, var.getCalibration());
        assertEquals(EMPTY_STRING, var.getDescription());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getVarName());
        var.setCalibration(null);
        assertEquals(EMPTY_STRING, var.getCalibration());
    }

    @Test
    public void testGetSetPrecision() {
        Variable var = new Variable();
        assertTrue(var.getPrecision().isNaN());
        var.setPrecision(PRECISION);
        assertEquals(PRECISION, var.getPrecision(), DELTA);
        assertEquals(EMPTY_STRING, var.getCalibration());
        assertEquals(EMPTY_STRING, var.getDescription());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getVarName());
        var.setPrecision(null);
        assertTrue(var.getPrecision().isNaN());
    }

    @Test
    public void testGetSetPrecisionUnit() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getPrecisionUnit());
        var.setPrecisionUnit(PRECISION_UNIT);
        assertEquals(PRECISION_UNIT, var.getPrecisionUnit());
        assertTrue(var.getPrecision().isNaN());
        assertEquals(EMPTY_STRING, var.getCalibration());
        assertEquals(EMPTY_STRING, var.getDescription());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getVarName());
        var.setPrecisionUnit(null);
        assertEquals(EMPTY_STRING, var.getPrecisionUnit());
    }

    @Test
    public void testGetSetAccuracy() {
        Variable var = new Variable();
        assertTrue(var.getAccuracy().isNaN());
        var.setAccuracy(ACCURACY);
        assertEquals(ACCURACY, var.getAccuracy(), DELTA);
        assertEquals(EMPTY_STRING, var.getPrecisionUnit());
        assertTrue(var.getPrecision().isNaN());
        assertEquals(EMPTY_STRING, var.getCalibration());
        assertEquals(EMPTY_STRING, var.getDescription());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getVarName());
        var.setAccuracy(null);
        assertTrue(var.getAccuracy().isNaN());
    }

    @Test
    public void testGetSetAccuracyUnit() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getAccuracyUnit());
        var.setAccuracyUnit(ACCURACY_UNIT);
        assertEquals(ACCURACY_UNIT, var.getAccuracyUnit());
        assertTrue(var.getAccuracy().isNaN());
        assertEquals(EMPTY_STRING, var.getPrecisionUnit());
        assertTrue(var.getPrecision().isNaN());
        assertEquals(EMPTY_STRING, var.getCalibration());
        assertEquals(EMPTY_STRING, var.getDescription());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getVarName());
        var.setAccuracyUnit(null);
        assertEquals(EMPTY_STRING, var.getAccuracyUnit());
    }

    @Test
    public void testGetSetSensorName() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getSensorName());
        var.setSensorName(SENSOR_NAME);
        assertEquals(SENSOR_NAME, var.getSensorName());
        assertEquals(EMPTY_STRING, var.getAccuracyUnit());
        assertTrue(var.getAccuracy().isNaN());
        assertEquals(EMPTY_STRING, var.getPrecisionUnit());
        assertTrue(var.getPrecision().isNaN());
        assertEquals(EMPTY_STRING, var.getCalibration());
        assertEquals(EMPTY_STRING, var.getDescription());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getVarName());
        var.setSensorName(null);
        assertEquals(EMPTY_STRING, var.getSensorName());
    }

    @Test
    public void testIsValid() {
        Variable var = new Variable();
        assertFalse(var.isValid());

        var.setVarName(VAR_NAME);
        var.setVarUnit(VAR_UNIT);
        var.setDescription(DESCRIPTION);
        var.setPrecision(PRECISION);
        var.setPrecisionUnit(PRECISION_UNIT);
        var.setAccuracy(ACCURACY);
        var.setAccuracyUnit(ACCURACY_UNIT);
        var.setSensorName(SENSOR_NAME);
        assertTrue(var.isValid());
    }

    @Test
    public void testClone() {
        Variable var = new Variable();
        Variable dup = var.clone();
        assertEquals(var, dup);
        assertNotSame(var, dup);

        var.setVarName(VAR_NAME);
        var.setVarUnit(VAR_UNIT);
        var.setDescription(DESCRIPTION);
        var.setCalibration(CALIBRATION);
        var.setPrecision(PRECISION);
        var.setPrecisionUnit(PRECISION_UNIT);
        var.setAccuracy(ACCURACY);
        var.setAccuracyUnit(ACCURACY_UNIT);
        var.setSensorName(SENSOR_NAME);
        assertNotEquals(var, dup);

        dup = var.clone();
        assertEquals(var, dup);
        assertNotSame(var, dup);
    }

    @Test
    public void testHashCodeEquals() {
        Variable first = new Variable();
        assertFalse(first.equals(null));
        assertFalse(first.equals(VAR_NAME));

        Variable second = new Variable();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setVarName(VAR_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setVarName(VAR_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setVarUnit(VAR_UNIT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setVarUnit(VAR_UNIT);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setDescription(DESCRIPTION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setDescription(DESCRIPTION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setCalibration(CALIBRATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCalibration(CALIBRATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPrecision(PRECISION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPrecision(PRECISION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPrecisionUnit(PRECISION_UNIT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPrecisionUnit(PRECISION_UNIT);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAccuracy(ACCURACY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAccuracy(ACCURACY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAccuracyUnit(ACCURACY_UNIT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAccuracyUnit(ACCURACY_UNIT);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSensorName(SENSOR_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSensorName(SENSOR_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}

