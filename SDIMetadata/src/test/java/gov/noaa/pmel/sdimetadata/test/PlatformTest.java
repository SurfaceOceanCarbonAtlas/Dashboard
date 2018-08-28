package gov.noaa.pmel.sdimetadata.test;

import gov.noaa.pmel.sdimetadata.Platform;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class PlatformTest {

    private static final String PLATFORM_ID = "33RO";
    private static final String PLATFORM_NAME = "Ronald H. Brown";
    private static final String PLATFORM_TYPE = "Ship";
    private static final String PLATFORM_OWNER = "NOAA";
    private static final String PLATFORM_COUNTRY = "USA";

    @Test
    public void testGetSetPlatformId() {
        Platform platform = new Platform();
        assertEquals("", platform.getPlatformId());
        platform.setPlatformId(PLATFORM_ID);
        assertEquals(PLATFORM_ID, platform.getPlatformId());
        platform.setPlatformId(null);
        assertEquals("", platform.getPlatformId());
        platform.setPlatformId("\t");
        assertEquals("", platform.getPlatformId());
    }

    @Test
    public void testGetSetPlatformName() {
        Platform platform = new Platform();
        assertEquals("", platform.getPlatformName());
        platform.setPlatformName(PLATFORM_NAME);
        assertEquals(PLATFORM_NAME, platform.getPlatformName());
        assertEquals("", platform.getPlatformId());
        platform.setPlatformName(null);
        assertEquals("", platform.getPlatformName());
        platform.setPlatformName("\t");
        assertEquals("", platform.getPlatformName());
    }

    @Test
    public void testGetSetPlatformType() {
        Platform platform = new Platform();
        assertEquals("", platform.getPlatformType());
        platform.setPlatformType(PLATFORM_TYPE);
        assertEquals(PLATFORM_TYPE, platform.getPlatformType());
        assertEquals("", platform.getPlatformName());
        assertEquals("", platform.getPlatformId());
        platform.setPlatformType(null);
        assertEquals("", platform.getPlatformType());
        platform.setPlatformType("\t");
        assertEquals("", platform.getPlatformType());
    }

    @Test
    public void testGetSetPlatformOwner() {
        Platform platform = new Platform();
        assertEquals("", platform.getPlatformOwner());
        platform.setPlatformOwner(PLATFORM_OWNER);
        assertEquals(PLATFORM_OWNER, platform.getPlatformOwner());
        assertEquals("", platform.getPlatformType());
        assertEquals("", platform.getPlatformName());
        assertEquals("", platform.getPlatformId());
        platform.setPlatformOwner(null);
        assertEquals("", platform.getPlatformOwner());
        platform.setPlatformOwner("\t");
        assertEquals("", platform.getPlatformOwner());
    }

    @Test
    public void testGetSetPlatformCountry() {
        Platform platform = new Platform();
        assertEquals("", platform.getPlatformCountry());
        platform.setPlatformCountry(PLATFORM_COUNTRY);
        assertEquals(PLATFORM_COUNTRY, platform.getPlatformCountry());
        assertEquals("", platform.getPlatformOwner());
        assertEquals("", platform.getPlatformType());
        assertEquals("", platform.getPlatformName());
        assertEquals("", platform.getPlatformId());
        platform.setPlatformCountry(null);
        assertEquals("", platform.getPlatformCountry());
        platform.setPlatformCountry("\t");
        assertEquals("", platform.getPlatformCountry());
    }

    @Test
    public void testHashCodeEquals() {
        Platform first = new Platform();
        assertFalse(first.equals(null));
        assertFalse(first.equals(PLATFORM_ID));

        Platform second = new Platform();
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPlatformId(PLATFORM_ID);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPlatformId(PLATFORM_ID);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPlatformName(PLATFORM_NAME);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPlatformName(PLATFORM_NAME);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPlatformType(PLATFORM_TYPE);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPlatformType(PLATFORM_TYPE);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPlatformOwner(PLATFORM_OWNER);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPlatformOwner(PLATFORM_OWNER);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));

        first.setPlatformCountry(PLATFORM_COUNTRY);
        assertNotEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(second));
        second.setPlatformCountry(PLATFORM_COUNTRY);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.equals(second));
    }

    @Test
    public void testIsValid() {
        Platform platform = new Platform();
        assertFalse(platform.isValid());
        platform.setPlatformId(PLATFORM_ID);
        platform.setPlatformName(PLATFORM_NAME);
        platform.setPlatformType(PLATFORM_TYPE);
        assertTrue(platform.isValid());
        platform.setPlatformType("\n");
        assertFalse(platform.isValid());
    }

    @Test
    public void testClone() {
        Platform platform = new Platform();
        Platform dup = platform.clone();
        assertEquals(platform, dup);
        assertNotSame(platform, dup);

        platform.setPlatformId(PLATFORM_ID);
        platform.setPlatformName(PLATFORM_NAME);
        platform.setPlatformType(PLATFORM_TYPE);
        platform.setPlatformOwner(PLATFORM_OWNER);
        platform.setPlatformCountry(PLATFORM_COUNTRY);
        assertNotEquals(platform, dup);

        dup = platform.clone();
        assertEquals(platform, dup);
        assertNotSame(platform, dup);
    }

}
