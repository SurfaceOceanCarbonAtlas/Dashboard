package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import gov.noaa.pmel.socatmetadata.shared.platform.PlatformType;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class PlatformTest {

    private static final String EMPTY_STR = "";

    private static final String PLATFORM_ID = "33RO";
    private static final String PLATFORM_NAME = "Ronald H. Brown";
    private static final PlatformType PLATFORM_TYPE = PlatformType.SHIP;
    private static final String PLATFORM_OWNER = "NOAA";
    private static final String PLATFORM_COUNTRY = "USA";

    @Test
    public void testGetSetPlatformId() {
        Platform platform = new Platform();
        assertEquals(EMPTY_STR, platform.getPlatformId());
        platform.setPlatformId(PLATFORM_ID);
        assertEquals(PLATFORM_ID, platform.getPlatformId());
        platform.setPlatformId(null);
        assertEquals(EMPTY_STR, platform.getPlatformId());
        platform.setPlatformId("\t");
        assertEquals(EMPTY_STR, platform.getPlatformId());
    }

    @Test
    public void testGetSetPlatformName() {
        Platform platform = new Platform();
        assertEquals(EMPTY_STR, platform.getPlatformName());
        platform.setPlatformName(PLATFORM_NAME);
        assertEquals(PLATFORM_NAME, platform.getPlatformName());
        assertEquals(EMPTY_STR, platform.getPlatformId());
        platform.setPlatformName(null);
        assertEquals(EMPTY_STR, platform.getPlatformName());
        platform.setPlatformName("\t");
        assertEquals(EMPTY_STR, platform.getPlatformName());
    }

    @Test
    public void testGetSetPlatformType() {
        Platform platform = new Platform();
        assertEquals(PlatformType.UNKNOWN, platform.getPlatformType());
        platform.setPlatformType(PLATFORM_TYPE);
        assertEquals(PLATFORM_TYPE, platform.getPlatformType());
        assertEquals(EMPTY_STR, platform.getPlatformName());
        assertEquals(EMPTY_STR, platform.getPlatformId());
        platform.setPlatformType(null);
        assertEquals(PlatformType.UNKNOWN, platform.getPlatformType());
    }

    @Test
    public void testGetSetPlatformOwner() {
        Platform platform = new Platform();
        assertEquals(EMPTY_STR, platform.getPlatformOwner());
        platform.setPlatformOwner(PLATFORM_OWNER);
        assertEquals(PLATFORM_OWNER, platform.getPlatformOwner());
        assertEquals(PlatformType.UNKNOWN, platform.getPlatformType());
        assertEquals(EMPTY_STR, platform.getPlatformName());
        assertEquals(EMPTY_STR, platform.getPlatformId());
        platform.setPlatformOwner(null);
        assertEquals(EMPTY_STR, platform.getPlatformOwner());
        platform.setPlatformOwner("\t");
        assertEquals(EMPTY_STR, platform.getPlatformOwner());
    }

    @Test
    public void testGetSetPlatformCountry() {
        Platform platform = new Platform();
        assertEquals(EMPTY_STR, platform.getPlatformCountry());
        platform.setPlatformCountry(PLATFORM_COUNTRY);
        assertEquals(PLATFORM_COUNTRY, platform.getPlatformCountry());
        assertEquals(EMPTY_STR, platform.getPlatformOwner());
        assertEquals(PlatformType.UNKNOWN, platform.getPlatformType());
        assertEquals(EMPTY_STR, platform.getPlatformName());
        assertEquals(EMPTY_STR, platform.getPlatformId());
        platform.setPlatformCountry(null);
        assertEquals(EMPTY_STR, platform.getPlatformCountry());
        platform.setPlatformCountry("\t");
        assertEquals(EMPTY_STR, platform.getPlatformCountry());
    }

    @Test
    public void testInvalidFieldNames() {
        Platform platform = new Platform();
        assertEquals(new HashSet<String>(Arrays.asList(
                "platformId", "platformName", "platformType")), platform.invalidFieldNames());
        platform.setPlatformId(PLATFORM_ID);
        assertEquals(new HashSet<String>(Arrays.asList("platformName", "platformType")), platform.invalidFieldNames());
        platform.setPlatformName(PLATFORM_NAME);
        assertEquals(new HashSet<String>(Arrays.asList("platformType")), platform.invalidFieldNames());
        platform.setPlatformType(PLATFORM_TYPE);
        assertEquals(new HashSet<String>(), platform.invalidFieldNames());
    }

    @Test
    public void testDuplicate() {
        Platform platform = new Platform();
        Platform dup = (Platform) (platform.duplicate(null));
        assertEquals(platform, dup);
        assertNotSame(platform, dup);

        platform.setPlatformId(PLATFORM_ID);
        platform.setPlatformName(PLATFORM_NAME);
        platform.setPlatformType(PLATFORM_TYPE);
        platform.setPlatformOwner(PLATFORM_OWNER);
        platform.setPlatformCountry(PLATFORM_COUNTRY);
        assertNotEquals(platform, dup);

        dup = (Platform) (platform.duplicate(null));
        assertEquals(platform, dup);
        assertNotSame(platform, dup);
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

}
