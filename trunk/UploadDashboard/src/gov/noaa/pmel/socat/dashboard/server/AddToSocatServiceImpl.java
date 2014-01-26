/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.AddToSocatService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.IOException;
import java.util.HashSet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server side implementation of AddToSocatService
 * 
 * @author Karl Smith
 */
public class AddToSocatServiceImpl extends RemoteServiceServlet 
										implements AddToSocatService {

	private static final long serialVersionUID = -177066153383975100L;

	@Override
	public void addCruisesToSocat(String username, String passhash, 
			HashSet<String> cruiseExpocodes, String archiveStatus, 
			String localTimestamp, boolean repeatSend) 
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
			throw new IllegalArgumentException("Invalid authentication credentials");

		CruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();
		HashSet<String> ingestExpos = new HashSet<String>();
		HashSet<String> archiveExpos = new HashSet<String>();
		HashSet<String> cdiacExpos = new HashSet<String>();

		// Update the SOCAT status of the cruises
		for ( String expocode : cruiseExpocodes ) {
			// Get the properties of this cruise
			DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
			if ( cruise == null ) 
				throw new IllegalArgumentException("Unknown cruise " + expocode);

			boolean changed = false;
			String commitMsg = "Cruise '" + expocode + "'";

			String qcStatus = cruise.getQcStatus();
			if ( qcStatus.equals(DashboardUtils.QC_STATUS_NOT_SUBMITTED) ||
				 qcStatus.equals(DashboardUtils.QC_STATUS_SUSPENDED) ||
				 qcStatus.equals(DashboardUtils.QC_STATUS_UNACCEPTABLE) ) { 
				// Update the QC status for this cruise
				String dataStatus = cruise.getDataCheckStatus();
				String omeFilename = cruise.getOmeFilename();
				if ( ( ! omeFilename.isEmpty() ) && 
					 ( DashboardUtils.CHECK_STATUS_ACCEPTABLE.equals(dataStatus) || 
					   DashboardUtils.CHECK_STATUS_QUESTIONABLE.equals(dataStatus) ) )
					qcStatus = DashboardUtils.QC_STATUS_SUBMITTED;
				else
					qcStatus = DashboardUtils.QC_STATUS_UNACCEPTABLE;
				cruise.setQcStatus(qcStatus);
				changed = true;
				commitMsg += " add with QC status '" + qcStatus + "'";
				ingestExpos.add(expocode);
			}

			if ( ! archiveStatus.equals(cruise.getArchiveStatus()) ) {
				// Update the archive status
				cruise.setArchiveStatus(archiveStatus);
				changed = true;
				commitMsg += " archive status '" + archiveStatus + "'"; 
				archiveExpos.add(expocode);
			}

			if ( archiveStatus.equals(DashboardUtils.ARCHIVE_STATUS_SENT_CDIAC) ) {
				if ( repeatSend || cruise.getCdiacDate().isEmpty() ) {
					// Send (or re-send) the original cruise data and metadata to CDIAC
					cruise.setCdiacDate(localTimestamp);
					changed = true;
					commitMsg += " send to CDIAC '" + localTimestamp + "'";
					cdiacExpos.add(expocode);
				}
			}

			if ( changed ) {
				// Commit this update of the cruise properties
				commitMsg += " by user '" + username + "'";
				cruiseHandler.saveCruiseInfoToFile(cruise, commitMsg);				
			}
		}

		// TODO: add ingestExpos cruises to SOCAT

		// TODO?: modify cruise archive info in SOCAT for archiveExpos ?

		// TODO: send data to CDIAC for cruises in cdiacExpos

	}

}
