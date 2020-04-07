package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.person.Person;
import gov.noaa.pmel.socatmetadata.shared.util.NumericString;
import gov.noaa.pmel.socatmetadata.shared.variable.AquGasConc;
import gov.noaa.pmel.socatmetadata.shared.variable.MethodType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class AquGasConcTest {

    private static final String EMPTY_STRING = "";
    private static final NumericString EMPTY_NUMSTR = new NumericString();
    private static final ArrayList<String> EMPTY_ARRAYLIST = new ArrayList<String>();
    private static final HashSet<String> EMPTY_HASHSET = new HashSet<String>();
    private static final Person EMPTY_PERSON = new Person();

    private static final String COL_NAME = "xCO2water";
    private static final String FULL_NAME = "Mole fraction CO2 in surface sea water";
    private static final String VAR_UNIT = "umol/mol";
    private static final String MISSING_VALUE = "-999";
    private static final String FLAG_COL_NAME = "WOCE xCO2water";
    private static final NumericString ACCURACY = new NumericString("0.01", "umol/mol");
    private static final NumericString PRECISION = new NumericString("0.001", "umol/mol");
    private static final ArrayList<String> ADDN_INFO = new ArrayList<String>(Arrays.asList(
            "Some sort of information",
            "Another bit of information"
    ));

    private static final String OBSERVE_TYPE = "Surface Underway";
    private static final MethodType MEASURE_METHOD = MethodType.MEASURED_INSITU;
    private static final String METHOD_DESCRIPTION = "Directly measured";
    private static final String METHOD_REFERENCE = "a very old reference";
    private static final String SAMPLING_LOCATION = "Bow";
    private static final String SAMPLING_ELEVATION = "~5 m below water line";
    private static final String STORAGE_METHOD = "Does not apply";
    private static final String MEASURE_TEMPERATURE = "20 deg C";
    private static final String REPLICATION_INFO = "Duplicate sampling was performed";
    private static final Person RESEARCHER = new Person("Smith", "John", "D.Z.", "PI-23423", "PIRecords", "NOAA/PMEL");
    private static final ArrayList<String> INSTRUMENT_NAMES = new ArrayList<String>(Arrays.asList(
            "Equilibrator",
            "Equilibrator LICOR"
    ));

    private static final String REPORT_TERMPERATURE = "SST";
    private static final String TEMPERATURE_CORRECTION = "Standard data reduction method";
    private static final String DRYING_METHOD = "Gas stream passes through a thermoelectric condenser (~5 &#176;C) and then through a Perma Pure (Nafion) dryer before reaching the analyzer (90% dry).";
    private static final String WATER_VAPOR_CORRECTION = "Another standard data reduction method";

    @Test
    public void testGetSetReportTemperature() {
        AquGasConc var = new AquGasConc();
        assertEquals(EMPTY_STRING, var.getReportTemperature());
        var.setReportTemperature(REPORT_TERMPERATURE);
        assertEquals(REPORT_TERMPERATURE, var.getReportTemperature());
        assertEquals(EMPTY_STRING, var.getWaterVaporCorrection());
        assertEquals(EMPTY_STRING, var.getDryingMethod());
        assertEquals(EMPTY_HASHSET, var.getInstrumentNames());
        assertEquals(EMPTY_PERSON, var.getResearcher());
        assertEquals(EMPTY_STRING, var.getReplication());
        assertEquals(EMPTY_STRING, var.getAnalysisTemperature());
        assertEquals(EMPTY_STRING, var.getStorageMethod());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_ARRAYLIST, var.getAddnInfo());
        assertEquals(EMPTY_NUMSTR, var.getPrecision());
        assertEquals(EMPTY_NUMSTR, var.getAccuracy());
        assertEquals(EMPTY_STRING, var.getFlagColName());
        assertEquals(EMPTY_STRING, var.getMissVal());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setReportTemperature(null);
        assertEquals(EMPTY_STRING, var.getReportTemperature());
        var.setReportTemperature("\t");
        assertEquals(EMPTY_STRING, var.getReportTemperature());
    }

    @Test
    public void testGetSetTemperatureCorrection() {
        AquGasConc var = new AquGasConc();
        assertEquals(EMPTY_STRING, var.getTemperatureCorrection());
        var.setTemperatureCorrection(TEMPERATURE_CORRECTION);
        assertEquals(TEMPERATURE_CORRECTION, var.getTemperatureCorrection());
        assertEquals(EMPTY_STRING, var.getReportTemperature());
        assertEquals(EMPTY_STRING, var.getWaterVaporCorrection());
        assertEquals(EMPTY_STRING, var.getDryingMethod());
        assertEquals(EMPTY_HASHSET, var.getInstrumentNames());
        assertEquals(EMPTY_PERSON, var.getResearcher());
        assertEquals(EMPTY_STRING, var.getReplication());
        assertEquals(EMPTY_STRING, var.getAnalysisTemperature());
        assertEquals(EMPTY_STRING, var.getStorageMethod());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getMethodReference());
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_ARRAYLIST, var.getAddnInfo());
        assertEquals(EMPTY_NUMSTR, var.getPrecision());
        assertEquals(EMPTY_NUMSTR, var.getAccuracy());
        assertEquals(EMPTY_STRING, var.getFlagColName());
        assertEquals(EMPTY_STRING, var.getMissVal());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setTemperatureCorrection(null);
        assertEquals(EMPTY_STRING, var.getTemperatureCorrection());
        var.setTemperatureCorrection("\t");
        assertEquals(EMPTY_STRING, var.getTemperatureCorrection());
    }

    @Test
    public void testInvalidFieldNames() {
        AquGasConc var = new AquGasConc();
        assertEquals(new HashSet<String>(Arrays.asList("colName", "fullName", "observeType",
                "accuracy", "measureMethod", "reportTemperature")), var.invalidFieldNames());

        var.setColName(COL_NAME);
        assertEquals(new HashSet<String>(Arrays.asList("fullName", "observeType", "accuracy",
                "measureMethod", "reportTemperature")), var.invalidFieldNames());

        var.setFullName(FULL_NAME);
        assertEquals(new HashSet<String>(Arrays.asList("observeType", "accuracy", "measureMethod",
                "reportTemperature")), var.invalidFieldNames());

        var.setObserveType(OBSERVE_TYPE);
        assertEquals(new HashSet<String>(Arrays.asList("accuracy", "measureMethod", "reportTemperature")),
                var.invalidFieldNames());

        var.setAccuracy(ACCURACY);
        assertEquals(new HashSet<String>(Arrays.asList("measureMethod", "reportTemperature")),
                var.invalidFieldNames());

        var.setReportTemperature(REPORT_TERMPERATURE);
        assertEquals(new HashSet<String>(Arrays.asList("measureMethod")), var.invalidFieldNames());

        var.setMeasureMethod(MethodType.MEASURED_INSITU);
        assertEquals(new HashSet<String>(Arrays.asList("instrumentNames")), var.invalidFieldNames());

        var.setInstrumentNames(INSTRUMENT_NAMES);
        assertEquals(EMPTY_HASHSET, var.invalidFieldNames());
        var.setInstrumentNames(null);

        var.setMeasureMethod(MethodType.COMPUTED);
        assertEquals(new HashSet<String>(Arrays.asList("methodDescription")), var.invalidFieldNames());

        var.setMethodDescription(METHOD_DESCRIPTION);
        assertEquals(EMPTY_HASHSET, var.invalidFieldNames());
    }

    @Test
    public void testClone() {
        AquGasConc var = new AquGasConc();
        AquGasConc dup = var.clone();
        assertEquals(var, dup);
        assertNotSame(var, dup);

        var.setColName(COL_NAME);
        var.setFullName(FULL_NAME);
        var.setVarUnit(VAR_UNIT);
        var.setMissVal(MISSING_VALUE);
        var.setFlagColName(FLAG_COL_NAME);
        var.setAccuracy(ACCURACY);
        var.setPrecision(PRECISION);
        var.setAddnInfo(ADDN_INFO);

        var.setObserveType(OBSERVE_TYPE);
        var.setMeasureMethod(MEASURE_METHOD);
        var.setMethodDescription(METHOD_DESCRIPTION);
        var.setMethodReference(METHOD_REFERENCE);
        var.setSamplingLocation(SAMPLING_LOCATION);
        var.setSamplingElevation(SAMPLING_ELEVATION);
        var.setStorageMethod(STORAGE_METHOD);
        var.setAnalysisTemperature(MEASURE_TEMPERATURE);
        var.setReplication(REPLICATION_INFO);
        var.setResearcher(RESEARCHER);
        var.setInstrumentNames(INSTRUMENT_NAMES);

        var.setWaterVaporCorrection(WATER_VAPOR_CORRECTION);
        var.setDryingMethod(DRYING_METHOD);

        var.setReportTemperature(REPORT_TERMPERATURE);
        var.setTemperatureCorrection(TEMPERATURE_CORRECTION);
        assertNotEquals(var, dup);

        dup = var.clone();
        assertEquals(var, dup);
        assertNotSame(var, dup);
        assertNotSame(var.getAccuracy(), dup.getAccuracy());
        assertNotSame(var.getPrecision(), dup.getPrecision());
        assertNotSame(var.getAddnInfo(), dup.getAddnInfo());
        assertNotSame(var.getResearcher(), dup.getResearcher());
        assertNotSame(var.getInstrumentNames(), dup.getInstrumentNames());
    }

    @Test
    public void testHashCodeEquals() {
        AquGasConc first = new AquGasConc();
        assertFalse(first.equals(null));
        assertFalse(first.equals(FULL_NAME));

        AquGasConc second = new AquGasConc();
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

        first.setMissVal(MISSING_VALUE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMissVal(MISSING_VALUE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFlagColName(FLAG_COL_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFlagColName(FLAG_COL_NAME);
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

        first.setAddnInfo(ADDN_INFO);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAddnInfo(ADDN_INFO);
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

        first.setStorageMethod(STORAGE_METHOD);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setStorageMethod(STORAGE_METHOD);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setAnalysisTemperature(MEASURE_TEMPERATURE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAnalysisTemperature(MEASURE_TEMPERATURE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setReplication(REPLICATION_INFO);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setReplication(REPLICATION_INFO);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setResearcher(RESEARCHER);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setResearcher(RESEARCHER);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setInstrumentNames(INSTRUMENT_NAMES);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setInstrumentNames(INSTRUMENT_NAMES);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setDryingMethod(DRYING_METHOD);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setDryingMethod(DRYING_METHOD);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setWaterVaporCorrection(WATER_VAPOR_CORRECTION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setWaterVaporCorrection(WATER_VAPOR_CORRECTION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setReportTemperature(REPORT_TERMPERATURE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setReportTemperature(REPORT_TERMPERATURE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setTemperatureCorrection(TEMPERATURE_CORRECTION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setTemperatureCorrection(TEMPERATURE_CORRECTION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}
