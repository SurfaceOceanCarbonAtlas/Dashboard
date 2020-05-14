package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.MultiString;
import gov.noaa.pmel.socatmetadata.shared.instrument.Equilibrator;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class EquilibratorTest {

    private static final String EMPTY_STRING = "";
    private static final MultiString EMPTY_MULTISTRING = new MultiString();

    private static final String NAME = "Equilibrator";
    private static final String ID = "325";
    private static final String MANUFACTURER = "NOAA";
    private static final String MODEL = "7";
    private static final MultiString ADDN_INFO = new MultiString(
            "Some comment\n" +
                    "Another comment"
    );
    private static final String EQUILIBRATOR_TYPE = "Sprayhead above dynamic pool, with thermal jacket";
    private static final String CHAMBER_VOLUME = "0.95L";
    private static final String CHAMBER_WATER_VOLUME = "0.4L";
    private static final String CHAMBER_GAS_VOLUME = "0.4L";
    private static final String WATER_FLOW_RATE = "1.5 - 2.0 L/min";
    private static final String GAS_FLOW_RATE = "70 - 150 ml/min";
    private static final String VENTING = "Primary equlibrator is vented through a secondary equilibrator";

    @Test
    public void testInvalidFieldNames() {
        Equilibrator sampler = new Equilibrator();
        assertEquals(new HashSet<String>(Arrays.asList(
                "name", "instrumentNames", "equilibratorType", "chamberWaterVol", "chamberGasVol",
                "waterFlowRate", "gasFlowRate", "venting"
        )), sampler.invalidFieldNames());

        sampler.setName(NAME);
        sampler.setInstrumentNames(Arrays.asList("TemperatureSensor", "PressureSensor", "CO2Sensor"));
        sampler.setEquilibratorType(EQUILIBRATOR_TYPE);
        assertEquals(new HashSet<String>(Arrays.asList(
                "chamberWaterVol", "chamberGasVol", "waterFlowRate", "gasFlowRate", "venting"
        )), sampler.invalidFieldNames());

        sampler.setChamberWaterVol(CHAMBER_WATER_VOLUME);
        assertEquals(new HashSet<String>(Arrays.asList(
                "chamberGasVol", "waterFlowRate", "gasFlowRate", "venting"
        )), sampler.invalidFieldNames());

        sampler.setChamberGasVol(CHAMBER_GAS_VOLUME);
        assertEquals(new HashSet<String>(Arrays.asList(
                "waterFlowRate", "gasFlowRate", "venting"
        )), sampler.invalidFieldNames());

        sampler.setWaterFlowRate(WATER_FLOW_RATE);
        assertEquals(new HashSet<String>(Arrays.asList("gasFlowRate", "venting")), sampler.invalidFieldNames());

        sampler.setGasFlowRate(GAS_FLOW_RATE);
        assertEquals(new HashSet<String>(Arrays.asList("venting")), sampler.invalidFieldNames());

        sampler.setVenting(VENTING);
        assertEquals(new HashSet<String>(), sampler.invalidFieldNames());
    }

    @Test
    public void testGetSetEquilibratorType() {
        Equilibrator sampler = new Equilibrator();
        assertEquals(EMPTY_STRING, sampler.getEquilibratorType());
        sampler.setEquilibratorType(EQUILIBRATOR_TYPE);
        assertEquals(EQUILIBRATOR_TYPE, sampler.getEquilibratorType());
        assertEquals(EMPTY_MULTISTRING, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getModel());
        assertEquals(EMPTY_STRING, sampler.getManufacturer());
        assertEquals(EMPTY_STRING, sampler.getId());
        assertEquals(EMPTY_STRING, sampler.getName());
        sampler.setEquilibratorType(null);
        assertEquals(EMPTY_STRING, sampler.getEquilibratorType());
        sampler.setEquilibratorType("\t");
        assertEquals(EMPTY_STRING, sampler.getEquilibratorType());
    }

    @Test
    public void testGetSetChamberVol() {
        Equilibrator sampler = new Equilibrator();
        assertEquals(EMPTY_STRING, sampler.getChamberVol());
        sampler.setChamberVol(CHAMBER_VOLUME);
        assertEquals(CHAMBER_VOLUME, sampler.getChamberVol());
        assertEquals(EMPTY_STRING, sampler.getEquilibratorType());
        assertEquals(EMPTY_MULTISTRING, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getModel());
        assertEquals(EMPTY_STRING, sampler.getManufacturer());
        assertEquals(EMPTY_STRING, sampler.getId());
        assertEquals(EMPTY_STRING, sampler.getName());
        sampler.setChamberVol(null);
        assertEquals(EMPTY_STRING, sampler.getChamberVol());
        sampler.setChamberVol("\t");
        assertEquals(EMPTY_STRING, sampler.getChamberVol());
    }

    @Test
    public void testGetSetChamberWaterVol() {
        Equilibrator sampler = new Equilibrator();
        assertEquals(EMPTY_STRING, sampler.getChamberWaterVol());
        sampler.setChamberWaterVol(CHAMBER_WATER_VOLUME);
        assertEquals(CHAMBER_WATER_VOLUME, sampler.getChamberWaterVol());
        assertEquals(EMPTY_STRING, sampler.getChamberVol());
        assertEquals(EMPTY_STRING, sampler.getEquilibratorType());
        assertEquals(EMPTY_MULTISTRING, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getModel());
        assertEquals(EMPTY_STRING, sampler.getManufacturer());
        assertEquals(EMPTY_STRING, sampler.getId());
        assertEquals(EMPTY_STRING, sampler.getName());
        sampler.setChamberWaterVol(null);
        assertEquals(EMPTY_STRING, sampler.getChamberWaterVol());
        sampler.setChamberWaterVol("\t");
        assertEquals(EMPTY_STRING, sampler.getChamberWaterVol());
    }

    @Test
    public void testGetSetChamberGasVol() {
        Equilibrator sampler = new Equilibrator();
        assertEquals(EMPTY_STRING, sampler.getChamberGasVol());
        sampler.setChamberGasVol(CHAMBER_GAS_VOLUME);
        assertEquals(CHAMBER_GAS_VOLUME, sampler.getChamberGasVol());
        assertEquals(EMPTY_STRING, sampler.getChamberWaterVol());
        assertEquals(EMPTY_STRING, sampler.getChamberVol());
        assertEquals(EMPTY_STRING, sampler.getEquilibratorType());
        assertEquals(EMPTY_MULTISTRING, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getModel());
        assertEquals(EMPTY_STRING, sampler.getManufacturer());
        assertEquals(EMPTY_STRING, sampler.getId());
        assertEquals(EMPTY_STRING, sampler.getName());
        sampler.setChamberGasVol(null);
        assertEquals(EMPTY_STRING, sampler.getChamberGasVol());
        sampler.setChamberGasVol("\t");
        assertEquals(EMPTY_STRING, sampler.getChamberGasVol());
    }

    @Test
    public void testGetSetWaterFlowRate() {
        Equilibrator sampler = new Equilibrator();
        assertEquals(EMPTY_STRING, sampler.getWaterFlowRate());
        sampler.setWaterFlowRate(WATER_FLOW_RATE);
        assertEquals(WATER_FLOW_RATE, sampler.getWaterFlowRate());
        assertEquals(EMPTY_STRING, sampler.getChamberGasVol());
        assertEquals(EMPTY_STRING, sampler.getChamberWaterVol());
        assertEquals(EMPTY_STRING, sampler.getChamberVol());
        assertEquals(EMPTY_STRING, sampler.getEquilibratorType());
        assertEquals(EMPTY_MULTISTRING, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getModel());
        assertEquals(EMPTY_STRING, sampler.getManufacturer());
        assertEquals(EMPTY_STRING, sampler.getId());
        assertEquals(EMPTY_STRING, sampler.getName());
        sampler.setWaterFlowRate(null);
        assertEquals(EMPTY_STRING, sampler.getWaterFlowRate());
        sampler.setWaterFlowRate("\t");
        assertEquals(EMPTY_STRING, sampler.getWaterFlowRate());
    }

    @Test
    public void testGetSetGasFlowRate() {
        Equilibrator sampler = new Equilibrator();
        assertEquals(EMPTY_STRING, sampler.getGasFlowRate());
        sampler.setGasFlowRate(GAS_FLOW_RATE);
        assertEquals(GAS_FLOW_RATE, sampler.getGasFlowRate());
        assertEquals(EMPTY_STRING, sampler.getWaterFlowRate());
        assertEquals(EMPTY_STRING, sampler.getChamberGasVol());
        assertEquals(EMPTY_STRING, sampler.getChamberWaterVol());
        assertEquals(EMPTY_STRING, sampler.getChamberVol());
        assertEquals(EMPTY_STRING, sampler.getEquilibratorType());
        assertEquals(EMPTY_MULTISTRING, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getModel());
        assertEquals(EMPTY_STRING, sampler.getManufacturer());
        assertEquals(EMPTY_STRING, sampler.getId());
        assertEquals(EMPTY_STRING, sampler.getName());
        sampler.setGasFlowRate(null);
        assertEquals(EMPTY_STRING, sampler.getGasFlowRate());
        sampler.setGasFlowRate("\t");
        assertEquals(EMPTY_STRING, sampler.getGasFlowRate());
    }

    @Test
    public void testGetSetVenting() {
        Equilibrator sampler = new Equilibrator();
        assertEquals(EMPTY_STRING, sampler.getVenting());
        sampler.setVenting(VENTING);
        assertEquals(VENTING, sampler.getVenting());
        assertEquals(EMPTY_STRING, sampler.getGasFlowRate());
        assertEquals(EMPTY_STRING, sampler.getWaterFlowRate());
        assertEquals(EMPTY_STRING, sampler.getChamberGasVol());
        assertEquals(EMPTY_STRING, sampler.getChamberWaterVol());
        assertEquals(EMPTY_STRING, sampler.getChamberVol());
        assertEquals(EMPTY_STRING, sampler.getEquilibratorType());
        assertEquals(EMPTY_MULTISTRING, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getModel());
        assertEquals(EMPTY_STRING, sampler.getManufacturer());
        assertEquals(EMPTY_STRING, sampler.getId());
        assertEquals(EMPTY_STRING, sampler.getName());
        sampler.setVenting(null);
        assertEquals(EMPTY_STRING, sampler.getVenting());
        sampler.setVenting("\t");
        assertEquals(EMPTY_STRING, sampler.getVenting());
    }

    @Test
    public void testDuplicate() {
        Equilibrator sampler = new Equilibrator();
        Equilibrator dup = (Equilibrator) (sampler.duplicate(null));
        assertEquals(sampler, dup);
        assertNotSame(sampler, dup);

        sampler.setName(NAME);
        sampler.setId(ID);
        sampler.setManufacturer(MANUFACTURER);
        sampler.setModel(MODEL);
        sampler.setAddnInfo(ADDN_INFO);
        sampler.setEquilibratorType(EQUILIBRATOR_TYPE);
        sampler.setChamberVol(CHAMBER_VOLUME);
        sampler.setChamberWaterVol(CHAMBER_WATER_VOLUME);
        sampler.setChamberGasVol(CHAMBER_GAS_VOLUME);
        sampler.setWaterFlowRate(WATER_FLOW_RATE);
        sampler.setGasFlowRate(GAS_FLOW_RATE);
        sampler.setVenting(VENTING);
        assertNotEquals(sampler, dup);

        dup = (Equilibrator) (sampler.duplicate(null));
        assertEquals(sampler, dup);
        assertNotSame(sampler, dup);
    }

    @Test
    public void testHashCodeEquals() {
        Equilibrator first = new Equilibrator();
        assertFalse(first.equals(null));
        assertFalse(first.equals(NAME));

        Equilibrator second = new Equilibrator();
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

        first.setEquilibratorType(EQUILIBRATOR_TYPE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setEquilibratorType(EQUILIBRATOR_TYPE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setChamberVol(CHAMBER_VOLUME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setChamberVol(CHAMBER_VOLUME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setChamberWaterVol(CHAMBER_WATER_VOLUME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setChamberWaterVol(CHAMBER_WATER_VOLUME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setChamberGasVol(CHAMBER_GAS_VOLUME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setChamberGasVol(CHAMBER_GAS_VOLUME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setWaterFlowRate(WATER_FLOW_RATE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setWaterFlowRate(WATER_FLOW_RATE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setGasFlowRate(GAS_FLOW_RATE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setGasFlowRate(GAS_FLOW_RATE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setVenting(VENTING);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setVenting(VENTING);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}
