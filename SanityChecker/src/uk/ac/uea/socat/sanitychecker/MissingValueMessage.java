package uk.ac.uea.socat.sanitychecker;

import java.util.TreeSet;

import uk.ac.exeter.QCRoutines.data.DataColumn;
import uk.ac.exeter.QCRoutines.messages.Flag;
import uk.ac.exeter.QCRoutines.messages.Message;
import uk.ac.exeter.QCRoutines.messages.MessageException;

public class MissingValueMessage extends Message {

	public MissingValueMessage(int lineNumber, TreeSet<Integer> columnIndices, TreeSet<String> columnNames, Flag flag, String fieldValue, String validValue) {
		super(lineNumber, columnIndices, columnNames, flag, fieldValue, validValue);
	}

	public MissingValueMessage(int lineNumber, DataColumn dataColumn, Flag flag) throws MessageException {
		super(lineNumber, dataColumn, flag, null);
	}

	@Override
	public String getFullMessage() {
		return "Missing required value for column '" + getColumnNamesAsString() + "'";
	}

	@Override
	public String getShortMessage() {
		return getColumnNamesAsString() + " missing";
	}

}
