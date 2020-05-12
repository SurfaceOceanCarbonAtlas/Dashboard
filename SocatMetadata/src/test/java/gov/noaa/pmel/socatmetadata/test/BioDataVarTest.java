package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import gov.noaa.pmel.socatmetadata.shared.person.Person;
import gov.noaa.pmel.socatmetadata.shared.variable.BioDataVar;
import gov.noaa.pmel.socatmetadata.shared.variable.InstDataVar;
import gov.noaa.pmel.socatmetadata.shared.variable.MethodType;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

public class BioDataVarTest {

    private static final String EMPTY_STRING = "";
    private static final NumericString EMPTY_NUMSTR = new NumericString();
    private static final ArrayList<String> EMPTY_ARRAYLIST = new ArrayList<String>();
    private static final HashSet<String> EMPTY_HASHSET = new HashSet<String>();
    private static final Person EMPTY_PERSON = new Person();

    private static final String COL_NAME = "Krill_Count";
    private static final String FULL_NAME = "Krill Count";
    private static final String MISSING_VALUE = "-999";
    private static final String FLAG_COL_NAME = "WOCE Krill Count";

    private static final String OBSERVE_TYPE = "Surface Underway";
    private static final MethodType MEASURE_METHOD = MethodType.MEASURED_INSITU;
    private static final String METHOD_DESCRIPTION = "Directly measured";
    private static final String METHOD_REFERENCE = "a very old reference";
    private static final Person RESEARCHER = new Person("Smith", "John", "D.Z.", "PI-23423", "PIRecords", "NOAA/PMEL");
    private static final HashSet<String> ANALYZER_NAMES = new HashSet<String>(Arrays.asList("Video Analyzer"));

    private static final String BIOLOGICAL_SUBJECT = "Krill";
    private static final String SPECIES_ID = "12345";
    private static final String LIFE_STAGE = "Adult";

    @Test
    public void testGetSetBiologicalSubject() {
        BioDataVar var = new BioDataVar();
        assertEquals(EMPTY_STRING, var.getBiologicalSubject());
        var.setBiologicalSubject(BIOLOGICAL_SUBJECT);
        assertEquals(BIOLOGICAL_SUBJECT, var.getBiologicalSubject());
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
        var.setBiologicalSubject(null);
        assertEquals(EMPTY_STRING, var.getBiologicalSubject());
        var.setBiologicalSubject("\t");
        assertEquals(EMPTY_STRING, var.getBiologicalSubject());
    }

    @Test
    public void testGetSetSpeciesId() {
        BioDataVar var = new BioDataVar();
        assertEquals(EMPTY_STRING, var.getSpeciesId());
        var.setSpeciesId(SPECIES_ID);
        assertEquals(SPECIES_ID, var.getSpeciesId());
        assertEquals(EMPTY_STRING, var.getBiologicalSubject());
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
        var.setSpeciesId(null);
        assertEquals(EMPTY_STRING, var.getSpeciesId());
        var.setSpeciesId("\t");
        assertEquals(EMPTY_STRING, var.getSpeciesId());
    }

    @Test
    public void testGetSetLifeStage() {
        BioDataVar var = new BioDataVar();
        assertEquals(EMPTY_STRING, var.getLifeStage());
        var.setLifeStage(LIFE_STAGE);
        assertEquals(LIFE_STAGE, var.getLifeStage());
        assertEquals(EMPTY_STRING, var.getSpeciesId());
        assertEquals(EMPTY_STRING, var.getBiologicalSubject());
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
        var.setLifeStage(null);
        assertEquals(EMPTY_STRING, var.getLifeStage());
        var.setLifeStage("\t");
        assertEquals(EMPTY_STRING, var.getLifeStage());
    }

    @Test
    public void testInvalidFieldNames() {
        BioDataVar var = new BioDataVar();
        assertEquals(new HashSet<String>(Arrays.asList("colName", "fullName",
                "observeType", "measureMethod", "speciesId")), var.invalidFieldNames());
        var.setColName(COL_NAME);
        var.setFullName(FULL_NAME);
        var.setObserveType(OBSERVE_TYPE);
        assertEquals(new HashSet<String>(Arrays.asList("measureMethod", "speciesId")), var.invalidFieldNames());
        var.setSpeciesId(SPECIES_ID);
        assertEquals(new HashSet<String>(Arrays.asList("measureMethod")), var.invalidFieldNames());

        var.setMeasureMethod(MethodType.MEASURED_INSITU);
        assertEquals(new HashSet<String>(Arrays.asList("instrumentNames")), var.invalidFieldNames());
        var.setInstrumentNames(ANALYZER_NAMES);
        assertEquals(EMPTY_HASHSET, var.invalidFieldNames());
        var.setInstrumentNames(null);
        var.setMeasureMethod(MethodType.COMPUTED);
        assertEquals(new HashSet<String>(Arrays.asList("methodDescription")), var.invalidFieldNames());
        var.setMethodDescription(METHOD_DESCRIPTION);
        assertEquals(EMPTY_HASHSET, var.invalidFieldNames());
    }

    @Test
    public void testBioDataVarVariable() {
        Variable var = new Variable();
        var.setColName(COL_NAME);
        var.setFullName(FULL_NAME);
        var.setMissVal(MISSING_VALUE);

        BioDataVar biovar = new BioDataVar(var);
        assertEquals(EMPTY_HASHSET, biovar.getInstrumentNames());
        assertEquals(EMPTY_PERSON, biovar.getResearcher());
        assertEquals(EMPTY_STRING, biovar.getMethodReference());
        assertEquals(EMPTY_STRING, biovar.getMethodDescription());
        assertEquals(MethodType.UNSPECIFIED, biovar.getMeasureMethod());
        assertEquals(EMPTY_STRING, biovar.getObserveType());
        assertEquals(EMPTY_STRING, biovar.getFlagColName());
        assertEquals(MISSING_VALUE, biovar.getMissVal());
        assertEquals(FULL_NAME, biovar.getFullName());
        assertEquals(COL_NAME, biovar.getColName());

        InstDataVar datavar = new InstDataVar(var);
        datavar.setFlagColName(FLAG_COL_NAME);
        datavar.setObserveType(OBSERVE_TYPE);
        datavar.setMeasureMethod(MEASURE_METHOD);
        datavar.setMethodDescription(METHOD_DESCRIPTION);
        datavar.setMethodReference(METHOD_REFERENCE);
        datavar.setResearcher(RESEARCHER);
        datavar.setInstrumentNames(ANALYZER_NAMES);

        biovar = new BioDataVar(datavar);
        assertEquals(ANALYZER_NAMES, biovar.getInstrumentNames());
        assertEquals(RESEARCHER, biovar.getResearcher());
        assertEquals(METHOD_REFERENCE, biovar.getMethodReference());
        assertEquals(METHOD_DESCRIPTION, biovar.getMethodDescription());
        assertEquals(MEASURE_METHOD, biovar.getMeasureMethod());
        assertEquals(OBSERVE_TYPE, biovar.getObserveType());
        assertEquals(FLAG_COL_NAME, biovar.getFlagColName());
        assertEquals(MISSING_VALUE, biovar.getMissVal());
        assertEquals(FULL_NAME, biovar.getFullName());
        assertEquals(COL_NAME, biovar.getColName());

        biovar = new BioDataVar(null);
        assertEquals(new BioDataVar(), biovar);
    }


    @Test
    public void testDuplicate() {
        BioDataVar var = new BioDataVar();
        BioDataVar dup = (BioDataVar) (var.duplicate(null));
        assertEquals(var, dup);
        assertNotSame(var, dup);

        var.setColName(COL_NAME);
        var.setFullName(FULL_NAME);
        var.setMissVal(MISSING_VALUE);
        var.setFlagColName(FLAG_COL_NAME);

        var.setObserveType(OBSERVE_TYPE);
        var.setMeasureMethod(MEASURE_METHOD);
        var.setMethodDescription(METHOD_DESCRIPTION);
        var.setMethodReference(METHOD_REFERENCE);
        var.setResearcher(RESEARCHER);
        var.setInstrumentNames(ANALYZER_NAMES);

        var.setBiologicalSubject(BIOLOGICAL_SUBJECT);
        var.setSpeciesId(SPECIES_ID);
        var.setLifeStage(LIFE_STAGE);
        assertNotEquals(var, dup);

        dup = (BioDataVar) (var.duplicate(null));
        assertEquals(var, dup);
        assertNotSame(var, dup);
    }

    @Test
    public void testhashCodeEquals() {
        BioDataVar first = new BioDataVar();
        assertFalse(first.equals(null));
        assertFalse(first.equals(FULL_NAME));

        BioDataVar second = new BioDataVar();
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

        first.setResearcher(RESEARCHER);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setResearcher(RESEARCHER);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setInstrumentNames(ANALYZER_NAMES);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setInstrumentNames(ANALYZER_NAMES);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setBiologicalSubject(BIOLOGICAL_SUBJECT);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setBiologicalSubject(BIOLOGICAL_SUBJECT);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSpeciesId(SPECIES_ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSpeciesId(SPECIES_ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setLifeStage(LIFE_STAGE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLifeStage(LIFE_STAGE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}
