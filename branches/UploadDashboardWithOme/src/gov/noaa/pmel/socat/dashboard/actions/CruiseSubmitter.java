/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.socat.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SocatEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatQCEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Submits Dashboard cruise for SOCAT QC
 * 
 * @author Karl Smith
 */
public class CruiseSubmitter {

	CruiseFileHandler cruiseHandler;
	CheckerMessageHandler msgHandler;
	MetadataFileHandler metadataHandler;
	CruiseChecker cruiseChecker;
	DsgNcFileHandler dsgNcHandler;
	DatabaseRequestHandler databaseHandler;
	String socatVersion;

	/**
	 * @param configStore
	 * 		create with the file handlers and data checker in this data store.
	 */
	public CruiseSubmitter(DashboardConfigStore configStore) {
		cruiseHandler = configStore.getCruiseFileHandler();
		msgHandler = configStore.getCheckerMsgHandler();
		metadataHandler = configStore.getMetadataFileHandler();
		cruiseChecker = configStore.getDashboardCruiseChecker();
		dsgNcHandler = configStore.getDsgNcFileHandler();
		databaseHandler = configStore.getDatabaseRequestHandler();
		socatVersion = configStore.getSocatUploadVersion();
	}

	/**
	 * Submit a dataset for SOCAT QC.  This sanity checks and generates
	 * DSG and decimated DSG files for datasets which are editable 
	 * (have a QC status of {@link DashboardUtils#QC_STATUS_NOT_SUBMITTED}, 
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
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid,
	 * 		if the data or metadata is missing,
	 * 		if the DSG files cannot be created, or
	 * 		if there was a problem saving the updated dataset information
	 * 		(including archive status)
	 */
	public void submitCruises(Set<String> cruiseExpocodes, 
			String archiveStatus, String localTimestamp, 
			boolean repeatSend, String submitter) throws IllegalArgumentException {

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

			if ( Boolean.TRUE.equals(cruise.isEditable()) ) {
				// QC flag to assign with this cruise
				Character flag;
				String qcStatus = cruise.getQcStatus();
				if ( SocatQCEvent.QC_STATUS_NOT_SUBMITTED.equals(qcStatus) ) {
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
				 *  Convert the cruise data into standard units and setting to null
				 *  those data lines which the PI has marked as bad.  
				 *  Also adds and assigns year, month, day, hour, minute, second, 
				 *  and WOCE columns if not present.  SanityChecker WOCE-4 flags
				 *  are added to the WOCE column.
				 *  Note: this saves messages and assigns WOCE flags with row 
				 *  numbers of the trimmed data.
				 */
				if ( ! cruiseChecker.standardizeCruiseData(cruiseData) ) {
					if ( cruiseData.getNumDataRows() < 1 )
						errorMsgs.add(expocode + ": unacceptable; all data points marked bad");
					else if (  ! cruiseChecker.checkProcessedOkay() )
						errorMsgs.add(expocode + ": unacceptable; automated checking of data failed");
					else if ( cruiseChecker.hadGeopositionErrors() )
						errorMsgs.add(expocode + ": unacceptable; automated checking of data " +
								"detected longitude, latitude, date, or time value errors");
					else
						errorMsgs.add(expocode + ": unacceptable for unknown reason - unexpected");
					continue;
				}

				try {
					// Get the OME metadata for this cruise
					DashboardMetadata omeInfo = metadataHandler.getMetadataInfo(expocode, DashboardMetadata.OME_FILENAME);
					DashboardOmeMetadata omeMData = new DashboardOmeMetadata(omeInfo, metadataHandler);

					// Generate the NetCDF DSG file, enhanced by Ferret, for this 
					// possibly modified and WOCEd cruise data
					dsgNcHandler.saveCruise(omeMData, cruiseData, flag.toString());

					// Generate the decimated-data DSG file from the full-data DSG file
					dsgNcHandler.decimateCruise(expocode);
				} catch (Exception ex) {
					errorMsgs.add(expocode + ": unacceptable; " + ex.getMessage());
					continue;
				}

				// Update cruise info with status values from cruiseData
				cruise.setQcStatus(qcStatus);
				cruise.setDataCheckStatus(cruiseData.getDataCheckStatus());
				cruise.setNumErrorRows(cruiseData.getNumErrorRows());
				cruise.setNumWarnRows(cruiseData.getNumWarnRows());

				// Create the QCEvent for submitting the initial QC flags
				SocatQCEvent initQC = new SocatQCEvent();
				initQC.setExpocode(expocode);
				initQC.setSocatVersion(socatVersion);
				initQC.setFlagDate(new Date());
				initQC.setUsername(SocatEvent.SANITY_CHECKER_USERNAME);
				initQC.setRealname(SocatEvent.SANITY_CHECKER_REALNAME);
				// Add the initial QC flag in each region only for new and updated cruises
				initQC.setFlag(flag);
				if ( SocatQCEvent.QC_NEW_FLAG.equals(flag) )
					initQC.setComment("Initial QC flag for new dataset");
				else
					initQC.setComment("Initial QC flag for updated dataset");

				// Get the regions in which the cruise reports 
				TreeSet<Character> regionsSet;
				try {
					regionsSet = dsgNcHandler.getDataRegionsSet(expocode);
				} catch (Exception ex) {
					throw new RuntimeException("Unexpected problems reading region IDs " +
							"from the newly created full-data DSG file for " + 
							expocode + ": " + ex.getMessage());
				}
				// Add the global and regional initial flags
				try {
					initQC.setRegionID(DataLocation.GLOBAL_REGION_ID);
					databaseHandler.addQCEvent(initQC);
					for ( Character regionID : regionsSet ) {
						initQC.setRegionID(regionID);
						databaseHandler.addQCEvent(initQC);
					}
				} catch (SQLException ex) {
					throw new IllegalArgumentException(
							"Unable to add an initial QC flag:\n    " + ex.getMessage());
				}

				// All cruises - remark on the number of data rows with error and warnings
				initQC.setRegionID(DataLocation.GLOBAL_REGION_ID);
				initQC.setFlag(SocatQCEvent.QC_COMMENT);
				initQC.setComment("Automated data check found " + 
						Integer.toString(cruiseData.getNumErrorRows()) + 
						" data points with errors and " + 
						Integer.toString(cruiseData.getNumWarnRows()) + 
						" data points with warnings.");
				try {
					databaseHandler.addQCEvent(initQC);
				} catch (SQLException ex) {
					throw new IllegalArgumentException("Unable to add an initial " +
							"QC comment with the number of error and warnings:\n    " + 
							ex.getMessage());
				}

				// Generate and add the WOCE flags from the SanityChecker results,
				// as well as the user-provided WOCE flags, to the database
				ArrayList<SocatWoceEvent> initWoceList;
				try {
					initWoceList = msgHandler.generateWoceEvents(cruiseData, dsgNcHandler);
				} catch (IOException ex) {
					throw new IllegalArgumentException(ex);
				}
				try {
					databaseHandler.resetWoceEvents(expocode);
					for ( SocatWoceEvent woceEvent : initWoceList ) {
						databaseHandler.addWoceEvent(woceEvent);
					}
				} catch (SQLException ex) {
					throw new IllegalArgumentException("Unable to add a WOCE flag "
							+ "with the data checking results:\n    " + ex.getMessage());
				}

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
			dsgNcHandler.flagErddap(true, true);

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
