package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
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
    private static final String TIMESTRING = "15:23:53";

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
        assertEquals(TIMESTRING, datestamp.timeString());
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

        try {
            new Datestamp(null, Integer.toString(MONTH), Integer.toString(DAY),
                    Integer.toString(HOUR), Integer.toString(MINUTE), Integer.toString(SECOND));
            fail("Datestamp constructor with null year succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            new Datestamp(Integer.toString(YEAR), null, Integer.toString(DAY),
                    Integer.toString(HOUR), Integer.toString(MINUTE), Integer.toString(SECOND));
            fail("Datestamp constructor with null month succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            new Datestamp(Integer.toString(YEAR), Integer.toString(MONTH), null,
                    Integer.toString(HOUR), Integer.toString(MINUTE), Integer.toString(SECOND));
            fail("Datestamp constructor with null day succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            new Datestamp(Integer.toString(YEAR), Integer.toString(MONTH), Integer.toString(DAY),
                    null, Integer.toString(MINUTE), Integer.toString(SECOND));
            fail("Datestamp constructor with null hour succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            new Datestamp(Integer.toString(YEAR), Integer.toString(MONTH), Integer.toString(DAY),
                    Integer.toString(HOUR), null, Integer.toString(SECOND));
            fail("Datestamp constructor with null minute succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            new Datestamp(Integer.toString(YEAR), Integer.toString(MONTH), Integer.toString(DAY),
                    Integer.toString(HOUR), Integer.toString(MINUTE), null);
            fail("Datestamp constructor with null second succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testIsValid() {
        Datestamp datestamp = new Datestamp();
        assertFalse(datestamp.isValid(null));

        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND);
        assertTrue(datestamp.isValid(null));

        datestamp = new Datestamp(2011, 2, 29, HOUR, MINUTE, SECOND);
        assertFalse(datestamp.isValid(null));
    }

    @Test
    public void testDuplicate() {
        Datestamp datestamp = new Datestamp();
        Datestamp clone = (Datestamp) (datestamp.duplicate(null));
        assertEquals(datestamp, clone);
        assertNotSame(datestamp, clone);

        datestamp = new Datestamp(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND);
        assertNotEquals(datestamp, clone);

        clone = (Datestamp) (datestamp.duplicate(null));
        assertEquals(datestamp, clone);
        assertNotSame(datestamp, clone);
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
