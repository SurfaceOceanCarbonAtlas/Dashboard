package uk.ac.uea.socat.sanitychecker.sanitychecks;

import java.util.ArrayList;
import java.util.List;

import uk.ac.uea.socat.sanitychecker.config.ConfigException;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.messages.MessageType;

/**
 * Sanity check to detect outliers by spotting values
 * outside {@code n} standard deviations from the mean.
 * 
 * The mean and standard deviation are calculated as the records
 * are passed in. At the end, each record is re-checked to see if it falls
 * outside the standard deviation limit.
 */
public class OutlierSanityCheck extends SanityCheck {
	
	private static final String OUTLIER_ID = "OUTLIER";
	
	private static MessageType OUTLIER_TYPE = null;

	/**
	 * The name of the column to be checked
	 */
	private String itsColumnName;
	
	/**
	 * The standard deviation limit. Values outside this number
	 * of standard deviations will be flagged as outliers.
	 */
	private double itsStdevLimit;
	
	/**
	 * The number of records processed
	 */
	private int itsValueCount = 0;
	
	/**
	 * The mean of all record values
	 */
	private double itsMean = 0;
	
	/**
	 * The standard deviation of all record values
	 */
	private double itsStdev = 0;
	
	private List<RecordValue> itsRecordValues;
	
	public void initialise(List<String> parameters) throws SanityCheckException {
		if (parameters.size() < 2) {
			throw new SanityCheckException("Must supply column name and standard deviation limit");
		}

		itsColumnName = parameters.get(0);

		try {
			SocatColumnConfig columnConfig = SocatColumnConfig.getInstance();
			if (null == columnConfig.getColumnConfig(itsColumnName)) {
				throw new SanityCheckException("Unrecognised column name '" + itsColumnName + "' in parameter to " + getClass().getName());
			}
		} catch (ConfigException e) {
			throw new SanityCheckException("Unhandled error while checking " + getClass().getName() + " parameters", e);
		}
		
		try {
			itsStdevLimit = Double.parseDouble(parameters.get(1));
		} catch(NumberFormatException e) {
			throw new SanityCheckException("Standard deviation limit must be numeric");
		}
		
		if (null == OUTLIER_TYPE) {
			OUTLIER_TYPE = new MessageType(OUTLIER_ID, "Value in column '" + MessageType.COLUMN_NAME_IDENTIFIER + "' (" + MessageType.FIELD_VALUE_IDENTIFIER + ") is outside " + MessageType.VALID_VALUE_IDENTIFIER + " standard deviations from the mean", "Outlier in column '" + MessageType.COLUMN_NAME_IDENTIFIER);
		}

		itsRecordValues = new ArrayList<RecordValue>();
	}
	
	/**
	 * Processing the records involves calculating the mean and standard deviation
	 */
	public void processRecord(SocatDataRecord record) throws SanityCheckException {
		try {
			double value = Double.parseDouble(record.getColumn(itsColumnName).getValue());
			if (!Double.isNaN(value)) {
				itsValueCount++;
				itsRecordValues.add(new RecordValue(record, value));
				
				if (itsValueCount == 1) {
					itsMean = value;
				} else {
					double d = value - itsMean;
					itsStdev += (itsValueCount - 1)*d*d/itsValueCount;
					itsMean += d/itsValueCount;
				}
			}
		} catch(NumberFormatException e) {
			throw new SanityCheckException("All record values must be numeric");
		}
	}

	@Override
	public void performFinalCheck() throws SanityCheckException {

		// Finalise the stdev calculation
		itsStdev = Math.sqrt(itsStdev / itsValueCount);
		
		// Check all values to see if they're outside the limit
		for (RecordValue recordValue : itsRecordValues) {
			double diffFromMean = Math.abs(recordValue.value - itsMean);
			
			if (diffFromMean > (itsStdev * itsStdevLimit)) {
				try {
					SocatDataRecord record = recordValue.record;
					record.getColumn(itsColumnName).setFlag(SocatColumnConfigItem.BAD_FLAG, itsMessages, record.getLineNumber(), record.getColumn(itsColumnName).getInputColumnIndex(), OUTLIER_TYPE, Double.toString(recordValue.value), Double.toString(itsStdevLimit));
				} catch (SocatDataBaseException e) {
					throw new SanityCheckException ("Error while setting flag on record", e);
				}
			}
		}
		
	}
	
	/**
	 * A simple object to store each record and its column value, to simplify
	 * checking after the mean and stdev have been calculated.
	 */
	private class RecordValue {
		private SocatDataRecord record;
		private double value;
		
		private RecordValue(SocatDataRecord record, double value) {
			this.record = record;
			this.value = value;
		}
	}
}
