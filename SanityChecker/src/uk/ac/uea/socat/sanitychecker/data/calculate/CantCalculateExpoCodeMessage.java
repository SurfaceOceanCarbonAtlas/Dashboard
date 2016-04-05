package uk.ac.uea.socat.sanitychecker.data.calculate;

import uk.ac.exeter.QCRoutines.messages.Flag;
import uk.ac.exeter.QCRoutines.messages.Message;

public class CantCalculateExpoCodeMessage extends Message {

	public CantCalculateExpoCodeMessage() {
		super(NO_LINE_NUMBER, NO_COLUMN_INDEX, null, Flag.BAD, null, null);
	}

	@Override
	protected String getFullMessage() {
		return "Cannot generate EXPO Code for record";
	}

	@Override
	public String getShortMessage() {
		return "Cannot generate EXPO Code";
	}

}
