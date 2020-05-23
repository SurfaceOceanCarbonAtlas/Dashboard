package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.core.MultiNames;
import org.junit.Test;

import java.util.Arrays;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MultiNamesTest {

    private static final String FIRST = "A name that should be first";
    private static final String SECOND = "This name is between the others";
    private static final String THIRD = "Zis name should be last";
    private static final String SEPARATOR = ", ";

    @Test
    public void testMultiNames() {
        MultiNames names = new MultiNames();
        assertNotNull(names);
        assertTrue(names.isEmpty());
        names.add(THIRD);
        assertFalse(names.isEmpty());
        names.add(SECOND);
        names.add(THIRD);
        names.add(FIRST);
        names.add(SECOND);
        assertEquals(FIRST, names.pop());
        assertEquals(SECOND, names.pop());
        names.clear();
        assertTrue(names.isEmpty());
        assertNull(names.pop());

        names = new MultiNames(THIRD + SEPARATOR + SECOND + SEPARATOR +
                SEPARATOR + THIRD + SEPARATOR + FIRST + SEPARATOR + SECOND + SEPARATOR + SEPARATOR);
        assertEquals(FIRST, names.pop());
        assertEquals(SECOND, names.pop());
        assertEquals(THIRD, names.pop());
        assertNull(names.pop());

        names = new MultiNames((String) null);
        assertNotNull(names);
        assertTrue(names.isEmpty());

        names = new MultiNames(SECOND + SEPARATOR + SEPARATOR + THIRD + SEPARATOR + FIRST + SEPARATOR);
        MultiNames other = new MultiNames(names);
        names.clear();
        assertTrue(names.isEmpty());
        assertNull(names.pop());

        assertFalse(other.isEmpty());
        assertEquals(FIRST, other.pop());
        assertEquals(SECOND, other.pop());
        assertEquals(THIRD, other.pop());
        assertNull(other.pop());

        names = new MultiNames((MultiNames) null);
        assertNotNull(names);
        assertTrue(names.isEmpty());
    }

    @Test
    public void testAsOneString() {
        MultiNames names = new MultiNames();
        assertEquals("", names.asOneString());
        names.add(THIRD);
        assertEquals(THIRD, names.asOneString());
        names.add(FIRST);
        assertEquals(FIRST + SEPARATOR + THIRD, names.asOneString());
        names.add(SECOND);
        assertEquals(FIRST + SEPARATOR + SECOND + SEPARATOR + THIRD, names.asOneString());
        names.pop();
        assertEquals(SECOND + SEPARATOR + THIRD, names.asOneString());
        names.pop();
        assertEquals(THIRD, names.asOneString());
        names.clear();
        assertEquals("", names.asOneString());
    }

    @Test
    public void testContains() {
        MultiNames names = new MultiNames();
        assertFalse(names.contains(null));
        assertFalse(names.contains(""));
        names.add(THIRD);
        assertFalse(names.contains(null));
        assertFalse(names.contains(""));
        assertTrue(names.contains(THIRD));
        names.add(FIRST);
        names.add(SECOND);
        assertTrue(names.contains(FIRST));
        assertTrue(names.contains(SECOND));
        assertTrue(names.contains(THIRD));
        names.pop();
        assertFalse(names.contains(FIRST));
        assertTrue(names.contains(SECOND));
        assertTrue(names.contains(THIRD));
        names.pop();
        assertFalse(names.contains(FIRST));
        assertFalse(names.contains(SECOND));
        assertTrue(names.contains(THIRD));
        names.clear();
        assertFalse(names.contains(FIRST));
        assertFalse(names.contains(SECOND));
        assertFalse(names.contains(THIRD));
        assertFalse(names.contains(""));
        assertFalse(names.contains(null));
    }

    @Test
    public void testIterator() {
        MultiNames names = new MultiNames(SECOND + SEPARATOR + FIRST + SEPARATOR + SECOND + SEPARATOR + THIRD);
        String expected = null;
        for (String value : names) {
            if ( expected == null )
                expected = FIRST;
            else if ( FIRST.equals(expected) )
                expected = SECOND;
            else if ( SECOND.equals(expected) )
                expected = THIRD;
            else
                fail("Iteration went further than expected");
            assertEquals(expected, value);
        }
    }

    @Test
    public void testGetSetNameSet() {
        MultiNames names = new MultiNames();
        assertEquals(new TreeSet<String>(), names.getNameSet());

        TreeSet<String> expectedSet = new TreeSet<String>(Arrays.asList(FIRST, SECOND, THIRD));
        names.setNameSet(expectedSet);
        TreeSet<String> nameSet = names.getNameSet();
        assertEquals(expectedSet, nameSet);
        assertNotSame(expectedSet, nameSet);
        names.clear();
        assertEquals(new TreeSet<String>(), names.getNameSet());

        names = new MultiNames(FIRST + SEPARATOR + SECOND + SEPARATOR + THIRD);
        assertEquals(expectedSet, names.getNameSet());
    }

    @Test
    public void testHashCodeEquals() {
        MultiNames names = new MultiNames();
        assertFalse(names.equals(null));
        assertFalse(names.equals(new TreeSet<String>()));

        MultiNames other = new MultiNames();
        assertEquals(names.hashCode(), other.hashCode());
        assertTrue(names.equals(other));

        names.add(FIRST);
        assertNotEquals(names.hashCode(), other.hashCode());
        assertFalse(names.equals(other));

        other.add(SECOND);
        assertNotEquals(names.hashCode(), other.hashCode());
        assertFalse(names.equals(other));

        names.add(SECOND);
        assertNotEquals(names.hashCode(), other.hashCode());
        assertFalse(names.equals(other));

        other.add(FIRST);
        assertEquals(names.hashCode(), other.hashCode());
        assertTrue(names.equals(other));

        other.add(SECOND);
        assertEquals(names.hashCode(), other.hashCode());
        assertTrue(names.equals(other));
    }

}
