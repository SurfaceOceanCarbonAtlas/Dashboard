package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.Coverage;
import gov.noaa.pmel.socatmetadata.shared.util.NumericString;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CoverageTest {

    private static final NumericString EMPTY_LONGITUDE = new NumericString(null, Coverage.LONGITUDE_UNITS);
    private static final NumericString EMPTY_LATITUDE = new NumericString(null, Coverage.LATITUDE_UNITS);
    private static final TreeSet<String> EMPTY_NAMESET = new TreeSet<String>();

    private static final NumericString WESTERN_LONGITUDE = new NumericString("146.23", Coverage.LONGITUDE_UNITS);
    private static final NumericString EASTERN_LONGITUDE = new NumericString("-120.45", Coverage.LONGITUDE_UNITS);
    private static final NumericString SOUTHERN_LATITUDE = new NumericString("15.36", Coverage.LATITUDE_UNITS);
    private static final NumericString NORTHERN_LATITUDE = new NumericString("45.03", Coverage.LATITUDE_UNITS);
    private static final String EARLIEST_DATA_TIMESTAMP = "2015-01-05 13:25:53";
    private static final String LATEST_DATA_TIMESTAMP = "2015-02-14 19:04:23";
    private static final String SPATIAL_REFERENCE = "NAD 83";
    private static final TreeSet<String> GEOGRAPHIC_NAMES = new TreeSet<String>(Arrays.asList(
            "North Pacific",
            "Tropical Pacific"
    ));

    private static final Date EARLIEST_DATA_TIME;
    private static final Date LATEST_DATA_TIME;

    static {
        DateFormat timeParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        timeParser.setLenient(false);
        try {
            EARLIEST_DATA_TIME = timeParser.parse(EARLIEST_DATA_TIMESTAMP);
            LATEST_DATA_TIME = timeParser.parse(LATEST_DATA_TIMESTAMP);
        } catch ( ParseException ex ) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void testGetSetWesternLongitude() {
        Coverage coverage = new Coverage();
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());
        coverage.setWesternLongitude(WESTERN_LONGITUDE);
        NumericString numstr = coverage.getWesternLongitude();
        assertEquals(WESTERN_LONGITUDE, numstr);
        assertNotSame(WESTERN_LONGITUDE, numstr);
        assertNotSame(numstr, coverage.getWesternLongitude());
        coverage.setWesternLongitude(null);
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());
        try {
            coverage.setWesternLongitude(new NumericString("-600.0", Coverage.LONGITUDE_UNITS));
            fail("calling setWesternLongitude with -600.0 succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            coverage.setWesternLongitude(new NumericString("0.0", Coverage.LATITUDE_UNITS));
            fail("calling setWesternLongitude with latitude units succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetEasternLongitude() {
        Coverage coverage = new Coverage();
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        coverage.setEasternLongitude(EASTERN_LONGITUDE);
        NumericString numstr = coverage.getEasternLongitude();
        assertEquals(EASTERN_LONGITUDE, numstr);
        assertNotSame(EASTERN_LONGITUDE, numstr);
        assertNotSame(numstr, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());
        coverage.setEasternLongitude(null);
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        try {
            coverage.setEasternLongitude(new NumericString("600.0", Coverage.LONGITUDE_UNITS));
            fail("calling setEasternLongitude with 600.0 succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            coverage.setEasternLongitude(new NumericString("0.0", Coverage.LATITUDE_UNITS));
            fail("calling setEasternLongitude with latitude units succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetSouthernLatitude() {
        Coverage coverage = new Coverage();
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        coverage.setSouthernLatitude(SOUTHERN_LATITUDE);
        NumericString numstr = coverage.getSouthernLatitude();
        assertEquals(SOUTHERN_LATITUDE, numstr);
        assertNotSame(SOUTHERN_LATITUDE, numstr);
        assertNotSame(numstr, coverage.getSouthernLatitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());
        coverage.setSouthernLatitude(null);
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        try {
            coverage.setSouthernLatitude(new NumericString("-100.0", Coverage.LATITUDE_UNITS));
            fail("calling setSouthernLatitude with -100.0 succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            coverage.setSouthernLatitude(new NumericString("0.0", Coverage.LONGITUDE_UNITS));
            fail("calling setSouthernLatitude with longitude units succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetNorthernLatitude() {
        Coverage coverage = new Coverage();
        assertEquals(EMPTY_LATITUDE, coverage.getNorthernLatitude());
        coverage.setNorthernLatitude(NORTHERN_LATITUDE);
        NumericString numstr = coverage.getNorthernLatitude();
        assertEquals(NORTHERN_LATITUDE, numstr);
        assertNotSame(NORTHERN_LATITUDE, numstr);
        assertNotSame(numstr, coverage.getNorthernLatitude());
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());
        coverage.setNorthernLatitude(null);
        assertEquals(EMPTY_LATITUDE, coverage.getNorthernLatitude());
        try {
            coverage.setNorthernLatitude(new NumericString("100.0", Coverage.LATITUDE_UNITS));
            fail("calling setNorthernLatitude with 100.0 succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            coverage.setNorthernLatitude(new NumericString("0.0", Coverage.LONGITUDE_UNITS));
            fail("calling setNorthernLatitude with longitude units succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetEarliestDataTime() {
        Coverage coverage = new Coverage();
        Date dataTime = coverage.getEarliestDataTime();
        assertNotNull(dataTime);
        assertTrue(dataTime.before(Coverage.MIN_DATA_TIME));
        coverage.setEarliestDataTime(EARLIEST_DATA_TIME);
        dataTime = coverage.getEarliestDataTime();
        assertEquals(EARLIEST_DATA_TIME, dataTime);
        assertNotSame(EARLIEST_DATA_TIME, dataTime);
        assertNotSame(dataTime, coverage.getEarliestDataTime());
        assertEquals(EMPTY_LATITUDE, coverage.getNorthernLatitude());
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());
        coverage.setEarliestDataTime(null);
        dataTime = coverage.getEarliestDataTime();
        assertNotNull(dataTime);
        assertTrue(dataTime.before(Coverage.MIN_DATA_TIME));
        dataTime = new Date(Coverage.MIN_DATA_TIME.getTime() - 3600000L);
        coverage.setEarliestDataTime(dataTime);
        assertEquals(dataTime, coverage.getEarliestDataTime());
        assertNotSame(dataTime, coverage.getEarliestDataTime());
        dataTime = new Date(System.currentTimeMillis() + 3600000L);
        coverage.setEarliestDataTime(dataTime);
        assertEquals(dataTime, coverage.getEarliestDataTime());
        assertNotSame(dataTime, coverage.getEarliestDataTime());
    }

    @Test
    public void testGetSetLatestDataTime() {
        Coverage coverage = new Coverage();
        Date dataTime = coverage.getLatestDataTime();
        assertNotNull(dataTime);
        assertTrue(dataTime.before(Coverage.MIN_DATA_TIME));
        coverage.setLatestDataTime(EARLIEST_DATA_TIME);
        dataTime = coverage.getLatestDataTime();
        assertEquals(EARLIEST_DATA_TIME, dataTime);
        assertNotSame(EARLIEST_DATA_TIME, dataTime);
        assertNotSame(dataTime, coverage.getLatestDataTime());
        dataTime = coverage.getEarliestDataTime();
        assertNotNull(dataTime);
        assertTrue(dataTime.before(Coverage.MIN_DATA_TIME));
        assertEquals(EMPTY_LATITUDE, coverage.getNorthernLatitude());
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());
        coverage.setLatestDataTime(null);
        dataTime = coverage.getLatestDataTime();
        assertNotNull(dataTime);
        assertTrue(dataTime.before(Coverage.MIN_DATA_TIME));
        dataTime = new Date(Coverage.MIN_DATA_TIME.getTime() - 3600000L);
        coverage.setLatestDataTime(dataTime);
        assertEquals(dataTime, coverage.getLatestDataTime());
        assertNotSame(dataTime, coverage.getLatestDataTime());
        dataTime = new Date(System.currentTimeMillis() + 3600000L);
        coverage.setLatestDataTime(dataTime);
        assertEquals(dataTime, coverage.getLatestDataTime());
        assertNotSame(dataTime, coverage.getLatestDataTime());
    }

    @Test
    public void testGetSetSpatialReference() {
        Coverage coverage = new Coverage();
        assertEquals(Coverage.WGS84, coverage.getSpatialReference());
        coverage.setSpatialReference(SPATIAL_REFERENCE);
        assertEquals(SPATIAL_REFERENCE, coverage.getSpatialReference());
        Date dataTime = coverage.getLatestDataTime();
        assertNotNull(dataTime);
        assertTrue(dataTime.before(Coverage.MIN_DATA_TIME));
        dataTime = coverage.getEarliestDataTime();
        assertNotNull(dataTime);
        assertTrue(dataTime.before(Coverage.MIN_DATA_TIME));
        assertEquals(EMPTY_LATITUDE, coverage.getNorthernLatitude());
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());
        coverage.setSpatialReference(null);
        assertEquals(Coverage.WGS84, coverage.getSpatialReference());
        coverage.setSpatialReference("\t");
        assertEquals(Coverage.WGS84, coverage.getSpatialReference());
    }

    @Test
    public void testGetSetGeographicNames() {
        Coverage coverage = new Coverage();
        assertEquals(EMPTY_NAMESET, coverage.getGeographicNames());
        coverage.setGeographicNames(GEOGRAPHIC_NAMES);
        TreeSet<String> nameSet = coverage.getGeographicNames();
        assertEquals(GEOGRAPHIC_NAMES, nameSet);
        assertNotSame(GEOGRAPHIC_NAMES, nameSet);
        assertNotSame(nameSet, coverage.getGeographicNames());
        assertEquals(Coverage.WGS84, coverage.getSpatialReference());
        Date dataTime = coverage.getLatestDataTime();
        assertNotNull(dataTime);
        assertTrue(dataTime.before(Coverage.MIN_DATA_TIME));
        dataTime = coverage.getEarliestDataTime();
        assertNotNull(dataTime);
        assertTrue(dataTime.before(Coverage.MIN_DATA_TIME));
        assertEquals(EMPTY_LATITUDE, coverage.getNorthernLatitude());
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());
        coverage.setGeographicNames(null);
        assertEquals(EMPTY_NAMESET, coverage.getGeographicNames());
        coverage.setSpatialReference("\t");
        assertEquals(EMPTY_NAMESET, coverage.getGeographicNames());
        try {
            coverage.setGeographicNames(Arrays.asList("North Pacific", null, "Tropical Pacific"));
            fail("setGeographicNames with null string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            coverage.setGeographicNames(Arrays.asList("North Pacific", "\n", "Tropical Pacific"));
            fail("setGeographicNames with blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testCoverage() {
        Coverage coverage = new Coverage(null, null, null, null, null, null);
        assertEquals(new Coverage(), coverage);
        coverage = new Coverage(WESTERN_LONGITUDE.getValueString(), EASTERN_LONGITUDE.getValueString(),
                SOUTHERN_LATITUDE.getValueString(), NORTHERN_LATITUDE.getValueString(),
                EARLIEST_DATA_TIMESTAMP, LATEST_DATA_TIMESTAMP);
        assertEquals(WESTERN_LONGITUDE, coverage.getWesternLongitude());
        assertEquals(EASTERN_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(SOUTHERN_LATITUDE, coverage.getSouthernLatitude());
        assertEquals(NORTHERN_LATITUDE, coverage.getNorthernLatitude());
        assertEquals(EARLIEST_DATA_TIME, coverage.getEarliestDataTime());
        assertEquals(LATEST_DATA_TIME, coverage.getLatestDataTime());
        assertEquals(Coverage.WGS84, coverage.getSpatialReference());
        assertEquals(EMPTY_NAMESET, coverage.getGeographicNames());
    }

    @Test
    public void testInvalidFieldNames() {
        Coverage coverage = new Coverage();
        assertEquals(new HashSet<String>(Arrays.asList("westernLongitude", "easternLongitude", "southernLatitude",
                "northernLatitude", "earliestDataTime", "latestDataTime")), coverage.invalidFieldNames());

        coverage.setWesternLongitude(WESTERN_LONGITUDE);
        coverage.setEasternLongitude(EASTERN_LONGITUDE);
        assertEquals(new HashSet<String>(Arrays.asList("southernLatitude", "northernLatitude",
                "earliestDataTime", "latestDataTime")), coverage.invalidFieldNames());

        coverage.setSouthernLatitude(SOUTHERN_LATITUDE);
        coverage.setNorthernLatitude(NORTHERN_LATITUDE);
        assertEquals(new HashSet<String>(Arrays.asList(
                "earliestDataTime", "latestDataTime")), coverage.invalidFieldNames());

        coverage.setEarliestDataTime(EARLIEST_DATA_TIME);
        coverage.setLatestDataTime(LATEST_DATA_TIME);
        assertEquals(new HashSet<String>(), coverage.invalidFieldNames());

        coverage.setWesternLongitude(EASTERN_LONGITUDE);
        coverage.setEasternLongitude(WESTERN_LONGITUDE);
        assertEquals(new HashSet<String>(), coverage.invalidFieldNames());

        coverage.setSouthernLatitude(NORTHERN_LATITUDE);
        coverage.setNorthernLatitude(SOUTHERN_LATITUDE);
        assertEquals(new HashSet<String>(Arrays.asList(
                "southernLatitude", "northernLatitude")), coverage.invalidFieldNames());
        coverage.setSouthernLatitude(SOUTHERN_LATITUDE);
        coverage.setNorthernLatitude(NORTHERN_LATITUDE);
        assertEquals(new HashSet<String>(), coverage.invalidFieldNames());

        coverage.setEarliestDataTime(LATEST_DATA_TIME);
        coverage.setLatestDataTime(EARLIEST_DATA_TIME);
        assertEquals(new HashSet<String>(Arrays.asList(
                "earliestDataTime", "latestDataTime")), coverage.invalidFieldNames());
    }

    @Test
    public void testClone() {
        Coverage coverage = new Coverage();
        Coverage dup = coverage.clone();
        assertEquals(coverage, dup);
        assertNotSame(coverage, dup);

        coverage.setWesternLongitude(WESTERN_LONGITUDE);
        coverage.setEasternLongitude(EASTERN_LONGITUDE);
        coverage.setSouthernLatitude(SOUTHERN_LATITUDE);
        coverage.setNorthernLatitude(NORTHERN_LATITUDE);
        coverage.setEarliestDataTime(EARLIEST_DATA_TIME);
        coverage.setLatestDataTime(LATEST_DATA_TIME);
        coverage.setSpatialReference(SPATIAL_REFERENCE);
        coverage.setGeographicNames(GEOGRAPHIC_NAMES);
        assertNotEquals(coverage, dup);

        dup = coverage.clone();
        assertEquals(coverage, dup);
        assertNotSame(coverage, dup);
        assertNotSame(coverage.getWesternLongitude(), dup.getWesternLongitude());
        assertNotSame(coverage.getEasternLongitude(), dup.getEasternLongitude());
        assertNotSame(coverage.getSouthernLatitude(), dup.getSouthernLatitude());
        assertNotSame(coverage.getNorthernLatitude(), dup.getNorthernLatitude());
        assertNotSame(coverage.getEarliestDataTime(), dup.getEarliestDataTime());
        assertNotSame(coverage.getLatestDataTime(), dup.getLatestDataTime());
        assertNotSame(coverage.getGeographicNames(), dup.getGeographicNames());
    }

    @Test
    public void testHashCodeEquals() {
        Coverage first = new Coverage();
        assertFalse(first.equals(null));
        assertFalse(first.equals(WESTERN_LONGITUDE));

        Coverage second = new Coverage();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setWesternLongitude(WESTERN_LONGITUDE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setWesternLongitude(WESTERN_LONGITUDE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setEasternLongitude(EASTERN_LONGITUDE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setEasternLongitude(EASTERN_LONGITUDE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSouthernLatitude(SOUTHERN_LATITUDE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSouthernLatitude(SOUTHERN_LATITUDE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setNorthernLatitude(NORTHERN_LATITUDE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setNorthernLatitude(NORTHERN_LATITUDE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setEarliestDataTime(EARLIEST_DATA_TIME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setEarliestDataTime(EARLIEST_DATA_TIME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setLatestDataTime(LATEST_DATA_TIME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLatestDataTime(LATEST_DATA_TIME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setSpatialReference(SPATIAL_REFERENCE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setSpatialReference(SPATIAL_REFERENCE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setGeographicNames(GEOGRAPHIC_NAMES);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setGeographicNames(GEOGRAPHIC_NAMES);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}
