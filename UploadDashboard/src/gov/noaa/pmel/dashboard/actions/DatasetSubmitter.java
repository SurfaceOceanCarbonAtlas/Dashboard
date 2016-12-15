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
import gov.noaa.pmel.dashboard.shared.QCFlag;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;
import gov.noaa.pmel.dashboard.shared.DataQCEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Submits a dataset.  At this time this just means creating the 
 * DSG and decimated DSG files for the dataset.
 * 
 * @author Karl Smith
 */
public class DatasetSubmitter {

	DataFileHandler dataHandler;
	CheckerMessageHandler msgHandler;
	MetadataFileHandler metadataHandler;
	DatasetChecker datasetChecker;
	DsgNcFileHandler dsgHandler;
	KnownDataTypes knownDataFileTypes;
	DatabaseRequestHandler databaseHandler;
	ArchiveFilesBundler filesBundler;
	String version;
	Logger logger;

	/**
	 * @param configStore
	 * 		create with the file handlers and data checker in this data store.
	 */
	public DatasetSubmitter(DashboardConfigStore configStore) {
		dataHandler = configStore.getDataFileHandler();
		msgHandler = configStore.getCheckerMsgHandler();
		metadataHandler = configStore.getMetadataFileHandler();
		datasetChecker = configStore.getDashboardDatasetChecker();
		dsgHandler = configStore.getDsgNcFileHandler();
		knownDataFileTypes = configStore.getKnownDataFileTypes();
		databaseHandler = configStore.getDatabaseRequestHandler();
		filesBundler = configStore.getArchiveFilesBundler();
		version = configStore.getUploadVersion();
		logger = Logger.getLogger(getClass());
	}

	/**
	 * Submit a dataset.  This standardized the data using the automated data checker 
	 * and generates DSG and decimated DSG files for datasets which are editable 
	 * (have a QC status of {@link DashboardUtils#QC_STATUS_NOT_SUBMITTED}, 
	 * {@link DashboardUtils#QC_STATUS_UNACCEPTABLE},
	 * {@link DashboardUtils#QC_STATUS_SUSPENDED}, or
	 * {@link DashboardUtils#QC_STATUS_EXCLUDED}.
	 * For all datasets, the archive status is updated to the given value.
	 * 
	 * If the archive status is {@link DashboardUtils#ARCHIVE_STATUS_SENT_FOR_ARHCIVAL},
	 * the archive request is sent for dataset which have not already been sent,
	 * or for all datasets if repeatSend is true.
	 * 
	 * @param idsSet
	 * 		IDs of the datasets to submit
	 * @param archiveStatus
	 * 		archive status to set for these cruises
	 * @param timestamp
	 * 		local timestamp to associate with this submission
	 * @param repeatSend
	 * 		re-send request to archive for datasets which already had a request sent?
	 * @param submitter
	 * 		user performing this submit 
	 * @throws IllegalArgumentException
	 * 		if the dataset ID is invalid,
	 * 		if the data or metadata is missing,
	 * 		if the DSG files cannot be created, or
	 * 		if there was a problem saving the updated dataset information (including archive status)
	 */
	public void submitDatasets(Collection<String> idsSet, String archiveStatus, String timestamp, 
			boolean repeatSend, String submitter) throws IllegalArgumentException {

		HashSet<String> ingestIds = new HashSet<String>();
		HashSet<String> archiveIds = new HashSet<String>();
		ArrayList<String> errorMsgs = new ArrayList<String>();
		for ( String datasetId : idsSet ) {
			// Get the properties of this dataset
			DashboardDataset dataset = dataHandler.getDatasetFromInfoFile(datasetId);
			if ( dataset == null ) 
				throw new IllegalArgumentException("Unknown dataset " + datasetId);

			boolean changed = false;
			String commitMsg = "Dataset " + datasetId;

			if ( Boolean.TRUE.equals(dataset.isEditable()) ) {
				// Get the complete original cruise data
				DashboardDatasetData datasetData = dataHandler.getDatasetDataFromFiles(datasetId, 0, -1);

				/*
				 *  Convert the cruise data into standard units.  Adds and assigns 
				 *  year, month, day, hour, minute, and seconds columns if not present.
				 */
				if ( ! datasetChecker.standardizeDatasetData(datasetData) ) {
					if ( datasetData.getNumDataRows() < 1 )
						errorMsgs.add(datasetId + ": unacceptable; no valid data points");
					else if (  ! datasetChecker.checkProcessedOkay() )
						errorMsgs.add(datasetId + ": unacceptable; automated checking of data failed");
					else if ( datasetChecker.hadGeopositionErrors() )
						errorMsgs.add(datasetId + ": unacceptable; automated checking of data " +
								"detected longitude, latitude, sample depth, or date/time value errors");
					else
						errorMsgs.add(datasetId + ": unacceptable for unknown reason - unexpected");
					continue;
				}

				// Make sure a column exists for the automated data checker WOCE flags
				int numRows = datasetData.getNumDataRows();
				ArrayList<String> colNames = datasetData.getUserColNames();
				ArrayList<DataColumnType> cruiseTypes = datasetData.getDataColTypes();
				ArrayList<ArrayList<String>> dataVals = datasetData.getDataValues();
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

				// Add automated data checker flags
				for ( QCFlag wtype : dataset.getCheckerFlags() ) {
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
					DashboardMetadata omeInfo = metadataHandler.getMetadataInfo(datasetId, DashboardUtils.OME_FILENAME);
					if ( ! version.equals(omeInfo.getVersion()) ) {
						metadataHandler.saveMetadataInfo(omeInfo, "Update metadata version number to " + 
								version + " with submission of " + datasetId, false);
					}
					DashboardOmeMetadata omeMData = new DashboardOmeMetadata(omeInfo, metadataHandler);

					// Generate the NetCDF DSG file, enhanced by Ferret, for this 
					// possibly modified and WOCEd cruise data
					logger.debug("Generating the full-data DSG file for " + datasetId);
					dsgHandler.saveDataset(omeMData, datasetData, version);

					// Generate the decimated-data DSG file from the full-data DSG file
					logger.debug("Generating the decimated-data DSG file for " + datasetId);
					dsgHandler.decimateCruise(datasetId);
				} catch (Exception ex) {
					errorMsgs.add(datasetId + ": unacceptable; " + ex.getMessage());
					continue;
				}

				// Update cruise info with status values from datasetData
				dataset.setSubmitStatus(DashboardUtils.STATUS_SUBMITTED);
				dataset.setVersion(version);
				dataset.setDataCheckStatus(datasetData.getDataCheckStatus());
				dataset.setNumErrorRows(datasetData.getNumErrorRows());
				dataset.setNumWarnRows(datasetData.getNumWarnRows());

				// Set up to save changes to version control
				changed = true;
				commitMsg += " submitted";
				ingestIds.add(datasetId);
			}

			if ( archiveStatus.equals(DashboardUtils.ARCHIVE_STATUS_SENT_FOR_ARHCIVAL) && 
				 ( repeatSend || dataset.getArchiveDate().isEmpty() ) ) {
				// Queue the request to send (or re-send) the original cruise data and metadata to CDIAC
				archiveIds.add(datasetId);
			}
			else if ( ! archiveStatus.equals(dataset.getArchiveStatus()) ) {
				// Update the archive status now
				dataset.setArchiveStatus(archiveStatus);
				changed = true;
				commitMsg += " archive status '" + archiveStatus + "'"; 
			}

			if ( changed ) {
				// Commit this update of the cruise properties
				commitMsg += " by user '" + submitter + "'";
				dataHandler.saveDatasetInfoToFile(dataset, commitMsg);
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
		if ( ! ingestIds.isEmpty() )
			dsgHandler.flagErddap(true, true);

		// Send original cruise data to CDIAC where user requested immediate archival
		if ( ! archiveIds.isEmpty() ) {
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

			for ( String expocode : archiveIds ) {
				String commitMsg = "Immediate archival of dataset " + expocode + " requested by " + 
						userRealName + " (" + userEmail + ") at " + timestamp;
				try {
					filesBundler.sendOrigFilesBundle(expocode, commitMsg, userRealName, userEmail);
				} catch (Exception ex) {
					errorMsgs.add("Failed to submit request for immediate archival of " + 
							expocode + ": " + ex.getMessage());
					continue;
				}
				// When successful, update the "sent to CDIAC" timestamp
				DashboardDataset cruise = dataHandler.getDatasetFromInfoFile(expocode);
				cruise.setArchiveStatus(archiveStatus);
				cruise.setArchiveDate(timestamp);
				dataHandler.saveDatasetInfoToFile(cruise, commitMsg);
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
