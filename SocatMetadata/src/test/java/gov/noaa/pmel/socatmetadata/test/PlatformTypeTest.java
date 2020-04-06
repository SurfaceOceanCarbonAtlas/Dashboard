package gov.noaa.pmel.socatmetadata.test;

import gov.noaa.pmel.socatmetadata.platform.PlatformType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlatformTypeTest {

    @Test
    public void testParse() {
        assertEquals(PlatformType.UNKNOWN, PlatformType.parse("Unknown"));
        assertEquals(PlatformType.SHIP, PlatformType.parse("Ship"));
        assertEquals(PlatformType.MOORING, PlatformType.parse("Mooring"));
        assertEquals(PlatformType.MOORING, PlatformType.parse("Buoy"));
        assertEquals(PlatformType.DRIFTING_BUOY, PlatformType.parse("Drifting Buoy"));
        assertEquals(PlatformType.UNKNOWN, PlatformType.parse("\t"));
        assertEquals(PlatformType.UNKNOWN, PlatformType.parse(null));
        assertEquals(PlatformType.UNKNOWN, PlatformType.parse("Sailboat"));
    }

    @Test
    public void testToString() {
        assertEquals("Unknown", PlatformType.UNKNOWN.toString());
        assertEquals("Ship", PlatformType.SHIP.toString());
        assertEquals("Mooring", PlatformType.MOORING.toString());
        assertEquals("Drifting Buoy", PlatformType.DRIFTING_BUOY.toString());
    }

}
