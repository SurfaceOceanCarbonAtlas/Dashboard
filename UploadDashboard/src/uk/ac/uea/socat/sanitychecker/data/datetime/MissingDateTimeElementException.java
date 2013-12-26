package uk.ac.uea.socat.sanitychecker.data.datetime;

public class MissingDateTimeElementException extends DateTimeException {

	private static final long serialVersionUID = -7336742613721826676L;

	public MissingDateTimeElementException() {
		super("One or more date/time elements are missing");
	}
}
