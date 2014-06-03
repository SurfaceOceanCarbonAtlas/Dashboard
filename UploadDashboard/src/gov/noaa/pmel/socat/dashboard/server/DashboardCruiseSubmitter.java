/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.nc.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.ome.OmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.uea.socat.sanitychecker.Output;

/**
 * Submits Dashboard cruise for SOCAT QC
 * 
 * @author Karl Smith
 */
public class DashboardCruiseSubmitter {

	CruiseFileHandler cruiseHandler;
	MetadataFileHandler metadataHandler;
	DashboardCruiseChecker cruiseChecker;
	DsgNcFileHandler dsgNcHandler;
	DatabaseRequestHandler databaseHandler;

	/**
	 * Create with the file handlers and data checker in the given data store.
	 */
	public DashboardCruiseSubmitter(DashboardDataStore dataStore) {
		cruiseHandler = dataStore.getCruiseFileHandler();
		metadataHandler = dataStore.getMetadataFileHandler();
		cruiseChecker = dataStore.getDashboardCruiseChecker();
		dsgNcHandler = dataStore.getDsgNcFileHandler();
		databaseHandler = dataStore.getDatabaseRequestHandler();
	}

	public void submitCruises(Set<String> cruiseExpocodes, 
			String archiveStatus, String localTimestamp, 
			boolean repeatSend, String submitter, String qcFlag, 
			String addlDocs) throws IllegalArgumentException {

		HashSet<String> ingestExpos = new HashSet<String>();
		HashSet<String> archiveExpos = new HashSet<String>();
		HashSet<String> cdiacExpos = new HashSet<String>();
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
				// QC flag to assign with this cruise
				String flag;
				if ( qcFlag != null ) {
					flag = qcFlag;
				}
				else if ( qcStatus.equals(DashboardUtils.QC_STATUS_NOT_SUBMITTED) ) {
					flag = "N";
				}
				else {
					flag = "U";
				}
				// Get the complete cruise data in standard units
				DashboardCruiseWithData cruiseData = 
						cruiseHandler.getCruiseDataFromFiles(expocode, 0, -1);
				Output output = cruiseChecker.standardizeCruiseData(cruiseData);
				// Reset an "N" or "U" flag to an "F" flag if the cruise has major issues
				if ( qcFlag == null ) {
					if ( cruiseData.getDataCheckStatus().equals(DashboardUtils.CHECK_STATUS_UNACCEPTABLE) ) {
						// Either checker failed, or geoposition WOCE flag of 4
						// Presumably this should never happen.
						flag = "F";
					}
				}
				// Get the OME metadata for this cruise
				OmeMetadata omeMData = new OmeMetadata(
						metadataHandler.getMetadataInfo(expocode, OmeMetadata.OME_FILENAME));
				if ( addlDocs != null ) {
					// Add the given additional documents (metadataHRefs from the database) 
					// to cruiseData so they will be added to the DSG file 
					TreeSet<String> addlDocsSet = cruiseData.getAddlDocs();
					addlDocsSet.addAll(Arrays.asList(addlDocs.split(" ; ")));
				}
				// Generate the NetCDF DSG file for this cruise
				dsgNcHandler.saveCruise(omeMData, cruiseData, flag);
				cruise.setQcStatus(DashboardUtils.QC_STATUS_SUBMITTED);
				// Update cruise with the any updates from the SanityChecker
				cruise.setDataCheckStatus(cruiseData.getDataCheckStatus());
				cruise.setNumErrorRows(cruiseData.getNumErrorRows());
				cruise.setNumWarnRows(cruiseData.getNumWarnRows());
				// Save the WOCE flags for the columns in the original cruise data
				int numDataCols = cruise.getDataColTypes().size();
				cruise.setWoceThreeRowIndices(new ArrayList<HashSet<Integer>>(
						cruiseData.getWoceThreeRowIndices().subList(0, numDataCols)));
				cruise.setWoceFourRowIndices(new ArrayList<HashSet<Integer>>(
						cruiseData.getWoceFourRowIndices().subList(0, numDataCols)));
				cruiseHandler.saveCruiseMessages(cruise.getExpocode(), output);
				// Create a SocatQCEvent for the cruise with the number of data rows with errors, warnings
				SocatQCEvent comment = new SocatQCEvent();
				comment.setFlag(null);
				comment.setExpocode(expocode);
				comment.setSocatVersion(cruise.getVersion());
				comment.setRegionID(null);
				comment.setFlagDate(new Date());
				comment.setUsername(DashboardUtils.SANITY_CHECKER_USERNAME);
				comment.setRealname(DashboardUtils.SANITY_CHECKER_REALNAME);
				String recFlag;
				if ( cruiseData.getNumErrorRows() > DashboardUtils.MAX_ACCEPTABLE_ERRORS ) {
					recFlag = "Recommend QC flag of 'F': ";
				}
				else {
					recFlag = "";
				}
				comment.setComment(recFlag + "Automated data check detected " + 
							Integer.toString(cruiseData.getNumErrorRows()) + 
							" data points with errors and " + 
							Integer.toString(cruiseData.getNumWarnRows()) + 
							" data points with warnings.");
				try {
					databaseHandler.addQCEvent(comment);
				} catch (SQLException ex) {
					throw new IllegalArgumentException(
							"Unable to add a QC comment with the Sanity Checker results:\n" +
							ex.getMessage());
				}

				// Generate the decimated-data DSG file from the full-data DSG file
				dsgNcHandler.decimateCruise(expocode);

				// Set up to save changes to version control
				changed = true;
				commitMsg += " submit with QC flag '" + flag + "'";
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
				commitMsg += " by user '" + submitter + "'";
				cruiseHandler.saveCruiseInfoToFile(cruise, commitMsg);
			}
		}

		// notify ERDDAP of new/updated cruises
		if ( ! ingestExpos.isEmpty() )
			dsgNcHandler.flagErddap(true);

		// TODO: ?modify cruise archive info in SOCAT for cruises in archiveExpos?

		// TODO: send data to CDIAC for cruises in cdiacExpos

	}
}
