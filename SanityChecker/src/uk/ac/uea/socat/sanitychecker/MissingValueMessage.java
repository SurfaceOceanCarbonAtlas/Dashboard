package uk.ac.uea.socat.sanitychecker;

import uk.ac.exeter.QCRoutines.data.DataColumn;
import uk.ac.exeter.QCRoutines.messages.Flag;
import uk.ac.exeter.QCRoutines.messages.Message;

public class MissingValueMessage extends Message {

	public MissingValueMessage(int lineNumber, DataColumn dataColumn, Flag flag) {
		super(lineNumber, dataColumn, flag, null);
	}

	@Override
	public String getFullMessage() {
		return "Missing required value for column '" + columnName + "'";
	}

	@Override
	public String getShortMessage() {
		return columnName + " missing";
	}

}
