package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.person.Person;
import gov.noaa.pmel.sdimetadata.variable.MethodType;
import gov.noaa.pmel.sdimetadata.variable.Variable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class VariableTest {
    private static final String EMPTY_STRING = "";
    private static final double DELTA = 1.0E-6;
    private static final String COL_NAME = "SST_C";
    private static final String FULL_NAME = "Sea surface temperature";
    private static final String UNIT = "degrees Celsius";
    private static final String OBSERVE_TYPE = "Surface Underway";
    private static final MethodType MEASURE_METHOD = MethodType.MEASURED_INSITU;
    private static final String METHOD_DESCRIPTION = "Directly measured";
    private static final String METHOD_REFERENCE = "No reference";
    private static final String SAMPLING_LOCATION = "~5 m below water line";
    private static final String POISON = "Cyanide";
    private static final String SAMPLER_NAME = "No sampler";
    private static final String SENSOR_NAME = "Ship's SST sensor";
    private static final double UNCERTAINTY = 0.01;
    private static final String UNCERTAINTY_UNIT = "deg C";
    private static final String FLAG_TYPE = "WOCE 2-4";
    private static final Person RESEARCHER = new Person("Smith", "John", "D.Z.", "PI-23423", "PIRecords", "NOAA/PMEL");
    private static final ArrayList<String> ADDN_INFO = new ArrayList<String>(Arrays.asList(
            "Some sort of information",
            "Another bit of information"
    ));

    @Test
    public void testGetSetColName() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getColName());
        var.setColName(COL_NAME);
        assertEquals(COL_NAME, var.getColName());
        var.setColName(null);
        assertEquals(EMPTY_STRING, var.getColName());
        var.setColName("\t");
        assertEquals(EMPTY_STRING, var.getColName());
    }

    @Test
    public void testGetSetFullName() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getFullName());
        var.setFullName(FULL_NAME);
        assertEquals(FULL_NAME, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setFullName(null);
        assertEquals(EMPTY_STRING, var.getFullName());
        var.setFullName("\t");
        assertEquals(EMPTY_STRING, var.getFullName());
    }

    @Test
    public void testGetSetUnit() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getUnit());
        var.setUnit(UNIT);
        assertEquals(UNIT, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setUnit(null);
        assertEquals(EMPTY_STRING, var.getUnit());
        var.setUnit("\t");
        assertEquals(EMPTY_STRING, var.getUnit());
    }

    @Test
    public void testGetSetObserveType() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getObserveType());
        var.setObserveType(OBSERVE_TYPE);
        assertEquals(OBSERVE_TYPE, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setObserveType(null);
        assertEquals(EMPTY_STRING, var.getObserveType());
        var.setObserveType("\t");
        assertEquals(EMPTY_STRING, var.getObserveType());
    }

    @Test
    public void testGetSetMeasureMethod() {
        Variable var = new Variable();
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        var.setMeasureMethod(MEASURE_METHOD);
        assertEquals(MEASURE_METHOD, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setMeasureMethod(null);
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
    }

    @Test
    public void testGetSetMethodDescription() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        var.setMethodDescription(METHOD_DESCRIPTION);
        assertEquals(METHOD_DESCRIPTION, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setMethodDescription(null);
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        var.setMethodDescription("\t");
        assertEquals(EMPTY_STRING, var.getMethodDescription());
    }

    @Test
    public void testGetSetMethodReference() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getMethodReference());
        var.setMethodReference(METHOD_REFERENCE);
        assertEquals(METHOD_REFERENCE, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setMethodReference(null);
        assertEquals(EMPTY_STRING, var.getMethodReference());
        var.setMethodReference("\t");
        assertEquals(EMPTY_STRING, var.getMethodReference());
    }

    @Test
    public void testGetSetSamplingLocation() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        var.setSamplingLocation(SAMPLING_LOCATION);
        assertEquals(SAMPLING_LOCATION, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setSamplingLocation(null);
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        var.setSamplingLocation("\t");
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
    }

    @Test
    public void testGetSetPoison() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getPoison());
        var.setPoison(POISON);
        assertEquals(POISON, var.getPoison());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setPoison(null);
        assertEquals(EMPTY_STRING, var.getPoison());
        var.setPoison("\t");
        assertEquals(EMPTY_STRING, var.getPoison());
    }

    @Test
    public void testGetSetSamplerName() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getSamplerName());
        var.setSamplerName(SAMPLER_NAME);
        assertEquals(SAMPLER_NAME, var.getSamplerName());
        assertEquals(EMPTY_STRING, var.getPoison());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setSamplerName(null);
        assertEquals(EMPTY_STRING, var.getSamplerName());
        var.setSamplerName("\t");
        assertEquals(EMPTY_STRING, var.getSamplerName());
    }

    @Test
    public void testGetSetSensorName() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getSensorName());
        var.setSensorName(SENSOR_NAME);
        assertEquals(SENSOR_NAME, var.getSensorName());
        assertEquals(EMPTY_STRING, var.getSamplerName());
        assertEquals(EMPTY_STRING, var.getPoison());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setSensorName(null);
        assertEquals(EMPTY_STRING, var.getSensorName());
        var.setSensorName("\t");
        assertEquals(EMPTY_STRING, var.getSensorName());
    }

    @Test
    public void testGetSetUncertainty() {
        Variable var = new Variable();
        assertTrue(var.getUncertainty().isNaN());
        var.setUncertainty(UNCERTAINTY);
        assertEquals(UNCERTAINTY, var.getUncertainty(), DELTA);
        assertEquals(EMPTY_STRING, var.getSensorName());
        assertEquals(EMPTY_STRING, var.getSamplerName());
        assertEquals(EMPTY_STRING, var.getPoison());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setUncertainty(null);
        assertTrue(var.getUncertainty().isNaN());
        var.setUncertainty(Double.NaN);
        assertTrue(var.getUncertainty().isNaN());
        try {
            var.setUncertainty(0.0);
            fail("calling setUncertainty with zero succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setUncertainty(-0.001);
            fail("calling setUncertainty with a negative number succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setUncertainty(Double.NEGATIVE_INFINITY);
            fail("calling setUncertainty with negative infinity succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setUncertainty(Double.POSITIVE_INFINITY);
            fail("calling setUncertainty with positive infinity succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetUncertaintyUnit() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getUncertaintyUnit());
        var.setUncertaintyUnit(UNCERTAINTY_UNIT);
        assertEquals(UNCERTAINTY_UNIT, var.getUncertaintyUnit());
        assertTrue(var.getUncertainty().isNaN());
        assertEquals(EMPTY_STRING, var.getSensorName());
        assertEquals(EMPTY_STRING, var.getSamplerName());
        assertEquals(EMPTY_STRING, var.getPoison());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setUncertaintyUnit(null);
        assertEquals(EMPTY_STRING, var.getUncertaintyUnit());
        var.setUncertaintyUnit("\t");
        assertEquals(EMPTY_STRING, var.getUncertaintyUnit());
    }

    @Test
    public void testGetSetFlagType() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getFlagType());
        var.setFlagType(FLAG_TYPE);
        assertEquals(FLAG_TYPE, var.getFlagType());
        assertEquals(EMPTY_STRING, var.getUncertaintyUnit());
        assertTrue(var.getUncertainty().isNaN());
        assertEquals(EMPTY_STRING, var.getSensorName());
        assertEquals(EMPTY_STRING, var.getSamplerName());
        assertEquals(EMPTY_STRING, var.getPoison());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setFlagType(null);
        assertEquals(EMPTY_STRING, var.getFlagType());
        var.setFlagType("\t");
        assertEquals(EMPTY_STRING, var.getFlagType());
    }

    @Test
    public void testGetSetResearcher() {
        Variable var = new Variable();
        assertEquals(new Person(), var.getResearcher());
        var.setResearcher(RESEARCHER);
        Person researcher = var.getResearcher();
        assertEquals(RESEARCHER, researcher);
        assertNotSame(RESEARCHER, researcher);
        assertNotSame(researcher, var.getResearcher());
        assertEquals(EMPTY_STRING, var.getFlagType());
        assertEquals(EMPTY_STRING, var.getUncertaintyUnit());
        assertTrue(var.getUncertainty().isNaN());
        assertEquals(EMPTY_STRING, var.getSensorName());
        assertEquals(EMPTY_STRING, var.getSamplerName());
        assertEquals(EMPTY_STRING, var.getPoison());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setResearcher(null);
        assertEquals(new Person(), var.getResearcher());
    }

    @Test
    public void testGetSetAddnInfo() {
        Variable var = new Variable();
        assertEquals(0, var.getAddnInfo().size());
        var.setAddnInfo(ADDN_INFO);
        ArrayList<String> addnInfo = var.getAddnInfo();
        assertEquals(ADDN_INFO, addnInfo);
        assertNotSame(ADDN_INFO, addnInfo);
        assertNotSame(addnInfo, var.getAddnInfo());
        assertEquals(new Person(), var.getResearcher());
        assertEquals(EMPTY_STRING, var.getFlagType());
        assertEquals(EMPTY_STRING, var.getUncertaintyUnit());
        assertTrue(var.getUncertainty().isNaN());
        assertEquals(EMPTY_STRING, var.getSensorName());
        assertEquals(EMPTY_STRING, var.getSamplerName());
        assertEquals(EMPTY_STRING, var.getPoison());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setAddnInfo(null);
        assertEquals(0, var.getAddnInfo().size());
        try {
            var.setAddnInfo(Arrays.asList("something", null, "else"));
            fail("calling setAddnInfo with a null string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setAddnInfo(Arrays.asList("something", "\n", "else"));
            fail("calling setAddnInfo with a blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testIsValid() {
        Variable var = new Variable();
        assertFalse(var.isValid());
        var.setColName(COL_NAME);
        var.setFullName(FULL_NAME);
        var.setUnit(UNIT);
        var.setObserveType(OBSERVE_TYPE);
        var.setSamplingLocation(SAMPLING_LOCATION);
        var.setUncertainty(UNCERTAINTY);
        var.setUncertaintyUnit(UNCERTAINTY_UNIT);

        var.setMeasureMethod(MethodType.MEASURED_INSITU);
        assertFalse(var.isValid());
        var.setSamplerName(SAMPLER_NAME);
        assertTrue(var.isValid());
        var.setSamplerName(null);
        var.setSensorName(SENSOR_NAME);
        assertTrue(var.isValid());
        var.setSensorName(null);

        var.setMeasureMethod(MethodType.COMPUTED);
        assertFalse(var.isValid());
        var.setMethodDescription(METHOD_DESCRIPTION);
        assertTrue(var.isValid());
    }

    @Test
    public void testClone() {
        Variable var = new Variable();
        Variable dup = var.clone();
        assertEquals(var, dup);
        assertNotSame(var, dup);

        var.setColName(COL_NAME);
        var.setFullName(FULL_NAME);
        var.setUnit(UNIT);
        var.setObserveType(OBSERVE_TYPE);
        var.setMeasureMethod(MEASURE_METHOD);
        var.setMethodDescription(METHOD_DESCRIPTION);
        var.setMethodReference(METHOD_REFERENCE);
        var.setSamplingLocation(SAMPLING_LOCATION);
        var.setPoison(POISON);
        var.setSamplerName(SAMPLER_NAME);
        var.setSensorName(SENSOR_NAME);
        var.setUncertainty(UNCERTAINTY);
        var.setUncertaintyUnit(UNCERTAINTY_UNIT);
        var.setFlagType(FLAG_TYPE);
        var.setResearcher(RESEARCHER);
        var.setAddnInfo(ADDN_INFO);
        assertNotEquals(var, dup);

        dup = var.clone();
        assertEquals(var, dup);
        assertNotSame(var, dup);
        assertNotSame(var.getResearcher(), dup.getResearcher());
        assertNotSame(var.getAddnInfo(), dup.getAddnInfo());
    }

    @Test
    public void testHashCodeEquals() {
        Variable first = new Variable();
        assertFalse(first.equals(null));
        assertFalse(first.equals(FULL_NAME));

        Variable second = new Variable();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setColName(COL_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setColName(COL_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFullName(FULL_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFullName(FULL_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setUnit(UNIT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setUnit(UNIT);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setObserveType(OBSERVE_TYPE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setObserveType(OBSERVE_TYPE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setMeasureMethod(MEASURE_METHOD);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMeasureMethod(MEASURE_METHOD);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setMethodDescription(METHOD_DESCRIPTION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMethodDescription(METHOD_DESCRIPTION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setMethodReference(METHOD_REFERENCE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMethodReference(METHOD_REFERENCE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSamplingLocation(SAMPLING_LOCATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSamplingLocation(SAMPLING_LOCATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPoison(POISON);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPoison(POISON);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSamplerName(SAMPLER_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSamplerName(SAMPLER_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSensorName(SENSOR_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSensorName(SENSOR_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setUncertainty(UNCERTAINTY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setUncertainty(UNCERTAINTY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setUncertaintyUnit(UNCERTAINTY_UNIT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setUncertaintyUnit(UNCERTAINTY_UNIT);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFlagType(FLAG_TYPE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFlagType(FLAG_TYPE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setResearcher(RESEARCHER);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setResearcher(RESEARCHER);
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

