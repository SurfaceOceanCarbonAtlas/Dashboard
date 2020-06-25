package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.MultiString;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class InstrumentTest {

    private static final String EMPTY_STRING = "";
    private static final MultiString EMPTY_MULTISTRING = new MultiString();
    private static final HashSet<String> EMPTY_NAMESET = new HashSet<String>();

    private static final String NAME = "Equilibrator headspace differential pressure sensor";
    private static final String ID = "Setra-239 #0003245";
    private static final String MANUFACTURER = "Setra";
    private static final String MODEL = "239";
    private static final MultiString ADDN_INFO = new MultiString(
            "Pressure reading from the Setra-270 on the exit of the analyzer was added to the " +
                    "differential pressure reading from Setra-239 attached to the equilibrator headspace " +
                    "to yield the equlibrator pressure.\n" +
                    "Some other comment just to have a second one."
    );

    @Test
    public void testGetSetName() {
        Instrument sensor = new Instrument();
        assertEquals(EMPTY_STRING, sensor.getName());
        sensor.setName(NAME);
        assertEquals(NAME, sensor.getName());
        sensor.setName(null);
        assertEquals(EMPTY_STRING, sensor.getName());
        sensor.setName("\t");
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
        sensor.setId("\t");
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
        sensor.setManufacturer("\t");
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
        sensor.setModel("\t");
        assertEquals(EMPTY_STRING, sensor.getModel());
    }

    @Test
    public void testGetSetAddnInfo() {
        Instrument sensor = new Instrument();
        assertEquals(EMPTY_MULTISTRING, sensor.getAddnInfo());
        sensor.setAddnInfo(ADDN_INFO);
        MultiString info = sensor.getAddnInfo();
        assertEquals(ADDN_INFO, info);
        assertNotSame(ADDN_INFO, info);
        assertNotSame(info, sensor.getAddnInfo());
        assertEquals(EMPTY_STRING, sensor.getModel());
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
        assertEquals(EMPTY_STRING, sensor.getId());
        assertEquals(EMPTY_STRING, sensor.getName());
        sensor.setAddnInfo(null);
        assertEquals(EMPTY_MULTISTRING, sensor.getAddnInfo());
        sensor.setAddnInfo(EMPTY_MULTISTRING);
        assertEquals(EMPTY_MULTISTRING, sensor.getAddnInfo());
    }

    @Test
    public void testInvalidFieldNames() {
        Instrument sensor = new Instrument();
        assertEquals(new HashSet<String>(Arrays.asList("name")), sensor.invalidFieldNames());
        sensor.setName(NAME);
        assertEquals(EMPTY_NAMESET, sensor.invalidFieldNames());
    }

    @Test
    public void testDuplicate() {
        Instrument sensor = new Instrument();
        Instrument dup = (Instrument) (sensor.duplicate(null));
        assertEquals(sensor, dup);
        assertNotSame(sensor, dup);

        sensor.setName(NAME);
        sensor.setId(ID);
        sensor.setManufacturer(MANUFACTURER);
        sensor.setModel(MODEL);
        sensor.setAddnInfo(ADDN_INFO);
        assertNotEquals(sensor, dup);

        dup = (Instrument) (sensor.duplicate(null));
        assertEquals(sensor, dup);
        assertNotSame(sensor, dup);
    }

    @Test
    public void testHashCodeEquals() {
        Instrument first = new Instrument();
        assertFalse(first.equals(null));
        assertFalse(first.equals(NAME));

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

        first.setAddnInfo(ADDN_INFO);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAddnInfo(ADDN_INFO);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}
