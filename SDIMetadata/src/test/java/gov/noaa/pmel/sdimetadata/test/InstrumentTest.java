package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.instrument.Instrument;
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

public class InstrumentTest {

    private static final String EMPTY_STRING = "";
    private static final String NAME = "Equilibrator headspace differential pressure sensor";
    private static final String ID = "Setra-239 #0003245";
    private static final String MANUFACTURER = "Setra";
    private static final String MODEL = "239";
    private static final String LOCATION = "Attached to equilibrator headspace";
    private static final ArrayList<String> ADDN_INFO = new ArrayList<String>(Arrays.asList(
            "Pressure reading from the Setra-270 on the exit of the analyzer was added to the differential pressure " +
                    "reading from Setra-239 attached to the equilibrator headspace to yield the equlibrator pressure.",
            "Some other comment just to have a second one."
    ));

    @Test
    public void testGetSetName() {
        Instrument sensor = new Instrument();
        assertEquals(EMPTY_STRING, sensor.getName());
        sensor.setName(NAME);
        assertEquals(NAME, sensor.getName());
        sensor.setName(null);
        assertEquals(EMPTY_STRING, sensor.getName());
    }

    @Test
    public void testGetSetId() {
        Instrument sensor = new Instrument();
        assertEquals(EMPTY_STRING, sensor.getId());
        sensor.setId(ID);
        assertEquals(ID, sensor.getId());
        assertEquals(EMPTY_STRING, sensor.getName());
        sensor.setId(null);
        assertEquals(EMPTY_STRING, sensor.getId());
    }

    @Test
    public void testGetSetManufacturer() {
        Instrument sensor = new Instrument();
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
        sensor.setManufacturer(MANUFACTURER);
        assertEquals(MANUFACTURER, sensor.getManufacturer());
        assertEquals(EMPTY_STRING, sensor.getId());
        assertEquals(EMPTY_STRING, sensor.getName());
        sensor.setManufacturer(null);
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
    }

    @Test
    public void testGetSetModel() {
        Instrument sensor = new Instrument();
        assertEquals(EMPTY_STRING, sensor.getModel());
        sensor.setModel(MODEL);
        assertEquals(MODEL, sensor.getModel());
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
        assertEquals(EMPTY_STRING, sensor.getId());
        assertEquals(EMPTY_STRING, sensor.getName());
        sensor.setModel(null);
        assertEquals(EMPTY_STRING, sensor.getModel());
    }

    @Test
    public void testGetSetLocation() {
        Instrument sensor = new Instrument();
        assertEquals(EMPTY_STRING, sensor.getLocation());
        sensor.setLocation(LOCATION);
        assertEquals(LOCATION, sensor.getLocation());
        assertEquals(EMPTY_STRING, sensor.getModel());
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
        assertEquals(EMPTY_STRING, sensor.getId());
        assertEquals(EMPTY_STRING, sensor.getName());
        sensor.setLocation(null);
        assertEquals(EMPTY_STRING, sensor.getLocation());
    }

    @Test
    public void testGetSetAddnInfo() {
        Instrument sensor = new Instrument();
        assertEquals(0, sensor.getAddnInfo().size());
        sensor.setAddnInfo(ADDN_INFO);
        assertEquals(ADDN_INFO, sensor.getAddnInfo());
        assertNotSame(ADDN_INFO, sensor.getAddnInfo());
        assertEquals(EMPTY_STRING, sensor.getModel());
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
        assertEquals(EMPTY_STRING, sensor.getLocation());
        assertEquals(EMPTY_STRING, sensor.getId());
        assertEquals(EMPTY_STRING, sensor.getName());
        sensor.setAddnInfo(null);
        assertEquals(0, sensor.getAddnInfo().size());
        sensor.setAddnInfo(ADDN_INFO);
        assertEquals(ADDN_INFO, sensor.getAddnInfo());
        sensor.setAddnInfo(new HashSet<String>());
        assertEquals(0, sensor.getAddnInfo().size());
        try {
            sensor.setAddnInfo(Arrays.asList(ADDN_INFO.get(0), "\n"));
            fail("setAddnInfo called with a list containing a blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            sensor.setAddnInfo(Arrays.asList(ADDN_INFO.get(0), null));
            fail("setAddnInfo called with a list containing null succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testIsValid() {
        Instrument sensor = new Instrument();
        assertFalse(sensor.isValid());
        sensor.setName(NAME);
        assertTrue(sensor.isValid());
    }

    @Test
    public void testClone() {
        Instrument sensor = new Instrument();
        Instrument dup = sensor.clone();
        assertEquals(sensor, dup);
        assertNotSame(sensor, dup);

        sensor.setName(NAME);
        sensor.setId(ID);
        sensor.setManufacturer(MANUFACTURER);
        sensor.setModel(MODEL);
        sensor.setLocation(LOCATION);
        sensor.setAddnInfo(ADDN_INFO);
        assertNotEquals(sensor, dup);

        dup = sensor.clone();
        assertEquals(sensor, dup);
        assertNotSame(sensor, dup);
    }

    @Test
    public void testHashCodeEquals() {
        Instrument first = new Instrument();
        assertFalse(first.equals(null));
        assertFalse(first.equals(LOCATION));

        Instrument second = new Instrument();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setName(NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setName(NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setId(ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setId(ID);
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

        first.setLocation(LOCATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLocation(LOCATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAddnInfo(ADDN_INFO);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAddnInfo(ADDN_INFO);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}

