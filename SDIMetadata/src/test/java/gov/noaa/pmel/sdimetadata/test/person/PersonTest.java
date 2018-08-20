package gov.noaa.pmel.sdimetadata.test.person;

import gov.noaa.pmel.sdimetadata.person.Person;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonTest {

    private static final String LAST_NAME = "Smith";
    private static final String FIRST_NAME = "John";
    private static final String INITIALS = "D.Z.";
    private static final String ORGANIZATION = "NOAA/PMEL";
    private static final String ADDRESS = "123 Main St, Seattle WA 98101";
    private static final String PHONE = "206-555-6789";
    private static final String EMAIL = "JDZSmith@nowhere.com";

    @Test
    public void testGetSetLastName() {
        Person person = new Person();
        assertEquals("", person.getLastName());
        person.setLastName(LAST_NAME);
        assertEquals(LAST_NAME, person.getLastName());
        person.setLastName(null);
        assertEquals("", person.getLastName());
    }

    @Test
    public void testGetSetFirstName() {
        Person person = new Person();
        assertEquals("", person.getFirstName());
        person.setFirstName(FIRST_NAME);
        assertEquals(FIRST_NAME, person.getFirstName());
        assertEquals("", person.getLastName());
        person.setFirstName(null);
        assertEquals("", person.getFirstName());
    }

    @Test
    public void testGetSetMiddleInitials() {
        Person person = new Person();
        assertEquals("", person.getMiddleInitials());
        person.setMiddleInitials(INITIALS);
        assertEquals(INITIALS, person.getMiddleInitials());
        assertEquals("", person.getFirstName());
        assertEquals("", person.getLastName());
        person.setMiddleInitials(null);
        assertEquals("", person.getMiddleInitials());
    }

    @Test
    public void testGetSetOrganization() {
        Person person = new Person();
        assertEquals("", person.getOrganization());
        person.setOrganization(ORGANIZATION);
        assertEquals(ORGANIZATION, person.getOrganization());
        assertEquals("", person.getMiddleInitials());
        assertEquals("", person.getFirstName());
        assertEquals("", person.getLastName());
        person.setOrganization(null);
        assertEquals("", person.getOrganization());
    }

    @Test
    public void testGetSetAddress() {
        Person person = new Person();
        assertEquals("", person.getAddress());
        person.setAddress(ADDRESS);
        assertEquals(ADDRESS, person.getAddress());
        assertEquals("", person.getOrganization());
        assertEquals("", person.getMiddleInitials());
        assertEquals("", person.getFirstName());
        assertEquals("", person.getLastName());
        person.setAddress(null);
        assertEquals("", person.getAddress());
    }

    @Test
    public void testGetSetPhone() {
        Person person = new Person();
        assertEquals("", person.getPhone());
        person.setPhone(PHONE);
        assertEquals(PHONE, person.getPhone());
        assertEquals("", person.getAddress());
        assertEquals("", person.getOrganization());
        assertEquals("", person.getMiddleInitials());
        assertEquals("", person.getFirstName());
        assertEquals("", person.getLastName());
        person.setPhone(null);
        assertEquals("", person.getPhone());
    }

    @Test
    public void testGetSetEmail() {
        Person person = new Person();
        assertEquals("", person.getEmail());
        person.setEmail(EMAIL);
        assertEquals(EMAIL, person.getEmail());
        assertEquals("", person.getPhone());
        assertEquals("", person.getAddress());
        assertEquals("", person.getOrganization());
        assertEquals("", person.getMiddleInitials());
        assertEquals("", person.getFirstName());
        assertEquals("", person.getLastName());
        person.setEmail(null);
        assertEquals("", person.getEmail());
    }

    @Test
    public void testHashCodeEquals() {
        Person first = new Person();
        assertFalse( first.equals(null) );
        assertFalse( first.equals(LAST_NAME) );

        Person second = new Person();
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue( first.equals(second) );

        first.setLastName(LAST_NAME);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse( first.equals(second) );
        second.setLastName(LAST_NAME);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue( first.equals(second) );

        first.setFirstName(FIRST_NAME);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse( first.equals(second) );
        second.setFirstName(FIRST_NAME);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue( first.equals(second) );

        first.setMiddleInitials(INITIALS);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse( first.equals(second) );
        second.setMiddleInitials(INITIALS);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue( first.equals(second) );

        first.setOrganization(ORGANIZATION);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse( first.equals(second) );
        second.setOrganization(ORGANIZATION);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue( first.equals(second) );

        first.setAddress(ADDRESS);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse( first.equals(second) );
        second.setAddress(ADDRESS);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue( first.equals(second) );

        first.setPhone(PHONE);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse( first.equals(second) );
        second.setPhone(PHONE);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue( first.equals(second) );

        first.setEmail(EMAIL);
        assertFalse(first.hashCode() == second.hashCode());
        assertFalse( first.equals(second) );
        second.setEmail(EMAIL);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue( first.equals(second) );

    }
}