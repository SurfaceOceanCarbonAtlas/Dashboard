package uk.ac.uea.socat.sanitychecker;

import java.io.PrintWriter;
import java.io.StringWriter;

import uk.ac.exeter.QCRoutines.messages.Flag;
import uk.ac.exeter.QCRoutines.messages.Message;

public class InternalErrorMessage extends Message {

	private Throwable cause;
	
	public InternalErrorMessage(Throwable cause) {
		super(NO_LINE_NUMBER, NO_COLUMN_INDEX, null, Flag.BAD, null, null);
		this.cause = cause;
	}

	@Override
	public String getFullMessage() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		cause.printStackTrace(pw);
		return "Internal Error: " + sw.toString();
	}

	@Override
	public String getShortMessage() {
		return "Internal Error: " + cause.getMessage();
	}

	
}
