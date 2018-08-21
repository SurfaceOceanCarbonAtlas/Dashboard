package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class SubmitterTest {

    private static final String LAST_NAME = "Smith";
    private static final String FIRST_NAME = "John";
    private static final String INITIALS = "D.Z.";
    private static final String ORGANIZATION = "NOAA/PMEL";
    private static final String ADDRESS = "123 Main St, Seattle WA 98101";
    private static final String PHONE = "206-555-6789";
    private static final String EMAIL = "JDZSmith@nowhere.com";

    @Test
    public void testIsValid() {
        Submitter submitter = new Submitter();
        assertFalse(submitter.isValid());

        submitter.setLastName(LAST_NAME);
        submitter.setFirstName(FIRST_NAME);
        assertFalse(submitter.isValid());

        submitter.setAddress(ADDRESS);
        submitter.setPhone(PHONE);
        submitter.setEmail(EMAIL);
        assertTrue(submitter.isValid());
    }

    @Test
    public void testClone() {
        Submitter submitter = new Submitter();
        Submitter dup = submitter.clone();
        assertEquals(submitter, dup);
        assertNotSame(submitter, dup);

        submitter.setLastName(LAST_NAME);
        submitter.setFirstName(FIRST_NAME);
        submitter.setMiddleInitials(INITIALS);
        submitter.setOrganization(ORGANIZATION);
        submitter.setAddress(ADDRESS);
        submitter.setPhone(PHONE);
        submitter.setEmail(EMAIL);
        assertNotEquals(submitter, dup);

        dup = submitter.clone();
        assertEquals(submitter, dup);
        assertNotSame(submitter, dup);
    }

    @Test
    public void testHashCodeEquals() {
        Submitter first = new Submitter();
        assertFalse(first.equals(null));
        assertFalse(first.equals(LAST_NAME));
        Investigator investigator = new Investigator();
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(first));

        Submitter second = new Submitter();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setLastName(LAST_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLastName(LAST_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setLastName(LAST_NAME);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));

        first.setFirstName(FIRST_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFirstName(FIRST_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setFirstName(FIRST_NAME);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));

        first.setMiddleInitials(INITIALS);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMiddleInitials(INITIALS);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setMiddleInitials(INITIALS);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));

        first.setOrganization(ORGANIZATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setOrganization(ORGANIZATION);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));
        investigator.setOrganization(ORGANIZATION);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));

        first.setAddress(ADDRESS);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAddress(ADDRESS);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setAddress(ADDRESS);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));

        first.setPhone(PHONE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPhone(PHONE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setPhone(PHONE);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));

        first.setEmail(EMAIL);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setEmail(EMAIL);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setEmail(EMAIL);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));
    }

}

