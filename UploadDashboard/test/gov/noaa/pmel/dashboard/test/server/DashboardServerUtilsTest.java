/**
 * 
 */
package gov.noaa.pmel.dashboard.test.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import gov.noaa.pmel.dashboard.datatype.LonLatConverter;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;

/**
 * Unit tests for methods in {@link gov.noaa.pmel.dashboard.server.DashboardServerUtils}
 * @author Karl Smith
 */
public class DashboardServerUtilsTest {

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashboardServerUtils#getKeyForName(java.lang.String)}.
	 */
	@Test
	public void testGetKeyForName() {
		assertEquals("xco2atmdryinterp", DashboardServerUtils.getKeyForName("xCO2_atm_dry_interp"));
		assertEquals("xco2atmdryinterp", DashboardServerUtils.getKeyForName("xCO2 Atm Dry Interp"));
		assertEquals("xco2atmdryinterp", DashboardServerUtils.getKeyForName("xCO2; Atm; Dry; Interp"));
		assertEquals("xco2atmdryinterp", DashboardServerUtils.getKeyForName("xCO2, Atm, Dry, Interp"));
		assertEquals("other", DashboardServerUtils.getKeyForName("(other)"));
		assertEquals("tempc", DashboardServerUtils.getKeyForName("Temp [" + LonLatConverter.DEGREE_SYMBOL + "C]"));
		assertEquals("tempc", DashboardServerUtils.getKeyForName("Temp [ºC]"));
		assertEquals( "k" + DashboardOmeMetadata.oUmlaut + "rtzinger", 
				DashboardServerUtils.getKeyForName("K" + DashboardOmeMetadata.OUmlaut + "rt*Zinger") );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashboardServerUtils#getDatasetIDFromName(java.lang.String)}.
	 */
	@Test
	public void testGetDatasetIDFromName() {
		assertEquals( "ATLANTIS120115300", 
				DashboardServerUtils.getDatasetIDFromName("Atlantis 12-01-15 3:00") );
		assertEquals( "N" + DashboardOmeMetadata.UUmlaut + "MBER20", 
				DashboardServerUtils.getDatasetIDFromName("\"N" + DashboardOmeMetadata.uUmlaut + 
						"mber 2' 0" + LonLatConverter.DEGREE_SYMBOL + "\t\"") );
	}

	/**
	 * Test method for {@link gov.noaa.pmel.dashboard.server.DashboardServerUtils#checkDatasetID(java.lang.String)}.
	 */
	@Test
	public void testCheckDatasetID() {
		assertEquals( "ATLANTIS120115300", 
				DashboardServerUtils.checkDatasetID("Atlantis120115300") );
		assertEquals( "N" + DashboardOmeMetadata.EGrave + "VER20", 
				DashboardServerUtils.checkDatasetID("N" + DashboardOmeMetadata.eGrave + "ver20") );
		boolean caught = false;
		try {
			DashboardServerUtils.checkDatasetID("Atlantis 12");
		} catch ( IllegalArgumentException ex ) {
			caught = true;
		}
		if ( ! caught ) {
			fail("Invalid character did not raise an Exception");
		}
		caught = false;
		try {
			DashboardServerUtils.checkDatasetID("Atlantis_12");
		} catch ( IllegalArgumentException ex ) {
			caught = true;
		}
		if ( ! caught ) {
			fail("Invalid character did not raise an Exception");
		}
		caught = false;
		try {
			DashboardServerUtils.checkDatasetID("Atlantis-12");
		} catch ( IllegalArgumentException ex ) {
			caught = true;
		}
		if ( ! caught ) {
			fail("Invalid character did not raise an Exception");
		}
	}

}
