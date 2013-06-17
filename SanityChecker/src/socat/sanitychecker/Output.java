package socat.sanitychecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to hold everything that will be used to construct the output XML for the Sanity Checker
 */
public class Output {
	
	/**
	 * A count of the number of processed metadata lines
	 */
	private int itsMetadataLines;
	
	/**
	 * A count of the number of processed data lines
	 */
	private int itsDataLines;
	
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
	public Output() {
		itsMetadataLines = 0;
		itsDataLines = 0;
		itsMetadataMessages = new ArrayList<MetadataMessage>();
		itsDataMessages = new ArrayList<DataMessage>();
	}
	
	/**
	 * Records the fact that a metadata line was successfully processed
	 */
	public void addMetadataLine() {
		itsMetadataLines++;
	}
	
	/**
	 * Records the fact that a data line was successfully processed
	 */
	public void addDataLine() {
		itsDataLines++;
	}
	
	/**
	 * Add a metadata message to the output
	 * @param message The message
	 */
	public void addMetadataMessage(MetadataMessage message) {
		itsMetadataMessages.add(message);
	}
	
	/**
	 * Add a data message to the output
	 * @param message The message
	 */
	public void addDataMessage(DataMessage message) {
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
	public void addDataMessages(List<DataMessage> messages) {
		itsDataMessages.addAll(messages);
		
	}
	
}
