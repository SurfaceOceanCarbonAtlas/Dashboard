package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.Coverage;
import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.core.MultiNames;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

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
    private static final Datestamp EMPTY_DATESTAMP = new Datestamp();
    private static final MultiNames EMPTY_NAMESET = new MultiNames();

    private static final NumericString WESTERN_LONGITUDE = new NumericString("146.23", Coverage.LONGITUDE_UNITS);
    private static final NumericString EASTERN_LONGITUDE = new NumericString("-120.45", Coverage.LONGITUDE_UNITS);
    private static final NumericString SOUTHERN_LATITUDE = new NumericString("15.36", Coverage.LATITUDE_UNITS);
    private static final NumericString NORTHERN_LATITUDE = new NumericString("45.03", Coverage.LATITUDE_UNITS);
    private static final String SPATIAL_REFERENCE = "NAD 83";
    private static final MultiNames GEOGRAPHIC_NAMES = new MultiNames("North Pacific, Tropical Pacific");

    private static final Datestamp START_DATESTAMP = new Datestamp(2015, 1, 5, -1, -1, -1);
    private static final Datestamp EARLIEST_DATA_DATE = new Datestamp(2015, 1, 5, 13, 25, 53);
    private static final Datestamp LATEST_DATA_DATE = new Datestamp(2015, 2, 14, 19, 4, 23);
    private static final Datestamp END_DATESTAMP = new Datestamp(2015, 2, 14, -1, -1, -1);

    private static final Datestamp TODAY;
    private static final Datestamp TOO_LONG_AGO;
    private static final Datestamp IN_THE_FUTURE;

    static {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH mm ss");
        String[] pieces = dateFormat.format(now).split(" ");
        TODAY = new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], pieces[5]);
        try {
            String fullDateString = Datestamp.MIN_VALID_DATESTAMP.dateString().replace('-', ' ') +
                    " " + Datestamp.MIN_VALID_DATESTAMP.timeString().replace(':', ' ');
            Date longAgo = dateFormat.parse(fullDateString);
            longAgo = new Date(longAgo.getTime() - 1000L);
            pieces = dateFormat.format(longAgo).split(" ");
            TOO_LONG_AGO = new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], pieces[5]);
            Date future = new Date(now.getTime() + 1000L);
            pieces = dateFormat.format(future).split(" ");
            IN_THE_FUTURE = new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], pieces[5]);
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
    public void testGetSetEarliestDataDate() {
        Coverage coverage = new Coverage();
        Datestamp dataDate = coverage.getEarliestDataDate();
        assertNotNull(dataDate);
        assertFalse(dataDate.isValid(TODAY));

        coverage.setEarliestDataDate(EARLIEST_DATA_DATE);
        dataDate = coverage.getEarliestDataDate();
        assertEquals(EARLIEST_DATA_DATE, dataDate);
        assertNotSame(EARLIEST_DATA_DATE, dataDate);
        assertTrue(dataDate.isValid(TODAY));
        assertNotSame(dataDate, coverage.getEarliestDataDate());
        assertEquals(EMPTY_LATITUDE, coverage.getNorthernLatitude());
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());

        coverage.setEarliestDataDate(null);
        dataDate = coverage.getEarliestDataDate();
        assertNotNull(dataDate);
        assertFalse(dataDate.isValid(TODAY));

        dataDate = TOO_LONG_AGO;
        coverage.setEarliestDataDate(dataDate);
        assertEquals(dataDate, coverage.getEarliestDataDate());
        assertNotSame(dataDate, coverage.getEarliestDataDate());
        assertFalse(dataDate.isValid(TODAY));

        dataDate = IN_THE_FUTURE;
        coverage.setEarliestDataDate(dataDate);
        assertEquals(dataDate, coverage.getEarliestDataDate());
        assertNotSame(dataDate, coverage.getEarliestDataDate());
        assertFalse(dataDate.isValid(TODAY));
    }

    @Test
    public void testGetSetLatestDataDate() {
        Coverage coverage = new Coverage();
        Datestamp dataDate = coverage.getLatestDataDate();
        assertNotNull(dataDate);
        assertFalse(dataDate.isValid(TODAY));

        coverage.setLatestDataDate(EARLIEST_DATA_DATE);
        dataDate = coverage.getLatestDataDate();
        assertEquals(EARLIEST_DATA_DATE, dataDate);
        assertNotSame(EARLIEST_DATA_DATE, dataDate);
        assertNotSame(dataDate, coverage.getLatestDataDate());
        assertTrue(dataDate.isValid(TODAY));
        dataDate = coverage.getEarliestDataDate();
        assertNotNull(dataDate);
        assertFalse(dataDate.isValid(TODAY));
        assertEquals(EMPTY_LATITUDE, coverage.getNorthernLatitude());
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());

        coverage.setLatestDataDate(null);
        dataDate = coverage.getLatestDataDate();
        assertNotNull(dataDate);
        assertFalse(dataDate.isValid(TODAY));

        dataDate = TOO_LONG_AGO;
        coverage.setLatestDataDate(dataDate);
        assertEquals(dataDate, coverage.getLatestDataDate());
        assertNotSame(dataDate, coverage.getLatestDataDate());
        assertFalse(dataDate.isValid(TODAY));

        dataDate = IN_THE_FUTURE;
        coverage.setLatestDataDate(dataDate);
        assertEquals(dataDate, coverage.getLatestDataDate());
        assertNotSame(dataDate, coverage.getLatestDataDate());
        assertFalse(dataDate.isValid(TODAY));
    }

    @Test
    public void testGetSetStartDatestamp() {
        Coverage coverage = new Coverage();
        assertEquals(EMPTY_DATESTAMP, coverage.getStartDatestamp());

        coverage.setStartDatestamp(START_DATESTAMP);
        Datestamp stamp = coverage.getStartDatestamp();
        assertEquals(START_DATESTAMP, stamp);
        assertNotSame(START_DATESTAMP, stamp);
        assertNotSame(stamp, coverage.getStartDatestamp());
        assertEquals(EMPTY_DATESTAMP, coverage.getLatestDataDate());
        assertEquals(EMPTY_DATESTAMP, coverage.getEarliestDataDate());
        assertEquals(EMPTY_LATITUDE, coverage.getNorthernLatitude());
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());

        coverage.setStartDatestamp(null);
        assertEquals(EMPTY_DATESTAMP, coverage.getStartDatestamp());
        coverage.setStartDatestamp(EMPTY_DATESTAMP);
        assertEquals(EMPTY_DATESTAMP, coverage.getStartDatestamp());
    }

    @Test
    public void testGetSetEndDatestamp() {
        Coverage coverage = new Coverage();
        assertEquals(EMPTY_DATESTAMP, coverage.getEndDatestamp());

        coverage.setEndDatestamp(END_DATESTAMP);
        Datestamp stamp = coverage.getEndDatestamp();
        assertEquals(END_DATESTAMP, stamp);
        assertNotSame(END_DATESTAMP, stamp);
        assertNotSame(stamp, coverage.getEndDatestamp());
        assertEquals(EMPTY_DATESTAMP, coverage.getStartDatestamp());
        assertEquals(EMPTY_DATESTAMP, coverage.getLatestDataDate());
        assertEquals(EMPTY_DATESTAMP, coverage.getEarliestDataDate());
        assertEquals(EMPTY_LATITUDE, coverage.getNorthernLatitude());
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());

        coverage.setEndDatestamp(null);
        assertEquals(EMPTY_DATESTAMP, coverage.getEndDatestamp());
        coverage.setEndDatestamp(EMPTY_DATESTAMP);
        assertEquals(EMPTY_DATESTAMP, coverage.getEndDatestamp());
    }

    @Test
    public void testGetSetSpatialReference() {
        Coverage coverage = new Coverage();
        assertEquals(Coverage.WGS84, coverage.getSpatialReference());
        coverage.setSpatialReference(SPATIAL_REFERENCE);
        assertEquals(SPATIAL_REFERENCE, coverage.getSpatialReference());
        Datestamp dataDate = coverage.getLatestDataDate();
        assertNotNull(dataDate);
        assertFalse(dataDate.isValid(TODAY));
        dataDate = coverage.getEarliestDataDate();
        assertNotNull(dataDate);
        assertFalse(dataDate.isValid(TODAY));
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
        MultiNames nameSet = coverage.getGeographicNames();
        assertEquals(GEOGRAPHIC_NAMES, nameSet);
        assertNotSame(GEOGRAPHIC_NAMES, nameSet);
        assertNotSame(nameSet, coverage.getGeographicNames());
        assertEquals(Coverage.WGS84, coverage.getSpatialReference());
        Datestamp dataDate = coverage.getLatestDataDate();
        assertNotNull(dataDate);
        assertFalse(dataDate.isValid(TODAY));
        dataDate = coverage.getEarliestDataDate();
        assertNotNull(dataDate);
        assertFalse(dataDate.isValid(TODAY));
        assertEquals(EMPTY_LATITUDE, coverage.getNorthernLatitude());
        assertEquals(EMPTY_LATITUDE, coverage.getSouthernLatitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getEasternLongitude());
        assertEquals(EMPTY_LONGITUDE, coverage.getWesternLongitude());
        coverage.setGeographicNames(null);
        assertEquals(EMPTY_NAMESET, coverage.getGeographicNames());
        coverage.setSpatialReference("\t");
        assertEquals(EMPTY_NAMESET, coverage.getGeographicNames());
    }

    @Test
    public void testInvalidFieldNames() {
        Coverage coverage = new Coverage();
        assertEquals(new HashSet<String>(Arrays.asList("westernLongitude", "easternLongitude", "southernLatitude",
                "northernLatitude", "earliestDataDate", "latestDataDate", "startDatestamp", "endDatestamp")),
                coverage.invalidFieldNames(TODAY));

        coverage.setWesternLongitude(WESTERN_LONGITUDE);
        coverage.setEasternLongitude(EASTERN_LONGITUDE);
        assertEquals(new HashSet<String>(Arrays.asList("southernLatitude", "northernLatitude", "earliestDataDate",
                "latestDataDate", "startDatestamp", "endDatestamp")), coverage.invalidFieldNames(TODAY));

        coverage.setSouthernLatitude(SOUTHERN_LATITUDE);
        coverage.setNorthernLatitude(NORTHERN_LATITUDE);
        assertEquals(new HashSet<String>(Arrays.asList("earliestDataDate", "latestDataDate",
                "startDatestamp", "endDatestamp")), coverage.invalidFieldNames(TODAY));

        coverage.setEarliestDataDate(EARLIEST_DATA_DATE);
        coverage.setLatestDataDate(LATEST_DATA_DATE);
        assertEquals(new HashSet<String>(Arrays.asList("startDatestamp", "endDatestamp")),
                coverage.invalidFieldNames(TODAY));

        coverage.setStartDatestamp(START_DATESTAMP);
        coverage.setEndDatestamp(END_DATESTAMP);
        assertEquals(new HashSet<String>(), coverage.invalidFieldNames(TODAY));

        coverage.setWesternLongitude(EASTERN_LONGITUDE);
        coverage.setEasternLongitude(WESTERN_LONGITUDE);
        assertEquals(new HashSet<String>(), coverage.invalidFieldNames(TODAY));

        coverage.setSouthernLatitude(NORTHERN_LATITUDE);
        coverage.setNorthernLatitude(SOUTHERN_LATITUDE);
        assertEquals(new HashSet<String>(Arrays.asList(
                "southernLatitude", "northernLatitude")), coverage.invalidFieldNames(TODAY));
        coverage.setSouthernLatitude(SOUTHERN_LATITUDE);
        coverage.setNorthernLatitude(NORTHERN_LATITUDE);
        assertEquals(new HashSet<String>(), coverage.invalidFieldNames(TODAY));

        coverage.setEarliestDataDate(LATEST_DATA_DATE);
        coverage.setLatestDataDate(EARLIEST_DATA_DATE);
        assertEquals(new HashSet<String>(Arrays.asList(
                "earliestDataDate", "latestDataDate")), coverage.invalidFieldNames(TODAY));
        coverage.setEarliestDataDate(EARLIEST_DATA_DATE);
        coverage.setLatestDataDate(LATEST_DATA_DATE);
        assertEquals(new HashSet<String>(), coverage.invalidFieldNames(TODAY));

        coverage.setStartDatestamp(LATEST_DATA_DATE);
        coverage.setEndDatestamp(EARLIEST_DATA_DATE);
        assertEquals(new HashSet<String>(Arrays.asList("startDatestamp", "endDatestamp")),
                coverage.invalidFieldNames(TODAY));
        coverage.setStartDatestamp(EARLIEST_DATA_DATE);
        coverage.setEndDatestamp(LATEST_DATA_DATE);
        assertEquals(new HashSet<String>(), coverage.invalidFieldNames(TODAY));
    }

    @Test
    public void testDuplicate() {
        Coverage coverage = new Coverage();
        Coverage dup = (Coverage) (coverage.duplicate(null));
        assertEquals(coverage, dup);
        assertNotSame(coverage, dup);

        coverage.setWesternLongitude(WESTERN_LONGITUDE);
        coverage.setEasternLongitude(EASTERN_LONGITUDE);
        coverage.setSouthernLatitude(SOUTHERN_LATITUDE);
        coverage.setNorthernLatitude(NORTHERN_LATITUDE);
        coverage.setEarliestDataDate(EARLIEST_DATA_DATE);
        coverage.setLatestDataDate(LATEST_DATA_DATE);
        coverage.setStartDatestamp(START_DATESTAMP);
        coverage.setEndDatestamp(END_DATESTAMP);
        coverage.setSpatialReference(SPATIAL_REFERENCE);
        coverage.setGeographicNames(GEOGRAPHIC_NAMES);
        assertNotEquals(coverage, dup);

        dup = (Coverage) (coverage.duplicate(null));
        assertEquals(coverage, dup);
        assertNotSame(coverage, dup);
        assertNotSame(coverage.getWesternLongitude(), dup.getWesternLongitude());
        assertNotSame(coverage.getEasternLongitude(), dup.getEasternLongitude());
        assertNotSame(coverage.getSouthernLatitude(), dup.getSouthernLatitude());
        assertNotSame(coverage.getNorthernLatitude(), dup.getNorthernLatitude());
        assertNotSame(coverage.getEarliestDataDate(), dup.getEarliestDataDate());
        assertNotSame(coverage.getStartDatestamp(), dup.getStartDatestamp());
        assertNotSame(coverage.getEndDatestamp(), dup.getEndDatestamp());
        assertNotSame(coverage.getLatestDataDate(), dup.getLatestDataDate());
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

        first.setEarliestDataDate(EARLIEST_DATA_DATE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setEarliestDataDate(EARLIEST_DATA_DATE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setLatestDataDate(LATEST_DATA_DATE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLatestDataDate(LATEST_DATA_DATE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setStartDatestamp(START_DATESTAMP);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setStartDatestamp(START_DATESTAMP);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setEndDatestamp(END_DATESTAMP);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setEndDatestamp(END_DATESTAMP);
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
