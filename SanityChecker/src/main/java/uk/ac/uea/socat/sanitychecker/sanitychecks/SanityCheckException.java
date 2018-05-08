package uk.ac.uea.socat.sanitychecker.sanitychecks;

public class SanityCheckException extends Exception {

	private static final long serialVersionUID = -4480716076568192163L;

	public SanityCheckException(String message) {
		super(message);
	}
	
	public SanityCheckException(String message, Throwable cause) {
		super(message, cause);
	}
}
