package uk.ac.uea.socat.sanitychecker.sanitychecks;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Seconds;

import uk.ac.uea.socat.sanitychecker.config.ConfigException;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.messages.MessageType;

/**
 * Sanity check to ensure that a given column's value
 * is not constant for longer than a specified period.
 * This is often the sign of a faulty instrument.
 *
 */
public class ConstantSanityCheck extends SanityCheck {
	
	private static final String CONSTANT_ID = "CONSTANT";
	
	private static MessageType CONSTANT_TYPE = null;
	
	/**
	 * The name of the column to be changed
	 */
	private String itsColumnName = null;
	
	/**
	 * The maximum allowed duration of a constant value
	 */
	private int itsMaxDuration = 0;
	
	/**
	 * The list of records with a constant value
	 */
	private List<SocatDataRecord> itsRecords = new ArrayList<SocatDataRecord>();
	
	@Override
	public void initialise(List<String> parameters) throws SanityCheckException {
		if (parameters.size() < 2) {
			throw new SanityCheckException("Must supply two parameters: Column name and duration");
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
			itsMaxDuration = Integer.parseInt(parameters.get(1));
			if (itsMaxDuration <= 0) {
				throw new SanityCheckException("Max duration must be larger than zero");
			}
		} catch(NumberFormatException e) {
			throw new SanityCheckException("Duration parameter must be numeric");
		}
		
		if (null == CONSTANT_TYPE) {
			CONSTANT_TYPE = new MessageType(CONSTANT_ID, "Value for column '" + MessageType.COLUMN_NAME_IDENTIFIER + "' is constant at '" + MessageType.FIELD_VALUE_IDENTIFIER + "' for more than " + MessageType.VALID_VALUE_IDENTIFIER + " minutes", "Column '"+ MessageType.COLUMN_NAME_IDENTIFIER + "' constant for too long");
		}

		
	}
	
	@Override
	public void processRecord(SocatDataRecord record) throws SanityCheckException {
		
		// Only check a record if it is ok (see recordOK Javadoc)
		if (recordOK(record)) {
			// If there's no record stored, this is the first of a new constant value
			if (itsRecords.size() == 0) {
				itsRecords.add(record);
			} else {
				if (equalsConstant(record)) {
					// If it equals the value in the first record, then it's still a constant value
					itsRecords.add(record);
				} else {
					// The value is no longer constant.
					// See how long it was constant for
					doDurationCheck();

					// Clear the list of constant records and start again
					itsRecords.clear();
					itsRecords.add(record);
				}
				
			}
		}
	}
	
	@Override
	public void performFinalCheck() throws SanityCheckException {
		// See how long the final value(s) were constant for
		doDurationCheck();
	}
	

	/**
	 * Determines whether or not the value in the passed record is identical to that
	 * in the list of constant records
	 * @param record The record to be checked
	 * @return {@code true} if the value in the record equals that in the list of constant records; {@code false} otherwise.
	 * @throws SanityCheckException If the value cannot be compared.
	 */
	private boolean equalsConstant(SocatDataRecord record) throws SanityCheckException {

		boolean result = false;
		
		try {
			double currentValue = getRecordValue(itsRecords.get(0));
			double recordValue = getRecordValue(record);
			
			result = (currentValue == recordValue);
		} catch (NumberFormatException e) {
			throw new SanityCheckException("Cannot compare non-numeric values", e);
		}
		
		return result;
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
	 * See how long the value has been constant in the set of stored records.
	 * If the value is constant for longer than the maximum time, flag each record accordingly.
	 * @throws SanityCheckException If the records cannot be flagged.
	 */
	private void doDurationCheck() throws SanityCheckException {

		// For measurements taken a long time apart, the value can easily be constant.
		// For example, measurements taken hourly can happily have the same value, but
		// if the constant check is set for 30 minutes it will always be triggered.
		//
		// Therefore we make sure there's more than two consecutive measurements with the
		// constant value.
		if (itsRecords.size() > 2) {
		
			double secondsDifference = Seconds.secondsBetween(itsRecords.get(0).getTime(), itsRecords.get(itsRecords.size() - 1).getTime()).getSeconds();
			double minutesDifference = secondsDifference / 60.0;
			
			
			if (minutesDifference > itsMaxDuration) {
				try {
					for (SocatDataRecord record : itsRecords) {
						record.getColumn(itsColumnName).setFlag(SocatColumnConfigItem.QUESTIONABLE_FLAG, itsMessages, itsRecords.get(0).getLineNumber(), record.getColumn(itsColumnName).getInputColumnIndex(), record.getColumn(itsColumnName).getInputColumnName(), CONSTANT_TYPE, new DecimalFormat("#").format(minutesDifference), Integer.toString(itsMaxDuration));
					}
				} catch (SocatDataBaseException e) {
					throw new SanityCheckException ("Error while setting flag on record", e);
				}
			}
		}
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
