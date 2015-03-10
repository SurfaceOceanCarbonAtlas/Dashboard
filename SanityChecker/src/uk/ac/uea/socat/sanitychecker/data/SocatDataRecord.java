package uk.ac.uea.socat.sanitychecker.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadata;
import uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadataException;
import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.Output;
import uk.ac.uea.socat.sanitychecker.config.ConfigException;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.calculate.DataCalculator;
import uk.ac.uea.socat.sanitychecker.data.conversion.ConversionException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeParseException;
import uk.ac.uea.socat.sanitychecker.data.datetime.MissingDateTimeElementException;
import uk.ac.uea.socat.sanitychecker.messages.Message;
import uk.ac.uea.socat.sanitychecker.messages.MessageException;
import uk.ac.uea.socat.sanitychecker.messages.MessageType;

/**
 * A class representing a single measurement record.
 * 
 * This class is responsible for taking in a line from a user-supplied data file,
 * and performing the necessary conversions to allow a SOCAT-formatted output file
 * to be produced.
 */

public class SocatDataRecord {
	
	/**
	 * The column name for the year
	 */
	public static final String YEAR_COLUMN_NAME = "yr";
	
	/**
	 * The column name for the month
	 */
	public static final String MONTH_COLUMN_NAME = "mon";
	
	/**
	 * The column name for the day
	 */
	public static final String DAY_COLUMN_NAME = "day";
	
	/**
	 * The column name for the hour
	 */
	public static final String HOUR_COLUMN_NAME = "hh";
	
	/**
	 * The column name for the minute
	 */
	public static final String MINUTE_COLUMN_NAME = "mm";
	
	/**
	 * The column name for the second
	 */
	public static final String SECOND_COLUMN_NAME = "ss";
	
	/**
	 * The list of date column headers. These columns will always return {@code false}
	 * to the {@link uk.ac.uea.socat.sanitychecker.data.ColumnSpec#isRequired(String)} function.
	 */
	public static final String DATE_COLUMN_NAMES[] = {"yr", "mon", "day", "hh", "mm", "ss", "iso_date"};
	
	private static MessageType MISSING_DATE_ELEMENT_TYPE = null;
	
	private static final String MISSING_DATE_ELEMENT_ID = "MISSING_DATE_ELEMENT";
	
	private static MessageType UNPARSABLE_DATE_TYPE = null;
	
	private static final String UNPARSABLE_DATE_ID = "UNPARSABLE_DATE";
	
	private static MessageType UNPARSABLE_NUMBER_TYPE = null;
	
	private static final String UNPARSABLE_NUMBER_ID = "UNPARSABLE_NUMBER";
	
	
	
	/**
	 * The column name for the ISO date
	 */
	private static final String ISO_DATE_COLUMN_NAME = "iso_date";
	
	/**
	 * The column name for the latitude
	 */
	public static final String LATITUDE_COLUMN_NAME = "latitude";
	
	/**
	 * The column name for the longitude
	 */
	public static final String LONGITUDE_COLUMN_NAME = "longitude";
	
	/**
	 * The output messages generated for this line, if any
	 */
	private List<Message> itsMessages;
	
	/**
	 * Flag to indicate the presence of warnings raised during processing
	 */
	private boolean itHasWarnings = false;
	
	/**
	 * Flag to indicate the presence of errors raised during processing
	 */
	private boolean itHasErrors = false;
	
	/**
	 * The line of the input file that this record came from
	 */
	private int itsLineNumber;

	/**
	 * The specification of the output columns
	 */
	private ColumnSpec itsColumnSpec;
	
	/**
	 * The configuration for populating the output columns
	 */
	private SocatColumnConfig itsColumnConfig;
	
	/**
	 * The set of output data columns
	 */
	private Map<String, SocatDataColumn> itsOutputColumns;
	
	private int itsDateFlag = 0;
	
	/**
	 * The program logger
	 */
	private Logger itsLogger;

	/**
	 * Builds a complete record object
	 * @param dataFields The set of data values for the record, in the order specified by the column specification
	 * @param lineNumber The line number of the record
	 * @param colSpec The column specification
	 * @param metadata The set of metadata for the column file. This may contain information that is required to generate some data values.
	 * @param dateTimeHandler The utility object for handling dates and time
	 * @param logger A logger instance
	 * @param output The Sanity Checker's {@link Output} object, where the any generated messages will be stored.
	 * @throws ConfigException If the output column configuration cannot be retrieved
	 * @throws SocatDataException If an error occurs while processing the data record
	 * @throws SocatDataBaseException If an error occurs while processing the data record
	 */
	public SocatDataRecord(List<String> dataFields, int lineNumber, ColumnSpec colSpec, OmeMetadata metadata, DateTimeHandler dateTimeHandler, Logger logger) throws ConfigException, SocatDataException, SocatDataBaseException, MessageException, OmeMetadataException {
		itsMessages = new ArrayList<Message>();
		itsColumnSpec = colSpec;
		itsLineNumber = lineNumber;
		itsLogger = logger;

		itsColumnConfig = SocatColumnConfig.getInstance();
		
		if (null == MISSING_DATE_ELEMENT_TYPE) {
			MISSING_DATE_ELEMENT_TYPE = new MessageType(MISSING_DATE_ELEMENT_ID, "One or more date/time elements are missing from " + MessageType.FIELD_VALUE_IDENTIFIER, "One or more date/time elements are missing");
		}

		if (null == UNPARSABLE_DATE_TYPE) {
			UNPARSABLE_DATE_TYPE = new MessageType(UNPARSABLE_DATE_ID, "The date/time could not be parsed " + MessageType.FIELD_VALUE_IDENTIFIER, "Date/time could not be parsed");
		}

		if (null == UNPARSABLE_NUMBER_TYPE) {
			UNPARSABLE_NUMBER_TYPE = new MessageType(UNPARSABLE_NUMBER_ID, "The value '" + MessageType.FIELD_VALUE_IDENTIFIER + "' in column '" + MessageType.COLUMN_NAME_IDENTIFIER + "' could not be parsed  - it should be numeric", "Non-numeric value in '" + MessageType.COLUMN_NAME_IDENTIFIER + "'");
		}

		// Build the set of data field objects ready to be populated
		itsOutputColumns = itsColumnConfig.buildDataFields(itsColumnSpec); 

		// Populate all the basic data columns
		setDataValues(dataFields);
		
		// Populate the date fields
		populateDateFields(dataFields, dateTimeHandler);
		
		// Populate all columns whose data is drawn from the metadata
		setMetadataValues(metadata, dateTimeHandler);
		
		// Run methods to populate columns from calculations
		setCalculatedValues(metadata, dateTimeHandler);
	}

	/**
	 * Populate the record's date fields from the input date columns.
	 * @param dataFields The list of data fields that constitute the record input
	 * @param dateTimeHandler The utility object for handling dates and times
	 * @throws SocatDataException If an error occurs while parsing the data
	 * @throws SocatDataBaseException If an error occurs while parsing the data
	 */
	private void populateDateFields(List<String> dataFields, DateTimeHandler dateTimeHandler) throws SocatDataException, SocatDataBaseException {
		DateColumnInfo colInfo = itsColumnSpec.getDateColumnInfo();
		
		try {
			DateTime parsedDateTime = colInfo.makeDateTime(dataFields, dateTimeHandler);
			
			itsLogger.trace("Setting record date/time to: " + dateTimeHandler.formatDateTime(parsedDateTime));
			
			setFieldValue(YEAR_COLUMN_NAME, String.valueOf(parsedDateTime.getYear()));
			setFieldValue(MONTH_COLUMN_NAME, String.valueOf(parsedDateTime.getMonthOfYear()));
			setFieldValue(DAY_COLUMN_NAME, String.valueOf(parsedDateTime.getDayOfMonth()));
			setFieldValue(HOUR_COLUMN_NAME, String.valueOf(parsedDateTime.getHourOfDay()));
			setFieldValue(MINUTE_COLUMN_NAME, String.valueOf(parsedDateTime.getMinuteOfHour()));
			
			double second = (double) parsedDateTime.getSecondOfMinute();
			second = second + (((double) parsedDateTime.getMillisOfSecond()) / 1000);
			setFieldValue(SECOND_COLUMN_NAME, String.valueOf(second));
			
			setFieldValue(ISO_DATE_COLUMN_NAME, dateTimeHandler.formatDateTime(parsedDateTime));
		} catch (MissingDateTimeElementException e) {
			try {
				setDateFlag(SocatColumnConfigItem.BAD_FLAG);
				itsMessages.add(new Message(Message.DATE_TIME_COLUMN_INDEX, Message.DATE_TIME_COLUMN_NAME, MISSING_DATE_ELEMENT_TYPE, Message.ERROR, itsLineNumber, CheckerUtils.listToString(dataFields), ""));
			} catch(SocatDataBaseException e2) {
				throw new SocatDataException(itsLineNumber, -1, "Date", e2);
			}
			
		} catch (DateTimeParseException e) {
			itsMessages.add(new Message(Message.DATE_TIME_COLUMN_INDEX, Message.DATE_TIME_COLUMN_NAME, UNPARSABLE_DATE_TYPE, Message.ERROR, itsLineNumber, CheckerUtils.listToString(dataFields), ""));
			setDateFlag(SocatColumnConfigItem.BAD_FLAG);
		} catch (DateTimeException e) {
			setDateFlag(SocatColumnConfigItem.BAD_FLAG);
			throw new SocatDataException(itsLineNumber, -1, "Date", e);
		}
	}
	
	/**
	 * Special method to set the flag for the date/time on this record. This eliminates
	 * the need to place flags on the individual date/time columns.
	 * @param flag The flag to set
	 * @throws SocatDataBaseException If the flag cannot be set
	 */
	public void setDateFlag(int flag) throws SocatDataBaseException {
		if (flag > itsDateFlag) {
			itsDateFlag = flag;
			
			// Date flags always set the cascade flags
			setCascadeFlags(flag);
		}
	}
	
	/**
	 * Retrieve the date/time flag for this record
	 * @return The date/time flag for this record
	 */
	public int getDateFlag() {
		return itsDateFlag;
	}
	
	/**
	 * Returns the date/time of this record as a single object.
	 * @return The date/time of this record.
	 */
	public DateTime getTime() {
		DateTime result = null;
		
		if (getDateFlag() != SocatColumnConfigItem.BAD_FLAG) {
		
			int year = Integer.parseInt(getColumn(YEAR_COLUMN_NAME).getValue());
			int month = Integer.parseInt(getColumn(MONTH_COLUMN_NAME).getValue());
			int day = Integer.parseInt(getColumn(DAY_COLUMN_NAME).getValue());
			int hour = Integer.parseInt(getColumn(HOUR_COLUMN_NAME).getValue());
			int minute = Integer.parseInt(getColumn(MINUTE_COLUMN_NAME).getValue());
			double second = Double.parseDouble(getColumn(SECOND_COLUMN_NAME).getValue());
			
			int wholeSecond = (int) Math.floor(second);
			int millisecond = (int) Math.floor((second - wholeSecond) * 1000);
			
			result = new DateTime(year, month, day, hour, minute, wholeSecond, millisecond, DateTimeZone.UTC);
		}
		
		return result;
	}
	
	/**
	 * Returns the longitude of this record
	 * @return The longitude of this record
	 */
	public double getLongitude() {
		return Double.parseDouble(getColumn(LONGITUDE_COLUMN_NAME).getValue());
	}
	
	/**
	 * Returns the latitude of this record
	 * @return The latitude of this record
	 */
	public double getLatitude() {
		return Double.parseDouble(getColumn(LATITUDE_COLUMN_NAME).getValue());
	}
	
	/**
	 * Populate all fields whose values are taken directly from the input data, converting where necessary
	 * @param lineNumber The current line number of the input file
	 * @param colSpec The column specification
	 * @param dataFields The input data fields
	 * @throws SocatDataException If an error occurs during processing
	 */
	private void setDataValues(List<String> dataFields) throws SocatDataException, SocatDataBaseException {
		for (String column : itsColumnConfig.getColumnList()) {
			if (getDataSource(column) == SocatColumnConfigItem.DATA_SOURCE) {
				StandardColumnInfo colInfo = itsColumnSpec.getColumnInfo(column);

				// colInfo indices are 1-based; lists are 0-based
				String value = "";
				
				if (null != colInfo) {
					int columnIndex = colInfo.getInputColumnIndex() - 1;
					if (dataFields.size() > columnIndex) {
						itsLogger.trace("Retrieving value for column '" + column + "' from input data column " + colInfo.getInputColumnIndex());
						value = dataFields.get(colInfo.getInputColumnIndex() - 1);
					}
					
					if (!CheckerUtils.isEmpty(value)) {
					
						boolean valueOK = true;
						
						if (itsColumnConfig.getColumnConfig(column).isNumeric()) {
							if (!CheckerUtils.isNumeric(value)) {
								valueOK = false;
								SocatDataColumn badColumn = getColumn(column);
								itsLogger.trace("Non-parseable numeric value on line " + getLineNumber() + ", column '" + column + "'");
								
								badColumn.setFlag(SocatColumnConfigItem.BAD_FLAG, itsMessages, getLineNumber(), columnIndex, colInfo.getInputColumnName(), UNPARSABLE_NUMBER_TYPE, value, null);
							}
						}
						
						if (valueOK) {
						
							String convertedValue = value;
							try {
								convertedValue = colInfo.convert(value);
							} catch (ConversionException e) {
								throw new SocatDataException(itsLineNumber, colInfo.getInputColumnIndex(), column, "Could not convert data value", e);
							}
							
							setFieldValue(column, convertedValue);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Populate all fields whose values are extracted from the file's metadata.
	 * @param metadata The set of metadata to use as a data source.
	 */
	private void setMetadataValues(OmeMetadata metadata, DateTimeHandler dateTimeHandler) throws SocatDataException, OmeMetadataException {
		for (String column : itsColumnConfig.getColumnList()) {
			if (getDataSource(column) == SocatColumnConfigItem.METADATA_SOURCE) {
				String metadataName = getMetadataSourceName(column);
				itsLogger.trace("Retrieving value for column '" + column + "' from metadata item '" + metadataName + "'");
				setFieldValue(column, metadata.getValue(metadataName));
			}
		}
	}

	/**
	 * Populate all fields whose values are calculated by the Sanity Checker
	 * @param metadata
	 */
	private void setCalculatedValues(OmeMetadata metadata, DateTimeHandler dateTimeHandler) throws SocatDataException {
		
		int columnIndex = 0;
		for (String column : itsColumnConfig.getColumnList()) {
			columnIndex++;
			
			if (getDataSource(column) == SocatColumnConfigItem.CALCULATION_SOURCE) {
				try {

					DataCalculator calculatorObject = getCalculatorObject(column);
					Method calculatorMethod = getCalculatorMethod(column);
					
					itsLogger.trace("Calculating value for column '" + column + "' using calculator class '" + calculatorObject.getClass().getName() + "'");
					String dataValue = (String) calculatorMethod.invoke(calculatorObject, metadata, this, columnIndex, column, dateTimeHandler);
					if (null != dataValue) {
						setFieldValue(column, dataValue);
					}
				} catch (Exception e) {
					throw new SocatDataException(itsLineNumber, -1, column, "Unhandled exception while invoking data calculator", e);
				}
			}
		}
	}
	
	/**
	 * Sets a field's value
	 * @param field The field whose value is to be set
	 * @param value The value
	 */
	private void setFieldValue(String field, String value) {
		Double missingValue = null;
		StandardColumnInfo colInfo = itsColumnSpec.getColumnInfo(field);
		if (null != colInfo) {
			missingValue = colInfo.getMissingValue();
		}
		
		itsOutputColumns.get(field).setValue(value, missingValue);
	}
	
	/**
	 * Set a flag on all columns that have cascade target flag
	 */
	private void setCascadeFlags(int flag) throws SocatDataBaseException {
		for (SocatDataColumn column : itsOutputColumns.values()) {
			if (column.getFlagType() == SocatColumnConfigItem.CASCADE_TARGET_FLAG) {
				column.setCascadeFlag(flag);
			}
		}
	}
	
	/**
	 * Determines which data source is to be used to populate a given column
	 * @param field The name of the column
	 * @return The data source
	 */
	private int getDataSource(String field) {
		return itsOutputColumns.get(field).getDataSource();
	}
	
	/**
	 * Returns the name of the metadata item to be used to populate the specified column
	 * @return The name of the metadata item to be used to populate the specified column
	 */
	private String getMetadataSourceName(String column) {
		return itsOutputColumns.get(column).getMetadataSourceName();
	}
	
	/**
	 * Returns the object containing the data calculation method for this column
	 * @return The object containing the data calculation method for this column
	 */
	private DataCalculator getCalculatorObject(String column) {
		return itsOutputColumns.get(column).getCalculatorObject();
	}
	
	/**
	 * Returns the method to be invoked to calculate the data value for this column
	 * @return The method to be invoked to calculate the data value for this column
	 */
	private Method getCalculatorMethod(String column) {
		return itsOutputColumns.get(column).getCalculatorMethod();
	}
	
	/**
	 * Indicates whether or not errors were raised during the processing of this
	 * data record
	 * @return {@code true} if errors were raised; {@code false} otherwise.
	 */
	public boolean hasErrors() {
		return itHasErrors;
	}
	
	/**
	 * Indicates whether or not warnings were raised during the processing of this
	 * data record
	 * @return {@code true} if warnings were raised; {@code false} otherwise.
	 */
	public boolean hasWarnings() {
		return itHasWarnings;
	}
	
	/**
	 * Returns the list of all messages created during processing of this record
	 * @return The list of messages
	 */
	public List<Message> getMessages() {
		return itsMessages;
	}
	
	/**
	 * Returns the line number in the original data file that this record came from
	 * @return The line number
	 */
	public int getLineNumber() {
		return itsLineNumber;
	}
	
	/**
	 * Returns the column details for the named column in this record
	 * @param columnName The name of the column
	 * @return The column details
	 */
	public SocatDataColumn getColumn(String columnName) {
		return itsOutputColumns.get(columnName);
	}
	
	/**
	 * Retrieves all the column values of the fields that are configured in a specified group. 
	 * @param groupName The name of the column group
	 * @return The list of column values in the specified column group
	 */
	public List<String> getRequiredGroupValues(String groupName) {
		List<String> result = new ArrayList<String>();
		
		for (String columnName: itsColumnConfig.getColumnList()) {
			SocatColumnConfigItem columnConfig = itsColumnConfig.getColumnConfig(columnName);
			if (null != columnConfig.getRequiredGroup() && columnConfig.getRequiredGroup().equalsIgnoreCase(groupName)) {
				result.add(getColumn(columnName).getValue());
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the list of column names in this record
	 * @return The list of column names in this record
	 */
	public Set<String> getColumnNames() {
		return itsOutputColumns.keySet();
	}
	
	public void addMessage(Message message) {
		itsMessages.add(message);
	}
}
