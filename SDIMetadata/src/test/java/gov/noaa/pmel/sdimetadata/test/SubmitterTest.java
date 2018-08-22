package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.person.Investigator;
import gov.noaa.pmel.sdimetadata.person.Submitter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

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
    private static final String PI_ID = "PI-23423";
    private static final String PI_ID_TYPE = "PIRecords";


    @Test
    public void testIsValid() {
        Submitter submitter = new Submitter();
        assertFalse(submitter.isValid());

        submitter.setLastName(LAST_NAME);
        submitter.setFirstName(FIRST_NAME);
        assertFalse(submitter.isValid());

        submitter.setStreets(STREETS);
        submitter.setCity(CITY);
        submitter.setCountry(COUNTRY);
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
        submitter.setStreets(STREETS);
        submitter.setCity(CITY);
        submitter.setRegion(REGION);
        submitter.setZipCode(ZIP_CODE);
        submitter.setCountry(COUNTRY);
        submitter.setPhone(PHONE);
        submitter.setEmail(EMAIL);
        submitter.setPiId(PI_ID);
        submitter.setPiIdType(PI_ID_TYPE);
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

        first.setStreets(STREETS);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setStreets(STREETS);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setStreets(STREETS);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));

        first.setCity(CITY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCity(CITY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setCity(CITY);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));

        first.setRegion(REGION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setRegion(REGION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setRegion(REGION);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));

        first.setZipCode(ZIP_CODE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setZipCode(ZIP_CODE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setZipCode(ZIP_CODE);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));

        first.setCountry(COUNTRY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCountry(COUNTRY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setCountry(COUNTRY);
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

        first.setPiId(PI_ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPiId(PI_ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setPiId(PI_ID);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));

        first.setPiIdType(PI_ID_TYPE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPiIdType(PI_ID_TYPE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
        investigator.setPiIdType(PI_ID_TYPE);
        assertFalse(first.equals(investigator));
        assertTrue(investigator.equals(second));
    }

}

