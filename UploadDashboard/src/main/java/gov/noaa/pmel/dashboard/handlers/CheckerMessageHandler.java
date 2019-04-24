/**
 *
 */
package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.dsg.StdUserDataArray;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.ADCMessageList;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCFlag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Processes automated data check flags and messages, as well as PI-provided QC flags, for a dataset.
 *
 * @author Karl Smith
 */
public class CheckerMessageHandler extends VersionedFileHandler {

    private static final String MSGS_FILENAME_EXTENSION = ".messages";
    private static final String MSG_KEY_VALUE_SEP = ":";
    private static final String MSG_SEVERITY_KEY = "MsgSeverity";
    private static final String MSG_ROW_NUMBER_KEY = "MsgRowNumber";
    private static final String MSG_COLUMN_NUMBER_KEY = "MsgColumnNumber";
    private static final String MSG_COLUMN_NAME_KEY = "MsgColumnName";
    private static final String MSG_GENERAL_MSG_KEY = "MsgGeneralMessage";
    private static final String MSG_DETAILED_MSG_KEY = "MsgDetailedMessage";
    private static final String MSG_SUMMARY_MSG_KEY = "MsgSummaryMessage";

    /**
     * Handler for automated data check flags and messages.
     *
     * @param filesDirName
     *         save messages under this directory
     * @param svnUsername
     *         username for SVN authentication
     * @param svnPassword
     *         password for SVN authentication
     *
     * @throws IllegalArgumentException
     *         if the specified directory does not exist, is not a directory or is not under version control
     */
    public CheckerMessageHandler(String filesDirName, String svnUsername, String svnPassword)
            throws IllegalArgumentException {
        super(filesDirName, svnUsername, svnPassword);
    }

    /**
     * @param datasetId
     *         ID of the dataset
     *
     * @return the messages file associated with the dataset
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid
     */
    private File messagesFile(String datasetId) throws IllegalArgumentException {
        // standardize the dataset ID
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        // Get the parent directory
        File parentDir = new File(filesDir, stdId.substring(0, 4));
        // Get the dataset messages file
        File msgsFile = new File(parentDir, stdId + MSGS_FILENAME_EXTENSION);
        return msgsFile;
    }

    /**
     * Appropriately renames a messages file, if one exists, for a change in dataset ID.
     *
     * @param oldId
     *         standardized old ID for the dataset
     * @param newId
     *         standardized new ID for the dataset
     *
     * @throws IllegalArgumentException
     *         if a messages file for the new ID already exists, or if unable to rename the messages file
     */
    public void renameMsgsFile(String oldId, String newId) throws IllegalArgumentException {
        File oldMsgsFile = messagesFile(oldId);
        if ( !oldMsgsFile.exists() )
            return;

        File newMsgsFile = messagesFile(newId);
        if ( newMsgsFile.exists() )
            throw new IllegalArgumentException("Messages file already exists for " + newId);

        File newParent = newMsgsFile.getParentFile();
        if ( !newParent.exists() )
            newParent.mkdirs();

        // Move the old dataset files to the new location and name
        String commitMsg = "Rename from " + oldId + " to " + newId;
        try {
            moveVersionedFile(oldMsgsFile, newMsgsFile, commitMsg);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems renaming the messages file from " +
                    oldId + " to " + newId + ": " + ex.getMessage());
        }
    }

    /**
     * Deletes the messages file, if it exists, associated with a dataset.
     *
     * @param datasetId
     *         delete the messages file for the dataset with this ID
     *
     * @return true if messages file exists and was deleted; false if the messages file does not exist.
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, or if the messages file exists but could not be deleted
     */
    public void deleteMsgsFile(String datasetId) throws IllegalArgumentException {
        File msgsFile = messagesFile(datasetId);
        if ( msgsFile.exists() ) {
            String commitMsg = "Deleted messages file for " + datasetId;
            try {
                deleteVersionedFile(msgsFile, commitMsg);
            } catch ( Exception ex ) {
                throw new IllegalArgumentException("Problems deleting the messages file for " +
                        datasetId + ": " + ex.getMessage());
            }
        }
    }

    /**
     * Save the list of automated data check messages given with a standardized user data array object.
     * Using these messages, assigns the set of automated data checker data QC flags in the dataset.
     * Also assigns the set of PI-provided data QC flags in the dataset.  This assumes PI-provided QC flags
     * indicating issues have flag values that are integers in the range [3,9] (e.g., WOCE or bottle QC flags).
     *
     * @param dataset
     *         save message for this dataset as well as the sets of data QC flags in this dataset
     * @param stdUserData
     *         standardized user data for this dataset containing automated data check messages
     *
     * @throws IllegalArgumentException
     *         if the dataset or standardized user data is invalid
     */
    public void processCheckerMessages(DashboardDataset dataset, StdUserDataArray stdUserData)
            throws IllegalArgumentException {
        int numSamples = stdUserData.getNumSamples();
        if ( numSamples <= 0 )
            throw new IllegalArgumentException("no standardized data");
        int numStdCols = stdUserData.getNumDataCols();
        if ( numStdCols <= 0 )
            throw new IllegalArgumentException("no standardized data columns");
        List<DashDataType<?>> stdDataTypes = stdUserData.getDataTypes();

        if ( dataset.getNumDataRows() != numSamples )
            throw new IllegalArgumentException("number of data rows recorded for this dataset (" +
                    dataset.getNumDataRows() + ") does not match the number of samples (rows) " +
                    "in the standardized data (" + numSamples + ")");
        int numUserCols = dataset.getDataColTypes().size();
        if ( numUserCols <= 0 )
            throw new IllegalArgumentException("no data columns in this dataset");
        if ( numUserCols > numStdCols )
            throw new IllegalArgumentException("more dataset columns than standardized user data columns");
        {
            int k = -1;
            for (DataColumnType dtype : dataset.getDataColTypes()) {
                k++;
                if ( !stdDataTypes.get(k).typeNameEquals(dtype) )
                    throw new IllegalArgumentException("types for column number " + Integer.toString(k + 1) +
                            " in the dataset and in the standardized data do not match");
            }
        }

        ArrayList<ADCMessage> msgList = stdUserData.getStandardizationMessages();

        // Get the dataset messages file to be written
        File msgsFile = messagesFile(dataset.getDatasetId());
        File parentFile = msgsFile.getParentFile();
        if ( !parentFile.exists() )
            parentFile.mkdirs();
        PrintWriter msgsWriter;
        try {
            msgsWriter = new PrintWriter(msgsFile);
        } catch ( FileNotFoundException ex ) {
            throw new RuntimeException("Unexpected error opening messages file " +
                    msgsFile.getPath() + "\n    " + ex.getMessage(), ex);
        }
        try {

            TreeMap<String,Integer> errorCnt = new TreeMap<String,Integer>();
            TreeMap<String,Integer> warnCnt = new TreeMap<String,Integer>();
            for (ADCMessage msg : msgList) {
                // Start with a summary giving the counts of each general message/severity
                QCFlag.Severity severity = msg.getSeverity();
                String summary = msg.getGeneralComment();
                if ( QCFlag.Severity.CRITICAL.equals(severity) || QCFlag.Severity.ERROR.equals(severity) ) {
                    Integer cnt = errorCnt.get(summary);
                    if ( cnt == null )
                        cnt = 1;
                    else
                        cnt += 1;
                    errorCnt.put(summary, cnt);
                }
                else if ( QCFlag.Severity.WARNING.equals(severity) ) {
                    Integer cnt = warnCnt.get(summary);
                    if ( cnt == null )
                        cnt = 1;
                    else
                        cnt += 1;
                    warnCnt.put(summary, cnt);
                }
            }
            for (Entry<String,Integer> sumCnt : warnCnt.entrySet()) {
                msgsWriter.println(MSG_SUMMARY_MSG_KEY + MSG_KEY_VALUE_SEP +
                        sumCnt.getValue() + " errors of type: " + sumCnt.getKey());
            }
            for (Entry<String,Integer> sumCnt : errorCnt.entrySet()) {
                msgsWriter.println(MSG_SUMMARY_MSG_KEY + MSG_KEY_VALUE_SEP +
                        sumCnt.getValue() + " warnings of type: " + sumCnt.getKey());
            }

            // WOCE-type QC flags to assign from the automated data check
            TreeSet<QCFlag> woceFlags = new TreeSet<QCFlag>();

            for (ADCMessage msg : msgList) {
                // Generate a list of key-value strings describing this message
                ArrayList<String> mappings = new ArrayList<String>();

                QCFlag.Severity severity = msg.getSeverity();
                mappings.add(MSG_SEVERITY_KEY + MSG_KEY_VALUE_SEP + severity.name());

                Integer rowNum = msg.getRowNumber();
                if ( (rowNum > 0) && (rowNum <= numSamples) &&
                        !DashboardUtils.INT_MISSING_VALUE.equals(rowNum) )
                    mappings.add(MSG_ROW_NUMBER_KEY + MSG_KEY_VALUE_SEP + rowNum);
                else
                    rowNum = null;

                Integer colNumber = msg.getColNumber();
                if ( (colNumber > 0) && (colNumber <= numUserCols) &&
                        !DashboardUtils.INT_MISSING_VALUE.equals(colNumber) )
                    mappings.add(MSG_COLUMN_NUMBER_KEY + MSG_KEY_VALUE_SEP + colNumber);
                else
                    colNumber = null;

                String colName = msg.getColName();
                if ( !DashboardUtils.STRING_MISSING_VALUE.equals(colName) )
                    mappings.add(MSG_COLUMN_NAME_KEY + MSG_KEY_VALUE_SEP + colName);

                // Assign the general message - escape newlines
                String summary = msg.getGeneralComment().replace("\n", "\\n");
                if ( !DashboardUtils.STRING_MISSING_VALUE.equals(summary) )
                    mappings.add(MSG_GENERAL_MSG_KEY + MSG_KEY_VALUE_SEP + summary);

                // Assign the detailed message - escape newlines
                String details = msg.getDetailedComment().replace("\n", "\\n");
                if ( !DashboardUtils.STRING_MISSING_VALUE.equals(details) )
                    mappings.add(MSG_DETAILED_MSG_KEY + MSG_KEY_VALUE_SEP + details);

                // Write this array list of key-value strings to file
                msgsWriter.println(DashboardUtils.encodeStringArrayList(mappings));

                // Create the QC flag for this message.
                if ( rowNum != null ) {
                    if ( QCFlag.Severity.CRITICAL.equals(severity) || QCFlag.Severity.ERROR.equals(severity) ) {
                        QCFlag flag;
                        if ( colNumber != null )
                            flag = new QCFlag(null, DashboardServerUtils.WOCE_BAD,
                                    QCFlag.Severity.ERROR, colNumber - 1, rowNum - 1);
                        else
                            flag = new QCFlag(null, DashboardServerUtils.WOCE_BAD,
                                    QCFlag.Severity.ERROR, null, rowNum - 1);
                        woceFlags.add(flag);
                    }
                    else if ( QCFlag.Severity.WARNING.equals(severity) ) {
                        QCFlag flag;
                        if ( colNumber > 0 )
                            flag = new QCFlag(null, DashboardServerUtils.WOCE_QUESTIONABLE,
                                    QCFlag.Severity.WARNING, colNumber - 1, rowNum - 1);
                        else
                            flag = new QCFlag(null, DashboardServerUtils.WOCE_QUESTIONABLE,
                                    QCFlag.Severity.WARNING, null, rowNum - 1);
                        woceFlags.add(flag);
                    }
                }
            }

            dataset.setCheckerFlags(woceFlags);

        } finally {
            msgsWriter.close();
        }

        // Commit the updated messages file, if possible
        try {
            commitVersion(msgsFile, "Updated checker messages for " + dataset.getDatasetId());
        } catch ( Exception ex ) {
            // ignore any errors at this time, as the file can be reproduced if needed
            // or may be configured without version control
        }

        // Assign any user-provided QC flags.
        // TODO: get severity from user-provided specification of the type
        // This assumes QC flags indicating problems have flag values that are integers 3-9.
        // If "WOCE" (case insensitive) is in the data type description, 3 is WARNING
        // and 4-9 are ERROR; otherwise 3-9 are all ERROR.
        TreeSet<QCFlag> qcFlags = new TreeSet<QCFlag>();
        for (int k = 0; k < numUserCols; k++) {
            DashDataType<?> colType = stdDataTypes.get(k);
            if ( !colType.isQCType() )
                continue;
            // Check for another column associated with this QC column
            int qcDataIdx = -1;
            for (int d = 0; d < numUserCols; d++) {
                if ( colType.isQCTypeFor(stdDataTypes.get(d)) ) {
                    qcDataIdx = d;
                    break;
                }
            }
            QCFlag.Severity severityOfThree = QCFlag.Severity.ERROR;
            if ( colType.getDescription().toUpperCase().contains("WOCE") )
                severityOfThree = QCFlag.Severity.WARNING;
            for (int j = 0; j < numSamples; j++) {
                try {
                    String flagVal = (String) stdUserData.getStdVal(j, k);
                    int value = Integer.parseInt(flagVal);
                    if ( (value >= 3) && (value <= 9) ) {
                        QCFlag.Severity severity;
                        if ( value == 3 )
                            severity = severityOfThree;
                        else
                            severity = QCFlag.Severity.ERROR;
                        QCFlag flag;
                        if ( qcDataIdx >= 0 )
                            flag = new QCFlag(colType.getVarName(), flagVal, severity, qcDataIdx, j);
                        else
                            flag = new QCFlag(colType.getVarName(), flagVal, severity, null, j);
                        qcFlags.add(flag);
                    }
                } catch ( NumberFormatException ex ) {
                    // Assuming a missing value
                }
            }
        }
        dataset.setUserFlags(qcFlags);
    }

    /**
     * Reads the list of messages from the messages file written by
     * {@link #processCheckerMessages(DashboardDataset, StdUserDataArray)}.
     *
     * @param datasetId
     *         get messages for the dataset with this ID
     *
     * @return the automated data checker messages for the dataset;
     *         never null, but may be empty if there were no messages.
     *         The datasetId, but not the username, will be assigned in the returned ADCMessageList
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, or if the messages file is invalid
     * @throws FileNotFoundException
     *         if there is no messages file for the dateset
     */
    public ADCMessageList getCheckerMessages(String datasetId) throws IllegalArgumentException, FileNotFoundException {
        // Create the list of messages to be returned
        ADCMessageList msgList = new ADCMessageList();
        msgList.setDatasetId(datasetId);
        // Directly modify the summary messages in the ADCMessageList
        ArrayList<String> summaryMsgs = msgList.getSummaries();
        // Read the cruise messages file
        File msgsFile = messagesFile(datasetId);
        BufferedReader msgReader;
        msgReader = new BufferedReader(new FileReader(msgsFile));
        try {
            try {
                String summmaryStart = MSG_SUMMARY_MSG_KEY + MSG_KEY_VALUE_SEP;
                String altSummmaryStart = "SC" + summmaryStart;
                for (String msgline = msgReader.readLine(); msgline != null; msgline = msgReader.readLine()) {
                    if ( msgline.trim().isEmpty() )
                        continue;

                    if ( msgline.startsWith(summmaryStart) ) {
                        summaryMsgs.add(msgline.substring(summmaryStart.length()).trim());
                        continue;
                    }
                    // For backwards compatibility
                    if ( msgline.startsWith(altSummmaryStart) ) {
                        summaryMsgs.add(msgline.substring(altSummmaryStart.length()).trim());
                        continue;
                    }

                    Properties msgProps = new Properties();
                    try {
                        for (String msgPart : DashboardUtils.decodeStringArrayList(msgline)) {
                            String[] keyValue = msgPart.split(MSG_KEY_VALUE_SEP, 2);
                            if ( keyValue.length != 2 )
                                throw new IOException("Invalid key:value pair '" + msgPart + "'");
                            msgProps.setProperty(keyValue[0], keyValue[1]);
                        }
                    } catch ( IllegalArgumentException ex ) {
                        throw new IOException("Invalid saved checker message: " + msgline);
                    }

                    ADCMessage msg = new ADCMessage();

                    try {
                        String propVal = msgProps.getProperty(MSG_SEVERITY_KEY);
                        if ( propVal == null )
                            propVal = msgProps.getProperty("SC" + MSG_SEVERITY_KEY);
                        msg.setSeverity(QCFlag.Severity.valueOf(propVal));
                    } catch ( Exception ex ) {
                        // leave as the default
                    }

                    try {
                        String propVal = msgProps.getProperty(MSG_ROW_NUMBER_KEY);
                        if ( propVal == null )
                            propVal = msgProps.getProperty("SC" + MSG_ROW_NUMBER_KEY);
                        msg.setRowNumber(Integer.parseInt(propVal));
                    } catch ( Exception ex ) {
                        // leave as the default
                    }

                    try {
                        String propVal = msgProps.getProperty(MSG_COLUMN_NUMBER_KEY);
                        if ( propVal == null )
                            propVal = msgProps.getProperty("SC" + MSG_COLUMN_NUMBER_KEY);
                        msg.setColNumber(Integer.parseInt(propVal));
                    } catch ( Exception ex ) {
                        // leave as the default
                    }

                    try {
                        String propVal = msgProps.getProperty(MSG_COLUMN_NAME_KEY);
                        if ( propVal == null )
                            propVal = msgProps.getProperty("SC" + MSG_COLUMN_NAME_KEY);
                        msg.setColName(propVal);
                    } catch ( Exception ex ) {
                        // leave as the default
                    }

                    try {
                        String propVal = msgProps.getProperty(MSG_GENERAL_MSG_KEY);
                        if ( propVal == null )
                            propVal = msgProps.getProperty("SC" + MSG_GENERAL_MSG_KEY);
                        // Replace all escaped newlines in the message string
                        if ( propVal != null ) {
                            propVal = propVal.replace("\\n", "\n");
                            msg.setGeneralComment(propVal);
                        }
                    } catch ( Exception ex ) {
                        // leave as the default
                    }

                    try {
                        String propVal = msgProps.getProperty(MSG_DETAILED_MSG_KEY);
                        if ( propVal == null )
                            propVal = msgProps.getProperty("SC" + MSG_DETAILED_MSG_KEY);
                        if ( propVal != null ) {
                            // Replace all escaped newlines in the message string
                            propVal = propVal.replace("\\n", "\n");
                            msg.setDetailedComment(propVal);
                        }
                    } catch ( Exception ex ) {
                        // leave as the default
                    }

                    msgList.add(msg);
                }
            } finally {
                msgReader.close();
            }
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Unexpected problem reading messages from " +
                    msgsFile.getPath() + "\n    " + ex.getMessage(), ex);
        }

        return msgList;
    }

}
