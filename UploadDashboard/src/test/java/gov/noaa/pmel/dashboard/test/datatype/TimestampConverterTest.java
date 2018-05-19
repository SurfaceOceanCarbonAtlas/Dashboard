package gov.noaa.pmel.dashboard.test.datatype;

import gov.noaa.pmel.dashboard.datatype.TimestampConverter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for method of {@link TimestampConverter}
 */
public class TimestampConverterTest {

    /**
     * Test of {@link TimestampConverter#TimestampConverter(String, String, String)}
     */
    @Test
    public void testTimestampConverter() {
        // timestamps
        String[] fromTimestampUnits = new String[] {
                "yyyy-mm-dd hh:mm:ss",
                "mm-dd-yyyy hh:mm:ss",
                "dd-mm-yyyy hh:mm:ss",
                "mon-dd-yyyy hh:mm:ss",
                "dd-mon-yyyy hh:mm:ss",
                "mm-dd-yy hh:mm:ss",
                "dd-mm-yy hh:mm:ss",
                "mon-dd-yy hh:mm:ss",
                "dd-mon-yy hh:mm:ss"
        };
        for (String fromUnit : fromTimestampUnits) {
            assertNotNull(new TimestampConverter(fromUnit, fromTimestampUnits[0], null));
        }
        String[] fromDateUnits = new String[] {
                "yyyy-mm-dd",
                "mm-dd-yyyy",
                "dd-mm-yyyy",
                "mm-dd-yy",
                "dd-mm-yy",
                "dd-mon-yyyy",
                "dd-mon-yy"
        };
        for (String fromUnit : fromDateUnits) {
            assertNotNull(new TimestampConverter(fromUnit, fromDateUnits[0], null));
        }
        String[] fromTimeUnits = new String[] { "hh:mm:ss" };
        for (String fromUnit : fromTimeUnits) {
            assertNotNull(new TimestampConverter(fromUnit, fromTimeUnits[0], null));
        }

    }

    /**
     * Test of {@link TimestampConverter#convertValueOf(String)}
     */
    @Test
    public void testConvertValueOf() {
        TimestampConverter converter = new TimestampConverter("mon-dd-yyyy hh:mm:ss", "yyyy-mm-dd hh:mm:ss", null);
        // assertEquals("1999-06-25 05:32:45", converter.convertValueOf("June 25, 1999 5:32:45"));
        assertEquals("1999-10-25 05:32:45.000", converter.convertValueOf("Oct-25-1999 5:32:45"));
        converter = new TimestampConverter("mm-dd-yy hh:mm:ss", "yyyy-mm-dd hh:mm:ss", null);
        assertEquals("1999-06-25 05:32:45.000", converter.convertValueOf("6-25-99 5:32:45"));
        converter = new TimestampConverter("dd-mm-yyyy", "yyyy-mm-dd", null);
        assertEquals("2015-03-15", converter.convertValueOf("15-3-2015"));
        converter = new TimestampConverter("dd-mon-yyyy", "yyyy-mm-dd", null);
        assertEquals("2015-05-15", converter.convertValueOf("15 May 2015"));
    }

}