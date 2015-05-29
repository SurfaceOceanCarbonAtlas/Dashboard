package uk.ac.uea.socat.sanitychecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import uk.ac.uea.socat.metadata.OmeMetadata.BadEntryNameException;
import uk.ac.uea.socat.metadata.OmeMetadata.InvalidConflictException;
import uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadata;
import uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadataException;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.messages.Message;
import uk.ac.uea.socat.sanitychecker.messages.MessageException;
import uk.ac.uea.socat.sanitychecker.messages.Messages;

/**
 * Class to hold the complete output of the Sanity Checker from processing
 * a single data file. Contains a result code, data, metadata and any generated messages. 
 */
public class Output {
	
	/**
	 * Exit code flag indicating that no errors or warnings occurred.
	 * This is the default exit code if everything works smoothly.
	 * 
	 * This code is also used by some functions to indicate a NO_ERROR state.
	 */
	public static final int NO_ERROR_FLAG = 0;
	
	/**
	 * Exit code flag indicating that no output files were generated from
	 * processing the input file.
	 */
	public static final int NO_OUTPUT_FLAG = 1;

	/**
	 * Exit code flag indicating that warnings were generated while processing
	 * the input file.
	 */
	public static final int WARNINGS_FLAG = 1 << 1;

	/**
	 * Exit code flag indicating that errors were generated while processing the
	 * input file.
	 */
	public static final int ERRORS_FLAG = 1 << 2;

	/**
	 * Exit code flag indicating that the input data supplied to the Sanity Checker was
	 * invalid.
	 */
	public static final int INVALID_INPUT_FLAG = 1 << 3;

	/**
	 * Exit code flag indicating that an internal error occurred.
	 */
	public static final int INTERNAL_ERROR_FLAG = 1 << 4;
	
	/**
	 * The result code from processing a file.
	 */
	private int itsResultCode;
	
	/**
	 * String representation of the metadata from the top
	 * of the file. This can be expanded with new values.
	 */
	private OmeMetadata itsMetadata;
	
	/**
	 * The list of data records
	 */
	private List<SocatDataRecord> itsDataRecords;
	
	/**
	 * The set of output messages
	 */
	private Messages itsMessages;
	
	/**
	 * The original filename to which this object pertains. This is a copy
	 * of the name that was originally passed in to the Sanity Checker
	 */
	private String itsFilename;
	
	private Logger itsLogger;
	
	/**
	 * Constructor for the output object
	 * @param filename The filename of the original data file
	 */
	protected Output(String filename, OmeMetadata metadata, int dataLength, ColumnSpec columnSpec, Logger logger) {
		// Initialize result code to zero since flags can only be mixed in
		itsResultCode = 0;
		itsFilename = filename;
		itsMetadata = metadata;
		itsDataRecords = new ArrayList<SocatDataRecord>(dataLength);
		itsMessages = new Messages(columnSpec);
		
		itsLogger = logger;
		
		// Always set the metadata to draft - this will remain in place until
		// the MetadataChecker is complete.
		itsMetadata.setDraft(true);
	}
	
	/**
	 * Returns the name of the original data file
	 * @return The name of the original data file
	 */
	public String getFilename() {
		return itsFilename;
	}
	
	/**
	 * Returns the numeric result code from processing the file.
	 * This can be used in conjunction with the flag constants to
	 * determine full information about the processing result.
	 * @return The numeric result code.
	 */
	public int getResultCode() {
		return itsResultCode;
	}
	
	/**
	 * Indicates whether or not the file has been successfully processed.
	 * @return {@code true} If processing was successful; {@code false} if the processing failed.
	 */
	public boolean processedOK() {
		if ((itsResultCode & INVALID_INPUT_FLAG) > 0 || (itsResultCode & INTERNAL_ERROR_FLAG) > 0) {
			return false;
		}	

		return true;
	}
	
	/**
	 * Indicates whether or not the processing generated any error messages.
	 * @return {@code true} if error messages were generated; {@code false} if no errors were generated.
	 */
	public boolean hasErrors() {
		return (itsResultCode & ERRORS_FLAG) > 0;
	}
	
	/**
	 * Indicates whether or not the processing generated any warning messages.
	 * @return {@code true} if warning messages were generated; {@code false} if no warnings were generated.
	 */
	public boolean hasWarnings() {
		return (itsResultCode & WARNINGS_FLAG) > 0;
	}
	
	/**
	 * Indicates whether or not the processing generated either error or warning messages.
	 * @return {@code true} if error or warning messages were generated; {@code false} if no errors or warnings were generated.
	 */
	public boolean hasErrorsOrWarnings() {
		return (hasErrors() || hasWarnings());
	}
	
	/**
	 * Indicates whether or not output was successfully generated from processing this file.
	 * Certain error conditions in the input data can mean that the sanity checker cannot
	 * produce valid output.
	 * 
	 * @return {@code true} if output was successfully generated; {@code false} if output could not be generated. 
	 */
	public boolean outputGenerated() {
		return !((itsResultCode & NO_OUTPUT_FLAG) > 0);
	}
	
	/**
	 * Set a flag on the Sanity Checker's exit code. Any flag set will be added to the
	 * existing flags. Note that once a flag has been set it cannot be un-set.
	 * 
	 * @param flag The flag to be set.
	 */
	protected void setExitFlag(int flag) {
		itsResultCode = itsResultCode | flag;
	}
	
	/**
	 * Add a data record to the output.
	 * @param record The record to be added.
	 */
	protected void addRecord(SocatDataRecord record) {
		itsDataRecords.add(record);
	}
	
	/**
	 * Returns the number of data records processed from the original file.
	 * @return The number of data records processed from the original file.
	 */
	public int getRecordCount() {
		int result = 0;
		
		if (null != itsDataRecords) {
			result = itsDataRecords.size();
		}
		
		return result;
	}
	
	/**
	 * Returns the data records processed from the original file.
	 * @return The data records processed from the original file.
	 */
	public List<SocatDataRecord> getRecords() {
		return itsDataRecords;
	}
	
	/**
	 * Add a data message to the output
	 * @param message The message
	 */
	public void addMessage(Message message) throws MessageException {
		itsMessages.addMessage(message);
		itsLogger.trace("Message added to output::-> " + message.getMessageString());
		setExitFlag(message);
	}
	
	public void addMessages(List<Message> messages) throws MessageException {
		for (Message message : messages) {
			itsMessages.addMessage(message);
			itsLogger.trace("Message added to output::-> " + message.getMessageString());
			setExitFlag(message);
		}
	}

	private void setExitFlag(Message message) {
		if (message.isError()) {
			setExitFlag(ERRORS_FLAG);
		} else if (message.isWarning()) {
			setExitFlag(WARNINGS_FLAG);
		}
	}
	
	/**
	 * Clear the metadata, data and (optionally) messages output.
	 * Used to destroy output data if no valid output can be made from processing a file.
	 * 
	 * It is sometimes desirable to clear the output messages, if unforseen errors mean that
	 * the messages are likely to be rendered irrelevant (e.g. if unhandled exceptions occur).
	 * This should be used with caution.
	 * 
	 * @param setNoOutputFlag States whether or not the {@link #NO_OUTPUT_FLAG} should be set.
	 * @param clearMessages States whether or not error and warning message should be removed.
	 */
	public void clear(boolean setNoOutputFlag) {
		itsMetadata = null;
		itsDataRecords = null;
		
		if (setNoOutputFlag) {
			setExitFlag(NO_OUTPUT_FLAG);
		}
	}
	
	public void addMetadataValue(String name, String value, int line) throws OmeMetadataException {
		itsMetadata.replaceValue(name, value, line);
	}
	
	public void addCompositeMetadataValue(String name, Properties values, int line) throws OmeMetadataException, BadEntryNameException, InvalidConflictException {
		itsMetadata.storeCompositeValue(name, values, line);
	}
	
	/**
	 * Returns the processed metadata
	 * @return The processed metadata
	 */
	public OmeMetadata getMetadata() {
		return itsMetadata;
	}
	
	/**
	 * Return the collection of messages generated while processing the data file.
	 * @return The collection of messages.
	 */
	public Messages getMessages() {
		return itsMessages;
	}
}
