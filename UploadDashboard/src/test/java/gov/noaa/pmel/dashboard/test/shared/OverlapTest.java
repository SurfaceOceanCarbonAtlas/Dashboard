package gov.noaa.pmel.dashboard.test.shared;

import gov.noaa.pmel.dashboard.shared.Overlap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link Overlap}
 *
 * @author Karl Smith
 */
public class OverlapTest {

    private static final String firstExpo = "AAAA19951201";
    private static final String secondExpo = "BBBB19951207";
    private static final ArrayList<Integer> firstRowNums = new ArrayList<Integer>(Arrays.asList(47, 48, 49, 50, 51));
    private static final ArrayList<Integer> secondRowNums = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
    private static final ArrayList<Double> lons = new ArrayList<Double>(
            Arrays.asList(125.25, 125.50, 125.75, 126.00, 126.25));
    private static final ArrayList<Double> lats = new ArrayList<Double>(
            Arrays.asList(45.35, 45.30, 45.25, 45.30, 45.35));
    private static final ArrayList<Double> times = new ArrayList<Double>(
            Arrays.asList(987654321.0, 987754321.0, 987854321.0, 987954321.0, 988054321.0));

    /**
     * Test method for {@link Overlap#getDatasetIds} and {@link Overlap#setDatasetIds(String[])}.
     */
    @Test
    public void testGetSetDatasetIds() {
        Overlap olap = new Overlap();
        String[] expos = olap.getDatasetIds();
        assertEquals(2, expos.length);
        assertTrue(expos[0].isEmpty());
        assertTrue(expos[1].isEmpty());

        olap.setDatasetIds(new String[] { firstExpo, secondExpo });
        expos = olap.getDatasetIds();
        assertEquals(2, expos.length);
        assertEquals(firstExpo, expos[0]);
        assertEquals(secondExpo, expos[1]);

        olap.setDatasetIds(null);
        expos = olap.getDatasetIds();
        assertEquals(2, expos.length);
        assertTrue(expos[0].isEmpty());
        assertTrue(expos[1].isEmpty());
    }

    /**
     * Test method for {@link Overlap#getRowNums} and {@link Overlap#setRowNums}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetSetRowNums() {
        Overlap olap = new Overlap();
        ArrayList<Integer>[] rowNums = olap.getRowNums();
        assertEquals(2, rowNums.length);
        assertEquals(0, rowNums[0].size());
        assertEquals(0, rowNums[1].size());

        olap.setRowNums(new ArrayList[] { firstRowNums, secondRowNums });
        rowNums = olap.getRowNums();
        assertEquals(2, rowNums.length);
        assertEquals(firstRowNums, rowNums[0]);
        assertEquals(secondRowNums, rowNums[1]);

        String[] expos = olap.getDatasetIds();
        assertEquals(2, expos.length);
        assertTrue(expos[0].isEmpty());
        assertTrue(expos[1].isEmpty());

        olap.setRowNums(null);
        rowNums = olap.getRowNums();
        assertEquals(2, rowNums.length);
        assertEquals(0, rowNums[0].size());
        assertEquals(0, rowNums[1].size());
    }

    /**
     * Test method for {@link Overlap#getLons} and {@link Overlap#setLons}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetSetLons() {
        Overlap olap = new Overlap();
        ArrayList<Double>[] mylons = olap.getLons();
        assertEquals(2, mylons.length);
        assertEquals(0, mylons[0].size());
        assertEquals(0, mylons[1].size());

        olap.setLons(new ArrayList[] { lons, lons });
        assertEquals(2, mylons.length);
        assertEquals(lons, mylons[0]);
        assertEquals(lons, mylons[1]);

        ArrayList<Integer>[] rowNums = olap.getRowNums();
        assertEquals(2, rowNums.length);
        assertEquals(0, rowNums[0].size());
        assertEquals(0, rowNums[1].size());

        String[] expos = olap.getDatasetIds();
        assertEquals(2, expos.length);
        assertTrue(expos[0].isEmpty());
        assertTrue(expos[1].isEmpty());

        olap.setLons(null);
        mylons = olap.getLons();
        assertEquals(2, mylons.length);
        assertEquals(0, mylons[0].size());
        assertEquals(0, mylons[1].size());
    }

    /**
     * Test method for {@link Overlap#getLats} and {@link Overlap#setLats}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetSetLats() {
        Overlap olap = new Overlap();
        ArrayList<Double>[] mylats = olap.getLats();
        assertEquals(2, mylats.length);
        assertEquals(0, mylats[0].size());
        assertEquals(0, mylats[1].size());

        olap.setLats(new ArrayList[] { lats, lats });
        mylats = olap.getLats();
        assertEquals(2, mylats.length);
        assertEquals(lats, mylats[0]);
        assertEquals(lats, mylats[1]);

        ArrayList<Double>[] mylons = olap.getLons();
        assertEquals(2, mylons.length);
        assertEquals(0, mylons[0].size());
        assertEquals(0, mylons[1].size());

        ArrayList<Integer>[] rowNums = olap.getRowNums();
        assertEquals(2, rowNums.length);
        assertEquals(0, rowNums[0].size());
        assertEquals(0, rowNums[1].size());

        String[] expos = olap.getDatasetIds();
        assertEquals(2, expos.length);
        assertTrue(expos[0].isEmpty());
        assertTrue(expos[1].isEmpty());

        olap.setLats(null);
        mylats = olap.getLats();
        assertEquals(2, mylats.length);
        assertEquals(0, mylats[0].size());
        assertEquals(0, mylats[1].size());
    }

    /**
     * Test method for {@link Overlap#getTimes} and {@link Overlap#setTimes}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetSetTimes() {
        Overlap olap = new Overlap();
        ArrayList<Double>[] mytimes = olap.getTimes();
        assertEquals(2, mytimes.length);
        assertEquals(0, mytimes[0].size());
        assertEquals(0, mytimes[1].size());

        olap.setTimes(new ArrayList[] { times, times });
        mytimes = olap.getTimes();
        assertEquals(2, mytimes.length);
        assertEquals(times, mytimes[0]);
        assertEquals(times, mytimes[1]);

        ArrayList<Double>[] mylats = olap.getLats();
        assertEquals(2, mylats.length);
        assertEquals(0, mylats[0].size());
        assertEquals(0, mylats[1].size());

        ArrayList<Double>[] mylons = olap.getLons();
        assertEquals(2, mylons.length);
        assertEquals(0, mylons[0].size());
        assertEquals(0, mylons[1].size());

        ArrayList<Integer>[] rowNums = olap.getRowNums();
        assertEquals(2, rowNums.length);
        assertEquals(0, rowNums[0].size());
        assertEquals(0, rowNums[1].size());

        String[] expos = olap.getDatasetIds();
        assertEquals(2, expos.length);
        assertTrue(expos[0].isEmpty());
        assertTrue(expos[1].isEmpty());

        olap.setTimes(null);
        mytimes = olap.getTimes();
        assertEquals(2, mytimes.length);
        assertEquals(0, mytimes[0].size());
        assertEquals(0, mytimes[1].size());
    }

    /**
     * Test method for {@link Overlap#isEmpty()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testIsEmpty() {
        Overlap oerlap = new Overlap();
        assertTrue(oerlap.isEmpty());

        oerlap = new Overlap(firstExpo, secondExpo);
        assertTrue(oerlap.isEmpty());

        oerlap.addDuplicatePoint(firstRowNums.get(0), secondRowNums.get(0),
                lons.get(0), lons.get(0), lats.get(0), lats.get(0), times.get(0), times.get(0));
        assertFalse(oerlap.isEmpty());

        oerlap = new Overlap(firstExpo, secondExpo);
        oerlap.setRowNums(new ArrayList[] { firstRowNums, secondRowNums });
        assertFalse(oerlap.isEmpty());

        oerlap = new Overlap(firstExpo, secondExpo);
        oerlap.setLons(new ArrayList[] { lons, lons });
        assertFalse(oerlap.isEmpty());

        oerlap = new Overlap(firstExpo, secondExpo);
        oerlap.setLats(new ArrayList[] { lats, lats });
        assertFalse(oerlap.isEmpty());

        oerlap = new Overlap(firstExpo, secondExpo);
        oerlap.setTimes(new ArrayList[] { times, times });
        assertFalse(oerlap.isEmpty());
    }

    /**
     * Test method for {@link Overlap#hashCode()} and {@link Overlap#equals(java.lang.Object)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testHashCodeEqualsObject() {
        Overlap first = new Overlap();
        assertFalse(first.equals(null));
        assertFalse(first.equals(firstExpo));

        Overlap second = new Overlap();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setDatasetIds(new String[] { firstExpo, secondExpo });
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setDatasetIds(new String[] { firstExpo, secondExpo });
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setRowNums(new ArrayList[] { firstRowNums, secondRowNums });
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setRowNums(new ArrayList[] { firstRowNums, secondRowNums });
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setLons(new ArrayList[] { lons, lons });
        // hashCode ignores floating-point values but pays attention to the number of values
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLons(new ArrayList[] { lons, lons });
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setLats(new ArrayList[] { lats, lats });
        // hashCode ignores floating-point values but pays attention to the number of values
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLats(new ArrayList[] { lats, lats });
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setTimes(new ArrayList[] { times, times });
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setTimes(new ArrayList[] { times, times });
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

    /**
     * Test method for {@link Overlap#addDuplicatePoint} and {@link Overlap#Overlap(String, String)}
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAddDuplicatePoint() {
        Overlap first = new Overlap();
        first.setDatasetIds(new String[] { firstExpo, secondExpo });
        first.setRowNums(new ArrayList[] { firstRowNums, secondRowNums });
        first.setLons(new ArrayList[] { lons, lons });
        first.setLats(new ArrayList[] { lats, lats });
        first.setTimes(new ArrayList[] { times, times });

        Overlap second = new Overlap(firstExpo, secondExpo);
        for (int k = 0; k < firstRowNums.size(); k++) {
            second.addDuplicatePoint(firstRowNums.get(k), secondRowNums.get(k),
                    lons.get(k), lons.get(k), lats.get(k), lats.get(k), times.get(k), times.get(k));
        }

        assertEquals(first, second);
    }

    /**
     * Test method for {@link Overlap#compareTo(Overlap)}
     */
    @Test
    public void testCompare() {
        Overlap first = new Overlap();
        Overlap second = new Overlap();
        assertEquals(0, first.compareTo(second));

        first = new Overlap(firstExpo, firstExpo);
        second = new Overlap(firstExpo, secondExpo);
        assertTrue(first.compareTo(second) < 0);
        second = new Overlap(secondExpo, firstExpo);
        assertTrue(first.compareTo(second) < 0);
        second.addDuplicatePoint(firstRowNums.get(0), secondRowNums.get(0),
                lons.get(0), lons.get(0), lats.get(0), lats.get(0), times.get(0), times.get(0));
        assertTrue(first.compareTo(second) < 0);
        second = new Overlap(secondExpo, secondExpo);
        assertEquals(firstExpo.compareTo(secondExpo), first.compareTo(second));

        first = new Overlap(firstExpo, secondExpo);
        second = new Overlap(secondExpo, firstExpo);
        assertEquals(firstExpo.compareTo(secondExpo), first.compareTo(second));

        first.addDuplicatePoint(firstRowNums.get(0), secondRowNums.get(0),
                lons.get(0), lons.get(0), lats.get(0), lats.get(0), times.get(0), times.get(0));
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);

        second.addDuplicatePoint(firstRowNums.get(0), secondRowNums.get(0),
                lons.get(0), lons.get(0), lats.get(0), lats.get(0), times.get(0), times.get(0));
        second.addDuplicatePoint(firstRowNums.get(1), secondRowNums.get(1),
                lons.get(1), lons.get(1), lats.get(1), lats.get(1), times.get(1), times.get(1));
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);

        second = new Overlap(firstExpo, secondExpo);
        assertTrue(first.compareTo(second) > 0);
        assertTrue(second.compareTo(first) < 0);
        second.addDuplicatePoint(firstRowNums.get(0), secondRowNums.get(0),
                lons.get(0), lons.get(0) + 360.0, lats.get(0), lats.get(0) + 1.0E-8, times.get(0), times.get(0));
        assertEquals(0, first.compareTo(second));
        assertEquals(0, second.compareTo(first));
    }

}
