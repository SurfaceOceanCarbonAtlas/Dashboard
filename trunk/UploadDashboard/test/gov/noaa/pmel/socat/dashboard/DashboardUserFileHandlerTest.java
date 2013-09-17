/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardUserFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

/**
 * @author Karl Smith
 */
public class DashboardUserFileHandlerTest {

	/**
	 * Test method for methods in 
	 * {@link gov.noaa.pmel.socat.dashboard.server.DashboardUserFileHandler}.
	 * This needs to be run after DashboardCruiseFileHandlerTest.
	 * @throws IOException
	 * @throws SVNException 
	 */
	@Test
	public void testDashboardUserFileHandler() throws IOException, SVNException {
		String username = "socatuser";
		String[] cruiseExpocodes = { 
				"FAKE20031205", 
				"GARBAGE202020"
		};
		String[] uploadFilenames = { 
				"fake20031205_revised.tsv",
				"garbage_data.tsv"
		};
		Date[] dataCheckDates = {
				null,
				new Date(System.currentTimeMillis())
		};
		Date[] metaCheckDates = {
				null,
				new Date(System.currentTimeMillis())
		};
		String[] qcStatuses = {
				"",
				"Suspended"
		};
		String[] archiveStatuses = {
				"",
				""
				
		};
		DashboardCruiseList cruiseList = new DashboardCruiseList();
		cruiseList.setUsername(username);
		for (int k = 0; k < cruiseExpocodes.length; k++) {
			DashboardCruise newCruise = new DashboardCruise();
			newCruise.setSelected(false);
			newCruise.setExpocode(cruiseExpocodes[k]);
			newCruise.setOwner(username);
			newCruise.setUploadFilename(uploadFilenames[k]);
			newCruise.setDataCheckDate(dataCheckDates[k]);
			newCruise.setMetaCheckDate(metaCheckDates[k]);
			newCruise.setQCStatus(qcStatuses[k]);
			newCruise.setArchiveStatus(archiveStatuses[k]);
			cruiseList.put(newCruise.getExpocode(), newCruise);
		}

		DashboardUserFileHandler handler = 
				DashboardDataStore.get().getUserFileHandler();
		assertNotNull( handler );

		handler.saveCruiseListing(cruiseList, "test check-in of fake cruise listing data");

		// The second cruise does not exist, so it should be 
		// automatically removed when the cruise list is retrieved
		cruiseList.remove(cruiseExpocodes[1]);

		DashboardCruiseList newListing = handler.getCruiseListing(username);
		assertEquals(cruiseList, newListing);
	}

}
