package gov.noaa.pmel.sdimetadata.test.coverage;

import gov.noaa.pmel.sdimetadata.coverage.Coverage;
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
    private static final double EARLIEST_DATA_TIME = 123456.78;
    private static final double LATEST_DATA_TIME = 234567.89;

    @Test
    public void testGetSetWesternLongitude() {
        Coverage coverage = new Coverage();
        assertTrue(coverage.getWesternLongitude().isNaN());
        coverage.setWesternLongitude(WESTERN_LONGITUDE);
        assertEquals(WESTERN_LONGITUDE, coverage.getWesternLongitude(), DELTA);
        coverage.setWesternLongitude(null);
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
    }

    @Test
    public void testHashCodeEquals() {
        Coverage first = new Coverage();
        assertFalse(first.equals(null));
        assertFalse(first.equals(WESTERN_LONGITUDE));

        Coverage second = new Coverage();
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setWesternLongitude(WESTERN_LONGITUDE);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setWesternLongitude(WESTERN_LONGITUDE);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setEasternLongitude(EASTERN_LONGITUDE);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setEasternLongitude(EASTERN_LONGITUDE);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setSouthernLatitude(SOUTHERN_LATITUDE);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setSouthernLatitude(SOUTHERN_LATITUDE);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setNorthernLatitude(NORTHERN_LATITUDE);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setNorthernLatitude(NORTHERN_LATITUDE);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setEarliestDataTime(EARLIEST_DATA_TIME);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setEarliestDataTime(EARLIEST_DATA_TIME);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setLatestDataTime(LATEST_DATA_TIME);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse(first.equals(second));
        second.setLatestDataTime(LATEST_DATA_TIME);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));
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
        assertNotEquals(coverage, dup);

        dup = coverage.clone();
        assertEquals(coverage, dup);
        assertNotSame(coverage, dup);
    }

}

