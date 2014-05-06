package uk.ac.uea.socat.sanitychecker;

/**
 * Exception class for unhandled errors in the Sanity Checker.
 * 
 * This will primarily be thrown if the input to the {@link SanityChecker}
 * constructor is invalid.
 * 
 * After that it should never be thrown, but it's the ultimate fallback.
 */
public class SanityCheckerException extends Exception {
	
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 10003001L;

	public SanityCheckerException(String message) {
		super("Unhandled error in Sanity checker: " + message);
	}

	public SanityCheckerException(String message, Exception e) {
		super(message, e);
	}
}
