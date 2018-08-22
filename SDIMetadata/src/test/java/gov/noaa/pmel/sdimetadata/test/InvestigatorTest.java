package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.Investigator;
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
    public void testGetSetStreets() {
        Investigator investigator = new Investigator();
        assertEquals(0, investigator.getStreets().size());
        investigator.setStreets(STREETS);
        assertEquals(STREETS, investigator.getStreets());
        assertNotSame(STREETS, investigator.getStreets());
        assertEquals("", investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setStreets(null);
        assertEquals(0, investigator.getStreets().size());
        investigator.setStreets(STREETS);
        assertEquals(STREETS, investigator.getStreets());
        investigator.setStreets(new HashSet<String>());
        assertEquals(0, investigator.getStreets().size());
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
        assertEquals("", investigator.getCity());
        investigator.setCity(CITY);
        assertEquals(CITY, investigator.getCity());
        assertEquals(0, investigator.getStreets().size());
        assertEquals("", investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setCity(null);
        assertEquals("", investigator.getCity());
    }

    @Test
    public void testGetSetRegion() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getRegion());
        investigator.setRegion(REGION);
        assertEquals(REGION, investigator.getRegion());
        assertEquals("", investigator.getCity());
        assertEquals(0, investigator.getStreets().size());
        assertEquals("", investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setRegion(null);
        assertEquals("", investigator.getRegion());
    }

    @Test
    public void testGetSetZipCode() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getZipCode());
        investigator.setZipCode(ZIP_CODE);
        assertEquals(ZIP_CODE, investigator.getZipCode());
        assertEquals("", investigator.getRegion());
        assertEquals("", investigator.getCity());
        assertEquals(0, investigator.getStreets().size());
        assertEquals("", investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setZipCode(null);
        assertEquals("", investigator.getZipCode());
    }

    @Test
    public void testGetSetCountry() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getCountry());
        investigator.setCountry(PHONE);
        assertEquals(PHONE, investigator.getCountry());
        assertEquals("", investigator.getZipCode());
        assertEquals("", investigator.getRegion());
        assertEquals("", investigator.getCity());
        assertEquals(0, investigator.getStreets().size());
        assertEquals("", investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setCountry(null);
        assertEquals("", investigator.getCountry());
    }

    @Test
    public void testGetSetPhone() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getPhone());
        investigator.setPhone(PHONE);
        assertEquals(PHONE, investigator.getPhone());
        assertEquals("", investigator.getCountry());
        assertEquals("", investigator.getZipCode());
        assertEquals("", investigator.getRegion());
        assertEquals("", investigator.getCity());
        assertEquals(0, investigator.getStreets().size());
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
        assertEquals("", investigator.getCountry());
        assertEquals("", investigator.getZipCode());
        assertEquals("", investigator.getRegion());
        assertEquals("", investigator.getCity());
        assertEquals(0, investigator.getStreets().size());
        assertEquals("", investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setEmail(null);
        assertEquals("", investigator.getEmail());
    }

    @Test
    public void testGetSetPiId() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getPiId());
        investigator.setPiId(PI_ID);
        assertEquals(PI_ID, investigator.getPiId());
        assertEquals("", investigator.getEmail());
        assertEquals("", investigator.getPhone());
        assertEquals("", investigator.getCountry());
        assertEquals("", investigator.getZipCode());
        assertEquals("", investigator.getRegion());
        assertEquals("", investigator.getCity());
        assertEquals(0, investigator.getStreets().size());
        assertEquals("", investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setPiId(null);
        assertEquals("", investigator.getPiId());
    }

    @Test
    public void testGetSetPiIdType() {
        Investigator investigator = new Investigator();
        assertEquals("", investigator.getPiIdType());
        investigator.setPiIdType(PI_ID_TYPE);
        assertEquals(PI_ID_TYPE, investigator.getPiIdType());
        assertEquals("", investigator.getPiId());
        assertEquals("", investigator.getEmail());
        assertEquals("", investigator.getPhone());
        assertEquals("", investigator.getCountry());
        assertEquals("", investigator.getZipCode());
        assertEquals("", investigator.getRegion());
        assertEquals("", investigator.getCity());
        assertEquals(0, investigator.getStreets().size());
        assertEquals("", investigator.getOrganization());
        assertEquals("", investigator.getMiddleInitials());
        assertEquals("", investigator.getFirstName());
        assertEquals("", investigator.getLastName());
        investigator.setPiIdType(null);
        assertEquals("", investigator.getPiIdType());
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
        investigator.setStreets(STREETS);
        investigator.setCity(CITY);
        investigator.setRegion(REGION);
        investigator.setZipCode(ZIP_CODE);
        investigator.setCountry(COUNTRY);
        investigator.setPhone(PHONE);
        investigator.setEmail(EMAIL);
        investigator.setPiId(PI_ID);
        investigator.setPiIdType(PI_ID_TYPE);
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

        first.setStreets(STREETS);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setStreets(STREETS);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setCity(CITY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCity(CITY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setRegion(REGION);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setRegion(REGION);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setZipCode(ZIP_CODE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setZipCode(ZIP_CODE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setCountry(COUNTRY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setCountry(COUNTRY);
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

        first.setPiId(PI_ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPiId(PI_ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPiIdType(PI_ID_TYPE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPiIdType(PI_ID_TYPE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

}

