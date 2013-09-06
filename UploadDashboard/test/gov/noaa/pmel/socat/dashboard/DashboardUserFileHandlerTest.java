/**
 * 
 */
package gov.noaa.pmel.socat.dashboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardUserFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseListing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

/**
 * @author Karl Smith
 */
public class DashboardUserFileHandlerTest {

	/**
	 * Test method for methods in 
	 * {@link gov.noaa.pmel.socat.dashboard.server.DashboardUserFileHandler}.
	 * @throws IOException
	 * @throws SVNException 
	 */
	@Test
	public void testDashboardUserFileHandler() throws IOException, SVNException {
		String username = "socatuser";
		String[] cruiseExpocodes = { 
				"FAKE19991206", 
				"FAKE20030318", 
				"FAKE20060213", 
				"FAKE20070123", 
				"FAKE20091129" 
		};
		String[] uploadFilenames = { 
				"fake19991206_rec.tsv", 
				"fake20030318_rev.tsv", 
				"fake20060213.tsv", 
				"fake20070123.tsv", 
				"fake20091129.tsv" 
		};
		Random random = new Random();
		Date[] dataCheckDates = { 
				new Date(System.currentTimeMillis() - random.nextInt(10000000)),
				new Date(System.currentTimeMillis() - random.nextInt(10000000)),
				new Date(System.currentTimeMillis() - random.nextInt(10000000)),
				new Date(System.currentTimeMillis() - random.nextInt(10000000)),
				new Date(System.currentTimeMillis() - random.nextInt(10000000)) 
		};
		Date[] metaCheckDates = { 
				new Date(System.currentTimeMillis() - random.nextInt(10000000)),
				new Date(System.currentTimeMillis() - random.nextInt(10000000)),
				new Date(System.currentTimeMillis() - random.nextInt(10000000)),
				new Date(System.currentTimeMillis() - random.nextInt(10000000)),
				new Date(System.currentTimeMillis() - random.nextInt(10000000)) 
		};
		String[] qcStatuses = {
				"Accepted, C",
				"Accepted, B",
				"Suspended",
				"Submitted",
				"Not Submitted"
		};
		String[] archiveStatuses = {
				"CDIAC, doi:xxxxx/xxxxx",
				"Next SOCAT Release",
				"Not Archived",
				"PANGAEA, doi unknown",
				"Not Archived"
		};
		ArrayList<DashboardCruise> cruises = 
				new ArrayList<DashboardCruise>(cruiseExpocodes.length);
		for (int k = 0; k < cruiseExpocodes.length; k++) {
			DashboardCruise newCruise = new DashboardCruise();
			newCruise.setSelected(false);
			newCruise.setExpocode(cruiseExpocodes[k]);
			newCruise.setUploadFilename(uploadFilenames[k]);
			newCruise.setDataCheckDate(dataCheckDates[k]);
			newCruise.setMetaCheckDate(metaCheckDates[k]);
			newCruise.setQCStatus(qcStatuses[k]);
			newCruise.setArchiveStatus(archiveStatuses[k]);
			cruises.add(newCruise);
		}
		DashboardCruiseListing cruiseListing = new DashboardCruiseListing();
		cruiseListing.setUsername(username);
		cruiseListing.setCruises(cruises);

		DashboardUserFileHandler handler = 
				DashboardDataStore.get().getUserFileHandler();
		assertNotNull( handler );

		handler.saveCruiseListing(cruiseListing, "test check-in of fake cruise listing data");

		DashboardCruiseListing newListing = handler.getCruiseListing(username);
		assertEquals(cruiseListing, newListing);
	}

}
