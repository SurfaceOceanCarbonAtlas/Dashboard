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
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.dsg.StdUserDataArray;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.ADCMessage;
import gov.noaa.pmel.dashboard.shared.ADCMessage.SCMsgSeverity;
import gov.noaa.pmel.dashboard.shared.ADCMessageList;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCFlag;
import gov.noaa.pmel.dashboard.shared.QCFlag.Severity;

/**
 * Processes automated data check flags and messages, 
 * as well as PI-provided QC flags, for a dataset.
 * 
 * @author Karl Smith
 */
public class CheckerMessageHandler {

	private static final String MSGS_FILENAME_EXTENSION = ".messages";
	private static final String MSG_KEY_VALUE_SEP = ":";
	private static final String MSG_SEVERITY_KEY = "MsgSeverity";
	private static final String MSG_ROW_NUMBER_KEY = "MsgRowNumber";
	private static final String MSG_LONGITUDE_KEY = "MsgLongitude";
	private static final String MSG_LATITUDE_KEY = "MsgLatitude";
	private static final String MSG_DEPTH_KEY = "MsgDepth";
	private static final String MSG_TIMESTAMP_KEY = "MsgTimestamp";
	private static final String MSG_COLUMN_NUMBER_KEY = "MsgColumnNumber";
	private static final String MSG_COLUMN_NAME_KEY = "MsgColumnName";
	private static final String MSG_GENERAL_MSG_KEY = "MsgGeneralMessage";
	private static final String MSG_DETAILED_MSG_KEY = "MsgDetailedMessage";
	private static final String MSG_SUMMARY_MSG_KEY = "MsgSummaryMessage";

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
					"the automated data checker messages file for " + datasetId);
		}
		return true;
	}

	/**
	 * Save the list of automated data check messages given with a standardized 
	 * user data array object.  Using these messages, assigns the WOCE_AUTOCHECK 
	 * data column values in the standardized data array as well as the set of
	 * automated data check flags in the dataset.  Also assigns the set of 
	 * PI-provided QC flags in the dataset.  This assumes PI-provided QC flags 
	 * indicating problems have flag values that are integers in the range [3,9].
	 * 
	 * @param dataset
	 * 		save message for, and assign QC flag sets in, this dataset
	 * @param stdUserData
	 * 		standardized user data for this dataset 
	 * 		containing automated data check messages
	 * @throws IllegalArgumentException
	 * 		if the dataset or standardized user data is invalid
	 */
	public void processCheckerMessages(DashboardDataset dataset, 
			StdUserDataArray stdUserData) throws IllegalArgumentException {
		int numSamples = stdUserData.getNumSamples();
		if ( numSamples <= 0 )
			throw new IllegalArgumentException("no standardized data");
		int numStdCols = stdUserData.getNumDataCols();
		if ( numStdCols <= 0 )
			throw new IllegalArgumentException("no standardized data columns");
		List<DashDataType<?>> stdDataTypes = stdUserData.getDataTypes();
		if ( stdDataTypes.indexOf(DashboardServerUtils.WOCE_AUTOCHECK) < 0 ) 
			throw new IllegalArgumentException("no WOCE_AUTOCHECK standardized data column");

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
			for ( DataColumnType dtype : dataset.getDataColTypes() ) {
				k++;
				if ( ! stdDataTypes.get(k).typeNameEquals(dtype) )
					throw new IllegalArgumentException("types for column number " + Integer.toString(k+1) + 
							" in the dataset and in the standardized data do not match");
			}
		}

		ArrayList<ADCMessage> msgList = stdUserData.getStandardizationMessages();
		stdUserData.resetWoceAutocheck();

		// Get the dataset messages file to be written
		File msgsFile = messagesFile(dataset.getDatasetId());
		File parentFile = msgsFile.getParentFile();
		if ( ! parentFile.exists() )
			parentFile.mkdirs();
		PrintWriter msgsWriter;
		try {
			msgsWriter = new PrintWriter(msgsFile);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Unexpected error opening messages file " + 
					msgsFile.getPath() + "\n    " + ex.getMessage(), ex);
		}
		try {

			TreeMap<String,Integer> errorCnt = new TreeMap<String,Integer>();
			TreeMap<String,Integer> warnCnt = new TreeMap<String,Integer>();
			for ( ADCMessage msg : msgList ) {
				// Start with a summary giving the counts of each general message/severity
				ADCMessage.SCMsgSeverity severity = msg.getSeverity();
				String summary = msg.getGeneralComment();
				if ( ADCMessage.SCMsgSeverity.CRITICAL.equals(severity) || 
					 ADCMessage.SCMsgSeverity.ERROR.equals(severity) ) {
					Integer cnt = errorCnt.get(summary);
					if ( cnt == null )
						cnt = 1;
					else
						cnt += 1;
					errorCnt.put(summary, cnt);
				}
				else if ( ADCMessage.SCMsgSeverity.WARNING.equals(severity) ) {
					Integer cnt = warnCnt.get(summary);
					if ( cnt == null )
						cnt = 1;
					else
						cnt += 1;
					warnCnt.put(summary, cnt);
				}
			}
			for ( Entry<String,Integer> sumCnt : warnCnt.entrySet() ) {
				msgsWriter.println(MSG_SUMMARY_MSG_KEY + MSG_KEY_VALUE_SEP  + 
						sumCnt.getValue() + " errors of type: " + sumCnt.getKey());
			}
			for ( Entry<String,Integer> sumCnt : errorCnt.entrySet() ) {
				msgsWriter.println(MSG_SUMMARY_MSG_KEY + MSG_KEY_VALUE_SEP + 
						sumCnt.getValue() + " warnings of type: " + sumCnt.getKey());
			}

			// WOCE-type QC flags to assign from the automated data check
			TreeSet<QCFlag> woceFlags = new TreeSet<QCFlag>();
			String woceFlagName = DashboardServerUtils.WOCE_AUTOCHECK.getVarName();

			for ( ADCMessage msg : msgList ) {
				// Generate a list of key-value strings describing this message
				ArrayList<String> mappings = new ArrayList<String>();

				ADCMessage.SCMsgSeverity severity = msg.getSeverity();
				mappings.add(MSG_SEVERITY_KEY + MSG_KEY_VALUE_SEP + severity.name());

				Integer rowNum = msg.getRowNumber();
				if ( (rowNum > 0) && (rowNum <= numSamples) && 
						! DashboardUtils.INT_MISSING_VALUE.equals(rowNum) )
					mappings.add(MSG_ROW_NUMBER_KEY + MSG_KEY_VALUE_SEP + rowNum);
				else
					rowNum = null;

				Double longitude = msg.getLongitude();
				if ( ! DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, longitude, 
						DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR))
					mappings.add(MSG_LONGITUDE_KEY + MSG_KEY_VALUE_SEP + longitude);
				else
					longitude = null;

				Double latitude = msg.getLatitude();
				if ( ! DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, latitude, 
						DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR))
					mappings.add(MSG_LATITUDE_KEY + MSG_KEY_VALUE_SEP + latitude);
				else
					latitude = null;

				Double depth = msg.getDepth();
				if ( ! DashboardUtils.closeTo(DashboardUtils.FP_MISSING_VALUE, depth, 
						DashboardUtils.MAX_RELATIVE_ERROR, DashboardUtils.MAX_ABSOLUTE_ERROR))
					mappings.add(MSG_DEPTH_KEY + MSG_KEY_VALUE_SEP + depth);
				else
					depth = null;

				String timestamp = msg.getTimestamp();
				if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(timestamp) )
					mappings.add(MSG_TIMESTAMP_KEY + MSG_KEY_VALUE_SEP + timestamp);
				else
					timestamp = null;

				Integer colNumber = msg.getColNumber();
				if ( (colNumber > 0) && (colNumber <= numUserCols) && 
						! DashboardUtils.INT_MISSING_VALUE.equals(colNumber) )
					mappings.add(MSG_COLUMN_NUMBER_KEY + MSG_KEY_VALUE_SEP + colNumber);
				else
					colNumber = null;

				String colName = msg.getColName();
				if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(colName) )
					mappings.add(MSG_COLUMN_NAME_KEY + MSG_KEY_VALUE_SEP + colName);
				else
					colName = null;

				// Assign the general message - escape newlines
				String summary = msg.getGeneralComment().replace("\n",  "\\n");
				if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(summary) )
					mappings.add(MSG_GENERAL_MSG_KEY + MSG_KEY_VALUE_SEP + summary);
				else
					summary = null;

				// Assign the detailed message - escape newlines
				String details = msg.getDetailedComment().replace("\n",  "\\n");
				if ( ! DashboardUtils.STRING_MISSING_VALUE.equals(details) )
					mappings.add(MSG_DETAILED_MSG_KEY + MSG_KEY_VALUE_SEP + details);
				else
					details = null;

				// Write this array list of key-value strings to file
				msgsWriter.println(DashboardUtils.encodeStringArrayList(mappings));

				// Create the QC flag for this message.
				if ( rowNum != null ) {
					if ( ADCMessage.SCMsgSeverity.CRITICAL.equals(severity) ||
						 ADCMessage.SCMsgSeverity.ERROR.equals(severity) ) {
						QCFlag flag;
						if ( colNumber != null )
							flag = new QCFlag(woceFlagName, DashboardServerUtils.WOCE_BAD, 
									Severity.BAD, colNumber-1, rowNum-1);
						else
							flag = new QCFlag(woceFlagName, DashboardServerUtils.WOCE_BAD, 
									Severity.BAD, null, rowNum-1);
						woceFlags.add(flag);
						stdUserData.setWoceAutocheck(rowNum-1, DashboardServerUtils.WOCE_BAD);
					}
					else if ( ADCMessage.SCMsgSeverity.WARNING.equals(severity) ) {
						QCFlag flag;
						if ( colNumber > 0 )
							flag = new QCFlag(woceFlagName, DashboardServerUtils.WOCE_QUESTIONABLE, 
									Severity.QUESTIONABLE, colNumber-1, rowNum-1);
						else
							flag = new QCFlag(woceFlagName, DashboardServerUtils.WOCE_QUESTIONABLE, 
									Severity.QUESTIONABLE, null, rowNum-1);
						woceFlags.add(flag);
						stdUserData.setWoceAutocheck(rowNum-1, DashboardServerUtils.WOCE_QUESTIONABLE);
					}
				}
			}

			dataset.setCheckerFlags(woceFlags);

		} finally {
			msgsWriter.close();
		}

		// Assign any user-provided QC flags.
		// TODO: get severity from user-provided specification of the type
		// This assumes QC flags indicating problems have flag values that are integers 3-9.
		// If "WOCE" (case insensitive) is in the data type description, 3 is QUESTIONABLE 
		// and 4-9 are BAD; otherwise 3-9 are all BAD.
		TreeSet<QCFlag> qcFlags = new TreeSet<QCFlag>();
		for (int k = 0; k < numUserCols; k++) {
			DashDataType<?> colType = stdDataTypes.get(k);
			if ( ! colType.isQCType() )
				continue;
			// Check for another column associated with this QC column
			int qcDataIdx = -1;
			for (int d = 0; d < numUserCols; d++) {
				if ( colType.isQCTypeFor(stdDataTypes.get(d)) ) {
					qcDataIdx = d;
					break;
				}
			}
			Severity severityOfThree = Severity.BAD;
			if ( colType.getDescription().toUpperCase().contains("WOCE") )
				severityOfThree = Severity.QUESTIONABLE;
			for (int j = 0; j < numSamples; j++) {
				try {
					Character flagVal = (Character) stdUserData.getStdVal(j, k);
					int value = Integer.parseInt(flagVal.toString());
					if ( (value >= 3) && (value <= 9) ) {
						Severity severity;
						if ( value == 3 )
							severity = severityOfThree;
						else
							severity = Severity.BAD;
						QCFlag flag;
						if ( qcDataIdx >= 0 )
							flag = new QCFlag(colType.getVarName(), flagVal, severity, qcDataIdx, k);
						else
							flag = new QCFlag(colType.getVarName(), flagVal, severity, null, k);
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

						if ( msgline.startsWith(MSG_SUMMARY_MSG_KEY + MSG_KEY_VALUE_SEP) ) {
							summaryMsgs.add(msgline.substring(MSG_SUMMARY_MSG_KEY.length() + 
									MSG_KEY_VALUE_SEP.length()).trim());
							msgline = msgReader.readLine();
							continue;
						}

						Properties msgProps = new Properties();
						for ( String msgPart : DashboardUtils.decodeStringArrayList(msgline) ) {
							String[] keyValue = msgPart.split(MSG_KEY_VALUE_SEP, 2);
							if ( keyValue.length != 2 )
								throw new IOException("Invalid key:value pair '" + msgPart + "'");
							msgProps.setProperty(keyValue[0], keyValue[1]);
						}

						ADCMessage msg = new ADCMessage();

						String propVal = msgProps.getProperty(MSG_SEVERITY_KEY);
						try {
							msg.setSeverity(SCMsgSeverity.valueOf(propVal));
						} catch ( Exception ex ) {
							// leave as the default SCMsgSeverity.UNKNOWN
						}

						propVal = msgProps.getProperty(MSG_ROW_NUMBER_KEY);
						try {
							msg.setRowNumber(Integer.parseInt(propVal));
						} catch ( Exception ex ) {
							// leave as the default -1
						}

						propVal = msgProps.getProperty(MSG_LONGITUDE_KEY);
						try {
							msg.setLongitude(Double.valueOf(propVal));
						} catch ( Exception ex ) {
							// leave as the default Double.NaN
						}

						propVal = msgProps.getProperty(MSG_LATITUDE_KEY);
						try {
							msg.setLatitude(Double.valueOf(propVal));
						} catch ( Exception ex ) {
							// leave as the default Double.NaN
						}

						propVal = msgProps.getProperty(MSG_TIMESTAMP_KEY);
						if ( propVal != null ) {
							msg.setTimestamp(propVal);
						}
						// default timestamp is an empty string

						propVal = msgProps.getProperty(MSG_COLUMN_NUMBER_KEY);
						try {
							msg.setColNumber(Integer.parseInt(propVal));
						} catch ( Exception ex ) {
							// leave as the default -1
						}

						propVal = msgProps.getProperty(MSG_COLUMN_NAME_KEY);
						if ( propVal != null ) {
							msg.setColName(propVal);
						}
						// default column name is an empty string 

						propVal = msgProps.getProperty(MSG_GENERAL_MSG_KEY);
						if ( propVal != null ) {
							// Replace all escaped newlines in the message string
							propVal = propVal.replace("\\n", "\n");
							msg.setGeneralComment(propVal);
						}
						// default general explanation is an empty string

						propVal = msgProps.getProperty(MSG_DETAILED_MSG_KEY);
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
