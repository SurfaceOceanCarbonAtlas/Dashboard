package uk.ac.uea.socat.sanitychecker.data;

import java.lang.reflect.Method;

import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.calculate.DataCalculator;

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

	/**
	 * Creates a new, empty data field ready to be populated
	 * @param config
	 */
	public SocatDataColumn(SocatColumnConfigItem config) {
		itsValue = MISSING_VALUE;
		itsFlag = SocatColumnConfigItem.GOOD_FLAG;
		itsConfig = config;
	}
	
	/**
	 * Sets the flag on this field. If a worse flag has already been set,
	 * no action is taken.
	 * @param flag The flag to be set
	 * @throws SocatDataBaseException If the field doesn't have a flag.
	 */
	public void setFlag(int flag) throws SocatDataBaseException {
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
	public void setValue(String value) {
		
		if (null == value) {
			itsValue = MISSING_VALUE;
		} else {
			String trimmedValue = value.trim();
			if (trimmedValue.length() == 0 || trimmedValue.equalsIgnoreCase("na") || trimmedValue.equalsIgnoreCase("nan")) {
				itsValue = MISSING_VALUE;
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
}
