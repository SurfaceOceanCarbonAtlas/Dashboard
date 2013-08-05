package uk.ac.uea.socat.sanitychecker.data.datetime;

/**
 * Exception class for errors encountered while parsing
 * or otherwise handling dates and times.
 */
public class DateTimeException extends Exception {

	/**
	 * Basic constructor with a message
	 * @param message
	 */
	public DateTimeException(String message) {
		super(message);
	}
}
