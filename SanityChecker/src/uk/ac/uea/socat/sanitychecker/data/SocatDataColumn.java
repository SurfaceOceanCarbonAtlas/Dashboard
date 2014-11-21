package uk.ac.uea.socat.sanitychecker.data;

import java.lang.reflect.Method;
import java.util.List;

import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.calculate.DataCalculator;
import uk.ac.uea.socat.sanitychecker.messages.Message;
import uk.ac.uea.socat.sanitychecker.messages.MessageType;

/**
 * Represents a single SOCAT output column value
 */
public class SocatDataColumn {
	
	/**
	 * The value used in SOCAT output files to indicate a missing value
	 */
	public static final String MISSING_VALUE = "NaN";
	
	/**
	 * The column value
	 */
	private String itsValue;
	
	/**
	 * The flag set for this column
	 */
	private int itsFlag;
	
	/**
	 * The configuration details for this column
	 */
	private SocatColumnConfigItem itsConfig;
	
	private StandardColumnInfo itsColumnInfo;

	/**
	 * Creates a new, empty data field ready to be populated
	 * @param config
	 */
	public SocatDataColumn(SocatColumnConfigItem config, StandardColumnInfo colInfo) {
		itsValue = MISSING_VALUE;
		itsFlag = SocatColumnConfigItem.GOOD_FLAG;
		itsConfig = config;
		itsColumnInfo = colInfo;
	}
	
	/**
	 * Sets the flag on this field, and add a message to the main checker output.
	 * If a worse flag has already been set, no action is taken.
	 * @param flag The flag to be set.
	 * @param messages A list of messages to which this flag's message will be added
	 * @param record The record number for the message
	 * @param columnIndex The column index for the message
	 * @param messageType The message type
	 * @param fieldValue The value of the column in the file
	 * @param validValue The valid value(s) for the column
	 * @throws SocatDataBaseException If the field doesn't have a flag.
	 */
	public void setFlag(int flag, List<Message> messages, int record, int columnIndex, MessageType messageType, String fieldValue, String validValue) throws SocatDataBaseException {
		if (itsConfig.hasFlag()) {
			
			// The flag codes are set up so worse flags are greater than weaker flags.
			// We only set the flag if it's worse than what's already been set.
			if (flag > itsFlag) {
				itsFlag = flag;

				// Add a message to the output regarding the flag
				if (itsFlag != SocatColumnConfigItem.NO_FLAG) {
					int severity = Message.WARNING;
					if (flag == SocatColumnConfigItem.BAD_FLAG) {
						severity = Message.ERROR;
					}
					
					messages.add(new Message(columnIndex, messageType, severity, record, fieldValue, validValue));
				}
			}
			
		} else {
			throw new SocatDataBaseException(itsConfig.getColumnName(), "Attempted to set the flag on a field without a flag.");
		}
	}
	
	/**
	 * Set the cascading flag for a column. This does not generate messages.
	 * @param flag The flag to be set
	 * @throws SocatDataBaseException If the field doesn't have a flag.
	 */
	public void setCascadeFlag(int flag) throws SocatDataBaseException {
		if (itsConfig.hasFlag()) {
			
			// The flag codes are set up so worse flags are greater than weaker flags.
			// We only set the flag if it's worse than what's already been set.
			if (flag > itsFlag) {
				itsFlag = flag;
			}
		} else {
			throw new SocatDataBaseException(itsConfig.getColumnName(), "Attempted to set the flag on a field without a flag.");
		}
	}
	
	public int getFlag() {
		return itsFlag;
	}
	
	/**
	 * Returns the flag type for this field
	 * @return The flag type for this field
	 */
	public int getFlagType() {
		return itsConfig.getFlagType();
	}
	
	/**
	 * Determines whether or not this field has a flag
	 * @return {@code true} if the field has a flag; {@code false} otherwise.
	 */
	public boolean hasFlag() {
		return itsConfig.hasFlag();
	}
	
	/**
	 * Sets the value for this field.
	 * @param value The value.
	 */
	public void setValue(String value, Double missingValue) {
		
		if (null == value) {
			itsValue = MISSING_VALUE;
		} else {
			if ( CheckerUtils.DEFAULT_MISSING_VALUE_STRINGS.contains(value.trim().toLowerCase()) ) {
				itsValue = MISSING_VALUE;
			} else if (null != missingValue) {
				try {
					double doubleValue = Double.parseDouble(value);
					if (doubleValue == missingValue.doubleValue()) {
						itsValue = MISSING_VALUE;
					} else {
						itsValue = value;
					}
				} catch (NumberFormatException e) {
					// If it's not a parseable number, we simply store the value
					itsValue = value;
				}
			} else {
				itsValue = value;
			}
		}
	}
	
	/**
	 * Returns the data source for this column
	 * @return The data source
	 */
	public int getDataSource() {
		return itsConfig.getDataSource();
	}
	
	/**
	 * Returns the value of this column
	 * @return the column value
	 */
	public String getValue() {
		return itsValue;
	}
	
	/**
	 * Returns the name of the metadata item to be used to populate this column
	 * @return The name of the metadata item to be used to populate this column
	 */
	public String getMetadataSourceName() {
		return itsConfig.getMetadataName();
	}
	
	/**
	 * Returns the object containing the data calculation method for this column
	 * @return The object containing the data calculation method for this column
	 */
	public DataCalculator getCalculatorObject() {
		return itsConfig.getCalculatorObject();
	}
	
	/**
	 * Returns the method to be invoked to calculate the data value for this column
	 * @return The method to be invoked to calculate the data value for this column
	 */
	public Method getCalculatorMethod() {
		return itsConfig.getCalculatorMethod();
	}
	
	/**
	 * Determines whether or not this column is empty
	 * @return {@code true} if the column is empty; {@code false} if it contains a value
	 */
	public boolean isEmpty() {
		return CheckerUtils.isEmpty(itsValue);
	}
	
	public int getInputColumnIndex() {
		int result = -1;
		
		if (null != itsColumnInfo) {
			result = itsColumnInfo.getInputColumnIndex();
		}
		
		return result;
	}
	
	public String getInputColumnName() {
		String result = null;
		
		if (null != itsColumnInfo) {
			result = itsColumnInfo.getInputColumnName();
		}
		
		return result;
	}
}
