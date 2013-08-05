package uk.ac.uea.socat.sanitychecker.data.datetime;

public class DateTimeParseException extends DateTimeException {

	public DateTimeParseException(String message) {
		super(message);
	}
	
	public DateTimeParseException(Throwable cause) {
		super(cause.getMessage());
	}
}
