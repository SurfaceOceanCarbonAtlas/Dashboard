/**
 *
 */
package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for method in {@link DashboardUtils}
 * except for the various {@link java.util.Comparator} methods.
 *
 * @author Karl Smith
 */
public class DashboardUtilsTest {

    /**
     * Simple test of working with the GregorianCalendar
     * (also a method of getting the value for DATE_MISSING_VALUE)
     */
    @Test
    public void testGregorianCalendar() {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        GregorianCalendar cal = new GregorianCalendar(utc);
        long value;

        // Full settings
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();
        cal.setTimeZone(utc);
        cal.set(1800, GregorianCalendar.JANUARY, 2, 0, 0, 0);
        value = cal.getTimeInMillis();
        assertEquals(-5364576000000L, value);

        // Clear does not remove the time zone
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();
        cal.set(1800, GregorianCalendar.JANUARY, 2, 0, 0, 0);
        value = cal.getTimeInMillis();
        assertEquals(-5364576000000L, value);

        // Actually just need to set milliseconds to zero
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(1800, GregorianCalendar.JANUARY, 2, 0, 0, 0);
        cal.set(GregorianCalendar.MILLISECOND, 0);
        value = cal.getTimeInMillis();
        assertEquals(-5364576000000L, value);
    }

    /**
     * Test method for {@link DashboardUtils#decodeStringArrayList(String)}
     * and {@link DashboardUtils#encodeStringArrayList(ArrayList)}.
     */
    @Test
    public void testEncodeDecodeStringArrayList() {
        ArrayList<String> myList = new ArrayList<String>(
                Arrays.asList("one", "two", "", "four, five, and six", "", ""));
        String encoded = DashboardUtils.encodeStringArrayList(myList);
        ArrayList<String> decodedList = DashboardUtils.decodeStringArrayList(encoded);
        assertEquals(myList, decodedList);
        decodedList = DashboardUtils.decodeStringArrayList("[]");
        assertEquals(0, decodedList.size());
        decodedList = DashboardUtils.decodeStringArrayList("[  ]");
        assertEquals(0, decodedList.size());
        decodedList = DashboardUtils.decodeStringArrayList("[\"\"]");
        assertEquals(new ArrayList<String>(Arrays.asList("")), decodedList);
    }

    /**
     * Test method for {@link DashboardUtils#decodeStringTreeSet(String)}
     * and {@link DashboardUtils#encodeStringTreeSet(TreeSet)}.
     */
    @Test
    public void testEncodeDecodeStringTreeSet() {
        TreeSet<String> mySet = new TreeSet<String>(
                Arrays.asList("one", "two", "", "four", "five and six"));
        String encoded = DashboardUtils.encodeStringTreeSet(mySet);
        TreeSet<String> decodedSet = DashboardUtils.decodeStringTreeSet(encoded);
        assertEquals(mySet, decodedSet);
        decodedSet = DashboardUtils.decodeStringTreeSet("[]");
        assertEquals(0, decodedSet.size());
        decodedSet = DashboardUtils.decodeStringTreeSet("[  ]");
        assertEquals(0, decodedSet.size());
        decodedSet = DashboardUtils.decodeStringTreeSet("[\"\"]");
        assertEquals(new TreeSet<String>(Arrays.asList("")), decodedSet);
    }

    /**
     * Test method for {@link DashboardUtils#baseName(String)}
     */
    @Test
    public void testBaseName() {
        String filename = "dataset.tsv";
        assertEquals(filename, DashboardUtils.baseName("/Some/path/to/my/" + filename));
        assertEquals(filename, DashboardUtils.baseName("relative/path/to/my/" + filename));
        assertEquals(filename, DashboardUtils.baseName("relative/path/to/my///" + filename));
        assertEquals(filename, DashboardUtils.baseName("  " + filename + "\n"));
        assertEquals(filename, DashboardUtils.baseName("\\Some\\path\\to\\my\\" + filename));
        assertEquals(filename, DashboardUtils.baseName("/Some/path\\to/my/" + filename));
        assertEquals(filename, DashboardUtils.baseName("relative/path/to/my\\ " + filename));
        assertEquals("my " + filename, DashboardUtils.baseName("relative/path/to/my " + filename));
        assertEquals("", DashboardUtils.baseName("/Some/path/to/my/"));
        assertEquals("", DashboardUtils.baseName(""));
        assertEquals("", DashboardUtils.baseName("  "));
        assertEquals("", DashboardUtils.baseName(null));
    }

    /**
     * Test method for {@link DashboardUtils#longitudeCloseTo(Double, Double, double, double)}
     * and {@link DashboardUtils#closeTo(Double, Double, double, double)}
     */
    @Test
    public void testCloseToLongitudeCloseTo() {
        double lon1 = -179.9999994;
        double deltalon1 = lon1 - 1.2E-6;
        double lon2 = deltalon1 + 360.0;

        assertTrue(DashboardUtils.closeTo(lon1, deltalon1, 0.0, 1.0E-5));
        assertFalse(DashboardUtils.closeTo(lon1, deltalon1, 0.0, 1.0E-6));
        assertTrue(DashboardUtils.closeTo(lon1, deltalon1, 1.0E-8, 0.0));
        assertFalse(DashboardUtils.closeTo(lon1, deltalon1, 1.0E-9, 0.0));
        assertTrue(DashboardUtils.closeTo(Double.NaN, Double.NaN, 0.0, 0.0));
        assertFalse(DashboardUtils.closeTo(Double.NaN, lon1, 1.0, 1.0));

        assertFalse(DashboardUtils.closeTo(lon1, lon2, 0.0, 1.0E-3));
        assertTrue(DashboardUtils.longitudeCloseTo(lon1, lon2, 0.0, 1.0E-5));
        assertFalse(DashboardUtils.longitudeCloseTo(lon1, lon2, 0.0, 1.0E-6));
        assertFalse(DashboardUtils.closeTo(lon1, lon2, 1.0E-3, 0.0));
        assertTrue(DashboardUtils.longitudeCloseTo(lon1, lon2, 1.0E-8, 0.0));
        assertFalse(DashboardUtils.longitudeCloseTo(lon1, lon2, 1.0E-9, 0.0));
        assertTrue(DashboardUtils.longitudeCloseTo(Double.NaN, Double.NaN, 0.0, 0.0));
        assertFalse(DashboardUtils.longitudeCloseTo(Double.NaN, lon1, 1.0, 1.0));
    }

    /**
     * Test method for {@link DashboardUtils#guessPlatformType(String, String)}.
     */
    @Test
    public void testGuessPlatformType() {
        assertEquals("Ship", DashboardUtils.guessPlatformType("ABCD", ""));
        assertEquals("Mooring", DashboardUtils.guessPlatformType("ABCD", "Mooring"));
        assertEquals("Mooring", DashboardUtils.guessPlatformType("ABCD", "Buoy"));
        assertEquals("Drifting Buoy", DashboardUtils.guessPlatformType("ABCD", "Drifting Buoy"));
        assertEquals("Mooring", DashboardUtils.guessPlatformType("3164", ""));
        assertEquals("Mooring", DashboardUtils.guessPlatformType("91FS", ""));
        assertEquals("Drifting Buoy", DashboardUtils.guessPlatformType("91DB", ""));
    }

}
