/**
 * 
 */
package gov.noaa.pmel.dashboard.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCFlag;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.ADCMessage.SCMsgSeverity;
import gov.noaa.pmel.dashboard.shared.ADCMessageList;

import uk.ac.uea.socat.sanitychecker.Output;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.messages.Message;
import uk.ac.uea.socat.sanitychecker.messages.MessageException;
import uk.ac.uea.socat.sanitychecker.messages.MessageSummary;

/**
 * Processes automated data check flags and messages, 
 * as well as PI-provided QC flags, for a dataset.
 * 
 * @author Karl Smith
 */
public class CheckerMessageHandler {

	private static final String MSGS_FILENAME_EXTENSION = ".messages";
	private static final String SCMSG_KEY_VALUE_SEP = ":";
	private static final String SCMSG_SEVERITY_KEY = "SCMsgSeverity";
	private static final String SCMSG_ROW_NUMBER_KEY = "SCMsgRowNumber";
	private static final String SCMSG_LONGITUDE_KEY = "SCMsgLongitude";
	private static final String SCMSG_LATITUDE_KEY = "SCMsgLatitude";
	private static final String SCMSG_TIMESTAMP_KEY = "SCMsgTimestamp";
	private static final String SCMSG_COLUMN_NUMBER_KEY = "SCMsgColumnNumber";
	private static final String SCMSG_COLUMN_NAME_KEY = "SCMsgColumnName";
	private static final String SCMSG_GENERAL_MSG_KEY = "SCMsgGeneralMessage";
	private static final String SCMSG_DETAILED_MSG_KEY = "SCMsgDetailedMessage";
	private static final String SCMSG_OLD_MESSAGE_KEY = "SCMsgMessage";
	private static final String SCMSG_SUMMARY_MSG_KEY = "SCMsgSummaryMessage";

	private static final DateTimeFormatter DATETIME_FORMATTER = 
			DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");

	private File filesDir;

	/**
	 * Handler for automated data check flags and messages.
	 * 
	 * @param filesDirName
	 * 		save messages under this directory
	 */
	public CheckerMessageHandler(String filesDirName) {
		filesDir = new File(filesDirName);
		if ( ! filesDir.isDirectory() )
			throw new IllegalArgumentException(filesDirName + " is not a directory");
	}

	/**
	 * @param datasetId
	 * 		ID of the dataset
	 * @return
	 * 		the messages file associated with the dataset
	 * @throws IllegalArgumentException
	 * 		if the dataset ID is invalid
	 */
	private File messagesFile(String datasetId) throws IllegalArgumentException {
		// standardize the dataset ID
		String stdId = DashboardServerUtils.checkDatasetID(datasetId);
		// Get the parent directory
		File parentDir = new File(filesDir, stdId.substring(0,4));
		// Get the dataset messages file
		File msgsFile = new File(parentDir, stdId + MSGS_FILENAME_EXTENSION);
		return msgsFile;
	}

	/**
	 * Appropriately renames a messages file, if one exists, 
	 * for a change in dataset ID.
	 * 
	 * @param oldId
	 * 		standardized old ID for the dataset
	 * @param newId
	 * 		standardized new ID for the dataset
	 * @throws IllegalArgumentException
	 * 		if a messages file for the new ID already exists, or
	 * 		if unable to rename the messages file
	 */
	public void renameMsgsFile(String oldId, String newId) throws IllegalArgumentException {
		File oldMsgsFile = messagesFile(oldId);
		if ( ! oldMsgsFile.exists() ) 
			return;

		File newMsgsFile = messagesFile(newId);
		if ( newMsgsFile.exists() )
			throw new IllegalArgumentException("Messages file already exists for " + newId);

		File newParent = newMsgsFile.getParentFile();
		if ( ! newParent.exists() ) 
			newParent.mkdirs();

		if ( ! oldMsgsFile.renameTo(newMsgsFile) ) 
			throw new IllegalArgumentException("Unable to rename messages file from " + 
					oldId + " to " + newId);
	}

	/**
	 * Deletes the messages file, if it exists, associated with a dataset.
	 * 
	 * @param datasetId
	 * 		delete the messages file for the dataset with this ID
	 * @return
	 * 		true if messages file exists and was deleted; 
	 * 		false if the messages file does not exist.
	 * @throws IllegalArgumentException
	 * 		if the dataset ID is invalid, or
	 * 		if the messages file exists but could not be deleted
	 */
	public boolean deleteMsgsFile(String datasetId) throws IllegalArgumentException {
		File msgsFile = messagesFile(datasetId);
		if ( ! msgsFile.exists() )
			return false;
		if ( ! msgsFile.delete() ) {
			throw new IllegalArgumentException("Unable to delete " +
					"the sanity checker messages file for " + datasetId);
		}
		return true;
	}

	/**
	 * Processes the list of messages produced by the automated data checker.
	 * Saves the automated data checker messages to the appropriate messages 
	 * file.  Clears and assigns the checker flags for the dataset using the 
	 * given automated data checker output.  Clears and assigns the user flags 
	 * for the dataset using PI-provided QC flags in the data.  This assumes
	 * PI-provided QC flags indicating problems have flag values that are 
	 * integers in the range [3,9].
	 * 
	 * @param dataset
	 * 		set flags and messages for this dataset
	 * @param output
	 * 		automated data checker output for this dataset
	 * @throws IllegalArgumentException
	 * 		if the ID given in the dataset is invalid
	 */
	public void processCheckerMessages(DashboardDatasetData dataset, 
							Output output) throws IllegalArgumentException {
		// Get the dataset messages file to be written
		File msgsFile = messagesFile(dataset.getDatasetId());
		// Create the parent subdirectories if they do not exist
		File parentFile = msgsFile.getParentFile();
		if ( ! parentFile.exists() )
			parentFile.mkdirs();
		// Write the messages to file and save WOCE-type QC flags from these messages
		PrintWriter msgsWriter;
		try {
			msgsWriter = new PrintWriter(msgsFile);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Unexpected error opening messages file " + 
					msgsFile.getPath() + "\n    " + ex.getMessage(), ex);
		}

		try {

			List<SocatDataRecord> dataRecs = output.getRecords();
			int numRecs = dataRecs.size();
			try {

				for ( MessageSummary summary : output.getMessages().getMessageSummaries() ) {
					String msg = summary.getSummaryString();
					int count = summary.getErrorCount();
					if ( count > 0 ) {
						msgsWriter.println(SCMSG_SUMMARY_MSG_KEY + SCMSG_KEY_VALUE_SEP  + 
								Integer.toString(count) + " errors of type: " + msg);
					}
					count = summary.getWarningCount();
					if ( count > 0 ) {
						msgsWriter.println(SCMSG_SUMMARY_MSG_KEY + SCMSG_KEY_VALUE_SEP + 
								Integer.toString(count) + " warnings of type: " + msg);
					}
				}

				// WOCE-type QC flags to assign from the automated data check
				TreeSet<QCFlag> woceFlags = new TreeSet<QCFlag>();
				String woceFlagName = DashboardUtils.WOCE_AUTOCHECK.getVarName();

				for ( Message msg : output.getMessages().getMessages() ) {
					int rowNum = msg.getLineNumber();
					int colNum = msg.getColumnIndex();

					// Generate a list of key-value strings describing this message
					ArrayList<String> mappings = new ArrayList<String>();

					if ( msg.isError() ) {
						mappings.add(SCMSG_SEVERITY_KEY + SCMSG_KEY_VALUE_SEP + 
								SCMsgSeverity.ERROR.name());
					}
					else if ( msg.isWarning() ) {
						mappings.add(SCMSG_SEVERITY_KEY + SCMSG_KEY_VALUE_SEP + 
								SCMsgSeverity.WARNING.name());
					}

					if ( (rowNum > 0) && (rowNum <= numRecs) ) {
						mappings.add(SCMSG_ROW_NUMBER_KEY + SCMSG_KEY_VALUE_SEP + 
								Integer.toString(rowNum));

						SocatDataRecord stdData = dataRecs.get(rowNum - 1);
						try {
							double longitude = stdData.getLongitude();
							if ( ! Double.isNaN(longitude) )
								mappings.add(SCMSG_LONGITUDE_KEY + SCMSG_KEY_VALUE_SEP + 
										Double.toString(longitude));
						} catch ( Exception ex ) {
							// no entry
						}
						try {
							double latitude = stdData.getLatitude();
							if ( ! Double.isNaN(latitude) )
								mappings.add(SCMSG_LATITUDE_KEY + SCMSG_KEY_VALUE_SEP + 
										Double.toString(latitude));
						} catch ( Exception ex ) {
							// no entry
						}
						try {
							DateTime timestamp = stdData.getTime();
							if ( timestamp != null )
								mappings.add(SCMSG_TIMESTAMP_KEY + SCMSG_KEY_VALUE_SEP +
										DATETIME_FORMATTER.print(timestamp));
						} catch ( Exception ex ) {
							// no entry
						}
					}

					if ( colNum > 0 )
						mappings.add(SCMSG_COLUMN_NUMBER_KEY + SCMSG_KEY_VALUE_SEP + 
								Integer.toString(colNum));

					String colName = msg.getColumnName();
					if ( colName != null )
						mappings.add(SCMSG_COLUMN_NAME_KEY + SCMSG_KEY_VALUE_SEP + colName);

					// Assign the general message - escape newlines
					String checkerMsg = msg.getMessageType().getSummaryMessage(colName).replace("\n",  "\\n");
					mappings.add(SCMSG_GENERAL_MSG_KEY + SCMSG_KEY_VALUE_SEP + checkerMsg);

					// Assign the detailed message - escape newlines
					checkerMsg = msg.getMessageString().replace("\n",  "\\n");
					mappings.add(SCMSG_DETAILED_MSG_KEY + SCMSG_KEY_VALUE_SEP + checkerMsg);

					// Write this array list of key-value strings to file
					msgsWriter.println(DashboardUtils.encodeStringArrayList(mappings));

					// Create the QC flags for this message.
					if ( rowNum > 0 ) {
						if ( msg.isError() ) {
							QCFlag flag;
							if ( colNum > 0 )
								flag = new QCFlag(woceFlagName, DashboardUtils.WOCE_BAD, 
										Severity.BAD, colNum-1, rowNum-1);
							else
								flag = new QCFlag(woceFlagName, DashboardUtils.WOCE_BAD, 
										Severity.BAD, null, rowNum-1);
							woceFlags.add(flag);
						}
						else if ( msg.isWarning() ) {
							QCFlag flag;
							if ( colNum > 0 ) {
								flag = new QCFlag(woceFlagName, DashboardUtils.WOCE_QUESTIONABLE, 
										Severity.QUESTIONABLE, colNum-1, rowNum-1);
							}
							else {
								flag = new QCFlag(woceFlagName, DashboardUtils.WOCE_QUESTIONABLE, 
										Severity.QUESTIONABLE, null, rowNum-1);
							}
							woceFlags.add(flag);
						}
					}
				}

				dataset.setCheckerFlags(woceFlags);

			} catch ( MessageException ex ) {
				throw new RuntimeException(ex);
			}

		} finally {
			msgsWriter.close();
		}

		DashboardConfigStore configStore = DashboardConfigStore.get(false);
		KnownDataTypes userKnownTypes = configStore.getKnownUserDataTypes();
		// Assign any user-provided QC flags.
		// TODO: get severity from user-provided specification of the type
		// This assumes QC flags indicating problems have flag values that are integers 3-9.
		// If "WOCE" (case insensive) is in the data type description, 3 is QUESTIONABLE 
		// and 4-9 are BAD; otherwise 3-9 are all BAD.
		ArrayList<DataColumnType> columnTypes = dataset.getDataColTypes();
		TreeSet<QCFlag> qcFlags = new TreeSet<QCFlag>();
		for (int k = 0; k < columnTypes.size(); k++) {
			// Look for QC columns
			DashDataType<?> colType = userKnownTypes.getDashDataType(columnTypes.get(k));
			if ( ! colType.isQCType() )
				continue;
			// Check for another column associated with this QC column
			int colIdx = -1;
			for (int j = 0; j < columnTypes.size(); j++) {
				if ( colType.isCommentTypeFor(userKnownTypes.getDashDataType(columnTypes.get(j))) ) {
					colIdx = j;
					break;
				}
			}
			Severity severityOfThree = Severity.BAD;
			if ( colType.getDescription().toUpperCase().contains("WOCE") )
				severityOfThree = Severity.QUESTIONABLE;
			for (int rowIdx = 0; rowIdx < dataset.getNumDataRows(); rowIdx++) {
				try {
					int value = Integer.parseInt(dataset.getDataValues().get(rowIdx).get(k));
					if ( (value >= 3) && (value <= 9) ) {
						Severity severity;
						if ( value == 3 )
							severity = severityOfThree;
						else
							severity = Severity.BAD;
						QCFlag flag;
						if ( colIdx >= 0 )
							flag = new QCFlag(colType.getVarName(), Integer.toString(value).charAt(0), severity, colIdx, rowIdx);
						else
							flag = new QCFlag(colType.getVarName(), Integer.toString(value).charAt(0), severity, null, rowIdx);
						qcFlags.add(flag);
					}
				} catch (NumberFormatException ex) {
					// Assuming a missing value
				}
			}
		}
		dataset.setUserFlags(qcFlags);
	}

	/**
	 * Reads the list of messages from the messages file written by 
	 * @link #processCheckerMessages(DashboardDatasetData, Output)}.
	 * 
	 * @param datasetId
	 * 		get messages for the dataset with this ID
	 * @return
	 * 		the automated data checker messages for the dataset;
	 * 		never null, but may be empty if there were no messages.
	 * 		The datasetId, but not the username, will be assigned 
	 * 		in the returned ADCMessageList
	 * @throws IllegalArgumentException
	 * 		if the dataset ID is invalid, or 
	 * 		if the messages file is invalid
	 * @throws FileNotFoundException
	 * 		if there is no messages file for the dateset
	 */
	public ADCMessageList getCheckerMessages(String datasetId) 
			throws IllegalArgumentException, FileNotFoundException {
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
				String msgline = msgReader.readLine();
				while ( msgline != null ) {
					if ( ! msgline.trim().isEmpty() ) {

						if ( msgline.startsWith(SCMSG_SUMMARY_MSG_KEY + SCMSG_KEY_VALUE_SEP) ) {
							summaryMsgs.add(msgline.substring(SCMSG_SUMMARY_MSG_KEY.length() + 
									SCMSG_KEY_VALUE_SEP.length()).trim());
							msgline = msgReader.readLine();
							continue;
						}

						Properties msgProps = new Properties();
						for ( String msgPart : DashboardUtils.decodeStringArrayList(msgline) ) {
							String[] keyValue = msgPart.split(SCMSG_KEY_VALUE_SEP, 2);
							if ( keyValue.length != 2 )
								throw new IOException("Invalid key:value pair '" + msgPart + "'");
							msgProps.setProperty(keyValue[0], keyValue[1]);
						}

						ADCMessage msg = new ADCMessage();

						String propVal = msgProps.getProperty(SCMSG_SEVERITY_KEY);
						try {
							msg.setSeverity(SCMsgSeverity.valueOf(propVal));
						} catch ( Exception ex ) {
							// leave as the default SCMsgSeverity.UNKNOWN
						}

						propVal = msgProps.getProperty(SCMSG_ROW_NUMBER_KEY);
						try {
							msg.setRowNumber(Integer.parseInt(propVal));
						} catch ( Exception ex ) {
							// leave as the default -1
						}

						propVal = msgProps.getProperty(SCMSG_LONGITUDE_KEY);
						try {
							msg.setLongitude(Double.valueOf(propVal));
						} catch ( Exception ex ) {
							// leave as the default Double.NaN
						}

						propVal = msgProps.getProperty(SCMSG_LATITUDE_KEY);
						try {
							msg.setLatitude(Double.valueOf(propVal));
						} catch ( Exception ex ) {
							// leave as the default Double.NaN
						}

						propVal = msgProps.getProperty(SCMSG_TIMESTAMP_KEY);
						if ( propVal != null ) {
							msg.setTimestamp(propVal);
						}
						// default timestamp is an empty string

						propVal = msgProps.getProperty(SCMSG_COLUMN_NUMBER_KEY);
						try {
							msg.setColNumber(Integer.parseInt(propVal));
						} catch ( Exception ex ) {
							// leave as the default -1
						}

						propVal = msgProps.getProperty(SCMSG_COLUMN_NAME_KEY);
						if ( propVal != null ) {
							msg.setColName(propVal);
						}
						// default column name is an empty string 

						propVal = msgProps.getProperty(SCMSG_GENERAL_MSG_KEY);
						if ( propVal == null )
							propVal = msgProps.getProperty(SCMSG_OLD_MESSAGE_KEY);
						if ( propVal != null ) {
							// Replace all escaped newlines in the message string
							propVal = propVal.replace("\\n", "\n");
							msg.setGeneralComment(propVal);
						}
						// default general explanation is an empty string

						propVal = msgProps.getProperty(SCMSG_DETAILED_MSG_KEY);
						if ( propVal == null )
							propVal = msgProps.getProperty(SCMSG_OLD_MESSAGE_KEY);
						if ( propVal != null ) {
							// Replace all escaped newlines in the message string
							propVal = propVal.replace("\\n", "\n");
							msg.setDetailedComment(propVal);
						}
						// default detailed explanation is an empty string

						msgList.add(msg);

						msgline = msgReader.readLine();
					}
				}
			} finally {
				msgReader.close();
			}
		} catch (IOException ex) {
			throw new IllegalArgumentException("Unexpected problem reading messages from " + 
					msgsFile.getPath() + "\n    " + ex.getMessage(), ex);
		}

		return msgList;
	}

}
