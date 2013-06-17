package socat.sanitychecker;

import java.util.Properties;

/**
 * Holds all the details of a message raised regarding a metadata line
 */
public class MetadataMessage {
	
	public static final int WARNING = 0;
	
	public static final int ERROR = 1;
	
	public static final String NAME_PROPERTY = "name";
	
	public static final String MIN_PROPERTY = "min";
	
	public static final String MAX_PROPERTY = "max";
	
	private int itsLine;
	
	private int itsSeverity;
	
	private String itsMessage;
	
	private Properties itsProperties;
	
	public MetadataMessage(int severity, int line, String message) {
		itsSeverity = severity;
		itsLine = line;
		itsMessage = message;
		itsProperties = new Properties();
	}
	
	public MetadataMessage(int severity, int line, String itemName, String message) {
		itsSeverity = severity;
		itsLine = line;
		itsMessage = message;
		itsProperties = new Properties();
		itsProperties.setProperty(NAME_PROPERTY, itemName);
	}
	
	public void addProperty(String name, String value) {
		itsProperties.setProperty(name, value);
	}
	
	public int getSeverity() {
		return itsSeverity;
	}
}
