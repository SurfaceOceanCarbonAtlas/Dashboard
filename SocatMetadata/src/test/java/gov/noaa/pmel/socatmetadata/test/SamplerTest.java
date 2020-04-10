package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.instrument.Analyzer;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;
import gov.noaa.pmel.socatmetadata.shared.instrument.Sampler;
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

public class SamplerTest {

    private static final String EMPTY_STRING = "";
    private static final ArrayList<String> EMPTY_NAMELIST = new ArrayList<String>();
    private static final HashSet<String> EMPTY_NAMESET = new HashSet<String>();

    private static final String NAME = "Equilibrator";
    private static final String ID = "325";
    private static final String MANUFACTURER = "NOAA";
    private static final String MODEL = "7";
    private static final ArrayList<String> ADDN_INFO = new ArrayList<String>(Arrays.asList(
            "Some comment",
            "Another comment"
    ));
    private static final HashSet<String> INSTRUMENT_NAMES = new HashSet<String>(Arrays.asList(
            "Equilibrator Pressure Sensor",
            "Equilibrator Temperature Sensor"
    ));

    @Test
    public void testGetSetInstrumentNames() {
        Sampler sampler = new Sampler();
        assertEquals(EMPTY_NAMESET, sampler.getInstrumentNames());
        sampler.setInstrumentNames(INSTRUMENT_NAMES);
        HashSet<String> instNames = sampler.getInstrumentNames();
        assertEquals(INSTRUMENT_NAMES, instNames);
        assertNotSame(INSTRUMENT_NAMES, instNames);
        assertNotSame(instNames, sampler.getInstrumentNames());
        assertEquals(EMPTY_NAMELIST, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getModel());
        assertEquals(EMPTY_STRING, sampler.getManufacturer());
        assertEquals(EMPTY_STRING, sampler.getId());
        assertEquals(EMPTY_STRING, sampler.getName());
        sampler.setInstrumentNames(null);
        assertEquals(EMPTY_NAMESET, sampler.getInstrumentNames());
        sampler.setInstrumentNames(EMPTY_NAMELIST);
        assertEquals(EMPTY_NAMESET, sampler.getInstrumentNames());
        try {
            sampler.setInstrumentNames(Arrays.asList("Equilibator Pressure Sensor", "\n"));
            fail("setInstrumentNames called with a list containing a blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            sampler.setInstrumentNames(Arrays.asList("Equilibator Pressure Sensor", null));
            fail("setInstrumentNames called with a list containing null succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }

    }

    @Test
    public void testDuplicate() {
        Sampler sampler = new Sampler();
        Sampler dup = (Sampler) (sampler.duplicate(null));
        assertEquals(sampler, dup);
        assertNotSame(sampler, dup);

        sampler.setName(NAME);
        sampler.setId(ID);
        sampler.setManufacturer(MANUFACTURER);
        sampler.setModel(MODEL);
        sampler.setAddnInfo(ADDN_INFO);
        sampler.setInstrumentNames(INSTRUMENT_NAMES);
        assertNotEquals(sampler, dup);

        dup = (Sampler) (sampler.duplicate(null));
        assertEquals(sampler, dup);
        assertNotSame(sampler, dup);
        assertNotSame(sampler.getAddnInfo(), dup.getAddnInfo());
    }

    @Test
    public void testHashCodeEquals() {
        Sampler first = new Sampler();
        assertFalse(first.equals(null));
        assertFalse(first.equals(NAME));

        Sampler second = new Sampler();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        Analyzer analyzer = new Analyzer();
        assertFalse(first.equals(analyzer));
        assertFalse(analyzer.equals(second));

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

        first.setAddnInfo(ADDN_INFO);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAddnInfo(ADDN_INFO);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setAddnInfo(ADDN_INFO);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setInstrumentNames(INSTRUMENT_NAMES);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setInstrumentNames(INSTRUMENT_NAMES);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));
    }

}
