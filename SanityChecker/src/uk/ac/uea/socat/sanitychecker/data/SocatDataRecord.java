package uk.ac.uea.socat.sanitychecker.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.DataMessage;
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
import uk.ac.uea.socat.sanitychecker.metadata.MetadataItem;

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
	private List<DataMessage> itsMessages;
	
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
	
	/**
	 * The program logger
	 */
	private Logger itsLogger;

	public SocatDataRecord(List<String> dataFields, int lineNumber, ColumnSpec colSpec, Map<String, MetadataItem> metadata, DateTimeHandler dateTimeHandler, Logger logger, Output output) throws ConfigException, SocatDataException {
		itsMessages = new ArrayList<DataMessage>();
		itsColumnSpec = colSpec;
		itsLineNumber = lineNumber;
		itsLogger = logger;

		itsColumnConfig = SocatColumnConfig.getInstance();

		// Build the set of data field objects ready to be populated
		itsOutputColumns = itsColumnConfig.buildDataFields(); 

		// Populate all the basic data columns
		setDataValues(dataFields);
		
		// Populate the date fields
		populateDateFields(dataFields, dateTimeHandler);
		
		// Populate all columns whose data is drawn from the metadata
		setMetadataValues(metadata, dateTimeHandler);
		
		// Run methods to populate columns from calculations
		setCalculatedValues(metadata, dateTimeHandler);
		
		// Add the list of messages to the output
		output.addDataMessages(itsMessages);
	}

	private void populateDateFields(List<String> dataFields, DateTimeHandler dateTimeHandler) throws SocatDataException {
		DateColumnInfo colInfo = itsColumnSpec.getDateColumnInfo();
		
		try {
			DateTime parsedDateTime = colInfo.makeDateTime(dataFields, dateTimeHandler);
			
			itsLogger.trace("Setting record date/time to: " + dateTimeHandler.formatDateTime(parsedDateTime));
			
			setFieldValue(YEAR_COLUMN_NAME, String.valueOf(parsedDateTime.getYear()));
			setFieldValue(MONTH_COLUMN_NAME, String.valueOf(parsedDateTime.getMonthOfYear()));
			setFieldValue(DAY_COLUMN_NAME, String.valueOf(parsedDateTime.getDayOfMonth()));
			setFieldValue(HOUR_COLUMN_NAME, String.valueOf(parsedDateTime.getHourOfDay()));
			setFieldValue(MINUTE_COLUMN_NAME, String.valueOf(parsedDateTime.getMinuteOfHour()));
			setFieldValue(SECOND_COLUMN_NAME, String.valueOf(parsedDateTime.getSecondOfMinute()));
			
			setFieldValue(ISO_DATE_COLUMN_NAME, dateTimeHandler.formatDateTime(parsedDateTime));
		} catch (MissingDateTimeElementException e) {
			itsMessages.add(new DataMessage(DataMessage.ERROR, itsLineNumber, -1, "", e.getMessage()));
			
			try {
				setDateFlags(SocatColumnConfigItem.BAD_FLAG, itsMessages, itsLineNumber, "Missing date element");
			} catch(SocatDataBaseException e2) {
				throw new SocatDataException(itsLineNumber, -1, "Date", e2);
			}
			
		} catch (DateTimeParseException e) {
			itsMessages.add(new DataMessage(DataMessage.ERROR, itsLineNumber, -1, "", e.getMessage()));
		} catch (DateTimeException e) {
			throw new SocatDataException(itsLineNumber, -1, "Date", e);
		}
	}
	
	
	/**
	 * Populate all fields whose values are taken directly from the input data, converting where necessary
	 * @param lineNumber The current line number of the input file
	 * @param colSpec The column specification
	 * @param dataFields The input data fields
	 * @throws SocatDataException If an error occurs during processing
	 */
	private void setDataValues(List<String> dataFields) throws SocatDataException {
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
	
	/**
	 * Populate all fields whose values are extracted from the file's metadata.
	 * @param metadata The set of metadata to use as a data source.
	 */
	private void setMetadataValues(Map<String, MetadataItem> metadata, DateTimeHandler dateTimeHandler) throws SocatDataException {
		for (String column : itsColumnConfig.getColumnList()) {
			if (getDataSource(column) == SocatColumnConfigItem.METADATA_SOURCE) {
				String metadataName = getMetadataSourceName(column);
				itsLogger.trace("Retrieving value for column '" + column + "' from metadata item '" + metadataName + "'");

				MetadataItem metadataItem = metadata.get(metadataName);
				if (null != metadataItem) {
					try {
						setFieldValue(column, metadataItem.getValue(dateTimeHandler));
					} catch (DateTimeException e) {
						throw new SocatDataException(itsLineNumber, itsColumnSpec.getColumnInfo(column).getInputColumnIndex(), column, "Could not retrieve metadata value", e);
					}
				}
			}
		}
	}

	/**
	 * Populate all fields whose values are calculated by the Sanity Checker
	 * @param metadata
	 */
	private void setCalculatedValues(Map<String, MetadataItem> metadata, DateTimeHandler dateTimeHandler) throws SocatDataException {
		
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
	 * Sets the flag on a specified field. If a higher (i.e. worse) flag has already been
	 * set on the field, it will not be updated.
	 * @param column The name of the field
	 * @param flag The flag value
	 * @throws SocatDataBaseException If the field doesn't have a flag
	 */
	private void setFlag(String column, int flag, List<DataMessage> messages, int record, String message) throws SocatDataBaseException {
		
		SocatDataColumn flagColumn = itsOutputColumns.get(column);
		flagColumn.setFlag(flag, messages, record, message);

		// If this is a cascading flag field, set all the cascading targets' flags
		int columnFlagType = flagColumn.getFlagType();
		if (columnFlagType == SocatColumnConfigItem.CASCADING_FLAG || columnFlagType == SocatColumnConfigItem.CASCADE_TARGET_FLAG) {
			setCascadeFlags(flag);
		}
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
	 * Set a flag for all date fields
	 * @param flag The flag value
	 * @throws SocatDataBaseException If the flag cannot be set
	 */
	private void setDateFlags(int flag, List<DataMessage> messages, int record, String message) throws SocatDataBaseException {
		setFlag(YEAR_COLUMN_NAME, flag, messages, record, message);
		setFlag(MONTH_COLUMN_NAME, flag, messages, record, message);
		setFlag(DAY_COLUMN_NAME, flag, messages, record, message);
		setFlag(HOUR_COLUMN_NAME, flag, messages, record, message);
		setFlag(MINUTE_COLUMN_NAME, flag, messages, record, message);
		setFlag(SECOND_COLUMN_NAME, flag, messages, record, message);
		
		// Date flags are always cascade flags.
		setCascadeFlags(flag);
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
	public List<DataMessage> getMessages() {
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
}
