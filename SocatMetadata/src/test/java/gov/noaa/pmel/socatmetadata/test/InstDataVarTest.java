package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import gov.noaa.pmel.socatmetadata.shared.person.Person;
import gov.noaa.pmel.socatmetadata.shared.variable.InstDataVar;
import gov.noaa.pmel.socatmetadata.shared.variable.MethodType;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;
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

public class InstDataVarTest {

    private static final String EMPTY_STRING = "";
    private static final NumericString EMPTY_NUMSTR = new NumericString();
    private static final ArrayList<String> EMPTY_ARRAYLIST = new ArrayList<String>();
    private static final HashSet<String> EMPTY_HASHSET = new HashSet<String>();
    private static final Person EMPTY_PERSON = new Person();

    private static final String COL_NAME = "SST_C";
    private static final String FULL_NAME = "Sea surface temperature";
    private static final String VAR_UNIT = "degrees Celsius";
    private static final String MISSING_VALUE = "-999";
    private static final String FLAG_COL_NAME = "WOCE SST";
    private static final NumericString ACCURACY = new NumericString("0.01", "deg C");
    private static final NumericString PRECISION = new NumericString("0.001", "deg C");
    private static final ArrayList<String> ADDN_INFO = new ArrayList<String>(Arrays.asList(
            "Some sort of information",
            "Another bit of information"
    ));

    private static final String OBSERVE_TYPE = "Surface Underway";
    private static final MethodType MEASURE_METHOD = MethodType.MEASURED_INSITU;
    private static final String METHOD_DESCRIPTION = "Directly measured";
    private static final String METHOD_REFERENCE = "a very old reference";
    private static final String MANIPULATION_DESCRIPTION = "not a manipulation variable";
    private static final String SAMPLING_LOCATION = "Bow";
    private static final String SAMPLING_ELEVATION = "~5 m below water line";
    private static final String STORAGE_METHOD = "Does not apply";
    private static final String DURATION = "Immediate measurements";
    private static final String MEASURE_TEMPERATURE = "20 deg C";
    private static final String REPLICATION_INFO = "Duplicate sampling was performed";
    private static final Person RESEARCHER = new Person("Smith", "John", "D.Z.", "PI-23423", "PIRecords", "NOAA/PMEL");
    private static final HashSet<String> INSTRUMENT_NAMES = new HashSet<String>(Arrays.asList("Ship's SST sensor"));

    @Test
    public void testGetSetColName() {
        InstDataVar var = new InstDataVar();
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
        InstDataVar var = new InstDataVar();
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
        InstDataVar var = new InstDataVar();
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
    public void testGetSetMissVal() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getMissVal());
        var.setMissVal(MISSING_VALUE);
        assertEquals(MISSING_VALUE, var.getMissVal());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setMissVal(null);
        assertEquals(EMPTY_STRING, var.getMissVal());
        var.setMissVal("\t");
        assertEquals(EMPTY_STRING, var.getMissVal());
    }

    @Test
    public void testGetSetFlagColName() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getFlagColName());
        var.setFlagColName(FLAG_COL_NAME);
        assertEquals(FLAG_COL_NAME, var.getFlagColName());
        assertEquals(EMPTY_STRING, var.getMissVal());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setFlagColName(null);
        assertEquals(EMPTY_STRING, var.getFlagColName());
        var.setFlagColName("\t");
        assertEquals(EMPTY_STRING, var.getFlagColName());
    }

    @Test
    public void testGetSetAccuracy() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_NUMSTR, var.getAccuracy());
        var.setAccuracy(ACCURACY);
        NumericString numstr = var.getAccuracy();
        assertEquals(ACCURACY, numstr);
        assertNotSame(ACCURACY, numstr);
        assertNotSame(numstr, var.getAccuracy());
        assertEquals(EMPTY_STRING, var.getFlagColName());
        assertEquals(EMPTY_STRING, var.getMissVal());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setAccuracy(null);
        assertEquals(EMPTY_NUMSTR, var.getAccuracy());
        var.setAccuracy(EMPTY_NUMSTR);
        assertEquals(EMPTY_NUMSTR, var.getAccuracy());
        try {
            var.setAccuracy(new NumericString("0.0", VAR_UNIT));
            fail("calling setAccuracy with a zero string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setAccuracy(new NumericString("-0.001", VAR_UNIT));
            fail("calling setAccuracy with a negative number string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetPrecision() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_NUMSTR, var.getPrecision());
        var.setPrecision(PRECISION);
        NumericString numstr = var.getPrecision();
        assertEquals(PRECISION, numstr);
        assertNotSame(PRECISION, numstr);
        assertNotSame(numstr, var.getPrecision());
        assertEquals(EMPTY_NUMSTR, var.getAccuracy());
        assertEquals(EMPTY_STRING, var.getFlagColName());
        assertEquals(EMPTY_STRING, var.getMissVal());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setPrecision(null);
        assertEquals(EMPTY_NUMSTR, var.getPrecision());
        var.setPrecision(EMPTY_NUMSTR);
        assertEquals(EMPTY_NUMSTR, var.getPrecision());
        try {
            var.setPrecision(new NumericString("0.0", VAR_UNIT));
            fail("calling setPrecision with a zero string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setPrecision(new NumericString("-0.001", VAR_UNIT));
            fail("calling setPrecision with a negative number string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetAddnInfo() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_ARRAYLIST, var.getAddnInfo());
        var.setAddnInfo(ADDN_INFO);
        ArrayList<String> addnInfo = var.getAddnInfo();
        assertEquals(ADDN_INFO, addnInfo);
        assertNotSame(ADDN_INFO, addnInfo);
        assertNotSame(addnInfo, var.getAddnInfo());
        assertEquals(EMPTY_NUMSTR, var.getPrecision());
        assertEquals(EMPTY_NUMSTR, var.getAccuracy());
        assertEquals(EMPTY_STRING, var.getFlagColName());
        assertEquals(EMPTY_STRING, var.getMissVal());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setAddnInfo(null);
        assertEquals(EMPTY_ARRAYLIST, var.getAddnInfo());
        var.setAddnInfo(EMPTY_HASHSET);
        assertEquals(EMPTY_ARRAYLIST, var.getAddnInfo());
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
    public void testGetSetObserveType() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getObserveType());
        var.setObserveType(OBSERVE_TYPE);
        assertEquals(OBSERVE_TYPE, var.getObserveType());
        assertEquals(EMPTY_ARRAYLIST, var.getAddnInfo());
        assertEquals(EMPTY_NUMSTR, var.getPrecision());
        assertEquals(EMPTY_NUMSTR, var.getAccuracy());
        assertEquals(EMPTY_STRING, var.getFlagColName());
        assertEquals(EMPTY_STRING, var.getMissVal());
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
        InstDataVar var = new InstDataVar();
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
        var.setMeasureMethod(MEASURE_METHOD);
        assertEquals(MEASURE_METHOD, var.getMeasureMethod());
        assertEquals(EMPTY_STRING, var.getObserveType());
        assertEquals(EMPTY_ARRAYLIST, var.getAddnInfo());
        assertEquals(EMPTY_NUMSTR, var.getPrecision());
        assertEquals(EMPTY_NUMSTR, var.getAccuracy());
        assertEquals(EMPTY_STRING, var.getFlagColName());
        assertEquals(EMPTY_STRING, var.getMissVal());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setMeasureMethod(null);
        assertEquals(MethodType.UNSPECIFIED, var.getMeasureMethod());
    }

    @Test
    public void testGetSetMethodDescription() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        var.setMethodDescription(METHOD_DESCRIPTION);
        assertEquals(METHOD_DESCRIPTION, var.getMethodDescription());
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
        var.setMethodDescription(null);
        assertEquals(EMPTY_STRING, var.getMethodDescription());
        var.setMethodDescription("\t");
        assertEquals(EMPTY_STRING, var.getMethodDescription());
    }

    @Test
    public void testGetSetMethodReference() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getMethodReference());
        var.setMethodReference(METHOD_REFERENCE);
        assertEquals(METHOD_REFERENCE, var.getMethodReference());
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
        var.setMethodReference(null);
        assertEquals(EMPTY_STRING, var.getMethodReference());
        var.setMethodReference("\t");
        assertEquals(EMPTY_STRING, var.getMethodReference());
    }

    @Test
    public void testGetSetManipulationDescription() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getManipulationDescription());
        var.setManipulationDescription(MANIPULATION_DESCRIPTION);
        assertEquals(MANIPULATION_DESCRIPTION, var.getManipulationDescription());
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
        var.setManipulationDescription(null);
        assertEquals(EMPTY_STRING, var.getManipulationDescription());
        var.setManipulationDescription("\t");
        assertEquals(EMPTY_STRING, var.getManipulationDescription());
    }

    @Test
    public void testGetSetSamplingLocation() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        var.setSamplingLocation(SAMPLING_LOCATION);
        assertEquals(SAMPLING_LOCATION, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getManipulationDescription());
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
        var.setSamplingLocation(null);
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        var.setSamplingLocation("\t");
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
    }

    @Test
    public void testGetSetSamplingElevation() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        var.setSamplingElevation(SAMPLING_ELEVATION);
        assertEquals(SAMPLING_ELEVATION, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getManipulationDescription());
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
        var.setSamplingElevation(null);
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        var.setSamplingElevation("\t");
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
    }

    @Test
    public void testGetSetStorageMethod() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getStorageMethod());
        var.setStorageMethod(STORAGE_METHOD);
        assertEquals(STORAGE_METHOD, var.getStorageMethod());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getManipulationDescription());
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
        var.setStorageMethod(null);
        assertEquals(EMPTY_STRING, var.getStorageMethod());
        var.setStorageMethod("\t");
        assertEquals(EMPTY_STRING, var.getStorageMethod());
    }

    @Test
    public void testGetSetDuration() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getDuration());
        var.setDuration(DURATION);
        assertEquals(DURATION, var.getDuration());
        assertEquals(EMPTY_STRING, var.getStorageMethod());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getManipulationDescription());
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
        var.setDuration(null);
        assertEquals(EMPTY_STRING, var.getDuration());
        var.setDuration("\t");
        assertEquals(EMPTY_STRING, var.getDuration());
    }

    @Test
    public void testGetSetMeasureTemperature() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getAnalysisTemperature());
        var.setAnalysisTemperature(MEASURE_TEMPERATURE);
        assertEquals(MEASURE_TEMPERATURE, var.getAnalysisTemperature());
        assertEquals(EMPTY_HASHSET, var.getInstrumentNames());
        assertEquals(EMPTY_STRING, var.getDuration());
        assertEquals(EMPTY_STRING, var.getStorageMethod());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getManipulationDescription());
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
        var.setAnalysisTemperature(null);
        assertEquals(EMPTY_STRING, var.getAnalysisTemperature());
        var.setAnalysisTemperature("\t");
        assertEquals(EMPTY_STRING, var.getAnalysisTemperature());
    }

    @Test
    public void testGetSetReplication() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_STRING, var.getReplication());
        var.setReplication(REPLICATION_INFO);
        assertEquals(REPLICATION_INFO, var.getReplication());
        assertEquals(EMPTY_STRING, var.getAnalysisTemperature());
        assertEquals(EMPTY_STRING, var.getDuration());
        assertEquals(EMPTY_STRING, var.getStorageMethod());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getManipulationDescription());
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
        var.setReplication(null);
        assertEquals(EMPTY_STRING, var.getReplication());
        var.setReplication("\t");
        assertEquals(EMPTY_STRING, var.getReplication());
    }

    @Test
    public void testGetSetResearcher() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_PERSON, var.getResearcher());
        var.setResearcher(RESEARCHER);
        Person researcher = var.getResearcher();
        assertEquals(RESEARCHER, researcher);
        assertNotSame(RESEARCHER, researcher);
        assertNotSame(researcher, var.getResearcher());
        assertEquals(EMPTY_STRING, var.getReplication());
        assertEquals(EMPTY_STRING, var.getAnalysisTemperature());
        assertEquals(EMPTY_STRING, var.getDuration());
        assertEquals(EMPTY_STRING, var.getStorageMethod());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getManipulationDescription());
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
        var.setResearcher(null);
        assertEquals(EMPTY_PERSON, var.getResearcher());
    }

    @Test
    public void testGetSetInstrumentNames() {
        InstDataVar var = new InstDataVar();
        assertEquals(EMPTY_HASHSET, var.getInstrumentNames());
        var.setInstrumentNames(INSTRUMENT_NAMES);
        HashSet<String> names = var.getInstrumentNames();
        assertEquals(INSTRUMENT_NAMES, names);
        assertNotSame(INSTRUMENT_NAMES, names);
        assertNotSame(names, var.getInstrumentNames());
        assertEquals(EMPTY_PERSON, var.getResearcher());
        assertEquals(EMPTY_STRING, var.getReplication());
        assertEquals(EMPTY_STRING, var.getAnalysisTemperature());
        assertEquals(EMPTY_STRING, var.getDuration());
        assertEquals(EMPTY_STRING, var.getStorageMethod());
        assertEquals(EMPTY_STRING, var.getSamplingElevation());
        assertEquals(EMPTY_STRING, var.getSamplingLocation());
        assertEquals(EMPTY_STRING, var.getManipulationDescription());
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
        var.setInstrumentNames(null);
        assertEquals(EMPTY_HASHSET, var.getInstrumentNames());
        var.setInstrumentNames(EMPTY_ARRAYLIST);
        assertEquals(EMPTY_HASHSET, var.getInstrumentNames());
        try {
            var.setInstrumentNames(Arrays.asList("something", null, "else"));
            fail("calling setInstrumentNames with a null string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            var.setInstrumentNames(Arrays.asList("something", "\n", "else"));
            fail("calling setInstrumentNames with a blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testDataVarVariable() {
        Variable var = new Variable();
        var.setColName(COL_NAME);
        var.setFullName(FULL_NAME);
        var.setVarUnit(VAR_UNIT);
        var.setMissVal(MISSING_VALUE);
        var.setFlagColName(FLAG_COL_NAME);
        var.setAccuracy(ACCURACY);
        var.setPrecision(PRECISION);
        var.setAddnInfo(ADDN_INFO);

        InstDataVar instDataVar = new InstDataVar(var);
        assertEquals(EMPTY_HASHSET, instDataVar.getInstrumentNames());
        assertEquals(EMPTY_PERSON, instDataVar.getResearcher());
        assertEquals(EMPTY_STRING, instDataVar.getReplication());
        assertEquals(EMPTY_STRING, instDataVar.getAnalysisTemperature());
        assertEquals(EMPTY_STRING, instDataVar.getDuration());
        assertEquals(EMPTY_STRING, instDataVar.getStorageMethod());
        assertEquals(EMPTY_STRING, instDataVar.getSamplingElevation());
        assertEquals(EMPTY_STRING, instDataVar.getSamplingLocation());
        assertEquals(EMPTY_STRING, instDataVar.getManipulationDescription());
        assertEquals(EMPTY_STRING, instDataVar.getMethodReference());
        assertEquals(EMPTY_STRING, instDataVar.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, instDataVar.getMeasureMethod());
        assertEquals(EMPTY_STRING, instDataVar.getObserveType());
        assertEquals(ADDN_INFO, instDataVar.getAddnInfo());
        assertEquals(PRECISION, instDataVar.getPrecision());
        assertEquals(ACCURACY, instDataVar.getAccuracy());
        assertEquals(FLAG_COL_NAME, instDataVar.getFlagColName());
        assertEquals(MISSING_VALUE, instDataVar.getMissVal());
        assertEquals(VAR_UNIT, instDataVar.getVarUnit());
        assertEquals(FULL_NAME, instDataVar.getFullName());
        assertEquals(COL_NAME, instDataVar.getColName());

        instDataVar = new InstDataVar(null);
        assertEquals(new InstDataVar(), instDataVar);
    }

    @Test
    public void testInvalidFieldNames() {
        InstDataVar var = new InstDataVar();
        assertEquals(new HashSet<String>(Arrays.asList("colName", "fullName",
                "observeType", "accuracy", "measureMethod")), var.invalidFieldNames());
        var.setColName(COL_NAME);
        var.setFullName(FULL_NAME);
        assertEquals(new HashSet<String>(Arrays.asList("observeType", "accuracy", "measureMethod")),
                var.invalidFieldNames());
        var.setObserveType(OBSERVE_TYPE);
        var.setAccuracy(ACCURACY);
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
    public void testDuplicate() {
        InstDataVar var = new InstDataVar();
        InstDataVar dup = (InstDataVar) (var.duplicate(null));
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
        var.setManipulationDescription(MANIPULATION_DESCRIPTION);
        var.setSamplingLocation(SAMPLING_LOCATION);
        var.setSamplingElevation(SAMPLING_ELEVATION);
        var.setDuration(DURATION);
        var.setStorageMethod(STORAGE_METHOD);
        var.setAnalysisTemperature(MEASURE_TEMPERATURE);
        var.setReplication(REPLICATION_INFO);
        var.setResearcher(RESEARCHER);
        var.setInstrumentNames(INSTRUMENT_NAMES);
        assertNotEquals(var, dup);

        dup = (InstDataVar) (var.duplicate(null));
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
        InstDataVar first = new InstDataVar();
        assertFalse(first.equals(null));
        assertFalse(first.equals(FULL_NAME));

        InstDataVar second = new InstDataVar();
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

        first.setManipulationDescription(MANIPULATION_DESCRIPTION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setManipulationDescription(MANIPULATION_DESCRIPTION);
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

        first.setDuration(DURATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setDuration(DURATION);
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
    }

}
