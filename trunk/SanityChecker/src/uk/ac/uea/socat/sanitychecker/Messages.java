package uk.ac.uea.socat.sanitychecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to hold messages generated while processing the data file
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
	 * @return The XML document
	 */
	public String getOutputXML() {
		return "XML!";
	}

	/**
	 * Add a set of messages
	 * @param messages The set of messages to be added
	 */
	protected void addMessages(List<Message> messages) {
		itsMessages.addAll(messages);
	}
	
	public List<Message> getMessages() {
		return itsMessages;
	}
	
	public List<Message> getMessages(int type, int severity) {
		List<Message> result = new ArrayList<Message>();
		
		for (Message message: itsMessages) {
			if (message.getMessageType() == type && message.getSeverity() == severity) {
				result.add(message);
			}
		}
		
		return result;
	}
	
	public List<Message> getMessagesByType(int type) {
		List<Message> result = new ArrayList<Message>();
		
		for (Message message: itsMessages) {
			if (message.getMessageType() == type) {
				result.add(message);
			}
		}
		
		return result;
	}
	
	public List<Message> getMessagesBySeverity(int severity) {
		List<Message> result = new ArrayList<Message>();
		
		for (Message message: itsMessages) {
			if (message.getSeverity() == severity) {
				result.add(message);
			}
		}
		
		return result;
	}
	
	public int getMessageCount() {
		return itsMessages.size();
	}
	
	public int getMessageCount(int type, int severity) {
		return getMessages(type, severity).size();
	}

	public int getMessageCountByType(int type) {
		return getMessagesByType(type).size();
	}
	
	public int getMessageCountBySeverity(int severity) {
		return getMessagesBySeverity(severity).size();
	}
}
