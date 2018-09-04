package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.instrument.Equilibrator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class EquilibratorTest {

    private static final String EMPTY_STRING = "";
    private static final ArrayList<String> EMPTY_NAMELIST = new ArrayList<String>();

    private static final String NAME = "Equilibrator";
    private static final String ID = "325";
    private static final String MANUFACTURER = "NOAA";
    private static final String MODEL = "7";
    private static final String LOCATION = "Bow of ship";
    private static final String CALIBRATION = "Factory calibration";
    private static final ArrayList<String> ADDN_INFO = new ArrayList<String>(Arrays.asList(
            "Some comment",
            "Another comment"
    ));
    private static final String EQUILIBRATOR_TYPE = "Sprayhead above dynamic pool, with thermal jacket";
    private static final String CHAMBER_VOLUME = "0.95L";
    private static final String CHAMBER_WATER_VOLUME = "0.4L";
    private static final String CHAMBER_GAS_VOLUME = "0.4L";
    private static final String WATER_FLOW_RATE = "1.5 - 2.0 L/min";
    private static final String GAS_FLOW_RATE = "70 - 150 ml/min";
    private static final String VENTING = "Primary equlibrator is vented through a secondary equilibrator";
    private static final String DRYING = "Gas stream passes through a thermoelectric condenser (~5 &#176;C) " +
            "and then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).";

    @Test
    public void testInvalidFieldNames() {
        Equilibrator sampler = new Equilibrator();
        assertEquals(new HashSet(Arrays.asList(
                "equilibratorType", "chamberWaterVol", "chamberGasVol",
                "waterFlowRate", "gasFlowRate", "venting", "drying"
        )), sampler.invalidFieldNames());

        sampler.setEquilibratorType(EQUILIBRATOR_TYPE);
        assertEquals(new HashSet(Arrays.asList(
                "chamberWaterVol", "chamberGasVol", "waterFlowRate", "gasFlowRate", "venting", "drying"
        )), sampler.invalidFieldNames());

        sampler.setChamberWaterVol(CHAMBER_WATER_VOLUME);
        assertEquals(new HashSet(Arrays.asList(
                "chamberGasVol", "waterFlowRate", "gasFlowRate", "venting", "drying"
        )), sampler.invalidFieldNames());

        sampler.setChamberGasVol(CHAMBER_GAS_VOLUME);
        assertEquals(new HashSet(Arrays.asList(
                "waterFlowRate", "gasFlowRate", "venting", "drying"
        )), sampler.invalidFieldNames());

        sampler.setWaterFlowRate(WATER_FLOW_RATE);
        assertEquals(new HashSet(Arrays.asList("gasFlowRate", "venting", "drying")), sampler.invalidFieldNames());

        sampler.setGasFlowRate(GAS_FLOW_RATE);
        assertEquals(new HashSet(Arrays.asList("venting", "drying")), sampler.invalidFieldNames());

        sampler.setVenting(VENTING);
        assertEquals(new HashSet(Arrays.asList("drying")), sampler.invalidFieldNames());

        sampler.setDrying(DRYING);
        assertEquals(new HashSet(Arrays.asList()), sampler.invalidFieldNames());
    }

    @Test
    public void testGetSetEquilibratorType() {
        Equilibrator sampler = new Equilibrator();
        assertEquals(EMPTY_STRING, sampler.getEquilibratorType());
        sampler.setEquilibratorType(EQUILIBRATOR_TYPE);
        assertEquals(EQUILIBRATOR_TYPE, sampler.getEquilibratorType());
        assertEquals(EMPTY_NAMELIST, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getCalibration());
        assertEquals(EMPTY_STRING, sampler.getLocation());
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
        assertEquals(EMPTY_NAMELIST, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getCalibration());
        assertEquals(EMPTY_STRING, sampler.getLocation());
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
        assertEquals(EMPTY_NAMELIST, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getCalibration());
        assertEquals(EMPTY_STRING, sampler.getLocation());
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
        assertEquals(EMPTY_NAMELIST, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getCalibration());
        assertEquals(EMPTY_STRING, sampler.getLocation());
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
        assertEquals(EMPTY_NAMELIST, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getCalibration());
        assertEquals(EMPTY_STRING, sampler.getLocation());
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
        assertEquals(EMPTY_NAMELIST, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getCalibration());
        assertEquals(EMPTY_STRING, sampler.getLocation());
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
        assertEquals(EMPTY_NAMELIST, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getCalibration());
        assertEquals(EMPTY_STRING, sampler.getLocation());
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
    public void testGetSetDrying() {
        Equilibrator sampler = new Equilibrator();
        assertEquals(EMPTY_STRING, sampler.getDrying());
        sampler.setDrying(DRYING);
        assertEquals(DRYING, sampler.getDrying());
        assertEquals(EMPTY_STRING, sampler.getVenting());
        assertEquals(EMPTY_STRING, sampler.getGasFlowRate());
        assertEquals(EMPTY_STRING, sampler.getWaterFlowRate());
        assertEquals(EMPTY_STRING, sampler.getChamberGasVol());
        assertEquals(EMPTY_STRING, sampler.getChamberWaterVol());
        assertEquals(EMPTY_STRING, sampler.getChamberVol());
        assertEquals(EMPTY_STRING, sampler.getEquilibratorType());
        assertEquals(EMPTY_NAMELIST, sampler.getAddnInfo());
        assertEquals(EMPTY_STRING, sampler.getCalibration());
        assertEquals(EMPTY_STRING, sampler.getLocation());
        assertEquals(EMPTY_STRING, sampler.getModel());
        assertEquals(EMPTY_STRING, sampler.getManufacturer());
        assertEquals(EMPTY_STRING, sampler.getId());
        assertEquals(EMPTY_STRING, sampler.getName());
        sampler.setDrying(null);
        assertEquals(EMPTY_STRING, sampler.getDrying());
        sampler.setDrying("\t");
        assertEquals(EMPTY_STRING, sampler.getDrying());
    }

    @Test
    public void testClone() {
        Equilibrator sampler = new Equilibrator();
        Equilibrator dup = sampler.clone();
        assertEquals(sampler, dup);
        assertNotSame(sampler, dup);

        sampler.setName(NAME);
        sampler.setId(ID);
        sampler.setManufacturer(MANUFACTURER);
        sampler.setModel(MODEL);
        sampler.setLocation(LOCATION);
        sampler.setCalibration(CALIBRATION);
        sampler.setAddnInfo(ADDN_INFO);
        sampler.setEquilibratorType(EQUILIBRATOR_TYPE);
        sampler.setChamberVol(CHAMBER_VOLUME);
        sampler.setChamberWaterVol(CHAMBER_WATER_VOLUME);
        sampler.setChamberGasVol(CHAMBER_GAS_VOLUME);
        sampler.setWaterFlowRate(WATER_FLOW_RATE);
        sampler.setGasFlowRate(GAS_FLOW_RATE);
        sampler.setVenting(VENTING);
        sampler.setDrying(DRYING);
        assertNotEquals(sampler, dup);

        dup = sampler.clone();
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

        first.setLocation(LOCATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLocation(LOCATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setCalibration(CALIBRATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCalibration(CALIBRATION);
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

        first.setDrying(DRYING);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setDrying(DRYING);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}

