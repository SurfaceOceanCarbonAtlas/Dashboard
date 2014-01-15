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
	 * The number of metadata errors
	 */
	private int itsMetadataErrorCount = 0;
	
	/**
	 * The number of metadata warnings
	 */
	private int itsMetadataWarningCount = 0;
	
	/**
	 * The number of data errors
	 */
	private int itsDataErrorCount = 0;
	
	/**
	 * The number of data warnings
	 */
	private int itsDataWarningCount = 0;
	
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
		addToCount(message);
	}
	
	/**
	 * Add a data message to the output
	 * @param message The message
	 */
	protected void addDataMessage(DataMessage message) {
		itsDataMessages.add(message);
		addToCount(message);
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
		for (DataMessage message : messages) {
			addToCount(message);
		}
	}
	
	/**
	 * Update the relevant message count based on a given message
	 * @param message The message to be added to the count
	 */
	private void addToCount(MetadataMessage message) {
		
		if (message instanceof DataMessage) {
			if (message.isWarning()) {
				itsDataWarningCount++;
			} else if (message.isError()) {
				itsDataErrorCount++;
			}
		} else {
			if (message.isWarning()) {
				itsMetadataWarningCount++;
			} else if (message.isError()) {
				itsMetadataErrorCount++;
			}
		}
	}
	
	public int getMetadataErrorCount() {
		return itsMetadataErrorCount;
	}
	
	public int getMetadataWarningCount() {
		return itsMetadataWarningCount;
	}
	
	public int getDataErrorCount() {
		return itsDataErrorCount;
	}
	
	public int getDataWarningCount() {
		return itsDataWarningCount;
	}
	
	/**
	 * Returns a string containing all metadata messages.
	 * This is a temporary method to be replaced by XML output,
	 * which can be formatted using XSLT
	 * 
	 * @return
	 */
	public String getMetadataMessageStrings() {
		StringBuffer output = new StringBuffer();
		
		for (MetadataMessage message: itsMetadataMessages) {
			output.append(message + "\n");
		}
		
		return output.toString();
	}
	
	
	/**
	 * Returns a string containing all metadata messages.
	 * This is a temporary method to be replaced by XML output,
	 * which can be formatted using XSLT
	 * 
	 * @return
	 */
	public String getDataMessageStrings() {
		StringBuffer output = new StringBuffer();
		
		for (MetadataMessage message: itsDataMessages) {
			output.append(message + "\n");
		}
		
		return output.toString();
	}
}
