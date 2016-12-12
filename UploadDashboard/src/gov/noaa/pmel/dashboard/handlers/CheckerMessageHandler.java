/**
 * 
 */
package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.DataLocation;
import gov.noaa.pmel.dashboard.shared.SCMessage;
import gov.noaa.pmel.dashboard.shared.SCMessage.SCMsgSeverity;
import gov.noaa.pmel.dashboard.shared.SCMessageList;
import gov.noaa.pmel.dashboard.shared.DataQCEvent;
import gov.noaa.pmel.dashboard.shared.QCFlag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
	 * @param dataset
	 * 		dataset of the cruise
	 * @return
	 * 		the cruise messages file associated with the cruise
	 * @throws IllegalArgumentException
	 * 		if the cruise dataset is invalid
	 */
	private File cruiseMsgsFile(String expocode) throws IllegalArgumentException {
		// Check that the dataset is somewhat reasonable
		String upperExpo = DashboardServerUtils.checkDatasetID(expocode);
		// Get the name of the cruise messages file
		return new File(filesDir, upperExpo.substring(0,4) + 
				File.separatorChar + upperExpo + CRUISE_MSGS_FILENAME_EXTENSION);
	}

	/**
	 * Appropriately renames a cruise messages file, if one exists, 
	 * for a change in cruise dataset.
	 * 
	 * @param oldExpocode
	 * 		standardized old dataset of the cruise
	 * @param newExpocode
	 * 		standardized new dataset for the cruise
	 * @throws IllegalArgumentException
	 * 		if a messages file for the new dataset already exists, or
	 * 		if unable to rename the messages file
	 */
	public void renameMsgsFile(String oldExpocode, String newExpocode) throws IllegalArgumentException {
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
	 * @param dataset
	 * 		delete the messages file associated with the cruise with this dataset
	 * @return
	 * 		true if messages file exists and was deleted; 
	 * 		false if the messages file does not exist.
	 * @throws IllegalArgumentException
	 * 		if the dataset is invalid, or
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
	 * Processes the list of messages produced by the SanityChecker.
	 * Saves the messages to the appropriate messages file.
	 * Clears and assigns the WOCE-3 or WOCE-4 flags for the given cruise 
	 * from the given SanityChecker output for the cruise as well as any 
	 * user-provided WOCE flags in the cruise data.
	 * 
	 * @param datasetData
	 * 		process messages for this cruise
	 * @param output
	 * 		SanityChecker output for this cruise
	 * @throws IllegalArgumentException
	 * 		if the dataset is invalid
	 */
	public void processCruiseMessages(DashboardDatasetData cruiseData, 
			Output output) throws IllegalArgumentException {

		TreeSet<WoceType> woceFours = new TreeSet<WoceType>();
		TreeSet<WoceType> woceThrees = new TreeSet<WoceType>();

		// Get the cruise messages file to be written
		File msgsFile = cruiseMsgsFile(cruiseData.getDatasetId());
		// Create the parent subdirectories if they do not exist
		File parentFile = msgsFile.getParentFile();
		if ( ! parentFile.exists() )
			parentFile.mkdirs();
		// Write the messages to file and save WOCE flags from these messages
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

					// Create the WOCE flag for this message.
					// TODO: Assign the correct WOCE name
					if ( rowNum > 0 ) {
						if ( msg.isError() ) {
							if ( colNum > 0 ) {
								woceFours.add(new WoceType(DashboardServerUtils.GENERIC_WOCE_FLAG.getVarName(), colNum-1, rowNum-1));
							}
							else {
								woceFours.add(new WoceType(DashboardServerUtils.GENERIC_WOCE_FLAG.getVarName(), null, rowNum-1));
							}
						}
						else if ( msg.isWarning() ) {
							if ( colNum > 0 ) {
								woceThrees.add(new WoceType(DashboardServerUtils.GENERIC_WOCE_FLAG.getVarName(), colNum-1, rowNum-1));
							}
							else {
								woceThrees.add(new WoceType(DashboardServerUtils.GENERIC_WOCE_FLAG.getVarName(), null, rowNum-1));
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

		cruiseData.setCheckerWoceFours(woceFours);
		cruiseData.setCheckerWoceThrees(woceThrees);

		woceFours.clear();
		woceThrees.clear();

		// Assign any user-provided WOCE-3 and WOCE-4 flags
		ArrayList<DataColumnType> columnTypes = cruiseData.getDataColTypes();
		for (int k = 0; k < columnTypes.size(); k++) {
			DataColumnType colType = columnTypes.get(k);
			if ( ! colType.isWoceType() )
				continue;
			for (int rowIdx = 0; rowIdx < cruiseData.getNumDataRows(); rowIdx++) {
				try {
					int value = Integer.parseInt(cruiseData.getDataValues().get(rowIdx).get(k));
					if ( value == 4 )
						woceFours.add(new WoceType(colType.getVarName(), null, rowIdx));
					else if ( value == 3 )
						woceThrees.add(new WoceType(colType.getVarName(), null, rowIdx));
					// Only handle 3 and 4
				} catch (NumberFormatException ex) {
					// Assuming a missing value
				}
			}
		}

		cruiseData.setUserWoceFours(woceFours);
		cruiseData.setUserWoceThrees(woceThrees);

		woceFours.clear();
		woceThrees.clear();
	}

	/**
	 * Reads the list of messages produced by the SanityChecker from the messages 
	 * file written by {@link #processCruiseMessages(DashboardDatasetData, Output)}.
	 * 
	 * @param dataset
	 * 		get messages for the cruise with this dataset
	 * @return
	 * 		the sanity checker messages for the cruise;
	 * 		never null, but may be empty if there were no sanity
	 * 		checker messages for the cruise.
	 * 		The dataset, but not the username, will be assigned 
	 * 		in the returned SCMessageList
	 * @throws IllegalArgumentException
	 * 		if the dataset is invalid, or 
	 * 		if the messages file is invalid
	 * @throws FileNotFoundException
	 * 		if there is no messages file for the cruise
	 */
	public SCMessageList getCruiseMessages(String expocode) 
			throws IllegalArgumentException, FileNotFoundException {
		// Create the list of messages to be returned
		SCMessageList msgList = new SCMessageList();
		msgList.setDatasetId(expocode);
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
	 * Generates a list of SocatWoceEvents to to be submitted from the saved cruise
	 * messages as well as PI-provided WOCE flags.
	 * 
	 * @param datasetData
	 * 		generate SocatWoceEvents for this cruise.  Uses the dataset, version,
	 * 		column types, user WOCE flags, and user WOCE comments from this object.
	 * 		SanityChecker cruise messages are read from the saved messages file for
	 * 		this cruise, and data is read from the saved full-data DSG file for this
	 * 		cruise. 
	 * @param dsgHandler
	 * 		DSG file handler to use to get the full-data DSG file for the cruise
	 * @param knowndDataFileTypes
	 * 		known types for data files
	 * @return
	 * 		the list of SocatWoceEvents for the cruise; never null but may be empty
	 * @throws IllegalArgumentException
	 * 		if the dataset in datasetData is invalid, or 
	 * 		if the messages file is invalid
	 * @throws FileNotFoundException
	 * 		if there is no messages file for the cruise, or
	 * 		if there is no full-data DSG file for the cruise
	 * @throws IOException
	 * 		if there is a problem opening or reading the full-data DSG file for the cruise
	 */
	public ArrayList<DataQCEvent> generateWoceEvents(DashboardDatasetData cruiseData, 
			DsgNcFileHandler dsgHandler, KnownDataTypes knownDataFileTypes) 
					throws IllegalArgumentException, FileNotFoundException, IOException {
		// Ordered set of all WOCE flags for this dataset
		TreeSet<QCFlag> woceFlagSet = new TreeSet<QCFlag>();

		// Create the flags from the SanityChecker messages
		String expocode = cruiseData.getDatasetId();
		for ( SCMessage msg : getCruiseMessages(expocode) ) {

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

			// TODO: get the correct WOCE flag name
			QCFlag info = new QCFlag(DashboardServerUtils.GENERIC_WOCE_FLAG.getVarName(), null, rowNum-1);
			if ( colNum > 0 )
				info.setColumnIndex(colNum-1);

			if ( severity.equals(SCMsgSeverity.ERROR) )
				info.setFlagValue(DashboardUtils.WOCE_BAD);
			else if ( severity.equals(SCMsgSeverity.WARNING) )
				info.setFlagValue(DashboardUtils.WOCE_QUESTIONABLE);
			else
				throw new RuntimeException("Unexpected message severity of " + severity.toString());

			String comment = msg.getGeneralComment();
			if ( comment.isEmpty() )
				comment = msg.getDetailedComment();
			info.setComment(comment);

			// Only add SanityChecker WOCE-4 flags; the SanityChecker marks all 
			// questionable data regardless of whether it is of consequence.
			if ( DashboardUtils.WOCE_BAD.equals(info.getFlagValue()) )
				woceFlagSet.add(info);
		}

		ArrayList<DataColumnType> columnTypes = cruiseData.getDataColTypes();

		TreeSet<WoceType> userWoceThrees = cruiseData.getUserWoceThrees();
		TreeSet<WoceType> userWoceFours = cruiseData.getUserWoceFours();
		if ( (userWoceThrees.size() > 0) || (userWoceFours.size() > 0) ) {

			HashMap<String,Integer> woceCommentIndex = new HashMap<String,Integer>();
			for ( DataColumnType colType : columnTypes ) {
				if ( colType.isWoceType() ) {
					for (int k = 0; k < columnTypes.size(); k++) {
						if ( columnTypes.get(k).isWoceCommentFor(colType) ) {
							woceCommentIndex.put(colType.getVarName(), Integer.valueOf(k));
						}
					}
				}
			}

			// Need the data values for any WOCE comments 
			ArrayList<ArrayList<String>> dataVals = cruiseData.getDataValues();

			// Add any PI WOCE-3 flags 
			for ( WoceType uwoce : userWoceThrees ) {
				String woceName = uwoce.getWoceName();
				// Use Integer.MAX_VALUE as the column index to put these at the end
				QCFlag info = new QCFlag(woceName, Integer.MAX_VALUE, uwoce.getRowIndex());
				info.setFlagValue(DashboardUtils.WOCE_QUESTIONABLE);
				String comment = DashboardUtils.PI_PROVIDED_WOCE_COMMENT_START + "3 flag";
				Integer idx = woceCommentIndex.get(woceName);
				if ( idx != null ) {
					comment += " with comment/subflag: " + dataVals.get(uwoce.getRowIndex()).get(idx);
				}
				info.setComment(comment);
				woceFlagSet.add(info);
			}

			// Add any PI WOCE-4 flags 
			for ( WoceType uwoce : userWoceFours ) {
				String woceName = uwoce.getWoceName();
				// Use Integer.MAX_VALUE as the column index to put these at the end
				QCFlag info = new QCFlag(woceName, Integer.MAX_VALUE, uwoce.getRowIndex());
				info.setFlagValue(DashboardUtils.WOCE_BAD);
				String comment = DashboardUtils.PI_PROVIDED_WOCE_COMMENT_START + "4 flag";
				Integer idx = woceCommentIndex.get(woceName);
				if ( idx != null ) {
					comment += " with comment/subflag: " + dataVals.get(uwoce.getRowIndex()).get(idx);
				}
				info.setComment(comment);
				woceFlagSet.add(info);
			}
		}

		ArrayList<DataQCEvent> woceList = new ArrayList<DataQCEvent>();
		// If no WOCE flags, return now before we read data from the DSG file
		if ( woceFlagSet.isEmpty() )
			return woceList;

		String version = cruiseData.getVersion();

		// Get the longitudes, latitudes, depths, and times 
		// from the full-data DSG file for this cruise
		double[][] lonlattime = dsgHandler.readLonLatTimeDataValues(expocode);
		double[] longitudes = lonlattime[0];
		double[] latitudes = lonlattime[1];
		double[] times = lonlattime[2];
		Date now = new Date();

		String lastWoceName = null;
		Character lastFlag = null;
		Integer lastColIdx = null;
		String lastComment = null;
		double[] dataValues = null;
		String lastDataVarName = null;
		String dataVarName = null;
		ArrayList<DataLocation> locations = null;
		for ( QCFlag info : woceFlagSet ) {

			// Check if a new WOCE event is needed
			String woceName = info.getFlagName();
			Character flag = info.getFlagValue();
			Integer colIdx = info.getColumnIndex();
			String comment = info.getComment();
			if ( ( ! woceName.equals(lastWoceName) ) ||
				 ( ! flag.equals(lastFlag) ) ||
				 ( ! colIdx.equals(lastColIdx) ) ||
				 ( ! comment.equals(lastComment) ) ) {
				lastWoceName = woceName;
				lastFlag = flag;
				lastColIdx = colIdx;
				lastComment = comment;

				DataQCEvent woceEvent = new DataQCEvent();
				woceEvent.setFlagName(woceName);
				woceEvent.setDatasetId(expocode);
				woceEvent.setVersion(version);
				woceEvent.setFlagValue(flag);
				woceEvent.setFlagDate(now);
				woceEvent.setUsername(DashboardUtils.SANITY_CHECKER_USERNAME);
				woceEvent.setRealname(DashboardUtils.SANITY_CHECKER_REALNAME);
				woceEvent.setComment(comment);

				// If a column can be identified, assign its name and 
				// get its values if we do not already have them 
				dataVarName = null;
				if ( (colIdx >= 0) && (colIdx != Integer.MAX_VALUE) ) {
					DataColumnType dataType = columnTypes.get(colIdx);
					// Geoposition is a problem in the combination of lon/lat/time, so no data assignment
					if ( DashboardServerUtils.GEOPOSITION.typeNameEquals(dataType) ) {
						dataVarName = null;
					}
					// Associate all time-related data columns with the time file variable
					else if ( DashboardServerUtils.TIMESTAMP.typeNameEquals(dataType) ||
							  DashboardServerUtils.DATE.typeNameEquals(dataType) ||
							  DashboardServerUtils.YEAR.typeNameEquals(dataType) ||
							  DashboardServerUtils.MONTH_OF_YEAR.typeNameEquals(dataType) ||
							  DashboardServerUtils.DAY_OF_MONTH.typeNameEquals(dataType) ||
							  DashboardServerUtils.TIME_OF_DAY.typeNameEquals(dataType) ||
							  DashboardServerUtils.HOUR_OF_DAY.typeNameEquals(dataType) ||
							  DashboardServerUtils.MINUTE_OF_HOUR.typeNameEquals(dataType) ||
							  DashboardServerUtils.SECOND_OF_MINUTE.typeNameEquals(dataType) ||
							  DashboardServerUtils.DAY_OF_YEAR.typeNameEquals(dataType) ||
							  DashboardServerUtils.SECOND_OF_DAY.typeNameEquals(dataType) ) {
						dataVarName = DashboardServerUtils.TIME.getVarName();
					}
					// Check if this type is known in the data file types
					else if ( knownDataFileTypes.getDataColumnType(dataType.getVarName()) == null ) {
						dataVarName = null;
					}
					else {
						dataVarName = dataType.getVarName();
					}
					if ( dataVarName != null ) {
						if ( ! dataVarName.equals(lastDataVarName) ) {
							// This should always succeed; but just in case ....
							try {
								dataValues = dsgHandler.readDoubleVarDataValues(expocode, dataVarName);
								lastDataVarName = dataVarName;
							} catch ( IllegalArgumentException ex ) {
								dataVarName = null;
							}
						}
					}
					if ( dataVarName != null ) {
						woceEvent.setVarName(dataVarName);
					}
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
