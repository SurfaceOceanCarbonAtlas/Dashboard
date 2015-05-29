package uk.ac.uea.socat.metadata.OmeMetadata;

public class InvalidConflictException extends Exception {

	private static final long serialVersionUID = 5915590070949773913L;

	public InvalidConflictException(String message) {
		super(message);
	}
	
	public InvalidConflictException(int line, String message) {
		super("Line " + line + ": " + message);
	}

}
