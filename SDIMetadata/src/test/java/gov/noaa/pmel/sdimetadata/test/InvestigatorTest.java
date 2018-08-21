package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.person.Investigator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class InvestigatorTest {

    private static final String LAST_NAME = "Smith";
    private static final String FIRST_NAME = "John";
    private static final String INITIALS = "D.Z.";
    private static final String ORGANIZATION = "NOAA/PMEL";
    private static final String ADDRESS = "123 Main St, Seattle WA 98101";
    private static final String PHONE = "206-555-6789";
    private static final String EMAIL = "JDZSmith@nowhere.com";

    @Test
    public void testGetSetLastName() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getLastName());
        investigator.setLastName(LAST_NAME);
        assertEquals(LAST_NAME, investigator.getLastName());
        investigator.setLastName(null);
        assertEquals("", investigator.getLastName());
    }

    @Test
    public void testGetSetFirstName() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getFirstName());
        investigator.setFirstName(FIRST_NAME);
        assertEquals(FIRST_NAME, investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setFirstName(null);
        assertEquals("", investigator.getFirstName());
    }

    @Test
    public void testGetSetMiddleInitials() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getMiddleInitials());
        investigator.setMiddleInitials(INITIALS);
        assertEquals(INITIALS, investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setMiddleInitials(null);
        assertEquals("", investigator.getMiddleInitials());
    }

    @Test
    public void testGetSetOrganization() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getOrganization());
        investigator.setOrganization(ORGANIZATION);
        assertEquals(ORGANIZATION, investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setOrganization(null);
        assertEquals("", investigator.getOrganization());
    }

    @Test
    public void testGetSetAddress() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getAddress());
        investigator.setAddress(ADDRESS);
        assertEquals(ADDRESS, investigator.getAddress());
        assertEquals("", investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setAddress(null);
        assertEquals("", investigator.getAddress());
    }

    @Test
    public void testGetSetPhone() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getPhone());
        investigator.setPhone(PHONE);
        assertEquals(PHONE, investigator.getPhone());
        assertEquals("", investigator.getAddress());
        assertEquals("", investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setPhone(null);
        assertEquals("", investigator.getPhone());
    }

    @Test
    public void testGetSetEmail() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getEmail());
        investigator.setEmail(EMAIL);
        assertEquals(EMAIL, investigator.getEmail());
        assertEquals("", investigator.getPhone());
        assertEquals("", investigator.getAddress());
        assertEquals("", investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setEmail(null);
        assertEquals("", investigator.getEmail());
    }

    @Test
    public void testIsValid() {
        Investigator investigator = new Investigator();
        assertFalse(investigator.isValid());

        investigator.setLastName(LAST_NAME);
        investigator.setFirstName(FIRST_NAME);
        assertTrue(investigator.isValid());

        investigator.setLastName("\n");
        assertFalse(investigator.isValid());
        investigator.setLastName(LAST_NAME);
        assertTrue(investigator.isValid());

        investigator.setFirstName("\t");
        assertFalse(investigator.isValid());
    }

    @Test
    public void testClone() {
        Investigator investigator = new Investigator();
        Investigator dup = investigator.clone();
        assertEquals(investigator, dup);
        assertNotSame(investigator, dup);

        investigator.setLastName(LAST_NAME);
        investigator.setFirstName(FIRST_NAME);
        investigator.setMiddleInitials(INITIALS);
        investigator.setOrganization(ORGANIZATION);
        investigator.setAddress(ADDRESS);
        investigator.setPhone(PHONE);
        investigator.setEmail(EMAIL);
        assertNotEquals(investigator, dup);

        dup = investigator.clone();
        assertEquals(investigator, dup);
        assertNotSame(investigator, dup);
    }

    @Test
    public void testHashCodeEquals() {
        Investigator first = new Investigator();
        assertFalse(first.equals(null));
        assertFalse(first.equals(LAST_NAME));

        Investigator second = new Investigator();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setLastName(LAST_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLastName(LAST_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setFirstName(FIRST_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFirstName(FIRST_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setMiddleInitials(INITIALS);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMiddleInitials(INITIALS);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setOrganization(ORGANIZATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setOrganization(ORGANIZATION);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));

        first.setAddress(ADDRESS);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setAddress(ADDRESS);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPhone(PHONE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPhone(PHONE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setEmail(EMAIL);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setEmail(EMAIL);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}

