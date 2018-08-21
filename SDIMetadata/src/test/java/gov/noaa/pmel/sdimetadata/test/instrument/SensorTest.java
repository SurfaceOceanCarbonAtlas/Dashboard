package gov.noaa.pmel.sdimetadata.test.instrument;

import gov.noaa.pmel.sdimetadata.instrument.Sensor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SensorTest {

    private static final String EMPTY_STRING = "";
    private static final String LOCATION = "Attached to equilibrator headspace";
    private static final String MANUFACTURER = "Setra";
    private static final String MODEL = "239";
    private static final String CALIBRATION = "Factory calibration";
    private static final ArrayList<String> COMMENTS = new ArrayList<String>(Arrays.asList(
            "Pressure reading from the Setra-270 on the exit of the analyzer was added to the differential pressure reading from Setra-239 attached to the equilibrator headspace to yield the equlibrator pressure.",
            "Some other comment just to have a second one."
    ));

    @Test
    public void testGetSetLocation() {
        Sensor sensor = new Sensor();
        assertEquals(EMPTY_STRING, sensor.getLocation());
        sensor.setLocation(LOCATION);
        assertEquals(LOCATION, sensor.getLocation());
        sensor.setLocation(null);
        assertEquals(EMPTY_STRING, sensor.getLocation());
    }

    @Test
    public void testGetSetManufacturer() {
        Sensor sensor = new Sensor();
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
        sensor.setManufacturer(MANUFACTURER);
        assertEquals(MANUFACTURER, sensor.getManufacturer());
        assertEquals(EMPTY_STRING, sensor.getLocation());
        sensor.setManufacturer(null);
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
    }

    @Test
    public void testGetSetModel() {
        Sensor sensor = new Sensor();
        assertEquals(EMPTY_STRING, sensor.getModel());
        sensor.setModel(MODEL);
        assertEquals(MODEL, sensor.getModel());
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
        assertEquals(EMPTY_STRING, sensor.getLocation());
        sensor.setModel(null);
        assertEquals(EMPTY_STRING, sensor.getModel());
    }

    @Test
    public void testGetSetCalibration() {
        Sensor sensor = new Sensor();
        assertEquals(EMPTY_STRING, sensor.getCalibration());
        sensor.setCalibration(CALIBRATION);
        assertEquals(CALIBRATION, sensor.getCalibration());
        assertEquals(EMPTY_STRING, sensor.getModel());
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
        assertEquals(EMPTY_STRING, sensor.getLocation());
        sensor.setCalibration(null);
        assertEquals(EMPTY_STRING, sensor.getCalibration());
    }

    @Test
    public void testGetSetComments() {
        Sensor sensor = new Sensor();
        assertEquals(0, sensor.getComments().size());
        sensor.setComments(COMMENTS);
        assertEquals(COMMENTS, sensor.getComments());
        assertNotSame(COMMENTS, sensor.getComments());
        assertEquals(EMPTY_STRING, sensor.getCalibration());
        assertEquals(EMPTY_STRING, sensor.getModel());
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
        assertEquals(EMPTY_STRING, sensor.getLocation());
        sensor.setComments(null);
        assertEquals(0, sensor.getComments().size());
        sensor.setComments(COMMENTS);
        assertEquals(COMMENTS, sensor.getComments());
        sensor.setComments(new HashSet<String>());
        assertEquals(0, sensor.getComments().size());
        try {
            sensor.setComments(Arrays.asList(COMMENTS.get(0), "\n"));
            fail("setComments called with a list containing a blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            sensor.setComments(Arrays.asList(COMMENTS.get(0), null));
            fail("setComments called with a list containing null succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testIsValid() {
        Sensor sensor = new Sensor();
        assertFalse(sensor.isValid());
        sensor.setLocation(LOCATION);
        sensor.setManufacturer(MANUFACTURER);
        sensor.setModel(MODEL);
        assertTrue(sensor.isValid());
    }

    @Test
    public void testClone() {
        Sensor sensor = new Sensor();
        Sensor dup = sensor.clone();
        assertEquals(sensor, dup);
        assertNotSame(sensor, dup);

        sensor.setLocation(LOCATION);
        sensor.setManufacturer(MANUFACTURER);
        sensor.setModel(MODEL);
        sensor.setCalibration(CALIBRATION);
        sensor.setComments(COMMENTS);
        assertNotEquals(sensor, dup);

        dup = sensor.clone();
        assertEquals(sensor, dup);
        assertNotSame(sensor, dup);
    }

    @Test
    public void testHashCodeEquals() {
        Sensor first = new Sensor();
        assertFalse(first.equals(null));
        assertFalse(first.equals(LOCATION));

        Sensor second = new Sensor();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setLocation(LOCATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLocation(LOCATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setManufacturer(MANUFACTURER);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setManufacturer(MANUFACTURER);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setModel(MODEL);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setModel(MODEL);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setCalibration(CALIBRATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCalibration(CALIBRATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setComments(COMMENTS);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setComments(COMMENTS);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}

