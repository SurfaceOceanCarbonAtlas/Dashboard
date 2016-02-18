/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.programs.RegenerateDsgs;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import org.junit.Test;

/**
 * Tests of method in {@link gov.noaa.pmel.socat.dashboard.programs.RegenerateDsgs}
 * 
 * @author Karl Smith
 */
public class RegenerateDsgsTest {

	/**
	 * Test method for {@link gov.noaa.pmel.socat.dashboard.programs.RegenerateDsgs#regenerateDsgFiles(java.lang.String, boolean)}.
	 * Uses the full-data DSG file in an existing Dashboard installaltion.
	 */
	@Test
	public void testRegenerateDsgFiles() throws IllegalArgumentException, IOException {
		final String expocode = "06AQ19911114";

		// Get the default dashboard configuration
		DashboardConfigStore configStore = null;		
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

		// Read the original data and metadata
		CruiseDsgNcFile fullDataDsg = dsgHandler.getDsgNcFile(expocode);
		fullDataDsg.readMetadata();
		fullDataDsg.readData();
		SocatMetadata origMeta = fullDataDsg.getMetadata();
		ArrayList<SocatCruiseData> origData = fullDataDsg.getDataList();

		// Regenerate the DSG files
		RegenerateDsgs regenerator = new RegenerateDsgs(configStore);
		if ( ! regenerator.regenerateDsgFiles(expocode, true) ) {
			fail("regenerateDsgFiles returned false when 'always' set to true");
		}

		// Re-read the data and metadata
		fullDataDsg.readMetadata();
		fullDataDsg.readData();
		SocatMetadata updatedMeta = fullDataDsg.getMetadata();
		ArrayList<SocatCruiseData> updatedData = fullDataDsg.getDataList();

		// Test that nothing has changed
		assertEquals(origMeta, updatedMeta);
		assertEquals(origData.size(), updatedData.size());
		for (int k = 0; k < origData.size(); k++) {
			assertEquals(origData.get(k), updatedData.get(k));
		}

	}

}
