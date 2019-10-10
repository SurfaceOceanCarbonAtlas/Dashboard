package gov.noaa.pmel.dashboard.actions;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.datatype.SocatTypes;
import gov.noaa.pmel.dashboard.dsg.DsgMetadata;
import gov.noaa.pmel.dashboard.dsg.StdUserDataArray;
import gov.noaa.pmel.dashboard.handlers.ArchiveFilesBundler;
import gov.noaa.pmel.dashboard.handlers.CheckerMessageHandler;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.qc.DataLocation;
import gov.noaa.pmel.dashboard.qc.DataQCEvent;
import gov.noaa.pmel.dashboard.qc.QCEvent;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.CommentedDataQCFlag;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataQCFlag;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/**
 * Submits a dataset.  At this time this just means creating the DSG and decimated DSG files for the dataset.
 *
 * @author Karl Smith
 */
public class DatasetSubmitter {

    public static final String PI_PROVIDED_WOCE_COMMENT_START = "PI provided WOCE ";
    public static final String PI_PROVIDED_BOTTLEQC_COMMENT_START = "PI provided data QC ";

    DataFileHandler dataHandler;
    MetadataFileHandler metadataHandler;
    DatasetChecker datasetChecker;
    CheckerMessageHandler messageHandler;
    DsgNcFileHandler dsgHandler;
    DatabaseRequestHandler databaseHandler;
    ArchiveFilesBundler filesBundler;
    KnownDataTypes fileMetadataTypes;
    KnownDataTypes fileDataTypes;
    String version;
    Logger itsLogger;

    /**
     * @param configStore
     *         create with the file handlers and data checker in this data store.
     */
    public DatasetSubmitter(DashboardConfigStore configStore) {
        dataHandler = configStore.getDataFileHandler();
        metadataHandler = configStore.getMetadataFileHandler();
        datasetChecker = configStore.getDashboardDatasetChecker();
        messageHandler = configStore.getCheckerMsgHandler();
        dsgHandler = configStore.getDsgNcFileHandler();
        databaseHandler = configStore.getDatabaseRequestHandler();
        filesBundler = configStore.getArchiveFilesBundler();
        fileMetadataTypes = configStore.getKnownMetadataTypes();
        fileDataTypes = configStore.getKnownDataFileTypes();
        version = configStore.getUploadVersion();
        itsLogger = configStore.getLogger();
    }

    /**
     * Submit a dataset.  This standardized the data using the automated data checker and generates DSG and decimated
     * DSG files for datasets which are editable. For all datasets, the archive status is updated to the given value.
     * <p>
     * If the archive status begins with {@link DashboardUtils#ARCHIVE_STATUS_SENT_TO_START}, the archive request
     * is sent for dataset which have not already been sent, or for all datasets if repeatSend is true.
     *
     * @param idsSet
     *         IDs of the datasets to submit
     * @param archiveStatus
     *         archive status to set for these cruises
     * @param timestamp
     *         local timestamp to associate with this submission
     * @param repeatSend
     *         re-send request to archive for datasets which already had a request sent?
     * @param submitter
     *         user performing this submit
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, if the data or metadata is missing, if the DSG files cannot be created, or
     *         if there was a problem saving the updated dataset information (including archive status)
     */
    public void submitDatasets(Collection<String> idsSet, String archiveStatus, String timestamp,
            boolean repeatSend, String submitter) throws IllegalArgumentException {

        HashSet<String> ingestIds = new HashSet<String>();
        HashSet<String> archiveIds = new HashSet<String>();
        ArrayList<String> errorMsgs = new ArrayList<String>();
        for (String datasetId : idsSet) {
            // Get the dataset with data since almost always submitting for QC
            DashboardDatasetData dataset = dataHandler.getDatasetDataFromFiles(datasetId, 0, -1);
            if ( dataset == null )
                throw new IllegalArgumentException("Unknown dataset " + datasetId);

            boolean changed = false;
            String commitMsg = "Dataset " + datasetId;

            if ( Boolean.TRUE.equals(dataset.isEditable()) ) {
                try {
                    // Get the OME metadata for this dataset
                    DashboardMetadata omeInfo = metadataHandler.getMetadataInfo(datasetId, DashboardUtils.OME_FILENAME);
                    if ( !version.equals(omeInfo.getVersion()) ) {
                        omeInfo.setVersion(version);
                        metadataHandler.saveMetadataInfo(omeInfo, "Update metadata version number to " +
                                version + " with submission of " + datasetId, false);
                    }
                    DashboardOmeMetadata omeMData = metadataHandler.getOmeFromFile(omeInfo);
                    // get the DOIs from data file properties; CDIAC OME does save the DOI as such (may be part of citation)
                    omeMData.setDatasetDOI(dataset.getSourceDOI());
                    DsgMetadata dsgMData = omeMData.createDsgMetadata(fileMetadataTypes);

                    // For SOCAT, the version string in the DsgMetadata is the submit version number plus an 'N' or 'U'
                    // depending on whether this dataset is new to this version of SOCAT or an update from a previous
                    // version of SOCAT.  An update within the same version of SOCAT does not change 'N' to 'U'.
                    String versionStatus = databaseHandler.getVersionStatus(datasetId);
                    String datasetQCFlag;
                    if ( versionStatus.isEmpty() ) {
                        versionStatus = version + "N";
                        datasetQCFlag = "N";
                    }
                    else if ( "U".equals(versionStatus.substring(versionStatus.length() - 1)) ) {
                        versionStatus = version + "U";
                        datasetQCFlag = "U";
                    }
                    else {
                        long newVersion;
                        try {
                            newVersion = Math.round(Double.parseDouble(version) * 10.0);
                        } catch ( NumberFormatException ex ) {
                            throw new RuntimeException("Unexpected non-numeric new version number '" + version + "'");
                        }
                        String oldNum = versionStatus.substring(0, versionStatus.length() - 1);
                        long oldVersion;
                        try {
                            oldVersion = Math.round(Double.parseDouble(oldNum) * 10.0);
                        } catch ( NumberFormatException ex ) {
                            throw new RuntimeException("Unexpected non-numeric old version number '" + oldNum + "'");
                        }
                        if ( newVersion > oldVersion ) {
                            versionStatus = version + "U";
                            datasetQCFlag = "U";
                        }
                        else {
                            versionStatus = version + "N";
                            datasetQCFlag = "N";
                        }
                    }
                    dsgMData.setVersion(versionStatus);
                    dsgMData.setDatasetQCFlag(datasetQCFlag);

                    // Standardize the data and perform the automated data checks.
                    // Saves the messages from the standardization and automated data checks.
                    // Assigns dataCheckStatus, numErrorRows, numWarnRows, checkerFlags, and userFlags in dataset
                    StdUserDataArray userStdData = datasetChecker.standardizeDataset(dataset, dsgMData);
                    if ( DashboardUtils.CHECK_STATUS_UNACCEPTABLE.equals(dataset.getDataCheckStatus()) ) {
                        errorMsgs.add(datasetId + ": unacceptable; check data check error messages " +
                                "(missing lon/lat/time or uninterpretable values)");
                        continue;
                    }

                    // Add the automated data checker data QC flags to the appropriate data QC columns
                    userStdData.addAutomatedDataQC();

                    // Generate the NetCDF DSG file, enhanced by Ferret
                    if ( itsLogger != null )
                        itsLogger.debug("Generating the full-data DSG file for " + datasetId);
                    dsgHandler.saveDatasetDsg(dsgMData, userStdData);

                    // Generate the decimated-data DSG file from the full-data DSG file
                    if ( itsLogger != null )
                        itsLogger.debug("Generating the decimated-data DSG file for " + datasetId);
                    dsgHandler.decimateDatasetDsg(datasetId);

                    // Update the all_region_ids metadata variable from the Ferret-generated
                    // region_id data variable in the full-data DSG file.
                    String allRegionIds = dsgHandler.updateAllRegionIds(datasetId);

                    // Add new or update (regardless of version) dataset QC flags to the database
                    // Uses the submitStatus from dataset to determine if new or updated
                    ArrayList<QCEvent> datasetQCEvents = generateDatasetQCEvents(dataset, allRegionIds);
                    databaseHandler.addDatasetQCEvents(datasetQCEvents);

                    // Generate the set of data QC events for the data QC flags from standardization
                    // and automated data checking as well as for user-provided data QC flags
                    ArrayList<DataQCEvent> dataQCEvents = generateDataQCEvents(dataset, userStdData);

                    // Update the data QC flags to those for this data
                    databaseHandler.resetDataQCEvents(datasetId);
                    databaseHandler.addDataQCEvent(dataQCEvents);

                    // (re)generate the WOCE flags messages file
                    metadataHandler.generateWoceFlagMsgsFile(datasetId, databaseHandler);
                } catch ( Exception ex ) {
                    errorMsgs.add(datasetId + ": unacceptable; " + ex.getMessage());
                    continue;
                }

                // Now mark the dataset as submitted in this version
                if ( dataset.getSubmitStatus().isPrivate() )
                    dataset.setSubmitStatus(new DatasetQCStatus(DatasetQCStatus.Status.NEW_AWAITING_QC, ""));
                else
                    dataset.setSubmitStatus(new DatasetQCStatus(DatasetQCStatus.Status.UPDATED_AWAITING_QC, ""));
                dataset.setVersion(version);

                // Set up to save changes to version control
                changed = true;
                commitMsg += " submitted";
                ingestIds.add(datasetId);
            }

            if ( archiveStatus.startsWith(DashboardUtils.ARCHIVE_STATUS_SENT_TO_START) &&
                    (repeatSend || dataset.getArchiveTimestamps().isEmpty()) ) {
                // Queue the request to send (or re-send) the data and metadata for archival.
                // In the future there might be more than one place to send for archival.
                archiveIds.add(datasetId);
            }
            else if ( !archiveStatus.equals(dataset.getArchiveStatus()) ) {
                // Update the archive status now
                dataset.setArchiveStatus(archiveStatus);
                changed = true;
                commitMsg += " archive status '" + archiveStatus + "'";
            }

            if ( changed ) {
                // Commit this update of the dataset properties
                commitMsg += " by user '" + submitter + "'";
                dataHandler.saveDatasetInfoToFile(dataset, commitMsg);
            }
            try {
                // Wait just a moment to let other things (mysql? svn?) catch up
                // or clear;  submits of lots of datasets can sometimes cause
                // messed-up DSG files not seen when submitted in small numbers.
                Thread.sleep(100);
            } catch ( InterruptedException ex ) {
                // Ignore
                ;
            }
        }

        // notify ERDDAP of new/updated dataset
        if ( !ingestIds.isEmpty() )
            dsgHandler.flagErddap(true, true);

        // Send dataset data and metadata for archival where user requested immediate archival
        if ( !archiveIds.isEmpty() ) {
            String userRealName;
            try {
                userRealName = databaseHandler.getReviewerRealname(submitter);
            } catch ( Exception ex ) {
                userRealName = null;
            }
            if ( (userRealName == null) || userRealName.isEmpty() )
                throw new IllegalArgumentException("Unknown real name for user " + submitter);

            String userEmail;
            try {
                userEmail = databaseHandler.getReviewerEmail(submitter);
            } catch ( Exception ex ) {
                userEmail = null;
            }
            if ( (userEmail == null) || userEmail.isEmpty() )
                throw new IllegalArgumentException("Unknown e-mail address for user " + submitter);

            for (String datasetId : archiveIds) {
                String commitMsg = "Immediate archival of dataset " + datasetId + " requested by " +
                        userRealName + " (" + userEmail + ") at " + timestamp;
                try {
                    filesBundler.sendOrigFilesBundle(datasetId, commitMsg, userRealName, userEmail);
                } catch ( Exception ex ) {
                    errorMsgs.add("Failed to submit request for immediate archival of " +
                            datasetId + ": " + ex.getMessage());
                    continue;
                }
                // When successful, update the archived timestamp
                DashboardDataset cruise = dataHandler.getDatasetFromInfoFile(datasetId);
                cruise.setArchiveStatus(archiveStatus);
                cruise.getArchiveTimestamps().add(timestamp);
                dataHandler.saveDatasetInfoToFile(cruise, commitMsg);
            }
        }

        // If any dataset submit had errors, return the error messages
        // TODO: do this in a return message, not an IllegalArgumentException
        if ( errorMsgs.size() > 0 ) {
            StringBuilder sb = new StringBuilder();
            for (String msg : errorMsgs) {
                sb.append(msg);
                sb.append("\n");
            }
            throw new IllegalArgumentException(sb.toString());
        }
    }

    /**
     * Generate a list of dataset QC events associated with submitting this dataset for QC.
     * Uses the submitStatus of dataset to determine if this is a new or updated dataset,
     * regardless of version.  Creates an initial dataset QC flag for the global region and
     * then each of the regions with IDs given in allRegionIds.  Finally adds a dataset QC
     * comment remarking on the number of data rows with errors and warnings obtained from
     * numErrors and numWarning of dataset.
     *
     * @param dataset
     *         generate dataset QC events for this dataset
     * @param allRegionIds
     *         string of concatenated IDs of regions this dataset occupies;
     *         this assumes region IDs are Strings of length one
     *
     * @return list of data QC events associated with submitting this dataset for QC
     */
    private ArrayList<QCEvent> generateDatasetQCEvents(DashboardDatasetData dataset, String allRegionIds) {
        ArrayList<QCEvent> qclist = new ArrayList<QCEvent>(allRegionIds.length() + 2);
        String expocode = dataset.getDatasetId();

        // Start with the initial new/updated dataset QC flags
        DatasetQCStatus flag = dataset.getSubmitStatus();
        String comment;
        if ( flag.isPrivate() ) {
            comment = "Initial QC flag for new dataset";
            flag.setActual(DatasetQCStatus.Status.NEW_AWAITING_QC);
        }
        else {
            comment = "Initial QC flag for updated dataset";
            flag.setActual(DatasetQCStatus.Status.UPDATED_AWAITING_QC);
        }
        String fullComment = comment;
        for (String piece : flag.getComments()) {
            fullComment += "; " + piece;
        }
        flag.addComment(comment);
        Date now = new Date();
        String flagString = flag.flagString();

        // First add a global flag with all the comments (presumably any auto-QC comments)
        {
            QCEvent initQC = new QCEvent();
            initQC.setDatasetId(expocode);
            initQC.setVersion(version);
            initQC.setUsername(DashboardServerUtils.AUTOMATED_DATA_CHECKER_USERNAME);
            initQC.setRealname(DashboardServerUtils.AUTOMATED_DATA_CHECKER_REALNAME);
            initQC.setFlagDate(now);
            initQC.setFlagValue(flagString);
            initQC.setRegionId(DashboardUtils.REGION_ID_GLOBAL);
            initQC.setComment(fullComment);
            qclist.add(initQC);
        }

        // Then add flags for each region with just the initial QC flag comment
        for (char regionId : allRegionIds.toCharArray()) {
            QCEvent initQC = new QCEvent();
            initQC.setDatasetId(expocode);
            initQC.setVersion(version);
            initQC.setUsername(DashboardServerUtils.AUTOMATED_DATA_CHECKER_USERNAME);
            initQC.setRealname(DashboardServerUtils.AUTOMATED_DATA_CHECKER_REALNAME);
            initQC.setFlagDate(now);
            initQC.setFlagValue(flagString);
            initQC.setRegionId(String.valueOf(regionId));
            initQC.setComment(comment);
            qclist.add(initQC);
        }

        // Add a comment on the number of data rows with errors and warnings
        QCEvent initQC = new QCEvent();
        initQC.setDatasetId(expocode);
        initQC.setVersion(version);
        initQC.setUsername(DashboardServerUtils.AUTOMATED_DATA_CHECKER_USERNAME);
        initQC.setRealname(DashboardServerUtils.AUTOMATED_DATA_CHECKER_REALNAME);
        initQC.setFlagDate(now);
        initQC.setFlagValue(DatasetQCStatus.FLAG_COMMENT);
        initQC.setRegionId(DashboardUtils.REGION_ID_GLOBAL);
        initQC.setComment("Automated data check found " +
                Integer.toString(dataset.getNumErrorRows()) + " data points with errors and " +
                Integer.toString(dataset.getNumWarnRows()) + " data points with warnings.");
        qclist.add(initQC);

        return qclist;
    }

    /**
     * Generates a list of DataQCEvent objects from the saved automated data checker messages
     * as well as the PI-provided WOCE flags.  The dataset ID, version, column types, and
     * PI-provided data QC flags and associated comments are obtained from the given DatasetData.
     * The automated data checker flags and messages are read from the saved messages files for
     * the dataset.  Values for the DataLocation objects are read from the saved full-data DSG file.
     * Warning messages from the automated data checker are ignored at this time.
     *
     * @param dataset
     *         generate DataQCEvents for this dataset.
     *
     * @return the list of DataQCEvents for the dataset; never null but may be empty
     *
     * @throws IllegalArgumentException
     *         if either argument is null or invalid
     * @throws FileNotFoundException
     *         if the full-data DSG file for this dataset is not found
     * @throws IOException
     *         if reading from the full-data DSG file for this dataset throws one
     */
    public ArrayList<DataQCEvent> generateDataQCEvents(DashboardDatasetData dataset, StdUserDataArray stdUserData)
            throws IllegalArgumentException, FileNotFoundException, IOException {
        if ( dataset == null )
            throw new IllegalArgumentException("dataset is null");
        if ( stdUserData == null )
            throw new IllegalArgumentException("stdUserData is null");

        String expocode = dataset.getDatasetId();
        List<DashDataType<?>> columnTypes = stdUserData.getDataTypes();

        // Add the automated-data-checker data QC flags from the saved messages
        TreeSet<CommentedDataQCFlag> dataqc = new TreeSet<CommentedDataQCFlag>();
        for (ADCMessage msg : stdUserData.getStandardizationMessages()) {

            // Data QC always has a positive row number.  Dataset QC as well as general and summaries
            // QC messages have a negative row number (DashboardUtils.INT_MISSING_VALUE)
            int rowNum = msg.getRowNumber();
            if ( rowNum <= 0 )
                continue;

            // TODO: in the general case, get the correct data QC flag variable name and value
            // For SOCAT, all the automated data checker flags are put under WOCE_CO2_water
            String flagName = SocatTypes.WOCE_CO2_WATER.getVarName();

            String flagValue;
            DataQCFlag.Severity severity = msg.getSeverity();
            switch ( severity ) {
                case UNASSIGNED:
                case ACCEPTABLE:
                    flagValue = "";
                    break;
                case WARNING:
                    // flagValue = DashboardServerUtils.WOCE_QUESTIONABLE;
                    // Ignore automated data checker warnings as the are just pointing out
                    // potential issues which may not be a problem or have any consequence
                    flagValue = "";
                    break;
                case ERROR:
                case CRITICAL:
                    flagValue = DashboardServerUtils.WOCE_BAD;
                    break;
                default:
                    throw new IllegalArgumentException("unexpected messages severity of " + severity);
            }
            if ( flagValue.isEmpty() )
                continue;

            String comment = msg.getGeneralComment();
            if ( comment.isEmpty() )
                comment = msg.getDetailedComment();

            int colNum = msg.getColNumber();
            Integer colIdx = (colNum > 0) ? (colNum - 1) : null;
            CommentedDataQCFlag info = new CommentedDataQCFlag(flagName, flagValue, severity, colIdx, rowNum - 1,
                    comment);
            dataqc.add(info);
        }

        // Add the PI-provided data QC flags from values in the dataset
        TreeSet<DataQCFlag> userFlags = dataset.getUserFlags();
        if ( userFlags.size() > 0 ) {
            // Map any QC comment columns to their QC flag names
            // Note that the column index in the DataQCFlag should be for the data value being QC'ed
            HashMap<String,Integer> flagCommentIndex = new HashMap<String,Integer>();
            for (int qcIdx = 0; qcIdx < columnTypes.size(); qcIdx++) {
                DashDataType<?> colType = columnTypes.get(qcIdx);
                if ( colType.isQCType() ) {
                    for (int commIdx = 0; commIdx < columnTypes.size(); commIdx++) {
                        if ( columnTypes.get(commIdx).isQCTypeFor(colType) ) {
                            flagCommentIndex.put(colType.getVarName(), commIdx);
                            break;
                        }
                    }
                }
            }

            // PI-provided comments are in the datavals stored in dataset
            ArrayList<ArrayList<String>> dataVals = dataset.getDataValues();
            for (DataQCFlag uflag : userFlags) {
                String comment;
                if ( uflag.getFlagName().toUpperCase().contains("WOCE)") )
                    comment = PI_PROVIDED_WOCE_COMMENT_START + uflag.getFlagValue() + " flag";
                else
                    comment = PI_PROVIDED_BOTTLEQC_COMMENT_START + uflag.getFlagValue() + " flag";
                Integer idx = flagCommentIndex.get(uflag.getFlagName());
                if ( idx != null ) {
                    comment += " with comment/subflag: " + dataVals.get(uflag.getRowIndex()).get(idx);
                }
                dataqc.add(new CommentedDataQCFlag(uflag, comment));
            }
        }

        ArrayList<DataQCEvent> woceList = new ArrayList<DataQCEvent>();
        // If no WOCE flags, return now before we read data from the DSG file
        if ( dataqc.isEmpty() )
            return woceList;

        // Get the longitudes, latitude, and times for the list of DataLocation objects
        double[][] lonlattimes = dsgHandler.readLonLatTimeDataValues(expocode);
        double[] longitudes = lonlattimes[0];
        double[] latitudes = lonlattimes[1];
        double[] times = lonlattimes[2];
        Date now = new Date();

        String lastFlagName = null;
        String lastFlagValue = null;
        Integer lastColIdx = null;
        String lastComment = null;
        double[] dataValues = null;
        String lastDataVarName = null;
        String dataVarName = null;
        ArrayList<DataLocation> locations = null;
        for (CommentedDataQCFlag info : dataqc) {

            // Check if a new DataQCEvent is needed
            String flagName = info.getFlagName();
            String flagValue = info.getFlagValue();
            Integer colIdx = info.getColumnIndex();
            String comment = info.getComment();
            if ( !(flagName.equals(lastFlagName) && flagValue.equals(lastFlagValue) &&
                    colIdx.equals(lastColIdx) && comment.equals(lastComment)) ) {
                lastFlagName = flagName;
                lastFlagValue = flagValue;
                lastColIdx = colIdx;
                lastComment = comment;

                DataQCEvent woceEvent = new DataQCEvent();
                woceEvent.setFlagName(flagName);
                woceEvent.setDatasetId(expocode);
                woceEvent.setVersion(version);
                woceEvent.setFlagValue(flagValue);
                woceEvent.setFlagDate(now);
                woceEvent.setUsername(DashboardServerUtils.AUTOMATED_DATA_CHECKER_USERNAME);
                woceEvent.setRealname(DashboardServerUtils.AUTOMATED_DATA_CHECKER_REALNAME);
                woceEvent.setComment(comment);

                // If a column can be identified, assign its name and
                // get its values if we do not already have them
                try {
                    DashDataType<?> dataType = columnTypes.get(colIdx);
                    dataVarName = dataType.getVarName();
                    // Check if this type is known in the data file types
                    if ( !fileDataTypes.containsTypeName(dataVarName) )
                        throw new IllegalArgumentException("unknown");
                    if ( !dataVarName.equals(lastDataVarName) ) {
                        dataValues = dsgHandler.readDoubleVarDataValues(expocode, dataVarName);
                        lastDataVarName = dataVarName;
                    }
                    woceEvent.setVarName(dataVarName);
                } catch ( Exception ex ) {
                    // Invalid data column index or unknown data column
                    dataVarName = null;
                    // leave varName unassigned in woceEvent; leave lastDataVarName and dataValues unchanged
                    // just in case the next woceEvent uses that data again
                }

                // Directly modify the locations ArrayList in this object
                locations = woceEvent.getLocations();
                woceList.add(woceEvent);
            }

            // Add a location for the current WOCE event
            DataLocation dataLoc = new DataLocation();
            int rowIdx = info.getRowIndex();
            dataLoc.setRowNumber(rowIdx + 1);
            dataLoc.setDataDate(new Date(Math.round(times[rowIdx] * 1000.0)));
            dataLoc.setLatitude(latitudes[rowIdx]);
            dataLoc.setLongitude(longitudes[rowIdx]);
            if ( dataVarName != null )
                dataLoc.setDataValue(dataValues[rowIdx]);
            locations.add(dataLoc);
        }

        return woceList;
    }

}
