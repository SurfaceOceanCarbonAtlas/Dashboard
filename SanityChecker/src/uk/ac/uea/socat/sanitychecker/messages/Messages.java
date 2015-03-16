package uk.ac.uea.socat.sanitychecker.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.uea.socat.sanitychecker.config.ConfigException;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;

/**
 * Handles messages generated during sanity checking.
 * Message Types must be registered with the handler before
 * they can be used.
 */
public class Messages {

	/**
	 * The list of registered message types
	 */
	private Map<String, MessageType> itsMessageTypes;
	
	/**
	 * The set of messages.
	 * Messages are grouped by message type (column index and message),
	 * and then ordered by line number.
	 */
	private Map<MessageKey, Map<Integer, Message>> itsMessages;
	
	private ColumnSpec itsColumnSpec;
	
	/**
	 * Constructor - simply initialises the fields
	 */
	public Messages(ColumnSpec columnSpec) {
		itsMessageTypes = new HashMap<String, MessageType>();
		itsMessages = new TreeMap<MessageKey, Map<Integer, Message>>();
		itsColumnSpec = columnSpec;
	}
	
	/**
	 * Registers a new message type. If the message type (identified
	 * by its key) has already been registered, an exception is thrown.
	 * 
	 * @param messageType
	 * @throws MessageException If the message type has already been registered
	 */
	private void registerMessageType(MessageType messageType) {
		if (!itsMessageTypes.containsKey(messageType.getID())) {
			itsMessageTypes.put(messageType.getID(), messageType);
		}
	}
	
	/**
	 * Add a message to the set of messages
	 * @param message The message to be added
	 * @throws MessageException If the supplied message is not of a recognised type
	 */
	public void addMessage(Message message) throws MessageException {
		registerMessageType(message.getMessageType());
		
		MessageKey key = message.generateMessageKey();
		
		if (!itsMessageTypes.containsKey(key.getMessageType().getID())) {
			throw new MessageException("Unrecognised message type '" + key.getMessageType().getID() + "'");
		} else {
			if (!itsMessages.containsKey(key)) {
				itsMessages.put(key, new TreeMap<Integer, Message>());
			}
			
			itsMessages.get(key).put(new Integer(message.getLineNumber()), message);
		}
	}
	
	/**
	 * Get a summary of the stored messages. These are returned as a
	 * map of message strings and counts. Each entry in the map
	 * represents a specific error and column index.
	 * 
	 * The summary is ordered by column index and message text.
	 * 
	 * @return A set of messages and the number of messages for each
	 * @throws MessageException If an error occurs while looking up column information
	 */
	public List<MessageSummary> getMessageSummaries() throws MessageException {
		
		List<MessageSummary> result = new ArrayList<MessageSummary>();
		
		for (MessageKey key : itsMessages.keySet()) {

			int columnIndex = key.getColumnIndex();
			String columnName = itsColumnSpec.getInputColumnName(columnIndex);
			MessageSummary summary = new MessageSummary(key.getMessageType(), columnName);
			
			for (Integer lineNumber : itsMessages.get(key).keySet()) {
				Message message = itsMessages.get(key).get(lineNumber);
				switch (message.getSeverity()) {
				case Message.WARNING:
				{
					summary.addWarning();
					break;
				}
				case Message.ERROR:
				{
					summary.addError();
					break;
				}
				default:
				{
					throw new MessageException("Unrecognised message status " + message.getSeverity());
				}
				}
			}
			
			result.add(summary);
		}
		
		return result;
	}
	
	/**
	 * Retrieves the complete list of message strings
	 * @return A list of messages
	 * @throws MessageException If an error occurs while looking up column information
	 */
	public List<Message> getMessages() throws MessageException {
		
		List<Message> result = new ArrayList<Message>();
		
		for (MessageKey key : itsMessages.keySet()) {
			for (Integer lineNumber : itsMessages.get(key).keySet()) {
				result.add(itsMessages.get(key).get(lineNumber));
			}
		}
		
		return result;
	}
}
