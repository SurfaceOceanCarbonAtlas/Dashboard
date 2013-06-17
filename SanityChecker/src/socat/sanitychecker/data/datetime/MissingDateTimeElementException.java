package socat.sanitychecker.data.datetime;

public class MissingDateTimeElementException extends DateTimeException {
	public MissingDateTimeElementException() {
		super("One or more date/time elements are missing");
	}
}
