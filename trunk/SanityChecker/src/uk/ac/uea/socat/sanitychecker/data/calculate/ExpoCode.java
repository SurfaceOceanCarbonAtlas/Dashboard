package uk.ac.uea.socat.sanitychecker.data.calculate;

import java.util.Map;

import uk.ac.uea.socat.sanitychecker.Message;
import uk.ac.uea.socat.sanitychecker.data.SocatDataException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;
import uk.ac.uea.socat.sanitychecker.metadata.MetadataItem;

public class ExpoCode implements DataCalculator {

	/**
	 * Calculates the EXPO code for the cruise 
	 */
	@Override
	public String calculateDataValue(Map<String, MetadataItem> metadata, SocatDataRecord record, int colIndex, String colName, DateTimeHandler dateTimeHandler) throws SocatDataException {
		
		/*
		 * The metadata processor will have already determined that the required metadata fields are
		 * present, so we can copy them without checking.
		 */
		try {
			String result = null;
			
			MetadataItem shipCode = metadata.get("nodccode");
			MetadataItem startDate = metadata.get("startdate");
			
			if (null == shipCode || null == startDate) {
				Message message = new Message(Message.DATA_MESSAGE, Message.ERROR, record.getLineNumber(), "Cannot calculate EXPO Code: Ship Code or Start Date are missing from metadata");
				record.addMessage(message);
			} else { 
				result = shipCode.getValue(dateTimeHandler) + startDate.getValue(dateTimeHandler);
			}
			
			return result;
		} catch (DateTimeException e) {
			throw new SocatDataException(record.getLineNumber(), colIndex, colName, e);
		}
	}
}
