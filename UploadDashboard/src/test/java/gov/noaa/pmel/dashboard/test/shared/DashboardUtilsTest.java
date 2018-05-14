/**
 *
 */
package gov.noaa.pmel.dashboard.test.shared;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.TreeSet;

import org.junit.Test;

import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCFlag;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

/**
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
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#longitudeCloseTo(Double, Double, double, double)}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#closeTo(Double, Double, double, double)}
     */
    @Test
    public void testCloseToLongitudeCloseTo() {
        double lon1 = -179.9999994;
        double deltalon1 = lon1 - 1.2E-6;
        double lon2 = deltalon1 + 360.0;

        assertTrue( DashboardUtils.closeTo(lon1, deltalon1, 0.0, 1.0E-5) );
        assertFalse( DashboardUtils.closeTo(lon1, deltalon1, 0.0, 1.0E-6) );
        assertTrue( DashboardUtils.closeTo(lon1, deltalon1, 1.0E-8, 0.0) );
        assertFalse( DashboardUtils.closeTo(lon1, deltalon1, 1.0E-9, 0.0) );
        assertTrue( DashboardUtils.closeTo(Double.NaN, Double.NaN, 0.0, 0.0) );
        assertFalse( DashboardUtils.closeTo(Double.NaN, lon1, 1.0, 1.0) );

        assertFalse( DashboardUtils.closeTo(lon1, lon2, 0.0, 1.0E-3) );
        assertTrue( DashboardUtils.longitudeCloseTo(lon1, lon2, 0.0, 1.0E-5) );
        assertFalse( DashboardUtils.longitudeCloseTo(lon1, lon2, 0.0, 1.0E-6) );
        assertFalse( DashboardUtils.closeTo(lon1, lon2, 1.0E-3, 0.0) );
        assertTrue( DashboardUtils.longitudeCloseTo(lon1, lon2, 1.0E-8, 0.0) );
        assertFalse( DashboardUtils.longitudeCloseTo(lon1, lon2, 1.0E-9, 0.0) );
        assertTrue( DashboardUtils.longitudeCloseTo(Double.NaN, Double.NaN, 0.0, 0.0) );
        assertFalse( DashboardUtils.longitudeCloseTo(Double.NaN, lon1, 1.0, 1.0) );
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#passhashFromPlainText()}.
     */
    @Test
    public void testPasshashFromPlainText() {
        String passhash = DashboardUtils.passhashFromPlainText("username", "password");
        assertTrue( passhash.length() >= 32 );

        String otherhash = DashboardUtils.passhashFromPlainText("username", "otherpass");
        assertTrue( otherhash.length() >= 32 );
        assertFalse( passhash.equals(otherhash) );

        otherhash = DashboardUtils.passhashFromPlainText("otheruser", "password");
        assertTrue( otherhash.length() >= 32 );
        assertFalse( passhash.equals(otherhash) );

        passhash = DashboardUtils.passhashFromPlainText("me", "password");
        assertEquals(0, passhash.length());

        passhash = DashboardUtils.passhashFromPlainText("username", "pass");
        assertEquals(0, passhash.length());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#decodeByteArray(java.lang.String)}.
     */
    @Test
    public void testDecodeByteArray() {
        String expectedStr = "[ 3, 6, -9, 15, 0, 12 ]";
        byte[] expectedArray = {3, 6, -9, 15, 0, 12};
        String oneStr = "[ 8 ]";
        byte[] oneArray = { 8 };
        String emptyStr = "[  ]";
        byte[] emptyArray = new byte[0];

        byte[] byteArray = DashboardUtils.decodeByteArray(expectedStr);
        assertArrayEquals(expectedArray, byteArray);

        byteArray = DashboardUtils.decodeByteArray(oneStr);
        assertArrayEquals(oneArray, byteArray);

        byteArray = DashboardUtils.decodeByteArray(emptyStr);
        assertArrayEquals(emptyArray, byteArray);

        boolean exceptionCaught = false;
        try {
            byteArray = DashboardUtils.decodeByteArray("3, 6, -9, 15, 0, 12");
        } catch ( IllegalArgumentException ex ) {
            exceptionCaught = true;
        }
        assertTrue( exceptionCaught );

        exceptionCaught = false;
        try {
            byteArray = DashboardUtils.decodeByteArray("[ 3, 6, -9, 1115, 0, 12 ]");
        } catch ( IllegalArgumentException ex ) {
            exceptionCaught = true;
        }
        assertTrue( exceptionCaught );
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#decodeIntegerArrayList(java.lang.String)}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#encodeIntegerArrayList(java.util.ArrayList)}.
     */
    @Test
    public void testEncodeDecodeIntegerArrayList() {
        ArrayList<Integer> myList = new ArrayList<Integer>(
                Arrays.asList(234, 4563, 90312, -2349));
        String encoded = DashboardUtils.encodeIntegerArrayList(myList);
        ArrayList<Integer> decodedList =
                DashboardUtils.decodeIntegerArrayList(encoded);
        assertEquals(myList, decodedList);
        decodedList = DashboardUtils.decodeIntegerArrayList("[]");
        assertEquals(0, decodedList.size());
        decodedList = DashboardUtils.decodeIntegerArrayList("[  ]");
        assertEquals(0, decodedList.size());
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#decodeStringArrayList(java.lang.String)}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#encodeStringArrayList(java.util.ArrayList)}.
     */
    @Test
    public void testEncodeDecodeStringArrayList() {
        ArrayList<String> myList = new ArrayList<String>(
                Arrays.asList("one", "two", "", "four, five, and six", "", ""));
        String encoded = DashboardUtils.encodeStringArrayList(myList);
        ArrayList<String> decodedList =
                DashboardUtils.decodeStringArrayList(encoded);
        assertEquals(myList, decodedList);
        decodedList = DashboardUtils.decodeStringArrayList("[]");
        assertEquals(0, decodedList.size());
        decodedList = DashboardUtils.decodeStringArrayList("[  ]");
        assertEquals(0, decodedList.size());
        decodedList = DashboardUtils.decodeStringArrayList("[\"\"]");
        assertEquals(new ArrayList<String>(Arrays.asList("")), decodedList);
    }

    /**
     * Test method for {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#decodeQCFlagSet(java.lang.String)}
     * and {@link gov.noaa.pmel.dashboard.shared.DashboardUtils#encodeQCFlagSet(java.util.TreeSet)}.
     */
    @Test
    public void testEncodeDecodeWoceTypeSet() {
        TreeSet<QCFlag> mySet = new TreeSet<QCFlag>( Arrays.asList(
                new QCFlag("WOCE_CO2_water", '4', Severity.ERROR, 3, 7),
                new QCFlag("WOCE_CO2_atm", '3', Severity.WARNING, 9, 2) ) );
        String encoded = DashboardUtils.encodeQCFlagSet(mySet);
        TreeSet<QCFlag> decodedSet = DashboardUtils.decodeQCFlagSet(encoded);
        assertEquals(mySet, decodedSet);
        decodedSet = DashboardUtils.decodeQCFlagSet("[]");
        assertEquals(0, decodedSet.size());
        decodedSet = DashboardUtils.decodeQCFlagSet("[  ]");
        assertEquals(0, decodedSet.size());
    }

}
