/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.nc.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.AddToSocatService;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import uk.ac.uea.socat.sanitychecker.Output;

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
		MetadataFileHandler metadataHandler = dataStore.getMetadataFileHandler();
		DashboardCruiseChecker cruiseChecker = dataStore.getDashboardCruiseChecker();
		DsgNcFileHandler dsgNcHandler = dataStore.getDsgNcFileHandler();
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
			String commitMsg = "Expocode " + expocode;

			String qcStatus = cruise.getQcStatus();
			if ( qcStatus.equals(DashboardUtils.QC_STATUS_NOT_SUBMITTED) ||
				 qcStatus.equals(DashboardUtils.QC_STATUS_SUSPENDED) ||
				 qcStatus.equals(DashboardUtils.QC_STATUS_UNACCEPTABLE) ) { 
				// Get the complete cruise data in standard units
				DashboardCruiseWithData cruiseData = 
						cruiseHandler.getCruiseDataFromFiles(expocode, 0, -1);
				Output output = cruiseChecker.standardizeCruiseData(cruiseData);
				// Get the OME metadata for this cruise
				OmeMetadata omeMData = new OmeMetadata(
						metadataHandler.getMetadataInfo(expocode, OmeMetadata.OME_FILENAME));
				// Generate the NetCDF DSG file for this cruise
				dsgNcHandler.saveCruise(omeMData, cruiseData); 
				// Update the QC status for this cruise
				String dataStatus = cruiseData.getDataCheckStatus();
				if ( dataStatus.equals(DashboardUtils.CHECK_STATUS_ACCEPTABLE) || 
					 dataStatus.startsWith(DashboardUtils.CHECK_STATUS_WARNINGS_PREFIX) )
					qcStatus = DashboardUtils.QC_STATUS_SUBMITTED;
				else
					qcStatus = DashboardUtils.QC_STATUS_UNACCEPTABLE;
				cruise.setQcStatus(qcStatus);
				// Update cruise with the any updates from the SanityChecker
				cruise.setDataCheckStatus(dataStatus);
				cruise.setNumErrorMsgs(cruiseData.getNumErrorMsgs());
				cruise.setNumWarnMsgs(cruiseData.getNumWarnMsgs());
				// subList in case year, month,day, hour, minute, seconds columns were added
				int numDataCols = cruise.getDataColTypes().size();
				cruise.setWoceThreeRowIndices(new ArrayList<HashSet<Integer>>(
						cruiseData.getWoceThreeRowIndices().subList(0, numDataCols)));
				cruise.setWoceFourRowIndices(new ArrayList<HashSet<Integer>>(
						cruiseData.getWoceFourRowIndices().subList(0, numDataCols)));
				cruiseHandler.saveCruiseMessages(cruise.getExpocode(), output);
				changed = true;
				commitMsg += " submit with QC status '" + qcStatus + "'";
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

		// TODO: notify ERDDAP of new/updated cruises given in ingestExpos

		// TODO: ?modify cruise archive info in SOCAT for cruises in archiveExpos?

		// TODO: send data to CDIAC for cruises in cdiacExpos

	}

}
