package uk.ac.uea.socat.sanitychecker.sanitychecks;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import uk.ac.uea.socat.sanitychecker.config.ConfigException;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.messages.MessageType;

/**
 * Sanity check to detect sudden jumps between measurements,
 * which typifies faulty sensors.
 * 
 * The check is made between two consecutive measurements only.
 * The limit is expressed in units per minute.
 *
 */
public class HighDeltaSanityCheck extends SanityCheck {
	
	private static final String HIGH_DELTA_ID = "HIGH_DELTA";
	
	private static MessageType HIGH_DELTA_TYPE = null;

	
	private static final double NO_VALUE = -99999.9;
	
	/**
	 * The name of the column to be changed
	 */
	private String itsColumnName = null;
	
	/**
	 * The maximum allowed change between two measurements, in units per minute
	 */
	private double itsMaxDelta = 0.0;
	
	/**
	 * The list of records with a constant value
	 */
	private double itsLastValue = NO_VALUE;
	
	/**
	 * The timestamp of the previous value
	 */
	private DateTime itsLastTime = null;
	
	@Override
	public void initialise(List<String> parameters) throws SanityCheckException {
		if (parameters.size() < 2) {
			throw new SanityCheckException("Must supply two parameters: Column name and max delta");
		}
		
		itsColumnName = parameters.get(0);
		
		try {
			SocatColumnConfig columnConfig = SocatColumnConfig.getInstance();
			if (null == columnConfig.getColumnConfig(itsColumnName)) {
				throw new SanityCheckException("Unrecognised column name '" + itsColumnName + "' in parameter to ConstantSanityCheck");
			}
		} catch (ConfigException e) {
			throw new SanityCheckException("Unhandled error while checking ConstantSanityCheck parameters", e);
		}
		
		try {
			itsMaxDelta = Double.parseDouble(parameters.get(1));
			if (itsMaxDelta <= 0) {
				throw new SanityCheckException("Max delta must be larger than zero");
			}
		} catch(NumberFormatException e) {
			throw new SanityCheckException("Delta parameter must be numeric");
		}
		
		if (null == HIGH_DELTA_TYPE) {
			HIGH_DELTA_TYPE = new MessageType(HIGH_DELTA_ID, "Value in column '" + MessageType.COLUMN_NAME_IDENTIFIER + "' changes faster than " + MessageType.VALID_VALUE_IDENTIFIER + " per minute", "Value in column '" + MessageType.COLUMN_NAME_IDENTIFIER + "' changes too fast");
		}
	}
	
	@Override
	public void processRecord(SocatDataRecord record) throws SanityCheckException {
		
		// Only check a record if it is ok (see recordOK Javadoc)
		if (recordOK(record)) {
			// If there's no record stored, this is the first of a new constant value
			if (itsLastValue == NO_VALUE) {
				itsLastValue = getRecordValue(record);
				itsLastTime = record.getTime();
			} else {
				
				// Calculate the change between this record and the previous one
				double thisValue = getRecordValue(record);
				DateTime thisTime = record.getTime();
				
				double minutesDifference = Seconds.secondsBetween(itsLastTime, thisTime).getSeconds() / 60.0;
				double valueDelta = Math.abs(thisValue - itsLastValue);
				
				double deltaPerMinute = valueDelta / minutesDifference;
				if (deltaPerMinute > itsMaxDelta) {
					try {
						record.getColumn(itsColumnName).setFlag(SocatColumnConfigItem.BAD_FLAG, itsMessages, record.getLineNumber(), record.getColumn(itsColumnName).getInputColumnIndex(), HIGH_DELTA_TYPE, null, Double.toString(itsMaxDelta));
					} catch (SocatDataBaseException e) {
						throw new SanityCheckException ("Error while setting flag on record", e);
					}
				}
				
				
				itsLastValue = thisValue;
				itsLastTime = thisTime;
			}
		}
	}
	
	/**
	 * Determines whether or not the value in a record is considered 'good'. If not, it is
	 * not checked as part of this sanity check.
	 * @param record The record whose column is to be checked.
	 * @return {@code true} if the record should be considered for checking; {@code false} if it should not be checked.
	 */
	private boolean recordOK(SocatDataRecord record) {
		boolean result = true;
		
		if (record.getColumn(itsColumnName).getFlag() == SocatColumnConfigItem.BAD_FLAG || record.getDateFlag() == SocatColumnConfigItem.BAD_FLAG) {
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Utility method for getting a record's value
	 * @param record The record whose value is to be retrieved
	 * @return The value parsed as a {@code double}
	 */
	private double getRecordValue(SocatDataRecord record) {
		return Double.parseDouble(record.getColumn(itsColumnName).getValue());
	}
}
