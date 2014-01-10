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

	private static final long serialVersionUID = -2864373216622024236L;

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

			// Update the archive status for this cruise.  
			// Does not offer (at this time) the "archive now with CDIAC" 
			// option, so no timestamp needed.
			cruise.setArchiveStatus(archiveStatus);

			// Commit this update of the cruise properties
			cruiseHandler.saveCruiseInfoToFile(cruise, "Cruise " + expocode +
					" submitted to SOCAT by " + username + 
					" with initial QC status '" + qcStatus + 
					"' and archive status '" + archiveStatus + "'");

			// TODO: add the cruise to SOCAT

		}
	}

	@Override
	public void setCruiseArchiveStatus(String username, String passhash,
			TreeSet<String> expocodes, String archiveStatus, 
			String localTimestamp, boolean repeatSend) {
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

		TreeSet<String> changedExpos = new TreeSet<String>();
		TreeSet<String> cdiacExpos = new TreeSet<String>();
		for ( String expo : expocodes ) {
			// Get the properties of this cruise
			DashboardCruise cruise = dataStore.getCruiseFileHandler()
											  .getCruiseFromInfoFile(expo);
			if ( cruise == null ) 
				throw new IllegalArgumentException("Unknown cruise " + expo);

			String commitMsg = "Archive status of cruise " + expo + " updated by " + 
					username + " to '" + archiveStatus + "'";

			if ( ! archiveStatus.equals(DashboardUtils.ARCHIVE_STATUS_SENT_CDIAC) ) {
				// No changes to archive status, which is not send to CDIAC; skip it
				if ( archiveStatus.equals(cruise.getArchiveStatus()) )
					continue;
			}
			else if ( repeatSend || cruise.getCdiacDate().isEmpty() ) {
				// Send to CDIAC; repeat the send or has never been sent
				commitMsg += " with CDIAC date of '" + localTimestamp + "'";
				cruise.setCdiacDate(localTimestamp);
				cdiacExpos.add(expo);
			}
			else if ( archiveStatus.equals(cruise.getArchiveStatus()) ) {
				// No changes to the archive status or the CDIAC timestamp; skip it
				continue;
			}
			changedExpos.add(expo);

			// Update the archive status for this cruise
			cruise.setArchiveStatus(archiveStatus);

			// Commit this update of the cruise properties
			dataStore.getCruiseFileHandler().saveCruiseInfoToFile(cruise, commitMsg);
		}

		// TODO: modify the cruise archive status in SOCAT for changedExpos

		// TODO: send notice to CDIAC to get cruises named in cdiacExpos

	}

}
