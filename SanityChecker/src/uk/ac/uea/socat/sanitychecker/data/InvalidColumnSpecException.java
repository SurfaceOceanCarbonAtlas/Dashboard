package uk.ac.uea.socat.sanitychecker.data;

import java.io.File;

/**
 * Exception for invalid column specification files
 */
public class InvalidColumnSpecException extends Exception {
	
	/**
	 * Serial UID 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The file in which the exception was found
	 */
	private File itsFile = null;
	
	/**
	 * Exception constructor
	 * @param specFile The file in which the exception occurred
	 * @param message The error message
	 */
	public InvalidColumnSpecException(File specFile, String message) {
		super(message);
		itsFile = specFile;
	}
	
	/**
	 * Exception constructor
	 * @param specFile The file in which the exception occurred
	 * @param message The error message
	 * @param cause The root cause of the error
	 */
	public InvalidColumnSpecException(File specFile, String message, Throwable cause) {
		super(message, cause);
		itsFile = specFile;
	}

	/**
	 * Returns the error message for this exception
	 */
	@Override
	public String getMessage() {
		return itsFile.getPath() + ": " + super.getMessage();
	}
}
