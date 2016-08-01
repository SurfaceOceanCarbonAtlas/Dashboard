package uk.ac.uea.socat.sanitychecker.data.calculate;

import java.util.TreeSet;

import uk.ac.exeter.QCRoutines.messages.Flag;
import uk.ac.exeter.QCRoutines.messages.Message;
import uk.ac.exeter.QCRoutines.messages.MessageException;

public class CantCalculateExpoCodeMessage extends Message {

	public CantCalculateExpoCodeMessage(int lineNumber, TreeSet<Integer> columnIndices, TreeSet<String> columnNames, Flag flag, String fieldValue, String validValue) {
		super(lineNumber, columnIndices, columnNames, flag, fieldValue, validValue);
	}

	public CantCalculateExpoCodeMessage() throws MessageException {
		super(NO_LINE_NUMBER, NO_COLUMN_INDEX, null, Flag.BAD, null, null);
	}

	@Override
	public String getFullMessage() {
		return "Cannot generate EXPO Code for record";
	}

	@Override
	public String getShortMessage() {
		return "Cannot generate EXPO Code";
	}

}
