package gov.noaa.pmel.dashboard.test.server;

import gov.noaa.pmel.dashboard.server.RowColumn;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for method of {@link RowColumn}
 */
public class RowColumnTest {

    /**
     * Test of {@link RowColumn#compareTo(RowColumn)} and {@link RowColumn#RowColumn(Integer, Integer)}
     */
    @Test
    public void testCompareTo() {
        RowColumn nulnul = new RowColumn(null, null);
        RowColumn nulzero = new RowColumn(null, 0);
        RowColumn zeronul = new RowColumn(0, null);
        RowColumn zerozero = new RowColumn(0, 0);
        RowColumn onezero = new RowColumn(1, 0);
        RowColumn twozero = new RowColumn(2, 0);
        RowColumn zeroone = new RowColumn(0, 1);
        RowColumn oneone = new RowColumn(1, 1);
        RowColumn twoone = new RowColumn(2, 1);
        RowColumn zerotwo = new RowColumn(0, 2);
        RowColumn onetwo = new RowColumn(1, 2);

        // check ordering of null
        assertEquals(0, nulnul.compareTo(nulnul));
        assertTrue(nulnul.compareTo(nulzero) < 0);
        assertTrue(nulzero.compareTo(nulnul) > 0);
        assertTrue(nulnul.compareTo(zeronul) < 0);
        assertTrue(zeronul.compareTo(nulnul) > 0);
        assertTrue(nulnul.compareTo(zerozero) < 0);
        assertTrue(zerozero.compareTo(nulnul) > 0);
        assertTrue(zeronul.compareTo(nulzero) < 0);
        assertTrue(nulzero.compareTo(zeronul) > 0);

        assertEquals(0, zerozero.compareTo(zerozero));

        // check col diff
        assertTrue(oneone.compareTo(onezero) > 0);
        assertTrue(onezero.compareTo(oneone) < 0);
        assertTrue(oneone.compareTo(onetwo) < 0);
        assertTrue(onetwo.compareTo(oneone) > 0);

        // check row diff
        assertTrue(oneone.compareTo(zeroone) > 0);
        assertTrue(zeroone.compareTo(oneone) < 0);
        assertTrue(oneone.compareTo(twoone) < 0);
        assertTrue(twoone.compareTo(oneone) > 0);

        // column first, then row
        assertTrue(oneone.compareTo(twozero) > 0);
        assertTrue(twozero.compareTo(oneone) < 0);
        assertTrue(oneone.compareTo(zerotwo) < 0);
        assertTrue(zerotwo.compareTo(oneone) > 0);
    }


    /**
     * Test of {@link RowColumn#hashCode()} and {@link RowColumn#equals(Object)}
     */
    @Test
    public void testHashCodeEquals() {
        RowColumn first = new RowColumn(null, null);
        assertFalse(first.equals(null));
        assertFalse(first.equals(DashboardUtils.INT_MISSING_VALUE));

        RowColumn second = new RowColumn(null, null);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        second = new RowColumn(DashboardUtils.INT_MISSING_VALUE, DashboardUtils.INT_MISSING_VALUE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        second = new RowColumn(0, 1);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));

        first = new RowColumn(1, 0);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));

        second = new RowColumn(1, 0);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}

