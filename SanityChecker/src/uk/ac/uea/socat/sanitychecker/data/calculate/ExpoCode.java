package uk.ac.uea.socat.sanitychecker.data.calculate;

import uk.ac.uea.socat.omemetadata.OmeMetadata;
import uk.ac.uea.socat.omemetadata.OmeMetadataException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;

public class ExpoCode implements DataCalculator {

	/**
	 * Calculates the EXPO code for the cruise 
	 */
	@Override
	public String calculateDataValue(OmeMetadata metadata, SocatDataRecord record, int colIndex, String colName, DateTimeHandler dateTimeHandler) throws SocatDataException, OmeMetadataException {
		
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
				try {
					record.addMessage(new CantCalculateExpoCodeMessage());
				} catch(Exception e) {
					throw new SocatDataException(record.getLineNumber(), colIndex, null, "Error while adding message to record", e); 
				}
			} else { 
				result = shipCode + startDate;
			}
		}
		
		return result;
	}
}
