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
    private static final String VAR_UNIT = "degrees Celsius";
    private static final String OBSERVE_TYPE = "Surface Underway";
    private static final MethodType MEASURE_METHOD = MethodType.MEASURED_INSITU;
    private static final String METHOD_DESCRIPTION = "Directly measured";
    private static final String METHOD_REFERENCE = "No reference";
    private static final String SAMPLING_LOCATION = "Bow";
    private static final String SAMPLING_ELEVATION = "~5 m below water line";
    private static final double ACCURACY = 0.01;
    private static final double PRECISION = 0.001;
    private static final String AP_UNIT = "deg C";
    private static final String FLAG_TYPE = "WOCE 2-4";
    private static final Person RESEARCHER = new Person("Smith", "John", "D.Z.", "PI-23423", "PIRecords", "NOAA/PMEL");
    private static final ArrayList<String> SAMPLER_NAMES = new ArrayList<String>(Arrays.asList("Cooling intake"));
    private static final ArrayList<String> ANALYZER_NAMES = new ArrayList<String>(Arrays.asList("Ship's SST sensor"));
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
    public void testGetSetVarUnit() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getVarUnit());
        var.setVarUnit(VAR_UNIT);
        assertEquals(VAR_UNIT, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setVarUnit(null);
        assertEquals(EMPTY_STRING, var.getVarUnit());
        var.setVarUnit("\t");
        assertEquals(EMPTY_STRING, var.getVarUnit());
    }

    @Test
    public void testGetSetObserveType() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getObserveType());
        var.setObserveType(OBSERVE_TYPE);
        assertEquals(OBSERVE_TYPE, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getVarUnit());
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
        assertEquals(EMPTY_STRING, var.getVarUnit());
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
        assertEquals(EMPTY_STRING, var.getVarUnit());
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
        assertEquals(EMPTY_STRING, var.getVarUnit());
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
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setSamplingLocation(null);
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        var.setSamplingLocation("\t");
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
    }

    @Test
    public void testGetSetSamplingElevation() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        var.setSamplingElevation(SAMPLING_ELEVATION);
        assertEquals(SAMPLING_ELEVATION, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setSamplingElevation(null);
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        var.setSamplingElevation("\t");
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
    }

    @Test
    public void testGetSetAccuracy() {
        Variable var = new Variable();
        assertTrue(var.getAccuracy().isNaN());
        var.setAccuracy(ACCURACY);
        assertEquals(ACCURACY, var.getAccuracy(), DELTA);
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setAccuracy(null);
        assertTrue(var.getAccuracy().isNaN());
        var.setAccuracy(Double.NaN);
        assertTrue(var.getAccuracy().isNaN());
        try {
            var.setAccuracy(0.0);
            fail("calling setAccuracy with zero succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setAccuracy(-0.001);
            fail("calling setAccuracy with a negative number succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setAccuracy(Double.NEGATIVE_INFINITY);
            fail("calling setAccuracy with negative infinity succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setAccuracy(Double.POSITIVE_INFINITY);
            fail("calling setAccuracy with positive infinity succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetPrecision() {
        Variable var = new Variable();
        assertTrue(var.getPrecision().isNaN());
        var.setPrecision(PRECISION);
        assertEquals(PRECISION, var.getPrecision(), DELTA);
        assertTrue(var.getAccuracy().isNaN());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setPrecision(null);
        assertTrue(var.getPrecision().isNaN());
        var.setPrecision(Double.NaN);
        assertTrue(var.getPrecision().isNaN());
        try {
            var.setPrecision(0.0);
            fail("calling setPrecision with zero succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setPrecision(-0.001);
            fail("calling setPrecision with a negative number succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setPrecision(Double.NEGATIVE_INFINITY);
            fail("calling setPrecision with negative infinity succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setPrecision(Double.POSITIVE_INFINITY);
            fail("calling setPrecision with positive infinity succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetAPUnit() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getApUnit());
        var.setApUnit(AP_UNIT);
        assertEquals(AP_UNIT, var.getApUnit());
        assertTrue(var.getPrecision().isNaN());
        assertTrue(var.getAccuracy().isNaN());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setApUnit(null);
        assertEquals(EMPTY_STRING, var.getApUnit());
        var.setApUnit("\t");
        assertEquals(EMPTY_STRING, var.getApUnit());
    }

    @Test
    public void testGetSetFlagType() {
        Variable var = new Variable();
        assertEquals(EMPTY_STRING, var.getFlagType());
        var.setFlagType(FLAG_TYPE);
        assertEquals(FLAG_TYPE, var.getFlagType());
        assertEquals(EMPTY_STRING, var.getApUnit());
        assertTrue(var.getPrecision().isNaN());
        assertTrue(var.getAccuracy().isNaN());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getVarUnit());
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
        assertEquals(EMPTY_STRING, var.getApUnit());
        assertTrue(var.getPrecision().isNaN());
        assertTrue(var.getAccuracy().isNaN());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setResearcher(null);
        assertEquals(new Person(), var.getResearcher());
    }

    @Test
    public void testGetSetSamplerNames() {
        Variable var = new Variable();
        assertEquals(0, var.getSamplerNames().size());
        var.setSamplerNames(SAMPLER_NAMES);
        ArrayList<String> names = var.getSamplerNames();
        assertEquals(SAMPLER_NAMES, names);
        assertNotSame(SAMPLER_NAMES, names);
        assertNotSame(names, var.getSamplerNames());
        assertEquals(new Person(), var.getResearcher());
        assertEquals(EMPTY_STRING, var.getFlagType());
        assertEquals(EMPTY_STRING, var.getApUnit());
        assertTrue(var.getPrecision().isNaN());
        assertTrue(var.getAccuracy().isNaN());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setSamplerNames(null);
        assertEquals(0, var.getSamplerNames().size());
        try {
            var.setSamplerNames(Arrays.asList("something", null, "else"));
            fail("calling setSamplerNames with a null string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setSamplerNames(Arrays.asList("something", "\n", "else"));
            fail("calling setSamplerNames with a blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetAnalyzerNames() {
        Variable var = new Variable();
        assertEquals(0, var.getAnalyzerNames().size());
        var.setAnalyzerNames(ANALYZER_NAMES);
        ArrayList<String> names = var.getAnalyzerNames();
        assertEquals(ANALYZER_NAMES, names);
        assertNotSame(ANALYZER_NAMES, names);
        assertNotSame(names, var.getAnalyzerNames());
        assertEquals(0, var.getSamplerNames().size());
        assertEquals(new Person(), var.getResearcher());
        assertEquals(EMPTY_STRING, var.getFlagType());
        assertEquals(EMPTY_STRING, var.getApUnit());
        assertTrue(var.getPrecision().isNaN());
        assertTrue(var.getAccuracy().isNaN());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setAnalyzerNames(null);
        assertEquals(0, var.getAnalyzerNames().size());
        try {
            var.setAnalyzerNames(Arrays.asList("something", null, "else"));
            fail("calling setAnalyzerNames with a null string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setAnalyzerNames(Arrays.asList("something", "\n", "else"));
            fail("calling setAnalyzerNames with a blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
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
        assertEquals(0, var.getAnalyzerNames().size());
        assertEquals(0, var.getSamplerNames().size());
        assertEquals(new Person(), var.getResearcher());
        assertEquals(EMPTY_STRING, var.getFlagType());
        assertEquals(EMPTY_STRING, var.getApUnit());
        assertTrue(var.getPrecision().isNaN());
        assertTrue(var.getAccuracy().isNaN());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_STRING, var.getVarUnit());
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
        var.setObserveType(OBSERVE_TYPE);
        var.setAccuracy(ACCURACY);

        var.setMeasureMethod(MethodType.MEASURED_INSITU);
        assertFalse(var.isValid());
        var.setSamplerNames(SAMPLER_NAMES);
        assertTrue(var.isValid());
        var.setSamplerNames(null);
        var.setAnalyzerNames(ANALYZER_NAMES);
        assertTrue(var.isValid());
        var.setAnalyzerNames(null);

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
        var.setVarUnit(VAR_UNIT);
        var.setObserveType(OBSERVE_TYPE);
        var.setMeasureMethod(MEASURE_METHOD);
        var.setMethodDescription(METHOD_DESCRIPTION);
        var.setMethodReference(METHOD_REFERENCE);
        var.setSamplingLocation(SAMPLING_LOCATION);
        var.setSamplingElevation(SAMPLING_ELEVATION);
        var.setAccuracy(ACCURACY);
        var.setPrecision(PRECISION);
        var.setApUnit(AP_UNIT);
        var.setFlagType(FLAG_TYPE);
        var.setResearcher(RESEARCHER);
        var.setSamplerNames(SAMPLER_NAMES);
        var.setAnalyzerNames(ANALYZER_NAMES);
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

        first.setVarUnit(VAR_UNIT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setVarUnit(VAR_UNIT);
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

        first.setSamplingElevation(SAMPLING_ELEVATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSamplingElevation(SAMPLING_ELEVATION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAccuracy(ACCURACY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAccuracy(ACCURACY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPrecision(PRECISION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPrecision(PRECISION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setApUnit(AP_UNIT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setApUnit(AP_UNIT);
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

        first.setSamplerNames(SAMPLER_NAMES);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSamplerNames(SAMPLER_NAMES);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAnalyzerNames(ANALYZER_NAMES);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAnalyzerNames(ANALYZER_NAMES);
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

