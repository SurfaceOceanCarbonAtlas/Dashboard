package uk.ac.uea.socat.sanitychecker.data.datetime;

public class DateTimeParseException extends DateTimeException {

	private static final long serialVersionUID = 5799741336184800085L;

	public DateTimeParseException(String message) {
		super(message);
	}
	
	public DateTimeParseException(Throwable cause) {
		super(cause.getMessage());
	}
}
