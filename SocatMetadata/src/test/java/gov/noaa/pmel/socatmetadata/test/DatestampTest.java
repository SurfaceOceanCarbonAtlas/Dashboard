package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DatestampTest {

    private static final int YEAR = 2010;
    private static final int MONTH = 6;
    private static final int DAY = 25;
    private static final int HOUR = 15;
    private static final int MINUTE = 23;
    private static final int SECOND = 53;
    private static final String DATESTRING = "2010-06-25";
    private static final String MONTHSTRING = "2010-06";
    private static final String YEARSTRING = "2010";
    private static final String FULLTIMESTRING = "15:23:53";
    private static final String MINUTETIMESTRING = "15:23";
    private static final String HOURTIMESTRING = "15";


    @Test
    public void testGetSetYear() {
        Datestamp datestamp = new Datestamp();
        assertEquals(Datestamp.INVALID, datestamp.getYear());
        datestamp.setYear(YEAR);
        assertEquals(YEAR, datestamp.getYear());
    }

    @Test
    public void testGetSetMonth() {
        Datestamp datestamp = new Datestamp();
        assertEquals(Datestamp.INVALID, datestamp.getMonth());
        datestamp.setMonth(MONTH);
        assertEquals(MONTH, datestamp.getMonth());
        assertEquals(Datestamp.INVALID, datestamp.getYear());
    }

    @Test
    public void testGetSetDay() {
        Datestamp datestamp = new Datestamp();
        assertEquals(Datestamp.INVALID, datestamp.getDay());
        datestamp.setDay(DAY);
        assertEquals(DAY, datestamp.getDay());
        assertEquals(Datestamp.INVALID, datestamp.getMonth());
        assertEquals(Datestamp.INVALID, datestamp.getYear());
    }

    @Test
    public void testGetSetHour() {
        Datestamp datestamp = new Datestamp();
        assertEquals(Datestamp.INVALID, datestamp.getHour());
        datestamp.setHour(HOUR);
        assertEquals(HOUR, datestamp.getHour());
        assertEquals(Datestamp.INVALID, datestamp.getDay());
        assertEquals(Datestamp.INVALID, datestamp.getMonth());
        assertEquals(Datestamp.INVALID, datestamp.getYear());
    }

    @Test
    public void testGetSetMinute() {
        Datestamp datestamp = new Datestamp();
        assertEquals(Datestamp.INVALID, datestamp.getMinute());
        datestamp.setMinute(MINUTE);
        assertEquals(MINUTE, datestamp.getMinute());
        assertEquals(Datestamp.INVALID, datestamp.getHour());
        assertEquals(Datestamp.INVALID, datestamp.getDay());
        assertEquals(Datestamp.INVALID, datestamp.getMonth());
        assertEquals(Datestamp.INVALID, datestamp.getYear());
    }

    @Test
    public void testGetSetSecond() {
        Datestamp datestamp = new Datestamp();
        assertEquals(Datestamp.INVALID, datestamp.getSecond());
        datestamp.setSecond(SECOND);
        assertEquals(SECOND, datestamp.getSecond());
        assertEquals(Datestamp.INVALID, datestamp.getMinute());
        assertEquals(Datestamp.INVALID, datestamp.getHour());
        assertEquals(Datestamp.INVALID, datestamp.getDay());
        assertEquals(Datestamp.INVALID, datestamp.getMonth());
        assertEquals(Datestamp.INVALID, datestamp.getYear());
    }

    @Test
    public void testDateStringTimeString() {
        Datestamp datestamp = new Datestamp();
        try {
            datestamp.dateString();
            fail("dateString called on an empty datestamp did not throw an exception");
        } catch ( IllegalArgumentException ex ) {
            // expected result
        }
        try {
            datestamp.timeString();
            fail("timeString called on an empty datestamp did not throw an exception");
        } catch ( IllegalArgumentException ex ) {
            // expected result
        }
        datestamp.setYear(YEAR);
        datestamp.setYear(YEAR);
        datestamp.setMonth(MONTH);
        datestamp.setDay(DAY);
        datestamp.setHour(HOUR);
        datestamp.setMinute(MINUTE);
        datestamp.setSecond(SECOND);
        assertEquals(DATESTRING, datestamp.dateString());
        assertEquals(FULLTIMESTRING, datestamp.timeString());

        datestamp.setSecond(Datestamp.INVALID);
        assertEquals(DATESTRING, datestamp.dateString());
        assertEquals(MINUTETIMESTRING, datestamp.timeString());
        datestamp.setSecond(SECOND);

        datestamp.setMinute(Datestamp.INVALID);
        assertEquals(DATESTRING, datestamp.dateString());
        assertEquals(HOURTIMESTRING, datestamp.timeString());
        datestamp.setMinute(MINUTE);

        datestamp.setHour(Datestamp.INVALID);
        assertEquals(DATESTRING, datestamp.dateString());
        try {
            datestamp.timeString();
            fail("timeString called on an invalid hour datestamp did not throw an exception");
        } catch ( IllegalArgumentException ex ) {
            // expected result
        }
        datestamp.setHour(HOUR);

        datestamp.setYear(2011);
        datestamp.setMonth(2);
        datestamp.setDay(29);
        try {
            datestamp.dateString();
            fail("dateString called on an invalid datestamp did not throw an exception");
        } catch ( IllegalArgumentException ex ) {
            // expected result
        }
        try {
            datestamp.timeString();
            fail("timeString called on an invalid datestamp did not throw an exception");
        } catch ( IllegalArgumentException ex ) {
            // expected result
        }
    }

    @Test
    public void testFullOrPartialString() {
        Datestamp datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND);
        assertEquals(DATESTRING + " " + FULLTIMESTRING, datestamp.fullOrPartialString());
        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, MINUTE, 60);
        assertEquals(DATESTRING + " " + MINUTETIMESTRING, datestamp.fullOrPartialString());
        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, 60, SECOND);
        assertEquals(DATESTRING + " " + HOURTIMESTRING, datestamp.fullOrPartialString());
        datestamp = new Datestamp(YEAR, MONTH, DAY, -5, MINUTE, SECOND);
        assertEquals(DATESTRING, datestamp.fullOrPartialString());
        datestamp = new Datestamp(YEAR, MONTH, -3, HOUR, MINUTE, SECOND);
        assertEquals(MONTHSTRING, datestamp.fullOrPartialString());
        datestamp = new Datestamp(YEAR, -1, DAY, HOUR, MINUTE, SECOND);
        assertEquals(YEARSTRING, datestamp.fullOrPartialString());
        datestamp = new Datestamp(1899, MONTH, DAY, HOUR, MINUTE, SECOND);
        assertEquals("", datestamp.fullOrPartialString());
    }

    @Test
    public void testDatestamp() {
        Datestamp datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND);
        assertEquals(YEAR, datestamp.getYear());
        assertEquals(MONTH, datestamp.getMonth());
        assertEquals(DAY, datestamp.getDay());
        assertEquals(HOUR, datestamp.getHour());
        assertEquals(MINUTE, datestamp.getMinute());
        assertEquals(SECOND, datestamp.getSecond());

        datestamp = new Datestamp(Integer.toString(YEAR), Integer.toString(MONTH), Integer.toString(DAY),
                Integer.toString(HOUR), Integer.toString(MINUTE), Integer.toString(SECOND));
        assertEquals(YEAR, datestamp.getYear());
        assertEquals(MONTH, datestamp.getMonth());
        assertEquals(DAY, datestamp.getDay());
        assertEquals(HOUR, datestamp.getHour());
        assertEquals(MINUTE, datestamp.getMinute());
        assertEquals(SECOND, datestamp.getSecond());

        datestamp = new Datestamp(null, Integer.toString(MONTH), Integer.toString(DAY),
                Integer.toString(HOUR), Integer.toString(MINUTE), Integer.toString(SECOND));
        assertEquals(Datestamp.INVALID, datestamp.getYear());
        assertEquals(MONTH, datestamp.getMonth());
        assertEquals(DAY, datestamp.getDay());
        assertEquals(HOUR, datestamp.getHour());
        assertEquals(MINUTE, datestamp.getMinute());
        assertEquals(SECOND, datestamp.getSecond());

        datestamp = new Datestamp(Integer.toString(YEAR), null, Integer.toString(DAY),
                Integer.toString(HOUR), Integer.toString(MINUTE), Integer.toString(SECOND));
        assertEquals(YEAR, datestamp.getYear());
        assertEquals(Datestamp.INVALID, datestamp.getMonth());
        assertEquals(DAY, datestamp.getDay());
        assertEquals(HOUR, datestamp.getHour());
        assertEquals(MINUTE, datestamp.getMinute());
        assertEquals(SECOND, datestamp.getSecond());

        datestamp = new Datestamp(Integer.toString(YEAR), Integer.toString(MONTH), null,
                Integer.toString(HOUR), Integer.toString(MINUTE), Integer.toString(SECOND));
        assertEquals(YEAR, datestamp.getYear());
        assertEquals(MONTH, datestamp.getMonth());
        assertEquals(Datestamp.INVALID, datestamp.getDay());
        assertEquals(HOUR, datestamp.getHour());
        assertEquals(MINUTE, datestamp.getMinute());
        assertEquals(SECOND, datestamp.getSecond());

        datestamp = new Datestamp(Integer.toString(YEAR), Integer.toString(MONTH), Integer.toString(DAY),
                null, Integer.toString(MINUTE), Integer.toString(SECOND));
        assertEquals(YEAR, datestamp.getYear());
        assertEquals(MONTH, datestamp.getMonth());
        assertEquals(DAY, datestamp.getDay());
        assertEquals(Datestamp.INVALID, datestamp.getHour());
        assertEquals(MINUTE, datestamp.getMinute());
        assertEquals(SECOND, datestamp.getSecond());

        datestamp = new Datestamp(Integer.toString(YEAR), Integer.toString(MONTH), Integer.toString(DAY),
                Integer.toString(HOUR), null, Integer.toString(SECOND));
        assertEquals(YEAR, datestamp.getYear());
        assertEquals(MONTH, datestamp.getMonth());
        assertEquals(DAY, datestamp.getDay());
        assertEquals(HOUR, datestamp.getHour());
        assertEquals(Datestamp.INVALID, datestamp.getMinute());
        assertEquals(SECOND, datestamp.getSecond());

        datestamp = new Datestamp(Integer.toString(YEAR), Integer.toString(MONTH), Integer.toString(DAY),
                Integer.toString(HOUR), Integer.toString(MINUTE), null);
        assertEquals(YEAR, datestamp.getYear());
        assertEquals(MONTH, datestamp.getMonth());
        assertEquals(DAY, datestamp.getDay());
        assertEquals(HOUR, datestamp.getHour());
        assertEquals(MINUTE, datestamp.getMinute());
        assertEquals(Datestamp.INVALID, datestamp.getSecond());

        datestamp = new Datestamp();
        Datestamp clone = new Datestamp(datestamp);
        assertEquals(datestamp, clone);

        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND);
        assertNotEquals(datestamp, clone);

        clone = new Datestamp(datestamp);
        assertEquals(datestamp, clone);
    }

    @Test
    public void testIsValid() {
        Datestamp datestamp = new Datestamp();
        assertFalse(datestamp.isValid(null));

        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND);
        assertTrue(datestamp.isValid(null));

        datestamp = new Datestamp(Datestamp.INVALID, MONTH, DAY, HOUR, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));
        datestamp = new Datestamp(1899, MONTH, DAY, HOUR, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));
        datestamp = new Datestamp(2100, MONTH, DAY, HOUR, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));

        datestamp = new Datestamp(YEAR, Datestamp.INVALID, DAY, HOUR, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));
        datestamp = new Datestamp(YEAR, 0, DAY, HOUR, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));
        datestamp = new Datestamp(YEAR, 13, DAY, HOUR, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));

        datestamp = new Datestamp(YEAR, MONTH, Datestamp.INVALID, HOUR, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));
        datestamp = new Datestamp(YEAR, MONTH, 0, HOUR, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));
        datestamp = new Datestamp(YEAR, MONTH, 32, HOUR, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));

        datestamp = new Datestamp(YEAR, MONTH, DAY, Datestamp.INVALID, MINUTE, SECOND);
        assertTrue(datestamp.isValid(null));
        datestamp = new Datestamp(YEAR, MONTH, DAY, -2, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));
        datestamp = new Datestamp(YEAR, MONTH, DAY, 32, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));

        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, Datestamp.INVALID, SECOND);
        assertTrue(datestamp.isValid(null));
        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, -2, SECOND);
        assertFalse(datestamp.isValid(null));
        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, 60, SECOND);
        assertFalse(datestamp.isValid(null));

        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, MINUTE, Datestamp.INVALID);
        assertTrue(datestamp.isValid(null));
        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, MINUTE, -2);
        assertFalse(datestamp.isValid(null));
        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, MINUTE, 60);
        assertFalse(datestamp.isValid(null));

        datestamp = new Datestamp(2011, 2, 29, HOUR, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));
    }

    @Test
    public void testHashCodeEquals() {
        Datestamp first = new Datestamp();
        assertFalse(first.equals(null));
        assertFalse(first.equals(YEAR));

        Datestamp second = new Datestamp();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setYear(YEAR);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setYear(YEAR);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setMonth(MONTH);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMonth(MONTH);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setDay(DAY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setDay(DAY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setHour(HOUR);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setHour(HOUR);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setMinute(MINUTE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMinute(MINUTE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSecond(SECOND);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSecond(SECOND);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}
