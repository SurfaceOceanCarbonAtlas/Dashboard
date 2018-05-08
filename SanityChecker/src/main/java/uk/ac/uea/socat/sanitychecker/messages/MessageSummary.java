package uk.ac.uea.socat.sanitychecker.messages;

/**
 * Holds a summary of messages for a particular message type.
 * The information contained is the Message Type, the Column Name,
 * and the number of warnings and errors.
 */
public class MessageSummary {

	/**
	 * The type of message that this summary is for
	 */
	private MessageType itsMessageType;
	
	/**
	 * The column name that this summary is for
	 */
	private String itsColumnName;
	
	/**
	 * The number of warnings generated for this message type and column
	 */
	private int itsWarningCount;
	
	/**
	 * The number of errors generated for this message type and column
	 */
	private int itsErrorCount;
	
	/**
	 * Initialise the summary with a message type and column name.
	 * The counts are set to zero.
	 * 
	 * @param type The message type
	 * @param column The column name
	 */
	protected MessageSummary(MessageType type, String column) {
		itsMessageType = type;
		itsColumnName = column;
		itsWarningCount = 0;
		itsErrorCount = 0;
	}
	
	/**
	 * Returns the summary string for the message type, including the column name
	 * @return The summary string for the message type
	 */
	public String getSummaryString() {
		return itsMessageType.getSummaryMessage(itsColumnName);
	}
	
	/**
	 * Returns the number of warning messages
	 * @return The number of warning messages
	 */
	public int getWarningCount() {
		return itsWarningCount;
	}
	
	/**
	 * Returns the number of error messages
	 * 
	 * @return The number of error messages
	 */
	public int getErrorCount() {
		return itsErrorCount;
	}
	
	/**
	 * Increment the warning count
	 */
	public void addWarning() {
		itsWarningCount++;
	}
	
	/**
	 * Increment the error count
	 */
	public void addError() {
		itsErrorCount++;
	}
}
