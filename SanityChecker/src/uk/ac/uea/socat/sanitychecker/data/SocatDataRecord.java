package uk.ac.uea.socat.sanitychecker.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import uk.ac.exeter.QCRoutines.data.DataRecord;
import uk.ac.exeter.QCRoutines.data.DataRecordException;
import uk.ac.exeter.QCRoutines.data.InvalidDataException;
import uk.ac.exeter.QCRoutines.data.NoSuchColumnException;
import uk.ac.exeter.QCRoutines.messages.Flag;
import uk.ac.exeter.QCRoutines.messages.ParsingMessages.MissingDateTimeElementMessage;
import uk.ac.exeter.QCRoutines.messages.ParsingMessages.UnparseableDateMessage;
import uk.ac.uea.socat.omemetadata.OmeMetadata;
import uk.ac.uea.socat.omemetadata.OmeMetadataException;
import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfig;
import uk.ac.uea.socat.sanitychecker.config.SocatColumnConfigItem;
import uk.ac.uea.socat.sanitychecker.config.SocatDataBaseException;
import uk.ac.uea.socat.sanitychecker.data.calculate.DataCalculator;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeParseException;
import uk.ac.uea.socat.sanitychecker.data.datetime.MissingDateTimeElementException;

/**
 * A class representing a single measurement record.
 * 
 * This class is responsible for taking in a line from a user-supplied data file,
 * and performing the necessary conversions to allow a SOCAT-formatted output file
 * to be produced.
 */

public class SocatDataRecord extends DataRecord {
	
	/**
	 * The specification of the output columns
	 */
	private ColumnSpec columnSpec;
	
	public SocatDataRecord(int lineNumber, SocatColumnConfig columnConfig, List<String> dataFields, ColumnSpec columnSpec, OmeMetadata metadata, DateTimeHandler dateTimeHandler) throws DataRecordException, OmeMetadataException {
		super(lineNumber, columnConfig, dataFields);
		this.columnSpec = columnSpec;
		
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
	 * @throws SocatDataBaseException If an error occurs while parsing the data
	 * @throws DataRecordException 
	 */
	private void populateDateFields(List<String> dataFields, DateTimeHandler dateTimeHandler) throws DataRecordException {
		DateColumnInfo colInfo = columnSpec.getDateColumnInfo();
		
		try {
			DateTime parsedDateTime = colInfo.makeDateTime(dataFields, dateTimeHandler);
			
			getColumn(SocatColumnConfig.YEAR_COLUMN_NAME).setValue(String.valueOf(parsedDateTime.getYear()));
			getColumn(SocatColumnConfig.MONTH_COLUMN_NAME).setValue(String.valueOf(parsedDateTime.getMonthOfYear()));
			getColumn(SocatColumnConfig.DAY_COLUMN_NAME).setValue(String.valueOf(parsedDateTime.getDayOfMonth()));
			getColumn(SocatColumnConfig.HOUR_COLUMN_NAME).setValue(String.valueOf(parsedDateTime.getHourOfDay()));
			getColumn(SocatColumnConfig.MINUTE_COLUMN_NAME).setValue(String.valueOf(parsedDateTime.getMinuteOfHour()));
			
			double second = (double) parsedDateTime.getSecondOfMinute();
			second = second + (((double) parsedDateTime.getMillisOfSecond()) / 1000);
			getColumn(SocatColumnConfig.SECOND_COLUMN_NAME).setValue(String.valueOf(second));
			
			getColumn(SocatColumnConfig.ISO_DATE_COLUMN_NAME).setValue(dateTimeHandler.formatDateTime(parsedDateTime));
		} catch (MissingDateTimeElementException e) {
			try {
				setDateFlag(Flag.BAD);
				addMessage(new MissingDateTimeElementMessage(this));
			} catch(DataRecordException e2) {
				throw new DataRecordException(lineNumber, "Error while processing date/time", e2);
			}
			
		} catch (DateTimeParseException e) {
			addMessage(new UnparseableDateMessage(this, CheckerUtils.listToString(dataFields)));
			setDateFlag(Flag.BAD);
		} catch (DateTimeException e) {
			setDateFlag(Flag.BAD);
			throw new DataRecordException(lineNumber, "Error while processing date/time", e);
		}
	}
	
	/**
	 * Special method to set the flag for the date/time on this record. This eliminates
	 * the need to place flags on the individual date/time columns.
	 * @param flag The flag to set
	 * @throws SocatDataBaseException If the flag cannot be set
	 * @throws NoSuchColumnException 
	 */
	public void setDateFlag(Flag flag) throws NoSuchColumnException {
		getColumn(SocatColumnConfig.YEAR_COLUMN_NAME).setFlag(flag);
		getColumn(SocatColumnConfig.MONTH_COLUMN_NAME).setFlag(flag);
		getColumn(SocatColumnConfig.DAY_COLUMN_NAME).setFlag(flag);
		getColumn(SocatColumnConfig.HOUR_COLUMN_NAME).setFlag(flag);
		getColumn(SocatColumnConfig.MINUTE_COLUMN_NAME).setFlag(flag);
		getColumn(SocatColumnConfig.SECOND_COLUMN_NAME).setFlag(flag);
		getColumn(SocatColumnConfig.ISO_DATE_COLUMN_NAME).setFlag(flag);
	}
	
	/**
	 * Returns the date/time of this record as a single object.
	 * @return The date/time of this record.
	 * @throws NoSuchColumnException 
	 * @throws NumberFormatException 
	 */
	public DateTime getTime() throws DataRecordException {
		DateTime result = null;
		
		try {
			if (!getColumn(SocatColumnConfig.YEAR_COLUMN_NAME).getFlag().equals(Flag.BAD)) {
			
				int year = Integer.parseInt(getColumn(SocatColumnConfig.YEAR_COLUMN_NAME).getValue());
				int month = Integer.parseInt(getColumn(SocatColumnConfig.MONTH_COLUMN_NAME).getValue());
				int day = Integer.parseInt(getColumn(SocatColumnConfig.DAY_COLUMN_NAME).getValue());
				int hour = Integer.parseInt(getColumn(SocatColumnConfig.HOUR_COLUMN_NAME).getValue());
				int minute = Integer.parseInt(getColumn(SocatColumnConfig.MINUTE_COLUMN_NAME).getValue());
				double second = Double.parseDouble(getColumn(SocatColumnConfig.SECOND_COLUMN_NAME).getValue());
				
				int wholeSecond = (int) Math.floor(second);
				int millisecond = (int) Math.floor((second - wholeSecond) * 1000);
				
				result = new DateTime(year, month, day, hour, minute, wholeSecond, millisecond, DateTimeZone.UTC);
			}
		} catch (NumberFormatException e) {
			throw new DataRecordException(lineNumber, "One or more date values is not numeric", e);
		}
		
		return result;
	}
	
	/**
	 * Returns the longitude of this record
	 * @return The longitude of this record
	 * @throws NoSuchColumnException 
	 * @throws NumberFormatException 
	 */
	public double getLongitude() throws DataRecordException {
		try {
			return Double.parseDouble(getColumn(SocatColumnConfig.LONGITUDE_COLUMN_NAME).getValue());
		} catch (NumberFormatException e) {
			throw new DataRecordException(lineNumber, "Longitude value is not numeric", e);
		}
	}
	
	/**
	 * Returns the latitude of this record
	 * @return The latitude of this record
	 * @throws NoSuchColumnException 
	 * @throws NumberFormatException 
	 */
	public double getLatitude() throws DataRecordException {
		try {
			return Double.parseDouble(getColumn(SocatColumnConfig.LATITUDE_COLUMN_NAME).getValue());
		} catch (NumberFormatException e) {
			throw new DataRecordException(lineNumber, "Latitude value is not numeric", e);
		}
	}
	
	/**
	 * Populate all fields whose values are extracted from the file's metadata.
	 * @param metadata The set of metadata to use as a data source.
	 * @throws InvalidDataException 
	 */
	private void setMetadataValues(OmeMetadata metadata, DateTimeHandler dateTimeHandler) throws SocatDataException, OmeMetadataException, InvalidDataException {
		for (int i = 0; i < data.size(); i++) {
			SocatDataColumn column = (SocatDataColumn) data.get(0);
			if (column.getDataSource() == SocatColumnConfigItem.METADATA_SOURCE) {
				column.setValue(metadata.getValue(column.getMetadataSourceName()));
			}
		}
	}

	/**
	 * Populate all fields whose values are calculated by the Sanity Checker
	 * @param metadata
	 * @throws DataRecordException 
	 */
	private void setCalculatedValues(OmeMetadata metadata, DateTimeHandler dateTimeHandler) throws DataRecordException {
		
		for (int columnIndex = 0; columnIndex < data.size(); columnIndex++) {
			columnIndex++;
			SocatDataColumn column = (SocatDataColumn) data.get(columnIndex);
			
			if (column.getDataSource() == SocatColumnConfigItem.CALCULATION_SOURCE) {
				try {

					DataCalculator calculatorObject = column.getCalculatorObject();
					Method calculatorMethod = column.getCalculatorMethod();
					
					String dataValue = (String) calculatorMethod.invoke(calculatorObject, metadata, this, columnIndex, column, dateTimeHandler);
					if (null != dataValue) {
						column.setValue(dataValue);
					}
				} catch (Exception e) {
					throw new DataRecordException(lineNumber, column, "Unhandled exception while invoking data calculator", e);
				}
			}
		}
	}
	
	/**
	 * Retrieves all the column values of the fields that are configured in a specified group. 
	 * @param groupName The name of the column group
	 * @return The list of column values in the specified column group
	 */
	public List<String> getRequiredGroupValues(String groupName) {
		List<String> result = new ArrayList<String>();
		
		for (int i = 0; i < data.size(); i++) {
			SocatDataColumn column = (SocatDataColumn) data.get(i);
			if (null != column.getRequiredGroup() && column.getRequiredGroup().equalsIgnoreCase(groupName)) {
				if (column.isEmpty()) {
					result.add(column.getValue());
				}
			}
		}
		
		return result;
	}
	
	@Override
	public TreeSet<Integer> getDateTimeColumns() {
		
		TreeSet<Integer> result = new TreeSet<Integer>();
		
		try {
			result.add(getColumnIndex(SocatColumnConfig.YEAR_COLUMN_NAME));
			result.add(getColumnIndex(SocatColumnConfig.MONTH_COLUMN_NAME));
			result.add(getColumnIndex(SocatColumnConfig.DAY_COLUMN_NAME));
			result.add(getColumnIndex(SocatColumnConfig.HOUR_COLUMN_NAME));
			result.add(getColumnIndex(SocatColumnConfig.MINUTE_COLUMN_NAME));

		} catch (NoSuchColumnException e) {
			// We shall ignore this error.
		}

		return result;
	}
	
	@Override
	public int getLongitudeColumn() {
		
		int result = -1;
		
		try {
			result = getColumnIndex(SocatColumnConfig.LONGITUDE_COLUMN_NAME);
		} catch (NoSuchColumnException e) {
			// We shall ignore this error.
		}
		
		return result;
	}

	@Override
	public int getLatitudeColumn() {
		
		int result = -1;
		
		try {
			result = getColumnIndex(SocatColumnConfig.LATITUDE_COLUMN_NAME);
		} catch (NoSuchColumnException e) {
			// We shall ignore this error.
		}
		
		return result;
	}
}
