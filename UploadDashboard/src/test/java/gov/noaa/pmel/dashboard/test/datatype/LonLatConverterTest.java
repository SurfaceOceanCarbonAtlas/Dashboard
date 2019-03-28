/**
 *
 */
package gov.noaa.pmel.dashboard.test.datatype;

import gov.noaa.pmel.dashboard.datatype.LonLatConverter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for methods of {@link LonLatConverter}
 *
 * @author Karl Smith
 */
public class LonLatConverterTest {

    /**
     * Test method for {@link LonLatConverter#LonLatConverter(String, String, String)}.
     */
    @Test
    public void testLonLatConverter() {
        String[] fromLonUnits = new String[] {
                "deg E",
                "deg min E",
                "deg min sec E",
                "DDD.MMSSss E",
                "deg W",
                "deg min W",
                "deg min sec W",
                "DDD.MMSSss W"
        };
        String[] fromLatUnits = new String[] {
                "deg N",
                "deg min N",
                "deg min sec N",
                "DD.MMSSss N",
                "deg S",
                "deg min S",
                "deg min sec S",
                "DD.MMSSss S"
        };
        for (String str : fromLonUnits) {
            assertNotNull(new LonLatConverter(str, fromLonUnits[0], null));
        }
        for (String str : fromLatUnits) {
            assertNotNull(new LonLatConverter(str, fromLatUnits[0], null));
        }
    }

    /**
     * Test method for {@link LonLatConverter#convertValueOf(String)}.
     */
    @Test
    public void testConvertValueOfString() {
        LonLatConverter converter = new LonLatConverter("deg S", "deg N", null);
        assertEquals(-45.67, converter.convertValueOf("45.67"), 1.0E-6);
        converter = new LonLatConverter("deg min N", "deg N", null);
        assertEquals(45.855, converter.convertValueOf("45" + LonLatConverter.DEGREE_SYMBOL + " 51.3'"), 1.0E-6);
        converter = new LonLatConverter("deg min sec S", "deg N", null);
        assertEquals(-45.8365, converter.convertValueOf("45" + LonLatConverter.DEGREE_SYMBOL + " 50' 11.4\""), 1.0E-6);
        converter = new LonLatConverter("DD.MMSSss N", "deg N", null);
        assertEquals(45.8365, converter.convertValueOf("45.50114"), 1.0E-6);
        converter = new LonLatConverter("DD.MMSSss S", "deg N", null);
        assertEquals(-45.8365, converter.convertValueOf("45.50114"), 1.0E-6);

        converter = new LonLatConverter("deg E", "deg E", null);
        assertEquals(123.45, converter.convertValueOf("123.45"), 1.0E-6);
        assertEquals(-45.67, converter.convertValueOf("-45.67"), 1.0E-6);
        // standardize if reasonable
        assertEquals(-45.0, converter.convertValueOf("315"), 1.0E-6);
        assertEquals(45.0, converter.convertValueOf("-315"), 1.0E-6);
        // anti-meridian is +180.0
        assertEquals(180.0, converter.convertValueOf("-180.0"), 1.0E-6);
        // do not standardize if unreasonable
        assertEquals(9999.0, converter.convertValueOf("9999"), 1.0E-6);
        // missing values
        assertNull(converter.convertValueOf("-9999"));
        assertNull(converter.convertValueOf(" --- "));

        converter = new LonLatConverter("deg W", "deg E", null);
        assertEquals(-123.45, converter.convertValueOf("123.45"), 1.0E-6);
        assertEquals(45.67, converter.convertValueOf("-45.67"), 1.0E-6);
        // standardize if reasonable
        assertEquals(45.0, converter.convertValueOf("315"), 1.0E-6);
        assertEquals(-45.0, converter.convertValueOf("-315"), 1.0E-6);
        // anti-meridian is +180.0
        assertEquals(180.0, converter.convertValueOf("-180.0"), 1.0E-6);
        // do not standardize if unreasonable
        assertEquals(-9999.0, converter.convertValueOf("9999"), 1.0E-6);
        // missing values
        assertNull(converter.convertValueOf("-9999"));
        assertNull(converter.convertValueOf(" --- "));

        converter = new LonLatConverter("deg min E", "deg E", null);
        assertEquals(-45.25, converter.convertValueOf("314" + LonLatConverter.DEGREE_SYMBOL + " 45.0'"), 1.0E-6);
        assertEquals(45.855, converter.convertValueOf("45" + LonLatConverter.DEGREE_SYMBOL + " 51.3'"), 1.0E-6);
        assertEquals(45.855, converter.convertValueOf("45 51.3"), 1.0E-6);

        // degrees must be given if deg min - does not pay attention to ° and '
        boolean errCaught = false;
        try {
            converter.convertValueOf("11.4'");
        } catch ( IllegalArgumentException ex ) {
            errCaught = true;
        }
        assertTrue(errCaught);

        converter = new LonLatConverter("deg min W", "deg E", null);
        assertEquals(45.25, converter.convertValueOf("314" + LonLatConverter.DEGREE_SYMBOL + " 45.0'"), 1.0E-6);
        assertEquals(-45.35, converter.convertValueOf("45" + LonLatConverter.DEGREE_SYMBOL + " 21'"), 1.0E-6);

        converter = new LonLatConverter("deg min sec E", "deg E", null);
        assertEquals(-45.7675, converter.convertValueOf("314" + LonLatConverter.DEGREE_SYMBOL + " 13' 57.0\""), 1.0E-6);
        assertEquals(45.8365, converter.convertValueOf("45" + LonLatConverter.DEGREE_SYMBOL + " 50' 11.4\""), 1.0E-6);
        assertEquals(45.8365, converter.convertValueOf("45" + LonLatConverter.DEGREE_SYMBOL + " 50' 11.4''"), 1.0E-6);
        assertEquals(45.8365, converter.convertValueOf("45  50  11.4"), 1.0E-6);

        converter = new LonLatConverter("deg min sec W", "deg E", null);
        assertEquals(45.7675, converter.convertValueOf("314" + LonLatConverter.DEGREE_SYMBOL + " 13' 57.0\""), 1.0E-6);
        assertEquals(-45.8365, converter.convertValueOf("45" + LonLatConverter.DEGREE_SYMBOL + " 50' 11.4\""), 1.0E-6);

        // degrees and minutes must be given if deg min sec - does not pay attention to °, ', and "
        errCaught = false;
        try {
            converter.convertValueOf("45" + LonLatConverter.DEGREE_SYMBOL + " 11.4\"");
        } catch ( IllegalArgumentException ex ) {
            errCaught = true;
        }
        assertTrue(errCaught);

        errCaught = false;
        try {
            converter.convertValueOf("50' 11.4\"");
        } catch ( IllegalArgumentException ex ) {
            errCaught = true;
        }
        assertTrue(errCaught);

        // °, ', and " are ignored - always taken as deg-min-sec
        assertEquals(-45.2325, converter.convertValueOf("45\" 13' 57" + LonLatConverter.DEGREE_SYMBOL), 1.0E-6);

        converter = new LonLatConverter("DDD.MMSSss E", "deg E", null);
        assertEquals(-45.25, converter.convertValueOf("314.45"), 1.0E-6);
        assertEquals(45.855, converter.convertValueOf("45.5118"), 1.0E-6);
        assertEquals(-45.7675, converter.convertValueOf("314.135700"), 1.0E-6);
        converter = new LonLatConverter("DDD.MMSSss W", "deg E", null);
        assertEquals(45.7675, converter.convertValueOf("314.135700"), 1.0E-6);
    }

}
