package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.person.Investigator;
import gov.noaa.pmel.socatmetadata.shared.person.Person;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class InvestigatorTest {

    private static final String EMPTY_STRING = "";
    private static final ArrayList<String> EMPTY_NAMELIST = new ArrayList<String>();
    private static final HashSet<String> EMPTY_NAMESET = new HashSet<String>();

    private static final String LAST_NAME = "Smith";
    private static final String FIRST_NAME = "John";
    private static final String INITIALS = "D.Z.";
    private static final String ID = "PI-23423";
    private static final String ID_TYPE = "PIRecords";
    private static final String ORGANIZATION = "NOAA/PMEL";
    private static final ArrayList<String> STREETS = new ArrayList<String>(Arrays.asList(
            "Room 259, Bldg 4",
            "123 Main St"
    ));
    private static final String CITY = "Seattle";
    private static final String REGION = "WA";
    private static final String ZIP_CODE = "98101";
    private static final String COUNTRY = "USA";
    private static final String PHONE = "206-555-6789";
    private static final String EMAIL = "JDZSmith@not.an.org";

    @Test
    public void testGetSetStreets() {
        Investigator investigator = new Investigator();
        assertEquals(EMPTY_NAMELIST, investigator.getStreets());
        investigator.setStreets(STREETS);
        ArrayList<String> nameList = investigator.getStreets();
        assertEquals(STREETS, nameList);
        assertNotSame(STREETS, nameList);
        assertNotSame(nameList, investigator.getStreets());
        assertEquals(EMPTY_STRING, investigator.getOrganization());
        assertEquals(EMPTY_STRING, investigator.getIdType());
        assertEquals(EMPTY_STRING, investigator.getId());
        assertEquals(EMPTY_STRING, investigator.getMiddle());
        assertEquals(EMPTY_STRING, investigator.getFirstName());
        assertEquals(EMPTY_STRING, investigator.getLastName());
        investigator.setStreets(null);
        assertEquals(EMPTY_NAMELIST, investigator.getStreets());
        investigator.setStreets(EMPTY_NAMESET);
        assertEquals(EMPTY_NAMELIST, investigator.getStreets());
        try {
            investigator.setStreets(Arrays.asList(STREETS.get(0), null, STREETS.get(1)));
            fail("calling setStreets with a list containing null succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
        try {
            investigator.setStreets(Arrays.asList(STREETS.get(0), "\n", STREETS.get(1)));
            fail("calling setStreets with a list containing a blank string succeeded");
        } catch ( IllegalArgumentException ex ) {
            // Expected result
        }
    }

    @Test
    public void testGetSetCity() {
        Investigator investigator = new Investigator();
        assertEquals(EMPTY_STRING, investigator.getCity());
        investigator.setCity(CITY);
        assertEquals(CITY, investigator.getCity());
        assertEquals(EMPTY_NAMELIST, investigator.getStreets());
        assertEquals(EMPTY_STRING, investigator.getOrganization());
        assertEquals(EMPTY_STRING, investigator.getIdType());
        assertEquals(EMPTY_STRING, investigator.getId());
        assertEquals(EMPTY_STRING, investigator.getMiddle());
        assertEquals(EMPTY_STRING, investigator.getFirstName());
        assertEquals(EMPTY_STRING, investigator.getLastName());
        investigator.setCity(null);
        assertEquals(EMPTY_STRING, investigator.getCity());
        investigator.setCity("\t");
        assertEquals(EMPTY_STRING, investigator.getCity());
    }

    @Test
    public void testGetSetRegion() {
        Investigator investigator = new Investigator();
        assertEquals(EMPTY_STRING, investigator.getRegion());
        investigator.setRegion(REGION);
        assertEquals(REGION, investigator.getRegion());
        assertEquals(EMPTY_STRING, investigator.getCity());
        assertEquals(EMPTY_NAMELIST, investigator.getStreets());
        assertEquals(EMPTY_STRING, investigator.getOrganization());
        assertEquals(EMPTY_STRING, investigator.getIdType());
        assertEquals(EMPTY_STRING, investigator.getId());
        assertEquals(EMPTY_STRING, investigator.getMiddle());
        assertEquals(EMPTY_STRING, investigator.getFirstName());
        assertEquals(EMPTY_STRING, investigator.getLastName());
        investigator.setRegion(null);
        assertEquals(EMPTY_STRING, investigator.getRegion());
        investigator.setRegion("\t");
        assertEquals(EMPTY_STRING, investigator.getRegion());
    }

    @Test
    public void testGetSetZipCode() {
        Investigator investigator = new Investigator();
        assertEquals(EMPTY_STRING, investigator.getZipCode());
        investigator.setZipCode(ZIP_CODE);
        assertEquals(ZIP_CODE, investigator.getZipCode());
        assertEquals(EMPTY_STRING, investigator.getRegion());
        assertEquals(EMPTY_STRING, investigator.getCity());
        assertEquals(EMPTY_NAMELIST, investigator.getStreets());
        assertEquals(EMPTY_STRING, investigator.getOrganization());
        assertEquals(EMPTY_STRING, investigator.getIdType());
        assertEquals(EMPTY_STRING, investigator.getId());
        assertEquals(EMPTY_STRING, investigator.getMiddle());
        assertEquals(EMPTY_STRING, investigator.getFirstName());
        assertEquals(EMPTY_STRING, investigator.getLastName());
        investigator.setZipCode(null);
        assertEquals(EMPTY_STRING, investigator.getZipCode());
        investigator.setZipCode("\t");
        assertEquals(EMPTY_STRING, investigator.getZipCode());
    }

    @Test
    public void testGetSetCountry() {
        Investigator investigator = new Investigator();
        assertEquals(EMPTY_STRING, investigator.getCountry());
        investigator.setCountry(PHONE);
        assertEquals(PHONE, investigator.getCountry());
        assertEquals(EMPTY_STRING, investigator.getZipCode());
        assertEquals(EMPTY_STRING, investigator.getRegion());
        assertEquals(EMPTY_STRING, investigator.getCity());
        assertEquals(EMPTY_NAMELIST, investigator.getStreets());
        assertEquals(EMPTY_STRING, investigator.getOrganization());
        assertEquals(EMPTY_STRING, investigator.getIdType());
        assertEquals(EMPTY_STRING, investigator.getId());
        assertEquals(EMPTY_STRING, investigator.getMiddle());
        assertEquals(EMPTY_STRING, investigator.getFirstName());
        assertEquals(EMPTY_STRING, investigator.getLastName());
        investigator.setCountry(null);
        assertEquals(EMPTY_STRING, investigator.getCountry());
        investigator.setCountry("\t");
        assertEquals(EMPTY_STRING, investigator.getCountry());
    }

    @Test
    public void testGetSetPhone() {
        Investigator investigator = new Investigator();
        assertEquals(EMPTY_STRING, investigator.getPhone());
        investigator.setPhone(PHONE);
        assertEquals(PHONE, investigator.getPhone());
        assertEquals(EMPTY_STRING, investigator.getCountry());
        assertEquals(EMPTY_STRING, investigator.getZipCode());
        assertEquals(EMPTY_STRING, investigator.getRegion());
        assertEquals(EMPTY_STRING, investigator.getCity());
        assertEquals(EMPTY_NAMELIST, investigator.getStreets());
        assertEquals(EMPTY_STRING, investigator.getOrganization());
        assertEquals(EMPTY_STRING, investigator.getIdType());
        assertEquals(EMPTY_STRING, investigator.getId());
        assertEquals(EMPTY_STRING, investigator.getMiddle());
        assertEquals(EMPTY_STRING, investigator.getFirstName());
        assertEquals(EMPTY_STRING, investigator.getLastName());
        investigator.setPhone(null);
        assertEquals(EMPTY_STRING, investigator.getPhone());
        investigator.setPhone("\t");
        assertEquals(EMPTY_STRING, investigator.getPhone());
    }

    @Test
    public void testGetSetEmail() {
        Investigator investigator = new Investigator();
        assertEquals(EMPTY_STRING, investigator.getEmail());
        investigator.setEmail(EMAIL);
        assertEquals(EMAIL, investigator.getEmail());
        assertEquals(EMPTY_STRING, investigator.getPhone());
        assertEquals(EMPTY_STRING, investigator.getCountry());
        assertEquals(EMPTY_STRING, investigator.getZipCode());
        assertEquals(EMPTY_STRING, investigator.getRegion());
        assertEquals(EMPTY_STRING, investigator.getCity());
        assertEquals(EMPTY_NAMELIST, investigator.getStreets());
        assertEquals(EMPTY_STRING, investigator.getOrganization());
        assertEquals(EMPTY_STRING, investigator.getIdType());
        assertEquals(EMPTY_STRING, investigator.getId());
        assertEquals(EMPTY_STRING, investigator.getMiddle());
        assertEquals(EMPTY_STRING, investigator.getFirstName());
        assertEquals(EMPTY_STRING, investigator.getLastName());
        investigator.setEmail(null);
        assertEquals(EMPTY_STRING, investigator.getEmail());
        investigator.setEmail("\t");
        assertEquals(EMPTY_STRING, investigator.getEmail());
    }

    @Test
    public void testInvalidFieldNames() {
        Investigator investigator = new Investigator();
        assertEquals(new HashSet<String>(Arrays.asList("lastName", "firstName", "organization")),
                investigator.invalidFieldNames());
        investigator.setOrganization(ORGANIZATION);
        assertEquals(new HashSet<String>(Arrays.asList("lastName", "firstName")), investigator.invalidFieldNames());
        investigator.setLastName(LAST_NAME);
        assertEquals(new HashSet<String>(Arrays.asList("firstName")), investigator.invalidFieldNames());
        investigator.setFirstName(FIRST_NAME);
        assertEquals(EMPTY_NAMESET, investigator.invalidFieldNames());
        investigator.setLastName("\n");
        assertEquals(new HashSet<String>(Arrays.asList("lastName")), investigator.invalidFieldNames());
        investigator.setLastName(LAST_NAME);
        investigator.setFirstName("\t");
        assertEquals(new HashSet<String>(Arrays.asList("firstName")), investigator.invalidFieldNames());
    }

    @Test
    public void testClone() {
        Investigator investigator = new Investigator();
        Investigator dup = investigator.clone();
        assertEquals(investigator, dup);
        assertNotSame(investigator, dup);

        investigator.setLastName(LAST_NAME);
        investigator.setFirstName(FIRST_NAME);
        investigator.setMiddle(INITIALS);
        investigator.setOrganization(ORGANIZATION);
        investigator.setId(ID);
        investigator.setIdType(ID_TYPE);
        investigator.setStreets(STREETS);
        investigator.setCity(CITY);
        investigator.setRegion(REGION);
        investigator.setZipCode(ZIP_CODE);
        investigator.setCountry(COUNTRY);
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

        Person other = new Person();
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setLastName(LAST_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setLastName(LAST_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setLastName(LAST_NAME);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setFirstName(FIRST_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setFirstName(FIRST_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setFirstName(FIRST_NAME);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setMiddle(INITIALS);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setMiddle(INITIALS);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setMiddle(INITIALS);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setId(ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setId(ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setId(ID);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setIdType(ID_TYPE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setIdType(ID_TYPE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setIdType(ID_TYPE);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setOrganization(ORGANIZATION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setOrganization(ORGANIZATION);
        assertTrue(first.hashCode() == second.hashCode());
        assertTrue(first.equals(second));
        other.setOrganization(ORGANIZATION);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setStreets(STREETS);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setStreets(STREETS);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setCity(CITY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCity(CITY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setRegion(REGION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setRegion(REGION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setZipCode(ZIP_CODE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setZipCode(ZIP_CODE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setCountry(COUNTRY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCountry(COUNTRY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setPhone(PHONE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPhone(PHONE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setEmail(EMAIL);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setEmail(EMAIL);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));
    }

}
