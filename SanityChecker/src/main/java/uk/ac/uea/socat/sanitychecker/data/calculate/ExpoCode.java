package uk.ac.uea.socat.sanitychecker.data.calculate;

import uk.ac.uea.socat.omemetadata.OmeMetadata;
import uk.ac.uea.socat.omemetadata.OmeMetadataException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;
import uk.ac.uea.socat.sanitychecker.messages.Message;
import uk.ac.uea.socat.sanitychecker.messages.MessageType;

public class ExpoCode implements DataCalculator {

	private static final String CANT_CALC_EXPOCODE_ID = "CANT_CALC_EXPOCODE";
	
	private static MessageType CANT_CALC_EXPOCODE_TYPE = null; 
	
	/**
	 * Calculates the EXPO code for the cruise 
	 */
	@Override
	public String calculateDataValue(OmeMetadata metadata, SocatDataRecord record, int colIndex, String colName, DateTimeHandler dateTimeHandler) throws SocatDataException, OmeMetadataException {
		
		if (null == CANT_CALC_EXPOCODE_TYPE) {
			CANT_CALC_EXPOCODE_TYPE = new MessageType(CANT_CALC_EXPOCODE_ID, "Cannot calculate EXPO Code: Ship Code or Start Date are missing from metadata", "Cannot calculate EXPO Code");
		}
		
		/*
		 * The metadata processor will have already determined that the required metadata fields are
		 * present, so we can copy them without checking.
		 */
		String result = null;
		
		String expocode = metadata.getValue(OmeMetadata.EXPOCODE_STRING);
		String shipCode = metadata.getValue(OmeMetadata.VESSEL_ID_STRING);
		String startDate = metadata.getValue(OmeMetadata.START_DATE_STRING);
		
		if (null != expocode) {
			result = expocode;
		} else {
			if (null == shipCode || null == startDate) {
				Message message = new Message(Message.NO_COLUMN_INDEX, null, CANT_CALC_EXPOCODE_TYPE, Message.ERROR, Message.NO_LINE_NUMBER, null, null);
				record.addMessage(message);
			} else { 
				result = shipCode + startDate;
			}
		}
		
		return result;
	}
}
