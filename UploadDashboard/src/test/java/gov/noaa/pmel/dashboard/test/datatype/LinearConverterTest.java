/**
 *
 */
package gov.noaa.pmel.dashboard.test.datatype;

import gov.noaa.pmel.dashboard.datatype.LinearConverter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for methods of {@link LinearConverter}
 *
 * @author Karl Smith
 */
public class LinearConverterTest {

    /**
     * Test method for {@link LinearConverter#convertValueOf(String)}.
     */
    @Test
    public void testConvertValueOfString() {
        double testVal = 123.456;
        String testStr = Double.toString(testVal);
        String[] defMissValStr = new String[] { "", "   ", " --- " };
        String defMissValNumStr = "-9999";
        String missValStr = "nothing";

        LinearConverter converter = new LinearConverter(null, null, null);
        assertEquals(testVal, converter.convertValueOf(testStr), 1.0E-6);

        for (String str : defMissValStr) {
            assertNull(converter.convertValueOf(str));
        }
        assertNull(converter.convertValueOf(defMissValNumStr));

        boolean errCaught = false;
        try {
            converter.convertValueOf(null);
        } catch ( IllegalArgumentException ex ) {
            errCaught = true;
        }
        assertTrue(errCaught);

        errCaught = false;
        try {
            converter.convertValueOf(missValStr);
        } catch ( IllegalArgumentException ex ) {
            errCaught = true;
        }
        assertTrue(errCaught);

        errCaught = false;
        try {
            converter.convertValueOf("1012.0 hPa");
        } catch ( IllegalArgumentException ex ) {
            errCaught = true;
        }
        assertTrue(errCaught);

        // unknown units, but acceptable because input unit same as output unit
        converter = new LinearConverter("widgets", "widgets", missValStr);
        assertEquals(testVal, converter.convertValueOf(testStr), 1.0E-6);
        assertNull(converter.convertValueOf(missValStr));

        for (String str : defMissValStr) {
            errCaught = false;
            try {
                converter.convertValueOf(str);
            } catch ( IllegalArgumentException ex ) {
                errCaught = true;
            }
            assertTrue(errCaught);
        }
        assertEquals(Double.parseDouble(defMissValNumStr),
                converter.convertValueOf(defMissValNumStr), 1.0E-6);

        errCaught = false;
        try {
            // unknown linear conversion (required sea-water density)
            converter = new LinearConverter("db", "m", null);
        } catch ( IllegalArgumentException ex ) {
            errCaught = true;
        }
        assertTrue(errCaught);

        // Test some known units
        converter = new LinearConverter("K", "degC", null);
        assertEquals(0.0, converter.convertValueOf("273.15"), 1.0E-6);
        assertEquals(100.0, converter.convertValueOf("373.15"), 1.0E-6);

        converter = new LinearConverter("mbar", "db", null);
        assertEquals(10.0, converter.convertValueOf("1000.0"), 1.0E-6);

        converter = new LinearConverter("km", "m", null);
        assertEquals(100.0, converter.convertValueOf("0.1"), 1.0E-6);
    }

}
