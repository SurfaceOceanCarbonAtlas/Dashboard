package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.Coverage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class CoverageTest {

    private static final double DELTA = 1.0E-6;
    private static final double WESTERN_LONGITUDE = 146.23;
    private static final double EASTERN_LONGITUDE = -120.45;
    private static final double SOUTHERN_LATITUDE = 15.36;
    private static final double NORTHERN_LATITUDE = 45.03;
    private static final double EARLIEST_DATA_TIME = 1421150400.000;
    private static final double LATEST_DATA_TIME = 1422532800.000;
    private static final String REGION_NAME = "Bering Sea";
    private static final String LOCATION_IN_REGION = "ocean floor";

    @Test
    public void testGetSetWesternLongitude() {
        Coverage coverage = new Coverage();
        assertTrue(coverage.getWesternLongitude().isNaN());
        coverage.setWesternLongitude(WESTERN_LONGITUDE);
        assertEquals(WESTERN_LONGITUDE, coverage.getWesternLongitude(), DELTA);
        coverage.setWesternLongitude(null);
        assertTrue(coverage.getWesternLongitude().isNaN());
        coverage.setWesternLongitude(WESTERN_LONGITUDE);
        assertEquals(WESTERN_LONGITUDE, coverage.getWesternLongitude(), DELTA);
        coverage.setWesternLongitude(-600.0);
        assertTrue(coverage.getWesternLongitude().isNaN());
    }

    @Test
    public void testGetSetEasternLongitude() {
        Coverage coverage = new Coverage();
        assertTrue(coverage.getEasternLongitude().isNaN());
        coverage.setEasternLongitude(EASTERN_LONGITUDE);
        assertEquals(EASTERN_LONGITUDE, coverage.getEasternLongitude(), DELTA);
        assertTrue(coverage.getWesternLongitude().isNaN());
        coverage.setEasternLongitude(null);
        assertTrue(coverage.getEasternLongitude().isNaN());
        coverage.setEasternLongitude(EASTERN_LONGITUDE);
        assertEquals(EASTERN_LONGITUDE, coverage.getEasternLongitude(), DELTA);
        coverage.setEasternLongitude(600.0);
        assertTrue(coverage.getEasternLongitude().isNaN());
    }

    @Test
    public void testGetSetSouthernLatitude() {
        Coverage coverage = new Coverage();
        assertTrue(coverage.getSouthernLatitude().isNaN());
        coverage.setSouthernLatitude(SOUTHERN_LATITUDE);
        assertEquals(SOUTHERN_LATITUDE, coverage.getSouthernLatitude(), DELTA);
        assertTrue(coverage.getEasternLongitude().isNaN());
        assertTrue(coverage.getWesternLongitude().isNaN());
        coverage.setSouthernLatitude(null);
        assertTrue(coverage.getSouthernLatitude().isNaN());
        coverage.setSouthernLatitude(SOUTHERN_LATITUDE);
        assertEquals(SOUTHERN_LATITUDE, coverage.getSouthernLatitude(), DELTA);
        coverage.setSouthernLatitude(-100.0);
        assertTrue(coverage.getSouthernLatitude().isNaN());
    }

    @Test
    public void testGetSetNorthernLatitude() {
        Coverage coverage = new Coverage();
        assertTrue(coverage.getNorthernLatitude().isNaN());
        coverage.setNorthernLatitude(NORTHERN_LATITUDE);
        assertEquals(NORTHERN_LATITUDE, coverage.getNorthernLatitude(), DELTA);
        assertTrue(coverage.getSouthernLatitude().isNaN());
        assertTrue(coverage.getEasternLongitude().isNaN());
        assertTrue(coverage.getWesternLongitude().isNaN());
        coverage.setNorthernLatitude(null);
        assertTrue(coverage.getNorthernLatitude().isNaN());
        coverage.setNorthernLatitude(NORTHERN_LATITUDE);
        assertEquals(NORTHERN_LATITUDE, coverage.getNorthernLatitude(), DELTA);
        coverage.setNorthernLatitude(100.0);
        assertTrue(coverage.getNorthernLatitude().isNaN());
    }

    @Test
    public void testGetSetEarliestDataTime() {
        Coverage coverage = new Coverage();
        assertTrue(coverage.getEarliestDataTime().isNaN());
        coverage.setEarliestDataTime(EARLIEST_DATA_TIME);
        assertEquals(EARLIEST_DATA_TIME, coverage.getEarliestDataTime(), DELTA);
        assertTrue(coverage.getNorthernLatitude().isNaN());
        assertTrue(coverage.getSouthernLatitude().isNaN());
        assertTrue(coverage.getEasternLongitude().isNaN());
        assertTrue(coverage.getWesternLongitude().isNaN());
        coverage.setEarliestDataTime(null);
        assertTrue(coverage.getEarliestDataTime().isNaN());
        coverage.setEarliestDataTime(EARLIEST_DATA_TIME);
        assertEquals(EARLIEST_DATA_TIME, coverage.getEarliestDataTime(), DELTA);
        coverage.setEarliestDataTime(Coverage.MIN_DATA_TIME - 60.0);
        assertTrue(coverage.getEarliestDataTime().isNaN());
    }

    @Test
    public void testGetSetLatestDataTime() {
        Coverage coverage = new Coverage();
        assertTrue(coverage.getLatestDataTime().isNaN());
        coverage.setLatestDataTime(LATEST_DATA_TIME);
        assertEquals(LATEST_DATA_TIME, coverage.getLatestDataTime(), DELTA);
        assertTrue(coverage.getEarliestDataTime().isNaN());
        assertTrue(coverage.getNorthernLatitude().isNaN());
        assertTrue(coverage.getSouthernLatitude().isNaN());
        assertTrue(coverage.getEasternLongitude().isNaN());
        assertTrue(coverage.getWesternLongitude().isNaN());
        coverage.setLatestDataTime(null);
        assertTrue(coverage.getLatestDataTime().isNaN());
        coverage.setLatestDataTime(LATEST_DATA_TIME);
        assertEquals(LATEST_DATA_TIME, coverage.getLatestDataTime(), DELTA);
        coverage.setLatestDataTime(System.currentTimeMillis() / 1000.0 + 60.0);
        assertTrue(coverage.getLatestDataTime().isNaN());
    }

    @Test
    public void testGetSetRegionName() {
        Coverage coverage = new Coverage();
        assertEquals("", coverage.getRegionName());
        coverage.setRegionName(REGION_NAME);
        assertEquals(REGION_NAME, coverage.getRegionName());
        assertTrue(coverage.getLatestDataTime().isNaN());
        assertTrue(coverage.getEarliestDataTime().isNaN());
        assertTrue(coverage.getNorthernLatitude().isNaN());
        assertTrue(coverage.getSouthernLatitude().isNaN());
        assertTrue(coverage.getEasternLongitude().isNaN());
        assertTrue(coverage.getWesternLongitude().isNaN());
        coverage.setRegionName(null);
        assertEquals("", coverage.getRegionName());
    }

    @Test
    public void testGetSetLocationInRegion() {
        Coverage coverage = new Coverage();
        assertEquals("", coverage.getLocationInRegion());
        coverage.setLocationInRegion(LOCATION_IN_REGION);
        assertEquals(LOCATION_IN_REGION, coverage.getLocationInRegion());
        assertEquals("", coverage.getRegionName());
        assertTrue(coverage.getLatestDataTime().isNaN());
        assertTrue(coverage.getEarliestDataTime().isNaN());
        assertTrue(coverage.getNorthernLatitude().isNaN());
        assertTrue(coverage.getSouthernLatitude().isNaN());
        assertTrue(coverage.getEasternLongitude().isNaN());
        assertTrue(coverage.getWesternLongitude().isNaN());
        coverage.setLocationInRegion(null);
        assertEquals("", coverage.getLocationInRegion());
    }

    @Test
    public void testCoverage() {
        Coverage coverage = new Coverage(null, null, null, null, null, null, null, null);
        assertEquals(new Coverage(), coverage);
        coverage = new Coverage(WESTERN_LONGITUDE, EASTERN_LONGITUDE, SOUTHERN_LATITUDE, NORTHERN_LATITUDE,
                EARLIEST_DATA_TIME, LATEST_DATA_TIME, REGION_NAME, LOCATION_IN_REGION);
        assertEquals(WESTERN_LONGITUDE, coverage.getWesternLongitude(), DELTA);
        assertEquals(EASTERN_LONGITUDE, coverage.getEasternLongitude(), DELTA);
        assertEquals(SOUTHERN_LATITUDE, coverage.getSouthernLatitude(), DELTA);
        assertEquals(NORTHERN_LATITUDE, coverage.getNorthernLatitude(), DELTA);
        assertEquals(EARLIEST_DATA_TIME, coverage.getEarliestDataTime(), DELTA);
        assertEquals(LATEST_DATA_TIME, coverage.getLatestDataTime(), DELTA);
        assertEquals(REGION_NAME, coverage.getRegionName());
        assertEquals(LOCATION_IN_REGION, coverage.getLocationInRegion());
    }

    @Test
    public void testIsValid() {
        Coverage coverage = new Coverage();
        assertFalse(coverage.isValid());

        coverage.setWesternLongitude(WESTERN_LONGITUDE);
        coverage.setEasternLongitude(EASTERN_LONGITUDE);
        coverage.setSouthernLatitude(SOUTHERN_LATITUDE);
        coverage.setNorthernLatitude(NORTHERN_LATITUDE);
        coverage.setEarliestDataTime(EARLIEST_DATA_TIME);
        coverage.setLatestDataTime(LATEST_DATA_TIME);
        assertTrue(coverage.isValid());

        coverage.setWesternLongitude(EASTERN_LONGITUDE);
        coverage.setEasternLongitude(WESTERN_LONGITUDE);
        assertTrue(coverage.isValid());

        coverage.setSouthernLatitude(NORTHERN_LATITUDE);
        coverage.setNorthernLatitude(SOUTHERN_LATITUDE);
        assertFalse(coverage.isValid());
        coverage.setSouthernLatitude(SOUTHERN_LATITUDE);
        coverage.setNorthernLatitude(NORTHERN_LATITUDE);
        assertTrue(coverage.isValid());

        coverage.setEarliestDataTime(LATEST_DATA_TIME);
        coverage.setLatestDataTime(EARLIEST_DATA_TIME);
        assertFalse(coverage.isValid());
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
        coverage.setRegionName(REGION_NAME);
        coverage.setLocationInRegion(LOCATION_IN_REGION);
        assertNotEquals(coverage, dup);

        dup = coverage.clone();
        assertEquals(coverage, dup);
        assertNotSame(coverage, dup);
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

        first.setRegionName(REGION_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setRegionName(REGION_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setLocationInRegion(LOCATION_IN_REGION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLocationInRegion(LOCATION_IN_REGION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}

