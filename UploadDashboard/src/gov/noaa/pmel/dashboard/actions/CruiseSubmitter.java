/**
 * 
 */
package gov.noaa.pmel.dashboard.actions;

import gov.noaa.pmel.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.handlers.SocatFilesBundler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCEvent;
import gov.noaa.pmel.dashboard.shared.WoceEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

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
	KnownDataTypes knownDataFileTypes;
	DatabaseRequestHandler databaseHandler;
	SocatFilesBundler cdiacBundler;
	String socatVersion;
	Logger logger;

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
		knownDataFileTypes = configStore.getKnownDataFileTypes();
		databaseHandler = configStore.getDatabaseRequestHandler();
		cdiacBundler = configStore.getCdiacFilesBundler();
		socatVersion = configStore.getSocatUploadVersion();
		logger = Logger.getLogger(getClass());
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
	 * If the archive status is {@link DashboardUtils#ARCHIVE_STATUS_SENT_CDIAC},
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
				if ( DashboardUtils.QC_STATUS_NOT_SUBMITTED.equals(qcStatus) ) {
					flag = DashboardUtils.QC_NEW_FLAG;
					qcStatus = DashboardUtils.QC_STATUS_SUBMITTED;
				}
				else {
					flag = DashboardUtils.QC_UPDATED_FLAG;
					qcStatus = DashboardUtils.QC_STATUS_SUBMITTED;
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
					DashboardMetadata omeInfo = metadataHandler.getMetadataInfo(expocode, DashboardUtils.OME_FILENAME);
					if ( ! socatVersion.equals(omeInfo.getVersion()) ) {
						metadataHandler.saveMetadataInfo(omeInfo, "Update metadata SOCAT version number to " + 
								socatVersion + " with submit for QC of " + expocode, false);
					}
					DashboardOmeMetadata omeMData = new DashboardOmeMetadata(omeInfo, metadataHandler);

					String socatVersionStatus = databaseHandler.getSocatVersionStatus(expocode);
					if ( socatVersionStatus.isEmpty() ) {
						// New dataset to the database
						socatVersionStatus = socatVersion + "N";
					}
					else {
						String status = socatVersionStatus.substring(socatVersionStatus.length() - 1);
						if ( ! "U".equals(status) ) {
							double newVers;
							try {
								newVers = Math.floor(Double.parseDouble(socatVersion) * 10.0) / 10.0;
							} catch (NumberFormatException ex) {
								throw new RuntimeException("Unexpected new SOCAT version of '" + socatVersion + "'");
							}
							String oldVersion = socatVersionStatus.substring(0, socatVersionStatus.length() - 1);
							double oldVers;
							try {
								oldVers = Math.floor(Double.parseDouble(oldVersion) * 10.0) / 10.0;
							} catch (NumberFormatException ex) {
								throw new RuntimeException("Unexpected old SOCAT version of '" + oldVersion + "'");
							}
							if ( newVers > oldVers ) {
								status = "U";
							}
							else {
								status = "N";
							}
						}
						socatVersionStatus = socatVersion + status;
					}
					// Generate the NetCDF DSG file, enhanced by Ferret, for this 
					// possibly modified and WOCEd cruise data
					logger.debug("Generating the full-data DSG file for " + expocode);
					dsgNcHandler.saveCruise(omeMData, cruiseData, socatVersionStatus, flag.toString());

					// Generate the decimated-data DSG file from the full-data DSG file
					logger.debug("Generating the decimated-data DSG file for " + expocode);
					dsgNcHandler.decimateCruise(expocode);
				} catch (Exception ex) {
					errorMsgs.add(expocode + ": unacceptable; " + ex.getMessage());
					continue;
				}

				// Update cruise info with status values from cruiseData
				cruise.setQcStatus(qcStatus);
				cruise.setVersion(socatVersion);
				cruise.setDataCheckStatus(cruiseData.getDataCheckStatus());
				cruise.setNumErrorRows(cruiseData.getNumErrorRows());
				cruise.setNumWarnRows(cruiseData.getNumWarnRows());

				// Create the QCEvent for submitting the initial QC flags
				QCEvent initQC = new QCEvent();
				initQC.setExpocode(expocode);
				initQC.setVersion(socatVersion);
				initQC.setFlagDate(new Date());
				initQC.setUsername(DashboardUtils.SANITY_CHECKER_USERNAME);
				initQC.setRealname(DashboardUtils.SANITY_CHECKER_REALNAME);
				// Add the initial QC flag in each region only for new and updated cruises
				initQC.setFlag(flag);
				if ( DashboardUtils.QC_NEW_FLAG.equals(flag) )
					initQC.setComment("Initial QC flag for new dataset");
				else
					initQC.setComment("Initial QC flag for updated dataset");

				// Get the regions in which the cruise reports 
				TreeSet<Character> regionsSet;
				try {
					regionsSet = dsgNcHandler.getDataRegionsSet(expocode);
				} catch (Exception ex) {
					throw new IllegalArgumentException("Unable to read region IDs " +
							"from the newly created full-data DSG file for " + 
							expocode + ": " + ex.getMessage());
				}
				// Add the global and regional initial flags
				try {
					initQC.setRegionID(DashboardUtils.GLOBAL_REGION_ID);
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
				initQC.setRegionID(DashboardUtils.GLOBAL_REGION_ID);
				initQC.setFlag(DashboardUtils.QC_COMMENT);
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
				ArrayList<WoceEvent> initWoceList;
				try {
					initWoceList = msgHandler.generateWoceEvents(cruiseData, 
							dsgNcHandler, knownDataFileTypes);
				} catch (IOException ex) {
					throw new IllegalArgumentException(ex);
				}
				try {
					databaseHandler.resetWoceEvents(expocode);
					for ( WoceEvent woceEvent : initWoceList ) {
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

			if ( archiveStatus.equals(DashboardUtils.ARCHIVE_STATUS_SENT_CDIAC) && 
				 ( repeatSend || cruise.getCdiacDate().isEmpty() ) ) {
				// Queue the request to send (or re-send) the original cruise data and metadata to CDIAC
				cdiacExpos.add(expocode);
			}
			else if ( ! archiveStatus.equals(cruise.getArchiveStatus()) ) {
				// Update the archive status now
				cruise.setArchiveStatus(archiveStatus);
				changed = true;
				commitMsg += " archive status '" + archiveStatus + "'"; 
			}

			if ( changed ) {
				// Commit this update of the cruise properties
				commitMsg += " by user '" + submitter + "'";
				cruiseHandler.saveCruiseInfoToFile(cruise, commitMsg);
			}
			try {
				// Wait just a moment to let other things (mysql? svn?) catch up 
				// or clear;  submits of lots of datasets can sometimes cause 
				// messed-up DSG files not seen when submitted in small numbers.
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				// Ignore
				;
			}
		}

		// notify ERDDAP of new/updated cruises
		if ( ! ingestExpos.isEmpty() )
			dsgNcHandler.flagErddap(true, true);

		// Send original cruise data to CDIAC where user requested immediate archival
		if ( ! cdiacExpos.isEmpty() ) {
			String userRealName;
			try {
				userRealName = databaseHandler.getReviewerRealname(submitter);
			} catch (Exception ex) {
				userRealName = null;
			}
			if ( (userRealName == null) || userRealName.isEmpty() )
				throw new IllegalArgumentException("Unknown real name for user " + submitter);

			String userEmail;
			try {
				userEmail = databaseHandler.getReviewerEmail(submitter);
			} catch (Exception ex) {
				userEmail = null;
			}
			if ( (userEmail == null) || userEmail.isEmpty() )
				throw new IllegalArgumentException("Unknown e-mail address for user " + submitter);

			for ( String expocode : cdiacExpos ) {
				String commitMsg = "Immediate archival of dataset " + expocode + 
						" requested by " + userRealName + " (" + userEmail + ") at " + localTimestamp;
				try {
					cdiacBundler.sendOrigFilesBundle(expocode, commitMsg, userRealName, userEmail);
				} catch (Exception ex) {
					errorMsgs.add("Failed to submit request for immediate archival of " + 
							expocode + ": " + ex.getMessage());
					continue;
				}
				// When successful, update the "sent to CDIAC" timestamp
				DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
				cruise.setArchiveStatus(archiveStatus);
				cruise.setCdiacDate(localTimestamp);
				cruiseHandler.saveCruiseInfoToFile(cruise, commitMsg);
			}
		}

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
