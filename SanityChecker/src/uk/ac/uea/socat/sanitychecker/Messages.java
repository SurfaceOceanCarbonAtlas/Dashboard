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
	private List<MetadataMessage> itsMetadataMessages;
	
	/**
	 * A list of all the data messages generated during processing 
	 */
	private List<DataMessage> itsDataMessages;
	
	/**
	 * Simple constructor - initialises an empty output object
	 */
	protected Messages() {
		itsMetadataMessages = new ArrayList<MetadataMessage>();
		itsDataMessages = new ArrayList<DataMessage>();
	}
	
	/**
	 * Add a metadata message to the output
	 * @param message The message
	 */
	protected void addMetadataMessage(MetadataMessage message) {
		itsMetadataMessages.add(message);
	}
	
	/**
	 * Add a data message to the output
	 * @param message The message
	 */
	protected void addDataMessage(DataMessage message) {
		itsDataMessages.add(message);
	}
	
	/**
	 * Returns an XML document containing all the details of the output
	 * @return The XML document
	 */
	public String getOutputXML() {
		return "XML!";
	}

	/**
	 * Add a set of data messages to the output
	 * @param messages The set of messages to be added
	 */
	protected void addDataMessages(List<DataMessage> messages) {
		itsDataMessages.addAll(messages);
		
	}
	
}
