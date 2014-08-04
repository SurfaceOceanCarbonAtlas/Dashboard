/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage.SCMsgSeverity;
import gov.noaa.pmel.socat.dashboard.shared.SCMessage.SCMsgType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import uk.ac.uea.socat.sanitychecker.Message;
import uk.ac.uea.socat.sanitychecker.Output;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;

/**
 * Processes SanityChecker messages for a cruise.
 * 
 * @author Karl Smith
 */
public class CheckerMessageHandler {

	private static final String CRUISE_MSGS_FILENAME_EXTENSION = ".messages";
	private static final String SCMSG_KEY_VALUE_SEP = ":";
	private static final String SCMSG_TYPE_KEY = "SCMsgType";
	private static final String SCMSG_SEVERITY_KEY = "SCMsgSeverity";
	private static final String SCMSG_ROW_NUMBER_KEY = "SCMsgRowNumber";
	private static final String SCMSG_LONGITUDE_KEY = "SCMsgLongitude";
	private static final String SCMSG_LATITUDE_KEY = "SCMsgLatitude";
	private static final String SCMSG_TIMESTAMP_KEY = "SCMsgTimestamp";
	private static final String SCMSG_COLUMN_NUMBER_KEY = "SCMsgColumnNumber";
	private static final String SCMSG_COLUMN_NAME_KEY = "SCMsgColumnName";
	private static final String SCMSG_MESSAGE_KEY = "SCMsgMessage";
	private static final DateTimeFormatter DATETIME_FORMATTER = 
			DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");

	private static final Set<Entry<String,SCMsgType>> MSG_FRAGMENT_TO_TYPE_SET;
	static {
		HashMap<String,SCMsgType> msgFragsToTypes = new HashMap<String,SCMsgType>();
		// value outside range (warning)
		msgFragsToTypes.put("expected range", SCMsgType.DATA_RANGE);
		// value outside range (error)
		msgFragsToTypes.put("extreme range", SCMsgType.DATA_RANGE);
		// data point times out of order
		msgFragsToTypes.put("timestamp", SCMsgType.DATA_TIME);
		// excessive speed between data points
		msgFragsToTypes.put("Ship speed", SCMsgType.DATA_SPEED);
		// missing value for required data value
		msgFragsToTypes.put("Missing required value", SCMsgType.DATA_MISSING);
		// values constant over some number of data points
		msgFragsToTypes.put("constant for", SCMsgType.DATA_CONSTANT);
		// data value markedly different from values in previous and subsequent data points
		msgFragsToTypes.put("standard deviations", SCMsgType.DATA_JUMP);
		// excessive time gap between successive data points
		msgFragsToTypes.put("days apart", SCMsgType.DATA_GAP);
		// unexpected exception not handled
		msgFragsToTypes.put("Unhandled exception", SCMsgType.DATA_ERROR);
		MSG_FRAGMENT_TO_TYPE_SET = msgFragsToTypes.entrySet();
	}

	private File filesDir;

	CheckerMessageHandler(String filesDirName) {
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
	 * 
	 * @param expocode
	 * 		expocode of the cruise
	 * @param output
	 * 		output object returned from checking the cruise by the SanityChecker
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 */
	public void saveCruiseMessages(String expocode, Output output) 
											throws IllegalArgumentException {
		// Get the cruise messages file
		File msgsFile = cruiseMsgsFile(expocode);
		// The parent directory should exist when this method gets called
		// Write the messages to file
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
			for ( Message msg : output.getMessages().getMessages() ) {
				// Generate a list of key-value strings describing this message
				ArrayList<String> mappings = new ArrayList<String>();

				// Message string should never be null
				String checkerMsg = msg.getMessage();

				int intVal = msg.getMessageType();
				if ( intVal == Message.DATA_MESSAGE ) {
					// Determine the error/warning type from the message itself
					SCMsgType type = SCMsgType.UNKNOWN;
					for ( Entry<String, SCMsgType> fragEntry : MSG_FRAGMENT_TO_TYPE_SET ) {
						if ( checkerMsg.contains(fragEntry.getKey()) ) {
							if ( type != SCMsgType.UNKNOWN )
								throw new IllegalArgumentException(
										"More than one message type (" + type.toString() + 
										" and " + fragEntry.getValue().toString() + 
										") associated with the data check message\n    " + 
										checkerMsg);
							type = fragEntry.getValue();
						}
					}
					if ( type.equals(SCMsgType.UNKNOWN) )
						throw new IllegalArgumentException("No message type found " +
								"for the data check message\n    " + checkerMsg);
					mappings.add(SCMSG_TYPE_KEY + SCMSG_KEY_VALUE_SEP + 
							type.name());
				}
				else if ( intVal == Message.METADATA_MESSAGE ) {
					mappings.add(SCMSG_TYPE_KEY + SCMSG_KEY_VALUE_SEP + 
							SCMsgType.METADATA.name());
				}

				intVal = msg.getSeverity();
				if ( intVal == Message.ERROR ) {
					mappings.add(SCMSG_SEVERITY_KEY + SCMSG_KEY_VALUE_SEP + 
							SCMsgSeverity.ERROR.name());
				}
				else if ( intVal == Message.WARNING ) {
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

				intVal = msg.getInputItemIndex();
				if ( intVal > 0 )
					mappings.add(SCMSG_COLUMN_NUMBER_KEY + SCMSG_KEY_VALUE_SEP + 
							Integer.toString(intVal));

				String strVal = msg.getInputItemName();
				if ( strVal != null )
					mappings.add(SCMSG_COLUMN_NAME_KEY + SCMSG_KEY_VALUE_SEP + strVal);

				// Escape all newlines in the message string when saving it
				strVal = checkerMsg.replace("\n", "\\n");
				mappings.add(SCMSG_MESSAGE_KEY + SCMSG_KEY_VALUE_SEP + strVal);

				// Write this array list of key-value string to file
				msgsWriter.println(DashboardUtils.encodeStringArrayList(mappings));
			}
		} finally {
			msgsWriter.close();
		}
	}

	/**
	 * Reads the list of messages produced by the SanityChecker from the messages 
	 * file written by {@link #saveCruiseMessages(String, Output)}.
	 * 
	 * @param expocode
	 * 		get messages for the cruise with this expocode
	 * @return
	 * 		the list of sanity checker messages for the cruise;
	 * 		never null, but may be empty if there were no sanity
	 * 		checker messages for the cruise.
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid, or 
	 * 		if the messages file is invalid
	 * @throws FileNotFoundException
	 * 		if there is no messages file for the cruise
	 */
	public ArrayList<SCMessage> getCruiseMessages(String expocode) 
					throws IllegalArgumentException, FileNotFoundException {
		// Create the list of messages to be returned
		ArrayList<SCMessage> msgList = new ArrayList<SCMessage>();
		// Read the cruise messages file
		File msgsFile = cruiseMsgsFile(expocode);
		BufferedReader msgReader;
		msgReader = new BufferedReader(new FileReader(msgsFile));
		try {
			try {
				String msgline = msgReader.readLine();
				while ( msgline != null ) {
					if ( ! msgline.trim().isEmpty() ) {

						Properties msgProps = new Properties();
						for ( String msgPart : DashboardUtils.decodeStringArrayList(msgline) ) {
							String[] keyValue = msgPart.split(SCMSG_KEY_VALUE_SEP, 2);
							if ( keyValue.length != 2 )
								throw new IOException("Invalid key:value pair '" + msgPart + "'");
							msgProps.setProperty(keyValue[0], keyValue[1]);
						}

						SCMessage msg = new SCMessage();

						String propVal = msgProps.getProperty(SCMSG_TYPE_KEY);
						try {
							msg.setType(SCMsgType.valueOf(propVal));
						} catch ( Exception ex ) {
							// leave as the default SCMsgType.UNKNOWN
						}

						propVal = msgProps.getProperty(SCMSG_SEVERITY_KEY);
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

						propVal = msgProps.getProperty(SCMSG_MESSAGE_KEY);
						if ( propVal != null ) {
							// Replace all escaped newlines in the message string
							propVal = propVal.replace("\\n", "\n");
							msg.setExplanation(propVal);
						}
						// default explanation is an empty string

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
	 * Clears and assigns the WOCE-3 or WOCE-4 flags for the given cruise from 
	 * the given SanityChecker output for the cruise as well as any user-provided 
	 * WOCE flags in the cruise data.  A row index may appear in multiple WOCE 
	 * sets, including both WOCE-3 and WOCE-4 sets.
	 * 
	 * @param cruise
	 * 		assign WOCE flags for this cruise
	 * @param checkerOutput
	 * 		SanityChecker output for this cruise  
	 */
	public void assignWoceFlags(DashboardCruiseWithData cruiseData, Output checkerOutput) {
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

		// Assign WOCE flags from the SanityChecker output
		for ( Message msg : checkerOutput.getMessages().getMessages() ) {
			int rowIdx = msg.getLineNumber();
			if ( (rowIdx <= 0) || (rowIdx > cruiseData.getNumDataRows()) )
				throw new RuntimeException("Unexpected row number of " + 
						Integer.toString(rowIdx) + " in the sanity checker message\n" +
						"    " + msg.toString());
			// Change row number to row index
			rowIdx--;

			int colIdx = msg.getInputItemIndex();
			if ( (colIdx == 0) || (colIdx > cruiseData.getDataColTypes().size()) )
				throw new RuntimeException("Unexpected input column number of " + 
						Integer.toString(colIdx) + " in the sanity checker message\n" +
						"    " + msg.toString());
			// Change column number to column index; 
			// negative numbers indicate an ambiguous source of error
			if ( colIdx > 0 )
				colIdx--;

			if ( msg.isError() ) {
				if ( colIdx < 0 )
					noColumnWoceFourSet.add(rowIdx);
				else
					woceFourSets.get(colIdx).add(rowIdx);
			}
			else if ( msg.isWarning() ) {
				if ( colIdx < 0 )
					noColumnWoceThreeSet.add(rowIdx);
				else
					woceThreeSets.get(colIdx).add(rowIdx);
			}
			else {
				// Should never happen
				throw new RuntimeException(
						"Unexpected message that is neither an error nor a warning:\n" +
						"    " + msg.toString());
			}
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

}
