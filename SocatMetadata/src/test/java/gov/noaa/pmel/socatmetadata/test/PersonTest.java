package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.person.Person;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class PersonTest {

    private static final String EMPTY_STRING = "";

    private static final String LAST_NAME = "Smith";
    private static final String FIRST_NAME = "John";
    private static final String INITIALS = "D.Z.";
    private static final String ID = "PI-23423";
    private static final String ID_TYPE = "PIRecords";
    private static final String ORGANIZATION = "NOAA/PMEL";

    @Test
    public void testGetSetLastName() {
        Person person = new Person();
        assertEquals(EMPTY_STRING, person.getLastName());
        person.setLastName(LAST_NAME);
        assertEquals(LAST_NAME, person.getLastName());
        person.setLastName(null);
        assertEquals(EMPTY_STRING, person.getLastName());
        person.setLastName("\t");
        assertEquals(EMPTY_STRING, person.getLastName());
    }

    @Test
    public void testGetSetFirstName() {
        Person person = new Person();
        assertEquals(EMPTY_STRING, person.getFirstName());
        person.setFirstName(FIRST_NAME);
        assertEquals(FIRST_NAME, person.getFirstName());
        assertEquals(EMPTY_STRING, person.getLastName());
        person.setFirstName(null);
        assertEquals(EMPTY_STRING, person.getFirstName());
        person.setFirstName("\t");
        assertEquals(EMPTY_STRING, person.getFirstName());
    }

    @Test
    public void testGetSetMiddle() {
        Person person = new Person();
        assertEquals(EMPTY_STRING, person.getMiddle());
        person.setMiddle(INITIALS);
        assertEquals(INITIALS, person.getMiddle());
        assertEquals(EMPTY_STRING, person.getFirstName());
        assertEquals(EMPTY_STRING, person.getLastName());
        person.setMiddle(null);
        assertEquals(EMPTY_STRING, person.getMiddle());
        person.setMiddle("\t");
        assertEquals(EMPTY_STRING, person.getMiddle());
    }

    @Test
    public void testGetSetId() {
        Person person = new Person();
        assertEquals(EMPTY_STRING, person.getId());
        person.setId(ID);
        assertEquals(ID, person.getId());
        assertEquals(EMPTY_STRING, person.getMiddle());
        assertEquals(EMPTY_STRING, person.getFirstName());
        assertEquals(EMPTY_STRING, person.getLastName());
        person.setId(null);
        assertEquals(EMPTY_STRING, person.getId());
        person.setId("\t");
        assertEquals(EMPTY_STRING, person.getId());
    }

    @Test
    public void testGetSetIdType() {
        Person person = new Person();
        assertEquals(EMPTY_STRING, person.getIdType());
        person.setIdType(ID_TYPE);
        assertEquals(ID_TYPE, person.getIdType());
        assertEquals(EMPTY_STRING, person.getId());
        assertEquals(EMPTY_STRING, person.getMiddle());
        assertEquals(EMPTY_STRING, person.getFirstName());
        assertEquals(EMPTY_STRING, person.getLastName());
        person.setIdType(null);
        assertEquals(EMPTY_STRING, person.getIdType());
        person.setIdType("\t");
        assertEquals(EMPTY_STRING, person.getIdType());
    }

    @Test
    public void testGetSetOrganization() {
        Person person = new Person();
        assertEquals(EMPTY_STRING, person.getOrganization());
        person.setOrganization(ORGANIZATION);
        assertEquals(ORGANIZATION, person.getOrganization());
        assertEquals(EMPTY_STRING, person.getIdType());
        assertEquals(EMPTY_STRING, person.getId());
        assertEquals(EMPTY_STRING, person.getMiddle());
        assertEquals(EMPTY_STRING, person.getFirstName());
        assertEquals(EMPTY_STRING, person.getLastName());
        person.setOrganization(null);
        assertEquals(EMPTY_STRING, person.getOrganization());
        person.setOrganization("\t");
        assertEquals(EMPTY_STRING, person.getOrganization());
    }

    @Test
    public void testGetReferenceName() {
        final String UNKNOWN = "Unknown";
        Person person = new Person();
        assertEquals(UNKNOWN, person.getReferenceName());
        person.setLastName(LAST_NAME);
        assertEquals(LAST_NAME, person.getReferenceName());
        person.setFirstName(FIRST_NAME);
        assertEquals(LAST_NAME + ", " + FIRST_NAME, person.getReferenceName());
        person.setMiddle(INITIALS);
        assertEquals(LAST_NAME + ", " + FIRST_NAME + " " + INITIALS, person.getReferenceName());
        person.setLastName(null);
        assertEquals(UNKNOWN + " " + FIRST_NAME + " " + INITIALS, person.getReferenceName());
        person.setFirstName(null);
        assertEquals(UNKNOWN, person.getReferenceName());
        person.setLastName(LAST_NAME);
        assertEquals(LAST_NAME, person.getReferenceName());
    }

    @Test
    public void testPerson() {
        Person person = new Person(null, null, null, null, null, null);
        assertEquals(new Person(), person);
        person = new Person(LAST_NAME, FIRST_NAME, INITIALS, ID, ID_TYPE, ORGANIZATION);
        assertEquals(LAST_NAME, person.getLastName());
        assertEquals(FIRST_NAME, person.getFirstName());
        assertEquals(INITIALS, person.getMiddle());
        assertEquals(ID, person.getId());
        assertEquals(ID_TYPE, person.getIdType());
        assertEquals(ORGANIZATION, person.getOrganization());
    }

    @Test
    public void testInvalidFieldNames() {
        Person person = new Person();
        assertEquals(new HashSet<String>(Arrays.asList("lastName", "firstName")), person.invalidFieldNames());
        person.setLastName(LAST_NAME);
        assertEquals(new HashSet<String>(Arrays.asList("firstName")), person.invalidFieldNames());
        person.setFirstName(FIRST_NAME);
        assertEquals(new HashSet<String>(), person.invalidFieldNames());
        person.setLastName("\n");
        assertEquals(new HashSet<String>(Arrays.asList("lastName")), person.invalidFieldNames());
        person.setLastName(LAST_NAME);
        person.setFirstName("\t");
        person.setFirstName(FIRST_NAME);
    }

    @Test
    public void testDuplicate() {
        Person person = new Person();
        Person dup = (Person) (person.duplicate(null));
        assertEquals(person, dup);
        assertNotSame(person, dup);

        person.setLastName(LAST_NAME);
        person.setFirstName(FIRST_NAME);
        person.setMiddle(INITIALS);
        person.setId(ID);
        person.setIdType(ID_TYPE);
        person.setOrganization(ORGANIZATION);
        assertNotEquals(person, dup);

        dup = (Person) (person.duplicate(null));
        assertEquals(person, dup);
        assertNotSame(person, dup);
    }

    @Test
    public void testHashCodeEquals() {
        Person first = new Person();
        assertFalse(first.equals(null));
        assertFalse(first.equals(LAST_NAME));

        Person second = new Person();
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

        first.setMiddle(INITIALS);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMiddle(INITIALS);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setId(ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setId(ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setIdType(ID_TYPE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setIdType(ID_TYPE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setOrganization(ORGANIZATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setOrganization(ORGANIZATION);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));
    }

}
