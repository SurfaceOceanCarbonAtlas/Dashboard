package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.MultiString;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import gov.noaa.pmel.socatmetadata.shared.variable.GenData;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class VariableTest {

    private static final String EMPTY_STRING = "";
    private static final NumericString EMPTY_NUMSTR = new NumericString();
    private static final MultiString EMPTY_MULTISTRING = new MultiString();

    private static final String COL_NAME = "SST_C";
    private static final String FULL_NAME = "Sea surface temperature";
    private static final String VAR_UNIT = "deg C";
    private static final String MISSING_VALUE = "-999";
    private static final String FLAG_COL_NAME = "WOCE SST";
    private static final NumericString ACCURACY = new NumericString("0.01", VAR_UNIT);
    private static final NumericString PRECISION = new NumericString("0.001", VAR_UNIT);
    private static final MultiString ADDN_INFO = new MultiString(
            "Some sort of information\n" +
                    "Another bit of information"
    );

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
    public void testGetSetMissVal() {
        Variable var = new Variable();
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
        GenData var = new GenData();
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
        GenData var = new GenData();
        assertEquals(EMPTY_NUMSTR, var.getAccuracy());
        var.setAccuracy(ACCURACY);
        assertEquals(new NumericString(ACCURACY.getValueString(), "deg C"), var.getAccuracy());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        var.setVarUnit(VAR_UNIT);
        assertEquals(ACCURACY, var.getAccuracy());
        var.setAccuracy(ACCURACY);
        NumericString numstr = var.getAccuracy();
        assertEquals(ACCURACY, numstr);
        assertNotSame(ACCURACY, numstr);
        assertNotSame(numstr, var.getAccuracy());
        assertEquals(EMPTY_STRING, var.getFlagColName());
        assertEquals(EMPTY_STRING, var.getMissVal());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setAccuracy(null);
        assertEquals(new NumericString("", VAR_UNIT), var.getAccuracy());
        var.setAccuracy(EMPTY_NUMSTR);
        assertEquals(new NumericString("", VAR_UNIT), var.getAccuracy());
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
        GenData var = new GenData();
        assertEquals(EMPTY_NUMSTR, var.getPrecision());
        var.setPrecision(PRECISION);
        assertEquals(new NumericString(PRECISION.getValueString(), "deg C"), var.getPrecision());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        var.setVarUnit(VAR_UNIT);
        assertEquals(PRECISION, var.getPrecision());
        var.setPrecision(PRECISION);
        NumericString numstr = var.getPrecision();
        assertEquals(PRECISION, numstr);
        assertNotSame(PRECISION, numstr);
        assertNotSame(numstr, var.getPrecision());
        assertEquals(new NumericString("", VAR_UNIT), var.getAccuracy());
        assertEquals(EMPTY_STRING, var.getFlagColName());
        assertEquals(EMPTY_STRING, var.getMissVal());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setPrecision(null);
        assertEquals(new NumericString("", VAR_UNIT), var.getPrecision());
        var.setPrecision(EMPTY_NUMSTR);
        assertEquals(new NumericString("", VAR_UNIT), var.getPrecision());
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
        Variable var = new Variable();
        assertEquals(EMPTY_MULTISTRING, var.getAddnInfo());
        var.setAddnInfo(ADDN_INFO);
        MultiString addnInfo = var.getAddnInfo();
        assertEquals(ADDN_INFO, addnInfo);
        assertNotSame(ADDN_INFO, addnInfo);
        assertNotSame(addnInfo, var.getAddnInfo());
        assertEquals(EMPTY_STRING, var.getMissVal());
        assertEquals(EMPTY_STRING, var.getVarUnit());
        assertEquals(EMPTY_STRING, var.getFullName());
        assertEquals(EMPTY_STRING, var.getColName());
        var.setAddnInfo(null);
        assertEquals(EMPTY_MULTISTRING, var.getAddnInfo());
        var.setAddnInfo(EMPTY_MULTISTRING);
        assertEquals(EMPTY_MULTISTRING, var.getAddnInfo());
    }

    @Test
    public void testInvalidFieldNames() {
        Variable var = new Variable();
        assertEquals(new HashSet<String>(Arrays.asList("colName", "fullName")), var.invalidFieldNames());
        var.setColName(COL_NAME);
        assertEquals(new HashSet<String>(Arrays.asList("fullName")), var.invalidFieldNames());
        var.setFullName(FULL_NAME);
        assertEquals(new HashSet<String>(), var.invalidFieldNames());
    }

    @Test
    public void testDuplicate() {
        Variable var = new Variable();
        Variable dup = (Variable) (var.duplicate(null));
        assertEquals(var, dup);
        assertNotSame(var, dup);

        var.setColName(COL_NAME);
        var.setFullName(FULL_NAME);
        var.setVarUnit(VAR_UNIT);
        var.setMissVal(MISSING_VALUE);
        var.setAddnInfo(ADDN_INFO);
        assertNotEquals(var, dup);

        dup = (Variable) (var.duplicate(null));
        assertEquals(var, dup);
        assertNotSame(var, dup);
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

        first.setMissVal(MISSING_VALUE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMissVal(MISSING_VALUE);
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

