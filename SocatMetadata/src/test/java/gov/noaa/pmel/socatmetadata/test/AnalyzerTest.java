package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.instrument.Analyzer;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;
import gov.noaa.pmel.socatmetadata.shared.instrument.Sampler;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class AnalyzerTest {

    private static final String EMPTY_STRING = "";
    private static final String NAME = "Equilibrator headspace differential pressure sensor";
    private static final String ID = "Setra-239 #0003245";
    private static final String MANUFACTURER = "Setra";
    private static final String MODEL = "239";
    private static final String CALIBRATION = "Factory calibration";
    private static final ArrayList<String> ADDN_INFO = new ArrayList<String>(Arrays.asList(
            "Pressure reading from the Setra-270 on the exit of the sensor was added to the differential pressure " +
                    "reading from Setra-239 attached to the equilibrator headspace to yield the equlibrator pressure.",
            "Some other comment just to have a second one."
    ));

    @Test
    public void testGetSetCalibration() {
        Analyzer sensor = new Analyzer();
        assertEquals(EMPTY_STRING, sensor.getCalibration());
        sensor.setCalibration(CALIBRATION);
        assertEquals(CALIBRATION, sensor.getCalibration());
        assertEquals(EMPTY_STRING, sensor.getModel());
        assertEquals(EMPTY_STRING, sensor.getManufacturer());
        assertEquals(EMPTY_STRING, sensor.getId());
        assertEquals(EMPTY_STRING, sensor.getName());
        sensor.setCalibration(null);
        assertEquals(EMPTY_STRING, sensor.getCalibration());
        sensor.setCalibration("\t");
        assertEquals(EMPTY_STRING, sensor.getCalibration());
    }

    @Test
    public void testDuplicate() {
        Analyzer analyzer = new Analyzer();
        Analyzer dup = analyzer.duplicate(null);
        assertEquals(analyzer, dup);
        assertNotSame(analyzer, dup);

        analyzer.setName(NAME);
        analyzer.setId(ID);
        analyzer.setManufacturer(MANUFACTURER);
        analyzer.setModel(MODEL);
        analyzer.setAddnInfo(ADDN_INFO);
        analyzer.setCalibration(CALIBRATION);
        assertNotEquals(analyzer, dup);

        dup = analyzer.duplicate(null);
        assertEquals(analyzer, dup);
        assertNotSame(analyzer, dup);
        assertNotSame(analyzer.getAddnInfo(), dup.getAddnInfo());
    }

    @Test
    public void testHashCodeEquals() {
        Analyzer first = new Analyzer();
        assertFalse(first.equals(null));
        assertFalse(first.equals(NAME));

        Analyzer second = new Analyzer();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        Sampler sampler = new Sampler();
        assertFalse(first.equals(sampler));
        assertFalse(sampler.equals(second));

        Instrument other = new Instrument();
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setCalibration(CALIBRATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCalibration(CALIBRATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

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
    }

}
