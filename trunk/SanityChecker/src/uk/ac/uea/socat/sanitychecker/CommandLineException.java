package uk.ac.uea.socat.sanitychecker;

/**
 * Exception class for errors encountered while processing the command line
 */
public class CommandLineException extends Exception {
	
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 10003001L;

	public CommandLineException(String message) {
		super("Error processing command line: " + message);
	}
}
