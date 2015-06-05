/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.handlers;

import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.DataLocation;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage.SCMsgSeverity;
import gov.noaa.pmel.socat.dashboard.shared.SCMessageList;
import gov.noaa.pmel.socat.dashboard.shared.SocatEvent;
import gov.noaa.pmel.socat.dashboard.shared.SocatWoceEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import uk.ac.uea.socat.sanitychecker.Output;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.messages.Message;
import uk.ac.uea.socat.sanitychecker.messages.MessageException;
import uk.ac.uea.socat.sanitychecker.messages.MessageSummary;

/**
 * Processes SanityChecker messages for a cruise.
 * 
 * @author Karl Smith
 */
public class CheckerMessageHandler {

	private static final String CRUISE_MSGS_FILENAME_EXTENSION = ".messages";
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
	 * Handler for SanityChecker messages, including categorizing and making WOCE flags
	 * and events from them.
	 * 
	 * @param filesDirName
	 * 		save SanityChecker messages under this directory
	 */
	public CheckerMessageHandler(String filesDirName) {
		filesDir = new File(filesDirName);
		if ( ! filesDir.isDirectory() )
			throw new IllegalArgumentException(filesDirName + " is not a directory");
	}

	/**
	 * 
	 * @param expocode
	 * 		expocode of the cruise
	 * @return
	 * 		the cruise messages file associated with the cruise
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is invalid
	 */
	private File cruiseMsgsFile(String expocode) throws IllegalArgumentException {
		// Check that the expocode is somewhat reasonable
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		// Get the name of the cruise messages file
		return new File(filesDir, upperExpo.substring(0,4) + 
				File.separatorChar + upperExpo + CRUISE_MSGS_FILENAME_EXTENSION);
	}

	/**
	 * Appropriately renames a cruise messages file, if one exists, 
	 * for a change in cruise expocode.
	 * 
	 * @param oldExpocode
	 * 		standardized old expocode of the cruise
	 * @param newExpocode
	 * 		standardized new expocode for the cruise
	 * @throws IllegalArgumentException
	 * 		if a messages file for the new expocode already exists, or
	 * 		if unable to rename the messages file
	 */
	public void renameMsgsFile(String oldExpocode, String newExpocode) 
											throws IllegalArgumentException {
		File oldMsgsFile = cruiseMsgsFile(oldExpocode);
		if ( ! oldMsgsFile.exists() ) 
			return;

		File newMsgsFile = cruiseMsgsFile(newExpocode);
		if ( newMsgsFile.exists() )
			throw new IllegalArgumentException(
					"Messages file already exists for " + newExpocode);

		File newParent = newMsgsFile.getParentFile();
		if ( ! newParent.exists() ) 
			newParent.mkdirs();

		if ( ! oldMsgsFile.renameTo(newMsgsFile) ) 
			throw new IllegalArgumentException("Unable to rename messages "
					+ "file from " + oldExpocode + " to " + newExpocode);
	}

	/**
	 * Deletes the sanity checker messages file, if it exists, associated with a cruise.
	 * 
	 * @param expocode
	 * 		delete the messages file associated with the cruise with this expocode
	 * @return
	 * 		true if messages file exists and was deleted; 
	 * 		false if the messages file does not exist.
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid, or
	 * 		if the messages file exists but could not be deleted
	 */
	public boolean deleteMsgsFile(String expocode) throws IllegalArgumentException {
		File msgsFile = cruiseMsgsFile(expocode);
		if ( ! msgsFile.exists() )
			return false;
		if ( ! msgsFile.delete() ) {
			throw new IllegalArgumentException("Unable to delete " +
					"the sanity checker messages file for " + expocode);
		}
		return true;
	}

	/**
	 * Saves the list of messages produced by the SanityChecker to file.
	 * Clears and assigns the WOCE-3 or WOCE-4 flags for the given cruise from 
	 * the given SanityChecker output for the cruise as well as any user-provided 
	 * WOCE flags in the cruise data.  A row index may appear in multiple WOCE 
	 * sets, including both WOCE-3 and WOCE-4 sets.
	 * 
	 * @param cruiseData
	 * 		save messages for this cruise, and assign WOCE flags to this cruise
	 * @param output
	 * 		SanityChecker output for this cruise  
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 */
	public void saveCruiseMessages(DashboardCruiseWithData cruiseData, Output output) 
											throws IllegalArgumentException {
		// Directly modify the sets in the cruise data
		ArrayList<HashSet<Integer>> woceFourSets = cruiseData.getWoceFourRowIndices();
		ArrayList<HashSet<Integer>> woceThreeSets = cruiseData.getWoceThreeRowIndices();
		HashSet<Integer> noColumnWoceFourSet = cruiseData.getNoColumnWoceFourRowIndices();
		HashSet<Integer> noColumnWoceThreeSet = cruiseData.getNoColumnWoceThreeRowIndices();
		HashSet<Integer> userWoceFourSet = cruiseData.getUserWoceFourRowIndices();
		HashSet<Integer> userWoceThreeSet = cruiseData.getUserWoceThreeRowIndices();

		// Clear all WOCE flag sets
		for ( HashSet<Integer> rowIdxSet : woceFourSets )
			rowIdxSet.clear();
		for ( HashSet<Integer> rowIdxSet : woceThreeSets )
			rowIdxSet.clear();
		noColumnWoceFourSet.clear();
		noColumnWoceThreeSet.clear();
		userWoceFourSet.clear();
		userWoceThreeSet.clear();

		// Get the cruise messages file to be written
		File msgsFile = cruiseMsgsFile(cruiseData.getExpocode());

		// Create the NODC subdirectory if it does not exist
		File parentFile = msgsFile.getParentFile();
		if ( ! parentFile.exists() )
			parentFile.mkdirs();

		// Write the messages to file and save WOCE flags from these messages
		PrintWriter msgsWriter;
		try {
			msgsWriter = new PrintWriter(msgsFile);
		} catch (FileNotFoundException ex) {
			throw new IllegalArgumentException(
					"Unexpected error opening messages file " + 
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
				for ( Message msg : output.getMessages().getMessages() ) {
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

					int rowNum = msg.getLineNumber();
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

					int colNum = msg.getColumnIndex();
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

					// Assign the WOCE flag
					if ( rowNum > 0 ) {
						if ( msg.isError() ) {
							if ( colNum > 0 ) {
								woceFourSets.get(colNum - 1).add(rowNum - 1);
							}
							else {
								noColumnWoceFourSet.add(rowNum - 1);
							}
						}
						else if ( msg.isWarning() ) {
							if ( colNum > 0 ) {
								woceThreeSets.get(colNum - 1).add(rowNum - 1);
							}
							else {
								noColumnWoceThreeSet.add(rowNum - 1);
							}
						}
					}
				}
			} catch ( MessageException ex ) {
				throw new RuntimeException(ex);
			}

		} finally {
			msgsWriter.close();
		}

		// Assign any user-provided WOCE-3 and WOCE-4 flags
		ArrayList<DataColumnType> columnTypes = cruiseData.getDataColTypes();
		for (int k = 0; k < columnTypes.size(); k++) {
			DataColumnType colType = columnTypes.get(k);
			if ( ! ( colType.equals(DataColumnType.WOCE_CO2_WATER) ||
					 colType.equals(DataColumnType.WOCE_CO2_ATM) ) )
				continue;
			for (int rowIdx = 0; rowIdx < cruiseData.getNumDataRows(); rowIdx++) {
				try {
					int value = Integer.parseInt(cruiseData.getDataValues().get(rowIdx).get(k));
					if ( value == 4 )
						userWoceFourSet.add(rowIdx);
					else if ( value == 3 )
						userWoceThreeSet.add(rowIdx);
					// Only handle 3 and 4
				} catch (NumberFormatException ex) {
					// Assuming a missing value
				}
			}
		}

	}

	/**
	 * Reads the list of messages produced by the SanityChecker from the messages 
	 * file written by {@link #saveCruiseMessages(DashboardCruiseWithData, Output)}.
	 * 
	 * @param expocode
	 * 		get messages for the cruise with this expocode
	 * @return
	 * 		the sanity checker messages for the cruise;
	 * 		never null, but may be empty if there were no sanity
	 * 		checker messages for the cruise.
	 * 		The expocode, but not the username, will be assigned 
	 * 		in the returned SCMessageList
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid, or 
	 * 		if the messages file is invalid
	 * @throws FileNotFoundException
	 * 		if there is no messages file for the cruise
	 */
	public SCMessageList getCruiseMessages(String expocode) 
					throws IllegalArgumentException, FileNotFoundException {
		// Create the list of messages to be returned
		SCMessageList msgList = new SCMessageList();
		msgList.setExpocode(expocode);
		// Directly modify the summary messages in the SCMessageList
		ArrayList<String> summaryMsgs = msgList.getSummaries();
		// Read the cruise messages file
		File msgsFile = cruiseMsgsFile(expocode);
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

						SCMessage msg = new SCMessage();

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
			throw new IllegalArgumentException(
					"Unexpected problem reading messages from " + msgsFile.getPath() +
					"\n    " + ex.getMessage(), ex);
		}

		return msgList;
	}

	/**
	 *  For combining and ordering all WOCE flags 
	 */
	private class WoceInfo implements Comparable<WoceInfo> {
		Character flag;
		Integer columnIndex;
		String comment;
		Integer rowIndex;

		public WoceInfo() {
			flag = ' ';
			columnIndex = Integer.MAX_VALUE;
			comment = "";
			rowIndex = -1;
		}

		@Override
		public int compareTo(WoceInfo other) {
			int result = flag.compareTo(other.flag);
			if ( result != 0 )
				return result;
			result = columnIndex.compareTo(other.columnIndex);
			if ( result != 0 )
				return result;
			result = comment.toUpperCase().compareTo(other.comment.toUpperCase());
			if ( result != 0 )
				return result;
			result = rowIndex.compareTo(other.rowIndex);
			return result;
		}

		@Override
		public int hashCode() {
			final int prime = 37;
			int result = flag.hashCode();
			result = result * prime + columnIndex.hashCode();
			result = result * prime + comment.toUpperCase().hashCode();
			result = prime * result + rowIndex.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj )
				return true;
			if ( obj == null )
				return false;

			if ( ! ( obj instanceof WoceInfo ) )
				return false;
			WoceInfo other = (WoceInfo) obj;

			if ( ! flag.equals(other.flag) )
				return false;
			if ( ! columnIndex.equals(other.columnIndex) )
				return false;
			if ( ! comment.equalsIgnoreCase(other.comment) )
				return false;
			if ( ! rowIndex.equals(other.rowIndex) )
				return false;

			return true;
		}
	}

	/**
	 * Generates a list of SocatWoceEvents to to be submitted from the saved cruise
	 * messages as well as PI-provided WOCE flags.
	 * 
	 * @param cruiseData
	 * 		generate SocatWoceEvents for this cruise.  Uses the expocode, version,
	 * 		column types, user WOCE flags, and user WOCE comments from this object.
	 * 		SanityChecker cruise messages are read from the saved messages file for
	 * 		this cruise, and data is read from the saved full-data DSG file for this
	 * 		cruise. 
	 * @param dsgHandler
	 * 		DSG file handler to use to get the full-data DSG file for the cruise
	 * @return
	 * 		the list of SocatWoceEvents for the cruise; never null but may be empty
	 * @throws IllegalArgumentException
	 * 		if the expocode in cruiseData is invalid, or 
	 * 		if the messages file is invalid
	 * @throws FileNotFoundException
	 * 		if there is no messages file for the cruise, or
	 * 		if there is no full-data DSG file for the cruise
	 * @throws IOException
	 * 		if there is a problem opening or reading the full-data DSG file for the cruise
	 */
	public ArrayList<SocatWoceEvent> generateWoceEvents(DashboardCruiseWithData cruiseData, 
			DsgNcFileHandler dsgHandler) 
					throws IllegalArgumentException, FileNotFoundException, IOException {
		// Get the info needed from the DashboardCruise
		String version = cruiseData.getVersion();
		String expocode = cruiseData.getExpocode();
		ArrayList<DataColumnType> columnTypes = cruiseData.getDataColTypes();
		HashSet<Integer> userWoceThrees = cruiseData.getUserWoceThreeRowIndices();
		HashSet<Integer> userWoceFours = cruiseData.getUserWoceFourRowIndices();
		int userCommentsIndex = -1;
		for (int k = 0; k < columnTypes.size(); k++) {
			DataColumnType type = columnTypes.get(k);
			// Want COMMENT_WOCE_CO2_WATER, but if not given accept COMMENT_WOCE_CO2_ATM
			if ( type.equals(DataColumnType.COMMENT_WOCE_CO2_WATER) ) {
				userCommentsIndex = k;
				break;
			}
			if ( type.equals(DataColumnType.COMMENT_WOCE_CO2_ATM) ) {
				userCommentsIndex = k;
			}
		}
		ArrayList<ArrayList<String>> dataVals = cruiseData.getDataValues();

		// Get the SanityChecker messages
		SCMessageList msgList = getCruiseMessages(expocode);

		TreeSet<WoceInfo> orderedWoceInfo = new TreeSet<WoceInfo>();
		for ( SCMessage msg : msgList ) {

			SCMsgSeverity severity = msg.getSeverity();
			if ( severity.equals(SCMsgSeverity.UNKNOWN) )
				continue;

			int rowNum = msg.getRowNumber();
			if ( rowNum <= 0 )
				continue;

			// if no specific column associated with this message, the column number is -1
			int colNum = msg.getColNumber();
			if ( colNum == 0 )
				continue;

			WoceInfo info = new WoceInfo();

			if ( severity.equals(SCMsgSeverity.ERROR) )
				info.flag = SocatWoceEvent.WOCE_BAD;
			else if ( severity.equals(SCMsgSeverity.WARNING) )
				info.flag = SocatWoceEvent.WOCE_QUESTIONABLE;
			else
				throw new RuntimeException("Unexpected message severity of " + severity.toString());
			// TODO: Only add SanityChecker WOCE-4 flags for now 
			// (here and at the end of CruiseChecker.standardizeCruiseData)
			if ( ! info.flag.equals(SocatWoceEvent.WOCE_BAD) )
				continue;

			if ( colNum > 0 )
				info.columnIndex = colNum - 1;
			// if no column index, leave as Integer.MAX_VALUE to put them last for this flag

			// Get the general explanation for the WOCE flag
			info.comment = msg.getGeneralComment();
			if ( info.comment.isEmpty() )
				info.comment = msg.getDetailedComment();

			info.rowIndex = rowNum - 1;

			orderedWoceInfo.add(info);
		}

		// Add any PI WOCE-3 flags 
		for ( Integer rowIdx : userWoceThrees ) {
			WoceInfo info = new WoceInfo();
			info.flag = SocatWoceEvent.WOCE_QUESTIONABLE;
			// leave columnIndex as Integer.MAX_VALUE to put them last for this flag
			// add ZZZZ to make these the last comments for the flag/column
			info.comment = "ZZZZ " + SocatWoceEvent.PI_PROVIDED_WOCE_COMMENT_START + "3 flag";
			if ( userCommentsIndex >= 0 ) {
				String addnMsg = dataVals.get(rowIdx).get(userCommentsIndex);
				if ( ! addnMsg.isEmpty() )
					info.comment += " with comment/subflag: " + addnMsg;
			}
			info.rowIndex = rowIdx;
			orderedWoceInfo.add(info);
		}

		// Add any PI WOCE-4 flags 
		for ( Integer rowIdx : userWoceFours ) {
			WoceInfo info = new WoceInfo();
			info.flag = SocatWoceEvent.WOCE_BAD;
			// leave columnIndex as Integer.MAX_VALUE to put them last for this flag
			// add ZZZZ to make these the last comments for the flag/column
			info.comment = "ZZZZ " + SocatWoceEvent.PI_PROVIDED_WOCE_COMMENT_START + "4 flag";
			if ( userCommentsIndex >= 0 ) {
				String addnMsg = dataVals.get(rowIdx).get(userCommentsIndex);
				if ( ! addnMsg.isEmpty() )
					info.comment += " with comment/subflag: " + addnMsg;
			}
			info.rowIndex = rowIdx;
			orderedWoceInfo.add(info);
		}

		ArrayList<SocatWoceEvent> woceList = new ArrayList<SocatWoceEvent>();
		// If no WOCE flags, return now before we read data from the DSG file
		if ( orderedWoceInfo.isEmpty() )
			return woceList;

		// Get the longitudes, latitude, times, and regions IDs 
		// from the full-data DSG file for this cruise
		double[] longitudes = dsgHandler.readDoubleVarDataValues(expocode, 
				Constants.SHORT_NAMES.get(Constants.longitude_VARNAME));
		double[] latitudes = dsgHandler.readDoubleVarDataValues(expocode, 
				Constants.SHORT_NAMES.get(Constants.latitude_VARNAME));
		double[] times = dsgHandler.readDoubleVarDataValues(expocode, 
				Constants.SHORT_NAMES.get(Constants.time_VARNAME));
		char[] regionIDs = dsgHandler.readCharVarDataValues(expocode, 
				Constants.SHORT_NAMES.get(Constants.regionID_VARNAME));
		Date now = new Date();

		Character lastFlag = null;
		Integer lastColumnIndex = null;
		String lastComment = null;
		double[] dataValues = null;
		String lastDataVarName = null;
		ArrayList<DataLocation> locations = null;
		for ( WoceInfo info : orderedWoceInfo ) {

			// Check if a new WOCE event is needed
			if ( ( ! info.flag.equals(lastFlag) ) ||
				 ( ! info.columnIndex.equals(lastColumnIndex) ) ||
				 ( ! info.comment.equals(lastComment) ) ) {
				lastFlag = info.flag;
				lastColumnIndex = info.columnIndex;
				lastComment = info.comment;

				SocatWoceEvent woceEvent = new SocatWoceEvent();
				woceEvent.setExpocode(expocode);
				woceEvent.setSocatVersion(version);
				woceEvent.setFlag(info.flag);
				woceEvent.setFlagDate(now);
				woceEvent.setUsername(SocatEvent.SANITY_CHECKER_USERNAME);
				woceEvent.setRealname(SocatEvent.SANITY_CHECKER_REALNAME);
				// Remove the ZZZZ added to the PI-provided WOCE flag comment
				if ( info.comment.startsWith("ZZZZ " + SocatWoceEvent.PI_PROVIDED_WOCE_COMMENT_START) )
					woceEvent.setComment(info.comment.substring(5));
				else
					woceEvent.setComment(info.comment);

				// If a column can be identified, assign its name and 
				// get its values if we do not already have them 
				if ( info.columnIndex != Integer.MAX_VALUE ) {
					DataColumnType dataType = columnTypes.get(info.columnIndex);
					String dataVarName = Constants.TYPE_TO_VARNAME_MAP.get(dataType);
					if ( dataVarName != null ) {
						woceEvent.setDataVarName(dataVarName);
						if ( ! dataVarName.equals(lastDataVarName) ) {
							dataValues = dsgHandler.readDoubleVarDataValues(expocode, dataVarName);
							lastDataVarName = dataVarName;
						}
					}
				}

				// Directly modify the locations ArrayList in this object
				locations = woceEvent.getLocations();
				woceList.add(woceEvent);
			}

			// Add a location for the current WOCE event
			DataLocation dataLoc = new DataLocation();
			dataLoc.setRowNumber(info.rowIndex + 1);
			dataLoc.setDataDate(new Date(Math.round(times[info.rowIndex] * 1000.0)));
			dataLoc.setLatitude(latitudes[info.rowIndex]);
			dataLoc.setLongitude(longitudes[info.rowIndex]);
			dataLoc.setRegionID(regionIDs[info.rowIndex]);
			if ( info.columnIndex != Integer.MAX_VALUE )
				dataLoc.setDataValue(dataValues[info.rowIndex]);
			locations.add(dataLoc);
		}

		return woceList;
	}

}
