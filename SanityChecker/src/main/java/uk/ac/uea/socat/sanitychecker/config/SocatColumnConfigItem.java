package uk.ac.uea.socat.sanitychecker.config;

import java.lang.reflect.Method;

import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.data.SocatDataRecord;
import uk.ac.uea.socat.sanitychecker.data.calculate.DataCalculator;

/**
 * Holds details of the configuration for a single SOCAT column
 */
public class SocatColumnConfigItem {
	
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
	 * The name of the SOCAT column
	 */
	private String itsName;
	
	/**
	 * The index of the column in the data file
	 */
	private int itsIndex;
	
	/**
	 * Indicates whether or not this column is required
	 */
	private boolean itIsRequired;
	
	/**
	 * The name of the Required Field Group.
	 */
	private String itsRequiredGroup;
	
	/**
	 * Indicates the source of the data for this column
	 */
	private int itsDataSource;
	
	/**
	 * Indicates the name of the metadata item to be used to populate
	 * this value. Only relevant if the column is sourced from metadata
	 */
	private String itsMetadataName;
	
	/**
	 * The object that contains the data calculator method.
	 * Only relevant if the column's data source is set as Calculated.
	 */
	private DataCalculator itsCalcObject;
	
	/**
	 * The method to be used to calculate the field value.
	 * Only relevant if the column's data source is set as Calculated.
	 */
	private Method itsCalcMethod;
	
	/**
	 * Indicates whether or not this is a numeric column
	 */
	private boolean itIsNumeric;
	
	/**
	 * Indicates whether or not this column has a data range outside which a questionable flag is raised
	 * Only relevant for numeric fields.
	 */
	private boolean itHasQuestionableRange;
	
	/**
	 * Indicates whether or not this column has a data range outside which a questionable flag is raised
	 * Only relevant for numeric fields.
	 */
	private boolean itHasBadRange;

	/**
	 * The minimum allowed value that this column can contain, outside which a questionable flag is raised.
	 * Only relevant for numeric columns with a range.
	 */
	private double itsQuestionableRangeMin;
	
	/**
	 * The minimum allowed value that this column can contain, outside which a questionable flag is raised.
	 * Only relevant for numeric columns with a range.
	 */
	private double itsQuestionableRangeMax;
	
	/**
	 * The minimum allowed value that this column can contain, outside which a bad flag is raised.
	 * Only relevant for numeric columns with a range.
	 */
	private double itsBadRangeMin;
	
	/**
	 * The minimum allowed value that this column can contain, outside which a bad flag is raised.
	 * Only relevant for numeric columns with a range.
	 */
	private double itsBadRangeMax;
	
	/**
	 * The type of flag set for this field
	 */
	private int itsFlagType;
	
	/**
	 * The flag set for missing values
	 */
	private int itsMissingFlag;
	
	/**
	 * The flag set for out-of-range values
	 */
	private int itsRangeFlag;

	/**
	 * The constructor for the output column configuration
	 * @param name The field name
	 * @param isRequired Is the field required?
	 * @param requiredGroup The collective group of required fields
	 * @param dataSource The source of the data
	 * @param metadataName The name of the metadata item from which data will be retrieved
	 * @param calcMethod The method to be used to calculate the field's value
	 * @param isNumeric Is this a numeric field?
	 * @param hasRange Is a range specified for this column?
	 * @param questionableRangeMin The minimum allowed value outside which a questionable flag is raised
	 * @param questionableRangeMax The maximum allowed value outside which a questionable flag is raised
	 * @param badRangeMin The minimum allowed value outside which a questionable flag is raised
	 * @param badRangeMax The maximum allowed value outside which a questionable flag is raised
	 * @param flagType The type of flag that can be set on this field
	 * @param missingFlag The severity of the missing flag
	 */
	public SocatColumnConfigItem(String name, int index, boolean isRequired, String requiredGroup, int dataSource, String metadataName, DataCalculator calculatorObject, Method calculatorMethod, boolean isNumeric, boolean hasQuestionableRange, double questionableRangeMin, double questionableRangeMax, boolean hasBadRange, double badRangeMin, double badRangeMax, int flagType, int missingFlag) {
		itsName = name;
		itsIndex = index;
		itsRequiredGroup = requiredGroup;
		if (!CheckerUtils.isEmpty(requiredGroup)) {
			itIsRequired = true;
		}
			
		itsDataSource = dataSource;
		itsMetadataName = metadataName;
		
		itsCalcObject = calculatorObject;
		itsCalcMethod = calculatorMethod;
		itIsNumeric = isNumeric;
		itHasQuestionableRange = hasQuestionableRange;
		itHasBadRange = hasBadRange;
		itsQuestionableRangeMin = questionableRangeMin;
		itsQuestionableRangeMax = questionableRangeMax;
		itsBadRangeMin = badRangeMin;
		itsBadRangeMax = badRangeMax;
		itsFlagType = flagType;
		itsMissingFlag = missingFlag;
		
		itIsRequired = isRequired;
		
		/*
		 * The date columns are never flagged as required here, because
		 * they are handled as a special case.
		 */
		if (itIsRequired) {
			for (int i = 0; i < SocatDataRecord.DATE_COLUMN_NAMES.length && itIsRequired; i++) {
				if (itsName.equalsIgnoreCase(SocatDataRecord.DATE_COLUMN_NAMES[i])) {
					itIsRequired = false;
				}
			}
		}
		
		/*
		 * Lat/lon fields, on the other hand, are always required. Their ranges are fixed too.
		 */
		if (itsName.equalsIgnoreCase(SocatDataRecord.LONGITUDE_COLUMN_NAME)) {
			itIsRequired = true;
			
			itHasQuestionableRange = false;
			itHasBadRange = true;
			itsBadRangeMin = -180.0;
			itsBadRangeMax = 360.0;
		}
		
		if (itsName.equalsIgnoreCase(SocatDataRecord.LATITUDE_COLUMN_NAME)) {
			itIsRequired = true;
			itHasQuestionableRange = false;
			itHasBadRange = true;
			itsBadRangeMin = -90.0;
			itsBadRangeMax = 90.0;
		}
	}
	
	/**
	 * Converts the string representation of the column data source from the Sanity Checker's configuration
	 * into an integer code for easy processing later.
	 * 
	 * @param configFile The name of the configuration file. Used for exception handling
	 * @param colName The name of the column. Used for exception handling
	 * @param lineNumber The current line in the config file. Used for exception handling
	 * @param dataSource The string code for the data source
	 * @return The integer code for the data source
	 * @throws ConfigException If the string code cannot be recognised.
	 */
	public static int convertDataSourceString(String configFile, String colName, int lineNumber, String dataSource) throws ConfigException {
		int result;
		
		if (dataSource.equalsIgnoreCase(METADATA_SOURCE_STRING)) {
			result = METADATA_SOURCE;
		} else if (dataSource.equalsIgnoreCase(DATA_SOURCE_STRING)) {
			result = DATA_SOURCE;
		} else if (dataSource.equalsIgnoreCase(CALCULATION_SOURCE_STRING)) {
			result = CALCULATION_SOURCE;
		} else if (dataSource.equalsIgnoreCase(NO_SOURCE_STRING)) {
			result = NO_SOURCE;
		} else {
			throw new ConfigException(configFile, colName, lineNumber, "Unrecognised SOCAT column data source '" + dataSource + "'");
		}
		
		return result;
	}
	
	/**
	 * Converts the string representation of the flag type from the Sanity Checker's configuration
	 * into an integer code for easy processing later.
	 * 
	 * @param configFile The name of the configuration file. Used for exception handling
	 * @param colName The name of the column. Used for exception handling
	 * @param lineNumber The current line in the config file. Used for exception handling
	 * @param flagType The string code for the flag type
	 * @return The integer code for the flag type
	 * @throws ConfigException If the string code cannot be recognised
	 */
	public static int convertFlagTypeString(String configFile, String colName, int lineNumber, String flagType) throws ConfigException {
		int result;
		
		if (flagType.equalsIgnoreCase(NO_FLAG_STRING)) {
			result = NO_FLAG;
		} else if (flagType.equalsIgnoreCase(FIELD_FLAG_STRING)) {
			result = FIELD_FLAG;
		} else if (flagType.equalsIgnoreCase(CASCADING_FLAG_STRING)) {
			result = CASCADING_FLAG;
		} else if (flagType.equalsIgnoreCase(CASCADE_TARGET_FLAG_STRING)) {
			result = CASCADE_TARGET_FLAG;
		} else {
			throw new ConfigException(configFile, colName, lineNumber, "Unrecognised flag type '" + flagType + "'");
		}
		
		return result;
	}
	
	/**
	 * Returns the flag type for this field
	 * @return The flag type for this field
	 */
	public int getFlagType() {
		return itsFlagType;
	}
	
	/**
	 * Converts the string representation of the flag value from the Sanity Checker's configuration
	 * into an integer code for easy processing later.
	 * 
	 * @param configFile The name of the configuration file. Used for exception handling
	 * @param colName The name of the column. Used for exception handling
	 * @param lineNumber The current line in the config file. Used for exception handling
	 * @param flagType The string code for the flag value
	 * @return The integer code for the flag value
	 * @throws ConfigException If the string code cannot be recognised
	 */
	public static int convertFlagValueString(String configFile, String colName, int lineNumber, String flagValue) throws ConfigException {
		int result;
		
		if (flagValue.equalsIgnoreCase(GOOD_FLAG_STRING)) {
			result = GOOD_FLAG;
		} else if (flagValue.equalsIgnoreCase(QUESTIONABLE_FLAG_STRING)) {
			result = QUESTIONABLE_FLAG;
		} else if (flagValue.equalsIgnoreCase(BAD_FLAG_STRING)) {
			result = BAD_FLAG;
		} else {
			throw new ConfigException(configFile, colName, lineNumber, "Unrecognised flag value '" + flagValue + "'");
		}
		
		return result;
	}
	
	/**
	 * Determines whether or not this field has a flag
	 * @return {@code true} if the field has a flag; {@code false} otherwise.
	 */
	public boolean hasFlag() {
		return itsFlagType != NO_FLAG;
	}
	
	/**
	 * Determines whether or not this field is numeric
	 * @return {@code true} if the field is numeric; {@code false} otherwise.
	 */
	public boolean isNumeric() {
		return itIsNumeric;
	}
	
	/**
	 * Returns the name of the output SOCAT column that this configuration item refers to.
	 * @return
	 */
	public String getColumnName() {
		return itsName;
	}
	
	/**
	 * Returns the data source for this column
	 * @return The data source
	 */
	public int getDataSource() {
		return itsDataSource;
	}
	
	/**
	 * Returns the name of the metadata item to be used to populate this column
	 * @return The name of the metadata item to be used to populate this column
	 */
	public String getMetadataName() {
		return itsMetadataName;
	}
	
	/**
	 * Returns the object containing the data calculation method for this column
	 * @return The object containing the data calculation method for this column
	 */
	public DataCalculator getCalculatorObject() {
		return itsCalcObject;
	}
	
	/**
	 * Returns the method to be invoked to calculate the data value for this column
	 * @return The method to be invoked to calculate the data value for this column
	 */
	public Method getCalculatorMethod() {
		return itsCalcMethod;
	}
	
	/**
	 * Determines whether or not this column is required to have a value.
	 * If the column is part of a required group, it is deemed to be required
	 * in this function.
	 * 
	 * @return {@code} true if the column must contain a value; {@code false} otherwise. 
	 */
	public boolean isRequired() {
		return itIsRequired;
	}
	
	/**
	 * Returns the flag value to be set if this column has a missing value
	 * @return The flag value.
	 */
	public int getMissingFlag() {
		return itsMissingFlag;
	}
	
	/**
	 * Returns the flag value to be set if this column's value is out of range
	 * @return The flag value.
	 */
	public int getRangeFlag() {
		return itsRangeFlag;
	}
	
	/**
	 * Returns the name of the Required Group that this column belongs to
	 * @return The name of the Required Group that this column belongs to
	 */
	public String getRequiredGroup() {
		return itsRequiredGroup;
	}
	
	/**
	 * Returns the index of this column in the data file
	 * @return The index of this column in the data file
	 */
	public int getIndex() {
		return itsIndex;
	}
	/**
	 * Check a value to see if it's within the range specified for the column.
	 * The result can be good, questionable or bad, and is returned as the appropriate flag.
	 * @param value The value to be checked
	 * @return A flag indicating whether the value is {@link SocatColumnConfigItem#GOOD_FLAG},
	 *         {@link SocatColumnConfigItem#QUESTIONABLE_FLAG} or {@link SocatColumnConfigItem#BAD_FLAG}
	 */
	public int checkRange(double value) {
		int result = GOOD_FLAG;
		
		if (itHasBadRange && (value < itsBadRangeMin || value > itsBadRangeMax)) {
			result = BAD_FLAG;
		} else if (itHasQuestionableRange && (value < itsQuestionableRangeMin || value > itsQuestionableRangeMax)) {
			result = QUESTIONABLE_FLAG;
		}
		
		return result;
	}
	
	/**
	 * Returns the number below which a value will be considered {@link SocatColumnConfigItem#BAD_FLAG}
	 * @return The number below which a value will be considered {@link SocatColumnConfigItem#BAD_FLAG}
	 */
	public double getBadRangeMin() {
		return itsBadRangeMin;
	}
	
	/**
	 * Returns the number above which a value will be considered {@link SocatColumnConfigItem#BAD_FLAG}
	 * @return The number above which a value will be considered {@link SocatColumnConfigItem#BAD_FLAG}
	 */
	public double getBadRangeMax() {
		return itsBadRangeMax;
	}
	
	/**
	 * Returns the number below which a value will be considered {@link SocatColumnConfigItem#QUESTIONABLE_FLAG}
	 * @return The number below which a value will be considered {@link SocatColumnConfigItem#QUESTIONABLE_FLAG}
	 */
	public double getQuestionableRangeMin() {
		return itsQuestionableRangeMin;
	}
	
	/**
	 * Returns the number above which a value will be considered {@link SocatColumnConfigItem#QUESTIONABLE_FLAG}
	 * @return The number above which a value will be considered {@link SocatColumnConfigItem#QUESTIONABLE_FLAG}
	 */
	public double getQuestionableRangeMax() {
		return itsQuestionableRangeMax;
	}
	
	/**
	 * Checks to see if this column has a Bad range defined
	 * @return {@code true} if the Bad range has been defined; {@code false} otherwise.
	 */
	public boolean hasBadRange() {
		return itHasBadRange;
	}
	
	/**
	 * Checks to see if this column has a Questionable range defined
	 * @return {@code true} if the Questionable range has been defined; {@code false} otherwise.
	 */
	public boolean hasQuestionableRange() {
		return itHasQuestionableRange;
	}
}
