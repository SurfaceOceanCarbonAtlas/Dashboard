package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.MultiString;
import gov.noaa.pmel.socatmetadata.shared.instrument.CalibrationGas;
import gov.noaa.pmel.socatmetadata.shared.instrument.GasSensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;
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

public class GasSensorTest {

    private static final String EMPTY_STR = "";
    private static final MultiString EMPTY_MULTISTR = new MultiString();
    private static final ArrayList<CalibrationGas> EMPTY_GASLIST = new ArrayList<CalibrationGas>();

    private static final String NAME = "LI-COR 840";
    private static final String ID = "LI-COR 840 #23923";
    private static final String MANUFACTURER = "LI-COR";
    private static final String MODEL = "840";
    private static final String CALIBRATION = "Calibrated using four non-zero gases, " +
            "followed by four atmospheric CO2 measurements, then 32 aqueous CO2 measurement";
    private static final ArrayList<CalibrationGas> CALIBRATION_GASES = new ArrayList<CalibrationGas>(Arrays.asList(
            new CalibrationGas("SM-250", "CO2", "Scott Marin", "248.73", "0.01", "every 3.5 h"),
            new CalibrationGas("SM-500", "CO2", "Scott Marin", "567.40", "0.01", "every 4.5 h"),
            new CalibrationGas("SM-1000", "CO2", "Scott Marin", "1036.95", "0.01", "every 5.5 h"),
            new CalibrationGas("SM-1500", "CO2", "Scott Marin", "1533.7", "0.1", "every 6.5 h")
    ));
    private static final MultiString ADDN_INFO = new MultiString(
            "Some comment just to have one."
    );

    @Test
    public void testGetSetCalibrationGasses() {
        GasSensor sensor = new GasSensor();
        assertEquals(EMPTY_GASLIST, sensor.getCalibrationGases());
        sensor.setCalibrationGases(CALIBRATION_GASES);
        ArrayList<CalibrationGas> gasList = sensor.getCalibrationGases();
        assertEquals(CALIBRATION_GASES, gasList);
        assertNotSame(CALIBRATION_GASES, gasList);
        for (int k = 0; k < CALIBRATION_GASES.size(); k++) {
            assertNotSame(CALIBRATION_GASES.get(k), gasList.get(k));
        }
        assertNotSame(gasList, sensor.getCalibrationGases());
        assertEquals(EMPTY_MULTISTR, sensor.getAddnInfo());
        assertEquals(EMPTY_STR, sensor.getCalibration());
        assertEquals(EMPTY_STR, sensor.getModel());
        assertEquals(EMPTY_STR, sensor.getManufacturer());
        assertEquals(EMPTY_STR, sensor.getId());
        assertEquals(EMPTY_STR, sensor.getName());
        sensor.setCalibrationGases(null);
        assertEquals(EMPTY_GASLIST, sensor.getCalibrationGases());
        sensor.setCalibrationGases(new HashSet<CalibrationGas>());
        assertEquals(EMPTY_GASLIST, sensor.getCalibrationGases());
        try {
            sensor.setCalibrationGases(Arrays.asList(CALIBRATION_GASES.get(0), null, CALIBRATION_GASES.get(1)));
            fail("setCalibrationGases with a null gas succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testDuplicate() {
        GasSensor sensor = new GasSensor();
        GasSensor dup = (GasSensor) (sensor.duplicate(null));
        assertEquals(sensor, dup);
        assertNotSame(sensor, dup);

        sensor.setName(NAME);
        sensor.setId(ID);
        sensor.setManufacturer(MANUFACTURER);
        sensor.setModel(MODEL);
        sensor.setCalibration(CALIBRATION);
        sensor.setAddnInfo(ADDN_INFO);
        sensor.setCalibrationGases(CALIBRATION_GASES);
        assertNotEquals(sensor, dup);

        dup = (GasSensor) (sensor.duplicate(null));
        assertEquals(sensor, dup);
        assertNotSame(sensor, dup);
        assertNotSame(sensor.getAddnInfo(), dup.getAddnInfo());
    }

    @Test
    public void testHashCodeEquals() {
        GasSensor first = new GasSensor();
        assertFalse(first.equals(null));
        assertFalse(first.equals(NAME));

        GasSensor second = new GasSensor();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

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

        first.setCalibrationGases(CALIBRATION_GASES);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCalibrationGases(CALIBRATION_GASES);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));
    }

}
