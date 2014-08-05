/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.nc.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.ome.OmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Submits Dashboard cruise for SOCAT QC
 * 
 * @author Karl Smith
 */
public class DashboardCruiseSubmitter {

	CruiseFileHandler cruiseHandler;
	CheckerMessageHandler msgHandler;
	MetadataFileHandler metadataHandler;
	DashboardCruiseChecker cruiseChecker;
	DsgNcFileHandler dsgNcHandler;
	DatabaseRequestHandler databaseHandler;

	/**
	 * Create with the file handlers and data checker in the given data store.
	 */
	DashboardCruiseSubmitter(DashboardDataStore dataStore) {
		cruiseHandler = dataStore.getCruiseFileHandler();
		msgHandler = dataStore.getCheckerMsgHandler();
		metadataHandler = dataStore.getMetadataFileHandler();
		cruiseChecker = dataStore.getDashboardCruiseChecker();
		dsgNcHandler = dataStore.getDsgNcFileHandler();
		databaseHandler = dataStore.getDatabaseRequestHandler();
	}

	/**
	 * Submit a dataset for SOCAT QC.  This sanity checks and generates
	 * DSG and decimated DSG files for datasets which have a QC status 
	 * of {@link DashboardUtils#QC_STATUS_NOT_SUBMITTED}, 
	 * {@link DashboardUtils#QC_STATUS_UNACCEPTABLE},
	 * {@link DashboardUtils#QC_STATUS_SUSPENDED}, or
	 * {@link DashboardUtils#QC_STATUS_EXCLUDED}.
	 * For all cruises, the archive status is updated to that given.
	 * 
	 * TODO (stubbed): If the archive status is {@link DashboardUtils#ARCHIVE_STATUS_SENT_CDIAC},
	 * the archive request is sent to CDIAC for dataset which have not been sent,
	 * or for all datasets if repeatSend is true.
	 * 
	 * @param cruiseExpocodes
	 * 		expocodes of the datasets to submit
	 * @param archiveStatus
	 * 		archive status to set for these cruises
	 * @param localTimestamp
	 * 		local timestamp to associate with this submission
	 * @param repeatSend
	 * 		re-send request to CDIAC to archive for datasets 
	 * 		which already had a request sent?
	 * @param submitter
	 * 		user performing this submit 
	 * @param qcFlag
	 * 		if not null, flag to associate with this submit.
	 * 		This is only for transferring v2 cruises and 
	 * 		should be null for standard use.
	 * @param addlDocs
	 * 		if not null, additional documents (metadata HRefs)
	 * 		to include.  This is only for transferring v2 cruises 
	 * 		and should be null for standard use. 
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid, or
	 * 		if the data or metadata is missing, or
	 * 		if the sanity checker fails or gives a geoposition error, or
	 * 		if the DSG files cannot be created, or
	 * 		if there was a problem saving the updated dataset information
	 * 		(including archive status)
	 */
	public void submitCruises(Set<String> cruiseExpocodes, 
			String archiveStatus, String localTimestamp, 
			boolean repeatSend, String submitter, String qcFlag, 
			String addlDocs) throws IllegalArgumentException {

		HashSet<String> ingestExpos = new HashSet<String>();
		HashSet<String> archiveExpos = new HashSet<String>();
		HashSet<String> cdiacExpos = new HashSet<String>();
		ArrayList<String> errorMsgs = new ArrayList<String>();
		for ( String expocode : cruiseExpocodes ) {
			// Get the properties of this cruise
			DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
			if ( cruise == null ) 
				throw new IllegalArgumentException("Unknown dataset " + expocode);

			boolean changed = false;
			String commitMsg = "Expocode " + expocode;

			String qcStatus = cruise.getQcStatus();
			if ( qcStatus.equals(SocatQCEvent.QC_STATUS_NOT_SUBMITTED) ||
				 qcStatus.equals(SocatQCEvent.QC_STATUS_UNACCEPTABLE) || 
				 qcStatus.equals(SocatQCEvent.QC_STATUS_SUSPENDED) ||
				 qcStatus.equals(SocatQCEvent.QC_STATUS_EXCLUDED) ) {

				// QC flag to assign with this cruise
				Character flag;
				if ( (qcFlag != null) && ! qcFlag.isEmpty() ) {
					flag = qcFlag.charAt(0);
					qcStatus = SocatQCEvent.FLAG_STATUS_MAP.get(flag);
					if ( qcStatus == null )
						throw new IllegalArgumentException("Unknown QC flag '" + qcFlag + "'");
				}
				else if ( qcStatus.equals(SocatQCEvent.QC_STATUS_NOT_SUBMITTED) ) {
					flag = SocatQCEvent.QC_NEW_FLAG;
					qcStatus = SocatQCEvent.QC_STATUS_SUBMITTED;
				}
				else {
					flag = SocatQCEvent.QC_UPDATED_FLAG;
					qcStatus = SocatQCEvent.QC_STATUS_SUBMITTED;
				}

				// Get the complete original cruise data
				DashboardCruiseWithData cruiseData = 
						cruiseHandler.getCruiseDataFromFiles(expocode, 0, -1);

				/*
				 *  Convert the cruise data into standard units after removing 
				 *  data lines with missing values for longitude, latitude, date, 
				 *  time, or timestamp, or which the PI has marked as bad.  
				 *  Also adds and assigns year, month, day, hour, minute, second, 
				 *  and WOCE columns if not present.  SanityChecker WOCE-4 flags
				 *  are added to the WOCE column.
				 *  Note: this saves messages and assigns WOCE flags with row 
				 *  numbers of the trimmed data.
				 */
				if ( ! cruiseChecker.standardizeCruiseData(cruiseData) ) {
					errorMsgs.add(expocode + ": unacceptable; automated checking of data failed");
					continue;
				}
				if ( (qcFlag == null) && cruiseChecker.hadGeopositionErrors() ) {
					errorMsgs.add(expocode + ": unacceptable; automated checking of data " +
							"detected longitude, latitude, date, or time value errors");
					continue;
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

				// Generate the NetCDF DSG file, enhanced by Ferret, for this 
				// possibly modified and WOCEd cruise data
				dsgNcHandler.saveCruise(omeMData, cruiseData, flag.toString());

				// Update cruise info with status values from cruiseData
				cruise.setQcStatus(qcStatus);
				cruise.setDataCheckStatus(cruiseData.getDataCheckStatus());
				cruise.setNumErrorRows(cruiseData.getNumErrorRows());
				cruise.setNumWarnRows(cruiseData.getNumWarnRows());
				
				// Create a SocatQCEvent for every region of the cruise 
				TreeSet<Character> regionsSet;
				try {
					regionsSet = dsgNcHandler.getDsgNcFile(expocode).readDataRegions();
				} catch (Exception ex) {
					throw new RuntimeException("Unexpected problems reading region IDs "
							+ "from the newly created full-data DSG file for " + 
							expocode + ": " + ex.getMessage());
				}
				regionsSet.remove(DataLocation.GLOBAL_REGION_ID);
				ArrayList<Character> regions = new ArrayList<Character>(regionsSet.size() + 1);
				// Add the global flag first because the database request handler 
				// expects there to always be a global flag 
				regions.add(DataLocation.GLOBAL_REGION_ID);
				regions.addAll(regionsSet);

				// Give the number of data rows with errors, warnings as the message
				SocatQCEvent comment = new SocatQCEvent();
				comment.setFlag(flag);
				comment.setExpocode(expocode);
				comment.setSocatVersion(cruise.getVersion());
				comment.setFlagDate(new Date());
				comment.setUsername(DashboardUtils.SANITY_CHECKER_USERNAME);
				comment.setRealname(DashboardUtils.SANITY_CHECKER_REALNAME);
				String recFlag;
				if ( cruiseData.getNumErrorRows() > DashboardUtils.MAX_ACCEPTABLE_ERRORS ) {
					recFlag = "Recommend QC flag of " + SocatQCEvent.QC_UNACCEPTABLE_FLAG + ": ";
				}
				else {
					recFlag = "";
				}
				comment.setComment(recFlag + "Automated data check found " + 
						Integer.toString(cruiseData.getNumErrorRows()) + 
						" data points with errors and " + 
						Integer.toString(cruiseData.getNumWarnRows()) + 
						" data points with warnings.");

				try {
					for ( Character regionID : regions ) {
						comment.setRegionID(regionID);
						databaseHandler.addQCEvent(comment);
					}
				} catch (SQLException ex) {
					throw new IllegalArgumentException("Unable to add a QC comment "
							+ "with the data checking results:\n" + ex.getMessage());
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

		// If any cruise submit errors, return the error messages
		// TODO: do this in a return message, not an IllegalArgumentException
		if ( errorMsgs.size() > 0 ) {
			StringBuilder sb = new StringBuilder();
			for ( String msg : errorMsgs ) { 
				sb.append(msg);
				sb.append("\n");
			}
			throw new IllegalArgumentException(sb.toString());
		}
	}
}
