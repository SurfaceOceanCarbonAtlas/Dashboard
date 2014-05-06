package uk.ac.uea.socat.sanitychecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to hold a collection of messages generated while processing a data file
 */
public class Messages {
	
	/**
	 * A list of all the metadata messages generated during processing 
	 */
	private List<Message> itsMessages;
	
	/**
	 * Simple constructor - initialises an empty output object
	 */
	protected Messages() {
		itsMessages = new ArrayList<Message>();
	}
	
	/**
	 * Add a message to the output
	 * @param message The message
	 */
	protected void addMessage(Message message) {
		itsMessages.add(message);
	}
	
	/**
	 * Returns an XML document containing all the details of the output
	 * 
	 * @return The XML document
	 */
	public String getOutputXML() {
		return "XML!";
	}

	/**
	 * Add a set of messages to the collection
	 * @param messages The set of messages to be added
	 */
	protected void addMessages(List<Message> messages) {
		itsMessages.addAll(messages);
	}
	
	/**
	 * Returns the complete set of messages held in the collection.
	 * @return The messages held in the collection
	 */
	public List<Message> getMessages() {
		return itsMessages;
	}
	
	/**
	 * Get the messages that are of the type and severity specified.
	 * @param type The type of message (either {@link Message#METADATA_MESSAGE} or {@link Message#DATA_MESSAGE}).
	 * @param severity The severity of the message (either {@link Message#ERROR} or {@link Message#WARNING}).
	 * @return The set of messages that are of the specified type and severity.
	 */
	public List<Message> getMessages(int type, int severity) {
		List<Message> result = new ArrayList<Message>();
		
		for (Message message: itsMessages) {
			if (message.getMessageType() == type && message.getSeverity() == severity) {
				result.add(message);
			}
		}
		
		return result;
	}
	
	/**
	 * Get the messages that are of the specified type.
	 * @param type The type of message (either {@link Message#METADATA_MESSAGE} or {@link Message#DATA_MESSAGE}).
	 * @return The set of messages that are of the specified type.
	 */
	public List<Message> getMessagesByType(int type) {
		List<Message> result = new ArrayList<Message>();
		
		for (Message message: itsMessages) {
			if (message.getMessageType() == type) {
				result.add(message);
			}
		}
		
		return result;
	}
	
	/**
	 * Get the messages that are of the specified severity.
	 * @param severity The severity of the message (either {@link Message#ERROR} or {@link Message#WARNING}).
	 * @return The set of messages that are of the specified severity.
	 */
	public List<Message> getMessagesBySeverity(int severity) {
		List<Message> result = new ArrayList<Message>();
		
		for (Message message: itsMessages) {
			if (message.getSeverity() == severity) {
				result.add(message);
			}
		}
		
		return result;
	}
	
	/**
	 * Get the total number of messages stored in this collection.
	 * @return The total number of messages stored in this collection.
	 */
	public int getMessageCount() {
		return itsMessages.size();
	}
	
	/**
	 * Get the number of messages of the specified severity and type.
	 * @param type The type of message (either {@link Message#METADATA_MESSAGE} or {@link Message#DATA_MESSAGE}).
	 * @param severity The severity of the message (either {@link Message#ERROR} or {@link Message#WARNING}).
	 * @return The number of messages of the specified severity and type.
	 */
	public int getMessageCount(int type, int severity) {
		return getMessages(type, severity).size();
	}

	/**
	 * Get the number of messages that are of the specified type.
	 * @param type The type of message (either {@link Message#METADATA_MESSAGE} or {@link Message#DATA_MESSAGE}).
	 * @return The number of messages that are of the specified type.
	 */
	public int getMessageCountByType(int type) {
		return getMessagesByType(type).size();
	}
	
	/**
	 * Get the number of messages that are of the specified severity.
	 * @param severity The severity of the message (either {@link Message#ERROR} or {@link Message#WARNING}).
	 * @return The number of messages that are of the specified severity.
	 */
	public int getMessageCountBySeverity(int severity) {
		return getMessagesBySeverity(severity).size();
	}
}
