package uk.ac.uea.socat.sanitychecker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.TreeSet;

import uk.ac.exeter.QCRoutines.messages.Flag;
import uk.ac.exeter.QCRoutines.messages.Message;
import uk.ac.exeter.QCRoutines.messages.MessageException;

public class InternalErrorMessage extends Message {

	private Throwable cause = null;
	
	public InternalErrorMessage(int lineNumber, TreeSet<Integer> columnIndices, TreeSet<String> columnNames, Flag flag, String fieldValue, String validValue) {
		super(lineNumber, columnIndices, columnNames, flag, fieldValue, validValue);
	}

	public InternalErrorMessage(Throwable cause) throws MessageException {
		super(NO_LINE_NUMBER, NO_COLUMN_INDEX, null, Flag.BAD, cause.getMessage(), null);
		this.cause = cause;
	}

	@Override
	public String getFullMessage() {
		
		String result;
		
		if (null == cause) {
			result = getShortMessage();
		} else {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			cause.printStackTrace(pw);
			result = "Internal Error: " + sw.toString();
		}
		
		return result;
	}

	@Override
	public String getShortMessage() {
		return "Internal Error: " + fieldValue;
	}

	
}
