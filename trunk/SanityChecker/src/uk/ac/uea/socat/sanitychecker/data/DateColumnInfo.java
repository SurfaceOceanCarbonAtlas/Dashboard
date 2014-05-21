package uk.ac.uea.socat.sanitychecker.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;

import uk.ac.uea.socat.sanitychecker.CheckerUtils;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeException;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeHandler;
import uk.ac.uea.socat.sanitychecker.data.datetime.DateTimeParseException;
import uk.ac.uea.socat.sanitychecker.data.datetime.MissingDateTimeElementException;

/**
 * An object to hold details of the date columns in an input file
 */
public class DateColumnInfo {

	/**
	 * Indicates that a single column contains both the date and time
	 */
	public static final int SINGLE_ELEMENT_TYPE = 1;

	/**
	 * Indicates that two columns are used for the date and time
	 */
	public static final int DATE_TIME_ELEMENT_TYPE = 2;
	
	/**
	 * Indicates that separate elements are used for each element of the date and time
	 */
	public static final int INDIVIDUAL_ELEMENTS_TYPE = 3;
	
	/**
	 * Indicates that the date is specified as year, day, second
	 */
	public static final int YEAR_DAY_SECOND_TYPE = 4;
	
	/**
	 * Indicates which type of date column setup is being used
	 */
	private int itsElementType;

	/**
	 * Column info for a combined date/time element
	 */
	private ColInfo itsSingleElementInfo = null;
	
	/**
	 * Column info for a separate date element
	 */
	private ColInfo itsDateInfo = null;
	
	/**
	 * Column info for a separate time element
	 */
	private ColInfo itsTimeInfo = null;
	
	/**
	 * Column info for a separate year element
	 */
	private ColInfo itsYearInfo = null;
	
	/**
	 * Column info for a separate month element
	 */
	private ColInfo itsMonthInfo = null;
	
	/**
	 * Column info for a separate day element
	 */
	private ColInfo itsDayInfo = null;
	
	/**
	 * Column info for a separate hour element
	 */
	private ColInfo itsHourInfo = null;
	
	/**
	 * Column info for a separate minute element
	 */
	private ColInfo itsMinuteInfo = null;
	
	/**
	 * Column info for a separate second element
	 */
	private ColInfo itsSecondInfo = null;
	
	/**
	 * Indicates which day index to use as 1st January
	 */
	private int itsJanFirstIndex = 0;
	
	/**
	 * Takes an XML column spec date element and parses it.
	 * @param dateElement The date element.
	 * @param logger The program logger
	 */
	public DateColumnInfo(Element dateElement, Logger logger) throws Exception {
		processDateElement(dateElement, logger);
	}
	
	/**
	 * Process the date XML element into a format that can be used by the logic
	 * for the data conversion. Note that the XML has already been validated so we
	 * don't need to do it here.
	 * 
	 * @param dateElement The date XML element
	 */
	private void processDateElement(Element dateElement, Logger logger) throws Exception{
		
		/*
		 * The date element contains one of the following:
		 * 
		 * 1. A single date_time element
		 * 2. Separate date and time elements
		 * 3. Separate y/m/d/h/m/s elements
		 * 4. Year, Day of year, Second of day
		 * 
		 * We can tell which we need to do by how many child elements there are
		 */
		int childCount = dateElement.getChildren().size();
		
		switch (childCount) {
		case 1:
		{
			processSingleElement(dateElement, logger);
			break;
		}
		case 2:
		{
			processDateTimeElements(dateElement, logger);
			break;
		}
		case 4:
		{
			processYearDaySecondElements(dateElement, logger);
			break;
		}
		case 5:
		case 6:
		{
			processIndividualColumnElements(dateElement, logger);
			break;
		}
		default:
		{
			// Something has gone horrifically wrong. ABORT!!!
			throw new Exception("FATAL ERROR: Unexpected number of elements in date column specification");
		}
		}
	}
	
	public DateTime makeDateTime(List<String> dataFields, DateTimeHandler dateTimeHandler) throws DateTimeException {

		DateTime result = null;

		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE: {
			
			String value = dataFields.get(itsSingleElementInfo.index - 1);
			
			if (CheckerUtils.isEmpty(value)) {
				throw new MissingDateTimeElementException();
			} else {
				result = dateTimeHandler.parseDateTime(value);
			}
			
			break;
		}
		
		case DATE_TIME_ELEMENT_TYPE: {
			
			String dateValue = dataFields.get(itsDateInfo.index - 1);
			String timeValue = dataFields.get(itsTimeInfo.index - 1);
			
			if (CheckerUtils.isEmpty(dateValue) || CheckerUtils.isEmpty(timeValue)) {
				throw new MissingDateTimeElementException();
			} else {
				String dateTimeString = dateValue + " " + timeValue;
				result = dateTimeHandler.parseDateTime(dateTimeString);
			}
			
			break;
		}
		
		case INDIVIDUAL_ELEMENTS_TYPE: {
			String yearString = dataFields.get(getYearColumnIndex() - 1);
			String monthString = dataFields.get(getMonthColumnIndex() - 1);
			String dayString = dataFields.get(getDayColumnIndex() - 1);
			String hourString = dataFields.get(getHourColumnIndex() - 1);
			String minuteString = dataFields.get(getMinuteColumnIndex() - 1);
			String secondString;
			int idx = getSecondColumnIndex();
			if ( idx > 0 )
				secondString = dataFields.get(idx - 1);
			else
				secondString = "0";
			
			if (CheckerUtils.isEmpty(yearString, monthString, dayString, hourString, minuteString, secondString)) {
				throw new MissingDateTimeElementException();
			} else {
				
				try {
					int year = Integer.parseInt(yearString);
					int month = Integer.parseInt(monthString);
					int day = Integer.parseInt(dayString);
					int hour = Integer.parseInt(hourString);
					int minute = Integer.parseInt(minuteString);
					
					double second = Double.parseDouble(secondString);
					int wholeSecond = (int) Math.floor(second);
					int millisecond = (int) Math.floor((second - wholeSecond) * 1000);
					
				
					result = new DateTime(year, month, day, hour, minute, wholeSecond, millisecond, DateTimeZone.UTC);
				} catch (NumberFormatException e) {
					throw new DateTimeParseException("Invalid date " + yearString + "/" + monthString + "/" + dayString + " " + hourString + ":" + minuteString + ":" + secondString);
				} catch (IllegalFieldValueException e) {
					throw new DateTimeParseException("Invalid date " + yearString + "/" + monthString + "/" + dayString + " " + hourString + ":" + minuteString + ":" + secondString);
				} catch (Exception e) {
					throw new DateTimeParseException(e);
				}
			}
			break;
		}
		case YEAR_DAY_SECOND_TYPE: {
			
			String yearString = dataFields.get(getYearColumnIndex() - 1);
			String dayString = dataFields.get(getDayColumnIndex() - 1);
			String secondString = dataFields.get(getSecondColumnIndex() - 1);
			
			String invalidDateMessage = "Invalid date " + yearString + " " + dayString + " " + secondString;
			
			if (CheckerUtils.isEmpty(yearString, dayString, secondString)) {
				throw new MissingDateTimeElementException();
			} else {
				try {
					int year = Integer.parseInt(yearString);
					int day = Integer.parseInt(dayString);
					int second = Integer.parseInt(secondString);

					// Set the 1st Jan to day zero
					day = day - itsJanFirstIndex;
					
					// Build the date, checking values as we go
					DateTime calculationDate = new DateTime(year, 1, 1, 0, 0, 0);
					
					if (day < 0) {
						throw new DateTimeParseException(invalidDateMessage);
					} else if (calculationDate.year().isLeap() && day > 366) {
						throw new DateTimeParseException(invalidDateMessage);
					} else if (day > 365) {
						throw new DateTimeParseException(invalidDateMessage);
					}
					
					calculationDate = calculationDate.plusDays(day);
					
					if (second < 0) {
						throw new DateTimeParseException(invalidDateMessage);
					} else if (second > 86400) {
						throw new DateTimeParseException(invalidDateMessage);
					}
					
					calculationDate = calculationDate.plusSeconds(second);
					
					result = calculationDate;
					
					
				} catch (NumberFormatException e) {
					throw new DateTimeParseException(invalidDateMessage);
				} catch (IllegalFieldValueException e) {
					throw new DateTimeParseException(invalidDateMessage);
				} catch (Exception e) {
					throw new DateTimeParseException(e);
				}
			}
			
			break;
		}
		}
		
		if (null == result) {
			throw new DateTimeException("Failed to parse date for unknown reason");
		}
		
		return result;
	}
	
	/**
	 * Process a combined date/time column specification
	 * @param parent The parent element
	 */
	private void processSingleElement(Element parent, Logger logger) {
		itsElementType = SINGLE_ELEMENT_TYPE;
		
		Element child = parent.getChild(ColumnSpec.SINGLE_DATE_TIME_ELEMENT, parent.getNamespace());
		int columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		String columnName = child.getTextTrim();
		logger.trace("Date column spec: Single column '" + columnName + "' (" + columnIndex + ")");
		itsSingleElementInfo = new ColInfo(columnIndex, columnName);
	}
	
	/**
	 * Process separate date and time column specifications
	 * @param parent The parent element
	 */
	private void processDateTimeElements(Element parent, Logger logger) {
		itsElementType = DATE_TIME_ELEMENT_TYPE;

		int columnIndex;
		String columnName;
		Element child;
		
		child = parent.getChild(ColumnSpec.DATE_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Date column '" + columnName + "' (" + columnIndex + ")");
		itsDateInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.TIME_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Time column '" + columnName + "' (" + columnIndex + ")");
		itsTimeInfo = new ColInfo(columnIndex, columnName);
	}
	
	private void processYearDaySecondElements(Element parent, Logger logger) {
		itsElementType = YEAR_DAY_SECOND_TYPE;
		
		int columnIndex;
		String columnName;
		Element child;
		
		child = parent.getChild(ColumnSpec.YDS_YEAR_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Year column '" + columnName + "' (" + columnIndex + ")");
		itsYearInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.YDS_DAY_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Day of Year column '" + columnName + "' (" + columnIndex + ")");
		itsDayInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.YDS_SECOND_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Second of Day column '" + columnName + "' (" + columnIndex + ")");
		itsSecondInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.JAN_FIRST_INDEX_ELEMENT, parent.getNamespace());
		itsJanFirstIndex = Integer.parseInt(child.getTextTrim());
	}
	
	/**
	 * Process individual date and time component column specifications
	 * @param parent The parent element
	 */
	private void processIndividualColumnElements(Element parent, Logger logger) {
		itsElementType = INDIVIDUAL_ELEMENTS_TYPE;

		int columnIndex;
		String columnName;
		Element child;
		
		child = parent.getChild(ColumnSpec.YEAR_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Year column '" + columnName + "' (" + columnIndex + ")");
		itsYearInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.MONTH_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Month column '" + columnName + "' (" + columnIndex + ")");
		itsMonthInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.DAY_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Day column '" + columnName + "' (" + columnIndex + ")");
		itsDayInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.HOUR_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Hour column '" + columnName + "' (" + columnIndex + ")");
		itsHourInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.MINUTE_ELEMENT, parent.getNamespace());
		columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
		columnName = child.getTextTrim();
		logger.trace("Date column spec: Minute column '" + columnName + "' (" + columnIndex + ")");
		itsMinuteInfo = new ColInfo(columnIndex, columnName);
		
		child = parent.getChild(ColumnSpec.SECOND_ELEMENT, parent.getNamespace());
		if ( child != null ) {
			columnIndex = Integer.parseInt(child.getAttributeValue(ColumnSpec.INPUT_COLUMN_INDEX_ATTRIBUTE));
			columnName = child.getTextTrim();
			logger.trace("Date column spec: Second column '" + columnName + "' (" + columnIndex + ")");
			itsSecondInfo = new ColInfo(columnIndex, columnName);
		}
		else {
			logger.trace("Date column spec: Second column not specified");
			itsSecondInfo = null;
		}
	}
	
	/**
	 * Returns the index of the column containing the year	
	 * @return The index of the column containing the year
	 */
	public int getYearColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsDateInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsYearInfo.index;
			break;
		}
		case YEAR_DAY_SECOND_TYPE: {
			result = itsYearInfo.index;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the index of the column containing the month	
	 * @return The index of the column containing the month
	 */
	public int getMonthColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsDateInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsMonthInfo.index;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the index of the column containing the day	
	 * @return The index of the column containing the day
	 */
	public int getDayColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsDateInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsDayInfo.index;
			break;
		}
		case YEAR_DAY_SECOND_TYPE: {
			result = itsDayInfo.index;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the index of the column containing the hour	
	 * @return The index of the column containing the hour
	 */
	public int getHourColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsTimeInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsHourInfo.index;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the index of the column containing the minute	
	 * @return The index of the column containing the minute
	 */
	public int getMinuteColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsTimeInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsMinuteInfo.index;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the index of the column containing the second	
	 * @return The index of the column containing the second
	 */
	public int getSecondColumnIndex() {
		int result = 0;
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.index;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsTimeInfo.index;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			if ( itsSecondInfo != null )
				result = itsSecondInfo.index;
			else
				result = -1;
			break;
		}
		case YEAR_DAY_SECOND_TYPE: {
			result = itsSecondInfo.index;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the name of the column containing the year	
	 * @return The name of the column containing the year
	 */
	public String getYearColumnName() {
		String result = "";
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.name;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsDateInfo.name;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsYearInfo.name;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the name of the column containing the month	
	 * @return The name of the column containing the month
	 */
	public String getMonthColumnName() {
		String result = "";
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.name;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsDateInfo.name;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsMonthInfo.name;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the name of the column containing the day	
	 * @return The name of the column containing the day
	 */
	public String getDayColumnName() {
		String result = "";
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.name;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsDateInfo.name;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsDayInfo.name;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the name of the column containing the hour	
	 * @return The name of the column containing the hour
	 */
	public String getHourColumnName() {
		String result = "";
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.name;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsTimeInfo.name;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsHourInfo.name;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the name of the column containing the minute	
	 * @return The name of the column containing the minute
	 */
	public String getMinuteColumnName() {
		String result = "";
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.name;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsTimeInfo.name;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			result = itsMinuteInfo.name;
			break;
		}
		}
		
		return result;
	}
	
	/**
	 * Returns the name of the column containing the second	
	 * @return The name of the column containing the second
	 */
	public String getSecondColumnName() {
		String result = "";
		
		switch (itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			result = itsSingleElementInfo.name;
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			result = itsTimeInfo.name;
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			if ( itsSecondInfo != null )
				result = itsSecondInfo.name;
			else
				result = null;
			break;
		}
		}
		
		return result;
	}

	/**
	 * Returns a list of all the date/time columns required in the data file.
	 * The list will vary according to the specified format of the date/time columns.
	 * @return The list of all required date/time columns.
	 */
	public List<String> getDateTimeInputColumns() {
		List<String> columnNames = new ArrayList<String>();

		switch(itsElementType) {
		case SINGLE_ELEMENT_TYPE:
		{
			columnNames.add(itsSingleElementInfo.name);
			break;
		}
		case DATE_TIME_ELEMENT_TYPE: {
			columnNames.add(itsDateInfo.name);
			columnNames.add(itsTimeInfo.name);
			break;
		}
		case INDIVIDUAL_ELEMENTS_TYPE: {
			columnNames.add(itsYearInfo.name);
			columnNames.add(itsMonthInfo.name);
			columnNames.add(itsDayInfo.name);
			columnNames.add(itsHourInfo.name);
			columnNames.add(itsMinuteInfo.name);
			columnNames.add(itsSecondInfo.name);
		}
		}
		
		return columnNames;
	}
	
	/**
	 * Inner class to store the index and name of an input column.
	 * This is only used here, so we're not going to bother with
	 * get methods. Just grab the values direct.
	 */
	class ColInfo {
	
		/**
		 * The input column index
		 */
		private int index;
		
		/**
		 * The input column name
		 */
		private String name;
		
		/**
		 * Constructor
		 * @param columnIndex Column index
		 * @param columnName Column name
		 */
		private ColInfo(int columnIndex, String columnName) {
			index = columnIndex;
			name = columnName;
		}
	}
}

