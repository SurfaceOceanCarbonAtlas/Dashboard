package uk.ac.uea.socat.sanitychecker.config;

import java.lang.reflect.Method;

import uk.ac.exeter.QCRoutines.config.ColumnConfigItem;
import uk.ac.exeter.QCRoutines.messages.Flag;
import uk.ac.uea.socat.sanitychecker.data.calculate.DataCalculator;

/**
 * Holds details of the configuration for a single SOCAT column
 */
public class SocatColumnConfigItem extends ColumnConfigItem {
	
	/**
	 * The string indicating that the source of the column is in metadata
	 */
	public static final String METADATA_SOURCE_STRING = "M";
	
	/**
	 * The internal code indicating that the source of the column is in metadata
	 */
	public static final int METADATA_SOURCE = 0;
	
	/**
	 * The string indicating that the source of the column is in the file data
	 */
	public static final String DATA_SOURCE_STRING = "D";
	
	/**
	 * The internal code indicating that the source of the column is in the file data
	 */
	public static final int DATA_SOURCE = 1;
	
	/**
	 * The string indicating that the column value is calculated
	 */
	public static final String CALCULATION_SOURCE_STRING = "C";
	
	/**
	 * The internal code indicating that the column value is calculated
	 */
	public static final int CALCULATION_SOURCE = 2;
	
	/**
	 * The string indicating that the column value is not set by the Sanity Checker
	 */
	public static final String NO_SOURCE_STRING = "N";
	
	/**
	 * The internal code indicating that the column value is not set by the Sanity Checker
	 */
	public static final int NO_SOURCE = 3;
	
	/**
	 * The string indicating that the column has no flag
	 */
	public static final String NO_FLAG_STRING = "N";
	
	/**
	 * The internal code indicating that the column has no flag
	 */
	public static final int NO_FLAG = 0;
	
	/**
	 * The string indicating that the flag applies only to the current field
	 */
	public static final String FIELD_FLAG_STRING = "F";
	
	/**
	 * The internal code indicating that the flag applies only to the current field
	 */
	public static final int FIELD_FLAG = 1;
	
	/**
	 * The string indicating that the flag must cascade to other fields in the record 
	 */
	public static final String CASCADING_FLAG_STRING = "C";
	
	/**
	 * The internal code indicating that the flag must cascade to other fields in the record 
	 */
	public static final int CASCADING_FLAG = 2;
	
	/**
	 * The string indicating that this field send and receives cascade flags
	 */
	public static final String CASCADE_TARGET_FLAG_STRING = "X";
	
	/**
	 * The internal code indicating that this field send and receives cascade flags
	 */
	public static final int CASCADE_TARGET_FLAG = 3;
	
	/**
	 * The string indicating the "Good" flag value
	 */
	public static final String GOOD_FLAG_STRING = "G";
	
	/**
	 * The internal code indicating the "Good" flag value
	 */
	public static final int GOOD_FLAG = 2;
	
	/**
	 * The string indicating the "Questionable" flag value
	 */
	public static final String QUESTIONABLE_FLAG_STRING = "Q";
	
	/**
	 * The internal code indicating the "Questionable" flag value
	 */
	public static final int QUESTIONABLE_FLAG = 3;

	/**
	 * The string indicating the "Bad" flag value
	 */
	public static final String BAD_FLAG_STRING = "B";
	
	/**
	 * The internal code indicating the "Bad" flag value
	 */
	public static final int BAD_FLAG = 4;
	
	/**
	 * The name of the Required Field Group.
	 */
	private String requiredGroup = null;
	
	/**
	 * Indicates the source of the data for this column
	 */
	private int dataSource = DATA_SOURCE;
	
	/**
	 * Indicates the name of the metadata item to be used to populate
	 * this value. Only relevant if the column is sourced from metadata
	 */
	private String metadataName = null;
	
	/**
	 * The object that contains the data calculator method.
	 * Only relevant if the column's data source is set as Calculated.
	 */
	private DataCalculator calcObject = null;
	
	/**
	 * The method to be used to calculate the field value.
	 * Only relevant if the column's data source is set as Calculated.
	 */
	private Method calcMethod = null;
	
	/**
	 * The flag set for missing values
	 */
	private Flag missingFlag = Flag.GOOD;
	
	public SocatColumnConfigItem(int lineCount, int entryCount) {
		super(lineCount, entryCount);
	}
	
	/**
	 * Returns the data source for this column
	 * @return The data source
	 */
	public int getDataSource() {
		return dataSource;
	}
	
	/**
	 * Returns the name of the metadata item to be used to populate this column
	 * @return The name of the metadata item to be used to populate this column
	 */
	public String getMetadataName() {
		return metadataName;
	}
	
	/**
	 * Returns the object containing the data calculation method for this column
	 * @return The object containing the data calculation method for this column
	 */
	public DataCalculator getCalculatorObject() {
		return calcObject;
	}
	
	/**
	 * Returns the method to be invoked to calculate the data value for this column
	 * @return The method to be invoked to calculate the data value for this column
	 */
	public Method getCalculatorMethod() {
		return calcMethod;
	}
	
	/**
	 * Returns the flag value to be set if this column has a missing value
	 * @return The flag value.
	 */
	public Flag getMissingFlag() {
		return missingFlag;
	}
	
	/**
	 * Returns the name of the Required Group that this column belongs to
	 * @return The name of the Required Group that this column belongs to
	 */
	public String getRequiredGroup() {
		return requiredGroup;
	}
	
	@Override
	protected void setRequired(boolean required) {
		super.setRequired(required);

		// Date columns are not flagged as required - they're treated as a special case
		if (required && SocatColumnConfig.isDateColumn(getColumnName())) {
			super.setRequired(false);
		}
		
		// Lat/Lon are always required
		if (!required && (getColumnName().equalsIgnoreCase(SocatColumnConfig.LONGITUDE_COLUMN_NAME) || getColumnName().equalsIgnoreCase(SocatColumnConfig.LATITUDE_COLUMN_NAME))) {
			super.setRequired(true);
		}
	}
	
	protected void setRequiredGroup(String requiredGroup) {
		this.requiredGroup = requiredGroup;
		super.setRequired(true);
	}
	
	protected void setDataSource(String dataSource) throws InvalidDataSourceException {

		switch (dataSource.toUpperCase()) {
		case METADATA_SOURCE_STRING: {
			this.dataSource = METADATA_SOURCE;
			break;
		}
		case DATA_SOURCE_STRING: {
			this.dataSource = DATA_SOURCE;
			break;
		}
		case CALCULATION_SOURCE_STRING: {
			this.dataSource = CALCULATION_SOURCE;
			break;
		}
		case NO_SOURCE_STRING: {
			this.dataSource = NO_SOURCE;
			break;
		}
		default: {
			throw new InvalidDataSourceException(dataSource);
		}
		}
	}
	
	protected void setMetadataName(String metadataName) {
		if (METADATA_SOURCE == dataSource) {
			this.metadataName = metadataName;
		}
	}
	
	protected void setCalculatorDetails(DataCalculator calcObject, Method calcMethod) {
		this.calcObject = calcObject;
		this.calcMethod = calcMethod;
	}
	
	protected void setMissingFlag(Flag missingFlag) {
		this.missingFlag = missingFlag;
	}
}
