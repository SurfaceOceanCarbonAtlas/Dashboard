package uk.ac.uea.socat.omemetadata;

public class BadEntryNameException extends Exception {

	private static final long serialVersionUID = 2223385261472280085L;

	public BadEntryNameException(String message) {
		super(message);
	}
	
	public BadEntryNameException(int line, String message) {
		super("Line " + line + ": " + message);
	}

}
