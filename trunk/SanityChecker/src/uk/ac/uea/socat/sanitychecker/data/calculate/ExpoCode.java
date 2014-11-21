package uk.ac.uea.socat.sanitychecker.data.calculate;

import java.util.Map;

import uk.ac.uea.socat.sanitychecker.data.SocatDataException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;
import uk.ac.uea.socat.sanitychecker.messages.Message;
import uk.ac.uea.socat.sanitychecker.messages.MessageType;
import uk.ac.uea.socat.sanitychecker.metadata.MetadataItem;

public class ExpoCode implements DataCalculator {

	private static final String CANT_CALC_EXPOCODE_ID = "CANT_CALC_EXPOCODE";
	
	private static MessageType CANT_CALC_EXPOCODE_TYPE = null; 
	
	/**
	 * Calculates the EXPO code for the cruise 
	 */
	@Override
	public String calculateDataValue(Map<String, MetadataItem> metadata, SocatDataRecord record, int colIndex, String colName, DateTimeHandler dateTimeHandler) throws SocatDataException {
		
		if (null == CANT_CALC_EXPOCODE_TYPE) {
			CANT_CALC_EXPOCODE_TYPE = new MessageType(CANT_CALC_EXPOCODE_ID, "Cannot calculate EXPO Code: Ship Code or Start Date are missing from metadata", "Cannot calculate EXPO Code");
		}
		
		/*
		 * The metadata processor will have already determined that the required metadata fields are
		 * present, so we can copy them without checking.
		 */
		try {
			String result = null;
			
			MetadataItem expocode = metadata.get("expocode");
			MetadataItem shipCode = metadata.get("nodccode");
			MetadataItem startDate = metadata.get("startdate");
			
			if (null != expocode) {
				result = expocode.getValue(dateTimeHandler);
			} else {
				if (null == shipCode || null == startDate) {
					Message message = new Message(Message.NO_COLUMN_INDEX, CANT_CALC_EXPOCODE_TYPE, Message.ERROR, Message.NO_LINE_NUMBER, null, null);
					record.addMessage(message);
				} else { 
					result = shipCode.getValue(dateTimeHandler) + startDate.getValue(dateTimeHandler);
				}
			}
			
			return result;
		} catch (DateTimeException e) {
			throw new SocatDataException(record.getLineNumber(), colIndex, colName, e);
		}
	}
}
