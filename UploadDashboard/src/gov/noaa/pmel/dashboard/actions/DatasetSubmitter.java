/**
 * 
 */
package gov.noaa.pmel.dashboard.actions;

import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;
import gov.noaa.pmel.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.server.DashDataType;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCEvent;
import gov.noaa.pmel.dashboard.shared.DataQCEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Submits Dashboard cruise for SOCAT QC
 * 
 * @author Karl Smith
 */
public class DatasetSubmitter {

	DataFileHandler cruiseHandler;
	CheckerMessageHandler msgHandler;
	MetadataFileHandler metadataHandler;
	DatasetChecker cruiseChecker;
	DsgNcFileHandler dsgNcHandler;
	KnownDataTypes knownDataFileTypes;
	DatabaseRequestHandler databaseHandler;
	ArchiveFilesBundler cdiacBundler;
	String version;
	Logger logger;

	/**
	 * @param configStore
	 * 		create with the file handlers and data checker in this data store.
	 */
	public DatasetSubmitter(DashboardConfigStore configStore) {
		cruiseHandler = configStore.getDataFileHandler();
		msgHandler = configStore.getCheckerMsgHandler();
		metadataHandler = configStore.getMetadataFileHandler();
		cruiseChecker = configStore.getDashboardCruiseChecker();
		dsgNcHandler = configStore.getDsgNcFileHandler();
		knownDataFileTypes = configStore.getKnownDataFileTypes();
		databaseHandler = configStore.getDatabaseRequestHandler();
		cdiacBundler = configStore.getArchiveFilesBundler();
		version = configStore.getUploadVersion();
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
	 * If the archive status is {@link DashboardUtils#ARCHIVE_STATUS_SENT_FOR_ARHCIVAL},
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
	 * 		if the dataset is invalid,
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
			DashboardDataset cruise = cruiseHandler.getDatasetFromInfoFile(expocode);
			if ( cruise == null ) 
				throw new IllegalArgumentException("Unknown dataset " + expocode);

			boolean changed = false;
			String commitMsg = "Expocode " + expocode;

			if ( Boolean.TRUE.equals(cruise.isEditable()) ) {
				// QC flag to assign with this cruise
				Character flag;
				String qcStatus = cruise.getSubmitStatus();
				if ( DashboardUtils.QC_STATUS_NOT_SUBMITTED.equals(qcStatus) ) {
					flag = DashboardUtils.QC_NEW_FLAG;
					qcStatus = DashboardUtils.QC_STATUS_SUBMITTED;
				}
				else {
					flag = DashboardUtils.QC_UPDATED_FLAG;
					qcStatus = DashboardUtils.QC_STATUS_SUBMITTED;
				}

				// Get the complete original cruise data
				DashboardDatasetData cruiseData = 
						cruiseHandler.getDatasetDataFromFiles(expocode, 0, -1);

				/*
				 *  Convert the cruise data into standard units.  Adds and assigns 
				 *  year, month, day, hour, minute, and seconds columns if not present.
				 */
				if ( ! cruiseChecker.standardizeCruiseData(cruiseData) ) {
					if ( cruiseData.getNumDataRows() < 1 )
						errorMsgs.add(expocode + ": unacceptable; no valid data points");
					else if (  ! cruiseChecker.checkProcessedOkay() )
						errorMsgs.add(expocode + ": unacceptable; automated checking of data failed");
					else if ( cruiseChecker.hadGeopositionErrors() )
						errorMsgs.add(expocode + ": unacceptable; automated checking of data " +
								"detected longitude, latitude, date, or time value errors");
					else
						errorMsgs.add(expocode + ": unacceptable for unknown reason - unexpected");
					continue;
				}

				// Make sure a column exists for each known WOCE type
				int numRows = cruiseData.getNumDataRows();
				ArrayList<String> colNames = cruiseData.getUserColNames();
				ArrayList<DataColumnType> cruiseTypes = cruiseData.getDataColTypes();
				ArrayList<ArrayList<String>> dataVals = cruiseData.getDataValues();
				HashMap<String,Integer> woceTypeIndices = new HashMap<String,Integer>();
				for ( DashDataType dtype : knownDataFileTypes.getKnownTypesSet() ) {
					if ( dtype.isWoceType() ) {
						Integer woceIdx = -1;
						for (int k = 0; k < cruiseTypes.size(); k++) {
							if ( dtype.typeNameEquals(cruiseTypes.get(k)) ) {
								woceIdx = k;
								break;
							}
						}
						if ( woceIdx < 0 ) {
							woceIdx = cruiseTypes.size();
							// Add a column for this WOCE type
							// !! Directly modifying the lists in datasetData !!
							colNames.add(dtype.getDisplayName());
							cruiseTypes.add(dtype.duplicate());
							for (int k = 0; k < numRows; k++)
								dataVals.get(k).add(DashboardUtils.WOCE_NOT_CHECKED.toString());
						}
						woceTypeIndices.put(dtype.getVarName(), woceIdx);
					}
				}

				// Only add SanityChecker WOCE-4 flags; the SanityChecker marks all 
				// questionable data regardless of whether it is of consequence.
				// Fine if these WOCE-4 flags overwrite a PI-provided WOCE flag.
				for ( WoceType wtype : cruise.getCheckerWoceFours() ) {
					Integer colIdx = woceTypeIndices.get(wtype.getWoceName());
					if ( colIdx == null )
						throw new RuntimeException("Unexpected unknown WOCE name: " + wtype.getWoceName());
					Integer rowIdx = wtype.getRowIndex();
					if ( (rowIdx < 0) || (rowIdx >= numRows) )
						throw new RuntimeException("Unexpected WOCE row index: " + rowIdx.toString());
					dataVals.get(rowIdx).set(colIdx, DashboardUtils.WOCE_BAD.toString());
				}
				// PI-provided WOCE flags are already in the data (that is where they came from)

				try {
					// Get the OME metadata for this cruise
					DashboardMetadata omeInfo = metadataHandler.getMetadataInfo(expocode, DashboardUtils.OME_FILENAME);
					if ( ! version.equals(omeInfo.getVersion()) ) {
						metadataHandler.saveMetadataInfo(omeInfo, "Update metadata version number to " + 
								version + " with submit for QC of " + expocode, false);
					}
					DashboardOmeMetadata omeMData = new DashboardOmeMetadata(omeInfo, metadataHandler);

					String socatVersionStatus = databaseHandler.getVersionStatus(expocode);
					if ( socatVersionStatus.isEmpty() ) {
						// New dataset to the database
						socatVersionStatus = version + "N";
					}
					else {
						String status = socatVersionStatus.substring(socatVersionStatus.length() - 1);
						if ( ! "U".equals(status) ) {
							double newVers;
							try {
								newVers = Math.floor(Double.parseDouble(version) * 10.0) / 10.0;
							} catch (NumberFormatException ex) {
								throw new RuntimeException("Unexpected new version of '" + version + "'");
							}
							String oldVersion = socatVersionStatus.substring(0, socatVersionStatus.length() - 1);
							double oldVers;
							try {
								oldVers = Math.floor(Double.parseDouble(oldVersion) * 10.0) / 10.0;
							} catch (NumberFormatException ex) {
								throw new RuntimeException("Unexpected old version of '" + oldVersion + "'");
							}
							if ( newVers > oldVers ) {
								status = "U";
							}
							else {
								status = "N";
							}
						}
						socatVersionStatus = version + status;
					}
					// Generate the NetCDF DSG file, enhanced by Ferret, for this 
					// possibly modified and WOCEd cruise data
					logger.debug("Generating the full-data DSG file for " + expocode);
					dsgNcHandler.saveDataset(omeMData, cruiseData, socatVersionStatus, flag.toString());

					// Generate the decimated-data DSG file from the full-data DSG file
					logger.debug("Generating the decimated-data DSG file for " + expocode);
					dsgNcHandler.decimateCruise(expocode);
				} catch (Exception ex) {
					errorMsgs.add(expocode + ": unacceptable; " + ex.getMessage());
					continue;
				}

				// Update cruise info with status values from datasetData
				cruise.setSubmitStatus(qcStatus);
				cruise.setVersion(version);
				cruise.setDataCheckStatus(cruiseData.getDataCheckStatus());
				cruise.setNumErrorRows(cruiseData.getNumErrorRows());
				cruise.setNumWarnRows(cruiseData.getNumWarnRows());

				// Set up to save changes to version control
				changed = true;
				commitMsg += " submit with QC flag '" + flag + "'";
				ingestExpos.add(expocode);
			}

			if ( archiveStatus.equals(DashboardUtils.ARCHIVE_STATUS_SENT_FOR_ARHCIVAL) && 
				 ( repeatSend || cruise.getArchiveDate().isEmpty() ) ) {
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
				cruiseHandler.saveDatasetInfoToFile(cruise, commitMsg);
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
				String commitMsg = "Immediate archival of dataset " + expocode + " requested by " + 
						userRealName + " (" + userEmail + ") at " + localTimestamp;
				try {
					cdiacBundler.sendOrigFilesBundle(expocode, commitMsg, userRealName, userEmail);
				} catch (Exception ex) {
					errorMsgs.add("Failed to submit request for immediate archival of " + 
							expocode + ": " + ex.getMessage());
					continue;
				}
				// When successful, update the "sent to CDIAC" timestamp
				DashboardDataset cruise = cruiseHandler.getDatasetFromInfoFile(expocode);
				cruise.setArchiveStatus(archiveStatus);
				cruise.setArchiveDate(localTimestamp);
				cruiseHandler.saveDatasetInfoToFile(cruise, commitMsg);
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
