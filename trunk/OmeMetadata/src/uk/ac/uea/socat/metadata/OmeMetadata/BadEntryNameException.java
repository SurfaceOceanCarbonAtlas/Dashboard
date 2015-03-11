package uk.ac.uea.socat.metadata.OmeMetadata;

public class BadEntryNameException extends Exception {

	public BadEntryNameException(String message) {
		super(message);
	}
	
	public BadEntryNameException(int line, String message) {
		super("Line " + line + ": " + message);
	}

}
