package uk.ac.uea.socat.sanitychecker.sanitychecks;

import java.util.List;

import uk.ac.uea.socat.sanitychecker.config.ConfigException;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.SocatDataColumn;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.messages.MessageType;

/**
 * Sanity check to ensure that the column value in every
 * record in a file is identical. Usually used to ensure that
 * the EXPO Code is the same in all records; if it isn't, that
 * means two cruises have been put in one file.
 *
 */
public class FixedValueSanityCheck extends SanityCheck {

	private static final String FIXED_VALUE_ID = "FIXED_VALUE";
	
	private static MessageType FIXED_VALUE_TYPE = null;

	/**
	 * The name of the column that must contain the column value
	 */
	private String itsColumnName = null;
	
	/**
	 * Indicates whether or not empty column values should be ignored for the
	 * purposes of the check
	 */
	private boolean ignoreMissing = false;
	
	/**
	 * The value stored in the first record. All subsequent records
	 * must contain this value.
	 */
	private String itsFixedValue = null;
	
	@Override
	public void initialise(List<String> parameters) throws SanityCheckException {
		if (parameters.size() < 1) {
			throw new SanityCheckException("Must supply column name");
		}
		itsColumnName = parameters.get(0);
		if ( (parameters.size() >= 2) &&
			 "Y".equalsIgnoreCase(parameters.get(1)) ) {
			ignoreMissing = true;
		}

		try {
			SocatColumnConfig columnConfig = SocatColumnConfig.getInstance();
			if (null == columnConfig.getColumnConfig(itsColumnName)) {
				throw new SanityCheckException("Unrecognised column name '" + itsColumnName + "' in parameter to FixedValueSanityCheck");
			}
		} catch (ConfigException e) {
			throw new SanityCheckException("Unhandled error while checking FixedValueSanityCheck parameters", e);
		}

		if (null == FIXED_VALUE_TYPE) {
			FIXED_VALUE_TYPE = new MessageType(FIXED_VALUE_ID, "Value for column '" + MessageType.COLUMN_NAME_IDENTIFIER + "' has changed - it must be the same in all records", "Value for column '"+ MessageType.COLUMN_NAME_IDENTIFIER + "' not fixed");
		}
	}

	@Override
	public void processRecord(SocatDataRecord record) throws SanityCheckException {

		String value = record.getColumn(itsColumnName).getValue();

		// We don't check null values
		if ( null == value )
			return;
		if ( ignoreMissing && ( value.equals(SocatDataColumn.MISSING_VALUE) || value.trim().isEmpty() ) )
			return;

		if ( null == itsFixedValue ) {
			itsFixedValue = value;
			return;
		}

		if ( value.compareTo(itsFixedValue) != 0 ) {
			try {
				record.getColumn(itsColumnName).setFlag(SocatColumnConfigItem.BAD_FLAG, itsMessages, record.getLineNumber(), record.getColumn(itsColumnName).getInputColumnIndex(), record.getColumn(itsColumnName).getInputColumnName(), FIXED_VALUE_TYPE, null, null);
			}
			catch (SocatDataBaseException e) {
				throw new SanityCheckException ("Error while setting flag on record", e);
			}
		}
	}

}
