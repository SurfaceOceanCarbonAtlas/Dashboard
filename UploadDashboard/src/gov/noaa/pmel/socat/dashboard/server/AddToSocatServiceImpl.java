/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.AddToSocatService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of AddToSocatService
 * 
 * @author Karl Smith
 */
public class AddToSocatServiceImpl extends RemoteServiceServlet 
										implements AddToSocatService {

	private static final long serialVersionUID = -7086803355103349071L;

	@Override
	public void addCruisesToSocat(String username, String passhash, 
			HashSet<String> cruiseExpocodes, String archiveStatus) 
										throws IllegalArgumentException {
		// Authenticate the user
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected configuration error: " + ex.getMessage());
		}
		if ( ! dataStore.validateUser(username, passhash) )
			throw new IllegalArgumentException(
					"Invalid authentication credentials");

		CruiseFileHandler cruiseHandler = 
				dataStore.getCruiseFileHandler();
		// Update the SOCAT status of the cruises
		for ( String expocode : cruiseExpocodes ) {
			// Get the properties of this cruise
			DashboardCruise cruise = 
					cruiseHandler.getCruiseFromInfoFile(expocode);
			if ( cruise == null ) 
				throw new IllegalArgumentException(
						"Unknown cruise " + expocode);

			// TODO: add the cruise to SOCAT

			// Update the QC status for this cruise
			String qcStatus;
			String dataStatus = cruise.getDataCheckStatus();
			TreeSet<String> metaNames = cruise.getMetadataFilenames();
			if ( (metaNames.size() > 0) && 
				 (DashboardUtils.CHECK_STATUS_ACCEPTABLE.equals(dataStatus) ||
				  DashboardUtils.CHECK_STATUS_QUESTIONABLE.equals(dataStatus)) )
				qcStatus = DashboardUtils.QC_STATUS_SUBMITTED;
			else
				qcStatus = DashboardUtils.QC_STATUS_UNACCEPTABLE;
			cruise.setQcStatus(qcStatus);

			// Update the archive status for this cruise
			String doiStatus = cruise.getArchiveStatus();
			if ( (doiStatus == null) || ! doiStatus.startsWith(
					DashboardUtils.ARCHIVE_STATUS_ARCHIVED_PREFIX) ) {
				cruise.setArchiveStatus(archiveStatus);
				doiStatus = archiveStatus;
			}

			// Commit this update of the cruise properties
			cruiseHandler.saveCruiseToInfoFile(cruise, "Cruise " + expocode +
					" submitted to SOCAT by " + username + 
					" with initial QC status '" + qcStatus + 
					"' and archive status '" + doiStatus + "'");
		}
	}

}
