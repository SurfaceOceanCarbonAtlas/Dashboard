package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class SubmitterTest {

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
    public void testInvalidFieldNames() {
        Submitter submitter = new Submitter();
        assertEquals(new HashSet<String>(Arrays.asList("lastName", "firstName",
                "streets", "city", "country", "phone", "email")), submitter.invalidFieldNames());
        submitter.setLastName(LAST_NAME);
        submitter.setFirstName(FIRST_NAME);
        assertEquals(new HashSet<String>(Arrays.asList("streets", "city",
                "country", "phone", "email")), submitter.invalidFieldNames());
        submitter.setStreets(STREETS);
        submitter.setCity(CITY);
        assertEquals(new HashSet<String>(Arrays.asList("country", "phone", "email")), submitter.invalidFieldNames());
        submitter.setCountry(COUNTRY);
        submitter.setPhone(PHONE);
        assertEquals(new HashSet<String>(Arrays.asList("email")), submitter.invalidFieldNames());
        submitter.setEmail(EMAIL);
        assertEquals(new HashSet<String>(), submitter.invalidFieldNames());
    }

    @Test
    public void testClone() {
        Submitter submitter = new Submitter();
        Submitter dup = submitter.clone();
        assertEquals(submitter, dup);
        assertNotSame(submitter, dup);

        submitter.setLastName(LAST_NAME);
        submitter.setFirstName(FIRST_NAME);
        submitter.setMiddle(INITIALS);
        submitter.setId(ID);
        submitter.setIdType(ID_TYPE);
        submitter.setOrganization(ORGANIZATION);
        submitter.setStreets(STREETS);
        submitter.setCity(CITY);
        submitter.setRegion(REGION);
        submitter.setZipCode(ZIP_CODE);
        submitter.setCountry(COUNTRY);
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

        Submitter second = new Submitter();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        Investigator other = new Investigator();
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
        other.setStreets(STREETS);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setCity(CITY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCity(CITY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setCity(CITY);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setRegion(REGION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setRegion(REGION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setRegion(REGION);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setZipCode(ZIP_CODE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setZipCode(ZIP_CODE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setZipCode(ZIP_CODE);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setCountry(COUNTRY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCountry(COUNTRY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setCountry(COUNTRY);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setPhone(PHONE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPhone(PHONE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setPhone(PHONE);
        assertFalse(first.equals(other));
        assertTrue(other.equals(second));

        first.setEmail(EMAIL);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setEmail(EMAIL);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        other.setEmail(EMAIL);
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
    }

}

