package uk.ac.uea.socat.sanitychecker.sanitychecks;

public class SanityCheckException extends Exception {

	public SanityCheckException(String message) {
		super(message);
	}
	
	public SanityCheckException(String message, Throwable cause) {
		super(message, cause);
	}
}
