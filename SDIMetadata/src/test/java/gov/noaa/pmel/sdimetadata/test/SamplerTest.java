package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.instrument.GasSensor;
import gov.noaa.pmel.sdimetadata.instrument.Instrument;
import gov.noaa.pmel.sdimetadata.instrument.Sampler;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class SamplerTest {

    private static final String NAME = "Equilibrator";
    private static final String ID = "325";
    private static final String MANUFACTURER = "NOAA";
    private static final String MODEL = "7";
    private static final String LOCATION = "Bow of ship";
    private static final ArrayList<String> ADDN_INFO = new ArrayList<String>(Arrays.asList(
            "Some comment",
            "Another comment"
    ));

    @Test
    public void testClone() {
        Sampler sampler = new Sampler();
        Sampler dup = sampler.clone();
        assertEquals(sampler, dup);
        assertNotSame(sampler, dup);

        sampler.setName(NAME);
        sampler.setId(ID);
        sampler.setManufacturer(MANUFACTURER);
        sampler.setModel(MODEL);
        sampler.setLocation(LOCATION);
        sampler.setAddnInfo(ADDN_INFO);
        assertNotEquals(sampler, dup);

        dup = sampler.clone();
        assertEquals(sampler, dup);
        assertNotSame(sampler, dup);
        assertNotSame(sampler.getAddnInfo(), dup.getAddnInfo());
    }

    @Test
    public void testHashCodeEquals() {
        Sampler first = new Sampler();
        assertFalse(first.equals(null));
        assertFalse(first.equals(LOCATION));

        Sampler second = new Sampler();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        GasSensor gasSensor = new GasSensor();
        assertFalse(first.equals(gasSensor));
        assertFalse(gasSensor.equals(second));

        Instrument other = new Instrument();
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setName(NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setName(NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setName(NAME);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setId(ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setId(ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setId(ID);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setManufacturer(MANUFACTURER);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setManufacturer(MANUFACTURER);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setManufacturer(MANUFACTURER);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setModel(MODEL);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setModel(MODEL);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setModel(MODEL);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setLocation(LOCATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLocation(LOCATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setLocation(LOCATION);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setAddnInfo(ADDN_INFO);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAddnInfo(ADDN_INFO);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setAddnInfo(ADDN_INFO);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));
    }

}
